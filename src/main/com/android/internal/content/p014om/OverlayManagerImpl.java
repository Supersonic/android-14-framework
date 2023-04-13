package com.android.internal.content.p014om;

import android.content.Context;
import android.content.p000om.OverlayIdentifier;
import android.content.p000om.OverlayInfo;
import android.content.p000om.OverlayManagerTransaction;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.parsing.FrameworkParsingPackageUtils;
import android.p008os.FabricatedOverlayInfo;
import android.p008os.FabricatedOverlayInternal;
import android.p008os.FabricatedOverlayInternalEntry;
import android.p008os.FileUtils;
import android.p008os.Process;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
/* renamed from: com.android.internal.content.om.OverlayManagerImpl */
/* loaded from: classes4.dex */
public class OverlayManagerImpl {
    private static final boolean DEBUG = false;
    private static final String FRRO_EXTENSION = ".frro";
    private static final String IDMAP_EXTENSION = ".idmap";
    public static final String SELF_TARGET = ".self_target";
    private static final String TAG = "OverlayManagerImpl";
    private Path mBasePath;
    private final Context mContext;

    private static native void createFrroFile(String str, FabricatedOverlayInternal fabricatedOverlayInternal) throws IOException;

    private static native void createIdmapFile(String str, String str2, String str3, String str4, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) throws IOException;

    private static native FabricatedOverlayInfo getFabricatedOverlayInfo(String str) throws IOException;

    public OverlayManagerImpl(Context context) {
        this.mContext = (Context) Objects.requireNonNull(context);
        if (!Process.myUserHandle().equals(context.getUser())) {
            throw new SecurityException("Self-Targeting doesn't support multiple user now!");
        }
    }

    private static void cleanExpiredOverlays(Path selfTargetingBasePath, Path folderForCurrentBaseApk) {
        try {
            final String currentBaseFolder = folderForCurrentBaseApk.toString();
            final String selfTargetingDir = selfTargetingBasePath.getFileName().toString();
            Files.walkFileTree(selfTargetingBasePath, new SimpleFileVisitor<Path>() { // from class: com.android.internal.content.om.OverlayManagerImpl.1
                @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String fileName = dir.getFileName().toString();
                    if (fileName.equals(currentBaseFolder)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return super.preVisitDirectory((C41391) dir, attrs);
                }

                @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!file.toFile().delete()) {
                        Log.m104w(OverlayManagerImpl.TAG, "Failed to delete file " + file);
                    }
                    return super.visitFile((C41391) file, attrs);
                }

                @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    String fileName = dir.getFileName().toString();
                    if (!fileName.equals(currentBaseFolder) && !fileName.equals(selfTargetingDir) && !dir.toFile().delete()) {
                        Log.m104w(OverlayManagerImpl.TAG, "Failed to delete dir " + dir);
                    }
                    return super.postVisitDirectory((C41391) dir, exc);
                }
            });
        } catch (IOException e) {
            Log.m104w(TAG, "Unknown fail " + e);
        }
    }

    public void ensureBaseDir() {
        String baseApkPath = this.mContext.getApplicationInfo().getBaseCodePath();
        boolean z = false;
        Path baseApkFolderName = Path.of(baseApkPath, new String[0]).getParent().getFileName();
        File selfTargetingBaseFile = this.mContext.getDir(SELF_TARGET, 0);
        Preconditions.checkArgument(selfTargetingBaseFile.isDirectory() && selfTargetingBaseFile.exists() && selfTargetingBaseFile.canWrite() && selfTargetingBaseFile.canRead() && selfTargetingBaseFile.canExecute(), "Can't work for this context");
        cleanExpiredOverlays(selfTargetingBaseFile.toPath(), baseApkFolderName);
        File baseFile = new File(selfTargetingBaseFile, baseApkFolderName.toString());
        if (!baseFile.exists()) {
            if (!baseFile.mkdirs()) {
                Log.m104w(TAG, "Failed to create directory " + baseFile);
            }
            FileUtils.setPermissions(baseFile, 448, -1, -1);
        }
        if (baseFile.isDirectory() && baseFile.exists() && baseFile.canWrite() && baseFile.canRead() && baseFile.canExecute()) {
            z = true;
        }
        Preconditions.checkArgument(z, "Can't create a workspace for this context");
        this.mBasePath = baseFile.toPath();
    }

    private boolean isSameWithTargetSignature(String targetPackage) {
        PackageManager packageManager = this.mContext.getPackageManager();
        String packageName = this.mContext.getPackageName();
        return TextUtils.equals(packageName, targetPackage) || packageManager.checkSignatures(packageName, targetPackage) == 0;
    }

    public static String checkOverlayNameValid(String name) {
        String overlayName = (String) Preconditions.checkStringNotEmpty(name, "overlayName should be neither empty nor null string");
        String checkOverlayNameResult = FrameworkParsingPackageUtils.validateName(overlayName, false, true);
        Preconditions.checkArgument(checkOverlayNameResult == null, TextUtils.formatSimple("Invalid overlayName \"%s\". The check result is %s.", overlayName, checkOverlayNameResult));
        return overlayName;
    }

    private void checkPackageName(String packageName) {
        Preconditions.checkStringNotEmpty(packageName);
        Preconditions.checkArgument(TextUtils.equals(this.mContext.getPackageName(), packageName), TextUtils.formatSimple("UID %d doesn't own the package %s", Integer.valueOf(Process.myUid()), packageName));
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x00cc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void registerFabricatedOverlay(FabricatedOverlayInternal overlayInternal) throws IOException, PackageManager.NameNotFoundException {
        Path frroPath;
        String path;
        String path2;
        boolean z;
        ensureBaseDir();
        Objects.requireNonNull(overlayInternal);
        List<FabricatedOverlayInternalEntry> entryList = (List) Objects.requireNonNull(overlayInternal.entries);
        boolean z2 = true;
        Preconditions.checkArgument(!entryList.isEmpty(), "overlay entries shouldn't be empty");
        String overlayName = checkOverlayNameValid(overlayInternal.overlayName);
        checkPackageName(overlayInternal.packageName);
        checkPackageName(overlayInternal.targetPackageName);
        Preconditions.checkStringNotEmpty(overlayInternal.targetOverlayable, "Target overlayable should be neither null nor empty string.");
        ApplicationInfo applicationInfo = this.mContext.getApplicationInfo();
        String targetPackage = (String) Preconditions.checkStringNotEmpty(applicationInfo.getBaseCodePath());
        Path frroPath2 = this.mBasePath.resolve(overlayName + FRRO_EXTENSION);
        Path idmapPath = this.mBasePath.resolve(overlayName + IDMAP_EXTENSION);
        createFrroFile(frroPath2.toString(), overlayInternal);
        try {
            path = frroPath2.toString();
            path2 = idmapPath.toString();
            if (!applicationInfo.isSystemApp()) {
                try {
                    if (!applicationInfo.isSystemExt()) {
                        z2 = false;
                    }
                } catch (IOException e) {
                    e = e;
                    frroPath = frroPath2;
                    if (!frroPath.toFile().delete()) {
                        Log.m104w(TAG, "Failed to delete file " + frroPath);
                    }
                    throw e;
                }
            }
            z = z2;
            frroPath = frroPath2;
        } catch (IOException e2) {
            e = e2;
            frroPath = frroPath2;
        }
        try {
            createIdmapFile(targetPackage, path, path2, overlayName, z, applicationInfo.isVendor(), applicationInfo.isProduct(), isSameWithTargetSignature(overlayInternal.targetPackageName), applicationInfo.isOdm(), applicationInfo.isOem());
        } catch (IOException e3) {
            e = e3;
            if (!frroPath.toFile().delete()) {
            }
            throw e;
        }
    }

    public void unregisterFabricatedOverlay(String overlayName) {
        ensureBaseDir();
        checkOverlayNameValid(overlayName);
        Path frroPath = this.mBasePath.resolve(overlayName + FRRO_EXTENSION);
        Path idmapPath = this.mBasePath.resolve(overlayName + IDMAP_EXTENSION);
        if (!frroPath.toFile().delete()) {
            Log.m104w(TAG, "Failed to delete file " + frroPath);
        }
        if (!idmapPath.toFile().delete()) {
            Log.m104w(TAG, "Failed to delete file " + idmapPath);
        }
    }

    public void commit(OverlayManagerTransaction transaction) throws PackageManager.NameNotFoundException, IOException {
        Objects.requireNonNull(transaction);
        Iterator<OverlayManagerTransaction.Request> it = transaction.getRequests();
        while (it.hasNext()) {
            OverlayManagerTransaction.Request request = it.next();
            if (request.type == 2) {
                FabricatedOverlayInternal fabricatedOverlayInternal = (FabricatedOverlayInternal) Objects.requireNonNull((FabricatedOverlayInternal) request.extras.getParcelable(OverlayManagerTransaction.Request.BUNDLE_FABRICATED_OVERLAY, FabricatedOverlayInternal.class));
                if (TextUtils.isEmpty(fabricatedOverlayInternal.packageName)) {
                    fabricatedOverlayInternal.packageName = this.mContext.getPackageName();
                } else if (!TextUtils.equals(fabricatedOverlayInternal.packageName, this.mContext.getPackageName())) {
                    throw new IllegalArgumentException("Unknown package name in transaction");
                }
                registerFabricatedOverlay(fabricatedOverlayInternal);
            } else if (request.type == 3) {
                OverlayIdentifier overlayIdentifier = (OverlayIdentifier) Objects.requireNonNull(request.overlay);
                unregisterFabricatedOverlay(overlayIdentifier.getOverlayName());
            } else {
                throw new IllegalArgumentException("Unknown request in transaction " + request);
            }
        }
    }

    public List<OverlayInfo> getOverlayInfosForTarget(String targetPackage) {
        ensureBaseDir();
        File base = this.mBasePath.toFile();
        File[] frroFiles = base.listFiles(new FilenameFilter() { // from class: com.android.internal.content.om.OverlayManagerImpl$$ExternalSyntheticLambda0
            @Override // java.io.FilenameFilter
            public final boolean accept(File file, String str) {
                return OverlayManagerImpl.lambda$getOverlayInfosForTarget$0(file, str);
            }
        });
        ArrayList<OverlayInfo> overlayInfos = new ArrayList<>();
        for (File file : frroFiles) {
            try {
                FabricatedOverlayInfo fabricatedOverlayInfo = getFabricatedOverlayInfo(file.getAbsolutePath());
                if (TextUtils.equals(targetPackage, fabricatedOverlayInfo.targetPackageName)) {
                    OverlayInfo overlayInfo = new OverlayInfo(fabricatedOverlayInfo.packageName, fabricatedOverlayInfo.overlayName, fabricatedOverlayInfo.targetPackageName, fabricatedOverlayInfo.targetOverlayable, null, file.getAbsolutePath(), 3, UserHandle.myUserId(), Integer.MAX_VALUE, true, true);
                    overlayInfos.add(overlayInfo);
                }
            } catch (IOException e) {
                Log.m104w(TAG, "can't load " + file);
            }
        }
        return overlayInfos;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getOverlayInfosForTarget$0(File dir, String name) {
        if (name.endsWith(FRRO_EXTENSION)) {
            String idmapFileName = name.substring(0, name.length() - FRRO_EXTENSION.length()) + IDMAP_EXTENSION;
            File idmapFile = new File(dir, idmapFileName);
            return idmapFile.exists();
        }
        return false;
    }
}
