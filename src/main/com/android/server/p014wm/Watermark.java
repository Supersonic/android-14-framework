package com.android.server.p014wm;

import android.graphics.BLASTBufferQueue;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceControl;
/* renamed from: com.android.server.wm.Watermark */
/* loaded from: classes2.dex */
public class Watermark {
    public final BLASTBufferQueue mBlastBufferQueue;
    public final int mDeltaX;
    public final int mDeltaY;
    public boolean mDrawNeeded;
    public int mLastDH;
    public int mLastDW;
    public final Surface mSurface;
    public final SurfaceControl mSurfaceControl;
    public final String mText;
    public final int mTextHeight;
    public final Paint mTextPaint;
    public final int mTextWidth;

    public Watermark(DisplayContent displayContent, DisplayMetrics displayMetrics, String[] strArr, SurfaceControl.Transaction transaction) {
        int i;
        int i2;
        int i3;
        int i4;
        StringBuilder sb = new StringBuilder(32);
        int length = strArr[0].length() & (-2);
        for (int i5 = 0; i5 < length; i5 += 2) {
            char charAt = strArr[0].charAt(i5);
            char charAt2 = strArr[0].charAt(i5 + 1);
            if (charAt >= 'a' && charAt <= 'f') {
                i2 = charAt - 'a';
            } else if (charAt < 'A' || charAt > 'F') {
                i = charAt - '0';
                if (charAt2 < 'a' && charAt2 <= 'f') {
                    i4 = charAt2 - 'a';
                } else if (charAt2 >= 'A' || charAt2 > 'F') {
                    i3 = charAt2 - '0';
                    sb.append((char) (255 - ((i * 16) + i3)));
                } else {
                    i4 = charAt2 - 'A';
                }
                i3 = i4 + 10;
                sb.append((char) (255 - ((i * 16) + i3)));
            } else {
                i2 = charAt - 'A';
            }
            i = i2 + 10;
            if (charAt2 < 'a') {
            }
            if (charAt2 >= 'A') {
            }
            i3 = charAt2 - '0';
            sb.append((char) (255 - ((i * 16) + i3)));
        }
        String sb2 = sb.toString();
        this.mText = sb2;
        int propertyInt = WindowManagerService.getPropertyInt(strArr, 1, 1, 20, displayMetrics);
        Paint paint = new Paint(1);
        this.mTextPaint = paint;
        paint.setTextSize(propertyInt);
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, 1));
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int measureText = (int) paint.measureText(sb2);
        this.mTextWidth = measureText;
        int i6 = fontMetricsInt.descent - fontMetricsInt.ascent;
        this.mTextHeight = i6;
        this.mDeltaX = WindowManagerService.getPropertyInt(strArr, 2, 0, measureText * 2, displayMetrics);
        this.mDeltaY = WindowManagerService.getPropertyInt(strArr, 3, 0, i6 * 3, displayMetrics);
        int propertyInt2 = WindowManagerService.getPropertyInt(strArr, 4, 0, -1342177280, displayMetrics);
        int propertyInt3 = WindowManagerService.getPropertyInt(strArr, 5, 0, 1627389951, displayMetrics);
        int propertyInt4 = WindowManagerService.getPropertyInt(strArr, 6, 0, 7, displayMetrics);
        int propertyInt5 = WindowManagerService.getPropertyInt(strArr, 8, 0, 0, displayMetrics);
        int propertyInt6 = WindowManagerService.getPropertyInt(strArr, 9, 0, 0, displayMetrics);
        paint.setColor(propertyInt3);
        paint.setShadowLayer(propertyInt4, propertyInt5, propertyInt6, propertyInt2);
        SurfaceControl surfaceControl = null;
        try {
            surfaceControl = displayContent.makeOverlay().setName("WatermarkSurface").setBLASTLayer().setFormat(-3).setCallsite("WatermarkSurface").build();
            transaction.setLayer(surfaceControl, 1000000).setPosition(surfaceControl, 0.0f, 0.0f).show(surfaceControl);
            InputMonitor.setTrustedOverlayInputInfo(surfaceControl, transaction, displayContent.getDisplayId(), "WatermarkSurface");
        } catch (Surface.OutOfResourcesException unused) {
        }
        SurfaceControl surfaceControl2 = surfaceControl;
        this.mSurfaceControl = surfaceControl2;
        BLASTBufferQueue bLASTBufferQueue = new BLASTBufferQueue("WatermarkSurface", surfaceControl2, 1, 1, 1);
        this.mBlastBufferQueue = bLASTBufferQueue;
        this.mSurface = bLASTBufferQueue.createSurface();
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

    public void drawIfNeeded() {
        if (this.mDrawNeeded) {
            int i = this.mLastDW;
            int i2 = this.mLastDH;
            this.mDrawNeeded = false;
            this.mBlastBufferQueue.update(this.mSurfaceControl, i, i2, 1);
            Canvas canvas = null;
            try {
                canvas = this.mSurface.lockCanvas(null);
            } catch (Surface.OutOfResourcesException | IllegalArgumentException unused) {
            }
            if (canvas != null) {
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                int i3 = this.mDeltaX;
                int i4 = this.mDeltaY;
                int i5 = this.mTextWidth;
                int i6 = (i + i5) - (((i + i5) / i3) * i3);
                int i7 = i3 / 4;
                if (i6 < i7 || i6 > i3 - i7) {
                    i3 += i3 / 3;
                }
                int i8 = -this.mTextHeight;
                int i9 = -i5;
                while (i8 < this.mTextHeight + i2) {
                    canvas.drawText(this.mText, i9, i8, this.mTextPaint);
                    i9 += i3;
                    if (i9 >= i) {
                        i9 -= this.mTextWidth + i;
                        i8 += i4;
                    }
                }
                this.mSurface.unlockCanvasAndPost(canvas);
            }
        }
    }
}
