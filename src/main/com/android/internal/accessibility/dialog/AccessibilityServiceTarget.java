package com.android.internal.accessibility.dialog;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import com.android.internal.accessibility.util.ShortcutUtils;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class AccessibilityServiceTarget extends AccessibilityTarget {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessibilityServiceTarget(Context context, int shortcutType, int fragmentType, AccessibilityServiceInfo serviceInfo) {
        super(context, shortcutType, fragmentType, ShortcutUtils.isShortcutContained(context, shortcutType, serviceInfo.getComponentName().flattenToString()), serviceInfo.getComponentName().flattenToString(), serviceInfo.getResolveInfo().loadLabel(context.getPackageManager()), serviceInfo.getResolveInfo().loadIcon(context.getPackageManager()), ShortcutUtils.convertToKey(ShortcutUtils.convertToUserType(shortcutType)));
    }
}
