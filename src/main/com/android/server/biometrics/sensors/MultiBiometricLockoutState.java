package com.android.server.biometrics.sensors;

import android.hardware.biometrics.BiometricManager;
import android.util.Slog;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class MultiBiometricLockoutState {
    public final Map<Integer, Map<Integer, AuthenticatorState>> mCanUserAuthenticate = new HashMap();
    public final Clock mClock;

    public MultiBiometricLockoutState(Clock clock) {
        this.mClock = clock;
    }

    public final Map<Integer, AuthenticatorState> createUnlockedMap() {
        HashMap hashMap = new HashMap();
        hashMap.put(15, new AuthenticatorState(15, false, 0L, this.mClock));
        hashMap.put(255, new AuthenticatorState(255, false, 0L, this.mClock));
        hashMap.put(4095, new AuthenticatorState(4095, false, 0L, this.mClock));
        return hashMap;
    }

    public final Map<Integer, AuthenticatorState> getAuthMapForUser(int i) {
        if (!this.mCanUserAuthenticate.containsKey(Integer.valueOf(i))) {
            this.mCanUserAuthenticate.put(Integer.valueOf(i), createUnlockedMap());
        }
        return this.mCanUserAuthenticate.get(Integer.valueOf(i));
    }

    public void setAuthenticatorTo(int i, @BiometricManager.Authenticators.Types int i2, boolean z) {
        Map<Integer, AuthenticatorState> authMapForUser = getAuthMapForUser(i);
        if (i2 == 15) {
            authMapForUser.get(15).mPermanentlyLockedOut = !z;
        } else if (i2 != 255) {
            if (i2 != 4095) {
                Slog.e("MultiBiometricLockoutState", "increaseLockoutTime called for invalid strength : " + i2);
                return;
            }
            authMapForUser.get(4095).mPermanentlyLockedOut = !z;
        }
        authMapForUser.get(255).mPermanentlyLockedOut = !z;
        authMapForUser.get(4095).mPermanentlyLockedOut = !z;
    }

    public void increaseLockoutTime(int i, @BiometricManager.Authenticators.Types int i2, long j) {
        Map<Integer, AuthenticatorState> authMapForUser = getAuthMapForUser(i);
        if (i2 == 15) {
            authMapForUser.get(15).increaseLockoutTo(j);
        } else if (i2 != 255) {
            if (i2 != 4095) {
                Slog.e("MultiBiometricLockoutState", "increaseLockoutTime called for invalid strength : " + i2);
                return;
            }
            authMapForUser.get(4095).increaseLockoutTo(j);
        }
        authMapForUser.get(255).increaseLockoutTo(j);
        authMapForUser.get(4095).increaseLockoutTo(j);
    }

    public void clearLockoutTime(int i, @BiometricManager.Authenticators.Types int i2) {
        Map<Integer, AuthenticatorState> authMapForUser = getAuthMapForUser(i);
        if (i2 == 15) {
            authMapForUser.get(15).setTimedLockout(0L);
        } else if (i2 != 255) {
            if (i2 != 4095) {
                Slog.e("MultiBiometricLockoutState", "clearLockoutTime called for invalid strength : " + i2);
                return;
            }
            authMapForUser.get(4095).setTimedLockout(0L);
        }
        authMapForUser.get(255).setTimedLockout(0L);
        authMapForUser.get(4095).setTimedLockout(0L);
    }

    public int getLockoutState(int i, @BiometricManager.Authenticators.Types int i2) {
        Map<Integer, AuthenticatorState> authMapForUser = getAuthMapForUser(i);
        if (!authMapForUser.containsKey(Integer.valueOf(i2))) {
            Slog.e("MultiBiometricLockoutState", "Error, getLockoutState for unknown strength: " + i2 + " returning LOCKOUT_NONE");
            return 0;
        }
        AuthenticatorState authenticatorState = authMapForUser.get(Integer.valueOf(i2));
        if (authenticatorState.mPermanentlyLockedOut) {
            return 2;
        }
        return authenticatorState.isTimedLockout() ? 1 : 0;
    }

    public String toString() {
        final long millis = this.mClock.millis();
        String str = "Permanent Lockouts\n";
        for (Map.Entry<Integer, Map<Integer, AuthenticatorState>> entry : this.mCanUserAuthenticate.entrySet()) {
            str = str + "UserId=" + entry.getKey().intValue() + ", {" + ((String) entry.getValue().entrySet().stream().map(new Function() { // from class: com.android.server.biometrics.sensors.MultiBiometricLockoutState$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$toString$0;
                    lambda$toString$0 = MultiBiometricLockoutState.lambda$toString$0(millis, (Map.Entry) obj);
                    return lambda$toString$0;
                }
            }).collect(Collectors.joining(", "))) + "}\n";
        }
        return str;
    }

    public static /* synthetic */ String lambda$toString$0(long j, Map.Entry entry) {
        return ((AuthenticatorState) entry.getValue()).toString(j);
    }

    /* loaded from: classes.dex */
    public static class AuthenticatorState {
        public Integer mAuthenticatorType;
        public Clock mClock;
        public boolean mPermanentlyLockedOut;
        public long mTimedLockout;

        public AuthenticatorState(Integer num, boolean z, long j, Clock clock) {
            this.mAuthenticatorType = num;
            this.mPermanentlyLockedOut = z;
            this.mTimedLockout = j;
            this.mClock = clock;
        }

        public boolean isTimedLockout() {
            return this.mClock.millis() - this.mTimedLockout < 0;
        }

        public void setTimedLockout(long j) {
            this.mTimedLockout = j;
        }

        public void increaseLockoutTo(long j) {
            this.mTimedLockout = Math.max(this.mTimedLockout, j);
        }

        public String toString(long j) {
            String str;
            if (this.mTimedLockout - j > 0) {
                str = (this.mTimedLockout - j) + "ms";
            } else {
                str = "none";
            }
            return String.format("(%s, permanentLockout=%s, timedLockoutRemaining=%s)", BiometricManager.authenticatorToStr(this.mAuthenticatorType.intValue()), this.mPermanentlyLockedOut ? "true" : "false", str);
        }
    }
}
