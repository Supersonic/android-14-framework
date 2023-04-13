package com.android.server.p011pm;

import android.util.SparseBooleanArray;
/* renamed from: com.android.server.pm.PackageVerificationState */
/* loaded from: classes2.dex */
public class PackageVerificationState {
    public boolean mIntegrityVerificationComplete;
    public boolean mSufficientVerificationComplete;
    public boolean mSufficientVerificationPassed;
    public final VerifyingSession mVerifyingSession;
    public final SparseBooleanArray mSufficientVerifierUids = new SparseBooleanArray();
    public final SparseBooleanArray mRequiredVerifierUids = new SparseBooleanArray();
    public final SparseBooleanArray mUnrespondedRequiredVerifierUids = new SparseBooleanArray();
    public boolean mRequiredVerificationComplete = false;
    public boolean mRequiredVerificationPassed = true;
    public boolean mExtendedTimeout = false;

    public PackageVerificationState(VerifyingSession verifyingSession) {
        this.mVerifyingSession = verifyingSession;
    }

    public VerifyingSession getVerifyingSession() {
        return this.mVerifyingSession;
    }

    public void addRequiredVerifierUid(int i) {
        this.mRequiredVerifierUids.put(i, true);
        this.mUnrespondedRequiredVerifierUids.put(i, true);
    }

    public boolean checkRequiredVerifierUid(int i) {
        return this.mRequiredVerifierUids.get(i, false);
    }

    public void addSufficientVerifier(int i) {
        this.mSufficientVerifierUids.put(i, true);
    }

    public boolean checkSufficientVerifierUid(int i) {
        return this.mSufficientVerifierUids.get(i, false);
    }

    public boolean setVerifierResponse(int i, int i2) {
        if (this.mRequiredVerifierUids.get(i)) {
            if (i2 != 1) {
                if (i2 == 2) {
                    this.mSufficientVerifierUids.clear();
                } else {
                    this.mRequiredVerificationPassed = false;
                }
            }
            this.mUnrespondedRequiredVerifierUids.delete(i);
            if (this.mUnrespondedRequiredVerifierUids.size() == 0) {
                this.mRequiredVerificationComplete = true;
            }
            return true;
        } else if (this.mSufficientVerifierUids.get(i)) {
            if (i2 == 1) {
                this.mSufficientVerificationPassed = true;
                this.mSufficientVerificationComplete = true;
            }
            this.mSufficientVerifierUids.delete(i);
            if (this.mSufficientVerifierUids.size() == 0) {
                this.mSufficientVerificationComplete = true;
            }
            return true;
        } else {
            return false;
        }
    }

    public void passRequiredVerification() {
        if (this.mUnrespondedRequiredVerifierUids.size() > 0) {
            throw new RuntimeException("Required verifiers still present.");
        }
        this.mRequiredVerificationPassed = true;
        this.mRequiredVerificationComplete = true;
    }

    public boolean isVerificationComplete() {
        if (this.mRequiredVerificationComplete) {
            if (this.mSufficientVerifierUids.size() == 0) {
                return true;
            }
            return this.mSufficientVerificationComplete;
        }
        return false;
    }

    public boolean isInstallAllowed() {
        if (this.mRequiredVerificationComplete && this.mRequiredVerificationPassed) {
            if (this.mSufficientVerificationComplete) {
                return this.mSufficientVerificationPassed;
            }
            return true;
        }
        return false;
    }

    public void extendTimeout() {
        if (this.mExtendedTimeout) {
            return;
        }
        this.mExtendedTimeout = true;
    }

    public boolean timeoutExtended() {
        return this.mExtendedTimeout;
    }

    public void setIntegrityVerificationResult(int i) {
        this.mIntegrityVerificationComplete = true;
    }

    public boolean isIntegrityVerificationComplete() {
        return this.mIntegrityVerificationComplete;
    }

    public boolean areAllVerificationsComplete() {
        return this.mIntegrityVerificationComplete && isVerificationComplete();
    }
}
