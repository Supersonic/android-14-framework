package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CellIdentity implements Parcelable {
    public static final Parcelable.Creator<CellIdentity> CREATOR = new Parcelable.Creator<CellIdentity>() { // from class: android.hardware.radio.network.CellIdentity.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellIdentity createFromParcel(Parcel _aidl_source) {
            return new CellIdentity(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellIdentity[] newArray(int _aidl_size) {
            return new CellIdentity[_aidl_size];
        }
    };
    public static final int cdma = 4;
    public static final int gsm = 1;
    public static final int lte = 5;
    public static final int noinit = 0;

    /* renamed from: nr */
    public static final int f195nr = 6;
    public static final int tdscdma = 3;
    public static final int wcdma = 2;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int cdma = 4;
        public static final int gsm = 1;
        public static final int lte = 5;
        public static final int noinit = 0;

        /* renamed from: nr */
        public static final int f196nr = 6;
        public static final int tdscdma = 3;
        public static final int wcdma = 2;
    }

    public CellIdentity() {
        this._tag = 0;
        this._value = false;
    }

    private CellIdentity(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private CellIdentity(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static CellIdentity noinit(boolean _value) {
        return new CellIdentity(0, Boolean.valueOf(_value));
    }

    public boolean getNoinit() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setNoinit(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static CellIdentity gsm(CellIdentityGsm _value) {
        return new CellIdentity(1, _value);
    }

    public CellIdentityGsm getGsm() {
        _assertTag(1);
        return (CellIdentityGsm) this._value;
    }

    public void setGsm(CellIdentityGsm _value) {
        _set(1, _value);
    }

    public static CellIdentity wcdma(CellIdentityWcdma _value) {
        return new CellIdentity(2, _value);
    }

    public CellIdentityWcdma getWcdma() {
        _assertTag(2);
        return (CellIdentityWcdma) this._value;
    }

    public void setWcdma(CellIdentityWcdma _value) {
        _set(2, _value);
    }

    public static CellIdentity tdscdma(CellIdentityTdscdma _value) {
        return new CellIdentity(3, _value);
    }

    public CellIdentityTdscdma getTdscdma() {
        _assertTag(3);
        return (CellIdentityTdscdma) this._value;
    }

    public void setTdscdma(CellIdentityTdscdma _value) {
        _set(3, _value);
    }

    public static CellIdentity cdma(CellIdentityCdma _value) {
        return new CellIdentity(4, _value);
    }

    public CellIdentityCdma getCdma() {
        _assertTag(4);
        return (CellIdentityCdma) this._value;
    }

    public void setCdma(CellIdentityCdma _value) {
        _set(4, _value);
    }

    public static CellIdentity lte(CellIdentityLte _value) {
        return new CellIdentity(5, _value);
    }

    public CellIdentityLte getLte() {
        _assertTag(5);
        return (CellIdentityLte) this._value;
    }

    public void setLte(CellIdentityLte _value) {
        _set(5, _value);
    }

    /* renamed from: nr */
    public static CellIdentity m163nr(CellIdentityNr _value) {
        return new CellIdentity(6, _value);
    }

    public CellIdentityNr getNr() {
        _assertTag(6);
        return (CellIdentityNr) this._value;
    }

    public void setNr(CellIdentityNr _value) {
        _set(6, _value);
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
                _aidl_parcel.writeTypedObject(getGsm(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedObject(getWcdma(), _aidl_flag);
                return;
            case 3:
                _aidl_parcel.writeTypedObject(getTdscdma(), _aidl_flag);
                return;
            case 4:
                _aidl_parcel.writeTypedObject(getCdma(), _aidl_flag);
                return;
            case 5:
                _aidl_parcel.writeTypedObject(getLte(), _aidl_flag);
                return;
            case 6:
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
                CellIdentityGsm _aidl_value2 = (CellIdentityGsm) _aidl_parcel.readTypedObject(CellIdentityGsm.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                CellIdentityWcdma _aidl_value3 = (CellIdentityWcdma) _aidl_parcel.readTypedObject(CellIdentityWcdma.CREATOR);
                _set(_aidl_tag, _aidl_value3);
                return;
            case 3:
                CellIdentityTdscdma _aidl_value4 = (CellIdentityTdscdma) _aidl_parcel.readTypedObject(CellIdentityTdscdma.CREATOR);
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                CellIdentityCdma _aidl_value5 = (CellIdentityCdma) _aidl_parcel.readTypedObject(CellIdentityCdma.CREATOR);
                _set(_aidl_tag, _aidl_value5);
                return;
            case 5:
                CellIdentityLte _aidl_value6 = (CellIdentityLte) _aidl_parcel.readTypedObject(CellIdentityLte.CREATOR);
                _set(_aidl_tag, _aidl_value6);
                return;
            case 6:
                CellIdentityNr _aidl_value7 = (CellIdentityNr) _aidl_parcel.readTypedObject(CellIdentityNr.CREATOR);
                _set(_aidl_tag, _aidl_value7);
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 1:
                int _mask = 0 | describeContents(getGsm());
                return _mask;
            case 2:
                int _mask2 = 0 | describeContents(getWcdma());
                return _mask2;
            case 3:
                int _mask3 = 0 | describeContents(getTdscdma());
                return _mask3;
            case 4:
                int _mask4 = 0 | describeContents(getCdma());
                return _mask4;
            case 5:
                int _mask5 = 0 | describeContents(getLte());
                return _mask5;
            case 6:
                int _mask6 = 0 | describeContents(getNr());
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
                return "android.hardware.radio.network.CellIdentity.noinit(" + getNoinit() + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.hardware.radio.network.CellIdentity.gsm(" + Objects.toString(getGsm()) + NavigationBarInflaterView.KEY_CODE_END;
            case 2:
                return "android.hardware.radio.network.CellIdentity.wcdma(" + Objects.toString(getWcdma()) + NavigationBarInflaterView.KEY_CODE_END;
            case 3:
                return "android.hardware.radio.network.CellIdentity.tdscdma(" + Objects.toString(getTdscdma()) + NavigationBarInflaterView.KEY_CODE_END;
            case 4:
                return "android.hardware.radio.network.CellIdentity.cdma(" + Objects.toString(getCdma()) + NavigationBarInflaterView.KEY_CODE_END;
            case 5:
                return "android.hardware.radio.network.CellIdentity.lte(" + Objects.toString(getLte()) + NavigationBarInflaterView.KEY_CODE_END;
            case 6:
                return "android.hardware.radio.network.CellIdentity.nr(" + Objects.toString(getNr()) + NavigationBarInflaterView.KEY_CODE_END;
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
                return "gsm";
            case 2:
                return "wcdma";
            case 3:
                return "tdscdma";
            case 4:
                return "cdma";
            case 5:
                return "lte";
            case 6:
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
