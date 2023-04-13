package android.hardware.p002tv.cec.V1_1;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* renamed from: android.hardware.tv.cec.V1_1.CecMessage */
/* loaded from: classes.dex */
public final class CecMessage {
    public int initiator = 0;
    public int destination = 0;
    public ArrayList<Byte> body = new ArrayList<>();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == CecMessage.class) {
            CecMessage cecMessage = (CecMessage) obj;
            return this.initiator == cecMessage.initiator && this.destination == cecMessage.destination && HidlSupport.deepEquals(this.body, cecMessage.body);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.initiator))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.destination))), Integer.valueOf(HidlSupport.deepHashCode(this.body)));
    }

    public final String toString() {
        return "{.initiator = " + CecLogicalAddress.toString(this.initiator) + ", .destination = " + CecLogicalAddress.toString(this.destination) + ", .body = " + this.body + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(24L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.initiator = hwBlob.getInt32(j + 0);
        this.destination = hwBlob.getInt32(j + 4);
        long j2 = j + 8;
        int int32 = hwBlob.getInt32(8 + j2);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 1, hwBlob.handle(), j2 + 0, true);
        this.body.clear();
        for (int i = 0; i < int32; i++) {
            this.body.add(Byte.valueOf(readEmbeddedBuffer.getInt8(i * 1)));
        }
    }

    public final void writeToParcel(HwParcel hwParcel) {
        HwBlob hwBlob = new HwBlob(24);
        writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putInt32(j + 0, this.initiator);
        hwBlob.putInt32(4 + j, this.destination);
        int size = this.body.size();
        long j2 = j + 8;
        hwBlob.putInt32(8 + j2, size);
        hwBlob.putBool(12 + j2, false);
        HwBlob hwBlob2 = new HwBlob(size * 1);
        for (int i = 0; i < size; i++) {
            hwBlob2.putInt8(i * 1, this.body.get(i).byteValue());
        }
        hwBlob.putBlob(j2 + 0, hwBlob2);
    }
}
