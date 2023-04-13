package android.service.selectiontoolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.p008os.IBinder;
import android.service.selectiontoolbar.RemoteSelectionToolbar;
import android.service.selectiontoolbar.SelectionToolbarRenderService;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.view.selectiontoolbar.ShowInfo;
import android.view.selectiontoolbar.ToolbarMenuItem;
import android.view.selectiontoolbar.WidgetInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.util.Preconditions;
import com.android.internal.widget.floatingtoolbar.FloatingToolbar;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class RemoteSelectionToolbar {
    private static final int MAX_OVERFLOW_SIZE = 4;
    private static final int MIN_OVERFLOW_SIZE = 2;
    private static final String TAG = "RemoteSelectionToolbar";
    private final Drawable mArrow;
    private final SelectionToolbarRenderService.RemoteCallbackWrapper mCallbackWrapper;
    private final AnimationSet mCloseOverflowAnimation;
    private final ViewGroup mContentContainer;
    private final Context mContext;
    private final AnimatorSet mDismissAnimation;
    private final Interpolator mFastOutLinearInInterpolator;
    private final Interpolator mFastOutSlowInInterpolator;
    private boolean mHidden;
    private final AnimatorSet mHideAnimation;
    private IBinder mHostInputToken;
    private final int mIconTextSpacing;
    private boolean mIsOverflowOpen;
    private final int mLineHeight;
    private final Interpolator mLinearOutSlowInInterpolator;
    private final Interpolator mLogAccelerateInterpolator;
    private final ViewGroup mMainPanel;
    private Size mMainPanelSize;
    private final int mMarginHorizontal;
    private final int mMarginVertical;
    private final View.OnClickListener mMenuItemButtonOnClickListener;
    private List<ToolbarMenuItem> mMenuItems;
    private final AnimationSet mOpenOverflowAnimation;
    private boolean mOpenOverflowUpwards;
    private final Drawable mOverflow;
    private final Animation.AnimationListener mOverflowAnimationListener;
    private final ImageButton mOverflowButton;
    private final Size mOverflowButtonSize;
    private final OverflowPanel mOverflowPanel;
    private Size mOverflowPanelSize;
    private final OverflowPanelViewHelper mOverflowPanelViewHelper;
    private int mPopupHeight;
    private int mPopupWidth;
    private final long mSelectionToolbarToken;
    private final AnimatorSet mShowAnimation;
    private SurfaceControlViewHost mSurfaceControlViewHost;
    private SurfaceControlViewHost.SurfacePackage mSurfacePackage;
    private final AnimatedVectorDrawable mToArrow;
    private final AnimatedVectorDrawable mToOverflow;
    private final SelectionToolbarRenderService.TransferTouchListener mTransferTouchListener;
    private int mTransitionDurationScale;
    private final Rect mViewPortOnScreen = new Rect();
    private final Point mRelativeCoordsForToolbar = new Point();
    private final Runnable mPreparePopupContentRTLHelper = new Runnable() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.1
        @Override // java.lang.Runnable
        public void run() {
            RemoteSelectionToolbar.this.setPanelsStatesAtRestingPosition();
            RemoteSelectionToolbar.this.mContentContainer.setAlpha(1.0f);
        }
    };
    private boolean mDismissed = true;
    private final Rect mPreviousContentRect = new Rect();
    private final Rect mTempContentRect = new Rect();
    private final Rect mTempContentRectForRoot = new Rect();
    private final int[] mTempCoords = new int[2];

    /* JADX INFO: Access modifiers changed from: package-private */
    public RemoteSelectionToolbar(Context context, long selectionToolbarToken, ShowInfo showInfo, SelectionToolbarRenderService.RemoteCallbackWrapper callbackWrapper, SelectionToolbarRenderService.TransferTouchListener transferTouchListener) {
        Context applyDefaultTheme = applyDefaultTheme(context, showInfo.isIsLightTheme());
        this.mContext = applyDefaultTheme;
        this.mSelectionToolbarToken = selectionToolbarToken;
        this.mCallbackWrapper = callbackWrapper;
        this.mTransferTouchListener = transferTouchListener;
        this.mHostInputToken = showInfo.getHostInputToken();
        ViewGroup createContentContainer = createContentContainer(applyDefaultTheme);
        this.mContentContainer = createContentContainer;
        this.mMarginHorizontal = applyDefaultTheme.getResources().getDimensionPixelSize(C4057R.dimen.floating_toolbar_horizontal_margin);
        this.mMarginVertical = applyDefaultTheme.getResources().getDimensionPixelSize(C4057R.dimen.floating_toolbar_vertical_margin);
        this.mLineHeight = applyDefaultTheme.getResources().getDimensionPixelSize(C4057R.dimen.floating_toolbar_height);
        int dimensionPixelSize = applyDefaultTheme.getResources().getDimensionPixelSize(C4057R.dimen.floating_toolbar_icon_text_spacing);
        this.mIconTextSpacing = dimensionPixelSize;
        this.mLogAccelerateInterpolator = new LogAccelerateInterpolator();
        this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(applyDefaultTheme, 17563661);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(applyDefaultTheme, 17563662);
        this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(applyDefaultTheme, 17563663);
        Drawable drawable = applyDefaultTheme.getResources().getDrawable(C4057R.C4058drawable.ft_avd_tooverflow, applyDefaultTheme.getTheme());
        this.mArrow = drawable;
        drawable.setAutoMirrored(true);
        Drawable drawable2 = applyDefaultTheme.getResources().getDrawable(C4057R.C4058drawable.ft_avd_toarrow, applyDefaultTheme.getTheme());
        this.mOverflow = drawable2;
        drawable2.setAutoMirrored(true);
        AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) applyDefaultTheme.getResources().getDrawable(C4057R.C4058drawable.ft_avd_toarrow_animation, applyDefaultTheme.getTheme());
        this.mToArrow = animatedVectorDrawable;
        animatedVectorDrawable.setAutoMirrored(true);
        AnimatedVectorDrawable animatedVectorDrawable2 = (AnimatedVectorDrawable) applyDefaultTheme.getResources().getDrawable(C4057R.C4058drawable.ft_avd_tooverflow_animation, applyDefaultTheme.getTheme());
        this.mToOverflow = animatedVectorDrawable2;
        animatedVectorDrawable2.setAutoMirrored(true);
        ImageButton createOverflowButton = createOverflowButton();
        this.mOverflowButton = createOverflowButton;
        this.mOverflowButtonSize = measure(createOverflowButton);
        this.mMainPanel = createMainPanel();
        this.mOverflowPanelViewHelper = new OverflowPanelViewHelper(applyDefaultTheme, dimensionPixelSize);
        this.mOverflowPanel = createOverflowPanel();
        Animation.AnimationListener createOverflowAnimationListener = createOverflowAnimationListener();
        this.mOverflowAnimationListener = createOverflowAnimationListener;
        AnimationSet animationSet = new AnimationSet(true);
        this.mOpenOverflowAnimation = animationSet;
        animationSet.setAnimationListener(createOverflowAnimationListener);
        AnimationSet animationSet2 = new AnimationSet(true);
        this.mCloseOverflowAnimation = animationSet2;
        animationSet2.setAnimationListener(createOverflowAnimationListener);
        this.mShowAnimation = createEnterAnimation(createContentContainer, new AnimatorListenerAdapter() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                RemoteSelectionToolbar.this.updateFloatingToolbarRootContentRect();
            }
        });
        this.mDismissAnimation = createExitAnimation(createContentContainer, 150, new AnimatorListenerAdapter() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                RemoteSelectionToolbar.this.mContentContainer.removeAllViews();
                RemoteSelectionToolbar.this.mSurfaceControlViewHost.release();
                RemoteSelectionToolbar.this.mSurfaceControlViewHost = null;
                RemoteSelectionToolbar.this.mSurfacePackage = null;
            }
        });
        this.mHideAnimation = createExitAnimation(createContentContainer, 0, null);
        this.mMenuItemButtonOnClickListener = new View.OnClickListener() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RemoteSelectionToolbar.this.lambda$new$0(view);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View v) {
        Object tag = v.getTag();
        if (!(tag instanceof ToolbarMenuItem)) {
            return;
        }
        this.mCallbackWrapper.onMenuItemClicked((ToolbarMenuItem) tag);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFloatingToolbarRootContentRect() {
        SurfaceControlViewHost surfaceControlViewHost = this.mSurfaceControlViewHost;
        if (surfaceControlViewHost == null) {
            return;
        }
        FloatingToolbarRoot root = (FloatingToolbarRoot) surfaceControlViewHost.getView();
        this.mContentContainer.getLocationOnScreen(this.mTempCoords);
        int[] iArr = this.mTempCoords;
        int contentLeft = iArr[0];
        int contentTop = iArr[1];
        this.mTempContentRectForRoot.set(contentLeft, contentTop, this.mContentContainer.getWidth() + contentLeft, this.mContentContainer.getHeight() + contentTop);
        root.setContentRect(this.mTempContentRectForRoot);
    }

    private WidgetInfo createWidgetInfo() {
        this.mTempContentRect.set(this.mRelativeCoordsForToolbar.f76x, this.mRelativeCoordsForToolbar.f77y, this.mRelativeCoordsForToolbar.f76x + this.mPopupWidth, this.mRelativeCoordsForToolbar.f77y + this.mPopupHeight);
        return new WidgetInfo(this.mSelectionToolbarToken, this.mTempContentRect, getSurfacePackage());
    }

    private SurfaceControlViewHost.SurfacePackage getSurfacePackage() {
        if (this.mSurfaceControlViewHost == null) {
            FloatingToolbarRoot contentHolder = new FloatingToolbarRoot(this.mContext, this.mHostInputToken, this.mTransferTouchListener);
            contentHolder.addView(this.mContentContainer);
            Context context = this.mContext;
            SurfaceControlViewHost surfaceControlViewHost = new SurfaceControlViewHost(context, context.getDisplay(), this.mHostInputToken, TAG);
            this.mSurfaceControlViewHost = surfaceControlViewHost;
            surfaceControlViewHost.setView(contentHolder, this.mPopupWidth, this.mPopupHeight);
        }
        if (this.mSurfacePackage == null) {
            this.mSurfacePackage = this.mSurfaceControlViewHost.getSurfacePackage();
        }
        return this.mSurfacePackage;
    }

    private void layoutMenuItems(List<ToolbarMenuItem> menuItems, int suggestedWidth) {
        cancelOverflowAnimations();
        clearPanels();
        List<ToolbarMenuItem> menuItems2 = layoutMainPanelItems(menuItems, getAdjustedToolbarWidth(suggestedWidth));
        if (!menuItems2.isEmpty()) {
            layoutOverflowPanelItems(menuItems2);
        }
        updatePopupSize();
    }

    public void onToolbarShowTimeout() {
        this.mCallbackWrapper.onToolbarShowTimeout();
    }

    public void show(ShowInfo showInfo) {
        debugLog("show() for " + showInfo);
        this.mMenuItems = showInfo.getMenuItems();
        this.mViewPortOnScreen.set(showInfo.getViewPortOnScreen());
        debugLog("show(): layoutRequired=" + showInfo.isLayoutRequired());
        if (showInfo.isLayoutRequired()) {
            layoutMenuItems(this.mMenuItems, showInfo.getSuggestedWidth());
        }
        Rect contentRect = showInfo.getContentRect();
        if (!isShowing()) {
            show(contentRect);
        } else if (!this.mPreviousContentRect.equals(contentRect)) {
            updateCoordinates(contentRect);
        }
        this.mPreviousContentRect.set(contentRect);
    }

    private void show(Rect contentRectOnScreen) {
        Objects.requireNonNull(contentRectOnScreen);
        this.mHidden = false;
        this.mDismissed = false;
        cancelDismissAndHideAnimations();
        cancelOverflowAnimations();
        refreshCoordinatesAndOverflowDirection(contentRectOnScreen);
        preparePopupContent();
        this.mCallbackWrapper.onShown(createWidgetInfo());
        this.mShowAnimation.start();
    }

    public void dismiss(long floatingToolbarToken) {
        debugLog("dismiss for " + floatingToolbarToken);
        if (this.mDismissed) {
            return;
        }
        this.mHidden = false;
        this.mDismissed = true;
        this.mHideAnimation.cancel();
        this.mDismissAnimation.start();
    }

    public void hide(long floatingToolbarToken) {
        debugLog("hide for " + floatingToolbarToken);
        if (!isShowing()) {
            return;
        }
        this.mHidden = true;
        this.mHideAnimation.start();
    }

    public boolean isShowing() {
        return (this.mDismissed || this.mHidden) ? false : true;
    }

    private void updateCoordinates(Rect contentRectOnScreen) {
        Objects.requireNonNull(contentRectOnScreen);
        if (!isShowing()) {
            return;
        }
        cancelOverflowAnimations();
        refreshCoordinatesAndOverflowDirection(contentRectOnScreen);
        preparePopupContent();
        WidgetInfo widgetInfo = createWidgetInfo();
        this.mSurfaceControlViewHost.relayout(this.mPopupWidth, this.mPopupHeight);
        this.mCallbackWrapper.onWidgetUpdated(widgetInfo);
    }

    private void refreshCoordinatesAndOverflowDirection(Rect contentRectOnScreen) {
        int minimumOverflowHeightWithMargin;
        int x = Math.min(contentRectOnScreen.centerX() - (this.mPopupWidth / 2), this.mViewPortOnScreen.right - this.mPopupWidth);
        int availableHeightAboveContent = contentRectOnScreen.top - this.mViewPortOnScreen.top;
        int availableHeightBelowContent = this.mViewPortOnScreen.bottom - contentRectOnScreen.bottom;
        int margin = this.mMarginVertical * 2;
        int toolbarHeightWithVerticalMargin = this.mLineHeight + margin;
        if (!hasOverflow()) {
            if (availableHeightAboveContent >= toolbarHeightWithVerticalMargin) {
                minimumOverflowHeightWithMargin = contentRectOnScreen.top - toolbarHeightWithVerticalMargin;
            } else if (availableHeightBelowContent >= toolbarHeightWithVerticalMargin) {
                minimumOverflowHeightWithMargin = contentRectOnScreen.bottom;
            } else {
                int y = this.mLineHeight;
                if (availableHeightBelowContent >= y) {
                    minimumOverflowHeightWithMargin = contentRectOnScreen.bottom - this.mMarginVertical;
                } else {
                    minimumOverflowHeightWithMargin = Math.max(this.mViewPortOnScreen.top, contentRectOnScreen.top - toolbarHeightWithVerticalMargin);
                }
            }
        } else {
            int minimumOverflowHeightWithMargin2 = calculateOverflowHeight(2) + margin;
            int availableHeightThroughContentDown = (this.mViewPortOnScreen.bottom - contentRectOnScreen.top) + toolbarHeightWithVerticalMargin;
            int availableHeightThroughContentUp = (contentRectOnScreen.bottom - this.mViewPortOnScreen.top) + toolbarHeightWithVerticalMargin;
            if (availableHeightAboveContent >= minimumOverflowHeightWithMargin2) {
                updateOverflowHeight(availableHeightAboveContent - margin);
                int y2 = contentRectOnScreen.top - this.mPopupHeight;
                this.mOpenOverflowUpwards = true;
                minimumOverflowHeightWithMargin = y2;
            } else if (availableHeightAboveContent >= toolbarHeightWithVerticalMargin && availableHeightThroughContentDown >= minimumOverflowHeightWithMargin2) {
                updateOverflowHeight(availableHeightThroughContentDown - margin);
                int y3 = contentRectOnScreen.top - toolbarHeightWithVerticalMargin;
                this.mOpenOverflowUpwards = false;
                minimumOverflowHeightWithMargin = y3;
            } else if (availableHeightBelowContent >= minimumOverflowHeightWithMargin2) {
                updateOverflowHeight(availableHeightBelowContent - margin);
                int y4 = contentRectOnScreen.bottom;
                this.mOpenOverflowUpwards = false;
                minimumOverflowHeightWithMargin = y4;
            } else if (availableHeightBelowContent >= toolbarHeightWithVerticalMargin && this.mViewPortOnScreen.height() >= minimumOverflowHeightWithMargin2) {
                updateOverflowHeight(availableHeightThroughContentUp - margin);
                int y5 = (contentRectOnScreen.bottom + toolbarHeightWithVerticalMargin) - this.mPopupHeight;
                this.mOpenOverflowUpwards = true;
                minimumOverflowHeightWithMargin = y5;
            } else {
                updateOverflowHeight(this.mViewPortOnScreen.height() - margin);
                int y6 = this.mViewPortOnScreen.top;
                this.mOpenOverflowUpwards = false;
                minimumOverflowHeightWithMargin = y6;
            }
        }
        this.mRelativeCoordsForToolbar.set(x, minimumOverflowHeightWithMargin);
    }

    private void cancelDismissAndHideAnimations() {
        this.mDismissAnimation.cancel();
        this.mHideAnimation.cancel();
    }

    private void cancelOverflowAnimations() {
        this.mContentContainer.clearAnimation();
        this.mMainPanel.animate().cancel();
        this.mOverflowPanel.animate().cancel();
        this.mToArrow.stop();
        this.mToOverflow.stop();
    }

    private void openOverflow() {
        final int targetWidth = this.mOverflowPanelSize.getWidth();
        final int targetHeight = this.mOverflowPanelSize.getHeight();
        final int startWidth = this.mContentContainer.getWidth();
        final int startHeight = this.mContentContainer.getHeight();
        final float startY = this.mContentContainer.getY();
        final float left = this.mContentContainer.getX();
        final float right = left + this.mContentContainer.getWidth();
        Animation widthAnimation = new Animation() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.view.animation.Animation
            public void applyTransformation(float interpolatedTime, Transformation t) {
                int deltaWidth = (int) ((targetWidth - startWidth) * interpolatedTime);
                RemoteSelectionToolbar.setWidth(RemoteSelectionToolbar.this.mContentContainer, startWidth + deltaWidth);
                if (RemoteSelectionToolbar.this.isInRTLMode()) {
                    RemoteSelectionToolbar.this.mContentContainer.setX(left);
                    RemoteSelectionToolbar.this.mMainPanel.setX(0.0f);
                    RemoteSelectionToolbar.this.mOverflowPanel.setX(0.0f);
                    return;
                }
                RemoteSelectionToolbar.this.mContentContainer.setX(right - RemoteSelectionToolbar.this.mContentContainer.getWidth());
                RemoteSelectionToolbar.this.mMainPanel.setX(RemoteSelectionToolbar.this.mContentContainer.getWidth() - startWidth);
                RemoteSelectionToolbar.this.mOverflowPanel.setX(RemoteSelectionToolbar.this.mContentContainer.getWidth() - targetWidth);
            }
        };
        Animation heightAnimation = new Animation() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.5
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.view.animation.Animation
            public void applyTransformation(float interpolatedTime, Transformation t) {
                int deltaHeight = (int) ((targetHeight - startHeight) * interpolatedTime);
                RemoteSelectionToolbar.setHeight(RemoteSelectionToolbar.this.mContentContainer, startHeight + deltaHeight);
                if (RemoteSelectionToolbar.this.mOpenOverflowUpwards) {
                    RemoteSelectionToolbar.this.mContentContainer.setY(startY - (RemoteSelectionToolbar.this.mContentContainer.getHeight() - startHeight));
                    RemoteSelectionToolbar.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                }
            }
        };
        final float overflowButtonStartX = this.mOverflowButton.getX();
        final float overflowButtonTargetX = isInRTLMode() ? (targetWidth + overflowButtonStartX) - this.mOverflowButton.getWidth() : (overflowButtonStartX - targetWidth) + this.mOverflowButton.getWidth();
        Animation overflowButtonAnimation = new Animation() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.6
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.view.animation.Animation
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float f = overflowButtonStartX;
                float overflowButtonX = f + ((overflowButtonTargetX - f) * interpolatedTime);
                float deltaContainerWidth = RemoteSelectionToolbar.this.isInRTLMode() ? 0.0f : RemoteSelectionToolbar.this.mContentContainer.getWidth() - startWidth;
                float actualOverflowButtonX = overflowButtonX + deltaContainerWidth;
                RemoteSelectionToolbar.this.mOverflowButton.setX(actualOverflowButtonX);
                RemoteSelectionToolbar.this.updateFloatingToolbarRootContentRect();
            }
        };
        widthAnimation.setInterpolator(this.mLogAccelerateInterpolator);
        widthAnimation.setDuration(getAnimationDuration());
        heightAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
        heightAnimation.setDuration(getAnimationDuration());
        overflowButtonAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
        overflowButtonAnimation.setDuration(getAnimationDuration());
        this.mOpenOverflowAnimation.getAnimations().clear();
        this.mOpenOverflowAnimation.addAnimation(widthAnimation);
        this.mOpenOverflowAnimation.addAnimation(heightAnimation);
        this.mOpenOverflowAnimation.addAnimation(overflowButtonAnimation);
        this.mContentContainer.startAnimation(this.mOpenOverflowAnimation);
        this.mIsOverflowOpen = true;
        this.mMainPanel.animate().alpha(0.0f).withLayer().setInterpolator(this.mLinearOutSlowInInterpolator).setDuration(250L).start();
        this.mOverflowPanel.setAlpha(1.0f);
    }

    private void closeOverflow() {
        final int targetWidth = this.mMainPanelSize.getWidth();
        final int startWidth = this.mContentContainer.getWidth();
        final float left = this.mContentContainer.getX();
        final float right = left + this.mContentContainer.getWidth();
        Animation widthAnimation = new Animation() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.7
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.view.animation.Animation
            public void applyTransformation(float interpolatedTime, Transformation t) {
                int deltaWidth = (int) ((targetWidth - startWidth) * interpolatedTime);
                RemoteSelectionToolbar.setWidth(RemoteSelectionToolbar.this.mContentContainer, startWidth + deltaWidth);
                if (RemoteSelectionToolbar.this.isInRTLMode()) {
                    RemoteSelectionToolbar.this.mContentContainer.setX(left);
                    RemoteSelectionToolbar.this.mMainPanel.setX(0.0f);
                    RemoteSelectionToolbar.this.mOverflowPanel.setX(0.0f);
                    return;
                }
                RemoteSelectionToolbar.this.mContentContainer.setX(right - RemoteSelectionToolbar.this.mContentContainer.getWidth());
                RemoteSelectionToolbar.this.mMainPanel.setX(RemoteSelectionToolbar.this.mContentContainer.getWidth() - targetWidth);
                RemoteSelectionToolbar.this.mOverflowPanel.setX(RemoteSelectionToolbar.this.mContentContainer.getWidth() - startWidth);
            }
        };
        final int targetHeight = this.mMainPanelSize.getHeight();
        final int startHeight = this.mContentContainer.getHeight();
        final float bottom = this.mContentContainer.getY() + this.mContentContainer.getHeight();
        Animation heightAnimation = new Animation() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.8
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.view.animation.Animation
            public void applyTransformation(float interpolatedTime, Transformation t) {
                int deltaHeight = (int) ((targetHeight - startHeight) * interpolatedTime);
                RemoteSelectionToolbar.setHeight(RemoteSelectionToolbar.this.mContentContainer, startHeight + deltaHeight);
                if (RemoteSelectionToolbar.this.mOpenOverflowUpwards) {
                    RemoteSelectionToolbar.this.mContentContainer.setY(bottom - RemoteSelectionToolbar.this.mContentContainer.getHeight());
                    RemoteSelectionToolbar.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                }
            }
        };
        final float overflowButtonStartX = this.mOverflowButton.getX();
        final float overflowButtonTargetX = isInRTLMode() ? (overflowButtonStartX - startWidth) + this.mOverflowButton.getWidth() : (startWidth + overflowButtonStartX) - this.mOverflowButton.getWidth();
        Animation overflowButtonAnimation = new Animation() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.9
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.view.animation.Animation
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float f = overflowButtonStartX;
                float overflowButtonX = f + ((overflowButtonTargetX - f) * interpolatedTime);
                float deltaContainerWidth = RemoteSelectionToolbar.this.isInRTLMode() ? 0.0f : RemoteSelectionToolbar.this.mContentContainer.getWidth() - startWidth;
                float actualOverflowButtonX = overflowButtonX + deltaContainerWidth;
                RemoteSelectionToolbar.this.mOverflowButton.setX(actualOverflowButtonX);
                RemoteSelectionToolbar.this.updateFloatingToolbarRootContentRect();
            }
        };
        widthAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
        widthAnimation.setDuration(getAnimationDuration());
        heightAnimation.setInterpolator(this.mLogAccelerateInterpolator);
        heightAnimation.setDuration(getAnimationDuration());
        overflowButtonAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
        overflowButtonAnimation.setDuration(getAnimationDuration());
        this.mCloseOverflowAnimation.getAnimations().clear();
        this.mCloseOverflowAnimation.addAnimation(widthAnimation);
        this.mCloseOverflowAnimation.addAnimation(heightAnimation);
        this.mCloseOverflowAnimation.addAnimation(overflowButtonAnimation);
        this.mContentContainer.startAnimation(this.mCloseOverflowAnimation);
        this.mIsOverflowOpen = false;
        this.mMainPanel.animate().alpha(1.0f).withLayer().setInterpolator(this.mFastOutLinearInInterpolator).setDuration(100L).start();
        this.mOverflowPanel.animate().alpha(0.0f).withLayer().setInterpolator(this.mLinearOutSlowInInterpolator).setDuration(150L).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPanelsStatesAtRestingPosition() {
        this.mOverflowButton.setEnabled(true);
        this.mOverflowPanel.awakenScrollBars();
        if (this.mIsOverflowOpen) {
            Size containerSize = this.mOverflowPanelSize;
            setSize(this.mContentContainer, containerSize);
            this.mMainPanel.setAlpha(0.0f);
            this.mMainPanel.setVisibility(4);
            this.mOverflowPanel.setAlpha(1.0f);
            this.mOverflowPanel.setVisibility(0);
            this.mOverflowButton.setImageDrawable(this.mArrow);
            this.mOverflowButton.setContentDescription(this.mContext.getString(C4057R.string.floating_toolbar_close_overflow_description));
            if (isInRTLMode()) {
                this.mContentContainer.setX(this.mMarginHorizontal);
                this.mMainPanel.setX(0.0f);
                this.mOverflowButton.setX(containerSize.getWidth() - this.mOverflowButtonSize.getWidth());
                this.mOverflowPanel.setX(0.0f);
            } else {
                this.mContentContainer.setX((this.mPopupWidth - containerSize.getWidth()) - this.mMarginHorizontal);
                this.mMainPanel.setX(-this.mContentContainer.getX());
                this.mOverflowButton.setX(0.0f);
                this.mOverflowPanel.setX(0.0f);
            }
            if (this.mOpenOverflowUpwards) {
                this.mContentContainer.setY(this.mMarginVertical);
                this.mMainPanel.setY(containerSize.getHeight() - this.mContentContainer.getHeight());
                this.mOverflowButton.setY(containerSize.getHeight() - this.mOverflowButtonSize.getHeight());
                this.mOverflowPanel.setY(0.0f);
                return;
            }
            this.mContentContainer.setY(this.mMarginVertical);
            this.mMainPanel.setY(0.0f);
            this.mOverflowButton.setY(0.0f);
            this.mOverflowPanel.setY(this.mOverflowButtonSize.getHeight());
            return;
        }
        Size containerSize2 = this.mMainPanelSize;
        setSize(this.mContentContainer, containerSize2);
        this.mMainPanel.setAlpha(1.0f);
        this.mMainPanel.setVisibility(0);
        this.mOverflowPanel.setAlpha(0.0f);
        this.mOverflowPanel.setVisibility(4);
        this.mOverflowButton.setImageDrawable(this.mOverflow);
        this.mOverflowButton.setContentDescription(this.mContext.getString(C4057R.string.floating_toolbar_open_overflow_description));
        if (hasOverflow()) {
            if (isInRTLMode()) {
                this.mContentContainer.setX(this.mMarginHorizontal);
                this.mMainPanel.setX(0.0f);
                this.mOverflowButton.setX(0.0f);
                this.mOverflowPanel.setX(0.0f);
            } else {
                this.mContentContainer.setX((this.mPopupWidth - containerSize2.getWidth()) - this.mMarginHorizontal);
                this.mMainPanel.setX(0.0f);
                this.mOverflowButton.setX(containerSize2.getWidth() - this.mOverflowButtonSize.getWidth());
                this.mOverflowPanel.setX(containerSize2.getWidth() - this.mOverflowPanelSize.getWidth());
            }
            if (this.mOpenOverflowUpwards) {
                this.mContentContainer.setY((this.mMarginVertical + this.mOverflowPanelSize.getHeight()) - containerSize2.getHeight());
                this.mMainPanel.setY(0.0f);
                this.mOverflowButton.setY(0.0f);
                this.mOverflowPanel.setY(containerSize2.getHeight() - this.mOverflowPanelSize.getHeight());
                return;
            }
            this.mContentContainer.setY(this.mMarginVertical);
            this.mMainPanel.setY(0.0f);
            this.mOverflowButton.setY(0.0f);
            this.mOverflowPanel.setY(this.mOverflowButtonSize.getHeight());
            return;
        }
        this.mContentContainer.setX(this.mMarginHorizontal);
        this.mContentContainer.setY(this.mMarginVertical);
        this.mMainPanel.setX(0.0f);
        this.mMainPanel.setY(0.0f);
    }

    private void updateOverflowHeight(int suggestedHeight) {
        if (hasOverflow()) {
            int maxItemSize = (suggestedHeight - this.mOverflowButtonSize.getHeight()) / this.mLineHeight;
            int newHeight = calculateOverflowHeight(maxItemSize);
            if (this.mOverflowPanelSize.getHeight() != newHeight) {
                this.mOverflowPanelSize = new Size(this.mOverflowPanelSize.getWidth(), newHeight);
            }
            setSize(this.mOverflowPanel, this.mOverflowPanelSize);
            if (this.mIsOverflowOpen) {
                setSize(this.mContentContainer, this.mOverflowPanelSize);
                if (this.mOpenOverflowUpwards) {
                    int deltaHeight = this.mOverflowPanelSize.getHeight() - newHeight;
                    ViewGroup viewGroup = this.mContentContainer;
                    viewGroup.setY(viewGroup.getY() + deltaHeight);
                    ImageButton imageButton = this.mOverflowButton;
                    imageButton.setY(imageButton.getY() - deltaHeight);
                }
            } else {
                setSize(this.mContentContainer, this.mMainPanelSize);
            }
            updatePopupSize();
        }
    }

    private void updatePopupSize() {
        int width = 0;
        int height = 0;
        Size size = this.mMainPanelSize;
        if (size != null) {
            width = Math.max(0, size.getWidth());
            height = Math.max(0, this.mMainPanelSize.getHeight());
        }
        Size size2 = this.mOverflowPanelSize;
        if (size2 != null) {
            width = Math.max(width, size2.getWidth());
            height = Math.max(height, this.mOverflowPanelSize.getHeight());
        }
        this.mPopupWidth = (this.mMarginHorizontal * 2) + width;
        this.mPopupHeight = (this.mMarginVertical * 2) + height;
        maybeComputeTransitionDurationScale();
    }

    private int getAdjustedToolbarWidth(int suggestedWidth) {
        int width = suggestedWidth;
        int maximumWidth = this.mViewPortOnScreen.width() - (this.mContext.getResources().getDimensionPixelSize(C4057R.dimen.floating_toolbar_horizontal_margin) * 2);
        if (width <= 0) {
            width = this.mContext.getResources().getDimensionPixelSize(C4057R.dimen.floating_toolbar_preferred_width);
        }
        return Math.min(width, maximumWidth);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInRTLMode() {
        return this.mContext.getApplicationInfo().hasRtlSupport() && this.mContext.getResources().getConfiguration().getLayoutDirection() == 1;
    }

    private boolean hasOverflow() {
        return this.mOverflowPanelSize != null;
    }

    private List<ToolbarMenuItem> layoutMainPanelItems(List<ToolbarMenuItem> menuItems, int toolbarWidth) {
        int i;
        int i2;
        LinkedList<ToolbarMenuItem> remainingMenuItems = new LinkedList<>();
        LinkedList<ToolbarMenuItem> overflowMenuItems = new LinkedList<>();
        Iterator<ToolbarMenuItem> it = menuItems.iterator();
        while (true) {
            i = 2;
            i2 = 16908353;
            if (!it.hasNext()) {
                break;
            }
            ToolbarMenuItem menuItem = it.next();
            if (menuItem.getItemId() != 16908353 && menuItem.getPriority() == 2) {
                overflowMenuItems.add(menuItem);
            } else {
                remainingMenuItems.add(menuItem);
            }
        }
        remainingMenuItems.addAll(overflowMenuItems);
        this.mMainPanel.removeAllViews();
        this.mMainPanel.setPaddingRelative(0, 0, 0, 0);
        int availableWidth = toolbarWidth;
        boolean isFirstItem = true;
        while (!remainingMenuItems.isEmpty()) {
            ToolbarMenuItem menuItem2 = remainingMenuItems.peek();
            if (!isFirstItem && menuItem2.getPriority() == i) {
                break;
            }
            boolean showIcon = isFirstItem && menuItem2.getItemId() == i2;
            View menuItemButton = createMenuItemButton(this.mContext, menuItem2, this.mIconTextSpacing, showIcon);
            if (!showIcon && (menuItemButton instanceof LinearLayout)) {
                ((LinearLayout) menuItemButton).setGravity(17);
            }
            if (isFirstItem) {
                menuItemButton.setPaddingRelative((int) (menuItemButton.getPaddingStart() * 1.5d), menuItemButton.getPaddingTop(), menuItemButton.getPaddingEnd(), menuItemButton.getPaddingBottom());
            }
            boolean isLastItem = remainingMenuItems.size() == 1;
            if (isLastItem) {
                menuItemButton.setPaddingRelative(menuItemButton.getPaddingStart(), menuItemButton.getPaddingTop(), (int) (menuItemButton.getPaddingEnd() * 1.5d), menuItemButton.getPaddingBottom());
            }
            menuItemButton.measure(0, 0);
            int menuItemButtonWidth = Math.min(menuItemButton.getMeasuredWidth(), toolbarWidth);
            boolean canFitWithOverflow = menuItemButtonWidth <= availableWidth - this.mOverflowButtonSize.getWidth();
            boolean canFitNoOverflow = isLastItem && menuItemButtonWidth <= availableWidth;
            if (!canFitWithOverflow && !canFitNoOverflow) {
                break;
            }
            menuItemButton.setTag(menuItem2);
            menuItemButton.setOnClickListener(this.mMenuItemButtonOnClickListener);
            menuItemButton.setTooltipText(menuItem2.getTooltipText());
            this.mMainPanel.addView(menuItemButton);
            ViewGroup.LayoutParams params = menuItemButton.getLayoutParams();
            params.width = menuItemButtonWidth;
            menuItemButton.setLayoutParams(params);
            availableWidth -= menuItemButtonWidth;
            remainingMenuItems.pop();
            isFirstItem = false;
            i = 2;
            i2 = 16908353;
        }
        if (!remainingMenuItems.isEmpty()) {
            this.mMainPanel.setPaddingRelative(0, 0, this.mOverflowButtonSize.getWidth(), 0);
        }
        this.mMainPanelSize = measure(this.mMainPanel);
        return remainingMenuItems;
    }

    private void layoutOverflowPanelItems(List<ToolbarMenuItem> menuItems) {
        ArrayAdapter<ToolbarMenuItem> overflowPanelAdapter = (ArrayAdapter) this.mOverflowPanel.getAdapter();
        overflowPanelAdapter.clear();
        int size = menuItems.size();
        for (int i = 0; i < size; i++) {
            overflowPanelAdapter.add(menuItems.get(i));
        }
        this.mOverflowPanel.setAdapter((ListAdapter) overflowPanelAdapter);
        if (this.mOpenOverflowUpwards) {
            this.mOverflowPanel.setY(0.0f);
        } else {
            this.mOverflowPanel.setY(this.mOverflowButtonSize.getHeight());
        }
        int width = Math.max(getOverflowWidth(), this.mOverflowButtonSize.getWidth());
        int height = calculateOverflowHeight(4);
        Size size2 = new Size(width, height);
        this.mOverflowPanelSize = size2;
        setSize(this.mOverflowPanel, size2);
    }

    private void preparePopupContent() {
        this.mContentContainer.removeAllViews();
        if (hasOverflow()) {
            this.mContentContainer.addView(this.mOverflowPanel);
        }
        this.mContentContainer.addView(this.mMainPanel);
        if (hasOverflow()) {
            this.mContentContainer.addView(this.mOverflowButton);
        }
        setPanelsStatesAtRestingPosition();
        if (isInRTLMode()) {
            this.mContentContainer.setAlpha(0.0f);
            this.mContentContainer.post(this.mPreparePopupContentRTLHelper);
        }
    }

    private void clearPanels() {
        this.mIsOverflowOpen = false;
        this.mMainPanelSize = null;
        this.mMainPanel.removeAllViews();
        this.mOverflowPanelSize = null;
        ArrayAdapter<ToolbarMenuItem> overflowPanelAdapter = (ArrayAdapter) this.mOverflowPanel.getAdapter();
        overflowPanelAdapter.clear();
        this.mOverflowPanel.setAdapter((ListAdapter) overflowPanelAdapter);
        this.mContentContainer.removeAllViews();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void positionContentYCoordinatesIfOpeningOverflowUpwards() {
        if (this.mOpenOverflowUpwards) {
            this.mMainPanel.setY(this.mContentContainer.getHeight() - this.mMainPanelSize.getHeight());
            this.mOverflowButton.setY(this.mContentContainer.getHeight() - this.mOverflowButton.getHeight());
            this.mOverflowPanel.setY(this.mContentContainer.getHeight() - this.mOverflowPanelSize.getHeight());
        }
    }

    private int getOverflowWidth() {
        int overflowWidth = 0;
        int count = this.mOverflowPanel.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            ToolbarMenuItem menuItem = (ToolbarMenuItem) this.mOverflowPanel.getAdapter().getItem(i);
            overflowWidth = Math.max(this.mOverflowPanelViewHelper.calculateWidth(menuItem), overflowWidth);
        }
        return overflowWidth;
    }

    private int calculateOverflowHeight(int maxItemSize) {
        int actualSize = Math.min(4, Math.min(Math.max(2, maxItemSize), this.mOverflowPanel.getCount()));
        int extension = 0;
        if (actualSize < this.mOverflowPanel.getCount()) {
            extension = (int) (this.mLineHeight * 0.5f);
        }
        return (this.mLineHeight * actualSize) + this.mOverflowButtonSize.getHeight() + extension;
    }

    private int getAnimationDuration() {
        int i = this.mTransitionDurationScale;
        if (i < 150) {
            return 200;
        }
        if (i > 300) {
            return 300;
        }
        return (int) (ValueAnimator.getDurationScale() * 250.0f);
    }

    private void maybeComputeTransitionDurationScale() {
        Size size = this.mMainPanelSize;
        if (size != null && this.mOverflowPanelSize != null) {
            int w = size.getWidth() - this.mOverflowPanelSize.getWidth();
            int h = this.mOverflowPanelSize.getHeight() - this.mMainPanelSize.getHeight();
            this.mTransitionDurationScale = (int) (Math.sqrt((w * w) + (h * h)) / this.mContentContainer.getContext().getResources().getDisplayMetrics().density);
        }
    }

    private ViewGroup createMainPanel() {
        return new LinearLayout(this.mContext) { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.10
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.widget.LinearLayout, android.view.View
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (RemoteSelectionToolbar.this.isOverflowAnimating()) {
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(RemoteSelectionToolbar.this.mMainPanelSize.getWidth(), 1073741824);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return RemoteSelectionToolbar.this.isOverflowAnimating();
            }
        };
    }

    private ImageButton createOverflowButton() {
        final ImageButton overflowButton = (ImageButton) LayoutInflater.from(this.mContext).inflate(C4057R.layout.floating_popup_overflow_button, (ViewGroup) null);
        overflowButton.setImageDrawable(this.mOverflow);
        overflowButton.setOnClickListener(new View.OnClickListener() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RemoteSelectionToolbar.this.lambda$createOverflowButton$1(overflowButton, view);
            }
        });
        return overflowButton;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createOverflowButton$1(ImageButton overflowButton, View v) {
        if (isShowing()) {
            preparePopupContent();
            WidgetInfo widgetInfo = createWidgetInfo();
            this.mSurfaceControlViewHost.relayout(this.mPopupWidth, this.mPopupHeight);
            this.mCallbackWrapper.onWidgetUpdated(widgetInfo);
        }
        if (this.mIsOverflowOpen) {
            overflowButton.setImageDrawable(this.mToOverflow);
            this.mToOverflow.start();
            closeOverflow();
            return;
        }
        overflowButton.setImageDrawable(this.mToArrow);
        this.mToArrow.start();
        openOverflow();
    }

    private OverflowPanel createOverflowPanel() {
        final OverflowPanel overflowPanel = new OverflowPanel(this);
        overflowPanel.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        overflowPanel.setDivider(null);
        overflowPanel.setDividerHeight(0);
        ArrayAdapter adapter = new ArrayAdapter<ToolbarMenuItem>(this.mContext, 0) { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar.11
            @Override // android.widget.ArrayAdapter, android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                return RemoteSelectionToolbar.this.mOverflowPanelViewHelper.getView(getItem(position), RemoteSelectionToolbar.this.mOverflowPanelSize.getWidth(), convertView);
            }
        };
        overflowPanel.setAdapter((ListAdapter) adapter);
        overflowPanel.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar$$ExternalSyntheticLambda0
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                RemoteSelectionToolbar.this.lambda$createOverflowPanel$2(overflowPanel, adapterView, view, i, j);
            }
        });
        return overflowPanel;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createOverflowPanel$2(OverflowPanel overflowPanel, AdapterView parent, View view, int position, long id) {
        ToolbarMenuItem menuItem = (ToolbarMenuItem) overflowPanel.getAdapter().getItem(position);
        this.mCallbackWrapper.onMenuItemClicked(menuItem);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isOverflowAnimating() {
        boolean overflowOpening = this.mOpenOverflowAnimation.hasStarted() && !this.mOpenOverflowAnimation.hasEnded();
        boolean overflowClosing = this.mCloseOverflowAnimation.hasStarted() && !this.mCloseOverflowAnimation.hasEnded();
        return overflowOpening || overflowClosing;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.service.selectiontoolbar.RemoteSelectionToolbar$12  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class animationAnimation$AnimationListenerC263212 implements Animation.AnimationListener {
        animationAnimation$AnimationListenerC263212() {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
            RemoteSelectionToolbar.this.mOverflowButton.setEnabled(false);
            RemoteSelectionToolbar.this.mMainPanel.setVisibility(0);
            RemoteSelectionToolbar.this.mOverflowPanel.setVisibility(0);
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            RemoteSelectionToolbar.this.mContentContainer.post(new Runnable() { // from class: android.service.selectiontoolbar.RemoteSelectionToolbar$12$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RemoteSelectionToolbar.animationAnimation$AnimationListenerC263212.this.lambda$onAnimationEnd$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAnimationEnd$0() {
            RemoteSelectionToolbar.this.setPanelsStatesAtRestingPosition();
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private Animation.AnimationListener createOverflowAnimationListener() {
        return new animationAnimation$AnimationListenerC263212();
    }

    private static Size measure(View view) {
        Preconditions.checkState(view.getParent() == null);
        view.measure(0, 0);
        return new Size(view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    private static void setSize(View view, int width, int height) {
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        ViewGroup.LayoutParams params2 = params == null ? new ViewGroup.LayoutParams(0, 0) : params;
        params2.width = width;
        params2.height = height;
        view.setLayoutParams(params2);
    }

    private static void setSize(View view, Size size) {
        setSize(view, size.getWidth(), size.getHeight());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setWidth(View view, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        setSize(view, width, params.height);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        setSize(view, params.width, height);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class OverflowPanel extends ListView {
        private final RemoteSelectionToolbar mPopup;

        OverflowPanel(RemoteSelectionToolbar popup) {
            super(((RemoteSelectionToolbar) Objects.requireNonNull(popup)).mContext);
            this.mPopup = popup;
            setScrollBarDefaultDelayBeforeFade(ViewConfiguration.getScrollDefaultDelay() * 3);
            setScrollIndicators(3);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height = this.mPopup.mOverflowPanelSize.getHeight() - this.mPopup.mOverflowButtonSize.getHeight();
            int heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec2);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (this.mPopup.isOverflowAnimating()) {
                return true;
            }
            return super.dispatchTouchEvent(ev);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public boolean awakenScrollBars() {
            return super.awakenScrollBars();
        }
    }

    /* loaded from: classes3.dex */
    private static final class LogAccelerateInterpolator implements Interpolator {
        private static final int BASE = 100;
        private static final float LOGS_SCALE = 1.0f / computeLog(1.0f, 100);

        private LogAccelerateInterpolator() {
        }

        private static float computeLog(float t, int base) {
            return (float) (1.0d - Math.pow(base, -t));
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float t) {
            return 1.0f - (computeLog(1.0f - t, 100) * LOGS_SCALE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class OverflowPanelViewHelper {
        private final View mCalculator = createMenuButton(null);
        private final Context mContext;
        private final int mIconTextSpacing;
        private final int mSidePadding;

        OverflowPanelViewHelper(Context context, int iconTextSpacing) {
            this.mContext = (Context) Objects.requireNonNull(context);
            this.mIconTextSpacing = iconTextSpacing;
            this.mSidePadding = context.getResources().getDimensionPixelSize(C4057R.dimen.floating_toolbar_overflow_side_padding);
        }

        public View getView(ToolbarMenuItem menuItem, int minimumWidth, View convertView) {
            Objects.requireNonNull(menuItem);
            if (convertView != null) {
                RemoteSelectionToolbar.updateMenuItemButton(convertView, menuItem, this.mIconTextSpacing, shouldShowIcon(menuItem));
            } else {
                convertView = createMenuButton(menuItem);
            }
            convertView.setMinimumWidth(minimumWidth);
            return convertView;
        }

        public int calculateWidth(ToolbarMenuItem menuItem) {
            RemoteSelectionToolbar.updateMenuItemButton(this.mCalculator, menuItem, this.mIconTextSpacing, shouldShowIcon(menuItem));
            this.mCalculator.measure(0, 0);
            return this.mCalculator.getMeasuredWidth();
        }

        private View createMenuButton(ToolbarMenuItem menuItem) {
            View button = RemoteSelectionToolbar.createMenuItemButton(this.mContext, menuItem, this.mIconTextSpacing, shouldShowIcon(menuItem));
            int i = this.mSidePadding;
            button.setPadding(i, 0, i, 0);
            return button;
        }

        private boolean shouldShowIcon(ToolbarMenuItem menuItem) {
            return menuItem != null && menuItem.getGroupId() == 16908353;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static View createMenuItemButton(Context context, ToolbarMenuItem menuItem, int iconTextSpacing, boolean showIcon) {
        View menuItemButton = LayoutInflater.from(context).inflate(C4057R.layout.floating_popup_menu_button, (ViewGroup) null);
        if (menuItem != null) {
            updateMenuItemButton(menuItemButton, menuItem, iconTextSpacing, showIcon);
        }
        return menuItemButton;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateMenuItemButton(View menuItemButton, ToolbarMenuItem menuItem, int iconTextSpacing, boolean showIcon) {
        TextView buttonText = (TextView) menuItemButton.findViewById(C4057R.C4059id.floating_toolbar_menu_item_text);
        buttonText.setEllipsize(null);
        if (TextUtils.isEmpty(menuItem.getTitle())) {
            buttonText.setVisibility(8);
        } else {
            buttonText.setVisibility(0);
            buttonText.setText(menuItem.getTitle());
        }
        ImageView buttonIcon = (ImageView) menuItemButton.findViewById(C4057R.C4059id.floating_toolbar_menu_item_image);
        if (menuItem.getIcon() == null || !showIcon) {
            buttonIcon.setVisibility(8);
            buttonText.setPaddingRelative(0, 0, 0, 0);
        } else {
            buttonIcon.setVisibility(0);
            buttonIcon.setImageDrawable(menuItem.getIcon().loadDrawable(menuItemButton.getContext()));
            buttonText.setPaddingRelative(iconTextSpacing, 0, 0, 0);
        }
        CharSequence contentDescription = menuItem.getContentDescription();
        if (TextUtils.isEmpty(contentDescription)) {
            menuItemButton.setContentDescription(menuItem.getTitle());
        } else {
            menuItemButton.setContentDescription(contentDescription);
        }
    }

    private static ViewGroup createContentContainer(Context context) {
        ViewGroup contentContainer = (ViewGroup) LayoutInflater.from(context).inflate(C4057R.layout.floating_popup_container, (ViewGroup) null);
        contentContainer.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        contentContainer.setTag(FloatingToolbar.FLOATING_TOOLBAR_TAG);
        contentContainer.setClipToOutline(true);
        return contentContainer;
    }

    private static AnimatorSet createEnterAnimation(View view, Animator.AnimatorListener listener) {
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f).setDuration(150L));
        animation.addListener(listener);
        return animation;
    }

    private static AnimatorSet createExitAnimation(View view, int startDelay, Animator.AnimatorListener listener) {
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.0f).setDuration(100L));
        animation.setStartDelay(startDelay);
        if (listener != null) {
            animation.addListener(listener);
        }
        return animation;
    }

    private static Context applyDefaultTheme(Context originalContext, boolean isLightTheme) {
        int themeId = isLightTheme ? 16974123 : 16974120;
        return new ContextThemeWrapper(originalContext, themeId);
    }

    private static void debugLog(String message) {
        if (Log.isLoggable(FloatingToolbar.FLOATING_TOOLBAR_TAG, 3)) {
            Log.m106v(TAG, message);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("toolbar token: ");
        pw.println(this.mSelectionToolbarToken);
        pw.print(prefix);
        pw.print("dismissed: ");
        pw.println(this.mDismissed);
        pw.print(prefix);
        pw.print("hidden: ");
        pw.println(this.mHidden);
        pw.print(prefix);
        pw.print("popup width: ");
        pw.println(this.mPopupWidth);
        pw.print(prefix);
        pw.print("popup height: ");
        pw.println(this.mPopupHeight);
        pw.print(prefix);
        pw.print("relative coords: ");
        pw.println(this.mRelativeCoordsForToolbar);
        pw.print(prefix);
        pw.print("main panel size: ");
        pw.println(this.mMainPanelSize);
        boolean hasOverflow = hasOverflow();
        pw.print(prefix);
        pw.print("has overflow: ");
        pw.println(hasOverflow);
        if (hasOverflow) {
            pw.print(prefix);
            pw.print("overflow open: ");
            pw.println(this.mIsOverflowOpen);
            pw.print(prefix);
            pw.print("overflow size: ");
            pw.println(this.mOverflowPanelSize);
        }
        SurfaceControlViewHost surfaceControlViewHost = this.mSurfaceControlViewHost;
        if (surfaceControlViewHost != null) {
            FloatingToolbarRoot root = (FloatingToolbarRoot) surfaceControlViewHost.getView();
            root.dump(prefix, pw);
        }
        List<ToolbarMenuItem> list = this.mMenuItems;
        if (list != null) {
            int menuItemSize = list.size();
            pw.print(prefix);
            pw.print("number menu items: ");
            pw.println(menuItemSize);
            for (int i = 0; i < menuItemSize; i++) {
                pw.print(prefix);
                pw.print("#");
                pw.println(i);
                pw.print(prefix + "  ");
                pw.println(this.mMenuItems.get(i));
            }
        }
    }
}
