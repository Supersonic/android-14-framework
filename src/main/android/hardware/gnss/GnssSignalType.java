package android.hardware.gnss;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class GnssSignalType implements Parcelable {
    public static final String CODE_TYPE_A = "A";
    public static final String CODE_TYPE_B = "B";
    public static final String CODE_TYPE_C = "C";
    public static final String CODE_TYPE_D = "D";
    public static final String CODE_TYPE_I = "I";
    public static final String CODE_TYPE_L = "L";
    public static final String CODE_TYPE_M = "M";
    public static final String CODE_TYPE_N = "N";
    public static final String CODE_TYPE_P = "P";
    public static final String CODE_TYPE_Q = "Q";
    public static final String CODE_TYPE_S = "S";
    public static final String CODE_TYPE_UNKNOWN = "UNKNOWN";
    public static final String CODE_TYPE_W = "W";
    public static final String CODE_TYPE_X = "X";
    public static final String CODE_TYPE_Y = "Y";
    public static final String CODE_TYPE_Z = "Z";
    public static final Parcelable.Creator<GnssSignalType> CREATOR = new Parcelable.Creator<GnssSignalType>() { // from class: android.hardware.gnss.GnssSignalType.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssSignalType createFromParcel(Parcel _aidl_source) {
            GnssSignalType _aidl_out = new GnssSignalType();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssSignalType[] newArray(int _aidl_size) {
            return new GnssSignalType[_aidl_size];
        }
    };
    public String codeType;
    public int constellation = 0;
    public double carrierFrequencyHz = 0.0d;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.constellation);
        _aidl_parcel.writeDouble(this.carrierFrequencyHz);
        _aidl_parcel.writeString(this.codeType);
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
            this.constellation = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.carrierFrequencyHz = _aidl_parcel.readDouble();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.codeType = _aidl_parcel.readString();
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
        return 0;
    }
}
