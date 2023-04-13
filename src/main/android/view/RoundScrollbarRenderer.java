package android.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.android.internal.C4057R;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class RoundScrollbarRenderer {
    private static final int DEFAULT_THUMB_COLOR = -1512723;
    private static final int DEFAULT_TRACK_COLOR = 1291845631;
    private static final int MAX_SCROLLBAR_ANGLE_SWIPE = 16;
    private static final int MIN_SCROLLBAR_ANGLE_SWIPE = 6;
    private static final int SCROLLBAR_ANGLE_RANGE = 90;
    private static final float WIDTH_PERCENTAGE = 0.02f;
    private final int mMaskThickness;
    private final View mParent;
    private final RectF mRect;
    private final Paint mThumbPaint;
    private final Paint mTrackPaint;

    public RoundScrollbarRenderer(View parent) {
        Paint paint = new Paint();
        this.mThumbPaint = paint;
        Paint paint2 = new Paint();
        this.mTrackPaint = paint2;
        this.mRect = new RectF();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint2.setAntiAlias(true);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        paint2.setStyle(Paint.Style.STROKE);
        this.mParent = parent;
        this.mMaskThickness = parent.getContext().getResources().getDimensionPixelSize(C4057R.dimen.circular_display_mask_thickness);
    }

    public void drawRoundScrollbars(Canvas canvas, float alpha, Rect bounds) {
        if (alpha == 0.0f) {
            return;
        }
        float maxScroll = this.mParent.computeVerticalScrollRange();
        float scrollExtent = this.mParent.computeVerticalScrollExtent();
        if (scrollExtent <= 0.0f || maxScroll <= scrollExtent) {
            return;
        }
        float currentScroll = Math.max(0, this.mParent.computeVerticalScrollOffset());
        float linearThumbLength = this.mParent.computeVerticalScrollExtent();
        float thumbWidth = this.mParent.getWidth() * WIDTH_PERCENTAGE;
        this.mThumbPaint.setStrokeWidth(thumbWidth);
        this.mTrackPaint.setStrokeWidth(thumbWidth);
        setThumbColor(applyAlpha(DEFAULT_THUMB_COLOR, alpha));
        setTrackColor(applyAlpha(DEFAULT_TRACK_COLOR, alpha));
        float sweepAngle = clamp((linearThumbLength / maxScroll) * 90.0f, 6.0f, 16.0f);
        float startAngle = (((90.0f - sweepAngle) * currentScroll) / (maxScroll - linearThumbLength)) - 45.0f;
        float startAngle2 = clamp(startAngle, -45.0f, 45.0f - sweepAngle);
        float inset = (thumbWidth / 2.0f) + this.mMaskThickness;
        this.mRect.set(bounds.left + inset, bounds.top + inset, bounds.right - inset, bounds.bottom - inset);
        canvas.drawArc(this.mRect, -45.0f, 90.0f, false, this.mTrackPaint);
        canvas.drawArc(this.mRect, startAngle2, sweepAngle, false, this.mThumbPaint);
    }

    private static float clamp(float val, float min, float max) {
        if (val < min) {
            return min;
        }
        if (val > max) {
            return max;
        }
        return val;
    }

    private static int applyAlpha(int color, float alpha) {
        int alphaByte = (int) (Color.alpha(color) * alpha);
        return Color.argb(alphaByte, Color.red(color), Color.green(color), Color.blue(color));
    }

    private void setThumbColor(int thumbColor) {
        if (this.mThumbPaint.getColor() != thumbColor) {
            this.mThumbPaint.setColor(thumbColor);
        }
    }

    private void setTrackColor(int trackColor) {
        if (this.mTrackPaint.getColor() != trackColor) {
            this.mTrackPaint.setColor(trackColor);
        }
    }
}
