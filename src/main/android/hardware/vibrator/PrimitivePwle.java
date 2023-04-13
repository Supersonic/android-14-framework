package android.hardware.vibrator;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public final class PrimitivePwle implements Parcelable {
    public static final Parcelable.Creator<PrimitivePwle> CREATOR = new Parcelable.Creator<PrimitivePwle>() { // from class: android.hardware.vibrator.PrimitivePwle.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrimitivePwle createFromParcel(Parcel _aidl_source) {
            return new PrimitivePwle(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrimitivePwle[] newArray(int _aidl_size) {
            return new PrimitivePwle[_aidl_size];
        }
    };
    public static final int active = 0;
    public static final int braking = 1;
    private int _tag;
    private Object _value;

    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int active = 0;
        public static final int braking = 1;
    }

    public PrimitivePwle() {
        this._tag = 0;
        this._value = null;
    }

    private PrimitivePwle(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private PrimitivePwle(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static PrimitivePwle active(ActivePwle _value) {
        return new PrimitivePwle(0, _value);
    }

    public ActivePwle getActive() {
        _assertTag(0);
        return (ActivePwle) this._value;
    }

    public void setActive(ActivePwle _value) {
        _set(0, _value);
    }

    public static PrimitivePwle braking(BrakingPwle _value) {
        return new PrimitivePwle(1, _value);
    }

    public BrakingPwle getBraking() {
        _assertTag(1);
        return (BrakingPwle) this._value;
    }

    public void setBraking(BrakingPwle _value) {
        _set(1, _value);
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
                _aidl_parcel.writeTypedObject(getActive(), _aidl_flag);
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getBraking(), _aidl_flag);
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                ActivePwle _aidl_value = (ActivePwle) _aidl_parcel.readTypedObject(ActivePwle.CREATOR);
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                BrakingPwle _aidl_value2 = (BrakingPwle) _aidl_parcel.readTypedObject(BrakingPwle.CREATOR);
                _set(_aidl_tag, _aidl_value2);
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 0:
                int _mask = 0 | describeContents(getActive());
                return _mask;
            case 1:
                int _mask2 = 0 | describeContents(getBraking());
                return _mask2;
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
                return "active";
            case 1:
                return "braking";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
