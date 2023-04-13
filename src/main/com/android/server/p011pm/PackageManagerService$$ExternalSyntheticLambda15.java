package com.android.server.p011pm;

import com.android.server.p011pm.ApkChecksums;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.pm.PackageManagerService$$ExternalSyntheticLambda15 */
/* loaded from: classes2.dex */
public final /* synthetic */ class PackageManagerService$$ExternalSyntheticLambda15 implements ApkChecksums.Injector.Producer {
    public final /* synthetic */ PackageManagerServiceInjector f$0;

    public /* synthetic */ PackageManagerService$$ExternalSyntheticLambda15(PackageManagerServiceInjector packageManagerServiceInjector) {
        this.f$0 = packageManagerServiceInjector;
    }

    @Override // com.android.server.p011pm.ApkChecksums.Injector.Producer
    public final Object produce() {
        return this.f$0.getIncrementalManager();
    }
}
