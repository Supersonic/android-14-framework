package android.content.p001pm;

import android.content.p001pm.IPackageInstallerSessionFileSystemConnector;
import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.incremental.IIncrementalServiceConnector;
import android.p008os.incremental.IncrementalFileSystemControlParcel;
/* renamed from: android.content.pm.FileSystemControlParcel */
/* loaded from: classes.dex */
public class FileSystemControlParcel implements Parcelable {
    public static final Parcelable.Creator<FileSystemControlParcel> CREATOR = new Parcelable.Creator<FileSystemControlParcel>() { // from class: android.content.pm.FileSystemControlParcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FileSystemControlParcel createFromParcel(Parcel _aidl_source) {
            FileSystemControlParcel _aidl_out = new FileSystemControlParcel();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FileSystemControlParcel[] newArray(int _aidl_size) {
            return new FileSystemControlParcel[_aidl_size];
        }
    };
    public IPackageInstallerSessionFileSystemConnector callback;
    public IncrementalFileSystemControlParcel incremental;
    public IIncrementalServiceConnector service;

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeTypedObject(this.incremental, _aidl_flag);
        _aidl_parcel.writeStrongInterface(this.service);
        _aidl_parcel.writeStrongInterface(this.callback);
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
            this.incremental = (IncrementalFileSystemControlParcel) _aidl_parcel.readTypedObject(IncrementalFileSystemControlParcel.CREATOR);
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.service = IIncrementalServiceConnector.Stub.asInterface(_aidl_parcel.readStrongBinder());
            if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                return;
            }
            this.callback = IPackageInstallerSessionFileSystemConnector.Stub.asInterface(_aidl_parcel.readStrongBinder());
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
        int _mask = 0 | describeContents(this.incremental);
        return _mask;
    }

    private int describeContents(Object _v) {
        if (_v == null || !(_v instanceof Parcelable)) {
            return 0;
        }
        return ((Parcelable) _v).describeContents();
    }
}
