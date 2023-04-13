package com.android.server.p011pm.dex;

import android.p005os.IInstalld;
import android.util.Log;
import com.android.server.art.model.DexoptParams;
import com.android.server.p011pm.PackageManagerServiceCompilerMapping;
import dalvik.system.DexFile;
/* renamed from: com.android.server.pm.dex.DexoptOptions */
/* loaded from: classes2.dex */
public final class DexoptOptions {
    public final int mCompilationReason;
    public final String mCompilerFilter;
    public final int mFlags;
    public final String mPackageName;
    public final String mSplitName;

    public DexoptOptions(String str, int i, int i2) {
        this(str, i, PackageManagerServiceCompilerMapping.getCompilerFilterForReason(i), null, i2);
    }

    public DexoptOptions(String str, int i, String str2, String str3, int i2) {
        if ((i2 & (-3696)) != 0) {
            throw new IllegalArgumentException("Invalid flags : " + Integer.toHexString(i2));
        }
        this.mPackageName = str;
        this.mCompilerFilter = str2;
        this.mFlags = i2;
        this.mSplitName = str3;
        this.mCompilationReason = i;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public boolean isCheckForProfileUpdates() {
        return (this.mFlags & 1) != 0;
    }

    public String getCompilerFilter() {
        return this.mCompilerFilter;
    }

    public boolean isForce() {
        return (this.mFlags & 2) != 0;
    }

    public boolean isBootComplete() {
        return (this.mFlags & 4) != 0;
    }

    public boolean isDexoptOnlySecondaryDex() {
        return (this.mFlags & 8) != 0;
    }

    public boolean isDowngrade() {
        return (this.mFlags & 32) != 0;
    }

    public boolean isDexoptAsSharedLibrary() {
        return (this.mFlags & 64) != 0;
    }

    public boolean isDexoptIdleBackgroundJob() {
        return (this.mFlags & 512) != 0;
    }

    public boolean isDexoptInstallWithDexMetadata() {
        return (this.mFlags & 1024) != 0;
    }

    public boolean isDexoptInstallForRestore() {
        return (this.mFlags & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0;
    }

    public String getSplitName() {
        return this.mSplitName;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getCompilationReason() {
        return this.mCompilationReason;
    }

    public boolean isCompilationEnabled() {
        return !this.mCompilerFilter.equals("skip");
    }

    public static String convertToArtServiceDexoptReason(int i) {
        switch (i) {
            case 0:
                return "first-boot";
            case 1:
                return "boot-after-ota";
            case 2:
            case 10:
            case 14:
                throw new UnsupportedOperationException("ART Service unsupported compilation reason " + i);
            case 3:
                return "install";
            case 4:
                return "install-fast";
            case 5:
                return "install-bulk";
            case 6:
                return "install-bulk-secondary";
            case 7:
                return "install-bulk-downgraded";
            case 8:
                return "install-bulk-secondary-downgraded";
            case 9:
                return "bg-dexopt";
            case 11:
                return "inactive";
            case 12:
                return "cmdline";
            case 13:
                return "boot-after-mainline-update";
            default:
                throw new IllegalArgumentException("Invalid compilation reason " + i);
        }
    }

    public DexoptParams convertToDexoptParams(int i) {
        if (this.mSplitName != null) {
            throw new UnsupportedOperationException("Request to optimize only split " + this.mSplitName + " for " + this.mPackageName);
        } else if ((this.mFlags & 1) == 0 && DexFile.isProfileGuidedCompilerFilter(this.mCompilerFilter)) {
            throw new IllegalArgumentException("DEXOPT_CHECK_FOR_PROFILES_UPDATES must be set with profile guided filter");
        } else {
            int i2 = this.mFlags;
            if ((i2 & 2) != 0) {
                i |= 16;
            }
            int i3 = (i2 & 8) != 0 ? i | 2 : i | 1;
            if ((i2 & 32) != 0) {
                i3 |= 8;
            }
            if ((i2 & 1024) == 0) {
                Log.w("DexoptOptions", "DEXOPT_INSTALL_WITH_DEX_METADATA_FILE not set in request to optimise " + this.mPackageName + " - ART Service will unconditionally use a DM file if present.");
            }
            int i4 = this.mFlags;
            return new DexoptParams.Builder(convertToArtServiceDexoptReason(this.mCompilationReason), i3).setCompilerFilter(this.mCompilerFilter).setPriorityClass((i4 & 4) != 0 ? (i4 & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0 ? 80 : (i4 & 512) != 0 ? 40 : 60 : 100).build();
        }
    }
}
