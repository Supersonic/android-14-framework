package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public final class IntegerPolicyValue extends PolicyValue<Integer> {
    public static final Parcelable.Creator<IntegerPolicyValue> CREATOR = new Parcelable.Creator<IntegerPolicyValue>() { // from class: android.app.admin.IntegerPolicyValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IntegerPolicyValue createFromParcel(Parcel source) {
            return new IntegerPolicyValue(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IntegerPolicyValue[] newArray(int size) {
            return new IntegerPolicyValue[size];
        }
    };

    public IntegerPolicyValue(int value) {
        super(Integer.valueOf(value));
    }

    private IntegerPolicyValue(Parcel source) {
        this(source.readInt());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntegerPolicyValue other = (IntegerPolicyValue) o;
        return Objects.equals(getValue(), other.getValue());
    }

    public int hashCode() {
        return Objects.hash(getValue());
    }

    public String toString() {
        return "IntegerPolicyValue { mValue= " + getValue() + " }";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getValue().intValue());
    }
}
