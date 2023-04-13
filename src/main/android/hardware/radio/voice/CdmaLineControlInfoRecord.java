package android.hardware.radio.voice;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class CdmaLineControlInfoRecord implements Parcelable {
    public static final Parcelable.Creator<CdmaLineControlInfoRecord> CREATOR = new Parcelable.Creator<CdmaLineControlInfoRecord>() { // from class: android.hardware.radio.voice.CdmaLineControlInfoRecord.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CdmaLineControlInfoRecord createFromParcel(Parcel _aidl_source) {
            CdmaLineControlInfoRecord _aidl_out = new CdmaLineControlInfoRecord();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CdmaLineControlInfoRecord[] newArray(int _aidl_size) {
            return new CdmaLineControlInfoRecord[_aidl_size];
        }
    };
    public byte lineCtrlPolarityIncluded = 0;
    public byte lineCtrlToggle = 0;
    public byte lineCtrlReverse = 0;
    public byte lineCtrlPowerDenial = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeByte(this.lineCtrlPolarityIncluded);
        _aidl_parcel.writeByte(this.lineCtrlToggle);
        _aidl_parcel.writeByte(this.lineCtrlReverse);
        _aidl_parcel.writeByte(this.lineCtrlPowerDenial);
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
            this.lineCtrlPolarityIncluded = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.lineCtrlToggle = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.lineCtrlReverse = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.lineCtrlPowerDenial = _aidl_parcel.readByte();
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
        _aidl_sj.add("lineCtrlPolarityIncluded: " + ((int) this.lineCtrlPolarityIncluded));
        _aidl_sj.add("lineCtrlToggle: " + ((int) this.lineCtrlToggle));
        _aidl_sj.add("lineCtrlReverse: " + ((int) this.lineCtrlReverse));
        _aidl_sj.add("lineCtrlPowerDenial: " + ((int) this.lineCtrlPowerDenial));
        return "android.hardware.radio.voice.CdmaLineControlInfoRecord" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
