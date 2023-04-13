package com.android.server.p011pm.parsing.pkg;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureGroupInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.SigningDetails;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.Parcelling;
import com.android.server.p011pm.parsing.PackageInfoUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.AndroidPackageSplit;
import com.android.server.p011pm.pkg.AndroidPackageSplitImpl;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.component.ComponentMutateUtils;
import com.android.server.p011pm.pkg.component.ParsedActivity;
import com.android.server.p011pm.pkg.component.ParsedActivityImpl;
import com.android.server.p011pm.pkg.component.ParsedApexSystemService;
import com.android.server.p011pm.pkg.component.ParsedApexSystemServiceImpl;
import com.android.server.p011pm.pkg.component.ParsedAttribution;
import com.android.server.p011pm.pkg.component.ParsedAttributionImpl;
import com.android.server.p011pm.pkg.component.ParsedComponent;
import com.android.server.p011pm.pkg.component.ParsedInstrumentation;
import com.android.server.p011pm.pkg.component.ParsedInstrumentationImpl;
import com.android.server.p011pm.pkg.component.ParsedIntentInfo;
import com.android.server.p011pm.pkg.component.ParsedMainComponent;
import com.android.server.p011pm.pkg.component.ParsedPermission;
import com.android.server.p011pm.pkg.component.ParsedPermissionGroup;
import com.android.server.p011pm.pkg.component.ParsedPermissionGroupImpl;
import com.android.server.p011pm.pkg.component.ParsedPermissionImpl;
import com.android.server.p011pm.pkg.component.ParsedProcess;
import com.android.server.p011pm.pkg.component.ParsedProvider;
import com.android.server.p011pm.pkg.component.ParsedProviderImpl;
import com.android.server.p011pm.pkg.component.ParsedService;
import com.android.server.p011pm.pkg.component.ParsedServiceImpl;
import com.android.server.p011pm.pkg.component.ParsedUsesPermission;
import com.android.server.p011pm.pkg.component.ParsedUsesPermissionImpl;
import com.android.server.p011pm.pkg.parsing.ParsingPackage;
import com.android.server.p011pm.pkg.parsing.ParsingPackageHidden;
import com.android.server.p011pm.pkg.parsing.ParsingPackageUtils;
import com.android.server.p011pm.pkg.parsing.ParsingUtils;
import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import libcore.util.EmptyArray;
/* renamed from: com.android.server.pm.parsing.pkg.PackageImpl */
/* loaded from: classes2.dex */
public class PackageImpl implements ParsedPackage, AndroidPackageInternal, AndroidPackageHidden, ParsingPackage, ParsingPackageHidden, Parcelable {
    public List<ParsedActivity> activities;
    public List<String> adoptPermissions;
    public Boolean anyDensity;
    public List<ParsedApexSystemService> apexSystemServices;
    public String appComponentFactory;
    public List<ParsedAttribution> attributions;
    public int autoRevokePermissions;
    public String backupAgentName;
    public int banner;
    public int baseRevisionCode;
    public int category;
    public String classLoaderName;
    public String className;
    public int compatibleWidthLimitDp;
    public int compileSdkVersion;
    public String compileSdkVersionCodeName;
    public List<ConfigurationInfo> configPreferences;
    public int dataExtractionRules;
    public int descriptionRes;
    public List<FeatureGroupInfo> featureGroups;
    public int fullBackupContent;
    public int gwpAsanMode;
    public int iconRes;
    public List<String> implicitPermissions;
    public int installLocation;
    public List<ParsedInstrumentation> instrumentations;
    public Map<String, ArraySet<PublicKey>> keySetMapping;
    public int labelRes;
    public int largestWidthLimitDp;
    public List<String> libraryNames;
    public int logo;
    public String mBaseApkPath;
    public String mBaseAppDataCredentialProtectedDirForSystemUser;
    public String mBaseAppDataDeviceProtectedDirForSystemUser;
    public int mBaseAppInfoFlags;
    public int mBaseAppInfoPrivateFlags;
    public int mBaseAppInfoPrivateFlagsExt;
    public long mBooleans;
    public long mBooleans2;
    public Set<String> mKnownActivityEmbeddingCerts;
    public int mLocaleConfigRes;
    public long mLongVersionCode;
    public String mPath;
    public Map<String, PackageManager.Property> mProperties;
    public List<AndroidPackageSplit> mSplits;
    public UUID mStorageUuid;
    public String[] mUsesLibrariesSorted;
    public String[] mUsesOptionalLibrariesSorted;
    public String[] mUsesSdkLibrariesSorted;
    public String[] mUsesStaticLibrariesSorted;
    public String manageSpaceActivityName;
    public final String manifestPackageName;
    public float maxAspectRatio;
    public int maxSdkVersion;
    public int memtagMode;
    public Bundle metaData;
    public Set<String> mimeGroups;
    public float minAspectRatio;
    public SparseIntArray minExtensionVersions;
    public int minSdkVersion;
    public int nativeHeapZeroInitialized;
    public String nativeLibraryDir;
    public String nativeLibraryRootDir;
    public boolean nativeLibraryRootRequiresIsa;
    public int networkSecurityConfigRes;
    public CharSequence nonLocalizedLabel;
    public List<String> originalPackages;
    public String overlayCategory;
    public int overlayPriority;
    public String overlayTarget;
    public String overlayTargetOverlayableName;
    public Map<String, String> overlayables;
    public String packageName;
    public String permission;
    public List<ParsedPermissionGroup> permissionGroups;
    public List<ParsedPermission> permissions;
    public List<Pair<String, ParsedIntentInfo>> preferredActivityFilters;
    public String primaryCpuAbi;
    public String processName;
    public Map<String, ParsedProcess> processes;
    public List<String> protectedBroadcasts;
    public List<ParsedProvider> providers;
    public List<Intent> queriesIntents;
    public List<String> queriesPackages;
    public Set<String> queriesProviders;
    public List<ParsedActivity> receivers;
    public List<FeatureInfo> reqFeatures;
    public Boolean requestRawExternalStorageAccess;
    @Deprecated
    public List<String> requestedPermissions;
    public String requiredAccountType;
    public int requiresSmallestWidthDp;
    public Boolean resizeable;
    public Boolean resizeableActivity;
    public byte[] restrictUpdateHash;
    public String restrictedAccountType;
    public int roundIconRes;
    public int sdkLibVersionMajor;
    public String sdkLibraryName;
    public String secondaryCpuAbi;
    public String secondaryNativeLibraryDir;
    public List<ParsedService> services;
    public String sharedUserId;
    public int sharedUserLabel;
    public SigningDetails signingDetails;
    public String[] splitClassLoaderNames;
    public String[] splitCodePaths;
    public SparseArray<int[]> splitDependencies;
    public int[] splitFlags;
    public String[] splitNames;
    public int[] splitRevisionCodes;
    public long staticSharedLibVersion;
    public String staticSharedLibraryName;
    public Boolean supportsExtraLargeScreens;
    public Boolean supportsLargeScreens;
    public Boolean supportsNormalScreens;
    public Boolean supportsSmallScreens;
    public int targetSandboxVersion;
    public int targetSdkVersion;
    public String taskAffinity;
    public int theme;
    public int uiOptions;
    public int uid;
    public Set<String> upgradeKeySets;
    public List<String> usesLibraries;
    public List<String> usesNativeLibraries;
    public List<String> usesOptionalLibraries;
    public List<String> usesOptionalNativeLibraries;
    public List<ParsedUsesPermission> usesPermissions;
    public List<String> usesSdkLibraries;
    public String[][] usesSdkLibrariesCertDigests;
    public long[] usesSdkLibrariesVersionsMajor;
    public List<String> usesStaticLibraries;
    public String[][] usesStaticLibrariesCertDigests;
    public long[] usesStaticLibrariesVersions;
    public int versionCode;
    public int versionCodeMajor;
    public String versionName;
    public String volumeUuid;
    public String zygotePreloadName;
    public static final SparseArray<int[]> EMPTY_INT_ARRAY_SPARSE_ARRAY = new SparseArray<>();
    public static final Comparator<ParsedMainComponent> ORDER_COMPARATOR = new Comparator() { // from class: com.android.server.pm.parsing.pkg.PackageImpl$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$static$0;
            lambda$static$0 = PackageImpl.lambda$static$0((ParsedMainComponent) obj, (ParsedMainComponent) obj2);
            return lambda$static$0;
        }
    };
    public static Parcelling.BuiltIn.ForBoolean sForBoolean = Parcelling.Cache.getOrCreate(Parcelling.BuiltIn.ForBoolean.class);
    public static Parcelling.BuiltIn.ForInternedString sForInternedString = Parcelling.Cache.getOrCreate(Parcelling.BuiltIn.ForInternedString.class);
    public static Parcelling.BuiltIn.ForInternedStringArray sForInternedStringArray = Parcelling.Cache.getOrCreate(Parcelling.BuiltIn.ForInternedStringArray.class);
    public static Parcelling.BuiltIn.ForInternedStringList sForInternedStringList = Parcelling.Cache.getOrCreate(Parcelling.BuiltIn.ForInternedStringList.class);
    public static Parcelling.BuiltIn.ForInternedStringValueMap sForInternedStringValueMap = Parcelling.Cache.getOrCreate(Parcelling.BuiltIn.ForInternedStringValueMap.class);
    public static Parcelling.BuiltIn.ForStringSet sForStringSet = Parcelling.Cache.getOrCreate(Parcelling.BuiltIn.ForStringSet.class);
    public static Parcelling.BuiltIn.ForInternedStringSet sForInternedStringSet = Parcelling.Cache.getOrCreate(Parcelling.BuiltIn.ForInternedStringSet.class);
    public static ParsingUtils.StringPairListParceler sForIntentInfoPairs = new ParsingUtils.StringPairListParceler();
    public static final Parcelable.Creator<PackageImpl> CREATOR = new Parcelable.Creator<PackageImpl>() { // from class: com.android.server.pm.parsing.pkg.PackageImpl.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageImpl createFromParcel(Parcel parcel) {
            return new PackageImpl(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageImpl[] newArray(int i) {
            return new PackageImpl[i];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public /* bridge */ /* synthetic */ ParsingPackage asSplit(String[] strArr, String[] strArr2, int[] iArr, SparseArray sparseArray) {
        return asSplit(strArr, strArr2, iArr, (SparseArray<int[]>) sparseArray);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public /* bridge */ /* synthetic */ ParsingPackage setProcesses(Map map) {
        return setProcesses((Map<String, ParsedProcess>) map);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public /* bridge */ /* synthetic */ ParsingPackage setUpgradeKeySets(Set set) {
        return setUpgradeKeySets((Set<String>) set);
    }

    public static /* synthetic */ int lambda$static$0(ParsedMainComponent parsedMainComponent, ParsedMainComponent parsedMainComponent2) {
        return Integer.compare(parsedMainComponent2.getOrder(), parsedMainComponent.getOrder());
    }

    public static PackageImpl forParsing(String str, String str2, String str3, TypedArray typedArray, boolean z) {
        return new PackageImpl(str, str2, str3, typedArray, z);
    }

    public static AndroidPackage buildFakeForDeletion(String str, String str2) {
        return forTesting(str).setVolumeUuid(str2).hideAsParsed().hideAsFinal();
    }

    @VisibleForTesting
    public static ParsingPackage forTesting(String str) {
        return forTesting(str, "");
    }

    @VisibleForTesting
    public static ParsingPackage forTesting(String str, String str2) {
        return new PackageImpl(str, str2, str2, null, false);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addActivity(ParsedActivity parsedActivity) {
        this.activities = CollectionUtils.add(this.activities, parsedActivity);
        addMimeGroupsFromComponent(parsedActivity);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addAdoptPermission(String str) {
        this.adoptPermissions = CollectionUtils.add(this.adoptPermissions, TextUtils.safeIntern(str));
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public final PackageImpl addApexSystemService(ParsedApexSystemService parsedApexSystemService) {
        this.apexSystemServices = CollectionUtils.add(this.apexSystemServices, parsedApexSystemService);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addAttribution(ParsedAttribution parsedAttribution) {
        this.attributions = CollectionUtils.add(this.attributions, parsedAttribution);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addConfigPreference(ConfigurationInfo configurationInfo) {
        this.configPreferences = CollectionUtils.add(this.configPreferences, configurationInfo);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addFeatureGroup(FeatureGroupInfo featureGroupInfo) {
        this.featureGroups = CollectionUtils.add(this.featureGroups, featureGroupInfo);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addImplicitPermission(String str) {
        addUsesPermission((ParsedUsesPermission) new ParsedUsesPermissionImpl(str, 0));
        this.implicitPermissions = CollectionUtils.add(this.implicitPermissions, TextUtils.safeIntern(str));
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addInstrumentation(ParsedInstrumentation parsedInstrumentation) {
        this.instrumentations = CollectionUtils.add(this.instrumentations, parsedInstrumentation);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addKeySet(String str, PublicKey publicKey) {
        ArraySet<PublicKey> arraySet = this.keySetMapping.get(str);
        if (arraySet == null) {
            arraySet = new ArraySet<>();
        }
        arraySet.add(publicKey);
        this.keySetMapping = CollectionUtils.add(this.keySetMapping, str, arraySet);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addLibraryName(String str) {
        this.libraryNames = CollectionUtils.add(this.libraryNames, TextUtils.safeIntern(str));
        return this;
    }

    public final void addMimeGroupsFromComponent(ParsedComponent parsedComponent) {
        for (int size = parsedComponent.getIntents().size() - 1; size >= 0; size--) {
            IntentFilter intentFilter = parsedComponent.getIntents().get(size).getIntentFilter();
            for (int countMimeGroups = intentFilter.countMimeGroups() - 1; countMimeGroups >= 0; countMimeGroups--) {
                Set<String> set = this.mimeGroups;
                if (set != null && set.size() > 500) {
                    throw new IllegalStateException("Max limit on number of MIME Groups reached");
                }
                this.mimeGroups = CollectionUtils.add(this.mimeGroups, intentFilter.getMimeGroup(countMimeGroups));
            }
        }
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addOriginalPackage(String str) {
        this.originalPackages = CollectionUtils.add(this.originalPackages, str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public ParsingPackage addOverlayable(String str, String str2) {
        this.overlayables = CollectionUtils.add(this.overlayables, str, TextUtils.safeIntern(str2));
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addPermission(ParsedPermission parsedPermission) {
        this.permissions = CollectionUtils.add(this.permissions, parsedPermission);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addPermissionGroup(ParsedPermissionGroup parsedPermissionGroup) {
        this.permissionGroups = CollectionUtils.add(this.permissionGroups, parsedPermissionGroup);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addPreferredActivityFilter(String str, ParsedIntentInfo parsedIntentInfo) {
        this.preferredActivityFilters = CollectionUtils.add(this.preferredActivityFilters, Pair.create(str, parsedIntentInfo));
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addProperty(PackageManager.Property property) {
        if (property == null) {
            return this;
        }
        this.mProperties = CollectionUtils.add(this.mProperties, property.getName(), property);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addProtectedBroadcast(String str) {
        if (!this.protectedBroadcasts.contains(str)) {
            this.protectedBroadcasts = CollectionUtils.add(this.protectedBroadcasts, TextUtils.safeIntern(str));
        }
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addProvider(ParsedProvider parsedProvider) {
        this.providers = CollectionUtils.add(this.providers, parsedProvider);
        addMimeGroupsFromComponent(parsedProvider);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addQueriesIntent(Intent intent) {
        this.queriesIntents = CollectionUtils.add(this.queriesIntents, intent);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addQueriesPackage(String str) {
        this.queriesPackages = CollectionUtils.add(this.queriesPackages, TextUtils.safeIntern(str));
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addQueriesProvider(String str) {
        this.queriesProviders = CollectionUtils.add(this.queriesProviders, str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addReceiver(ParsedActivity parsedActivity) {
        this.receivers = CollectionUtils.add(this.receivers, parsedActivity);
        addMimeGroupsFromComponent(parsedActivity);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addReqFeature(FeatureInfo featureInfo) {
        this.reqFeatures = CollectionUtils.add(this.reqFeatures, featureInfo);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addService(ParsedService parsedService) {
        this.services = CollectionUtils.add(this.services, parsedService);
        addMimeGroupsFromComponent(parsedService);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addUsesLibrary(String str) {
        this.usesLibraries = CollectionUtils.add(this.usesLibraries, TextUtils.safeIntern(str));
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public final PackageImpl addUsesNativeLibrary(String str) {
        this.usesNativeLibraries = CollectionUtils.add(this.usesNativeLibraries, TextUtils.safeIntern(str));
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addUsesOptionalLibrary(String str) {
        this.usesOptionalLibraries = CollectionUtils.add(this.usesOptionalLibraries, TextUtils.safeIntern(str));
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public final PackageImpl addUsesOptionalNativeLibrary(String str) {
        this.usesOptionalNativeLibraries = CollectionUtils.add(this.usesOptionalNativeLibraries, TextUtils.safeIntern(str));
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addUsesPermission(ParsedUsesPermission parsedUsesPermission) {
        this.usesPermissions = CollectionUtils.add(this.usesPermissions, parsedUsesPermission);
        this.requestedPermissions = CollectionUtils.add(this.requestedPermissions, parsedUsesPermission.getName());
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addUsesSdkLibrary(String str, long j, String[] strArr) {
        this.usesSdkLibraries = CollectionUtils.add(this.usesSdkLibraries, TextUtils.safeIntern(str));
        this.usesSdkLibrariesVersionsMajor = ArrayUtils.appendLong(this.usesSdkLibrariesVersionsMajor, j, true);
        this.usesSdkLibrariesCertDigests = (String[][]) ArrayUtils.appendElement(String[].class, this.usesSdkLibrariesCertDigests, strArr, true);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl addUsesStaticLibrary(String str, long j, String[] strArr) {
        this.usesStaticLibraries = CollectionUtils.add(this.usesStaticLibraries, TextUtils.safeIntern(str));
        this.usesStaticLibrariesVersions = ArrayUtils.appendLong(this.usesStaticLibrariesVersions, j, true);
        this.usesStaticLibrariesCertDigests = (String[][]) ArrayUtils.appendElement(String[].class, this.usesStaticLibrariesCertDigests, strArr, true);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isAttributionsUserVisible() {
        return getBoolean(140737488355328L);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl asSplit(String[] strArr, String[] strArr2, int[] iArr, SparseArray<int[]> sparseArray) {
        this.splitNames = strArr;
        this.splitCodePaths = strArr2;
        this.splitRevisionCodes = iArr;
        this.splitDependencies = sparseArray;
        int length = strArr.length;
        this.splitFlags = new int[length];
        this.splitClassLoaderNames = new String[length];
        return this;
    }

    public void assignDerivedFields() {
        this.mStorageUuid = StorageManager.convert(this.volumeUuid);
        this.mLongVersionCode = PackageInfo.composeLongVersionCode(this.versionCodeMajor, this.versionCode);
    }

    public final ArrayMap<String, String> buildAppClassNamesByProcess() {
        if (ArrayUtils.size(this.processes) == 0) {
            return null;
        }
        ArrayMap<String, String> arrayMap = new ArrayMap<>(4);
        for (String str : this.processes.keySet()) {
            ArrayMap<String, String> appClassNamesByPackage = this.processes.get(str).getAppClassNamesByPackage();
            for (int i = 0; i < appClassNamesByPackage.size(); i++) {
                if (this.packageName.equals(appClassNamesByPackage.keyAt(i))) {
                    String valueAt = appClassNamesByPackage.valueAt(i);
                    if (!TextUtils.isEmpty(valueAt)) {
                        arrayMap.put(str, valueAt);
                    }
                }
            }
        }
        return arrayMap;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<AndroidPackageSplit> getSplits() {
        if (this.mSplits == null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new AndroidPackageSplitImpl(null, getBaseApkPath(), getBaseRevisionCode(), isDeclaredHavingCode() ? 4 : 0, getClassLoaderName()));
            if (this.splitNames != null) {
                int i = 0;
                while (true) {
                    String[] strArr = this.splitNames;
                    if (i >= strArr.length) {
                        break;
                    }
                    arrayList.add(new AndroidPackageSplitImpl(strArr[i], this.splitCodePaths[i], this.splitRevisionCodes[i], this.splitFlags[i], this.splitClassLoaderNames[i]));
                    i++;
                }
            }
            if (this.splitDependencies != null) {
                for (int i2 = 0; i2 < this.splitDependencies.size(); i2++) {
                    int keyAt = this.splitDependencies.keyAt(i2);
                    int[] valueAt = this.splitDependencies.valueAt(i2);
                    ArrayList arrayList2 = new ArrayList();
                    for (int i3 : valueAt) {
                        if (i3 >= 0) {
                            arrayList2.add((AndroidPackageSplit) arrayList.get(i3));
                        }
                    }
                    ((AndroidPackageSplitImpl) arrayList.get(keyAt)).fillDependencies(Collections.unmodifiableList(arrayList2));
                }
            }
            this.mSplits = Collections.unmodifiableList(arrayList);
        }
        return this.mSplits;
    }

    public String toString() {
        return "Package{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<ParsedActivity> getActivities() {
        return this.activities;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<String> getAdoptPermissions() {
        return this.adoptPermissions;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<ParsedApexSystemService> getApexSystemServices() {
        return this.apexSystemServices;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getAppComponentFactory() {
        return this.appComponentFactory;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<ParsedAttribution> getAttributions() {
        return this.attributions;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getAutoRevokePermissions() {
        return this.autoRevokePermissions;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getBackupAgentName() {
        return this.backupAgentName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getBannerResourceId() {
        return this.banner;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String getBaseApkPath() {
        return this.mBaseApkPath;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getBaseRevisionCode() {
        return this.baseRevisionCode;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getCategory() {
        return this.category;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String getClassLoaderName() {
        return this.classLoaderName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getApplicationClassName() {
        return this.className;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getCompatibleWidthLimitDp() {
        return this.compatibleWidthLimitDp;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getCompileSdkVersion() {
        return this.compileSdkVersion;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getCompileSdkVersionCodeName() {
        return this.compileSdkVersionCodeName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<ConfigurationInfo> getConfigPreferences() {
        return this.configPreferences;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getDataExtractionRulesResourceId() {
        return this.dataExtractionRules;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getDescriptionResourceId() {
        return this.descriptionRes;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<FeatureGroupInfo> getFeatureGroups() {
        return this.featureGroups;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getFullBackupContentResourceId() {
        return this.fullBackupContent;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getGwpAsanMode() {
        return this.gwpAsanMode;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getIconResourceId() {
        return this.iconRes;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<String> getImplicitPermissions() {
        return this.implicitPermissions;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getInstallLocation() {
        return this.installLocation;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<ParsedInstrumentation> getInstrumentations() {
        return this.instrumentations;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public Map<String, ArraySet<PublicKey>> getKeySetMapping() {
        return this.keySetMapping;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public Set<String> getKnownActivityEmbeddingCerts() {
        return this.mKnownActivityEmbeddingCerts;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getLabelResourceId() {
        return this.labelRes;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getLargestWidthLimitDp() {
        return this.largestWidthLimitDp;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<String> getLibraryNames() {
        return this.libraryNames;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getLocaleConfigResourceId() {
        return this.mLocaleConfigRes;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getLogoResourceId() {
        return this.logo;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getManageSpaceActivityName() {
        return this.manageSpaceActivityName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public float getMaxAspectRatio() {
        return this.maxAspectRatio;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getMaxSdkVersion() {
        return this.maxSdkVersion;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getMemtagMode() {
        return this.memtagMode;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public Bundle getMetaData() {
        return this.metaData;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public Set<String> getMimeGroups() {
        return this.mimeGroups;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public float getMinAspectRatio() {
        return this.minAspectRatio;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public SparseIntArray getMinExtensionVersions() {
        return this.minExtensionVersions;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getMinSdkVersion() {
        return this.minSdkVersion;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getNativeHeapZeroInitialized() {
        return this.nativeHeapZeroInitialized;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getNetworkSecurityConfigResourceId() {
        return this.networkSecurityConfigRes;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public CharSequence getNonLocalizedLabel() {
        return this.nonLocalizedLabel;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<String> getOriginalPackages() {
        return this.originalPackages;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getOverlayCategory() {
        return this.overlayCategory;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getOverlayPriority() {
        return this.overlayPriority;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getOverlayTarget() {
        return this.overlayTarget;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getOverlayTargetOverlayableName() {
        return this.overlayTargetOverlayableName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public Map<String, String> getOverlayables() {
        return this.overlayables;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String getPackageName() {
        return this.packageName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getPath() {
        return this.mPath;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String getPermission() {
        return this.permission;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<ParsedPermissionGroup> getPermissionGroups() {
        return this.permissionGroups;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<ParsedPermission> getPermissions() {
        return this.permissions;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<Pair<String, ParsedIntentInfo>> getPreferredActivityFilters() {
        return this.preferredActivityFilters;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String getProcessName() {
        String str = this.processName;
        return str != null ? str : this.packageName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public Map<String, ParsedProcess> getProcesses() {
        return this.processes;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public Map<String, PackageManager.Property> getProperties() {
        return this.mProperties;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<String> getProtectedBroadcasts() {
        return this.protectedBroadcasts;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<ParsedProvider> getProviders() {
        return this.providers;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<Intent> getQueriesIntents() {
        return this.queriesIntents;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<String> getQueriesPackages() {
        return this.queriesPackages;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public Set<String> getQueriesProviders() {
        return this.queriesProviders;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<ParsedActivity> getReceivers() {
        return this.receivers;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<FeatureInfo> getRequestedFeatures() {
        return this.reqFeatures;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    @Deprecated
    public List<String> getRequestedPermissions() {
        return this.requestedPermissions;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getRequiredAccountType() {
        return this.requiredAccountType;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getRequiresSmallestWidthDp() {
        return this.requiresSmallestWidthDp;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public Boolean getResizeableActivity() {
        return this.resizeableActivity;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public byte[] getRestrictUpdateHash() {
        return this.restrictUpdateHash;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getRestrictedAccountType() {
        return this.restrictedAccountType;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getRoundIconResourceId() {
        return this.roundIconRes;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String getSdkLibraryName() {
        return this.sdkLibraryName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getSdkLibVersionMajor() {
        return this.sdkLibVersionMajor;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<ParsedService> getServices() {
        return this.services;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String getSharedUserId() {
        return this.sharedUserId;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getSharedUserLabelResourceId() {
        return this.sharedUserLabel;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public SigningDetails getSigningDetails() {
        return this.signingDetails;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String[] getSplitClassLoaderNames() {
        String[] strArr = this.splitClassLoaderNames;
        return strArr == null ? EmptyArray.STRING : strArr;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String[] getSplitCodePaths() {
        String[] strArr = this.splitCodePaths;
        return strArr == null ? EmptyArray.STRING : strArr;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public SparseArray<int[]> getSplitDependencies() {
        SparseArray<int[]> sparseArray = this.splitDependencies;
        return sparseArray == null ? EMPTY_INT_ARRAY_SPARSE_ARRAY : sparseArray;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int[] getSplitFlags() {
        return this.splitFlags;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String[] getSplitNames() {
        String[] strArr = this.splitNames;
        return strArr == null ? EmptyArray.STRING : strArr;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int[] getSplitRevisionCodes() {
        int[] iArr = this.splitRevisionCodes;
        return iArr == null ? EmptyArray.INT : iArr;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String getStaticSharedLibraryName() {
        return this.staticSharedLibraryName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public long getStaticSharedLibraryVersion() {
        return this.staticSharedLibVersion;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public UUID getStorageUuid() {
        return this.mStorageUuid;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getTargetSandboxVersion() {
        return this.targetSandboxVersion;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public int getTargetSdkVersion() {
        return this.targetSdkVersion;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public String getTaskAffinity() {
        return this.taskAffinity;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getThemeResourceId() {
        return this.theme;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public int getUiOptions() {
        return this.uiOptions;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public Set<String> getUpgradeKeySets() {
        return this.upgradeKeySets;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<String> getUsesLibraries() {
        return this.usesLibraries;
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageInternal
    public String[] getUsesLibrariesSorted() {
        if (this.mUsesLibrariesSorted == null) {
            this.mUsesLibrariesSorted = sortLibraries(this.usesLibraries);
        }
        return this.mUsesLibrariesSorted;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<String> getUsesNativeLibraries() {
        return this.usesNativeLibraries;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<String> getUsesOptionalLibraries() {
        return this.usesOptionalLibraries;
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageInternal
    public String[] getUsesOptionalLibrariesSorted() {
        if (this.mUsesOptionalLibrariesSorted == null) {
            this.mUsesOptionalLibrariesSorted = sortLibraries(this.usesOptionalLibraries);
        }
        return this.mUsesOptionalLibrariesSorted;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public List<String> getUsesOptionalNativeLibraries() {
        return this.usesOptionalNativeLibraries;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<ParsedUsesPermission> getUsesPermissions() {
        return this.usesPermissions;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<String> getUsesSdkLibraries() {
        return this.usesSdkLibraries;
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageInternal
    public String[] getUsesSdkLibrariesSorted() {
        if (this.mUsesSdkLibrariesSorted == null) {
            this.mUsesSdkLibrariesSorted = sortLibraries(this.usesSdkLibraries);
        }
        return this.mUsesSdkLibrariesSorted;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String[][] getUsesSdkLibrariesCertDigests() {
        return this.usesSdkLibrariesCertDigests;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public long[] getUsesSdkLibrariesVersionsMajor() {
        return this.usesSdkLibrariesVersionsMajor;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public List<String> getUsesStaticLibraries() {
        return this.usesStaticLibraries;
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageInternal
    public String[] getUsesStaticLibrariesSorted() {
        if (this.mUsesStaticLibrariesSorted == null) {
            this.mUsesStaticLibrariesSorted = sortLibraries(this.usesStaticLibraries);
        }
        return this.mUsesStaticLibrariesSorted;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String[][] getUsesStaticLibrariesCertDigests() {
        return this.usesStaticLibrariesCertDigests;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public long[] getUsesStaticLibrariesVersions() {
        return this.usesStaticLibrariesVersions;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackageHidden
    public int getVersionCode() {
        return this.versionCode;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackageHidden
    public int getVersionCodeMajor() {
        return this.versionCodeMajor;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getVersionName() {
        return this.versionName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getVolumeUuid() {
        return this.volumeUuid;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getZygotePreloadName() {
        return this.zygotePreloadName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean hasPreserveLegacyExternalStorage() {
        return getBoolean(137438953472L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean hasRequestForegroundServiceExemption() {
        return getBoolean(70368744177664L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public Boolean hasRequestRawExternalStorageAccess() {
        return this.requestRawExternalStorageAccess;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isAllowAudioPlaybackCapture() {
        return getBoolean(2147483648L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isBackupAllowed() {
        return getBoolean(4L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isClearUserDataAllowed() {
        return getBoolean(2048L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isClearUserDataOnFailedRestoreAllowed() {
        return getBoolean(1073741824L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isAllowNativeHeapPointerTagging() {
        return getBoolean(68719476736L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isTaskReparentingAllowed() {
        return getBoolean(1024L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isAnyDensity() {
        Boolean bool = this.anyDensity;
        if (bool == null) {
            return this.targetSdkVersion >= 4;
        }
        return bool.booleanValue();
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isBackupInForeground() {
        return getBoolean(16777216L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isHardwareAccelerated() {
        return getBoolean(2L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isSaveStateDisallowed() {
        return getBoolean(34359738368L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isCrossProfile() {
        return getBoolean(8796093022208L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isDebuggable() {
        return getBoolean(128L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isDefaultToDeviceProtectedStorage() {
        return getBoolean(67108864L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isDirectBootAware() {
        return getBoolean(134217728L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isEnabled() {
        return getBoolean(17592186044416L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isExternalStorage() {
        return getBoolean(1L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isExtractNativeLibrariesRequested() {
        return getBoolean(131072L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isForceQueryable() {
        return getBoolean(4398046511104L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isFullBackupOnly() {
        return getBoolean(32L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isGame() {
        return getBoolean(262144L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isDeclaredHavingCode() {
        return getBoolean(512L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isHasDomainUrls() {
        return getBoolean(4194304L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isUserDataFragile() {
        return getBoolean(17179869184L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isIsolatedSplitLoading() {
        return getBoolean(2097152L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isKillAfterRestoreAllowed() {
        return getBoolean(8L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isLargeHeap() {
        return getBoolean(4096L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isLeavingSharedUser() {
        return getBoolean(2251799813685248L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isMultiArch() {
        return getBoolean(65536L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isOnBackInvokedCallbackEnabled() {
        return getBoolean(1125899906842624L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isResourceOverlay() {
        return getBoolean(1048576L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isOverlayIsStatic() {
        return getBoolean(549755813888L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isPartiallyDirectBootAware() {
        return getBoolean(268435456L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isPersistent() {
        return getBoolean(64L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isProfileable() {
        return !getBoolean(35184372088832L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isProfileableByShell() {
        return isProfileable() && getBoolean(8388608L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isRequestLegacyExternalStorage() {
        return getBoolean(4294967296L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isRequiredForAllUsers() {
        return getBoolean(274877906944L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isResetEnabledSettingsOnAppDataCleared() {
        return getBoolean(281474976710656L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isResizeable() {
        Boolean bool = this.resizeable;
        if (bool == null) {
            return this.targetSdkVersion >= 4;
        }
        return bool.booleanValue();
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isResizeableActivityViaSdkVersion() {
        return getBoolean(536870912L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isRestoreAnyVersion() {
        return getBoolean(16L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isSdkLibrary() {
        return getBoolean(562949953421312L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isStaticSharedLibrary() {
        return getBoolean(524288L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isExtraLargeScreensSupported() {
        Boolean bool = this.supportsExtraLargeScreens;
        if (bool == null) {
            return this.targetSdkVersion >= 9;
        }
        return bool.booleanValue();
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isLargeScreensSupported() {
        Boolean bool = this.supportsLargeScreens;
        if (bool == null) {
            return this.targetSdkVersion >= 4;
        }
        return bool.booleanValue();
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isNormalScreensSupported() {
        Boolean bool = this.supportsNormalScreens;
        return bool == null || bool.booleanValue();
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isRtlSupported() {
        return getBoolean(16384L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage, com.android.server.p011pm.pkg.parsing.ParsingPackage
    public boolean isSmallScreensSupported() {
        Boolean bool = this.supportsSmallScreens;
        if (bool == null) {
            return this.targetSdkVersion >= 4;
        }
        return bool.booleanValue();
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isTestOnly() {
        return getBoolean(32768L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean is32BitAbiPreferred() {
        return getBoolean(1099511627776L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isUseEmbeddedDex() {
        return getBoolean(33554432L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isCleartextTrafficAllowed() {
        return getBoolean(8192L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isNonSdkApiRequested() {
        return getBoolean(8589934592L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isVisibleToInstantApps() {
        return getBoolean(2199023255552L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isAllowUpdateOwnership() {
        return getBoolean2(4L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isVmSafeMode() {
        return getBoolean(256L);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl removeUsesOptionalNativeLibrary(String str) {
        this.usesOptionalNativeLibraries = CollectionUtils.remove(this.usesOptionalNativeLibraries, str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setAllowAudioPlaybackCapture(boolean z) {
        return setBoolean(2147483648L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setBackupAllowed(boolean z) {
        return setBoolean(4L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setClearUserDataAllowed(boolean z) {
        return setBoolean(2048L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setClearUserDataOnFailedRestoreAllowed(boolean z) {
        return setBoolean(1073741824L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setAllowNativeHeapPointerTagging(boolean z) {
        return setBoolean(68719476736L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setTaskReparentingAllowed(boolean z) {
        return setBoolean(1024L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setAnyDensity(int i) {
        if (i == 1) {
            return this;
        }
        this.anyDensity = Boolean.valueOf(i < 0);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setAppComponentFactory(String str) {
        this.appComponentFactory = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public ParsingPackage setAttributionsAreUserVisible(boolean z) {
        setBoolean(140737488355328L, z);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setAutoRevokePermissions(int i) {
        this.autoRevokePermissions = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setBackupAgentName(String str) {
        this.backupAgentName = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setBackupInForeground(boolean z) {
        return setBoolean(16777216L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setBannerResourceId(int i) {
        this.banner = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setHardwareAccelerated(boolean z) {
        return setBoolean(2L, z);
    }

    public PackageImpl setBaseRevisionCode(int i) {
        this.baseRevisionCode = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setSaveStateDisallowed(boolean z) {
        return setBoolean(34359738368L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setCategory(int i) {
        this.category = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setClassLoaderName(String str) {
        this.classLoaderName = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setApplicationClassName(String str) {
        this.className = str == null ? null : str.trim();
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setCompatibleWidthLimitDp(int i) {
        this.compatibleWidthLimitDp = i;
        return this;
    }

    public PackageImpl setCompileSdkVersion(int i) {
        this.compileSdkVersion = i;
        return this;
    }

    public ParsingPackage setCompileSdkVersionCodeName(String str) {
        this.compileSdkVersionCodeName = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setCrossProfile(boolean z) {
        return setBoolean(8796093022208L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setDataExtractionRulesResourceId(int i) {
        this.dataExtractionRules = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setDebuggable(boolean z) {
        return setBoolean(128L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setDescriptionResourceId(int i) {
        this.descriptionRes = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setEnabled(boolean z) {
        return setBoolean(17592186044416L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setExternalStorage(boolean z) {
        return setBoolean(1L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setExtractNativeLibrariesRequested(boolean z) {
        return setBoolean(131072L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setForceQueryable(boolean z) {
        return setBoolean(4398046511104L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setFullBackupContentResourceId(int i) {
        this.fullBackupContent = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setFullBackupOnly(boolean z) {
        return setBoolean(32L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setGame(boolean z) {
        return setBoolean(262144L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setGwpAsanMode(int i) {
        this.gwpAsanMode = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setDeclaredHavingCode(boolean z) {
        return setBoolean(512L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setHasDomainUrls(boolean z) {
        return setBoolean(4194304L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setUserDataFragile(boolean z) {
        return setBoolean(17179869184L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setIconResourceId(int i) {
        this.iconRes = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setInstallLocation(int i) {
        this.installLocation = i;
        return this;
    }

    public PackageImpl setIsolatedSplitLoading(boolean z) {
        return setBoolean(2097152L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setKillAfterRestoreAllowed(boolean z) {
        return setBoolean(8L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public ParsingPackage setKnownActivityEmbeddingCerts(Set<String> set) {
        this.mKnownActivityEmbeddingCerts = set;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setLabelResourceId(int i) {
        this.labelRes = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setLargeHeap(boolean z) {
        return setBoolean(4096L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setLargestWidthLimitDp(int i) {
        this.largestWidthLimitDp = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setLeavingSharedUser(boolean z) {
        return setBoolean(2251799813685248L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setLocaleConfigResourceId(int i) {
        this.mLocaleConfigRes = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setLogoResourceId(int i) {
        this.logo = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setManageSpaceActivityName(String str) {
        this.manageSpaceActivityName = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setMaxAspectRatio(float f) {
        this.maxAspectRatio = f;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setMaxSdkVersion(int i) {
        this.maxSdkVersion = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setMemtagMode(int i) {
        this.memtagMode = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setMetaData(Bundle bundle) {
        this.metaData = bundle;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setMinAspectRatio(float f) {
        this.minAspectRatio = f;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setMinExtensionVersions(SparseIntArray sparseIntArray) {
        this.minExtensionVersions = sparseIntArray;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setMinSdkVersion(int i) {
        this.minSdkVersion = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setMultiArch(boolean z) {
        return setBoolean(65536L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setNativeHeapZeroInitialized(int i) {
        this.nativeHeapZeroInitialized = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setNetworkSecurityConfigResourceId(int i) {
        this.networkSecurityConfigRes = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setNonLocalizedLabel(CharSequence charSequence) {
        this.nonLocalizedLabel = charSequence == null ? null : charSequence.toString().trim();
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public ParsingPackage setOnBackInvokedCallbackEnabled(boolean z) {
        setBoolean(1125899906842624L, z);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setResourceOverlay(boolean z) {
        return setBoolean(1048576L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setOverlayCategory(String str) {
        this.overlayCategory = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setOverlayIsStatic(boolean z) {
        return setBoolean(549755813888L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setOverlayPriority(int i) {
        this.overlayPriority = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setOverlayTarget(String str) {
        this.overlayTarget = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setOverlayTargetOverlayableName(String str) {
        this.overlayTargetOverlayableName = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setPartiallyDirectBootAware(boolean z) {
        return setBoolean(268435456L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setPermission(String str) {
        this.permission = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setPreserveLegacyExternalStorage(boolean z) {
        return setBoolean(137438953472L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setProcessName(String str) {
        this.processName = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setProcesses(Map<String, ParsedProcess> map) {
        this.processes = map;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setProfileable(boolean z) {
        return setBoolean(35184372088832L, !z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setProfileableByShell(boolean z) {
        return setBoolean(8388608L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRequestForegroundServiceExemption(boolean z) {
        return setBoolean(70368744177664L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRequestLegacyExternalStorage(boolean z) {
        return setBoolean(4294967296L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRequestRawExternalStorageAccess(Boolean bool) {
        this.requestRawExternalStorageAccess = bool;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRequiredAccountType(String str) {
        this.requiredAccountType = TextUtils.nullIfEmpty(str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRequiredForAllUsers(boolean z) {
        return setBoolean(274877906944L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRequiresSmallestWidthDp(int i) {
        this.requiresSmallestWidthDp = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public ParsingPackage setResetEnabledSettingsOnAppDataCleared(boolean z) {
        setBoolean(281474976710656L, z);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setResizeable(int i) {
        if (i == 1) {
            return this;
        }
        this.resizeable = Boolean.valueOf(i < 0);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setResizeableActivity(Boolean bool) {
        this.resizeableActivity = bool;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setResizeableActivityViaSdkVersion(boolean z) {
        return setBoolean(536870912L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRestoreAnyVersion(boolean z) {
        return setBoolean(16L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRestrictedAccountType(String str) {
        this.restrictedAccountType = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRoundIconResourceId(int i) {
        this.roundIconRes = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setSdkLibraryName(String str) {
        this.sdkLibraryName = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setSdkLibVersionMajor(int i) {
        this.sdkLibVersionMajor = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setSdkLibrary(boolean z) {
        return setBoolean(562949953421312L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setSharedUserId(String str) {
        this.sharedUserId = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setSharedUserLabelResourceId(int i) {
        this.sharedUserLabel = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setSplitClassLoaderName(int i, String str) {
        this.splitClassLoaderNames[i] = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setSplitHasCode(int i, boolean z) {
        int i2;
        int[] iArr = this.splitFlags;
        if (z) {
            i2 = iArr[i] | 4;
        } else {
            i2 = iArr[i] & (-5);
        }
        iArr[i] = i2;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setStaticSharedLibraryName(String str) {
        this.staticSharedLibraryName = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setStaticSharedLibraryVersion(long j) {
        this.staticSharedLibVersion = j;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setStaticSharedLibrary(boolean z) {
        return setBoolean(524288L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setExtraLargeScreensSupported(int i) {
        if (i == 1) {
            return this;
        }
        this.supportsExtraLargeScreens = Boolean.valueOf(i < 0);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setLargeScreensSupported(int i) {
        if (i == 1) {
            return this;
        }
        this.supportsLargeScreens = Boolean.valueOf(i < 0);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setNormalScreensSupported(int i) {
        if (i == 1) {
            return this;
        }
        this.supportsNormalScreens = Boolean.valueOf(i < 0);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRtlSupported(boolean z) {
        return setBoolean(16384L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setSmallScreensSupported(int i) {
        if (i == 1) {
            return this;
        }
        this.supportsSmallScreens = Boolean.valueOf(i < 0);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setTargetSandboxVersion(int i) {
        this.targetSandboxVersion = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setTargetSdkVersion(int i) {
        this.targetSdkVersion = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setTaskAffinity(String str) {
        this.taskAffinity = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setTestOnly(boolean z) {
        return setBoolean(32768L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setThemeResourceId(int i) {
        this.theme = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setUiOptions(int i) {
        this.uiOptions = i;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setUpgradeKeySets(Set<String> set) {
        this.upgradeKeySets = set;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl set32BitAbiPreferred(boolean z) {
        return setBoolean(1099511627776L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setUseEmbeddedDex(boolean z) {
        return setBoolean(33554432L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setCleartextTrafficAllowed(boolean z) {
        return setBoolean(8192L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setNonSdkApiRequested(boolean z) {
        return setBoolean(8589934592L, z);
    }

    public PackageImpl setVersionName(String str) {
        this.versionName = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setVisibleToInstantApps(boolean z) {
        return setBoolean(2199023255552L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setVmSafeMode(boolean z) {
        return setBoolean(256L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setVolumeUuid(String str) {
        this.volumeUuid = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setZygotePreloadName(String str) {
        this.zygotePreloadName = str;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setAllowUpdateOwnership(boolean z) {
        return setBoolean2(4L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl sortActivities() {
        Collections.sort(this.activities, ORDER_COMPARATOR);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl sortReceivers() {
        Collections.sort(this.receivers, ORDER_COMPARATOR);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl sortServices() {
        Collections.sort(this.services, ORDER_COMPARATOR);
        return this;
    }

    public ApplicationInfo toAppInfoWithoutStateWithoutFlags() {
        int i;
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.appComponentFactory = this.appComponentFactory;
        applicationInfo.backupAgentName = this.backupAgentName;
        applicationInfo.banner = this.banner;
        applicationInfo.category = this.category;
        applicationInfo.classLoaderName = this.classLoaderName;
        applicationInfo.className = this.className;
        applicationInfo.compatibleWidthLimitDp = this.compatibleWidthLimitDp;
        applicationInfo.compileSdkVersion = this.compileSdkVersion;
        applicationInfo.compileSdkVersionCodename = this.compileSdkVersionCodeName;
        applicationInfo.crossProfile = isCrossProfile();
        applicationInfo.descriptionRes = this.descriptionRes;
        applicationInfo.enabled = getBoolean(17592186044416L);
        applicationInfo.fullBackupContent = this.fullBackupContent;
        applicationInfo.dataExtractionRulesRes = this.dataExtractionRules;
        if (!ParsingPackageUtils.sUseRoundIcon || (i = this.roundIconRes) == 0) {
            i = this.iconRes;
        }
        applicationInfo.icon = i;
        applicationInfo.iconRes = this.iconRes;
        applicationInfo.roundIconRes = this.roundIconRes;
        applicationInfo.installLocation = this.installLocation;
        applicationInfo.labelRes = this.labelRes;
        applicationInfo.largestWidthLimitDp = this.largestWidthLimitDp;
        applicationInfo.logo = this.logo;
        applicationInfo.manageSpaceActivityName = this.manageSpaceActivityName;
        applicationInfo.maxAspectRatio = this.maxAspectRatio;
        applicationInfo.metaData = this.metaData;
        applicationInfo.minAspectRatio = this.minAspectRatio;
        applicationInfo.minSdkVersion = this.minSdkVersion;
        applicationInfo.name = this.className;
        applicationInfo.networkSecurityConfigRes = this.networkSecurityConfigRes;
        applicationInfo.nonLocalizedLabel = this.nonLocalizedLabel;
        applicationInfo.packageName = this.packageName;
        applicationInfo.permission = this.permission;
        applicationInfo.processName = getProcessName();
        applicationInfo.requiresSmallestWidthDp = this.requiresSmallestWidthDp;
        applicationInfo.splitClassLoaderNames = this.splitClassLoaderNames;
        SparseArray<int[]> sparseArray = this.splitDependencies;
        applicationInfo.splitDependencies = (sparseArray == null || sparseArray.size() == 0) ? null : this.splitDependencies;
        applicationInfo.splitNames = this.splitNames;
        applicationInfo.storageUuid = this.mStorageUuid;
        applicationInfo.targetSandboxVersion = this.targetSandboxVersion;
        applicationInfo.targetSdkVersion = this.targetSdkVersion;
        applicationInfo.taskAffinity = this.taskAffinity;
        applicationInfo.theme = this.theme;
        applicationInfo.uiOptions = this.uiOptions;
        applicationInfo.volumeUuid = this.volumeUuid;
        applicationInfo.zygotePreloadName = this.zygotePreloadName;
        applicationInfo.setGwpAsanMode(this.gwpAsanMode);
        applicationInfo.setMemtagMode(this.memtagMode);
        applicationInfo.setNativeHeapZeroInitialized(this.nativeHeapZeroInitialized);
        applicationInfo.setRequestRawExternalStorageAccess(this.requestRawExternalStorageAccess);
        applicationInfo.setBaseCodePath(this.mBaseApkPath);
        applicationInfo.setBaseResourcePath(this.mBaseApkPath);
        applicationInfo.setCodePath(this.mPath);
        applicationInfo.setResourcePath(this.mPath);
        applicationInfo.setSplitCodePaths(ArrayUtils.size(this.splitCodePaths) == 0 ? null : this.splitCodePaths);
        applicationInfo.setSplitResourcePaths(ArrayUtils.size(this.splitCodePaths) != 0 ? this.splitCodePaths : null);
        applicationInfo.setVersionCode(this.mLongVersionCode);
        applicationInfo.setAppClassNamesByProcess(buildAppClassNamesByProcess());
        applicationInfo.setLocaleConfigRes(this.mLocaleConfigRes);
        if (!this.mKnownActivityEmbeddingCerts.isEmpty()) {
            applicationInfo.setKnownActivityEmbeddingCerts(this.mKnownActivityEmbeddingCerts);
        }
        return applicationInfo;
    }

    public final PackageImpl setBoolean(long j, boolean z) {
        if (z) {
            this.mBooleans = j | this.mBooleans;
        } else {
            this.mBooleans = (~j) & this.mBooleans;
        }
        return this;
    }

    public final boolean getBoolean(long j) {
        return (this.mBooleans & j) != 0;
    }

    public final PackageImpl setBoolean2(long j, boolean z) {
        if (z) {
            this.mBooleans2 = j | this.mBooleans2;
        } else {
            this.mBooleans2 = (~j) & this.mBooleans2;
        }
        return this;
    }

    public final boolean getBoolean2(long j) {
        return (this.mBooleans2 & j) != 0;
    }

    @VisibleForTesting
    public PackageImpl(String str, String str2, String str3, TypedArray typedArray, boolean z) {
        this.usesLibraries = Collections.emptyList();
        this.usesOptionalLibraries = Collections.emptyList();
        this.usesNativeLibraries = Collections.emptyList();
        this.usesOptionalNativeLibraries = Collections.emptyList();
        this.originalPackages = Collections.emptyList();
        this.adoptPermissions = Collections.emptyList();
        this.requestedPermissions = Collections.emptyList();
        this.protectedBroadcasts = Collections.emptyList();
        this.activities = Collections.emptyList();
        this.apexSystemServices = Collections.emptyList();
        this.receivers = Collections.emptyList();
        this.services = Collections.emptyList();
        this.providers = Collections.emptyList();
        this.permissions = Collections.emptyList();
        this.permissionGroups = Collections.emptyList();
        this.instrumentations = Collections.emptyList();
        this.overlayables = Collections.emptyMap();
        this.libraryNames = Collections.emptyList();
        this.usesStaticLibraries = Collections.emptyList();
        this.usesSdkLibraries = Collections.emptyList();
        this.configPreferences = Collections.emptyList();
        this.reqFeatures = Collections.emptyList();
        this.featureGroups = Collections.emptyList();
        this.usesPermissions = Collections.emptyList();
        this.implicitPermissions = Collections.emptyList();
        this.upgradeKeySets = Collections.emptySet();
        this.keySetMapping = Collections.emptyMap();
        this.attributions = Collections.emptyList();
        this.preferredActivityFilters = Collections.emptyList();
        this.processes = Collections.emptyMap();
        this.mProperties = Collections.emptyMap();
        this.signingDetails = SigningDetails.UNKNOWN;
        this.queriesIntents = Collections.emptyList();
        this.queriesPackages = Collections.emptyList();
        this.queriesProviders = Collections.emptySet();
        this.category = -1;
        this.installLocation = -1;
        this.minSdkVersion = 1;
        this.maxSdkVersion = Integer.MAX_VALUE;
        this.targetSdkVersion = 0;
        this.mimeGroups = Collections.emptySet();
        this.mBooleans = 17592186044416L;
        this.mKnownActivityEmbeddingCerts = Collections.emptySet();
        this.uid = -1;
        this.packageName = TextUtils.safeIntern(str);
        this.mBaseApkPath = str2;
        this.mPath = str3;
        if (typedArray != null) {
            this.versionCode = typedArray.getInteger(1, 0);
            this.versionCodeMajor = typedArray.getInteger(11, 0);
            setBaseRevisionCode(typedArray.getInteger(5, 0));
            setVersionName(typedArray.getNonConfigurationString(2, 0));
            setCompileSdkVersion(typedArray.getInteger(9, 0));
            setCompileSdkVersionCodeName(typedArray.getNonConfigurationString(10, 0));
            setIsolatedSplitLoading(typedArray.getBoolean(6, false));
        }
        this.manifestPackageName = this.packageName;
        setBoolean(4503599627370496L, z);
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl hideAsParsed() {
        assignDerivedFields();
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public AndroidPackageInternal hideAsFinal() {
        if (this.mStorageUuid == null) {
            assignDerivedFields();
        }
        assignDerivedFields2();
        makeImmutable();
        return this;
    }

    public static String[] sortLibraries(List<String> list) {
        if (list.size() == 0) {
            return EmptyArray.STRING;
        }
        String[] strArr = (String[]) list.toArray(EmptyArray.STRING);
        Arrays.sort(strArr);
        return strArr;
    }

    public final void assignDerivedFields2() {
        this.mBaseAppInfoFlags = PackageInfoUtils.appInfoFlags(this, (PackageStateInternal) null);
        this.mBaseAppInfoPrivateFlags = PackageInfoUtils.appInfoPrivateFlags(this, (PackageStateInternal) null);
        this.mBaseAppInfoPrivateFlagsExt = PackageInfoUtils.appInfoPrivateFlagsExt(this, (PackageStateInternal) null);
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getDataDirectoryPath(getVolumeUuid()));
        String str = File.separator;
        sb.append(str);
        String sb2 = sb.toString();
        String str2 = str + 0 + str;
        this.mBaseAppDataCredentialProtectedDirForSystemUser = TextUtils.safeIntern(sb2 + "user" + str2);
        this.mBaseAppDataDeviceProtectedDirForSystemUser = TextUtils.safeIntern(sb2 + "user_de" + str2);
    }

    public final void makeImmutable() {
        this.usesLibraries = Collections.unmodifiableList(this.usesLibraries);
        this.usesOptionalLibraries = Collections.unmodifiableList(this.usesOptionalLibraries);
        this.usesNativeLibraries = Collections.unmodifiableList(this.usesNativeLibraries);
        this.usesOptionalNativeLibraries = Collections.unmodifiableList(this.usesOptionalNativeLibraries);
        this.originalPackages = Collections.unmodifiableList(this.originalPackages);
        this.adoptPermissions = Collections.unmodifiableList(this.adoptPermissions);
        this.requestedPermissions = Collections.unmodifiableList(this.requestedPermissions);
        this.protectedBroadcasts = Collections.unmodifiableList(this.protectedBroadcasts);
        this.apexSystemServices = Collections.unmodifiableList(this.apexSystemServices);
        this.activities = Collections.unmodifiableList(this.activities);
        this.receivers = Collections.unmodifiableList(this.receivers);
        this.services = Collections.unmodifiableList(this.services);
        this.providers = Collections.unmodifiableList(this.providers);
        this.permissions = Collections.unmodifiableList(this.permissions);
        this.permissionGroups = Collections.unmodifiableList(this.permissionGroups);
        this.instrumentations = Collections.unmodifiableList(this.instrumentations);
        this.overlayables = Collections.unmodifiableMap(this.overlayables);
        this.libraryNames = Collections.unmodifiableList(this.libraryNames);
        this.usesStaticLibraries = Collections.unmodifiableList(this.usesStaticLibraries);
        this.usesSdkLibraries = Collections.unmodifiableList(this.usesSdkLibraries);
        this.configPreferences = Collections.unmodifiableList(this.configPreferences);
        this.reqFeatures = Collections.unmodifiableList(this.reqFeatures);
        this.featureGroups = Collections.unmodifiableList(this.featureGroups);
        this.usesPermissions = Collections.unmodifiableList(this.usesPermissions);
        this.usesSdkLibraries = Collections.unmodifiableList(this.usesSdkLibraries);
        this.implicitPermissions = Collections.unmodifiableList(this.implicitPermissions);
        this.upgradeKeySets = Collections.unmodifiableSet(this.upgradeKeySets);
        this.keySetMapping = Collections.unmodifiableMap(this.keySetMapping);
        this.attributions = Collections.unmodifiableList(this.attributions);
        this.preferredActivityFilters = Collections.unmodifiableList(this.preferredActivityFilters);
        this.processes = Collections.unmodifiableMap(this.processes);
        this.mProperties = Collections.unmodifiableMap(this.mProperties);
        this.queriesIntents = Collections.unmodifiableList(this.queriesIntents);
        this.queriesPackages = Collections.unmodifiableList(this.queriesPackages);
        this.queriesProviders = Collections.unmodifiableSet(this.queriesProviders);
        this.mimeGroups = Collections.unmodifiableSet(this.mimeGroups);
        this.mKnownActivityEmbeddingCerts = Collections.unmodifiableSet(this.mKnownActivityEmbeddingCerts);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public long getLongVersionCode() {
        return PackageInfo.composeLongVersionCode(this.versionCodeMajor, this.versionCode);
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl removePermission(int i) {
        this.permissions.remove(i);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl addUsesOptionalLibrary(int i, String str) {
        this.usesOptionalLibraries = CollectionUtils.add(this.usesOptionalLibraries, i, TextUtils.safeIntern(str));
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl addUsesLibrary(int i, String str) {
        this.usesLibraries = CollectionUtils.add(this.usesLibraries, i, TextUtils.safeIntern(str));
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl removeUsesLibrary(String str) {
        this.usesLibraries = CollectionUtils.remove(this.usesLibraries, str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl removeUsesOptionalLibrary(String str) {
        this.usesOptionalLibraries = CollectionUtils.remove(this.usesOptionalLibraries, str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setSigningDetails(SigningDetails signingDetails) {
        this.signingDetails = signingDetails;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setRestrictUpdateHash(byte... bArr) {
        this.restrictUpdateHash = bArr;
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setPersistent(boolean z) {
        setBoolean(64L, z);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setDefaultToDeviceProtectedStorage(boolean z) {
        setBoolean(67108864L, z);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.parsing.ParsingPackage
    public PackageImpl setDirectBootAware(boolean z) {
        setBoolean(134217728L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl clearProtectedBroadcasts() {
        this.protectedBroadcasts.clear();
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl clearOriginalPackages() {
        this.originalPackages.clear();
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl clearAdoptPermissions() {
        this.adoptPermissions.clear();
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setPath(String str) {
        this.mPath = str;
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setPackageName(String str) {
        this.packageName = TextUtils.safeIntern(str);
        int size = this.permissions.size();
        for (int i = 0; i < size; i++) {
            ComponentMutateUtils.setPackageName(this.permissions.get(i), this.packageName);
        }
        int size2 = this.permissionGroups.size();
        for (int i2 = 0; i2 < size2; i2++) {
            ComponentMutateUtils.setPackageName(this.permissionGroups.get(i2), this.packageName);
        }
        int size3 = this.activities.size();
        for (int i3 = 0; i3 < size3; i3++) {
            ComponentMutateUtils.setPackageName(this.activities.get(i3), this.packageName);
        }
        int size4 = this.receivers.size();
        for (int i4 = 0; i4 < size4; i4++) {
            ComponentMutateUtils.setPackageName(this.receivers.get(i4), this.packageName);
        }
        int size5 = this.providers.size();
        for (int i5 = 0; i5 < size5; i5++) {
            ComponentMutateUtils.setPackageName(this.providers.get(i5), this.packageName);
        }
        int size6 = this.services.size();
        for (int i6 = 0; i6 < size6; i6++) {
            ComponentMutateUtils.setPackageName(this.services.get(i6), this.packageName);
        }
        int size7 = this.instrumentations.size();
        for (int i7 = 0; i7 < size7; i7++) {
            ComponentMutateUtils.setPackageName(this.instrumentations.get(i7), this.packageName);
        }
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setAllComponentsDirectBootAware(boolean z) {
        int size = this.activities.size();
        for (int i = 0; i < size; i++) {
            ComponentMutateUtils.setDirectBootAware(this.activities.get(i), z);
        }
        int size2 = this.receivers.size();
        for (int i2 = 0; i2 < size2; i2++) {
            ComponentMutateUtils.setDirectBootAware(this.receivers.get(i2), z);
        }
        int size3 = this.providers.size();
        for (int i3 = 0; i3 < size3; i3++) {
            ComponentMutateUtils.setDirectBootAware(this.providers.get(i3), z);
        }
        int size4 = this.services.size();
        for (int i4 = 0; i4 < size4; i4++) {
            ComponentMutateUtils.setDirectBootAware(this.services.get(i4), z);
        }
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setBaseApkPath(String str) {
        this.mBaseApkPath = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setNativeLibraryDir(String str) {
        this.nativeLibraryDir = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setNativeLibraryRootDir(String str) {
        this.nativeLibraryRootDir = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setPrimaryCpuAbi(String str) {
        this.primaryCpuAbi = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setSecondaryCpuAbi(String str) {
        this.secondaryCpuAbi = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setSecondaryNativeLibraryDir(String str) {
        this.secondaryNativeLibraryDir = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setSplitCodePaths(String[] strArr) {
        this.splitCodePaths = strArr;
        if (strArr != null) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                String[] strArr2 = this.splitCodePaths;
                strArr2[i] = TextUtils.safeIntern(strArr2[i]);
            }
        }
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl capPermissionPriorities() {
        for (int size = this.permissionGroups.size() - 1; size >= 0; size--) {
            ComponentMutateUtils.setPriority(this.permissionGroups.get(size), 0);
        }
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl markNotActivitiesAsNotExportedIfSingleUser() {
        int size = this.receivers.size();
        for (int i = 0; i < size; i++) {
            ParsedActivity parsedActivity = this.receivers.get(i);
            if ((1073741824 & parsedActivity.getFlags()) != 0) {
                ComponentMutateUtils.setExported(parsedActivity, false);
            }
        }
        int size2 = this.services.size();
        for (int i2 = 0; i2 < size2; i2++) {
            ParsedService parsedService = this.services.get(i2);
            if ((parsedService.getFlags() & 1073741824) != 0) {
                ComponentMutateUtils.setExported(parsedService, false);
            }
        }
        int size3 = this.providers.size();
        for (int i3 = 0; i3 < size3; i3++) {
            ParsedProvider parsedProvider = this.providers.get(i3);
            if ((parsedProvider.getFlags() & 1073741824) != 0) {
                ComponentMutateUtils.setExported(parsedProvider, false);
            }
        }
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setCoreApp(boolean z) {
        return setBoolean(4503599627370496L, z);
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setVersionCode(int i) {
        this.versionCode = i;
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setVersionCodeMajor(int i) {
        this.versionCodeMajor = i;
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageHidden
    public ApplicationInfo toAppInfoWithoutState() {
        ApplicationInfo appInfoWithoutStateWithoutFlags = toAppInfoWithoutStateWithoutFlags();
        appInfoWithoutStateWithoutFlags.flags = this.mBaseAppInfoFlags;
        appInfoWithoutStateWithoutFlags.privateFlags = this.mBaseAppInfoPrivateFlags;
        appInfoWithoutStateWithoutFlags.privateFlagsExt = this.mBaseAppInfoPrivateFlagsExt;
        appInfoWithoutStateWithoutFlags.nativeLibraryDir = this.nativeLibraryDir;
        appInfoWithoutStateWithoutFlags.nativeLibraryRootDir = this.nativeLibraryRootDir;
        appInfoWithoutStateWithoutFlags.nativeLibraryRootRequiresIsa = this.nativeLibraryRootRequiresIsa;
        appInfoWithoutStateWithoutFlags.primaryCpuAbi = this.primaryCpuAbi;
        appInfoWithoutStateWithoutFlags.secondaryCpuAbi = this.secondaryCpuAbi;
        appInfoWithoutStateWithoutFlags.secondaryNativeLibraryDir = this.secondaryNativeLibraryDir;
        appInfoWithoutStateWithoutFlags.seInfoUser = ":complete";
        appInfoWithoutStateWithoutFlags.uid = this.uid;
        return appInfoWithoutStateWithoutFlags;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        sForBoolean.parcel(this.supportsSmallScreens, parcel, i);
        sForBoolean.parcel(this.supportsNormalScreens, parcel, i);
        sForBoolean.parcel(this.supportsLargeScreens, parcel, i);
        sForBoolean.parcel(this.supportsExtraLargeScreens, parcel, i);
        sForBoolean.parcel(this.resizeable, parcel, i);
        sForBoolean.parcel(this.anyDensity, parcel, i);
        parcel.writeInt(this.versionCode);
        parcel.writeInt(this.versionCodeMajor);
        parcel.writeInt(this.baseRevisionCode);
        sForInternedString.parcel(this.versionName, parcel, i);
        parcel.writeInt(this.compileSdkVersion);
        parcel.writeString(this.compileSdkVersionCodeName);
        sForInternedString.parcel(this.packageName, parcel, i);
        parcel.writeString(this.mBaseApkPath);
        parcel.writeString(this.restrictedAccountType);
        parcel.writeString(this.requiredAccountType);
        sForInternedString.parcel(this.overlayTarget, parcel, i);
        parcel.writeString(this.overlayTargetOverlayableName);
        parcel.writeString(this.overlayCategory);
        parcel.writeInt(this.overlayPriority);
        sForInternedStringValueMap.parcel(this.overlayables, parcel, i);
        sForInternedString.parcel(this.sdkLibraryName, parcel, i);
        parcel.writeInt(this.sdkLibVersionMajor);
        sForInternedString.parcel(this.staticSharedLibraryName, parcel, i);
        parcel.writeLong(this.staticSharedLibVersion);
        sForInternedStringList.parcel(this.libraryNames, parcel, i);
        sForInternedStringList.parcel(this.usesLibraries, parcel, i);
        sForInternedStringList.parcel(this.usesOptionalLibraries, parcel, i);
        sForInternedStringList.parcel(this.usesNativeLibraries, parcel, i);
        sForInternedStringList.parcel(this.usesOptionalNativeLibraries, parcel, i);
        sForInternedStringList.parcel(this.usesStaticLibraries, parcel, i);
        parcel.writeLongArray(this.usesStaticLibrariesVersions);
        String[][] strArr = this.usesStaticLibrariesCertDigests;
        int i2 = 0;
        if (strArr == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(strArr.length);
            int i3 = 0;
            while (true) {
                String[][] strArr2 = this.usesStaticLibrariesCertDigests;
                if (i3 >= strArr2.length) {
                    break;
                }
                parcel.writeStringArray(strArr2[i3]);
                i3++;
            }
        }
        sForInternedStringList.parcel(this.usesSdkLibraries, parcel, i);
        parcel.writeLongArray(this.usesSdkLibrariesVersionsMajor);
        String[][] strArr3 = this.usesSdkLibrariesCertDigests;
        if (strArr3 == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(strArr3.length);
            while (true) {
                String[][] strArr4 = this.usesSdkLibrariesCertDigests;
                if (i2 >= strArr4.length) {
                    break;
                }
                parcel.writeStringArray(strArr4[i2]);
                i2++;
            }
        }
        sForInternedString.parcel(this.sharedUserId, parcel, i);
        parcel.writeInt(this.sharedUserLabel);
        parcel.writeTypedList(this.configPreferences);
        parcel.writeTypedList(this.reqFeatures);
        parcel.writeTypedList(this.featureGroups);
        parcel.writeByteArray(this.restrictUpdateHash);
        parcel.writeStringList(this.originalPackages);
        sForInternedStringList.parcel(this.adoptPermissions, parcel, i);
        sForInternedStringList.parcel(this.requestedPermissions, parcel, i);
        ParsingUtils.writeParcelableList(parcel, this.usesPermissions);
        sForInternedStringList.parcel(this.implicitPermissions, parcel, i);
        sForStringSet.parcel(this.upgradeKeySets, parcel, i);
        ParsingPackageUtils.writeKeySetMapping(parcel, this.keySetMapping);
        sForInternedStringList.parcel(this.protectedBroadcasts, parcel, i);
        ParsingUtils.writeParcelableList(parcel, this.activities);
        ParsingUtils.writeParcelableList(parcel, this.apexSystemServices);
        ParsingUtils.writeParcelableList(parcel, this.receivers);
        ParsingUtils.writeParcelableList(parcel, this.services);
        ParsingUtils.writeParcelableList(parcel, this.providers);
        ParsingUtils.writeParcelableList(parcel, this.attributions);
        ParsingUtils.writeParcelableList(parcel, this.permissions);
        ParsingUtils.writeParcelableList(parcel, this.permissionGroups);
        ParsingUtils.writeParcelableList(parcel, this.instrumentations);
        sForIntentInfoPairs.parcel(this.preferredActivityFilters, parcel, i);
        parcel.writeMap(this.processes);
        parcel.writeBundle(this.metaData);
        sForInternedString.parcel(this.volumeUuid, parcel, i);
        parcel.writeParcelable(this.signingDetails, i);
        parcel.writeString(this.mPath);
        parcel.writeTypedList(this.queriesIntents, i);
        sForInternedStringList.parcel(this.queriesPackages, parcel, i);
        sForInternedStringSet.parcel(this.queriesProviders, parcel, i);
        parcel.writeString(this.appComponentFactory);
        parcel.writeString(this.backupAgentName);
        parcel.writeInt(this.banner);
        parcel.writeInt(this.category);
        parcel.writeString(this.classLoaderName);
        parcel.writeString(this.className);
        parcel.writeInt(this.compatibleWidthLimitDp);
        parcel.writeInt(this.descriptionRes);
        parcel.writeInt(this.fullBackupContent);
        parcel.writeInt(this.dataExtractionRules);
        parcel.writeInt(this.iconRes);
        parcel.writeInt(this.installLocation);
        parcel.writeInt(this.labelRes);
        parcel.writeInt(this.largestWidthLimitDp);
        parcel.writeInt(this.logo);
        parcel.writeString(this.manageSpaceActivityName);
        parcel.writeFloat(this.maxAspectRatio);
        parcel.writeFloat(this.minAspectRatio);
        parcel.writeInt(this.minSdkVersion);
        parcel.writeInt(this.maxSdkVersion);
        parcel.writeInt(this.networkSecurityConfigRes);
        parcel.writeCharSequence(this.nonLocalizedLabel);
        parcel.writeString(this.permission);
        parcel.writeString(this.processName);
        parcel.writeInt(this.requiresSmallestWidthDp);
        parcel.writeInt(this.roundIconRes);
        parcel.writeInt(this.targetSandboxVersion);
        parcel.writeInt(this.targetSdkVersion);
        parcel.writeString(this.taskAffinity);
        parcel.writeInt(this.theme);
        parcel.writeInt(this.uiOptions);
        parcel.writeString(this.zygotePreloadName);
        parcel.writeStringArray(this.splitClassLoaderNames);
        parcel.writeStringArray(this.splitCodePaths);
        parcel.writeSparseArray(this.splitDependencies);
        parcel.writeIntArray(this.splitFlags);
        parcel.writeStringArray(this.splitNames);
        parcel.writeIntArray(this.splitRevisionCodes);
        sForBoolean.parcel(this.resizeableActivity, parcel, i);
        parcel.writeInt(this.autoRevokePermissions);
        sForInternedStringSet.parcel(this.mimeGroups, parcel, i);
        parcel.writeInt(this.gwpAsanMode);
        parcel.writeSparseIntArray(this.minExtensionVersions);
        parcel.writeMap(this.mProperties);
        parcel.writeInt(this.memtagMode);
        parcel.writeInt(this.nativeHeapZeroInitialized);
        sForBoolean.parcel(this.requestRawExternalStorageAccess, parcel, i);
        parcel.writeInt(this.mLocaleConfigRes);
        sForStringSet.parcel(this.mKnownActivityEmbeddingCerts, parcel, i);
        sForInternedString.parcel(this.manifestPackageName, parcel, i);
        parcel.writeString(this.nativeLibraryDir);
        parcel.writeString(this.nativeLibraryRootDir);
        parcel.writeBoolean(this.nativeLibraryRootRequiresIsa);
        sForInternedString.parcel(this.primaryCpuAbi, parcel, i);
        sForInternedString.parcel(this.secondaryCpuAbi, parcel, i);
        parcel.writeString(this.secondaryNativeLibraryDir);
        parcel.writeInt(this.uid);
        parcel.writeLong(this.mBooleans);
        parcel.writeLong(this.mBooleans2);
    }

    public PackageImpl(Parcel parcel) {
        this.usesLibraries = Collections.emptyList();
        this.usesOptionalLibraries = Collections.emptyList();
        this.usesNativeLibraries = Collections.emptyList();
        this.usesOptionalNativeLibraries = Collections.emptyList();
        this.originalPackages = Collections.emptyList();
        this.adoptPermissions = Collections.emptyList();
        this.requestedPermissions = Collections.emptyList();
        this.protectedBroadcasts = Collections.emptyList();
        this.activities = Collections.emptyList();
        this.apexSystemServices = Collections.emptyList();
        this.receivers = Collections.emptyList();
        this.services = Collections.emptyList();
        this.providers = Collections.emptyList();
        this.permissions = Collections.emptyList();
        this.permissionGroups = Collections.emptyList();
        this.instrumentations = Collections.emptyList();
        this.overlayables = Collections.emptyMap();
        this.libraryNames = Collections.emptyList();
        this.usesStaticLibraries = Collections.emptyList();
        this.usesSdkLibraries = Collections.emptyList();
        this.configPreferences = Collections.emptyList();
        this.reqFeatures = Collections.emptyList();
        this.featureGroups = Collections.emptyList();
        this.usesPermissions = Collections.emptyList();
        this.implicitPermissions = Collections.emptyList();
        this.upgradeKeySets = Collections.emptySet();
        this.keySetMapping = Collections.emptyMap();
        this.attributions = Collections.emptyList();
        this.preferredActivityFilters = Collections.emptyList();
        this.processes = Collections.emptyMap();
        this.mProperties = Collections.emptyMap();
        this.signingDetails = SigningDetails.UNKNOWN;
        this.queriesIntents = Collections.emptyList();
        this.queriesPackages = Collections.emptyList();
        this.queriesProviders = Collections.emptySet();
        this.category = -1;
        this.installLocation = -1;
        this.minSdkVersion = 1;
        this.maxSdkVersion = Integer.MAX_VALUE;
        this.targetSdkVersion = 0;
        this.mimeGroups = Collections.emptySet();
        this.mBooleans = 17592186044416L;
        this.mKnownActivityEmbeddingCerts = Collections.emptySet();
        this.uid = -1;
        ClassLoader classLoader = Object.class.getClassLoader();
        this.supportsSmallScreens = sForBoolean.unparcel(parcel);
        this.supportsNormalScreens = sForBoolean.unparcel(parcel);
        this.supportsLargeScreens = sForBoolean.unparcel(parcel);
        this.supportsExtraLargeScreens = sForBoolean.unparcel(parcel);
        this.resizeable = sForBoolean.unparcel(parcel);
        this.anyDensity = sForBoolean.unparcel(parcel);
        this.versionCode = parcel.readInt();
        this.versionCodeMajor = parcel.readInt();
        this.baseRevisionCode = parcel.readInt();
        this.versionName = sForInternedString.unparcel(parcel);
        this.compileSdkVersion = parcel.readInt();
        this.compileSdkVersionCodeName = parcel.readString();
        this.packageName = sForInternedString.unparcel(parcel);
        this.mBaseApkPath = parcel.readString();
        this.restrictedAccountType = parcel.readString();
        this.requiredAccountType = parcel.readString();
        this.overlayTarget = sForInternedString.unparcel(parcel);
        this.overlayTargetOverlayableName = parcel.readString();
        this.overlayCategory = parcel.readString();
        this.overlayPriority = parcel.readInt();
        this.overlayables = sForInternedStringValueMap.unparcel(parcel);
        this.sdkLibraryName = sForInternedString.unparcel(parcel);
        this.sdkLibVersionMajor = parcel.readInt();
        this.staticSharedLibraryName = sForInternedString.unparcel(parcel);
        this.staticSharedLibVersion = parcel.readLong();
        this.libraryNames = sForInternedStringList.unparcel(parcel);
        this.usesLibraries = sForInternedStringList.unparcel(parcel);
        this.usesOptionalLibraries = sForInternedStringList.unparcel(parcel);
        this.usesNativeLibraries = sForInternedStringList.unparcel(parcel);
        this.usesOptionalNativeLibraries = sForInternedStringList.unparcel(parcel);
        this.usesStaticLibraries = sForInternedStringList.unparcel(parcel);
        this.usesStaticLibrariesVersions = parcel.createLongArray();
        int readInt = parcel.readInt();
        if (readInt >= 0) {
            this.usesStaticLibrariesCertDigests = new String[readInt];
            for (int i = 0; i < readInt; i++) {
                this.usesStaticLibrariesCertDigests[i] = sForInternedStringArray.unparcel(parcel);
            }
        }
        this.usesSdkLibraries = sForInternedStringList.unparcel(parcel);
        this.usesSdkLibrariesVersionsMajor = parcel.createLongArray();
        int readInt2 = parcel.readInt();
        if (readInt2 >= 0) {
            this.usesSdkLibrariesCertDigests = new String[readInt2];
            for (int i2 = 0; i2 < readInt2; i2++) {
                this.usesSdkLibrariesCertDigests[i2] = sForInternedStringArray.unparcel(parcel);
            }
        }
        this.sharedUserId = sForInternedString.unparcel(parcel);
        this.sharedUserLabel = parcel.readInt();
        this.configPreferences = parcel.createTypedArrayList(ConfigurationInfo.CREATOR);
        this.reqFeatures = parcel.createTypedArrayList(FeatureInfo.CREATOR);
        this.featureGroups = parcel.createTypedArrayList(FeatureGroupInfo.CREATOR);
        this.restrictUpdateHash = parcel.createByteArray();
        this.originalPackages = parcel.createStringArrayList();
        this.adoptPermissions = sForInternedStringList.unparcel(parcel);
        this.requestedPermissions = sForInternedStringList.unparcel(parcel);
        this.usesPermissions = ParsingUtils.createTypedInterfaceList(parcel, ParsedUsesPermissionImpl.CREATOR);
        this.implicitPermissions = sForInternedStringList.unparcel(parcel);
        this.upgradeKeySets = sForStringSet.unparcel(parcel);
        this.keySetMapping = ParsingPackageUtils.readKeySetMapping(parcel);
        this.protectedBroadcasts = sForInternedStringList.unparcel(parcel);
        Parcelable.Creator<ParsedActivityImpl> creator = ParsedActivityImpl.CREATOR;
        this.activities = ParsingUtils.createTypedInterfaceList(parcel, creator);
        this.apexSystemServices = ParsingUtils.createTypedInterfaceList(parcel, ParsedApexSystemServiceImpl.CREATOR);
        this.receivers = ParsingUtils.createTypedInterfaceList(parcel, creator);
        this.services = ParsingUtils.createTypedInterfaceList(parcel, ParsedServiceImpl.CREATOR);
        this.providers = ParsingUtils.createTypedInterfaceList(parcel, ParsedProviderImpl.CREATOR);
        this.attributions = ParsingUtils.createTypedInterfaceList(parcel, ParsedAttributionImpl.CREATOR);
        this.permissions = ParsingUtils.createTypedInterfaceList(parcel, ParsedPermissionImpl.CREATOR);
        this.permissionGroups = ParsingUtils.createTypedInterfaceList(parcel, ParsedPermissionGroupImpl.CREATOR);
        this.instrumentations = ParsingUtils.createTypedInterfaceList(parcel, ParsedInstrumentationImpl.CREATOR);
        this.preferredActivityFilters = sForIntentInfoPairs.unparcel(parcel);
        this.processes = parcel.readHashMap(ParsedProcess.class.getClassLoader());
        this.metaData = parcel.readBundle(classLoader);
        this.volumeUuid = sForInternedString.unparcel(parcel);
        this.signingDetails = (SigningDetails) parcel.readParcelable(classLoader, SigningDetails.class);
        this.mPath = parcel.readString();
        this.queriesIntents = parcel.createTypedArrayList(Intent.CREATOR);
        this.queriesPackages = sForInternedStringList.unparcel(parcel);
        this.queriesProviders = sForInternedStringSet.unparcel(parcel);
        this.appComponentFactory = parcel.readString();
        this.backupAgentName = parcel.readString();
        this.banner = parcel.readInt();
        this.category = parcel.readInt();
        this.classLoaderName = parcel.readString();
        this.className = parcel.readString();
        this.compatibleWidthLimitDp = parcel.readInt();
        this.descriptionRes = parcel.readInt();
        this.fullBackupContent = parcel.readInt();
        this.dataExtractionRules = parcel.readInt();
        this.iconRes = parcel.readInt();
        this.installLocation = parcel.readInt();
        this.labelRes = parcel.readInt();
        this.largestWidthLimitDp = parcel.readInt();
        this.logo = parcel.readInt();
        this.manageSpaceActivityName = parcel.readString();
        this.maxAspectRatio = parcel.readFloat();
        this.minAspectRatio = parcel.readFloat();
        this.minSdkVersion = parcel.readInt();
        this.maxSdkVersion = parcel.readInt();
        this.networkSecurityConfigRes = parcel.readInt();
        this.nonLocalizedLabel = parcel.readCharSequence();
        this.permission = parcel.readString();
        this.processName = parcel.readString();
        this.requiresSmallestWidthDp = parcel.readInt();
        this.roundIconRes = parcel.readInt();
        this.targetSandboxVersion = parcel.readInt();
        this.targetSdkVersion = parcel.readInt();
        this.taskAffinity = parcel.readString();
        this.theme = parcel.readInt();
        this.uiOptions = parcel.readInt();
        this.zygotePreloadName = parcel.readString();
        this.splitClassLoaderNames = parcel.createStringArray();
        this.splitCodePaths = parcel.createStringArray();
        this.splitDependencies = parcel.readSparseArray(classLoader);
        this.splitFlags = parcel.createIntArray();
        this.splitNames = parcel.createStringArray();
        this.splitRevisionCodes = parcel.createIntArray();
        this.resizeableActivity = sForBoolean.unparcel(parcel);
        this.autoRevokePermissions = parcel.readInt();
        this.mimeGroups = sForInternedStringSet.unparcel(parcel);
        this.gwpAsanMode = parcel.readInt();
        this.minExtensionVersions = parcel.readSparseIntArray();
        this.mProperties = parcel.readHashMap(classLoader);
        this.memtagMode = parcel.readInt();
        this.nativeHeapZeroInitialized = parcel.readInt();
        this.requestRawExternalStorageAccess = sForBoolean.unparcel(parcel);
        this.mLocaleConfigRes = parcel.readInt();
        this.mKnownActivityEmbeddingCerts = sForStringSet.unparcel(parcel);
        this.manifestPackageName = sForInternedString.unparcel(parcel);
        this.nativeLibraryDir = parcel.readString();
        this.nativeLibraryRootDir = parcel.readString();
        this.nativeLibraryRootRequiresIsa = parcel.readBoolean();
        this.primaryCpuAbi = sForInternedString.unparcel(parcel);
        this.secondaryCpuAbi = sForInternedString.unparcel(parcel);
        this.secondaryNativeLibraryDir = parcel.readString();
        this.uid = parcel.readInt();
        this.mBooleans = parcel.readLong();
        this.mBooleans2 = parcel.readLong();
        assignDerivedFields();
        assignDerivedFields2();
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getManifestPackageName() {
        return this.manifestPackageName;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isStub() {
        return getBoolean2(1L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getNativeLibraryDir() {
        return this.nativeLibraryDir;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getNativeLibraryRootDir() {
        return this.nativeLibraryRootDir;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isNativeLibraryRootRequiresIsa() {
        return this.nativeLibraryRootRequiresIsa;
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageHidden
    public String getPrimaryCpuAbi() {
        return this.primaryCpuAbi;
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageHidden
    public String getSecondaryCpuAbi() {
        return this.secondaryCpuAbi;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public String getSecondaryNativeLibraryDir() {
        return this.secondaryNativeLibraryDir;
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isCoreApp() {
        return getBoolean(4503599627370496L);
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageHidden
    public boolean isSystem() {
        return getBoolean(9007199254740992L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isFactoryTest() {
        return getBoolean(18014398509481984L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isApex() {
        return getBoolean2(2L);
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageHidden
    public boolean isSystemExt() {
        return getBoolean(72057594037927936L);
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageHidden
    public boolean isPrivileged() {
        return getBoolean(144115188075855872L);
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageHidden
    public boolean isOem() {
        return getBoolean(288230376151711744L);
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageHidden
    public boolean isVendor() {
        return getBoolean(576460752303423488L);
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageHidden
    public boolean isProduct() {
        return getBoolean(1152921504606846976L);
    }

    @Override // com.android.server.p011pm.parsing.pkg.AndroidPackageHidden
    public boolean isOdm() {
        return getBoolean(2305843009213693952L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public boolean isSignedWithPlatformKey() {
        return getBoolean(4611686018427387904L);
    }

    @Override // com.android.server.p011pm.pkg.AndroidPackage
    public int getUid() {
        return this.uid;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setStub(boolean z) {
        setBoolean2(1L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setNativeLibraryRootRequiresIsa(boolean z) {
        this.nativeLibraryRootRequiresIsa = z;
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setSystem(boolean z) {
        setBoolean(9007199254740992L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setFactoryTest(boolean z) {
        setBoolean(18014398509481984L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setApex(boolean z) {
        setBoolean2(2L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setSystemExt(boolean z) {
        setBoolean(72057594037927936L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setPrivileged(boolean z) {
        setBoolean(144115188075855872L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setOem(boolean z) {
        setBoolean(288230376151711744L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setVendor(boolean z) {
        setBoolean(576460752303423488L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setProduct(boolean z) {
        setBoolean(1152921504606846976L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setOdm(boolean z) {
        setBoolean(2305843009213693952L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setSignedWithPlatformKey(boolean z) {
        setBoolean(4611686018427387904L, z);
        return this;
    }

    @Override // com.android.server.p011pm.parsing.pkg.ParsedPackage
    public PackageImpl setUid(int i) {
        this.uid = i;
        return this;
    }

    public String getBaseAppDataCredentialProtectedDirForSystemUser() {
        return this.mBaseAppDataCredentialProtectedDirForSystemUser;
    }

    public String getBaseAppDataDeviceProtectedDirForSystemUser() {
        return this.mBaseAppDataDeviceProtectedDirForSystemUser;
    }
}
