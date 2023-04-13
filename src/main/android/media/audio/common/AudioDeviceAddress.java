package android.media.audio.common;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class AudioDeviceAddress implements Parcelable {
    public static final Parcelable.Creator<AudioDeviceAddress> CREATOR = new Parcelable.Creator<AudioDeviceAddress>() { // from class: android.media.audio.common.AudioDeviceAddress.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioDeviceAddress createFromParcel(Parcel _aidl_source) {
            return new AudioDeviceAddress(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioDeviceAddress[] newArray(int _aidl_size) {
            return new AudioDeviceAddress[_aidl_size];
        }
    };
    public static final int alsa = 4;

    /* renamed from: id */
    public static final int f282id = 0;
    public static final int ipv4 = 2;
    public static final int ipv6 = 3;
    public static final int mac = 1;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int alsa = 4;

        /* renamed from: id */
        public static final int f283id = 0;
        public static final int ipv4 = 2;
        public static final int ipv6 = 3;
        public static final int mac = 1;
    }

    public AudioDeviceAddress() {
        this._tag = 0;
        this._value = null;
    }

    private AudioDeviceAddress(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private AudioDeviceAddress(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    /* renamed from: id */
    public static AudioDeviceAddress m147id(String _value) {
        return new AudioDeviceAddress(0, _value);
    }

    public String getId() {
        _assertTag(0);
        return (String) this._value;
    }

    public void setId(String _value) {
        _set(0, _value);
    }

    public static AudioDeviceAddress mac(byte[] _value) {
        return new AudioDeviceAddress(1, _value);
    }

    public byte[] getMac() {
        _assertTag(1);
        return (byte[]) this._value;
    }

    public void setMac(byte[] _value) {
        _set(1, _value);
    }

    public static AudioDeviceAddress ipv4(byte[] _value) {
        return new AudioDeviceAddress(2, _value);
    }

    public byte[] getIpv4() {
        _assertTag(2);
        return (byte[]) this._value;
    }

    public void setIpv4(byte[] _value) {
        _set(2, _value);
    }

    public static AudioDeviceAddress ipv6(int[] _value) {
        return new AudioDeviceAddress(3, _value);
    }

    public int[] getIpv6() {
        _assertTag(3);
        return (int[]) this._value;
    }

    public void setIpv6(int[] _value) {
        _set(3, _value);
    }

    public static AudioDeviceAddress alsa(int[] _value) {
        return new AudioDeviceAddress(4, _value);
    }

    public int[] getAlsa() {
        _assertTag(4);
        return (int[]) this._value;
    }

    public void setAlsa(int[] _value) {
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
                _aidl_parcel.writeString(getId());
                return;
            case 1:
                _aidl_parcel.writeByteArray(getMac());
                return;
            case 2:
                _aidl_parcel.writeByteArray(getIpv4());
                return;
            case 3:
                _aidl_parcel.writeIntArray(getIpv6());
                return;
            case 4:
                _aidl_parcel.writeIntArray(getAlsa());
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                String _aidl_value = _aidl_parcel.readString();
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                byte[] _aidl_value2 = _aidl_parcel.createByteArray();
                _set(_aidl_tag, _aidl_value2);
                return;
            case 2:
                byte[] _aidl_value3 = _aidl_parcel.createByteArray();
                _set(_aidl_tag, _aidl_value3);
                return;
            case 3:
                int[] _aidl_value4 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value4);
                return;
            case 4:
                int[] _aidl_value5 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value5);
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
                return "android.media.audio.common.AudioDeviceAddress.id(" + Objects.toString(getId()) + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "android.media.audio.common.AudioDeviceAddress.mac(" + Arrays.toString(getMac()) + NavigationBarInflaterView.KEY_CODE_END;
            case 2:
                return "android.media.audio.common.AudioDeviceAddress.ipv4(" + Arrays.toString(getIpv4()) + NavigationBarInflaterView.KEY_CODE_END;
            case 3:
                return "android.media.audio.common.AudioDeviceAddress.ipv6(" + Arrays.toString(getIpv6()) + NavigationBarInflaterView.KEY_CODE_END;
            case 4:
                return "android.media.audio.common.AudioDeviceAddress.alsa(" + Arrays.toString(getAlsa()) + NavigationBarInflaterView.KEY_CODE_END;
            default:
                throw new IllegalStateException("unknown field: " + this._tag);
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioDeviceAddress)) {
            return false;
        }
        AudioDeviceAddress that = (AudioDeviceAddress) other;
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
                return "id";
            case 1:
                return "mac";
            case 2:
                return "ipv4";
            case 3:
                return "ipv6";
            case 4:
                return "alsa";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
