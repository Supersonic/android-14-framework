package com.android.internal.p028os;

import android.p008os.BadParcelableException;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import java.util.List;
/* renamed from: com.android.internal.os.IBinaryTransparencyService */
/* loaded from: classes4.dex */
public interface IBinaryTransparencyService extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.os.IBinaryTransparencyService";

    List<ApexInfo> collectAllApexInfo(boolean z) throws RemoteException;

    List<AppInfo> collectAllSilentInstalledMbaInfo(Bundle bundle) throws RemoteException;

    List<AppInfo> collectAllUpdatedPreloadInfo(Bundle bundle) throws RemoteException;

    String getSignedImageInfo() throws RemoteException;

    void recordMeasurementsForAllPackages() throws RemoteException;

    /* renamed from: com.android.internal.os.IBinaryTransparencyService$Default */
    /* loaded from: classes4.dex */
    public static class Default implements IBinaryTransparencyService {
        @Override // com.android.internal.p028os.IBinaryTransparencyService
        public String getSignedImageInfo() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.p028os.IBinaryTransparencyService
        public void recordMeasurementsForAllPackages() throws RemoteException {
        }

        @Override // com.android.internal.p028os.IBinaryTransparencyService
        public List<ApexInfo> collectAllApexInfo(boolean includeTestOnly) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.p028os.IBinaryTransparencyService
        public List<AppInfo> collectAllUpdatedPreloadInfo(Bundle packagesToSkip) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.p028os.IBinaryTransparencyService
        public List<AppInfo> collectAllSilentInstalledMbaInfo(Bundle packagesToSkip) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: com.android.internal.os.IBinaryTransparencyService$Stub */
    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IBinaryTransparencyService {
        static final int TRANSACTION_collectAllApexInfo = 3;
        static final int TRANSACTION_collectAllSilentInstalledMbaInfo = 5;
        static final int TRANSACTION_collectAllUpdatedPreloadInfo = 4;
        static final int TRANSACTION_getSignedImageInfo = 1;
        static final int TRANSACTION_recordMeasurementsForAllPackages = 2;

        public Stub() {
            attachInterface(this, IBinaryTransparencyService.DESCRIPTOR);
        }

        public static IBinaryTransparencyService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBinaryTransparencyService.DESCRIPTOR);
            if (iin != null && (iin instanceof IBinaryTransparencyService)) {
                return (IBinaryTransparencyService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "getSignedImageInfo";
                case 2:
                    return "recordMeasurementsForAllPackages";
                case 3:
                    return "collectAllApexInfo";
                case 4:
                    return "collectAllUpdatedPreloadInfo";
                case 5:
                    return "collectAllSilentInstalledMbaInfo";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(IBinaryTransparencyService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBinaryTransparencyService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _result = getSignedImageInfo();
                            reply.writeNoException();
                            reply.writeString(_result);
                            break;
                        case 2:
                            recordMeasurementsForAllPackages();
                            reply.writeNoException();
                            break;
                        case 3:
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            List<ApexInfo> _result2 = collectAllApexInfo(_arg0);
                            reply.writeNoException();
                            reply.writeTypedList(_result2, 1);
                            break;
                        case 4:
                            Bundle _arg02 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            List<AppInfo> _result3 = collectAllUpdatedPreloadInfo(_arg02);
                            reply.writeNoException();
                            reply.writeTypedList(_result3, 1);
                            break;
                        case 5:
                            Bundle _arg03 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            List<AppInfo> _result4 = collectAllSilentInstalledMbaInfo(_arg03);
                            reply.writeNoException();
                            reply.writeTypedList(_result4, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: com.android.internal.os.IBinaryTransparencyService$Stub$Proxy */
        /* loaded from: classes4.dex */
        private static class Proxy implements IBinaryTransparencyService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBinaryTransparencyService.DESCRIPTOR;
            }

            @Override // com.android.internal.p028os.IBinaryTransparencyService
            public String getSignedImageInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBinaryTransparencyService.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.p028os.IBinaryTransparencyService
            public void recordMeasurementsForAllPackages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBinaryTransparencyService.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.p028os.IBinaryTransparencyService
            public List<ApexInfo> collectAllApexInfo(boolean includeTestOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBinaryTransparencyService.DESCRIPTOR);
                    _data.writeBoolean(includeTestOnly);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    List<ApexInfo> _result = _reply.createTypedArrayList(ApexInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.p028os.IBinaryTransparencyService
            public List<AppInfo> collectAllUpdatedPreloadInfo(Bundle packagesToSkip) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBinaryTransparencyService.DESCRIPTOR);
                    _data.writeTypedObject(packagesToSkip, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    List<AppInfo> _result = _reply.createTypedArrayList(AppInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.p028os.IBinaryTransparencyService
            public List<AppInfo> collectAllSilentInstalledMbaInfo(Bundle packagesToSkip) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBinaryTransparencyService.DESCRIPTOR);
                    _data.writeTypedObject(packagesToSkip, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<AppInfo> _result = _reply.createTypedArrayList(AppInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }

    /* renamed from: com.android.internal.os.IBinaryTransparencyService$ApexInfo */
    /* loaded from: classes4.dex */
    public static class ApexInfo implements Parcelable {
        public static final Parcelable.Creator<ApexInfo> CREATOR = new Parcelable.Creator<ApexInfo>() { // from class: com.android.internal.os.IBinaryTransparencyService.ApexInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ApexInfo createFromParcel(Parcel _aidl_source) {
                ApexInfo _aidl_out = new ApexInfo();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ApexInfo[] newArray(int _aidl_size) {
                return new ApexInfo[_aidl_size];
            }
        };
        public byte[] digest;
        public String moduleName;
        public String packageName;
        public String[] signerDigests;
        public long longVersion = 0;
        public int digestAlgorithm = 0;

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeString(this.packageName);
            _aidl_parcel.writeLong(this.longVersion);
            _aidl_parcel.writeByteArray(this.digest);
            _aidl_parcel.writeInt(this.digestAlgorithm);
            _aidl_parcel.writeStringArray(this.signerDigests);
            _aidl_parcel.writeString(this.moduleName);
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
                this.packageName = _aidl_parcel.readString();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.longVersion = _aidl_parcel.readLong();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.digest = _aidl_parcel.createByteArray();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.digestAlgorithm = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.signerDigests = _aidl_parcel.createStringArray();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.moduleName = _aidl_parcel.readString();
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

    /* renamed from: com.android.internal.os.IBinaryTransparencyService$AppInfo */
    /* loaded from: classes4.dex */
    public static class AppInfo implements Parcelable {
        public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>() { // from class: com.android.internal.os.IBinaryTransparencyService.AppInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AppInfo createFromParcel(Parcel _aidl_source) {
                AppInfo _aidl_out = new AppInfo();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AppInfo[] newArray(int _aidl_size) {
                return new AppInfo[_aidl_size];
            }
        };
        public byte[] digest;
        public String initiator;
        public String[] initiatorSignerDigests;
        public String installer;
        public String originator;
        public String packageName;
        public String[] signerDigests;
        public String splitName;
        public long longVersion = 0;
        public int digestAlgorithm = 0;
        public int mbaStatus = 0;

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeString(this.packageName);
            _aidl_parcel.writeLong(this.longVersion);
            _aidl_parcel.writeString(this.splitName);
            _aidl_parcel.writeByteArray(this.digest);
            _aidl_parcel.writeInt(this.digestAlgorithm);
            _aidl_parcel.writeStringArray(this.signerDigests);
            _aidl_parcel.writeInt(this.mbaStatus);
            _aidl_parcel.writeString(this.initiator);
            _aidl_parcel.writeStringArray(this.initiatorSignerDigests);
            _aidl_parcel.writeString(this.installer);
            _aidl_parcel.writeString(this.originator);
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
                this.packageName = _aidl_parcel.readString();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.longVersion = _aidl_parcel.readLong();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.splitName = _aidl_parcel.readString();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.digest = _aidl_parcel.createByteArray();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.digestAlgorithm = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.signerDigests = _aidl_parcel.createStringArray();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.mbaStatus = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.initiator = _aidl_parcel.readString();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.initiatorSignerDigests = _aidl_parcel.createStringArray();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.installer = _aidl_parcel.readString();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.originator = _aidl_parcel.readString();
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
}
