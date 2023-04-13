package com.android.server.biometrics.sensors;

import android.hardware.biometrics.IBiometricService;
import android.os.Handler;
import android.os.Looper;
import android.os.ServiceManager;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.biometrics.sensors.UserAwareBiometricScheduler;
import com.android.server.biometrics.sensors.fingerprint.GestureAvailabilityDispatcher;
/* loaded from: classes.dex */
public class UserAwareBiometricScheduler extends BiometricScheduler {
    public final CurrentUserRetriever mCurrentUserRetriever;
    public StopUserClient<?> mStopUserClient;
    public final UserSwitchCallback mUserSwitchCallback;

    /* loaded from: classes.dex */
    public interface CurrentUserRetriever {
        int getCurrentUserId();
    }

    /* loaded from: classes.dex */
    public interface UserSwitchCallback {
        StartUserClient<?, ?> getStartUserClient(int i);

        StopUserClient<?> getStopUserClient(int i);
    }

    /* loaded from: classes.dex */
    public class ClientFinishedCallback implements ClientMonitorCallback {
        public final BaseClientMonitor mOwner;

        public ClientFinishedCallback(BaseClientMonitor baseClientMonitor) {
            this.mOwner = baseClientMonitor;
        }

        @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
        public void onClientFinished(final BaseClientMonitor baseClientMonitor, final boolean z) {
            UserAwareBiometricScheduler.this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.UserAwareBiometricScheduler$ClientFinishedCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UserAwareBiometricScheduler.ClientFinishedCallback.this.lambda$onClientFinished$0(baseClientMonitor, z);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onClientFinished$0(BaseClientMonitor baseClientMonitor, boolean z) {
            String tag = UserAwareBiometricScheduler.this.getTag();
            Slog.d(tag, "[Client finished] " + baseClientMonitor + ", success: " + z);
            if ((baseClientMonitor instanceof StopUserClient) && !z) {
                Slog.w(UserAwareBiometricScheduler.this.getTag(), "StopUserClient failed(), is the HAL stuck? Clearing mStopUserClient");
                UserAwareBiometricScheduler.this.mStopUserClient = null;
            }
            BiometricSchedulerOperation biometricSchedulerOperation = UserAwareBiometricScheduler.this.mCurrentOperation;
            if (biometricSchedulerOperation != null && biometricSchedulerOperation.isFor(this.mOwner)) {
                UserAwareBiometricScheduler.this.mCurrentOperation = null;
            } else {
                String tag2 = UserAwareBiometricScheduler.this.getTag();
                Slog.w(tag2, "operation is already null or different (reset?): " + UserAwareBiometricScheduler.this.mCurrentOperation);
            }
            UserAwareBiometricScheduler.this.startNextOperationIfIdle();
        }
    }

    @VisibleForTesting
    public UserAwareBiometricScheduler(String str, Handler handler, int i, GestureAvailabilityDispatcher gestureAvailabilityDispatcher, IBiometricService iBiometricService, CurrentUserRetriever currentUserRetriever, UserSwitchCallback userSwitchCallback) {
        super(str, handler, i, gestureAvailabilityDispatcher, iBiometricService, 50);
        this.mCurrentUserRetriever = currentUserRetriever;
        this.mUserSwitchCallback = userSwitchCallback;
    }

    public UserAwareBiometricScheduler(String str, int i, GestureAvailabilityDispatcher gestureAvailabilityDispatcher, CurrentUserRetriever currentUserRetriever, UserSwitchCallback userSwitchCallback) {
        this(str, new Handler(Looper.getMainLooper()), i, gestureAvailabilityDispatcher, IBiometricService.Stub.asInterface(ServiceManager.getService("biometric")), currentUserRetriever, userSwitchCallback);
    }

    @Override // com.android.server.biometrics.sensors.BiometricScheduler
    public String getTag() {
        return "UaBiometricScheduler/" + this.mBiometricTag;
    }

    @Override // com.android.server.biometrics.sensors.BiometricScheduler
    public void startNextOperationIfIdle() {
        if (this.mCurrentOperation != null) {
            String tag = getTag();
            Slog.v(tag, "Not idle, current operation: " + this.mCurrentOperation);
        } else if (this.mPendingOperations.isEmpty()) {
            Slog.d(getTag(), "No operations, returning to idle");
        } else {
            int currentUserId = this.mCurrentUserRetriever.getCurrentUserId();
            int targetUserId = this.mPendingOperations.getFirst().getTargetUserId();
            if (targetUserId == currentUserId) {
                super.startNextOperationIfIdle();
            } else if (currentUserId == -10000) {
                StartUserClient<?, ?> startUserClient = this.mUserSwitchCallback.getStartUserClient(targetUserId);
                ClientFinishedCallback clientFinishedCallback = new ClientFinishedCallback(startUserClient);
                String tag2 = getTag();
                Slog.d(tag2, "[Starting User] " + startUserClient);
                this.mCurrentOperation = new BiometricSchedulerOperation(startUserClient, clientFinishedCallback, 2);
                startUserClient.start(clientFinishedCallback);
            } else if (this.mStopUserClient != null) {
                String tag3 = getTag();
                Slog.d(tag3, "[Waiting for StopUser] " + this.mStopUserClient);
            } else {
                StopUserClient<?> stopUserClient = this.mUserSwitchCallback.getStopUserClient(currentUserId);
                this.mStopUserClient = stopUserClient;
                ClientFinishedCallback clientFinishedCallback2 = new ClientFinishedCallback(stopUserClient);
                String tag4 = getTag();
                Slog.d(tag4, "[Stopping User] current: " + currentUserId + ", next: " + targetUserId + ". " + this.mStopUserClient);
                this.mCurrentOperation = new BiometricSchedulerOperation(this.mStopUserClient, clientFinishedCallback2, 2);
                this.mStopUserClient.start(clientFinishedCallback2);
            }
        }
    }

    public void onUserStopped() {
        if (this.mStopUserClient == null) {
            Slog.e(getTag(), "Unexpected onUserStopped");
            return;
        }
        String tag = getTag();
        Slog.d(tag, "[OnUserStopped]: " + this.mStopUserClient);
        this.mStopUserClient.onUserStopped();
        this.mStopUserClient = null;
    }

    @VisibleForTesting
    public StopUserClient<?> getStopUserClient() {
        return this.mStopUserClient;
    }
}
