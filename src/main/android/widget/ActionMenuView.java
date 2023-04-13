package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.TtmlUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import com.android.internal.view.menu.ActionMenuItemView;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuPresenter;
import com.android.internal.view.menu.MenuView;
/* loaded from: classes4.dex */
public class ActionMenuView extends LinearLayout implements MenuBuilder.ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    private MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    /* loaded from: classes4.dex */
    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    /* loaded from: classes4.dex */
    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBaselineAligned(false);
        float density = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * density);
        this.mGeneratedItemPadding = (int) (4.0f * density);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
    }

    public void setPopupTheme(int resId) {
        if (this.mPopupTheme != resId) {
            this.mPopupTheme = resId;
            if (resId == 0) {
                this.mPopupContext = this.mContext;
            } else {
                this.mPopupContext = new ContextThemeWrapper(this.mContext, resId);
            }
        }
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    public void setPresenter(ActionMenuPresenter presenter) {
        this.mPresenter = presenter;
        presenter.setMenuView(this);
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        MenuBuilder menuBuilder;
        boolean wasFormatted = this.mFormatItems;
        boolean z = View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824;
        this.mFormatItems = z;
        if (wasFormatted != z) {
            this.mFormatItemsWidth = 0;
        }
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        if (this.mFormatItems && (menuBuilder = this.mMenu) != null && widthSize != this.mFormatItemsWidth) {
            this.mFormatItemsWidth = widthSize;
            menuBuilder.onItemsChanged(true);
        }
        int childCount = getChildCount();
        if (this.mFormatItems && childCount > 0) {
            onMeasureExactFormat(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.rightMargin = 0;
            lp.leftMargin = 0;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /* JADX WARN: Removed duplicated region for block: B:138:0x029f  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x02cc  */
    /* JADX WARN: Removed duplicated region for block: B:149:0x02d4  */
    /* JADX WARN: Removed duplicated region for block: B:150:0x02d6  */
    /* JADX WARN: Type inference failed for: r3v11 */
    /* JADX WARN: Type inference failed for: r3v12, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r3v29 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void onMeasureExactFormat(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode;
        int widthSize;
        boolean needsExpansion;
        boolean needsExpansion2;
        int heightSize;
        int visibleItemCount;
        int extraPixels;
        int heightMode2;
        int widthSize2;
        int visibleItemCount2;
        ?? r3;
        int heightMode3 = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize3 = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int itemHeightSpec = getChildMeasureSpec(heightMeasureSpec, heightPadding, -2);
        int widthSize4 = widthSize3 - widthPadding;
        int i = this.mMinCellSize;
        int cellCount = widthSize4 / i;
        int cellSizeRemaining = widthSize4 % i;
        if (cellCount == 0) {
            setMeasuredDimension(widthSize4, 0);
            return;
        }
        int cellSize = i + (cellSizeRemaining / cellCount);
        int cellsRemaining = cellCount;
        int maxChildHeight = 0;
        int maxCellsUsed = 0;
        int expandableItemCount = 0;
        boolean hasOverflow = false;
        long smallestItemsAt = 0;
        int childCount = getChildCount();
        int maxChildHeight2 = 0;
        int widthPadding2 = 0;
        while (widthPadding2 < childCount) {
            View child = getChildAt(widthPadding2);
            int cellCount2 = cellCount;
            int cellSizeRemaining2 = cellSizeRemaining;
            if (child.getVisibility() != 8) {
                boolean isGeneratedItem = child instanceof ActionMenuItemView;
                int visibleItemCount3 = maxChildHeight2 + 1;
                if (!isGeneratedItem) {
                    visibleItemCount2 = visibleItemCount3;
                    r3 = 0;
                } else {
                    int i2 = this.mGeneratedItemPadding;
                    visibleItemCount2 = visibleItemCount3;
                    r3 = 0;
                    child.setPadding(i2, 0, i2, 0);
                }
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.expanded = r3;
                lp.extraPixels = r3;
                lp.cellsUsed = r3;
                lp.expandable = r3;
                lp.leftMargin = r3;
                lp.rightMargin = r3;
                lp.preventEdgeOffset = isGeneratedItem && ((ActionMenuItemView) child).hasText();
                int cellsAvailable = lp.isOverflowButton ? 1 : cellsRemaining;
                int cellsUsed = measureChildForCells(child, cellSize, cellsAvailable, itemHeightSpec, heightPadding);
                maxCellsUsed = Math.max(maxCellsUsed, cellsUsed);
                if (lp.expandable) {
                    expandableItemCount++;
                }
                if (lp.isOverflowButton) {
                    hasOverflow = true;
                }
                cellsRemaining -= cellsUsed;
                int maxChildHeight3 = Math.max(maxChildHeight, child.getMeasuredHeight());
                if (cellsUsed == 1) {
                    maxChildHeight = maxChildHeight3;
                    smallestItemsAt |= 1 << widthPadding2;
                    maxChildHeight2 = visibleItemCount2;
                } else {
                    maxChildHeight = maxChildHeight3;
                    maxChildHeight2 = visibleItemCount2;
                }
            }
            widthPadding2++;
            cellCount = cellCount2;
            cellSizeRemaining = cellSizeRemaining2;
        }
        boolean centerSingleExpandedItem = hasOverflow && maxChildHeight2 == 2;
        boolean needsExpansion3 = false;
        while (expandableItemCount > 0 && cellsRemaining > 0) {
            int minCells = Integer.MAX_VALUE;
            long minCellsAt = 0;
            int minCellsItemCount = 0;
            int heightPadding2 = heightPadding;
            int heightPadding3 = 0;
            while (heightPadding3 < childCount) {
                boolean needsExpansion4 = needsExpansion3;
                LayoutParams lp2 = (LayoutParams) getChildAt(heightPadding3).getLayoutParams();
                int expandableItemCount2 = expandableItemCount;
                if (!lp2.expandable) {
                    heightMode2 = heightMode3;
                    widthSize2 = widthSize4;
                } else if (lp2.cellsUsed < minCells) {
                    int minCells2 = lp2.cellsUsed;
                    int minCells3 = 1 << heightPadding3;
                    heightMode2 = heightMode3;
                    widthSize2 = widthSize4;
                    long minCellsAt2 = minCells3;
                    minCellsItemCount = 1;
                    minCellsAt = minCellsAt2;
                    minCells = minCells2;
                } else {
                    heightMode2 = heightMode3;
                    widthSize2 = widthSize4;
                    int heightMode4 = lp2.cellsUsed;
                    if (heightMode4 == minCells) {
                        minCellsItemCount++;
                        minCellsAt |= 1 << heightPadding3;
                    }
                }
                heightPadding3++;
                expandableItemCount = expandableItemCount2;
                needsExpansion3 = needsExpansion4;
                heightMode3 = heightMode2;
                widthSize4 = widthSize2;
            }
            heightMode = heightMode3;
            widthSize = widthSize4;
            needsExpansion = needsExpansion3;
            int expandableItemCount3 = expandableItemCount;
            smallestItemsAt |= minCellsAt;
            if (minCellsItemCount > cellsRemaining) {
                break;
            }
            int minCells4 = minCells + 1;
            int i3 = 0;
            while (i3 < childCount) {
                View child2 = getChildAt(i3);
                LayoutParams lp3 = (LayoutParams) child2.getLayoutParams();
                int minCells5 = minCells4;
                if ((minCellsAt & (1 << i3)) == 0) {
                    if (lp3.cellsUsed == minCells5) {
                        minCells5 = minCells5;
                        smallestItemsAt |= 1 << i3;
                    } else {
                        minCells5 = minCells5;
                    }
                } else {
                    if (centerSingleExpandedItem && lp3.preventEdgeOffset && cellsRemaining == 1) {
                        int i4 = this.mGeneratedItemPadding;
                        child2.setPadding(i4 + cellSize, 0, i4, 0);
                    }
                    lp3.cellsUsed++;
                    lp3.expanded = true;
                    cellsRemaining--;
                }
                i3++;
                minCells4 = minCells5;
            }
            needsExpansion3 = true;
            heightPadding = heightPadding2;
            expandableItemCount = expandableItemCount3;
            heightMode3 = heightMode;
            widthSize4 = widthSize;
        }
        heightMode = heightMode3;
        widthSize = widthSize4;
        needsExpansion = needsExpansion3;
        boolean singleItem = !hasOverflow && maxChildHeight2 == 1;
        if (cellsRemaining > 0 && smallestItemsAt != 0) {
            if (cellsRemaining < maxChildHeight2 - 1 || singleItem || maxCellsUsed > 1) {
                float expandCount = Long.bitCount(smallestItemsAt);
                if (!singleItem) {
                    if ((smallestItemsAt & 1) == 0) {
                        extraPixels = 0;
                    } else {
                        extraPixels = 0;
                        if (!((LayoutParams) getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                            expandCount -= 0.5f;
                        }
                    }
                    if ((smallestItemsAt & (1 << (childCount - 1))) != 0 && !((LayoutParams) getChildAt(childCount - 1).getLayoutParams()).preventEdgeOffset) {
                        expandCount -= 0.5f;
                    }
                } else {
                    extraPixels = 0;
                }
                if (expandCount > 0.0f) {
                    extraPixels = (int) ((cellsRemaining * cellSize) / expandCount);
                }
                int i5 = 0;
                needsExpansion2 = needsExpansion;
                while (i5 < childCount) {
                    boolean singleItem2 = singleItem;
                    float expandCount2 = expandCount;
                    if ((smallestItemsAt & (1 << i5)) != 0) {
                        View child3 = getChildAt(i5);
                        LayoutParams lp4 = (LayoutParams) child3.getLayoutParams();
                        if (child3 instanceof ActionMenuItemView) {
                            lp4.extraPixels = extraPixels;
                            lp4.expanded = true;
                            if (i5 == 0 && !lp4.preventEdgeOffset) {
                                lp4.leftMargin = (-extraPixels) / 2;
                            }
                            needsExpansion2 = true;
                        } else if (lp4.isOverflowButton) {
                            lp4.extraPixels = extraPixels;
                            lp4.expanded = true;
                            lp4.rightMargin = (-extraPixels) / 2;
                            needsExpansion2 = true;
                        } else {
                            if (i5 != 0) {
                                lp4.leftMargin = extraPixels / 2;
                            }
                            if (i5 != childCount - 1) {
                                lp4.rightMargin = extraPixels / 2;
                            }
                        }
                    }
                    i5++;
                    singleItem = singleItem2;
                    expandCount = expandCount2;
                }
                if (!needsExpansion2) {
                    int i6 = 0;
                    while (i6 < childCount) {
                        View child4 = getChildAt(i6);
                        LayoutParams lp5 = (LayoutParams) child4.getLayoutParams();
                        if (lp5.expanded) {
                            int width = (lp5.cellsUsed * cellSize) + lp5.extraPixels;
                            visibleItemCount = maxChildHeight2;
                            int visibleItemCount4 = View.MeasureSpec.makeMeasureSpec(width, 1073741824);
                            child4.measure(visibleItemCount4, itemHeightSpec);
                        } else {
                            visibleItemCount = maxChildHeight2;
                        }
                        i6++;
                        maxChildHeight2 = visibleItemCount;
                    }
                }
                if (heightMode != 1073741824) {
                    heightSize = heightSize2;
                } else {
                    heightSize = maxChildHeight;
                }
                setMeasuredDimension(widthSize, heightSize);
            }
        }
        needsExpansion2 = needsExpansion;
        if (!needsExpansion2) {
        }
        if (heightMode != 1073741824) {
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int measureChildForCells(View child, int cellSize, int cellsRemaining, int parentHeightMeasureSpec, int parentHeightPadding) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int childHeightSize = View.MeasureSpec.getSize(parentHeightMeasureSpec) - parentHeightPadding;
        int childHeightMode = View.MeasureSpec.getMode(parentHeightMeasureSpec);
        int childHeightSpec = View.MeasureSpec.makeMeasureSpec(childHeightSize, childHeightMode);
        ActionMenuItemView itemView = child instanceof ActionMenuItemView ? (ActionMenuItemView) child : null;
        boolean expandable = false;
        boolean hasText = itemView != null && itemView.hasText();
        int cellsUsed = 0;
        if (cellsRemaining > 0 && (!hasText || cellsRemaining >= 2)) {
            int childWidthSpec = View.MeasureSpec.makeMeasureSpec(cellSize * cellsRemaining, Integer.MIN_VALUE);
            child.measure(childWidthSpec, childHeightSpec);
            int measuredWidth = child.getMeasuredWidth();
            cellsUsed = measuredWidth / cellSize;
            if (measuredWidth % cellSize != 0) {
                cellsUsed++;
            }
            if (hasText && cellsUsed < 2) {
                cellsUsed = 2;
            }
        }
        if (!lp.isOverflowButton && hasText) {
            expandable = true;
        }
        lp.expandable = expandable;
        lp.cellsUsed = cellsUsed;
        int targetWidth = cellsUsed * cellSize;
        child.measure(View.MeasureSpec.makeMeasureSpec(targetWidth, 1073741824), childHeightSpec);
        return cellsUsed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int i;
        int dividerWidth;
        int overflowWidth;
        int midVertical;
        boolean isLayoutRtl;
        int r;
        int l;
        ActionMenuView actionMenuView = this;
        if (!actionMenuView.mFormatItems) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        int childCount = getChildCount();
        int midVertical2 = (bottom - top) / 2;
        int dividerWidth2 = getDividerWidth();
        int overflowWidth2 = 0;
        int nonOverflowWidth = 0;
        int nonOverflowCount = 0;
        int widthRemaining = ((right - left) - getPaddingRight()) - getPaddingLeft();
        int i2 = 0;
        boolean isLayoutRtl2 = isLayoutRtl();
        int i3 = 0;
        while (true) {
            i = 8;
            if (i3 >= childCount) {
                break;
            }
            View v = actionMenuView.getChildAt(i3);
            if (v.getVisibility() == 8) {
                midVertical = midVertical2;
                isLayoutRtl = isLayoutRtl2;
            } else {
                LayoutParams p = (LayoutParams) v.getLayoutParams();
                if (p.isOverflowButton) {
                    overflowWidth2 = v.getMeasuredWidth();
                    if (actionMenuView.hasDividerBeforeChildAt(i3)) {
                        overflowWidth2 += dividerWidth2;
                    }
                    int height = v.getMeasuredHeight();
                    if (isLayoutRtl2) {
                        l = getPaddingLeft() + p.leftMargin;
                        r = l + overflowWidth2;
                    } else {
                        r = (getWidth() - getPaddingRight()) - p.rightMargin;
                        l = r - overflowWidth2;
                    }
                    isLayoutRtl = isLayoutRtl2;
                    int t = midVertical2 - (height / 2);
                    midVertical = midVertical2;
                    int midVertical3 = t + height;
                    v.layout(l, t, r, midVertical3);
                    widthRemaining -= overflowWidth2;
                    i2 = 1;
                } else {
                    midVertical = midVertical2;
                    isLayoutRtl = isLayoutRtl2;
                    int midVertical4 = v.getMeasuredWidth();
                    int size = midVertical4 + p.leftMargin + p.rightMargin;
                    nonOverflowWidth += size;
                    widthRemaining -= size;
                    if (actionMenuView.hasDividerBeforeChildAt(i3)) {
                        nonOverflowWidth += dividerWidth2;
                    }
                    nonOverflowCount++;
                }
            }
            i3++;
            midVertical2 = midVertical;
            isLayoutRtl2 = isLayoutRtl;
        }
        int midVertical5 = midVertical2;
        boolean isLayoutRtl3 = isLayoutRtl2;
        if (childCount == 1 && i2 == 0) {
            View v2 = actionMenuView.getChildAt(0);
            int width = v2.getMeasuredWidth();
            int height2 = v2.getMeasuredHeight();
            int midHorizontal = (right - left) / 2;
            int l2 = midHorizontal - (width / 2);
            int t2 = midVertical5 - (height2 / 2);
            v2.layout(l2, t2, l2 + width, t2 + height2);
            return;
        }
        int spacerCount = nonOverflowCount - (i2 ^ 1);
        int spacerSize = Math.max(0, spacerCount > 0 ? widthRemaining / spacerCount : 0);
        if (isLayoutRtl3) {
            int startRight = getWidth() - getPaddingRight();
            int i4 = 0;
            while (i4 < childCount) {
                View v3 = actionMenuView.getChildAt(i4);
                LayoutParams lp = (LayoutParams) v3.getLayoutParams();
                if (v3.getVisibility() == i) {
                    dividerWidth = dividerWidth2;
                    overflowWidth = overflowWidth2;
                } else if (lp.isOverflowButton) {
                    dividerWidth = dividerWidth2;
                    overflowWidth = overflowWidth2;
                } else {
                    int startRight2 = startRight - lp.rightMargin;
                    int width2 = v3.getMeasuredWidth();
                    int height3 = v3.getMeasuredHeight();
                    int t3 = midVertical5 - (height3 / 2);
                    dividerWidth = dividerWidth2;
                    overflowWidth = overflowWidth2;
                    int overflowWidth3 = t3 + height3;
                    v3.layout(startRight2 - width2, t3, startRight2, overflowWidth3);
                    startRight = startRight2 - ((lp.leftMargin + width2) + spacerSize);
                }
                i4++;
                dividerWidth2 = dividerWidth;
                overflowWidth2 = overflowWidth;
                i = 8;
            }
            return;
        }
        int startLeft = getPaddingLeft();
        int i5 = 0;
        while (i5 < childCount) {
            View v4 = actionMenuView.getChildAt(i5);
            LayoutParams lp2 = (LayoutParams) v4.getLayoutParams();
            if (v4.getVisibility() != 8 && !lp2.isOverflowButton) {
                int startLeft2 = startLeft + lp2.leftMargin;
                int width3 = v4.getMeasuredWidth();
                int height4 = v4.getMeasuredHeight();
                int t4 = midVertical5 - (height4 / 2);
                v4.layout(startLeft2, t4, startLeft2 + width3, t4 + height4);
                startLeft = startLeft2 + lp2.rightMargin + width3 + spacerSize;
            }
            i5++;
            actionMenuView = this;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    public void setOverflowIcon(Drawable icon) {
        getMenu();
        this.mPresenter.setOverflowIcon(icon);
    }

    public Drawable getOverflowIcon() {
        getMenu();
        return this.mPresenter.getOverflowIcon();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    public void setOverflowReserved(boolean reserveOverflow) {
        this.mReserveOverflow = reserveOverflow;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(-2, -2);
        params.gravity = 16;
        return params;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        LayoutParams result;
        if (p != null) {
            if (p instanceof LayoutParams) {
                result = new LayoutParams((LayoutParams) p);
            } else {
                result = new LayoutParams(p);
            }
            if (result.gravity <= 0) {
                result.gravity = 16;
            }
            return result;
        }
        LayoutParams result2 = generateDefaultLayoutParams();
        return result2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && (p instanceof LayoutParams);
    }

    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams result = generateDefaultLayoutParams();
        result.isOverflowButton = true;
        return result;
    }

    @Override // com.android.internal.view.menu.MenuBuilder.ItemInvoker
    public boolean invokeItem(MenuItemImpl item) {
        return this.mMenu.performItemAction(item, 0);
    }

    @Override // com.android.internal.view.menu.MenuView
    public int getWindowAnimations() {
        return 0;
    }

    @Override // com.android.internal.view.menu.MenuView
    public void initialize(MenuBuilder menu) {
        this.mMenu = menu;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            MenuBuilder menuBuilder = new MenuBuilder(context);
            this.mMenu = menuBuilder;
            menuBuilder.setCallback(new MenuBuilderCallback());
            ActionMenuPresenter actionMenuPresenter = new ActionMenuPresenter(context);
            this.mPresenter = actionMenuPresenter;
            actionMenuPresenter.setReserveOverflow(true);
            ActionMenuPresenter actionMenuPresenter2 = this.mPresenter;
            MenuPresenter.Callback callback = this.mActionMenuPresenterCallback;
            if (callback == null) {
                callback = new ActionMenuPresenterCallback();
            }
            actionMenuPresenter2.setCallback(callback);
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    public void setMenuCallbacks(MenuPresenter.Callback pcb, MenuBuilder.Callback mcb) {
        this.mActionMenuPresenterCallback = pcb;
        this.mMenuBuilderCallback = mcb;
    }

    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    public boolean showOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.showOverflowMenu();
    }

    public boolean hideOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.hideOverflowMenu();
    }

    public boolean isOverflowMenuShowing() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowing();
    }

    public boolean isOverflowMenuShowPending() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowPending();
    }

    public void dismissPopupMenus() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.dismissPopupMenus();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout
    public boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return false;
        }
        View childBefore = getChildAt(childIndex - 1);
        View child = getChildAt(childIndex);
        boolean result = false;
        if (childIndex < getChildCount() && (childBefore instanceof ActionMenuChildView)) {
            result = false | ((ActionMenuChildView) childBefore).needsDividerAfter();
        }
        if (childIndex > 0 && (child instanceof ActionMenuChildView)) {
            return result | ((ActionMenuChildView) child).needsDividerBefore();
        }
        return result;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent event) {
        return false;
    }

    public void setExpandedActionViewsExclusive(boolean exclusive) {
        this.mPresenter.setExpandedActionViewsExclusive(exclusive);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class MenuBuilderCallback implements MenuBuilder.Callback {
        private MenuBuilderCallback() {
        }

        @Override // com.android.internal.view.menu.MenuBuilder.Callback
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(item);
        }

        @Override // com.android.internal.view.menu.MenuBuilder.Callback
        public void onMenuModeChange(MenuBuilder menu) {
            if (ActionMenuView.this.mMenuBuilderCallback != null) {
                ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menu);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        private ActionMenuPresenterCallback() {
        }

        @Override // com.android.internal.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        @Override // com.android.internal.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            return false;
        }
    }

    /* loaded from: classes4.dex */
    public static class LayoutParams extends LinearLayout.LayoutParams {
        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public int cellsUsed;
        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public boolean expandable;
        public boolean expanded;
        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public int extraPixels;
        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public boolean isOverflowButton;
        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public boolean preventEdgeOffset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
        }

        public LayoutParams(LayoutParams other) {
            super((LinearLayout.LayoutParams) other);
            this.isOverflowButton = other.isOverflowButton;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.isOverflowButton = false;
        }

        public LayoutParams(int width, int height, boolean isOverflowButton) {
            super(width, height);
            this.isOverflowButton = isOverflowButton;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.LinearLayout.LayoutParams, android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        public void encodeProperties(ViewHierarchyEncoder encoder) {
            super.encodeProperties(encoder);
            encoder.addProperty("layout:overFlowButton", this.isOverflowButton);
            encoder.addProperty("layout:cellsUsed", this.cellsUsed);
            encoder.addProperty("layout:extraPixels", this.extraPixels);
            encoder.addProperty("layout:expandable", this.expandable);
            encoder.addProperty("layout:preventEdgeOffset", this.preventEdgeOffset);
        }
    }
}
