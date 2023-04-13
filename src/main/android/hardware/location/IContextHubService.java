package android.hardware.location;

import android.Manifest;
import android.app.ActivityThread;
import android.app.PendingIntent;
import android.content.AttributionSource;
import android.hardware.location.IContextHubCallback;
import android.hardware.location.IContextHubClient;
import android.hardware.location.IContextHubClientCallback;
import android.hardware.location.IContextHubTransactionCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface IContextHubService extends IInterface {
    IContextHubClient createClient(int i, IContextHubClientCallback iContextHubClientCallback, String str, String str2) throws RemoteException;

    IContextHubClient createPendingIntentClient(int i, PendingIntent pendingIntent, long j, String str) throws RemoteException;

    void disableNanoApp(int i, IContextHubTransactionCallback iContextHubTransactionCallback, long j) throws RemoteException;

    void enableNanoApp(int i, IContextHubTransactionCallback iContextHubTransactionCallback, long j) throws RemoteException;

    int[] findNanoAppOnHub(int i, NanoAppFilter nanoAppFilter) throws RemoteException;

    int[] getContextHubHandles() throws RemoteException;

    ContextHubInfo getContextHubInfo(int i) throws RemoteException;

    List<ContextHubInfo> getContextHubs() throws RemoteException;

    NanoAppInstanceInfo getNanoAppInstanceInfo(int i) throws RemoteException;

    long[] getPreloadedNanoAppIds(ContextHubInfo contextHubInfo) throws RemoteException;

    int loadNanoApp(int i, NanoApp nanoApp) throws RemoteException;

    void loadNanoAppOnHub(int i, IContextHubTransactionCallback iContextHubTransactionCallback, NanoAppBinary nanoAppBinary) throws RemoteException;

    void queryNanoApps(int i, IContextHubTransactionCallback iContextHubTransactionCallback) throws RemoteException;

    int registerCallback(IContextHubCallback iContextHubCallback) throws RemoteException;

    int sendMessage(int i, int i2, ContextHubMessage contextHubMessage) throws RemoteException;

    boolean setTestMode(boolean z) throws RemoteException;

    int unloadNanoApp(int i) throws RemoteException;

    void unloadNanoAppFromHub(int i, IContextHubTransactionCallback iContextHubTransactionCallback, long j) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IContextHubService {
        @Override // android.hardware.location.IContextHubService
        public int registerCallback(IContextHubCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.location.IContextHubService
        public int[] getContextHubHandles() throws RemoteException {
            return null;
        }

        @Override // android.hardware.location.IContextHubService
        public ContextHubInfo getContextHubInfo(int contextHubHandle) throws RemoteException {
            return null;
        }

        @Override // android.hardware.location.IContextHubService
        public int loadNanoApp(int contextHubHandle, NanoApp nanoApp) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.location.IContextHubService
        public int unloadNanoApp(int nanoAppHandle) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.location.IContextHubService
        public NanoAppInstanceInfo getNanoAppInstanceInfo(int nanoAppHandle) throws RemoteException {
            return null;
        }

        @Override // android.hardware.location.IContextHubService
        public int[] findNanoAppOnHub(int contextHubHandle, NanoAppFilter filter) throws RemoteException {
            return null;
        }

        @Override // android.hardware.location.IContextHubService
        public int sendMessage(int contextHubHandle, int nanoAppHandle, ContextHubMessage msg) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.location.IContextHubService
        public IContextHubClient createClient(int contextHubId, IContextHubClientCallback client, String attributionTag, String packageName) throws RemoteException {
            return null;
        }

        @Override // android.hardware.location.IContextHubService
        public IContextHubClient createPendingIntentClient(int contextHubId, PendingIntent pendingIntent, long nanoAppId, String attributionTag) throws RemoteException {
            return null;
        }

        @Override // android.hardware.location.IContextHubService
        public List<ContextHubInfo> getContextHubs() throws RemoteException {
            return null;
        }

        @Override // android.hardware.location.IContextHubService
        public void loadNanoAppOnHub(int contextHubId, IContextHubTransactionCallback transactionCallback, NanoAppBinary nanoAppBinary) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubService
        public void unloadNanoAppFromHub(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubService
        public void enableNanoApp(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubService
        public void disableNanoApp(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubService
        public void queryNanoApps(int contextHubId, IContextHubTransactionCallback transactionCallback) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubService
        public long[] getPreloadedNanoAppIds(ContextHubInfo hubInfo) throws RemoteException {
            return null;
        }

        @Override // android.hardware.location.IContextHubService
        public boolean setTestMode(boolean enable) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IContextHubService {
        public static final String DESCRIPTOR = "android.hardware.location.IContextHubService";
        static final int TRANSACTION_createClient = 9;
        static final int TRANSACTION_createPendingIntentClient = 10;
        static final int TRANSACTION_disableNanoApp = 15;
        static final int TRANSACTION_enableNanoApp = 14;
        static final int TRANSACTION_findNanoAppOnHub = 7;
        static final int TRANSACTION_getContextHubHandles = 2;
        static final int TRANSACTION_getContextHubInfo = 3;
        static final int TRANSACTION_getContextHubs = 11;
        static final int TRANSACTION_getNanoAppInstanceInfo = 6;
        static final int TRANSACTION_getPreloadedNanoAppIds = 17;
        static final int TRANSACTION_loadNanoApp = 4;
        static final int TRANSACTION_loadNanoAppOnHub = 12;
        static final int TRANSACTION_queryNanoApps = 16;
        static final int TRANSACTION_registerCallback = 1;
        static final int TRANSACTION_sendMessage = 8;
        static final int TRANSACTION_setTestMode = 18;
        static final int TRANSACTION_unloadNanoApp = 5;
        static final int TRANSACTION_unloadNanoAppFromHub = 13;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IContextHubService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IContextHubService)) {
                return (IContextHubService) iin;
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
                    return "registerCallback";
                case 2:
                    return "getContextHubHandles";
                case 3:
                    return "getContextHubInfo";
                case 4:
                    return "loadNanoApp";
                case 5:
                    return "unloadNanoApp";
                case 6:
                    return "getNanoAppInstanceInfo";
                case 7:
                    return "findNanoAppOnHub";
                case 8:
                    return "sendMessage";
                case 9:
                    return "createClient";
                case 10:
                    return "createPendingIntentClient";
                case 11:
                    return "getContextHubs";
                case 12:
                    return "loadNanoAppOnHub";
                case 13:
                    return "unloadNanoAppFromHub";
                case 14:
                    return "enableNanoApp";
                case 15:
                    return "disableNanoApp";
                case 16:
                    return "queryNanoApps";
                case 17:
                    return "getPreloadedNanoAppIds";
                case 18:
                    return "setTestMode";
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
                            IContextHubCallback _arg0 = IContextHubCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result = registerCallback(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int[] _result2 = getContextHubHandles();
                            reply.writeNoException();
                            reply.writeIntArray(_result2);
                            break;
                        case 3:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            ContextHubInfo _result3 = getContextHubInfo(_arg02);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            NanoApp _arg1 = (NanoApp) data.readTypedObject(NanoApp.CREATOR);
                            data.enforceNoDataAvail();
                            int _result4 = loadNanoApp(_arg03, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result5 = unloadNanoApp(_arg04);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            NanoAppInstanceInfo _result6 = getNanoAppInstanceInfo(_arg05);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 7:
                            int _arg06 = data.readInt();
                            NanoAppFilter _arg12 = (NanoAppFilter) data.readTypedObject(NanoAppFilter.CREATOR);
                            data.enforceNoDataAvail();
                            int[] _result7 = findNanoAppOnHub(_arg06, _arg12);
                            reply.writeNoException();
                            reply.writeIntArray(_result7);
                            break;
                        case 8:
                            int _arg07 = data.readInt();
                            int _arg13 = data.readInt();
                            ContextHubMessage _arg2 = (ContextHubMessage) data.readTypedObject(ContextHubMessage.CREATOR);
                            data.enforceNoDataAvail();
                            int _result8 = sendMessage(_arg07, _arg13, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            IContextHubClientCallback _arg14 = IContextHubClientCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg22 = data.readString();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            IContextHubClient _result9 = createClient(_arg08, _arg14, _arg22, _arg3);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result9);
                            break;
                        case 10:
                            int _arg09 = data.readInt();
                            PendingIntent _arg15 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            long _arg23 = data.readLong();
                            String _arg32 = data.readString();
                            data.enforceNoDataAvail();
                            IContextHubClient _result10 = createPendingIntentClient(_arg09, _arg15, _arg23, _arg32);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result10);
                            break;
                        case 11:
                            List<ContextHubInfo> _result11 = getContextHubs();
                            reply.writeNoException();
                            reply.writeTypedList(_result11, 1);
                            break;
                        case 12:
                            int _arg010 = data.readInt();
                            IContextHubTransactionCallback _arg16 = IContextHubTransactionCallback.Stub.asInterface(data.readStrongBinder());
                            NanoAppBinary _arg24 = (NanoAppBinary) data.readTypedObject(NanoAppBinary.CREATOR);
                            data.enforceNoDataAvail();
                            loadNanoAppOnHub(_arg010, _arg16, _arg24);
                            reply.writeNoException();
                            break;
                        case 13:
                            int _arg011 = data.readInt();
                            IContextHubTransactionCallback _arg17 = IContextHubTransactionCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg25 = data.readLong();
                            data.enforceNoDataAvail();
                            unloadNanoAppFromHub(_arg011, _arg17, _arg25);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg012 = data.readInt();
                            IContextHubTransactionCallback _arg18 = IContextHubTransactionCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg26 = data.readLong();
                            data.enforceNoDataAvail();
                            enableNanoApp(_arg012, _arg18, _arg26);
                            reply.writeNoException();
                            break;
                        case 15:
                            int _arg013 = data.readInt();
                            IContextHubTransactionCallback _arg19 = IContextHubTransactionCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg27 = data.readLong();
                            data.enforceNoDataAvail();
                            disableNanoApp(_arg013, _arg19, _arg27);
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg014 = data.readInt();
                            IContextHubTransactionCallback _arg110 = IContextHubTransactionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            queryNanoApps(_arg014, _arg110);
                            reply.writeNoException();
                            break;
                        case 17:
                            ContextHubInfo _arg015 = (ContextHubInfo) data.readTypedObject(ContextHubInfo.CREATOR);
                            data.enforceNoDataAvail();
                            long[] _result12 = getPreloadedNanoAppIds(_arg015);
                            reply.writeNoException();
                            reply.writeLongArray(_result12);
                            break;
                        case 18:
                            boolean _arg016 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result13 = setTestMode(_arg016);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IContextHubService {
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

            @Override // android.hardware.location.IContextHubService
            public int registerCallback(IContextHubCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public int[] getContextHubHandles() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public ContextHubInfo getContextHubInfo(int contextHubHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubHandle);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ContextHubInfo _result = (ContextHubInfo) _reply.readTypedObject(ContextHubInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public int loadNanoApp(int contextHubHandle, NanoApp nanoApp) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubHandle);
                    _data.writeTypedObject(nanoApp, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public int unloadNanoApp(int nanoAppHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nanoAppHandle);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public NanoAppInstanceInfo getNanoAppInstanceInfo(int nanoAppHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nanoAppHandle);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    NanoAppInstanceInfo _result = (NanoAppInstanceInfo) _reply.readTypedObject(NanoAppInstanceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public int[] findNanoAppOnHub(int contextHubHandle, NanoAppFilter filter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubHandle);
                    _data.writeTypedObject(filter, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public int sendMessage(int contextHubHandle, int nanoAppHandle, ContextHubMessage msg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubHandle);
                    _data.writeInt(nanoAppHandle);
                    _data.writeTypedObject(msg, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public IContextHubClient createClient(int contextHubId, IContextHubClientCallback client, String attributionTag, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongInterface(client);
                    _data.writeString(attributionTag);
                    _data.writeString(packageName);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    IContextHubClient _result = IContextHubClient.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public IContextHubClient createPendingIntentClient(int contextHubId, PendingIntent pendingIntent, long nanoAppId, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeTypedObject(pendingIntent, 0);
                    _data.writeLong(nanoAppId);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    IContextHubClient _result = IContextHubClient.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public List<ContextHubInfo> getContextHubs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    List<ContextHubInfo> _result = _reply.createTypedArrayList(ContextHubInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public void loadNanoAppOnHub(int contextHubId, IContextHubTransactionCallback transactionCallback, NanoAppBinary nanoAppBinary) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongInterface(transactionCallback);
                    _data.writeTypedObject(nanoAppBinary, 0);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public void unloadNanoAppFromHub(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongInterface(transactionCallback);
                    _data.writeLong(nanoAppId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public void enableNanoApp(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongInterface(transactionCallback);
                    _data.writeLong(nanoAppId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public void disableNanoApp(int contextHubId, IContextHubTransactionCallback transactionCallback, long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongInterface(transactionCallback);
                    _data.writeLong(nanoAppId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public void queryNanoApps(int contextHubId, IContextHubTransactionCallback transactionCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongInterface(transactionCallback);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public long[] getPreloadedNanoAppIds(ContextHubInfo hubInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(hubInfo, 0);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    long[] _result = _reply.createLongArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubService
            public boolean setTestMode(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void registerCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void getContextHubHandles_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void getContextHubInfo_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void loadNanoApp_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void unloadNanoApp_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void getNanoAppInstanceInfo_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void findNanoAppOnHub_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void sendMessage_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void createClient_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void createPendingIntentClient_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void getContextHubs_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void loadNanoAppOnHub_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void unloadNanoAppFromHub_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void enableNanoApp_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void disableNanoApp_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void queryNanoApps_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void getPreloadedNanoAppIds_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        protected void setTestMode_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_CONTEXT_HUB, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 17;
        }
    }
}
