package com.android.server.p011pm;

import android.content.pm.SharedLibraryInfo;
import android.content.pm.SigningDetails;
import android.util.ArrayMap;
import com.android.server.p011pm.pkg.AndroidPackage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/* renamed from: com.android.server.pm.ReconciledPackage */
/* loaded from: classes2.dex */
public final class ReconciledPackage {
    public final Map<String, AndroidPackage> mAllPackages;
    public final List<SharedLibraryInfo> mAllowedSharedLibraryInfos;
    public ArrayList<SharedLibraryInfo> mCollectedSharedLibraryInfos;
    public final DeletePackageAction mDeletePackageAction;
    public final InstallRequest mInstallRequest;
    public final List<InstallRequest> mInstallRequests;
    public final boolean mRemoveAppKeySetData;
    public final boolean mSharedUserSignaturesChanged;
    public final SigningDetails mSigningDetails;

    public ReconciledPackage(List<InstallRequest> list, Map<String, AndroidPackage> map, InstallRequest installRequest, DeletePackageAction deletePackageAction, List<SharedLibraryInfo> list2, SigningDetails signingDetails, boolean z, boolean z2) {
        this.mInstallRequests = list;
        this.mAllPackages = map;
        this.mInstallRequest = installRequest;
        this.mDeletePackageAction = deletePackageAction;
        this.mAllowedSharedLibraryInfos = list2;
        this.mSigningDetails = signingDetails;
        this.mSharedUserSignaturesChanged = z;
        this.mRemoveAppKeySetData = z2;
    }

    public Map<String, AndroidPackage> getCombinedAvailablePackages() {
        ArrayMap arrayMap = new ArrayMap(this.mAllPackages.size() + this.mInstallRequests.size());
        arrayMap.putAll(this.mAllPackages);
        for (InstallRequest installRequest : this.mInstallRequests) {
            arrayMap.put(installRequest.getScannedPackageSetting().getPackageName(), installRequest.getParsedPackage());
        }
        return arrayMap;
    }
}
