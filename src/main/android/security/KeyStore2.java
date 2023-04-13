package android.security;

import android.p008os.Binder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.ServiceSpecificException;
import android.security.keymaster.KeymasterDefs;
import android.system.keystore2.IKeystoreService;
import android.system.keystore2.KeyDescriptor;
import android.system.keystore2.KeyEntryResponse;
import android.util.Log;
import java.util.Calendar;
import java.util.Objects;
/* loaded from: classes3.dex */
public class KeyStore2 {
    private static final String KEYSTORE2_SERVICE_NAME = "android.system.keystore2.IKeystoreService/default";
    private static final String KEYSTORE_ENGINE_GRANT_ALIAS_PREFIX = "ks2_keystore-engine_grant_id:0x";
    static final long KEYSTORE_OPERATION_CREATION_MAY_FAIL = 169897160;
    private static final int RECOVERY_GRACE_PERIOD_MS = 50;
    private static final String TAG = "KeyStore";
    private IKeystoreService mBinder = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface CheckedRemoteRequest<R> {
        R execute(IKeystoreService iKeystoreService) throws RemoteException;
    }

    private <R> R handleRemoteExceptionWithRetry(CheckedRemoteRequest<R> request) throws KeyStoreException {
        IKeystoreService service = getService(false);
        boolean firstTry = true;
        while (true) {
            try {
                return request.execute(service);
            } catch (RemoteException e) {
                if (firstTry) {
                    Log.m104w(TAG, "Looks like we may have lost connection to the Keystore daemon.");
                    Log.m104w(TAG, "Retrying after giving Keystore 50ms to recover.");
                    interruptedPreservingSleep(50L);
                    service = getService(true);
                    firstTry = false;
                } else {
                    Log.m109e(TAG, "Cannot connect to Keystore daemon.", e);
                    throw new KeyStoreException(4, "", e.getMessage());
                }
            } catch (ServiceSpecificException e2) {
                throw getKeyStoreException(e2.errorCode, e2.getMessage());
            }
        }
    }

    private KeyStore2() {
    }

    public static KeyStore2 getInstance() {
        return new KeyStore2();
    }

    private synchronized IKeystoreService getService(boolean retryLookup) {
        if (this.mBinder == null || retryLookup) {
            IKeystoreService asInterface = IKeystoreService.Stub.asInterface(ServiceManager.getService(KEYSTORE2_SERVICE_NAME));
            this.mBinder = asInterface;
            Binder.allowBlocking(asInterface.asBinder());
        }
        return (IKeystoreService) Objects.requireNonNull(this.mBinder);
    }

    void delete(final KeyDescriptor descriptor) throws KeyStoreException {
        handleRemoteExceptionWithRetry(new CheckedRemoteRequest() { // from class: android.security.KeyStore2$$ExternalSyntheticLambda6
            @Override // android.security.KeyStore2.CheckedRemoteRequest
            public final Object execute(IKeystoreService iKeystoreService) {
                return iKeystoreService.deleteKey(KeyDescriptor.this);
            }
        });
    }

    public KeyDescriptor[] list(final int domain, final long namespace) throws KeyStoreException {
        return (KeyDescriptor[]) handleRemoteExceptionWithRetry(new CheckedRemoteRequest() { // from class: android.security.KeyStore2$$ExternalSyntheticLambda2
            @Override // android.security.KeyStore2.CheckedRemoteRequest
            public final Object execute(IKeystoreService iKeystoreService) {
                KeyDescriptor[] listEntries;
                listEntries = iKeystoreService.listEntries(domain, namespace);
                return listEntries;
            }
        });
    }

    public static String makeKeystoreEngineGrantString(long grantId) {
        return String.format("%s%016X", KEYSTORE_ENGINE_GRANT_ALIAS_PREFIX, Long.valueOf(grantId));
    }

    public static KeyDescriptor keystoreEngineGrantString2KeyDescriptor(String grantString) {
        KeyDescriptor key = new KeyDescriptor();
        key.domain = 1;
        key.nspace = Long.parseUnsignedLong(grantString.substring(KEYSTORE_ENGINE_GRANT_ALIAS_PREFIX.length()), 16);
        key.alias = null;
        key.blob = null;
        return key;
    }

    public KeyDescriptor grant(final KeyDescriptor descriptor, final int granteeUid, final int accessVector) throws KeyStoreException {
        return (KeyDescriptor) handleRemoteExceptionWithRetry(new CheckedRemoteRequest() { // from class: android.security.KeyStore2$$ExternalSyntheticLambda5
            @Override // android.security.KeyStore2.CheckedRemoteRequest
            public final Object execute(IKeystoreService iKeystoreService) {
                KeyDescriptor grant;
                grant = iKeystoreService.grant(KeyDescriptor.this, granteeUid, accessVector);
                return grant;
            }
        });
    }

    public void ungrant(final KeyDescriptor descriptor, final int granteeUid) throws KeyStoreException {
        handleRemoteExceptionWithRetry(new CheckedRemoteRequest() { // from class: android.security.KeyStore2$$ExternalSyntheticLambda4
            @Override // android.security.KeyStore2.CheckedRemoteRequest
            public final Object execute(IKeystoreService iKeystoreService) {
                return iKeystoreService.ungrant(KeyDescriptor.this, granteeUid);
            }
        });
    }

    public KeyEntryResponse getKeyEntry(final KeyDescriptor descriptor) throws KeyStoreException {
        return (KeyEntryResponse) handleRemoteExceptionWithRetry(new CheckedRemoteRequest() { // from class: android.security.KeyStore2$$ExternalSyntheticLambda3
            @Override // android.security.KeyStore2.CheckedRemoteRequest
            public final Object execute(IKeystoreService iKeystoreService) {
                KeyEntryResponse keyEntry;
                keyEntry = iKeystoreService.getKeyEntry(KeyDescriptor.this);
                return keyEntry;
            }
        });
    }

    public KeyStoreSecurityLevel getSecurityLevel(final int securityLevel) throws KeyStoreException {
        return (KeyStoreSecurityLevel) handleRemoteExceptionWithRetry(new CheckedRemoteRequest() { // from class: android.security.KeyStore2$$ExternalSyntheticLambda1
            @Override // android.security.KeyStore2.CheckedRemoteRequest
            public final Object execute(IKeystoreService iKeystoreService) {
                return KeyStore2.lambda$getSecurityLevel$5(securityLevel, iKeystoreService);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ KeyStoreSecurityLevel lambda$getSecurityLevel$5(int securityLevel, IKeystoreService service) throws RemoteException {
        return new KeyStoreSecurityLevel(service.getSecurityLevel(securityLevel));
    }

    public void updateSubcomponents(final KeyDescriptor key, final byte[] publicCert, final byte[] publicCertChain) throws KeyStoreException {
        handleRemoteExceptionWithRetry(new CheckedRemoteRequest() { // from class: android.security.KeyStore2$$ExternalSyntheticLambda7
            @Override // android.security.KeyStore2.CheckedRemoteRequest
            public final Object execute(IKeystoreService iKeystoreService) {
                return iKeystoreService.updateSubcomponent(KeyDescriptor.this, publicCert, publicCertChain);
            }
        });
    }

    public void deleteKey(final KeyDescriptor descriptor) throws KeyStoreException {
        handleRemoteExceptionWithRetry(new CheckedRemoteRequest() { // from class: android.security.KeyStore2$$ExternalSyntheticLambda0
            @Override // android.security.KeyStore2.CheckedRemoteRequest
            public final Object execute(IKeystoreService iKeystoreService) {
                return iKeystoreService.deleteKey(KeyDescriptor.this);
            }
        });
    }

    protected static void interruptedPreservingSleep(long millis) {
        boolean wasInterrupted = false;
        Calendar calendar = Calendar.getInstance();
        long target = calendar.getTimeInMillis() + millis;
        while (true) {
            try {
                Thread.sleep(target - calendar.getTimeInMillis());
                break;
            } catch (IllegalArgumentException e) {
            } catch (InterruptedException e2) {
                wasInterrupted = true;
            }
        }
        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KeyStoreException getKeyStoreException(int errorCode, String serviceErrorMessage) {
        if (errorCode > 0) {
            switch (errorCode) {
                case 2:
                    return new KeyStoreException(errorCode, "User authentication required", serviceErrorMessage);
                case 3:
                    return new KeyStoreException(errorCode, "Keystore not initialized", serviceErrorMessage);
                case 4:
                    return new KeyStoreException(errorCode, "System error", serviceErrorMessage);
                case 6:
                    return new KeyStoreException(errorCode, "Permission denied", serviceErrorMessage);
                case 7:
                    return new KeyStoreException(errorCode, "Key not found", serviceErrorMessage);
                case 8:
                    return new KeyStoreException(errorCode, "Key blob corrupted", serviceErrorMessage);
                case 17:
                    return new KeyStoreException(errorCode, "Key permanently invalidated", serviceErrorMessage);
                case 22:
                    return new KeyStoreException(errorCode, serviceErrorMessage, 1);
                default:
                    return new KeyStoreException(errorCode, String.valueOf(errorCode), serviceErrorMessage);
            }
        }
        switch (errorCode) {
            case -16:
                return new KeyStoreException(errorCode, "Invalid user authentication validity duration", serviceErrorMessage);
            default:
                return new KeyStoreException(errorCode, KeymasterDefs.getErrorMessage(errorCode), serviceErrorMessage);
        }
    }
}
