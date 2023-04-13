package android.view.contentcapture;

import android.content.ComponentName;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.contentcapture.IContentCaptureOptionsCallback;
import android.view.contentcapture.IDataShareWriteAdapter;
import com.android.internal.p028os.IResultReceiver;
/* loaded from: classes4.dex */
public interface IContentCaptureManager extends IInterface {
    public static final String DESCRIPTOR = "android.view.contentcapture.IContentCaptureManager";

    void finishSession(int i) throws RemoteException;

    void getContentCaptureConditions(String str, IResultReceiver iResultReceiver) throws RemoteException;

    void getServiceComponentName(IResultReceiver iResultReceiver) throws RemoteException;

    void getServiceSettingsActivity(IResultReceiver iResultReceiver) throws RemoteException;

    void isContentCaptureFeatureEnabled(IResultReceiver iResultReceiver) throws RemoteException;

    void registerContentCaptureOptionsCallback(String str, IContentCaptureOptionsCallback iContentCaptureOptionsCallback) throws RemoteException;

    void removeData(DataRemovalRequest dataRemovalRequest) throws RemoteException;

    void resetTemporaryService(int i) throws RemoteException;

    void setDefaultServiceEnabled(int i, boolean z) throws RemoteException;

    void setTemporaryService(int i, String str, int i2) throws RemoteException;

    void shareData(DataShareRequest dataShareRequest, IDataShareWriteAdapter iDataShareWriteAdapter) throws RemoteException;

    void startSession(IBinder iBinder, IBinder iBinder2, ComponentName componentName, int i, int i2, IResultReceiver iResultReceiver) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IContentCaptureManager {
        @Override // android.view.contentcapture.IContentCaptureManager
        public void startSession(IBinder activityToken, IBinder shareableActivityToken, ComponentName componentName, int sessionId, int flags, IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void finishSession(int sessionId) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void getServiceComponentName(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void removeData(DataRemovalRequest request) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void shareData(DataShareRequest request, IDataShareWriteAdapter adapter) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void isContentCaptureFeatureEnabled(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void getServiceSettingsActivity(IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void getContentCaptureConditions(String packageName, IResultReceiver result) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void resetTemporaryService(int userId) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void setTemporaryService(int userId, String serviceName, int duration) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void setDefaultServiceEnabled(int userId, boolean enabled) throws RemoteException {
        }

        @Override // android.view.contentcapture.IContentCaptureManager
        public void registerContentCaptureOptionsCallback(String packageName, IContentCaptureOptionsCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IContentCaptureManager {
        static final int TRANSACTION_finishSession = 2;
        static final int TRANSACTION_getContentCaptureConditions = 8;
        static final int TRANSACTION_getServiceComponentName = 3;
        static final int TRANSACTION_getServiceSettingsActivity = 7;
        static final int TRANSACTION_isContentCaptureFeatureEnabled = 6;
        static final int TRANSACTION_registerContentCaptureOptionsCallback = 12;
        static final int TRANSACTION_removeData = 4;
        static final int TRANSACTION_resetTemporaryService = 9;
        static final int TRANSACTION_setDefaultServiceEnabled = 11;
        static final int TRANSACTION_setTemporaryService = 10;
        static final int TRANSACTION_shareData = 5;
        static final int TRANSACTION_startSession = 1;

        public Stub() {
            attachInterface(this, IContentCaptureManager.DESCRIPTOR);
        }

        public static IContentCaptureManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IContentCaptureManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IContentCaptureManager)) {
                return (IContentCaptureManager) iin;
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
                    return "startSession";
                case 2:
                    return "finishSession";
                case 3:
                    return "getServiceComponentName";
                case 4:
                    return "removeData";
                case 5:
                    return "shareData";
                case 6:
                    return "isContentCaptureFeatureEnabled";
                case 7:
                    return "getServiceSettingsActivity";
                case 8:
                    return "getContentCaptureConditions";
                case 9:
                    return "resetTemporaryService";
                case 10:
                    return "setTemporaryService";
                case 11:
                    return "setDefaultServiceEnabled";
                case 12:
                    return "registerContentCaptureOptionsCallback";
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
                data.enforceInterface(IContentCaptureManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IContentCaptureManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            IBinder _arg1 = data.readStrongBinder();
                            ComponentName _arg2 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            IResultReceiver _arg5 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startSession(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            finishSession(_arg02);
                            break;
                        case 3:
                            IResultReceiver _arg03 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getServiceComponentName(_arg03);
                            break;
                        case 4:
                            DataRemovalRequest _arg04 = (DataRemovalRequest) data.readTypedObject(DataRemovalRequest.CREATOR);
                            data.enforceNoDataAvail();
                            removeData(_arg04);
                            break;
                        case 5:
                            DataShareRequest _arg05 = (DataShareRequest) data.readTypedObject(DataShareRequest.CREATOR);
                            IDataShareWriteAdapter _arg12 = IDataShareWriteAdapter.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            shareData(_arg05, _arg12);
                            break;
                        case 6:
                            IResultReceiver _arg06 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            isContentCaptureFeatureEnabled(_arg06);
                            break;
                        case 7:
                            IResultReceiver _arg07 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getServiceSettingsActivity(_arg07);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            IResultReceiver _arg13 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getContentCaptureConditions(_arg08, _arg13);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            resetTemporaryService(_arg09);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            String _arg14 = data.readString();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            setTemporaryService(_arg010, _arg14, _arg22);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            boolean _arg15 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setDefaultServiceEnabled(_arg011, _arg15);
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            IContentCaptureOptionsCallback _arg16 = IContentCaptureOptionsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerContentCaptureOptionsCallback(_arg012, _arg16);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IContentCaptureManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IContentCaptureManager.DESCRIPTOR;
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void startSession(IBinder activityToken, IBinder shareableActivityToken, ComponentName componentName, int sessionId, int flags, IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeStrongBinder(activityToken);
                    _data.writeStrongBinder(shareableActivityToken);
                    _data.writeTypedObject(componentName, 0);
                    _data.writeInt(sessionId);
                    _data.writeInt(flags);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void finishSession(int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void getServiceComponentName(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void removeData(DataRemovalRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void shareData(DataShareRequest request, IDataShareWriteAdapter adapter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(adapter);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void isContentCaptureFeatureEnabled(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void getServiceSettingsActivity(IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void getContentCaptureConditions(String packageName, IResultReceiver result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(result);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void resetTemporaryService(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void setTemporaryService(int userId, String serviceName, int duration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(serviceName);
                    _data.writeInt(duration);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void setDefaultServiceEnabled(int userId, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.contentcapture.IContentCaptureManager
            public void registerContentCaptureOptionsCallback(String packageName, IContentCaptureOptionsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 11;
        }
    }
}
