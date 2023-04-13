package android.hardware.p005tv.tuner;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.AvStreamType */
/* loaded from: classes2.dex */
public final class AvStreamType implements Parcelable {
    public static final Parcelable.Creator<AvStreamType> CREATOR = new Parcelable.Creator<AvStreamType>() { // from class: android.hardware.tv.tuner.AvStreamType.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AvStreamType createFromParcel(Parcel _aidl_source) {
            return new AvStreamType(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AvStreamType[] newArray(int _aidl_size) {
            return new AvStreamType[_aidl_size];
        }
    };
    public static final int audio = 1;
    public static final int video = 0;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.AvStreamType$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int audio = 1;
        public static final int video = 0;
    }

    public AvStreamType() {
        this._tag = 0;
        this._value = 0;
    }

    private AvStreamType(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private AvStreamType(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static AvStreamType video(int _value) {
        return new AvStreamType(0, Integer.valueOf(_value));
    }

    public int getVideo() {
        _assertTag(0);
        return ((Integer) this._value).intValue();
    }

    public void setVideo(int _value) {
        _set(0, Integer.valueOf(_value));
    }

    public static AvStreamType audio(int _value) {
        return new AvStreamType(1, Integer.valueOf(_value));
    }

    public int getAudio() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setAudio(int _value) {
        _set(1, Integer.valueOf(_value));
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
                _aidl_parcel.writeInt(getVideo());
                return;
            case 1:
                _aidl_parcel.writeInt(getAudio());
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
                return "video";
            case 1:
                return "audio";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
