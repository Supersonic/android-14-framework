package android.hardware.radio.voice;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class CdmaRedirectingNumberInfoRecord implements Parcelable {
    public static final Parcelable.Creator<CdmaRedirectingNumberInfoRecord> CREATOR = new Parcelable.Creator<CdmaRedirectingNumberInfoRecord>() { // from class: android.hardware.radio.voice.CdmaRedirectingNumberInfoRecord.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CdmaRedirectingNumberInfoRecord createFromParcel(Parcel _aidl_source) {
            CdmaRedirectingNumberInfoRecord _aidl_out = new CdmaRedirectingNumberInfoRecord();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CdmaRedirectingNumberInfoRecord[] newArray(int _aidl_size) {
            return new CdmaRedirectingNumberInfoRecord[_aidl_size];
        }
    };
    public static final int REDIRECTING_REASON_CALLED_DTE_OUT_OF_ORDER = 9;
    public static final int REDIRECTING_REASON_CALL_FORWARDING_BUSY = 1;
    public static final int REDIRECTING_REASON_CALL_FORWARDING_BY_THE_CALLED_DTE = 10;
    public static final int REDIRECTING_REASON_CALL_FORWARDING_NO_REPLY = 2;
    public static final int REDIRECTING_REASON_CALL_FORWARDING_UNCONDITIONAL = 15;
    public static final int REDIRECTING_REASON_RESERVED = 16;
    public static final int REDIRECTING_REASON_UNKNOWN = 0;
    public CdmaNumberInfoRecord redirectingNumber;
    public int redirectingReason = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeTypedObject(this.redirectingNumber, _aidl_flag);
        _aidl_parcel.writeInt(this.redirectingReason);
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
            this.redirectingNumber = (CdmaNumberInfoRecord) _aidl_parcel.readTypedObject(CdmaNumberInfoRecord.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.redirectingReason = _aidl_parcel.readInt();
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
        _aidl_sj.add("redirectingNumber: " + Objects.toString(this.redirectingNumber));
        _aidl_sj.add("redirectingReason: " + this.redirectingReason);
        return "android.hardware.radio.voice.CdmaRedirectingNumberInfoRecord" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.redirectingNumber);
        return _mask;
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
