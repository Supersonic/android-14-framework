package android.app;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class MissingForegroundServiceTypeException extends ForegroundServiceTypeException implements Parcelable {
    public static final Parcelable.Creator<MissingForegroundServiceTypeException> CREATOR = new Parcelable.Creator<MissingForegroundServiceTypeException>() { // from class: android.app.MissingForegroundServiceTypeException.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MissingForegroundServiceTypeException createFromParcel(Parcel source) {
            return new MissingForegroundServiceTypeException(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MissingForegroundServiceTypeException[] newArray(int size) {
            return new MissingForegroundServiceTypeException[size];
        }
    };

    public MissingForegroundServiceTypeException(String message) {
        super(message);
    }

    MissingForegroundServiceTypeException(Parcel source) {
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
