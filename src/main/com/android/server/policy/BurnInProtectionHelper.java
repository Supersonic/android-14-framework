package com.android.server.policy;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.os.SystemClock;
import android.view.Display;
import android.view.animation.LinearInterpolator;
import com.android.server.LocalServices;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class BurnInProtectionHelper implements DisplayManager.DisplayListener, Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
    public static final long BURNIN_PROTECTION_FIRST_WAKEUP_INTERVAL_MS;
    public static final long BURNIN_PROTECTION_MINIMAL_INTERVAL_MS;
    public static final long BURNIN_PROTECTION_SUBSEQUENT_WAKEUP_INTERVAL_MS;
    public final AlarmManager mAlarmManager;
    public boolean mBurnInProtectionActive;
    public final PendingIntent mBurnInProtectionIntent;
    public final int mBurnInRadiusMaxSquared;
    public final ValueAnimator mCenteringAnimator;
    public final Display mDisplay;
    public final DisplayManagerInternal mDisplayManagerInternal;
    public boolean mFirstUpdate;
    public final int mMaxHorizontalBurnInOffset;
    public final int mMaxVerticalBurnInOffset;
    public final int mMinHorizontalBurnInOffset;
    public final int mMinVerticalBurnInOffset;
    public int mLastBurnInXOffset = 0;
    public int mXOffsetDirection = 1;
    public int mLastBurnInYOffset = 0;
    public int mYOffsetDirection = 1;
    public int mAppliedBurnInXOffset = 0;
    public int mAppliedBurnInYOffset = 0;
    public BroadcastReceiver mBurnInProtectionReceiver = new BroadcastReceiver() { // from class: com.android.server.policy.BurnInProtectionHelper.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            BurnInProtectionHelper.this.updateBurnInProtection();
        }
    };

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animator) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationRepeat(Animator animator) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animator) {
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayAdded(int i) {
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayRemoved(int i) {
    }

    static {
        TimeUnit timeUnit = TimeUnit.MINUTES;
        BURNIN_PROTECTION_FIRST_WAKEUP_INTERVAL_MS = timeUnit.toMillis(1L);
        BURNIN_PROTECTION_SUBSEQUENT_WAKEUP_INTERVAL_MS = timeUnit.toMillis(2L);
        BURNIN_PROTECTION_MINIMAL_INTERVAL_MS = TimeUnit.SECONDS.toMillis(10L);
    }

    public BurnInProtectionHelper(Context context, int i, int i2, int i3, int i4, int i5) {
        this.mMinHorizontalBurnInOffset = i;
        this.mMaxHorizontalBurnInOffset = i2;
        this.mMinVerticalBurnInOffset = i3;
        this.mMaxVerticalBurnInOffset = i4;
        if (i5 != -1) {
            this.mBurnInRadiusMaxSquared = i5 * i5;
        } else {
            this.mBurnInRadiusMaxSquared = -1;
        }
        this.mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        context.registerReceiver(this.mBurnInProtectionReceiver, new IntentFilter("android.internal.policy.action.BURN_IN_PROTECTION"));
        Intent intent = new Intent("android.internal.policy.action.BURN_IN_PROTECTION");
        intent.setPackage(context.getPackageName());
        intent.setFlags(1073741824);
        this.mBurnInProtectionIntent = PendingIntent.getBroadcast(context, 0, intent, 167772160);
        DisplayManager displayManager = (DisplayManager) context.getSystemService("display");
        this.mDisplay = displayManager.getDisplay(0);
        displayManager.registerDisplayListener(this, null);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
        this.mCenteringAnimator = ofFloat;
        ofFloat.setDuration(100L);
        ofFloat.setInterpolator(new LinearInterpolator());
        ofFloat.addListener(this);
        ofFloat.addUpdateListener(this);
    }

    public void startBurnInProtection() {
        if (this.mBurnInProtectionActive) {
            return;
        }
        this.mBurnInProtectionActive = true;
        this.mFirstUpdate = true;
        this.mCenteringAnimator.cancel();
        updateBurnInProtection();
    }

    public final void updateBurnInProtection() {
        long j;
        if (this.mBurnInProtectionActive) {
            boolean z = this.mFirstUpdate;
            if (z) {
                j = BURNIN_PROTECTION_FIRST_WAKEUP_INTERVAL_MS;
            } else {
                j = BURNIN_PROTECTION_SUBSEQUENT_WAKEUP_INTERVAL_MS;
            }
            if (z) {
                this.mFirstUpdate = false;
            } else {
                adjustOffsets();
                this.mAppliedBurnInXOffset = this.mLastBurnInXOffset;
                this.mAppliedBurnInYOffset = this.mLastBurnInYOffset;
                this.mDisplayManagerInternal.setDisplayOffsets(this.mDisplay.getDisplayId(), this.mLastBurnInXOffset, this.mLastBurnInYOffset);
            }
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j2 = BURNIN_PROTECTION_MINIMAL_INTERVAL_MS + currentTimeMillis;
            this.mAlarmManager.setExact(3, elapsedRealtime + (((j2 - (j2 % j)) + j) - currentTimeMillis), this.mBurnInProtectionIntent);
            return;
        }
        this.mAlarmManager.cancel(this.mBurnInProtectionIntent);
        this.mCenteringAnimator.start();
    }

    public void cancelBurnInProtection() {
        if (this.mBurnInProtectionActive) {
            this.mBurnInProtectionActive = false;
            updateBurnInProtection();
        }
    }

    public final void adjustOffsets() {
        int i;
        int i2;
        int i3;
        do {
            int i4 = this.mXOffsetDirection;
            int i5 = i4 * 2;
            int i6 = this.mLastBurnInXOffset + i5;
            this.mLastBurnInXOffset = i6;
            if (i6 > this.mMaxHorizontalBurnInOffset || i6 < this.mMinHorizontalBurnInOffset) {
                this.mLastBurnInXOffset = i6 - i5;
                this.mXOffsetDirection = i4 * (-1);
                int i7 = this.mYOffsetDirection;
                int i8 = i7 * 2;
                int i9 = this.mLastBurnInYOffset + i8;
                this.mLastBurnInYOffset = i9;
                if (i9 > this.mMaxVerticalBurnInOffset || i9 < this.mMinVerticalBurnInOffset) {
                    this.mLastBurnInYOffset = i9 - i8;
                    this.mYOffsetDirection = i7 * (-1);
                }
            }
            i = this.mBurnInRadiusMaxSquared;
            if (i == -1) {
                return;
            }
            i2 = this.mLastBurnInXOffset;
            i3 = this.mLastBurnInYOffset;
        } while ((i2 * i2) + (i3 * i3) > i);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "BurnInProtection");
        String str2 = str + "  ";
        printWriter.println(str2 + "mBurnInProtectionActive=" + this.mBurnInProtectionActive);
        printWriter.println(str2 + "mHorizontalBurnInOffsetsBounds=(" + this.mMinHorizontalBurnInOffset + ", " + this.mMaxHorizontalBurnInOffset + ")");
        printWriter.println(str2 + "mVerticalBurnInOffsetsBounds=(" + this.mMinVerticalBurnInOffset + ", " + this.mMaxVerticalBurnInOffset + ")");
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append("mBurnInRadiusMaxSquared=");
        sb.append(this.mBurnInRadiusMaxSquared);
        printWriter.println(sb.toString());
        printWriter.println(str2 + "mLastBurnInOffset=(" + this.mLastBurnInXOffset + ", " + this.mLastBurnInYOffset + ")");
        printWriter.println(str2 + "mOfsetChangeDirections=(" + this.mXOffsetDirection + ", " + this.mYOffsetDirection + ")");
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayChanged(int i) {
        if (i == this.mDisplay.getDisplayId()) {
            if (this.mDisplay.getState() == 3 || this.mDisplay.getState() == 4 || this.mDisplay.getState() == 6) {
                startBurnInProtection();
            } else {
                cancelBurnInProtection();
            }
        }
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animator) {
        if (animator != this.mCenteringAnimator || this.mBurnInProtectionActive) {
            return;
        }
        this.mAppliedBurnInXOffset = 0;
        this.mAppliedBurnInYOffset = 0;
        this.mDisplayManagerInternal.setDisplayOffsets(this.mDisplay.getDisplayId(), 0, 0);
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (this.mBurnInProtectionActive) {
            return;
        }
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mDisplayManagerInternal.setDisplayOffsets(this.mDisplay.getDisplayId(), (int) (this.mAppliedBurnInXOffset * floatValue), (int) (this.mAppliedBurnInYOffset * floatValue));
    }
}
