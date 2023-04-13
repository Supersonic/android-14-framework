package android.widget;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TabWidget;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.List;
@Deprecated
/* loaded from: classes4.dex */
public class TabHost extends FrameLayout implements ViewTreeObserver.OnTouchModeChangeListener {
    private static final int TABWIDGET_LOCATION_BOTTOM = 3;
    private static final int TABWIDGET_LOCATION_LEFT = 0;
    private static final int TABWIDGET_LOCATION_RIGHT = 2;
    private static final int TABWIDGET_LOCATION_TOP = 1;
    protected int mCurrentTab;
    private View mCurrentView;
    protected LocalActivityManager mLocalActivityManager;
    private OnTabChangeListener mOnTabChangeListener;
    private FrameLayout mTabContent;
    private View.OnKeyListener mTabKeyListener;
    private int mTabLayoutId;
    private List<TabSpec> mTabSpecs;
    private TabWidget mTabWidget;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface ContentStrategy {
        View getContentView();

        void tabClosed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface IndicatorStrategy {
        View createIndicatorView();
    }

    /* loaded from: classes4.dex */
    public interface OnTabChangeListener {
        void onTabChanged(String str);
    }

    /* loaded from: classes4.dex */
    public interface TabContentFactory {
        View createTabContent(String str);
    }

    public TabHost(Context context) {
        super(context);
        this.mTabSpecs = new ArrayList(2);
        this.mCurrentTab = -1;
        this.mCurrentView = null;
        this.mLocalActivityManager = null;
        initTabHost();
    }

    public TabHost(Context context, AttributeSet attrs) {
        this(context, attrs, 16842883);
    }

    public TabHost(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TabHost(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.mTabSpecs = new ArrayList(2);
        this.mCurrentTab = -1;
        this.mCurrentView = null;
        this.mLocalActivityManager = null;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.TabWidget, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.TabWidget, attrs, a, defStyleAttr, defStyleRes);
        this.mTabLayoutId = a.getResourceId(4, 0);
        a.recycle();
        if (this.mTabLayoutId == 0) {
            this.mTabLayoutId = C4057R.layout.tab_indicator_holo;
        }
        initTabHost();
    }

    private void initTabHost() {
        setFocusableInTouchMode(true);
        setDescendantFocusability(262144);
        this.mCurrentTab = -1;
        this.mCurrentView = null;
    }

    public TabSpec newTabSpec(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("tag must be non-null");
        }
        return new TabSpec(tag);
    }

    public void setup() {
        TabWidget tabWidget = (TabWidget) findViewById(16908307);
        this.mTabWidget = tabWidget;
        if (tabWidget == null) {
            throw new RuntimeException("Your TabHost must have a TabWidget whose id attribute is 'android.R.id.tabs'");
        }
        this.mTabKeyListener = new View.OnKeyListener() { // from class: android.widget.TabHost.1
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.isModifierKey(keyCode)) {
                    return false;
                }
                switch (keyCode) {
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 61:
                    case 62:
                    case 66:
                        return false;
                    default:
                        TabHost.this.mTabContent.requestFocus(2);
                        return TabHost.this.mTabContent.dispatchKeyEvent(event);
                }
            }
        };
        this.mTabWidget.setTabSelectionListener(new TabWidget.OnTabSelectionChanged() { // from class: android.widget.TabHost.2
            @Override // android.widget.TabWidget.OnTabSelectionChanged
            public void onTabSelectionChanged(int tabIndex, boolean clicked) {
                TabHost.this.setCurrentTab(tabIndex);
                if (clicked) {
                    TabHost.this.mTabContent.requestFocus(2);
                }
            }
        });
        FrameLayout frameLayout = (FrameLayout) findViewById(16908305);
        this.mTabContent = frameLayout;
        if (frameLayout == null) {
            throw new RuntimeException("Your TabHost must have a FrameLayout whose id attribute is 'android.R.id.tabcontent'");
        }
    }

    @Override // android.view.View
    public void sendAccessibilityEventInternal(int eventType) {
    }

    public void setup(LocalActivityManager activityGroup) {
        setup();
        this.mLocalActivityManager = activityGroup;
    }

    @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
    public void onTouchModeChanged(boolean isInTouchMode) {
    }

    public void addTab(TabSpec tabSpec) {
        if (tabSpec.mIndicatorStrategy == null) {
            throw new IllegalArgumentException("you must specify a way to create the tab indicator.");
        }
        if (tabSpec.mContentStrategy == null) {
            throw new IllegalArgumentException("you must specify a way to create the tab content");
        }
        View tabIndicator = tabSpec.mIndicatorStrategy.createIndicatorView();
        tabIndicator.setOnKeyListener(this.mTabKeyListener);
        if (tabSpec.mIndicatorStrategy instanceof ViewIndicatorStrategy) {
            this.mTabWidget.setStripEnabled(false);
        }
        this.mTabWidget.addView(tabIndicator);
        this.mTabSpecs.add(tabSpec);
        if (this.mCurrentTab == -1) {
            setCurrentTab(0);
        }
    }

    public void clearAllTabs() {
        this.mTabWidget.removeAllViews();
        initTabHost();
        this.mTabContent.removeAllViews();
        this.mTabSpecs.clear();
        requestLayout();
        invalidate();
    }

    public TabWidget getTabWidget() {
        return this.mTabWidget;
    }

    public int getCurrentTab() {
        return this.mCurrentTab;
    }

    public String getCurrentTabTag() {
        int i = this.mCurrentTab;
        if (i >= 0 && i < this.mTabSpecs.size()) {
            return this.mTabSpecs.get(this.mCurrentTab).getTag();
        }
        return null;
    }

    public View getCurrentTabView() {
        int i = this.mCurrentTab;
        if (i >= 0 && i < this.mTabSpecs.size()) {
            return this.mTabWidget.getChildTabViewAt(this.mCurrentTab);
        }
        return null;
    }

    public View getCurrentView() {
        return this.mCurrentView;
    }

    public void setCurrentTabByTag(String tag) {
        int count = this.mTabSpecs.size();
        for (int i = 0; i < count; i++) {
            if (this.mTabSpecs.get(i).getTag().equals(tag)) {
                setCurrentTab(i);
                return;
            }
        }
    }

    public FrameLayout getTabContentView() {
        return this.mTabContent;
    }

    private int getTabWidgetLocation() {
        switch (this.mTabWidget.getOrientation()) {
            case 1:
                int location = this.mTabContent.getLeft() < this.mTabWidget.getLeft() ? 2 : 0;
                return location;
            default:
                int location2 = this.mTabContent.getTop() < this.mTabWidget.getTop() ? 3 : 1;
                return location2;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        View view;
        int keyCodeShouldChangeFocus;
        int directionShouldChangeFocus;
        int soundEffect;
        boolean handled = super.dispatchKeyEvent(event);
        if (!handled && event.getAction() == 0 && (view = this.mCurrentView) != null && view.isRootNamespace() && this.mCurrentView.hasFocus()) {
            switch (getTabWidgetLocation()) {
                case 0:
                    keyCodeShouldChangeFocus = 21;
                    directionShouldChangeFocus = 17;
                    soundEffect = 1;
                    break;
                case 1:
                default:
                    keyCodeShouldChangeFocus = 19;
                    directionShouldChangeFocus = 33;
                    soundEffect = 2;
                    break;
                case 2:
                    keyCodeShouldChangeFocus = 22;
                    directionShouldChangeFocus = 66;
                    soundEffect = 3;
                    break;
                case 3:
                    keyCodeShouldChangeFocus = 20;
                    directionShouldChangeFocus = 130;
                    soundEffect = 4;
                    break;
            }
            if (event.getKeyCode() == keyCodeShouldChangeFocus && this.mCurrentView.findFocus().focusSearch(directionShouldChangeFocus) == null) {
                this.mTabWidget.getChildTabViewAt(this.mCurrentTab).requestFocus();
                playSoundEffect(soundEffect);
                return true;
            }
        }
        return handled;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        View view = this.mCurrentView;
        if (view != null) {
            view.dispatchWindowFocusChanged(hasFocus);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return TabHost.class.getName();
    }

    public void setCurrentTab(int index) {
        int i;
        if (index < 0 || index >= this.mTabSpecs.size() || index == (i = this.mCurrentTab)) {
            return;
        }
        if (i != -1) {
            this.mTabSpecs.get(i).mContentStrategy.tabClosed();
        }
        this.mCurrentTab = index;
        TabSpec spec = this.mTabSpecs.get(index);
        this.mTabWidget.focusCurrentTab(this.mCurrentTab);
        View contentView = spec.mContentStrategy.getContentView();
        this.mCurrentView = contentView;
        if (contentView.getParent() == null) {
            this.mTabContent.addView(this.mCurrentView, new ViewGroup.LayoutParams(-1, -1));
        }
        if (!this.mTabWidget.hasFocus()) {
            this.mCurrentView.requestFocus();
        }
        invokeOnTabChangeListener();
    }

    public void setOnTabChangedListener(OnTabChangeListener l) {
        this.mOnTabChangeListener = l;
    }

    private void invokeOnTabChangeListener() {
        OnTabChangeListener onTabChangeListener = this.mOnTabChangeListener;
        if (onTabChangeListener != null) {
            onTabChangeListener.onTabChanged(getCurrentTabTag());
        }
    }

    /* loaded from: classes4.dex */
    public class TabSpec {
        private ContentStrategy mContentStrategy;
        private IndicatorStrategy mIndicatorStrategy;
        private final String mTag;

        private TabSpec(String tag) {
            this.mTag = tag;
        }

        public TabSpec setIndicator(CharSequence label) {
            this.mIndicatorStrategy = new LabelIndicatorStrategy(label);
            return this;
        }

        public TabSpec setIndicator(CharSequence label, Drawable icon) {
            this.mIndicatorStrategy = new LabelAndIconIndicatorStrategy(label, icon);
            return this;
        }

        public TabSpec setIndicator(View view) {
            this.mIndicatorStrategy = new ViewIndicatorStrategy(view);
            return this;
        }

        public TabSpec setContent(int viewId) {
            this.mContentStrategy = new ViewIdContentStrategy(viewId);
            return this;
        }

        public TabSpec setContent(TabContentFactory contentFactory) {
            this.mContentStrategy = new FactoryContentStrategy(this.mTag, contentFactory);
            return this;
        }

        public TabSpec setContent(Intent intent) {
            this.mContentStrategy = new IntentContentStrategy(this.mTag, intent);
            return this;
        }

        public String getTag() {
            return this.mTag;
        }
    }

    /* loaded from: classes4.dex */
    private class LabelIndicatorStrategy implements IndicatorStrategy {
        private final CharSequence mLabel;

        private LabelIndicatorStrategy(CharSequence label) {
            this.mLabel = label;
        }

        @Override // android.widget.TabHost.IndicatorStrategy
        public View createIndicatorView() {
            Context context = TabHost.this.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View tabIndicator = inflater.inflate(TabHost.this.mTabLayoutId, (ViewGroup) TabHost.this.mTabWidget, false);
            TextView tv = (TextView) tabIndicator.findViewById(16908310);
            tv.setText(this.mLabel);
            if (context.getApplicationInfo().targetSdkVersion <= 4) {
                tabIndicator.setBackgroundResource(C4057R.C4058drawable.tab_indicator_v4);
                tv.setTextColor(context.getColorStateList(C4057R.color.tab_indicator_text_v4));
            }
            return tabIndicator;
        }
    }

    /* loaded from: classes4.dex */
    private class LabelAndIconIndicatorStrategy implements IndicatorStrategy {
        private final Drawable mIcon;
        private final CharSequence mLabel;

        private LabelAndIconIndicatorStrategy(CharSequence label, Drawable icon) {
            this.mLabel = label;
            this.mIcon = icon;
        }

        @Override // android.widget.TabHost.IndicatorStrategy
        public View createIndicatorView() {
            Drawable drawable;
            Context context = TabHost.this.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View tabIndicator = inflater.inflate(TabHost.this.mTabLayoutId, (ViewGroup) TabHost.this.mTabWidget, false);
            TextView tv = (TextView) tabIndicator.findViewById(16908310);
            ImageView iconView = (ImageView) tabIndicator.findViewById(16908294);
            boolean z = true;
            boolean exclusive = iconView.getVisibility() == 8;
            if (exclusive && !TextUtils.isEmpty(this.mLabel)) {
                z = false;
            }
            boolean bindIcon = z;
            tv.setText(this.mLabel);
            if (bindIcon && (drawable = this.mIcon) != null) {
                iconView.setImageDrawable(drawable);
                iconView.setVisibility(0);
            }
            if (context.getApplicationInfo().targetSdkVersion <= 4) {
                tabIndicator.setBackgroundResource(C4057R.C4058drawable.tab_indicator_v4);
                tv.setTextColor(context.getColorStateList(C4057R.color.tab_indicator_text_v4));
            }
            return tabIndicator;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ViewIndicatorStrategy implements IndicatorStrategy {
        private final View mView;

        private ViewIndicatorStrategy(View view) {
            this.mView = view;
        }

        @Override // android.widget.TabHost.IndicatorStrategy
        public View createIndicatorView() {
            return this.mView;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ViewIdContentStrategy implements ContentStrategy {
        private final View mView;

        private ViewIdContentStrategy(int viewId) {
            View findViewById = TabHost.this.mTabContent.findViewById(viewId);
            this.mView = findViewById;
            if (findViewById != null) {
                findViewById.setVisibility(8);
                return;
            }
            throw new RuntimeException("Could not create tab content because could not find view with id " + viewId);
        }

        @Override // android.widget.TabHost.ContentStrategy
        public View getContentView() {
            this.mView.setVisibility(0);
            return this.mView;
        }

        @Override // android.widget.TabHost.ContentStrategy
        public void tabClosed() {
            this.mView.setVisibility(8);
        }
    }

    /* loaded from: classes4.dex */
    private class FactoryContentStrategy implements ContentStrategy {
        private TabContentFactory mFactory;
        private View mTabContent;
        private final CharSequence mTag;

        public FactoryContentStrategy(CharSequence tag, TabContentFactory factory) {
            this.mTag = tag;
            this.mFactory = factory;
        }

        @Override // android.widget.TabHost.ContentStrategy
        public View getContentView() {
            if (this.mTabContent == null) {
                this.mTabContent = this.mFactory.createTabContent(this.mTag.toString());
            }
            this.mTabContent.setVisibility(0);
            return this.mTabContent;
        }

        @Override // android.widget.TabHost.ContentStrategy
        public void tabClosed() {
            this.mTabContent.setVisibility(8);
        }
    }

    /* loaded from: classes4.dex */
    private class IntentContentStrategy implements ContentStrategy {
        private final Intent mIntent;
        private View mLaunchedView;
        private final String mTag;

        private IntentContentStrategy(String tag, Intent intent) {
            this.mTag = tag;
            this.mIntent = intent;
        }

        @Override // android.widget.TabHost.ContentStrategy
        public View getContentView() {
            if (TabHost.this.mLocalActivityManager == null) {
                throw new IllegalStateException("Did you forget to call 'public void setup(LocalActivityManager activityGroup)'?");
            }
            Window w = TabHost.this.mLocalActivityManager.startActivity(this.mTag, this.mIntent);
            View wd = w != null ? w.getDecorView() : null;
            View view = this.mLaunchedView;
            if (view != wd && view != null && view.getParent() != null) {
                TabHost.this.mTabContent.removeView(this.mLaunchedView);
            }
            this.mLaunchedView = wd;
            if (wd != null) {
                wd.setVisibility(0);
                this.mLaunchedView.setFocusableInTouchMode(true);
                ((ViewGroup) this.mLaunchedView).setDescendantFocusability(262144);
            }
            return this.mLaunchedView;
        }

        @Override // android.widget.TabHost.ContentStrategy
        public void tabClosed() {
            View view = this.mLaunchedView;
            if (view != null) {
                view.setVisibility(8);
            }
        }
    }
}
