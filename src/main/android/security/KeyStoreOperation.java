package android.security;

import android.hardware.security.keymint.KeyParameter;
import android.p008os.Binder;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.system.keystore2.IKeystoreOperation;
import android.util.Log;
/* loaded from: classes3.dex */
public class KeyStoreOperation {
    static final String TAG = "KeyStoreOperation";
    private final Long mChallenge;
    private final IKeystoreOperation mOperation;
    private final KeyParameter[] mParameters;

    public KeyStoreOperation(IKeystoreOperation operation, Long challenge, KeyParameter[] parameters) {
        Binder.allowBlocking(operation.asBinder());
        this.mOperation = operation;
        this.mChallenge = challenge;
        this.mParameters = parameters;
    }

    public Long getChallenge() {
        return this.mChallenge;
    }

    public KeyParameter[] getParameters() {
        return this.mParameters;
    }

    private <R> R handleExceptions(CheckedRemoteRequest<R> request) throws KeyStoreException {
        try {
            return request.execute();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Remote exception while advancing a KeyStoreOperation.", e);
            throw new KeyStoreException(-28, "", e.getMessage());
        } catch (ServiceSpecificException e2) {
            switch (e2.errorCode) {
                case 19:
                    throw new IllegalThreadStateException("Cannot update the same operation concurrently.");
                default:
                    throw KeyStore2.getKeyStoreException(e2.errorCode, e2.getMessage());
            }
        }
    }

    public void updateAad(final byte[] input) throws KeyStoreException {
        handleExceptions(new CheckedRemoteRequest() { // from class: android.security.KeyStoreOperation$$ExternalSyntheticLambda3
            @Override // android.security.CheckedRemoteRequest
            public final Object execute() {
                Integer lambda$updateAad$0;
                lambda$updateAad$0 = KeyStoreOperation.this.lambda$updateAad$0(input);
                return lambda$updateAad$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$updateAad$0(byte[] input) throws RemoteException {
        this.mOperation.updateAad(input);
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ byte[] lambda$update$1(byte[] input) throws RemoteException {
        return this.mOperation.update(input);
    }

    public byte[] update(final byte[] input) throws KeyStoreException {
        return (byte[]) handleExceptions(new CheckedRemoteRequest() { // from class: android.security.KeyStoreOperation$$ExternalSyntheticLambda1
            @Override // android.security.CheckedRemoteRequest
            public final Object execute() {
                byte[] lambda$update$1;
                lambda$update$1 = KeyStoreOperation.this.lambda$update$1(input);
                return lambda$update$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ byte[] lambda$finish$2(byte[] input, byte[] signature) throws RemoteException {
        return this.mOperation.finish(input, signature);
    }

    public byte[] finish(final byte[] input, final byte[] signature) throws KeyStoreException {
        return (byte[]) handleExceptions(new CheckedRemoteRequest() { // from class: android.security.KeyStoreOperation$$ExternalSyntheticLambda2
            @Override // android.security.CheckedRemoteRequest
            public final Object execute() {
                byte[] lambda$finish$2;
                lambda$finish$2 = KeyStoreOperation.this.lambda$finish$2(input, signature);
                return lambda$finish$2;
            }
        });
    }

    public void abort() throws KeyStoreException {
        handleExceptions(new CheckedRemoteRequest() { // from class: android.security.KeyStoreOperation$$ExternalSyntheticLambda0
            @Override // android.security.CheckedRemoteRequest
            public final Object execute() {
                Integer lambda$abort$3;
                lambda$abort$3 = KeyStoreOperation.this.lambda$abort$3();
                return lambda$abort$3;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$abort$3() throws RemoteException {
        this.mOperation.abort();
        return 0;
    }
}
