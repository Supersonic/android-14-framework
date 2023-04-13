package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.Set;
@SystemApi
/* loaded from: classes3.dex */
public final class DelegateRegistrationState implements Parcelable {
    public static final Parcelable.Creator<DelegateRegistrationState> CREATOR = new Parcelable.Creator<DelegateRegistrationState>() { // from class: android.telephony.ims.DelegateRegistrationState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DelegateRegistrationState createFromParcel(Parcel source) {
            return new DelegateRegistrationState(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DelegateRegistrationState[] newArray(int size) {
            return new DelegateRegistrationState[size];
        }
    };
    public static final int DEREGISTERED_REASON_NOT_PROVISIONED = 1;
    public static final int DEREGISTERED_REASON_NOT_REGISTERED = 2;
    public static final int DEREGISTERED_REASON_UNKNOWN = 0;
    public static final int DEREGISTERING_REASON_DESTROY_PENDING = 6;
    public static final int DEREGISTERING_REASON_FEATURE_TAGS_CHANGING = 5;
    public static final int DEREGISTERING_REASON_LOSING_PDN = 7;
    public static final int DEREGISTERING_REASON_PDN_CHANGE = 3;
    public static final int DEREGISTERING_REASON_PROVISIONING_CHANGE = 4;
    public static final int DEREGISTERING_REASON_UNSPECIFIED = 8;
    private final ArraySet<FeatureTagState> mDeregisteredTags;
    private final ArraySet<FeatureTagState> mDeregisteringTags;
    private ArraySet<String> mRegisteredTags;
    private ArraySet<String> mRegisteringTags;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DeregisteredReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DeregisteringReason {
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final DelegateRegistrationState mState = new DelegateRegistrationState();

        public Builder addRegisteringFeatureTags(Set<String> featureTags) {
            this.mState.mRegisteringTags.addAll(featureTags);
            return this;
        }

        public Builder addRegisteredFeatureTag(String featureTag) {
            this.mState.mRegisteredTags.add(featureTag);
            return this;
        }

        public Builder addRegisteredFeatureTags(Set<String> featureTags) {
            this.mState.mRegisteredTags.addAll(featureTags);
            return this;
        }

        public Builder addDeregisteringFeatureTag(String featureTag, int reason) {
            this.mState.mDeregisteringTags.add(new FeatureTagState(featureTag, reason));
            return this;
        }

        public Builder addDeregisteredFeatureTag(String featureTag, int reason) {
            this.mState.mDeregisteredTags.add(new FeatureTagState(featureTag, reason));
            return this;
        }

        public DelegateRegistrationState build() {
            return this.mState;
        }
    }

    private DelegateRegistrationState() {
        this.mRegisteringTags = new ArraySet<>();
        this.mRegisteredTags = new ArraySet<>();
        this.mDeregisteringTags = new ArraySet<>();
        this.mDeregisteredTags = new ArraySet<>();
    }

    private DelegateRegistrationState(Parcel source) {
        this.mRegisteringTags = new ArraySet<>();
        this.mRegisteredTags = new ArraySet<>();
        ArraySet<FeatureTagState> arraySet = new ArraySet<>();
        this.mDeregisteringTags = arraySet;
        ArraySet<FeatureTagState> arraySet2 = new ArraySet<>();
        this.mDeregisteredTags = arraySet2;
        this.mRegisteredTags = source.readArraySet(null);
        readStateFromParcel(source, arraySet);
        readStateFromParcel(source, arraySet2);
        this.mRegisteringTags = source.readArraySet(null);
    }

    public Set<String> getRegisteringFeatureTags() {
        return new ArraySet((ArraySet) this.mRegisteringTags);
    }

    public Set<String> getRegisteredFeatureTags() {
        return new ArraySet((ArraySet) this.mRegisteredTags);
    }

    public Set<FeatureTagState> getDeregisteringFeatureTags() {
        return new ArraySet((ArraySet) this.mDeregisteringTags);
    }

    public Set<FeatureTagState> getDeregisteredFeatureTags() {
        return new ArraySet((ArraySet) this.mDeregisteredTags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArraySet(this.mRegisteredTags);
        writeStateToParcel(dest, this.mDeregisteringTags);
        writeStateToParcel(dest, this.mDeregisteredTags);
        dest.writeArraySet(this.mRegisteringTags);
    }

    private void writeStateToParcel(Parcel dest, Set<FeatureTagState> state) {
        dest.writeInt(state.size());
        for (FeatureTagState s : state) {
            dest.writeString(s.getFeatureTag());
            dest.writeInt(s.getState());
        }
    }

    private void readStateFromParcel(Parcel source, Set<FeatureTagState> emptyState) {
        int len = source.readInt();
        for (int i = 0; i < len; i++) {
            String ft = source.readString();
            int reason = source.readInt();
            emptyState.add(new FeatureTagState(ft, reason));
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DelegateRegistrationState that = (DelegateRegistrationState) o;
        if (this.mRegisteringTags.equals(that.mRegisteringTags) && this.mRegisteredTags.equals(that.mRegisteredTags) && this.mDeregisteringTags.equals(that.mDeregisteringTags) && this.mDeregisteredTags.equals(that.mDeregisteredTags)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mRegisteringTags, this.mRegisteredTags, this.mDeregisteringTags, this.mDeregisteredTags);
    }

    public String toString() {
        return "DelegateRegistrationState{ registered={" + this.mRegisteredTags + "}, registering={" + this.mRegisteringTags + "}, deregistering={" + this.mDeregisteringTags + "}, deregistered={" + this.mDeregisteredTags + "}}";
    }
}
