package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ISpatializerHeadTrackerAvailableCallback extends IInterface {
    public static final String DESCRIPTOR = "android.media.ISpatializerHeadTrackerAvailableCallback";

    void dispatchSpatializerHeadTrackerAvailable(boolean z) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISpatializerHeadTrackerAvailableCallback {
        @Override // android.media.ISpatializerHeadTrackerAvailableCallback
        public void dispatchSpatializerHeadTrackerAvailable(boolean available) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISpatializerHeadTrackerAvailableCallback {
        static final int TRANSACTION_dispatchSpatializerHeadTrackerAvailable = 1;

        public Stub() {
            attachInterface(this, ISpatializerHeadTrackerAvailableCallback.DESCRIPTOR);
        }

        public static ISpatializerHeadTrackerAvailableCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISpatializerHeadTrackerAvailableCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISpatializerHeadTrackerAvailableCallback)) {
                return (ISpatializerHeadTrackerAvailableCallback) iin;
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
                    return "dispatchSpatializerHeadTrackerAvailable";
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
                data.enforceInterface(ISpatializerHeadTrackerAvailableCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISpatializerHeadTrackerAvailableCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            dispatchSpatializerHeadTrackerAvailable(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ISpatializerHeadTrackerAvailableCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISpatializerHeadTrackerAvailableCallback.DESCRIPTOR;
            }

            @Override // android.media.ISpatializerHeadTrackerAvailableCallback
            public void dispatchSpatializerHeadTrackerAvailable(boolean available) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISpatializerHeadTrackerAvailableCallback.DESCRIPTOR);
                    _data.writeBoolean(available);
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
