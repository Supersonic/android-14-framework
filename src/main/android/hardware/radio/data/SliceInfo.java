package android.hardware.radio.data;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class SliceInfo implements Parcelable {
    public static final Parcelable.Creator<SliceInfo> CREATOR = new Parcelable.Creator<SliceInfo>() { // from class: android.hardware.radio.data.SliceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SliceInfo createFromParcel(Parcel _aidl_source) {
            SliceInfo _aidl_out = new SliceInfo();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SliceInfo[] newArray(int _aidl_size) {
            return new SliceInfo[_aidl_size];
        }
    };
    public static final byte SERVICE_TYPE_EMBB = 1;
    public static final byte SERVICE_TYPE_MIOT = 3;
    public static final byte SERVICE_TYPE_NONE = 0;
    public static final byte SERVICE_TYPE_URLLC = 2;
    public static final byte STATUS_ALLOWED = 2;
    public static final byte STATUS_CONFIGURED = 1;
    public static final byte STATUS_DEFAULT_CONFIGURED = 5;
    public static final byte STATUS_REJECTED_NOT_AVAILABLE_IN_PLMN = 3;
    public static final byte STATUS_REJECTED_NOT_AVAILABLE_IN_REG_AREA = 4;
    public static final byte STATUS_UNKNOWN = 0;
    public byte sliceServiceType = 0;
    public int sliceDifferentiator = 0;
    public byte mappedHplmnSst = 0;
    public int mappedHplmnSd = 0;
    public byte status = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeByte(this.sliceServiceType);
        _aidl_parcel.writeInt(this.sliceDifferentiator);
        _aidl_parcel.writeByte(this.mappedHplmnSst);
        _aidl_parcel.writeInt(this.mappedHplmnSd);
        _aidl_parcel.writeByte(this.status);
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
            this.sliceServiceType = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.sliceDifferentiator = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.mappedHplmnSst = _aidl_parcel.readByte();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.mappedHplmnSd = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.status = _aidl_parcel.readByte();
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
        _aidl_sj.add("sliceServiceType: " + ((int) this.sliceServiceType));
        _aidl_sj.add("sliceDifferentiator: " + this.sliceDifferentiator);
        _aidl_sj.add("mappedHplmnSst: " + ((int) this.mappedHplmnSst));
        _aidl_sj.add("mappedHplmnSd: " + this.mappedHplmnSd);
        _aidl_sj.add("status: " + ((int) this.status));
        return "android.hardware.radio.data.SliceInfo" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
