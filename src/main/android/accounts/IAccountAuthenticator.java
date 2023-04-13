package android.accounts;

import android.Manifest;
import android.accounts.IAccountAuthenticatorResponse;
import android.app.ActivityThread;
import android.content.AttributionSource;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IAccountAuthenticator extends IInterface {
    void addAccount(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str, String str2, String[] strArr, Bundle bundle) throws RemoteException;

    void addAccountFromCredentials(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, Bundle bundle) throws RemoteException;

    void confirmCredentials(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, Bundle bundle) throws RemoteException;

    void editProperties(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str) throws RemoteException;

    void finishSession(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str, Bundle bundle) throws RemoteException;

    void getAccountCredentialsForCloning(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account) throws RemoteException;

    void getAccountRemovalAllowed(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account) throws RemoteException;

    void getAuthToken(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String str, Bundle bundle) throws RemoteException;

    void getAuthTokenLabel(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str) throws RemoteException;

    void hasFeatures(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String[] strArr) throws RemoteException;

    void isCredentialsUpdateSuggested(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String str) throws RemoteException;

    void startAddAccountSession(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str, String str2, String[] strArr, Bundle bundle) throws RemoteException;

    void startUpdateCredentialsSession(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String str, Bundle bundle) throws RemoteException;

    void updateCredentials(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String str, Bundle bundle) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAccountAuthenticator {
        @Override // android.accounts.IAccountAuthenticator
        public void addAccount(IAccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void confirmCredentials(IAccountAuthenticatorResponse response, Account account, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void getAuthToken(IAccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void getAuthTokenLabel(IAccountAuthenticatorResponse response, String authTokenType) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void updateCredentials(IAccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void editProperties(IAccountAuthenticatorResponse response, String accountType) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void hasFeatures(IAccountAuthenticatorResponse response, Account account, String[] features) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void getAccountRemovalAllowed(IAccountAuthenticatorResponse response, Account account) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void getAccountCredentialsForCloning(IAccountAuthenticatorResponse response, Account account) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void addAccountFromCredentials(IAccountAuthenticatorResponse response, Account account, Bundle accountCredentials) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void startAddAccountSession(IAccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void startUpdateCredentialsSession(IAccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void finishSession(IAccountAuthenticatorResponse response, String accountType, Bundle sessionBundle) throws RemoteException {
        }

        @Override // android.accounts.IAccountAuthenticator
        public void isCredentialsUpdateSuggested(IAccountAuthenticatorResponse response, Account account, String statusToken) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAccountAuthenticator {
        public static final String DESCRIPTOR = "android.accounts.IAccountAuthenticator";
        static final int TRANSACTION_addAccount = 1;
        static final int TRANSACTION_addAccountFromCredentials = 10;
        static final int TRANSACTION_confirmCredentials = 2;
        static final int TRANSACTION_editProperties = 6;
        static final int TRANSACTION_finishSession = 13;
        static final int TRANSACTION_getAccountCredentialsForCloning = 9;
        static final int TRANSACTION_getAccountRemovalAllowed = 8;
        static final int TRANSACTION_getAuthToken = 3;
        static final int TRANSACTION_getAuthTokenLabel = 4;
        static final int TRANSACTION_hasFeatures = 7;
        static final int TRANSACTION_isCredentialsUpdateSuggested = 14;
        static final int TRANSACTION_startAddAccountSession = 11;
        static final int TRANSACTION_startUpdateCredentialsSession = 12;
        static final int TRANSACTION_updateCredentials = 5;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IAccountAuthenticator asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccountAuthenticator)) {
                return (IAccountAuthenticator) iin;
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
                    return "addAccount";
                case 2:
                    return "confirmCredentials";
                case 3:
                    return "getAuthToken";
                case 4:
                    return "getAuthTokenLabel";
                case 5:
                    return "updateCredentials";
                case 6:
                    return "editProperties";
                case 7:
                    return "hasFeatures";
                case 8:
                    return "getAccountRemovalAllowed";
                case 9:
                    return "getAccountCredentialsForCloning";
                case 10:
                    return "addAccountFromCredentials";
                case 11:
                    return "startAddAccountSession";
                case 12:
                    return "startUpdateCredentialsSession";
                case 13:
                    return "finishSession";
                case 14:
                    return "isCredentialsUpdateSuggested";
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
                            IAccountAuthenticatorResponse _arg0 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            String[] _arg3 = data.createStringArray();
                            Bundle _arg4 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            addAccount(_arg0, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 2:
                            IAccountAuthenticatorResponse _arg02 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg12 = (Account) data.readTypedObject(Account.CREATOR);
                            Bundle _arg22 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            confirmCredentials(_arg02, _arg12, _arg22);
                            break;
                        case 3:
                            IAccountAuthenticatorResponse _arg03 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg13 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg23 = data.readString();
                            Bundle _arg32 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            getAuthToken(_arg03, _arg13, _arg23, _arg32);
                            break;
                        case 4:
                            IAccountAuthenticatorResponse _arg04 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            getAuthTokenLabel(_arg04, _arg14);
                            break;
                        case 5:
                            IAccountAuthenticatorResponse _arg05 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg15 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg24 = data.readString();
                            Bundle _arg33 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            updateCredentials(_arg05, _arg15, _arg24, _arg33);
                            break;
                        case 6:
                            IAccountAuthenticatorResponse _arg06 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg16 = data.readString();
                            data.enforceNoDataAvail();
                            editProperties(_arg06, _arg16);
                            break;
                        case 7:
                            IAccountAuthenticatorResponse _arg07 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg17 = (Account) data.readTypedObject(Account.CREATOR);
                            String[] _arg25 = data.createStringArray();
                            data.enforceNoDataAvail();
                            hasFeatures(_arg07, _arg17, _arg25);
                            break;
                        case 8:
                            IAccountAuthenticatorResponse _arg08 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg18 = (Account) data.readTypedObject(Account.CREATOR);
                            data.enforceNoDataAvail();
                            getAccountRemovalAllowed(_arg08, _arg18);
                            break;
                        case 9:
                            IAccountAuthenticatorResponse _arg09 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg19 = (Account) data.readTypedObject(Account.CREATOR);
                            data.enforceNoDataAvail();
                            getAccountCredentialsForCloning(_arg09, _arg19);
                            break;
                        case 10:
                            IAccountAuthenticatorResponse _arg010 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg110 = (Account) data.readTypedObject(Account.CREATOR);
                            Bundle _arg26 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            addAccountFromCredentials(_arg010, _arg110, _arg26);
                            break;
                        case 11:
                            IAccountAuthenticatorResponse _arg011 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg111 = data.readString();
                            String _arg27 = data.readString();
                            String[] _arg34 = data.createStringArray();
                            Bundle _arg42 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            startAddAccountSession(_arg011, _arg111, _arg27, _arg34, _arg42);
                            break;
                        case 12:
                            IAccountAuthenticatorResponse _arg012 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg112 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg28 = data.readString();
                            Bundle _arg35 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            startUpdateCredentialsSession(_arg012, _arg112, _arg28, _arg35);
                            break;
                        case 13:
                            IAccountAuthenticatorResponse _arg013 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg113 = data.readString();
                            Bundle _arg29 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            finishSession(_arg013, _arg113, _arg29);
                            break;
                        case 14:
                            IAccountAuthenticatorResponse _arg014 = IAccountAuthenticatorResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg114 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg210 = data.readString();
                            data.enforceNoDataAvail();
                            isCredentialsUpdateSuggested(_arg014, _arg114, _arg210);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IAccountAuthenticator {
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

            @Override // android.accounts.IAccountAuthenticator
            public void addAccount(IAccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    _data.writeString(authTokenType);
                    _data.writeStringArray(requiredFeatures);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void confirmCredentials(IAccountAuthenticatorResponse response, Account account, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void getAuthToken(IAccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authTokenType);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void getAuthTokenLabel(IAccountAuthenticatorResponse response, String authTokenType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(authTokenType);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void updateCredentials(IAccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authTokenType);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void editProperties(IAccountAuthenticatorResponse response, String accountType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void hasFeatures(IAccountAuthenticatorResponse response, Account account, String[] features) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeStringArray(features);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void getAccountRemovalAllowed(IAccountAuthenticatorResponse response, Account account) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void getAccountCredentialsForCloning(IAccountAuthenticatorResponse response, Account account) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void addAccountFromCredentials(IAccountAuthenticatorResponse response, Account account, Bundle accountCredentials) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeTypedObject(accountCredentials, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void startAddAccountSession(IAccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    _data.writeString(authTokenType);
                    _data.writeStringArray(requiredFeatures);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void startUpdateCredentialsSession(IAccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authTokenType);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void finishSession(IAccountAuthenticatorResponse response, String accountType, Bundle sessionBundle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    _data.writeTypedObject(sessionBundle, 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountAuthenticator
            public void isCredentialsUpdateSuggested(IAccountAuthenticatorResponse response, Account account, String statusToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(statusToken);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void addAccount_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void confirmCredentials_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void getAuthToken_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void getAuthTokenLabel_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void updateCredentials_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void editProperties_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void hasFeatures_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void getAccountRemovalAllowed_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void getAccountCredentialsForCloning_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void addAccountFromCredentials_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void startAddAccountSession_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void startUpdateCredentialsSession_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void finishSession_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void isCredentialsUpdateSuggested_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCOUNT_MANAGER, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 13;
        }
    }
}
