package android.media.soundtrigger_middleware;

import android.media.soundtrigger.PhraseRecognitionEvent;
import android.media.soundtrigger.RecognitionEvent;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ISoundTriggerCallback extends IInterface {
    public static final String DESCRIPTOR = "android$media$soundtrigger_middleware$ISoundTriggerCallback".replace('$', '.');

    void onModelUnloaded(int i) throws RemoteException;

    void onModuleDied() throws RemoteException;

    void onPhraseRecognition(int i, PhraseRecognitionEvent phraseRecognitionEvent, int i2) throws RemoteException;

    void onRecognition(int i, RecognitionEvent recognitionEvent, int i2) throws RemoteException;

    void onResourcesAvailable() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISoundTriggerCallback {
        @Override // android.media.soundtrigger_middleware.ISoundTriggerCallback
        public void onRecognition(int modelHandle, RecognitionEvent event, int captureSession) throws RemoteException {
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerCallback
        public void onPhraseRecognition(int modelHandle, PhraseRecognitionEvent event, int captureSession) throws RemoteException {
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerCallback
        public void onResourcesAvailable() throws RemoteException {
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerCallback
        public void onModelUnloaded(int modelHandle) throws RemoteException {
        }

        @Override // android.media.soundtrigger_middleware.ISoundTriggerCallback
        public void onModuleDied() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISoundTriggerCallback {
        static final int TRANSACTION_onModelUnloaded = 4;
        static final int TRANSACTION_onModuleDied = 5;
        static final int TRANSACTION_onPhraseRecognition = 2;
        static final int TRANSACTION_onRecognition = 1;
        static final int TRANSACTION_onResourcesAvailable = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISoundTriggerCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISoundTriggerCallback)) {
                return (ISoundTriggerCallback) iin;
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
                            int _arg0 = data.readInt();
                            RecognitionEvent _arg1 = (RecognitionEvent) data.readTypedObject(RecognitionEvent.CREATOR);
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            onRecognition(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            PhraseRecognitionEvent _arg12 = (PhraseRecognitionEvent) data.readTypedObject(PhraseRecognitionEvent.CREATOR);
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            onPhraseRecognition(_arg02, _arg12, _arg22);
                            break;
                        case 3:
                            onResourcesAvailable();
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onModelUnloaded(_arg03);
                            break;
                        case 5:
                            onModuleDied();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ISoundTriggerCallback {
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

            @Override // android.media.soundtrigger_middleware.ISoundTriggerCallback
            public void onRecognition(int modelHandle, RecognitionEvent event, int captureSession) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    _data.writeTypedObject(event, 0);
                    _data.writeInt(captureSession);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerCallback
            public void onPhraseRecognition(int modelHandle, PhraseRecognitionEvent event, int captureSession) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    _data.writeTypedObject(event, 0);
                    _data.writeInt(captureSession);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerCallback
            public void onResourcesAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerCallback
            public void onModelUnloaded(int modelHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(modelHandle);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.soundtrigger_middleware.ISoundTriggerCallback
            public void onModuleDied() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
