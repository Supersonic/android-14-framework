package android.telephony.ims;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class RtpHeaderExtensionType implements Parcelable {
    public static final Parcelable.Creator<RtpHeaderExtensionType> CREATOR = new Parcelable.Creator<RtpHeaderExtensionType>() { // from class: android.telephony.ims.RtpHeaderExtensionType.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RtpHeaderExtensionType createFromParcel(Parcel in) {
            return new RtpHeaderExtensionType(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RtpHeaderExtensionType[] newArray(int size) {
            return new RtpHeaderExtensionType[size];
        }
    };
    private int mLocalIdentifier;
    private Uri mUri;

    public RtpHeaderExtensionType(int localIdentifier, Uri uri) {
        if (localIdentifier < 1 || localIdentifier > 13) {
            throw new IllegalArgumentException("localIdentifier must be in range 1-14");
        }
        if (uri == null) {
            throw new NullPointerException("uri is required.");
        }
        this.mLocalIdentifier = localIdentifier;
        this.mUri = uri;
    }

    private RtpHeaderExtensionType(Parcel in) {
        this.mLocalIdentifier = in.readInt();
        this.mUri = (Uri) in.readParcelable(Uri.class.getClassLoader(), Uri.class);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mLocalIdentifier);
        dest.writeParcelable(this.mUri, flags);
    }

    public int getLocalIdentifier() {
        return this.mLocalIdentifier;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RtpHeaderExtensionType that = (RtpHeaderExtensionType) o;
        if (this.mLocalIdentifier == that.mLocalIdentifier && this.mUri.equals(that.mUri)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mLocalIdentifier), this.mUri);
    }

    public String toString() {
        return "RtpHeaderExtensionType{mLocalIdentifier=" + this.mLocalIdentifier + ", mUri=" + this.mUri + "}";
    }
}
