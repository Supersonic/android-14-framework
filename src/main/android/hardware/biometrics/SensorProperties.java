package android.hardware.biometrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SensorProperties {
    public static final int STRENGTH_CONVENIENCE = 0;
    public static final int STRENGTH_STRONG = 2;
    public static final int STRENGTH_WEAK = 1;
    private final List<ComponentInfo> mComponentInfo;
    private final int mSensorId;
    private final int mSensorStrength;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Strength {
    }

    /* loaded from: classes.dex */
    public static final class ComponentInfo {
        private final String mComponentId;
        private final String mFirmwareVersion;
        private final String mHardwareVersion;
        private final String mSerialNumber;
        private final String mSoftwareVersion;

        public ComponentInfo(String componentId, String hardwareVersion, String firmwareVersion, String serialNumber, String softwareVersion) {
            this.mComponentId = componentId;
            this.mHardwareVersion = hardwareVersion;
            this.mFirmwareVersion = firmwareVersion;
            this.mSerialNumber = serialNumber;
            this.mSoftwareVersion = softwareVersion;
        }

        public String getComponentId() {
            return this.mComponentId;
        }

        public String getHardwareVersion() {
            return this.mHardwareVersion;
        }

        public String getFirmwareVersion() {
            return this.mFirmwareVersion;
        }

        public String getSerialNumber() {
            return this.mSerialNumber;
        }

        public String getSoftwareVersion() {
            return this.mSoftwareVersion;
        }

        public static ComponentInfo from(ComponentInfoInternal internalComp) {
            return new ComponentInfo(internalComp.componentId, internalComp.hardwareVersion, internalComp.firmwareVersion, internalComp.serialNumber, internalComp.softwareVersion);
        }
    }

    public SensorProperties(int sensorId, int sensorStrength, List<ComponentInfo> componentInfo) {
        this.mSensorId = sensorId;
        this.mSensorStrength = sensorStrength;
        this.mComponentInfo = componentInfo;
    }

    public int getSensorId() {
        return this.mSensorId;
    }

    public int getSensorStrength() {
        return this.mSensorStrength;
    }

    public List<ComponentInfo> getComponentInfo() {
        return this.mComponentInfo;
    }

    public static SensorProperties from(SensorPropertiesInternal internalProp) {
        List<ComponentInfo> componentInfo = new ArrayList<>();
        for (ComponentInfoInternal internalComp : internalProp.componentInfo) {
            componentInfo.add(ComponentInfo.from(internalComp));
        }
        return new SensorProperties(internalProp.sensorId, internalProp.sensorStrength, componentInfo);
    }
}
