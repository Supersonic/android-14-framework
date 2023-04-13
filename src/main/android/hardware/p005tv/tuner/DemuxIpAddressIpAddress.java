package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.DemuxIpAddressIpAddress */
/* loaded from: classes2.dex */
public final class DemuxIpAddressIpAddress implements Parcelable {
    public static final Parcelable.Creator<DemuxIpAddressIpAddress> CREATOR = new Parcelable.Creator<DemuxIpAddressIpAddress>() { // from class: android.hardware.tv.tuner.DemuxIpAddressIpAddress.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxIpAddressIpAddress createFromParcel(Parcel _aidl_source) {
            return new DemuxIpAddressIpAddress(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxIpAddressIpAddress[] newArray(int _aidl_size) {
            return new DemuxIpAddressIpAddress[_aidl_size];
        }
    };

    /* renamed from: v4 */
    public static final int f223v4 = 0;

    /* renamed from: v6 */
    public static final int f224v6 = 1;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.DemuxIpAddressIpAddress$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {

        /* renamed from: v4 */
        public static final int f225v4 = 0;

        /* renamed from: v6 */
        public static final int f226v6 = 1;
    }

    public DemuxIpAddressIpAddress() {
        byte[] _value = new byte[0];
        this._tag = 0;
        this._value = _value;
    }

    private DemuxIpAddressIpAddress(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DemuxIpAddressIpAddress(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    /* renamed from: v4 */
    public static DemuxIpAddressIpAddress m159v4(byte[] _value) {
        return new DemuxIpAddressIpAddress(0, _value);
    }

    public byte[] getV4() {
        _assertTag(0);
        return (byte[]) this._value;
    }

    public void setV4(byte[] _value) {
        _set(0, _value);
    }

    /* renamed from: v6 */
    public static DemuxIpAddressIpAddress m158v6(byte[] _value) {
        return new DemuxIpAddressIpAddress(1, _value);
    }

    public byte[] getV6() {
        _assertTag(1);
        return (byte[]) this._value;
    }

    public void setV6(byte[] _value) {
        _set(1, _value);
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
                _aidl_parcel.writeByteArray(getV4());
                return;
            case 1:
                _aidl_parcel.writeByteArray(getV6());
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                byte[] _aidl_value = _aidl_parcel.createByteArray();
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                byte[] _aidl_value2 = _aidl_parcel.createByteArray();
                _set(_aidl_tag, _aidl_value2);
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
                return "v4";
            case 1:
                return "v6";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
