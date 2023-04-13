package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.DemuxFilterScIndexMask */
/* loaded from: classes2.dex */
public final class DemuxFilterScIndexMask implements Parcelable {
    public static final Parcelable.Creator<DemuxFilterScIndexMask> CREATOR = new Parcelable.Creator<DemuxFilterScIndexMask>() { // from class: android.hardware.tv.tuner.DemuxFilterScIndexMask.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterScIndexMask createFromParcel(Parcel _aidl_source) {
            return new DemuxFilterScIndexMask(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterScIndexMask[] newArray(int _aidl_size) {
            return new DemuxFilterScIndexMask[_aidl_size];
        }
    };
    public static final int scAvc = 1;
    public static final int scHevc = 2;
    public static final int scIndex = 0;
    public static final int scVvc = 3;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.DemuxFilterScIndexMask$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int scAvc = 1;
        public static final int scHevc = 2;
        public static final int scIndex = 0;
        public static final int scVvc = 3;
    }

    public DemuxFilterScIndexMask() {
        this._tag = 0;
        this._value = 0;
    }

    private DemuxFilterScIndexMask(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DemuxFilterScIndexMask(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static DemuxFilterScIndexMask scIndex(int _value) {
        return new DemuxFilterScIndexMask(0, Integer.valueOf(_value));
    }

    public int getScIndex() {
        _assertTag(0);
        return ((Integer) this._value).intValue();
    }

    public void setScIndex(int _value) {
        _set(0, Integer.valueOf(_value));
    }

    public static DemuxFilterScIndexMask scAvc(int _value) {
        return new DemuxFilterScIndexMask(1, Integer.valueOf(_value));
    }

    public int getScAvc() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setScAvc(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static DemuxFilterScIndexMask scHevc(int _value) {
        return new DemuxFilterScIndexMask(2, Integer.valueOf(_value));
    }

    public int getScHevc() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setScHevc(int _value) {
        _set(2, Integer.valueOf(_value));
    }

    public static DemuxFilterScIndexMask scVvc(int _value) {
        return new DemuxFilterScIndexMask(3, Integer.valueOf(_value));
    }

    public int getScVvc() {
        _assertTag(3);
        return ((Integer) this._value).intValue();
    }

    public void setScVvc(int _value) {
        _set(3, Integer.valueOf(_value));
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
                _aidl_parcel.writeInt(getScIndex());
                return;
            case 1:
                _aidl_parcel.writeInt(getScAvc());
                return;
            case 2:
                _aidl_parcel.writeInt(getScHevc());
                return;
            case 3:
                _aidl_parcel.writeInt(getScVvc());
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
                return "scIndex";
            case 1:
                return "scAvc";
            case 2:
                return "scHevc";
            case 3:
                return "scVvc";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
