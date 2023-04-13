package android.app;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class GrantedUriPermission implements Parcelable {
    public static final Parcelable.Creator<GrantedUriPermission> CREATOR = new Parcelable.Creator<GrantedUriPermission>() { // from class: android.app.GrantedUriPermission.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GrantedUriPermission createFromParcel(Parcel in) {
            return new GrantedUriPermission(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GrantedUriPermission[] newArray(int size) {
            return new GrantedUriPermission[size];
        }
    };
    public final String packageName;
    public final Uri uri;

    public GrantedUriPermission(Uri uri, String packageName) {
        this.uri = uri;
        this.packageName = packageName;
    }

    public String toString() {
        return this.packageName + ":" + this.uri;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.uri, flags);
        out.writeString(this.packageName);
    }

    private GrantedUriPermission(Parcel in) {
        this.uri = (Uri) in.readParcelable(null, Uri.class);
        this.packageName = in.readString();
    }
}
