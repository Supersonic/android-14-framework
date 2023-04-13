package android.app.time;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.service.timezone.TimeZoneProviderStatus;
import android.text.TextUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class LocationTimeZoneAlgorithmStatus implements Parcelable {
    public static final int PROVIDER_STATUS_IS_CERTAIN = 3;
    public static final int PROVIDER_STATUS_IS_UNCERTAIN = 4;
    public static final int PROVIDER_STATUS_NOT_PRESENT = 1;
    public static final int PROVIDER_STATUS_NOT_READY = 2;
    private final TimeZoneProviderStatus mPrimaryProviderReportedStatus;
    private final int mPrimaryProviderStatus;
    private final TimeZoneProviderStatus mSecondaryProviderReportedStatus;
    private final int mSecondaryProviderStatus;
    private final int mStatus;
    public static final LocationTimeZoneAlgorithmStatus NOT_SUPPORTED = new LocationTimeZoneAlgorithmStatus(1, 1, null, 1, null);
    public static final LocationTimeZoneAlgorithmStatus RUNNING_NOT_REPORTED = new LocationTimeZoneAlgorithmStatus(2, 2, null, 2, null);
    public static final LocationTimeZoneAlgorithmStatus NOT_RUNNING = new LocationTimeZoneAlgorithmStatus(2, 2, null, 2, null);
    public static final Parcelable.Creator<LocationTimeZoneAlgorithmStatus> CREATOR = new Parcelable.Creator<LocationTimeZoneAlgorithmStatus>() { // from class: android.app.time.LocationTimeZoneAlgorithmStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LocationTimeZoneAlgorithmStatus createFromParcel(Parcel in) {
            int algorithmStatus = in.readInt();
            int primaryProviderStatus = in.readInt();
            TimeZoneProviderStatus primaryProviderReportedStatus = (TimeZoneProviderStatus) in.readParcelable(getClass().getClassLoader(), TimeZoneProviderStatus.class);
            int secondaryProviderStatus = in.readInt();
            TimeZoneProviderStatus secondaryProviderReportedStatus = (TimeZoneProviderStatus) in.readParcelable(getClass().getClassLoader(), TimeZoneProviderStatus.class);
            return new LocationTimeZoneAlgorithmStatus(algorithmStatus, primaryProviderStatus, primaryProviderReportedStatus, secondaryProviderStatus, secondaryProviderReportedStatus);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LocationTimeZoneAlgorithmStatus[] newArray(int size) {
            return new LocationTimeZoneAlgorithmStatus[size];
        }
    };

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ProviderStatus {
    }

    public LocationTimeZoneAlgorithmStatus(int status, int primaryProviderStatus, TimeZoneProviderStatus primaryProviderReportedStatus, int secondaryProviderStatus, TimeZoneProviderStatus secondaryProviderReportedStatus) {
        this.mStatus = DetectorStatusTypes.requireValidDetectionAlgorithmStatus(status);
        this.mPrimaryProviderStatus = requireValidProviderStatus(primaryProviderStatus);
        this.mPrimaryProviderReportedStatus = primaryProviderReportedStatus;
        this.mSecondaryProviderStatus = requireValidProviderStatus(secondaryProviderStatus);
        this.mSecondaryProviderReportedStatus = secondaryProviderReportedStatus;
        boolean primaryProviderHasReported = hasProviderReported(primaryProviderStatus);
        boolean primaryProviderReportedStatusPresent = primaryProviderReportedStatus != null;
        if (!primaryProviderHasReported && primaryProviderReportedStatusPresent) {
            throw new IllegalArgumentException("primaryProviderReportedStatus=" + primaryProviderReportedStatus + ", primaryProviderStatus=" + providerStatusToString(primaryProviderStatus));
        }
        boolean secondaryProviderHasReported = hasProviderReported(secondaryProviderStatus);
        boolean secondaryProviderReportedStatusPresent = secondaryProviderReportedStatus != null;
        if (!secondaryProviderHasReported && secondaryProviderReportedStatusPresent) {
            throw new IllegalArgumentException("secondaryProviderReportedStatus=" + secondaryProviderReportedStatus + ", secondaryProviderStatus=" + providerStatusToString(secondaryProviderStatus));
        }
        if (status != 3) {
            if (primaryProviderHasReported || secondaryProviderHasReported) {
                throw new IllegalArgumentException("algorithmStatus=" + DetectorStatusTypes.detectionAlgorithmStatusToString(status) + ", primaryProviderReportedStatus=" + primaryProviderReportedStatus + ", secondaryProviderReportedStatus=" + secondaryProviderReportedStatus);
            }
        }
    }

    public int getStatus() {
        return this.mStatus;
    }

    public int getPrimaryProviderStatus() {
        return this.mPrimaryProviderStatus;
    }

    public TimeZoneProviderStatus getPrimaryProviderReportedStatus() {
        return this.mPrimaryProviderReportedStatus;
    }

    public int getSecondaryProviderStatus() {
        return this.mSecondaryProviderStatus;
    }

    public TimeZoneProviderStatus getSecondaryProviderReportedStatus() {
        return this.mSecondaryProviderReportedStatus;
    }

    public String toString() {
        return "LocationTimeZoneAlgorithmStatus{mAlgorithmStatus=" + DetectorStatusTypes.detectionAlgorithmStatusToString(this.mStatus) + ", mPrimaryProviderStatus=" + providerStatusToString(this.mPrimaryProviderStatus) + ", mPrimaryProviderReportedStatus=" + this.mPrimaryProviderReportedStatus + ", mSecondaryProviderStatus=" + providerStatusToString(this.mSecondaryProviderStatus) + ", mSecondaryProviderReportedStatus=" + this.mSecondaryProviderReportedStatus + '}';
    }

    public static LocationTimeZoneAlgorithmStatus parseCommandlineArg(String arg) {
        Pattern pattern = Pattern.compile("LocationTimeZoneAlgorithmStatus\\{mAlgorithmStatus=(.+), mPrimaryProviderStatus=([^,]+), mPrimaryProviderReportedStatus=(null|TimeZoneProviderStatus\\{[^}]+\\}), mSecondaryProviderStatus=([^,]+), mSecondaryProviderReportedStatus=(null|TimeZoneProviderStatus\\{[^}]+\\})\\}");
        Matcher matcher = pattern.matcher(arg);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unable to parse algorithm status arg: " + arg);
        }
        int algorithmStatus = DetectorStatusTypes.detectionAlgorithmStatusFromString(matcher.group(1));
        int primaryProviderStatus = providerStatusFromString(matcher.group(2));
        TimeZoneProviderStatus primaryProviderReportedStatus = parseTimeZoneProviderStatusOrNull(matcher.group(3));
        int secondaryProviderStatus = providerStatusFromString(matcher.group(4));
        TimeZoneProviderStatus secondaryProviderReportedStatus = parseTimeZoneProviderStatusOrNull(matcher.group(5));
        return new LocationTimeZoneAlgorithmStatus(algorithmStatus, primaryProviderStatus, primaryProviderReportedStatus, secondaryProviderStatus, secondaryProviderReportedStatus);
    }

    private static TimeZoneProviderStatus parseTimeZoneProviderStatusOrNull(String providerReportedStatusString) {
        if ("null".equals(providerReportedStatusString)) {
            return null;
        }
        TimeZoneProviderStatus providerReportedStatus = TimeZoneProviderStatus.parseProviderStatus(providerReportedStatusString);
        return providerReportedStatus;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mStatus);
        parcel.writeInt(this.mPrimaryProviderStatus);
        parcel.writeParcelable(this.mPrimaryProviderReportedStatus, flags);
        parcel.writeInt(this.mSecondaryProviderStatus);
        parcel.writeParcelable(this.mSecondaryProviderReportedStatus, flags);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocationTimeZoneAlgorithmStatus that = (LocationTimeZoneAlgorithmStatus) o;
        if (this.mStatus == that.mStatus && this.mPrimaryProviderStatus == that.mPrimaryProviderStatus && Objects.equals(this.mPrimaryProviderReportedStatus, that.mPrimaryProviderReportedStatus) && this.mSecondaryProviderStatus == that.mSecondaryProviderStatus && Objects.equals(this.mSecondaryProviderReportedStatus, that.mSecondaryProviderReportedStatus)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mStatus), Integer.valueOf(this.mPrimaryProviderStatus), this.mPrimaryProviderReportedStatus, Integer.valueOf(this.mSecondaryProviderStatus), this.mSecondaryProviderReportedStatus);
    }

    public boolean couldEnableTelephonyFallback() {
        TimeZoneProviderStatus timeZoneProviderStatus;
        TimeZoneProviderStatus timeZoneProviderStatus2;
        int i = this.mStatus;
        if (i == 0 || i == 2 || i == 1) {
            return false;
        }
        boolean primarySuggestsFallback = false;
        int i2 = this.mPrimaryProviderStatus;
        if (i2 == 1) {
            primarySuggestsFallback = true;
        } else if (i2 == 4 && (timeZoneProviderStatus = this.mPrimaryProviderReportedStatus) != null) {
            primarySuggestsFallback = timeZoneProviderStatus.couldEnableTelephonyFallback();
        }
        boolean secondarySuggestsFallback = false;
        int i3 = this.mSecondaryProviderStatus;
        if (i3 == 1) {
            secondarySuggestsFallback = true;
        } else if (i3 == 4 && (timeZoneProviderStatus2 = this.mSecondaryProviderReportedStatus) != null) {
            secondarySuggestsFallback = timeZoneProviderStatus2.couldEnableTelephonyFallback();
        }
        return primarySuggestsFallback && secondarySuggestsFallback;
    }

    public static String providerStatusToString(int providerStatus) {
        switch (providerStatus) {
            case 1:
                return "NOT_PRESENT";
            case 2:
                return "NOT_READY";
            case 3:
                return "IS_CERTAIN";
            case 4:
                return "IS_UNCERTAIN";
            default:
                throw new IllegalArgumentException("Unknown status: " + providerStatus);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int providerStatusFromString(String providerStatusString) {
        char c;
        if (TextUtils.isEmpty(providerStatusString)) {
            throw new IllegalArgumentException("Empty status: " + providerStatusString);
        }
        switch (providerStatusString.hashCode()) {
            case -1705279891:
                if (providerStatusString.equals("IS_CERTAIN")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 187660047:
                if (providerStatusString.equals("NOT_PRESENT")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1034051831:
                if (providerStatusString.equals("NOT_READY")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1393440500:
                if (providerStatusString.equals("IS_UNCERTAIN")) {
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
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            default:
                throw new IllegalArgumentException("Unknown status: " + providerStatusString);
        }
    }

    private static boolean hasProviderReported(int providerStatus) {
        return providerStatus == 3 || providerStatus == 4;
    }

    public static int requireValidProviderStatus(int providerStatus) {
        if (providerStatus < 1 || providerStatus > 4) {
            throw new IllegalArgumentException("Invalid provider status: " + providerStatus);
        }
        return providerStatus;
    }
}
