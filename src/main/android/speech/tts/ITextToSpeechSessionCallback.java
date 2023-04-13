package android.speech.tts;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.speech.tts.ITextToSpeechSession;
/* loaded from: classes3.dex */
public interface ITextToSpeechSessionCallback extends IInterface {
    public static final String DESCRIPTOR = "android.speech.tts.ITextToSpeechSessionCallback";

    void onConnected(ITextToSpeechSession iTextToSpeechSession, IBinder iBinder) throws RemoteException;

    void onDisconnected() throws RemoteException;

    void onError(String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITextToSpeechSessionCallback {
        @Override // android.speech.tts.ITextToSpeechSessionCallback
        public void onConnected(ITextToSpeechSession session, IBinder serviceBinder) throws RemoteException {
        }

        @Override // android.speech.tts.ITextToSpeechSessionCallback
        public void onDisconnected() throws RemoteException {
        }

        @Override // android.speech.tts.ITextToSpeechSessionCallback
        public void onError(String errorInfo) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITextToSpeechSessionCallback {
        static final int TRANSACTION_onConnected = 1;
        static final int TRANSACTION_onDisconnected = 2;
        static final int TRANSACTION_onError = 3;

        public Stub() {
            attachInterface(this, ITextToSpeechSessionCallback.DESCRIPTOR);
        }

        public static ITextToSpeechSessionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITextToSpeechSessionCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ITextToSpeechSessionCallback)) {
                return (ITextToSpeechSessionCallback) iin;
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
                    return "onConnected";
                case 2:
                    return "onDisconnected";
                case 3:
                    return "onError";
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
                data.enforceInterface(ITextToSpeechSessionCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITextToSpeechSessionCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ITextToSpeechSession _arg0 = ITextToSpeechSession.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg1 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            onConnected(_arg0, _arg1);
                            break;
                        case 2:
                            onDisconnected();
                            break;
                        case 3:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            onError(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ITextToSpeechSessionCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITextToSpeechSessionCallback.DESCRIPTOR;
            }

            @Override // android.speech.tts.ITextToSpeechSessionCallback
            public void onConnected(ITextToSpeechSession session, IBinder serviceBinder) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITextToSpeechSessionCallback.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeStrongBinder(serviceBinder);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechSessionCallback
            public void onDisconnected() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITextToSpeechSessionCallback.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechSessionCallback
            public void onError(String errorInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITextToSpeechSessionCallback.DESCRIPTOR);
                    _data.writeString(errorInfo);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
