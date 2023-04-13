package android.hardware.radio.config.V1_1;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class PhoneCapability {
    public byte maxActiveData = 0;
    public byte maxActiveInternetData = 0;
    public boolean isInternetLingeringSupported = false;
    public ArrayList<ModemInfo> logicalModemList = new ArrayList<>();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == PhoneCapability.class) {
            PhoneCapability phoneCapability = (PhoneCapability) obj;
            return this.maxActiveData == phoneCapability.maxActiveData && this.maxActiveInternetData == phoneCapability.maxActiveInternetData && this.isInternetLingeringSupported == phoneCapability.isInternetLingeringSupported && HidlSupport.deepEquals(this.logicalModemList, phoneCapability.logicalModemList);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.maxActiveData))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.maxActiveInternetData))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isInternetLingeringSupported))), Integer.valueOf(HidlSupport.deepHashCode(this.logicalModemList)));
    }

    public final String toString() {
        return "{.maxActiveData = " + ((int) this.maxActiveData) + ", .maxActiveInternetData = " + ((int) this.maxActiveInternetData) + ", .isInternetLingeringSupported = " + this.isInternetLingeringSupported + ", .logicalModemList = " + this.logicalModemList + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(24L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.maxActiveData = hwBlob.getInt8(j + 0);
        this.maxActiveInternetData = hwBlob.getInt8(j + 1);
        this.isInternetLingeringSupported = hwBlob.getBool(j + 2);
        long j2 = j + 8;
        int int32 = hwBlob.getInt32(8 + j2);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 1, hwBlob.handle(), j2 + 0, true);
        this.logicalModemList.clear();
        for (int i = 0; i < int32; i++) {
            ModemInfo modemInfo = new ModemInfo();
            modemInfo.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 1);
            this.logicalModemList.add(modemInfo);
        }
    }
}
