package android.hardware.radio;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class RadioConst implements Parcelable {
    public static final int CARD_MAX_APPS = 8;
    public static final Parcelable.Creator<RadioConst> CREATOR = new Parcelable.Creator<RadioConst>() { // from class: android.hardware.radio.RadioConst.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RadioConst createFromParcel(Parcel _aidl_source) {
            RadioConst _aidl_out = new RadioConst();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RadioConst[] newArray(int _aidl_size) {
            return new RadioConst[_aidl_size];
        }
    };
    public static final int MAX_RILDS = 3;
    public static final int MAX_UUID_LENGTH = 64;
    public static final int P2_CONSTANT_NO_P2 = -1;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        int _aidl_end_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.setDataPosition(_aidl_start_pos);
        _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
        _aidl_parcel.setDataPosition(_aidl_end_pos);
    }

    public final void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        int _aidl_parcelable_size = _aidl_parcel.readInt();
        if (_aidl_parcelable_size < 4) {
            try {
                throw new BadParcelableException("Parcelable too small");
            } catch (Throwable th) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                throw th;
            }
        } else if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
            throw new BadParcelableException("Overflow in the size of parcelable");
        } else {
            _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
        }
    }

    public String toString() {
        StringJoiner _aidl_sj = new StringJoiner(", ", "{", "}");
        return "android.hardware.radio.RadioConst" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
