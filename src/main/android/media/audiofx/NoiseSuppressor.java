package android.media.audiofx;

import android.util.Log;
/* loaded from: classes2.dex */
public class NoiseSuppressor extends AudioEffect {
    private static final String TAG = "NoiseSuppressor";

    public static boolean isAvailable() {
        return AudioEffect.isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_NS);
    }

    public static NoiseSuppressor create(int audioSession) {
        try {
            NoiseSuppressor ns = new NoiseSuppressor(audioSession);
            return ns;
        } catch (IllegalArgumentException e) {
            Log.m104w(TAG, "not implemented on this device " + ((Object) null));
            return null;
        } catch (UnsupportedOperationException e2) {
            Log.m104w(TAG, "not enough resources");
            return null;
        } catch (RuntimeException e3) {
            Log.m104w(TAG, "not enough memory");
            return null;
        }
    }

    private NoiseSuppressor(int audioSession) throws IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        super(EFFECT_TYPE_NS, EFFECT_TYPE_NULL, 0, audioSession);
    }
}
