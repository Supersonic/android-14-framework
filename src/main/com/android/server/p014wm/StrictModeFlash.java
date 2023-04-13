package com.android.server.p014wm;

import android.graphics.BLASTBufferQueue;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Surface;
import android.view.SurfaceControl;
/* renamed from: com.android.server.wm.StrictModeFlash */
/* loaded from: classes2.dex */
public class StrictModeFlash {
    public final BLASTBufferQueue mBlastBufferQueue;
    public boolean mDrawNeeded;
    public int mLastDH;
    public int mLastDW;
    public final Surface mSurface;
    public final SurfaceControl mSurfaceControl;
    public final int mThickness = 20;

    public StrictModeFlash(DisplayContent displayContent, SurfaceControl.Transaction transaction) {
        SurfaceControl surfaceControl = null;
        try {
            surfaceControl = displayContent.makeOverlay().setName("StrictModeFlash").setBLASTLayer().setFormat(-3).setCallsite("StrictModeFlash").build();
            transaction.setLayer(surfaceControl, 1010000);
            transaction.setPosition(surfaceControl, 0.0f, 0.0f);
            transaction.show(surfaceControl);
            InputMonitor.setTrustedOverlayInputInfo(surfaceControl, transaction, displayContent.getDisplayId(), "StrictModeFlash");
        } catch (Surface.OutOfResourcesException unused) {
        }
        SurfaceControl surfaceControl2 = surfaceControl;
        this.mSurfaceControl = surfaceControl2;
        this.mDrawNeeded = true;
        BLASTBufferQueue bLASTBufferQueue = new BLASTBufferQueue("StrictModeFlash", surfaceControl2, 1, 1, 1);
        this.mBlastBufferQueue = bLASTBufferQueue;
        this.mSurface = bLASTBufferQueue.createSurface();
    }

    public final void drawIfNeeded() {
        if (this.mDrawNeeded) {
            this.mDrawNeeded = false;
            int i = this.mLastDW;
            int i2 = this.mLastDH;
            this.mBlastBufferQueue.update(this.mSurfaceControl, i, i2, 1);
            Canvas canvas = null;
            try {
                canvas = this.mSurface.lockCanvas(null);
            } catch (Surface.OutOfResourcesException | IllegalArgumentException unused) {
            }
            if (canvas == null) {
                return;
            }
            canvas.save();
            canvas.clipRect(new Rect(0, 0, i, 20));
            canvas.drawColor(-65536);
            canvas.restore();
            canvas.save();
            canvas.clipRect(new Rect(0, 0, 20, i2));
            canvas.drawColor(-65536);
            canvas.restore();
            canvas.save();
            canvas.clipRect(new Rect(i - 20, 0, i, i2));
            canvas.drawColor(-65536);
            canvas.restore();
            canvas.save();
            canvas.clipRect(new Rect(0, i2 - 20, i, i2));
            canvas.drawColor(-65536);
            canvas.restore();
            this.mSurface.unlockCanvasAndPost(canvas);
        }
    }

    public void setVisibility(boolean z, SurfaceControl.Transaction transaction) {
        if (this.mSurfaceControl == null) {
            return;
        }
        drawIfNeeded();
        if (z) {
            transaction.show(this.mSurfaceControl);
        } else {
            transaction.hide(this.mSurfaceControl);
        }
    }

    public void positionSurface(int i, int i2, SurfaceControl.Transaction transaction) {
        if (this.mLastDW == i && this.mLastDH == i2) {
            return;
        }
        this.mLastDW = i;
        this.mLastDH = i2;
        transaction.setBufferSize(this.mSurfaceControl, i, i2);
        this.mDrawNeeded = true;
    }
}
