package android.service.contentcapture;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IDataShareReadAdapter extends IInterface {
    public static final String DESCRIPTOR = "android.service.contentcapture.IDataShareReadAdapter";

    void error(int i) throws RemoteException;

    void finish() throws RemoteException;

    void start(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IDataShareReadAdapter {
        @Override // android.service.contentcapture.IDataShareReadAdapter
        public void start(ParcelFileDescriptor fd) throws RemoteException {
        }

        @Override // android.service.contentcapture.IDataShareReadAdapter
        public void error(int errorCode) throws RemoteException {
        }

        @Override // android.service.contentcapture.IDataShareReadAdapter
        public void finish() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IDataShareReadAdapter {
        static final int TRANSACTION_error = 2;
        static final int TRANSACTION_finish = 3;
        static final int TRANSACTION_start = 1;

        public Stub() {
            attachInterface(this, IDataShareReadAdapter.DESCRIPTOR);
        }

        public static IDataShareReadAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDataShareReadAdapter.DESCRIPTOR);
            if (iin != null && (iin instanceof IDataShareReadAdapter)) {
                return (IDataShareReadAdapter) iin;
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
                    return "start";
                case 2:
                    return "error";
                case 3:
                    return "finish";
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
                data.enforceInterface(IDataShareReadAdapter.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDataShareReadAdapter.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ParcelFileDescriptor _arg0 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            start(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            error(_arg02);
                            break;
                        case 3:
                            finish();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IDataShareReadAdapter {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDataShareReadAdapter.DESCRIPTOR;
            }

            @Override // android.service.contentcapture.IDataShareReadAdapter
            public void start(ParcelFileDescriptor fd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDataShareReadAdapter.DESCRIPTOR);
                    _data.writeTypedObject(fd, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IDataShareReadAdapter
            public void error(int errorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDataShareReadAdapter.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.contentcapture.IDataShareReadAdapter
            public void finish() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDataShareReadAdapter.DESCRIPTOR);
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
