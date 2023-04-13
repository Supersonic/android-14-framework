package android.app;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IParcelFileDescriptorRetriever extends IInterface {
    public static final String DESCRIPTOR = "android.app.IParcelFileDescriptorRetriever";

    ParcelFileDescriptor getPfd() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IParcelFileDescriptorRetriever {
        @Override // android.app.IParcelFileDescriptorRetriever
        public ParcelFileDescriptor getPfd() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IParcelFileDescriptorRetriever {
        static final int TRANSACTION_getPfd = 1;

        public Stub() {
            attachInterface(this, IParcelFileDescriptorRetriever.DESCRIPTOR);
        }

        public static IParcelFileDescriptorRetriever asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IParcelFileDescriptorRetriever.DESCRIPTOR);
            if (iin != null && (iin instanceof IParcelFileDescriptorRetriever)) {
                return (IParcelFileDescriptorRetriever) iin;
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
                    return "getPfd";
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
                data.enforceInterface(IParcelFileDescriptorRetriever.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IParcelFileDescriptorRetriever.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ParcelFileDescriptor _result = getPfd();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IParcelFileDescriptorRetriever {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IParcelFileDescriptorRetriever.DESCRIPTOR;
            }

            @Override // android.app.IParcelFileDescriptorRetriever
            public ParcelFileDescriptor getPfd() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IParcelFileDescriptorRetriever.DESCRIPTOR);
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
