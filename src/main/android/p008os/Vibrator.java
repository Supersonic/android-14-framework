package android.p008os;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.content.Context;
import android.content.res.Resources;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.AudioAttributes;
import android.p008os.VibrationAttributes;
import android.p008os.VibratorInfo;
import android.p008os.vibrator.VibrationConfig;
import android.p008os.vibrator.VibratorFrequencyProfile;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
/* renamed from: android.os.Vibrator */
/* loaded from: classes3.dex */
public abstract class Vibrator {
    private static final String TAG = "Vibrator";
    public static final int VIBRATION_EFFECT_SUPPORT_NO = 2;
    public static final int VIBRATION_EFFECT_SUPPORT_UNKNOWN = 0;
    public static final int VIBRATION_EFFECT_SUPPORT_YES = 1;
    public static final int VIBRATION_INTENSITY_HIGH = 3;
    public static final int VIBRATION_INTENSITY_LOW = 1;
    public static final int VIBRATION_INTENSITY_MEDIUM = 2;
    public static final int VIBRATION_INTENSITY_OFF = 0;
    private final String mPackageName;
    private final Resources mResources;
    private volatile VibrationConfig mVibrationConfig;

    @SystemApi
    /* renamed from: android.os.Vibrator$OnVibratorStateChangedListener */
    /* loaded from: classes3.dex */
    public interface OnVibratorStateChangedListener {
        void onVibratorStateChanged(boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.Vibrator$VibrationEffectSupport */
    /* loaded from: classes3.dex */
    public @interface VibrationEffectSupport {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.Vibrator$VibrationIntensity */
    /* loaded from: classes3.dex */
    public @interface VibrationIntensity {
    }

    public abstract void cancel();

    public abstract void cancel(int i);

    public abstract boolean hasAmplitudeControl();

    public abstract boolean hasVibrator();

    public abstract void vibrate(int i, String str, VibrationEffect vibrationEffect, String str2, VibrationAttributes vibrationAttributes);

    public Vibrator() {
        this.mPackageName = ActivityThread.currentPackageName();
        this.mResources = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Vibrator(Context context) {
        this.mPackageName = context.getOpPackageName();
        this.mResources = context.getResources();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VibratorInfo getInfo() {
        return VibratorInfo.EMPTY_VIBRATOR_INFO;
    }

    private VibrationConfig getConfig() {
        if (this.mVibrationConfig == null) {
            Resources resources = this.mResources;
            if (resources == null) {
                Context ctx = ActivityThread.currentActivityThread().getSystemContext();
                resources = ctx != null ? ctx.getResources() : null;
            }
            this.mVibrationConfig = new VibrationConfig(resources);
        }
        return this.mVibrationConfig;
    }

    public int getDefaultVibrationIntensity(int usage) {
        return getConfig().getDefaultVibrationIntensity(usage);
    }

    public int getId() {
        return getInfo().getId();
    }

    public boolean hasFrequencyControl() {
        return getInfo().hasCapability(1536L);
    }

    public boolean areVibrationFeaturesSupported(VibrationEffect effect) {
        return effect.areVibrationFeaturesSupported(this);
    }

    public boolean hasExternalControl() {
        return getInfo().hasCapability(8L);
    }

    public float getResonantFrequency() {
        return getInfo().getResonantFrequencyHz();
    }

    public float getQFactor() {
        return getInfo().getQFactor();
    }

    public VibratorFrequencyProfile getFrequencyProfile() {
        VibratorInfo.FrequencyProfile frequencyProfile = getInfo().getFrequencyProfile();
        if (frequencyProfile.isEmpty()) {
            return null;
        }
        return new VibratorFrequencyProfile(frequencyProfile);
    }

    public float getHapticChannelMaximumAmplitude() {
        return getConfig().getHapticChannelMaximumAmplitude();
    }

    public boolean setAlwaysOnEffect(int alwaysOnId, VibrationEffect effect, VibrationAttributes attributes) {
        return setAlwaysOnEffect(Process.myUid(), this.mPackageName, alwaysOnId, effect, attributes);
    }

    public boolean setAlwaysOnEffect(int uid, String opPkg, int alwaysOnId, VibrationEffect effect, VibrationAttributes attributes) {
        Log.m104w(TAG, "Always-on effects aren't supported");
        return false;
    }

    @Deprecated
    public void vibrate(long milliseconds) {
        vibrate(milliseconds, (AudioAttributes) null);
    }

    @Deprecated
    public void vibrate(long milliseconds, AudioAttributes attributes) {
        try {
            VibrationEffect effect = VibrationEffect.createOneShot(milliseconds, -1);
            vibrate(effect, attributes);
        } catch (IllegalArgumentException iae) {
            Log.m109e(TAG, "Failed to create VibrationEffect", iae);
        }
    }

    @Deprecated
    public void vibrate(long[] pattern, int repeat) {
        vibrate(pattern, repeat, null);
    }

    @Deprecated
    public void vibrate(long[] pattern, int repeat, AudioAttributes attributes) {
        if (repeat < -1 || repeat >= pattern.length) {
            Log.m110e(TAG, "vibrate called with repeat index out of bounds (pattern.length=" + pattern.length + ", index=" + repeat + NavigationBarInflaterView.KEY_CODE_END);
            throw new ArrayIndexOutOfBoundsException();
        }
        try {
            vibrate(VibrationEffect.createWaveform(pattern, repeat), attributes);
        } catch (IllegalArgumentException iae) {
            Log.m109e(TAG, "Failed to create VibrationEffect", iae);
        }
    }

    public void vibrate(VibrationEffect vibe) {
        vibrate(vibe, new VibrationAttributes.Builder().build());
    }

    public void vibrate(VibrationEffect vibe, AudioAttributes attributes) {
        VibrationAttributes build;
        if (attributes == null) {
            build = new VibrationAttributes.Builder().build();
        } else {
            build = new VibrationAttributes.Builder(attributes).build();
        }
        vibrate(vibe, build);
    }

    public void vibrate(VibrationEffect vibe, VibrationAttributes attributes) {
        vibrate(Process.myUid(), this.mPackageName, vibe, null, attributes);
    }

    public int[] areEffectsSupported(int... effectIds) {
        VibratorInfo info = getInfo();
        int[] supported = new int[effectIds.length];
        for (int i = 0; i < effectIds.length; i++) {
            supported[i] = info.isEffectSupported(effectIds[i]);
        }
        return supported;
    }

    public final int areAllEffectsSupported(int... effectIds) {
        VibratorInfo info = getInfo();
        int allSupported = 1;
        for (int effectId : effectIds) {
            switch (info.isEffectSupported(effectId)) {
                case 1:
                    break;
                case 2:
                    return 2;
                default:
                    allSupported = 0;
                    break;
            }
        }
        return allSupported;
    }

    public boolean[] arePrimitivesSupported(int... primitiveIds) {
        VibratorInfo info = getInfo();
        boolean[] supported = new boolean[primitiveIds.length];
        for (int i = 0; i < primitiveIds.length; i++) {
            supported[i] = info.isPrimitiveSupported(primitiveIds[i]);
        }
        return supported;
    }

    public final boolean areAllPrimitivesSupported(int... primitiveIds) {
        VibratorInfo info = getInfo();
        for (int primitiveId : primitiveIds) {
            if (!info.isPrimitiveSupported(primitiveId)) {
                return false;
            }
        }
        return true;
    }

    public int[] getPrimitiveDurations(int... primitiveIds) {
        VibratorInfo info = getInfo();
        int[] durations = new int[primitiveIds.length];
        for (int i = 0; i < primitiveIds.length; i++) {
            durations[i] = info.getPrimitiveDuration(primitiveIds[i]);
        }
        return durations;
    }

    @SystemApi
    public boolean isVibrating() {
        return false;
    }

    @SystemApi
    public void addVibratorStateListener(OnVibratorStateChangedListener listener) {
    }

    @SystemApi
    public void addVibratorStateListener(Executor executor, OnVibratorStateChangedListener listener) {
    }

    @SystemApi
    public void removeVibratorStateListener(OnVibratorStateChangedListener listener) {
    }
}
