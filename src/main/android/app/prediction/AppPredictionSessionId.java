package android.app.prediction;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class AppPredictionSessionId implements Parcelable {
    public static final Parcelable.Creator<AppPredictionSessionId> CREATOR = new Parcelable.Creator<AppPredictionSessionId>() { // from class: android.app.prediction.AppPredictionSessionId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppPredictionSessionId createFromParcel(Parcel parcel) {
            return new AppPredictionSessionId(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppPredictionSessionId[] newArray(int size) {
            return new AppPredictionSessionId[size];
        }
    };
    private final String mId;
    private final int mUserId;

    public AppPredictionSessionId(String id, int userId) {
        this.mId = id;
        this.mUserId = userId;
    }

    private AppPredictionSessionId(Parcel p) {
        this.mId = p.readString();
        this.mUserId = p.readInt();
    }

    public int getUserId() {
        return this.mUserId;
    }

    public boolean equals(Object o) {
        if (getClass().equals(o != null ? o.getClass() : null)) {
            AppPredictionSessionId other = (AppPredictionSessionId) o;
            return this.mId.equals(other.mId) && this.mUserId == other.mUserId;
        }
        return false;
    }

    public String toString() {
        return this.mId + "," + this.mUserId;
    }

    public int hashCode() {
        return Objects.hash(this.mId, Integer.valueOf(this.mUserId));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeInt(this.mUserId);
    }
}
