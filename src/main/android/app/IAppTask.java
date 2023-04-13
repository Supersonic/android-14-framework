package android.app;

import android.app.ActivityManager;
import android.app.IApplicationThread;
import android.content.Intent;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IAppTask extends IInterface {
    void finishAndRemoveTask() throws RemoteException;

    ActivityManager.RecentTaskInfo getTaskInfo() throws RemoteException;

    void moveToFront(IApplicationThread iApplicationThread, String str) throws RemoteException;

    void setExcludeFromRecents(boolean z) throws RemoteException;

    int startActivity(IBinder iBinder, String str, String str2, Intent intent, String str3, Bundle bundle) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAppTask {
        @Override // android.app.IAppTask
        public void finishAndRemoveTask() throws RemoteException {
        }

        @Override // android.app.IAppTask
        public ActivityManager.RecentTaskInfo getTaskInfo() throws RemoteException {
            return null;
        }

        @Override // android.app.IAppTask
        public void moveToFront(IApplicationThread appThread, String callingPackage) throws RemoteException {
        }

        @Override // android.app.IAppTask
        public int startActivity(IBinder whoThread, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, Bundle options) throws RemoteException {
            return 0;
        }

        @Override // android.app.IAppTask
        public void setExcludeFromRecents(boolean exclude) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAppTask {
        public static final String DESCRIPTOR = "android.app.IAppTask";
        static final int TRANSACTION_finishAndRemoveTask = 1;
        static final int TRANSACTION_getTaskInfo = 2;
        static final int TRANSACTION_moveToFront = 3;
        static final int TRANSACTION_setExcludeFromRecents = 5;
        static final int TRANSACTION_startActivity = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAppTask asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAppTask)) {
                return (IAppTask) iin;
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
                    return "finishAndRemoveTask";
                case 2:
                    return "getTaskInfo";
                case 3:
                    return "moveToFront";
                case 4:
                    return "startActivity";
                case 5:
                    return "setExcludeFromRecents";
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
                            finishAndRemoveTask();
                            reply.writeNoException();
                            break;
                        case 2:
                            ActivityManager.RecentTaskInfo _result = getTaskInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 3:
                            IApplicationThread _arg0 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            moveToFront(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 4:
                            IBinder _arg02 = data.readStrongBinder();
                            String _arg12 = data.readString();
                            String _arg2 = data.readString();
                            Intent _arg3 = (Intent) data.readTypedObject(Intent.CREATOR);
                            String _arg4 = data.readString();
                            Bundle _arg5 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            int _result2 = startActivity(_arg02, _arg12, _arg2, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 5:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setExcludeFromRecents(_arg03);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IAppTask {
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

            @Override // android.app.IAppTask
            public void finishAndRemoveTask() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IAppTask
            public ActivityManager.RecentTaskInfo getTaskInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ActivityManager.RecentTaskInfo _result = (ActivityManager.RecentTaskInfo) _reply.readTypedObject(ActivityManager.RecentTaskInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IAppTask
            public void moveToFront(IApplicationThread appThread, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(appThread);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IAppTask
            public int startActivity(IBinder whoThread, String callingPackage, String callingFeatureId, Intent intent, String resolvedType, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(whoThread);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeTypedObject(intent, 0);
                    _data.writeString(resolvedType);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IAppTask
            public void setExcludeFromRecents(boolean exclude) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(exclude);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
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
