package android.security.keystore.recovery;

import android.annotation.SystemApi;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.ServiceSpecificException;
import android.security.KeyStore;
import android.security.KeyStore2;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore2.AndroidKeyStoreProvider;
import android.system.keystore2.KeyDescriptor;
import com.android.internal.widget.ILockSettings;
import java.security.Key;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@SystemApi
/* loaded from: classes3.dex */
public class RecoveryController {
    private static final String APPLICATION_KEY_GRANT_PREFIX = "recoverable_key:";
    public static final int ERROR_BAD_CERTIFICATE_FORMAT = 25;
    public static final int ERROR_DECRYPTION_FAILED = 26;
    public static final int ERROR_DOWNGRADE_CERTIFICATE = 29;
    public static final int ERROR_INSECURE_USER = 23;
    public static final int ERROR_INVALID_CERTIFICATE = 28;
    public static final int ERROR_INVALID_KEY_FORMAT = 27;
    public static final int ERROR_KEY_NOT_FOUND = 30;
    public static final int ERROR_NO_SNAPSHOT_PENDING = 21;
    public static final int ERROR_SERVICE_INTERNAL_ERROR = 22;
    public static final int ERROR_SESSION_EXPIRED = 24;
    public static final int RECOVERY_STATUS_PERMANENT_FAILURE = 3;
    public static final int RECOVERY_STATUS_SYNCED = 0;
    public static final int RECOVERY_STATUS_SYNC_IN_PROGRESS = 1;
    private static final String TAG = "RecoveryController";
    private final ILockSettings mBinder;
    private final KeyStore mKeyStore;

    private RecoveryController(ILockSettings binder, KeyStore keystore) {
        this.mBinder = binder;
        this.mKeyStore = keystore;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ILockSettings getBinder() {
        return this.mBinder;
    }

    public static RecoveryController getInstance(Context context) {
        ILockSettings lockSettings = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
        return new RecoveryController(lockSettings, KeyStore.getInstance());
    }

    public static boolean isRecoverableKeyStoreEnabled(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        return keyguardManager != null && keyguardManager.isDeviceSecure();
    }

    public void initRecoveryService(String rootCertificateAlias, byte[] certificateFile, byte[] signatureFile) throws CertificateException, InternalRecoveryServiceException {
        try {
            this.mBinder.initRecoveryServiceWithSigFile(rootCertificateAlias, certificateFile, signatureFile);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 25 || e2.errorCode == 28) {
                throw new CertificateException("Invalid certificate for recovery service", e2);
            }
            if (e2.errorCode == 29) {
                throw new CertificateException("Downgrading certificate serial version isn't supported.", e2);
            }
            throw wrapUnexpectedServiceSpecificException(e2);
        }
    }

    public KeyChainSnapshot getKeyChainSnapshot() throws InternalRecoveryServiceException {
        try {
            return this.mBinder.getKeyChainSnapshot();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 21) {
                return null;
            }
            throw wrapUnexpectedServiceSpecificException(e2);
        }
    }

    public void setSnapshotCreatedPendingIntent(PendingIntent intent) throws InternalRecoveryServiceException {
        try {
            this.mBinder.setSnapshotCreatedPendingIntent(intent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            throw wrapUnexpectedServiceSpecificException(e2);
        }
    }

    public void setServerParams(byte[] serverParams) throws InternalRecoveryServiceException {
        try {
            this.mBinder.setServerParams(serverParams);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            throw wrapUnexpectedServiceSpecificException(e2);
        }
    }

    public List<String> getAliases() throws InternalRecoveryServiceException {
        try {
            Map<String, Integer> allStatuses = this.mBinder.getRecoveryStatus();
            return new ArrayList(allStatuses.keySet());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            throw wrapUnexpectedServiceSpecificException(e2);
        }
    }

    public void setRecoveryStatus(String alias, int status) throws InternalRecoveryServiceException {
        try {
            this.mBinder.setRecoveryStatus(alias, status);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            throw wrapUnexpectedServiceSpecificException(e2);
        }
    }

    public int getRecoveryStatus(String alias) throws InternalRecoveryServiceException {
        try {
            Map<String, Integer> allStatuses = this.mBinder.getRecoveryStatus();
            Integer status = allStatuses.get(alias);
            if (status == null) {
                return 3;
            }
            return status.intValue();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            throw wrapUnexpectedServiceSpecificException(e2);
        }
    }

    public void setRecoverySecretTypes(int[] secretTypes) throws InternalRecoveryServiceException {
        try {
            this.mBinder.setRecoverySecretTypes(secretTypes);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            throw wrapUnexpectedServiceSpecificException(e2);
        }
    }

    public int[] getRecoverySecretTypes() throws InternalRecoveryServiceException {
        try {
            return this.mBinder.getRecoverySecretTypes();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            throw wrapUnexpectedServiceSpecificException(e2);
        }
    }

    @Deprecated
    public Key generateKey(String alias) throws InternalRecoveryServiceException, LockScreenRequiredException {
        try {
            String grantAlias = this.mBinder.generateKey(alias);
            if (grantAlias == null) {
                throw new InternalRecoveryServiceException("null grant alias");
            }
            return getKeyFromGrant(grantAlias);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 23) {
                throw new LockScreenRequiredException(e2.getMessage());
            }
            throw wrapUnexpectedServiceSpecificException(e2);
        } catch (KeyPermanentlyInvalidatedException | UnrecoverableKeyException e3) {
            throw new InternalRecoveryServiceException("Failed to get key from keystore", e3);
        }
    }

    public Key generateKey(String alias, byte[] metadata) throws InternalRecoveryServiceException, LockScreenRequiredException {
        try {
            String grantAlias = this.mBinder.generateKeyWithMetadata(alias, metadata);
            if (grantAlias == null) {
                throw new InternalRecoveryServiceException("null grant alias");
            }
            return getKeyFromGrant(grantAlias);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 23) {
                throw new LockScreenRequiredException(e2.getMessage());
            }
            throw wrapUnexpectedServiceSpecificException(e2);
        } catch (KeyPermanentlyInvalidatedException | UnrecoverableKeyException e3) {
            throw new InternalRecoveryServiceException("Failed to get key from keystore", e3);
        }
    }

    @Deprecated
    public Key importKey(String alias, byte[] keyBytes) throws InternalRecoveryServiceException, LockScreenRequiredException {
        try {
            String grantAlias = this.mBinder.importKey(alias, keyBytes);
            if (grantAlias == null) {
                throw new InternalRecoveryServiceException("Null grant alias");
            }
            return getKeyFromGrant(grantAlias);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 23) {
                throw new LockScreenRequiredException(e2.getMessage());
            }
            throw wrapUnexpectedServiceSpecificException(e2);
        } catch (KeyPermanentlyInvalidatedException | UnrecoverableKeyException e3) {
            throw new InternalRecoveryServiceException("Failed to get key from keystore", e3);
        }
    }

    public Key importKey(String alias, byte[] keyBytes, byte[] metadata) throws InternalRecoveryServiceException, LockScreenRequiredException {
        try {
            String grantAlias = this.mBinder.importKeyWithMetadata(alias, keyBytes, metadata);
            if (grantAlias == null) {
                throw new InternalRecoveryServiceException("Null grant alias");
            }
            return getKeyFromGrant(grantAlias);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 23) {
                throw new LockScreenRequiredException(e2.getMessage());
            }
            throw wrapUnexpectedServiceSpecificException(e2);
        } catch (KeyPermanentlyInvalidatedException | UnrecoverableKeyException e3) {
            throw new InternalRecoveryServiceException("Failed to get key from keystore", e3);
        }
    }

    public Key getKey(String alias) throws InternalRecoveryServiceException, UnrecoverableKeyException {
        try {
            String grantAlias = this.mBinder.getKey(alias);
            if (grantAlias != null && !"".equals(grantAlias)) {
                return getKeyFromGrant(grantAlias);
            }
            return null;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 30) {
                throw new UnrecoverableKeyException(e2.getMessage());
            }
            throw wrapUnexpectedServiceSpecificException(e2);
        } catch (KeyPermanentlyInvalidatedException | UnrecoverableKeyException e3) {
            throw new UnrecoverableKeyException(e3.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Key getKeyFromGrant(String grantAlias) throws UnrecoverableKeyException, KeyPermanentlyInvalidatedException {
        return AndroidKeyStoreProvider.loadAndroidKeyStoreSecretKeyFromKeystore(KeyStore2.getInstance(), getGrantDescriptor(grantAlias));
    }

    private static KeyDescriptor getGrantDescriptor(String grantAlias) {
        KeyDescriptor result = new KeyDescriptor();
        result.domain = 1;
        result.blob = null;
        result.alias = null;
        try {
            result.nspace = Long.parseUnsignedLong(grantAlias.substring(APPLICATION_KEY_GRANT_PREFIX.length()), 16);
            return result;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void removeKey(String alias) throws InternalRecoveryServiceException {
        try {
            this.mBinder.removeKey(alias);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            throw wrapUnexpectedServiceSpecificException(e2);
        }
    }

    public RecoverySession createRecoverySession() {
        return RecoverySession.newInstance(this);
    }

    public Map<String, X509Certificate> getRootCertificates() {
        return TrustedRootCertificates.getRootCertificates();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InternalRecoveryServiceException wrapUnexpectedServiceSpecificException(ServiceSpecificException e) {
        if (e.errorCode == 22) {
            return new InternalRecoveryServiceException(e.getMessage(), e);
        }
        return new InternalRecoveryServiceException("Unexpected error code for method: " + e.errorCode, e);
    }
}
