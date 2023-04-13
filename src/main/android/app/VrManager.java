package android.app;

import android.annotation.SystemApi;
import android.app.VrManager;
import android.content.ComponentName;
import android.p008os.RemoteException;
import android.service.p011vr.IPersistentVrStateCallbacks;
import android.service.p011vr.IVrManager;
import android.service.p011vr.IVrStateCallbacks;
import android.util.ArrayMap;
import java.util.Map;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes.dex */
public class VrManager {
    private Map<VrStateCallback, CallbackEntry> mCallbackMap = new ArrayMap();
    private final IVrManager mService;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CallbackEntry {
        final VrStateCallback mCallback;
        final Executor mExecutor;
        final IVrStateCallbacks mStateCallback = new BinderC03681();
        final IPersistentVrStateCallbacks mPersistentStateCallback = new BinderC03692();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: android.app.VrManager$CallbackEntry$1 */
        /* loaded from: classes.dex */
        public class BinderC03681 extends IVrStateCallbacks.Stub {
            BinderC03681() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onVrStateChanged$0(boolean enabled) {
                CallbackEntry.this.mCallback.onVrStateChanged(enabled);
            }

            @Override // android.service.p011vr.IVrStateCallbacks
            public void onVrStateChanged(final boolean enabled) {
                CallbackEntry.this.mExecutor.execute(new Runnable() { // from class: android.app.VrManager$CallbackEntry$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        VrManager.CallbackEntry.BinderC03681.this.lambda$onVrStateChanged$0(enabled);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: android.app.VrManager$CallbackEntry$2 */
        /* loaded from: classes.dex */
        public class BinderC03692 extends IPersistentVrStateCallbacks.Stub {
            BinderC03692() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onPersistentVrStateChanged$0(boolean enabled) {
                CallbackEntry.this.mCallback.onPersistentVrStateChanged(enabled);
            }

            @Override // android.service.p011vr.IPersistentVrStateCallbacks
            public void onPersistentVrStateChanged(final boolean enabled) {
                CallbackEntry.this.mExecutor.execute(new Runnable() { // from class: android.app.VrManager$CallbackEntry$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        VrManager.CallbackEntry.BinderC03692.this.lambda$onPersistentVrStateChanged$0(enabled);
                    }
                });
            }
        }

        CallbackEntry(VrStateCallback callback, Executor executor) {
            this.mCallback = callback;
            this.mExecutor = executor;
        }
    }

    public VrManager(IVrManager service) {
        this.mService = service;
    }

    public void registerVrStateCallback(Executor executor, VrStateCallback callback) {
        if (callback == null || this.mCallbackMap.containsKey(callback)) {
            return;
        }
        CallbackEntry entry = new CallbackEntry(callback, executor);
        this.mCallbackMap.put(callback, entry);
        try {
            this.mService.registerListener(entry.mStateCallback);
            this.mService.registerPersistentVrStateListener(entry.mPersistentStateCallback);
        } catch (RemoteException e) {
            try {
                unregisterVrStateCallback(callback);
            } catch (Exception e2) {
                e.rethrowFromSystemServer();
            }
        }
    }

    public void unregisterVrStateCallback(VrStateCallback callback) {
        CallbackEntry entry = this.mCallbackMap.remove(callback);
        if (entry != null) {
            try {
                this.mService.unregisterListener(entry.mStateCallback);
            } catch (RemoteException e) {
            }
            try {
                this.mService.unregisterPersistentVrStateListener(entry.mPersistentStateCallback);
            } catch (RemoteException e2) {
            }
        }
    }

    public boolean isVrModeEnabled() {
        try {
            return this.mService.getVrModeState();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean isPersistentVrModeEnabled() {
        try {
            return this.mService.getPersistentVrModeEnabled();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public void setPersistentVrModeEnabled(boolean enabled) {
        try {
            this.mService.setPersistentVrModeEnabled(enabled);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setVr2dDisplayProperties(Vr2dDisplayProperties vr2dDisplayProp) {
        try {
            this.mService.setVr2dDisplayProperties(vr2dDisplayProp);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setAndBindVrCompositor(ComponentName componentName) {
        try {
            this.mService.setAndBindCompositor(componentName == null ? null : componentName.flattenToString());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setStandbyEnabled(boolean standby) {
        try {
            this.mService.setStandbyEnabled(standby);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setVrInputMethod(ComponentName componentName) {
    }

    public int getVr2dDisplayId() {
        try {
            return this.mService.getVr2dDisplayId();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return -1;
        }
    }
}
