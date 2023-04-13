package android.hardware.usb.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class PortStatus {
    public String portName = new String();
    public int currentDataRole = 0;
    public int currentPowerRole = 0;
    public int currentMode = 0;
    public boolean canChangeMode = false;
    public boolean canChangeDataRole = false;
    public boolean canChangePowerRole = false;
    public int supportedModes = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == PortStatus.class) {
            PortStatus portStatus = (PortStatus) obj;
            return HidlSupport.deepEquals(this.portName, portStatus.portName) && this.currentDataRole == portStatus.currentDataRole && this.currentPowerRole == portStatus.currentPowerRole && this.currentMode == portStatus.currentMode && this.canChangeMode == portStatus.canChangeMode && this.canChangeDataRole == portStatus.canChangeDataRole && this.canChangePowerRole == portStatus.canChangePowerRole && this.supportedModes == portStatus.supportedModes;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.portName)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.currentDataRole))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.currentPowerRole))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.currentMode))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.canChangeMode))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.canChangeDataRole))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.canChangePowerRole))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.supportedModes))));
    }

    public final String toString() {
        return "{.portName = " + this.portName + ", .currentDataRole = " + PortDataRole.toString(this.currentDataRole) + ", .currentPowerRole = " + PortPowerRole.toString(this.currentPowerRole) + ", .currentMode = " + PortMode.toString(this.currentMode) + ", .canChangeMode = " + this.canChangeMode + ", .canChangeDataRole = " + this.canChangeDataRole + ", .canChangePowerRole = " + this.canChangePowerRole + ", .supportedModes = " + PortMode.toString(this.supportedModes) + "}";
    }

    public static final ArrayList<PortStatus> readVectorFromParcel(HwParcel hwParcel) {
        ArrayList<PortStatus> arrayList = new ArrayList<>();
        HwBlob readBuffer = hwParcel.readBuffer(16L);
        int int32 = readBuffer.getInt32(8L);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 40, readBuffer.handle(), 0L, true);
        arrayList.clear();
        for (int i = 0; i < int32; i++) {
            PortStatus portStatus = new PortStatus();
            portStatus.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 40);
            arrayList.add(portStatus);
        }
        return arrayList;
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        long j2 = j + 0;
        String string = hwBlob.getString(j2);
        this.portName = string;
        hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
        this.currentDataRole = hwBlob.getInt32(j + 16);
        this.currentPowerRole = hwBlob.getInt32(j + 20);
        this.currentMode = hwBlob.getInt32(j + 24);
        this.canChangeMode = hwBlob.getBool(j + 28);
        this.canChangeDataRole = hwBlob.getBool(j + 29);
        this.canChangePowerRole = hwBlob.getBool(j + 30);
        this.supportedModes = hwBlob.getInt32(j + 32);
    }
}
