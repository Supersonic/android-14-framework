package android.p008os.storage;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.p008os.Environment;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.DebugUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.C4057R;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.io.CharArrayWriter;
import java.io.File;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
/* renamed from: android.os.storage.VolumeInfo */
/* loaded from: classes3.dex */
public class VolumeInfo implements Parcelable {
    public static final String ACTION_VOLUME_STATE_CHANGED = "android.os.storage.action.VOLUME_STATE_CHANGED";
    public static final Parcelable.Creator<VolumeInfo> CREATOR;
    private static final String DOCUMENT_AUTHORITY = "com.android.externalstorage.documents";
    private static final String DOCUMENT_ROOT_PRIMARY_EMULATED = "primary";
    public static final String EXTRA_VOLUME_ID = "android.os.storage.extra.VOLUME_ID";
    public static final String EXTRA_VOLUME_STATE = "android.os.storage.extra.VOLUME_STATE";
    public static final String ID_EMULATED_INTERNAL = "emulated";
    public static final String ID_PRIVATE_INTERNAL = "private";
    public static final int MOUNT_FLAG_PRIMARY = 1;
    public static final int MOUNT_FLAG_VISIBLE_FOR_READ = 2;
    public static final int MOUNT_FLAG_VISIBLE_FOR_WRITE = 4;
    public static final int STATE_BAD_REMOVAL = 8;
    public static final int STATE_CHECKING = 1;
    public static final int STATE_EJECTING = 5;
    public static final int STATE_FORMATTING = 4;
    public static final int STATE_MOUNTED = 2;
    public static final int STATE_MOUNTED_READ_ONLY = 3;
    public static final int STATE_REMOVED = 7;
    public static final int STATE_UNMOUNTABLE = 6;
    public static final int STATE_UNMOUNTED = 0;
    public static final int TYPE_ASEC = 3;
    public static final int TYPE_EMULATED = 2;
    public static final int TYPE_OBB = 4;
    public static final int TYPE_PRIVATE = 1;
    public static final int TYPE_PUBLIC = 0;
    public static final int TYPE_STUB = 5;
    public final DiskInfo disk;
    public String fsLabel;
    public String fsType;
    public String fsUuid;

    /* renamed from: id */
    public final String f330id;
    public String internalPath;
    public int mountFlags;
    public int mountUserId;
    public final String partGuid;
    public String path;
    public int state;
    public final int type;
    private static SparseArray<String> sStateToEnvironment = new SparseArray<>();
    private static ArrayMap<String, String> sEnvironmentToBroadcast = new ArrayMap<>();
    private static SparseIntArray sStateToDescrip = new SparseIntArray();
    private static final Comparator<VolumeInfo> sDescriptionComparator = new Comparator<VolumeInfo>() { // from class: android.os.storage.VolumeInfo.1
        @Override // java.util.Comparator
        public int compare(VolumeInfo lhs, VolumeInfo rhs) {
            if (VolumeInfo.ID_PRIVATE_INTERNAL.equals(lhs.getId())) {
                return -1;
            }
            if (lhs.getDescription() == null) {
                return 1;
            }
            if (rhs.getDescription() == null) {
                return -1;
            }
            return lhs.getDescription().compareTo(rhs.getDescription());
        }
    };

    static {
        sStateToEnvironment.put(0, Environment.MEDIA_UNMOUNTED);
        sStateToEnvironment.put(1, Environment.MEDIA_CHECKING);
        sStateToEnvironment.put(2, Environment.MEDIA_MOUNTED);
        sStateToEnvironment.put(3, Environment.MEDIA_MOUNTED_READ_ONLY);
        sStateToEnvironment.put(4, Environment.MEDIA_UNMOUNTED);
        sStateToEnvironment.put(5, Environment.MEDIA_EJECTING);
        sStateToEnvironment.put(6, Environment.MEDIA_UNMOUNTABLE);
        sStateToEnvironment.put(7, Environment.MEDIA_REMOVED);
        sStateToEnvironment.put(8, Environment.MEDIA_BAD_REMOVAL);
        sEnvironmentToBroadcast.put(Environment.MEDIA_UNMOUNTED, Intent.ACTION_MEDIA_UNMOUNTED);
        sEnvironmentToBroadcast.put(Environment.MEDIA_CHECKING, Intent.ACTION_MEDIA_CHECKING);
        sEnvironmentToBroadcast.put(Environment.MEDIA_MOUNTED, Intent.ACTION_MEDIA_MOUNTED);
        sEnvironmentToBroadcast.put(Environment.MEDIA_MOUNTED_READ_ONLY, Intent.ACTION_MEDIA_MOUNTED);
        sEnvironmentToBroadcast.put(Environment.MEDIA_EJECTING, Intent.ACTION_MEDIA_EJECT);
        sEnvironmentToBroadcast.put(Environment.MEDIA_UNMOUNTABLE, Intent.ACTION_MEDIA_UNMOUNTABLE);
        sEnvironmentToBroadcast.put(Environment.MEDIA_REMOVED, Intent.ACTION_MEDIA_REMOVED);
        sEnvironmentToBroadcast.put(Environment.MEDIA_BAD_REMOVAL, Intent.ACTION_MEDIA_BAD_REMOVAL);
        sStateToDescrip.put(0, C4057R.string.ext_media_status_unmounted);
        sStateToDescrip.put(1, C4057R.string.ext_media_status_checking);
        sStateToDescrip.put(2, C4057R.string.ext_media_status_mounted);
        sStateToDescrip.put(3, C4057R.string.ext_media_status_mounted_ro);
        sStateToDescrip.put(4, C4057R.string.ext_media_status_formatting);
        sStateToDescrip.put(5, C4057R.string.ext_media_status_ejecting);
        sStateToDescrip.put(6, C4057R.string.ext_media_status_unmountable);
        sStateToDescrip.put(7, C4057R.string.ext_media_status_removed);
        sStateToDescrip.put(8, C4057R.string.ext_media_status_bad_removal);
        CREATOR = new Parcelable.Creator<VolumeInfo>() { // from class: android.os.storage.VolumeInfo.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public VolumeInfo createFromParcel(Parcel in) {
                return new VolumeInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public VolumeInfo[] newArray(int size) {
                return new VolumeInfo[size];
            }
        };
    }

    public VolumeInfo(String id, int type, DiskInfo disk, String partGuid) {
        this.mountFlags = 0;
        this.mountUserId = -10000;
        this.state = 0;
        this.f330id = (String) Preconditions.checkNotNull(id);
        this.type = type;
        this.disk = disk;
        this.partGuid = partGuid;
    }

    public VolumeInfo(Parcel parcel) {
        this.mountFlags = 0;
        this.mountUserId = -10000;
        this.state = 0;
        this.f330id = parcel.readString8();
        this.type = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.disk = DiskInfo.CREATOR.createFromParcel(parcel);
        } else {
            this.disk = null;
        }
        this.partGuid = parcel.readString8();
        this.mountFlags = parcel.readInt();
        this.mountUserId = parcel.readInt();
        this.state = parcel.readInt();
        this.fsType = parcel.readString8();
        this.fsUuid = parcel.readString8();
        this.fsLabel = parcel.readString8();
        this.path = parcel.readString8();
        this.internalPath = parcel.readString8();
    }

    public VolumeInfo(VolumeInfo volumeInfo) {
        this.mountFlags = 0;
        this.mountUserId = -10000;
        this.state = 0;
        this.f330id = volumeInfo.f330id;
        this.type = volumeInfo.type;
        this.disk = volumeInfo.disk;
        this.partGuid = volumeInfo.partGuid;
        this.mountFlags = volumeInfo.mountFlags;
        this.mountUserId = volumeInfo.mountUserId;
        this.state = volumeInfo.state;
        this.fsType = volumeInfo.fsType;
        this.fsUuid = volumeInfo.fsUuid;
        this.fsLabel = volumeInfo.fsLabel;
        this.path = volumeInfo.path;
        this.internalPath = volumeInfo.internalPath;
    }

    public static String getEnvironmentForState(int state) {
        String envState = sStateToEnvironment.get(state);
        if (envState != null) {
            return envState;
        }
        return "unknown";
    }

    public static String getBroadcastForEnvironment(String envState) {
        return sEnvironmentToBroadcast.get(envState);
    }

    public static String getBroadcastForState(int state) {
        return getBroadcastForEnvironment(getEnvironmentForState(state));
    }

    public static Comparator<VolumeInfo> getDescriptionComparator() {
        return sDescriptionComparator;
    }

    public String getId() {
        return this.f330id;
    }

    public DiskInfo getDisk() {
        return this.disk;
    }

    public String getDiskId() {
        DiskInfo diskInfo = this.disk;
        if (diskInfo != null) {
            return diskInfo.f329id;
        }
        return null;
    }

    public int getType() {
        return this.type;
    }

    public int getState() {
        return this.state;
    }

    public int getStateDescription() {
        return sStateToDescrip.get(this.state, 0);
    }

    public String getFsUuid() {
        return this.fsUuid;
    }

    public String getNormalizedFsUuid() {
        String str = this.fsUuid;
        if (str != null) {
            return str.toLowerCase(Locale.US);
        }
        return null;
    }

    public int getMountUserId() {
        return this.mountUserId;
    }

    public String getDescription() {
        if (ID_PRIVATE_INTERNAL.equals(this.f330id) || this.f330id.startsWith("emulated;")) {
            return Resources.getSystem().getString(C4057R.string.storage_internal);
        }
        if (!TextUtils.isEmpty(this.fsLabel)) {
            return this.fsLabel;
        }
        return null;
    }

    public boolean isMountedReadable() {
        int i = this.state;
        return i == 2 || i == 3;
    }

    public boolean isMountedWritable() {
        return this.state == 2;
    }

    public boolean isPrimary() {
        return (this.mountFlags & 1) != 0;
    }

    public boolean isPrimaryPhysical() {
        return isPrimary() && getType() == 0;
    }

    private boolean isVisibleForRead() {
        return (this.mountFlags & 2) != 0;
    }

    private boolean isVisibleForWrite() {
        return (this.mountFlags & 4) != 0;
    }

    public boolean isVisible() {
        return isVisibleForRead() || isVisibleForWrite();
    }

    private boolean isVolumeSupportedForUser(int userId) {
        if (this.mountUserId != userId) {
            return false;
        }
        int i = this.type;
        return i == 0 || i == 5 || i == 2;
    }

    public boolean isVisibleForUser(int userId) {
        return isVolumeSupportedForUser(userId) && isVisible();
    }

    public boolean isPrimaryEmulatedForUser(int userId) {
        return this.f330id.equals("emulated;" + userId);
    }

    public boolean isVisibleForRead(int userId) {
        return isVolumeSupportedForUser(userId) && isVisibleForRead();
    }

    public boolean isVisibleForWrite(int userId) {
        return isVolumeSupportedForUser(userId) && isVisibleForWrite();
    }

    public File getPath() {
        if (this.path != null) {
            return new File(this.path);
        }
        return null;
    }

    public File getInternalPath() {
        if (this.internalPath != null) {
            return new File(this.internalPath);
        }
        return null;
    }

    public File getPathForUser(int userId) {
        if (this.path == null) {
            return null;
        }
        int i = this.type;
        if (i == 0 || i == 5) {
            return new File(this.path);
        }
        if (i == 2) {
            return new File(this.path, Integer.toString(userId));
        }
        return null;
    }

    public File getInternalPathForUser(int userId) {
        if (this.path == null) {
            return null;
        }
        int i = this.type;
        if (i == 0 || i == 5) {
            return new File(this.path.replace("/storage/", "/mnt/media_rw/"));
        }
        return getPathForUser(userId);
    }

    public StorageVolume buildStorageVolume(Context context, int userId, boolean reportUnmounted) {
        String environmentForState;
        File userPath;
        File internalPath;
        boolean emulated;
        UUID uuid;
        String derivedFsUuid;
        long maxFileSize;
        boolean removable;
        String description;
        UUID uuid2;
        boolean removable2;
        StorageManager storage = (StorageManager) context.getSystemService(StorageManager.class);
        boolean externallyManaged = this.type == 5;
        if (!reportUnmounted) {
            environmentForState = getEnvironmentForState(this.state);
        } else {
            environmentForState = Environment.MEDIA_UNMOUNTED;
        }
        String envState = environmentForState;
        File userPath2 = getPathForUser(userId);
        if (userPath2 != null) {
            userPath = userPath2;
        } else {
            userPath = new File("/dev/null");
        }
        File internalPath2 = getInternalPathForUser(userId);
        if (internalPath2 != null) {
            internalPath = internalPath2;
        } else {
            internalPath = new File("/dev/null");
        }
        String description2 = null;
        String derivedFsUuid2 = this.fsUuid;
        int i = this.type;
        if (i == 2) {
            emulated = true;
            VolumeInfo privateVol = storage.findPrivateForEmulated(this);
            if (privateVol != null) {
                description2 = storage.getBestVolumeDescription(privateVol);
                uuid2 = StorageManager.convert(privateVol.fsUuid);
                derivedFsUuid2 = privateVol.fsUuid;
            } else {
                uuid2 = StorageManager.UUID_DEFAULT;
            }
            if (isPrimaryEmulatedForUser(userId)) {
                removable2 = false;
            } else {
                removable2 = true;
            }
            uuid = uuid2;
            derivedFsUuid = derivedFsUuid2;
            maxFileSize = 0;
            removable = removable2;
        } else if (i == 0 || i == 5) {
            emulated = false;
            description2 = storage.getBestVolumeDescription(this);
            if ("vfat".equals(this.fsType)) {
                uuid = null;
                derivedFsUuid = derivedFsUuid2;
                maxFileSize = 4294967295L;
                removable = true;
            } else {
                uuid = null;
                derivedFsUuid = derivedFsUuid2;
                maxFileSize = 0;
                removable = true;
            }
        } else {
            throw new IllegalStateException("Unexpected volume type " + this.type);
        }
        if (description2 != null) {
            description = description2;
        } else {
            String description3 = context.getString(17039374);
            description = description3;
        }
        return new StorageVolume(this.f330id, userPath, internalPath, description, isPrimary(), removable, emulated, externallyManaged, false, maxFileSize, new UserHandle(userId), uuid, derivedFsUuid, envState);
    }

    public static int buildStableMtpStorageId(String fsUuid) {
        if (TextUtils.isEmpty(fsUuid)) {
            return 0;
        }
        int hash = 0;
        for (int i = 0; i < fsUuid.length(); i++) {
            hash = (hash * 31) + fsUuid.charAt(i);
        }
        int i2 = hash << 16;
        int hash2 = (i2 ^ hash) & (-65536);
        if (hash2 == 0) {
            hash2 = 131072;
        }
        if (hash2 == 65536) {
            hash2 = 131072;
        }
        if (hash2 == -65536) {
            hash2 = -131072;
        }
        return hash2 | 1;
    }

    public Intent buildBrowseIntent() {
        return buildBrowseIntentForUser(UserHandle.myUserId());
    }

    public Intent buildBrowseIntentForUser(int userId) {
        Uri uri;
        int i = this.type;
        if ((i == 0 || i == 5) && this.mountUserId == userId) {
            uri = DocumentsContract.buildRootUri("com.android.externalstorage.documents", this.fsUuid);
        } else if (i == 2 && isPrimary()) {
            uri = DocumentsContract.buildRootUri("com.android.externalstorage.documents", "primary");
        } else {
            return null;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uri, DocumentsContract.Root.MIME_TYPE_ITEM);
        intent.putExtra(DocumentsContract.EXTRA_SHOW_ADVANCED, isPrimary());
        return intent;
    }

    public String toString() {
        CharArrayWriter writer = new CharArrayWriter();
        dump(new IndentingPrintWriter(writer, "    ", 80));
        return writer.toString();
    }

    public void dump(IndentingPrintWriter pw) {
        pw.println("VolumeInfo{" + this.f330id + "}:");
        pw.increaseIndent();
        pw.printPair("type", DebugUtils.valueToString(getClass(), "TYPE_", this.type));
        pw.printPair("diskId", getDiskId());
        pw.printPair("partGuid", this.partGuid);
        pw.printPair("mountFlags", DebugUtils.flagsToString(getClass(), "MOUNT_FLAG_", this.mountFlags));
        pw.printPair("mountUserId", Integer.valueOf(this.mountUserId));
        pw.printPair("state", DebugUtils.valueToString(getClass(), "STATE_", this.state));
        pw.println();
        pw.printPair("fsType", this.fsType);
        pw.printPair("fsUuid", this.fsUuid);
        pw.printPair("fsLabel", this.fsLabel);
        pw.println();
        pw.printPair("path", this.path);
        pw.printPair("internalPath", this.internalPath);
        pw.decreaseIndent();
        pw.println();
    }

    /* renamed from: clone */
    public VolumeInfo m3245clone() {
        Parcel temp = Parcel.obtain();
        try {
            writeToParcel(temp, 0);
            temp.setDataPosition(0);
            return CREATOR.createFromParcel(temp);
        } finally {
            temp.recycle();
        }
    }

    public boolean equals(Object o) {
        if (o instanceof VolumeInfo) {
            return Objects.equals(this.f330id, ((VolumeInfo) o).f330id);
        }
        return false;
    }

    public int hashCode() {
        return this.f330id.hashCode();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString8(this.f330id);
        parcel.writeInt(this.type);
        if (this.disk != null) {
            parcel.writeInt(1);
            this.disk.writeToParcel(parcel, flags);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeString8(this.partGuid);
        parcel.writeInt(this.mountFlags);
        parcel.writeInt(this.mountUserId);
        parcel.writeInt(this.state);
        parcel.writeString8(this.fsType);
        parcel.writeString8(this.fsUuid);
        parcel.writeString8(this.fsLabel);
        parcel.writeString8(this.path);
        parcel.writeString8(this.internalPath);
    }
}
