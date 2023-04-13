package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CellInfoRatSpecificInfo implements Parcelable {
    public static final Parcelable.Creator<CellInfoRatSpecificInfo> CREATOR = new Parcelable.Creator<CellInfoRatSpecificInfo>() { // from class: android.hardware.radio.network.CellInfoRatSpecificInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfoRatSpecificInfo createFromParcel(Parcel _aidl_source) {
            return new CellInfoRatSpecificInfo(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfoRatSpecificInfo[] newArray(int _aidl_size) {
            return new CellInfoRatSpecificInfo[_aidl_size];
        }
    };
    public static final int cdma = 5;
    public static final int gsm = 0;
    public static final int lte = 3;

    /* renamed from: nr */
    public static final int f198nr = 4;
    public static final int tdscdma = 2;
    public static final int wcdma = 1;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int cdma = 5;
        public static final int gsm = 0;
        public static final int lte = 3;

        /* renamed from: nr */
        public static final int f199nr = 4;
        public static final int tdscdma = 2;
        public static final int wcdma = 1;
    }

    public CellInfoRatSpecificInfo() {
        this._tag = 0;
        this._value = null;
    }

    private CellInfoRatSpecificInfo(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private CellInfoRatSpecificInfo(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static CellInfoRatSpecificInfo gsm(CellInfoGsm _value) {
        return new CellInfoRatSpecificInfo(0, _value);
    }

    public CellInfoGsm getGsm() {
        _assertTag(0);
        return (CellInfoGsm) this._value;
    }

    public void setGsm(CellInfoGsm _value) {
        _set(0, _value);
    }

    public static CellInfoRatSpecificInfo wcdma(CellInfoWcdma _value) {
        return new CellInfoRatSpecificInfo(1, _value);
    }

    public CellInfoWcdma getWcdma() {
        _assertTag(1);
        return (CellInfoWcdma) this._value;
    }

    public void setWcdma(CellInfoWcdma _value) {
        _set(1, _value);
    }

    public static CellInfoRatSpecificInfo tdscdma(CellInfoTdscdma _value) {
        return new CellInfoRatSpecificInfo(2, _value);
    }

    public CellInfoTdscdma getTdscdma() {
        _assertTag(2);
        return (CellInfoTdscdma) this._value;
    }

    public void setTdscdma(CellInfoTdscdma _value) {
        _set(2, _value);
    }

    public static CellInfoRatSpecificInfo lte(CellInfoLte _value) {
        return new CellInfoRatSpecificInfo(3, _value);
    }

    public CellInfoLte getLte() {
        _assertTag(3);
        return (CellInfoLte) this._value;
    }

    public void setLte(CellInfoLte _value) {
        _set(3, _value);
    }

    /* renamed from: nr */
    public static CellInfoRatSpecificInfo m162nr(CellInfoNr _value) {
        return new CellInfoRatSpecificInfo(4, _value);
    }

    public CellInfoNr getNr() {
        _assertTag(4);
        return (CellInfoNr) this._value;
    }

    public void setNr(CellInfoNr _value) {
        _set(4, _value);
    }

    public static CellInfoRatSpecificInfo cdma(CellInfoCdma _value) {
        return new CellInfoRatSpecificInfo(5, _value);
    }

    public CellInfoCdma getCdma() {
        _assertTag(5);
        return (CellInfoCdma) this._value;
    }

    public void setCdma(CellInfoCdma _value) {
        _set(5, _value);
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
                _aidl_parcel.writeTypedObject(getGsm(), _aidl_flag);
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getWcdma(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedObject(getTdscdma(), _aidl_flag);
                return;
            case 3:
                _aidl_parcel.writeTypedObject(getLte(), _aidl_flag);
                return;
            case 4:
                _aidl_parcel.writeTypedObject(getNr(), _aidl_flag);
                return;
            case 5:
                _aidl_parcel.writeTypedObject(getCdma(), _aidl_flag);
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                CellInfoGsm _aidl_value = (CellInfoGsm) _aidl_parcel.readTypedObject(CellInfoGsm.CREATOR);
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                CellInfoWcdma _aidl_value2 = (CellInfoWcdma) _aidl_parcel.readTypedObject(CellInfoWcdma.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                CellInfoTdscdma _aidl_value3 = (CellInfoTdscdma) _aidl_parcel.readTypedObject(CellInfoTdscdma.CREATOR);
                _set(_aidl_tag, _aidl_value3);
                return;
            case 3:
                CellInfoLte _aidl_value4 = (CellInfoLte) _aidl_parcel.readTypedObject(CellInfoLte.CREATOR);
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                CellInfoNr _aidl_value5 = (CellInfoNr) _aidl_parcel.readTypedObject(CellInfoNr.CREATOR);
                _set(_aidl_tag, _aidl_value5);
                return;
            case 5:
                CellInfoCdma _aidl_value6 = (CellInfoCdma) _aidl_parcel.readTypedObject(CellInfoCdma.CREATOR);
                _set(_aidl_tag, _aidl_value6);
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 0:
                int _mask = 0 | describeContents(getGsm());
                return _mask;
            case 1:
                int _mask2 = 0 | describeContents(getWcdma());
                return _mask2;
            case 2:
                int _mask3 = 0 | describeContents(getTdscdma());
                return _mask3;
            case 3:
                int _mask4 = 0 | describeContents(getLte());
                return _mask4;
            case 4:
                int _mask5 = 0 | describeContents(getNr());
                return _mask5;
            case 5:
                int _mask6 = 0 | describeContents(getCdma());
                return _mask6;
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
                return "android.hardware.radio.network.CellInfoRatSpecificInfo.gsm(" + Objects.toString(getGsm()) + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.hardware.radio.network.CellInfoRatSpecificInfo.wcdma(" + Objects.toString(getWcdma()) + NavigationBarInflaterView.KEY_CODE_END;
            case 2:
                return "android.hardware.radio.network.CellInfoRatSpecificInfo.tdscdma(" + Objects.toString(getTdscdma()) + NavigationBarInflaterView.KEY_CODE_END;
            case 3:
                return "android.hardware.radio.network.CellInfoRatSpecificInfo.lte(" + Objects.toString(getLte()) + NavigationBarInflaterView.KEY_CODE_END;
            case 4:
                return "android.hardware.radio.network.CellInfoRatSpecificInfo.nr(" + Objects.toString(getNr()) + NavigationBarInflaterView.KEY_CODE_END;
            case 5:
                return "android.hardware.radio.network.CellInfoRatSpecificInfo.cdma(" + Objects.toString(getCdma()) + NavigationBarInflaterView.KEY_CODE_END;
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
                return "gsm";
            case 1:
                return "wcdma";
            case 2:
                return "tdscdma";
            case 3:
                return "lte";
            case 4:
                return "nr";
            case 5:
                return "cdma";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
