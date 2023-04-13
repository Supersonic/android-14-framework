package android.hardware.radio.config.V1_2;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class SimSlotStatus {
    public android.hardware.radio.config.V1_0.SimSlotStatus base = new android.hardware.radio.config.V1_0.SimSlotStatus();
    public String eid = new String();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == SimSlotStatus.class) {
            SimSlotStatus simSlotStatus = (SimSlotStatus) obj;
            return HidlSupport.deepEquals(this.base, simSlotStatus.base) && HidlSupport.deepEquals(this.eid, simSlotStatus.eid);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.base)), Integer.valueOf(HidlSupport.deepHashCode(this.eid)));
    }

    public final String toString() {
        return "{.base = " + this.base + ", .eid = " + this.eid + "}";
    }

    public static final ArrayList<SimSlotStatus> readVectorFromParcel(HwParcel hwParcel) {
        ArrayList<SimSlotStatus> arrayList = new ArrayList<>();
        HwBlob readBuffer = hwParcel.readBuffer(16L);
        int int32 = readBuffer.getInt32(8L);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 64, readBuffer.handle(), 0L, true);
        arrayList.clear();
        for (int i = 0; i < int32; i++) {
            SimSlotStatus simSlotStatus = new SimSlotStatus();
            simSlotStatus.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 64);
            arrayList.add(simSlotStatus);
        }
        return arrayList;
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.base.readEmbeddedFromParcel(hwParcel, hwBlob, j + 0);
        long j2 = j + 48;
        String string = hwBlob.getString(j2);
        this.eid = string;
        hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
    }
}
