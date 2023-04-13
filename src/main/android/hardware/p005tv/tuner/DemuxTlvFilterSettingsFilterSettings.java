package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.DemuxTlvFilterSettingsFilterSettings */
/* loaded from: classes2.dex */
public final class DemuxTlvFilterSettingsFilterSettings implements Parcelable {
    public static final Parcelable.Creator<DemuxTlvFilterSettingsFilterSettings> CREATOR = new Parcelable.Creator<DemuxTlvFilterSettingsFilterSettings>() { // from class: android.hardware.tv.tuner.DemuxTlvFilterSettingsFilterSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxTlvFilterSettingsFilterSettings createFromParcel(Parcel _aidl_source) {
            return new DemuxTlvFilterSettingsFilterSettings(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxTlvFilterSettingsFilterSettings[] newArray(int _aidl_size) {
            return new DemuxTlvFilterSettingsFilterSettings[_aidl_size];
        }
    };
    public static final int bPassthrough = 2;
    public static final int noinit = 0;
    public static final int section = 1;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.DemuxTlvFilterSettingsFilterSettings$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int bPassthrough = 2;
        public static final int noinit = 0;
        public static final int section = 1;
    }

    public DemuxTlvFilterSettingsFilterSettings() {
        this._tag = 0;
        this._value = false;
    }

    private DemuxTlvFilterSettingsFilterSettings(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DemuxTlvFilterSettingsFilterSettings(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static DemuxTlvFilterSettingsFilterSettings noinit(boolean _value) {
        return new DemuxTlvFilterSettingsFilterSettings(0, Boolean.valueOf(_value));
    }

    public boolean getNoinit() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setNoinit(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static DemuxTlvFilterSettingsFilterSettings section(DemuxFilterSectionSettings _value) {
        return new DemuxTlvFilterSettingsFilterSettings(1, _value);
    }

    public DemuxFilterSectionSettings getSection() {
        _assertTag(1);
        return (DemuxFilterSectionSettings) this._value;
    }

    public void setSection(DemuxFilterSectionSettings _value) {
        _set(1, _value);
    }

    public static DemuxTlvFilterSettingsFilterSettings bPassthrough(boolean _value) {
        return new DemuxTlvFilterSettingsFilterSettings(2, Boolean.valueOf(_value));
    }

    public boolean getBPassthrough() {
        _assertTag(2);
        return ((Boolean) this._value).booleanValue();
    }

    public void setBPassthrough(boolean _value) {
        _set(2, Boolean.valueOf(_value));
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
                _aidl_parcel.writeBoolean(getBPassthrough());
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
                boolean _aidl_value3 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value3));
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
                return "bPassthrough";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
