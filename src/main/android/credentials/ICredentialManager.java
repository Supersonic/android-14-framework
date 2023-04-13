package android.credentials;

import android.content.ComponentName;
import android.credentials.IClearCredentialStateCallback;
import android.credentials.ICreateCredentialCallback;
import android.credentials.IGetCredentialCallback;
import android.credentials.ISetEnabledProvidersCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface ICredentialManager extends IInterface {
    public static final String DESCRIPTOR = "android.credentials.ICredentialManager";

    ICancellationSignal clearCredentialState(ClearCredentialStateRequest clearCredentialStateRequest, IClearCredentialStateCallback iClearCredentialStateCallback, String str) throws RemoteException;

    ICancellationSignal executeCreateCredential(CreateCredentialRequest createCredentialRequest, ICreateCredentialCallback iCreateCredentialCallback, String str) throws RemoteException;

    ICancellationSignal executeGetCredential(GetCredentialRequest getCredentialRequest, IGetCredentialCallback iGetCredentialCallback, String str) throws RemoteException;

    List<CredentialProviderInfo> getCredentialProviderServices(int i, int i2) throws RemoteException;

    List<CredentialProviderInfo> getCredentialProviderServicesForTesting(int i) throws RemoteException;

    boolean isEnabledCredentialProviderService(ComponentName componentName, String str) throws RemoteException;

    void registerCredentialDescription(RegisterCredentialDescriptionRequest registerCredentialDescriptionRequest, String str) throws RemoteException;

    void setEnabledProviders(List<String> list, int i, ISetEnabledProvidersCallback iSetEnabledProvidersCallback) throws RemoteException;

    void unregisterCredentialDescription(UnregisterCredentialDescriptionRequest unregisterCredentialDescriptionRequest, String str) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICredentialManager {
        @Override // android.credentials.ICredentialManager
        public ICancellationSignal executeGetCredential(GetCredentialRequest request, IGetCredentialCallback callback, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.credentials.ICredentialManager
        public ICancellationSignal executeCreateCredential(CreateCredentialRequest request, ICreateCredentialCallback callback, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.credentials.ICredentialManager
        public ICancellationSignal clearCredentialState(ClearCredentialStateRequest request, IClearCredentialStateCallback callback, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.credentials.ICredentialManager
        public void setEnabledProviders(List<String> providers, int userId, ISetEnabledProvidersCallback callback) throws RemoteException {
        }

        @Override // android.credentials.ICredentialManager
        public void registerCredentialDescription(RegisterCredentialDescriptionRequest request, String callingPackage) throws RemoteException {
        }

        @Override // android.credentials.ICredentialManager
        public void unregisterCredentialDescription(UnregisterCredentialDescriptionRequest request, String callingPackage) throws RemoteException {
        }

        @Override // android.credentials.ICredentialManager
        public boolean isEnabledCredentialProviderService(ComponentName componentName, String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.credentials.ICredentialManager
        public List<CredentialProviderInfo> getCredentialProviderServices(int userId, int providerFilter) throws RemoteException {
            return null;
        }

        @Override // android.credentials.ICredentialManager
        public List<CredentialProviderInfo> getCredentialProviderServicesForTesting(int providerFilter) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICredentialManager {
        static final int TRANSACTION_clearCredentialState = 3;
        static final int TRANSACTION_executeCreateCredential = 2;
        static final int TRANSACTION_executeGetCredential = 1;
        static final int TRANSACTION_getCredentialProviderServices = 8;
        static final int TRANSACTION_getCredentialProviderServicesForTesting = 9;
        static final int TRANSACTION_isEnabledCredentialProviderService = 7;
        static final int TRANSACTION_registerCredentialDescription = 5;
        static final int TRANSACTION_setEnabledProviders = 4;
        static final int TRANSACTION_unregisterCredentialDescription = 6;

        public Stub() {
            attachInterface(this, ICredentialManager.DESCRIPTOR);
        }

        public static ICredentialManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICredentialManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ICredentialManager)) {
                return (ICredentialManager) iin;
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
                    return "executeGetCredential";
                case 2:
                    return "executeCreateCredential";
                case 3:
                    return "clearCredentialState";
                case 4:
                    return "setEnabledProviders";
                case 5:
                    return "registerCredentialDescription";
                case 6:
                    return "unregisterCredentialDescription";
                case 7:
                    return "isEnabledCredentialProviderService";
                case 8:
                    return "getCredentialProviderServices";
                case 9:
                    return "getCredentialProviderServicesForTesting";
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
                data.enforceInterface(ICredentialManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICredentialManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            GetCredentialRequest _arg0 = (GetCredentialRequest) data.readTypedObject(GetCredentialRequest.CREATOR);
                            IGetCredentialCallback _arg1 = IGetCredentialCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            ICancellationSignal _result = executeGetCredential(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            CreateCredentialRequest _arg02 = (CreateCredentialRequest) data.readTypedObject(CreateCredentialRequest.CREATOR);
                            ICreateCredentialCallback _arg12 = ICreateCredentialCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            ICancellationSignal _result2 = executeCreateCredential(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            ClearCredentialStateRequest _arg03 = (ClearCredentialStateRequest) data.readTypedObject(ClearCredentialStateRequest.CREATOR);
                            IClearCredentialStateCallback _arg13 = IClearCredentialStateCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            ICancellationSignal _result3 = clearCredentialState(_arg03, _arg13, _arg23);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 4:
                            List<String> _arg04 = data.createStringArrayList();
                            int _arg14 = data.readInt();
                            ISetEnabledProvidersCallback _arg24 = ISetEnabledProvidersCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setEnabledProviders(_arg04, _arg14, _arg24);
                            reply.writeNoException();
                            break;
                        case 5:
                            RegisterCredentialDescriptionRequest _arg05 = (RegisterCredentialDescriptionRequest) data.readTypedObject(RegisterCredentialDescriptionRequest.CREATOR);
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            registerCredentialDescription(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 6:
                            UnregisterCredentialDescriptionRequest _arg06 = (UnregisterCredentialDescriptionRequest) data.readTypedObject(UnregisterCredentialDescriptionRequest.CREATOR);
                            String _arg16 = data.readString();
                            data.enforceNoDataAvail();
                            unregisterCredentialDescription(_arg06, _arg16);
                            reply.writeNoException();
                            break;
                        case 7:
                            ComponentName _arg07 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result4 = isEnabledCredentialProviderService(_arg07, _arg17);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            List<CredentialProviderInfo> _result5 = getCredentialProviderServices(_arg08, _arg18);
                            reply.writeNoException();
                            reply.writeTypedList(_result5, 1);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            List<CredentialProviderInfo> _result6 = getCredentialProviderServicesForTesting(_arg09);
                            reply.writeNoException();
                            reply.writeTypedList(_result6, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements ICredentialManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICredentialManager.DESCRIPTOR;
            }

            @Override // android.credentials.ICredentialManager
            public ICancellationSignal executeGetCredential(GetCredentialRequest request, IGetCredentialCallback callback, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialManager.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.credentials.ICredentialManager
            public ICancellationSignal executeCreateCredential(CreateCredentialRequest request, ICreateCredentialCallback callback, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialManager.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.credentials.ICredentialManager
            public ICancellationSignal clearCredentialState(ClearCredentialStateRequest request, IClearCredentialStateCallback callback, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialManager.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.credentials.ICredentialManager
            public void setEnabledProviders(List<String> providers, int userId, ISetEnabledProvidersCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialManager.DESCRIPTOR);
                    _data.writeStringList(providers);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.credentials.ICredentialManager
            public void registerCredentialDescription(RegisterCredentialDescriptionRequest request, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialManager.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.credentials.ICredentialManager
            public void unregisterCredentialDescription(UnregisterCredentialDescriptionRequest request, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialManager.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.credentials.ICredentialManager
            public boolean isEnabledCredentialProviderService(ComponentName componentName, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialManager.DESCRIPTOR);
                    _data.writeTypedObject(componentName, 0);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.credentials.ICredentialManager
            public List<CredentialProviderInfo> getCredentialProviderServices(int userId, int providerFilter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(providerFilter);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    List<CredentialProviderInfo> _result = _reply.createTypedArrayList(CredentialProviderInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.credentials.ICredentialManager
            public List<CredentialProviderInfo> getCredentialProviderServicesForTesting(int providerFilter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialManager.DESCRIPTOR);
                    _data.writeInt(providerFilter);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    List<CredentialProviderInfo> _result = _reply.createTypedArrayList(CredentialProviderInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
