package com.android.server.p011pm.pkg;

import android.content.pm.SharedLibraryInfo;
import android.content.pm.VersionedPackage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* renamed from: com.android.server.pm.pkg.SharedLibraryWrapper */
/* loaded from: classes2.dex */
public class SharedLibraryWrapper implements SharedLibrary {
    public List<SharedLibrary> cachedDependenciesList;
    public final SharedLibraryInfo mInfo;

    public SharedLibraryWrapper(SharedLibraryInfo sharedLibraryInfo) {
        this.mInfo = sharedLibraryInfo;
    }

    public SharedLibraryInfo getInfo() {
        return this.mInfo;
    }

    @Override // com.android.server.p011pm.pkg.SharedLibrary
    public String getPath() {
        return this.mInfo.getPath();
    }

    @Override // com.android.server.p011pm.pkg.SharedLibrary
    public String getPackageName() {
        return this.mInfo.getPackageName();
    }

    @Override // com.android.server.p011pm.pkg.SharedLibrary
    public String getName() {
        return this.mInfo.getName();
    }

    @Override // com.android.server.p011pm.pkg.SharedLibrary
    public List<String> getAllCodePaths() {
        return Collections.unmodifiableList(this.mInfo.getAllCodePaths());
    }

    @Override // com.android.server.p011pm.pkg.SharedLibrary
    public long getVersion() {
        return this.mInfo.getLongVersion();
    }

    @Override // com.android.server.p011pm.pkg.SharedLibrary
    public int getType() {
        return this.mInfo.getType();
    }

    @Override // com.android.server.p011pm.pkg.SharedLibrary
    public boolean isNative() {
        return this.mInfo.isNative();
    }

    @Override // com.android.server.p011pm.pkg.SharedLibrary
    public VersionedPackage getDeclaringPackage() {
        return this.mInfo.getDeclaringPackage();
    }

    @Override // com.android.server.p011pm.pkg.SharedLibrary
    public List<VersionedPackage> getDependentPackages() {
        return Collections.unmodifiableList(this.mInfo.getDependentPackages());
    }

    @Override // com.android.server.p011pm.pkg.SharedLibrary
    public List<SharedLibrary> getDependencies() {
        if (this.cachedDependenciesList == null) {
            List dependencies = this.mInfo.getDependencies();
            if (dependencies == null) {
                this.cachedDependenciesList = Collections.emptyList();
            } else {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < dependencies.size(); i++) {
                    arrayList.add(new SharedLibraryWrapper((SharedLibraryInfo) dependencies.get(i)));
                }
                this.cachedDependenciesList = Collections.unmodifiableList(arrayList);
            }
        }
        return this.cachedDependenciesList;
    }
}
