package com.android.internal.org.bouncycastle.util;

import com.android.internal.org.bouncycastle.math.raw.Mod;
import com.android.internal.org.bouncycastle.math.raw.Nat;
import java.math.BigInteger;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public final class BigIntegers {
    private static final int MAX_ITERATIONS = 1000;
    public static final BigInteger ZERO = BigInteger.valueOf(0);
    public static final BigInteger ONE = BigInteger.valueOf(1);
    public static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger("8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f", 16);
    private static final int MAX_SMALL = BigInteger.valueOf(743).bitLength();

    public static byte[] asUnsignedByteArray(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes[0] == 0 && bytes.length != 1) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            return tmp;
        }
        return bytes;
    }

    public static byte[] asUnsignedByteArray(int length, BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length == length) {
            return bytes;
        }
        int start = 0;
        if (bytes[0] == 0 && bytes.length != 1) {
            start = 1;
        }
        int count = bytes.length - start;
        if (count > length) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }
        byte[] tmp = new byte[length];
        System.arraycopy(bytes, start, tmp, tmp.length - count, count);
        return tmp;
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0012, code lost:
        if (r0.length != 1) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void asUnsignedByteArray(BigInteger value, byte[] buf, int off, int len) {
        int i;
        byte[] bytes = value.toByteArray();
        if (bytes.length == len) {
            System.arraycopy(bytes, 0, buf, off, len);
            return;
        }
        if (bytes[0] == 0) {
            i = 1;
        }
        i = 0;
        int start = i;
        int count = bytes.length - start;
        if (count > len) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }
        int padLen = len - count;
        Arrays.fill(buf, off, off + padLen, (byte) 0);
        System.arraycopy(bytes, start, buf, off + padLen, count);
    }

    public static BigInteger createRandomInRange(BigInteger min, BigInteger max, SecureRandom random) {
        int cmp = min.compareTo(max);
        if (cmp >= 0) {
            if (cmp > 0) {
                throw new IllegalArgumentException("'min' may not be greater than 'max'");
            }
            return min;
        } else if (min.bitLength() > max.bitLength() / 2) {
            return createRandomInRange(ZERO, max.subtract(min), random).add(min);
        } else {
            for (int i = 0; i < 1000; i++) {
                BigInteger x = createRandomBigInteger(max.bitLength(), random);
                if (x.compareTo(min) >= 0 && x.compareTo(max) <= 0) {
                    return x;
                }
            }
            return createRandomBigInteger(max.subtract(min).bitLength() - 1, random).add(min);
        }
    }

    public static BigInteger fromUnsignedByteArray(byte[] buf) {
        return new BigInteger(1, buf);
    }

    public static BigInteger fromUnsignedByteArray(byte[] buf, int off, int length) {
        byte[] mag = buf;
        if (off != 0 || length != buf.length) {
            mag = new byte[length];
            System.arraycopy(buf, off, mag, 0, length);
        }
        return new BigInteger(1, mag);
    }

    public static int intValueExact(BigInteger x) {
        if (x.bitLength() > 31) {
            throw new ArithmeticException("BigInteger out of int range");
        }
        return x.intValue();
    }

    public static long longValueExact(BigInteger x) {
        if (x.bitLength() > 63) {
            throw new ArithmeticException("BigInteger out of long range");
        }
        return x.longValue();
    }

    public static BigInteger modOddInverse(BigInteger M, BigInteger X) {
        if (!M.testBit(0)) {
            throw new IllegalArgumentException("'M' must be odd");
        }
        if (M.signum() != 1) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        if (X.signum() < 0 || X.compareTo(M) >= 0) {
            X = X.mod(M);
        }
        int bits = M.bitLength();
        int[] m = Nat.fromBigInteger(bits, M);
        int[] x = Nat.fromBigInteger(bits, X);
        int len = m.length;
        int[] z = Nat.create(len);
        if (Mod.modOddInverse(m, x, z) == 0) {
            throw new ArithmeticException("BigInteger not invertible.");
        }
        return Nat.toBigInteger(len, z);
    }

    public static BigInteger modOddInverseVar(BigInteger M, BigInteger X) {
        if (!M.testBit(0)) {
            throw new IllegalArgumentException("'M' must be odd");
        }
        if (M.signum() != 1) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        BigInteger bigInteger = ONE;
        if (M.equals(bigInteger)) {
            return ZERO;
        }
        if (X.signum() < 0 || X.compareTo(M) >= 0) {
            X = X.mod(M);
        }
        if (X.equals(bigInteger)) {
            return bigInteger;
        }
        int bits = M.bitLength();
        int[] m = Nat.fromBigInteger(bits, M);
        int[] x = Nat.fromBigInteger(bits, X);
        int len = m.length;
        int[] z = Nat.create(len);
        if (!Mod.modOddInverseVar(m, x, z)) {
            throw new ArithmeticException("BigInteger not invertible.");
        }
        return Nat.toBigInteger(len, z);
    }

    public static int getUnsignedByteLength(BigInteger n) {
        if (n.equals(ZERO)) {
            return 1;
        }
        return (n.bitLength() + 7) / 8;
    }

    public static BigInteger createRandomBigInteger(int bitLength, SecureRandom random) {
        return new BigInteger(1, createRandom(bitLength, random));
    }

    public static BigInteger createRandomPrime(int bitLength, int certainty, SecureRandom random) {
        BigInteger rv;
        if (bitLength < 2) {
            throw new IllegalArgumentException("bitLength < 2");
        }
        if (bitLength == 2) {
            return random.nextInt() < 0 ? TWO : THREE;
        }
        do {
            byte[] base = createRandom(bitLength, random);
            int xBits = (base.length * 8) - bitLength;
            byte lead = (byte) (1 << (7 - xBits));
            base[0] = (byte) (base[0] | lead);
            int length = base.length - 1;
            base[length] = (byte) (base[length] | 1);
            rv = new BigInteger(1, base);
            if (bitLength > MAX_SMALL) {
                while (!rv.gcd(SMALL_PRIMES_PRODUCT).equals(ONE)) {
                    rv = rv.add(TWO);
                }
            }
        } while (!rv.isProbablePrime(certainty));
        return rv;
    }

    private static byte[] createRandom(int bitLength, SecureRandom random) throws IllegalArgumentException {
        if (bitLength < 1) {
            throw new IllegalArgumentException("bitLength must be at least 1");
        }
        int nBytes = (bitLength + 7) / 8;
        byte[] rv = new byte[nBytes];
        random.nextBytes(rv);
        int xBits = (nBytes * 8) - bitLength;
        rv[0] = (byte) (rv[0] & ((byte) (255 >>> xBits)));
        return rv;
    }
}
