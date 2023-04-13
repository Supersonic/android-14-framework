package android.media.audio.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class AudioPortMixExt implements Parcelable {
    public static final Parcelable.Creator<AudioPortMixExt> CREATOR = new Parcelable.Creator<AudioPortMixExt>() { // from class: android.media.audio.common.AudioPortMixExt.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPortMixExt createFromParcel(Parcel _aidl_source) {
            AudioPortMixExt _aidl_out = new AudioPortMixExt();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPortMixExt[] newArray(int _aidl_size) {
            return new AudioPortMixExt[_aidl_size];
        }
    };
    public AudioPortMixExtUseCase usecase;
    public int handle = 0;
    public int maxOpenStreamCount = 0;
    public int maxActiveStreamCount = 0;
    public int recommendedMuteDurationMs = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.handle);
        _aidl_parcel.writeTypedObject(this.usecase, _aidl_flag);
        _aidl_parcel.writeInt(this.maxOpenStreamCount);
        _aidl_parcel.writeInt(this.maxActiveStreamCount);
        _aidl_parcel.writeInt(this.recommendedMuteDurationMs);
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
            this.handle = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.usecase = (AudioPortMixExtUseCase) _aidl_parcel.readTypedObject(AudioPortMixExtUseCase.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.maxOpenStreamCount = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.maxActiveStreamCount = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.recommendedMuteDurationMs = _aidl_parcel.readInt();
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
        _aidl_sj.add("handle: " + this.handle);
        _aidl_sj.add("usecase: " + Objects.toString(this.usecase));
        _aidl_sj.add("maxOpenStreamCount: " + this.maxOpenStreamCount);
        _aidl_sj.add("maxActiveStreamCount: " + this.maxActiveStreamCount);
        _aidl_sj.add("recommendedMuteDurationMs: " + this.recommendedMuteDurationMs);
        return "android.media.audio.common.AudioPortMixExt" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioPortMixExt)) {
            return false;
        }
        AudioPortMixExt that = (AudioPortMixExt) other;
        if (Objects.deepEquals(Integer.valueOf(this.handle), Integer.valueOf(that.handle)) && Objects.deepEquals(this.usecase, that.usecase) && Objects.deepEquals(Integer.valueOf(this.maxOpenStreamCount), Integer.valueOf(that.maxOpenStreamCount)) && Objects.deepEquals(Integer.valueOf(this.maxActiveStreamCount), Integer.valueOf(that.maxActiveStreamCount)) && Objects.deepEquals(Integer.valueOf(this.recommendedMuteDurationMs), Integer.valueOf(that.recommendedMuteDurationMs))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.handle), this.usecase, Integer.valueOf(this.maxOpenStreamCount), Integer.valueOf(this.maxActiveStreamCount), Integer.valueOf(this.recommendedMuteDurationMs)).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.usecase);
        return _mask;
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
