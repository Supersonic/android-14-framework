package com.android.server.p014wm;

import android.graphics.Region;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.EventLog;
import android.view.InputApplicationHandle;
import android.view.InputChannel;
import android.view.InputWindowHandle;
import android.view.SurfaceControl;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.LocalServices;
import com.android.server.inputmethod.InputMethodManagerInternal;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.InputMonitor */
/* loaded from: classes2.dex */
public final class InputMonitor {
    public final DisplayContent mDisplayContent;
    public int mDisplayHeight;
    public final int mDisplayId;
    public boolean mDisplayRemoved;
    public int mDisplayWidth;
    public final Handler mHandler;
    public final SurfaceControl.Transaction mInputTransaction;
    public final WindowManagerService mService;
    public boolean mUpdateInputWindowsImmediately;
    public boolean mUpdateInputWindowsPending;
    public IBinder mInputFocus = null;
    public long mInputFocusRequestTimeMillis = 0;
    public boolean mUpdateInputWindowsNeeded = true;
    public final Region mTmpRegion = new Region();
    public final ArrayMap<String, InputConsumerImpl> mInputConsumers = new ArrayMap<>();
    public WeakReference<ActivityRecord> mActiveRecentsActivity = null;
    public WeakReference<ActivityRecord> mActiveRecentsLayerRef = null;
    public final UpdateInputWindows mUpdateInputWindows = new UpdateInputWindows();
    public final UpdateInputForAllWindowsConsumer mUpdateInputForAllWindowsConsumer = new UpdateInputForAllWindowsConsumer();

    public static boolean isTrustedOverlay(int i) {
        return i == 2039 || i == 2011 || i == 2012 || i == 2027 || i == 2000 || i == 2040 || i == 2019 || i == 2024 || i == 2015 || i == 2034 || i == 2032 || i == 2022 || i == 2031 || i == 2041;
    }

    /* renamed from: com.android.server.wm.InputMonitor$UpdateInputWindows */
    /* loaded from: classes2.dex */
    public class UpdateInputWindows implements Runnable {
        public UpdateInputWindows() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (InputMonitor.this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    InputMonitor.this.mUpdateInputWindowsPending = false;
                    InputMonitor.this.mUpdateInputWindowsNeeded = false;
                    if (InputMonitor.this.mDisplayRemoved) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    InputMonitor.this.mUpdateInputForAllWindowsConsumer.updateInputWindows(InputMonitor.this.mService.mDragDropController.dragDropActiveLocked());
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public InputMonitor(WindowManagerService windowManagerService, DisplayContent displayContent) {
        this.mService = windowManagerService;
        this.mDisplayContent = displayContent;
        this.mDisplayId = displayContent.getDisplayId();
        this.mInputTransaction = windowManagerService.mTransactionFactory.get();
        this.mHandler = windowManagerService.mAnimationHandler;
    }

    public void onDisplayRemoved() {
        this.mHandler.removeCallbacks(this.mUpdateInputWindows);
        this.mService.mTransactionFactory.get().addWindowInfosReportedListener(new Runnable() { // from class: com.android.server.wm.InputMonitor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                InputMonitor.this.lambda$onDisplayRemoved$0();
            }
        }).apply();
        this.mDisplayRemoved = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDisplayRemoved$0() {
        this.mService.mInputManager.onDisplayRemoved(this.mDisplayId);
    }

    public final void addInputConsumer(String str, InputConsumerImpl inputConsumerImpl) {
        this.mInputConsumers.put(str, inputConsumerImpl);
        inputConsumerImpl.linkToDeathRecipient();
        inputConsumerImpl.layout(this.mInputTransaction, this.mDisplayWidth, this.mDisplayHeight);
        updateInputWindowsLw(true);
    }

    public boolean destroyInputConsumer(String str) {
        if (disposeInputConsumer(this.mInputConsumers.remove(str))) {
            updateInputWindowsLw(true);
            return true;
        }
        return false;
    }

    public final boolean disposeInputConsumer(InputConsumerImpl inputConsumerImpl) {
        if (inputConsumerImpl != null) {
            inputConsumerImpl.disposeChannelsLw(this.mInputTransaction);
            return true;
        }
        return false;
    }

    public InputConsumerImpl getInputConsumer(String str) {
        return this.mInputConsumers.get(str);
    }

    public void layoutInputConsumers(int i, int i2) {
        if (this.mDisplayWidth == i && this.mDisplayHeight == i2) {
            return;
        }
        this.mDisplayWidth = i;
        this.mDisplayHeight = i2;
        try {
            Trace.traceBegin(32L, "layoutInputConsumer");
            for (int size = this.mInputConsumers.size() - 1; size >= 0; size--) {
                this.mInputConsumers.valueAt(size).layout(this.mInputTransaction, i, i2);
            }
        } finally {
            Trace.traceEnd(32L);
        }
    }

    public void resetInputConsumers(SurfaceControl.Transaction transaction) {
        for (int size = this.mInputConsumers.size() - 1; size >= 0; size--) {
            this.mInputConsumers.valueAt(size).hide(transaction);
        }
    }

    public void createInputConsumer(IBinder iBinder, String str, InputChannel inputChannel, int i, UserHandle userHandle) {
        if (this.mInputConsumers.containsKey(str)) {
            throw new IllegalStateException("Existing input consumer found with name: " + str + ", display: " + this.mDisplayId);
        }
        InputConsumerImpl inputConsumerImpl = new InputConsumerImpl(this.mService, iBinder, str, inputChannel, i, userHandle, this.mDisplayId);
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1525776435:
                if (str.equals("recents_animation_input_consumer")) {
                    c = 0;
                    break;
                }
                break;
            case 1024719987:
                if (str.equals("pip_input_consumer")) {
                    c = 1;
                    break;
                }
                break;
            case 1415830696:
                if (str.equals("wallpaper_input_consumer")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                inputConsumerImpl.mWindowHandle.inputConfig &= -5;
                break;
            case 1:
                break;
            case 2:
                inputConsumerImpl.mWindowHandle.inputConfig |= 32;
                break;
            default:
                throw new IllegalArgumentException("Illegal input consumer : " + str + ", display: " + this.mDisplayId);
        }
        addInputConsumer(str, inputConsumerImpl);
    }

    @VisibleForTesting
    public void populateInputWindowHandle(InputWindowHandleWrapper inputWindowHandleWrapper, WindowState windowState) {
        TaskFragment taskFragment;
        ActivityRecord activityRecord = windowState.mActivityRecord;
        SurfaceControl surfaceControl = null;
        boolean z = false;
        inputWindowHandleWrapper.setInputApplicationHandle(activityRecord != null ? activityRecord.getInputApplicationHandle(false) : null);
        inputWindowHandleWrapper.setToken(windowState.mInputChannelToken);
        inputWindowHandleWrapper.setDispatchingTimeoutMillis(windowState.getInputDispatchingTimeoutMillis());
        inputWindowHandleWrapper.setTouchOcclusionMode(windowState.getTouchOcclusionMode());
        ActivityRecord activityRecord2 = windowState.mActivityRecord;
        inputWindowHandleWrapper.setPaused(activityRecord2 != null && activityRecord2.paused);
        inputWindowHandleWrapper.setWindowToken(windowState.mClient);
        inputWindowHandleWrapper.setName(windowState.getName());
        WindowManager.LayoutParams layoutParams = windowState.mAttrs;
        int i = layoutParams.flags;
        if (layoutParams.isModal()) {
            i |= 32;
        }
        inputWindowHandleWrapper.setLayoutParamsFlags(i);
        WindowManager.LayoutParams layoutParams2 = windowState.mAttrs;
        inputWindowHandleWrapper.setInputConfigMasked(InputConfigAdapter.getInputConfigFromWindowParams(layoutParams2.type, i, layoutParams2.inputFeatures), InputConfigAdapter.getMask());
        inputWindowHandleWrapper.setFocusable(windowState.canReceiveKeys() && (this.mDisplayContent.hasOwnFocus() || this.mDisplayContent.isOnTop()));
        inputWindowHandleWrapper.setHasWallpaper(this.mDisplayContent.mWallpaperController.isWallpaperTarget(windowState) && !this.mService.mPolicy.isKeyguardShowing() && windowState.mAttrs.areWallpaperTouchEventsEnabled());
        inputWindowHandleWrapper.setSurfaceInset(windowState.mAttrs.surfaceInsets.left);
        float f = windowState.mGlobalScale;
        inputWindowHandleWrapper.setScaleFactor(f != 1.0f ? 1.0f / f : 1.0f);
        Task task = windowState.getTask();
        if (task != null) {
            if (task.isOrganized() && task.getWindowingMode() != 1) {
                if (windowState.mAttrs.isModal() && (taskFragment = windowState.getTaskFragment()) != null) {
                    surfaceControl = taskFragment.getSurfaceControl();
                }
                z = true;
            } else if (task.cropWindowsToRootTaskBounds() && !windowState.inFreeformWindowingMode()) {
                surfaceControl = task.getRootTask().getSurfaceControl();
            }
        }
        inputWindowHandleWrapper.setReplaceTouchableRegionWithCrop(z);
        inputWindowHandleWrapper.setTouchableRegionCrop(surfaceControl);
        if (z) {
            return;
        }
        windowState.getSurfaceTouchableRegion(this.mTmpRegion, windowState.mAttrs);
        inputWindowHandleWrapper.setTouchableRegion(this.mTmpRegion);
    }

    public void setUpdateInputWindowsNeededLw() {
        this.mUpdateInputWindowsNeeded = true;
    }

    public void updateInputWindowsLw(boolean z) {
        if (z || this.mUpdateInputWindowsNeeded) {
            scheduleUpdateInputWindows();
        }
    }

    public final void scheduleUpdateInputWindows() {
        if (this.mDisplayRemoved || this.mUpdateInputWindowsPending) {
            return;
        }
        this.mUpdateInputWindowsPending = true;
        this.mHandler.post(this.mUpdateInputWindows);
    }

    public void updateInputWindowsImmediately(SurfaceControl.Transaction transaction) {
        this.mHandler.removeCallbacks(this.mUpdateInputWindows);
        this.mUpdateInputWindowsImmediately = true;
        this.mUpdateInputWindows.run();
        this.mUpdateInputWindowsImmediately = false;
        transaction.merge(this.mInputTransaction);
    }

    public void setInputFocusLw(WindowState windowState, boolean z) {
        if (ProtoLogCache.WM_DEBUG_FOCUS_LIGHT_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_FOCUS_LIGHT, -1438175584, 4, (String) null, new Object[]{String.valueOf(windowState), Long.valueOf(this.mDisplayId)});
        }
        if ((windowState != null ? windowState.mInputChannelToken : null) == this.mInputFocus) {
            return;
        }
        if (windowState != null && windowState.canReceiveKeys()) {
            windowState.mToken.paused = false;
        }
        setUpdateInputWindowsNeededLw();
        if (z) {
            updateInputWindowsLw(false);
        }
    }

    public void setActiveRecents(ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        boolean z = activityRecord == null;
        this.mActiveRecentsActivity = z ? null : new WeakReference<>(activityRecord);
        this.mActiveRecentsLayerRef = z ? null : new WeakReference<>(activityRecord2);
    }

    public static <T> T getWeak(WeakReference<T> weakReference) {
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public final void updateInputFocusRequest(InputConsumerImpl inputConsumerImpl) {
        InputWindowHandle inputWindowHandle;
        WindowState windowState = this.mDisplayContent.mCurrentFocus;
        if (inputConsumerImpl != null && windowState != null) {
            RecentsAnimationController recentsAnimationController = this.mService.getRecentsAnimationController();
            if ((recentsAnimationController != null && recentsAnimationController.shouldApplyInputConsumer(windowState.mActivityRecord)) || (getWeak(this.mActiveRecentsActivity) != null && windowState.inTransition() && windowState.isActivityTypeHomeOrRecents())) {
                IBinder iBinder = this.mInputFocus;
                IBinder iBinder2 = inputConsumerImpl.mWindowHandle.token;
                if (iBinder != iBinder2) {
                    requestFocus(iBinder2, inputConsumerImpl.mName);
                    if (!this.mDisplayContent.isImeAttachedToApp()) {
                        InputMethodManagerInternal inputMethodManagerInternal = (InputMethodManagerInternal) LocalServices.getService(InputMethodManagerInternal.class);
                        if (inputMethodManagerInternal != null) {
                            inputMethodManagerInternal.hideCurrentInputMethod(19);
                            return;
                        }
                        return;
                    }
                    InputMethodManagerInternal.get().updateImeWindowStatus(true);
                    return;
                }
                return;
            }
        }
        IBinder iBinder3 = windowState != null ? windowState.mInputChannelToken : null;
        if (iBinder3 != null) {
            if (!windowState.mWinAnimator.hasSurface() || !windowState.mInputWindowHandle.isFocusable()) {
                if (ProtoLogCache.WM_DEBUG_FOCUS_LIGHT_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_FOCUS_LIGHT, -760764543, 0, (String) null, new Object[]{String.valueOf(windowState)});
                }
                this.mInputFocus = null;
                return;
            }
            requestFocus(iBinder3, windowState.getName());
        } else if (inputConsumerImpl == null || (inputWindowHandle = inputConsumerImpl.mWindowHandle) == null || this.mInputFocus != inputWindowHandle.token) {
            ActivityRecord activityRecord = this.mDisplayContent.mFocusedApp;
            if (activityRecord != null && this.mInputFocus != null) {
                if (ProtoLogCache.WM_DEBUG_FOCUS_LIGHT_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_FOCUS_LIGHT, 2001473656, 0, (String) null, new Object[]{String.valueOf(activityRecord.getName())});
                }
                EventLog.writeEvent(62001, "Requesting to set focus to null window", "reason=UpdateInputWindows");
                this.mInputTransaction.removeCurrentInputFocus(this.mDisplayId);
            }
            this.mInputFocus = null;
        }
    }

    public final void requestFocus(IBinder iBinder, String str) {
        if (iBinder == this.mInputFocus) {
            return;
        }
        this.mInputFocus = iBinder;
        this.mInputFocusRequestTimeMillis = SystemClock.uptimeMillis();
        this.mInputTransaction.setFocusedWindow(this.mInputFocus, str, this.mDisplayId);
        EventLog.writeEvent(62001, "Focus request " + str, "reason=UpdateInputWindows");
        if (ProtoLogCache.WM_DEBUG_FOCUS_LIGHT_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_FOCUS_LIGHT, 155482615, 0, (String) null, new Object[]{String.valueOf(str)});
        }
    }

    public void setFocusedAppLw(ActivityRecord activityRecord) {
        this.mService.mInputManager.setFocusedApplication(this.mDisplayId, activityRecord != null ? activityRecord.getInputApplicationHandle(true) : null);
    }

    public void pauseDispatchingLw(WindowToken windowToken) {
        if (windowToken.paused) {
            return;
        }
        windowToken.paused = true;
        updateInputWindowsLw(true);
    }

    public void resumeDispatchingLw(WindowToken windowToken) {
        if (windowToken.paused) {
            windowToken.paused = false;
            updateInputWindowsLw(true);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        Set<String> keySet = this.mInputConsumers.keySet();
        if (keySet.isEmpty()) {
            return;
        }
        printWriter.println(str + "InputConsumers:");
        for (String str2 : keySet) {
            this.mInputConsumers.get(str2).dump(printWriter, str2, str);
        }
    }

    /* renamed from: com.android.server.wm.InputMonitor$UpdateInputForAllWindowsConsumer */
    /* loaded from: classes2.dex */
    public final class UpdateInputForAllWindowsConsumer implements Consumer<WindowState> {
        public boolean mAddPipInputConsumerHandle;
        public boolean mAddRecentsAnimationInputConsumerHandle;
        public boolean mAddWallpaperInputConsumerHandle;
        public boolean mInDrag;
        public InputConsumerImpl mPipInputConsumer;
        public InputConsumerImpl mRecentsAnimationInputConsumer;
        public InputConsumerImpl mWallpaperInputConsumer;

        public UpdateInputForAllWindowsConsumer() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v18, types: [com.android.server.wm.WindowContainer] */
        public final void updateInputWindows(boolean z) {
            Trace.traceBegin(32L, "updateInputWindows");
            this.mPipInputConsumer = InputMonitor.this.getInputConsumer("pip_input_consumer");
            this.mWallpaperInputConsumer = InputMonitor.this.getInputConsumer("wallpaper_input_consumer");
            InputConsumerImpl inputConsumer = InputMonitor.this.getInputConsumer("recents_animation_input_consumer");
            this.mRecentsAnimationInputConsumer = inputConsumer;
            this.mAddPipInputConsumerHandle = this.mPipInputConsumer != null;
            this.mAddWallpaperInputConsumerHandle = this.mWallpaperInputConsumer != null;
            this.mAddRecentsAnimationInputConsumerHandle = inputConsumer != null;
            this.mInDrag = z;
            InputMonitor inputMonitor = InputMonitor.this;
            inputMonitor.resetInputConsumers(inputMonitor.mInputTransaction);
            ActivityRecord activityRecord = (ActivityRecord) InputMonitor.getWeak(InputMonitor.this.mActiveRecentsActivity);
            if (this.mAddRecentsAnimationInputConsumerHandle && activityRecord != null && activityRecord.getSurfaceControl() != null) {
                ?? r0 = (WindowContainer) InputMonitor.getWeak(InputMonitor.this.mActiveRecentsLayerRef);
                if (r0 != 0) {
                    activityRecord = r0;
                }
                if (activityRecord.getSurfaceControl() != null) {
                    this.mRecentsAnimationInputConsumer.mWindowHandle.replaceTouchableRegionWithCrop(activityRecord.getSurfaceControl());
                    this.mRecentsAnimationInputConsumer.show(InputMonitor.this.mInputTransaction, activityRecord);
                    this.mAddRecentsAnimationInputConsumerHandle = false;
                }
            }
            InputMonitor.this.mDisplayContent.forAllWindows((Consumer<WindowState>) this, true);
            InputMonitor.this.updateInputFocusRequest(this.mRecentsAnimationInputConsumer);
            if (!InputMonitor.this.mUpdateInputWindowsImmediately) {
                InputMonitor.this.mDisplayContent.getPendingTransaction().merge(InputMonitor.this.mInputTransaction);
                InputMonitor.this.mDisplayContent.scheduleAnimation();
            }
            Trace.traceEnd(32L);
        }

        @Override // java.util.function.Consumer
        public void accept(WindowState windowState) {
            DisplayArea targetAppDisplayArea;
            InputWindowHandleWrapper inputWindowHandleWrapper = windowState.mInputWindowHandle;
            if (windowState.mInputChannelToken == null || windowState.mRemoved || !windowState.canReceiveTouchInput()) {
                if (windowState.mWinAnimator.hasSurface()) {
                    InputMonitor.populateOverlayInputInfo(inputWindowHandleWrapper, windowState);
                    InputMonitor.setInputWindowInfoIfNeeded(InputMonitor.this.mInputTransaction, windowState.mWinAnimator.mSurfaceController.mSurfaceControl, inputWindowHandleWrapper);
                    return;
                }
                return;
            }
            RecentsAnimationController recentsAnimationController = InputMonitor.this.mService.getRecentsAnimationController();
            boolean z = recentsAnimationController != null && recentsAnimationController.shouldApplyInputConsumer(windowState.mActivityRecord);
            if (this.mAddRecentsAnimationInputConsumerHandle && z && recentsAnimationController.updateInputConsumerForApp(this.mRecentsAnimationInputConsumer.mWindowHandle) && (targetAppDisplayArea = recentsAnimationController.getTargetAppDisplayArea()) != null) {
                this.mRecentsAnimationInputConsumer.reparent(InputMonitor.this.mInputTransaction, targetAppDisplayArea);
                this.mRecentsAnimationInputConsumer.show(InputMonitor.this.mInputTransaction, 2147483645);
                this.mAddRecentsAnimationInputConsumerHandle = false;
            }
            if (windowState.inPinnedWindowingMode() && this.mAddPipInputConsumerHandle) {
                Task rootTask = windowState.getTask().getRootTask();
                this.mPipInputConsumer.mWindowHandle.replaceTouchableRegionWithCrop(rootTask.getSurfaceControl());
                TaskDisplayArea displayArea = rootTask.getDisplayArea();
                if (displayArea != null) {
                    this.mPipInputConsumer.layout(InputMonitor.this.mInputTransaction, rootTask.getBounds());
                    this.mPipInputConsumer.reparent(InputMonitor.this.mInputTransaction, displayArea);
                    this.mPipInputConsumer.show(InputMonitor.this.mInputTransaction, 2147483646);
                    this.mAddPipInputConsumerHandle = false;
                }
            }
            if (this.mAddWallpaperInputConsumerHandle && windowState.mAttrs.type == 2013 && windowState.isVisible()) {
                this.mWallpaperInputConsumer.mWindowHandle.replaceTouchableRegionWithCrop((SurfaceControl) null);
                this.mWallpaperInputConsumer.show(InputMonitor.this.mInputTransaction, windowState);
                this.mAddWallpaperInputConsumerHandle = false;
            }
            if (this.mInDrag && windowState.isVisible() && windowState.getDisplayContent().isDefaultDisplay) {
                InputMonitor.this.mService.mDragDropController.sendDragStartedIfNeededLocked(windowState);
            }
            InputMonitor.this.mService.mKeyInterceptionInfoForToken.put(windowState.mInputChannelToken, windowState.getKeyInterceptionInfo());
            if (windowState.mWinAnimator.hasSurface()) {
                InputMonitor.this.populateInputWindowHandle(inputWindowHandleWrapper, windowState);
                InputMonitor.setInputWindowInfoIfNeeded(InputMonitor.this.mInputTransaction, windowState.mWinAnimator.mSurfaceController.mSurfaceControl, inputWindowHandleWrapper);
            }
        }
    }

    @VisibleForTesting
    public static void setInputWindowInfoIfNeeded(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, InputWindowHandleWrapper inputWindowHandleWrapper) {
        if (inputWindowHandleWrapper.isChanged()) {
            inputWindowHandleWrapper.applyChangesToSurface(transaction, surfaceControl);
        }
    }

    public static void populateOverlayInputInfo(InputWindowHandleWrapper inputWindowHandleWrapper, WindowState windowState) {
        populateOverlayInputInfo(inputWindowHandleWrapper);
        inputWindowHandleWrapper.setTouchOcclusionMode(windowState.getTouchOcclusionMode());
    }

    @VisibleForTesting
    public static void populateOverlayInputInfo(InputWindowHandleWrapper inputWindowHandleWrapper) {
        inputWindowHandleWrapper.setDispatchingTimeoutMillis(0L);
        inputWindowHandleWrapper.setFocusable(false);
        inputWindowHandleWrapper.setToken(null);
        inputWindowHandleWrapper.setScaleFactor(1.0f);
        inputWindowHandleWrapper.setLayoutParamsType(2);
        inputWindowHandleWrapper.setInputConfigMasked(InputConfigAdapter.getInputConfigFromWindowParams(2, 16, 1), InputConfigAdapter.getMask());
        inputWindowHandleWrapper.clearTouchableRegion();
        inputWindowHandleWrapper.setTouchableRegionCrop(null);
    }

    public static void setTrustedOverlayInputInfo(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, int i, String str) {
        InputWindowHandleWrapper inputWindowHandleWrapper = new InputWindowHandleWrapper(new InputWindowHandle((InputApplicationHandle) null, i));
        inputWindowHandleWrapper.setName(str);
        inputWindowHandleWrapper.setLayoutParamsType(2015);
        inputWindowHandleWrapper.setTrustedOverlay(true);
        populateOverlayInputInfo(inputWindowHandleWrapper);
        setInputWindowInfoIfNeeded(transaction, surfaceControl, inputWindowHandleWrapper);
    }
}
