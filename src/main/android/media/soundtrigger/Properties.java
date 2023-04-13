package android.media.soundtrigger;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class Properties implements Parcelable {
    public static final Parcelable.Creator<Properties> CREATOR = new Parcelable.Creator<Properties>() { // from class: android.media.soundtrigger.Properties.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Properties createFromParcel(Parcel _aidl_source) {
            Properties _aidl_out = new Properties();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Properties[] newArray(int _aidl_size) {
            return new Properties[_aidl_size];
        }
    };
    public String description;
    public String implementor;
    public String supportedModelArch;
    public String uuid;
    public int version = 0;
    public int maxSoundModels = 0;
    public int maxKeyPhrases = 0;
    public int maxUsers = 0;
    public int recognitionModes = 0;
    public boolean captureTransition = false;
    public int maxBufferMs = 0;
    public boolean concurrentCapture = false;
    public boolean triggerInEvent = false;
    public int powerConsumptionMw = 0;
    public int audioCapabilities = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeString(this.implementor);
        _aidl_parcel.writeString(this.description);
        _aidl_parcel.writeInt(this.version);
        _aidl_parcel.writeString(this.uuid);
        _aidl_parcel.writeString(this.supportedModelArch);
        _aidl_parcel.writeInt(this.maxSoundModels);
        _aidl_parcel.writeInt(this.maxKeyPhrases);
        _aidl_parcel.writeInt(this.maxUsers);
        _aidl_parcel.writeInt(this.recognitionModes);
        _aidl_parcel.writeBoolean(this.captureTransition);
        _aidl_parcel.writeInt(this.maxBufferMs);
        _aidl_parcel.writeBoolean(this.concurrentCapture);
        _aidl_parcel.writeBoolean(this.triggerInEvent);
        _aidl_parcel.writeInt(this.powerConsumptionMw);
        _aidl_parcel.writeInt(this.audioCapabilities);
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
            this.implementor = _aidl_parcel.readString();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.description = _aidl_parcel.readString();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.version = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.uuid = _aidl_parcel.readString();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.supportedModelArch = _aidl_parcel.readString();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.maxSoundModels = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.maxKeyPhrases = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.maxUsers = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.recognitionModes = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.captureTransition = _aidl_parcel.readBoolean();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.maxBufferMs = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.concurrentCapture = _aidl_parcel.readBoolean();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.triggerInEvent = _aidl_parcel.readBoolean();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.powerConsumptionMw = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.audioCapabilities = _aidl_parcel.readInt();
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
        _aidl_sj.add("implementor: " + Objects.toString(this.implementor));
        _aidl_sj.add("description: " + Objects.toString(this.description));
        _aidl_sj.add("version: " + this.version);
        _aidl_sj.add("uuid: " + Objects.toString(this.uuid));
        _aidl_sj.add("supportedModelArch: " + Objects.toString(this.supportedModelArch));
        _aidl_sj.add("maxSoundModels: " + this.maxSoundModels);
        _aidl_sj.add("maxKeyPhrases: " + this.maxKeyPhrases);
        _aidl_sj.add("maxUsers: " + this.maxUsers);
        _aidl_sj.add("recognitionModes: " + this.recognitionModes);
        _aidl_sj.add("captureTransition: " + this.captureTransition);
        _aidl_sj.add("maxBufferMs: " + this.maxBufferMs);
        _aidl_sj.add("concurrentCapture: " + this.concurrentCapture);
        _aidl_sj.add("triggerInEvent: " + this.triggerInEvent);
        _aidl_sj.add("powerConsumptionMw: " + this.powerConsumptionMw);
        _aidl_sj.add("audioCapabilities: " + this.audioCapabilities);
        return "android.media.soundtrigger.Properties" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof Properties)) {
            return false;
        }
        Properties that = (Properties) other;
        if (Objects.deepEquals(this.implementor, that.implementor) && Objects.deepEquals(this.description, that.description) && Objects.deepEquals(Integer.valueOf(this.version), Integer.valueOf(that.version)) && Objects.deepEquals(this.uuid, that.uuid) && Objects.deepEquals(this.supportedModelArch, that.supportedModelArch) && Objects.deepEquals(Integer.valueOf(this.maxSoundModels), Integer.valueOf(that.maxSoundModels)) && Objects.deepEquals(Integer.valueOf(this.maxKeyPhrases), Integer.valueOf(that.maxKeyPhrases)) && Objects.deepEquals(Integer.valueOf(this.maxUsers), Integer.valueOf(that.maxUsers)) && Objects.deepEquals(Integer.valueOf(this.recognitionModes), Integer.valueOf(that.recognitionModes)) && Objects.deepEquals(Boolean.valueOf(this.captureTransition), Boolean.valueOf(that.captureTransition)) && Objects.deepEquals(Integer.valueOf(this.maxBufferMs), Integer.valueOf(that.maxBufferMs)) && Objects.deepEquals(Boolean.valueOf(this.concurrentCapture), Boolean.valueOf(that.concurrentCapture)) && Objects.deepEquals(Boolean.valueOf(this.triggerInEvent), Boolean.valueOf(that.triggerInEvent)) && Objects.deepEquals(Integer.valueOf(this.powerConsumptionMw), Integer.valueOf(that.powerConsumptionMw)) && Objects.deepEquals(Integer.valueOf(this.audioCapabilities), Integer.valueOf(that.audioCapabilities))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.implementor, this.description, Integer.valueOf(this.version), this.uuid, this.supportedModelArch, Integer.valueOf(this.maxSoundModels), Integer.valueOf(this.maxKeyPhrases), Integer.valueOf(this.maxUsers), Integer.valueOf(this.recognitionModes), Boolean.valueOf(this.captureTransition), Integer.valueOf(this.maxBufferMs), Boolean.valueOf(this.concurrentCapture), Boolean.valueOf(this.triggerInEvent), Integer.valueOf(this.powerConsumptionMw), Integer.valueOf(this.audioCapabilities)).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
