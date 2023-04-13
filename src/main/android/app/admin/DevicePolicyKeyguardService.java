package android.app.admin;

import android.annotation.SystemApi;
import android.app.Service;
import android.app.admin.DevicePolicyKeyguardService;
import android.app.admin.IKeyguardClient;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.util.Log;
import android.view.SurfaceControlViewHost;
@SystemApi
/* loaded from: classes.dex */
public class DevicePolicyKeyguardService extends Service {
    private static final String TAG = "DevicePolicyKeyguardService";
    private IKeyguardCallback mCallback;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final IKeyguardClient mClient = new adminIKeyguardClient$StubC03831();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.app.admin.DevicePolicyKeyguardService$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class adminIKeyguardClient$StubC03831 extends IKeyguardClient.Stub {
        adminIKeyguardClient$StubC03831() {
        }

        @Override // android.app.admin.IKeyguardClient
        public void onCreateKeyguardSurface(final IBinder hostInputToken, IKeyguardCallback callback) {
            DevicePolicyKeyguardService.this.mCallback = callback;
            DevicePolicyKeyguardService.this.mHandler.post(new Runnable() { // from class: android.app.admin.DevicePolicyKeyguardService$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DevicePolicyKeyguardService.adminIKeyguardClient$StubC03831.this.lambda$onCreateKeyguardSurface$0(hostInputToken);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateKeyguardSurface$0(IBinder hostInputToken) {
            SurfaceControlViewHost.SurfacePackage surfacePackage = DevicePolicyKeyguardService.this.onCreateKeyguardSurface(hostInputToken);
            try {
                DevicePolicyKeyguardService.this.mCallback.onRemoteContentReady(surfacePackage);
            } catch (RemoteException e) {
                Log.m109e(DevicePolicyKeyguardService.TAG, "Failed to return created SurfacePackage", e);
            }
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mHandler.removeCallbacksAndMessages(null);
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mClient.asBinder();
    }

    public SurfaceControlViewHost.SurfacePackage onCreateKeyguardSurface(IBinder hostInputToken) {
        return null;
    }

    public void dismiss() {
        IKeyguardCallback iKeyguardCallback = this.mCallback;
        if (iKeyguardCallback == null) {
            Log.m104w(TAG, "KeyguardCallback was unexpectedly null");
            return;
        }
        try {
            iKeyguardCallback.onDismiss();
        } catch (RemoteException e) {
            Log.m109e(TAG, "onDismiss failed", e);
        }
    }
}
