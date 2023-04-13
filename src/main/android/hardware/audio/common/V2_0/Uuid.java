package android.hardware.audio.common.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Uuid {
    public int timeLow = 0;
    public short timeMid = 0;
    public short versionAndTimeHigh = 0;
    public short variantAndClockSeqHigh = 0;
    public byte[] node = new byte[6];

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == Uuid.class) {
            Uuid uuid = (Uuid) obj;
            return this.timeLow == uuid.timeLow && this.timeMid == uuid.timeMid && this.versionAndTimeHigh == uuid.versionAndTimeHigh && this.variantAndClockSeqHigh == uuid.variantAndClockSeqHigh && HidlSupport.deepEquals(this.node, uuid.node);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.timeLow))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.timeMid))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.versionAndTimeHigh))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.variantAndClockSeqHigh))), Integer.valueOf(HidlSupport.deepHashCode(this.node)));
    }

    public final String toString() {
        return "{.timeLow = " + this.timeLow + ", .timeMid = " + ((int) this.timeMid) + ", .versionAndTimeHigh = " + ((int) this.versionAndTimeHigh) + ", .variantAndClockSeqHigh = " + ((int) this.variantAndClockSeqHigh) + ", .node = " + Arrays.toString(this.node) + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.timeLow = hwBlob.getInt32(0 + j);
        this.timeMid = hwBlob.getInt16(4 + j);
        this.versionAndTimeHigh = hwBlob.getInt16(6 + j);
        this.variantAndClockSeqHigh = hwBlob.getInt16(8 + j);
        hwBlob.copyToInt8Array(j + 10, this.node, 6);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putInt32(0 + j, this.timeLow);
        hwBlob.putInt16(4 + j, this.timeMid);
        hwBlob.putInt16(6 + j, this.versionAndTimeHigh);
        hwBlob.putInt16(8 + j, this.variantAndClockSeqHigh);
        long j2 = j + 10;
        byte[] bArr = this.node;
        if (bArr == null || bArr.length != 6) {
            throw new IllegalArgumentException("Array element is not of the expected length");
        }
        hwBlob.putInt8Array(j2, bArr);
    }
}
