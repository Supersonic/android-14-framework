package android.content;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IRestrictionsManager extends IInterface {
    Intent createLocalApprovalIntent() throws RemoteException;

    Bundle getApplicationRestrictions(String str) throws RemoteException;

    List<Bundle> getApplicationRestrictionsPerAdminForUser(int i, String str) throws RemoteException;

    boolean hasRestrictionsProvider() throws RemoteException;

    void notifyPermissionResponse(String str, PersistableBundle persistableBundle) throws RemoteException;

    void requestPermission(String str, String str2, String str3, PersistableBundle persistableBundle) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IRestrictionsManager {
        @Override // android.content.IRestrictionsManager
        public Bundle getApplicationRestrictions(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.content.IRestrictionsManager
        public List<Bundle> getApplicationRestrictionsPerAdminForUser(int userId, String packageName) throws RemoteException {
            return null;
        }

        @Override // android.content.IRestrictionsManager
        public boolean hasRestrictionsProvider() throws RemoteException {
            return false;
        }

        @Override // android.content.IRestrictionsManager
        public void requestPermission(String packageName, String requestType, String requestId, PersistableBundle requestData) throws RemoteException {
        }

        @Override // android.content.IRestrictionsManager
        public void notifyPermissionResponse(String packageName, PersistableBundle response) throws RemoteException {
        }

        @Override // android.content.IRestrictionsManager
        public Intent createLocalApprovalIntent() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IRestrictionsManager {
        public static final String DESCRIPTOR = "android.content.IRestrictionsManager";
        static final int TRANSACTION_createLocalApprovalIntent = 6;
        static final int TRANSACTION_getApplicationRestrictions = 1;
        static final int TRANSACTION_getApplicationRestrictionsPerAdminForUser = 2;
        static final int TRANSACTION_hasRestrictionsProvider = 3;
        static final int TRANSACTION_notifyPermissionResponse = 5;
        static final int TRANSACTION_requestPermission = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRestrictionsManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRestrictionsManager)) {
                return (IRestrictionsManager) iin;
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
                    return "getApplicationRestrictions";
                case 2:
                    return "getApplicationRestrictionsPerAdminForUser";
                case 3:
                    return "hasRestrictionsProvider";
                case 4:
                    return "requestPermission";
                case 5:
                    return "notifyPermissionResponse";
                case 6:
                    return "createLocalApprovalIntent";
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
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            Bundle _result = getApplicationRestrictions(_arg0);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            List<Bundle> _result2 = getApplicationRestrictionsPerAdminForUser(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeTypedList(_result2, 1);
                            break;
                        case 3:
                            boolean _result3 = hasRestrictionsProvider();
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            String _arg03 = data.readString();
                            String _arg12 = data.readString();
                            String _arg2 = data.readString();
                            PersistableBundle _arg3 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            data.enforceNoDataAvail();
                            requestPermission(_arg03, _arg12, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 5:
                            String _arg04 = data.readString();
                            PersistableBundle _arg13 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            data.enforceNoDataAvail();
                            notifyPermissionResponse(_arg04, _arg13);
                            reply.writeNoException();
                            break;
                        case 6:
                            Intent _result4 = createLocalApprovalIntent();
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IRestrictionsManager {
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

            @Override // android.content.IRestrictionsManager
            public Bundle getApplicationRestrictions(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IRestrictionsManager
            public List<Bundle> getApplicationRestrictionsPerAdminForUser(int userId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(packageName);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    List<Bundle> _result = _reply.createTypedArrayList(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IRestrictionsManager
            public boolean hasRestrictionsProvider() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IRestrictionsManager
            public void requestPermission(String packageName, String requestType, String requestId, PersistableBundle requestData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(requestType);
                    _data.writeString(requestId);
                    _data.writeTypedObject(requestData, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IRestrictionsManager
            public void notifyPermissionResponse(String packageName, PersistableBundle response) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(response, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IRestrictionsManager
            public Intent createLocalApprovalIntent() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    Intent _result = (Intent) _reply.readTypedObject(Intent.CREATOR);
                    return _result;
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
