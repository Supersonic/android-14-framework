package com.android.server.display.whitebalance;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.TypedValue;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.display.utils.AmbientFilter;
import com.android.server.display.utils.AmbientFilterFactory;
import com.android.server.display.whitebalance.AmbientSensor;
/* loaded from: classes.dex */
public class DisplayWhiteBalanceFactory {
    public static DisplayWhiteBalanceController create(Handler handler, SensorManager sensorManager, Resources resources) {
        AmbientSensor.AmbientBrightnessSensor createBrightnessSensor = createBrightnessSensor(handler, sensorManager, resources);
        AmbientFilter createBrightnessFilter = AmbientFilterFactory.createBrightnessFilter("AmbientBrightnessFilter", resources);
        AmbientSensor.AmbientColorTemperatureSensor createColorTemperatureSensor = createColorTemperatureSensor(handler, sensorManager, resources);
        DisplayWhiteBalanceController displayWhiteBalanceController = new DisplayWhiteBalanceController(createBrightnessSensor, createBrightnessFilter, createColorTemperatureSensor, AmbientFilterFactory.createColorTemperatureFilter("AmbientColorTemperatureFilter", resources), createThrottler(resources), getFloatArray(resources, 17236054), getFloatArray(resources, 17236053), getFloat(resources, 17105075), getFloatArray(resources, 17236051), getFloatArray(resources, 17236050), getFloat(resources, 17105074), getFloatArray(resources, 17236044), getFloatArray(resources, 17236047), getFloatArray(resources, 17236055), getFloatArray(resources, 17236056), resources.getBoolean(17891616));
        createBrightnessSensor.setCallbacks(displayWhiteBalanceController);
        createColorTemperatureSensor.setCallbacks(displayWhiteBalanceController);
        return displayWhiteBalanceController;
    }

    @VisibleForTesting
    public static AmbientSensor.AmbientBrightnessSensor createBrightnessSensor(Handler handler, SensorManager sensorManager, Resources resources) {
        return new AmbientSensor.AmbientBrightnessSensor(handler, sensorManager, resources.getInteger(17694816));
    }

    @VisibleForTesting
    public static AmbientSensor.AmbientColorTemperatureSensor createColorTemperatureSensor(Handler handler, SensorManager sensorManager, Resources resources) {
        return new AmbientSensor.AmbientColorTemperatureSensor(handler, sensorManager, resources.getString(17039921), resources.getInteger(17694821));
    }

    public static DisplayWhiteBalanceThrottler createThrottler(Resources resources) {
        return new DisplayWhiteBalanceThrottler(resources.getInteger(17694822), resources.getInteger(17694823), getFloatArray(resources, 17236045), getFloatArray(resources, 17236052), getFloatArray(resources, 17236046));
    }

    public static float getFloat(Resources resources, int i) {
        TypedValue typedValue = new TypedValue();
        resources.getValue(i, typedValue, true);
        if (typedValue.type != 4) {
            return Float.NaN;
        }
        return typedValue.getFloat();
    }

    public static float[] getFloatArray(Resources resources, int i) {
        TypedArray obtainTypedArray = resources.obtainTypedArray(i);
        try {
            if (obtainTypedArray.length() == 0) {
                return null;
            }
            int length = obtainTypedArray.length();
            float[] fArr = new float[length];
            for (int i2 = 0; i2 < length; i2++) {
                float f = obtainTypedArray.getFloat(i2, Float.NaN);
                fArr[i2] = f;
                if (Float.isNaN(f)) {
                    return null;
                }
            }
            return fArr;
        } finally {
            obtainTypedArray.recycle();
        }
    }
}
