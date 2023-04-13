package android.content.p001pm;

import android.p008os.Build;
import android.p008os.Environment;
import android.p008os.FileUtils;
import android.p008os.SystemProperties;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
/* renamed from: android.content.pm.PackagePartitions */
/* loaded from: classes.dex */
public class PackagePartitions {
    public static final int PARTITION_ODM = 2;
    public static final int PARTITION_OEM = 3;
    public static final int PARTITION_PRODUCT = 4;
    public static final int PARTITION_SYSTEM = 0;
    public static final int PARTITION_SYSTEM_EXT = 5;
    public static final int PARTITION_VENDOR = 1;
    private static final ArrayList<SystemPartition> SYSTEM_PARTITIONS = new ArrayList<>(Arrays.asList(new SystemPartition(Environment.getRootDirectory(), 0, "system", true, false), new SystemPartition(Environment.getVendorDirectory(), 1, "vendor", true, true), new SystemPartition(Environment.getOdmDirectory(), 2, Build.Partition.PARTITION_NAME_ODM, true, true), new SystemPartition(Environment.getOemDirectory(), 3, Build.Partition.PARTITION_NAME_OEM, false, true), new SystemPartition(Environment.getProductDirectory(), 4, "product", true, true), new SystemPartition(Environment.getSystemExtDirectory(), 5, Build.Partition.PARTITION_NAME_SYSTEM_EXT, true, true)));
    public static final String FINGERPRINT = getFingerprint();

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.PackagePartitions$PartitionType */
    /* loaded from: classes.dex */
    public @interface PartitionType {
    }

    public static <T> ArrayList<T> getOrderedPartitions(Function<SystemPartition, T> producer) {
        ArrayList<T> out = new ArrayList<>();
        int n = SYSTEM_PARTITIONS.size();
        for (int i = 0; i < n; i++) {
            T v = producer.apply(SYSTEM_PARTITIONS.get(i));
            if (v != null) {
                out.add(v);
            }
        }
        return out;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static File canonicalize(File path) {
        try {
            return path.getCanonicalFile();
        } catch (IOException e) {
            return path;
        }
    }

    private static String getFingerprint() {
        String[] digestProperties = new String[SYSTEM_PARTITIONS.size() + 1];
        int i = 0;
        while (true) {
            ArrayList<SystemPartition> arrayList = SYSTEM_PARTITIONS;
            if (i < arrayList.size()) {
                String partitionName = arrayList.get(i).getName();
                digestProperties[i] = "ro." + partitionName + ".build.fingerprint";
                i++;
            } else {
                int i2 = arrayList.size();
                digestProperties[i2] = "ro.build.fingerprint";
                return SystemProperties.digestOf(digestProperties);
            }
        }
    }

    /* renamed from: android.content.pm.PackagePartitions$SystemPartition */
    /* loaded from: classes.dex */
    public static class SystemPartition {
        private final DeferredCanonicalFile mAppFolder;
        private final DeferredCanonicalFile mFolder;
        private final String mName;
        private final File mNonConicalFolder;
        private final DeferredCanonicalFile mOverlayFolder;
        private final DeferredCanonicalFile mPrivAppFolder;
        public final int type;

        private SystemPartition(File folder, int type, String name, boolean containsPrivApp, boolean containsOverlay) {
            this.type = type;
            this.mName = name;
            this.mFolder = new DeferredCanonicalFile(folder);
            this.mAppFolder = new DeferredCanonicalFile(folder, "app");
            this.mPrivAppFolder = containsPrivApp ? new DeferredCanonicalFile(folder, "priv-app") : null;
            this.mOverlayFolder = containsOverlay ? new DeferredCanonicalFile(folder, "overlay") : null;
            this.mNonConicalFolder = folder;
        }

        public SystemPartition(SystemPartition original) {
            this.type = original.type;
            this.mName = original.mName;
            this.mFolder = new DeferredCanonicalFile(original.mFolder.getFile());
            this.mAppFolder = original.mAppFolder;
            this.mPrivAppFolder = original.mPrivAppFolder;
            this.mOverlayFolder = original.mOverlayFolder;
            this.mNonConicalFolder = original.mNonConicalFolder;
        }

        public SystemPartition(File rootFolder, SystemPartition partition) {
            this(rootFolder, partition.type, partition.mName, partition.mPrivAppFolder != null, partition.mOverlayFolder != null);
        }

        public String getName() {
            return this.mName;
        }

        public File getFolder() {
            return this.mFolder.getFile();
        }

        public File getNonConicalFolder() {
            return this.mNonConicalFolder;
        }

        public File getAppFolder() {
            DeferredCanonicalFile deferredCanonicalFile = this.mAppFolder;
            if (deferredCanonicalFile == null) {
                return null;
            }
            return deferredCanonicalFile.getFile();
        }

        public File getPrivAppFolder() {
            DeferredCanonicalFile deferredCanonicalFile = this.mPrivAppFolder;
            if (deferredCanonicalFile == null) {
                return null;
            }
            return deferredCanonicalFile.getFile();
        }

        public File getOverlayFolder() {
            DeferredCanonicalFile deferredCanonicalFile = this.mOverlayFolder;
            if (deferredCanonicalFile == null) {
                return null;
            }
            return deferredCanonicalFile.getFile();
        }

        public boolean containsPath(String path) {
            return containsFile(new File(path));
        }

        public boolean containsFile(File file) {
            return FileUtils.contains(this.mFolder.getFile(), PackagePartitions.canonicalize(file));
        }

        public boolean containsPrivApp(File scanFile) {
            DeferredCanonicalFile deferredCanonicalFile = this.mPrivAppFolder;
            return deferredCanonicalFile != null && FileUtils.contains(deferredCanonicalFile.getFile(), PackagePartitions.canonicalize(scanFile));
        }

        public boolean containsApp(File scanFile) {
            DeferredCanonicalFile deferredCanonicalFile = this.mAppFolder;
            return deferredCanonicalFile != null && FileUtils.contains(deferredCanonicalFile.getFile(), PackagePartitions.canonicalize(scanFile));
        }

        public boolean containsOverlay(File scanFile) {
            DeferredCanonicalFile deferredCanonicalFile = this.mOverlayFolder;
            return deferredCanonicalFile != null && FileUtils.contains(deferredCanonicalFile.getFile(), PackagePartitions.canonicalize(scanFile));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.content.pm.PackagePartitions$DeferredCanonicalFile */
    /* loaded from: classes.dex */
    public static class DeferredCanonicalFile {
        private File mFile;
        private boolean mIsCanonical;

        private DeferredCanonicalFile(File dir) {
            this.mIsCanonical = false;
            this.mFile = dir;
        }

        private DeferredCanonicalFile(File dir, String fileName) {
            this.mIsCanonical = false;
            this.mFile = new File(dir, fileName);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public File getFile() {
            if (!this.mIsCanonical) {
                this.mFile = PackagePartitions.canonicalize(this.mFile);
                this.mIsCanonical = true;
            }
            return this.mFile;
        }
    }
}
