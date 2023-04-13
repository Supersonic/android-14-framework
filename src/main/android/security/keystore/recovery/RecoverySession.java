package android.security.keystore.recovery;

import android.annotation.SystemApi;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.util.ArrayMap;
import android.util.Log;
import java.security.Key;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import libcore.util.HexEncoding;
@SystemApi
/* loaded from: classes3.dex */
public class RecoverySession implements AutoCloseable {
    private static final int SESSION_ID_LENGTH_BYTES = 16;
    private static final String TAG = "RecoverySession";
    private final RecoveryController mRecoveryController;
    private final String mSessionId;

    private RecoverySession(RecoveryController recoveryController, String sessionId) {
        this.mRecoveryController = recoveryController;
        this.mSessionId = sessionId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RecoverySession newInstance(RecoveryController recoveryController) {
        return new RecoverySession(recoveryController, newSessionId());
    }

    private static String newSessionId() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] sessionId = new byte[16];
        secureRandom.nextBytes(sessionId);
        return HexEncoding.encodeToString(sessionId, false);
    }

    public byte[] start(String rootCertificateAlias, CertPath verifierCertPath, byte[] vaultParams, byte[] vaultChallenge, List<KeyChainProtectionParams> secrets) throws CertificateException, InternalRecoveryServiceException {
        RecoveryCertPath recoveryCertPath = RecoveryCertPath.createRecoveryCertPath(verifierCertPath);
        try {
            byte[] recoveryClaim = this.mRecoveryController.getBinder().startRecoverySessionWithCertPath(this.mSessionId, rootCertificateAlias, recoveryCertPath, vaultParams, vaultChallenge, secrets);
            return recoveryClaim;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 25 || e2.errorCode == 28) {
                throw new CertificateException("Invalid certificate for recovery session", e2);
            }
            throw this.mRecoveryController.wrapUnexpectedServiceSpecificException(e2);
        }
    }

    public Map<String, Key> recoverKeyChainSnapshot(byte[] recoveryKeyBlob, List<WrappedApplicationKey> applicationKeys) throws SessionExpiredException, DecryptionFailedException, InternalRecoveryServiceException {
        try {
            Map<String, String> grantAliases = this.mRecoveryController.getBinder().recoverKeyChainSnapshot(this.mSessionId, recoveryKeyBlob, applicationKeys);
            return getKeysFromGrants(grantAliases);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 26) {
                throw new DecryptionFailedException(e2.getMessage());
            }
            if (e2.errorCode == 24) {
                throw new SessionExpiredException(e2.getMessage());
            }
            throw this.mRecoveryController.wrapUnexpectedServiceSpecificException(e2);
        }
    }

    private Map<String, Key> getKeysFromGrants(Map<String, String> grantAliases) throws InternalRecoveryServiceException {
        ArrayMap<String, Key> keysByAlias = new ArrayMap<>(grantAliases.size());
        for (String alias : grantAliases.keySet()) {
            String grantAlias = grantAliases.get(alias);
            try {
                Key key = this.mRecoveryController.getKeyFromGrant(grantAlias);
                keysByAlias.put(alias, key);
            } catch (KeyPermanentlyInvalidatedException | UnrecoverableKeyException e) {
                throw new InternalRecoveryServiceException(String.format(Locale.US, "Failed to get key '%s' from grant '%s'", alias, grantAlias), e);
            }
        }
        return keysByAlias;
    }

    String getSessionId() {
        return this.mSessionId;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        try {
            this.mRecoveryController.getBinder().closeSession(this.mSessionId);
        } catch (RemoteException | ServiceSpecificException e) {
            Log.m109e(TAG, "Unexpected error trying to close session", e);
        }
    }
}
