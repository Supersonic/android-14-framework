package android.security;

import android.annotation.SystemApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.security.IKeyChainAliasCallback;
import android.security.IKeyChainService;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.security.keystore2.AndroidKeyStoreProvider;
import android.system.keystore2.KeyDescriptor;
import android.util.Log;
import com.android.org.conscrypt.TrustedCertificateStore;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.security.KeyPair;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javax.security.auth.x500.X500Principal;
/* loaded from: classes3.dex */
public final class KeyChain {
    public static final String ACCOUNT_TYPE = "com.android.keychain";
    private static final String ACTION_CHOOSER = "com.android.keychain.CHOOSER";
    private static final String ACTION_INSTALL = "android.credentials.INSTALL";
    public static final String ACTION_KEYCHAIN_CHANGED = "android.security.action.KEYCHAIN_CHANGED";
    public static final String ACTION_KEY_ACCESS_CHANGED = "android.security.action.KEY_ACCESS_CHANGED";
    public static final String ACTION_STORAGE_CHANGED = "android.security.STORAGE_CHANGED";
    public static final String ACTION_TRUST_STORE_CHANGED = "android.security.action.TRUST_STORE_CHANGED";
    private static final String CERT_INSTALLER_PACKAGE = "com.android.certinstaller";
    public static final String EXTRA_ALIAS = "alias";
    public static final String EXTRA_AUTHENTICATION_POLICY = "android.security.extra.AUTHENTICATION_POLICY";
    public static final String EXTRA_CERTIFICATE = "CERT";
    public static final String EXTRA_ISSUERS = "issuers";
    public static final String EXTRA_KEY_ACCESSIBLE = "android.security.extra.KEY_ACCESSIBLE";
    public static final String EXTRA_KEY_ALIAS = "android.security.extra.KEY_ALIAS";
    public static final String EXTRA_KEY_TYPES = "key_types";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_PKCS12 = "PKCS12";
    public static final String EXTRA_RESPONSE = "response";
    public static final String EXTRA_SENDER = "sender";
    public static final String EXTRA_URI = "uri";
    public static final String GRANT_ALIAS_PREFIX = "ks2_keychain_grant_id:";
    private static final String KEYCHAIN_PACKAGE = "com.android.keychain";
    public static final String KEY_ALIAS_SELECTION_DENIED = "android:alias-selection-denied";
    public static final int KEY_ATTESTATION_CANNOT_ATTEST_IDS = 3;
    public static final int KEY_ATTESTATION_CANNOT_COLLECT_DATA = 2;
    public static final int KEY_ATTESTATION_FAILURE = 4;
    public static final int KEY_ATTESTATION_MISSING_CHALLENGE = 1;
    public static final int KEY_ATTESTATION_SUCCESS = 0;
    public static final int KEY_GEN_FAILURE = 7;
    public static final int KEY_GEN_INVALID_ALGORITHM_PARAMETERS = 4;
    public static final int KEY_GEN_MISSING_ALIAS = 1;
    public static final int KEY_GEN_NO_KEYSTORE_PROVIDER = 5;
    public static final int KEY_GEN_NO_SUCH_ALGORITHM = 3;
    public static final int KEY_GEN_STRONGBOX_UNAVAILABLE = 6;
    public static final int KEY_GEN_SUCCESS = 0;
    public static final int KEY_GEN_SUPERFLUOUS_ATTESTATION_CHALLENGE = 2;
    public static final String LOG = "KeyChain";
    private static final String SETTINGS_PACKAGE = "com.android.settings";

    public static Intent createInstallIntent() {
        Intent intent = new Intent("android.credentials.INSTALL");
        intent.setClassName(CERT_INSTALLER_PACKAGE, "com.android.certinstaller.CertInstallerMain");
        return intent;
    }

    public static Intent createManageCredentialsIntent(AppUriAuthenticationPolicy policy) {
        Intent intent = new Intent(Credentials.ACTION_MANAGE_CREDENTIALS);
        intent.setComponent(ComponentName.createRelative(SETTINGS_PACKAGE, ".security.RequestManageCredentials"));
        intent.putExtra(EXTRA_AUTHENTICATION_POLICY, policy);
        return intent;
    }

    public static void choosePrivateKeyAlias(Activity activity, KeyChainAliasCallback response, String[] keyTypes, Principal[] issuers, String host, int port, String alias) {
        Uri uri = null;
        if (host != null) {
            uri = new Uri.Builder().authority(host + (port != -1 ? ":" + port : "")).build();
        }
        choosePrivateKeyAlias(activity, response, keyTypes, issuers, uri, alias);
    }

    public static void choosePrivateKeyAlias(Activity activity, KeyChainAliasCallback response, String[] keyTypes, Principal[] issuers, Uri uri, String alias) {
        if (activity == null) {
            throw new NullPointerException("activity == null");
        }
        if (response == null) {
            throw new NullPointerException("response == null");
        }
        Intent intent = new Intent(ACTION_CHOOSER);
        intent.setPackage("com.android.keychain");
        intent.putExtra("response", new AliasResponse(response));
        intent.putExtra("uri", uri);
        intent.putExtra("alias", alias);
        intent.putExtra(EXTRA_KEY_TYPES, keyTypes);
        ArrayList<byte[]> issuersList = new ArrayList<>();
        if (issuers != null) {
            for (Principal issuer : issuers) {
                if (!(issuer instanceof X500Principal)) {
                    throw new IllegalArgumentException(String.format("Issuer %s is of type %s, not X500Principal", issuer.toString(), issuer.getClass()));
                }
                issuersList.add(((X500Principal) issuer).getEncoded());
            }
        }
        intent.putExtra(EXTRA_ISSUERS, issuersList);
        intent.putExtra(EXTRA_SENDER, PendingIntent.getActivity(activity, 0, new Intent(), 67108864));
        activity.startActivity(intent);
    }

    public static boolean isCredentialManagementApp(Context context) {
        try {
            KeyChainConnection keyChainConnection = bind(context);
            try {
                boolean isCredentialManagementApp = keyChainConnection.getService().isCredentialManagementApp(context.getPackageName());
                if (keyChainConnection != null) {
                    keyChainConnection.close();
                    return isCredentialManagementApp;
                }
                return isCredentialManagementApp;
            } catch (Throwable th) {
                if (keyChainConnection != null) {
                    try {
                        keyChainConnection.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
            return false;
        } catch (InterruptedException e2) {
            throw new RuntimeException("Interrupted while checking whether the caller is the credential management app.", e2);
        } catch (SecurityException e3) {
            return false;
        }
    }

    public static AppUriAuthenticationPolicy getCredentialManagementAppPolicy(Context context) throws SecurityException {
        AppUriAuthenticationPolicy policy = null;
        try {
            KeyChainConnection keyChainConnection = bind(context);
            policy = keyChainConnection.getService().getCredentialManagementAppPolicy();
            if (keyChainConnection != null) {
                keyChainConnection.close();
            }
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        } catch (InterruptedException e2) {
            throw new RuntimeException("Interrupted while getting credential management app policy.", e2);
        }
        return policy;
    }

    public static boolean setCredentialManagementApp(Context context, String packageName, AppUriAuthenticationPolicy authenticationPolicy) {
        try {
            KeyChainConnection keyChainConnection = bind(context);
            keyChainConnection.getService().setCredentialManagementApp(packageName, authenticationPolicy);
            if (keyChainConnection != null) {
                keyChainConnection.close();
                return true;
            }
            return true;
        } catch (RemoteException | InterruptedException e) {
            Log.m103w(LOG, "Set credential management app failed", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public static boolean removeCredentialManagementApp(Context context) {
        try {
            KeyChainConnection keyChainConnection = bind(context);
            keyChainConnection.getService().removeCredentialManagementApp();
            if (keyChainConnection != null) {
                keyChainConnection.close();
                return true;
            }
            return true;
        } catch (RemoteException | InterruptedException e) {
            Log.m103w(LOG, "Remove credential management app failed", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class AliasResponse extends IKeyChainAliasCallback.Stub {
        private final KeyChainAliasCallback keyChainAliasResponse;

        private AliasResponse(KeyChainAliasCallback keyChainAliasResponse) {
            this.keyChainAliasResponse = keyChainAliasResponse;
        }

        @Override // android.security.IKeyChainAliasCallback
        public void alias(String alias) {
            this.keyChainAliasResponse.alias(alias);
        }
    }

    public static PrivateKey getPrivateKey(Context context, String alias) throws KeyChainException, InterruptedException {
        KeyPair keyPair = getKeyPair(context, alias);
        if (keyPair != null) {
            return keyPair.getPrivate();
        }
        return null;
    }

    private static KeyDescriptor getGrantDescriptor(String keyid) {
        KeyDescriptor result = new KeyDescriptor();
        result.domain = 1;
        result.blob = null;
        result.alias = null;
        try {
            result.nspace = Long.parseUnsignedLong(keyid.substring(GRANT_ALIAS_PREFIX.length()), 16);
            return result;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String getGrantString(KeyDescriptor key) {
        return String.format("ks2_keychain_grant_id:%016X", Long.valueOf(key.nspace));
    }

    public static KeyPair getKeyPair(Context context, String alias) throws KeyChainException, InterruptedException {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        try {
            KeyChainConnection keyChainConnection = bind(context.getApplicationContext());
            String keyId = keyChainConnection.getService().requestPrivateKey(alias);
            if (keyChainConnection != null) {
                keyChainConnection.close();
            }
            if (keyId == null) {
                return null;
            }
            try {
                return AndroidKeyStoreProvider.loadAndroidKeyStoreKeyPairFromKeystore(KeyStore2.getInstance(), getGrantDescriptor(keyId));
            } catch (KeyPermanentlyInvalidatedException | UnrecoverableKeyException e) {
                throw new KeyChainException(e);
            }
        } catch (RemoteException e2) {
            throw new KeyChainException(e2);
        } catch (RuntimeException e3) {
            throw new KeyChainException(e3);
        }
    }

    public static X509Certificate[] getCertificateChain(Context context, String alias) throws KeyChainException, InterruptedException {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        try {
            KeyChainConnection keyChainConnection = bind(context.getApplicationContext());
            try {
                IKeyChainService keyChainService = keyChainConnection.getService();
                byte[] certificateBytes = keyChainService.getCertificate(alias);
                if (certificateBytes != null) {
                    byte[] certChainBytes = keyChainService.getCaCertificates(alias);
                    if (keyChainConnection != null) {
                        keyChainConnection.close();
                    }
                    try {
                        X509Certificate leafCert = toCertificate(certificateBytes);
                        if (certChainBytes != null && certChainBytes.length != 0) {
                            Collection<? extends X509Certificate> chain = toCertificates(certChainBytes);
                            ArrayList<X509Certificate> fullChain = new ArrayList<>(chain.size() + 1);
                            fullChain.add(leafCert);
                            fullChain.addAll(chain);
                            return (X509Certificate[]) fullChain.toArray(new X509Certificate[fullChain.size()]);
                        }
                        TrustedCertificateStore store = new TrustedCertificateStore();
                        List<X509Certificate> chain2 = store.getCertificateChain(leafCert);
                        return (X509Certificate[]) chain2.toArray(new X509Certificate[chain2.size()]);
                    } catch (RuntimeException | CertificateException e) {
                        throw new KeyChainException(e);
                    }
                } else if (keyChainConnection != null) {
                    keyChainConnection.close();
                    return null;
                } else {
                    return null;
                }
            } catch (Throwable th) {
                if (keyChainConnection != null) {
                    try {
                        keyChainConnection.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (RemoteException e2) {
            throw new KeyChainException(e2);
        } catch (RuntimeException e3) {
            throw new KeyChainException(e3);
        }
    }

    public static boolean isKeyAlgorithmSupported(String algorithm) {
        String algUpper = algorithm.toUpperCase(Locale.US);
        return KeyProperties.KEY_ALGORITHM_EC.equals(algUpper) || KeyProperties.KEY_ALGORITHM_RSA.equals(algUpper);
    }

    @Deprecated
    public static boolean isBoundKeyAlgorithm(String algorithm) {
        return true;
    }

    public static X509Certificate toCertificate(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes == null");
        }
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(bytes));
            return (X509Certificate) cert;
        } catch (CertificateException e) {
            throw new AssertionError(e);
        }
    }

    public static Collection<X509Certificate> toCertificates(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes == null");
        }
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return certFactory.generateCertificates(new ByteArrayInputStream(bytes));
        } catch (CertificateException e) {
            throw new AssertionError(e);
        }
    }

    /* loaded from: classes3.dex */
    public static class KeyChainConnection implements Closeable {
        private final Context mContext;
        private final IKeyChainService mService;
        private final ServiceConnection mServiceConnection;

        protected KeyChainConnection(Context context, ServiceConnection serviceConnection, IKeyChainService service) {
            this.mContext = context;
            this.mServiceConnection = serviceConnection;
            this.mService = service;
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.mContext.unbindService(this.mServiceConnection);
        }

        public IKeyChainService getService() {
            return this.mService;
        }
    }

    public static KeyChainConnection bind(Context context) throws InterruptedException {
        return bindAsUser(context, Process.myUserHandle());
    }

    public static KeyChainConnection bindAsUser(Context context, UserHandle user) throws InterruptedException {
        return bindAsUser(context, null, user);
    }

    @SystemApi
    public static String getWifiKeyGrantAsUser(Context context, UserHandle user, String alias) {
        try {
            KeyChainConnection keyChainConnection = bindAsUser(context.getApplicationContext(), user);
            String wifiKeyGrantAsUser = keyChainConnection.getService().getWifiKeyGrantAsUser(alias);
            if (keyChainConnection != null) {
                keyChainConnection.close();
            }
            return wifiKeyGrantAsUser;
        } catch (RemoteException | RuntimeException e) {
            Log.m107i(LOG, "Couldn't get grant for wifi", e);
            return null;
        } catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            Log.m107i(LOG, "Interrupted while getting grant for wifi", e2);
            return null;
        }
    }

    @SystemApi
    public static boolean hasWifiKeyGrantAsUser(Context context, UserHandle user, String alias) {
        try {
            KeyChainConnection keyChainConnection = bindAsUser(context.getApplicationContext(), user);
            try {
                boolean hasGrant = keyChainConnection.getService().hasGrant(1010, alias);
                if (keyChainConnection != null) {
                    keyChainConnection.close();
                }
                return hasGrant;
            } catch (Throwable th) {
                if (keyChainConnection != null) {
                    try {
                        keyChainConnection.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (RemoteException | RuntimeException e) {
            Log.m107i(LOG, "Couldn't query grant for wifi", e);
            return false;
        } catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            Log.m107i(LOG, "Interrupted while querying grant for wifi", e2);
            return false;
        }
    }

    public static KeyChainConnection bindAsUser(Context context, Handler handler, UserHandle user) throws InterruptedException {
        boolean bindSucceed;
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        if (handler == null) {
            ensureNotOnMainThread(context);
        }
        if (!UserManager.get(context).isUserUnlocked(user)) {
            throw new IllegalStateException("User must be unlocked");
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference<IKeyChainService> keyChainService = new AtomicReference<>();
        ServiceConnection keyChainServiceConnection = new ServiceConnection() { // from class: android.security.KeyChain.1
            volatile boolean mConnectedAtLeastOnce = false;

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (!this.mConnectedAtLeastOnce) {
                    this.mConnectedAtLeastOnce = true;
                    keyChainService.set(IKeyChainService.Stub.asInterface(Binder.allowBlocking(service)));
                    countDownLatch.countDown();
                }
            }

            @Override // android.content.ServiceConnection
            public void onBindingDied(ComponentName name) {
                if (!this.mConnectedAtLeastOnce) {
                    this.mConnectedAtLeastOnce = true;
                    countDownLatch.countDown();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        Intent intent = new Intent(IKeyChainService.class.getName());
        ComponentName comp = intent.resolveSystemService(context.getPackageManager(), 0);
        if (comp == null) {
            throw new AssertionError("could not resolve KeyChainService");
        }
        intent.setComponent(comp);
        if (handler != null) {
            bindSucceed = context.bindServiceAsUser(intent, keyChainServiceConnection, 1, handler, user);
        } else {
            bindSucceed = context.bindServiceAsUser(intent, keyChainServiceConnection, 1, user);
        }
        if (!bindSucceed) {
            context.unbindService(keyChainServiceConnection);
            throw new AssertionError("could not bind to KeyChainService");
        }
        countDownLatch.await();
        IKeyChainService service = keyChainService.get();
        if (service != null) {
            return new KeyChainConnection(context, keyChainServiceConnection, service);
        }
        context.unbindService(keyChainServiceConnection);
        throw new AssertionError("KeyChainService died while binding");
    }

    private static void ensureNotOnMainThread(Context context) {
        Looper looper = Looper.myLooper();
        if (looper != null && looper == context.getMainLooper()) {
            throw new IllegalStateException("calling this from your main thread can lead to deadlock");
        }
    }
}
