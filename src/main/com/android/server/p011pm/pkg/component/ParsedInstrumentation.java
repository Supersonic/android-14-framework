package com.android.server.p011pm.pkg.component;
/* renamed from: com.android.server.pm.pkg.component.ParsedInstrumentation */
/* loaded from: classes2.dex */
public interface ParsedInstrumentation extends ParsedComponent {
    String getTargetPackage();

    String getTargetProcesses();

    boolean isFunctionalTest();

    boolean isHandleProfiling();
}
