package android.hardware.radio.voice;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class UusInfo implements Parcelable {
    public static final Parcelable.Creator<UusInfo> CREATOR = new Parcelable.Creator<UusInfo>() { // from class: android.hardware.radio.voice.UusInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UusInfo createFromParcel(Parcel _aidl_source) {
            UusInfo _aidl_out = new UusInfo();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UusInfo[] newArray(int _aidl_size) {
            return new UusInfo[_aidl_size];
        }
    };
    public static final int UUS_DCS_IA5C = 4;
    public static final int UUS_DCS_OSIHLP = 1;
    public static final int UUS_DCS_RMCF = 3;
    public static final int UUS_DCS_USP = 0;
    public static final int UUS_DCS_X244 = 2;
    public static final int UUS_TYPE_TYPE1_IMPLICIT = 0;
    public static final int UUS_TYPE_TYPE1_NOT_REQUIRED = 2;
    public static final int UUS_TYPE_TYPE1_REQUIRED = 1;
    public static final int UUS_TYPE_TYPE2_NOT_REQUIRED = 4;
    public static final int UUS_TYPE_TYPE2_REQUIRED = 3;
    public static final int UUS_TYPE_TYPE3_NOT_REQUIRED = 6;
    public static final int UUS_TYPE_TYPE3_REQUIRED = 5;
    public String uusData;
    public int uusType = 0;
    public int uusDcs = 0;

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.uusType);
        _aidl_parcel.writeInt(this.uusDcs);
        _aidl_parcel.writeString(this.uusData);
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
            this.uusType = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.uusDcs = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.uusData = _aidl_parcel.readString();
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
        _aidl_sj.add("uusType: " + this.uusType);
        _aidl_sj.add("uusDcs: " + this.uusDcs);
        _aidl_sj.add("uusData: " + Objects.toString(this.uusData));
        return "android.hardware.radio.voice.UusInfo" + _aidl_sj.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
