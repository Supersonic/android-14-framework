package android.content.p000om;

import android.content.p000om.IOverlayManager;
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
/* renamed from: android.content.om.IOverlayManager */
/* loaded from: classes.dex */
public interface IOverlayManager extends IInterface {
    void commit(OverlayManagerTransaction overlayManagerTransaction) throws RemoteException;

    Map<String, List<OverlayInfo>> getAllOverlays(int i) throws RemoteException;

    String[] getDefaultOverlayPackages() throws RemoteException;

    OverlayInfo getOverlayInfo(String str, int i) throws RemoteException;

    OverlayInfo getOverlayInfoByIdentifier(OverlayIdentifier overlayIdentifier, int i) throws RemoteException;

    List<OverlayInfo> getOverlayInfosForTarget(String str, int i) throws RemoteException;

    void invalidateCachesForOverlay(String str, int i) throws RemoteException;

    boolean setEnabled(String str, boolean z, int i) throws RemoteException;

    boolean setEnabledExclusive(String str, boolean z, int i) throws RemoteException;

    boolean setEnabledExclusiveInCategory(String str, int i) throws RemoteException;

    boolean setHighestPriority(String str, int i) throws RemoteException;

    boolean setLowestPriority(String str, int i) throws RemoteException;

    boolean setPriority(String str, String str2, int i) throws RemoteException;

    /* renamed from: android.content.om.IOverlayManager$Default */
    /* loaded from: classes.dex */
    public static class Default implements IOverlayManager {
        @Override // android.content.p000om.IOverlayManager
        public Map<String, List<OverlayInfo>> getAllOverlays(int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p000om.IOverlayManager
        public List<OverlayInfo> getOverlayInfosForTarget(String targetPackageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p000om.IOverlayManager
        public OverlayInfo getOverlayInfo(String packageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p000om.IOverlayManager
        public OverlayInfo getOverlayInfoByIdentifier(OverlayIdentifier packageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p000om.IOverlayManager
        public boolean setEnabled(String packageName, boolean enable, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p000om.IOverlayManager
        public boolean setEnabledExclusive(String packageName, boolean enable, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p000om.IOverlayManager
        public boolean setEnabledExclusiveInCategory(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p000om.IOverlayManager
        public boolean setPriority(String packageName, String newParentPackageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p000om.IOverlayManager
        public boolean setHighestPriority(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p000om.IOverlayManager
        public boolean setLowestPriority(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p000om.IOverlayManager
        public String[] getDefaultOverlayPackages() throws RemoteException {
            return null;
        }

        @Override // android.content.p000om.IOverlayManager
        public void invalidateCachesForOverlay(String packageName, int userId) throws RemoteException {
        }

        @Override // android.content.p000om.IOverlayManager
        public void commit(OverlayManagerTransaction transaction) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.om.IOverlayManager$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IOverlayManager {
        public static final String DESCRIPTOR = "android.content.om.IOverlayManager";
        static final int TRANSACTION_commit = 13;
        static final int TRANSACTION_getAllOverlays = 1;
        static final int TRANSACTION_getDefaultOverlayPackages = 11;
        static final int TRANSACTION_getOverlayInfo = 3;
        static final int TRANSACTION_getOverlayInfoByIdentifier = 4;
        static final int TRANSACTION_getOverlayInfosForTarget = 2;
        static final int TRANSACTION_invalidateCachesForOverlay = 12;
        static final int TRANSACTION_setEnabled = 5;
        static final int TRANSACTION_setEnabledExclusive = 6;
        static final int TRANSACTION_setEnabledExclusiveInCategory = 7;
        static final int TRANSACTION_setHighestPriority = 9;
        static final int TRANSACTION_setLowestPriority = 10;
        static final int TRANSACTION_setPriority = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOverlayManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IOverlayManager)) {
                return (IOverlayManager) iin;
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
                    return "getAllOverlays";
                case 2:
                    return "getOverlayInfosForTarget";
                case 3:
                    return "getOverlayInfo";
                case 4:
                    return "getOverlayInfoByIdentifier";
                case 5:
                    return "setEnabled";
                case 6:
                    return "setEnabledExclusive";
                case 7:
                    return "setEnabledExclusiveInCategory";
                case 8:
                    return "setPriority";
                case 9:
                    return "setHighestPriority";
                case 10:
                    return "setLowestPriority";
                case 11:
                    return "getDefaultOverlayPackages";
                case 12:
                    return "invalidateCachesForOverlay";
                case 13:
                    return "commit";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            Map<String, List<OverlayInfo>> _result = getAllOverlays(_arg0);
                            reply.writeNoException();
                            if (_result == null) {
                                reply.writeInt(-1);
                                break;
                            } else {
                                reply.writeInt(_result.size());
                                _result.forEach(new BiConsumer() { // from class: android.content.om.IOverlayManager$Stub$$ExternalSyntheticLambda0
                                    @Override // java.util.function.BiConsumer
                                    public final void accept(Object obj, Object obj2) {
                                        IOverlayManager.Stub.lambda$onTransact$0(Parcel.this, (String) obj, (List) obj2);
                                    }
                                });
                                break;
                            }
                        case 2:
                            String _arg02 = data.readString();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            List<OverlayInfo> _result2 = getOverlayInfosForTarget(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeTypedList(_result2, 1);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            OverlayInfo _result3 = getOverlayInfo(_arg03, _arg12);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            OverlayIdentifier _arg04 = (OverlayIdentifier) data.readTypedObject(OverlayIdentifier.CREATOR);
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            OverlayInfo _result4 = getOverlayInfoByIdentifier(_arg04, _arg13);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            boolean _arg14 = data.readBoolean();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = setEnabled(_arg05, _arg14, _arg2);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            boolean _arg15 = data.readBoolean();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result6 = setEnabledExclusive(_arg06, _arg15, _arg22);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = setEnabledExclusiveInCategory(_arg07, _arg16);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            String _arg17 = data.readString();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result8 = setPriority(_arg08, _arg17, _arg23);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result9 = setHighestPriority(_arg09, _arg18);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result10 = setLowestPriority(_arg010, _arg19);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 11:
                            String[] _result11 = getDefaultOverlayPackages();
                            reply.writeNoException();
                            reply.writeStringArray(_result11);
                            break;
                        case 12:
                            String _arg011 = data.readString();
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            invalidateCachesForOverlay(_arg011, _arg110);
                            reply.writeNoException();
                            break;
                        case 13:
                            OverlayManagerTransaction _arg012 = (OverlayManagerTransaction) data.readTypedObject(OverlayManagerTransaction.CREATOR);
                            data.enforceNoDataAvail();
                            commit(_arg012);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onTransact$0(Parcel reply, String k, List v) {
            reply.writeString(k);
            reply.writeTypedList(v, 1);
        }

        /* renamed from: android.content.om.IOverlayManager$Stub$Proxy */
        /* loaded from: classes.dex */
        private static class Proxy implements IOverlayManager {
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

            @Override // android.content.p000om.IOverlayManager
            public Map<String, List<OverlayInfo>> getAllOverlays(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                final Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int N = _reply.readInt();
                    final Map<String, List<OverlayInfo>> _result = N < 0 ? null : new HashMap<>();
                    IntStream.range(0, N).forEach(new IntConsumer() { // from class: android.content.om.IOverlayManager$Stub$Proxy$$ExternalSyntheticLambda0
                        @Override // java.util.function.IntConsumer
                        public final void accept(int i) {
                            IOverlayManager.Stub.Proxy.lambda$getAllOverlays$0(Parcel.this, _result, i);
                        }
                    });
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static /* synthetic */ void lambda$getAllOverlays$0(Parcel _reply, Map _result, int i) {
                String k = _reply.readString();
                _result.put(k, _reply.createTypedArrayList(OverlayInfo.CREATOR));
            }

            @Override // android.content.p000om.IOverlayManager
            public List<OverlayInfo> getOverlayInfosForTarget(String targetPackageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPackageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    List<OverlayInfo> _result = _reply.createTypedArrayList(OverlayInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public OverlayInfo getOverlayInfo(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    OverlayInfo _result = (OverlayInfo) _reply.readTypedObject(OverlayInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public OverlayInfo getOverlayInfoByIdentifier(OverlayIdentifier packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(packageName, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    OverlayInfo _result = (OverlayInfo) _reply.readTypedObject(OverlayInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public boolean setEnabled(String packageName, boolean enable, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(enable);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public boolean setEnabledExclusive(String packageName, boolean enable, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(enable);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public boolean setEnabledExclusiveInCategory(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public boolean setPriority(String packageName, String newParentPackageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(newParentPackageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public boolean setHighestPriority(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public boolean setLowestPriority(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public String[] getDefaultOverlayPackages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public void invalidateCachesForOverlay(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p000om.IOverlayManager
            public void commit(OverlayManagerTransaction transaction) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(transaction, 0);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 12;
        }
    }
}
