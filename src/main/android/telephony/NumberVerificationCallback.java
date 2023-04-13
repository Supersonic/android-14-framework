package android.telephony;

import android.annotation.SystemApi;
@SystemApi
/* loaded from: classes3.dex */
public interface NumberVerificationCallback {
    public static final int REASON_CONCURRENT_REQUESTS = 4;
    public static final int REASON_IN_ECBM = 5;
    public static final int REASON_IN_EMERGENCY_CALL = 6;
    public static final int REASON_NETWORK_NOT_AVAILABLE = 2;
    public static final int REASON_TIMED_OUT = 1;
    public static final int REASON_TOO_MANY_CALLS = 3;
    public static final int REASON_UNSPECIFIED = 0;

    /* loaded from: classes3.dex */
    public @interface NumberVerificationFailureReason {
    }

    default void onCallReceived(String phoneNumber) {
    }

    default void onVerificationFailed(int reason) {
    }
}
