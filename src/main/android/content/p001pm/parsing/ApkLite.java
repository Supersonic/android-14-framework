package android.content.p001pm.parsing;

import android.content.p001pm.PackageInfo;
import android.content.p001pm.SigningDetails;
import android.content.p001pm.VerifierInfo;
import com.android.internal.util.CollectionUtils;
import java.util.List;
import java.util.Set;
/* renamed from: android.content.pm.parsing.ApkLite */
/* loaded from: classes.dex */
public class ApkLite {
    private final String mConfigForSplit;
    private final boolean mCoreApp;
    private final boolean mDebuggable;
    private final boolean mExtractNativeLibs;
    private final boolean mFeatureSplit;
    private final boolean mHasDeviceAdminReceiver;
    private final int mInstallLocation;
    private final boolean mIsSdkLibrary;
    private final boolean mIsolatedSplits;
    private final int mMinSdkVersion;
    private final boolean mMultiArch;
    private final boolean mOverlayIsStatic;
    private final int mOverlayPriority;
    private final String mPackageName;
    private final String mPath;
    private final boolean mProfileableByShell;
    private final Set<String> mRequiredSplitTypes;
    private final String mRequiredSystemPropertyName;
    private final String mRequiredSystemPropertyValue;
    private final int mRevisionCode;
    private final int mRollbackDataPolicy;
    private final SigningDetails mSigningDetails;
    private final String mSplitName;
    private final boolean mSplitRequired;
    private final Set<String> mSplitTypes;
    private final String mTargetPackageName;
    private final int mTargetSdkVersion;
    private final boolean mUse32bitAbi;
    private final boolean mUseEmbeddedDex;
    private final String mUsesSplitName;
    private final VerifierInfo[] mVerifiers;
    private final int mVersionCode;
    private final int mVersionCodeMajor;

    public ApkLite(String path, String packageName, String splitName, boolean isFeatureSplit, String configForSplit, String usesSplitName, boolean isSplitRequired, int versionCode, int versionCodeMajor, int revisionCode, int installLocation, List<VerifierInfo> verifiers, SigningDetails signingDetails, boolean coreApp, boolean debuggable, boolean profileableByShell, boolean multiArch, boolean use32bitAbi, boolean useEmbeddedDex, boolean extractNativeLibs, boolean isolatedSplits, String targetPackageName, boolean overlayIsStatic, int overlayPriority, String requiredSystemPropertyName, String requiredSystemPropertyValue, int minSdkVersion, int targetSdkVersion, int rollbackDataPolicy, Set<String> requiredSplitTypes, Set<String> splitTypes, boolean hasDeviceAdminReceiver, boolean isSdkLibrary) {
        this.mPath = path;
        this.mPackageName = packageName;
        this.mSplitName = splitName;
        this.mSplitTypes = splitTypes;
        this.mFeatureSplit = isFeatureSplit;
        this.mConfigForSplit = configForSplit;
        this.mUsesSplitName = usesSplitName;
        this.mRequiredSplitTypes = requiredSplitTypes;
        this.mSplitRequired = isSplitRequired || hasAnyRequiredSplitTypes();
        this.mVersionCode = versionCode;
        this.mVersionCodeMajor = versionCodeMajor;
        this.mRevisionCode = revisionCode;
        this.mInstallLocation = installLocation;
        this.mVerifiers = (VerifierInfo[]) verifiers.toArray(new VerifierInfo[verifiers.size()]);
        this.mSigningDetails = signingDetails;
        this.mCoreApp = coreApp;
        this.mDebuggable = debuggable;
        this.mProfileableByShell = profileableByShell;
        this.mMultiArch = multiArch;
        this.mUse32bitAbi = use32bitAbi;
        this.mUseEmbeddedDex = useEmbeddedDex;
        this.mExtractNativeLibs = extractNativeLibs;
        this.mIsolatedSplits = isolatedSplits;
        this.mTargetPackageName = targetPackageName;
        this.mOverlayIsStatic = overlayIsStatic;
        this.mOverlayPriority = overlayPriority;
        this.mRequiredSystemPropertyName = requiredSystemPropertyName;
        this.mRequiredSystemPropertyValue = requiredSystemPropertyValue;
        this.mMinSdkVersion = minSdkVersion;
        this.mTargetSdkVersion = targetSdkVersion;
        this.mRollbackDataPolicy = rollbackDataPolicy;
        this.mHasDeviceAdminReceiver = hasDeviceAdminReceiver;
        this.mIsSdkLibrary = isSdkLibrary;
    }

    public long getLongVersionCode() {
        return PackageInfo.composeLongVersionCode(this.mVersionCodeMajor, this.mVersionCode);
    }

    private boolean hasAnyRequiredSplitTypes() {
        return !CollectionUtils.isEmpty(this.mRequiredSplitTypes);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getPath() {
        return this.mPath;
    }

    public String getSplitName() {
        return this.mSplitName;
    }

    public String getUsesSplitName() {
        return this.mUsesSplitName;
    }

    public String getConfigForSplit() {
        return this.mConfigForSplit;
    }

    public Set<String> getRequiredSplitTypes() {
        return this.mRequiredSplitTypes;
    }

    public Set<String> getSplitTypes() {
        return this.mSplitTypes;
    }

    public int getVersionCodeMajor() {
        return this.mVersionCodeMajor;
    }

    public int getVersionCode() {
        return this.mVersionCode;
    }

    public int getRevisionCode() {
        return this.mRevisionCode;
    }

    public int getInstallLocation() {
        return this.mInstallLocation;
    }

    public int getMinSdkVersion() {
        return this.mMinSdkVersion;
    }

    public int getTargetSdkVersion() {
        return this.mTargetSdkVersion;
    }

    public VerifierInfo[] getVerifiers() {
        return this.mVerifiers;
    }

    public SigningDetails getSigningDetails() {
        return this.mSigningDetails;
    }

    public boolean isFeatureSplit() {
        return this.mFeatureSplit;
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

    public boolean isProfileableByShell() {
        return this.mProfileableByShell;
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

    public boolean isUseEmbeddedDex() {
        return this.mUseEmbeddedDex;
    }

    public String getTargetPackageName() {
        return this.mTargetPackageName;
    }

    public boolean isOverlayIsStatic() {
        return this.mOverlayIsStatic;
    }

    public int getOverlayPriority() {
        return this.mOverlayPriority;
    }

    public String getRequiredSystemPropertyName() {
        return this.mRequiredSystemPropertyName;
    }

    public String getRequiredSystemPropertyValue() {
        return this.mRequiredSystemPropertyValue;
    }

    public int getRollbackDataPolicy() {
        return this.mRollbackDataPolicy;
    }

    public boolean isHasDeviceAdminReceiver() {
        return this.mHasDeviceAdminReceiver;
    }

    public boolean isIsSdkLibrary() {
        return this.mIsSdkLibrary;
    }

    @Deprecated
    private void __metadata() {
    }
}
