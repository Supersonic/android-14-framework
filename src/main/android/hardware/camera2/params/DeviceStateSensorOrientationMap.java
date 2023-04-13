package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
/* loaded from: classes.dex */
public final class DeviceStateSensorOrientationMap {
    public static final long FOLDED = 4;
    public static final long NORMAL = 0;
    private final HashMap<Long, Integer> mDeviceStateOrientationMap;
    private final long[] mElements;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DeviceState {
    }

    public DeviceStateSensorOrientationMap(long[] elements) {
        this.mElements = (long[]) Objects.requireNonNull(elements, "elements must not be null");
        this.mDeviceStateOrientationMap = new HashMap<>();
        if (elements.length % 2 != 0) {
            throw new IllegalArgumentException("Device state sensor orientation map length " + elements.length + " is not even!");
        }
        for (int i = 0; i < elements.length; i += 2) {
            if (elements[i + 1] % 90 != 0) {
                throw new IllegalArgumentException("Sensor orientation not divisible by 90: " + elements[i + 1]);
            }
            this.mDeviceStateOrientationMap.put(Long.valueOf(elements[i]), Integer.valueOf(Math.toIntExact(elements[i + 1])));
        }
    }

    private DeviceStateSensorOrientationMap(ArrayList<Long> elements, HashMap<Long, Integer> deviceStateOrientationMap) {
        this.mElements = new long[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            this.mElements[i] = elements.get(i).longValue();
        }
        this.mDeviceStateOrientationMap = deviceStateOrientationMap;
    }

    public int getSensorOrientation(long deviceState) {
        if (!this.mDeviceStateOrientationMap.containsKey(Long.valueOf(deviceState))) {
            throw new IllegalArgumentException("Invalid device state: " + deviceState);
        }
        return this.mDeviceStateOrientationMap.get(Long.valueOf(deviceState)).intValue();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DeviceStateSensorOrientationMap)) {
            return false;
        }
        DeviceStateSensorOrientationMap other = (DeviceStateSensorOrientationMap) obj;
        return Arrays.equals(this.mElements, other.mElements);
    }

    public int hashCode() {
        return HashCodeHelpers.hashCodeGeneric(this.mElements);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private final ArrayList<Long> mElements = new ArrayList<>();
        private final HashMap<Long, Integer> mDeviceStateOrientationMap = new HashMap<>();

        public Builder addOrientationForState(long deviceState, long angle) {
            if (angle % 90 != 0) {
                throw new IllegalArgumentException("Sensor orientation not divisible by 90: " + angle);
            }
            this.mDeviceStateOrientationMap.put(Long.valueOf(deviceState), Integer.valueOf(Math.toIntExact(angle)));
            this.mElements.add(Long.valueOf(deviceState));
            this.mElements.add(Long.valueOf(angle));
            return this;
        }

        public DeviceStateSensorOrientationMap build() {
            if (this.mElements.size() == 0) {
                throw new IllegalStateException("Cannot build a DeviceStateSensorOrientationMap with zero elements.");
            }
            return new DeviceStateSensorOrientationMap(this.mElements, this.mDeviceStateOrientationMap);
        }
    }
}
