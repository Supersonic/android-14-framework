package android.app.smartspace;

import android.app.smartspace.ISmartspaceCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ISmartspaceManager extends IInterface {
    public static final String DESCRIPTOR = "android.app.smartspace.ISmartspaceManager";

    void createSmartspaceSession(SmartspaceConfig smartspaceConfig, SmartspaceSessionId smartspaceSessionId, IBinder iBinder) throws RemoteException;

    void destroySmartspaceSession(SmartspaceSessionId smartspaceSessionId) throws RemoteException;

    void notifySmartspaceEvent(SmartspaceSessionId smartspaceSessionId, SmartspaceTargetEvent smartspaceTargetEvent) throws RemoteException;

    void registerSmartspaceUpdates(SmartspaceSessionId smartspaceSessionId, ISmartspaceCallback iSmartspaceCallback) throws RemoteException;

    void requestSmartspaceUpdate(SmartspaceSessionId smartspaceSessionId) throws RemoteException;

    void unregisterSmartspaceUpdates(SmartspaceSessionId smartspaceSessionId, ISmartspaceCallback iSmartspaceCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISmartspaceManager {
        @Override // android.app.smartspace.ISmartspaceManager
        public void createSmartspaceSession(SmartspaceConfig config, SmartspaceSessionId sessionId, IBinder token) throws RemoteException {
        }

        @Override // android.app.smartspace.ISmartspaceManager
        public void notifySmartspaceEvent(SmartspaceSessionId sessionId, SmartspaceTargetEvent event) throws RemoteException {
        }

        @Override // android.app.smartspace.ISmartspaceManager
        public void requestSmartspaceUpdate(SmartspaceSessionId sessionId) throws RemoteException {
        }

        @Override // android.app.smartspace.ISmartspaceManager
        public void registerSmartspaceUpdates(SmartspaceSessionId sessionId, ISmartspaceCallback callback) throws RemoteException {
        }

        @Override // android.app.smartspace.ISmartspaceManager
        public void unregisterSmartspaceUpdates(SmartspaceSessionId sessionId, ISmartspaceCallback callback) throws RemoteException {
        }

        @Override // android.app.smartspace.ISmartspaceManager
        public void destroySmartspaceSession(SmartspaceSessionId sessionId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISmartspaceManager {
        static final int TRANSACTION_createSmartspaceSession = 1;
        static final int TRANSACTION_destroySmartspaceSession = 6;
        static final int TRANSACTION_notifySmartspaceEvent = 2;
        static final int TRANSACTION_registerSmartspaceUpdates = 4;
        static final int TRANSACTION_requestSmartspaceUpdate = 3;
        static final int TRANSACTION_unregisterSmartspaceUpdates = 5;

        public Stub() {
            attachInterface(this, ISmartspaceManager.DESCRIPTOR);
        }

        public static ISmartspaceManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISmartspaceManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ISmartspaceManager)) {
                return (ISmartspaceManager) iin;
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
                    return "createSmartspaceSession";
                case 2:
                    return "notifySmartspaceEvent";
                case 3:
                    return "requestSmartspaceUpdate";
                case 4:
                    return "registerSmartspaceUpdates";
                case 5:
                    return "unregisterSmartspaceUpdates";
                case 6:
                    return "destroySmartspaceSession";
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
                data.enforceInterface(ISmartspaceManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISmartspaceManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SmartspaceConfig _arg0 = (SmartspaceConfig) data.readTypedObject(SmartspaceConfig.CREATOR);
                            SmartspaceSessionId _arg1 = (SmartspaceSessionId) data.readTypedObject(SmartspaceSessionId.CREATOR);
                            IBinder _arg2 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            createSmartspaceSession(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            SmartspaceSessionId _arg02 = (SmartspaceSessionId) data.readTypedObject(SmartspaceSessionId.CREATOR);
                            SmartspaceTargetEvent _arg12 = (SmartspaceTargetEvent) data.readTypedObject(SmartspaceTargetEvent.CREATOR);
                            data.enforceNoDataAvail();
                            notifySmartspaceEvent(_arg02, _arg12);
                            break;
                        case 3:
                            SmartspaceSessionId _arg03 = (SmartspaceSessionId) data.readTypedObject(SmartspaceSessionId.CREATOR);
                            data.enforceNoDataAvail();
                            requestSmartspaceUpdate(_arg03);
                            break;
                        case 4:
                            SmartspaceSessionId _arg04 = (SmartspaceSessionId) data.readTypedObject(SmartspaceSessionId.CREATOR);
                            ISmartspaceCallback _arg13 = ISmartspaceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerSmartspaceUpdates(_arg04, _arg13);
                            break;
                        case 5:
                            SmartspaceSessionId _arg05 = (SmartspaceSessionId) data.readTypedObject(SmartspaceSessionId.CREATOR);
                            ISmartspaceCallback _arg14 = ISmartspaceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterSmartspaceUpdates(_arg05, _arg14);
                            break;
                        case 6:
                            SmartspaceSessionId _arg06 = (SmartspaceSessionId) data.readTypedObject(SmartspaceSessionId.CREATOR);
                            data.enforceNoDataAvail();
                            destroySmartspaceSession(_arg06);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISmartspaceManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISmartspaceManager.DESCRIPTOR;
            }

            @Override // android.app.smartspace.ISmartspaceManager
            public void createSmartspaceSession(SmartspaceConfig config, SmartspaceSessionId sessionId, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceManager.DESCRIPTOR);
                    _data.writeTypedObject(config, 0);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.smartspace.ISmartspaceManager
            public void notifySmartspaceEvent(SmartspaceSessionId sessionId, SmartspaceTargetEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.smartspace.ISmartspaceManager
            public void requestSmartspaceUpdate(SmartspaceSessionId sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.smartspace.ISmartspaceManager
            public void registerSmartspaceUpdates(SmartspaceSessionId sessionId, ISmartspaceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.smartspace.ISmartspaceManager
            public void unregisterSmartspaceUpdates(SmartspaceSessionId sessionId, ISmartspaceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.smartspace.ISmartspaceManager
            public void destroySmartspaceSession(SmartspaceSessionId sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
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
