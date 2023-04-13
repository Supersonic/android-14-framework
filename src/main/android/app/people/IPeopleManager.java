package android.app.people;

import android.app.people.IConversationListener;
import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IPeopleManager extends IInterface {
    public static final String DESCRIPTOR = "android.app.people.IPeopleManager";

    void addOrUpdateStatus(String str, int i, String str2, ConversationStatus conversationStatus) throws RemoteException;

    void clearStatus(String str, int i, String str2, String str3) throws RemoteException;

    void clearStatuses(String str, int i, String str2) throws RemoteException;

    ConversationChannel getConversation(String str, int i, String str2) throws RemoteException;

    long getLastInteraction(String str, int i, String str2) throws RemoteException;

    ParceledListSlice getRecentConversations() throws RemoteException;

    ParceledListSlice getStatuses(String str, int i, String str2) throws RemoteException;

    boolean isConversation(String str, int i, String str2) throws RemoteException;

    void registerConversationListener(String str, int i, String str2, IConversationListener iConversationListener) throws RemoteException;

    void removeAllRecentConversations() throws RemoteException;

    void removeRecentConversation(String str, int i, String str2) throws RemoteException;

    void unregisterConversationListener(IConversationListener iConversationListener) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IPeopleManager {
        @Override // android.app.people.IPeopleManager
        public ConversationChannel getConversation(String packageName, int userId, String shortcutId) throws RemoteException {
            return null;
        }

        @Override // android.app.people.IPeopleManager
        public ParceledListSlice getRecentConversations() throws RemoteException {
            return null;
        }

        @Override // android.app.people.IPeopleManager
        public void removeRecentConversation(String packageName, int userId, String shortcutId) throws RemoteException {
        }

        @Override // android.app.people.IPeopleManager
        public void removeAllRecentConversations() throws RemoteException {
        }

        @Override // android.app.people.IPeopleManager
        public boolean isConversation(String packageName, int userId, String shortcutId) throws RemoteException {
            return false;
        }

        @Override // android.app.people.IPeopleManager
        public long getLastInteraction(String packageName, int userId, String shortcutId) throws RemoteException {
            return 0L;
        }

        @Override // android.app.people.IPeopleManager
        public void addOrUpdateStatus(String packageName, int userId, String conversationId, ConversationStatus status) throws RemoteException {
        }

        @Override // android.app.people.IPeopleManager
        public void clearStatus(String packageName, int userId, String conversationId, String statusId) throws RemoteException {
        }

        @Override // android.app.people.IPeopleManager
        public void clearStatuses(String packageName, int userId, String conversationId) throws RemoteException {
        }

        @Override // android.app.people.IPeopleManager
        public ParceledListSlice getStatuses(String packageName, int userId, String conversationId) throws RemoteException {
            return null;
        }

        @Override // android.app.people.IPeopleManager
        public void registerConversationListener(String packageName, int userId, String shortcutId, IConversationListener callback) throws RemoteException {
        }

        @Override // android.app.people.IPeopleManager
        public void unregisterConversationListener(IConversationListener callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IPeopleManager {
        static final int TRANSACTION_addOrUpdateStatus = 7;
        static final int TRANSACTION_clearStatus = 8;
        static final int TRANSACTION_clearStatuses = 9;
        static final int TRANSACTION_getConversation = 1;
        static final int TRANSACTION_getLastInteraction = 6;
        static final int TRANSACTION_getRecentConversations = 2;
        static final int TRANSACTION_getStatuses = 10;
        static final int TRANSACTION_isConversation = 5;
        static final int TRANSACTION_registerConversationListener = 11;
        static final int TRANSACTION_removeAllRecentConversations = 4;
        static final int TRANSACTION_removeRecentConversation = 3;
        static final int TRANSACTION_unregisterConversationListener = 12;

        public Stub() {
            attachInterface(this, IPeopleManager.DESCRIPTOR);
        }

        public static IPeopleManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPeopleManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IPeopleManager)) {
                return (IPeopleManager) iin;
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
                    return "getConversation";
                case 2:
                    return "getRecentConversations";
                case 3:
                    return "removeRecentConversation";
                case 4:
                    return "removeAllRecentConversations";
                case 5:
                    return "isConversation";
                case 6:
                    return "getLastInteraction";
                case 7:
                    return "addOrUpdateStatus";
                case 8:
                    return "clearStatus";
                case 9:
                    return "clearStatuses";
                case 10:
                    return "getStatuses";
                case 11:
                    return "registerConversationListener";
                case 12:
                    return "unregisterConversationListener";
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
                data.enforceInterface(IPeopleManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPeopleManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            ConversationChannel _result = getConversation(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            ParceledListSlice _result2 = getRecentConversations();
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            removeRecentConversation(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 4:
                            removeAllRecentConversations();
                            reply.writeNoException();
                            break;
                        case 5:
                            String _arg03 = data.readString();
                            int _arg13 = data.readInt();
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result3 = isConversation(_arg03, _arg13, _arg23);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 6:
                            String _arg04 = data.readString();
                            int _arg14 = data.readInt();
                            String _arg24 = data.readString();
                            data.enforceNoDataAvail();
                            long _result4 = getLastInteraction(_arg04, _arg14, _arg24);
                            reply.writeNoException();
                            reply.writeLong(_result4);
                            break;
                        case 7:
                            String _arg05 = data.readString();
                            int _arg15 = data.readInt();
                            String _arg25 = data.readString();
                            ConversationStatus _arg3 = (ConversationStatus) data.readTypedObject(ConversationStatus.CREATOR);
                            data.enforceNoDataAvail();
                            addOrUpdateStatus(_arg05, _arg15, _arg25, _arg3);
                            reply.writeNoException();
                            break;
                        case 8:
                            String _arg06 = data.readString();
                            int _arg16 = data.readInt();
                            String _arg26 = data.readString();
                            String _arg32 = data.readString();
                            data.enforceNoDataAvail();
                            clearStatus(_arg06, _arg16, _arg26, _arg32);
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg07 = data.readString();
                            int _arg17 = data.readInt();
                            String _arg27 = data.readString();
                            data.enforceNoDataAvail();
                            clearStatuses(_arg07, _arg17, _arg27);
                            reply.writeNoException();
                            break;
                        case 10:
                            String _arg08 = data.readString();
                            int _arg18 = data.readInt();
                            String _arg28 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result5 = getStatuses(_arg08, _arg18, _arg28);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 11:
                            String _arg09 = data.readString();
                            int _arg19 = data.readInt();
                            String _arg29 = data.readString();
                            IConversationListener _arg33 = IConversationListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerConversationListener(_arg09, _arg19, _arg29, _arg33);
                            reply.writeNoException();
                            break;
                        case 12:
                            IConversationListener _arg010 = IConversationListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterConversationListener(_arg010);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IPeopleManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPeopleManager.DESCRIPTOR;
            }

            @Override // android.app.people.IPeopleManager
            public ConversationChannel getConversation(String packageName, int userId, String shortcutId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(shortcutId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ConversationChannel _result = (ConversationChannel) _reply.readTypedObject(ConversationChannel.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public ParceledListSlice getRecentConversations() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public void removeRecentConversation(String packageName, int userId, String shortcutId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(shortcutId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public void removeAllRecentConversations() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public boolean isConversation(String packageName, int userId, String shortcutId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(shortcutId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public long getLastInteraction(String packageName, int userId, String shortcutId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(shortcutId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public void addOrUpdateStatus(String packageName, int userId, String conversationId, ConversationStatus status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(conversationId);
                    _data.writeTypedObject(status, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public void clearStatus(String packageName, int userId, String conversationId, String statusId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(conversationId);
                    _data.writeString(statusId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public void clearStatuses(String packageName, int userId, String conversationId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(conversationId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public ParceledListSlice getStatuses(String packageName, int userId, String conversationId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(conversationId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public void registerConversationListener(String packageName, int userId, String shortcutId, IConversationListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(shortcutId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.people.IPeopleManager
            public void unregisterConversationListener(IConversationListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPeopleManager.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
