package android.media.audiofx;

import android.media.audiofx.AudioEffect;
import java.util.StringTokenizer;
/* loaded from: classes2.dex */
public class EnvironmentalReverb extends AudioEffect {
    public static final int PARAM_DECAY_HF_RATIO = 3;
    public static final int PARAM_DECAY_TIME = 2;
    public static final int PARAM_DENSITY = 9;
    public static final int PARAM_DIFFUSION = 8;
    private static final int PARAM_PROPERTIES = 10;
    public static final int PARAM_REFLECTIONS_DELAY = 5;
    public static final int PARAM_REFLECTIONS_LEVEL = 4;
    public static final int PARAM_REVERB_DELAY = 7;
    public static final int PARAM_REVERB_LEVEL = 6;
    public static final int PARAM_ROOM_HF_LEVEL = 1;
    public static final int PARAM_ROOM_LEVEL = 0;
    private static int PROPERTY_SIZE = 26;
    private static final String TAG = "EnvironmentalReverb";
    private BaseParameterListener mBaseParamListener;
    private OnParameterChangeListener mParamListener;
    private final Object mParamListenerLock;

    /* loaded from: classes2.dex */
    public interface OnParameterChangeListener {
        void onParameterChange(EnvironmentalReverb environmentalReverb, int i, int i2, int i3);
    }

    public EnvironmentalReverb(int priority, int audioSession) throws IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        super(EFFECT_TYPE_ENV_REVERB, EFFECT_TYPE_NULL, priority, audioSession);
        this.mParamListener = null;
        this.mBaseParamListener = null;
        this.mParamListenerLock = new Object();
    }

    public void setRoomLevel(short room) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = shortToByteArray(room);
        checkStatus(setParameter(0, param));
    }

    public short getRoomLevel() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[2];
        checkStatus(getParameter(0, param));
        return byteArrayToShort(param);
    }

    public void setRoomHFLevel(short roomHF) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = shortToByteArray(roomHF);
        checkStatus(setParameter(1, param));
    }

    public short getRoomHFLevel() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[2];
        checkStatus(getParameter(1, param));
        return byteArrayToShort(param);
    }

    public void setDecayTime(int decayTime) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = intToByteArray(decayTime);
        checkStatus(setParameter(2, param));
    }

    public int getDecayTime() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[4];
        checkStatus(getParameter(2, param));
        return byteArrayToInt(param);
    }

    public void setDecayHFRatio(short decayHFRatio) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = shortToByteArray(decayHFRatio);
        checkStatus(setParameter(3, param));
    }

    public short getDecayHFRatio() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[2];
        checkStatus(getParameter(3, param));
        return byteArrayToShort(param);
    }

    public void setReflectionsLevel(short reflectionsLevel) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = shortToByteArray(reflectionsLevel);
        checkStatus(setParameter(4, param));
    }

    public short getReflectionsLevel() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[2];
        checkStatus(getParameter(4, param));
        return byteArrayToShort(param);
    }

    public void setReflectionsDelay(int reflectionsDelay) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = intToByteArray(reflectionsDelay);
        checkStatus(setParameter(5, param));
    }

    public int getReflectionsDelay() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[4];
        checkStatus(getParameter(5, param));
        return byteArrayToInt(param);
    }

    public void setReverbLevel(short reverbLevel) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = shortToByteArray(reverbLevel);
        checkStatus(setParameter(6, param));
    }

    public short getReverbLevel() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[2];
        checkStatus(getParameter(6, param));
        return byteArrayToShort(param);
    }

    public void setReverbDelay(int reverbDelay) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = intToByteArray(reverbDelay);
        checkStatus(setParameter(7, param));
    }

    public int getReverbDelay() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[4];
        checkStatus(getParameter(7, param));
        return byteArrayToInt(param);
    }

    public void setDiffusion(short diffusion) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = shortToByteArray(diffusion);
        checkStatus(setParameter(8, param));
    }

    public short getDiffusion() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[2];
        checkStatus(getParameter(8, param));
        return byteArrayToShort(param);
    }

    public void setDensity(short density) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = shortToByteArray(density);
        checkStatus(setParameter(9, param));
    }

    public short getDensity() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[2];
        checkStatus(getParameter(9, param));
        return byteArrayToShort(param);
    }

    /* loaded from: classes2.dex */
    private class BaseParameterListener implements AudioEffect.OnParameterChangeListener {
        private BaseParameterListener() {
        }

        @Override // android.media.audiofx.AudioEffect.OnParameterChangeListener
        public void onParameterChange(AudioEffect effect, int status, byte[] param, byte[] value) {
            OnParameterChangeListener l = null;
            synchronized (EnvironmentalReverb.this.mParamListenerLock) {
                if (EnvironmentalReverb.this.mParamListener != null) {
                    l = EnvironmentalReverb.this.mParamListener;
                }
            }
            if (l != null) {
                int p = -1;
                int v = -1;
                if (param.length == 4) {
                    p = AudioEffect.byteArrayToInt(param, 0);
                }
                if (value.length == 2) {
                    v = AudioEffect.byteArrayToShort(value, 0);
                } else if (value.length == 4) {
                    v = AudioEffect.byteArrayToInt(value, 0);
                }
                if (p != -1 && v != -1) {
                    l.onParameterChange(EnvironmentalReverb.this, status, p, v);
                }
            }
        }
    }

    public void setParameterListener(OnParameterChangeListener listener) {
        synchronized (this.mParamListenerLock) {
            if (this.mParamListener == null) {
                this.mParamListener = listener;
                BaseParameterListener baseParameterListener = new BaseParameterListener();
                this.mBaseParamListener = baseParameterListener;
                super.setParameterListener(baseParameterListener);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class Settings {
        public short decayHFRatio;
        public int decayTime;
        public short density;
        public short diffusion;
        public int reflectionsDelay;
        public short reflectionsLevel;
        public int reverbDelay;
        public short reverbLevel;
        public short roomHFLevel;
        public short roomLevel;

        public Settings() {
        }

        public Settings(String settings) {
            StringTokenizer st = new StringTokenizer(settings, "=;");
            st.countTokens();
            if (st.countTokens() != 21) {
                throw new IllegalArgumentException("settings: " + settings);
            }
            String key = st.nextToken();
            if (!key.equals(EnvironmentalReverb.TAG)) {
                throw new IllegalArgumentException("invalid settings for EnvironmentalReverb: " + key);
            }
            try {
                String key2 = st.nextToken();
                if (!key2.equals("roomLevel")) {
                    throw new IllegalArgumentException("invalid key name: " + key2);
                }
                this.roomLevel = Short.parseShort(st.nextToken());
                String key3 = st.nextToken();
                if (!key3.equals("roomHFLevel")) {
                    throw new IllegalArgumentException("invalid key name: " + key3);
                }
                this.roomHFLevel = Short.parseShort(st.nextToken());
                String key4 = st.nextToken();
                if (!key4.equals("decayTime")) {
                    throw new IllegalArgumentException("invalid key name: " + key4);
                }
                this.decayTime = Integer.parseInt(st.nextToken());
                String key5 = st.nextToken();
                if (!key5.equals("decayHFRatio")) {
                    throw new IllegalArgumentException("invalid key name: " + key5);
                }
                this.decayHFRatio = Short.parseShort(st.nextToken());
                String key6 = st.nextToken();
                if (!key6.equals("reflectionsLevel")) {
                    throw new IllegalArgumentException("invalid key name: " + key6);
                }
                this.reflectionsLevel = Short.parseShort(st.nextToken());
                String key7 = st.nextToken();
                if (!key7.equals("reflectionsDelay")) {
                    throw new IllegalArgumentException("invalid key name: " + key7);
                }
                this.reflectionsDelay = Integer.parseInt(st.nextToken());
                String key8 = st.nextToken();
                if (!key8.equals("reverbLevel")) {
                    throw new IllegalArgumentException("invalid key name: " + key8);
                }
                this.reverbLevel = Short.parseShort(st.nextToken());
                String key9 = st.nextToken();
                if (!key9.equals("reverbDelay")) {
                    throw new IllegalArgumentException("invalid key name: " + key9);
                }
                this.reverbDelay = Integer.parseInt(st.nextToken());
                String key10 = st.nextToken();
                if (!key10.equals("diffusion")) {
                    throw new IllegalArgumentException("invalid key name: " + key10);
                }
                this.diffusion = Short.parseShort(st.nextToken());
                String key11 = st.nextToken();
                if (!key11.equals("density")) {
                    throw new IllegalArgumentException("invalid key name: " + key11);
                }
                this.density = Short.parseShort(st.nextToken());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid value for key: " + key);
            }
        }

        public String toString() {
            return new String("EnvironmentalReverb;roomLevel=" + Short.toString(this.roomLevel) + ";roomHFLevel=" + Short.toString(this.roomHFLevel) + ";decayTime=" + Integer.toString(this.decayTime) + ";decayHFRatio=" + Short.toString(this.decayHFRatio) + ";reflectionsLevel=" + Short.toString(this.reflectionsLevel) + ";reflectionsDelay=" + Integer.toString(this.reflectionsDelay) + ";reverbLevel=" + Short.toString(this.reverbLevel) + ";reverbDelay=" + Integer.toString(this.reverbDelay) + ";diffusion=" + Short.toString(this.diffusion) + ";density=" + Short.toString(this.density));
        }
    }

    public Settings getProperties() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[PROPERTY_SIZE];
        checkStatus(getParameter(10, param));
        Settings settings = new Settings();
        settings.roomLevel = byteArrayToShort(param, 0);
        settings.roomHFLevel = byteArrayToShort(param, 2);
        settings.decayTime = byteArrayToInt(param, 4);
        settings.decayHFRatio = byteArrayToShort(param, 8);
        settings.reflectionsLevel = byteArrayToShort(param, 10);
        settings.reflectionsDelay = byteArrayToInt(param, 12);
        settings.reverbLevel = byteArrayToShort(param, 16);
        settings.reverbDelay = byteArrayToInt(param, 18);
        settings.diffusion = byteArrayToShort(param, 22);
        settings.density = byteArrayToShort(param, 24);
        return settings;
    }

    public void setProperties(Settings settings) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = concatArrays(shortToByteArray(settings.roomLevel), shortToByteArray(settings.roomHFLevel), intToByteArray(settings.decayTime), shortToByteArray(settings.decayHFRatio), shortToByteArray(settings.reflectionsLevel), intToByteArray(settings.reflectionsDelay), shortToByteArray(settings.reverbLevel), intToByteArray(settings.reverbDelay), shortToByteArray(settings.diffusion), shortToByteArray(settings.density));
        checkStatus(setParameter(10, param));
    }
}
