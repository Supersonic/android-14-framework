package com.android.internal.view.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.view.menu.MenuView;
/* loaded from: classes2.dex */
public class ListMenuItemView extends LinearLayout implements MenuView.ItemView, AbsListView.SelectionBoundsAdjuster {
    private static final String TAG = "ListMenuItemView";
    private Drawable mBackground;
    private CheckBox mCheckBox;
    private LinearLayout mContent;
    private boolean mForceShowIcon;
    private ImageView mGroupDivider;
    private boolean mHasListDivider;
    private ImageView mIconView;
    private LayoutInflater mInflater;
    private MenuItemImpl mItemData;
    private int mMenuType;
    private boolean mPreserveIconSpacing;
    private RadioButton mRadioButton;
    private TextView mShortcutView;
    private Drawable mSubMenuArrow;
    private ImageView mSubMenuArrowView;
    private int mTextAppearance;
    private Context mTextAppearanceContext;
    private TextView mTitleView;

    public ListMenuItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.MenuView, defStyleAttr, defStyleRes);
        this.mBackground = a.getDrawable(5);
        this.mTextAppearance = a.getResourceId(1, -1);
        this.mPreserveIconSpacing = a.getBoolean(8, false);
        this.mTextAppearanceContext = context;
        this.mSubMenuArrow = a.getDrawable(7);
        TypedArray b = context.getTheme().obtainStyledAttributes(null, new int[]{16843049}, 16842861, 0);
        this.mHasListDivider = b.hasValue(0);
        a.recycle();
        b.recycle();
    }

    public ListMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ListMenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 16844018);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        setBackgroundDrawable(this.mBackground);
        TextView textView = (TextView) findViewById(16908310);
        this.mTitleView = textView;
        int i = this.mTextAppearance;
        if (i != -1) {
            textView.setTextAppearance(this.mTextAppearanceContext, i);
        }
        this.mShortcutView = (TextView) findViewById(C4057R.C4059id.shortcut);
        ImageView imageView = (ImageView) findViewById(C4057R.C4059id.submenuarrow);
        this.mSubMenuArrowView = imageView;
        if (imageView != null) {
            imageView.setImageDrawable(this.mSubMenuArrow);
        }
        this.mGroupDivider = (ImageView) findViewById(C4057R.C4059id.group_divider);
        this.mContent = (LinearLayout) findViewById(16908290);
    }

    @Override // com.android.internal.view.menu.MenuView.ItemView
    public void initialize(MenuItemImpl itemData, int menuType) {
        this.mItemData = itemData;
        this.mMenuType = menuType;
        setVisibility(itemData.isVisible() ? 0 : 8);
        setTitle(itemData.getTitleForItemView(this));
        setCheckable(itemData.isCheckable());
        setShortcut(itemData.shouldShowShortcut(), itemData.getShortcut());
        setIcon(itemData.getIcon());
        setEnabled(itemData.isEnabled());
        setSubMenuArrowVisible(itemData.hasSubMenu());
        setContentDescription(itemData.getContentDescription());
    }

    private void addContentView(View v) {
        addContentView(v, -1);
    }

    private void addContentView(View v, int index) {
        LinearLayout linearLayout = this.mContent;
        if (linearLayout != null) {
            linearLayout.addView(v, index);
        } else {
            addView(v, index);
        }
    }

    public void setForceShowIcon(boolean forceShow) {
        this.mForceShowIcon = forceShow;
        this.mPreserveIconSpacing = forceShow;
    }

    @Override // com.android.internal.view.menu.MenuView.ItemView
    public void setTitle(CharSequence title) {
        if (title != null) {
            this.mTitleView.setText(title);
            if (this.mTitleView.getVisibility() != 0) {
                this.mTitleView.setVisibility(0);
            }
        } else if (this.mTitleView.getVisibility() != 8) {
            this.mTitleView.setVisibility(8);
        }
    }

    @Override // com.android.internal.view.menu.MenuView.ItemView
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    @Override // com.android.internal.view.menu.MenuView.ItemView
    public void setCheckable(boolean checkable) {
        CompoundButton compoundButton;
        CompoundButton otherCompoundButton;
        if (!checkable && this.mRadioButton == null && this.mCheckBox == null) {
            return;
        }
        if (this.mItemData.isExclusiveCheckable()) {
            if (this.mRadioButton == null) {
                insertRadioButton();
            }
            compoundButton = this.mRadioButton;
            otherCompoundButton = this.mCheckBox;
        } else {
            CompoundButton compoundButton2 = this.mCheckBox;
            if (compoundButton2 == null) {
                insertCheckBox();
            }
            compoundButton = this.mCheckBox;
            otherCompoundButton = this.mRadioButton;
        }
        if (checkable) {
            compoundButton.setChecked(this.mItemData.isChecked());
            int newVisibility = checkable ? 0 : 8;
            if (compoundButton.getVisibility() != newVisibility) {
                compoundButton.setVisibility(newVisibility);
            }
            if (otherCompoundButton != null && otherCompoundButton.getVisibility() != 8) {
                otherCompoundButton.setVisibility(8);
                return;
            }
            return;
        }
        CheckBox checkBox = this.mCheckBox;
        if (checkBox != null) {
            checkBox.setVisibility(8);
        }
        RadioButton radioButton = this.mRadioButton;
        if (radioButton != null) {
            radioButton.setVisibility(8);
        }
    }

    @Override // com.android.internal.view.menu.MenuView.ItemView
    public void setChecked(boolean checked) {
        CompoundButton compoundButton;
        if (this.mItemData.isExclusiveCheckable()) {
            if (this.mRadioButton == null) {
                insertRadioButton();
            }
            compoundButton = this.mRadioButton;
        } else {
            CompoundButton compoundButton2 = this.mCheckBox;
            if (compoundButton2 == null) {
                insertCheckBox();
            }
            compoundButton = this.mCheckBox;
        }
        compoundButton.setChecked(checked);
    }

    private void setSubMenuArrowVisible(boolean hasSubmenu) {
        ImageView imageView = this.mSubMenuArrowView;
        if (imageView != null) {
            imageView.setVisibility(hasSubmenu ? 0 : 8);
        }
    }

    @Override // com.android.internal.view.menu.MenuView.ItemView
    public void setShortcut(boolean showShortcut, char shortcutKey) {
        int newVisibility = (showShortcut && this.mItemData.shouldShowShortcut()) ? 0 : 8;
        if (newVisibility == 0) {
            this.mShortcutView.setText(this.mItemData.getShortcutLabel());
        }
        if (this.mShortcutView.getVisibility() != newVisibility) {
            this.mShortcutView.setVisibility(newVisibility);
        }
    }

    @Override // com.android.internal.view.menu.MenuView.ItemView
    public void setIcon(Drawable icon) {
        boolean showIcon = this.mItemData.shouldShowIcon() || this.mForceShowIcon;
        if (!showIcon && !this.mPreserveIconSpacing) {
            return;
        }
        ImageView imageView = this.mIconView;
        if (imageView == null && icon == null && !this.mPreserveIconSpacing) {
            return;
        }
        if (imageView == null) {
            insertIconView();
        }
        if (icon != null || this.mPreserveIconSpacing) {
            this.mIconView.setImageDrawable(showIcon ? icon : null);
            if (this.mIconView.getVisibility() != 0) {
                this.mIconView.setVisibility(0);
                return;
            }
            return;
        }
        this.mIconView.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mIconView != null && this.mPreserveIconSpacing) {
            ViewGroup.LayoutParams lp = getLayoutParams();
            LinearLayout.LayoutParams iconLp = (LinearLayout.LayoutParams) this.mIconView.getLayoutParams();
            if (lp.height > 0 && iconLp.width <= 0) {
                iconLp.width = lp.height;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void insertIconView() {
        LayoutInflater inflater = getInflater();
        ImageView imageView = (ImageView) inflater.inflate(C4057R.layout.list_menu_item_icon, (ViewGroup) this, false);
        this.mIconView = imageView;
        addContentView(imageView, 0);
    }

    private void insertRadioButton() {
        LayoutInflater inflater = getInflater();
        RadioButton radioButton = (RadioButton) inflater.inflate(C4057R.layout.list_menu_item_radio, (ViewGroup) this, false);
        this.mRadioButton = radioButton;
        addContentView(radioButton);
    }

    private void insertCheckBox() {
        LayoutInflater inflater = getInflater();
        CheckBox checkBox = (CheckBox) inflater.inflate(C4057R.layout.list_menu_item_checkbox, (ViewGroup) this, false);
        this.mCheckBox = checkBox;
        addContentView(checkBox);
    }

    @Override // com.android.internal.view.menu.MenuView.ItemView
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override // com.android.internal.view.menu.MenuView.ItemView
    public boolean showsIcon() {
        return this.mForceShowIcon;
    }

    private LayoutInflater getInflater() {
        if (this.mInflater == null) {
            this.mInflater = LayoutInflater.from(this.mContext);
        }
        return this.mInflater;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        MenuItemImpl menuItemImpl = this.mItemData;
        if (menuItemImpl != null && menuItemImpl.hasSubMenu()) {
            info.setCanOpenPopup(true);
        }
    }

    public void setGroupDividerEnabled(boolean groupDividerEnabled) {
        ImageView imageView = this.mGroupDivider;
        if (imageView != null) {
            imageView.setVisibility((this.mHasListDivider || !groupDividerEnabled) ? 8 : 0);
        }
    }

    @Override // android.widget.AbsListView.SelectionBoundsAdjuster
    public void adjustListItemSelectionBounds(Rect rect) {
        ImageView imageView = this.mGroupDivider;
        if (imageView != null && imageView.getVisibility() == 0) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mGroupDivider.getLayoutParams();
            rect.top += this.mGroupDivider.getHeight() + lp.topMargin + lp.bottomMargin;
        }
    }
}
