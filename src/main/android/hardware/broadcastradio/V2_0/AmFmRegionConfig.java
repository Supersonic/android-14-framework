package android.hardware.broadcastradio.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AmFmRegionConfig {
    public byte fmDeemphasis;
    public byte fmRds;
    public ArrayList<AmFmBandRange> ranges = new ArrayList<>();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == AmFmRegionConfig.class) {
            AmFmRegionConfig amFmRegionConfig = (AmFmRegionConfig) obj;
            return HidlSupport.deepEquals(this.ranges, amFmRegionConfig.ranges) && HidlSupport.deepEquals(Byte.valueOf(this.fmDeemphasis), Byte.valueOf(amFmRegionConfig.fmDeemphasis)) && HidlSupport.deepEquals(Byte.valueOf(this.fmRds), Byte.valueOf(amFmRegionConfig.fmRds));
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.ranges)), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.fmDeemphasis))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.fmRds))));
    }

    public final String toString() {
        return "{.ranges = " + this.ranges + ", .fmDeemphasis = " + Deemphasis.dumpBitfield(this.fmDeemphasis) + ", .fmRds = " + Rds.dumpBitfield(this.fmRds) + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(24L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        long j2 = j + 0;
        int int32 = hwBlob.getInt32(8 + j2);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 16, hwBlob.handle(), j2 + 0, true);
        this.ranges.clear();
        for (int i = 0; i < int32; i++) {
            AmFmBandRange amFmBandRange = new AmFmBandRange();
            amFmBandRange.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 16);
            this.ranges.add(amFmBandRange);
        }
        this.fmDeemphasis = hwBlob.getInt8(j + 16);
        this.fmRds = hwBlob.getInt8(j + 17);
    }

    public final void writeToParcel(HwParcel hwParcel) {
        HwBlob hwBlob = new HwBlob(24);
        writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        int size = this.ranges.size();
        long j2 = j + 0;
        hwBlob.putInt32(8 + j2, size);
        hwBlob.putBool(12 + j2, false);
        HwBlob hwBlob2 = new HwBlob(size * 16);
        for (int i = 0; i < size; i++) {
            this.ranges.get(i).writeEmbeddedToBlob(hwBlob2, i * 16);
        }
        hwBlob.putBlob(j2 + 0, hwBlob2);
        hwBlob.putInt8(16 + j, this.fmDeemphasis);
        hwBlob.putInt8(j + 17, this.fmRds);
    }
}
