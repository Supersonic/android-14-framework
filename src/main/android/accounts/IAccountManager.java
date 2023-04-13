package android.accounts;

import android.accounts.IAccountManagerResponse;
import android.content.IntentSender;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import java.util.Map;
/* loaded from: classes.dex */
public interface IAccountManager extends IInterface {
    boolean accountAuthenticated(Account account) throws RemoteException;

    void addAccount(IAccountManagerResponse iAccountManagerResponse, String str, String str2, String[] strArr, boolean z, Bundle bundle) throws RemoteException;

    void addAccountAsUser(IAccountManagerResponse iAccountManagerResponse, String str, String str2, String[] strArr, boolean z, Bundle bundle, int i) throws RemoteException;

    boolean addAccountExplicitly(Account account, String str, Bundle bundle, String str2) throws RemoteException;

    boolean addAccountExplicitlyWithVisibility(Account account, String str, Bundle bundle, Map map, String str2) throws RemoteException;

    void addSharedAccountsFromParentUser(int i, int i2, String str) throws RemoteException;

    void clearPassword(Account account) throws RemoteException;

    void confirmCredentialsAsUser(IAccountManagerResponse iAccountManagerResponse, Account account, Bundle bundle, boolean z, int i) throws RemoteException;

    void copyAccountToUser(IAccountManagerResponse iAccountManagerResponse, Account account, int i, int i2) throws RemoteException;

    IntentSender createRequestAccountAccessIntentSenderAsUser(Account account, String str, UserHandle userHandle) throws RemoteException;

    void editProperties(IAccountManagerResponse iAccountManagerResponse, String str, boolean z) throws RemoteException;

    void finishSessionAsUser(IAccountManagerResponse iAccountManagerResponse, Bundle bundle, boolean z, Bundle bundle2, int i) throws RemoteException;

    void getAccountByTypeAndFeatures(IAccountManagerResponse iAccountManagerResponse, String str, String[] strArr, String str2) throws RemoteException;

    int getAccountVisibility(Account account, String str) throws RemoteException;

    Map getAccountsAndVisibilityForPackage(String str, String str2) throws RemoteException;

    Account[] getAccountsAsUser(String str, int i, String str2) throws RemoteException;

    void getAccountsByFeatures(IAccountManagerResponse iAccountManagerResponse, String str, String[] strArr, String str2) throws RemoteException;

    Account[] getAccountsByTypeForPackage(String str, String str2, String str3) throws RemoteException;

    Account[] getAccountsForPackage(String str, int i, String str2) throws RemoteException;

    void getAuthToken(IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, boolean z2, Bundle bundle) throws RemoteException;

    void getAuthTokenLabel(IAccountManagerResponse iAccountManagerResponse, String str, String str2) throws RemoteException;

    AuthenticatorDescription[] getAuthenticatorTypes(int i) throws RemoteException;

    Map getPackagesAndVisibilityForAccount(Account account) throws RemoteException;

    String getPassword(Account account) throws RemoteException;

    String getPreviousName(Account account) throws RemoteException;

    String getUserData(Account account, String str) throws RemoteException;

    boolean hasAccountAccess(Account account, String str, UserHandle userHandle) throws RemoteException;

    void hasFeatures(IAccountManagerResponse iAccountManagerResponse, Account account, String[] strArr, int i, String str) throws RemoteException;

    void invalidateAuthToken(String str, String str2) throws RemoteException;

    void isCredentialsUpdateSuggested(IAccountManagerResponse iAccountManagerResponse, Account account, String str) throws RemoteException;

    void onAccountAccessed(String str) throws RemoteException;

    String peekAuthToken(Account account, String str) throws RemoteException;

    void registerAccountListener(String[] strArr, String str) throws RemoteException;

    void removeAccountAsUser(IAccountManagerResponse iAccountManagerResponse, Account account, boolean z, int i) throws RemoteException;

    boolean removeAccountExplicitly(Account account) throws RemoteException;

    void renameAccount(IAccountManagerResponse iAccountManagerResponse, Account account, String str) throws RemoteException;

    boolean setAccountVisibility(Account account, String str, int i) throws RemoteException;

    void setAuthToken(Account account, String str, String str2) throws RemoteException;

    void setPassword(Account account, String str) throws RemoteException;

    void setUserData(Account account, String str, String str2) throws RemoteException;

    boolean someUserHasAccount(Account account) throws RemoteException;

    void startAddAccountSession(IAccountManagerResponse iAccountManagerResponse, String str, String str2, String[] strArr, boolean z, Bundle bundle) throws RemoteException;

    void startUpdateCredentialsSession(IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, Bundle bundle) throws RemoteException;

    void unregisterAccountListener(String[] strArr, String str) throws RemoteException;

    void updateAppPermission(Account account, String str, int i, boolean z) throws RemoteException;

    void updateCredentials(IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, Bundle bundle) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAccountManager {
        @Override // android.accounts.IAccountManager
        public String getPassword(Account account) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public String getUserData(Account account, String key) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public AuthenticatorDescription[] getAuthenticatorTypes(int userId) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public Account[] getAccountsForPackage(String packageName, int uid, String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public Account[] getAccountsByTypeForPackage(String type, String packageName, String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public Account[] getAccountsAsUser(String accountType, int userId, String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public void hasFeatures(IAccountManagerResponse response, Account account, String[] features, int userId, String opPackageName) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void getAccountByTypeAndFeatures(IAccountManagerResponse response, String accountType, String[] features, String opPackageName) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void getAccountsByFeatures(IAccountManagerResponse response, String accountType, String[] features, String opPackageName) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public boolean addAccountExplicitly(Account account, String password, Bundle extras, String opPackageName) throws RemoteException {
            return false;
        }

        @Override // android.accounts.IAccountManager
        public void removeAccountAsUser(IAccountManagerResponse response, Account account, boolean expectActivityLaunch, int userId) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public boolean removeAccountExplicitly(Account account) throws RemoteException {
            return false;
        }

        @Override // android.accounts.IAccountManager
        public void copyAccountToUser(IAccountManagerResponse response, Account account, int userFrom, int userTo) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void invalidateAuthToken(String accountType, String authToken) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public String peekAuthToken(Account account, String authTokenType) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public void setAuthToken(Account account, String authTokenType, String authToken) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void setPassword(Account account, String password) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void clearPassword(Account account) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void setUserData(Account account, String key, String value) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void updateAppPermission(Account account, String authTokenType, int uid, boolean value) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void getAuthToken(IAccountManagerResponse response, Account account, String authTokenType, boolean notifyOnAuthFailure, boolean expectActivityLaunch, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void addAccount(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void addAccountAsUser(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle options, int userId) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void updateCredentials(IAccountManagerResponse response, Account account, String authTokenType, boolean expectActivityLaunch, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void editProperties(IAccountManagerResponse response, String accountType, boolean expectActivityLaunch) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void confirmCredentialsAsUser(IAccountManagerResponse response, Account account, Bundle options, boolean expectActivityLaunch, int userId) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public boolean accountAuthenticated(Account account) throws RemoteException {
            return false;
        }

        @Override // android.accounts.IAccountManager
        public void getAuthTokenLabel(IAccountManagerResponse response, String accountType, String authTokenType) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void addSharedAccountsFromParentUser(int parentUserId, int userId, String opPackageName) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void renameAccount(IAccountManagerResponse response, Account accountToRename, String newName) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public String getPreviousName(Account account) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public void startAddAccountSession(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void startUpdateCredentialsSession(IAccountManagerResponse response, Account account, String authTokenType, boolean expectActivityLaunch, Bundle options) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void finishSessionAsUser(IAccountManagerResponse response, Bundle sessionBundle, boolean expectActivityLaunch, Bundle appInfo, int userId) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public boolean someUserHasAccount(Account account) throws RemoteException {
            return false;
        }

        @Override // android.accounts.IAccountManager
        public void isCredentialsUpdateSuggested(IAccountManagerResponse response, Account account, String statusToken) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public Map getPackagesAndVisibilityForAccount(Account account) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public boolean addAccountExplicitlyWithVisibility(Account account, String password, Bundle extras, Map visibility, String opPackageName) throws RemoteException {
            return false;
        }

        @Override // android.accounts.IAccountManager
        public boolean setAccountVisibility(Account a, String packageName, int newVisibility) throws RemoteException {
            return false;
        }

        @Override // android.accounts.IAccountManager
        public int getAccountVisibility(Account a, String packageName) throws RemoteException {
            return 0;
        }

        @Override // android.accounts.IAccountManager
        public Map getAccountsAndVisibilityForPackage(String packageName, String accountType) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public void registerAccountListener(String[] accountTypes, String opPackageName) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public void unregisterAccountListener(String[] accountTypes, String opPackageName) throws RemoteException {
        }

        @Override // android.accounts.IAccountManager
        public boolean hasAccountAccess(Account account, String packageName, UserHandle userHandle) throws RemoteException {
            return false;
        }

        @Override // android.accounts.IAccountManager
        public IntentSender createRequestAccountAccessIntentSenderAsUser(Account account, String packageName, UserHandle userHandle) throws RemoteException {
            return null;
        }

        @Override // android.accounts.IAccountManager
        public void onAccountAccessed(String token) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAccountManager {
        public static final String DESCRIPTOR = "android.accounts.IAccountManager";
        static final int TRANSACTION_accountAuthenticated = 27;
        static final int TRANSACTION_addAccount = 22;
        static final int TRANSACTION_addAccountAsUser = 23;
        static final int TRANSACTION_addAccountExplicitly = 10;
        static final int TRANSACTION_addAccountExplicitlyWithVisibility = 38;
        static final int TRANSACTION_addSharedAccountsFromParentUser = 29;
        static final int TRANSACTION_clearPassword = 18;
        static final int TRANSACTION_confirmCredentialsAsUser = 26;
        static final int TRANSACTION_copyAccountToUser = 13;
        static final int TRANSACTION_createRequestAccountAccessIntentSenderAsUser = 45;
        static final int TRANSACTION_editProperties = 25;
        static final int TRANSACTION_finishSessionAsUser = 34;
        static final int TRANSACTION_getAccountByTypeAndFeatures = 8;
        static final int TRANSACTION_getAccountVisibility = 40;
        static final int TRANSACTION_getAccountsAndVisibilityForPackage = 41;
        static final int TRANSACTION_getAccountsAsUser = 6;
        static final int TRANSACTION_getAccountsByFeatures = 9;
        static final int TRANSACTION_getAccountsByTypeForPackage = 5;
        static final int TRANSACTION_getAccountsForPackage = 4;
        static final int TRANSACTION_getAuthToken = 21;
        static final int TRANSACTION_getAuthTokenLabel = 28;
        static final int TRANSACTION_getAuthenticatorTypes = 3;
        static final int TRANSACTION_getPackagesAndVisibilityForAccount = 37;
        static final int TRANSACTION_getPassword = 1;
        static final int TRANSACTION_getPreviousName = 31;
        static final int TRANSACTION_getUserData = 2;
        static final int TRANSACTION_hasAccountAccess = 44;
        static final int TRANSACTION_hasFeatures = 7;
        static final int TRANSACTION_invalidateAuthToken = 14;
        static final int TRANSACTION_isCredentialsUpdateSuggested = 36;
        static final int TRANSACTION_onAccountAccessed = 46;
        static final int TRANSACTION_peekAuthToken = 15;
        static final int TRANSACTION_registerAccountListener = 42;
        static final int TRANSACTION_removeAccountAsUser = 11;
        static final int TRANSACTION_removeAccountExplicitly = 12;
        static final int TRANSACTION_renameAccount = 30;
        static final int TRANSACTION_setAccountVisibility = 39;
        static final int TRANSACTION_setAuthToken = 16;
        static final int TRANSACTION_setPassword = 17;
        static final int TRANSACTION_setUserData = 19;
        static final int TRANSACTION_someUserHasAccount = 35;
        static final int TRANSACTION_startAddAccountSession = 32;
        static final int TRANSACTION_startUpdateCredentialsSession = 33;
        static final int TRANSACTION_unregisterAccountListener = 43;
        static final int TRANSACTION_updateAppPermission = 20;
        static final int TRANSACTION_updateCredentials = 24;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccountManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccountManager)) {
                return (IAccountManager) iin;
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
                    return "getPassword";
                case 2:
                    return "getUserData";
                case 3:
                    return "getAuthenticatorTypes";
                case 4:
                    return "getAccountsForPackage";
                case 5:
                    return "getAccountsByTypeForPackage";
                case 6:
                    return "getAccountsAsUser";
                case 7:
                    return "hasFeatures";
                case 8:
                    return "getAccountByTypeAndFeatures";
                case 9:
                    return "getAccountsByFeatures";
                case 10:
                    return "addAccountExplicitly";
                case 11:
                    return "removeAccountAsUser";
                case 12:
                    return "removeAccountExplicitly";
                case 13:
                    return "copyAccountToUser";
                case 14:
                    return "invalidateAuthToken";
                case 15:
                    return "peekAuthToken";
                case 16:
                    return "setAuthToken";
                case 17:
                    return "setPassword";
                case 18:
                    return "clearPassword";
                case 19:
                    return "setUserData";
                case 20:
                    return "updateAppPermission";
                case 21:
                    return "getAuthToken";
                case 22:
                    return "addAccount";
                case 23:
                    return "addAccountAsUser";
                case 24:
                    return "updateCredentials";
                case 25:
                    return "editProperties";
                case 26:
                    return "confirmCredentialsAsUser";
                case 27:
                    return "accountAuthenticated";
                case 28:
                    return "getAuthTokenLabel";
                case 29:
                    return "addSharedAccountsFromParentUser";
                case 30:
                    return "renameAccount";
                case 31:
                    return "getPreviousName";
                case 32:
                    return "startAddAccountSession";
                case 33:
                    return "startUpdateCredentialsSession";
                case 34:
                    return "finishSessionAsUser";
                case 35:
                    return "someUserHasAccount";
                case 36:
                    return "isCredentialsUpdateSuggested";
                case 37:
                    return "getPackagesAndVisibilityForAccount";
                case 38:
                    return "addAccountExplicitlyWithVisibility";
                case 39:
                    return "setAccountVisibility";
                case 40:
                    return "getAccountVisibility";
                case 41:
                    return "getAccountsAndVisibilityForPackage";
                case 42:
                    return "registerAccountListener";
                case 43:
                    return "unregisterAccountListener";
                case 44:
                    return "hasAccountAccess";
                case 45:
                    return "createRequestAccountAccessIntentSenderAsUser";
                case 46:
                    return "onAccountAccessed";
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
                            Account _arg0 = (Account) data.readTypedObject(Account.CREATOR);
                            data.enforceNoDataAvail();
                            String _result = getPassword(_arg0);
                            reply.writeNoException();
                            reply.writeString(_result);
                            break;
                        case 2:
                            Account _arg02 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            String _result2 = getUserData(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeString(_result2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            AuthenticatorDescription[] _result3 = getAuthenticatorTypes(_arg03);
                            reply.writeNoException();
                            reply.writeTypedArray(_result3, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg12 = data.readInt();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            Account[] _result4 = getAccountsForPackage(_arg04, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeTypedArray(_result4, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            String _arg13 = data.readString();
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            Account[] _result5 = getAccountsByTypeForPackage(_arg05, _arg13, _arg22);
                            reply.writeNoException();
                            reply.writeTypedArray(_result5, 1);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            int _arg14 = data.readInt();
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            Account[] _result6 = getAccountsAsUser(_arg06, _arg14, _arg23);
                            reply.writeNoException();
                            reply.writeTypedArray(_result6, 1);
                            break;
                        case 7:
                            IAccountManagerResponse _arg07 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg15 = (Account) data.readTypedObject(Account.CREATOR);
                            String[] _arg24 = data.createStringArray();
                            int _arg3 = data.readInt();
                            String _arg4 = data.readString();
                            data.enforceNoDataAvail();
                            hasFeatures(_arg07, _arg15, _arg24, _arg3, _arg4);
                            reply.writeNoException();
                            break;
                        case 8:
                            IAccountManagerResponse _arg08 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg16 = data.readString();
                            String[] _arg25 = data.createStringArray();
                            String _arg32 = data.readString();
                            data.enforceNoDataAvail();
                            getAccountByTypeAndFeatures(_arg08, _arg16, _arg25, _arg32);
                            reply.writeNoException();
                            break;
                        case 9:
                            IAccountManagerResponse _arg09 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg17 = data.readString();
                            String[] _arg26 = data.createStringArray();
                            String _arg33 = data.readString();
                            data.enforceNoDataAvail();
                            getAccountsByFeatures(_arg09, _arg17, _arg26, _arg33);
                            reply.writeNoException();
                            break;
                        case 10:
                            Account _arg010 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg18 = data.readString();
                            Bundle _arg27 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            String _arg34 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result7 = addAccountExplicitly(_arg010, _arg18, _arg27, _arg34);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 11:
                            IAccountManagerResponse _arg011 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg19 = (Account) data.readTypedObject(Account.CREATOR);
                            boolean _arg28 = data.readBoolean();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            removeAccountAsUser(_arg011, _arg19, _arg28, _arg35);
                            reply.writeNoException();
                            break;
                        case 12:
                            Account _arg012 = (Account) data.readTypedObject(Account.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result8 = removeAccountExplicitly(_arg012);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 13:
                            IAccountManagerResponse _arg013 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg110 = (Account) data.readTypedObject(Account.CREATOR);
                            int _arg29 = data.readInt();
                            int _arg36 = data.readInt();
                            data.enforceNoDataAvail();
                            copyAccountToUser(_arg013, _arg110, _arg29, _arg36);
                            reply.writeNoException();
                            break;
                        case 14:
                            String _arg014 = data.readString();
                            String _arg111 = data.readString();
                            data.enforceNoDataAvail();
                            invalidateAuthToken(_arg014, _arg111);
                            reply.writeNoException();
                            break;
                        case 15:
                            Account _arg015 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg112 = data.readString();
                            data.enforceNoDataAvail();
                            String _result9 = peekAuthToken(_arg015, _arg112);
                            reply.writeNoException();
                            reply.writeString(_result9);
                            break;
                        case 16:
                            Account _arg016 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg113 = data.readString();
                            String _arg210 = data.readString();
                            data.enforceNoDataAvail();
                            setAuthToken(_arg016, _arg113, _arg210);
                            reply.writeNoException();
                            break;
                        case 17:
                            Account _arg017 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg114 = data.readString();
                            data.enforceNoDataAvail();
                            setPassword(_arg017, _arg114);
                            reply.writeNoException();
                            break;
                        case 18:
                            Account _arg018 = (Account) data.readTypedObject(Account.CREATOR);
                            data.enforceNoDataAvail();
                            clearPassword(_arg018);
                            reply.writeNoException();
                            break;
                        case 19:
                            Account _arg019 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg115 = data.readString();
                            String _arg211 = data.readString();
                            data.enforceNoDataAvail();
                            setUserData(_arg019, _arg115, _arg211);
                            reply.writeNoException();
                            break;
                        case 20:
                            Account _arg020 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg116 = data.readString();
                            int _arg212 = data.readInt();
                            boolean _arg37 = data.readBoolean();
                            data.enforceNoDataAvail();
                            updateAppPermission(_arg020, _arg116, _arg212, _arg37);
                            reply.writeNoException();
                            break;
                        case 21:
                            IAccountManagerResponse _arg021 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg117 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg213 = data.readString();
                            boolean _arg38 = data.readBoolean();
                            boolean _arg42 = data.readBoolean();
                            Bundle _arg5 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            getAuthToken(_arg021, _arg117, _arg213, _arg38, _arg42, _arg5);
                            reply.writeNoException();
                            break;
                        case 22:
                            IAccountManagerResponse _arg022 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg118 = data.readString();
                            String _arg214 = data.readString();
                            String[] _arg39 = data.createStringArray();
                            boolean _arg43 = data.readBoolean();
                            Bundle _arg52 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            addAccount(_arg022, _arg118, _arg214, _arg39, _arg43, _arg52);
                            reply.writeNoException();
                            break;
                        case 23:
                            IAccountManagerResponse _arg023 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg119 = data.readString();
                            String _arg215 = data.readString();
                            String[] _arg310 = data.createStringArray();
                            boolean _arg44 = data.readBoolean();
                            Bundle _arg53 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg6 = data.readInt();
                            data.enforceNoDataAvail();
                            addAccountAsUser(_arg023, _arg119, _arg215, _arg310, _arg44, _arg53, _arg6);
                            reply.writeNoException();
                            break;
                        case 24:
                            IAccountManagerResponse _arg024 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg120 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg216 = data.readString();
                            boolean _arg311 = data.readBoolean();
                            Bundle _arg45 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            updateCredentials(_arg024, _arg120, _arg216, _arg311, _arg45);
                            reply.writeNoException();
                            break;
                        case 25:
                            IAccountManagerResponse _arg025 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg121 = data.readString();
                            boolean _arg217 = data.readBoolean();
                            data.enforceNoDataAvail();
                            editProperties(_arg025, _arg121, _arg217);
                            reply.writeNoException();
                            break;
                        case 26:
                            IAccountManagerResponse _arg026 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg122 = (Account) data.readTypedObject(Account.CREATOR);
                            Bundle _arg218 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            boolean _arg312 = data.readBoolean();
                            int _arg46 = data.readInt();
                            data.enforceNoDataAvail();
                            confirmCredentialsAsUser(_arg026, _arg122, _arg218, _arg312, _arg46);
                            reply.writeNoException();
                            break;
                        case 27:
                            Account _arg027 = (Account) data.readTypedObject(Account.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result10 = accountAuthenticated(_arg027);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 28:
                            IAccountManagerResponse _arg028 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg123 = data.readString();
                            String _arg219 = data.readString();
                            data.enforceNoDataAvail();
                            getAuthTokenLabel(_arg028, _arg123, _arg219);
                            reply.writeNoException();
                            break;
                        case 29:
                            int _arg029 = data.readInt();
                            int _arg124 = data.readInt();
                            String _arg220 = data.readString();
                            data.enforceNoDataAvail();
                            addSharedAccountsFromParentUser(_arg029, _arg124, _arg220);
                            reply.writeNoException();
                            break;
                        case 30:
                            IAccountManagerResponse _arg030 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg125 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg221 = data.readString();
                            data.enforceNoDataAvail();
                            renameAccount(_arg030, _arg125, _arg221);
                            reply.writeNoException();
                            break;
                        case 31:
                            Account _arg031 = (Account) data.readTypedObject(Account.CREATOR);
                            data.enforceNoDataAvail();
                            String _result11 = getPreviousName(_arg031);
                            reply.writeNoException();
                            reply.writeString(_result11);
                            break;
                        case 32:
                            IAccountManagerResponse _arg032 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            String _arg126 = data.readString();
                            String _arg222 = data.readString();
                            String[] _arg313 = data.createStringArray();
                            boolean _arg47 = data.readBoolean();
                            Bundle _arg54 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            startAddAccountSession(_arg032, _arg126, _arg222, _arg313, _arg47, _arg54);
                            reply.writeNoException();
                            break;
                        case 33:
                            IAccountManagerResponse _arg033 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg127 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg223 = data.readString();
                            boolean _arg314 = data.readBoolean();
                            Bundle _arg48 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            startUpdateCredentialsSession(_arg033, _arg127, _arg223, _arg314, _arg48);
                            reply.writeNoException();
                            break;
                        case 34:
                            IAccountManagerResponse _arg034 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            Bundle _arg128 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            boolean _arg224 = data.readBoolean();
                            Bundle _arg315 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg49 = data.readInt();
                            data.enforceNoDataAvail();
                            finishSessionAsUser(_arg034, _arg128, _arg224, _arg315, _arg49);
                            reply.writeNoException();
                            break;
                        case 35:
                            Account _arg035 = (Account) data.readTypedObject(Account.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result12 = someUserHasAccount(_arg035);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        case 36:
                            IAccountManagerResponse _arg036 = IAccountManagerResponse.Stub.asInterface(data.readStrongBinder());
                            Account _arg129 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg225 = data.readString();
                            data.enforceNoDataAvail();
                            isCredentialsUpdateSuggested(_arg036, _arg129, _arg225);
                            reply.writeNoException();
                            break;
                        case 37:
                            Account _arg037 = (Account) data.readTypedObject(Account.CREATOR);
                            data.enforceNoDataAvail();
                            Map _result13 = getPackagesAndVisibilityForAccount(_arg037);
                            reply.writeNoException();
                            reply.writeMap(_result13);
                            break;
                        case 38:
                            Account _arg038 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg130 = data.readString();
                            Bundle _arg226 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            ClassLoader cl = getClass().getClassLoader();
                            Map _arg316 = data.readHashMap(cl);
                            String _arg410 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result14 = addAccountExplicitlyWithVisibility(_arg038, _arg130, _arg226, _arg316, _arg410);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            break;
                        case 39:
                            Account _arg039 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg131 = data.readString();
                            int _arg227 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result15 = setAccountVisibility(_arg039, _arg131, _arg227);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 40:
                            Account _arg040 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg132 = data.readString();
                            data.enforceNoDataAvail();
                            int _result16 = getAccountVisibility(_arg040, _arg132);
                            reply.writeNoException();
                            reply.writeInt(_result16);
                            break;
                        case 41:
                            String _arg041 = data.readString();
                            String _arg133 = data.readString();
                            data.enforceNoDataAvail();
                            Map _result17 = getAccountsAndVisibilityForPackage(_arg041, _arg133);
                            reply.writeNoException();
                            reply.writeMap(_result17);
                            break;
                        case 42:
                            String[] _arg042 = data.createStringArray();
                            String _arg134 = data.readString();
                            data.enforceNoDataAvail();
                            registerAccountListener(_arg042, _arg134);
                            reply.writeNoException();
                            break;
                        case 43:
                            String[] _arg043 = data.createStringArray();
                            String _arg135 = data.readString();
                            data.enforceNoDataAvail();
                            unregisterAccountListener(_arg043, _arg135);
                            reply.writeNoException();
                            break;
                        case 44:
                            Account _arg044 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg136 = data.readString();
                            UserHandle _arg228 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result18 = hasAccountAccess(_arg044, _arg136, _arg228);
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            break;
                        case 45:
                            Account _arg045 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg137 = data.readString();
                            UserHandle _arg229 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            IntentSender _result19 = createRequestAccountAccessIntentSenderAsUser(_arg045, _arg137, _arg229);
                            reply.writeNoException();
                            reply.writeTypedObject(_result19, 1);
                            break;
                        case 46:
                            String _arg046 = data.readString();
                            data.enforceNoDataAvail();
                            onAccountAccessed(_arg046);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IAccountManager {
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

            @Override // android.accounts.IAccountManager
            public String getPassword(Account account) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public String getUserData(Account account, String key) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(key);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public AuthenticatorDescription[] getAuthenticatorTypes(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    AuthenticatorDescription[] _result = (AuthenticatorDescription[]) _reply.createTypedArray(AuthenticatorDescription.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public Account[] getAccountsForPackage(String packageName, int uid, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(uid);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    Account[] _result = (Account[]) _reply.createTypedArray(Account.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public Account[] getAccountsByTypeForPackage(String type, String packageName, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeString(packageName);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    Account[] _result = (Account[]) _reply.createTypedArray(Account.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public Account[] getAccountsAsUser(String accountType, int userId, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(accountType);
                    _data.writeInt(userId);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    Account[] _result = (Account[]) _reply.createTypedArray(Account.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void hasFeatures(IAccountManagerResponse response, Account account, String[] features, int userId, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeStringArray(features);
                    _data.writeInt(userId);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void getAccountByTypeAndFeatures(IAccountManagerResponse response, String accountType, String[] features, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    _data.writeStringArray(features);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void getAccountsByFeatures(IAccountManagerResponse response, String accountType, String[] features, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    _data.writeStringArray(features);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public boolean addAccountExplicitly(Account account, String password, Bundle extras, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(password);
                    _data.writeTypedObject(extras, 0);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void removeAccountAsUser(IAccountManagerResponse response, Account account, boolean expectActivityLaunch, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeBoolean(expectActivityLaunch);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public boolean removeAccountExplicitly(Account account) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void copyAccountToUser(IAccountManagerResponse response, Account account, int userFrom, int userTo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeInt(userFrom);
                    _data.writeInt(userTo);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void invalidateAuthToken(String accountType, String authToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(accountType);
                    _data.writeString(authToken);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public String peekAuthToken(Account account, String authTokenType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authTokenType);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void setAuthToken(Account account, String authTokenType, String authToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authTokenType);
                    _data.writeString(authToken);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void setPassword(Account account, String password) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(password);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void clearPassword(Account account) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void setUserData(Account account, String key, String value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(key);
                    _data.writeString(value);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void updateAppPermission(Account account, String authTokenType, int uid, boolean value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authTokenType);
                    _data.writeInt(uid);
                    _data.writeBoolean(value);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void getAuthToken(IAccountManagerResponse response, Account account, String authTokenType, boolean notifyOnAuthFailure, boolean expectActivityLaunch, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authTokenType);
                    _data.writeBoolean(notifyOnAuthFailure);
                    _data.writeBoolean(expectActivityLaunch);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void addAccount(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    _data.writeString(authTokenType);
                    _data.writeStringArray(requiredFeatures);
                    _data.writeBoolean(expectActivityLaunch);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void addAccountAsUser(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    _data.writeString(authTokenType);
                    _data.writeStringArray(requiredFeatures);
                    _data.writeBoolean(expectActivityLaunch);
                    _data.writeTypedObject(options, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void updateCredentials(IAccountManagerResponse response, Account account, String authTokenType, boolean expectActivityLaunch, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authTokenType);
                    _data.writeBoolean(expectActivityLaunch);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void editProperties(IAccountManagerResponse response, String accountType, boolean expectActivityLaunch) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    _data.writeBoolean(expectActivityLaunch);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void confirmCredentialsAsUser(IAccountManagerResponse response, Account account, Bundle options, boolean expectActivityLaunch, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeTypedObject(options, 0);
                    _data.writeBoolean(expectActivityLaunch);
                    _data.writeInt(userId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public boolean accountAuthenticated(Account account) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void getAuthTokenLabel(IAccountManagerResponse response, String accountType, String authTokenType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    _data.writeString(authTokenType);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void addSharedAccountsFromParentUser(int parentUserId, int userId, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(parentUserId);
                    _data.writeInt(userId);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void renameAccount(IAccountManagerResponse response, Account accountToRename, String newName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(accountToRename, 0);
                    _data.writeString(newName);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public String getPreviousName(Account account) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void startAddAccountSession(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeString(accountType);
                    _data.writeString(authTokenType);
                    _data.writeStringArray(requiredFeatures);
                    _data.writeBoolean(expectActivityLaunch);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void startUpdateCredentialsSession(IAccountManagerResponse response, Account account, String authTokenType, boolean expectActivityLaunch, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authTokenType);
                    _data.writeBoolean(expectActivityLaunch);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void finishSessionAsUser(IAccountManagerResponse response, Bundle sessionBundle, boolean expectActivityLaunch, Bundle appInfo, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(sessionBundle, 0);
                    _data.writeBoolean(expectActivityLaunch);
                    _data.writeTypedObject(appInfo, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public boolean someUserHasAccount(Account account) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void isCredentialsUpdateSuggested(IAccountManagerResponse response, Account account, String statusToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(response);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(statusToken);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public Map getPackagesAndVisibilityForAccount(Account account) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    Map _result = _reply.readHashMap(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public boolean addAccountExplicitlyWithVisibility(Account account, String password, Bundle extras, Map visibility, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(password);
                    _data.writeTypedObject(extras, 0);
                    _data.writeMap(visibility);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public boolean setAccountVisibility(Account a, String packageName, int newVisibility) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(a, 0);
                    _data.writeString(packageName);
                    _data.writeInt(newVisibility);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public int getAccountVisibility(Account a, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(a, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public Map getAccountsAndVisibilityForPackage(String packageName, String accountType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(accountType);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    Map _result = _reply.readHashMap(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void registerAccountListener(String[] accountTypes, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(accountTypes);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void unregisterAccountListener(String[] accountTypes, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(accountTypes);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public boolean hasAccountAccess(Account account, String packageName, UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(packageName);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public IntentSender createRequestAccountAccessIntentSenderAsUser(Account account, String packageName, UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(packageName);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    IntentSender _result = (IntentSender) _reply.readTypedObject(IntentSender.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.accounts.IAccountManager
            public void onAccountAccessed(String token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(token);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 45;
        }
    }
}
