package com.android.server.p008om;

import android.content.om.OverlayInfo;
import android.content.om.OverlayableInfo;
import android.os.FabricatedOverlayInfo;
import android.os.FabricatedOverlayInternal;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Slog;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import java.io.IOException;
import java.util.List;
/* renamed from: com.android.server.om.IdmapManager */
/* loaded from: classes2.dex */
public final class IdmapManager {
    public static final boolean VENDOR_IS_Q_OR_LATER;
    public final String mConfigSignaturePackage;
    public final IdmapDaemon mIdmapDaemon;
    public final PackageManagerHelper mPackageManager;

    static {
        boolean z = true;
        try {
            if (Integer.parseInt(SystemProperties.get("ro.vndk.version", "29")) < 29) {
                z = false;
            }
        } catch (NumberFormatException unused) {
        }
        VENDOR_IS_Q_OR_LATER = z;
    }

    public IdmapManager(IdmapDaemon idmapDaemon, PackageManagerHelper packageManagerHelper) {
        this.mPackageManager = packageManagerHelper;
        this.mIdmapDaemon = idmapDaemon;
        this.mConfigSignaturePackage = packageManagerHelper.getConfigSignaturePackage();
    }

    public int createIdmap(AndroidPackage androidPackage, PackageState packageState, AndroidPackage androidPackage2, String str, String str2, int i) {
        String path = androidPackage.getSplits().get(0).getPath();
        try {
            int calculateFulfilledPolicies = calculateFulfilledPolicies(androidPackage, packageState, androidPackage2, i);
            boolean enforceOverlayable = enforceOverlayable(packageState, androidPackage2);
            if (this.mIdmapDaemon.verifyIdmap(path, str, str2, calculateFulfilledPolicies, enforceOverlayable, i)) {
                return 1;
            }
            return this.mIdmapDaemon.createIdmap(path, str, str2, calculateFulfilledPolicies, enforceOverlayable, i) != null ? 3 : 0;
        } catch (Exception e) {
            Slog.w("OverlayManager", "failed to generate idmap for " + path + " and " + str, e);
            return 0;
        }
    }

    public boolean removeIdmap(OverlayInfo overlayInfo, int i) {
        try {
            return this.mIdmapDaemon.removeIdmap(overlayInfo.baseCodePath, i);
        } catch (Exception e) {
            Slog.w("OverlayManager", "failed to remove idmap for " + overlayInfo.baseCodePath, e);
            return false;
        }
    }

    public boolean idmapExists(OverlayInfo overlayInfo) {
        return this.mIdmapDaemon.idmapExists(overlayInfo.baseCodePath, overlayInfo.userId);
    }

    public List<FabricatedOverlayInfo> getFabricatedOverlayInfos() {
        return this.mIdmapDaemon.getFabricatedOverlayInfos();
    }

    public FabricatedOverlayInfo createFabricatedOverlay(FabricatedOverlayInternal fabricatedOverlayInternal) {
        return this.mIdmapDaemon.createFabricatedOverlay(fabricatedOverlayInternal);
    }

    public boolean deleteFabricatedOverlay(String str) {
        return this.mIdmapDaemon.deleteFabricatedOverlay(str);
    }

    public String dumpIdmap(String str) {
        return this.mIdmapDaemon.dumpIdmap(str);
    }

    public final boolean enforceOverlayable(PackageState packageState, AndroidPackage androidPackage) {
        if (androidPackage.getTargetSdkVersion() >= 29) {
            return true;
        }
        if (packageState.isVendor()) {
            return VENDOR_IS_Q_OR_LATER;
        }
        return (packageState.isSystem() || androidPackage.isSignedWithPlatformKey()) ? false : true;
    }

    public final int calculateFulfilledPolicies(AndroidPackage androidPackage, PackageState packageState, AndroidPackage androidPackage2, int i) {
        int i2 = this.mPackageManager.signaturesMatching(androidPackage.getPackageName(), androidPackage2.getPackageName(), i) ? 17 : 1;
        if (matchesActorSignature(androidPackage, androidPackage2, i)) {
            i2 |= 128;
        }
        if (!TextUtils.isEmpty(this.mConfigSignaturePackage) && this.mPackageManager.signaturesMatching(this.mConfigSignaturePackage, androidPackage2.getPackageName(), i)) {
            i2 |= 256;
        }
        return packageState.isVendor() ? i2 | 4 : packageState.isProduct() ? i2 | 8 : packageState.isOdm() ? i2 | 32 : packageState.isOem() ? i2 | 64 : (packageState.isSystem() || packageState.isSystemExt()) ? i2 | 2 : i2;
    }

    public final boolean matchesActorSignature(AndroidPackage androidPackage, AndroidPackage androidPackage2, int i) {
        String str;
        String overlayTargetOverlayableName = androidPackage2.getOverlayTargetOverlayableName();
        if (overlayTargetOverlayableName != null) {
            try {
                OverlayableInfo overlayableForTarget = this.mPackageManager.getOverlayableForTarget(androidPackage.getPackageName(), overlayTargetOverlayableName, i);
                if (overlayableForTarget == null || (str = overlayableForTarget.actor) == null) {
                    return false;
                }
                return this.mPackageManager.signaturesMatching((String) OverlayActorEnforcer.getPackageNameForActor(str, this.mPackageManager.getNamedActors()).first, androidPackage2.getPackageName(), i);
            } catch (IOException unused) {
                return false;
            }
        }
        return false;
    }
}
