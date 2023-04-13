package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.DemuxTsFilterSettingsFilterSettings */
/* loaded from: classes2.dex */
public final class DemuxTsFilterSettingsFilterSettings implements Parcelable {
    public static final Parcelable.Creator<DemuxTsFilterSettingsFilterSettings> CREATOR = new Parcelable.Creator<DemuxTsFilterSettingsFilterSettings>() { // from class: android.hardware.tv.tuner.DemuxTsFilterSettingsFilterSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxTsFilterSettingsFilterSettings createFromParcel(Parcel _aidl_source) {
            return new DemuxTsFilterSettingsFilterSettings(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxTsFilterSettingsFilterSettings[] newArray(int _aidl_size) {
            return new DemuxTsFilterSettingsFilterSettings[_aidl_size];
        }
    };

    /* renamed from: av */
    public static final int f231av = 2;
    public static final int noinit = 0;
    public static final int pesData = 3;
    public static final int record = 4;
    public static final int section = 1;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.DemuxTsFilterSettingsFilterSettings$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {

        /* renamed from: av */
        public static final int f232av = 2;
        public static final int noinit = 0;
        public static final int pesData = 3;
        public static final int record = 4;
        public static final int section = 1;
    }

    public DemuxTsFilterSettingsFilterSettings() {
        this._tag = 0;
        this._value = false;
    }

    private DemuxTsFilterSettingsFilterSettings(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DemuxTsFilterSettingsFilterSettings(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static DemuxTsFilterSettingsFilterSettings noinit(boolean _value) {
        return new DemuxTsFilterSettingsFilterSettings(0, Boolean.valueOf(_value));
    }

    public boolean getNoinit() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setNoinit(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static DemuxTsFilterSettingsFilterSettings section(DemuxFilterSectionSettings _value) {
        return new DemuxTsFilterSettingsFilterSettings(1, _value);
    }

    public DemuxFilterSectionSettings getSection() {
        _assertTag(1);
        return (DemuxFilterSectionSettings) this._value;
    }

    public void setSection(DemuxFilterSectionSettings _value) {
        _set(1, _value);
    }

    /* renamed from: av */
    public static DemuxTsFilterSettingsFilterSettings m156av(DemuxFilterAvSettings _value) {
        return new DemuxTsFilterSettingsFilterSettings(2, _value);
    }

    public DemuxFilterAvSettings getAv() {
        _assertTag(2);
        return (DemuxFilterAvSettings) this._value;
    }

    public void setAv(DemuxFilterAvSettings _value) {
        _set(2, _value);
    }

    public static DemuxTsFilterSettingsFilterSettings pesData(DemuxFilterPesDataSettings _value) {
        return new DemuxTsFilterSettingsFilterSettings(3, _value);
    }

    public DemuxFilterPesDataSettings getPesData() {
        _assertTag(3);
        return (DemuxFilterPesDataSettings) this._value;
    }

    public void setPesData(DemuxFilterPesDataSettings _value) {
        _set(3, _value);
    }

    public static DemuxTsFilterSettingsFilterSettings record(DemuxFilterRecordSettings _value) {
        return new DemuxTsFilterSettingsFilterSettings(4, _value);
    }

    public DemuxFilterRecordSettings getRecord() {
        _assertTag(4);
        return (DemuxFilterRecordSettings) this._value;
    }

    public void setRecord(DemuxFilterRecordSettings _value) {
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
                _aidl_parcel.writeBoolean(getNoinit());
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getSection(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedObject(getAv(), _aidl_flag);
                return;
            case 3:
                _aidl_parcel.writeTypedObject(getPesData(), _aidl_flag);
                return;
            case 4:
                _aidl_parcel.writeTypedObject(getRecord(), _aidl_flag);
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
                DemuxFilterSectionSettings _aidl_value2 = (DemuxFilterSectionSettings) _aidl_parcel.readTypedObject(DemuxFilterSectionSettings.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                DemuxFilterAvSettings _aidl_value3 = (DemuxFilterAvSettings) _aidl_parcel.readTypedObject(DemuxFilterAvSettings.CREATOR);
                _set(_aidl_tag, _aidl_value3);
                return;
            case 3:
                DemuxFilterPesDataSettings _aidl_value4 = (DemuxFilterPesDataSettings) _aidl_parcel.readTypedObject(DemuxFilterPesDataSettings.CREATOR);
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                DemuxFilterRecordSettings _aidl_value5 = (DemuxFilterRecordSettings) _aidl_parcel.readTypedObject(DemuxFilterRecordSettings.CREATOR);
                _set(_aidl_tag, _aidl_value5);
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 1:
                int _mask = 0 | describeContents(getSection());
                return _mask;
            case 2:
                int _mask2 = 0 | describeContents(getAv());
                return _mask2;
            case 3:
                int _mask3 = 0 | describeContents(getPesData());
                return _mask3;
            case 4:
                int _mask4 = 0 | describeContents(getRecord());
                return _mask4;
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
                return "noinit";
            case 1:
                return "section";
            case 2:
                return "av";
            case 3:
                return "pesData";
            case 4:
                return "record";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
