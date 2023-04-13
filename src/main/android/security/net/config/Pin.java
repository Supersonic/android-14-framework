package android.security.net.config;

import java.util.Arrays;
/* loaded from: classes3.dex */
public final class Pin {
    public final byte[] digest;
    public final String digestAlgorithm;
    private final int mHashCode;

    public Pin(String digestAlgorithm, byte[] digest) {
        this.digestAlgorithm = digestAlgorithm;
        this.digest = digest;
        this.mHashCode = Arrays.hashCode(digest) ^ digestAlgorithm.hashCode();
    }

    public static boolean isSupportedDigestAlgorithm(String algorithm) {
        return "SHA-256".equalsIgnoreCase(algorithm);
    }

    public static int getDigestLength(String algorithm) {
        if ("SHA-256".equalsIgnoreCase(algorithm)) {
            return 32;
        }
        throw new IllegalArgumentException("Unsupported digest algorithm: " + algorithm);
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Pin) {
            Pin other = (Pin) obj;
            return other.hashCode() == this.mHashCode && Arrays.equals(this.digest, other.digest) && this.digestAlgorithm.equals(other.digestAlgorithm);
        }
        return false;
    }
}
