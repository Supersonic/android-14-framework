package com.android.internal.accessibility.dialog;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.widget.Toast;
import com.android.internal.C4057R;
import com.android.internal.accessibility.util.AccessibilityUtils;
import com.android.internal.accessibility.util.ShortcutUtils;
/* loaded from: classes4.dex */
class VolumeShortcutToggleAccessibilityServiceTarget extends AccessibilityServiceTarget {
    /* JADX INFO: Access modifiers changed from: package-private */
    public VolumeShortcutToggleAccessibilityServiceTarget(Context context, int shortcutType, AccessibilityServiceInfo serviceInfo) {
        super(context, shortcutType, 0, serviceInfo);
    }

    @Override // com.android.internal.accessibility.dialog.AccessibilityTarget, com.android.internal.accessibility.dialog.OnTargetCheckedChangeListener
    public void onCheckedChanged(boolean isChecked) {
        switch (getShortcutType()) {
            case 0:
                onCheckedFromAccessibilityButton(isChecked);
                return;
            case 1:
                super.onCheckedChanged(isChecked);
                return;
            default:
                throw new IllegalStateException("Unexpected shortcut type");
        }
    }

    private void onCheckedFromAccessibilityButton(boolean isChecked) {
        setShortcutEnabled(isChecked);
        ComponentName componentName = ComponentName.unflattenFromString(getId());
        AccessibilityUtils.setAccessibilityServiceState(getContext(), componentName, isChecked);
        if (!isChecked) {
            ShortcutUtils.optOutValueFromSettings(getContext(), 2, getId());
            String warningText = getContext().getString(C4057R.string.accessibility_uncheck_legacy_item_warning, getLabel());
            Toast.makeText(getContext(), warningText, 0).show();
        }
    }
}
