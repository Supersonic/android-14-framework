package android.net;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import java.util.List;
/* loaded from: classes2.dex */
public interface IVpnManager extends IInterface {
    public static final String DESCRIPTOR = "android.net.IVpnManager";

    boolean addVpnAddress(String str, int i) throws RemoteException;

    void deleteVpnProfile(String str) throws RemoteException;

    ParcelFileDescriptor establishVpn(VpnConfig vpnConfig) throws RemoteException;

    void factoryReset() throws RemoteException;

    String getAlwaysOnVpnPackage(int i) throws RemoteException;

    List<String> getAppExclusionList(int i, String str) throws RemoteException;

    LegacyVpnInfo getLegacyVpnInfo(int i) throws RemoteException;

    VpnProfileState getProvisionedVpnProfileState(String str) throws RemoteException;

    VpnConfig getVpnConfig(int i) throws RemoteException;

    List<String> getVpnLockdownAllowlist(int i) throws RemoteException;

    boolean isAlwaysOnVpnPackageSupported(int i, String str) throws RemoteException;

    boolean isCallerCurrentAlwaysOnVpnApp() throws RemoteException;

    boolean isCallerCurrentAlwaysOnVpnLockdownApp() throws RemoteException;

    boolean isVpnLockdownEnabled(int i) throws RemoteException;

    boolean prepareVpn(String str, String str2, int i) throws RemoteException;

    boolean provisionVpnProfile(VpnProfile vpnProfile, String str) throws RemoteException;

    boolean removeVpnAddress(String str, int i) throws RemoteException;

    boolean setAlwaysOnVpnPackage(int i, String str, boolean z, List<String> list) throws RemoteException;

    boolean setAppExclusionList(int i, String str, List<String> list) throws RemoteException;

    boolean setUnderlyingNetworksForVpn(Network[] networkArr) throws RemoteException;

    void setVpnPackageAuthorization(String str, int i, int i2) throws RemoteException;

    void startLegacyVpn(VpnProfile vpnProfile) throws RemoteException;

    String startVpnProfile(String str) throws RemoteException;

    void stopVpnProfile(String str) throws RemoteException;

    boolean updateLockdownVpn() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IVpnManager {
        @Override // android.net.IVpnManager
        public boolean prepareVpn(String oldPackage, String newPackage, int userId) throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public void setVpnPackageAuthorization(String packageName, int userId, int vpnType) throws RemoteException {
        }

        @Override // android.net.IVpnManager
        public ParcelFileDescriptor establishVpn(VpnConfig config) throws RemoteException {
            return null;
        }

        @Override // android.net.IVpnManager
        public boolean addVpnAddress(String address, int prefixLength) throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public boolean removeVpnAddress(String address, int prefixLength) throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public boolean setUnderlyingNetworksForVpn(Network[] networks) throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public boolean provisionVpnProfile(VpnProfile profile, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public void deleteVpnProfile(String packageName) throws RemoteException {
        }

        @Override // android.net.IVpnManager
        public String startVpnProfile(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.net.IVpnManager
        public void stopVpnProfile(String packageName) throws RemoteException {
        }

        @Override // android.net.IVpnManager
        public VpnProfileState getProvisionedVpnProfileState(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.net.IVpnManager
        public boolean setAppExclusionList(int userId, String vpnPackage, List<String> excludedApps) throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public List<String> getAppExclusionList(int userId, String vpnPackage) throws RemoteException {
            return null;
        }

        @Override // android.net.IVpnManager
        public boolean isAlwaysOnVpnPackageSupported(int userId, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public boolean setAlwaysOnVpnPackage(int userId, String packageName, boolean lockdown, List<String> lockdownAllowlist) throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public String getAlwaysOnVpnPackage(int userId) throws RemoteException {
            return null;
        }

        @Override // android.net.IVpnManager
        public boolean isVpnLockdownEnabled(int userId) throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public List<String> getVpnLockdownAllowlist(int userId) throws RemoteException {
            return null;
        }

        @Override // android.net.IVpnManager
        public boolean isCallerCurrentAlwaysOnVpnApp() throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public boolean isCallerCurrentAlwaysOnVpnLockdownApp() throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public void startLegacyVpn(VpnProfile profile) throws RemoteException {
        }

        @Override // android.net.IVpnManager
        public LegacyVpnInfo getLegacyVpnInfo(int userId) throws RemoteException {
            return null;
        }

        @Override // android.net.IVpnManager
        public boolean updateLockdownVpn() throws RemoteException {
            return false;
        }

        @Override // android.net.IVpnManager
        public VpnConfig getVpnConfig(int userId) throws RemoteException {
            return null;
        }

        @Override // android.net.IVpnManager
        public void factoryReset() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IVpnManager {
        static final int TRANSACTION_addVpnAddress = 4;
        static final int TRANSACTION_deleteVpnProfile = 8;
        static final int TRANSACTION_establishVpn = 3;
        static final int TRANSACTION_factoryReset = 25;
        static final int TRANSACTION_getAlwaysOnVpnPackage = 16;
        static final int TRANSACTION_getAppExclusionList = 13;
        static final int TRANSACTION_getLegacyVpnInfo = 22;
        static final int TRANSACTION_getProvisionedVpnProfileState = 11;
        static final int TRANSACTION_getVpnConfig = 24;
        static final int TRANSACTION_getVpnLockdownAllowlist = 18;
        static final int TRANSACTION_isAlwaysOnVpnPackageSupported = 14;
        static final int TRANSACTION_isCallerCurrentAlwaysOnVpnApp = 19;
        static final int TRANSACTION_isCallerCurrentAlwaysOnVpnLockdownApp = 20;
        static final int TRANSACTION_isVpnLockdownEnabled = 17;
        static final int TRANSACTION_prepareVpn = 1;
        static final int TRANSACTION_provisionVpnProfile = 7;
        static final int TRANSACTION_removeVpnAddress = 5;
        static final int TRANSACTION_setAlwaysOnVpnPackage = 15;
        static final int TRANSACTION_setAppExclusionList = 12;
        static final int TRANSACTION_setUnderlyingNetworksForVpn = 6;
        static final int TRANSACTION_setVpnPackageAuthorization = 2;
        static final int TRANSACTION_startLegacyVpn = 21;
        static final int TRANSACTION_startVpnProfile = 9;
        static final int TRANSACTION_stopVpnProfile = 10;
        static final int TRANSACTION_updateLockdownVpn = 23;

        public Stub() {
            attachInterface(this, IVpnManager.DESCRIPTOR);
        }

        public static IVpnManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IVpnManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IVpnManager)) {
                return (IVpnManager) iin;
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
                    return "prepareVpn";
                case 2:
                    return "setVpnPackageAuthorization";
                case 3:
                    return "establishVpn";
                case 4:
                    return "addVpnAddress";
                case 5:
                    return "removeVpnAddress";
                case 6:
                    return "setUnderlyingNetworksForVpn";
                case 7:
                    return "provisionVpnProfile";
                case 8:
                    return "deleteVpnProfile";
                case 9:
                    return "startVpnProfile";
                case 10:
                    return "stopVpnProfile";
                case 11:
                    return "getProvisionedVpnProfileState";
                case 12:
                    return "setAppExclusionList";
                case 13:
                    return "getAppExclusionList";
                case 14:
                    return "isAlwaysOnVpnPackageSupported";
                case 15:
                    return "setAlwaysOnVpnPackage";
                case 16:
                    return "getAlwaysOnVpnPackage";
                case 17:
                    return "isVpnLockdownEnabled";
                case 18:
                    return "getVpnLockdownAllowlist";
                case 19:
                    return "isCallerCurrentAlwaysOnVpnApp";
                case 20:
                    return "isCallerCurrentAlwaysOnVpnLockdownApp";
                case 21:
                    return "startLegacyVpn";
                case 22:
                    return "getLegacyVpnInfo";
                case 23:
                    return "updateLockdownVpn";
                case 24:
                    return "getVpnConfig";
                case 25:
                    return "factoryReset";
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
                data.enforceInterface(IVpnManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IVpnManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = prepareVpn(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            setVpnPackageAuthorization(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 3:
                            VpnConfig _arg03 = (VpnConfig) data.readTypedObject(VpnConfig.CREATOR);
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result2 = establishVpn(_arg03);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result3 = addVpnAddress(_arg04, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = removeVpnAddress(_arg05, _arg14);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 6:
                            Network[] _arg06 = (Network[]) data.createTypedArray(Network.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result5 = setUnderlyingNetworksForVpn(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 7:
                            VpnProfile _arg07 = (VpnProfile) data.readTypedObject(VpnProfile.CREATOR);
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result6 = provisionVpnProfile(_arg07, _arg15);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            data.enforceNoDataAvail();
                            deleteVpnProfile(_arg08);
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            String _result7 = startVpnProfile(_arg09);
                            reply.writeNoException();
                            reply.writeString(_result7);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            data.enforceNoDataAvail();
                            stopVpnProfile(_arg010);
                            reply.writeNoException();
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            data.enforceNoDataAvail();
                            VpnProfileState _result8 = getProvisionedVpnProfileState(_arg011);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            String _arg16 = data.readString();
                            List<String> _arg23 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            boolean _result9 = setAppExclusionList(_arg012, _arg16, _arg23);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result10 = getAppExclusionList(_arg013, _arg17);
                            reply.writeNoException();
                            reply.writeStringList(_result10);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            String _arg18 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result11 = isAlwaysOnVpnPackageSupported(_arg014, _arg18);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            String _arg19 = data.readString();
                            boolean _arg24 = data.readBoolean();
                            List<String> _arg3 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            boolean _result12 = setAlwaysOnVpnPackage(_arg015, _arg19, _arg24, _arg3);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result13 = getAlwaysOnVpnPackage(_arg016);
                            reply.writeNoException();
                            reply.writeString(_result13);
                            break;
                        case 17:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result14 = isVpnLockdownEnabled(_arg017);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            break;
                        case 18:
                            int _arg018 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result15 = getVpnLockdownAllowlist(_arg018);
                            reply.writeNoException();
                            reply.writeStringList(_result15);
                            break;
                        case 19:
                            boolean _result16 = isCallerCurrentAlwaysOnVpnApp();
                            reply.writeNoException();
                            reply.writeBoolean(_result16);
                            break;
                        case 20:
                            boolean _result17 = isCallerCurrentAlwaysOnVpnLockdownApp();
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            break;
                        case 21:
                            VpnProfile _arg019 = (VpnProfile) data.readTypedObject(VpnProfile.CREATOR);
                            data.enforceNoDataAvail();
                            startLegacyVpn(_arg019);
                            reply.writeNoException();
                            break;
                        case 22:
                            int _arg020 = data.readInt();
                            data.enforceNoDataAvail();
                            LegacyVpnInfo _result18 = getLegacyVpnInfo(_arg020);
                            reply.writeNoException();
                            reply.writeTypedObject(_result18, 1);
                            break;
                        case 23:
                            boolean _result19 = updateLockdownVpn();
                            reply.writeNoException();
                            reply.writeBoolean(_result19);
                            break;
                        case 24:
                            int _arg021 = data.readInt();
                            data.enforceNoDataAvail();
                            VpnConfig _result20 = getVpnConfig(_arg021);
                            reply.writeNoException();
                            reply.writeTypedObject(_result20, 1);
                            break;
                        case 25:
                            factoryReset();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IVpnManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IVpnManager.DESCRIPTOR;
            }

            @Override // android.net.IVpnManager
            public boolean prepareVpn(String oldPackage, String newPackage, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeString(oldPackage);
                    _data.writeString(newPackage);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public void setVpnPackageAuthorization(String packageName, int userId, int vpnType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeInt(vpnType);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public ParcelFileDescriptor establishVpn(VpnConfig config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean addVpnAddress(String address, int prefixLength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(prefixLength);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean removeVpnAddress(String address, int prefixLength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(prefixLength);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean setUnderlyingNetworksForVpn(Network[] networks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeTypedArray(networks, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean provisionVpnProfile(VpnProfile profile, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public void deleteVpnProfile(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public String startVpnProfile(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public void stopVpnProfile(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public VpnProfileState getProvisionedVpnProfileState(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    VpnProfileState _result = (VpnProfileState) _reply.readTypedObject(VpnProfileState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean setAppExclusionList(int userId, String vpnPackage, List<String> excludedApps) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(vpnPackage);
                    _data.writeStringList(excludedApps);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public List<String> getAppExclusionList(int userId, String vpnPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(vpnPackage);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean isAlwaysOnVpnPackageSupported(int userId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(packageName);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean setAlwaysOnVpnPackage(int userId, String packageName, boolean lockdown, List<String> lockdownAllowlist) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(packageName);
                    _data.writeBoolean(lockdown);
                    _data.writeStringList(lockdownAllowlist);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public String getAlwaysOnVpnPackage(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean isVpnLockdownEnabled(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public List<String> getVpnLockdownAllowlist(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean isCallerCurrentAlwaysOnVpnApp() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean isCallerCurrentAlwaysOnVpnLockdownApp() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public void startLegacyVpn(VpnProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public LegacyVpnInfo getLegacyVpnInfo(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    LegacyVpnInfo _result = (LegacyVpnInfo) _reply.readTypedObject(LegacyVpnInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public boolean updateLockdownVpn() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public VpnConfig getVpnConfig(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    VpnConfig _result = (VpnConfig) _reply.readTypedObject(VpnConfig.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IVpnManager
            public void factoryReset() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVpnManager.DESCRIPTOR);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 24;
        }
    }
}
