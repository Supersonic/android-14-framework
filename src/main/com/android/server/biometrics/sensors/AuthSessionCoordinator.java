package com.android.server.biometrics.sensors;

import android.hardware.biometrics.BiometricManager;
import android.os.SystemClock;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class AuthSessionCoordinator {
    public final Set<Integer> mAuthOperations;
    public AuthResultCoordinator mAuthResultCoordinator;
    public final Clock mClock;
    public boolean mIsAuthenticating;
    public final MultiBiometricLockoutState mMultiBiometricLockoutState;
    public final RingBuffer mRingBuffer;
    public final List<Pair<Integer, Long>> mTimedLockouts;
    public int mUserId;

    public AuthSessionCoordinator() {
        this(SystemClock.elapsedRealtimeClock());
    }

    @VisibleForTesting
    public AuthSessionCoordinator(Clock clock) {
        this.mAuthOperations = new HashSet();
        this.mAuthResultCoordinator = new AuthResultCoordinator();
        this.mMultiBiometricLockoutState = new MultiBiometricLockoutState(clock);
        this.mRingBuffer = new RingBuffer(100);
        this.mTimedLockouts = new ArrayList();
        this.mClock = clock;
    }

    public void onAuthSessionStarted(int i) {
        this.mAuthOperations.clear();
        this.mUserId = i;
        this.mIsAuthenticating = true;
        this.mAuthOperations.clear();
        this.mTimedLockouts.clear();
        this.mAuthResultCoordinator = new AuthResultCoordinator();
        RingBuffer ringBuffer = this.mRingBuffer;
        ringBuffer.addApiCall("internal : onAuthSessionStarted(" + i + ")");
    }

    public void endAuthSession() {
        if (this.mIsAuthenticating) {
            long millis = this.mClock.millis();
            for (Pair<Integer, Long> pair : this.mTimedLockouts) {
                this.mMultiBiometricLockoutState.increaseLockoutTime(this.mUserId, ((Integer) pair.first).intValue(), ((Long) pair.second).longValue() + millis);
            }
            Map<Integer, Integer> result = this.mAuthResultCoordinator.getResult();
            for (Integer num : Arrays.asList(4095, 255, 15)) {
                int intValue = num.intValue();
                Integer num2 = result.get(Integer.valueOf(intValue));
                if ((num2.intValue() & 2) == 2) {
                    this.mMultiBiometricLockoutState.setAuthenticatorTo(this.mUserId, intValue, true);
                    this.mMultiBiometricLockoutState.clearLockoutTime(this.mUserId, intValue);
                } else if ((num2.intValue() & 1) == 1) {
                    this.mMultiBiometricLockoutState.setAuthenticatorTo(this.mUserId, intValue, false);
                }
            }
            RingBuffer ringBuffer = this.mRingBuffer;
            ringBuffer.addApiCall("internal : onAuthSessionEnded(" + this.mUserId + ")");
            clearSession();
        }
    }

    public final void clearSession() {
        this.mIsAuthenticating = false;
        this.mTimedLockouts.clear();
        this.mAuthOperations.clear();
    }

    public int getLockoutStateFor(int i, @BiometricManager.Authenticators.Types int i2) {
        return this.mMultiBiometricLockoutState.getLockoutState(i, i2);
    }

    public void authStartedFor(int i, int i2, long j) {
        RingBuffer ringBuffer = this.mRingBuffer;
        ringBuffer.addApiCall("authStartedFor(userId=" + i + ", sensorId=" + i2 + ", requestId=" + j + ")");
        if (!this.mIsAuthenticating) {
            onAuthSessionStarted(i);
        }
        if (this.mAuthOperations.contains(Integer.valueOf(i2))) {
            Slog.e("AuthSessionCoordinator", "Error, authStartedFor(" + i2 + ") without being finished");
        } else if (this.mUserId != i) {
            Slog.e("AuthSessionCoordinator", "Error authStartedFor(" + i + ") Incorrect userId, expected" + this.mUserId + ", ignoring...");
        } else {
            this.mAuthOperations.add(Integer.valueOf(i2));
        }
    }

    public void lockedOutFor(int i, @BiometricManager.Authenticators.Types int i2, int i3, long j) {
        String str = "lockOutFor(userId=" + i + ", biometricStrength=" + i2 + ", sensorId=" + i3 + ", requestId=" + j + ")";
        this.mRingBuffer.addApiCall(str);
        this.mAuthResultCoordinator.lockedOutFor(i2);
        attemptToFinish(i, i3, str);
    }

    public void lockOutTimed(int i, @BiometricManager.Authenticators.Types int i2, int i3, long j, long j2) {
        String str = "lockOutTimedFor(userId=" + i + ", biometricStrength=" + i2 + ", sensorId=" + i3 + "time=" + j + ", requestId=" + j2 + ")";
        this.mRingBuffer.addApiCall(str);
        this.mTimedLockouts.add(new Pair<>(Integer.valueOf(i2), Long.valueOf(j)));
        attemptToFinish(i, i3, str);
    }

    public void authEndedFor(int i, @BiometricManager.Authenticators.Types int i2, int i3, long j, boolean z) {
        String str = "authEndedFor(userId=" + i + " ,biometricStrength=" + i2 + ", sensorId=" + i3 + ", requestId=" + j + ", wasSuccessful=" + z + ")";
        this.mRingBuffer.addApiCall(str);
        if (z) {
            this.mAuthResultCoordinator.authenticatedFor(i2);
        }
        attemptToFinish(i, i3, str);
    }

    public void resetLockoutFor(int i, @BiometricManager.Authenticators.Types int i2, long j) {
        this.mRingBuffer.addApiCall("resetLockoutFor(userId=" + i + " ,biometricStrength=" + i2 + ", requestId=" + j + ")");
        if (i2 == 15) {
            clearSession();
            this.mMultiBiometricLockoutState.setAuthenticatorTo(i, i2, true);
            this.mMultiBiometricLockoutState.clearLockoutTime(i, i2);
        }
    }

    public final void attemptToFinish(int i, int i2, String str) {
        boolean z;
        boolean z2 = true;
        if (this.mAuthOperations.contains(Integer.valueOf(i2))) {
            z = false;
        } else {
            Slog.e("AuthSessionCoordinator", "Error unable to find auth operation : " + str);
            z = true;
        }
        if (i != this.mUserId) {
            Slog.e("AuthSessionCoordinator", "Error mismatched userId, expected=" + this.mUserId + " for " + str);
        } else {
            z2 = z;
        }
        if (z2) {
            return;
        }
        this.mAuthOperations.remove(Integer.valueOf(i2));
        if (this.mIsAuthenticating && this.mAuthOperations.isEmpty()) {
            endAuthSession();
        }
    }

    public String toString() {
        return this.mRingBuffer + "\n" + this.mMultiBiometricLockoutState;
    }

    /* loaded from: classes.dex */
    public static class RingBuffer {
        public int mApiCallNumber;
        public final String[] mApiCalls;
        public int mCurr;
        public final int mSize;

        public RingBuffer(int i) {
            if (i <= 0) {
                Slog.wtf("AuthSessionCoordinator", "Cannot initialize ring buffer of size: " + i);
            }
            this.mApiCalls = new String[i];
            this.mCurr = 0;
            this.mSize = i;
            this.mApiCallNumber = 0;
        }

        public void addApiCall(String str) {
            String[] strArr = this.mApiCalls;
            int i = this.mCurr;
            strArr[i] = str;
            int i2 = i + 1;
            this.mCurr = i2;
            this.mCurr = i2 % this.mSize;
            this.mApiCallNumber++;
        }

        public String toString() {
            int i;
            int i2 = this.mApiCallNumber;
            int i3 = this.mSize;
            int i4 = 0;
            int i5 = i2 > i3 ? i2 - i3 : 0;
            String str = "";
            while (true) {
                int i6 = this.mSize;
                if (i4 >= i6) {
                    return str;
                }
                if (this.mApiCalls[(this.mCurr + i4) % i6] != null) {
                    str = str + String.format("#%-5d %s\n", Integer.valueOf(i5), this.mApiCalls[i]);
                    i5++;
                }
                i4++;
            }
        }
    }
}
