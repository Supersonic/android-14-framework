package android.hardware.p005tv.tuner;

import android.media.audio.common.AudioDeviceDescription;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.FrontendSettings */
/* loaded from: classes2.dex */
public final class FrontendSettings implements Parcelable {
    public static final Parcelable.Creator<FrontendSettings> CREATOR = new Parcelable.Creator<FrontendSettings>() { // from class: android.hardware.tv.tuner.FrontendSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendSettings createFromParcel(Parcel _aidl_source) {
            return new FrontendSettings(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendSettings[] newArray(int _aidl_size) {
            return new FrontendSettings[_aidl_size];
        }
    };
    public static final int analog = 0;
    public static final int atsc = 1;
    public static final int atsc3 = 2;
    public static final int dtmb = 9;
    public static final int dvbc = 4;
    public static final int dvbs = 3;
    public static final int dvbt = 5;
    public static final int iptv = 10;
    public static final int isdbs = 6;
    public static final int isdbs3 = 7;
    public static final int isdbt = 8;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.FrontendSettings$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int analog = 0;
        public static final int atsc = 1;
        public static final int atsc3 = 2;
        public static final int dtmb = 9;
        public static final int dvbc = 4;
        public static final int dvbs = 3;
        public static final int dvbt = 5;
        public static final int iptv = 10;
        public static final int isdbs = 6;
        public static final int isdbs3 = 7;
        public static final int isdbt = 8;
    }

    public FrontendSettings() {
        this._tag = 0;
        this._value = null;
    }

    private FrontendSettings(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private FrontendSettings(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static FrontendSettings analog(FrontendAnalogSettings _value) {
        return new FrontendSettings(0, _value);
    }

    public FrontendAnalogSettings getAnalog() {
        _assertTag(0);
        return (FrontendAnalogSettings) this._value;
    }

    public void setAnalog(FrontendAnalogSettings _value) {
        _set(0, _value);
    }

    public static FrontendSettings atsc(FrontendAtscSettings _value) {
        return new FrontendSettings(1, _value);
    }

    public FrontendAtscSettings getAtsc() {
        _assertTag(1);
        return (FrontendAtscSettings) this._value;
    }

    public void setAtsc(FrontendAtscSettings _value) {
        _set(1, _value);
    }

    public static FrontendSettings atsc3(FrontendAtsc3Settings _value) {
        return new FrontendSettings(2, _value);
    }

    public FrontendAtsc3Settings getAtsc3() {
        _assertTag(2);
        return (FrontendAtsc3Settings) this._value;
    }

    public void setAtsc3(FrontendAtsc3Settings _value) {
        _set(2, _value);
    }

    public static FrontendSettings dvbs(FrontendDvbsSettings _value) {
        return new FrontendSettings(3, _value);
    }

    public FrontendDvbsSettings getDvbs() {
        _assertTag(3);
        return (FrontendDvbsSettings) this._value;
    }

    public void setDvbs(FrontendDvbsSettings _value) {
        _set(3, _value);
    }

    public static FrontendSettings dvbc(FrontendDvbcSettings _value) {
        return new FrontendSettings(4, _value);
    }

    public FrontendDvbcSettings getDvbc() {
        _assertTag(4);
        return (FrontendDvbcSettings) this._value;
    }

    public void setDvbc(FrontendDvbcSettings _value) {
        _set(4, _value);
    }

    public static FrontendSettings dvbt(FrontendDvbtSettings _value) {
        return new FrontendSettings(5, _value);
    }

    public FrontendDvbtSettings getDvbt() {
        _assertTag(5);
        return (FrontendDvbtSettings) this._value;
    }

    public void setDvbt(FrontendDvbtSettings _value) {
        _set(5, _value);
    }

    public static FrontendSettings isdbs(FrontendIsdbsSettings _value) {
        return new FrontendSettings(6, _value);
    }

    public FrontendIsdbsSettings getIsdbs() {
        _assertTag(6);
        return (FrontendIsdbsSettings) this._value;
    }

    public void setIsdbs(FrontendIsdbsSettings _value) {
        _set(6, _value);
    }

    public static FrontendSettings isdbs3(FrontendIsdbs3Settings _value) {
        return new FrontendSettings(7, _value);
    }

    public FrontendIsdbs3Settings getIsdbs3() {
        _assertTag(7);
        return (FrontendIsdbs3Settings) this._value;
    }

    public void setIsdbs3(FrontendIsdbs3Settings _value) {
        _set(7, _value);
    }

    public static FrontendSettings isdbt(FrontendIsdbtSettings _value) {
        return new FrontendSettings(8, _value);
    }

    public FrontendIsdbtSettings getIsdbt() {
        _assertTag(8);
        return (FrontendIsdbtSettings) this._value;
    }

    public void setIsdbt(FrontendIsdbtSettings _value) {
        _set(8, _value);
    }

    public static FrontendSettings dtmb(FrontendDtmbSettings _value) {
        return new FrontendSettings(9, _value);
    }

    public FrontendDtmbSettings getDtmb() {
        _assertTag(9);
        return (FrontendDtmbSettings) this._value;
    }

    public void setDtmb(FrontendDtmbSettings _value) {
        _set(9, _value);
    }

    public static FrontendSettings iptv(FrontendIptvSettings _value) {
        return new FrontendSettings(10, _value);
    }

    public FrontendIptvSettings getIptv() {
        _assertTag(10);
        return (FrontendIptvSettings) this._value;
    }

    public void setIptv(FrontendIptvSettings _value) {
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
                _aidl_parcel.writeTypedObject(getAnalog(), _aidl_flag);
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getAtsc(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedObject(getAtsc3(), _aidl_flag);
                return;
            case 3:
                _aidl_parcel.writeTypedObject(getDvbs(), _aidl_flag);
                return;
            case 4:
                _aidl_parcel.writeTypedObject(getDvbc(), _aidl_flag);
                return;
            case 5:
                _aidl_parcel.writeTypedObject(getDvbt(), _aidl_flag);
                return;
            case 6:
                _aidl_parcel.writeTypedObject(getIsdbs(), _aidl_flag);
                return;
            case 7:
                _aidl_parcel.writeTypedObject(getIsdbs3(), _aidl_flag);
                return;
            case 8:
                _aidl_parcel.writeTypedObject(getIsdbt(), _aidl_flag);
                return;
            case 9:
                _aidl_parcel.writeTypedObject(getDtmb(), _aidl_flag);
                return;
            case 10:
                _aidl_parcel.writeTypedObject(getIptv(), _aidl_flag);
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                FrontendAnalogSettings _aidl_value = (FrontendAnalogSettings) _aidl_parcel.readTypedObject(FrontendAnalogSettings.CREATOR);
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                FrontendAtscSettings _aidl_value2 = (FrontendAtscSettings) _aidl_parcel.readTypedObject(FrontendAtscSettings.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                FrontendAtsc3Settings _aidl_value3 = (FrontendAtsc3Settings) _aidl_parcel.readTypedObject(FrontendAtsc3Settings.CREATOR);
                _set(_aidl_tag, _aidl_value3);
                return;
            case 3:
                FrontendDvbsSettings _aidl_value4 = (FrontendDvbsSettings) _aidl_parcel.readTypedObject(FrontendDvbsSettings.CREATOR);
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                FrontendDvbcSettings _aidl_value5 = (FrontendDvbcSettings) _aidl_parcel.readTypedObject(FrontendDvbcSettings.CREATOR);
                _set(_aidl_tag, _aidl_value5);
                return;
            case 5:
                FrontendDvbtSettings _aidl_value6 = (FrontendDvbtSettings) _aidl_parcel.readTypedObject(FrontendDvbtSettings.CREATOR);
                _set(_aidl_tag, _aidl_value6);
                return;
            case 6:
                FrontendIsdbsSettings _aidl_value7 = (FrontendIsdbsSettings) _aidl_parcel.readTypedObject(FrontendIsdbsSettings.CREATOR);
                _set(_aidl_tag, _aidl_value7);
                return;
            case 7:
                FrontendIsdbs3Settings _aidl_value8 = (FrontendIsdbs3Settings) _aidl_parcel.readTypedObject(FrontendIsdbs3Settings.CREATOR);
                _set(_aidl_tag, _aidl_value8);
                return;
            case 8:
                FrontendIsdbtSettings _aidl_value9 = (FrontendIsdbtSettings) _aidl_parcel.readTypedObject(FrontendIsdbtSettings.CREATOR);
                _set(_aidl_tag, _aidl_value9);
                return;
            case 9:
                FrontendDtmbSettings _aidl_value10 = (FrontendDtmbSettings) _aidl_parcel.readTypedObject(FrontendDtmbSettings.CREATOR);
                _set(_aidl_tag, _aidl_value10);
                return;
            case 10:
                FrontendIptvSettings _aidl_value11 = (FrontendIptvSettings) _aidl_parcel.readTypedObject(FrontendIptvSettings.CREATOR);
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
                int _mask = 0 | describeContents(getAnalog());
                return _mask;
            case 1:
                int _mask2 = 0 | describeContents(getAtsc());
                return _mask2;
            case 2:
                int _mask3 = 0 | describeContents(getAtsc3());
                return _mask3;
            case 3:
                int _mask4 = 0 | describeContents(getDvbs());
                return _mask4;
            case 4:
                int _mask5 = 0 | describeContents(getDvbc());
                return _mask5;
            case 5:
                int _mask6 = 0 | describeContents(getDvbt());
                return _mask6;
            case 6:
                int _mask7 = 0 | describeContents(getIsdbs());
                return _mask7;
            case 7:
                int _mask8 = 0 | describeContents(getIsdbs3());
                return _mask8;
            case 8:
                int _mask9 = 0 | describeContents(getIsdbt());
                return _mask9;
            case 9:
                int _mask10 = 0 | describeContents(getDtmb());
                return _mask10;
            case 10:
                int _mask11 = 0 | describeContents(getIptv());
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
                return AudioDeviceDescription.CONNECTION_ANALOG;
            case 1:
                return "atsc";
            case 2:
                return "atsc3";
            case 3:
                return "dvbs";
            case 4:
                return "dvbc";
            case 5:
                return "dvbt";
            case 6:
                return "isdbs";
            case 7:
                return "isdbs3";
            case 8:
                return "isdbt";
            case 9:
                return "dtmb";
            case 10:
                return "iptv";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
