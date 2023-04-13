package android.view.autofill;

import android.p008os.Looper;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class AutofillValue implements Parcelable {
    public static final Parcelable.Creator<AutofillValue> CREATOR = new Parcelable.Creator<AutofillValue>() { // from class: android.view.autofill.AutofillValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AutofillValue createFromParcel(Parcel source) {
            return new AutofillValue(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AutofillValue[] newArray(int size) {
            return new AutofillValue[size];
        }
    };
    private static final String TAG = "AutofillValue";
    private final int mType;
    private final Object mValue;

    private AutofillValue(int type, Object value) {
        this.mType = type;
        this.mValue = value;
    }

    public CharSequence getTextValue() {
        Preconditions.checkState(isText(), "value must be a text value, not type=%d", Integer.valueOf(this.mType));
        return (CharSequence) this.mValue;
    }

    public boolean isText() {
        return this.mType == 1;
    }

    public boolean getToggleValue() {
        Preconditions.checkState(isToggle(), "value must be a toggle value, not type=%d", Integer.valueOf(this.mType));
        return ((Boolean) this.mValue).booleanValue();
    }

    public boolean isToggle() {
        return this.mType == 2;
    }

    public int getListValue() {
        Preconditions.checkState(isList(), "value must be a list value, not type=%d", Integer.valueOf(this.mType));
        return ((Integer) this.mValue).intValue();
    }

    public boolean isList() {
        return this.mType == 3;
    }

    public long getDateValue() {
        Preconditions.checkState(isDate(), "value must be a date value, not type=%d", Integer.valueOf(this.mType));
        return ((Long) this.mValue).longValue();
    }

    public boolean isDate() {
        return this.mType == 4;
    }

    public boolean isEmpty() {
        return isText() && ((CharSequence) this.mValue).length() == 0;
    }

    public int hashCode() {
        return this.mType + this.mValue.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AutofillValue other = (AutofillValue) obj;
        if (this.mType != other.mType) {
            return false;
        }
        if (isText()) {
            return this.mValue.toString().equals(other.mValue.toString());
        }
        return Objects.equals(this.mValue, other.mValue);
    }

    public String toString() {
        if (Helper.sDebug) {
            StringBuilder string = new StringBuilder().append("[type=").append(this.mType).append(", value=");
            if (isText()) {
                Helper.appendRedacted(string, (CharSequence) this.mValue);
            } else {
                string.append(this.mValue);
            }
            return string.append(']').toString();
        }
        return super.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mType);
        switch (this.mType) {
            case 1:
                parcel.writeCharSequence((CharSequence) this.mValue);
                return;
            case 2:
                parcel.writeInt(((Boolean) this.mValue).booleanValue() ? 1 : 0);
                return;
            case 3:
                parcel.writeInt(((Integer) this.mValue).intValue());
                return;
            case 4:
                parcel.writeLong(((Long) this.mValue).longValue());
                return;
            default:
                return;
        }
    }

    private AutofillValue(Parcel parcel) {
        int readInt = parcel.readInt();
        this.mType = readInt;
        switch (readInt) {
            case 1:
                this.mValue = parcel.readCharSequence();
                return;
            case 2:
                int rawValue = parcel.readInt();
                this.mValue = Boolean.valueOf(rawValue != 0);
                return;
            case 3:
                this.mValue = Integer.valueOf(parcel.readInt());
                return;
            case 4:
                this.mValue = Long.valueOf(parcel.readLong());
                return;
            default:
                throw new IllegalArgumentException("type=" + readInt + " not valid");
        }
    }

    public static AutofillValue forText(CharSequence value) {
        if (Helper.sVerbose && !Looper.getMainLooper().isCurrentThread()) {
            Log.m106v(TAG, "forText() not called on main thread: " + Thread.currentThread());
        }
        if (value == null) {
            return null;
        }
        return new AutofillValue(1, TextUtils.trimNoCopySpans(value));
    }

    public static AutofillValue forToggle(boolean value) {
        return new AutofillValue(2, Boolean.valueOf(value));
    }

    public static AutofillValue forList(int value) {
        return new AutofillValue(3, Integer.valueOf(value));
    }

    public static AutofillValue forDate(long value) {
        return new AutofillValue(4, Long.valueOf(value));
    }
}
