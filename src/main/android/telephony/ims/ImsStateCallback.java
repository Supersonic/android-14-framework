package android.telephony.ims;

import android.p008os.Binder;
import android.telephony.ims.ImsStateCallback;
import com.android.internal.telephony.IImsStateCallback;
import com.android.internal.util.FunctionalUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public abstract class ImsStateCallback {
    public static final int REASON_IMS_SERVICE_DISCONNECTED = 3;
    public static final int REASON_IMS_SERVICE_NOT_READY = 6;
    public static final int REASON_NO_IMS_SERVICE_CONFIGURED = 4;
    public static final int REASON_SUBSCRIPTION_INACTIVE = 5;
    public static final int REASON_UNKNOWN_PERMANENT_ERROR = 2;
    public static final int REASON_UNKNOWN_TEMPORARY_ERROR = 1;
    private IImsStateCallbackStub mCallback;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DisconnectedReason {
    }

    public abstract void onAvailable();

    /* renamed from: onError */
    public abstract void lambda$binderDied$0();

    public abstract void onUnavailable(int i);

    public void init(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("ImsStateCallback Executor must be non-null");
        }
        this.mCallback = new IImsStateCallbackStub(this, executor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class IImsStateCallbackStub extends IImsStateCallback.Stub {
        private Executor mExecutor;
        private WeakReference<ImsStateCallback> mImsStateCallbackWeakRef;

        IImsStateCallbackStub(ImsStateCallback imsStateCallback, Executor executor) {
            this.mImsStateCallbackWeakRef = new WeakReference<>(imsStateCallback);
            this.mExecutor = executor;
        }

        Executor getExecutor() {
            return this.mExecutor;
        }

        @Override // com.android.internal.telephony.IImsStateCallback
        public void onAvailable() {
            final ImsStateCallback callback = this.mImsStateCallbackWeakRef.get();
            if (callback == null) {
                return;
            }
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.ims.ImsStateCallback$IImsStateCallbackStub$$ExternalSyntheticLambda2
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    ImsStateCallback.IImsStateCallbackStub.this.lambda$onAvailable$1(callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAvailable$1(final ImsStateCallback callback) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ImsStateCallback$IImsStateCallbackStub$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImsStateCallback.this.onAvailable();
                }
            });
        }

        @Override // com.android.internal.telephony.IImsStateCallback
        public void onUnavailable(final int reason) {
            final ImsStateCallback callback = this.mImsStateCallbackWeakRef.get();
            if (callback == null) {
                return;
            }
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.ims.ImsStateCallback$IImsStateCallbackStub$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    ImsStateCallback.IImsStateCallbackStub.this.lambda$onUnavailable$3(callback, reason);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onUnavailable$3(final ImsStateCallback callback, final int reason) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ImsStateCallback$IImsStateCallbackStub$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ImsStateCallback.this.onUnavailable(reason);
                }
            });
        }
    }

    public final void binderDied() {
        IImsStateCallbackStub iImsStateCallbackStub = this.mCallback;
        if (iImsStateCallbackStub != null) {
            iImsStateCallbackStub.getExecutor().execute(new Runnable() { // from class: android.telephony.ims.ImsStateCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImsStateCallback.this.lambda$binderDied$0();
                }
            });
        }
    }

    public IImsStateCallbackStub getCallbackBinder() {
        return this.mCallback;
    }
}
