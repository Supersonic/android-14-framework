package android.permission;

import android.Manifest;
import android.app.ActivityThread;
import android.content.AttributionSource;
import android.content.AttributionSourceState;
import android.content.p001pm.ParceledListSlice;
import android.content.p001pm.PermissionGroupInfo;
import android.content.p001pm.PermissionInfo;
import android.content.p001pm.permission.SplitPermissionInfoParcelable;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import android.permission.IOnPermissionsChangeListener;
import java.util.List;
/* loaded from: classes3.dex */
public interface IPermissionManager extends IInterface {
    public static final String DESCRIPTOR = "android.permission.IPermissionManager";

    boolean addAllowlistedRestrictedPermission(String str, String str2, int i, int i2) throws RemoteException;

    void addOnPermissionsChangeListener(IOnPermissionsChangeListener iOnPermissionsChangeListener) throws RemoteException;

    boolean addPermission(PermissionInfo permissionInfo, boolean z) throws RemoteException;

    ParceledListSlice getAllPermissionGroups(int i) throws RemoteException;

    List<String> getAllowlistedRestrictedPermissions(String str, int i, int i2) throws RemoteException;

    List<String> getAutoRevokeExemptionGrantedPackages(int i) throws RemoteException;

    List<String> getAutoRevokeExemptionRequestedPackages(int i) throws RemoteException;

    int getPermissionFlags(String str, String str2, int i) throws RemoteException;

    PermissionGroupInfo getPermissionGroupInfo(String str, int i) throws RemoteException;

    PermissionInfo getPermissionInfo(String str, String str2, int i) throws RemoteException;

    List<SplitPermissionInfoParcelable> getSplitPermissions() throws RemoteException;

    void grantRuntimePermission(String str, String str2, int i) throws RemoteException;

    boolean isAutoRevokeExempted(String str, int i) throws RemoteException;

    boolean isPermissionRevokedByPolicy(String str, String str2, int i) throws RemoteException;

    boolean isRegisteredAttributionSource(AttributionSourceState attributionSourceState) throws RemoteException;

    ParceledListSlice queryPermissionsByGroup(String str, int i) throws RemoteException;

    void registerAttributionSource(AttributionSourceState attributionSourceState) throws RemoteException;

    boolean removeAllowlistedRestrictedPermission(String str, String str2, int i, int i2) throws RemoteException;

    void removeOnPermissionsChangeListener(IOnPermissionsChangeListener iOnPermissionsChangeListener) throws RemoteException;

    void removePermission(String str) throws RemoteException;

    void revokePostNotificationPermissionWithoutKillForTest(String str, int i) throws RemoteException;

    void revokeRuntimePermission(String str, String str2, int i, String str3) throws RemoteException;

    boolean setAutoRevokeExempted(String str, boolean z, int i) throws RemoteException;

    boolean shouldShowRequestPermissionRationale(String str, String str2, int i) throws RemoteException;

    void startOneTimePermissionSession(String str, int i, long j, long j2) throws RemoteException;

    void stopOneTimePermissionSession(String str, int i) throws RemoteException;

    void updatePermissionFlags(String str, String str2, int i, int i2, boolean z, int i3) throws RemoteException;

    void updatePermissionFlagsForAllApps(int i, int i2, int i3) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IPermissionManager {
        @Override // android.permission.IPermissionManager
        public ParceledListSlice getAllPermissionGroups(int flags) throws RemoteException {
            return null;
        }

        @Override // android.permission.IPermissionManager
        public PermissionGroupInfo getPermissionGroupInfo(String groupName, int flags) throws RemoteException {
            return null;
        }

        @Override // android.permission.IPermissionManager
        public PermissionInfo getPermissionInfo(String permissionName, String packageName, int flags) throws RemoteException {
            return null;
        }

        @Override // android.permission.IPermissionManager
        public ParceledListSlice queryPermissionsByGroup(String groupName, int flags) throws RemoteException {
            return null;
        }

        @Override // android.permission.IPermissionManager
        public boolean addPermission(PermissionInfo permissionInfo, boolean async) throws RemoteException {
            return false;
        }

        @Override // android.permission.IPermissionManager
        public void removePermission(String permissionName) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public int getPermissionFlags(String packageName, String permissionName, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.permission.IPermissionManager
        public void updatePermissionFlags(String packageName, String permissionName, int flagMask, int flagValues, boolean checkAdjustPolicyFlagPermission, int userId) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public void updatePermissionFlagsForAllApps(int flagMask, int flagValues, int userId) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public void addOnPermissionsChangeListener(IOnPermissionsChangeListener listener) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public void removeOnPermissionsChangeListener(IOnPermissionsChangeListener listener) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public List<String> getAllowlistedRestrictedPermissions(String packageName, int flags, int userId) throws RemoteException {
            return null;
        }

        @Override // android.permission.IPermissionManager
        public boolean addAllowlistedRestrictedPermission(String packageName, String permissionName, int flags, int userId) throws RemoteException {
            return false;
        }

        @Override // android.permission.IPermissionManager
        public boolean removeAllowlistedRestrictedPermission(String packageName, String permissionName, int flags, int userId) throws RemoteException {
            return false;
        }

        @Override // android.permission.IPermissionManager
        public void grantRuntimePermission(String packageName, String permissionName, int userId) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public void revokeRuntimePermission(String packageName, String permissionName, int userId, String reason) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public void revokePostNotificationPermissionWithoutKillForTest(String packageName, int userId) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public boolean shouldShowRequestPermissionRationale(String packageName, String permissionName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.permission.IPermissionManager
        public boolean isPermissionRevokedByPolicy(String packageName, String permissionName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.permission.IPermissionManager
        public List<SplitPermissionInfoParcelable> getSplitPermissions() throws RemoteException {
            return null;
        }

        @Override // android.permission.IPermissionManager
        public void startOneTimePermissionSession(String packageName, int userId, long timeout, long revokeAfterKilledDelay) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public void stopOneTimePermissionSession(String packageName, int userId) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public List<String> getAutoRevokeExemptionRequestedPackages(int userId) throws RemoteException {
            return null;
        }

        @Override // android.permission.IPermissionManager
        public List<String> getAutoRevokeExemptionGrantedPackages(int userId) throws RemoteException {
            return null;
        }

        @Override // android.permission.IPermissionManager
        public boolean setAutoRevokeExempted(String packageName, boolean exempted, int userId) throws RemoteException {
            return false;
        }

        @Override // android.permission.IPermissionManager
        public boolean isAutoRevokeExempted(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.permission.IPermissionManager
        public void registerAttributionSource(AttributionSourceState source) throws RemoteException {
        }

        @Override // android.permission.IPermissionManager
        public boolean isRegisteredAttributionSource(AttributionSourceState source) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPermissionManager {
        static final int TRANSACTION_addAllowlistedRestrictedPermission = 13;
        static final int TRANSACTION_addOnPermissionsChangeListener = 10;
        static final int TRANSACTION_addPermission = 5;
        static final int TRANSACTION_getAllPermissionGroups = 1;
        static final int TRANSACTION_getAllowlistedRestrictedPermissions = 12;
        static final int TRANSACTION_getAutoRevokeExemptionGrantedPackages = 24;
        static final int TRANSACTION_getAutoRevokeExemptionRequestedPackages = 23;
        static final int TRANSACTION_getPermissionFlags = 7;
        static final int TRANSACTION_getPermissionGroupInfo = 2;
        static final int TRANSACTION_getPermissionInfo = 3;
        static final int TRANSACTION_getSplitPermissions = 20;
        static final int TRANSACTION_grantRuntimePermission = 15;
        static final int TRANSACTION_isAutoRevokeExempted = 26;
        static final int TRANSACTION_isPermissionRevokedByPolicy = 19;
        static final int TRANSACTION_isRegisteredAttributionSource = 28;
        static final int TRANSACTION_queryPermissionsByGroup = 4;
        static final int TRANSACTION_registerAttributionSource = 27;
        static final int TRANSACTION_removeAllowlistedRestrictedPermission = 14;
        static final int TRANSACTION_removeOnPermissionsChangeListener = 11;
        static final int TRANSACTION_removePermission = 6;
        static final int TRANSACTION_revokePostNotificationPermissionWithoutKillForTest = 17;
        static final int TRANSACTION_revokeRuntimePermission = 16;
        static final int TRANSACTION_setAutoRevokeExempted = 25;
        static final int TRANSACTION_shouldShowRequestPermissionRationale = 18;
        static final int TRANSACTION_startOneTimePermissionSession = 21;
        static final int TRANSACTION_stopOneTimePermissionSession = 22;
        static final int TRANSACTION_updatePermissionFlags = 8;
        static final int TRANSACTION_updatePermissionFlagsForAllApps = 9;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, IPermissionManager.DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IPermissionManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPermissionManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IPermissionManager)) {
                return (IPermissionManager) iin;
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
                    return "getAllPermissionGroups";
                case 2:
                    return "getPermissionGroupInfo";
                case 3:
                    return "getPermissionInfo";
                case 4:
                    return "queryPermissionsByGroup";
                case 5:
                    return "addPermission";
                case 6:
                    return "removePermission";
                case 7:
                    return "getPermissionFlags";
                case 8:
                    return "updatePermissionFlags";
                case 9:
                    return "updatePermissionFlagsForAllApps";
                case 10:
                    return "addOnPermissionsChangeListener";
                case 11:
                    return "removeOnPermissionsChangeListener";
                case 12:
                    return "getAllowlistedRestrictedPermissions";
                case 13:
                    return "addAllowlistedRestrictedPermission";
                case 14:
                    return "removeAllowlistedRestrictedPermission";
                case 15:
                    return "grantRuntimePermission";
                case 16:
                    return "revokeRuntimePermission";
                case 17:
                    return "revokePostNotificationPermissionWithoutKillForTest";
                case 18:
                    return "shouldShowRequestPermissionRationale";
                case 19:
                    return "isPermissionRevokedByPolicy";
                case 20:
                    return "getSplitPermissions";
                case 21:
                    return "startOneTimePermissionSession";
                case 22:
                    return "stopOneTimePermissionSession";
                case 23:
                    return "getAutoRevokeExemptionRequestedPackages";
                case 24:
                    return "getAutoRevokeExemptionGrantedPackages";
                case 25:
                    return "setAutoRevokeExempted";
                case 26:
                    return "isAutoRevokeExempted";
                case 27:
                    return "registerAttributionSource";
                case 28:
                    return "isRegisteredAttributionSource";
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
                data.enforceInterface(IPermissionManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPermissionManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result = getAllPermissionGroups(_arg0);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            PermissionGroupInfo _result2 = getPermissionGroupInfo(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg12 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            PermissionInfo _result3 = getPermissionInfo(_arg03, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result4 = queryPermissionsByGroup(_arg04, _arg13);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            PermissionInfo _arg05 = (PermissionInfo) data.readTypedObject(PermissionInfo.CREATOR);
                            boolean _arg14 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result5 = addPermission(_arg05, _arg14);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            data.enforceNoDataAvail();
                            removePermission(_arg06);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            String _arg15 = data.readString();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result6 = getPermissionFlags(_arg07, _arg15, _arg22);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            String _arg16 = data.readString();
                            int _arg23 = data.readInt();
                            int _arg3 = data.readInt();
                            boolean _arg4 = data.readBoolean();
                            int _arg5 = data.readInt();
                            data.enforceNoDataAvail();
                            updatePermissionFlags(_arg08, _arg16, _arg23, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            int _arg17 = data.readInt();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            updatePermissionFlagsForAllApps(_arg09, _arg17, _arg24);
                            reply.writeNoException();
                            break;
                        case 10:
                            IOnPermissionsChangeListener _arg010 = IOnPermissionsChangeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addOnPermissionsChangeListener(_arg010);
                            reply.writeNoException();
                            break;
                        case 11:
                            IOnPermissionsChangeListener _arg011 = IOnPermissionsChangeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeOnPermissionsChangeListener(_arg011);
                            reply.writeNoException();
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            int _arg18 = data.readInt();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result7 = getAllowlistedRestrictedPermissions(_arg012, _arg18, _arg25);
                            reply.writeNoException();
                            reply.writeStringList(_result7);
                            break;
                        case 13:
                            String _arg013 = data.readString();
                            String _arg19 = data.readString();
                            int _arg26 = data.readInt();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result8 = addAllowlistedRestrictedPermission(_arg013, _arg19, _arg26, _arg32);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 14:
                            String _arg014 = data.readString();
                            String _arg110 = data.readString();
                            int _arg27 = data.readInt();
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result9 = removeAllowlistedRestrictedPermission(_arg014, _arg110, _arg27, _arg33);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 15:
                            String _arg015 = data.readString();
                            String _arg111 = data.readString();
                            int _arg28 = data.readInt();
                            data.enforceNoDataAvail();
                            grantRuntimePermission(_arg015, _arg111, _arg28);
                            reply.writeNoException();
                            break;
                        case 16:
                            String _arg016 = data.readString();
                            String _arg112 = data.readString();
                            int _arg29 = data.readInt();
                            String _arg34 = data.readString();
                            data.enforceNoDataAvail();
                            revokeRuntimePermission(_arg016, _arg112, _arg29, _arg34);
                            reply.writeNoException();
                            break;
                        case 17:
                            String _arg017 = data.readString();
                            int _arg113 = data.readInt();
                            data.enforceNoDataAvail();
                            revokePostNotificationPermissionWithoutKillForTest(_arg017, _arg113);
                            reply.writeNoException();
                            break;
                        case 18:
                            String _arg018 = data.readString();
                            String _arg114 = data.readString();
                            int _arg210 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result10 = shouldShowRequestPermissionRationale(_arg018, _arg114, _arg210);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 19:
                            String _arg019 = data.readString();
                            String _arg115 = data.readString();
                            int _arg211 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result11 = isPermissionRevokedByPolicy(_arg019, _arg115, _arg211);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 20:
                            List<SplitPermissionInfoParcelable> _result12 = getSplitPermissions();
                            reply.writeNoException();
                            reply.writeTypedList(_result12, 1);
                            break;
                        case 21:
                            String _arg020 = data.readString();
                            int _arg116 = data.readInt();
                            long _arg212 = data.readLong();
                            long _arg35 = data.readLong();
                            data.enforceNoDataAvail();
                            startOneTimePermissionSession(_arg020, _arg116, _arg212, _arg35);
                            reply.writeNoException();
                            break;
                        case 22:
                            String _arg021 = data.readString();
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            stopOneTimePermissionSession(_arg021, _arg117);
                            reply.writeNoException();
                            break;
                        case 23:
                            int _arg022 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result13 = getAutoRevokeExemptionRequestedPackages(_arg022);
                            reply.writeNoException();
                            reply.writeStringList(_result13);
                            break;
                        case 24:
                            int _arg023 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result14 = getAutoRevokeExemptionGrantedPackages(_arg023);
                            reply.writeNoException();
                            reply.writeStringList(_result14);
                            break;
                        case 25:
                            String _arg024 = data.readString();
                            boolean _arg118 = data.readBoolean();
                            int _arg213 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result15 = setAutoRevokeExempted(_arg024, _arg118, _arg213);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 26:
                            String _arg025 = data.readString();
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result16 = isAutoRevokeExempted(_arg025, _arg119);
                            reply.writeNoException();
                            reply.writeBoolean(_result16);
                            break;
                        case 27:
                            AttributionSourceState _arg026 = (AttributionSourceState) data.readTypedObject(AttributionSourceState.CREATOR);
                            data.enforceNoDataAvail();
                            registerAttributionSource(_arg026);
                            reply.writeNoException();
                            break;
                        case 28:
                            AttributionSourceState _arg027 = (AttributionSourceState) data.readTypedObject(AttributionSourceState.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result17 = isRegisteredAttributionSource(_arg027);
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IPermissionManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPermissionManager.DESCRIPTOR;
            }

            @Override // android.permission.IPermissionManager
            public ParceledListSlice getAllPermissionGroups(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public PermissionGroupInfo getPermissionGroupInfo(String groupName, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(groupName);
                    _data.writeInt(flags);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    PermissionGroupInfo _result = (PermissionGroupInfo) _reply.readTypedObject(PermissionGroupInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public PermissionInfo getPermissionInfo(String permissionName, String packageName, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(permissionName);
                    _data.writeString(packageName);
                    _data.writeInt(flags);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    PermissionInfo _result = (PermissionInfo) _reply.readTypedObject(PermissionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public ParceledListSlice queryPermissionsByGroup(String groupName, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(groupName);
                    _data.writeInt(flags);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public boolean addPermission(PermissionInfo permissionInfo, boolean async) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeTypedObject(permissionInfo, 0);
                    _data.writeBoolean(async);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void removePermission(String permissionName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(permissionName);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public int getPermissionFlags(String packageName, String permissionName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void updatePermissionFlags(String packageName, String permissionName, int flagMask, int flagValues, boolean checkAdjustPolicyFlagPermission, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    _data.writeInt(flagMask);
                    _data.writeInt(flagValues);
                    _data.writeBoolean(checkAdjustPolicyFlagPermission);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void updatePermissionFlagsForAllApps(int flagMask, int flagValues, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeInt(flagMask);
                    _data.writeInt(flagValues);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void addOnPermissionsChangeListener(IOnPermissionsChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void removeOnPermissionsChangeListener(IOnPermissionsChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public List<String> getAllowlistedRestrictedPermissions(String packageName, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public boolean addAllowlistedRestrictedPermission(String packageName, String permissionName, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public boolean removeAllowlistedRestrictedPermission(String packageName, String permissionName, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void grantRuntimePermission(String packageName, String permissionName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    _data.writeInt(userId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void revokeRuntimePermission(String packageName, String permissionName, int userId, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    _data.writeInt(userId);
                    _data.writeString(reason);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void revokePostNotificationPermissionWithoutKillForTest(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public boolean shouldShowRequestPermissionRationale(String packageName, String permissionName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    _data.writeInt(userId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public boolean isPermissionRevokedByPolicy(String packageName, String permissionName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    _data.writeInt(userId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public List<SplitPermissionInfoParcelable> getSplitPermissions() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    List<SplitPermissionInfoParcelable> _result = _reply.createTypedArrayList(SplitPermissionInfoParcelable.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void startOneTimePermissionSession(String packageName, int userId, long timeout, long revokeAfterKilledDelay) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeLong(timeout);
                    _data.writeLong(revokeAfterKilledDelay);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void stopOneTimePermissionSession(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public List<String> getAutoRevokeExemptionRequestedPackages(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public List<String> getAutoRevokeExemptionGrantedPackages(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public boolean setAutoRevokeExempted(String packageName, boolean exempted, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(exempted);
                    _data.writeInt(userId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public boolean isAutoRevokeExempted(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public void registerAttributionSource(AttributionSourceState source) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeTypedObject(source, 0);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionManager
            public boolean isRegisteredAttributionSource(AttributionSourceState source) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPermissionManager.DESCRIPTOR);
                    _data.writeTypedObject(source, 0);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void stopOneTimePermissionSession_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_ONE_TIME_PERMISSION_SESSIONS, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 27;
        }
    }
}
