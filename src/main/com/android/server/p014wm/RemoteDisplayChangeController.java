package com.android.server.p014wm;

import android.os.RemoteException;
import android.util.Slog;
import android.view.IDisplayChangeWindowCallback;
import android.window.DisplayAreaInfo;
import android.window.WindowContainerTransaction;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.p014wm.RemoteDisplayChangeController;
import com.android.server.p014wm.WindowManagerService;
import java.util.ArrayList;
import java.util.List;
/* renamed from: com.android.server.wm.RemoteDisplayChangeController */
/* loaded from: classes2.dex */
public class RemoteDisplayChangeController {
    public final int mDisplayId;
    public final WindowManagerService mService;
    public final Runnable mTimeoutRunnable = new Runnable() { // from class: com.android.server.wm.RemoteDisplayChangeController$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            RemoteDisplayChangeController.this.onContinueTimedOut();
        }
    };
    public final List<ContinueRemoteDisplayChangeCallback> mCallbacks = new ArrayList();

    /* renamed from: com.android.server.wm.RemoteDisplayChangeController$ContinueRemoteDisplayChangeCallback */
    /* loaded from: classes2.dex */
    public interface ContinueRemoteDisplayChangeCallback {
        void onContinueRemoteDisplayChange(WindowContainerTransaction windowContainerTransaction);
    }

    public RemoteDisplayChangeController(WindowManagerService windowManagerService, int i) {
        this.mService = windowManagerService;
        this.mDisplayId = i;
    }

    public boolean isWaitingForRemoteDisplayChange() {
        return !this.mCallbacks.isEmpty();
    }

    public boolean performRemoteDisplayChange(int i, int i2, DisplayAreaInfo displayAreaInfo, ContinueRemoteDisplayChangeCallback continueRemoteDisplayChangeCallback) {
        if (this.mService.mDisplayChangeController == null) {
            return false;
        }
        this.mCallbacks.add(continueRemoteDisplayChangeCallback);
        if (displayAreaInfo != null && ProtoLogCache.WM_DEBUG_CONFIGURATION_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONFIGURATION, 1393721079, 85, (String) null, new Object[]{Long.valueOf(i), Long.valueOf(displayAreaInfo.configuration.windowConfiguration.getMaxBounds().width()), Long.valueOf(displayAreaInfo.configuration.windowConfiguration.getMaxBounds().height()), Long.valueOf(i2)});
        }
        IDisplayChangeWindowCallback createCallback = createCallback(continueRemoteDisplayChangeCallback);
        try {
            this.mService.f1164mH.removeCallbacks(this.mTimeoutRunnable);
            this.mService.f1164mH.postDelayed(this.mTimeoutRunnable, 800L);
            this.mService.mDisplayChangeController.onDisplayChange(this.mDisplayId, i, i2, displayAreaInfo, createCallback);
            return true;
        } catch (RemoteException e) {
            Slog.e("RemoteDisplayChangeController", "Exception while dispatching remote display-change", e);
            this.mCallbacks.remove(continueRemoteDisplayChangeCallback);
            return false;
        }
    }

    public final void onContinueTimedOut() {
        Slog.e("RemoteDisplayChangeController", "RemoteDisplayChange timed-out, UI might get messed-up after this.");
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (int i = 0; i < this.mCallbacks.size(); i++) {
                    this.mCallbacks.get(i).onContinueRemoteDisplayChange(null);
                }
                this.mCallbacks.clear();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public final void continueDisplayChange(ContinueRemoteDisplayChangeCallback continueRemoteDisplayChangeCallback, WindowContainerTransaction windowContainerTransaction) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                int indexOf = this.mCallbacks.indexOf(continueRemoteDisplayChangeCallback);
                if (indexOf < 0) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                for (int i = 0; i < indexOf; i++) {
                    this.mCallbacks.get(i).onContinueRemoteDisplayChange(null);
                }
                this.mCallbacks.subList(0, indexOf + 1).clear();
                if (this.mCallbacks.isEmpty()) {
                    this.mService.f1164mH.removeCallbacks(this.mTimeoutRunnable);
                }
                continueRemoteDisplayChangeCallback.onContinueRemoteDisplayChange(windowContainerTransaction);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* renamed from: com.android.server.wm.RemoteDisplayChangeController$1 */
    /* loaded from: classes2.dex */
    public class C18871 extends IDisplayChangeWindowCallback.Stub {
        public final /* synthetic */ ContinueRemoteDisplayChangeCallback val$callback;

        public C18871(ContinueRemoteDisplayChangeCallback continueRemoteDisplayChangeCallback) {
            this.val$callback = continueRemoteDisplayChangeCallback;
        }

        public void continueDisplayChange(final WindowContainerTransaction windowContainerTransaction) {
            synchronized (RemoteDisplayChangeController.this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (!RemoteDisplayChangeController.this.mCallbacks.contains(this.val$callback)) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    WindowManagerService.HandlerC1915H handlerC1915H = RemoteDisplayChangeController.this.mService.f1164mH;
                    final ContinueRemoteDisplayChangeCallback continueRemoteDisplayChangeCallback = this.val$callback;
                    handlerC1915H.post(new Runnable() { // from class: com.android.server.wm.RemoteDisplayChangeController$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            RemoteDisplayChangeController.C18871.this.lambda$continueDisplayChange$0(continueRemoteDisplayChangeCallback, windowContainerTransaction);
                        }
                    });
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$continueDisplayChange$0(ContinueRemoteDisplayChangeCallback continueRemoteDisplayChangeCallback, WindowContainerTransaction windowContainerTransaction) {
            RemoteDisplayChangeController.this.continueDisplayChange(continueRemoteDisplayChangeCallback, windowContainerTransaction);
        }
    }

    public final IDisplayChangeWindowCallback createCallback(ContinueRemoteDisplayChangeCallback continueRemoteDisplayChangeCallback) {
        return new C18871(continueRemoteDisplayChangeCallback);
    }
}
