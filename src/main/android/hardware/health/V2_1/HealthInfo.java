package android.hardware.health.V2_1;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class HealthInfo {
    public android.hardware.health.V2_0.HealthInfo legacy = new android.hardware.health.V2_0.HealthInfo();
    public int batteryCapacityLevel = 0;
    public long batteryChargeTimeToFullNowSeconds = 0;
    public int batteryFullChargeDesignCapacityUah = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == HealthInfo.class) {
            HealthInfo healthInfo = (HealthInfo) obj;
            return HidlSupport.deepEquals(this.legacy, healthInfo.legacy) && this.batteryCapacityLevel == healthInfo.batteryCapacityLevel && this.batteryChargeTimeToFullNowSeconds == healthInfo.batteryChargeTimeToFullNowSeconds && this.batteryFullChargeDesignCapacityUah == healthInfo.batteryFullChargeDesignCapacityUah;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.legacy)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryCapacityLevel))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.batteryChargeTimeToFullNowSeconds))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryFullChargeDesignCapacityUah))));
    }

    public final String toString() {
        return "{.legacy = " + this.legacy + ", .batteryCapacityLevel = " + BatteryCapacityLevel.toString(this.batteryCapacityLevel) + ", .batteryChargeTimeToFullNowSeconds = " + this.batteryChargeTimeToFullNowSeconds + ", .batteryFullChargeDesignCapacityUah = " + this.batteryFullChargeDesignCapacityUah + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(136L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.legacy.readEmbeddedFromParcel(hwParcel, hwBlob, 0 + j);
        this.batteryCapacityLevel = hwBlob.getInt32(112 + j);
        this.batteryChargeTimeToFullNowSeconds = hwBlob.getInt64(120 + j);
        this.batteryFullChargeDesignCapacityUah = hwBlob.getInt32(j + 128);
    }
}
