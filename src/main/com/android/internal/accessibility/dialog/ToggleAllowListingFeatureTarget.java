package com.android.internal.accessibility.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import com.android.internal.C4057R;
import com.android.internal.accessibility.dialog.TargetAdapter;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class ToggleAllowListingFeatureTarget extends AccessibilityTarget {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ToggleAllowListingFeatureTarget(Context context, int shortcutType, boolean isShortcutSwitched, String id, CharSequence label, Drawable icon, String key) {
        super(context, shortcutType, 2, isShortcutSwitched, id, label, icon, key);
        int statusResId;
        if (isFeatureEnabled()) {
            statusResId = C4057R.string.accessibility_shortcut_menu_item_status_on;
        } else {
            statusResId = C4057R.string.accessibility_shortcut_menu_item_status_off;
        }
        setStateDescription(getContext().getString(statusResId));
    }

    @Override // com.android.internal.accessibility.dialog.AccessibilityTarget, com.android.internal.accessibility.dialog.TargetOperations
    public void updateActionItem(TargetAdapter.ViewHolder holder, int shortcutMenuMode) {
        super.updateActionItem(holder, shortcutMenuMode);
        boolean isEditMenuMode = shortcutMenuMode == 1;
        holder.mStatusView.setVisibility(isEditMenuMode ? 8 : 0);
        holder.mStatusView.setText(getStateDescription());
    }

    private boolean isFeatureEnabled() {
        return Settings.Secure.getInt(getContext().getContentResolver(), getKey(), 0) == 1;
    }
}
