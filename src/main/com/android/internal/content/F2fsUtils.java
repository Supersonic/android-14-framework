package com.android.internal.content;

import android.content.ContentResolver;
import android.p008os.Environment;
import android.p008os.incremental.IncrementalManager;
import android.provider.Settings;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public final class F2fsUtils {
    private static final String COMPRESSION_FEATURE = "compression";
    private static final boolean DEBUG_F2FS = false;
    private static final String TAG = "F2fsUtils";
    private static final File sKernelFeatures = new File("/sys/fs/f2fs/features");
    private static final File sUserDataFeatures = new File("/dev/sys/fs/by-name/userdata/features");
    private static final File sDataDirectory = Environment.getDataDirectory();
    private static final boolean sKernelCompressionAvailable = isCompressionEnabledInKernel();
    private static final boolean sUserDataCompressionAvailable = isCompressionEnabledOnUserData();

    private static native long nativeReleaseCompressedBlocks(String str);

    public static void releaseCompressedBlocks(ContentResolver resolver, File file) {
        File[] files;
        if (!sKernelCompressionAvailable || !sUserDataCompressionAvailable) {
            return;
        }
        boolean releaseCompressBlocks = Settings.Secure.getInt(resolver, Settings.Secure.RELEASE_COMPRESS_BLOCKS_ON_INSTALL, 1) != 0;
        if (!releaseCompressBlocks || !isCompressionAllowed(file) || (files = getFilesToRelease(file)) == null || files.length == 0) {
            return;
        }
        for (int i = files.length - 1; i >= 0; i--) {
            nativeReleaseCompressedBlocks(files[i].getAbsolutePath());
        }
    }

    private static boolean isCompressionAllowed(File file) {
        try {
            String filePath = file.getCanonicalPath();
            if (IncrementalManager.isIncrementalPath(filePath) || !isChild(sDataDirectory, filePath)) {
                return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isChild(File base, String childPath) {
        try {
            File base2 = base.getCanonicalFile();
            for (File parentFile = new File(childPath).getCanonicalFile(); parentFile != null; parentFile = parentFile.getParentFile()) {
                if (base2.equals(parentFile)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isCompressionEnabledInKernel() {
        File[] features = sKernelFeatures.listFiles();
        if (features == null || features.length == 0) {
            return false;
        }
        for (int i = features.length - 1; i >= 0; i--) {
            File file = features[i];
            if (COMPRESSION_FEATURE.equals(features[i].getName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCompressionEnabledOnUserData() {
        File file = sUserDataFeatures;
        if (file.exists() && file.isFile() && file.canRead()) {
            try {
                List<String> configLines = Files.readAllLines(file.toPath());
                if (configLines == null || configLines.size() > 1 || TextUtils.isEmpty(configLines.get(0))) {
                    return false;
                }
                String[] features = configLines.get(0).split(",");
                for (int i = features.length - 1; i >= 0; i--) {
                    if (COMPRESSION_FEATURE.equals(features[i].trim())) {
                        return true;
                    }
                }
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    private static List<File> getFilesRecursive(File path) {
        File[] allFiles = path.listFiles();
        if (allFiles == null) {
            return null;
        }
        ArrayList<File> files = new ArrayList<>();
        for (File f : allFiles) {
            if (f.isDirectory()) {
                files.addAll(getFilesRecursive(f));
            } else if (f.isFile()) {
                files.add(f);
            }
        }
        return files;
    }

    private static File[] getFilesToRelease(File codePath) {
        List<File> files = getFilesRecursive(codePath);
        if (files == null) {
            if (!codePath.isFile()) {
                return null;
            }
            return new File[]{codePath};
        } else if (files.size() == 0) {
            return null;
        } else {
            return (File[]) files.toArray(new File[files.size()]);
        }
    }
}
