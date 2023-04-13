package android.media.audio.common;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class AudioIoFlags implements Parcelable {
    public static final Parcelable.Creator<AudioIoFlags> CREATOR = new Parcelable.Creator<AudioIoFlags>() { // from class: android.media.audio.common.AudioIoFlags.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioIoFlags createFromParcel(Parcel _aidl_source) {
            return new AudioIoFlags(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioIoFlags[] newArray(int _aidl_size) {
            return new AudioIoFlags[_aidl_size];
        }
    };
    public static final int input = 0;
    public static final int output = 1;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int input = 0;
        public static final int output = 1;
    }

    public AudioIoFlags() {
        this._tag = 0;
        this._value = 0;
    }

    private AudioIoFlags(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private AudioIoFlags(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static AudioIoFlags input(int _value) {
        return new AudioIoFlags(0, Integer.valueOf(_value));
    }

    public int getInput() {
        _assertTag(0);
        return ((Integer) this._value).intValue();
    }

    public void setInput(int _value) {
        _set(0, Integer.valueOf(_value));
    }

    public static AudioIoFlags output(int _value) {
        return new AudioIoFlags(1, Integer.valueOf(_value));
    }

    public int getOutput() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setOutput(int _value) {
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
                _aidl_parcel.writeInt(getInput());
                return;
            case 1:
                _aidl_parcel.writeInt(getOutput());
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

    public String toString() {
        switch (this._tag) {
            case 0:
                return "android.media.audio.common.AudioIoFlags.input(" + getInput() + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.media.audio.common.AudioIoFlags.output(" + getOutput() + NavigationBarInflaterView.KEY_CODE_END;
            default:
                throw new IllegalStateException("unknown field: " + this._tag);
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioIoFlags)) {
            return false;
        }
        AudioIoFlags that = (AudioIoFlags) other;
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
                return "input";
            case 1:
                return "output";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
