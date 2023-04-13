package android.content.p001pm.parsing;

import android.content.p001pm.PackageInfo;
import android.content.p001pm.VerifierInfo;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
/* renamed from: android.content.pm.parsing.PackageLite */
/* loaded from: classes.dex */
public class PackageLite {
    private final String mBaseApkPath;
    private final Set<String> mBaseRequiredSplitTypes;
    private final int mBaseRevisionCode;
    private final String[] mConfigForSplit;
    private final boolean mCoreApp;
    private final boolean mDebuggable;
    private final boolean mExtractNativeLibs;
    private final int mInstallLocation;
    private final boolean[] mIsFeatureSplits;
    private final boolean mIsSdkLibrary;
    private final boolean mIsolatedSplits;
    private final boolean mMultiArch;
    private final String mPackageName;
    private final String mPath;
    private final boolean mProfileableByShell;
    private final Set<String>[] mRequiredSplitTypes;
    private final String[] mSplitApkPaths;
    private final String[] mSplitNames;
    private final boolean mSplitRequired;
    private final int[] mSplitRevisionCodes;
    private final Set<String>[] mSplitTypes;
    private final int mTargetSdk;
    private final boolean mUse32bitAbi;
    private final boolean mUseEmbeddedDex;
    private final String[] mUsesSplitNames;
    private final VerifierInfo[] mVerifiers;
    private final int mVersionCode;
    private final int mVersionCodeMajor;

    public PackageLite(String path, String baseApkPath, ApkLite baseApk, String[] splitNames, boolean[] isFeatureSplits, String[] usesSplitNames, String[] configForSplit, String[] splitApkPaths, int[] splitRevisionCodes, int targetSdk, Set<String>[] requiredSplitTypes, Set<String>[] splitTypes) {
        this.mPath = path;
        this.mBaseApkPath = baseApkPath;
        this.mPackageName = baseApk.getPackageName();
        this.mVersionCode = baseApk.getVersionCode();
        this.mVersionCodeMajor = baseApk.getVersionCodeMajor();
        this.mInstallLocation = baseApk.getInstallLocation();
        this.mVerifiers = baseApk.getVerifiers();
        this.mBaseRevisionCode = baseApk.getRevisionCode();
        this.mCoreApp = baseApk.isCoreApp();
        this.mDebuggable = baseApk.isDebuggable();
        this.mMultiArch = baseApk.isMultiArch();
        this.mUse32bitAbi = baseApk.isUse32bitAbi();
        this.mExtractNativeLibs = baseApk.isExtractNativeLibs();
        this.mIsolatedSplits = baseApk.isIsolatedSplits();
        this.mUseEmbeddedDex = baseApk.isUseEmbeddedDex();
        this.mBaseRequiredSplitTypes = baseApk.getRequiredSplitTypes();
        this.mRequiredSplitTypes = requiredSplitTypes;
        this.mSplitRequired = baseApk.isSplitRequired() || hasAnyRequiredSplitTypes();
        this.mProfileableByShell = baseApk.isProfileableByShell();
        this.mIsSdkLibrary = baseApk.isIsSdkLibrary();
        this.mSplitNames = splitNames;
        this.mSplitTypes = splitTypes;
        this.mIsFeatureSplits = isFeatureSplits;
        this.mUsesSplitNames = usesSplitNames;
        this.mConfigForSplit = configForSplit;
        this.mSplitApkPaths = splitApkPaths;
        this.mSplitRevisionCodes = splitRevisionCodes;
        this.mTargetSdk = targetSdk;
    }

    public List<String> getAllApkPaths() {
        ArrayList<String> paths = new ArrayList<>();
        paths.add(this.mBaseApkPath);
        if (!ArrayUtils.isEmpty(this.mSplitApkPaths)) {
            Collections.addAll(paths, this.mSplitApkPaths);
        }
        return paths;
    }

    public long getLongVersionCode() {
        return PackageInfo.composeLongVersionCode(this.mVersionCodeMajor, this.mVersionCode);
    }

    private boolean hasAnyRequiredSplitTypes() {
        return (CollectionUtils.isEmpty(this.mBaseRequiredSplitTypes) && ArrayUtils.find(this.mRequiredSplitTypes, new Predicate() { // from class: android.content.pm.parsing.PackageLite$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return PackageLite.lambda$hasAnyRequiredSplitTypes$0((Set) obj);
            }
        }) == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$hasAnyRequiredSplitTypes$0(Set r) {
        return !CollectionUtils.isEmpty(r);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getPath() {
        return this.mPath;
    }

    public String getBaseApkPath() {
        return this.mBaseApkPath;
    }

    public String[] getSplitApkPaths() {
        return this.mSplitApkPaths;
    }

    public String[] getSplitNames() {
        return this.mSplitNames;
    }

    public String[] getUsesSplitNames() {
        return this.mUsesSplitNames;
    }

    public String[] getConfigForSplit() {
        return this.mConfigForSplit;
    }

    public Set<String> getBaseRequiredSplitTypes() {
        return this.mBaseRequiredSplitTypes;
    }

    public Set<String>[] getRequiredSplitTypes() {
        return this.mRequiredSplitTypes;
    }

    public Set<String>[] getSplitTypes() {
        return this.mSplitTypes;
    }

    public int getVersionCodeMajor() {
        return this.mVersionCodeMajor;
    }

    public int getVersionCode() {
        return this.mVersionCode;
    }

    public int getTargetSdk() {
        return this.mTargetSdk;
    }

    public int getBaseRevisionCode() {
        return this.mBaseRevisionCode;
    }

    public int[] getSplitRevisionCodes() {
        return this.mSplitRevisionCodes;
    }

    public int getInstallLocation() {
        return this.mInstallLocation;
    }

    public VerifierInfo[] getVerifiers() {
        return this.mVerifiers;
    }

    public boolean[] getIsFeatureSplits() {
        return this.mIsFeatureSplits;
    }

    public boolean isIsolatedSplits() {
        return this.mIsolatedSplits;
    }

    public boolean isSplitRequired() {
        return this.mSplitRequired;
    }

    public boolean isCoreApp() {
        return this.mCoreApp;
    }

    public boolean isDebuggable() {
        return this.mDebuggable;
    }

    public boolean isMultiArch() {
        return this.mMultiArch;
    }

    public boolean isUse32bitAbi() {
        return this.mUse32bitAbi;
    }

    public boolean isExtractNativeLibs() {
        return this.mExtractNativeLibs;
    }

    public boolean isProfileableByShell() {
        return this.mProfileableByShell;
    }

    public boolean isUseEmbeddedDex() {
        return this.mUseEmbeddedDex;
    }

    public boolean isIsSdkLibrary() {
        return this.mIsSdkLibrary;
    }

    @Deprecated
    private void __metadata() {
    }
}
