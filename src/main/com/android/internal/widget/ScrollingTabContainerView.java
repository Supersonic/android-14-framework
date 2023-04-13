package com.android.internal.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.android.internal.view.ActionBarPolicy;
/* loaded from: classes5.dex */
public class ScrollingTabContainerView extends HorizontalScrollView implements AdapterView.OnItemClickListener {
    private static final int FADE_DURATION = 200;
    private static final String TAG = "ScrollingTabContainerView";
    private static final TimeInterpolator sAlphaInterpolator = new DecelerateInterpolator();
    private boolean mAllowCollapse;
    private int mContentHeight;
    int mMaxTabWidth;
    private int mSelectedTabIndex;
    int mStackedTabMaxWidth;
    private TabClickListener mTabClickListener;
    private LinearLayout mTabLayout;
    Runnable mTabSelector;
    private Spinner mTabSpinner;
    protected final VisibilityAnimListener mVisAnimListener;
    protected Animator mVisibilityAnim;

    public ScrollingTabContainerView(Context context) {
        super(context);
        this.mVisAnimListener = new VisibilityAnimListener();
        setHorizontalScrollBarEnabled(false);
        ActionBarPolicy abp = ActionBarPolicy.get(context);
        setContentHeight(abp.getTabContainerHeight());
        this.mStackedTabMaxWidth = abp.getStackedTabMaxWidth();
        LinearLayout createTabLayout = createTabLayout();
        this.mTabLayout = createTabLayout;
        addView(createTabLayout, new ViewGroup.LayoutParams(-2, -1));
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        boolean canCollapse = true;
        boolean lockedExpanded = widthMode == 1073741824;
        setFillViewport(lockedExpanded);
        int childCount = this.mTabLayout.getChildCount();
        if (childCount > 1 && (widthMode == 1073741824 || widthMode == Integer.MIN_VALUE)) {
            if (childCount <= 2) {
                this.mMaxTabWidth = View.MeasureSpec.getSize(widthMeasureSpec) / 2;
            } else {
                this.mMaxTabWidth = (int) (View.MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
            }
            this.mMaxTabWidth = Math.min(this.mMaxTabWidth, this.mStackedTabMaxWidth);
        } else {
            this.mMaxTabWidth = -1;
        }
        int heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(this.mContentHeight, 1073741824);
        if (lockedExpanded || !this.mAllowCollapse) {
            canCollapse = false;
        }
        if (canCollapse) {
            this.mTabLayout.measure(0, heightMeasureSpec2);
            if (this.mTabLayout.getMeasuredWidth() > View.MeasureSpec.getSize(widthMeasureSpec)) {
                performCollapse();
            } else {
                performExpand();
            }
        } else {
            performExpand();
        }
        int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec2);
        int newWidth = getMeasuredWidth();
        if (lockedExpanded && oldWidth != newWidth) {
            setTabSelected(this.mSelectedTabIndex);
        }
    }

    private boolean isCollapsed() {
        Spinner spinner = this.mTabSpinner;
        return spinner != null && spinner.getParent() == this;
    }

    public void setAllowCollapse(boolean allowCollapse) {
        this.mAllowCollapse = allowCollapse;
    }

    private void performCollapse() {
        if (isCollapsed()) {
            return;
        }
        if (this.mTabSpinner == null) {
            this.mTabSpinner = createSpinner();
        }
        removeView(this.mTabLayout);
        addView(this.mTabSpinner, new ViewGroup.LayoutParams(-2, -1));
        if (this.mTabSpinner.getAdapter() == null) {
            TabAdapter adapter = new TabAdapter(this.mContext);
            adapter.setDropDownViewContext(this.mTabSpinner.getPopupContext());
            this.mTabSpinner.setAdapter((SpinnerAdapter) adapter);
        }
        Runnable runnable = this.mTabSelector;
        if (runnable != null) {
            removeCallbacks(runnable);
            this.mTabSelector = null;
        }
        this.mTabSpinner.setSelection(this.mSelectedTabIndex);
    }

    private boolean performExpand() {
        if (isCollapsed()) {
            removeView(this.mTabSpinner);
            addView(this.mTabLayout, new ViewGroup.LayoutParams(-2, -1));
            setTabSelected(this.mTabSpinner.getSelectedItemPosition());
            return false;
        }
        return false;
    }

    public void setTabSelected(int position) {
        this.mSelectedTabIndex = position;
        int tabCount = this.mTabLayout.getChildCount();
        int i = 0;
        while (i < tabCount) {
            View child = this.mTabLayout.getChildAt(i);
            boolean isSelected = i == position;
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(position);
            }
            i++;
        }
        Spinner spinner = this.mTabSpinner;
        if (spinner != null && position >= 0) {
            spinner.setSelection(position);
        }
    }

    public void setContentHeight(int contentHeight) {
        this.mContentHeight = contentHeight;
        requestLayout();
    }

    private LinearLayout createTabLayout() {
        LinearLayout tabLayout = new LinearLayout(getContext(), null, 16843508);
        tabLayout.setMeasureWithLargestChildEnabled(true);
        tabLayout.setGravity(17);
        tabLayout.setLayoutParams(new LinearLayout.LayoutParams(-2, -1));
        return tabLayout;
    }

    private Spinner createSpinner() {
        Spinner spinner = new Spinner(getContext(), null, 16843479);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(-2, -1));
        spinner.setOnItemClickListenerInt(this);
        return spinner;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionBarPolicy abp = ActionBarPolicy.get(getContext());
        setContentHeight(abp.getTabContainerHeight());
        this.mStackedTabMaxWidth = abp.getStackedTabMaxWidth();
    }

    public void animateToVisibility(int visibility) {
        Animator animator = this.mVisibilityAnim;
        if (animator != null) {
            animator.cancel();
        }
        if (visibility == 0) {
            if (getVisibility() != 0) {
                setAlpha(0.0f);
            }
            ObjectAnimator anim = ObjectAnimator.ofFloat(this, "alpha", 1.0f);
            anim.setDuration(200L);
            anim.setInterpolator(sAlphaInterpolator);
            anim.addListener(this.mVisAnimListener.withFinalVisibility(visibility));
            anim.start();
            return;
        }
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(this, "alpha", 0.0f);
        anim2.setDuration(200L);
        anim2.setInterpolator(sAlphaInterpolator);
        anim2.addListener(this.mVisAnimListener.withFinalVisibility(visibility));
        anim2.start();
    }

    public void animateToTab(int position) {
        final View tabView = this.mTabLayout.getChildAt(position);
        Runnable runnable = this.mTabSelector;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
        Runnable runnable2 = new Runnable() { // from class: com.android.internal.widget.ScrollingTabContainerView.1
            @Override // java.lang.Runnable
            public void run() {
                int scrollPos = tabView.getLeft() - ((ScrollingTabContainerView.this.getWidth() - tabView.getWidth()) / 2);
                ScrollingTabContainerView.this.smoothScrollTo(scrollPos, 0);
                ScrollingTabContainerView.this.mTabSelector = null;
            }
        };
        this.mTabSelector = runnable2;
        post(runnable2);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Runnable runnable = this.mTabSelector;
        if (runnable != null) {
            post(runnable);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Runnable runnable = this.mTabSelector;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TabView createTabView(Context context, ActionBar.Tab tab, boolean forAdapter) {
        TabView tabView = new TabView(context, tab, forAdapter);
        if (forAdapter) {
            tabView.setBackgroundDrawable(null);
            tabView.setLayoutParams(new AbsListView.LayoutParams(-1, this.mContentHeight));
        } else {
            tabView.setFocusable(true);
            if (this.mTabClickListener == null) {
                this.mTabClickListener = new TabClickListener();
            }
            tabView.setOnClickListener(this.mTabClickListener);
        }
        return tabView;
    }

    public void addTab(ActionBar.Tab tab, boolean setSelected) {
        TabView tabView = createTabView(this.mContext, tab, false);
        this.mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, -1, 1.0f));
        Spinner spinner = this.mTabSpinner;
        if (spinner != null) {
            ((TabAdapter) spinner.getAdapter()).notifyDataSetChanged();
        }
        if (setSelected) {
            tabView.setSelected(true);
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void addTab(ActionBar.Tab tab, int position, boolean setSelected) {
        TabView tabView = createTabView(this.mContext, tab, false);
        this.mTabLayout.addView(tabView, position, new LinearLayout.LayoutParams(0, -1, 1.0f));
        Spinner spinner = this.mTabSpinner;
        if (spinner != null) {
            ((TabAdapter) spinner.getAdapter()).notifyDataSetChanged();
        }
        if (setSelected) {
            tabView.setSelected(true);
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void updateTab(int position) {
        ((TabView) this.mTabLayout.getChildAt(position)).update();
        Spinner spinner = this.mTabSpinner;
        if (spinner != null) {
            ((TabAdapter) spinner.getAdapter()).notifyDataSetChanged();
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void removeTabAt(int position) {
        this.mTabLayout.removeViewAt(position);
        Spinner spinner = this.mTabSpinner;
        if (spinner != null) {
            ((TabAdapter) spinner.getAdapter()).notifyDataSetChanged();
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void removeAllTabs() {
        this.mTabLayout.removeAllViews();
        Spinner spinner = this.mTabSpinner;
        if (spinner != null) {
            ((TabAdapter) spinner.getAdapter()).notifyDataSetChanged();
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TabView tabView = (TabView) view;
        tabView.getTab().select();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class TabView extends LinearLayout {
        private View mCustomView;
        private ImageView mIconView;
        private ActionBar.Tab mTab;
        private TextView mTextView;

        public TabView(Context context, ActionBar.Tab tab, boolean forList) {
            super(context, null, 16843507);
            this.mTab = tab;
            if (forList) {
                setGravity(8388627);
            }
            update();
        }

        public void bindTab(ActionBar.Tab tab) {
            this.mTab = tab;
            update();
        }

        @Override // android.view.View
        public void setSelected(boolean selected) {
            boolean changed = isSelected() != selected;
            super.setSelected(selected);
            if (changed && selected) {
                sendAccessibilityEvent(4);
            }
        }

        @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
        public CharSequence getAccessibilityClassName() {
            return ActionBar.Tab.class.getName();
        }

        @Override // android.widget.LinearLayout, android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (ScrollingTabContainerView.this.mMaxTabWidth > 0 && getMeasuredWidth() > ScrollingTabContainerView.this.mMaxTabWidth) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(ScrollingTabContainerView.this.mMaxTabWidth, 1073741824), heightMeasureSpec);
            }
        }

        public void update() {
            ActionBar.Tab tab = this.mTab;
            View custom = tab.getCustomView();
            if (custom != null) {
                ViewParent customParent = custom.getParent();
                if (customParent != this) {
                    if (customParent != null) {
                        ((ViewGroup) customParent).removeView(custom);
                    }
                    addView(custom);
                }
                this.mCustomView = custom;
                TextView textView = this.mTextView;
                if (textView != null) {
                    textView.setVisibility(8);
                }
                ImageView imageView = this.mIconView;
                if (imageView != null) {
                    imageView.setVisibility(8);
                    this.mIconView.setImageDrawable(null);
                    return;
                }
                return;
            }
            View view = this.mCustomView;
            if (view != null) {
                removeView(view);
                this.mCustomView = null;
            }
            Drawable icon = tab.getIcon();
            CharSequence text = tab.getText();
            if (icon != null) {
                if (this.mIconView == null) {
                    ImageView iconView = new ImageView(getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
                    lp.gravity = 16;
                    iconView.setLayoutParams(lp);
                    addView(iconView, 0);
                    this.mIconView = iconView;
                }
                this.mIconView.setImageDrawable(icon);
                this.mIconView.setVisibility(0);
            } else {
                ImageView imageView2 = this.mIconView;
                if (imageView2 != null) {
                    imageView2.setVisibility(8);
                    this.mIconView.setImageDrawable(null);
                }
            }
            boolean hasText = !TextUtils.isEmpty(text);
            if (hasText) {
                if (this.mTextView == null) {
                    TextView textView2 = new TextView(getContext(), null, 16843509);
                    textView2.setEllipsize(TextUtils.TruncateAt.END);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-2, -2);
                    lp2.gravity = 16;
                    textView2.setLayoutParams(lp2);
                    addView(textView2);
                    this.mTextView = textView2;
                }
                this.mTextView.setText(text);
                this.mTextView.setVisibility(0);
            } else {
                TextView textView3 = this.mTextView;
                if (textView3 != null) {
                    textView3.setVisibility(8);
                    this.mTextView.setText((CharSequence) null);
                }
            }
            ImageView imageView3 = this.mIconView;
            if (imageView3 != null) {
                imageView3.setContentDescription(tab.getContentDescription());
            }
            setTooltipText(hasText ? null : tab.getContentDescription());
        }

        public ActionBar.Tab getTab() {
            return this.mTab;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class TabAdapter extends BaseAdapter {
        private Context mDropDownContext;

        public TabAdapter(Context context) {
            setDropDownViewContext(context);
        }

        public void setDropDownViewContext(Context context) {
            this.mDropDownContext = context;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return ScrollingTabContainerView.this.mTabLayout.getChildCount();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return ((TabView) ScrollingTabContainerView.this.mTabLayout.getChildAt(position)).getTab();
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ScrollingTabContainerView scrollingTabContainerView = ScrollingTabContainerView.this;
                return scrollingTabContainerView.createTabView(scrollingTabContainerView.mContext, (ActionBar.Tab) getItem(position), true);
            }
            ((TabView) convertView).bindTab((ActionBar.Tab) getItem(position));
            return convertView;
        }

        @Override // android.widget.BaseAdapter, android.widget.SpinnerAdapter
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                return ScrollingTabContainerView.this.createTabView(this.mDropDownContext, (ActionBar.Tab) getItem(position), true);
            }
            ((TabView) convertView).bindTab((ActionBar.Tab) getItem(position));
            return convertView;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class TabClickListener implements View.OnClickListener {
        private TabClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            tabView.getTab().select();
            int tabCount = ScrollingTabContainerView.this.mTabLayout.getChildCount();
            for (int i = 0; i < tabCount; i++) {
                View child = ScrollingTabContainerView.this.mTabLayout.getChildAt(i);
                child.setSelected(child == view);
            }
        }
    }

    /* loaded from: classes5.dex */
    protected class VisibilityAnimListener implements Animator.AnimatorListener {
        private boolean mCanceled = false;
        private int mFinalVisibility;

        protected VisibilityAnimListener() {
        }

        public VisibilityAnimListener withFinalVisibility(int visibility) {
            this.mFinalVisibility = visibility;
            return this;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
            ScrollingTabContainerView.this.setVisibility(0);
            ScrollingTabContainerView.this.mVisibilityAnim = animation;
            this.mCanceled = false;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            if (this.mCanceled) {
                return;
            }
            ScrollingTabContainerView.this.mVisibilityAnim = null;
            ScrollingTabContainerView.this.setVisibility(this.mFinalVisibility);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            this.mCanceled = true;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animation) {
        }
    }
}
