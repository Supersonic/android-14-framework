package android.hardware.configstore.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class OptionalBool {
    public boolean specified = false;
    public boolean value = false;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == OptionalBool.class) {
            OptionalBool optionalBool = (OptionalBool) obj;
            return this.specified == optionalBool.specified && this.value == optionalBool.value;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.specified))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.value))));
    }

    public final String toString() {
        return "{.specified = " + this.specified + ", .value = " + this.value + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(2L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.specified = hwBlob.getBool(0 + j);
        this.value = hwBlob.getBool(j + 1);
    }

    public final void writeToParcel(HwParcel hwParcel) {
        HwBlob hwBlob = new HwBlob(2);
        writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putBool(0 + j, this.specified);
        hwBlob.putBool(j + 1, this.value);
    }
}
