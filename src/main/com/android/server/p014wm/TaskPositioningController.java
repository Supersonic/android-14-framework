package com.android.server.p014wm;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Slog;
import android.view.Display;
import android.view.IWindow;
import android.view.InputWindowHandle;
import android.view.SurfaceControl;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
/* renamed from: com.android.server.wm.TaskPositioningController */
/* loaded from: classes2.dex */
public class TaskPositioningController {
    public SurfaceControl mInputSurface;
    public DisplayContent mPositioningDisplay;
    public final WindowManagerService mService;
    public TaskPositioner mTaskPositioner;
    public final Rect mTmpClipRect = new Rect();
    public final SurfaceControl.Transaction mTransaction;

    public InputWindowHandle getDragWindowHandleLocked() {
        TaskPositioner taskPositioner = this.mTaskPositioner;
        if (taskPositioner != null) {
            return taskPositioner.mDragWindowHandle;
        }
        return null;
    }

    public TaskPositioningController(WindowManagerService windowManagerService) {
        this.mService = windowManagerService;
        this.mTransaction = windowManagerService.mTransactionFactory.get();
    }

    public void hideInputSurface(int i) {
        SurfaceControl surfaceControl;
        DisplayContent displayContent = this.mPositioningDisplay;
        if (displayContent == null || displayContent.getDisplayId() != i || (surfaceControl = this.mInputSurface) == null) {
            return;
        }
        this.mTransaction.hide(surfaceControl).apply();
    }

    public CompletableFuture<Void> showInputSurface(int i) {
        DisplayContent displayContent = this.mPositioningDisplay;
        if (displayContent == null || displayContent.getDisplayId() != i) {
            return CompletableFuture.completedFuture(null);
        }
        DisplayContent displayContent2 = this.mService.mRoot.getDisplayContent(i);
        if (this.mInputSurface == null) {
            this.mInputSurface = this.mService.makeSurfaceBuilder(displayContent2.getSession()).setContainerLayer().setName("Drag and Drop Input Consumer").setCallsite("TaskPositioningController.showInputSurface").setParent(displayContent2.getOverlayLayer()).build();
        }
        InputWindowHandle dragWindowHandleLocked = getDragWindowHandleLocked();
        if (dragWindowHandleLocked == null) {
            Slog.w(StartingSurfaceController.TAG, "Drag is in progress but there is no drag window handle.");
            return CompletableFuture.completedFuture(null);
        }
        Display display = displayContent2.getDisplay();
        Point point = new Point();
        display.getRealSize(point);
        this.mTmpClipRect.set(0, 0, point.x, point.y);
        final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        this.mTransaction.show(this.mInputSurface).setInputWindowInfo(this.mInputSurface, dragWindowHandleLocked).setLayer(this.mInputSurface, Integer.MAX_VALUE).setPosition(this.mInputSurface, 0.0f, 0.0f).setCrop(this.mInputSurface, this.mTmpClipRect).addWindowInfosReportedListener(new Runnable() { // from class: com.android.server.wm.TaskPositioningController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                completableFuture.complete(null);
            }
        }).apply();
        return completableFuture;
    }

    public boolean startMovingTask(IWindow iWindow, float f, float f2) {
        WindowState windowForClientLocked;
        CompletableFuture<Boolean> startPositioningLocked;
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                windowForClientLocked = this.mService.windowForClientLocked((Session) null, iWindow, false);
                startPositioningLocked = startPositioningLocked(windowForClientLocked, false, false, f, f2);
            } finally {
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        try {
            if (startPositioningLocked.get().booleanValue()) {
                synchronized (this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        this.mService.mAtmService.setFocusedTask(windowForClientLocked.getTask().mTaskId);
                    } finally {
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
                return true;
            }
            return false;
        } catch (Exception e) {
            Slog.e(StartingSurfaceController.TAG, "Exception thrown while waiting for startPositionLocked future", e);
            return false;
        }
    }

    public void handleTapOutsideTask(final DisplayContent displayContent, final int i, final int i2) {
        this.mService.f1164mH.post(new Runnable() { // from class: com.android.server.wm.TaskPositioningController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TaskPositioningController.this.lambda$handleTapOutsideTask$1(displayContent, i, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleTapOutsideTask$1(DisplayContent displayContent, int i, int i2) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Task findTaskForResizePoint = displayContent.findTaskForResizePoint(i, i2);
                if (findTaskForResizePoint != null && findTaskForResizePoint.isResizeable()) {
                    CompletableFuture<Boolean> startPositioningLocked = startPositioningLocked(findTaskForResizePoint.getTopVisibleAppMainWindow(), true, findTaskForResizePoint.preserveOrientationOnResize(), i, i2);
                    try {
                        if (startPositioningLocked.get().booleanValue()) {
                            synchronized (this.mService.mGlobalLock) {
                                try {
                                    WindowManagerService.boostPriorityForLockedSection();
                                    this.mService.mAtmService.setFocusedTask(findTaskForResizePoint.mTaskId);
                                } finally {
                                }
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        return;
                    } catch (Exception e) {
                        Slog.e(StartingSurfaceController.TAG, "Exception thrown while waiting for startPositionLocked future", e);
                        return;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } finally {
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    public final CompletableFuture<Boolean> startPositioningLocked(final WindowState windowState, final boolean z, final boolean z2, final float f, final float f2) {
        if (windowState == null || windowState.mActivityRecord == null) {
            Slog.w(StartingSurfaceController.TAG, "startPositioningLocked: Bad window " + windowState);
            return CompletableFuture.completedFuture(Boolean.FALSE);
        } else if (windowState.mInputChannel == null) {
            Slog.wtf(StartingSurfaceController.TAG, "startPositioningLocked: " + windowState + " has no input channel,  probably being removed");
            return CompletableFuture.completedFuture(Boolean.FALSE);
        } else {
            final DisplayContent displayContent = windowState.getDisplayContent();
            if (displayContent == null) {
                Slog.w(StartingSurfaceController.TAG, "startPositioningLocked: Invalid display content " + windowState);
                return CompletableFuture.completedFuture(Boolean.FALSE);
            }
            this.mPositioningDisplay = displayContent;
            TaskPositioner create = TaskPositioner.create(this.mService);
            this.mTaskPositioner = create;
            return create.register(displayContent, windowState).thenApply(new Function() { // from class: com.android.server.wm.TaskPositioningController$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Boolean lambda$startPositioningLocked$2;
                    lambda$startPositioningLocked$2 = TaskPositioningController.this.lambda$startPositioningLocked$2(windowState, displayContent, z, z2, f, f2, (Void) obj);
                    return lambda$startPositioningLocked$2;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$startPositioningLocked$2(WindowState windowState, DisplayContent displayContent, boolean z, boolean z2, float f, float f2, Void r9) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowState windowState2 = displayContent.mCurrentFocus;
                if (windowState2 != null && windowState2 != windowState && windowState2.mActivityRecord == windowState.mActivityRecord) {
                    windowState = windowState2;
                }
                if (!this.mService.mInputManager.transferTouchFocus(windowState.mInputChannel, this.mTaskPositioner.mClientChannel, false)) {
                    Slog.e(StartingSurfaceController.TAG, "startPositioningLocked: Unable to transfer touch focus");
                    cleanUpTaskPositioner();
                    Boolean bool = Boolean.FALSE;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return bool;
                }
                this.mTaskPositioner.startDrag(z, z2, f, f2);
                Boolean bool2 = Boolean.TRUE;
                WindowManagerService.resetPriorityAfterLockedSection();
                return bool2;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void finishTaskPositioning(IWindow iWindow) {
        TaskPositioner taskPositioner = this.mTaskPositioner;
        if (taskPositioner == null || taskPositioner.mClientCallback != iWindow.asBinder()) {
            return;
        }
        finishTaskPositioning();
    }

    public void finishTaskPositioning() {
        this.mService.mAnimationHandler.post(new Runnable() { // from class: com.android.server.wm.TaskPositioningController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TaskPositioningController.this.lambda$finishTaskPositioning$3();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishTaskPositioning$3() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                cleanUpTaskPositioner();
                this.mPositioningDisplay = null;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public final void cleanUpTaskPositioner() {
        TaskPositioner taskPositioner = this.mTaskPositioner;
        if (taskPositioner == null) {
            return;
        }
        this.mTaskPositioner = null;
        taskPositioner.unregister();
    }
}
