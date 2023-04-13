package android.app.admin;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemProperties;
import android.p008os.UserHandle;
import android.util.EventLog;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
/* loaded from: classes.dex */
public class SecurityLog {
    public static final int LEVEL_ERROR = 3;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_WARNING = 2;
    private static final String PROPERTY_LOGGING_ENABLED = "persist.logd.security";
    public static final int TAG_ADB_SHELL_CMD = 210002;
    public static final int TAG_ADB_SHELL_INTERACTIVE = 210001;
    public static final int TAG_APP_PROCESS_START = 210005;
    public static final int TAG_BLUETOOTH_CONNECTION = 210039;
    public static final int TAG_BLUETOOTH_DISCONNECTION = 210040;
    public static final int TAG_CAMERA_POLICY_SET = 210034;
    public static final int TAG_CERT_AUTHORITY_INSTALLED = 210029;
    public static final int TAG_CERT_AUTHORITY_REMOVED = 210030;
    public static final int TAG_CERT_VALIDATION_FAILURE = 210033;
    public static final int TAG_CRYPTO_SELF_TEST_COMPLETED = 210031;
    public static final int TAG_KEYGUARD_DISABLED_FEATURES_SET = 210021;
    public static final int TAG_KEYGUARD_DISMISSED = 210006;
    public static final int TAG_KEYGUARD_DISMISS_AUTH_ATTEMPT = 210007;
    public static final int TAG_KEYGUARD_SECURED = 210008;
    public static final int TAG_KEY_DESTRUCTION = 210026;
    public static final int TAG_KEY_GENERATED = 210024;
    public static final int TAG_KEY_IMPORT = 210025;
    public static final int TAG_KEY_INTEGRITY_VIOLATION = 210032;
    public static final int TAG_LOGGING_STARTED = 210011;
    public static final int TAG_LOGGING_STOPPED = 210012;
    public static final int TAG_LOG_BUFFER_SIZE_CRITICAL = 210015;
    public static final int TAG_MAX_PASSWORD_ATTEMPTS_SET = 210020;
    public static final int TAG_MAX_SCREEN_LOCK_TIMEOUT_SET = 210019;
    public static final int TAG_MEDIA_MOUNT = 210013;
    public static final int TAG_MEDIA_UNMOUNT = 210014;
    public static final int TAG_OS_SHUTDOWN = 210010;
    public static final int TAG_OS_STARTUP = 210009;
    public static final int TAG_PACKAGE_INSTALLED = 210041;
    public static final int TAG_PACKAGE_UNINSTALLED = 210043;
    public static final int TAG_PACKAGE_UPDATED = 210042;
    public static final int TAG_PASSWORD_CHANGED = 210036;
    public static final int TAG_PASSWORD_COMPLEXITY_REQUIRED = 210035;
    public static final int TAG_PASSWORD_COMPLEXITY_SET = 210017;
    public static final int TAG_PASSWORD_EXPIRATION_SET = 210016;
    public static final int TAG_PASSWORD_HISTORY_LENGTH_SET = 210018;
    public static final int TAG_REMOTE_LOCK = 210022;
    public static final int TAG_SYNC_RECV_FILE = 210003;
    public static final int TAG_SYNC_SEND_FILE = 210004;
    public static final int TAG_USER_RESTRICTION_ADDED = 210027;
    public static final int TAG_USER_RESTRICTION_REMOVED = 210028;
    public static final int TAG_WIFI_CONNECTION = 210037;
    public static final int TAG_WIFI_DISCONNECTION = 210038;
    public static final int TAG_WIPE_FAILURE = 210023;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SecurityLogLevel {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SecurityLogTag {
    }

    public static native boolean isLoggingEnabled();

    public static native void readEvents(Collection<SecurityEvent> collection) throws IOException;

    public static native void readEventsOnWrapping(long j, Collection<SecurityEvent> collection) throws IOException;

    public static native void readEventsSince(long j, Collection<SecurityEvent> collection) throws IOException;

    public static native void readPreviousEvents(Collection<SecurityEvent> collection) throws IOException;

    @SystemApi
    public static native int writeEvent(int i, Object... objArr);

    public static void setLoggingEnabledProperty(boolean enabled) {
        SystemProperties.set(PROPERTY_LOGGING_ENABLED, enabled ? "true" : "false");
    }

    public static boolean getLoggingEnabledProperty() {
        return SystemProperties.getBoolean(PROPERTY_LOGGING_ENABLED, false);
    }

    /* loaded from: classes.dex */
    public static final class SecurityEvent implements Parcelable {
        public static final Parcelable.Creator<SecurityEvent> CREATOR = new Parcelable.Creator<SecurityEvent>() { // from class: android.app.admin.SecurityLog.SecurityEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SecurityEvent createFromParcel(Parcel source) {
                return new SecurityEvent(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SecurityEvent[] newArray(int size) {
                return new SecurityEvent[size];
            }
        };
        private EventLog.Event mEvent;
        private long mId;

        SecurityEvent(byte[] data) {
            this(0L, data);
        }

        SecurityEvent(Parcel source) {
            this(source.readLong(), source.createByteArray());
        }

        public SecurityEvent(long id, byte[] data) {
            this.mId = id;
            this.mEvent = EventLog.Event.fromBytes(data);
        }

        public long getTimeNanos() {
            return this.mEvent.getTimeNanos();
        }

        public int getTag() {
            return this.mEvent.getTag();
        }

        public Object getData() {
            return this.mEvent.getData();
        }

        public int getIntegerData(int index) {
            return ((Integer) ((Object[]) this.mEvent.getData())[index]).intValue();
        }

        public String getStringData(int index) {
            return (String) ((Object[]) this.mEvent.getData())[index];
        }

        public void setId(long id) {
            this.mId = id;
        }

        public long getId() {
            return this.mId;
        }

        public int getLogLevel() {
            switch (getTag()) {
                case 210001:
                case 210002:
                case 210003:
                case 210004:
                case 210005:
                case 210006:
                case 210008:
                case 210009:
                case 210010:
                case 210011:
                case 210012:
                case 210013:
                case 210014:
                case 210016:
                case 210017:
                case 210018:
                case 210019:
                case 210020:
                case 210027:
                case 210028:
                case 210034:
                case 210035:
                case 210036:
                    return 1;
                case 210007:
                case 210024:
                case 210025:
                case 210026:
                case 210029:
                    return getSuccess() ? 1 : 2;
                case 210015:
                case 210023:
                case 210032:
                    return 3;
                case 210021:
                case 210022:
                default:
                    return 1;
                case 210030:
                case 210031:
                    return getSuccess() ? 1 : 3;
                case 210033:
                    return 2;
            }
        }

        private boolean getSuccess() {
            Object data = getData();
            if (data == null || !(data instanceof Object[])) {
                return false;
            }
            Object[] array = (Object[]) data;
            return array.length >= 1 && (array[0] instanceof Integer) && ((Integer) array[0]).intValue() != 0;
        }

        public SecurityEvent redact(int accessingUser) {
            int userId;
            switch (getTag()) {
                case 210002:
                    return new SecurityEvent(getId(), this.mEvent.withNewData("").getBytes());
                case 210005:
                    try {
                        userId = UserHandle.getUserId(getIntegerData(2));
                        break;
                    } catch (Exception e) {
                        return null;
                    }
                case 210013:
                case 210014:
                    try {
                        String mountPoint = getStringData(0);
                        return new SecurityEvent(getId(), this.mEvent.withNewData(new Object[]{mountPoint, ""}).getBytes());
                    } catch (Exception e2) {
                        return null;
                    }
                case 210024:
                case 210025:
                case 210026:
                    try {
                        userId = UserHandle.getUserId(getIntegerData(2));
                        break;
                    } catch (Exception e3) {
                        return null;
                    }
                case 210029:
                case 210030:
                case 210041:
                case 210042:
                case 210043:
                    try {
                        userId = getIntegerData(2);
                        break;
                    } catch (Exception e4) {
                        return null;
                    }
                case 210032:
                    try {
                        userId = UserHandle.getUserId(getIntegerData(1));
                        break;
                    } catch (Exception e5) {
                        return null;
                    }
                case 210036:
                    try {
                        userId = getIntegerData(1);
                        break;
                    } catch (Exception e6) {
                        return null;
                    }
                default:
                    userId = -10000;
                    break;
            }
            if (userId == -10000 || accessingUser == userId) {
                return this;
            }
            return null;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.mId);
            dest.writeByteArray(this.mEvent.getBytes());
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SecurityEvent other = (SecurityEvent) o;
            if (this.mEvent.equals(other.mEvent) && this.mId == other.mId) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mEvent, Long.valueOf(this.mId));
        }

        public boolean eventEquals(SecurityEvent other) {
            return other != null && this.mEvent.equals(other.mEvent);
        }
    }

    public static void redactEvents(ArrayList<SecurityEvent> logList, int accessingUser) {
        if (accessingUser == -1) {
            return;
        }
        int end = 0;
        for (int i = 0; i < logList.size(); i++) {
            SecurityEvent event = logList.get(i).redact(accessingUser);
            if (event != null) {
                logList.set(end, event);
                end++;
            }
        }
        int i2 = logList.size();
        for (int i3 = i2 - 1; i3 >= end; i3--) {
            logList.remove(i3);
        }
    }
}
