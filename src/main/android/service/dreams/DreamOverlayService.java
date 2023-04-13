package android.service.dreams;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.service.dreams.DreamOverlayService;
import android.service.dreams.IDreamOverlay;
import android.service.dreams.IDreamOverlayClient;
import android.util.Log;
import android.view.WindowManager;
/* loaded from: classes3.dex */
public abstract class DreamOverlayService extends Service {
    private static final boolean DEBUG = false;
    private static final String TAG = "DreamOverlayService";
    private OverlayClient mCurrentClient;
    private IDreamOverlay mDreamOverlay = new IDreamOverlay.Stub() { // from class: android.service.dreams.DreamOverlayService.1
        @Override // android.service.dreams.IDreamOverlay
        public void getClient(IDreamOverlayClientCallback callback) {
            try {
                callback.onDreamOverlayClient(new OverlayClient(DreamOverlayService.this));
            } catch (RemoteException e) {
                Log.m109e(DreamOverlayService.TAG, "could not send client to callback", e);
            }
        }
    };

    public abstract void onStartDream(WindowManager.LayoutParams layoutParams);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class OverlayClient extends IDreamOverlayClient.Stub {
        private ComponentName mDreamComponent;
        IDreamOverlayCallback mDreamOverlayCallback;
        private final DreamOverlayService mService;
        private boolean mShowComplications;

        OverlayClient(DreamOverlayService service) {
            this.mService = service;
        }

        @Override // android.service.dreams.IDreamOverlayClient
        public void startDream(WindowManager.LayoutParams params, IDreamOverlayCallback callback, String dreamComponent, boolean shouldShowComplications) throws RemoteException {
            this.mDreamComponent = ComponentName.unflattenFromString(dreamComponent);
            this.mShowComplications = shouldShowComplications;
            this.mDreamOverlayCallback = callback;
            this.mService.startDream(this, params);
        }

        @Override // android.service.dreams.IDreamOverlayClient
        public void wakeUp() {
            this.mService.wakeUp(this, new Runnable() { // from class: android.service.dreams.DreamOverlayService$OverlayClient$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DreamOverlayService.OverlayClient.this.lambda$wakeUp$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$wakeUp$0() {
            try {
                this.mDreamOverlayCallback.onWakeUpComplete();
            } catch (RemoteException e) {
                Log.m109e(DreamOverlayService.TAG, "Could not notify dream of wakeUp", e);
            }
        }

        @Override // android.service.dreams.IDreamOverlayClient
        public void endDream() {
            this.mService.endDream(this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onExitRequested() {
            try {
                this.mDreamOverlayCallback.onExitRequested();
            } catch (RemoteException e) {
                Log.m110e(DreamOverlayService.TAG, "Could not request exit:" + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean shouldShowComplications() {
            return this.mShowComplications;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ComponentName getComponent() {
            return this.mDreamComponent;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDream(OverlayClient client, WindowManager.LayoutParams params) {
        endDream(this.mCurrentClient);
        this.mCurrentClient = client;
        onStartDream(params);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void endDream(OverlayClient client) {
        if (client == null || client != this.mCurrentClient) {
            return;
        }
        onEndDream();
        this.mCurrentClient = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void wakeUp(OverlayClient client, Runnable callback) {
        if (this.mCurrentClient != client) {
            return;
        }
        onWakeUp(callback);
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mDreamOverlay.asBinder();
    }

    public void onWakeUp(Runnable onCompleteCallback) {
        onCompleteCallback.run();
    }

    public void onEndDream() {
    }

    public final void requestExit() {
        OverlayClient overlayClient = this.mCurrentClient;
        if (overlayClient == null) {
            throw new IllegalStateException("requested exit with no dream present");
        }
        overlayClient.onExitRequested();
    }

    public final boolean shouldShowComplications() {
        OverlayClient overlayClient = this.mCurrentClient;
        if (overlayClient == null) {
            throw new IllegalStateException("requested if should show complication when no dream active");
        }
        return overlayClient.shouldShowComplications();
    }

    public final ComponentName getDreamComponent() {
        OverlayClient overlayClient = this.mCurrentClient;
        if (overlayClient == null) {
            throw new IllegalStateException("requested dream component when no dream active");
        }
        return overlayClient.getComponent();
    }
}
