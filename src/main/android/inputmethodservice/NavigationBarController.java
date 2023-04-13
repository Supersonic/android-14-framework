package android.inputmethodservice;

import android.animation.ValueAnimator;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.Region;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.NavigationBarController;
import android.inputmethodservice.navigationbar.NavigationBarFrame;
import android.inputmethodservice.navigationbar.NavigationBarView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import com.android.internal.C4057R;
import java.util.Objects;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class NavigationBarController {
    private final Callback mImpl;

    /* loaded from: classes2.dex */
    private interface Callback {
        public static final Callback NOOP = new Callback() { // from class: android.inputmethodservice.NavigationBarController.Callback.1
        };

        default void updateTouchableInsets(InputMethodService.Insets originalInsets, ViewTreeObserver.InternalInsetsInfo dest) {
        }

        default void onSoftInputWindowCreated(SoftInputWindow softInputWindow) {
        }

        default void onViewInitialized() {
        }

        default void onWindowShown() {
        }

        default void onDestroy() {
        }

        default void onNavButtonFlagsChanged(int navButtonFlags) {
        }

        default String toDebugString() {
            return "No-op implementation";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NavigationBarController(InputMethodService inputMethodService) {
        this.mImpl = InputMethodService.canImeRenderGesturalNavButtons() ? new Impl(inputMethodService) : Callback.NOOP;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateTouchableInsets(InputMethodService.Insets originalInsets, ViewTreeObserver.InternalInsetsInfo dest) {
        this.mImpl.updateTouchableInsets(originalInsets, dest);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onSoftInputWindowCreated(SoftInputWindow softInputWindow) {
        this.mImpl.onSoftInputWindowCreated(softInputWindow);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onViewInitialized() {
        this.mImpl.onViewInitialized();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onWindowShown() {
        this.mImpl.onWindowShown();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDestroy() {
        this.mImpl.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onNavButtonFlagsChanged(int navButtonFlags) {
        this.mImpl.onNavButtonFlagsChanged(navButtonFlags);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String toDebugString() {
        return this.mImpl.toDebugString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class Impl implements Callback, Window.DecorCallback {
        private static final int DEFAULT_COLOR_ADAPT_TRANSITION_TIME = 1700;
        private static final Interpolator LEGACY_DECELERATE = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
        private int mAppearance;
        private float mDarkIntensity;
        private boolean mDrawLegacyNavigationBarBackground;
        private boolean mImeDrawsImeNavBar;
        Insets mLastInsets;
        private NavigationBarFrame mNavigationBarFrame;
        private final InputMethodService mService;
        private boolean mShouldShowImeSwitcherWhenImeIsShown;
        private ValueAnimator mTintAnimator;
        private boolean mDestroyed = false;
        private final Rect mTempRect = new Rect();
        private final int[] mTempPos = new int[2];

        Impl(InputMethodService inputMethodService) {
            this.mService = inputMethodService;
        }

        private Insets getSystemInsets() {
            View decorView;
            WindowInsets windowInsets;
            if (this.mService.mWindow == null || (decorView = this.mService.mWindow.getWindow().getDecorView()) == null || (windowInsets = decorView.getRootWindowInsets()) == null) {
                return null;
            }
            Insets stableBarInsets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return Insets.min(windowInsets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout()), stableBarInsets);
        }

        private void installNavigationBarFrameIfNecessary() {
            int i;
            if (!this.mImeDrawsImeNavBar || this.mNavigationBarFrame != null) {
                return;
            }
            View rawDecorView = this.mService.mWindow.getWindow().getDecorView();
            if (!(rawDecorView instanceof ViewGroup)) {
                return;
            }
            ViewGroup decorView = (ViewGroup) rawDecorView;
            Objects.requireNonNull(NavigationBarFrame.class);
            this.mNavigationBarFrame = (NavigationBarFrame) decorView.findViewByPredicate(new NavigationBarController$Impl$$ExternalSyntheticLambda0(NavigationBarFrame.class));
            Insets systemInsets = getSystemInsets();
            NavigationBarFrame navigationBarFrame = this.mNavigationBarFrame;
            if (navigationBarFrame == null) {
                this.mNavigationBarFrame = new NavigationBarFrame(this.mService);
                LayoutInflater.from(this.mService).inflate(C4057R.layout.input_method_navigation_bar, this.mNavigationBarFrame);
                if (systemInsets != null) {
                    decorView.addView(this.mNavigationBarFrame, new FrameLayout.LayoutParams(-1, systemInsets.bottom, 80));
                    this.mLastInsets = systemInsets;
                } else {
                    decorView.addView(this.mNavigationBarFrame);
                }
                NavigationBarFrame navigationBarFrame2 = this.mNavigationBarFrame;
                Objects.requireNonNull(NavigationBarView.class);
                NavigationBarView navigationBarView = (NavigationBarView) navigationBarFrame2.findViewByPredicate(new NavigationBarController$Impl$$ExternalSyntheticLambda0(NavigationBarView.class));
                if (navigationBarView != null) {
                    if (this.mShouldShowImeSwitcherWhenImeIsShown) {
                        i = 4;
                    } else {
                        i = 0;
                    }
                    int hints = i | 1;
                    navigationBarView.setNavigationIconHints(hints);
                }
            } else {
                navigationBarFrame.setLayoutParams(new FrameLayout.LayoutParams(-1, systemInsets.bottom, 80));
                this.mLastInsets = systemInsets;
            }
            if (this.mDrawLegacyNavigationBarBackground) {
                this.mNavigationBarFrame.setBackgroundColor(-16777216);
            } else {
                this.mNavigationBarFrame.setBackground(null);
            }
            setIconTintInternal(calculateTargetDarkIntensity(this.mAppearance, this.mDrawLegacyNavigationBarBackground));
        }

        private void uninstallNavigationBarFrameIfNecessary() {
            NavigationBarFrame navigationBarFrame = this.mNavigationBarFrame;
            if (navigationBarFrame == null) {
                return;
            }
            ViewParent parent = navigationBarFrame.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.mNavigationBarFrame);
            }
            this.mNavigationBarFrame = null;
        }

        @Override // android.inputmethodservice.NavigationBarController.Callback
        public void updateTouchableInsets(InputMethodService.Insets originalInsets, ViewTreeObserver.InternalInsetsInfo dest) {
            Insets systemInsets;
            if (this.mImeDrawsImeNavBar && this.mNavigationBarFrame != null && !this.mService.isExtractViewShown() && (systemInsets = getSystemInsets()) != null) {
                Window window = this.mService.mWindow.getWindow();
                View decor = window.getDecorView();
                Region touchableRegion = null;
                View inputFrame = this.mService.mInputFrame;
                boolean z = false;
                switch (originalInsets.touchableInsets) {
                    case 0:
                        if (inputFrame.getVisibility() == 0) {
                            inputFrame.getLocationInWindow(this.mTempPos);
                            Rect rect = this.mTempRect;
                            int[] iArr = this.mTempPos;
                            int i = iArr[0];
                            rect.set(i, iArr[1], inputFrame.getWidth() + i, this.mTempPos[1] + inputFrame.getHeight());
                            touchableRegion = new Region(this.mTempRect);
                            break;
                        }
                        break;
                    case 1:
                        if (inputFrame.getVisibility() == 0) {
                            inputFrame.getLocationInWindow(this.mTempPos);
                            this.mTempRect.set(this.mTempPos[0], originalInsets.contentTopInsets, this.mTempPos[0] + inputFrame.getWidth(), this.mTempPos[1] + inputFrame.getHeight());
                            touchableRegion = new Region(this.mTempRect);
                            break;
                        }
                        break;
                    case 2:
                        if (inputFrame.getVisibility() == 0) {
                            inputFrame.getLocationInWindow(this.mTempPos);
                            this.mTempRect.set(this.mTempPos[0], originalInsets.visibleTopInsets, this.mTempPos[0] + inputFrame.getWidth(), this.mTempPos[1] + inputFrame.getHeight());
                            touchableRegion = new Region(this.mTempRect);
                            break;
                        }
                        break;
                    case 3:
                        touchableRegion = new Region();
                        touchableRegion.set(originalInsets.touchableRegion);
                        break;
                }
                this.mTempRect.set(decor.getLeft(), decor.getBottom() - systemInsets.bottom, decor.getRight(), decor.getBottom());
                if (touchableRegion == null) {
                    touchableRegion = new Region(this.mTempRect);
                } else {
                    touchableRegion.union(this.mTempRect);
                }
                dest.touchableRegion.set(touchableRegion);
                dest.setTouchableInsets(3);
                boolean zOrderChanged = false;
                if (decor instanceof ViewGroup) {
                    ViewGroup decorGroup = (ViewGroup) decor;
                    View navbarBackgroundView = window.getNavigationBarBackgroundView();
                    if (navbarBackgroundView != null && decorGroup.indexOfChild(navbarBackgroundView) > decorGroup.indexOfChild(this.mNavigationBarFrame)) {
                        z = true;
                    }
                    zOrderChanged = z;
                }
                boolean insetChanged = true ^ Objects.equals(systemInsets, this.mLastInsets);
                if (zOrderChanged || insetChanged) {
                    scheduleRelayout();
                }
            }
        }

        private void scheduleRelayout() {
            final NavigationBarFrame frame = this.mNavigationBarFrame;
            frame.post(new Runnable() { // from class: android.inputmethodservice.NavigationBarController$Impl$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    NavigationBarController.Impl.this.lambda$scheduleRelayout$0(frame);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$scheduleRelayout$0(NavigationBarFrame frame) {
            Window window;
            View decor;
            if (this.mDestroyed || !frame.isAttachedToWindow() || (window = this.mService.mWindow.getWindow()) == null || (decor = window.peekDecorView()) == null || !(decor instanceof ViewGroup)) {
                return;
            }
            ViewGroup decorGroup = (ViewGroup) decor;
            Insets currentSystemInsets = getSystemInsets();
            if (!Objects.equals(currentSystemInsets, this.mLastInsets)) {
                frame.setLayoutParams(new FrameLayout.LayoutParams(-1, currentSystemInsets.bottom, 80));
                this.mLastInsets = currentSystemInsets;
            }
            View navbarBackgroundView = window.getNavigationBarBackgroundView();
            if (navbarBackgroundView != null && decorGroup.indexOfChild(navbarBackgroundView) > decorGroup.indexOfChild(frame)) {
                decorGroup.bringChildToFront(frame);
            }
        }

        @Override // android.inputmethodservice.NavigationBarController.Callback
        public void onSoftInputWindowCreated(SoftInputWindow softInputWindow) {
            Window window = softInputWindow.getWindow();
            this.mAppearance = window.getSystemBarAppearance();
            window.setDecorCallback(this);
        }

        @Override // android.inputmethodservice.NavigationBarController.Callback
        public void onViewInitialized() {
            if (this.mDestroyed) {
                return;
            }
            installNavigationBarFrameIfNecessary();
        }

        @Override // android.inputmethodservice.NavigationBarController.Callback
        public void onDestroy() {
            if (this.mDestroyed) {
                return;
            }
            ValueAnimator valueAnimator = this.mTintAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.mTintAnimator = null;
            }
            this.mDestroyed = true;
        }

        @Override // android.inputmethodservice.NavigationBarController.Callback
        public void onWindowShown() {
            Insets systemInsets;
            if (!this.mDestroyed && this.mImeDrawsImeNavBar && this.mNavigationBarFrame != null && (systemInsets = getSystemInsets()) != null) {
                if (!Objects.equals(systemInsets, this.mLastInsets)) {
                    this.mNavigationBarFrame.setLayoutParams(new FrameLayout.LayoutParams(-1, systemInsets.bottom, 80));
                    this.mLastInsets = systemInsets;
                }
                Window window = this.mService.mWindow.getWindow();
                View rawDecorView = window.getDecorView();
                if (rawDecorView instanceof ViewGroup) {
                    ViewGroup decor = (ViewGroup) rawDecorView;
                    View navbarBackgroundView = window.getNavigationBarBackgroundView();
                    if (navbarBackgroundView != null && decor.indexOfChild(navbarBackgroundView) > decor.indexOfChild(this.mNavigationBarFrame)) {
                        decor.bringChildToFront(this.mNavigationBarFrame);
                    }
                }
                this.mNavigationBarFrame.setVisibility(0);
            }
        }

        @Override // android.inputmethodservice.NavigationBarController.Callback
        public void onNavButtonFlagsChanged(int navButtonFlags) {
            if (this.mDestroyed) {
                return;
            }
            boolean imeDrawsImeNavBar = (navButtonFlags & 1) != 0;
            boolean shouldShowImeSwitcherWhenImeIsShown = (navButtonFlags & 2) != 0;
            this.mImeDrawsImeNavBar = imeDrawsImeNavBar;
            boolean prevShouldShowImeSwitcherWhenImeIsShown = this.mShouldShowImeSwitcherWhenImeIsShown;
            this.mShouldShowImeSwitcherWhenImeIsShown = shouldShowImeSwitcherWhenImeIsShown;
            if (imeDrawsImeNavBar) {
                installNavigationBarFrameIfNecessary();
                NavigationBarFrame navigationBarFrame = this.mNavigationBarFrame;
                if (navigationBarFrame == null || this.mShouldShowImeSwitcherWhenImeIsShown == prevShouldShowImeSwitcherWhenImeIsShown) {
                    return;
                }
                Objects.requireNonNull(NavigationBarView.class);
                NavigationBarView navigationBarView = (NavigationBarView) navigationBarFrame.findViewByPredicate(new NavigationBarController$Impl$$ExternalSyntheticLambda0(NavigationBarView.class));
                if (navigationBarView == null) {
                    return;
                }
                int hints = (shouldShowImeSwitcherWhenImeIsShown ? 4 : 0) | 1;
                navigationBarView.setNavigationIconHints(hints);
                return;
            }
            uninstallNavigationBarFrameIfNecessary();
        }

        @Override // android.view.Window.DecorCallback
        public void onSystemBarAppearanceChanged(int appearance) {
            if (this.mDestroyed) {
                return;
            }
            this.mAppearance = appearance;
            if (this.mNavigationBarFrame == null) {
                return;
            }
            float targetDarkIntensity = calculateTargetDarkIntensity(appearance, this.mDrawLegacyNavigationBarBackground);
            ValueAnimator valueAnimator = this.mTintAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.mDarkIntensity, targetDarkIntensity);
            this.mTintAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.inputmethodservice.NavigationBarController$Impl$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    NavigationBarController.Impl.this.lambda$onSystemBarAppearanceChanged$1(valueAnimator2);
                }
            });
            this.mTintAnimator.setDuration(1700L);
            this.mTintAnimator.setStartDelay(0L);
            this.mTintAnimator.setInterpolator(LEGACY_DECELERATE);
            this.mTintAnimator.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSystemBarAppearanceChanged$1(ValueAnimator animation) {
            setIconTintInternal(((Float) animation.getAnimatedValue()).floatValue());
        }

        private void setIconTintInternal(float darkIntensity) {
            this.mDarkIntensity = darkIntensity;
            NavigationBarFrame navigationBarFrame = this.mNavigationBarFrame;
            if (navigationBarFrame == null) {
                return;
            }
            Objects.requireNonNull(NavigationBarView.class);
            NavigationBarView navigationBarView = (NavigationBarView) navigationBarFrame.findViewByPredicate(new NavigationBarController$Impl$$ExternalSyntheticLambda0(NavigationBarView.class));
            if (navigationBarView == null) {
                return;
            }
            navigationBarView.setDarkIntensity(darkIntensity);
        }

        private static float calculateTargetDarkIntensity(int appearance, boolean drawLegacyNavigationBarBackground) {
            boolean lightNavBar = (drawLegacyNavigationBarBackground || (appearance & 16) == 0) ? false : true;
            return lightNavBar ? 1.0f : 0.0f;
        }

        @Override // android.view.Window.DecorCallback
        public boolean onDrawLegacyNavigationBarBackgroundChanged(boolean drawLegacyNavigationBarBackground) {
            if (this.mDestroyed) {
                return false;
            }
            if (drawLegacyNavigationBarBackground != this.mDrawLegacyNavigationBarBackground) {
                this.mDrawLegacyNavigationBarBackground = drawLegacyNavigationBarBackground;
                NavigationBarFrame navigationBarFrame = this.mNavigationBarFrame;
                if (navigationBarFrame != null) {
                    if (drawLegacyNavigationBarBackground) {
                        navigationBarFrame.setBackgroundColor(-16777216);
                    } else {
                        navigationBarFrame.setBackground(null);
                    }
                    scheduleRelayout();
                }
                onSystemBarAppearanceChanged(this.mAppearance);
            }
            return drawLegacyNavigationBarBackground;
        }

        @Override // android.inputmethodservice.NavigationBarController.Callback
        public String toDebugString() {
            return "{mImeDrawsImeNavBar=" + this.mImeDrawsImeNavBar + " mNavigationBarFrame=" + this.mNavigationBarFrame + " mShouldShowImeSwitcherWhenImeIsShown=" + this.mShouldShowImeSwitcherWhenImeIsShown + " mAppearance=0x" + Integer.toHexString(this.mAppearance) + " mDarkIntensity=" + this.mDarkIntensity + " mDrawLegacyNavigationBarBackground=" + this.mDrawLegacyNavigationBarBackground + "}";
        }
    }
}
