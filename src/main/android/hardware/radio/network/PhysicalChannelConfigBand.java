package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public final class PhysicalChannelConfigBand implements Parcelable {
    public static final Parcelable.Creator<PhysicalChannelConfigBand> CREATOR = new Parcelable.Creator<PhysicalChannelConfigBand>() { // from class: android.hardware.radio.network.PhysicalChannelConfigBand.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PhysicalChannelConfigBand createFromParcel(Parcel _aidl_source) {
            return new PhysicalChannelConfigBand(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PhysicalChannelConfigBand[] newArray(int _aidl_size) {
            return new PhysicalChannelConfigBand[_aidl_size];
        }
    };
    public static final int eutranBand = 3;
    public static final int geranBand = 1;
    public static final int ngranBand = 4;
    public static final int noinit = 0;
    public static final int utranBand = 2;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int eutranBand = 3;
        public static final int geranBand = 1;
        public static final int ngranBand = 4;
        public static final int noinit = 0;
        public static final int utranBand = 2;
    }

    public PhysicalChannelConfigBand() {
        this._tag = 0;
        this._value = false;
    }

    private PhysicalChannelConfigBand(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private PhysicalChannelConfigBand(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static PhysicalChannelConfigBand noinit(boolean _value) {
        return new PhysicalChannelConfigBand(0, Boolean.valueOf(_value));
    }

    public boolean getNoinit() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setNoinit(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static PhysicalChannelConfigBand geranBand(int _value) {
        return new PhysicalChannelConfigBand(1, Integer.valueOf(_value));
    }

    public int getGeranBand() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setGeranBand(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static PhysicalChannelConfigBand utranBand(int _value) {
        return new PhysicalChannelConfigBand(2, Integer.valueOf(_value));
    }

    public int getUtranBand() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setUtranBand(int _value) {
        _set(2, Integer.valueOf(_value));
    }

    public static PhysicalChannelConfigBand eutranBand(int _value) {
        return new PhysicalChannelConfigBand(3, Integer.valueOf(_value));
    }

    public int getEutranBand() {
        _assertTag(3);
        return ((Integer) this._value).intValue();
    }

    public void setEutranBand(int _value) {
        _set(3, Integer.valueOf(_value));
    }

    public static PhysicalChannelConfigBand ngranBand(int _value) {
        return new PhysicalChannelConfigBand(4, Integer.valueOf(_value));
    }

    public int getNgranBand() {
        _assertTag(4);
        return ((Integer) this._value).intValue();
    }

    public void setNgranBand(int _value) {
        _set(4, Integer.valueOf(_value));
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
                _aidl_parcel.writeInt(getGeranBand());
                return;
            case 2:
                _aidl_parcel.writeInt(getUtranBand());
                return;
            case 3:
                _aidl_parcel.writeInt(getEutranBand());
                return;
            case 4:
                _aidl_parcel.writeInt(getNgranBand());
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
            case 2:
                int _aidl_value3 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value3));
                return;
            case 3:
                int _aidl_value4 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value4));
                return;
            case 4:
                int _aidl_value5 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value5));
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
                return "android.hardware.radio.network.PhysicalChannelConfigBand.noinit(" + getNoinit() + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.hardware.radio.network.PhysicalChannelConfigBand.geranBand(" + GeranBands$$.toString(getGeranBand()) + NavigationBarInflaterView.KEY_CODE_END;
            case 2:
                return "android.hardware.radio.network.PhysicalChannelConfigBand.utranBand(" + UtranBands$$.toString(getUtranBand()) + NavigationBarInflaterView.KEY_CODE_END;
            case 3:
                return "android.hardware.radio.network.PhysicalChannelConfigBand.eutranBand(" + EutranBands$$.toString(getEutranBand()) + NavigationBarInflaterView.KEY_CODE_END;
            case 4:
                return "android.hardware.radio.network.PhysicalChannelConfigBand.ngranBand(" + NgranBands$$.toString(getNgranBand()) + NavigationBarInflaterView.KEY_CODE_END;
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
                return "geranBand";
            case 2:
                return "utranBand";
            case 3:
                return "eutranBand";
            case 4:
                return "ngranBand";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
