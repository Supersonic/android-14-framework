package android.hardware.p005tv.tuner;

import android.content.Context;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.DemuxFilterEvent */
/* loaded from: classes2.dex */
public final class DemuxFilterEvent implements Parcelable {
    public static final Parcelable.Creator<DemuxFilterEvent> CREATOR = new Parcelable.Creator<DemuxFilterEvent>() { // from class: android.hardware.tv.tuner.DemuxFilterEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterEvent createFromParcel(Parcel _aidl_source) {
            return new DemuxFilterEvent(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterEvent[] newArray(int _aidl_size) {
            return new DemuxFilterEvent[_aidl_size];
        }
    };
    public static final int download = 5;
    public static final int ipPayload = 6;
    public static final int media = 1;
    public static final int mmtpRecord = 4;
    public static final int monitorEvent = 8;
    public static final int pes = 2;
    public static final int section = 0;
    public static final int startId = 9;
    public static final int temi = 7;
    public static final int tsRecord = 3;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.DemuxFilterEvent$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int download = 5;
        public static final int ipPayload = 6;
        public static final int media = 1;
        public static final int mmtpRecord = 4;
        public static final int monitorEvent = 8;
        public static final int pes = 2;
        public static final int section = 0;
        public static final int startId = 9;
        public static final int temi = 7;
        public static final int tsRecord = 3;
    }

    public DemuxFilterEvent() {
        this._tag = 0;
        this._value = null;
    }

    private DemuxFilterEvent(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DemuxFilterEvent(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static DemuxFilterEvent section(DemuxFilterSectionEvent _value) {
        return new DemuxFilterEvent(0, _value);
    }

    public DemuxFilterSectionEvent getSection() {
        _assertTag(0);
        return (DemuxFilterSectionEvent) this._value;
    }

    public void setSection(DemuxFilterSectionEvent _value) {
        _set(0, _value);
    }

    public static DemuxFilterEvent media(DemuxFilterMediaEvent _value) {
        return new DemuxFilterEvent(1, _value);
    }

    public DemuxFilterMediaEvent getMedia() {
        _assertTag(1);
        return (DemuxFilterMediaEvent) this._value;
    }

    public void setMedia(DemuxFilterMediaEvent _value) {
        _set(1, _value);
    }

    public static DemuxFilterEvent pes(DemuxFilterPesEvent _value) {
        return new DemuxFilterEvent(2, _value);
    }

    public DemuxFilterPesEvent getPes() {
        _assertTag(2);
        return (DemuxFilterPesEvent) this._value;
    }

    public void setPes(DemuxFilterPesEvent _value) {
        _set(2, _value);
    }

    public static DemuxFilterEvent tsRecord(DemuxFilterTsRecordEvent _value) {
        return new DemuxFilterEvent(3, _value);
    }

    public DemuxFilterTsRecordEvent getTsRecord() {
        _assertTag(3);
        return (DemuxFilterTsRecordEvent) this._value;
    }

    public void setTsRecord(DemuxFilterTsRecordEvent _value) {
        _set(3, _value);
    }

    public static DemuxFilterEvent mmtpRecord(DemuxFilterMmtpRecordEvent _value) {
        return new DemuxFilterEvent(4, _value);
    }

    public DemuxFilterMmtpRecordEvent getMmtpRecord() {
        _assertTag(4);
        return (DemuxFilterMmtpRecordEvent) this._value;
    }

    public void setMmtpRecord(DemuxFilterMmtpRecordEvent _value) {
        _set(4, _value);
    }

    public static DemuxFilterEvent download(DemuxFilterDownloadEvent _value) {
        return new DemuxFilterEvent(5, _value);
    }

    public DemuxFilterDownloadEvent getDownload() {
        _assertTag(5);
        return (DemuxFilterDownloadEvent) this._value;
    }

    public void setDownload(DemuxFilterDownloadEvent _value) {
        _set(5, _value);
    }

    public static DemuxFilterEvent ipPayload(DemuxFilterIpPayloadEvent _value) {
        return new DemuxFilterEvent(6, _value);
    }

    public DemuxFilterIpPayloadEvent getIpPayload() {
        _assertTag(6);
        return (DemuxFilterIpPayloadEvent) this._value;
    }

    public void setIpPayload(DemuxFilterIpPayloadEvent _value) {
        _set(6, _value);
    }

    public static DemuxFilterEvent temi(DemuxFilterTemiEvent _value) {
        return new DemuxFilterEvent(7, _value);
    }

    public DemuxFilterTemiEvent getTemi() {
        _assertTag(7);
        return (DemuxFilterTemiEvent) this._value;
    }

    public void setTemi(DemuxFilterTemiEvent _value) {
        _set(7, _value);
    }

    public static DemuxFilterEvent monitorEvent(DemuxFilterMonitorEvent _value) {
        return new DemuxFilterEvent(8, _value);
    }

    public DemuxFilterMonitorEvent getMonitorEvent() {
        _assertTag(8);
        return (DemuxFilterMonitorEvent) this._value;
    }

    public void setMonitorEvent(DemuxFilterMonitorEvent _value) {
        _set(8, _value);
    }

    public static DemuxFilterEvent startId(int _value) {
        return new DemuxFilterEvent(9, Integer.valueOf(_value));
    }

    public int getStartId() {
        _assertTag(9);
        return ((Integer) this._value).intValue();
    }

    public void setStartId(int _value) {
        _set(9, Integer.valueOf(_value));
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
                _aidl_parcel.writeTypedObject(getSection(), _aidl_flag);
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getMedia(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedObject(getPes(), _aidl_flag);
                return;
            case 3:
                _aidl_parcel.writeTypedObject(getTsRecord(), _aidl_flag);
                return;
            case 4:
                _aidl_parcel.writeTypedObject(getMmtpRecord(), _aidl_flag);
                return;
            case 5:
                _aidl_parcel.writeTypedObject(getDownload(), _aidl_flag);
                return;
            case 6:
                _aidl_parcel.writeTypedObject(getIpPayload(), _aidl_flag);
                return;
            case 7:
                _aidl_parcel.writeTypedObject(getTemi(), _aidl_flag);
                return;
            case 8:
                _aidl_parcel.writeTypedObject(getMonitorEvent(), _aidl_flag);
                return;
            case 9:
                _aidl_parcel.writeInt(getStartId());
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                DemuxFilterSectionEvent _aidl_value = (DemuxFilterSectionEvent) _aidl_parcel.readTypedObject(DemuxFilterSectionEvent.CREATOR);
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                DemuxFilterMediaEvent _aidl_value2 = (DemuxFilterMediaEvent) _aidl_parcel.readTypedObject(DemuxFilterMediaEvent.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                DemuxFilterPesEvent _aidl_value3 = (DemuxFilterPesEvent) _aidl_parcel.readTypedObject(DemuxFilterPesEvent.CREATOR);
                _set(_aidl_tag, _aidl_value3);
                return;
            case 3:
                DemuxFilterTsRecordEvent _aidl_value4 = (DemuxFilterTsRecordEvent) _aidl_parcel.readTypedObject(DemuxFilterTsRecordEvent.CREATOR);
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                DemuxFilterMmtpRecordEvent _aidl_value5 = (DemuxFilterMmtpRecordEvent) _aidl_parcel.readTypedObject(DemuxFilterMmtpRecordEvent.CREATOR);
                _set(_aidl_tag, _aidl_value5);
                return;
            case 5:
                DemuxFilterDownloadEvent _aidl_value6 = (DemuxFilterDownloadEvent) _aidl_parcel.readTypedObject(DemuxFilterDownloadEvent.CREATOR);
                _set(_aidl_tag, _aidl_value6);
                return;
            case 6:
                DemuxFilterIpPayloadEvent _aidl_value7 = (DemuxFilterIpPayloadEvent) _aidl_parcel.readTypedObject(DemuxFilterIpPayloadEvent.CREATOR);
                _set(_aidl_tag, _aidl_value7);
                return;
            case 7:
                DemuxFilterTemiEvent _aidl_value8 = (DemuxFilterTemiEvent) _aidl_parcel.readTypedObject(DemuxFilterTemiEvent.CREATOR);
                _set(_aidl_tag, _aidl_value8);
                return;
            case 8:
                DemuxFilterMonitorEvent _aidl_value9 = (DemuxFilterMonitorEvent) _aidl_parcel.readTypedObject(DemuxFilterMonitorEvent.CREATOR);
                _set(_aidl_tag, _aidl_value9);
                return;
            case 9:
                int _aidl_value10 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value10));
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 0:
                int _mask = 0 | describeContents(getSection());
                return _mask;
            case 1:
                int _mask2 = 0 | describeContents(getMedia());
                return _mask2;
            case 2:
                int _mask3 = 0 | describeContents(getPes());
                return _mask3;
            case 3:
                int _mask4 = 0 | describeContents(getTsRecord());
                return _mask4;
            case 4:
                int _mask5 = 0 | describeContents(getMmtpRecord());
                return _mask5;
            case 5:
                int _mask6 = 0 | describeContents(getDownload());
                return _mask6;
            case 6:
                int _mask7 = 0 | describeContents(getIpPayload());
                return _mask7;
            case 7:
                int _mask8 = 0 | describeContents(getTemi());
                return _mask8;
            case 8:
                int _mask9 = 0 | describeContents(getMonitorEvent());
                return _mask9;
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
                return "section";
            case 1:
                return "media";
            case 2:
                return "pes";
            case 3:
                return "tsRecord";
            case 4:
                return "mmtpRecord";
            case 5:
                return Context.DOWNLOAD_SERVICE;
            case 6:
                return "ipPayload";
            case 7:
                return "temi";
            case 8:
                return "monitorEvent";
            case 9:
                return "startId";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
