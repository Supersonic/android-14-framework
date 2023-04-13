package android.hardware.vibrator;

import android.hardware.vibrator.IVibratorCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IVibrator extends IInterface {
    public static final int CAP_ALWAYS_ON_CONTROL = 64;
    public static final int CAP_AMPLITUDE_CONTROL = 4;
    public static final int CAP_COMPOSE_EFFECTS = 32;
    public static final int CAP_COMPOSE_PWLE_EFFECTS = 1024;
    public static final int CAP_EXTERNAL_AMPLITUDE_CONTROL = 16;
    public static final int CAP_EXTERNAL_CONTROL = 8;
    public static final int CAP_FREQUENCY_CONTROL = 512;
    public static final int CAP_GET_Q_FACTOR = 256;
    public static final int CAP_GET_RESONANT_FREQUENCY = 128;
    public static final int CAP_ON_CALLBACK = 1;
    public static final int CAP_PERFORM_CALLBACK = 2;
    public static final String DESCRIPTOR = "android$hardware$vibrator$IVibrator".replace('$', '.');
    public static final String HASH = "ea8742d6993e1a82917da38b9938e537aa7fcb54";
    public static final int VERSION = 2;

    void alwaysOnDisable(int i) throws RemoteException;

    void alwaysOnEnable(int i, int i2, byte b) throws RemoteException;

    void compose(CompositeEffect[] compositeEffectArr, IVibratorCallback iVibratorCallback) throws RemoteException;

    void composePwle(PrimitivePwle[] primitivePwleArr, IVibratorCallback iVibratorCallback) throws RemoteException;

    float[] getBandwidthAmplitudeMap() throws RemoteException;

    int getCapabilities() throws RemoteException;

    int getCompositionDelayMax() throws RemoteException;

    int getCompositionSizeMax() throws RemoteException;

    float getFrequencyMinimum() throws RemoteException;

    float getFrequencyResolution() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    int getPrimitiveDuration(int i) throws RemoteException;

    int getPwleCompositionSizeMax() throws RemoteException;

    int getPwlePrimitiveDurationMax() throws RemoteException;

    float getQFactor() throws RemoteException;

    float getResonantFrequency() throws RemoteException;

    int[] getSupportedAlwaysOnEffects() throws RemoteException;

    int[] getSupportedBraking() throws RemoteException;

    int[] getSupportedEffects() throws RemoteException;

    int[] getSupportedPrimitives() throws RemoteException;

    void off() throws RemoteException;

    /* renamed from: on */
    void mo154on(int i, IVibratorCallback iVibratorCallback) throws RemoteException;

    int perform(int i, byte b, IVibratorCallback iVibratorCallback) throws RemoteException;

    void setAmplitude(float f) throws RemoteException;

    void setExternalControl(boolean z) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IVibrator {
        @Override // android.hardware.vibrator.IVibrator
        public int getCapabilities() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.vibrator.IVibrator
        public void off() throws RemoteException {
        }

        @Override // android.hardware.vibrator.IVibrator
        /* renamed from: on */
        public void mo154on(int timeoutMs, IVibratorCallback callback) throws RemoteException {
        }

        @Override // android.hardware.vibrator.IVibrator
        public int perform(int effect, byte strength, IVibratorCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.vibrator.IVibrator
        public int[] getSupportedEffects() throws RemoteException {
            return null;
        }

        @Override // android.hardware.vibrator.IVibrator
        public void setAmplitude(float amplitude) throws RemoteException {
        }

        @Override // android.hardware.vibrator.IVibrator
        public void setExternalControl(boolean enabled) throws RemoteException {
        }

        @Override // android.hardware.vibrator.IVibrator
        public int getCompositionDelayMax() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.vibrator.IVibrator
        public int getCompositionSizeMax() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.vibrator.IVibrator
        public int[] getSupportedPrimitives() throws RemoteException {
            return null;
        }

        @Override // android.hardware.vibrator.IVibrator
        public int getPrimitiveDuration(int primitive) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.vibrator.IVibrator
        public void compose(CompositeEffect[] composite, IVibratorCallback callback) throws RemoteException {
        }

        @Override // android.hardware.vibrator.IVibrator
        public int[] getSupportedAlwaysOnEffects() throws RemoteException {
            return null;
        }

        @Override // android.hardware.vibrator.IVibrator
        public void alwaysOnEnable(int id, int effect, byte strength) throws RemoteException {
        }

        @Override // android.hardware.vibrator.IVibrator
        public void alwaysOnDisable(int id) throws RemoteException {
        }

        @Override // android.hardware.vibrator.IVibrator
        public float getResonantFrequency() throws RemoteException {
            return 0.0f;
        }

        @Override // android.hardware.vibrator.IVibrator
        public float getQFactor() throws RemoteException {
            return 0.0f;
        }

        @Override // android.hardware.vibrator.IVibrator
        public float getFrequencyResolution() throws RemoteException {
            return 0.0f;
        }

        @Override // android.hardware.vibrator.IVibrator
        public float getFrequencyMinimum() throws RemoteException {
            return 0.0f;
        }

        @Override // android.hardware.vibrator.IVibrator
        public float[] getBandwidthAmplitudeMap() throws RemoteException {
            return null;
        }

        @Override // android.hardware.vibrator.IVibrator
        public int getPwlePrimitiveDurationMax() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.vibrator.IVibrator
        public int getPwleCompositionSizeMax() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.vibrator.IVibrator
        public int[] getSupportedBraking() throws RemoteException {
            return null;
        }

        @Override // android.hardware.vibrator.IVibrator
        public void composePwle(PrimitivePwle[] composite, IVibratorCallback callback) throws RemoteException {
        }

        @Override // android.hardware.vibrator.IVibrator
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.vibrator.IVibrator
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IVibrator {
        static final int TRANSACTION_alwaysOnDisable = 15;
        static final int TRANSACTION_alwaysOnEnable = 14;
        static final int TRANSACTION_compose = 12;
        static final int TRANSACTION_composePwle = 24;
        static final int TRANSACTION_getBandwidthAmplitudeMap = 20;
        static final int TRANSACTION_getCapabilities = 1;
        static final int TRANSACTION_getCompositionDelayMax = 8;
        static final int TRANSACTION_getCompositionSizeMax = 9;
        static final int TRANSACTION_getFrequencyMinimum = 19;
        static final int TRANSACTION_getFrequencyResolution = 18;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getPrimitiveDuration = 11;
        static final int TRANSACTION_getPwleCompositionSizeMax = 22;
        static final int TRANSACTION_getPwlePrimitiveDurationMax = 21;
        static final int TRANSACTION_getQFactor = 17;
        static final int TRANSACTION_getResonantFrequency = 16;
        static final int TRANSACTION_getSupportedAlwaysOnEffects = 13;
        static final int TRANSACTION_getSupportedBraking = 23;
        static final int TRANSACTION_getSupportedEffects = 5;
        static final int TRANSACTION_getSupportedPrimitives = 10;
        static final int TRANSACTION_off = 2;
        static final int TRANSACTION_on = 3;
        static final int TRANSACTION_perform = 4;
        static final int TRANSACTION_setAmplitude = 6;
        static final int TRANSACTION_setExternalControl = 7;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IVibrator asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IVibrator)) {
                return (IVibrator) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case 16777214:
                    reply.writeNoException();
                    reply.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    reply.writeNoException();
                    reply.writeInt(getInterfaceVersion());
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _result = getCapabilities();
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            off();
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg0 = data.readInt();
                            IVibratorCallback _arg1 = IVibratorCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            mo154on(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg02 = data.readInt();
                            byte _arg12 = data.readByte();
                            IVibratorCallback _arg2 = IVibratorCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result2 = perform(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 5:
                            int[] _result3 = getSupportedEffects();
                            reply.writeNoException();
                            reply.writeIntArray(_result3);
                            break;
                        case 6:
                            float _arg03 = data.readFloat();
                            data.enforceNoDataAvail();
                            setAmplitude(_arg03);
                            reply.writeNoException();
                            break;
                        case 7:
                            boolean _arg04 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setExternalControl(_arg04);
                            reply.writeNoException();
                            break;
                        case 8:
                            int _result4 = getCompositionDelayMax();
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 9:
                            int _result5 = getCompositionSizeMax();
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 10:
                            int[] _result6 = getSupportedPrimitives();
                            reply.writeNoException();
                            reply.writeIntArray(_result6);
                            break;
                        case 11:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result7 = getPrimitiveDuration(_arg05);
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 12:
                            CompositeEffect[] _arg06 = (CompositeEffect[]) data.createTypedArray(CompositeEffect.CREATOR);
                            IVibratorCallback _arg13 = IVibratorCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            compose(_arg06, _arg13);
                            reply.writeNoException();
                            break;
                        case 13:
                            int[] _result8 = getSupportedAlwaysOnEffects();
                            reply.writeNoException();
                            reply.writeIntArray(_result8);
                            break;
                        case 14:
                            int _arg07 = data.readInt();
                            int _arg14 = data.readInt();
                            byte _arg22 = data.readByte();
                            data.enforceNoDataAvail();
                            alwaysOnEnable(_arg07, _arg14, _arg22);
                            reply.writeNoException();
                            break;
                        case 15:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            alwaysOnDisable(_arg08);
                            reply.writeNoException();
                            break;
                        case 16:
                            float _result9 = getResonantFrequency();
                            reply.writeNoException();
                            reply.writeFloat(_result9);
                            break;
                        case 17:
                            float _result10 = getQFactor();
                            reply.writeNoException();
                            reply.writeFloat(_result10);
                            break;
                        case 18:
                            float _result11 = getFrequencyResolution();
                            reply.writeNoException();
                            reply.writeFloat(_result11);
                            break;
                        case 19:
                            float _result12 = getFrequencyMinimum();
                            reply.writeNoException();
                            reply.writeFloat(_result12);
                            break;
                        case 20:
                            float[] _result13 = getBandwidthAmplitudeMap();
                            reply.writeNoException();
                            reply.writeFloatArray(_result13);
                            break;
                        case 21:
                            int _result14 = getPwlePrimitiveDurationMax();
                            reply.writeNoException();
                            reply.writeInt(_result14);
                            break;
                        case 22:
                            int _result15 = getPwleCompositionSizeMax();
                            reply.writeNoException();
                            reply.writeInt(_result15);
                            break;
                        case 23:
                            int[] _result16 = getSupportedBraking();
                            reply.writeNoException();
                            reply.writeIntArray(_result16);
                            break;
                        case 24:
                            PrimitivePwle[] _arg09 = (PrimitivePwle[]) data.createTypedArray(PrimitivePwle.CREATOR);
                            IVibratorCallback _arg15 = IVibratorCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            composePwle(_arg09, _arg15);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IVibrator {
            private IBinder mRemote;
            private int mCachedVersion = -1;
            private String mCachedHash = "-1";

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.hardware.vibrator.IVibrator
            public int getCapabilities() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getCapabilities is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public void off() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method off is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            /* renamed from: on */
            public void mo154on(int timeoutMs, IVibratorCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(timeoutMs);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method on is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int perform(int effect, byte strength, IVibratorCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(effect);
                    _data.writeByte(strength);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method perform is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int[] getSupportedEffects() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getSupportedEffects is unimplemented.");
                    }
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public void setAmplitude(float amplitude) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeFloat(amplitude);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setAmplitude is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public void setExternalControl(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setExternalControl is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int getCompositionDelayMax() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getCompositionDelayMax is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int getCompositionSizeMax() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getCompositionSizeMax is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int[] getSupportedPrimitives() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getSupportedPrimitives is unimplemented.");
                    }
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int getPrimitiveDuration(int primitive) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(primitive);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getPrimitiveDuration is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public void compose(CompositeEffect[] composite, IVibratorCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedArray(composite, 0);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method compose is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int[] getSupportedAlwaysOnEffects() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getSupportedAlwaysOnEffects is unimplemented.");
                    }
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public void alwaysOnEnable(int id, int effect, byte strength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(effect);
                    _data.writeByte(strength);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method alwaysOnEnable is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public void alwaysOnDisable(int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(id);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method alwaysOnDisable is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public float getResonantFrequency() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(16, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getResonantFrequency is unimplemented.");
                    }
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public float getQFactor() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(17, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getQFactor is unimplemented.");
                    }
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public float getFrequencyResolution() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(18, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getFrequencyResolution is unimplemented.");
                    }
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public float getFrequencyMinimum() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(19, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getFrequencyMinimum is unimplemented.");
                    }
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public float[] getBandwidthAmplitudeMap() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(20, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getBandwidthAmplitudeMap is unimplemented.");
                    }
                    _reply.readException();
                    float[] _result = _reply.createFloatArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int getPwlePrimitiveDurationMax() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(21, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getPwlePrimitiveDurationMax is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int getPwleCompositionSizeMax() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(22, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getPwleCompositionSizeMax is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int[] getSupportedBraking() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(23, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getSupportedBraking is unimplemented.");
                    }
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public void composePwle(PrimitivePwle[] composite, IVibratorCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedArray(composite, 0);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(24, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method composePwle is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.vibrator.IVibrator
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(DESCRIPTOR);
                        this.mRemote.transact(16777215, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.hardware.vibrator.IVibrator
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(16777214, data, reply, 0);
                    reply.readException();
                    this.mCachedHash = reply.readString();
                    reply.recycle();
                    data.recycle();
                }
                return this.mCachedHash;
            }
        }
    }
}
