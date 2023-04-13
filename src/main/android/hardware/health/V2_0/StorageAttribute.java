package android.hardware.health.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class StorageAttribute {
    public boolean isInternal = false;
    public boolean isBootDevice = false;
    public String name = new String();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == StorageAttribute.class) {
            StorageAttribute storageAttribute = (StorageAttribute) obj;
            return this.isInternal == storageAttribute.isInternal && this.isBootDevice == storageAttribute.isBootDevice && HidlSupport.deepEquals(this.name, storageAttribute.name);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isInternal))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isBootDevice))), Integer.valueOf(HidlSupport.deepHashCode(this.name)));
    }

    public final String toString() {
        return "{.isInternal = " + this.isInternal + ", .isBootDevice = " + this.isBootDevice + ", .name = " + this.name + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.isInternal = hwBlob.getBool(j + 0);
        this.isBootDevice = hwBlob.getBool(1 + j);
        long j2 = j + 8;
        String string = hwBlob.getString(j2);
        this.name = string;
        hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putBool(0 + j, this.isInternal);
        hwBlob.putBool(1 + j, this.isBootDevice);
        hwBlob.putString(j + 8, this.name);
    }
}
