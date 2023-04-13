package android.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes3.dex */
public interface ICellInfoCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ICellInfoCallback";

    void onCellInfo(List<CellInfo> list) throws RemoteException;

    void onError(int i, String str, String str2) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ICellInfoCallback {
        @Override // android.telephony.ICellInfoCallback
        public void onCellInfo(List<CellInfo> state) throws RemoteException {
        }

        @Override // android.telephony.ICellInfoCallback
        public void onError(int errorCode, String exceptionName, String message) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ICellInfoCallback {
        static final int TRANSACTION_onCellInfo = 1;
        static final int TRANSACTION_onError = 2;

        public Stub() {
            attachInterface(this, ICellInfoCallback.DESCRIPTOR);
        }

        public static ICellInfoCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICellInfoCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ICellInfoCallback)) {
                return (ICellInfoCallback) iin;
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
                    return "onCellInfo";
                case 2:
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
                data.enforceInterface(ICellInfoCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICellInfoCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<CellInfo> _arg0 = data.createTypedArrayList(CellInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onCellInfo(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            onError(_arg02, _arg1, _arg2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ICellInfoCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICellInfoCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ICellInfoCallback
            public void onCellInfo(List<CellInfo> state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICellInfoCallback.DESCRIPTOR);
                    _data.writeTypedList(state, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ICellInfoCallback
            public void onError(int errorCode, String exceptionName, String message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICellInfoCallback.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    _data.writeString(exceptionName);
                    _data.writeString(message);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
