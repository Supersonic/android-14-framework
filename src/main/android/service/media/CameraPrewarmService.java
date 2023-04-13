package android.service.media;

import android.app.Service;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Message;
import android.p008os.Messenger;
/* loaded from: classes3.dex */
public abstract class CameraPrewarmService extends Service {
    public static final String ACTION_PREWARM = "android.service.media.CameraPrewarmService.ACTION_PREWARM";
    public static final int MSG_CAMERA_FIRED = 1;
    private boolean mCameraIntentFired;
    private final Handler mHandler = new Handler() { // from class: android.service.media.CameraPrewarmService.1
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    CameraPrewarmService.this.mCameraIntentFired = true;
                    return;
                default:
                    super.handleMessage(msg);
                    return;
            }
        }
    };

    public abstract void onCooldown(boolean z);

    public abstract void onPrewarm();

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (ACTION_PREWARM.equals(intent.getAction())) {
            onPrewarm();
            return new Messenger(this.mHandler).getBinder();
        }
        return null;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        if (ACTION_PREWARM.equals(intent.getAction())) {
            onCooldown(this.mCameraIntentFired);
            return false;
        }
        return false;
    }
}
