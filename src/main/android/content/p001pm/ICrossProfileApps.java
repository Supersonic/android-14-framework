package android.content.p001pm;

import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.Intent;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import java.util.List;
/* renamed from: android.content.pm.ICrossProfileApps */
/* loaded from: classes.dex */
public interface ICrossProfileApps extends IInterface {
    boolean canConfigureInteractAcrossProfiles(int i, String str) throws RemoteException;

    boolean canInteractAcrossProfiles(String str) throws RemoteException;

    boolean canRequestInteractAcrossProfiles(String str) throws RemoteException;

    boolean canUserAttemptToConfigureInteractAcrossProfiles(int i, String str) throws RemoteException;

    void clearInteractAcrossProfilesAppOps(int i) throws RemoteException;

    List<UserHandle> getTargetUserProfiles(String str) throws RemoteException;

    void resetInteractAcrossProfilesAppOps(int i, List<String> list) throws RemoteException;

    void setInteractAcrossProfilesAppOp(int i, String str, int i2) throws RemoteException;

    void startActivityAsUser(IApplicationThread iApplicationThread, String str, String str2, ComponentName componentName, int i, boolean z, IBinder iBinder, Bundle bundle) throws RemoteException;

    void startActivityAsUserByIntent(IApplicationThread iApplicationThread, String str, String str2, Intent intent, int i, IBinder iBinder, Bundle bundle) throws RemoteException;

    /* renamed from: android.content.pm.ICrossProfileApps$Default */
    /* loaded from: classes.dex */
    public static class Default implements ICrossProfileApps {
        @Override // android.content.p001pm.ICrossProfileApps
        public void startActivityAsUser(IApplicationThread caller, String callingPackage, String callingFeatureId, ComponentName component, int userId, boolean launchMainActivity, IBinder task, Bundle options) throws RemoteException {
        }

        @Override // android.content.p001pm.ICrossProfileApps
        public void startActivityAsUserByIntent(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, int userId, IBinder callingActivity, Bundle options) throws RemoteException {
        }

        @Override // android.content.p001pm.ICrossProfileApps
        public List<UserHandle> getTargetUserProfiles(String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ICrossProfileApps
        public boolean canInteractAcrossProfiles(String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.ICrossProfileApps
        public boolean canRequestInteractAcrossProfiles(String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.ICrossProfileApps
        public void setInteractAcrossProfilesAppOp(int userId, String packageName, int newMode) throws RemoteException {
        }

        @Override // android.content.p001pm.ICrossProfileApps
        public boolean canConfigureInteractAcrossProfiles(int userId, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.ICrossProfileApps
        public boolean canUserAttemptToConfigureInteractAcrossProfiles(int userId, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.ICrossProfileApps
        public void resetInteractAcrossProfilesAppOps(int userId, List<String> packageNames) throws RemoteException {
        }

        @Override // android.content.p001pm.ICrossProfileApps
        public void clearInteractAcrossProfilesAppOps(int userId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.pm.ICrossProfileApps$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICrossProfileApps {
        public static final String DESCRIPTOR = "android.content.pm.ICrossProfileApps";
        static final int TRANSACTION_canConfigureInteractAcrossProfiles = 7;
        static final int TRANSACTION_canInteractAcrossProfiles = 4;
        static final int TRANSACTION_canRequestInteractAcrossProfiles = 5;
        static final int TRANSACTION_canUserAttemptToConfigureInteractAcrossProfiles = 8;
        static final int TRANSACTION_clearInteractAcrossProfilesAppOps = 10;
        static final int TRANSACTION_getTargetUserProfiles = 3;
        static final int TRANSACTION_resetInteractAcrossProfilesAppOps = 9;
        static final int TRANSACTION_setInteractAcrossProfilesAppOp = 6;
        static final int TRANSACTION_startActivityAsUser = 1;
        static final int TRANSACTION_startActivityAsUserByIntent = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICrossProfileApps asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICrossProfileApps)) {
                return (ICrossProfileApps) iin;
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
                    return "startActivityAsUser";
                case 2:
                    return "startActivityAsUserByIntent";
                case 3:
                    return "getTargetUserProfiles";
                case 4:
                    return "canInteractAcrossProfiles";
                case 5:
                    return "canRequestInteractAcrossProfiles";
                case 6:
                    return "setInteractAcrossProfilesAppOp";
                case 7:
                    return "canConfigureInteractAcrossProfiles";
                case 8:
                    return "canUserAttemptToConfigureInteractAcrossProfiles";
                case 9:
                    return "resetInteractAcrossProfilesAppOps";
                case 10:
                    return "clearInteractAcrossProfilesAppOps";
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
                            IApplicationThread _arg0 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            ComponentName _arg3 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg4 = data.readInt();
                            boolean _arg5 = data.readBoolean();
                            IBinder _arg6 = data.readStrongBinder();
                            Bundle _arg7 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            startActivityAsUser(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
                            reply.writeNoException();
                            break;
                        case 2:
                            IApplicationThread _arg02 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg12 = data.readString();
                            String _arg22 = data.readString();
                            Intent _arg32 = (Intent) data.readTypedObject(Intent.CREATOR);
                            int _arg42 = data.readInt();
                            IBinder _arg52 = data.readStrongBinder();
                            Bundle _arg62 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            startActivityAsUserByIntent(_arg02, _arg12, _arg22, _arg32, _arg42, _arg52, _arg62);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            List<UserHandle> _result = getTargetUserProfiles(_arg03);
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result2 = canInteractAcrossProfiles(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result3 = canRequestInteractAcrossProfiles(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            String _arg13 = data.readString();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            setInteractAcrossProfilesAppOp(_arg06, _arg13, _arg23);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result4 = canConfigureInteractAcrossProfiles(_arg07, _arg14);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result5 = canUserAttemptToConfigureInteractAcrossProfiles(_arg08, _arg15);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            List<String> _arg16 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            resetInteractAcrossProfilesAppOps(_arg09, _arg16);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            clearInteractAcrossProfilesAppOps(_arg010);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.content.pm.ICrossProfileApps$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements ICrossProfileApps {
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

            @Override // android.content.p001pm.ICrossProfileApps
            public void startActivityAsUser(IApplicationThread caller, String callingPackage, String callingFeatureId, ComponentName component, int userId, boolean launchMainActivity, IBinder task, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeTypedObject(component, 0);
                    _data.writeInt(userId);
                    _data.writeBoolean(launchMainActivity);
                    _data.writeStrongBinder(task);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ICrossProfileApps
            public void startActivityAsUserByIntent(IApplicationThread caller, String callingPackage, String callingFeatureId, Intent intent, int userId, IBinder callingActivity, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeTypedObject(intent, 0);
                    _data.writeInt(userId);
                    _data.writeStrongBinder(callingActivity);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ICrossProfileApps
            public List<UserHandle> getTargetUserProfiles(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    List<UserHandle> _result = _reply.createTypedArrayList(UserHandle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ICrossProfileApps
            public boolean canInteractAcrossProfiles(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ICrossProfileApps
            public boolean canRequestInteractAcrossProfiles(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ICrossProfileApps
            public void setInteractAcrossProfilesAppOp(int userId, String packageName, int newMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(packageName);
                    _data.writeInt(newMode);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ICrossProfileApps
            public boolean canConfigureInteractAcrossProfiles(int userId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
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

            @Override // android.content.p001pm.ICrossProfileApps
            public boolean canUserAttemptToConfigureInteractAcrossProfiles(int userId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(packageName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ICrossProfileApps
            public void resetInteractAcrossProfilesAppOps(int userId, List<String> packageNames) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeStringList(packageNames);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ICrossProfileApps
            public void clearInteractAcrossProfilesAppOps(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
