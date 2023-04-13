package com.android.internal.org.bouncycastle.jce.provider;

import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.digests.AndroidDigestFactory;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
/* loaded from: classes4.dex */
public class CertBlocklist {
    private static final Logger logger = Logger.getLogger(CertBlocklist.class.getName());
    public final Set<byte[]> pubkeyBlocklist;
    public final Set<BigInteger> serialBlocklist;

    public CertBlocklist() {
        String androidData = System.getenv("ANDROID_DATA");
        String blocklistRoot = androidData + "/misc/keychain/";
        String defaultPubkeyBlocklistPath = blocklistRoot + "pubkey_blacklist.txt";
        String defaultSerialBlocklistPath = blocklistRoot + "serial_blacklist.txt";
        this.pubkeyBlocklist = readPublicKeyBlockList(defaultPubkeyBlocklistPath);
        this.serialBlocklist = readSerialBlockList(defaultSerialBlocklistPath);
    }

    public CertBlocklist(String pubkeyBlocklistPath, String serialBlocklistPath) {
        this.pubkeyBlocklist = readPublicKeyBlockList(pubkeyBlocklistPath);
        this.serialBlocklist = readSerialBlockList(serialBlocklistPath);
    }

    private static boolean isHex(String value) {
        try {
            new BigInteger(value, 16);
            return true;
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Could not parse hex value " + value, (Throwable) e);
            return false;
        }
    }

    private static boolean isPubkeyHash(String value) {
        if (value.length() != 40) {
            logger.log(Level.WARNING, "Invalid pubkey hash length: " + value.length());
            return false;
        }
        return isHex(value);
    }

    private static String readBlocklist(String path) {
        try {
            return readFileAsString(path);
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e2) {
            logger.log(Level.WARNING, "Could not read blocklist", (Throwable) e2);
            return "";
        }
    }

    private static String readFileAsString(String path) throws IOException {
        return readFileAsBytes(path).toString("UTF-8");
    }

    private static ByteArrayOutputStream readFileAsBytes(String path) throws IOException {
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(path, "r");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream((int) f.length());
            byte[] buffer = new byte[8192];
            while (true) {
                int byteCount = f.read(buffer);
                if (byteCount == -1) {
                    return bytes;
                }
                bytes.write(buffer, 0, byteCount);
            }
        } finally {
            closeQuietly(f);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }

    private static Set<BigInteger> readSerialBlockList(String path) {
        String[] split;
        Set<BigInteger> bl = new HashSet<>(Arrays.asList(new BigInteger("077a59bcd53459601ca6907267a6dd1c", 16), new BigInteger("047ecbe9fca55f7bd09eae36e10cae1e", 16), new BigInteger("d8f35f4eb7872b2dab0692e315382fb0", 16), new BigInteger("b0b7133ed096f9b56fae91c874bd3ac0", 16), new BigInteger("9239d5348f40d1695a745470e1f23f43", 16), new BigInteger("e9028b9578e415dc1a710a2b88154447", 16), new BigInteger("d7558fdaf5f1105bb213282b707729a3", 16), new BigInteger("f5c86af36162f13a64f54f6dc9587c06", 16), new BigInteger("392a434f0e07df1f8aa305de34e0c229", 16), new BigInteger("3e75ced46b693021218830ae86a82a71", 16)));
        String serialBlocklist = readBlocklist(path);
        if (!serialBlocklist.equals("")) {
            for (String value : serialBlocklist.split(",")) {
                try {
                    bl.add(new BigInteger(value, 16));
                } catch (NumberFormatException e) {
                    logger.log(Level.WARNING, "Tried to blocklist invalid serial number " + value, (Throwable) e);
                }
            }
        }
        return Collections.unmodifiableSet(bl);
    }

    private static Set<byte[]> readPublicKeyBlockList(String path) {
        Set<byte[]> bl = new HashSet<>(Arrays.asList("410f36363258f30b347d12ce4863e433437806a8".getBytes(), "ba3e7bd38cd7e1e6b9cd4c219962e59d7a2f4e37".getBytes(), "e23b8d105f87710a68d9248050ebefc627be4ca6".getBytes(), "7b2e16bc39bcd72b456e9f055d1de615b74945db".getBytes(), "e8f91200c65cee16e039b9f883841661635f81c5".getBytes(), "0129bcd5b448ae8d2496d1c3e19723919088e152".getBytes(), "5f3ab33d55007054bc5e3e5553cd8d8465d77c61".getBytes(), "783333c9687df63377efceddd82efa9101913e8e".getBytes(), "3ecf4bbbe46096d514bb539bb913d77aa4ef31bf".getBytes()));
        String pubkeyBlocklist = readBlocklist(path);
        if (!pubkeyBlocklist.equals("")) {
            for (String value : pubkeyBlocklist.split(",")) {
                String value2 = value.trim();
                if (isPubkeyHash(value2)) {
                    bl.add(value2.getBytes());
                } else {
                    logger.log(Level.WARNING, "Tried to blocklist invalid pubkey " + value2);
                }
            }
        }
        return bl;
    }

    public boolean isPublicKeyBlockListed(PublicKey publicKey) {
        byte[] encoded = publicKey.getEncoded();
        Digest digest = AndroidDigestFactory.getSHA1();
        digest.update(encoded, 0, encoded.length);
        byte[] out = new byte[digest.getDigestSize()];
        digest.doFinal(out, 0);
        for (byte[] blocklisted : this.pubkeyBlocklist) {
            if (Arrays.equals(blocklisted, Hex.encode(out))) {
                return true;
            }
        }
        return false;
    }

    public boolean isSerialNumberBlockListed(BigInteger serial) {
        return this.serialBlocklist.contains(serial);
    }
}
