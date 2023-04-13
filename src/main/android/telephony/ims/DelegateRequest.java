package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
@SystemApi
/* loaded from: classes3.dex */
public final class DelegateRequest implements Parcelable {
    public static final Parcelable.Creator<DelegateRequest> CREATOR = new Parcelable.Creator<DelegateRequest>() { // from class: android.telephony.ims.DelegateRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DelegateRequest createFromParcel(Parcel source) {
            return new DelegateRequest(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DelegateRequest[] newArray(int size) {
            return new DelegateRequest[size];
        }
    };
    private final ArrayList<String> mFeatureTags;

    public DelegateRequest(Set<String> featureTags) {
        if (featureTags == null) {
            throw new IllegalStateException("Invalid arguments, featureTags List can not be null");
        }
        this.mFeatureTags = new ArrayList<>(featureTags);
    }

    public Set<String> getFeatureTags() {
        return new ArraySet(this.mFeatureTags);
    }

    private DelegateRequest(Parcel in) {
        ArrayList<String> arrayList = new ArrayList<>();
        this.mFeatureTags = arrayList;
        in.readList(arrayList, null, String.class);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.mFeatureTags);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DelegateRequest that = (DelegateRequest) o;
        return this.mFeatureTags.equals(that.mFeatureTags);
    }

    public int hashCode() {
        return Objects.hash(this.mFeatureTags);
    }

    public String toString() {
        return "DelegateRequest{mFeatureTags=" + this.mFeatureTags + '}';
    }
}
