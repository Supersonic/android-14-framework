package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.DvrSettings */
/* loaded from: classes2.dex */
public final class DvrSettings implements Parcelable {
    public static final Parcelable.Creator<DvrSettings> CREATOR = new Parcelable.Creator<DvrSettings>() { // from class: android.hardware.tv.tuner.DvrSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DvrSettings createFromParcel(Parcel _aidl_source) {
            return new DvrSettings(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DvrSettings[] newArray(int _aidl_size) {
            return new DvrSettings[_aidl_size];
        }
    };
    public static final int playback = 1;
    public static final int record = 0;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.DvrSettings$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int playback = 1;
        public static final int record = 0;
    }

    public DvrSettings() {
        this._tag = 0;
        this._value = null;
    }

    private DvrSettings(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DvrSettings(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static DvrSettings record(RecordSettings _value) {
        return new DvrSettings(0, _value);
    }

    public RecordSettings getRecord() {
        _assertTag(0);
        return (RecordSettings) this._value;
    }

    public void setRecord(RecordSettings _value) {
        _set(0, _value);
    }

    public static DvrSettings playback(PlaybackSettings _value) {
        return new DvrSettings(1, _value);
    }

    public PlaybackSettings getPlayback() {
        _assertTag(1);
        return (PlaybackSettings) this._value;
    }

    public void setPlayback(PlaybackSettings _value) {
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
                _aidl_parcel.writeTypedObject(getRecord(), _aidl_flag);
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getPlayback(), _aidl_flag);
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                RecordSettings _aidl_value = (RecordSettings) _aidl_parcel.readTypedObject(RecordSettings.CREATOR);
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                PlaybackSettings _aidl_value2 = (PlaybackSettings) _aidl_parcel.readTypedObject(PlaybackSettings.CREATOR);
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
                int _mask = 0 | describeContents(getRecord());
                return _mask;
            case 1:
                int _mask2 = 0 | describeContents(getPlayback());
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
                return "record";
            case 1:
                return "playback";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
