package android.hardware.radio.network;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class EutranRegistrationInfo implements Parcelable {
    public static final Parcelable.Creator<EutranRegistrationInfo> CREATOR = new Parcelable.Creator<EutranRegistrationInfo>() { // from class: android.hardware.radio.network.EutranRegistrationInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EutranRegistrationInfo createFromParcel(Parcel _aidl_source) {
            EutranRegistrationInfo _aidl_out = new EutranRegistrationInfo();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EutranRegistrationInfo[] newArray(int _aidl_size) {
            return new EutranRegistrationInfo[_aidl_size];
        }
    };
    public static final int EXTRA_CSFB_NOT_PREFERRED = 1;
    public static final int EXTRA_SMS_ONLY = 2;
    public int extraInfo = 0;
    public byte lteAttachResultType;
    public LteVopsInfo lteVopsInfo;
    public NrIndicators nrIndicators;

    /* loaded from: classes2.dex */
    public @interface AttachResultType {
        public static final byte COMBINED = 2;
        public static final byte EPS_ONLY = 1;
        public static final byte NONE = 0;
    }

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeTypedObject(this.lteVopsInfo, _aidl_flag);
        _aidl_parcel.writeTypedObject(this.nrIndicators, _aidl_flag);
        _aidl_parcel.writeByte(this.lteAttachResultType);
        _aidl_parcel.writeInt(this.extraInfo);
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
            this.lteVopsInfo = (LteVopsInfo) _aidl_parcel.readTypedObject(LteVopsInfo.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.nrIndicators = (NrIndicators) _aidl_parcel.readTypedObject(NrIndicators.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.lteAttachResultType = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.extraInfo = _aidl_parcel.readInt();
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
        _aidl_sj.add("lteVopsInfo: " + Objects.toString(this.lteVopsInfo));
        _aidl_sj.add("nrIndicators: " + Objects.toString(this.nrIndicators));
        _aidl_sj.add("lteAttachResultType: " + ((int) this.lteAttachResultType));
        _aidl_sj.add("extraInfo: " + this.extraInfo);
        return "android.hardware.radio.network.EutranRegistrationInfo" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        int _mask = 0 | describeContents(this.lteVopsInfo);
        return _mask | describeContents(this.nrIndicators);
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
