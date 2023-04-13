package android.telephony.mbms;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes3.dex */
public final class UriPathPair implements Parcelable {
    public static final Parcelable.Creator<UriPathPair> CREATOR = new Parcelable.Creator<UriPathPair>() { // from class: android.telephony.mbms.UriPathPair.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UriPathPair createFromParcel(Parcel in) {
            return new UriPathPair(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UriPathPair[] newArray(int size) {
            return new UriPathPair[size];
        }
    };
    private final Uri mContentUri;
    private final Uri mFilePathUri;

    public UriPathPair(Uri fileUri, Uri contentUri) {
        if (fileUri == null || !"file".equals(fileUri.getScheme())) {
            throw new IllegalArgumentException("File URI must have file scheme");
        }
        if (contentUri == null || !"content".equals(contentUri.getScheme())) {
            throw new IllegalArgumentException("Content URI must have content scheme");
        }
        this.mFilePathUri = fileUri;
        this.mContentUri = contentUri;
    }

    private UriPathPair(Parcel in) {
        this.mFilePathUri = (Uri) in.readParcelable(Uri.class.getClassLoader(), Uri.class);
        this.mContentUri = (Uri) in.readParcelable(Uri.class.getClassLoader(), Uri.class);
    }

    public Uri getFilePathUri() {
        return this.mFilePathUri;
    }

    public Uri getContentUri() {
        return this.mContentUri;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mFilePathUri, flags);
        dest.writeParcelable(this.mContentUri, flags);
    }
}
