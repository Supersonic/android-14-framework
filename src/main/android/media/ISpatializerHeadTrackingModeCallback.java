package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ISpatializerHeadTrackingModeCallback extends IInterface {
    public static final String DESCRIPTOR = "android.media.ISpatializerHeadTrackingModeCallback";

    void dispatchSpatializerActualHeadTrackingModeChanged(int i) throws RemoteException;

    void dispatchSpatializerDesiredHeadTrackingModeChanged(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISpatializerHeadTrackingModeCallback {
        @Override // android.media.ISpatializerHeadTrackingModeCallback
        public void dispatchSpatializerActualHeadTrackingModeChanged(int mode) throws RemoteException {
        }

        @Override // android.media.ISpatializerHeadTrackingModeCallback
        public void dispatchSpatializerDesiredHeadTrackingModeChanged(int mode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISpatializerHeadTrackingModeCallback {
        static final int TRANSACTION_dispatchSpatializerActualHeadTrackingModeChanged = 1;
        static final int TRANSACTION_dispatchSpatializerDesiredHeadTrackingModeChanged = 2;

        public Stub() {
            attachInterface(this, ISpatializerHeadTrackingModeCallback.DESCRIPTOR);
        }

        public static ISpatializerHeadTrackingModeCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISpatializerHeadTrackingModeCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISpatializerHeadTrackingModeCallback)) {
                return (ISpatializerHeadTrackingModeCallback) iin;
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
                    return "dispatchSpatializerActualHeadTrackingModeChanged";
                case 2:
                    return "dispatchSpatializerDesiredHeadTrackingModeChanged";
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
                data.enforceInterface(ISpatializerHeadTrackingModeCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISpatializerHeadTrackingModeCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchSpatializerActualHeadTrackingModeChanged(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchSpatializerDesiredHeadTrackingModeChanged(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ISpatializerHeadTrackingModeCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISpatializerHeadTrackingModeCallback.DESCRIPTOR;
            }

            @Override // android.media.ISpatializerHeadTrackingModeCallback
            public void dispatchSpatializerActualHeadTrackingModeChanged(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISpatializerHeadTrackingModeCallback.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.ISpatializerHeadTrackingModeCallback
            public void dispatchSpatializerDesiredHeadTrackingModeChanged(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISpatializerHeadTrackingModeCallback.DESCRIPTOR);
                    _data.writeInt(mode);
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
