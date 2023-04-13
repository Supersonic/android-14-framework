package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface ITaskFpsCallback extends IInterface {
    public static final String DESCRIPTOR = "android.window.ITaskFpsCallback";

    void onFpsReported(float f) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ITaskFpsCallback {
        @Override // android.window.ITaskFpsCallback
        public void onFpsReported(float fps) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ITaskFpsCallback {
        static final int TRANSACTION_onFpsReported = 1;

        public Stub() {
            attachInterface(this, ITaskFpsCallback.DESCRIPTOR);
        }

        public static ITaskFpsCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITaskFpsCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ITaskFpsCallback)) {
                return (ITaskFpsCallback) iin;
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
                    return "onFpsReported";
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
                data.enforceInterface(ITaskFpsCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITaskFpsCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            float _arg0 = data.readFloat();
                            data.enforceNoDataAvail();
                            onFpsReported(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements ITaskFpsCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITaskFpsCallback.DESCRIPTOR;
            }

            @Override // android.window.ITaskFpsCallback
            public void onFpsReported(float fps) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITaskFpsCallback.DESCRIPTOR);
                    _data.writeFloat(fps);
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
