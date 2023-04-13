package com.android.internal.widget;

import android.p008os.AsyncTask;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import java.util.Objects;
/* loaded from: classes5.dex */
public final class LockPatternChecker {

    /* loaded from: classes5.dex */
    public interface OnVerifyCallback {
        void onVerified(VerifyCredentialResponse verifyCredentialResponse, int i);
    }

    /* loaded from: classes5.dex */
    public interface OnCheckCallback {
        void onChecked(boolean z, int i);

        default void onEarlyMatched() {
        }

        default void onCancelled() {
        }
    }

    public static AsyncTask<?, ?, ?> verifyCredential(final LockPatternUtils utils, LockscreenCredential credential, final int userId, final int flags, final OnVerifyCallback callback) {
        final LockscreenCredential credentialCopy = credential.duplicate();
        AsyncTask<Void, Void, VerifyCredentialResponse> task = new AsyncTask<Void, Void, VerifyCredentialResponse>() { // from class: com.android.internal.widget.LockPatternChecker.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.AsyncTask
            public VerifyCredentialResponse doInBackground(Void... args) {
                return LockPatternUtils.this.verifyCredential(credentialCopy, userId, flags);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.AsyncTask
            public void onPostExecute(VerifyCredentialResponse result) {
                callback.onVerified(result, result.getTimeout());
                credentialCopy.zeroize();
            }

            @Override // android.p008os.AsyncTask
            protected void onCancelled() {
                credentialCopy.zeroize();
            }
        };
        task.execute(new Void[0]);
        return task;
    }

    public static AsyncTask<?, ?, ?> checkCredential(final LockPatternUtils utils, LockscreenCredential credential, final int userId, final OnCheckCallback callback) {
        final LockscreenCredential credentialCopy = credential.duplicate();
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() { // from class: com.android.internal.widget.LockPatternChecker.2
            private int mThrottleTimeout;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.AsyncTask
            public Boolean doInBackground(Void... args) {
                try {
                    LockPatternUtils lockPatternUtils = LockPatternUtils.this;
                    LockscreenCredential lockscreenCredential = credentialCopy;
                    int i = userId;
                    final OnCheckCallback onCheckCallback = callback;
                    Objects.requireNonNull(onCheckCallback);
                    return Boolean.valueOf(lockPatternUtils.checkCredential(lockscreenCredential, i, new LockPatternUtils.CheckCredentialProgressCallback() { // from class: com.android.internal.widget.LockPatternChecker$2$$ExternalSyntheticLambda0
                        @Override // com.android.internal.widget.LockPatternUtils.CheckCredentialProgressCallback
                        public final void onEarlyMatched() {
                            LockPatternChecker.OnCheckCallback.this.onEarlyMatched();
                        }
                    }));
                } catch (LockPatternUtils.RequestThrottledException ex) {
                    this.mThrottleTimeout = ex.getTimeoutMs();
                    return false;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.AsyncTask
            public void onPostExecute(Boolean result) {
                callback.onChecked(result.booleanValue(), this.mThrottleTimeout);
                if (LockPatternUtils.isAutoPinConfirmFeatureAvailable()) {
                    LockPatternUtils.this.setPinLength(userId, credentialCopy.size());
                }
                credentialCopy.zeroize();
            }

            @Override // android.p008os.AsyncTask
            protected void onCancelled() {
                callback.onCancelled();
                credentialCopy.zeroize();
            }
        };
        task.execute(new Void[0]);
        return task;
    }

    public static AsyncTask<?, ?, ?> verifyTiedProfileChallenge(final LockPatternUtils utils, LockscreenCredential credential, final int userId, final int flags, final OnVerifyCallback callback) {
        final LockscreenCredential credentialCopy = credential.duplicate();
        AsyncTask<Void, Void, VerifyCredentialResponse> task = new AsyncTask<Void, Void, VerifyCredentialResponse>() { // from class: com.android.internal.widget.LockPatternChecker.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.AsyncTask
            public VerifyCredentialResponse doInBackground(Void... args) {
                return LockPatternUtils.this.verifyTiedProfileChallenge(credentialCopy, userId, flags);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.AsyncTask
            public void onPostExecute(VerifyCredentialResponse response) {
                callback.onVerified(response, response.getTimeout());
                credentialCopy.zeroize();
            }

            @Override // android.p008os.AsyncTask
            protected void onCancelled() {
                credentialCopy.zeroize();
            }
        };
        task.execute(new Void[0]);
        return task;
    }
}
