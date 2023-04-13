package android.service.credentials;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.credentials.IBeginCreateCredentialCallback;
import android.service.credentials.IBeginGetCredentialCallback;
import android.service.credentials.IClearCredentialStateCallback;
/* loaded from: classes3.dex */
public interface ICredentialProviderService extends IInterface {
    public static final String DESCRIPTOR = "android.service.credentials.ICredentialProviderService";

    ICancellationSignal onBeginCreateCredential(BeginCreateCredentialRequest beginCreateCredentialRequest, IBeginCreateCredentialCallback iBeginCreateCredentialCallback) throws RemoteException;

    ICancellationSignal onBeginGetCredential(BeginGetCredentialRequest beginGetCredentialRequest, IBeginGetCredentialCallback iBeginGetCredentialCallback) throws RemoteException;

    ICancellationSignal onClearCredentialState(ClearCredentialStateRequest clearCredentialStateRequest, IClearCredentialStateCallback iClearCredentialStateCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ICredentialProviderService {
        @Override // android.service.credentials.ICredentialProviderService
        public ICancellationSignal onBeginGetCredential(BeginGetCredentialRequest request, IBeginGetCredentialCallback callback) throws RemoteException {
            return null;
        }

        @Override // android.service.credentials.ICredentialProviderService
        public ICancellationSignal onBeginCreateCredential(BeginCreateCredentialRequest request, IBeginCreateCredentialCallback callback) throws RemoteException {
            return null;
        }

        @Override // android.service.credentials.ICredentialProviderService
        public ICancellationSignal onClearCredentialState(ClearCredentialStateRequest request, IClearCredentialStateCallback callback) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ICredentialProviderService {
        static final int TRANSACTION_onBeginCreateCredential = 2;
        static final int TRANSACTION_onBeginGetCredential = 1;
        static final int TRANSACTION_onClearCredentialState = 3;

        public Stub() {
            attachInterface(this, ICredentialProviderService.DESCRIPTOR);
        }

        public static ICredentialProviderService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICredentialProviderService.DESCRIPTOR);
            if (iin != null && (iin instanceof ICredentialProviderService)) {
                return (ICredentialProviderService) iin;
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
                    return "onBeginGetCredential";
                case 2:
                    return "onBeginCreateCredential";
                case 3:
                    return "onClearCredentialState";
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
                data.enforceInterface(ICredentialProviderService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICredentialProviderService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            BeginGetCredentialRequest _arg0 = (BeginGetCredentialRequest) data.readTypedObject(BeginGetCredentialRequest.CREATOR);
                            IBeginGetCredentialCallback _arg1 = IBeginGetCredentialCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ICancellationSignal _result = onBeginGetCredential(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            BeginCreateCredentialRequest _arg02 = (BeginCreateCredentialRequest) data.readTypedObject(BeginCreateCredentialRequest.CREATOR);
                            IBeginCreateCredentialCallback _arg12 = IBeginCreateCredentialCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ICancellationSignal _result2 = onBeginCreateCredential(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            ClearCredentialStateRequest _arg03 = (ClearCredentialStateRequest) data.readTypedObject(ClearCredentialStateRequest.CREATOR);
                            IClearCredentialStateCallback _arg13 = IClearCredentialStateCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ICancellationSignal _result3 = onClearCredentialState(_arg03, _arg13);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ICredentialProviderService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICredentialProviderService.DESCRIPTOR;
            }

            @Override // android.service.credentials.ICredentialProviderService
            public ICancellationSignal onBeginGetCredential(BeginGetCredentialRequest request, IBeginGetCredentialCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialProviderService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.credentials.ICredentialProviderService
            public ICancellationSignal onBeginCreateCredential(BeginCreateCredentialRequest request, IBeginCreateCredentialCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialProviderService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.credentials.ICredentialProviderService
            public ICancellationSignal onClearCredentialState(ClearCredentialStateRequest request, IClearCredentialStateCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialProviderService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
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
