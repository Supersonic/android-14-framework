package com.android.server.p011pm.pkg.component;
/* renamed from: com.android.server.pm.pkg.component.ParsedMainComponent */
/* loaded from: classes2.dex */
public interface ParsedMainComponent extends ParsedComponent {
    String[] getAttributionTags();

    String getClassName();

    int getOrder();

    String getProcessName();

    String getSplitName();

    boolean isDirectBootAware();

    boolean isEnabled();

    boolean isExported();
}
