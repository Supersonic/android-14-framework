package com.android.server.p011pm.pkg.component;

import android.content.IntentFilter;
/* renamed from: com.android.server.pm.pkg.component.ParsedIntentInfo */
/* loaded from: classes2.dex */
public interface ParsedIntentInfo {
    int getIcon();

    IntentFilter getIntentFilter();

    int getLabelRes();

    CharSequence getNonLocalizedLabel();

    boolean isHasDefault();
}
