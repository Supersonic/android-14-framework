package android.hardware.soundtrigger.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ConfidenceLevel {
    public int userId = 0;
    public int levelPercent = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == ConfidenceLevel.class) {
            ConfidenceLevel confidenceLevel = (ConfidenceLevel) obj;
            return this.userId == confidenceLevel.userId && this.levelPercent == confidenceLevel.levelPercent;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.userId))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.levelPercent))));
    }

    public final String toString() {
        return "{.userId = " + this.userId + ", .levelPercent = " + this.levelPercent + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.userId = hwBlob.getInt32(0 + j);
        this.levelPercent = hwBlob.getInt32(j + 4);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putInt32(0 + j, this.userId);
        hwBlob.putInt32(j + 4, this.levelPercent);
    }
}
