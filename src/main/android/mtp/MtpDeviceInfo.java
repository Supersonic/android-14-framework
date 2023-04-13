package android.mtp;
/* loaded from: classes2.dex */
public class MtpDeviceInfo {
    private int[] mDevicePropertySupported;
    private int[] mEventsSupported;
    private String mManufacturer;
    private String mModel;
    private int[] mOperationsSupported;
    private String mSerialNumber;
    private String mVersion;

    private MtpDeviceInfo() {
    }

    public final String getManufacturer() {
        return this.mManufacturer;
    }

    public final String getModel() {
        return this.mModel;
    }

    public final String getVersion() {
        return this.mVersion;
    }

    public final String getSerialNumber() {
        return this.mSerialNumber;
    }

    public final int[] getOperationsSupported() {
        return this.mOperationsSupported;
    }

    public final int[] getEventsSupported() {
        return this.mEventsSupported;
    }

    public final int[] getDevicePropertySupported() {
        return this.mDevicePropertySupported;
    }

    public boolean isOperationSupported(int code) {
        return isSupported(this.mOperationsSupported, code);
    }

    public boolean isEventSupported(int code) {
        return isSupported(this.mEventsSupported, code);
    }

    public boolean isDevicePropertySupported(int code) {
        return isSupported(this.mDevicePropertySupported, code);
    }

    private static boolean isSupported(int[] set, int code) {
        for (int element : set) {
            if (element == code) {
                return true;
            }
        }
        return false;
    }
}
