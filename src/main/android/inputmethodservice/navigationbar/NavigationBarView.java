package android.inputmethodservice.navigationbar;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import com.android.internal.C4057R;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public final class NavigationBarView extends FrameLayout {
    private static final boolean DEBUG = false;
    private static final Interpolator FAST_OUT_SLOW_IN = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    private static final String TAG = "NavBarView";
    private KeyButtonDrawable mBackIcon;
    private final SparseArray<ButtonDispatcher> mButtonDispatchers;
    private Configuration mConfiguration;
    private int mCurrentRotation;
    View mCurrentView;
    private final int mDarkIconColor;
    private final DeadZone mDeadZone;
    private boolean mDeadZoneConsuming;
    int mDisabledFlags;
    private View mHorizontal;
    private KeyButtonDrawable mImeSwitcherIcon;
    private Context mLightContext;
    private final int mLightIconColor;
    private final int mNavBarMode;
    int mNavigationIconHints;
    private NavigationBarInflaterView mNavigationInflaterView;
    private Configuration mTmpLastConfiguration;

    public NavigationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentView = null;
        this.mCurrentRotation = -1;
        this.mDisabledFlags = 0;
        this.mNavigationIconHints = 1;
        this.mNavBarMode = 2;
        this.mDeadZoneConsuming = false;
        SparseArray<ButtonDispatcher> sparseArray = new SparseArray<>();
        this.mButtonDispatchers = sparseArray;
        this.mLightContext = context;
        this.mLightIconColor = -1;
        this.mDarkIconColor = -1728053248;
        this.mConfiguration = new Configuration();
        this.mTmpLastConfiguration = new Configuration();
        this.mConfiguration.updateFrom(context.getResources().getConfiguration());
        sparseArray.put(C4057R.C4059id.input_method_nav_back, new ButtonDispatcher(C4057R.C4059id.input_method_nav_back));
        sparseArray.put(C4057R.C4059id.input_method_nav_ime_switcher, new ButtonDispatcher(C4057R.C4059id.input_method_nav_ime_switcher));
        sparseArray.put(C4057R.C4059id.input_method_nav_home_handle, new ButtonDispatcher(C4057R.C4059id.input_method_nav_home_handle));
        this.mDeadZone = new DeadZone(this);
        getImeSwitchButton().setOnClickListener(new View.OnClickListener() { // from class: android.inputmethodservice.navigationbar.NavigationBarView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ((InputMethodManager) view.getContext().getSystemService(InputMethodManager.class)).showInputMethodPicker();
            }
        });
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return shouldDeadZoneConsumeTouchEvents(event) || super.onInterceptTouchEvent(event);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        shouldDeadZoneConsumeTouchEvents(event);
        return super.onTouchEvent(event);
    }

    private boolean shouldDeadZoneConsumeTouchEvents(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == 0) {
            this.mDeadZoneConsuming = false;
        }
        if (!this.mDeadZone.onTouchEvent(event) && !this.mDeadZoneConsuming) {
            return false;
        }
        switch (action) {
            case 0:
                this.mDeadZoneConsuming = true;
                break;
            case 1:
            case 3:
                this.mDeadZoneConsuming = false;
                break;
        }
        return true;
    }

    public View getCurrentView() {
        return this.mCurrentView;
    }

    public void forEachView(Consumer<View> consumer) {
        View view = this.mHorizontal;
        if (view != null) {
            consumer.accept(view);
        }
    }

    public ButtonDispatcher getBackButton() {
        return this.mButtonDispatchers.get(C4057R.C4059id.input_method_nav_back);
    }

    public ButtonDispatcher getImeSwitchButton() {
        return this.mButtonDispatchers.get(C4057R.C4059id.input_method_nav_ime_switcher);
    }

    public ButtonDispatcher getHomeHandle() {
        return this.mButtonDispatchers.get(C4057R.C4059id.input_method_nav_home_handle);
    }

    public SparseArray<ButtonDispatcher> getButtonDispatchers() {
        return this.mButtonDispatchers;
    }

    private void reloadNavIcons() {
        updateIcons(Configuration.EMPTY);
    }

    private void updateIcons(Configuration oldConfig) {
        boolean orientationChange = oldConfig.orientation != this.mConfiguration.orientation;
        boolean densityChange = oldConfig.densityDpi != this.mConfiguration.densityDpi;
        boolean dirChange = oldConfig.getLayoutDirection() != this.mConfiguration.getLayoutDirection();
        if (densityChange || dirChange) {
            this.mImeSwitcherIcon = getDrawable(C4057R.C4058drawable.ic_ime_switcher);
        }
        if (orientationChange || densityChange || dirChange) {
            this.mBackIcon = getBackDrawable();
        }
    }

    private KeyButtonDrawable getBackDrawable() {
        KeyButtonDrawable drawable = getDrawable(C4057R.C4058drawable.ic_ime_nav_back);
        orientBackButton(drawable);
        return drawable;
    }

    public static boolean isGesturalMode(int mode) {
        return mode == 2;
    }

    private void orientBackButton(KeyButtonDrawable drawable) {
        float degrees;
        boolean useAltBack = (this.mNavigationIconHints & 1) != 0;
        boolean isRtl = this.mConfiguration.getLayoutDirection() == 1;
        float targetY = 0.0f;
        if (useAltBack) {
            degrees = isRtl ? 90 : -90;
        } else {
            degrees = 0.0f;
        }
        if (drawable.getRotation() == degrees) {
            return;
        }
        if (isGesturalMode(2)) {
            drawable.setRotation(degrees);
            return;
        }
        if (useAltBack) {
            targetY = -NavigationBarUtils.dpToPx(2.0f, getResources());
        }
        ObjectAnimator navBarAnimator = ObjectAnimator.ofPropertyValuesHolder(drawable, PropertyValuesHolder.ofFloat(KeyButtonDrawable.KEY_DRAWABLE_ROTATE, degrees), PropertyValuesHolder.ofFloat(KeyButtonDrawable.KEY_DRAWABLE_TRANSLATE_Y, targetY));
        navBarAnimator.setInterpolator(FAST_OUT_SLOW_IN);
        navBarAnimator.setDuration(200L);
        navBarAnimator.start();
    }

    private KeyButtonDrawable getDrawable(int icon) {
        return KeyButtonDrawable.create(this.mLightContext, this.mLightIconColor, this.mDarkIconColor, icon, true, null);
    }

    @Override // android.view.View
    public void setLayoutDirection(int layoutDirection) {
        reloadNavIcons();
        super.setLayoutDirection(layoutDirection);
    }

    public void setNavigationIconHints(int hints) {
        int i = this.mNavigationIconHints;
        if (hints == i) {
            return;
        }
        if ((hints & 1) != 0) {
        }
        boolean z = (i & 1) != 0;
        this.mNavigationIconHints = hints;
        updateNavButtonIcons();
    }

    private void updateNavButtonIcons() {
        KeyButtonDrawable backIcon = this.mBackIcon;
        orientBackButton(backIcon);
        getBackButton().setImageDrawable(backIcon);
        getImeSwitchButton().setImageDrawable(this.mImeSwitcherIcon);
        boolean imeSwitcherVisible = (this.mNavigationIconHints & 4) != 0;
        getImeSwitchButton().setVisibility(imeSwitcherVisible ? 0 : 4);
        getBackButton().setVisibility(0);
        getHomeHandle().setVisibility(4);
    }

    private Display getContextDisplay() {
        return getContext().getDisplay();
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        NavigationBarInflaterView navigationBarInflaterView = (NavigationBarInflaterView) findViewById(C4057R.C4059id.input_method_nav_inflater);
        this.mNavigationInflaterView = navigationBarInflaterView;
        navigationBarInflaterView.setButtonDispatchers(this.mButtonDispatchers);
        updateOrientationViews();
        reloadNavIcons();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        this.mDeadZone.onDraw(canvas);
        super.onDraw(canvas);
    }

    private void updateOrientationViews() {
        this.mHorizontal = findViewById(C4057R.C4059id.input_method_nav_horizontal);
        updateCurrentView();
    }

    private void updateCurrentView() {
        resetViews();
        View view = this.mHorizontal;
        this.mCurrentView = view;
        view.setVisibility(0);
        int rotation = getContextDisplay().getRotation();
        this.mCurrentRotation = rotation;
        this.mNavigationInflaterView.setAlternativeOrder(rotation == 1);
        this.mNavigationInflaterView.updateButtonDispatchersCurrentView();
    }

    private void resetViews() {
        this.mHorizontal.setVisibility(8);
    }

    private void reorient() {
        updateCurrentView();
        NavigationBarFrame frame = (NavigationBarFrame) getRootView().findViewByPredicate(new Predicate() { // from class: android.inputmethodservice.navigationbar.NavigationBarView$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return NavigationBarView.lambda$reorient$1((View) obj);
            }
        });
        frame.setDeadZone(this.mDeadZone);
        this.mDeadZone.onConfigurationChanged(this.mCurrentRotation);
        if (!isLayoutDirectionResolved()) {
            resolveLayoutDirection();
        }
        updateNavButtonIcons();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$reorient$1(View view) {
        return view instanceof NavigationBarFrame;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mTmpLastConfiguration.updateFrom(this.mConfiguration);
        this.mConfiguration.updateFrom(newConfig);
        updateIcons(this.mTmpLastConfiguration);
        if (this.mTmpLastConfiguration.densityDpi != this.mConfiguration.densityDpi || this.mTmpLastConfiguration.getLayoutDirection() != this.mConfiguration.getLayoutDirection()) {
            updateNavButtonIcons();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestApplyInsets();
        reorient();
        updateNavButtonIcons();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
            this.mButtonDispatchers.valueAt(i).onDestroy();
        }
    }

    public void setDarkIntensity(float intensity) {
        for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
            this.mButtonDispatchers.valueAt(i).setDarkIntensity(intensity);
        }
    }
}
