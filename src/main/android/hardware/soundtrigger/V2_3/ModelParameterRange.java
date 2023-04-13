package android.hardware.soundtrigger.V2_3;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ModelParameterRange {
    public int start = 0;
    public int end = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == ModelParameterRange.class) {
            ModelParameterRange modelParameterRange = (ModelParameterRange) obj;
            return this.start == modelParameterRange.start && this.end == modelParameterRange.end;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.start))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.end))));
    }

    public final String toString() {
        return "{.start = " + this.start + ", .end = " + this.end + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.start = hwBlob.getInt32(0 + j);
        this.end = hwBlob.getInt32(j + 4);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putInt32(0 + j, this.start);
        hwBlob.putInt32(j + 4, this.end);
    }
}
