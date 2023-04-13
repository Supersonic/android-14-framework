package android.hardware.display;

import android.app.PropertyInvalidatedCache;
import android.content.Context;
import android.content.p001pm.ParceledListSlice;
import android.content.res.Resources;
import android.graphics.ColorSpace;
import android.graphics.Point;
import android.hardware.OverlayProperties;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerGlobal;
import android.hardware.display.IDisplayManager;
import android.hardware.display.IDisplayManagerCallback;
import android.hardware.display.IVirtualDisplayCallback;
import android.hardware.display.VirtualDisplay;
import android.hardware.graphics.common.DisplayDecorationSupport;
import android.media.projection.IMediaProjection;
import android.media.projection.MediaProjection;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayAdjustments;
import android.view.DisplayInfo;
import android.view.Surface;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes.dex */
public final class DisplayManagerGlobal {
    public static final String CACHE_KEY_DISPLAY_INFO_PROPERTY = "cache_key.display_info";
    private static final boolean DEBUG = false;
    public static final int EVENT_DISPLAY_ADDED = 1;
    public static final int EVENT_DISPLAY_BRIGHTNESS_CHANGED = 4;
    public static final int EVENT_DISPLAY_CHANGED = 2;
    public static final int EVENT_DISPLAY_HDR_SDR_RATIO_CHANGED = 5;
    public static final int EVENT_DISPLAY_REMOVED = 3;
    private static final String TAG = "DisplayManager";
    private static final boolean USE_CACHE = false;
    private static DisplayManagerGlobal sInstance;
    private DisplayManagerCallback mCallback;
    private int[] mDisplayIdCache;
    private final IDisplayManager mDm;
    private float mNativeCallbackReportedRefreshRate;
    private final OverlayProperties mOverlayProperties;
    private final ColorSpace mWideColorSpace;
    private int mWifiDisplayScanNestCount;
    private boolean mDispatchNativeCallbacks = false;
    private final Object mLock = new Object();
    private long mRegisteredEventsMask = 0;
    private final CopyOnWriteArrayList<DisplayListenerDelegate> mDisplayListeners = new CopyOnWriteArrayList<>();
    private final SparseArray<DisplayInfo> mDisplayInfoCache = new SparseArray<>();
    private PropertyInvalidatedCache<Integer, DisplayInfo> mDisplayCache = new PropertyInvalidatedCache<Integer, DisplayInfo>(8, CACHE_KEY_DISPLAY_INFO_PROPERTY) { // from class: android.hardware.display.DisplayManagerGlobal.1
        @Override // android.app.PropertyInvalidatedCache
        public DisplayInfo recompute(Integer id) {
            try {
                return DisplayManagerGlobal.this.mDm.getDisplayInfo(id.intValue());
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DisplayEvent {
    }

    private static native void nSignalNativeCallbacks(float f);

    public DisplayManagerGlobal(IDisplayManager dm) {
        this.mDm = dm;
        try {
            this.mWideColorSpace = ColorSpace.get(ColorSpace.Named.values()[dm.getPreferredWideGamutColorSpaceId()]);
            this.mOverlayProperties = dm.getOverlaySupport();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public static DisplayManagerGlobal getInstance() {
        DisplayManagerGlobal displayManagerGlobal;
        IBinder b;
        synchronized (DisplayManagerGlobal.class) {
            if (sInstance == null && (b = ServiceManager.getService(Context.DISPLAY_SERVICE)) != null) {
                sInstance = new DisplayManagerGlobal(IDisplayManager.Stub.asInterface(b));
            }
            displayManagerGlobal = sInstance;
        }
        return displayManagerGlobal;
    }

    public DisplayInfo getDisplayInfo(int displayId) {
        DisplayInfo displayInfoLocked;
        synchronized (this.mLock) {
            displayInfoLocked = getDisplayInfoLocked(displayId);
        }
        return displayInfoLocked;
    }

    private DisplayInfo getDisplayInfoLocked(int displayId) {
        DisplayInfo info = null;
        PropertyInvalidatedCache<Integer, DisplayInfo> propertyInvalidatedCache = this.mDisplayCache;
        if (propertyInvalidatedCache != null) {
            DisplayInfo info2 = propertyInvalidatedCache.query(Integer.valueOf(displayId));
            info = info2;
        } else {
            try {
                info = this.mDm.getDisplayInfo(displayId);
            } catch (RemoteException ex) {
                ex.rethrowFromSystemServer();
            }
        }
        if (info == null) {
            return null;
        }
        registerCallbackIfNeededLocked();
        return info;
    }

    public int[] getDisplayIds() {
        return getDisplayIds(false);
    }

    public int[] getDisplayIds(boolean includeDisabled) {
        int[] displayIds;
        try {
            synchronized (this.mLock) {
                displayIds = this.mDm.getDisplayIds(includeDisabled);
                registerCallbackIfNeededLocked();
            }
            return displayIds;
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean isUidPresentOnDisplay(int uid, int displayId) {
        try {
            return this.mDm.isUidPresentOnDisplay(uid, displayId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public Display getCompatibleDisplay(int displayId, DisplayAdjustments daj) {
        DisplayInfo displayInfo = getDisplayInfo(displayId);
        if (displayInfo == null) {
            return null;
        }
        return new Display(this, displayId, displayInfo, daj);
    }

    public Display getCompatibleDisplay(int displayId, Resources resources) {
        DisplayInfo displayInfo = getDisplayInfo(displayId);
        if (displayInfo == null) {
            return null;
        }
        return new Display(this, displayId, displayInfo, resources);
    }

    public Display getRealDisplay(int displayId) {
        return getCompatibleDisplay(displayId, DisplayAdjustments.DEFAULT_DISPLAY_ADJUSTMENTS);
    }

    public void registerDisplayListener(DisplayManager.DisplayListener listener, Handler handler, long eventsMask) {
        Looper looper = getLooperForHandler(handler);
        Handler springBoard = new Handler(looper);
        registerDisplayListener(listener, new HandlerExecutor(springBoard), eventsMask);
    }

    public void registerDisplayListener(DisplayManager.DisplayListener listener, Executor executor, long eventsMask) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        if (eventsMask == 0) {
            throw new IllegalArgumentException("The set of events to listen to must not be empty.");
        }
        synchronized (this.mLock) {
            int index = findDisplayListenerLocked(listener);
            if (index < 0) {
                this.mDisplayListeners.add(new DisplayListenerDelegate(listener, executor, eventsMask));
                registerCallbackIfNeededLocked();
            } else {
                this.mDisplayListeners.get(index).setEventsMask(eventsMask);
            }
            updateCallbackIfNeededLocked();
        }
    }

    public void unregisterDisplayListener(DisplayManager.DisplayListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mLock) {
            int index = findDisplayListenerLocked(listener);
            if (index >= 0) {
                DisplayListenerDelegate d = this.mDisplayListeners.get(index);
                d.clearEvents();
                this.mDisplayListeners.remove(index);
                updateCallbackIfNeededLocked();
            }
        }
    }

    private static Looper getLooperForHandler(Handler handler) {
        Looper looper = handler != null ? handler.getLooper() : Looper.myLooper();
        if (looper == null) {
            looper = Looper.getMainLooper();
        }
        if (looper == null) {
            throw new RuntimeException("Could not get Looper for the UI thread.");
        }
        return looper;
    }

    private int findDisplayListenerLocked(DisplayManager.DisplayListener listener) {
        int numListeners = this.mDisplayListeners.size();
        for (int i = 0; i < numListeners; i++) {
            if (this.mDisplayListeners.get(i).mListener == listener) {
                return i;
            }
        }
        return -1;
    }

    private int calculateEventsMaskLocked() {
        int mask = 0;
        int numListeners = this.mDisplayListeners.size();
        for (int i = 0; i < numListeners; i++) {
            mask = (int) (mask | this.mDisplayListeners.get(i).mEventsMask);
        }
        if (this.mDispatchNativeCallbacks) {
            return (int) (mask | 7);
        }
        return mask;
    }

    private void registerCallbackIfNeededLocked() {
        if (this.mCallback == null) {
            this.mCallback = new DisplayManagerCallback();
            updateCallbackIfNeededLocked();
        }
    }

    private void updateCallbackIfNeededLocked() {
        int mask = calculateEventsMaskLocked();
        if (mask != this.mRegisteredEventsMask) {
            try {
                this.mDm.registerCallbackWithEventMask(this.mCallback, mask);
                this.mRegisteredEventsMask = mask;
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayEvent(int displayId, int event) {
        DisplayInfo info;
        DisplayInfo display;
        synchronized (this.mLock) {
            info = getDisplayInfoLocked(displayId);
            if (event == 2 && this.mDispatchNativeCallbacks && displayId == 0 && (display = getDisplayInfoLocked(displayId)) != null && this.mNativeCallbackReportedRefreshRate != display.getRefreshRate()) {
                float refreshRate = display.getRefreshRate();
                this.mNativeCallbackReportedRefreshRate = refreshRate;
                nSignalNativeCallbacks(refreshRate);
            }
        }
        Iterator<DisplayListenerDelegate> it = this.mDisplayListeners.iterator();
        while (it.hasNext()) {
            DisplayListenerDelegate listener = it.next();
            listener.sendDisplayEvent(displayId, event, info);
        }
    }

    public void startWifiDisplayScan() {
        synchronized (this.mLock) {
            int i = this.mWifiDisplayScanNestCount;
            this.mWifiDisplayScanNestCount = i + 1;
            if (i == 0) {
                registerCallbackIfNeededLocked();
                try {
                    this.mDm.startWifiDisplayScan();
                } catch (RemoteException ex) {
                    throw ex.rethrowFromSystemServer();
                }
            }
        }
    }

    public void stopWifiDisplayScan() {
        synchronized (this.mLock) {
            int i = this.mWifiDisplayScanNestCount - 1;
            this.mWifiDisplayScanNestCount = i;
            if (i == 0) {
                try {
                    this.mDm.stopWifiDisplayScan();
                } catch (RemoteException ex) {
                    throw ex.rethrowFromSystemServer();
                }
            } else if (i < 0) {
                Log.wtf(TAG, "Wifi display scan nest count became negative: " + this.mWifiDisplayScanNestCount);
                this.mWifiDisplayScanNestCount = 0;
            }
        }
    }

    public void connectWifiDisplay(String deviceAddress) {
        if (deviceAddress == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        }
        try {
            this.mDm.connectWifiDisplay(deviceAddress);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void pauseWifiDisplay() {
        try {
            this.mDm.pauseWifiDisplay();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void resumeWifiDisplay() {
        try {
            this.mDm.resumeWifiDisplay();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void disconnectWifiDisplay() {
        try {
            this.mDm.disconnectWifiDisplay();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void renameWifiDisplay(String deviceAddress, String alias) {
        if (deviceAddress == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        }
        try {
            this.mDm.renameWifiDisplay(deviceAddress, alias);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void forgetWifiDisplay(String deviceAddress) {
        if (deviceAddress == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        }
        try {
            this.mDm.forgetWifiDisplay(deviceAddress);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public WifiDisplayStatus getWifiDisplayStatus() {
        try {
            return this.mDm.getWifiDisplayStatus();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setUserDisabledHdrTypes(int[] userDisabledHdrTypes) {
        try {
            this.mDm.setUserDisabledHdrTypes(userDisabledHdrTypes);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setAreUserDisabledHdrTypesAllowed(boolean areUserDisabledHdrTypesAllowed) {
        try {
            this.mDm.setAreUserDisabledHdrTypesAllowed(areUserDisabledHdrTypesAllowed);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean areUserDisabledHdrTypesAllowed() {
        try {
            return this.mDm.areUserDisabledHdrTypesAllowed();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public int[] getUserDisabledHdrTypes() {
        try {
            return this.mDm.getUserDisabledHdrTypes();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void overrideHdrTypes(int displayId, int[] modes) {
        try {
            this.mDm.overrideHdrTypes(displayId, modes);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void requestColorMode(int displayId, int colorMode) {
        try {
            this.mDm.requestColorMode(displayId, colorMode);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public VirtualDisplay createVirtualDisplay(Context context, MediaProjection projection, VirtualDisplayConfig virtualDisplayConfig, VirtualDisplay.Callback callback, Executor executor, Context windowContext) {
        VirtualDisplayCallback callbackWrapper = new VirtualDisplayCallback(callback, executor);
        IMediaProjection projectionToken = projection != null ? projection.getProjection() : null;
        try {
            int displayId = this.mDm.createVirtualDisplay(virtualDisplayConfig, callbackWrapper, projectionToken, context.getPackageName());
            return createVirtualDisplayWrapper(virtualDisplayConfig, windowContext, callbackWrapper, displayId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public VirtualDisplay createVirtualDisplayWrapper(VirtualDisplayConfig virtualDisplayConfig, Context windowContext, IVirtualDisplayCallback callbackWrapper, int displayId) {
        if (displayId < 0) {
            Log.m110e(TAG, "Could not create virtual display: " + virtualDisplayConfig.getName());
            return null;
        }
        Display display = getRealDisplay(displayId);
        if (display == null) {
            Log.wtf(TAG, "Could not obtain display info for newly created virtual display: " + virtualDisplayConfig.getName());
            try {
                this.mDm.releaseVirtualDisplay(callbackWrapper);
                return null;
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
        return new VirtualDisplay(this, display, callbackWrapper, virtualDisplayConfig.getSurface(), windowContext);
    }

    public void setVirtualDisplaySurface(IVirtualDisplayCallback token, Surface surface) {
        try {
            this.mDm.setVirtualDisplaySurface(token, surface);
            setVirtualDisplayState(token, surface != null);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void resizeVirtualDisplay(IVirtualDisplayCallback token, int width, int height, int densityDpi) {
        try {
            this.mDm.resizeVirtualDisplay(token, width, height, densityDpi);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void releaseVirtualDisplay(IVirtualDisplayCallback token) {
        try {
            this.mDm.releaseVirtualDisplay(token);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setVirtualDisplayState(IVirtualDisplayCallback token, boolean isOn) {
        try {
            this.mDm.setVirtualDisplayState(token, isOn);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public Point getStableDisplaySize() {
        try {
            return this.mDm.getStableDisplaySize();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public List<BrightnessChangeEvent> getBrightnessEvents(String callingPackage) {
        try {
            ParceledListSlice<BrightnessChangeEvent> events = this.mDm.getBrightnessEvents(callingPackage);
            if (events == null) {
                return Collections.emptyList();
            }
            return events.getList();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public BrightnessInfo getBrightnessInfo(int displayId) {
        try {
            return this.mDm.getBrightnessInfo(displayId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public ColorSpace getPreferredWideGamutColorSpace() {
        return this.mWideColorSpace;
    }

    public OverlayProperties getOverlaySupport() {
        return this.mOverlayProperties;
    }

    public void setBrightnessConfigurationForUser(BrightnessConfiguration c, int userId, String packageName) {
        try {
            this.mDm.setBrightnessConfigurationForUser(c, userId, packageName);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setBrightnessConfigurationForDisplay(BrightnessConfiguration c, String uniqueDisplayId, int userId, String packageName) {
        try {
            this.mDm.setBrightnessConfigurationForDisplay(c, uniqueDisplayId, userId, packageName);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public BrightnessConfiguration getBrightnessConfigurationForDisplay(String uniqueDisplayId, int userId) {
        try {
            return this.mDm.getBrightnessConfigurationForDisplay(uniqueDisplayId, userId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public BrightnessConfiguration getBrightnessConfigurationForUser(int userId) {
        try {
            return this.mDm.getBrightnessConfigurationForUser(userId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public BrightnessConfiguration getDefaultBrightnessConfiguration() {
        try {
            return this.mDm.getDefaultBrightnessConfiguration();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean isMinimalPostProcessingRequested(int displayId) {
        try {
            return this.mDm.isMinimalPostProcessingRequested(displayId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setTemporaryBrightness(int displayId, float brightness) {
        try {
            this.mDm.setTemporaryBrightness(displayId, brightness);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setBrightness(int displayId, float brightness) {
        try {
            this.mDm.setBrightness(displayId, brightness);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public DisplayDecorationSupport getDisplayDecorationSupport(int displayId) {
        try {
            return this.mDm.getDisplayDecorationSupport(displayId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public float getBrightness(int displayId) {
        try {
            return this.mDm.getBrightness(displayId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setTemporaryAutoBrightnessAdjustment(float adjustment) {
        try {
            this.mDm.setTemporaryAutoBrightnessAdjustment(adjustment);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public Pair<float[], float[]> getMinimumBrightnessCurve() {
        try {
            Curve curve = this.mDm.getMinimumBrightnessCurve();
            return Pair.create(curve.getX(), curve.getY());
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public List<AmbientBrightnessDayStats> getAmbientBrightnessStats() {
        try {
            ParceledListSlice<AmbientBrightnessDayStats> stats = this.mDm.getAmbientBrightnessStats();
            if (stats == null) {
                return Collections.emptyList();
            }
            return stats.getList();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setUserPreferredDisplayMode(int displayId, Display.Mode mode) {
        try {
            this.mDm.setUserPreferredDisplayMode(displayId, mode);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public Display.Mode getUserPreferredDisplayMode(int displayId) {
        try {
            return this.mDm.getUserPreferredDisplayMode(displayId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public Display.Mode getSystemPreferredDisplayMode(int displayId) {
        try {
            return this.mDm.getSystemPreferredDisplayMode(displayId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setHdrConversionMode(HdrConversionMode hdrConversionMode) {
        try {
            this.mDm.setHdrConversionMode(hdrConversionMode);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public HdrConversionMode getHdrConversionModeSetting() {
        try {
            return this.mDm.getHdrConversionModeSetting();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public HdrConversionMode getHdrConversionMode() {
        try {
            return this.mDm.getHdrConversionMode();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public int[] getSupportedHdrOutputTypes() {
        try {
            return this.mDm.getSupportedHdrOutputTypes();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setShouldAlwaysRespectAppRequestedMode(boolean enabled) {
        try {
            this.mDm.setShouldAlwaysRespectAppRequestedMode(enabled);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean shouldAlwaysRespectAppRequestedMode() {
        try {
            return this.mDm.shouldAlwaysRespectAppRequestedMode();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setRefreshRateSwitchingType(int newValue) {
        try {
            this.mDm.setRefreshRateSwitchingType(newValue);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public int getRefreshRateSwitchingType() {
        try {
            return this.mDm.getRefreshRateSwitchingType();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class DisplayManagerCallback extends IDisplayManagerCallback.Stub {
        private DisplayManagerCallback() {
        }

        @Override // android.hardware.display.IDisplayManagerCallback
        public void onDisplayEvent(int displayId, int event) {
            DisplayManagerGlobal.this.handleDisplayEvent(displayId, event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class DisplayListenerDelegate {
        public volatile long mEventsMask;
        private final Executor mExecutor;
        public final DisplayManager.DisplayListener mListener;
        private final DisplayInfo mDisplayInfo = new DisplayInfo();
        private AtomicLong mGenerationId = new AtomicLong(1);

        DisplayListenerDelegate(DisplayManager.DisplayListener listener, Executor executor, long eventsMask) {
            this.mExecutor = executor;
            this.mListener = listener;
            this.mEventsMask = eventsMask;
        }

        public void sendDisplayEvent(int displayId, int event, DisplayInfo info) {
            final long generationId = this.mGenerationId.get();
            final Message msg = Message.obtain(null, event, displayId, 0, info);
            this.mExecutor.execute(new Runnable() { // from class: android.hardware.display.DisplayManagerGlobal$DisplayListenerDelegate$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayManagerGlobal.DisplayListenerDelegate.this.lambda$sendDisplayEvent$0(generationId, msg);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sendDisplayEvent$0(long generationId, Message msg) {
            if (generationId == this.mGenerationId.get()) {
                handleMessage(msg);
            }
            msg.recycle();
        }

        public void clearEvents() {
            this.mGenerationId.incrementAndGet();
        }

        public void setEventsMask(long newEventsMask) {
            this.mEventsMask = newEventsMask;
        }

        private void handleMessage(Message msg) {
            DisplayInfo newInfo;
            switch (msg.what) {
                case 1:
                    if ((this.mEventsMask & 1) != 0) {
                        this.mListener.onDisplayAdded(msg.arg1);
                        return;
                    }
                    return;
                case 2:
                    if ((this.mEventsMask & 4) != 0 && (newInfo = (DisplayInfo) msg.obj) != null && !newInfo.equals(this.mDisplayInfo)) {
                        this.mDisplayInfo.copyFrom(newInfo);
                        this.mListener.onDisplayChanged(msg.arg1);
                        return;
                    }
                    return;
                case 3:
                    if ((this.mEventsMask & 2) != 0) {
                        this.mListener.onDisplayRemoved(msg.arg1);
                        return;
                    }
                    return;
                case 4:
                    if ((this.mEventsMask & 8) != 0) {
                        this.mListener.onDisplayChanged(msg.arg1);
                        return;
                    }
                    return;
                case 5:
                    if ((this.mEventsMask & 16) != 0) {
                        this.mListener.onDisplayChanged(msg.arg1);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class VirtualDisplayCallback extends IVirtualDisplayCallback.Stub {
        private final VirtualDisplay.Callback mCallback;
        private final Executor mExecutor;

        public VirtualDisplayCallback(VirtualDisplay.Callback callback, Executor executor) {
            this.mCallback = callback;
            this.mExecutor = callback != null ? (Executor) Objects.requireNonNull(executor) : null;
        }

        @Override // android.hardware.display.IVirtualDisplayCallback
        public void onPaused() {
            final VirtualDisplay.Callback callback = this.mCallback;
            if (callback != null) {
                Executor executor = this.mExecutor;
                Objects.requireNonNull(callback);
                executor.execute(new Runnable() { // from class: android.hardware.display.DisplayManagerGlobal$VirtualDisplayCallback$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        VirtualDisplay.Callback.this.onPaused();
                    }
                });
            }
        }

        @Override // android.hardware.display.IVirtualDisplayCallback
        public void onResumed() {
            final VirtualDisplay.Callback callback = this.mCallback;
            if (callback != null) {
                Executor executor = this.mExecutor;
                Objects.requireNonNull(callback);
                executor.execute(new Runnable() { // from class: android.hardware.display.DisplayManagerGlobal$VirtualDisplayCallback$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        VirtualDisplay.Callback.this.onResumed();
                    }
                });
            }
        }

        @Override // android.hardware.display.IVirtualDisplayCallback
        public void onStopped() {
            final VirtualDisplay.Callback callback = this.mCallback;
            if (callback != null) {
                Executor executor = this.mExecutor;
                Objects.requireNonNull(callback);
                executor.execute(new Runnable() { // from class: android.hardware.display.DisplayManagerGlobal$VirtualDisplayCallback$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        VirtualDisplay.Callback.this.onStopped();
                    }
                });
            }
        }
    }

    public static void invalidateLocalDisplayInfoCaches() {
        PropertyInvalidatedCache.invalidateCache(CACHE_KEY_DISPLAY_INFO_PROPERTY);
    }

    public void disableLocalDisplayInfoCaches() {
        this.mDisplayCache = null;
    }

    public void registerNativeChoreographerForRefreshRateCallbacks() {
        synchronized (this.mLock) {
            this.mDispatchNativeCallbacks = true;
            registerCallbackIfNeededLocked();
            updateCallbackIfNeededLocked();
            DisplayInfo display = getDisplayInfoLocked(0);
            if (display != null) {
                float refreshRate = display.getRefreshRate();
                this.mNativeCallbackReportedRefreshRate = refreshRate;
                nSignalNativeCallbacks(refreshRate);
            }
        }
    }

    public void unregisterNativeChoreographerForRefreshRateCallbacks() {
        synchronized (this.mLock) {
            this.mDispatchNativeCallbacks = false;
            updateCallbackIfNeededLocked();
        }
    }

    private static String eventToString(int event) {
        switch (event) {
            case 1:
                return "ADDED";
            case 2:
                return "CHANGED";
            case 3:
                return "REMOVED";
            case 4:
                return "BRIGHTNESS_CHANGED";
            case 5:
                return "HDR_SDR_RATIO_CHANGED";
            default:
                return "UNKNOWN";
        }
    }
}
