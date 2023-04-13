package android.media.audio.common;

import android.hardware.usb.UsbManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class AudioPortExt implements Parcelable {
    public static final Parcelable.Creator<AudioPortExt> CREATOR = new Parcelable.Creator<AudioPortExt>() { // from class: android.media.audio.common.AudioPortExt.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPortExt createFromParcel(Parcel _aidl_source) {
            return new AudioPortExt(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPortExt[] newArray(int _aidl_size) {
            return new AudioPortExt[_aidl_size];
        }
    };
    public static final int device = 1;
    public static final int mix = 2;
    public static final int session = 3;
    public static final int unspecified = 0;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int device = 1;
        public static final int mix = 2;
        public static final int session = 3;
        public static final int unspecified = 0;
    }

    public AudioPortExt() {
        this._tag = 0;
        this._value = false;
    }

    private AudioPortExt(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private AudioPortExt(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static AudioPortExt unspecified(boolean _value) {
        return new AudioPortExt(0, java.lang.Boolean.valueOf(_value));
    }

    public boolean getUnspecified() {
        _assertTag(0);
        return ((java.lang.Boolean) this._value).booleanValue();
    }

    public void setUnspecified(boolean _value) {
        _set(0, java.lang.Boolean.valueOf(_value));
    }

    public static AudioPortExt device(AudioPortDeviceExt _value) {
        return new AudioPortExt(1, _value);
    }

    public AudioPortDeviceExt getDevice() {
        _assertTag(1);
        return (AudioPortDeviceExt) this._value;
    }

    public void setDevice(AudioPortDeviceExt _value) {
        _set(1, _value);
    }

    public static AudioPortExt mix(AudioPortMixExt _value) {
        return new AudioPortExt(2, _value);
    }

    public AudioPortMixExt getMix() {
        _assertTag(2);
        return (AudioPortMixExt) this._value;
    }

    public void setMix(AudioPortMixExt _value) {
        _set(2, _value);
    }

    public static AudioPortExt session(int _value) {
        return new AudioPortExt(3, Integer.valueOf(_value));
    }

    public int getSession() {
        _assertTag(3);
        return ((Integer) this._value).intValue();
    }

    public void setSession(int _value) {
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
                _aidl_parcel.writeBoolean(getUnspecified());
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getDevice(), _aidl_flag);
                return;
            case 2:
                _aidl_parcel.writeTypedObject(getMix(), _aidl_flag);
                return;
            case 3:
                _aidl_parcel.writeInt(getSession());
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
                AudioPortDeviceExt _aidl_value2 = (AudioPortDeviceExt) _aidl_parcel.readTypedObject(AudioPortDeviceExt.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                AudioPortMixExt _aidl_value3 = (AudioPortMixExt) _aidl_parcel.readTypedObject(AudioPortMixExt.CREATOR);
                _set(_aidl_tag, _aidl_value3);
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
        switch (getTag()) {
            case 1:
                int _mask = 0 | describeContents(getDevice());
                return _mask;
            case 2:
                int _mask2 = 0 | describeContents(getMix());
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

    public String toString() {
        switch (this._tag) {
            case 0:
                return "android.media.audio.common.AudioPortExt.unspecified(" + getUnspecified() + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.media.audio.common.AudioPortExt.device(" + Objects.toString(getDevice()) + NavigationBarInflaterView.KEY_CODE_END;
            case 2:
                return "android.media.audio.common.AudioPortExt.mix(" + Objects.toString(getMix()) + NavigationBarInflaterView.KEY_CODE_END;
            case 3:
                return "android.media.audio.common.AudioPortExt.session(" + getSession() + NavigationBarInflaterView.KEY_CODE_END;
            default:
                throw new IllegalStateException("unknown field: " + this._tag);
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioPortExt)) {
            return false;
        }
        AudioPortExt that = (AudioPortExt) other;
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
                return UsbManager.EXTRA_DEVICE;
            case 2:
                return "mix";
            case 3:
                return "session";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
