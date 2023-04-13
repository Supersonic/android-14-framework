package android.hardware.radio.config.V1_0;

import android.hardware.radio.V1_0.CardState;
import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class SimSlotStatus {
    public int cardState = 0;
    public int slotState = 0;
    public String atr = new String();
    public int logicalSlotId = 0;
    public String iccid = new String();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == SimSlotStatus.class) {
            SimSlotStatus simSlotStatus = (SimSlotStatus) obj;
            return this.cardState == simSlotStatus.cardState && this.slotState == simSlotStatus.slotState && HidlSupport.deepEquals(this.atr, simSlotStatus.atr) && this.logicalSlotId == simSlotStatus.logicalSlotId && HidlSupport.deepEquals(this.iccid, simSlotStatus.iccid);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.cardState))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.slotState))), Integer.valueOf(HidlSupport.deepHashCode(this.atr)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.logicalSlotId))), Integer.valueOf(HidlSupport.deepHashCode(this.iccid)));
    }

    public final String toString() {
        return "{.cardState = " + CardState.toString(this.cardState) + ", .slotState = " + SlotState.toString(this.slotState) + ", .atr = " + this.atr + ", .logicalSlotId = " + this.logicalSlotId + ", .iccid = " + this.iccid + "}";
    }

    public static final ArrayList<SimSlotStatus> readVectorFromParcel(HwParcel hwParcel) {
        ArrayList<SimSlotStatus> arrayList = new ArrayList<>();
        HwBlob readBuffer = hwParcel.readBuffer(16L);
        int int32 = readBuffer.getInt32(8L);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 48, readBuffer.handle(), 0L, true);
        arrayList.clear();
        for (int i = 0; i < int32; i++) {
            SimSlotStatus simSlotStatus = new SimSlotStatus();
            simSlotStatus.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 48);
            arrayList.add(simSlotStatus);
        }
        return arrayList;
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.cardState = hwBlob.getInt32(j + 0);
        this.slotState = hwBlob.getInt32(j + 4);
        long j2 = j + 8;
        String string = hwBlob.getString(j2);
        this.atr = string;
        hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
        this.logicalSlotId = hwBlob.getInt32(j + 24);
        long j3 = j + 32;
        String string2 = hwBlob.getString(j3);
        this.iccid = string2;
        hwParcel.readEmbeddedBuffer(string2.getBytes().length + 1, hwBlob.handle(), j3 + 0, false);
    }
}
