package com.android.server.display;

import android.content.Context;
import android.database.ContentObserver;
import android.hardware.display.BrightnessInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.IThermalEventListener;
import android.os.IThermalService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Temperature;
import android.provider.Settings;
import android.util.MathUtils;
import android.util.Slog;
import android.util.TimeUtils;
import android.view.SurfaceControlHdrLayerInfoListener;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.display.DisplayDeviceConfig;
import com.android.server.display.DisplayManagerService;
import com.android.server.display.HighBrightnessModeController;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Iterator;
/* loaded from: classes.dex */
public class HighBrightnessModeController {
    @VisibleForTesting
    static final float HBM_TRANSITION_POINT_INVALID = Float.POSITIVE_INFINITY;
    public float mAmbientLux;
    public float mBrightness;
    public final float mBrightnessMax;
    public final float mBrightnessMin;
    public final DisplayManagerService.Clock mClock;
    public final Context mContext;
    public int mDisplayStatsId;
    public final Handler mHandler;
    public final Runnable mHbmChangeCallback;
    public DisplayDeviceConfig.HighBrightnessModeData mHbmData;
    public int mHbmMode;
    public int mHbmStatsState;
    public HdrBrightnessDeviceConfig mHdrBrightnessCfg;
    public HdrListener mHdrListener;
    public int mHeight;
    public HighBrightnessModeMetadata mHighBrightnessModeMetadata;
    public final Injector mInjector;
    public boolean mIsAutoBrightnessEnabled;
    public boolean mIsAutoBrightnessOffByState;
    public boolean mIsBlockedByLowPowerMode;
    public boolean mIsHdrLayerPresent;
    public boolean mIsInAllowedAmbientRange;
    public boolean mIsThermalStatusWithinLimit;
    public boolean mIsTimeAvailable;
    public final Runnable mRecalcRunnable;
    public IBinder mRegisteredDisplayToken;
    public final SettingsObserver mSettingsObserver;
    public final SkinThermalStatusObserver mSkinThermalStatusObserver;
    public int mThrottlingReason;
    public float mUnthrottledBrightness;
    public int mWidth;

    /* loaded from: classes.dex */
    public interface HdrBrightnessDeviceConfig {
        float getHdrBrightnessFromSdr(float f);
    }

    public HighBrightnessModeController(Handler handler, int i, int i2, IBinder iBinder, String str, float f, float f2, DisplayDeviceConfig.HighBrightnessModeData highBrightnessModeData, HdrBrightnessDeviceConfig hdrBrightnessDeviceConfig, Runnable runnable, HighBrightnessModeMetadata highBrightnessModeMetadata, Context context) {
        this(new Injector(), handler, i, i2, iBinder, str, f, f2, highBrightnessModeData, hdrBrightnessDeviceConfig, runnable, highBrightnessModeMetadata, context);
    }

    @VisibleForTesting
    public HighBrightnessModeController(Injector injector, Handler handler, int i, int i2, IBinder iBinder, String str, float f, float f2, DisplayDeviceConfig.HighBrightnessModeData highBrightnessModeData, HdrBrightnessDeviceConfig hdrBrightnessDeviceConfig, Runnable runnable, HighBrightnessModeMetadata highBrightnessModeMetadata, Context context) {
        this.mIsInAllowedAmbientRange = false;
        this.mIsTimeAvailable = false;
        this.mIsAutoBrightnessEnabled = false;
        this.mIsAutoBrightnessOffByState = false;
        this.mThrottlingReason = 0;
        this.mHbmMode = 0;
        this.mIsHdrLayerPresent = false;
        this.mIsThermalStatusWithinLimit = true;
        this.mIsBlockedByLowPowerMode = false;
        this.mHbmStatsState = 1;
        this.mHighBrightnessModeMetadata = null;
        this.mInjector = injector;
        this.mContext = context;
        this.mClock = injector.getClock();
        this.mHandler = handler;
        this.mBrightness = f;
        this.mBrightnessMin = f;
        this.mBrightnessMax = f2;
        this.mHbmChangeCallback = runnable;
        this.mHighBrightnessModeMetadata = highBrightnessModeMetadata;
        this.mSkinThermalStatusObserver = new SkinThermalStatusObserver(injector, handler);
        this.mSettingsObserver = new SettingsObserver(handler);
        this.mRecalcRunnable = new Runnable() { // from class: com.android.server.display.HighBrightnessModeController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HighBrightnessModeController.this.recalculateTimeAllowance();
            }
        };
        this.mHdrListener = new HdrListener();
        resetHbmData(i, i2, iBinder, str, highBrightnessModeData, hdrBrightnessDeviceConfig);
    }

    public void setAutoBrightnessEnabled(int i) {
        boolean z = i == 1;
        this.mIsAutoBrightnessOffByState = i == 3;
        if (!deviceSupportsHbm() || z == this.mIsAutoBrightnessEnabled) {
            return;
        }
        this.mIsAutoBrightnessEnabled = z;
        this.mIsInAllowedAmbientRange = false;
        recalculateTimeAllowance();
    }

    public float getCurrentBrightnessMin() {
        return this.mBrightnessMin;
    }

    public float getCurrentBrightnessMax() {
        if (!deviceSupportsHbm() || isCurrentlyAllowed()) {
            return this.mBrightnessMax;
        }
        return this.mHbmData.transitionPoint;
    }

    public float getNormalBrightnessMax() {
        return deviceSupportsHbm() ? this.mHbmData.transitionPoint : this.mBrightnessMax;
    }

    public float getHdrBrightnessValue() {
        HdrBrightnessDeviceConfig hdrBrightnessDeviceConfig = this.mHdrBrightnessCfg;
        if (hdrBrightnessDeviceConfig != null) {
            float hdrBrightnessFromSdr = hdrBrightnessDeviceConfig.getHdrBrightnessFromSdr(this.mBrightness);
            if (hdrBrightnessFromSdr != -1.0f) {
                return hdrBrightnessFromSdr;
            }
        }
        return MathUtils.map(getCurrentBrightnessMin(), getCurrentBrightnessMax(), this.mBrightnessMin, this.mBrightnessMax, this.mBrightness);
    }

    public void onAmbientLuxChange(float f) {
        this.mAmbientLux = f;
        if (deviceSupportsHbm() && this.mIsAutoBrightnessEnabled) {
            boolean z = f >= this.mHbmData.minimumLux;
            if (z != this.mIsInAllowedAmbientRange) {
                this.mIsInAllowedAmbientRange = z;
                recalculateTimeAllowance();
            }
        }
    }

    public void onBrightnessChanged(float f, float f2, int i) {
        if (deviceSupportsHbm()) {
            this.mBrightness = f;
            this.mUnthrottledBrightness = f2;
            this.mThrottlingReason = i;
            long runningStartTimeMillis = this.mHighBrightnessModeMetadata.getRunningStartTimeMillis();
            boolean z = true;
            boolean z2 = runningStartTimeMillis != -1;
            if (this.mBrightness <= this.mHbmData.transitionPoint || this.mIsHdrLayerPresent) {
                z = false;
            }
            if (z2 != z) {
                long uptimeMillis = this.mClock.uptimeMillis();
                if (z) {
                    this.mHighBrightnessModeMetadata.setRunningStartTimeMillis(uptimeMillis);
                } else {
                    this.mHighBrightnessModeMetadata.addHbmEvent(new HbmEvent(runningStartTimeMillis, uptimeMillis));
                    this.mHighBrightnessModeMetadata.setRunningStartTimeMillis(-1L);
                }
            }
            recalculateTimeAllowance();
        }
    }

    public int getHighBrightnessMode() {
        return this.mHbmMode;
    }

    public float getTransitionPoint() {
        return deviceSupportsHbm() ? this.mHbmData.transitionPoint : HBM_TRANSITION_POINT_INVALID;
    }

    public void stop() {
        registerHdrListener(null);
        this.mSkinThermalStatusObserver.stopObserving();
        this.mSettingsObserver.stopObserving();
    }

    public void setHighBrightnessModeMetadata(HighBrightnessModeMetadata highBrightnessModeMetadata) {
        this.mHighBrightnessModeMetadata = highBrightnessModeMetadata;
    }

    public void resetHbmData(int i, int i2, IBinder iBinder, String str, DisplayDeviceConfig.HighBrightnessModeData highBrightnessModeData, HdrBrightnessDeviceConfig hdrBrightnessDeviceConfig) {
        this.mWidth = i;
        this.mHeight = i2;
        this.mHbmData = highBrightnessModeData;
        this.mHdrBrightnessCfg = hdrBrightnessDeviceConfig;
        this.mDisplayStatsId = str.hashCode();
        unregisterHdrListener();
        this.mSkinThermalStatusObserver.stopObserving();
        this.mSettingsObserver.stopObserving();
        if (deviceSupportsHbm()) {
            registerHdrListener(iBinder);
            recalculateTimeAllowance();
            if (this.mHbmData.thermalStatusLimit > 0) {
                this.mIsThermalStatusWithinLimit = true;
                this.mSkinThermalStatusObserver.startObserving();
            }
            if (this.mHbmData.allowInLowPowerMode) {
                return;
            }
            this.mIsBlockedByLowPowerMode = false;
            this.mSettingsObserver.startObserving();
        }
    }

    public void dump(final PrintWriter printWriter) {
        this.mHandler.runWithScissors(new Runnable() { // from class: com.android.server.display.HighBrightnessModeController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                HighBrightnessModeController.this.lambda$dump$0(printWriter);
            }
        }, 1000L);
    }

    @VisibleForTesting
    public HdrListener getHdrListener() {
        return this.mHdrListener;
    }

    /* renamed from: dumpLocal */
    public final void lambda$dump$0(PrintWriter printWriter) {
        String str;
        printWriter.println("HighBrightnessModeController:");
        printWriter.println("  mBrightness=" + this.mBrightness);
        printWriter.println("  mUnthrottledBrightness=" + this.mUnthrottledBrightness);
        printWriter.println("  mThrottlingReason=" + BrightnessInfo.briMaxReasonToString(this.mThrottlingReason));
        printWriter.println("  mCurrentMin=" + getCurrentBrightnessMin());
        printWriter.println("  mCurrentMax=" + getCurrentBrightnessMax());
        StringBuilder sb = new StringBuilder();
        sb.append("  mHbmMode=");
        sb.append(BrightnessInfo.hbmToString(this.mHbmMode));
        if (this.mHbmMode == 2) {
            str = "(" + getHdrBrightnessValue() + ")";
        } else {
            str = "";
        }
        sb.append(str);
        printWriter.println(sb.toString());
        printWriter.println("  mHbmStatsState=" + hbmStatsStateToString(this.mHbmStatsState));
        printWriter.println("  mHbmData=" + this.mHbmData);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mAmbientLux=");
        sb2.append(this.mAmbientLux);
        sb2.append(this.mIsAutoBrightnessEnabled ? "" : " (old/invalid)");
        printWriter.println(sb2.toString());
        printWriter.println("  mIsInAllowedAmbientRange=" + this.mIsInAllowedAmbientRange);
        printWriter.println("  mIsAutoBrightnessEnabled=" + this.mIsAutoBrightnessEnabled);
        printWriter.println("  mIsAutoBrightnessOffByState=" + this.mIsAutoBrightnessOffByState);
        printWriter.println("  mIsHdrLayerPresent=" + this.mIsHdrLayerPresent);
        printWriter.println("  mBrightnessMin=" + this.mBrightnessMin);
        printWriter.println("  mBrightnessMax=" + this.mBrightnessMax);
        printWriter.println("  remainingTime=" + calculateRemainingTime(this.mClock.uptimeMillis()));
        printWriter.println("  mIsTimeAvailable= " + this.mIsTimeAvailable);
        printWriter.println("  mRunningStartTimeMillis=" + TimeUtils.formatUptime(this.mHighBrightnessModeMetadata.getRunningStartTimeMillis()));
        printWriter.println("  mIsThermalStatusWithinLimit=" + this.mIsThermalStatusWithinLimit);
        printWriter.println("  mIsBlockedByLowPowerMode=" + this.mIsBlockedByLowPowerMode);
        printWriter.println("  width*height=" + this.mWidth + "*" + this.mHeight);
        printWriter.println("  mEvents=");
        long uptimeMillis = this.mClock.uptimeMillis();
        long runningStartTimeMillis = this.mHighBrightnessModeMetadata.getRunningStartTimeMillis();
        if (runningStartTimeMillis != -1) {
            uptimeMillis = dumpHbmEvent(printWriter, new HbmEvent(runningStartTimeMillis, uptimeMillis));
        }
        Iterator<HbmEvent> it = this.mHighBrightnessModeMetadata.getHbmEventQueue().iterator();
        while (it.hasNext()) {
            HbmEvent next = it.next();
            if (uptimeMillis > next.getEndTimeMillis()) {
                printWriter.println("    event: [normal brightness]: " + TimeUtils.formatDuration(uptimeMillis - next.getEndTimeMillis()));
            }
            uptimeMillis = dumpHbmEvent(printWriter, next);
        }
        this.mSkinThermalStatusObserver.dump(printWriter);
    }

    public final long dumpHbmEvent(PrintWriter printWriter, HbmEvent hbmEvent) {
        long endTimeMillis = hbmEvent.getEndTimeMillis() - hbmEvent.getStartTimeMillis();
        printWriter.println("    event: [" + TimeUtils.formatUptime(hbmEvent.getStartTimeMillis()) + ", " + TimeUtils.formatUptime(hbmEvent.getEndTimeMillis()) + "] (" + TimeUtils.formatDuration(endTimeMillis) + ")");
        return hbmEvent.getStartTimeMillis();
    }

    public final boolean isCurrentlyAllowed() {
        return !this.mIsHdrLayerPresent && this.mIsAutoBrightnessEnabled && this.mIsTimeAvailable && this.mIsInAllowedAmbientRange && this.mIsThermalStatusWithinLimit && !this.mIsBlockedByLowPowerMode;
    }

    public final boolean deviceSupportsHbm() {
        return this.mHbmData != null;
    }

    public final long calculateRemainingTime(long j) {
        long j2;
        if (deviceSupportsHbm()) {
            long runningStartTimeMillis = this.mHighBrightnessModeMetadata.getRunningStartTimeMillis();
            if (runningStartTimeMillis > 0) {
                if (runningStartTimeMillis > j) {
                    Slog.e("HighBrightnessModeController", "Start time set to the future. curr: " + j + ", start: " + runningStartTimeMillis);
                    this.mHighBrightnessModeMetadata.setRunningStartTimeMillis(j);
                    runningStartTimeMillis = j;
                }
                j2 = j - runningStartTimeMillis;
            } else {
                j2 = 0;
            }
            long j3 = j - this.mHbmData.timeWindowMillis;
            Iterator<HbmEvent> it = this.mHighBrightnessModeMetadata.getHbmEventQueue().iterator();
            while (it.hasNext()) {
                HbmEvent next = it.next();
                if (next.getEndTimeMillis() < j3) {
                    it.remove();
                } else {
                    j2 += next.getEndTimeMillis() - Math.max(next.getStartTimeMillis(), j3);
                }
            }
            return Math.max(0L, this.mHbmData.timeMaxMillis - j2);
        }
        return 0L;
    }

    public final void recalculateTimeAllowance() {
        long j;
        long uptimeMillis = this.mClock.uptimeMillis();
        long calculateRemainingTime = calculateRemainingTime(uptimeMillis);
        DisplayDeviceConfig.HighBrightnessModeData highBrightnessModeData = this.mHbmData;
        boolean z = true;
        boolean z2 = calculateRemainingTime >= highBrightnessModeData.timeMinMillis;
        boolean z3 = !z2 && calculateRemainingTime > 0 && this.mBrightness > highBrightnessModeData.transitionPoint;
        if (!z2 && !z3) {
            z = false;
        }
        this.mIsTimeAvailable = z;
        ArrayDeque<HbmEvent> hbmEventQueue = this.mHighBrightnessModeMetadata.getHbmEventQueue();
        if (this.mBrightness > this.mHbmData.transitionPoint) {
            j = uptimeMillis + calculateRemainingTime;
        } else if (this.mIsTimeAvailable || hbmEventQueue.size() <= 0) {
            j = -1;
        } else {
            long j2 = uptimeMillis - this.mHbmData.timeWindowMillis;
            j = (uptimeMillis + ((Math.max(j2, hbmEventQueue.peekLast().getStartTimeMillis()) + this.mHbmData.timeMinMillis) - j2)) - calculateRemainingTime;
        }
        if (j != -1) {
            this.mHandler.removeCallbacks(this.mRecalcRunnable);
            this.mHandler.postAtTime(this.mRecalcRunnable, j + 1);
        }
        updateHbmMode();
    }

    public final void updateHbmMode() {
        int calculateHighBrightnessMode = calculateHighBrightnessMode();
        updateHbmStats(calculateHighBrightnessMode);
        if (this.mHbmMode != calculateHighBrightnessMode) {
            this.mHbmMode = calculateHighBrightnessMode;
            this.mHbmChangeCallback.run();
        }
    }

    public final void updateHbmStats(int i) {
        int i2;
        float f = this.mHbmData.transitionPoint;
        int i3 = 3;
        if (i != 2 || getHdrBrightnessValue() <= f) {
            i2 = (i != 1 || this.mBrightness <= f) ? 1 : 3;
        } else {
            i2 = 2;
        }
        int i4 = this.mHbmStatsState;
        if (i2 == i4) {
            return;
        }
        boolean z = i4 == 3;
        boolean z2 = i2 == 3;
        if (z && !z2) {
            boolean z3 = !this.mIsThermalStatusWithinLimit;
            boolean z4 = this.mUnthrottledBrightness > f && this.mBrightness <= f && this.mThrottlingReason == 1;
            boolean z5 = this.mIsAutoBrightnessEnabled;
            if (!z5 && this.mIsAutoBrightnessOffByState) {
                i3 = 6;
            } else if (!z5) {
                i3 = 7;
            } else if (!this.mIsInAllowedAmbientRange) {
                i3 = 1;
            } else if (!this.mIsTimeAvailable) {
                i3 = 2;
            } else if (!z3 && !z4) {
                if (this.mIsHdrLayerPresent) {
                    i3 = 4;
                } else if (this.mIsBlockedByLowPowerMode) {
                    i3 = 5;
                } else if (this.mBrightness <= this.mHbmData.transitionPoint) {
                    i3 = 9;
                }
            }
            this.mInjector.reportHbmStateChange(this.mDisplayStatsId, i2, i3);
            this.mHbmStatsState = i2;
        }
        i3 = 0;
        this.mInjector.reportHbmStateChange(this.mDisplayStatsId, i2, i3);
        this.mHbmStatsState = i2;
    }

    public final String hbmStatsStateToString(int i) {
        return i != 1 ? i != 2 ? i != 3 ? String.valueOf(i) : "HBM_ON_SUNLIGHT" : "HBM_ON_HDR" : "HBM_OFF";
    }

    public final int calculateHighBrightnessMode() {
        if (deviceSupportsHbm()) {
            if (this.mIsHdrLayerPresent) {
                return 2;
            }
            return isCurrentlyAllowed() ? 1 : 0;
        }
        return 0;
    }

    public final void registerHdrListener(IBinder iBinder) {
        if (this.mRegisteredDisplayToken == iBinder) {
            return;
        }
        unregisterHdrListener();
        this.mRegisteredDisplayToken = iBinder;
        if (iBinder != null) {
            this.mHdrListener.register(iBinder);
        }
    }

    public final void unregisterHdrListener() {
        IBinder iBinder = this.mRegisteredDisplayToken;
        if (iBinder != null) {
            this.mHdrListener.unregister(iBinder);
            this.mIsHdrLayerPresent = false;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class HdrListener extends SurfaceControlHdrLayerInfoListener {
        public HdrListener() {
        }

        public void onHdrInfoChanged(IBinder iBinder, final int i, final int i2, final int i3, int i4) {
            HighBrightnessModeController.this.mHandler.post(new Runnable() { // from class: com.android.server.display.HighBrightnessModeController$HdrListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HighBrightnessModeController.HdrListener.this.lambda$onHdrInfoChanged$0(i, i2, i3);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onHdrInfoChanged$0(int i, int i2, int i3) {
            HighBrightnessModeController highBrightnessModeController = HighBrightnessModeController.this;
            highBrightnessModeController.mIsHdrLayerPresent = i > 0 && ((float) (i2 * i3)) >= ((float) (highBrightnessModeController.mWidth * HighBrightnessModeController.this.mHeight)) * HighBrightnessModeController.this.mHbmData.minimumHdrPercentOfScreen;
            HighBrightnessModeController highBrightnessModeController2 = HighBrightnessModeController.this;
            highBrightnessModeController2.onBrightnessChanged(highBrightnessModeController2.mBrightness, HighBrightnessModeController.this.mUnthrottledBrightness, HighBrightnessModeController.this.mThrottlingReason);
        }
    }

    /* loaded from: classes.dex */
    public final class SkinThermalStatusObserver extends IThermalEventListener.Stub {
        public final Handler mHandler;
        public final Injector mInjector;
        public boolean mStarted;
        public IThermalService mThermalService;

        public SkinThermalStatusObserver(Injector injector, Handler handler) {
            this.mInjector = injector;
            this.mHandler = handler;
        }

        public void notifyThrottling(final Temperature temperature) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.display.HighBrightnessModeController$SkinThermalStatusObserver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HighBrightnessModeController.SkinThermalStatusObserver.this.lambda$notifyThrottling$0(temperature);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyThrottling$0(Temperature temperature) {
            HighBrightnessModeController.this.mIsThermalStatusWithinLimit = temperature.getStatus() <= HighBrightnessModeController.this.mHbmData.thermalStatusLimit;
            HighBrightnessModeController.this.updateHbmMode();
        }

        public void startObserving() {
            if (this.mStarted) {
                return;
            }
            IThermalService thermalService = this.mInjector.getThermalService();
            this.mThermalService = thermalService;
            if (thermalService == null) {
                Slog.w("HighBrightnessModeController", "Could not observe thermal status. Service not available");
                return;
            }
            try {
                thermalService.registerThermalEventListenerWithType(this, 3);
                this.mStarted = true;
            } catch (RemoteException e) {
                Slog.e("HighBrightnessModeController", "Failed to register thermal status listener", e);
            }
        }

        public void stopObserving() {
            HighBrightnessModeController.this.mIsThermalStatusWithinLimit = true;
            if (this.mStarted) {
                try {
                    this.mThermalService.unregisterThermalEventListener(this);
                    this.mStarted = false;
                } catch (RemoteException e) {
                    Slog.e("HighBrightnessModeController", "Failed to unregister thermal status listener", e);
                }
                this.mThermalService = null;
            }
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println("  SkinThermalStatusObserver:");
            printWriter.println("    mStarted: " + this.mStarted);
            if (this.mThermalService != null) {
                printWriter.println("    ThermalService available");
            } else {
                printWriter.println("    ThermalService not available");
            }
        }
    }

    /* loaded from: classes.dex */
    public final class SettingsObserver extends ContentObserver {
        public final Uri mLowPowerModeSetting;
        public boolean mStarted;

        public SettingsObserver(Handler handler) {
            super(handler);
            this.mLowPowerModeSetting = Settings.Global.getUriFor("low_power");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            updateLowPower();
        }

        public void startObserving() {
            if (this.mStarted) {
                return;
            }
            HighBrightnessModeController.this.mContext.getContentResolver().registerContentObserver(this.mLowPowerModeSetting, false, this, -1);
            this.mStarted = true;
            updateLowPower();
        }

        public void stopObserving() {
            HighBrightnessModeController.this.mIsBlockedByLowPowerMode = false;
            if (this.mStarted) {
                HighBrightnessModeController.this.mContext.getContentResolver().unregisterContentObserver(this);
                this.mStarted = false;
            }
        }

        public final void updateLowPower() {
            boolean isLowPowerMode = isLowPowerMode();
            if (isLowPowerMode == HighBrightnessModeController.this.mIsBlockedByLowPowerMode) {
                return;
            }
            HighBrightnessModeController.this.mIsBlockedByLowPowerMode = isLowPowerMode;
            HighBrightnessModeController.this.updateHbmMode();
        }

        public final boolean isLowPowerMode() {
            return Settings.Global.getInt(HighBrightnessModeController.this.mContext.getContentResolver(), "low_power", 0) != 0;
        }
    }

    /* loaded from: classes.dex */
    public static class Injector {
        public DisplayManagerService.Clock getClock() {
            return new DisplayManagerService.Clock() { // from class: com.android.server.display.HighBrightnessModeController$Injector$$ExternalSyntheticLambda0
                @Override // com.android.server.display.DisplayManagerService.Clock
                public final long uptimeMillis() {
                    return SystemClock.uptimeMillis();
                }
            };
        }

        public IThermalService getThermalService() {
            return IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
        }

        public void reportHbmStateChange(int i, int i2, int i3) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.DISPLAY_HBM_STATE_CHANGED, i, i2, i3);
        }
    }
}
