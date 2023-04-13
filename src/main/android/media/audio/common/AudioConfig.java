package android.media.audio.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class AudioConfig implements Parcelable {
    public static final Parcelable.Creator<AudioConfig> CREATOR = new Parcelable.Creator<AudioConfig>() { // from class: android.media.audio.common.AudioConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioConfig createFromParcel(Parcel _aidl_source) {
            AudioConfig _aidl_out = new AudioConfig();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioConfig[] newArray(int _aidl_size) {
            return new AudioConfig[_aidl_size];
        }
    };
    public AudioConfigBase base;
    public long frameCount = 0;
    public AudioOffloadInfo offloadInfo;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeTypedObject(this.base, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.offloadInfo, _aidl_flag);
        _aidl_parcel.writeLong(this.frameCount);
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
            this.base = (AudioConfigBase) _aidl_parcel.readTypedObject(AudioConfigBase.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.offloadInfo = (AudioOffloadInfo) _aidl_parcel.readTypedObject(AudioOffloadInfo.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.frameCount = _aidl_parcel.readLong();
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
        _aidl_sj.add("base: " + Objects.toString(this.base));
        _aidl_sj.add("offloadInfo: " + Objects.toString(this.offloadInfo));
        _aidl_sj.add("frameCount: " + this.frameCount);
        return "android.media.audio.common.AudioConfig" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioConfig)) {
            return false;
        }
        AudioConfig that = (AudioConfig) other;
        if (Objects.deepEquals(this.base, that.base) && Objects.deepEquals(this.offloadInfo, that.offloadInfo) && Objects.deepEquals(java.lang.Long.valueOf(this.frameCount), java.lang.Long.valueOf(that.frameCount))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.base, this.offloadInfo, java.lang.Long.valueOf(this.frameCount)).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.base);
        return _mask | describeContents(this.offloadInfo);
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
