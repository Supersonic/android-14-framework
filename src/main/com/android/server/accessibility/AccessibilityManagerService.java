package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityGestureEvent;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.MagnificationConfig;
import android.annotation.RequiresPermission;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.appwidget.AppWidgetManagerInternal;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.display.DisplayManager;
import android.hardware.fingerprint.IFingerprintService;
import android.media.AudioManagerInternal;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.provider.SettingsStringUtil;
import android.safetycenter.SafetyCenterManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.IWindow;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MagnificationSpec;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.WindowInfo;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowAttributes;
import android.view.accessibility.AccessibilityWindowInfo;
import android.view.accessibility.IAccessibilityInteractionConnection;
import android.view.accessibility.IAccessibilityManager;
import android.view.accessibility.IAccessibilityManagerClient;
import android.view.accessibility.IWindowMagnificationConnection;
import android.view.inputmethod.EditorInfo;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.accessibility.dialog.AccessibilityButtonChooserActivity;
import com.android.internal.accessibility.dialog.AccessibilityShortcutChooserActivity;
import com.android.internal.accessibility.util.AccessibilityStatsLogUtils;
import com.android.internal.accessibility.util.AccessibilityUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.PackageMonitor;
import com.android.internal.inputmethod.IAccessibilityInputMethodSession;
import com.android.internal.inputmethod.IRemoteAccessibilityInputConnection;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.IntPair;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.AccessibilityManagerInternal;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.accessibility.AbstractAccessibilityServiceConnection;
import com.android.server.accessibility.AccessibilityManagerService;
import com.android.server.accessibility.AccessibilitySecurityPolicy;
import com.android.server.accessibility.AccessibilityUserState;
import com.android.server.accessibility.AccessibilityWindowManager;
import com.android.server.accessibility.PolicyWarningUIController;
import com.android.server.accessibility.SystemActionPerformer;
import com.android.server.accessibility.magnification.MagnificationController;
import com.android.server.accessibility.magnification.MagnificationProcessor;
import com.android.server.accessibility.magnification.MagnificationScaleProvider;
import com.android.server.accessibility.magnification.WindowMagnificationManager;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.WindowManagerInternal;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AccessibilityManagerService extends IAccessibilityManager.Stub implements AbstractAccessibilityServiceConnection.SystemSupport, AccessibilityUserState.ServiceInfoChangeListener, AccessibilityWindowManager.AccessibilityEventSender, AccessibilitySecurityPolicy.AccessibilityUserManager, SystemActionPerformer.SystemActionsChangedListener {
    public static final int OWN_PROCESS_ID = Process.myPid();
    public static int sIdCounter = 1;
    public final AccessibilityDisplayListener mA11yDisplayListener;
    public SparseArray<SurfaceControl> mA11yOverlayLayers;
    public final AccessibilityWindowManager mA11yWindowManager;
    public final ActivityTaskManagerInternal mActivityTaskManagerService;
    public final CaptioningManagerImpl mCaptioningManagerImpl;
    public final Context mContext;
    public int mCurrentUserId;
    public EditorInfo mEditorInfo;
    public AlertDialog mEnableTouchExplorationDialog;
    public FingerprintGestureDispatcher mFingerprintGestureDispatcher;
    public final FlashNotificationsController mFlashNotificationsController;
    public final RemoteCallbackList<IAccessibilityManagerClient> mGlobalClients;
    public boolean mHasInputFilter;
    public boolean mInitialized;
    public boolean mInputBound;
    public AccessibilityInputFilter mInputFilter;
    public boolean mInputFilterInstalled;
    public boolean mInputSessionRequested;
    public InteractionBridge mInteractionBridge;
    public boolean mIsAccessibilityButtonShown;
    public KeyEventDispatcher mKeyEventDispatcher;
    public final Object mLock;
    public final MagnificationController mMagnificationController;
    public final MagnificationProcessor mMagnificationProcessor;
    public final Handler mMainHandler;
    public SparseArray<MotionEventInjector> mMotionEventInjectors;
    public final PackageManager mPackageManager;
    public final PowerManager mPowerManager;
    public final ProxyManager mProxyManager;
    public IRemoteAccessibilityInputConnection mRemoteInputConnection;
    public boolean mRestarting;
    public final AccessibilitySecurityPolicy mSecurityPolicy;
    public final List<SendWindowStateChangedEventRunnable> mSendWindowStateChangedEventRunnables;
    public final TextUtils.SimpleStringSplitter mStringColonSplitter;
    public SystemActionPerformer mSystemActionPerformer;
    public final List<AccessibilityServiceInfo> mTempAccessibilityServiceInfoList;
    public final Set<ComponentName> mTempComponentNameSet;
    public final IntArray mTempIntArray;
    public Point mTempPoint;
    public final Rect mTempRect;
    public final Rect mTempRect1;
    public final AccessibilityTraceManager mTraceManager;
    public final UiAutomationManager mUiAutomationManager;
    @VisibleForTesting
    final SparseArray<AccessibilityUserState> mUserStates;
    public final WindowManagerInternal mWindowManagerService;

    public static /* synthetic */ String lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$20(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$readAccessibilityButtonTargetsLocked$13(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$readAccessibilityShortcutKeySettingLocked$12(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$removeShortcutTargetForUnboundServiceLocked$21(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$removeShortcutTargetForUnboundServiceLocked$22(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$restoreAccessibilityButtonTargetsLocked$2(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$restoreAccessibilityButtonTargetsLocked$3(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$restoreAccessibilityButtonTargetsLocked$4(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$restoreLegacyDisplayMagnificationNavBarIfNeededLocked$0(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$restoreLegacyDisplayMagnificationNavBarIfNeededLocked$1(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$updateAccessibilityButtonTargetsLocked$17(String str) {
        return str;
    }

    public static /* synthetic */ String lambda$updateAccessibilityShortcutKeyTargetsLocked$15(String str) {
        return str;
    }

    public final int combineUserStateAndProxyState(int i, int i2) {
        return i | i2;
    }

    public final AccessibilityUserState getCurrentUserStateLocked() {
        return getUserStateLocked(this.mCurrentUserId);
    }

    public void changeMagnificationMode(int i, int i2) {
        synchronized (this.mLock) {
            if (i == 0) {
                persistMagnificationModeSettingsLocked(i2);
            } else {
                AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
                if (i2 != currentUserStateLocked.getMagnificationModeLocked(i)) {
                    currentUserStateLocked.setMagnificationModeLocked(i, i2);
                    updateMagnificationModeChangeSettingsLocked(currentUserStateLocked, i);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class LocalServiceImpl extends AccessibilityManagerInternal {
        public final AccessibilityManagerService mService;

        public LocalServiceImpl(AccessibilityManagerService accessibilityManagerService) {
            this.mService = accessibilityManagerService;
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void setImeSessionEnabled(SparseArray<IAccessibilityInputMethodSession> sparseArray, boolean z) {
            this.mService.scheduleSetImeSessionEnabled(sparseArray, z);
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void unbindInput() {
            this.mService.scheduleUnbindInput();
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void bindInput() {
            this.mService.scheduleBindInput();
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void createImeSession(ArraySet<Integer> arraySet) {
            this.mService.scheduleCreateImeSession(arraySet);
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void startInput(IRemoteAccessibilityInputConnection iRemoteAccessibilityInputConnection, EditorInfo editorInfo, boolean z) {
            this.mService.scheduleStartInput(iRemoteAccessibilityInputConnection, editorInfo, z);
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void performSystemAction(int i) {
            this.mService.getSystemActionPerformer().performSystemAction(i);
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public boolean isTouchExplorationEnabled(int i) {
            boolean isTouchExplorationEnabledLocked;
            synchronized (this.mService.mLock) {
                isTouchExplorationEnabledLocked = this.mService.getUserStateLocked(i).isTouchExplorationEnabledLocked();
            }
            return isTouchExplorationEnabledLocked;
        }
    }

    /* loaded from: classes.dex */
    public static final class Lifecycle extends SystemService {
        public final AccessibilityManagerService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new AccessibilityManagerService(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            LocalServices.addService(AccessibilityManagerInternal.class, new LocalServiceImpl(this.mService));
            publishBinderService("accessibility", this.mService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            this.mService.onBootPhase(i);
        }
    }

    @VisibleForTesting
    public AccessibilityManagerService(Context context, Handler handler, PackageManager packageManager, AccessibilitySecurityPolicy accessibilitySecurityPolicy, SystemActionPerformer systemActionPerformer, AccessibilityWindowManager accessibilityWindowManager, AccessibilityDisplayListener accessibilityDisplayListener, MagnificationController magnificationController, AccessibilityInputFilter accessibilityInputFilter, ProxyManager proxyManager) {
        Object obj = new Object();
        this.mLock = obj;
        this.mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        this.mTempRect = new Rect();
        this.mTempRect1 = new Rect();
        this.mTempComponentNameSet = new HashSet();
        this.mTempAccessibilityServiceInfoList = new ArrayList();
        this.mTempIntArray = new IntArray(0);
        this.mGlobalClients = new RemoteCallbackList<>();
        this.mUserStates = new SparseArray<>();
        this.mUiAutomationManager = new UiAutomationManager(obj);
        this.mSendWindowStateChangedEventRunnables = new ArrayList();
        this.mCurrentUserId = 0;
        this.mTempPoint = new Point();
        this.mA11yOverlayLayers = new SparseArray<>();
        this.mContext = context;
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        WindowManagerInternal windowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        this.mWindowManagerService = windowManagerInternal;
        this.mTraceManager = AccessibilityTraceManager.getInstance(windowManagerInternal.getAccessibilityController(), this, obj);
        this.mMainHandler = handler;
        this.mActivityTaskManagerService = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        this.mPackageManager = packageManager;
        this.mSecurityPolicy = accessibilitySecurityPolicy;
        this.mSystemActionPerformer = systemActionPerformer;
        this.mA11yWindowManager = accessibilityWindowManager;
        this.mA11yDisplayListener = accessibilityDisplayListener;
        this.mMagnificationController = magnificationController;
        this.mMagnificationProcessor = new MagnificationProcessor(magnificationController);
        this.mCaptioningManagerImpl = new CaptioningManagerImpl(context);
        this.mProxyManager = proxyManager;
        if (accessibilityInputFilter != null) {
            this.mInputFilter = accessibilityInputFilter;
            this.mHasInputFilter = true;
        }
        this.mFlashNotificationsController = new FlashNotificationsController(context);
        init();
    }

    public AccessibilityManagerService(Context context) {
        Object obj = new Object();
        this.mLock = obj;
        this.mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        this.mTempRect = new Rect();
        this.mTempRect1 = new Rect();
        this.mTempComponentNameSet = new HashSet();
        this.mTempAccessibilityServiceInfoList = new ArrayList();
        this.mTempIntArray = new IntArray(0);
        this.mGlobalClients = new RemoteCallbackList<>();
        this.mUserStates = new SparseArray<>();
        this.mUiAutomationManager = new UiAutomationManager(obj);
        this.mSendWindowStateChangedEventRunnables = new ArrayList();
        this.mCurrentUserId = 0;
        this.mTempPoint = new Point();
        this.mA11yOverlayLayers = new SparseArray<>();
        this.mContext = context;
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        WindowManagerInternal windowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        this.mWindowManagerService = windowManagerInternal;
        AccessibilityTraceManager accessibilityTraceManager = AccessibilityTraceManager.getInstance(windowManagerInternal.getAccessibilityController(), this, obj);
        this.mTraceManager = accessibilityTraceManager;
        MainHandler mainHandler = new MainHandler(context.getMainLooper());
        this.mMainHandler = mainHandler;
        this.mActivityTaskManagerService = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        this.mPackageManager = context.getPackageManager();
        AccessibilitySecurityPolicy accessibilitySecurityPolicy = new AccessibilitySecurityPolicy(new PolicyWarningUIController(mainHandler, context, new PolicyWarningUIController.NotificationController(context)), context, this);
        this.mSecurityPolicy = accessibilitySecurityPolicy;
        AccessibilityWindowManager accessibilityWindowManager = new AccessibilityWindowManager(obj, mainHandler, windowManagerInternal, this, accessibilitySecurityPolicy, this, accessibilityTraceManager);
        this.mA11yWindowManager = accessibilityWindowManager;
        this.mA11yDisplayListener = new AccessibilityDisplayListener(context, mainHandler);
        MagnificationController magnificationController = new MagnificationController(this, obj, context, new MagnificationScaleProvider(context));
        this.mMagnificationController = magnificationController;
        this.mMagnificationProcessor = new MagnificationProcessor(magnificationController);
        this.mCaptioningManagerImpl = new CaptioningManagerImpl(context);
        this.mProxyManager = new ProxyManager(obj, accessibilityWindowManager, context);
        this.mFlashNotificationsController = new FlashNotificationsController(context);
        init();
    }

    public final void init() {
        this.mSecurityPolicy.setAccessibilityWindowManager(this.mA11yWindowManager);
        registerBroadcastReceivers();
        new AccessibilityContentObserver(this.mMainHandler).register(this.mContext.getContentResolver());
        disableAccessibilityMenuToMigrateIfNeeded();
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport, com.android.server.accessibility.AccessibilitySecurityPolicy.AccessibilityUserManager
    public int getCurrentUserIdLocked() {
        return this.mCurrentUserId;
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public boolean isAccessibilityButtonShown() {
        return this.mIsAccessibilityButtonShown;
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public Pair<float[], MagnificationSpec> getWindowTransformationMatrixAndMagnificationSpec(int i) {
        WindowInfo findWindowInfoByIdLocked;
        IBinder windowTokenForUserAndWindowIdLocked;
        synchronized (this.mLock) {
            findWindowInfoByIdLocked = this.mA11yWindowManager.findWindowInfoByIdLocked(i);
        }
        if (findWindowInfoByIdLocked != null) {
            MagnificationSpec magnificationSpec = new MagnificationSpec();
            magnificationSpec.setTo(findWindowInfoByIdLocked.mMagnificationSpec);
            return new Pair<>(findWindowInfoByIdLocked.mTransformMatrix, magnificationSpec);
        }
        synchronized (this.mLock) {
            windowTokenForUserAndWindowIdLocked = this.mA11yWindowManager.getWindowTokenForUserAndWindowIdLocked(this.mCurrentUserId, i);
        }
        Pair<Matrix, MagnificationSpec> windowTransformationMatrixAndMagnificationSpec = this.mWindowManagerService.getWindowTransformationMatrixAndMagnificationSpec(windowTokenForUserAndWindowIdLocked);
        float[] fArr = new float[9];
        Matrix matrix = (Matrix) windowTransformationMatrixAndMagnificationSpec.first;
        MagnificationSpec magnificationSpec2 = (MagnificationSpec) windowTransformationMatrixAndMagnificationSpec.second;
        if (!magnificationSpec2.isNop()) {
            float f = magnificationSpec2.scale;
            matrix.postScale(f, f);
            matrix.postTranslate(magnificationSpec2.offsetX, magnificationSpec2.offsetY);
        }
        matrix.getValues(fArr);
        return new Pair<>(fArr, (MagnificationSpec) windowTransformationMatrixAndMagnificationSpec.second);
    }

    public IAccessibilityManager.WindowTransformationSpec getWindowTransformationSpec(int i) {
        IAccessibilityManager.WindowTransformationSpec windowTransformationSpec = new IAccessibilityManager.WindowTransformationSpec();
        Pair<float[], MagnificationSpec> windowTransformationMatrixAndMagnificationSpec = getWindowTransformationMatrixAndMagnificationSpec(i);
        windowTransformationSpec.transformationMatrix = (float[]) windowTransformationMatrixAndMagnificationSpec.first;
        windowTransformationSpec.magnificationSpec = (MagnificationSpec) windowTransformationMatrixAndMagnificationSpec.second;
        return windowTransformationSpec;
    }

    @Override // com.android.server.accessibility.AccessibilityUserState.ServiceInfoChangeListener
    public void onServiceInfoChangedLocked(AccessibilityUserState accessibilityUserState) {
        this.mSecurityPolicy.onBoundServicesChangedLocked(accessibilityUserState.mUserId, accessibilityUserState.mBoundServices);
        scheduleNotifyClientsOfServicesStateChangeLocked(accessibilityUserState);
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public FingerprintGestureDispatcher getFingerprintGestureDispatcher() {
        return this.mFingerprintGestureDispatcher;
    }

    public void onInputFilterInstalled(boolean z) {
        synchronized (this.mLock) {
            this.mInputFilterInstalled = z;
            this.mLock.notifyAll();
        }
    }

    public final void onBootPhase(int i) {
        if (i == 500 && this.mPackageManager.hasSystemFeature("android.software.app_widgets")) {
            this.mSecurityPolicy.setAppWidgetManager((AppWidgetManagerInternal) LocalServices.getService(AppWidgetManagerInternal.class));
        }
        if (i == 600) {
            setNonA11yToolNotificationToMatchSafetyCenter();
        }
    }

    public final void setNonA11yToolNotificationToMatchSafetyCenter() {
        boolean z = !((SafetyCenterManager) this.mContext.getSystemService(SafetyCenterManager.class)).isSafetyCenterEnabled();
        synchronized (this.mLock) {
            this.mSecurityPolicy.setSendingNonA11yToolNotificationLocked(z);
        }
    }

    public AccessibilityUserState getCurrentUserState() {
        AccessibilityUserState currentUserStateLocked;
        synchronized (this.mLock) {
            currentUserStateLocked = getCurrentUserStateLocked();
        }
        return currentUserStateLocked;
    }

    public final AccessibilityUserState getUserState(int i) {
        AccessibilityUserState userStateLocked;
        synchronized (this.mLock) {
            userStateLocked = getUserStateLocked(i);
        }
        return userStateLocked;
    }

    public final AccessibilityUserState getUserStateLocked(int i) {
        AccessibilityUserState accessibilityUserState = this.mUserStates.get(i);
        if (accessibilityUserState == null) {
            AccessibilityUserState accessibilityUserState2 = new AccessibilityUserState(i, this.mContext, this);
            this.mUserStates.put(i, accessibilityUserState2);
            return accessibilityUserState2;
        }
        return accessibilityUserState;
    }

    public boolean getBindInstantServiceAllowed(int i) {
        boolean bindInstantServiceAllowedLocked;
        synchronized (this.mLock) {
            bindInstantServiceAllowedLocked = getUserStateLocked(i).getBindInstantServiceAllowedLocked();
        }
        return bindInstantServiceAllowedLocked;
    }

    public void setBindInstantServiceAllowed(int i, boolean z) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_BIND_INSTANT_SERVICE", "setBindInstantServiceAllowed");
        synchronized (this.mLock) {
            AccessibilityUserState userStateLocked = getUserStateLocked(i);
            if (z != userStateLocked.getBindInstantServiceAllowedLocked()) {
                userStateLocked.setBindInstantServiceAllowedLocked(z);
                onUserStateChangedLocked(userStateLocked);
            }
        }
    }

    public final void onSomePackagesChangedLocked() {
        AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
        currentUserStateLocked.mInstalledServices.clear();
        if (readConfigurationForUserStateLocked(currentUserStateLocked)) {
            onUserStateChangedLocked(currentUserStateLocked);
        }
    }

    /* renamed from: com.android.server.accessibility.AccessibilityManagerService$1 */
    /* loaded from: classes.dex */
    public class C02671 extends PackageMonitor {
        public C02671() {
            AccessibilityManagerService.this = r1;
        }

        public void onSomePackagesChanged() {
            if (AccessibilityManagerService.this.mTraceManager.isA11yTracingEnabledForTypes(32768L)) {
                AccessibilityManagerService.this.mTraceManager.logTrace("AccessibilityManagerService.PM.onSomePackagesChanged", 32768L);
            }
            synchronized (AccessibilityManagerService.this.mLock) {
                if (getChangingUserId() != AccessibilityManagerService.this.mCurrentUserId) {
                    return;
                }
                AccessibilityManagerService.this.onSomePackagesChangedLocked();
            }
        }

        public void onPackageUpdateFinished(final String str, int i) {
            boolean z;
            if (AccessibilityManagerService.this.mTraceManager.isA11yTracingEnabledForTypes(32768L)) {
                AccessibilityManagerService.this.mTraceManager.logTrace("AccessibilityManagerService.PM.onPackageUpdateFinished", 32768L, "packageName=" + str + ";uid=" + i);
            }
            synchronized (AccessibilityManagerService.this.mLock) {
                int changingUserId = getChangingUserId();
                if (changingUserId != AccessibilityManagerService.this.mCurrentUserId) {
                    return;
                }
                AccessibilityUserState userStateLocked = AccessibilityManagerService.this.getUserStateLocked(changingUserId);
                if (!userStateLocked.getBindingServicesLocked().removeIf(new Predicate() { // from class: com.android.server.accessibility.AccessibilityManagerService$1$$ExternalSyntheticLambda1
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$onPackageUpdateFinished$0;
                        lambda$onPackageUpdateFinished$0 = AccessibilityManagerService.C02671.lambda$onPackageUpdateFinished$0(str, (ComponentName) obj);
                        return lambda$onPackageUpdateFinished$0;
                    }
                }) && !userStateLocked.mCrashedServices.removeIf(new Predicate() { // from class: com.android.server.accessibility.AccessibilityManagerService$1$$ExternalSyntheticLambda2
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$onPackageUpdateFinished$1;
                        lambda$onPackageUpdateFinished$1 = AccessibilityManagerService.C02671.lambda$onPackageUpdateFinished$1(str, (ComponentName) obj);
                        return lambda$onPackageUpdateFinished$1;
                    }
                })) {
                    z = false;
                    userStateLocked.mInstalledServices.clear();
                    boolean readConfigurationForUserStateLocked = AccessibilityManagerService.this.readConfigurationForUserStateLocked(userStateLocked);
                    if (!z || readConfigurationForUserStateLocked) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(userStateLocked);
                    }
                    AccessibilityManagerService.this.migrateAccessibilityButtonSettingsIfNecessaryLocked(userStateLocked, str, 0);
                }
                z = true;
                userStateLocked.mInstalledServices.clear();
                boolean readConfigurationForUserStateLocked2 = AccessibilityManagerService.this.readConfigurationForUserStateLocked(userStateLocked);
                if (!z) {
                }
                AccessibilityManagerService.this.onUserStateChangedLocked(userStateLocked);
                AccessibilityManagerService.this.migrateAccessibilityButtonSettingsIfNecessaryLocked(userStateLocked, str, 0);
            }
        }

        public static /* synthetic */ boolean lambda$onPackageUpdateFinished$0(String str, ComponentName componentName) {
            return componentName != null && componentName.getPackageName().equals(str);
        }

        public static /* synthetic */ boolean lambda$onPackageUpdateFinished$1(String str, ComponentName componentName) {
            return componentName != null && componentName.getPackageName().equals(str);
        }

        public void onPackageRemoved(final String str, int i) {
            if (AccessibilityManagerService.this.mTraceManager.isA11yTracingEnabledForTypes(32768L)) {
                AccessibilityTraceManager accessibilityTraceManager = AccessibilityManagerService.this.mTraceManager;
                accessibilityTraceManager.logTrace("AccessibilityManagerService.PM.onPackageRemoved", 32768L, "packageName=" + str + ";uid=" + i);
            }
            synchronized (AccessibilityManagerService.this.mLock) {
                int changingUserId = getChangingUserId();
                if (changingUserId != AccessibilityManagerService.this.mCurrentUserId) {
                    return;
                }
                AccessibilityUserState userStateLocked = AccessibilityManagerService.this.getUserStateLocked(changingUserId);
                Predicate<? super ComponentName> predicate = new Predicate() { // from class: com.android.server.accessibility.AccessibilityManagerService$1$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$onPackageRemoved$2;
                        lambda$onPackageRemoved$2 = AccessibilityManagerService.C02671.lambda$onPackageRemoved$2(str, (ComponentName) obj);
                        return lambda$onPackageRemoved$2;
                    }
                };
                userStateLocked.mBindingServices.removeIf(predicate);
                userStateLocked.mCrashedServices.removeIf(predicate);
                Iterator<ComponentName> it = userStateLocked.mEnabledServices.iterator();
                boolean z = false;
                while (it.hasNext()) {
                    ComponentName next = it.next();
                    if (next.getPackageName().equals(str)) {
                        it.remove();
                        userStateLocked.mTouchExplorationGrantedServices.remove(next);
                        z = true;
                    }
                }
                if (z) {
                    AccessibilityManagerService.this.persistComponentNamesToSettingLocked("enabled_accessibility_services", userStateLocked.mEnabledServices, changingUserId);
                    AccessibilityManagerService.this.persistComponentNamesToSettingLocked("touch_exploration_granted_accessibility_services", userStateLocked.mTouchExplorationGrantedServices, changingUserId);
                    AccessibilityManagerService.this.onUserStateChangedLocked(userStateLocked);
                }
            }
        }

        public static /* synthetic */ boolean lambda$onPackageRemoved$2(String str, ComponentName componentName) {
            return componentName != null && componentName.getPackageName().equals(str);
        }

        public boolean onHandleForceStop(Intent intent, String[] strArr, int i, boolean z) {
            if (AccessibilityManagerService.this.mTraceManager.isA11yTracingEnabledForTypes(32768L)) {
                AccessibilityManagerService.this.mTraceManager.logTrace("AccessibilityManagerService.PM.onHandleForceStop", 32768L, "intent=" + intent + ";packages=" + Arrays.toString(strArr) + ";uid=" + i + ";doit=" + z);
            }
            synchronized (AccessibilityManagerService.this.mLock) {
                int changingUserId = getChangingUserId();
                if (changingUserId != AccessibilityManagerService.this.mCurrentUserId) {
                    return false;
                }
                AccessibilityUserState userStateLocked = AccessibilityManagerService.this.getUserStateLocked(changingUserId);
                Iterator<ComponentName> it = userStateLocked.mEnabledServices.iterator();
                while (it.hasNext()) {
                    ComponentName next = it.next();
                    String packageName = next.getPackageName();
                    for (String str : strArr) {
                        if (packageName.equals(str)) {
                            if (!z) {
                                return true;
                            }
                            it.remove();
                            userStateLocked.getBindingServicesLocked().remove(next);
                            userStateLocked.getCrashedServicesLocked().remove(next);
                            AccessibilityManagerService.this.persistComponentNamesToSettingLocked("enabled_accessibility_services", userStateLocked.mEnabledServices, changingUserId);
                            AccessibilityManagerService.this.onUserStateChangedLocked(userStateLocked);
                        }
                    }
                }
                return false;
            }
        }
    }

    public final void registerBroadcastReceivers() {
        new C02671().register(this.mContext, (Looper) null, UserHandle.ALL, true);
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        if (packageManagerInternal != null) {
            packageManagerInternal.getPackageList(new PackageManagerInternal.PackageListObserver() { // from class: com.android.server.accessibility.AccessibilityManagerService.2
                {
                    AccessibilityManagerService.this = this;
                }

                @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
                public void onPackageAdded(String str, int i) {
                    int userId = UserHandle.getUserId(i);
                    synchronized (AccessibilityManagerService.this.mLock) {
                        if (userId == AccessibilityManagerService.this.mCurrentUserId) {
                            AccessibilityManagerService.this.onSomePackagesChangedLocked();
                        }
                    }
                }
            });
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        intentFilter.addAction("android.os.action.SETTING_RESTORED");
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.accessibility.AccessibilityManagerService.3
            {
                AccessibilityManagerService.this = this;
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (AccessibilityManagerService.this.mTraceManager.isA11yTracingEnabledForTypes(65536L)) {
                    AccessibilityTraceManager accessibilityTraceManager = AccessibilityManagerService.this.mTraceManager;
                    accessibilityTraceManager.logTrace("AccessibilityManagerService.BR.onReceive", 65536L, "context=" + context + ";intent=" + intent);
                }
                String action = intent.getAction();
                if ("android.intent.action.USER_SWITCHED".equals(action)) {
                    AccessibilityManagerService.this.switchUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                    AccessibilityManagerService.this.unlockUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                    AccessibilityManagerService.this.removeUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if ("android.os.action.SETTING_RESTORED".equals(action)) {
                    String stringExtra = intent.getStringExtra("setting_name");
                    if ("enabled_accessibility_services".equals(stringExtra)) {
                        synchronized (AccessibilityManagerService.this.mLock) {
                            AccessibilityManagerService.this.restoreEnabledAccessibilityServicesLocked(intent.getStringExtra("previous_value"), intent.getStringExtra("new_value"), intent.getIntExtra("restored_from_sdk_int", 0));
                        }
                    } else if ("accessibility_display_magnification_navbar_enabled".equals(stringExtra)) {
                        synchronized (AccessibilityManagerService.this.mLock) {
                            AccessibilityManagerService.this.restoreLegacyDisplayMagnificationNavBarIfNeededLocked(intent.getStringExtra("new_value"), intent.getIntExtra("restored_from_sdk_int", 0));
                        }
                    } else if ("accessibility_button_targets".equals(stringExtra)) {
                        synchronized (AccessibilityManagerService.this.mLock) {
                            AccessibilityManagerService.this.restoreAccessibilityButtonTargetsLocked(intent.getStringExtra("previous_value"), intent.getStringExtra("new_value"));
                        }
                    }
                }
            }
        }, UserHandle.ALL, intentFilter, null, null);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.safetycenter.action.SAFETY_CENTER_ENABLED_CHANGED");
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.accessibility.AccessibilityManagerService.4
            {
                AccessibilityManagerService.this = this;
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                AccessibilityManagerService.this.setNonA11yToolNotificationToMatchSafetyCenter();
            }
        }, UserHandle.ALL, intentFilter2, null, this.mMainHandler, 2);
    }

    public final void disableAccessibilityMenuToMigrateIfNeeded() {
        int i;
        synchronized (this.mLock) {
            i = this.mCurrentUserId;
        }
        ComponentName accessibilityMenuComponentToMigrate = AccessibilityUtils.getAccessibilityMenuComponentToMigrate(this.mPackageManager, i);
        if (accessibilityMenuComponentToMigrate != null) {
            this.mPackageManager.setComponentEnabledSetting(accessibilityMenuComponentToMigrate, 2, 1);
        }
    }

    public final void restoreLegacyDisplayMagnificationNavBarIfNeededLocked(String str, int i) {
        if (i >= 30) {
            return;
        }
        try {
            boolean z = Integer.parseInt(str) == 1;
            AccessibilityUserState userStateLocked = getUserStateLocked(0);
            ArraySet arraySet = new ArraySet();
            readColonDelimitedSettingToSet("accessibility_button_targets", userStateLocked.mUserId, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda39
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$restoreLegacyDisplayMagnificationNavBarIfNeededLocked$0;
                    lambda$restoreLegacyDisplayMagnificationNavBarIfNeededLocked$0 = AccessibilityManagerService.lambda$restoreLegacyDisplayMagnificationNavBarIfNeededLocked$0((String) obj);
                    return lambda$restoreLegacyDisplayMagnificationNavBarIfNeededLocked$0;
                }
            }, arraySet);
            if (arraySet.contains("com.android.server.accessibility.MagnificationController") == z) {
                return;
            }
            if (z) {
                arraySet.add("com.android.server.accessibility.MagnificationController");
            } else {
                arraySet.remove("com.android.server.accessibility.MagnificationController");
            }
            persistColonDelimitedSetToSettingLocked("accessibility_button_targets", userStateLocked.mUserId, arraySet, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda40
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$restoreLegacyDisplayMagnificationNavBarIfNeededLocked$1;
                    lambda$restoreLegacyDisplayMagnificationNavBarIfNeededLocked$1 = AccessibilityManagerService.lambda$restoreLegacyDisplayMagnificationNavBarIfNeededLocked$1((String) obj);
                    return lambda$restoreLegacyDisplayMagnificationNavBarIfNeededLocked$1;
                }
            });
            readAccessibilityButtonTargetsLocked(userStateLocked);
            onUserStateChangedLocked(userStateLocked);
        } catch (NumberFormatException e) {
            Slog.w("AccessibilityManagerService", "number format is incorrect" + e);
        }
    }

    public long addClient(IAccessibilityManagerClient iAccessibilityManagerClient, int i) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.addClient", 4L, "callback=" + iAccessibilityManagerClient + ";userId=" + i);
        }
        synchronized (this.mLock) {
            int resolveCallingUserIdEnforcingPermissionsLocked = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(i);
            AccessibilityUserState userStateLocked = getUserStateLocked(resolveCallingUserIdEnforcingPermissionsLocked);
            Client client = new Client(iAccessibilityManagerClient, Binder.getCallingUid(), userStateLocked);
            if (this.mSecurityPolicy.isCallerInteractingAcrossUsers(i)) {
                this.mGlobalClients.register(iAccessibilityManagerClient, client);
                return IntPair.of(combineUserStateAndProxyState(getClientStateLocked(userStateLocked), this.mProxyManager.getStateLocked()), client.mLastSentRelevantEventTypes);
            }
            userStateLocked.mUserClients.register(iAccessibilityManagerClient, client);
            return IntPair.of(resolveCallingUserIdEnforcingPermissionsLocked == this.mCurrentUserId ? combineUserStateAndProxyState(getClientStateLocked(userStateLocked), this.mProxyManager.getStateLocked()) : 0, client.mLastSentRelevantEventTypes);
        }
    }

    public boolean removeClient(IAccessibilityManagerClient iAccessibilityManagerClient, int i) {
        synchronized (this.mLock) {
            AccessibilityUserState userStateLocked = getUserStateLocked(this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(i));
            if (this.mSecurityPolicy.isCallerInteractingAcrossUsers(i)) {
                return this.mGlobalClients.unregister(iAccessibilityManagerClient);
            }
            return userStateLocked.mUserClients.unregister(iAccessibilityManagerClient);
        }
    }

    public void sendAccessibilityEvent(AccessibilityEvent accessibilityEvent, int i) {
        int resolveCallingUserIdEnforcingPermissionsLocked;
        boolean z;
        boolean z2;
        AccessibilityWindowInfo pictureInPictureWindowLocked;
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            this.mTraceManager.logTrace("AccessibilityManagerService.sendAccessibilityEvent", 4L, "event=" + accessibilityEvent + ";userId=" + i);
        }
        synchronized (this.mLock) {
            if (accessibilityEvent.getWindowId() == -3 && (pictureInPictureWindowLocked = this.mA11yWindowManager.getPictureInPictureWindowLocked()) != null) {
                accessibilityEvent.setWindowId(pictureInPictureWindowLocked.getId());
            }
            resolveCallingUserIdEnforcingPermissionsLocked = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(i);
            accessibilityEvent.setPackageName(this.mSecurityPolicy.resolveValidReportedPackageLocked(accessibilityEvent.getPackageName(), UserHandle.getCallingAppId(), resolveCallingUserIdEnforcingPermissionsLocked, IAccessibilityManager.Stub.getCallingPid()));
            int i2 = this.mCurrentUserId;
            z = true;
            if (resolveCallingUserIdEnforcingPermissionsLocked == i2) {
                if (this.mSecurityPolicy.canDispatchAccessibilityEventLocked(i2, accessibilityEvent)) {
                    this.mA11yWindowManager.updateActiveAndAccessibilityFocusedWindowLocked(this.mCurrentUserId, accessibilityEvent.getWindowId(), accessibilityEvent.getSourceNodeId(), accessibilityEvent.getEventType(), accessibilityEvent.getAction());
                    this.mSecurityPolicy.updateEventSourceLocked(accessibilityEvent);
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (this.mHasInputFilter && this.mInputFilter != null) {
                    this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda7
                        @Override // java.util.function.BiConsumer
                        public final void accept(Object obj, Object obj2) {
                            ((AccessibilityManagerService) obj).sendAccessibilityEventToInputFilter((AccessibilityEvent) obj2);
                        }
                    }, this, AccessibilityEvent.obtain(accessibilityEvent)));
                }
            } else {
                z2 = false;
            }
        }
        if (z2) {
            int displayId = accessibilityEvent.getDisplayId();
            synchronized (this.mLock) {
                int windowId = accessibilityEvent.getWindowId();
                if (windowId != -1 && displayId == -1) {
                    displayId = this.mA11yWindowManager.getDisplayIdByUserIdAndWindowIdLocked(resolveCallingUserIdEnforcingPermissionsLocked, windowId);
                    accessibilityEvent.setDisplayId(displayId);
                }
                if (accessibilityEvent.getEventType() != 32 || displayId == -1 || !this.mA11yWindowManager.isTrackingWindowsLocked(displayId)) {
                    z = false;
                }
            }
            if (z) {
                if (this.mTraceManager.isA11yTracingEnabledForTypes(512L)) {
                    this.mTraceManager.logTrace("WindowManagerInternal.computeWindowsForAccessibility", 512L, "display=" + displayId);
                }
                ((WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class)).computeWindowsForAccessibility(displayId);
                if (postponeWindowStateEvent(accessibilityEvent)) {
                    return;
                }
            }
            synchronized (this.mLock) {
                dispatchAccessibilityEventLocked(accessibilityEvent);
            }
        }
        if (OWN_PROCESS_ID != Binder.getCallingPid()) {
            accessibilityEvent.recycle();
        }
    }

    public final void dispatchAccessibilityEventLocked(AccessibilityEvent accessibilityEvent) {
        if (this.mProxyManager.isProxyed(accessibilityEvent.getDisplayId())) {
            this.mProxyManager.sendAccessibilityEventLocked(accessibilityEvent);
        } else {
            notifyAccessibilityServicesDelayedLocked(accessibilityEvent, false);
            notifyAccessibilityServicesDelayedLocked(accessibilityEvent, true);
        }
        this.mUiAutomationManager.sendAccessibilityEventLocked(accessibilityEvent);
    }

    public final void sendAccessibilityEventToInputFilter(AccessibilityEvent accessibilityEvent) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            if (this.mHasInputFilter && (accessibilityInputFilter = this.mInputFilter) != null) {
                accessibilityInputFilter.notifyAccessibilityEvent(accessibilityEvent);
            }
        }
        accessibilityEvent.recycle();
    }

    public void registerSystemAction(RemoteAction remoteAction, int i) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.registerSystemAction", 4L, "action=" + remoteAction + ";actionId=" + i);
        }
        this.mSecurityPolicy.enforceCallingOrSelfPermission("android.permission.MANAGE_ACCESSIBILITY");
        getSystemActionPerformer().registerSystemAction(i, remoteAction);
    }

    public void unregisterSystemAction(int i) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.unregisterSystemAction", 4L, "actionId=" + i);
        }
        this.mSecurityPolicy.enforceCallingOrSelfPermission("android.permission.MANAGE_ACCESSIBILITY");
        getSystemActionPerformer().unregisterSystemAction(i);
    }

    public final SystemActionPerformer getSystemActionPerformer() {
        if (this.mSystemActionPerformer == null) {
            this.mSystemActionPerformer = new SystemActionPerformer(this.mContext, this.mWindowManagerService, null, this);
        }
        return this.mSystemActionPerformer;
    }

    public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(int i) {
        int resolveCallingUserIdEnforcingPermissionsLocked;
        ArrayList arrayList;
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            this.mTraceManager.logTrace("AccessibilityManagerService.getInstalledAccessibilityServiceList", 4L, "userId=" + i);
        }
        synchronized (this.mLock) {
            resolveCallingUserIdEnforcingPermissionsLocked = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(i);
            arrayList = new ArrayList(getUserStateLocked(resolveCallingUserIdEnforcingPermissionsLocked).mInstalledServices);
        }
        if (Binder.getCallingPid() == OWN_PROCESS_ID) {
            return arrayList;
        }
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int callingUid = Binder.getCallingUid();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            if (packageManagerInternal.filterAppAccess(((AccessibilityServiceInfo) arrayList.get(size)).getComponentName().getPackageName(), callingUid, resolveCallingUserIdEnforcingPermissionsLocked)) {
                arrayList.remove(size);
            }
        }
        return arrayList;
    }

    public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int i, int i2) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.getEnabledAccessibilityServiceList", 4L, "feedbackType=" + i + ";userId=" + i2);
        }
        synchronized (this.mLock) {
            AccessibilityUserState userStateLocked = getUserStateLocked(this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(i2));
            if (this.mUiAutomationManager.suppressingAccessibilityServicesLocked()) {
                return Collections.emptyList();
            }
            ArrayList<AccessibilityServiceConnection> arrayList = userStateLocked.mBoundServices;
            int size = arrayList.size();
            ArrayList arrayList2 = new ArrayList(size);
            for (int i3 = 0; i3 < size; i3++) {
                AccessibilityServiceConnection accessibilityServiceConnection = arrayList.get(i3);
                if ((accessibilityServiceConnection.mFeedbackType & i) != 0 || i == -1) {
                    arrayList2.add(accessibilityServiceConnection.getServiceInfo());
                }
            }
            return arrayList2;
        }
    }

    public void interrupt(int i) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            this.mTraceManager.logTrace("AccessibilityManagerService.interrupt", 4L, "userId=" + i);
        }
        synchronized (this.mLock) {
            int resolveCallingUserIdEnforcingPermissionsLocked = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(i);
            if (resolveCallingUserIdEnforcingPermissionsLocked != this.mCurrentUserId) {
                return;
            }
            ArrayList<AccessibilityServiceConnection> arrayList = getUserStateLocked(resolveCallingUserIdEnforcingPermissionsLocked).mBoundServices;
            ArrayList arrayList2 = new ArrayList(arrayList.size() + this.mProxyManager.getNumProxysLocked());
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                AccessibilityServiceConnection accessibilityServiceConnection = arrayList.get(i2);
                IBinder iBinder = accessibilityServiceConnection.mService;
                IAccessibilityServiceClient iAccessibilityServiceClient = accessibilityServiceConnection.mServiceInterface;
                if (iBinder != null && iAccessibilityServiceClient != null) {
                    arrayList2.add(iAccessibilityServiceClient);
                }
            }
            this.mProxyManager.addServiceInterfacesLocked(arrayList2);
            int size = arrayList2.size();
            for (int i3 = 0; i3 < size; i3++) {
                try {
                    if (this.mTraceManager.isA11yTracingEnabledForTypes(2L)) {
                        this.mTraceManager.logTrace("AccessibilityManagerService.IAccessibilityServiceClient.onInterrupt", 2L);
                    }
                    ((IAccessibilityServiceClient) arrayList2.get(i3)).onInterrupt();
                } catch (RemoteException e) {
                    Slog.e("AccessibilityManagerService", "Error sending interrupt request to " + arrayList2.get(i3), e);
                }
            }
        }
    }

    public int addAccessibilityInteractionConnection(IWindow iWindow, IBinder iBinder, IAccessibilityInteractionConnection iAccessibilityInteractionConnection, String str, int i) throws RemoteException {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.addAccessibilityInteractionConnection", 4L, "windowToken=" + iWindow + "leashToken=" + iBinder + ";connection=" + iAccessibilityInteractionConnection + "; packageName=" + str + ";userId=" + i);
        }
        return this.mA11yWindowManager.addAccessibilityInteractionConnection(iWindow, iBinder, iAccessibilityInteractionConnection, str, i);
    }

    public void removeAccessibilityInteractionConnection(IWindow iWindow) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.removeAccessibilityInteractionConnection", 4L, "window=" + iWindow);
        }
        this.mA11yWindowManager.removeAccessibilityInteractionConnection(iWindow);
    }

    public void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection iAccessibilityInteractionConnection) throws RemoteException {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.setPictureInPictureActionReplacingConnection", 4L, "connection=" + iAccessibilityInteractionConnection);
        }
        this.mSecurityPolicy.enforceCallingPermission("android.permission.MODIFY_ACCESSIBILITY_DATA", "setPictureInPictureActionReplacingConnection");
        this.mA11yWindowManager.setPictureInPictureActionReplacingConnection(iAccessibilityInteractionConnection);
    }

    public void registerUiTestAutomationService(IBinder iBinder, IAccessibilityServiceClient iAccessibilityServiceClient, AccessibilityServiceInfo accessibilityServiceInfo, int i) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.registerUiTestAutomationService", 4L, "owner=" + iBinder + ";serviceClient=" + iAccessibilityServiceClient + ";accessibilityServiceInfo=" + accessibilityServiceInfo + ";flags=" + i);
        }
        this.mSecurityPolicy.enforceCallingPermission("android.permission.RETRIEVE_WINDOW_CONTENT", "registerUiTestAutomationService");
        synchronized (this.mLock) {
            UiAutomationManager uiAutomationManager = this.mUiAutomationManager;
            Context context = this.mContext;
            int i2 = sIdCounter;
            sIdCounter = i2 + 1;
            uiAutomationManager.registerUiTestAutomationServiceLocked(iBinder, iAccessibilityServiceClient, context, accessibilityServiceInfo, i2, this.mMainHandler, this.mSecurityPolicy, this, getTraceManager(), this.mWindowManagerService, getSystemActionPerformer(), this.mA11yWindowManager, i);
            onUserStateChangedLocked(getCurrentUserStateLocked());
        }
    }

    public void unregisterUiTestAutomationService(IAccessibilityServiceClient iAccessibilityServiceClient) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.unregisterUiTestAutomationService", 4L, "serviceClient=" + iAccessibilityServiceClient);
        }
        synchronized (this.mLock) {
            this.mUiAutomationManager.unregisterUiTestAutomationServiceLocked(iAccessibilityServiceClient);
        }
    }

    public IBinder getWindowToken(int i, int i2) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.getWindowToken", 4L, "windowId=" + i + ";userId=" + i2);
        }
        this.mSecurityPolicy.enforceCallingPermission("android.permission.RETRIEVE_WINDOW_TOKEN", "getWindowToken");
        synchronized (this.mLock) {
            if (this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(i2) != this.mCurrentUserId) {
                return null;
            }
            AccessibilityWindowInfo findA11yWindowInfoByIdLocked = this.mA11yWindowManager.findA11yWindowInfoByIdLocked(i);
            if (findA11yWindowInfoByIdLocked == null) {
                return null;
            }
            return this.mA11yWindowManager.getWindowTokenForUserAndWindowIdLocked(i2, findA11yWindowInfoByIdLocked.getId());
        }
    }

    public void notifyAccessibilityButtonClicked(int i, String str) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.notifyAccessibilityButtonClicked", 4L, "displayId=" + i + ";targetName=" + str);
        }
        if (this.mContext.checkCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE") != 0) {
            throw new SecurityException("Caller does not hold permission android.permission.STATUS_BAR_SERVICE");
        }
        if (str == null) {
            synchronized (this.mLock) {
                str = getCurrentUserStateLocked().getTargetAssignedToAccessibilityButton();
            }
        }
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new AccessibilityManagerService$$ExternalSyntheticLambda1(), this, Integer.valueOf(i), 0, str));
    }

    public void notifyAccessibilityButtonVisibilityChanged(boolean z) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.notifyAccessibilityButtonVisibilityChanged", 4L, "shown=" + z);
        }
        this.mSecurityPolicy.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE");
        synchronized (this.mLock) {
            notifyAccessibilityButtonVisibilityChangedLocked(z);
        }
    }

    public boolean onGesture(AccessibilityGestureEvent accessibilityGestureEvent) {
        boolean notifyGestureLocked;
        synchronized (this.mLock) {
            notifyGestureLocked = notifyGestureLocked(accessibilityGestureEvent, false);
            if (!notifyGestureLocked) {
                notifyGestureLocked = notifyGestureLocked(accessibilityGestureEvent, true);
            }
        }
        return notifyGestureLocked;
    }

    public boolean sendMotionEventToListeningServices(MotionEvent motionEvent) {
        return scheduleNotifyMotionEvent(MotionEvent.obtain(motionEvent));
    }

    public boolean onTouchStateChanged(int i, int i2) {
        return scheduleNotifyTouchState(i, i2);
    }

    @Override // com.android.server.accessibility.SystemActionPerformer.SystemActionsChangedListener
    public void onSystemActionsChanged() {
        synchronized (this.mLock) {
            notifySystemActionsChangedLocked(getCurrentUserStateLocked());
        }
    }

    @VisibleForTesting
    public void notifySystemActionsChangedLocked(AccessibilityUserState accessibilityUserState) {
        for (int size = accessibilityUserState.mBoundServices.size() - 1; size >= 0; size--) {
            accessibilityUserState.mBoundServices.get(size).notifySystemActionsChangedLocked();
        }
    }

    @VisibleForTesting
    public boolean notifyKeyEvent(KeyEvent keyEvent, int i) {
        synchronized (this.mLock) {
            ArrayList<AccessibilityServiceConnection> arrayList = getCurrentUserStateLocked().mBoundServices;
            if (arrayList.isEmpty()) {
                return false;
            }
            return getKeyEventDispatcher().notifyKeyEventLocked(keyEvent, i, arrayList);
        }
    }

    public void notifyMagnificationChanged(int i, Region region, MagnificationConfig magnificationConfig) {
        synchronized (this.mLock) {
            notifyClearAccessibilityCacheLocked();
            notifyMagnificationChangedLocked(i, region, magnificationConfig);
        }
    }

    public void setMotionEventInjectors(SparseArray<MotionEventInjector> sparseArray) {
        synchronized (this.mLock) {
            this.mMotionEventInjectors = sparseArray;
            this.mLock.notifyAll();
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public MotionEventInjector getMotionEventInjectorForDisplayLocked(int i) {
        long uptimeMillis = SystemClock.uptimeMillis() + 1000;
        while (this.mMotionEventInjectors == null && SystemClock.uptimeMillis() < uptimeMillis) {
            try {
                this.mLock.wait(uptimeMillis - SystemClock.uptimeMillis());
            } catch (InterruptedException unused) {
            }
        }
        SparseArray<MotionEventInjector> sparseArray = this.mMotionEventInjectors;
        if (sparseArray == null) {
            Slog.e("AccessibilityManagerService", "MotionEventInjector installation timed out");
            return null;
        }
        return sparseArray.get(i);
    }

    public boolean getAccessibilityFocusClickPointInScreen(Point point) {
        return getInteractionBridge().getAccessibilityFocusClickPointInScreenNotLocked(point);
    }

    public boolean performActionOnAccessibilityFocusedItem(AccessibilityNodeInfo.AccessibilityAction accessibilityAction) {
        return getInteractionBridge().performActionOnAccessibilityFocusedItemNotLocked(accessibilityAction);
    }

    public boolean accessibilityFocusOnlyInActiveWindow() {
        boolean accessibilityFocusOnlyInActiveWindowLocked;
        synchronized (this.mLock) {
            accessibilityFocusOnlyInActiveWindowLocked = this.mA11yWindowManager.accessibilityFocusOnlyInActiveWindowLocked();
        }
        return accessibilityFocusOnlyInActiveWindowLocked;
    }

    public boolean getWindowBounds(int i, Rect rect) {
        IBinder windowToken;
        synchronized (this.mLock) {
            windowToken = getWindowToken(i, this.mCurrentUserId);
        }
        if (this.mTraceManager.isA11yTracingEnabledForTypes(512L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("WindowManagerInternal.getWindowFrame", 512L, "token=" + windowToken + ";outBounds=" + rect);
        }
        this.mWindowManagerService.getWindowFrame(windowToken, rect);
        return !rect.isEmpty();
    }

    public int getActiveWindowId() {
        return this.mA11yWindowManager.getActiveWindowId(this.mCurrentUserId);
    }

    public void onTouchInteractionStart() {
        this.mA11yWindowManager.onTouchInteractionStart();
    }

    public void onTouchInteractionEnd() {
        this.mA11yWindowManager.onTouchInteractionEnd();
    }

    public final void switchUser(int i) {
        this.mMagnificationController.updateUserIdIfNeeded(i);
        synchronized (this.mLock) {
            if (this.mCurrentUserId == i && this.mInitialized) {
                return;
            }
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            currentUserStateLocked.onSwitchToAnotherUserLocked();
            if (currentUserStateLocked.mUserClients.getRegisteredCallbackCount() > 0) {
                this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda41
                    public final void accept(Object obj, Object obj2, Object obj3) {
                        ((AccessibilityManagerService) obj).sendStateToClients(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
                    }
                }, this, 0, Integer.valueOf(currentUserStateLocked.mUserId)));
            }
            boolean z = true;
            if (((UserManager) this.mContext.getSystemService("user")).getUsers().size() <= 1) {
                z = false;
            }
            this.mCurrentUserId = i;
            AccessibilityUserState currentUserStateLocked2 = getCurrentUserStateLocked();
            readConfigurationForUserStateLocked(currentUserStateLocked2);
            this.mSecurityPolicy.onSwitchUserLocked(this.mCurrentUserId, currentUserStateLocked2.mEnabledServices);
            onUserStateChangedLocked(currentUserStateLocked2);
            migrateAccessibilityButtonSettingsIfNecessaryLocked(currentUserStateLocked2, null, 0);
            if (z) {
                this.mMainHandler.sendMessageDelayed(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda42
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((AccessibilityManagerService) obj).announceNewUserIfNeeded();
                    }
                }, this), BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
            }
        }
    }

    public final void announceNewUserIfNeeded() {
        synchronized (this.mLock) {
            if (getCurrentUserStateLocked().isHandlingAccessibilityEventsLocked()) {
                String string = this.mContext.getString(17041705, ((UserManager) this.mContext.getSystemService("user")).getUserInfo(this.mCurrentUserId).name);
                AccessibilityEvent obtain = AccessibilityEvent.obtain(16384);
                obtain.getText().add(string);
                sendAccessibilityEventLocked(obtain, this.mCurrentUserId);
            }
        }
    }

    public final void unlockUser(int i) {
        synchronized (this.mLock) {
            int resolveProfileParentLocked = this.mSecurityPolicy.resolveProfileParentLocked(i);
            int i2 = this.mCurrentUserId;
            if (resolveProfileParentLocked == i2) {
                onUserStateChangedLocked(getUserStateLocked(i2));
            }
        }
    }

    public final void removeUser(int i) {
        synchronized (this.mLock) {
            this.mUserStates.remove(i);
        }
        getMagnificationController().onUserRemoved(i);
    }

    public void restoreEnabledAccessibilityServicesLocked(String str, String str2, int i) {
        readComponentNamesFromStringLocked(str, this.mTempComponentNameSet, false);
        readComponentNamesFromStringLocked(str2, this.mTempComponentNameSet, true);
        AccessibilityUserState userStateLocked = getUserStateLocked(0);
        userStateLocked.mEnabledServices.clear();
        userStateLocked.mEnabledServices.addAll(this.mTempComponentNameSet);
        persistComponentNamesToSettingLocked("enabled_accessibility_services", userStateLocked.mEnabledServices, 0);
        onUserStateChangedLocked(userStateLocked);
        migrateAccessibilityButtonSettingsIfNecessaryLocked(userStateLocked, null, i);
    }

    public void restoreAccessibilityButtonTargetsLocked(String str, String str2) {
        ArraySet arraySet = new ArraySet();
        readColonDelimitedStringToSet(str, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda36
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$restoreAccessibilityButtonTargetsLocked$2;
                lambda$restoreAccessibilityButtonTargetsLocked$2 = AccessibilityManagerService.lambda$restoreAccessibilityButtonTargetsLocked$2((String) obj);
                return lambda$restoreAccessibilityButtonTargetsLocked$2;
            }
        }, arraySet, false);
        readColonDelimitedStringToSet(str2, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda37
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$restoreAccessibilityButtonTargetsLocked$3;
                lambda$restoreAccessibilityButtonTargetsLocked$3 = AccessibilityManagerService.lambda$restoreAccessibilityButtonTargetsLocked$3((String) obj);
                return lambda$restoreAccessibilityButtonTargetsLocked$3;
            }
        }, arraySet, true);
        AccessibilityUserState userStateLocked = getUserStateLocked(0);
        userStateLocked.mAccessibilityButtonTargets.clear();
        userStateLocked.mAccessibilityButtonTargets.addAll((Collection<? extends String>) arraySet);
        persistColonDelimitedSetToSettingLocked("accessibility_button_targets", 0, userStateLocked.mAccessibilityButtonTargets, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda38
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$restoreAccessibilityButtonTargetsLocked$4;
                lambda$restoreAccessibilityButtonTargetsLocked$4 = AccessibilityManagerService.lambda$restoreAccessibilityButtonTargetsLocked$4((String) obj);
                return lambda$restoreAccessibilityButtonTargetsLocked$4;
            }
        });
        scheduleNotifyClientsOfServicesStateChangeLocked(userStateLocked);
        onUserStateChangedLocked(userStateLocked);
    }

    public final int getClientStateLocked(AccessibilityUserState accessibilityUserState) {
        return accessibilityUserState.getClientStateLocked(this.mUiAutomationManager.isUiAutomationRunningLocked(), this.mTraceManager.getTraceStateForAccessibilityManagerClientState());
    }

    public final InteractionBridge getInteractionBridge() {
        InteractionBridge interactionBridge;
        synchronized (this.mLock) {
            if (this.mInteractionBridge == null) {
                this.mInteractionBridge = new InteractionBridge();
            }
            interactionBridge = this.mInteractionBridge;
        }
        return interactionBridge;
    }

    public final boolean notifyGestureLocked(AccessibilityGestureEvent accessibilityGestureEvent, boolean z) {
        AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
        for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
            AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(size);
            if (accessibilityServiceConnection.mRequestTouchExplorationMode && accessibilityServiceConnection.mIsDefault == z) {
                accessibilityServiceConnection.notifyGesture(accessibilityGestureEvent);
                return true;
            }
        }
        return false;
    }

    public final boolean scheduleNotifyMotionEvent(MotionEvent motionEvent) {
        boolean z;
        int displayId = motionEvent.getDisplayId();
        synchronized (this.mLock) {
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            z = false;
            for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
                AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(size);
                if (accessibilityServiceConnection.wantsGenericMotionEvent(motionEvent) || (motionEvent.isFromSource(4098) && accessibilityServiceConnection.isServiceDetectsGesturesEnabled(displayId))) {
                    accessibilityServiceConnection.notifyMotionEvent(motionEvent);
                    z = true;
                }
            }
        }
        return z;
    }

    public final boolean scheduleNotifyTouchState(int i, int i2) {
        boolean z;
        synchronized (this.mLock) {
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            z = false;
            for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
                AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(size);
                if (accessibilityServiceConnection.isServiceDetectsGesturesEnabled(i)) {
                    accessibilityServiceConnection.notifyTouchState(i, i2);
                    z = true;
                }
            }
        }
        return z;
    }

    public final void notifyClearAccessibilityCacheLocked() {
        AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
        for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
            currentUserStateLocked.mBoundServices.get(size).notifyClearAccessibilityNodeInfoCache();
        }
        this.mProxyManager.clearCacheLocked();
    }

    public final void notifyMagnificationChangedLocked(int i, Region region, MagnificationConfig magnificationConfig) {
        AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
        for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
            currentUserStateLocked.mBoundServices.get(size).notifyMagnificationChangedLocked(i, region, magnificationConfig);
        }
    }

    public final void sendAccessibilityButtonToInputFilter(int i) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            if (this.mHasInputFilter && (accessibilityInputFilter = this.mInputFilter) != null) {
                accessibilityInputFilter.notifyAccessibilityButtonClicked(i);
            }
        }
    }

    public final void showAccessibilityTargetsSelection(int i, int i2) {
        String name;
        Intent intent = new Intent("com.android.internal.intent.action.CHOOSE_ACCESSIBILITY_BUTTON");
        if (i2 == 1) {
            name = AccessibilityShortcutChooserActivity.class.getName();
        } else {
            name = AccessibilityButtonChooserActivity.class.getName();
        }
        intent.setClassName(PackageManagerShellCommandDataLoader.PACKAGE, name);
        intent.addFlags(268468224);
        this.mContext.startActivityAsUser(intent, ActivityOptions.makeBasic().setLaunchDisplayId(i).toBundle(), UserHandle.of(this.mCurrentUserId));
    }

    public final void launchShortcutTargetActivity(int i, ComponentName componentName) {
        Intent intent = new Intent();
        Bundle bundle = ActivityOptions.makeBasic().setLaunchDisplayId(i).toBundle();
        intent.setComponent(componentName);
        intent.addFlags(268435456);
        try {
            this.mContext.startActivityAsUser(intent, bundle, UserHandle.of(this.mCurrentUserId));
        } catch (ActivityNotFoundException unused) {
        }
    }

    public final void launchAccessibilitySubSettings(int i, ComponentName componentName) {
        Intent intent = new Intent("android.settings.ACCESSIBILITY_DETAILS_SETTINGS");
        Bundle bundle = ActivityOptions.makeBasic().setLaunchDisplayId(i).toBundle();
        intent.addFlags(268435456);
        intent.putExtra("android.intent.extra.COMPONENT_NAME", componentName.flattenToString());
        try {
            this.mContext.startActivityAsUser(intent, bundle, UserHandle.of(this.mCurrentUserId));
        } catch (ActivityNotFoundException unused) {
        }
    }

    public final void notifyAccessibilityButtonVisibilityChangedLocked(boolean z) {
        AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
        this.mIsAccessibilityButtonShown = z;
        for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
            AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(size);
            if (accessibilityServiceConnection.mRequestAccessibilityButton) {
                accessibilityServiceConnection.notifyAccessibilityButtonAvailabilityChangedLocked(accessibilityServiceConnection.isAccessibilityButtonAvailableLocked(currentUserStateLocked));
            }
        }
    }

    public final boolean readInstalledAccessibilityServiceLocked(AccessibilityUserState accessibilityUserState) {
        this.mTempAccessibilityServiceInfoList.clear();
        List queryIntentServicesAsUser = this.mPackageManager.queryIntentServicesAsUser(new Intent("android.accessibilityservice.AccessibilityService"), accessibilityUserState.getBindInstantServiceAllowedLocked() ? 9207940 : 819332, this.mCurrentUserId);
        int size = queryIntentServicesAsUser.size();
        for (int i = 0; i < size; i++) {
            ResolveInfo resolveInfo = (ResolveInfo) queryIntentServicesAsUser.get(i);
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (this.mSecurityPolicy.canRegisterService(serviceInfo)) {
                try {
                    AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo(resolveInfo, this.mContext);
                    if (!accessibilityServiceInfo.isWithinParcelableSize()) {
                        Slog.e("AccessibilityManagerService", "Skipping service " + accessibilityServiceInfo.getResolveInfo().getComponentInfo() + " because service info size is larger than safe parcelable limits.");
                    } else {
                        if (accessibilityUserState.mCrashedServices.contains(serviceInfo.getComponentName())) {
                            accessibilityServiceInfo.crashed = true;
                        }
                        this.mTempAccessibilityServiceInfoList.add(accessibilityServiceInfo);
                    }
                } catch (IOException | XmlPullParserException e) {
                    Slog.e("AccessibilityManagerService", "Error while initializing AccessibilityServiceInfo", e);
                }
            }
        }
        if (!this.mTempAccessibilityServiceInfoList.equals(accessibilityUserState.mInstalledServices)) {
            accessibilityUserState.mInstalledServices.clear();
            accessibilityUserState.mInstalledServices.addAll(this.mTempAccessibilityServiceInfoList);
            this.mTempAccessibilityServiceInfoList.clear();
            return true;
        }
        this.mTempAccessibilityServiceInfoList.clear();
        return false;
    }

    public final boolean readInstalledAccessibilityShortcutLocked(AccessibilityUserState accessibilityUserState) {
        List installedAccessibilityShortcutListAsUser = AccessibilityManager.getInstance(this.mContext).getInstalledAccessibilityShortcutListAsUser(this.mContext, this.mCurrentUserId);
        if (installedAccessibilityShortcutListAsUser.equals(accessibilityUserState.mInstalledShortcuts)) {
            return false;
        }
        accessibilityUserState.mInstalledShortcuts.clear();
        accessibilityUserState.mInstalledShortcuts.addAll(installedAccessibilityShortcutListAsUser);
        return true;
    }

    public final boolean readEnabledAccessibilityServicesLocked(AccessibilityUserState accessibilityUserState) {
        this.mTempComponentNameSet.clear();
        readComponentNamesFromSettingLocked("enabled_accessibility_services", accessibilityUserState.mUserId, this.mTempComponentNameSet);
        if (!this.mTempComponentNameSet.equals(accessibilityUserState.mEnabledServices)) {
            accessibilityUserState.mEnabledServices.clear();
            accessibilityUserState.mEnabledServices.addAll(this.mTempComponentNameSet);
            this.mTempComponentNameSet.clear();
            return true;
        }
        this.mTempComponentNameSet.clear();
        return false;
    }

    public final boolean readTouchExplorationGrantedAccessibilityServicesLocked(AccessibilityUserState accessibilityUserState) {
        this.mTempComponentNameSet.clear();
        readComponentNamesFromSettingLocked("touch_exploration_granted_accessibility_services", accessibilityUserState.mUserId, this.mTempComponentNameSet);
        if (!this.mTempComponentNameSet.equals(accessibilityUserState.mTouchExplorationGrantedServices)) {
            accessibilityUserState.mTouchExplorationGrantedServices.clear();
            accessibilityUserState.mTouchExplorationGrantedServices.addAll(this.mTempComponentNameSet);
            this.mTempComponentNameSet.clear();
            return true;
        }
        this.mTempComponentNameSet.clear();
        return false;
    }

    public final void notifyAccessibilityServicesDelayedLocked(AccessibilityEvent accessibilityEvent, boolean z) {
        try {
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            int size = currentUserStateLocked.mBoundServices.size();
            for (int i = 0; i < size; i++) {
                AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(i);
                if (accessibilityServiceConnection.mIsDefault == z) {
                    accessibilityServiceConnection.notifyAccessibilityEvent(accessibilityEvent);
                }
            }
        } catch (IndexOutOfBoundsException unused) {
        }
    }

    public final void updateRelevantEventsLocked(final AccessibilityUserState accessibilityUserState) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(2L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.updateRelevantEventsLocked", 2L, "userState=" + accessibilityUserState);
        }
        this.mMainHandler.post(new Runnable() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                AccessibilityManagerService.this.lambda$updateRelevantEventsLocked$6(accessibilityUserState);
            }
        });
    }

    public /* synthetic */ void lambda$updateRelevantEventsLocked$6(final AccessibilityUserState accessibilityUserState) {
        broadcastToClients(accessibilityUserState, FunctionalUtils.ignoreRemoteException(new FunctionalUtils.RemoteExceptionIgnoringConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda47
            public final void acceptOrThrow(Object obj) {
                AccessibilityManagerService.this.lambda$updateRelevantEventsLocked$5(accessibilityUserState, (AccessibilityManagerService.Client) obj);
            }
        }));
    }

    public /* synthetic */ void lambda$updateRelevantEventsLocked$5(AccessibilityUserState accessibilityUserState, Client client) throws RemoteException {
        int computeRelevantEventTypesLocked;
        boolean z;
        synchronized (this.mLock) {
            computeRelevantEventTypesLocked = computeRelevantEventTypesLocked(accessibilityUserState, client);
            if (client.mLastSentRelevantEventTypes != computeRelevantEventTypesLocked) {
                client.mLastSentRelevantEventTypes = computeRelevantEventTypesLocked;
                z = true;
            } else {
                z = false;
            }
        }
        if (z) {
            client.mCallback.setRelevantEventTypes(computeRelevantEventTypesLocked);
        }
    }

    public final int computeRelevantEventTypesLocked(AccessibilityUserState accessibilityUserState, Client client) {
        int size = accessibilityUserState.mBoundServices.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            AccessibilityServiceConnection accessibilityServiceConnection = accessibilityUserState.mBoundServices.get(i2);
            i |= isClientInPackageAllowlist(accessibilityServiceConnection.getServiceInfo(), client) ? accessibilityServiceConnection.getRelevantEventTypes() : 0;
        }
        return this.mProxyManager.getRelevantEventTypesLocked() | i | (isClientInPackageAllowlist(this.mUiAutomationManager.getServiceInfo(), client) ? this.mUiAutomationManager.getRelevantEventTypes() : 0);
    }

    public final void updateMagnificationModeChangeSettingsLocked(AccessibilityUserState accessibilityUserState, int i) {
        if (accessibilityUserState.mUserId == this.mCurrentUserId && !fallBackMagnificationModeSettingsLocked(accessibilityUserState, i)) {
            this.mMagnificationController.transitionMagnificationModeLocked(i, accessibilityUserState.getMagnificationModeLocked(i), new MagnificationController.TransitionCallBack() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda9
                @Override // com.android.server.accessibility.magnification.MagnificationController.TransitionCallBack
                public final void onResult(int i2, boolean z) {
                    AccessibilityManagerService.this.onMagnificationTransitionEndedLocked(i2, z);
                }
            });
        }
    }

    public void onMagnificationTransitionEndedLocked(int i, boolean z) {
        AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
        int magnificationModeLocked = currentUserStateLocked.getMagnificationModeLocked(i) ^ 3;
        if (!z && magnificationModeLocked != 0) {
            currentUserStateLocked.setMagnificationModeLocked(i, magnificationModeLocked);
            if (i == 0) {
                persistMagnificationModeSettingsLocked(magnificationModeLocked);
                return;
            }
            return;
        }
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda50
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).notifyRefreshMagnificationModeToInputFilter(((Integer) obj2).intValue());
            }
        }, this, Integer.valueOf(i)));
    }

    public final void notifyRefreshMagnificationModeToInputFilter(int i) {
        synchronized (this.mLock) {
            if (this.mHasInputFilter) {
                ArrayList<Display> validDisplayList = getValidDisplayList();
                for (int i2 = 0; i2 < validDisplayList.size(); i2++) {
                    Display display = validDisplayList.get(i2);
                    if (display != null && display.getDisplayId() == i) {
                        this.mInputFilter.refreshMagnificationMode(display);
                        return;
                    }
                }
            }
        }
    }

    public static boolean isClientInPackageAllowlist(AccessibilityServiceInfo accessibilityServiceInfo, Client client) {
        if (accessibilityServiceInfo == null) {
            return false;
        }
        String[] strArr = client.mPackageNames;
        boolean isEmpty = ArrayUtils.isEmpty(accessibilityServiceInfo.packageNames);
        if (isEmpty || strArr == null) {
            return isEmpty;
        }
        for (String str : strArr) {
            if (ArrayUtils.contains(accessibilityServiceInfo.packageNames, str)) {
                return true;
            }
        }
        return isEmpty;
    }

    public final void broadcastToClients(AccessibilityUserState accessibilityUserState, Consumer<Client> consumer) {
        this.mGlobalClients.broadcastForEachCookie(consumer);
        accessibilityUserState.mUserClients.broadcastForEachCookie(consumer);
    }

    public final void readComponentNamesFromSettingLocked(String str, int i, Set<ComponentName> set) {
        readColonDelimitedSettingToSet(str, i, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda51
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ComponentName unflattenFromString;
                unflattenFromString = ComponentName.unflattenFromString((String) obj);
                return unflattenFromString;
            }
        }, set);
    }

    public final void readComponentNamesFromStringLocked(String str, Set<ComponentName> set, boolean z) {
        readColonDelimitedStringToSet(str, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda35
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ComponentName unflattenFromString;
                unflattenFromString = ComponentName.unflattenFromString((String) obj);
                return unflattenFromString;
            }
        }, set, z);
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void persistComponentNamesToSettingLocked(String str, Set<ComponentName> set, int i) {
        persistColonDelimitedSetToSettingLocked(str, i, set, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda13
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String flattenToShortString;
                flattenToShortString = ((ComponentName) obj).flattenToShortString();
                return flattenToShortString;
            }
        });
    }

    public final <T> void readColonDelimitedSettingToSet(String str, int i, Function<String, T> function, Set<T> set) {
        readColonDelimitedStringToSet(Settings.Secure.getStringForUser(this.mContext.getContentResolver(), str, i), function, set, false);
    }

    public final <T> void readColonDelimitedStringToSet(String str, Function<String, T> function, Set<T> set, boolean z) {
        T apply;
        if (!z) {
            set.clear();
        }
        if (TextUtils.isEmpty(str)) {
            return;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = this.mStringColonSplitter;
        simpleStringSplitter.setString(str);
        while (simpleStringSplitter.hasNext()) {
            String next = simpleStringSplitter.next();
            if (!TextUtils.isEmpty(next) && (apply = function.apply(next)) != null) {
                set.add(apply);
            }
        }
    }

    public final <T> void persistColonDelimitedSetToSettingLocked(String str, int i, Set<T> set, Function<T, String> function) {
        String str2;
        StringBuilder sb = new StringBuilder();
        Iterator<T> it = set.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            T next = it.next();
            str2 = next != null ? function.apply(next) : null;
            if (!TextUtils.isEmpty(str2)) {
                if (sb.length() > 0) {
                    sb.append(':');
                }
                sb.append(str2);
            }
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            String sb2 = sb.toString();
            ContentResolver contentResolver = this.mContext.getContentResolver();
            if (!TextUtils.isEmpty(sb2)) {
                str2 = sb2;
            }
            Settings.Secure.putStringForUser(contentResolver, str, str2, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:69:0x00c9, code lost:
        if (r15.mBoundServices.contains(r0) != false) goto L22;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updateServicesLocked(AccessibilityUserState accessibilityUserState) {
        int i;
        int i2;
        Map<ComponentName, AccessibilityServiceConnection> map;
        AccessibilityUserState accessibilityUserState2;
        AccessibilityManagerService accessibilityManagerService;
        AccessibilityUserState accessibilityUserState3;
        AccessibilityManagerService accessibilityManagerService2 = this;
        AccessibilityUserState accessibilityUserState4 = accessibilityUserState;
        Map<ComponentName, AccessibilityServiceConnection> map2 = accessibilityUserState4.mComponentNameToServiceMap;
        boolean isUserUnlockingOrUnlocked = ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).isUserUnlockingOrUnlocked(accessibilityUserState4.mUserId);
        int size = accessibilityUserState4.mInstalledServices.size();
        int i3 = 0;
        while (i3 < size) {
            AccessibilityServiceInfo accessibilityServiceInfo = accessibilityUserState4.mInstalledServices.get(i3);
            ComponentName unflattenFromString = ComponentName.unflattenFromString(accessibilityServiceInfo.getId());
            AccessibilityServiceConnection accessibilityServiceConnection = map2.get(unflattenFromString);
            if (!isUserUnlockingOrUnlocked && !accessibilityServiceInfo.isDirectBootAware()) {
                Slog.d("AccessibilityManagerService", "Ignoring non-encryption-aware service " + unflattenFromString);
            } else if (!accessibilityUserState.getBindingServicesLocked().contains(unflattenFromString) && !accessibilityUserState.getCrashedServicesLocked().contains(unflattenFromString)) {
                if (!accessibilityUserState4.mEnabledServices.contains(unflattenFromString) || accessibilityManagerService2.mUiAutomationManager.suppressingAccessibilityServicesLocked()) {
                    i = i3;
                    i2 = size;
                    map = map2;
                    accessibilityUserState2 = accessibilityUserState4;
                    if (accessibilityServiceConnection != null) {
                        accessibilityServiceConnection.unbindLocked();
                        accessibilityManagerService = this;
                        accessibilityUserState3 = accessibilityUserState2;
                        accessibilityManagerService.removeShortcutTargetForUnboundServiceLocked(accessibilityUserState3, accessibilityServiceConnection);
                        i3 = i + 1;
                        accessibilityManagerService2 = accessibilityManagerService;
                        accessibilityUserState4 = accessibilityUserState3;
                        map2 = map;
                        size = i2;
                    }
                } else {
                    if (accessibilityServiceConnection == null) {
                        Context context = accessibilityManagerService2.mContext;
                        int i4 = sIdCounter;
                        sIdCounter = i4 + 1;
                        Handler handler = accessibilityManagerService2.mMainHandler;
                        Object obj = accessibilityManagerService2.mLock;
                        AccessibilitySecurityPolicy accessibilitySecurityPolicy = accessibilityManagerService2.mSecurityPolicy;
                        AccessibilityTraceManager traceManager = getTraceManager();
                        WindowManagerInternal windowManagerInternal = accessibilityManagerService2.mWindowManagerService;
                        SystemActionPerformer systemActionPerformer = getSystemActionPerformer();
                        AccessibilityWindowManager accessibilityWindowManager = accessibilityManagerService2.mA11yWindowManager;
                        ActivityTaskManagerInternal activityTaskManagerInternal = accessibilityManagerService2.mActivityTaskManagerService;
                        i = i3;
                        i2 = size;
                        map = map2;
                        accessibilityUserState2 = accessibilityUserState4;
                        accessibilityServiceConnection = new AccessibilityServiceConnection(accessibilityUserState, context, unflattenFromString, accessibilityServiceInfo, i4, handler, obj, accessibilitySecurityPolicy, this, traceManager, windowManagerInternal, systemActionPerformer, accessibilityWindowManager, activityTaskManagerInternal);
                    } else {
                        i = i3;
                        i2 = size;
                        map = map2;
                        accessibilityUserState2 = accessibilityUserState4;
                    }
                    accessibilityServiceConnection.bindLocked();
                }
                accessibilityManagerService = this;
                accessibilityUserState3 = accessibilityUserState2;
                i3 = i + 1;
                accessibilityManagerService2 = accessibilityManagerService;
                accessibilityUserState4 = accessibilityUserState3;
                map2 = map;
                size = i2;
            }
            i = i3;
            i2 = size;
            map = map2;
            accessibilityUserState3 = accessibilityUserState4;
            accessibilityManagerService = accessibilityManagerService2;
            i3 = i + 1;
            accessibilityManagerService2 = accessibilityManagerService;
            accessibilityUserState4 = accessibilityUserState3;
            map2 = map;
            size = i2;
        }
        AccessibilityUserState accessibilityUserState5 = accessibilityUserState4;
        AccessibilityManagerService accessibilityManagerService3 = accessibilityManagerService2;
        int size2 = accessibilityUserState5.mBoundServices.size();
        accessibilityManagerService3.mTempIntArray.clear();
        for (int i5 = 0; i5 < size2; i5++) {
            ResolveInfo resolveInfo = accessibilityUserState5.mBoundServices.get(i5).mAccessibilityServiceInfo.getResolveInfo();
            if (resolveInfo != null) {
                accessibilityManagerService3.mTempIntArray.add(resolveInfo.serviceInfo.applicationInfo.uid);
            }
        }
        AudioManagerInternal audioManagerInternal = (AudioManagerInternal) LocalServices.getService(AudioManagerInternal.class);
        if (audioManagerInternal != null) {
            audioManagerInternal.setAccessibilityServiceUids(accessibilityManagerService3.mTempIntArray);
        }
        accessibilityManagerService3.mActivityTaskManagerService.setAccessibilityServiceUids(accessibilityManagerService3.mTempIntArray);
        updateAccessibilityEnabledSettingLocked(accessibilityUserState);
    }

    public void scheduleUpdateClientsIfNeededLocked(AccessibilityUserState accessibilityUserState) {
        int clientStateLocked = getClientStateLocked(accessibilityUserState);
        int stateLocked = this.mProxyManager.getStateLocked();
        if (accessibilityUserState.getLastSentClientStateLocked() == clientStateLocked && this.mProxyManager.getLastSentStateLocked() == stateLocked) {
            return;
        }
        if (this.mGlobalClients.getRegisteredCallbackCount() > 0 || accessibilityUserState.mUserClients.getRegisteredCallbackCount() > 0) {
            accessibilityUserState.setLastSentClientStateLocked(clientStateLocked);
            this.mProxyManager.setLastStateLocked(stateLocked);
            this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda33
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((AccessibilityManagerService) obj).sendStateToAllClients(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
                }
            }, this, Integer.valueOf(combineUserStateAndProxyState(clientStateLocked, stateLocked)), Integer.valueOf(accessibilityUserState.mUserId)));
        }
    }

    public final void sendStateToAllClients(int i, int i2) {
        sendStateToClients(i, this.mGlobalClients);
        sendStateToClients(i, i2);
    }

    public final void sendStateToClients(int i, int i2) {
        sendStateToClients(i, getUserState(i2).mUserClients);
    }

    public final void sendStateToClients(final int i, RemoteCallbackList<IAccessibilityManagerClient> remoteCallbackList) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(8L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.sendStateToClients", 8L, "clientState=" + i);
        }
        remoteCallbackList.broadcast(FunctionalUtils.ignoreRemoteException(new FunctionalUtils.RemoteExceptionIgnoringConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda48
            public final void acceptOrThrow(Object obj) {
                ((IAccessibilityManagerClient) obj).setState(i);
            }
        }));
    }

    public final void scheduleNotifyClientsOfServicesStateChangeLocked(AccessibilityUserState accessibilityUserState) {
        updateRecommendedUiTimeoutLocked(accessibilityUserState);
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda17
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((AccessibilityManagerService) obj).sendServicesStateChanged((RemoteCallbackList) obj2, ((Long) obj3).longValue());
            }
        }, this, accessibilityUserState.mUserClients, Long.valueOf(getRecommendedTimeoutMillisLocked(accessibilityUserState))));
    }

    public final void sendServicesStateChanged(RemoteCallbackList<IAccessibilityManagerClient> remoteCallbackList, long j) {
        notifyClientsOfServicesStateChange(this.mGlobalClients, j);
        notifyClientsOfServicesStateChange(remoteCallbackList, j);
    }

    public final void notifyClientsOfServicesStateChange(RemoteCallbackList<IAccessibilityManagerClient> remoteCallbackList, final long j) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(8L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.notifyClientsOfServicesStateChange", 8L, "uiTimeout=" + j);
        }
        remoteCallbackList.broadcast(FunctionalUtils.ignoreRemoteException(new FunctionalUtils.RemoteExceptionIgnoringConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda46
            public final void acceptOrThrow(Object obj) {
                ((IAccessibilityManagerClient) obj).notifyServicesStateChanged(j);
            }
        }));
    }

    public final void scheduleUpdateInputFilter(AccessibilityUserState accessibilityUserState) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda21
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).updateInputFilter((AccessibilityUserState) obj2);
            }
        }, this, accessibilityUserState));
    }

    public final void scheduleUpdateFingerprintGestureHandling(AccessibilityUserState accessibilityUserState) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda10
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).updateFingerprintGestureHandling((AccessibilityUserState) obj2);
            }
        }, this, accessibilityUserState));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void updateInputFilter(AccessibilityUserState accessibilityUserState) {
        boolean z;
        AccessibilityInputFilter accessibilityInputFilter;
        if (this.mUiAutomationManager.suppressingAccessibilityServicesLocked()) {
            return;
        }
        synchronized (this.mLock) {
            boolean isDisplayMagnificationEnabledLocked = accessibilityUserState.isDisplayMagnificationEnabledLocked();
            if (accessibilityUserState.isShortcutMagnificationEnabledLocked()) {
                isDisplayMagnificationEnabledLocked |= true;
            }
            boolean z2 = isDisplayMagnificationEnabledLocked;
            if (userHasMagnificationServicesLocked(accessibilityUserState)) {
                z2 = (isDisplayMagnificationEnabledLocked ? 1 : 0) | true;
            }
            boolean z3 = z2;
            if (accessibilityUserState.isHandlingAccessibilityEventsLocked()) {
                z3 = z2;
                if (accessibilityUserState.isTouchExplorationEnabledLocked()) {
                    boolean z4 = (z2 ? 1 : 0) | true;
                    boolean z5 = z4;
                    if (accessibilityUserState.isServiceHandlesDoubleTapEnabledLocked()) {
                        z5 = (z4 ? 1 : 0) | true;
                    }
                    boolean z6 = z5;
                    if (accessibilityUserState.isMultiFingerGesturesEnabledLocked()) {
                        z6 = (z5 ? 1 : 0) | true;
                    }
                    z3 = z6;
                    if (accessibilityUserState.isTwoFingerPassthroughEnabledLocked()) {
                        z3 = (z6 ? 1 : 0) | true;
                    }
                }
            }
            boolean z7 = z3;
            if (accessibilityUserState.isFilterKeyEventsEnabledLocked()) {
                z7 = (z3 ? 1 : 0) | true;
            }
            boolean z8 = z7;
            if (accessibilityUserState.isSendMotionEventsEnabled()) {
                z8 = (z7 ? 1 : 0) | true;
            }
            boolean z9 = z8;
            if (accessibilityUserState.isAutoclickEnabledLocked()) {
                z9 = (z8 ? 1 : 0) | true;
            }
            int i = z9;
            if (accessibilityUserState.isPerformGesturesEnabledLocked()) {
                i = (z9 ? 1 : 0) | true;
            }
            Iterator<AccessibilityServiceConnection> it = accessibilityUserState.mBoundServices.iterator();
            z = false;
            int i2 = 0;
            while (it.hasNext()) {
                i2 |= it.next().mGenericMotionEventSources;
            }
            if (i2 != 0) {
                i = (i == true ? 1 : 0) | 2048;
            }
            accessibilityInputFilter = null;
            if (i != 0) {
                if (!this.mHasInputFilter) {
                    this.mHasInputFilter = true;
                    if (this.mInputFilter == null) {
                        this.mInputFilter = new AccessibilityInputFilter(this.mContext, this);
                    }
                    accessibilityInputFilter = this.mInputFilter;
                    this.mProxyManager.setAccessibilityInputFilter(accessibilityInputFilter);
                    z = true;
                }
                this.mInputFilter.setUserAndEnabledFeatures(accessibilityUserState.mUserId, i);
                this.mInputFilter.setCombinedGenericMotionEventSources(i2);
            } else if (this.mHasInputFilter) {
                this.mHasInputFilter = false;
                this.mInputFilter.setUserAndEnabledFeatures(accessibilityUserState.mUserId, 0);
                this.mInputFilter.resetServiceDetectsGestures();
                if (accessibilityUserState.isTouchExplorationEnabledLocked()) {
                    Iterator<Display> it2 = getValidDisplayList().iterator();
                    while (it2.hasNext()) {
                        int displayId = it2.next().getDisplayId();
                        this.mInputFilter.setServiceDetectsGesturesEnabled(displayId, accessibilityUserState.isServiceDetectsGesturesEnabled(displayId));
                    }
                }
                z = true;
            }
        }
        if (z) {
            if (this.mTraceManager.isA11yTracingEnabledForTypes(4608L)) {
                this.mTraceManager.logTrace("WindowManagerInternal.setInputFilter", 4608L, "inputFilter=" + accessibilityInputFilter);
            }
            this.mWindowManagerService.setInputFilter(accessibilityInputFilter);
        }
    }

    public final void showEnableTouchExplorationDialog(final AccessibilityServiceConnection accessibilityServiceConnection) {
        synchronized (this.mLock) {
            String charSequence = accessibilityServiceConnection.getServiceInfo().getResolveInfo().loadLabel(this.mContext.getPackageManager()).toString();
            final AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            if (currentUserStateLocked.isTouchExplorationEnabledLocked()) {
                return;
            }
            AlertDialog alertDialog = this.mEnableTouchExplorationDialog;
            if (alertDialog == null || !alertDialog.isShowing()) {
                AlertDialog create = new AlertDialog.Builder(this.mContext).setIconAttribute(16843605).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.server.accessibility.AccessibilityManagerService.6
                    {
                        AccessibilityManagerService.this = this;
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currentUserStateLocked.mTouchExplorationGrantedServices.add(accessibilityServiceConnection.mComponentName);
                        AccessibilityManagerService accessibilityManagerService = AccessibilityManagerService.this;
                        AccessibilityUserState accessibilityUserState = currentUserStateLocked;
                        accessibilityManagerService.persistComponentNamesToSettingLocked("touch_exploration_granted_accessibility_services", accessibilityUserState.mTouchExplorationGrantedServices, accessibilityUserState.mUserId);
                        currentUserStateLocked.setTouchExplorationEnabledLocked(true);
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        try {
                            Settings.Secure.putIntForUser(AccessibilityManagerService.this.mContext.getContentResolver(), "touch_exploration_enabled", 1, currentUserStateLocked.mUserId);
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                        } catch (Throwable th) {
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            throw th;
                        }
                    }
                }).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.server.accessibility.AccessibilityManagerService.5
                    {
                        AccessibilityManagerService.this = this;
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setTitle(17040183).setMessage(this.mContext.getString(17040182, charSequence)).create();
                this.mEnableTouchExplorationDialog = create;
                create.getWindow().setType(2003);
                this.mEnableTouchExplorationDialog.getWindow().getAttributes().privateFlags |= 16;
                this.mEnableTouchExplorationDialog.setCanceledOnTouchOutside(true);
                this.mEnableTouchExplorationDialog.show();
            }
        }
    }

    public final void onUserStateChangedLocked(AccessibilityUserState accessibilityUserState) {
        this.mInitialized = true;
        updateLegacyCapabilitiesLocked(accessibilityUserState);
        updateServicesLocked(accessibilityUserState);
        updateWindowsForAccessibilityCallbackLocked(accessibilityUserState);
        updateFilterKeyEventsLocked(accessibilityUserState);
        updateTouchExplorationLocked(accessibilityUserState);
        updatePerformGesturesLocked(accessibilityUserState);
        updateMagnificationLocked(accessibilityUserState);
        scheduleUpdateFingerprintGestureHandling(accessibilityUserState);
        scheduleUpdateInputFilter(accessibilityUserState);
        updateRelevantEventsLocked(accessibilityUserState);
        scheduleUpdateClientsIfNeededLocked(accessibilityUserState);
        updateAccessibilityShortcutKeyTargetsLocked(accessibilityUserState);
        updateAccessibilityButtonTargetsLocked(accessibilityUserState);
        updateMagnificationCapabilitiesSettingsChangeLocked(accessibilityUserState);
        updateMagnificationModeChangeSettingsForAllDisplaysLocked(accessibilityUserState);
        updateFocusAppearanceDataLocked(accessibilityUserState);
    }

    public final void updateMagnificationModeChangeSettingsForAllDisplaysLocked(AccessibilityUserState accessibilityUserState) {
        ArrayList<Display> validDisplayList = getValidDisplayList();
        for (int i = 0; i < validDisplayList.size(); i++) {
            updateMagnificationModeChangeSettingsLocked(accessibilityUserState, validDisplayList.get(i).getDisplayId());
        }
    }

    public final void updateWindowsForAccessibilityCallbackLocked(AccessibilityUserState accessibilityUserState) {
        boolean z = this.mUiAutomationManager.canRetrieveInteractiveWindowsLocked() || this.mProxyManager.canRetrieveInteractiveWindowsLocked();
        ArrayList<AccessibilityServiceConnection> arrayList = accessibilityUserState.mBoundServices;
        int size = arrayList.size();
        for (int i = 0; !z && i < size; i++) {
            if (arrayList.get(i).canRetrieveInteractiveWindowsLocked()) {
                accessibilityUserState.setAccessibilityFocusOnlyInActiveWindow(false);
                z = true;
            }
        }
        accessibilityUserState.setAccessibilityFocusOnlyInActiveWindow(true);
        ArrayList<Display> validDisplayList = getValidDisplayList();
        for (int i2 = 0; i2 < validDisplayList.size(); i2++) {
            Display display = validDisplayList.get(i2);
            if (display != null) {
                if (z) {
                    this.mA11yWindowManager.startTrackingWindows(display.getDisplayId(), this.mProxyManager.isProxyed(display.getDisplayId()));
                } else {
                    this.mA11yWindowManager.stopTrackingWindows(display.getDisplayId());
                }
            }
        }
    }

    public final void updateLegacyCapabilitiesLocked(AccessibilityUserState accessibilityUserState) {
        int size = accessibilityUserState.mInstalledServices.size();
        for (int i = 0; i < size; i++) {
            AccessibilityServiceInfo accessibilityServiceInfo = accessibilityUserState.mInstalledServices.get(i);
            ResolveInfo resolveInfo = accessibilityServiceInfo.getResolveInfo();
            if ((accessibilityServiceInfo.getCapabilities() & 2) == 0 && resolveInfo.serviceInfo.applicationInfo.targetSdkVersion <= 17) {
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                if (accessibilityUserState.mTouchExplorationGrantedServices.contains(new ComponentName(serviceInfo.packageName, serviceInfo.name))) {
                    accessibilityServiceInfo.setCapabilities(accessibilityServiceInfo.getCapabilities() | 2);
                }
            }
        }
    }

    public final void updatePerformGesturesLocked(AccessibilityUserState accessibilityUserState) {
        int size = accessibilityUserState.mBoundServices.size();
        for (int i = 0; i < size; i++) {
            if ((accessibilityUserState.mBoundServices.get(i).getCapabilities() & 32) != 0) {
                accessibilityUserState.setPerformGesturesEnabledLocked(true);
                return;
            }
        }
        accessibilityUserState.setPerformGesturesEnabledLocked(false);
    }

    public final void updateFilterKeyEventsLocked(AccessibilityUserState accessibilityUserState) {
        int size = accessibilityUserState.mBoundServices.size();
        for (int i = 0; i < size; i++) {
            AccessibilityServiceConnection accessibilityServiceConnection = accessibilityUserState.mBoundServices.get(i);
            if (accessibilityServiceConnection.mRequestFilterKeyEvents && (accessibilityServiceConnection.getCapabilities() & 8) != 0) {
                accessibilityUserState.setFilterKeyEventsEnabledLocked(true);
                return;
            }
        }
        accessibilityUserState.setFilterKeyEventsEnabledLocked(false);
    }

    public final boolean readConfigurationForUserStateLocked(AccessibilityUserState accessibilityUserState) {
        return readAlwaysOnMagnificationLocked(accessibilityUserState) | readInstalledAccessibilityServiceLocked(accessibilityUserState) | readInstalledAccessibilityShortcutLocked(accessibilityUserState) | readEnabledAccessibilityServicesLocked(accessibilityUserState) | readTouchExplorationGrantedAccessibilityServicesLocked(accessibilityUserState) | readTouchExplorationEnabledSettingLocked(accessibilityUserState) | readHighTextContrastEnabledSettingLocked(accessibilityUserState) | readAudioDescriptionEnabledSettingLocked(accessibilityUserState) | readMagnificationEnabledSettingsLocked(accessibilityUserState) | readAutoclickEnabledSettingLocked(accessibilityUserState) | readAccessibilityShortcutKeySettingLocked(accessibilityUserState) | readAccessibilityButtonTargetsLocked(accessibilityUserState) | readAccessibilityButtonTargetComponentLocked(accessibilityUserState) | readUserRecommendedUiTimeoutSettingsLocked(accessibilityUserState) | readMagnificationModeForDefaultDisplayLocked(accessibilityUserState) | readMagnificationCapabilitiesLocked(accessibilityUserState) | readMagnificationFollowTypingLocked(accessibilityUserState);
    }

    public final void updateAccessibilityEnabledSettingLocked(AccessibilityUserState accessibilityUserState) {
        boolean z = this.mUiAutomationManager.isUiAutomationRunningLocked() || accessibilityUserState.isHandlingAccessibilityEventsLocked();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "accessibility_enabled", z ? 1 : 0, accessibilityUserState.mUserId);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean readTouchExplorationEnabledSettingLocked(AccessibilityUserState accessibilityUserState) {
        boolean z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "touch_exploration_enabled", 0, accessibilityUserState.mUserId) == 1;
        if (z != accessibilityUserState.isTouchExplorationEnabledLocked()) {
            accessibilityUserState.setTouchExplorationEnabledLocked(z);
            return true;
        }
        return false;
    }

    public final boolean readMagnificationEnabledSettingsLocked(AccessibilityUserState accessibilityUserState) {
        boolean z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_magnification_enabled", 0, accessibilityUserState.mUserId) == 1;
        if (z != accessibilityUserState.isDisplayMagnificationEnabledLocked()) {
            accessibilityUserState.setDisplayMagnificationEnabledLocked(z);
            return true;
        }
        return false;
    }

    public final boolean readAutoclickEnabledSettingLocked(AccessibilityUserState accessibilityUserState) {
        boolean z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_autoclick_enabled", 0, accessibilityUserState.mUserId) == 1;
        if (z != accessibilityUserState.isAutoclickEnabledLocked()) {
            accessibilityUserState.setAutoclickEnabledLocked(z);
            return true;
        }
        return false;
    }

    public final boolean readHighTextContrastEnabledSettingLocked(AccessibilityUserState accessibilityUserState) {
        boolean z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "high_text_contrast_enabled", 0, accessibilityUserState.mUserId) == 1;
        if (z != accessibilityUserState.isTextHighContrastEnabledLocked()) {
            accessibilityUserState.setTextHighContrastEnabledLocked(z);
            return true;
        }
        return false;
    }

    public final boolean readAudioDescriptionEnabledSettingLocked(AccessibilityUserState accessibilityUserState) {
        boolean z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "enabled_accessibility_audio_description_by_default", 0, accessibilityUserState.mUserId) == 1;
        if (z != accessibilityUserState.isAudioDescriptionByDefaultEnabledLocked()) {
            accessibilityUserState.setAudioDescriptionByDefaultEnabledLocked(z);
            return true;
        }
        return false;
    }

    public final void updateTouchExplorationLocked(AccessibilityUserState accessibilityUserState) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean isTouchExplorationEnabledLocked = this.mUiAutomationManager.isTouchExplorationEnabledLocked();
        int size = accessibilityUserState.mBoundServices.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                z = false;
                z2 = false;
                z3 = false;
                z4 = false;
                break;
            }
            AccessibilityServiceConnection accessibilityServiceConnection = accessibilityUserState.mBoundServices.get(i);
            if (canRequestAndRequestsTouchExplorationLocked(accessibilityServiceConnection, accessibilityUserState)) {
                boolean isServiceHandlesDoubleTapEnabled = accessibilityServiceConnection.isServiceHandlesDoubleTapEnabled();
                boolean isMultiFingerGesturesEnabled = accessibilityServiceConnection.isMultiFingerGesturesEnabled();
                boolean isTwoFingerPassthroughEnabled = accessibilityServiceConnection.isTwoFingerPassthroughEnabled();
                z4 = accessibilityServiceConnection.isSendMotionEventsEnabled();
                z3 = isTwoFingerPassthroughEnabled;
                z2 = isMultiFingerGesturesEnabled;
                z = isServiceHandlesDoubleTapEnabled;
                isTouchExplorationEnabledLocked = true;
                break;
            }
            i++;
        }
        if (isTouchExplorationEnabledLocked != accessibilityUserState.isTouchExplorationEnabledLocked()) {
            accessibilityUserState.setTouchExplorationEnabledLocked(isTouchExplorationEnabledLocked);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "touch_exploration_enabled", isTouchExplorationEnabledLocked ? 1 : 0, accessibilityUserState.mUserId);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        accessibilityUserState.resetServiceDetectsGestures();
        ArrayList<Display> validDisplayList = getValidDisplayList();
        Iterator<AccessibilityServiceConnection> it = accessibilityUserState.mBoundServices.iterator();
        while (it.hasNext()) {
            AccessibilityServiceConnection next = it.next();
            Iterator<Display> it2 = validDisplayList.iterator();
            while (it2.hasNext()) {
                int displayId = it2.next().getDisplayId();
                if (next.isServiceDetectsGesturesEnabled(displayId)) {
                    accessibilityUserState.setServiceDetectsGesturesEnabled(displayId, true);
                }
            }
        }
        accessibilityUserState.setServiceHandlesDoubleTapLocked(z);
        accessibilityUserState.setMultiFingerGesturesLocked(z2);
        accessibilityUserState.setTwoFingerPassthroughLocked(z3);
        accessibilityUserState.setSendMotionEventsEnabled(z4);
    }

    public final boolean readAccessibilityShortcutKeySettingLocked(AccessibilityUserState accessibilityUserState) {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "accessibility_shortcut_target_service", accessibilityUserState.mUserId);
        ArraySet arraySet = new ArraySet();
        readColonDelimitedStringToSet(stringForUser, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda49
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$readAccessibilityShortcutKeySettingLocked$12;
                lambda$readAccessibilityShortcutKeySettingLocked$12 = AccessibilityManagerService.lambda$readAccessibilityShortcutKeySettingLocked$12((String) obj);
                return lambda$readAccessibilityShortcutKeySettingLocked$12;
            }
        }, arraySet, false);
        if (stringForUser == null) {
            String string = this.mContext.getString(17039868);
            if (!TextUtils.isEmpty(string)) {
                arraySet.add(string);
            }
        }
        ArraySet<String> shortcutTargetsLocked = accessibilityUserState.getShortcutTargetsLocked(1);
        if (arraySet.equals(shortcutTargetsLocked)) {
            return false;
        }
        shortcutTargetsLocked.clear();
        shortcutTargetsLocked.addAll((Collection<? extends String>) arraySet);
        scheduleNotifyClientsOfServicesStateChangeLocked(accessibilityUserState);
        return true;
    }

    public final boolean readAccessibilityButtonTargetsLocked(AccessibilityUserState accessibilityUserState) {
        ArraySet arraySet = new ArraySet();
        readColonDelimitedSettingToSet("accessibility_button_targets", accessibilityUserState.mUserId, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda45
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$readAccessibilityButtonTargetsLocked$13;
                lambda$readAccessibilityButtonTargetsLocked$13 = AccessibilityManagerService.lambda$readAccessibilityButtonTargetsLocked$13((String) obj);
                return lambda$readAccessibilityButtonTargetsLocked$13;
            }
        }, arraySet);
        ArraySet<String> shortcutTargetsLocked = accessibilityUserState.getShortcutTargetsLocked(0);
        if (arraySet.equals(shortcutTargetsLocked)) {
            return false;
        }
        shortcutTargetsLocked.clear();
        shortcutTargetsLocked.addAll((Collection<? extends String>) arraySet);
        scheduleNotifyClientsOfServicesStateChangeLocked(accessibilityUserState);
        return true;
    }

    public final boolean readAccessibilityButtonTargetComponentLocked(AccessibilityUserState accessibilityUserState) {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "accessibility_button_target_component", accessibilityUserState.mUserId);
        if (TextUtils.isEmpty(stringForUser)) {
            if (accessibilityUserState.getTargetAssignedToAccessibilityButton() == null) {
                return false;
            }
            accessibilityUserState.setTargetAssignedToAccessibilityButton(null);
            return true;
        } else if (stringForUser.equals(accessibilityUserState.getTargetAssignedToAccessibilityButton())) {
            return false;
        } else {
            accessibilityUserState.setTargetAssignedToAccessibilityButton(stringForUser);
            return true;
        }
    }

    public final boolean readUserRecommendedUiTimeoutSettingsLocked(AccessibilityUserState accessibilityUserState) {
        int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_non_interactive_ui_timeout_ms", 0, accessibilityUserState.mUserId);
        int intForUser2 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_interactive_ui_timeout_ms", 0, accessibilityUserState.mUserId);
        if (intForUser == accessibilityUserState.getUserNonInteractiveUiTimeoutLocked() && intForUser2 == accessibilityUserState.getUserInteractiveUiTimeoutLocked()) {
            return false;
        }
        accessibilityUserState.setUserNonInteractiveUiTimeoutLocked(intForUser);
        accessibilityUserState.setUserInteractiveUiTimeoutLocked(intForUser2);
        scheduleNotifyClientsOfServicesStateChangeLocked(accessibilityUserState);
        return true;
    }

    public final void updateAccessibilityShortcutKeyTargetsLocked(final AccessibilityUserState accessibilityUserState) {
        ArraySet<String> shortcutTargetsLocked = accessibilityUserState.getShortcutTargetsLocked(1);
        int size = shortcutTargetsLocked.size();
        if (size == 0) {
            return;
        }
        shortcutTargetsLocked.removeIf(new Predicate() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda30
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateAccessibilityShortcutKeyTargetsLocked$14;
                lambda$updateAccessibilityShortcutKeyTargetsLocked$14 = AccessibilityManagerService.lambda$updateAccessibilityShortcutKeyTargetsLocked$14(AccessibilityUserState.this, (String) obj);
                return lambda$updateAccessibilityShortcutKeyTargetsLocked$14;
            }
        });
        if (size == shortcutTargetsLocked.size()) {
            return;
        }
        persistColonDelimitedSetToSettingLocked("accessibility_shortcut_target_service", accessibilityUserState.mUserId, shortcutTargetsLocked, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda31
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$updateAccessibilityShortcutKeyTargetsLocked$15;
                lambda$updateAccessibilityShortcutKeyTargetsLocked$15 = AccessibilityManagerService.lambda$updateAccessibilityShortcutKeyTargetsLocked$15((String) obj);
                return lambda$updateAccessibilityShortcutKeyTargetsLocked$15;
            }
        });
        scheduleNotifyClientsOfServicesStateChangeLocked(accessibilityUserState);
    }

    public static /* synthetic */ boolean lambda$updateAccessibilityShortcutKeyTargetsLocked$14(AccessibilityUserState accessibilityUserState, String str) {
        return !accessibilityUserState.isShortcutTargetInstalledLocked(str);
    }

    public final boolean canRequestAndRequestsTouchExplorationLocked(AccessibilityServiceConnection accessibilityServiceConnection, AccessibilityUserState accessibilityUserState) {
        if (accessibilityServiceConnection.canReceiveEventsLocked() && accessibilityServiceConnection.mRequestTouchExplorationMode) {
            if (accessibilityServiceConnection.getServiceInfo().getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion <= 17) {
                if (accessibilityUserState.mTouchExplorationGrantedServices.contains(accessibilityServiceConnection.mComponentName)) {
                    return true;
                }
                AlertDialog alertDialog = this.mEnableTouchExplorationDialog;
                if (alertDialog == null || !alertDialog.isShowing()) {
                    this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda25
                        @Override // java.util.function.BiConsumer
                        public final void accept(Object obj, Object obj2) {
                            ((AccessibilityManagerService) obj).showEnableTouchExplorationDialog((AccessibilityServiceConnection) obj2);
                        }
                    }, this, accessibilityServiceConnection));
                }
            } else if ((accessibilityServiceConnection.getCapabilities() & 2) != 0) {
                return true;
            }
        }
        return false;
    }

    public final void updateMagnificationLocked(AccessibilityUserState accessibilityUserState) {
        if (accessibilityUserState.mUserId != this.mCurrentUserId) {
            return;
        }
        if (this.mUiAutomationManager.suppressingAccessibilityServicesLocked() && this.mMagnificationController.isFullScreenMagnificationControllerInitialized()) {
            getMagnificationController().getFullScreenMagnificationController().unregisterAll();
            return;
        }
        ArrayList<Display> validDisplayList = getValidDisplayList();
        int i = 0;
        if (accessibilityUserState.isDisplayMagnificationEnabledLocked() || accessibilityUserState.isShortcutMagnificationEnabledLocked()) {
            while (i < validDisplayList.size()) {
                getMagnificationController().getFullScreenMagnificationController().register(validDisplayList.get(i).getDisplayId());
                i++;
            }
            return;
        }
        while (i < validDisplayList.size()) {
            int displayId = validDisplayList.get(i).getDisplayId();
            if (userHasListeningMagnificationServicesLocked(accessibilityUserState, displayId)) {
                getMagnificationController().getFullScreenMagnificationController().register(displayId);
            } else if (this.mMagnificationController.isFullScreenMagnificationControllerInitialized()) {
                getMagnificationController().getFullScreenMagnificationController().unregister(displayId);
            }
            i++;
        }
    }

    public final void updateWindowMagnificationConnectionIfNeeded(AccessibilityUserState accessibilityUserState) {
        if (this.mMagnificationController.supportWindowMagnification()) {
            boolean z = true;
            if (((!accessibilityUserState.isShortcutMagnificationEnabledLocked() && !accessibilityUserState.isDisplayMagnificationEnabledLocked()) || accessibilityUserState.getMagnificationCapabilitiesLocked() == 1) && !userHasMagnificationServicesLocked(accessibilityUserState)) {
                z = false;
            }
            getWindowMagnificationMgr().requestConnection(z);
        }
    }

    public final boolean userHasMagnificationServicesLocked(AccessibilityUserState accessibilityUserState) {
        ArrayList<AccessibilityServiceConnection> arrayList = accessibilityUserState.mBoundServices;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (this.mSecurityPolicy.canControlMagnification(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public final boolean userHasListeningMagnificationServicesLocked(AccessibilityUserState accessibilityUserState, int i) {
        ArrayList<AccessibilityServiceConnection> arrayList = accessibilityUserState.mBoundServices;
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            AccessibilityServiceConnection accessibilityServiceConnection = arrayList.get(i2);
            if (this.mSecurityPolicy.canControlMagnification(accessibilityServiceConnection) && accessibilityServiceConnection.isMagnificationCallbackEnabled(i)) {
                return true;
            }
        }
        return false;
    }

    public final void updateFingerprintGestureHandling(AccessibilityUserState accessibilityUserState) {
        ArrayList<AccessibilityServiceConnection> arrayList;
        synchronized (this.mLock) {
            arrayList = accessibilityUserState.mBoundServices;
            if (this.mFingerprintGestureDispatcher == null && this.mPackageManager.hasSystemFeature("android.hardware.fingerprint")) {
                int size = arrayList.size();
                int i = 0;
                while (true) {
                    if (i >= size) {
                        break;
                    }
                    if (arrayList.get(i).isCapturingFingerprintGestures()) {
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        IFingerprintService asInterface = IFingerprintService.Stub.asInterface(ServiceManager.getService("fingerprint"));
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        if (asInterface != null) {
                            this.mFingerprintGestureDispatcher = new FingerprintGestureDispatcher(asInterface, this.mContext.getResources(), this.mLock);
                            break;
                        }
                    }
                    i++;
                }
            }
        }
        FingerprintGestureDispatcher fingerprintGestureDispatcher = this.mFingerprintGestureDispatcher;
        if (fingerprintGestureDispatcher != null) {
            fingerprintGestureDispatcher.updateClientList(arrayList);
        }
    }

    public final void updateAccessibilityButtonTargetsLocked(final AccessibilityUserState accessibilityUserState) {
        for (int size = accessibilityUserState.mBoundServices.size() - 1; size >= 0; size--) {
            AccessibilityServiceConnection accessibilityServiceConnection = accessibilityUserState.mBoundServices.get(size);
            if (accessibilityServiceConnection.mRequestAccessibilityButton) {
                accessibilityServiceConnection.notifyAccessibilityButtonAvailabilityChangedLocked(accessibilityServiceConnection.isAccessibilityButtonAvailableLocked(accessibilityUserState));
            }
        }
        ArraySet<String> shortcutTargetsLocked = accessibilityUserState.getShortcutTargetsLocked(0);
        int size2 = shortcutTargetsLocked.size();
        if (size2 == 0) {
            return;
        }
        shortcutTargetsLocked.removeIf(new Predicate() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateAccessibilityButtonTargetsLocked$16;
                lambda$updateAccessibilityButtonTargetsLocked$16 = AccessibilityManagerService.lambda$updateAccessibilityButtonTargetsLocked$16(AccessibilityUserState.this, (String) obj);
                return lambda$updateAccessibilityButtonTargetsLocked$16;
            }
        });
        if (size2 == shortcutTargetsLocked.size()) {
            return;
        }
        persistColonDelimitedSetToSettingLocked("accessibility_button_targets", accessibilityUserState.mUserId, shortcutTargetsLocked, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$updateAccessibilityButtonTargetsLocked$17;
                lambda$updateAccessibilityButtonTargetsLocked$17 = AccessibilityManagerService.lambda$updateAccessibilityButtonTargetsLocked$17((String) obj);
                return lambda$updateAccessibilityButtonTargetsLocked$17;
            }
        });
        scheduleNotifyClientsOfServicesStateChangeLocked(accessibilityUserState);
    }

    public static /* synthetic */ boolean lambda$updateAccessibilityButtonTargetsLocked$16(AccessibilityUserState accessibilityUserState, String str) {
        return !accessibilityUserState.isShortcutTargetInstalledLocked(str);
    }

    public final void migrateAccessibilityButtonSettingsIfNecessaryLocked(final AccessibilityUserState accessibilityUserState, final String str, int i) {
        if (i > 29) {
            return;
        }
        final ArraySet<String> shortcutTargetsLocked = accessibilityUserState.getShortcutTargetsLocked(0);
        int size = shortcutTargetsLocked.size();
        shortcutTargetsLocked.removeIf(new Predicate() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda52
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$18;
                lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$18 = AccessibilityManagerService.lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$18(str, accessibilityUserState, (String) obj);
                return lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$18;
            }
        });
        boolean z = size != shortcutTargetsLocked.size();
        int size2 = shortcutTargetsLocked.size();
        final ArraySet<String> shortcutTargetsLocked2 = accessibilityUserState.getShortcutTargetsLocked(1);
        accessibilityUserState.mEnabledServices.forEach(new Consumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda53
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AccessibilityManagerService.lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$19(str, accessibilityUserState, shortcutTargetsLocked, shortcutTargetsLocked2, (ComponentName) obj);
            }
        });
        if (!z && !(size2 != shortcutTargetsLocked.size())) {
            return;
        }
        persistColonDelimitedSetToSettingLocked("accessibility_button_targets", accessibilityUserState.mUserId, shortcutTargetsLocked, new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda54
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$20;
                lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$20 = AccessibilityManagerService.lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$20((String) obj);
                return lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$20;
            }
        });
        scheduleNotifyClientsOfServicesStateChangeLocked(accessibilityUserState);
    }

    public static /* synthetic */ boolean lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$18(String str, AccessibilityUserState accessibilityUserState, String str2) {
        ComponentName unflattenFromString;
        AccessibilityServiceInfo installedServiceInfoLocked;
        if ((str != null && str2 != null && !str2.contains(str)) || (unflattenFromString = ComponentName.unflattenFromString(str2)) == null || (installedServiceInfoLocked = accessibilityUserState.getInstalledServiceInfoLocked(unflattenFromString)) == null) {
            return false;
        }
        if (installedServiceInfoLocked.getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion <= 29) {
            Slog.v("AccessibilityManagerService", "Legacy service " + unflattenFromString + " should not in the button");
            return true;
        }
        if (!((installedServiceInfoLocked.flags & 256) != 0) || accessibilityUserState.mEnabledServices.contains(unflattenFromString)) {
            return false;
        }
        Slog.v("AccessibilityManagerService", "Service requesting a11y button and be assigned to the button" + unflattenFromString + " should be enabled state");
        return true;
    }

    public static /* synthetic */ void lambda$migrateAccessibilityButtonSettingsIfNecessaryLocked$19(String str, AccessibilityUserState accessibilityUserState, Set set, Set set2, ComponentName componentName) {
        AccessibilityServiceInfo installedServiceInfoLocked;
        if ((str == null || componentName == null || str.equals(componentName.getPackageName())) && (installedServiceInfoLocked = accessibilityUserState.getInstalledServiceInfoLocked(componentName)) != null) {
            boolean z = (installedServiceInfoLocked.flags & 256) != 0;
            if (installedServiceInfoLocked.getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion <= 29 || !z) {
                return;
            }
            String flattenToString = componentName.flattenToString();
            if (TextUtils.isEmpty(flattenToString) || AccessibilityUserState.doesShortcutTargetsStringContain(set, flattenToString) || AccessibilityUserState.doesShortcutTargetsStringContain(set2, flattenToString)) {
                return;
            }
            Slog.v("AccessibilityManagerService", "A enabled service requesting a11y button " + componentName + " should be assign to the button or shortcut.");
            set.add(flattenToString);
        }
    }

    public final void removeShortcutTargetForUnboundServiceLocked(AccessibilityUserState accessibilityUserState, AccessibilityServiceConnection accessibilityServiceConnection) {
        if (!accessibilityServiceConnection.mRequestAccessibilityButton || accessibilityServiceConnection.getServiceInfo().getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion <= 29) {
            return;
        }
        ComponentName componentName = accessibilityServiceConnection.getComponentName();
        if (accessibilityUserState.removeShortcutTargetLocked(1, componentName)) {
            persistColonDelimitedSetToSettingLocked("accessibility_shortcut_target_service", accessibilityUserState.mUserId, accessibilityUserState.getShortcutTargetsLocked(1), new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda15
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$removeShortcutTargetForUnboundServiceLocked$21;
                    lambda$removeShortcutTargetForUnboundServiceLocked$21 = AccessibilityManagerService.lambda$removeShortcutTargetForUnboundServiceLocked$21((String) obj);
                    return lambda$removeShortcutTargetForUnboundServiceLocked$21;
                }
            });
        }
        if (accessibilityUserState.removeShortcutTargetLocked(0, componentName)) {
            persistColonDelimitedSetToSettingLocked("accessibility_button_targets", accessibilityUserState.mUserId, accessibilityUserState.getShortcutTargetsLocked(0), new Function() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda16
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$removeShortcutTargetForUnboundServiceLocked$22;
                    lambda$removeShortcutTargetForUnboundServiceLocked$22 = AccessibilityManagerService.lambda$removeShortcutTargetForUnboundServiceLocked$22((String) obj);
                    return lambda$removeShortcutTargetForUnboundServiceLocked$22;
                }
            });
        }
    }

    public final void updateRecommendedUiTimeoutLocked(AccessibilityUserState accessibilityUserState) {
        int userNonInteractiveUiTimeoutLocked = accessibilityUserState.getUserNonInteractiveUiTimeoutLocked();
        int userInteractiveUiTimeoutLocked = accessibilityUserState.getUserInteractiveUiTimeoutLocked();
        if (userNonInteractiveUiTimeoutLocked == 0 || userInteractiveUiTimeoutLocked == 0) {
            ArrayList<AccessibilityServiceConnection> arrayList = accessibilityUserState.mBoundServices;
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                int interactiveUiTimeoutMillis = arrayList.get(i3).getServiceInfo().getInteractiveUiTimeoutMillis();
                if (i < interactiveUiTimeoutMillis) {
                    i = interactiveUiTimeoutMillis;
                }
                int nonInteractiveUiTimeoutMillis = arrayList.get(i3).getServiceInfo().getNonInteractiveUiTimeoutMillis();
                if (i2 < nonInteractiveUiTimeoutMillis) {
                    i2 = nonInteractiveUiTimeoutMillis;
                }
            }
            if (userNonInteractiveUiTimeoutLocked == 0) {
                userNonInteractiveUiTimeoutLocked = i2;
            }
            if (userInteractiveUiTimeoutLocked == 0) {
                userInteractiveUiTimeoutLocked = i;
            }
        }
        accessibilityUserState.setNonInteractiveUiTimeoutLocked(userNonInteractiveUiTimeoutLocked);
        accessibilityUserState.setInteractiveUiTimeoutLocked(userInteractiveUiTimeoutLocked);
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public KeyEventDispatcher getKeyEventDispatcher() {
        if (this.mKeyEventDispatcher == null) {
            this.mKeyEventDispatcher = new KeyEventDispatcher(this.mMainHandler, 8, this.mLock, this.mPowerManager);
        }
        return this.mKeyEventDispatcher;
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public PendingIntent getPendingIntentActivity(Context context, int i, Intent intent, int i2) {
        return PendingIntent.getActivity(context, i, intent, i2);
    }

    public void performAccessibilityShortcut(String str) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.performAccessibilityShortcut", 4L, "targetName=" + str);
        }
        if (UserHandle.getAppId(Binder.getCallingUid()) != 1000 && this.mContext.checkCallingPermission("android.permission.MANAGE_ACCESSIBILITY") != 0) {
            throw new SecurityException("performAccessibilityShortcut requires the MANAGE_ACCESSIBILITY permission");
        }
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new AccessibilityManagerService$$ExternalSyntheticLambda1(), this, 0, 1, str));
    }

    public final void performAccessibilityShortcutInternal(int i, int i2, String str) {
        List<String> accessibilityShortcutTargetsInternal = getAccessibilityShortcutTargetsInternal(i2);
        if (accessibilityShortcutTargetsInternal.isEmpty()) {
            Slog.d("AccessibilityManagerService", "No target to perform shortcut, shortcutType=" + i2);
            return;
        }
        if (str != null && !AccessibilityUserState.doesShortcutTargetsStringContain(accessibilityShortcutTargetsInternal, str)) {
            Slog.v("AccessibilityManagerService", "Perform shortcut failed, invalid target name:" + str);
            str = null;
        }
        if (str == null) {
            if (accessibilityShortcutTargetsInternal.size() > 1) {
                showAccessibilityTargetsSelection(i, i2);
                return;
            }
            str = accessibilityShortcutTargetsInternal.get(0);
        }
        if (str.equals("com.android.server.accessibility.MagnificationController")) {
            AccessibilityStatsLogUtils.logAccessibilityShortcutActivated(this.mContext, AccessibilityShortcutController.MAGNIFICATION_COMPONENT_NAME, i2, !getMagnificationController().getFullScreenMagnificationController().isActivated(i));
            sendAccessibilityButtonToInputFilter(i);
            return;
        }
        ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        if (unflattenFromString == null) {
            Slog.d("AccessibilityManagerService", "Perform shortcut failed, invalid target name:" + str);
        } else if (performAccessibilityFrameworkFeature(i, unflattenFromString, i2)) {
        } else {
            if (performAccessibilityShortcutTargetActivity(i, unflattenFromString)) {
                AccessibilityStatsLogUtils.logAccessibilityShortcutActivated(this.mContext, unflattenFromString, i2);
            } else {
                performAccessibilityShortcutTargetService(i, i2, unflattenFromString);
            }
        }
    }

    public final boolean performAccessibilityFrameworkFeature(int i, ComponentName componentName, int i2) {
        Map frameworkShortcutFeaturesMap = AccessibilityShortcutController.getFrameworkShortcutFeaturesMap();
        if (frameworkShortcutFeaturesMap.containsKey(componentName)) {
            AccessibilityShortcutController.FrameworkFeatureInfo frameworkFeatureInfo = (AccessibilityShortcutController.FrameworkFeatureInfo) frameworkShortcutFeaturesMap.get(componentName);
            SettingsStringUtil.SettingStringHelper settingStringHelper = new SettingsStringUtil.SettingStringHelper(this.mContext.getContentResolver(), frameworkFeatureInfo.getSettingKey(), this.mCurrentUserId);
            if (frameworkFeatureInfo instanceof AccessibilityShortcutController.LaunchableFrameworkFeatureInfo) {
                AccessibilityStatsLogUtils.logAccessibilityShortcutActivated(this.mContext, componentName, i2, true);
                launchAccessibilityFrameworkFeature(i, componentName);
                return true;
            }
            if (!TextUtils.equals(frameworkFeatureInfo.getSettingOnValue(), settingStringHelper.read())) {
                AccessibilityStatsLogUtils.logAccessibilityShortcutActivated(this.mContext, componentName, i2, true);
                settingStringHelper.write(frameworkFeatureInfo.getSettingOnValue());
            } else {
                AccessibilityStatsLogUtils.logAccessibilityShortcutActivated(this.mContext, componentName, i2, false);
                settingStringHelper.write(frameworkFeatureInfo.getSettingOffValue());
            }
            return true;
        }
        return false;
    }

    public final boolean performAccessibilityShortcutTargetActivity(int i, ComponentName componentName) {
        synchronized (this.mLock) {
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            for (int i2 = 0; i2 < currentUserStateLocked.mInstalledShortcuts.size(); i2++) {
                if (currentUserStateLocked.mInstalledShortcuts.get(i2).getComponentName().equals(componentName)) {
                    launchShortcutTargetActivity(i, componentName);
                    return true;
                }
            }
            return false;
        }
    }

    public final boolean performAccessibilityShortcutTargetService(int i, int i2, ComponentName componentName) {
        synchronized (this.mLock) {
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            AccessibilityServiceInfo installedServiceInfoLocked = currentUserStateLocked.getInstalledServiceInfoLocked(componentName);
            if (installedServiceInfoLocked == null) {
                Slog.d("AccessibilityManagerService", "Perform shortcut failed, invalid component name:" + componentName);
                return false;
            }
            AccessibilityServiceConnection serviceConnectionLocked = currentUserStateLocked.getServiceConnectionLocked(componentName);
            int i3 = installedServiceInfoLocked.getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion;
            boolean z = (installedServiceInfoLocked.flags & 256) != 0;
            if ((i3 <= 29 && i2 == 1) || (i3 > 29 && !z)) {
                if (serviceConnectionLocked == null) {
                    AccessibilityStatsLogUtils.logAccessibilityShortcutActivated(this.mContext, componentName, i2, true);
                    enableAccessibilityServiceLocked(componentName, this.mCurrentUserId);
                } else {
                    AccessibilityStatsLogUtils.logAccessibilityShortcutActivated(this.mContext, componentName, i2, false);
                    disableAccessibilityServiceLocked(componentName, this.mCurrentUserId);
                }
                return true;
            } else if (i2 == 1 && i3 > 29 && z && !currentUserStateLocked.getEnabledServicesLocked().contains(componentName)) {
                enableAccessibilityServiceLocked(componentName, this.mCurrentUserId);
                return true;
            } else {
                if (serviceConnectionLocked != null && currentUserStateLocked.mBoundServices.contains(serviceConnectionLocked) && serviceConnectionLocked.mRequestAccessibilityButton) {
                    AccessibilityStatsLogUtils.logAccessibilityShortcutActivated(this.mContext, componentName, i2, true);
                    serviceConnectionLocked.notifyAccessibilityButtonClickedLocked(i);
                    return true;
                }
                Slog.d("AccessibilityManagerService", "Perform shortcut failed, service is not ready:" + componentName);
                return false;
            }
        }
    }

    public final void launchAccessibilityFrameworkFeature(int i, ComponentName componentName) {
        if (componentName.equals(AccessibilityShortcutController.ACCESSIBILITY_HEARING_AIDS_COMPONENT_NAME)) {
            launchAccessibilitySubSettings(i, AccessibilityShortcutController.ACCESSIBILITY_HEARING_AIDS_COMPONENT_NAME);
        }
    }

    public List<String> getAccessibilityShortcutTargets(int i) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.getAccessibilityShortcutTargets", 4L, "shortcutType=" + i);
        }
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_ACCESSIBILITY") != 0) {
            throw new SecurityException("getAccessibilityShortcutService requires the MANAGE_ACCESSIBILITY permission");
        }
        return getAccessibilityShortcutTargetsInternal(i);
    }

    public final List<String> getAccessibilityShortcutTargetsInternal(int i) {
        synchronized (this.mLock) {
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            ArrayList arrayList = new ArrayList(currentUserStateLocked.getShortcutTargetsLocked(i));
            if (i != 0) {
                return arrayList;
            }
            for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
                AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(size);
                if (accessibilityServiceConnection.mRequestAccessibilityButton && accessibilityServiceConnection.getServiceInfo().getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion <= 29) {
                    String flattenToString = accessibilityServiceConnection.getComponentName().flattenToString();
                    if (!TextUtils.isEmpty(flattenToString)) {
                        arrayList.add(flattenToString);
                    }
                }
            }
            return arrayList;
        }
    }

    public final void enableAccessibilityServiceLocked(ComponentName componentName, int i) {
        this.mTempComponentNameSet.clear();
        readComponentNamesFromSettingLocked("enabled_accessibility_services", i, this.mTempComponentNameSet);
        this.mTempComponentNameSet.add(componentName);
        persistComponentNamesToSettingLocked("enabled_accessibility_services", this.mTempComponentNameSet, i);
        AccessibilityUserState userStateLocked = getUserStateLocked(i);
        if (userStateLocked.mEnabledServices.add(componentName)) {
            onUserStateChangedLocked(userStateLocked);
        }
    }

    public final void disableAccessibilityServiceLocked(ComponentName componentName, int i) {
        this.mTempComponentNameSet.clear();
        readComponentNamesFromSettingLocked("enabled_accessibility_services", i, this.mTempComponentNameSet);
        this.mTempComponentNameSet.remove(componentName);
        persistComponentNamesToSettingLocked("enabled_accessibility_services", this.mTempComponentNameSet, i);
        AccessibilityUserState userStateLocked = getUserStateLocked(i);
        if (userStateLocked.mEnabledServices.remove(componentName)) {
            onUserStateChangedLocked(userStateLocked);
        }
    }

    @Override // com.android.server.accessibility.AccessibilityWindowManager.AccessibilityEventSender
    public void sendAccessibilityEventForCurrentUserLocked(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getWindowChanges() == 1) {
            sendPendingWindowStateChangedEventsForAvailableWindowLocked(accessibilityEvent.getWindowId());
        }
        sendAccessibilityEventLocked(accessibilityEvent, this.mCurrentUserId);
    }

    public final void sendAccessibilityEventLocked(AccessibilityEvent accessibilityEvent, int i) {
        accessibilityEvent.setEventTime(SystemClock.uptimeMillis());
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda43
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((AccessibilityManagerService) obj).sendAccessibilityEvent((AccessibilityEvent) obj2, ((Integer) obj3).intValue());
            }
        }, this, accessibilityEvent, Integer.valueOf(i)));
    }

    public boolean sendFingerprintGesture(int i) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(131076L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.sendFingerprintGesture", 131076L, "gestureKeyCode=" + i);
        }
        synchronized (this.mLock) {
            if (UserHandle.getAppId(Binder.getCallingUid()) != 1000) {
                throw new SecurityException("Only SYSTEM can call sendFingerprintGesture");
            }
        }
        FingerprintGestureDispatcher fingerprintGestureDispatcher = this.mFingerprintGestureDispatcher;
        if (fingerprintGestureDispatcher == null) {
            return false;
        }
        return fingerprintGestureDispatcher.onFingerprintGesture(i);
    }

    public int getAccessibilityWindowId(IBinder iBinder) {
        int findWindowIdLocked;
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.getAccessibilityWindowId", 4L, "windowToken=" + iBinder);
        }
        synchronized (this.mLock) {
            if (UserHandle.getAppId(Binder.getCallingUid()) != 1000) {
                throw new SecurityException("Only SYSTEM can call getAccessibilityWindowId");
            }
            findWindowIdLocked = this.mA11yWindowManager.findWindowIdLocked(this.mCurrentUserId, iBinder);
        }
        return findWindowIdLocked;
    }

    public long getRecommendedTimeoutMillis() {
        long recommendedTimeoutMillisLocked;
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            this.mTraceManager.logTrace("AccessibilityManagerService.getRecommendedTimeoutMillis", 4L);
        }
        synchronized (this.mLock) {
            recommendedTimeoutMillisLocked = getRecommendedTimeoutMillisLocked(getCurrentUserStateLocked());
        }
        return recommendedTimeoutMillisLocked;
    }

    public final long getRecommendedTimeoutMillisLocked(AccessibilityUserState accessibilityUserState) {
        return IntPair.of(accessibilityUserState.getInteractiveUiTimeoutLocked(), accessibilityUserState.getNonInteractiveUiTimeoutLocked());
    }

    public void setWindowMagnificationConnection(IWindowMagnificationConnection iWindowMagnificationConnection) throws RemoteException {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(132L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.setWindowMagnificationConnection", 132L, "connection=" + iWindowMagnificationConnection);
        }
        this.mSecurityPolicy.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE");
        getWindowMagnificationMgr().setConnection(iWindowMagnificationConnection);
    }

    public WindowMagnificationManager getWindowMagnificationMgr() {
        WindowMagnificationManager windowMagnificationMgr;
        synchronized (this.mLock) {
            windowMagnificationMgr = this.mMagnificationController.getWindowMagnificationMgr();
        }
        return windowMagnificationMgr;
    }

    public MagnificationController getMagnificationController() {
        return this.mMagnificationController;
    }

    public void associateEmbeddedHierarchy(IBinder iBinder, IBinder iBinder2) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.associateEmbeddedHierarchy", 4L, "host=" + iBinder + ";embedded=" + iBinder2);
        }
        synchronized (this.mLock) {
            this.mA11yWindowManager.associateEmbeddedHierarchyLocked(iBinder, iBinder2);
        }
    }

    public void disassociateEmbeddedHierarchy(IBinder iBinder) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.disassociateEmbeddedHierarchy", 4L, "token=" + iBinder);
        }
        synchronized (this.mLock) {
            this.mA11yWindowManager.disassociateEmbeddedHierarchyLocked(iBinder);
        }
    }

    public int getFocusStrokeWidth() {
        int focusStrokeWidthLocked;
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            this.mTraceManager.logTrace("AccessibilityManagerService.getFocusStrokeWidth", 4L);
        }
        synchronized (this.mLock) {
            focusStrokeWidthLocked = getCurrentUserStateLocked().getFocusStrokeWidthLocked();
        }
        return focusStrokeWidthLocked;
    }

    public int getFocusColor() {
        int focusColorLocked;
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            this.mTraceManager.logTrace("AccessibilityManagerService.getFocusColor", 4L);
        }
        synchronized (this.mLock) {
            focusColorLocked = getCurrentUserStateLocked().getFocusColorLocked();
        }
        return focusColorLocked;
    }

    public boolean isAudioDescriptionByDefaultEnabled() {
        boolean isAudioDescriptionByDefaultEnabledLocked;
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            this.mTraceManager.logTrace("AccessibilityManagerService.isAudioDescriptionByDefaultEnabled", 4L);
        }
        synchronized (this.mLock) {
            isAudioDescriptionByDefaultEnabledLocked = getCurrentUserStateLocked().isAudioDescriptionByDefaultEnabledLocked();
        }
        return isAudioDescriptionByDefaultEnabledLocked;
    }

    public void setAccessibilityWindowAttributes(int i, int i2, int i3, AccessibilityWindowAttributes accessibilityWindowAttributes) {
        if (this.mTraceManager.isA11yTracingEnabledForTypes(4L)) {
            this.mTraceManager.logTrace("AccessibilityManagerService.setAccessibilityWindowAttributes", 4L);
        }
        this.mA11yWindowManager.setAccessibilityWindowAttributes(i, i2, i3, accessibilityWindowAttributes);
    }

    @RequiresPermission("android.permission.SET_SYSTEM_AUDIO_CAPTION")
    public void setSystemAudioCaptioningEnabled(boolean z, int i) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.SET_SYSTEM_AUDIO_CAPTION", "setSystemAudioCaptioningEnabled");
        this.mCaptioningManagerImpl.setSystemAudioCaptioningEnabled(z, i);
    }

    public boolean isSystemAudioCaptioningUiEnabled(int i) {
        return this.mCaptioningManagerImpl.isSystemAudioCaptioningUiEnabled(i);
    }

    @RequiresPermission("android.permission.SET_SYSTEM_AUDIO_CAPTION")
    public void setSystemAudioCaptioningUiEnabled(boolean z, int i) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.SET_SYSTEM_AUDIO_CAPTION", "setSystemAudioCaptioningUiEnabled");
        this.mCaptioningManagerImpl.setSystemAudioCaptioningUiEnabled(z, i);
    }

    public boolean registerProxyForDisplay(IAccessibilityServiceClient iAccessibilityServiceClient, int i) throws RemoteException {
        this.mSecurityPolicy.enforceCallingOrSelfPermission("android.permission.MANAGE_ACCESSIBILITY");
        this.mSecurityPolicy.enforceCallingOrSelfPermission("android.permission.CREATE_VIRTUAL_DEVICE");
        if (iAccessibilityServiceClient == null) {
            return false;
        }
        if (i < 0) {
            throw new IllegalArgumentException("The display id " + i + " is invalid.");
        } else if (i == 0) {
            throw new IllegalArgumentException("The default display cannot be proxy-ed.");
        } else {
            if (!isTrackedDisplay(i)) {
                throw new IllegalArgumentException("The display " + i + " does not exist or is not tracked by accessibility.");
            } else if (this.mProxyManager.isProxyed(i)) {
                throw new IllegalArgumentException("The display " + i + " is already beingproxy-ed");
            } else {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    ProxyManager proxyManager = this.mProxyManager;
                    Context context = this.mContext;
                    int i2 = sIdCounter;
                    sIdCounter = i2 + 1;
                    proxyManager.registerProxy(iAccessibilityServiceClient, i, context, i2, this.mMainHandler, this.mSecurityPolicy, this, getTraceManager(), this.mWindowManagerService);
                    synchronized (this.mLock) {
                        notifyClearAccessibilityCacheLocked();
                    }
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return true;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    throw th;
                }
            }
        }
    }

    public boolean unregisterProxyForDisplay(int i) {
        this.mSecurityPolicy.enforceCallingOrSelfPermission("android.permission.MANAGE_ACCESSIBILITY");
        this.mSecurityPolicy.enforceCallingOrSelfPermission("android.permission.CREATE_VIRTUAL_DEVICE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mProxyManager.unregisterProxy(i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isDisplayProxyed(int i) {
        return this.mProxyManager.isProxyed(i);
    }

    public boolean startFlashNotificationSequence(String str, int i, IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mFlashNotificationsController.startFlashNotificationSequence(str, i, iBinder);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean stopFlashNotificationSequence(String str) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mFlashNotificationsController.stopFlashNotificationSequence(str);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean startFlashNotificationEvent(String str, int i, String str2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mFlashNotificationsController.startFlashNotificationEvent(str, i, str2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AccessibilityInputFilter accessibilityInputFilter;
        if (DumpUtils.checkDumpPermission(this.mContext, "AccessibilityManagerService", printWriter)) {
            synchronized (this.mLock) {
                printWriter.println("ACCESSIBILITY MANAGER (dumpsys accessibility)");
                printWriter.println();
                printWriter.append("currentUserId=").append((CharSequence) String.valueOf(this.mCurrentUserId));
                printWriter.println();
                printWriter.append("hasWindowMagnificationConnection=").append((CharSequence) String.valueOf(getWindowMagnificationMgr().isConnected()));
                printWriter.println();
                this.mMagnificationProcessor.dump(printWriter, getValidDisplayList());
                int size = this.mUserStates.size();
                for (int i = 0; i < size; i++) {
                    this.mUserStates.valueAt(i).dump(fileDescriptor, printWriter, strArr);
                }
                if (this.mUiAutomationManager.isUiAutomationRunningLocked()) {
                    this.mUiAutomationManager.dumpUiAutomationService(fileDescriptor, printWriter, strArr);
                    printWriter.println();
                }
                this.mA11yWindowManager.dump(fileDescriptor, printWriter, strArr);
                if (this.mHasInputFilter && (accessibilityInputFilter = this.mInputFilter) != null) {
                    accessibilityInputFilter.dump(fileDescriptor, printWriter, strArr);
                }
                printWriter.println("Global client list info:{");
                this.mGlobalClients.dump(printWriter, "    Client list ");
                printWriter.println("    Registered clients:{");
                for (int i2 = 0; i2 < this.mGlobalClients.getRegisteredCallbackCount(); i2++) {
                    printWriter.append((CharSequence) Arrays.toString(((Client) this.mGlobalClients.getRegisteredCallbackCookie(i2)).mPackageNames));
                }
                printWriter.println();
                this.mProxyManager.dump(fileDescriptor, printWriter, strArr);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class MainHandler extends Handler {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public MainHandler(Looper looper) {
            super(looper);
            AccessibilityManagerService.this = r1;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 8) {
                KeyEvent keyEvent = (KeyEvent) message.obj;
                int i = message.arg1;
                synchronized (AccessibilityManagerService.this.mLock) {
                    if (AccessibilityManagerService.this.mHasInputFilter && AccessibilityManagerService.this.mInputFilter != null) {
                        AccessibilityManagerService.this.mInputFilter.sendInputEvent(keyEvent, i);
                    }
                }
                keyEvent.recycle();
            }
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public MagnificationProcessor getMagnificationProcessor() {
        return this.mMagnificationProcessor;
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void onClientChangeLocked(boolean z) {
        AccessibilityUserState userStateLocked = getUserStateLocked(this.mCurrentUserId);
        onUserStateChangedLocked(userStateLocked);
        if (z) {
            scheduleNotifyClientsOfServicesStateChangeLocked(userStateLocked);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new AccessibilityShellCommand(this, this.mSystemActionPerformer).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    /* loaded from: classes.dex */
    public final class InteractionBridge {
        public final ComponentName COMPONENT_NAME;
        public final AccessibilityInteractionClient mClient;
        public final int mConnectionId;
        public final Display mDefaultDisplay;

        public InteractionBridge() {
            AccessibilityUserState currentUserStateLocked;
            AccessibilityManagerService.this = r19;
            ComponentName componentName = new ComponentName("com.android.server.accessibility", "InteractionBridge");
            this.COMPONENT_NAME = componentName;
            AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
            accessibilityServiceInfo.setCapabilities(1);
            accessibilityServiceInfo.flags = accessibilityServiceInfo.flags | 64 | 2;
            accessibilityServiceInfo.setAccessibilityTool(true);
            synchronized (r19.mLock) {
                currentUserStateLocked = r19.getCurrentUserStateLocked();
            }
            Context context = r19.mContext;
            int i = AccessibilityManagerService.sIdCounter;
            AccessibilityManagerService.sIdCounter = i + 1;
            AccessibilityServiceConnection accessibilityServiceConnection = new AccessibilityServiceConnection(currentUserStateLocked, context, componentName, accessibilityServiceInfo, i, r19.mMainHandler, r19.mLock, r19.mSecurityPolicy, r19, r19.getTraceManager(), r19.mWindowManagerService, r19.getSystemActionPerformer(), r19.mA11yWindowManager, r19.mActivityTaskManagerService) { // from class: com.android.server.accessibility.AccessibilityManagerService.InteractionBridge.1
                @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection
                public boolean supportsFlagForNotImportantViews(AccessibilityServiceInfo accessibilityServiceInfo2) {
                    return true;
                }

                {
                    InteractionBridge.this = this;
                }
            };
            int i2 = accessibilityServiceConnection.mId;
            this.mConnectionId = i2;
            this.mClient = AccessibilityInteractionClient.getInstance(r19.mContext);
            AccessibilityInteractionClient.addConnection(i2, accessibilityServiceConnection, false);
            this.mDefaultDisplay = ((DisplayManager) r19.mContext.getSystemService("display")).getDisplay(0);
        }

        public boolean performActionOnAccessibilityFocusedItemNotLocked(AccessibilityNodeInfo.AccessibilityAction accessibilityAction) {
            AccessibilityNodeInfo accessibilityFocusNotLocked = getAccessibilityFocusNotLocked();
            if (accessibilityFocusNotLocked == null || !accessibilityFocusNotLocked.getActionList().contains(accessibilityAction)) {
                return false;
            }
            return accessibilityFocusNotLocked.performAction(accessibilityAction.getId());
        }

        public boolean getAccessibilityFocusClickPointInScreenNotLocked(Point point) {
            MagnificationSpec magnificationSpec;
            AccessibilityNodeInfo accessibilityFocusNotLocked = getAccessibilityFocusNotLocked();
            if (accessibilityFocusNotLocked == null) {
                return false;
            }
            synchronized (AccessibilityManagerService.this.mLock) {
                Rect rect = AccessibilityManagerService.this.mTempRect;
                accessibilityFocusNotLocked.getBoundsInScreen(rect);
                Point point2 = new Point(rect.centerX(), rect.centerY());
                Pair<float[], MagnificationSpec> windowTransformationMatrixAndMagnificationSpec = AccessibilityManagerService.this.getWindowTransformationMatrixAndMagnificationSpec(accessibilityFocusNotLocked.getWindowId());
                if (windowTransformationMatrixAndMagnificationSpec == null || windowTransformationMatrixAndMagnificationSpec.second == null) {
                    magnificationSpec = null;
                } else {
                    magnificationSpec = new MagnificationSpec();
                    magnificationSpec.setTo((MagnificationSpec) windowTransformationMatrixAndMagnificationSpec.second);
                }
                if (magnificationSpec != null && !magnificationSpec.isNop()) {
                    rect.offset((int) (-magnificationSpec.offsetX), (int) (-magnificationSpec.offsetY));
                    rect.scale(1.0f / magnificationSpec.scale);
                }
                Rect rect2 = AccessibilityManagerService.this.mTempRect1;
                AccessibilityManagerService.this.getWindowBounds(accessibilityFocusNotLocked.getWindowId(), rect2);
                if (rect.intersect(rect2)) {
                    Point point3 = AccessibilityManagerService.this.mTempPoint;
                    this.mDefaultDisplay.getRealSize(point3);
                    if (rect.intersect(0, 0, point3.x, point3.y)) {
                        point.set(point2.x, point2.y);
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }

        public final AccessibilityNodeInfo getAccessibilityFocusNotLocked() {
            synchronized (AccessibilityManagerService.this.mLock) {
                int focusedWindowId = AccessibilityManagerService.this.mA11yWindowManager.getFocusedWindowId(2);
                if (focusedWindowId == -1) {
                    return null;
                }
                return getAccessibilityFocusNotLocked(focusedWindowId);
            }
        }

        public final AccessibilityNodeInfo getAccessibilityFocusNotLocked(int i) {
            return this.mClient.findFocus(this.mConnectionId, i, AccessibilityNodeInfo.ROOT_NODE_ID, 2);
        }
    }

    public ArrayList<Display> getValidDisplayList() {
        return this.mA11yDisplayListener.getValidDisplayList();
    }

    public final boolean isTrackedDisplay(int i) {
        Iterator<Display> it = getValidDisplayList().iterator();
        while (it.hasNext()) {
            if (it.next().getDisplayId() == i) {
                return true;
            }
        }
        return false;
    }

    /* loaded from: classes.dex */
    public class AccessibilityDisplayListener implements DisplayManager.DisplayListener {
        public final DisplayManager mDisplayManager;
        public final ArrayList<Display> mDisplaysList = new ArrayList<>();
        public int mSystemUiUid;

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
        }

        public AccessibilityDisplayListener(Context context, Handler handler) {
            AccessibilityManagerService.this = r3;
            this.mSystemUiUid = 0;
            DisplayManager displayManager = (DisplayManager) context.getSystemService("display");
            this.mDisplayManager = displayManager;
            displayManager.registerDisplayListener(this, handler);
            initializeDisplayList();
            PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            if (packageManagerInternal != null) {
                this.mSystemUiUid = packageManagerInternal.getPackageUid(packageManagerInternal.getSystemUiServiceComponent().getPackageName(), 1048576L, r3.mCurrentUserId);
            }
        }

        public ArrayList<Display> getValidDisplayList() {
            ArrayList<Display> arrayList;
            synchronized (AccessibilityManagerService.this.mLock) {
                arrayList = this.mDisplaysList;
            }
            return arrayList;
        }

        public final void initializeDisplayList() {
            Display[] displays = this.mDisplayManager.getDisplays();
            synchronized (AccessibilityManagerService.this.mLock) {
                this.mDisplaysList.clear();
                for (Display display : displays) {
                    if (isValidDisplay(display)) {
                        this.mDisplaysList.add(display);
                    }
                }
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
            Display display = this.mDisplayManager.getDisplay(i);
            if (isValidDisplay(display)) {
                synchronized (AccessibilityManagerService.this.mLock) {
                    this.mDisplaysList.add(display);
                    AccessibilityManagerService.this.mA11yOverlayLayers.put(i, AccessibilityManagerService.this.mWindowManagerService.getA11yOverlayLayer(i));
                    if (AccessibilityManagerService.this.mInputFilter != null) {
                        AccessibilityManagerService.this.mInputFilter.onDisplayAdded(display);
                    }
                    AccessibilityUserState currentUserStateLocked = AccessibilityManagerService.this.getCurrentUserStateLocked();
                    if (i != 0) {
                        ArrayList<AccessibilityServiceConnection> arrayList = currentUserStateLocked.mBoundServices;
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            arrayList.get(i2).onDisplayAdded(i);
                        }
                    }
                    AccessibilityManagerService.this.updateMagnificationLocked(currentUserStateLocked);
                    AccessibilityManagerService.this.updateWindowsForAccessibilityCallbackLocked(currentUserStateLocked);
                    AccessibilityManagerService.this.notifyClearAccessibilityCacheLocked();
                }
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
            synchronized (AccessibilityManagerService.this.mLock) {
                if (removeDisplayFromList(i)) {
                    AccessibilityManagerService.this.mA11yOverlayLayers.remove(i);
                    if (AccessibilityManagerService.this.mInputFilter != null) {
                        AccessibilityManagerService.this.mInputFilter.onDisplayRemoved(i);
                    }
                    AccessibilityUserState currentUserStateLocked = AccessibilityManagerService.this.getCurrentUserStateLocked();
                    if (i != 0) {
                        ArrayList<AccessibilityServiceConnection> arrayList = currentUserStateLocked.mBoundServices;
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            arrayList.get(i2).onDisplayRemoved(i);
                        }
                    }
                    AccessibilityManagerService.this.mMagnificationController.onDisplayRemoved(i);
                    AccessibilityManagerService.this.mA11yWindowManager.stopTrackingWindows(i);
                }
            }
        }

        @GuardedBy({"mLock"})
        public final boolean removeDisplayFromList(int i) {
            for (int i2 = 0; i2 < this.mDisplaysList.size(); i2++) {
                if (this.mDisplaysList.get(i2).getDisplayId() == i) {
                    this.mDisplaysList.remove(i2);
                    return true;
                }
            }
            return false;
        }

        public final boolean isValidDisplay(Display display) {
            if (display == null || display.getType() == 4) {
                return false;
            }
            return display.getType() != 5 || (display.getFlags() & 4) == 0 || display.getOwnerUid() == this.mSystemUiUid;
        }
    }

    /* loaded from: classes.dex */
    public class Client {
        public final IAccessibilityManagerClient mCallback;
        public int mLastSentRelevantEventTypes;
        public final String[] mPackageNames;

        public Client(IAccessibilityManagerClient iAccessibilityManagerClient, int i, AccessibilityUserState accessibilityUserState) {
            AccessibilityManagerService.this = r1;
            this.mCallback = iAccessibilityManagerClient;
            this.mPackageNames = r1.mPackageManager.getPackagesForUid(i);
            synchronized (r1.mLock) {
                this.mLastSentRelevantEventTypes = r1.computeRelevantEventTypesLocked(accessibilityUserState, this);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class AccessibilityContentObserver extends ContentObserver {
        public final Uri mAccessibilityButtonComponentIdUri;
        public final Uri mAccessibilityButtonTargetsUri;
        public final Uri mAccessibilityShortcutServiceIdUri;
        public final Uri mAccessibilitySoftKeyboardModeUri;
        public final Uri mAlwaysOnMagnificationUri;
        public final Uri mAudioDescriptionByDefaultUri;
        public final Uri mAutoclickEnabledUri;
        public final Uri mDisplayMagnificationEnabledUri;
        public final Uri mEnabledAccessibilityServicesUri;
        public final Uri mHighTextContrastUri;
        public final Uri mMagnificationCapabilityUri;
        public final Uri mMagnificationFollowTypingUri;
        public final Uri mMagnificationModeUri;
        public final Uri mShowImeWithHardKeyboardUri;
        public final Uri mTouchExplorationEnabledUri;
        public final Uri mTouchExplorationGrantedAccessibilityServicesUri;
        public final Uri mUserInteractiveUiTimeoutUri;
        public final Uri mUserNonInteractiveUiTimeoutUri;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AccessibilityContentObserver(Handler handler) {
            super(handler);
            AccessibilityManagerService.this = r1;
            this.mTouchExplorationEnabledUri = Settings.Secure.getUriFor("touch_exploration_enabled");
            this.mDisplayMagnificationEnabledUri = Settings.Secure.getUriFor("accessibility_display_magnification_enabled");
            this.mAutoclickEnabledUri = Settings.Secure.getUriFor("accessibility_autoclick_enabled");
            this.mEnabledAccessibilityServicesUri = Settings.Secure.getUriFor("enabled_accessibility_services");
            this.mTouchExplorationGrantedAccessibilityServicesUri = Settings.Secure.getUriFor("touch_exploration_granted_accessibility_services");
            this.mHighTextContrastUri = Settings.Secure.getUriFor("high_text_contrast_enabled");
            this.mAudioDescriptionByDefaultUri = Settings.Secure.getUriFor("enabled_accessibility_audio_description_by_default");
            this.mAccessibilitySoftKeyboardModeUri = Settings.Secure.getUriFor("accessibility_soft_keyboard_mode");
            this.mShowImeWithHardKeyboardUri = Settings.Secure.getUriFor("show_ime_with_hard_keyboard");
            this.mAccessibilityShortcutServiceIdUri = Settings.Secure.getUriFor("accessibility_shortcut_target_service");
            this.mAccessibilityButtonComponentIdUri = Settings.Secure.getUriFor("accessibility_button_target_component");
            this.mAccessibilityButtonTargetsUri = Settings.Secure.getUriFor("accessibility_button_targets");
            this.mUserNonInteractiveUiTimeoutUri = Settings.Secure.getUriFor("accessibility_non_interactive_ui_timeout_ms");
            this.mUserInteractiveUiTimeoutUri = Settings.Secure.getUriFor("accessibility_interactive_ui_timeout_ms");
            this.mMagnificationModeUri = Settings.Secure.getUriFor("accessibility_magnification_mode");
            this.mMagnificationCapabilityUri = Settings.Secure.getUriFor("accessibility_magnification_capability");
            this.mMagnificationFollowTypingUri = Settings.Secure.getUriFor("accessibility_magnification_follow_typing_enabled");
            this.mAlwaysOnMagnificationUri = Settings.Secure.getUriFor("accessibility_magnification_always_on_enabled");
        }

        public void register(ContentResolver contentResolver) {
            contentResolver.registerContentObserver(this.mTouchExplorationEnabledUri, false, this, -1);
            contentResolver.registerContentObserver(this.mDisplayMagnificationEnabledUri, false, this, -1);
            contentResolver.registerContentObserver(this.mAutoclickEnabledUri, false, this, -1);
            contentResolver.registerContentObserver(this.mEnabledAccessibilityServicesUri, false, this, -1);
            contentResolver.registerContentObserver(this.mTouchExplorationGrantedAccessibilityServicesUri, false, this, -1);
            contentResolver.registerContentObserver(this.mHighTextContrastUri, false, this, -1);
            contentResolver.registerContentObserver(this.mAudioDescriptionByDefaultUri, false, this, -1);
            contentResolver.registerContentObserver(this.mAccessibilitySoftKeyboardModeUri, false, this, -1);
            contentResolver.registerContentObserver(this.mShowImeWithHardKeyboardUri, false, this, -1);
            contentResolver.registerContentObserver(this.mAccessibilityShortcutServiceIdUri, false, this, -1);
            contentResolver.registerContentObserver(this.mAccessibilityButtonComponentIdUri, false, this, -1);
            contentResolver.registerContentObserver(this.mAccessibilityButtonTargetsUri, false, this, -1);
            contentResolver.registerContentObserver(this.mUserNonInteractiveUiTimeoutUri, false, this, -1);
            contentResolver.registerContentObserver(this.mUserInteractiveUiTimeoutUri, false, this, -1);
            contentResolver.registerContentObserver(this.mMagnificationModeUri, false, this, -1);
            contentResolver.registerContentObserver(this.mMagnificationCapabilityUri, false, this, -1);
            contentResolver.registerContentObserver(this.mMagnificationFollowTypingUri, false, this, -1);
            contentResolver.registerContentObserver(this.mAlwaysOnMagnificationUri, false, this, -1);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            synchronized (AccessibilityManagerService.this.mLock) {
                AccessibilityUserState currentUserStateLocked = AccessibilityManagerService.this.getCurrentUserStateLocked();
                if (this.mTouchExplorationEnabledUri.equals(uri)) {
                    if (AccessibilityManagerService.this.readTouchExplorationEnabledSettingLocked(currentUserStateLocked)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                    }
                } else if (this.mDisplayMagnificationEnabledUri.equals(uri)) {
                    if (AccessibilityManagerService.this.readMagnificationEnabledSettingsLocked(currentUserStateLocked)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                    }
                } else if (this.mAutoclickEnabledUri.equals(uri)) {
                    if (AccessibilityManagerService.this.readAutoclickEnabledSettingLocked(currentUserStateLocked)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                    }
                } else if (this.mEnabledAccessibilityServicesUri.equals(uri)) {
                    if (AccessibilityManagerService.this.readEnabledAccessibilityServicesLocked(currentUserStateLocked)) {
                        AccessibilityManagerService.this.mSecurityPolicy.onEnabledServicesChangedLocked(currentUserStateLocked.mUserId, currentUserStateLocked.mEnabledServices);
                        currentUserStateLocked.removeDisabledServicesFromTemporaryStatesLocked();
                        AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                    }
                } else if (this.mTouchExplorationGrantedAccessibilityServicesUri.equals(uri)) {
                    if (AccessibilityManagerService.this.readTouchExplorationGrantedAccessibilityServicesLocked(currentUserStateLocked)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                    }
                } else if (this.mHighTextContrastUri.equals(uri)) {
                    if (AccessibilityManagerService.this.readHighTextContrastEnabledSettingLocked(currentUserStateLocked)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                    }
                } else if (this.mAudioDescriptionByDefaultUri.equals(uri)) {
                    if (AccessibilityManagerService.this.readAudioDescriptionEnabledSettingLocked(currentUserStateLocked)) {
                        AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                    }
                } else {
                    if (!this.mAccessibilitySoftKeyboardModeUri.equals(uri) && !this.mShowImeWithHardKeyboardUri.equals(uri)) {
                        if (this.mAccessibilityShortcutServiceIdUri.equals(uri)) {
                            if (AccessibilityManagerService.this.readAccessibilityShortcutKeySettingLocked(currentUserStateLocked)) {
                                AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                            }
                        } else if (this.mAccessibilityButtonComponentIdUri.equals(uri)) {
                            if (AccessibilityManagerService.this.readAccessibilityButtonTargetComponentLocked(currentUserStateLocked)) {
                                AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                            }
                        } else if (this.mAccessibilityButtonTargetsUri.equals(uri)) {
                            if (AccessibilityManagerService.this.readAccessibilityButtonTargetsLocked(currentUserStateLocked)) {
                                AccessibilityManagerService.this.onUserStateChangedLocked(currentUserStateLocked);
                            }
                        } else {
                            if (!this.mUserNonInteractiveUiTimeoutUri.equals(uri) && !this.mUserInteractiveUiTimeoutUri.equals(uri)) {
                                if (this.mMagnificationModeUri.equals(uri)) {
                                    if (AccessibilityManagerService.this.readMagnificationModeForDefaultDisplayLocked(currentUserStateLocked)) {
                                        AccessibilityManagerService.this.updateMagnificationModeChangeSettingsLocked(currentUserStateLocked, 0);
                                    }
                                } else if (this.mMagnificationCapabilityUri.equals(uri)) {
                                    if (AccessibilityManagerService.this.readMagnificationCapabilitiesLocked(currentUserStateLocked)) {
                                        AccessibilityManagerService.this.updateMagnificationCapabilitiesSettingsChangeLocked(currentUserStateLocked);
                                    }
                                } else if (this.mMagnificationFollowTypingUri.equals(uri)) {
                                    AccessibilityManagerService.this.readMagnificationFollowTypingLocked(currentUserStateLocked);
                                } else if (this.mAlwaysOnMagnificationUri.equals(uri)) {
                                    AccessibilityManagerService.this.readAlwaysOnMagnificationLocked(currentUserStateLocked);
                                }
                            }
                            AccessibilityManagerService.this.readUserRecommendedUiTimeoutSettingsLocked(currentUserStateLocked);
                        }
                    }
                    currentUserStateLocked.reconcileSoftKeyboardModeWithSettingsLocked();
                }
            }
        }
    }

    public final void updateMagnificationCapabilitiesSettingsChangeLocked(AccessibilityUserState accessibilityUserState) {
        ArrayList<Display> validDisplayList = getValidDisplayList();
        for (int i = 0; i < validDisplayList.size(); i++) {
            int displayId = validDisplayList.get(i).getDisplayId();
            if (fallBackMagnificationModeSettingsLocked(accessibilityUserState, displayId)) {
                updateMagnificationModeChangeSettingsLocked(accessibilityUserState, displayId);
            }
        }
        updateWindowMagnificationConnectionIfNeeded(accessibilityUserState);
        if ((accessibilityUserState.isDisplayMagnificationEnabledLocked() || accessibilityUserState.isShortcutMagnificationEnabledLocked()) && accessibilityUserState.getMagnificationCapabilitiesLocked() == 3) {
            return;
        }
        for (int i2 = 0; i2 < validDisplayList.size(); i2++) {
            getWindowMagnificationMgr().removeMagnificationButton(validDisplayList.get(i2).getDisplayId());
        }
    }

    public final boolean fallBackMagnificationModeSettingsLocked(AccessibilityUserState accessibilityUserState, int i) {
        if (accessibilityUserState.isValidMagnificationModeLocked(i)) {
            return false;
        }
        Slog.w("AccessibilityManagerService", "displayId " + i + ", invalid magnification mode:" + accessibilityUserState.getMagnificationModeLocked(i));
        int magnificationCapabilitiesLocked = accessibilityUserState.getMagnificationCapabilitiesLocked();
        accessibilityUserState.setMagnificationModeLocked(i, magnificationCapabilitiesLocked);
        if (i == 0) {
            persistMagnificationModeSettingsLocked(magnificationCapabilitiesLocked);
            return true;
        }
        return true;
    }

    public final void persistMagnificationModeSettingsLocked(final int i) {
        BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                AccessibilityManagerService.this.lambda$persistMagnificationModeSettingsLocked$23(i);
            }
        });
    }

    public /* synthetic */ void lambda$persistMagnificationModeSettingsLocked$23(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "accessibility_magnification_mode", i, this.mCurrentUserId);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getMagnificationMode(int i) {
        int magnificationModeLocked;
        synchronized (this.mLock) {
            magnificationModeLocked = getCurrentUserStateLocked().getMagnificationModeLocked(i);
        }
        return magnificationModeLocked;
    }

    public final boolean readMagnificationModeForDefaultDisplayLocked(AccessibilityUserState accessibilityUserState) {
        int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_magnification_mode", 1, accessibilityUserState.mUserId);
        if (intForUser != accessibilityUserState.getMagnificationModeLocked(0)) {
            accessibilityUserState.setMagnificationModeLocked(0, intForUser);
            return true;
        }
        return false;
    }

    public final boolean readMagnificationCapabilitiesLocked(AccessibilityUserState accessibilityUserState) {
        int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_magnification_capability", 1, accessibilityUserState.mUserId);
        if (intForUser != accessibilityUserState.getMagnificationCapabilitiesLocked()) {
            accessibilityUserState.setMagnificationCapabilitiesLocked(intForUser);
            this.mMagnificationController.setMagnificationCapabilities(intForUser);
            return true;
        }
        return false;
    }

    public boolean readMagnificationFollowTypingLocked(AccessibilityUserState accessibilityUserState) {
        boolean z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_magnification_follow_typing_enabled", 1, accessibilityUserState.mUserId) == 1;
        if (z != accessibilityUserState.isMagnificationFollowTypingEnabled()) {
            accessibilityUserState.setMagnificationFollowTypingEnabled(z);
            this.mMagnificationController.setMagnificationFollowTypingEnabled(z);
            return true;
        }
        return false;
    }

    public void updateAlwaysOnMagnification() {
        synchronized (this.mLock) {
            readAlwaysOnMagnificationLocked(getCurrentUserState());
        }
    }

    @GuardedBy({"mLock"})
    public boolean readAlwaysOnMagnificationLocked(AccessibilityUserState accessibilityUserState) {
        boolean z = this.mMagnificationController.isAlwaysOnMagnificationFeatureFlagEnabled() && (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_magnification_always_on_enabled", 1, accessibilityUserState.mUserId) == 1);
        if (z != accessibilityUserState.isAlwaysOnMagnificationEnabled()) {
            accessibilityUserState.setAlwaysOnMagnificationEnabled(z);
            this.mMagnificationController.setAlwaysOnMagnificationEnabled(z);
            return true;
        }
        return false;
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void setGestureDetectionPassthroughRegion(int i, Region region) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda3
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((AccessibilityManagerService) obj).setGestureDetectionPassthroughRegionInternal(((Integer) obj2).intValue(), (Region) obj3);
            }
        }, this, Integer.valueOf(i), region));
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void setTouchExplorationPassthroughRegion(int i, Region region) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda26
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((AccessibilityManagerService) obj).setTouchExplorationPassthroughRegionInternal(((Integer) obj2).intValue(), (Region) obj3);
            }
        }, this, Integer.valueOf(i), region));
    }

    public final void setTouchExplorationPassthroughRegionInternal(int i, Region region) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            if (this.mHasInputFilter && (accessibilityInputFilter = this.mInputFilter) != null) {
                accessibilityInputFilter.setTouchExplorationPassthroughRegion(i, region);
            }
        }
    }

    public final void setGestureDetectionPassthroughRegionInternal(int i, Region region) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            if (this.mHasInputFilter && (accessibilityInputFilter = this.mInputFilter) != null) {
                accessibilityInputFilter.setGestureDetectionPassthroughRegion(i, region);
            }
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void setServiceDetectsGesturesEnabled(int i, boolean z) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda23
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((AccessibilityManagerService) obj).setServiceDetectsGesturesInternal(((Integer) obj2).intValue(), ((Boolean) obj3).booleanValue());
            }
        }, this, Integer.valueOf(i), Boolean.valueOf(z)));
    }

    public final void setServiceDetectsGesturesInternal(int i, boolean z) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            getCurrentUserStateLocked().setServiceDetectsGesturesEnabled(i, z);
            if (this.mHasInputFilter && (accessibilityInputFilter = this.mInputFilter) != null) {
                accessibilityInputFilter.setServiceDetectsGesturesEnabled(i, z);
            }
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void requestTouchExploration(int i) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda19
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).requestTouchExplorationInternal(((Integer) obj2).intValue());
            }
        }, this, Integer.valueOf(i)));
    }

    public final void requestTouchExplorationInternal(int i) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            if (this.mHasInputFilter && (accessibilityInputFilter = this.mInputFilter) != null) {
                accessibilityInputFilter.requestTouchExploration(i);
            }
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void requestDragging(int i, int i2) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda14
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((AccessibilityManagerService) obj).requestDraggingInternal(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
            }
        }, this, Integer.valueOf(i), Integer.valueOf(i2)));
    }

    public final void requestDraggingInternal(int i, int i2) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            if (this.mHasInputFilter && (accessibilityInputFilter = this.mInputFilter) != null) {
                accessibilityInputFilter.requestDragging(i, i2);
            }
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void requestDelegating(int i) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda22
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).requestDelegatingInternal(((Integer) obj2).intValue());
            }
        }, this, Integer.valueOf(i)));
    }

    public final void requestDelegatingInternal(int i) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            if (this.mHasInputFilter && (accessibilityInputFilter = this.mInputFilter) != null) {
                accessibilityInputFilter.requestDelegating(i);
            }
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void onDoubleTap(int i) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda32
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).onDoubleTapInternal(((Integer) obj2).intValue());
            }
        }, this, Integer.valueOf(i)));
    }

    public final void onDoubleTapInternal(int i) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            if (!this.mHasInputFilter || (accessibilityInputFilter = this.mInputFilter) == null) {
                accessibilityInputFilter = null;
            }
        }
        if (accessibilityInputFilter != null) {
            accessibilityInputFilter.onDoubleTap(i);
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void onDoubleTapAndHold(int i) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda2
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).onDoubleTapAndHoldInternal(((Integer) obj2).intValue());
            }
        }, this, Integer.valueOf(i)));
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void requestImeLocked(AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda28
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).createSessionForConnection((AbstractAccessibilityServiceConnection) obj2);
            }
        }, this, abstractAccessibilityServiceConnection));
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda29
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).bindAndStartInputForConnection((AbstractAccessibilityServiceConnection) obj2);
            }
        }, this, abstractAccessibilityServiceConnection));
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void unbindImeLocked(AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda8
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).unbindInputForConnection((AbstractAccessibilityServiceConnection) obj2);
            }
        }, this, abstractAccessibilityServiceConnection));
    }

    public final void createSessionForConnection(AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) {
        synchronized (this.mLock) {
            if (this.mInputSessionRequested) {
                abstractAccessibilityServiceConnection.createImeSessionLocked();
            }
        }
    }

    public final void bindAndStartInputForConnection(AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) {
        synchronized (this.mLock) {
            if (this.mInputBound) {
                abstractAccessibilityServiceConnection.bindInputLocked();
                abstractAccessibilityServiceConnection.startInputLocked(this.mRemoteInputConnection, this.mEditorInfo, this.mRestarting);
            }
        }
    }

    public final void unbindInputForConnection(AbstractAccessibilityServiceConnection abstractAccessibilityServiceConnection) {
        InputMethodManagerInternal.get().unbindAccessibilityFromCurrentClient(abstractAccessibilityServiceConnection.mId);
        synchronized (this.mLock) {
            abstractAccessibilityServiceConnection.unbindInputLocked();
        }
    }

    public final void onDoubleTapAndHoldInternal(int i) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            if (this.mHasInputFilter && (accessibilityInputFilter = this.mInputFilter) != null) {
                accessibilityInputFilter.onDoubleTapAndHold(i);
            }
        }
    }

    public final void updateFocusAppearanceDataLocked(final AccessibilityUserState accessibilityUserState) {
        if (accessibilityUserState.mUserId != this.mCurrentUserId) {
            return;
        }
        if (this.mTraceManager.isA11yTracingEnabledForTypes(2L)) {
            AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
            accessibilityTraceManager.logTrace("AccessibilityManagerService.updateFocusAppearanceDataLocked", 2L, "userState=" + accessibilityUserState);
        }
        this.mMainHandler.post(new Runnable() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                AccessibilityManagerService.this.lambda$updateFocusAppearanceDataLocked$25(accessibilityUserState);
            }
        });
    }

    public /* synthetic */ void lambda$updateFocusAppearanceDataLocked$25(final AccessibilityUserState accessibilityUserState) {
        broadcastToClients(accessibilityUserState, FunctionalUtils.ignoreRemoteException(new FunctionalUtils.RemoteExceptionIgnoringConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda34
            public final void acceptOrThrow(Object obj) {
                AccessibilityManagerService.lambda$updateFocusAppearanceDataLocked$24(AccessibilityUserState.this, (AccessibilityManagerService.Client) obj);
            }
        }));
    }

    public static /* synthetic */ void lambda$updateFocusAppearanceDataLocked$24(AccessibilityUserState accessibilityUserState, Client client) throws RemoteException {
        client.mCallback.setFocusAppearance(accessibilityUserState.getFocusStrokeWidthLocked(), accessibilityUserState.getFocusColorLocked());
    }

    public AccessibilityTraceManager getTraceManager() {
        return this.mTraceManager;
    }

    public void scheduleBindInput() {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda11
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AccessibilityManagerService) obj).bindInput();
            }
        }, this));
    }

    public final void bindInput() {
        synchronized (this.mLock) {
            this.mInputBound = true;
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
                AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(size);
                if (accessibilityServiceConnection.requestImeApis()) {
                    accessibilityServiceConnection.bindInputLocked();
                }
            }
        }
    }

    public void scheduleUnbindInput() {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda44
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AccessibilityManagerService) obj).unbindInput();
            }
        }, this));
    }

    public final void unbindInput() {
        synchronized (this.mLock) {
            this.mInputBound = false;
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
                AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(size);
                if (accessibilityServiceConnection.requestImeApis()) {
                    accessibilityServiceConnection.unbindInputLocked();
                }
            }
        }
    }

    public void scheduleStartInput(IRemoteAccessibilityInputConnection iRemoteAccessibilityInputConnection, EditorInfo editorInfo, boolean z) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda24
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                ((AccessibilityManagerService) obj).startInput((IRemoteAccessibilityInputConnection) obj2, (EditorInfo) obj3, ((Boolean) obj4).booleanValue());
            }
        }, this, iRemoteAccessibilityInputConnection, editorInfo, Boolean.valueOf(z)));
    }

    public final void startInput(IRemoteAccessibilityInputConnection iRemoteAccessibilityInputConnection, EditorInfo editorInfo, boolean z) {
        synchronized (this.mLock) {
            this.mRemoteInputConnection = iRemoteAccessibilityInputConnection;
            this.mEditorInfo = editorInfo;
            this.mRestarting = z;
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
                AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(size);
                if (accessibilityServiceConnection.requestImeApis()) {
                    accessibilityServiceConnection.startInputLocked(iRemoteAccessibilityInputConnection, editorInfo, z);
                }
            }
        }
    }

    public void scheduleCreateImeSession(ArraySet<Integer> arraySet) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityManagerService) obj).createImeSession((ArraySet) obj2);
            }
        }, this, arraySet));
    }

    public final void createImeSession(ArraySet<Integer> arraySet) {
        synchronized (this.mLock) {
            this.mInputSessionRequested = true;
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
                AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(size);
                if (!arraySet.contains(Integer.valueOf(accessibilityServiceConnection.mId)) && accessibilityServiceConnection.requestImeApis()) {
                    accessibilityServiceConnection.createImeSessionLocked();
                }
            }
        }
    }

    public void scheduleSetImeSessionEnabled(SparseArray<IAccessibilityInputMethodSession> sparseArray, boolean z) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda27
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((AccessibilityManagerService) obj).setImeSessionEnabled((SparseArray) obj2, ((Boolean) obj3).booleanValue());
            }
        }, this, sparseArray, Boolean.valueOf(z)));
    }

    public final void setImeSessionEnabled(SparseArray<IAccessibilityInputMethodSession> sparseArray, boolean z) {
        synchronized (this.mLock) {
            AccessibilityUserState currentUserStateLocked = getCurrentUserStateLocked();
            for (int size = currentUserStateLocked.mBoundServices.size() - 1; size >= 0; size--) {
                AccessibilityServiceConnection accessibilityServiceConnection = currentUserStateLocked.mBoundServices.get(size);
                if (sparseArray.contains(accessibilityServiceConnection.mId) && accessibilityServiceConnection.requestImeApis()) {
                    accessibilityServiceConnection.setImeSessionEnabledLocked(sparseArray.get(accessibilityServiceConnection.mId), z);
                }
            }
        }
    }

    public void injectInputEventToInputFilter(InputEvent inputEvent) {
        AccessibilityInputFilter accessibilityInputFilter;
        synchronized (this.mLock) {
            long uptimeMillis = SystemClock.uptimeMillis() + 1000;
            while (!this.mInputFilterInstalled && SystemClock.uptimeMillis() < uptimeMillis) {
                try {
                    this.mLock.wait(uptimeMillis - SystemClock.uptimeMillis());
                } catch (InterruptedException unused) {
                }
            }
        }
        if (this.mInputFilterInstalled && (accessibilityInputFilter = this.mInputFilter) != null) {
            accessibilityInputFilter.onInputEvent(inputEvent, 1090519040);
        } else {
            Slog.w("AccessibilityManagerService", "Cannot injectInputEventToInputFilter because the AccessibilityInputFilter is not installed.");
        }
    }

    /* loaded from: classes.dex */
    public final class SendWindowStateChangedEventRunnable implements Runnable {
        public final AccessibilityEvent mPendingEvent;
        public final int mWindowId;

        public SendWindowStateChangedEventRunnable(AccessibilityEvent accessibilityEvent) {
            AccessibilityManagerService.this = r1;
            this.mPendingEvent = accessibilityEvent;
            this.mWindowId = accessibilityEvent.getWindowId();
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (AccessibilityManagerService.this.mLock) {
                Slog.w("AccessibilityManagerService", " wait for adding window timeout: " + this.mWindowId);
                sendPendingEventLocked();
            }
        }

        public final void sendPendingEventLocked() {
            AccessibilityManagerService.this.mSendWindowStateChangedEventRunnables.remove(this);
            AccessibilityManagerService.this.dispatchAccessibilityEventLocked(this.mPendingEvent);
        }

        public final int getWindowId() {
            return this.mWindowId;
        }
    }

    public void sendPendingWindowStateChangedEventsForAvailableWindowLocked(int i) {
        for (int size = this.mSendWindowStateChangedEventRunnables.size() - 1; size >= 0; size--) {
            SendWindowStateChangedEventRunnable sendWindowStateChangedEventRunnable = this.mSendWindowStateChangedEventRunnables.get(size);
            if (sendWindowStateChangedEventRunnable.getWindowId() == i) {
                this.mMainHandler.removeCallbacks(sendWindowStateChangedEventRunnable);
                sendWindowStateChangedEventRunnable.sendPendingEventLocked();
            }
        }
    }

    public final boolean postponeWindowStateEvent(AccessibilityEvent accessibilityEvent) {
        synchronized (this.mLock) {
            if (this.mA11yWindowManager.findWindowInfoByIdLocked(this.mA11yWindowManager.resolveParentWindowIdLocked(accessibilityEvent.getWindowId())) != null) {
                return false;
            }
            SendWindowStateChangedEventRunnable sendWindowStateChangedEventRunnable = new SendWindowStateChangedEventRunnable(new AccessibilityEvent(accessibilityEvent));
            this.mMainHandler.postDelayed(sendWindowStateChangedEventRunnable, 500L);
            this.mSendWindowStateChangedEventRunnables.add(sendWindowStateChangedEventRunnable);
            return true;
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void attachAccessibilityOverlayToDisplay(int i, SurfaceControl surfaceControl) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityManagerService$$ExternalSyntheticLambda6
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((AccessibilityManagerService) obj).attachAccessibilityOverlayToDisplayInternal(((Integer) obj2).intValue(), (SurfaceControl) obj3);
            }
        }, this, Integer.valueOf(i), surfaceControl));
    }

    public void attachAccessibilityOverlayToDisplayInternal(int i, SurfaceControl surfaceControl) {
        if (!this.mA11yOverlayLayers.contains(i)) {
            this.mA11yOverlayLayers.put(i, this.mWindowManagerService.getA11yOverlayLayer(i));
        }
        SurfaceControl surfaceControl2 = this.mA11yOverlayLayers.get(i);
        if (surfaceControl2 == null) {
            Slog.e("AccessibilityManagerService", "Unable to get accessibility overlay SurfaceControl.");
            this.mA11yOverlayLayers.remove(i);
            return;
        }
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        transaction.reparent(surfaceControl, surfaceControl2);
        transaction.apply();
        transaction.close();
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection.SystemSupport
    public void setCurrentUserFocusAppearance(int i, int i2) {
        synchronized (this.mLock) {
            getCurrentUserStateLocked().setFocusAppearanceLocked(i, i2);
        }
    }
}
