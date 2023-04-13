package android.credentials;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Credential implements Parcelable {
    public static final Parcelable.Creator<Credential> CREATOR = new Parcelable.Creator<Credential>() { // from class: android.credentials.Credential.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Credential[] newArray(int size) {
            return new Credential[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Credential createFromParcel(Parcel in) {
            return new Credential(in);
        }
    };
    public static final String TYPE_PASSWORD_CREDENTIAL = "android.credentials.TYPE_PASSWORD_CREDENTIAL";
    private final Bundle mData;
    private final String mType;

    public String getType() {
        return this.mType;
    }

    public Bundle getData() {
        return this.mData;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mType);
        dest.writeBundle(this.mData);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "Credential {type=" + this.mType + ", data=" + this.mData + "}";
    }

    public Credential(String type, Bundle data) {
        this.mType = (String) Preconditions.checkStringNotEmpty(type, "type must not be empty");
        this.mData = (Bundle) Objects.requireNonNull(data, "data must not be null");
    }

    private Credential(Parcel in) {
        String type = in.readString8();
        Bundle data = in.readBundle();
        this.mType = type;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) type);
        this.mData = data;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) data);
    }
}
