package android.text.style;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spanned;
/* loaded from: classes3.dex */
public class IconMarginSpan implements LeadingMarginSpan, LineHeightSpan {
    private final Bitmap mBitmap;
    private final int mPad;

    public IconMarginSpan(Bitmap bitmap) {
        this(bitmap, 0);
    }

    public IconMarginSpan(Bitmap bitmap, int pad) {
        this.mBitmap = bitmap;
        this.mPad = pad;
    }

    @Override // android.text.style.LeadingMarginSpan
    public int getLeadingMargin(boolean first) {
        return this.mBitmap.getWidth() + this.mPad;
    }

    @Override // android.text.style.LeadingMarginSpan
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        int x2;
        int st = ((Spanned) text).getSpanStart(this);
        int itop = layout.getLineTop(layout.getLineForOffset(st));
        if (dir >= 0) {
            x2 = x;
        } else {
            x2 = x - this.mBitmap.getWidth();
        }
        c.drawBitmap(this.mBitmap, x2, itop, p);
    }

    @Override // android.text.style.LineHeightSpan
    public void chooseHeight(CharSequence text, int start, int end, int istartv, int v, Paint.FontMetricsInt fm) {
        if (end == ((Spanned) text).getSpanEnd(this)) {
            int ht = this.mBitmap.getHeight();
            int need = ht - (((fm.descent + v) - fm.ascent) - istartv);
            if (need > 0) {
                fm.descent += need;
            }
            int need2 = ht - (((fm.bottom + v) - fm.top) - istartv);
            if (need2 > 0) {
                fm.bottom += need2;
            }
        }
    }

    public String toString() {
        return "IconMarginSpan{bitmap=" + getBitmap() + ", padding=" + getPadding() + '}';
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public int getPadding() {
        return this.mPad;
    }
}
