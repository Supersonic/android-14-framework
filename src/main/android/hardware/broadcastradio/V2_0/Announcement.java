package android.hardware.broadcastradio.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Announcement {
    public ProgramSelector selector = new ProgramSelector();
    public byte type = 0;
    public ArrayList<VendorKeyValue> vendorInfo = new ArrayList<>();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == Announcement.class) {
            Announcement announcement = (Announcement) obj;
            return HidlSupport.deepEquals(this.selector, announcement.selector) && this.type == announcement.type && HidlSupport.deepEquals(this.vendorInfo, announcement.vendorInfo);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.selector)), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(this.vendorInfo)));
    }

    public final String toString() {
        return "{.selector = " + this.selector + ", .type = " + AnnouncementType.toString(this.type) + ", .vendorInfo = " + this.vendorInfo + "}";
    }

    public static final ArrayList<Announcement> readVectorFromParcel(HwParcel hwParcel) {
        ArrayList<Announcement> arrayList = new ArrayList<>();
        HwBlob readBuffer = hwParcel.readBuffer(16L);
        int int32 = readBuffer.getInt32(8L);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 56, readBuffer.handle(), 0L, true);
        arrayList.clear();
        for (int i = 0; i < int32; i++) {
            Announcement announcement = new Announcement();
            announcement.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 56);
            arrayList.add(announcement);
        }
        return arrayList;
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.selector.readEmbeddedFromParcel(hwParcel, hwBlob, j + 0);
        this.type = hwBlob.getInt8(j + 32);
        long j2 = j + 40;
        int int32 = hwBlob.getInt32(8 + j2);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 32, hwBlob.handle(), j2 + 0, true);
        this.vendorInfo.clear();
        for (int i = 0; i < int32; i++) {
            VendorKeyValue vendorKeyValue = new VendorKeyValue();
            vendorKeyValue.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 32);
            this.vendorInfo.add(vendorKeyValue);
        }
    }
}
