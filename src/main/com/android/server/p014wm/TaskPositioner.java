package com.android.server.p014wm;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.InputConstants;
import android.os.RemoteException;
import android.os.Trace;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.BatchedInputEventReceiver;
import android.view.InputApplicationHandle;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.InputWindowHandle;
import android.view.MotionEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.TaskResizingAlgorithm;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import java.util.concurrent.CompletableFuture;
/* renamed from: com.android.server.wm.TaskPositioner */
/* loaded from: classes2.dex */
public class TaskPositioner implements IBinder.DeathRecipient {
    public static Factory sFactory;
    public IBinder mClientCallback;
    public InputChannel mClientChannel;
    public DisplayContent mDisplayContent;
    public InputApplicationHandle mDragApplicationHandle;
    @VisibleForTesting
    boolean mDragEnded;
    public InputWindowHandle mDragWindowHandle;
    public InputEventReceiver mInputEventReceiver;
    public int mMinVisibleHeight;
    public int mMinVisibleWidth;
    public boolean mPreserveOrientation;
    public boolean mResizing;
    public final WindowManagerService mService;
    public float mStartDragX;
    public float mStartDragY;
    public boolean mStartOrientationWasLandscape;
    @VisibleForTesting
    Task mTask;
    public WindowState mWindow;
    public Rect mTmpRect = new Rect();
    public final Rect mWindowOriginalBounds = new Rect();
    public final Rect mWindowDragBounds = new Rect();
    public final Point mMaxVisibleSize = new Point();
    public int mCtrlType = 0;

    public final void checkBoundsForOrientationViolations(Rect rect) {
    }

    @VisibleForTesting
    public TaskPositioner(WindowManagerService windowManagerService) {
        this.mService = windowManagerService;
    }

    public final boolean onInputEvent(InputEvent inputEvent) {
        if (!(inputEvent instanceof MotionEvent) || (inputEvent.getSource() & 2) == 0) {
            return false;
        }
        MotionEvent motionEvent = (MotionEvent) inputEvent;
        if (this.mDragEnded) {
            return true;
        }
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        int action = motionEvent.getAction();
        if (action == 1) {
            this.mDragEnded = true;
        } else if (action == 2) {
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mDragEnded = notifyMoveLocked(rawX, rawY);
                    this.mTask.getDimBounds(this.mTmpRect);
                } finally {
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (!this.mTmpRect.equals(this.mWindowDragBounds)) {
                Trace.traceBegin(32L, "wm.TaskPositioner.resizeTask");
                this.mService.mAtmService.resizeTask(this.mTask.mTaskId, this.mWindowDragBounds, 1);
                Trace.traceEnd(32L);
            }
        } else if (action == 3) {
            this.mDragEnded = true;
        }
        if (this.mDragEnded) {
            boolean z = this.mResizing;
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    endDragLocked();
                    this.mTask.getDimBounds(this.mTmpRect);
                } finally {
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (z && !this.mTmpRect.equals(this.mWindowDragBounds)) {
                this.mService.mAtmService.resizeTask(this.mTask.mTaskId, this.mWindowDragBounds, 3);
            }
            this.mService.mTaskPositioningController.finishTaskPositioning();
        }
        return true;
    }

    @VisibleForTesting
    public Rect getWindowDragBounds() {
        return this.mWindowDragBounds;
    }

    public CompletableFuture<Void> register(final DisplayContent displayContent, final WindowState windowState) {
        if (this.mClientChannel != null) {
            Slog.e(StartingSurfaceController.TAG, "Task positioner already registered");
            return CompletableFuture.completedFuture(null);
        }
        this.mDisplayContent = displayContent;
        this.mClientChannel = this.mService.mInputManager.createInputChannel(StartingSurfaceController.TAG);
        this.mInputEventReceiver = new BatchedInputEventReceiver.SimpleBatchedInputEventReceiver(this.mClientChannel, this.mService.mAnimationHandler.getLooper(), this.mService.mAnimator.getChoreographer(), new BatchedInputEventReceiver.SimpleBatchedInputEventReceiver.InputEventListener() { // from class: com.android.server.wm.TaskPositioner$$ExternalSyntheticLambda0
            public final boolean onInputEvent(InputEvent inputEvent) {
                boolean onInputEvent;
                onInputEvent = TaskPositioner.this.onInputEvent(inputEvent);
                return onInputEvent;
            }
        });
        this.mDragApplicationHandle = new InputApplicationHandle(new Binder(), StartingSurfaceController.TAG, InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS);
        InputWindowHandle inputWindowHandle = new InputWindowHandle(this.mDragApplicationHandle, displayContent.getDisplayId());
        this.mDragWindowHandle = inputWindowHandle;
        inputWindowHandle.name = StartingSurfaceController.TAG;
        inputWindowHandle.token = this.mClientChannel.getToken();
        InputWindowHandle inputWindowHandle2 = this.mDragWindowHandle;
        inputWindowHandle2.layoutParamsType = 2016;
        inputWindowHandle2.dispatchingTimeoutMillis = InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
        inputWindowHandle2.ownerPid = WindowManagerService.MY_PID;
        inputWindowHandle2.ownerUid = WindowManagerService.MY_UID;
        inputWindowHandle2.scaleFactor = 1.0f;
        inputWindowHandle2.inputConfig = 4;
        inputWindowHandle2.touchableRegion.setEmpty();
        if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ORIENTATION, 791468751, 0, (String) null, (Object[]) null);
        }
        this.mDisplayContent.getDisplayRotation().pause();
        return this.mService.mTaskPositioningController.showInputSurface(windowState.getDisplayId()).thenRun(new Runnable() { // from class: com.android.server.wm.TaskPositioner$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TaskPositioner.this.lambda$register$0(displayContent, windowState);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$register$0(DisplayContent displayContent, WindowState windowState) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Rect rect = this.mTmpRect;
                displayContent.getBounds(rect);
                DisplayMetrics displayMetrics = displayContent.getDisplayMetrics();
                this.mMinVisibleWidth = WindowManagerService.dipToPixel(48, displayMetrics);
                this.mMinVisibleHeight = WindowManagerService.dipToPixel(32, displayMetrics);
                this.mMaxVisibleSize.set(rect.width(), rect.height());
                this.mDragEnded = false;
                try {
                    IBinder asBinder = windowState.mClient.asBinder();
                    this.mClientCallback = asBinder;
                    asBinder.linkToDeath(this, 0);
                    this.mWindow = windowState;
                    this.mTask = windowState.getTask();
                } catch (RemoteException unused) {
                    this.mService.mTaskPositioningController.finishTaskPositioning();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void unregister() {
        if (this.mClientChannel == null) {
            Slog.e(StartingSurfaceController.TAG, "Task positioner not registered");
            return;
        }
        this.mService.mTaskPositioningController.hideInputSurface(this.mDisplayContent.getDisplayId());
        this.mService.mInputManager.removeInputChannel(this.mClientChannel.getToken());
        this.mInputEventReceiver.dispose();
        this.mInputEventReceiver = null;
        this.mClientChannel.dispose();
        this.mClientChannel = null;
        this.mDragWindowHandle = null;
        this.mDragApplicationHandle = null;
        this.mDragEnded = true;
        this.mDisplayContent.getInputMonitor().updateInputWindowsLw(true);
        if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ORIENTATION, 1422781269, 0, (String) null, (Object[]) null);
        }
        this.mDisplayContent.getDisplayRotation().resume();
        this.mDisplayContent = null;
        IBinder iBinder = this.mClientCallback;
        if (iBinder != null) {
            iBinder.unlinkToDeath(this, 0);
        }
        this.mWindow = null;
    }

    public void startDrag(boolean z, boolean z2, float f, float f2) {
        final Rect rect = this.mTmpRect;
        this.mTask.getBounds(rect);
        this.mCtrlType = 0;
        this.mStartDragX = f;
        this.mStartDragY = f2;
        this.mPreserveOrientation = z2;
        if (z) {
            if (f < rect.left) {
                this.mCtrlType = 0 | 1;
            }
            if (f > rect.right) {
                this.mCtrlType |= 2;
            }
            if (f2 < rect.top) {
                this.mCtrlType |= 4;
            }
            if (f2 > rect.bottom) {
                this.mCtrlType |= 8;
            }
            this.mResizing = this.mCtrlType != 0;
        }
        this.mStartOrientationWasLandscape = rect.width() >= rect.height();
        this.mWindowOriginalBounds.set(rect);
        if (this.mResizing) {
            notifyMoveLocked(f, f2);
            this.mService.f1164mH.post(new Runnable() { // from class: com.android.server.wm.TaskPositioner$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    TaskPositioner.this.lambda$startDrag$1(rect);
                }
            });
        }
        this.mWindowDragBounds.set(rect);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDrag$1(Rect rect) {
        this.mService.mAtmService.resizeTask(this.mTask.mTaskId, rect, 3);
    }

    public final void endDragLocked() {
        this.mResizing = false;
        this.mTask.setDragResizing(false);
    }

    @VisibleForTesting
    public boolean notifyMoveLocked(float f, float f2) {
        if (this.mCtrlType != 0) {
            resizeDrag(f, f2);
            this.mTask.setDragResizing(true);
            return false;
        }
        this.mDisplayContent.getStableRect(this.mTmpRect);
        this.mTmpRect.intersect(this.mTask.getRootTask().getParent().getBounds());
        int i = (int) f;
        int i2 = (int) f2;
        if (!this.mTmpRect.contains(i, i2)) {
            i = Math.min(Math.max(i, this.mTmpRect.left), this.mTmpRect.right);
            i2 = Math.min(Math.max(i2, this.mTmpRect.top), this.mTmpRect.bottom);
        }
        updateWindowDragBounds(i, i2, this.mTmpRect);
        return false;
    }

    @VisibleForTesting
    public void resizeDrag(float f, float f2) {
        updateDraggedBounds(TaskResizingAlgorithm.resizeDrag(f, f2, this.mStartDragX, this.mStartDragY, this.mWindowOriginalBounds, this.mCtrlType, this.mMinVisibleWidth, this.mMinVisibleHeight, this.mMaxVisibleSize, this.mPreserveOrientation, this.mStartOrientationWasLandscape));
    }

    public final void updateDraggedBounds(Rect rect) {
        this.mWindowDragBounds.set(rect);
        checkBoundsForOrientationViolations(this.mWindowDragBounds);
    }

    public final void updateWindowDragBounds(int i, int i2, Rect rect) {
        int round = Math.round(i - this.mStartDragX);
        int round2 = Math.round(i2 - this.mStartDragY);
        this.mWindowDragBounds.set(this.mWindowOriginalBounds);
        int i3 = rect.right;
        int i4 = this.mMinVisibleWidth;
        int i5 = i3 - i4;
        int width = (rect.left + i4) - this.mWindowOriginalBounds.width();
        int i6 = rect.top;
        this.mWindowDragBounds.offsetTo(Math.min(Math.max(this.mWindowOriginalBounds.left + round, width), i5), Math.min(Math.max(this.mWindowOriginalBounds.top + round2, i6), rect.bottom - this.mMinVisibleHeight));
    }

    public static TaskPositioner create(WindowManagerService windowManagerService) {
        if (sFactory == null) {
            sFactory = new Factory() { // from class: com.android.server.wm.TaskPositioner.1
            };
        }
        return sFactory.create(windowManagerService);
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        this.mService.mTaskPositioningController.finishTaskPositioning();
    }

    /* renamed from: com.android.server.wm.TaskPositioner$Factory */
    /* loaded from: classes2.dex */
    public interface Factory {
        default TaskPositioner create(WindowManagerService windowManagerService) {
            return new TaskPositioner(windowManagerService);
        }
    }
}
