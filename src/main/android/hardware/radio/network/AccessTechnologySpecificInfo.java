package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class AccessTechnologySpecificInfo implements Parcelable {
    public static final Parcelable.Creator<AccessTechnologySpecificInfo> CREATOR = new Parcelable.Creator<AccessTechnologySpecificInfo>() { // from class: android.hardware.radio.network.AccessTechnologySpecificInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccessTechnologySpecificInfo createFromParcel(Parcel _aidl_source) {
            return new AccessTechnologySpecificInfo(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccessTechnologySpecificInfo[] newArray(int _aidl_size) {
            return new AccessTechnologySpecificInfo[_aidl_size];
        }
    };
    public static final int cdmaInfo = 1;
    public static final int eutranInfo = 2;
    public static final int geranDtmSupported = 4;
    public static final int ngranNrVopsInfo = 3;
    public static final int noinit = 0;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int cdmaInfo = 1;
        public static final int eutranInfo = 2;
        public static final int geranDtmSupported = 4;
        public static final int ngranNrVopsInfo = 3;
        public static final int noinit = 0;
    }

    public AccessTechnologySpecificInfo() {
        this._tag = 0;
        this._value = false;
    }

    private AccessTechnologySpecificInfo(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private AccessTechnologySpecificInfo(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static AccessTechnologySpecificInfo noinit(boolean _value) {
        return new AccessTechnologySpecificInfo(0, Boolean.valueOf(_value));
    }

    public boolean getNoinit() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setNoinit(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static AccessTechnologySpecificInfo cdmaInfo(Cdma2000RegistrationInfo _value) {
        return new AccessTechnologySpecificInfo(1, _value);
    }

    public Cdma2000RegistrationInfo getCdmaInfo() {
        _assertTag(1);
        return (Cdma2000RegistrationInfo) this._value;
    }

    public void setCdmaInfo(Cdma2000RegistrationInfo _value) {
        _set(1, _value);
    }

    public static AccessTechnologySpecificInfo eutranInfo(EutranRegistrationInfo _value) {
        return new AccessTechnologySpecificInfo(2, _value);
    }

    public EutranRegistrationInfo getEutranInfo() {
        _assertTag(2);
        return (EutranRegistrationInfo) this._value;
    }

    public void setEutranInfo(EutranRegistrationInfo _value) {
        _set(2, _value);
    }

    public static AccessTechnologySpecificInfo ngranNrVopsInfo(NrVopsInfo _value) {
        return new AccessTechnologySpecificInfo(3, _value);
    }

    public NrVopsInfo getNgranNrVopsInfo() {
        _assertTag(3);
        return (NrVopsInfo) this._value;
    }

    public void setNgranNrVopsInfo(NrVopsInfo _value) {
        _set(3, _value);
    }

    public static AccessTechnologySpecificInfo geranDtmSupported(boolean _value) {
        return new AccessTechnologySpecificInfo(4, Boolean.valueOf(_value));
    }

    public boolean getGeranDtmSupported() {
        _assertTag(4);
        return ((Boolean) this._value).booleanValue();
    }

    public void setGeranDtmSupported(boolean _value) {
        _set(4, Boolean.valueOf(_value));
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
                _aidl_parcel.writeTypedObject(getCdmaInfo(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedObject(getEutranInfo(), _aidl_flag);
                return;
            case 3:
                _aidl_parcel.writeTypedObject(getNgranNrVopsInfo(), _aidl_flag);
                return;
            case 4:
                _aidl_parcel.writeBoolean(getGeranDtmSupported());
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
                Cdma2000RegistrationInfo _aidl_value2 = (Cdma2000RegistrationInfo) _aidl_parcel.readTypedObject(Cdma2000RegistrationInfo.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                EutranRegistrationInfo _aidl_value3 = (EutranRegistrationInfo) _aidl_parcel.readTypedObject(EutranRegistrationInfo.CREATOR);
                _set(_aidl_tag, _aidl_value3);
                return;
            case 3:
                NrVopsInfo _aidl_value4 = (NrVopsInfo) _aidl_parcel.readTypedObject(NrVopsInfo.CREATOR);
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                boolean _aidl_value5 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value5));
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 1:
                int _mask = 0 | describeContents(getCdmaInfo());
                return _mask;
            case 2:
                int _mask2 = 0 | describeContents(getEutranInfo());
                return _mask2;
            case 3:
                int _mask3 = 0 | describeContents(getNgranNrVopsInfo());
                return _mask3;
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
                return "android.hardware.radio.network.AccessTechnologySpecificInfo.noinit(" + getNoinit() + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.hardware.radio.network.AccessTechnologySpecificInfo.cdmaInfo(" + Objects.toString(getCdmaInfo()) + NavigationBarInflaterView.KEY_CODE_END;
            case 2:
                return "android.hardware.radio.network.AccessTechnologySpecificInfo.eutranInfo(" + Objects.toString(getEutranInfo()) + NavigationBarInflaterView.KEY_CODE_END;
            case 3:
                return "android.hardware.radio.network.AccessTechnologySpecificInfo.ngranNrVopsInfo(" + Objects.toString(getNgranNrVopsInfo()) + NavigationBarInflaterView.KEY_CODE_END;
            case 4:
                return "android.hardware.radio.network.AccessTechnologySpecificInfo.geranDtmSupported(" + getGeranDtmSupported() + NavigationBarInflaterView.KEY_CODE_END;
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
                return "cdmaInfo";
            case 2:
                return "eutranInfo";
            case 3:
                return "ngranNrVopsInfo";
            case 4:
                return "geranDtmSupported";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
