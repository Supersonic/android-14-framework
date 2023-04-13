package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.DemuxFilterSettings */
/* loaded from: classes2.dex */
public final class DemuxFilterSettings implements Parcelable {
    public static final Parcelable.Creator<DemuxFilterSettings> CREATOR = new Parcelable.Creator<DemuxFilterSettings>() { // from class: android.hardware.tv.tuner.DemuxFilterSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterSettings createFromParcel(Parcel _aidl_source) {
            return new DemuxFilterSettings(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterSettings[] newArray(int _aidl_size) {
            return new DemuxFilterSettings[_aidl_size];
        }
    };
    public static final int alp = 4;

    /* renamed from: ip */
    public static final int f219ip = 2;
    public static final int mmtp = 1;
    public static final int tlv = 3;

    /* renamed from: ts */
    public static final int f220ts = 0;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.DemuxFilterSettings$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int alp = 4;

        /* renamed from: ip */
        public static final int f221ip = 2;
        public static final int mmtp = 1;
        public static final int tlv = 3;

        /* renamed from: ts */
        public static final int f222ts = 0;
    }

    public DemuxFilterSettings() {
        this._tag = 0;
        this._value = null;
    }

    private DemuxFilterSettings(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DemuxFilterSettings(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    /* renamed from: ts */
    public static DemuxFilterSettings m160ts(DemuxTsFilterSettings _value) {
        return new DemuxFilterSettings(0, _value);
    }

    public DemuxTsFilterSettings getTs() {
        _assertTag(0);
        return (DemuxTsFilterSettings) this._value;
    }

    public void setTs(DemuxTsFilterSettings _value) {
        _set(0, _value);
    }

    public static DemuxFilterSettings mmtp(DemuxMmtpFilterSettings _value) {
        return new DemuxFilterSettings(1, _value);
    }

    public DemuxMmtpFilterSettings getMmtp() {
        _assertTag(1);
        return (DemuxMmtpFilterSettings) this._value;
    }

    public void setMmtp(DemuxMmtpFilterSettings _value) {
        _set(1, _value);
    }

    /* renamed from: ip */
    public static DemuxFilterSettings m161ip(DemuxIpFilterSettings _value) {
        return new DemuxFilterSettings(2, _value);
    }

    public DemuxIpFilterSettings getIp() {
        _assertTag(2);
        return (DemuxIpFilterSettings) this._value;
    }

    public void setIp(DemuxIpFilterSettings _value) {
        _set(2, _value);
    }

    public static DemuxFilterSettings tlv(DemuxTlvFilterSettings _value) {
        return new DemuxFilterSettings(3, _value);
    }

    public DemuxTlvFilterSettings getTlv() {
        _assertTag(3);
        return (DemuxTlvFilterSettings) this._value;
    }

    public void setTlv(DemuxTlvFilterSettings _value) {
        _set(3, _value);
    }

    public static DemuxFilterSettings alp(DemuxAlpFilterSettings _value) {
        return new DemuxFilterSettings(4, _value);
    }

    public DemuxAlpFilterSettings getAlp() {
        _assertTag(4);
        return (DemuxAlpFilterSettings) this._value;
    }

    public void setAlp(DemuxAlpFilterSettings _value) {
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
                _aidl_parcel.writeTypedObject(getTs(), _aidl_flag);
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getMmtp(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedObject(getIp(), _aidl_flag);
                return;
            case 3:
                _aidl_parcel.writeTypedObject(getTlv(), _aidl_flag);
                return;
            case 4:
                _aidl_parcel.writeTypedObject(getAlp(), _aidl_flag);
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                DemuxTsFilterSettings _aidl_value = (DemuxTsFilterSettings) _aidl_parcel.readTypedObject(DemuxTsFilterSettings.CREATOR);
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                DemuxMmtpFilterSettings _aidl_value2 = (DemuxMmtpFilterSettings) _aidl_parcel.readTypedObject(DemuxMmtpFilterSettings.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                DemuxIpFilterSettings _aidl_value3 = (DemuxIpFilterSettings) _aidl_parcel.readTypedObject(DemuxIpFilterSettings.CREATOR);
                _set(_aidl_tag, _aidl_value3);
                return;
            case 3:
                DemuxTlvFilterSettings _aidl_value4 = (DemuxTlvFilterSettings) _aidl_parcel.readTypedObject(DemuxTlvFilterSettings.CREATOR);
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                DemuxAlpFilterSettings _aidl_value5 = (DemuxAlpFilterSettings) _aidl_parcel.readTypedObject(DemuxAlpFilterSettings.CREATOR);
                _set(_aidl_tag, _aidl_value5);
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 0:
                int _mask = 0 | describeContents(getTs());
                return _mask;
            case 1:
                int _mask2 = 0 | describeContents(getMmtp());
                return _mask2;
            case 2:
                int _mask3 = 0 | describeContents(getIp());
                return _mask3;
            case 3:
                int _mask4 = 0 | describeContents(getTlv());
                return _mask4;
            case 4:
                int _mask5 = 0 | describeContents(getAlp());
                return _mask5;
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
                return "ts";
            case 1:
                return "mmtp";
            case 2:
                return "ip";
            case 3:
                return "tlv";
            case 4:
                return "alp";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
