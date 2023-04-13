package android.telephony.ims;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.Build;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@SystemApi
/* loaded from: classes3.dex */
public final class RcsContactUceCapability implements Parcelable {
    public static final int CAPABILITY_MECHANISM_OPTIONS = 2;
    public static final int CAPABILITY_MECHANISM_PRESENCE = 1;
    public static final Parcelable.Creator<RcsContactUceCapability> CREATOR = new Parcelable.Creator<RcsContactUceCapability>() { // from class: android.telephony.ims.RcsContactUceCapability.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RcsContactUceCapability createFromParcel(Parcel in) {
            return new RcsContactUceCapability(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RcsContactUceCapability[] newArray(int size) {
            return new RcsContactUceCapability[size];
        }
    };
    public static final int REQUEST_RESULT_FOUND = 3;
    public static final int REQUEST_RESULT_NOT_FOUND = 2;
    public static final int REQUEST_RESULT_NOT_ONLINE = 1;
    public static final int REQUEST_RESULT_UNKNOWN = 0;
    public static final int SOURCE_TYPE_CACHED = 1;
    public static final int SOURCE_TYPE_NETWORK = 0;
    private int mCapabilityMechanism;
    private final Uri mContactUri;
    private Uri mEntityUri;
    private final Set<String> mFeatureTags;
    private final List<RcsContactPresenceTuple> mPresenceTuples;
    private int mRequestResult;
    private int mSourceType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CapabilityMechanism {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RequestResult {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SourceType {
    }

    /* loaded from: classes3.dex */
    public static final class OptionsBuilder {
        private final RcsContactUceCapability mCapabilities;

        public OptionsBuilder(Uri contact) {
            this.mCapabilities = new RcsContactUceCapability(contact, 2, 0);
        }

        public OptionsBuilder(Uri contact, int sourceType) {
            this.mCapabilities = new RcsContactUceCapability(contact, 2, sourceType);
        }

        public OptionsBuilder setRequestResult(int requestResult) {
            this.mCapabilities.mRequestResult = requestResult;
            return this;
        }

        public OptionsBuilder addFeatureTag(String tag) {
            this.mCapabilities.mFeatureTags.add(tag);
            return this;
        }

        public OptionsBuilder addFeatureTags(Set<String> tags) {
            this.mCapabilities.mFeatureTags.addAll(tags);
            return this;
        }

        public RcsContactUceCapability build() {
            return this.mCapabilities;
        }
    }

    /* loaded from: classes3.dex */
    public static final class PresenceBuilder {
        private final RcsContactUceCapability mCapabilities;

        public PresenceBuilder(Uri contact, int sourceType, int requestResult) {
            RcsContactUceCapability rcsContactUceCapability = new RcsContactUceCapability(contact, 1, sourceType);
            this.mCapabilities = rcsContactUceCapability;
            rcsContactUceCapability.mRequestResult = requestResult;
        }

        public PresenceBuilder addCapabilityTuple(RcsContactPresenceTuple tuple) {
            this.mCapabilities.mPresenceTuples.add(tuple);
            return this;
        }

        public PresenceBuilder addCapabilityTuples(List<RcsContactPresenceTuple> tuples) {
            this.mCapabilities.mPresenceTuples.addAll(tuples);
            return this;
        }

        public PresenceBuilder setEntityUri(Uri entityUri) {
            this.mCapabilities.mEntityUri = entityUri;
            return this;
        }

        public RcsContactUceCapability build() {
            return this.mCapabilities;
        }
    }

    private RcsContactUceCapability(Uri contactUri, int mechanism, int sourceType) {
        this.mFeatureTags = new HashSet();
        this.mPresenceTuples = new ArrayList();
        this.mContactUri = contactUri;
        this.mCapabilityMechanism = mechanism;
        this.mSourceType = sourceType;
    }

    private RcsContactUceCapability(Parcel in) {
        HashSet hashSet = new HashSet();
        this.mFeatureTags = hashSet;
        ArrayList arrayList = new ArrayList();
        this.mPresenceTuples = arrayList;
        this.mContactUri = (Uri) in.readParcelable(Uri.class.getClassLoader(), Uri.class);
        this.mCapabilityMechanism = in.readInt();
        this.mSourceType = in.readInt();
        this.mRequestResult = in.readInt();
        this.mEntityUri = (Uri) in.readParcelable(Uri.class.getClassLoader(), Uri.class);
        ArrayList arrayList2 = new ArrayList();
        in.readStringList(arrayList2);
        hashSet.addAll(arrayList2);
        in.readParcelableList(arrayList, RcsContactPresenceTuple.class.getClassLoader(), RcsContactPresenceTuple.class);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.mContactUri, flags);
        out.writeInt(this.mCapabilityMechanism);
        out.writeInt(this.mSourceType);
        out.writeInt(this.mRequestResult);
        out.writeParcelable(this.mEntityUri, flags);
        out.writeStringList(new ArrayList(this.mFeatureTags));
        out.writeParcelableList(this.mPresenceTuples, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getCapabilityMechanism() {
        return this.mCapabilityMechanism;
    }

    public Set<String> getFeatureTags() {
        if (this.mCapabilityMechanism != 2) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(this.mFeatureTags);
    }

    public List<RcsContactPresenceTuple> getCapabilityTuples() {
        if (this.mCapabilityMechanism != 1) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.mPresenceTuples);
    }

    public RcsContactPresenceTuple getCapabilityTuple(String serviceId) {
        if (this.mCapabilityMechanism != 1) {
            return null;
        }
        for (RcsContactPresenceTuple tuple : this.mPresenceTuples) {
            if (tuple.getServiceId() != null && tuple.getServiceId().equals(serviceId)) {
                return tuple;
            }
        }
        return null;
    }

    public int getSourceType() {
        return this.mSourceType;
    }

    public int getRequestResult() {
        return this.mRequestResult;
    }

    public Uri getContactUri() {
        return this.mContactUri;
    }

    public Uri getEntityUri() {
        return this.mEntityUri;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RcsContactUceCapability");
        int i = this.mCapabilityMechanism;
        if (i == 1) {
            builder.append("(presence) {");
        } else if (i == 2) {
            builder.append("(options) {");
        } else {
            builder.append("(?) {");
        }
        if (Build.IS_ENG) {
            builder.append("uri=");
            builder.append(this.mContactUri);
        } else {
            builder.append("uri (isNull)=");
            builder.append(this.mContactUri != null ? "XXX" : "null");
        }
        builder.append(", sourceType=");
        builder.append(this.mSourceType);
        builder.append(", requestResult=");
        builder.append(this.mRequestResult);
        if (Build.IS_ENG) {
            builder.append("entity uri=");
            Uri uri = this.mEntityUri;
            builder.append(uri != null ? uri : "null");
        } else {
            builder.append("entity uri (isNull)=");
            builder.append(this.mEntityUri == null ? "null" : "XXX");
        }
        int i2 = this.mCapabilityMechanism;
        if (i2 == 1) {
            builder.append(", presenceTuples={");
            builder.append(this.mPresenceTuples);
            builder.append("}");
        } else if (i2 == 2) {
            builder.append(", featureTags={");
            builder.append(this.mFeatureTags);
            builder.append("}");
        }
        return builder.toString();
    }
}
