package com.android.internal.p028os;

import android.p008os.Binder;
import android.p008os.DropBoxManager;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
/* renamed from: com.android.internal.os.IDropBoxManagerService */
/* loaded from: classes4.dex */
public interface IDropBoxManagerService extends IInterface {
    void addData(String str, byte[] bArr, int i) throws RemoteException;

    void addFile(String str, ParcelFileDescriptor parcelFileDescriptor, int i) throws RemoteException;

    DropBoxManager.Entry getNextEntry(String str, long j, String str2) throws RemoteException;

    DropBoxManager.Entry getNextEntryWithAttribution(String str, long j, String str2, String str3) throws RemoteException;

    boolean isTagEnabled(String str) throws RemoteException;

    /* renamed from: com.android.internal.os.IDropBoxManagerService$Default */
    /* loaded from: classes4.dex */
    public static class Default implements IDropBoxManagerService {
        @Override // com.android.internal.p028os.IDropBoxManagerService
        public void addData(String tag, byte[] data, int flags) throws RemoteException {
        }

        @Override // com.android.internal.p028os.IDropBoxManagerService
        public void addFile(String tag, ParcelFileDescriptor fd, int flags) throws RemoteException {
        }

        @Override // com.android.internal.p028os.IDropBoxManagerService
        public boolean isTagEnabled(String tag) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.p028os.IDropBoxManagerService
        public DropBoxManager.Entry getNextEntry(String tag, long millis, String packageName) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.p028os.IDropBoxManagerService
        public DropBoxManager.Entry getNextEntryWithAttribution(String tag, long millis, String packageName, String attributionTag) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: com.android.internal.os.IDropBoxManagerService$Stub */
    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IDropBoxManagerService {
        public static final String DESCRIPTOR = "com.android.internal.os.IDropBoxManagerService";
        static final int TRANSACTION_addData = 1;
        static final int TRANSACTION_addFile = 2;
        static final int TRANSACTION_getNextEntry = 4;
        static final int TRANSACTION_getNextEntryWithAttribution = 5;
        static final int TRANSACTION_isTagEnabled = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDropBoxManagerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDropBoxManagerService)) {
                return (IDropBoxManagerService) iin;
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
                    return "addData";
                case 2:
                    return "addFile";
                case 3:
                    return "isTagEnabled";
                case 4:
                    return "getNextEntry";
                case 5:
                    return "getNextEntryWithAttribution";
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
                            byte[] _arg1 = data.createByteArray();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            addData(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            ParcelFileDescriptor _arg12 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            addFile(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = isTagEnabled(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            long _arg13 = data.readLong();
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            DropBoxManager.Entry _result2 = getNextEntry(_arg04, _arg13, _arg23);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            long _arg14 = data.readLong();
                            String _arg24 = data.readString();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            DropBoxManager.Entry _result3 = getNextEntryWithAttribution(_arg05, _arg14, _arg24, _arg3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: com.android.internal.os.IDropBoxManagerService$Stub$Proxy */
        /* loaded from: classes4.dex */
        private static class Proxy implements IDropBoxManagerService {
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

            @Override // com.android.internal.p028os.IDropBoxManagerService
            public void addData(String tag, byte[] data, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tag);
                    _data.writeByteArray(data);
                    _data.writeInt(flags);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.p028os.IDropBoxManagerService
            public void addFile(String tag, ParcelFileDescriptor fd, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tag);
                    _data.writeTypedObject(fd, 0);
                    _data.writeInt(flags);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.p028os.IDropBoxManagerService
            public boolean isTagEnabled(String tag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tag);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.p028os.IDropBoxManagerService
            public DropBoxManager.Entry getNextEntry(String tag, long millis, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tag);
                    _data.writeLong(millis);
                    _data.writeString(packageName);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    DropBoxManager.Entry _result = (DropBoxManager.Entry) _reply.readTypedObject(DropBoxManager.Entry.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.p028os.IDropBoxManagerService
            public DropBoxManager.Entry getNextEntryWithAttribution(String tag, long millis, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tag);
                    _data.writeLong(millis);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    DropBoxManager.Entry _result = (DropBoxManager.Entry) _reply.readTypedObject(DropBoxManager.Entry.CREATOR);
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
}
