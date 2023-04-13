package com.android.internal.app;

import android.content.ComponentName;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.SoundTrigger;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelUuid;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface ISoundTriggerSession extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.app.ISoundTriggerSession";

    void deleteSoundModel(ParcelUuid parcelUuid) throws RemoteException;

    int getModelState(ParcelUuid parcelUuid) throws RemoteException;

    SoundTrigger.ModuleProperties getModuleProperties() throws RemoteException;

    int getParameter(ParcelUuid parcelUuid, int i) throws RemoteException;

    SoundTrigger.GenericSoundModel getSoundModel(ParcelUuid parcelUuid) throws RemoteException;

    boolean isRecognitionActive(ParcelUuid parcelUuid) throws RemoteException;

    int loadGenericSoundModel(SoundTrigger.GenericSoundModel genericSoundModel) throws RemoteException;

    int loadKeyphraseSoundModel(SoundTrigger.KeyphraseSoundModel keyphraseSoundModel) throws RemoteException;

    SoundTrigger.ModelParamRange queryParameter(ParcelUuid parcelUuid, int i) throws RemoteException;

    int setParameter(ParcelUuid parcelUuid, int i, int i2) throws RemoteException;

    int startRecognition(SoundTrigger.GenericSoundModel genericSoundModel, IRecognitionStatusCallback iRecognitionStatusCallback, SoundTrigger.RecognitionConfig recognitionConfig, boolean z) throws RemoteException;

    int startRecognitionForService(ParcelUuid parcelUuid, Bundle bundle, ComponentName componentName, SoundTrigger.RecognitionConfig recognitionConfig) throws RemoteException;

    int stopRecognition(ParcelUuid parcelUuid, IRecognitionStatusCallback iRecognitionStatusCallback) throws RemoteException;

    int stopRecognitionForService(ParcelUuid parcelUuid) throws RemoteException;

    int unloadSoundModel(ParcelUuid parcelUuid) throws RemoteException;

    void updateSoundModel(SoundTrigger.GenericSoundModel genericSoundModel) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ISoundTriggerSession {
        @Override // com.android.internal.app.ISoundTriggerSession
        public SoundTrigger.GenericSoundModel getSoundModel(ParcelUuid soundModelId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public void updateSoundModel(SoundTrigger.GenericSoundModel soundModel) throws RemoteException {
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public void deleteSoundModel(ParcelUuid soundModelId) throws RemoteException {
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public int startRecognition(SoundTrigger.GenericSoundModel soundModel, IRecognitionStatusCallback callback, SoundTrigger.RecognitionConfig config, boolean runInBatterySaver) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public int stopRecognition(ParcelUuid soundModelId, IRecognitionStatusCallback callback) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public int loadGenericSoundModel(SoundTrigger.GenericSoundModel soundModel) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public int loadKeyphraseSoundModel(SoundTrigger.KeyphraseSoundModel soundModel) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public int startRecognitionForService(ParcelUuid soundModelId, Bundle params, ComponentName callbackIntent, SoundTrigger.RecognitionConfig config) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public int stopRecognitionForService(ParcelUuid soundModelId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public int unloadSoundModel(ParcelUuid soundModelId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public boolean isRecognitionActive(ParcelUuid parcelUuid) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public int getModelState(ParcelUuid soundModelId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public SoundTrigger.ModuleProperties getModuleProperties() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public int setParameter(ParcelUuid soundModelId, int modelParam, int value) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public int getParameter(ParcelUuid soundModelId, int modelParam) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.ISoundTriggerSession
        public SoundTrigger.ModelParamRange queryParameter(ParcelUuid soundModelId, int modelParam) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ISoundTriggerSession {
        static final int TRANSACTION_deleteSoundModel = 3;
        static final int TRANSACTION_getModelState = 12;
        static final int TRANSACTION_getModuleProperties = 13;
        static final int TRANSACTION_getParameter = 15;
        static final int TRANSACTION_getSoundModel = 1;
        static final int TRANSACTION_isRecognitionActive = 11;
        static final int TRANSACTION_loadGenericSoundModel = 6;
        static final int TRANSACTION_loadKeyphraseSoundModel = 7;
        static final int TRANSACTION_queryParameter = 16;
        static final int TRANSACTION_setParameter = 14;
        static final int TRANSACTION_startRecognition = 4;
        static final int TRANSACTION_startRecognitionForService = 8;
        static final int TRANSACTION_stopRecognition = 5;
        static final int TRANSACTION_stopRecognitionForService = 9;
        static final int TRANSACTION_unloadSoundModel = 10;
        static final int TRANSACTION_updateSoundModel = 2;

        public Stub() {
            attachInterface(this, ISoundTriggerSession.DESCRIPTOR);
        }

        public static ISoundTriggerSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISoundTriggerSession.DESCRIPTOR);
            if (iin != null && (iin instanceof ISoundTriggerSession)) {
                return (ISoundTriggerSession) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "getSoundModel";
                case 2:
                    return "updateSoundModel";
                case 3:
                    return "deleteSoundModel";
                case 4:
                    return "startRecognition";
                case 5:
                    return "stopRecognition";
                case 6:
                    return "loadGenericSoundModel";
                case 7:
                    return "loadKeyphraseSoundModel";
                case 8:
                    return "startRecognitionForService";
                case 9:
                    return "stopRecognitionForService";
                case 10:
                    return "unloadSoundModel";
                case 11:
                    return "isRecognitionActive";
                case 12:
                    return "getModelState";
                case 13:
                    return "getModuleProperties";
                case 14:
                    return "setParameter";
                case 15:
                    return "getParameter";
                case 16:
                    return "queryParameter";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(ISoundTriggerSession.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISoundTriggerSession.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ParcelUuid _arg0 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            data.enforceNoDataAvail();
                            SoundTrigger.GenericSoundModel _result = getSoundModel(_arg0);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            SoundTrigger.GenericSoundModel _arg02 = (SoundTrigger.GenericSoundModel) data.readTypedObject(SoundTrigger.GenericSoundModel.CREATOR);
                            data.enforceNoDataAvail();
                            updateSoundModel(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            ParcelUuid _arg03 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            data.enforceNoDataAvail();
                            deleteSoundModel(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            SoundTrigger.GenericSoundModel _arg04 = (SoundTrigger.GenericSoundModel) data.readTypedObject(SoundTrigger.GenericSoundModel.CREATOR);
                            IRecognitionStatusCallback _arg1 = IRecognitionStatusCallback.Stub.asInterface(data.readStrongBinder());
                            SoundTrigger.RecognitionConfig _arg2 = (SoundTrigger.RecognitionConfig) data.readTypedObject(SoundTrigger.RecognitionConfig.CREATOR);
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result2 = startRecognition(_arg04, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 5:
                            ParcelUuid _arg05 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            IRecognitionStatusCallback _arg12 = IRecognitionStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result3 = stopRecognition(_arg05, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 6:
                            SoundTrigger.GenericSoundModel _arg06 = (SoundTrigger.GenericSoundModel) data.readTypedObject(SoundTrigger.GenericSoundModel.CREATOR);
                            data.enforceNoDataAvail();
                            int _result4 = loadGenericSoundModel(_arg06);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 7:
                            SoundTrigger.KeyphraseSoundModel _arg07 = (SoundTrigger.KeyphraseSoundModel) data.readTypedObject(SoundTrigger.KeyphraseSoundModel.CREATOR);
                            data.enforceNoDataAvail();
                            int _result5 = loadKeyphraseSoundModel(_arg07);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 8:
                            ParcelUuid _arg08 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            Bundle _arg13 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            ComponentName _arg22 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            SoundTrigger.RecognitionConfig _arg32 = (SoundTrigger.RecognitionConfig) data.readTypedObject(SoundTrigger.RecognitionConfig.CREATOR);
                            data.enforceNoDataAvail();
                            int _result6 = startRecognitionForService(_arg08, _arg13, _arg22, _arg32);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 9:
                            ParcelUuid _arg09 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            data.enforceNoDataAvail();
                            int _result7 = stopRecognitionForService(_arg09);
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 10:
                            ParcelUuid _arg010 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            data.enforceNoDataAvail();
                            int _result8 = unloadSoundModel(_arg010);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 11:
                            ParcelUuid _arg011 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result9 = isRecognitionActive(_arg011);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 12:
                            ParcelUuid _arg012 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            data.enforceNoDataAvail();
                            int _result10 = getModelState(_arg012);
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            break;
                        case 13:
                            SoundTrigger.ModuleProperties _result11 = getModuleProperties();
                            reply.writeNoException();
                            reply.writeTypedObject(_result11, 1);
                            break;
                        case 14:
                            ParcelUuid _arg013 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            int _arg14 = data.readInt();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result12 = setParameter(_arg013, _arg14, _arg23);
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            break;
                        case 15:
                            ParcelUuid _arg014 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result13 = getParameter(_arg014, _arg15);
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            break;
                        case 16:
                            ParcelUuid _arg015 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            SoundTrigger.ModelParamRange _result14 = queryParameter(_arg015, _arg16);
                            reply.writeNoException();
                            reply.writeTypedObject(_result14, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements ISoundTriggerSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISoundTriggerSession.DESCRIPTOR;
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public SoundTrigger.GenericSoundModel getSoundModel(ParcelUuid soundModelId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModelId, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    SoundTrigger.GenericSoundModel _result = (SoundTrigger.GenericSoundModel) _reply.readTypedObject(SoundTrigger.GenericSoundModel.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public void updateSoundModel(SoundTrigger.GenericSoundModel soundModel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModel, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public void deleteSoundModel(ParcelUuid soundModelId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModelId, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public int startRecognition(SoundTrigger.GenericSoundModel soundModel, IRecognitionStatusCallback callback, SoundTrigger.RecognitionConfig config, boolean runInBatterySaver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModel, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeTypedObject(config, 0);
                    _data.writeBoolean(runInBatterySaver);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public int stopRecognition(ParcelUuid soundModelId, IRecognitionStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModelId, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public int loadGenericSoundModel(SoundTrigger.GenericSoundModel soundModel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModel, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public int loadKeyphraseSoundModel(SoundTrigger.KeyphraseSoundModel soundModel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModel, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public int startRecognitionForService(ParcelUuid soundModelId, Bundle params, ComponentName callbackIntent, SoundTrigger.RecognitionConfig config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModelId, 0);
                    _data.writeTypedObject(params, 0);
                    _data.writeTypedObject(callbackIntent, 0);
                    _data.writeTypedObject(config, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public int stopRecognitionForService(ParcelUuid soundModelId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModelId, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public int unloadSoundModel(ParcelUuid soundModelId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModelId, 0);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public boolean isRecognitionActive(ParcelUuid parcelUuid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(parcelUuid, 0);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public int getModelState(ParcelUuid soundModelId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModelId, 0);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public SoundTrigger.ModuleProperties getModuleProperties() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    SoundTrigger.ModuleProperties _result = (SoundTrigger.ModuleProperties) _reply.readTypedObject(SoundTrigger.ModuleProperties.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public int setParameter(ParcelUuid soundModelId, int modelParam, int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModelId, 0);
                    _data.writeInt(modelParam);
                    _data.writeInt(value);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public int getParameter(ParcelUuid soundModelId, int modelParam) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModelId, 0);
                    _data.writeInt(modelParam);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ISoundTriggerSession
            public SoundTrigger.ModelParamRange queryParameter(ParcelUuid soundModelId, int modelParam) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISoundTriggerSession.DESCRIPTOR);
                    _data.writeTypedObject(soundModelId, 0);
                    _data.writeInt(modelParam);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    SoundTrigger.ModelParamRange _result = (SoundTrigger.ModelParamRange) _reply.readTypedObject(SoundTrigger.ModelParamRange.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 15;
        }
    }
}
