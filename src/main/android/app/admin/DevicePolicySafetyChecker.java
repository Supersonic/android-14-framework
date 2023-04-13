package android.app.admin;

import com.android.internal.p028os.IResultReceiver;
/* loaded from: classes.dex */
public interface DevicePolicySafetyChecker {
    int getUnsafeOperationReason(int i);

    boolean isSafeOperation(int i);

    void onFactoryReset(IResultReceiver iResultReceiver);

    default UnsafeStateException newUnsafeStateException(int operation, int reason) {
        return new UnsafeStateException(operation, reason);
    }
}
