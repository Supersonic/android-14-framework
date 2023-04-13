package android.service.credentials;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class ClearCredentialStateRequest implements Parcelable {
    public static final Parcelable.Creator<ClearCredentialStateRequest> CREATOR = new Parcelable.Creator<ClearCredentialStateRequest>() { // from class: android.service.credentials.ClearCredentialStateRequest.1
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
    private final CallingAppInfo mCallingAppInfo;
    private final Bundle mData;

    public Bundle getData() {
        return this.mData;
    }

    public CallingAppInfo getCallingAppInfo() {
        return this.mCallingAppInfo;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mCallingAppInfo, flags);
        dest.writeBundle(this.mData);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "ClearCredentialStateRequest {callingAppInfo=" + this.mCallingAppInfo.toString() + " }, {data= " + this.mData + "}";
    }

    public ClearCredentialStateRequest(CallingAppInfo callingAppInfo, Bundle data) {
        this.mCallingAppInfo = (CallingAppInfo) Objects.requireNonNull(callingAppInfo, "callingAppInfo must not be null");
        this.mData = (Bundle) Objects.requireNonNull(data, "data must not be null");
    }

    private ClearCredentialStateRequest(Parcel in) {
        this.mCallingAppInfo = (CallingAppInfo) in.readTypedObject(CallingAppInfo.CREATOR);
        Bundle data = in.readBundle();
        this.mData = data;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) data);
    }
}
