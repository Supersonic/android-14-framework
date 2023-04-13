package android.service.rotationresolver;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IRotationResolverCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.rotationresolver.IRotationResolverCallback";

    void onCancellable(ICancellationSignal iCancellationSignal) throws RemoteException;

    void onFailure(int i) throws RemoteException;

    void onSuccess(int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IRotationResolverCallback {
        @Override // android.service.rotationresolver.IRotationResolverCallback
        public void onCancellable(ICancellationSignal cancellation) throws RemoteException {
        }

        @Override // android.service.rotationresolver.IRotationResolverCallback
        public void onSuccess(int recommendedRotation) throws RemoteException {
        }

        @Override // android.service.rotationresolver.IRotationResolverCallback
        public void onFailure(int error) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IRotationResolverCallback {
        static final int TRANSACTION_onCancellable = 1;
        static final int TRANSACTION_onFailure = 3;
        static final int TRANSACTION_onSuccess = 2;

        public Stub() {
            attachInterface(this, IRotationResolverCallback.DESCRIPTOR);
        }

        public static IRotationResolverCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRotationResolverCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IRotationResolverCallback)) {
                return (IRotationResolverCallback) iin;
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
                    return "onCancellable";
                case 2:
                    return "onSuccess";
                case 3:
                    return "onFailure";
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
                data.enforceInterface(IRotationResolverCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRotationResolverCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ICancellationSignal _arg0 = ICancellationSignal.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onCancellable(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onSuccess(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onFailure(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IRotationResolverCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRotationResolverCallback.DESCRIPTOR;
            }

            @Override // android.service.rotationresolver.IRotationResolverCallback
            public void onCancellable(ICancellationSignal cancellation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRotationResolverCallback.DESCRIPTOR);
                    _data.writeStrongInterface(cancellation);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.rotationresolver.IRotationResolverCallback
            public void onSuccess(int recommendedRotation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRotationResolverCallback.DESCRIPTOR);
                    _data.writeInt(recommendedRotation);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.rotationresolver.IRotationResolverCallback
            public void onFailure(int error) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRotationResolverCallback.DESCRIPTOR);
                    _data.writeInt(error);
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
