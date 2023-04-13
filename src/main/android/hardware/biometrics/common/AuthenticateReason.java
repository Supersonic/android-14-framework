package android.hardware.biometrics.common;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ParcelableHolder;
/* loaded from: classes.dex */
public final class AuthenticateReason implements Parcelable {
    public static final Parcelable.Creator<AuthenticateReason> CREATOR = new Parcelable.Creator<AuthenticateReason>() { // from class: android.hardware.biometrics.common.AuthenticateReason.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AuthenticateReason createFromParcel(Parcel _aidl_source) {
            return new AuthenticateReason(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AuthenticateReason[] newArray(int _aidl_size) {
            return new AuthenticateReason[_aidl_size];
        }
    };
    public static final int faceAuthenticateReason = 1;
    public static final int fingerprintAuthenticateReason = 2;
    public static final int vendorAuthenticateReason = 0;
    private int _tag;
    private Object _value;

    /* loaded from: classes.dex */
    public @interface Face {
        public static final int ALTERNATE_BIOMETRIC_BOUNCER_SHOWN = 4;
        public static final int ASSISTANT_VISIBLE = 3;
        public static final int NOTIFICATION_PANEL_CLICKED = 5;
        public static final int OCCLUDING_APP_REQUESTED = 6;
        public static final int PICK_UP_GESTURE_TRIGGERED = 7;
        public static final int PRIMARY_BOUNCER_SHOWN = 2;
        public static final int QS_EXPANDED = 8;
        public static final int STARTED_WAKING_UP = 1;
        public static final int SWIPE_UP_ON_BOUNCER = 9;
        public static final int UDFPS_POINTER_DOWN = 10;
        public static final int UNKNOWN = 0;
    }

    /* loaded from: classes.dex */
    public @interface Fingerprint {
        public static final int UNKNOWN = 0;
    }

    /* loaded from: classes.dex */
    public @interface Tag {
        public static final int faceAuthenticateReason = 1;
        public static final int fingerprintAuthenticateReason = 2;
        public static final int vendorAuthenticateReason = 0;
    }

    public AuthenticateReason() {
        this._tag = 0;
        this._value = null;
    }

    private AuthenticateReason(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private AuthenticateReason(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static AuthenticateReason vendorAuthenticateReason(Vendor _value) {
        return new AuthenticateReason(0, _value);
    }

    public Vendor getVendorAuthenticateReason() {
        _assertTag(0);
        return (Vendor) this._value;
    }

    public void setVendorAuthenticateReason(Vendor _value) {
        _set(0, _value);
    }

    public static AuthenticateReason faceAuthenticateReason(int _value) {
        return new AuthenticateReason(1, Integer.valueOf(_value));
    }

    public int getFaceAuthenticateReason() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setFaceAuthenticateReason(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static AuthenticateReason fingerprintAuthenticateReason(int _value) {
        return new AuthenticateReason(2, Integer.valueOf(_value));
    }

    public int getFingerprintAuthenticateReason() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setFingerprintAuthenticateReason(int _value) {
        _set(2, Integer.valueOf(_value));
    }

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        _aidl_parcel.writeInt(this._tag);
        switch (this._tag) {
            case 0:
                _aidl_parcel.writeTypedObject(getVendorAuthenticateReason(), _aidl_flag);
                return;
            case 1:
                _aidl_parcel.writeInt(getFaceAuthenticateReason());
                return;
            case 2:
                _aidl_parcel.writeInt(getFingerprintAuthenticateReason());
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                Vendor _aidl_value = (Vendor) _aidl_parcel.readTypedObject(Vendor.CREATOR);
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                int _aidl_value2 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value2));
                return;
            case 2:
                int _aidl_value3 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value3));
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 0:
                int _mask = 0 | describeContents(getVendorAuthenticateReason());
                return _mask;
            default:
                return 0;
        }
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }

    private void _assertTag(int tag) {
        if (getTag() != tag) {
            throw new IllegalStateException("bad access: " + _tagString(tag) + ", " + _tagString(getTag()) + " is available.");
        }
    }

    private String _tagString(int _tag) {
        switch (_tag) {
            case 0:
                return "vendorAuthenticateReason";
            case 1:
                return "faceAuthenticateReason";
            case 2:
                return "fingerprintAuthenticateReason";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    /* loaded from: classes.dex */
    public static class Vendor implements Parcelable {
        public static final Parcelable.Creator<Vendor> CREATOR = new Parcelable.Creator<Vendor>() { // from class: android.hardware.biometrics.common.AuthenticateReason.Vendor.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Vendor createFromParcel(Parcel _aidl_source) {
                Vendor _aidl_out = new Vendor();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Vendor[] newArray(int _aidl_size) {
                return new Vendor[_aidl_size];
            }
        };
        public final ParcelableHolder extension = new ParcelableHolder(1);

        @Override // android.p008os.Parcelable
        public final int getStability() {
            return 1;
        }

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeTypedObject(this.extension, 0);
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
                if (_aidl_parcel.readInt() != 0) {
                    this.extension.readFromParcel(_aidl_parcel);
                }
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

        @Override // android.p008os.Parcelable
        public int describeContents() {
            int _mask = 0 | describeContents(this.extension);
            return _mask;
        }

        private int describeContents(Object _v) {
            if (_v == null || !(_v instanceof Parcelable)) {
                return 0;
            }
            return ((Parcelable) _v).describeContents();
        }
    }
}
