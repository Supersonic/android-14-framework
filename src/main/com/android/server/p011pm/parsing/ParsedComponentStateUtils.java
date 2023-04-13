package com.android.server.p011pm.parsing;

import android.util.Pair;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.component.ParsedComponent;
/* renamed from: com.android.server.pm.parsing.ParsedComponentStateUtils */
/* loaded from: classes2.dex */
public class ParsedComponentStateUtils {
    public static Pair<CharSequence, Integer> getNonLocalizedLabelAndIcon(ParsedComponent parsedComponent, PackageStateInternal packageStateInternal, int i) {
        CharSequence nonLocalizedLabel = parsedComponent.getNonLocalizedLabel();
        int icon = parsedComponent.getIcon();
        Pair<String, Integer> overrideLabelIconForComponent = packageStateInternal == null ? null : packageStateInternal.getUserStateOrDefault(i).getOverrideLabelIconForComponent(parsedComponent.getComponentName());
        if (overrideLabelIconForComponent != null) {
            Object obj = overrideLabelIconForComponent.first;
            if (obj != null) {
                nonLocalizedLabel = (CharSequence) obj;
            }
            Object obj2 = overrideLabelIconForComponent.second;
            if (obj2 != null) {
                icon = ((Integer) obj2).intValue();
            }
        }
        return Pair.create(nonLocalizedLabel, Integer.valueOf(icon));
    }
}
