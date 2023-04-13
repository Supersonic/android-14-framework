package android.hardware.broadcastradio.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ProgramIdentifier {
    public int type = 0;
    public long value = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == ProgramIdentifier.class) {
            ProgramIdentifier programIdentifier = (ProgramIdentifier) obj;
            return this.type == programIdentifier.type && this.value == programIdentifier.value;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.value))));
    }

    public final String toString() {
        return "{.type = " + this.type + ", .value = " + this.value + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.type = hwBlob.getInt32(0 + j);
        this.value = hwBlob.getInt64(j + 8);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putInt32(0 + j, this.type);
        hwBlob.putInt64(j + 8, this.value);
    }
}
