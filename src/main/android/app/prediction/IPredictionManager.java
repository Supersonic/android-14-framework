package android.app.prediction;

import android.app.prediction.IPredictionCallback;
import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IPredictionManager extends IInterface {
    public static final String DESCRIPTOR = "android.app.prediction.IPredictionManager";

    void createPredictionSession(AppPredictionContext appPredictionContext, AppPredictionSessionId appPredictionSessionId, IBinder iBinder) throws RemoteException;

    void notifyAppTargetEvent(AppPredictionSessionId appPredictionSessionId, AppTargetEvent appTargetEvent) throws RemoteException;

    void notifyLaunchLocationShown(AppPredictionSessionId appPredictionSessionId, String str, ParceledListSlice parceledListSlice) throws RemoteException;

    void onDestroyPredictionSession(AppPredictionSessionId appPredictionSessionId) throws RemoteException;

    void registerPredictionUpdates(AppPredictionSessionId appPredictionSessionId, IPredictionCallback iPredictionCallback) throws RemoteException;

    void requestPredictionUpdate(AppPredictionSessionId appPredictionSessionId) throws RemoteException;

    void sortAppTargets(AppPredictionSessionId appPredictionSessionId, ParceledListSlice parceledListSlice, IPredictionCallback iPredictionCallback) throws RemoteException;

    void unregisterPredictionUpdates(AppPredictionSessionId appPredictionSessionId, IPredictionCallback iPredictionCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IPredictionManager {
        @Override // android.app.prediction.IPredictionManager
        public void createPredictionSession(AppPredictionContext context, AppPredictionSessionId sessionId, IBinder token) throws RemoteException {
        }

        @Override // android.app.prediction.IPredictionManager
        public void notifyAppTargetEvent(AppPredictionSessionId sessionId, AppTargetEvent event) throws RemoteException {
        }

        @Override // android.app.prediction.IPredictionManager
        public void notifyLaunchLocationShown(AppPredictionSessionId sessionId, String launchLocation, ParceledListSlice targetIds) throws RemoteException {
        }

        @Override // android.app.prediction.IPredictionManager
        public void sortAppTargets(AppPredictionSessionId sessionId, ParceledListSlice targets, IPredictionCallback callback) throws RemoteException {
        }

        @Override // android.app.prediction.IPredictionManager
        public void registerPredictionUpdates(AppPredictionSessionId sessionId, IPredictionCallback callback) throws RemoteException {
        }

        @Override // android.app.prediction.IPredictionManager
        public void unregisterPredictionUpdates(AppPredictionSessionId sessionId, IPredictionCallback callback) throws RemoteException {
        }

        @Override // android.app.prediction.IPredictionManager
        public void requestPredictionUpdate(AppPredictionSessionId sessionId) throws RemoteException {
        }

        @Override // android.app.prediction.IPredictionManager
        public void onDestroyPredictionSession(AppPredictionSessionId sessionId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IPredictionManager {
        static final int TRANSACTION_createPredictionSession = 1;
        static final int TRANSACTION_notifyAppTargetEvent = 2;
        static final int TRANSACTION_notifyLaunchLocationShown = 3;
        static final int TRANSACTION_onDestroyPredictionSession = 8;
        static final int TRANSACTION_registerPredictionUpdates = 5;
        static final int TRANSACTION_requestPredictionUpdate = 7;
        static final int TRANSACTION_sortAppTargets = 4;
        static final int TRANSACTION_unregisterPredictionUpdates = 6;

        public Stub() {
            attachInterface(this, IPredictionManager.DESCRIPTOR);
        }

        public static IPredictionManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPredictionManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IPredictionManager)) {
                return (IPredictionManager) iin;
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
                    return "createPredictionSession";
                case 2:
                    return "notifyAppTargetEvent";
                case 3:
                    return "notifyLaunchLocationShown";
                case 4:
                    return "sortAppTargets";
                case 5:
                    return "registerPredictionUpdates";
                case 6:
                    return "unregisterPredictionUpdates";
                case 7:
                    return "requestPredictionUpdate";
                case 8:
                    return "onDestroyPredictionSession";
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
                data.enforceInterface(IPredictionManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPredictionManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            AppPredictionContext _arg0 = (AppPredictionContext) data.readTypedObject(AppPredictionContext.CREATOR);
                            AppPredictionSessionId _arg1 = (AppPredictionSessionId) data.readTypedObject(AppPredictionSessionId.CREATOR);
                            IBinder _arg2 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            createPredictionSession(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            AppPredictionSessionId _arg02 = (AppPredictionSessionId) data.readTypedObject(AppPredictionSessionId.CREATOR);
                            AppTargetEvent _arg12 = (AppTargetEvent) data.readTypedObject(AppTargetEvent.CREATOR);
                            data.enforceNoDataAvail();
                            notifyAppTargetEvent(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 3:
                            AppPredictionSessionId _arg03 = (AppPredictionSessionId) data.readTypedObject(AppPredictionSessionId.CREATOR);
                            String _arg13 = data.readString();
                            ParceledListSlice _arg22 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            data.enforceNoDataAvail();
                            notifyLaunchLocationShown(_arg03, _arg13, _arg22);
                            reply.writeNoException();
                            break;
                        case 4:
                            AppPredictionSessionId _arg04 = (AppPredictionSessionId) data.readTypedObject(AppPredictionSessionId.CREATOR);
                            ParceledListSlice _arg14 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            IPredictionCallback _arg23 = IPredictionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            sortAppTargets(_arg04, _arg14, _arg23);
                            reply.writeNoException();
                            break;
                        case 5:
                            AppPredictionSessionId _arg05 = (AppPredictionSessionId) data.readTypedObject(AppPredictionSessionId.CREATOR);
                            IPredictionCallback _arg15 = IPredictionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerPredictionUpdates(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 6:
                            AppPredictionSessionId _arg06 = (AppPredictionSessionId) data.readTypedObject(AppPredictionSessionId.CREATOR);
                            IPredictionCallback _arg16 = IPredictionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterPredictionUpdates(_arg06, _arg16);
                            reply.writeNoException();
                            break;
                        case 7:
                            AppPredictionSessionId _arg07 = (AppPredictionSessionId) data.readTypedObject(AppPredictionSessionId.CREATOR);
                            data.enforceNoDataAvail();
                            requestPredictionUpdate(_arg07);
                            reply.writeNoException();
                            break;
                        case 8:
                            AppPredictionSessionId _arg08 = (AppPredictionSessionId) data.readTypedObject(AppPredictionSessionId.CREATOR);
                            data.enforceNoDataAvail();
                            onDestroyPredictionSession(_arg08);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IPredictionManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPredictionManager.DESCRIPTOR;
            }

            @Override // android.app.prediction.IPredictionManager
            public void createPredictionSession(AppPredictionContext context, AppPredictionSessionId sessionId, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPredictionManager.DESCRIPTOR);
                    _data.writeTypedObject(context, 0);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.prediction.IPredictionManager
            public void notifyAppTargetEvent(AppPredictionSessionId sessionId, AppTargetEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPredictionManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.prediction.IPredictionManager
            public void notifyLaunchLocationShown(AppPredictionSessionId sessionId, String launchLocation, ParceledListSlice targetIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPredictionManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeString(launchLocation);
                    _data.writeTypedObject(targetIds, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.prediction.IPredictionManager
            public void sortAppTargets(AppPredictionSessionId sessionId, ParceledListSlice targets, IPredictionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPredictionManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(targets, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.prediction.IPredictionManager
            public void registerPredictionUpdates(AppPredictionSessionId sessionId, IPredictionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPredictionManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.prediction.IPredictionManager
            public void unregisterPredictionUpdates(AppPredictionSessionId sessionId, IPredictionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPredictionManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.prediction.IPredictionManager
            public void requestPredictionUpdate(AppPredictionSessionId sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPredictionManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.prediction.IPredictionManager
            public void onDestroyPredictionSession(AppPredictionSessionId sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPredictionManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 7;
        }
    }
}
