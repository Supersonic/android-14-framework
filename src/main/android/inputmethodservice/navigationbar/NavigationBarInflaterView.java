package android.inputmethodservice.navigationbar;

import android.content.Context;
import android.content.res.Configuration;
import android.inputmethodservice.navigationbar.ReverseLinearLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import com.android.internal.C4057R;
/* loaded from: classes2.dex */
public final class NavigationBarInflaterView extends FrameLayout {
    private static final String ABSOLUTE_SUFFIX = "A";
    private static final String ABSOLUTE_VERTICAL_CENTERED_SUFFIX = "C";
    public static final String BACK = "back";
    public static final String BUTTON_SEPARATOR = ",";
    public static final String CLIPBOARD = "clipboard";
    private static final String CONFIG_NAV_BAR_LAYOUT_HANDLE = "back[70AC];home_handle;ime_switcher[70AC]";
    public static final String CONTEXTUAL = "contextual";
    public static final String GRAVITY_SEPARATOR = ";";
    public static final String HOME = "home";
    public static final String HOME_HANDLE = "home_handle";
    public static final String IME_SWITCHER = "ime_switcher";
    public static final String KEY = "key";
    public static final String KEY_CODE_END = ")";
    public static final String KEY_CODE_START = "(";
    public static final String KEY_IMAGE_DELIM = ":";
    public static final String LEFT = "left";
    public static final String MENU_IME_ROTATE = "menu_ime";
    public static final String NAVSPACE = "space";
    public static final String NAV_BAR_LEFT = "sysui_nav_bar_left";
    public static final String NAV_BAR_RIGHT = "sysui_nav_bar_right";
    public static final String NAV_BAR_VIEWS = "sysui_nav_bar";
    public static final String RECENT = "recent";
    public static final String RIGHT = "right";
    public static final String SIZE_MOD_END = "]";
    public static final String SIZE_MOD_START = "[";
    private static final String TAG = "NavBarInflater";
    private static final String WEIGHT_CENTERED_SUFFIX = "WC";
    private static final String WEIGHT_SUFFIX = "W";
    private boolean mAlternativeOrder;
    SparseArray<ButtonDispatcher> mButtonDispatchers;
    protected FrameLayout mHorizontal;
    protected LayoutInflater mLandscapeInflater;
    private View mLastLandscape;
    private View mLastPortrait;
    protected LayoutInflater mLayoutInflater;

    public NavigationBarInflaterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createInflaters();
    }

    void createInflaters() {
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        Configuration landscape = new Configuration();
        landscape.setTo(this.mContext.getResources().getConfiguration());
        landscape.orientation = 2;
        this.mLandscapeInflater = LayoutInflater.from(this.mContext.createConfigurationContext(landscape));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        inflateChildren();
        clearViews();
        inflateLayout(getDefaultLayout());
    }

    private void inflateChildren() {
        removeAllViews();
        FrameLayout frameLayout = (FrameLayout) this.mLayoutInflater.inflate(C4057R.layout.input_method_navigation_layout, (ViewGroup) this, false);
        this.mHorizontal = frameLayout;
        addView(frameLayout);
        updateAlternativeOrder();
    }

    String getDefaultLayout() {
        return CONFIG_NAV_BAR_LAYOUT_HANDLE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setButtonDispatchers(SparseArray<ButtonDispatcher> buttonDispatchers) {
        this.mButtonDispatchers = buttonDispatchers;
        for (int i = 0; i < buttonDispatchers.size(); i++) {
            initiallyFill(buttonDispatchers.valueAt(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateButtonDispatchersCurrentView() {
        if (this.mButtonDispatchers != null) {
            View view = this.mHorizontal;
            for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
                ButtonDispatcher dispatcher = this.mButtonDispatchers.valueAt(i);
                dispatcher.setCurrentView(view);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAlternativeOrder(boolean alternativeOrder) {
        if (alternativeOrder != this.mAlternativeOrder) {
            this.mAlternativeOrder = alternativeOrder;
            updateAlternativeOrder();
        }
    }

    private void updateAlternativeOrder() {
        updateAlternativeOrder(this.mHorizontal.findViewById(C4057R.C4059id.input_method_nav_ends_group));
        updateAlternativeOrder(this.mHorizontal.findViewById(C4057R.C4059id.input_method_nav_center_group));
    }

    private void updateAlternativeOrder(View v) {
        if (v instanceof ReverseLinearLayout) {
            ((ReverseLinearLayout) v).setAlternativeOrder(this.mAlternativeOrder);
        }
    }

    private void initiallyFill(ButtonDispatcher buttonDispatcher) {
        addAll(buttonDispatcher, (ViewGroup) this.mHorizontal.findViewById(C4057R.C4059id.input_method_nav_ends_group));
        addAll(buttonDispatcher, (ViewGroup) this.mHorizontal.findViewById(C4057R.C4059id.input_method_nav_center_group));
    }

    private void addAll(ButtonDispatcher buttonDispatcher, ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i).getId() == buttonDispatcher.getId()) {
                buttonDispatcher.addView(parent.getChildAt(i));
            }
            if (parent.getChildAt(i) instanceof ViewGroup) {
                addAll(buttonDispatcher, (ViewGroup) parent.getChildAt(i));
            }
        }
    }

    protected void inflateLayout(String newLayout) {
        if (newLayout == null) {
            newLayout = getDefaultLayout();
        }
        String[] sets = newLayout.split(GRAVITY_SEPARATOR, 3);
        if (sets.length != 3) {
            Log.m112d(TAG, "Invalid layout.");
            sets = getDefaultLayout().split(GRAVITY_SEPARATOR, 3);
        }
        String[] start = sets[0].split(",");
        String[] center = sets[1].split(",");
        String[] end = sets[2].split(",");
        inflateButtons(start, (ViewGroup) this.mHorizontal.findViewById(C4057R.C4059id.input_method_nav_ends_group), false, true);
        inflateButtons(center, (ViewGroup) this.mHorizontal.findViewById(C4057R.C4059id.input_method_nav_center_group), false, false);
        addGravitySpacer((LinearLayout) this.mHorizontal.findViewById(C4057R.C4059id.input_method_nav_ends_group));
        inflateButtons(end, (ViewGroup) this.mHorizontal.findViewById(C4057R.C4059id.input_method_nav_ends_group), false, false);
        updateButtonDispatchersCurrentView();
    }

    private void addGravitySpacer(LinearLayout layout) {
        layout.addView(new Space(this.mContext), new LinearLayout.LayoutParams(0, 0, 1.0f));
    }

    private void inflateButtons(String[] buttons, ViewGroup parent, boolean landscape, boolean start) {
        for (String str : buttons) {
            inflateButton(str, parent, landscape, start);
        }
    }

    private ViewGroup.LayoutParams copy(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            return new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height, ((LinearLayout.LayoutParams) layoutParams).weight);
        }
        return new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
    }

    protected View inflateButton(String buttonSpec, ViewGroup parent, boolean landscape, boolean start) {
        LayoutInflater inflater = landscape ? this.mLandscapeInflater : this.mLayoutInflater;
        View v = createView(buttonSpec, parent, inflater);
        if (v == null) {
            return null;
        }
        View v2 = applySize(v, buttonSpec, landscape, start);
        parent.addView(v2);
        addToDispatchers(v2);
        View lastView = landscape ? this.mLastLandscape : this.mLastPortrait;
        View accessibilityView = v2;
        if (v2 instanceof ReverseLinearLayout.ReverseRelativeLayout) {
            accessibilityView = ((ReverseLinearLayout.ReverseRelativeLayout) v2).getChildAt(0);
        }
        if (lastView != null) {
            accessibilityView.setAccessibilityTraversalAfter(lastView.getId());
        }
        if (landscape) {
            this.mLastLandscape = accessibilityView;
        } else {
            this.mLastPortrait = accessibilityView;
        }
        return v2;
    }

    private View applySize(View v, String buttonSpec, boolean landscape, boolean start) {
        int gravity;
        String sizeStr = extractSize(buttonSpec);
        if (sizeStr == null) {
            return v;
        }
        if (sizeStr.contains("W") || sizeStr.contains("A")) {
            ReverseLinearLayout.ReverseRelativeLayout frame = new ReverseLinearLayout.ReverseRelativeLayout(this.mContext);
            FrameLayout.LayoutParams childParams = new FrameLayout.LayoutParams(v.getLayoutParams());
            if (landscape) {
                gravity = start ? 48 : 80;
            } else {
                gravity = start ? Gravity.START : Gravity.END;
            }
            if (sizeStr.endsWith(WEIGHT_CENTERED_SUFFIX)) {
                gravity = 17;
            } else if (sizeStr.endsWith("C")) {
                gravity = 16;
            }
            frame.setDefaultGravity(gravity);
            frame.setGravity(gravity);
            frame.addView(v, childParams);
            if (sizeStr.contains("W")) {
                float weight = Float.parseFloat(sizeStr.substring(0, sizeStr.indexOf("W")));
                frame.setLayoutParams(new LinearLayout.LayoutParams(0, -1, weight));
            } else {
                int width = (int) convertDpToPx(this.mContext, Float.parseFloat(sizeStr.substring(0, sizeStr.indexOf("A"))));
                frame.setLayoutParams(new LinearLayout.LayoutParams(width, -1));
            }
            frame.setClipChildren(false);
            frame.setClipToPadding(false);
            return frame;
        }
        float size = Float.parseFloat(sizeStr);
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.width = (int) (params.width * size);
        return v;
    }

    View createView(String buttonSpec, ViewGroup parent, LayoutInflater inflater) {
        String button = extractButton(buttonSpec);
        if (LEFT.equals(button)) {
            button = extractButton(NAVSPACE);
        } else if (RIGHT.equals(button)) {
            button = extractButton(MENU_IME_ROTATE);
        }
        if ("home".equals(button)) {
            return null;
        }
        if (BACK.equals(button)) {
            View v = inflater.inflate(C4057R.layout.input_method_nav_back, parent, false);
            return v;
        } else if (RECENT.equals(button) || MENU_IME_ROTATE.equals(button) || NAVSPACE.equals(button) || "clipboard".equals(button) || CONTEXTUAL.equals(button)) {
            return null;
        } else {
            if (HOME_HANDLE.equals(button)) {
                View v2 = inflater.inflate(C4057R.layout.input_method_nav_home_handle, parent, false);
                return v2;
            } else if (IME_SWITCHER.equals(button)) {
                View v3 = inflater.inflate(C4057R.layout.input_method_nav_ime_switcher, parent, false);
                return v3;
            } else {
                button.startsWith("key");
                return null;
            }
        }
    }

    private static String extractSize(String buttonSpec) {
        if (!buttonSpec.contains(SIZE_MOD_START)) {
            return null;
        }
        int sizeStart = buttonSpec.indexOf(SIZE_MOD_START);
        return buttonSpec.substring(sizeStart + 1, buttonSpec.indexOf(SIZE_MOD_END));
    }

    private static String extractButton(String buttonSpec) {
        if (!buttonSpec.contains(SIZE_MOD_START)) {
            return buttonSpec;
        }
        return buttonSpec.substring(0, buttonSpec.indexOf(SIZE_MOD_START));
    }

    private void addToDispatchers(View v) {
        SparseArray<ButtonDispatcher> sparseArray = this.mButtonDispatchers;
        if (sparseArray != null) {
            int indexOfKey = sparseArray.indexOfKey(v.getId());
            if (indexOfKey >= 0) {
                this.mButtonDispatchers.valueAt(indexOfKey).addView(v);
            }
            if (v instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) v;
                int numChildViews = viewGroup.getChildCount();
                for (int i = 0; i < numChildViews; i++) {
                    addToDispatchers(viewGroup.getChildAt(i));
                }
            }
        }
    }

    private void clearViews() {
        if (this.mButtonDispatchers != null) {
            for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
                this.mButtonDispatchers.valueAt(i).clear();
            }
        }
        clearAllChildren((ViewGroup) this.mHorizontal.findViewById(C4057R.C4059id.input_method_nav_buttons));
    }

    private void clearAllChildren(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            ((ViewGroup) group.getChildAt(i)).removeAllViews();
        }
    }

    private static float convertDpToPx(Context context, float dp) {
        return context.getResources().getDisplayMetrics().density * dp;
    }
}
