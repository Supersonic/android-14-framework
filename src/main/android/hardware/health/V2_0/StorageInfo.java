package android.hardware.health.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class StorageInfo {
    public StorageAttribute attr = new StorageAttribute();
    public short eol = 0;
    public short lifetimeA = 0;
    public short lifetimeB = 0;
    public String version = new String();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == StorageInfo.class) {
            StorageInfo storageInfo = (StorageInfo) obj;
            return HidlSupport.deepEquals(this.attr, storageInfo.attr) && this.eol == storageInfo.eol && this.lifetimeA == storageInfo.lifetimeA && this.lifetimeB == storageInfo.lifetimeB && HidlSupport.deepEquals(this.version, storageInfo.version);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.attr)), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.eol))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.lifetimeA))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.lifetimeB))), Integer.valueOf(HidlSupport.deepHashCode(this.version)));
    }

    public final String toString() {
        return "{.attr = " + this.attr + ", .eol = " + ((int) this.eol) + ", .lifetimeA = " + ((int) this.lifetimeA) + ", .lifetimeB = " + ((int) this.lifetimeB) + ", .version = " + this.version + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.attr.readEmbeddedFromParcel(hwParcel, hwBlob, j + 0);
        this.eol = hwBlob.getInt16(24 + j);
        this.lifetimeA = hwBlob.getInt16(26 + j);
        this.lifetimeB = hwBlob.getInt16(28 + j);
        long j2 = j + 32;
        String string = hwBlob.getString(j2);
        this.version = string;
        hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        this.attr.writeEmbeddedToBlob(hwBlob, 0 + j);
        hwBlob.putInt16(24 + j, this.eol);
        hwBlob.putInt16(26 + j, this.lifetimeA);
        hwBlob.putInt16(28 + j, this.lifetimeB);
        hwBlob.putString(j + 32, this.version);
    }
}
