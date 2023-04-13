package com.android.server.p014wm;

import android.content.Context;
import android.graphics.BLASTBufferQueue;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceControl;
/* renamed from: com.android.server.wm.EmulatorDisplayOverlay */
/* loaded from: classes2.dex */
public class EmulatorDisplayOverlay {
    public final BLASTBufferQueue mBlastBufferQueue;
    public boolean mDrawNeeded;
    public int mLastDH;
    public int mLastDW;
    public final Drawable mOverlay;
    public int mRotation;
    public Point mScreenSize;
    public final Surface mSurface;
    public final SurfaceControl mSurfaceControl;
    public boolean mVisible;

    public EmulatorDisplayOverlay(Context context, DisplayContent displayContent, int i, SurfaceControl.Transaction transaction) {
        Display display = displayContent.getDisplay();
        Point point = new Point();
        this.mScreenSize = point;
        display.getSize(point);
        SurfaceControl surfaceControl = null;
        try {
            surfaceControl = displayContent.makeOverlay().setName("EmulatorDisplayOverlay").setBLASTLayer().setFormat(-3).setCallsite("EmulatorDisplayOverlay").build();
            transaction.setLayer(surfaceControl, i);
            transaction.setPosition(surfaceControl, 0.0f, 0.0f);
            transaction.show(surfaceControl);
            InputMonitor.setTrustedOverlayInputInfo(surfaceControl, transaction, displayContent.getDisplayId(), "EmulatorDisplayOverlay");
        } catch (Surface.OutOfResourcesException unused) {
        }
        SurfaceControl surfaceControl2 = surfaceControl;
        this.mSurfaceControl = surfaceControl2;
        this.mDrawNeeded = true;
        this.mOverlay = context.getDrawable(17302233);
        Point point2 = this.mScreenSize;
        BLASTBufferQueue bLASTBufferQueue = new BLASTBufferQueue("EmulatorDisplayOverlay", surfaceControl2, point2.x, point2.y, 1);
        this.mBlastBufferQueue = bLASTBufferQueue;
        this.mSurface = bLASTBufferQueue.createSurface();
    }

    public final void drawIfNeeded(SurfaceControl.Transaction transaction) {
        if (this.mDrawNeeded && this.mVisible) {
            this.mDrawNeeded = false;
            Canvas canvas = null;
            try {
                canvas = this.mSurface.lockCanvas(null);
            } catch (Surface.OutOfResourcesException | IllegalArgumentException unused) {
            }
            if (canvas == null) {
                return;
            }
            canvas.drawColor(0, PorterDuff.Mode.SRC);
            transaction.setPosition(this.mSurfaceControl, 0.0f, 0.0f);
            Point point = this.mScreenSize;
            int max = Math.max(point.x, point.y);
            this.mOverlay.setBounds(0, 0, max, max);
            this.mOverlay.draw(canvas);
            this.mSurface.unlockCanvasAndPost(canvas);
        }
    }

    public void setVisibility(boolean z, SurfaceControl.Transaction transaction) {
        if (this.mSurfaceControl == null) {
            return;
        }
        this.mVisible = z;
        drawIfNeeded(transaction);
        if (z) {
            transaction.show(this.mSurfaceControl);
        } else {
            transaction.hide(this.mSurfaceControl);
        }
    }

    public void positionSurface(int i, int i2, int i3, SurfaceControl.Transaction transaction) {
        if (this.mLastDW == i && this.mLastDH == i2 && this.mRotation == i3) {
            return;
        }
        this.mLastDW = i;
        this.mLastDH = i2;
        this.mDrawNeeded = true;
        this.mRotation = i3;
        drawIfNeeded(transaction);
    }
}
