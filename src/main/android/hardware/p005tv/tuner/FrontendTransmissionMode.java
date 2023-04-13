package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.FrontendTransmissionMode */
/* loaded from: classes2.dex */
public final class FrontendTransmissionMode implements Parcelable {
    public static final Parcelable.Creator<FrontendTransmissionMode> CREATOR = new Parcelable.Creator<FrontendTransmissionMode>() { // from class: android.hardware.tv.tuner.FrontendTransmissionMode.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendTransmissionMode createFromParcel(Parcel _aidl_source) {
            return new FrontendTransmissionMode(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendTransmissionMode[] newArray(int _aidl_size) {
            return new FrontendTransmissionMode[_aidl_size];
        }
    };
    public static final int dtmb = 2;
    public static final int dvbt = 0;
    public static final int isdbt = 1;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.FrontendTransmissionMode$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int dtmb = 2;
        public static final int dvbt = 0;
        public static final int isdbt = 1;
    }

    public FrontendTransmissionMode() {
        this._tag = 0;
        this._value = 0;
    }

    private FrontendTransmissionMode(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private FrontendTransmissionMode(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static FrontendTransmissionMode dvbt(int _value) {
        return new FrontendTransmissionMode(0, Integer.valueOf(_value));
    }

    public int getDvbt() {
        _assertTag(0);
        return ((Integer) this._value).intValue();
    }

    public void setDvbt(int _value) {
        _set(0, Integer.valueOf(_value));
    }

    public static FrontendTransmissionMode isdbt(int _value) {
        return new FrontendTransmissionMode(1, Integer.valueOf(_value));
    }

    public int getIsdbt() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setIsdbt(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static FrontendTransmissionMode dtmb(int _value) {
        return new FrontendTransmissionMode(2, Integer.valueOf(_value));
    }

    public int getDtmb() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setDtmb(int _value) {
        _set(2, Integer.valueOf(_value));
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
                _aidl_parcel.writeInt(getDvbt());
                return;
            case 1:
                _aidl_parcel.writeInt(getIsdbt());
                return;
            case 2:
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
                return "dvbt";
            case 1:
                return "isdbt";
            case 2:
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
