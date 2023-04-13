package android.widget;

import android.view.MenuItem;
import com.android.internal.view.menu.MenuBuilder;
/* loaded from: classes4.dex */
public interface MenuItemHoverListener {
    void onItemHoverEnter(MenuBuilder menuBuilder, MenuItem menuItem);

    void onItemHoverExit(MenuBuilder menuBuilder, MenuItem menuItem);
}
