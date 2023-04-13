package com.android.server.p011pm.pkg.component;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import java.util.List;
import java.util.Map;
/* renamed from: com.android.server.pm.pkg.component.ParsedComponent */
/* loaded from: classes2.dex */
public interface ParsedComponent {
    int getBanner();

    ComponentName getComponentName();

    int getDescriptionRes();

    int getFlags();

    int getIcon();

    List<ParsedIntentInfo> getIntents();

    int getLabelRes();

    int getLogo();

    Bundle getMetaData();

    String getName();

    CharSequence getNonLocalizedLabel();

    String getPackageName();

    Map<String, PackageManager.Property> getProperties();
}
