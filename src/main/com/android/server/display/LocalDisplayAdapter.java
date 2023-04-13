package com.android.server.display;

import android.app.ActivityThread;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.sidekick.SidekickInternal;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemProperties;
import android.os.Trace;
import android.p005os.IInstalld;
import android.util.DisplayUtils;
import android.util.LongSparseArray;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayAddress;
import android.view.DisplayCutout;
import android.view.DisplayEventReceiver;
import android.view.DisplayShape;
import android.view.RoundedCorners;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.display.BrightnessSynchronizer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.LocalServices;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;
import com.android.server.display.LocalDisplayAdapter;
import com.android.server.display.mode.DisplayModeDirector;
import com.android.server.lights.LightsManager;
import com.android.server.lights.LogicalLight;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class LocalDisplayAdapter extends DisplayAdapter {
    public final LongSparseArray<LocalDisplayDevice> mDevices;
    public final Injector mInjector;
    public final boolean mIsBootDisplayModeSupported;
    public Context mOverlayContext;
    public final SurfaceControlProxy mSurfaceControlProxy;

    /* loaded from: classes.dex */
    public interface DisplayEventListener {
        void onFrameRateOverridesChanged(long j, long j2, DisplayEventReceiver.FrameRateOverride[] frameRateOverrideArr);

        void onHotplug(long j, long j2, boolean z);

        void onModeChanged(long j, long j2, int i, long j3);
    }

    public static int getPowerModeForState(int i) {
        if (i != 1) {
            if (i != 6) {
                if (i != 3) {
                    return i != 4 ? 2 : 3;
                }
                return 1;
            }
            return 4;
        }
        return 0;
    }

    public LocalDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener) {
        this(syncRoot, context, handler, listener, new Injector());
    }

    @VisibleForTesting
    public LocalDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener, Injector injector) {
        super(syncRoot, context, handler, listener, "LocalDisplayAdapter");
        this.mDevices = new LongSparseArray<>();
        this.mInjector = injector;
        SurfaceControlProxy surfaceControlProxy = injector.getSurfaceControlProxy();
        this.mSurfaceControlProxy = surfaceControlProxy;
        this.mIsBootDisplayModeSupported = surfaceControlProxy.getBootDisplayModeSupport();
    }

    @Override // com.android.server.display.DisplayAdapter
    public void registerLocked() {
        super.registerLocked();
        this.mInjector.setDisplayEventListenerLocked(getHandler().getLooper(), new LocalDisplayEventListener());
        for (long j : this.mSurfaceControlProxy.getPhysicalDisplayIds()) {
            tryConnectDisplayLocked(j);
        }
    }

    public final void tryConnectDisplayLocked(long j) {
        IBinder physicalDisplayToken = this.mSurfaceControlProxy.getPhysicalDisplayToken(j);
        if (physicalDisplayToken != null) {
            SurfaceControl.StaticDisplayInfo staticDisplayInfo = this.mSurfaceControlProxy.getStaticDisplayInfo(j);
            if (staticDisplayInfo == null) {
                Slog.w("LocalDisplayAdapter", "No valid static info found for display device " + j);
                return;
            }
            SurfaceControl.DynamicDisplayInfo dynamicDisplayInfo = this.mSurfaceControlProxy.getDynamicDisplayInfo(j);
            if (dynamicDisplayInfo == null) {
                Slog.w("LocalDisplayAdapter", "No valid dynamic info found for display device " + j);
            } else if (dynamicDisplayInfo.supportedDisplayModes == null) {
                Slog.w("LocalDisplayAdapter", "No valid modes found for display device " + j);
            } else if (dynamicDisplayInfo.activeDisplayModeId < 0) {
                Slog.w("LocalDisplayAdapter", "No valid active mode found for display device " + j);
            } else {
                if (dynamicDisplayInfo.activeColorMode < 0) {
                    Slog.w("LocalDisplayAdapter", "No valid active color mode for display device " + j);
                    dynamicDisplayInfo.activeColorMode = -1;
                }
                SurfaceControl.DesiredDisplayModeSpecs desiredDisplayModeSpecs = this.mSurfaceControlProxy.getDesiredDisplayModeSpecs(physicalDisplayToken);
                LocalDisplayDevice localDisplayDevice = this.mDevices.get(j);
                if (localDisplayDevice == null) {
                    LocalDisplayDevice localDisplayDevice2 = new LocalDisplayDevice(physicalDisplayToken, j, staticDisplayInfo, dynamicDisplayInfo, desiredDisplayModeSpecs, this.mDevices.size() == 0);
                    this.mDevices.put(j, localDisplayDevice2);
                    sendDisplayDeviceEventLocked(localDisplayDevice2, 1);
                } else if (localDisplayDevice.updateDisplayPropertiesLocked(staticDisplayInfo, dynamicDisplayInfo, desiredDisplayModeSpecs)) {
                    sendDisplayDeviceEventLocked(localDisplayDevice, 2);
                }
            }
        }
    }

    public final void tryDisconnectDisplayLocked(long j) {
        LocalDisplayDevice localDisplayDevice = this.mDevices.get(j);
        if (localDisplayDevice != null) {
            this.mDevices.remove(j);
            sendDisplayDeviceEventLocked(localDisplayDevice, 3);
        }
    }

    /* loaded from: classes.dex */
    public final class LocalDisplayDevice extends DisplayDevice {
        public int mActiveColorMode;
        public int mActiveModeId;
        public float mActiveRenderFrameRate;
        public SurfaceControl.DisplayMode mActiveSfDisplayMode;
        public int mActiveSfDisplayModeAtStartId;
        public boolean mAllmRequested;
        public boolean mAllmSupported;
        public final BacklightAdapter mBacklightAdapter;
        public float mBrightnessState;
        public int mCommittedState;
        public float mCurrentHdrSdrRatio;
        public int mDefaultModeGroup;
        public int mDefaultModeId;
        public final DisplayModeDirector.DesiredDisplayModeSpecs mDisplayModeSpecs;
        public boolean mDisplayModeSpecsInvalid;
        public DisplayEventReceiver.FrameRateOverride[] mFrameRateOverrides;
        public boolean mGameContentTypeRequested;
        public boolean mGameContentTypeSupported;
        public boolean mHavePendingChanges;
        public Display.HdrCapabilities mHdrCapabilities;
        public DisplayDeviceInfo mInfo;
        public final boolean mIsFirstDisplay;
        public final long mPhysicalDisplayId;
        public float mSdrBrightnessState;
        public SurfaceControl.DisplayMode[] mSfDisplayModes;
        public boolean mSidekickActive;
        public final SidekickInternal mSidekickInternal;
        public int mState;
        public SurfaceControl.StaticDisplayInfo mStaticDisplayInfo;
        public final ArrayList<Integer> mSupportedColorModes;
        public final SparseArray<DisplayModeRecord> mSupportedModes;
        public int mSystemPreferredModeId;
        public Display.Mode mUserPreferredMode;
        public int mUserPreferredModeId;

        @Override // com.android.server.display.DisplayDevice
        public boolean hasStableUniqueId() {
            return true;
        }

        public LocalDisplayDevice(IBinder iBinder, long j, SurfaceControl.StaticDisplayInfo staticDisplayInfo, SurfaceControl.DynamicDisplayInfo dynamicDisplayInfo, SurfaceControl.DesiredDisplayModeSpecs desiredDisplayModeSpecs, boolean z) {
            super(LocalDisplayAdapter.this, iBinder, "local:" + j, LocalDisplayAdapter.this.getContext());
            this.mSupportedModes = new SparseArray<>();
            this.mSupportedColorModes = new ArrayList<>();
            this.mDisplayModeSpecs = new DisplayModeDirector.DesiredDisplayModeSpecs();
            this.mState = 0;
            this.mCommittedState = 0;
            this.mBrightnessState = Float.NaN;
            this.mSdrBrightnessState = Float.NaN;
            this.mCurrentHdrSdrRatio = Float.NaN;
            this.mDefaultModeId = -1;
            this.mSystemPreferredModeId = -1;
            this.mUserPreferredModeId = -1;
            this.mActiveSfDisplayModeAtStartId = -1;
            this.mActiveModeId = -1;
            this.mFrameRateOverrides = new DisplayEventReceiver.FrameRateOverride[0];
            this.mPhysicalDisplayId = j;
            this.mIsFirstDisplay = z;
            updateDisplayPropertiesLocked(staticDisplayInfo, dynamicDisplayInfo, desiredDisplayModeSpecs);
            this.mSidekickInternal = (SidekickInternal) LocalServices.getService(SidekickInternal.class);
            this.mBacklightAdapter = new BacklightAdapter(iBinder, z, LocalDisplayAdapter.this.mSurfaceControlProxy);
            this.mActiveSfDisplayModeAtStartId = dynamicDisplayInfo.activeDisplayModeId;
        }

        @Override // com.android.server.display.DisplayDevice
        public Display.Mode getActiveDisplayModeAtStartLocked() {
            return findMode(findMatchingModeIdLocked(this.mActiveSfDisplayModeAtStartId));
        }

        public boolean updateDisplayPropertiesLocked(SurfaceControl.StaticDisplayInfo staticDisplayInfo, SurfaceControl.DynamicDisplayInfo dynamicDisplayInfo, SurfaceControl.DesiredDisplayModeSpecs desiredDisplayModeSpecs) {
            boolean updateStaticInfo = updateStaticInfo(staticDisplayInfo) | updateDisplayModesLocked(dynamicDisplayInfo.supportedDisplayModes, dynamicDisplayInfo.preferredBootDisplayMode, dynamicDisplayInfo.activeDisplayModeId, dynamicDisplayInfo.renderFrameRate, desiredDisplayModeSpecs) | updateColorModesLocked(dynamicDisplayInfo.supportedColorModes, dynamicDisplayInfo.activeColorMode) | updateHdrCapabilitiesLocked(dynamicDisplayInfo.hdrCapabilities) | updateAllmSupport(dynamicDisplayInfo.autoLowLatencyModeSupported) | updateGameContentTypeSupport(dynamicDisplayInfo.gameContentTypeSupported);
            if (updateStaticInfo) {
                this.mHavePendingChanges = true;
            }
            return updateStaticInfo;
        }

        /* JADX WARN: Code restructure failed: missing block: B:86:0x0191, code lost:
            if (r16.mDisplayModeSpecs.appRequest.equals(r21.appRequestRanges) != false) goto L96;
         */
        /* JADX WARN: Removed duplicated region for block: B:100:0x01b6  */
        /* JADX WARN: Removed duplicated region for block: B:79:0x016f  */
        /* JADX WARN: Removed duplicated region for block: B:95:0x01ae  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public boolean updateDisplayModesLocked(SurfaceControl.DisplayMode[] displayModeArr, int i, int i2, float f, SurfaceControl.DesiredDisplayModeSpecs desiredDisplayModeSpecs) {
            DisplayModeRecord displayModeRecord;
            DisplayModeRecord displayModeRecord2;
            boolean z;
            int i3;
            boolean z2;
            float f2;
            boolean z3;
            boolean z4;
            this.mSfDisplayModes = (SurfaceControl.DisplayMode[]) Arrays.copyOf(displayModeArr, displayModeArr.length);
            this.mActiveSfDisplayMode = getModeById(displayModeArr, i2);
            SurfaceControl.DisplayMode modeById = getModeById(displayModeArr, i);
            ArrayList arrayList = new ArrayList();
            int i4 = 0;
            boolean z5 = false;
            while (i4 < displayModeArr.length) {
                SurfaceControl.DisplayMode displayMode = displayModeArr[i4];
                ArrayList arrayList2 = new ArrayList();
                int i5 = 0;
                while (i5 < displayModeArr.length) {
                    SurfaceControl.DisplayMode displayMode2 = displayModeArr[i5];
                    if (i5 != i4 && displayMode2.width == displayMode.width && displayMode2.height == displayMode.height && displayMode2.refreshRate != displayMode.refreshRate && displayMode2.group == displayMode.group) {
                        arrayList2.add(Float.valueOf(displayMode2.refreshRate));
                    }
                    i5++;
                }
                Collections.sort(arrayList2);
                Iterator it = arrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z4 = false;
                        break;
                    }
                    DisplayModeRecord displayModeRecord3 = (DisplayModeRecord) it.next();
                    if (displayModeRecord3.hasMatchingMode(displayMode) && refreshRatesEquals(arrayList2, displayModeRecord3.mMode.getAlternativeRefreshRates())) {
                        z4 = true;
                        break;
                    }
                }
                if (!z4) {
                    DisplayModeRecord findDisplayModeRecord = findDisplayModeRecord(displayMode, arrayList2);
                    if (findDisplayModeRecord == null) {
                        int size = arrayList2.size();
                        float[] fArr = new float[size];
                        for (int i6 = 0; i6 < size; i6++) {
                            fArr[i6] = arrayList2.get(i6).floatValue();
                        }
                        findDisplayModeRecord = new DisplayModeRecord(displayMode, fArr);
                        z5 = true;
                    }
                    arrayList.add(findDisplayModeRecord);
                }
                i4++;
            }
            Iterator it2 = arrayList.iterator();
            while (true) {
                displayModeRecord = null;
                if (!it2.hasNext()) {
                    displayModeRecord2 = null;
                    break;
                }
                displayModeRecord2 = (DisplayModeRecord) it2.next();
                if (displayModeRecord2.hasMatchingMode(this.mActiveSfDisplayMode)) {
                    break;
                }
            }
            if (i != -1 && modeById != null) {
                Iterator it3 = arrayList.iterator();
                while (true) {
                    if (!it3.hasNext()) {
                        break;
                    }
                    DisplayModeRecord displayModeRecord4 = (DisplayModeRecord) it3.next();
                    if (displayModeRecord4.hasMatchingMode(modeById)) {
                        displayModeRecord = displayModeRecord4;
                        break;
                    }
                }
                if (displayModeRecord != null) {
                    int modeId = displayModeRecord.mMode.getModeId();
                    if (LocalDisplayAdapter.this.mIsBootDisplayModeSupported && this.mSystemPreferredModeId != modeId) {
                        this.mSystemPreferredModeId = modeId;
                        z = true;
                        i3 = this.mActiveModeId;
                        if (i3 != -1 || i3 == displayModeRecord2.mMode.getModeId()) {
                            z2 = false;
                        } else {
                            Slog.d("LocalDisplayAdapter", "The active mode was changed from SurfaceFlinger or the display device to " + displayModeRecord2.mMode);
                            this.mActiveModeId = displayModeRecord2.mMode.getModeId();
                            LocalDisplayAdapter.this.sendTraversalRequestLocked();
                            z2 = true;
                        }
                        f2 = this.mActiveRenderFrameRate;
                        if (f2 > 0.0f || f2 == f) {
                            z3 = false;
                        } else {
                            Slog.d("LocalDisplayAdapter", "The render frame rate was changed from SurfaceFlinger or the display device to " + f);
                            this.mActiveRenderFrameRate = f;
                            LocalDisplayAdapter.this.sendTraversalRequestLocked();
                            z3 = true;
                        }
                        if (this.mDisplayModeSpecs.baseModeId != -1) {
                            int findMatchingModeIdLocked = findMatchingModeIdLocked(desiredDisplayModeSpecs.defaultMode);
                            if (findMatchingModeIdLocked != -1) {
                                DisplayModeDirector.DesiredDisplayModeSpecs desiredDisplayModeSpecs2 = this.mDisplayModeSpecs;
                                if (desiredDisplayModeSpecs2.baseModeId == findMatchingModeIdLocked) {
                                    if (desiredDisplayModeSpecs2.primary.equals(desiredDisplayModeSpecs.primaryRanges)) {
                                    }
                                }
                            }
                            this.mDisplayModeSpecsInvalid = true;
                            LocalDisplayAdapter.this.sendTraversalRequestLocked();
                        }
                        if (arrayList.size() == this.mSupportedModes.size() || z5) {
                            return z2 || z || z3;
                        }
                        this.mSupportedModes.clear();
                        Iterator it4 = arrayList.iterator();
                        while (it4.hasNext()) {
                            DisplayModeRecord displayModeRecord5 = (DisplayModeRecord) it4.next();
                            this.mSupportedModes.put(displayModeRecord5.mMode.getModeId(), displayModeRecord5);
                        }
                        int i7 = this.mDefaultModeId;
                        if (i7 == -1) {
                            this.mDefaultModeId = displayModeRecord2.mMode.getModeId();
                            this.mDefaultModeGroup = this.mActiveSfDisplayMode.group;
                            this.mActiveRenderFrameRate = f;
                        } else if (z5 && z2) {
                            Slog.d("LocalDisplayAdapter", "New display modes are added and the active mode has changed, use active mode as default mode.");
                            this.mDefaultModeId = displayModeRecord2.mMode.getModeId();
                            this.mDefaultModeGroup = this.mActiveSfDisplayMode.group;
                            this.mActiveRenderFrameRate = f;
                        } else if (findSfDisplayModeIdLocked(i7, this.mDefaultModeGroup) < 0) {
                            Slog.w("LocalDisplayAdapter", "Default display mode no longer available, using currently active mode as default.");
                            this.mDefaultModeId = displayModeRecord2.mMode.getModeId();
                            this.mDefaultModeGroup = this.mActiveSfDisplayMode.group;
                            this.mActiveRenderFrameRate = f;
                        }
                        if (this.mSupportedModes.indexOfKey(this.mDisplayModeSpecs.baseModeId) < 0) {
                            if (this.mDisplayModeSpecs.baseModeId != -1) {
                                Slog.w("LocalDisplayAdapter", "DisplayModeSpecs base mode no longer available, using currently active mode.");
                            }
                            this.mDisplayModeSpecs.baseModeId = displayModeRecord2.mMode.getModeId();
                            this.mDisplayModeSpecsInvalid = true;
                        }
                        Display.Mode mode = this.mUserPreferredMode;
                        if (mode != null) {
                            this.mUserPreferredModeId = findUserPreferredModeIdLocked(mode);
                        }
                        if (this.mSupportedModes.indexOfKey(this.mActiveModeId) < 0) {
                            if (this.mActiveModeId != -1) {
                                Slog.w("LocalDisplayAdapter", "Active display mode no longer available, reverting to default mode.");
                            }
                            this.mActiveModeId = getPreferredModeId();
                        }
                        LocalDisplayAdapter.this.sendTraversalRequestLocked();
                        return true;
                    }
                }
            }
            z = false;
            i3 = this.mActiveModeId;
            if (i3 != -1) {
            }
            z2 = false;
            f2 = this.mActiveRenderFrameRate;
            if (f2 > 0.0f) {
            }
            z3 = false;
            if (this.mDisplayModeSpecs.baseModeId != -1) {
            }
            if (arrayList.size() == this.mSupportedModes.size() || z5) {
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public DisplayDeviceConfig getDisplayDeviceConfig() {
            if (this.mDisplayDeviceConfig == null) {
                loadDisplayDeviceConfig();
            }
            return this.mDisplayDeviceConfig;
        }

        public final int getPreferredModeId() {
            int i = this.mUserPreferredModeId;
            return i != -1 ? i : this.mDefaultModeId;
        }

        public final int getLogicalDensity() {
            DensityMapping densityMapping = getDisplayDeviceConfig().getDensityMapping();
            if (densityMapping == null) {
                return (int) ((this.mStaticDisplayInfo.density * 160.0f) + 0.5d);
            }
            DisplayDeviceInfo displayDeviceInfo = this.mInfo;
            return densityMapping.getDensityForResolution(displayDeviceInfo.width, displayDeviceInfo.height);
        }

        public final void loadDisplayDeviceConfig() {
            DisplayDeviceConfig create = DisplayDeviceConfig.create(LocalDisplayAdapter.this.getOverlayContext(), this.mPhysicalDisplayId, this.mIsFirstDisplay);
            this.mDisplayDeviceConfig = create;
            this.mBacklightAdapter.setForceSurfaceControl(create.hasQuirk("canSetBrightnessViaHwc"));
        }

        public final boolean updateStaticInfo(SurfaceControl.StaticDisplayInfo staticDisplayInfo) {
            if (Objects.equals(this.mStaticDisplayInfo, staticDisplayInfo)) {
                return false;
            }
            this.mStaticDisplayInfo = staticDisplayInfo;
            return true;
        }

        public final boolean updateColorModesLocked(int[] iArr, int i) {
            if (iArr == null) {
                return false;
            }
            ArrayList arrayList = new ArrayList();
            boolean z = false;
            for (int i2 : iArr) {
                if (!this.mSupportedColorModes.contains(Integer.valueOf(i2))) {
                    z = true;
                }
                arrayList.add(Integer.valueOf(i2));
            }
            if (arrayList.size() != this.mSupportedColorModes.size() || z) {
                this.mSupportedColorModes.clear();
                this.mSupportedColorModes.addAll(arrayList);
                Collections.sort(this.mSupportedColorModes);
                if (!this.mSupportedColorModes.contains(Integer.valueOf(this.mActiveColorMode))) {
                    if (this.mActiveColorMode != 0) {
                        Slog.w("LocalDisplayAdapter", "Active color mode no longer available, reverting to default mode.");
                        this.mActiveColorMode = 0;
                    } else if (!this.mSupportedColorModes.isEmpty()) {
                        Slog.e("LocalDisplayAdapter", "Default and active color mode is no longer available! Reverting to first available mode.");
                        this.mActiveColorMode = this.mSupportedColorModes.get(0).intValue();
                    } else {
                        Slog.e("LocalDisplayAdapter", "No color modes available!");
                    }
                }
                return true;
            }
            return false;
        }

        public final boolean updateHdrCapabilitiesLocked(Display.HdrCapabilities hdrCapabilities) {
            if (Objects.equals(this.mHdrCapabilities, hdrCapabilities)) {
                return false;
            }
            this.mHdrCapabilities = hdrCapabilities;
            return true;
        }

        public final boolean updateAllmSupport(boolean z) {
            if (this.mAllmSupported == z) {
                return false;
            }
            this.mAllmSupported = z;
            return true;
        }

        public final boolean updateGameContentTypeSupport(boolean z) {
            if (this.mGameContentTypeSupported == z) {
                return false;
            }
            this.mGameContentTypeSupported = z;
            return true;
        }

        public final SurfaceControl.DisplayMode getModeById(SurfaceControl.DisplayMode[] displayModeArr, int i) {
            for (SurfaceControl.DisplayMode displayMode : displayModeArr) {
                if (displayMode.id == i) {
                    return displayMode;
                }
            }
            Slog.e("LocalDisplayAdapter", "Can't find display mode with id " + i);
            return null;
        }

        public final DisplayModeRecord findDisplayModeRecord(SurfaceControl.DisplayMode displayMode, List<Float> list) {
            for (int i = 0; i < this.mSupportedModes.size(); i++) {
                DisplayModeRecord valueAt = this.mSupportedModes.valueAt(i);
                if (valueAt.hasMatchingMode(displayMode) && refreshRatesEquals(list, valueAt.mMode.getAlternativeRefreshRates()) && LocalDisplayAdapter.this.hdrTypesEqual(displayMode.supportedHdrTypes, valueAt.mMode.getSupportedHdrTypes())) {
                    return valueAt;
                }
            }
            return null;
        }

        public final boolean refreshRatesEquals(List<Float> list, float[] fArr) {
            if (list.size() != fArr.length) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                if (Float.floatToIntBits(list.get(i).floatValue()) != Float.floatToIntBits(fArr[i])) {
                    return false;
                }
            }
            return true;
        }

        @Override // com.android.server.display.DisplayDevice
        public void applyPendingDisplayDeviceInfoChangesLocked() {
            if (this.mHavePendingChanges) {
                this.mInfo = null;
                this.mHavePendingChanges = false;
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public DisplayDeviceInfo getDisplayDeviceInfoLocked() {
            if (this.mInfo == null) {
                DisplayDeviceInfo displayDeviceInfo = new DisplayDeviceInfo();
                this.mInfo = displayDeviceInfo;
                SurfaceControl.DisplayMode displayMode = this.mActiveSfDisplayMode;
                displayDeviceInfo.width = displayMode.width;
                displayDeviceInfo.height = displayMode.height;
                displayDeviceInfo.modeId = this.mActiveModeId;
                displayDeviceInfo.renderFrameRate = this.mActiveRenderFrameRate;
                displayDeviceInfo.defaultModeId = getPreferredModeId();
                this.mInfo.supportedModes = getDisplayModes(this.mSupportedModes);
                DisplayDeviceInfo displayDeviceInfo2 = this.mInfo;
                displayDeviceInfo2.colorMode = this.mActiveColorMode;
                displayDeviceInfo2.allmSupported = this.mAllmSupported;
                displayDeviceInfo2.gameContentTypeSupported = this.mGameContentTypeSupported;
                displayDeviceInfo2.supportedColorModes = new int[this.mSupportedColorModes.size()];
                for (int i = 0; i < this.mSupportedColorModes.size(); i++) {
                    this.mInfo.supportedColorModes[i] = this.mSupportedColorModes.get(i).intValue();
                }
                DisplayDeviceInfo displayDeviceInfo3 = this.mInfo;
                displayDeviceInfo3.hdrCapabilities = this.mHdrCapabilities;
                SurfaceControl.DisplayMode displayMode2 = this.mActiveSfDisplayMode;
                displayDeviceInfo3.appVsyncOffsetNanos = displayMode2.appVsyncOffsetNanos;
                displayDeviceInfo3.presentationDeadlineNanos = displayMode2.presentationDeadlineNanos;
                displayDeviceInfo3.state = this.mState;
                displayDeviceInfo3.committedState = this.mCommittedState;
                displayDeviceInfo3.uniqueId = getUniqueId();
                DisplayAddress.Physical fromPhysicalDisplayId = DisplayAddress.fromPhysicalDisplayId(this.mPhysicalDisplayId);
                DisplayDeviceInfo displayDeviceInfo4 = this.mInfo;
                displayDeviceInfo4.address = fromPhysicalDisplayId;
                displayDeviceInfo4.densityDpi = getLogicalDensity();
                DisplayDeviceInfo displayDeviceInfo5 = this.mInfo;
                SurfaceControl.DisplayMode displayMode3 = this.mActiveSfDisplayMode;
                displayDeviceInfo5.xDpi = displayMode3.xDpi;
                displayDeviceInfo5.yDpi = displayMode3.yDpi;
                SurfaceControl.StaticDisplayInfo staticDisplayInfo = this.mStaticDisplayInfo;
                displayDeviceInfo5.deviceProductInfo = staticDisplayInfo.deviceProductInfo;
                if (staticDisplayInfo.secure) {
                    displayDeviceInfo5.flags = 12;
                }
                Resources resources = LocalDisplayAdapter.this.getOverlayContext().getResources();
                this.mInfo.flags |= 1;
                if (this.mIsFirstDisplay) {
                    if (resources.getBoolean(17891736) || (Build.IS_EMULATOR && SystemProperties.getBoolean("ro.emulator.circular", false))) {
                        this.mInfo.flags |= 256;
                    }
                } else {
                    if (!resources.getBoolean(17891728)) {
                        this.mInfo.flags |= 128;
                    }
                    if (isDisplayPrivate(fromPhysicalDisplayId)) {
                        this.mInfo.flags |= 16;
                    }
                }
                if (DisplayCutout.getMaskBuiltInDisplayCutout(resources, this.mInfo.uniqueId)) {
                    this.mInfo.flags |= IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES;
                }
                Display.Mode maximumResolutionDisplayMode = DisplayUtils.getMaximumResolutionDisplayMode(this.mInfo.supportedModes);
                int physicalWidth = maximumResolutionDisplayMode == null ? this.mInfo.width : maximumResolutionDisplayMode.getPhysicalWidth();
                int physicalHeight = maximumResolutionDisplayMode == null ? this.mInfo.height : maximumResolutionDisplayMode.getPhysicalHeight();
                DisplayDeviceInfo displayDeviceInfo6 = this.mInfo;
                int i2 = physicalWidth;
                int i3 = physicalHeight;
                displayDeviceInfo6.displayCutout = DisplayCutout.fromResourcesRectApproximation(resources, displayDeviceInfo6.uniqueId, i2, i3, displayDeviceInfo6.width, displayDeviceInfo6.height);
                DisplayDeviceInfo displayDeviceInfo7 = this.mInfo;
                displayDeviceInfo7.roundedCorners = RoundedCorners.fromResources(resources, displayDeviceInfo7.uniqueId, i2, i3, displayDeviceInfo7.width, displayDeviceInfo7.height);
                DisplayDeviceInfo displayDeviceInfo8 = this.mInfo;
                displayDeviceInfo8.installOrientation = this.mStaticDisplayInfo.installOrientation;
                displayDeviceInfo8.displayShape = DisplayShape.fromResources(resources, displayDeviceInfo8.uniqueId, i2, i3, displayDeviceInfo8.width, displayDeviceInfo8.height);
                this.mInfo.name = getDisplayDeviceConfig().getName();
                if (this.mStaticDisplayInfo.isInternal) {
                    DisplayDeviceInfo displayDeviceInfo9 = this.mInfo;
                    displayDeviceInfo9.type = 1;
                    displayDeviceInfo9.touch = 1;
                    displayDeviceInfo9.flags = 2 | displayDeviceInfo9.flags;
                    if (displayDeviceInfo9.name == null) {
                        displayDeviceInfo9.name = resources.getString(17040133);
                    }
                } else {
                    DisplayDeviceInfo displayDeviceInfo10 = this.mInfo;
                    displayDeviceInfo10.type = 2;
                    displayDeviceInfo10.touch = 2;
                    displayDeviceInfo10.flags |= 64;
                    if (displayDeviceInfo10.name == null) {
                        displayDeviceInfo10.name = LocalDisplayAdapter.this.getContext().getResources().getString(17040134);
                    }
                }
                DisplayDeviceInfo displayDeviceInfo11 = this.mInfo;
                displayDeviceInfo11.frameRateOverrides = this.mFrameRateOverrides;
                displayDeviceInfo11.flags |= IInstalld.FLAG_FORCE;
                displayDeviceInfo11.brightnessMinimum = 0.0f;
                displayDeviceInfo11.brightnessMaximum = 1.0f;
                displayDeviceInfo11.brightnessDefault = getDisplayDeviceConfig().getBrightnessDefault();
                this.mInfo.hdrSdrRatio = this.mCurrentHdrSdrRatio;
            }
            return this.mInfo;
        }

        @Override // com.android.server.display.DisplayDevice
        public Runnable requestDisplayStateLocked(final int i, final float f, final float f2) {
            final boolean z = false;
            boolean z2 = this.mState != i;
            if (this.mBrightnessState != f || this.mSdrBrightnessState != f2) {
                z = true;
            }
            if (z2 || z) {
                final long j = this.mPhysicalDisplayId;
                final IBinder displayTokenLocked = getDisplayTokenLocked();
                final int i2 = this.mState;
                if (z2) {
                    this.mState = i;
                    updateDeviceInfoLocked();
                }
                return new Runnable() { // from class: com.android.server.display.LocalDisplayAdapter.LocalDisplayDevice.1
                    @Override // java.lang.Runnable
                    public void run() {
                        int i3;
                        int i4 = i2;
                        if (Display.isSuspendedState(i4) || i2 == 0) {
                            if (!Display.isSuspendedState(i)) {
                                setDisplayState(i);
                                i4 = i;
                            } else {
                                int i5 = i;
                                if (i5 == 4 || (i3 = i2) == 4) {
                                    i4 = 3;
                                    setDisplayState(3);
                                } else if (i5 == 6 || i3 == 6) {
                                    i4 = 2;
                                    setDisplayState(2);
                                } else if (i3 != 0) {
                                    return;
                                }
                            }
                        }
                        if (z) {
                            setDisplayBrightness(f, f2);
                            LocalDisplayDevice.this.mBrightnessState = f;
                            LocalDisplayDevice.this.mSdrBrightnessState = f2;
                        }
                        int i6 = i;
                        if (i6 != i4) {
                            setDisplayState(i6);
                        }
                    }

                    public final void setDisplayState(int i3) {
                        if (LocalDisplayDevice.this.mSidekickActive) {
                            Trace.traceBegin(131072L, "SidekickInternal#endDisplayControl");
                            try {
                                LocalDisplayDevice.this.mSidekickInternal.endDisplayControl();
                                Trace.traceEnd(131072L);
                                LocalDisplayDevice.this.mSidekickActive = false;
                            } finally {
                            }
                        }
                        int powerModeForState = LocalDisplayAdapter.getPowerModeForState(i3);
                        Trace.traceBegin(131072L, "setDisplayState(id=" + j + ", state=" + Display.stateToString(i3) + ")");
                        try {
                            LocalDisplayAdapter.this.mSurfaceControlProxy.setDisplayPowerMode(displayTokenLocked, powerModeForState);
                            Trace.traceCounter(131072L, "DisplayPowerMode", powerModeForState);
                            Trace.traceEnd(131072L);
                            setCommittedState(i3);
                            if (!Display.isSuspendedState(i3) || i3 == 1 || LocalDisplayDevice.this.mSidekickInternal == null || LocalDisplayDevice.this.mSidekickActive) {
                                return;
                            }
                            Trace.traceBegin(131072L, "SidekickInternal#startDisplayControl");
                            try {
                                LocalDisplayDevice localDisplayDevice = LocalDisplayDevice.this;
                                localDisplayDevice.mSidekickActive = localDisplayDevice.mSidekickInternal.startDisplayControl(i3);
                            } finally {
                            }
                        } finally {
                        }
                    }

                    public final void setCommittedState(int i3) {
                        synchronized (LocalDisplayAdapter.this.getSyncRoot()) {
                            LocalDisplayDevice.this.mCommittedState = i3;
                            LocalDisplayDevice.this.updateDeviceInfoLocked();
                        }
                    }

                    public final void setDisplayBrightness(float f3, float f4) {
                        if (Float.isNaN(f3) || Float.isNaN(f4)) {
                            return;
                        }
                        Trace.traceBegin(131072L, "setDisplayBrightness(id=" + j + ", brightnessState=" + f3 + ", sdrBrightnessState=" + f4 + ")");
                        try {
                            float brightnessToBacklight = brightnessToBacklight(f3);
                            float brightnessToBacklight2 = brightnessToBacklight(f4);
                            float backlightToNits = backlightToNits(brightnessToBacklight);
                            float backlightToNits2 = backlightToNits(brightnessToBacklight2);
                            LocalDisplayDevice.this.mBacklightAdapter.setBacklight(brightnessToBacklight2, backlightToNits2, brightnessToBacklight, backlightToNits);
                            Trace.traceCounter(131072L, "ScreenBrightness", BrightnessSynchronizer.brightnessFloatToInt(f3));
                            Trace.traceCounter(131072L, "SdrScreenBrightness", BrightnessSynchronizer.brightnessFloatToInt(f4));
                            handleHdrSdrNitsChanged(backlightToNits, backlightToNits2);
                        } finally {
                            Trace.traceEnd(131072L);
                        }
                    }

                    public final float brightnessToBacklight(float f3) {
                        if (f3 == -1.0f) {
                            return -1.0f;
                        }
                        return LocalDisplayDevice.this.getDisplayDeviceConfig().getBacklightFromBrightness(f3);
                    }

                    public final float backlightToNits(float f3) {
                        return LocalDisplayDevice.this.getDisplayDeviceConfig().getNitsFromBacklight(f3);
                    }

                    public void handleHdrSdrNitsChanged(float f3, float f4) {
                        float f5 = (f3 == -1.0f || f4 == -1.0f) ? Float.NaN : f3 / f4;
                        if (BrightnessSynchronizer.floatEquals(LocalDisplayDevice.this.mCurrentHdrSdrRatio, f5)) {
                            return;
                        }
                        synchronized (LocalDisplayAdapter.this.getSyncRoot()) {
                            LocalDisplayDevice.this.mCurrentHdrSdrRatio = f5;
                            LocalDisplayDevice.this.updateDeviceInfoLocked();
                        }
                    }
                };
            }
            return null;
        }

        @Override // com.android.server.display.DisplayDevice
        public void setUserPreferredDisplayModeLocked(Display.Mode mode) {
            Display.Mode findMode;
            int i;
            int preferredModeId = getPreferredModeId();
            this.mUserPreferredMode = mode;
            if (mode == null && (i = this.mSystemPreferredModeId) != -1) {
                this.mDefaultModeId = i;
            }
            if (mode != null && ((mode.isRefreshRateSet() || mode.isResolutionSet()) && (findMode = findMode(mode.getPhysicalWidth(), mode.getPhysicalHeight(), mode.getRefreshRate())) != null)) {
                this.mUserPreferredMode = findMode;
            }
            this.mUserPreferredModeId = findUserPreferredModeIdLocked(this.mUserPreferredMode);
            if (preferredModeId == getPreferredModeId()) {
                return;
            }
            updateDeviceInfoLocked();
            if (LocalDisplayAdapter.this.mIsBootDisplayModeSupported) {
                if (this.mUserPreferredModeId == -1) {
                    LocalDisplayAdapter.this.mSurfaceControlProxy.clearBootDisplayMode(getDisplayTokenLocked());
                } else {
                    LocalDisplayAdapter.this.mSurfaceControlProxy.setBootDisplayMode(getDisplayTokenLocked(), findSfDisplayModeIdLocked(this.mUserPreferredMode.getModeId(), this.mDefaultModeGroup));
                }
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public Display.Mode getUserPreferredDisplayModeLocked() {
            return this.mUserPreferredMode;
        }

        @Override // com.android.server.display.DisplayDevice
        public Display.Mode getSystemPreferredDisplayModeLocked() {
            return findMode(this.mSystemPreferredModeId);
        }

        @Override // com.android.server.display.DisplayDevice
        public void setRequestedColorModeLocked(int i) {
            requestColorModeLocked(i);
        }

        @Override // com.android.server.display.DisplayDevice
        public void setDesiredDisplayModeSpecsLocked(DisplayModeDirector.DesiredDisplayModeSpecs desiredDisplayModeSpecs) {
            int i = desiredDisplayModeSpecs.baseModeId;
            if (i == 0) {
                return;
            }
            int findSfDisplayModeIdLocked = findSfDisplayModeIdLocked(i, this.mDefaultModeGroup);
            if (findSfDisplayModeIdLocked < 0) {
                Slog.w("LocalDisplayAdapter", "Ignoring request for invalid base mode id " + desiredDisplayModeSpecs.baseModeId);
                updateDeviceInfoLocked();
            } else if (this.mDisplayModeSpecsInvalid || !desiredDisplayModeSpecs.equals(this.mDisplayModeSpecs)) {
                this.mDisplayModeSpecsInvalid = false;
                this.mDisplayModeSpecs.copyFrom(desiredDisplayModeSpecs);
                Handler handler = LocalDisplayAdapter.this.getHandler();
                TriConsumer triConsumer = new TriConsumer() { // from class: com.android.server.display.LocalDisplayAdapter$LocalDisplayDevice$$ExternalSyntheticLambda1
                    public final void accept(Object obj, Object obj2, Object obj3) {
                        ((LocalDisplayAdapter.LocalDisplayDevice) obj).setDesiredDisplayModeSpecsAsync((IBinder) obj2, (SurfaceControl.DesiredDisplayModeSpecs) obj3);
                    }
                };
                IBinder displayTokenLocked = getDisplayTokenLocked();
                DisplayModeDirector.DesiredDisplayModeSpecs desiredDisplayModeSpecs2 = this.mDisplayModeSpecs;
                handler.sendMessage(PooledLambda.obtainMessage(triConsumer, this, displayTokenLocked, new SurfaceControl.DesiredDisplayModeSpecs(findSfDisplayModeIdLocked, desiredDisplayModeSpecs2.allowGroupSwitching, desiredDisplayModeSpecs2.primary, desiredDisplayModeSpecs2.appRequest)));
            }
        }

        public final void setDesiredDisplayModeSpecsAsync(IBinder iBinder, SurfaceControl.DesiredDisplayModeSpecs desiredDisplayModeSpecs) {
            LocalDisplayAdapter.this.mSurfaceControlProxy.setDesiredDisplayModeSpecs(iBinder, desiredDisplayModeSpecs);
        }

        @Override // com.android.server.display.DisplayDevice
        public void onOverlayChangedLocked() {
            updateDeviceInfoLocked();
        }

        public void onActiveDisplayModeChangedLocked(int i, float f) {
            if (updateActiveModeLocked(i, f)) {
                updateDeviceInfoLocked();
            }
        }

        public void onFrameRateOverridesChanged(DisplayEventReceiver.FrameRateOverride[] frameRateOverrideArr) {
            if (updateFrameRateOverridesLocked(frameRateOverrideArr)) {
                updateDeviceInfoLocked();
            }
        }

        public boolean updateActiveModeLocked(int i, float f) {
            if (this.mActiveSfDisplayMode.id == i && this.mActiveRenderFrameRate == f) {
                return false;
            }
            this.mActiveSfDisplayMode = getModeById(this.mSfDisplayModes, i);
            int findMatchingModeIdLocked = findMatchingModeIdLocked(i);
            this.mActiveModeId = findMatchingModeIdLocked;
            if (findMatchingModeIdLocked == -1) {
                Slog.w("LocalDisplayAdapter", "In unknown mode after setting allowed modes, activeModeId=" + i);
            }
            this.mActiveRenderFrameRate = f;
            return true;
        }

        public boolean updateFrameRateOverridesLocked(DisplayEventReceiver.FrameRateOverride[] frameRateOverrideArr) {
            if (Arrays.equals(frameRateOverrideArr, this.mFrameRateOverrides)) {
                return false;
            }
            this.mFrameRateOverrides = frameRateOverrideArr;
            return true;
        }

        public void requestColorModeLocked(int i) {
            if (this.mActiveColorMode == i) {
                return;
            }
            if (!this.mSupportedColorModes.contains(Integer.valueOf(i))) {
                Slog.w("LocalDisplayAdapter", "Unable to find color mode " + i + ", ignoring request.");
                return;
            }
            this.mActiveColorMode = i;
            LocalDisplayAdapter.this.getHandler().sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.display.LocalDisplayAdapter$LocalDisplayDevice$$ExternalSyntheticLambda0
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((LocalDisplayAdapter.LocalDisplayDevice) obj).requestColorModeAsync((IBinder) obj2, ((Integer) obj3).intValue());
                }
            }, this, getDisplayTokenLocked(), Integer.valueOf(i)));
        }

        public final void requestColorModeAsync(IBinder iBinder, int i) {
            LocalDisplayAdapter.this.mSurfaceControlProxy.setActiveColorMode(iBinder, i);
            synchronized (LocalDisplayAdapter.this.getSyncRoot()) {
                updateDeviceInfoLocked();
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public void setAutoLowLatencyModeLocked(boolean z) {
            if (this.mAllmRequested == z) {
                return;
            }
            this.mAllmRequested = z;
            if (!this.mAllmSupported) {
                Slog.d("LocalDisplayAdapter", "Unable to set ALLM because the connected display does not support ALLM.");
            } else {
                LocalDisplayAdapter.this.mSurfaceControlProxy.setAutoLowLatencyMode(getDisplayTokenLocked(), z);
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public void setGameContentTypeLocked(boolean z) {
            if (this.mGameContentTypeRequested == z) {
                return;
            }
            this.mGameContentTypeRequested = z;
            LocalDisplayAdapter.this.mSurfaceControlProxy.setGameContentType(getDisplayTokenLocked(), z);
        }

        @Override // com.android.server.display.DisplayDevice
        public void dumpLocked(PrintWriter printWriter) {
            super.dumpLocked(printWriter);
            printWriter.println("mPhysicalDisplayId=" + this.mPhysicalDisplayId);
            printWriter.println("mDisplayModeSpecs={" + this.mDisplayModeSpecs + "}");
            StringBuilder sb = new StringBuilder();
            sb.append("mDisplayModeSpecsInvalid=");
            sb.append(this.mDisplayModeSpecsInvalid);
            printWriter.println(sb.toString());
            printWriter.println("mActiveModeId=" + this.mActiveModeId);
            printWriter.println("mActiveColorMode=" + this.mActiveColorMode);
            printWriter.println("mDefaultModeId=" + this.mDefaultModeId);
            printWriter.println("mUserPreferredModeId=" + this.mUserPreferredModeId);
            printWriter.println("mState=" + Display.stateToString(this.mState));
            printWriter.println("mCommittedState=" + Display.stateToString(this.mCommittedState));
            printWriter.println("mBrightnessState=" + this.mBrightnessState);
            printWriter.println("mBacklightAdapter=" + this.mBacklightAdapter);
            printWriter.println("mAllmSupported=" + this.mAllmSupported);
            printWriter.println("mAllmRequested=" + this.mAllmRequested);
            printWriter.println("mGameContentTypeSupported=" + this.mGameContentTypeSupported);
            printWriter.println("mGameContentTypeRequested=" + this.mGameContentTypeRequested);
            printWriter.println("mStaticDisplayInfo=" + this.mStaticDisplayInfo);
            printWriter.println("mSfDisplayModes=");
            for (SurfaceControl.DisplayMode displayMode : this.mSfDisplayModes) {
                printWriter.println("  " + displayMode);
            }
            printWriter.println("mActiveSfDisplayMode=" + this.mActiveSfDisplayMode);
            printWriter.println("mActiveRenderFrameRate=" + this.mActiveRenderFrameRate);
            printWriter.println("mSupportedModes=");
            for (int i = 0; i < this.mSupportedModes.size(); i++) {
                printWriter.println("  " + this.mSupportedModes.valueAt(i));
            }
            printWriter.println("mSupportedColorModes=" + this.mSupportedColorModes);
            printWriter.println("mDisplayDeviceConfig=" + this.mDisplayDeviceConfig);
        }

        public final int findSfDisplayModeIdLocked(int i, int i2) {
            SurfaceControl.DisplayMode[] displayModeArr;
            DisplayModeRecord displayModeRecord = this.mSupportedModes.get(i);
            if (displayModeRecord != null) {
                int i3 = -1;
                for (SurfaceControl.DisplayMode displayMode : this.mSfDisplayModes) {
                    if (displayModeRecord.hasMatchingMode(displayMode)) {
                        if (i3 == -1) {
                            i3 = displayMode.id;
                        }
                        if (displayMode.group == i2) {
                            return displayMode.id;
                        }
                    }
                }
                return i3;
            }
            return -1;
        }

        public final Display.Mode findMode(int i) {
            for (int i2 = 0; i2 < this.mSupportedModes.size(); i2++) {
                Display.Mode mode = this.mSupportedModes.valueAt(i2).mMode;
                if (mode.getModeId() == i) {
                    return mode;
                }
            }
            return null;
        }

        public final Display.Mode findMode(int i, int i2, float f) {
            for (int i3 = 0; i3 < this.mSupportedModes.size(); i3++) {
                Display.Mode mode = this.mSupportedModes.valueAt(i3).mMode;
                if (mode.matchesIfValid(i, i2, f)) {
                    return mode;
                }
            }
            return null;
        }

        public final int findUserPreferredModeIdLocked(Display.Mode mode) {
            if (mode != null) {
                for (int i = 0; i < this.mSupportedModes.size(); i++) {
                    Display.Mode mode2 = this.mSupportedModes.valueAt(i).mMode;
                    if (mode.matches(mode2.getPhysicalWidth(), mode2.getPhysicalHeight(), mode2.getRefreshRate())) {
                        return mode2.getModeId();
                    }
                }
                return -1;
            }
            return -1;
        }

        public final int findMatchingModeIdLocked(int i) {
            SurfaceControl.DisplayMode modeById = getModeById(this.mSfDisplayModes, i);
            if (modeById == null) {
                Slog.e("LocalDisplayAdapter", "Invalid display mode ID " + i);
                return -1;
            }
            for (int i2 = 0; i2 < this.mSupportedModes.size(); i2++) {
                DisplayModeRecord valueAt = this.mSupportedModes.valueAt(i2);
                if (valueAt.hasMatchingMode(modeById)) {
                    return valueAt.mMode.getModeId();
                }
            }
            return -1;
        }

        public final void updateDeviceInfoLocked() {
            this.mInfo = null;
            LocalDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 2);
        }

        public final Display.Mode[] getDisplayModes(SparseArray<DisplayModeRecord> sparseArray) {
            int size = sparseArray.size();
            Display.Mode[] modeArr = new Display.Mode[size];
            for (int i = 0; i < size; i++) {
                modeArr[i] = sparseArray.valueAt(i).mMode;
            }
            return modeArr;
        }

        public final boolean isDisplayPrivate(DisplayAddress.Physical physical) {
            int[] intArray;
            if (physical != null && (intArray = LocalDisplayAdapter.this.getOverlayContext().getResources().getIntArray(17236086)) != null) {
                int port = physical.getPort();
                for (int i : intArray) {
                    if (i == port) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public final boolean hdrTypesEqual(int[] iArr, int[] iArr2) {
        int[] copyOf = Arrays.copyOf(iArr, iArr.length);
        Arrays.sort(copyOf);
        return Arrays.equals(copyOf, iArr2);
    }

    public Context getOverlayContext() {
        if (this.mOverlayContext == null) {
            this.mOverlayContext = ActivityThread.currentActivityThread().getSystemUiContext();
        }
        return this.mOverlayContext;
    }

    /* loaded from: classes.dex */
    public static final class DisplayModeRecord {
        public final Display.Mode mMode;

        public DisplayModeRecord(SurfaceControl.DisplayMode displayMode, float[] fArr) {
            this.mMode = DisplayAdapter.createMode(displayMode.width, displayMode.height, displayMode.refreshRate, fArr, displayMode.supportedHdrTypes);
        }

        public boolean hasMatchingMode(SurfaceControl.DisplayMode displayMode) {
            return this.mMode.getPhysicalWidth() == displayMode.width && this.mMode.getPhysicalHeight() == displayMode.height && Float.floatToIntBits(this.mMode.getRefreshRate()) == Float.floatToIntBits(displayMode.refreshRate);
        }

        public String toString() {
            return "DisplayModeRecord{mMode=" + this.mMode + "}";
        }
    }

    /* loaded from: classes.dex */
    public static class Injector {
        public ProxyDisplayEventReceiver mReceiver;

        public void setDisplayEventListenerLocked(Looper looper, DisplayEventListener displayEventListener) {
            this.mReceiver = new ProxyDisplayEventReceiver(looper, displayEventListener);
        }

        public SurfaceControlProxy getSurfaceControlProxy() {
            return new SurfaceControlProxy();
        }
    }

    /* loaded from: classes.dex */
    public static final class ProxyDisplayEventReceiver extends DisplayEventReceiver {
        public final DisplayEventListener mListener;

        public ProxyDisplayEventReceiver(Looper looper, DisplayEventListener displayEventListener) {
            super(looper, 0, 3);
            this.mListener = displayEventListener;
        }

        public void onHotplug(long j, long j2, boolean z) {
            this.mListener.onHotplug(j, j2, z);
        }

        public void onModeChanged(long j, long j2, int i, long j3) {
            this.mListener.onModeChanged(j, j2, i, j3);
        }

        public void onFrameRateOverridesChanged(long j, long j2, DisplayEventReceiver.FrameRateOverride[] frameRateOverrideArr) {
            this.mListener.onFrameRateOverridesChanged(j, j2, frameRateOverrideArr);
        }
    }

    /* loaded from: classes.dex */
    public final class LocalDisplayEventListener implements DisplayEventListener {
        public LocalDisplayEventListener() {
        }

        @Override // com.android.server.display.LocalDisplayAdapter.DisplayEventListener
        public void onHotplug(long j, long j2, boolean z) {
            synchronized (LocalDisplayAdapter.this.getSyncRoot()) {
                if (z) {
                    LocalDisplayAdapter.this.tryConnectDisplayLocked(j2);
                } else {
                    LocalDisplayAdapter.this.tryDisconnectDisplayLocked(j2);
                }
            }
        }

        @Override // com.android.server.display.LocalDisplayAdapter.DisplayEventListener
        public void onModeChanged(long j, long j2, int i, long j3) {
            synchronized (LocalDisplayAdapter.this.getSyncRoot()) {
                LocalDisplayDevice localDisplayDevice = (LocalDisplayDevice) LocalDisplayAdapter.this.mDevices.get(j2);
                if (localDisplayDevice == null) {
                    return;
                }
                localDisplayDevice.onActiveDisplayModeChangedLocked(i, 1.0E9f / ((float) j3));
            }
        }

        @Override // com.android.server.display.LocalDisplayAdapter.DisplayEventListener
        public void onFrameRateOverridesChanged(long j, long j2, DisplayEventReceiver.FrameRateOverride[] frameRateOverrideArr) {
            synchronized (LocalDisplayAdapter.this.getSyncRoot()) {
                LocalDisplayDevice localDisplayDevice = (LocalDisplayDevice) LocalDisplayAdapter.this.mDevices.get(j2);
                if (localDisplayDevice == null) {
                    return;
                }
                localDisplayDevice.onFrameRateOverridesChanged(frameRateOverrideArr);
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class SurfaceControlProxy {
        public SurfaceControl.DynamicDisplayInfo getDynamicDisplayInfo(long j) {
            return SurfaceControl.getDynamicDisplayInfo(j);
        }

        public long[] getPhysicalDisplayIds() {
            return DisplayControl.getPhysicalDisplayIds();
        }

        public IBinder getPhysicalDisplayToken(long j) {
            return DisplayControl.getPhysicalDisplayToken(j);
        }

        public SurfaceControl.StaticDisplayInfo getStaticDisplayInfo(long j) {
            return SurfaceControl.getStaticDisplayInfo(j);
        }

        public SurfaceControl.DesiredDisplayModeSpecs getDesiredDisplayModeSpecs(IBinder iBinder) {
            return SurfaceControl.getDesiredDisplayModeSpecs(iBinder);
        }

        public boolean setDesiredDisplayModeSpecs(IBinder iBinder, SurfaceControl.DesiredDisplayModeSpecs desiredDisplayModeSpecs) {
            return SurfaceControl.setDesiredDisplayModeSpecs(iBinder, desiredDisplayModeSpecs);
        }

        public void setDisplayPowerMode(IBinder iBinder, int i) {
            SurfaceControl.setDisplayPowerMode(iBinder, i);
        }

        public boolean setActiveColorMode(IBinder iBinder, int i) {
            return SurfaceControl.setActiveColorMode(iBinder, i);
        }

        public boolean getBootDisplayModeSupport() {
            Trace.traceBegin(32L, "getBootDisplayModeSupport");
            try {
                return SurfaceControl.getBootDisplayModeSupport();
            } finally {
                Trace.traceEnd(32L);
            }
        }

        public void setBootDisplayMode(IBinder iBinder, int i) {
            SurfaceControl.setBootDisplayMode(iBinder, i);
        }

        public void clearBootDisplayMode(IBinder iBinder) {
            SurfaceControl.clearBootDisplayMode(iBinder);
        }

        public void setAutoLowLatencyMode(IBinder iBinder, boolean z) {
            SurfaceControl.setAutoLowLatencyMode(iBinder, z);
        }

        public void setGameContentType(IBinder iBinder, boolean z) {
            SurfaceControl.setGameContentType(iBinder, z);
        }

        public boolean getDisplayBrightnessSupport(IBinder iBinder) {
            return SurfaceControl.getDisplayBrightnessSupport(iBinder);
        }

        public boolean setDisplayBrightness(IBinder iBinder, float f) {
            return SurfaceControl.setDisplayBrightness(iBinder, f);
        }

        public boolean setDisplayBrightness(IBinder iBinder, float f, float f2, float f3, float f4) {
            return SurfaceControl.setDisplayBrightness(iBinder, f, f2, f3, f4);
        }
    }

    /* loaded from: classes.dex */
    public static class BacklightAdapter {
        public final LogicalLight mBacklight;
        public final IBinder mDisplayToken;
        public boolean mForceSurfaceControl = false;
        public final SurfaceControlProxy mSurfaceControlProxy;
        public final boolean mUseSurfaceControlBrightness;

        public BacklightAdapter(IBinder iBinder, boolean z, SurfaceControlProxy surfaceControlProxy) {
            this.mDisplayToken = iBinder;
            this.mSurfaceControlProxy = surfaceControlProxy;
            boolean displayBrightnessSupport = surfaceControlProxy.getDisplayBrightnessSupport(iBinder);
            this.mUseSurfaceControlBrightness = displayBrightnessSupport;
            if (!displayBrightnessSupport && z) {
                this.mBacklight = ((LightsManager) LocalServices.getService(LightsManager.class)).getLight(0);
            } else {
                this.mBacklight = null;
            }
        }

        public void setBacklight(float f, float f2, float f3, float f4) {
            if (this.mUseSurfaceControlBrightness || this.mForceSurfaceControl) {
                if (BrightnessSynchronizer.floatEquals(f, Float.NaN)) {
                    this.mSurfaceControlProxy.setDisplayBrightness(this.mDisplayToken, f3);
                    return;
                } else {
                    this.mSurfaceControlProxy.setDisplayBrightness(this.mDisplayToken, f, f2, f3, f4);
                    return;
                }
            }
            LogicalLight logicalLight = this.mBacklight;
            if (logicalLight != null) {
                logicalLight.setBrightness(f3);
            }
        }

        public void setForceSurfaceControl(boolean z) {
            this.mForceSurfaceControl = z;
        }

        public String toString() {
            return "BacklightAdapter [useSurfaceControl=" + this.mUseSurfaceControlBrightness + " (force_anyway? " + this.mForceSurfaceControl + "), backlight=" + this.mBacklight + "]";
        }
    }
}
