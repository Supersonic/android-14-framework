package android.accounts;

import android.accounts.IAccountManagerResponse;
import android.annotation.SystemApi;
import android.app.Activity;
import android.app.PropertyInvalidatedCache;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.p001pm.UserPackage;
import android.content.res.Resources;
import android.database.SQLException;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Parcelable;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.C4057R;
import com.google.android.collect.Maps;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes.dex */
public class AccountManager {
    public static final String ACCOUNT_ACCESS_TOKEN_TYPE = "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE";
    public static final String ACTION_ACCOUNT_REMOVED = "android.accounts.action.ACCOUNT_REMOVED";
    public static final String ACTION_AUTHENTICATOR_INTENT = "android.accounts.AccountAuthenticator";
    public static final String ACTION_VISIBLE_ACCOUNTS_CHANGED = "android.accounts.action.VISIBLE_ACCOUNTS_CHANGED";
    public static final String AUTHENTICATOR_ATTRIBUTES_NAME = "account-authenticator";
    public static final String AUTHENTICATOR_META_DATA_NAME = "android.accounts.AccountAuthenticator";
    public static final int CACHE_ACCOUNTS_DATA_SIZE = 4;
    public static final String CACHE_KEY_ACCOUNTS_DATA_PROPERTY = "cache_key.system_server.accounts_data";
    public static final String CACHE_KEY_USER_DATA_PROPERTY = "cache_key.system_server.account_user_data";
    public static final int CACHE_USER_DATA_SIZE = 4;
    public static final int ERROR_CODE_BAD_ARGUMENTS = 7;
    public static final int ERROR_CODE_BAD_AUTHENTICATION = 9;
    public static final int ERROR_CODE_BAD_REQUEST = 8;
    public static final int ERROR_CODE_CANCELED = 4;
    public static final int ERROR_CODE_INVALID_RESPONSE = 5;
    public static final int ERROR_CODE_MANAGEMENT_DISABLED_FOR_ACCOUNT_TYPE = 101;
    public static final int ERROR_CODE_NETWORK_ERROR = 3;
    public static final int ERROR_CODE_REMOTE_EXCEPTION = 1;
    public static final int ERROR_CODE_UNSUPPORTED_OPERATION = 6;
    public static final int ERROR_CODE_USER_RESTRICTED = 100;
    public static final String KEY_ACCOUNTS = "accounts";
    public static final String KEY_ACCOUNT_ACCESS_ID = "accountAccessId";
    public static final String KEY_ACCOUNT_AUTHENTICATOR_RESPONSE = "accountAuthenticatorResponse";
    public static final String KEY_ACCOUNT_MANAGER_RESPONSE = "accountManagerResponse";
    public static final String KEY_ACCOUNT_NAME = "authAccount";
    public static final String KEY_ACCOUNT_SESSION_BUNDLE = "accountSessionBundle";
    public static final String KEY_ACCOUNT_STATUS_TOKEN = "accountStatusToken";
    public static final String KEY_ACCOUNT_TYPE = "accountType";
    public static final String KEY_ANDROID_PACKAGE_NAME = "androidPackageName";
    public static final String KEY_AUTHENTICATOR_TYPES = "authenticator_types";
    public static final String KEY_AUTHTOKEN = "authtoken";
    public static final String KEY_AUTH_FAILED_MESSAGE = "authFailedMessage";
    public static final String KEY_AUTH_TOKEN_LABEL = "authTokenLabelKey";
    public static final String KEY_BOOLEAN_RESULT = "booleanResult";
    public static final String KEY_CALLER_PID = "callerPid";
    public static final String KEY_CALLER_UID = "callerUid";
    public static final String KEY_ERROR_CODE = "errorCode";
    public static final String KEY_ERROR_MESSAGE = "errorMessage";
    public static final String KEY_INTENT = "intent";
    public static final String KEY_LAST_AUTHENTICATED_TIME = "lastAuthenticatedTime";
    public static final String KEY_NOTIFY_ON_FAILURE = "notifyOnAuthFailure";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USERDATA = "userdata";
    public static final String LOGIN_ACCOUNTS_CHANGED_ACTION = "android.accounts.LOGIN_ACCOUNTS_CHANGED";
    public static final String PACKAGE_NAME_KEY_LEGACY_NOT_VISIBLE = "android:accounts:key_legacy_not_visible";
    public static final String PACKAGE_NAME_KEY_LEGACY_VISIBLE = "android:accounts:key_legacy_visible";
    private static final String TAG = "AccountManager";
    public static final int VISIBILITY_NOT_VISIBLE = 3;
    public static final int VISIBILITY_UNDEFINED = 0;
    public static final int VISIBILITY_USER_MANAGED_NOT_VISIBLE = 4;
    public static final int VISIBILITY_USER_MANAGED_VISIBLE = 2;
    public static final int VISIBILITY_VISIBLE = 1;
    private final Context mContext;
    private final Handler mMainHandler;
    private final IAccountManager mService;
    PropertyInvalidatedCache<UserPackage, Account[]> mAccountsForUserCache = new PropertyInvalidatedCache<UserPackage, Account[]>(4, CACHE_KEY_ACCOUNTS_DATA_PROPERTY) { // from class: android.accounts.AccountManager.1
        @Override // android.app.PropertyInvalidatedCache
        public Account[] recompute(UserPackage userAndPackage) {
            try {
                return AccountManager.this.mService.getAccountsAsUser(null, userAndPackage.userId, userAndPackage.packageName);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        @Override // android.app.PropertyInvalidatedCache
        public boolean bypass(UserPackage query) {
            return query.userId < 0;
        }

        @Override // android.app.PropertyInvalidatedCache
        public boolean resultEquals(Account[] l, Account[] r) {
            if (l == r) {
                return true;
            }
            if (l == null || r == null) {
                return false;
            }
            return Arrays.equals(l, r);
        }
    };
    PropertyInvalidatedCache<AccountKeyData, String> mUserDataCache = new PropertyInvalidatedCache<AccountKeyData, String>(4, CACHE_KEY_USER_DATA_PROPERTY) { // from class: android.accounts.AccountManager.2
        @Override // android.app.PropertyInvalidatedCache
        public String recompute(AccountKeyData accountKeyData) {
            Account account = accountKeyData.account;
            String key = accountKeyData.key;
            if (account == null) {
                throw new IllegalArgumentException("account is null");
            }
            if (key == null) {
                throw new IllegalArgumentException("key is null");
            }
            try {
                return AccountManager.this.mService.getUserData(account, key);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    };
    private final HashMap<OnAccountsUpdateListener, Handler> mAccountsUpdatedListeners = Maps.newHashMap();
    private final HashMap<OnAccountsUpdateListener, Set<String>> mAccountsUpdatedListenersTypes = Maps.newHashMap();
    private final BroadcastReceiver mAccountsChangedBroadcastReceiver = new BroadcastReceiver() { // from class: android.accounts.AccountManager.20
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Account[] accounts = AccountManager.this.getAccounts();
            synchronized (AccountManager.this.mAccountsUpdatedListeners) {
                for (Map.Entry<OnAccountsUpdateListener, Handler> entry : AccountManager.this.mAccountsUpdatedListeners.entrySet()) {
                    AccountManager.this.postToHandler(entry.getValue(), entry.getKey(), accounts);
                }
            }
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface AccountVisibility {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class AccountKeyData {
        public final Account account;
        public final String key;

        public AccountKeyData(Account Account, String Key) {
            this.account = Account;
            this.key = Key;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }
            if (o.getClass() != getClass()) {
                return false;
            }
            AccountKeyData e = (AccountKeyData) o;
            if (!e.account.equals(this.account) || !e.key.equals(this.key)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(this.account, this.key);
        }
    }

    public AccountManager(Context context, IAccountManager service) {
        this.mContext = context;
        this.mService = service;
        this.mMainHandler = new Handler(context.getMainLooper());
    }

    public AccountManager(Context context, IAccountManager service, Handler handler) {
        this.mContext = context;
        this.mService = service;
        this.mMainHandler = handler;
    }

    public static Bundle sanitizeResult(Bundle result) {
        if (result != null && result.containsKey(KEY_AUTHTOKEN) && !TextUtils.isEmpty(result.getString(KEY_AUTHTOKEN))) {
            Bundle newResult = new Bundle(result);
            newResult.putString(KEY_AUTHTOKEN, "<omitted for logging purposes>");
            return newResult;
        }
        return result;
    }

    public static AccountManager get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        return (AccountManager) context.getSystemService("account");
    }

    public String getPassword(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            return this.mService.getPassword(account);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getUserData(Account account, String key) {
        return this.mUserDataCache.query(new AccountKeyData(account, key));
    }

    public AuthenticatorDescription[] getAuthenticatorTypes() {
        return getAuthenticatorTypesAsUser(this.mContext.getUserId());
    }

    public AuthenticatorDescription[] getAuthenticatorTypesAsUser(int userId) {
        try {
            return this.mService.getAuthenticatorTypes(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Account[] getAccounts() {
        return getAccountsAsUser(this.mContext.getUserId());
    }

    public Account[] getAccountsAsUser(int userId) {
        UserPackage userAndPackage = UserPackage.m187of(userId, this.mContext.getOpPackageName());
        return this.mAccountsForUserCache.query(userAndPackage);
    }

    public Account[] getAccountsForPackage(String packageName, int uid) {
        try {
            return this.mService.getAccountsForPackage(packageName, uid, this.mContext.getOpPackageName());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public Account[] getAccountsByTypeForPackage(String type, String packageName) {
        try {
            return this.mService.getAccountsByTypeForPackage(type, packageName, this.mContext.getOpPackageName());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public Account[] getAccountsByType(String type) {
        return getAccountsByTypeAsUser(type, this.mContext.getUser());
    }

    public Account[] getAccountsByTypeAsUser(String type, UserHandle userHandle) {
        try {
            return this.mService.getAccountsAsUser(type, userHandle.getIdentifier(), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void updateAppPermission(Account account, String authTokenType, int uid, boolean value) {
        try {
            this.mService.updateAppPermission(account, authTokenType, uid, value);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public AccountManagerFuture<String> getAuthTokenLabel(final String accountType, final String authTokenType, AccountManagerCallback<String> callback, Handler handler) {
        if (accountType == null) {
            throw new IllegalArgumentException("accountType is null");
        }
        if (authTokenType == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        return new Future2Task<String>(handler, callback) { // from class: android.accounts.AccountManager.3
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.getAuthTokenLabel(this.mResponse, accountType, authTokenType);
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public String bundleToResult(Bundle bundle) throws AuthenticatorException {
                if (!bundle.containsKey(AccountManager.KEY_AUTH_TOKEN_LABEL)) {
                    throw new AuthenticatorException("no result in response");
                }
                return bundle.getString(AccountManager.KEY_AUTH_TOKEN_LABEL);
            }
        }.start();
    }

    public AccountManagerFuture<Boolean> hasFeatures(Account account, String[] features, AccountManagerCallback<Boolean> callback, Handler handler) {
        return hasFeaturesAsUser(account, features, callback, handler, this.mContext.getUserId());
    }

    private AccountManagerFuture<Boolean> hasFeaturesAsUser(final Account account, final String[] features, AccountManagerCallback<Boolean> callback, Handler handler, final int userId) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (features == null) {
            throw new IllegalArgumentException("features is null");
        }
        return new Future2Task<Boolean>(handler, callback) { // from class: android.accounts.AccountManager.4
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.hasFeatures(this.mResponse, account, features, userId, AccountManager.this.mContext.getOpPackageName());
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public Boolean bundleToResult(Bundle bundle) throws AuthenticatorException {
                if (!bundle.containsKey(AccountManager.KEY_BOOLEAN_RESULT)) {
                    throw new AuthenticatorException("no result in response");
                }
                return Boolean.valueOf(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
            }
        }.start();
    }

    public AccountManagerFuture<Account[]> getAccountsByTypeAndFeatures(final String type, final String[] features, AccountManagerCallback<Account[]> callback, Handler handler) {
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        }
        return new Future2Task<Account[]>(handler, callback) { // from class: android.accounts.AccountManager.5
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.getAccountsByFeatures(this.mResponse, type, features, AccountManager.this.mContext.getOpPackageName());
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public Account[] bundleToResult(Bundle bundle) throws AuthenticatorException {
                if (!bundle.containsKey(AccountManager.KEY_ACCOUNTS)) {
                    throw new AuthenticatorException("no result in response");
                }
                Parcelable[] parcelables = bundle.getParcelableArray(AccountManager.KEY_ACCOUNTS);
                Account[] descs = new Account[parcelables.length];
                for (int i = 0; i < parcelables.length; i++) {
                    descs[i] = (Account) parcelables[i];
                }
                return descs;
            }
        }.start();
    }

    public boolean addAccountExplicitly(Account account, String password, Bundle userdata) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            return this.mService.addAccountExplicitly(account, password, userdata, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean addAccountExplicitly(Account account, String password, Bundle extras, Map<String, Integer> visibility) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            return this.mService.addAccountExplicitlyWithVisibility(account, password, extras, visibility, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Map<String, Integer> getPackagesAndVisibilityForAccount(Account account) {
        try {
            if (account == null) {
                throw new IllegalArgumentException("account is null");
            }
            Map<String, Integer> result = this.mService.getPackagesAndVisibilityForAccount(account);
            return result;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public Map<Account, Integer> getAccountsAndVisibilityForPackage(String packageName, String accountType) {
        try {
            Map<Account, Integer> result = this.mService.getAccountsAndVisibilityForPackage(packageName, accountType);
            return result;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean setAccountVisibility(Account account, String packageName, int visibility) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            return this.mService.setAccountVisibility(account, packageName, visibility);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public int getAccountVisibility(Account account, String packageName) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            return this.mService.getAccountVisibility(account, packageName);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean notifyAccountAuthenticated(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            return this.mService.accountAuthenticated(account);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public AccountManagerFuture<Account> renameAccount(final Account account, final String newName, AccountManagerCallback<Account> callback, Handler handler) {
        if (account == null) {
            throw new IllegalArgumentException("account is null.");
        }
        if (TextUtils.isEmpty(newName)) {
            throw new IllegalArgumentException("newName is empty or null.");
        }
        return new Future2Task<Account>(handler, callback) { // from class: android.accounts.AccountManager.6
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.renameAccount(this.mResponse, account, newName);
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public Account bundleToResult(Bundle bundle) throws AuthenticatorException {
                String name = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                String type = bundle.getString("accountType");
                String accessId = bundle.getString(AccountManager.KEY_ACCOUNT_ACCESS_ID);
                return new Account(name, type, accessId);
            }
        }.start();
    }

    public String getPreviousName(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            return this.mService.getPreviousName(account);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public AccountManagerFuture<Boolean> removeAccount(Account account, AccountManagerCallback<Boolean> callback, Handler handler) {
        return removeAccountAsUser(account, callback, handler, this.mContext.getUser());
    }

    public AccountManagerFuture<Bundle> removeAccount(Account account, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        return removeAccountAsUser(account, activity, callback, handler, this.mContext.getUser());
    }

    @Deprecated
    public AccountManagerFuture<Boolean> removeAccountAsUser(final Account account, AccountManagerCallback<Boolean> callback, Handler handler, final UserHandle userHandle) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (userHandle == null) {
            throw new IllegalArgumentException("userHandle is null");
        }
        return new Future2Task<Boolean>(handler, callback) { // from class: android.accounts.AccountManager.7
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.removeAccountAsUser(this.mResponse, account, false, userHandle.getIdentifier());
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public Boolean bundleToResult(Bundle bundle) throws AuthenticatorException {
                if (!bundle.containsKey(AccountManager.KEY_BOOLEAN_RESULT)) {
                    throw new AuthenticatorException("no result in response");
                }
                return Boolean.valueOf(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
            }
        }.start();
    }

    public AccountManagerFuture<Bundle> removeAccountAsUser(final Account account, final Activity activity, AccountManagerCallback<Bundle> callback, Handler handler, final UserHandle userHandle) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (userHandle == null) {
            throw new IllegalArgumentException("userHandle is null");
        }
        return new AmsTask(activity, handler, callback) { // from class: android.accounts.AccountManager.8
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.removeAccountAsUser(this.mResponse, account, activity != null, userHandle.getIdentifier());
            }
        }.start();
    }

    public boolean removeAccountExplicitly(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            return this.mService.removeAccountExplicitly(account);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void invalidateAuthToken(String accountType, String authToken) {
        if (accountType == null) {
            throw new IllegalArgumentException("accountType is null");
        }
        if (authToken != null) {
            try {
                this.mService.invalidateAuthToken(accountType, authToken);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public String peekAuthToken(Account account, String authTokenType) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (authTokenType == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        try {
            return this.mService.peekAuthToken(account, authTokenType);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setPassword(Account account, String password) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            this.mService.setPassword(account, password);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void clearPassword(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        try {
            this.mService.clearPassword(account);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setUserData(Account account, String key, String value) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        try {
            this.mService.setUserData(account, key, value);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setAuthToken(Account account, String authTokenType, String authToken) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (authTokenType == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        try {
            this.mService.setAuthToken(account, authTokenType, authToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String blockingGetAuthToken(Account account, String authTokenType, boolean notifyAuthFailure) throws OperationCanceledException, IOException, AuthenticatorException {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (authTokenType == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        Bundle bundle = getAuthToken(account, authTokenType, notifyAuthFailure, null, null).getResult();
        if (bundle == null) {
            Log.m110e(TAG, "blockingGetAuthToken: null was returned from getResult() for " + account + ", authTokenType " + authTokenType);
            return null;
        }
        return bundle.getString(KEY_AUTHTOKEN);
    }

    public AccountManagerFuture<Bundle> getAuthToken(final Account account, final String authTokenType, Bundle options, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        if (account != null) {
            if (authTokenType != null) {
                final Bundle optionsIn = new Bundle();
                if (options != null) {
                    optionsIn.putAll(options);
                }
                optionsIn.putString(KEY_ANDROID_PACKAGE_NAME, this.mContext.getPackageName());
                return new AmsTask(activity, handler, callback) { // from class: android.accounts.AccountManager.9
                    @Override // android.accounts.AccountManager.AmsTask
                    public void doWork() throws RemoteException {
                        AccountManager.this.mService.getAuthToken(this.mResponse, account, authTokenType, false, true, optionsIn);
                    }
                }.start();
            }
            throw new IllegalArgumentException("authTokenType is null");
        }
        throw new IllegalArgumentException("account is null");
    }

    @Deprecated
    public AccountManagerFuture<Bundle> getAuthToken(Account account, String authTokenType, boolean notifyAuthFailure, AccountManagerCallback<Bundle> callback, Handler handler) {
        return getAuthToken(account, authTokenType, (Bundle) null, notifyAuthFailure, callback, handler);
    }

    public AccountManagerFuture<Bundle> getAuthToken(final Account account, final String authTokenType, Bundle options, final boolean notifyAuthFailure, AccountManagerCallback<Bundle> callback, Handler handler) {
        if (account != null) {
            if (authTokenType != null) {
                final Bundle optionsIn = new Bundle();
                if (options != null) {
                    optionsIn.putAll(options);
                }
                optionsIn.putString(KEY_ANDROID_PACKAGE_NAME, this.mContext.getPackageName());
                return new AmsTask(null, handler, callback) { // from class: android.accounts.AccountManager.10
                    @Override // android.accounts.AccountManager.AmsTask
                    public void doWork() throws RemoteException {
                        AccountManager.this.mService.getAuthToken(this.mResponse, account, authTokenType, notifyAuthFailure, false, optionsIn);
                    }
                }.start();
            }
            throw new IllegalArgumentException("authTokenType is null");
        }
        throw new IllegalArgumentException("account is null");
    }

    public AccountManagerFuture<Bundle> addAccount(final String accountType, final String authTokenType, final String[] requiredFeatures, Bundle addAccountOptions, final Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        if (Process.myUserHandle().equals(this.mContext.getUser())) {
            if (accountType == null) {
                throw new IllegalArgumentException("accountType is null");
            }
            final Bundle optionsIn = new Bundle();
            if (addAccountOptions != null) {
                optionsIn.putAll(addAccountOptions);
            }
            optionsIn.putString(KEY_ANDROID_PACKAGE_NAME, this.mContext.getPackageName());
            return new AmsTask(activity, handler, callback) { // from class: android.accounts.AccountManager.11
                @Override // android.accounts.AccountManager.AmsTask
                public void doWork() throws RemoteException {
                    AccountManager.this.mService.addAccount(this.mResponse, accountType, authTokenType, requiredFeatures, activity != null, optionsIn);
                }
            }.start();
        }
        return addAccountAsUser(accountType, authTokenType, requiredFeatures, addAccountOptions, activity, callback, handler, this.mContext.getUser());
    }

    public AccountManagerFuture<Bundle> addAccountAsUser(final String accountType, final String authTokenType, final String[] requiredFeatures, Bundle addAccountOptions, final Activity activity, AccountManagerCallback<Bundle> callback, Handler handler, final UserHandle userHandle) {
        if (accountType != null) {
            if (userHandle != null) {
                final Bundle optionsIn = new Bundle();
                if (addAccountOptions != null) {
                    optionsIn.putAll(addAccountOptions);
                }
                optionsIn.putString(KEY_ANDROID_PACKAGE_NAME, this.mContext.getPackageName());
                return new AmsTask(activity, handler, callback) { // from class: android.accounts.AccountManager.12
                    @Override // android.accounts.AccountManager.AmsTask
                    public void doWork() throws RemoteException {
                        AccountManager.this.mService.addAccountAsUser(this.mResponse, accountType, authTokenType, requiredFeatures, activity != null, optionsIn, userHandle.getIdentifier());
                    }
                }.start();
            }
            throw new IllegalArgumentException("userHandle is null");
        }
        throw new IllegalArgumentException("accountType is null");
    }

    public void addSharedAccountsFromParentUser(UserHandle parentUser, UserHandle user) {
        try {
            this.mService.addSharedAccountsFromParentUser(parentUser.getIdentifier(), user.getIdentifier(), this.mContext.getOpPackageName());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public AccountManagerFuture<Boolean> copyAccountToUser(final Account account, final UserHandle fromUser, final UserHandle toUser, AccountManagerCallback<Boolean> callback, Handler handler) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (toUser == null || fromUser == null) {
            throw new IllegalArgumentException("fromUser and toUser cannot be null");
        }
        return new Future2Task<Boolean>(handler, callback) { // from class: android.accounts.AccountManager.13
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.copyAccountToUser(this.mResponse, account, fromUser.getIdentifier(), toUser.getIdentifier());
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public Boolean bundleToResult(Bundle bundle) throws AuthenticatorException {
                if (!bundle.containsKey(AccountManager.KEY_BOOLEAN_RESULT)) {
                    throw new AuthenticatorException("no result in response");
                }
                return Boolean.valueOf(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
            }
        }.start();
    }

    public AccountManagerFuture<Bundle> confirmCredentials(Account account, Bundle options, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        return confirmCredentialsAsUser(account, options, activity, callback, handler, this.mContext.getUser());
    }

    public AccountManagerFuture<Bundle> confirmCredentialsAsUser(final Account account, final Bundle options, final Activity activity, AccountManagerCallback<Bundle> callback, Handler handler, UserHandle userHandle) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        final int userId = userHandle.getIdentifier();
        return new AmsTask(activity, handler, callback) { // from class: android.accounts.AccountManager.14
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.confirmCredentialsAsUser(this.mResponse, account, options, activity != null, userId);
            }
        }.start();
    }

    public AccountManagerFuture<Bundle> updateCredentials(final Account account, final String authTokenType, final Bundle options, final Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        return new AmsTask(activity, handler, callback) { // from class: android.accounts.AccountManager.15
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.updateCredentials(this.mResponse, account, authTokenType, activity != null, options);
            }
        }.start();
    }

    public AccountManagerFuture<Bundle> editProperties(final String accountType, final Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        if (accountType == null) {
            throw new IllegalArgumentException("accountType is null");
        }
        return new AmsTask(activity, handler, callback) { // from class: android.accounts.AccountManager.16
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.editProperties(this.mResponse, accountType, activity != null);
            }
        }.start();
    }

    public boolean someUserHasAccount(Account account) {
        try {
            return this.mService.someUserHasAccount(account);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureNotOnMainThread() {
        Looper looper = Looper.myLooper();
        if (looper != null && looper == this.mContext.getMainLooper()) {
            IllegalStateException exception = new IllegalStateException("calling this from your main thread can lead to deadlock");
            Log.m109e(TAG, "calling this from your main thread can lead to deadlock and/or ANRs", exception);
            if (this.mContext.getApplicationInfo().targetSdkVersion >= 8) {
                throw exception;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postToHandler(Handler handler, final AccountManagerCallback<Bundle> callback, final AccountManagerFuture<Bundle> future) {
        (handler == null ? this.mMainHandler : handler).post(new Runnable() { // from class: android.accounts.AccountManager.17
            @Override // java.lang.Runnable
            public void run() {
                callback.run(future);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postToHandler(Handler handler, final OnAccountsUpdateListener listener, Account[] accounts) {
        final Account[] accountsCopy = new Account[accounts.length];
        System.arraycopy(accounts, 0, accountsCopy, 0, accountsCopy.length);
        (handler == null ? this.mMainHandler : handler).post(new Runnable() { // from class: android.accounts.AccountManager.18
            @Override // java.lang.Runnable
            public void run() {
                Account[] accountArr;
                synchronized (AccountManager.this.mAccountsUpdatedListeners) {
                    try {
                        if (AccountManager.this.mAccountsUpdatedListeners.containsKey(listener)) {
                            Set<String> types = (Set) AccountManager.this.mAccountsUpdatedListenersTypes.get(listener);
                            if (types != null) {
                                ArrayList<Account> filtered = new ArrayList<>();
                                for (Account account : accountsCopy) {
                                    if (types.contains(account.type)) {
                                        filtered.add(account);
                                    }
                                }
                                listener.onAccountsUpdated((Account[]) filtered.toArray(new Account[filtered.size()]));
                            } else {
                                listener.onAccountsUpdated(accountsCopy);
                            }
                        }
                    } catch (SQLException e) {
                        Log.m109e(AccountManager.TAG, "Can't update accounts", e);
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public abstract class AmsTask extends FutureTask<Bundle> implements AccountManagerFuture<Bundle> {
        final Activity mActivity;
        final AccountManagerCallback<Bundle> mCallback;
        final Handler mHandler;
        final IAccountManagerResponse mResponse;

        public abstract void doWork() throws RemoteException;

        public AmsTask(Activity activity, Handler handler, AccountManagerCallback<Bundle> callback) {
            super(new Callable<Bundle>() { // from class: android.accounts.AccountManager.AmsTask.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Bundle call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });
            this.mHandler = handler;
            this.mCallback = callback;
            this.mActivity = activity;
            this.mResponse = new Response();
        }

        public final AccountManagerFuture<Bundle> start() {
            try {
                doWork();
            } catch (RemoteException e) {
                setException(e);
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.util.concurrent.FutureTask
        public void set(Bundle bundle) {
            if (bundle == null) {
                Log.m109e(AccountManager.TAG, "the bundle must not be null", new Exception());
            }
            super.set((AmsTask) bundle);
        }

        private Bundle internalGetResult(Long timeout, TimeUnit unit) throws OperationCanceledException, IOException, AuthenticatorException {
            if (!isDone()) {
                AccountManager.this.ensureNotOnMainThread();
            }
            try {
                try {
                    try {
                        return timeout == null ? get() : get(timeout.longValue(), unit);
                    } catch (InterruptedException e) {
                        cancel(true);
                        throw new OperationCanceledException();
                    } catch (ExecutionException e2) {
                        Throwable cause = e2.getCause();
                        if (cause instanceof IOException) {
                            throw ((IOException) cause);
                        }
                        if (cause instanceof UnsupportedOperationException) {
                            throw new AuthenticatorException(cause);
                        }
                        if (cause instanceof AuthenticatorException) {
                            throw ((AuthenticatorException) cause);
                        }
                        if (cause instanceof RuntimeException) {
                            throw ((RuntimeException) cause);
                        }
                        if (cause instanceof Error) {
                            throw ((Error) cause);
                        }
                        throw new IllegalStateException(cause);
                    }
                } catch (CancellationException e3) {
                    throw new OperationCanceledException();
                } catch (TimeoutException e4) {
                    cancel(true);
                    throw new OperationCanceledException();
                }
            } finally {
                cancel(true);
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.accounts.AccountManagerFuture
        public Bundle getResult() throws OperationCanceledException, IOException, AuthenticatorException {
            return internalGetResult(null, null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.accounts.AccountManagerFuture
        public Bundle getResult(long timeout, TimeUnit unit) throws OperationCanceledException, IOException, AuthenticatorException {
            return internalGetResult(Long.valueOf(timeout), unit);
        }

        @Override // java.util.concurrent.FutureTask
        protected void done() {
            AccountManagerCallback<Bundle> accountManagerCallback = this.mCallback;
            if (accountManagerCallback != null) {
                AccountManager.this.postToHandler(this.mHandler, accountManagerCallback, this);
            }
        }

        /* loaded from: classes.dex */
        private class Response extends IAccountManagerResponse.Stub {
            private Response() {
            }

            @Override // android.accounts.IAccountManagerResponse
            public void onResult(Bundle bundle) {
                if (bundle == null) {
                    onError(5, "null bundle returned");
                    return;
                }
                Intent intent = (Intent) bundle.getParcelable("intent", Intent.class);
                if (intent != null && AmsTask.this.mActivity != null) {
                    AmsTask.this.mActivity.startActivity(intent);
                } else if (bundle.getBoolean("retry")) {
                    try {
                        AmsTask.this.doWork();
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                } else {
                    AmsTask.this.set(bundle);
                }
            }

            @Override // android.accounts.IAccountManagerResponse
            public void onError(int code, String message) {
                if (code == 4 || code == 100 || code == 101) {
                    AmsTask.this.cancel(true);
                    return;
                }
                AmsTask amsTask = AmsTask.this;
                amsTask.setException(AccountManager.this.convertErrorToException(code, message));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public abstract class BaseFutureTask<T> extends FutureTask<T> {
        final Handler mHandler;
        public final IAccountManagerResponse mResponse;

        public abstract T bundleToResult(Bundle bundle) throws AuthenticatorException;

        public abstract void doWork() throws RemoteException;

        public BaseFutureTask(Handler handler) {
            super(new Callable<T>() { // from class: android.accounts.AccountManager.BaseFutureTask.1
                @Override // java.util.concurrent.Callable
                public T call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });
            this.mHandler = handler;
            this.mResponse = new Response();
        }

        protected void postRunnableToHandler(Runnable runnable) {
            Handler handler = this.mHandler;
            if (handler == null) {
                handler = AccountManager.this.mMainHandler;
            }
            handler.post(runnable);
        }

        protected void startTask() {
            try {
                doWork();
            } catch (RemoteException e) {
                setException(e);
            }
        }

        /* loaded from: classes.dex */
        protected class Response extends IAccountManagerResponse.Stub {
            protected Response() {
            }

            @Override // android.accounts.IAccountManagerResponse
            public void onResult(Bundle bundle) {
                try {
                    Object bundleToResult = BaseFutureTask.this.bundleToResult(bundle);
                    if (bundleToResult != null) {
                        BaseFutureTask.this.set(bundleToResult);
                    }
                } catch (AuthenticatorException | ClassCastException e) {
                    onError(5, "no result in response");
                }
            }

            @Override // android.accounts.IAccountManagerResponse
            public void onError(int code, String message) {
                if (code == 4 || code == 100 || code == 101) {
                    BaseFutureTask.this.cancel(true);
                    return;
                }
                BaseFutureTask baseFutureTask = BaseFutureTask.this;
                baseFutureTask.setException(AccountManager.this.convertErrorToException(code, message));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public abstract class Future2Task<T> extends BaseFutureTask<T> implements AccountManagerFuture<T> {
        final AccountManagerCallback<T> mCallback;

        public Future2Task(Handler handler, AccountManagerCallback<T> callback) {
            super(handler);
            this.mCallback = callback;
        }

        @Override // java.util.concurrent.FutureTask
        protected void done() {
            if (this.mCallback != null) {
                postRunnableToHandler(new Runnable() { // from class: android.accounts.AccountManager.Future2Task.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Future2Task.this.mCallback.run(Future2Task.this);
                    }
                });
            }
        }

        public Future2Task<T> start() {
            startTask();
            return this;
        }

        private T internalGetResult(Long timeout, TimeUnit unit) throws OperationCanceledException, IOException, AuthenticatorException {
            if (!isDone()) {
                AccountManager.this.ensureNotOnMainThread();
            }
            try {
                try {
                    return timeout == null ? (T) get() : (T) get(timeout.longValue(), unit);
                } catch (InterruptedException e) {
                    cancel(true);
                    throw new OperationCanceledException();
                } catch (CancellationException e2) {
                    cancel(true);
                    throw new OperationCanceledException();
                } catch (ExecutionException e3) {
                    Throwable cause = e3.getCause();
                    if (cause instanceof IOException) {
                        throw ((IOException) cause);
                    }
                    if (cause instanceof UnsupportedOperationException) {
                        throw new AuthenticatorException(cause);
                    }
                    if (cause instanceof AuthenticatorException) {
                        throw ((AuthenticatorException) cause);
                    }
                    if (cause instanceof RuntimeException) {
                        throw ((RuntimeException) cause);
                    }
                    if (cause instanceof Error) {
                        throw ((Error) cause);
                    }
                    throw new IllegalStateException(cause);
                } catch (TimeoutException e4) {
                    cancel(true);
                    throw new OperationCanceledException();
                }
            } finally {
                cancel(true);
            }
        }

        @Override // android.accounts.AccountManagerFuture
        public T getResult() throws OperationCanceledException, IOException, AuthenticatorException {
            return internalGetResult(null, null);
        }

        @Override // android.accounts.AccountManagerFuture
        public T getResult(long timeout, TimeUnit unit) throws OperationCanceledException, IOException, AuthenticatorException {
            return internalGetResult(Long.valueOf(timeout), unit);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Exception convertErrorToException(int code, String message) {
        if (code == 3) {
            return new IOException(message);
        }
        if (code == 6) {
            return new UnsupportedOperationException(message);
        }
        if (code == 5) {
            return new AuthenticatorException(message);
        }
        if (code == 7) {
            return new IllegalArgumentException(message);
        }
        return new AuthenticatorException(message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getAccountByTypeAndFeatures(final String accountType, final String[] features, AccountManagerCallback<Bundle> callback, Handler handler) {
        new AmsTask(null, handler, callback) { // from class: android.accounts.AccountManager.19
            @Override // android.accounts.AccountManager.AmsTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.getAccountByTypeAndFeatures(this.mResponse, accountType, features, AccountManager.this.mContext.getOpPackageName());
            }
        }.start();
    }

    /* loaded from: classes.dex */
    private class GetAuthTokenByTypeAndFeaturesTask extends AmsTask implements AccountManagerCallback<Bundle> {
        final String mAccountType;
        final Bundle mAddAccountOptions;
        final String mAuthTokenType;
        final String[] mFeatures;
        volatile AccountManagerFuture<Bundle> mFuture;
        final Bundle mLoginOptions;
        final AccountManagerCallback<Bundle> mMyCallback;
        private volatile int mNumAccounts;

        GetAuthTokenByTypeAndFeaturesTask(String accountType, String authTokenType, String[] features, Activity activityForPrompting, Bundle addAccountOptions, Bundle loginOptions, AccountManagerCallback<Bundle> callback, Handler handler) {
            super(activityForPrompting, handler, callback);
            this.mFuture = null;
            this.mNumAccounts = 0;
            if (accountType == null) {
                throw new IllegalArgumentException("account type is null");
            }
            this.mAccountType = accountType;
            this.mAuthTokenType = authTokenType;
            this.mFeatures = features;
            this.mAddAccountOptions = addAccountOptions;
            this.mLoginOptions = loginOptions;
            this.mMyCallback = this;
        }

        @Override // android.accounts.AccountManager.AmsTask
        public void doWork() throws RemoteException {
            AccountManager.this.getAccountByTypeAndFeatures(this.mAccountType, this.mFeatures, new AccountManagerCallback<Bundle>() { // from class: android.accounts.AccountManager.GetAuthTokenByTypeAndFeaturesTask.1
                @Override // android.accounts.AccountManagerCallback
                public void run(AccountManagerFuture<Bundle> future) {
                    try {
                        Bundle result = future.getResult();
                        String accountName = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                        String accountType = result.getString("accountType");
                        if (accountName == null) {
                            if (GetAuthTokenByTypeAndFeaturesTask.this.mActivity != null) {
                                GetAuthTokenByTypeAndFeaturesTask getAuthTokenByTypeAndFeaturesTask = GetAuthTokenByTypeAndFeaturesTask.this;
                                getAuthTokenByTypeAndFeaturesTask.mFuture = AccountManager.this.addAccount(GetAuthTokenByTypeAndFeaturesTask.this.mAccountType, GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, GetAuthTokenByTypeAndFeaturesTask.this.mFeatures, GetAuthTokenByTypeAndFeaturesTask.this.mAddAccountOptions, GetAuthTokenByTypeAndFeaturesTask.this.mActivity, GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
                                return;
                            }
                            Bundle result2 = new Bundle();
                            result2.putString(AccountManager.KEY_ACCOUNT_NAME, null);
                            result2.putString("accountType", null);
                            result2.putString(AccountManager.KEY_AUTHTOKEN, null);
                            result2.putBinder(AccountManager.KEY_ACCOUNT_ACCESS_ID, null);
                            try {
                                GetAuthTokenByTypeAndFeaturesTask.this.mResponse.onResult(result2);
                                return;
                            } catch (RemoteException e) {
                                return;
                            }
                        }
                        GetAuthTokenByTypeAndFeaturesTask.this.mNumAccounts = 1;
                        Account account = new Account(accountName, accountType);
                        if (GetAuthTokenByTypeAndFeaturesTask.this.mActivity == null) {
                            GetAuthTokenByTypeAndFeaturesTask getAuthTokenByTypeAndFeaturesTask2 = GetAuthTokenByTypeAndFeaturesTask.this;
                            getAuthTokenByTypeAndFeaturesTask2.mFuture = AccountManager.this.getAuthToken(account, GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, false, GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
                            return;
                        }
                        GetAuthTokenByTypeAndFeaturesTask getAuthTokenByTypeAndFeaturesTask3 = GetAuthTokenByTypeAndFeaturesTask.this;
                        getAuthTokenByTypeAndFeaturesTask3.mFuture = AccountManager.this.getAuthToken(account, GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, GetAuthTokenByTypeAndFeaturesTask.this.mLoginOptions, GetAuthTokenByTypeAndFeaturesTask.this.mActivity, GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
                    } catch (AuthenticatorException e2) {
                        GetAuthTokenByTypeAndFeaturesTask.this.setException(e2);
                    } catch (OperationCanceledException e3) {
                        GetAuthTokenByTypeAndFeaturesTask.this.setException(e3);
                    } catch (IOException e4) {
                        GetAuthTokenByTypeAndFeaturesTask.this.setException(e4);
                    }
                }
            }, this.mHandler);
        }

        @Override // android.accounts.AccountManagerCallback
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle result = future.getResult();
                if (this.mNumAccounts == 0) {
                    String accountName = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                    String accountType = result.getString("accountType");
                    if (!TextUtils.isEmpty(accountName) && !TextUtils.isEmpty(accountType)) {
                        String accessId = result.getString(AccountManager.KEY_ACCOUNT_ACCESS_ID);
                        Account account = new Account(accountName, accountType, accessId);
                        this.mNumAccounts = 1;
                        AccountManager.this.getAuthToken(account, this.mAuthTokenType, (Bundle) null, this.mActivity, this.mMyCallback, this.mHandler);
                        return;
                    }
                    setException(new AuthenticatorException("account not in result"));
                    return;
                }
                set(result);
            } catch (AuthenticatorException e) {
                setException(e);
            } catch (OperationCanceledException e2) {
                cancel(true);
            } catch (IOException e3) {
                setException(e3);
            }
        }
    }

    public AccountManagerFuture<Bundle> getAuthTokenByFeatures(String accountType, String authTokenType, String[] features, Activity activity, Bundle addAccountOptions, Bundle getAuthTokenOptions, AccountManagerCallback<Bundle> callback, Handler handler) {
        if (accountType == null) {
            throw new IllegalArgumentException("account type is null");
        }
        if (authTokenType == null) {
            throw new IllegalArgumentException("authTokenType is null");
        }
        GetAuthTokenByTypeAndFeaturesTask task = new GetAuthTokenByTypeAndFeaturesTask(accountType, authTokenType, features, activity, addAccountOptions, getAuthTokenOptions, callback, handler);
        task.start();
        return task;
    }

    @Deprecated
    public static Intent newChooseAccountIntent(Account selectedAccount, ArrayList<Account> allowableAccounts, String[] allowableAccountTypes, boolean alwaysPromptForAccount, String descriptionOverrideText, String addAccountAuthTokenType, String[] addAccountRequiredFeatures, Bundle addAccountOptions) {
        return newChooseAccountIntent(selectedAccount, allowableAccounts, allowableAccountTypes, descriptionOverrideText, addAccountAuthTokenType, addAccountRequiredFeatures, addAccountOptions);
    }

    public static Intent newChooseAccountIntent(Account selectedAccount, List<Account> allowableAccounts, String[] allowableAccountTypes, String descriptionOverrideText, String addAccountAuthTokenType, String[] addAccountRequiredFeatures, Bundle addAccountOptions) {
        Intent intent = new Intent();
        ComponentName componentName = ComponentName.unflattenFromString(Resources.getSystem().getString(C4057R.string.config_chooseTypeAndAccountActivity));
        intent.setClassName(componentName.getPackageName(), componentName.getClassName());
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ALLOWABLE_ACCOUNTS_ARRAYLIST, allowableAccounts == null ? null : new ArrayList(allowableAccounts));
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY, allowableAccountTypes);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ADD_ACCOUNT_OPTIONS_BUNDLE, addAccountOptions);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_SELECTED_ACCOUNT, selectedAccount);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_DESCRIPTION_TEXT_OVERRIDE, descriptionOverrideText);
        intent.putExtra("authTokenType", addAccountAuthTokenType);
        intent.putExtra(ChooseTypeAndAccountActivity.EXTRA_ADD_ACCOUNT_REQUIRED_FEATURES_STRING_ARRAY, addAccountRequiredFeatures);
        return intent;
    }

    public void addOnAccountsUpdatedListener(OnAccountsUpdateListener listener, Handler handler, boolean updateImmediately) {
        addOnAccountsUpdatedListener(listener, handler, updateImmediately, null);
    }

    public void addOnAccountsUpdatedListener(OnAccountsUpdateListener listener, Handler handler, boolean updateImmediately, String[] accountTypes) {
        if (listener == null) {
            throw new IllegalArgumentException("the listener is null");
        }
        synchronized (this.mAccountsUpdatedListeners) {
            if (this.mAccountsUpdatedListeners.containsKey(listener)) {
                throw new IllegalStateException("this listener is already added");
            }
            boolean wasEmpty = this.mAccountsUpdatedListeners.isEmpty();
            this.mAccountsUpdatedListeners.put(listener, handler);
            if (accountTypes != null) {
                this.mAccountsUpdatedListenersTypes.put(listener, new HashSet(Arrays.asList(accountTypes)));
            } else {
                this.mAccountsUpdatedListenersTypes.put(listener, null);
            }
            if (wasEmpty) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION_VISIBLE_ACCOUNTS_CHANGED);
                intentFilter.addAction(Intent.ACTION_DEVICE_STORAGE_OK);
                this.mContext.registerReceiver(this.mAccountsChangedBroadcastReceiver, intentFilter);
            }
            try {
                this.mService.registerAccountListener(accountTypes, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        if (updateImmediately) {
            postToHandler(handler, listener, getAccounts());
        }
    }

    public void removeOnAccountsUpdatedListener(OnAccountsUpdateListener listener) {
        String[] accountsArray;
        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        synchronized (this.mAccountsUpdatedListeners) {
            if (!this.mAccountsUpdatedListeners.containsKey(listener)) {
                Log.m110e(TAG, "Listener was not previously added");
                return;
            }
            Set<String> accountTypes = this.mAccountsUpdatedListenersTypes.get(listener);
            if (accountTypes != null) {
                accountsArray = (String[]) accountTypes.toArray(new String[accountTypes.size()]);
            } else {
                accountsArray = null;
            }
            this.mAccountsUpdatedListeners.remove(listener);
            this.mAccountsUpdatedListenersTypes.remove(listener);
            if (this.mAccountsUpdatedListeners.isEmpty()) {
                this.mContext.unregisterReceiver(this.mAccountsChangedBroadcastReceiver);
            }
            try {
                this.mService.unregisterAccountListener(accountsArray, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public AccountManagerFuture<Bundle> startAddAccountSession(final String accountType, final String authTokenType, final String[] requiredFeatures, Bundle options, final Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        if (accountType != null) {
            final Bundle optionsIn = new Bundle();
            if (options != null) {
                optionsIn.putAll(options);
            }
            optionsIn.putString(KEY_ANDROID_PACKAGE_NAME, this.mContext.getPackageName());
            return new AmsTask(activity, handler, callback) { // from class: android.accounts.AccountManager.21
                @Override // android.accounts.AccountManager.AmsTask
                public void doWork() throws RemoteException {
                    AccountManager.this.mService.startAddAccountSession(this.mResponse, accountType, authTokenType, requiredFeatures, activity != null, optionsIn);
                }
            }.start();
        }
        throw new IllegalArgumentException("accountType is null");
    }

    public AccountManagerFuture<Bundle> startUpdateCredentialsSession(final Account account, final String authTokenType, Bundle options, final Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        if (account != null) {
            final Bundle optionsIn = new Bundle();
            if (options != null) {
                optionsIn.putAll(options);
            }
            optionsIn.putString(KEY_ANDROID_PACKAGE_NAME, this.mContext.getPackageName());
            return new AmsTask(activity, handler, callback) { // from class: android.accounts.AccountManager.22
                @Override // android.accounts.AccountManager.AmsTask
                public void doWork() throws RemoteException {
                    AccountManager.this.mService.startUpdateCredentialsSession(this.mResponse, account, authTokenType, activity != null, optionsIn);
                }
            }.start();
        }
        throw new IllegalArgumentException("account is null");
    }

    public AccountManagerFuture<Bundle> finishSession(Bundle sessionBundle, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        return finishSessionAsUser(sessionBundle, activity, this.mContext.getUser(), callback, handler);
    }

    @SystemApi
    public AccountManagerFuture<Bundle> finishSessionAsUser(final Bundle sessionBundle, final Activity activity, final UserHandle userHandle, AccountManagerCallback<Bundle> callback, Handler handler) {
        if (sessionBundle != null) {
            final Bundle appInfo = new Bundle();
            appInfo.putString(KEY_ANDROID_PACKAGE_NAME, this.mContext.getPackageName());
            return new AmsTask(activity, handler, callback) { // from class: android.accounts.AccountManager.23
                @Override // android.accounts.AccountManager.AmsTask
                public void doWork() throws RemoteException {
                    AccountManager.this.mService.finishSessionAsUser(this.mResponse, sessionBundle, activity != null, appInfo, userHandle.getIdentifier());
                }
            }.start();
        }
        throw new IllegalArgumentException("sessionBundle is null");
    }

    public AccountManagerFuture<Boolean> isCredentialsUpdateSuggested(final Account account, final String statusToken, AccountManagerCallback<Boolean> callback, Handler handler) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        if (TextUtils.isEmpty(statusToken)) {
            throw new IllegalArgumentException("status token is empty");
        }
        return new Future2Task<Boolean>(handler, callback) { // from class: android.accounts.AccountManager.24
            @Override // android.accounts.AccountManager.BaseFutureTask
            public void doWork() throws RemoteException {
                AccountManager.this.mService.isCredentialsUpdateSuggested(this.mResponse, account, statusToken);
            }

            @Override // android.accounts.AccountManager.BaseFutureTask
            public Boolean bundleToResult(Bundle bundle) throws AuthenticatorException {
                if (!bundle.containsKey(AccountManager.KEY_BOOLEAN_RESULT)) {
                    throw new AuthenticatorException("no result in response");
                }
                return Boolean.valueOf(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
            }
        }.start();
    }

    public boolean hasAccountAccess(Account account, String packageName, UserHandle userHandle) {
        try {
            return this.mService.hasAccountAccess(account, packageName, userHandle);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public IntentSender createRequestAccountAccessIntentSenderAsUser(Account account, String packageName, UserHandle userHandle) {
        try {
            return this.mService.createRequestAccountAccessIntentSenderAsUser(account, packageName, userHandle);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void invalidateLocalAccountsDataCaches() {
        PropertyInvalidatedCache.invalidateCache(CACHE_KEY_ACCOUNTS_DATA_PROPERTY);
    }

    public void disableLocalAccountCaches() {
        this.mAccountsForUserCache.disableLocal();
    }

    public static void invalidateLocalAccountUserDataCaches() {
        PropertyInvalidatedCache.invalidateCache(CACHE_KEY_USER_DATA_PROPERTY);
    }

    public void disableLocalUserInfoCaches() {
        this.mUserDataCache.disableLocal();
    }
}
