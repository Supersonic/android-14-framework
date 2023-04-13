package android.permission;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ILegacyPermissionManager extends IInterface {
    public static final String DESCRIPTOR = "android.permission.ILegacyPermissionManager";

    int checkDeviceIdentifierAccess(String str, String str2, String str3, int i, int i2) throws RemoteException;

    int checkPhoneNumberAccess(String str, String str2, String str3, int i, int i2) throws RemoteException;

    void grantDefaultPermissionsToActiveLuiApp(String str, int i) throws RemoteException;

    void grantDefaultPermissionsToCarrierServiceApp(String str, int i) throws RemoteException;

    void grantDefaultPermissionsToEnabledCarrierApps(String[] strArr, int i) throws RemoteException;

    void grantDefaultPermissionsToEnabledImsServices(String[] strArr, int i) throws RemoteException;

    void grantDefaultPermissionsToEnabledTelephonyDataServices(String[] strArr, int i) throws RemoteException;

    void revokeDefaultPermissionsFromDisabledTelephonyDataServices(String[] strArr, int i) throws RemoteException;

    void revokeDefaultPermissionsFromLuiApps(String[] strArr, int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ILegacyPermissionManager {
        @Override // android.permission.ILegacyPermissionManager
        public int checkDeviceIdentifierAccess(String packageName, String message, String callingFeatureId, int pid, int uid) throws RemoteException {
            return 0;
        }

        @Override // android.permission.ILegacyPermissionManager
        public int checkPhoneNumberAccess(String packageName, String message, String callingFeatureId, int pid, int uid) throws RemoteException {
            return 0;
        }

        @Override // android.permission.ILegacyPermissionManager
        public void grantDefaultPermissionsToEnabledCarrierApps(String[] packageNames, int userId) throws RemoteException {
        }

        @Override // android.permission.ILegacyPermissionManager
        public void grantDefaultPermissionsToEnabledImsServices(String[] packageNames, int userId) throws RemoteException {
        }

        @Override // android.permission.ILegacyPermissionManager
        public void grantDefaultPermissionsToEnabledTelephonyDataServices(String[] packageNames, int userId) throws RemoteException {
        }

        @Override // android.permission.ILegacyPermissionManager
        public void revokeDefaultPermissionsFromDisabledTelephonyDataServices(String[] packageNames, int userId) throws RemoteException {
        }

        @Override // android.permission.ILegacyPermissionManager
        public void grantDefaultPermissionsToActiveLuiApp(String packageName, int userId) throws RemoteException {
        }

        @Override // android.permission.ILegacyPermissionManager
        public void revokeDefaultPermissionsFromLuiApps(String[] packageNames, int userId) throws RemoteException {
        }

        @Override // android.permission.ILegacyPermissionManager
        public void grantDefaultPermissionsToCarrierServiceApp(String packageName, int userId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ILegacyPermissionManager {
        static final int TRANSACTION_checkDeviceIdentifierAccess = 1;
        static final int TRANSACTION_checkPhoneNumberAccess = 2;
        static final int TRANSACTION_grantDefaultPermissionsToActiveLuiApp = 7;
        static final int TRANSACTION_grantDefaultPermissionsToCarrierServiceApp = 9;
        static final int TRANSACTION_grantDefaultPermissionsToEnabledCarrierApps = 3;
        static final int TRANSACTION_grantDefaultPermissionsToEnabledImsServices = 4;

        /* renamed from: TRANSACTION_grantDefaultPermissionsToEnabledTelephonyDataServices */
        static final int f332x5a100098 = 5;

        /* renamed from: TRANSACTION_revokeDefaultPermissionsFromDisabledTelephonyDataServices */
        static final int f333x26be1bd0 = 6;
        static final int TRANSACTION_revokeDefaultPermissionsFromLuiApps = 8;

        public Stub() {
            attachInterface(this, ILegacyPermissionManager.DESCRIPTOR);
        }

        public static ILegacyPermissionManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ILegacyPermissionManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ILegacyPermissionManager)) {
                return (ILegacyPermissionManager) iin;
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
                    return "checkDeviceIdentifierAccess";
                case 2:
                    return "checkPhoneNumberAccess";
                case 3:
                    return "grantDefaultPermissionsToEnabledCarrierApps";
                case 4:
                    return "grantDefaultPermissionsToEnabledImsServices";
                case 5:
                    return "grantDefaultPermissionsToEnabledTelephonyDataServices";
                case 6:
                    return "revokeDefaultPermissionsFromDisabledTelephonyDataServices";
                case 7:
                    return "grantDefaultPermissionsToActiveLuiApp";
                case 8:
                    return "revokeDefaultPermissionsFromLuiApps";
                case 9:
                    return "grantDefaultPermissionsToCarrierServiceApp";
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
                data.enforceInterface(ILegacyPermissionManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ILegacyPermissionManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result = checkDeviceIdentifierAccess(_arg0, _arg1, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            String _arg12 = data.readString();
                            String _arg22 = data.readString();
                            int _arg32 = data.readInt();
                            int _arg42 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result2 = checkPhoneNumberAccess(_arg02, _arg12, _arg22, _arg32, _arg42);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            String[] _arg03 = data.createStringArray();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            grantDefaultPermissionsToEnabledCarrierApps(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        case 4:
                            String[] _arg04 = data.createStringArray();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            grantDefaultPermissionsToEnabledImsServices(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 5:
                            String[] _arg05 = data.createStringArray();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            grantDefaultPermissionsToEnabledTelephonyDataServices(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 6:
                            String[] _arg06 = data.createStringArray();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            revokeDefaultPermissionsFromDisabledTelephonyDataServices(_arg06, _arg16);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            grantDefaultPermissionsToActiveLuiApp(_arg07, _arg17);
                            reply.writeNoException();
                            break;
                        case 8:
                            String[] _arg08 = data.createStringArray();
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            revokeDefaultPermissionsFromLuiApps(_arg08, _arg18);
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            grantDefaultPermissionsToCarrierServiceApp(_arg09, _arg19);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ILegacyPermissionManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ILegacyPermissionManager.DESCRIPTOR;
            }

            @Override // android.permission.ILegacyPermissionManager
            public int checkDeviceIdentifierAccess(String packageName, String message, String callingFeatureId, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILegacyPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(message);
                    _data.writeString(callingFeatureId);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.ILegacyPermissionManager
            public int checkPhoneNumberAccess(String packageName, String message, String callingFeatureId, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILegacyPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(message);
                    _data.writeString(callingFeatureId);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.ILegacyPermissionManager
            public void grantDefaultPermissionsToEnabledCarrierApps(String[] packageNames, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILegacyPermissionManager.DESCRIPTOR);
                    _data.writeStringArray(packageNames);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.ILegacyPermissionManager
            public void grantDefaultPermissionsToEnabledImsServices(String[] packageNames, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILegacyPermissionManager.DESCRIPTOR);
                    _data.writeStringArray(packageNames);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.ILegacyPermissionManager
            public void grantDefaultPermissionsToEnabledTelephonyDataServices(String[] packageNames, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILegacyPermissionManager.DESCRIPTOR);
                    _data.writeStringArray(packageNames);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.ILegacyPermissionManager
            public void revokeDefaultPermissionsFromDisabledTelephonyDataServices(String[] packageNames, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILegacyPermissionManager.DESCRIPTOR);
                    _data.writeStringArray(packageNames);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.ILegacyPermissionManager
            public void grantDefaultPermissionsToActiveLuiApp(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILegacyPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.ILegacyPermissionManager
            public void revokeDefaultPermissionsFromLuiApps(String[] packageNames, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILegacyPermissionManager.DESCRIPTOR);
                    _data.writeStringArray(packageNames);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.ILegacyPermissionManager
            public void grantDefaultPermissionsToCarrierServiceApp(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ILegacyPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
