package android.hardware.soundtrigger3;

import android.hardware.soundtrigger3.ISoundTriggerHwCallback;
import android.hardware.soundtrigger3.ISoundTriggerHwGlobalCallback;
import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.Properties;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.SoundModel;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ISoundTriggerHw extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$soundtrigger3$ISoundTriggerHw".replace('$', '.');
    public static final String HASH = "7d8d63478cd50e766d2072140c8aa3457f9fb585";
    public static final int VERSION = 1;

    void forceRecognitionEvent(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    int getParameter(int i, int i2) throws RemoteException;

    Properties getProperties() throws RemoteException;

    int loadPhraseSoundModel(PhraseSoundModel phraseSoundModel, ISoundTriggerHwCallback iSoundTriggerHwCallback) throws RemoteException;

    int loadSoundModel(SoundModel soundModel, ISoundTriggerHwCallback iSoundTriggerHwCallback) throws RemoteException;

    ModelParameterRange queryParameter(int i, int i2) throws RemoteException;

    void registerGlobalCallback(ISoundTriggerHwGlobalCallback iSoundTriggerHwGlobalCallback) throws RemoteException;

    void setParameter(int i, int i2, int i3) throws RemoteException;

    void startRecognition(int i, int i2, int i3, RecognitionConfig recognitionConfig) throws RemoteException;

    void stopRecognition(int i) throws RemoteException;

    void unloadSoundModel(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISoundTriggerHw {
        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public Properties getProperties() throws RemoteException {
            return null;
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public void registerGlobalCallback(ISoundTriggerHwGlobalCallback callback) throws RemoteException {
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public int loadSoundModel(SoundModel soundModel, ISoundTriggerHwCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public int loadPhraseSoundModel(PhraseSoundModel soundModel, ISoundTriggerHwCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public void unloadSoundModel(int modelHandle) throws RemoteException {
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public void startRecognition(int modelHandle, int deviceHandle, int ioHandle, RecognitionConfig config) throws RemoteException {
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public void stopRecognition(int modelHandle) throws RemoteException {
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public void forceRecognitionEvent(int modelHandle) throws RemoteException {
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public ModelParameterRange queryParameter(int modelHandle, int modelParam) throws RemoteException {
            return null;
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public int getParameter(int modelHandle, int modelParam) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public void setParameter(int modelHandle, int modelParam, int value) throws RemoteException {
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHw
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISoundTriggerHw {
        static final int TRANSACTION_forceRecognitionEvent = 8;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getParameter = 10;
        static final int TRANSACTION_getProperties = 1;
        static final int TRANSACTION_loadPhraseSoundModel = 4;
        static final int TRANSACTION_loadSoundModel = 3;
        static final int TRANSACTION_queryParameter = 9;
        static final int TRANSACTION_registerGlobalCallback = 2;
        static final int TRANSACTION_setParameter = 11;
        static final int TRANSACTION_startRecognition = 6;
        static final int TRANSACTION_stopRecognition = 7;
        static final int TRANSACTION_unloadSoundModel = 5;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ISoundTriggerHw asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISoundTriggerHw)) {
                return (ISoundTriggerHw) iin;
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
                            Properties _result = getProperties();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            ISoundTriggerHwGlobalCallback _arg0 = ISoundTriggerHwGlobalCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerGlobalCallback(_arg0);
                            reply.writeNoException();
                            break;
                        case 3:
                            SoundModel _arg02 = (SoundModel) data.readTypedObject(SoundModel.CREATOR);
                            ISoundTriggerHwCallback _arg1 = ISoundTriggerHwCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result2 = loadSoundModel(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 4:
                            PhraseSoundModel _arg03 = (PhraseSoundModel) data.readTypedObject(PhraseSoundModel.CREATOR);
                            ISoundTriggerHwCallback _arg12 = ISoundTriggerHwCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result3 = loadPhraseSoundModel(_arg03, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            unloadSoundModel(_arg04);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            int _arg13 = data.readInt();
                            int _arg2 = data.readInt();
                            RecognitionConfig _arg3 = (RecognitionConfig) data.readTypedObject(RecognitionConfig.CREATOR);
                            data.enforceNoDataAvail();
                            startRecognition(_arg05, _arg13, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            stopRecognition(_arg06);
                            reply.writeNoException();
                            break;
                        case 8:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            forceRecognitionEvent(_arg07);
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            ModelParameterRange _result4 = queryParameter(_arg08, _arg14);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 10:
                            int _arg09 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result5 = getParameter(_arg09, _arg15);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 11:
                            int _arg010 = data.readInt();
                            int _arg16 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            setParameter(_arg010, _arg16, _arg22);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ISoundTriggerHw {
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

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public Properties getProperties() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getProperties is unimplemented.");
                    }
                    _reply.readException();
                    Properties _result = (Properties) _reply.readTypedObject(Properties.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public void registerGlobalCallback(ISoundTriggerHwGlobalCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method registerGlobalCallback is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public int loadSoundModel(SoundModel soundModel, ISoundTriggerHwCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(soundModel, 0);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method loadSoundModel is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public int loadPhraseSoundModel(PhraseSoundModel soundModel, ISoundTriggerHwCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(soundModel, 0);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method loadPhraseSoundModel is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public void unloadSoundModel(int modelHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method unloadSoundModel is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public void startRecognition(int modelHandle, int deviceHandle, int ioHandle, RecognitionConfig config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    _data.writeInt(deviceHandle);
                    _data.writeInt(ioHandle);
                    _data.writeTypedObject(config, 0);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method startRecognition is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public void stopRecognition(int modelHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method stopRecognition is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public void forceRecognitionEvent(int modelHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method forceRecognitionEvent is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public ModelParameterRange queryParameter(int modelHandle, int modelParam) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    _data.writeInt(modelParam);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method queryParameter is unimplemented.");
                    }
                    _reply.readException();
                    ModelParameterRange _result = (ModelParameterRange) _reply.readTypedObject(ModelParameterRange.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public int getParameter(int modelHandle, int modelParam) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    _data.writeInt(modelParam);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getParameter is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
            public void setParameter(int modelHandle, int modelParam, int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    _data.writeInt(modelParam);
                    _data.writeInt(value);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setParameter is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
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

            @Override // android.hardware.soundtrigger3.ISoundTriggerHw
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
