package android.app.admin;

import android.annotation.SystemApi;
import android.content.IntentFilter;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
@SystemApi
/* loaded from: classes.dex */
public final class IntentFilterPolicyKey extends PolicyKey {
    public static final Parcelable.Creator<IntentFilterPolicyKey> CREATOR = new Parcelable.Creator<IntentFilterPolicyKey>() { // from class: android.app.admin.IntentFilterPolicyKey.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IntentFilterPolicyKey createFromParcel(Parcel source) {
            return new IntentFilterPolicyKey(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IntentFilterPolicyKey[] newArray(int size) {
            return new IntentFilterPolicyKey[size];
        }
    };
    private final IntentFilter mFilter;

    public IntentFilterPolicyKey(String identifier, IntentFilter filter) {
        super(identifier);
        this.mFilter = (IntentFilter) Objects.requireNonNull(filter);
    }

    public IntentFilterPolicyKey(String identifier) {
        super(identifier);
        this.mFilter = null;
    }

    private IntentFilterPolicyKey(Parcel source) {
        super(source.readString());
        this.mFilter = (IntentFilter) source.readTypedObject(IntentFilter.CREATOR);
    }

    public IntentFilter getIntentFilter() {
        return this.mFilter;
    }

    @Override // android.app.admin.PolicyKey
    public void saveToXml(TypedXmlSerializer serializer) throws IOException {
        serializer.attribute(null, "policy-identifier", getIdentifier());
        this.mFilter.writeToXml(serializer);
    }

    @Override // android.app.admin.PolicyKey
    public IntentFilterPolicyKey readFromXml(TypedXmlPullParser parser) throws XmlPullParserException, IOException {
        String identifier = parser.getAttributeValue(null, "policy-identifier");
        IntentFilter filter = new IntentFilter();
        filter.readFromXml(parser);
        return new IntentFilterPolicyKey(identifier, filter);
    }

    @Override // android.app.admin.PolicyKey
    public void writeToBundle(Bundle bundle) {
        bundle.putString(PolicyUpdateReceiver.EXTRA_POLICY_KEY, getIdentifier());
        Bundle extraPolicyParams = new Bundle();
        extraPolicyParams.putParcelable(PolicyUpdateReceiver.EXTRA_INTENT_FILTER, this.mFilter);
        bundle.putBundle(PolicyUpdateReceiver.EXTRA_POLICY_BUNDLE_KEY, extraPolicyParams);
    }

    @Override // android.app.admin.PolicyKey
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntentFilterPolicyKey other = (IntentFilterPolicyKey) o;
        if (Objects.equals(getIdentifier(), other.getIdentifier()) && IntentFilter.filterEquals(this.mFilter, other.mFilter)) {
            return true;
        }
        return false;
    }

    @Override // android.app.admin.PolicyKey
    public int hashCode() {
        return Objects.hash(getIdentifier());
    }

    public String toString() {
        return "IntentFilterPolicyKey{mKey= " + getIdentifier() + "; mFilter= " + this.mFilter + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getIdentifier());
        dest.writeTypedObject(this.mFilter, flags);
    }
}
