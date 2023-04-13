package com.android.server.locksettings.recoverablekeystore.storage;

import android.os.SystemClock;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.security.SecureBox;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
/* loaded from: classes2.dex */
public class RemoteLockscreenValidationSessionStorage {
    @VisibleForTesting
    final SparseArray<LockscreenVerificationSession> mSessionsByUserId = new SparseArray<>(0);

    public LockscreenVerificationSession get(int i) {
        LockscreenVerificationSession lockscreenVerificationSession;
        synchronized (this.mSessionsByUserId) {
            lockscreenVerificationSession = this.mSessionsByUserId.get(i);
        }
        return lockscreenVerificationSession;
    }

    public LockscreenVerificationSession startSession(int i) {
        LockscreenVerificationSession lockscreenVerificationSession;
        synchronized (this.mSessionsByUserId) {
            if (this.mSessionsByUserId.get(i) != null) {
                this.mSessionsByUserId.delete(i);
            }
            try {
                lockscreenVerificationSession = new LockscreenVerificationSession(SecureBox.genKeyPair(), SystemClock.elapsedRealtime());
                this.mSessionsByUserId.put(i, lockscreenVerificationSession);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return lockscreenVerificationSession;
    }

    public void finishSession(int i) {
        synchronized (this.mSessionsByUserId) {
            this.mSessionsByUserId.delete(i);
        }
    }

    /* loaded from: classes2.dex */
    public class LockscreenVerificationSession {
        public final long mElapsedStartTime;
        public final KeyPair mKeyPair;

        public LockscreenVerificationSession(KeyPair keyPair, long j) {
            this.mKeyPair = keyPair;
            this.mElapsedStartTime = j;
        }

        public KeyPair getKeyPair() {
            return this.mKeyPair;
        }
    }
}
