package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.transition.Transition;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import com.android.internal.view.menu.ListMenuItemView;
import com.android.internal.view.menu.MenuAdapter;
import com.android.internal.view.menu.MenuBuilder;
/* loaded from: classes4.dex */
public class MenuPopupWindow extends ListPopupWindow implements MenuItemHoverListener {
    private MenuItemHoverListener mHoverListener;

    public MenuPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.widget.ListPopupWindow
    DropDownListView createDropDownListView(Context context, boolean hijackFocus) {
        MenuDropDownListView view = new MenuDropDownListView(context, hijackFocus);
        view.setHoverListener(this);
        return view;
    }

    public void setEnterTransition(Transition enterTransition) {
        this.mPopup.setEnterTransition(enterTransition);
    }

    public void setExitTransition(Transition exitTransition) {
        this.mPopup.setExitTransition(exitTransition);
    }

    public void setHoverListener(MenuItemHoverListener hoverListener) {
        this.mHoverListener = hoverListener;
    }

    public void setTouchModal(boolean touchModal) {
        this.mPopup.setTouchModal(touchModal);
    }

    @Override // android.widget.MenuItemHoverListener
    public void onItemHoverEnter(MenuBuilder menu, MenuItem item) {
        MenuItemHoverListener menuItemHoverListener = this.mHoverListener;
        if (menuItemHoverListener != null) {
            menuItemHoverListener.onItemHoverEnter(menu, item);
        }
    }

    @Override // android.widget.MenuItemHoverListener
    public void onItemHoverExit(MenuBuilder menu, MenuItem item) {
        MenuItemHoverListener menuItemHoverListener = this.mHoverListener;
        if (menuItemHoverListener != null) {
            menuItemHoverListener.onItemHoverExit(menu, item);
        }
    }

    /* loaded from: classes4.dex */
    public static class MenuDropDownListView extends DropDownListView {
        final int mAdvanceKey;
        private MenuItemHoverListener mHoverListener;
        private MenuItem mHoveredMenuItem;
        final int mRetreatKey;

        public MenuDropDownListView(Context context, boolean hijackFocus) {
            super(context, hijackFocus);
            Resources res = context.getResources();
            Configuration config = res.getConfiguration();
            if (config.getLayoutDirection() == 1) {
                this.mAdvanceKey = 21;
                this.mRetreatKey = 22;
                return;
            }
            this.mAdvanceKey = 22;
            this.mRetreatKey = 21;
        }

        public void setHoverListener(MenuItemHoverListener hoverListener) {
            this.mHoverListener = hoverListener;
        }

        public void clearSelection() {
            setSelectedPositionInt(-1);
            setNextSelectedPositionInt(-1);
        }

        @Override // android.widget.ListView, android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            ListMenuItemView selectedItem = (ListMenuItemView) getSelectedView();
            if (selectedItem != null && keyCode == this.mAdvanceKey) {
                if (selectedItem.isEnabled() && selectedItem.getItemData().hasSubMenu()) {
                    performItemClick(selectedItem, getSelectedItemPosition(), getSelectedItemId());
                }
                return true;
            } else if (selectedItem != null && keyCode == this.mRetreatKey) {
                setSelectedPositionInt(-1);
                setNextSelectedPositionInt(-1);
                ((MenuAdapter) getAdapter()).getAdapterMenu().close(false);
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }

        @Override // android.widget.DropDownListView, android.view.View
        public boolean onHoverEvent(MotionEvent ev) {
            int headersCount;
            MenuAdapter menuAdapter;
            int position;
            int itemPosition;
            if (this.mHoverListener != null) {
                ListAdapter adapter = getAdapter();
                if (adapter instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) adapter;
                    headersCount = headerAdapter.getHeadersCount();
                    menuAdapter = (MenuAdapter) headerAdapter.getWrappedAdapter();
                } else {
                    headersCount = 0;
                    menuAdapter = (MenuAdapter) adapter;
                }
                MenuItem menuItem = null;
                if (ev.getAction() != 10 && (position = pointToPosition((int) ev.getX(), (int) ev.getY())) != -1 && (itemPosition = position - headersCount) >= 0 && itemPosition < menuAdapter.getCount()) {
                    menuItem = menuAdapter.getItem(itemPosition);
                }
                MenuItem oldMenuItem = this.mHoveredMenuItem;
                if (oldMenuItem != menuItem) {
                    MenuBuilder menu = menuAdapter.getAdapterMenu();
                    if (oldMenuItem != null) {
                        this.mHoverListener.onItemHoverExit(menu, oldMenuItem);
                    }
                    this.mHoveredMenuItem = menuItem;
                    if (menuItem != null) {
                        this.mHoverListener.onItemHoverEnter(menu, menuItem);
                    }
                }
            }
            return super.onHoverEvent(ev);
        }
    }
}
