package android.companion;

import android.companion.IAssociationRequestCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.infra.AndroidFuture;
/* loaded from: classes.dex */
public interface ICompanionDeviceDiscoveryService extends IInterface {
    void onAssociationCreated() throws RemoteException;

    void startDiscovery(AssociationRequest associationRequest, String str, IAssociationRequestCallback iAssociationRequestCallback, AndroidFuture<String> androidFuture) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICompanionDeviceDiscoveryService {
        @Override // android.companion.ICompanionDeviceDiscoveryService
        public void startDiscovery(AssociationRequest request, String callingPackage, IAssociationRequestCallback applicationCallback, AndroidFuture<String> serviceCallback) throws RemoteException {
        }

        @Override // android.companion.ICompanionDeviceDiscoveryService
        public void onAssociationCreated() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICompanionDeviceDiscoveryService {
        public static final String DESCRIPTOR = "android.companion.ICompanionDeviceDiscoveryService";
        static final int TRANSACTION_onAssociationCreated = 2;
        static final int TRANSACTION_startDiscovery = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICompanionDeviceDiscoveryService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICompanionDeviceDiscoveryService)) {
                return (ICompanionDeviceDiscoveryService) iin;
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
                    return "startDiscovery";
                case 2:
                    return "onAssociationCreated";
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
                            AssociationRequest _arg0 = (AssociationRequest) data.readTypedObject(AssociationRequest.CREATOR);
                            String _arg1 = data.readString();
                            IAssociationRequestCallback _arg2 = IAssociationRequestCallback.Stub.asInterface(data.readStrongBinder());
                            AndroidFuture<String> _arg3 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            startDiscovery(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            onAssociationCreated();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ICompanionDeviceDiscoveryService {
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

            @Override // android.companion.ICompanionDeviceDiscoveryService
            public void startDiscovery(AssociationRequest request, String callingPackage, IAssociationRequestCallback applicationCallback, AndroidFuture<String> serviceCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeString(callingPackage);
                    _data.writeStrongInterface(applicationCallback);
                    _data.writeTypedObject(serviceCallback, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.companion.ICompanionDeviceDiscoveryService
            public void onAssociationCreated() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
