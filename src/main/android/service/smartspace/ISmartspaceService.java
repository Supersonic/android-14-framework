package android.service.smartspace;

import android.app.smartspace.ISmartspaceCallback;
import android.app.smartspace.SmartspaceConfig;
import android.app.smartspace.SmartspaceSessionId;
import android.app.smartspace.SmartspaceTargetEvent;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ISmartspaceService extends IInterface {
    public static final String DESCRIPTOR = "android.service.smartspace.ISmartspaceService";

    void notifySmartspaceEvent(SmartspaceSessionId smartspaceSessionId, SmartspaceTargetEvent smartspaceTargetEvent) throws RemoteException;

    void onCreateSmartspaceSession(SmartspaceConfig smartspaceConfig, SmartspaceSessionId smartspaceSessionId) throws RemoteException;

    void onDestroySmartspaceSession(SmartspaceSessionId smartspaceSessionId) throws RemoteException;

    void registerSmartspaceUpdates(SmartspaceSessionId smartspaceSessionId, ISmartspaceCallback iSmartspaceCallback) throws RemoteException;

    void requestSmartspaceUpdate(SmartspaceSessionId smartspaceSessionId) throws RemoteException;

    void unregisterSmartspaceUpdates(SmartspaceSessionId smartspaceSessionId, ISmartspaceCallback iSmartspaceCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISmartspaceService {
        @Override // android.service.smartspace.ISmartspaceService
        public void onCreateSmartspaceSession(SmartspaceConfig context, SmartspaceSessionId sessionId) throws RemoteException {
        }

        @Override // android.service.smartspace.ISmartspaceService
        public void notifySmartspaceEvent(SmartspaceSessionId sessionId, SmartspaceTargetEvent event) throws RemoteException {
        }

        @Override // android.service.smartspace.ISmartspaceService
        public void requestSmartspaceUpdate(SmartspaceSessionId sessionId) throws RemoteException {
        }

        @Override // android.service.smartspace.ISmartspaceService
        public void registerSmartspaceUpdates(SmartspaceSessionId sessionId, ISmartspaceCallback callback) throws RemoteException {
        }

        @Override // android.service.smartspace.ISmartspaceService
        public void unregisterSmartspaceUpdates(SmartspaceSessionId sessionId, ISmartspaceCallback callback) throws RemoteException {
        }

        @Override // android.service.smartspace.ISmartspaceService
        public void onDestroySmartspaceSession(SmartspaceSessionId sessionId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISmartspaceService {
        static final int TRANSACTION_notifySmartspaceEvent = 2;
        static final int TRANSACTION_onCreateSmartspaceSession = 1;
        static final int TRANSACTION_onDestroySmartspaceSession = 6;
        static final int TRANSACTION_registerSmartspaceUpdates = 4;
        static final int TRANSACTION_requestSmartspaceUpdate = 3;
        static final int TRANSACTION_unregisterSmartspaceUpdates = 5;

        public Stub() {
            attachInterface(this, ISmartspaceService.DESCRIPTOR);
        }

        public static ISmartspaceService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISmartspaceService.DESCRIPTOR);
            if (iin != null && (iin instanceof ISmartspaceService)) {
                return (ISmartspaceService) iin;
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
                    return "onCreateSmartspaceSession";
                case 2:
                    return "notifySmartspaceEvent";
                case 3:
                    return "requestSmartspaceUpdate";
                case 4:
                    return "registerSmartspaceUpdates";
                case 5:
                    return "unregisterSmartspaceUpdates";
                case 6:
                    return "onDestroySmartspaceSession";
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
                data.enforceInterface(ISmartspaceService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISmartspaceService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SmartspaceConfig _arg0 = (SmartspaceConfig) data.readTypedObject(SmartspaceConfig.CREATOR);
                            SmartspaceSessionId _arg1 = (SmartspaceSessionId) data.readTypedObject(SmartspaceSessionId.CREATOR);
                            data.enforceNoDataAvail();
                            onCreateSmartspaceSession(_arg0, _arg1);
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
                            onDestroySmartspaceSession(_arg06);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISmartspaceService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISmartspaceService.DESCRIPTOR;
            }

            @Override // android.service.smartspace.ISmartspaceService
            public void onCreateSmartspaceSession(SmartspaceConfig context, SmartspaceSessionId sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceService.DESCRIPTOR);
                    _data.writeTypedObject(context, 0);
                    _data.writeTypedObject(sessionId, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.smartspace.ISmartspaceService
            public void notifySmartspaceEvent(SmartspaceSessionId sessionId, SmartspaceTargetEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceService.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.smartspace.ISmartspaceService
            public void requestSmartspaceUpdate(SmartspaceSessionId sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceService.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.smartspace.ISmartspaceService
            public void registerSmartspaceUpdates(SmartspaceSessionId sessionId, ISmartspaceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceService.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.smartspace.ISmartspaceService
            public void unregisterSmartspaceUpdates(SmartspaceSessionId sessionId, ISmartspaceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceService.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.smartspace.ISmartspaceService
            public void onDestroySmartspaceSession(SmartspaceSessionId sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceService.DESCRIPTOR);
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
