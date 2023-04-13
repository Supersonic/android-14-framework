package com.android.server.power;

import android.app.ActivityManagerInternal;
import android.app.AppOpsManager;
import android.app.BroadcastOptions;
import android.app.trust.TrustManager;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.hardware.display.DisplayManagerInternal;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IWakeLockCallback;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManagerInternal;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.WorkSource;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.EventLog;
import android.view.WindowManagerPolicyConstants;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.input.InputManagerInternal;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
@VisibleForTesting
/* loaded from: classes2.dex */
public class Notifier {
    public static final int[] CHARGING_VIBRATION_AMPLITUDE;
    public static final VibrationEffect CHARGING_VIBRATION_EFFECT;
    public static final long[] CHARGING_VIBRATION_TIME;
    public static final VibrationAttributes HARDWARE_FEEDBACK_VIBRATION_ATTRIBUTES = VibrationAttributes.createForUsage(50);
    public final AppOpsManager mAppOps;
    public final Executor mBackgroundExecutor;
    public final IBatteryStats mBatteryStats;
    public boolean mBroadcastInProgress;
    public long mBroadcastStartTime;
    public int mBroadcastedInteractiveState;
    public final Context mContext;
    public final FaceDownDetector mFaceDownDetector;
    public final NotifierHandler mHandler;
    public int mInteractiveChangeReason;
    public long mInteractiveChangeStartTime;
    public boolean mInteractiveChanging;
    public boolean mPendingGoToSleepBroadcast;
    public int mPendingInteractiveState;
    public boolean mPendingWakeUpBroadcast;
    public final WindowManagerPolicy mPolicy;
    public final Intent mScreenOffIntent;
    public final Intent mScreenOnIntent;
    public final Bundle mScreenOnOffOptions;
    public final ScreenUndimDetector mScreenUndimDetector;
    public final boolean mShowWirelessChargingAnimationConfig;
    public final SuspendBlocker mSuspendBlocker;
    public final boolean mSuspendWhenScreenOffDueToProximityConfig;
    public final TrustManager mTrustManager;
    public boolean mUserActivityPending;
    public final Vibrator mVibrator;
    public final WakeLockLog mWakeLockLog;
    public final Object mLock = new Object();
    public boolean mInteractive = true;
    public final AtomicBoolean mIsPlayingChargingStartedFeedback = new AtomicBoolean(false);
    public final IIntentReceiver mWakeUpBroadcastDone = new IIntentReceiver.Stub() { // from class: com.android.server.power.Notifier.2
        public void performReceive(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, int i2) {
            EventLog.writeEvent(2726, 1, Long.valueOf(SystemClock.uptimeMillis() - Notifier.this.mBroadcastStartTime), 1);
            Notifier.this.sendNextBroadcast();
        }
    };
    public final IIntentReceiver mGoToSleepBroadcastDone = new IIntentReceiver.Stub() { // from class: com.android.server.power.Notifier.3
        public void performReceive(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, int i2) {
            EventLog.writeEvent(2726, 0, Long.valueOf(SystemClock.uptimeMillis() - Notifier.this.mBroadcastStartTime), 1);
            Notifier.this.sendNextBroadcast();
        }
    };
    public final ActivityManagerInternal mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
    public final InputManagerInternal mInputManagerInternal = (InputManagerInternal) LocalServices.getService(InputManagerInternal.class);
    public final InputMethodManagerInternal mInputMethodManagerInternal = (InputMethodManagerInternal) LocalServices.getService(InputMethodManagerInternal.class);
    public final StatusBarManagerInternal mStatusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
    public final DisplayManagerInternal mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);

    static {
        long[] jArr = {40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40};
        CHARGING_VIBRATION_TIME = jArr;
        int[] iArr = {1, 4, 11, 25, 44, 67, 91, 114, 123, 103, 79, 55, 34, 17, 7, 2};
        CHARGING_VIBRATION_AMPLITUDE = iArr;
        CHARGING_VIBRATION_EFFECT = VibrationEffect.createWaveform(jArr, iArr, -1);
    }

    public Notifier(Looper looper, Context context, IBatteryStats iBatteryStats, SuspendBlocker suspendBlocker, WindowManagerPolicy windowManagerPolicy, FaceDownDetector faceDownDetector, ScreenUndimDetector screenUndimDetector, Executor executor) {
        this.mContext = context;
        this.mBatteryStats = iBatteryStats;
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mSuspendBlocker = suspendBlocker;
        this.mPolicy = windowManagerPolicy;
        this.mFaceDownDetector = faceDownDetector;
        this.mScreenUndimDetector = screenUndimDetector;
        this.mTrustManager = (TrustManager) context.getSystemService(TrustManager.class);
        this.mVibrator = (Vibrator) context.getSystemService(Vibrator.class);
        this.mHandler = new NotifierHandler(looper);
        this.mBackgroundExecutor = executor;
        Intent intent = new Intent("android.intent.action.SCREEN_ON");
        this.mScreenOnIntent = intent;
        intent.addFlags(1344274432);
        Intent intent2 = new Intent("android.intent.action.SCREEN_OFF");
        this.mScreenOffIntent = intent2;
        intent2.addFlags(1344274432);
        this.mScreenOnOffOptions = createScreenOnOffBroadcastOptions();
        this.mSuspendWhenScreenOffDueToProximityConfig = context.getResources().getBoolean(17891838);
        this.mShowWirelessChargingAnimationConfig = context.getResources().getBoolean(17891789);
        this.mWakeLockLog = new WakeLockLog();
        try {
            iBatteryStats.noteInteractive(true);
        } catch (RemoteException unused) {
        }
        FrameworkStatsLog.write(33, 1);
    }

    public final Bundle createScreenOnOffBroadcastOptions() {
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setDeliveryGroupPolicy(1);
        makeBasic.setDeliveryGroupMatchingKey(UUID.randomUUID().toString(), "android.intent.action.SCREEN_ON");
        makeBasic.setDeferUntilActive(true);
        return makeBasic.toBundle();
    }

    public void onWakeLockAcquired(int i, String str, String str2, int i2, int i3, WorkSource workSource, String str3, IWakeLockCallback iWakeLockCallback) {
        boolean z = true;
        notifyWakeLockListener(iWakeLockCallback, true);
        int batteryStatsWakeLockMonitorType = getBatteryStatsWakeLockMonitorType(i);
        if (batteryStatsWakeLockMonitorType >= 0) {
            boolean z2 = (i2 != 1000 || (1073741824 & i) == 0) ? false : false;
            try {
                if (workSource != null) {
                    this.mBatteryStats.noteStartWakelockFromSource(workSource, i3, str, str3, batteryStatsWakeLockMonitorType, z2);
                } else {
                    this.mBatteryStats.noteStartWakelock(i2, i3, str, str3, batteryStatsWakeLockMonitorType, z2);
                    this.mAppOps.startOpNoThrow(40, i2, str2);
                }
            } catch (RemoteException unused) {
            }
        }
        this.mWakeLockLog.onWakeLockAcquired(str, i2, i);
    }

    public void onLongPartialWakeLockStart(String str, int i, WorkSource workSource, String str2) {
        try {
            if (workSource != null) {
                this.mBatteryStats.noteLongPartialWakelockStartFromSource(str, str2, workSource);
                FrameworkStatsLog.write(11, workSource, str, str2, 1);
            } else {
                this.mBatteryStats.noteLongPartialWakelockStart(str, str2, i);
                FrameworkStatsLog.write_non_chained(11, i, (String) null, str, str2, 1);
            }
        } catch (RemoteException unused) {
        }
    }

    public void onLongPartialWakeLockFinish(String str, int i, WorkSource workSource, String str2) {
        try {
            if (workSource != null) {
                this.mBatteryStats.noteLongPartialWakelockFinishFromSource(str, str2, workSource);
                FrameworkStatsLog.write(11, workSource, str, str2, 0);
            } else {
                this.mBatteryStats.noteLongPartialWakelockFinish(str, str2, i);
                FrameworkStatsLog.write_non_chained(11, i, (String) null, str, str2, 0);
            }
        } catch (RemoteException unused) {
        }
    }

    public void onWakeLockChanging(int i, String str, String str2, int i2, int i3, WorkSource workSource, String str3, IWakeLockCallback iWakeLockCallback, int i4, String str4, String str5, int i5, int i6, WorkSource workSource2, String str6, IWakeLockCallback iWakeLockCallback2) {
        int batteryStatsWakeLockMonitorType = getBatteryStatsWakeLockMonitorType(i);
        int batteryStatsWakeLockMonitorType2 = getBatteryStatsWakeLockMonitorType(i4);
        if (workSource != null && workSource2 != null && batteryStatsWakeLockMonitorType >= 0 && batteryStatsWakeLockMonitorType2 >= 0) {
            try {
                this.mBatteryStats.noteChangeWakelockFromSource(workSource, i3, str, str3, batteryStatsWakeLockMonitorType, workSource2, i6, str4, str6, batteryStatsWakeLockMonitorType2, i5 == 1000 && (1073741824 & i4) != 0);
            } catch (RemoteException unused) {
            }
        } else if (!PowerManagerService.isSameCallback(iWakeLockCallback, iWakeLockCallback2)) {
            onWakeLockReleased(i, str, str2, i2, i3, workSource, str3, null);
            onWakeLockAcquired(i4, str4, str5, i5, i6, workSource2, str6, iWakeLockCallback2);
        } else {
            onWakeLockReleased(i, str, str2, i2, i3, workSource, str3, iWakeLockCallback);
            onWakeLockAcquired(i4, str4, str5, i5, i6, workSource2, str6, iWakeLockCallback2);
        }
    }

    public void onWakeLockReleased(int i, String str, String str2, int i2, int i3, WorkSource workSource, String str3, IWakeLockCallback iWakeLockCallback) {
        notifyWakeLockListener(iWakeLockCallback, false);
        int batteryStatsWakeLockMonitorType = getBatteryStatsWakeLockMonitorType(i);
        if (batteryStatsWakeLockMonitorType >= 0) {
            try {
                if (workSource != null) {
                    this.mBatteryStats.noteStopWakelockFromSource(workSource, i3, str, str3, batteryStatsWakeLockMonitorType);
                } else {
                    this.mBatteryStats.noteStopWakelock(i2, i3, str, str3, batteryStatsWakeLockMonitorType);
                    this.mAppOps.finishOp(40, i2, str2);
                }
            } catch (RemoteException unused) {
            }
        }
        this.mWakeLockLog.onWakeLockReleased(str, i2);
    }

    public final int getBatteryStatsWakeLockMonitorType(int i) {
        int i2 = i & GnssNative.GNSS_AIDING_TYPE_ALL;
        if (i2 != 1) {
            if (i2 == 6 || i2 == 10) {
                return 1;
            }
            return i2 != 32 ? i2 != 128 ? -1 : 18 : this.mSuspendWhenScreenOffDueToProximityConfig ? -1 : 0;
        }
        return 0;
    }

    public void onWakefulnessChangeStarted(final int i, int i2, long j) {
        boolean isInteractive = PowerManagerInternal.isInteractive(i);
        this.mHandler.post(new Runnable() { // from class: com.android.server.power.Notifier.1
            @Override // java.lang.Runnable
            public void run() {
                Notifier.this.mActivityManagerInternal.onWakefulnessChanged(i);
            }
        });
        if (this.mInteractive != isInteractive) {
            if (this.mInteractiveChanging) {
                handleLateInteractiveChange();
            }
            this.mInputManagerInternal.setInteractive(isInteractive);
            this.mInputMethodManagerInternal.setInteractive(isInteractive);
            try {
                this.mBatteryStats.noteInteractive(isInteractive);
            } catch (RemoteException unused) {
            }
            FrameworkStatsLog.write(33, isInteractive ? 1 : 0);
            this.mInteractive = isInteractive;
            this.mInteractiveChangeReason = i2;
            this.mInteractiveChangeStartTime = j;
            this.mInteractiveChanging = true;
            handleEarlyInteractiveChange();
        }
    }

    public void onWakefulnessChangeFinished() {
        if (this.mInteractiveChanging) {
            this.mInteractiveChanging = false;
            handleLateInteractiveChange();
        }
    }

    public final void handleEarlyInteractiveChange() {
        synchronized (this.mLock) {
            if (this.mInteractive) {
                this.mHandler.post(new Runnable() { // from class: com.android.server.power.Notifier$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        Notifier.this.lambda$handleEarlyInteractiveChange$0();
                    }
                });
                this.mPendingInteractiveState = 1;
                this.mPendingWakeUpBroadcast = true;
                updatePendingBroadcastLocked();
            } else {
                this.mHandler.post(new Runnable() { // from class: com.android.server.power.Notifier$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        Notifier.this.lambda$handleEarlyInteractiveChange$1();
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleEarlyInteractiveChange$0() {
        this.mPolicy.startedWakingUp(this.mInteractiveChangeReason);
        this.mDisplayManagerInternal.onEarlyInteractivityChange(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleEarlyInteractiveChange$1() {
        this.mPolicy.startedGoingToSleep(this.mInteractiveChangeReason);
        this.mDisplayManagerInternal.onEarlyInteractivityChange(false);
    }

    public final void handleLateInteractiveChange() {
        synchronized (this.mLock) {
            final int uptimeMillis = (int) (SystemClock.uptimeMillis() - this.mInteractiveChangeStartTime);
            if (this.mInteractive) {
                this.mHandler.post(new Runnable() { // from class: com.android.server.power.Notifier$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        Notifier.this.lambda$handleLateInteractiveChange$2(uptimeMillis);
                    }
                });
            } else {
                if (this.mUserActivityPending) {
                    this.mUserActivityPending = false;
                    this.mHandler.removeMessages(1);
                }
                final int translateSleepReasonToOffReason = WindowManagerPolicyConstants.translateSleepReasonToOffReason(this.mInteractiveChangeReason);
                this.mHandler.post(new Runnable() { // from class: com.android.server.power.Notifier$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        Notifier.this.lambda$handleLateInteractiveChange$3(translateSleepReasonToOffReason, uptimeMillis);
                    }
                });
                this.mPendingInteractiveState = 2;
                this.mPendingGoToSleepBroadcast = true;
                updatePendingBroadcastLocked();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleLateInteractiveChange$2(int i) {
        LogMaker logMaker = new LogMaker((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_USB_DATA_SIGNALING);
        logMaker.setType(1);
        logMaker.setSubtype(WindowManagerPolicyConstants.translateWakeReasonToOnReason(this.mInteractiveChangeReason));
        logMaker.setLatency(i);
        logMaker.addTaggedData(1694, Integer.valueOf(this.mInteractiveChangeReason));
        MetricsLogger.action(logMaker);
        EventLogTags.writePowerScreenState(1, 0, 0L, 0, i);
        this.mPolicy.finishedWakingUp(this.mInteractiveChangeReason);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleLateInteractiveChange$3(int i, int i2) {
        LogMaker logMaker = new LogMaker((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_USB_DATA_SIGNALING);
        logMaker.setType(2);
        logMaker.setSubtype(i);
        logMaker.setLatency(i2);
        logMaker.addTaggedData(1695, Integer.valueOf(this.mInteractiveChangeReason));
        MetricsLogger.action(logMaker);
        EventLogTags.writePowerScreenState(0, i, 0L, 0, i2);
        this.mPolicy.finishedGoingToSleep(this.mInteractiveChangeReason);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPowerGroupWakefulnessChanged$4(int i, int i2, int i3, int i4) {
        this.mPolicy.onPowerGroupWakefulnessChanged(i, i2, i3, i4);
    }

    public void onPowerGroupWakefulnessChanged(final int i, final int i2, final int i3, final int i4) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.power.Notifier$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                Notifier.this.lambda$onPowerGroupWakefulnessChanged$4(i, i2, i3, i4);
            }
        });
    }

    public void onUserActivity(int i, int i2, int i3) {
        try {
            this.mBatteryStats.noteUserActivity(i3, i2);
        } catch (RemoteException unused) {
        }
        synchronized (this.mLock) {
            if (!this.mUserActivityPending) {
                this.mUserActivityPending = true;
                Message obtainMessage = this.mHandler.obtainMessage(1);
                obtainMessage.arg1 = i;
                obtainMessage.arg2 = i2;
                obtainMessage.setAsynchronous(true);
                this.mHandler.sendMessage(obtainMessage);
            }
        }
    }

    public void onWakeUp(int i, String str, int i2, String str2, int i3) {
        try {
            this.mBatteryStats.noteWakeUp(str, i2);
            if (str2 != null) {
                this.mAppOps.noteOpNoThrow(61, i3, str2);
            }
        } catch (RemoteException unused) {
        }
        FrameworkStatsLog.write((int) FrameworkStatsLog.DISPLAY_WAKE_REPORTED, i, i2);
    }

    public void onProfileTimeout(int i) {
        Message obtainMessage = this.mHandler.obtainMessage(5);
        obtainMessage.setAsynchronous(true);
        obtainMessage.arg1 = i;
        this.mHandler.sendMessage(obtainMessage);
    }

    public void onWirelessChargingStarted(int i, int i2) {
        this.mSuspendBlocker.acquire();
        Message obtainMessage = this.mHandler.obtainMessage(3);
        obtainMessage.setAsynchronous(true);
        obtainMessage.arg1 = i;
        obtainMessage.arg2 = i2;
        this.mHandler.sendMessage(obtainMessage);
    }

    public void onWiredChargingStarted(int i) {
        this.mSuspendBlocker.acquire();
        Message obtainMessage = this.mHandler.obtainMessage(6);
        obtainMessage.setAsynchronous(true);
        obtainMessage.arg1 = i;
        this.mHandler.sendMessage(obtainMessage);
    }

    public void onScreenPolicyUpdate(int i, int i2) {
        synchronized (this.mLock) {
            Message obtainMessage = this.mHandler.obtainMessage(7);
            obtainMessage.arg1 = i;
            obtainMessage.arg2 = i2;
            obtainMessage.setAsynchronous(true);
            this.mHandler.sendMessage(obtainMessage);
        }
    }

    public void dump(PrintWriter printWriter) {
        WakeLockLog wakeLockLog = this.mWakeLockLog;
        if (wakeLockLog != null) {
            wakeLockLog.dump(printWriter);
        }
    }

    public final void updatePendingBroadcastLocked() {
        int i;
        if (this.mBroadcastInProgress || (i = this.mPendingInteractiveState) == 0) {
            return;
        }
        if (this.mPendingWakeUpBroadcast || this.mPendingGoToSleepBroadcast || i != this.mBroadcastedInteractiveState) {
            this.mBroadcastInProgress = true;
            this.mSuspendBlocker.acquire();
            Message obtainMessage = this.mHandler.obtainMessage(2);
            obtainMessage.setAsynchronous(true);
            this.mHandler.sendMessage(obtainMessage);
        }
    }

    public final void finishPendingBroadcastLocked() {
        this.mBroadcastInProgress = false;
        this.mSuspendBlocker.release();
    }

    public final void sendUserActivity(int i, int i2) {
        synchronized (this.mLock) {
            if (this.mUserActivityPending) {
                this.mUserActivityPending = false;
                ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).notifyUserActivity();
                this.mInputManagerInternal.notifyUserActivity();
                this.mPolicy.userActivity(i, i2);
                this.mFaceDownDetector.userActivity(i2);
                this.mScreenUndimDetector.userActivity(i);
            }
        }
    }

    public void postEnhancedDischargePredictionBroadcast(long j) {
        this.mHandler.sendEmptyMessageDelayed(4, j);
    }

    public final void sendEnhancedDischargePredictionBroadcast() {
        this.mContext.sendBroadcastAsUser(new Intent("android.os.action.ENHANCED_DISCHARGE_PREDICTION_CHANGED").addFlags(1073741824), UserHandle.ALL);
    }

    public final void sendNextBroadcast() {
        synchronized (this.mLock) {
            int i = this.mBroadcastedInteractiveState;
            if (i == 0) {
                if (this.mPendingInteractiveState == 2) {
                    this.mPendingGoToSleepBroadcast = false;
                    this.mBroadcastedInteractiveState = 2;
                } else {
                    this.mPendingWakeUpBroadcast = false;
                    this.mBroadcastedInteractiveState = 1;
                }
            } else if (i == 1) {
                if (!this.mPendingWakeUpBroadcast && !this.mPendingGoToSleepBroadcast && this.mPendingInteractiveState != 2) {
                    finishPendingBroadcastLocked();
                    return;
                }
                this.mPendingGoToSleepBroadcast = false;
                this.mBroadcastedInteractiveState = 2;
            } else {
                if (!this.mPendingWakeUpBroadcast && !this.mPendingGoToSleepBroadcast && this.mPendingInteractiveState != 1) {
                    finishPendingBroadcastLocked();
                    return;
                }
                this.mPendingWakeUpBroadcast = false;
                this.mBroadcastedInteractiveState = 1;
            }
            this.mBroadcastStartTime = SystemClock.uptimeMillis();
            int i2 = this.mBroadcastedInteractiveState;
            EventLog.writeEvent(2725, 1);
            if (i2 == 1) {
                sendWakeUpBroadcast();
            } else {
                sendGoToSleepBroadcast();
            }
        }
    }

    public final void sendWakeUpBroadcast() {
        if (this.mActivityManagerInternal.isSystemReady()) {
            this.mActivityManagerInternal.broadcastIntent(this.mScreenOnIntent, this.mWakeUpBroadcastDone, (String[]) null, !this.mActivityManagerInternal.isModernQueueEnabled(), -1, (int[]) null, (BiFunction) null, this.mScreenOnOffOptions);
            return;
        }
        EventLog.writeEvent(2727, 2, 1);
        sendNextBroadcast();
    }

    public final void sendGoToSleepBroadcast() {
        if (this.mActivityManagerInternal.isSystemReady()) {
            this.mActivityManagerInternal.broadcastIntent(this.mScreenOffIntent, this.mGoToSleepBroadcastDone, (String[]) null, !this.mActivityManagerInternal.isModernQueueEnabled(), -1, (int[]) null, (BiFunction) null, this.mScreenOnOffOptions);
            return;
        }
        EventLog.writeEvent(2727, 3, 1);
        sendNextBroadcast();
    }

    public final void playChargingStartedFeedback(final int i, final boolean z) {
        if (isChargingFeedbackEnabled(i) && this.mIsPlayingChargingStartedFeedback.compareAndSet(false, true)) {
            this.mBackgroundExecutor.execute(new Runnable() { // from class: com.android.server.power.Notifier$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    Notifier.this.lambda$playChargingStartedFeedback$5(i, z);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$playChargingStartedFeedback$5(int i, boolean z) {
        Ringtone ringtone;
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "charging_vibration_enabled", 1, i) != 0) {
            this.mVibrator.vibrate(CHARGING_VIBRATION_EFFECT, HARDWARE_FEEDBACK_VIBRATION_ATTRIBUTES);
        }
        String string = Settings.Global.getString(this.mContext.getContentResolver(), z ? "wireless_charging_started_sound" : "charging_started_sound");
        Uri parse = Uri.parse("file://" + string);
        if (parse != null && (ringtone = RingtoneManager.getRingtone(this.mContext, parse)) != null) {
            ringtone.setStreamType(1);
            ringtone.play();
        }
        this.mIsPlayingChargingStartedFeedback.set(false);
    }

    public final void showWirelessChargingStarted(int i, int i2) {
        StatusBarManagerInternal statusBarManagerInternal;
        playChargingStartedFeedback(i2, true);
        if (this.mShowWirelessChargingAnimationConfig && (statusBarManagerInternal = this.mStatusBarManagerInternal) != null) {
            statusBarManagerInternal.showChargingAnimation(i);
        }
        this.mSuspendBlocker.release();
    }

    public final void showWiredChargingStarted(int i) {
        playChargingStartedFeedback(i, false);
        this.mSuspendBlocker.release();
    }

    public final void screenPolicyChanging(int i, int i2) {
        this.mScreenUndimDetector.recordScreenPolicy(i, i2);
    }

    public final void lockProfile(int i) {
        this.mTrustManager.setDeviceLockedForUser(i, true);
    }

    public final boolean isChargingFeedbackEnabled(int i) {
        return (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "charging_sounds_enabled", 1, i) != 0) && (Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode", 1) == 0);
    }

    public final void notifyWakeLockListener(final IWakeLockCallback iWakeLockCallback, final boolean z) {
        if (iWakeLockCallback != null) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.power.Notifier$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Notifier.lambda$notifyWakeLockListener$6(iWakeLockCallback, z);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$notifyWakeLockListener$6(IWakeLockCallback iWakeLockCallback, boolean z) {
        try {
            iWakeLockCallback.onStateChanged(z);
        } catch (RemoteException e) {
            throw new IllegalArgumentException("Wakelock.mCallback is already dead.", e);
        }
    }

    /* loaded from: classes2.dex */
    public final class NotifierHandler extends Handler {
        public NotifierHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Notifier.this.sendUserActivity(message.arg1, message.arg2);
                    return;
                case 2:
                    Notifier.this.sendNextBroadcast();
                    return;
                case 3:
                    Notifier.this.showWirelessChargingStarted(message.arg1, message.arg2);
                    return;
                case 4:
                    removeMessages(4);
                    Notifier.this.sendEnhancedDischargePredictionBroadcast();
                    return;
                case 5:
                    Notifier.this.lockProfile(message.arg1);
                    return;
                case 6:
                    Notifier.this.showWiredChargingStarted(message.arg1);
                    return;
                case 7:
                    Notifier.this.screenPolicyChanging(message.arg1, message.arg2);
                    return;
                default:
                    return;
            }
        }
    }
}
