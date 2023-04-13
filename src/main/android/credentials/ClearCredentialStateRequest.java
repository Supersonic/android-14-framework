package android.credentials;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ClearCredentialStateRequest implements Parcelable {
    public static final Parcelable.Creator<ClearCredentialStateRequest> CREATOR = new Parcelable.Creator<ClearCredentialStateRequest>() { // from class: android.credentials.ClearCredentialStateRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClearCredentialStateRequest[] newArray(int size) {
            return new ClearCredentialStateRequest[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClearCredentialStateRequest createFromParcel(Parcel in) {
            return new ClearCredentialStateRequest(in);
        }
    };
    private final Bundle mData;

    public Bundle getData() {
        return this.mData;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(this.mData);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "ClearCredentialStateRequest {data=" + this.mData + "}";
    }

    public ClearCredentialStateRequest(Bundle data) {
        this.mData = (Bundle) Objects.requireNonNull(data, "data must not be null");
    }

    private ClearCredentialStateRequest(Parcel in) {
        Bundle data = in.readBundle();
        this.mData = data;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) data);
    }
}
