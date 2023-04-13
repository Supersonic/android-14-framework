package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.FrontendScanMessage */
/* loaded from: classes2.dex */
public final class FrontendScanMessage implements Parcelable {
    public static final Parcelable.Creator<FrontendScanMessage> CREATOR = new Parcelable.Creator<FrontendScanMessage>() { // from class: android.hardware.tv.tuner.FrontendScanMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendScanMessage createFromParcel(Parcel _aidl_source) {
            return new FrontendScanMessage(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendScanMessage[] newArray(int _aidl_size) {
            return new FrontendScanMessage[_aidl_size];
        }
    };
    public static final int analogType = 6;
    public static final int annex = 13;
    public static final int atsc3PlpInfos = 11;
    public static final int dvbtCellIds = 15;
    public static final int frequencies = 3;
    public static final int groupIds = 8;
    public static final int hierarchy = 5;
    public static final int inputStreamIds = 9;
    public static final int isEnd = 1;
    public static final int isHighPriority = 14;
    public static final int isLocked = 0;
    public static final int modulation = 12;
    public static final int plpIds = 7;
    public static final int progressPercent = 2;
    public static final int std = 10;
    public static final int symbolRates = 4;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.FrontendScanMessage$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int analogType = 6;
        public static final int annex = 13;
        public static final int atsc3PlpInfos = 11;
        public static final int dvbtCellIds = 15;
        public static final int frequencies = 3;
        public static final int groupIds = 8;
        public static final int hierarchy = 5;
        public static final int inputStreamIds = 9;
        public static final int isEnd = 1;
        public static final int isHighPriority = 14;
        public static final int isLocked = 0;
        public static final int modulation = 12;
        public static final int plpIds = 7;
        public static final int progressPercent = 2;
        public static final int std = 10;
        public static final int symbolRates = 4;
    }

    public FrontendScanMessage() {
        this._tag = 0;
        this._value = false;
    }

    private FrontendScanMessage(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private FrontendScanMessage(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static FrontendScanMessage isLocked(boolean _value) {
        return new FrontendScanMessage(0, Boolean.valueOf(_value));
    }

    public boolean getIsLocked() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setIsLocked(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static FrontendScanMessage isEnd(boolean _value) {
        return new FrontendScanMessage(1, Boolean.valueOf(_value));
    }

    public boolean getIsEnd() {
        _assertTag(1);
        return ((Boolean) this._value).booleanValue();
    }

    public void setIsEnd(boolean _value) {
        _set(1, Boolean.valueOf(_value));
    }

    public static FrontendScanMessage progressPercent(int _value) {
        return new FrontendScanMessage(2, Integer.valueOf(_value));
    }

    public int getProgressPercent() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setProgressPercent(int _value) {
        _set(2, Integer.valueOf(_value));
    }

    public static FrontendScanMessage frequencies(long[] _value) {
        return new FrontendScanMessage(3, _value);
    }

    public long[] getFrequencies() {
        _assertTag(3);
        return (long[]) this._value;
    }

    public void setFrequencies(long[] _value) {
        _set(3, _value);
    }

    public static FrontendScanMessage symbolRates(int[] _value) {
        return new FrontendScanMessage(4, _value);
    }

    public int[] getSymbolRates() {
        _assertTag(4);
        return (int[]) this._value;
    }

    public void setSymbolRates(int[] _value) {
        _set(4, _value);
    }

    public static FrontendScanMessage hierarchy(int _value) {
        return new FrontendScanMessage(5, Integer.valueOf(_value));
    }

    public int getHierarchy() {
        _assertTag(5);
        return ((Integer) this._value).intValue();
    }

    public void setHierarchy(int _value) {
        _set(5, Integer.valueOf(_value));
    }

    public static FrontendScanMessage analogType(int _value) {
        return new FrontendScanMessage(6, Integer.valueOf(_value));
    }

    public int getAnalogType() {
        _assertTag(6);
        return ((Integer) this._value).intValue();
    }

    public void setAnalogType(int _value) {
        _set(6, Integer.valueOf(_value));
    }

    public static FrontendScanMessage plpIds(int[] _value) {
        return new FrontendScanMessage(7, _value);
    }

    public int[] getPlpIds() {
        _assertTag(7);
        return (int[]) this._value;
    }

    public void setPlpIds(int[] _value) {
        _set(7, _value);
    }

    public static FrontendScanMessage groupIds(int[] _value) {
        return new FrontendScanMessage(8, _value);
    }

    public int[] getGroupIds() {
        _assertTag(8);
        return (int[]) this._value;
    }

    public void setGroupIds(int[] _value) {
        _set(8, _value);
    }

    public static FrontendScanMessage inputStreamIds(int[] _value) {
        return new FrontendScanMessage(9, _value);
    }

    public int[] getInputStreamIds() {
        _assertTag(9);
        return (int[]) this._value;
    }

    public void setInputStreamIds(int[] _value) {
        _set(9, _value);
    }

    public static FrontendScanMessage std(FrontendScanMessageStandard _value) {
        return new FrontendScanMessage(10, _value);
    }

    public FrontendScanMessageStandard getStd() {
        _assertTag(10);
        return (FrontendScanMessageStandard) this._value;
    }

    public void setStd(FrontendScanMessageStandard _value) {
        _set(10, _value);
    }

    public static FrontendScanMessage atsc3PlpInfos(FrontendScanAtsc3PlpInfo[] _value) {
        return new FrontendScanMessage(11, _value);
    }

    public FrontendScanAtsc3PlpInfo[] getAtsc3PlpInfos() {
        _assertTag(11);
        return (FrontendScanAtsc3PlpInfo[]) this._value;
    }

    public void setAtsc3PlpInfos(FrontendScanAtsc3PlpInfo[] _value) {
        _set(11, _value);
    }

    public static FrontendScanMessage modulation(FrontendModulation _value) {
        return new FrontendScanMessage(12, _value);
    }

    public FrontendModulation getModulation() {
        _assertTag(12);
        return (FrontendModulation) this._value;
    }

    public void setModulation(FrontendModulation _value) {
        _set(12, _value);
    }

    public static FrontendScanMessage annex(byte _value) {
        return new FrontendScanMessage(13, Byte.valueOf(_value));
    }

    public byte getAnnex() {
        _assertTag(13);
        return ((Byte) this._value).byteValue();
    }

    public void setAnnex(byte _value) {
        _set(13, Byte.valueOf(_value));
    }

    public static FrontendScanMessage isHighPriority(boolean _value) {
        return new FrontendScanMessage(14, Boolean.valueOf(_value));
    }

    public boolean getIsHighPriority() {
        _assertTag(14);
        return ((Boolean) this._value).booleanValue();
    }

    public void setIsHighPriority(boolean _value) {
        _set(14, Boolean.valueOf(_value));
    }

    public static FrontendScanMessage dvbtCellIds(int[] _value) {
        return new FrontendScanMessage(15, _value);
    }

    public int[] getDvbtCellIds() {
        _assertTag(15);
        return (int[]) this._value;
    }

    public void setDvbtCellIds(int[] _value) {
        _set(15, _value);
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
                _aidl_parcel.writeBoolean(getIsLocked());
                return;
            case 1:
                _aidl_parcel.writeBoolean(getIsEnd());
                return;
            case 2:
                _aidl_parcel.writeInt(getProgressPercent());
                return;
            case 3:
                _aidl_parcel.writeLongArray(getFrequencies());
                return;
            case 4:
                _aidl_parcel.writeIntArray(getSymbolRates());
                return;
            case 5:
                _aidl_parcel.writeInt(getHierarchy());
                return;
            case 6:
                _aidl_parcel.writeInt(getAnalogType());
                return;
            case 7:
                _aidl_parcel.writeIntArray(getPlpIds());
                return;
            case 8:
                _aidl_parcel.writeIntArray(getGroupIds());
                return;
            case 9:
                _aidl_parcel.writeIntArray(getInputStreamIds());
                return;
            case 10:
                _aidl_parcel.writeTypedObject(getStd(), _aidl_flag);
                return;
            case 11:
                _aidl_parcel.writeTypedArray(getAtsc3PlpInfos(), _aidl_flag);
                return;
            case 12:
                _aidl_parcel.writeTypedObject(getModulation(), _aidl_flag);
                return;
            case 13:
                _aidl_parcel.writeByte(getAnnex());
                return;
            case 14:
                _aidl_parcel.writeBoolean(getIsHighPriority());
                return;
            case 15:
                _aidl_parcel.writeIntArray(getDvbtCellIds());
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
                boolean _aidl_value2 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value2));
                return;
            case 2:
                int _aidl_value3 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value3));
                return;
            case 3:
                long[] _aidl_value4 = _aidl_parcel.createLongArray();
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                int[] _aidl_value5 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value5);
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
                int[] _aidl_value8 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value8);
                return;
            case 8:
                int[] _aidl_value9 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value9);
                return;
            case 9:
                int[] _aidl_value10 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value10);
                return;
            case 10:
                FrontendScanMessageStandard _aidl_value11 = (FrontendScanMessageStandard) _aidl_parcel.readTypedObject(FrontendScanMessageStandard.CREATOR);
                _set(_aidl_tag, _aidl_value11);
                return;
            case 11:
                FrontendScanAtsc3PlpInfo[] _aidl_value12 = (FrontendScanAtsc3PlpInfo[]) _aidl_parcel.createTypedArray(FrontendScanAtsc3PlpInfo.CREATOR);
                _set(_aidl_tag, _aidl_value12);
                return;
            case 12:
                FrontendModulation _aidl_value13 = (FrontendModulation) _aidl_parcel.readTypedObject(FrontendModulation.CREATOR);
                _set(_aidl_tag, _aidl_value13);
                return;
            case 13:
                byte _aidl_value14 = _aidl_parcel.readByte();
                _set(_aidl_tag, Byte.valueOf(_aidl_value14));
                return;
            case 14:
                boolean _aidl_value15 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value15));
                return;
            case 15:
                int[] _aidl_value16 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value16);
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 10:
                int _mask = 0 | describeContents(getStd());
                return _mask;
            case 11:
                int _mask2 = 0 | describeContents(getAtsc3PlpInfos());
                return _mask2;
            case 12:
                int _mask3 = 0 | describeContents(getModulation());
                return _mask3;
            default:
                return 0;
        }
    }

    private int describeContents(Object _v) {
        Object[] objArr;
        if (_v == null) {
            return 0;
        }
        if (_v instanceof Object[]) {
            int _mask = 0;
            for (Object o : (Object[]) _v) {
                _mask |= describeContents(o);
            }
            return _mask;
        } else if (!(_v instanceof Parcelable)) {
            return 0;
        } else {
            return ((Parcelable) _v).describeContents();
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
                return "isLocked";
            case 1:
                return "isEnd";
            case 2:
                return "progressPercent";
            case 3:
                return "frequencies";
            case 4:
                return "symbolRates";
            case 5:
                return "hierarchy";
            case 6:
                return "analogType";
            case 7:
                return "plpIds";
            case 8:
                return "groupIds";
            case 9:
                return "inputStreamIds";
            case 10:
                return "std";
            case 11:
                return "atsc3PlpInfos";
            case 12:
                return "modulation";
            case 13:
                return "annex";
            case 14:
                return "isHighPriority";
            case 15:
                return "dvbtCellIds";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
