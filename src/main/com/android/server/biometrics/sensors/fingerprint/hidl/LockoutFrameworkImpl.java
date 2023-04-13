package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.server.biometrics.sensors.LockoutTracker;
/* loaded from: classes.dex */
public class LockoutFrameworkImpl implements LockoutTracker {
    public final AlarmManager mAlarmManager;
    public final Context mContext;
    public final LockoutReceiver mLockoutReceiver;
    public final LockoutResetCallback mLockoutResetCallback;
    public final SparseBooleanArray mTimedLockoutCleared = new SparseBooleanArray();
    public final SparseIntArray mFailedAttempts = new SparseIntArray();

    /* loaded from: classes.dex */
    public interface LockoutResetCallback {
        void onLockoutReset(int i);
    }

    /* loaded from: classes.dex */
    public final class LockoutReceiver extends BroadcastReceiver {
        public LockoutReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Slog.v("LockoutTracker", "Resetting lockout: " + intent.getAction());
            if ("com.android.server.biometrics.sensors.fingerprint.ACTION_LOCKOUT_RESET".equals(intent.getAction())) {
                LockoutFrameworkImpl.this.resetFailedAttemptsForUser(false, intent.getIntExtra("lockout_reset_user", 0));
            }
        }
    }

    public LockoutFrameworkImpl(Context context, LockoutResetCallback lockoutResetCallback) {
        this.mContext = context;
        this.mLockoutResetCallback = lockoutResetCallback;
        this.mAlarmManager = (AlarmManager) context.getSystemService(AlarmManager.class);
        LockoutReceiver lockoutReceiver = new LockoutReceiver();
        this.mLockoutReceiver = lockoutReceiver;
        context.registerReceiver(lockoutReceiver, new IntentFilter("com.android.server.biometrics.sensors.fingerprint.ACTION_LOCKOUT_RESET"), "android.permission.RESET_FINGERPRINT_LOCKOUT", null, 2);
    }

    public void resetFailedAttemptsForUser(boolean z, int i) {
        if (getLockoutModeForUser(i) != 0) {
            Slog.v("LockoutTracker", "Reset biometric lockout for user: " + i + ", clearAttemptCounter: " + z);
        }
        if (z) {
            this.mFailedAttempts.put(i, 0);
        }
        this.mTimedLockoutCleared.put(i, true);
        cancelLockoutResetForUser(i);
        this.mLockoutResetCallback.onLockoutReset(i);
    }

    public void addFailedAttemptForUser(int i) {
        SparseIntArray sparseIntArray = this.mFailedAttempts;
        sparseIntArray.put(i, sparseIntArray.get(i, 0) + 1);
        this.mTimedLockoutCleared.put(i, false);
        if (getLockoutModeForUser(i) != 0) {
            scheduleLockoutResetForUser(i);
        }
    }

    @Override // com.android.server.biometrics.sensors.LockoutTracker
    public int getLockoutModeForUser(int i) {
        int i2 = this.mFailedAttempts.get(i, 0);
        if (i2 >= 20) {
            return 2;
        }
        return (i2 <= 0 || this.mTimedLockoutCleared.get(i, false) || i2 % 5 != 0) ? 0 : 1;
    }

    public final void cancelLockoutResetForUser(int i) {
        this.mAlarmManager.cancel(getLockoutResetIntentForUser(i));
    }

    public final void scheduleLockoutResetForUser(int i) {
        this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + 30000, getLockoutResetIntentForUser(i));
    }

    public final PendingIntent getLockoutResetIntentForUser(int i) {
        return PendingIntent.getBroadcast(this.mContext, i, new Intent("com.android.server.biometrics.sensors.fingerprint.ACTION_LOCKOUT_RESET").putExtra("lockout_reset_user", i), 201326592);
    }
}
