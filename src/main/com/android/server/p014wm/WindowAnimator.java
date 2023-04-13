package com.android.server.p014wm;

import android.content.Context;
import android.os.Trace;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.Choreographer;
import android.view.SurfaceControl;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.policy.WindowManagerPolicy;
import java.io.PrintWriter;
import java.util.ArrayList;
/* renamed from: com.android.server.wm.WindowAnimator */
/* loaded from: classes2.dex */
public class WindowAnimator {
    public final Choreographer.FrameCallback mAnimationFrameCallback;
    public boolean mAnimationFrameCallbackScheduled;
    public Choreographer mChoreographer;
    public final Context mContext;
    public long mCurrentTime;
    public boolean mInExecuteAfterPrepareSurfacesRunnables;
    public boolean mLastRootAnimating;
    public Object mLastWindowFreezeSource;
    public final WindowManagerPolicy mPolicy;
    public boolean mRunningExpensiveAnimations;
    public final WindowManagerService mService;
    public final SurfaceControl.Transaction mTransaction;
    public int mBulkUpdateParams = 0;
    public SparseArray<DisplayContentsAnimator> mDisplayContentsAnimators = new SparseArray<>(2);
    public boolean mInitialized = false;
    public boolean mRemoveReplacedWindows = false;
    public boolean mNotifyWhenNoAnimation = false;
    public final ArrayList<Runnable> mAfterPrepareSurfacesRunnables = new ArrayList<>();

    public WindowAnimator(WindowManagerService windowManagerService) {
        this.mService = windowManagerService;
        this.mContext = windowManagerService.mContext;
        this.mPolicy = windowManagerService.mPolicy;
        this.mTransaction = windowManagerService.mTransactionFactory.get();
        windowManagerService.mAnimationHandler.runWithScissors(new Runnable() { // from class: com.android.server.wm.WindowAnimator$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WindowAnimator.this.lambda$new$0();
            }
        }, 0L);
        this.mAnimationFrameCallback = new Choreographer.FrameCallback() { // from class: com.android.server.wm.WindowAnimator$$ExternalSyntheticLambda1
            @Override // android.view.Choreographer.FrameCallback
            public final void doFrame(long j) {
                WindowAnimator.this.lambda$new$1(j);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mChoreographer = Choreographer.getSfInstance();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(long j) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAnimationFrameCallbackScheduled = false;
                animate(j, this.mChoreographer.getVsyncId());
                if (this.mNotifyWhenNoAnimation && !this.mLastRootAnimating) {
                    this.mService.mGlobalLock.notifyAll();
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void addDisplayLocked(int i) {
        getDisplayContentsAnimatorLocked(i);
    }

    public void removeDisplayLocked(int i) {
        this.mDisplayContentsAnimators.delete(i);
    }

    public void ready() {
        this.mInitialized = true;
    }

    public final void animate(long j, long j2) {
        if (this.mInitialized) {
            scheduleAnimation();
            RootWindowContainer rootWindowContainer = this.mService.mRoot;
            this.mCurrentTime = j / 1000000;
            this.mBulkUpdateParams = 0;
            rootWindowContainer.mOrientationChangeComplete = true;
            if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, 1984782949, 0, (String) null, (Object[]) null);
            }
            this.mService.openSurfaceTransaction();
            try {
                rootWindowContainer.handleCompleteDeferredRemoval();
                AccessibilityController accessibilityController = this.mService.mAccessibilityController;
                int size = this.mDisplayContentsAnimators.size();
                for (int i = 0; i < size; i++) {
                    DisplayContent displayContent = rootWindowContainer.getDisplayContent(this.mDisplayContentsAnimators.keyAt(i));
                    displayContent.updateWindowsForAnimator();
                    displayContent.prepareSurfaces();
                }
                for (int i2 = 0; i2 < size; i2++) {
                    int keyAt = this.mDisplayContentsAnimators.keyAt(i2);
                    rootWindowContainer.getDisplayContent(keyAt).checkAppWindowsReadyToShow();
                    if (accessibilityController.hasCallbacks()) {
                        accessibilityController.drawMagnifiedRegionBorderIfNeeded(keyAt, this.mTransaction);
                    }
                }
                cancelAnimation();
                Watermark watermark = this.mService.mWatermark;
                if (watermark != null) {
                    watermark.drawIfNeeded();
                }
            } catch (RuntimeException e) {
                Slog.wtf(StartingSurfaceController.TAG, "Unhandled exception in Window Manager", e);
            }
            boolean hasPendingLayoutChanges = rootWindowContainer.hasPendingLayoutChanges(this);
            boolean z = (this.mBulkUpdateParams != 0 || rootWindowContainer.mOrientationChangeComplete) && rootWindowContainer.copyAnimToLayoutParams();
            if (hasPendingLayoutChanges || z) {
                this.mService.mWindowPlacerLocked.requestTraversal();
            }
            boolean isAnimating = rootWindowContainer.isAnimating(5, -1);
            if (isAnimating && !this.mLastRootAnimating) {
                Trace.asyncTraceBegin(32L, "animating", 0);
            }
            if (!isAnimating && this.mLastRootAnimating) {
                this.mService.mWindowPlacerLocked.requestTraversal();
                Trace.asyncTraceEnd(32L, "animating", 0);
            }
            this.mLastRootAnimating = isAnimating;
            boolean isAnimating2 = rootWindowContainer.isAnimating(5, 11);
            if (isAnimating2 && !this.mRunningExpensiveAnimations) {
                this.mService.mSnapshotPersistQueue.setPaused(true);
                this.mTransaction.setEarlyWakeupStart();
            } else if (!isAnimating2 && this.mRunningExpensiveAnimations) {
                this.mService.mSnapshotPersistQueue.setPaused(false);
                this.mTransaction.setEarlyWakeupEnd();
            }
            this.mRunningExpensiveAnimations = isAnimating2;
            SurfaceControl.mergeToGlobalTransaction(this.mTransaction);
            this.mService.closeSurfaceTransaction("WindowAnimator");
            if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, -545190927, 0, (String) null, (Object[]) null);
            }
            if (this.mRemoveReplacedWindows) {
                rootWindowContainer.removeReplacedWindows();
                this.mRemoveReplacedWindows = false;
            }
            this.mService.mAtmService.mTaskOrganizerController.dispatchPendingEvents();
            executeAfterPrepareSurfacesRunnables();
        }
    }

    public static String bulkUpdateParamsToString(int i) {
        StringBuilder sb = new StringBuilder(128);
        if ((i & 1) != 0) {
            sb.append(" UPDATE_ROTATION");
        }
        if ((i & 2) != 0) {
            sb.append(" SET_WALLPAPER_ACTION_PENDING");
        }
        return sb.toString();
    }

    public void dumpLocked(PrintWriter printWriter, String str, boolean z) {
        String str2 = "  " + str;
        for (int i = 0; i < this.mDisplayContentsAnimators.size(); i++) {
            printWriter.print(str);
            printWriter.print("DisplayContentsAnimator #");
            printWriter.print(this.mDisplayContentsAnimators.keyAt(i));
            printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
            this.mService.mRoot.getDisplayContent(this.mDisplayContentsAnimators.keyAt(i)).dumpWindowAnimators(printWriter, str2);
            printWriter.println();
        }
        printWriter.println();
        if (z) {
            printWriter.print(str);
            printWriter.print("mCurrentTime=");
            printWriter.println(TimeUtils.formatUptime(this.mCurrentTime));
        }
        if (this.mBulkUpdateParams != 0) {
            printWriter.print(str);
            printWriter.print("mBulkUpdateParams=0x");
            printWriter.print(Integer.toHexString(this.mBulkUpdateParams));
            printWriter.println(bulkUpdateParamsToString(this.mBulkUpdateParams));
        }
    }

    public final DisplayContentsAnimator getDisplayContentsAnimatorLocked(int i) {
        if (i < 0) {
            return null;
        }
        DisplayContentsAnimator displayContentsAnimator = this.mDisplayContentsAnimators.get(i);
        if (displayContentsAnimator != null || this.mService.mRoot.getDisplayContent(i) == null) {
            return displayContentsAnimator;
        }
        DisplayContentsAnimator displayContentsAnimator2 = new DisplayContentsAnimator();
        this.mDisplayContentsAnimators.put(i, displayContentsAnimator2);
        return displayContentsAnimator2;
    }

    public void requestRemovalOfReplacedWindows(WindowState windowState) {
        this.mRemoveReplacedWindows = true;
    }

    public void scheduleAnimation() {
        if (this.mAnimationFrameCallbackScheduled) {
            return;
        }
        this.mAnimationFrameCallbackScheduled = true;
        this.mChoreographer.postFrameCallback(this.mAnimationFrameCallback);
    }

    public final void cancelAnimation() {
        if (this.mAnimationFrameCallbackScheduled) {
            this.mAnimationFrameCallbackScheduled = false;
            this.mChoreographer.removeFrameCallback(this.mAnimationFrameCallback);
        }
    }

    /* renamed from: com.android.server.wm.WindowAnimator$DisplayContentsAnimator */
    /* loaded from: classes2.dex */
    public class DisplayContentsAnimator {
        public DisplayContentsAnimator() {
        }
    }

    public boolean isAnimationScheduled() {
        return this.mAnimationFrameCallbackScheduled;
    }

    public Choreographer getChoreographer() {
        return this.mChoreographer;
    }

    public void addAfterPrepareSurfacesRunnable(Runnable runnable) {
        if (this.mInExecuteAfterPrepareSurfacesRunnables) {
            runnable.run();
            return;
        }
        this.mAfterPrepareSurfacesRunnables.add(runnable);
        scheduleAnimation();
    }

    public void executeAfterPrepareSurfacesRunnables() {
        if (this.mInExecuteAfterPrepareSurfacesRunnables) {
            return;
        }
        this.mInExecuteAfterPrepareSurfacesRunnables = true;
        int size = this.mAfterPrepareSurfacesRunnables.size();
        for (int i = 0; i < size; i++) {
            this.mAfterPrepareSurfacesRunnables.get(i).run();
        }
        this.mAfterPrepareSurfacesRunnables.clear();
        this.mInExecuteAfterPrepareSurfacesRunnables = false;
    }
}
