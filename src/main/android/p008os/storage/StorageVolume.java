package android.p008os.storage;

import android.annotation.SystemApi;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.Environment;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.provider.DocumentsContract;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.io.CharArrayWriter;
import java.io.File;
import java.util.Locale;
import java.util.UUID;
/* renamed from: android.os.storage.StorageVolume */
/* loaded from: classes3.dex */
public final class StorageVolume implements Parcelable {
    private static final String ACTION_OPEN_EXTERNAL_DIRECTORY = "android.os.storage.action.OPEN_EXTERNAL_DIRECTORY";
    public static final Parcelable.Creator<StorageVolume> CREATOR = new Parcelable.Creator<StorageVolume>() { // from class: android.os.storage.StorageVolume.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StorageVolume createFromParcel(Parcel in) {
            return new StorageVolume(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StorageVolume[] newArray(int size) {
            return new StorageVolume[size];
        }
    };
    public static final String EXTRA_DIRECTORY_NAME = "android.os.storage.extra.DIRECTORY_NAME";
    public static final String EXTRA_STORAGE_VOLUME = "android.os.storage.extra.STORAGE_VOLUME";
    public static final int STORAGE_ID_INVALID = 0;
    public static final int STORAGE_ID_PRIMARY = 65537;
    private final boolean mAllowMassStorage;
    private final String mDescription;
    private final boolean mEmulated;
    private final boolean mExternallyManaged;
    private final String mFsUuid;
    private final String mId;
    private final File mInternalPath;
    private final long mMaxFileSize;
    private final UserHandle mOwner;
    private final File mPath;
    private final boolean mPrimary;
    private final boolean mRemovable;
    private final String mState;
    private final UUID mUuid;

    public StorageVolume(String id, File path, File internalPath, String description, boolean primary, boolean removable, boolean emulated, boolean externallyManaged, boolean allowMassStorage, long maxFileSize, UserHandle owner, UUID uuid, String fsUuid, String state) {
        this.mId = (String) Preconditions.checkNotNull(id);
        this.mPath = (File) Preconditions.checkNotNull(path);
        this.mInternalPath = (File) Preconditions.checkNotNull(internalPath);
        this.mDescription = (String) Preconditions.checkNotNull(description);
        this.mPrimary = primary;
        this.mRemovable = removable;
        this.mEmulated = emulated;
        this.mExternallyManaged = externallyManaged;
        this.mAllowMassStorage = allowMassStorage;
        this.mMaxFileSize = maxFileSize;
        this.mOwner = (UserHandle) Preconditions.checkNotNull(owner);
        this.mUuid = uuid;
        this.mFsUuid = fsUuid;
        this.mState = (String) Preconditions.checkNotNull(state);
    }

    private StorageVolume(Parcel in) {
        this.mId = in.readString8();
        this.mPath = new File(in.readString8());
        this.mInternalPath = new File(in.readString8());
        this.mDescription = in.readString8();
        this.mPrimary = in.readInt() != 0;
        this.mRemovable = in.readInt() != 0;
        this.mEmulated = in.readInt() != 0;
        this.mExternallyManaged = in.readInt() != 0;
        this.mAllowMassStorage = in.readInt() != 0;
        this.mMaxFileSize = in.readLong();
        this.mOwner = (UserHandle) in.readParcelable(null, UserHandle.class);
        if (in.readInt() != 0) {
            this.mUuid = StorageManager.convert(in.readString8());
        } else {
            this.mUuid = null;
        }
        this.mFsUuid = in.readString8();
        this.mState = in.readString8();
    }

    @SystemApi
    public String getId() {
        return this.mId;
    }

    public String getPath() {
        return this.mPath.toString();
    }

    public String getInternalPath() {
        return this.mInternalPath.toString();
    }

    public File getPathFile() {
        return this.mPath;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public File getDirectory() {
        char c;
        String str = this.mState;
        switch (str.hashCode()) {
            case 1242932856:
                if (str.equals(Environment.MEDIA_MOUNTED)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1299749220:
                if (str.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
                return this.mPath;
            default:
                return null;
        }
    }

    public String getDescription(Context context) {
        return this.mDescription;
    }

    public boolean isPrimary() {
        return this.mPrimary;
    }

    public boolean isRemovable() {
        return this.mRemovable;
    }

    public boolean isEmulated() {
        return this.mEmulated;
    }

    @SystemApi
    public boolean isExternallyManaged() {
        return this.mExternallyManaged;
    }

    public boolean allowMassStorage() {
        return this.mAllowMassStorage;
    }

    public long getMaxFileSize() {
        return this.mMaxFileSize;
    }

    public UserHandle getOwner() {
        return this.mOwner;
    }

    public UUID getStorageUuid() {
        return this.mUuid;
    }

    public String getUuid() {
        return this.mFsUuid;
    }

    public String getMediaStoreVolumeName() {
        if (isPrimary()) {
            return "external_primary";
        }
        return getNormalizedUuid();
    }

    public static String normalizeUuid(String fsUuid) {
        if (fsUuid != null) {
            return fsUuid.toLowerCase(Locale.US);
        }
        return null;
    }

    public String getNormalizedUuid() {
        return normalizeUuid(this.mFsUuid);
    }

    public int getFatVolumeId() {
        String str = this.mFsUuid;
        if (str == null || str.length() != 9) {
            return -1;
        }
        try {
            return (int) Long.parseLong(this.mFsUuid.replace(NativeLibraryHelper.CLEAR_ABI_OVERRIDE, ""), 16);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getUserLabel() {
        return this.mDescription;
    }

    public String getState() {
        return this.mState;
    }

    @Deprecated
    public Intent createAccessIntent(String directoryName) {
        if (!isPrimary() || directoryName != null) {
            if (directoryName != null && !Environment.isStandardDirectory(directoryName)) {
                return null;
            }
            Intent intent = new Intent(ACTION_OPEN_EXTERNAL_DIRECTORY);
            intent.putExtra(EXTRA_STORAGE_VOLUME, this);
            intent.putExtra(EXTRA_DIRECTORY_NAME, directoryName);
            return intent;
        }
        return null;
    }

    public Intent createOpenDocumentTreeIntent() {
        String rootId;
        if (isEmulated()) {
            rootId = "primary";
        } else {
            rootId = this.mFsUuid;
        }
        Uri rootUri = DocumentsContract.buildRootUri(DocumentsContract.EXTERNAL_STORAGE_PROVIDER_AUTHORITY, rootId);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).putExtra(DocumentsContract.EXTRA_INITIAL_URI, rootUri).putExtra(DocumentsContract.EXTRA_SHOW_ADVANCED, true);
        return intent;
    }

    public boolean equals(Object obj) {
        File file;
        if ((obj instanceof StorageVolume) && (file = this.mPath) != null) {
            StorageVolume volume = (StorageVolume) obj;
            return file.equals(volume.mPath);
        }
        return false;
    }

    public int hashCode() {
        return this.mPath.hashCode();
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder("StorageVolume: ").append(this.mDescription);
        if (this.mFsUuid != null) {
            buffer.append(" (").append(this.mFsUuid).append(NavigationBarInflaterView.KEY_CODE_END);
        }
        return buffer.toString();
    }

    public String dump() {
        CharArrayWriter writer = new CharArrayWriter();
        dump(new IndentingPrintWriter(writer, "    ", 80));
        return writer.toString();
    }

    public void dump(IndentingPrintWriter pw) {
        pw.println("StorageVolume:");
        pw.increaseIndent();
        pw.printPair("mId", this.mId);
        pw.printPair("mPath", this.mPath);
        pw.printPair("mInternalPath", this.mInternalPath);
        pw.printPair("mDescription", this.mDescription);
        pw.printPair("mPrimary", Boolean.valueOf(this.mPrimary));
        pw.printPair("mRemovable", Boolean.valueOf(this.mRemovable));
        pw.printPair("mEmulated", Boolean.valueOf(this.mEmulated));
        pw.printPair("mExternallyManaged", Boolean.valueOf(this.mExternallyManaged));
        pw.printPair("mAllowMassStorage", Boolean.valueOf(this.mAllowMassStorage));
        pw.printPair("mMaxFileSize", Long.valueOf(this.mMaxFileSize));
        pw.printPair("mOwner", this.mOwner);
        pw.printPair("mFsUuid", this.mFsUuid);
        pw.printPair("mState", this.mState);
        pw.decreaseIndent();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString8(this.mId);
        parcel.writeString8(this.mPath.toString());
        parcel.writeString8(this.mInternalPath.toString());
        parcel.writeString8(this.mDescription);
        parcel.writeInt(this.mPrimary ? 1 : 0);
        parcel.writeInt(this.mRemovable ? 1 : 0);
        parcel.writeInt(this.mEmulated ? 1 : 0);
        parcel.writeInt(this.mExternallyManaged ? 1 : 0);
        parcel.writeInt(this.mAllowMassStorage ? 1 : 0);
        parcel.writeLong(this.mMaxFileSize);
        parcel.writeParcelable(this.mOwner, flags);
        if (this.mUuid != null) {
            parcel.writeInt(1);
            parcel.writeString8(StorageManager.convert(this.mUuid));
        } else {
            parcel.writeInt(0);
        }
        parcel.writeString8(this.mFsUuid);
        parcel.writeString8(this.mState);
    }

    /* renamed from: android.os.storage.StorageVolume$Builder */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private String mDescription;
        private boolean mEmulated;
        private String mId;
        private UserHandle mOwner;
        private File mPath;
        private boolean mPrimary;
        private boolean mRemovable;
        private String mState;
        private UUID mStorageUuid;
        private String mUuid;

        public Builder(String id, File path, String description, UserHandle owner, String state) {
            this.mId = id;
            this.mPath = path;
            this.mDescription = description;
            this.mOwner = owner;
            this.mState = state;
        }

        public Builder setStorageUuid(UUID storageUuid) {
            this.mStorageUuid = storageUuid;
            return this;
        }

        public Builder setUuid(String uuid) {
            this.mUuid = uuid;
            return this;
        }

        public Builder setPrimary(boolean primary) {
            this.mPrimary = primary;
            return this;
        }

        public Builder setRemovable(boolean removable) {
            this.mRemovable = removable;
            return this;
        }

        public Builder setEmulated(boolean emulated) {
            this.mEmulated = emulated;
            return this;
        }

        public StorageVolume build() {
            String str = this.mId;
            File file = this.mPath;
            return new StorageVolume(str, file, file, this.mDescription, this.mPrimary, this.mRemovable, this.mEmulated, false, false, 0L, this.mOwner, this.mStorageUuid, this.mUuid, this.mState);
        }
    }
}
