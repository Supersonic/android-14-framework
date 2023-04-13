package android.media.audiofx;

import android.annotation.SystemApi;
import android.content.AttributionSource;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceInfo;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.Parcel;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.UUID;
/* loaded from: classes2.dex */
public class AudioEffect {
    public static final String ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION = "android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION";
    public static final String ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL = "android.media.action.DISPLAY_AUDIO_EFFECT_CONTROL_PANEL";
    public static final String ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION = "android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION";
    public static final int ALREADY_EXISTS = -2;
    public static final int CONTENT_TYPE_GAME = 2;
    public static final int CONTENT_TYPE_MOVIE = 1;
    public static final int CONTENT_TYPE_MUSIC = 0;
    public static final int CONTENT_TYPE_VOICE = 3;
    public static final String EFFECT_AUXILIARY = "Auxiliary";
    public static final String EFFECT_INSERT = "Insert";
    public static final String EFFECT_POST_PROCESSING = "Post Processing";
    public static final String EFFECT_PRE_PROCESSING = "Pre Processing";
    public static final UUID EFFECT_TYPE_AEC;
    public static final UUID EFFECT_TYPE_AGC;
    public static final UUID EFFECT_TYPE_BASS_BOOST;
    public static final UUID EFFECT_TYPE_DYNAMICS_PROCESSING;
    public static final UUID EFFECT_TYPE_ENV_REVERB;
    public static final UUID EFFECT_TYPE_EQUALIZER;
    public static final UUID EFFECT_TYPE_HAPTIC_GENERATOR;
    public static final UUID EFFECT_TYPE_LOUDNESS_ENHANCER;
    public static final UUID EFFECT_TYPE_NS;
    public static final UUID EFFECT_TYPE_NULL;
    public static final UUID EFFECT_TYPE_PRESET_REVERB;
    public static final UUID EFFECT_TYPE_VIRTUALIZER;
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -4;
    public static final int ERROR_DEAD_OBJECT = -7;
    public static final int ERROR_INVALID_OPERATION = -5;
    public static final int ERROR_NO_INIT = -3;
    public static final int ERROR_NO_MEMORY = -6;
    public static final String EXTRA_AUDIO_SESSION = "android.media.extra.AUDIO_SESSION";
    public static final String EXTRA_CONTENT_TYPE = "android.media.extra.CONTENT_TYPE";
    public static final String EXTRA_PACKAGE_NAME = "android.media.extra.PACKAGE_NAME";
    public static final int NATIVE_EVENT_CONTROL_STATUS = 0;
    public static final int NATIVE_EVENT_ENABLED_STATUS = 1;
    public static final int NATIVE_EVENT_PARAMETER_CHANGED = 2;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_UNINITIALIZED = 0;
    public static final int SUCCESS = 0;
    private static final String TAG = "AudioEffect-JAVA";
    private OnControlStatusChangeListener mControlChangeStatusListener;
    private Descriptor mDescriptor;
    private OnEnableStatusChangeListener mEnableStatusChangeListener;
    private int mId;
    private long mJniData;
    public final Object mListenerLock;
    private long mNativeAudioEffect;
    public NativeEventHandler mNativeEventHandler;
    private OnParameterChangeListener mParameterChangeListener;
    private int mState;
    private final Object mStateLock;

    /* loaded from: classes2.dex */
    public interface OnControlStatusChangeListener {
        void onControlStatusChange(AudioEffect audioEffect, boolean z);
    }

    /* loaded from: classes2.dex */
    public interface OnEnableStatusChangeListener {
        void onEnableStatusChange(AudioEffect audioEffect, boolean z);
    }

    /* loaded from: classes2.dex */
    public interface OnParameterChangeListener {
        void onParameterChange(AudioEffect audioEffect, int i, byte[] bArr, byte[] bArr2);
    }

    private final native int native_command(int i, int i2, byte[] bArr, int i3, byte[] bArr2);

    private final native void native_finalize();

    private final native boolean native_getEnabled();

    private final native int native_getParameter(int i, byte[] bArr, int i2, byte[] bArr2);

    private final native boolean native_hasControl();

    private static final native void native_init();

    private static native Object[] native_query_effects();

    private static native Object[] native_query_pre_processing(int i);

    private final native void native_release();

    private final native int native_setEnabled(boolean z);

    private final native int native_setParameter(int i, byte[] bArr, int i2, byte[] bArr2);

    private final native int native_setup(Object obj, String str, String str2, int i, int i2, int i3, String str3, int[] iArr, Object[] objArr, Parcel parcel, boolean z);

    static {
        System.loadLibrary("audioeffect_jni");
        native_init();
        EFFECT_TYPE_ENV_REVERB = UUID.fromString("c2e5d5f0-94bd-4763-9cac-4e234d06839e");
        EFFECT_TYPE_PRESET_REVERB = UUID.fromString("47382d60-ddd8-11db-bf3a-0002a5d5c51b");
        EFFECT_TYPE_EQUALIZER = UUID.fromString("0bed4300-ddd6-11db-8f34-0002a5d5c51b");
        EFFECT_TYPE_BASS_BOOST = UUID.fromString("0634f220-ddd4-11db-a0fc-0002a5d5c51b");
        EFFECT_TYPE_VIRTUALIZER = UUID.fromString("37cc2c00-dddd-11db-8577-0002a5d5c51b");
        EFFECT_TYPE_AGC = UUID.fromString("0a8abfe0-654c-11e0-ba26-0002a5d5c51b");
        EFFECT_TYPE_AEC = UUID.fromString("7b491460-8d4d-11e0-bd61-0002a5d5c51b");
        EFFECT_TYPE_NS = UUID.fromString("58b4b260-8e06-11e0-aa8e-0002a5d5c51b");
        EFFECT_TYPE_LOUDNESS_ENHANCER = UUID.fromString("fe3199be-aed0-413f-87bb-11260eb63cf1");
        EFFECT_TYPE_DYNAMICS_PROCESSING = UUID.fromString("7261676f-6d75-7369-6364-28e2fd3ac39e");
        EFFECT_TYPE_HAPTIC_GENERATOR = UUID.fromString("1411e6d6-aecd-4021-a1cf-a6aceb0d71e5");
        EFFECT_TYPE_NULL = UUID.fromString("ec7178ec-e5e1-4432-a3f4-4657e6795210");
    }

    /* loaded from: classes2.dex */
    public static class Descriptor {
        public String connectMode;
        public String implementor;
        public String name;
        public UUID type;
        public UUID uuid;

        public Descriptor() {
        }

        public Descriptor(String type, String uuid, String connectMode, String name, String implementor) {
            this.type = UUID.fromString(type);
            this.uuid = UUID.fromString(uuid);
            this.connectMode = connectMode;
            this.name = name;
            this.implementor = implementor;
        }

        public Descriptor(Parcel in) {
            this.type = UUID.fromString(in.readString());
            this.uuid = UUID.fromString(in.readString());
            this.connectMode = in.readString();
            this.name = in.readString();
            this.implementor = in.readString();
        }

        public int hashCode() {
            return Objects.hash(this.type, this.uuid, this.connectMode, this.name, this.implementor);
        }

        public void writeToParcel(Parcel dest) {
            dest.writeString(this.type.toString());
            dest.writeString(this.uuid.toString());
            dest.writeString(this.connectMode);
            dest.writeString(this.name);
            dest.writeString(this.implementor);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof Descriptor)) {
                return false;
            }
            Descriptor that = (Descriptor) o;
            if (this.type.equals(that.type) && this.uuid.equals(that.uuid) && this.connectMode.equals(that.connectMode) && this.name.equals(that.name) && this.implementor.equals(that.implementor)) {
                return true;
            }
            return false;
        }
    }

    public AudioEffect(UUID type, UUID uuid, int priority, int audioSession) throws IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        this(type, uuid, priority, audioSession, null);
    }

    @SystemApi
    public AudioEffect(UUID uuid, AudioDeviceAttributes device) {
        this(EFFECT_TYPE_NULL, (UUID) Objects.requireNonNull(uuid), 0, -2, (AudioDeviceAttributes) Objects.requireNonNull(device));
    }

    private AudioEffect(UUID type, UUID uuid, int priority, int audioSession, AudioDeviceAttributes device) throws IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        this(type, uuid, priority, audioSession, device, false);
    }

    private AudioEffect(UUID type, UUID uuid, int priority, int audioSession, AudioDeviceAttributes device, boolean probe) throws IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        int deviceType;
        String deviceAddress;
        Throwable th;
        int deviceType2;
        this.mState = 0;
        Object obj = new Object();
        this.mStateLock = obj;
        this.mEnableStatusChangeListener = null;
        this.mControlChangeStatusListener = null;
        this.mParameterChangeListener = null;
        this.mListenerLock = new Object();
        this.mNativeEventHandler = null;
        int[] id = new int[1];
        Descriptor[] desc = new Descriptor[1];
        if (device == null) {
            deviceType = 0;
            deviceAddress = "";
        } else {
            if (device.getRole() == 2) {
                deviceType2 = AudioDeviceInfo.convertDeviceTypeToInternalDevice(device.getType());
            } else {
                deviceType2 = AudioDeviceInfo.convertDeviceTypeToInternalInputDevice(device.getType(), device.getAddress());
            }
            String deviceAddress2 = device.getAddress();
            deviceType = deviceType2;
            deviceAddress = deviceAddress2;
        }
        AttributionSource.ScopedParcelState attributionSourceState = AttributionSource.myAttributionSource().asScopedParcelState();
        try {
        } catch (Throwable th2) {
            th = th2;
        }
        try {
            int initResult = native_setup(new WeakReference(this), type.toString(), uuid.toString(), priority, audioSession, deviceType, deviceAddress, id, desc, attributionSourceState.getParcel(), probe);
            if (attributionSourceState != null) {
                attributionSourceState.close();
            }
            if (initResult == 0 || initResult == -2) {
                this.mId = id[0];
                this.mDescriptor = desc[0];
                if (!probe) {
                    synchronized (obj) {
                        this.mState = 1;
                    }
                    return;
                }
                return;
            }
            Log.m110e(TAG, "Error code " + initResult + " when initializing AudioEffect.");
            switch (initResult) {
                case -5:
                    throw new UnsupportedOperationException("Effect library not loaded");
                case -4:
                    throw new IllegalArgumentException("Effect type: " + type + " not supported.");
                default:
                    throw new RuntimeException("Cannot initialize effect engine for type: " + type + " Error: " + initResult);
            }
        } catch (Throwable th3) {
            th = th3;
            if (attributionSourceState != null) {
                try {
                    attributionSourceState.close();
                } catch (Throwable th4) {
                    th.addSuppressed(th4);
                }
            }
            throw th;
        }
    }

    @SystemApi
    public static boolean isEffectSupportedForDevice(UUID uuid, AudioDeviceAttributes device) {
        try {
            AudioEffect fx = new AudioEffect(EFFECT_TYPE_NULL, (UUID) Objects.requireNonNull(uuid), 0, -2, (AudioDeviceAttributes) Objects.requireNonNull(device), true);
            fx.release();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void release() {
        synchronized (this.mStateLock) {
            native_release();
            this.mState = 0;
        }
    }

    protected void finalize() {
        native_finalize();
    }

    public Descriptor getDescriptor() throws IllegalStateException {
        checkState("getDescriptor()");
        return this.mDescriptor;
    }

    public static Descriptor[] queryEffects() {
        return (Descriptor[]) native_query_effects();
    }

    public static Descriptor[] queryPreProcessings(int audioSession) {
        return (Descriptor[]) native_query_pre_processing(audioSession);
    }

    public static boolean isEffectTypeAvailable(UUID type) {
        Descriptor[] desc = queryEffects();
        if (desc == null) {
            return false;
        }
        for (Descriptor descriptor : desc) {
            if (descriptor.type.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public int setEnabled(boolean enabled) throws IllegalStateException {
        checkState("setEnabled()");
        return native_setEnabled(enabled);
    }

    public int setParameter(byte[] param, byte[] value) throws IllegalStateException {
        checkState("setParameter()");
        return native_setParameter(param.length, param, value.length, value);
    }

    public int setParameter(int param, int value) throws IllegalStateException {
        byte[] p = intToByteArray(param);
        byte[] v = intToByteArray(value);
        return setParameter(p, v);
    }

    public int setParameter(int param, short value) throws IllegalStateException {
        byte[] p = intToByteArray(param);
        byte[] v = shortToByteArray(value);
        return setParameter(p, v);
    }

    public int setParameter(int param, byte[] value) throws IllegalStateException {
        byte[] p = intToByteArray(param);
        return setParameter(p, value);
    }

    public int setParameter(int[] param, int[] value) throws IllegalStateException {
        if (param.length > 2 || value.length > 2) {
            return -4;
        }
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            byte[] p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        byte[] v = intToByteArray(value[0]);
        if (value.length > 1) {
            byte[] v2 = intToByteArray(value[1]);
            v = concatArrays(v, v2);
        }
        return setParameter(p, v);
    }

    public int setParameter(int[] param, short[] value) throws IllegalStateException {
        if (param.length > 2 || value.length > 2) {
            return -4;
        }
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            byte[] p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        byte[] v = shortToByteArray(value[0]);
        if (value.length > 1) {
            byte[] v2 = shortToByteArray(value[1]);
            v = concatArrays(v, v2);
        }
        return setParameter(p, v);
    }

    public int setParameter(int[] param, byte[] value) throws IllegalStateException {
        if (param.length > 2) {
            return -4;
        }
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            byte[] p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        return setParameter(p, value);
    }

    public int getParameter(byte[] param, byte[] value) throws IllegalStateException {
        checkState("getParameter()");
        return native_getParameter(param.length, param, value.length, value);
    }

    public int getParameter(int param, byte[] value) throws IllegalStateException {
        byte[] p = intToByteArray(param);
        return getParameter(p, value);
    }

    public int getParameter(int param, int[] value) throws IllegalStateException {
        if (value.length > 2) {
            return -4;
        }
        byte[] p = intToByteArray(param);
        byte[] v = new byte[value.length * 4];
        int status = getParameter(p, v);
        if (status == 4 || status == 8) {
            value[0] = byteArrayToInt(v);
            if (status == 8) {
                value[1] = byteArrayToInt(v, 4);
            }
            return status / 4;
        }
        return -1;
    }

    public int getParameter(int param, short[] value) throws IllegalStateException {
        if (value.length > 2) {
            return -4;
        }
        byte[] p = intToByteArray(param);
        byte[] v = new byte[value.length * 2];
        int status = getParameter(p, v);
        if (status == 2 || status == 4) {
            value[0] = byteArrayToShort(v);
            if (status == 4) {
                value[1] = byteArrayToShort(v, 2);
            }
            return status / 2;
        }
        return -1;
    }

    public int getParameter(int[] param, int[] value) throws IllegalStateException {
        if (param.length > 2 || value.length > 2) {
            return -4;
        }
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            byte[] p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        byte[] v = new byte[value.length * 4];
        int status = getParameter(p, v);
        if (status == 4 || status == 8) {
            value[0] = byteArrayToInt(v);
            if (status == 8) {
                value[1] = byteArrayToInt(v, 4);
            }
            return status / 4;
        }
        return -1;
    }

    public int getParameter(int[] param, short[] value) throws IllegalStateException {
        if (param.length > 2 || value.length > 2) {
            return -4;
        }
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            byte[] p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        byte[] v = new byte[value.length * 2];
        int status = getParameter(p, v);
        if (status == 2 || status == 4) {
            value[0] = byteArrayToShort(v);
            if (status == 4) {
                value[1] = byteArrayToShort(v, 2);
            }
            return status / 2;
        }
        return -1;
    }

    public int getParameter(int[] param, byte[] value) throws IllegalStateException {
        if (param.length > 2) {
            return -4;
        }
        byte[] p = intToByteArray(param[0]);
        if (param.length > 1) {
            byte[] p2 = intToByteArray(param[1]);
            p = concatArrays(p, p2);
        }
        return getParameter(p, value);
    }

    public int command(int cmdCode, byte[] command, byte[] reply) throws IllegalStateException {
        checkState("command()");
        return native_command(cmdCode, command.length, command, reply.length, reply);
    }

    public int getId() throws IllegalStateException {
        checkState("getId()");
        return this.mId;
    }

    public boolean getEnabled() throws IllegalStateException {
        checkState("getEnabled()");
        return native_getEnabled();
    }

    public boolean hasControl() throws IllegalStateException {
        checkState("hasControl()");
        return native_hasControl();
    }

    public void setEnableStatusListener(OnEnableStatusChangeListener listener) {
        synchronized (this.mListenerLock) {
            this.mEnableStatusChangeListener = listener;
        }
        if (listener != null && this.mNativeEventHandler == null) {
            createNativeEventHandler();
        }
    }

    public void setControlStatusListener(OnControlStatusChangeListener listener) {
        synchronized (this.mListenerLock) {
            this.mControlChangeStatusListener = listener;
        }
        if (listener != null && this.mNativeEventHandler == null) {
            createNativeEventHandler();
        }
    }

    public void setParameterListener(OnParameterChangeListener listener) {
        synchronized (this.mListenerLock) {
            this.mParameterChangeListener = listener;
        }
        if (listener != null && this.mNativeEventHandler == null) {
            createNativeEventHandler();
        }
    }

    private void createNativeEventHandler() {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this.mNativeEventHandler = new NativeEventHandler(this, looper);
            return;
        }
        Looper looper2 = Looper.getMainLooper();
        if (looper2 != null) {
            this.mNativeEventHandler = new NativeEventHandler(this, looper2);
        } else {
            this.mNativeEventHandler = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class NativeEventHandler extends Handler {
        private AudioEffect mAudioEffect;

        public NativeEventHandler(AudioEffect ae, Looper looper) {
            super(looper);
            this.mAudioEffect = ae;
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            OnControlStatusChangeListener controlStatusChangeListener;
            OnEnableStatusChangeListener enableStatusChangeListener;
            OnParameterChangeListener parameterChangeListener;
            if (this.mAudioEffect == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    synchronized (AudioEffect.this.mListenerLock) {
                        controlStatusChangeListener = this.mAudioEffect.mControlChangeStatusListener;
                    }
                    if (controlStatusChangeListener != null) {
                        controlStatusChangeListener.onControlStatusChange(this.mAudioEffect, msg.arg1 != 0);
                        return;
                    }
                    return;
                case 1:
                    synchronized (AudioEffect.this.mListenerLock) {
                        enableStatusChangeListener = this.mAudioEffect.mEnableStatusChangeListener;
                    }
                    if (enableStatusChangeListener != null) {
                        enableStatusChangeListener.onEnableStatusChange(this.mAudioEffect, msg.arg1 != 0);
                        return;
                    }
                    return;
                case 2:
                    synchronized (AudioEffect.this.mListenerLock) {
                        parameterChangeListener = this.mAudioEffect.mParameterChangeListener;
                    }
                    if (parameterChangeListener != null) {
                        int vOffset = msg.arg1;
                        byte[] p = (byte[]) msg.obj;
                        int status = AudioEffect.byteArrayToInt(p, 0);
                        int psize = AudioEffect.byteArrayToInt(p, 4);
                        int vsize = AudioEffect.byteArrayToInt(p, 8);
                        byte[] param = new byte[psize];
                        byte[] value = new byte[vsize];
                        System.arraycopy(p, 12, param, 0, psize);
                        System.arraycopy(p, vOffset, value, 0, vsize);
                        parameterChangeListener.onParameterChange(this.mAudioEffect, status, param, value);
                        return;
                    }
                    return;
                default:
                    Log.m110e(AudioEffect.TAG, "handleMessage() Unknown event type: " + msg.what);
                    return;
            }
        }
    }

    private static void postEventFromNative(Object effect_ref, int what, int arg1, int arg2, Object obj) {
        NativeEventHandler nativeEventHandler;
        AudioEffect effect = (AudioEffect) ((WeakReference) effect_ref).get();
        if (effect != null && (nativeEventHandler = effect.mNativeEventHandler) != null) {
            Message m = nativeEventHandler.obtainMessage(what, arg1, arg2, obj);
            effect.mNativeEventHandler.sendMessage(m);
        }
    }

    public void checkState(String methodName) throws IllegalStateException {
        synchronized (this.mStateLock) {
            if (this.mState != 1) {
                throw new IllegalStateException(methodName + " called on uninitialized AudioEffect.");
            }
        }
    }

    public void checkStatus(int status) {
        if (isError(status)) {
            switch (status) {
                case -5:
                    throw new UnsupportedOperationException("AudioEffect: invalid parameter operation");
                case -4:
                    throw new IllegalArgumentException("AudioEffect: bad parameter value");
                default:
                    throw new RuntimeException("AudioEffect: set/get parameter error");
            }
        }
    }

    public static boolean isError(int status) {
        return status < 0;
    }

    public static int byteArrayToInt(byte[] valueBuf) {
        return byteArrayToInt(valueBuf, 0);
    }

    public static int byteArrayToInt(byte[] valueBuf, int offset) {
        ByteBuffer converter = ByteBuffer.wrap(valueBuf);
        converter.order(ByteOrder.nativeOrder());
        return converter.getInt(offset);
    }

    public static byte[] intToByteArray(int value) {
        ByteBuffer converter = ByteBuffer.allocate(4);
        converter.order(ByteOrder.nativeOrder());
        converter.putInt(value);
        return converter.array();
    }

    public static short byteArrayToShort(byte[] valueBuf) {
        return byteArrayToShort(valueBuf, 0);
    }

    public static short byteArrayToShort(byte[] valueBuf, int offset) {
        ByteBuffer converter = ByteBuffer.wrap(valueBuf);
        converter.order(ByteOrder.nativeOrder());
        return converter.getShort(offset);
    }

    public static byte[] shortToByteArray(short value) {
        ByteBuffer converter = ByteBuffer.allocate(2);
        converter.order(ByteOrder.nativeOrder());
        converter.putShort(value);
        return converter.array();
    }

    public static float byteArrayToFloat(byte[] valueBuf) {
        return byteArrayToFloat(valueBuf, 0);
    }

    public static float byteArrayToFloat(byte[] valueBuf, int offset) {
        ByteBuffer converter = ByteBuffer.wrap(valueBuf);
        converter.order(ByteOrder.nativeOrder());
        return converter.getFloat(offset);
    }

    public static byte[] floatToByteArray(float value) {
        ByteBuffer converter = ByteBuffer.allocate(4);
        converter.order(ByteOrder.nativeOrder());
        converter.putFloat(value);
        return converter.array();
    }

    public static byte[] concatArrays(byte[]... arrays) {
        int len = 0;
        for (byte[] a : arrays) {
            len += a.length;
        }
        byte[] b = new byte[len];
        int offs = 0;
        for (byte[] a2 : arrays) {
            System.arraycopy(a2, 0, b, offs, a2.length);
            offs += a2.length;
        }
        return b;
    }
}
