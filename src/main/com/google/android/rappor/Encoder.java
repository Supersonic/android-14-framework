package com.google.android.rappor;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.BitSet;
import java.util.Random;
/* loaded from: classes.dex */
public class Encoder {
    private static final byte HMAC_DRBG_TYPE_COHORT = 0;
    private static final byte HMAC_DRBG_TYPE_PRR = 1;
    public static final int MAX_BITS = 4096;
    public static final int MAX_BLOOM_HASHES = 8;
    public static final int MAX_COHORTS = 128;
    public static final int MIN_USER_SECRET_BYTES = 48;
    public static final long VERSION = 3;
    private final int cohort;
    private final byte[] encoderIdBytes;
    private final BitSet inputMask;
    private final MessageDigest md5;
    private final int numBits;
    private final int numBloomHashes;
    private final int numCohorts;
    private final double probabilityF;
    private final double probabilityP;
    private final double probabilityQ;
    private final Random random;
    private final MessageDigest sha256;
    private final byte[] userSecret;

    public Encoder(byte[] userSecret, String encoderId, int numBits, double probabilityF, double probabilityP, double probabilityQ, int numCohorts, int numBloomHashes) {
        this(null, null, null, userSecret, encoderId, numBits, probabilityF, probabilityP, probabilityQ, numCohorts, numBloomHashes);
    }

    public Encoder(Random random, MessageDigest md5, MessageDigest sha256, byte[] userSecret, String encoderId, int numBits, double probabilityF, double probabilityP, double probabilityQ, int numCohorts, int numBloomHashes) {
        if (md5 == null) {
            try {
                this.md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException impossible) {
                throw new AssertionError(impossible);
            }
        } else {
            this.md5 = md5;
        }
        this.md5.reset();
        if (sha256 == null) {
            try {
                this.sha256 = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException impossible2) {
                throw new AssertionError(impossible2);
            }
        } else {
            this.sha256 = sha256;
        }
        this.sha256.reset();
        this.encoderIdBytes = encoderId.getBytes(StandardCharsets.UTF_8);
        if (random == null) {
            this.random = new SecureRandom();
        } else {
            this.random = random;
        }
        checkArgument(userSecret.length >= 48, "userSecret must be at least 48 bytes of high-quality entropy.");
        this.userSecret = userSecret;
        checkArgument(probabilityF >= 0.0d && probabilityF <= 1.0d, "probabilityF must be on range [0.0, 1.0]");
        this.probabilityF = Math.round(probabilityF * 128.0d) / 128.0d;
        checkArgument(probabilityP >= 0.0d && probabilityP <= 1.0d, "probabilityP must be on range [0.0, 1.0]");
        this.probabilityP = probabilityP;
        checkArgument(probabilityQ >= 0.0d && probabilityQ <= 1.0d, "probabilityQ must be on range [0.0, 1.0]");
        this.probabilityQ = probabilityQ;
        checkArgument(numBits >= 1 && numBits <= 4096, "numBits must be on range [1, 4096].");
        this.numBits = numBits;
        BitSet bitSet = new BitSet(numBits);
        this.inputMask = bitSet;
        bitSet.set(0, numBits, true);
        checkArgument(numBloomHashes >= 1 && numBloomHashes <= numBits, "numBloomHashes must be on range [1, numBits).");
        this.numBloomHashes = numBloomHashes;
        checkArgument(numCohorts >= 1 && numCohorts <= 128, "numCohorts must be on range [1, 128].");
        boolean numCohortsIsPowerOfTwo = ((numCohorts + (-1)) & numCohorts) == 0;
        checkArgument(numCohortsIsPowerOfTwo, "numCohorts must be a power of 2.");
        this.numCohorts = numCohorts;
        HmacDrbg cohortDrbg = new HmacDrbg(userSecret, new byte[]{HMAC_DRBG_TYPE_COHORT});
        ByteBuffer cohortDrbgBytes = ByteBuffer.wrap(cohortDrbg.nextBytes(4));
        int cohortMasterAssignment = Math.abs(cohortDrbgBytes.getInt()) % 128;
        this.cohort = (numCohorts - 1) & cohortMasterAssignment;
    }

    public double getProbabilityF() {
        return this.probabilityF;
    }

    public double getProbabilityP() {
        return this.probabilityP;
    }

    public double getProbabilityQ() {
        return this.probabilityQ;
    }

    public int getNumBits() {
        return this.numBits;
    }

    public int getNumBloomHashes() {
        return this.numBloomHashes;
    }

    public int getNumCohorts() {
        return this.numCohorts;
    }

    public int getCohort() {
        return this.cohort;
    }

    public String getEncoderId() {
        return new String(this.encoderIdBytes, StandardCharsets.UTF_8);
    }

    public byte[] encodeBoolean(boolean bool) {
        BitSet input = new BitSet(this.numBits);
        input.set(0, bool);
        return encodeBits(input);
    }

    public byte[] encodeOrdinal(int ordinal) {
        checkArgument(ordinal >= 0 && ordinal < this.numBits, "Ordinal value must be in range [0, numBits).");
        BitSet input = new BitSet(this.numBits);
        input.set(ordinal, true);
        return encodeBits(input);
    }

    public byte[] encodeString(String string) {
        byte[] digest;
        byte[] stringInUtf8 = string.getBytes(StandardCharsets.UTF_8);
        byte[] message = ByteBuffer.allocate(stringInUtf8.length + 4).putInt(this.cohort).put(stringInUtf8).array();
        synchronized (this) {
            this.md5.reset();
            digest = this.md5.digest(message);
        }
        verify(digest.length == 16);
        verify(this.numBloomHashes <= digest.length / 2);
        BitSet input = new BitSet(this.numBits);
        for (int i = 0; i < this.numBloomHashes; i++) {
            int digestWord = ((digest[i * 2] & 255) * 256) + (digest[(i * 2) + 1] & 255);
            int chosenBit = digestWord % this.numBits;
            input.set(chosenBit, true);
        }
        return encodeBits(input);
    }

    public byte[] encodeBits(byte[] bits) {
        return encodeBits(BitSet.valueOf(bits));
    }

    private byte[] encodeBits(BitSet bits) {
        BitSet permanentRandomizedResponse = computePermanentRandomizedResponse(bits);
        BitSet encodedBitSet = computeInstantaneousRandomizedResponse(permanentRandomizedResponse);
        byte[] encodedBytes = encodedBitSet.toByteArray();
        byte[] output = new byte[(this.numBits + 7) / 8];
        verify(encodedBytes.length <= output.length);
        System.arraycopy(encodedBytes, 0, output, 0, encodedBytes.length);
        return output;
    }

    private BitSet computePermanentRandomizedResponse(BitSet bits) {
        byte[] personalizationString;
        BitSet masked = new BitSet();
        masked.or(bits);
        masked.andNot(this.inputMask);
        checkArgument(masked.isEmpty(), "Input bits had bits set past Encoder's numBits limit.");
        if (this.probabilityF == 0.0d) {
            return bits;
        }
        synchronized (this) {
            int personalizationStringLength = Math.min(20, this.sha256.getDigestLength() + 1);
            personalizationString = new byte[personalizationStringLength];
            personalizationString[0] = HMAC_DRBG_TYPE_PRR;
            this.sha256.reset();
            this.sha256.update(this.encoderIdBytes);
            this.sha256.update(new byte[]{HMAC_DRBG_TYPE_COHORT});
            this.sha256.update(bits.toByteArray());
            byte[] digest = this.sha256.digest(personalizationString);
            System.arraycopy(digest, 0, personalizationString, 1, personalizationString.length - 1);
        }
        HmacDrbg drbg = new HmacDrbg(this.userSecret, personalizationString);
        byte[] pseudorandomStream = drbg.nextBytes(this.numBits);
        verify(this.numBits <= pseudorandomStream.length);
        int probabilityFTimes128 = (int) Math.round(this.probabilityF * 128.0d);
        BitSet result = new BitSet(this.numBits);
        for (int i = 0; i < this.numBits; i++) {
            int pseudorandomByte = pseudorandomStream[i] & 255;
            int uniform0to127 = pseudorandomByte >> 1;
            boolean shouldUseNoise = uniform0to127 < probabilityFTimes128;
            if (shouldUseNoise) {
                result.set(i, (pseudorandomByte & 1) > 0);
            } else {
                result.set(i, bits.get(i));
            }
        }
        return result;
    }

    private BitSet computeInstantaneousRandomizedResponse(BitSet bits) {
        BitSet masked = new BitSet();
        masked.or(bits);
        masked.andNot(this.inputMask);
        checkArgument(masked.isEmpty(), "Input bits had bits set past Encoder's numBits limit.");
        if (this.probabilityP == 0.0d && this.probabilityQ == 1.0d) {
            return bits;
        }
        BitSet response = new BitSet(this.numBits);
        for (int i = 0; i < this.numBits; i++) {
            boolean bit = bits.get(i);
            double probability = bit ? this.probabilityQ : this.probabilityP;
            boolean responseBit = ((double) this.random.nextFloat()) < probability;
            response.set(i, responseBit);
        }
        return response;
    }

    private static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    private static void verify(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
}
