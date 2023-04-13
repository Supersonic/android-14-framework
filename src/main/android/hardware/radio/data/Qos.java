package android.hardware.radio.data;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class Qos implements Parcelable {
    public static final Parcelable.Creator<Qos> CREATOR = new Parcelable.Creator<Qos>() { // from class: android.hardware.radio.data.Qos.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Qos createFromParcel(Parcel _aidl_source) {
            return new Qos(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Qos[] newArray(int _aidl_size) {
            return new Qos[_aidl_size];
        }
    };
    public static final int eps = 1;
    public static final int noinit = 0;

    /* renamed from: nr */
    public static final int f192nr = 2;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int eps = 1;
        public static final int noinit = 0;

        /* renamed from: nr */
        public static final int f193nr = 2;
    }

    public Qos() {
        this._tag = 0;
        this._value = false;
    }

    private Qos(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private Qos(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static Qos noinit(boolean _value) {
        return new Qos(0, Boolean.valueOf(_value));
    }

    public boolean getNoinit() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setNoinit(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static Qos eps(EpsQos _value) {
        return new Qos(1, _value);
    }

    public EpsQos getEps() {
        _assertTag(1);
        return (EpsQos) this._value;
    }

    public void setEps(EpsQos _value) {
        _set(1, _value);
    }

    /* renamed from: nr */
    public static Qos m164nr(NrQos _value) {
        return new Qos(2, _value);
    }

    public NrQos getNr() {
        _assertTag(2);
        return (NrQos) this._value;
    }

    public void setNr(NrQos _value) {
        _set(2, _value);
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
                _aidl_parcel.writeTypedObject(getEps(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedObject(getNr(), _aidl_flag);
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
                EpsQos _aidl_value2 = (EpsQos) _aidl_parcel.readTypedObject(EpsQos.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                NrQos _aidl_value3 = (NrQos) _aidl_parcel.readTypedObject(NrQos.CREATOR);
                _set(_aidl_tag, _aidl_value3);
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 1:
                int _mask = 0 | describeContents(getEps());
                return _mask;
            case 2:
                int _mask2 = 0 | describeContents(getNr());
                return _mask2;
            default:
                return 0;
        }
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }

    public String toString() {
        switch (this._tag) {
            case 0:
                return "android.hardware.radio.data.Qos.noinit(" + getNoinit() + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.hardware.radio.data.Qos.eps(" + Objects.toString(getEps()) + NavigationBarInflaterView.KEY_CODE_END;
            case 2:
                return "android.hardware.radio.data.Qos.nr(" + Objects.toString(getNr()) + NavigationBarInflaterView.KEY_CODE_END;
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
                return "eps";
            case 2:
                return "nr";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
