package android.hardware.radio.ims;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class ConnectionFailureInfo implements Parcelable {
    public static final Parcelable.Creator<ConnectionFailureInfo> CREATOR = new Parcelable.Creator<ConnectionFailureInfo>() { // from class: android.hardware.radio.ims.ConnectionFailureInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConnectionFailureInfo createFromParcel(Parcel _aidl_source) {
            ConnectionFailureInfo _aidl_out = new ConnectionFailureInfo();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConnectionFailureInfo[] newArray(int _aidl_size) {
            return new ConnectionFailureInfo[_aidl_size];
        }
    };
    public int failureReason;
    public int causeCode = 0;
    public int waitTimeMillis = 0;

    /* loaded from: classes2.dex */
    public @interface ConnectionFailureReason {
        public static final int REASON_ACCESS_DENIED = 1;
        public static final int REASON_NAS_FAILURE = 2;
        public static final int REASON_NO_SERVICE = 7;
        public static final int REASON_PDN_NOT_AVAILABLE = 8;
        public static final int REASON_RACH_FAILURE = 3;
        public static final int REASON_RF_BUSY = 9;
        public static final int REASON_RLC_FAILURE = 4;
        public static final int REASON_RRC_REJECT = 5;
        public static final int REASON_RRC_TIMEOUT = 6;
        public static final int REASON_UNSPECIFIED = 65535;
    }

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.failureReason);
        _aidl_parcel.writeInt(this.causeCode);
        _aidl_parcel.writeInt(this.waitTimeMillis);
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
            this.failureReason = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.causeCode = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.waitTimeMillis = _aidl_parcel.readInt();
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
        _aidl_sj.add("failureReason: " + this.failureReason);
        _aidl_sj.add("causeCode: " + this.causeCode);
        _aidl_sj.add("waitTimeMillis: " + this.waitTimeMillis);
        return "android.hardware.radio.ims.ConnectionFailureInfo" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
