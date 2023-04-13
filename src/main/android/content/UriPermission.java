package android.content;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class UriPermission implements Parcelable {
    public static final Parcelable.Creator<UriPermission> CREATOR = new Parcelable.Creator<UriPermission>() { // from class: android.content.UriPermission.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UriPermission createFromParcel(Parcel source) {
            return new UriPermission(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UriPermission[] newArray(int size) {
            return new UriPermission[size];
        }
    };
    public static final long INVALID_TIME = Long.MIN_VALUE;
    private final int mModeFlags;
    private final long mPersistedTime;
    private final Uri mUri;

    public UriPermission(Uri uri, int modeFlags, long persistedTime) {
        this.mUri = uri;
        this.mModeFlags = modeFlags;
        this.mPersistedTime = persistedTime;
    }

    public UriPermission(Parcel in) {
        this.mUri = (Uri) in.readParcelable(null, Uri.class);
        this.mModeFlags = in.readInt();
        this.mPersistedTime = in.readLong();
    }

    public Uri getUri() {
        return this.mUri;
    }

    public boolean isReadPermission() {
        return (this.mModeFlags & 1) != 0;
    }

    public boolean isWritePermission() {
        return (this.mModeFlags & 2) != 0;
    }

    public long getPersistedTime() {
        return this.mPersistedTime;
    }

    public String toString() {
        return "UriPermission {uri=" + this.mUri + ", modeFlags=" + this.mModeFlags + ", persistedTime=" + this.mPersistedTime + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mUri, flags);
        dest.writeInt(this.mModeFlags);
        dest.writeLong(this.mPersistedTime);
    }
}
