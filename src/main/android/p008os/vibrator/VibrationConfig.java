package android.p008os.vibrator;

import android.content.res.Resources;
import com.android.internal.C4057R;
/* renamed from: android.os.vibrator.VibrationConfig */
/* loaded from: classes3.dex */
public class VibrationConfig {
    private final int mDefaultAlarmVibrationIntensity;
    private final int mDefaultHapticFeedbackIntensity;
    private final int mDefaultMediaVibrationIntensity;
    private final int mDefaultNotificationVibrationIntensity;
    private final int mDefaultRingVibrationIntensity;
    private final float mHapticChannelMaxVibrationAmplitude;
    private final int mRampDownDurationMs;
    private final int mRampStepDurationMs;

    public VibrationConfig(Resources resources) {
        this.mHapticChannelMaxVibrationAmplitude = loadFloat(resources, C4057R.dimen.config_hapticChannelMaxVibrationAmplitude, 0.0f);
        this.mRampDownDurationMs = loadInteger(resources, C4057R.integer.config_vibrationWaveformRampDownDuration, 0);
        this.mRampStepDurationMs = loadInteger(resources, C4057R.integer.config_vibrationWaveformRampStepDuration, 0);
        this.mDefaultAlarmVibrationIntensity = loadDefaultIntensity(resources, C4057R.integer.config_defaultAlarmVibrationIntensity);
        this.mDefaultHapticFeedbackIntensity = loadDefaultIntensity(resources, C4057R.integer.config_defaultHapticFeedbackIntensity);
        this.mDefaultMediaVibrationIntensity = loadDefaultIntensity(resources, C4057R.integer.config_defaultMediaVibrationIntensity);
        this.mDefaultNotificationVibrationIntensity = loadDefaultIntensity(resources, C4057R.integer.config_defaultNotificationVibrationIntensity);
        this.mDefaultRingVibrationIntensity = loadDefaultIntensity(resources, C4057R.integer.config_defaultRingVibrationIntensity);
    }

    private static int loadDefaultIntensity(Resources res, int resId) {
        int value = loadInteger(res, resId, 2);
        if (value < 0 || value > 3) {
            return 2;
        }
        return value;
    }

    private static float loadFloat(Resources res, int resId, float defaultValue) {
        return res != null ? res.getFloat(resId) : defaultValue;
    }

    private static int loadInteger(Resources res, int resId, int defaultValue) {
        return res != null ? res.getInteger(resId) : defaultValue;
    }

    public float getHapticChannelMaximumAmplitude() {
        float f = this.mHapticChannelMaxVibrationAmplitude;
        if (f <= 0.0f) {
            return Float.NaN;
        }
        return f;
    }

    public int getRampDownDurationMs() {
        int i = this.mRampDownDurationMs;
        if (i < 0) {
            return 0;
        }
        return i;
    }

    public int getRampStepDurationMs() {
        int i = this.mRampStepDurationMs;
        if (i < 0) {
            return 0;
        }
        return i;
    }

    public int getDefaultVibrationIntensity(int usage) {
        switch (usage) {
            case 17:
                return this.mDefaultAlarmVibrationIntensity;
            case 18:
            case 34:
            case 50:
            case 66:
                return this.mDefaultHapticFeedbackIntensity;
            case 33:
                return this.mDefaultRingVibrationIntensity;
            case 49:
            case 65:
                return this.mDefaultNotificationVibrationIntensity;
            default:
                return this.mDefaultMediaVibrationIntensity;
        }
    }

    public String toString() {
        return "VibrationConfig{mHapticChannelMaxVibrationAmplitude=" + this.mHapticChannelMaxVibrationAmplitude + ", mRampStepDurationMs=" + this.mRampStepDurationMs + ", mRampDownDurationMs=" + this.mRampDownDurationMs + ", mDefaultAlarmIntensity=" + this.mDefaultAlarmVibrationIntensity + ", mDefaultHapticFeedbackIntensity=" + this.mDefaultHapticFeedbackIntensity + ", mDefaultMediaIntensity=" + this.mDefaultMediaVibrationIntensity + ", mDefaultNotificationIntensity=" + this.mDefaultNotificationVibrationIntensity + ", mDefaultRingIntensity=" + this.mDefaultRingVibrationIntensity + "}";
    }
}
