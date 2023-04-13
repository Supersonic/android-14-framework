package android.apphibernation;

import android.apphibernation.IAppHibernationService;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public interface IAppHibernationService extends IInterface {
    public static final String DESCRIPTOR = "android.apphibernation.IAppHibernationService";

    List<String> getHibernatingPackagesForUser(int i) throws RemoteException;

    Map<String, HibernationStats> getHibernationStatsForUser(List<String> list, int i) throws RemoteException;

    boolean isHibernatingForUser(String str, int i) throws RemoteException;

    boolean isHibernatingGlobally(String str) throws RemoteException;

    boolean isOatArtifactDeletionEnabled() throws RemoteException;

    void setHibernatingForUser(String str, int i, boolean z) throws RemoteException;

    void setHibernatingGlobally(String str, boolean z) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAppHibernationService {
        @Override // android.apphibernation.IAppHibernationService
        public boolean isHibernatingForUser(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.apphibernation.IAppHibernationService
        public void setHibernatingForUser(String packageName, int userId, boolean isHibernating) throws RemoteException {
        }

        @Override // android.apphibernation.IAppHibernationService
        public boolean isHibernatingGlobally(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.apphibernation.IAppHibernationService
        public void setHibernatingGlobally(String packageName, boolean isHibernating) throws RemoteException {
        }

        @Override // android.apphibernation.IAppHibernationService
        public List<String> getHibernatingPackagesForUser(int userId) throws RemoteException {
            return null;
        }

        @Override // android.apphibernation.IAppHibernationService
        public Map<String, HibernationStats> getHibernationStatsForUser(List<String> packageNames, int userId) throws RemoteException {
            return null;
        }

        @Override // android.apphibernation.IAppHibernationService
        public boolean isOatArtifactDeletionEnabled() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAppHibernationService {
        static final int TRANSACTION_getHibernatingPackagesForUser = 5;
        static final int TRANSACTION_getHibernationStatsForUser = 6;
        static final int TRANSACTION_isHibernatingForUser = 1;
        static final int TRANSACTION_isHibernatingGlobally = 3;
        static final int TRANSACTION_isOatArtifactDeletionEnabled = 7;
        static final int TRANSACTION_setHibernatingForUser = 2;
        static final int TRANSACTION_setHibernatingGlobally = 4;

        public Stub() {
            attachInterface(this, IAppHibernationService.DESCRIPTOR);
        }

        public static IAppHibernationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAppHibernationService.DESCRIPTOR);
            if (iin != null && (iin instanceof IAppHibernationService)) {
                return (IAppHibernationService) iin;
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
                    return "isHibernatingForUser";
                case 2:
                    return "setHibernatingForUser";
                case 3:
                    return "isHibernatingGlobally";
                case 4:
                    return "setHibernatingGlobally";
                case 5:
                    return "getHibernatingPackagesForUser";
                case 6:
                    return "getHibernationStatsForUser";
                case 7:
                    return "isOatArtifactDeletionEnabled";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, final Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(IAppHibernationService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAppHibernationService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = isHibernatingForUser(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setHibernatingForUser(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result2 = isHibernatingGlobally(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            boolean _arg13 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setHibernatingGlobally(_arg04, _arg13);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result3 = getHibernatingPackagesForUser(_arg05);
                            reply.writeNoException();
                            reply.writeStringList(_result3);
                            break;
                        case 6:
                            List<String> _arg06 = data.createStringArrayList();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            Map<String, HibernationStats> _result4 = getHibernationStatsForUser(_arg06, _arg14);
                            reply.writeNoException();
                            if (_result4 == null) {
                                reply.writeInt(-1);
                                break;
                            } else {
                                reply.writeInt(_result4.size());
                                _result4.forEach(new BiConsumer() { // from class: android.apphibernation.IAppHibernationService$Stub$$ExternalSyntheticLambda0
                                    @Override // java.util.function.BiConsumer
                                    public final void accept(Object obj, Object obj2) {
                                        IAppHibernationService.Stub.lambda$onTransact$0(Parcel.this, (String) obj, (HibernationStats) obj2);
                                    }
                                });
                                break;
                            }
                        case 7:
                            boolean _result5 = isOatArtifactDeletionEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onTransact$0(Parcel reply, String k, HibernationStats v) {
            reply.writeString(k);
            reply.writeTypedObject(v, 1);
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IAppHibernationService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAppHibernationService.DESCRIPTOR;
            }

            @Override // android.apphibernation.IAppHibernationService
            public boolean isHibernatingForUser(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppHibernationService.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apphibernation.IAppHibernationService
            public void setHibernatingForUser(String packageName, int userId, boolean isHibernating) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppHibernationService.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeBoolean(isHibernating);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apphibernation.IAppHibernationService
            public boolean isHibernatingGlobally(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppHibernationService.DESCRIPTOR);
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

            @Override // android.apphibernation.IAppHibernationService
            public void setHibernatingGlobally(String packageName, boolean isHibernating) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppHibernationService.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(isHibernating);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apphibernation.IAppHibernationService
            public List<String> getHibernatingPackagesForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppHibernationService.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.apphibernation.IAppHibernationService
            public Map<String, HibernationStats> getHibernationStatsForUser(List<String> packageNames, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                final Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppHibernationService.DESCRIPTOR);
                    _data.writeStringList(packageNames);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int N = _reply.readInt();
                    final Map<String, HibernationStats> _result = N < 0 ? null : new HashMap<>();
                    IntStream.range(0, N).forEach(new IntConsumer() { // from class: android.apphibernation.IAppHibernationService$Stub$Proxy$$ExternalSyntheticLambda0
                        @Override // java.util.function.IntConsumer
                        public final void accept(int i) {
                            IAppHibernationService.Stub.Proxy.lambda$getHibernationStatsForUser$0(Parcel.this, _result, i);
                        }
                    });
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static /* synthetic */ void lambda$getHibernationStatsForUser$0(Parcel _reply, Map _result, int i) {
                String k = _reply.readString();
                HibernationStats v = (HibernationStats) _reply.readTypedObject(HibernationStats.CREATOR);
                _result.put(k, v);
            }

            @Override // android.apphibernation.IAppHibernationService
            public boolean isOatArtifactDeletionEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAppHibernationService.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
