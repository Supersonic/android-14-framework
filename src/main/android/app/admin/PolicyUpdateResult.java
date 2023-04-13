package android.app.admin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public final class PolicyUpdateResult {
    public static final int RESULT_FAILURE_CONFLICTING_ADMIN_POLICY = 1;
    public static final int RESULT_FAILURE_HARDWARE_LIMITATION = 4;
    public static final int RESULT_FAILURE_STORAGE_LIMIT_REACHED = 3;
    public static final int RESULT_FAILURE_UNKNOWN = -1;
    public static final int RESULT_POLICY_CLEARED = 2;
    public static final int RESULT_SUCCESS = 0;
    private final int mResultCode;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ResultCode {
    }

    public PolicyUpdateResult(int resultCode) {
        this.mResultCode = resultCode;
    }

    public int getResultCode() {
        return this.mResultCode;
    }
}
