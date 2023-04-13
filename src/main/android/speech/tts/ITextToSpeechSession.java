package android.speech.tts;

import android.media.MediaMetrics;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ITextToSpeechSession extends IInterface {
    public static final String DESCRIPTOR = "android.speech.tts.ITextToSpeechSession";

    void disconnect() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITextToSpeechSession {
        @Override // android.speech.tts.ITextToSpeechSession
        public void disconnect() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITextToSpeechSession {
        static final int TRANSACTION_disconnect = 1;

        public Stub() {
            attachInterface(this, ITextToSpeechSession.DESCRIPTOR);
        }

        public static ITextToSpeechSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITextToSpeechSession.DESCRIPTOR);
            if (iin != null && (iin instanceof ITextToSpeechSession)) {
                return (ITextToSpeechSession) iin;
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
                    return MediaMetrics.Value.DISCONNECT;
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
                data.enforceInterface(ITextToSpeechSession.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITextToSpeechSession.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            disconnect();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITextToSpeechSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITextToSpeechSession.DESCRIPTOR;
            }

            @Override // android.speech.tts.ITextToSpeechSession
            public void disconnect() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITextToSpeechSession.DESCRIPTOR);
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
