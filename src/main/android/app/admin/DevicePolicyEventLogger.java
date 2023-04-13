package android.app.admin;

import android.content.ComponentName;
import android.stats.devicepolicy.nano.StringList;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.internal.util.FrameworkStatsLog;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public class DevicePolicyEventLogger {
    private String mAdminPackageName;
    private boolean mBooleanValue;
    private final int mEventId;
    private int mIntValue;
    private String[] mStringArrayValue;
    private long mTimePeriodMs;

    private DevicePolicyEventLogger(int eventId) {
        this.mEventId = eventId;
    }

    public static DevicePolicyEventLogger createEvent(int eventId) {
        return new DevicePolicyEventLogger(eventId);
    }

    public int getEventId() {
        return this.mEventId;
    }

    public DevicePolicyEventLogger setInt(int value) {
        this.mIntValue = value;
        return this;
    }

    public int getInt() {
        return this.mIntValue;
    }

    public DevicePolicyEventLogger setBoolean(boolean value) {
        this.mBooleanValue = value;
        return this;
    }

    public boolean getBoolean() {
        return this.mBooleanValue;
    }

    public DevicePolicyEventLogger setTimePeriod(long timePeriodMillis) {
        this.mTimePeriodMs = timePeriodMillis;
        return this;
    }

    public long getTimePeriod() {
        return this.mTimePeriodMs;
    }

    public DevicePolicyEventLogger setStrings(String... values) {
        this.mStringArrayValue = values;
        return this;
    }

    public DevicePolicyEventLogger setStrings(String value, String[] values) {
        Objects.requireNonNull(values, "values parameter cannot be null");
        String[] strArr = new String[values.length + 1];
        this.mStringArrayValue = strArr;
        strArr[0] = value;
        System.arraycopy(values, 0, strArr, 1, values.length);
        return this;
    }

    public DevicePolicyEventLogger setStrings(String value1, String value2, String[] values) {
        Objects.requireNonNull(values, "values parameter cannot be null");
        String[] strArr = new String[values.length + 2];
        this.mStringArrayValue = strArr;
        strArr[0] = value1;
        strArr[1] = value2;
        System.arraycopy(values, 0, strArr, 2, values.length);
        return this;
    }

    public String[] getStringArray() {
        String[] strArr = this.mStringArrayValue;
        if (strArr == null) {
            return null;
        }
        return (String[]) Arrays.copyOf(strArr, strArr.length);
    }

    public DevicePolicyEventLogger setAdmin(String packageName) {
        this.mAdminPackageName = packageName;
        return this;
    }

    public DevicePolicyEventLogger setAdmin(ComponentName componentName) {
        this.mAdminPackageName = componentName != null ? componentName.getPackageName() : null;
        return this;
    }

    public String getAdminPackageName() {
        return this.mAdminPackageName;
    }

    public void write() {
        byte[] bytes = stringArrayValueToBytes(this.mStringArrayValue);
        FrameworkStatsLog.write(103, this.mEventId, this.mAdminPackageName, this.mIntValue, this.mBooleanValue, this.mTimePeriodMs, bytes);
    }

    private static byte[] stringArrayValueToBytes(String[] array) {
        if (array == null) {
            return null;
        }
        StringList stringList = new StringList();
        stringList.stringValue = array;
        return MessageNano.toByteArray(stringList);
    }
}
