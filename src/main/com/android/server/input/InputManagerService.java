package com.android.server.input;

import android.annotation.EnforcePermission;
import android.app.ActivityManagerInternal;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.hardware.SensorPrivacyManagerInternal;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.display.DisplayViewport;
import android.hardware.input.HostUsiVersion;
import android.hardware.input.IInputDeviceBatteryListener;
import android.hardware.input.IInputDeviceBatteryState;
import android.hardware.input.IInputDevicesChangedListener;
import android.hardware.input.IInputManager;
import android.hardware.input.IInputSensorEventListener;
import android.hardware.input.IKeyboardBacklightListener;
import android.hardware.input.ITabletModeChangedListener;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.InputSensorInfo;
import android.hardware.input.KeyboardLayout;
import android.hardware.input.TouchCalibration;
import android.hardware.lights.Light;
import android.hardware.lights.LightState;
import android.media.AudioManager;
import android.os.Binder;
import android.os.CombinedVibration;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.IVibratorStateListener;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.SystemProperties;
import android.os.VibrationEffect;
import android.os.vibrator.StepSegment;
import android.os.vibrator.VibrationEffectSegment;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.IInputFilter;
import android.view.IInputFilterHost;
import android.view.IInputMonitorHost;
import android.view.InputApplicationHandle;
import android.view.InputChannel;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.InputMonitor;
import android.view.KeyEvent;
import android.view.PointerIcon;
import android.view.SurfaceControl;
import android.view.VerifiedInputEvent;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.inputmethod.InputMethodSubtypeHandle;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.Preconditions;
import com.android.server.DisplayThread;
import com.android.server.LocalServices;
import com.android.server.Watchdog;
import com.android.server.input.InputManagerInternal;
import com.android.server.input.InputManagerService;
import com.android.server.input.NativeInputManagerService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class InputManagerService extends IInputManager.Stub implements Watchdog.Monitor {
    public static final boolean DEBUG = Log.isLoggable("InputManager", 3);
    public static final AdditionalDisplayInputProperties DEFAULT_ADDITIONAL_DISPLAY_INPUT_PROPERTIES = new AdditionalDisplayInputProperties();
    public static final boolean KEYBOARD_BACKLIGHT_CONTROL_ENABLED = SystemProperties.getBoolean("persist.input.keyboard.backlight_control.enabled", true);
    @GuardedBy({"mAdditionalDisplayInputPropertiesLock"})
    public int mAcknowledgedPointerDisplayId;
    @GuardedBy({"mAdditionalDisplayInputPropertiesLock"})
    public final SparseArray<AdditionalDisplayInputProperties> mAdditionalDisplayInputProperties;
    public final Object mAdditionalDisplayInputPropertiesLock;
    public final Object mAssociationsLock;
    public final BatteryController mBatteryController;
    public final Context mContext;
    @GuardedBy({"mAdditionalDisplayInputPropertiesLock"})
    public final AdditionalDisplayInputProperties mCurrentDisplayProperties;
    public final PersistentDataStore mDataStore;
    @GuardedBy({"mAssociationsLock"})
    public final Map<String, String> mDeviceTypeAssociations;
    public DisplayManagerInternal mDisplayManagerInternal;
    public final File mDoubleTouchGestureEnableFile;
    public final InputManagerHandler mHandler;
    @GuardedBy({"mInputDevicesLock"})
    public InputDevice[] mInputDevices;
    @GuardedBy({"mInputDevicesLock"})
    public final SparseArray<InputDevicesChangedListenerRecord> mInputDevicesChangedListeners;
    @GuardedBy({"mInputDevicesLock"})
    public boolean mInputDevicesChangedPending;
    public final Object mInputDevicesLock;
    @GuardedBy({"mInputFilterLock"})
    public IInputFilter mInputFilter;
    @GuardedBy({"mInputFilterLock"})
    public InputFilterHost mInputFilterHost;
    public final Object mInputFilterLock;
    @GuardedBy({"mInputMonitors"})
    public final Map<IBinder, GestureMonitorSpyWindow> mInputMonitors;
    @GuardedBy({"mVibratorLock"})
    public final SparseBooleanArray mIsVibrating;
    public final KeyRemapper mKeyRemapper;
    public final KeyboardBacklightControllerInterface mKeyboardBacklightController;
    @GuardedBy({"mAssociationsLock"})
    public final Map<String, String> mKeyboardLayoutAssociations;
    public final KeyboardLayoutManager mKeyboardLayoutManager;
    @GuardedBy({"mLidSwitchLock"})
    public final List<InputManagerInternal.LidSwitchCallback> mLidSwitchCallbacks;
    public final Object mLidSwitchLock;
    public final Object mLightLock;
    @GuardedBy({"mLightLock"})
    public final ArrayMap<IBinder, LightSession> mLightSessions;
    public final NativeInputManagerService mNative;
    public int mNextVibratorTokenValue;
    @GuardedBy({"mAdditionalDisplayInputPropertiesLock"})
    public int mOverriddenPointerDisplayId;
    @GuardedBy({"mAdditionalDisplayInputPropertiesLock"})
    public PointerIcon mPointerIcon;
    public Context mPointerIconDisplayContext;
    @GuardedBy({"mAdditionalDisplayInputPropertiesLock"})
    public int mPointerIconType;
    @GuardedBy({"mAdditionalDisplayInputPropertiesLock"})
    public int mRequestedPointerDisplayId;
    @GuardedBy({"mAssociationsLock"})
    public final Map<String, Integer> mRuntimeAssociations;
    public final List<SensorEventListenerRecord> mSensorAccuracyListenersToNotify;
    @GuardedBy({"mSensorEventLock"})
    public final SparseArray<SensorEventListenerRecord> mSensorEventListeners;
    public final List<SensorEventListenerRecord> mSensorEventListenersToNotify;
    public final Object mSensorEventLock;
    public final InputSettingsObserver mSettingsObserver;
    public final Map<String, Integer> mStaticAssociations;
    public boolean mSystemReady;
    @GuardedBy({"mTabletModeLock"})
    public final SparseArray<TabletModeChangedListenerRecord> mTabletModeChangedListeners;
    public final Object mTabletModeLock;
    public final ArrayList<InputDevicesChangedListenerRecord> mTempInputDevicesChangedListenersToNotify;
    public final List<TabletModeChangedListenerRecord> mTempTabletModeChangedListenersToNotify;
    @GuardedBy({"mAssociationsLock"})
    public final Map<String, String> mUniqueIdAssociations;
    public final boolean mUseDevInputEventForAudioJack;
    public final String mVelocityTrackerStrategy;
    public final Object mVibratorLock;
    @GuardedBy({"mVibratorLock"})
    public final SparseArray<RemoteCallbackList<IVibratorStateListener>> mVibratorStateListeners;
    public final Map<IBinder, VibratorToken> mVibratorTokens;
    public WindowManagerCallbacks mWindowManagerCallbacks;
    public WiredAccessoryCallbacks mWiredAccessoryCallbacks;

    /* loaded from: classes.dex */
    public interface KeyboardBacklightControllerInterface {
        default void decrementKeyboardBacklight(int i) {
        }

        default void dump(PrintWriter printWriter) {
        }

        default void incrementKeyboardBacklight(int i) {
        }

        default void notifyUserActivity() {
        }

        default void onInteractiveChanged(boolean z) {
        }

        default void registerKeyboardBacklightListener(IKeyboardBacklightListener iKeyboardBacklightListener, int i) {
        }

        default void systemRunning() {
        }

        default void unregisterKeyboardBacklightListener(IKeyboardBacklightListener iKeyboardBacklightListener, int i) {
        }
    }

    /* loaded from: classes.dex */
    public interface WindowManagerCallbacks extends InputManagerInternal.LidSwitchCallback {
        SurfaceControl createSurfaceForGestureMonitor(String str, int i);

        KeyEvent dispatchUnhandledKey(IBinder iBinder, KeyEvent keyEvent, int i);

        PointF getCursorPosition();

        SurfaceControl getParentSurfaceForPointers(int i);

        int getPointerDisplayId();

        int getPointerLayer();

        long interceptKeyBeforeDispatching(IBinder iBinder, KeyEvent keyEvent, int i);

        int interceptKeyBeforeQueueing(KeyEvent keyEvent, int i);

        int interceptMotionBeforeQueueingNonInteractive(int i, long j, int i2);

        void notifyCameraLensCoverSwitchChanged(long j, boolean z);

        void notifyConfigurationChanged();

        void notifyDropWindow(IBinder iBinder, float f, float f2);

        void notifyFocusChanged(IBinder iBinder, IBinder iBinder2);

        void notifyInputChannelBroken(IBinder iBinder);

        void notifyNoFocusedWindowAnr(InputApplicationHandle inputApplicationHandle);

        void notifyPointerDisplayIdChanged(int i, float f, float f2);

        void notifyWindowResponsive(IBinder iBinder, OptionalInt optionalInt);

        void notifyWindowUnresponsive(IBinder iBinder, OptionalInt optionalInt, String str);

        void onPointerDownOutsideFocus(IBinder iBinder);
    }

    /* loaded from: classes.dex */
    public interface WiredAccessoryCallbacks {
        void notifyWiredAccessoryChanged(long j, int i, int i2);

        void systemReady();
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public final Context mContext;
        public final Looper mLooper;

        public Injector(Context context, Looper looper) {
            this.mContext = context;
            this.mLooper = looper;
        }

        public Context getContext() {
            return this.mContext;
        }

        public Looper getLooper() {
            return this.mLooper;
        }

        public NativeInputManagerService getNativeService(InputManagerService inputManagerService) {
            return new NativeInputManagerService.NativeImpl(inputManagerService, this.mLooper.getQueue());
        }

        public void registerLocalService(InputManagerInternal inputManagerInternal) {
            LocalServices.addService(InputManagerInternal.class, inputManagerInternal);
        }
    }

    public InputManagerService(Context context) {
        this(new Injector(context, DisplayThread.get().getLooper()));
    }

    @VisibleForTesting
    public InputManagerService(Injector injector) {
        KeyboardBacklightControllerInterface keyboardBacklightControllerInterface;
        this.mTabletModeLock = new Object();
        this.mTabletModeChangedListeners = new SparseArray<>();
        this.mTempTabletModeChangedListenersToNotify = new ArrayList();
        this.mSensorEventLock = new Object();
        this.mSensorEventListeners = new SparseArray<>();
        this.mSensorEventListenersToNotify = new ArrayList();
        this.mSensorAccuracyListenersToNotify = new ArrayList();
        PersistentDataStore persistentDataStore = new PersistentDataStore();
        this.mDataStore = persistentDataStore;
        this.mInputDevicesLock = new Object();
        this.mInputDevices = new InputDevice[0];
        this.mInputDevicesChangedListeners = new SparseArray<>();
        this.mTempInputDevicesChangedListenersToNotify = new ArrayList<>();
        this.mVibratorLock = new Object();
        this.mVibratorTokens = new ArrayMap();
        this.mVibratorStateListeners = new SparseArray<>();
        this.mIsVibrating = new SparseBooleanArray();
        this.mLightLock = new Object();
        this.mLightSessions = new ArrayMap<>();
        this.mLidSwitchLock = new Object();
        this.mLidSwitchCallbacks = new ArrayList();
        this.mInputFilterLock = new Object();
        this.mAssociationsLock = new Object();
        this.mRuntimeAssociations = new ArrayMap();
        this.mUniqueIdAssociations = new ArrayMap();
        this.mKeyboardLayoutAssociations = new ArrayMap();
        this.mDeviceTypeAssociations = new ArrayMap();
        this.mAdditionalDisplayInputPropertiesLock = new Object();
        this.mOverriddenPointerDisplayId = -1;
        this.mAcknowledgedPointerDisplayId = -1;
        this.mRequestedPointerDisplayId = -1;
        this.mAdditionalDisplayInputProperties = new SparseArray<>();
        this.mCurrentDisplayProperties = new AdditionalDisplayInputProperties();
        this.mPointerIconType = 1;
        this.mInputMonitors = new HashMap();
        this.mStaticAssociations = loadStaticInputPortAssociations();
        Context context = injector.getContext();
        this.mContext = context;
        InputManagerHandler inputManagerHandler = new InputManagerHandler(injector.getLooper());
        this.mHandler = inputManagerHandler;
        NativeInputManagerService nativeService = injector.getNativeService(this);
        this.mNative = nativeService;
        this.mSettingsObserver = new InputSettingsObserver(context, inputManagerHandler, nativeService);
        this.mKeyboardLayoutManager = new KeyboardLayoutManager(context, nativeService, persistentDataStore, injector.getLooper());
        this.mBatteryController = new BatteryController(context, nativeService, injector.getLooper());
        if (KEYBOARD_BACKLIGHT_CONTROL_ENABLED) {
            keyboardBacklightControllerInterface = new KeyboardBacklightController(context, nativeService, persistentDataStore, injector.getLooper());
        } else {
            keyboardBacklightControllerInterface = new KeyboardBacklightControllerInterface() { // from class: com.android.server.input.InputManagerService.1
            };
        }
        this.mKeyboardBacklightController = keyboardBacklightControllerInterface;
        this.mKeyRemapper = new KeyRemapper(context, nativeService, persistentDataStore, injector.getLooper());
        boolean z = context.getResources().getBoolean(17891859);
        this.mUseDevInputEventForAudioJack = z;
        Slog.i("InputManager", "Initializing input manager, mUseDevInputEventForAudioJack=" + z);
        String string = context.getResources().getString(17039924);
        this.mDoubleTouchGestureEnableFile = TextUtils.isEmpty(string) ? null : new File(string);
        this.mVelocityTrackerStrategy = DeviceConfig.getProperty("input_native_boot", "velocitytracker_strategy");
        injector.registerLocalService(new LocalService());
    }

    public void setWindowManagerCallbacks(WindowManagerCallbacks windowManagerCallbacks) {
        WindowManagerCallbacks windowManagerCallbacks2 = this.mWindowManagerCallbacks;
        if (windowManagerCallbacks2 != null) {
            unregisterLidSwitchCallbackInternal(windowManagerCallbacks2);
        }
        this.mWindowManagerCallbacks = windowManagerCallbacks;
        registerLidSwitchCallbackInternal(windowManagerCallbacks);
    }

    public void setWiredAccessoryCallbacks(WiredAccessoryCallbacks wiredAccessoryCallbacks) {
        this.mWiredAccessoryCallbacks = wiredAccessoryCallbacks;
    }

    public void registerLidSwitchCallbackInternal(InputManagerInternal.LidSwitchCallback lidSwitchCallback) {
        synchronized (this.mLidSwitchLock) {
            this.mLidSwitchCallbacks.add(lidSwitchCallback);
            if (this.mSystemReady) {
                lidSwitchCallback.notifyLidSwitchChanged(0L, getSwitchState(-1, -256, 0) == 0);
            }
        }
    }

    public void unregisterLidSwitchCallbackInternal(InputManagerInternal.LidSwitchCallback lidSwitchCallback) {
        synchronized (this.mLidSwitchLock) {
            this.mLidSwitchCallbacks.remove(lidSwitchCallback);
        }
    }

    public void start() {
        Slog.i("InputManager", "Starting input manager");
        this.mNative.start();
        Watchdog.getInstance().addMonitor(this);
        this.mSettingsObserver.registerAndUpdate();
    }

    public void systemRunning() {
        if (DEBUG) {
            Slog.d("InputManager", "System ready.");
        }
        this.mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
        synchronized (this.mLidSwitchLock) {
            this.mSystemReady = true;
            int switchState = getSwitchState(-1, -256, 0);
            for (int i = 0; i < this.mLidSwitchCallbacks.size(); i++) {
                this.mLidSwitchCallbacks.get(i).notifyLidSwitchChanged(0L, switchState == 0);
            }
        }
        if (getSwitchState(-1, -256, 14) == 1) {
            setSensorPrivacy(1, true);
        }
        if (getSwitchState(-1, -256, 9) == 1) {
            setSensorPrivacy(2, true);
        }
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.input.InputManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                InputManagerService.this.reloadDeviceAliases();
            }
        }, new IntentFilter("android.bluetooth.device.action.ALIAS_CHANGED"), null, this.mHandler);
        this.mHandler.sendEmptyMessage(2);
        WiredAccessoryCallbacks wiredAccessoryCallbacks = this.mWiredAccessoryCallbacks;
        if (wiredAccessoryCallbacks != null) {
            wiredAccessoryCallbacks.systemReady();
        }
        this.mKeyboardLayoutManager.systemRunning();
        this.mBatteryController.systemRunning();
        this.mKeyboardBacklightController.systemRunning();
        this.mKeyRemapper.systemRunning();
    }

    public final void reloadDeviceAliases() {
        if (DEBUG) {
            Slog.d("InputManager", "Reloading device names.");
        }
        this.mNative.reloadDeviceAliases();
    }

    public final void setDisplayViewportsInternal(List<DisplayViewport> list) {
        DisplayViewport[] displayViewportArr = new DisplayViewport[list.size()];
        for (int size = list.size() - 1; size >= 0; size--) {
            displayViewportArr[size] = list.get(size);
        }
        this.mNative.setDisplayViewports(displayViewportArr);
        int pointerDisplayId = this.mWindowManagerCallbacks.getPointerDisplayId();
        synchronized (this.mAdditionalDisplayInputPropertiesLock) {
            if (this.mOverriddenPointerDisplayId == -1) {
                updatePointerDisplayIdLocked(pointerDisplayId);
            }
        }
    }

    public int getKeyCodeState(int i, int i2, int i3) {
        return this.mNative.getKeyCodeState(i, i2, i3);
    }

    public int getScanCodeState(int i, int i2, int i3) {
        return this.mNative.getScanCodeState(i, i2, i3);
    }

    public int getSwitchState(int i, int i2, int i3) {
        return this.mNative.getSwitchState(i, i2, i3);
    }

    public boolean hasKeys(int i, int i2, int[] iArr, boolean[] zArr) {
        Objects.requireNonNull(iArr, "keyCodes must not be null");
        Objects.requireNonNull(zArr, "keyExists must not be null");
        if (zArr.length < iArr.length) {
            throw new IllegalArgumentException("keyExists must be at least as large as keyCodes");
        }
        return this.mNative.hasKeys(i, i2, iArr, zArr);
    }

    public int getKeyCodeForKeyLocation(int i, int i2) {
        if (i2 <= 0 || i2 > KeyEvent.getMaxKeyCode()) {
            return 0;
        }
        return this.mNative.getKeyCodeForKeyLocation(i, i2);
    }

    public boolean transferTouch(IBinder iBinder, int i) {
        Objects.requireNonNull(iBinder, "destChannelToken must not be null");
        return this.mNative.transferTouch(iBinder, i);
    }

    public InputChannel monitorInput(String str, int i) {
        Objects.requireNonNull(str, "inputChannelName not be null");
        if (i < 0) {
            throw new IllegalArgumentException("displayId must >= 0.");
        }
        return this.mNative.createInputMonitor(i, str, Binder.getCallingPid());
    }

    public final InputChannel createSpyWindowGestureMonitor(IBinder iBinder, String str, int i, int i2, int i3) {
        SurfaceControl createSurfaceForGestureMonitor = this.mWindowManagerCallbacks.createSurfaceForGestureMonitor(str, i);
        if (createSurfaceForGestureMonitor == null) {
            throw new IllegalArgumentException("Could not create gesture monitor surface on display: " + i);
        }
        final InputChannel createInputChannel = createInputChannel(str);
        try {
            iBinder.linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.input.InputManagerService$$ExternalSyntheticLambda7
                @Override // android.os.IBinder.DeathRecipient
                public final void binderDied() {
                    InputManagerService.this.lambda$createSpyWindowGestureMonitor$0(createInputChannel);
                }
            }, 0);
            synchronized (this.mInputMonitors) {
                this.mInputMonitors.put(createInputChannel.getToken(), new GestureMonitorSpyWindow(iBinder, str, i, i2, i3, createSurfaceForGestureMonitor, createInputChannel));
            }
            InputChannel inputChannel = new InputChannel();
            createInputChannel.copyTo(inputChannel);
            return inputChannel;
        } catch (RemoteException unused) {
            Slog.i("InputManager", "Client died before '" + str + "' could be created.");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createSpyWindowGestureMonitor$0(InputChannel inputChannel) {
        removeSpyWindowGestureMonitor(inputChannel.getToken());
    }

    public final void removeSpyWindowGestureMonitor(IBinder iBinder) {
        GestureMonitorSpyWindow remove;
        synchronized (this.mInputMonitors) {
            remove = this.mInputMonitors.remove(iBinder);
        }
        removeInputChannel(iBinder);
        if (remove == null) {
            return;
        }
        remove.remove();
    }

    public InputMonitor monitorGestureInput(IBinder iBinder, String str, int i) {
        if (!checkCallingPermission("android.permission.MONITOR_INPUT", "monitorGestureInput()")) {
            throw new SecurityException("Requires MONITOR_INPUT permission");
        }
        Objects.requireNonNull(str, "name must not be null.");
        Objects.requireNonNull(iBinder, "token must not be null.");
        if (i < 0) {
            throw new IllegalArgumentException("displayId must >= 0.");
        }
        String str2 = "[Gesture Monitor] " + str;
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            InputChannel createSpyWindowGestureMonitor = createSpyWindowGestureMonitor(iBinder, str2, i, callingPid, callingUid);
            return new InputMonitor(createSpyWindowGestureMonitor, new InputMonitorHost(createSpyWindowGestureMonitor.getToken()));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public InputChannel createInputChannel(String str) {
        return this.mNative.createInputChannel(str);
    }

    public void removeInputChannel(IBinder iBinder) {
        Objects.requireNonNull(iBinder, "connectionToken must not be null");
        this.mNative.removeInputChannel(iBinder);
    }

    public void setInputFilter(IInputFilter iInputFilter) {
        synchronized (this.mInputFilterLock) {
            IInputFilter iInputFilter2 = this.mInputFilter;
            if (iInputFilter2 == iInputFilter) {
                return;
            }
            if (iInputFilter2 != null) {
                this.mInputFilter = null;
                this.mInputFilterHost.disconnectLocked();
                this.mInputFilterHost = null;
                try {
                    iInputFilter2.uninstall();
                } catch (RemoteException unused) {
                }
            }
            if (iInputFilter != null) {
                this.mInputFilter = iInputFilter;
                InputFilterHost inputFilterHost = new InputFilterHost();
                this.mInputFilterHost = inputFilterHost;
                try {
                    iInputFilter.install(inputFilterHost);
                } catch (RemoteException unused2) {
                }
            }
            this.mNative.setInputFilterEnabled(iInputFilter != null);
        }
    }

    public boolean setInTouchMode(boolean z, int i, int i2, boolean z2, int i3) {
        return this.mNative.setInTouchMode(z, i, i2, z2, i3);
    }

    public boolean injectInputEvent(InputEvent inputEvent, int i) {
        return injectInputEventToTarget(inputEvent, i, -1);
    }

    public boolean injectInputEventToTarget(InputEvent inputEvent, int i, int i2) {
        if (!checkCallingPermission("android.permission.INJECT_EVENTS", "injectInputEvent()", true)) {
            throw new SecurityException("Injecting input events requires the caller (or the source of the instrumentation, if any) to have the INJECT_EVENTS permission.");
        }
        Objects.requireNonNull(inputEvent, "event must not be null");
        if (i != 0 && i != 2 && i != 1) {
            throw new IllegalArgumentException("mode is invalid");
        }
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        boolean z = i2 != -1;
        try {
            int injectInputEvent = this.mNative.injectInputEvent(inputEvent, z, i2, i, 30000, 134217728);
            if (injectInputEvent != 0) {
                if (injectInputEvent == 1) {
                    if (!z) {
                        throw new IllegalStateException("Injection should not result in TARGET_MISMATCH when it is not targeted into to a specific uid.");
                    }
                    throw new IllegalArgumentException("Targeted input event injection from pid " + callingPid + " was not directed at a window owned by uid " + i2 + ".");
                } else if (injectInputEvent == 3) {
                    Slog.w("InputManager", "Input event injection from pid " + callingPid + " timed out.");
                    return false;
                } else {
                    Slog.w("InputManager", "Input event injection from pid " + callingPid + " failed.");
                    return false;
                }
            }
            return true;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public VerifiedInputEvent verifyInputEvent(InputEvent inputEvent) {
        Objects.requireNonNull(inputEvent, "event must not be null");
        return this.mNative.verifyInputEvent(inputEvent);
    }

    public String getVelocityTrackerStrategy() {
        return this.mVelocityTrackerStrategy;
    }

    public InputDevice getInputDevice(int i) {
        InputDevice[] inputDeviceArr;
        synchronized (this.mInputDevicesLock) {
            for (InputDevice inputDevice : this.mInputDevices) {
                if (inputDevice.getId() == i) {
                    return inputDevice;
                }
            }
            return null;
        }
    }

    public boolean isInputDeviceEnabled(int i) {
        return this.mNative.isInputDeviceEnabled(i);
    }

    public void enableInputDevice(int i) {
        if (!checkCallingPermission("android.permission.DISABLE_INPUT_DEVICE", "enableInputDevice()")) {
            throw new SecurityException("Requires DISABLE_INPUT_DEVICE permission");
        }
        this.mNative.enableInputDevice(i);
    }

    public void disableInputDevice(int i) {
        if (!checkCallingPermission("android.permission.DISABLE_INPUT_DEVICE", "disableInputDevice()")) {
            throw new SecurityException("Requires DISABLE_INPUT_DEVICE permission");
        }
        this.mNative.disableInputDevice(i);
    }

    public int[] getInputDeviceIds() {
        int[] iArr;
        synchronized (this.mInputDevicesLock) {
            int length = this.mInputDevices.length;
            iArr = new int[length];
            for (int i = 0; i < length; i++) {
                iArr[i] = this.mInputDevices[i].getId();
            }
        }
        return iArr;
    }

    public InputDevice[] getInputDevices() {
        InputDevice[] inputDeviceArr;
        synchronized (this.mInputDevicesLock) {
            inputDeviceArr = this.mInputDevices;
        }
        return inputDeviceArr;
    }

    public void registerInputDevicesChangedListener(IInputDevicesChangedListener iInputDevicesChangedListener) {
        Objects.requireNonNull(iInputDevicesChangedListener, "listener must not be null");
        synchronized (this.mInputDevicesLock) {
            int callingPid = Binder.getCallingPid();
            if (this.mInputDevicesChangedListeners.get(callingPid) != null) {
                throw new SecurityException("The calling process has already registered an InputDevicesChangedListener.");
            }
            InputDevicesChangedListenerRecord inputDevicesChangedListenerRecord = new InputDevicesChangedListenerRecord(callingPid, iInputDevicesChangedListener);
            try {
                iInputDevicesChangedListener.asBinder().linkToDeath(inputDevicesChangedListenerRecord, 0);
                this.mInputDevicesChangedListeners.put(callingPid, inputDevicesChangedListenerRecord);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public final void onInputDevicesChangedListenerDied(int i) {
        synchronized (this.mInputDevicesLock) {
            this.mInputDevicesChangedListeners.remove(i);
        }
    }

    public final void deliverInputDevicesChanged(InputDevice[] inputDeviceArr) {
        this.mTempInputDevicesChangedListenersToNotify.clear();
        synchronized (this.mInputDevicesLock) {
            if (this.mInputDevicesChangedPending) {
                this.mInputDevicesChangedPending = false;
                int size = this.mInputDevicesChangedListeners.size();
                for (int i = 0; i < size; i++) {
                    this.mTempInputDevicesChangedListenersToNotify.add(this.mInputDevicesChangedListeners.valueAt(i));
                }
                int length = this.mInputDevices.length;
                int[] iArr = new int[length * 2];
                for (int i2 = 0; i2 < length; i2++) {
                    InputDevice inputDevice = this.mInputDevices[i2];
                    int i3 = i2 * 2;
                    iArr[i3] = inputDevice.getId();
                    iArr[i3 + 1] = inputDevice.getGeneration();
                    if (DEBUG) {
                        Log.d("InputManager", "device " + inputDevice.getId() + " generation " + inputDevice.getGeneration());
                    }
                }
                for (int i4 = 0; i4 < size; i4++) {
                    this.mTempInputDevicesChangedListenersToNotify.get(i4).notifyInputDevicesChanged(iArr);
                }
                this.mTempInputDevicesChangedListenersToNotify.clear();
            }
        }
    }

    public TouchCalibration getTouchCalibrationForInputDevice(String str, int i) {
        TouchCalibration touchCalibration;
        Objects.requireNonNull(str, "inputDeviceDescriptor must not be null");
        synchronized (this.mDataStore) {
            touchCalibration = this.mDataStore.getTouchCalibration(str, i);
        }
        return touchCalibration;
    }

    public void setTouchCalibrationForInputDevice(String str, int i, TouchCalibration touchCalibration) {
        if (!checkCallingPermission("android.permission.SET_INPUT_CALIBRATION", "setTouchCalibrationForInputDevice()")) {
            throw new SecurityException("Requires SET_INPUT_CALIBRATION permission");
        }
        Objects.requireNonNull(str, "inputDeviceDescriptor must not be null");
        Objects.requireNonNull(touchCalibration, "calibration must not be null");
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("surfaceRotation value out of bounds");
        }
        synchronized (this.mDataStore) {
            if (this.mDataStore.setTouchCalibration(str, i, touchCalibration)) {
                this.mNative.reloadCalibration();
            }
            this.mDataStore.saveIfNeeded();
        }
    }

    public int isInTabletMode() {
        if (!checkCallingPermission("android.permission.TABLET_MODE", "isInTabletMode()")) {
            throw new SecurityException("Requires TABLET_MODE permission");
        }
        return getSwitchState(-1, -256, 1);
    }

    public int isMicMuted() {
        return getSwitchState(-1, -256, 14);
    }

    public void registerTabletModeChangedListener(ITabletModeChangedListener iTabletModeChangedListener) {
        if (!checkCallingPermission("android.permission.TABLET_MODE", "registerTabletModeChangedListener()")) {
            throw new SecurityException("Requires TABLET_MODE_LISTENER permission");
        }
        Objects.requireNonNull(iTabletModeChangedListener, "event must not be null");
        synchronized (this.mTabletModeLock) {
            int callingPid = Binder.getCallingPid();
            if (this.mTabletModeChangedListeners.get(callingPid) != null) {
                throw new IllegalStateException("The calling process has already registered a TabletModeChangedListener.");
            }
            TabletModeChangedListenerRecord tabletModeChangedListenerRecord = new TabletModeChangedListenerRecord(callingPid, iTabletModeChangedListener);
            try {
                iTabletModeChangedListener.asBinder().linkToDeath(tabletModeChangedListenerRecord, 0);
                this.mTabletModeChangedListeners.put(callingPid, tabletModeChangedListenerRecord);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public final void onTabletModeChangedListenerDied(int i) {
        synchronized (this.mTabletModeLock) {
            this.mTabletModeChangedListeners.remove(i);
        }
    }

    public final void deliverTabletModeChanged(long j, boolean z) {
        int size;
        int i;
        this.mTempTabletModeChangedListenersToNotify.clear();
        synchronized (this.mTabletModeLock) {
            size = this.mTabletModeChangedListeners.size();
            for (int i2 = 0; i2 < size; i2++) {
                this.mTempTabletModeChangedListenersToNotify.add(this.mTabletModeChangedListeners.valueAt(i2));
            }
        }
        for (i = 0; i < size; i++) {
            this.mTempTabletModeChangedListenersToNotify.get(i).notifyTabletModeChanged(j, z);
        }
    }

    public KeyboardLayout[] getKeyboardLayouts() {
        return this.mKeyboardLayoutManager.getKeyboardLayouts();
    }

    public KeyboardLayout[] getKeyboardLayoutsForInputDevice(InputDeviceIdentifier inputDeviceIdentifier) {
        return this.mKeyboardLayoutManager.getKeyboardLayoutsForInputDevice(inputDeviceIdentifier);
    }

    public KeyboardLayout getKeyboardLayout(String str) {
        return this.mKeyboardLayoutManager.getKeyboardLayout(str);
    }

    public String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier) {
        return this.mKeyboardLayoutManager.getCurrentKeyboardLayoutForInputDevice(inputDeviceIdentifier);
    }

    @EnforcePermission("android.permission.SET_KEYBOARD_LAYOUT")
    public void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, String str) {
        super.setCurrentKeyboardLayoutForInputDevice_enforcePermission();
        this.mKeyboardLayoutManager.setCurrentKeyboardLayoutForInputDevice(inputDeviceIdentifier, str);
    }

    public String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier inputDeviceIdentifier) {
        return this.mKeyboardLayoutManager.getEnabledKeyboardLayoutsForInputDevice(inputDeviceIdentifier);
    }

    @EnforcePermission("android.permission.SET_KEYBOARD_LAYOUT")
    public void addKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, String str) {
        super.addKeyboardLayoutForInputDevice_enforcePermission();
        this.mKeyboardLayoutManager.addKeyboardLayoutForInputDevice(inputDeviceIdentifier, str);
    }

    @EnforcePermission("android.permission.SET_KEYBOARD_LAYOUT")
    public void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, String str) {
        super.removeKeyboardLayoutForInputDevice_enforcePermission();
        this.mKeyboardLayoutManager.removeKeyboardLayoutForInputDevice(inputDeviceIdentifier, str);
    }

    public String getKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, int i, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
        return this.mKeyboardLayoutManager.getKeyboardLayoutForInputDevice(inputDeviceIdentifier, i, inputMethodInfo, inputMethodSubtype);
    }

    @EnforcePermission("android.permission.SET_KEYBOARD_LAYOUT")
    public void setKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, int i, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype, String str) {
        super.setKeyboardLayoutForInputDevice_enforcePermission();
        this.mKeyboardLayoutManager.setKeyboardLayoutForInputDevice(inputDeviceIdentifier, i, inputMethodInfo, inputMethodSubtype, str);
    }

    public KeyboardLayout[] getKeyboardLayoutListForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, int i, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
        return this.mKeyboardLayoutManager.getKeyboardLayoutListForInputDevice(inputDeviceIdentifier, i, inputMethodInfo, inputMethodSubtype);
    }

    public void switchKeyboardLayout(int i, int i2) {
        this.mKeyboardLayoutManager.switchKeyboardLayout(i, i2);
    }

    public void setFocusedApplication(int i, InputApplicationHandle inputApplicationHandle) {
        this.mNative.setFocusedApplication(i, inputApplicationHandle);
    }

    public void setFocusedDisplay(int i) {
        this.mNative.setFocusedDisplay(i);
    }

    public void onDisplayRemoved(int i) {
        Context context = this.mPointerIconDisplayContext;
        if (context != null && context.getDisplay().getDisplayId() == i) {
            this.mPointerIconDisplayContext = null;
        }
        updateAdditionalDisplayInputProperties(i, new Consumer() { // from class: com.android.server.input.InputManagerService$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((InputManagerService.AdditionalDisplayInputProperties) obj).reset();
            }
        });
        this.mNative.displayRemoved(i);
    }

    public void requestPointerCapture(IBinder iBinder, boolean z) {
        Objects.requireNonNull(iBinder, "event must not be null");
        this.mNative.requestPointerCapture(iBinder, z);
    }

    public void setInputDispatchMode(boolean z, boolean z2) {
        this.mNative.setInputDispatchMode(z, z2);
    }

    public void setSystemUiLightsOut(boolean z) {
        this.mNative.setSystemUiLightsOut(z);
    }

    public boolean transferTouchFocus(InputChannel inputChannel, InputChannel inputChannel2, boolean z) {
        return this.mNative.transferTouchFocus(inputChannel.getToken(), inputChannel2.getToken(), z);
    }

    public boolean transferTouchFocus(IBinder iBinder, IBinder iBinder2) {
        Objects.requireNonNull(iBinder);
        Objects.requireNonNull(iBinder2);
        return this.mNative.transferTouchFocus(iBinder, iBinder2, false);
    }

    public void tryPointerSpeed(int i) {
        if (!checkCallingPermission("android.permission.SET_POINTER_SPEED", "tryPointerSpeed()")) {
            throw new SecurityException("Requires SET_POINTER_SPEED permission");
        }
        if (i < -7 || i > 7) {
            throw new IllegalArgumentException("speed out of range");
        }
        setPointerSpeedUnchecked(i);
    }

    public final void setPointerSpeedUnchecked(int i) {
        this.mNative.setPointerSpeed(Math.min(Math.max(i, -7), 7));
    }

    public final void setPointerAcceleration(final float f, int i) {
        updateAdditionalDisplayInputProperties(i, new Consumer() { // from class: com.android.server.input.InputManagerService$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((InputManagerService.AdditionalDisplayInputProperties) obj).pointerAcceleration = f;
            }
        });
    }

    public final void setPointerIconVisible(final boolean z, int i) {
        updateAdditionalDisplayInputProperties(i, new Consumer() { // from class: com.android.server.input.InputManagerService$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((InputManagerService.AdditionalDisplayInputProperties) obj).pointerIconVisible = z;
            }
        });
    }

    @GuardedBy({"mAdditionalDisplayInputPropertiesLock"})
    public final boolean updatePointerDisplayIdLocked(int i) {
        if (this.mRequestedPointerDisplayId == i) {
            return false;
        }
        this.mRequestedPointerDisplayId = i;
        this.mNative.setPointerDisplayId(i);
        applyAdditionalDisplayInputProperties();
        return true;
    }

    public final void handlePointerDisplayIdChanged(PointerDisplayIdChangedArgs pointerDisplayIdChangedArgs) {
        synchronized (this.mAdditionalDisplayInputPropertiesLock) {
            this.mAcknowledgedPointerDisplayId = pointerDisplayIdChangedArgs.mPointerDisplayId;
            this.mAdditionalDisplayInputPropertiesLock.notifyAll();
        }
        this.mWindowManagerCallbacks.notifyPointerDisplayIdChanged(pointerDisplayIdChangedArgs.mPointerDisplayId, pointerDisplayIdChangedArgs.mXPosition, pointerDisplayIdChangedArgs.mYPosition);
    }

    public final boolean setVirtualMousePointerDisplayIdBlocking(int i) {
        boolean z = false;
        boolean z2 = i == -1;
        int pointerDisplayId = z2 ? this.mWindowManagerCallbacks.getPointerDisplayId() : i;
        synchronized (this.mAdditionalDisplayInputPropertiesLock) {
            this.mOverriddenPointerDisplayId = i;
            if (updatePointerDisplayIdLocked(pointerDisplayId) || this.mAcknowledgedPointerDisplayId != pointerDisplayId) {
                if (z2 && this.mAcknowledgedPointerDisplayId == -1) {
                    return true;
                }
                try {
                    this.mAdditionalDisplayInputPropertiesLock.wait(5000L);
                } catch (InterruptedException unused) {
                }
                if (z2 || this.mAcknowledgedPointerDisplayId == i) {
                    z = true;
                }
                return z;
            }
            return true;
        }
    }

    public final int getVirtualMousePointerDisplayId() {
        int i;
        synchronized (this.mAdditionalDisplayInputPropertiesLock) {
            i = this.mOverriddenPointerDisplayId;
        }
        return i;
    }

    public final void setDisplayEligibilityForPointerCapture(int i, boolean z) {
        this.mNative.setDisplayEligibilityForPointerCapture(i, z);
    }

    /* loaded from: classes.dex */
    public static class VibrationInfo {
        public final int[] mAmplitudes;
        public final long[] mPattern;
        public final int mRepeat;

        public long[] getPattern() {
            return this.mPattern;
        }

        public int[] getAmplitudes() {
            return this.mAmplitudes;
        }

        public int getRepeatIndex() {
            return this.mRepeat;
        }

        public VibrationInfo(VibrationEffect vibrationEffect) {
            long[] jArr;
            int i;
            int i2;
            int[] iArr;
            if (vibrationEffect instanceof VibrationEffect.Composed) {
                VibrationEffect.Composed composed = (VibrationEffect.Composed) vibrationEffect;
                int size = composed.getSegments().size();
                jArr = new long[size];
                iArr = new int[size];
                i = composed.getRepeatIndex();
                int i3 = 0;
                i2 = 0;
                while (true) {
                    if (i3 >= size) {
                        break;
                    }
                    StepSegment stepSegment = (VibrationEffectSegment) composed.getSegments().get(i3);
                    i = composed.getRepeatIndex() == i3 ? i2 : i;
                    if (!(stepSegment instanceof StepSegment)) {
                        Slog.w("InputManager", "Input devices don't support segment " + stepSegment);
                        i2 = -1;
                        break;
                    }
                    float amplitude = stepSegment.getAmplitude();
                    if (Float.compare(amplitude, -1.0f) == 0) {
                        iArr[i2] = 192;
                    } else {
                        iArr[i2] = (int) (amplitude * 255.0f);
                    }
                    jArr[i2] = stepSegment.getDuration();
                    i3++;
                    i2++;
                }
            } else {
                jArr = null;
                i = -1;
                i2 = -1;
                iArr = null;
            }
            if (i2 < 0) {
                Slog.w("InputManager", "Only oneshot and step waveforms are supported on input devices");
                this.mPattern = new long[0];
                this.mAmplitudes = new int[0];
                this.mRepeat = -1;
                return;
            }
            this.mRepeat = i;
            long[] jArr2 = new long[i2];
            this.mPattern = jArr2;
            int[] iArr2 = new int[i2];
            this.mAmplitudes = iArr2;
            System.arraycopy(jArr, 0, jArr2, 0, i2);
            System.arraycopy(iArr, 0, iArr2, 0, i2);
            if (i < jArr2.length) {
                return;
            }
            throw new ArrayIndexOutOfBoundsException("Repeat index " + i + " must be within the bounds of the pattern.length " + jArr2.length);
        }
    }

    public final VibratorToken getVibratorToken(int i, IBinder iBinder) {
        VibratorToken vibratorToken;
        synchronized (this.mVibratorLock) {
            vibratorToken = this.mVibratorTokens.get(iBinder);
            if (vibratorToken == null) {
                int i2 = this.mNextVibratorTokenValue;
                this.mNextVibratorTokenValue = i2 + 1;
                vibratorToken = new VibratorToken(i, iBinder, i2);
                try {
                    iBinder.linkToDeath(vibratorToken, 0);
                    this.mVibratorTokens.put(iBinder, vibratorToken);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return vibratorToken;
    }

    public void vibrate(int i, VibrationEffect vibrationEffect, IBinder iBinder) {
        VibrationInfo vibrationInfo = new VibrationInfo(vibrationEffect);
        VibratorToken vibratorToken = getVibratorToken(i, iBinder);
        synchronized (vibratorToken) {
            vibratorToken.mVibrating = true;
            this.mNative.vibrate(i, vibrationInfo.getPattern(), vibrationInfo.getAmplitudes(), vibrationInfo.getRepeatIndex(), vibratorToken.mTokenValue);
        }
    }

    public int[] getVibratorIds(int i) {
        return this.mNative.getVibratorIds(i);
    }

    public boolean isVibrating(int i) {
        return this.mNative.isVibrating(i);
    }

    public void vibrateCombined(int i, CombinedVibration combinedVibration, IBinder iBinder) {
        VibratorToken vibratorToken = getVibratorToken(i, iBinder);
        synchronized (vibratorToken) {
            if (!(combinedVibration instanceof CombinedVibration.Mono) && !(combinedVibration instanceof CombinedVibration.Stereo)) {
                Slog.e("InputManager", "Only Mono and Stereo effects are supported");
                return;
            }
            vibratorToken.mVibrating = true;
            if (combinedVibration instanceof CombinedVibration.Mono) {
                VibrationInfo vibrationInfo = new VibrationInfo(((CombinedVibration.Mono) combinedVibration).getEffect());
                this.mNative.vibrate(i, vibrationInfo.getPattern(), vibrationInfo.getAmplitudes(), vibrationInfo.getRepeatIndex(), vibratorToken.mTokenValue);
            } else if (combinedVibration instanceof CombinedVibration.Stereo) {
                SparseArray effects = ((CombinedVibration.Stereo) combinedVibration).getEffects();
                SparseArray<int[]> sparseArray = new SparseArray<>(effects.size());
                long[] jArr = new long[0];
                int i2 = Integer.MIN_VALUE;
                for (int i3 = 0; i3 < effects.size(); i3++) {
                    VibrationInfo vibrationInfo2 = new VibrationInfo((VibrationEffect) effects.valueAt(i3));
                    if (jArr.length == 0) {
                        jArr = vibrationInfo2.getPattern();
                    }
                    if (i2 == Integer.MIN_VALUE) {
                        i2 = vibrationInfo2.getRepeatIndex();
                    }
                    sparseArray.put(effects.keyAt(i3), vibrationInfo2.getAmplitudes());
                }
                this.mNative.vibrateCombined(i, jArr, sparseArray, i2, vibratorToken.mTokenValue);
            }
        }
    }

    public void cancelVibrate(int i, IBinder iBinder) {
        synchronized (this.mVibratorLock) {
            VibratorToken vibratorToken = this.mVibratorTokens.get(iBinder);
            if (vibratorToken != null && vibratorToken.mDeviceId == i) {
                cancelVibrateIfNeeded(vibratorToken);
            }
        }
    }

    public void onVibratorTokenDied(VibratorToken vibratorToken) {
        synchronized (this.mVibratorLock) {
            this.mVibratorTokens.remove(vibratorToken.mToken);
        }
        cancelVibrateIfNeeded(vibratorToken);
    }

    public final void cancelVibrateIfNeeded(VibratorToken vibratorToken) {
        synchronized (vibratorToken) {
            if (vibratorToken.mVibrating) {
                this.mNative.cancelVibrate(vibratorToken.mDeviceId, vibratorToken.mTokenValue);
                vibratorToken.mVibrating = false;
            }
        }
    }

    public final void notifyVibratorState(int i, boolean z) {
        if (DEBUG) {
            Slog.d("InputManager", "notifyVibratorState: deviceId=" + i + " isOn=" + z);
        }
        synchronized (this.mVibratorLock) {
            this.mIsVibrating.put(i, z);
            notifyVibratorStateListenersLocked(i);
        }
    }

    @GuardedBy({"mVibratorLock"})
    public final void notifyVibratorStateListenersLocked(int i) {
        if (!this.mVibratorStateListeners.contains(i)) {
            if (DEBUG) {
                Slog.v("InputManager", "Device " + i + " doesn't have vibrator state listener.");
                return;
            }
            return;
        }
        RemoteCallbackList<IVibratorStateListener> remoteCallbackList = this.mVibratorStateListeners.get(i);
        int beginBroadcast = remoteCallbackList.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                notifyVibratorStateListenerLocked(i, remoteCallbackList.getBroadcastItem(i2));
            } finally {
                remoteCallbackList.finishBroadcast();
            }
        }
    }

    @GuardedBy({"mVibratorLock"})
    public final void notifyVibratorStateListenerLocked(int i, IVibratorStateListener iVibratorStateListener) {
        try {
            iVibratorStateListener.onVibrating(this.mIsVibrating.get(i));
        } catch (RemoteException | RuntimeException e) {
            Slog.e("InputManager", "Vibrator state listener failed to call", e);
        }
    }

    public boolean registerVibratorStateListener(int i, IVibratorStateListener iVibratorStateListener) {
        RemoteCallbackList<IVibratorStateListener> remoteCallbackList;
        Objects.requireNonNull(iVibratorStateListener, "listener must not be null");
        synchronized (this.mVibratorLock) {
            if (!this.mVibratorStateListeners.contains(i)) {
                remoteCallbackList = new RemoteCallbackList<>();
                this.mVibratorStateListeners.put(i, remoteCallbackList);
            } else {
                remoteCallbackList = this.mVibratorStateListeners.get(i);
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            if (!remoteCallbackList.register(iVibratorStateListener)) {
                Slog.e("InputManager", "Could not register vibrator state listener " + iVibratorStateListener);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
            notifyVibratorStateListenerLocked(i, iVibratorStateListener);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return true;
        }
    }

    public boolean unregisterVibratorStateListener(int i, IVibratorStateListener iVibratorStateListener) {
        synchronized (this.mVibratorLock) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            if (!this.mVibratorStateListeners.contains(i)) {
                Slog.w("InputManager", "Vibrator state listener " + i + " doesn't exist");
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
            boolean unregister = this.mVibratorStateListeners.get(i).unregister(iVibratorStateListener);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return unregister;
        }
    }

    public IInputDeviceBatteryState getBatteryState(int i) {
        return this.mBatteryController.getBatteryState(i);
    }

    public void setPointerIconType(int i) {
        if (i == -1) {
            throw new IllegalArgumentException("Use setCustomPointerIcon to set custom pointers");
        }
        synchronized (this.mAdditionalDisplayInputPropertiesLock) {
            this.mPointerIcon = null;
            this.mPointerIconType = i;
            if (this.mCurrentDisplayProperties.pointerIconVisible) {
                this.mNative.setPointerIconType(i);
            }
        }
    }

    public void setCustomPointerIcon(PointerIcon pointerIcon) {
        Objects.requireNonNull(pointerIcon);
        synchronized (this.mAdditionalDisplayInputPropertiesLock) {
            this.mPointerIconType = -1;
            this.mPointerIcon = pointerIcon;
            if (this.mCurrentDisplayProperties.pointerIconVisible) {
                this.mNative.setCustomPointerIcon(pointerIcon);
            }
        }
    }

    public void addPortAssociation(String str, int i) {
        if (!checkCallingPermission("android.permission.ASSOCIATE_INPUT_DEVICE_TO_DISPLAY", "addPortAssociation()")) {
            throw new SecurityException("Requires ASSOCIATE_INPUT_DEVICE_TO_DISPLAY permission");
        }
        Objects.requireNonNull(str);
        synchronized (this.mAssociationsLock) {
            this.mRuntimeAssociations.put(str, Integer.valueOf(i));
        }
        this.mNative.notifyPortAssociationsChanged();
    }

    public void removePortAssociation(String str) {
        if (!checkCallingPermission("android.permission.ASSOCIATE_INPUT_DEVICE_TO_DISPLAY", "removePortAssociation()")) {
            throw new SecurityException("Requires ASSOCIATE_INPUT_DEVICE_TO_DISPLAY permission");
        }
        Objects.requireNonNull(str);
        synchronized (this.mAssociationsLock) {
            this.mRuntimeAssociations.remove(str);
        }
        this.mNative.notifyPortAssociationsChanged();
    }

    public void addUniqueIdAssociation(String str, String str2) {
        if (!checkCallingPermission("android.permission.ASSOCIATE_INPUT_DEVICE_TO_DISPLAY", "addUniqueIdAssociation()")) {
            throw new SecurityException("Requires ASSOCIATE_INPUT_DEVICE_TO_DISPLAY permission");
        }
        Objects.requireNonNull(str);
        Objects.requireNonNull(str2);
        synchronized (this.mAssociationsLock) {
            this.mUniqueIdAssociations.put(str, str2);
        }
        this.mNative.changeUniqueIdAssociation();
    }

    public void removeUniqueIdAssociation(String str) {
        if (!checkCallingPermission("android.permission.ASSOCIATE_INPUT_DEVICE_TO_DISPLAY", "removeUniqueIdAssociation()")) {
            throw new SecurityException("Requires ASSOCIATE_INPUT_DEVICE_TO_DISPLAY permission");
        }
        Objects.requireNonNull(str);
        synchronized (this.mAssociationsLock) {
            this.mUniqueIdAssociations.remove(str);
        }
        this.mNative.changeUniqueIdAssociation();
    }

    public void setTypeAssociationInternal(String str, String str2) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(str2);
        synchronized (this.mAssociationsLock) {
            this.mDeviceTypeAssociations.put(str, str2);
        }
        this.mNative.changeTypeAssociation();
    }

    public void unsetTypeAssociationInternal(String str) {
        Objects.requireNonNull(str);
        synchronized (this.mAssociationsLock) {
            this.mDeviceTypeAssociations.remove(str);
        }
        this.mNative.changeTypeAssociation();
    }

    public final void addKeyboardLayoutAssociation(String str, String str2, String str3) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(str2);
        Objects.requireNonNull(str3);
        synchronized (this.mAssociationsLock) {
            this.mKeyboardLayoutAssociations.put(str, TextUtils.formatSimple("%s,%s", new Object[]{str2, str3}));
        }
        this.mNative.changeKeyboardLayoutAssociation();
    }

    public final void removeKeyboardLayoutAssociation(String str) {
        Objects.requireNonNull(str);
        synchronized (this.mAssociationsLock) {
            this.mKeyboardLayoutAssociations.remove(str);
        }
        this.mNative.changeKeyboardLayoutAssociation();
    }

    public InputSensorInfo[] getSensorList(int i) {
        return this.mNative.getSensorList(i);
    }

    public boolean registerSensorListener(IInputSensorEventListener iInputSensorEventListener) {
        if (DEBUG) {
            Slog.d("InputManager", "registerSensorListener: listener=" + iInputSensorEventListener + " callingPid=" + Binder.getCallingPid());
        }
        Objects.requireNonNull(iInputSensorEventListener, "listener must not be null");
        synchronized (this.mSensorEventLock) {
            int callingPid = Binder.getCallingPid();
            if (this.mSensorEventListeners.get(callingPid) != null) {
                Slog.e("InputManager", "The calling process " + callingPid + " has already registered an InputSensorEventListener.");
                return false;
            }
            SensorEventListenerRecord sensorEventListenerRecord = new SensorEventListenerRecord(callingPid, iInputSensorEventListener);
            try {
                iInputSensorEventListener.asBinder().linkToDeath(sensorEventListenerRecord, 0);
                this.mSensorEventListeners.put(callingPid, sensorEventListenerRecord);
                return true;
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void unregisterSensorListener(IInputSensorEventListener iInputSensorEventListener) {
        if (DEBUG) {
            Slog.d("InputManager", "unregisterSensorListener: listener=" + iInputSensorEventListener + " callingPid=" + Binder.getCallingPid());
        }
        Objects.requireNonNull(iInputSensorEventListener, "listener must not be null");
        synchronized (this.mSensorEventLock) {
            int callingPid = Binder.getCallingPid();
            if (this.mSensorEventListeners.get(callingPid) != null) {
                if (this.mSensorEventListeners.get(callingPid).getListener().asBinder() != iInputSensorEventListener.asBinder()) {
                    throw new IllegalArgumentException("listener is not registered");
                }
                this.mSensorEventListeners.remove(callingPid);
            }
        }
    }

    public boolean flushSensor(int i, int i2) {
        synchronized (this.mSensorEventLock) {
            if (this.mSensorEventListeners.get(Binder.getCallingPid()) != null) {
                return this.mNative.flushSensor(i, i2);
            }
            return false;
        }
    }

    public boolean enableSensor(int i, int i2, int i3, int i4) {
        boolean enableSensor;
        synchronized (this.mInputDevicesLock) {
            enableSensor = this.mNative.enableSensor(i, i2, i3, i4);
        }
        return enableSensor;
    }

    public void disableSensor(int i, int i2) {
        synchronized (this.mInputDevicesLock) {
            this.mNative.disableSensor(i, i2);
        }
    }

    /* loaded from: classes.dex */
    public final class LightSession implements IBinder.DeathRecipient {
        public final int mDeviceId;
        public int[] mLightIds;
        public LightState[] mLightStates;
        public final String mOpPkg;
        public final IBinder mToken;

        public LightSession(int i, String str, IBinder iBinder) {
            this.mDeviceId = i;
            this.mOpPkg = str;
            this.mToken = iBinder;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            if (InputManagerService.DEBUG) {
                Slog.d("InputManager", "Light token died.");
            }
            synchronized (InputManagerService.this.mLightLock) {
                InputManagerService.this.closeLightSession(this.mDeviceId, this.mToken);
                InputManagerService.this.mLightSessions.remove(this.mToken);
            }
        }
    }

    public List<Light> getLights(int i) {
        return this.mNative.getLights(i);
    }

    public final void setLightStateInternal(int i, Light light, LightState lightState) {
        Objects.requireNonNull(light, "light does not exist");
        if (DEBUG) {
            Slog.d("InputManager", "setLightStateInternal device " + i + " light " + light + "lightState " + lightState);
        }
        if (light.getType() == 10002) {
            this.mNative.setLightPlayerId(i, light.getId(), lightState.getPlayerId());
        } else {
            this.mNative.setLightColor(i, light.getId(), lightState.getColor());
        }
    }

    public final void setLightStatesInternal(int i, int[] iArr, LightState[] lightStateArr) {
        List<Light> lights = this.mNative.getLights(i);
        SparseArray sparseArray = new SparseArray();
        for (int i2 = 0; i2 < lights.size(); i2++) {
            sparseArray.put(lights.get(i2).getId(), lights.get(i2));
        }
        for (int i3 = 0; i3 < iArr.length; i3++) {
            if (sparseArray.contains(iArr[i3])) {
                setLightStateInternal(i, (Light) sparseArray.get(iArr[i3]), lightStateArr[i3]);
            }
        }
    }

    public void setLightStates(int i, int[] iArr, LightState[] lightStateArr, IBinder iBinder) {
        boolean z = true;
        Preconditions.checkArgument(iArr.length == lightStateArr.length, "lights and light states are not same length");
        synchronized (this.mLightLock) {
            LightSession lightSession = this.mLightSessions.get(iBinder);
            Preconditions.checkArgument(lightSession != null, "not registered");
            if (lightSession.mDeviceId != i) {
                z = false;
            }
            Preconditions.checkState(z, "Incorrect device ID");
            lightSession.mLightIds = (int[]) iArr.clone();
            lightSession.mLightStates = (LightState[]) lightStateArr.clone();
            if (DEBUG) {
                Slog.d("InputManager", "setLightStates for " + lightSession.mOpPkg + " device " + i);
            }
        }
        setLightStatesInternal(i, iArr, lightStateArr);
    }

    public LightState getLightState(int i, int i2) {
        LightState lightState;
        synchronized (this.mLightLock) {
            lightState = new LightState(this.mNative.getLightColor(i, i2), this.mNative.getLightPlayerId(i, i2));
        }
        return lightState;
    }

    public void openLightSession(int i, String str, IBinder iBinder) {
        Objects.requireNonNull(iBinder);
        synchronized (this.mLightLock) {
            Preconditions.checkState(this.mLightSessions.get(iBinder) == null, "already registered");
            LightSession lightSession = new LightSession(i, str, iBinder);
            try {
                iBinder.linkToDeath(lightSession, 0);
            } catch (RemoteException e) {
                e.rethrowAsRuntimeException();
            }
            this.mLightSessions.put(iBinder, lightSession);
            if (DEBUG) {
                Slog.d("InputManager", "Open light session for " + str + " device " + i);
            }
        }
    }

    public void closeLightSession(int i, IBinder iBinder) {
        Objects.requireNonNull(iBinder);
        synchronized (this.mLightLock) {
            LightSession lightSession = this.mLightSessions.get(iBinder);
            Preconditions.checkState(lightSession != null, "not registered");
            Arrays.fill(lightSession.mLightStates, new LightState(0));
            setLightStatesInternal(i, lightSession.mLightIds, lightSession.mLightStates);
            this.mLightSessions.remove(iBinder);
            if (!this.mLightSessions.isEmpty()) {
                LightSession valueAt = this.mLightSessions.valueAt(0);
                setLightStatesInternal(i, valueAt.mLightIds, valueAt.mLightStates);
            }
        }
    }

    public void cancelCurrentTouch() {
        if (!checkCallingPermission("android.permission.MONITOR_INPUT", "cancelCurrentTouch()")) {
            throw new SecurityException("Requires MONITOR_INPUT permission");
        }
        this.mNative.cancelCurrentTouch();
    }

    public void registerBatteryListener(int i, IInputDeviceBatteryListener iInputDeviceBatteryListener) {
        Objects.requireNonNull(iInputDeviceBatteryListener);
        this.mBatteryController.registerBatteryListener(i, iInputDeviceBatteryListener, Binder.getCallingPid());
    }

    public void unregisterBatteryListener(int i, IInputDeviceBatteryListener iInputDeviceBatteryListener) {
        Objects.requireNonNull(iInputDeviceBatteryListener);
        this.mBatteryController.unregisterBatteryListener(i, iInputDeviceBatteryListener, Binder.getCallingPid());
    }

    @EnforcePermission("android.permission.BLUETOOTH")
    public String getInputDeviceBluetoothAddress(int i) {
        super.getInputDeviceBluetoothAddress_enforcePermission();
        String bluetoothAddress = this.mNative.getBluetoothAddress(i);
        if (bluetoothAddress == null) {
            return null;
        }
        if (BluetoothAdapter.checkBluetoothAddress(bluetoothAddress)) {
            return bluetoothAddress;
        }
        throw new IllegalStateException("The Bluetooth address of input device " + i + " should not be invalid: address=" + bluetoothAddress);
    }

    @EnforcePermission("android.permission.MONITOR_INPUT")
    public void pilferPointers(IBinder iBinder) {
        super.pilferPointers_enforcePermission();
        Objects.requireNonNull(iBinder);
        this.mNative.pilferPointers(iBinder);
    }

    @EnforcePermission("android.permission.MONITOR_KEYBOARD_BACKLIGHT")
    public void registerKeyboardBacklightListener(IKeyboardBacklightListener iKeyboardBacklightListener) {
        super.registerKeyboardBacklightListener_enforcePermission();
        Objects.requireNonNull(iKeyboardBacklightListener);
        this.mKeyboardBacklightController.registerKeyboardBacklightListener(iKeyboardBacklightListener, Binder.getCallingPid());
    }

    @EnforcePermission("android.permission.MONITOR_KEYBOARD_BACKLIGHT")
    public void unregisterKeyboardBacklightListener(IKeyboardBacklightListener iKeyboardBacklightListener) {
        super.unregisterKeyboardBacklightListener_enforcePermission();
        Objects.requireNonNull(iKeyboardBacklightListener);
        this.mKeyboardBacklightController.unregisterKeyboardBacklightListener(iKeyboardBacklightListener, Binder.getCallingPid());
    }

    public HostUsiVersion getHostUsiVersionFromDisplayConfig(int i) {
        return this.mDisplayManagerInternal.getHostUsiVersion(i);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "InputManager", printWriter)) {
            PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            indentingPrintWriter.println("INPUT MANAGER (dumpsys input)\n");
            String dump = this.mNative.dump();
            if (dump != null) {
                printWriter.println(dump);
            }
            indentingPrintWriter.println("Input Manager Service (Java) State:");
            indentingPrintWriter.increaseIndent();
            dumpAssociations(indentingPrintWriter);
            dumpSpyWindowGestureMonitors(indentingPrintWriter);
            dumpDisplayInputPropertiesValues(indentingPrintWriter);
            this.mBatteryController.dump(indentingPrintWriter);
            this.mKeyboardBacklightController.dump(indentingPrintWriter);
        }
    }

    public final void dumpAssociations(final IndentingPrintWriter indentingPrintWriter) {
        if (!this.mStaticAssociations.isEmpty()) {
            indentingPrintWriter.println("Static Associations:");
            this.mStaticAssociations.forEach(new BiConsumer() { // from class: com.android.server.input.InputManagerService$$ExternalSyntheticLambda3
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    InputManagerService.lambda$dumpAssociations$3(indentingPrintWriter, (String) obj, (Integer) obj2);
                }
            });
        }
        synchronized (this.mAssociationsLock) {
            if (!this.mRuntimeAssociations.isEmpty()) {
                indentingPrintWriter.println("Runtime Associations:");
                this.mRuntimeAssociations.forEach(new BiConsumer() { // from class: com.android.server.input.InputManagerService$$ExternalSyntheticLambda4
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        InputManagerService.lambda$dumpAssociations$4(indentingPrintWriter, (String) obj, (Integer) obj2);
                    }
                });
            }
            if (!this.mUniqueIdAssociations.isEmpty()) {
                indentingPrintWriter.println("Unique Id Associations:");
                this.mUniqueIdAssociations.forEach(new BiConsumer() { // from class: com.android.server.input.InputManagerService$$ExternalSyntheticLambda5
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        InputManagerService.lambda$dumpAssociations$5(indentingPrintWriter, (String) obj, (String) obj2);
                    }
                });
            }
            if (!this.mDeviceTypeAssociations.isEmpty()) {
                indentingPrintWriter.println("Type Associations:");
                this.mDeviceTypeAssociations.forEach(new BiConsumer() { // from class: com.android.server.input.InputManagerService$$ExternalSyntheticLambda6
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        InputManagerService.lambda$dumpAssociations$6(indentingPrintWriter, (String) obj, (String) obj2);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$dumpAssociations$3(IndentingPrintWriter indentingPrintWriter, String str, Integer num) {
        indentingPrintWriter.print("  port: " + str);
        indentingPrintWriter.println("  display: " + num);
    }

    public static /* synthetic */ void lambda$dumpAssociations$4(IndentingPrintWriter indentingPrintWriter, String str, Integer num) {
        indentingPrintWriter.print("  port: " + str);
        indentingPrintWriter.println("  display: " + num);
    }

    public static /* synthetic */ void lambda$dumpAssociations$5(IndentingPrintWriter indentingPrintWriter, String str, String str2) {
        indentingPrintWriter.print("  port: " + str);
        indentingPrintWriter.println("  uniqueId: " + str2);
    }

    public static /* synthetic */ void lambda$dumpAssociations$6(IndentingPrintWriter indentingPrintWriter, String str, String str2) {
        indentingPrintWriter.print("  port: " + str);
        indentingPrintWriter.println("  type: " + str2);
    }

    public final void dumpSpyWindowGestureMonitors(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mInputMonitors) {
            if (this.mInputMonitors.isEmpty()) {
                return;
            }
            indentingPrintWriter.println("Gesture Monitors (implemented as spy windows):");
            int i = 0;
            for (GestureMonitorSpyWindow gestureMonitorSpyWindow : this.mInputMonitors.values()) {
                indentingPrintWriter.append("  " + i + ": ").println(gestureMonitorSpyWindow.dump());
                i++;
            }
        }
    }

    public final void dumpDisplayInputPropertiesValues(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mAdditionalDisplayInputPropertiesLock) {
            if (this.mAdditionalDisplayInputProperties.size() != 0) {
                indentingPrintWriter.println("mAdditionalDisplayInputProperties:");
                indentingPrintWriter.increaseIndent();
                for (int i = 0; i < this.mAdditionalDisplayInputProperties.size(); i++) {
                    indentingPrintWriter.println("displayId: " + this.mAdditionalDisplayInputProperties.keyAt(i));
                    AdditionalDisplayInputProperties valueAt = this.mAdditionalDisplayInputProperties.valueAt(i);
                    indentingPrintWriter.println("pointerAcceleration: " + valueAt.pointerAcceleration);
                    indentingPrintWriter.println("pointerIconVisible: " + valueAt.pointerIconVisible);
                }
                indentingPrintWriter.decreaseIndent();
            }
            if (this.mOverriddenPointerDisplayId != -1) {
                indentingPrintWriter.println("mOverriddenPointerDisplayId: " + this.mOverriddenPointerDisplayId);
            }
            indentingPrintWriter.println("mAcknowledgedPointerDisplayId=" + this.mAcknowledgedPointerDisplayId);
            indentingPrintWriter.println("mRequestedPointerDisplayId=" + this.mRequestedPointerDisplayId);
            indentingPrintWriter.println("mPointerIconType=" + PointerIcon.typeToString(this.mPointerIconType));
            indentingPrintWriter.println("mPointerIcon=" + this.mPointerIcon);
        }
    }

    public final boolean checkCallingPermission(String str, String str2) {
        return checkCallingPermission(str, str2, false);
    }

    public final boolean checkCallingPermission(String str, String str2, boolean z) {
        if (Binder.getCallingPid() == Process.myPid() || this.mContext.checkCallingPermission(str) == 0) {
            return true;
        }
        if (z) {
            ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
            Objects.requireNonNull(activityManagerInternal, "ActivityManagerInternal should not be null.");
            int instrumentationSourceUid = activityManagerInternal.getInstrumentationSourceUid(Binder.getCallingUid());
            if (instrumentationSourceUid != -1) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (this.mContext.checkPermission(str, -1, instrumentationSourceUid) == 0) {
                        return true;
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
        Slog.w("InputManager", "Permission Denial: " + str2 + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + str);
        return false;
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        synchronized (this.mInputFilterLock) {
        }
        synchronized (this.mAssociationsLock) {
        }
        synchronized (this.mLidSwitchLock) {
        }
        synchronized (this.mInputMonitors) {
        }
        synchronized (this.mAdditionalDisplayInputPropertiesLock) {
        }
        this.mBatteryController.monitor();
        this.mNative.monitor();
    }

    public final void notifyConfigurationChanged(long j) {
        this.mWindowManagerCallbacks.notifyConfigurationChanged();
    }

    public final void notifyInputDevicesChanged(InputDevice[] inputDeviceArr) {
        synchronized (this.mInputDevicesLock) {
            if (!this.mInputDevicesChangedPending) {
                this.mInputDevicesChangedPending = true;
                this.mHandler.obtainMessage(1, this.mInputDevices).sendToTarget();
            }
            this.mInputDevices = inputDeviceArr;
        }
    }

    public final void notifySwitch(long j, int i, int i2) {
        if (DEBUG) {
            Slog.d("InputManager", "notifySwitch: values=" + Integer.toHexString(i) + ", mask=" + Integer.toHexString(i2));
        }
        if ((i2 & 1) != 0) {
            boolean z = (i & 1) == 0;
            synchronized (this.mLidSwitchLock) {
                if (this.mSystemReady) {
                    for (int i3 = 0; i3 < this.mLidSwitchCallbacks.size(); i3++) {
                        this.mLidSwitchCallbacks.get(i3).notifyLidSwitchChanged(j, z);
                    }
                }
            }
        }
        if ((i2 & 512) != 0) {
            boolean z2 = (i & 512) != 0;
            this.mWindowManagerCallbacks.notifyCameraLensCoverSwitchChanged(j, z2);
            setSensorPrivacy(2, z2);
        }
        if (this.mUseDevInputEventForAudioJack && (i2 & FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__ROLE_HOLDER_UPDATER_UPDATE_FAILED) != 0) {
            this.mWiredAccessoryCallbacks.notifyWiredAccessoryChanged(j, i, i2);
        }
        if ((i2 & 2) != 0) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argi1 = (int) ((-1) & j);
            obtain.argi2 = (int) (j >> 32);
            obtain.arg1 = Boolean.valueOf((i & 2) != 0);
            this.mHandler.obtainMessage(3, obtain).sendToTarget();
        }
        if ((i2 & 16384) != 0) {
            boolean z3 = (i & 16384) != 0;
            ((AudioManager) this.mContext.getSystemService(AudioManager.class)).setMicrophoneMuteFromSwitch(z3);
            setSensorPrivacy(1, z3);
        }
    }

    public final void setSensorPrivacy(int i, boolean z) {
        ((SensorPrivacyManagerInternal) LocalServices.getService(SensorPrivacyManagerInternal.class)).setPhysicalToggleSensorPrivacy(-2, i, z);
    }

    public final void notifyInputChannelBroken(IBinder iBinder) {
        synchronized (this.mInputMonitors) {
            if (this.mInputMonitors.containsKey(iBinder)) {
                removeSpyWindowGestureMonitor(iBinder);
            }
        }
        this.mWindowManagerCallbacks.notifyInputChannelBroken(iBinder);
    }

    public final void notifyFocusChanged(IBinder iBinder, IBinder iBinder2) {
        this.mWindowManagerCallbacks.notifyFocusChanged(iBinder, iBinder2);
    }

    public final void notifyDropWindow(IBinder iBinder, float f, float f2) {
        this.mWindowManagerCallbacks.notifyDropWindow(iBinder, f, f2);
    }

    public final void notifyNoFocusedWindowAnr(InputApplicationHandle inputApplicationHandle) {
        this.mWindowManagerCallbacks.notifyNoFocusedWindowAnr(inputApplicationHandle);
    }

    public final void notifyWindowUnresponsive(IBinder iBinder, int i, boolean z, String str) {
        this.mWindowManagerCallbacks.notifyWindowUnresponsive(iBinder, z ? OptionalInt.of(i) : OptionalInt.empty(), str);
    }

    public final void notifyWindowResponsive(IBinder iBinder, int i, boolean z) {
        this.mWindowManagerCallbacks.notifyWindowResponsive(iBinder, z ? OptionalInt.of(i) : OptionalInt.empty());
    }

    public final void notifySensorEvent(int i, int i2, int i3, long j, float[] fArr) {
        int size;
        if (DEBUG) {
            Slog.d("InputManager", "notifySensorEvent: deviceId=" + i + " sensorType=" + i2 + " values=" + Arrays.toString(fArr));
        }
        this.mSensorEventListenersToNotify.clear();
        synchronized (this.mSensorEventLock) {
            size = this.mSensorEventListeners.size();
            for (int i4 = 0; i4 < size; i4++) {
                this.mSensorEventListenersToNotify.add(this.mSensorEventListeners.valueAt(i4));
            }
        }
        for (int i5 = 0; i5 < size; i5++) {
            this.mSensorEventListenersToNotify.get(i5).notifySensorEvent(i, i2, i3, j, fArr);
        }
        this.mSensorEventListenersToNotify.clear();
    }

    public final void notifySensorAccuracy(int i, int i2, int i3) {
        int size;
        int i4;
        this.mSensorAccuracyListenersToNotify.clear();
        synchronized (this.mSensorEventLock) {
            size = this.mSensorEventListeners.size();
            for (int i5 = 0; i5 < size; i5++) {
                this.mSensorAccuracyListenersToNotify.add(this.mSensorEventListeners.valueAt(i5));
            }
        }
        for (i4 = 0; i4 < size; i4++) {
            this.mSensorAccuracyListenersToNotify.get(i4).notifySensorAccuracy(i, i2, i3);
        }
        this.mSensorAccuracyListenersToNotify.clear();
    }

    public final boolean filterInputEvent(InputEvent inputEvent, int i) {
        synchronized (this.mInputFilterLock) {
            IInputFilter iInputFilter = this.mInputFilter;
            if (iInputFilter != null) {
                try {
                    iInputFilter.filterInputEvent(inputEvent, i);
                } catch (RemoteException unused) {
                }
                return false;
            }
            inputEvent.recycle();
            return true;
        }
    }

    public final int interceptKeyBeforeQueueing(KeyEvent keyEvent, int i) {
        return this.mWindowManagerCallbacks.interceptKeyBeforeQueueing(keyEvent, i);
    }

    public final int interceptMotionBeforeQueueingNonInteractive(int i, long j, int i2) {
        return this.mWindowManagerCallbacks.interceptMotionBeforeQueueingNonInteractive(i, j, i2);
    }

    public final long interceptKeyBeforeDispatching(IBinder iBinder, KeyEvent keyEvent, int i) {
        return this.mWindowManagerCallbacks.interceptKeyBeforeDispatching(iBinder, keyEvent, i);
    }

    public final KeyEvent dispatchUnhandledKey(IBinder iBinder, KeyEvent keyEvent, int i) {
        return this.mWindowManagerCallbacks.dispatchUnhandledKey(iBinder, keyEvent, i);
    }

    public final void onPointerDownOutsideFocus(IBinder iBinder) {
        this.mWindowManagerCallbacks.onPointerDownOutsideFocus(iBinder);
    }

    public final int getVirtualKeyQuietTimeMillis() {
        return this.mContext.getResources().getInteger(17694986);
    }

    public static String[] getExcludedDeviceNames() {
        File file;
        ArrayList arrayList = new ArrayList();
        File[] fileArr = {Environment.getRootDirectory(), Environment.getVendorDirectory()};
        for (int i = 0; i < 2; i++) {
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(fileArr[i], "etc/excluded-input-devices.xml"));
                arrayList.addAll(ConfigurationProcessor.processExcludedDeviceNames(fileInputStream));
                fileInputStream.close();
            } catch (FileNotFoundException unused) {
            } catch (Exception e) {
                Slog.e("InputManager", "Could not parse '" + file.getAbsolutePath() + "'", e);
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public final boolean isPerDisplayTouchModeEnabled() {
        return this.mContext.getResources().getBoolean(17891332);
    }

    public final void notifyStylusGestureStarted(int i, long j) {
        this.mBatteryController.notifyStylusGestureStarted(i, j);
    }

    public static <T> String[] flatten(Map<String, T> map) {
        final ArrayList arrayList = new ArrayList(map.size() * 2);
        map.forEach(new BiConsumer() { // from class: com.android.server.input.InputManagerService$$ExternalSyntheticLambda2
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                InputManagerService.lambda$flatten$7(arrayList, (String) obj, obj2);
            }
        });
        return (String[]) arrayList.toArray(new String[0]);
    }

    public static /* synthetic */ void lambda$flatten$7(List list, String str, Object obj) {
        list.add(str);
        list.add(obj.toString());
    }

    public static Map<String, Integer> loadStaticInputPortAssociations() {
        File file = new File(Environment.getVendorDirectory(), "etc/input-port-associations.xml");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                Map<String, Integer> processInputPortAssociations = ConfigurationProcessor.processInputPortAssociations(fileInputStream);
                fileInputStream.close();
                return processInputPortAssociations;
            } catch (Throwable th) {
                try {
                    fileInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (FileNotFoundException unused) {
            return new HashMap();
        } catch (Exception e) {
            Slog.e("InputManager", "Could not parse '" + file.getAbsolutePath() + "'", e);
            return new HashMap();
        }
    }

    public final String[] getInputPortAssociations() {
        HashMap hashMap = new HashMap(this.mStaticAssociations);
        synchronized (this.mAssociationsLock) {
            hashMap.putAll(this.mRuntimeAssociations);
        }
        return flatten(hashMap);
    }

    public final String[] getInputUniqueIdAssociations() {
        HashMap hashMap;
        synchronized (this.mAssociationsLock) {
            hashMap = new HashMap(this.mUniqueIdAssociations);
        }
        return flatten(hashMap);
    }

    @VisibleForTesting
    public String[] getDeviceTypeAssociations() {
        HashMap hashMap;
        synchronized (this.mAssociationsLock) {
            hashMap = new HashMap(this.mDeviceTypeAssociations);
        }
        return flatten(hashMap);
    }

    @VisibleForTesting
    private String[] getKeyboardLayoutAssociations() {
        ArrayMap arrayMap = new ArrayMap();
        synchronized (this.mAssociationsLock) {
            arrayMap.putAll(this.mKeyboardLayoutAssociations);
        }
        return flatten(arrayMap);
    }

    public boolean canDispatchToDisplay(int i, int i2) {
        return this.mNative.canDispatchToDisplay(i, i2);
    }

    public final int getKeyRepeatTimeout() {
        return ViewConfiguration.getKeyRepeatTimeout();
    }

    public final int getKeyRepeatDelay() {
        return ViewConfiguration.getKeyRepeatDelay();
    }

    public final int getHoverTapTimeout() {
        return ViewConfiguration.getHoverTapTimeout();
    }

    public final int getHoverTapSlop() {
        return ViewConfiguration.getHoverTapSlop();
    }

    public final int getDoubleTapTimeout() {
        return ViewConfiguration.getDoubleTapTimeout();
    }

    public final int getLongPressTimeout() {
        return ViewConfiguration.getLongPressTimeout();
    }

    public final int getPointerLayer() {
        return this.mWindowManagerCallbacks.getPointerLayer();
    }

    public final PointerIcon getPointerIcon(int i) {
        return PointerIcon.getDefaultIcon(getContextForPointerIcon(i));
    }

    public final long getParentSurfaceForPointers(int i) {
        SurfaceControl parentSurfaceForPointers = this.mWindowManagerCallbacks.getParentSurfaceForPointers(i);
        if (parentSurfaceForPointers == null) {
            return 0L;
        }
        return parentSurfaceForPointers.mNativeObject;
    }

    public final Context getContextForPointerIcon(int i) {
        Context context = this.mPointerIconDisplayContext;
        if (context != null && context.getDisplay().getDisplayId() == i) {
            return this.mPointerIconDisplayContext;
        }
        Context contextForDisplay = getContextForDisplay(i);
        this.mPointerIconDisplayContext = contextForDisplay;
        if (contextForDisplay == null) {
            this.mPointerIconDisplayContext = getContextForDisplay(0);
        }
        return this.mPointerIconDisplayContext;
    }

    public final Context getContextForDisplay(int i) {
        if (i == -1) {
            return null;
        }
        if (this.mContext.getDisplay().getDisplayId() == i) {
            return this.mContext;
        }
        DisplayManager displayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        Objects.requireNonNull(displayManager);
        Display display = displayManager.getDisplay(i);
        if (display == null) {
            return null;
        }
        return this.mContext.createDisplayContext(display);
    }

    public final String[] getKeyboardLayoutOverlay(InputDeviceIdentifier inputDeviceIdentifier) {
        if (this.mSystemReady) {
            return this.mKeyboardLayoutManager.getKeyboardLayoutOverlay(inputDeviceIdentifier);
        }
        return null;
    }

    @EnforcePermission("android.permission.REMAP_MODIFIER_KEYS")
    public void remapModifierKey(int i, int i2) {
        super.remapModifierKey_enforcePermission();
        this.mKeyRemapper.remapKey(i, i2);
    }

    @EnforcePermission("android.permission.REMAP_MODIFIER_KEYS")
    public void clearAllModifierKeyRemappings() {
        super.clearAllModifierKeyRemappings_enforcePermission();
        this.mKeyRemapper.clearAllKeyRemappings();
    }

    @EnforcePermission("android.permission.REMAP_MODIFIER_KEYS")
    public Map<Integer, Integer> getModifierKeyRemapping() {
        super.getModifierKeyRemapping_enforcePermission();
        return this.mKeyRemapper.getKeyRemapping();
    }

    public final String getDeviceAlias(String str) {
        BluetoothAdapter.checkBluetoothAddress(str);
        return null;
    }

    public final boolean isStylusPointerIconEnabled() {
        InputManager inputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
        Objects.requireNonNull(inputManager);
        return inputManager.isStylusPointerIconEnabled();
    }

    /* loaded from: classes.dex */
    public static class PointerDisplayIdChangedArgs {
        public final int mPointerDisplayId;
        public final float mXPosition;
        public final float mYPosition;

        public PointerDisplayIdChangedArgs(int i, float f, float f2) {
            this.mPointerDisplayId = i;
            this.mXPosition = f;
            this.mYPosition = f2;
        }
    }

    @VisibleForTesting
    public void onPointerDisplayIdChanged(int i, float f, float f2) {
        this.mHandler.obtainMessage(4, new PointerDisplayIdChangedArgs(i, f, f2)).sendToTarget();
    }

    /* loaded from: classes.dex */
    public final class InputManagerHandler extends Handler {
        public InputManagerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                InputManagerService.this.deliverInputDevicesChanged((InputDevice[]) message.obj);
            } else if (i == 2) {
                InputManagerService.this.reloadDeviceAliases();
            } else if (i != 3) {
                if (i != 4) {
                    return;
                }
                InputManagerService.this.handlePointerDisplayIdChanged((PointerDisplayIdChangedArgs) message.obj);
            } else {
                SomeArgs someArgs = (SomeArgs) message.obj;
                boolean booleanValue = ((Boolean) someArgs.arg1).booleanValue();
                InputManagerService.this.deliverTabletModeChanged((someArgs.argi1 & 4294967295L) | (someArgs.argi2 << 32), booleanValue);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class InputFilterHost extends IInputFilterHost.Stub {
        @GuardedBy({"mInputFilterLock"})
        public boolean mDisconnected;

        public InputFilterHost() {
        }

        @GuardedBy({"mInputFilterLock"})
        public void disconnectLocked() {
            this.mDisconnected = true;
        }

        public void sendInputEvent(InputEvent inputEvent, int i) {
            if (!InputManagerService.this.checkCallingPermission("android.permission.INJECT_EVENTS", "sendInputEvent()")) {
                throw new SecurityException("The INJECT_EVENTS permission is required for injecting input events.");
            }
            Objects.requireNonNull(inputEvent, "event must not be null");
            synchronized (InputManagerService.this.mInputFilterLock) {
                if (!this.mDisconnected) {
                    InputManagerService.this.mNative.injectInputEvent(inputEvent, false, -1, 0, 0, i | 67108864);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public final class InputMonitorHost extends IInputMonitorHost.Stub {
        public final IBinder mInputChannelToken;

        public InputMonitorHost(IBinder iBinder) {
            this.mInputChannelToken = iBinder;
        }

        public void pilferPointers() {
            InputManagerService.this.mNative.pilferPointers(this.mInputChannelToken);
        }

        public void dispose() {
            InputManagerService.this.removeSpyWindowGestureMonitor(this.mInputChannelToken);
        }
    }

    /* loaded from: classes.dex */
    public final class InputDevicesChangedListenerRecord implements IBinder.DeathRecipient {
        public final IInputDevicesChangedListener mListener;
        public final int mPid;

        public InputDevicesChangedListenerRecord(int i, IInputDevicesChangedListener iInputDevicesChangedListener) {
            this.mPid = i;
            this.mListener = iInputDevicesChangedListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            if (InputManagerService.DEBUG) {
                Slog.d("InputManager", "Input devices changed listener for pid " + this.mPid + " died.");
            }
            InputManagerService.this.onInputDevicesChangedListenerDied(this.mPid);
        }

        public void notifyInputDevicesChanged(int[] iArr) {
            try {
                this.mListener.onInputDevicesChanged(iArr);
            } catch (RemoteException e) {
                Slog.w("InputManager", "Failed to notify process " + this.mPid + " that input devices changed, assuming it died.", e);
                binderDied();
            }
        }
    }

    /* loaded from: classes.dex */
    public final class TabletModeChangedListenerRecord implements IBinder.DeathRecipient {
        public final ITabletModeChangedListener mListener;
        public final int mPid;

        public TabletModeChangedListenerRecord(int i, ITabletModeChangedListener iTabletModeChangedListener) {
            this.mPid = i;
            this.mListener = iTabletModeChangedListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            if (InputManagerService.DEBUG) {
                Slog.d("InputManager", "Tablet mode changed listener for pid " + this.mPid + " died.");
            }
            InputManagerService.this.onTabletModeChangedListenerDied(this.mPid);
        }

        public void notifyTabletModeChanged(long j, boolean z) {
            try {
                this.mListener.onTabletModeChanged(j, z);
            } catch (RemoteException e) {
                Slog.w("InputManager", "Failed to notify process " + this.mPid + " that tablet mode changed, assuming it died.", e);
                binderDied();
            }
        }
    }

    public final void onSensorEventListenerDied(int i) {
        synchronized (this.mSensorEventLock) {
            this.mSensorEventListeners.remove(i);
        }
    }

    /* loaded from: classes.dex */
    public final class SensorEventListenerRecord implements IBinder.DeathRecipient {
        public final IInputSensorEventListener mListener;
        public final int mPid;

        public SensorEventListenerRecord(int i, IInputSensorEventListener iInputSensorEventListener) {
            this.mPid = i;
            this.mListener = iInputSensorEventListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            if (InputManagerService.DEBUG) {
                Slog.d("InputManager", "Sensor event listener for pid " + this.mPid + " died.");
            }
            InputManagerService.this.onSensorEventListenerDied(this.mPid);
        }

        public IInputSensorEventListener getListener() {
            return this.mListener;
        }

        public void notifySensorEvent(int i, int i2, int i3, long j, float[] fArr) {
            try {
                this.mListener.onInputSensorChanged(i, i2, i3, j, fArr);
            } catch (RemoteException e) {
                Slog.w("InputManager", "Failed to notify process " + this.mPid + " that sensor event notified, assuming it died.", e);
                binderDied();
            }
        }

        public void notifySensorAccuracy(int i, int i2, int i3) {
            try {
                this.mListener.onInputSensorAccuracyChanged(i, i2, i3);
            } catch (RemoteException e) {
                Slog.w("InputManager", "Failed to notify process " + this.mPid + " that sensor accuracy notified, assuming it died.", e);
                binderDied();
            }
        }
    }

    /* loaded from: classes.dex */
    public final class VibratorToken implements IBinder.DeathRecipient {
        public final int mDeviceId;
        public final IBinder mToken;
        public final int mTokenValue;
        public boolean mVibrating;

        public VibratorToken(int i, IBinder iBinder, int i2) {
            this.mDeviceId = i;
            this.mToken = iBinder;
            this.mTokenValue = i2;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            if (InputManagerService.DEBUG) {
                Slog.d("InputManager", "Vibrator token died.");
            }
            InputManagerService.this.onVibratorTokenDied(this);
        }
    }

    /* loaded from: classes.dex */
    public final class LocalService extends InputManagerInternal {
        public LocalService() {
        }

        @Override // com.android.server.input.InputManagerInternal
        public void setDisplayViewports(List<DisplayViewport> list) {
            InputManagerService.this.setDisplayViewportsInternal(list);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void setInteractive(boolean z) {
            InputManagerService.this.mNative.setInteractive(z);
            InputManagerService.this.mBatteryController.onInteractiveChanged(z);
            InputManagerService.this.mKeyboardBacklightController.onInteractiveChanged(z);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void toggleCapsLock(int i) {
            InputManagerService.this.mNative.toggleCapsLock(i);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void setPulseGestureEnabled(boolean z) {
            FileWriter fileWriter;
            if (InputManagerService.this.mDoubleTouchGestureEnableFile != null) {
                FileWriter fileWriter2 = null;
                try {
                    try {
                        fileWriter = new FileWriter(InputManagerService.this.mDoubleTouchGestureEnableFile);
                    } catch (IOException e) {
                        e = e;
                    }
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    fileWriter.write(z ? "1" : "0");
                    IoUtils.closeQuietly(fileWriter);
                } catch (IOException e2) {
                    e = e2;
                    fileWriter2 = fileWriter;
                    Log.wtf("InputManager", "Unable to setPulseGestureEnabled", e);
                    IoUtils.closeQuietly(fileWriter2);
                } catch (Throwable th2) {
                    th = th2;
                    fileWriter2 = fileWriter;
                    IoUtils.closeQuietly(fileWriter2);
                    throw th;
                }
            }
        }

        @Override // com.android.server.input.InputManagerInternal
        public boolean transferTouchFocus(IBinder iBinder, IBinder iBinder2) {
            return InputManagerService.this.transferTouchFocus(iBinder, iBinder2);
        }

        @Override // com.android.server.input.InputManagerInternal
        public boolean setVirtualMousePointerDisplayId(int i) {
            return InputManagerService.this.setVirtualMousePointerDisplayIdBlocking(i);
        }

        @Override // com.android.server.input.InputManagerInternal
        public int getVirtualMousePointerDisplayId() {
            return InputManagerService.this.getVirtualMousePointerDisplayId();
        }

        @Override // com.android.server.input.InputManagerInternal
        public PointF getCursorPosition() {
            return InputManagerService.this.mWindowManagerCallbacks.getCursorPosition();
        }

        @Override // com.android.server.input.InputManagerInternal
        public void setPointerAcceleration(float f, int i) {
            InputManagerService.this.setPointerAcceleration(f, i);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void setDisplayEligibilityForPointerCapture(int i, boolean z) {
            InputManagerService.this.setDisplayEligibilityForPointerCapture(i, z);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void setPointerIconVisible(boolean z, int i) {
            InputManagerService.this.setPointerIconVisible(z, i);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void registerLidSwitchCallback(InputManagerInternal.LidSwitchCallback lidSwitchCallback) {
            InputManagerService.this.registerLidSwitchCallbackInternal(lidSwitchCallback);
        }

        @Override // com.android.server.input.InputManagerInternal
        public InputChannel createInputChannel(String str) {
            return InputManagerService.this.createInputChannel(str);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void onInputMethodSubtypeChangedForKeyboardLayoutMapping(int i, InputMethodSubtypeHandle inputMethodSubtypeHandle, InputMethodSubtype inputMethodSubtype) {
            InputManagerService.this.mKeyboardLayoutManager.onInputMethodSubtypeChanged(i, inputMethodSubtypeHandle, inputMethodSubtype);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void notifyUserActivity() {
            InputManagerService.this.mKeyboardBacklightController.notifyUserActivity();
        }

        @Override // com.android.server.input.InputManagerInternal
        public void incrementKeyboardBacklight(int i) {
            InputManagerService.this.mKeyboardBacklightController.incrementKeyboardBacklight(i);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void decrementKeyboardBacklight(int i) {
            InputManagerService.this.mKeyboardBacklightController.decrementKeyboardBacklight(i);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void setTypeAssociation(String str, String str2) {
            InputManagerService.this.setTypeAssociationInternal(str, str2);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void unsetTypeAssociation(String str) {
            InputManagerService.this.unsetTypeAssociationInternal(str);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void addKeyboardLayoutAssociation(String str, String str2, String str3) {
            InputManagerService.this.addKeyboardLayoutAssociation(str, str2, str3);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void removeKeyboardLayoutAssociation(String str) {
            InputManagerService.this.removeKeyboardLayoutAssociation(str);
        }

        @Override // com.android.server.input.InputManagerInternal
        public void setStylusButtonMotionEventsEnabled(boolean z) {
            InputManagerService.this.mNative.setStylusButtonMotionEventsEnabled(z);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new InputShellCommand().exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    /* loaded from: classes.dex */
    public static class AdditionalDisplayInputProperties {
        public float pointerAcceleration;
        public boolean pointerIconVisible;

        public AdditionalDisplayInputProperties() {
            reset();
        }

        public boolean allDefaults() {
            return Float.compare(this.pointerAcceleration, 3.0f) == 0 && this.pointerIconVisible;
        }

        public void reset() {
            this.pointerAcceleration = 3.0f;
            this.pointerIconVisible = true;
        }
    }

    public final void applyAdditionalDisplayInputProperties() {
        synchronized (this.mAdditionalDisplayInputPropertiesLock) {
            AdditionalDisplayInputProperties additionalDisplayInputProperties = this.mAdditionalDisplayInputProperties.get(this.mRequestedPointerDisplayId);
            if (additionalDisplayInputProperties == null) {
                additionalDisplayInputProperties = DEFAULT_ADDITIONAL_DISPLAY_INPUT_PROPERTIES;
            }
            applyAdditionalDisplayInputPropertiesLocked(additionalDisplayInputProperties);
        }
    }

    @GuardedBy({"mAdditionalDisplayInputPropertiesLock"})
    public final void applyAdditionalDisplayInputPropertiesLocked(AdditionalDisplayInputProperties additionalDisplayInputProperties) {
        boolean z = additionalDisplayInputProperties.pointerIconVisible;
        AdditionalDisplayInputProperties additionalDisplayInputProperties2 = this.mCurrentDisplayProperties;
        if (z != additionalDisplayInputProperties2.pointerIconVisible) {
            additionalDisplayInputProperties2.pointerIconVisible = z;
            if (additionalDisplayInputProperties.pointerIconVisible) {
                int i = this.mPointerIconType;
                if (i == -1) {
                    Objects.requireNonNull(this.mPointerIcon);
                    this.mNative.setCustomPointerIcon(this.mPointerIcon);
                } else {
                    this.mNative.setPointerIconType(i);
                }
            } else {
                this.mNative.setPointerIconType(0);
            }
        }
        float f = additionalDisplayInputProperties.pointerAcceleration;
        AdditionalDisplayInputProperties additionalDisplayInputProperties3 = this.mCurrentDisplayProperties;
        if (f != additionalDisplayInputProperties3.pointerAcceleration) {
            additionalDisplayInputProperties3.pointerAcceleration = f;
            this.mNative.setPointerAcceleration(additionalDisplayInputProperties.pointerAcceleration);
        }
    }

    public final void updateAdditionalDisplayInputProperties(int i, Consumer<AdditionalDisplayInputProperties> consumer) {
        synchronized (this.mAdditionalDisplayInputPropertiesLock) {
            AdditionalDisplayInputProperties additionalDisplayInputProperties = this.mAdditionalDisplayInputProperties.get(i);
            if (additionalDisplayInputProperties == null) {
                additionalDisplayInputProperties = new AdditionalDisplayInputProperties();
                this.mAdditionalDisplayInputProperties.put(i, additionalDisplayInputProperties);
            }
            consumer.accept(additionalDisplayInputProperties);
            if (additionalDisplayInputProperties.allDefaults()) {
                this.mAdditionalDisplayInputProperties.remove(i);
            }
            if (i != this.mRequestedPointerDisplayId) {
                Log.i("InputManager", "Not applying additional properties for display " + i + " because the pointer is currently targeting display " + this.mRequestedPointerDisplayId + ".");
                return;
            }
            applyAdditionalDisplayInputPropertiesLocked(additionalDisplayInputProperties);
        }
    }
}
