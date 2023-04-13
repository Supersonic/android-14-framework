package android.media.soundtrigger_middleware;

import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.SoundModel;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ISoundTriggerModule extends IInterface {
    public static final String DESCRIPTOR = "android$media$soundtrigger_middleware$ISoundTriggerModule".replace('$', '.');

    void detach() throws RemoteException;

    void forceRecognitionEvent(int i) throws RemoteException;

    int getModelParameter(int i, int i2) throws RemoteException;

    int loadModel(SoundModel soundModel) throws RemoteException;

    int loadPhraseModel(PhraseSoundModel phraseSoundModel) throws RemoteException;

    ModelParameterRange queryModelParameterSupport(int i, int i2) throws RemoteException;

    void setModelParameter(int i, int i2, int i3) throws RemoteException;

    void startRecognition(int i, RecognitionConfig recognitionConfig) throws RemoteException;

    void stopRecognition(int i) throws RemoteException;

    void unloadModel(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISoundTriggerModule {
        @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
        public int loadModel(SoundModel model) throws RemoteException {
            return 0;
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
        public int loadPhraseModel(PhraseSoundModel model) throws RemoteException {
            return 0;
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
        public void unloadModel(int modelHandle) throws RemoteException {
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
        public void startRecognition(int modelHandle, RecognitionConfig config) throws RemoteException {
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
        public void stopRecognition(int modelHandle) throws RemoteException {
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
        public void forceRecognitionEvent(int modelHandle) throws RemoteException {
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
        public void setModelParameter(int modelHandle, int modelParam, int value) throws RemoteException {
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
        public int getModelParameter(int modelHandle, int modelParam) throws RemoteException {
            return 0;
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
        public ModelParameterRange queryModelParameterSupport(int modelHandle, int modelParam) throws RemoteException {
            return null;
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
        public void detach() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISoundTriggerModule {
        static final int TRANSACTION_detach = 10;
        static final int TRANSACTION_forceRecognitionEvent = 6;
        static final int TRANSACTION_getModelParameter = 8;
        static final int TRANSACTION_loadModel = 1;
        static final int TRANSACTION_loadPhraseModel = 2;
        static final int TRANSACTION_queryModelParameterSupport = 9;
        static final int TRANSACTION_setModelParameter = 7;
        static final int TRANSACTION_startRecognition = 4;
        static final int TRANSACTION_stopRecognition = 5;
        static final int TRANSACTION_unloadModel = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISoundTriggerModule asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISoundTriggerModule)) {
                return (ISoundTriggerModule) iin;
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
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SoundModel _arg0 = (SoundModel) data.readTypedObject(SoundModel.CREATOR);
                            data.enforceNoDataAvail();
                            int _result = loadModel(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            PhraseSoundModel _arg02 = (PhraseSoundModel) data.readTypedObject(PhraseSoundModel.CREATOR);
                            data.enforceNoDataAvail();
                            int _result2 = loadPhraseModel(_arg02);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            unloadModel(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            RecognitionConfig _arg1 = (RecognitionConfig) data.readTypedObject(RecognitionConfig.CREATOR);
                            data.enforceNoDataAvail();
                            startRecognition(_arg04, _arg1);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            stopRecognition(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            forceRecognitionEvent(_arg06);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            int _arg12 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            setModelParameter(_arg07, _arg12, _arg2);
                            reply.writeNoException();
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result3 = getModelParameter(_arg08, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            ModelParameterRange _result4 = queryModelParameterSupport(_arg09, _arg14);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 10:
                            detach();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ISoundTriggerModule {
            private IBinder mRemote;

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

            @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
            public int loadModel(SoundModel model) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(model, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
            public int loadPhraseModel(PhraseSoundModel model) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(model, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
            public void unloadModel(int modelHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
            public void startRecognition(int modelHandle, RecognitionConfig config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    _data.writeTypedObject(config, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
            public void stopRecognition(int modelHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
            public void forceRecognitionEvent(int modelHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
            public void setModelParameter(int modelHandle, int modelParam, int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    _data.writeInt(modelParam);
                    _data.writeInt(value);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
            public int getModelParameter(int modelHandle, int modelParam) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    _data.writeInt(modelParam);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
            public ModelParameterRange queryModelParameterSupport(int modelHandle, int modelParam) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    _data.writeInt(modelParam);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    ModelParameterRange _result = (ModelParameterRange) _reply.readTypedObject(ModelParameterRange.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerModule
            public void detach() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
