package android.hardware.p002tv.cec.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* renamed from: android.hardware.tv.cec.V1_0.HdmiPortInfo */
/* loaded from: classes.dex */
public final class HdmiPortInfo {
    public int type = 0;
    public int portId = 0;
    public boolean cecSupported = false;
    public boolean arcSupported = false;
    public short physicalAddress = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == HdmiPortInfo.class) {
            HdmiPortInfo hdmiPortInfo = (HdmiPortInfo) obj;
            return this.type == hdmiPortInfo.type && this.portId == hdmiPortInfo.portId && this.cecSupported == hdmiPortInfo.cecSupported && this.arcSupported == hdmiPortInfo.arcSupported && this.physicalAddress == hdmiPortInfo.physicalAddress;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.portId))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.cecSupported))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.arcSupported))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.physicalAddress))));
    }

    public final String toString() {
        return "{.type = " + HdmiPortType.toString(this.type) + ", .portId = " + this.portId + ", .cecSupported = " + this.cecSupported + ", .arcSupported = " + this.arcSupported + ", .physicalAddress = " + ((int) this.physicalAddress) + "}";
    }

    public static final ArrayList<HdmiPortInfo> readVectorFromParcel(HwParcel hwParcel) {
        ArrayList<HdmiPortInfo> arrayList = new ArrayList<>();
        HwBlob readBuffer = hwParcel.readBuffer(16L);
        int int32 = readBuffer.getInt32(8L);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 12, readBuffer.handle(), 0L, true);
        arrayList.clear();
        for (int i = 0; i < int32; i++) {
            HdmiPortInfo hdmiPortInfo = new HdmiPortInfo();
            hdmiPortInfo.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 12);
            arrayList.add(hdmiPortInfo);
        }
        return arrayList;
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.type = hwBlob.getInt32(0 + j);
        this.portId = hwBlob.getInt32(4 + j);
        this.cecSupported = hwBlob.getBool(8 + j);
        this.arcSupported = hwBlob.getBool(9 + j);
        this.physicalAddress = hwBlob.getInt16(j + 10);
    }

    public static final void writeVectorToParcel(HwParcel hwParcel, ArrayList<HdmiPortInfo> arrayList) {
        HwBlob hwBlob = new HwBlob(16);
        int size = arrayList.size();
        hwBlob.putInt32(8L, size);
        hwBlob.putBool(12L, false);
        HwBlob hwBlob2 = new HwBlob(size * 12);
        for (int i = 0; i < size; i++) {
            arrayList.get(i).writeEmbeddedToBlob(hwBlob2, i * 12);
        }
        hwBlob.putBlob(0L, hwBlob2);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putInt32(0 + j, this.type);
        hwBlob.putInt32(4 + j, this.portId);
        hwBlob.putBool(8 + j, this.cecSupported);
        hwBlob.putBool(9 + j, this.arcSupported);
        hwBlob.putInt16(j + 10, this.physicalAddress);
    }
}
