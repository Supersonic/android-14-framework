package com.android.server.display.mode;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.BrightnessInfo;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback;
import android.net.Uri;
import android.os.Handler;
import android.os.IThermalEventListener;
import android.os.IThermalService;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.DeviceConfig;
import android.provider.DeviceConfigInterface;
import android.provider.Settings;
import android.sysprop.SurfaceFlingerProperties;
import android.util.IndentingPrintWriter;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.display.BrightnessSynchronizer;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.display.DisplayDeviceConfig;
import com.android.server.display.mode.DisplayModeDirector;
import com.android.server.display.utils.AmbientFilter;
import com.android.server.display.utils.AmbientFilterFactory;
import com.android.server.display.utils.SensorUtils;
import com.android.server.sensors.SensorManagerInternal;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public class DisplayModeDirector {
    public boolean mAlwaysRespectAppRequest;
    public final AppRequestObserver mAppRequestObserver;
    public BrightnessObserver mBrightnessObserver;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public DisplayDeviceConfig mDefaultDisplayDeviceConfig;
    public SparseArray<Display.Mode> mDefaultModeByDisplay;
    public DesiredDisplayModeSpecsListener mDesiredDisplayModeSpecsListener;
    public final DeviceConfigInterface mDeviceConfig;
    public final DeviceConfigDisplaySettings mDeviceConfigDisplaySettings;
    public final DisplayObserver mDisplayObserver;
    public final DisplayModeDirectorHandler mHandler;
    public final HbmObserver mHbmObserver;
    public final Injector mInjector;
    public final Object mLock;
    public boolean mLoggingEnabled;
    public int mModeSwitchingType;
    public final SensorObserver mSensorObserver;
    public final SettingsObserver mSettingsObserver;
    public final SkinThermalStatusObserver mSkinThermalStatusObserver;
    public SparseArray<Display.Mode[]> mSupportedModesByDisplay;
    public final boolean mSupportsFrameRateOverride;
    public final UdfpsObserver mUdfpsObserver;
    public SparseArray<SparseArray<Vote>> mVotesByDisplay;

    /* loaded from: classes.dex */
    public interface BallotBox {
        void vote(int i, int i2, Vote vote);
    }

    /* loaded from: classes.dex */
    public interface DesiredDisplayModeSpecsListener {
        void onDesiredDisplayModeSpecsChanged();
    }

    /* loaded from: classes.dex */
    public interface Injector {
        public static final Uri PEAK_REFRESH_RATE_URI = Settings.System.getUriFor("peak_refresh_rate");

        BrightnessInfo getBrightnessInfo(int i);

        DeviceConfigInterface getDeviceConfig();

        boolean getDisplayInfo(int i, DisplayInfo displayInfo);

        Display[] getDisplays();

        boolean isDozeState(Display display);

        void registerDisplayListener(DisplayManager.DisplayListener displayListener, Handler handler, long j);

        void registerPeakRefreshRateObserver(ContentResolver contentResolver, ContentObserver contentObserver);

        boolean registerThermalServiceListener(IThermalEventListener iThermalEventListener);

        boolean supportsFrameRateOverride();
    }

    public final boolean equalsWithinFloatTolerance(float f, float f2) {
        return f >= f2 - 0.01f && f <= f2 + 0.01f;
    }

    public DisplayModeDirector(Context context, Handler handler) {
        this(context, handler, new RealInjector(context));
    }

    public DisplayModeDirector(Context context, Handler handler, Injector injector) {
        this.mLock = new Object();
        this.mModeSwitchingType = 1;
        this.mContext = context;
        this.mHandler = new DisplayModeDirectorHandler(handler.getLooper());
        this.mInjector = injector;
        this.mVotesByDisplay = new SparseArray<>();
        this.mSupportedModesByDisplay = new SparseArray<>();
        this.mDefaultModeByDisplay = new SparseArray<>();
        this.mAppRequestObserver = new AppRequestObserver();
        this.mDeviceConfig = injector.getDeviceConfig();
        DeviceConfigDisplaySettings deviceConfigDisplaySettings = new DeviceConfigDisplaySettings();
        this.mDeviceConfigDisplaySettings = deviceConfigDisplaySettings;
        this.mSettingsObserver = new SettingsObserver(context, handler);
        this.mBrightnessObserver = new BrightnessObserver(context, handler, injector);
        this.mDefaultDisplayDeviceConfig = null;
        this.mUdfpsObserver = new UdfpsObserver();
        BallotBox ballotBox = new BallotBox() { // from class: com.android.server.display.mode.DisplayModeDirector$$ExternalSyntheticLambda0
            @Override // com.android.server.display.mode.DisplayModeDirector.BallotBox
            public final void vote(int i, int i2, DisplayModeDirector.Vote vote) {
                DisplayModeDirector.this.lambda$new$0(i, i2, vote);
            }
        };
        this.mDisplayObserver = new DisplayObserver(context, handler, ballotBox);
        this.mSensorObserver = new SensorObserver(context, ballotBox, injector);
        this.mSkinThermalStatusObserver = new SkinThermalStatusObserver(injector, ballotBox);
        this.mHbmObserver = new HbmObserver(injector, ballotBox, BackgroundThread.getHandler(), deviceConfigDisplaySettings);
        this.mAlwaysRespectAppRequest = false;
        this.mSupportsFrameRateOverride = injector.supportsFrameRateOverride();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, Vote vote) {
        synchronized (this.mLock) {
            updateVoteLocked(i, i2, vote);
        }
    }

    public void start(SensorManager sensorManager) {
        this.mSettingsObserver.observe();
        this.mDisplayObserver.observe();
        this.mBrightnessObserver.observe(sensorManager);
        this.mSensorObserver.observe();
        this.mHbmObserver.observe();
        this.mSkinThermalStatusObserver.observe();
        synchronized (this.mLock) {
            notifyDesiredDisplayModeSpecsChangedLocked();
        }
    }

    public void onBootCompleted() {
        this.mUdfpsObserver.observe();
    }

    public void setLoggingEnabled(boolean z) {
        if (this.mLoggingEnabled == z) {
            return;
        }
        this.mLoggingEnabled = z;
        this.mBrightnessObserver.setLoggingEnabled(z);
        this.mSkinThermalStatusObserver.setLoggingEnabled(z);
    }

    public final SparseArray<Vote> getVotesLocked(int i) {
        SparseArray<Vote> sparseArray;
        SparseArray<Vote> sparseArray2 = this.mVotesByDisplay.get(i);
        if (sparseArray2 != null) {
            sparseArray = sparseArray2.clone();
        } else {
            sparseArray = new SparseArray<>();
        }
        SparseArray<Vote> sparseArray3 = this.mVotesByDisplay.get(-1);
        if (sparseArray3 != null) {
            for (int i2 = 0; i2 < sparseArray3.size(); i2++) {
                int keyAt = sparseArray3.keyAt(i2);
                if (sparseArray.indexOfKey(keyAt) < 0) {
                    sparseArray.put(keyAt, sparseArray3.valueAt(i2));
                }
            }
        }
        return sparseArray;
    }

    /* loaded from: classes.dex */
    public static final class VoteSummary {
        public float appRequestBaseModeRefreshRate;
        public boolean disableRefreshRateSwitching;
        public int height;
        public float maxPhysicalRefreshRate;
        public float maxRenderFrameRate;
        public float minPhysicalRefreshRate;
        public float minRenderFrameRate;
        public int width;

        public VoteSummary() {
            reset();
        }

        public void reset() {
            this.minPhysicalRefreshRate = 0.0f;
            this.maxPhysicalRefreshRate = Float.POSITIVE_INFINITY;
            this.minRenderFrameRate = 0.0f;
            this.maxRenderFrameRate = Float.POSITIVE_INFINITY;
            this.width = -1;
            this.height = -1;
            this.disableRefreshRateSwitching = false;
            this.appRequestBaseModeRefreshRate = 0.0f;
        }

        public String toString() {
            return "minPhysicalRefreshRate=" + this.minPhysicalRefreshRate + ", maxPhysicalRefreshRate=" + this.maxPhysicalRefreshRate + ", minRenderFrameRate=" + this.minRenderFrameRate + ", maxRenderFrameRate=" + this.maxRenderFrameRate + ", width=" + this.width + ", height=" + this.height + ", disableRefreshRateSwitching=" + this.disableRefreshRateSwitching + ", appRequestBaseModeRefreshRate=" + this.appRequestBaseModeRefreshRate;
        }
    }

    public final void summarizeVotes(SparseArray<Vote> sparseArray, int i, int i2, VoteSummary voteSummary) {
        int i3;
        int i4;
        voteSummary.reset();
        while (i2 >= i) {
            Vote vote = sparseArray.get(i2);
            if (vote != null) {
                SurfaceControl.RefreshRateRanges refreshRateRanges = vote.refreshRateRanges;
                voteSummary.minPhysicalRefreshRate = Math.max(voteSummary.minPhysicalRefreshRate, Math.max(refreshRateRanges.physical.min, refreshRateRanges.render.min));
                voteSummary.maxPhysicalRefreshRate = Math.min(voteSummary.maxPhysicalRefreshRate, vote.refreshRateRanges.physical.max);
                SurfaceControl.RefreshRateRanges refreshRateRanges2 = vote.refreshRateRanges;
                float min = Math.min(refreshRateRanges2.render.max, refreshRateRanges2.physical.max);
                voteSummary.minRenderFrameRate = Math.max(voteSummary.minRenderFrameRate, vote.refreshRateRanges.render.min);
                voteSummary.maxRenderFrameRate = Math.min(voteSummary.maxRenderFrameRate, min);
                if (voteSummary.height == -1 && voteSummary.width == -1 && (i3 = vote.height) > 0 && (i4 = vote.width) > 0) {
                    voteSummary.width = i4;
                    voteSummary.height = i3;
                }
                if (!voteSummary.disableRefreshRateSwitching && vote.disableRefreshRateSwitching) {
                    voteSummary.disableRefreshRateSwitching = true;
                }
                if (voteSummary.appRequestBaseModeRefreshRate == 0.0f) {
                    float f = vote.appRequestBaseModeRefreshRate;
                    if (f > 0.0f) {
                        voteSummary.appRequestBaseModeRefreshRate = f;
                    }
                }
                if (this.mLoggingEnabled) {
                    Slog.w("DisplayModeDirector", "Vote summary for priority " + Vote.priorityToString(i2) + ": " + voteSummary);
                }
            }
            i2--;
        }
    }

    public final Display.Mode selectBaseMode(VoteSummary voteSummary, ArrayList<Display.Mode> arrayList, Display.Mode mode) {
        float f = voteSummary.appRequestBaseModeRefreshRate;
        if (f <= 0.0f) {
            f = mode.getRefreshRate();
        }
        Iterator<Display.Mode> it = arrayList.iterator();
        while (it.hasNext()) {
            Display.Mode next = it.next();
            if (equalsWithinFloatTolerance(f, next.getRefreshRate())) {
                return next;
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList.get(0);
    }

    public final void disableModeSwitching(VoteSummary voteSummary, float f) {
        voteSummary.maxPhysicalRefreshRate = f;
        voteSummary.minPhysicalRefreshRate = f;
        voteSummary.maxRenderFrameRate = Math.min(voteSummary.maxRenderFrameRate, f);
        if (this.mLoggingEnabled) {
            Slog.i("DisplayModeDirector", "Disabled mode switching on summary: " + voteSummary);
        }
    }

    public final void disableRenderRateSwitching(VoteSummary voteSummary, float f) {
        voteSummary.minRenderFrameRate = voteSummary.maxRenderFrameRate;
        if (!isRenderRateAchievable(f, voteSummary)) {
            voteSummary.maxRenderFrameRate = f;
            voteSummary.minRenderFrameRate = f;
        }
        if (this.mLoggingEnabled) {
            Slog.i("DisplayModeDirector", "Disabled render rate switching on summary: " + voteSummary);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:50:0x0256 A[Catch: all -> 0x02c4, TryCatch #0 {, blocks: (B:4:0x0003, B:8:0x001d, B:13:0x0039, B:15:0x0041, B:18:0x0051, B:20:0x005b, B:22:0x005f, B:23:0x00d2, B:25:0x00d6, B:26:0x0140, B:17:0x0045, B:27:0x0144, B:29:0x0148, B:30:0x0183, B:32:0x01b7, B:33:0x01f2, B:35:0x01f8, B:36:0x0239, B:38:0x023b, B:46:0x0249, B:53:0x0263, B:56:0x0269, B:57:0x02a0, B:48:0x024d, B:50:0x0256, B:52:0x0260, B:59:0x02a2, B:60:0x02c2), top: B:65:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0268  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public DesiredDisplayModeSpecs getDesiredDisplayModeSpecs(int i) {
        int i2;
        int i3;
        boolean z;
        synchronized (this.mLock) {
            SparseArray<Vote> votesLocked = getVotesLocked(i);
            Display.Mode[] modeArr = this.mSupportedModesByDisplay.get(i);
            Display.Mode mode = this.mDefaultModeByDisplay.get(i);
            if (modeArr != null && mode != null) {
                ArrayList<Display.Mode> arrayList = new ArrayList<>();
                arrayList.add(mode);
                VoteSummary voteSummary = new VoteSummary();
                if (this.mAlwaysRespectAppRequest) {
                    i2 = 6;
                    i3 = 4;
                } else {
                    i2 = 14;
                    i3 = 0;
                }
                while (true) {
                    if (i3 > i2) {
                        break;
                    }
                    summarizeVotes(votesLocked, i3, i2, voteSummary);
                    if (voteSummary.height == -1 || voteSummary.width == -1) {
                        voteSummary.width = mode.getPhysicalWidth();
                        voteSummary.height = mode.getPhysicalHeight();
                    }
                    arrayList = filterModes(modeArr, voteSummary);
                    if (!arrayList.isEmpty()) {
                        if (this.mLoggingEnabled) {
                            Slog.w("DisplayModeDirector", "Found available modes=" + arrayList + " with lowest priority considered " + Vote.priorityToString(i3) + " and constraints: width=" + voteSummary.width + ", height=" + voteSummary.height + ", minPhysicalRefreshRate=" + voteSummary.minPhysicalRefreshRate + ", maxPhysicalRefreshRate=" + voteSummary.maxPhysicalRefreshRate + ", minRenderFrameRate=" + voteSummary.minRenderFrameRate + ", maxRenderFrameRate=" + voteSummary.maxRenderFrameRate + ", disableRefreshRateSwitching=" + voteSummary.disableRefreshRateSwitching + ", appRequestBaseModeRefreshRate=" + voteSummary.appRequestBaseModeRefreshRate);
                        }
                    } else {
                        if (this.mLoggingEnabled) {
                            Slog.w("DisplayModeDirector", "Couldn't find available modes with lowest priority set to " + Vote.priorityToString(i3) + " and with the following constraints: width=" + voteSummary.width + ", height=" + voteSummary.height + ", minPhysicalRefreshRate=" + voteSummary.minPhysicalRefreshRate + ", maxPhysicalRefreshRate=" + voteSummary.maxPhysicalRefreshRate + ", minRenderFrameRate=" + voteSummary.minRenderFrameRate + ", maxRenderFrameRate=" + voteSummary.maxRenderFrameRate + ", disableRefreshRateSwitching=" + voteSummary.disableRefreshRateSwitching + ", appRequestBaseModeRefreshRate=" + voteSummary.appRequestBaseModeRefreshRate);
                        }
                        i3++;
                    }
                }
                if (this.mLoggingEnabled) {
                    Slog.i("DisplayModeDirector", "Primary physical range: [" + voteSummary.minPhysicalRefreshRate + " " + voteSummary.maxPhysicalRefreshRate + "] render frame rate range: [" + voteSummary.minRenderFrameRate + " " + voteSummary.maxRenderFrameRate + "]");
                }
                VoteSummary voteSummary2 = new VoteSummary();
                summarizeVotes(votesLocked, 4, 14, voteSummary2);
                voteSummary2.minPhysicalRefreshRate = Math.min(voteSummary2.minPhysicalRefreshRate, voteSummary.minPhysicalRefreshRate);
                voteSummary2.maxPhysicalRefreshRate = Math.max(voteSummary2.maxPhysicalRefreshRate, voteSummary.maxPhysicalRefreshRate);
                voteSummary2.minRenderFrameRate = Math.min(voteSummary2.minRenderFrameRate, voteSummary.minRenderFrameRate);
                voteSummary2.maxRenderFrameRate = Math.max(voteSummary2.maxRenderFrameRate, voteSummary.maxRenderFrameRate);
                if (this.mLoggingEnabled) {
                    Slog.i("DisplayModeDirector", "App request range: [" + voteSummary2.minPhysicalRefreshRate + " " + voteSummary2.maxPhysicalRefreshRate + "] Frame rate range: [" + voteSummary2.minRenderFrameRate + " " + voteSummary2.maxRenderFrameRate + "]");
                }
                Display.Mode selectBaseMode = selectBaseMode(voteSummary, arrayList, mode);
                if (selectBaseMode == null) {
                    Slog.w("DisplayModeDirector", "Can't find a set of allowed modes which satisfies the votes. Falling back to the default mode. Display = " + i + ", votes = " + votesLocked + ", supported modes = " + Arrays.toString(modeArr));
                    float refreshRate = mode.getRefreshRate();
                    SurfaceControl.RefreshRateRange refreshRateRange = new SurfaceControl.RefreshRateRange(refreshRate, refreshRate);
                    SurfaceControl.RefreshRateRanges refreshRateRanges = new SurfaceControl.RefreshRateRanges(refreshRateRange, refreshRateRange);
                    return new DesiredDisplayModeSpecs(mode.getModeId(), false, refreshRateRanges, refreshRateRanges);
                }
                int i4 = this.mModeSwitchingType;
                if (i4 != 0 && i4 != 3) {
                    z = false;
                    if (!z || voteSummary.disableRefreshRateSwitching) {
                        float refreshRate2 = selectBaseMode.getRefreshRate();
                        disableModeSwitching(voteSummary, refreshRate2);
                        if (z) {
                            disableModeSwitching(voteSummary2, refreshRate2);
                            disableRenderRateSwitching(voteSummary, refreshRate2);
                            if (this.mModeSwitchingType == 0) {
                                disableRenderRateSwitching(voteSummary2, refreshRate2);
                            }
                        }
                    }
                    return new DesiredDisplayModeSpecs(selectBaseMode.getModeId(), this.mModeSwitchingType == 2, new SurfaceControl.RefreshRateRanges(new SurfaceControl.RefreshRateRange(voteSummary.minPhysicalRefreshRate, voteSummary.maxPhysicalRefreshRate), new SurfaceControl.RefreshRateRange(voteSummary.minRenderFrameRate, voteSummary.maxRenderFrameRate)), new SurfaceControl.RefreshRateRanges(new SurfaceControl.RefreshRateRange(voteSummary2.minPhysicalRefreshRate, voteSummary2.maxPhysicalRefreshRate), new SurfaceControl.RefreshRateRange(voteSummary2.minRenderFrameRate, voteSummary2.maxRenderFrameRate)));
                }
                z = true;
                if (!z) {
                }
                float refreshRate22 = selectBaseMode.getRefreshRate();
                disableModeSwitching(voteSummary, refreshRate22);
                if (z) {
                }
                return new DesiredDisplayModeSpecs(selectBaseMode.getModeId(), this.mModeSwitchingType == 2, new SurfaceControl.RefreshRateRanges(new SurfaceControl.RefreshRateRange(voteSummary.minPhysicalRefreshRate, voteSummary.maxPhysicalRefreshRate), new SurfaceControl.RefreshRateRange(voteSummary.minRenderFrameRate, voteSummary.maxRenderFrameRate)), new SurfaceControl.RefreshRateRanges(new SurfaceControl.RefreshRateRange(voteSummary2.minPhysicalRefreshRate, voteSummary2.maxPhysicalRefreshRate), new SurfaceControl.RefreshRateRange(voteSummary2.minRenderFrameRate, voteSummary2.maxRenderFrameRate)));
            }
            Slog.e("DisplayModeDirector", "Asked about unknown display, returning empty display mode specs!(id=" + i + ")");
            return new DesiredDisplayModeSpecs();
        }
    }

    public final boolean isRenderRateAchievable(float f, VoteSummary voteSummary) {
        return f / ((float) ((int) Math.ceil((double) ((f / voteSummary.maxRenderFrameRate) - 0.01f)))) >= voteSummary.minRenderFrameRate - 0.01f;
    }

    public final ArrayList<Display.Mode> filterModes(Display.Mode[] modeArr, VoteSummary voteSummary) {
        if (voteSummary.minRenderFrameRate > voteSummary.maxRenderFrameRate + 0.01f) {
            if (this.mLoggingEnabled) {
                Slog.w("DisplayModeDirector", "Vote summary resulted in empty set (invalid frame rate range): minRenderFrameRate=" + voteSummary.minRenderFrameRate + ", maxRenderFrameRate=" + voteSummary.maxRenderFrameRate);
            }
            return new ArrayList<>();
        }
        ArrayList<Display.Mode> arrayList = new ArrayList<>();
        boolean z = voteSummary.appRequestBaseModeRefreshRate > 0.0f;
        for (Display.Mode mode : modeArr) {
            if (mode.getPhysicalWidth() != voteSummary.width || mode.getPhysicalHeight() != voteSummary.height) {
                if (this.mLoggingEnabled) {
                    Slog.w("DisplayModeDirector", "Discarding mode " + mode.getModeId() + ", wrong size: desiredWidth=" + voteSummary.width + ": desiredHeight=" + voteSummary.height + ": actualWidth=" + mode.getPhysicalWidth() + ": actualHeight=" + mode.getPhysicalHeight());
                }
            } else {
                float refreshRate = mode.getRefreshRate();
                if (refreshRate < voteSummary.minPhysicalRefreshRate - 0.01f || refreshRate > voteSummary.maxPhysicalRefreshRate + 0.01f) {
                    if (this.mLoggingEnabled) {
                        Slog.w("DisplayModeDirector", "Discarding mode " + mode.getModeId() + ", outside refresh rate bounds: minPhysicalRefreshRate=" + voteSummary.minPhysicalRefreshRate + ", maxPhysicalRefreshRate=" + voteSummary.maxPhysicalRefreshRate + ", modeRefreshRate=" + refreshRate);
                    }
                } else if (!this.mSupportsFrameRateOverride && (refreshRate < voteSummary.minRenderFrameRate - 0.01f || refreshRate > voteSummary.maxRenderFrameRate + 0.01f)) {
                    if (this.mLoggingEnabled) {
                        Slog.w("DisplayModeDirector", "Discarding mode " + mode.getModeId() + ", outside render rate bounds: minPhysicalRefreshRate=" + voteSummary.minPhysicalRefreshRate + ", maxPhysicalRefreshRate=" + voteSummary.maxPhysicalRefreshRate + ", modeRefreshRate=" + refreshRate);
                    }
                } else if (!isRenderRateAchievable(refreshRate, voteSummary)) {
                    if (this.mLoggingEnabled) {
                        Slog.w("DisplayModeDirector", "Discarding mode " + mode.getModeId() + ", outside frame rate bounds: minRenderFrameRate=" + voteSummary.minRenderFrameRate + ", maxRenderFrameRate=" + voteSummary.maxRenderFrameRate + ", modePhysicalRefreshRate=" + refreshRate);
                    }
                } else {
                    arrayList.add(mode);
                    if (equalsWithinFloatTolerance(mode.getRefreshRate(), voteSummary.appRequestBaseModeRefreshRate)) {
                        z = false;
                    }
                }
            }
        }
        return z ? new ArrayList<>() : arrayList;
    }

    public AppRequestObserver getAppRequestObserver() {
        return this.mAppRequestObserver;
    }

    public void setDesiredDisplayModeSpecsListener(DesiredDisplayModeSpecsListener desiredDisplayModeSpecsListener) {
        synchronized (this.mLock) {
            this.mDesiredDisplayModeSpecsListener = desiredDisplayModeSpecsListener;
        }
    }

    public void defaultDisplayDeviceUpdated(DisplayDeviceConfig displayDeviceConfig) {
        synchronized (this.mLock) {
            this.mDefaultDisplayDeviceConfig = displayDeviceConfig;
            this.mSettingsObserver.setRefreshRates(displayDeviceConfig, true);
            this.mBrightnessObserver.updateBlockingZoneThresholds(displayDeviceConfig, true);
            this.mBrightnessObserver.reloadLightSensor(displayDeviceConfig);
            this.mHbmObserver.setupHdrRefreshRates(displayDeviceConfig);
        }
    }

    public void setShouldAlwaysRespectAppRequestedMode(boolean z) {
        synchronized (this.mLock) {
            if (this.mAlwaysRespectAppRequest != z) {
                this.mAlwaysRespectAppRequest = z;
                notifyDesiredDisplayModeSpecsChangedLocked();
            }
        }
    }

    public boolean shouldAlwaysRespectAppRequestedMode() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mAlwaysRespectAppRequest;
        }
        return z;
    }

    public void setModeSwitchingType(int i) {
        synchronized (this.mLock) {
            if (i != this.mModeSwitchingType) {
                this.mModeSwitchingType = i;
                notifyDesiredDisplayModeSpecsChangedLocked();
            }
        }
    }

    public int getModeSwitchingType() {
        int i;
        synchronized (this.mLock) {
            i = this.mModeSwitchingType;
        }
        return i;
    }

    @VisibleForTesting
    public Vote getVote(int i, int i2) {
        Vote vote;
        synchronized (this.mLock) {
            vote = getVotesLocked(i).get(i2);
        }
        return vote;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("DisplayModeDirector");
        synchronized (this.mLock) {
            printWriter.println("  mSupportedModesByDisplay:");
            for (int i = 0; i < this.mSupportedModesByDisplay.size(); i++) {
                printWriter.println("    " + this.mSupportedModesByDisplay.keyAt(i) + " -> " + Arrays.toString(this.mSupportedModesByDisplay.valueAt(i)));
            }
            printWriter.println("  mDefaultModeByDisplay:");
            for (int i2 = 0; i2 < this.mDefaultModeByDisplay.size(); i2++) {
                printWriter.println("    " + this.mDefaultModeByDisplay.keyAt(i2) + " -> " + this.mDefaultModeByDisplay.valueAt(i2));
            }
            printWriter.println("  mVotesByDisplay:");
            for (int i3 = 0; i3 < this.mVotesByDisplay.size(); i3++) {
                printWriter.println("    " + this.mVotesByDisplay.keyAt(i3) + XmlUtils.STRING_ARRAY_SEPARATOR);
                SparseArray<Vote> valueAt = this.mVotesByDisplay.valueAt(i3);
                for (int i4 = 14; i4 >= 0; i4--) {
                    Vote vote = valueAt.get(i4);
                    if (vote != null) {
                        printWriter.println("      " + Vote.priorityToString(i4) + " -> " + vote);
                    }
                }
            }
            printWriter.println("  mModeSwitchingType: " + switchingTypeToString(this.mModeSwitchingType));
            printWriter.println("  mAlwaysRespectAppRequest: " + this.mAlwaysRespectAppRequest);
            this.mSettingsObserver.dumpLocked(printWriter);
            this.mAppRequestObserver.dumpLocked(printWriter);
            this.mBrightnessObserver.dumpLocked(printWriter);
            this.mUdfpsObserver.dumpLocked(printWriter);
            this.mHbmObserver.dumpLocked(printWriter);
            this.mSkinThermalStatusObserver.dumpLocked(printWriter);
        }
        this.mSensorObserver.dump(printWriter);
    }

    public final void updateVoteLocked(int i, Vote vote) {
        updateVoteLocked(-1, i, vote);
    }

    public final void updateVoteLocked(int i, int i2, Vote vote) {
        if (this.mLoggingEnabled) {
            Slog.i("DisplayModeDirector", "updateVoteLocked(displayId=" + i + ", priority=" + Vote.priorityToString(i2) + ", vote=" + vote + ")");
        }
        if (i2 < 0 || i2 > 14) {
            Slog.w("DisplayModeDirector", "Received a vote with an invalid priority, ignoring: priority=" + Vote.priorityToString(i2) + ", vote=" + vote, new Throwable());
            return;
        }
        SparseArray<Vote> orCreateVotesByDisplay = getOrCreateVotesByDisplay(i);
        if (vote != null) {
            orCreateVotesByDisplay.put(i2, vote);
        } else {
            orCreateVotesByDisplay.remove(i2);
        }
        if (orCreateVotesByDisplay.size() == 0) {
            if (this.mLoggingEnabled) {
                Slog.i("DisplayModeDirector", "No votes left for display " + i + ", removing.");
            }
            this.mVotesByDisplay.remove(i);
        }
        notifyDesiredDisplayModeSpecsChangedLocked();
    }

    @GuardedBy({"mLock"})
    public final float getMaxRefreshRateLocked(int i) {
        Display.Mode[] modeArr;
        float f = 0.0f;
        for (Display.Mode mode : this.mSupportedModesByDisplay.get(i)) {
            if (mode.getRefreshRate() > f) {
                f = mode.getRefreshRate();
            }
        }
        return f;
    }

    public final void notifyDesiredDisplayModeSpecsChangedLocked() {
        if (this.mDesiredDisplayModeSpecsListener == null || this.mHandler.hasMessages(1)) {
            return;
        }
        this.mHandler.obtainMessage(1, this.mDesiredDisplayModeSpecsListener).sendToTarget();
    }

    public final SparseArray<Vote> getOrCreateVotesByDisplay(int i) {
        if (this.mVotesByDisplay.indexOfKey(i) >= 0) {
            return this.mVotesByDisplay.get(i);
        }
        SparseArray<Vote> sparseArray = new SparseArray<>();
        this.mVotesByDisplay.put(i, sparseArray);
        return sparseArray;
    }

    public static String switchingTypeToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return "Unknown SwitchingType " + i;
                    }
                    return "SWITCHING_TYPE_RENDER_FRAME_RATE_ONLY";
                }
                return "SWITCHING_TYPE_ACROSS_AND_WITHIN_GROUPS";
            }
            return "SWITCHING_TYPE_WITHIN_GROUPS";
        }
        return "SWITCHING_TYPE_NONE";
    }

    @VisibleForTesting
    public void injectSupportedModesByDisplay(SparseArray<Display.Mode[]> sparseArray) {
        this.mSupportedModesByDisplay = sparseArray;
    }

    @VisibleForTesting
    public void injectDefaultModeByDisplay(SparseArray<Display.Mode> sparseArray) {
        this.mDefaultModeByDisplay = sparseArray;
    }

    @VisibleForTesting
    public void injectVotesByDisplay(SparseArray<SparseArray<Vote>> sparseArray) {
        this.mVotesByDisplay = sparseArray;
    }

    @VisibleForTesting
    public void injectBrightnessObserver(BrightnessObserver brightnessObserver) {
        this.mBrightnessObserver = brightnessObserver;
    }

    @VisibleForTesting
    public BrightnessObserver getBrightnessObserver() {
        return this.mBrightnessObserver;
    }

    @VisibleForTesting
    public SettingsObserver getSettingsObserver() {
        return this.mSettingsObserver;
    }

    @VisibleForTesting
    public UdfpsObserver getUdpfsObserver() {
        return this.mUdfpsObserver;
    }

    @VisibleForTesting
    public HbmObserver getHbmObserver() {
        return this.mHbmObserver;
    }

    @VisibleForTesting
    public DesiredDisplayModeSpecs getDesiredDisplayModeSpecsWithInjectedFpsSettings(float f, float f2, float f3) {
        DesiredDisplayModeSpecs desiredDisplayModeSpecs;
        synchronized (this.mLock) {
            this.mSettingsObserver.updateRefreshRateSettingLocked(f, f2, f3);
            desiredDisplayModeSpecs = getDesiredDisplayModeSpecs(0);
        }
        return desiredDisplayModeSpecs;
    }

    /* loaded from: classes.dex */
    public final class DisplayModeDirectorHandler extends Handler {
        public DisplayModeDirectorHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    ((DesiredDisplayModeSpecsListener) message.obj).onDesiredDisplayModeSpecsChanged();
                    return;
                case 2:
                    Pair pair = (Pair) message.obj;
                    DisplayModeDirector.this.mBrightnessObserver.onDeviceConfigLowBrightnessThresholdsChanged((int[]) pair.first, (int[]) pair.second);
                    return;
                case 3:
                    DisplayModeDirector.this.mSettingsObserver.onDeviceConfigDefaultPeakRefreshRateChanged((Float) message.obj);
                    return;
                case 4:
                    DisplayModeDirector.this.mBrightnessObserver.onDeviceConfigRefreshRateInLowZoneChanged(message.arg1);
                    return;
                case 5:
                    DisplayModeDirector.this.mBrightnessObserver.onDeviceConfigRefreshRateInHighZoneChanged(message.arg1);
                    return;
                case 6:
                    Pair pair2 = (Pair) message.obj;
                    DisplayModeDirector.this.mBrightnessObserver.onDeviceConfigHighBrightnessThresholdsChanged((int[]) pair2.first, (int[]) pair2.second);
                    return;
                case 7:
                    DisplayModeDirector.this.mHbmObserver.onDeviceConfigRefreshRateInHbmSunlightChanged(message.arg1);
                    return;
                case 8:
                    DisplayModeDirector.this.mHbmObserver.onDeviceConfigRefreshRateInHbmHdrChanged(message.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class DesiredDisplayModeSpecs {
        public boolean allowGroupSwitching;
        public final SurfaceControl.RefreshRateRanges appRequest;
        public int baseModeId;
        public final SurfaceControl.RefreshRateRanges primary;

        public DesiredDisplayModeSpecs() {
            this.primary = new SurfaceControl.RefreshRateRanges();
            this.appRequest = new SurfaceControl.RefreshRateRanges();
        }

        public DesiredDisplayModeSpecs(int i, boolean z, SurfaceControl.RefreshRateRanges refreshRateRanges, SurfaceControl.RefreshRateRanges refreshRateRanges2) {
            this.baseModeId = i;
            this.allowGroupSwitching = z;
            this.primary = refreshRateRanges;
            this.appRequest = refreshRateRanges2;
        }

        public String toString() {
            return String.format("baseModeId=%d allowGroupSwitching=%b primary=%s appRequest=%s", Integer.valueOf(this.baseModeId), Boolean.valueOf(this.allowGroupSwitching), this.primary.toString(), this.appRequest.toString());
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof DesiredDisplayModeSpecs) {
                DesiredDisplayModeSpecs desiredDisplayModeSpecs = (DesiredDisplayModeSpecs) obj;
                return this.baseModeId == desiredDisplayModeSpecs.baseModeId && this.allowGroupSwitching == desiredDisplayModeSpecs.allowGroupSwitching && this.primary.equals(desiredDisplayModeSpecs.primary) && this.appRequest.equals(desiredDisplayModeSpecs.appRequest);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.baseModeId), Boolean.valueOf(this.allowGroupSwitching), this.primary, this.appRequest);
        }

        public void copyFrom(DesiredDisplayModeSpecs desiredDisplayModeSpecs) {
            this.baseModeId = desiredDisplayModeSpecs.baseModeId;
            this.allowGroupSwitching = desiredDisplayModeSpecs.allowGroupSwitching;
            SurfaceControl.RefreshRateRanges refreshRateRanges = this.primary;
            SurfaceControl.RefreshRateRange refreshRateRange = refreshRateRanges.physical;
            SurfaceControl.RefreshRateRanges refreshRateRanges2 = desiredDisplayModeSpecs.primary;
            SurfaceControl.RefreshRateRange refreshRateRange2 = refreshRateRanges2.physical;
            refreshRateRange.min = refreshRateRange2.min;
            refreshRateRange.max = refreshRateRange2.max;
            SurfaceControl.RefreshRateRange refreshRateRange3 = refreshRateRanges.render;
            SurfaceControl.RefreshRateRange refreshRateRange4 = refreshRateRanges2.render;
            refreshRateRange3.min = refreshRateRange4.min;
            refreshRateRange3.max = refreshRateRange4.max;
            SurfaceControl.RefreshRateRanges refreshRateRanges3 = this.appRequest;
            SurfaceControl.RefreshRateRange refreshRateRange5 = refreshRateRanges3.physical;
            SurfaceControl.RefreshRateRanges refreshRateRanges4 = desiredDisplayModeSpecs.appRequest;
            SurfaceControl.RefreshRateRange refreshRateRange6 = refreshRateRanges4.physical;
            refreshRateRange5.min = refreshRateRange6.min;
            refreshRateRange5.max = refreshRateRange6.max;
            SurfaceControl.RefreshRateRange refreshRateRange7 = refreshRateRanges3.render;
            SurfaceControl.RefreshRateRange refreshRateRange8 = refreshRateRanges4.render;
            refreshRateRange7.min = refreshRateRange8.min;
            refreshRateRange7.max = refreshRateRange8.max;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static final class Vote {
        public final float appRequestBaseModeRefreshRate;
        public final boolean disableRefreshRateSwitching;
        public final int height;
        public final SurfaceControl.RefreshRateRanges refreshRateRanges;
        public final int width;

        public static Vote forPhysicalRefreshRates(float f, float f2) {
            return new Vote(-1, -1, f, f2, 0.0f, Float.POSITIVE_INFINITY, f == f2, 0.0f);
        }

        public static Vote forRenderFrameRates(float f, float f2) {
            return new Vote(-1, -1, 0.0f, Float.POSITIVE_INFINITY, f, f2, false, 0.0f);
        }

        public static Vote forSize(int i, int i2) {
            return new Vote(i, i2, 0.0f, Float.POSITIVE_INFINITY, 0.0f, Float.POSITIVE_INFINITY, false, 0.0f);
        }

        public static Vote forDisableRefreshRateSwitching() {
            return new Vote(-1, -1, 0.0f, Float.POSITIVE_INFINITY, 0.0f, Float.POSITIVE_INFINITY, true, 0.0f);
        }

        public static Vote forBaseModeRefreshRate(float f) {
            return new Vote(-1, -1, 0.0f, Float.POSITIVE_INFINITY, 0.0f, Float.POSITIVE_INFINITY, false, f);
        }

        public Vote(int i, int i2, float f, float f2, float f3, float f4, boolean z, float f5) {
            this.width = i;
            this.height = i2;
            this.refreshRateRanges = new SurfaceControl.RefreshRateRanges(new SurfaceControl.RefreshRateRange(f, f2), new SurfaceControl.RefreshRateRange(f3, f4));
            this.disableRefreshRateSwitching = z;
            this.appRequestBaseModeRefreshRate = f5;
        }

        public static String priorityToString(int i) {
            switch (i) {
                case 0:
                    return "PRIORITY_DEFAULT_REFRESH_RATE";
                case 1:
                    return "PRIORITY_FLICKER_REFRESH_RATE";
                case 2:
                    return "PRIORITY_HIGH_BRIGHTNESS_MODE";
                case 3:
                    return "PRIORITY_USER_SETTING_MIN_RENDER_FRAME_RATE";
                case 4:
                    return "PRIORITY_APP_REQUEST_RENDER_FRAME_RATE_RANGE";
                case 5:
                    return "PRIORITY_APP_REQUEST_BASE_MODE_REFRESH_RATE";
                case 6:
                    return "PRIORITY_APP_REQUEST_SIZE";
                case 7:
                    return "PRIORITY_USER_SETTING_PEAK_RENDER_FRAME_RATE";
                case 8:
                    return "PRIORITY_AUTH_OPTIMIZER_RENDER_FRAME_RATE";
                case 9:
                    return "PRIORITY_LAYOUT_LIMITED_FRAME_RATE";
                case 10:
                    return "PRIORITY_LOW_POWER_MODE";
                case 11:
                    return "PRIORITY_FLICKER_REFRESH_RATE_SWITCH";
                case 12:
                    return "PRIORITY_SKIN_TEMPERATURE";
                case 13:
                    return "PRIORITY_PROXIMITY";
                case 14:
                    return "PRIORITY_UDFPS";
                default:
                    return Integer.toString(i);
            }
        }

        public String toString() {
            return "Vote{width=" + this.width + ", height=" + this.height + ", refreshRateRanges=" + this.refreshRateRanges + ", disableRefreshRateSwitching=" + this.disableRefreshRateSwitching + ", appRequestBaseModeRefreshRate=" + this.appRequestBaseModeRefreshRate + "}";
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public final class SettingsObserver extends ContentObserver {
        public final Context mContext;
        public float mDefaultPeakRefreshRate;
        public float mDefaultRefreshRate;
        public final Uri mLowPowerModeSetting;
        public final Uri mMatchContentFrameRateSetting;
        public final Uri mMinRefreshRateSetting;
        public final Uri mPeakRefreshRateSetting;

        public SettingsObserver(Context context, Handler handler) {
            super(handler);
            this.mPeakRefreshRateSetting = Settings.System.getUriFor("peak_refresh_rate");
            this.mMinRefreshRateSetting = Settings.System.getUriFor("min_refresh_rate");
            this.mLowPowerModeSetting = Settings.Global.getUriFor("low_power");
            this.mMatchContentFrameRateSetting = Settings.Secure.getUriFor("match_content_frame_rate");
            this.mContext = context;
            setRefreshRates(null, false);
        }

        public void setRefreshRates(DisplayDeviceConfig displayDeviceConfig, boolean z) {
            int defaultRefreshRate;
            setDefaultPeakRefreshRate(displayDeviceConfig, z);
            if (displayDeviceConfig == null) {
                defaultRefreshRate = this.mContext.getResources().getInteger(17694802);
            } else {
                defaultRefreshRate = displayDeviceConfig.getDefaultRefreshRate();
            }
            this.mDefaultRefreshRate = defaultRefreshRate;
        }

        public void observe() {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            DisplayModeDirector.this.mInjector.registerPeakRefreshRateObserver(contentResolver, this);
            contentResolver.registerContentObserver(this.mMinRefreshRateSetting, false, this, 0);
            contentResolver.registerContentObserver(this.mLowPowerModeSetting, false, this, 0);
            contentResolver.registerContentObserver(this.mMatchContentFrameRateSetting, false, this);
            Float defaultPeakRefreshRate = DisplayModeDirector.this.mDeviceConfigDisplaySettings.getDefaultPeakRefreshRate();
            if (defaultPeakRefreshRate != null) {
                this.mDefaultPeakRefreshRate = defaultPeakRefreshRate.floatValue();
            }
            synchronized (DisplayModeDirector.this.mLock) {
                updateRefreshRateSettingLocked();
                updateLowPowerModeSettingLocked();
                updateModeSwitchingTypeSettingLocked();
            }
        }

        public void onDeviceConfigDefaultPeakRefreshRateChanged(Float f) {
            if (f == null) {
                f = Float.valueOf(this.mContext.getResources().getInteger(17694800));
            }
            if (this.mDefaultPeakRefreshRate != f.floatValue()) {
                synchronized (DisplayModeDirector.this.mLock) {
                    this.mDefaultPeakRefreshRate = f.floatValue();
                    updateRefreshRateSettingLocked();
                }
            }
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            synchronized (DisplayModeDirector.this.mLock) {
                if (!this.mPeakRefreshRateSetting.equals(uri) && !this.mMinRefreshRateSetting.equals(uri)) {
                    if (this.mLowPowerModeSetting.equals(uri)) {
                        updateLowPowerModeSettingLocked();
                    } else if (this.mMatchContentFrameRateSetting.equals(uri)) {
                        updateModeSwitchingTypeSettingLocked();
                    }
                }
                updateRefreshRateSettingLocked();
            }
        }

        @VisibleForTesting
        public float getDefaultRefreshRate() {
            return this.mDefaultRefreshRate;
        }

        @VisibleForTesting
        public float getDefaultPeakRefreshRate() {
            return this.mDefaultPeakRefreshRate;
        }

        /* JADX WARN: Removed duplicated region for block: B:7:0x0010  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final void setDefaultPeakRefreshRate(DisplayDeviceConfig displayDeviceConfig, boolean z) {
            Float defaultPeakRefreshRate;
            int defaultPeakRefreshRate2;
            if (z) {
                try {
                    defaultPeakRefreshRate = DisplayModeDirector.this.mDeviceConfigDisplaySettings.getDefaultPeakRefreshRate();
                } catch (Exception unused) {
                }
                if (defaultPeakRefreshRate == null) {
                    if (displayDeviceConfig == null) {
                        defaultPeakRefreshRate2 = this.mContext.getResources().getInteger(17694800);
                    } else {
                        defaultPeakRefreshRate2 = displayDeviceConfig.getDefaultPeakRefreshRate();
                    }
                    defaultPeakRefreshRate = Float.valueOf(defaultPeakRefreshRate2);
                }
                this.mDefaultPeakRefreshRate = defaultPeakRefreshRate.floatValue();
            }
            defaultPeakRefreshRate = null;
            if (defaultPeakRefreshRate == null) {
            }
            this.mDefaultPeakRefreshRate = defaultPeakRefreshRate.floatValue();
        }

        public final void updateLowPowerModeSettingLocked() {
            boolean z = Settings.Global.getInt(this.mContext.getContentResolver(), "low_power", 0) != 0;
            DisplayModeDirector.this.updateVoteLocked(10, z ? Vote.forRenderFrameRates(0.0f, 60.0f) : null);
            DisplayModeDirector.this.mBrightnessObserver.onLowPowerModeEnabledLocked(z);
        }

        public final void updateRefreshRateSettingLocked() {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            updateRefreshRateSettingLocked(Settings.System.getFloatForUser(contentResolver, "min_refresh_rate", 0.0f, contentResolver.getUserId()), Settings.System.getFloatForUser(contentResolver, "peak_refresh_rate", this.mDefaultPeakRefreshRate, contentResolver.getUserId()), this.mDefaultRefreshRate);
        }

        public final void updateRefreshRateSettingLocked(float f, float f2, float f3) {
            int i = (f2 > 0.0f ? 1 : (f2 == 0.0f ? 0 : -1));
            DisplayModeDirector.this.updateVoteLocked(7, i == 0 ? null : Vote.forRenderFrameRates(0.0f, Math.max(f, f2)));
            DisplayModeDirector.this.updateVoteLocked(3, Vote.forRenderFrameRates(f, Float.POSITIVE_INFINITY));
            int i2 = (f3 > 0.0f ? 1 : (f3 == 0.0f ? 0 : -1));
            DisplayModeDirector.this.updateVoteLocked(0, i2 != 0 ? Vote.forRenderFrameRates(0.0f, f3) : null);
            if (i == 0 && i2 == 0) {
                Slog.e("DisplayModeDirector", "Default and peak refresh rates are both 0. One of them should be set to a valid value.");
                f2 = f;
            } else if (i == 0) {
                f2 = f3;
            } else if (i2 != 0) {
                f2 = Math.min(f3, f2);
            }
            DisplayModeDirector.this.mBrightnessObserver.onRefreshRateSettingChangedLocked(f, f2);
        }

        public final void updateModeSwitchingTypeSettingLocked() {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            int intForUser = Settings.Secure.getIntForUser(contentResolver, "match_content_frame_rate", DisplayModeDirector.this.mModeSwitchingType, contentResolver.getUserId());
            if (intForUser != DisplayModeDirector.this.mModeSwitchingType) {
                DisplayModeDirector.this.mModeSwitchingType = intForUser;
                DisplayModeDirector.this.notifyDesiredDisplayModeSpecsChangedLocked();
            }
        }

        public void dumpLocked(PrintWriter printWriter) {
            printWriter.println("  SettingsObserver");
            printWriter.println("    mDefaultRefreshRate: " + this.mDefaultRefreshRate);
            printWriter.println("    mDefaultPeakRefreshRate: " + this.mDefaultPeakRefreshRate);
        }
    }

    /* loaded from: classes.dex */
    public final class AppRequestObserver {
        public final SparseArray<Display.Mode> mAppRequestedModeByDisplay = new SparseArray<>();
        public final SparseArray<SurfaceControl.RefreshRateRange> mAppPreferredRefreshRateRangeByDisplay = new SparseArray<>();

        public AppRequestObserver() {
        }

        public void setAppRequest(int i, int i2, float f, float f2) {
            synchronized (DisplayModeDirector.this.mLock) {
                setAppRequestedModeLocked(i, i2);
                setAppPreferredRefreshRateRangeLocked(i, f, f2);
            }
        }

        public final void setAppRequestedModeLocked(int i, int i2) {
            Vote vote;
            Vote vote2;
            Display.Mode findModeByIdLocked = findModeByIdLocked(i, i2);
            if (Objects.equals(findModeByIdLocked, this.mAppRequestedModeByDisplay.get(i))) {
                return;
            }
            if (findModeByIdLocked != null) {
                this.mAppRequestedModeByDisplay.put(i, findModeByIdLocked);
                vote = Vote.forBaseModeRefreshRate(findModeByIdLocked.getRefreshRate());
                vote2 = Vote.forSize(findModeByIdLocked.getPhysicalWidth(), findModeByIdLocked.getPhysicalHeight());
            } else {
                this.mAppRequestedModeByDisplay.remove(i);
                vote = null;
                vote2 = null;
            }
            DisplayModeDirector.this.updateVoteLocked(i, 5, vote);
            DisplayModeDirector.this.updateVoteLocked(i, 6, vote2);
        }

        /* JADX WARN: Code restructure failed: missing block: B:15:0x0023, code lost:
            if (r1.max == 0.0f) goto L5;
         */
        /* JADX WARN: Removed duplicated region for block: B:19:0x0032 A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:20:0x0033  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final void setAppPreferredRefreshRateRangeLocked(int i, float f, float f2) {
            SurfaceControl.RefreshRateRange refreshRateRange;
            Vote vote = null;
            if (f > 0.0f || f2 > 0.0f) {
                if (f2 <= 0.0f) {
                    f2 = Float.POSITIVE_INFINITY;
                }
                refreshRateRange = new SurfaceControl.RefreshRateRange(f, f2);
                if (refreshRateRange.min == 0.0f) {
                }
                if (Objects.equals(refreshRateRange, this.mAppPreferredRefreshRateRangeByDisplay.get(i))) {
                    if (refreshRateRange != null) {
                        this.mAppPreferredRefreshRateRangeByDisplay.put(i, refreshRateRange);
                        vote = Vote.forRenderFrameRates(refreshRateRange.min, refreshRateRange.max);
                    } else {
                        this.mAppPreferredRefreshRateRangeByDisplay.remove(i);
                    }
                    synchronized (DisplayModeDirector.this.mLock) {
                        DisplayModeDirector.this.updateVoteLocked(i, 4, vote);
                    }
                    return;
                }
                return;
            }
            refreshRateRange = null;
            if (Objects.equals(refreshRateRange, this.mAppPreferredRefreshRateRangeByDisplay.get(i))) {
            }
        }

        public final Display.Mode findModeByIdLocked(int i, int i2) {
            Display.Mode[] modeArr = (Display.Mode[]) DisplayModeDirector.this.mSupportedModesByDisplay.get(i);
            if (modeArr == null) {
                return null;
            }
            for (Display.Mode mode : modeArr) {
                if (mode.getModeId() == i2) {
                    return mode;
                }
            }
            return null;
        }

        public final void dumpLocked(PrintWriter printWriter) {
            printWriter.println("  AppRequestObserver");
            printWriter.println("    mAppRequestedModeByDisplay:");
            for (int i = 0; i < this.mAppRequestedModeByDisplay.size(); i++) {
                printWriter.println("    " + this.mAppRequestedModeByDisplay.keyAt(i) + " -> " + this.mAppRequestedModeByDisplay.valueAt(i));
            }
            printWriter.println("    mAppPreferredRefreshRateRangeByDisplay:");
            for (int i2 = 0; i2 < this.mAppPreferredRefreshRateRangeByDisplay.size(); i2++) {
                printWriter.println("    " + this.mAppPreferredRefreshRateRangeByDisplay.keyAt(i2) + " -> " + this.mAppPreferredRefreshRateRangeByDisplay.valueAt(i2));
            }
        }
    }

    /* loaded from: classes.dex */
    public final class DisplayObserver implements DisplayManager.DisplayListener {
        public final BallotBox mBallotBox;
        public final Context mContext;
        public final Handler mHandler;

        public DisplayObserver(Context context, Handler handler, BallotBox ballotBox) {
            this.mContext = context;
            this.mHandler = handler;
            this.mBallotBox = ballotBox;
        }

        public void observe() {
            Display[] displays;
            DisplayManager displayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
            displayManager.registerDisplayListener(this, this.mHandler);
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            DisplayInfo displayInfo = new DisplayInfo();
            for (Display display : displayManager.getDisplays("android.hardware.display.category.ALL_INCLUDING_DISABLED")) {
                int displayId = display.getDisplayId();
                display.getDisplayInfo(displayInfo);
                sparseArray.put(displayId, displayInfo.supportedModes);
                sparseArray2.put(displayId, displayInfo.getDefaultMode());
            }
            synchronized (DisplayModeDirector.this.mLock) {
                int size = sparseArray.size();
                for (int i = 0; i < size; i++) {
                    DisplayModeDirector.this.mSupportedModesByDisplay.put(sparseArray.keyAt(i), (Display.Mode[]) sparseArray.valueAt(i));
                    DisplayModeDirector.this.mDefaultModeByDisplay.put(sparseArray2.keyAt(i), (Display.Mode) sparseArray2.valueAt(i));
                }
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
            DisplayInfo displayInfo = getDisplayInfo(i);
            updateDisplayModes(i, displayInfo);
            updateLayoutLimitedFrameRate(i, displayInfo);
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
            synchronized (DisplayModeDirector.this.mLock) {
                DisplayModeDirector.this.mSupportedModesByDisplay.remove(i);
                DisplayModeDirector.this.mDefaultModeByDisplay.remove(i);
            }
            updateLayoutLimitedFrameRate(i, null);
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
            DisplayInfo displayInfo = getDisplayInfo(i);
            updateDisplayModes(i, displayInfo);
            updateLayoutLimitedFrameRate(i, displayInfo);
        }

        public final DisplayInfo getDisplayInfo(int i) {
            Display display = ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(i);
            if (display == null) {
                return null;
            }
            DisplayInfo displayInfo = new DisplayInfo();
            display.getDisplayInfo(displayInfo);
            return displayInfo;
        }

        public final void updateLayoutLimitedFrameRate(int i, DisplayInfo displayInfo) {
            SurfaceControl.RefreshRateRange refreshRateRange;
            this.mBallotBox.vote(i, 9, (displayInfo == null || (refreshRateRange = displayInfo.layoutLimitedRefreshRate) == null) ? null : Vote.forPhysicalRefreshRates(refreshRateRange.min, refreshRateRange.max));
        }

        public final void updateDisplayModes(int i, DisplayInfo displayInfo) {
            boolean z;
            if (displayInfo == null) {
                return;
            }
            synchronized (DisplayModeDirector.this.mLock) {
                boolean z2 = true;
                if (Arrays.equals((Object[]) DisplayModeDirector.this.mSupportedModesByDisplay.get(i), displayInfo.supportedModes)) {
                    z = false;
                } else {
                    DisplayModeDirector.this.mSupportedModesByDisplay.put(i, displayInfo.supportedModes);
                    z = true;
                }
                if (Objects.equals(DisplayModeDirector.this.mDefaultModeByDisplay.get(i), displayInfo.getDefaultMode())) {
                    z2 = z;
                } else {
                    DisplayModeDirector.this.mDefaultModeByDisplay.put(i, displayInfo.getDefaultMode());
                }
                if (z2) {
                    DisplayModeDirector.this.notifyDesiredDisplayModeSpecsChangedLocked();
                }
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class BrightnessObserver implements DisplayManager.DisplayListener {
        public AmbientFilter mAmbientFilter;
        public final Context mContext;
        public final Handler mHandler;
        public int[] mHighAmbientBrightnessThresholds;
        public int[] mHighDisplayBrightnessThresholds;
        public final Injector mInjector;
        public Sensor mLightSensor;
        public String mLightSensorName;
        public String mLightSensorType;
        public boolean mLoggingEnabled;
        public int[] mLowAmbientBrightnessThresholds;
        public int[] mLowDisplayBrightnessThresholds;
        public int mRefreshRateInHighZone;
        public int mRefreshRateInLowZone;
        public Sensor mRegisteredLightSensor;
        public SensorManager mSensorManager;
        public boolean mShouldObserveAmbientHighChange;
        public boolean mShouldObserveAmbientLowChange;
        public boolean mShouldObserveDisplayHighChange;
        public boolean mShouldObserveDisplayLowChange;
        public final LightSensorEventListener mLightSensorListener = new LightSensorEventListener();
        public float mAmbientLux = -1.0f;
        public int mBrightness = -1;
        public int mDefaultDisplayState = 0;
        public boolean mRefreshRateChangeable = false;
        public boolean mLowPowerModeEnabled = false;

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
        }

        public BrightnessObserver(Context context, Handler handler, Injector injector) {
            this.mContext = context;
            this.mHandler = handler;
            this.mInjector = injector;
            updateBlockingZoneThresholds(null, false);
            this.mRefreshRateInHighZone = context.getResources().getInteger(17694846);
        }

        public void updateBlockingZoneThresholds(DisplayDeviceConfig displayDeviceConfig, boolean z) {
            loadLowBrightnessThresholds(displayDeviceConfig, z);
            loadHighBrightnessThresholds(displayDeviceConfig, z);
        }

        @VisibleForTesting
        public int[] getLowDisplayBrightnessThreshold() {
            return this.mLowDisplayBrightnessThresholds;
        }

        @VisibleForTesting
        public int[] getLowAmbientBrightnessThreshold() {
            return this.mLowAmbientBrightnessThresholds;
        }

        @VisibleForTesting
        public int[] getHighDisplayBrightnessThreshold() {
            return this.mHighDisplayBrightnessThresholds;
        }

        @VisibleForTesting
        public int[] getHighAmbientBrightnessThreshold() {
            return this.mHighAmbientBrightnessThresholds;
        }

        @VisibleForTesting
        public int getRefreshRateInHighZone() {
            return this.mRefreshRateInHighZone;
        }

        @VisibleForTesting
        public int getRefreshRateInLowZone() {
            return this.mRefreshRateInLowZone;
        }

        public final void loadLowBrightnessThresholds(final DisplayDeviceConfig displayDeviceConfig, boolean z) {
            loadRefreshRateInHighZone(displayDeviceConfig, z);
            loadRefreshRateInLowZone(displayDeviceConfig, z);
            this.mLowDisplayBrightnessThresholds = loadBrightnessThresholds(new Callable() { // from class: com.android.server.display.mode.DisplayModeDirector$BrightnessObserver$$ExternalSyntheticLambda4
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    int[] lambda$loadLowBrightnessThresholds$0;
                    lambda$loadLowBrightnessThresholds$0 = DisplayModeDirector.BrightnessObserver.this.lambda$loadLowBrightnessThresholds$0();
                    return lambda$loadLowBrightnessThresholds$0;
                }
            }, new Callable() { // from class: com.android.server.display.mode.DisplayModeDirector$BrightnessObserver$$ExternalSyntheticLambda5
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    int[] lowDisplayBrightnessThresholds;
                    lowDisplayBrightnessThresholds = DisplayDeviceConfig.this.getLowDisplayBrightnessThresholds();
                    return lowDisplayBrightnessThresholds;
                }
            }, 17236005, displayDeviceConfig, z);
            int[] loadBrightnessThresholds = loadBrightnessThresholds(new Callable() { // from class: com.android.server.display.mode.DisplayModeDirector$BrightnessObserver$$ExternalSyntheticLambda6
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    int[] lambda$loadLowBrightnessThresholds$2;
                    lambda$loadLowBrightnessThresholds$2 = DisplayModeDirector.BrightnessObserver.this.lambda$loadLowBrightnessThresholds$2();
                    return lambda$loadLowBrightnessThresholds$2;
                }
            }, new Callable() { // from class: com.android.server.display.mode.DisplayModeDirector$BrightnessObserver$$ExternalSyntheticLambda7
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    int[] lowAmbientBrightnessThresholds;
                    lowAmbientBrightnessThresholds = DisplayDeviceConfig.this.getLowAmbientBrightnessThresholds();
                    return lowAmbientBrightnessThresholds;
                }
            }, 17235985, displayDeviceConfig, z);
            this.mLowAmbientBrightnessThresholds = loadBrightnessThresholds;
            if (this.mLowDisplayBrightnessThresholds.length == loadBrightnessThresholds.length) {
                return;
            }
            throw new RuntimeException("display low brightness threshold array and ambient brightness threshold array have different length: displayBrightnessThresholds=" + Arrays.toString(this.mLowDisplayBrightnessThresholds) + ", ambientBrightnessThresholds=" + Arrays.toString(this.mLowAmbientBrightnessThresholds));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ int[] lambda$loadLowBrightnessThresholds$0() throws Exception {
            return DisplayModeDirector.this.mDeviceConfigDisplaySettings.getLowDisplayBrightnessThresholds();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ int[] lambda$loadLowBrightnessThresholds$2() throws Exception {
            return DisplayModeDirector.this.mDeviceConfigDisplaySettings.getLowAmbientBrightnessThresholds();
        }

        public final void loadRefreshRateInLowZone(DisplayDeviceConfig displayDeviceConfig, boolean z) {
            int defaultLowBlockingZoneRefreshRate;
            if (displayDeviceConfig == null) {
                defaultLowBlockingZoneRefreshRate = this.mContext.getResources().getInteger(17694805);
            } else {
                defaultLowBlockingZoneRefreshRate = displayDeviceConfig.getDefaultLowBlockingZoneRefreshRate();
            }
            if (z) {
                try {
                    defaultLowBlockingZoneRefreshRate = DisplayModeDirector.this.mDeviceConfig.getInt("display_manager", "refresh_rate_in_zone", defaultLowBlockingZoneRefreshRate);
                } catch (Exception unused) {
                }
            }
            this.mRefreshRateInLowZone = defaultLowBlockingZoneRefreshRate;
        }

        public final void loadRefreshRateInHighZone(DisplayDeviceConfig displayDeviceConfig, boolean z) {
            int defaultHighBlockingZoneRefreshRate;
            if (displayDeviceConfig == null) {
                defaultHighBlockingZoneRefreshRate = this.mContext.getResources().getInteger(17694846);
            } else {
                defaultHighBlockingZoneRefreshRate = displayDeviceConfig.getDefaultHighBlockingZoneRefreshRate();
            }
            if (z) {
                try {
                    defaultHighBlockingZoneRefreshRate = DisplayModeDirector.this.mDeviceConfig.getInt("display_manager", "refresh_rate_in_high_zone", defaultHighBlockingZoneRefreshRate);
                } catch (Exception unused) {
                }
            }
            this.mRefreshRateInHighZone = defaultHighBlockingZoneRefreshRate;
        }

        public final void loadHighBrightnessThresholds(final DisplayDeviceConfig displayDeviceConfig, boolean z) {
            this.mHighDisplayBrightnessThresholds = loadBrightnessThresholds(new Callable() { // from class: com.android.server.display.mode.DisplayModeDirector$BrightnessObserver$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    int[] lambda$loadHighBrightnessThresholds$4;
                    lambda$loadHighBrightnessThresholds$4 = DisplayModeDirector.BrightnessObserver.this.lambda$loadHighBrightnessThresholds$4();
                    return lambda$loadHighBrightnessThresholds$4;
                }
            }, new Callable() { // from class: com.android.server.display.mode.DisplayModeDirector$BrightnessObserver$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    int[] highDisplayBrightnessThresholds;
                    highDisplayBrightnessThresholds = DisplayDeviceConfig.this.getHighDisplayBrightnessThresholds();
                    return highDisplayBrightnessThresholds;
                }
            }, 17236082, displayDeviceConfig, z);
            int[] loadBrightnessThresholds = loadBrightnessThresholds(new Callable() { // from class: com.android.server.display.mode.DisplayModeDirector$BrightnessObserver$$ExternalSyntheticLambda2
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    int[] lambda$loadHighBrightnessThresholds$6;
                    lambda$loadHighBrightnessThresholds$6 = DisplayModeDirector.BrightnessObserver.this.lambda$loadHighBrightnessThresholds$6();
                    return lambda$loadHighBrightnessThresholds$6;
                }
            }, new Callable() { // from class: com.android.server.display.mode.DisplayModeDirector$BrightnessObserver$$ExternalSyntheticLambda3
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    int[] highAmbientBrightnessThresholds;
                    highAmbientBrightnessThresholds = DisplayDeviceConfig.this.getHighAmbientBrightnessThresholds();
                    return highAmbientBrightnessThresholds;
                }
            }, 17236081, displayDeviceConfig, z);
            this.mHighAmbientBrightnessThresholds = loadBrightnessThresholds;
            if (this.mHighDisplayBrightnessThresholds.length == loadBrightnessThresholds.length) {
                return;
            }
            throw new RuntimeException("display high brightness threshold array and ambient brightness threshold array have different length: displayBrightnessThresholds=" + Arrays.toString(this.mHighDisplayBrightnessThresholds) + ", ambientBrightnessThresholds=" + Arrays.toString(this.mHighAmbientBrightnessThresholds));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ int[] lambda$loadHighBrightnessThresholds$4() throws Exception {
            return DisplayModeDirector.this.mDeviceConfigDisplaySettings.getHighDisplayBrightnessThresholds();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ int[] lambda$loadHighBrightnessThresholds$6() throws Exception {
            return DisplayModeDirector.this.mDeviceConfigDisplaySettings.getHighAmbientBrightnessThresholds();
        }

        /* JADX WARN: Removed duplicated region for block: B:17:0x000c A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final int[] loadBrightnessThresholds(Callable<int[]> callable, Callable<int[]> callable2, int i, DisplayDeviceConfig displayDeviceConfig, boolean z) {
            int[] call;
            int[] call2;
            if (z) {
                try {
                    call = callable.call();
                } catch (Exception unused) {
                }
                if (call != null) {
                    try {
                        if (displayDeviceConfig == null) {
                            call2 = this.mContext.getResources().getIntArray(i);
                        } else {
                            call2 = callable2.call();
                        }
                        call = call2;
                        return call;
                    } catch (Exception e) {
                        Slog.e("DisplayModeDirector", "Unexpectedly failed to load display brightness threshold");
                        e.printStackTrace();
                        return call;
                    }
                }
                return call;
            }
            call = null;
            if (call != null) {
            }
        }

        @VisibleForTesting
        public int[] getLowDisplayBrightnessThresholds() {
            return this.mLowDisplayBrightnessThresholds;
        }

        @VisibleForTesting
        public int[] getLowAmbientBrightnessThresholds() {
            return this.mLowAmbientBrightnessThresholds;
        }

        public final void observe(SensorManager sensorManager) {
            this.mSensorManager = sensorManager;
            this.mBrightness = getBrightness(0);
            int[] lowDisplayBrightnessThresholds = DisplayModeDirector.this.mDeviceConfigDisplaySettings.getLowDisplayBrightnessThresholds();
            int[] lowAmbientBrightnessThresholds = DisplayModeDirector.this.mDeviceConfigDisplaySettings.getLowAmbientBrightnessThresholds();
            if (lowDisplayBrightnessThresholds != null && lowAmbientBrightnessThresholds != null && lowDisplayBrightnessThresholds.length == lowAmbientBrightnessThresholds.length) {
                this.mLowDisplayBrightnessThresholds = lowDisplayBrightnessThresholds;
                this.mLowAmbientBrightnessThresholds = lowAmbientBrightnessThresholds;
            }
            int[] highDisplayBrightnessThresholds = DisplayModeDirector.this.mDeviceConfigDisplaySettings.getHighDisplayBrightnessThresholds();
            int[] highAmbientBrightnessThresholds = DisplayModeDirector.this.mDeviceConfigDisplaySettings.getHighAmbientBrightnessThresholds();
            if (highDisplayBrightnessThresholds != null && highAmbientBrightnessThresholds != null && highDisplayBrightnessThresholds.length == highAmbientBrightnessThresholds.length) {
                this.mHighDisplayBrightnessThresholds = highDisplayBrightnessThresholds;
                this.mHighAmbientBrightnessThresholds = highAmbientBrightnessThresholds;
            }
            int refreshRateInLowZone = DisplayModeDirector.this.mDeviceConfigDisplaySettings.getRefreshRateInLowZone();
            if (refreshRateInLowZone != -1) {
                this.mRefreshRateInLowZone = refreshRateInLowZone;
            }
            int refreshRateInHighZone = DisplayModeDirector.this.mDeviceConfigDisplaySettings.getRefreshRateInHighZone();
            if (refreshRateInHighZone != -1) {
                this.mRefreshRateInHighZone = refreshRateInHighZone;
            }
            restartObserver();
            DisplayModeDirector.this.mDeviceConfigDisplaySettings.startListening();
            this.mInjector.registerDisplayListener(this, this.mHandler, 12L);
        }

        public final void setLoggingEnabled(boolean z) {
            if (this.mLoggingEnabled == z) {
                return;
            }
            this.mLoggingEnabled = z;
            this.mLightSensorListener.setLoggingEnabled(z);
        }

        @VisibleForTesting
        public void onRefreshRateSettingChangedLocked(float f, float f2) {
            boolean z = f2 - f > 1.0f && f2 > 60.0f;
            if (this.mRefreshRateChangeable != z) {
                this.mRefreshRateChangeable = z;
                updateSensorStatus();
                if (z) {
                    return;
                }
                DisplayModeDirector.this.updateVoteLocked(1, null);
                DisplayModeDirector.this.updateVoteLocked(11, null);
            }
        }

        public final void onLowPowerModeEnabledLocked(boolean z) {
            if (this.mLowPowerModeEnabled != z) {
                this.mLowPowerModeEnabled = z;
                updateSensorStatus();
            }
        }

        public final void onDeviceConfigLowBrightnessThresholdsChanged(int[] iArr, int[] iArr2) {
            if (iArr != null && iArr2 != null && iArr.length == iArr2.length) {
                this.mLowDisplayBrightnessThresholds = iArr;
                this.mLowAmbientBrightnessThresholds = iArr2;
            } else {
                this.mLowDisplayBrightnessThresholds = this.mContext.getResources().getIntArray(17236005);
                this.mLowAmbientBrightnessThresholds = this.mContext.getResources().getIntArray(17235985);
            }
            restartObserver();
        }

        public void onDeviceConfigRefreshRateInLowZoneChanged(int i) {
            if (i != this.mRefreshRateInLowZone) {
                this.mRefreshRateInLowZone = i;
                restartObserver();
            }
        }

        public final void onDeviceConfigHighBrightnessThresholdsChanged(int[] iArr, int[] iArr2) {
            if (iArr != null && iArr2 != null && iArr.length == iArr2.length) {
                this.mHighDisplayBrightnessThresholds = iArr;
                this.mHighAmbientBrightnessThresholds = iArr2;
            } else {
                this.mHighDisplayBrightnessThresholds = this.mContext.getResources().getIntArray(17236082);
                this.mHighAmbientBrightnessThresholds = this.mContext.getResources().getIntArray(17236081);
            }
            restartObserver();
        }

        public void onDeviceConfigRefreshRateInHighZoneChanged(int i) {
            if (i != this.mRefreshRateInHighZone) {
                this.mRefreshRateInHighZone = i;
                restartObserver();
            }
        }

        public void dumpLocked(PrintWriter printWriter) {
            printWriter.println("  BrightnessObserver");
            printWriter.println("    mAmbientLux: " + this.mAmbientLux);
            printWriter.println("    mBrightness: " + this.mBrightness);
            printWriter.println("    mDefaultDisplayState: " + this.mDefaultDisplayState);
            printWriter.println("    mLowPowerModeEnabled: " + this.mLowPowerModeEnabled);
            printWriter.println("    mRefreshRateChangeable: " + this.mRefreshRateChangeable);
            printWriter.println("    mShouldObserveDisplayLowChange: " + this.mShouldObserveDisplayLowChange);
            printWriter.println("    mShouldObserveAmbientLowChange: " + this.mShouldObserveAmbientLowChange);
            printWriter.println("    mRefreshRateInLowZone: " + this.mRefreshRateInLowZone);
            for (int i : this.mLowDisplayBrightnessThresholds) {
                printWriter.println("    mDisplayLowBrightnessThreshold: " + i);
            }
            for (int i2 : this.mLowAmbientBrightnessThresholds) {
                printWriter.println("    mAmbientLowBrightnessThreshold: " + i2);
            }
            printWriter.println("    mShouldObserveDisplayHighChange: " + this.mShouldObserveDisplayHighChange);
            printWriter.println("    mShouldObserveAmbientHighChange: " + this.mShouldObserveAmbientHighChange);
            printWriter.println("    mRefreshRateInHighZone: " + this.mRefreshRateInHighZone);
            int[] iArr = this.mHighDisplayBrightnessThresholds;
            int length = iArr.length;
            for (int i3 = 0; i3 < length; i3++) {
                printWriter.println("    mDisplayHighBrightnessThresholds: " + iArr[i3]);
            }
            for (int i4 : this.mHighAmbientBrightnessThresholds) {
                printWriter.println("    mAmbientHighBrightnessThresholds: " + i4);
            }
            printWriter.println("    mRegisteredLightSensor: " + this.mRegisteredLightSensor);
            printWriter.println("    mLightSensor: " + this.mLightSensor);
            printWriter.println("    mLightSensorName: " + this.mLightSensorName);
            printWriter.println("    mLightSensorType: " + this.mLightSensorType);
            this.mLightSensorListener.dumpLocked(printWriter);
            if (this.mAmbientFilter != null) {
                this.mAmbientFilter.dump(new IndentingPrintWriter(printWriter, "    "));
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
            if (i == 0) {
                updateDefaultDisplayState();
                int brightness = getBrightness(i);
                synchronized (DisplayModeDirector.this.mLock) {
                    if (brightness != this.mBrightness) {
                        this.mBrightness = brightness;
                        onBrightnessChangedLocked();
                    }
                }
            }
        }

        public final void restartObserver() {
            if (this.mRefreshRateInLowZone > 0) {
                this.mShouldObserveDisplayLowChange = hasValidThreshold(this.mLowDisplayBrightnessThresholds);
                this.mShouldObserveAmbientLowChange = hasValidThreshold(this.mLowAmbientBrightnessThresholds);
            } else {
                this.mShouldObserveDisplayLowChange = false;
                this.mShouldObserveAmbientLowChange = false;
            }
            if (this.mRefreshRateInHighZone > 0) {
                this.mShouldObserveDisplayHighChange = hasValidThreshold(this.mHighDisplayBrightnessThresholds);
                this.mShouldObserveAmbientHighChange = hasValidThreshold(this.mHighAmbientBrightnessThresholds);
            } else {
                this.mShouldObserveDisplayHighChange = false;
                this.mShouldObserveAmbientHighChange = false;
            }
            if (this.mShouldObserveAmbientLowChange || this.mShouldObserveAmbientHighChange) {
                Sensor lightSensor = getLightSensor();
                if (lightSensor != null && lightSensor != this.mLightSensor) {
                    this.mAmbientFilter = AmbientFilterFactory.createBrightnessFilter("DisplayModeDirector", this.mContext.getResources());
                    this.mLightSensor = lightSensor;
                }
            } else {
                this.mAmbientFilter = null;
                this.mLightSensor = null;
            }
            updateSensorStatus();
            if (this.mRefreshRateChangeable) {
                synchronized (DisplayModeDirector.this.mLock) {
                    onBrightnessChangedLocked();
                }
            }
        }

        public final void reloadLightSensor(DisplayDeviceConfig displayDeviceConfig) {
            reloadLightSensorData(displayDeviceConfig);
            restartObserver();
        }

        public final void reloadLightSensorData(DisplayDeviceConfig displayDeviceConfig) {
            if (displayDeviceConfig != null && displayDeviceConfig.getAmbientLightSensor() != null) {
                this.mLightSensorType = displayDeviceConfig.getAmbientLightSensor().type;
                this.mLightSensorName = displayDeviceConfig.getAmbientLightSensor().name;
            } else if (this.mLightSensorName == null && this.mLightSensorType == null) {
                this.mLightSensorType = this.mContext.getResources().getString(17039920);
                this.mLightSensorName = "";
            }
        }

        public final Sensor getLightSensor() {
            return SensorUtils.findSensor(this.mSensorManager, this.mLightSensorType, this.mLightSensorName, 5);
        }

        public final boolean hasValidThreshold(int[] iArr) {
            for (int i : iArr) {
                if (i >= 0) {
                    return true;
                }
            }
            return false;
        }

        public final boolean isInsideLowZone(int i, float f) {
            int i2 = 0;
            while (true) {
                int[] iArr = this.mLowDisplayBrightnessThresholds;
                if (i2 >= iArr.length) {
                    return false;
                }
                int i3 = iArr[i2];
                int i4 = this.mLowAmbientBrightnessThresholds[i2];
                if (i3 < 0 || i4 < 0) {
                    if (i3 >= 0) {
                        if (i <= i3) {
                            return true;
                        }
                    } else if (i4 >= 0 && f <= i4) {
                        return true;
                    }
                } else if (i <= i3 && f <= i4) {
                    return true;
                }
                i2++;
            }
        }

        public final boolean isInsideHighZone(int i, float f) {
            int i2 = 0;
            while (true) {
                int[] iArr = this.mHighDisplayBrightnessThresholds;
                if (i2 >= iArr.length) {
                    return false;
                }
                int i3 = iArr[i2];
                int i4 = this.mHighAmbientBrightnessThresholds[i2];
                if (i3 < 0 || i4 < 0) {
                    if (i3 >= 0) {
                        if (i >= i3) {
                            return true;
                        }
                    } else if (i4 >= 0 && f >= i4) {
                        return true;
                    }
                } else if (i >= i3 && f >= i4) {
                    return true;
                }
                i2++;
            }
        }

        public final void onBrightnessChangedLocked() {
            Vote vote;
            Vote vote2;
            if (this.mBrightness < 0) {
                return;
            }
            boolean z = false;
            if (hasValidLowZone() && isInsideLowZone(this.mBrightness, this.mAmbientLux)) {
                int i = this.mRefreshRateInLowZone;
                vote = Vote.forPhysicalRefreshRates(i, i);
                vote2 = Vote.forDisableRefreshRateSwitching();
            } else {
                vote = null;
                vote2 = null;
            }
            if (hasValidHighZone() && isInsideHighZone(this.mBrightness, this.mAmbientLux)) {
                z = true;
            }
            if (z) {
                int i2 = this.mRefreshRateInHighZone;
                vote = Vote.forPhysicalRefreshRates(i2, i2);
                vote2 = Vote.forDisableRefreshRateSwitching();
            }
            if (this.mLoggingEnabled) {
                Slog.d("DisplayModeDirector", "Display brightness " + this.mBrightness + ", ambient lux " + this.mAmbientLux + ", Vote " + vote);
            }
            DisplayModeDirector.this.updateVoteLocked(1, vote);
            DisplayModeDirector.this.updateVoteLocked(11, vote2);
        }

        public final boolean hasValidLowZone() {
            return this.mRefreshRateInLowZone > 0 && (this.mShouldObserveDisplayLowChange || this.mShouldObserveAmbientLowChange);
        }

        public final boolean hasValidHighZone() {
            return this.mRefreshRateInHighZone > 0 && (this.mShouldObserveDisplayHighChange || this.mShouldObserveAmbientHighChange);
        }

        public final void updateDefaultDisplayState() {
            Display display = ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(0);
            if (display == null) {
                return;
            }
            setDefaultDisplayState(display.getState());
        }

        @VisibleForTesting
        public void setDefaultDisplayState(int i) {
            if (this.mLoggingEnabled) {
                Slog.d("DisplayModeDirector", "setDefaultDisplayState: mDefaultDisplayState = " + this.mDefaultDisplayState + ", state = " + i);
            }
            if (this.mDefaultDisplayState != i) {
                this.mDefaultDisplayState = i;
                updateSensorStatus();
            }
        }

        public final void updateSensorStatus() {
            if (this.mSensorManager == null || this.mLightSensorListener == null) {
                return;
            }
            if (this.mLoggingEnabled) {
                Slog.d("DisplayModeDirector", "updateSensorStatus: mShouldObserveAmbientLowChange = " + this.mShouldObserveAmbientLowChange + ", mShouldObserveAmbientHighChange = " + this.mShouldObserveAmbientHighChange);
                Slog.d("DisplayModeDirector", "updateSensorStatus: mLowPowerModeEnabled = " + this.mLowPowerModeEnabled + ", mRefreshRateChangeable = " + this.mRefreshRateChangeable);
            }
            if ((this.mShouldObserveAmbientLowChange || this.mShouldObserveAmbientHighChange) && isDeviceActive() && !this.mLowPowerModeEnabled && this.mRefreshRateChangeable) {
                registerLightSensor();
            } else {
                unregisterSensorListener();
            }
        }

        public final void registerLightSensor() {
            Sensor sensor = this.mRegisteredLightSensor;
            if (sensor == this.mLightSensor) {
                return;
            }
            if (sensor != null) {
                unregisterSensorListener();
            }
            this.mSensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, 250000, this.mHandler);
            this.mRegisteredLightSensor = this.mLightSensor;
            if (this.mLoggingEnabled) {
                Slog.d("DisplayModeDirector", "updateSensorStatus: registerListener");
            }
        }

        public final void unregisterSensorListener() {
            this.mLightSensorListener.removeCallbacks();
            this.mSensorManager.unregisterListener(this.mLightSensorListener);
            this.mRegisteredLightSensor = null;
            if (this.mLoggingEnabled) {
                Slog.d("DisplayModeDirector", "updateSensorStatus: unregisterListener");
            }
        }

        public final boolean isDeviceActive() {
            return this.mDefaultDisplayState == 2;
        }

        public final int getBrightness(int i) {
            BrightnessInfo brightnessInfo = this.mInjector.getBrightnessInfo(i);
            if (brightnessInfo != null) {
                return BrightnessSynchronizer.brightnessFloatToInt(brightnessInfo.adjustedBrightness);
            }
            return -1;
        }

        /* loaded from: classes.dex */
        public final class LightSensorEventListener implements SensorEventListener {
            public final Runnable mInjectSensorEventRunnable;
            public float mLastSensorData;
            public boolean mLoggingEnabled;
            public long mTimestamp;

            @Override // android.hardware.SensorEventListener
            public void onAccuracyChanged(Sensor sensor, int i) {
            }

            public LightSensorEventListener() {
                this.mInjectSensorEventRunnable = new Runnable() { // from class: com.android.server.display.mode.DisplayModeDirector.BrightnessObserver.LightSensorEventListener.1
                    @Override // java.lang.Runnable
                    public void run() {
                        LightSensorEventListener.this.processSensorData(SystemClock.uptimeMillis());
                        LightSensorEventListener lightSensorEventListener = LightSensorEventListener.this;
                        if (!lightSensorEventListener.isDifferentZone(lightSensorEventListener.mLastSensorData, BrightnessObserver.this.mAmbientLux, BrightnessObserver.this.mLowAmbientBrightnessThresholds)) {
                            LightSensorEventListener lightSensorEventListener2 = LightSensorEventListener.this;
                            if (!lightSensorEventListener2.isDifferentZone(lightSensorEventListener2.mLastSensorData, BrightnessObserver.this.mAmbientLux, BrightnessObserver.this.mHighAmbientBrightnessThresholds)) {
                                return;
                            }
                        }
                        BrightnessObserver.this.mHandler.postDelayed(LightSensorEventListener.this.mInjectSensorEventRunnable, 250L);
                    }
                };
            }

            public void dumpLocked(PrintWriter printWriter) {
                printWriter.println("    mLastSensorData: " + this.mLastSensorData);
                printWriter.println("    mTimestamp: " + formatTimestamp(this.mTimestamp));
            }

            public void setLoggingEnabled(boolean z) {
                if (this.mLoggingEnabled == z) {
                    return;
                }
                this.mLoggingEnabled = z;
            }

            @Override // android.hardware.SensorEventListener
            public void onSensorChanged(SensorEvent sensorEvent) {
                this.mLastSensorData = sensorEvent.values[0];
                if (this.mLoggingEnabled) {
                    Slog.d("DisplayModeDirector", "On sensor changed: " + this.mLastSensorData);
                }
                boolean isDifferentZone = isDifferentZone(this.mLastSensorData, BrightnessObserver.this.mAmbientLux, BrightnessObserver.this.mLowAmbientBrightnessThresholds);
                boolean isDifferentZone2 = isDifferentZone(this.mLastSensorData, BrightnessObserver.this.mAmbientLux, BrightnessObserver.this.mHighAmbientBrightnessThresholds);
                if (((isDifferentZone && this.mLastSensorData < BrightnessObserver.this.mAmbientLux) || (isDifferentZone2 && this.mLastSensorData > BrightnessObserver.this.mAmbientLux)) && BrightnessObserver.this.mAmbientFilter != null) {
                    BrightnessObserver.this.mAmbientFilter.clear();
                }
                long uptimeMillis = SystemClock.uptimeMillis();
                this.mTimestamp = System.currentTimeMillis();
                if (BrightnessObserver.this.mAmbientFilter != null) {
                    BrightnessObserver.this.mAmbientFilter.addValue(uptimeMillis, this.mLastSensorData);
                }
                BrightnessObserver.this.mHandler.removeCallbacks(this.mInjectSensorEventRunnable);
                processSensorData(uptimeMillis);
                if ((!isDifferentZone || this.mLastSensorData <= BrightnessObserver.this.mAmbientLux) && (!isDifferentZone2 || this.mLastSensorData >= BrightnessObserver.this.mAmbientLux)) {
                    return;
                }
                BrightnessObserver.this.mHandler.postDelayed(this.mInjectSensorEventRunnable, 250L);
            }

            public void removeCallbacks() {
                BrightnessObserver.this.mHandler.removeCallbacks(this.mInjectSensorEventRunnable);
            }

            public final String formatTimestamp(long j) {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(new Date(j));
            }

            public final void processSensorData(long j) {
                if (BrightnessObserver.this.mAmbientFilter != null) {
                    BrightnessObserver brightnessObserver = BrightnessObserver.this;
                    brightnessObserver.mAmbientLux = brightnessObserver.mAmbientFilter.getEstimate(j);
                } else {
                    BrightnessObserver.this.mAmbientLux = this.mLastSensorData;
                }
                synchronized (DisplayModeDirector.this.mLock) {
                    BrightnessObserver.this.onBrightnessChangedLocked();
                }
            }

            public final boolean isDifferentZone(float f, float f2, int[] iArr) {
                for (float f3 : iArr) {
                    if (f <= f3 && f2 > f3) {
                        return true;
                    }
                    if (f > f3 && f2 <= f3) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    /* loaded from: classes.dex */
    public class UdfpsObserver extends IUdfpsRefreshRateRequestCallback.Stub {
        public final SparseBooleanArray mAuthenticationPossible;
        public final SparseBooleanArray mUdfpsRefreshRateEnabled;

        public UdfpsObserver() {
            this.mUdfpsRefreshRateEnabled = new SparseBooleanArray();
            this.mAuthenticationPossible = new SparseBooleanArray();
        }

        public void observe() {
            StatusBarManagerInternal statusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
            if (statusBarManagerInternal != null) {
                statusBarManagerInternal.setUdfpsRefreshRateCallback(this);
            }
        }

        public void onRequestEnabled(int i) {
            synchronized (DisplayModeDirector.this.mLock) {
                this.mUdfpsRefreshRateEnabled.put(i, true);
                updateVoteLocked(i, true, 14);
            }
        }

        public void onRequestDisabled(int i) {
            synchronized (DisplayModeDirector.this.mLock) {
                this.mUdfpsRefreshRateEnabled.put(i, false);
                updateVoteLocked(i, false, 14);
            }
        }

        public void onAuthenticationPossible(int i, boolean z) {
            synchronized (DisplayModeDirector.this.mLock) {
                this.mAuthenticationPossible.put(i, z);
                updateVoteLocked(i, z, 8);
            }
        }

        @GuardedBy({"mLock"})
        public final void updateVoteLocked(int i, boolean z, int i2) {
            Vote vote;
            if (z) {
                float maxRefreshRateLocked = DisplayModeDirector.this.getMaxRefreshRateLocked(i);
                vote = Vote.forPhysicalRefreshRates(maxRefreshRateLocked, maxRefreshRateLocked);
            } else {
                vote = null;
            }
            DisplayModeDirector.this.updateVoteLocked(i, i2, vote);
        }

        public void dumpLocked(PrintWriter printWriter) {
            printWriter.println("  UdfpsObserver");
            printWriter.println("    mUdfpsRefreshRateEnabled: ");
            for (int i = 0; i < this.mUdfpsRefreshRateEnabled.size(); i++) {
                printWriter.println("      Display " + this.mUdfpsRefreshRateEnabled.keyAt(i) + ": " + (this.mUdfpsRefreshRateEnabled.valueAt(i) ? "enabled" : "disabled"));
            }
            printWriter.println("    mAuthenticationPossible: ");
            for (int i2 = 0; i2 < this.mAuthenticationPossible.size(); i2++) {
                printWriter.println("      Display " + this.mAuthenticationPossible.keyAt(i2) + ": " + (this.mAuthenticationPossible.valueAt(i2) ? "possible" : "impossible"));
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class SensorObserver implements SensorManagerInternal.ProximityActiveListener, DisplayManager.DisplayListener {
        public final BallotBox mBallotBox;
        public final Context mContext;
        public DisplayManager mDisplayManager;
        public DisplayManagerInternal mDisplayManagerInternal;
        public final Injector mInjector;
        public final String mProximitySensorName = null;
        public final String mProximitySensorType = "android.sensor.proximity";
        @GuardedBy({"mSensorObserverLock"})
        public final SparseBooleanArray mDozeStateByDisplay = new SparseBooleanArray();
        public final Object mSensorObserverLock = new Object();
        @GuardedBy({"mSensorObserverLock"})
        public boolean mIsProxActive = false;

        public SensorObserver(Context context, BallotBox ballotBox, Injector injector) {
            this.mContext = context;
            this.mBallotBox = ballotBox;
            this.mInjector = injector;
        }

        @Override // com.android.server.sensors.SensorManagerInternal.ProximityActiveListener
        public void onProximityActive(boolean z) {
            synchronized (this.mSensorObserverLock) {
                if (this.mIsProxActive != z) {
                    this.mIsProxActive = z;
                    recalculateVotesLocked();
                }
            }
        }

        public void observe() {
            Display[] displays;
            this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
            this.mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
            ((SensorManagerInternal) LocalServices.getService(SensorManagerInternal.class)).addProximityActiveListener(BackgroundThread.getExecutor(), this);
            synchronized (this.mSensorObserverLock) {
                for (Display display : this.mDisplayManager.getDisplays("android.hardware.display.category.ALL_INCLUDING_DISABLED")) {
                    this.mDozeStateByDisplay.put(display.getDisplayId(), this.mInjector.isDozeState(display));
                }
            }
            this.mInjector.registerDisplayListener(this, BackgroundThread.getHandler(), 7L);
        }

        public final void recalculateVotesLocked() {
            SurfaceControl.RefreshRateRange refreshRateForDisplayAndSensor;
            for (Display display : this.mDisplayManager.getDisplays("android.hardware.display.category.ALL_INCLUDING_DISABLED")) {
                int displayId = display.getDisplayId();
                this.mBallotBox.vote(displayId, 13, (!this.mIsProxActive || this.mDozeStateByDisplay.get(displayId) || (refreshRateForDisplayAndSensor = this.mDisplayManagerInternal.getRefreshRateForDisplayAndSensor(displayId, this.mProximitySensorName, "android.sensor.proximity")) == null) ? null : Vote.forPhysicalRefreshRates(refreshRateForDisplayAndSensor.min, refreshRateForDisplayAndSensor.max));
            }
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println("  SensorObserver");
            synchronized (this.mSensorObserverLock) {
                printWriter.println("    mIsProxActive=" + this.mIsProxActive);
                printWriter.println("    mDozeStateByDisplay:");
                for (int i = 0; i < this.mDozeStateByDisplay.size(); i++) {
                    int keyAt = this.mDozeStateByDisplay.keyAt(i);
                    boolean valueAt = this.mDozeStateByDisplay.valueAt(i);
                    printWriter.println("      " + keyAt + " -> " + valueAt);
                }
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
            boolean isDozeState = this.mInjector.isDozeState(this.mDisplayManager.getDisplay(i));
            synchronized (this.mSensorObserverLock) {
                this.mDozeStateByDisplay.put(i, isDozeState);
                recalculateVotesLocked();
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
            boolean z = this.mDozeStateByDisplay.get(i);
            synchronized (this.mSensorObserverLock) {
                this.mDozeStateByDisplay.put(i, this.mInjector.isDozeState(this.mDisplayManager.getDisplay(i)));
                if (z != this.mDozeStateByDisplay.get(i)) {
                    recalculateVotesLocked();
                }
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
            synchronized (this.mSensorObserverLock) {
                this.mDozeStateByDisplay.delete(i);
                recalculateVotesLocked();
            }
        }
    }

    /* loaded from: classes.dex */
    public class HbmObserver implements DisplayManager.DisplayListener {
        public final BallotBox mBallotBox;
        public final DeviceConfigDisplaySettings mDeviceConfigDisplaySettings;
        public DisplayManagerInternal mDisplayManagerInternal;
        public final Handler mHandler;
        public final Injector mInjector;
        public int mRefreshRateInHbmHdr;
        public int mRefreshRateInHbmSunlight;
        public final SparseIntArray mHbmMode = new SparseIntArray();
        public final SparseBooleanArray mHbmActive = new SparseBooleanArray();

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
        }

        public HbmObserver(Injector injector, BallotBox ballotBox, Handler handler, DeviceConfigDisplaySettings deviceConfigDisplaySettings) {
            this.mInjector = injector;
            this.mBallotBox = ballotBox;
            this.mHandler = handler;
            this.mDeviceConfigDisplaySettings = deviceConfigDisplaySettings;
        }

        public void setupHdrRefreshRates(DisplayDeviceConfig displayDeviceConfig) {
            this.mRefreshRateInHbmHdr = this.mDeviceConfigDisplaySettings.getRefreshRateInHbmHdr(displayDeviceConfig);
            this.mRefreshRateInHbmSunlight = this.mDeviceConfigDisplaySettings.getRefreshRateInHbmSunlight(displayDeviceConfig);
        }

        public void observe() {
            synchronized (DisplayModeDirector.this.mLock) {
                setupHdrRefreshRates(DisplayModeDirector.this.mDefaultDisplayDeviceConfig);
            }
            this.mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
            this.mInjector.registerDisplayListener(this, this.mHandler, 10L);
        }

        @VisibleForTesting
        public int getRefreshRateInHbmSunlight() {
            return this.mRefreshRateInHbmSunlight;
        }

        @VisibleForTesting
        public int getRefreshRateInHbmHdr() {
            return this.mRefreshRateInHbmHdr;
        }

        public void onDeviceConfigRefreshRateInHbmSunlightChanged(int i) {
            if (i != this.mRefreshRateInHbmSunlight) {
                this.mRefreshRateInHbmSunlight = i;
                onDeviceConfigRefreshRateInHbmChanged();
            }
        }

        public void onDeviceConfigRefreshRateInHbmHdrChanged(int i) {
            if (i != this.mRefreshRateInHbmHdr) {
                this.mRefreshRateInHbmHdr = i;
                onDeviceConfigRefreshRateInHbmChanged();
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
            this.mBallotBox.vote(i, 2, null);
            this.mHbmMode.delete(i);
            this.mHbmActive.delete(i);
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
            BrightnessInfo brightnessInfo = this.mInjector.getBrightnessInfo(i);
            if (brightnessInfo == null) {
                return;
            }
            int i2 = brightnessInfo.highBrightnessMode;
            boolean z = i2 != 0 && brightnessInfo.adjustedBrightness > brightnessInfo.highBrightnessTransitionPoint;
            if (i2 == this.mHbmMode.get(i) && z == this.mHbmActive.get(i)) {
                return;
            }
            this.mHbmMode.put(i, i2);
            this.mHbmActive.put(i, z);
            recalculateVotesForDisplay(i);
        }

        public final void onDeviceConfigRefreshRateInHbmChanged() {
            int[] copyKeys = this.mHbmMode.copyKeys();
            if (copyKeys != null) {
                for (int i : copyKeys) {
                    recalculateVotesForDisplay(i);
                }
            }
        }

        public final void recalculateVotesForDisplay(int i) {
            int i2;
            int i3 = 0;
            Vote vote = null;
            if (this.mHbmActive.get(i, false)) {
                int i4 = this.mHbmMode.get(i, 0);
                if (i4 == 1) {
                    int i5 = this.mRefreshRateInHbmSunlight;
                    if (i5 > 0) {
                        vote = Vote.forPhysicalRefreshRates(i5, i5);
                    } else {
                        List refreshRateLimitations = this.mDisplayManagerInternal.getRefreshRateLimitations(i);
                        while (true) {
                            if (refreshRateLimitations == null || i3 >= refreshRateLimitations.size()) {
                                break;
                            }
                            DisplayManagerInternal.RefreshRateLimitation refreshRateLimitation = (DisplayManagerInternal.RefreshRateLimitation) refreshRateLimitations.get(i3);
                            if (refreshRateLimitation.type == 1) {
                                SurfaceControl.RefreshRateRange refreshRateRange = refreshRateLimitation.range;
                                vote = Vote.forPhysicalRefreshRates(refreshRateRange.min, refreshRateRange.max);
                                break;
                            }
                            i3++;
                        }
                    }
                } else if (i4 == 2 && (i2 = this.mRefreshRateInHbmHdr) > 0) {
                    vote = Vote.forPhysicalRefreshRates(i2, i2);
                } else {
                    Slog.w("DisplayModeDirector", "Unexpected HBM mode " + i4 + " for display ID " + i);
                }
            }
            this.mBallotBox.vote(i, 2, vote);
        }

        public void dumpLocked(PrintWriter printWriter) {
            printWriter.println("   HbmObserver");
            printWriter.println("     mHbmMode: " + this.mHbmMode);
            printWriter.println("     mHbmActive: " + this.mHbmActive);
            printWriter.println("     mRefreshRateInHbmSunlight: " + this.mRefreshRateInHbmSunlight);
            printWriter.println("     mRefreshRateInHbmHdr: " + this.mRefreshRateInHbmHdr);
        }
    }

    /* loaded from: classes.dex */
    public class DeviceConfigDisplaySettings implements DeviceConfig.OnPropertiesChangedListener {
        public DeviceConfigDisplaySettings() {
        }

        public void startListening() {
            DisplayModeDirector.this.mDeviceConfig.addOnPropertiesChangedListener("display_manager", BackgroundThread.getExecutor(), this);
        }

        public int[] getLowDisplayBrightnessThresholds() {
            return getIntArrayProperty("peak_refresh_rate_brightness_thresholds");
        }

        public int[] getLowAmbientBrightnessThresholds() {
            return getIntArrayProperty("peak_refresh_rate_ambient_thresholds");
        }

        public int getRefreshRateInLowZone() {
            return DisplayModeDirector.this.mDeviceConfig.getInt("display_manager", "refresh_rate_in_zone", -1);
        }

        public int[] getHighDisplayBrightnessThresholds() {
            return getIntArrayProperty("fixed_refresh_rate_high_display_brightness_thresholds");
        }

        public int[] getHighAmbientBrightnessThresholds() {
            return getIntArrayProperty("fixed_refresh_rate_high_ambient_brightness_thresholds");
        }

        public int getRefreshRateInHighZone() {
            return DisplayModeDirector.this.mDeviceConfig.getInt("display_manager", "refresh_rate_in_high_zone", -1);
        }

        public int getRefreshRateInHbmHdr(DisplayDeviceConfig displayDeviceConfig) {
            int defaultRefreshRateInHbmHdr;
            if (displayDeviceConfig == null) {
                defaultRefreshRateInHbmHdr = DisplayModeDirector.this.mContext.getResources().getInteger(17694803);
            } else {
                defaultRefreshRateInHbmHdr = displayDeviceConfig.getDefaultRefreshRateInHbmHdr();
            }
            try {
                return DisplayModeDirector.this.mDeviceConfig.getInt("display_manager", "refresh_rate_in_hbm_hdr", defaultRefreshRateInHbmHdr);
            } catch (NullPointerException unused) {
                return defaultRefreshRateInHbmHdr;
            }
        }

        public int getRefreshRateInHbmSunlight(DisplayDeviceConfig displayDeviceConfig) {
            int defaultRefreshRateInHbmSunlight;
            if (displayDeviceConfig == null) {
                defaultRefreshRateInHbmSunlight = DisplayModeDirector.this.mContext.getResources().getInteger(17694804);
            } else {
                defaultRefreshRateInHbmSunlight = displayDeviceConfig.getDefaultRefreshRateInHbmSunlight();
            }
            try {
                return DisplayModeDirector.this.mDeviceConfig.getInt("display_manager", "refresh_rate_in_hbm_sunlight", defaultRefreshRateInHbmSunlight);
            } catch (NullPointerException unused) {
                return defaultRefreshRateInHbmSunlight;
            }
        }

        public Float getDefaultPeakRefreshRate() {
            float f = DisplayModeDirector.this.mDeviceConfig.getFloat("display_manager", "peak_refresh_rate_default", -1.0f);
            if (f == -1.0f) {
                return null;
            }
            return Float.valueOf(f);
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            DisplayModeDirector.this.mHandler.obtainMessage(3, getDefaultPeakRefreshRate()).sendToTarget();
            int[] lowDisplayBrightnessThresholds = getLowDisplayBrightnessThresholds();
            int[] lowAmbientBrightnessThresholds = getLowAmbientBrightnessThresholds();
            int refreshRateInLowZone = getRefreshRateInLowZone();
            DisplayModeDirector.this.mHandler.obtainMessage(2, new Pair(lowDisplayBrightnessThresholds, lowAmbientBrightnessThresholds)).sendToTarget();
            if (refreshRateInLowZone != -1) {
                DisplayModeDirector.this.mHandler.obtainMessage(4, refreshRateInLowZone, 0).sendToTarget();
            }
            int[] highDisplayBrightnessThresholds = getHighDisplayBrightnessThresholds();
            int[] highAmbientBrightnessThresholds = getHighAmbientBrightnessThresholds();
            int refreshRateInHighZone = getRefreshRateInHighZone();
            DisplayModeDirector.this.mHandler.obtainMessage(6, new Pair(highDisplayBrightnessThresholds, highAmbientBrightnessThresholds)).sendToTarget();
            if (refreshRateInHighZone != -1) {
                DisplayModeDirector.this.mHandler.obtainMessage(5, refreshRateInHighZone, 0).sendToTarget();
            }
            synchronized (DisplayModeDirector.this.mLock) {
                DisplayModeDirector.this.mHandler.obtainMessage(7, getRefreshRateInHbmSunlight(DisplayModeDirector.this.mDefaultDisplayDeviceConfig), 0).sendToTarget();
                DisplayModeDirector.this.mHandler.obtainMessage(8, getRefreshRateInHbmHdr(DisplayModeDirector.this.mDefaultDisplayDeviceConfig), 0).sendToTarget();
            }
        }

        public final int[] getIntArrayProperty(String str) {
            String string = DisplayModeDirector.this.mDeviceConfig.getString("display_manager", str, (String) null);
            if (string != null) {
                return parseIntArray(string);
            }
            return null;
        }

        public final int[] parseIntArray(String str) {
            String[] split = str.split(",");
            int length = split.length;
            int[] iArr = new int[length];
            for (int i = 0; i < length; i++) {
                try {
                    iArr[i] = Integer.parseInt(split[i]);
                } catch (NumberFormatException e) {
                    Slog.e("DisplayModeDirector", "Incorrect format for array: '" + str + "'", e);
                    return null;
                }
            }
            return iArr;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class RealInjector implements Injector {
        public final Context mContext;
        public DisplayManager mDisplayManager;

        public RealInjector(Context context) {
            this.mContext = context;
        }

        @Override // com.android.server.display.mode.DisplayModeDirector.Injector
        public DeviceConfigInterface getDeviceConfig() {
            return DeviceConfigInterface.REAL;
        }

        @Override // com.android.server.display.mode.DisplayModeDirector.Injector
        public void registerPeakRefreshRateObserver(ContentResolver contentResolver, ContentObserver contentObserver) {
            contentResolver.registerContentObserver(Injector.PEAK_REFRESH_RATE_URI, false, contentObserver, 0);
        }

        @Override // com.android.server.display.mode.DisplayModeDirector.Injector
        public void registerDisplayListener(DisplayManager.DisplayListener displayListener, Handler handler, long j) {
            getDisplayManager().registerDisplayListener(displayListener, handler, j);
        }

        @Override // com.android.server.display.mode.DisplayModeDirector.Injector
        public Display[] getDisplays() {
            return getDisplayManager().getDisplays("android.hardware.display.category.ALL_INCLUDING_DISABLED");
        }

        @Override // com.android.server.display.mode.DisplayModeDirector.Injector
        public boolean getDisplayInfo(int i, DisplayInfo displayInfo) {
            Display display = getDisplayManager().getDisplay(i);
            if (display != null) {
                return display.getDisplayInfo(displayInfo);
            }
            return false;
        }

        @Override // com.android.server.display.mode.DisplayModeDirector.Injector
        public BrightnessInfo getBrightnessInfo(int i) {
            Display display = getDisplayManager().getDisplay(i);
            if (display != null) {
                return display.getBrightnessInfo();
            }
            return null;
        }

        @Override // com.android.server.display.mode.DisplayModeDirector.Injector
        public boolean isDozeState(Display display) {
            if (display == null) {
                return false;
            }
            return Display.isDozeState(display.getState());
        }

        @Override // com.android.server.display.mode.DisplayModeDirector.Injector
        public boolean registerThermalServiceListener(IThermalEventListener iThermalEventListener) {
            IThermalService thermalService = getThermalService();
            if (thermalService == null) {
                Slog.w("DisplayModeDirector", "Could not observe thermal status. Service not available");
                return false;
            }
            try {
                thermalService.registerThermalEventListenerWithType(iThermalEventListener, 3);
                return true;
            } catch (RemoteException e) {
                Slog.e("DisplayModeDirector", "Failed to register thermal status listener", e);
                return false;
            }
        }

        @Override // com.android.server.display.mode.DisplayModeDirector.Injector
        public boolean supportsFrameRateOverride() {
            return SurfaceFlingerProperties.enable_frame_rate_override().orElse(Boolean.TRUE).booleanValue();
        }

        public final DisplayManager getDisplayManager() {
            if (this.mDisplayManager == null) {
                this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
            }
            return this.mDisplayManager;
        }

        public final IThermalService getThermalService() {
            return IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
        }
    }
}
