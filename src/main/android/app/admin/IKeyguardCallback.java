package android.app.admin;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.SurfaceControlViewHost;
/* loaded from: classes.dex */
public interface IKeyguardCallback extends IInterface {
    public static final String DESCRIPTOR = "android.app.admin.IKeyguardCallback";

    void onDismiss() throws RemoteException;

    void onRemoteContentReady(SurfaceControlViewHost.SurfacePackage surfacePackage) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IKeyguardCallback {
        @Override // android.app.admin.IKeyguardCallback
        public void onRemoteContentReady(SurfaceControlViewHost.SurfacePackage surfacePackage) throws RemoteException {
        }

        @Override // android.app.admin.IKeyguardCallback
        public void onDismiss() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IKeyguardCallback {
        static final int TRANSACTION_onDismiss = 2;
        static final int TRANSACTION_onRemoteContentReady = 1;

        public Stub() {
            attachInterface(this, IKeyguardCallback.DESCRIPTOR);
        }

        public static IKeyguardCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IKeyguardCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IKeyguardCallback)) {
                return (IKeyguardCallback) iin;
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
                    return "onRemoteContentReady";
                case 2:
                    return "onDismiss";
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
                data.enforceInterface(IKeyguardCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IKeyguardCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SurfaceControlViewHost.SurfacePackage _arg0 = (SurfaceControlViewHost.SurfacePackage) data.readTypedObject(SurfaceControlViewHost.SurfacePackage.CREATOR);
                            data.enforceNoDataAvail();
                            onRemoteContentReady(_arg0);
                            break;
                        case 2:
                            onDismiss();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IKeyguardCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IKeyguardCallback.DESCRIPTOR;
            }

            @Override // android.app.admin.IKeyguardCallback
            public void onRemoteContentReady(SurfaceControlViewHost.SurfacePackage surfacePackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IKeyguardCallback.DESCRIPTOR);
                    _data.writeTypedObject(surfacePackage, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.admin.IKeyguardCallback
            public void onDismiss() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IKeyguardCallback.DESCRIPTOR);
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
