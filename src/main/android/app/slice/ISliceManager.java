package android.app.slice;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ISliceManager extends IInterface {
    void applyRestore(byte[] bArr, int i) throws RemoteException;

    int checkSlicePermission(Uri uri, String str, int i, int i2, String[] strArr) throws RemoteException;

    byte[] getBackupPayload(int i) throws RemoteException;

    Uri[] getPinnedSlices(String str) throws RemoteException;

    SliceSpec[] getPinnedSpecs(Uri uri, String str) throws RemoteException;

    void grantPermissionFromUser(Uri uri, String str, String str2, boolean z) throws RemoteException;

    void grantSlicePermission(String str, String str2, Uri uri) throws RemoteException;

    boolean hasSliceAccess(String str) throws RemoteException;

    void pinSlice(String str, Uri uri, SliceSpec[] sliceSpecArr, IBinder iBinder) throws RemoteException;

    void revokeSlicePermission(String str, String str2, Uri uri) throws RemoteException;

    void unpinSlice(String str, Uri uri, IBinder iBinder) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISliceManager {
        @Override // android.app.slice.ISliceManager
        public void pinSlice(String pkg, Uri uri, SliceSpec[] specs, IBinder token) throws RemoteException {
        }

        @Override // android.app.slice.ISliceManager
        public void unpinSlice(String pkg, Uri uri, IBinder token) throws RemoteException {
        }

        @Override // android.app.slice.ISliceManager
        public boolean hasSliceAccess(String pkg) throws RemoteException {
            return false;
        }

        @Override // android.app.slice.ISliceManager
        public SliceSpec[] getPinnedSpecs(Uri uri, String pkg) throws RemoteException {
            return null;
        }

        @Override // android.app.slice.ISliceManager
        public Uri[] getPinnedSlices(String pkg) throws RemoteException {
            return null;
        }

        @Override // android.app.slice.ISliceManager
        public byte[] getBackupPayload(int user) throws RemoteException {
            return null;
        }

        @Override // android.app.slice.ISliceManager
        public void applyRestore(byte[] payload, int user) throws RemoteException {
        }

        @Override // android.app.slice.ISliceManager
        public void grantSlicePermission(String callingPkg, String toPkg, Uri uri) throws RemoteException {
        }

        @Override // android.app.slice.ISliceManager
        public void revokeSlicePermission(String callingPkg, String toPkg, Uri uri) throws RemoteException {
        }

        @Override // android.app.slice.ISliceManager
        public int checkSlicePermission(Uri uri, String callingPkg, int pid, int uid, String[] autoGrantPermissions) throws RemoteException {
            return 0;
        }

        @Override // android.app.slice.ISliceManager
        public void grantPermissionFromUser(Uri uri, String pkg, String callingPkg, boolean allSlices) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISliceManager {
        public static final String DESCRIPTOR = "android.app.slice.ISliceManager";
        static final int TRANSACTION_applyRestore = 7;
        static final int TRANSACTION_checkSlicePermission = 10;
        static final int TRANSACTION_getBackupPayload = 6;
        static final int TRANSACTION_getPinnedSlices = 5;
        static final int TRANSACTION_getPinnedSpecs = 4;
        static final int TRANSACTION_grantPermissionFromUser = 11;
        static final int TRANSACTION_grantSlicePermission = 8;
        static final int TRANSACTION_hasSliceAccess = 3;
        static final int TRANSACTION_pinSlice = 1;
        static final int TRANSACTION_revokeSlicePermission = 9;
        static final int TRANSACTION_unpinSlice = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISliceManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISliceManager)) {
                return (ISliceManager) iin;
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
                    return "pinSlice";
                case 2:
                    return "unpinSlice";
                case 3:
                    return "hasSliceAccess";
                case 4:
                    return "getPinnedSpecs";
                case 5:
                    return "getPinnedSlices";
                case 6:
                    return "getBackupPayload";
                case 7:
                    return "applyRestore";
                case 8:
                    return "grantSlicePermission";
                case 9:
                    return "revokeSlicePermission";
                case 10:
                    return "checkSlicePermission";
                case 11:
                    return "grantPermissionFromUser";
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
                            Uri _arg1 = (Uri) data.readTypedObject(Uri.CREATOR);
                            SliceSpec[] _arg2 = (SliceSpec[]) data.createTypedArray(SliceSpec.CREATOR);
                            IBinder _arg3 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            pinSlice(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            Uri _arg12 = (Uri) data.readTypedObject(Uri.CREATOR);
                            IBinder _arg22 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            unpinSlice(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = hasSliceAccess(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 4:
                            Uri _arg04 = (Uri) data.readTypedObject(Uri.CREATOR);
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            SliceSpec[] _result2 = getPinnedSpecs(_arg04, _arg13);
                            reply.writeNoException();
                            reply.writeTypedArray(_result2, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            Uri[] _result3 = getPinnedSlices(_arg05);
                            reply.writeNoException();
                            reply.writeTypedArray(_result3, 1);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            byte[] _result4 = getBackupPayload(_arg06);
                            reply.writeNoException();
                            reply.writeByteArray(_result4);
                            break;
                        case 7:
                            byte[] _arg07 = data.createByteArray();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            applyRestore(_arg07, _arg14);
                            reply.writeNoException();
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            String _arg15 = data.readString();
                            Uri _arg23 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            grantSlicePermission(_arg08, _arg15, _arg23);
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            String _arg16 = data.readString();
                            Uri _arg24 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            revokeSlicePermission(_arg09, _arg16, _arg24);
                            reply.writeNoException();
                            break;
                        case 10:
                            Uri _arg010 = (Uri) data.readTypedObject(Uri.CREATOR);
                            String _arg17 = data.readString();
                            int _arg25 = data.readInt();
                            int _arg32 = data.readInt();
                            String[] _arg4 = data.createStringArray();
                            data.enforceNoDataAvail();
                            int _result5 = checkSlicePermission(_arg010, _arg17, _arg25, _arg32, _arg4);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 11:
                            Uri _arg011 = (Uri) data.readTypedObject(Uri.CREATOR);
                            String _arg18 = data.readString();
                            String _arg26 = data.readString();
                            boolean _arg33 = data.readBoolean();
                            data.enforceNoDataAvail();
                            grantPermissionFromUser(_arg011, _arg18, _arg26, _arg33);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISliceManager {
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

            @Override // android.app.slice.ISliceManager
            public void pinSlice(String pkg, Uri uri, SliceSpec[] specs, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeTypedObject(uri, 0);
                    _data.writeTypedArray(specs, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.slice.ISliceManager
            public void unpinSlice(String pkg, Uri uri, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeTypedObject(uri, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.slice.ISliceManager
            public boolean hasSliceAccess(String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.slice.ISliceManager
            public SliceSpec[] getPinnedSpecs(Uri uri, String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(uri, 0);
                    _data.writeString(pkg);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    SliceSpec[] _result = (SliceSpec[]) _reply.createTypedArray(SliceSpec.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.slice.ISliceManager
            public Uri[] getPinnedSlices(String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    Uri[] _result = (Uri[]) _reply.createTypedArray(Uri.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.slice.ISliceManager
            public byte[] getBackupPayload(int user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(user);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.slice.ISliceManager
            public void applyRestore(byte[] payload, int user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(payload);
                    _data.writeInt(user);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.slice.ISliceManager
            public void grantSlicePermission(String callingPkg, String toPkg, Uri uri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeString(toPkg);
                    _data.writeTypedObject(uri, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.slice.ISliceManager
            public void revokeSlicePermission(String callingPkg, String toPkg, Uri uri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeString(toPkg);
                    _data.writeTypedObject(uri, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.slice.ISliceManager
            public int checkSlicePermission(Uri uri, String callingPkg, int pid, int uid, String[] autoGrantPermissions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(uri, 0);
                    _data.writeString(callingPkg);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeStringArray(autoGrantPermissions);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.slice.ISliceManager
            public void grantPermissionFromUser(Uri uri, String pkg, String callingPkg, boolean allSlices) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(uri, 0);
                    _data.writeString(pkg);
                    _data.writeString(callingPkg);
                    _data.writeBoolean(allSlices);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 10;
        }
    }
}
