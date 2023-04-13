package android.content.p001pm.parsing;

import android.content.p001pm.PackageManager;
import android.content.p001pm.Signature;
import android.content.p001pm.SigningDetails;
import android.content.p001pm.parsing.result.ParseInput;
import android.content.p001pm.parsing.result.ParseResult;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.internal.modules.utils.build.UnboundedSdkLevel;
import android.p008os.FileUtils;
import android.p008os.SystemProperties;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Slog;
import android.util.apk.ApkSignatureVerifier;
import com.android.internal.util.ArrayUtils;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
/* renamed from: android.content.pm.parsing.FrameworkParsingPackageUtils */
/* loaded from: classes.dex */
public class FrameworkParsingPackageUtils {
    private static final int MAX_FILE_NAME_SIZE = 223;
    public static final int PARSE_APK_IN_APEX = 512;
    public static final int PARSE_IGNORE_OVERLAY_REQUIRED_SYSTEM_PROPERTY = 128;
    private static final String TAG = "FrameworkParsingPackageUtils";

    public static String validateName(String name, boolean requireSeparator, boolean requireFilename) {
        int N = name.length();
        boolean hasSep = false;
        boolean front = true;
        for (int i = 0; i < N; i++) {
            char c = name.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                front = false;
            } else if (front || ((c < '0' || c > '9') && c != '_')) {
                if (c == '.') {
                    hasSep = true;
                    front = true;
                } else {
                    return "bad character '" + c + "'";
                }
            }
        }
        if (requireFilename) {
            if (!FileUtils.isValidExtFilename(name)) {
                return "Invalid filename";
            }
            if (N > 223) {
                return "the length of the name is greater than 223";
            }
        }
        if (hasSep || !requireSeparator) {
            return null;
        }
        return "must have at least one '.' separator";
    }

    public static ParseResult validateName(ParseInput input, String name, boolean requireSeparator, boolean requireFilename) {
        String errorMessage = validateName(name, requireSeparator, requireFilename);
        if (errorMessage != null) {
            return input.error(errorMessage);
        }
        return input.success(null);
    }

    public static PublicKey parsePublicKey(String encodedPublicKey) {
        if (encodedPublicKey == null) {
            Slog.m90w(TAG, "Could not parse null public key");
            return null;
        }
        try {
            return parsePublicKey(Base64.decode(encodedPublicKey, 0));
        } catch (IllegalArgumentException e) {
            Slog.m90w(TAG, "Could not parse verifier public key; invalid Base64");
            return null;
        }
    }

    public static PublicKey parsePublicKey(byte[] publicKey) {
        if (publicKey == null) {
            Slog.m90w(TAG, "Could not parse null public key");
            return null;
        }
        try {
            EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA);
                return keyFactory.generatePublic(keySpec);
            } catch (NoSuchAlgorithmException e) {
                Slog.wtf(TAG, "Could not parse public key: RSA KeyFactory not included in build");
                try {
                    KeyFactory keyFactory2 = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC);
                    return keyFactory2.generatePublic(keySpec);
                } catch (NoSuchAlgorithmException e2) {
                    Slog.wtf(TAG, "Could not parse public key: EC KeyFactory not included in build");
                    try {
                        KeyFactory keyFactory3 = KeyFactory.getInstance("DSA");
                        return keyFactory3.generatePublic(keySpec);
                    } catch (NoSuchAlgorithmException e3) {
                        Slog.wtf(TAG, "Could not parse public key: DSA KeyFactory not included in build");
                        return null;
                    } catch (InvalidKeySpecException e4) {
                        return null;
                    }
                } catch (InvalidKeySpecException e5) {
                    KeyFactory keyFactory32 = KeyFactory.getInstance("DSA");
                    return keyFactory32.generatePublic(keySpec);
                }
            } catch (InvalidKeySpecException e6) {
                KeyFactory keyFactory22 = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC);
                return keyFactory22.generatePublic(keySpec);
            }
        } catch (IllegalArgumentException e7) {
            Slog.m90w(TAG, "Could not parse verifier public key; invalid Base64");
            return null;
        }
    }

    public static boolean checkRequiredSystemProperties(String rawPropNames, String rawPropValues) {
        if (TextUtils.isEmpty(rawPropNames) || TextUtils.isEmpty(rawPropValues)) {
            if (TextUtils.isEmpty(rawPropNames) && TextUtils.isEmpty(rawPropValues)) {
                return true;
            }
            Slog.m90w(TAG, "Disabling overlay - incomplete property :'" + rawPropNames + "=" + rawPropValues + "' - require both requiredSystemPropertyName AND requiredSystemPropertyValue to be specified.");
            return false;
        }
        String[] propNames = rawPropNames.split(",");
        String[] propValues = rawPropValues.split(",");
        if (propNames.length != propValues.length) {
            Slog.m90w(TAG, "Disabling overlay - property :'" + rawPropNames + "=" + rawPropValues + "' - require both requiredSystemPropertyName AND requiredSystemPropertyValue lists to have the same size.");
            return false;
        }
        for (int i = 0; i < propNames.length; i++) {
            String currValue = SystemProperties.get(propNames[i]);
            if (!TextUtils.equals(currValue, propValues[i])) {
                return false;
            }
        }
        return true;
    }

    public static ParseResult<SigningDetails> getSigningDetails(ParseInput input, String baseCodePath, boolean skipVerify, boolean isStaticSharedLibrary, SigningDetails existingSigningDetails, int targetSdk) {
        ParseResult<SigningDetails> verified;
        int minSignatureScheme = ApkSignatureVerifier.getMinimumSignatureSchemeVersionForTargetSdk(targetSdk);
        if (isStaticSharedLibrary) {
            minSignatureScheme = 2;
        }
        if (skipVerify) {
            verified = ApkSignatureVerifier.unsafeGetCertsWithoutVerification(input, baseCodePath, 1);
        } else {
            verified = ApkSignatureVerifier.verify(input, baseCodePath, minSignatureScheme);
        }
        if (verified.isError()) {
            return input.error(verified);
        }
        if (existingSigningDetails == SigningDetails.UNKNOWN) {
            return verified;
        }
        if (!Signature.areExactMatch(existingSigningDetails.getSignatures(), verified.getResult().getSignatures())) {
            return input.error(PackageManager.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES, baseCodePath + " has mismatched certificates");
        }
        return input.success(existingSigningDetails);
    }

    public static ParseResult<Integer> computeMinSdkVersion(int minVers, String minCode, int platformSdkVersion, String[] platformSdkCodenames, ParseInput input) {
        if (minCode == null) {
            if (minVers > platformSdkVersion) {
                return input.error(-12, "Requires newer sdk version #" + minVers + " (current version is #" + platformSdkVersion + NavigationBarInflaterView.KEY_CODE_END);
            }
            return input.success(Integer.valueOf(minVers));
        } else if (matchTargetCode(platformSdkCodenames, minCode)) {
            return input.success(10000);
        } else {
            if (platformSdkCodenames.length > 0) {
                return input.error(-12, "Requires development platform " + minCode + " (current platform is any of " + Arrays.toString(platformSdkCodenames) + NavigationBarInflaterView.KEY_CODE_END);
            }
            return input.error(-12, "Requires development platform " + minCode + " but this is a release platform.");
        }
    }

    public static ParseResult<Integer> computeTargetSdkVersion(int targetVers, String targetCode, String[] platformSdkCodenames, ParseInput input, boolean allowUnknownCodenames) {
        if (targetCode == null) {
            return input.success(Integer.valueOf(targetVers));
        }
        if (allowUnknownCodenames) {
            try {
                if (UnboundedSdkLevel.isAtMost(targetCode)) {
                    return input.success(10000);
                }
            } catch (IllegalArgumentException e) {
                return input.error(-12, e.getMessage());
            }
        }
        if (matchTargetCode(platformSdkCodenames, targetCode)) {
            return input.success(10000);
        }
        if (platformSdkCodenames.length > 0) {
            return input.error(-12, "Requires development platform " + targetCode + " (current platform is any of " + Arrays.toString(platformSdkCodenames) + NavigationBarInflaterView.KEY_CODE_END);
        }
        return input.error(-12, "Requires development platform " + targetCode + " but this is a release platform.");
    }

    public static ParseResult<Integer> computeMaxSdkVersion(int maxVers, int platformSdkVersion, ParseInput input) {
        if (platformSdkVersion > maxVers) {
            return input.error(-14, "Requires max SDK version " + maxVers + " but is " + platformSdkVersion);
        }
        return input.success(Integer.valueOf(maxVers));
    }

    private static boolean matchTargetCode(String[] codeNames, String targetCode) {
        String targetCodeName;
        int targetCodeIdx = targetCode.indexOf(46);
        if (targetCodeIdx == -1) {
            targetCodeName = targetCode;
        } else {
            targetCodeName = targetCode.substring(0, targetCodeIdx);
        }
        return ArrayUtils.contains(codeNames, targetCodeName);
    }
}
