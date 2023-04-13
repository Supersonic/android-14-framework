package android.p008os;
/* renamed from: android.os.NullVibrator */
/* loaded from: classes3.dex */
public class NullVibrator extends Vibrator {
    private static final NullVibrator sInstance = new NullVibrator();

    private NullVibrator() {
    }

    public static NullVibrator getInstance() {
        return sInstance;
    }

    @Override // android.p008os.Vibrator
    public boolean hasVibrator() {
        return false;
    }

    @Override // android.p008os.Vibrator
    public boolean isVibrating() {
        return false;
    }

    @Override // android.p008os.Vibrator
    public boolean hasAmplitudeControl() {
        return false;
    }

    @Override // android.p008os.Vibrator
    public void vibrate(int uid, String opPkg, VibrationEffect effect, String reason, VibrationAttributes attributes) {
    }

    @Override // android.p008os.Vibrator
    public void cancel() {
    }

    @Override // android.p008os.Vibrator
    public void cancel(int usageFilter) {
    }
}
