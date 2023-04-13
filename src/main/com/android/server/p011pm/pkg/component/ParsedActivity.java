package com.android.server.p011pm.pkg.component;

import android.content.pm.ActivityInfo;
import java.util.Set;
/* renamed from: com.android.server.pm.pkg.component.ParsedActivity */
/* loaded from: classes2.dex */
public interface ParsedActivity extends ParsedMainComponent {
    int getColorMode();

    int getConfigChanges();

    int getDocumentLaunchMode();

    Set<String> getKnownActivityEmbeddingCerts();

    int getLaunchMode();

    int getLockTaskLaunchMode();

    float getMaxAspectRatio();

    int getMaxRecents();

    float getMinAspectRatio();

    String getParentActivityName();

    String getPermission();

    int getPersistableMode();

    int getPrivateFlags();

    String getRequestedVrComponent();

    String getRequiredDisplayCategory();

    int getResizeMode();

    int getRotationAnimation();

    int getScreenOrientation();

    int getSoftInputMode();

    String getTargetActivity();

    String getTaskAffinity();

    int getTheme();

    int getUiOptions();

    ActivityInfo.WindowLayout getWindowLayout();

    boolean isSupportsSizeChanges();

    static ParsedActivity makeAppDetailsActivity(String str, String str2, int i, String str3, boolean z) {
        return ParsedActivityImpl.makeAppDetailsActivity(str, str2, i, str3, z);
    }
}
