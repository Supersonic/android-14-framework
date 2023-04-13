package android.hardware.usb.V1_2;

import android.hardware.usb.V1_1.PortStatus_1_1;
import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class PortStatus {
    public int supportedContaminantProtectionModes;
    public PortStatus_1_1 status_1_1 = new PortStatus_1_1();
    public boolean supportsEnableContaminantPresenceProtection = false;
    public int contaminantProtectionStatus = 0;
    public boolean supportsEnableContaminantPresenceDetection = false;
    public int contaminantDetectionStatus = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == PortStatus.class) {
            PortStatus portStatus = (PortStatus) obj;
            return HidlSupport.deepEquals(this.status_1_1, portStatus.status_1_1) && HidlSupport.deepEquals(Integer.valueOf(this.supportedContaminantProtectionModes), Integer.valueOf(portStatus.supportedContaminantProtectionModes)) && this.supportsEnableContaminantPresenceProtection == portStatus.supportsEnableContaminantPresenceProtection && this.contaminantProtectionStatus == portStatus.contaminantProtectionStatus && this.supportsEnableContaminantPresenceDetection == portStatus.supportsEnableContaminantPresenceDetection && this.contaminantDetectionStatus == portStatus.contaminantDetectionStatus;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.status_1_1)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.supportedContaminantProtectionModes))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.supportsEnableContaminantPresenceProtection))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.contaminantProtectionStatus))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.supportsEnableContaminantPresenceDetection))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.contaminantDetectionStatus))));
    }

    public final String toString() {
        return "{.status_1_1 = " + this.status_1_1 + ", .supportedContaminantProtectionModes = " + ContaminantProtectionMode.dumpBitfield(this.supportedContaminantProtectionModes) + ", .supportsEnableContaminantPresenceProtection = " + this.supportsEnableContaminantPresenceProtection + ", .contaminantProtectionStatus = " + ContaminantProtectionStatus.toString(this.contaminantProtectionStatus) + ", .supportsEnableContaminantPresenceDetection = " + this.supportsEnableContaminantPresenceDetection + ", .contaminantDetectionStatus = " + ContaminantDetectionStatus.toString(this.contaminantDetectionStatus) + "}";
    }

    public static final ArrayList<PortStatus> readVectorFromParcel(HwParcel hwParcel) {
        ArrayList<PortStatus> arrayList = new ArrayList<>();
        HwBlob readBuffer = hwParcel.readBuffer(16L);
        int int32 = readBuffer.getInt32(8L);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 72, readBuffer.handle(), 0L, true);
        arrayList.clear();
        for (int i = 0; i < int32; i++) {
            PortStatus portStatus = new PortStatus();
            portStatus.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 72);
            arrayList.add(portStatus);
        }
        return arrayList;
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.status_1_1.readEmbeddedFromParcel(hwParcel, hwBlob, 0 + j);
        this.supportedContaminantProtectionModes = hwBlob.getInt32(48 + j);
        this.supportsEnableContaminantPresenceProtection = hwBlob.getBool(52 + j);
        this.contaminantProtectionStatus = hwBlob.getInt32(56 + j);
        this.supportsEnableContaminantPresenceDetection = hwBlob.getBool(60 + j);
        this.contaminantDetectionStatus = hwBlob.getInt32(j + 64);
    }
}
