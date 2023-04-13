package android.content.p001pm;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
/* renamed from: android.content.pm.IOnAppsChangedListener */
/* loaded from: classes.dex */
public interface IOnAppsChangedListener extends IInterface {
    void onPackageAdded(UserHandle userHandle, String str) throws RemoteException;

    void onPackageChanged(UserHandle userHandle, String str) throws RemoteException;

    void onPackageLoadingProgressChanged(UserHandle userHandle, String str, float f) throws RemoteException;

    void onPackageRemoved(UserHandle userHandle, String str) throws RemoteException;

    void onPackagesAvailable(UserHandle userHandle, String[] strArr, boolean z) throws RemoteException;

    void onPackagesSuspended(UserHandle userHandle, String[] strArr, Bundle bundle) throws RemoteException;

    void onPackagesUnavailable(UserHandle userHandle, String[] strArr, boolean z) throws RemoteException;

    void onPackagesUnsuspended(UserHandle userHandle, String[] strArr) throws RemoteException;

    void onShortcutChanged(UserHandle userHandle, String str, ParceledListSlice parceledListSlice) throws RemoteException;

    /* renamed from: android.content.pm.IOnAppsChangedListener$Default */
    /* loaded from: classes.dex */
    public static class Default implements IOnAppsChangedListener {
        @Override // android.content.p001pm.IOnAppsChangedListener
        public void onPackageRemoved(UserHandle user, String packageName) throws RemoteException {
        }

        @Override // android.content.p001pm.IOnAppsChangedListener
        public void onPackageAdded(UserHandle user, String packageName) throws RemoteException {
        }

        @Override // android.content.p001pm.IOnAppsChangedListener
        public void onPackageChanged(UserHandle user, String packageName) throws RemoteException {
        }

        @Override // android.content.p001pm.IOnAppsChangedListener
        public void onPackagesAvailable(UserHandle user, String[] packageNames, boolean replacing) throws RemoteException {
        }

        @Override // android.content.p001pm.IOnAppsChangedListener
        public void onPackagesUnavailable(UserHandle user, String[] packageNames, boolean replacing) throws RemoteException {
        }

        @Override // android.content.p001pm.IOnAppsChangedListener
        public void onPackagesSuspended(UserHandle user, String[] packageNames, Bundle launcherExtras) throws RemoteException {
        }

        @Override // android.content.p001pm.IOnAppsChangedListener
        public void onPackagesUnsuspended(UserHandle user, String[] packageNames) throws RemoteException {
        }

        @Override // android.content.p001pm.IOnAppsChangedListener
        public void onShortcutChanged(UserHandle user, String packageName, ParceledListSlice shortcuts) throws RemoteException {
        }

        @Override // android.content.p001pm.IOnAppsChangedListener
        public void onPackageLoadingProgressChanged(UserHandle user, String packageName, float progress) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.pm.IOnAppsChangedListener$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IOnAppsChangedListener {
        public static final String DESCRIPTOR = "android.content.pm.IOnAppsChangedListener";
        static final int TRANSACTION_onPackageAdded = 2;
        static final int TRANSACTION_onPackageChanged = 3;
        static final int TRANSACTION_onPackageLoadingProgressChanged = 9;
        static final int TRANSACTION_onPackageRemoved = 1;
        static final int TRANSACTION_onPackagesAvailable = 4;
        static final int TRANSACTION_onPackagesSuspended = 6;
        static final int TRANSACTION_onPackagesUnavailable = 5;
        static final int TRANSACTION_onPackagesUnsuspended = 7;
        static final int TRANSACTION_onShortcutChanged = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOnAppsChangedListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IOnAppsChangedListener)) {
                return (IOnAppsChangedListener) iin;
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
                    return "onPackageRemoved";
                case 2:
                    return "onPackageAdded";
                case 3:
                    return "onPackageChanged";
                case 4:
                    return "onPackagesAvailable";
                case 5:
                    return "onPackagesUnavailable";
                case 6:
                    return "onPackagesSuspended";
                case 7:
                    return "onPackagesUnsuspended";
                case 8:
                    return "onShortcutChanged";
                case 9:
                    return "onPackageLoadingProgressChanged";
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
                            UserHandle _arg0 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            onPackageRemoved(_arg0, _arg1);
                            break;
                        case 2:
                            UserHandle _arg02 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            onPackageAdded(_arg02, _arg12);
                            break;
                        case 3:
                            UserHandle _arg03 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            onPackageChanged(_arg03, _arg13);
                            break;
                        case 4:
                            UserHandle _arg04 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            String[] _arg14 = data.createStringArray();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onPackagesAvailable(_arg04, _arg14, _arg2);
                            break;
                        case 5:
                            UserHandle _arg05 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            String[] _arg15 = data.createStringArray();
                            boolean _arg22 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onPackagesUnavailable(_arg05, _arg15, _arg22);
                            break;
                        case 6:
                            UserHandle _arg06 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            String[] _arg16 = data.createStringArray();
                            Bundle _arg23 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onPackagesSuspended(_arg06, _arg16, _arg23);
                            break;
                        case 7:
                            UserHandle _arg07 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            String[] _arg17 = data.createStringArray();
                            data.enforceNoDataAvail();
                            onPackagesUnsuspended(_arg07, _arg17);
                            break;
                        case 8:
                            UserHandle _arg08 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            String _arg18 = data.readString();
                            ParceledListSlice _arg24 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            data.enforceNoDataAvail();
                            onShortcutChanged(_arg08, _arg18, _arg24);
                            break;
                        case 9:
                            UserHandle _arg09 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            String _arg19 = data.readString();
                            float _arg25 = data.readFloat();
                            data.enforceNoDataAvail();
                            onPackageLoadingProgressChanged(_arg09, _arg19, _arg25);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.content.pm.IOnAppsChangedListener$Stub$Proxy */
        /* loaded from: classes.dex */
        private static class Proxy implements IOnAppsChangedListener {
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

            @Override // android.content.p001pm.IOnAppsChangedListener
            public void onPackageRemoved(UserHandle user, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IOnAppsChangedListener
            public void onPackageAdded(UserHandle user, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IOnAppsChangedListener
            public void onPackageChanged(UserHandle user, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeString(packageName);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IOnAppsChangedListener
            public void onPackagesAvailable(UserHandle user, String[] packageNames, boolean replacing) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeStringArray(packageNames);
                    _data.writeBoolean(replacing);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IOnAppsChangedListener
            public void onPackagesUnavailable(UserHandle user, String[] packageNames, boolean replacing) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeStringArray(packageNames);
                    _data.writeBoolean(replacing);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IOnAppsChangedListener
            public void onPackagesSuspended(UserHandle user, String[] packageNames, Bundle launcherExtras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeStringArray(packageNames);
                    _data.writeTypedObject(launcherExtras, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IOnAppsChangedListener
            public void onPackagesUnsuspended(UserHandle user, String[] packageNames) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeStringArray(packageNames);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IOnAppsChangedListener
            public void onShortcutChanged(UserHandle user, String packageName, ParceledListSlice shortcuts) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeString(packageName);
                    _data.writeTypedObject(shortcuts, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IOnAppsChangedListener
            public void onPackageLoadingProgressChanged(UserHandle user, String packageName, float progress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(user, 0);
                    _data.writeString(packageName);
                    _data.writeFloat(progress);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
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
