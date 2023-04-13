package com.android.server.p011pm.pkg.component;

import android.annotation.SuppressLint;
import android.util.ArrayMap;
import java.util.Set;
/* renamed from: com.android.server.pm.pkg.component.ParsedProcess */
/* loaded from: classes2.dex */
public interface ParsedProcess {
    @SuppressLint({"ConcreteCollection"})
    ArrayMap<String, String> getAppClassNamesByPackage();

    Set<String> getDeniedPermissions();

    int getGwpAsanMode();

    int getMemtagMode();

    String getName();

    int getNativeHeapZeroInitialized();
}
