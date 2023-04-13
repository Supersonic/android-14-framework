package android.p008os;

import android.app.ActivityThread;
import android.content.Context;
import android.util.Log;
/* renamed from: android.os.VibratorManager */
/* loaded from: classes3.dex */
public abstract class VibratorManager {
    private static final String TAG = "VibratorManager";
    private final String mPackageName;

    public abstract void cancel();

    public abstract void cancel(int i);

    public abstract Vibrator getDefaultVibrator();

    public abstract Vibrator getVibrator(int i);

    public abstract int[] getVibratorIds();

    public abstract void vibrate(int i, String str, CombinedVibration combinedVibration, String str2, VibrationAttributes vibrationAttributes);

    public VibratorManager() {
        this.mPackageName = ActivityThread.currentPackageName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VibratorManager(Context context) {
        this.mPackageName = context.getOpPackageName();
    }

    public boolean setAlwaysOnEffect(int uid, String opPkg, int alwaysOnId, CombinedVibration effect, VibrationAttributes attributes) {
        Log.m104w(TAG, "Always-on effects aren't supported");
        return false;
    }

    public final void vibrate(CombinedVibration effect) {
        vibrate(effect, null);
    }

    public final void vibrate(CombinedVibration effect, VibrationAttributes attributes) {
        vibrate(Process.myUid(), this.mPackageName, effect, null, attributes);
    }
}
