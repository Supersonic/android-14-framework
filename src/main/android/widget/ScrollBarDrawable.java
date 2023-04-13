package android.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.android.internal.widget.ScrollBarUtils;
/* loaded from: classes4.dex */
public class ScrollBarDrawable extends Drawable implements Drawable.Callback {
    private int mAlpha = 255;
    private boolean mAlwaysDrawHorizontalTrack;
    private boolean mAlwaysDrawVerticalTrack;
    private boolean mBoundsChanged;
    private ColorFilter mColorFilter;
    private int mExtent;
    private boolean mHasSetAlpha;
    private boolean mHasSetColorFilter;
    private Drawable mHorizontalThumb;
    private Drawable mHorizontalTrack;
    private boolean mMutated;
    private int mOffset;
    private int mRange;
    private boolean mRangeChanged;
    private boolean mVertical;
    private Drawable mVerticalThumb;
    private Drawable mVerticalTrack;

    public void setAlwaysDrawHorizontalTrack(boolean alwaysDrawTrack) {
        this.mAlwaysDrawHorizontalTrack = alwaysDrawTrack;
    }

    public void setAlwaysDrawVerticalTrack(boolean alwaysDrawTrack) {
        this.mAlwaysDrawVerticalTrack = alwaysDrawTrack;
    }

    public boolean getAlwaysDrawVerticalTrack() {
        return this.mAlwaysDrawVerticalTrack;
    }

    public boolean getAlwaysDrawHorizontalTrack() {
        return this.mAlwaysDrawHorizontalTrack;
    }

    public void setParameters(int range, int offset, int extent, boolean vertical) {
        if (this.mVertical != vertical) {
            this.mVertical = vertical;
            this.mBoundsChanged = true;
        }
        if (this.mRange != range || this.mOffset != offset || this.mExtent != extent) {
            this.mRange = range;
            this.mOffset = offset;
            this.mExtent = extent;
            this.mRangeChanged = true;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        boolean drawTrack;
        boolean drawThumb;
        boolean vertical = this.mVertical;
        int extent = this.mExtent;
        int range = this.mRange;
        if (extent > 0 && range > extent) {
            drawTrack = true;
            drawThumb = true;
        } else {
            boolean drawTrack2 = vertical ? this.mAlwaysDrawVerticalTrack : this.mAlwaysDrawHorizontalTrack;
            drawTrack = drawTrack2;
            drawThumb = false;
        }
        Rect r = getBounds();
        if (canvas.quickReject(r.left, r.top, r.right, r.bottom)) {
            return;
        }
        if (drawTrack) {
            drawTrack(canvas, r, vertical);
        }
        if (drawThumb) {
            int scrollBarLength = vertical ? r.height() : r.width();
            int thickness = vertical ? r.width() : r.height();
            int thumbLength = ScrollBarUtils.getThumbLength(scrollBarLength, thickness, extent, range);
            int thumbOffset = ScrollBarUtils.getThumbOffset(scrollBarLength, thumbLength, extent, range, this.mOffset);
            drawThumb(canvas, r, thumbOffset, thumbLength, vertical);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.mBoundsChanged = true;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        Drawable drawable4 = this.mVerticalTrack;
        return (drawable4 != null && drawable4.isStateful()) || ((drawable = this.mVerticalThumb) != null && drawable.isStateful()) || (((drawable2 = this.mHorizontalTrack) != null && drawable2.isStateful()) || (((drawable3 = this.mHorizontalThumb) != null && drawable3.isStateful()) || super.isStateful()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onStateChange(int[] state) {
        boolean changed = super.onStateChange(state);
        Drawable drawable = this.mVerticalTrack;
        if (drawable != null) {
            changed |= drawable.setState(state);
        }
        Drawable drawable2 = this.mVerticalThumb;
        if (drawable2 != null) {
            changed |= drawable2.setState(state);
        }
        Drawable drawable3 = this.mHorizontalTrack;
        if (drawable3 != null) {
            changed |= drawable3.setState(state);
        }
        Drawable drawable4 = this.mHorizontalThumb;
        if (drawable4 != null) {
            return changed | drawable4.setState(state);
        }
        return changed;
    }

    private void drawTrack(Canvas canvas, Rect bounds, boolean vertical) {
        Drawable track;
        if (vertical) {
            track = this.mVerticalTrack;
        } else {
            track = this.mHorizontalTrack;
        }
        if (track != null) {
            if (this.mBoundsChanged) {
                track.setBounds(bounds);
            }
            track.draw(canvas);
        }
    }

    private void drawThumb(Canvas canvas, Rect bounds, int offset, int length, boolean vertical) {
        boolean changed = this.mRangeChanged || this.mBoundsChanged;
        if (vertical) {
            if (this.mVerticalThumb != null) {
                Drawable thumb = this.mVerticalThumb;
                if (changed) {
                    thumb.setBounds(bounds.left, bounds.top + offset, bounds.right, bounds.top + offset + length);
                }
                thumb.draw(canvas);
            }
        } else if (this.mHorizontalThumb != null) {
            Drawable thumb2 = this.mHorizontalThumb;
            if (changed) {
                thumb2.setBounds(bounds.left + offset, bounds.top, bounds.left + offset + length, bounds.bottom);
            }
            thumb2.draw(canvas);
        }
    }

    public void setVerticalThumbDrawable(Drawable thumb) {
        Drawable drawable = this.mVerticalThumb;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        propagateCurrentState(thumb);
        this.mVerticalThumb = thumb;
    }

    public Drawable getVerticalTrackDrawable() {
        return this.mVerticalTrack;
    }

    public Drawable getVerticalThumbDrawable() {
        return this.mVerticalThumb;
    }

    public Drawable getHorizontalTrackDrawable() {
        return this.mHorizontalTrack;
    }

    public Drawable getHorizontalThumbDrawable() {
        return this.mHorizontalThumb;
    }

    public void setVerticalTrackDrawable(Drawable track) {
        Drawable drawable = this.mVerticalTrack;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        propagateCurrentState(track);
        this.mVerticalTrack = track;
    }

    public void setHorizontalThumbDrawable(Drawable thumb) {
        Drawable drawable = this.mHorizontalThumb;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        propagateCurrentState(thumb);
        this.mHorizontalThumb = thumb;
    }

    public void setHorizontalTrackDrawable(Drawable track) {
        Drawable drawable = this.mHorizontalTrack;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        propagateCurrentState(track);
        this.mHorizontalTrack = track;
    }

    private void propagateCurrentState(Drawable d) {
        if (d != null) {
            if (this.mMutated) {
                d.mutate();
            }
            d.setState(getState());
            d.setCallback(this);
            if (this.mHasSetAlpha) {
                d.setAlpha(this.mAlpha);
            }
            if (this.mHasSetColorFilter) {
                d.setColorFilter(this.mColorFilter);
            }
        }
    }

    public int getSize(boolean vertical) {
        if (vertical) {
            Drawable drawable = this.mVerticalTrack;
            if (drawable != null) {
                return drawable.getIntrinsicWidth();
            }
            Drawable drawable2 = this.mVerticalThumb;
            if (drawable2 != null) {
                return drawable2.getIntrinsicWidth();
            }
            return 0;
        }
        Drawable drawable3 = this.mHorizontalTrack;
        if (drawable3 != null) {
            return drawable3.getIntrinsicHeight();
        }
        Drawable drawable4 = this.mHorizontalThumb;
        if (drawable4 != null) {
            return drawable4.getIntrinsicHeight();
        }
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public ScrollBarDrawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            Drawable drawable = this.mVerticalTrack;
            if (drawable != null) {
                drawable.mutate();
            }
            Drawable drawable2 = this.mVerticalThumb;
            if (drawable2 != null) {
                drawable2.mutate();
            }
            Drawable drawable3 = this.mHorizontalTrack;
            if (drawable3 != null) {
                drawable3.mutate();
            }
            Drawable drawable4 = this.mHorizontalThumb;
            if (drawable4 != null) {
                drawable4.mutate();
            }
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
        this.mHasSetAlpha = true;
        Drawable drawable = this.mVerticalTrack;
        if (drawable != null) {
            drawable.setAlpha(alpha);
        }
        Drawable drawable2 = this.mVerticalThumb;
        if (drawable2 != null) {
            drawable2.setAlpha(alpha);
        }
        Drawable drawable3 = this.mHorizontalTrack;
        if (drawable3 != null) {
            drawable3.setAlpha(alpha);
        }
        Drawable drawable4 = this.mHorizontalThumb;
        if (drawable4 != null) {
            drawable4.setAlpha(alpha);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mColorFilter = colorFilter;
        this.mHasSetColorFilter = true;
        Drawable drawable = this.mVerticalTrack;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        Drawable drawable2 = this.mVerticalThumb;
        if (drawable2 != null) {
            drawable2.setColorFilter(colorFilter);
        }
        Drawable drawable3 = this.mHorizontalTrack;
        if (drawable3 != null) {
            drawable3.setColorFilter(colorFilter);
        }
        Drawable drawable4 = this.mHorizontalThumb;
        if (drawable4 != null) {
            drawable4.setColorFilter(colorFilter);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }

    public String toString() {
        return "ScrollBarDrawable: range=" + this.mRange + " offset=" + this.mOffset + " extent=" + this.mExtent + (this.mVertical ? " V" : " H");
    }
}
