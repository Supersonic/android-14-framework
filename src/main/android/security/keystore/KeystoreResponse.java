package android.security.keystore;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public class KeystoreResponse implements Parcelable {
    public static final Parcelable.Creator<KeystoreResponse> CREATOR = new Parcelable.Creator<KeystoreResponse>() { // from class: android.security.keystore.KeystoreResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeystoreResponse createFromParcel(Parcel in) {
            int error_code = in.readInt();
            String error_msg = in.readString();
            return new KeystoreResponse(error_code, error_msg);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeystoreResponse[] newArray(int size) {
            return new KeystoreResponse[size];
        }
    };
    public final int error_code_;
    public final String error_msg_;

    protected KeystoreResponse(int error_code, String error_msg) {
        this.error_code_ = error_code;
        this.error_msg_ = error_msg;
    }

    public final int getErrorCode() {
        return this.error_code_;
    }

    public final String getErrorMessage() {
        return this.error_msg_;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.error_code_);
        out.writeString(this.error_msg_);
    }
}
