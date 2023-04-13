package com.android.server.biometrics.sensors;

import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.server.backup.BackupAgentTimeoutParameters;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
/* loaded from: classes.dex */
public class BiometricSchedulerOperation {
    @VisibleForTesting
    final Runnable mCancelWatchdog;
    public final ClientMonitorCallback mClientCallback;
    public final BaseClientMonitor mClientMonitor;
    public final BooleanSupplier mIsDebuggable;
    public ClientMonitorCallback mOnStartCallback;
    public int mState;

    public BiometricSchedulerOperation(BaseClientMonitor baseClientMonitor, ClientMonitorCallback clientMonitorCallback) {
        this(baseClientMonitor, clientMonitorCallback, 0);
    }

    @VisibleForTesting
    public BiometricSchedulerOperation(BaseClientMonitor baseClientMonitor, ClientMonitorCallback clientMonitorCallback, BooleanSupplier booleanSupplier) {
        this(baseClientMonitor, clientMonitorCallback, 0, booleanSupplier);
    }

    public BiometricSchedulerOperation(BaseClientMonitor baseClientMonitor, ClientMonitorCallback clientMonitorCallback, int i) {
        this(baseClientMonitor, clientMonitorCallback, i, new BooleanSupplier() { // from class: com.android.server.biometrics.sensors.BiometricSchedulerOperation$$ExternalSyntheticLambda1
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return Build.isDebuggable();
            }
        });
    }

    public BiometricSchedulerOperation(BaseClientMonitor baseClientMonitor, ClientMonitorCallback clientMonitorCallback, int i, BooleanSupplier booleanSupplier) {
        this.mClientMonitor = baseClientMonitor;
        this.mClientCallback = clientMonitorCallback;
        this.mState = i;
        this.mIsDebuggable = booleanSupplier;
        this.mCancelWatchdog = new Runnable() { // from class: com.android.server.biometrics.sensors.BiometricSchedulerOperation$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BiometricSchedulerOperation.this.lambda$new$0();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (isFinished()) {
            return;
        }
        Slog.e("BiometricSchedulerOperation", "[Watchdog Triggered]: " + this);
        getWrappedCallback(this.mOnStartCallback).onClientFinished(this.mClientMonitor, false);
    }

    public int isReadyToStart(ClientMonitorCallback clientMonitorCallback) {
        int i = this.mState;
        if (i == 4 || i == 0) {
            int cookie = this.mClientMonitor.getCookie();
            if (cookie != 0) {
                this.mState = 4;
                this.mClientMonitor.waitForCookie(getWrappedCallback(clientMonitorCallback));
            }
            return cookie;
        }
        return 0;
    }

    public boolean start(ClientMonitorCallback clientMonitorCallback) {
        if (errorWhenNoneOf("start", 0, 4, 1)) {
            return false;
        }
        if (this.mClientMonitor.getCookie() != 0) {
            if (this.mIsDebuggable.getAsBoolean()) {
                throw new IllegalStateException("operation requires cookie");
            }
            Slog.e("BiometricSchedulerOperation", "operation requires cookie");
        }
        return doStart(clientMonitorCallback);
    }

    public boolean startWithCookie(ClientMonitorCallback clientMonitorCallback, int i) {
        if (this.mClientMonitor.getCookie() != i) {
            Slog.e("BiometricSchedulerOperation", "Mismatched cookie for operation: " + this + ", received: " + i);
            return false;
        } else if (errorWhenNoneOf("start", 0, 4, 1)) {
            return false;
        } else {
            return doStart(clientMonitorCallback);
        }
    }

    public final boolean doStart(ClientMonitorCallback clientMonitorCallback) {
        this.mOnStartCallback = clientMonitorCallback;
        ClientMonitorCallback wrappedCallback = getWrappedCallback(clientMonitorCallback);
        if (this.mState == 1) {
            Slog.d("BiometricSchedulerOperation", "Operation marked for cancellation, cancelling now: " + this);
            wrappedCallback.onClientFinished(this.mClientMonitor, true);
            BaseClientMonitor baseClientMonitor = this.mClientMonitor;
            if (baseClientMonitor instanceof ErrorConsumer) {
                ((ErrorConsumer) baseClientMonitor).onError(5, 0);
            } else {
                Slog.w("BiometricSchedulerOperation", "monitor cancelled but does not implement ErrorConsumer");
            }
            return false;
        } else if (isUnstartableHalOperation()) {
            Slog.v("BiometricSchedulerOperation", "unable to start: " + this);
            ((HalClientMonitor) this.mClientMonitor).unableToStart();
            wrappedCallback.onClientFinished(this.mClientMonitor, false);
            return false;
        } else {
            this.mState = 2;
            this.mClientMonitor.start(wrappedCallback);
            Slog.v("BiometricSchedulerOperation", "started: " + this);
            return true;
        }
    }

    public void abort() {
        if (errorWhenNoneOf("abort", 0, 4, 1)) {
            return;
        }
        if (isHalOperation()) {
            ((HalClientMonitor) this.mClientMonitor).unableToStart();
        }
        getWrappedCallback().onClientFinished(this.mClientMonitor, false);
        Slog.v("BiometricSchedulerOperation", "Aborted: " + this);
    }

    public boolean markCanceling() {
        if (this.mState == 0) {
            this.mState = 1;
            return true;
        }
        return false;
    }

    public void cancel(Handler handler, ClientMonitorCallback clientMonitorCallback) {
        if (errorWhenOneOf("cancel", 5)) {
            return;
        }
        int i = this.mState;
        if (i == 3) {
            Slog.w("BiometricSchedulerOperation", "Cannot cancel - already invoked for operation: " + this);
            return;
        }
        this.mState = 3;
        if (i == 0 || i == 1 || i == 4) {
            Slog.d("BiometricSchedulerOperation", "[Cancelling] Current client (without start): " + this.mClientMonitor);
            this.mClientMonitor.cancelWithoutStarting(getWrappedCallback(clientMonitorCallback));
        } else {
            Slog.d("BiometricSchedulerOperation", "[Cancelling] Current client: " + this.mClientMonitor);
            this.mClientMonitor.cancel();
        }
        handler.postDelayed(this.mCancelWatchdog, BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
    }

    public final ClientMonitorCallback getWrappedCallback() {
        return getWrappedCallback(null);
    }

    public final ClientMonitorCallback getWrappedCallback(ClientMonitorCallback clientMonitorCallback) {
        return new ClientMonitorCompositeCallback(new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.BiometricSchedulerOperation.1
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                Slog.d("BiometricSchedulerOperation", "[Finished / destroy]: " + baseClientMonitor);
                BiometricSchedulerOperation.this.mClientMonitor.destroy();
                BiometricSchedulerOperation.this.mState = 5;
            }
        }, clientMonitorCallback, this.mClientCallback);
    }

    public int getSensorId() {
        return this.mClientMonitor.getSensorId();
    }

    public int getProtoEnum() {
        return this.mClientMonitor.getProtoEnum();
    }

    public int getTargetUserId() {
        return this.mClientMonitor.getTargetUserId();
    }

    public boolean isFor(BaseClientMonitor baseClientMonitor) {
        return this.mClientMonitor == baseClientMonitor;
    }

    public boolean isInterruptable() {
        return this.mClientMonitor.isInterruptable();
    }

    public final boolean isHalOperation() {
        return this.mClientMonitor instanceof HalClientMonitor;
    }

    public final boolean isUnstartableHalOperation() {
        return isHalOperation() && ((HalClientMonitor) this.mClientMonitor).getFreshDaemon() == null;
    }

    public boolean isEnrollOperation() {
        return this.mClientMonitor instanceof EnrollClient;
    }

    public boolean isAuthenticationOrDetectionOperation() {
        BaseClientMonitor baseClientMonitor = this.mClientMonitor;
        return (baseClientMonitor instanceof AuthenticationConsumer) || (baseClientMonitor instanceof DetectionConsumer);
    }

    public boolean isAcquisitionOperation() {
        return this.mClientMonitor instanceof AcquisitionClient;
    }

    public boolean isMatchingRequestId(long j) {
        return !this.mClientMonitor.hasRequestId() || this.mClientMonitor.getRequestId() == j;
    }

    public boolean isMatchingToken(IBinder iBinder) {
        return this.mClientMonitor.getToken() == iBinder;
    }

    public boolean isStarted() {
        return this.mState == 2;
    }

    public boolean isFinished() {
        return this.mState == 5;
    }

    public boolean isMarkedCanceling() {
        return this.mState == 1;
    }

    @Deprecated
    public BaseClientMonitor getClientMonitor() {
        return this.mClientMonitor;
    }

    public final boolean errorWhenOneOf(String str, int... iArr) {
        boolean contains = ArrayUtils.contains(iArr, this.mState);
        if (contains) {
            String str2 = str + ": mState must not be " + this.mState;
            if (this.mIsDebuggable.getAsBoolean()) {
                throw new IllegalStateException(str2);
            }
            Slog.e("BiometricSchedulerOperation", str2);
        }
        return contains;
    }

    public final boolean errorWhenNoneOf(String str, int... iArr) {
        boolean z = !ArrayUtils.contains(iArr, this.mState);
        if (z) {
            String str2 = str + ": mState=" + this.mState + " must be one of " + Arrays.toString(iArr);
            if (this.mIsDebuggable.getAsBoolean()) {
                throw new IllegalStateException(str2);
            }
            Slog.e("BiometricSchedulerOperation", str2);
        }
        return z;
    }

    public String toString() {
        return this.mClientMonitor + ", State: " + this.mState;
    }
}
