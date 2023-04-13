package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Binder;
import android.telephony.ims.SipDialogStateCallback;
import com.android.internal.telephony.ISipDialogStateCallback;
import com.android.internal.util.FunctionalUtils;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes3.dex */
public abstract class SipDialogStateCallback {
    private CallbackBinder mCallback;

    public abstract void onActiveSipDialogsChanged(List<SipDialogState> list);

    /* renamed from: onError */
    public abstract void lambda$binderDied$0();

    public void attachExecutor(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("SipDialogStateCallback Executor must be non-null");
        }
        this.mCallback = new CallbackBinder(executor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class CallbackBinder extends ISipDialogStateCallback.Stub {
        private Executor mExecutor;
        private WeakReference<SipDialogStateCallback> mSipDialogStateCallbackWeakRef;

        private CallbackBinder(SipDialogStateCallback callback, Executor executor) {
            this.mSipDialogStateCallbackWeakRef = new WeakReference<>(callback);
            this.mExecutor = executor;
        }

        Executor getExecutor() {
            return this.mExecutor;
        }

        @Override // com.android.internal.telephony.ISipDialogStateCallback
        public void onActiveSipDialogsChanged(final List<SipDialogState> dialogs) {
            final SipDialogStateCallback callback = this.mSipDialogStateCallbackWeakRef.get();
            if (callback == null || dialogs == null) {
                return;
            }
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.ims.SipDialogStateCallback$CallbackBinder$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    SipDialogStateCallback.CallbackBinder.this.lambda$onActiveSipDialogsChanged$1(callback, dialogs);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onActiveSipDialogsChanged$1(final SipDialogStateCallback callback, final List dialogs) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.SipDialogStateCallback$CallbackBinder$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SipDialogStateCallback.this.onActiveSipDialogsChanged(dialogs);
                }
            });
        }
    }

    public final void binderDied() {
        CallbackBinder callbackBinder = this.mCallback;
        if (callbackBinder != null) {
            callbackBinder.getExecutor().execute(new Runnable() { // from class: android.telephony.ims.SipDialogStateCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SipDialogStateCallback.this.lambda$binderDied$0();
                }
            });
        }
    }

    public CallbackBinder getCallbackBinder() {
        return this.mCallback;
    }
}
