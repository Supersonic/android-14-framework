package android.hardware.fingerprint;

import android.hardware.biometrics.ComponentInfoInternal;
import android.hardware.biometrics.SensorProperties;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FingerprintSensorProperties extends SensorProperties {
    public static final int TYPE_HOME_BUTTON = 5;
    public static final int TYPE_POWER_BUTTON = 4;
    public static final int TYPE_REAR = 1;
    public static final int TYPE_UDFPS_OPTICAL = 3;
    public static final int TYPE_UDFPS_ULTRASONIC = 2;
    public static final int TYPE_UNKNOWN = 0;
    final int mSensorType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SensorType {
    }

    public static FingerprintSensorProperties from(FingerprintSensorPropertiesInternal internalProp) {
        List<SensorProperties.ComponentInfo> componentInfo = new ArrayList<>();
        for (ComponentInfoInternal internalComp : internalProp.componentInfo) {
            componentInfo.add(SensorProperties.ComponentInfo.from(internalComp));
        }
        return new FingerprintSensorProperties(internalProp.sensorId, internalProp.sensorStrength, componentInfo, internalProp.sensorType);
    }

    public FingerprintSensorProperties(int sensorId, int sensorStrength, List<SensorProperties.ComponentInfo> componentInfo, int sensorType) {
        super(sensorId, sensorStrength, componentInfo);
        this.mSensorType = sensorType;
    }

    public int getSensorType() {
        return this.mSensorType;
    }
}
