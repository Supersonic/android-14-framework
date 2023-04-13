package android.hardware.display;

import android.annotation.SystemApi;
import android.content.Context;
import android.content.p001pm.IPackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.display.DisplayManagerGlobal;
import android.hardware.display.IDisplayManager;
import android.hardware.display.VirtualDisplay;
import android.hardware.display.VirtualDisplayConfig;
import android.media.projection.MediaProjection;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.Looper;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.Surface;
import com.android.internal.C4057R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class DisplayManager {
    public static final String ACTION_WIFI_DISPLAY_STATUS_CHANGED = "android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED";
    private static final boolean DEBUG = false;
    public static final String DISPLAY_CATEGORY_ALL_INCLUDING_DISABLED = "android.hardware.display.category.ALL_INCLUDING_DISABLED";
    public static final String DISPLAY_CATEGORY_PRESENTATION = "android.hardware.display.category.PRESENTATION";
    public static final String DISPLAY_CATEGORY_REAR = "android.hardware.display.category.REAR";
    private static final boolean ENABLE_VIRTUAL_DISPLAY_REFRESH_RATE = true;
    public static final long EVENT_FLAG_DISPLAY_ADDED = 1;
    public static final long EVENT_FLAG_DISPLAY_BRIGHTNESS = 8;
    public static final long EVENT_FLAG_DISPLAY_CHANGED = 4;
    public static final long EVENT_FLAG_DISPLAY_REMOVED = 2;
    public static final long EVENT_FLAG_HDR_SDR_RATIO_CHANGED = 16;
    public static final String EXTRA_WIFI_DISPLAY_STATUS = "android.hardware.display.extra.WIFI_DISPLAY_STATUS";
    public static final String HDR_OUTPUT_CONTROL_FLAG = "enable_hdr_output_control";
    public static final int MATCH_CONTENT_FRAMERATE_ALWAYS = 2;
    public static final int MATCH_CONTENT_FRAMERATE_NEVER = 0;
    public static final int MATCH_CONTENT_FRAMERATE_SEAMLESSS_ONLY = 1;
    public static final int MATCH_CONTENT_FRAMERATE_UNKNOWN = -1;
    public static final int SWITCHING_TYPE_ACROSS_AND_WITHIN_GROUPS = 2;
    public static final int SWITCHING_TYPE_NONE = 0;
    public static final int SWITCHING_TYPE_RENDER_FRAME_RATE_ONLY = 3;
    public static final int SWITCHING_TYPE_WITHIN_GROUPS = 1;
    private static final String TAG = "DisplayManager";
    public static final int VIRTUAL_DISPLAY_FLAG_ALWAYS_UNLOCKED = 4096;
    public static final int VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR = 16;
    public static final int VIRTUAL_DISPLAY_FLAG_CAN_SHOW_WITH_INSECURE_KEYGUARD = 32;
    public static final int VIRTUAL_DISPLAY_FLAG_DESTROY_CONTENT_ON_REMOVAL = 256;
    public static final int VIRTUAL_DISPLAY_FLAG_DEVICE_DISPLAY_GROUP = 32768;
    public static final int VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY = 8;
    public static final int VIRTUAL_DISPLAY_FLAG_OWN_DISPLAY_GROUP = 2048;
    public static final int VIRTUAL_DISPLAY_FLAG_OWN_FOCUS = 16384;
    public static final int VIRTUAL_DISPLAY_FLAG_PRESENTATION = 2;
    public static final int VIRTUAL_DISPLAY_FLAG_PUBLIC = 1;
    public static final int VIRTUAL_DISPLAY_FLAG_ROTATES_WITH_CONTENT = 128;
    public static final int VIRTUAL_DISPLAY_FLAG_SECURE = 4;
    public static final int VIRTUAL_DISPLAY_FLAG_SHOULD_SHOW_SYSTEM_DECORATIONS = 512;
    @SystemApi
    public static final int VIRTUAL_DISPLAY_FLAG_STEAL_TOP_FOCUS_DISABLED = 65536;
    public static final int VIRTUAL_DISPLAY_FLAG_SUPPORTS_TOUCH = 64;
    public static final int VIRTUAL_DISPLAY_FLAG_TOUCH_FEEDBACK_DISABLED = 8192;
    @SystemApi
    public static final int VIRTUAL_DISPLAY_FLAG_TRUSTED = 1024;
    private final Context mContext;
    private final Object mLock = new Object();
    private final SparseArray<Display> mDisplays = new SparseArray<>();
    private final ArrayList<Display> mTempDisplays = new ArrayList<>();
    private final DisplayManagerGlobal mGlobal = DisplayManagerGlobal.getInstance();

    /* loaded from: classes.dex */
    public interface DeviceConfig {
        public static final String KEY_BRIGHTNESS_THROTTLING_DATA = "brightness_throttling_data";
        public static final String KEY_FIXED_REFRESH_RATE_HIGH_AMBIENT_BRIGHTNESS_THRESHOLDS = "fixed_refresh_rate_high_ambient_brightness_thresholds";
        public static final String KEY_FIXED_REFRESH_RATE_HIGH_DISPLAY_BRIGHTNESS_THRESHOLDS = "fixed_refresh_rate_high_display_brightness_thresholds";
        public static final String KEY_FIXED_REFRESH_RATE_LOW_AMBIENT_BRIGHTNESS_THRESHOLDS = "peak_refresh_rate_ambient_thresholds";
        public static final String KEY_FIXED_REFRESH_RATE_LOW_DISPLAY_BRIGHTNESS_THRESHOLDS = "peak_refresh_rate_brightness_thresholds";
        public static final String KEY_HIGH_REFRESH_RATE_BLACKLIST = "high_refresh_rate_blacklist";
        public static final String KEY_PEAK_REFRESH_RATE_DEFAULT = "peak_refresh_rate_default";
        public static final String KEY_REFRESH_RATE_IN_HBM_HDR = "refresh_rate_in_hbm_hdr";
        public static final String KEY_REFRESH_RATE_IN_HBM_SUNLIGHT = "refresh_rate_in_hbm_sunlight";
        public static final String KEY_REFRESH_RATE_IN_HIGH_ZONE = "refresh_rate_in_high_zone";
        public static final String KEY_REFRESH_RATE_IN_LOW_ZONE = "refresh_rate_in_zone";
    }

    /* loaded from: classes.dex */
    public interface DisplayListener {
        void onDisplayAdded(int i);

        void onDisplayChanged(int i);

        void onDisplayRemoved(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface EventsMask {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface MatchContentFrameRateType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SwitchingType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface VirtualDisplayFlag {
    }

    public DisplayManager(Context context) {
        this.mContext = context;
    }

    public Display getDisplay(int displayId) {
        Display orCreateDisplayLocked;
        synchronized (this.mLock) {
            orCreateDisplayLocked = getOrCreateDisplayLocked(displayId, false);
        }
        return orCreateDisplayLocked;
    }

    public Display[] getDisplays() {
        return getDisplays(null);
    }

    public Display[] getDisplays(String category) {
        Display[] displayArr;
        boolean includeDisabled = category != null && category.equals(DISPLAY_CATEGORY_ALL_INCLUDING_DISABLED);
        int[] displayIds = this.mGlobal.getDisplayIds(includeDisabled);
        synchronized (this.mLock) {
            if (DISPLAY_CATEGORY_PRESENTATION.equals(category)) {
                addDisplaysLocked(this.mTempDisplays, displayIds, 3, 8);
                addDisplaysLocked(this.mTempDisplays, displayIds, 2, 8);
                addDisplaysLocked(this.mTempDisplays, displayIds, 4, 8);
                addDisplaysLocked(this.mTempDisplays, displayIds, 5, 8);
                addDisplaysLocked(this.mTempDisplays, displayIds, 1, 8);
            } else if (DISPLAY_CATEGORY_REAR.equals(category)) {
                addDisplaysLocked(this.mTempDisplays, displayIds, 1, 8192);
            } else if (category == null || DISPLAY_CATEGORY_ALL_INCLUDING_DISABLED.equals(category)) {
                addAllDisplaysLocked(this.mTempDisplays, displayIds);
            }
            ArrayList<Display> arrayList = this.mTempDisplays;
            displayArr = (Display[]) arrayList.toArray(new Display[arrayList.size()]);
            this.mTempDisplays.clear();
        }
        return displayArr;
    }

    private void addAllDisplaysLocked(ArrayList<Display> displays, int[] displayIds) {
        for (int i : displayIds) {
            Display display = getOrCreateDisplayLocked(i, true);
            if (display != null) {
                displays.add(display);
            }
        }
    }

    private void addDisplaysLocked(ArrayList<Display> displays, int[] displayIds, int matchType, int flagMask) {
        Display display;
        for (int displayId : displayIds) {
            if (displayId != 0 && (display = getOrCreateDisplayLocked(displayId, true)) != null && (display.getFlags() & flagMask) == flagMask && display.getType() == matchType) {
                displays.add(display);
            }
        }
    }

    private Display getOrCreateDisplayLocked(int displayId, boolean assumeValid) {
        Display display = this.mDisplays.get(displayId);
        if (display != null) {
            if (!assumeValid && !display.isValid()) {
                return null;
            }
            return display;
        }
        Resources resources = this.mContext.getDisplayId() == displayId ? this.mContext.getResources() : null;
        Display display2 = this.mGlobal.getCompatibleDisplay(displayId, resources);
        if (display2 != null) {
            this.mDisplays.put(displayId, display2);
            return display2;
        }
        return display2;
    }

    public void registerDisplayListener(DisplayListener listener, Handler handler) {
        registerDisplayListener(listener, handler, 7L);
    }

    public void registerDisplayListener(DisplayListener listener, Handler handler, long eventsMask) {
        this.mGlobal.registerDisplayListener(listener, handler, eventsMask);
    }

    public void unregisterDisplayListener(DisplayListener listener) {
        this.mGlobal.unregisterDisplayListener(listener);
    }

    public void startWifiDisplayScan() {
        this.mGlobal.startWifiDisplayScan();
    }

    public void stopWifiDisplayScan() {
        this.mGlobal.stopWifiDisplayScan();
    }

    public void connectWifiDisplay(String deviceAddress) {
        this.mGlobal.connectWifiDisplay(deviceAddress);
    }

    public void pauseWifiDisplay() {
        this.mGlobal.pauseWifiDisplay();
    }

    public void resumeWifiDisplay() {
        this.mGlobal.resumeWifiDisplay();
    }

    public void disconnectWifiDisplay() {
        this.mGlobal.disconnectWifiDisplay();
    }

    public void renameWifiDisplay(String deviceAddress, String alias) {
        this.mGlobal.renameWifiDisplay(deviceAddress, alias);
    }

    public void forgetWifiDisplay(String deviceAddress) {
        this.mGlobal.forgetWifiDisplay(deviceAddress);
    }

    public WifiDisplayStatus getWifiDisplayStatus() {
        return this.mGlobal.getWifiDisplayStatus();
    }

    @SystemApi
    public void setSaturationLevel(float level) {
        if (level < 0.0f || level > 1.0f) {
            throw new IllegalArgumentException("Saturation level must be between 0 and 1");
        }
        ColorDisplayManager cdm = (ColorDisplayManager) this.mContext.getSystemService(ColorDisplayManager.class);
        cdm.setSaturationLevel(Math.round(100.0f * level));
    }

    public void setUserDisabledHdrTypes(int[] userDisabledTypes) {
        this.mGlobal.setUserDisabledHdrTypes(userDisabledTypes);
    }

    public void setAreUserDisabledHdrTypesAllowed(boolean areUserDisabledHdrTypesAllowed) {
        this.mGlobal.setAreUserDisabledHdrTypesAllowed(areUserDisabledHdrTypesAllowed);
    }

    public boolean areUserDisabledHdrTypesAllowed() {
        return this.mGlobal.areUserDisabledHdrTypesAllowed();
    }

    public int[] getUserDisabledHdrTypes() {
        return this.mGlobal.getUserDisabledHdrTypes();
    }

    public void overrideHdrTypes(int displayId, int[] modes) {
        this.mGlobal.overrideHdrTypes(displayId, modes);
    }

    public VirtualDisplay createVirtualDisplay(String name, int width, int height, int densityDpi, Surface surface, int flags) {
        return createVirtualDisplay(name, width, height, densityDpi, surface, flags, null, null);
    }

    public VirtualDisplay createVirtualDisplay(String name, int width, int height, int densityDpi, Surface surface, int flags, VirtualDisplay.Callback callback, Handler handler) {
        VirtualDisplayConfig.Builder builder = new VirtualDisplayConfig.Builder(name, width, height, densityDpi);
        builder.setFlags(flags);
        if (surface != null) {
            builder.setSurface(surface);
        }
        return createVirtualDisplay(builder.build(), handler, callback);
    }

    public VirtualDisplay createVirtualDisplay(VirtualDisplayConfig config) {
        return createVirtualDisplay(config, null, null);
    }

    public VirtualDisplay createVirtualDisplay(VirtualDisplayConfig config, Handler handler, VirtualDisplay.Callback callback) {
        return createVirtualDisplay((MediaProjection) null, config, callback, handler, (Context) null);
    }

    public VirtualDisplay createVirtualDisplay(MediaProjection projection, String name, int width, int height, int densityDpi, Surface surface, int flags, VirtualDisplay.Callback callback, Handler handler, String uniqueId) {
        VirtualDisplayConfig.Builder builder = new VirtualDisplayConfig.Builder(name, width, height, densityDpi);
        builder.setFlags(flags);
        if (uniqueId != null) {
            builder.setUniqueId(uniqueId);
        }
        if (surface != null) {
            builder.setSurface(surface);
        }
        return createVirtualDisplay(projection, builder.build(), callback, handler, (Context) null);
    }

    public VirtualDisplay createVirtualDisplay(MediaProjection projection, VirtualDisplayConfig virtualDisplayConfig, VirtualDisplay.Callback callback, Handler handler, Context windowContext) {
        Executor executor = null;
        if (callback != null) {
            executor = new HandlerExecutor(Handler.createAsync(handler != null ? handler.getLooper() : Looper.myLooper()));
        }
        return this.mGlobal.createVirtualDisplay(this.mContext, projection, virtualDisplayConfig, callback, executor, windowContext);
    }

    @SystemApi
    public Point getStableDisplaySize() {
        return this.mGlobal.getStableDisplaySize();
    }

    @SystemApi
    public List<BrightnessChangeEvent> getBrightnessEvents() {
        return this.mGlobal.getBrightnessEvents(this.mContext.getOpPackageName());
    }

    @SystemApi
    public List<AmbientBrightnessDayStats> getAmbientBrightnessStats() {
        return this.mGlobal.getAmbientBrightnessStats();
    }

    @SystemApi
    public void setBrightnessConfiguration(BrightnessConfiguration c) {
        setBrightnessConfigurationForUser(c, this.mContext.getUserId(), this.mContext.getPackageName());
    }

    @SystemApi
    public void setBrightnessConfigurationForDisplay(BrightnessConfiguration c, String uniqueId) {
        this.mGlobal.setBrightnessConfigurationForDisplay(c, uniqueId, this.mContext.getUserId(), this.mContext.getPackageName());
    }

    @SystemApi
    public BrightnessConfiguration getBrightnessConfigurationForDisplay(String uniqueId) {
        return this.mGlobal.getBrightnessConfigurationForDisplay(uniqueId, this.mContext.getUserId());
    }

    public void setBrightnessConfigurationForUser(BrightnessConfiguration c, int userId, String packageName) {
        this.mGlobal.setBrightnessConfigurationForUser(c, userId, packageName);
    }

    @SystemApi
    public BrightnessConfiguration getBrightnessConfiguration() {
        return getBrightnessConfigurationForUser(this.mContext.getUserId());
    }

    public BrightnessConfiguration getBrightnessConfigurationForUser(int userId) {
        return this.mGlobal.getBrightnessConfigurationForUser(userId);
    }

    @SystemApi
    public BrightnessConfiguration getDefaultBrightnessConfiguration() {
        return this.mGlobal.getDefaultBrightnessConfiguration();
    }

    public boolean isMinimalPostProcessingRequested(int displayId) {
        return this.mGlobal.isMinimalPostProcessingRequested(displayId);
    }

    public void setTemporaryBrightness(int displayId, float brightness) {
        this.mGlobal.setTemporaryBrightness(displayId, brightness);
    }

    public void setBrightness(int displayId, float brightness) {
        this.mGlobal.setBrightness(displayId, brightness);
    }

    public float getBrightness(int displayId) {
        return this.mGlobal.getBrightness(displayId);
    }

    public void setTemporaryAutoBrightnessAdjustment(float adjustment) {
        this.mGlobal.setTemporaryAutoBrightnessAdjustment(adjustment);
    }

    @SystemApi
    public Pair<float[], float[]> getMinimumBrightnessCurve() {
        return this.mGlobal.getMinimumBrightnessCurve();
    }

    public void setGlobalUserPreferredDisplayMode(Display.Mode mode) {
        Display.Mode preferredMode = new Display.Mode(mode.getPhysicalWidth(), mode.getPhysicalHeight(), mode.getRefreshRate());
        this.mGlobal.setUserPreferredDisplayMode(-1, preferredMode);
    }

    public void clearGlobalUserPreferredDisplayMode() {
        this.mGlobal.setUserPreferredDisplayMode(-1, null);
    }

    public Display.Mode getGlobalUserPreferredDisplayMode() {
        return this.mGlobal.getUserPreferredDisplayMode(-1);
    }

    public void setHdrConversionMode(HdrConversionMode hdrConversionMode) {
        this.mGlobal.setHdrConversionMode(hdrConversionMode);
    }

    public HdrConversionMode getHdrConversionMode() {
        return this.mGlobal.getHdrConversionMode();
    }

    public HdrConversionMode getHdrConversionModeSetting() {
        return this.mGlobal.getHdrConversionModeSetting();
    }

    public int[] getSupportedHdrOutputTypes() {
        return this.mGlobal.getSupportedHdrOutputTypes();
    }

    public void setShouldAlwaysRespectAppRequestedMode(boolean enabled) {
        this.mGlobal.setShouldAlwaysRespectAppRequestedMode(enabled);
    }

    public boolean shouldAlwaysRespectAppRequestedMode() {
        return this.mGlobal.shouldAlwaysRespectAppRequestedMode();
    }

    public boolean supportsSeamlessRefreshRateSwitching() {
        return this.mContext.getResources().getBoolean(C4057R.bool.config_supportsSeamlessRefreshRateSwitching);
    }

    public void setRefreshRateSwitchingType(int newValue) {
        this.mGlobal.setRefreshRateSwitchingType(newValue);
    }

    public int getMatchContentFrameRateUserPreference() {
        return toMatchContentFrameRateSetting(this.mGlobal.getRefreshRateSwitchingType());
    }

    private int toMatchContentFrameRateSetting(int switchingType) {
        switch (switchingType) {
            case 0:
                return 0;
            case 1:
            case 3:
                return 1;
            case 2:
                return 2;
            default:
                Slog.m96e(TAG, switchingType + " is not a valid value of switching type.");
                return -1;
        }
    }

    @SystemApi
    public static VirtualDisplay createVirtualDisplay(String name, int width, int height, int displayIdToMirror, Surface surface) {
        IDisplayManager sDm = IDisplayManager.Stub.asInterface(ServiceManager.getService(Context.DISPLAY_SERVICE));
        IPackageManager sPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        VirtualDisplayConfig.Builder builder = new VirtualDisplayConfig.Builder(name, width, height, 1).setFlags(16).setDisplayIdToMirror(displayIdToMirror);
        if (surface != null) {
            builder.setSurface(surface);
        }
        VirtualDisplayConfig virtualDisplayConfig = builder.build();
        try {
            String[] packages = sPackageManager.getPackagesForUid(Process.myUid());
            String packageName = packages == null ? null : packages[0];
            DisplayManagerGlobal.VirtualDisplayCallback callbackWrapper = new DisplayManagerGlobal.VirtualDisplayCallback(null, null);
            try {
                int displayId = sDm.createVirtualDisplay(virtualDisplayConfig, callbackWrapper, null, packageName);
                return DisplayManagerGlobal.getInstance().createVirtualDisplayWrapper(virtualDisplayConfig, null, callbackWrapper, displayId);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        } catch (RemoteException ex2) {
            throw ex2.rethrowFromSystemServer();
        }
    }
}
