package android.service.timezone;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@SystemApi
/* loaded from: classes3.dex */
public final class TimeZoneProviderStatus implements Parcelable {
    public static final Parcelable.Creator<TimeZoneProviderStatus> CREATOR = new Parcelable.Creator<TimeZoneProviderStatus>() { // from class: android.service.timezone.TimeZoneProviderStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneProviderStatus createFromParcel(Parcel in) {
            int locationDetectionStatus = in.readInt();
            int connectivityStatus = in.readInt();
            int timeZoneResolutionStatus = in.readInt();
            return new TimeZoneProviderStatus(locationDetectionStatus, connectivityStatus, timeZoneResolutionStatus);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneProviderStatus[] newArray(int size) {
            return new TimeZoneProviderStatus[size];
        }
    };
    public static final int DEPENDENCY_STATUS_BLOCKED_BY_ENVIRONMENT = 4;
    public static final int DEPENDENCY_STATUS_BLOCKED_BY_SETTINGS = 6;
    public static final int DEPENDENCY_STATUS_DEGRADED_BY_SETTINGS = 5;
    public static final int DEPENDENCY_STATUS_NOT_APPLICABLE = 1;
    public static final int DEPENDENCY_STATUS_OK = 2;
    public static final int DEPENDENCY_STATUS_TEMPORARILY_UNAVAILABLE = 3;
    public static final int DEPENDENCY_STATUS_UNKNOWN = 0;
    public static final int OPERATION_STATUS_FAILED = 3;
    public static final int OPERATION_STATUS_NOT_APPLICABLE = 1;
    public static final int OPERATION_STATUS_OK = 2;
    public static final int OPERATION_STATUS_UNKNOWN = 0;
    private final int mConnectivityDependencyStatus;
    private final int mLocationDetectionDependencyStatus;
    private final int mTimeZoneResolutionOperationStatus;

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DependencyStatus {
    }

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface OperationStatus {
    }

    private TimeZoneProviderStatus(int locationDetectionStatus, int connectivityStatus, int timeZoneResolutionStatus) {
        this.mLocationDetectionDependencyStatus = locationDetectionStatus;
        this.mConnectivityDependencyStatus = connectivityStatus;
        this.mTimeZoneResolutionOperationStatus = timeZoneResolutionStatus;
    }

    public int getLocationDetectionDependencyStatus() {
        return this.mLocationDetectionDependencyStatus;
    }

    public int getConnectivityDependencyStatus() {
        return this.mConnectivityDependencyStatus;
    }

    public int getTimeZoneResolutionOperationStatus() {
        return this.mTimeZoneResolutionOperationStatus;
    }

    public String toString() {
        return "TimeZoneProviderStatus{mLocationDetectionDependencyStatus=" + dependencyStatusToString(this.mLocationDetectionDependencyStatus) + ", mConnectivityDependencyStatus=" + dependencyStatusToString(this.mConnectivityDependencyStatus) + ", mTimeZoneResolutionOperationStatus=" + operationStatusToString(this.mTimeZoneResolutionOperationStatus) + '}';
    }

    public static TimeZoneProviderStatus parseProviderStatus(String arg) {
        Pattern pattern = Pattern.compile("TimeZoneProviderStatus\\{mLocationDetectionDependencyStatus=([^,]+), mConnectivityDependencyStatus=([^,]+), mTimeZoneResolutionOperationStatus=([^\\}]+)\\}");
        Matcher matcher = pattern.matcher(arg);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unable to parse provider status: " + arg);
        }
        int locationDependencyStatus = dependencyStatusFromString(matcher.group(1));
        int connectivityDependencyStatus = dependencyStatusFromString(matcher.group(2));
        int timeZoneResolutionOperationStatus = operationStatusFromString(matcher.group(3));
        return new TimeZoneProviderStatus(locationDependencyStatus, connectivityDependencyStatus, timeZoneResolutionOperationStatus);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mLocationDetectionDependencyStatus);
        parcel.writeInt(this.mConnectivityDependencyStatus);
        parcel.writeInt(this.mTimeZoneResolutionOperationStatus);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeZoneProviderStatus that = (TimeZoneProviderStatus) o;
        if (this.mLocationDetectionDependencyStatus == that.mLocationDetectionDependencyStatus && this.mConnectivityDependencyStatus == that.mConnectivityDependencyStatus && this.mTimeZoneResolutionOperationStatus == that.mTimeZoneResolutionOperationStatus) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mLocationDetectionDependencyStatus), Integer.valueOf(this.mConnectivityDependencyStatus), Integer.valueOf(this.mTimeZoneResolutionOperationStatus));
    }

    public boolean couldEnableTelephonyFallback() {
        int i;
        int i2 = this.mLocationDetectionDependencyStatus;
        return i2 == 4 || i2 == 6 || (i = this.mConnectivityDependencyStatus) == 4 || i == 6;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mConnectivityDependencyStatus;
        private int mLocationDetectionDependencyStatus;
        private int mTimeZoneResolutionOperationStatus;

        public Builder() {
            this.mLocationDetectionDependencyStatus = 0;
            this.mConnectivityDependencyStatus = 0;
            this.mTimeZoneResolutionOperationStatus = 0;
        }

        public Builder(TimeZoneProviderStatus toCopy) {
            this.mLocationDetectionDependencyStatus = 0;
            this.mConnectivityDependencyStatus = 0;
            this.mTimeZoneResolutionOperationStatus = 0;
            this.mLocationDetectionDependencyStatus = toCopy.mLocationDetectionDependencyStatus;
            this.mConnectivityDependencyStatus = toCopy.mConnectivityDependencyStatus;
            this.mTimeZoneResolutionOperationStatus = toCopy.mTimeZoneResolutionOperationStatus;
        }

        public Builder setLocationDetectionDependencyStatus(int locationDetectionStatus) {
            this.mLocationDetectionDependencyStatus = locationDetectionStatus;
            return this;
        }

        public Builder setConnectivityDependencyStatus(int connectivityStatus) {
            this.mConnectivityDependencyStatus = connectivityStatus;
            return this;
        }

        public Builder setTimeZoneResolutionOperationStatus(int timeZoneResolutionStatus) {
            this.mTimeZoneResolutionOperationStatus = timeZoneResolutionStatus;
            return this;
        }

        public TimeZoneProviderStatus build() {
            return new TimeZoneProviderStatus(TimeZoneProviderStatus.requireValidDependencyStatus(this.mLocationDetectionDependencyStatus), TimeZoneProviderStatus.requireValidDependencyStatus(this.mConnectivityDependencyStatus), TimeZoneProviderStatus.requireValidOperationStatus(this.mTimeZoneResolutionOperationStatus));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int requireValidOperationStatus(int operationStatus) {
        if (operationStatus < 0 || operationStatus > 3) {
            throw new IllegalArgumentException(Integer.toString(operationStatus));
        }
        return operationStatus;
    }

    public static String operationStatusToString(int operationStatus) {
        switch (operationStatus) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "NOT_APPLICABLE";
            case 2:
                return "OK";
            case 3:
                return "FAILED";
            default:
                throw new IllegalArgumentException("Unknown status: " + operationStatus);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int operationStatusFromString(String operationStatusString) {
        char c;
        if (TextUtils.isEmpty(operationStatusString)) {
            throw new IllegalArgumentException("Empty status: " + operationStatusString);
        }
        switch (operationStatusString.hashCode()) {
            case 2524:
                if (operationStatusString.equals("OK")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 433141802:
                if (operationStatusString.equals("UNKNOWN")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 978028715:
                if (operationStatusString.equals("NOT_APPLICABLE")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 2066319421:
                if (operationStatusString.equals("FAILED")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown status: " + operationStatusString);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int requireValidDependencyStatus(int dependencyStatus) {
        if (dependencyStatus < 0 || dependencyStatus > 6) {
            throw new IllegalArgumentException(Integer.toString(dependencyStatus));
        }
        return dependencyStatus;
    }

    public static String dependencyStatusToString(int dependencyStatus) {
        switch (dependencyStatus) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "NOT_APPLICABLE";
            case 2:
                return "OK";
            case 3:
                return "TEMPORARILY_UNAVAILABLE";
            case 4:
                return "BLOCKED_BY_ENVIRONMENT";
            case 5:
                return "DEGRADED_BY_SETTINGS";
            case 6:
                return "BLOCKED_BY_SETTINGS";
            default:
                throw new IllegalArgumentException("Unknown status: " + dependencyStatus);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int dependencyStatusFromString(String dependencyStatusString) {
        char c;
        if (TextUtils.isEmpty(dependencyStatusString)) {
            throw new IllegalArgumentException("Empty status: " + dependencyStatusString);
        }
        switch (dependencyStatusString.hashCode()) {
            case -1834303208:
                if (dependencyStatusString.equals("BLOCKED_BY_SETTINGS")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1113872161:
                if (dependencyStatusString.equals("TEMPORARILY_UNAVAILABLE")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -822505250:
                if (dependencyStatusString.equals("BLOCKED_BY_ENVIRONMENT")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 2524:
                if (dependencyStatusString.equals("OK")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 433141802:
                if (dependencyStatusString.equals("UNKNOWN")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 978028715:
                if (dependencyStatusString.equals("NOT_APPLICABLE")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1705468794:
                if (dependencyStatusString.equals("DEGRADED_BY_SETTINGS")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            default:
                throw new IllegalArgumentException("Unknown status: " + dependencyStatusString);
        }
    }
}
