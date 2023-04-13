package android.service.assist.classification;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.assist.classification.IFieldClassificationCallback;
/* loaded from: classes3.dex */
public interface IFieldClassificationService extends IInterface {
    public static final String DESCRIPTOR = "android.service.assist.classification.IFieldClassificationService";

    void onConnected(boolean z, boolean z2) throws RemoteException;

    void onDisconnected() throws RemoteException;

    void onFieldClassificationRequest(FieldClassificationRequest fieldClassificationRequest, IFieldClassificationCallback iFieldClassificationCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IFieldClassificationService {
        @Override // android.service.assist.classification.IFieldClassificationService
        public void onConnected(boolean debug, boolean verbose) throws RemoteException {
        }

        @Override // android.service.assist.classification.IFieldClassificationService
        public void onDisconnected() throws RemoteException {
        }

        @Override // android.service.assist.classification.IFieldClassificationService
        public void onFieldClassificationRequest(FieldClassificationRequest request, IFieldClassificationCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IFieldClassificationService {
        static final int TRANSACTION_onConnected = 1;
        static final int TRANSACTION_onDisconnected = 2;
        static final int TRANSACTION_onFieldClassificationRequest = 3;

        public Stub() {
            attachInterface(this, IFieldClassificationService.DESCRIPTOR);
        }

        public static IFieldClassificationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IFieldClassificationService.DESCRIPTOR);
            if (iin != null && (iin instanceof IFieldClassificationService)) {
                return (IFieldClassificationService) iin;
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
                    return "onConnected";
                case 2:
                    return "onDisconnected";
                case 3:
                    return "onFieldClassificationRequest";
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
                data.enforceInterface(IFieldClassificationService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IFieldClassificationService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onConnected(_arg0, _arg1);
                            break;
                        case 2:
                            onDisconnected();
                            break;
                        case 3:
                            FieldClassificationRequest _arg02 = (FieldClassificationRequest) data.readTypedObject(FieldClassificationRequest.CREATOR);
                            IFieldClassificationCallback _arg12 = IFieldClassificationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onFieldClassificationRequest(_arg02, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IFieldClassificationService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IFieldClassificationService.DESCRIPTOR;
            }

            @Override // android.service.assist.classification.IFieldClassificationService
            public void onConnected(boolean debug, boolean verbose) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFieldClassificationService.DESCRIPTOR);
                    _data.writeBoolean(debug);
                    _data.writeBoolean(verbose);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.assist.classification.IFieldClassificationService
            public void onDisconnected() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFieldClassificationService.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.assist.classification.IFieldClassificationService
            public void onFieldClassificationRequest(FieldClassificationRequest request, IFieldClassificationCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFieldClassificationService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
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
