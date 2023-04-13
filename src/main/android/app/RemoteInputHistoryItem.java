package android.app;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class RemoteInputHistoryItem implements Parcelable {
    public static final Parcelable.Creator<RemoteInputHistoryItem> CREATOR = new Parcelable.Creator<RemoteInputHistoryItem>() { // from class: android.app.RemoteInputHistoryItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoteInputHistoryItem createFromParcel(Parcel in) {
            return new RemoteInputHistoryItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoteInputHistoryItem[] newArray(int size) {
            return new RemoteInputHistoryItem[size];
        }
    };
    private String mMimeType;
    private CharSequence mText;
    private Uri mUri;

    public RemoteInputHistoryItem(String mimeType, Uri uri, CharSequence backupText) {
        this.mMimeType = mimeType;
        this.mUri = uri;
        this.mText = Notification.safeCharSequence(backupText);
    }

    public RemoteInputHistoryItem(CharSequence text) {
        this.mText = Notification.safeCharSequence(text);
    }

    protected RemoteInputHistoryItem(Parcel in) {
        this.mText = in.readCharSequence();
        this.mMimeType = in.readStringNoHelper();
        this.mUri = (Uri) in.readParcelable(Uri.class.getClassLoader(), Uri.class);
    }

    public CharSequence getText() {
        return this.mText;
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public Uri getUri() {
        return this.mUri;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeCharSequence(this.mText);
        dest.writeStringNoHelper(this.mMimeType);
        dest.writeParcelable(this.mUri, flags);
    }
}
