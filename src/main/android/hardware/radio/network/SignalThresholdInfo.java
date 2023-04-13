package android.hardware.radio.network;

import android.hardware.radio.AccessNetwork$$;
import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class SignalThresholdInfo implements Parcelable {
    public static final Parcelable.Creator<SignalThresholdInfo> CREATOR = new Parcelable.Creator<SignalThresholdInfo>() { // from class: android.hardware.radio.network.SignalThresholdInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SignalThresholdInfo createFromParcel(Parcel _aidl_source) {
            SignalThresholdInfo _aidl_out = new SignalThresholdInfo();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SignalThresholdInfo[] newArray(int _aidl_size) {
            return new SignalThresholdInfo[_aidl_size];
        }
    };
    public static final int SIGNAL_MEASUREMENT_TYPE_ECNO = 9;
    public static final int SIGNAL_MEASUREMENT_TYPE_RSCP = 2;
    public static final int SIGNAL_MEASUREMENT_TYPE_RSRP = 3;
    public static final int SIGNAL_MEASUREMENT_TYPE_RSRQ = 4;
    public static final int SIGNAL_MEASUREMENT_TYPE_RSSI = 1;
    public static final int SIGNAL_MEASUREMENT_TYPE_RSSNR = 5;
    public static final int SIGNAL_MEASUREMENT_TYPE_SSRSRP = 6;
    public static final int SIGNAL_MEASUREMENT_TYPE_SSRSRQ = 7;
    public static final int SIGNAL_MEASUREMENT_TYPE_SSSINR = 8;
    public int ran;
    public int[] thresholds;
    public int signalMeasurement = 0;
    public int hysteresisMs = 0;
    public int hysteresisDb = 0;
    public boolean isEnabled = false;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.signalMeasurement);
        _aidl_parcel.writeInt(this.hysteresisMs);
        _aidl_parcel.writeInt(this.hysteresisDb);
        _aidl_parcel.writeIntArray(this.thresholds);
        _aidl_parcel.writeBoolean(this.isEnabled);
        _aidl_parcel.writeInt(this.ran);
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
            this.signalMeasurement = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.hysteresisMs = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.hysteresisDb = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.thresholds = _aidl_parcel.createIntArray();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.isEnabled = _aidl_parcel.readBoolean();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.ran = _aidl_parcel.readInt();
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
        _aidl_sj.add("signalMeasurement: " + this.signalMeasurement);
        _aidl_sj.add("hysteresisMs: " + this.hysteresisMs);
        _aidl_sj.add("hysteresisDb: " + this.hysteresisDb);
        _aidl_sj.add("thresholds: " + Arrays.toString(this.thresholds));
        _aidl_sj.add("isEnabled: " + this.isEnabled);
        _aidl_sj.add("ran: " + AccessNetwork$$.toString(this.ran));
        return "android.hardware.radio.network.SignalThresholdInfo" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
