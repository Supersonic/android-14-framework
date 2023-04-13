package android.window;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.window.ISurfaceSyncGroup;
import android.window.ITransactionReadyCallback;
/* loaded from: classes4.dex */
public class AddToSurfaceSyncGroupResult implements Parcelable {
    public static final Parcelable.Creator<AddToSurfaceSyncGroupResult> CREATOR = new Parcelable.Creator<AddToSurfaceSyncGroupResult>() { // from class: android.window.AddToSurfaceSyncGroupResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AddToSurfaceSyncGroupResult createFromParcel(Parcel _aidl_source) {
            AddToSurfaceSyncGroupResult _aidl_out = new AddToSurfaceSyncGroupResult();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AddToSurfaceSyncGroupResult[] newArray(int _aidl_size) {
            return new AddToSurfaceSyncGroupResult[_aidl_size];
        }
    };
    public ISurfaceSyncGroup mParentSyncGroup;
    public ITransactionReadyCallback mTransactionReadyCallback;

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeStrongInterface(this.mParentSyncGroup);
        _aidl_parcel.writeStrongInterface(this.mTransactionReadyCallback);
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
            this.mParentSyncGroup = ISurfaceSyncGroup.Stub.asInterface(_aidl_parcel.readStrongBinder());
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.mTransactionReadyCallback = ITransactionReadyCallback.Stub.asInterface(_aidl_parcel.readStrongBinder());
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
        return 0;
    }
}
