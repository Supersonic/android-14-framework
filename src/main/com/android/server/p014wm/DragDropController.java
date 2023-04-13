package com.android.server.p014wm;

import android.content.ClipData;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Slog;
import android.view.IWindow;
import android.view.SurfaceControl;
import android.view.accessibility.AccessibilityManager;
import com.android.server.p014wm.DragState;
import com.android.server.p014wm.WindowManagerInternal;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
/* renamed from: com.android.server.wm.DragDropController */
/* loaded from: classes2.dex */
public class DragDropController {
    public AtomicReference<WindowManagerInternal.IDragDropCallback> mCallback = new AtomicReference<>(new WindowManagerInternal.IDragDropCallback() { // from class: com.android.server.wm.DragDropController.1
    });
    public DragState mDragState;
    public final Handler mHandler;
    public WindowManagerService mService;

    public DragDropController(WindowManagerService windowManagerService, Looper looper) {
        this.mService = windowManagerService;
        this.mHandler = new DragHandler(windowManagerService, looper);
    }

    public boolean dragDropActiveLocked() {
        DragState dragState = this.mDragState;
        return (dragState == null || dragState.isClosing()) ? false : true;
    }

    public void registerCallback(WindowManagerInternal.IDragDropCallback iDragDropCallback) {
        Objects.requireNonNull(iDragDropCallback);
        this.mCallback.set(iDragDropCallback);
    }

    public void sendDragStartedIfNeededLocked(WindowState windowState) {
        this.mDragState.sendDragStartedIfNeededLocked(windowState);
    }

    public IBinder performDrag(int i, int i2, IWindow iWindow, int i3, SurfaceControl surfaceControl, int i4, float f, float f2, float f3, float f4, ClipData clipData) {
        SurfaceControl surfaceControl2;
        AtomicReference<WindowManagerInternal.IDragDropCallback> atomicReference;
        WindowManagerInternal.IDragDropCallback iDragDropCallback;
        WindowManagerInternal.IDragDropCallback iDragDropCallback2;
        boolean z;
        Slog.d(StartingSurfaceController.TAG, "perform drag: win=" + iWindow + " surface=" + surfaceControl + " flags=" + Integer.toHexString(i3) + " data=" + clipData + " touch(" + f + "," + f2 + ") thumb center(" + f3 + "," + f4 + ")");
        Binder binder = new Binder();
        boolean prePerformDrag = this.mCallback.get().prePerformDrag(iWindow, binder, i4, f, f2, f3, f4, clipData);
        try {
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    try {
                        if (!prePerformDrag) {
                            Slog.w(StartingSurfaceController.TAG, "IDragDropCallback rejects the performDrag request");
                            if (surfaceControl != null) {
                                surfaceControl.release();
                            }
                        } else if (!dragDropActiveLocked()) {
                            WindowState windowForClientLocked = this.mService.windowForClientLocked((Session) null, iWindow, false);
                            if (windowForClientLocked != null && windowForClientLocked.canReceiveTouchInput()) {
                                DisplayContent displayContent = windowForClientLocked.getDisplayContent();
                                if (displayContent != null) {
                                    float f5 = (i3 & 512) == 0 ? 0.7071f : 1.0f;
                                    DragState dragState = new DragState(this.mService, this, new Binder(), surfaceControl, i3, iWindow.asBinder());
                                    this.mDragState = dragState;
                                    try {
                                        dragState.mPid = i;
                                        dragState.mUid = i2;
                                        dragState.mOriginalAlpha = f5;
                                        dragState.mAnimatedScale = windowForClientLocked.mGlobalScale;
                                        dragState.mToken = binder;
                                        dragState.mDisplayContent = displayContent;
                                        dragState.mData = clipData;
                                        if ((i3 & 1024) != 0) {
                                            dragState.broadcastDragStartedLocked(f, f2);
                                            sendTimeoutMessage(0, windowForClientLocked.mClient.asBinder(), getAccessibilityManager().getRecommendedTimeoutMillis(60000, 4));
                                            WindowManagerService.resetPriorityAfterLockedSection();
                                            return binder;
                                        }
                                        CompletableFuture<Boolean> registerInputChannel = this.mCallback.get().registerInputChannel(this.mDragState, displayContent.getDisplay(), this.mService.mInputManager, windowForClientLocked.mInputChannel);
                                        WindowManagerService.resetPriorityAfterLockedSection();
                                        try {
                                            z = registerInputChannel.get(5000L, TimeUnit.MILLISECONDS).booleanValue();
                                        } catch (Exception e) {
                                            Slog.e(StartingSurfaceController.TAG, "Exception thrown while waiting for touch focus transfer", e);
                                            z = false;
                                        }
                                        synchronized (this.mService.mGlobalLock) {
                                            WindowManagerService.boostPriorityForLockedSection();
                                            if (!z) {
                                                Slog.e(StartingSurfaceController.TAG, "Unable to transfer touch focus");
                                                this.mDragState.closeLocked();
                                                WindowManagerService.resetPriorityAfterLockedSection();
                                                this.mCallback.get().postPerformDrag();
                                                return null;
                                            }
                                            DragState dragState2 = this.mDragState;
                                            SurfaceControl surfaceControl3 = dragState2.mSurfaceControl;
                                            dragState2.broadcastDragStartedLocked(f, f2);
                                            this.mDragState.overridePointerIconLocked(i4);
                                            DragState dragState3 = this.mDragState;
                                            dragState3.mThumbOffsetX = f3;
                                            dragState3.mThumbOffsetY = f4;
                                            SurfaceControl.Transaction transaction = dragState3.mTransaction;
                                            transaction.setAlpha(surfaceControl3, dragState3.mOriginalAlpha);
                                            transaction.show(surfaceControl3);
                                            displayContent.reparentToOverlay(transaction, surfaceControl3);
                                            this.mDragState.updateDragSurfaceLocked(true, f, f2);
                                            WindowManagerService.resetPriorityAfterLockedSection();
                                            return binder;
                                        }
                                    } catch (Throwable th) {
                                        th = th;
                                        surfaceControl2 = null;
                                        if (surfaceControl2 != null) {
                                            surfaceControl2.release();
                                        }
                                        throw th;
                                    }
                                }
                                Slog.w(StartingSurfaceController.TAG, "display content is null");
                                if (surfaceControl != null) {
                                    surfaceControl.release();
                                }
                            }
                            Slog.w(StartingSurfaceController.TAG, "Bad requesting window " + iWindow);
                            if (surfaceControl != null) {
                                surfaceControl.release();
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            this.mCallback.get().postPerformDrag();
                            return null;
                        } else {
                            Slog.w(StartingSurfaceController.TAG, "Drag already in progress");
                            if (surfaceControl != null) {
                                surfaceControl.release();
                            }
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    } catch (Throwable th2) {
                        th = th2;
                        surfaceControl2 = surfaceControl;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    surfaceControl2 = surfaceControl;
                }
            }
        } finally {
            this.mCallback.get().postPerformDrag();
        }
    }

    public void reportDropResult(IWindow iWindow, boolean z) {
        IBinder asBinder = iWindow.asBinder();
        Slog.d(StartingSurfaceController.TAG, "Drop result=" + z + " reported by " + asBinder);
        this.mCallback.get().preReportDropResult(iWindow, z);
        try {
            synchronized (this.mService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                DragState dragState = this.mDragState;
                if (dragState == null) {
                    Slog.w(StartingSurfaceController.TAG, "Drop result given but no drag in progress");
                } else if (dragState.mToken != asBinder) {
                    Slog.w(StartingSurfaceController.TAG, "Invalid drop-result claim by " + iWindow);
                    throw new IllegalStateException("reportDropResult() by non-recipient");
                } else {
                    boolean z2 = false;
                    this.mHandler.removeMessages(0, iWindow.asBinder());
                    WindowState windowForClientLocked = this.mService.windowForClientLocked((Session) null, iWindow, false);
                    if (windowForClientLocked == null) {
                        Slog.w(StartingSurfaceController.TAG, "Bad result-reporting window " + iWindow);
                    } else {
                        DragState dragState2 = this.mDragState;
                        dragState2.mDragResult = z;
                        if (z && dragState2.targetInterceptsGlobalDrag(windowForClientLocked)) {
                            z2 = true;
                        }
                        dragState2.mRelinquishDragSurfaceToDropTarget = z2;
                        this.mDragState.endDragLocked();
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            this.mCallback.get().postReportDropResult();
        }
    }

    public void cancelDragAndDrop(IBinder iBinder, boolean z) {
        Slog.d(StartingSurfaceController.TAG, "cancelDragAndDrop");
        this.mCallback.get().preCancelDragAndDrop(iBinder);
        try {
            synchronized (this.mService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                DragState dragState = this.mDragState;
                if (dragState == null) {
                    Slog.w(StartingSurfaceController.TAG, "cancelDragAndDrop() without prepareDrag()");
                    throw new IllegalStateException("cancelDragAndDrop() without prepareDrag()");
                } else if (dragState.mToken != iBinder) {
                    Slog.w(StartingSurfaceController.TAG, "cancelDragAndDrop() does not match prepareDrag()");
                    throw new IllegalStateException("cancelDragAndDrop() does not match prepareDrag()");
                } else {
                    dragState.mDragResult = false;
                    dragState.cancelDragLocked(z);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            this.mCallback.get().postCancelDragAndDrop();
        }
    }

    public void handleMotionEvent(boolean z, float f, float f2) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (!dragDropActiveLocked()) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                this.mDragState.updateDragSurfaceLocked(z, f, f2);
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void dragRecipientEntered(IWindow iWindow) {
        Slog.d(StartingSurfaceController.TAG, "Drag into new candidate view @ " + iWindow.asBinder());
        this.mCallback.get().dragRecipientEntered(iWindow);
    }

    public void dragRecipientExited(IWindow iWindow) {
        Slog.d(StartingSurfaceController.TAG, "Drag from old candidate view @ " + iWindow.asBinder());
        this.mCallback.get().dragRecipientExited(iWindow);
    }

    public void sendHandlerMessage(int i, Object obj) {
        this.mHandler.obtainMessage(i, obj).sendToTarget();
    }

    public void sendTimeoutMessage(int i, Object obj, long j) {
        this.mHandler.removeMessages(i, obj);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(i, obj), j);
    }

    public void onDragStateClosedLocked(DragState dragState) {
        if (this.mDragState != dragState) {
            Slog.wtf(StartingSurfaceController.TAG, "Unknown drag state is closed");
        } else {
            this.mDragState = null;
        }
    }

    public void reportDropWindow(IBinder iBinder, float f, float f2) {
        if (this.mDragState == null) {
            Slog.w(StartingSurfaceController.TAG, "Drag state is closed.");
            return;
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mDragState.reportDropWindowLock(iBinder, f, f2);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public boolean dropForAccessibility(IWindow iWindow, float f, float f2) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                boolean isEnabled = getAccessibilityManager().isEnabled();
                if (!dragDropActiveLocked()) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                } else if (!this.mDragState.isAccessibilityDragDrop() || !isEnabled) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                } else {
                    WindowState windowForClientLocked = this.mService.windowForClientLocked((Session) null, iWindow, false);
                    if (!this.mDragState.isWindowNotified(windowForClientLocked)) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return false;
                    }
                    boolean reportDropWindowLock = this.mDragState.reportDropWindowLock(windowForClientLocked.mInputChannelToken, f, f2);
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return reportDropWindowLock;
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public AccessibilityManager getAccessibilityManager() {
        return (AccessibilityManager) this.mService.mContext.getSystemService("accessibility");
    }

    /* renamed from: com.android.server.wm.DragDropController$DragHandler */
    /* loaded from: classes2.dex */
    public class DragHandler extends Handler {
        public final WindowManagerService mService;

        public DragHandler(WindowManagerService windowManagerService, Looper looper) {
            super(looper);
            this.mService = windowManagerService;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                Slog.w(StartingSurfaceController.TAG, "Timeout ending drag to win " + ((IBinder) message.obj));
                synchronized (this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (DragDropController.this.mDragState != null) {
                            DragDropController.this.mDragState.mDragResult = false;
                            DragDropController.this.mDragState.endDragLocked();
                        }
                    } finally {
                        WindowManagerService.resetPriorityAfterLockedSection();
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } else if (i == 1) {
                Slog.d(StartingSurfaceController.TAG, "Drag ending; tearing down input channel");
                DragState.InputInterceptor inputInterceptor = (DragState.InputInterceptor) message.obj;
                if (inputInterceptor == null) {
                    return;
                }
                synchronized (this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        inputInterceptor.tearDown();
                    } finally {
                        WindowManagerService.resetPriorityAfterLockedSection();
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } else if (i != 2) {
                if (i != 3) {
                    return;
                }
                synchronized (this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        this.mService.mTransactionFactory.get().reparent((SurfaceControl) message.obj, null).apply();
                    } finally {
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } else {
                synchronized (this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (DragDropController.this.mDragState == null) {
                            Slog.wtf(StartingSurfaceController.TAG, "mDragState unexpectedly became null while playing animation");
                            return;
                        }
                        DragDropController.this.mDragState.closeLocked();
                        WindowManagerService.resetPriorityAfterLockedSection();
                    } finally {
                        WindowManagerService.resetPriorityAfterLockedSection();
                    }
                }
            }
        }
    }
}
