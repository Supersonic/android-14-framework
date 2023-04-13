package android.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.SystemClock;
import android.text.TextUtils;
import android.util.IntProperty;
import android.util.MathUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.ImageView;
import com.android.internal.C4057R;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class FastScroller {
    private static final int DURATION_CROSS_FADE = 50;
    private static final int DURATION_FADE_IN = 150;
    private static final int DURATION_FADE_OUT = 300;
    private static final int DURATION_RESIZE = 100;
    private static final long FADE_TIMEOUT = 1500;
    private static final int MIN_PAGES = 4;
    private static final int OVERLAY_ABOVE_THUMB = 2;
    private static final int OVERLAY_AT_THUMB = 1;
    private static final int OVERLAY_FLOATING = 0;
    private static final int PREVIEW_LEFT = 0;
    private static final int PREVIEW_RIGHT = 1;
    private static final int STATE_DRAGGING = 2;
    private static final int STATE_NONE = 0;
    private static final int STATE_VISIBLE = 1;
    private static final int THUMB_POSITION_INSIDE = 1;
    private static final int THUMB_POSITION_MIDPOINT = 0;
    private boolean mAlwaysShow;
    private AnimatorSet mDecorAnimation;
    private boolean mEnabled;
    private int mFirstVisibleItem;
    private int mHeaderCount;
    private float mInitialTouchY;
    private boolean mLayoutFromRight;
    private final AbsListView mList;
    private Adapter mListAdapter;
    private boolean mLongList;
    private boolean mMatchDragPosition;
    private final int mMinimumTouchTarget;
    private int mOldChildCount;
    private int mOldItemCount;
    private final ViewGroupOverlay mOverlay;
    private int mOverlayPosition;
    private AnimatorSet mPreviewAnimation;
    private final View mPreviewImage;
    private int mPreviewMinHeight;
    private int mPreviewMinWidth;
    private int mPreviewPadding;
    private final TextView mPrimaryText;
    private int mScaledTouchSlop;
    private int mScrollBarStyle;
    private boolean mScrollCompleted;
    private final TextView mSecondaryText;
    private SectionIndexer mSectionIndexer;
    private Object[] mSections;
    private boolean mShowingPreview;
    private boolean mShowingPrimary;
    private int mState;
    private int mTextAppearance;
    private ColorStateList mTextColor;
    private float mTextSize;
    private Drawable mThumbDrawable;
    private final ImageView mThumbImage;
    private int mThumbMinHeight;
    private int mThumbMinWidth;
    private float mThumbOffset;
    private int mThumbPosition;
    private float mThumbRange;
    private Drawable mTrackDrawable;
    private final ImageView mTrackImage;
    private boolean mUpdatingLayout;
    private int mWidth;
    private static final long TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private static Property<View, Integer> LEFT = new IntProperty<View>(NavigationBarInflaterView.LEFT) { // from class: android.widget.FastScroller.3
        @Override // android.util.IntProperty
        public void setValue(View object, int value) {
            object.setLeft(value);
        }

        @Override // android.util.Property
        public Integer get(View object) {
            return Integer.valueOf(object.getLeft());
        }
    };
    private static Property<View, Integer> TOP = new IntProperty<View>("top") { // from class: android.widget.FastScroller.4
        @Override // android.util.IntProperty
        public void setValue(View object, int value) {
            object.setTop(value);
        }

        @Override // android.util.Property
        public Integer get(View object) {
            return Integer.valueOf(object.getTop());
        }
    };
    private static Property<View, Integer> RIGHT = new IntProperty<View>(NavigationBarInflaterView.RIGHT) { // from class: android.widget.FastScroller.5
        @Override // android.util.IntProperty
        public void setValue(View object, int value) {
            object.setRight(value);
        }

        @Override // android.util.Property
        public Integer get(View object) {
            return Integer.valueOf(object.getRight());
        }
    };
    private static Property<View, Integer> BOTTOM = new IntProperty<View>("bottom") { // from class: android.widget.FastScroller.6
        @Override // android.util.IntProperty
        public void setValue(View object, int value) {
            object.setBottom(value);
        }

        @Override // android.util.Property
        public Integer get(View object) {
            return Integer.valueOf(object.getBottom());
        }
    };
    private final Rect mTempBounds = new Rect();
    private final Rect mTempMargins = new Rect();
    private final Rect mContainerRect = new Rect();
    private final int[] mPreviewResId = new int[2];
    private int mCurrentSection = -1;
    private int mScrollbarPosition = -1;
    private long mPendingDrag = -1;
    private final Runnable mDeferHide = new Runnable() { // from class: android.widget.FastScroller.1
        @Override // java.lang.Runnable
        public void run() {
            FastScroller.this.setState(0);
        }
    };
    private final Animator.AnimatorListener mSwitchPrimaryListener = new AnimatorListenerAdapter() { // from class: android.widget.FastScroller.2
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            FastScroller fastScroller = FastScroller.this;
            fastScroller.mShowingPrimary = !fastScroller.mShowingPrimary;
        }
    };

    public FastScroller(AbsListView listView, int styleResId) {
        this.mList = listView;
        this.mOldItemCount = listView.getCount();
        this.mOldChildCount = listView.getChildCount();
        Context context = listView.getContext();
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mScrollBarStyle = listView.getScrollBarStyle();
        this.mScrollCompleted = true;
        this.mState = 1;
        this.mMatchDragPosition = context.getApplicationInfo().targetSdkVersion >= 11;
        ImageView imageView = new ImageView(context);
        this.mTrackImage = imageView;
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView imageView2 = new ImageView(context);
        this.mThumbImage = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
        View view = new View(context);
        this.mPreviewImage = view;
        view.setAlpha(0.0f);
        TextView createPreviewTextView = createPreviewTextView(context);
        this.mPrimaryText = createPreviewTextView;
        TextView createPreviewTextView2 = createPreviewTextView(context);
        this.mSecondaryText = createPreviewTextView2;
        this.mMinimumTouchTarget = listView.getResources().getDimensionPixelSize(C4057R.dimen.fast_scroller_minimum_touch_target);
        setStyle(styleResId);
        ViewGroupOverlay overlay = listView.getOverlay();
        this.mOverlay = overlay;
        overlay.add(imageView);
        overlay.add(imageView2);
        overlay.add(view);
        overlay.add(createPreviewTextView);
        overlay.add(createPreviewTextView2);
        getSectionsFromIndexer();
        updateLongList(this.mOldChildCount, this.mOldItemCount);
        setScrollbarPosition(listView.getVerticalScrollbarPosition());
        postAutoHide();
    }

    private void updateAppearance() {
        this.mTrackImage.setImageDrawable(this.mTrackDrawable);
        Drawable drawable = this.mTrackDrawable;
        int width = drawable != null ? Math.max(0, drawable.getIntrinsicWidth()) : 0;
        this.mThumbImage.setImageDrawable(this.mThumbDrawable);
        this.mThumbImage.setMinimumWidth(this.mThumbMinWidth);
        this.mThumbImage.setMinimumHeight(this.mThumbMinHeight);
        Drawable drawable2 = this.mThumbDrawable;
        if (drawable2 != null) {
            width = Math.max(width, drawable2.getIntrinsicWidth());
        }
        this.mWidth = Math.max(width, this.mThumbMinWidth);
        int i = this.mTextAppearance;
        if (i != 0) {
            this.mPrimaryText.setTextAppearance(i);
            this.mSecondaryText.setTextAppearance(this.mTextAppearance);
        }
        ColorStateList colorStateList = this.mTextColor;
        if (colorStateList != null) {
            this.mPrimaryText.setTextColor(colorStateList);
            this.mSecondaryText.setTextColor(this.mTextColor);
        }
        float f = this.mTextSize;
        if (f > 0.0f) {
            this.mPrimaryText.setTextSize(0, f);
            this.mSecondaryText.setTextSize(0, this.mTextSize);
        }
        int padding = this.mPreviewPadding;
        this.mPrimaryText.setIncludeFontPadding(false);
        this.mPrimaryText.setPadding(padding, padding, padding, padding);
        this.mSecondaryText.setIncludeFontPadding(false);
        this.mSecondaryText.setPadding(padding, padding, padding, padding);
        refreshDrawablePressedState();
    }

    public void setStyle(int resId) {
        Context context = this.mList.getContext();
        TypedArray ta = context.obtainStyledAttributes(null, C4057R.styleable.FastScroll, 16843767, resId);
        int N = ta.getIndexCount();
        for (int i = 0; i < N; i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case 0:
                    this.mTextAppearance = ta.getResourceId(index, 0);
                    break;
                case 1:
                    this.mTextSize = ta.getDimensionPixelSize(index, 0);
                    break;
                case 2:
                    this.mTextColor = ta.getColorStateList(index);
                    break;
                case 3:
                    this.mPreviewPadding = ta.getDimensionPixelSize(index, 0);
                    break;
                case 4:
                    this.mPreviewMinWidth = ta.getDimensionPixelSize(index, 0);
                    break;
                case 5:
                    this.mPreviewMinHeight = ta.getDimensionPixelSize(index, 0);
                    break;
                case 6:
                    this.mThumbPosition = ta.getInt(index, 0);
                    break;
                case 7:
                    this.mPreviewResId[0] = ta.getResourceId(index, 0);
                    break;
                case 8:
                    this.mPreviewResId[1] = ta.getResourceId(index, 0);
                    break;
                case 9:
                    this.mOverlayPosition = ta.getInt(index, 0);
                    break;
                case 10:
                    this.mThumbDrawable = ta.getDrawable(index);
                    break;
                case 11:
                    this.mThumbMinHeight = ta.getDimensionPixelSize(index, 0);
                    break;
                case 12:
                    this.mThumbMinWidth = ta.getDimensionPixelSize(index, 0);
                    break;
                case 13:
                    this.mTrackDrawable = ta.getDrawable(index);
                    break;
            }
        }
        ta.recycle();
        updateAppearance();
    }

    public void remove() {
        this.mOverlay.remove(this.mTrackImage);
        this.mOverlay.remove(this.mThumbImage);
        this.mOverlay.remove(this.mPreviewImage);
        this.mOverlay.remove(this.mPrimaryText);
        this.mOverlay.remove(this.mSecondaryText);
    }

    public void setEnabled(boolean enabled) {
        if (this.mEnabled != enabled) {
            this.mEnabled = enabled;
            onStateDependencyChanged(true);
        }
    }

    public boolean isEnabled() {
        return this.mEnabled && (this.mLongList || this.mAlwaysShow);
    }

    public void setAlwaysShow(boolean alwaysShow) {
        if (this.mAlwaysShow != alwaysShow) {
            this.mAlwaysShow = alwaysShow;
            onStateDependencyChanged(false);
        }
    }

    public boolean isAlwaysShowEnabled() {
        return this.mAlwaysShow;
    }

    private void onStateDependencyChanged(boolean peekIfEnabled) {
        if (isEnabled()) {
            if (isAlwaysShowEnabled()) {
                setState(1);
            } else if (this.mState == 1) {
                postAutoHide();
            } else if (peekIfEnabled) {
                setState(1);
                postAutoHide();
            }
        } else {
            stop();
        }
        this.mList.resolvePadding();
    }

    public void setScrollBarStyle(int style) {
        if (this.mScrollBarStyle != style) {
            this.mScrollBarStyle = style;
            updateLayout();
        }
    }

    public void stop() {
        setState(0);
    }

    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1, types: [boolean] */
    /* JADX WARN: Type inference failed for: r0v3 */
    public void setScrollbarPosition(int position) {
        ?? r0 = 1;
        if (position == 0) {
            position = this.mList.isLayoutRtl() ? 1 : 2;
        }
        if (this.mScrollbarPosition != position) {
            this.mScrollbarPosition = position;
            if (position == 1) {
                r0 = 0;
            }
            this.mLayoutFromRight = r0;
            int previewResId = this.mPreviewResId[r0];
            this.mPreviewImage.setBackgroundResource(previewResId);
            int textMinWidth = Math.max(0, (this.mPreviewMinWidth - this.mPreviewImage.getPaddingLeft()) - this.mPreviewImage.getPaddingRight());
            this.mPrimaryText.setMinimumWidth(textMinWidth);
            this.mSecondaryText.setMinimumWidth(textMinWidth);
            int textMinHeight = Math.max(0, (this.mPreviewMinHeight - this.mPreviewImage.getPaddingTop()) - this.mPreviewImage.getPaddingBottom());
            this.mPrimaryText.setMinimumHeight(textMinHeight);
            this.mSecondaryText.setMinimumHeight(textMinHeight);
            updateLayout();
        }
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        updateLayout();
    }

    public void onItemCountChanged(int childCount, int itemCount) {
        if (this.mOldItemCount != itemCount || this.mOldChildCount != childCount) {
            this.mOldItemCount = itemCount;
            this.mOldChildCount = childCount;
            boolean hasMoreItems = itemCount - childCount > 0;
            if (hasMoreItems && this.mState != 2) {
                int firstVisibleItem = this.mList.getFirstVisiblePosition();
                setThumbPos(getPosFromItemCount(firstVisibleItem, childCount, itemCount));
            }
            updateLongList(childCount, itemCount);
        }
    }

    private void updateLongList(int childCount, int itemCount) {
        boolean longList = childCount > 0 && itemCount / childCount >= 4;
        if (this.mLongList != longList) {
            this.mLongList = longList;
            onStateDependencyChanged(false);
        }
    }

    private TextView createPreviewTextView(Context context) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-2, -2);
        TextView textView = new TextView(context);
        textView.setLayoutParams(params);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        textView.setGravity(17);
        textView.setAlpha(0.0f);
        textView.setLayoutDirection(this.mList.getLayoutDirection());
        return textView;
    }

    public void updateLayout() {
        if (this.mUpdatingLayout) {
            return;
        }
        this.mUpdatingLayout = true;
        updateContainerRect();
        layoutThumb();
        layoutTrack();
        updateOffsetAndRange();
        Rect bounds = this.mTempBounds;
        measurePreview(this.mPrimaryText, bounds);
        applyLayout(this.mPrimaryText, bounds);
        measurePreview(this.mSecondaryText, bounds);
        applyLayout(this.mSecondaryText, bounds);
        if (this.mPreviewImage != null) {
            bounds.left -= this.mPreviewImage.getPaddingLeft();
            bounds.top -= this.mPreviewImage.getPaddingTop();
            bounds.right += this.mPreviewImage.getPaddingRight();
            bounds.bottom += this.mPreviewImage.getPaddingBottom();
            applyLayout(this.mPreviewImage, bounds);
        }
        this.mUpdatingLayout = false;
    }

    private void applyLayout(View view, Rect bounds) {
        view.layout(bounds.left, bounds.top, bounds.right, bounds.bottom);
        view.setPivotX(this.mLayoutFromRight ? bounds.right - bounds.left : 0.0f);
    }

    private void measurePreview(View v, Rect out) {
        Rect margins = this.mTempMargins;
        margins.left = this.mPreviewImage.getPaddingLeft();
        margins.top = this.mPreviewImage.getPaddingTop();
        margins.right = this.mPreviewImage.getPaddingRight();
        margins.bottom = this.mPreviewImage.getPaddingBottom();
        if (this.mOverlayPosition == 0) {
            measureFloating(v, margins, out);
        } else {
            measureViewToSide(v, this.mThumbImage, margins, out);
        }
    }

    private void measureViewToSide(View view, View adjacent, Rect margins, Rect out) {
        int marginLeft;
        int marginTop;
        int marginRight;
        int maxWidth;
        int left;
        int right;
        if (margins == null) {
            marginLeft = 0;
            marginTop = 0;
            marginRight = 0;
        } else {
            marginLeft = margins.left;
            marginTop = margins.top;
            marginRight = margins.right;
        }
        Rect container = this.mContainerRect;
        int containerWidth = container.width();
        if (adjacent == null) {
            maxWidth = containerWidth;
        } else if (this.mLayoutFromRight) {
            maxWidth = adjacent.getLeft();
        } else {
            int maxWidth2 = adjacent.getRight();
            maxWidth = containerWidth - maxWidth2;
        }
        int adjMaxHeight = Math.max(0, container.height());
        int adjMaxWidth = Math.max(0, (maxWidth - marginLeft) - marginRight);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(adjMaxWidth, Integer.MIN_VALUE);
        int heightMeasureSpec = View.MeasureSpec.makeSafeMeasureSpec(adjMaxHeight, 0);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(adjMaxWidth, view.getMeasuredWidth());
        if (this.mLayoutFromRight) {
            right = (adjacent == null ? container.right : adjacent.getLeft()) - marginRight;
            left = right - width;
        } else {
            left = (adjacent == null ? container.left : adjacent.getRight()) + marginLeft;
            right = left + width;
        }
        int top = marginTop;
        int bottom = top + view.getMeasuredHeight();
        out.set(left, top, right, bottom);
    }

    private void measureFloating(View preview, Rect margins, Rect out) {
        int marginLeft;
        int marginTop;
        int marginRight;
        if (margins == null) {
            marginLeft = 0;
            marginTop = 0;
            marginRight = 0;
        } else {
            marginLeft = margins.left;
            marginTop = margins.top;
            marginRight = margins.right;
        }
        Rect container = this.mContainerRect;
        int containerWidth = container.width();
        int adjMaxHeight = Math.max(0, container.height());
        int adjMaxWidth = Math.max(0, (containerWidth - marginLeft) - marginRight);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(adjMaxWidth, Integer.MIN_VALUE);
        int heightMeasureSpec = View.MeasureSpec.makeSafeMeasureSpec(adjMaxHeight, 0);
        preview.measure(widthMeasureSpec, heightMeasureSpec);
        int containerHeight = container.height();
        int width = preview.getMeasuredWidth();
        int top = (containerHeight / 10) + marginTop + container.top;
        int bottom = preview.getMeasuredHeight() + top;
        int left = ((containerWidth - width) / 2) + container.left;
        int marginLeft2 = left + width;
        out.set(left, top, marginLeft2, bottom);
    }

    private void updateContainerRect() {
        AbsListView list = this.mList;
        list.resolvePadding();
        Rect container = this.mContainerRect;
        container.left = 0;
        container.top = 0;
        container.right = list.getWidth();
        container.bottom = list.getHeight();
        int scrollbarStyle = this.mScrollBarStyle;
        if (scrollbarStyle == 16777216 || scrollbarStyle == 0) {
            container.left += list.getPaddingLeft();
            container.top += list.getPaddingTop();
            container.right -= list.getPaddingRight();
            container.bottom -= list.getPaddingBottom();
            if (scrollbarStyle == 16777216) {
                int width = getWidth();
                if (this.mScrollbarPosition == 2) {
                    container.right += width;
                } else {
                    container.left -= width;
                }
            }
        }
    }

    private void layoutThumb() {
        Rect bounds = this.mTempBounds;
        measureViewToSide(this.mThumbImage, null, null, bounds);
        applyLayout(this.mThumbImage, bounds);
    }

    private void layoutTrack() {
        int thumbHalfHeight;
        int top;
        View track = this.mTrackImage;
        View thumb = this.mThumbImage;
        Rect container = this.mContainerRect;
        int maxWidth = Math.max(0, container.width());
        int maxHeight = Math.max(0, container.height());
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxWidth, Integer.MIN_VALUE);
        int heightMeasureSpec = View.MeasureSpec.makeSafeMeasureSpec(maxHeight, 0);
        track.measure(widthMeasureSpec, heightMeasureSpec);
        if (this.mThumbPosition == 1) {
            thumbHalfHeight = container.top;
            top = container.bottom;
        } else {
            int top2 = thumb.getHeight();
            int thumbHalfHeight2 = top2 / 2;
            int top3 = container.top + thumbHalfHeight2;
            int i = container.bottom - thumbHalfHeight2;
            thumbHalfHeight = top3;
            top = i;
        }
        int trackWidth = track.getMeasuredWidth();
        int left = thumb.getLeft() + ((thumb.getWidth() - trackWidth) / 2);
        int right = left + trackWidth;
        track.layout(left, thumbHalfHeight, right, top);
    }

    private void updateOffsetAndRange() {
        float min;
        float max;
        View trackImage = this.mTrackImage;
        View thumbImage = this.mThumbImage;
        if (this.mThumbPosition == 1) {
            float halfThumbHeight = thumbImage.getHeight() / 2.0f;
            min = trackImage.getTop() + halfThumbHeight;
            max = trackImage.getBottom() - halfThumbHeight;
        } else {
            min = trackImage.getTop();
            max = trackImage.getBottom();
        }
        this.mThumbOffset = min;
        this.mThumbRange = max - min;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setState(int state) {
        this.mList.removeCallbacks(this.mDeferHide);
        if (this.mAlwaysShow && state == 0) {
            state = 1;
        }
        if (state == this.mState) {
            return;
        }
        switch (state) {
            case 0:
                transitionToHidden();
                break;
            case 1:
                transitionToVisible();
                break;
            case 2:
                if (transitionPreviewLayout(this.mCurrentSection)) {
                    transitionToDragging();
                    break;
                } else {
                    transitionToVisible();
                    break;
                }
        }
        this.mState = state;
        refreshDrawablePressedState();
    }

    private void refreshDrawablePressedState() {
        boolean isPressed = this.mState == 2;
        this.mThumbImage.setPressed(isPressed);
        this.mTrackImage.setPressed(isPressed);
    }

    private void transitionToHidden() {
        AnimatorSet animatorSet = this.mDecorAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        Animator fadeOut = groupAnimatorOfFloat(View.ALPHA, 0.0f, this.mThumbImage, this.mTrackImage, this.mPreviewImage, this.mPrimaryText, this.mSecondaryText).setDuration(300L);
        float offset = this.mLayoutFromRight ? this.mThumbImage.getWidth() : -this.mThumbImage.getWidth();
        Animator slideOut = groupAnimatorOfFloat(View.TRANSLATION_X, offset, this.mThumbImage, this.mTrackImage).setDuration(300L);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mDecorAnimation = animatorSet2;
        animatorSet2.playTogether(fadeOut, slideOut);
        this.mDecorAnimation.start();
        this.mShowingPreview = false;
    }

    private void transitionToVisible() {
        AnimatorSet animatorSet = this.mDecorAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        Animator fadeIn = groupAnimatorOfFloat(View.ALPHA, 1.0f, this.mThumbImage, this.mTrackImage).setDuration(150L);
        Animator fadeOut = groupAnimatorOfFloat(View.ALPHA, 0.0f, this.mPreviewImage, this.mPrimaryText, this.mSecondaryText).setDuration(300L);
        Animator slideIn = groupAnimatorOfFloat(View.TRANSLATION_X, 0.0f, this.mThumbImage, this.mTrackImage).setDuration(150L);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mDecorAnimation = animatorSet2;
        animatorSet2.playTogether(fadeIn, fadeOut, slideIn);
        this.mDecorAnimation.start();
        this.mShowingPreview = false;
    }

    private void transitionToDragging() {
        AnimatorSet animatorSet = this.mDecorAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        Animator fadeIn = groupAnimatorOfFloat(View.ALPHA, 1.0f, this.mThumbImage, this.mTrackImage, this.mPreviewImage).setDuration(150L);
        Animator slideIn = groupAnimatorOfFloat(View.TRANSLATION_X, 0.0f, this.mThumbImage, this.mTrackImage).setDuration(150L);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mDecorAnimation = animatorSet2;
        animatorSet2.playTogether(fadeIn, slideIn);
        this.mDecorAnimation.start();
        this.mShowingPreview = true;
    }

    private void postAutoHide() {
        this.mList.removeCallbacks(this.mDeferHide);
        this.mList.postDelayed(this.mDeferHide, FADE_TIMEOUT);
    }

    public void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!isEnabled()) {
            setState(0);
            return;
        }
        boolean hasMoreItems = totalItemCount - visibleItemCount > 0;
        if (hasMoreItems && this.mState != 2) {
            setThumbPos(getPosFromItemCount(firstVisibleItem, visibleItemCount, totalItemCount));
        }
        this.mScrollCompleted = true;
        if (this.mFirstVisibleItem != firstVisibleItem) {
            this.mFirstVisibleItem = firstVisibleItem;
            if (this.mState != 2) {
                setState(1);
                postAutoHide();
            }
        }
    }

    private void getSectionsFromIndexer() {
        this.mSectionIndexer = null;
        ListAdapter adapter = this.mList.getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            this.mHeaderCount = ((HeaderViewListAdapter) adapter).getHeadersCount();
            adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        }
        if (adapter instanceof ExpandableListConnector) {
            ExpandableListAdapter expAdapter = ((ExpandableListConnector) adapter).getAdapter();
            if (expAdapter instanceof SectionIndexer) {
                SectionIndexer sectionIndexer = (SectionIndexer) expAdapter;
                this.mSectionIndexer = sectionIndexer;
                this.mListAdapter = adapter;
                this.mSections = sectionIndexer.getSections();
            }
        } else if (adapter instanceof SectionIndexer) {
            this.mListAdapter = adapter;
            SectionIndexer sectionIndexer2 = (SectionIndexer) adapter;
            this.mSectionIndexer = sectionIndexer2;
            this.mSections = sectionIndexer2.getSections();
        } else {
            this.mListAdapter = adapter;
            this.mSections = null;
        }
    }

    public void onSectionsChanged() {
        this.mListAdapter = null;
    }

    private void scrollTo(float position) {
        int sectionIndex;
        int targetIndex;
        this.mScrollCompleted = false;
        int count = this.mList.getCount();
        Object[] sections = this.mSections;
        int sectionCount = sections == null ? 0 : sections.length;
        if (sections != null && sectionCount > 1) {
            int exactSection = MathUtils.constrain((int) (sectionCount * position), 0, sectionCount - 1);
            int targetSection = exactSection;
            int targetIndex2 = this.mSectionIndexer.getPositionForSection(targetSection);
            sectionIndex = targetSection;
            int nextIndex = count;
            int prevIndex = targetIndex2;
            int prevSection = targetSection;
            int nextSection = targetSection + 1;
            if (targetSection < sectionCount - 1) {
                nextIndex = this.mSectionIndexer.getPositionForSection(targetSection + 1);
            }
            if (nextIndex == targetIndex2) {
                while (true) {
                    if (targetSection > 0) {
                        targetSection--;
                        prevIndex = this.mSectionIndexer.getPositionForSection(targetSection);
                        if (prevIndex != targetIndex2) {
                            prevSection = targetSection;
                            sectionIndex = targetSection;
                            break;
                        } else if (targetSection == 0) {
                            sectionIndex = 0;
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            int nextNextSection = nextSection + 1;
            while (nextNextSection < sectionCount && this.mSectionIndexer.getPositionForSection(nextNextSection) == nextIndex) {
                nextNextSection++;
                nextSection++;
            }
            float prevPosition = prevSection / sectionCount;
            float nextPosition = nextSection / sectionCount;
            float snapThreshold = count == 0 ? Float.MAX_VALUE : 0.125f / count;
            if (prevSection == exactSection && position - prevPosition < snapThreshold) {
                targetIndex = prevIndex;
            } else {
                targetIndex = ((int) (((nextIndex - prevIndex) * (position - prevPosition)) / (nextPosition - prevPosition))) + prevIndex;
            }
            int targetIndex3 = MathUtils.constrain(targetIndex, 0, count - 1);
            AbsListView absListView = this.mList;
            if (absListView instanceof ExpandableListView) {
                ExpandableListView expList = (ExpandableListView) absListView;
                expList.setSelectionFromTop(expList.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(this.mHeaderCount + targetIndex3)), 0);
            } else if (absListView instanceof ListView) {
                ((ListView) absListView).setSelectionFromTop(this.mHeaderCount + targetIndex3, 0);
            } else {
                absListView.setSelection(this.mHeaderCount + targetIndex3);
            }
        } else {
            int index = MathUtils.constrain((int) (count * position), 0, count - 1);
            AbsListView absListView2 = this.mList;
            if (absListView2 instanceof ExpandableListView) {
                ExpandableListView expList2 = (ExpandableListView) absListView2;
                expList2.setSelectionFromTop(expList2.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(this.mHeaderCount + index)), 0);
            } else if (absListView2 instanceof ListView) {
                ((ListView) absListView2).setSelectionFromTop(this.mHeaderCount + index, 0);
            } else {
                absListView2.setSelection(this.mHeaderCount + index);
            }
            sectionIndex = -1;
        }
        if (this.mCurrentSection != sectionIndex) {
            this.mCurrentSection = sectionIndex;
            boolean hasPreview = transitionPreviewLayout(sectionIndex);
            boolean z = this.mShowingPreview;
            if (!z && hasPreview) {
                transitionToDragging();
            } else if (z && !hasPreview) {
                transitionToVisible();
            }
        }
    }

    private boolean transitionPreviewLayout(int sectionIndex) {
        TextView showing;
        TextView target;
        Object section;
        Object[] sections = this.mSections;
        String text = null;
        if (sections != null && sectionIndex >= 0 && sectionIndex < sections.length && (section = sections[sectionIndex]) != null) {
            text = section.toString();
        }
        Rect bounds = this.mTempBounds;
        View preview = this.mPreviewImage;
        if (this.mShowingPrimary) {
            showing = this.mPrimaryText;
            target = this.mSecondaryText;
        } else {
            showing = this.mSecondaryText;
            target = this.mPrimaryText;
        }
        target.setText(text);
        measurePreview(target, bounds);
        applyLayout(target, bounds);
        AnimatorSet animatorSet = this.mPreviewAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        Animator showTarget = animateAlpha(target, 1.0f).setDuration(50L);
        Animator hideShowing = animateAlpha(showing, 0.0f).setDuration(50L);
        hideShowing.addListener(this.mSwitchPrimaryListener);
        bounds.left -= preview.getPaddingLeft();
        bounds.top -= preview.getPaddingTop();
        bounds.right += preview.getPaddingRight();
        bounds.bottom += preview.getPaddingBottom();
        Animator resizePreview = animateBounds(preview, bounds);
        resizePreview.setDuration(100L);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mPreviewAnimation = animatorSet2;
        AnimatorSet.Builder builder = animatorSet2.play(hideShowing).with(showTarget);
        builder.with(resizePreview);
        int previewWidth = (preview.getWidth() - preview.getPaddingLeft()) - preview.getPaddingRight();
        int targetWidth = target.getWidth();
        if (targetWidth <= previewWidth) {
            target.setScaleX(1.0f);
        } else {
            target.setScaleX(previewWidth / targetWidth);
            Animator scaleAnim = animateScaleX(target, 1.0f).setDuration(100L);
            builder.with(scaleAnim);
        }
        int showingWidth = showing.getWidth();
        if (showingWidth > targetWidth) {
            float scale = targetWidth / showingWidth;
            Animator scaleAnim2 = animateScaleX(showing, scale).setDuration(100L);
            builder.with(scaleAnim2);
        }
        this.mPreviewAnimation.start();
        return !TextUtils.isEmpty(text);
    }

    private void setThumbPos(float position) {
        float previewPos;
        float thumbMiddle = (this.mThumbRange * position) + this.mThumbOffset;
        ImageView imageView = this.mThumbImage;
        imageView.setTranslationY(thumbMiddle - (imageView.getHeight() / 2.0f));
        View previewImage = this.mPreviewImage;
        float previewHalfHeight = previewImage.getHeight() / 2.0f;
        switch (this.mOverlayPosition) {
            case 1:
                previewPos = thumbMiddle;
                break;
            case 2:
                previewPos = thumbMiddle - previewHalfHeight;
                break;
            default:
                previewPos = 0.0f;
                break;
        }
        Rect container = this.mContainerRect;
        int top = container.top;
        int bottom = container.bottom;
        float minP = top + previewHalfHeight;
        float maxP = bottom - previewHalfHeight;
        float previewMiddle = MathUtils.constrain(previewPos, minP, maxP);
        float previewTop = previewMiddle - previewHalfHeight;
        previewImage.setTranslationY(previewTop);
        this.mPrimaryText.setTranslationY(previewTop);
        this.mSecondaryText.setTranslationY(previewTop);
    }

    private float getPosFromMotionEvent(float y) {
        float f = this.mThumbRange;
        if (f <= 0.0f) {
            return 0.0f;
        }
        return MathUtils.constrain((y - this.mThumbOffset) / f, 0.0f, 1.0f);
    }

    private float getPosFromItemCount(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        float incrementalPos;
        int nextSectionPos;
        float posWithinSection;
        int currentVisibleSize;
        int maxSize;
        int nextSectionPos2;
        Object[] objArr;
        SectionIndexer sectionIndexer = this.mSectionIndexer;
        if (sectionIndexer == null || this.mListAdapter == null) {
            getSectionsFromIndexer();
        }
        if (visibleItemCount == 0 || totalItemCount == 0) {
            return 0.0f;
        }
        boolean hasSections = (sectionIndexer == null || (objArr = this.mSections) == null || objArr.length <= 0) ? false : true;
        if (!hasSections || !this.mMatchDragPosition) {
            if (visibleItemCount == totalItemCount) {
                return 0.0f;
            }
            return firstVisibleItem / (totalItemCount - visibleItemCount);
        }
        int i = this.mHeaderCount;
        int firstVisibleItem2 = firstVisibleItem - i;
        if (firstVisibleItem2 < 0) {
            return 0.0f;
        }
        int totalItemCount2 = totalItemCount - i;
        View child = this.mList.getChildAt(0);
        if (child == null || child.getHeight() == 0) {
            incrementalPos = 0.0f;
        } else {
            incrementalPos = (this.mList.getPaddingTop() - child.getTop()) / child.getHeight();
        }
        int section = sectionIndexer.getSectionForPosition(firstVisibleItem2);
        int sectionPos = sectionIndexer.getPositionForSection(section);
        int sectionCount = this.mSections.length;
        if (section < sectionCount - 1) {
            if (section + 1 < sectionCount) {
                nextSectionPos2 = sectionIndexer.getPositionForSection(section + 1);
            } else {
                nextSectionPos2 = totalItemCount2 - 1;
            }
            nextSectionPos = nextSectionPos2 - sectionPos;
        } else {
            nextSectionPos = totalItemCount2 - sectionPos;
        }
        if (nextSectionPos == 0) {
            posWithinSection = 0.0f;
        } else {
            float posWithinSection2 = firstVisibleItem2;
            posWithinSection = ((posWithinSection2 + incrementalPos) - sectionPos) / nextSectionPos;
        }
        float result = (section + posWithinSection) / sectionCount;
        if (firstVisibleItem2 > 0 && firstVisibleItem2 + visibleItemCount == totalItemCount2) {
            View lastChild = this.mList.getChildAt(visibleItemCount - 1);
            int bottomPadding = this.mList.getPaddingBottom();
            if (this.mList.getClipToPadding()) {
                int maxSize2 = lastChild.getHeight();
                currentVisibleSize = (this.mList.getHeight() - bottomPadding) - lastChild.getTop();
                maxSize = maxSize2;
            } else {
                int currentVisibleSize2 = lastChild.getHeight();
                int maxSize3 = currentVisibleSize2 + bottomPadding;
                currentVisibleSize = this.mList.getHeight() - lastChild.getTop();
                maxSize = maxSize3;
            }
            if (currentVisibleSize > 0 && maxSize > 0) {
                return result + ((1.0f - result) * (currentVisibleSize / maxSize));
            }
            return result;
        }
        return result;
    }

    private void cancelFling() {
        MotionEvent cancelFling = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
        this.mList.onTouchEvent(cancelFling);
        cancelFling.recycle();
    }

    private void cancelPendingDrag() {
        this.mPendingDrag = -1L;
    }

    private void startPendingDrag() {
        this.mPendingDrag = SystemClock.uptimeMillis() + TAP_TIMEOUT;
    }

    private void beginDrag() {
        this.mPendingDrag = -1L;
        setState(2);
        if (this.mListAdapter == null && this.mList != null) {
            getSectionsFromIndexer();
        }
        AbsListView absListView = this.mList;
        if (absListView != null) {
            absListView.requestDisallowInterceptTouchEvent(true);
            this.mList.reportScrollStateChange(1);
        }
        cancelFling();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isEnabled()) {
            switch (ev.getActionMasked()) {
                case 0:
                    if (isPointInside(ev.getX(), ev.getY())) {
                        if (!this.mList.isInScrollingContainer()) {
                            return true;
                        }
                        this.mInitialTouchY = ev.getY();
                        startPendingDrag();
                        break;
                    }
                    break;
                case 1:
                case 3:
                    cancelPendingDrag();
                    break;
                case 2:
                    if (!isPointInside(ev.getX(), ev.getY())) {
                        cancelPendingDrag();
                        break;
                    } else {
                        long j = this.mPendingDrag;
                        if (j >= 0 && j <= SystemClock.uptimeMillis()) {
                            beginDrag();
                            float pos = getPosFromMotionEvent(this.mInitialTouchY);
                            scrollTo(pos);
                            return onTouchEvent(ev);
                        }
                    }
                    break;
            }
            return false;
        }
        return false;
    }

    public boolean onInterceptHoverEvent(MotionEvent ev) {
        if (isEnabled()) {
            int actionMasked = ev.getActionMasked();
            if ((actionMasked == 9 || actionMasked == 7) && this.mState == 0 && isPointInside(ev.getX(), ev.getY())) {
                setState(1);
                postAutoHide();
            }
            return false;
        }
        return false;
    }

    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        if (this.mState == 2 || isPointInside(event.getX(), event.getY())) {
            return PointerIcon.getSystemIcon(this.mList.getContext(), 1000);
        }
        return null;
    }

    public boolean onTouchEvent(MotionEvent me) {
        if (isEnabled()) {
            switch (me.getActionMasked()) {
                case 0:
                    if (isPointInside(me.getX(), me.getY()) && !this.mList.isInScrollingContainer()) {
                        beginDrag();
                        return true;
                    }
                    break;
                case 1:
                    if (this.mPendingDrag >= 0) {
                        beginDrag();
                        float pos = getPosFromMotionEvent(me.getY());
                        setThumbPos(pos);
                        scrollTo(pos);
                    }
                    if (this.mState == 2) {
                        AbsListView absListView = this.mList;
                        if (absListView != null) {
                            absListView.requestDisallowInterceptTouchEvent(false);
                            this.mList.reportScrollStateChange(0);
                        }
                        setState(1);
                        postAutoHide();
                        return true;
                    }
                    break;
                case 2:
                    if (this.mPendingDrag >= 0 && Math.abs(me.getY() - this.mInitialTouchY) > this.mScaledTouchSlop) {
                        beginDrag();
                    }
                    if (this.mState == 2) {
                        float pos2 = getPosFromMotionEvent(me.getY());
                        setThumbPos(pos2);
                        if (this.mScrollCompleted) {
                            scrollTo(pos2);
                        }
                        return true;
                    }
                    break;
                case 3:
                    cancelPendingDrag();
                    break;
            }
            return false;
        }
        return false;
    }

    private boolean isPointInside(float x, float y) {
        return isPointInsideX(x) && (this.mTrackDrawable != null || isPointInsideY(y));
    }

    private boolean isPointInsideX(float x) {
        float offset = this.mThumbImage.getTranslationX();
        float left = this.mThumbImage.getLeft() + offset;
        float right = this.mThumbImage.getRight() + offset;
        float targetSizeDiff = this.mMinimumTouchTarget - (right - left);
        float adjust = targetSizeDiff > 0.0f ? targetSizeDiff : 0.0f;
        return this.mLayoutFromRight ? x >= ((float) this.mThumbImage.getLeft()) - adjust : x <= ((float) this.mThumbImage.getRight()) + adjust;
    }

    private boolean isPointInsideY(float y) {
        float offset = this.mThumbImage.getTranslationY();
        float top = this.mThumbImage.getTop() + offset;
        float bottom = this.mThumbImage.getBottom() + offset;
        float targetSizeDiff = this.mMinimumTouchTarget - (bottom - top);
        float adjust = targetSizeDiff > 0.0f ? targetSizeDiff / 2.0f : 0.0f;
        return y >= top - adjust && y <= bottom + adjust;
    }

    private static Animator groupAnimatorOfFloat(Property<View, Float> property, float value, View... views) {
        AnimatorSet animSet = new AnimatorSet();
        AnimatorSet.Builder builder = null;
        for (int i = views.length - 1; i >= 0; i--) {
            Animator anim = ObjectAnimator.ofFloat(views[i], property, value);
            if (builder == null) {
                builder = animSet.play(anim);
            } else {
                builder.with(anim);
            }
        }
        return animSet;
    }

    private static Animator animateScaleX(View v, float target) {
        return ObjectAnimator.ofFloat(v, View.SCALE_X, target);
    }

    private static Animator animateAlpha(View v, float alpha) {
        return ObjectAnimator.ofFloat(v, View.ALPHA, alpha);
    }

    private static Animator animateBounds(View v, Rect bounds) {
        PropertyValuesHolder left = PropertyValuesHolder.ofInt(LEFT, bounds.left);
        PropertyValuesHolder top = PropertyValuesHolder.ofInt(TOP, bounds.top);
        PropertyValuesHolder right = PropertyValuesHolder.ofInt(RIGHT, bounds.right);
        PropertyValuesHolder bottom = PropertyValuesHolder.ofInt(BOTTOM, bounds.bottom);
        return ObjectAnimator.ofPropertyValuesHolder(v, left, top, right, bottom);
    }
}
