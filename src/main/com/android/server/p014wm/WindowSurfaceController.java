package com.android.server.p014wm;

import android.os.Debug;
import android.os.Trace;
import android.util.EventLog;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import android.view.WindowContentFrameStats;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.WindowSurfaceController */
/* loaded from: classes2.dex */
public class WindowSurfaceController {
    public final WindowStateAnimator mAnimator;
    public final WindowManagerService mService;
    public SurfaceControl mSurfaceControl;
    public final Session mWindowSession;
    public final int mWindowType;
    public final String title;
    public boolean mSurfaceShown = false;
    public float mSurfaceX = 0.0f;
    public float mSurfaceY = 0.0f;
    public float mLastDsdx = 1.0f;
    public float mLastDtdx = 0.0f;
    public float mLastDsdy = 0.0f;
    public float mLastDtdy = 1.0f;
    public float mSurfaceAlpha = 0.0f;
    public int mSurfaceLayer = 0;

    public WindowSurfaceController(String str, int i, int i2, WindowStateAnimator windowStateAnimator, int i3) {
        boolean z = false;
        this.mAnimator = windowStateAnimator;
        this.title = str;
        WindowManagerService windowManagerService = windowStateAnimator.mService;
        this.mService = windowManagerService;
        WindowState windowState = windowStateAnimator.mWin;
        this.mWindowType = i3;
        Session session = windowState.mSession;
        this.mWindowSession = session;
        Trace.traceBegin(32L, "new SurfaceControl");
        SurfaceControl.Builder callsite = windowState.makeSurface().setParent(windowState.getSurfaceControl()).setName(str).setFormat(i).setFlags(i2).setMetadata(2, i3).setMetadata(1, session.mUid).setMetadata(6, session.mPid).setCallsite("WindowSurfaceController");
        if (windowManagerService.mUseBLAST && (windowState.getAttrs().privateFlags & 33554432) != 0) {
            z = true;
        }
        if (z) {
            callsite.setBLASTLayer();
        }
        this.mSurfaceControl = callsite.build();
        Trace.traceEnd(32L);
    }

    public void hide(SurfaceControl.Transaction transaction, String str) {
        if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, -1259022216, 0, (String) null, new Object[]{String.valueOf(str), String.valueOf(this.title)});
        }
        if (this.mSurfaceShown) {
            hideSurface(transaction);
        }
    }

    public final void hideSurface(SurfaceControl.Transaction transaction) {
        if (this.mSurfaceControl == null) {
            return;
        }
        setShown(false);
        try {
            transaction.hide(this.mSurfaceControl);
            WindowStateAnimator windowStateAnimator = this.mAnimator;
            if (windowStateAnimator.mIsWallpaper) {
                DisplayContent displayContent = windowStateAnimator.mWin.getDisplayContent();
                EventLog.writeEvent(33001, Integer.valueOf(displayContent.mDisplayId), 0, String.valueOf(displayContent.mWallpaperController.getWallpaperTarget()));
            }
        } catch (RuntimeException unused) {
            Slog.w(StartingSurfaceController.TAG, "Exception hiding surface in " + this);
        }
    }

    public void destroy(SurfaceControl.Transaction transaction) {
        if (ProtoLogCache.WM_SHOW_SURFACE_ALLOC_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_SURFACE_ALLOC, -861707633, 0, (String) null, new Object[]{String.valueOf(this), String.valueOf(Debug.getCallers(8))});
        }
        try {
            try {
                if (this.mSurfaceControl != null) {
                    WindowStateAnimator windowStateAnimator = this.mAnimator;
                    if (windowStateAnimator.mIsWallpaper) {
                        WindowState windowState = windowStateAnimator.mWin;
                        if (!windowState.mWindowRemovalAllowed && !windowState.mRemoveOnExit) {
                            Slog.e(StartingSurfaceController.TAG, "Unexpected removing wallpaper surface of " + this.mAnimator.mWin + " by " + Debug.getCallers(8));
                        }
                    }
                    transaction.remove(this.mSurfaceControl);
                }
            } catch (RuntimeException e) {
                Slog.w(StartingSurfaceController.TAG, "Error destroying surface in: " + this, e);
            }
        } finally {
            setShown(false);
            this.mSurfaceControl = null;
        }
    }

    public boolean prepareToShowInTransaction(SurfaceControl.Transaction transaction, float f) {
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl == null) {
            return false;
        }
        this.mSurfaceAlpha = f;
        transaction.setAlpha(surfaceControl, f);
        return true;
    }

    public void setOpaque(boolean z) {
        if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, 558823034, 3, (String) null, new Object[]{Boolean.valueOf(z), String.valueOf(this.title)});
        }
        if (this.mSurfaceControl == null) {
            return;
        }
        this.mService.openSurfaceTransaction();
        try {
            SurfaceControl.getGlobalTransaction().setOpaque(this.mSurfaceControl, z);
        } finally {
            this.mService.closeSurfaceTransaction("setOpaqueLocked");
        }
    }

    public void setSecure(boolean z) {
        if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, -1176488860, 3, (String) null, new Object[]{Boolean.valueOf(z), String.valueOf(this.title)});
        }
        if (this.mSurfaceControl == null) {
            return;
        }
        this.mService.openSurfaceTransaction();
        try {
            SurfaceControl.getGlobalTransaction().setSecure(this.mSurfaceControl, z);
            DisplayContent displayContent = this.mAnimator.mWin.mDisplayContent;
            if (displayContent != null) {
                displayContent.refreshImeSecureFlag(SurfaceControl.getGlobalTransaction());
            }
        } finally {
            this.mService.closeSurfaceTransaction("setSecure");
        }
    }

    public void setColorSpaceAgnostic(SurfaceControl.Transaction transaction, boolean z) {
        if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, 585096182, 3, (String) null, new Object[]{Boolean.valueOf(z), String.valueOf(this.title)});
        }
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl == null) {
            return;
        }
        transaction.setColorSpaceAgnostic(surfaceControl, z);
    }

    public void showRobustly(SurfaceControl.Transaction transaction) {
        if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, -1089874824, 0, (String) null, new Object[]{String.valueOf(this.title)});
        }
        if (this.mSurfaceShown) {
            return;
        }
        setShown(true);
        transaction.show(this.mSurfaceControl);
        WindowStateAnimator windowStateAnimator = this.mAnimator;
        if (windowStateAnimator.mIsWallpaper) {
            DisplayContent displayContent = windowStateAnimator.mWin.getDisplayContent();
            EventLog.writeEvent(33001, Integer.valueOf(displayContent.mDisplayId), 1, String.valueOf(displayContent.mWallpaperController.getWallpaperTarget()));
        }
    }

    public boolean clearWindowContentFrameStats() {
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl == null) {
            return false;
        }
        return surfaceControl.clearContentFrameStats();
    }

    public boolean getWindowContentFrameStats(WindowContentFrameStats windowContentFrameStats) {
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl == null) {
            return false;
        }
        return surfaceControl.getContentFrameStats(windowContentFrameStats);
    }

    public boolean hasSurface() {
        return this.mSurfaceControl != null;
    }

    public void getSurfaceControl(SurfaceControl surfaceControl) {
        surfaceControl.copyFrom(this.mSurfaceControl, "WindowSurfaceController.getSurfaceControl");
    }

    public boolean getShown() {
        return this.mSurfaceShown;
    }

    public void setShown(boolean z) {
        this.mSurfaceShown = z;
        this.mService.updateNonSystemOverlayWindowsVisibilityIfNeeded(this.mAnimator.mWin, z);
        this.mAnimator.mWin.onSurfaceShownChanged(z);
        Session session = this.mWindowSession;
        if (session != null) {
            session.onWindowSurfaceVisibilityChanged(this, this.mSurfaceShown, this.mWindowType);
        }
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1133871366145L, this.mSurfaceShown);
        protoOutputStream.write(1120986464258L, this.mSurfaceLayer);
        protoOutputStream.end(start);
    }

    public void dump(PrintWriter printWriter, String str, boolean z) {
        if (z) {
            printWriter.print(str);
            printWriter.print("mSurface=");
            printWriter.println(this.mSurfaceControl);
        }
        printWriter.print(str);
        printWriter.print("Surface: shown=");
        printWriter.print(this.mSurfaceShown);
        printWriter.print(" layer=");
        printWriter.print(this.mSurfaceLayer);
        printWriter.print(" alpha=");
        printWriter.print(this.mSurfaceAlpha);
        printWriter.print(" rect=(");
        printWriter.print(this.mSurfaceX);
        printWriter.print(",");
        printWriter.print(this.mSurfaceY);
        printWriter.print(") ");
        printWriter.print(" transform=(");
        printWriter.print(this.mLastDsdx);
        printWriter.print(", ");
        printWriter.print(this.mLastDtdx);
        printWriter.print(", ");
        printWriter.print(this.mLastDsdy);
        printWriter.print(", ");
        printWriter.print(this.mLastDtdy);
        printWriter.println(")");
    }

    public String toString() {
        return this.mSurfaceControl.toString();
    }
}
