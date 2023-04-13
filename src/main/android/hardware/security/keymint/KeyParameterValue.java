package android.hardware.security.keymint;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.org.bouncycastle.cms.CMSAttributeTableGenerator;
/* loaded from: classes2.dex */
public final class KeyParameterValue implements Parcelable {
    public static final Parcelable.Creator<KeyParameterValue> CREATOR = new Parcelable.Creator<KeyParameterValue>() { // from class: android.hardware.security.keymint.KeyParameterValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyParameterValue createFromParcel(Parcel _aidl_source) {
            return new KeyParameterValue(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyParameterValue[] newArray(int _aidl_size) {
            return new KeyParameterValue[_aidl_size];
        }
    };
    public static final int algorithm = 1;
    public static final int blob = 14;
    public static final int blockMode = 2;
    public static final int boolValue = 10;
    public static final int dateTime = 13;
    public static final int digest = 4;
    public static final int ecCurve = 5;
    public static final int hardwareAuthenticatorType = 8;
    public static final int integer = 11;
    public static final int invalid = 0;
    public static final int keyPurpose = 7;
    public static final int longInteger = 12;
    public static final int origin = 6;
    public static final int paddingMode = 3;
    public static final int securityLevel = 9;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int algorithm = 1;
        public static final int blob = 14;
        public static final int blockMode = 2;
        public static final int boolValue = 10;
        public static final int dateTime = 13;
        public static final int digest = 4;
        public static final int ecCurve = 5;
        public static final int hardwareAuthenticatorType = 8;
        public static final int integer = 11;
        public static final int invalid = 0;
        public static final int keyPurpose = 7;
        public static final int longInteger = 12;
        public static final int origin = 6;
        public static final int paddingMode = 3;
        public static final int securityLevel = 9;
    }

    public KeyParameterValue() {
        this._tag = 0;
        this._value = 0;
    }

    private KeyParameterValue(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private KeyParameterValue(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static KeyParameterValue invalid(int _value) {
        return new KeyParameterValue(0, Integer.valueOf(_value));
    }

    public int getInvalid() {
        _assertTag(0);
        return ((Integer) this._value).intValue();
    }

    public void setInvalid(int _value) {
        _set(0, Integer.valueOf(_value));
    }

    public static KeyParameterValue algorithm(int _value) {
        return new KeyParameterValue(1, Integer.valueOf(_value));
    }

    public int getAlgorithm() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setAlgorithm(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static KeyParameterValue blockMode(int _value) {
        return new KeyParameterValue(2, Integer.valueOf(_value));
    }

    public int getBlockMode() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setBlockMode(int _value) {
        _set(2, Integer.valueOf(_value));
    }

    public static KeyParameterValue paddingMode(int _value) {
        return new KeyParameterValue(3, Integer.valueOf(_value));
    }

    public int getPaddingMode() {
        _assertTag(3);
        return ((Integer) this._value).intValue();
    }

    public void setPaddingMode(int _value) {
        _set(3, Integer.valueOf(_value));
    }

    public static KeyParameterValue digest(int _value) {
        return new KeyParameterValue(4, Integer.valueOf(_value));
    }

    public int getDigest() {
        _assertTag(4);
        return ((Integer) this._value).intValue();
    }

    public void setDigest(int _value) {
        _set(4, Integer.valueOf(_value));
    }

    public static KeyParameterValue ecCurve(int _value) {
        return new KeyParameterValue(5, Integer.valueOf(_value));
    }

    public int getEcCurve() {
        _assertTag(5);
        return ((Integer) this._value).intValue();
    }

    public void setEcCurve(int _value) {
        _set(5, Integer.valueOf(_value));
    }

    public static KeyParameterValue origin(int _value) {
        return new KeyParameterValue(6, Integer.valueOf(_value));
    }

    public int getOrigin() {
        _assertTag(6);
        return ((Integer) this._value).intValue();
    }

    public void setOrigin(int _value) {
        _set(6, Integer.valueOf(_value));
    }

    public static KeyParameterValue keyPurpose(int _value) {
        return new KeyParameterValue(7, Integer.valueOf(_value));
    }

    public int getKeyPurpose() {
        _assertTag(7);
        return ((Integer) this._value).intValue();
    }

    public void setKeyPurpose(int _value) {
        _set(7, Integer.valueOf(_value));
    }

    public static KeyParameterValue hardwareAuthenticatorType(int _value) {
        return new KeyParameterValue(8, Integer.valueOf(_value));
    }

    public int getHardwareAuthenticatorType() {
        _assertTag(8);
        return ((Integer) this._value).intValue();
    }

    public void setHardwareAuthenticatorType(int _value) {
        _set(8, Integer.valueOf(_value));
    }

    public static KeyParameterValue securityLevel(int _value) {
        return new KeyParameterValue(9, Integer.valueOf(_value));
    }

    public int getSecurityLevel() {
        _assertTag(9);
        return ((Integer) this._value).intValue();
    }

    public void setSecurityLevel(int _value) {
        _set(9, Integer.valueOf(_value));
    }

    public static KeyParameterValue boolValue(boolean _value) {
        return new KeyParameterValue(10, Boolean.valueOf(_value));
    }

    public boolean getBoolValue() {
        _assertTag(10);
        return ((Boolean) this._value).booleanValue();
    }

    public void setBoolValue(boolean _value) {
        _set(10, Boolean.valueOf(_value));
    }

    public static KeyParameterValue integer(int _value) {
        return new KeyParameterValue(11, Integer.valueOf(_value));
    }

    public int getInteger() {
        _assertTag(11);
        return ((Integer) this._value).intValue();
    }

    public void setInteger(int _value) {
        _set(11, Integer.valueOf(_value));
    }

    public static KeyParameterValue longInteger(long _value) {
        return new KeyParameterValue(12, Long.valueOf(_value));
    }

    public long getLongInteger() {
        _assertTag(12);
        return ((Long) this._value).longValue();
    }

    public void setLongInteger(long _value) {
        _set(12, Long.valueOf(_value));
    }

    public static KeyParameterValue dateTime(long _value) {
        return new KeyParameterValue(13, Long.valueOf(_value));
    }

    public long getDateTime() {
        _assertTag(13);
        return ((Long) this._value).longValue();
    }

    public void setDateTime(long _value) {
        _set(13, Long.valueOf(_value));
    }

    public static KeyParameterValue blob(byte[] _value) {
        return new KeyParameterValue(14, _value);
    }

    public byte[] getBlob() {
        _assertTag(14);
        return (byte[]) this._value;
    }

    public void setBlob(byte[] _value) {
        _set(14, _value);
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
                _aidl_parcel.writeInt(getInvalid());
                return;
            case 1:
                _aidl_parcel.writeInt(getAlgorithm());
                return;
            case 2:
                _aidl_parcel.writeInt(getBlockMode());
                return;
            case 3:
                _aidl_parcel.writeInt(getPaddingMode());
                return;
            case 4:
                _aidl_parcel.writeInt(getDigest());
                return;
            case 5:
                _aidl_parcel.writeInt(getEcCurve());
                return;
            case 6:
                _aidl_parcel.writeInt(getOrigin());
                return;
            case 7:
                _aidl_parcel.writeInt(getKeyPurpose());
                return;
            case 8:
                _aidl_parcel.writeInt(getHardwareAuthenticatorType());
                return;
            case 9:
                _aidl_parcel.writeInt(getSecurityLevel());
                return;
            case 10:
                _aidl_parcel.writeBoolean(getBoolValue());
                return;
            case 11:
                _aidl_parcel.writeInt(getInteger());
                return;
            case 12:
                _aidl_parcel.writeLong(getLongInteger());
                return;
            case 13:
                _aidl_parcel.writeLong(getDateTime());
                return;
            case 14:
                _aidl_parcel.writeByteArray(getBlob());
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
            case 5:
                int _aidl_value6 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value6));
                return;
            case 6:
                int _aidl_value7 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value7));
                return;
            case 7:
                int _aidl_value8 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value8));
                return;
            case 8:
                int _aidl_value9 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value9));
                return;
            case 9:
                int _aidl_value10 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value10));
                return;
            case 10:
                boolean _aidl_value11 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value11));
                return;
            case 11:
                int _aidl_value12 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value12));
                return;
            case 12:
                long _aidl_value13 = _aidl_parcel.readLong();
                _set(_aidl_tag, Long.valueOf(_aidl_value13));
                return;
            case 13:
                long _aidl_value14 = _aidl_parcel.readLong();
                _set(_aidl_tag, Long.valueOf(_aidl_value14));
                return;
            case 14:
                byte[] _aidl_value15 = _aidl_parcel.createByteArray();
                _set(_aidl_tag, _aidl_value15);
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
                return "invalid";
            case 1:
                return "algorithm";
            case 2:
                return "blockMode";
            case 3:
                return "paddingMode";
            case 4:
                return CMSAttributeTableGenerator.DIGEST;
            case 5:
                return "ecCurve";
            case 6:
                return "origin";
            case 7:
                return "keyPurpose";
            case 8:
                return "hardwareAuthenticatorType";
            case 9:
                return "securityLevel";
            case 10:
                return "boolValue";
            case 11:
                return "integer";
            case 12:
                return "longInteger";
            case 13:
                return "dateTime";
            case 14:
                return "blob";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
