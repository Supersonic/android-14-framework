package com.android.server.health;

import android.hardware.health.HealthInfo;
/* loaded from: classes.dex */
public class Utils {
    public static void copyV1Battery(HealthInfo healthInfo, HealthInfo healthInfo2) {
        healthInfo.chargerAcOnline = healthInfo2.chargerAcOnline;
        healthInfo.chargerUsbOnline = healthInfo2.chargerUsbOnline;
        healthInfo.chargerWirelessOnline = healthInfo2.chargerWirelessOnline;
        healthInfo.maxChargingCurrentMicroamps = healthInfo2.maxChargingCurrentMicroamps;
        healthInfo.maxChargingVoltageMicrovolts = healthInfo2.maxChargingVoltageMicrovolts;
        healthInfo.batteryStatus = healthInfo2.batteryStatus;
        healthInfo.batteryHealth = healthInfo2.batteryHealth;
        healthInfo.batteryPresent = healthInfo2.batteryPresent;
        healthInfo.batteryLevel = healthInfo2.batteryLevel;
        healthInfo.batteryVoltageMillivolts = healthInfo2.batteryVoltageMillivolts;
        healthInfo.batteryTemperatureTenthsCelsius = healthInfo2.batteryTemperatureTenthsCelsius;
        healthInfo.batteryCurrentMicroamps = healthInfo2.batteryCurrentMicroamps;
        healthInfo.batteryCycleCount = healthInfo2.batteryCycleCount;
        healthInfo.batteryFullChargeUah = healthInfo2.batteryFullChargeUah;
        healthInfo.batteryChargeCounterUah = healthInfo2.batteryChargeCounterUah;
        healthInfo.batteryTechnology = healthInfo2.batteryTechnology;
        healthInfo.batteryCurrentAverageMicroamps = healthInfo2.batteryCurrentAverageMicroamps;
        healthInfo.batteryCapacityLevel = healthInfo2.batteryCapacityLevel;
        healthInfo.batteryChargeTimeToFullNowSeconds = healthInfo2.batteryChargeTimeToFullNowSeconds;
        healthInfo.batteryFullChargeDesignCapacityUah = healthInfo2.batteryFullChargeDesignCapacityUah;
    }
}
