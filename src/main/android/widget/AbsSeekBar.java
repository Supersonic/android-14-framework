package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.p008os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import com.android.internal.C4057R;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes4.dex */
public abstract class AbsSeekBar extends ProgressBar {
    private static final int NO_ALPHA = 255;
    private float mDisabledAlpha;
    private final List<Rect> mGestureExclusionRects;
    private boolean mHasThumbBlendMode;
    private boolean mHasThumbTint;
    private boolean mHasTickMarkBlendMode;
    private boolean mHasTickMarkTint;
    private boolean mIsDragging;
    boolean mIsUserSeekable;
    private int mKeyProgressIncrement;
    private int mScaledTouchSlop;
    private boolean mSplitTrack;
    private final Rect mTempRect;
    private Drawable mThumb;
    private BlendMode mThumbBlendMode;
    private int mThumbExclusionMaxSize;
    private int mThumbOffset;
    private final Rect mThumbRect;
    private ColorStateList mThumbTintList;
    private Drawable mTickMark;
    private BlendMode mTickMarkBlendMode;
    private ColorStateList mTickMarkTintList;
    private float mTouchDownX;
    float mTouchProgressOffset;
    private float mTouchThumbOffset;
    private List<Rect> mUserGestureExclusionRects;

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<AbsSeekBar> {
        private boolean mPropertiesMapped = false;
        private int mThumbTintId;
        private int mThumbTintModeId;
        private int mTickMarkTintBlendModeId;
        private int mTickMarkTintId;
        private int mTickMarkTintModeId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mThumbTintId = propertyMapper.mapObject("thumbTint", 16843889);
            this.mThumbTintModeId = propertyMapper.mapObject("thumbTintMode", 16843890);
            this.mTickMarkTintId = propertyMapper.mapObject("tickMarkTint", 16844043);
            this.mTickMarkTintBlendModeId = propertyMapper.mapObject("tickMarkTintBlendMode", 7);
            this.mTickMarkTintModeId = propertyMapper.mapObject("tickMarkTintMode", 16844044);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(AbsSeekBar node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readObject(this.mThumbTintId, node.getThumbTintList());
            propertyReader.readObject(this.mThumbTintModeId, node.getThumbTintMode());
            propertyReader.readObject(this.mTickMarkTintId, node.getTickMarkTintList());
            propertyReader.readObject(this.mTickMarkTintBlendModeId, node.getTickMarkTintBlendMode());
            propertyReader.readObject(this.mTickMarkTintModeId, node.getTickMarkTintMode());
        }
    }

    public AbsSeekBar(Context context) {
        super(context);
        this.mTempRect = new Rect();
        this.mThumbTintList = null;
        this.mThumbBlendMode = null;
        this.mHasThumbTint = false;
        this.mHasThumbBlendMode = false;
        this.mTickMarkTintList = null;
        this.mTickMarkBlendMode = null;
        this.mHasTickMarkTint = false;
        this.mHasTickMarkBlendMode = false;
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
        this.mTouchThumbOffset = 0.0f;
        this.mUserGestureExclusionRects = Collections.emptyList();
        this.mGestureExclusionRects = new ArrayList();
        this.mThumbRect = new Rect();
    }

    public AbsSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTempRect = new Rect();
        this.mThumbTintList = null;
        this.mThumbBlendMode = null;
        this.mHasThumbTint = false;
        this.mHasThumbBlendMode = false;
        this.mTickMarkTintList = null;
        this.mTickMarkBlendMode = null;
        this.mHasTickMarkTint = false;
        this.mHasTickMarkBlendMode = false;
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
        this.mTouchThumbOffset = 0.0f;
        this.mUserGestureExclusionRects = Collections.emptyList();
        this.mGestureExclusionRects = new ArrayList();
        this.mThumbRect = new Rect();
    }

    public AbsSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AbsSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTempRect = new Rect();
        this.mThumbTintList = null;
        this.mThumbBlendMode = null;
        this.mHasThumbTint = false;
        this.mHasThumbBlendMode = false;
        this.mTickMarkTintList = null;
        this.mTickMarkBlendMode = null;
        this.mHasTickMarkTint = false;
        this.mHasTickMarkBlendMode = false;
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
        this.mTouchThumbOffset = 0.0f;
        this.mUserGestureExclusionRects = Collections.emptyList();
        this.mGestureExclusionRects = new ArrayList();
        this.mThumbRect = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.SeekBar, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.SeekBar, attrs, a, defStyleAttr, defStyleRes);
        Drawable thumb = a.getDrawable(0);
        setThumb(thumb);
        if (a.hasValue(4)) {
            this.mThumbBlendMode = Drawable.parseBlendMode(a.getInt(4, -1), this.mThumbBlendMode);
            this.mHasThumbBlendMode = true;
        }
        if (a.hasValue(3)) {
            this.mThumbTintList = a.getColorStateList(3);
            this.mHasThumbTint = true;
        }
        Drawable tickMark = a.getDrawable(5);
        setTickMark(tickMark);
        if (a.hasValue(7)) {
            this.mTickMarkBlendMode = Drawable.parseBlendMode(a.getInt(7, -1), this.mTickMarkBlendMode);
            this.mHasTickMarkBlendMode = true;
        }
        if (a.hasValue(6)) {
            this.mTickMarkTintList = a.getColorStateList(6);
            this.mHasTickMarkTint = true;
        }
        this.mSplitTrack = a.getBoolean(2, false);
        int thumbOffset = a.getDimensionPixelOffset(1, getThumbOffset());
        setThumbOffset(thumbOffset);
        boolean useDisabledAlpha = a.getBoolean(8, true);
        a.recycle();
        if (useDisabledAlpha) {
            TypedArray ta = context.obtainStyledAttributes(attrs, C4057R.styleable.Theme, 0, 0);
            this.mDisabledAlpha = ta.getFloat(3, 0.5f);
            ta.recycle();
        } else {
            this.mDisabledAlpha = 1.0f;
        }
        applyThumbTint();
        applyTickMarkTint();
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mThumbExclusionMaxSize = getResources().getDimensionPixelSize(C4057R.dimen.seekbar_thumb_exclusion_max_size);
    }

    public void setThumb(Drawable thumb) {
        boolean needUpdate;
        Drawable drawable = this.mThumb;
        if (drawable != null && thumb != drawable) {
            drawable.setCallback(null);
            needUpdate = true;
        } else {
            needUpdate = false;
        }
        if (thumb != null) {
            thumb.setCallback(this);
            if (canResolveLayoutDirection()) {
                thumb.setLayoutDirection(getLayoutDirection());
            }
            this.mThumbOffset = thumb.getIntrinsicWidth() / 2;
            if (needUpdate && (thumb.getIntrinsicWidth() != this.mThumb.getIntrinsicWidth() || thumb.getIntrinsicHeight() != this.mThumb.getIntrinsicHeight())) {
                requestLayout();
            }
        }
        this.mThumb = thumb;
        applyThumbTint();
        invalidate();
        if (needUpdate) {
            updateThumbAndTrackPos(getWidth(), getHeight());
            if (thumb != null && thumb.isStateful()) {
                int[] state = getDrawableState();
                thumb.setState(state);
            }
        }
    }

    public Drawable getThumb() {
        return this.mThumb;
    }

    public void setThumbTintList(ColorStateList tint) {
        this.mThumbTintList = tint;
        this.mHasThumbTint = true;
        applyThumbTint();
    }

    public ColorStateList getThumbTintList() {
        return this.mThumbTintList;
    }

    public void setThumbTintMode(PorterDuff.Mode tintMode) {
        setThumbTintBlendMode(tintMode != null ? BlendMode.fromValue(tintMode.nativeInt) : null);
    }

    public void setThumbTintBlendMode(BlendMode blendMode) {
        this.mThumbBlendMode = blendMode;
        this.mHasThumbBlendMode = true;
        applyThumbTint();
    }

    public PorterDuff.Mode getThumbTintMode() {
        BlendMode blendMode = this.mThumbBlendMode;
        if (blendMode != null) {
            return BlendMode.blendModeToPorterDuffMode(blendMode);
        }
        return null;
    }

    public BlendMode getThumbTintBlendMode() {
        return this.mThumbBlendMode;
    }

    private void applyThumbTint() {
        Drawable drawable = this.mThumb;
        if (drawable != null) {
            if (this.mHasThumbTint || this.mHasThumbBlendMode) {
                Drawable mutate = drawable.mutate();
                this.mThumb = mutate;
                if (this.mHasThumbTint) {
                    mutate.setTintList(this.mThumbTintList);
                }
                if (this.mHasThumbBlendMode) {
                    this.mThumb.setTintBlendMode(this.mThumbBlendMode);
                }
                if (this.mThumb.isStateful()) {
                    this.mThumb.setState(getDrawableState());
                }
            }
        }
    }

    public int getThumbOffset() {
        return this.mThumbOffset;
    }

    public void setThumbOffset(int thumbOffset) {
        this.mThumbOffset = thumbOffset;
        invalidate();
    }

    public void setSplitTrack(boolean splitTrack) {
        this.mSplitTrack = splitTrack;
        invalidate();
    }

    public boolean getSplitTrack() {
        return this.mSplitTrack;
    }

    public void setTickMark(Drawable tickMark) {
        Drawable drawable = this.mTickMark;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        this.mTickMark = tickMark;
        if (tickMark != null) {
            tickMark.setCallback(this);
            tickMark.setLayoutDirection(getLayoutDirection());
            if (tickMark.isStateful()) {
                tickMark.setState(getDrawableState());
            }
            applyTickMarkTint();
        }
        invalidate();
    }

    public Drawable getTickMark() {
        return this.mTickMark;
    }

    public void setTickMarkTintList(ColorStateList tint) {
        this.mTickMarkTintList = tint;
        this.mHasTickMarkTint = true;
        applyTickMarkTint();
    }

    public ColorStateList getTickMarkTintList() {
        return this.mTickMarkTintList;
    }

    public void setTickMarkTintMode(PorterDuff.Mode tintMode) {
        setTickMarkTintBlendMode(tintMode != null ? BlendMode.fromValue(tintMode.nativeInt) : null);
    }

    public void setTickMarkTintBlendMode(BlendMode blendMode) {
        this.mTickMarkBlendMode = blendMode;
        this.mHasTickMarkBlendMode = true;
        applyTickMarkTint();
    }

    public PorterDuff.Mode getTickMarkTintMode() {
        BlendMode blendMode = this.mTickMarkBlendMode;
        if (blendMode != null) {
            return BlendMode.blendModeToPorterDuffMode(blendMode);
        }
        return null;
    }

    public BlendMode getTickMarkTintBlendMode() {
        return this.mTickMarkBlendMode;
    }

    private void applyTickMarkTint() {
        Drawable drawable = this.mTickMark;
        if (drawable != null) {
            if (this.mHasTickMarkTint || this.mHasTickMarkBlendMode) {
                Drawable mutate = drawable.mutate();
                this.mTickMark = mutate;
                if (this.mHasTickMarkTint) {
                    mutate.setTintList(this.mTickMarkTintList);
                }
                if (this.mHasTickMarkBlendMode) {
                    this.mTickMark.setTintBlendMode(this.mTickMarkBlendMode);
                }
                if (this.mTickMark.isStateful()) {
                    this.mTickMark.setState(getDrawableState());
                }
            }
        }
    }

    public void setKeyProgressIncrement(int increment) {
        this.mKeyProgressIncrement = increment < 0 ? -increment : increment;
    }

    public int getKeyProgressIncrement() {
        return this.mKeyProgressIncrement;
    }

    @Override // android.widget.ProgressBar
    public synchronized void setMin(int min) {
        super.setMin(min);
        int range = getMax() - getMin();
        int i = this.mKeyProgressIncrement;
        if (i == 0 || range / i > 20) {
            setKeyProgressIncrement(Math.max(1, Math.round(range / 20.0f)));
        }
    }

    @Override // android.widget.ProgressBar
    public synchronized void setMax(int max) {
        super.setMax(max);
        int range = getMax() - getMin();
        int i = this.mKeyProgressIncrement;
        if (i == 0 || range / i > 20) {
            setKeyProgressIncrement(Math.max(1, Math.round(range / 20.0f)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ProgressBar, android.view.View
    public boolean verifyDrawable(Drawable who) {
        return who == this.mThumb || who == this.mTickMark || super.verifyDrawable(who);
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mThumb;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
        Drawable drawable2 = this.mTickMark;
        if (drawable2 != null) {
            drawable2.jumpToCurrentState();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ProgressBar, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null && this.mDisabledAlpha < 1.0f) {
            progressDrawable.setAlpha(isEnabled() ? 255 : (int) (this.mDisabledAlpha * 255.0f));
        }
        Drawable thumb = this.mThumb;
        if (thumb != null && thumb.isStateful() && thumb.setState(getDrawableState())) {
            invalidateDrawable(thumb);
        }
        Drawable tickMark = this.mTickMark;
        if (tickMark != null && tickMark.isStateful() && tickMark.setState(getDrawableState())) {
            invalidateDrawable(tickMark);
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        Drawable drawable = this.mThumb;
        if (drawable != null) {
            drawable.setHotspot(x, y);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.ProgressBar
    public void onVisualProgressChanged(int id, float scale) {
        Drawable thumb;
        super.onVisualProgressChanged(id, scale);
        if (id == 16908301 && (thumb = this.mThumb) != null) {
            setThumbPos(getWidth(), thumb, scale, Integer.MIN_VALUE);
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ProgressBar, android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateThumbAndTrackPos(w, h);
    }

    private void updateThumbAndTrackPos(int w, int h) {
        int trackOffset;
        int offsetHeight;
        int paddedHeight = (h - this.mPaddingTop) - this.mPaddingBottom;
        Drawable track = getCurrentDrawable();
        Drawable thumb = this.mThumb;
        int trackHeight = Math.min(this.mMaxHeight, paddedHeight);
        int thumbHeight = thumb == null ? 0 : thumb.getIntrinsicHeight();
        if (thumbHeight > trackHeight) {
            offsetHeight = (paddedHeight - thumbHeight) / 2;
            trackOffset = ((thumbHeight - trackHeight) / 2) + offsetHeight;
        } else {
            int thumbOffset = paddedHeight - trackHeight;
            int offsetHeight2 = thumbOffset / 2;
            trackOffset = offsetHeight2;
            offsetHeight = ((trackHeight - thumbHeight) / 2) + offsetHeight2;
        }
        if (track != null) {
            int trackWidth = (w - this.mPaddingRight) - this.mPaddingLeft;
            track.setBounds(0, trackOffset, trackWidth, trackOffset + trackHeight);
        }
        if (thumb != null) {
            setThumbPos(w, thumb, getScale(), offsetHeight);
        }
    }

    private float getScale() {
        int min = getMin();
        int max = getMax();
        int range = max - min;
        if (range > 0) {
            return (getProgress() - min) / range;
        }
        return 0.0f;
    }

    private void setThumbPos(int w, Drawable thumb, float scale, int offset) {
        int top;
        int bottom;
        int available = (w - this.mPaddingLeft) - this.mPaddingRight;
        int thumbWidth = thumb.getIntrinsicWidth();
        int thumbHeight = thumb.getIntrinsicHeight();
        int available2 = (available - thumbWidth) + (this.mThumbOffset * 2);
        int thumbPos = (int) ((available2 * scale) + 0.5f);
        if (offset == Integer.MIN_VALUE) {
            Rect oldBounds = thumb.getBounds();
            top = oldBounds.top;
            bottom = oldBounds.bottom;
        } else {
            top = offset;
            bottom = offset + thumbHeight;
        }
        int left = (isLayoutRtl() && this.mMirrorForRtl) ? available2 - thumbPos : thumbPos;
        int right = left + thumbWidth;
        Drawable background = getBackground();
        if (background != null) {
            int offsetX = this.mPaddingLeft - this.mThumbOffset;
            int offsetY = this.mPaddingTop;
            background.setHotspotBounds(left + offsetX, top + offsetY, right + offsetX, bottom + offsetY);
        }
        thumb.setBounds(left, top, right, bottom);
        updateGestureExclusionRects();
    }

    @Override // android.view.View
    public void setSystemGestureExclusionRects(List<Rect> rects) {
        Preconditions.checkNotNull(rects, "rects must not be null");
        this.mUserGestureExclusionRects = rects;
        updateGestureExclusionRects();
    }

    private void updateGestureExclusionRects() {
        Drawable thumb = this.mThumb;
        if (thumb == null) {
            super.setSystemGestureExclusionRects(this.mUserGestureExclusionRects);
            return;
        }
        this.mGestureExclusionRects.clear();
        thumb.copyBounds(this.mThumbRect);
        this.mThumbRect.offset(this.mPaddingLeft - this.mThumbOffset, this.mPaddingTop);
        growRectTo(this.mThumbRect, Math.min(getHeight(), this.mThumbExclusionMaxSize));
        this.mGestureExclusionRects.add(this.mThumbRect);
        this.mGestureExclusionRects.addAll(this.mUserGestureExclusionRects);
        super.setSystemGestureExclusionRects(this.mGestureExclusionRects);
    }

    public void growRectTo(Rect r, int minimumSize) {
        int dy = minimumSize - r.height();
        if (dy > 0) {
            r.top -= (dy + 1) / 2;
            r.bottom += dy / 2;
        }
        int dx = minimumSize - r.width();
        if (dx > 0) {
            r.left -= (dx + 1) / 2;
            r.right += dx / 2;
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onResolveDrawables(int layoutDirection) {
        super.onResolveDrawables(layoutDirection);
        Drawable drawable = this.mThumb;
        if (drawable != null) {
            drawable.setLayoutDirection(layoutDirection);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ProgressBar, android.view.View
    public synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawThumb(canvas);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.ProgressBar
    public void drawTrack(Canvas canvas) {
        Drawable thumbDrawable = this.mThumb;
        if (thumbDrawable != null && this.mSplitTrack) {
            Insets insets = thumbDrawable.getOpticalInsets();
            Rect tempRect = this.mTempRect;
            thumbDrawable.copyBounds(tempRect);
            tempRect.offset(this.mPaddingLeft - this.mThumbOffset, this.mPaddingTop);
            tempRect.left += insets.left;
            tempRect.right -= insets.right;
            int saveCount = canvas.save();
            canvas.clipRect(tempRect, Region.EnumC0813Op.DIFFERENCE);
            super.drawTrack(canvas);
            drawTickMarks(canvas);
            canvas.restoreToCount(saveCount);
            return;
        }
        super.drawTrack(canvas);
        drawTickMarks(canvas);
    }

    protected void drawTickMarks(Canvas canvas) {
        if (this.mTickMark != null) {
            int count = getMax() - getMin();
            if (count > 1) {
                int w = this.mTickMark.getIntrinsicWidth();
                int h = this.mTickMark.getIntrinsicHeight();
                int halfW = w >= 0 ? w / 2 : 1;
                int halfH = h >= 0 ? h / 2 : 1;
                this.mTickMark.setBounds(-halfW, -halfH, halfW, halfH);
                float spacing = ((getWidth() - this.mPaddingLeft) - this.mPaddingRight) / count;
                int saveCount = canvas.save();
                canvas.translate(this.mPaddingLeft, getHeight() / 2);
                for (int i = 0; i <= count; i++) {
                    this.mTickMark.draw(canvas);
                    canvas.translate(spacing, 0.0f);
                }
                canvas.restoreToCount(saveCount);
            }
        }
    }

    void drawThumb(Canvas canvas) {
        if (this.mThumb != null) {
            int saveCount = canvas.save();
            canvas.translate(this.mPaddingLeft - this.mThumbOffset, this.mPaddingTop);
            this.mThumb.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ProgressBar, android.view.View
    public synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getCurrentDrawable();
        Drawable drawable = this.mThumb;
        int thumbHeight = drawable == null ? 0 : drawable.getIntrinsicHeight();
        int dw = 0;
        int dh = 0;
        if (d != null) {
            dw = Math.max(this.mMinWidth, Math.min(this.mMaxWidth, d.getIntrinsicWidth()));
            int dh2 = Math.max(this.mMinHeight, Math.min(this.mMaxHeight, d.getIntrinsicHeight()));
            dh = Math.max(thumbHeight, dh2);
        }
        setMeasuredDimension(resolveSizeAndState(dw + this.mPaddingLeft + this.mPaddingRight, widthMeasureSpec, 0), resolveSizeAndState(dh + this.mPaddingTop + this.mPaddingBottom, heightMeasureSpec, 0));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mIsUserSeekable && isEnabled()) {
            switch (event.getAction()) {
                case 0:
                    if (this.mThumb != null) {
                        int availableWidth = (getWidth() - this.mPaddingLeft) - this.mPaddingRight;
                        float progress = ((getProgress() - getMin()) / (getMax() - getMin())) - ((event.getX() - this.mPaddingLeft) / availableWidth);
                        this.mTouchThumbOffset = progress;
                        if (Math.abs(progress * availableWidth) > getThumbOffset()) {
                            this.mTouchThumbOffset = 0.0f;
                        }
                    }
                    if (isInScrollingContainer()) {
                        this.mTouchDownX = event.getX();
                        return true;
                    }
                    startDrag(event);
                    return true;
                case 1:
                    if (this.mIsDragging) {
                        trackTouchEvent(event);
                        onStopTrackingTouch();
                        setPressed(false);
                    } else {
                        onStartTrackingTouch();
                        trackTouchEvent(event);
                        onStopTrackingTouch();
                    }
                    invalidate();
                    return true;
                case 2:
                    if (this.mIsDragging) {
                        trackTouchEvent(event);
                        return true;
                    }
                    float x = event.getX();
                    if (Math.abs(x - this.mTouchDownX) > this.mScaledTouchSlop) {
                        startDrag(event);
                        return true;
                    }
                    return true;
                case 3:
                    if (this.mIsDragging) {
                        onStopTrackingTouch();
                        setPressed(false);
                    }
                    invalidate();
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    private void startDrag(MotionEvent event) {
        setPressed(true);
        Drawable drawable = this.mThumb;
        if (drawable != null) {
            invalidate(drawable.getBounds());
        }
        onStartTrackingTouch();
        trackTouchEvent(event);
        attemptClaimDrag();
    }

    private void setHotspot(float x, float y) {
        Drawable bg = getBackground();
        if (bg != null) {
            bg.setHotspot(x, y);
        }
    }

    private void trackTouchEvent(MotionEvent event) {
        float scale;
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());
        int width = getWidth();
        int availableWidth = (width - this.mPaddingLeft) - this.mPaddingRight;
        float progress = 0.0f;
        if (isLayoutRtl() && this.mMirrorForRtl) {
            if (x > width - this.mPaddingRight) {
                scale = 0.0f;
            } else if (x < this.mPaddingLeft) {
                scale = 1.0f;
            } else {
                scale = (((availableWidth - x) + this.mPaddingLeft) / availableWidth) + this.mTouchThumbOffset;
                progress = this.mTouchProgressOffset;
            }
        } else if (x < this.mPaddingLeft) {
            scale = 0.0f;
        } else if (x > width - this.mPaddingRight) {
            scale = 1.0f;
        } else {
            scale = ((x - this.mPaddingLeft) / availableWidth) + this.mTouchThumbOffset;
            progress = this.mTouchProgressOffset;
        }
        int range = getMax() - getMin();
        float progress2 = progress + (range * scale) + getMin();
        setHotspot(x, y);
        setProgressInternal(Math.round(progress2), true, false);
    }

    private void attemptClaimDrag() {
        if (this.mParent != null) {
            this.mParent.requestDisallowInterceptTouchEvent(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onStartTrackingTouch() {
        this.mIsDragging = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onStopTrackingTouch() {
        this.mIsDragging = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onKeyChange() {
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0013  */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0015  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0023  */
    @Override // android.view.View, android.view.KeyEvent.Callback
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isEnabled()) {
            int increment = this.mKeyProgressIncrement;
            switch (keyCode) {
                case 21:
                case 69:
                    increment = -increment;
                    if (setProgressInternal(getProgress() + (!isLayoutRtl() ? -increment : increment), true, true)) {
                        onKeyChange();
                        return true;
                    }
                    break;
                case 22:
                case 70:
                case 81:
                    if (setProgressInternal(getProgress() + (!isLayoutRtl() ? -increment : increment), true, true)) {
                    }
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.widget.ProgressBar, android.view.View
    public CharSequence getAccessibilityClassName() {
        return AbsSeekBar.class.getName();
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        if (isEnabled()) {
            int progress = getProgress();
            if (progress > getMin()) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
            }
            if (progress < getMax()) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
            }
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        if (super.performAccessibilityActionInternal(action, arguments)) {
            return true;
        }
        if (isEnabled()) {
            switch (action) {
                case 4096:
                case 8192:
                    if (canUserSetProgress()) {
                        int range = getMax() - getMin();
                        int increment = Math.max(1, Math.round(range / 20.0f));
                        if (action == 8192) {
                            increment = -increment;
                        }
                        if (setProgressInternal(getProgress() + increment, true, true)) {
                            onKeyChange();
                            return true;
                        }
                        return false;
                    }
                    return false;
                case 16908349:
                    if (canUserSetProgress() && arguments != null && arguments.containsKey(AccessibilityNodeInfo.ACTION_ARGUMENT_PROGRESS_VALUE)) {
                        float value = arguments.getFloat(AccessibilityNodeInfo.ACTION_ARGUMENT_PROGRESS_VALUE);
                        return setProgressInternal((int) value, true, true);
                    }
                    return false;
                default:
                    return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canUserSetProgress() {
        return !isIndeterminate() && isEnabled();
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        Drawable thumb = this.mThumb;
        if (thumb != null) {
            setThumbPos(getWidth(), thumb, getScale(), Integer.MIN_VALUE);
            invalidate();
        }
    }
}
