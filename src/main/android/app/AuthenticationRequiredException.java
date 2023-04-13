package android.app;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AuthenticationRequiredException extends SecurityException implements Parcelable {
    public static final Parcelable.Creator<AuthenticationRequiredException> CREATOR = new Parcelable.Creator<AuthenticationRequiredException>() { // from class: android.app.AuthenticationRequiredException.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AuthenticationRequiredException createFromParcel(Parcel source) {
            return new AuthenticationRequiredException(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AuthenticationRequiredException[] newArray(int size) {
            return new AuthenticationRequiredException[size];
        }
    };
    private static final String TAG = "AuthenticationRequiredException";
    private final PendingIntent mUserAction;

    public AuthenticationRequiredException(Parcel in) {
        this(new SecurityException(in.readString()), PendingIntent.CREATOR.createFromParcel(in));
    }

    public AuthenticationRequiredException(Throwable cause, PendingIntent userAction) {
        super(cause.getMessage());
        this.mUserAction = (PendingIntent) Objects.requireNonNull(userAction);
    }

    public PendingIntent getUserAction() {
        return this.mUserAction;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMessage());
        this.mUserAction.writeToParcel(dest, flags);
    }
}
