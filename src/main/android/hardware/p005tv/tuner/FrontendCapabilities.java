package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.FrontendCapabilities */
/* loaded from: classes2.dex */
public final class FrontendCapabilities implements Parcelable {
    public static final Parcelable.Creator<FrontendCapabilities> CREATOR = new Parcelable.Creator<FrontendCapabilities>() { // from class: android.hardware.tv.tuner.FrontendCapabilities.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendCapabilities createFromParcel(Parcel _aidl_source) {
            return new FrontendCapabilities(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendCapabilities[] newArray(int _aidl_size) {
            return new FrontendCapabilities[_aidl_size];
        }
    };
    public static final int analogCaps = 0;
    public static final int atsc3Caps = 2;
    public static final int atscCaps = 1;
    public static final int dtmbCaps = 3;
    public static final int dvbcCaps = 5;
    public static final int dvbsCaps = 4;
    public static final int dvbtCaps = 6;
    public static final int iptvCaps = 10;
    public static final int isdbs3Caps = 8;
    public static final int isdbsCaps = 7;
    public static final int isdbtCaps = 9;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.FrontendCapabilities$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int analogCaps = 0;
        public static final int atsc3Caps = 2;
        public static final int atscCaps = 1;
        public static final int dtmbCaps = 3;
        public static final int dvbcCaps = 5;
        public static final int dvbsCaps = 4;
        public static final int dvbtCaps = 6;
        public static final int iptvCaps = 10;
        public static final int isdbs3Caps = 8;
        public static final int isdbsCaps = 7;
        public static final int isdbtCaps = 9;
    }

    public FrontendCapabilities() {
        this._tag = 0;
        this._value = null;
    }

    private FrontendCapabilities(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private FrontendCapabilities(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static FrontendCapabilities analogCaps(FrontendAnalogCapabilities _value) {
        return new FrontendCapabilities(0, _value);
    }

    public FrontendAnalogCapabilities getAnalogCaps() {
        _assertTag(0);
        return (FrontendAnalogCapabilities) this._value;
    }

    public void setAnalogCaps(FrontendAnalogCapabilities _value) {
        _set(0, _value);
    }

    public static FrontendCapabilities atscCaps(FrontendAtscCapabilities _value) {
        return new FrontendCapabilities(1, _value);
    }

    public FrontendAtscCapabilities getAtscCaps() {
        _assertTag(1);
        return (FrontendAtscCapabilities) this._value;
    }

    public void setAtscCaps(FrontendAtscCapabilities _value) {
        _set(1, _value);
    }

    public static FrontendCapabilities atsc3Caps(FrontendAtsc3Capabilities _value) {
        return new FrontendCapabilities(2, _value);
    }

    public FrontendAtsc3Capabilities getAtsc3Caps() {
        _assertTag(2);
        return (FrontendAtsc3Capabilities) this._value;
    }

    public void setAtsc3Caps(FrontendAtsc3Capabilities _value) {
        _set(2, _value);
    }

    public static FrontendCapabilities dtmbCaps(FrontendDtmbCapabilities _value) {
        return new FrontendCapabilities(3, _value);
    }

    public FrontendDtmbCapabilities getDtmbCaps() {
        _assertTag(3);
        return (FrontendDtmbCapabilities) this._value;
    }

    public void setDtmbCaps(FrontendDtmbCapabilities _value) {
        _set(3, _value);
    }

    public static FrontendCapabilities dvbsCaps(FrontendDvbsCapabilities _value) {
        return new FrontendCapabilities(4, _value);
    }

    public FrontendDvbsCapabilities getDvbsCaps() {
        _assertTag(4);
        return (FrontendDvbsCapabilities) this._value;
    }

    public void setDvbsCaps(FrontendDvbsCapabilities _value) {
        _set(4, _value);
    }

    public static FrontendCapabilities dvbcCaps(FrontendDvbcCapabilities _value) {
        return new FrontendCapabilities(5, _value);
    }

    public FrontendDvbcCapabilities getDvbcCaps() {
        _assertTag(5);
        return (FrontendDvbcCapabilities) this._value;
    }

    public void setDvbcCaps(FrontendDvbcCapabilities _value) {
        _set(5, _value);
    }

    public static FrontendCapabilities dvbtCaps(FrontendDvbtCapabilities _value) {
        return new FrontendCapabilities(6, _value);
    }

    public FrontendDvbtCapabilities getDvbtCaps() {
        _assertTag(6);
        return (FrontendDvbtCapabilities) this._value;
    }

    public void setDvbtCaps(FrontendDvbtCapabilities _value) {
        _set(6, _value);
    }

    public static FrontendCapabilities isdbsCaps(FrontendIsdbsCapabilities _value) {
        return new FrontendCapabilities(7, _value);
    }

    public FrontendIsdbsCapabilities getIsdbsCaps() {
        _assertTag(7);
        return (FrontendIsdbsCapabilities) this._value;
    }

    public void setIsdbsCaps(FrontendIsdbsCapabilities _value) {
        _set(7, _value);
    }

    public static FrontendCapabilities isdbs3Caps(FrontendIsdbs3Capabilities _value) {
        return new FrontendCapabilities(8, _value);
    }

    public FrontendIsdbs3Capabilities getIsdbs3Caps() {
        _assertTag(8);
        return (FrontendIsdbs3Capabilities) this._value;
    }

    public void setIsdbs3Caps(FrontendIsdbs3Capabilities _value) {
        _set(8, _value);
    }

    public static FrontendCapabilities isdbtCaps(FrontendIsdbtCapabilities _value) {
        return new FrontendCapabilities(9, _value);
    }

    public FrontendIsdbtCapabilities getIsdbtCaps() {
        _assertTag(9);
        return (FrontendIsdbtCapabilities) this._value;
    }

    public void setIsdbtCaps(FrontendIsdbtCapabilities _value) {
        _set(9, _value);
    }

    public static FrontendCapabilities iptvCaps(FrontendIptvCapabilities _value) {
        return new FrontendCapabilities(10, _value);
    }

    public FrontendIptvCapabilities getIptvCaps() {
        _assertTag(10);
        return (FrontendIptvCapabilities) this._value;
    }

    public void setIptvCaps(FrontendIptvCapabilities _value) {
        _set(10, _value);
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
                _aidl_parcel.writeTypedObject(getAnalogCaps(), _aidl_flag);
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getAtscCaps(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedObject(getAtsc3Caps(), _aidl_flag);
                return;
            case 3:
                _aidl_parcel.writeTypedObject(getDtmbCaps(), _aidl_flag);
                return;
            case 4:
                _aidl_parcel.writeTypedObject(getDvbsCaps(), _aidl_flag);
                return;
            case 5:
                _aidl_parcel.writeTypedObject(getDvbcCaps(), _aidl_flag);
                return;
            case 6:
                _aidl_parcel.writeTypedObject(getDvbtCaps(), _aidl_flag);
                return;
            case 7:
                _aidl_parcel.writeTypedObject(getIsdbsCaps(), _aidl_flag);
                return;
            case 8:
                _aidl_parcel.writeTypedObject(getIsdbs3Caps(), _aidl_flag);
                return;
            case 9:
                _aidl_parcel.writeTypedObject(getIsdbtCaps(), _aidl_flag);
                return;
            case 10:
                _aidl_parcel.writeTypedObject(getIptvCaps(), _aidl_flag);
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                FrontendAnalogCapabilities _aidl_value = (FrontendAnalogCapabilities) _aidl_parcel.readTypedObject(FrontendAnalogCapabilities.CREATOR);
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                FrontendAtscCapabilities _aidl_value2 = (FrontendAtscCapabilities) _aidl_parcel.readTypedObject(FrontendAtscCapabilities.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                FrontendAtsc3Capabilities _aidl_value3 = (FrontendAtsc3Capabilities) _aidl_parcel.readTypedObject(FrontendAtsc3Capabilities.CREATOR);
                _set(_aidl_tag, _aidl_value3);
                return;
            case 3:
                FrontendDtmbCapabilities _aidl_value4 = (FrontendDtmbCapabilities) _aidl_parcel.readTypedObject(FrontendDtmbCapabilities.CREATOR);
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                FrontendDvbsCapabilities _aidl_value5 = (FrontendDvbsCapabilities) _aidl_parcel.readTypedObject(FrontendDvbsCapabilities.CREATOR);
                _set(_aidl_tag, _aidl_value5);
                return;
            case 5:
                FrontendDvbcCapabilities _aidl_value6 = (FrontendDvbcCapabilities) _aidl_parcel.readTypedObject(FrontendDvbcCapabilities.CREATOR);
                _set(_aidl_tag, _aidl_value6);
                return;
            case 6:
                FrontendDvbtCapabilities _aidl_value7 = (FrontendDvbtCapabilities) _aidl_parcel.readTypedObject(FrontendDvbtCapabilities.CREATOR);
                _set(_aidl_tag, _aidl_value7);
                return;
            case 7:
                FrontendIsdbsCapabilities _aidl_value8 = (FrontendIsdbsCapabilities) _aidl_parcel.readTypedObject(FrontendIsdbsCapabilities.CREATOR);
                _set(_aidl_tag, _aidl_value8);
                return;
            case 8:
                FrontendIsdbs3Capabilities _aidl_value9 = (FrontendIsdbs3Capabilities) _aidl_parcel.readTypedObject(FrontendIsdbs3Capabilities.CREATOR);
                _set(_aidl_tag, _aidl_value9);
                return;
            case 9:
                FrontendIsdbtCapabilities _aidl_value10 = (FrontendIsdbtCapabilities) _aidl_parcel.readTypedObject(FrontendIsdbtCapabilities.CREATOR);
                _set(_aidl_tag, _aidl_value10);
                return;
            case 10:
                FrontendIptvCapabilities _aidl_value11 = (FrontendIptvCapabilities) _aidl_parcel.readTypedObject(FrontendIptvCapabilities.CREATOR);
                _set(_aidl_tag, _aidl_value11);
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 0:
                int _mask = 0 | describeContents(getAnalogCaps());
                return _mask;
            case 1:
                int _mask2 = 0 | describeContents(getAtscCaps());
                return _mask2;
            case 2:
                int _mask3 = 0 | describeContents(getAtsc3Caps());
                return _mask3;
            case 3:
                int _mask4 = 0 | describeContents(getDtmbCaps());
                return _mask4;
            case 4:
                int _mask5 = 0 | describeContents(getDvbsCaps());
                return _mask5;
            case 5:
                int _mask6 = 0 | describeContents(getDvbcCaps());
                return _mask6;
            case 6:
                int _mask7 = 0 | describeContents(getDvbtCaps());
                return _mask7;
            case 7:
                int _mask8 = 0 | describeContents(getIsdbsCaps());
                return _mask8;
            case 8:
                int _mask9 = 0 | describeContents(getIsdbs3Caps());
                return _mask9;
            case 9:
                int _mask10 = 0 | describeContents(getIsdbtCaps());
                return _mask10;
            case 10:
                int _mask11 = 0 | describeContents(getIptvCaps());
                return _mask11;
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

    private void _assertTag(int tag) {
        if (getTag() != tag) {
            throw new IllegalStateException("bad access: " + _tagString(tag) + ", " + _tagString(getTag()) + " is available.");
        }
    }

    private String _tagString(int _tag) {
        switch (_tag) {
            case 0:
                return "analogCaps";
            case 1:
                return "atscCaps";
            case 2:
                return "atsc3Caps";
            case 3:
                return "dtmbCaps";
            case 4:
                return "dvbsCaps";
            case 5:
                return "dvbcCaps";
            case 6:
                return "dvbtCaps";
            case 7:
                return "isdbsCaps";
            case 8:
                return "isdbs3Caps";
            case 9:
                return "isdbtCaps";
            case 10:
                return "iptvCaps";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
