package android.app.search;

import android.app.search.ISearchCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ISearchUiManager extends IInterface {
    public static final String DESCRIPTOR = "android.app.search.ISearchUiManager";

    void createSearchSession(SearchContext searchContext, SearchSessionId searchSessionId, IBinder iBinder) throws RemoteException;

    void destroySearchSession(SearchSessionId searchSessionId) throws RemoteException;

    void notifyEvent(SearchSessionId searchSessionId, Query query, SearchTargetEvent searchTargetEvent) throws RemoteException;

    void query(SearchSessionId searchSessionId, Query query, ISearchCallback iSearchCallback) throws RemoteException;

    void registerEmptyQueryResultUpdateCallback(SearchSessionId searchSessionId, ISearchCallback iSearchCallback) throws RemoteException;

    void unregisterEmptyQueryResultUpdateCallback(SearchSessionId searchSessionId, ISearchCallback iSearchCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISearchUiManager {
        @Override // android.app.search.ISearchUiManager
        public void createSearchSession(SearchContext context, SearchSessionId sessionId, IBinder token) throws RemoteException {
        }

        @Override // android.app.search.ISearchUiManager
        public void query(SearchSessionId sessionId, Query input, ISearchCallback callback) throws RemoteException {
        }

        @Override // android.app.search.ISearchUiManager
        public void notifyEvent(SearchSessionId sessionId, Query input, SearchTargetEvent event) throws RemoteException {
        }

        @Override // android.app.search.ISearchUiManager
        public void registerEmptyQueryResultUpdateCallback(SearchSessionId sessionId, ISearchCallback callback) throws RemoteException {
        }

        @Override // android.app.search.ISearchUiManager
        public void unregisterEmptyQueryResultUpdateCallback(SearchSessionId sessionId, ISearchCallback callback) throws RemoteException {
        }

        @Override // android.app.search.ISearchUiManager
        public void destroySearchSession(SearchSessionId sessionId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISearchUiManager {
        static final int TRANSACTION_createSearchSession = 1;
        static final int TRANSACTION_destroySearchSession = 6;
        static final int TRANSACTION_notifyEvent = 3;
        static final int TRANSACTION_query = 2;
        static final int TRANSACTION_registerEmptyQueryResultUpdateCallback = 4;
        static final int TRANSACTION_unregisterEmptyQueryResultUpdateCallback = 5;

        public Stub() {
            attachInterface(this, ISearchUiManager.DESCRIPTOR);
        }

        public static ISearchUiManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISearchUiManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ISearchUiManager)) {
                return (ISearchUiManager) iin;
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
                    return "createSearchSession";
                case 2:
                    return "query";
                case 3:
                    return "notifyEvent";
                case 4:
                    return "registerEmptyQueryResultUpdateCallback";
                case 5:
                    return "unregisterEmptyQueryResultUpdateCallback";
                case 6:
                    return "destroySearchSession";
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
                data.enforceInterface(ISearchUiManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISearchUiManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SearchContext _arg0 = (SearchContext) data.readTypedObject(SearchContext.CREATOR);
                            SearchSessionId _arg1 = (SearchSessionId) data.readTypedObject(SearchSessionId.CREATOR);
                            IBinder _arg2 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            createSearchSession(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            SearchSessionId _arg02 = (SearchSessionId) data.readTypedObject(SearchSessionId.CREATOR);
                            Query _arg12 = (Query) data.readTypedObject(Query.CREATOR);
                            ISearchCallback _arg22 = ISearchCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            query(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 3:
                            SearchSessionId _arg03 = (SearchSessionId) data.readTypedObject(SearchSessionId.CREATOR);
                            Query _arg13 = (Query) data.readTypedObject(Query.CREATOR);
                            SearchTargetEvent _arg23 = (SearchTargetEvent) data.readTypedObject(SearchTargetEvent.CREATOR);
                            data.enforceNoDataAvail();
                            notifyEvent(_arg03, _arg13, _arg23);
                            reply.writeNoException();
                            break;
                        case 4:
                            SearchSessionId _arg04 = (SearchSessionId) data.readTypedObject(SearchSessionId.CREATOR);
                            ISearchCallback _arg14 = ISearchCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerEmptyQueryResultUpdateCallback(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 5:
                            SearchSessionId _arg05 = (SearchSessionId) data.readTypedObject(SearchSessionId.CREATOR);
                            ISearchCallback _arg15 = ISearchCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterEmptyQueryResultUpdateCallback(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 6:
                            SearchSessionId _arg06 = (SearchSessionId) data.readTypedObject(SearchSessionId.CREATOR);
                            data.enforceNoDataAvail();
                            destroySearchSession(_arg06);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISearchUiManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISearchUiManager.DESCRIPTOR;
            }

            @Override // android.app.search.ISearchUiManager
            public void createSearchSession(SearchContext context, SearchSessionId sessionId, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISearchUiManager.DESCRIPTOR);
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

            @Override // android.app.search.ISearchUiManager
            public void query(SearchSessionId sessionId, Query input, ISearchCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISearchUiManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(input, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.search.ISearchUiManager
            public void notifyEvent(SearchSessionId sessionId, Query input, SearchTargetEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISearchUiManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeTypedObject(input, 0);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.search.ISearchUiManager
            public void registerEmptyQueryResultUpdateCallback(SearchSessionId sessionId, ISearchCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISearchUiManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.search.ISearchUiManager
            public void unregisterEmptyQueryResultUpdateCallback(SearchSessionId sessionId, ISearchCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISearchUiManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.search.ISearchUiManager
            public void destroySearchSession(SearchSessionId sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISearchUiManager.DESCRIPTOR);
                    _data.writeTypedObject(sessionId, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
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
