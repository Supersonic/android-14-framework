package android.p008os;

import android.content.ComponentName;
import java.util.List;
import java.util.Map;
/* renamed from: android.os.ISystemConfig */
/* loaded from: classes3.dex */
public interface ISystemConfig extends IInterface {
    public static final String DESCRIPTOR = "android.os.ISystemConfig";

    List<ComponentName> getDefaultVrComponents() throws RemoteException;

    List<String> getDisabledUntilUsedPreinstalledCarrierApps() throws RemoteException;

    Map getDisabledUntilUsedPreinstalledCarrierAssociatedAppEntries() throws RemoteException;

    Map getDisabledUntilUsedPreinstalledCarrierAssociatedApps() throws RemoteException;

    List<ComponentName> getEnabledComponentOverrides(String str) throws RemoteException;

    int[] getSystemPermissionUids(String str) throws RemoteException;

    /* renamed from: android.os.ISystemConfig$Default */
    /* loaded from: classes3.dex */
    public static class Default implements ISystemConfig {
        @Override // android.p008os.ISystemConfig
        public List<String> getDisabledUntilUsedPreinstalledCarrierApps() throws RemoteException {
            return null;
        }

        @Override // android.p008os.ISystemConfig
        public Map getDisabledUntilUsedPreinstalledCarrierAssociatedApps() throws RemoteException {
            return null;
        }

        @Override // android.p008os.ISystemConfig
        public Map getDisabledUntilUsedPreinstalledCarrierAssociatedAppEntries() throws RemoteException {
            return null;
        }

        @Override // android.p008os.ISystemConfig
        public int[] getSystemPermissionUids(String permissionName) throws RemoteException {
            return null;
        }

        @Override // android.p008os.ISystemConfig
        public List<ComponentName> getEnabledComponentOverrides(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.p008os.ISystemConfig
        public List<ComponentName> getDefaultVrComponents() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.ISystemConfig$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISystemConfig {
        static final int TRANSACTION_getDefaultVrComponents = 6;
        static final int TRANSACTION_getDisabledUntilUsedPreinstalledCarrierApps = 1;

        /* renamed from: TRANSACTION_getDisabledUntilUsedPreinstalledCarrierAssociatedAppEntries */
        static final int f316x5e5a4e8c = 3;

        /* renamed from: TRANSACTION_getDisabledUntilUsedPreinstalledCarrierAssociatedApps */
        static final int f317xcd65f78f = 2;
        static final int TRANSACTION_getEnabledComponentOverrides = 5;
        static final int TRANSACTION_getSystemPermissionUids = 4;

        public Stub() {
            attachInterface(this, ISystemConfig.DESCRIPTOR);
        }

        public static ISystemConfig asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISystemConfig.DESCRIPTOR);
            if (iin != null && (iin instanceof ISystemConfig)) {
                return (ISystemConfig) iin;
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
                    return "getDisabledUntilUsedPreinstalledCarrierApps";
                case 2:
                    return "getDisabledUntilUsedPreinstalledCarrierAssociatedApps";
                case 3:
                    return "getDisabledUntilUsedPreinstalledCarrierAssociatedAppEntries";
                case 4:
                    return "getSystemPermissionUids";
                case 5:
                    return "getEnabledComponentOverrides";
                case 6:
                    return "getDefaultVrComponents";
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
                data.enforceInterface(ISystemConfig.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISystemConfig.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<String> _result = getDisabledUntilUsedPreinstalledCarrierApps();
                            reply.writeNoException();
                            reply.writeStringList(_result);
                            break;
                        case 2:
                            Map _result2 = getDisabledUntilUsedPreinstalledCarrierAssociatedApps();
                            reply.writeNoException();
                            reply.writeMap(_result2);
                            break;
                        case 3:
                            Map _result3 = getDisabledUntilUsedPreinstalledCarrierAssociatedAppEntries();
                            reply.writeNoException();
                            reply.writeMap(_result3);
                            break;
                        case 4:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            int[] _result4 = getSystemPermissionUids(_arg0);
                            reply.writeNoException();
                            reply.writeIntArray(_result4);
                            break;
                        case 5:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            List<ComponentName> _result5 = getEnabledComponentOverrides(_arg02);
                            reply.writeNoException();
                            reply.writeTypedList(_result5, 1);
                            break;
                        case 6:
                            List<ComponentName> _result6 = getDefaultVrComponents();
                            reply.writeNoException();
                            reply.writeTypedList(_result6, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.ISystemConfig$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements ISystemConfig {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISystemConfig.DESCRIPTOR;
            }

            @Override // android.p008os.ISystemConfig
            public List<String> getDisabledUntilUsedPreinstalledCarrierApps() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISystemConfig.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.ISystemConfig
            public Map getDisabledUntilUsedPreinstalledCarrierAssociatedApps() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISystemConfig.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    Map _result = _reply.readHashMap(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.ISystemConfig
            public Map getDisabledUntilUsedPreinstalledCarrierAssociatedAppEntries() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISystemConfig.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    Map _result = _reply.readHashMap(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.ISystemConfig
            public int[] getSystemPermissionUids(String permissionName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISystemConfig.DESCRIPTOR);
                    _data.writeString(permissionName);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.ISystemConfig
            public List<ComponentName> getEnabledComponentOverrides(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISystemConfig.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<ComponentName> _result = _reply.createTypedArrayList(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.ISystemConfig
            public List<ComponentName> getDefaultVrComponents() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISystemConfig.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    List<ComponentName> _result = _reply.createTypedArrayList(ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
