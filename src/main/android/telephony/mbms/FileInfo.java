package android.telephony.mbms;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class FileInfo implements Parcelable {
    public static final Parcelable.Creator<FileInfo> CREATOR = new Parcelable.Creator<FileInfo>() { // from class: android.telephony.mbms.FileInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FileInfo createFromParcel(Parcel source) {
            return new FileInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
    private final String mimeType;
    private final Uri uri;

    @SystemApi
    public FileInfo(Uri uri, String mimeType) {
        this.uri = uri;
        this.mimeType = mimeType;
    }

    private FileInfo(Parcel in) {
        this.uri = (Uri) in.readParcelable(null, Uri.class);
        this.mimeType = in.readString();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.uri, flags);
        dest.writeString(this.mimeType);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Uri getUri() {
        return this.uri;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileInfo fileInfo = (FileInfo) o;
        if (Objects.equals(this.uri, fileInfo.uri) && Objects.equals(this.mimeType, fileInfo.mimeType)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.uri, this.mimeType);
    }
}
