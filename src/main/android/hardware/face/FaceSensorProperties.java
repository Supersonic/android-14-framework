package android.hardware.face;

import android.hardware.biometrics.ComponentInfoInternal;
import android.hardware.biometrics.SensorProperties;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FaceSensorProperties extends SensorProperties {
    public static final int TYPE_IR = 2;
    public static final int TYPE_RGB = 1;
    public static final int TYPE_UNKNOWN = 0;
    final int mSensorType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SensorType {
    }

    public static FaceSensorProperties from(FaceSensorPropertiesInternal internalProp) {
        List<SensorProperties.ComponentInfo> componentInfo = new ArrayList<>();
        for (ComponentInfoInternal internalComp : internalProp.componentInfo) {
            componentInfo.add(SensorProperties.ComponentInfo.from(internalComp));
        }
        return new FaceSensorProperties(internalProp.sensorId, internalProp.sensorStrength, componentInfo, internalProp.sensorType);
    }

    public FaceSensorProperties(int sensorId, int sensorStrength, List<SensorProperties.ComponentInfo> componentInfo, int sensorType) {
        super(sensorId, sensorStrength, componentInfo);
        this.mSensorType = sensorType;
    }

    public int getSensorType() {
        return this.mSensorType;
    }
}
