package android.hardware.radio.network;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class NrVopsInfo implements Parcelable {
    public static final Parcelable.Creator<NrVopsInfo> CREATOR = new Parcelable.Creator<NrVopsInfo>() { // from class: android.hardware.radio.network.NrVopsInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NrVopsInfo createFromParcel(Parcel _aidl_source) {
            NrVopsInfo _aidl_out = new NrVopsInfo();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NrVopsInfo[] newArray(int _aidl_size) {
            return new NrVopsInfo[_aidl_size];
        }
    };
    public static final byte EMC_INDICATOR_BOTH_NR_EUTRA_CONNECTED_TO_5GCN = 3;
    public static final byte EMC_INDICATOR_EUTRA_CONNECTED_TO_5GCN = 2;
    public static final byte EMC_INDICATOR_NOT_SUPPORTED = 0;
    public static final byte EMC_INDICATOR_NR_CONNECTED_TO_5GCN = 1;
    public static final byte EMF_INDICATOR_BOTH_NR_EUTRA_CONNECTED_TO_5GCN = 3;
    public static final byte EMF_INDICATOR_EUTRA_CONNECTED_TO_5GCN = 2;
    public static final byte EMF_INDICATOR_NOT_SUPPORTED = 0;
    public static final byte EMF_INDICATOR_NR_CONNECTED_TO_5GCN = 1;
    public static final byte VOPS_INDICATOR_VOPS_NOT_SUPPORTED = 0;
    public static final byte VOPS_INDICATOR_VOPS_OVER_3GPP = 1;
    public static final byte VOPS_INDICATOR_VOPS_OVER_NON_3GPP = 2;
    public byte vopsSupported = 0;
    public byte emcSupported = 0;
    public byte emfSupported = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeByte(this.vopsSupported);
        _aidl_parcel.writeByte(this.emcSupported);
        _aidl_parcel.writeByte(this.emfSupported);
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
            this.vopsSupported = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.emcSupported = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.emfSupported = _aidl_parcel.readByte();
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
        _aidl_sj.add("vopsSupported: " + ((int) this.vopsSupported));
        _aidl_sj.add("emcSupported: " + ((int) this.emcSupported));
        _aidl_sj.add("emfSupported: " + ((int) this.emfSupported));
        return "android.hardware.radio.network.NrVopsInfo" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
