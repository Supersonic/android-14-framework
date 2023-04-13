package android.service.autofill;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.autofill.IFillCallback;
import android.service.autofill.ISaveCallback;
import com.android.internal.p028os.IResultReceiver;
/* loaded from: classes3.dex */
public interface IAutoFillService extends IInterface {
    void onConnectedStateChanged(boolean z) throws RemoteException;

    void onFillRequest(FillRequest fillRequest, IFillCallback iFillCallback) throws RemoteException;

    void onSaveRequest(SaveRequest saveRequest, ISaveCallback iSaveCallback) throws RemoteException;

    void onSavedPasswordCountRequest(IResultReceiver iResultReceiver) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IAutoFillService {
        @Override // android.service.autofill.IAutoFillService
        public void onConnectedStateChanged(boolean connected) throws RemoteException {
        }

        @Override // android.service.autofill.IAutoFillService
        public void onFillRequest(FillRequest request, IFillCallback callback) throws RemoteException {
        }

        @Override // android.service.autofill.IAutoFillService
        public void onSaveRequest(SaveRequest request, ISaveCallback callback) throws RemoteException {
        }

        @Override // android.service.autofill.IAutoFillService
        public void onSavedPasswordCountRequest(IResultReceiver receiver) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IAutoFillService {
        public static final String DESCRIPTOR = "android.service.autofill.IAutoFillService";
        static final int TRANSACTION_onConnectedStateChanged = 1;
        static final int TRANSACTION_onFillRequest = 2;
        static final int TRANSACTION_onSaveRequest = 3;
        static final int TRANSACTION_onSavedPasswordCountRequest = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAutoFillService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAutoFillService)) {
                return (IAutoFillService) iin;
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
                    return "onConnectedStateChanged";
                case 2:
                    return "onFillRequest";
                case 3:
                    return "onSaveRequest";
                case 4:
                    return "onSavedPasswordCountRequest";
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
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onConnectedStateChanged(_arg0);
                            break;
                        case 2:
                            FillRequest _arg02 = (FillRequest) data.readTypedObject(FillRequest.CREATOR);
                            IFillCallback _arg1 = IFillCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onFillRequest(_arg02, _arg1);
                            break;
                        case 3:
                            SaveRequest _arg03 = (SaveRequest) data.readTypedObject(SaveRequest.CREATOR);
                            ISaveCallback _arg12 = ISaveCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onSaveRequest(_arg03, _arg12);
                            break;
                        case 4:
                            IResultReceiver _arg04 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onSavedPasswordCountRequest(_arg04);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IAutoFillService {
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

            @Override // android.service.autofill.IAutoFillService
            public void onConnectedStateChanged(boolean connected) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(connected);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.IAutoFillService
            public void onFillRequest(FillRequest request, IFillCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.IAutoFillService
            public void onSaveRequest(SaveRequest request, ISaveCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.autofill.IAutoFillService
            public void onSavedPasswordCountRequest(IResultReceiver receiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(receiver);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
