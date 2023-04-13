package android.app.admin;

import android.content.ComponentName;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ComponentNamePolicyValue extends PolicyValue<ComponentName> {
    public static final Parcelable.Creator<ComponentNamePolicyValue> CREATOR = new Parcelable.Creator<ComponentNamePolicyValue>() { // from class: android.app.admin.ComponentNamePolicyValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ComponentNamePolicyValue createFromParcel(Parcel source) {
            return new ComponentNamePolicyValue(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ComponentNamePolicyValue[] newArray(int size) {
            return new ComponentNamePolicyValue[size];
        }
    };

    public ComponentNamePolicyValue(ComponentName value) {
        super(value);
    }

    private ComponentNamePolicyValue(Parcel source) {
        this((ComponentName) source.readParcelable(ComponentName.class.getClassLoader()));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ComponentNamePolicyValue other = (ComponentNamePolicyValue) o;
        return Objects.equals(getValue(), other.getValue());
    }

    public int hashCode() {
        return Objects.hash(getValue());
    }

    public String toString() {
        return "ComponentNamePolicyValue { mValue= " + getValue() + " }";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(getValue(), flags);
    }
}
