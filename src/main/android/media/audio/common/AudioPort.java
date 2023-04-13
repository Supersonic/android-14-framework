package android.media.audio.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class AudioPort implements Parcelable {
    public static final Parcelable.Creator<AudioPort> CREATOR = new Parcelable.Creator<AudioPort>() { // from class: android.media.audio.common.AudioPort.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPort createFromParcel(Parcel _aidl_source) {
            AudioPort _aidl_out = new AudioPort();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPort[] newArray(int _aidl_size) {
            return new AudioPort[_aidl_size];
        }
    };
    public AudioPortExt ext;
    public ExtraAudioDescriptor[] extraAudioDescriptors;
    public AudioIoFlags flags;
    public AudioGain[] gains;

    /* renamed from: id */
    public int f288id = 0;
    public String name;
    public AudioProfile[] profiles;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.f288id);
        _aidl_parcel.writeString(this.name);
        _aidl_parcel.writeTypedArray(this.profiles, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.flags, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.extraAudioDescriptors, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.gains, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.ext, _aidl_flag);
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
            this.f288id = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.name = _aidl_parcel.readString();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.profiles = (AudioProfile[]) _aidl_parcel.createTypedArray(AudioProfile.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.flags = (AudioIoFlags) _aidl_parcel.readTypedObject(AudioIoFlags.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.extraAudioDescriptors = (ExtraAudioDescriptor[]) _aidl_parcel.createTypedArray(ExtraAudioDescriptor.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.gains = (AudioGain[]) _aidl_parcel.createTypedArray(AudioGain.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.ext = (AudioPortExt) _aidl_parcel.readTypedObject(AudioPortExt.CREATOR);
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
        _aidl_sj.add("id: " + this.f288id);
        _aidl_sj.add("name: " + Objects.toString(this.name));
        _aidl_sj.add("profiles: " + Arrays.toString(this.profiles));
        _aidl_sj.add("flags: " + Objects.toString(this.flags));
        _aidl_sj.add("extraAudioDescriptors: " + Arrays.toString(this.extraAudioDescriptors));
        _aidl_sj.add("gains: " + Arrays.toString(this.gains));
        _aidl_sj.add("ext: " + Objects.toString(this.ext));
        return "android.media.audio.common.AudioPort" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioPort)) {
            return false;
        }
        AudioPort that = (AudioPort) other;
        if (Objects.deepEquals(Integer.valueOf(this.f288id), Integer.valueOf(that.f288id)) && Objects.deepEquals(this.name, that.name) && Objects.deepEquals(this.profiles, that.profiles) && Objects.deepEquals(this.flags, that.flags) && Objects.deepEquals(this.extraAudioDescriptors, that.extraAudioDescriptors) && Objects.deepEquals(this.gains, that.gains) && Objects.deepEquals(this.ext, that.ext)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.f288id), this.name, this.profiles, this.flags, this.extraAudioDescriptors, this.gains, this.ext).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.profiles);
        return _mask | describeContents(this.flags) | describeContents(this.extraAudioDescriptors) | describeContents(this.gains) | describeContents(this.ext);
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
