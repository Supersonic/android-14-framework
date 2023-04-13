package android.location;

import android.accessibilityservice.AccessibilityTrace;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Deprecated
/* loaded from: classes2.dex */
public class Criteria implements Parcelable {
    public static final int ACCURACY_COARSE = 2;
    public static final int ACCURACY_FINE = 1;
    public static final int ACCURACY_HIGH = 3;
    public static final int ACCURACY_LOW = 1;
    public static final int ACCURACY_MEDIUM = 2;
    public static final Parcelable.Creator<Criteria> CREATOR = new Parcelable.Creator<Criteria>() { // from class: android.location.Criteria.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Criteria createFromParcel(Parcel in) {
            Criteria c = new Criteria();
            c.mHorizontalAccuracy = in.readInt();
            c.mVerticalAccuracy = in.readInt();
            c.mSpeedAccuracy = in.readInt();
            c.mBearingAccuracy = in.readInt();
            c.mPowerRequirement = in.readInt();
            c.mAltitudeRequired = in.readInt() != 0;
            c.mBearingRequired = in.readInt() != 0;
            c.mSpeedRequired = in.readInt() != 0;
            c.mCostAllowed = in.readInt() != 0;
            return c;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Criteria[] newArray(int size) {
            return new Criteria[size];
        }
    };
    public static final int NO_REQUIREMENT = 0;
    public static final int POWER_HIGH = 3;
    public static final int POWER_LOW = 1;
    public static final int POWER_MEDIUM = 2;
    private boolean mAltitudeRequired;
    private int mBearingAccuracy;
    private boolean mBearingRequired;
    private boolean mCostAllowed;
    private int mHorizontalAccuracy;
    private int mPowerRequirement;
    private int mSpeedAccuracy;
    private boolean mSpeedRequired;
    private int mVerticalAccuracy;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AccuracyRequirement {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface LocationAccuracyRequirement {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface PowerRequirement {
    }

    public Criteria() {
        this.mHorizontalAccuracy = 0;
        this.mVerticalAccuracy = 0;
        this.mSpeedAccuracy = 0;
        this.mBearingAccuracy = 0;
        this.mPowerRequirement = 0;
        this.mAltitudeRequired = false;
        this.mBearingRequired = false;
        this.mSpeedRequired = false;
        this.mCostAllowed = false;
    }

    public Criteria(Criteria criteria) {
        this.mHorizontalAccuracy = 0;
        this.mVerticalAccuracy = 0;
        this.mSpeedAccuracy = 0;
        this.mBearingAccuracy = 0;
        this.mPowerRequirement = 0;
        this.mAltitudeRequired = false;
        this.mBearingRequired = false;
        this.mSpeedRequired = false;
        this.mCostAllowed = false;
        this.mHorizontalAccuracy = criteria.mHorizontalAccuracy;
        this.mVerticalAccuracy = criteria.mVerticalAccuracy;
        this.mSpeedAccuracy = criteria.mSpeedAccuracy;
        this.mBearingAccuracy = criteria.mBearingAccuracy;
        this.mPowerRequirement = criteria.mPowerRequirement;
        this.mAltitudeRequired = criteria.mAltitudeRequired;
        this.mBearingRequired = criteria.mBearingRequired;
        this.mSpeedRequired = criteria.mSpeedRequired;
        this.mCostAllowed = criteria.mCostAllowed;
    }

    public void setHorizontalAccuracy(int accuracy) {
        this.mHorizontalAccuracy = Preconditions.checkArgumentInRange(accuracy, 0, 3, "accuracy");
    }

    public int getHorizontalAccuracy() {
        return this.mHorizontalAccuracy;
    }

    public void setVerticalAccuracy(int accuracy) {
        this.mVerticalAccuracy = Preconditions.checkArgumentInRange(accuracy, 0, 3, "accuracy");
    }

    public int getVerticalAccuracy() {
        return this.mVerticalAccuracy;
    }

    public void setSpeedAccuracy(int accuracy) {
        this.mSpeedAccuracy = Preconditions.checkArgumentInRange(accuracy, 0, 3, "accuracy");
    }

    public int getSpeedAccuracy() {
        return this.mSpeedAccuracy;
    }

    public void setBearingAccuracy(int accuracy) {
        this.mBearingAccuracy = Preconditions.checkArgumentInRange(accuracy, 0, 3, "accuracy");
    }

    public int getBearingAccuracy() {
        return this.mBearingAccuracy;
    }

    public void setAccuracy(int accuracy) {
        Preconditions.checkArgumentInRange(accuracy, 0, 2, "accuracy");
        switch (accuracy) {
            case 0:
                setHorizontalAccuracy(0);
                return;
            case 1:
                setHorizontalAccuracy(3);
                return;
            case 2:
                setHorizontalAccuracy(1);
                return;
            default:
                return;
        }
    }

    public int getAccuracy() {
        if (this.mHorizontalAccuracy >= 3) {
            return 1;
        }
        return 2;
    }

    public void setPowerRequirement(int powerRequirement) {
        this.mPowerRequirement = Preconditions.checkArgumentInRange(powerRequirement, 0, 3, "powerRequirement");
    }

    public int getPowerRequirement() {
        return this.mPowerRequirement;
    }

    public void setCostAllowed(boolean costAllowed) {
        this.mCostAllowed = costAllowed;
    }

    public boolean isCostAllowed() {
        return this.mCostAllowed;
    }

    public void setAltitudeRequired(boolean altitudeRequired) {
        this.mAltitudeRequired = altitudeRequired;
    }

    public boolean isAltitudeRequired() {
        return this.mAltitudeRequired;
    }

    public void setSpeedRequired(boolean speedRequired) {
        this.mSpeedRequired = speedRequired;
    }

    public boolean isSpeedRequired() {
        return this.mSpeedRequired;
    }

    public void setBearingRequired(boolean bearingRequired) {
        this.mBearingRequired = bearingRequired;
    }

    public boolean isBearingRequired() {
        return this.mBearingRequired;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mHorizontalAccuracy);
        parcel.writeInt(this.mVerticalAccuracy);
        parcel.writeInt(this.mSpeedAccuracy);
        parcel.writeInt(this.mBearingAccuracy);
        parcel.writeInt(this.mPowerRequirement);
        parcel.writeInt(this.mAltitudeRequired ? 1 : 0);
        parcel.writeInt(this.mBearingRequired ? 1 : 0);
        parcel.writeInt(this.mSpeedRequired ? 1 : 0);
        parcel.writeInt(this.mCostAllowed ? 1 : 0);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Criteria[");
        s.append("power=").append(requirementToString(this.mPowerRequirement)).append(", ");
        s.append("accuracy=").append(requirementToString(this.mHorizontalAccuracy));
        if (this.mVerticalAccuracy != 0) {
            s.append(", verticalAccuracy=").append(requirementToString(this.mVerticalAccuracy));
        }
        if (this.mSpeedAccuracy != 0) {
            s.append(", speedAccuracy=").append(requirementToString(this.mSpeedAccuracy));
        }
        if (this.mBearingAccuracy != 0) {
            s.append(", bearingAccuracy=").append(requirementToString(this.mBearingAccuracy));
        }
        if (this.mAltitudeRequired || this.mBearingRequired || this.mSpeedRequired) {
            s.append(", required=[");
            if (this.mAltitudeRequired) {
                s.append("altitude, ");
            }
            if (this.mBearingRequired) {
                s.append("bearing, ");
            }
            if (this.mSpeedRequired) {
                s.append("speed, ");
            }
            s.setLength(s.length() - 2);
            s.append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        if (this.mCostAllowed) {
            s.append(", costAllowed");
        }
        s.append(']');
        return s.toString();
    }

    private static String requirementToString(int power) {
        switch (power) {
            case 0:
                return AccessibilityTrace.NAME_NONE;
            case 1:
                return "Low";
            case 2:
                return "Medium";
            case 3:
                return "High";
            default:
                return "???";
        }
    }
}
