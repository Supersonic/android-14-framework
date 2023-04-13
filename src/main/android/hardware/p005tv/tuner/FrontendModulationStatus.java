package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.FrontendModulationStatus */
/* loaded from: classes2.dex */
public final class FrontendModulationStatus implements Parcelable {
    public static final Parcelable.Creator<FrontendModulationStatus> CREATOR = new Parcelable.Creator<FrontendModulationStatus>() { // from class: android.hardware.tv.tuner.FrontendModulationStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendModulationStatus createFromParcel(Parcel _aidl_source) {
            return new FrontendModulationStatus(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendModulationStatus[] newArray(int _aidl_size) {
            return new FrontendModulationStatus[_aidl_size];
        }
    };
    public static final int dvbc = 0;
    public static final int dvbs = 1;
    public static final int isdbs = 2;
    public static final int isdbs3 = 3;
    public static final int isdbt = 4;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.FrontendModulationStatus$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int dvbc = 0;
        public static final int dvbs = 1;
        public static final int isdbs = 2;
        public static final int isdbs3 = 3;
        public static final int isdbt = 4;
    }

    public FrontendModulationStatus() {
        this._tag = 0;
        this._value = 0;
    }

    private FrontendModulationStatus(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private FrontendModulationStatus(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static FrontendModulationStatus dvbc(int _value) {
        return new FrontendModulationStatus(0, Integer.valueOf(_value));
    }

    public int getDvbc() {
        _assertTag(0);
        return ((Integer) this._value).intValue();
    }

    public void setDvbc(int _value) {
        _set(0, Integer.valueOf(_value));
    }

    public static FrontendModulationStatus dvbs(int _value) {
        return new FrontendModulationStatus(1, Integer.valueOf(_value));
    }

    public int getDvbs() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setDvbs(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static FrontendModulationStatus isdbs(int _value) {
        return new FrontendModulationStatus(2, Integer.valueOf(_value));
    }

    public int getIsdbs() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setIsdbs(int _value) {
        _set(2, Integer.valueOf(_value));
    }

    public static FrontendModulationStatus isdbs3(int _value) {
        return new FrontendModulationStatus(3, Integer.valueOf(_value));
    }

    public int getIsdbs3() {
        _assertTag(3);
        return ((Integer) this._value).intValue();
    }

    public void setIsdbs3(int _value) {
        _set(3, Integer.valueOf(_value));
    }

    public static FrontendModulationStatus isdbt(int _value) {
        return new FrontendModulationStatus(4, Integer.valueOf(_value));
    }

    public int getIsdbt() {
        _assertTag(4);
        return ((Integer) this._value).intValue();
    }

    public void setIsdbt(int _value) {
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
                _aidl_parcel.writeInt(getDvbc());
                return;
            case 1:
                _aidl_parcel.writeInt(getDvbs());
                return;
            case 2:
                _aidl_parcel.writeInt(getIsdbs());
                return;
            case 3:
                _aidl_parcel.writeInt(getIsdbs3());
                return;
            case 4:
                _aidl_parcel.writeInt(getIsdbt());
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
                return "isdbs";
            case 3:
                return "isdbs3";
            case 4:
                return "isdbt";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
