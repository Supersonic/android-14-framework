package com.android.server.p011pm.parsing;

import android.app.ActivityThread;
import android.content.pm.ApplicationInfo;
import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.permission.PermissionManager;
import android.util.DisplayMetrics;
import android.util.Slog;
import com.android.internal.compat.IPlatformCompat;
import com.android.internal.util.ArrayUtils;
import com.android.server.p011pm.PackageManagerException;
import com.android.server.p011pm.parsing.pkg.PackageImpl;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import com.android.server.p011pm.pkg.parsing.ParsingPackage;
import com.android.server.p011pm.pkg.parsing.ParsingPackageUtils;
import java.io.File;
import java.util.List;
import java.util.function.Supplier;
/* renamed from: com.android.server.pm.parsing.PackageParser2 */
/* loaded from: classes2.dex */
public class PackageParser2 implements AutoCloseable {
    public static final boolean LOG_PARSE_TIMINGS = Build.IS_DEBUGGABLE;
    public PackageCacher mCacher;
    public ThreadLocal<ApplicationInfo> mSharedAppInfo = ThreadLocal.withInitial(new Supplier() { // from class: com.android.server.pm.parsing.PackageParser2$$ExternalSyntheticLambda0
        @Override // java.util.function.Supplier
        public final Object get() {
            ApplicationInfo lambda$new$0;
            lambda$new$0 = PackageParser2.lambda$new$0();
            return lambda$new$0;
        }
    });
    public ThreadLocal<ParseTypeImpl> mSharedResult;
    public ParsingPackageUtils parsingUtils;

    public static PackageParser2 forParsingFileWithDefaults() {
        final IPlatformCompat asInterface = IPlatformCompat.Stub.asInterface(ServiceManager.getService("platform_compat"));
        return new PackageParser2(null, null, null, new Callback() { // from class: com.android.server.pm.parsing.PackageParser2.1
            @Override // com.android.server.p011pm.pkg.parsing.ParsingPackageUtils.Callback
            public boolean hasFeature(String str) {
                return false;
            }

            @Override // com.android.server.p011pm.parsing.PackageParser2.Callback
            public boolean isChangeEnabled(long j, ApplicationInfo applicationInfo) {
                try {
                    return asInterface.isChangeEnabled(j, applicationInfo);
                } catch (Exception e) {
                    Slog.wtf("PackageParsing", "IPlatformCompat query failed", e);
                    return true;
                }
            }
        });
    }

    public static /* synthetic */ ApplicationInfo lambda$new$0() {
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.uid = -1;
        return applicationInfo;
    }

    public PackageParser2(String[] strArr, DisplayMetrics displayMetrics, File file, final Callback callback) {
        if (displayMetrics == null) {
            displayMetrics = new DisplayMetrics();
            displayMetrics.setToDefaults();
        }
        List splitPermissions = ((PermissionManager) ActivityThread.currentApplication().getSystemService(PermissionManager.class)).getSplitPermissions();
        this.mCacher = file == null ? null : new PackageCacher(file);
        this.parsingUtils = new ParsingPackageUtils(strArr, displayMetrics, splitPermissions, callback);
        final ParseInput.Callback callback2 = new ParseInput.Callback() { // from class: com.android.server.pm.parsing.PackageParser2$$ExternalSyntheticLambda1
            public final boolean isChangeEnabled(long j, String str, int i) {
                boolean lambda$new$1;
                lambda$new$1 = PackageParser2.this.lambda$new$1(callback, j, str, i);
                return lambda$new$1;
            }
        };
        this.mSharedResult = ThreadLocal.withInitial(new Supplier() { // from class: com.android.server.pm.parsing.PackageParser2$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                ParseTypeImpl lambda$new$2;
                lambda$new$2 = PackageParser2.lambda$new$2(callback2);
                return lambda$new$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1(Callback callback, long j, String str, int i) {
        ApplicationInfo applicationInfo = this.mSharedAppInfo.get();
        applicationInfo.packageName = str;
        applicationInfo.targetSdkVersion = i;
        return callback.isChangeEnabled(j, applicationInfo);
    }

    public static /* synthetic */ ParseTypeImpl lambda$new$2(ParseInput.Callback callback) {
        return new ParseTypeImpl(callback);
    }

    public ParsedPackage parsePackage(File file, int i, boolean z) throws PackageManagerException {
        PackageCacher packageCacher;
        ParsedPackage cachedResult;
        File[] listFiles = file.listFiles();
        if (ArrayUtils.size(listFiles) == 1 && listFiles[0].isDirectory()) {
            file = listFiles[0];
        }
        if (!z || (packageCacher = this.mCacher) == null || (cachedResult = packageCacher.getCachedResult(file, i)) == null) {
            boolean z2 = LOG_PARSE_TIMINGS;
            long uptimeMillis = z2 ? SystemClock.uptimeMillis() : 0L;
            ParseResult<ParsingPackage> parsePackage = this.parsingUtils.parsePackage(this.mSharedResult.get().reset(), file, i);
            if (parsePackage.isError()) {
                throw new PackageManagerException(parsePackage.getErrorCode(), parsePackage.getErrorMessage(), parsePackage.getException());
            }
            ParsedPackage hideAsParsed = ((ParsingPackage) parsePackage.getResult()).hideAsParsed();
            long uptimeMillis2 = z2 ? SystemClock.uptimeMillis() : 0L;
            PackageCacher packageCacher2 = this.mCacher;
            if (packageCacher2 != null) {
                packageCacher2.cacheResult(file, i, hideAsParsed);
            }
            if (z2) {
                long j = uptimeMillis2 - uptimeMillis;
                long uptimeMillis3 = SystemClock.uptimeMillis() - uptimeMillis2;
                if (j + uptimeMillis3 > 100) {
                    Slog.i("PackageParsing", "Parse times for '" + file + "': parse=" + j + "ms, update_cache=" + uptimeMillis3 + " ms");
                }
            }
            return hideAsParsed;
        }
        return cachedResult;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        this.mSharedResult.remove();
        this.mSharedAppInfo.remove();
    }

    /* renamed from: com.android.server.pm.parsing.PackageParser2$Callback */
    /* loaded from: classes2.dex */
    public static abstract class Callback implements ParsingPackageUtils.Callback {
        public abstract boolean isChangeEnabled(long j, ApplicationInfo applicationInfo);

        @Override // com.android.server.p011pm.pkg.parsing.ParsingPackageUtils.Callback
        public final ParsingPackage startParsingPackage(String str, String str2, String str3, TypedArray typedArray, boolean z) {
            return PackageImpl.forParsing(str, str2, str3, typedArray, z);
        }
    }
}
