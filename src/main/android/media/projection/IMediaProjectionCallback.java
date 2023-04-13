package android.media.projection;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMediaProjectionCallback extends IInterface {
    void onCapturedContentResize(int i, int i2) throws RemoteException;

    void onCapturedContentVisibilityChanged(boolean z) throws RemoteException;

    void onStop() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMediaProjectionCallback {
        @Override // android.media.projection.IMediaProjectionCallback
        public void onStop() throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjectionCallback
        public void onCapturedContentResize(int width, int height) throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjectionCallback
        public void onCapturedContentVisibilityChanged(boolean isVisible) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMediaProjectionCallback {
        public static final String DESCRIPTOR = "android.media.projection.IMediaProjectionCallback";
        static final int TRANSACTION_onCapturedContentResize = 2;
        static final int TRANSACTION_onCapturedContentVisibilityChanged = 3;
        static final int TRANSACTION_onStop = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaProjectionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaProjectionCallback)) {
                return (IMediaProjectionCallback) iin;
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
                    return "onStop";
                case 2:
                    return "onCapturedContentResize";
                case 3:
                    return "onCapturedContentVisibilityChanged";
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
                            onStop();
                            break;
                        case 2:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onCapturedContentResize(_arg0, _arg1);
                            break;
                        case 3:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onCapturedContentVisibilityChanged(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMediaProjectionCallback {
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

            @Override // android.media.projection.IMediaProjectionCallback
            public void onStop() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionCallback
            public void onCapturedContentResize(int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionCallback
            public void onCapturedContentVisibilityChanged(boolean isVisible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isVisible);
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
