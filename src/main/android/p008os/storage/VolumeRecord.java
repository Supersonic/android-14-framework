package android.p008os.storage;

import android.content.Context;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.util.DebugUtils;
import android.util.TimeUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.util.Locale;
import java.util.Objects;
/* renamed from: android.os.storage.VolumeRecord */
/* loaded from: classes3.dex */
public class VolumeRecord implements Parcelable {
    public static final Parcelable.Creator<VolumeRecord> CREATOR = new Parcelable.Creator<VolumeRecord>() { // from class: android.os.storage.VolumeRecord.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VolumeRecord createFromParcel(Parcel in) {
            return new VolumeRecord(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VolumeRecord[] newArray(int size) {
            return new VolumeRecord[size];
        }
    };
    public static final String EXTRA_FS_UUID = "android.os.storage.extra.FS_UUID";
    public static final int USER_FLAG_INITED = 1;
    public static final int USER_FLAG_SNOOZED = 2;
    public long createdMillis;
    public final String fsUuid;
    public long lastBenchMillis;
    public long lastSeenMillis;
    public long lastTrimMillis;
    public String nickname;
    public String partGuid;
    public final int type;
    public int userFlags;

    public VolumeRecord(int type, String fsUuid) {
        this.type = type;
        this.fsUuid = (String) Preconditions.checkNotNull(fsUuid);
    }

    public VolumeRecord(Parcel parcel) {
        this.type = parcel.readInt();
        this.fsUuid = parcel.readString();
        this.partGuid = parcel.readString();
        this.nickname = parcel.readString();
        this.userFlags = parcel.readInt();
        this.createdMillis = parcel.readLong();
        this.lastSeenMillis = parcel.readLong();
        this.lastTrimMillis = parcel.readLong();
        this.lastBenchMillis = parcel.readLong();
    }

    public int getType() {
        return this.type;
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

    public String getNickname() {
        return this.nickname;
    }

    public boolean isInited() {
        return (this.userFlags & 1) != 0;
    }

    public boolean isSnoozed() {
        return (this.userFlags & 2) != 0;
    }

    public StorageVolume buildStorageVolume(Context context) {
        String id = "unknown:" + this.fsUuid;
        File userPath = new File("/dev/null");
        File internalPath = new File("/dev/null");
        UserHandle user = new UserHandle(-10000);
        String description = this.nickname;
        return new StorageVolume(id, userPath, internalPath, description == null ? context.getString(17039374) : description, false, true, false, false, false, 0L, user, null, this.fsUuid, "unknown");
    }

    public void dump(IndentingPrintWriter pw) {
        pw.println("VolumeRecord:");
        pw.increaseIndent();
        pw.printPair("type", DebugUtils.valueToString(VolumeInfo.class, "TYPE_", this.type));
        pw.printPair("fsUuid", this.fsUuid);
        pw.printPair("partGuid", this.partGuid);
        pw.println();
        pw.printPair("nickname", this.nickname);
        pw.printPair("userFlags", DebugUtils.flagsToString(VolumeRecord.class, "USER_FLAG_", this.userFlags));
        pw.println();
        pw.printPair("createdMillis", TimeUtils.formatForLogging(this.createdMillis));
        pw.printPair("lastSeenMillis", TimeUtils.formatForLogging(this.lastSeenMillis));
        pw.printPair("lastTrimMillis", TimeUtils.formatForLogging(this.lastTrimMillis));
        pw.printPair("lastBenchMillis", TimeUtils.formatForLogging(this.lastBenchMillis));
        pw.decreaseIndent();
        pw.println();
    }

    /* renamed from: clone */
    public VolumeRecord m3246clone() {
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
        if (o instanceof VolumeRecord) {
            return Objects.equals(this.fsUuid, ((VolumeRecord) o).fsUuid);
        }
        return false;
    }

    public int hashCode() {
        return this.fsUuid.hashCode();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.type);
        parcel.writeString(this.fsUuid);
        parcel.writeString(this.partGuid);
        parcel.writeString(this.nickname);
        parcel.writeInt(this.userFlags);
        parcel.writeLong(this.createdMillis);
        parcel.writeLong(this.lastSeenMillis);
        parcel.writeLong(this.lastTrimMillis);
        parcel.writeLong(this.lastBenchMillis);
    }
}
