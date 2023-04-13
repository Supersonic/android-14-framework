package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.DemuxFilterMediaEventExtraMetaData */
/* loaded from: classes2.dex */
public final class DemuxFilterMediaEventExtraMetaData implements Parcelable {
    public static final Parcelable.Creator<DemuxFilterMediaEventExtraMetaData> CREATOR = new Parcelable.Creator<DemuxFilterMediaEventExtraMetaData>() { // from class: android.hardware.tv.tuner.DemuxFilterMediaEventExtraMetaData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterMediaEventExtraMetaData createFromParcel(Parcel _aidl_source) {
            return new DemuxFilterMediaEventExtraMetaData(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DemuxFilterMediaEventExtraMetaData[] newArray(int _aidl_size) {
            return new DemuxFilterMediaEventExtraMetaData[_aidl_size];
        }
    };
    public static final int audio = 1;
    public static final int audioPresentations = 2;
    public static final int noinit = 0;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.DemuxFilterMediaEventExtraMetaData$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int audio = 1;
        public static final int audioPresentations = 2;
        public static final int noinit = 0;
    }

    public DemuxFilterMediaEventExtraMetaData() {
        this._tag = 0;
        this._value = false;
    }

    private DemuxFilterMediaEventExtraMetaData(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DemuxFilterMediaEventExtraMetaData(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static DemuxFilterMediaEventExtraMetaData noinit(boolean _value) {
        return new DemuxFilterMediaEventExtraMetaData(0, Boolean.valueOf(_value));
    }

    public boolean getNoinit() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setNoinit(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static DemuxFilterMediaEventExtraMetaData audio(AudioExtraMetaData _value) {
        return new DemuxFilterMediaEventExtraMetaData(1, _value);
    }

    public AudioExtraMetaData getAudio() {
        _assertTag(1);
        return (AudioExtraMetaData) this._value;
    }

    public void setAudio(AudioExtraMetaData _value) {
        _set(1, _value);
    }

    public static DemuxFilterMediaEventExtraMetaData audioPresentations(AudioPresentation[] _value) {
        return new DemuxFilterMediaEventExtraMetaData(2, _value);
    }

    public AudioPresentation[] getAudioPresentations() {
        _assertTag(2);
        return (AudioPresentation[]) this._value;
    }

    public void setAudioPresentations(AudioPresentation[] _value) {
        _set(2, _value);
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
                _aidl_parcel.writeTypedObject(getAudio(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedArray(getAudioPresentations(), _aidl_flag);
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
                AudioExtraMetaData _aidl_value2 = (AudioExtraMetaData) _aidl_parcel.readTypedObject(AudioExtraMetaData.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                AudioPresentation[] _aidl_value3 = (AudioPresentation[]) _aidl_parcel.createTypedArray(AudioPresentation.CREATOR);
                _set(_aidl_tag, _aidl_value3);
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 1:
                int _mask = 0 | describeContents(getAudio());
                return _mask;
            case 2:
                int _mask2 = 0 | describeContents(getAudioPresentations());
                return _mask2;
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
                return "noinit";
            case 1:
                return "audio";
            case 2:
                return "audioPresentations";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
