package android.content.p001pm.parsing;

import android.Manifest;
import android.app.admin.DeviceAdminReceiver;
import android.content.p001pm.PackageManager;
import android.content.p001pm.SigningDetails;
import android.content.p001pm.VerifierInfo;
import android.content.p001pm.parsing.result.ParseInput;
import android.content.p001pm.parsing.result.ParseResult;
import android.content.res.ApkAssets;
import android.content.res.XmlResourceParser;
import android.p008os.Build;
import android.p008os.Trace;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: android.content.pm.parsing.ApkLiteParseUtils */
/* loaded from: classes.dex */
public class ApkLiteParseUtils {
    public static final String ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml";
    private static final String ANDROID_RES_NAMESPACE = "http://schemas.android.com/apk/res/android";
    public static final String APK_FILE_EXTENSION = ".apk";
    private static final int DEFAULT_MIN_SDK_VERSION = 1;
    private static final int DEFAULT_TARGET_SDK_VERSION = 0;
    private static final int PARSE_COLLECT_CERTIFICATES = 32;
    private static final int PARSE_DEFAULT_INSTALL_LOCATION = -1;
    private static final int PARSE_IS_SYSTEM_DIR = 16;
    private static final String TAG = "ApkLiteParseUtils";
    private static final String TAG_APPLICATION = "application";
    private static final String TAG_MANIFEST = "manifest";
    private static final String TAG_OVERLAY = "overlay";
    private static final String TAG_PACKAGE_VERIFIER = "package-verifier";
    private static final String TAG_PROFILEABLE = "profileable";
    private static final String TAG_RECEIVER = "receiver";
    private static final String TAG_SDK_LIBRARY = "sdk-library";
    private static final String TAG_USES_SDK = "uses-sdk";
    private static final String TAG_USES_SPLIT = "uses-split";
    private static final Comparator<String> sSplitNameComparator = new SplitNameComparator();
    private static final int SDK_VERSION = Build.VERSION.SDK_INT;
    private static final String[] SDK_CODENAMES = Build.VERSION.ACTIVE_CODENAMES;

    public static ParseResult<PackageLite> parsePackageLite(ParseInput input, File packageFile, int flags) {
        if (packageFile.isDirectory()) {
            return parseClusterPackageLite(input, packageFile, flags);
        }
        return parseMonolithicPackageLite(input, packageFile, flags);
    }

    public static ParseResult<PackageLite> parseMonolithicPackageLite(ParseInput input, File packageFile, int flags) {
        Trace.traceBegin(262144L, "parseApkLite");
        try {
            ParseResult<ApkLite> result = parseApkLite(input, packageFile, flags);
            if (result.isError()) {
                return input.error(result);
            }
            ApkLite baseApk = result.getResult();
            String packagePath = packageFile.getAbsolutePath();
            return input.success(new PackageLite(packagePath, baseApk.getPath(), baseApk, null, null, null, null, null, null, baseApk.getTargetSdkVersion(), null, null));
        } finally {
            Trace.traceEnd(262144L);
        }
    }

    public static ParseResult<PackageLite> parseClusterPackageLite(ParseInput input, File packageDir, int flags) {
        File[] files = packageDir.listFiles();
        if (ArrayUtils.isEmpty(files)) {
            return input.error(-100, "No packages found in split");
        }
        int i = 0;
        if (files.length == 1 && files[0].isDirectory()) {
            return parseClusterPackageLite(input, files[0], flags);
        }
        String packageName = null;
        int versionCode = 0;
        ArrayMap<String, ApkLite> apks = new ArrayMap<>();
        long j = 262144;
        Trace.traceBegin(262144L, "parseApkLite");
        try {
            int length = files.length;
            while (i < length) {
                File file = files[i];
                if (isApkFile(file)) {
                    ParseResult<ApkLite> result = parseApkLite(input, file, flags);
                    if (result.isError()) {
                        ParseResult<PackageLite> error = input.error(result);
                        Trace.traceEnd(j);
                        return error;
                    }
                    ApkLite lite = result.getResult();
                    if (packageName == null) {
                        packageName = lite.getPackageName();
                        versionCode = lite.getVersionCode();
                    } else if (!packageName.equals(lite.getPackageName())) {
                        return input.error(-101, "Inconsistent package " + lite.getPackageName() + " in " + file + "; expected " + packageName);
                    } else {
                        if (versionCode != lite.getVersionCode()) {
                            return input.error(-101, "Inconsistent version " + lite.getVersionCode() + " in " + file + "; expected " + versionCode);
                        }
                    }
                    if (apks.put(lite.getSplitName(), lite) != null) {
                        return input.error(-101, "Split name " + lite.getSplitName() + " defined more than once; most recent was " + file);
                    }
                }
                i++;
                j = 262144;
            }
            ApkLite baseApk = apks.remove(null);
            Trace.traceEnd(262144L);
            return composePackageLiteFromApks(input, packageDir, baseApk, apks);
        } finally {
            Trace.traceEnd(262144L);
        }
    }

    public static ParseResult<PackageLite> composePackageLiteFromApks(ParseInput input, File packageDir, ApkLite baseApk, ArrayMap<String, ApkLite> splitApks) {
        return composePackageLiteFromApks(input, packageDir, baseApk, splitApks, false);
    }

    public static ParseResult<PackageLite> composePackageLiteFromApks(ParseInput input, File packageDir, ApkLite baseApk, ArrayMap<String, ApkLite> splitApks, boolean apkRenamed) {
        String[] splitNames;
        Set<String>[] requiredSplitTypes;
        Set<String>[] splitTypes;
        boolean[] isFeatureSplits;
        String[] usesSplitNames;
        String[] configForSplits;
        String[] splitCodePaths;
        int[] splitRevisionCodes;
        if (baseApk == null) {
            return input.error(-101, "Missing base APK in " + packageDir);
        }
        int size = ArrayUtils.size(splitApks);
        if (size <= 0) {
            splitNames = null;
            requiredSplitTypes = null;
            splitTypes = null;
            isFeatureSplits = null;
            usesSplitNames = null;
            configForSplits = null;
            splitCodePaths = null;
            splitRevisionCodes = null;
        } else {
            String[] splitNames2 = new String[size];
            Set<String>[] requiredSplitTypes2 = new Set[size];
            Set<String>[] splitTypes2 = new Set[size];
            boolean[] isFeatureSplits2 = new boolean[size];
            String[] usesSplitNames2 = new String[size];
            String[] configForSplits2 = new String[size];
            String[] splitCodePaths2 = new String[size];
            int[] splitRevisionCodes2 = new int[size];
            String[] splitNames3 = (String[]) splitApks.keySet().toArray(splitNames2);
            Arrays.sort(splitNames3, sSplitNameComparator);
            for (int i = 0; i < size; i++) {
                ApkLite apk = splitApks.get(splitNames3[i]);
                requiredSplitTypes2[i] = apk.getRequiredSplitTypes();
                splitTypes2[i] = apk.getSplitTypes();
                usesSplitNames2[i] = apk.getUsesSplitName();
                isFeatureSplits2[i] = apk.isFeatureSplit();
                configForSplits2[i] = apk.getConfigForSplit();
                splitCodePaths2[i] = apkRenamed ? new File(packageDir, splitNameToFileName(apk)).getAbsolutePath() : apk.getPath();
                splitRevisionCodes2[i] = apk.getRevisionCode();
            }
            splitNames = splitNames3;
            requiredSplitTypes = requiredSplitTypes2;
            splitTypes = splitTypes2;
            isFeatureSplits = isFeatureSplits2;
            usesSplitNames = usesSplitNames2;
            configForSplits = configForSplits2;
            splitCodePaths = splitCodePaths2;
            splitRevisionCodes = splitRevisionCodes2;
        }
        String codePath = packageDir.getAbsolutePath();
        String baseCodePath = apkRenamed ? new File(packageDir, splitNameToFileName(baseApk)).getAbsolutePath() : baseApk.getPath();
        return input.success(new PackageLite(codePath, baseCodePath, baseApk, splitNames, isFeatureSplits, usesSplitNames, configForSplits, splitCodePaths, splitRevisionCodes, baseApk.getTargetSdkVersion(), requiredSplitTypes, splitTypes));
    }

    public static String splitNameToFileName(ApkLite apk) {
        Objects.requireNonNull(apk);
        String fileName = apk.getSplitName() == null ? "base" : "split_" + apk.getSplitName();
        return fileName + ".apk";
    }

    public static ParseResult<ApkLite> parseApkLite(ParseInput input, File apkFile, int flags) {
        return parseApkLiteInner(input, apkFile, null, null, flags);
    }

    public static ParseResult<ApkLite> parseApkLite(ParseInput input, FileDescriptor fd, String debugPathName, int flags) {
        return parseApkLiteInner(input, null, fd, debugPathName, flags);
    }

    private static ParseResult<ApkLite> parseApkLiteInner(ParseInput input, File apkFile, FileDescriptor fd, String debugPathName, int flags) {
        Exception e;
        SigningDetails signingDetails;
        long j;
        String apkPath = fd != null ? debugPathName : apkFile.getAbsolutePath();
        XmlResourceParser parser = null;
        ApkAssets apkAssets = null;
        try {
            try {
                try {
                    ApkAssets apkAssets2 = fd != null ? ApkAssets.loadFromFd(fd, debugPathName, 0, null) : ApkAssets.loadFromPath(apkPath);
                    try {
                        XmlResourceParser parser2 = apkAssets2.openXml("AndroidManifest.xml");
                        try {
                            if ((flags & 32) != 0) {
                                boolean skipVerify = (flags & 16) != 0;
                                Trace.traceBegin(262144L, "collectCertificates");
                                try {
                                    j = 262144;
                                    try {
                                        ParseResult<SigningDetails> result = FrameworkParsingPackageUtils.getSigningDetails(input, apkFile.getAbsolutePath(), skipVerify, false, SigningDetails.UNKNOWN, 0);
                                        if (result.isError()) {
                                            ParseResult<ApkLite> error = input.error(result);
                                            Trace.traceEnd(262144L);
                                            IoUtils.closeQuietly(parser2);
                                            if (apkAssets2 != null) {
                                                try {
                                                    apkAssets2.close();
                                                } catch (Throwable th) {
                                                }
                                            }
                                            return error;
                                        }
                                        SigningDetails signingDetails2 = result.getResult();
                                        Trace.traceEnd(262144L);
                                        signingDetails = signingDetails2;
                                    } catch (Throwable th2) {
                                        th = th2;
                                        Trace.traceEnd(j);
                                        throw th;
                                    }
                                } catch (Throwable th3) {
                                    th = th3;
                                    j = 262144;
                                }
                            } else {
                                signingDetails = SigningDetails.UNKNOWN;
                            }
                            ParseResult<ApkLite> parseApkLite = parseApkLite(input, apkPath, parser2, signingDetails, flags);
                            IoUtils.closeQuietly(parser2);
                            if (apkAssets2 != null) {
                                try {
                                    apkAssets2.close();
                                } catch (Throwable th4) {
                                }
                            }
                            return parseApkLite;
                        } catch (IOException | RuntimeException | XmlPullParserException e2) {
                            e = e2;
                            apkAssets = apkAssets2;
                            parser = parser2;
                            Exception e3 = e;
                            Slog.m89w(TAG, "Failed to parse " + apkPath, e3);
                            ParseResult<ApkLite> error2 = input.error(-102, "Failed to parse " + apkPath, e3);
                            IoUtils.closeQuietly(parser);
                            if (apkAssets != null) {
                                try {
                                    apkAssets.close();
                                } catch (Throwable th5) {
                                }
                            }
                            return error2;
                        } catch (Throwable th6) {
                            e = th6;
                            apkAssets = apkAssets2;
                            parser = parser2;
                            IoUtils.closeQuietly(parser);
                            if (apkAssets != null) {
                                try {
                                    apkAssets.close();
                                } catch (Throwable th7) {
                                }
                            }
                            throw e;
                        }
                    } catch (IOException | RuntimeException | XmlPullParserException e4) {
                        e = e4;
                        apkAssets = apkAssets2;
                    } catch (Throwable th8) {
                        e = th8;
                        apkAssets = apkAssets2;
                    }
                } catch (Throwable th9) {
                    e = th9;
                }
            } catch (IOException | RuntimeException | XmlPullParserException e5) {
                e = e5;
            }
        } catch (IOException e6) {
            ParseResult<ApkLite> error3 = input.error(-100, "Failed to parse " + apkPath, e6);
            IoUtils.closeQuietly((AutoCloseable) null);
            if (0 != 0) {
                try {
                    apkAssets.close();
                } catch (Throwable th10) {
                }
            }
            return error3;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:111:0x0357, code lost:
        if ((r75 & 128) != 0) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x035d, code lost:
        if (android.content.p001pm.parsing.FrameworkParsingPackageUtils.checkRequiredSystemProperties(r14, r11) != false) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:114:0x035f, code lost:
        r0 = "Skipping target and overlay pair " + r15 + " and " + r72 + ": overlay ignored due to required system property: " + r14 + " with value: " + r11;
        android.util.Slog.m94i(android.content.p001pm.parsing.ApkLiteParseUtils.TAG, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x0399, code lost:
        return r71.skip(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:118:0x039f, code lost:
        r12 = r5.first;
        r6 = r5.second;
     */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x0401, code lost:
        return r71.success(new android.content.p001pm.parsing.ApkLite(r72, r12, r6, r49, r51, r62, r50, r44, r45, r46, r8, r21, r74, r47, r56, r57, r58, r59, r61, r60, r48, r15, r63, r64, r14, r11, r55, r54, r65, r7.first, r7.second, r66, r67));
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static ParseResult<ApkLite> parseApkLite(ParseInput input, String codePath, XmlResourceParser parser, SigningDetails signingDetails, int flags) throws IOException, XmlPullParserException {
        List<VerifierInfo> verifiers;
        ParseResult<Pair<Set<String>, Set<String>>> requiredSplitTypesResult;
        String str;
        List<VerifierInfo> verifiers2;
        int i;
        int searchDepth;
        List<VerifierInfo> verifiers3;
        int i2;
        String minCode;
        int targetVer;
        String targetCode;
        boolean allowUnknownCodenames;
        ParseResult<Pair<Set<String>, Set<String>>> requiredSplitTypesResult2;
        List<VerifierInfo> verifiers4;
        XmlResourceParser xmlResourceParser = parser;
        ParseResult<Pair<String, String>> result = parsePackageSplitNames(input, xmlResourceParser);
        if (result.isError()) {
            return input.error(result);
        }
        Pair<String, String> packageSplit = result.getResult();
        ParseResult<Pair<Set<String>, Set<String>>> requiredSplitTypesResult3 = parseRequiredSplitTypes(input, xmlResourceParser);
        if (requiredSplitTypesResult3.isError()) {
            return input.error(result);
        }
        Pair<Set<String>, Set<String>> requiredSplitTypes = requiredSplitTypesResult3.getResult();
        String str2 = "http://schemas.android.com/apk/res/android";
        int installLocation = xmlResourceParser.getAttributeIntValue("http://schemas.android.com/apk/res/android", "installLocation", -1);
        int versionCode = xmlResourceParser.getAttributeIntValue("http://schemas.android.com/apk/res/android", "versionCode", 0);
        int versionCodeMajor = xmlResourceParser.getAttributeIntValue("http://schemas.android.com/apk/res/android", "versionCodeMajor", 0);
        int revisionCode = xmlResourceParser.getAttributeIntValue("http://schemas.android.com/apk/res/android", "revisionCode", 0);
        boolean coreApp = xmlResourceParser.getAttributeBooleanValue(null, "coreApp", false);
        boolean isolatedSplits = xmlResourceParser.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "isolatedSplits", false);
        boolean isFeatureSplit = xmlResourceParser.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "isFeatureSplit", false);
        boolean isSplitRequired = xmlResourceParser.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "isSplitRequired", false);
        String configForSplit = xmlResourceParser.getAttributeValue(null, "configForSplit");
        int i3 = 1;
        int searchDepth2 = parser.getDepth() + 1;
        List<VerifierInfo> verifiers5 = new ArrayList<>();
        int targetSdkVersion = 0;
        int minSdkVersion = 1;
        boolean debuggable = false;
        boolean profilableByShell = false;
        boolean multiArch = false;
        boolean use32bitAbi = false;
        boolean extractNativeLibs = true;
        boolean useEmbeddedDex = false;
        String usesSplitName = null;
        String targetPackage = null;
        boolean overlayIsStatic = false;
        int overlayPriority = 0;
        int rollbackDataPolicy = 0;
        String requiredSystemPropertyName = null;
        String requiredSystemPropertyValue = null;
        boolean hasDeviceAdminReceiver = false;
        boolean isSdkLibrary = false;
        while (true) {
            int type = parser.next();
            if (type == i3) {
                verifiers = verifiers5;
                break;
            } else if (type == 3 && parser.getDepth() < searchDepth2) {
                verifiers = verifiers5;
                break;
            } else {
                if (type == 3) {
                    requiredSplitTypesResult = requiredSplitTypesResult3;
                    str = str2;
                    verifiers2 = verifiers5;
                    i = 1;
                    searchDepth = searchDepth2;
                } else if (type == 4) {
                    requiredSplitTypesResult = requiredSplitTypesResult3;
                    str = str2;
                    verifiers2 = verifiers5;
                    i = 1;
                    searchDepth = searchDepth2;
                } else if (parser.getDepth() != searchDepth2) {
                    requiredSplitTypesResult = requiredSplitTypesResult3;
                    str = str2;
                    verifiers2 = verifiers5;
                    i = 1;
                    searchDepth = searchDepth2;
                } else if ("package-verifier".equals(parser.getName())) {
                    VerifierInfo verifier = parseVerifier(parser);
                    if (verifier == null) {
                        verifiers3 = verifiers5;
                    } else {
                        verifiers3 = verifiers5;
                        verifiers3.add(verifier);
                    }
                    searchDepth = searchDepth2;
                    requiredSplitTypesResult = requiredSplitTypesResult3;
                    str = str2;
                    verifiers2 = verifiers3;
                    i = 1;
                } else {
                    List<VerifierInfo> verifiers6 = verifiers5;
                    searchDepth = searchDepth2;
                    if ("application".equals(parser.getName())) {
                        debuggable = xmlResourceParser.getAttributeBooleanValue(str2, "debuggable", false);
                        multiArch = xmlResourceParser.getAttributeBooleanValue(str2, "multiArch", false);
                        use32bitAbi = xmlResourceParser.getAttributeBooleanValue(str2, "use32bitAbi", false);
                        extractNativeLibs = xmlResourceParser.getAttributeBooleanValue(str2, "extractNativeLibs", true);
                        useEmbeddedDex = xmlResourceParser.getAttributeBooleanValue(str2, "useEmbeddedDex", false);
                        rollbackDataPolicy = xmlResourceParser.getAttributeIntValue(str2, "rollbackDataPolicy", 0);
                        String permission = xmlResourceParser.getAttributeValue(str2, "permission");
                        boolean hasBindDeviceAdminPermission = Manifest.C0000permission.BIND_DEVICE_ADMIN.equals(permission);
                        int innerDepth = parser.getDepth();
                        boolean profilableByShell2 = profilableByShell;
                        while (true) {
                            requiredSplitTypesResult2 = requiredSplitTypesResult3;
                            int innerType = parser.next();
                            verifiers4 = verifiers6;
                            if (innerType == 1 || (innerType == 3 && parser.getDepth() <= innerDepth)) {
                                break;
                            }
                            if (innerType != 3 && innerType != 4 && parser.getDepth() == innerDepth + 1) {
                                if ("profileable".equals(parser.getName())) {
                                    profilableByShell2 = xmlResourceParser.getAttributeBooleanValue(str2, "shell", profilableByShell2);
                                    verifiers6 = verifiers4;
                                    requiredSplitTypesResult3 = requiredSplitTypesResult2;
                                } else if ("receiver".equals(parser.getName())) {
                                    hasDeviceAdminReceiver |= isDeviceAdminReceiver(xmlResourceParser, hasBindDeviceAdminPermission);
                                    verifiers6 = verifiers4;
                                    requiredSplitTypesResult3 = requiredSplitTypesResult2;
                                } else if (TAG_SDK_LIBRARY.equals(parser.getName())) {
                                    isSdkLibrary = true;
                                    verifiers6 = verifiers4;
                                    requiredSplitTypesResult3 = requiredSplitTypesResult2;
                                }
                            }
                            verifiers6 = verifiers4;
                            requiredSplitTypesResult3 = requiredSplitTypesResult2;
                        }
                        profilableByShell = profilableByShell2;
                        searchDepth2 = searchDepth;
                        requiredSplitTypesResult3 = requiredSplitTypesResult2;
                        i3 = 1;
                        verifiers5 = verifiers4;
                    } else {
                        requiredSplitTypesResult = requiredSplitTypesResult3;
                        verifiers2 = verifiers6;
                        if ("overlay".equals(parser.getName())) {
                            requiredSystemPropertyName = xmlResourceParser.getAttributeValue(str2, "requiredSystemPropertyName");
                            requiredSystemPropertyValue = xmlResourceParser.getAttributeValue(str2, "requiredSystemPropertyValue");
                            targetPackage = xmlResourceParser.getAttributeValue(str2, "targetPackage");
                            overlayIsStatic = xmlResourceParser.getAttributeBooleanValue(str2, "isStatic", false);
                            overlayPriority = xmlResourceParser.getAttributeIntValue(str2, "priority", 0);
                            searchDepth2 = searchDepth;
                            requiredSplitTypesResult3 = requiredSplitTypesResult;
                            i3 = 1;
                            verifiers5 = verifiers2;
                        } else if ("uses-split".equals(parser.getName())) {
                            if (usesSplitName != null) {
                                Slog.m90w(TAG, "Only one <uses-split> permitted. Ignoring others.");
                                str = str2;
                                i = 1;
                            } else {
                                usesSplitName = xmlResourceParser.getAttributeValue(str2, "name");
                                if (usesSplitName == null) {
                                    return input.error(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, "<uses-split> tag requires 'android:name' attribute");
                                }
                                searchDepth2 = searchDepth;
                                requiredSplitTypesResult3 = requiredSplitTypesResult;
                                i3 = 1;
                                verifiers5 = verifiers2;
                            }
                        } else if (!"uses-sdk".equals(parser.getName())) {
                            str = str2;
                            i = 1;
                        } else {
                            String minSdkVersionString = xmlResourceParser.getAttributeValue(str2, "minSdkVersion");
                            String targetSdkVersionString = xmlResourceParser.getAttributeValue(str2, "targetSdkVersion");
                            int minVer = 1;
                            String minCode2 = null;
                            boolean minAssigned = false;
                            int targetVer2 = 0;
                            String targetCode2 = null;
                            if (TextUtils.isEmpty(minSdkVersionString)) {
                                i2 = 1;
                            } else {
                                try {
                                    minVer = Integer.parseInt(minSdkVersionString);
                                    minAssigned = true;
                                    i2 = 1;
                                } catch (NumberFormatException e) {
                                    minCode2 = minSdkVersionString;
                                    i2 = 1;
                                    minAssigned = !TextUtils.isEmpty(minCode2);
                                }
                            }
                            if (!TextUtils.isEmpty(targetSdkVersionString)) {
                                try {
                                    targetVer2 = Integer.parseInt(targetSdkVersionString);
                                } catch (NumberFormatException e2) {
                                    targetCode2 = targetSdkVersionString;
                                    if (!minAssigned) {
                                        minCode2 = targetCode2;
                                    }
                                }
                                minCode = minCode2;
                                targetVer = targetVer2;
                                targetCode = targetCode2;
                            } else {
                                String targetCode3 = minCode2;
                                minCode = minCode2;
                                targetVer = minVer;
                                targetCode = targetCode3;
                            }
                            if ((flags & 512) == 0) {
                                allowUnknownCodenames = false;
                            } else {
                                allowUnknownCodenames = true;
                            }
                            String str3 = str2;
                            String[] strArr = SDK_CODENAMES;
                            ParseResult<Integer> targetResult = FrameworkParsingPackageUtils.computeTargetSdkVersion(targetVer, targetCode, strArr, input, allowUnknownCodenames);
                            if (targetResult.isError()) {
                                return input.error(targetResult);
                            }
                            targetSdkVersion = targetResult.getResult().intValue();
                            ParseResult<Integer> minResult = FrameworkParsingPackageUtils.computeMinSdkVersion(minVer, minCode, SDK_VERSION, strArr, input);
                            if (minResult.isError()) {
                                return input.error(minResult);
                            }
                            minSdkVersion = minResult.getResult().intValue();
                            xmlResourceParser = parser;
                            i3 = i2;
                            str2 = str3;
                            searchDepth2 = searchDepth;
                            requiredSplitTypesResult3 = requiredSplitTypesResult;
                            verifiers5 = verifiers2;
                        }
                    }
                }
                xmlResourceParser = parser;
                i3 = i;
                str2 = str;
                searchDepth2 = searchDepth;
                requiredSplitTypesResult3 = requiredSplitTypesResult;
                verifiers5 = verifiers2;
            }
        }
    }

    private static boolean isDeviceAdminReceiver(XmlResourceParser parser, boolean applicationHasBindDeviceAdminPermission) throws XmlPullParserException, IOException {
        String permission = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "permission");
        if (!applicationHasBindDeviceAdminPermission && !Manifest.C0000permission.BIND_DEVICE_ADMIN.equals(permission)) {
            return false;
        }
        boolean hasDeviceAdminReceiver = false;
        int depth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= depth)) {
                break;
            } else if (type != 3 && type != 4 && parser.getDepth() == depth + 1 && !hasDeviceAdminReceiver && "meta-data".equals(parser.getName())) {
                String name = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "name");
                if (DeviceAdminReceiver.DEVICE_ADMIN_META_DATA.equals(name)) {
                    hasDeviceAdminReceiver = true;
                }
            }
        }
        return hasDeviceAdminReceiver;
    }

    public static ParseResult<Pair<String, String>> parsePackageSplitNames(ParseInput input, XmlResourceParser parser) throws IOException, XmlPullParserException {
        int type;
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            return input.error(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, "No start tag found");
        }
        if (!parser.getName().equals("manifest")) {
            return input.error(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, "No <manifest> tag");
        }
        String packageName = parser.getAttributeValue(null, "package");
        if (!"android".equals(packageName)) {
            ParseResult<?> nameResult = FrameworkParsingPackageUtils.validateName(input, packageName, true, true);
            if (nameResult.isError()) {
                return input.error(PackageManager.INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME, "Invalid manifest package: " + nameResult.getErrorMessage());
            }
        }
        String splitName = parser.getAttributeValue(null, "split");
        if (splitName != null) {
            if (splitName.length() == 0) {
                splitName = null;
            } else {
                ParseResult<?> nameResult2 = FrameworkParsingPackageUtils.validateName(input, splitName, false, false);
                if (nameResult2.isError()) {
                    return input.error(PackageManager.INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME, "Invalid manifest split: " + nameResult2.getErrorMessage());
                }
            }
        }
        return input.success(Pair.create(packageName.intern(), splitName != null ? splitName.intern() : splitName));
    }

    public static ParseResult<Pair<Set<String>, Set<String>>> parseRequiredSplitTypes(ParseInput input, XmlResourceParser parser) {
        Set<String> requiredSplitTypes = null;
        Set<String> splitTypes = null;
        String value = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "requiredSplitTypes");
        if (!TextUtils.isEmpty(value)) {
            ParseResult<Set<String>> result = separateAndValidateSplitTypes(input, value);
            if (result.isError()) {
                return input.error(result);
            }
            Set<String> requiredSplitTypes2 = result.getResult();
            requiredSplitTypes = requiredSplitTypes2;
        }
        String value2 = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "splitTypes");
        if (!TextUtils.isEmpty(value2)) {
            ParseResult<Set<String>> result2 = separateAndValidateSplitTypes(input, value2);
            if (result2.isError()) {
                return input.error(result2);
            }
            Set<String> splitTypes2 = result2.getResult();
            splitTypes = splitTypes2;
        }
        return input.success(Pair.create(requiredSplitTypes, splitTypes));
    }

    private static ParseResult<Set<String>> separateAndValidateSplitTypes(ParseInput input, String values) {
        String[] split;
        ArraySet arraySet = new ArraySet();
        for (String value : values.trim().split(",")) {
            String type = value.trim();
            ParseResult<?> nameResult = FrameworkParsingPackageUtils.validateName(input, type, false, true);
            if (nameResult.isError()) {
                return input.error(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, "Invalid manifest split types: " + nameResult.getErrorMessage());
            }
            if (!arraySet.add(type)) {
                Slog.m90w(TAG, type + " was defined multiple times");
            }
        }
        return input.success(arraySet);
    }

    public static VerifierInfo parseVerifier(AttributeSet attrs) {
        String packageName = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "name");
        String encodedPublicKey = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "publicKey");
        if (packageName == null || packageName.length() == 0) {
            Slog.m94i(TAG, "verifier package name was null; skipping");
            return null;
        }
        PublicKey publicKey = FrameworkParsingPackageUtils.parsePublicKey(encodedPublicKey);
        if (publicKey == null) {
            Slog.m94i(TAG, "Unable to parse verifier public key for " + packageName);
            return null;
        }
        return new VerifierInfo(packageName, publicKey);
    }

    /* renamed from: android.content.pm.parsing.ApkLiteParseUtils$SplitNameComparator */
    /* loaded from: classes.dex */
    private static class SplitNameComparator implements Comparator<String> {
        private SplitNameComparator() {
        }

        @Override // java.util.Comparator
        public int compare(String lhs, String rhs) {
            if (lhs == null) {
                return -1;
            }
            if (rhs == null) {
                return 1;
            }
            return lhs.compareTo(rhs);
        }
    }

    public static boolean isApkFile(File file) {
        return isApkPath(file.getName());
    }

    public static boolean isApkPath(String path) {
        return path.endsWith(".apk");
    }
}
