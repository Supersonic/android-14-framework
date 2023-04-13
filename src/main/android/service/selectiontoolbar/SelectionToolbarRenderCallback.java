package android.service.selectiontoolbar;

import android.view.selectiontoolbar.ToolbarMenuItem;
import android.view.selectiontoolbar.WidgetInfo;
/* loaded from: classes3.dex */
public interface SelectionToolbarRenderCallback {
    void onError(int i);

    void onMenuItemClicked(ToolbarMenuItem toolbarMenuItem);

    void onShown(WidgetInfo widgetInfo);

    void onToolbarShowTimeout();

    void onWidgetUpdated(WidgetInfo widgetInfo);
}
