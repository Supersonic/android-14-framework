package com.android.internal.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemClock;
import android.util.AttributeSet;
import android.util.IntArray;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.internal.C4057R;
import com.android.internal.graphics.ColorUtils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes5.dex */
public class LockPatternView extends View {
    private static final int ASPECT_LOCK_HEIGHT = 2;
    private static final int ASPECT_LOCK_WIDTH = 1;
    private static final int ASPECT_SQUARE = 0;
    public static final boolean DEBUG_A11Y = false;
    private static final int DOT_ACTIVATION_DURATION_MILLIS = 50;
    private static final int DOT_RADIUS_DECREASE_DURATION_MILLIS = 192;
    private static final int DOT_RADIUS_INCREASE_DURATION_MILLIS = 96;
    private static final float DRAG_THRESHHOLD = 0.0f;
    private static final int LINE_END_ANIMATION_DURATION_MILLIS = 50;
    private static final int MILLIS_PER_CIRCLE_ANIMATING = 700;
    private static final float MIN_DOT_HIT_FACTOR = 0.2f;
    private static final boolean PROFILE_DRAWING = false;
    private static final String TAG = "LockPatternView";
    public static final int VIRTUAL_BASE_VIEW_ID = 1;
    private long mAnimatingPeriodStart;
    private int mAspect;
    private final CellState[][] mCellStates;
    private final Path mCurrentPath;
    private int mDotActivatedColor;
    private int mDotColor;
    private final float mDotHitFactor;
    private float mDotHitRadius;
    private final int mDotSize;
    private final int mDotSizeActivated;
    private boolean mDrawingProfilingStarted;
    private int mErrorColor;
    private final PatternExploreByTouchHelper mExploreByTouchHelper;
    private final LinearGradient mFadeOutGradientShader;
    private boolean mFadePattern;
    private final Interpolator mFastOutSlowInInterpolator;
    private float mInProgressX;
    private float mInProgressY;
    private boolean mInStealthMode;
    private boolean mInputEnabled;
    private final Rect mInvalidate;
    private final int mLineFadeOutAnimationDelayMs;
    private final int mLineFadeOutAnimationDurationMs;
    private long[] mLineFadeStart;
    private final Interpolator mLinearOutSlowInInterpolator;
    private Drawable mNotSelectedDrawable;
    private OnPatternListener mOnPatternListener;
    private final Paint mPaint;
    private final Paint mPathPaint;
    private final int mPathWidth;
    private final ArrayList<Cell> mPattern;
    private DisplayMode mPatternDisplayMode;
    private final boolean[][] mPatternDrawLookup;
    private boolean mPatternInProgress;
    private int mRegularColor;
    private Drawable mSelectedDrawable;
    private float mSquareHeight;
    private float mSquareWidth;
    private int mSuccessColor;
    private final Rect mTmpInvalidateRect;
    private boolean mUseLockPatternDrawable;

    /* loaded from: classes5.dex */
    public static class CellState {
        float activationAnimationProgress;
        Animator activationAnimator;
        int col;
        boolean hwAnimating;
        CanvasProperty<Float> hwCenterX;
        CanvasProperty<Float> hwCenterY;
        CanvasProperty<Paint> hwPaint;
        CanvasProperty<Float> hwRadius;
        float radius;
        int row;
        float translationY;
        float alpha = 1.0f;
        public float lineEndX = Float.MIN_VALUE;
        public float lineEndY = Float.MIN_VALUE;
    }

    /* loaded from: classes5.dex */
    public enum DisplayMode {
        Correct,
        Animate,
        Wrong
    }

    /* loaded from: classes5.dex */
    public interface OnPatternListener {
        void onPatternCellAdded(List<Cell> list);

        void onPatternCleared();

        void onPatternDetected(List<Cell> list);

        void onPatternStart();
    }

    /* loaded from: classes5.dex */
    public static final class Cell {
        private static final Cell[][] sCells = createCells();
        final int column;
        final int row;

        private static Cell[][] createCells() {
            Cell[][] res = (Cell[][]) Array.newInstance(Cell.class, 3, 3);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    res[i][j] = new Cell(i, j);
                }
            }
            return res;
        }

        private Cell(int row, int column) {
            checkRange(row, column);
            this.row = row;
            this.column = column;
        }

        public int getRow() {
            return this.row;
        }

        public int getColumn() {
            return this.column;
        }

        /* renamed from: of */
        public static Cell m15of(int row, int column) {
            checkRange(row, column);
            return sCells[row][column];
        }

        private static void checkRange(int row, int column) {
            if (row < 0 || row > 2) {
                throw new IllegalArgumentException("row must be in range 0-2");
            }
            if (column < 0 || column > 2) {
                throw new IllegalArgumentException("column must be in range 0-2");
            }
        }

        public String toString() {
            return "(row=" + this.row + ",clmn=" + this.column + NavigationBarInflaterView.KEY_CODE_END;
        }
    }

    public LockPatternView(Context context) {
        this(context, null);
    }

    public LockPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDrawingProfilingStarted = false;
        Paint paint = new Paint();
        this.mPaint = paint;
        Paint paint2 = new Paint();
        this.mPathPaint = paint2;
        this.mPattern = new ArrayList<>(9);
        this.mPatternDrawLookup = (boolean[][]) Array.newInstance(Boolean.TYPE, 3, 3);
        this.mInProgressX = -1.0f;
        this.mInProgressY = -1.0f;
        this.mLineFadeStart = new long[9];
        this.mPatternDisplayMode = DisplayMode.Correct;
        this.mInputEnabled = true;
        this.mInStealthMode = false;
        this.mPatternInProgress = false;
        this.mFadePattern = true;
        this.mCurrentPath = new Path();
        this.mInvalidate = new Rect();
        this.mTmpInvalidateRect = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.LockPatternView, C4057R.attr.lockPatternStyle, C4057R.C4062style.Widget_LockPatternView);
        String aspect = a.getString(0);
        if ("square".equals(aspect)) {
            this.mAspect = 0;
        } else if ("lock_width".equals(aspect)) {
            this.mAspect = 1;
        } else if ("lock_height".equals(aspect)) {
            this.mAspect = 2;
        } else {
            this.mAspect = 0;
        }
        setClickable(true);
        paint2.setAntiAlias(true);
        paint2.setDither(true);
        this.mRegularColor = a.getColor(5, 0);
        this.mErrorColor = a.getColor(3, 0);
        this.mSuccessColor = a.getColor(6, 0);
        int color = a.getColor(2, this.mRegularColor);
        this.mDotColor = color;
        this.mDotActivatedColor = a.getColor(1, color);
        int pathColor = a.getColor(4, this.mRegularColor);
        paint2.setColor(pathColor);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeJoin(Paint.Join.ROUND);
        paint2.setStrokeCap(Paint.Cap.ROUND);
        int dimensionPixelSize = getResources().getDimensionPixelSize(C4057R.dimen.lock_pattern_dot_line_width);
        this.mPathWidth = dimensionPixelSize;
        paint2.setStrokeWidth(dimensionPixelSize);
        this.mLineFadeOutAnimationDurationMs = getResources().getInteger(C4057R.integer.lock_pattern_line_fade_out_duration);
        this.mLineFadeOutAnimationDelayMs = getResources().getInteger(C4057R.integer.lock_pattern_line_fade_out_delay);
        this.mDotSize = getResources().getDimensionPixelSize(C4057R.dimen.lock_pattern_dot_size);
        this.mDotSizeActivated = getResources().getDimensionPixelSize(C4057R.dimen.lock_pattern_dot_size_activated);
        TypedValue outValue = new TypedValue();
        getResources().getValue(C4057R.dimen.lock_pattern_dot_hit_factor, outValue, true);
        this.mDotHitFactor = Math.max(Math.min(outValue.getFloat(), 1.0f), 0.2f);
        boolean z = getResources().getBoolean(C4057R.bool.use_lock_pattern_drawable);
        this.mUseLockPatternDrawable = z;
        if (z) {
            this.mSelectedDrawable = getResources().getDrawable(C4057R.C4058drawable.lockscreen_selected);
            this.mNotSelectedDrawable = getResources().getDrawable(C4057R.C4058drawable.lockscreen_notselected);
        }
        paint.setAntiAlias(true);
        paint.setDither(true);
        this.mCellStates = (CellState[][]) Array.newInstance(CellState.class, 3, 3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.mCellStates[i][j] = new CellState();
                this.mCellStates[i][j].radius = this.mDotSize / 2;
                this.mCellStates[i][j].row = i;
                this.mCellStates[i][j].col = j;
            }
        }
        this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563661);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
        PatternExploreByTouchHelper patternExploreByTouchHelper = new PatternExploreByTouchHelper(this);
        this.mExploreByTouchHelper = patternExploreByTouchHelper;
        setAccessibilityDelegate(patternExploreByTouchHelper);
        int fadeAwayGradientWidth = getResources().getDimensionPixelSize(C4057R.dimen.lock_pattern_fade_away_gradient_width);
        this.mFadeOutGradientShader = new LinearGradient((-fadeAwayGradientWidth) / 2.0f, 0.0f, fadeAwayGradientWidth / 2.0f, 0.0f, 0, pathColor, Shader.TileMode.CLAMP);
        a.recycle();
    }

    public CellState[][] getCellStates() {
        return this.mCellStates;
    }

    public boolean isInStealthMode() {
        return this.mInStealthMode;
    }

    public void setInStealthMode(boolean inStealthMode) {
        this.mInStealthMode = inStealthMode;
    }

    public void setFadePattern(boolean fadePattern) {
        this.mFadePattern = fadePattern;
    }

    public void setOnPatternListener(OnPatternListener onPatternListener) {
        this.mOnPatternListener = onPatternListener;
    }

    public void setPattern(DisplayMode displayMode, List<Cell> pattern) {
        this.mPattern.clear();
        this.mPattern.addAll(pattern);
        clearPatternDrawLookup();
        for (Cell cell : pattern) {
            this.mPatternDrawLookup[cell.getRow()][cell.getColumn()] = true;
        }
        setDisplayMode(displayMode);
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.mPatternDisplayMode = displayMode;
        if (displayMode == DisplayMode.Animate) {
            if (this.mPattern.size() == 0) {
                throw new IllegalStateException("you must have a pattern to animate if you want to set the display mode to animate");
            }
            this.mAnimatingPeriodStart = SystemClock.elapsedRealtime();
            Cell first = this.mPattern.get(0);
            this.mInProgressX = getCenterXForColumn(first.getColumn());
            this.mInProgressY = getCenterYForRow(first.getRow());
            clearPatternDrawLookup();
        }
        invalidate();
    }

    public void startCellStateAnimation(CellState cellState, float startAlpha, float endAlpha, float startTranslationY, float endTranslationY, float startScale, float endScale, long delay, long duration, Interpolator interpolator, Runnable finishRunnable) {
        if (isHardwareAccelerated()) {
            startCellStateAnimationHw(cellState, startAlpha, endAlpha, startTranslationY, endTranslationY, startScale, endScale, delay, duration, interpolator, finishRunnable);
        } else {
            startCellStateAnimationSw(cellState, startAlpha, endAlpha, startTranslationY, endTranslationY, startScale, endScale, delay, duration, interpolator, finishRunnable);
        }
    }

    private void startCellStateAnimationSw(final CellState cellState, final float startAlpha, final float endAlpha, final float startTranslationY, final float endTranslationY, final float startScale, final float endScale, long delay, long duration, Interpolator interpolator, final Runnable finishRunnable) {
        cellState.alpha = startAlpha;
        cellState.translationY = startTranslationY;
        cellState.radius = (this.mDotSize / 2) * startScale;
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.setInterpolator(interpolator);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.internal.widget.LockPatternView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = ((Float) animation.getAnimatedValue()).floatValue();
                cellState.alpha = ((1.0f - t) * startAlpha) + (endAlpha * t);
                cellState.translationY = ((1.0f - t) * startTranslationY) + (endTranslationY * t);
                cellState.radius = (LockPatternView.this.mDotSize / 2) * (((1.0f - t) * startScale) + (endScale * t));
                LockPatternView.this.invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.internal.widget.LockPatternView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Runnable runnable = finishRunnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        animator.start();
    }

    private void startCellStateAnimationHw(final CellState cellState, float startAlpha, float endAlpha, float startTranslationY, float endTranslationY, float startScale, float endScale, long delay, long duration, Interpolator interpolator, final Runnable finishRunnable) {
        cellState.alpha = endAlpha;
        cellState.translationY = endTranslationY;
        cellState.radius = (this.mDotSize / 2) * endScale;
        cellState.hwAnimating = true;
        cellState.hwCenterY = CanvasProperty.createFloat(getCenterYForRow(cellState.row) + startTranslationY);
        cellState.hwCenterX = CanvasProperty.createFloat(getCenterXForColumn(cellState.col));
        cellState.hwRadius = CanvasProperty.createFloat((this.mDotSize / 2) * startScale);
        this.mPaint.setColor(getDotColor());
        this.mPaint.setAlpha((int) (255.0f * startAlpha));
        cellState.hwPaint = CanvasProperty.createPaint(new Paint(this.mPaint));
        startRtFloatAnimation(cellState.hwCenterY, getCenterYForRow(cellState.row) + endTranslationY, delay, duration, interpolator);
        startRtFloatAnimation(cellState.hwRadius, (this.mDotSize / 2) * endScale, delay, duration, interpolator);
        startRtAlphaAnimation(cellState, endAlpha, delay, duration, interpolator, new AnimatorListenerAdapter() { // from class: com.android.internal.widget.LockPatternView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                cellState.hwAnimating = false;
                Runnable runnable = finishRunnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        invalidate();
    }

    private void startRtAlphaAnimation(CellState cellState, float endAlpha, long delay, long duration, Interpolator interpolator, Animator.AnimatorListener listener) {
        RenderNodeAnimator animator = new RenderNodeAnimator(cellState.hwPaint, 1, (int) (255.0f * endAlpha));
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.setInterpolator(interpolator);
        animator.setTarget((View) this);
        animator.addListener(listener);
        animator.start();
    }

    private void startRtFloatAnimation(CanvasProperty<Float> property, float endValue, long delay, long duration, Interpolator interpolator) {
        RenderNodeAnimator animator = new RenderNodeAnimator(property, endValue);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.setInterpolator(interpolator);
        animator.setTarget((View) this);
        animator.start();
    }

    private void notifyCellAdded() {
        OnPatternListener onPatternListener = this.mOnPatternListener;
        if (onPatternListener != null) {
            onPatternListener.onPatternCellAdded(this.mPattern);
        }
        this.mExploreByTouchHelper.invalidateRoot();
    }

    private void notifyPatternStarted() {
        sendAccessEvent(C4057R.string.lockscreen_access_pattern_start);
        OnPatternListener onPatternListener = this.mOnPatternListener;
        if (onPatternListener != null) {
            onPatternListener.onPatternStart();
        }
    }

    private void notifyPatternDetected() {
        sendAccessEvent(C4057R.string.lockscreen_access_pattern_detected);
        OnPatternListener onPatternListener = this.mOnPatternListener;
        if (onPatternListener != null) {
            onPatternListener.onPatternDetected(this.mPattern);
        }
    }

    private void notifyPatternCleared() {
        sendAccessEvent(C4057R.string.lockscreen_access_pattern_cleared);
        OnPatternListener onPatternListener = this.mOnPatternListener;
        if (onPatternListener != null) {
            onPatternListener.onPatternCleared();
        }
    }

    public void clearPattern() {
        resetPattern();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean dispatchHoverEvent(MotionEvent event) {
        boolean handled = super.dispatchHoverEvent(event);
        return handled | this.mExploreByTouchHelper.dispatchHoverEvent(event);
    }

    private void resetPattern() {
        this.mPattern.clear();
        clearPatternDrawLookup();
        this.mPatternDisplayMode = DisplayMode.Correct;
        invalidate();
    }

    public boolean isEmpty() {
        return this.mPattern.isEmpty();
    }

    private void clearPatternDrawLookup() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.mPatternDrawLookup[i][j] = false;
                this.mLineFadeStart[(j * 3) + i] = 0;
            }
        }
    }

    public void disableInput() {
        this.mInputEnabled = false;
    }

    public void enableInput() {
        this.mInputEnabled = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        int width = (w - this.mPaddingLeft) - this.mPaddingRight;
        this.mSquareWidth = width / 3.0f;
        int height = (h - this.mPaddingTop) - this.mPaddingBottom;
        this.mSquareHeight = height / 3.0f;
        this.mExploreByTouchHelper.invalidateRoot();
        this.mDotHitRadius = Math.min(this.mSquareHeight / 2.0f, this.mSquareWidth / 2.0f) * this.mDotHitFactor;
        if (this.mUseLockPatternDrawable) {
            this.mNotSelectedDrawable.setBounds(this.mPaddingLeft, this.mPaddingTop, width, height);
            this.mSelectedDrawable.setBounds(this.mPaddingLeft, this.mPaddingTop, width, height);
        }
    }

    private int resolveMeasured(int measureSpec, int desired) {
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (View.MeasureSpec.getMode(measureSpec)) {
            case Integer.MIN_VALUE:
                int result = Math.max(specSize, desired);
                return result;
            case 0:
                return desired;
            default:
                return specSize;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minimumWidth = getSuggestedMinimumWidth();
        int minimumHeight = getSuggestedMinimumHeight();
        int viewWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
        int viewHeight = resolveMeasured(heightMeasureSpec, minimumHeight);
        switch (this.mAspect) {
            case 0:
                int min = Math.min(viewWidth, viewHeight);
                viewHeight = min;
                viewWidth = min;
                break;
            case 1:
                viewHeight = Math.min(viewWidth, viewHeight);
                break;
            case 2:
                viewWidth = Math.min(viewWidth, viewHeight);
                break;
        }
        setMeasuredDimension(viewWidth, viewHeight);
    }

    private Cell detectAndAddHit(float x, float y) {
        Cell cell = checkForNewHit(x, y);
        if (cell != null) {
            Cell fillInGapCell = null;
            ArrayList<Cell> pattern = this.mPattern;
            if (!pattern.isEmpty()) {
                Cell lastCell = pattern.get(pattern.size() - 1);
                int dRow = cell.row - lastCell.row;
                int dColumn = cell.column - lastCell.column;
                int fillInRow = lastCell.row;
                int fillInColumn = lastCell.column;
                if (Math.abs(dRow) == 2 && Math.abs(dColumn) != 1) {
                    fillInRow = lastCell.row + (dRow > 0 ? 1 : -1);
                }
                if (Math.abs(dColumn) == 2 && Math.abs(dRow) != 1) {
                    fillInColumn = lastCell.column + (dColumn > 0 ? 1 : -1);
                }
                fillInGapCell = Cell.m15of(fillInRow, fillInColumn);
            }
            if (fillInGapCell != null && !this.mPatternDrawLookup[fillInGapCell.row][fillInGapCell.column]) {
                addCellToPattern(fillInGapCell);
            }
            addCellToPattern(cell);
            performHapticFeedback(1, 1);
            return cell;
        }
        return null;
    }

    private void addCellToPattern(Cell newCell) {
        this.mPatternDrawLookup[newCell.getRow()][newCell.getColumn()] = true;
        this.mPattern.add(newCell);
        if (!this.mInStealthMode) {
            startCellActivatedAnimation(newCell);
        }
        notifyCellAdded();
    }

    private void startCellActivatedAnimation(Cell cell) {
        final CellState cellState = this.mCellStates[cell.row][cell.column];
        if (cellState.activationAnimator != null) {
            cellState.activationAnimator.cancel();
        }
        AnimatorSet animatorSet = new AnimatorSet();
        AnimatorSet.Builder animatorSetBuilder = animatorSet.play(createLineDisappearingAnimation()).with(createLineEndAnimation(cellState, this.mInProgressX, this.mInProgressY, getCenterXForColumn(cell.column), getCenterYForRow(cell.row)));
        if (this.mDotSize != this.mDotSizeActivated) {
            animatorSetBuilder.with(createDotRadiusAnimation(cellState));
        }
        if (this.mDotColor != this.mDotActivatedColor) {
            animatorSetBuilder.with(createDotActivationColorAnimation(cellState));
        }
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.internal.widget.LockPatternView.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                cellState.activationAnimator = null;
                LockPatternView.this.invalidate();
            }
        });
        cellState.activationAnimator = animatorSet;
        animatorSet.start();
    }

    private Animator createDotActivationColorAnimation(final CellState cellState) {
        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.internal.widget.LockPatternView$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                LockPatternView.this.lambda$createDotActivationColorAnimation$0(cellState, valueAnimator);
            }
        };
        ValueAnimator activateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        ValueAnimator deactivateAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        activateAnimator.addUpdateListener(updateListener);
        deactivateAnimator.addUpdateListener(updateListener);
        activateAnimator.setInterpolator(this.mFastOutSlowInInterpolator);
        deactivateAnimator.setInterpolator(this.mLinearOutSlowInInterpolator);
        activateAnimator.setDuration(50L);
        deactivateAnimator.setDuration(50L);
        AnimatorSet set = new AnimatorSet();
        set.play(deactivateAnimator).after((this.mLineFadeOutAnimationDelayMs + this.mLineFadeOutAnimationDurationMs) - 100).after(activateAnimator);
        return set;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createDotActivationColorAnimation$0(CellState cellState, ValueAnimator valueAnimator) {
        cellState.activationAnimationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private Animator createLineEndAnimation(final CellState state, final float startX, final float startY, final float targetX, final float targetY) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.internal.widget.LockPatternView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                LockPatternView.this.lambda$createLineEndAnimation$1(state, startX, targetX, startY, targetY, valueAnimator2);
            }
        });
        valueAnimator.setInterpolator(this.mFastOutSlowInInterpolator);
        valueAnimator.setDuration(50L);
        return valueAnimator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createLineEndAnimation$1(CellState state, float startX, float targetX, float startY, float targetY, ValueAnimator animation) {
        float t = ((Float) animation.getAnimatedValue()).floatValue();
        state.lineEndX = ((1.0f - t) * startX) + (t * targetX);
        state.lineEndY = ((1.0f - t) * startY) + (t * targetY);
        invalidate();
    }

    private Animator createLineDisappearingAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.internal.widget.LockPatternView$$ExternalSyntheticLambda3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                LockPatternView.this.lambda$createLineDisappearingAnimation$2(valueAnimator2);
            }
        });
        valueAnimator.setStartDelay(this.mLineFadeOutAnimationDelayMs);
        valueAnimator.setDuration(this.mLineFadeOutAnimationDurationMs);
        return valueAnimator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createLineDisappearingAnimation$2(ValueAnimator animation) {
        invalidate();
    }

    private Animator createDotRadiusAnimation(final CellState state) {
        float defaultRadius = this.mDotSize / 2.0f;
        float activatedRadius = this.mDotSizeActivated / 2.0f;
        ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.internal.widget.LockPatternView$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                LockPatternView.this.lambda$createDotRadiusAnimation$3(state, valueAnimator);
            }
        };
        ValueAnimator activationAnimator = ValueAnimator.ofFloat(defaultRadius, activatedRadius);
        activationAnimator.addUpdateListener(animatorUpdateListener);
        activationAnimator.setInterpolator(this.mLinearOutSlowInInterpolator);
        activationAnimator.setDuration(96L);
        ValueAnimator deactivationAnimator = ValueAnimator.ofFloat(activatedRadius, defaultRadius);
        deactivationAnimator.addUpdateListener(animatorUpdateListener);
        deactivationAnimator.setInterpolator(this.mFastOutSlowInInterpolator);
        deactivationAnimator.setDuration(192L);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(activationAnimator, deactivationAnimator);
        return set;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createDotRadiusAnimation$3(CellState state, ValueAnimator animation) {
        state.radius = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    private Cell checkForNewHit(float x, float y) {
        Cell cellHit = detectCellHit(x, y);
        if (cellHit != null && !this.mPatternDrawLookup[cellHit.row][cellHit.column]) {
            return cellHit;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Cell detectCellHit(float x, float y) {
        float f = this.mDotHitRadius;
        float hitRadiusSquared = f * f;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                float centerY = getCenterYForRow(row);
                float centerX = getCenterXForColumn(column);
                if (((x - centerX) * (x - centerX)) + ((y - centerY) * (y - centerY)) < hitRadiusSquared) {
                    return Cell.m15of(row, column);
                }
            }
        }
        return null;
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent event) {
        if (AccessibilityManager.getInstance(this.mContext).isTouchExplorationEnabled()) {
            int action = event.getAction();
            switch (action) {
                case 7:
                    event.setAction(2);
                    break;
                case 9:
                    event.setAction(0);
                    break;
                case 10:
                    event.setAction(1);
                    break;
            }
            onTouchEvent(event);
            event.setAction(action);
        }
        return super.onHoverEvent(event);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mInputEnabled && isEnabled()) {
            switch (event.getAction()) {
                case 0:
                    handleActionDown(event);
                    return true;
                case 1:
                    handleActionUp();
                    return true;
                case 2:
                    handleActionMove(event);
                    return true;
                case 3:
                    if (this.mPatternInProgress) {
                        setPatternInProgress(false);
                        resetPattern();
                        notifyPatternCleared();
                    }
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private void setPatternInProgress(boolean progress) {
        this.mPatternInProgress = progress;
        this.mExploreByTouchHelper.invalidateRoot();
    }

    private void handleActionMove(MotionEvent event) {
        float radius;
        int historySize;
        boolean invalidateNow;
        MotionEvent motionEvent = event;
        float radius2 = this.mPathWidth;
        int historySize2 = event.getHistorySize();
        this.mTmpInvalidateRect.setEmpty();
        boolean invalidateNow2 = false;
        int i = 0;
        while (i < historySize2 + 1) {
            float x = i < historySize2 ? motionEvent.getHistoricalX(i) : event.getX();
            float y = i < historySize2 ? motionEvent.getHistoricalY(i) : event.getY();
            Cell hitCell = detectAndAddHit(x, y);
            int patternSize = this.mPattern.size();
            if (hitCell != null && patternSize == 1) {
                setPatternInProgress(true);
                notifyPatternStarted();
            }
            float dx = Math.abs(x - this.mInProgressX);
            float dy = Math.abs(y - this.mInProgressY);
            invalidateNow2 = (dx > 0.0f || dy > 0.0f) ? true : true;
            if (!this.mPatternInProgress || patternSize <= 0) {
                radius = radius2;
                historySize = historySize2;
                invalidateNow = invalidateNow2;
            } else {
                ArrayList<Cell> pattern = this.mPattern;
                Cell lastCell = pattern.get(patternSize - 1);
                float lastCellCenterX = getCenterXForColumn(lastCell.column);
                float lastCellCenterY = getCenterYForRow(lastCell.row);
                float left = Math.min(lastCellCenterX, x) - radius2;
                historySize = historySize2;
                float right = Math.max(lastCellCenterX, x) + radius2;
                invalidateNow = invalidateNow2;
                float top = Math.min(lastCellCenterY, y) - radius2;
                float bottom = Math.max(lastCellCenterY, y) + radius2;
                if (hitCell == null) {
                    radius = radius2;
                } else {
                    radius = radius2;
                    float radius3 = this.mSquareWidth;
                    float width = radius3 * 0.5f;
                    float height = this.mSquareHeight * 0.5f;
                    float hitCellCenterX = getCenterXForColumn(hitCell.column);
                    float hitCellCenterY = getCenterYForRow(hitCell.row);
                    left = Math.min(hitCellCenterX - width, left);
                    right = Math.max(hitCellCenterX + width, right);
                    top = Math.min(hitCellCenterY - height, top);
                    bottom = Math.max(hitCellCenterY + height, bottom);
                }
                this.mTmpInvalidateRect.union(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
            }
            i++;
            motionEvent = event;
            radius2 = radius;
            historySize2 = historySize;
            invalidateNow2 = invalidateNow;
        }
        this.mInProgressX = event.getX();
        this.mInProgressY = event.getY();
        if (invalidateNow2) {
            this.mInvalidate.union(this.mTmpInvalidateRect);
            invalidate(this.mInvalidate);
            this.mInvalidate.set(this.mTmpInvalidateRect);
        }
    }

    private void sendAccessEvent(int resId) {
        announceForAccessibility(this.mContext.getString(resId));
    }

    private void handleActionUp() {
        if (!this.mPattern.isEmpty()) {
            setPatternInProgress(false);
            cancelLineAnimations();
            notifyPatternDetected();
            if (this.mFadePattern) {
                clearPatternDrawLookup();
                this.mPatternDisplayMode = DisplayMode.Correct;
            }
            invalidate();
        }
    }

    private void cancelLineAnimations() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                CellState state = this.mCellStates[i][j];
                if (state.activationAnimator != null) {
                    state.activationAnimator.cancel();
                    state.activationAnimator = null;
                    state.radius = this.mDotSize / 2.0f;
                    state.activationAnimationProgress = 0.0f;
                    state.lineEndX = Float.MIN_VALUE;
                    state.lineEndY = Float.MIN_VALUE;
                }
            }
        }
    }

    private void handleActionDown(MotionEvent event) {
        resetPattern();
        float x = event.getX();
        float y = event.getY();
        Cell hitCell = detectAndAddHit(x, y);
        if (hitCell != null) {
            setPatternInProgress(true);
            this.mPatternDisplayMode = DisplayMode.Correct;
            notifyPatternStarted();
        } else if (this.mPatternInProgress) {
            setPatternInProgress(false);
            notifyPatternCleared();
        }
        if (hitCell != null) {
            float startX = getCenterXForColumn(hitCell.column);
            float startY = getCenterYForRow(hitCell.row);
            float widthOffset = this.mSquareWidth / 2.0f;
            float heightOffset = this.mSquareHeight / 2.0f;
            invalidate((int) (startX - widthOffset), (int) (startY - heightOffset), (int) (startX + widthOffset), (int) (startY + heightOffset));
        }
        this.mInProgressX = x;
        this.mInProgressY = y;
    }

    public void setColors(int regularColor, int successColor, int errorColor) {
        this.mRegularColor = regularColor;
        this.mErrorColor = errorColor;
        this.mSuccessColor = successColor;
        this.mPathPaint.setColor(regularColor);
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getCenterXForColumn(int column) {
        float f = this.mSquareWidth;
        return this.mPaddingLeft + (column * f) + (f / 2.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getCenterYForRow(int row) {
        float f = this.mSquareHeight;
        return this.mPaddingTop + (row * f) + (f / 2.0f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        float lastX;
        float lastY;
        int i;
        ArrayList<Cell> pattern;
        int count;
        float endX;
        float endY;
        ArrayList<Cell> pattern2 = this.mPattern;
        int count2 = pattern2.size();
        boolean[][] drawLookup = this.mPatternDrawLookup;
        if (this.mPatternDisplayMode == DisplayMode.Animate) {
            int oneCycle = (count2 + 1) * 700;
            int spotInCycle = ((int) (SystemClock.elapsedRealtime() - this.mAnimatingPeriodStart)) % oneCycle;
            int numCircles = spotInCycle / 700;
            clearPatternDrawLookup();
            for (int i2 = 0; i2 < numCircles; i2++) {
                Cell cell = pattern2.get(i2);
                drawLookup[cell.getRow()][cell.getColumn()] = true;
            }
            boolean needToUpdateInProgressPoint = numCircles > 0 && numCircles < count2;
            if (needToUpdateInProgressPoint) {
                float percentageOfNextCircle = (spotInCycle % 700) / 700.0f;
                Cell currentCell = pattern2.get(numCircles - 1);
                float centerX = getCenterXForColumn(currentCell.column);
                float centerY = getCenterYForRow(currentCell.row);
                Cell nextCell = pattern2.get(numCircles);
                float dx = (getCenterXForColumn(nextCell.column) - centerX) * percentageOfNextCircle;
                float dy = (getCenterYForRow(nextCell.row) - centerY) * percentageOfNextCircle;
                this.mInProgressX = centerX + dx;
                this.mInProgressY = centerY + dy;
            }
            invalidate();
        }
        Path currentPath = this.mCurrentPath;
        currentPath.rewind();
        boolean drawPath = !this.mInStealthMode;
        if (drawPath) {
            this.mPathPaint.setColor(getCurrentColor(true));
            boolean anyCircles = false;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            float lastX2 = 0.0f;
            float lastY2 = 0.0f;
            int i3 = 0;
            while (true) {
                if (i3 >= count2) {
                    lastX = lastX2;
                    lastY = lastY2;
                    break;
                }
                Cell cell2 = pattern2.get(i3);
                if (!drawLookup[cell2.row][cell2.column]) {
                    lastX = lastX2;
                    lastY = lastY2;
                    break;
                }
                long[] jArr = this.mLineFadeStart;
                if (jArr[i3] == 0) {
                    jArr[i3] = SystemClock.elapsedRealtime();
                }
                float centerX2 = getCenterXForColumn(cell2.column);
                float centerY2 = getCenterYForRow(cell2.row);
                if (i3 == 0) {
                    i = i3;
                    pattern = pattern2;
                    count = count2;
                } else {
                    CellState state = this.mCellStates[cell2.row][cell2.column];
                    currentPath.rewind();
                    if (state.lineEndX != Float.MIN_VALUE && state.lineEndY != Float.MIN_VALUE) {
                        float endX2 = state.lineEndX;
                        endX = endX2;
                        endY = state.lineEndY;
                    } else {
                        endX = centerX2;
                        endY = centerY2;
                    }
                    i = i3;
                    pattern = pattern2;
                    count = count2;
                    drawLineSegment(canvas, lastX2, lastY2, endX, endY, this.mLineFadeStart[i3], elapsedRealtime);
                }
                lastX2 = centerX2;
                lastY2 = centerY2;
                i3 = i + 1;
                anyCircles = true;
                pattern2 = pattern;
                count2 = count;
            }
            if ((this.mPatternInProgress || this.mPatternDisplayMode == DisplayMode.Animate) && anyCircles) {
                currentPath.rewind();
                currentPath.moveTo(lastX, lastY);
                currentPath.lineTo(this.mInProgressX, this.mInProgressY);
                this.mPathPaint.setAlpha((int) (calculateLastSegmentAlpha(this.mInProgressX, this.mInProgressY, lastX, lastY) * 255.0f));
                canvas.drawPath(currentPath, this.mPathPaint);
            }
        }
        for (int i4 = 0; i4 < 3; i4++) {
            float centerY3 = getCenterYForRow(i4);
            for (int j = 0; j < 3; j++) {
                CellState cellState = this.mCellStates[i4][j];
                float centerX3 = getCenterXForColumn(j);
                float translationY = cellState.translationY;
                if (this.mUseLockPatternDrawable) {
                    drawCellDrawable(canvas, i4, j, cellState.radius, drawLookup[i4][j]);
                } else if (!isHardwareAccelerated() || !cellState.hwAnimating) {
                    drawCircle(canvas, (int) centerX3, ((int) centerY3) + translationY, cellState.radius, drawLookup[i4][j], cellState.alpha, cellState.activationAnimationProgress);
                } else {
                    RecordingCanvas recordingCanvas = (RecordingCanvas) canvas;
                    recordingCanvas.drawCircle(cellState.hwCenterX, cellState.hwCenterY, cellState.hwRadius, cellState.hwPaint);
                }
            }
        }
    }

    private void drawLineSegment(Canvas canvas, float startX, float startY, float endX, float endY, long lineFadeStart, long elapsedRealtime) {
        if (this.mFadePattern) {
            int i = this.mLineFadeOutAnimationDelayMs;
            int i2 = this.mLineFadeOutAnimationDurationMs;
            if (elapsedRealtime - lineFadeStart >= i + i2) {
                return;
            }
            float fadeAwayProgress = Math.max(((float) ((elapsedRealtime - lineFadeStart) - i)) / i2, 0.0f);
            drawFadingAwayLineSegment(canvas, startX, startY, endX, endY, fadeAwayProgress);
            return;
        }
        this.mPathPaint.setAlpha(255);
        canvas.drawLine(startX, startY, endX, endY, this.mPathPaint);
    }

    private void drawFadingAwayLineSegment(Canvas canvas, float startX, float startY, float endX, float endY, float fadeAwayProgress) {
        float segmentAngleDegrees;
        this.mPathPaint.setAlpha((int) ((1.0f - fadeAwayProgress) * 255.0f));
        this.mPathPaint.setShader(this.mFadeOutGradientShader);
        canvas.save();
        float gradientMidX = (endX * fadeAwayProgress) + ((1.0f - fadeAwayProgress) * startX);
        float gradientMidY = (endY * fadeAwayProgress) + ((1.0f - fadeAwayProgress) * startY);
        canvas.translate(gradientMidX, gradientMidY);
        double segmentAngleRad = Math.atan((endY - startY) / (endX - startX));
        float segmentAngleDegrees2 = (float) Math.toDegrees(segmentAngleRad);
        if (endX - startX >= 0.0f) {
            segmentAngleDegrees = segmentAngleDegrees2;
        } else {
            segmentAngleDegrees = segmentAngleDegrees2 + 180.0f;
        }
        canvas.rotate(segmentAngleDegrees);
        float segmentLength = (float) Math.hypot(endX - startX, endY - startY);
        canvas.drawLine((-segmentLength) * fadeAwayProgress, 0.0f, segmentLength * (1.0f - fadeAwayProgress), 0.0f, this.mPathPaint);
        canvas.restore();
        this.mPathPaint.setShader(null);
    }

    private float calculateLastSegmentAlpha(float x, float y, float lastX, float lastY) {
        float diffX = x - lastX;
        float diffY = y - lastY;
        float dist = (float) Math.sqrt((diffX * diffX) + (diffY * diffY));
        float frac = dist / this.mSquareWidth;
        return Math.min(1.0f, Math.max(0.0f, (frac - 0.3f) * 4.0f));
    }

    private int getDotColor() {
        if (this.mInStealthMode) {
            return this.mDotColor;
        }
        if (this.mPatternDisplayMode == DisplayMode.Wrong) {
            return this.mErrorColor;
        }
        return this.mDotColor;
    }

    private int getCurrentColor(boolean partOfPattern) {
        if (!partOfPattern || this.mInStealthMode || this.mPatternInProgress) {
            return this.mRegularColor;
        }
        if (this.mPatternDisplayMode == DisplayMode.Wrong) {
            return this.mErrorColor;
        }
        if (this.mPatternDisplayMode == DisplayMode.Correct || this.mPatternDisplayMode == DisplayMode.Animate) {
            return this.mSuccessColor;
        }
        throw new IllegalStateException("unknown display mode " + this.mPatternDisplayMode);
    }

    private void drawCircle(Canvas canvas, float centerX, float centerY, float radius, boolean partOfPattern, float alpha, float activationAnimationProgress) {
        if (this.mFadePattern && !this.mInStealthMode) {
            int resultColor = ColorUtils.blendARGB(this.mDotColor, this.mDotActivatedColor, activationAnimationProgress);
            this.mPaint.setColor(resultColor);
        } else {
            this.mPaint.setColor(getDotColor());
        }
        this.mPaint.setAlpha((int) (255.0f * alpha));
        canvas.drawCircle(centerX, centerY, radius, this.mPaint);
    }

    private void drawCellDrawable(Canvas canvas, int i, int j, float radius, boolean partOfPattern) {
        Rect dst = new Rect((int) (this.mPaddingLeft + (j * this.mSquareWidth)), (int) (this.mPaddingTop + (i * this.mSquareHeight)), (int) (this.mPaddingLeft + ((j + 1) * this.mSquareWidth)), (int) (this.mPaddingTop + ((i + 1) * this.mSquareHeight)));
        float scale = radius / (this.mDotSize / 2);
        canvas.save();
        canvas.clipRect(dst);
        canvas.scale(scale, scale, dst.centerX(), dst.centerY());
        if (!partOfPattern || scale > 1.0f) {
            this.mNotSelectedDrawable.draw(canvas);
        } else {
            this.mSelectedDrawable.draw(canvas);
        }
        canvas.restore();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        byte[] patternBytes = LockPatternUtils.patternToByteArray(this.mPattern);
        String patternString = patternBytes != null ? new String(patternBytes) : null;
        return new SavedState(superState, patternString, this.mPatternDisplayMode.ordinal(), this.mInputEnabled, this.mInStealthMode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setPattern(DisplayMode.Correct, LockPatternUtils.byteArrayToPattern(ss.getSerializedPattern().getBytes()));
        this.mPatternDisplayMode = DisplayMode.values()[ss.getDisplayMode()];
        this.mInputEnabled = ss.isInputEnabled();
        this.mInStealthMode = ss.isInStealthMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.internal.widget.LockPatternView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private final int mDisplayMode;
        private final boolean mInStealthMode;
        private final boolean mInputEnabled;
        private final String mSerializedPattern;

        private SavedState(Parcelable superState, String serializedPattern, int displayMode, boolean inputEnabled, boolean inStealthMode) {
            super(superState);
            this.mSerializedPattern = serializedPattern;
            this.mDisplayMode = displayMode;
            this.mInputEnabled = inputEnabled;
            this.mInStealthMode = inStealthMode;
        }

        private SavedState(Parcel in) {
            super(in);
            this.mSerializedPattern = in.readString();
            this.mDisplayMode = in.readInt();
            this.mInputEnabled = ((Boolean) in.readValue(null)).booleanValue();
            this.mInStealthMode = ((Boolean) in.readValue(null)).booleanValue();
        }

        public String getSerializedPattern() {
            return this.mSerializedPattern;
        }

        public int getDisplayMode() {
            return this.mDisplayMode;
        }

        public boolean isInputEnabled() {
            return this.mInputEnabled;
        }

        public boolean isInStealthMode() {
            return this.mInStealthMode;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.mSerializedPattern);
            dest.writeInt(this.mDisplayMode);
            dest.writeValue(Boolean.valueOf(this.mInputEnabled));
            dest.writeValue(Boolean.valueOf(this.mInStealthMode));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public final class PatternExploreByTouchHelper extends ExploreByTouchHelper {
        private final SparseArray<VirtualViewContainer> mItems;
        private Rect mTempRect;

        /* loaded from: classes5.dex */
        class VirtualViewContainer {
            CharSequence description;

            public VirtualViewContainer(CharSequence description) {
                this.description = description;
            }
        }

        public PatternExploreByTouchHelper(View forView) {
            super(forView);
            this.mTempRect = new Rect();
            this.mItems = new SparseArray<>();
            for (int i = 1; i < 10; i++) {
                this.mItems.put(i, new VirtualViewContainer(getTextForVirtualView(i)));
            }
        }

        @Override // com.android.internal.widget.ExploreByTouchHelper
        protected int getVirtualViewAt(float x, float y) {
            return getVirtualViewIdForHit(x, y);
        }

        @Override // com.android.internal.widget.ExploreByTouchHelper
        protected void getVisibleVirtualViews(IntArray virtualViewIds) {
            if (!LockPatternView.this.mPatternInProgress) {
                return;
            }
            for (int i = 1; i < 10; i++) {
                virtualViewIds.add(i);
            }
        }

        @Override // com.android.internal.widget.ExploreByTouchHelper
        protected void onPopulateEventForVirtualView(int virtualViewId, AccessibilityEvent event) {
            VirtualViewContainer container = this.mItems.get(virtualViewId);
            if (container != null) {
                event.getText().add(container.description);
            }
        }

        @Override // android.view.View.AccessibilityDelegate
        public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onPopulateAccessibilityEvent(host, event);
            if (!LockPatternView.this.mPatternInProgress) {
                CharSequence contentDescription = LockPatternView.this.getContext().getText(C4057R.string.lockscreen_access_pattern_area);
                event.setContentDescription(contentDescription);
            }
        }

        @Override // com.android.internal.widget.ExploreByTouchHelper
        protected void onPopulateNodeForVirtualView(int virtualViewId, AccessibilityNodeInfo node) {
            node.setText(getTextForVirtualView(virtualViewId));
            node.setContentDescription(getTextForVirtualView(virtualViewId));
            if (LockPatternView.this.mPatternInProgress) {
                node.setFocusable(true);
                if (isClickable(virtualViewId)) {
                    node.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                    node.setClickable(isClickable(virtualViewId));
                }
            }
            Rect bounds = getBoundsForVirtualView(virtualViewId);
            node.setBoundsInParent(bounds);
        }

        private boolean isClickable(int virtualViewId) {
            if (virtualViewId != Integer.MIN_VALUE) {
                int row = (virtualViewId - 1) / 3;
                int col = (virtualViewId - 1) % 3;
                if (row < 3) {
                    return !LockPatternView.this.mPatternDrawLookup[row][col];
                }
                return false;
            }
            return false;
        }

        @Override // com.android.internal.widget.ExploreByTouchHelper
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action, Bundle arguments) {
            switch (action) {
                case 16:
                    return onItemClicked(virtualViewId);
                default:
                    return false;
            }
        }

        boolean onItemClicked(int index) {
            invalidateVirtualView(index);
            sendEventForVirtualView(index, 1);
            return true;
        }

        private Rect getBoundsForVirtualView(int virtualViewId) {
            int ordinal = virtualViewId - 1;
            Rect bounds = this.mTempRect;
            int row = ordinal / 3;
            int col = ordinal % 3;
            float centerX = LockPatternView.this.getCenterXForColumn(col);
            float centerY = LockPatternView.this.getCenterYForRow(row);
            float cellHitRadius = LockPatternView.this.mDotHitRadius;
            bounds.left = (int) (centerX - cellHitRadius);
            bounds.right = (int) (centerX + cellHitRadius);
            bounds.top = (int) (centerY - cellHitRadius);
            bounds.bottom = (int) (centerY + cellHitRadius);
            return bounds;
        }

        private CharSequence getTextForVirtualView(int virtualViewId) {
            Resources res = LockPatternView.this.getResources();
            return res.getString(C4057R.string.lockscreen_access_pattern_cell_added_verbose, Integer.valueOf(virtualViewId));
        }

        private int getVirtualViewIdForHit(float x, float y) {
            Cell cellHit = LockPatternView.this.detectCellHit(x, y);
            if (cellHit == null) {
                return Integer.MIN_VALUE;
            }
            boolean dotAvailable = LockPatternView.this.mPatternDrawLookup[cellHit.row][cellHit.column];
            int dotId = (cellHit.row * 3) + cellHit.column + 1;
            if (dotAvailable) {
                return dotId;
            }
            return Integer.MIN_VALUE;
        }
    }
}
