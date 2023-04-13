package android.p008os;

import android.p008os.Parcelable;
/* renamed from: android.os.StatsBootstrapAtomValue */
/* loaded from: classes3.dex */
public final class StatsBootstrapAtomValue implements Parcelable {
    public static final Parcelable.Creator<StatsBootstrapAtomValue> CREATOR = new Parcelable.Creator<StatsBootstrapAtomValue>() { // from class: android.os.StatsBootstrapAtomValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StatsBootstrapAtomValue createFromParcel(Parcel _aidl_source) {
            return new StatsBootstrapAtomValue(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StatsBootstrapAtomValue[] newArray(int _aidl_size) {
            return new StatsBootstrapAtomValue[_aidl_size];
        }
    };
    public static final int boolValue = 0;
    public static final int bytesValue = 5;
    public static final int floatValue = 3;
    public static final int intValue = 1;
    public static final int longValue = 2;
    public static final int stringValue = 4;
    private int _tag;
    private Object _value;

    /* renamed from: android.os.StatsBootstrapAtomValue$Tag */
    /* loaded from: classes3.dex */
    public @interface Tag {
        public static final int boolValue = 0;
        public static final int bytesValue = 5;
        public static final int floatValue = 3;
        public static final int intValue = 1;
        public static final int longValue = 2;
        public static final int stringValue = 4;
    }

    public StatsBootstrapAtomValue() {
        this._tag = 0;
        this._value = false;
    }

    private StatsBootstrapAtomValue(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private StatsBootstrapAtomValue(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static StatsBootstrapAtomValue boolValue(boolean _value) {
        return new StatsBootstrapAtomValue(0, Boolean.valueOf(_value));
    }

    public boolean getBoolValue() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setBoolValue(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static StatsBootstrapAtomValue intValue(int _value) {
        return new StatsBootstrapAtomValue(1, Integer.valueOf(_value));
    }

    public int getIntValue() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setIntValue(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static StatsBootstrapAtomValue longValue(long _value) {
        return new StatsBootstrapAtomValue(2, Long.valueOf(_value));
    }

    public long getLongValue() {
        _assertTag(2);
        return ((Long) this._value).longValue();
    }

    public void setLongValue(long _value) {
        _set(2, Long.valueOf(_value));
    }

    public static StatsBootstrapAtomValue floatValue(float _value) {
        return new StatsBootstrapAtomValue(3, Float.valueOf(_value));
    }

    public float getFloatValue() {
        _assertTag(3);
        return ((Float) this._value).floatValue();
    }

    public void setFloatValue(float _value) {
        _set(3, Float.valueOf(_value));
    }

    public static StatsBootstrapAtomValue stringValue(String _value) {
        return new StatsBootstrapAtomValue(4, _value);
    }

    public String getStringValue() {
        _assertTag(4);
        return (String) this._value;
    }

    public void setStringValue(String _value) {
        _set(4, _value);
    }

    public static StatsBootstrapAtomValue bytesValue(byte[] _value) {
        return new StatsBootstrapAtomValue(5, _value);
    }

    public byte[] getBytesValue() {
        _assertTag(5);
        return (byte[]) this._value;
    }

    public void setBytesValue(byte[] _value) {
        _set(5, _value);
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        _aidl_parcel.writeInt(this._tag);
        switch (this._tag) {
            case 0:
                _aidl_parcel.writeBoolean(getBoolValue());
                return;
            case 1:
                _aidl_parcel.writeInt(getIntValue());
                return;
            case 2:
                _aidl_parcel.writeLong(getLongValue());
                return;
            case 3:
                _aidl_parcel.writeFloat(getFloatValue());
                return;
            case 4:
                _aidl_parcel.writeString(getStringValue());
                return;
            case 5:
                _aidl_parcel.writeByteArray(getBytesValue());
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
                int _aidl_value2 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value2));
                return;
            case 2:
                long _aidl_value3 = _aidl_parcel.readLong();
                _set(_aidl_tag, Long.valueOf(_aidl_value3));
                return;
            case 3:
                float _aidl_value4 = _aidl_parcel.readFloat();
                _set(_aidl_tag, Float.valueOf(_aidl_value4));
                return;
            case 4:
                String _aidl_value5 = _aidl_parcel.readString();
                _set(_aidl_tag, _aidl_value5);
                return;
            case 5:
                byte[] _aidl_value6 = _aidl_parcel.createByteArray();
                _set(_aidl_tag, _aidl_value6);
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
                return "boolValue";
            case 1:
                return "intValue";
            case 2:
                return "longValue";
            case 3:
                return "floatValue";
            case 4:
                return "stringValue";
            case 5:
                return "bytesValue";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
