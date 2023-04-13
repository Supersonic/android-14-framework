package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public final class RadioAccessSpecifierBands implements Parcelable {
    public static final Parcelable.Creator<RadioAccessSpecifierBands> CREATOR = new Parcelable.Creator<RadioAccessSpecifierBands>() { // from class: android.hardware.radio.network.RadioAccessSpecifierBands.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RadioAccessSpecifierBands createFromParcel(Parcel _aidl_source) {
            return new RadioAccessSpecifierBands(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RadioAccessSpecifierBands[] newArray(int _aidl_size) {
            return new RadioAccessSpecifierBands[_aidl_size];
        }
    };
    public static final int eutranBands = 3;
    public static final int geranBands = 1;
    public static final int ngranBands = 4;
    public static final int noinit = 0;
    public static final int utranBands = 2;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int eutranBands = 3;
        public static final int geranBands = 1;
        public static final int ngranBands = 4;
        public static final int noinit = 0;
        public static final int utranBands = 2;
    }

    public RadioAccessSpecifierBands() {
        this._tag = 0;
        this._value = false;
    }

    private RadioAccessSpecifierBands(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private RadioAccessSpecifierBands(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static RadioAccessSpecifierBands noinit(boolean _value) {
        return new RadioAccessSpecifierBands(0, Boolean.valueOf(_value));
    }

    public boolean getNoinit() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setNoinit(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static RadioAccessSpecifierBands geranBands(int[] _value) {
        return new RadioAccessSpecifierBands(1, _value);
    }

    public int[] getGeranBands() {
        _assertTag(1);
        return (int[]) this._value;
    }

    public void setGeranBands(int[] _value) {
        _set(1, _value);
    }

    public static RadioAccessSpecifierBands utranBands(int[] _value) {
        return new RadioAccessSpecifierBands(2, _value);
    }

    public int[] getUtranBands() {
        _assertTag(2);
        return (int[]) this._value;
    }

    public void setUtranBands(int[] _value) {
        _set(2, _value);
    }

    public static RadioAccessSpecifierBands eutranBands(int[] _value) {
        return new RadioAccessSpecifierBands(3, _value);
    }

    public int[] getEutranBands() {
        _assertTag(3);
        return (int[]) this._value;
    }

    public void setEutranBands(int[] _value) {
        _set(3, _value);
    }

    public static RadioAccessSpecifierBands ngranBands(int[] _value) {
        return new RadioAccessSpecifierBands(4, _value);
    }

    public int[] getNgranBands() {
        _assertTag(4);
        return (int[]) this._value;
    }

    public void setNgranBands(int[] _value) {
        _set(4, _value);
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
                _aidl_parcel.writeIntArray(getGeranBands());
                return;
            case 2:
                _aidl_parcel.writeIntArray(getUtranBands());
                return;
            case 3:
                _aidl_parcel.writeIntArray(getEutranBands());
                return;
            case 4:
                _aidl_parcel.writeIntArray(getNgranBands());
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
                int[] _aidl_value2 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                int[] _aidl_value3 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value3);
                return;
            case 3:
                int[] _aidl_value4 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                int[] _aidl_value5 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value5);
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
                return "android.hardware.radio.network.RadioAccessSpecifierBands.noinit(" + getNoinit() + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.hardware.radio.network.RadioAccessSpecifierBands.geranBands(" + GeranBands$$.arrayToString(getGeranBands()) + NavigationBarInflaterView.KEY_CODE_END;
            case 2:
                return "android.hardware.radio.network.RadioAccessSpecifierBands.utranBands(" + UtranBands$$.arrayToString(getUtranBands()) + NavigationBarInflaterView.KEY_CODE_END;
            case 3:
                return "android.hardware.radio.network.RadioAccessSpecifierBands.eutranBands(" + EutranBands$$.arrayToString(getEutranBands()) + NavigationBarInflaterView.KEY_CODE_END;
            case 4:
                return "android.hardware.radio.network.RadioAccessSpecifierBands.ngranBands(" + NgranBands$$.arrayToString(getNgranBands()) + NavigationBarInflaterView.KEY_CODE_END;
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
                return "geranBands";
            case 2:
                return "utranBands";
            case 3:
                return "eutranBands";
            case 4:
                return "ngranBands";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
