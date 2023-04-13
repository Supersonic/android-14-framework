package com.android.internal.widget.floatingtoolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.selectiontoolbar.ISelectionToolbarCallback;
import android.view.selectiontoolbar.SelectionToolbarManager;
import android.view.selectiontoolbar.ShowInfo;
import android.view.selectiontoolbar.ToolbarMenuItem;
import android.view.selectiontoolbar.WidgetInfo;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.android.internal.C4057R;
import com.android.internal.widget.floatingtoolbar.LocalFloatingToolbarPopup;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
/* loaded from: classes5.dex */
public final class RemoteFloatingToolbarPopup implements FloatingToolbarPopup {
    private static final boolean DEBUG = Log.isLoggable(FloatingToolbar.FLOATING_TOOLBAR_TAG, 2);
    private static final int TOOLBAR_STATE_DISMISSED = 3;
    private static final int TOOLBAR_STATE_HIDDEN = 2;
    private static final int TOOLBAR_STATE_SHOWN = 1;
    private final boolean mIsLightTheme;
    private MenuItem.OnMenuItemClickListener mMenuItemClickListener;
    private List<MenuItem> mMenuItems;
    private final View mParent;
    private final PopupWindow mPopupWindow;
    private final SelectionToolbarManager mSelectionToolbarManager;
    private int mState;
    private int mSuggestedWidth;
    private final Rect mPreviousContentRect = new Rect();
    private final Rect mScreenViewPort = new Rect();
    private boolean mWidthChanged = true;
    private final int[] mCoordsOnScreen = new int[2];
    private final int[] mCoordsOnWindow = new int[2];
    private final SelectionToolbarCallbackImpl mSelectionToolbarCallback = new SelectionToolbarCallbackImpl(this);
    private long mFloatingToolbarToken = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes5.dex */
    public @interface ToolbarState {
    }

    public RemoteFloatingToolbarPopup(Context context, View parent) {
        this.mParent = (View) Objects.requireNonNull(parent);
        this.mPopupWindow = createPopupWindow(context);
        this.mSelectionToolbarManager = (SelectionToolbarManager) context.getSystemService(SelectionToolbarManager.class);
        this.mIsLightTheme = isLightTheme(context);
    }

    private boolean isLightTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[]{16844176});
        boolean isLightTheme = a.getBoolean(0, true);
        a.recycle();
        return isLightTheme;
    }

    @Override // com.android.internal.widget.floatingtoolbar.FloatingToolbarPopup
    public void show(List<MenuItem> menuItems, MenuItem.OnMenuItemClickListener menuItemClickListener, Rect contentRect) {
        int suggestWidth;
        Objects.requireNonNull(menuItems);
        Objects.requireNonNull(menuItemClickListener);
        if (isShowing() && Objects.equals(menuItems, this.mMenuItems) && Objects.equals(contentRect, this.mPreviousContentRect)) {
            if (DEBUG) {
                Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "Ignore duplicate show() for the same content.");
                return;
            }
            return;
        }
        List<MenuItem> list = this.mMenuItems;
        boolean isLayoutRequired = list == null || !MenuItemRepr.reprEquals(menuItems, list) || this.mWidthChanged;
        if (isLayoutRequired) {
            this.mSelectionToolbarManager.dismissToolbar(this.mFloatingToolbarToken);
            doDismissPopupWindow();
        }
        this.mMenuItemClickListener = menuItemClickListener;
        this.mMenuItems = menuItems;
        this.mParent.getWindowVisibleDisplayFrame(this.mScreenViewPort);
        int i = this.mSuggestedWidth;
        if (i > 0) {
            suggestWidth = i;
        } else {
            suggestWidth = this.mParent.getResources().getDimensionPixelSize(C4057R.dimen.floating_toolbar_preferred_width);
        }
        ShowInfo showInfo = new ShowInfo(this.mFloatingToolbarToken, isLayoutRequired, getToolbarMenuItems(this.mMenuItems), contentRect, suggestWidth, this.mScreenViewPort, this.mParent.getViewRootImpl().getInputToken(), this.mIsLightTheme);
        if (DEBUG) {
            Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "RemoteFloatingToolbarPopup.show() for " + showInfo);
        }
        this.mSelectionToolbarManager.showToolbar(showInfo, this.mSelectionToolbarCallback);
        this.mPreviousContentRect.set(contentRect);
    }

    @Override // com.android.internal.widget.floatingtoolbar.FloatingToolbarPopup
    public void dismiss() {
        if (this.mState == 3) {
            Log.m104w(FloatingToolbar.FLOATING_TOOLBAR_TAG, "The floating toolbar already dismissed.");
            return;
        }
        if (DEBUG) {
            Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "RemoteFloatingToolbarPopup.dismiss().");
        }
        this.mSelectionToolbarManager.dismissToolbar(this.mFloatingToolbarToken);
        doDismissPopupWindow();
    }

    @Override // com.android.internal.widget.floatingtoolbar.FloatingToolbarPopup
    public void hide() {
        int i = this.mState;
        if (i == 3 || i == 2) {
            if (DEBUG) {
                Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "The floating toolbar already dismissed or hidden.");
                return;
            }
            return;
        }
        if (DEBUG) {
            Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "RemoteFloatingToolbarPopup.hide().");
        }
        this.mSelectionToolbarManager.hideToolbar(this.mFloatingToolbarToken);
        this.mState = 2;
        this.mPopupWindow.dismiss();
    }

    @Override // com.android.internal.widget.floatingtoolbar.FloatingToolbarPopup
    public void setSuggestedWidth(int suggestedWidth) {
        int difference = Math.abs(suggestedWidth - this.mSuggestedWidth);
        this.mWidthChanged = ((double) difference) > ((double) this.mSuggestedWidth) * 0.2d;
        this.mSuggestedWidth = suggestedWidth;
    }

    @Override // com.android.internal.widget.floatingtoolbar.FloatingToolbarPopup
    public void setWidthChanged(boolean widthChanged) {
        this.mWidthChanged = widthChanged;
    }

    @Override // com.android.internal.widget.floatingtoolbar.FloatingToolbarPopup
    public boolean isHidden() {
        return this.mState == 2;
    }

    @Override // com.android.internal.widget.floatingtoolbar.FloatingToolbarPopup
    public boolean isShowing() {
        return this.mState == 1;
    }

    @Override // com.android.internal.widget.floatingtoolbar.FloatingToolbarPopup
    public boolean setOutsideTouchable(boolean outsideTouchable, PopupWindow.OnDismissListener onDismiss) {
        if (this.mState == 3) {
            return false;
        }
        boolean ret = false;
        if (this.mPopupWindow.isOutsideTouchable() ^ outsideTouchable) {
            this.mPopupWindow.setOutsideTouchable(outsideTouchable);
            this.mPopupWindow.setFocusable(!outsideTouchable);
            this.mPopupWindow.update();
            ret = true;
        }
        this.mPopupWindow.setOnDismissListener(onDismiss);
        return ret;
    }

    private void updatePopupWindowContent(WidgetInfo widgetInfo) {
        if (DEBUG) {
            Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "updatePopupWindowContent.");
        }
        ViewGroup contentContainer = (ViewGroup) this.mPopupWindow.getContentView();
        contentContainer.removeAllViews();
        SurfaceView surfaceView = new SurfaceView(this.mParent.getContext());
        surfaceView.setZOrderOnTop(true);
        surfaceView.getHolder().setFormat(-2);
        surfaceView.setChildSurfacePackage(widgetInfo.getSurfacePackage());
        contentContainer.addView(surfaceView);
    }

    private MenuItem getMenuItemByToolbarMenuItem(ToolbarMenuItem toolbarMenuItem) {
        for (MenuItem item : this.mMenuItems) {
            if (toolbarMenuItem.getItemId() == item.getItemId()) {
                return item;
            }
        }
        return null;
    }

    private Point getCoordinatesInWindow(int x, int y) {
        this.mParent.getRootView().getLocationOnScreen(this.mCoordsOnScreen);
        this.mParent.getRootView().getLocationInWindow(this.mCoordsOnWindow);
        int[] iArr = this.mCoordsOnScreen;
        int i = iArr[0];
        int[] iArr2 = this.mCoordsOnWindow;
        int windowLeftOnScreen = i - iArr2[0];
        int windowTopOnScreen = iArr[1] - iArr2[1];
        return new Point(Math.max(0, x - windowLeftOnScreen), Math.max(0, y - windowTopOnScreen));
    }

    private static List<ToolbarMenuItem> getToolbarMenuItems(List<MenuItem> menuItems) {
        List<ToolbarMenuItem> list = new ArrayList<>(menuItems.size());
        for (MenuItem menuItem : menuItems) {
            ToolbarMenuItem toolbarMenuItem = new ToolbarMenuItem.Builder(menuItem.getItemId(), menuItem.getTitle(), menuItem.getContentDescription(), menuItem.getGroupId(), convertDrawableToIcon(menuItem.getIcon()), menuItem.getTooltipText(), ToolbarMenuItem.getPriorityFromMenuItem(menuItem)).build();
            list.add(toolbarMenuItem);
        }
        return list;
    }

    private static Icon convertDrawableToIcon(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return Icon.createWithBitmap(bitmapDrawable.getBitmap());
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return Icon.createWithBitmap(bitmap);
    }

    private static PopupWindow createPopupWindow(Context content) {
        ViewGroup popupContentHolder = new LinearLayout(content);
        PopupWindow popupWindow = new PopupWindow(popupContentHolder);
        popupWindow.setClippingEnabled(false);
        popupWindow.setWindowLayoutType(1005);
        popupWindow.setAnimationStyle(0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        return popupWindow;
    }

    private void doDismissPopupWindow() {
        if (DEBUG) {
            Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "RemoteFloatingToolbarPopup.doDismiss().");
        }
        this.mState = 3;
        this.mMenuItems = null;
        this.mMenuItemClickListener = null;
        this.mFloatingToolbarToken = 0L;
        this.mSuggestedWidth = 0;
        this.mWidthChanged = true;
        resetCoords();
        this.mPreviousContentRect.setEmpty();
        this.mScreenViewPort.setEmpty();
        this.mPopupWindow.dismiss();
    }

    private void resetCoords() {
        int[] iArr = this.mCoordsOnScreen;
        iArr[0] = 0;
        iArr[1] = 0;
        int[] iArr2 = this.mCoordsOnWindow;
        iArr2[0] = 0;
        iArr2[1] = 0;
    }

    private void runOnUiThread(Runnable runnable) {
        this.mParent.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onShow(final WidgetInfo info) {
        runOnUiThread(new Runnable() { // from class: com.android.internal.widget.floatingtoolbar.RemoteFloatingToolbarPopup$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                RemoteFloatingToolbarPopup.this.lambda$onShow$0(info);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onShow$0(WidgetInfo info) {
        this.mFloatingToolbarToken = info.getWidgetToken();
        this.mState = 1;
        updatePopupWindowContent(info);
        Rect contentRect = info.getContentRect();
        this.mPopupWindow.setWidth(contentRect.width());
        this.mPopupWindow.setHeight(contentRect.height());
        Point coords = getCoordinatesInWindow(contentRect.left, contentRect.top);
        this.mPopupWindow.showAtLocation(this.mParent, 0, coords.f76x, coords.f77y);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onWidgetUpdated(final WidgetInfo info) {
        runOnUiThread(new Runnable() { // from class: com.android.internal.widget.floatingtoolbar.RemoteFloatingToolbarPopup$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RemoteFloatingToolbarPopup.this.lambda$onWidgetUpdated$1(info);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onWidgetUpdated$1(WidgetInfo info) {
        if (!isShowing()) {
            Log.m104w(FloatingToolbar.FLOATING_TOOLBAR_TAG, "onWidgetUpdated(): The widget isn't showing.");
            return;
        }
        updatePopupWindowContent(info);
        Rect contentRect = info.getContentRect();
        Point coords = getCoordinatesInWindow(contentRect.left, contentRect.top);
        if (DEBUG) {
            Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "PopupWindow x= " + coords.f76x + " y= " + coords.f77y + " w=" + contentRect.width() + " h=" + contentRect.height());
        }
        this.mPopupWindow.update(coords.f76x, coords.f77y, contentRect.width(), contentRect.height());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onToolbarShowTimeout() {
        runOnUiThread(new Runnable() { // from class: com.android.internal.widget.floatingtoolbar.RemoteFloatingToolbarPopup$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                RemoteFloatingToolbarPopup.this.lambda$onToolbarShowTimeout$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onToolbarShowTimeout$2() {
        if (this.mState == 3) {
            return;
        }
        doDismissPopupWindow();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMenuItemClicked(final ToolbarMenuItem toolbarMenuItem) {
        runOnUiThread(new Runnable() { // from class: com.android.internal.widget.floatingtoolbar.RemoteFloatingToolbarPopup$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RemoteFloatingToolbarPopup.this.lambda$onMenuItemClicked$3(toolbarMenuItem);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onMenuItemClicked$3(ToolbarMenuItem toolbarMenuItem) {
        if (this.mMenuItems == null || this.mMenuItemClickListener == null) {
            return;
        }
        MenuItem item = getMenuItemByToolbarMenuItem(toolbarMenuItem);
        if (DEBUG) {
            Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "SelectionToolbarCallbackImpl onMenuItemClicked. toolbarMenuItem=" + toolbarMenuItem + " item=" + item);
        }
        if (item != null) {
            this.mMenuItemClickListener.onMenuItemClick(item);
        } else {
            Log.m110e(FloatingToolbar.FLOATING_TOOLBAR_TAG, "onMenuItemClicked: cannot find menu item.");
        }
    }

    /* loaded from: classes5.dex */
    private static class SelectionToolbarCallbackImpl extends ISelectionToolbarCallback.Stub {
        private final WeakReference<RemoteFloatingToolbarPopup> mRemotePopup;

        SelectionToolbarCallbackImpl(RemoteFloatingToolbarPopup popup) {
            this.mRemotePopup = new WeakReference<>(popup);
        }

        @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
        public void onShown(WidgetInfo info) {
            if (RemoteFloatingToolbarPopup.DEBUG) {
                Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "SelectionToolbarCallbackImpl onShown: " + info);
            }
            RemoteFloatingToolbarPopup remoteFloatingToolbarPopup = this.mRemotePopup.get();
            if (remoteFloatingToolbarPopup != null) {
                remoteFloatingToolbarPopup.onShow(info);
            } else {
                Log.m104w(FloatingToolbar.FLOATING_TOOLBAR_TAG, "Lost remoteFloatingToolbarPopup reference for onShown.");
            }
        }

        @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
        public void onWidgetUpdated(WidgetInfo info) {
            if (RemoteFloatingToolbarPopup.DEBUG) {
                Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "SelectionToolbarCallbackImpl onWidgetUpdated: info = " + info);
            }
            RemoteFloatingToolbarPopup remoteFloatingToolbarPopup = this.mRemotePopup.get();
            if (remoteFloatingToolbarPopup != null) {
                remoteFloatingToolbarPopup.onWidgetUpdated(info);
            } else {
                Log.m104w(FloatingToolbar.FLOATING_TOOLBAR_TAG, "Lost remoteFloatingToolbarPopup reference for onWidgetUpdated.");
            }
        }

        @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
        public void onToolbarShowTimeout() {
            RemoteFloatingToolbarPopup remoteFloatingToolbarPopup = this.mRemotePopup.get();
            if (remoteFloatingToolbarPopup != null) {
                remoteFloatingToolbarPopup.onToolbarShowTimeout();
            } else {
                Log.m104w(FloatingToolbar.FLOATING_TOOLBAR_TAG, "Lost remoteFloatingToolbarPopup reference for onToolbarShowTimeout.");
            }
        }

        @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
        public void onMenuItemClicked(ToolbarMenuItem toolbarMenuItem) {
            RemoteFloatingToolbarPopup remoteFloatingToolbarPopup = this.mRemotePopup.get();
            if (remoteFloatingToolbarPopup != null) {
                remoteFloatingToolbarPopup.onMenuItemClicked(toolbarMenuItem);
            } else {
                Log.m104w(FloatingToolbar.FLOATING_TOOLBAR_TAG, "Lost remoteFloatingToolbarPopup reference for onMenuItemClicked.");
            }
        }

        @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
        public void onError(int errorCode) {
            if (RemoteFloatingToolbarPopup.DEBUG) {
                Log.m106v(FloatingToolbar.FLOATING_TOOLBAR_TAG, "SelectionToolbarCallbackImpl onError: " + errorCode);
            }
        }
    }

    /* loaded from: classes5.dex */
    static final class MenuItemRepr {
        public final int mGroupId;
        private final Drawable mIcon;
        public final int mItemId;
        public final String mTitle;

        private MenuItemRepr(int itemId, int groupId, CharSequence title, Drawable icon) {
            this.mItemId = itemId;
            this.mGroupId = groupId;
            this.mTitle = title == null ? null : title.toString();
            this.mIcon = icon;
        }

        /* renamed from: of */
        public static MenuItemRepr m13of(MenuItem menuItem) {
            return new MenuItemRepr(menuItem.getItemId(), menuItem.getGroupId(), menuItem.getTitle(), menuItem.getIcon());
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mItemId), Integer.valueOf(this.mGroupId), this.mTitle, this.mIcon);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof LocalFloatingToolbarPopup.MenuItemRepr) {
                MenuItemRepr other = (MenuItemRepr) o;
                return this.mItemId == other.mItemId && this.mGroupId == other.mGroupId && TextUtils.equals(this.mTitle, other.mTitle) && Objects.equals(this.mIcon, other.mIcon);
            }
            return false;
        }

        public static boolean reprEquals(Collection<MenuItem> menuItems1, Collection<MenuItem> menuItems2) {
            if (menuItems1.size() != menuItems2.size()) {
                return false;
            }
            Iterator<MenuItem> menuItems2Iter = menuItems2.iterator();
            for (MenuItem menuItem1 : menuItems1) {
                MenuItem menuItem2 = menuItems2Iter.next();
                if (!m13of(menuItem1).equals(m13of(menuItem2))) {
                    return false;
                }
            }
            return true;
        }
    }
}
