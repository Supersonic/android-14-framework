package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.p008os.IBinder;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.window.WindowOnBackInvokedDispatcher;
import com.android.internal.C4057R;
import java.lang.ref.WeakReference;
import java.util.List;
/* loaded from: classes4.dex */
public class PopupWindow {
    private static final int[] ABOVE_ANCHOR_STATE_SET = {16842922};
    private static final int ANIMATION_STYLE_DEFAULT = -1;
    private static final int DEFAULT_ANCHORED_GRAVITY = 8388659;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED = 1;
    public static final int INPUT_METHOD_NOT_NEEDED = 2;
    private boolean mAboveAnchor;
    private Drawable mAboveAnchorBackgroundDrawable;
    private boolean mAllowScrollingAnchorParent;
    private WeakReference<View> mAnchor;
    private WeakReference<View> mAnchorRoot;
    private int mAnchorXoff;
    private int mAnchorYoff;
    private int mAnchoredGravity;
    private int mAnimationStyle;
    private boolean mAttachedInDecor;
    private boolean mAttachedInDecorSet;
    private OnBackInvokedCallback mBackCallback;
    private Drawable mBackground;
    private View mBackgroundView;
    private Drawable mBelowAnchorBackgroundDrawable;
    private boolean mClipToScreen;
    private boolean mClippingEnabled;
    private View mContentView;
    private Context mContext;
    private PopupDecorView mDecorView;
    private float mElevation;
    private Transition mEnterTransition;
    private Rect mEpicenterBounds;
    private Transition mExitTransition;
    private boolean mFocusable;
    private int mGravity;
    private int mHeight;
    private int mHeightMode;
    private boolean mIgnoreCheekPress;
    private int mInputMethodMode;
    private boolean mIsAnchorRootAttached;
    private boolean mIsDropdown;
    private boolean mIsShowing;
    private boolean mIsTransitioningToDismiss;
    private int mLastHeight;
    private int mLastWidth;
    private boolean mLayoutInScreen;
    private boolean mLayoutInsetDecor;
    private boolean mNotTouchModal;
    private final View.OnAttachStateChangeListener mOnAnchorDetachedListener;
    private final View.OnAttachStateChangeListener mOnAnchorRootDetachedListener;
    private OnDismissListener mOnDismissListener;
    private final View.OnLayoutChangeListener mOnLayoutChangeListener;
    private final ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
    private boolean mOutsideTouchable;
    private boolean mOverlapAnchor;
    private WeakReference<View> mParentRootView;
    private boolean mPopupViewInitialLayoutDirectionInherited;
    private int mSoftInputMode;
    private int mSplitTouchEnabled;
    private final Rect mTempRect;
    private final int[] mTmpAppLocation;
    private final int[] mTmpDrawingLocation;
    private final int[] mTmpScreenLocation;
    private View.OnTouchListener mTouchInterceptor;
    private boolean mTouchable;
    private int mWidth;
    private int mWidthMode;
    private int mWindowLayoutType;
    private WindowManager mWindowManager;

    /* loaded from: classes4.dex */
    public interface OnDismissListener {
        void onDismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        alignToAnchor();
    }

    public PopupWindow(Context context) {
        this(context, (AttributeSet) null);
    }

    public PopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 16842870);
    }

    public PopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Transition exitTransition;
        this.mTmpDrawingLocation = new int[2];
        this.mTmpScreenLocation = new int[2];
        this.mTmpAppLocation = new int[2];
        this.mTempRect = new Rect();
        this.mInputMethodMode = 0;
        this.mSoftInputMode = 1;
        this.mTouchable = true;
        this.mOutsideTouchable = false;
        this.mClippingEnabled = true;
        this.mSplitTouchEnabled = -1;
        this.mAllowScrollingAnchorParent = true;
        this.mLayoutInsetDecor = false;
        this.mAttachedInDecor = true;
        this.mAttachedInDecorSet = false;
        this.mWidth = -2;
        this.mHeight = -2;
        this.mWindowLayoutType = 1000;
        this.mIgnoreCheekPress = false;
        this.mAnimationStyle = -1;
        this.mGravity = 0;
        this.mOnAnchorDetachedListener = new View.OnAttachStateChangeListener() { // from class: android.widget.PopupWindow.1
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
                PopupWindow.this.alignToAnchor();
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
            }
        };
        this.mOnAnchorRootDetachedListener = new View.OnAttachStateChangeListener() { // from class: android.widget.PopupWindow.2
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
                PopupWindow.this.mIsAnchorRootAttached = false;
            }
        };
        this.mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.widget.PopupWindow$$ExternalSyntheticLambda0
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public final void onScrollChanged() {
                PopupWindow.this.alignToAnchor();
            }
        };
        this.mOnLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: android.widget.PopupWindow$$ExternalSyntheticLambda1
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                PopupWindow.this.lambda$new$0(view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        };
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.PopupWindow, defStyleAttr, defStyleRes);
        Drawable bg = a.getDrawable(0);
        this.mElevation = a.getDimension(3, 0.0f);
        this.mOverlapAnchor = a.getBoolean(2, false);
        if (a.hasValueOrEmpty(1)) {
            int animStyle = a.getResourceId(1, 0);
            if (animStyle == 16974594) {
                this.mAnimationStyle = -1;
            } else {
                this.mAnimationStyle = animStyle;
            }
        } else {
            this.mAnimationStyle = -1;
        }
        Transition enterTransition = getTransition(a.getResourceId(4, 0));
        if (a.hasValueOrEmpty(5)) {
            exitTransition = getTransition(a.getResourceId(5, 0));
        } else {
            exitTransition = enterTransition == null ? null : enterTransition.mo4799clone();
        }
        a.recycle();
        setEnterTransition(enterTransition);
        setExitTransition(exitTransition);
        setBackgroundDrawable(bg);
    }

    public PopupWindow() {
        this((View) null, 0, 0);
    }

    public PopupWindow(View contentView) {
        this(contentView, 0, 0);
    }

    public PopupWindow(int width, int height) {
        this((View) null, width, height);
    }

    public PopupWindow(View contentView, int width, int height) {
        this(contentView, width, height, false);
    }

    public PopupWindow(View contentView, int width, int height, boolean focusable) {
        this.mTmpDrawingLocation = new int[2];
        this.mTmpScreenLocation = new int[2];
        this.mTmpAppLocation = new int[2];
        this.mTempRect = new Rect();
        this.mInputMethodMode = 0;
        this.mSoftInputMode = 1;
        this.mTouchable = true;
        this.mOutsideTouchable = false;
        this.mClippingEnabled = true;
        this.mSplitTouchEnabled = -1;
        this.mAllowScrollingAnchorParent = true;
        this.mLayoutInsetDecor = false;
        this.mAttachedInDecor = true;
        this.mAttachedInDecorSet = false;
        this.mWidth = -2;
        this.mHeight = -2;
        this.mWindowLayoutType = 1000;
        this.mIgnoreCheekPress = false;
        this.mAnimationStyle = -1;
        this.mGravity = 0;
        this.mOnAnchorDetachedListener = new View.OnAttachStateChangeListener() { // from class: android.widget.PopupWindow.1
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
                PopupWindow.this.alignToAnchor();
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
            }
        };
        this.mOnAnchorRootDetachedListener = new View.OnAttachStateChangeListener() { // from class: android.widget.PopupWindow.2
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
                PopupWindow.this.mIsAnchorRootAttached = false;
            }
        };
        this.mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.widget.PopupWindow$$ExternalSyntheticLambda0
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public final void onScrollChanged() {
                PopupWindow.this.alignToAnchor();
            }
        };
        this.mOnLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: android.widget.PopupWindow$$ExternalSyntheticLambda1
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                PopupWindow.this.lambda$new$0(view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        };
        if (contentView != null) {
            Context context = contentView.getContext();
            this.mContext = context;
            this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        setContentView(contentView);
        setWidth(width);
        setHeight(height);
        setFocusable(focusable);
    }

    public void setEnterTransition(Transition enterTransition) {
        this.mEnterTransition = enterTransition;
    }

    public Transition getEnterTransition() {
        return this.mEnterTransition;
    }

    public void setExitTransition(Transition exitTransition) {
        this.mExitTransition = exitTransition;
    }

    public Transition getExitTransition() {
        return this.mExitTransition;
    }

    public Rect getEpicenterBounds() {
        if (this.mEpicenterBounds != null) {
            return new Rect(this.mEpicenterBounds);
        }
        return null;
    }

    public void setEpicenterBounds(Rect bounds) {
        this.mEpicenterBounds = bounds != null ? new Rect(bounds) : null;
    }

    private Transition getTransition(int resId) {
        if (resId != 0 && resId != 17760256) {
            TransitionInflater inflater = TransitionInflater.from(this.mContext);
            Transition transition = inflater.inflateTransition(resId);
            if (transition != null) {
                boolean isEmpty = (transition instanceof TransitionSet) && ((TransitionSet) transition).getTransitionCount() == 0;
                if (!isEmpty) {
                    return transition;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public Drawable getBackground() {
        return this.mBackground;
    }

    public void setBackgroundDrawable(Drawable background) {
        this.mBackground = background;
        if (background instanceof StateListDrawable) {
            StateListDrawable stateList = (StateListDrawable) background;
            int aboveAnchorStateIndex = stateList.findStateDrawableIndex(ABOVE_ANCHOR_STATE_SET);
            int count = stateList.getStateCount();
            int belowAnchorStateIndex = -1;
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                } else if (i == aboveAnchorStateIndex) {
                    i++;
                } else {
                    belowAnchorStateIndex = i;
                    break;
                }
            }
            if (aboveAnchorStateIndex != -1 && belowAnchorStateIndex != -1) {
                this.mAboveAnchorBackgroundDrawable = stateList.getStateDrawable(aboveAnchorStateIndex);
                this.mBelowAnchorBackgroundDrawable = stateList.getStateDrawable(belowAnchorStateIndex);
                return;
            }
            this.mBelowAnchorBackgroundDrawable = null;
            this.mAboveAnchorBackgroundDrawable = null;
        }
    }

    public float getElevation() {
        return this.mElevation;
    }

    public void setElevation(float elevation) {
        this.mElevation = elevation;
    }

    public int getAnimationStyle() {
        return this.mAnimationStyle;
    }

    public void setIgnoreCheekPress() {
        this.mIgnoreCheekPress = true;
    }

    public void setAnimationStyle(int animationStyle) {
        this.mAnimationStyle = animationStyle;
    }

    public View getContentView() {
        return this.mContentView;
    }

    public void setContentView(View contentView) {
        if (isShowing()) {
            return;
        }
        this.mContentView = contentView;
        if (this.mContext == null && contentView != null) {
            this.mContext = contentView.getContext();
        }
        if (this.mWindowManager == null && this.mContentView != null) {
            this.mWindowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        Context context = this.mContext;
        if (context != null && !this.mAttachedInDecorSet) {
            setAttachedInDecor(context.getApplicationInfo().targetSdkVersion >= 22);
        }
    }

    public void setTouchInterceptor(View.OnTouchListener l) {
        this.mTouchInterceptor = l;
    }

    public boolean isFocusable() {
        return this.mFocusable;
    }

    public void setFocusable(boolean focusable) {
        this.mFocusable = focusable;
    }

    public int getInputMethodMode() {
        return this.mInputMethodMode;
    }

    public void setInputMethodMode(int mode) {
        this.mInputMethodMode = mode;
    }

    public void setSoftInputMode(int mode) {
        this.mSoftInputMode = mode;
    }

    public int getSoftInputMode() {
        return this.mSoftInputMode;
    }

    public boolean isTouchable() {
        return this.mTouchable;
    }

    public void setTouchable(boolean touchable) {
        this.mTouchable = touchable;
    }

    public boolean isOutsideTouchable() {
        return this.mOutsideTouchable;
    }

    public void setOutsideTouchable(boolean touchable) {
        this.mOutsideTouchable = touchable;
    }

    public boolean isClippingEnabled() {
        return this.mClippingEnabled;
    }

    public void setClippingEnabled(boolean enabled) {
        this.mClippingEnabled = enabled;
    }

    @Deprecated
    public boolean isClipToScreenEnabled() {
        return this.mClipToScreen;
    }

    @Deprecated
    public void setClipToScreenEnabled(boolean enabled) {
        this.mClipToScreen = enabled;
    }

    public boolean isClippedToScreen() {
        return this.mClipToScreen;
    }

    public void setIsClippedToScreen(boolean enabled) {
        this.mClipToScreen = enabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAllowScrollingAnchorParent(boolean enabled) {
        this.mAllowScrollingAnchorParent = enabled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean getAllowScrollingAnchorParent() {
        return this.mAllowScrollingAnchorParent;
    }

    public boolean isSplitTouchEnabled() {
        Context context;
        int i = this.mSplitTouchEnabled;
        return (i >= 0 || (context = this.mContext) == null) ? i == 1 : context.getApplicationInfo().targetSdkVersion >= 11;
    }

    public void setSplitTouchEnabled(boolean enabled) {
        this.mSplitTouchEnabled = enabled ? 1 : 0;
    }

    @Deprecated
    public boolean isLayoutInScreenEnabled() {
        return this.mLayoutInScreen;
    }

    @Deprecated
    public void setLayoutInScreenEnabled(boolean enabled) {
        this.mLayoutInScreen = enabled;
    }

    public boolean isLaidOutInScreen() {
        return this.mLayoutInScreen;
    }

    public void setIsLaidOutInScreen(boolean enabled) {
        this.mLayoutInScreen = enabled;
    }

    public boolean isAttachedInDecor() {
        return this.mAttachedInDecor;
    }

    public void setAttachedInDecor(boolean enabled) {
        this.mAttachedInDecor = enabled;
        this.mAttachedInDecorSet = true;
    }

    public void setLayoutInsetDecor(boolean enabled) {
        this.mLayoutInsetDecor = enabled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isLayoutInsetDecor() {
        return this.mLayoutInsetDecor;
    }

    public void setWindowLayoutType(int layoutType) {
        this.mWindowLayoutType = layoutType;
    }

    public int getWindowLayoutType() {
        return this.mWindowLayoutType;
    }

    public boolean isTouchModal() {
        return !this.mNotTouchModal;
    }

    public void setTouchModal(boolean touchModal) {
        this.mNotTouchModal = !touchModal;
    }

    @Deprecated
    public void setWindowLayoutMode(int widthSpec, int heightSpec) {
        this.mWidthMode = widthSpec;
        this.mHeightMode = heightSpec;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public void setOverlapAnchor(boolean overlapAnchor) {
        this.mOverlapAnchor = overlapAnchor;
    }

    public boolean getOverlapAnchor() {
        return this.mOverlapAnchor;
    }

    public boolean isShowing() {
        return this.mIsShowing;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setShowing(boolean isShowing) {
        this.mIsShowing = isShowing;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setDropDown(boolean isDropDown) {
        this.mIsDropdown = isDropDown;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setTransitioningToDismiss(boolean transitioningToDismiss) {
        this.mIsTransitioningToDismiss = transitioningToDismiss;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isTransitioningToDismiss() {
        return this.mIsTransitioningToDismiss;
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        this.mParentRootView = new WeakReference<>(parent.getRootView());
        showAtLocation(parent.getWindowToken(), gravity, x, y);
    }

    public void showAtLocation(IBinder token, int gravity, int x, int y) {
        if (isShowing() || this.mContentView == null) {
            return;
        }
        TransitionManager.endTransitions(this.mDecorView);
        detachFromAnchor();
        this.mIsShowing = true;
        this.mIsDropdown = false;
        this.mGravity = gravity;
        WindowManager.LayoutParams p = createPopupLayoutParams(token);
        preparePopup(p);
        p.f504x = x;
        p.f505y = y;
        invokePopup(p);
    }

    public void showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        showAsDropDown(anchor, xoff, yoff, DEFAULT_ANCHORED_GRAVITY);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (isShowing() || !hasContentView()) {
            return;
        }
        TransitionManager.endTransitions(this.mDecorView);
        attachToAnchor(anchor, xoff, yoff, gravity);
        this.mIsShowing = true;
        this.mIsDropdown = true;
        WindowManager.LayoutParams p = createPopupLayoutParams(anchor.getApplicationWindowToken());
        preparePopup(p);
        boolean aboveAnchor = findDropDownPosition(anchor, p, xoff, yoff, p.width, p.height, gravity, this.mAllowScrollingAnchorParent);
        updateAboveAnchor(aboveAnchor);
        p.accessibilityIdOfAnchor = anchor != null ? anchor.getAccessibilityViewId() : -1L;
        invokePopup(p);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void updateAboveAnchor(boolean aboveAnchor) {
        View view;
        if (aboveAnchor != this.mAboveAnchor) {
            this.mAboveAnchor = aboveAnchor;
            if (this.mBackground != null && (view = this.mBackgroundView) != null) {
                Drawable drawable = this.mAboveAnchorBackgroundDrawable;
                if (drawable != null) {
                    if (aboveAnchor) {
                        view.setBackground(drawable);
                        return;
                    } else {
                        view.setBackground(this.mBelowAnchorBackgroundDrawable);
                        return;
                    }
                }
                view.refreshDrawableState();
            }
        }
    }

    public boolean isAboveAnchor() {
        return this.mAboveAnchor;
    }

    private void preparePopup(WindowManager.LayoutParams p) {
        if (this.mContentView == null || this.mContext == null || this.mWindowManager == null) {
            throw new IllegalStateException("You must specify a valid content view by calling setContentView() before attempting to show the popup.");
        }
        if (p.accessibilityTitle == null) {
            p.accessibilityTitle = this.mContext.getString(C4057R.string.popup_window_default_title);
        }
        PopupDecorView popupDecorView = this.mDecorView;
        if (popupDecorView != null) {
            popupDecorView.cancelTransitions();
        }
        if (this.mBackground != null) {
            PopupBackgroundView createBackgroundView = createBackgroundView(this.mContentView);
            this.mBackgroundView = createBackgroundView;
            createBackgroundView.setBackground(this.mBackground);
        } else {
            this.mBackgroundView = this.mContentView;
        }
        PopupDecorView createDecorView = createDecorView(this.mBackgroundView);
        this.mDecorView = createDecorView;
        createDecorView.setIsRootNamespace(true);
        this.mBackgroundView.setElevation(this.mElevation);
        p.setSurfaceInsets(this.mBackgroundView, true, true);
        this.mPopupViewInitialLayoutDirectionInherited = this.mContentView.getRawLayoutDirection() == 2;
    }

    private PopupBackgroundView createBackgroundView(View contentView) {
        int height;
        ViewGroup.LayoutParams layoutParams = this.mContentView.getLayoutParams();
        if (layoutParams != null && layoutParams.height == -2) {
            height = -2;
        } else {
            height = -1;
        }
        PopupBackgroundView backgroundView = new PopupBackgroundView(this.mContext);
        FrameLayout.LayoutParams listParams = new FrameLayout.LayoutParams(-1, height);
        backgroundView.addView(contentView, listParams);
        return backgroundView;
    }

    private PopupDecorView createDecorView(View contentView) {
        int height;
        ViewGroup.LayoutParams layoutParams = this.mContentView.getLayoutParams();
        if (layoutParams != null && layoutParams.height == -2) {
            height = -2;
        } else {
            height = -1;
        }
        PopupDecorView decorView = new PopupDecorView(this.mContext);
        decorView.addView(contentView, -1, height);
        decorView.setClipChildren(false);
        decorView.setClipToPadding(false);
        return decorView;
    }

    private void invokePopup(WindowManager.LayoutParams p) {
        Context context = this.mContext;
        if (context != null) {
            p.packageName = context.getPackageName();
        }
        PopupDecorView decorView = this.mDecorView;
        decorView.setFitsSystemWindows(this.mLayoutInsetDecor);
        setLayoutDirectionFromAnchor();
        this.mWindowManager.addView(decorView, p);
        Transition transition = this.mEnterTransition;
        if (transition != null) {
            decorView.requestEnterTransition(transition);
        }
    }

    private void setLayoutDirectionFromAnchor() {
        View anchor;
        WeakReference<View> weakReference = this.mAnchor;
        if (weakReference != null && (anchor = weakReference.get()) != null && this.mPopupViewInitialLayoutDirectionInherited) {
            this.mDecorView.setLayoutDirection(anchor.getLayoutDirection());
        }
    }

    private int computeGravity() {
        int gravity = this.mGravity;
        if (gravity == 0) {
            gravity = DEFAULT_ANCHORED_GRAVITY;
        }
        if (this.mIsDropdown) {
            if (this.mClipToScreen || this.mClippingEnabled) {
                return gravity | 268435456;
            }
            return gravity;
        }
        return gravity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final WindowManager.LayoutParams createPopupLayoutParams(IBinder token) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.gravity = computeGravity();
        p.flags = computeFlags(p.flags);
        p.type = this.mWindowLayoutType;
        p.token = token;
        p.softInputMode = this.mSoftInputMode;
        p.windowAnimations = computeAnimationResource();
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            p.format = drawable.getOpacity();
        } else {
            p.format = -3;
        }
        int i = this.mHeightMode;
        if (i < 0) {
            this.mLastHeight = i;
            p.height = i;
        } else {
            int i2 = this.mHeight;
            this.mLastHeight = i2;
            p.height = i2;
        }
        int i3 = this.mWidthMode;
        if (i3 < 0) {
            this.mLastWidth = i3;
            p.width = i3;
        } else {
            int i4 = this.mWidth;
            this.mLastWidth = i4;
            p.width = i4;
        }
        p.privateFlags = 98304;
        p.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));
        return p;
    }

    private int computeFlags(int curFlags) {
        int curFlags2 = curFlags & (-8815129);
        if (this.mIgnoreCheekPress) {
            curFlags2 |= 32768;
        }
        if (!this.mFocusable) {
            curFlags2 |= 8;
            if (this.mInputMethodMode == 1) {
                curFlags2 |= 131072;
            }
        } else if (this.mInputMethodMode == 2) {
            curFlags2 |= 131072;
        }
        if (!this.mTouchable) {
            curFlags2 |= 16;
        }
        if (this.mOutsideTouchable) {
            curFlags2 |= 262144;
        }
        if (!this.mClippingEnabled || this.mClipToScreen) {
            curFlags2 |= 512;
        }
        if (isSplitTouchEnabled()) {
            curFlags2 |= 8388608;
        }
        if (this.mLayoutInScreen) {
            curFlags2 |= 256;
        }
        if (this.mLayoutInsetDecor) {
            curFlags2 |= 65536;
        }
        if (this.mNotTouchModal) {
            curFlags2 |= 32;
        }
        if (this.mAttachedInDecor) {
            return curFlags2 | 1073741824;
        }
        return curFlags2;
    }

    private int computeAnimationResource() {
        int i = this.mAnimationStyle;
        if (i == -1) {
            if (this.mIsDropdown) {
                if (this.mAboveAnchor) {
                    return C4057R.C4062style.Animation_DropDownUp;
                }
                return C4057R.C4062style.Animation_DropDownDown;
            }
            return 0;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r19v5 */
    /* JADX WARN: Type inference failed for: r19v6 */
    /* JADX WARN: Type inference failed for: r19v9 */
    public boolean findDropDownPosition(View anchor, WindowManager.LayoutParams outParams, int xOffset, int yOffset, int width, int height, int gravity, boolean allowScroll) {
        int yOffset2;
        int width2;
        int height2;
        WindowManager.LayoutParams layoutParams;
        int hgrav;
        boolean z;
        WindowManager.LayoutParams layoutParams2;
        ?? r19;
        int anchorHeight = anchor.getHeight();
        int anchorWidth = anchor.getWidth();
        if (!this.mOverlapAnchor) {
            yOffset2 = yOffset;
        } else {
            yOffset2 = yOffset - anchorHeight;
        }
        int[] appScreenLocation = this.mTmpAppLocation;
        View appRootView = getAppRootView(anchor);
        appRootView.getLocationOnScreen(appScreenLocation);
        int[] screenLocation = this.mTmpScreenLocation;
        anchor.getLocationOnScreen(screenLocation);
        int[] drawingLocation = this.mTmpDrawingLocation;
        drawingLocation[0] = screenLocation[0] - appScreenLocation[0];
        drawingLocation[1] = screenLocation[1] - appScreenLocation[1];
        outParams.f504x = drawingLocation[0] + xOffset;
        outParams.f505y = drawingLocation[1] + anchorHeight + yOffset2;
        Rect displayFrame = new Rect();
        appRootView.getWindowVisibleDisplayFrame(displayFrame);
        if (width != -1) {
            width2 = width;
        } else {
            width2 = displayFrame.right - displayFrame.left;
        }
        if (height != -1) {
            height2 = height;
        } else {
            height2 = displayFrame.bottom - displayFrame.top;
        }
        outParams.gravity = computeGravity();
        outParams.width = width2;
        outParams.height = height2;
        int hgrav2 = Gravity.getAbsoluteGravity(gravity, anchor.getLayoutDirection()) & 7;
        if (hgrav2 == 5) {
            outParams.f504x -= width2 - anchorWidth;
        }
        int height3 = height2;
        int width3 = width2;
        boolean fitsVertical = tryFitVertical(outParams, yOffset2, height3, anchorHeight, drawingLocation[1], screenLocation[1], displayFrame.top, displayFrame.bottom, false);
        boolean fitsHorizontal = tryFitHorizontal(outParams, xOffset, width3, anchorWidth, drawingLocation[0], screenLocation[0], displayFrame.left, displayFrame.right, false);
        if (fitsVertical && fitsHorizontal) {
            r19 = 1;
            layoutParams2 = outParams;
        } else {
            int scrollX = anchor.getScrollX();
            int scrollY = anchor.getScrollY();
            Rect r = new Rect(scrollX, scrollY, scrollX + width3 + xOffset, scrollY + height3 + anchorHeight + yOffset2);
            if (!allowScroll) {
                layoutParams = outParams;
                hgrav = hgrav2;
                z = true;
            } else if (!anchor.requestRectangleOnScreen(r, true)) {
                layoutParams = outParams;
                z = true;
                hgrav = hgrav2;
            } else {
                anchor.getLocationOnScreen(screenLocation);
                drawingLocation[0] = screenLocation[0] - appScreenLocation[0];
                drawingLocation[1] = screenLocation[1] - appScreenLocation[1];
                layoutParams = outParams;
                boolean z2 = true;
                layoutParams.f504x = drawingLocation[0] + xOffset;
                layoutParams.f505y = drawingLocation[1] + anchorHeight + yOffset2;
                hgrav = hgrav2;
                z = z2;
                if (hgrav == 5) {
                    layoutParams.f504x -= width3 - anchorWidth;
                    z = z2;
                }
            }
            layoutParams2 = layoutParams;
            tryFitVertical(outParams, yOffset2, height3, anchorHeight, drawingLocation[z ? 1 : 0], screenLocation[z ? 1 : 0], displayFrame.top, displayFrame.bottom, this.mClipToScreen);
            tryFitHorizontal(outParams, xOffset, width3, anchorWidth, drawingLocation[0], screenLocation[0], displayFrame.left, displayFrame.right, this.mClipToScreen);
            r19 = z;
        }
        if (layoutParams2.f505y < drawingLocation[r19]) {
            return r19;
        }
        return false;
    }

    private boolean tryFitVertical(WindowManager.LayoutParams outParams, int yOffset, int height, int anchorHeight, int drawingLocationY, int screenLocationY, int displayFrameTop, int displayFrameBottom, boolean allowResize) {
        int yOffset2;
        int winOffsetY = screenLocationY - drawingLocationY;
        int anchorTopInScreen = outParams.f505y + winOffsetY;
        int spaceBelow = displayFrameBottom - anchorTopInScreen;
        if (anchorTopInScreen >= displayFrameTop && height <= spaceBelow) {
            return true;
        }
        int spaceAbove = (anchorTopInScreen - anchorHeight) - displayFrameTop;
        if (height > spaceAbove) {
            if (positionInDisplayVertical(outParams, height, drawingLocationY, screenLocationY, displayFrameTop, displayFrameBottom, allowResize)) {
                return true;
            }
            return false;
        }
        if (!this.mOverlapAnchor) {
            yOffset2 = yOffset;
        } else {
            yOffset2 = yOffset + anchorHeight;
        }
        outParams.f505y = (drawingLocationY - height) + yOffset2;
        return true;
    }

    private boolean positionInDisplayVertical(WindowManager.LayoutParams outParams, int height, int drawingLocationY, int screenLocationY, int displayFrameTop, int displayFrameBottom, boolean canResize) {
        boolean fitsInDisplay = true;
        int winOffsetY = screenLocationY - drawingLocationY;
        outParams.f505y += winOffsetY;
        outParams.height = height;
        int bottom = outParams.f505y + height;
        if (bottom > displayFrameBottom) {
            outParams.f505y -= bottom - displayFrameBottom;
        }
        if (outParams.f505y < displayFrameTop) {
            outParams.f505y = displayFrameTop;
            int displayFrameHeight = displayFrameBottom - displayFrameTop;
            if (canResize && height > displayFrameHeight) {
                outParams.height = displayFrameHeight;
            } else {
                fitsInDisplay = false;
            }
        }
        outParams.f505y -= winOffsetY;
        return fitsInDisplay;
    }

    private boolean tryFitHorizontal(WindowManager.LayoutParams outParams, int xOffset, int width, int anchorWidth, int drawingLocationX, int screenLocationX, int displayFrameLeft, int displayFrameRight, boolean allowResize) {
        int winOffsetX = screenLocationX - drawingLocationX;
        int anchorLeftInScreen = outParams.f504x + winOffsetX;
        int spaceRight = displayFrameRight - anchorLeftInScreen;
        if (anchorLeftInScreen >= displayFrameLeft && width <= spaceRight) {
            return true;
        }
        if (positionInDisplayHorizontal(outParams, width, drawingLocationX, screenLocationX, displayFrameLeft, displayFrameRight, allowResize)) {
            return true;
        }
        return false;
    }

    private boolean positionInDisplayHorizontal(WindowManager.LayoutParams outParams, int width, int drawingLocationX, int screenLocationX, int displayFrameLeft, int displayFrameRight, boolean canResize) {
        boolean fitsInDisplay = true;
        int winOffsetX = screenLocationX - drawingLocationX;
        outParams.f504x += winOffsetX;
        int right = outParams.f504x + width;
        if (right > displayFrameRight) {
            outParams.f504x -= right - displayFrameRight;
        }
        if (outParams.f504x < displayFrameLeft) {
            outParams.f504x = displayFrameLeft;
            int displayFrameWidth = displayFrameRight - displayFrameLeft;
            if (canResize && width > displayFrameWidth) {
                outParams.width = displayFrameWidth;
            } else {
                fitsInDisplay = false;
            }
        }
        outParams.f504x -= winOffsetX;
        return fitsInDisplay;
    }

    public int getMaxAvailableHeight(View anchor) {
        return getMaxAvailableHeight(anchor, 0);
    }

    public int getMaxAvailableHeight(View anchor, int yOffset) {
        return getMaxAvailableHeight(anchor, yOffset, false);
    }

    public int getMaxAvailableHeight(View anchor, int yOffset, boolean ignoreBottomDecorations) {
        Rect displayFrame;
        int distanceToBottom;
        Rect visibleDisplayFrame = new Rect();
        View appView = getAppRootView(anchor);
        appView.getWindowVisibleDisplayFrame(visibleDisplayFrame);
        if (ignoreBottomDecorations) {
            displayFrame = new Rect();
            anchor.getWindowDisplayFrame(displayFrame);
            displayFrame.top = visibleDisplayFrame.top;
            displayFrame.right = visibleDisplayFrame.right;
            displayFrame.left = visibleDisplayFrame.left;
        } else {
            displayFrame = visibleDisplayFrame;
        }
        int[] anchorPos = this.mTmpDrawingLocation;
        anchor.getLocationOnScreen(anchorPos);
        int bottomEdge = displayFrame.bottom;
        if (this.mOverlapAnchor) {
            distanceToBottom = (bottomEdge - anchorPos[1]) - yOffset;
        } else {
            int distanceToBottom2 = anchorPos[1];
            distanceToBottom = (bottomEdge - (distanceToBottom2 + anchor.getHeight())) - yOffset;
        }
        int distanceToTop = (anchorPos[1] - displayFrame.top) + yOffset;
        int returnedHeight = Math.max(distanceToBottom, distanceToTop);
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.getPadding(this.mTempRect);
            return returnedHeight - (this.mTempRect.top + this.mTempRect.bottom);
        }
        return returnedHeight;
    }

    public void dismiss() {
        final ViewGroup contentHolder;
        if (!isShowing() || isTransitioningToDismiss()) {
            return;
        }
        final PopupDecorView decorView = this.mDecorView;
        final View contentView = this.mContentView;
        unregisterBackCallback(decorView.findOnBackInvokedDispatcher());
        ViewParent contentParent = contentView.getParent();
        if (contentParent instanceof ViewGroup) {
            contentHolder = (ViewGroup) contentParent;
        } else {
            contentHolder = null;
        }
        decorView.cancelTransitions();
        this.mIsShowing = false;
        this.mIsTransitioningToDismiss = true;
        Transition exitTransition = this.mExitTransition;
        if (exitTransition != null && decorView.isLaidOut() && (this.mIsAnchorRootAttached || this.mAnchorRoot == null)) {
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) decorView.getLayoutParams();
            p.flags |= 16;
            p.flags |= 8;
            p.flags &= -131073;
            this.mWindowManager.updateViewLayout(decorView, p);
            WeakReference<View> weakReference = this.mAnchorRoot;
            View anchorRoot = weakReference != null ? weakReference.get() : null;
            Rect epicenter = getTransitionEpicenter();
            decorView.startExitTransition(exitTransition, anchorRoot, epicenter, new TransitionListenerAdapter() { // from class: android.widget.PopupWindow.3
                @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                public void onTransitionEnd(Transition transition) {
                    PopupWindow.this.dismissImmediate(decorView, contentHolder, contentView);
                }
            });
        } else {
            dismissImmediate(decorView, contentHolder, contentView);
        }
        detachFromAnchor();
        OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterBackCallback(OnBackInvokedDispatcher onBackInvokedDispatcher) {
        OnBackInvokedCallback backCallback = this.mBackCallback;
        this.mBackCallback = null;
        if (onBackInvokedDispatcher != null && backCallback != null) {
            onBackInvokedDispatcher.unregisterOnBackInvokedCallback(backCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Rect getTransitionEpicenter() {
        WeakReference<View> weakReference = this.mAnchor;
        View anchor = weakReference != null ? weakReference.get() : null;
        View decor = this.mDecorView;
        if (anchor == null || decor == null) {
            return null;
        }
        int[] anchorLocation = anchor.getLocationOnScreen();
        int[] popupLocation = this.mDecorView.getLocationOnScreen();
        Rect bounds = new Rect(0, 0, anchor.getWidth(), anchor.getHeight());
        bounds.offset(anchorLocation[0] - popupLocation[0], anchorLocation[1] - popupLocation[1]);
        if (this.mEpicenterBounds != null) {
            int offsetX = bounds.left;
            int offsetY = bounds.top;
            bounds.set(this.mEpicenterBounds);
            bounds.offset(offsetX, offsetY);
        }
        return bounds;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissImmediate(View decorView, ViewGroup contentHolder, View contentView) {
        if (decorView.getParent() != null) {
            this.mWindowManager.removeViewImmediate(decorView);
        }
        if (contentHolder != null) {
            contentHolder.removeView(contentView);
        }
        this.mDecorView = null;
        this.mBackgroundView = null;
        this.mIsTransitioningToDismiss = false;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final OnDismissListener getOnDismissListener() {
        return this.mOnDismissListener;
    }

    public void update() {
        if (!isShowing() || !hasContentView()) {
            return;
        }
        WindowManager.LayoutParams p = getDecorViewLayoutParams();
        boolean update = false;
        int newAnim = computeAnimationResource();
        if (newAnim != p.windowAnimations) {
            p.windowAnimations = newAnim;
            update = true;
        }
        int newFlags = computeFlags(p.flags);
        if (newFlags != p.flags) {
            p.flags = newFlags;
            update = true;
        }
        int newGravity = computeGravity();
        if (newGravity != p.gravity) {
            p.gravity = newGravity;
            update = true;
        }
        if (update) {
            WeakReference<View> weakReference = this.mAnchor;
            update(weakReference != null ? weakReference.get() : null, p);
        }
    }

    protected void update(View anchor, WindowManager.LayoutParams params) {
        setLayoutDirectionFromAnchor();
        this.mWindowManager.updateViewLayout(this.mDecorView, params);
    }

    public void update(int width, int height) {
        WindowManager.LayoutParams p = getDecorViewLayoutParams();
        update(p.f504x, p.f505y, width, height, false);
    }

    public void update(int x, int y, int width, int height) {
        update(x, y, width, height, false);
    }

    public void update(int x, int y, int width, int height, boolean force) {
        if (width >= 0) {
            this.mLastWidth = width;
            setWidth(width);
        }
        if (height >= 0) {
            this.mLastHeight = height;
            setHeight(height);
        }
        if (!isShowing() || !hasContentView()) {
            return;
        }
        WindowManager.LayoutParams p = getDecorViewLayoutParams();
        boolean update = force;
        int finalWidth = this.mWidthMode;
        if (finalWidth >= 0) {
            finalWidth = this.mLastWidth;
        }
        if (width != -1 && p.width != finalWidth) {
            this.mLastWidth = finalWidth;
            p.width = finalWidth;
            update = true;
        }
        int finalHeight = this.mHeightMode;
        if (finalHeight >= 0) {
            finalHeight = this.mLastHeight;
        }
        if (height != -1 && p.height != finalHeight) {
            this.mLastHeight = finalHeight;
            p.height = finalHeight;
            update = true;
        }
        if (p.f504x != x) {
            p.f504x = x;
            update = true;
        }
        if (p.f505y != y) {
            p.f505y = y;
            update = true;
        }
        int newAnim = computeAnimationResource();
        if (newAnim != p.windowAnimations) {
            p.windowAnimations = newAnim;
            update = true;
        }
        int newFlags = computeFlags(p.flags);
        if (newFlags != p.flags) {
            p.flags = newFlags;
            update = true;
        }
        int newGravity = computeGravity();
        if (newGravity != p.gravity) {
            p.gravity = newGravity;
            update = true;
        }
        View anchor = null;
        int newAccessibilityIdOfAnchor = -1;
        WeakReference<View> weakReference = this.mAnchor;
        if (weakReference != null && weakReference.get() != null) {
            anchor = this.mAnchor.get();
            newAccessibilityIdOfAnchor = anchor.getAccessibilityViewId();
        }
        if (newAccessibilityIdOfAnchor != p.accessibilityIdOfAnchor) {
            p.accessibilityIdOfAnchor = newAccessibilityIdOfAnchor;
            update = true;
        }
        if (update) {
            update(anchor, p);
        }
    }

    protected boolean hasContentView() {
        return this.mContentView != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasDecorView() {
        return this.mDecorView != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WindowManager.LayoutParams getDecorViewLayoutParams() {
        return (WindowManager.LayoutParams) this.mDecorView.getLayoutParams();
    }

    public void update(View anchor, int width, int height) {
        update(anchor, false, 0, 0, width, height);
    }

    public void update(View anchor, int xoff, int yoff, int width, int height) {
        update(anchor, true, xoff, yoff, width, height);
    }

    private void update(View anchor, boolean updateLocation, int xoff, int yoff, int width, int height) {
        int width2;
        int height2;
        if (!isShowing() || !hasContentView()) {
            return;
        }
        WeakReference<View> oldAnchor = this.mAnchor;
        int gravity = this.mAnchoredGravity;
        boolean needsUpdate = updateLocation && !(this.mAnchorXoff == xoff && this.mAnchorYoff == yoff);
        if (oldAnchor == null || oldAnchor.get() != anchor || (needsUpdate && !this.mIsDropdown)) {
            attachToAnchor(anchor, xoff, yoff, gravity);
        } else if (needsUpdate) {
            this.mAnchorXoff = xoff;
            this.mAnchorYoff = yoff;
        }
        WindowManager.LayoutParams p = getDecorViewLayoutParams();
        int oldGravity = p.gravity;
        int oldWidth = p.width;
        int oldHeight = p.height;
        int oldX = p.f504x;
        int oldY = p.f505y;
        if (width >= 0) {
            width2 = width;
        } else {
            width2 = this.mWidth;
        }
        if (height >= 0) {
            height2 = height;
        } else {
            height2 = this.mHeight;
        }
        int oldHeight2 = width2;
        int oldWidth2 = height2;
        boolean aboveAnchor = findDropDownPosition(anchor, p, this.mAnchorXoff, this.mAnchorYoff, oldHeight2, oldWidth2, gravity, this.mAllowScrollingAnchorParent);
        updateAboveAnchor(aboveAnchor);
        boolean paramsChanged = (oldGravity == p.gravity && oldX == p.f504x && oldY == p.f505y && oldWidth == p.width && oldHeight == p.height) ? false : true;
        int newWidth = width2 < 0 ? width2 : p.width;
        int newHeight = height2 < 0 ? height2 : p.height;
        update(p.f504x, p.f505y, newWidth, newHeight, paramsChanged);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void detachFromAnchor() {
        View anchor = getAnchor();
        if (anchor != null) {
            ViewTreeObserver vto = anchor.getViewTreeObserver();
            vto.removeOnScrollChangedListener(this.mOnScrollChangedListener);
            anchor.removeOnAttachStateChangeListener(this.mOnAnchorDetachedListener);
        }
        WeakReference<View> weakReference = this.mAnchorRoot;
        View anchorRoot = weakReference != null ? weakReference.get() : null;
        if (anchorRoot != null) {
            anchorRoot.removeOnAttachStateChangeListener(this.mOnAnchorRootDetachedListener);
            anchorRoot.removeOnLayoutChangeListener(this.mOnLayoutChangeListener);
        }
        this.mAnchor = null;
        this.mAnchorRoot = null;
        this.mIsAnchorRootAttached = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void attachToAnchor(View anchor, int xoff, int yoff, int gravity) {
        detachFromAnchor();
        ViewTreeObserver vto = anchor.getViewTreeObserver();
        if (vto != null) {
            vto.addOnScrollChangedListener(this.mOnScrollChangedListener);
        }
        anchor.addOnAttachStateChangeListener(this.mOnAnchorDetachedListener);
        View anchorRoot = anchor.getRootView();
        anchorRoot.addOnAttachStateChangeListener(this.mOnAnchorRootDetachedListener);
        anchorRoot.addOnLayoutChangeListener(this.mOnLayoutChangeListener);
        this.mAnchor = new WeakReference<>(anchor);
        this.mAnchorRoot = new WeakReference<>(anchorRoot);
        this.mIsAnchorRootAttached = anchorRoot.isAttachedToWindow();
        this.mParentRootView = this.mAnchorRoot;
        this.mAnchorXoff = xoff;
        this.mAnchorYoff = yoff;
        this.mAnchoredGravity = gravity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View getAnchor() {
        WeakReference<View> weakReference = this.mAnchor;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void alignToAnchor() {
        WeakReference<View> weakReference = this.mAnchor;
        View anchor = weakReference != null ? weakReference.get() : null;
        if (anchor != null && anchor.isAttachedToWindow() && hasDecorView()) {
            WindowManager.LayoutParams p = getDecorViewLayoutParams();
            updateAboveAnchor(findDropDownPosition(anchor, p, this.mAnchorXoff, this.mAnchorYoff, p.width, p.height, this.mAnchoredGravity, false));
            update(p.f504x, p.f505y, -1, -1, true);
        }
    }

    private View getAppRootView(View anchor) {
        View appWindowView = WindowManagerGlobal.getInstance().getWindowView(anchor.getApplicationWindowToken());
        if (appWindowView != null) {
            return appWindowView;
        }
        return anchor.getRootView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class PopupDecorView extends FrameLayout {
        private Runnable mCleanupAfterExit;
        private final View.OnAttachStateChangeListener mOnAnchorRootDetachedListener;

        public PopupDecorView(Context context) {
            super(context);
            this.mOnAnchorRootDetachedListener = new View.OnAttachStateChangeListener() { // from class: android.widget.PopupWindow.PopupDecorView.4
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View v) {
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View v) {
                    v.removeOnAttachStateChangeListener(this);
                    if (PopupDecorView.this.isAttachedToWindow()) {
                        TransitionManager.endTransitions(PopupDecorView.this);
                    }
                }
            };
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent event) {
            KeyEvent.DispatcherState state;
            if (event.getKeyCode() == 4 || event.getKeyCode() == 111) {
                if (getKeyDispatcherState() == null) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState state2 = getKeyDispatcherState();
                    if (state2 != null) {
                        state2.startTracking(event, this);
                    }
                    return true;
                } else if (event.getAction() == 1 && (state = getKeyDispatcherState()) != null && state.isTracking(event) && !event.isCanceled()) {
                    PopupWindow.this.dismiss();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
            }
            return super.dispatchKeyEvent(event);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (PopupWindow.this.mTouchInterceptor != null && PopupWindow.this.mTouchInterceptor.onTouch(this, ev)) {
                return true;
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (event.getAction() == 0 && (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())) {
                PopupWindow.this.dismiss();
                return true;
            } else if (event.getAction() == 4) {
                PopupWindow.this.dismiss();
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        }

        public void requestEnterTransition(Transition transition) {
            ViewTreeObserver observer = getViewTreeObserver();
            if (observer != null && transition != null) {
                final Transition enterTransition = transition.mo4799clone();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.widget.PopupWindow.PopupDecorView.1
                    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                    public void onGlobalLayout() {
                        ViewTreeObserver observer2 = PopupDecorView.this.getViewTreeObserver();
                        if (observer2 != null) {
                            observer2.removeOnGlobalLayoutListener(this);
                        }
                        final Rect epicenter = PopupWindow.this.getTransitionEpicenter();
                        enterTransition.setEpicenterCallback(new Transition.EpicenterCallback() { // from class: android.widget.PopupWindow.PopupDecorView.1.1
                            @Override // android.transition.Transition.EpicenterCallback
                            public Rect onGetEpicenter(Transition transition2) {
                                return epicenter;
                            }
                        });
                        PopupDecorView.this.startEnterTransition(enterTransition);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startEnterTransition(Transition enterTransition) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                enterTransition.addTarget(child);
                child.setTransitionVisibility(4);
            }
            TransitionManager.beginDelayedTransition(this, enterTransition);
            for (int i2 = 0; i2 < count; i2++) {
                getChildAt(i2).setTransitionVisibility(0);
            }
        }

        public void startExitTransition(final Transition transition, final View anchorRoot, final Rect epicenter, final Transition.TransitionListener listener) {
            if (transition == null) {
                return;
            }
            if (anchorRoot != null) {
                anchorRoot.addOnAttachStateChangeListener(this.mOnAnchorRootDetachedListener);
            }
            this.mCleanupAfterExit = new Runnable() { // from class: android.widget.PopupWindow$PopupDecorView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PopupWindow.PopupDecorView.this.lambda$startExitTransition$0(listener, transition, anchorRoot);
                }
            };
            Transition exitTransition = transition.mo4799clone();
            exitTransition.addListener(new TransitionListenerAdapter() { // from class: android.widget.PopupWindow.PopupDecorView.2
                @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                public void onTransitionEnd(Transition t) {
                    t.removeListener(this);
                    if (PopupDecorView.this.mCleanupAfterExit != null) {
                        PopupDecorView.this.mCleanupAfterExit.run();
                    }
                }
            });
            exitTransition.setEpicenterCallback(new Transition.EpicenterCallback() { // from class: android.widget.PopupWindow.PopupDecorView.3
                @Override // android.transition.Transition.EpicenterCallback
                public Rect onGetEpicenter(Transition transition2) {
                    return epicenter;
                }
            });
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                exitTransition.addTarget(child);
            }
            TransitionManager.beginDelayedTransition(this, exitTransition);
            for (int i2 = 0; i2 < count; i2++) {
                View child2 = getChildAt(i2);
                child2.setVisibility(4);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$startExitTransition$0(Transition.TransitionListener listener, Transition transition, View anchorRoot) {
            listener.onTransitionEnd(transition);
            if (anchorRoot != null) {
                anchorRoot.removeOnAttachStateChangeListener(this.mOnAnchorRootDetachedListener);
            }
            this.mCleanupAfterExit = null;
        }

        public void cancelTransitions() {
            TransitionManager.endTransitions(this);
            Runnable runnable = this.mCleanupAfterExit;
            if (runnable != null) {
                runnable.run();
            }
        }

        @Override // android.view.View
        public void requestKeyboardShortcuts(List<KeyboardShortcutGroup> list, int deviceId) {
            View parentRoot;
            if (PopupWindow.this.mParentRootView != null && (parentRoot = (View) PopupWindow.this.mParentRootView.get()) != null) {
                parentRoot.requestKeyboardShortcuts(list, deviceId);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            OnBackInvokedDispatcher dispatcher;
            super.onAttachedToWindow();
            if (!WindowOnBackInvokedDispatcher.isOnBackInvokedCallbackEnabled(this.mContext) || (dispatcher = findOnBackInvokedDispatcher()) == null) {
                return;
            }
            final PopupWindow popupWindow = PopupWindow.this;
            popupWindow.mBackCallback = new OnBackInvokedCallback() { // from class: android.widget.PopupWindow$PopupDecorView$$ExternalSyntheticLambda0
                @Override // android.window.OnBackInvokedCallback
                public final void onBackInvoked() {
                    PopupWindow.this.dismiss();
                }
            };
            dispatcher.registerOnBackInvokedCallback(0, PopupWindow.this.mBackCallback);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            PopupWindow.this.unregisterBackCallback(findOnBackInvokedDispatcher());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class PopupBackgroundView extends FrameLayout {
        public PopupBackgroundView(Context context) {
            super(context);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public int[] onCreateDrawableState(int extraSpace) {
            if (PopupWindow.this.mAboveAnchor) {
                int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
                View.mergeDrawableStates(drawableState, PopupWindow.ABOVE_ANCHOR_STATE_SET);
                return drawableState;
            }
            return super.onCreateDrawableState(extraSpace);
        }
    }
}
