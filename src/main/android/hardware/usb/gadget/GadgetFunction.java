package android.hardware.usb.gadget;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public class GadgetFunction implements Parcelable {
    public static final long ACCESSORY = 2;
    public static final long ADB = 1;
    public static final long AUDIO_SOURCE = 64;
    public static final Parcelable.Creator<GadgetFunction> CREATOR = new Parcelable.Creator<GadgetFunction>() { // from class: android.hardware.usb.gadget.GadgetFunction.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GadgetFunction createFromParcel(Parcel _aidl_source) {
            GadgetFunction _aidl_out = new GadgetFunction();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GadgetFunction[] newArray(int _aidl_size) {
            return new GadgetFunction[_aidl_size];
        }
    };
    public static final long MIDI = 8;
    public static final long MTP = 4;
    public static final long NCM = 1024;
    public static final long NONE = 0;
    public static final long PTP = 16;
    public static final long RNDIS = 32;
    public static final long UVC = 128;

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

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
