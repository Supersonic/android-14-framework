package android.hardware.radio.network;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class SignalStrength implements Parcelable {
    public static final Parcelable.Creator<SignalStrength> CREATOR = new Parcelable.Creator<SignalStrength>() { // from class: android.hardware.radio.network.SignalStrength.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SignalStrength createFromParcel(Parcel _aidl_source) {
            SignalStrength _aidl_out = new SignalStrength();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SignalStrength[] newArray(int _aidl_size) {
            return new SignalStrength[_aidl_size];
        }
    };
    public CdmaSignalStrength cdma;
    public EvdoSignalStrength evdo;
    public GsmSignalStrength gsm;
    public LteSignalStrength lte;

    /* renamed from: nr */
    public NrSignalStrength f202nr;
    public TdscdmaSignalStrength tdscdma;
    public WcdmaSignalStrength wcdma;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeTypedObject(this.gsm, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.cdma, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.evdo, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.lte, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.tdscdma, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.wcdma, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.f202nr, _aidl_flag);
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
            this.gsm = (GsmSignalStrength) _aidl_parcel.readTypedObject(GsmSignalStrength.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.cdma = (CdmaSignalStrength) _aidl_parcel.readTypedObject(CdmaSignalStrength.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.evdo = (EvdoSignalStrength) _aidl_parcel.readTypedObject(EvdoSignalStrength.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.lte = (LteSignalStrength) _aidl_parcel.readTypedObject(LteSignalStrength.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.tdscdma = (TdscdmaSignalStrength) _aidl_parcel.readTypedObject(TdscdmaSignalStrength.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.wcdma = (WcdmaSignalStrength) _aidl_parcel.readTypedObject(WcdmaSignalStrength.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.f202nr = (NrSignalStrength) _aidl_parcel.readTypedObject(NrSignalStrength.CREATOR);
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
        _aidl_sj.add("gsm: " + Objects.toString(this.gsm));
        _aidl_sj.add("cdma: " + Objects.toString(this.cdma));
        _aidl_sj.add("evdo: " + Objects.toString(this.evdo));
        _aidl_sj.add("lte: " + Objects.toString(this.lte));
        _aidl_sj.add("tdscdma: " + Objects.toString(this.tdscdma));
        _aidl_sj.add("wcdma: " + Objects.toString(this.wcdma));
        _aidl_sj.add("nr: " + Objects.toString(this.f202nr));
        return "android.hardware.radio.network.SignalStrength" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.gsm);
        return _mask | describeContents(this.cdma) | describeContents(this.evdo) | describeContents(this.lte) | describeContents(this.tdscdma) | describeContents(this.wcdma) | describeContents(this.f202nr);
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
