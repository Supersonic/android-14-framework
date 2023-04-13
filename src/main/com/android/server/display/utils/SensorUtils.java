package com.android.server.display.utils;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.text.TextUtils;
/* loaded from: classes.dex */
public class SensorUtils {
    public static Sensor findSensor(SensorManager sensorManager, String str, String str2, int i) {
        if (sensorManager == null) {
            return null;
        }
        if ("".equals(str2) && "".equals(str)) {
            return null;
        }
        boolean z = !TextUtils.isEmpty(str2);
        boolean z2 = !TextUtils.isEmpty(str);
        if (z || z2) {
            for (Sensor sensor : sensorManager.getSensorList(-1)) {
                if (!z || str2.equals(sensor.getName())) {
                    if (!z2 || str.equals(sensor.getStringType())) {
                        return sensor;
                    }
                }
            }
        }
        if (i != 0) {
            return sensorManager.getDefaultSensor(i);
        }
        return null;
    }
}
