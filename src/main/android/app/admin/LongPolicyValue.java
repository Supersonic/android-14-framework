package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public final class LongPolicyValue extends PolicyValue<Long> {
    public static final Parcelable.Creator<LongPolicyValue> CREATOR = new Parcelable.Creator<LongPolicyValue>() { // from class: android.app.admin.LongPolicyValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LongPolicyValue createFromParcel(Parcel source) {
            return new LongPolicyValue(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LongPolicyValue[] newArray(int size) {
            return new LongPolicyValue[size];
        }
    };

    public LongPolicyValue(long value) {
        super(Long.valueOf(value));
    }

    private LongPolicyValue(Parcel source) {
        this(source.readLong());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LongPolicyValue other = (LongPolicyValue) o;
        return Objects.equals(getValue(), other.getValue());
    }

    public int hashCode() {
        return Objects.hash(getValue());
    }

    public String toString() {
        return "LongPolicyValue { mValue= " + getValue() + " }";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getValue().longValue());
    }
}
