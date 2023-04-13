package android.telephony.mbms;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IStreamingServiceCallback extends IInterface {
    void onBroadcastSignalStrengthUpdated(int i) throws RemoteException;

    void onError(int i, String str) throws RemoteException;

    void onMediaDescriptionUpdated() throws RemoteException;

    void onStreamMethodUpdated(int i) throws RemoteException;

    void onStreamStateUpdated(int i, int i2) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IStreamingServiceCallback {
        @Override // android.telephony.mbms.IStreamingServiceCallback
        public void onError(int errorCode, String message) throws RemoteException {
        }

        @Override // android.telephony.mbms.IStreamingServiceCallback
        public void onStreamStateUpdated(int state, int reason) throws RemoteException {
        }

        @Override // android.telephony.mbms.IStreamingServiceCallback
        public void onMediaDescriptionUpdated() throws RemoteException {
        }

        @Override // android.telephony.mbms.IStreamingServiceCallback
        public void onBroadcastSignalStrengthUpdated(int signalStrength) throws RemoteException {
        }

        @Override // android.telephony.mbms.IStreamingServiceCallback
        public void onStreamMethodUpdated(int methodType) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IStreamingServiceCallback {
        public static final String DESCRIPTOR = "android.telephony.mbms.IStreamingServiceCallback";
        static final int TRANSACTION_onBroadcastSignalStrengthUpdated = 4;
        static final int TRANSACTION_onError = 1;
        static final int TRANSACTION_onMediaDescriptionUpdated = 3;
        static final int TRANSACTION_onStreamMethodUpdated = 5;
        static final int TRANSACTION_onStreamStateUpdated = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IStreamingServiceCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IStreamingServiceCallback)) {
                return (IStreamingServiceCallback) iin;
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
                    return "onError";
                case 2:
                    return "onStreamStateUpdated";
                case 3:
                    return "onMediaDescriptionUpdated";
                case 4:
                    return "onBroadcastSignalStrengthUpdated";
                case 5:
                    return "onStreamMethodUpdated";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            onError(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onStreamStateUpdated(_arg02, _arg12);
                            break;
                        case 3:
                            onMediaDescriptionUpdated();
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onBroadcastSignalStrengthUpdated(_arg03);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            onStreamMethodUpdated(_arg04);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IStreamingServiceCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.telephony.mbms.IStreamingServiceCallback
            public void onError(int errorCode, String message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    _data.writeString(message);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.IStreamingServiceCallback
            public void onStreamStateUpdated(int state, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeInt(reason);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.IStreamingServiceCallback
            public void onMediaDescriptionUpdated() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.IStreamingServiceCallback
            public void onBroadcastSignalStrengthUpdated(int signalStrength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(signalStrength);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.IStreamingServiceCallback
            public void onStreamMethodUpdated(int methodType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(methodType);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
