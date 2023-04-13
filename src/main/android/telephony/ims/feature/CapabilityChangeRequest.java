package android.telephony.ims.feature;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@SystemApi
/* loaded from: classes3.dex */
public final class CapabilityChangeRequest implements Parcelable {
    public static final Parcelable.Creator<CapabilityChangeRequest> CREATOR = new Parcelable.Creator<CapabilityChangeRequest>() { // from class: android.telephony.ims.feature.CapabilityChangeRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CapabilityChangeRequest createFromParcel(Parcel in) {
            return new CapabilityChangeRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CapabilityChangeRequest[] newArray(int size) {
            return new CapabilityChangeRequest[size];
        }
    };
    private final Set<CapabilityPair> mCapabilitiesToDisable;
    private final Set<CapabilityPair> mCapabilitiesToEnable;

    /* loaded from: classes3.dex */
    public static class CapabilityPair {
        private final int mCapability;
        private final int radioTech;

        public CapabilityPair(int capability, int radioTech) {
            this.mCapability = capability;
            this.radioTech = radioTech;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof CapabilityPair) {
                CapabilityPair that = (CapabilityPair) o;
                return getCapability() == that.getCapability() && getRadioTech() == that.getRadioTech();
            }
            return false;
        }

        public int hashCode() {
            int result = getCapability();
            return (result * 31) + getRadioTech();
        }

        public int getCapability() {
            return this.mCapability;
        }

        public int getRadioTech() {
            return this.radioTech;
        }

        public String toString() {
            return "CapabilityPair{mCapability=" + this.mCapability + ", radioTech=" + this.radioTech + '}';
        }
    }

    public CapabilityChangeRequest() {
        this.mCapabilitiesToEnable = new ArraySet();
        this.mCapabilitiesToDisable = new ArraySet();
    }

    public void addCapabilitiesToEnableForTech(int capabilities, int radioTech) {
        addAllCapabilities(this.mCapabilitiesToEnable, capabilities, radioTech);
    }

    public void addCapabilitiesToDisableForTech(int capabilities, int radioTech) {
        addAllCapabilities(this.mCapabilitiesToDisable, capabilities, radioTech);
    }

    public List<CapabilityPair> getCapabilitiesToEnable() {
        return new ArrayList(this.mCapabilitiesToEnable);
    }

    public List<CapabilityPair> getCapabilitiesToDisable() {
        return new ArrayList(this.mCapabilitiesToDisable);
    }

    private void addAllCapabilities(Set<CapabilityPair> set, int capabilities, int tech) {
        long highestCapability = Long.highestOneBit(capabilities);
        for (int i = 1; i <= highestCapability; i *= 2) {
            if ((i & capabilities) > 0) {
                set.add(new CapabilityPair(i, tech));
            }
        }
    }

    protected CapabilityChangeRequest(Parcel in) {
        int enableSize = in.readInt();
        this.mCapabilitiesToEnable = new ArraySet(enableSize);
        for (int i = 0; i < enableSize; i++) {
            this.mCapabilitiesToEnable.add(new CapabilityPair(in.readInt(), in.readInt()));
        }
        int disableSize = in.readInt();
        this.mCapabilitiesToDisable = new ArraySet(disableSize);
        for (int i2 = 0; i2 < disableSize; i2++) {
            this.mCapabilitiesToDisable.add(new CapabilityPair(in.readInt(), in.readInt()));
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCapabilitiesToEnable.size());
        for (CapabilityPair pair : this.mCapabilitiesToEnable) {
            dest.writeInt(pair.getCapability());
            dest.writeInt(pair.getRadioTech());
        }
        dest.writeInt(this.mCapabilitiesToDisable.size());
        for (CapabilityPair pair2 : this.mCapabilitiesToDisable) {
            dest.writeInt(pair2.getCapability());
            dest.writeInt(pair2.getRadioTech());
        }
    }

    public String toString() {
        return "CapabilityChangeRequest{mCapabilitiesToEnable=" + this.mCapabilitiesToEnable + ", mCapabilitiesToDisable=" + this.mCapabilitiesToDisable + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CapabilityChangeRequest) {
            CapabilityChangeRequest that = (CapabilityChangeRequest) o;
            if (this.mCapabilitiesToEnable.equals(that.mCapabilitiesToEnable)) {
                return this.mCapabilitiesToDisable.equals(that.mCapabilitiesToDisable);
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        int result = this.mCapabilitiesToEnable.hashCode();
        return (result * 31) + this.mCapabilitiesToDisable.hashCode();
    }
}
