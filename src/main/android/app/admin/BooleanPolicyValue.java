package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public final class BooleanPolicyValue extends PolicyValue<Boolean> {
    public static final Parcelable.Creator<BooleanPolicyValue> CREATOR = new Parcelable.Creator<BooleanPolicyValue>() { // from class: android.app.admin.BooleanPolicyValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BooleanPolicyValue createFromParcel(Parcel source) {
            return new BooleanPolicyValue(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BooleanPolicyValue[] newArray(int size) {
            return new BooleanPolicyValue[size];
        }
    };

    public BooleanPolicyValue(boolean value) {
        super(Boolean.valueOf(value));
    }

    private BooleanPolicyValue(Parcel source) {
        this(source.readBoolean());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BooleanPolicyValue other = (BooleanPolicyValue) o;
        return Objects.equals(getValue(), other.getValue());
    }

    public int hashCode() {
        return Objects.hash(getValue());
    }

    public String toString() {
        return "BooleanPolicyValue { mValue= " + getValue() + " }";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(getValue().booleanValue());
    }
}
