package com.android.server.p014wm;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.input.InputManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.InputConstants;
import android.os.RemoteException;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.util.Slog;
import android.view.Display;
import android.view.DragEvent;
import android.view.InputApplicationHandle;
import android.view.InputChannel;
import android.view.InputWindowHandle;
import android.view.SurfaceControl;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.view.IDragAndDropPermissions;
import com.android.server.LocalServices;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p014wm.DragState;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.DragState */
/* loaded from: classes2.dex */
public class DragState {
    public ValueAnimator mAnimator;
    public boolean mCrossProfileCopyAllowed;
    public float mCurrentX;
    public float mCurrentY;
    public ClipData mData;
    public ClipDescription mDataDescription;
    public DisplayContent mDisplayContent;
    public final DragDropController mDragDropController;
    public boolean mDragInProgress;
    public boolean mDragResult;
    public int mFlags;
    public InputInterceptor mInputInterceptor;
    public SurfaceControl mInputSurface;
    public boolean mIsClosing;
    public IBinder mLocalWin;
    public float mOriginalAlpha;
    public float mOriginalX;
    public float mOriginalY;
    public int mPid;
    public boolean mRelinquishDragSurfaceToDropTarget;
    public final WindowManagerService mService;
    public int mSourceUserId;
    public SurfaceControl mSurfaceControl;
    public float mThumbOffsetX;
    public float mThumbOffsetY;
    public IBinder mToken;
    public int mTouchSource;
    public final SurfaceControl.Transaction mTransaction;
    public int mUid;
    public float mAnimatedScale = 1.0f;
    public volatile boolean mAnimationCompleted = false;
    public final Interpolator mCubicEaseOutInterpolator = new DecelerateInterpolator(1.5f);
    public final Point mDisplaySize = new Point();
    public final Rect mTmpClipRect = new Rect();
    public ArrayList<WindowState> mNotifiedWindows = new ArrayList<>();

    public DragState(WindowManagerService windowManagerService, DragDropController dragDropController, IBinder iBinder, SurfaceControl surfaceControl, int i, IBinder iBinder2) {
        this.mService = windowManagerService;
        this.mDragDropController = dragDropController;
        this.mToken = iBinder;
        this.mSurfaceControl = surfaceControl;
        this.mFlags = i;
        this.mLocalWin = iBinder2;
        this.mTransaction = windowManagerService.mTransactionFactory.get();
    }

    public boolean isClosing() {
        return this.mIsClosing;
    }

    public final CompletableFuture<Void> showInputSurface() {
        if (this.mInputSurface == null) {
            this.mInputSurface = this.mService.makeSurfaceBuilder(this.mDisplayContent.getSession()).setContainerLayer().setName("Drag and Drop Input Consumer").setCallsite("DragState.showInputSurface").setParent(this.mDisplayContent.getOverlayLayer()).build();
        }
        InputWindowHandle inputWindowHandle = getInputWindowHandle();
        if (inputWindowHandle == null) {
            Slog.w(StartingSurfaceController.TAG, "Drag is in progress but there is no drag window handle.");
            return CompletableFuture.completedFuture(null);
        }
        Rect rect = this.mTmpClipRect;
        Point point = this.mDisplaySize;
        rect.set(0, 0, point.x, point.y);
        this.mTransaction.show(this.mInputSurface).setInputWindowInfo(this.mInputSurface, inputWindowHandle).setLayer(this.mInputSurface, Integer.MAX_VALUE).setCrop(this.mInputSurface, this.mTmpClipRect);
        final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        this.mTransaction.addWindowInfosReportedListener(new Runnable() { // from class: com.android.server.wm.DragState$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                completableFuture.complete(null);
            }
        }).apply();
        return completableFuture;
    }

    public void closeLocked() {
        float f;
        float f2;
        SurfaceControl surfaceControl;
        this.mIsClosing = true;
        if (this.mInputInterceptor != null) {
            Slog.d(StartingSurfaceController.TAG, "unregistering drag input channel");
            this.mDragDropController.sendHandlerMessage(1, this.mInputInterceptor);
            this.mInputInterceptor = null;
        }
        if (this.mDragInProgress) {
            Slog.d(StartingSurfaceController.TAG, "broadcasting DRAG_ENDED");
            Iterator<WindowState> it = this.mNotifiedWindows.iterator();
            while (it.hasNext()) {
                WindowState next = it.next();
                if (this.mDragResult || next.mSession.mPid != this.mPid) {
                    f = 0.0f;
                    f2 = 0.0f;
                    surfaceControl = null;
                } else {
                    float translateToWindowX = next.translateToWindowX(this.mCurrentX);
                    float translateToWindowY = next.translateToWindowY(this.mCurrentY);
                    if (relinquishDragSurfaceToDragSource()) {
                        f = translateToWindowX;
                        f2 = translateToWindowY;
                        surfaceControl = this.mSurfaceControl;
                    } else {
                        surfaceControl = null;
                        f = translateToWindowX;
                        f2 = translateToWindowY;
                    }
                }
                DragEvent obtain = DragEvent.obtain(4, f, f2, this.mThumbOffsetX, this.mThumbOffsetY, null, null, null, surfaceControl, null, this.mDragResult);
                try {
                    next.mClient.dispatchDragEvent(obtain);
                } catch (RemoteException unused) {
                    Slog.w(StartingSurfaceController.TAG, "Unable to drag-end window " + next);
                }
                if (WindowManagerService.MY_PID != next.mSession.mPid) {
                    obtain.recycle();
                }
            }
            this.mNotifiedWindows.clear();
            this.mDragInProgress = false;
        }
        if (isFromSource(8194)) {
            this.mService.restorePointerIconLocked(this.mDisplayContent, this.mCurrentX, this.mCurrentY);
            this.mTouchSource = 0;
        }
        SurfaceControl surfaceControl2 = this.mInputSurface;
        if (surfaceControl2 != null) {
            this.mTransaction.remove(surfaceControl2).apply();
            this.mInputSurface = null;
        }
        if (this.mSurfaceControl != null) {
            if (!this.mRelinquishDragSurfaceToDropTarget && !relinquishDragSurfaceToDragSource()) {
                this.mTransaction.reparent(this.mSurfaceControl, null).apply();
            } else {
                this.mDragDropController.sendTimeoutMessage(3, this.mSurfaceControl, 5000L);
            }
            this.mSurfaceControl = null;
        }
        if (this.mAnimator != null && !this.mAnimationCompleted) {
            Slog.wtf(StartingSurfaceController.TAG, "Unexpectedly destroying mSurfaceControl while animation is running");
        }
        this.mFlags = 0;
        this.mLocalWin = null;
        this.mToken = null;
        this.mData = null;
        this.mThumbOffsetY = 0.0f;
        this.mThumbOffsetX = 0.0f;
        this.mNotifiedWindows = null;
        this.mDragDropController.onDragStateClosedLocked(this);
    }

    public boolean reportDropWindowLock(IBinder iBinder, float f, float f2) {
        ClipData clipData;
        if (this.mAnimator != null) {
            return false;
        }
        WindowState windowState = this.mService.mInputToWindowMap.get(iBinder);
        if (!isWindowNotified(windowState)) {
            this.mDragResult = false;
            endDragLocked();
            Slog.d(StartingSurfaceController.TAG, "Drop outside a valid window " + windowState);
            return false;
        }
        Slog.d(StartingSurfaceController.TAG, "sending DROP to " + windowState);
        int userId = UserHandle.getUserId(windowState.getOwningUid());
        int i = this.mFlags;
        IDragAndDropPermissions dragAndDropPermissionsHandler = ((i & 256) == 0 || (i & 3) == 0 || this.mData == null) ? null : new DragAndDropPermissionsHandler(this.mService.mGlobalLock, this.mData, this.mUid, windowState.getOwningPackage(), this.mFlags & FrameworkStatsLog.f411x277c884, this.mSourceUserId, userId);
        int i2 = this.mSourceUserId;
        if (i2 != userId && (clipData = this.mData) != null) {
            clipData.fixUris(i2);
        }
        IBinder asBinder = windowState.mClient.asBinder();
        DragEvent obtainDragEvent = obtainDragEvent(3, f, f2, this.mData, targetInterceptsGlobalDrag(windowState), dragAndDropPermissionsHandler);
        try {
            try {
                windowState.mClient.dispatchDragEvent(obtainDragEvent);
                this.mDragDropController.sendTimeoutMessage(0, asBinder, 5000L);
                if (WindowManagerService.MY_PID != windowState.mSession.mPid) {
                    obtainDragEvent.recycle();
                }
                this.mToken = asBinder;
                return true;
            } catch (RemoteException unused) {
                Slog.w(StartingSurfaceController.TAG, "can't send drop notification to win " + windowState);
                endDragLocked();
                if (WindowManagerService.MY_PID != windowState.mSession.mPid) {
                    obtainDragEvent.recycle();
                }
                return false;
            }
        } catch (Throwable th) {
            if (WindowManagerService.MY_PID != windowState.mSession.mPid) {
                obtainDragEvent.recycle();
            }
            throw th;
        }
    }

    /* renamed from: com.android.server.wm.DragState$InputInterceptor */
    /* loaded from: classes2.dex */
    public class InputInterceptor {
        public InputChannel mClientChannel;
        public InputApplicationHandle mDragApplicationHandle = new InputApplicationHandle(new Binder(), "drag", InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS);
        public InputWindowHandle mDragWindowHandle;
        public DragInputEventReceiver mInputEventReceiver;

        public InputInterceptor(Display display) {
            this.mClientChannel = DragState.this.mService.mInputManager.createInputChannel("drag");
            this.mInputEventReceiver = new DragInputEventReceiver(this.mClientChannel, DragState.this.mService.f1164mH.getLooper(), DragState.this.mDragDropController);
            InputWindowHandle inputWindowHandle = new InputWindowHandle(this.mDragApplicationHandle, display.getDisplayId());
            this.mDragWindowHandle = inputWindowHandle;
            inputWindowHandle.name = "drag";
            inputWindowHandle.token = this.mClientChannel.getToken();
            InputWindowHandle inputWindowHandle2 = this.mDragWindowHandle;
            inputWindowHandle2.layoutParamsType = 2016;
            inputWindowHandle2.dispatchingTimeoutMillis = InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
            inputWindowHandle2.ownerPid = WindowManagerService.MY_PID;
            inputWindowHandle2.ownerUid = WindowManagerService.MY_UID;
            inputWindowHandle2.scaleFactor = 1.0f;
            inputWindowHandle2.inputConfig = 272;
            inputWindowHandle2.touchableRegion.setEmpty();
            if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ORIENTATION, -694710814, 0, (String) null, (Object[]) null);
            }
            DragState.this.mService.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.DragState$InputInterceptor$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DragState.InputInterceptor.lambda$new$0((DisplayContent) obj);
                }
            });
        }

        public static /* synthetic */ void lambda$new$0(DisplayContent displayContent) {
            displayContent.getDisplayRotation().pause();
        }

        public void tearDown() {
            DragState.this.mService.mInputManager.removeInputChannel(this.mClientChannel.getToken());
            this.mInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
            this.mClientChannel.dispose();
            this.mClientChannel = null;
            this.mDragWindowHandle = null;
            this.mDragApplicationHandle = null;
            if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_ORIENTATION, 269576220, 0, (String) null, (Object[]) null);
            }
            DragState.this.mService.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.DragState$InputInterceptor$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DragState.InputInterceptor.lambda$tearDown$1((DisplayContent) obj);
                }
            });
        }

        public static /* synthetic */ void lambda$tearDown$1(DisplayContent displayContent) {
            displayContent.getDisplayRotation().resume();
        }
    }

    public InputChannel getInputChannel() {
        InputInterceptor inputInterceptor = this.mInputInterceptor;
        if (inputInterceptor == null) {
            return null;
        }
        return inputInterceptor.mClientChannel;
    }

    public InputWindowHandle getInputWindowHandle() {
        InputInterceptor inputInterceptor = this.mInputInterceptor;
        if (inputInterceptor == null) {
            return null;
        }
        return inputInterceptor.mDragWindowHandle;
    }

    public CompletableFuture<Void> register(Display display) {
        display.getRealSize(this.mDisplaySize);
        Slog.d(StartingSurfaceController.TAG, "registering drag input channel");
        if (this.mInputInterceptor != null) {
            Slog.e(StartingSurfaceController.TAG, "Duplicate register of drag input channel");
            return CompletableFuture.completedFuture(null);
        }
        this.mInputInterceptor = new InputInterceptor(display);
        return showInputSurface();
    }

    public void broadcastDragStartedLocked(final float f, final float f2) {
        this.mCurrentX = f;
        this.mOriginalX = f;
        this.mCurrentY = f2;
        this.mOriginalY = f2;
        ClipData clipData = this.mData;
        this.mDataDescription = clipData != null ? clipData.getDescription() : null;
        this.mNotifiedWindows.clear();
        this.mDragInProgress = true;
        this.mSourceUserId = UserHandle.getUserId(this.mUid);
        this.mCrossProfileCopyAllowed = true ^ ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserRestriction(this.mSourceUserId, "no_cross_profile_copy_paste");
        Slog.d(StartingSurfaceController.TAG, "broadcasting DRAG_STARTED at (" + f + ", " + f2 + ")");
        final boolean containsApplicationExtras = containsApplicationExtras(this.mDataDescription);
        this.mService.mRoot.forAllWindows(new Consumer() { // from class: com.android.server.wm.DragState$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DragState.this.lambda$broadcastDragStartedLocked$1(f, f2, containsApplicationExtras, (WindowState) obj);
            }
        }, false);
    }

    /* renamed from: sendDragStartedLocked */
    public final void lambda$broadcastDragStartedLocked$1(WindowState windowState, float f, float f2, boolean z) {
        boolean targetInterceptsGlobalDrag = targetInterceptsGlobalDrag(windowState);
        if (this.mDragInProgress && isValidDropTarget(windowState, z, targetInterceptsGlobalDrag)) {
            DragEvent obtainDragEvent = obtainDragEvent(1, windowState.translateToWindowX(f), windowState.translateToWindowY(f2), targetInterceptsGlobalDrag ? this.mData.copyForTransferWithActivityInfo() : null, false, null);
            try {
                try {
                    windowState.mClient.dispatchDragEvent(obtainDragEvent);
                    this.mNotifiedWindows.add(windowState);
                    if (WindowManagerService.MY_PID == windowState.mSession.mPid) {
                        return;
                    }
                } catch (RemoteException unused) {
                    Slog.w(StartingSurfaceController.TAG, "Unable to drag-start window " + windowState);
                    if (WindowManagerService.MY_PID == windowState.mSession.mPid) {
                        return;
                    }
                }
                obtainDragEvent.recycle();
            } catch (Throwable th) {
                if (WindowManagerService.MY_PID != windowState.mSession.mPid) {
                    obtainDragEvent.recycle();
                }
                throw th;
            }
        }
    }

    public final boolean containsApplicationExtras(ClipDescription clipDescription) {
        if (clipDescription == null) {
            return false;
        }
        return clipDescription.hasMimeType("application/vnd.android.activity") || clipDescription.hasMimeType("application/vnd.android.shortcut") || clipDescription.hasMimeType("application/vnd.android.task");
    }

    public final boolean isValidDropTarget(WindowState windowState, boolean z, boolean z2) {
        if (windowState == null) {
            return false;
        }
        boolean z3 = this.mLocalWin == windowState.mClient.asBinder();
        if ((z3 || z2 || !z) && windowState.isPotentialDragTarget(z2)) {
            if (((this.mFlags & 256) == 0 || !targetWindowSupportsGlobalDrag(windowState)) && !z3) {
                return false;
            }
            return z2 || this.mCrossProfileCopyAllowed || this.mSourceUserId == UserHandle.getUserId(windowState.getOwningUid());
        }
        return false;
    }

    public final boolean targetWindowSupportsGlobalDrag(WindowState windowState) {
        ActivityRecord activityRecord = windowState.mActivityRecord;
        return activityRecord == null || activityRecord.mTargetSdk >= 24;
    }

    public boolean targetInterceptsGlobalDrag(WindowState windowState) {
        return (windowState.mAttrs.privateFlags & Integer.MIN_VALUE) != 0;
    }

    public void sendDragStartedIfNeededLocked(WindowState windowState) {
        if (!this.mDragInProgress || isWindowNotified(windowState)) {
            return;
        }
        Slog.d(StartingSurfaceController.TAG, "need to send DRAG_STARTED to new window " + windowState);
        lambda$broadcastDragStartedLocked$1(windowState, this.mCurrentX, this.mCurrentY, containsApplicationExtras(this.mDataDescription));
    }

    public boolean isWindowNotified(WindowState windowState) {
        Iterator<WindowState> it = this.mNotifiedWindows.iterator();
        while (it.hasNext()) {
            if (it.next() == windowState) {
                return true;
            }
        }
        return false;
    }

    public void endDragLocked() {
        if (this.mAnimator != null) {
            return;
        }
        if (!this.mDragResult && !isAccessibilityDragDrop() && !relinquishDragSurfaceToDragSource()) {
            this.mAnimator = createReturnAnimationLocked();
        } else {
            closeLocked();
        }
    }

    public void cancelDragLocked(boolean z) {
        if (this.mAnimator != null) {
            return;
        }
        if (!this.mDragInProgress || z || isAccessibilityDragDrop()) {
            closeLocked();
        } else {
            this.mAnimator = createCancelAnimationLocked();
        }
    }

    public void updateDragSurfaceLocked(boolean z, float f, float f2) {
        if (this.mAnimator != null) {
            return;
        }
        this.mCurrentX = f;
        this.mCurrentY = f2;
        if (z) {
            this.mTransaction.setPosition(this.mSurfaceControl, f - this.mThumbOffsetX, f2 - this.mThumbOffsetY).apply();
            if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, 342460966, 20, (String) null, new Object[]{String.valueOf(this.mSurfaceControl), Long.valueOf((int) (f - this.mThumbOffsetX)), Long.valueOf((int) (f2 - this.mThumbOffsetY))});
            }
        }
    }

    public final DragEvent obtainDragEvent(int i, float f, float f2, ClipData clipData, boolean z, IDragAndDropPermissions iDragAndDropPermissions) {
        return DragEvent.obtain(i, f, f2, this.mThumbOffsetX, this.mThumbOffsetY, null, this.mDataDescription, clipData, z ? this.mSurfaceControl : null, iDragAndDropPermissions, false);
    }

    public final ValueAnimator createReturnAnimationLocked() {
        float f = this.mCurrentX;
        float f2 = this.mThumbOffsetX;
        PropertyValuesHolder ofFloat = PropertyValuesHolder.ofFloat("x", f - f2, this.mOriginalX - f2);
        float f3 = this.mCurrentY;
        float f4 = this.mThumbOffsetY;
        PropertyValuesHolder ofFloat2 = PropertyValuesHolder.ofFloat("y", f3 - f4, this.mOriginalY - f4);
        float f5 = this.mAnimatedScale;
        PropertyValuesHolder ofFloat3 = PropertyValuesHolder.ofFloat("scale", f5, f5);
        float f6 = this.mOriginalAlpha;
        final ValueAnimator ofPropertyValuesHolder = ValueAnimator.ofPropertyValuesHolder(ofFloat, ofFloat2, ofFloat3, PropertyValuesHolder.ofFloat("alpha", f6, f6 / 2.0f));
        float f7 = this.mOriginalX - this.mCurrentX;
        float f8 = this.mOriginalY - this.mCurrentY;
        double sqrt = Math.sqrt((f7 * f7) + (f8 * f8));
        Point point = this.mDisplaySize;
        int i = point.x;
        int i2 = point.y;
        AnimationListener animationListener = new AnimationListener();
        ofPropertyValuesHolder.setDuration(((long) ((sqrt / Math.sqrt((i * i) + (i2 * i2))) * 180.0d)) + 195);
        ofPropertyValuesHolder.setInterpolator(this.mCubicEaseOutInterpolator);
        ofPropertyValuesHolder.addListener(animationListener);
        ofPropertyValuesHolder.addUpdateListener(animationListener);
        this.mService.mAnimationHandler.post(new Runnable() { // from class: com.android.server.wm.DragState$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ofPropertyValuesHolder.start();
            }
        });
        return ofPropertyValuesHolder;
    }

    public final ValueAnimator createCancelAnimationLocked() {
        float f = this.mCurrentX;
        PropertyValuesHolder ofFloat = PropertyValuesHolder.ofFloat("x", f - this.mThumbOffsetX, f);
        float f2 = this.mCurrentY;
        final ValueAnimator ofPropertyValuesHolder = ValueAnimator.ofPropertyValuesHolder(ofFloat, PropertyValuesHolder.ofFloat("y", f2 - this.mThumbOffsetY, f2), PropertyValuesHolder.ofFloat("scale", this.mAnimatedScale, 0.0f), PropertyValuesHolder.ofFloat("alpha", this.mOriginalAlpha, 0.0f));
        AnimationListener animationListener = new AnimationListener();
        ofPropertyValuesHolder.setDuration(195L);
        ofPropertyValuesHolder.setInterpolator(this.mCubicEaseOutInterpolator);
        ofPropertyValuesHolder.addListener(animationListener);
        ofPropertyValuesHolder.addUpdateListener(animationListener);
        this.mService.mAnimationHandler.post(new Runnable() { // from class: com.android.server.wm.DragState$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ofPropertyValuesHolder.start();
            }
        });
        return ofPropertyValuesHolder;
    }

    public final boolean isFromSource(int i) {
        return (this.mTouchSource & i) == i;
    }

    public void overridePointerIconLocked(int i) {
        this.mTouchSource = i;
        if (isFromSource(8194)) {
            InputManager.getInstance().setPointerIconType(1021);
        }
    }

    /* renamed from: com.android.server.wm.DragState$AnimationListener */
    /* loaded from: classes2.dex */
    public class AnimationListener implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }

        public AnimationListener() {
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            SurfaceControl.Transaction transaction = DragState.this.mService.mTransactionFactory.get();
            try {
                transaction.setPosition(DragState.this.mSurfaceControl, ((Float) valueAnimator.getAnimatedValue("x")).floatValue(), ((Float) valueAnimator.getAnimatedValue("y")).floatValue());
                transaction.setAlpha(DragState.this.mSurfaceControl, ((Float) valueAnimator.getAnimatedValue("alpha")).floatValue());
                transaction.setMatrix(DragState.this.mSurfaceControl, ((Float) valueAnimator.getAnimatedValue("scale")).floatValue(), 0.0f, 0.0f, ((Float) valueAnimator.getAnimatedValue("scale")).floatValue());
                transaction.apply();
                transaction.close();
            } catch (Throwable th) {
                if (transaction != null) {
                    try {
                        transaction.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            DragState.this.mAnimationCompleted = true;
            DragState.this.mDragDropController.sendHandlerMessage(2, null);
        }
    }

    public boolean isAccessibilityDragDrop() {
        return (this.mFlags & 1024) != 0;
    }

    public final boolean relinquishDragSurfaceToDragSource() {
        return (this.mFlags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0;
    }
}
