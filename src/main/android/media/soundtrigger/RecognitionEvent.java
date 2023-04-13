package android.media.soundtrigger;

import android.media.audio.common.AudioConfig;
import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class RecognitionEvent implements Parcelable {
    public static final Parcelable.Creator<RecognitionEvent> CREATOR = new Parcelable.Creator<RecognitionEvent>() { // from class: android.media.soundtrigger.RecognitionEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RecognitionEvent createFromParcel(Parcel _aidl_source) {
            RecognitionEvent _aidl_out = new RecognitionEvent();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RecognitionEvent[] newArray(int _aidl_size) {
            return new RecognitionEvent[_aidl_size];
        }
    };
    public AudioConfig audioConfig;
    public byte[] data;
    public int status = -1;
    public int type = -1;
    public boolean captureAvailable = false;
    public int captureDelayMs = 0;
    public int capturePreambleMs = 0;
    public boolean triggerInData = false;
    public boolean recognitionStillActive = false;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.status);
        _aidl_parcel.writeInt(this.type);
        _aidl_parcel.writeBoolean(this.captureAvailable);
        _aidl_parcel.writeInt(this.captureDelayMs);
        _aidl_parcel.writeInt(this.capturePreambleMs);
        _aidl_parcel.writeBoolean(this.triggerInData);
        _aidl_parcel.writeTypedObject(this.audioConfig, _aidl_flag);
        _aidl_parcel.writeByteArray(this.data);
        _aidl_parcel.writeBoolean(this.recognitionStillActive);
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
            this.status = _aidl_parcel.readInt();
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
            this.captureAvailable = _aidl_parcel.readBoolean();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.captureDelayMs = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.capturePreambleMs = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.triggerInData = _aidl_parcel.readBoolean();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.audioConfig = (AudioConfig) _aidl_parcel.readTypedObject(AudioConfig.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.data = _aidl_parcel.createByteArray();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.recognitionStillActive = _aidl_parcel.readBoolean();
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
        _aidl_sj.add("status: " + this.status);
        _aidl_sj.add("type: " + this.type);
        _aidl_sj.add("captureAvailable: " + this.captureAvailable);
        _aidl_sj.add("captureDelayMs: " + this.captureDelayMs);
        _aidl_sj.add("capturePreambleMs: " + this.capturePreambleMs);
        _aidl_sj.add("triggerInData: " + this.triggerInData);
        _aidl_sj.add("audioConfig: " + Objects.toString(this.audioConfig));
        _aidl_sj.add("data: " + Arrays.toString(this.data));
        _aidl_sj.add("recognitionStillActive: " + this.recognitionStillActive);
        return "android.media.soundtrigger.RecognitionEvent" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof RecognitionEvent)) {
            return false;
        }
        RecognitionEvent that = (RecognitionEvent) other;
        if (Objects.deepEquals(Integer.valueOf(this.status), Integer.valueOf(that.status)) && Objects.deepEquals(Integer.valueOf(this.type), Integer.valueOf(that.type)) && Objects.deepEquals(Boolean.valueOf(this.captureAvailable), Boolean.valueOf(that.captureAvailable)) && Objects.deepEquals(Integer.valueOf(this.captureDelayMs), Integer.valueOf(that.captureDelayMs)) && Objects.deepEquals(Integer.valueOf(this.capturePreambleMs), Integer.valueOf(that.capturePreambleMs)) && Objects.deepEquals(Boolean.valueOf(this.triggerInData), Boolean.valueOf(that.triggerInData)) && Objects.deepEquals(this.audioConfig, that.audioConfig) && Objects.deepEquals(this.data, that.data) && Objects.deepEquals(Boolean.valueOf(this.recognitionStillActive), Boolean.valueOf(that.recognitionStillActive))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.status), Integer.valueOf(this.type), Boolean.valueOf(this.captureAvailable), Integer.valueOf(this.captureDelayMs), Integer.valueOf(this.capturePreambleMs), Boolean.valueOf(this.triggerInData), this.audioConfig, this.data, Boolean.valueOf(this.recognitionStillActive)).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.audioConfig);
        return _mask;
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
