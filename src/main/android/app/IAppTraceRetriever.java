package android.app;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IAppTraceRetriever extends IInterface {
    public static final String DESCRIPTOR = "android.app.IAppTraceRetriever";

    ParcelFileDescriptor getTraceFileDescriptor(String str, int i, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAppTraceRetriever {
        @Override // android.app.IAppTraceRetriever
        public ParcelFileDescriptor getTraceFileDescriptor(String packageName, int uid, int pid) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAppTraceRetriever {
        static final int TRANSACTION_getTraceFileDescriptor = 1;

        public Stub() {
            attachInterface(this, IAppTraceRetriever.DESCRIPTOR);
        }

        public static IAppTraceRetriever asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAppTraceRetriever.DESCRIPTOR);
            if (iin != null && (iin instanceof IAppTraceRetriever)) {
                return (IAppTraceRetriever) iin;
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
                    return "getTraceFileDescriptor";
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
                data.enforceInterface(IAppTraceRetriever.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAppTraceRetriever.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result = getTraceFileDescriptor(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IAppTraceRetriever {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAppTraceRetriever.DESCRIPTOR;
            }

            @Override // android.app.IAppTraceRetriever
            public ParcelFileDescriptor getTraceFileDescriptor(String packageName, int uid, int pid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppTraceRetriever.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(uid);
                    _data.writeInt(pid);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
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
