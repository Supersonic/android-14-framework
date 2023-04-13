package android.hardware.weaver.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class WeaverConfig {
    public int slots = 0;
    public int keySize = 0;
    public int valueSize = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == WeaverConfig.class) {
            WeaverConfig weaverConfig = (WeaverConfig) obj;
            return this.slots == weaverConfig.slots && this.keySize == weaverConfig.keySize && this.valueSize == weaverConfig.valueSize;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.slots))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.keySize))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.valueSize))));
    }

    public final String toString() {
        return "{.slots = " + this.slots + ", .keySize = " + this.keySize + ", .valueSize = " + this.valueSize + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(12L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.slots = hwBlob.getInt32(0 + j);
        this.keySize = hwBlob.getInt32(4 + j);
        this.valueSize = hwBlob.getInt32(j + 8);
    }

    public final void writeToParcel(HwParcel hwParcel) {
        HwBlob hwBlob = new HwBlob(12);
        writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putInt32(0 + j, this.slots);
        hwBlob.putInt32(4 + j, this.keySize);
        hwBlob.putInt32(j + 8, this.valueSize);
    }
}
