package android.print;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.UUID;
/* loaded from: classes3.dex */
public final class PrintJobId implements Parcelable {
    public static final Parcelable.Creator<PrintJobId> CREATOR = new Parcelable.Creator<PrintJobId>() { // from class: android.print.PrintJobId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrintJobId createFromParcel(Parcel parcel) {
            return new PrintJobId((String) Preconditions.checkNotNull(parcel.readString()));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrintJobId[] newArray(int size) {
            return new PrintJobId[size];
        }
    };
    private final String mValue;

    public PrintJobId() {
        this(UUID.randomUUID().toString());
    }

    public PrintJobId(String value) {
        this.mValue = value;
    }

    public int hashCode() {
        int result = (1 * 31) + this.mValue.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrintJobId other = (PrintJobId) obj;
        if (this.mValue.equals(other.mValue)) {
            return true;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mValue);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String flattenToString() {
        return this.mValue;
    }

    public static PrintJobId unflattenFromString(String string) {
        return new PrintJobId(string);
    }
}
