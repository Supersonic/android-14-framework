package android.telephony.mbms;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IGroupCallCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.mbms.IGroupCallCallback";

    void onBroadcastSignalStrengthUpdated(int i) throws RemoteException;

    void onError(int i, String str) throws RemoteException;

    void onGroupCallStateChanged(int i, int i2) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IGroupCallCallback {
        @Override // android.telephony.mbms.IGroupCallCallback
        public void onError(int errorCode, String message) throws RemoteException {
        }

        @Override // android.telephony.mbms.IGroupCallCallback
        public void onGroupCallStateChanged(int state, int reason) throws RemoteException {
        }

        @Override // android.telephony.mbms.IGroupCallCallback
        public void onBroadcastSignalStrengthUpdated(int signalStrength) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IGroupCallCallback {
        static final int TRANSACTION_onBroadcastSignalStrengthUpdated = 3;
        static final int TRANSACTION_onError = 1;
        static final int TRANSACTION_onGroupCallStateChanged = 2;

        public Stub() {
            attachInterface(this, IGroupCallCallback.DESCRIPTOR);
        }

        public static IGroupCallCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGroupCallCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IGroupCallCallback)) {
                return (IGroupCallCallback) iin;
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
                    return "onGroupCallStateChanged";
                case 3:
                    return "onBroadcastSignalStrengthUpdated";
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
                data.enforceInterface(IGroupCallCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IGroupCallCallback.DESCRIPTOR);
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
                            onGroupCallStateChanged(_arg02, _arg12);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onBroadcastSignalStrengthUpdated(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IGroupCallCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGroupCallCallback.DESCRIPTOR;
            }

            @Override // android.telephony.mbms.IGroupCallCallback
            public void onError(int errorCode, String message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGroupCallCallback.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    _data.writeString(message);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.IGroupCallCallback
            public void onGroupCallStateChanged(int state, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGroupCallCallback.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeInt(reason);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.IGroupCallCallback
            public void onBroadcastSignalStrengthUpdated(int signalStrength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGroupCallCallback.DESCRIPTOR);
                    _data.writeInt(signalStrength);
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
