package android.hardware.biometrics.fingerprint;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public class PointerContext implements Parcelable {
    public static final Parcelable.Creator<PointerContext> CREATOR = new Parcelable.Creator<PointerContext>() { // from class: android.hardware.biometrics.fingerprint.PointerContext.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PointerContext createFromParcel(Parcel _aidl_source) {
            PointerContext _aidl_out = new PointerContext();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PointerContext[] newArray(int _aidl_size) {
            return new PointerContext[_aidl_size];
        }
    };
    public int pointerId = -1;

    /* renamed from: x */
    public float f99x = 0.0f;

    /* renamed from: y */
    public float f100y = 0.0f;
    public float minor = 0.0f;
    public float major = 0.0f;
    public float orientation = 0.0f;
    public boolean isAod = false;
    public long time = 0;
    public long gestureStart = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.pointerId);
        _aidl_parcel.writeFloat(this.f99x);
        _aidl_parcel.writeFloat(this.f100y);
        _aidl_parcel.writeFloat(this.minor);
        _aidl_parcel.writeFloat(this.major);
        _aidl_parcel.writeFloat(this.orientation);
        _aidl_parcel.writeBoolean(this.isAod);
        _aidl_parcel.writeLong(this.time);
        _aidl_parcel.writeLong(this.gestureStart);
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
            this.pointerId = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.f99x = _aidl_parcel.readFloat();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.f100y = _aidl_parcel.readFloat();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.minor = _aidl_parcel.readFloat();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.major = _aidl_parcel.readFloat();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.orientation = _aidl_parcel.readFloat();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.isAod = _aidl_parcel.readBoolean();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.time = _aidl_parcel.readLong();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.gestureStart = _aidl_parcel.readLong();
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

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof PointerContext)) {
            return false;
        }
        PointerContext that = (PointerContext) other;
        if (Objects.deepEquals(Integer.valueOf(this.pointerId), Integer.valueOf(that.pointerId)) && Objects.deepEquals(Float.valueOf(this.f99x), Float.valueOf(that.f99x)) && Objects.deepEquals(Float.valueOf(this.f100y), Float.valueOf(that.f100y)) && Objects.deepEquals(Float.valueOf(this.minor), Float.valueOf(that.minor)) && Objects.deepEquals(Float.valueOf(this.major), Float.valueOf(that.major)) && Objects.deepEquals(Float.valueOf(this.orientation), Float.valueOf(that.orientation)) && Objects.deepEquals(Boolean.valueOf(this.isAod), Boolean.valueOf(that.isAod)) && Objects.deepEquals(Long.valueOf(this.time), Long.valueOf(that.time)) && Objects.deepEquals(Long.valueOf(this.gestureStart), Long.valueOf(that.gestureStart))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.pointerId), Float.valueOf(this.f99x), Float.valueOf(this.f100y), Float.valueOf(this.minor), Float.valueOf(this.major), Float.valueOf(this.orientation), Boolean.valueOf(this.isAod), Long.valueOf(this.time), Long.valueOf(this.gestureStart)).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
