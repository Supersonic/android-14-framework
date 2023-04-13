package android.media.musicrecognition;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMusicRecognitionManager extends IInterface {
    public static final String DESCRIPTOR = "android.media.musicrecognition.IMusicRecognitionManager";

    void beginRecognition(RecognitionRequest recognitionRequest, IBinder iBinder) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMusicRecognitionManager {
        @Override // android.media.musicrecognition.IMusicRecognitionManager
        public void beginRecognition(RecognitionRequest recognitionRequest, IBinder callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMusicRecognitionManager {
        static final int TRANSACTION_beginRecognition = 1;

        public Stub() {
            attachInterface(this, IMusicRecognitionManager.DESCRIPTOR);
        }

        public static IMusicRecognitionManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IMusicRecognitionManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IMusicRecognitionManager)) {
                return (IMusicRecognitionManager) iin;
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
                    return "beginRecognition";
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
                data.enforceInterface(IMusicRecognitionManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IMusicRecognitionManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            RecognitionRequest _arg0 = (RecognitionRequest) data.readTypedObject(RecognitionRequest.CREATOR);
                            IBinder _arg1 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            beginRecognition(_arg0, _arg1);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMusicRecognitionManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMusicRecognitionManager.DESCRIPTOR;
            }

            @Override // android.media.musicrecognition.IMusicRecognitionManager
            public void beginRecognition(RecognitionRequest recognitionRequest, IBinder callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMusicRecognitionManager.DESCRIPTOR);
                    _data.writeTypedObject(recognitionRequest, 0);
                    _data.writeStrongBinder(callback);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
