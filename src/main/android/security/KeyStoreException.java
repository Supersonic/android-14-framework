package android.security;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public class KeyStoreException extends Exception {
    public static final int ERROR_ATTESTATION_CHALLENGE_TOO_LARGE = 9;
    public static final int ERROR_ATTESTATION_KEYS_UNAVAILABLE = 16;
    public static final int ERROR_DEVICE_REQUIRES_UPGRADE_FOR_ATTESTATION = 17;
    public static final int ERROR_ID_ATTESTATION_FAILURE = 8;
    public static final int ERROR_INCORRECT_USAGE = 13;
    public static final int ERROR_INTERNAL_SYSTEM_ERROR = 4;
    public static final int ERROR_KEYMINT_FAILURE = 10;
    public static final int ERROR_KEYSTORE_FAILURE = 11;
    public static final int ERROR_KEYSTORE_UNINITIALIZED = 3;
    public static final int ERROR_KEY_CORRUPTED = 7;
    public static final int ERROR_KEY_DOES_NOT_EXIST = 6;
    public static final int ERROR_KEY_NOT_TEMPORALLY_VALID = 14;
    public static final int ERROR_KEY_OPERATION_EXPIRED = 15;
    public static final int ERROR_OTHER = 1;
    public static final int ERROR_PERMISSION_DENIED = 5;
    public static final int ERROR_UNIMPLEMENTED = 12;
    public static final int ERROR_USER_AUTHENTICATION_REQUIRED = 2;
    private static final PublicErrorInformation GENERAL_KEYMINT_ERROR;
    private static final PublicErrorInformation GENERAL_KEYSTORE_ERROR;
    private static final int IS_SYSTEM_ERROR = 2;
    private static final int IS_TRANSIENT_ERROR = 4;
    private static final PublicErrorInformation KEYMINT_INCORRECT_USAGE_ERROR;
    private static final PublicErrorInformation KEYMINT_RETRYABLE_ERROR;
    private static final PublicErrorInformation KEYMINT_TEMPORAL_VALIDITY_ERROR;
    private static final PublicErrorInformation KEYMINT_UNIMPLEMENTED_ERROR;
    private static final int REQUIRES_USER_AUTHENTICATION = 8;
    public static final int RETRY_AFTER_NEXT_REBOOT = 4;
    public static final int RETRY_NEVER = 1;
    public static final int RETRY_WHEN_CONNECTIVITY_AVAILABLE = 3;
    public static final int RETRY_WITH_EXPONENTIAL_BACKOFF = 2;
    public static final int RKP_FETCHING_PENDING_CONNECTIVITY = 3;
    public static final int RKP_FETCHING_PENDING_SOFTWARE_REBOOT = 4;
    public static final int RKP_SERVER_REFUSED_ISSUANCE = 2;
    public static final int RKP_SUCCESS = 0;
    public static final int RKP_TEMPORARILY_UNAVAILABLE = 1;
    private static final String TAG = "KeyStoreException";
    private static final Map<Integer, PublicErrorInformation> sErrorCodeToFailureInfo;
    private final int mErrorCode;
    private final int mRkpStatus;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface PublicErrorCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RetryPolicy {
    }

    private static int initializeRkpStatusForRegularErrors(int errorCode) {
        if (errorCode == 22) {
            Log.m110e(TAG, "RKP error code without RKP status");
            return 2;
        }
        return 0;
    }

    public KeyStoreException(int errorCode, String message) {
        super(message);
        this.mErrorCode = errorCode;
        this.mRkpStatus = initializeRkpStatusForRegularErrors(errorCode);
    }

    public KeyStoreException(int errorCode, String message, String keystoreErrorMessage) {
        super(message + " (internal Keystore code: " + errorCode + " message: " + keystoreErrorMessage + NavigationBarInflaterView.KEY_CODE_END);
        this.mErrorCode = errorCode;
        this.mRkpStatus = initializeRkpStatusForRegularErrors(errorCode);
    }

    public KeyStoreException(int errorCode, String message, int rkpStatus) {
        super(message);
        this.mErrorCode = errorCode;
        this.mRkpStatus = rkpStatus;
        if (errorCode != 22) {
            Log.m110e(TAG, "Providing RKP status for error code " + errorCode + " has no effect.");
        }
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public int getNumericErrorCode() {
        PublicErrorInformation failureInfo = getErrorInformation(this.mErrorCode);
        return failureInfo.errorCode;
    }

    public boolean isTransientFailure() {
        PublicErrorInformation failureInfo = getErrorInformation(this.mErrorCode);
        int i = this.mRkpStatus;
        if (i == 0 || this.mErrorCode != 22) {
            return (failureInfo.indicators & 4) != 0;
        }
        switch (i) {
            case 1:
            case 3:
            case 4:
                return true;
            case 2:
            default:
                return false;
        }
    }

    public boolean requiresUserAuthentication() {
        PublicErrorInformation failureInfo = getErrorInformation(this.mErrorCode);
        return (failureInfo.indicators & 8) != 0;
    }

    public boolean isSystemError() {
        PublicErrorInformation failureInfo = getErrorInformation(this.mErrorCode);
        return (failureInfo.indicators & 2) != 0;
    }

    public int getRetryPolicy() {
        PublicErrorInformation failureInfo = getErrorInformation(this.mErrorCode);
        int i = this.mRkpStatus;
        if (i == 0) {
            switch (this.mErrorCode) {
                case 23:
                    return 4;
                case 24:
                    return 3;
                default:
                    return (failureInfo.indicators & 4) != 0 ? 2 : 1;
            }
        }
        switch (i) {
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 3;
            case 4:
                return 4;
            default:
                return (failureInfo.indicators & 4) != 0 ? 2 : 1;
        }
    }

    @Override // java.lang.Throwable
    public String toString() {
        String errorCodes = String.format(" (public error code: %d internal Keystore code: %d)", Integer.valueOf(getNumericErrorCode()), Integer.valueOf(this.mErrorCode));
        return super.toString() + errorCodes;
    }

    private static PublicErrorInformation getErrorInformation(int internalErrorCode) {
        PublicErrorInformation errorInfo = sErrorCodeToFailureInfo.get(Integer.valueOf(internalErrorCode));
        if (errorInfo != null) {
            return errorInfo;
        }
        if (internalErrorCode > 0) {
            return GENERAL_KEYSTORE_ERROR;
        }
        return GENERAL_KEYMINT_ERROR;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class PublicErrorInformation {
        public final int errorCode;
        public final int indicators;

        PublicErrorInformation(int indicators, int errorCode) {
            this.indicators = indicators;
            this.errorCode = errorCode;
        }
    }

    static {
        PublicErrorInformation publicErrorInformation = new PublicErrorInformation(0, 10);
        GENERAL_KEYMINT_ERROR = publicErrorInformation;
        GENERAL_KEYSTORE_ERROR = new PublicErrorInformation(0, 11);
        PublicErrorInformation publicErrorInformation2 = new PublicErrorInformation(2, 12);
        KEYMINT_UNIMPLEMENTED_ERROR = publicErrorInformation2;
        PublicErrorInformation publicErrorInformation3 = new PublicErrorInformation(6, 10);
        KEYMINT_RETRYABLE_ERROR = publicErrorInformation3;
        PublicErrorInformation publicErrorInformation4 = new PublicErrorInformation(0, 13);
        KEYMINT_INCORRECT_USAGE_ERROR = publicErrorInformation4;
        PublicErrorInformation publicErrorInformation5 = new PublicErrorInformation(0, 14);
        KEYMINT_TEMPORAL_VALIDITY_ERROR = publicErrorInformation5;
        HashMap hashMap = new HashMap();
        sErrorCodeToFailureInfo = hashMap;
        hashMap.put(0, publicErrorInformation);
        hashMap.put(-1, publicErrorInformation);
        hashMap.put(-2, publicErrorInformation4);
        hashMap.put(-3, publicErrorInformation4);
        hashMap.put(-4, publicErrorInformation2);
        hashMap.put(-5, publicErrorInformation4);
        hashMap.put(-6, publicErrorInformation2);
        hashMap.put(-7, publicErrorInformation2);
        hashMap.put(-8, publicErrorInformation4);
        hashMap.put(-9, publicErrorInformation2);
        hashMap.put(-10, publicErrorInformation4);
        hashMap.put(-11, publicErrorInformation4);
        hashMap.put(-12, publicErrorInformation2);
        hashMap.put(-13, publicErrorInformation4);
        hashMap.put(-14, publicErrorInformation4);
        hashMap.put(-15, publicErrorInformation);
        hashMap.put(-16, publicErrorInformation4);
        hashMap.put(-17, publicErrorInformation4);
        hashMap.put(-18, publicErrorInformation4);
        hashMap.put(-19, publicErrorInformation2);
        hashMap.put(-20, publicErrorInformation2);
        hashMap.put(-21, publicErrorInformation4);
        hashMap.put(-22, publicErrorInformation4);
        hashMap.put(-23, publicErrorInformation);
        hashMap.put(-24, publicErrorInformation5);
        hashMap.put(-25, publicErrorInformation5);
        hashMap.put(-26, new PublicErrorInformation(8, 2));
        hashMap.put(-27, publicErrorInformation);
        hashMap.put(-28, new PublicErrorInformation(6, 15));
        hashMap.put(-29, publicErrorInformation);
        hashMap.put(-30, publicErrorInformation);
        hashMap.put(-31, publicErrorInformation);
        hashMap.put(-32, publicErrorInformation);
        hashMap.put(-33, publicErrorInformation);
        hashMap.put(-34, publicErrorInformation4);
        hashMap.put(-35, publicErrorInformation4);
        hashMap.put(-36, publicErrorInformation4);
        hashMap.put(-37, publicErrorInformation4);
        hashMap.put(-38, publicErrorInformation);
        hashMap.put(-39, publicErrorInformation2);
        hashMap.put(-40, publicErrorInformation4);
        hashMap.put(-41, publicErrorInformation);
        hashMap.put(-44, publicErrorInformation);
        hashMap.put(-45, publicErrorInformation);
        hashMap.put(-46, publicErrorInformation);
        hashMap.put(-47, publicErrorInformation);
        hashMap.put(-48, publicErrorInformation3);
        hashMap.put(-49, publicErrorInformation3);
        hashMap.put(-50, publicErrorInformation2);
        hashMap.put(-51, publicErrorInformation4);
        hashMap.put(-52, publicErrorInformation4);
        hashMap.put(-53, publicErrorInformation4);
        hashMap.put(-54, publicErrorInformation3);
        hashMap.put(-55, publicErrorInformation);
        hashMap.put(-56, publicErrorInformation);
        hashMap.put(-57, publicErrorInformation4);
        hashMap.put(-58, publicErrorInformation4);
        hashMap.put(-59, publicErrorInformation4);
        hashMap.put(-60, publicErrorInformation2);
        hashMap.put(-61, publicErrorInformation2);
        hashMap.put(-63, publicErrorInformation4);
        hashMap.put(-64, new PublicErrorInformation(2, 10));
        hashMap.put(-65, publicErrorInformation3);
        hashMap.put(-66, new PublicErrorInformation(2, 8));
        hashMap.put(-67, publicErrorInformation2);
        hashMap.put(-68, publicErrorInformation2);
        hashMap.put(-72, new PublicErrorInformation(10, 2));
        hashMap.put(-77, publicErrorInformation2);
        hashMap.put(-78, publicErrorInformation4);
        hashMap.put(-79, publicErrorInformation2);
        hashMap.put(-80, publicErrorInformation4);
        hashMap.put(-81, publicErrorInformation4);
        hashMap.put(-85, publicErrorInformation);
        hashMap.put(-100, publicErrorInformation2);
        hashMap.put(-1000, new PublicErrorInformation(2, 10));
        hashMap.put(-101, publicErrorInformation);
        hashMap.put(2, new PublicErrorInformation(8, 2));
        hashMap.put(3, new PublicErrorInformation(2, 3));
        hashMap.put(4, new PublicErrorInformation(2, 4));
        hashMap.put(6, new PublicErrorInformation(0, 5));
        hashMap.put(7, new PublicErrorInformation(0, 6));
        hashMap.put(8, new PublicErrorInformation(0, 7));
        hashMap.put(17, new PublicErrorInformation(0, 6));
        hashMap.put(22, new PublicErrorInformation(2, 16));
        hashMap.put(23, new PublicErrorInformation(6, 17));
        hashMap.put(24, new PublicErrorInformation(6, 16));
        hashMap.put(25, new PublicErrorInformation(6, 16));
        hashMap.put(26, new PublicErrorInformation(2, 16));
    }

    public static boolean hasFailureInfoForError(int internalErrorCode) {
        return sErrorCodeToFailureInfo.containsKey(Integer.valueOf(internalErrorCode));
    }
}
