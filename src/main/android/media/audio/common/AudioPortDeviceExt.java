package android.media.audio.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class AudioPortDeviceExt implements Parcelable {
    public static final Parcelable.Creator<AudioPortDeviceExt> CREATOR = new Parcelable.Creator<AudioPortDeviceExt>() { // from class: android.media.audio.common.AudioPortDeviceExt.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPortDeviceExt createFromParcel(Parcel _aidl_source) {
            AudioPortDeviceExt _aidl_out = new AudioPortDeviceExt();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPortDeviceExt[] newArray(int _aidl_size) {
            return new AudioPortDeviceExt[_aidl_size];
        }
    };
    public static final int FLAG_INDEX_DEFAULT_DEVICE = 0;
    public AudioDevice device;
    public AudioFormatDescription[] encodedFormats;
    public int flags = 0;
    public int encapsulationModes = 0;
    public int encapsulationMetadataTypes = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeTypedObject(this.device, _aidl_flag);
        _aidl_parcel.writeInt(this.flags);
        _aidl_parcel.writeTypedArray(this.encodedFormats, _aidl_flag);
        _aidl_parcel.writeInt(this.encapsulationModes);
        _aidl_parcel.writeInt(this.encapsulationMetadataTypes);
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
            this.device = (AudioDevice) _aidl_parcel.readTypedObject(AudioDevice.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.flags = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.encodedFormats = (AudioFormatDescription[]) _aidl_parcel.createTypedArray(AudioFormatDescription.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.encapsulationModes = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.encapsulationMetadataTypes = _aidl_parcel.readInt();
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
        _aidl_sj.add("device: " + Objects.toString(this.device));
        _aidl_sj.add("flags: " + this.flags);
        _aidl_sj.add("encodedFormats: " + Arrays.toString(this.encodedFormats));
        _aidl_sj.add("encapsulationModes: " + this.encapsulationModes);
        _aidl_sj.add("encapsulationMetadataTypes: " + this.encapsulationMetadataTypes);
        return "android.media.audio.common.AudioPortDeviceExt" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioPortDeviceExt)) {
            return false;
        }
        AudioPortDeviceExt that = (AudioPortDeviceExt) other;
        if (Objects.deepEquals(this.device, that.device) && Objects.deepEquals(Integer.valueOf(this.flags), Integer.valueOf(that.flags)) && Objects.deepEquals(this.encodedFormats, that.encodedFormats) && Objects.deepEquals(Integer.valueOf(this.encapsulationModes), Integer.valueOf(that.encapsulationModes)) && Objects.deepEquals(Integer.valueOf(this.encapsulationMetadataTypes), Integer.valueOf(that.encapsulationMetadataTypes))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.device, Integer.valueOf(this.flags), this.encodedFormats, Integer.valueOf(this.encapsulationModes), Integer.valueOf(this.encapsulationMetadataTypes)).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.device);
        return _mask | describeContents(this.encodedFormats);
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
}
