package android.speech.tts;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.speech.tts.ITextToSpeechSessionCallback;
/* loaded from: classes3.dex */
public interface ITextToSpeechManager extends IInterface {
    public static final String DESCRIPTOR = "android.speech.tts.ITextToSpeechManager";

    void createSession(String str, ITextToSpeechSessionCallback iTextToSpeechSessionCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITextToSpeechManager {
        @Override // android.speech.tts.ITextToSpeechManager
        public void createSession(String engine, ITextToSpeechSessionCallback managerCallback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITextToSpeechManager {
        static final int TRANSACTION_createSession = 1;

        public Stub() {
            attachInterface(this, ITextToSpeechManager.DESCRIPTOR);
        }

        public static ITextToSpeechManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITextToSpeechManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ITextToSpeechManager)) {
                return (ITextToSpeechManager) iin;
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
                    return "createSession";
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
                data.enforceInterface(ITextToSpeechManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITextToSpeechManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            ITextToSpeechSessionCallback _arg1 = ITextToSpeechSessionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            createSession(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITextToSpeechManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITextToSpeechManager.DESCRIPTOR;
            }

            @Override // android.speech.tts.ITextToSpeechManager
            public void createSession(String engine, ITextToSpeechSessionCallback managerCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITextToSpeechManager.DESCRIPTOR);
                    _data.writeString(engine);
                    _data.writeStrongInterface(managerCallback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
