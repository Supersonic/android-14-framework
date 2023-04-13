package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface ISurfaceSyncGroup extends IInterface {
    public static final String DESCRIPTOR = "android.window.ISurfaceSyncGroup";

    boolean addToSync(ISurfaceSyncGroup iSurfaceSyncGroup, boolean z) throws RemoteException;

    boolean onAddedToSyncGroup(IBinder iBinder, boolean z) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ISurfaceSyncGroup {
        @Override // android.window.ISurfaceSyncGroup
        public boolean onAddedToSyncGroup(IBinder parentSyncGroupToken, boolean parentSyncGroupMerge) throws RemoteException {
            return false;
        }

        @Override // android.window.ISurfaceSyncGroup
        public boolean addToSync(ISurfaceSyncGroup surfaceSyncGroup, boolean parentSyncGroupMerge) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ISurfaceSyncGroup {
        static final int TRANSACTION_addToSync = 2;
        static final int TRANSACTION_onAddedToSyncGroup = 1;

        public Stub() {
            attachInterface(this, ISurfaceSyncGroup.DESCRIPTOR);
        }

        public static ISurfaceSyncGroup asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISurfaceSyncGroup.DESCRIPTOR);
            if (iin != null && (iin instanceof ISurfaceSyncGroup)) {
                return (ISurfaceSyncGroup) iin;
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
                    return "onAddedToSyncGroup";
                case 2:
                    return "addToSync";
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
                data.enforceInterface(ISurfaceSyncGroup.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISurfaceSyncGroup.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result = onAddedToSyncGroup(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            ISurfaceSyncGroup _arg02 = asInterface(data.readStrongBinder());
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result2 = addToSync(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements ISurfaceSyncGroup {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISurfaceSyncGroup.DESCRIPTOR;
            }

            @Override // android.window.ISurfaceSyncGroup
            public boolean onAddedToSyncGroup(IBinder parentSyncGroupToken, boolean parentSyncGroupMerge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISurfaceSyncGroup.DESCRIPTOR);
                    _data.writeStrongBinder(parentSyncGroupToken);
                    _data.writeBoolean(parentSyncGroupMerge);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.window.ISurfaceSyncGroup
            public boolean addToSync(ISurfaceSyncGroup surfaceSyncGroup, boolean parentSyncGroupMerge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISurfaceSyncGroup.DESCRIPTOR);
                    _data.writeStrongInterface(surfaceSyncGroup);
                    _data.writeBoolean(parentSyncGroupMerge);
                    this.mRemote.transact(2, _data, _reply, 0);
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
            return 1;
        }
    }
}
