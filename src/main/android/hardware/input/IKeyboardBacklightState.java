package android.hardware.input;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public class IKeyboardBacklightState implements Parcelable {
    public static final Parcelable.Creator<IKeyboardBacklightState> CREATOR = new Parcelable.Creator<IKeyboardBacklightState>() { // from class: android.hardware.input.IKeyboardBacklightState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IKeyboardBacklightState createFromParcel(Parcel _aidl_source) {
            IKeyboardBacklightState _aidl_out = new IKeyboardBacklightState();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IKeyboardBacklightState[] newArray(int _aidl_size) {
            return new IKeyboardBacklightState[_aidl_size];
        }
    };
    public int brightnessLevel = 0;
    public int maxBrightnessLevel = 0;

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.brightnessLevel);
        _aidl_parcel.writeInt(this.maxBrightnessLevel);
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
            this.brightnessLevel = _aidl_parcel.readInt();
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.maxBrightnessLevel = _aidl_parcel.readInt();
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

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof IKeyboardBacklightState)) {
            return false;
        }
        IKeyboardBacklightState that = (IKeyboardBacklightState) other;
        if (Objects.deepEquals(Integer.valueOf(this.brightnessLevel), Integer.valueOf(that.brightnessLevel)) && Objects.deepEquals(Integer.valueOf(this.maxBrightnessLevel), Integer.valueOf(that.maxBrightnessLevel))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.brightnessLevel), Integer.valueOf(this.maxBrightnessLevel)).toArray());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
