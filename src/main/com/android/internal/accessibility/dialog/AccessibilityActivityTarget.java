package com.android.internal.accessibility.dialog;

import android.accessibilityservice.AccessibilityShortcutInfo;
import android.content.Context;
import com.android.internal.accessibility.util.ShortcutUtils;
/* loaded from: classes4.dex */
class AccessibilityActivityTarget extends AccessibilityTarget {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessibilityActivityTarget(Context context, int shortcutType, AccessibilityShortcutInfo shortcutInfo) {
        super(context, shortcutType, 3, ShortcutUtils.isShortcutContained(context, shortcutType, shortcutInfo.getComponentName().flattenToString()), shortcutInfo.getComponentName().flattenToString(), shortcutInfo.getActivityInfo().loadLabel(context.getPackageManager()), shortcutInfo.getActivityInfo().loadIcon(context.getPackageManager()), ShortcutUtils.convertToKey(ShortcutUtils.convertToUserType(shortcutType)));
    }
}
