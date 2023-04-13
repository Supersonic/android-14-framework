package android.hardware.broadcastradio.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Metadata {
    public int key = 0;
    public long intValue = 0;
    public String stringValue = new String();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == Metadata.class) {
            Metadata metadata = (Metadata) obj;
            return this.key == metadata.key && this.intValue == metadata.intValue && HidlSupport.deepEquals(this.stringValue, metadata.stringValue);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.key))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.intValue))), Integer.valueOf(HidlSupport.deepHashCode(this.stringValue)));
    }

    public final String toString() {
        return "{.key = " + this.key + ", .intValue = " + this.intValue + ", .stringValue = " + this.stringValue + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.key = hwBlob.getInt32(j + 0);
        this.intValue = hwBlob.getInt64(8 + j);
        long j2 = j + 16;
        String string = hwBlob.getString(j2);
        this.stringValue = string;
        hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
    }
}
