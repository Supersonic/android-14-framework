package com.android.internal.content.p014om;

import android.content.p001pm.parsing.ApkLite;
import android.content.p001pm.parsing.ApkLiteParseUtils;
import android.content.p001pm.parsing.FrameworkParsingPackageUtils;
import android.content.p001pm.parsing.result.ParseResult;
import android.content.p001pm.parsing.result.ParseTypeImpl;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import com.android.internal.content.p014om.OverlayConfigParser;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/* renamed from: com.android.internal.content.om.OverlayScanner */
/* loaded from: classes4.dex */
public class OverlayScanner {
    private final ArrayMap<String, ParsedOverlayInfo> mParsedOverlayInfos = new ArrayMap<>();
    private final List<Pair<String, File>> mExcludedOverlayPackages = new ArrayList();

    /* renamed from: com.android.internal.content.om.OverlayScanner$ParsedOverlayInfo */
    /* loaded from: classes4.dex */
    public static class ParsedOverlayInfo {
        public final boolean isStatic;
        public final String packageName;
        public final File path;
        public final File preInstalledApexPath;
        public final int priority;
        public final String targetPackageName;
        public final int targetSdkVersion;

        public ParsedOverlayInfo(String packageName, String targetPackageName, int targetSdkVersion, boolean isStatic, int priority, File path, File preInstalledApexPath) {
            this.packageName = packageName;
            this.targetPackageName = targetPackageName;
            this.targetSdkVersion = targetSdkVersion;
            this.isStatic = isStatic;
            this.priority = priority;
            this.path = path;
            this.preInstalledApexPath = preInstalledApexPath;
        }

        public String toString() {
            return getClass().getSimpleName() + String.format("{packageName=%s, targetPackageName=%s, targetSdkVersion=%s, isStatic=%s, priority=%s, path=%s, preInstalledApexPath=%s}", this.packageName, this.targetPackageName, Integer.valueOf(this.targetSdkVersion), Boolean.valueOf(this.isStatic), Integer.valueOf(this.priority), this.path, this.preInstalledApexPath);
        }

        public File getOriginalPartitionPath() {
            File file = this.preInstalledApexPath;
            return file != null ? file : this.path;
        }
    }

    public final ParsedOverlayInfo getParsedInfo(String packageName) {
        return this.mParsedOverlayInfos.get(packageName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Collection<ParsedOverlayInfo> getAllParsedInfos() {
        return this.mParsedOverlayInfos.values();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isExcludedOverlayPackage(String packageName, OverlayConfigParser.OverlayPartition overlayPartition) {
        for (int i = 0; i < this.mExcludedOverlayPackages.size(); i++) {
            Pair<String, File> pair = this.mExcludedOverlayPackages.get(i);
            if (pair.first.equals(packageName) && overlayPartition.containsOverlay(pair.second)) {
                return true;
            }
        }
        return false;
    }

    public void scanDir(File partitionOverlayDir) {
        ParsedOverlayInfo info;
        if (!partitionOverlayDir.exists() || !partitionOverlayDir.isDirectory()) {
            return;
        }
        if (!partitionOverlayDir.canRead()) {
            Log.m104w("OverlayConfig", "Directory " + partitionOverlayDir + " cannot be read");
            return;
        }
        File[] files = partitionOverlayDir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                scanDir(f);
            }
            if (f.isFile() && f.getPath().endsWith(".apk") && (info = parseOverlayManifest(f, this.mExcludedOverlayPackages)) != null) {
                this.mParsedOverlayInfos.put(info.packageName, info);
            }
        }
    }

    public ParsedOverlayInfo parseOverlayManifest(File overlayApk, List<Pair<String, File>> outExcludedOverlayPackages) {
        ParseTypeImpl input = ParseTypeImpl.forParsingWithoutPlatformCompat();
        ParseResult<ApkLite> ret = ApkLiteParseUtils.parseApkLite(input.reset(), overlayApk, 128);
        if (ret.isError()) {
            Log.m103w("OverlayConfig", "Got exception loading overlay.", ret.getException());
            return null;
        }
        ApkLite apkLite = ret.getResult();
        if (apkLite.getTargetPackageName() == null) {
            return null;
        }
        String propName = apkLite.getRequiredSystemPropertyName();
        String propValue = apkLite.getRequiredSystemPropertyValue();
        if ((!TextUtils.isEmpty(propName) || !TextUtils.isEmpty(propValue)) && !FrameworkParsingPackageUtils.checkRequiredSystemProperties(propName, propValue)) {
            outExcludedOverlayPackages.add(Pair.create(apkLite.getPackageName(), overlayApk));
            return null;
        }
        return new ParsedOverlayInfo(apkLite.getPackageName(), apkLite.getTargetPackageName(), apkLite.getTargetSdkVersion(), apkLite.isOverlayIsStatic(), apkLite.getOverlayPriority(), new File(apkLite.getPath()), null);
    }
}
