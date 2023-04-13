package android.hardware.cas;

import android.hardware.common.NativeHandle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class DestinationBuffer implements Parcelable {
    public static final Parcelable.Creator<DestinationBuffer> CREATOR = new Parcelable.Creator<DestinationBuffer>() { // from class: android.hardware.cas.DestinationBuffer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DestinationBuffer createFromParcel(Parcel _aidl_source) {
            return new DestinationBuffer(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DestinationBuffer[] newArray(int _aidl_size) {
            return new DestinationBuffer[_aidl_size];
        }
    };
    public static final int nonsecureMemory = 0;
    public static final int secureMemory = 1;
    private int _tag;
    private Object _value;

    /* loaded from: classes.dex */
    public @interface Tag {
        public static final int nonsecureMemory = 0;
        public static final int secureMemory = 1;
    }

    public DestinationBuffer() {
        this._tag = 0;
        this._value = null;
    }

    private DestinationBuffer(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private DestinationBuffer(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static DestinationBuffer nonsecureMemory(SharedBuffer _value) {
        return new DestinationBuffer(0, _value);
    }

    public SharedBuffer getNonsecureMemory() {
        _assertTag(0);
        return (SharedBuffer) this._value;
    }

    public void setNonsecureMemory(SharedBuffer _value) {
        _set(0, _value);
    }

    public static DestinationBuffer secureMemory(NativeHandle _value) {
        return new DestinationBuffer(1, _value);
    }

    public NativeHandle getSecureMemory() {
        _assertTag(1);
        return (NativeHandle) this._value;
    }

    public void setSecureMemory(NativeHandle _value) {
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
                _aidl_parcel.writeTypedObject(getNonsecureMemory(), _aidl_flag);
                return;
            case 1:
                _aidl_parcel.writeTypedObject(getSecureMemory(), _aidl_flag);
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                SharedBuffer _aidl_value = (SharedBuffer) _aidl_parcel.readTypedObject(SharedBuffer.CREATOR);
                _set(_aidl_tag, _aidl_value);
                return;
            case 1:
                NativeHandle _aidl_value2 = (NativeHandle) _aidl_parcel.readTypedObject(NativeHandle.CREATOR);
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
                int _mask = 0 | describeContents(getNonsecureMemory());
                return _mask;
            case 1:
                int _mask2 = 0 | describeContents(getSecureMemory());
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
                return "nonsecureMemory";
            case 1:
                return "secureMemory";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
