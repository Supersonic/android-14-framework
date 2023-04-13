package android.view;

import android.app.IAssistDataReceiver;
import android.content.ComponentName;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.IRemoteCallback;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.text.TextUtils;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.ICrossWindowBlurEnabledListener;
import android.view.IDisplayChangeWindowController;
import android.view.IDisplayFoldListener;
import android.view.IDisplayWindowInsetsController;
import android.view.IDisplayWindowListener;
import android.view.IOnKeyguardExitResult;
import android.view.IPinnedTaskListener;
import android.view.IRotationWatcher;
import android.view.IScrollCaptureResponseListener;
import android.view.ISystemGestureExclusionListener;
import android.view.IWallpaperVisibilityListener;
import android.view.IWindow;
import android.view.IWindowSession;
import android.view.IWindowSessionCallback;
import android.view.displayhash.DisplayHash;
import android.view.displayhash.VerifiedDisplayHash;
import android.window.AddToSurfaceSyncGroupResult;
import android.window.ISurfaceSyncGroupCompletedListener;
import android.window.ITaskFpsCallback;
import android.window.ScreenCapture;
import com.android.internal.p028os.IResultReceiver;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardLockedStateListener;
import com.android.internal.policy.IShortcutService;
import java.util.List;
/* loaded from: classes4.dex */
public interface IWindowManager extends IInterface {
    public static final int FIXED_TO_USER_ROTATION_DEFAULT = 0;
    public static final int FIXED_TO_USER_ROTATION_DISABLED = 1;
    public static final int FIXED_TO_USER_ROTATION_ENABLED = 2;

    void addKeyguardLockedStateListener(IKeyguardLockedStateListener iKeyguardLockedStateListener) throws RemoteException;

    SurfaceControl addShellRoot(int i, IWindow iWindow, int i2) throws RemoteException;

    boolean addToSurfaceSyncGroup(IBinder iBinder, boolean z, ISurfaceSyncGroupCompletedListener iSurfaceSyncGroupCompletedListener, AddToSurfaceSyncGroupResult addToSurfaceSyncGroupResult) throws RemoteException;

    void addWindowToken(IBinder iBinder, int i, int i2, Bundle bundle) throws RemoteException;

    Configuration attachToDisplayContent(IBinder iBinder, int i) throws RemoteException;

    Configuration attachWindowContextToDisplayArea(IBinder iBinder, int i, int i2, Bundle bundle) throws RemoteException;

    void attachWindowContextToWindowToken(IBinder iBinder, IBinder iBinder2) throws RemoteException;

    void captureDisplay(int i, ScreenCapture.CaptureArgs captureArgs, ScreenCapture.ScreenCaptureListener screenCaptureListener) throws RemoteException;

    void clearForcedDisplayDensityForUser(int i, int i2) throws RemoteException;

    void clearForcedDisplaySize(int i) throws RemoteException;

    void clearTaskTransitionSpec() throws RemoteException;

    boolean clearWindowContentFrameStats(IBinder iBinder) throws RemoteException;

    void closeSystemDialogs(String str) throws RemoteException;

    void createInputConsumer(IBinder iBinder, String str, int i, InputChannel inputChannel) throws RemoteException;

    boolean destroyInputConsumer(String str, int i) throws RemoteException;

    void detachWindowContextFromWindowContainer(IBinder iBinder) throws RemoteException;

    @Deprecated
    void disableKeyguard(IBinder iBinder, String str, int i) throws RemoteException;

    void dismissKeyguard(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) throws RemoteException;

    @Deprecated
    void endProlongedAnimations() throws RemoteException;

    void exitKeyguardSecurely(IOnKeyguardExitResult iOnKeyguardExitResult) throws RemoteException;

    void freezeDisplayRotation(int i, int i2) throws RemoteException;

    void freezeRotation(int i) throws RemoteException;

    float getAnimationScale(int i) throws RemoteException;

    float[] getAnimationScales() throws RemoteException;

    int getBaseDisplayDensity(int i) throws RemoteException;

    void getBaseDisplaySize(int i, Point point) throws RemoteException;

    float getCurrentAnimatorScale() throws RemoteException;

    Region getCurrentImeTouchRegion() throws RemoteException;

    int getDefaultDisplayRotation() throws RemoteException;

    int getDisplayIdByUniqueId(String str) throws RemoteException;

    int getDisplayImePolicy(int i) throws RemoteException;

    int getDockedStackSide() throws RemoteException;

    int getImeDisplayId() throws RemoteException;

    int getInitialDisplayDensity(int i) throws RemoteException;

    void getInitialDisplaySize(int i, Point point) throws RemoteException;

    int getLetterboxBackgroundColorInArgb() throws RemoteException;

    List<DisplayInfo> getPossibleDisplayInfo(int i) throws RemoteException;

    int getPreferredOptionsPanelGravity(int i) throws RemoteException;

    int getRemoveContentMode(int i) throws RemoteException;

    void getStableInsets(int i, Rect rect) throws RemoteException;

    String[] getSupportedDisplayHashAlgorithms() throws RemoteException;

    WindowContentFrameStats getWindowContentFrameStats(IBinder iBinder) throws RemoteException;

    boolean getWindowInsets(int i, IBinder iBinder, InsetsState insetsState) throws RemoteException;

    int getWindowingMode(int i) throws RemoteException;

    boolean hasNavigationBar(int i) throws RemoteException;

    void hideTransientBars(int i) throws RemoteException;

    void holdLock(IBinder iBinder, int i) throws RemoteException;

    boolean isDisplayRotationFrozen(int i) throws RemoteException;

    boolean isGlobalKey(int i) throws RemoteException;

    boolean isInTouchMode(int i) throws RemoteException;

    boolean isKeyguardLocked() throws RemoteException;

    boolean isKeyguardSecure(int i) throws RemoteException;

    boolean isLayerTracing() throws RemoteException;

    boolean isLetterboxBackgroundMultiColored() throws RemoteException;

    boolean isRotationFrozen() throws RemoteException;

    boolean isSafeModeEnabled() throws RemoteException;

    boolean isTaskSnapshotSupported() throws RemoteException;

    boolean isTransitionTraceEnabled() throws RemoteException;

    boolean isViewServerRunning() throws RemoteException;

    boolean isWindowToken(IBinder iBinder) throws RemoteException;

    boolean isWindowTraceEnabled() throws RemoteException;

    void lockNow(Bundle bundle) throws RemoteException;

    void markSurfaceSyncGroupReady(IBinder iBinder) throws RemoteException;

    boolean mirrorDisplay(int i, SurfaceControl surfaceControl) throws RemoteException;

    SurfaceControl mirrorWallpaperSurface(int i) throws RemoteException;

    List<ComponentName> notifyScreenshotListeners(int i) throws RemoteException;

    IWindowSession openSession(IWindowSessionCallback iWindowSessionCallback) throws RemoteException;

    void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture iAppTransitionAnimationSpecsFuture, IRemoteCallback iRemoteCallback, boolean z, int i) throws RemoteException;

    void overridePendingAppTransitionRemote(RemoteAnimationAdapter remoteAnimationAdapter, int i) throws RemoteException;

    @Deprecated
    void reenableKeyguard(IBinder iBinder, int i) throws RemoteException;

    void refreshScreenCaptureDisabled() throws RemoteException;

    boolean registerCrossWindowBlurEnabledListener(ICrossWindowBlurEnabledListener iCrossWindowBlurEnabledListener) throws RemoteException;

    void registerDisplayFoldListener(IDisplayFoldListener iDisplayFoldListener) throws RemoteException;

    int[] registerDisplayWindowListener(IDisplayWindowListener iDisplayWindowListener) throws RemoteException;

    void registerPinnedTaskListener(int i, IPinnedTaskListener iPinnedTaskListener) throws RemoteException;

    int registerProposedRotationListener(IBinder iBinder, IRotationWatcher iRotationWatcher) throws RemoteException;

    void registerShortcutKey(long j, IShortcutService iShortcutService) throws RemoteException;

    void registerSystemGestureExclusionListener(ISystemGestureExclusionListener iSystemGestureExclusionListener, int i) throws RemoteException;

    void registerTaskFpsCallback(int i, ITaskFpsCallback iTaskFpsCallback) throws RemoteException;

    boolean registerWallpaperVisibilityListener(IWallpaperVisibilityListener iWallpaperVisibilityListener, int i) throws RemoteException;

    void removeKeyguardLockedStateListener(IKeyguardLockedStateListener iKeyguardLockedStateListener) throws RemoteException;

    void removeRotationWatcher(IRotationWatcher iRotationWatcher) throws RemoteException;

    void removeWindowToken(IBinder iBinder, int i) throws RemoteException;

    void requestAppKeyboardShortcuts(IResultReceiver iResultReceiver, int i) throws RemoteException;

    boolean requestAssistScreenshot(IAssistDataReceiver iAssistDataReceiver) throws RemoteException;

    void requestScrollCapture(int i, IBinder iBinder, int i2, IScrollCaptureResponseListener iScrollCaptureResponseListener) throws RemoteException;

    void saveWindowTraceToFile() throws RemoteException;

    Bitmap screenshotWallpaper() throws RemoteException;

    void setActiveTransactionTracing(boolean z) throws RemoteException;

    void setAnimationScale(int i, float f) throws RemoteException;

    void setAnimationScales(float[] fArr) throws RemoteException;

    void setDisplayChangeWindowController(IDisplayChangeWindowController iDisplayChangeWindowController) throws RemoteException;

    void setDisplayHashThrottlingEnabled(boolean z) throws RemoteException;

    void setDisplayImePolicy(int i, int i2) throws RemoteException;

    void setDisplayWindowInsetsController(int i, IDisplayWindowInsetsController iDisplayWindowInsetsController) throws RemoteException;

    void setEventDispatching(boolean z) throws RemoteException;

    void setFixedToUserRotation(int i, int i2) throws RemoteException;

    void setForcedDisplayDensityForUser(int i, int i2, int i3) throws RemoteException;

    void setForcedDisplayScalingMode(int i, int i2) throws RemoteException;

    void setForcedDisplaySize(int i, int i2, int i3) throws RemoteException;

    void setIgnoreOrientationRequest(int i, boolean z) throws RemoteException;

    void setInTouchMode(boolean z, int i) throws RemoteException;

    void setInTouchModeOnAllDisplays(boolean z) throws RemoteException;

    void setLayerTracing(boolean z) throws RemoteException;

    void setLayerTracingFlags(int i) throws RemoteException;

    void setNavBarVirtualKeyHapticFeedbackEnabled(boolean z) throws RemoteException;

    void setRecentsAppBehindSystemBars(boolean z) throws RemoteException;

    void setRecentsVisibility(boolean z) throws RemoteException;

    void setRemoveContentMode(int i, int i2) throws RemoteException;

    void setShellRootAccessibilityWindow(int i, int i2, IWindow iWindow) throws RemoteException;

    void setShouldShowSystemDecors(int i, boolean z) throws RemoteException;

    void setShouldShowWithInsecureKeyguard(int i, boolean z) throws RemoteException;

    void setStrictModeVisualIndicatorPreference(String str) throws RemoteException;

    void setSwitchingUser(boolean z) throws RemoteException;

    void setTaskSnapshotEnabled(boolean z) throws RemoteException;

    void setTaskTransitionSpec(TaskTransitionSpec taskTransitionSpec) throws RemoteException;

    void setWindowingMode(int i, int i2) throws RemoteException;

    boolean shouldShowSystemDecors(int i) throws RemoteException;

    boolean shouldShowWithInsecureKeyguard(int i) throws RemoteException;

    void showGlobalActions() throws RemoteException;

    void showStrictModeViolation(boolean z) throws RemoteException;

    Bitmap snapshotTaskForRecents(int i) throws RemoteException;

    void startFreezingScreen(int i, int i2) throws RemoteException;

    void startTransitionTrace() throws RemoteException;

    boolean startViewServer(int i) throws RemoteException;

    void startWindowTrace() throws RemoteException;

    void stopFreezingScreen() throws RemoteException;

    void stopTransitionTrace() throws RemoteException;

    boolean stopViewServer() throws RemoteException;

    void stopWindowTrace() throws RemoteException;

    void syncInputTransactions(boolean z) throws RemoteException;

    void thawDisplayRotation(int i) throws RemoteException;

    void thawRotation() throws RemoteException;

    void unregisterCrossWindowBlurEnabledListener(ICrossWindowBlurEnabledListener iCrossWindowBlurEnabledListener) throws RemoteException;

    void unregisterDisplayFoldListener(IDisplayFoldListener iDisplayFoldListener) throws RemoteException;

    void unregisterDisplayWindowListener(IDisplayWindowListener iDisplayWindowListener) throws RemoteException;

    void unregisterSystemGestureExclusionListener(ISystemGestureExclusionListener iSystemGestureExclusionListener, int i) throws RemoteException;

    void unregisterTaskFpsCallback(ITaskFpsCallback iTaskFpsCallback) throws RemoteException;

    void unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener iWallpaperVisibilityListener, int i) throws RemoteException;

    void updateDisplayWindowRequestedVisibleTypes(int i, int i2) throws RemoteException;

    void updateStaticPrivacyIndicatorBounds(int i, Rect[] rectArr) throws RemoteException;

    boolean useBLAST() throws RemoteException;

    VerifiedDisplayHash verifyDisplayHash(DisplayHash displayHash) throws RemoteException;

    int watchRotation(IRotationWatcher iRotationWatcher, int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IWindowManager {
        @Override // android.view.IWindowManager
        public boolean startViewServer(int port) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public boolean stopViewServer() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public boolean isViewServerRunning() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public IWindowSession openSession(IWindowSessionCallback callback) throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public boolean useBLAST() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void getInitialDisplaySize(int displayId, Point size) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void getBaseDisplaySize(int displayId, Point size) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setForcedDisplaySize(int displayId, int width, int height) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void clearForcedDisplaySize(int displayId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public int getInitialDisplayDensity(int displayId) throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public int getBaseDisplayDensity(int displayId) throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public int getDisplayIdByUniqueId(String uniqueId) throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public void setForcedDisplayDensityForUser(int displayId, int density, int userId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void clearForcedDisplayDensityForUser(int displayId, int userId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setForcedDisplayScalingMode(int displayId, int mode) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setEventDispatching(boolean enabled) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isWindowToken(IBinder binder) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void addWindowToken(IBinder token, int type, int displayId, Bundle options) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void removeWindowToken(IBinder token, int displayId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setDisplayChangeWindowController(IDisplayChangeWindowController controller) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public SurfaceControl addShellRoot(int displayId, IWindow client, int shellRootLayer) throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public void setShellRootAccessibilityWindow(int displayId, int shellRootLayer, IWindow target) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture specsFuture, IRemoteCallback startedCallback, boolean scaleUp, int displayId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void overridePendingAppTransitionRemote(RemoteAnimationAdapter remoteAnimationAdapter, int displayId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void endProlongedAnimations() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void startFreezingScreen(int exitAnim, int enterAnim) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void stopFreezingScreen() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void disableKeyguard(IBinder token, String tag, int userId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void reenableKeyguard(IBinder token, int userId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void exitKeyguardSecurely(IOnKeyguardExitResult callback) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isKeyguardLocked() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public boolean isKeyguardSecure(int userId) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void dismissKeyguard(IKeyguardDismissCallback callback, CharSequence message) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void addKeyguardLockedStateListener(IKeyguardLockedStateListener listener) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void removeKeyguardLockedStateListener(IKeyguardLockedStateListener listener) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setSwitchingUser(boolean switching) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void closeSystemDialogs(String reason) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public float getAnimationScale(int which) throws RemoteException {
            return 0.0f;
        }

        @Override // android.view.IWindowManager
        public float[] getAnimationScales() throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public void setAnimationScale(int which, float scale) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setAnimationScales(float[] scales) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public float getCurrentAnimatorScale() throws RemoteException {
            return 0.0f;
        }

        @Override // android.view.IWindowManager
        public void setInTouchMode(boolean inTouch, int displayId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setInTouchModeOnAllDisplays(boolean inTouch) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isInTouchMode(int displayId) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void showStrictModeViolation(boolean on) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setStrictModeVisualIndicatorPreference(String enabled) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void refreshScreenCaptureDisabled() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public int getDefaultDisplayRotation() throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public int watchRotation(IRotationWatcher watcher, int displayId) throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public void removeRotationWatcher(IRotationWatcher watcher) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public int registerProposedRotationListener(IBinder contextToken, IRotationWatcher listener) throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public int getPreferredOptionsPanelGravity(int displayId) throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public void freezeRotation(int rotation) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void thawRotation() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isRotationFrozen() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void freezeDisplayRotation(int displayId, int rotation) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void thawDisplayRotation(int displayId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isDisplayRotationFrozen(int displayId) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void setFixedToUserRotation(int displayId, int fixedToUserRotation) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setIgnoreOrientationRequest(int displayId, boolean ignoreOrientationRequest) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public Bitmap screenshotWallpaper() throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public SurfaceControl mirrorWallpaperSurface(int displayId) throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public boolean registerWallpaperVisibilityListener(IWallpaperVisibilityListener listener, int displayId) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener listener, int displayId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void registerSystemGestureExclusionListener(ISystemGestureExclusionListener listener, int displayId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void unregisterSystemGestureExclusionListener(ISystemGestureExclusionListener listener, int displayId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean requestAssistScreenshot(IAssistDataReceiver receiver) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void hideTransientBars(int displayId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setRecentsVisibility(boolean visible) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void updateStaticPrivacyIndicatorBounds(int displayId, Rect[] staticBounds) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setNavBarVirtualKeyHapticFeedbackEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean hasNavigationBar(int displayId) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void lockNow(Bundle options) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isSafeModeEnabled() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public boolean clearWindowContentFrameStats(IBinder token) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public WindowContentFrameStats getWindowContentFrameStats(IBinder token) throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public int getDockedStackSide() throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public void registerPinnedTaskListener(int displayId, IPinnedTaskListener listener) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void requestAppKeyboardShortcuts(IResultReceiver receiver, int deviceId) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void getStableInsets(int displayId, Rect outInsets) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void registerShortcutKey(long shortcutCode, IShortcutService keySubscriber) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void createInputConsumer(IBinder token, String name, int displayId, InputChannel inputChannel) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean destroyInputConsumer(String name, int displayId) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public Region getCurrentImeTouchRegion() throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public void registerDisplayFoldListener(IDisplayFoldListener listener) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void unregisterDisplayFoldListener(IDisplayFoldListener listener) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public int[] registerDisplayWindowListener(IDisplayWindowListener listener) throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public void unregisterDisplayWindowListener(IDisplayWindowListener listener) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void startWindowTrace() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void stopWindowTrace() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void saveWindowTraceToFile() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isWindowTraceEnabled() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void startTransitionTrace() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void stopTransitionTrace() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isTransitionTraceEnabled() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public int getWindowingMode(int displayId) throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public void setWindowingMode(int displayId, int mode) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public int getRemoveContentMode(int displayId) throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public void setRemoveContentMode(int displayId, int mode) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean shouldShowWithInsecureKeyguard(int displayId) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void setShouldShowWithInsecureKeyguard(int displayId, boolean shouldShow) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean shouldShowSystemDecors(int displayId) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void setShouldShowSystemDecors(int displayId, boolean shouldShow) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public int getDisplayImePolicy(int displayId) throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public void setDisplayImePolicy(int displayId, int imePolicy) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void syncInputTransactions(boolean waitForAnimations) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isLayerTracing() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void setLayerTracing(boolean enabled) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean mirrorDisplay(int displayId, SurfaceControl outSurfaceControl) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void setDisplayWindowInsetsController(int displayId, IDisplayWindowInsetsController displayWindowInsetsController) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void updateDisplayWindowRequestedVisibleTypes(int displayId, int requestedVisibleTypes) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean getWindowInsets(int displayId, IBinder token, InsetsState outInsetsState) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public List<DisplayInfo> getPossibleDisplayInfo(int displayId) throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public void showGlobalActions() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setLayerTracingFlags(int flags) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setActiveTransactionTracing(boolean active) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void requestScrollCapture(int displayId, IBinder behindClient, int taskId, IScrollCaptureResponseListener listener) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void holdLock(IBinder token, int durationMs) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public String[] getSupportedDisplayHashAlgorithms() throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public VerifiedDisplayHash verifyDisplayHash(DisplayHash displayHash) throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public void setDisplayHashThrottlingEnabled(boolean enable) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public Configuration attachWindowContextToDisplayArea(IBinder clientToken, int type, int displayId, Bundle options) throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public void attachWindowContextToWindowToken(IBinder clientToken, IBinder token) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public Configuration attachToDisplayContent(IBinder clientToken, int displayId) throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public void detachWindowContextFromWindowContainer(IBinder clientToken) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean registerCrossWindowBlurEnabledListener(ICrossWindowBlurEnabledListener listener) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void unregisterCrossWindowBlurEnabledListener(ICrossWindowBlurEnabledListener listener) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isTaskSnapshotSupported() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public int getImeDisplayId() throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public void setTaskSnapshotEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void setTaskTransitionSpec(TaskTransitionSpec spec) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void clearTaskTransitionSpec() throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void registerTaskFpsCallback(int taskId, ITaskFpsCallback callback) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public void unregisterTaskFpsCallback(ITaskFpsCallback listener) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public Bitmap snapshotTaskForRecents(int taskId) throws RemoteException {
            return null;
        }

        @Override // android.view.IWindowManager
        public void setRecentsAppBehindSystemBars(boolean behindSystemBars) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public int getLetterboxBackgroundColorInArgb() throws RemoteException {
            return 0;
        }

        @Override // android.view.IWindowManager
        public boolean isLetterboxBackgroundMultiColored() throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void captureDisplay(int displayId, ScreenCapture.CaptureArgs captureArgs, ScreenCapture.ScreenCaptureListener listener) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public boolean isGlobalKey(int keyCode) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public boolean addToSurfaceSyncGroup(IBinder syncGroupToken, boolean parentSyncGroupMerge, ISurfaceSyncGroupCompletedListener completedListener, AddToSurfaceSyncGroupResult addToSurfaceSyncGroupResult) throws RemoteException {
            return false;
        }

        @Override // android.view.IWindowManager
        public void markSurfaceSyncGroupReady(IBinder syncGroupToken) throws RemoteException {
        }

        @Override // android.view.IWindowManager
        public List<ComponentName> notifyScreenshotListeners(int displayId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IWindowManager {
        public static final String DESCRIPTOR = "android.view.IWindowManager";
        static final int TRANSACTION_addKeyguardLockedStateListener = 34;
        static final int TRANSACTION_addShellRoot = 21;
        static final int TRANSACTION_addToSurfaceSyncGroup = 142;
        static final int TRANSACTION_addWindowToken = 18;
        static final int TRANSACTION_attachToDisplayContent = 125;
        static final int TRANSACTION_attachWindowContextToDisplayArea = 123;
        static final int TRANSACTION_attachWindowContextToWindowToken = 124;
        static final int TRANSACTION_captureDisplay = 140;
        static final int TRANSACTION_clearForcedDisplayDensityForUser = 14;
        static final int TRANSACTION_clearForcedDisplaySize = 9;
        static final int TRANSACTION_clearTaskTransitionSpec = 133;
        static final int TRANSACTION_clearWindowContentFrameStats = 76;
        static final int TRANSACTION_closeSystemDialogs = 37;
        static final int TRANSACTION_createInputConsumer = 83;
        static final int TRANSACTION_destroyInputConsumer = 84;
        static final int TRANSACTION_detachWindowContextFromWindowContainer = 126;
        static final int TRANSACTION_disableKeyguard = 28;
        static final int TRANSACTION_dismissKeyguard = 33;
        static final int TRANSACTION_endProlongedAnimations = 25;
        static final int TRANSACTION_exitKeyguardSecurely = 30;
        static final int TRANSACTION_freezeDisplayRotation = 57;
        static final int TRANSACTION_freezeRotation = 54;
        static final int TRANSACTION_getAnimationScale = 38;
        static final int TRANSACTION_getAnimationScales = 39;
        static final int TRANSACTION_getBaseDisplayDensity = 11;
        static final int TRANSACTION_getBaseDisplaySize = 7;
        static final int TRANSACTION_getCurrentAnimatorScale = 42;
        static final int TRANSACTION_getCurrentImeTouchRegion = 85;
        static final int TRANSACTION_getDefaultDisplayRotation = 49;
        static final int TRANSACTION_getDisplayIdByUniqueId = 12;
        static final int TRANSACTION_getDisplayImePolicy = 105;
        static final int TRANSACTION_getDockedStackSide = 78;
        static final int TRANSACTION_getImeDisplayId = 130;
        static final int TRANSACTION_getInitialDisplayDensity = 10;
        static final int TRANSACTION_getInitialDisplaySize = 6;
        static final int TRANSACTION_getLetterboxBackgroundColorInArgb = 138;
        static final int TRANSACTION_getPossibleDisplayInfo = 114;
        static final int TRANSACTION_getPreferredOptionsPanelGravity = 53;
        static final int TRANSACTION_getRemoveContentMode = 99;
        static final int TRANSACTION_getStableInsets = 81;
        static final int TRANSACTION_getSupportedDisplayHashAlgorithms = 120;
        static final int TRANSACTION_getWindowContentFrameStats = 77;
        static final int TRANSACTION_getWindowInsets = 113;
        static final int TRANSACTION_getWindowingMode = 97;
        static final int TRANSACTION_hasNavigationBar = 73;
        static final int TRANSACTION_hideTransientBars = 69;
        static final int TRANSACTION_holdLock = 119;
        static final int TRANSACTION_isDisplayRotationFrozen = 59;
        static final int TRANSACTION_isGlobalKey = 141;
        static final int TRANSACTION_isInTouchMode = 45;
        static final int TRANSACTION_isKeyguardLocked = 31;
        static final int TRANSACTION_isKeyguardSecure = 32;
        static final int TRANSACTION_isLayerTracing = 108;
        static final int TRANSACTION_isLetterboxBackgroundMultiColored = 139;
        static final int TRANSACTION_isRotationFrozen = 56;
        static final int TRANSACTION_isSafeModeEnabled = 75;
        static final int TRANSACTION_isTaskSnapshotSupported = 129;
        static final int TRANSACTION_isTransitionTraceEnabled = 96;
        static final int TRANSACTION_isViewServerRunning = 3;
        static final int TRANSACTION_isWindowToken = 17;
        static final int TRANSACTION_isWindowTraceEnabled = 93;
        static final int TRANSACTION_lockNow = 74;
        static final int TRANSACTION_markSurfaceSyncGroupReady = 143;
        static final int TRANSACTION_mirrorDisplay = 110;
        static final int TRANSACTION_mirrorWallpaperSurface = 63;
        static final int TRANSACTION_notifyScreenshotListeners = 144;
        static final int TRANSACTION_openSession = 4;
        static final int TRANSACTION_overridePendingAppTransitionMultiThumbFuture = 23;
        static final int TRANSACTION_overridePendingAppTransitionRemote = 24;
        static final int TRANSACTION_reenableKeyguard = 29;
        static final int TRANSACTION_refreshScreenCaptureDisabled = 48;
        static final int TRANSACTION_registerCrossWindowBlurEnabledListener = 127;
        static final int TRANSACTION_registerDisplayFoldListener = 86;
        static final int TRANSACTION_registerDisplayWindowListener = 88;
        static final int TRANSACTION_registerPinnedTaskListener = 79;
        static final int TRANSACTION_registerProposedRotationListener = 52;
        static final int TRANSACTION_registerShortcutKey = 82;
        static final int TRANSACTION_registerSystemGestureExclusionListener = 66;
        static final int TRANSACTION_registerTaskFpsCallback = 134;
        static final int TRANSACTION_registerWallpaperVisibilityListener = 64;
        static final int TRANSACTION_removeKeyguardLockedStateListener = 35;
        static final int TRANSACTION_removeRotationWatcher = 51;
        static final int TRANSACTION_removeWindowToken = 19;
        static final int TRANSACTION_requestAppKeyboardShortcuts = 80;
        static final int TRANSACTION_requestAssistScreenshot = 68;
        static final int TRANSACTION_requestScrollCapture = 118;
        static final int TRANSACTION_saveWindowTraceToFile = 92;
        static final int TRANSACTION_screenshotWallpaper = 62;
        static final int TRANSACTION_setActiveTransactionTracing = 117;
        static final int TRANSACTION_setAnimationScale = 40;
        static final int TRANSACTION_setAnimationScales = 41;
        static final int TRANSACTION_setDisplayChangeWindowController = 20;
        static final int TRANSACTION_setDisplayHashThrottlingEnabled = 122;
        static final int TRANSACTION_setDisplayImePolicy = 106;
        static final int TRANSACTION_setDisplayWindowInsetsController = 111;
        static final int TRANSACTION_setEventDispatching = 16;
        static final int TRANSACTION_setFixedToUserRotation = 60;
        static final int TRANSACTION_setForcedDisplayDensityForUser = 13;
        static final int TRANSACTION_setForcedDisplayScalingMode = 15;
        static final int TRANSACTION_setForcedDisplaySize = 8;
        static final int TRANSACTION_setIgnoreOrientationRequest = 61;
        static final int TRANSACTION_setInTouchMode = 43;
        static final int TRANSACTION_setInTouchModeOnAllDisplays = 44;
        static final int TRANSACTION_setLayerTracing = 109;
        static final int TRANSACTION_setLayerTracingFlags = 116;
        static final int TRANSACTION_setNavBarVirtualKeyHapticFeedbackEnabled = 72;
        static final int TRANSACTION_setRecentsAppBehindSystemBars = 137;
        static final int TRANSACTION_setRecentsVisibility = 70;
        static final int TRANSACTION_setRemoveContentMode = 100;
        static final int TRANSACTION_setShellRootAccessibilityWindow = 22;
        static final int TRANSACTION_setShouldShowSystemDecors = 104;
        static final int TRANSACTION_setShouldShowWithInsecureKeyguard = 102;
        static final int TRANSACTION_setStrictModeVisualIndicatorPreference = 47;
        static final int TRANSACTION_setSwitchingUser = 36;
        static final int TRANSACTION_setTaskSnapshotEnabled = 131;
        static final int TRANSACTION_setTaskTransitionSpec = 132;
        static final int TRANSACTION_setWindowingMode = 98;
        static final int TRANSACTION_shouldShowSystemDecors = 103;
        static final int TRANSACTION_shouldShowWithInsecureKeyguard = 101;
        static final int TRANSACTION_showGlobalActions = 115;
        static final int TRANSACTION_showStrictModeViolation = 46;
        static final int TRANSACTION_snapshotTaskForRecents = 136;
        static final int TRANSACTION_startFreezingScreen = 26;
        static final int TRANSACTION_startTransitionTrace = 94;
        static final int TRANSACTION_startViewServer = 1;
        static final int TRANSACTION_startWindowTrace = 90;
        static final int TRANSACTION_stopFreezingScreen = 27;
        static final int TRANSACTION_stopTransitionTrace = 95;
        static final int TRANSACTION_stopViewServer = 2;
        static final int TRANSACTION_stopWindowTrace = 91;
        static final int TRANSACTION_syncInputTransactions = 107;
        static final int TRANSACTION_thawDisplayRotation = 58;
        static final int TRANSACTION_thawRotation = 55;
        static final int TRANSACTION_unregisterCrossWindowBlurEnabledListener = 128;
        static final int TRANSACTION_unregisterDisplayFoldListener = 87;
        static final int TRANSACTION_unregisterDisplayWindowListener = 89;
        static final int TRANSACTION_unregisterSystemGestureExclusionListener = 67;
        static final int TRANSACTION_unregisterTaskFpsCallback = 135;
        static final int TRANSACTION_unregisterWallpaperVisibilityListener = 65;
        static final int TRANSACTION_updateDisplayWindowRequestedVisibleTypes = 112;
        static final int TRANSACTION_updateStaticPrivacyIndicatorBounds = 71;
        static final int TRANSACTION_useBLAST = 5;
        static final int TRANSACTION_verifyDisplayHash = 121;
        static final int TRANSACTION_watchRotation = 50;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWindowManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWindowManager)) {
                return (IWindowManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "startViewServer";
                case 2:
                    return "stopViewServer";
                case 3:
                    return "isViewServerRunning";
                case 4:
                    return "openSession";
                case 5:
                    return "useBLAST";
                case 6:
                    return "getInitialDisplaySize";
                case 7:
                    return "getBaseDisplaySize";
                case 8:
                    return "setForcedDisplaySize";
                case 9:
                    return "clearForcedDisplaySize";
                case 10:
                    return "getInitialDisplayDensity";
                case 11:
                    return "getBaseDisplayDensity";
                case 12:
                    return "getDisplayIdByUniqueId";
                case 13:
                    return "setForcedDisplayDensityForUser";
                case 14:
                    return "clearForcedDisplayDensityForUser";
                case 15:
                    return "setForcedDisplayScalingMode";
                case 16:
                    return "setEventDispatching";
                case 17:
                    return "isWindowToken";
                case 18:
                    return "addWindowToken";
                case 19:
                    return "removeWindowToken";
                case 20:
                    return "setDisplayChangeWindowController";
                case 21:
                    return "addShellRoot";
                case 22:
                    return "setShellRootAccessibilityWindow";
                case 23:
                    return "overridePendingAppTransitionMultiThumbFuture";
                case 24:
                    return "overridePendingAppTransitionRemote";
                case 25:
                    return "endProlongedAnimations";
                case 26:
                    return "startFreezingScreen";
                case 27:
                    return "stopFreezingScreen";
                case 28:
                    return "disableKeyguard";
                case 29:
                    return "reenableKeyguard";
                case 30:
                    return "exitKeyguardSecurely";
                case 31:
                    return "isKeyguardLocked";
                case 32:
                    return "isKeyguardSecure";
                case 33:
                    return "dismissKeyguard";
                case 34:
                    return "addKeyguardLockedStateListener";
                case 35:
                    return "removeKeyguardLockedStateListener";
                case 36:
                    return "setSwitchingUser";
                case 37:
                    return "closeSystemDialogs";
                case 38:
                    return "getAnimationScale";
                case 39:
                    return "getAnimationScales";
                case 40:
                    return "setAnimationScale";
                case 41:
                    return "setAnimationScales";
                case 42:
                    return "getCurrentAnimatorScale";
                case 43:
                    return "setInTouchMode";
                case 44:
                    return "setInTouchModeOnAllDisplays";
                case 45:
                    return "isInTouchMode";
                case 46:
                    return "showStrictModeViolation";
                case 47:
                    return "setStrictModeVisualIndicatorPreference";
                case 48:
                    return "refreshScreenCaptureDisabled";
                case 49:
                    return "getDefaultDisplayRotation";
                case 50:
                    return "watchRotation";
                case 51:
                    return "removeRotationWatcher";
                case 52:
                    return "registerProposedRotationListener";
                case 53:
                    return "getPreferredOptionsPanelGravity";
                case 54:
                    return "freezeRotation";
                case 55:
                    return "thawRotation";
                case 56:
                    return "isRotationFrozen";
                case 57:
                    return "freezeDisplayRotation";
                case 58:
                    return "thawDisplayRotation";
                case 59:
                    return "isDisplayRotationFrozen";
                case 60:
                    return "setFixedToUserRotation";
                case 61:
                    return "setIgnoreOrientationRequest";
                case 62:
                    return "screenshotWallpaper";
                case 63:
                    return "mirrorWallpaperSurface";
                case 64:
                    return "registerWallpaperVisibilityListener";
                case 65:
                    return "unregisterWallpaperVisibilityListener";
                case 66:
                    return "registerSystemGestureExclusionListener";
                case 67:
                    return "unregisterSystemGestureExclusionListener";
                case 68:
                    return "requestAssistScreenshot";
                case 69:
                    return "hideTransientBars";
                case 70:
                    return "setRecentsVisibility";
                case 71:
                    return "updateStaticPrivacyIndicatorBounds";
                case 72:
                    return "setNavBarVirtualKeyHapticFeedbackEnabled";
                case 73:
                    return "hasNavigationBar";
                case 74:
                    return "lockNow";
                case 75:
                    return "isSafeModeEnabled";
                case 76:
                    return "clearWindowContentFrameStats";
                case 77:
                    return "getWindowContentFrameStats";
                case 78:
                    return "getDockedStackSide";
                case 79:
                    return "registerPinnedTaskListener";
                case 80:
                    return "requestAppKeyboardShortcuts";
                case 81:
                    return "getStableInsets";
                case 82:
                    return "registerShortcutKey";
                case 83:
                    return "createInputConsumer";
                case 84:
                    return "destroyInputConsumer";
                case 85:
                    return "getCurrentImeTouchRegion";
                case 86:
                    return "registerDisplayFoldListener";
                case 87:
                    return "unregisterDisplayFoldListener";
                case 88:
                    return "registerDisplayWindowListener";
                case 89:
                    return "unregisterDisplayWindowListener";
                case 90:
                    return "startWindowTrace";
                case 91:
                    return "stopWindowTrace";
                case 92:
                    return "saveWindowTraceToFile";
                case 93:
                    return "isWindowTraceEnabled";
                case 94:
                    return "startTransitionTrace";
                case 95:
                    return "stopTransitionTrace";
                case 96:
                    return "isTransitionTraceEnabled";
                case 97:
                    return "getWindowingMode";
                case 98:
                    return "setWindowingMode";
                case 99:
                    return "getRemoveContentMode";
                case 100:
                    return "setRemoveContentMode";
                case 101:
                    return "shouldShowWithInsecureKeyguard";
                case 102:
                    return "setShouldShowWithInsecureKeyguard";
                case 103:
                    return "shouldShowSystemDecors";
                case 104:
                    return "setShouldShowSystemDecors";
                case 105:
                    return "getDisplayImePolicy";
                case 106:
                    return "setDisplayImePolicy";
                case 107:
                    return "syncInputTransactions";
                case 108:
                    return "isLayerTracing";
                case 109:
                    return "setLayerTracing";
                case 110:
                    return "mirrorDisplay";
                case 111:
                    return "setDisplayWindowInsetsController";
                case 112:
                    return "updateDisplayWindowRequestedVisibleTypes";
                case 113:
                    return "getWindowInsets";
                case 114:
                    return "getPossibleDisplayInfo";
                case 115:
                    return "showGlobalActions";
                case 116:
                    return "setLayerTracingFlags";
                case 117:
                    return "setActiveTransactionTracing";
                case 118:
                    return "requestScrollCapture";
                case 119:
                    return "holdLock";
                case 120:
                    return "getSupportedDisplayHashAlgorithms";
                case 121:
                    return "verifyDisplayHash";
                case 122:
                    return "setDisplayHashThrottlingEnabled";
                case 123:
                    return "attachWindowContextToDisplayArea";
                case 124:
                    return "attachWindowContextToWindowToken";
                case 125:
                    return "attachToDisplayContent";
                case 126:
                    return "detachWindowContextFromWindowContainer";
                case 127:
                    return "registerCrossWindowBlurEnabledListener";
                case 128:
                    return "unregisterCrossWindowBlurEnabledListener";
                case 129:
                    return "isTaskSnapshotSupported";
                case 130:
                    return "getImeDisplayId";
                case 131:
                    return "setTaskSnapshotEnabled";
                case 132:
                    return "setTaskTransitionSpec";
                case 133:
                    return "clearTaskTransitionSpec";
                case 134:
                    return "registerTaskFpsCallback";
                case 135:
                    return "unregisterTaskFpsCallback";
                case 136:
                    return "snapshotTaskForRecents";
                case 137:
                    return "setRecentsAppBehindSystemBars";
                case 138:
                    return "getLetterboxBackgroundColorInArgb";
                case 139:
                    return "isLetterboxBackgroundMultiColored";
                case 140:
                    return "captureDisplay";
                case 141:
                    return "isGlobalKey";
                case 142:
                    return "addToSurfaceSyncGroup";
                case 143:
                    return "markSurfaceSyncGroupReady";
                case 144:
                    return "notifyScreenshotListeners";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = startViewServer(_arg0);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            boolean _result2 = stopViewServer();
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 3:
                            boolean _result3 = isViewServerRunning();
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            IWindowSessionCallback _arg02 = IWindowSessionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            IWindowSession _result4 = openSession(_arg02);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result4);
                            break;
                        case 5:
                            boolean _result5 = useBLAST();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 6:
                            int _arg03 = data.readInt();
                            Point _arg1 = new Point();
                            data.enforceNoDataAvail();
                            getInitialDisplaySize(_arg03, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_arg1, 1);
                            break;
                        case 7:
                            int _arg04 = data.readInt();
                            Point _arg12 = new Point();
                            data.enforceNoDataAvail();
                            getBaseDisplaySize(_arg04, _arg12);
                            reply.writeNoException();
                            reply.writeTypedObject(_arg12, 1);
                            break;
                        case 8:
                            int _arg05 = data.readInt();
                            int _arg13 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            setForcedDisplaySize(_arg05, _arg13, _arg2);
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            clearForcedDisplaySize(_arg06);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result6 = getInitialDisplayDensity(_arg07);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 11:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result7 = getBaseDisplayDensity(_arg08);
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 12:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            int _result8 = getDisplayIdByUniqueId(_arg09);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 13:
                            int _arg010 = data.readInt();
                            int _arg14 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            setForcedDisplayDensityForUser(_arg010, _arg14, _arg22);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg011 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            clearForcedDisplayDensityForUser(_arg011, _arg15);
                            reply.writeNoException();
                            break;
                        case 15:
                            int _arg012 = data.readInt();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            setForcedDisplayScalingMode(_arg012, _arg16);
                            reply.writeNoException();
                            break;
                        case 16:
                            boolean _arg013 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setEventDispatching(_arg013);
                            reply.writeNoException();
                            break;
                        case 17:
                            IBinder _arg014 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            boolean _result9 = isWindowToken(_arg014);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 18:
                            IBinder _arg015 = data.readStrongBinder();
                            int _arg17 = data.readInt();
                            int _arg23 = data.readInt();
                            Bundle _arg3 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            addWindowToken(_arg015, _arg17, _arg23, _arg3);
                            reply.writeNoException();
                            break;
                        case 19:
                            IBinder _arg016 = data.readStrongBinder();
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            removeWindowToken(_arg016, _arg18);
                            reply.writeNoException();
                            break;
                        case 20:
                            IDisplayChangeWindowController _arg017 = IDisplayChangeWindowController.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setDisplayChangeWindowController(_arg017);
                            reply.writeNoException();
                            break;
                        case 21:
                            int _arg018 = data.readInt();
                            IWindow _arg19 = IWindow.Stub.asInterface(data.readStrongBinder());
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            SurfaceControl _result10 = addShellRoot(_arg018, _arg19, _arg24);
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            break;
                        case 22:
                            int _arg019 = data.readInt();
                            int _arg110 = data.readInt();
                            IWindow _arg25 = IWindow.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setShellRootAccessibilityWindow(_arg019, _arg110, _arg25);
                            reply.writeNoException();
                            break;
                        case 23:
                            IAppTransitionAnimationSpecsFuture _arg020 = IAppTransitionAnimationSpecsFuture.Stub.asInterface(data.readStrongBinder());
                            IRemoteCallback _arg111 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                            boolean _arg26 = data.readBoolean();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            overridePendingAppTransitionMultiThumbFuture(_arg020, _arg111, _arg26, _arg32);
                            reply.writeNoException();
                            break;
                        case 24:
                            RemoteAnimationAdapter _arg021 = (RemoteAnimationAdapter) data.readTypedObject(RemoteAnimationAdapter.CREATOR);
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            overridePendingAppTransitionRemote(_arg021, _arg112);
                            reply.writeNoException();
                            break;
                        case 25:
                            endProlongedAnimations();
                            reply.writeNoException();
                            break;
                        case 26:
                            int _arg022 = data.readInt();
                            int _arg113 = data.readInt();
                            data.enforceNoDataAvail();
                            startFreezingScreen(_arg022, _arg113);
                            reply.writeNoException();
                            break;
                        case 27:
                            stopFreezingScreen();
                            reply.writeNoException();
                            break;
                        case 28:
                            IBinder _arg023 = data.readStrongBinder();
                            String _arg114 = data.readString();
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            disableKeyguard(_arg023, _arg114, _arg27);
                            reply.writeNoException();
                            break;
                        case 29:
                            IBinder _arg024 = data.readStrongBinder();
                            int _arg115 = data.readInt();
                            data.enforceNoDataAvail();
                            reenableKeyguard(_arg024, _arg115);
                            reply.writeNoException();
                            break;
                        case 30:
                            IOnKeyguardExitResult _arg025 = IOnKeyguardExitResult.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            exitKeyguardSecurely(_arg025);
                            reply.writeNoException();
                            break;
                        case 31:
                            boolean _result11 = isKeyguardLocked();
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 32:
                            int _arg026 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result12 = isKeyguardSecure(_arg026);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        case 33:
                            IKeyguardDismissCallback _arg027 = IKeyguardDismissCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            dismissKeyguard(_arg027, (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR));
                            reply.writeNoException();
                            break;
                        case 34:
                            IKeyguardLockedStateListener _arg028 = IKeyguardLockedStateListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addKeyguardLockedStateListener(_arg028);
                            reply.writeNoException();
                            break;
                        case 35:
                            IKeyguardLockedStateListener _arg029 = IKeyguardLockedStateListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeKeyguardLockedStateListener(_arg029);
                            reply.writeNoException();
                            break;
                        case 36:
                            boolean _arg030 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSwitchingUser(_arg030);
                            reply.writeNoException();
                            break;
                        case 37:
                            String _arg031 = data.readString();
                            data.enforceNoDataAvail();
                            closeSystemDialogs(_arg031);
                            reply.writeNoException();
                            break;
                        case 38:
                            int _arg032 = data.readInt();
                            data.enforceNoDataAvail();
                            float _result13 = getAnimationScale(_arg032);
                            reply.writeNoException();
                            reply.writeFloat(_result13);
                            break;
                        case 39:
                            float[] _result14 = getAnimationScales();
                            reply.writeNoException();
                            reply.writeFloatArray(_result14);
                            break;
                        case 40:
                            int _arg033 = data.readInt();
                            float _arg116 = data.readFloat();
                            data.enforceNoDataAvail();
                            setAnimationScale(_arg033, _arg116);
                            reply.writeNoException();
                            break;
                        case 41:
                            float[] _arg034 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            setAnimationScales(_arg034);
                            reply.writeNoException();
                            break;
                        case 42:
                            float _result15 = getCurrentAnimatorScale();
                            reply.writeNoException();
                            reply.writeFloat(_result15);
                            break;
                        case 43:
                            boolean _arg035 = data.readBoolean();
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            setInTouchMode(_arg035, _arg117);
                            reply.writeNoException();
                            break;
                        case 44:
                            boolean _arg036 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setInTouchModeOnAllDisplays(_arg036);
                            reply.writeNoException();
                            break;
                        case 45:
                            int _arg037 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result16 = isInTouchMode(_arg037);
                            reply.writeNoException();
                            reply.writeBoolean(_result16);
                            break;
                        case 46:
                            boolean _arg038 = data.readBoolean();
                            data.enforceNoDataAvail();
                            showStrictModeViolation(_arg038);
                            reply.writeNoException();
                            break;
                        case 47:
                            String _arg039 = data.readString();
                            data.enforceNoDataAvail();
                            setStrictModeVisualIndicatorPreference(_arg039);
                            reply.writeNoException();
                            break;
                        case 48:
                            refreshScreenCaptureDisabled();
                            reply.writeNoException();
                            break;
                        case 49:
                            int _result17 = getDefaultDisplayRotation();
                            reply.writeNoException();
                            reply.writeInt(_result17);
                            break;
                        case 50:
                            IRotationWatcher _arg040 = IRotationWatcher.Stub.asInterface(data.readStrongBinder());
                            int _arg118 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result18 = watchRotation(_arg040, _arg118);
                            reply.writeNoException();
                            reply.writeInt(_result18);
                            break;
                        case 51:
                            IBinder _arg041 = data.readStrongBinder();
                            IRotationWatcher _arg042 = IRotationWatcher.Stub.asInterface(_arg041);
                            data.enforceNoDataAvail();
                            removeRotationWatcher(_arg042);
                            reply.writeNoException();
                            break;
                        case 52:
                            IBinder _arg043 = data.readStrongBinder();
                            IRotationWatcher _arg119 = IRotationWatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result19 = registerProposedRotationListener(_arg043, _arg119);
                            reply.writeNoException();
                            reply.writeInt(_result19);
                            break;
                        case 53:
                            int _arg044 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result20 = getPreferredOptionsPanelGravity(_arg044);
                            reply.writeNoException();
                            reply.writeInt(_result20);
                            break;
                        case 54:
                            int _arg045 = data.readInt();
                            data.enforceNoDataAvail();
                            freezeRotation(_arg045);
                            reply.writeNoException();
                            break;
                        case 55:
                            thawRotation();
                            reply.writeNoException();
                            break;
                        case 56:
                            boolean _result21 = isRotationFrozen();
                            reply.writeNoException();
                            reply.writeBoolean(_result21);
                            break;
                        case 57:
                            int _arg046 = data.readInt();
                            int _arg120 = data.readInt();
                            data.enforceNoDataAvail();
                            freezeDisplayRotation(_arg046, _arg120);
                            reply.writeNoException();
                            break;
                        case 58:
                            int _arg047 = data.readInt();
                            data.enforceNoDataAvail();
                            thawDisplayRotation(_arg047);
                            reply.writeNoException();
                            break;
                        case 59:
                            int _arg048 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result22 = isDisplayRotationFrozen(_arg048);
                            reply.writeNoException();
                            reply.writeBoolean(_result22);
                            break;
                        case 60:
                            int _arg049 = data.readInt();
                            int _arg121 = data.readInt();
                            data.enforceNoDataAvail();
                            setFixedToUserRotation(_arg049, _arg121);
                            reply.writeNoException();
                            break;
                        case 61:
                            int _arg050 = data.readInt();
                            boolean _arg122 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setIgnoreOrientationRequest(_arg050, _arg122);
                            reply.writeNoException();
                            break;
                        case 62:
                            Bitmap _result23 = screenshotWallpaper();
                            reply.writeNoException();
                            reply.writeTypedObject(_result23, 1);
                            break;
                        case 63:
                            int _arg051 = data.readInt();
                            data.enforceNoDataAvail();
                            SurfaceControl _result24 = mirrorWallpaperSurface(_arg051);
                            reply.writeNoException();
                            reply.writeTypedObject(_result24, 1);
                            break;
                        case 64:
                            IWallpaperVisibilityListener _arg052 = IWallpaperVisibilityListener.Stub.asInterface(data.readStrongBinder());
                            int _arg123 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result25 = registerWallpaperVisibilityListener(_arg052, _arg123);
                            reply.writeNoException();
                            reply.writeBoolean(_result25);
                            break;
                        case 65:
                            IWallpaperVisibilityListener _arg053 = IWallpaperVisibilityListener.Stub.asInterface(data.readStrongBinder());
                            int _arg124 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterWallpaperVisibilityListener(_arg053, _arg124);
                            reply.writeNoException();
                            break;
                        case 66:
                            ISystemGestureExclusionListener _arg054 = ISystemGestureExclusionListener.Stub.asInterface(data.readStrongBinder());
                            int _arg125 = data.readInt();
                            data.enforceNoDataAvail();
                            registerSystemGestureExclusionListener(_arg054, _arg125);
                            reply.writeNoException();
                            break;
                        case 67:
                            ISystemGestureExclusionListener _arg055 = ISystemGestureExclusionListener.Stub.asInterface(data.readStrongBinder());
                            int _arg126 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterSystemGestureExclusionListener(_arg055, _arg126);
                            reply.writeNoException();
                            break;
                        case 68:
                            IAssistDataReceiver _arg056 = IAssistDataReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result26 = requestAssistScreenshot(_arg056);
                            reply.writeNoException();
                            reply.writeBoolean(_result26);
                            break;
                        case 69:
                            int _arg057 = data.readInt();
                            data.enforceNoDataAvail();
                            hideTransientBars(_arg057);
                            break;
                        case 70:
                            boolean _arg058 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setRecentsVisibility(_arg058);
                            break;
                        case 71:
                            int _arg059 = data.readInt();
                            data.enforceNoDataAvail();
                            updateStaticPrivacyIndicatorBounds(_arg059, (Rect[]) data.createTypedArray(Rect.CREATOR));
                            break;
                        case 72:
                            boolean _arg060 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setNavBarVirtualKeyHapticFeedbackEnabled(_arg060);
                            reply.writeNoException();
                            break;
                        case 73:
                            int _arg061 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result27 = hasNavigationBar(_arg061);
                            reply.writeNoException();
                            reply.writeBoolean(_result27);
                            break;
                        case 74:
                            Bundle _arg062 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            lockNow(_arg062);
                            reply.writeNoException();
                            break;
                        case 75:
                            boolean _result28 = isSafeModeEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result28);
                            break;
                        case 76:
                            IBinder _arg063 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            boolean _result29 = clearWindowContentFrameStats(_arg063);
                            reply.writeNoException();
                            reply.writeBoolean(_result29);
                            break;
                        case 77:
                            IBinder _arg064 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            WindowContentFrameStats _result30 = getWindowContentFrameStats(_arg064);
                            reply.writeNoException();
                            reply.writeTypedObject(_result30, 1);
                            break;
                        case 78:
                            int _result31 = getDockedStackSide();
                            reply.writeNoException();
                            reply.writeInt(_result31);
                            break;
                        case 79:
                            int _arg065 = data.readInt();
                            IPinnedTaskListener _arg127 = IPinnedTaskListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerPinnedTaskListener(_arg065, _arg127);
                            reply.writeNoException();
                            break;
                        case 80:
                            IResultReceiver _arg066 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            int _arg128 = data.readInt();
                            data.enforceNoDataAvail();
                            requestAppKeyboardShortcuts(_arg066, _arg128);
                            reply.writeNoException();
                            break;
                        case 81:
                            int _arg067 = data.readInt();
                            Rect _arg129 = new Rect();
                            data.enforceNoDataAvail();
                            getStableInsets(_arg067, _arg129);
                            reply.writeNoException();
                            reply.writeTypedObject(_arg129, 1);
                            break;
                        case 82:
                            long _arg068 = data.readLong();
                            IShortcutService _arg130 = IShortcutService.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerShortcutKey(_arg068, _arg130);
                            reply.writeNoException();
                            break;
                        case 83:
                            IBinder _arg069 = data.readStrongBinder();
                            String _arg131 = data.readString();
                            int _arg28 = data.readInt();
                            InputChannel _arg33 = new InputChannel();
                            data.enforceNoDataAvail();
                            createInputConsumer(_arg069, _arg131, _arg28, _arg33);
                            reply.writeNoException();
                            reply.writeTypedObject(_arg33, 1);
                            break;
                        case 84:
                            String _arg070 = data.readString();
                            int _arg132 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result32 = destroyInputConsumer(_arg070, _arg132);
                            reply.writeNoException();
                            reply.writeBoolean(_result32);
                            break;
                        case 85:
                            Region _result33 = getCurrentImeTouchRegion();
                            reply.writeNoException();
                            reply.writeTypedObject(_result33, 1);
                            break;
                        case 86:
                            IDisplayFoldListener _arg071 = IDisplayFoldListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerDisplayFoldListener(_arg071);
                            reply.writeNoException();
                            break;
                        case 87:
                            IDisplayFoldListener _arg072 = IDisplayFoldListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterDisplayFoldListener(_arg072);
                            reply.writeNoException();
                            break;
                        case 88:
                            IDisplayWindowListener _arg073 = IDisplayWindowListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int[] _result34 = registerDisplayWindowListener(_arg073);
                            reply.writeNoException();
                            reply.writeIntArray(_result34);
                            break;
                        case 89:
                            IDisplayWindowListener _arg074 = IDisplayWindowListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterDisplayWindowListener(_arg074);
                            reply.writeNoException();
                            break;
                        case 90:
                            startWindowTrace();
                            reply.writeNoException();
                            break;
                        case 91:
                            stopWindowTrace();
                            reply.writeNoException();
                            break;
                        case 92:
                            saveWindowTraceToFile();
                            reply.writeNoException();
                            break;
                        case 93:
                            boolean _result35 = isWindowTraceEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result35);
                            break;
                        case 94:
                            startTransitionTrace();
                            reply.writeNoException();
                            break;
                        case 95:
                            stopTransitionTrace();
                            reply.writeNoException();
                            break;
                        case 96:
                            boolean _result36 = isTransitionTraceEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result36);
                            break;
                        case 97:
                            int _arg075 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result37 = getWindowingMode(_arg075);
                            reply.writeNoException();
                            reply.writeInt(_result37);
                            break;
                        case 98:
                            int _arg076 = data.readInt();
                            int _arg133 = data.readInt();
                            data.enforceNoDataAvail();
                            setWindowingMode(_arg076, _arg133);
                            reply.writeNoException();
                            break;
                        case 99:
                            int _arg077 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result38 = getRemoveContentMode(_arg077);
                            reply.writeNoException();
                            reply.writeInt(_result38);
                            break;
                        case 100:
                            int _arg078 = data.readInt();
                            int _arg134 = data.readInt();
                            data.enforceNoDataAvail();
                            setRemoveContentMode(_arg078, _arg134);
                            reply.writeNoException();
                            break;
                        case 101:
                            int _arg079 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result39 = shouldShowWithInsecureKeyguard(_arg079);
                            reply.writeNoException();
                            reply.writeBoolean(_result39);
                            break;
                        case 102:
                            int _arg080 = data.readInt();
                            boolean _arg135 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setShouldShowWithInsecureKeyguard(_arg080, _arg135);
                            reply.writeNoException();
                            break;
                        case 103:
                            int _arg081 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result40 = shouldShowSystemDecors(_arg081);
                            reply.writeNoException();
                            reply.writeBoolean(_result40);
                            break;
                        case 104:
                            int _arg082 = data.readInt();
                            boolean _arg136 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setShouldShowSystemDecors(_arg082, _arg136);
                            reply.writeNoException();
                            break;
                        case 105:
                            int _arg083 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result41 = getDisplayImePolicy(_arg083);
                            reply.writeNoException();
                            reply.writeInt(_result41);
                            break;
                        case 106:
                            int _arg084 = data.readInt();
                            int _arg137 = data.readInt();
                            data.enforceNoDataAvail();
                            setDisplayImePolicy(_arg084, _arg137);
                            reply.writeNoException();
                            break;
                        case 107:
                            boolean _arg085 = data.readBoolean();
                            data.enforceNoDataAvail();
                            syncInputTransactions(_arg085);
                            reply.writeNoException();
                            break;
                        case 108:
                            boolean _result42 = isLayerTracing();
                            reply.writeNoException();
                            reply.writeBoolean(_result42);
                            break;
                        case 109:
                            boolean _arg086 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setLayerTracing(_arg086);
                            reply.writeNoException();
                            break;
                        case 110:
                            int _arg087 = data.readInt();
                            SurfaceControl _arg138 = new SurfaceControl();
                            data.enforceNoDataAvail();
                            boolean _result43 = mirrorDisplay(_arg087, _arg138);
                            reply.writeNoException();
                            reply.writeBoolean(_result43);
                            reply.writeTypedObject(_arg138, 1);
                            break;
                        case 111:
                            int _arg088 = data.readInt();
                            IDisplayWindowInsetsController _arg139 = IDisplayWindowInsetsController.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setDisplayWindowInsetsController(_arg088, _arg139);
                            reply.writeNoException();
                            break;
                        case 112:
                            int _arg089 = data.readInt();
                            int _arg140 = data.readInt();
                            data.enforceNoDataAvail();
                            updateDisplayWindowRequestedVisibleTypes(_arg089, _arg140);
                            reply.writeNoException();
                            break;
                        case 113:
                            int _arg090 = data.readInt();
                            IBinder _arg141 = data.readStrongBinder();
                            InsetsState _arg29 = new InsetsState();
                            data.enforceNoDataAvail();
                            boolean _result44 = getWindowInsets(_arg090, _arg141, _arg29);
                            reply.writeNoException();
                            reply.writeBoolean(_result44);
                            reply.writeTypedObject(_arg29, 1);
                            break;
                        case 114:
                            int _arg091 = data.readInt();
                            data.enforceNoDataAvail();
                            List<DisplayInfo> _result45 = getPossibleDisplayInfo(_arg091);
                            reply.writeNoException();
                            reply.writeTypedList(_result45, 1);
                            break;
                        case 115:
                            showGlobalActions();
                            reply.writeNoException();
                            break;
                        case 116:
                            int _arg092 = data.readInt();
                            data.enforceNoDataAvail();
                            setLayerTracingFlags(_arg092);
                            reply.writeNoException();
                            break;
                        case 117:
                            boolean _arg093 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setActiveTransactionTracing(_arg093);
                            reply.writeNoException();
                            break;
                        case 118:
                            int _arg094 = data.readInt();
                            IBinder _arg142 = data.readStrongBinder();
                            int _arg210 = data.readInt();
                            IScrollCaptureResponseListener _arg34 = IScrollCaptureResponseListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestScrollCapture(_arg094, _arg142, _arg210, _arg34);
                            reply.writeNoException();
                            break;
                        case 119:
                            IBinder _arg095 = data.readStrongBinder();
                            int _arg143 = data.readInt();
                            data.enforceNoDataAvail();
                            holdLock(_arg095, _arg143);
                            reply.writeNoException();
                            break;
                        case 120:
                            String[] _result46 = getSupportedDisplayHashAlgorithms();
                            reply.writeNoException();
                            reply.writeStringArray(_result46);
                            break;
                        case 121:
                            DisplayHash _arg096 = (DisplayHash) data.readTypedObject(DisplayHash.CREATOR);
                            data.enforceNoDataAvail();
                            VerifiedDisplayHash _result47 = verifyDisplayHash(_arg096);
                            reply.writeNoException();
                            reply.writeTypedObject(_result47, 1);
                            break;
                        case 122:
                            boolean _arg097 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setDisplayHashThrottlingEnabled(_arg097);
                            reply.writeNoException();
                            break;
                        case 123:
                            IBinder _arg098 = data.readStrongBinder();
                            int _arg144 = data.readInt();
                            int _arg211 = data.readInt();
                            Bundle _arg35 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            Configuration _result48 = attachWindowContextToDisplayArea(_arg098, _arg144, _arg211, _arg35);
                            reply.writeNoException();
                            reply.writeTypedObject(_result48, 1);
                            break;
                        case 124:
                            IBinder _arg099 = data.readStrongBinder();
                            IBinder _arg145 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            attachWindowContextToWindowToken(_arg099, _arg145);
                            reply.writeNoException();
                            break;
                        case 125:
                            IBinder _arg0100 = data.readStrongBinder();
                            int _arg146 = data.readInt();
                            data.enforceNoDataAvail();
                            Configuration _result49 = attachToDisplayContent(_arg0100, _arg146);
                            reply.writeNoException();
                            reply.writeTypedObject(_result49, 1);
                            break;
                        case 126:
                            IBinder _arg0101 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            detachWindowContextFromWindowContainer(_arg0101);
                            reply.writeNoException();
                            break;
                        case 127:
                            ICrossWindowBlurEnabledListener _arg0102 = ICrossWindowBlurEnabledListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result50 = registerCrossWindowBlurEnabledListener(_arg0102);
                            reply.writeNoException();
                            reply.writeBoolean(_result50);
                            break;
                        case 128:
                            ICrossWindowBlurEnabledListener _arg0103 = ICrossWindowBlurEnabledListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterCrossWindowBlurEnabledListener(_arg0103);
                            reply.writeNoException();
                            break;
                        case 129:
                            boolean _result51 = isTaskSnapshotSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result51);
                            break;
                        case 130:
                            int _result52 = getImeDisplayId();
                            reply.writeNoException();
                            reply.writeInt(_result52);
                            break;
                        case 131:
                            boolean _arg0104 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setTaskSnapshotEnabled(_arg0104);
                            reply.writeNoException();
                            break;
                        case 132:
                            TaskTransitionSpec _arg0105 = (TaskTransitionSpec) data.readTypedObject(TaskTransitionSpec.CREATOR);
                            data.enforceNoDataAvail();
                            setTaskTransitionSpec(_arg0105);
                            reply.writeNoException();
                            break;
                        case 133:
                            clearTaskTransitionSpec();
                            reply.writeNoException();
                            break;
                        case 134:
                            int _arg0106 = data.readInt();
                            ITaskFpsCallback _arg147 = ITaskFpsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerTaskFpsCallback(_arg0106, _arg147);
                            reply.writeNoException();
                            break;
                        case 135:
                            ITaskFpsCallback _arg0107 = ITaskFpsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterTaskFpsCallback(_arg0107);
                            reply.writeNoException();
                            break;
                        case 136:
                            int _arg0108 = data.readInt();
                            data.enforceNoDataAvail();
                            Bitmap _result53 = snapshotTaskForRecents(_arg0108);
                            reply.writeNoException();
                            reply.writeTypedObject(_result53, 1);
                            break;
                        case 137:
                            boolean _arg0109 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setRecentsAppBehindSystemBars(_arg0109);
                            reply.writeNoException();
                            break;
                        case 138:
                            int _result54 = getLetterboxBackgroundColorInArgb();
                            reply.writeNoException();
                            reply.writeInt(_result54);
                            break;
                        case 139:
                            boolean _result55 = isLetterboxBackgroundMultiColored();
                            reply.writeNoException();
                            reply.writeBoolean(_result55);
                            break;
                        case 140:
                            int _arg0110 = data.readInt();
                            ScreenCapture.ScreenCaptureListener _arg212 = (ScreenCapture.ScreenCaptureListener) data.readTypedObject(ScreenCapture.ScreenCaptureListener.CREATOR);
                            data.enforceNoDataAvail();
                            captureDisplay(_arg0110, (ScreenCapture.CaptureArgs) data.readTypedObject(ScreenCapture.CaptureArgs.CREATOR), _arg212);
                            break;
                        case 141:
                            int _arg0111 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result56 = isGlobalKey(_arg0111);
                            reply.writeNoException();
                            reply.writeBoolean(_result56);
                            break;
                        case 142:
                            IBinder _arg0112 = data.readStrongBinder();
                            boolean _arg148 = data.readBoolean();
                            ISurfaceSyncGroupCompletedListener _arg213 = ISurfaceSyncGroupCompletedListener.Stub.asInterface(data.readStrongBinder());
                            AddToSurfaceSyncGroupResult _arg36 = new AddToSurfaceSyncGroupResult();
                            data.enforceNoDataAvail();
                            boolean _result57 = addToSurfaceSyncGroup(_arg0112, _arg148, _arg213, _arg36);
                            reply.writeNoException();
                            reply.writeBoolean(_result57);
                            reply.writeTypedObject(_arg36, 1);
                            break;
                        case 143:
                            IBinder _arg0113 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            markSurfaceSyncGroupReady(_arg0113);
                            break;
                        case 144:
                            int _arg0114 = data.readInt();
                            data.enforceNoDataAvail();
                            List<ComponentName> _result58 = notifyScreenshotListeners(_arg0114);
                            reply.writeNoException();
                            reply.writeTypedList(_result58, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IWindowManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.view.IWindowManager
            public boolean startViewServer(int port) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(port);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean stopViewServer() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isViewServerRunning() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public IWindowSession openSession(IWindowSessionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    IWindowSession _result = IWindowSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean useBLAST() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void getInitialDisplaySize(int displayId, Point size) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        size.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void getBaseDisplaySize(int displayId, Point size) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        size.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setForcedDisplaySize(int displayId, int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void clearForcedDisplaySize(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getInitialDisplayDensity(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getBaseDisplayDensity(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getDisplayIdByUniqueId(String uniqueId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uniqueId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setForcedDisplayDensityForUser(int displayId, int density, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(density);
                    _data.writeInt(userId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void clearForcedDisplayDensityForUser(int displayId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(userId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setForcedDisplayScalingMode(int displayId, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(mode);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setEventDispatching(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isWindowToken(IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void addWindowToken(IBinder token, int type, int displayId, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(type);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void removeWindowToken(IBinder token, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(displayId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setDisplayChangeWindowController(IDisplayChangeWindowController controller) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(controller);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public SurfaceControl addShellRoot(int displayId, IWindow client, int shellRootLayer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeStrongInterface(client);
                    _data.writeInt(shellRootLayer);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    SurfaceControl _result = (SurfaceControl) _reply.readTypedObject(SurfaceControl.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setShellRootAccessibilityWindow(int displayId, int shellRootLayer, IWindow target) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(shellRootLayer);
                    _data.writeStrongInterface(target);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture specsFuture, IRemoteCallback startedCallback, boolean scaleUp, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(specsFuture);
                    _data.writeStrongInterface(startedCallback);
                    _data.writeBoolean(scaleUp);
                    _data.writeInt(displayId);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void overridePendingAppTransitionRemote(RemoteAnimationAdapter remoteAnimationAdapter, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(remoteAnimationAdapter, 0);
                    _data.writeInt(displayId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void endProlongedAnimations() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void startFreezingScreen(int exitAnim, int enterAnim) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(exitAnim);
                    _data.writeInt(enterAnim);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void stopFreezingScreen() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void disableKeyguard(IBinder token, String tag, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(tag);
                    _data.writeInt(userId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void reenableKeyguard(IBinder token, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(userId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void exitKeyguardSecurely(IOnKeyguardExitResult callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isKeyguardLocked() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isKeyguardSecure(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void dismissKeyguard(IKeyguardDismissCallback callback, CharSequence message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    if (message != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(message, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void addKeyguardLockedStateListener(IKeyguardLockedStateListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void removeKeyguardLockedStateListener(IKeyguardLockedStateListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setSwitchingUser(boolean switching) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(switching);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void closeSystemDialogs(String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public float getAnimationScale(int which) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(which);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public float[] getAnimationScales() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    float[] _result = _reply.createFloatArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setAnimationScale(int which, float scale) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(which);
                    _data.writeFloat(scale);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setAnimationScales(float[] scales) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloatArray(scales);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public float getCurrentAnimatorScale() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setInTouchMode(boolean inTouch, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(inTouch);
                    _data.writeInt(displayId);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setInTouchModeOnAllDisplays(boolean inTouch) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(inTouch);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isInTouchMode(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void showStrictModeViolation(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(on);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setStrictModeVisualIndicatorPreference(String enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(enabled);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void refreshScreenCaptureDisabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getDefaultDisplayRotation() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int watchRotation(IRotationWatcher watcher, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(watcher);
                    _data.writeInt(displayId);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void removeRotationWatcher(IRotationWatcher watcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(watcher);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int registerProposedRotationListener(IBinder contextToken, IRotationWatcher listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(contextToken);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getPreferredOptionsPanelGravity(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void freezeRotation(int rotation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rotation);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void thawRotation() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isRotationFrozen() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void freezeDisplayRotation(int displayId, int rotation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(rotation);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void thawDisplayRotation(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isDisplayRotationFrozen(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setFixedToUserRotation(int displayId, int fixedToUserRotation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(fixedToUserRotation);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setIgnoreOrientationRequest(int displayId, boolean ignoreOrientationRequest) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeBoolean(ignoreOrientationRequest);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public Bitmap screenshotWallpaper() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                    Bitmap _result = (Bitmap) _reply.readTypedObject(Bitmap.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public SurfaceControl mirrorWallpaperSurface(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                    SurfaceControl _result = (SurfaceControl) _reply.readTypedObject(SurfaceControl.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean registerWallpaperVisibilityListener(IWallpaperVisibilityListener listener, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(displayId);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener listener, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(displayId);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void registerSystemGestureExclusionListener(ISystemGestureExclusionListener listener, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(displayId);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void unregisterSystemGestureExclusionListener(ISystemGestureExclusionListener listener, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(displayId);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean requestAssistScreenshot(IAssistDataReceiver receiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(receiver);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void hideTransientBars(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(69, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setRecentsVisibility(boolean visible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(visible);
                    this.mRemote.transact(70, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void updateStaticPrivacyIndicatorBounds(int displayId, Rect[] staticBounds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedArray(staticBounds, 0);
                    this.mRemote.transact(71, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setNavBarVirtualKeyHapticFeedbackEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean hasNavigationBar(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void lockNow(Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isSafeModeEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean clearWindowContentFrameStats(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public WindowContentFrameStats getWindowContentFrameStats(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                    WindowContentFrameStats _result = (WindowContentFrameStats) _reply.readTypedObject(WindowContentFrameStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getDockedStackSide() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void registerPinnedTaskListener(int displayId, IPinnedTaskListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void requestAppKeyboardShortcuts(IResultReceiver receiver, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(receiver);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void getStableInsets(int displayId, Rect outInsets) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        outInsets.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void registerShortcutKey(long shortcutCode, IShortcutService keySubscriber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(shortcutCode);
                    _data.writeStrongInterface(keySubscriber);
                    this.mRemote.transact(82, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void createInputConsumer(IBinder token, String name, int displayId, InputChannel inputChannel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(name);
                    _data.writeInt(displayId);
                    this.mRemote.transact(83, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        inputChannel.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean destroyInputConsumer(String name, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(displayId);
                    this.mRemote.transact(84, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public Region getCurrentImeTouchRegion() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    Region _result = (Region) _reply.readTypedObject(Region.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void registerDisplayFoldListener(IDisplayFoldListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void unregisterDisplayFoldListener(IDisplayFoldListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(87, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int[] registerDisplayWindowListener(IDisplayWindowListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void unregisterDisplayWindowListener(IDisplayWindowListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(89, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void startWindowTrace() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void stopWindowTrace() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(91, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void saveWindowTraceToFile() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(92, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isWindowTraceEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(93, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void startTransitionTrace() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void stopTransitionTrace() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(95, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isTransitionTraceEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(96, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getWindowingMode(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(97, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setWindowingMode(int displayId, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(mode);
                    this.mRemote.transact(98, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getRemoveContentMode(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(99, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setRemoveContentMode(int displayId, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(mode);
                    this.mRemote.transact(100, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean shouldShowWithInsecureKeyguard(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(101, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setShouldShowWithInsecureKeyguard(int displayId, boolean shouldShow) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeBoolean(shouldShow);
                    this.mRemote.transact(102, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean shouldShowSystemDecors(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(103, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setShouldShowSystemDecors(int displayId, boolean shouldShow) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeBoolean(shouldShow);
                    this.mRemote.transact(104, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getDisplayImePolicy(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(105, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setDisplayImePolicy(int displayId, int imePolicy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(imePolicy);
                    this.mRemote.transact(106, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void syncInputTransactions(boolean waitForAnimations) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(waitForAnimations);
                    this.mRemote.transact(107, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isLayerTracing() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(108, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setLayerTracing(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(109, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean mirrorDisplay(int displayId, SurfaceControl outSurfaceControl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(110, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    if (_reply.readInt() != 0) {
                        outSurfaceControl.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setDisplayWindowInsetsController(int displayId, IDisplayWindowInsetsController displayWindowInsetsController) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeStrongInterface(displayWindowInsetsController);
                    this.mRemote.transact(111, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void updateDisplayWindowRequestedVisibleTypes(int displayId, int requestedVisibleTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(requestedVisibleTypes);
                    this.mRemote.transact(112, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean getWindowInsets(int displayId, IBinder token, InsetsState outInsetsState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(113, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    if (_reply.readInt() != 0) {
                        outInsetsState.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public List<DisplayInfo> getPossibleDisplayInfo(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(114, _data, _reply, 0);
                    _reply.readException();
                    List<DisplayInfo> _result = _reply.createTypedArrayList(DisplayInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void showGlobalActions() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(115, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setLayerTracingFlags(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(116, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setActiveTransactionTracing(boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(active);
                    this.mRemote.transact(117, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void requestScrollCapture(int displayId, IBinder behindClient, int taskId, IScrollCaptureResponseListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeStrongBinder(behindClient);
                    _data.writeInt(taskId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(118, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void holdLock(IBinder token, int durationMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(durationMs);
                    this.mRemote.transact(119, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public String[] getSupportedDisplayHashAlgorithms() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(120, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public VerifiedDisplayHash verifyDisplayHash(DisplayHash displayHash) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(displayHash, 0);
                    this.mRemote.transact(121, _data, _reply, 0);
                    _reply.readException();
                    VerifiedDisplayHash _result = (VerifiedDisplayHash) _reply.readTypedObject(VerifiedDisplayHash.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setDisplayHashThrottlingEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(122, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public Configuration attachWindowContextToDisplayArea(IBinder clientToken, int type, int displayId, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientToken);
                    _data.writeInt(type);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(123, _data, _reply, 0);
                    _reply.readException();
                    Configuration _result = (Configuration) _reply.readTypedObject(Configuration.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void attachWindowContextToWindowToken(IBinder clientToken, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientToken);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(124, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public Configuration attachToDisplayContent(IBinder clientToken, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientToken);
                    _data.writeInt(displayId);
                    this.mRemote.transact(125, _data, _reply, 0);
                    _reply.readException();
                    Configuration _result = (Configuration) _reply.readTypedObject(Configuration.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void detachWindowContextFromWindowContainer(IBinder clientToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientToken);
                    this.mRemote.transact(126, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean registerCrossWindowBlurEnabledListener(ICrossWindowBlurEnabledListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(127, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void unregisterCrossWindowBlurEnabledListener(ICrossWindowBlurEnabledListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(128, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isTaskSnapshotSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(129, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getImeDisplayId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(130, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setTaskSnapshotEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(131, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setTaskTransitionSpec(TaskTransitionSpec spec) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(spec, 0);
                    this.mRemote.transact(132, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void clearTaskTransitionSpec() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(133, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void registerTaskFpsCallback(int taskId, ITaskFpsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(134, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void unregisterTaskFpsCallback(ITaskFpsCallback listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(135, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public Bitmap snapshotTaskForRecents(int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    this.mRemote.transact(136, _data, _reply, 0);
                    _reply.readException();
                    Bitmap _result = (Bitmap) _reply.readTypedObject(Bitmap.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void setRecentsAppBehindSystemBars(boolean behindSystemBars) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(behindSystemBars);
                    this.mRemote.transact(137, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public int getLetterboxBackgroundColorInArgb() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(138, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isLetterboxBackgroundMultiColored() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(139, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void captureDisplay(int displayId, ScreenCapture.CaptureArgs captureArgs, ScreenCapture.ScreenCaptureListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(captureArgs, 0);
                    _data.writeTypedObject(listener, 0);
                    this.mRemote.transact(140, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean isGlobalKey(int keyCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyCode);
                    this.mRemote.transact(141, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public boolean addToSurfaceSyncGroup(IBinder syncGroupToken, boolean parentSyncGroupMerge, ISurfaceSyncGroupCompletedListener completedListener, AddToSurfaceSyncGroupResult addToSurfaceSyncGroupResult) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(syncGroupToken);
                    _data.writeBoolean(parentSyncGroupMerge);
                    _data.writeStrongInterface(completedListener);
                    this.mRemote.transact(142, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    if (_reply.readInt() != 0) {
                        addToSurfaceSyncGroupResult.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public void markSurfaceSyncGroupReady(IBinder syncGroupToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(syncGroupToken);
                    this.mRemote.transact(143, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowManager
            public List<ComponentName> notifyScreenshotListeners(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(144, _data, _reply, 0);
                    _reply.readException();
                    List<ComponentName> _result = _reply.createTypedArrayList(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 143;
        }
    }
}
