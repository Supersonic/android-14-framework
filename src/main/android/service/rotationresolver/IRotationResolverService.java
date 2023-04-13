package android.service.rotationresolver;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.rotationresolver.IRotationResolverCallback;
/* loaded from: classes3.dex */
public interface IRotationResolverService extends IInterface {
    public static final String DESCRIPTOR = "android.service.rotationresolver.IRotationResolverService";

    void resolveRotation(IRotationResolverCallback iRotationResolverCallback, RotationResolutionRequest rotationResolutionRequest) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IRotationResolverService {
        @Override // android.service.rotationresolver.IRotationResolverService
        public void resolveRotation(IRotationResolverCallback callback, RotationResolutionRequest request) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IRotationResolverService {
        static final int TRANSACTION_resolveRotation = 1;

        public Stub() {
            attachInterface(this, IRotationResolverService.DESCRIPTOR);
        }

        public static IRotationResolverService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRotationResolverService.DESCRIPTOR);
            if (iin != null && (iin instanceof IRotationResolverService)) {
                return (IRotationResolverService) iin;
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
                    return "resolveRotation";
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
                data.enforceInterface(IRotationResolverService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRotationResolverService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IRotationResolverCallback _arg0 = IRotationResolverCallback.Stub.asInterface(data.readStrongBinder());
                            RotationResolutionRequest _arg1 = (RotationResolutionRequest) data.readTypedObject(RotationResolutionRequest.CREATOR);
                            data.enforceNoDataAvail();
                            resolveRotation(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IRotationResolverService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRotationResolverService.DESCRIPTOR;
            }

            @Override // android.service.rotationresolver.IRotationResolverService
            public void resolveRotation(IRotationResolverCallback callback, RotationResolutionRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRotationResolverService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeTypedObject(request, 0);
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
