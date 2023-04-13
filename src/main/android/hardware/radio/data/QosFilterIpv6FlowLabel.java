package android.hardware.radio.data;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public final class QosFilterIpv6FlowLabel implements Parcelable {
    public static final Parcelable.Creator<QosFilterIpv6FlowLabel> CREATOR = new Parcelable.Creator<QosFilterIpv6FlowLabel>() { // from class: android.hardware.radio.data.QosFilterIpv6FlowLabel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public QosFilterIpv6FlowLabel createFromParcel(Parcel _aidl_source) {
            return new QosFilterIpv6FlowLabel(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public QosFilterIpv6FlowLabel[] newArray(int _aidl_size) {
            return new QosFilterIpv6FlowLabel[_aidl_size];
        }
    };
    public static final int noinit = 0;
    public static final int value = 1;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int noinit = 0;
        public static final int value = 1;
    }

    public QosFilterIpv6FlowLabel() {
        this._tag = 0;
        this._value = false;
    }

    private QosFilterIpv6FlowLabel(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private QosFilterIpv6FlowLabel(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static QosFilterIpv6FlowLabel noinit(boolean _value) {
        return new QosFilterIpv6FlowLabel(0, Boolean.valueOf(_value));
    }

    public boolean getNoinit() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setNoinit(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static QosFilterIpv6FlowLabel value(int _value) {
        return new QosFilterIpv6FlowLabel(1, Integer.valueOf(_value));
    }

    public int getValue() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setValue(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        _aidl_parcel.writeInt(this._tag);
        switch (this._tag) {
            case 0:
                _aidl_parcel.writeBoolean(getNoinit());
                return;
            case 1:
                _aidl_parcel.writeInt(getValue());
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                boolean _aidl_value = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value));
                return;
            case 1:
                int _aidl_value2 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value2));
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        getTag();
        return 0;
    }

    public String toString() {
        switch (this._tag) {
            case 0:
                return "android.hardware.radio.data.QosFilterIpv6FlowLabel.noinit(" + getNoinit() + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.hardware.radio.data.QosFilterIpv6FlowLabel.value(" + getValue() + NavigationBarInflaterView.KEY_CODE_END;
            default:
                throw new IllegalStateException("unknown field: " + this._tag);
        }
    }

    private void _assertTag(int tag) {
        if (getTag() != tag) {
            throw new IllegalStateException("bad access: " + _tagString(tag) + ", " + _tagString(getTag()) + " is available.");
        }
    }

    private String _tagString(int _tag) {
        switch (_tag) {
            case 0:
                return "noinit";
            case 1:
                return "value";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
