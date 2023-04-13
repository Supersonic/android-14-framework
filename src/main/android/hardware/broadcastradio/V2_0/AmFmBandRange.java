package android.hardware.broadcastradio.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AmFmBandRange {
    public int lowerBound = 0;
    public int upperBound = 0;
    public int spacing = 0;
    public int scanSpacing = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == AmFmBandRange.class) {
            AmFmBandRange amFmBandRange = (AmFmBandRange) obj;
            return this.lowerBound == amFmBandRange.lowerBound && this.upperBound == amFmBandRange.upperBound && this.spacing == amFmBandRange.spacing && this.scanSpacing == amFmBandRange.scanSpacing;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.lowerBound))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.upperBound))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.spacing))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.scanSpacing))));
    }

    public final String toString() {
        return "{.lowerBound = " + this.lowerBound + ", .upperBound = " + this.upperBound + ", .spacing = " + this.spacing + ", .scanSpacing = " + this.scanSpacing + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.lowerBound = hwBlob.getInt32(0 + j);
        this.upperBound = hwBlob.getInt32(4 + j);
        this.spacing = hwBlob.getInt32(8 + j);
        this.scanSpacing = hwBlob.getInt32(j + 12);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putInt32(0 + j, this.lowerBound);
        hwBlob.putInt32(4 + j, this.upperBound);
        hwBlob.putInt32(8 + j, this.spacing);
        hwBlob.putInt32(j + 12, this.scanSpacing);
    }
}
