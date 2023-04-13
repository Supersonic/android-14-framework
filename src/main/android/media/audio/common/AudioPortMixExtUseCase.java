package android.media.audio.common;

import android.app.slice.Slice;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class AudioPortMixExtUseCase implements Parcelable {
    public static final Parcelable.Creator<AudioPortMixExtUseCase> CREATOR = new Parcelable.Creator<AudioPortMixExtUseCase>() { // from class: android.media.audio.common.AudioPortMixExtUseCase.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPortMixExtUseCase createFromParcel(Parcel _aidl_source) {
            return new AudioPortMixExtUseCase(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPortMixExtUseCase[] newArray(int _aidl_size) {
            return new AudioPortMixExtUseCase[_aidl_size];
        }
    };
    public static final int source = 2;
    public static final int stream = 1;
    public static final int unspecified = 0;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int source = 2;
        public static final int stream = 1;
        public static final int unspecified = 0;
    }

    public AudioPortMixExtUseCase() {
        this._tag = 0;
        this._value = false;
    }

    private AudioPortMixExtUseCase(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private AudioPortMixExtUseCase(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static AudioPortMixExtUseCase unspecified(boolean _value) {
        return new AudioPortMixExtUseCase(0, java.lang.Boolean.valueOf(_value));
    }

    public boolean getUnspecified() {
        _assertTag(0);
        return ((java.lang.Boolean) this._value).booleanValue();
    }

    public void setUnspecified(boolean _value) {
        _set(0, java.lang.Boolean.valueOf(_value));
    }

    public static AudioPortMixExtUseCase stream(int _value) {
        return new AudioPortMixExtUseCase(1, Integer.valueOf(_value));
    }

    public int getStream() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setStream(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static AudioPortMixExtUseCase source(int _value) {
        return new AudioPortMixExtUseCase(2, Integer.valueOf(_value));
    }

    public int getSource() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setSource(int _value) {
        _set(2, Integer.valueOf(_value));
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
                _aidl_parcel.writeBoolean(getUnspecified());
                return;
            case 1:
                _aidl_parcel.writeInt(getStream());
                return;
            case 2:
                _aidl_parcel.writeInt(getSource());
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
                _set(_aidl_tag, java.lang.Boolean.valueOf(_aidl_value));
                return;
            case 1:
                int _aidl_value2 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value2));
                return;
            case 2:
                int _aidl_value3 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value3));
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

    public String toString() {
        switch (this._tag) {
            case 0:
                return "android.media.audio.common.AudioPortMixExtUseCase.unspecified(" + getUnspecified() + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.media.audio.common.AudioPortMixExtUseCase.stream(" + getStream() + NavigationBarInflaterView.KEY_CODE_END;
            case 2:
                return "android.media.audio.common.AudioPortMixExtUseCase.source(" + getSource() + NavigationBarInflaterView.KEY_CODE_END;
            default:
                throw new IllegalStateException("unknown field: " + this._tag);
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioPortMixExtUseCase)) {
            return false;
        }
        AudioPortMixExtUseCase that = (AudioPortMixExtUseCase) other;
        if (this._tag == that._tag && Objects.deepEquals(this._value, that._value)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this._tag), this._value).toArray());
    }

    private void _assertTag(int tag) {
        if (getTag() != tag) {
            throw new IllegalStateException("bad access: " + _tagString(tag) + ", " + _tagString(getTag()) + " is available.");
        }
    }

    private String _tagString(int _tag) {
        switch (_tag) {
            case 0:
                return "unspecified";
            case 1:
                return "stream";
            case 2:
                return Slice.SUBTYPE_SOURCE;
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
