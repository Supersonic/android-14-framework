package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.ims.feature.ImsFeature;
import android.util.ArraySet;
import java.util.Set;
@SystemApi
/* loaded from: classes3.dex */
public final class ImsFeatureConfiguration implements Parcelable {
    public static final Parcelable.Creator<ImsFeatureConfiguration> CREATOR = new Parcelable.Creator<ImsFeatureConfiguration>() { // from class: android.telephony.ims.stub.ImsFeatureConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsFeatureConfiguration createFromParcel(Parcel in) {
            return new ImsFeatureConfiguration(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsFeatureConfiguration[] newArray(int size) {
            return new ImsFeatureConfiguration[size];
        }
    };
    private final Set<FeatureSlotPair> mFeatures;

    /* loaded from: classes3.dex */
    public static final class FeatureSlotPair {
        public final int featureType;
        public final int slotId;

        public FeatureSlotPair(int slotId, int featureType) {
            this.slotId = slotId;
            this.featureType = featureType;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FeatureSlotPair that = (FeatureSlotPair) o;
            if (this.slotId == that.slotId && this.featureType == that.featureType) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = this.slotId;
            return (result * 31) + this.featureType;
        }

        public String toString() {
            return "{s=" + this.slotId + ", f=" + ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(this.featureType)) + "}";
        }
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        ImsFeatureConfiguration mConfig = new ImsFeatureConfiguration();

        public Builder addFeature(int slotId, int featureType) {
            this.mConfig.addFeature(slotId, featureType);
            return this;
        }

        public ImsFeatureConfiguration build() {
            return this.mConfig;
        }
    }

    public ImsFeatureConfiguration() {
        this.mFeatures = new ArraySet();
    }

    public ImsFeatureConfiguration(Set<FeatureSlotPair> features) {
        ArraySet arraySet = new ArraySet();
        this.mFeatures = arraySet;
        if (features != null) {
            arraySet.addAll(features);
        }
    }

    public Set<FeatureSlotPair> getServiceFeatures() {
        return new ArraySet(this.mFeatures);
    }

    void addFeature(int slotId, int feature) {
        this.mFeatures.add(new FeatureSlotPair(slotId, feature));
    }

    protected ImsFeatureConfiguration(Parcel in) {
        int featurePairLength = in.readInt();
        this.mFeatures = new ArraySet(featurePairLength);
        for (int i = 0; i < featurePairLength; i++) {
            this.mFeatures.add(new FeatureSlotPair(in.readInt(), in.readInt()));
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        FeatureSlotPair[] featureSlotPairs = new FeatureSlotPair[this.mFeatures.size()];
        this.mFeatures.toArray(featureSlotPairs);
        dest.writeInt(featureSlotPairs.length);
        for (FeatureSlotPair featureSlotPair : featureSlotPairs) {
            dest.writeInt(featureSlotPair.slotId);
            dest.writeInt(featureSlotPair.featureType);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ImsFeatureConfiguration) {
            ImsFeatureConfiguration that = (ImsFeatureConfiguration) o;
            return this.mFeatures.equals(that.mFeatures);
        }
        return false;
    }

    public int hashCode() {
        return this.mFeatures.hashCode();
    }
}
