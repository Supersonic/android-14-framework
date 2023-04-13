package android.view;

import android.graphics.drawable.Drawable;
/* loaded from: classes4.dex */
public interface ContextMenu extends Menu {

    /* loaded from: classes4.dex */
    public interface ContextMenuInfo {
    }

    void clearHeader();

    ContextMenu setHeaderIcon(int i);

    ContextMenu setHeaderIcon(Drawable drawable);

    ContextMenu setHeaderTitle(int i);

    ContextMenu setHeaderTitle(CharSequence charSequence);

    ContextMenu setHeaderView(View view);
}
