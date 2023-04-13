package android.app.prediction;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes.dex */
public final class AppTargetId implements Parcelable {
    public static final Parcelable.Creator<AppTargetId> CREATOR = new Parcelable.Creator<AppTargetId>() { // from class: android.app.prediction.AppTargetId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppTargetId createFromParcel(Parcel parcel) {
            return new AppTargetId(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppTargetId[] newArray(int size) {
            return new AppTargetId[size];
        }
    };
    private final String mId;

    @SystemApi
    public AppTargetId(String id) {
        this.mId = id;
    }

    private AppTargetId(Parcel parcel) {
        this.mId = parcel.readString();
    }

    public String getId() {
        return this.mId;
    }

    public boolean equals(Object o) {
        if (getClass().equals(o != null ? o.getClass() : null)) {
            AppTargetId other = (AppTargetId) o;
            return this.mId.equals(other.mId);
        }
        return false;
    }

    public int hashCode() {
        return this.mId.hashCode();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
    }
}
