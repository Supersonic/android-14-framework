package com.android.internal.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.p008os.IBinder;
import android.p008os.health.ServiceHealthStats;
import android.util.EventLog;
import android.view.ContextMenu;
import android.view.View;
/* loaded from: classes2.dex */
public class ContextMenuBuilder extends MenuBuilder implements ContextMenu {
    public ContextMenuBuilder(Context context) {
        super(context);
    }

    @Override // android.view.ContextMenu
    public ContextMenu setHeaderIcon(Drawable icon) {
        return (ContextMenu) super.setHeaderIconInt(icon);
    }

    @Override // android.view.ContextMenu
    public ContextMenu setHeaderIcon(int iconRes) {
        return (ContextMenu) super.setHeaderIconInt(iconRes);
    }

    @Override // android.view.ContextMenu
    public ContextMenu setHeaderTitle(CharSequence title) {
        return (ContextMenu) super.setHeaderTitleInt(title);
    }

    @Override // android.view.ContextMenu
    public ContextMenu setHeaderTitle(int titleRes) {
        return (ContextMenu) super.setHeaderTitleInt(titleRes);
    }

    @Override // android.view.ContextMenu
    public ContextMenu setHeaderView(View view) {
        return (ContextMenu) super.setHeaderViewInt(view);
    }

    public MenuDialogHelper showDialog(View originalView, IBinder token) {
        if (originalView != null) {
            originalView.createContextMenu(this);
        }
        if (getVisibleItems().size() > 0) {
            EventLog.writeEvent((int) ServiceHealthStats.MEASUREMENT_START_SERVICE_COUNT, 1);
            MenuDialogHelper helper = new MenuDialogHelper(this);
            helper.show(token);
            return helper;
        }
        return null;
    }

    public MenuPopupHelper showPopup(Context context, View originalView, float x, float y) {
        if (originalView != null) {
            originalView.createContextMenu(this);
        }
        if (getVisibleItems().size() > 0) {
            EventLog.writeEvent((int) ServiceHealthStats.MEASUREMENT_START_SERVICE_COUNT, 1);
            int[] location = new int[2];
            originalView.getLocationOnScreen(location);
            MenuPopupHelper helper = new MenuPopupHelper(context, this, originalView, false, 16844033);
            helper.show(Math.round(x), Math.round(y));
            return helper;
        }
        return null;
    }
}
