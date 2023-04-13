package com.android.internal.view.menu;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import com.android.internal.C4057R;
import com.android.internal.view.menu.MenuBuilder;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class IconMenuView extends ViewGroup implements MenuBuilder.ItemInvoker, MenuView, Runnable {
    private static final int ITEM_CAPTION_CYCLE_DELAY = 1000;
    private int mAnimations;
    private boolean mHasStaleChildren;
    private Drawable mHorizontalDivider;
    private int mHorizontalDividerHeight;
    private ArrayList<Rect> mHorizontalDividerRects;
    private Drawable mItemBackground;
    private boolean mLastChildrenCaptionMode;
    private int[] mLayout;
    private int mLayoutNumRows;
    private int mMaxItems;
    private int mMaxItemsPerRow;
    private int mMaxRows;
    private MenuBuilder mMenu;
    private boolean mMenuBeingLongpressed;
    private Drawable mMoreIcon;
    private int mNumActualItemsShown;
    private int mRowHeight;
    private Drawable mVerticalDivider;
    private ArrayList<Rect> mVerticalDividerRects;
    private int mVerticalDividerWidth;

    public IconMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMenuBeingLongpressed = false;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.IconMenuView, 0, 0);
        this.mRowHeight = a.getDimensionPixelSize(0, 64);
        this.mMaxRows = a.getInt(1, 2);
        this.mMaxItems = a.getInt(4, 6);
        this.mMaxItemsPerRow = a.getInt(2, 3);
        this.mMoreIcon = a.getDrawable(3);
        a.recycle();
        TypedArray a2 = context.obtainStyledAttributes(attrs, C4057R.styleable.MenuView, 0, 0);
        this.mItemBackground = a2.getDrawable(5);
        this.mHorizontalDivider = a2.getDrawable(2);
        this.mHorizontalDividerRects = new ArrayList<>();
        this.mVerticalDivider = a2.getDrawable(3);
        this.mVerticalDividerRects = new ArrayList<>();
        this.mAnimations = a2.getResourceId(0, 0);
        a2.recycle();
        Drawable drawable = this.mHorizontalDivider;
        if (drawable != null) {
            int intrinsicHeight = drawable.getIntrinsicHeight();
            this.mHorizontalDividerHeight = intrinsicHeight;
            if (intrinsicHeight == -1) {
                this.mHorizontalDividerHeight = 1;
            }
        }
        Drawable drawable2 = this.mVerticalDivider;
        if (drawable2 != null) {
            int intrinsicWidth = drawable2.getIntrinsicWidth();
            this.mVerticalDividerWidth = intrinsicWidth;
            if (intrinsicWidth == -1) {
                this.mVerticalDividerWidth = 1;
            }
        }
        this.mLayout = new int[this.mMaxRows];
        setWillNotDraw(false);
        setFocusableInTouchMode(true);
        setDescendantFocusability(262144);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMaxItems() {
        return this.mMaxItems;
    }

    private void layoutItems(int width) {
        int numItems = getChildCount();
        if (numItems == 0) {
            this.mLayoutNumRows = 0;
            return;
        }
        for (int curNumRows = Math.min((int) Math.ceil(numItems / this.mMaxItemsPerRow), this.mMaxRows); curNumRows <= this.mMaxRows; curNumRows++) {
            layoutItemsUsingGravity(curNumRows, numItems);
            if (curNumRows >= numItems || doItemsFit()) {
                return;
            }
        }
    }

    private void layoutItemsUsingGravity(int numRows, int numItems) {
        int numBaseItemsPerRow = numItems / numRows;
        int numLeftoverItems = numItems % numRows;
        int rowsThatGetALeftoverItem = numRows - numLeftoverItems;
        int[] layout = this.mLayout;
        for (int i = 0; i < numRows; i++) {
            layout[i] = numBaseItemsPerRow;
            if (i >= rowsThatGetALeftoverItem) {
                layout[i] = layout[i] + 1;
            }
        }
        this.mLayoutNumRows = numRows;
    }

    private boolean doItemsFit() {
        int itemPos = 0;
        int[] layout = this.mLayout;
        int numRows = this.mLayoutNumRows;
        for (int row = 0; row < numRows; row++) {
            int numItemsOnRow = layout[row];
            if (numItemsOnRow == 1) {
                itemPos++;
            } else {
                int itemsOnRowCounter = numItemsOnRow;
                while (itemsOnRowCounter > 0) {
                    int itemPos2 = itemPos + 1;
                    View child = getChildAt(itemPos);
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    if (lp.maxNumItemsOnRow < numItemsOnRow) {
                        return false;
                    }
                    itemsOnRowCounter--;
                    itemPos = itemPos2;
                }
                continue;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Drawable getItemBackgroundDrawable() {
        return this.mItemBackground.getConstantState().newDrawable(getContext().getResources());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IconMenuItemView createMoreItemView() {
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        IconMenuItemView itemView = (IconMenuItemView) inflater.inflate(C4057R.layout.icon_menu_item_layout, (ViewGroup) null);
        Resources r = context.getResources();
        itemView.initialize(r.getText(C4057R.string.more_item_label), this.mMoreIcon);
        itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.internal.view.menu.IconMenuView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                IconMenuView.this.mMenu.changeMenuMode();
            }
        });
        return itemView;
    }

    @Override // com.android.internal.view.menu.MenuView
    public void initialize(MenuBuilder menu) {
        this.mMenu = menu;
    }

    private void positionChildren(int menuWidth, int menuHeight) {
        int[] numItemsForRow;
        int itemPos;
        LayoutParams childLayoutParams;
        if (this.mHorizontalDivider != null) {
            this.mHorizontalDividerRects.clear();
        }
        if (this.mVerticalDivider != null) {
            this.mVerticalDividerRects.clear();
        }
        int numRows = this.mLayoutNumRows;
        int numRowsMinus1 = numRows - 1;
        int[] numItemsForRow2 = this.mLayout;
        int itemPos2 = 0;
        LayoutParams childLayoutParams2 = null;
        float itemTop = 0.0f;
        float itemHeight = (menuHeight - (this.mHorizontalDividerHeight * (numRows - 1))) / numRows;
        int row = 0;
        while (row < numRows) {
            float itemLeft = 0.0f;
            float itemWidth = (menuWidth - (this.mVerticalDividerWidth * (numItemsForRow2[row] - 1))) / numItemsForRow2[row];
            int itemPosOnRow = 0;
            while (itemPosOnRow < numItemsForRow2[row]) {
                View child = getChildAt(itemPos2);
                int numRows2 = numRows;
                child.measure(View.MeasureSpec.makeMeasureSpec((int) itemWidth, 1073741824), View.MeasureSpec.makeMeasureSpec((int) itemHeight, 1073741824));
                LayoutParams childLayoutParams3 = (LayoutParams) child.getLayoutParams();
                childLayoutParams3.left = (int) itemLeft;
                childLayoutParams3.right = (int) (itemLeft + itemWidth);
                childLayoutParams3.top = (int) itemTop;
                childLayoutParams3.bottom = (int) (itemTop + itemHeight);
                float itemLeft2 = itemLeft + itemWidth;
                int itemPos3 = itemPos2 + 1;
                if (this.mVerticalDivider == null) {
                    numItemsForRow = numItemsForRow2;
                    itemPos = itemPos3;
                    childLayoutParams = childLayoutParams3;
                } else {
                    ArrayList<Rect> arrayList = this.mVerticalDividerRects;
                    numItemsForRow = numItemsForRow2;
                    itemPos = itemPos3;
                    int itemPos4 = this.mVerticalDividerWidth;
                    childLayoutParams = childLayoutParams3;
                    arrayList.add(new Rect((int) itemLeft2, (int) itemTop, (int) (itemPos4 + itemLeft2), (int) (itemTop + itemHeight)));
                }
                itemLeft = itemLeft2 + this.mVerticalDividerWidth;
                itemPosOnRow++;
                numRows = numRows2;
                numItemsForRow2 = numItemsForRow;
                itemPos2 = itemPos;
                childLayoutParams2 = childLayoutParams;
            }
            int numRows3 = numRows;
            int[] numItemsForRow3 = numItemsForRow2;
            if (childLayoutParams2 != null) {
                childLayoutParams2.right = menuWidth;
            }
            itemTop += itemHeight;
            if (this.mHorizontalDivider != null && row < numRowsMinus1) {
                this.mHorizontalDividerRects.add(new Rect(0, (int) itemTop, menuWidth, (int) (this.mHorizontalDividerHeight + itemTop)));
                itemTop += this.mHorizontalDividerHeight;
            }
            row++;
            numRows = numRows3;
            numItemsForRow2 = numItemsForRow3;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = resolveSize(Integer.MAX_VALUE, widthMeasureSpec);
        calculateItemFittingMetadata(measuredWidth);
        layoutItems(measuredWidth);
        int layoutNumRows = this.mLayoutNumRows;
        int i = this.mRowHeight;
        int i2 = this.mHorizontalDividerHeight;
        int desiredHeight = ((i + i2) * layoutNumRows) - i2;
        setMeasuredDimension(measuredWidth, resolveSize(desiredHeight, heightMeasureSpec));
        if (layoutNumRows > 0) {
            positionChildren(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            LayoutParams childLayoutParams = (LayoutParams) child.getLayoutParams();
            child.layout(childLayoutParams.left, childLayoutParams.top, childLayoutParams.right, childLayoutParams.bottom);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Drawable drawable = this.mHorizontalDivider;
        if (drawable != null) {
            ArrayList<Rect> rects = this.mHorizontalDividerRects;
            for (int i = rects.size() - 1; i >= 0; i--) {
                drawable.setBounds(rects.get(i));
                drawable.draw(canvas);
            }
        }
        Drawable drawable2 = this.mVerticalDivider;
        if (drawable2 != null) {
            ArrayList<Rect> rects2 = this.mVerticalDividerRects;
            for (int i2 = rects2.size() - 1; i2 >= 0; i2--) {
                drawable2.setBounds(rects2.get(i2));
                drawable2.draw(canvas);
            }
        }
    }

    @Override // com.android.internal.view.menu.MenuBuilder.ItemInvoker
    public boolean invokeItem(MenuItemImpl item) {
        return this.mMenu.performItemAction(item, 0);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void markStaleChildren() {
        if (!this.mHasStaleChildren) {
            this.mHasStaleChildren = true;
            requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getNumActualItemsShown() {
        return this.mNumActualItemsShown;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNumActualItemsShown(int count) {
        this.mNumActualItemsShown = count;
    }

    @Override // com.android.internal.view.menu.MenuView
    public int getWindowAnimations() {
        return this.mAnimations;
    }

    public int[] getLayout() {
        return this.mLayout;
    }

    public int getLayoutNumRows() {
        return this.mLayoutNumRows;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 82) {
            if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                removeCallbacks(this);
                postDelayed(this, ViewConfiguration.getLongPressTimeout());
            } else if (event.getAction() == 1) {
                if (this.mMenuBeingLongpressed) {
                    setCycleShortcutCaptionMode(false);
                    return true;
                }
                removeCallbacks(this);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestFocus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        setCycleShortcutCaptionMode(false);
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
            setCycleShortcutCaptionMode(false);
        }
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void setCycleShortcutCaptionMode(boolean cycleShortcutAndNormal) {
        if (!cycleShortcutAndNormal) {
            removeCallbacks(this);
            setChildrenCaptionMode(false);
            this.mMenuBeingLongpressed = false;
            return;
        }
        setChildrenCaptionMode(true);
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.mMenuBeingLongpressed) {
            setChildrenCaptionMode(!this.mLastChildrenCaptionMode);
        } else {
            this.mMenuBeingLongpressed = true;
            setCycleShortcutCaptionMode(true);
        }
        postDelayed(this, 1000L);
    }

    private void setChildrenCaptionMode(boolean shortcut) {
        this.mLastChildrenCaptionMode = shortcut;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            ((IconMenuItemView) getChildAt(i)).setCaptionMode(shortcut);
        }
    }

    private void calculateItemFittingMetadata(int width) {
        int maxNumItemsPerRow = this.mMaxItemsPerRow;
        int numItems = getChildCount();
        for (int i = 0; i < numItems; i++) {
            LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
            lp.maxNumItemsOnRow = 1;
            int curNumItemsPerRow = maxNumItemsPerRow;
            while (true) {
                if (curNumItemsPerRow > 0) {
                    if (lp.desiredWidth < width / curNumItemsPerRow) {
                        lp.maxNumItemsOnRow = curNumItemsPerRow;
                        break;
                    } else {
                        curNumItemsPerRow--;
                    }
                } else {
                    break;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        View focusedView = getFocusedChild();
        for (int i = getChildCount() - 1; i >= 0; i--) {
            if (getChildAt(i) == focusedView) {
                return new SavedState(superState, i);
            }
        }
        return new SavedState(superState, -1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        View v;
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (ss.focusedPosition < getChildCount() && (v = getChildAt(ss.focusedPosition)) != null) {
            v.requestFocus();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.internal.view.menu.IconMenuView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int focusedPosition;

        public SavedState(Parcelable superState, int focusedPosition) {
            super(superState);
            this.focusedPosition = focusedPosition;
        }

        private SavedState(Parcel in) {
            super(in);
            this.focusedPosition = in.readInt();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.focusedPosition);
        }
    }

    /* loaded from: classes2.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        int bottom;
        int desiredWidth;
        int left;
        int maxNumItemsOnRow;
        int right;
        int top;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
