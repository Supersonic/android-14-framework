package android.media.audio.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class AudioPlaybackRate implements Parcelable {
    public static final Parcelable.Creator<AudioPlaybackRate> CREATOR = new Parcelable.Creator<AudioPlaybackRate>() { // from class: android.media.audio.common.AudioPlaybackRate.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPlaybackRate createFromParcel(Parcel _aidl_source) {
            AudioPlaybackRate _aidl_out = new AudioPlaybackRate();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPlaybackRate[] newArray(int _aidl_size) {
            return new AudioPlaybackRate[_aidl_size];
        }
    };
    public float speed = 0.0f;
    public float pitch = 0.0f;
    public int timestretchMode = 0;
    public int fallbackMode = 0;

    /* loaded from: classes2.dex */
    public @interface TimestretchFallbackMode {
        public static final int FAIL = 2;
        public static final int MUTE = 1;
        public static final int SYS_RESERVED_CUT_REPEAT = -1;
        public static final int SYS_RESERVED_DEFAULT = 0;
    }

    /* loaded from: classes2.dex */
    public @interface TimestretchMode {
        public static final int DEFAULT = 0;
        public static final int VOICE = 1;
    }

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeFloat(this.speed);
        _aidl_parcel.writeFloat(this.pitch);
        _aidl_parcel.writeInt(this.timestretchMode);
        _aidl_parcel.writeInt(this.fallbackMode);
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
            this.speed = _aidl_parcel.readFloat();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.pitch = _aidl_parcel.readFloat();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.timestretchMode = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.fallbackMode = _aidl_parcel.readInt();
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
        _aidl_sj.add("speed: " + this.speed);
        _aidl_sj.add("pitch: " + this.pitch);
        _aidl_sj.add("timestretchMode: " + this.timestretchMode);
        _aidl_sj.add("fallbackMode: " + this.fallbackMode);
        return "android.media.audio.common.AudioPlaybackRate" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioPlaybackRate)) {
            return false;
        }
        AudioPlaybackRate that = (AudioPlaybackRate) other;
        if (Objects.deepEquals(java.lang.Float.valueOf(this.speed), java.lang.Float.valueOf(that.speed)) && Objects.deepEquals(java.lang.Float.valueOf(this.pitch), java.lang.Float.valueOf(that.pitch)) && Objects.deepEquals(Integer.valueOf(this.timestretchMode), Integer.valueOf(that.timestretchMode)) && Objects.deepEquals(Integer.valueOf(this.fallbackMode), Integer.valueOf(that.fallbackMode))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(java.lang.Float.valueOf(this.speed), java.lang.Float.valueOf(this.pitch), Integer.valueOf(this.timestretchMode), Integer.valueOf(this.fallbackMode)).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
