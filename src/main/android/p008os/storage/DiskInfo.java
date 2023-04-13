package android.p008os.storage;

import android.content.res.Resources;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.DebugUtils;
import com.android.internal.C4057R;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.io.CharArrayWriter;
import java.util.Objects;
/* renamed from: android.os.storage.DiskInfo */
/* loaded from: classes3.dex */
public class DiskInfo implements Parcelable {
    public static final String ACTION_DISK_SCANNED = "android.os.storage.action.DISK_SCANNED";
    public static final Parcelable.Creator<DiskInfo> CREATOR = new Parcelable.Creator<DiskInfo>() { // from class: android.os.storage.DiskInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DiskInfo createFromParcel(Parcel in) {
            return new DiskInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DiskInfo[] newArray(int size) {
            return new DiskInfo[size];
        }
    };
    public static final String EXTRA_DISK_ID = "android.os.storage.extra.DISK_ID";
    public static final String EXTRA_VOLUME_COUNT = "android.os.storage.extra.VOLUME_COUNT";
    public static final int FLAG_ADOPTABLE = 1;
    public static final int FLAG_DEFAULT_PRIMARY = 2;
    public static final int FLAG_SD = 4;
    public static final int FLAG_STUB_VISIBLE = 64;
    public static final int FLAG_USB = 8;
    public final int flags;

    /* renamed from: id */
    public final String f329id;
    public String label;
    public long size;
    public String sysPath;
    public int volumeCount;

    public DiskInfo(String id, int flags) {
        this.f329id = (String) Preconditions.checkNotNull(id);
        this.flags = flags;
    }

    public DiskInfo(Parcel parcel) {
        this.f329id = parcel.readString();
        this.flags = parcel.readInt();
        this.size = parcel.readLong();
        this.label = parcel.readString();
        this.volumeCount = parcel.readInt();
        this.sysPath = parcel.readString();
    }

    public String getId() {
        return this.f329id;
    }

    private boolean isInteresting(String label) {
        return (TextUtils.isEmpty(label) || label.equalsIgnoreCase("ata") || label.toLowerCase().contains("generic") || label.toLowerCase().startsWith("usb") || label.toLowerCase().startsWith("multiple")) ? false : true;
    }

    public String getDescription() {
        Resources res = Resources.getSystem();
        int i = this.flags;
        if ((i & 4) != 0) {
            if (isInteresting(this.label)) {
                return res.getString(C4057R.string.storage_sd_card_label, this.label);
            }
            return res.getString(C4057R.string.storage_sd_card);
        } else if ((i & 8) != 0) {
            if (isInteresting(this.label)) {
                return res.getString(C4057R.string.storage_usb_drive_label, this.label);
            }
            return res.getString(C4057R.string.storage_usb_drive);
        } else {
            return null;
        }
    }

    public String getShortDescription() {
        Resources res = Resources.getSystem();
        if (isSd()) {
            return res.getString(C4057R.string.storage_sd_card);
        }
        if (isUsb()) {
            return res.getString(C4057R.string.storage_usb_drive);
        }
        return null;
    }

    public boolean isAdoptable() {
        return (this.flags & 1) != 0;
    }

    public boolean isDefaultPrimary() {
        return (this.flags & 2) != 0;
    }

    public boolean isSd() {
        return (this.flags & 4) != 0;
    }

    public boolean isUsb() {
        return (this.flags & 8) != 0;
    }

    public boolean isStubVisible() {
        return (this.flags & 64) != 0;
    }

    public String toString() {
        CharArrayWriter writer = new CharArrayWriter();
        dump(new IndentingPrintWriter(writer, "    ", 80));
        return writer.toString();
    }

    public void dump(IndentingPrintWriter pw) {
        pw.println("DiskInfo{" + this.f329id + "}:");
        pw.increaseIndent();
        pw.printPair("flags", DebugUtils.flagsToString(getClass(), "FLAG_", this.flags));
        pw.printPair("size", Long.valueOf(this.size));
        pw.printPair("label", this.label);
        pw.println();
        pw.printPair("sysPath", this.sysPath);
        pw.decreaseIndent();
        pw.println();
    }

    /* renamed from: clone */
    public DiskInfo m3239clone() {
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
        if (o instanceof DiskInfo) {
            return Objects.equals(this.f329id, ((DiskInfo) o).f329id);
        }
        return false;
    }

    public int hashCode() {
        return this.f329id.hashCode();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.f329id);
        parcel.writeInt(this.flags);
        parcel.writeLong(this.size);
        parcel.writeString(this.label);
        parcel.writeInt(this.volumeCount);
        parcel.writeString(this.sysPath);
    }
}
