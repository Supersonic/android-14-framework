package android.service.credentials;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
/* loaded from: classes3.dex */
public class BeginCreateCredentialRequest implements Parcelable {
    public static final Parcelable.Creator<BeginCreateCredentialRequest> CREATOR = new Parcelable.Creator<BeginCreateCredentialRequest>() { // from class: android.service.credentials.BeginCreateCredentialRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BeginCreateCredentialRequest createFromParcel(Parcel in) {
            return new BeginCreateCredentialRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BeginCreateCredentialRequest[] newArray(int size) {
            return new BeginCreateCredentialRequest[size];
        }
    };
    private final CallingAppInfo mCallingAppInfo;
    private final Bundle mData;
    private final String mType;

    public BeginCreateCredentialRequest(String type, Bundle data, CallingAppInfo callingAppInfo) {
        this.mType = (String) Preconditions.checkStringNotEmpty(type, "type must not be null or empty");
        Bundle dataCopy = new Bundle();
        dataCopy.putAll(data);
        this.mData = dataCopy;
        this.mCallingAppInfo = callingAppInfo;
    }

    public BeginCreateCredentialRequest(String type, Bundle data) {
        this(type, data, null);
    }

    private BeginCreateCredentialRequest(Parcel in) {
        this.mCallingAppInfo = (CallingAppInfo) in.readTypedObject(CallingAppInfo.CREATOR);
        this.mType = in.readString8();
        this.mData = in.readBundle(Bundle.class.getClassLoader());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mCallingAppInfo, flags);
        dest.writeString8(this.mType);
        dest.writeBundle(this.mData);
    }

    public CallingAppInfo getCallingAppInfo() {
        return this.mCallingAppInfo;
    }

    public String getType() {
        return this.mType;
    }

    public Bundle getData() {
        return this.mData;
    }
}
