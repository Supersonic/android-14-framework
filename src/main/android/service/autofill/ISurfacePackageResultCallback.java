package android.service.autofill;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.SurfaceControlViewHost;
/* loaded from: classes3.dex */
public interface ISurfacePackageResultCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.autofill.ISurfacePackageResultCallback";

    void onResult(SurfaceControlViewHost.SurfacePackage surfacePackage) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISurfacePackageResultCallback {
        @Override // android.service.autofill.ISurfacePackageResultCallback
        public void onResult(SurfaceControlViewHost.SurfacePackage result) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISurfacePackageResultCallback {
        static final int TRANSACTION_onResult = 1;

        public Stub() {
            attachInterface(this, ISurfacePackageResultCallback.DESCRIPTOR);
        }

        public static ISurfacePackageResultCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISurfacePackageResultCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISurfacePackageResultCallback)) {
                return (ISurfacePackageResultCallback) iin;
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
                    return "onResult";
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
                data.enforceInterface(ISurfacePackageResultCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISurfacePackageResultCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SurfaceControlViewHost.SurfacePackage _arg0 = (SurfaceControlViewHost.SurfacePackage) data.readTypedObject(SurfaceControlViewHost.SurfacePackage.CREATOR);
                            data.enforceNoDataAvail();
                            onResult(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISurfacePackageResultCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISurfacePackageResultCallback.DESCRIPTOR;
            }

            @Override // android.service.autofill.ISurfacePackageResultCallback
            public void onResult(SurfaceControlViewHost.SurfacePackage result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISurfacePackageResultCallback.DESCRIPTOR);
                    _data.writeTypedObject(result, 0);
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
