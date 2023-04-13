package com.android.internal.app;

import android.hardware.soundtrigger.SoundTrigger;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
/* loaded from: classes4.dex */
public interface IVoiceInteractionSoundTriggerSession extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.app.IVoiceInteractionSoundTriggerSession";

    SoundTrigger.ModuleProperties getDspModuleProperties() throws RemoteException;

    int getParameter(int i, int i2) throws RemoteException;

    SoundTrigger.ModelParamRange queryParameter(int i, int i2) throws RemoteException;

    int setParameter(int i, int i2, int i3) throws RemoteException;

    int startRecognition(int i, String str, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback, SoundTrigger.RecognitionConfig recognitionConfig, boolean z) throws RemoteException;

    int stopRecognition(int i, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IVoiceInteractionSoundTriggerSession {
        @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
        public SoundTrigger.ModuleProperties getDspModuleProperties() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
        public int startRecognition(int keyphraseId, String bcp47Locale, IHotwordRecognitionStatusCallback callback, SoundTrigger.RecognitionConfig recognitionConfig, boolean runInBatterySaver) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
        public int stopRecognition(int keyphraseId, IHotwordRecognitionStatusCallback callback) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
        public int setParameter(int keyphraseId, int modelParam, int value) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
        public int getParameter(int keyphraseId, int modelParam) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
        public SoundTrigger.ModelParamRange queryParameter(int keyphraseId, int modelParam) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IVoiceInteractionSoundTriggerSession {
        static final int TRANSACTION_getDspModuleProperties = 1;
        static final int TRANSACTION_getParameter = 5;
        static final int TRANSACTION_queryParameter = 6;
        static final int TRANSACTION_setParameter = 4;
        static final int TRANSACTION_startRecognition = 2;
        static final int TRANSACTION_stopRecognition = 3;

        public Stub() {
            attachInterface(this, IVoiceInteractionSoundTriggerSession.DESCRIPTOR);
        }

        public static IVoiceInteractionSoundTriggerSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IVoiceInteractionSoundTriggerSession.DESCRIPTOR);
            if (iin != null && (iin instanceof IVoiceInteractionSoundTriggerSession)) {
                return (IVoiceInteractionSoundTriggerSession) iin;
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
                    return "getDspModuleProperties";
                case 2:
                    return "startRecognition";
                case 3:
                    return "stopRecognition";
                case 4:
                    return "setParameter";
                case 5:
                    return "getParameter";
                case 6:
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
                data.enforceInterface(IVoiceInteractionSoundTriggerSession.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IVoiceInteractionSoundTriggerSession.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SoundTrigger.ModuleProperties _result = getDspModuleProperties();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            int _arg0 = data.readInt();
                            String _arg1 = data.readString();
                            IHotwordRecognitionStatusCallback _arg2 = IHotwordRecognitionStatusCallback.Stub.asInterface(data.readStrongBinder());
                            SoundTrigger.RecognitionConfig _arg3 = (SoundTrigger.RecognitionConfig) data.readTypedObject(SoundTrigger.RecognitionConfig.CREATOR);
                            boolean _arg4 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result2 = startRecognition(_arg0, _arg1, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            int _arg02 = data.readInt();
                            IHotwordRecognitionStatusCallback _arg12 = IHotwordRecognitionStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result3 = stopRecognition(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            int _arg13 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result4 = setParameter(_arg03, _arg13, _arg22);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result5 = getParameter(_arg04, _arg14);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            SoundTrigger.ModelParamRange _result6 = queryParameter(_arg05, _arg15);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IVoiceInteractionSoundTriggerSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IVoiceInteractionSoundTriggerSession.DESCRIPTOR;
            }

            @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
            public SoundTrigger.ModuleProperties getDspModuleProperties() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVoiceInteractionSoundTriggerSession.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    SoundTrigger.ModuleProperties _result = (SoundTrigger.ModuleProperties) _reply.readTypedObject(SoundTrigger.ModuleProperties.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
            public int startRecognition(int keyphraseId, String bcp47Locale, IHotwordRecognitionStatusCallback callback, SoundTrigger.RecognitionConfig recognitionConfig, boolean runInBatterySaver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVoiceInteractionSoundTriggerSession.DESCRIPTOR);
                    _data.writeInt(keyphraseId);
                    _data.writeString(bcp47Locale);
                    _data.writeStrongInterface(callback);
                    _data.writeTypedObject(recognitionConfig, 0);
                    _data.writeBoolean(runInBatterySaver);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
            public int stopRecognition(int keyphraseId, IHotwordRecognitionStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVoiceInteractionSoundTriggerSession.DESCRIPTOR);
                    _data.writeInt(keyphraseId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
            public int setParameter(int keyphraseId, int modelParam, int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVoiceInteractionSoundTriggerSession.DESCRIPTOR);
                    _data.writeInt(keyphraseId);
                    _data.writeInt(modelParam);
                    _data.writeInt(value);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
            public int getParameter(int keyphraseId, int modelParam) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVoiceInteractionSoundTriggerSession.DESCRIPTOR);
                    _data.writeInt(keyphraseId);
                    _data.writeInt(modelParam);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IVoiceInteractionSoundTriggerSession
            public SoundTrigger.ModelParamRange queryParameter(int keyphraseId, int modelParam) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVoiceInteractionSoundTriggerSession.DESCRIPTOR);
                    _data.writeInt(keyphraseId);
                    _data.writeInt(modelParam);
                    this.mRemote.transact(6, _data, _reply, 0);
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
            return 5;
        }
    }
}
