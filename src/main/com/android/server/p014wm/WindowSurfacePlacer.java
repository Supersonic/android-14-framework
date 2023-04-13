package com.android.server.p014wm;

import android.os.Debug;
import android.util.Slog;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.WindowSurfacePlacer */
/* loaded from: classes2.dex */
public class WindowSurfacePlacer {
    public int mDeferredRequests;
    public int mLayoutRepeatCount;
    public final WindowManagerService mService;
    public boolean mTraversalScheduled;
    public boolean mInLayout = false;
    public int mDeferDepth = 0;
    public final Traverser mPerformSurfacePlacement = new Traverser();

    /* renamed from: com.android.server.wm.WindowSurfacePlacer$Traverser */
    /* loaded from: classes2.dex */
    public class Traverser implements Runnable {
        public Traverser() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (WindowSurfacePlacer.this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowSurfacePlacer.this.performSurfacePlacement();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    public WindowSurfacePlacer(WindowManagerService windowManagerService) {
        this.mService = windowManagerService;
    }

    public void deferLayout() {
        this.mDeferDepth++;
    }

    public void continueLayout(boolean z) {
        int i = this.mDeferDepth - 1;
        this.mDeferDepth = i;
        if (i > 0) {
            return;
        }
        if (z || this.mDeferredRequests > 0) {
            performSurfacePlacement();
            this.mDeferredRequests = 0;
        }
    }

    public boolean isLayoutDeferred() {
        return this.mDeferDepth > 0;
    }

    public void performSurfacePlacementIfScheduled() {
        if (this.mTraversalScheduled) {
            performSurfacePlacement();
        }
    }

    public final void performSurfacePlacement() {
        performSurfacePlacement(false);
    }

    public final void performSurfacePlacement(boolean z) {
        if (this.mDeferDepth > 0 && !z) {
            this.mDeferredRequests++;
            return;
        }
        int i = 6;
        do {
            this.mTraversalScheduled = false;
            performSurfacePlacementLoop();
            this.mService.mAnimationHandler.removeCallbacks(this.mPerformSurfacePlacement);
            i--;
            if (!this.mTraversalScheduled) {
                break;
            }
        } while (i > 0);
        this.mService.mRoot.mWallpaperActionPending = false;
    }

    public final void performSurfacePlacementLoop() {
        if (this.mInLayout) {
            Slog.w(StartingSurfaceController.TAG, "performLayoutAndPlaceSurfacesLocked called while in layout. Callers=" + Debug.getCallers(3));
        } else if (this.mService.getDefaultDisplayContentLocked().mWaitingForConfig) {
        } else {
            WindowManagerService windowManagerService = this.mService;
            if (windowManagerService.mDisplayReady) {
                this.mInLayout = true;
                if (!windowManagerService.mForceRemoves.isEmpty()) {
                    while (!this.mService.mForceRemoves.isEmpty()) {
                        WindowState remove = this.mService.mForceRemoves.remove(0);
                        Slog.i(StartingSurfaceController.TAG, "Force removing: " + remove);
                        remove.removeImmediately();
                    }
                    Slog.w(StartingSurfaceController.TAG, "Due to memory failure, waiting a bit for next layout");
                    Object obj = new Object();
                    synchronized (obj) {
                        try {
                            obj.wait(250L);
                        } catch (InterruptedException unused) {
                        }
                    }
                }
                try {
                    this.mService.mRoot.performSurfacePlacement();
                    this.mInLayout = false;
                    if (this.mService.mRoot.isLayoutNeeded()) {
                        int i = this.mLayoutRepeatCount + 1;
                        this.mLayoutRepeatCount = i;
                        if (i < 6) {
                            requestTraversal();
                        } else {
                            Slog.e(StartingSurfaceController.TAG, "Performed 6 layouts in a row. Skipping");
                            this.mLayoutRepeatCount = 0;
                        }
                    } else {
                        this.mLayoutRepeatCount = 0;
                    }
                    WindowManagerService windowManagerService2 = this.mService;
                    if (!windowManagerService2.mWindowsChanged || windowManagerService2.mWindowChangeListeners.isEmpty()) {
                        return;
                    }
                    this.mService.f1164mH.removeMessages(19);
                    this.mService.f1164mH.sendEmptyMessage(19);
                } catch (RuntimeException e) {
                    this.mInLayout = false;
                    Slog.wtf(StartingSurfaceController.TAG, "Unhandled exception while laying out windows", e);
                }
            }
        }
    }

    public boolean isInLayout() {
        return this.mInLayout;
    }

    public void requestTraversal() {
        if (this.mTraversalScheduled) {
            return;
        }
        this.mTraversalScheduled = true;
        if (this.mDeferDepth > 0) {
            this.mDeferredRequests++;
        } else {
            this.mService.mAnimationHandler.post(this.mPerformSurfacePlacement);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println(str + "mTraversalScheduled=" + this.mTraversalScheduled);
    }
}
