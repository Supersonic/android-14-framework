package com.android.internal.widget.floatingtoolbar;

import android.content.Context;
import android.graphics.Rect;
import android.view.MenuItem;
import android.view.View;
import android.view.selectiontoolbar.SelectionToolbarManager;
import android.widget.PopupWindow;
import java.util.List;
/* loaded from: classes5.dex */
public interface FloatingToolbarPopup {
    void dismiss();

    void hide();

    boolean isHidden();

    boolean isShowing();

    boolean setOutsideTouchable(boolean z, PopupWindow.OnDismissListener onDismissListener);

    void setSuggestedWidth(int i);

    void setWidthChanged(boolean z);

    void show(List<MenuItem> list, MenuItem.OnMenuItemClickListener onMenuItemClickListener, Rect rect);

    static FloatingToolbarPopup createInstance(Context context, View parent) {
        boolean enabled = SelectionToolbarManager.isRemoteSelectionToolbarEnabled(context);
        if (enabled) {
            return new RemoteFloatingToolbarPopup(context, parent);
        }
        return new LocalFloatingToolbarPopup(context, parent);
    }
}
