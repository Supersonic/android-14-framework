package com.android.internal.app;

import android.content.p001pm.PackageInfoLite;
import android.content.res.ObbInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.p028os.IParcelFileDescriptorFactory;
/* loaded from: classes4.dex */
public interface IMediaContainerService extends IInterface {
    long calculateInstalledSize(String str, String str2) throws RemoteException;

    int copyPackage(String str, IParcelFileDescriptorFactory iParcelFileDescriptorFactory) throws RemoteException;

    PackageInfoLite getMinimalPackageInfo(String str, int i, String str2) throws RemoteException;

    ObbInfo getObbInfo(String str) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IMediaContainerService {
        @Override // com.android.internal.app.IMediaContainerService
        public int copyPackage(String packagePath, IParcelFileDescriptorFactory target) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.IMediaContainerService
        public PackageInfoLite getMinimalPackageInfo(String packagePath, int flags, String abiOverride) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IMediaContainerService
        public ObbInfo getObbInfo(String filename) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IMediaContainerService
        public long calculateInstalledSize(String packagePath, String abiOverride) throws RemoteException {
            return 0L;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IMediaContainerService {
        public static final String DESCRIPTOR = "com.android.internal.app.IMediaContainerService";
        static final int TRANSACTION_calculateInstalledSize = 4;
        static final int TRANSACTION_copyPackage = 1;
        static final int TRANSACTION_getMinimalPackageInfo = 2;
        static final int TRANSACTION_getObbInfo = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaContainerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaContainerService)) {
                return (IMediaContainerService) iin;
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
                    return "copyPackage";
                case 2:
                    return "getMinimalPackageInfo";
                case 3:
                    return "getObbInfo";
                case 4:
                    return "calculateInstalledSize";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            IParcelFileDescriptorFactory _arg1 = IParcelFileDescriptorFactory.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result = copyPackage(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            PackageInfoLite _result2 = getMinimalPackageInfo(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            ObbInfo _result3 = getObbInfo(_arg03);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            long _result4 = calculateInstalledSize(_arg04, _arg13);
                            reply.writeNoException();
                            reply.writeLong(_result4);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IMediaContainerService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.android.internal.app.IMediaContainerService
            public int copyPackage(String packagePath, IParcelFileDescriptorFactory target) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packagePath);
                    _data.writeStrongInterface(target);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IMediaContainerService
            public PackageInfoLite getMinimalPackageInfo(String packagePath, int flags, String abiOverride) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packagePath);
                    _data.writeInt(flags);
                    _data.writeString(abiOverride);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    PackageInfoLite _result = (PackageInfoLite) _reply.readTypedObject(PackageInfoLite.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IMediaContainerService
            public ObbInfo getObbInfo(String filename) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(filename);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ObbInfo _result = (ObbInfo) _reply.readTypedObject(ObbInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IMediaContainerService
            public long calculateInstalledSize(String packagePath, String abiOverride) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packagePath);
                    _data.writeString(abiOverride);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
