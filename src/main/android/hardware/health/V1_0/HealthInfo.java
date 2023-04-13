package android.hardware.health.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class HealthInfo {
    public boolean chargerAcOnline = false;
    public boolean chargerUsbOnline = false;
    public boolean chargerWirelessOnline = false;
    public int maxChargingCurrent = 0;
    public int maxChargingVoltage = 0;
    public int batteryStatus = 0;
    public int batteryHealth = 0;
    public boolean batteryPresent = false;
    public int batteryLevel = 0;
    public int batteryVoltage = 0;
    public int batteryTemperature = 0;
    public int batteryCurrent = 0;
    public int batteryCycleCount = 0;
    public int batteryFullCharge = 0;
    public int batteryChargeCounter = 0;
    public String batteryTechnology = new String();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == HealthInfo.class) {
            HealthInfo healthInfo = (HealthInfo) obj;
            return this.chargerAcOnline == healthInfo.chargerAcOnline && this.chargerUsbOnline == healthInfo.chargerUsbOnline && this.chargerWirelessOnline == healthInfo.chargerWirelessOnline && this.maxChargingCurrent == healthInfo.maxChargingCurrent && this.maxChargingVoltage == healthInfo.maxChargingVoltage && this.batteryStatus == healthInfo.batteryStatus && this.batteryHealth == healthInfo.batteryHealth && this.batteryPresent == healthInfo.batteryPresent && this.batteryLevel == healthInfo.batteryLevel && this.batteryVoltage == healthInfo.batteryVoltage && this.batteryTemperature == healthInfo.batteryTemperature && this.batteryCurrent == healthInfo.batteryCurrent && this.batteryCycleCount == healthInfo.batteryCycleCount && this.batteryFullCharge == healthInfo.batteryFullCharge && this.batteryChargeCounter == healthInfo.batteryChargeCounter && HidlSupport.deepEquals(this.batteryTechnology, healthInfo.batteryTechnology);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.chargerAcOnline))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.chargerUsbOnline))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.chargerWirelessOnline))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxChargingCurrent))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxChargingVoltage))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryStatus))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryHealth))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.batteryPresent))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryLevel))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryVoltage))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryTemperature))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryCurrent))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryCycleCount))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryFullCharge))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.batteryChargeCounter))), Integer.valueOf(HidlSupport.deepHashCode(this.batteryTechnology)));
    }

    public final String toString() {
        return "{.chargerAcOnline = " + this.chargerAcOnline + ", .chargerUsbOnline = " + this.chargerUsbOnline + ", .chargerWirelessOnline = " + this.chargerWirelessOnline + ", .maxChargingCurrent = " + this.maxChargingCurrent + ", .maxChargingVoltage = " + this.maxChargingVoltage + ", .batteryStatus = " + BatteryStatus.toString(this.batteryStatus) + ", .batteryHealth = " + BatteryHealth.toString(this.batteryHealth) + ", .batteryPresent = " + this.batteryPresent + ", .batteryLevel = " + this.batteryLevel + ", .batteryVoltage = " + this.batteryVoltage + ", .batteryTemperature = " + this.batteryTemperature + ", .batteryCurrent = " + this.batteryCurrent + ", .batteryCycleCount = " + this.batteryCycleCount + ", .batteryFullCharge = " + this.batteryFullCharge + ", .batteryChargeCounter = " + this.batteryChargeCounter + ", .batteryTechnology = " + this.batteryTechnology + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.chargerAcOnline = hwBlob.getBool(j + 0);
        this.chargerUsbOnline = hwBlob.getBool(1 + j);
        this.chargerWirelessOnline = hwBlob.getBool(2 + j);
        this.maxChargingCurrent = hwBlob.getInt32(4 + j);
        this.maxChargingVoltage = hwBlob.getInt32(8 + j);
        this.batteryStatus = hwBlob.getInt32(12 + j);
        this.batteryHealth = hwBlob.getInt32(16 + j);
        this.batteryPresent = hwBlob.getBool(20 + j);
        this.batteryLevel = hwBlob.getInt32(24 + j);
        this.batteryVoltage = hwBlob.getInt32(28 + j);
        this.batteryTemperature = hwBlob.getInt32(32 + j);
        this.batteryCurrent = hwBlob.getInt32(36 + j);
        this.batteryCycleCount = hwBlob.getInt32(40 + j);
        this.batteryFullCharge = hwBlob.getInt32(44 + j);
        this.batteryChargeCounter = hwBlob.getInt32(48 + j);
        long j2 = j + 56;
        String string = hwBlob.getString(j2);
        this.batteryTechnology = string;
        hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putBool(0 + j, this.chargerAcOnline);
        hwBlob.putBool(1 + j, this.chargerUsbOnline);
        hwBlob.putBool(2 + j, this.chargerWirelessOnline);
        hwBlob.putInt32(4 + j, this.maxChargingCurrent);
        hwBlob.putInt32(8 + j, this.maxChargingVoltage);
        hwBlob.putInt32(12 + j, this.batteryStatus);
        hwBlob.putInt32(16 + j, this.batteryHealth);
        hwBlob.putBool(20 + j, this.batteryPresent);
        hwBlob.putInt32(24 + j, this.batteryLevel);
        hwBlob.putInt32(28 + j, this.batteryVoltage);
        hwBlob.putInt32(32 + j, this.batteryTemperature);
        hwBlob.putInt32(36 + j, this.batteryCurrent);
        hwBlob.putInt32(40 + j, this.batteryCycleCount);
        hwBlob.putInt32(44 + j, this.batteryFullCharge);
        hwBlob.putInt32(48 + j, this.batteryChargeCounter);
        hwBlob.putString(j + 56, this.batteryTechnology);
    }
}
