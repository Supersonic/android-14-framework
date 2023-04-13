package android.media.audio.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class AudioGain implements Parcelable {
    public static final Parcelable.Creator<AudioGain> CREATOR = new Parcelable.Creator<AudioGain>() { // from class: android.media.audio.common.AudioGain.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioGain createFromParcel(Parcel _aidl_source) {
            AudioGain _aidl_out = new AudioGain();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioGain[] newArray(int _aidl_size) {
            return new AudioGain[_aidl_size];
        }
    };
    public AudioChannelLayout channelMask;
    public int mode = 0;
    public int minValue = 0;
    public int maxValue = 0;
    public int defaultValue = 0;
    public int stepValue = 0;
    public int minRampMs = 0;
    public int maxRampMs = 0;
    public boolean useForVolume = false;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.mode);
        _aidl_parcel.writeTypedObject(this.channelMask, _aidl_flag);
        _aidl_parcel.writeInt(this.minValue);
        _aidl_parcel.writeInt(this.maxValue);
        _aidl_parcel.writeInt(this.defaultValue);
        _aidl_parcel.writeInt(this.stepValue);
        _aidl_parcel.writeInt(this.minRampMs);
        _aidl_parcel.writeInt(this.maxRampMs);
        _aidl_parcel.writeBoolean(this.useForVolume);
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
            this.mode = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.channelMask = (AudioChannelLayout) _aidl_parcel.readTypedObject(AudioChannelLayout.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.minValue = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.maxValue = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.defaultValue = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.stepValue = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.minRampMs = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.maxRampMs = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.useForVolume = _aidl_parcel.readBoolean();
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
        _aidl_sj.add("mode: " + this.mode);
        _aidl_sj.add("channelMask: " + Objects.toString(this.channelMask));
        _aidl_sj.add("minValue: " + this.minValue);
        _aidl_sj.add("maxValue: " + this.maxValue);
        _aidl_sj.add("defaultValue: " + this.defaultValue);
        _aidl_sj.add("stepValue: " + this.stepValue);
        _aidl_sj.add("minRampMs: " + this.minRampMs);
        _aidl_sj.add("maxRampMs: " + this.maxRampMs);
        _aidl_sj.add("useForVolume: " + this.useForVolume);
        return "android.media.audio.common.AudioGain" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioGain)) {
            return false;
        }
        AudioGain that = (AudioGain) other;
        if (Objects.deepEquals(Integer.valueOf(this.mode), Integer.valueOf(that.mode)) && Objects.deepEquals(this.channelMask, that.channelMask) && Objects.deepEquals(Integer.valueOf(this.minValue), Integer.valueOf(that.minValue)) && Objects.deepEquals(Integer.valueOf(this.maxValue), Integer.valueOf(that.maxValue)) && Objects.deepEquals(Integer.valueOf(this.defaultValue), Integer.valueOf(that.defaultValue)) && Objects.deepEquals(Integer.valueOf(this.stepValue), Integer.valueOf(that.stepValue)) && Objects.deepEquals(Integer.valueOf(this.minRampMs), Integer.valueOf(that.minRampMs)) && Objects.deepEquals(Integer.valueOf(this.maxRampMs), Integer.valueOf(that.maxRampMs)) && Objects.deepEquals(java.lang.Boolean.valueOf(this.useForVolume), java.lang.Boolean.valueOf(that.useForVolume))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.mode), this.channelMask, Integer.valueOf(this.minValue), Integer.valueOf(this.maxValue), Integer.valueOf(this.defaultValue), Integer.valueOf(this.stepValue), Integer.valueOf(this.minRampMs), Integer.valueOf(this.maxRampMs), java.lang.Boolean.valueOf(this.useForVolume)).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.channelMask);
        return _mask;
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
