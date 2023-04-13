package android.app;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class BackgroundServiceStartNotAllowedException extends ServiceStartNotAllowedException implements Parcelable {
    public static final Parcelable.Creator<BackgroundServiceStartNotAllowedException> CREATOR = new Parcelable.Creator<BackgroundServiceStartNotAllowedException>() { // from class: android.app.BackgroundServiceStartNotAllowedException.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BackgroundServiceStartNotAllowedException createFromParcel(Parcel source) {
            return new BackgroundServiceStartNotAllowedException(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BackgroundServiceStartNotAllowedException[] newArray(int size) {
            return new BackgroundServiceStartNotAllowedException[size];
        }
    };

    public BackgroundServiceStartNotAllowedException(String message) {
        super(message);
    }

    BackgroundServiceStartNotAllowedException(Parcel source) {
        super(source.readString());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMessage());
    }
}
