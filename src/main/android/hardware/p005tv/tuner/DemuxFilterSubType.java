package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.DemuxFilterSubType */
/* loaded from: classes2.dex */
public final class DemuxFilterSubType implements Parcelable {
    public static final Parcelable.Creator<DemuxFilterSubType> CREATOR = new Parcelable.Creator<DemuxFilterSubType>() { // from class: android.hardware.tv.tuner.DemuxFilterSubType.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterSubType createFromParcel(Parcel _aidl_source) {
            return new DemuxFilterSubType(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterSubType[] newArray(int _aidl_size) {
            return new DemuxFilterSubType[_aidl_size];
        }
    };
    public static final int alpFilterType = 4;
    public static final int ipFilterType = 2;
    public static final int mmtpFilterType = 1;
    public static final int tlvFilterType = 3;
    public static final int tsFilterType = 0;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.DemuxFilterSubType$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int alpFilterType = 4;
        public static final int ipFilterType = 2;
        public static final int mmtpFilterType = 1;
        public static final int tlvFilterType = 3;
        public static final int tsFilterType = 0;
    }

    public DemuxFilterSubType() {
        this._tag = 0;
        this._value = 0;
    }

    private DemuxFilterSubType(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DemuxFilterSubType(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static DemuxFilterSubType tsFilterType(int _value) {
        return new DemuxFilterSubType(0, Integer.valueOf(_value));
    }

    public int getTsFilterType() {
        _assertTag(0);
        return ((Integer) this._value).intValue();
    }

    public void setTsFilterType(int _value) {
        _set(0, Integer.valueOf(_value));
    }

    public static DemuxFilterSubType mmtpFilterType(int _value) {
        return new DemuxFilterSubType(1, Integer.valueOf(_value));
    }

    public int getMmtpFilterType() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setMmtpFilterType(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static DemuxFilterSubType ipFilterType(int _value) {
        return new DemuxFilterSubType(2, Integer.valueOf(_value));
    }

    public int getIpFilterType() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setIpFilterType(int _value) {
        _set(2, Integer.valueOf(_value));
    }

    public static DemuxFilterSubType tlvFilterType(int _value) {
        return new DemuxFilterSubType(3, Integer.valueOf(_value));
    }

    public int getTlvFilterType() {
        _assertTag(3);
        return ((Integer) this._value).intValue();
    }

    public void setTlvFilterType(int _value) {
        _set(3, Integer.valueOf(_value));
    }

    public static DemuxFilterSubType alpFilterType(int _value) {
        return new DemuxFilterSubType(4, Integer.valueOf(_value));
    }

    public int getAlpFilterType() {
        _assertTag(4);
        return ((Integer) this._value).intValue();
    }

    public void setAlpFilterType(int _value) {
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
                _aidl_parcel.writeInt(getTsFilterType());
                return;
            case 1:
                _aidl_parcel.writeInt(getMmtpFilterType());
                return;
            case 2:
                _aidl_parcel.writeInt(getIpFilterType());
                return;
            case 3:
                _aidl_parcel.writeInt(getTlvFilterType());
                return;
            case 4:
                _aidl_parcel.writeInt(getAlpFilterType());
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
                return "tsFilterType";
            case 1:
                return "mmtpFilterType";
            case 2:
                return "ipFilterType";
            case 3:
                return "tlvFilterType";
            case 4:
                return "alpFilterType";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
