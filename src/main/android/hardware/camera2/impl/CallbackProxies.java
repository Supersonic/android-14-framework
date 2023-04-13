package android.hardware.camera2.impl;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.impl.CallbackProxies;
import android.p008os.Binder;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class CallbackProxies {

    /* loaded from: classes.dex */
    public static class SessionStateCallbackProxy extends CameraCaptureSession.StateCallback {
        private final CameraCaptureSession.StateCallback mCallback;
        private final Executor mExecutor;

        public SessionStateCallbackProxy(Executor executor, CameraCaptureSession.StateCallback callback) {
            this.mExecutor = (Executor) Preconditions.checkNotNull(executor, "executor must not be null");
            this.mCallback = (CameraCaptureSession.StateCallback) Preconditions.checkNotNull(callback, "callback must not be null");
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigured(final CameraCaptureSession session) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CallbackProxies$SessionStateCallbackProxy$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        CallbackProxies.SessionStateCallbackProxy.this.lambda$onConfigured$0(session);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onConfigured$0(CameraCaptureSession session) {
            this.mCallback.onConfigured(session);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigureFailed(final CameraCaptureSession session) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CallbackProxies$SessionStateCallbackProxy$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        CallbackProxies.SessionStateCallbackProxy.this.lambda$onConfigureFailed$1(session);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onConfigureFailed$1(CameraCaptureSession session) {
            this.mCallback.onConfigureFailed(session);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onReady(final CameraCaptureSession session) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CallbackProxies$SessionStateCallbackProxy$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        CallbackProxies.SessionStateCallbackProxy.this.lambda$onReady$2(session);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReady$2(CameraCaptureSession session) {
            this.mCallback.onReady(session);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onActive(final CameraCaptureSession session) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CallbackProxies$SessionStateCallbackProxy$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        CallbackProxies.SessionStateCallbackProxy.this.lambda$onActive$3(session);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onActive$3(CameraCaptureSession session) {
            this.mCallback.onActive(session);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onCaptureQueueEmpty(final CameraCaptureSession session) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CallbackProxies$SessionStateCallbackProxy$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        CallbackProxies.SessionStateCallbackProxy.this.lambda$onCaptureQueueEmpty$4(session);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureQueueEmpty$4(CameraCaptureSession session) {
            this.mCallback.onCaptureQueueEmpty(session);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onClosed(final CameraCaptureSession session) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CallbackProxies$SessionStateCallbackProxy$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        CallbackProxies.SessionStateCallbackProxy.this.lambda$onClosed$5(session);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onClosed$5(CameraCaptureSession session) {
            this.mCallback.onClosed(session);
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onSurfacePrepared(final CameraCaptureSession session, final Surface surface) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CallbackProxies$SessionStateCallbackProxy$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        CallbackProxies.SessionStateCallbackProxy.this.lambda$onSurfacePrepared$6(session, surface);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSurfacePrepared$6(CameraCaptureSession session, Surface surface) {
            this.mCallback.onSurfacePrepared(session, surface);
        }
    }

    private CallbackProxies() {
        throw new AssertionError();
    }
}
