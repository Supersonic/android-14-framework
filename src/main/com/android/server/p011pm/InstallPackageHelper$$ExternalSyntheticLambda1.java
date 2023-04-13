package com.android.server.p011pm;

import java.util.function.Supplier;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.pm.InstallPackageHelper$$ExternalSyntheticLambda1 */
/* loaded from: classes2.dex */
public final /* synthetic */ class InstallPackageHelper$$ExternalSyntheticLambda1 implements Supplier {
    public final /* synthetic */ PackageManagerService f$0;

    public /* synthetic */ InstallPackageHelper$$ExternalSyntheticLambda1(PackageManagerService packageManagerService) {
        this.f$0 = packageManagerService;
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        return this.f$0.snapshotComputer();
    }
}
