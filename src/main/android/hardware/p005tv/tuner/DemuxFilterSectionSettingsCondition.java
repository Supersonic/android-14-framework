package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.DemuxFilterSectionSettingsCondition */
/* loaded from: classes2.dex */
public final class DemuxFilterSectionSettingsCondition implements Parcelable {
    public static final Parcelable.Creator<DemuxFilterSectionSettingsCondition> CREATOR = new Parcelable.Creator<DemuxFilterSectionSettingsCondition>() { // from class: android.hardware.tv.tuner.DemuxFilterSectionSettingsCondition.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterSectionSettingsCondition createFromParcel(Parcel _aidl_source) {
            return new DemuxFilterSectionSettingsCondition(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterSectionSettingsCondition[] newArray(int _aidl_size) {
            return new DemuxFilterSectionSettingsCondition[_aidl_size];
        }
    };
    public static final int sectionBits = 0;
    public static final int tableInfo = 1;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.DemuxFilterSectionSettingsCondition$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int sectionBits = 0;
        public static final int tableInfo = 1;
    }

    public DemuxFilterSectionSettingsCondition() {
        this._tag = 0;
        this._value = null;
    }

    private DemuxFilterSectionSettingsCondition(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DemuxFilterSectionSettingsCondition(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static DemuxFilterSectionSettingsCondition sectionBits(DemuxFilterSectionBits _value) {
        return new DemuxFilterSectionSettingsCondition(0, _value);
    }

    public DemuxFilterSectionBits getSectionBits() {
        _assertTag(0);
        return (DemuxFilterSectionBits) this._value;
    }

    public void setSectionBits(DemuxFilterSectionBits _value) {
        _set(0, _value);
    }

    public static DemuxFilterSectionSettingsCondition tableInfo(DemuxFilterSectionSettingsConditionTableInfo _value) {
        return new DemuxFilterSectionSettingsCondition(1, _value);
    }

    public DemuxFilterSectionSettingsConditionTableInfo getTableInfo() {
        _assertTag(1);
        return (DemuxFilterSectionSettingsConditionTableInfo) this._value;
    }

    public void setTableInfo(DemuxFilterSectionSettingsConditionTableInfo _value) {
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
                _aidl_parcel.writeTypedObject(getSectionBits(), _aidl_flag);
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getTableInfo(), _aidl_flag);
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                DemuxFilterSectionBits _aidl_value = (DemuxFilterSectionBits) _aidl_parcel.readTypedObject(DemuxFilterSectionBits.CREATOR);
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                DemuxFilterSectionSettingsConditionTableInfo _aidl_value2 = (DemuxFilterSectionSettingsConditionTableInfo) _aidl_parcel.readTypedObject(DemuxFilterSectionSettingsConditionTableInfo.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 0:
                int _mask = 0 | describeContents(getSectionBits());
                return _mask;
            case 1:
                int _mask2 = 0 | describeContents(getTableInfo());
                return _mask2;
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
                return "sectionBits";
            case 1:
                return "tableInfo";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
