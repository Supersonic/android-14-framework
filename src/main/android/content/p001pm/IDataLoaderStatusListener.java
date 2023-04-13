package android.content.p001pm;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.content.pm.IDataLoaderStatusListener */
/* loaded from: classes.dex */
public interface IDataLoaderStatusListener extends IInterface {
    public static final int DATA_LOADER_BINDING = 1;
    public static final int DATA_LOADER_BOUND = 2;
    public static final int DATA_LOADER_CREATED = 3;
    public static final int DATA_LOADER_DESTROYED = 0;
    public static final int DATA_LOADER_IMAGE_NOT_READY = 7;
    public static final int DATA_LOADER_IMAGE_READY = 6;
    public static final int DATA_LOADER_STARTED = 4;
    public static final int DATA_LOADER_STOPPED = 5;
    public static final int DATA_LOADER_UNAVAILABLE = 8;
    public static final int DATA_LOADER_UNRECOVERABLE = 9;
    public static final String DESCRIPTOR = "android.content.pm.IDataLoaderStatusListener";

    void onStatusChanged(int i, int i2) throws RemoteException;

    /* renamed from: android.content.pm.IDataLoaderStatusListener$Default */
    /* loaded from: classes.dex */
    public static class Default implements IDataLoaderStatusListener {
        @Override // android.content.p001pm.IDataLoaderStatusListener
        public void onStatusChanged(int dataLoaderId, int status) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.pm.IDataLoaderStatusListener$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDataLoaderStatusListener {
        static final int TRANSACTION_onStatusChanged = 1;

        public Stub() {
            attachInterface(this, IDataLoaderStatusListener.DESCRIPTOR);
        }

        public static IDataLoaderStatusListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDataLoaderStatusListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IDataLoaderStatusListener)) {
                return (IDataLoaderStatusListener) iin;
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
                    return "onStatusChanged";
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
                data.enforceInterface(IDataLoaderStatusListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDataLoaderStatusListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onStatusChanged(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* renamed from: android.content.pm.IDataLoaderStatusListener$Stub$Proxy */
        /* loaded from: classes.dex */
        private static class Proxy implements IDataLoaderStatusListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDataLoaderStatusListener.DESCRIPTOR;
            }

            @Override // android.content.p001pm.IDataLoaderStatusListener
            public void onStatusChanged(int dataLoaderId, int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDataLoaderStatusListener.DESCRIPTOR);
                    _data.writeInt(dataLoaderId);
                    _data.writeInt(status);
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
