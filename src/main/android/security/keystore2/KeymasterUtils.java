package android.security.keystore2;

import android.security.keymaster.KeymasterArguments;
import android.security.keystore.KeyProperties;
import com.android.internal.util.ArrayUtils;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;
/* loaded from: classes3.dex */
public abstract class KeymasterUtils {
    private KeymasterUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getDigestOutputSizeBits(int keymasterDigest) {
        switch (keymasterDigest) {
            case 0:
                return -1;
            case 1:
                return 128;
            case 2:
                return 160;
            case 3:
                return 224;
            case 4:
                return 256;
            case 5:
                return 384;
            case 6:
                return 512;
            default:
                throw new IllegalArgumentException("Unknown digest: " + keymasterDigest);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isKeymasterBlockModeIndCpaCompatibleWithSymmetricCrypto(int keymasterBlockMode) {
        switch (keymasterBlockMode) {
            case 1:
                return false;
            case 2:
            case 3:
            case 32:
                return true;
            default:
                throw new IllegalArgumentException("Unsupported block mode: " + keymasterBlockMode);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isKeymasterPaddingSchemeIndCpaCompatibleWithAsymmetricCrypto(int keymasterPadding) {
        switch (keymasterPadding) {
            case 1:
                return false;
            case 2:
            case 4:
                return true;
            case 3:
            default:
                throw new IllegalArgumentException("Unsupported asymmetric encryption padding scheme: " + keymasterPadding);
        }
    }

    public static void addMinMacLengthAuthorizationIfNecessary(KeymasterArguments args, int keymasterAlgorithm, int[] keymasterBlockModes, int[] keymasterDigests) {
        switch (keymasterAlgorithm) {
            case 32:
                if (ArrayUtils.contains(keymasterBlockModes, 32)) {
                    args.addUnsignedInt(805306376, 96L);
                    return;
                }
                return;
            case 128:
                if (keymasterDigests.length != 1) {
                    throw new ProviderException("Unsupported number of authorized digests for HMAC key: " + keymasterDigests.length + ". Exactly one digest must be authorized");
                }
                int keymasterDigest = keymasterDigests[0];
                int digestOutputSizeBits = getDigestOutputSizeBits(keymasterDigest);
                if (digestOutputSizeBits != -1) {
                    args.addUnsignedInt(805306376, digestOutputSizeBits);
                    return;
                }
                throw new ProviderException("HMAC key authorized for unsupported digest: " + KeyProperties.Digest.fromKeymaster(keymasterDigest));
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getEcCurveFromKeymaster(int ecCurve) {
        switch (ecCurve) {
            case 0:
                return "secp224r1";
            case 1:
                return "secp256r1";
            case 2:
                return "secp384r1";
            case 3:
                return "secp521r1";
            default:
                return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getKeymasterEcCurve(String ecCurveName) {
        if (ecCurveName.equals("secp224r1")) {
            return 0;
        }
        if (ecCurveName.equals("secp256r1")) {
            return 1;
        }
        if (ecCurveName.equals("secp384r1")) {
            return 2;
        }
        if (ecCurveName.equals("secp521r1")) {
            return 3;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ECParameterSpec getCurveSpec(String name) throws NoSuchAlgorithmException, InvalidParameterSpecException {
        AlgorithmParameters parameters = AlgorithmParameters.getInstance(KeyProperties.KEY_ALGORITHM_EC);
        parameters.init(new ECGenParameterSpec(name));
        return (ECParameterSpec) parameters.getParameterSpec(ECParameterSpec.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getCurveName(ECParameterSpec spec) {
        if (isECParameterSpecOfCurve(spec, "secp224r1")) {
            return "secp224r1";
        }
        if (isECParameterSpecOfCurve(spec, "secp256r1")) {
            return "secp256r1";
        }
        if (isECParameterSpecOfCurve(spec, "secp384r1")) {
            return "secp384r1";
        }
        if (isECParameterSpecOfCurve(spec, "secp521r1")) {
            return "secp521r1";
        }
        return null;
    }

    private static boolean isECParameterSpecOfCurve(ECParameterSpec spec, String curveName) {
        try {
            ECParameterSpec curveSpec = getCurveSpec(curveName);
            if (curveSpec.getCurve().equals(spec.getCurve()) && curveSpec.getOrder().equals(spec.getOrder())) {
                if (curveSpec.getGenerator().equals(spec.getGenerator())) {
                    return true;
                }
            }
            return false;
        } catch (NoSuchAlgorithmException | InvalidParameterSpecException e) {
            return false;
        }
    }
}
