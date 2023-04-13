package android.media.audio.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class AudioDeviceDescription implements Parcelable {
    public static final String CONNECTION_ANALOG = "analog";
    public static final String CONNECTION_BT_A2DP = "bt-a2dp";
    public static final String CONNECTION_BT_LE = "bt-le";
    public static final String CONNECTION_BT_SCO = "bt-sco";
    public static final String CONNECTION_BUS = "bus";
    public static final String CONNECTION_HDMI = "hdmi";
    public static final String CONNECTION_HDMI_ARC = "hdmi-arc";
    public static final String CONNECTION_HDMI_EARC = "hdmi-earc";
    public static final String CONNECTION_IP_V4 = "ip-v4";
    public static final String CONNECTION_SPDIF = "spdif";
    public static final String CONNECTION_USB = "usb";
    public static final String CONNECTION_VIRTUAL = "virtual";
    public static final String CONNECTION_WIRELESS = "wireless";
    public static final Parcelable.Creator<AudioDeviceDescription> CREATOR = new Parcelable.Creator<AudioDeviceDescription>() { // from class: android.media.audio.common.AudioDeviceDescription.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioDeviceDescription createFromParcel(Parcel _aidl_source) {
            AudioDeviceDescription _aidl_out = new AudioDeviceDescription();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioDeviceDescription[] newArray(int _aidl_size) {
            return new AudioDeviceDescription[_aidl_size];
        }
    };
    public String connection;
    public int type = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.type);
        _aidl_parcel.writeString(this.connection);
        int _aidl_end_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.setDataPosition(_aidl_start_pos);
        _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
        _aidl_parcel.setDataPosition(_aidl_end_pos);
    }

    public final void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        int _aidl_parcelable_size = _aidl_parcel.readInt();
        try {
            if (_aidl_parcelable_size < 4) {
                throw new BadParcelableException("Parcelable too small");
            }
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.type = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.connection = _aidl_parcel.readString();
            if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
        } catch (Throwable th) {
            if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
            throw th;
        }
    }

    public String toString() {
        StringJoiner _aidl_sj = new StringJoiner(", ", "{", "}");
        _aidl_sj.add("type: " + this.type);
        _aidl_sj.add("connection: " + Objects.toString(this.connection));
        return "android.media.audio.common.AudioDeviceDescription" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioDeviceDescription)) {
            return false;
        }
        AudioDeviceDescription that = (AudioDeviceDescription) other;
        if (Objects.deepEquals(Integer.valueOf(this.type), Integer.valueOf(that.type)) && Objects.deepEquals(this.connection, that.connection)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.type), this.connection).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
