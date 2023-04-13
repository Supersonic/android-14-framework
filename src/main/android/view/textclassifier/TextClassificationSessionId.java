package android.view.textclassifier;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
/* loaded from: classes4.dex */
public final class TextClassificationSessionId implements Parcelable {
    public static final Parcelable.Creator<TextClassificationSessionId> CREATOR = new Parcelable.Creator<TextClassificationSessionId>() { // from class: android.view.textclassifier.TextClassificationSessionId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextClassificationSessionId createFromParcel(Parcel parcel) {
            return new TextClassificationSessionId(parcel.readString(), parcel.readStrongBinder());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextClassificationSessionId[] newArray(int size) {
            return new TextClassificationSessionId[size];
        }
    };
    private final IBinder mToken;
    private final String mValue;

    public TextClassificationSessionId() {
        this(UUID.randomUUID().toString(), new Binder());
    }

    public TextClassificationSessionId(String value, IBinder token) {
        this.mValue = (String) Objects.requireNonNull(value);
        this.mToken = (IBinder) Objects.requireNonNull(token);
    }

    public IBinder getToken() {
        return this.mToken;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TextClassificationSessionId that = (TextClassificationSessionId) o;
        if (Objects.equals(this.mValue, that.mValue) && Objects.equals(this.mToken, that.mToken)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mValue, this.mToken);
    }

    public String toString() {
        return String.format(Locale.US, "TextClassificationSessionId {%s}", this.mValue);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mValue);
        parcel.writeStrongBinder(this.mToken);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getValue() {
        return this.mValue;
    }
}
