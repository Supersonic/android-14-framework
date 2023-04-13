package android.companion;
/* loaded from: classes.dex */
public class DeviceNotAssociatedException extends RuntimeException {
    public DeviceNotAssociatedException(String deviceName) {
        super("Device not associated with the current app: " + deviceName);
    }
}
