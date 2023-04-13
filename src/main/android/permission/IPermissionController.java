package android.permission;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import com.android.internal.infra.AndroidFuture;
import java.util.List;
/* loaded from: classes3.dex */
public interface IPermissionController extends IInterface {
    public static final String DESCRIPTOR = "android.permission.IPermissionController";

    void applyStagedRuntimePermissionBackup(String str, UserHandle userHandle, AndroidFuture androidFuture) throws RemoteException;

    void countPermissionApps(List<String> list, int i, AndroidFuture androidFuture) throws RemoteException;

    void getAppPermissions(String str, AndroidFuture androidFuture) throws RemoteException;

    void getGroupOfPlatformPermission(String str, AndroidFuture<String> androidFuture) throws RemoteException;

    void getHibernationEligibility(String str, AndroidFuture androidFuture) throws RemoteException;

    void getPermissionUsages(boolean z, long j, AndroidFuture androidFuture) throws RemoteException;

    void getPlatformPermissionsForGroup(String str, AndroidFuture<List<String>> androidFuture) throws RemoteException;

    void getPrivilegesDescriptionStringForProfile(String str, AndroidFuture<String> androidFuture) throws RemoteException;

    void getRuntimePermissionBackup(UserHandle userHandle, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    void getUnusedAppCount(AndroidFuture androidFuture) throws RemoteException;

    void grantOrUpgradeDefaultRuntimePermissions(AndroidFuture androidFuture) throws RemoteException;

    void notifyOneTimePermissionSessionTimeout(String str) throws RemoteException;

    void revokeRuntimePermission(String str, String str2) throws RemoteException;

    void revokeRuntimePermissions(Bundle bundle, boolean z, int i, String str, AndroidFuture androidFuture) throws RemoteException;

    void revokeSelfPermissionsOnKill(String str, List<String> list, AndroidFuture androidFuture) throws RemoteException;

    void setRuntimePermissionGrantStateByDeviceAdminFromParams(String str, AdminPermissionControlParams adminPermissionControlParams, AndroidFuture androidFuture) throws RemoteException;

    void stageAndApplyRuntimePermissionsBackup(UserHandle userHandle, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    void updateUserSensitiveForApp(int i, AndroidFuture androidFuture) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IPermissionController {
        @Override // android.permission.IPermissionController
        public void revokeRuntimePermissions(Bundle request, boolean doDryRun, int reason, String callerPackageName, AndroidFuture callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void getRuntimePermissionBackup(UserHandle user, ParcelFileDescriptor pipe) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void stageAndApplyRuntimePermissionsBackup(UserHandle user, ParcelFileDescriptor pipe) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void applyStagedRuntimePermissionBackup(String packageName, UserHandle user, AndroidFuture callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void getAppPermissions(String packageName, AndroidFuture callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void revokeRuntimePermission(String packageName, String permissionName) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void countPermissionApps(List<String> permissionNames, int flags, AndroidFuture callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void getPermissionUsages(boolean countSystem, long numMillis, AndroidFuture callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void setRuntimePermissionGrantStateByDeviceAdminFromParams(String callerPackageName, AdminPermissionControlParams params, AndroidFuture callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void grantOrUpgradeDefaultRuntimePermissions(AndroidFuture callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void notifyOneTimePermissionSessionTimeout(String packageName) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void updateUserSensitiveForApp(int uid, AndroidFuture callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void getPrivilegesDescriptionStringForProfile(String deviceProfileName, AndroidFuture<String> callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void getPlatformPermissionsForGroup(String permissionGroupName, AndroidFuture<List<String>> callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void getGroupOfPlatformPermission(String permissionName, AndroidFuture<String> callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void getUnusedAppCount(AndroidFuture callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void getHibernationEligibility(String packageName, AndroidFuture callback) throws RemoteException {
        }

        @Override // android.permission.IPermissionController
        public void revokeSelfPermissionsOnKill(String packageName, List<String> permissions, AndroidFuture callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPermissionController {
        static final int TRANSACTION_applyStagedRuntimePermissionBackup = 4;
        static final int TRANSACTION_countPermissionApps = 7;
        static final int TRANSACTION_getAppPermissions = 5;
        static final int TRANSACTION_getGroupOfPlatformPermission = 15;
        static final int TRANSACTION_getHibernationEligibility = 17;
        static final int TRANSACTION_getPermissionUsages = 8;
        static final int TRANSACTION_getPlatformPermissionsForGroup = 14;
        static final int TRANSACTION_getPrivilegesDescriptionStringForProfile = 13;
        static final int TRANSACTION_getRuntimePermissionBackup = 2;
        static final int TRANSACTION_getUnusedAppCount = 16;
        static final int TRANSACTION_grantOrUpgradeDefaultRuntimePermissions = 10;
        static final int TRANSACTION_notifyOneTimePermissionSessionTimeout = 11;
        static final int TRANSACTION_revokeRuntimePermission = 6;
        static final int TRANSACTION_revokeRuntimePermissions = 1;
        static final int TRANSACTION_revokeSelfPermissionsOnKill = 18;

        /* renamed from: TRANSACTION_setRuntimePermissionGrantStateByDeviceAdminFromParams */
        static final int f334x42c91ad7 = 9;
        static final int TRANSACTION_stageAndApplyRuntimePermissionsBackup = 3;
        static final int TRANSACTION_updateUserSensitiveForApp = 12;

        public Stub() {
            attachInterface(this, IPermissionController.DESCRIPTOR);
        }

        public static IPermissionController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPermissionController.DESCRIPTOR);
            if (iin != null && (iin instanceof IPermissionController)) {
                return (IPermissionController) iin;
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
                    return "revokeRuntimePermissions";
                case 2:
                    return "getRuntimePermissionBackup";
                case 3:
                    return "stageAndApplyRuntimePermissionsBackup";
                case 4:
                    return "applyStagedRuntimePermissionBackup";
                case 5:
                    return "getAppPermissions";
                case 6:
                    return "revokeRuntimePermission";
                case 7:
                    return "countPermissionApps";
                case 8:
                    return "getPermissionUsages";
                case 9:
                    return "setRuntimePermissionGrantStateByDeviceAdminFromParams";
                case 10:
                    return "grantOrUpgradeDefaultRuntimePermissions";
                case 11:
                    return "notifyOneTimePermissionSessionTimeout";
                case 12:
                    return "updateUserSensitiveForApp";
                case 13:
                    return "getPrivilegesDescriptionStringForProfile";
                case 14:
                    return "getPlatformPermissionsForGroup";
                case 15:
                    return "getGroupOfPlatformPermission";
                case 16:
                    return "getUnusedAppCount";
                case 17:
                    return "getHibernationEligibility";
                case 18:
                    return "revokeSelfPermissionsOnKill";
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
                data.enforceInterface(IPermissionController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPermissionController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            Bundle _arg0 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            boolean _arg1 = data.readBoolean();
                            int _arg2 = data.readInt();
                            String _arg3 = data.readString();
                            AndroidFuture _arg4 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            revokeRuntimePermissions(_arg0, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 2:
                            UserHandle _arg02 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            ParcelFileDescriptor _arg12 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            getRuntimePermissionBackup(_arg02, _arg12);
                            break;
                        case 3:
                            UserHandle _arg03 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            ParcelFileDescriptor _arg13 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            stageAndApplyRuntimePermissionsBackup(_arg03, _arg13);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            UserHandle _arg14 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            AndroidFuture _arg22 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            applyStagedRuntimePermissionBackup(_arg04, _arg14, _arg22);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            AndroidFuture _arg15 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getAppPermissions(_arg05, _arg15);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            String _arg16 = data.readString();
                            data.enforceNoDataAvail();
                            revokeRuntimePermission(_arg06, _arg16);
                            break;
                        case 7:
                            List<String> _arg07 = data.createStringArrayList();
                            int _arg17 = data.readInt();
                            AndroidFuture _arg23 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            countPermissionApps(_arg07, _arg17, _arg23);
                            break;
                        case 8:
                            boolean _arg08 = data.readBoolean();
                            long _arg18 = data.readLong();
                            AndroidFuture _arg24 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getPermissionUsages(_arg08, _arg18, _arg24);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            AdminPermissionControlParams _arg19 = (AdminPermissionControlParams) data.readTypedObject(AdminPermissionControlParams.CREATOR);
                            AndroidFuture _arg25 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            setRuntimePermissionGrantStateByDeviceAdminFromParams(_arg09, _arg19, _arg25);
                            break;
                        case 10:
                            AndroidFuture _arg010 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            grantOrUpgradeDefaultRuntimePermissions(_arg010);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            data.enforceNoDataAvail();
                            notifyOneTimePermissionSessionTimeout(_arg011);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            AndroidFuture _arg110 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            updateUserSensitiveForApp(_arg012, _arg110);
                            break;
                        case 13:
                            String _arg013 = data.readString();
                            AndroidFuture<String> _arg111 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getPrivilegesDescriptionStringForProfile(_arg013, _arg111);
                            break;
                        case 14:
                            String _arg014 = data.readString();
                            AndroidFuture<List<String>> _arg112 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getPlatformPermissionsForGroup(_arg014, _arg112);
                            break;
                        case 15:
                            String _arg015 = data.readString();
                            AndroidFuture<String> _arg113 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getGroupOfPlatformPermission(_arg015, _arg113);
                            break;
                        case 16:
                            AndroidFuture _arg016 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getUnusedAppCount(_arg016);
                            break;
                        case 17:
                            String _arg017 = data.readString();
                            AndroidFuture _arg114 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getHibernationEligibility(_arg017, _arg114);
                            break;
                        case 18:
                            String _arg018 = data.readString();
                            List<String> _arg115 = data.createStringArrayList();
                            AndroidFuture _arg26 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            revokeSelfPermissionsOnKill(_arg018, _arg115, _arg26);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IPermissionController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPermissionController.DESCRIPTOR;
            }

            @Override // android.permission.IPermissionController
            public void revokeRuntimePermissions(Bundle request, boolean doDryRun, int reason, String callerPackageName, AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeBoolean(doDryRun);
                    _data.writeInt(reason);
                    _data.writeString(callerPackageName);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void getRuntimePermissionBackup(UserHandle user, ParcelFileDescriptor pipe) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeTypedObject(pipe, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void stageAndApplyRuntimePermissionsBackup(UserHandle user, ParcelFileDescriptor pipe) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeTypedObject(pipe, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void applyStagedRuntimePermissionBackup(String packageName, UserHandle user, AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(user, 0);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void getAppPermissions(String packageName, AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void revokeRuntimePermission(String packageName, String permissionName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void countPermissionApps(List<String> permissionNames, int flags, AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeStringList(permissionNames);
                    _data.writeInt(flags);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void getPermissionUsages(boolean countSystem, long numMillis, AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeBoolean(countSystem);
                    _data.writeLong(numMillis);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void setRuntimePermissionGrantStateByDeviceAdminFromParams(String callerPackageName, AdminPermissionControlParams params, AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeString(callerPackageName);
                    _data.writeTypedObject(params, 0);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void grantOrUpgradeDefaultRuntimePermissions(AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void notifyOneTimePermissionSessionTimeout(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void updateUserSensitiveForApp(int uid, AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void getPrivilegesDescriptionStringForProfile(String deviceProfileName, AndroidFuture<String> callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeString(deviceProfileName);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void getPlatformPermissionsForGroup(String permissionGroupName, AndroidFuture<List<String>> callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeString(permissionGroupName);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void getGroupOfPlatformPermission(String permissionName, AndroidFuture<String> callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeString(permissionName);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void getUnusedAppCount(AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void getHibernationEligibility(String packageName, AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionController
            public void revokeSelfPermissionsOnKill(String packageName, List<String> permissions, AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPermissionController.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStringList(permissions);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 17;
        }
    }
}
