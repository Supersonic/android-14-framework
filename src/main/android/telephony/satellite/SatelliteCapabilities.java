package android.telephony.satellite;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes3.dex */
public final class SatelliteCapabilities implements Parcelable {
    public static final Parcelable.Creator<SatelliteCapabilities> CREATOR = new Parcelable.Creator<SatelliteCapabilities>() { // from class: android.telephony.satellite.SatelliteCapabilities.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SatelliteCapabilities createFromParcel(Parcel in) {
            return new SatelliteCapabilities(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SatelliteCapabilities[] newArray(int size) {
            return new SatelliteCapabilities[size];
        }
    };
    private boolean mIsAlwaysOn;
    private boolean mNeedsPointingToSatellite;
    private boolean mNeedsSeparateSimProfile;
    private Set<Integer> mSupportedRadioTechnologies;

    public SatelliteCapabilities(Set<Integer> supportedRadioTechnologies, boolean isAlwaysOn, boolean needsPointingToSatellite, boolean needsSeparateSimProfile) {
        this.mSupportedRadioTechnologies = supportedRadioTechnologies == null ? new HashSet<>() : supportedRadioTechnologies;
        this.mIsAlwaysOn = isAlwaysOn;
        this.mNeedsPointingToSatellite = needsPointingToSatellite;
        this.mNeedsSeparateSimProfile = needsSeparateSimProfile;
    }

    private SatelliteCapabilities(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        Set<Integer> set = this.mSupportedRadioTechnologies;
        if (set != null && !set.isEmpty()) {
            out.writeInt(this.mSupportedRadioTechnologies.size());
            for (Integer num : this.mSupportedRadioTechnologies) {
                int technology = num.intValue();
                out.writeInt(technology);
            }
        } else {
            out.writeInt(0);
        }
        out.writeBoolean(this.mIsAlwaysOn);
        out.writeBoolean(this.mNeedsPointingToSatellite);
        out.writeBoolean(this.mNeedsSeparateSimProfile);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SupportedRadioTechnology:");
        Set<Integer> set = this.mSupportedRadioTechnologies;
        if (set != null && !set.isEmpty()) {
            for (Integer num : this.mSupportedRadioTechnologies) {
                int technology = num.intValue();
                sb.append(technology);
                sb.append(",");
            }
        } else {
            sb.append("none,");
        }
        sb.append("isAlwaysOn:");
        sb.append(this.mIsAlwaysOn);
        sb.append(",");
        sb.append("needsPointingToSatellite:");
        sb.append(this.mNeedsPointingToSatellite);
        sb.append(",");
        sb.append("needsSeparateSimProfile:");
        sb.append(this.mNeedsSeparateSimProfile);
        return sb.toString();
    }

    public Set<Integer> getSupportedRadioTechnologies() {
        return this.mSupportedRadioTechnologies;
    }

    public boolean isAlwaysOn() {
        return this.mIsAlwaysOn;
    }

    public boolean needsPointingToSatellite() {
        return this.mNeedsPointingToSatellite;
    }

    public boolean needsSeparateSimProfile() {
        return this.mNeedsSeparateSimProfile;
    }

    private void readFromParcel(Parcel in) {
        this.mSupportedRadioTechnologies = new HashSet();
        int numSupportedRadioTechnologies = in.readInt();
        if (numSupportedRadioTechnologies > 0) {
            for (int i = 0; i < numSupportedRadioTechnologies; i++) {
                this.mSupportedRadioTechnologies.add(Integer.valueOf(in.readInt()));
            }
        }
        this.mIsAlwaysOn = in.readBoolean();
        this.mNeedsPointingToSatellite = in.readBoolean();
        this.mNeedsSeparateSimProfile = in.readBoolean();
    }
}
