package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.FrontendModulation */
/* loaded from: classes2.dex */
public final class FrontendModulation implements Parcelable {
    public static final Parcelable.Creator<FrontendModulation> CREATOR = new Parcelable.Creator<FrontendModulation>() { // from class: android.hardware.tv.tuner.FrontendModulation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendModulation createFromParcel(Parcel _aidl_source) {
            return new FrontendModulation(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendModulation[] newArray(int _aidl_size) {
            return new FrontendModulation[_aidl_size];
        }
    };
    public static final int atsc = 6;
    public static final int atsc3 = 7;
    public static final int dtmb = 8;
    public static final int dvbc = 0;
    public static final int dvbs = 1;
    public static final int dvbt = 2;
    public static final int isdbs = 3;
    public static final int isdbs3 = 4;
    public static final int isdbt = 5;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.FrontendModulation$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int atsc = 6;
        public static final int atsc3 = 7;
        public static final int dtmb = 8;
        public static final int dvbc = 0;
        public static final int dvbs = 1;
        public static final int dvbt = 2;
        public static final int isdbs = 3;
        public static final int isdbs3 = 4;
        public static final int isdbt = 5;
    }

    public FrontendModulation() {
        this._tag = 0;
        this._value = 0;
    }

    private FrontendModulation(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private FrontendModulation(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static FrontendModulation dvbc(int _value) {
        return new FrontendModulation(0, Integer.valueOf(_value));
    }

    public int getDvbc() {
        _assertTag(0);
        return ((Integer) this._value).intValue();
    }

    public void setDvbc(int _value) {
        _set(0, Integer.valueOf(_value));
    }

    public static FrontendModulation dvbs(int _value) {
        return new FrontendModulation(1, Integer.valueOf(_value));
    }

    public int getDvbs() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setDvbs(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static FrontendModulation dvbt(int _value) {
        return new FrontendModulation(2, Integer.valueOf(_value));
    }

    public int getDvbt() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setDvbt(int _value) {
        _set(2, Integer.valueOf(_value));
    }

    public static FrontendModulation isdbs(int _value) {
        return new FrontendModulation(3, Integer.valueOf(_value));
    }

    public int getIsdbs() {
        _assertTag(3);
        return ((Integer) this._value).intValue();
    }

    public void setIsdbs(int _value) {
        _set(3, Integer.valueOf(_value));
    }

    public static FrontendModulation isdbs3(int _value) {
        return new FrontendModulation(4, Integer.valueOf(_value));
    }

    public int getIsdbs3() {
        _assertTag(4);
        return ((Integer) this._value).intValue();
    }

    public void setIsdbs3(int _value) {
        _set(4, Integer.valueOf(_value));
    }

    public static FrontendModulation isdbt(int _value) {
        return new FrontendModulation(5, Integer.valueOf(_value));
    }

    public int getIsdbt() {
        _assertTag(5);
        return ((Integer) this._value).intValue();
    }

    public void setIsdbt(int _value) {
        _set(5, Integer.valueOf(_value));
    }

    public static FrontendModulation atsc(int _value) {
        return new FrontendModulation(6, Integer.valueOf(_value));
    }

    public int getAtsc() {
        _assertTag(6);
        return ((Integer) this._value).intValue();
    }

    public void setAtsc(int _value) {
        _set(6, Integer.valueOf(_value));
    }

    public static FrontendModulation atsc3(int _value) {
        return new FrontendModulation(7, Integer.valueOf(_value));
    }

    public int getAtsc3() {
        _assertTag(7);
        return ((Integer) this._value).intValue();
    }

    public void setAtsc3(int _value) {
        _set(7, Integer.valueOf(_value));
    }

    public static FrontendModulation dtmb(int _value) {
        return new FrontendModulation(8, Integer.valueOf(_value));
    }

    public int getDtmb() {
        _assertTag(8);
        return ((Integer) this._value).intValue();
    }

    public void setDtmb(int _value) {
        _set(8, Integer.valueOf(_value));
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
                _aidl_parcel.writeInt(getDvbc());
                return;
            case 1:
                _aidl_parcel.writeInt(getDvbs());
                return;
            case 2:
                _aidl_parcel.writeInt(getDvbt());
                return;
            case 3:
                _aidl_parcel.writeInt(getIsdbs());
                return;
            case 4:
                _aidl_parcel.writeInt(getIsdbs3());
                return;
            case 5:
                _aidl_parcel.writeInt(getIsdbt());
                return;
            case 6:
                _aidl_parcel.writeInt(getAtsc());
                return;
            case 7:
                _aidl_parcel.writeInt(getAtsc3());
                return;
            case 8:
                _aidl_parcel.writeInt(getDtmb());
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                int _aidl_value = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value));
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
            case 5:
                int _aidl_value6 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value6));
                return;
            case 6:
                int _aidl_value7 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value7));
                return;
            case 7:
                int _aidl_value8 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value8));
                return;
            case 8:
                int _aidl_value9 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value9));
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

    private void _assertTag(int tag) {
        if (getTag() != tag) {
            throw new IllegalStateException("bad access: " + _tagString(tag) + ", " + _tagString(getTag()) + " is available.");
        }
    }

    private String _tagString(int _tag) {
        switch (_tag) {
            case 0:
                return "dvbc";
            case 1:
                return "dvbs";
            case 2:
                return "dvbt";
            case 3:
                return "isdbs";
            case 4:
                return "isdbs3";
            case 5:
                return "isdbt";
            case 6:
                return "atsc";
            case 7:
                return "atsc3";
            case 8:
                return "dtmb";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
