package android.media.audio.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class AudioOffloadInfo implements Parcelable {
    public static final Parcelable.Creator<AudioOffloadInfo> CREATOR = new Parcelable.Creator<AudioOffloadInfo>() { // from class: android.media.audio.common.AudioOffloadInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioOffloadInfo createFromParcel(Parcel _aidl_source) {
            AudioOffloadInfo _aidl_out = new AudioOffloadInfo();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioOffloadInfo[] newArray(int _aidl_size) {
            return new AudioOffloadInfo[_aidl_size];
        }
    };
    public AudioConfigBase base;
    public int streamType = -2;
    public int bitRatePerSecond = 0;
    public long durationUs = 0;
    public boolean hasVideo = false;
    public boolean isStreaming = false;
    public int bitWidth = 16;
    public int offloadBufferSize = 0;
    public int usage = -1;
    public byte encapsulationMode = -1;
    public int contentId = 0;
    public int syncId = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeTypedObject(this.base, _aidl_flag);
        _aidl_parcel.writeInt(this.streamType);
        _aidl_parcel.writeInt(this.bitRatePerSecond);
        _aidl_parcel.writeLong(this.durationUs);
        _aidl_parcel.writeBoolean(this.hasVideo);
        _aidl_parcel.writeBoolean(this.isStreaming);
        _aidl_parcel.writeInt(this.bitWidth);
        _aidl_parcel.writeInt(this.offloadBufferSize);
        _aidl_parcel.writeInt(this.usage);
        _aidl_parcel.writeByte(this.encapsulationMode);
        _aidl_parcel.writeInt(this.contentId);
        _aidl_parcel.writeInt(this.syncId);
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
            this.streamType = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.bitRatePerSecond = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.durationUs = _aidl_parcel.readLong();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.hasVideo = _aidl_parcel.readBoolean();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.isStreaming = _aidl_parcel.readBoolean();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.bitWidth = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.offloadBufferSize = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.usage = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.encapsulationMode = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.contentId = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.syncId = _aidl_parcel.readInt();
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
        _aidl_sj.add("streamType: " + this.streamType);
        _aidl_sj.add("bitRatePerSecond: " + this.bitRatePerSecond);
        _aidl_sj.add("durationUs: " + this.durationUs);
        _aidl_sj.add("hasVideo: " + this.hasVideo);
        _aidl_sj.add("isStreaming: " + this.isStreaming);
        _aidl_sj.add("bitWidth: " + this.bitWidth);
        _aidl_sj.add("offloadBufferSize: " + this.offloadBufferSize);
        _aidl_sj.add("usage: " + this.usage);
        _aidl_sj.add("encapsulationMode: " + ((int) this.encapsulationMode));
        _aidl_sj.add("contentId: " + this.contentId);
        _aidl_sj.add("syncId: " + this.syncId);
        return "android.media.audio.common.AudioOffloadInfo" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioOffloadInfo)) {
            return false;
        }
        AudioOffloadInfo that = (AudioOffloadInfo) other;
        if (Objects.deepEquals(this.base, that.base) && Objects.deepEquals(Integer.valueOf(this.streamType), Integer.valueOf(that.streamType)) && Objects.deepEquals(Integer.valueOf(this.bitRatePerSecond), Integer.valueOf(that.bitRatePerSecond)) && Objects.deepEquals(java.lang.Long.valueOf(this.durationUs), java.lang.Long.valueOf(that.durationUs)) && Objects.deepEquals(java.lang.Boolean.valueOf(this.hasVideo), java.lang.Boolean.valueOf(that.hasVideo)) && Objects.deepEquals(java.lang.Boolean.valueOf(this.isStreaming), java.lang.Boolean.valueOf(that.isStreaming)) && Objects.deepEquals(Integer.valueOf(this.bitWidth), Integer.valueOf(that.bitWidth)) && Objects.deepEquals(Integer.valueOf(this.offloadBufferSize), Integer.valueOf(that.offloadBufferSize)) && Objects.deepEquals(Integer.valueOf(this.usage), Integer.valueOf(that.usage)) && Objects.deepEquals(java.lang.Byte.valueOf(this.encapsulationMode), java.lang.Byte.valueOf(that.encapsulationMode)) && Objects.deepEquals(Integer.valueOf(this.contentId), Integer.valueOf(that.contentId)) && Objects.deepEquals(Integer.valueOf(this.syncId), Integer.valueOf(that.syncId))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.base, Integer.valueOf(this.streamType), Integer.valueOf(this.bitRatePerSecond), java.lang.Long.valueOf(this.durationUs), java.lang.Boolean.valueOf(this.hasVideo), java.lang.Boolean.valueOf(this.isStreaming), Integer.valueOf(this.bitWidth), Integer.valueOf(this.offloadBufferSize), Integer.valueOf(this.usage), java.lang.Byte.valueOf(this.encapsulationMode), Integer.valueOf(this.contentId), Integer.valueOf(this.syncId)).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.base);
        return _mask;
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
