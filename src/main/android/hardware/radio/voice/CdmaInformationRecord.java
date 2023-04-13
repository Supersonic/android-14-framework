package android.hardware.radio.voice;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class CdmaInformationRecord implements Parcelable {
    public static final int CDMA_MAX_NUMBER_OF_INFO_RECS = 10;
    public static final Parcelable.Creator<CdmaInformationRecord> CREATOR = new Parcelable.Creator<CdmaInformationRecord>() { // from class: android.hardware.radio.voice.CdmaInformationRecord.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CdmaInformationRecord createFromParcel(Parcel _aidl_source) {
            CdmaInformationRecord _aidl_out = new CdmaInformationRecord();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CdmaInformationRecord[] newArray(int _aidl_size) {
            return new CdmaInformationRecord[_aidl_size];
        }
    };
    public static final int NAME_CALLED_PARTY_NUMBER = 1;
    public static final int NAME_CALLING_PARTY_NUMBER = 2;
    public static final int NAME_CONNECTED_NUMBER = 3;
    public static final int NAME_DISPLAY = 0;
    public static final int NAME_EXTENDED_DISPLAY = 7;
    public static final int NAME_LINE_CONTROL = 6;
    public static final int NAME_REDIRECTING_NUMBER = 5;
    public static final int NAME_SIGNAL = 4;
    public static final int NAME_T53_AUDIO_CONTROL = 10;
    public static final int NAME_T53_CLIR = 8;
    public static final int NAME_T53_RELEASE = 9;
    public CdmaT53AudioControlInfoRecord[] audioCtrl;
    public CdmaT53ClirInfoRecord[] clir;
    public CdmaDisplayInfoRecord[] display;
    public CdmaLineControlInfoRecord[] lineCtrl;
    public int name = 0;
    public CdmaNumberInfoRecord[] number;
    public CdmaRedirectingNumberInfoRecord[] redir;
    public CdmaSignalInfoRecord[] signal;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.name);
        _aidl_parcel.writeTypedArray(this.display, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.number, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.signal, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.redir, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.lineCtrl, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.clir, _aidl_flag);
        _aidl_parcel.writeTypedArray(this.audioCtrl, _aidl_flag);
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
            this.name = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.display = (CdmaDisplayInfoRecord[]) _aidl_parcel.createTypedArray(CdmaDisplayInfoRecord.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.number = (CdmaNumberInfoRecord[]) _aidl_parcel.createTypedArray(CdmaNumberInfoRecord.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.signal = (CdmaSignalInfoRecord[]) _aidl_parcel.createTypedArray(CdmaSignalInfoRecord.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.redir = (CdmaRedirectingNumberInfoRecord[]) _aidl_parcel.createTypedArray(CdmaRedirectingNumberInfoRecord.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.lineCtrl = (CdmaLineControlInfoRecord[]) _aidl_parcel.createTypedArray(CdmaLineControlInfoRecord.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.clir = (CdmaT53ClirInfoRecord[]) _aidl_parcel.createTypedArray(CdmaT53ClirInfoRecord.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.audioCtrl = (CdmaT53AudioControlInfoRecord[]) _aidl_parcel.createTypedArray(CdmaT53AudioControlInfoRecord.CREATOR);
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
        _aidl_sj.add("name: " + this.name);
        _aidl_sj.add("display: " + Arrays.toString(this.display));
        _aidl_sj.add("number: " + Arrays.toString(this.number));
        _aidl_sj.add("signal: " + Arrays.toString(this.signal));
        _aidl_sj.add("redir: " + Arrays.toString(this.redir));
        _aidl_sj.add("lineCtrl: " + Arrays.toString(this.lineCtrl));
        _aidl_sj.add("clir: " + Arrays.toString(this.clir));
        _aidl_sj.add("audioCtrl: " + Arrays.toString(this.audioCtrl));
        return "android.hardware.radio.voice.CdmaInformationRecord" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.display);
        return _mask | describeContents(this.number) | describeContents(this.signal) | describeContents(this.redir) | describeContents(this.lineCtrl) | describeContents(this.clir) | describeContents(this.audioCtrl);
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
