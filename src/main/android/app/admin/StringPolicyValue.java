package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public final class StringPolicyValue extends PolicyValue<String> {
    public static final Parcelable.Creator<StringPolicyValue> CREATOR = new Parcelable.Creator<StringPolicyValue>() { // from class: android.app.admin.StringPolicyValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StringPolicyValue createFromParcel(Parcel source) {
            return new StringPolicyValue(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StringPolicyValue[] newArray(int size) {
            return new StringPolicyValue[size];
        }
    };

    public StringPolicyValue(String value) {
        super(value);
    }

    private StringPolicyValue(Parcel source) {
        super(source.readString());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StringPolicyValue other = (StringPolicyValue) o;
        return Objects.equals(getValue(), other.getValue());
    }

    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "StringPolicyValue { " + getValue() + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getValue());
    }
}
