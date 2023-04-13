package android.net;

import android.net.INetworkScoreCache;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface INetworkScoreService extends IInterface {
    boolean clearScores() throws RemoteException;

    void disableScoring() throws RemoteException;

    NetworkScorerAppData getActiveScorer() throws RemoteException;

    String getActiveScorerPackage() throws RemoteException;

    List<NetworkScorerAppData> getAllValidScorers() throws RemoteException;

    boolean isCallerActiveScorer(int i) throws RemoteException;

    void registerNetworkScoreCache(int i, INetworkScoreCache iNetworkScoreCache, int i2) throws RemoteException;

    boolean requestScores(NetworkKey[] networkKeyArr) throws RemoteException;

    boolean setActiveScorer(String str) throws RemoteException;

    void unregisterNetworkScoreCache(int i, INetworkScoreCache iNetworkScoreCache) throws RemoteException;

    boolean updateScores(ScoredNetwork[] scoredNetworkArr) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements INetworkScoreService {
        @Override // android.net.INetworkScoreService
        public boolean updateScores(ScoredNetwork[] networks) throws RemoteException {
            return false;
        }

        @Override // android.net.INetworkScoreService
        public boolean clearScores() throws RemoteException {
            return false;
        }

        @Override // android.net.INetworkScoreService
        public boolean setActiveScorer(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.net.INetworkScoreService
        public void disableScoring() throws RemoteException {
        }

        @Override // android.net.INetworkScoreService
        public void registerNetworkScoreCache(int networkType, INetworkScoreCache scoreCache, int filterType) throws RemoteException {
        }

        @Override // android.net.INetworkScoreService
        public void unregisterNetworkScoreCache(int networkType, INetworkScoreCache scoreCache) throws RemoteException {
        }

        @Override // android.net.INetworkScoreService
        public boolean requestScores(NetworkKey[] networks) throws RemoteException {
            return false;
        }

        @Override // android.net.INetworkScoreService
        public boolean isCallerActiveScorer(int callingUid) throws RemoteException {
            return false;
        }

        @Override // android.net.INetworkScoreService
        public String getActiveScorerPackage() throws RemoteException {
            return null;
        }

        @Override // android.net.INetworkScoreService
        public NetworkScorerAppData getActiveScorer() throws RemoteException {
            return null;
        }

        @Override // android.net.INetworkScoreService
        public List<NetworkScorerAppData> getAllValidScorers() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements INetworkScoreService {
        public static final String DESCRIPTOR = "android.net.INetworkScoreService";
        static final int TRANSACTION_clearScores = 2;
        static final int TRANSACTION_disableScoring = 4;
        static final int TRANSACTION_getActiveScorer = 10;
        static final int TRANSACTION_getActiveScorerPackage = 9;
        static final int TRANSACTION_getAllValidScorers = 11;
        static final int TRANSACTION_isCallerActiveScorer = 8;
        static final int TRANSACTION_registerNetworkScoreCache = 5;
        static final int TRANSACTION_requestScores = 7;
        static final int TRANSACTION_setActiveScorer = 3;
        static final int TRANSACTION_unregisterNetworkScoreCache = 6;
        static final int TRANSACTION_updateScores = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkScoreService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INetworkScoreService)) {
                return (INetworkScoreService) iin;
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
                    return "updateScores";
                case 2:
                    return "clearScores";
                case 3:
                    return "setActiveScorer";
                case 4:
                    return "disableScoring";
                case 5:
                    return "registerNetworkScoreCache";
                case 6:
                    return "unregisterNetworkScoreCache";
                case 7:
                    return "requestScores";
                case 8:
                    return "isCallerActiveScorer";
                case 9:
                    return "getActiveScorerPackage";
                case 10:
                    return "getActiveScorer";
                case 11:
                    return "getAllValidScorers";
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
                            ScoredNetwork[] _arg0 = (ScoredNetwork[]) data.createTypedArray(ScoredNetwork.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result = updateScores(_arg0);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            boolean _result2 = clearScores();
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 3:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result3 = setActiveScorer(_arg02);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            disableScoring();
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg03 = data.readInt();
                            INetworkScoreCache _arg1 = INetworkScoreCache.Stub.asInterface(data.readStrongBinder());
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            registerNetworkScoreCache(_arg03, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg04 = data.readInt();
                            INetworkScoreCache _arg12 = INetworkScoreCache.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterNetworkScoreCache(_arg04, _arg12);
                            reply.writeNoException();
                            break;
                        case 7:
                            NetworkKey[] _arg05 = (NetworkKey[]) data.createTypedArray(NetworkKey.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result4 = requestScores(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 8:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = isCallerActiveScorer(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 9:
                            String _result6 = getActiveScorerPackage();
                            reply.writeNoException();
                            reply.writeString(_result6);
                            break;
                        case 10:
                            NetworkScorerAppData _result7 = getActiveScorer();
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 11:
                            List<NetworkScorerAppData> _result8 = getAllValidScorers();
                            reply.writeNoException();
                            reply.writeTypedList(_result8, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements INetworkScoreService {
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

            @Override // android.net.INetworkScoreService
            public boolean updateScores(ScoredNetwork[] networks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(networks, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkScoreService
            public boolean clearScores() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkScoreService
            public boolean setActiveScorer(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkScoreService
            public void disableScoring() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkScoreService
            public void registerNetworkScoreCache(int networkType, INetworkScoreCache scoreCache, int filterType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeStrongInterface(scoreCache);
                    _data.writeInt(filterType);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkScoreService
            public void unregisterNetworkScoreCache(int networkType, INetworkScoreCache scoreCache) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeStrongInterface(scoreCache);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkScoreService
            public boolean requestScores(NetworkKey[] networks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(networks, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkScoreService
            public boolean isCallerActiveScorer(int callingUid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(callingUid);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkScoreService
            public String getActiveScorerPackage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkScoreService
            public NetworkScorerAppData getActiveScorer() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    NetworkScorerAppData _result = (NetworkScorerAppData) _reply.readTypedObject(NetworkScorerAppData.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.INetworkScoreService
            public List<NetworkScorerAppData> getAllValidScorers() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    List<NetworkScorerAppData> _result = _reply.createTypedArrayList(NetworkScorerAppData.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 10;
        }
    }
}
