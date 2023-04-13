package android.hardware.radio.voice;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class CdmaCallWaiting implements Parcelable {
    public static final Parcelable.Creator<CdmaCallWaiting> CREATOR = new Parcelable.Creator<CdmaCallWaiting>() { // from class: android.hardware.radio.voice.CdmaCallWaiting.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CdmaCallWaiting createFromParcel(Parcel _aidl_source) {
            CdmaCallWaiting _aidl_out = new CdmaCallWaiting();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CdmaCallWaiting[] newArray(int _aidl_size) {
            return new CdmaCallWaiting[_aidl_size];
        }
    };
    public static final int NUMBER_PLAN_DATA = 3;
    public static final int NUMBER_PLAN_ISDN = 1;
    public static final int NUMBER_PLAN_NATIONAL = 8;
    public static final int NUMBER_PLAN_PRIVATE = 9;
    public static final int NUMBER_PLAN_TELEX = 4;
    public static final int NUMBER_PLAN_UNKNOWN = 0;
    public static final int NUMBER_PRESENTATION_ALLOWED = 0;
    public static final int NUMBER_PRESENTATION_RESTRICTED = 1;
    public static final int NUMBER_PRESENTATION_UNKNOWN = 2;
    public static final int NUMBER_TYPE_INTERNATIONAL = 1;
    public static final int NUMBER_TYPE_NATIONAL = 2;
    public static final int NUMBER_TYPE_NETWORK_SPECIFIC = 3;
    public static final int NUMBER_TYPE_SUBSCRIBER = 4;
    public static final int NUMBER_TYPE_UNKNOWN = 0;
    public String name;
    public String number;
    public CdmaSignalInfoRecord signalInfoRecord;
    public int numberPresentation = 0;
    public int numberType = 0;
    public int numberPlan = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeString(this.number);
        _aidl_parcel.writeInt(this.numberPresentation);
        _aidl_parcel.writeString(this.name);
        _aidl_parcel.writeTypedObject(this.signalInfoRecord, _aidl_flag);
        _aidl_parcel.writeInt(this.numberType);
        _aidl_parcel.writeInt(this.numberPlan);
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
            this.number = _aidl_parcel.readString();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.numberPresentation = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.name = _aidl_parcel.readString();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.signalInfoRecord = (CdmaSignalInfoRecord) _aidl_parcel.readTypedObject(CdmaSignalInfoRecord.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.numberType = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.numberPlan = _aidl_parcel.readInt();
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
        _aidl_sj.add("number: " + Objects.toString(this.number));
        _aidl_sj.add("numberPresentation: " + this.numberPresentation);
        _aidl_sj.add("name: " + Objects.toString(this.name));
        _aidl_sj.add("signalInfoRecord: " + Objects.toString(this.signalInfoRecord));
        _aidl_sj.add("numberType: " + this.numberType);
        _aidl_sj.add("numberPlan: " + this.numberPlan);
        return "android.hardware.radio.voice.CdmaCallWaiting" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.signalInfoRecord);
        return _mask;
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
