package android.companion;

import android.bluetooth.BluetoothDevice;
import android.net.wifi.ScanResult;
import android.p008os.ParcelUuid;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class BluetoothDeviceFilterUtils {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "CDM_BluetoothDeviceFilterUtils";

    private BluetoothDeviceFilterUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String patternToString(Pattern p) {
        if (p == null) {
            return null;
        }
        return p.pattern();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Pattern patternFromString(String s) {
        if (s == null) {
            return null;
        }
        return Pattern.compile(s);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean matchesAddress(String deviceAddress, BluetoothDevice device) {
        return deviceAddress == null || (device != null && deviceAddress.equals(device.getAddress()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean matchesServiceUuids(List<ParcelUuid> serviceUuids, List<ParcelUuid> serviceUuidMasks, BluetoothDevice device) {
        for (int i = 0; i < serviceUuids.size(); i++) {
            ParcelUuid uuid = serviceUuids.get(i);
            ParcelUuid uuidMask = serviceUuidMasks.get(i);
            if (!matchesServiceUuid(uuid, uuidMask, device)) {
                return false;
            }
        }
        return true;
    }

    static boolean matchesServiceUuid(ParcelUuid serviceUuid, ParcelUuid serviceUuidMask, BluetoothDevice device) {
        boolean result = false;
        List<ParcelUuid> deviceUuids = device.getUuids() == null ? Collections.emptyList() : Arrays.asList(device.getUuids());
        if (serviceUuid == null) {
            return true;
        }
        for (ParcelUuid parcelUuid : deviceUuids) {
            UUID uuidMask = serviceUuidMask == null ? null : serviceUuidMask.getUuid();
            if (uuidsMaskedEquals(parcelUuid.getUuid(), serviceUuid.getUuid(), uuidMask)) {
                result = true;
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean matchesName(Pattern namePattern, BluetoothDevice device) {
        if (namePattern == null) {
            return true;
        }
        if (device == null) {
            return false;
        }
        String name = device.getName();
        boolean result = name != null && namePattern.matcher(name).find();
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean matchesName(Pattern namePattern, ScanResult device) {
        if (namePattern == null) {
            return true;
        }
        if (device == null) {
            return false;
        }
        String name = device.SSID;
        boolean result = name != null && namePattern.matcher(name).find();
        return result;
    }

    private static void debugLogMatchResult(boolean result, BluetoothDevice device, Object criteria) {
        Log.m108i(LOG_TAG, getDeviceDisplayNameInternal(device) + (result ? " ~ " : " !~ ") + criteria);
    }

    private static void debugLogMatchResult(boolean result, ScanResult device, Object criteria) {
        Log.m108i(LOG_TAG, getDeviceDisplayNameInternal(device) + (result ? " ~ " : " !~ ") + criteria);
    }

    public static String getDeviceDisplayNameInternal(BluetoothDevice device) {
        return TextUtils.firstNotEmpty(device.getAlias(), device.getAddress());
    }

    public static String getDeviceDisplayNameInternal(ScanResult device) {
        return TextUtils.firstNotEmpty(device.SSID, device.BSSID);
    }

    public static String getDeviceMacAddress(Parcelable device) {
        if (device instanceof BluetoothDevice) {
            return ((BluetoothDevice) device).getAddress();
        }
        if (device instanceof ScanResult) {
            return ((ScanResult) device).BSSID;
        }
        if (device instanceof android.bluetooth.le.ScanResult) {
            return getDeviceMacAddress(((android.bluetooth.le.ScanResult) device).getDevice());
        }
        throw new IllegalArgumentException("Unknown device type: " + device);
    }

    public static boolean uuidsMaskedEquals(UUID data, UUID uuid, UUID mask) {
        if (mask == null) {
            return Objects.equals(data, uuid);
        }
        return (data.getLeastSignificantBits() & mask.getLeastSignificantBits()) == (uuid.getLeastSignificantBits() & mask.getLeastSignificantBits()) && (data.getMostSignificantBits() & mask.getMostSignificantBits()) == (uuid.getMostSignificantBits() & mask.getMostSignificantBits());
    }
}
