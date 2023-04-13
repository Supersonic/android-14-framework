package android.media.audio.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class AudioHalEngineConfig implements Parcelable {
    public static final Parcelable.Creator<AudioHalEngineConfig> CREATOR = new Parcelable.Creator<AudioHalEngineConfig>() { // from class: android.media.audio.common.AudioHalEngineConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioHalEngineConfig createFromParcel(Parcel _aidl_source) {
            AudioHalEngineConfig _aidl_out = new AudioHalEngineConfig();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioHalEngineConfig[] newArray(int _aidl_size) {
            return new AudioHalEngineConfig[_aidl_size];
        }
    };
    public CapSpecificConfig capSpecificConfig;
    public int defaultProductStrategyId = -1;
    public AudioHalProductStrategy[] productStrategies;
    public AudioHalVolumeGroup[] volumeGroups;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.defaultProductStrategyId);
        _aidl_parcel.writeTypedArray(this.productStrategies, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.volumeGroups, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.capSpecificConfig, _aidl_flag);
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
            this.defaultProductStrategyId = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.productStrategies = (AudioHalProductStrategy[]) _aidl_parcel.createTypedArray(AudioHalProductStrategy.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.volumeGroups = (AudioHalVolumeGroup[]) _aidl_parcel.createTypedArray(AudioHalVolumeGroup.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.capSpecificConfig = (CapSpecificConfig) _aidl_parcel.readTypedObject(CapSpecificConfig.CREATOR);
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
        _aidl_sj.add("defaultProductStrategyId: " + this.defaultProductStrategyId);
        _aidl_sj.add("productStrategies: " + Arrays.toString(this.productStrategies));
        _aidl_sj.add("volumeGroups: " + Arrays.toString(this.volumeGroups));
        _aidl_sj.add("capSpecificConfig: " + Objects.toString(this.capSpecificConfig));
        return "android.media.audio.common.AudioHalEngineConfig" + _aidl_sj.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof AudioHalEngineConfig)) {
            return false;
        }
        AudioHalEngineConfig that = (AudioHalEngineConfig) other;
        if (Objects.deepEquals(Integer.valueOf(this.defaultProductStrategyId), Integer.valueOf(that.defaultProductStrategyId)) && Objects.deepEquals(this.productStrategies, that.productStrategies) && Objects.deepEquals(this.volumeGroups, that.volumeGroups) && Objects.deepEquals(this.capSpecificConfig, that.capSpecificConfig)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.defaultProductStrategyId), this.productStrategies, this.volumeGroups, this.capSpecificConfig).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.productStrategies);
        return _mask | describeContents(this.volumeGroups) | describeContents(this.capSpecificConfig);
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

    /* loaded from: classes2.dex */
    public static class CapSpecificConfig implements Parcelable {
        public static final Parcelable.Creator<CapSpecificConfig> CREATOR = new Parcelable.Creator<CapSpecificConfig>() { // from class: android.media.audio.common.AudioHalEngineConfig.CapSpecificConfig.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public CapSpecificConfig createFromParcel(Parcel _aidl_source) {
                CapSpecificConfig _aidl_out = new CapSpecificConfig();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public CapSpecificConfig[] newArray(int _aidl_size) {
                return new CapSpecificConfig[_aidl_size];
            }
        };
        public AudioHalCapCriterion[] criteria;
        public AudioHalCapCriterionType[] criterionTypes;

        @Override // android.p008os.Parcelable
        public final int getStability() {
            return 1;
        }

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeTypedArray(this.criteria, _aidl_flag);
            _aidl_parcel.writeTypedArray(this.criterionTypes, _aidl_flag);
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
                this.criteria = (AudioHalCapCriterion[]) _aidl_parcel.createTypedArray(AudioHalCapCriterion.CREATOR);
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.criterionTypes = (AudioHalCapCriterionType[]) _aidl_parcel.createTypedArray(AudioHalCapCriterionType.CREATOR);
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

        @Override // android.p008os.Parcelable
        public int describeContents() {
            int _mask = 0 | describeContents(this.criteria);
            return _mask | describeContents(this.criterionTypes);
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
}
