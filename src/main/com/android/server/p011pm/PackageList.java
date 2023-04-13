package com.android.server.p011pm;

import android.content.p000pm.PackageManagerInternal;
import com.android.server.LocalServices;
import java.util.List;
/* renamed from: com.android.server.pm.PackageList */
/* loaded from: classes2.dex */
public class PackageList implements PackageManagerInternal.PackageListObserver, AutoCloseable {
    public final List<String> mPackageNames;
    public final PackageManagerInternal.PackageListObserver mWrappedObserver;

    public PackageList(List<String> list, PackageManagerInternal.PackageListObserver packageListObserver) {
        this.mPackageNames = list;
        this.mWrappedObserver = packageListObserver;
    }

    @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
    public void onPackageAdded(String str, int i) {
        PackageManagerInternal.PackageListObserver packageListObserver = this.mWrappedObserver;
        if (packageListObserver != null) {
            packageListObserver.onPackageAdded(str, i);
        }
    }

    @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
    public void onPackageChanged(String str, int i) {
        PackageManagerInternal.PackageListObserver packageListObserver = this.mWrappedObserver;
        if (packageListObserver != null) {
            packageListObserver.onPackageChanged(str, i);
        }
    }

    @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
    public void onPackageRemoved(String str, int i) {
        PackageManagerInternal.PackageListObserver packageListObserver = this.mWrappedObserver;
        if (packageListObserver != null) {
            packageListObserver.onPackageRemoved(str, i);
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() throws Exception {
        ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).removePackageListObserver(this);
    }

    public List<String> getPackageNames() {
        return this.mPackageNames;
    }
}
