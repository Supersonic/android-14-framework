package android.media;

import android.media.IResourceManagerClient;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IResourceManagerService extends IInterface {
    public static final String DESCRIPTOR = "android.media.IResourceManagerService";
    public static final String kPolicySupportsMultipleSecureCodecs = "supports-multiple-secure-codecs";
    public static final String kPolicySupportsSecureWithNonSecureCodec = "supports-secure-with-non-secure-codec";

    void addResource(ClientInfoParcel clientInfoParcel, IResourceManagerClient iResourceManagerClient, MediaResourceParcel[] mediaResourceParcelArr) throws RemoteException;

    void config(MediaResourcePolicyParcel[] mediaResourcePolicyParcelArr) throws RemoteException;

    void markClientForPendingRemoval(ClientInfoParcel clientInfoParcel) throws RemoteException;

    void overridePid(int i, int i2) throws RemoteException;

    void overrideProcessInfo(IResourceManagerClient iResourceManagerClient, int i, int i2, int i3) throws RemoteException;

    boolean reclaimResource(ClientInfoParcel clientInfoParcel, MediaResourceParcel[] mediaResourceParcelArr) throws RemoteException;

    void reclaimResourcesFromClientsPendingRemoval(int i) throws RemoteException;

    void removeClient(ClientInfoParcel clientInfoParcel) throws RemoteException;

    void removeResource(ClientInfoParcel clientInfoParcel, MediaResourceParcel[] mediaResourceParcelArr) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IResourceManagerService {
        @Override // android.media.IResourceManagerService
        public void config(MediaResourcePolicyParcel[] policies) throws RemoteException {
        }

        @Override // android.media.IResourceManagerService
        public void addResource(ClientInfoParcel clientInfo, IResourceManagerClient client, MediaResourceParcel[] resources) throws RemoteException {
        }

        @Override // android.media.IResourceManagerService
        public void removeResource(ClientInfoParcel clientInfo, MediaResourceParcel[] resources) throws RemoteException {
        }

        @Override // android.media.IResourceManagerService
        public void removeClient(ClientInfoParcel clientInfo) throws RemoteException {
        }

        @Override // android.media.IResourceManagerService
        public boolean reclaimResource(ClientInfoParcel clientInfo, MediaResourceParcel[] resources) throws RemoteException {
            return false;
        }

        @Override // android.media.IResourceManagerService
        public void overridePid(int originalPid, int newPid) throws RemoteException {
        }

        @Override // android.media.IResourceManagerService
        public void overrideProcessInfo(IResourceManagerClient client, int pid, int procState, int oomScore) throws RemoteException {
        }

        @Override // android.media.IResourceManagerService
        public void markClientForPendingRemoval(ClientInfoParcel clientInfo) throws RemoteException {
        }

        @Override // android.media.IResourceManagerService
        public void reclaimResourcesFromClientsPendingRemoval(int pid) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IResourceManagerService {
        static final int TRANSACTION_addResource = 2;
        static final int TRANSACTION_config = 1;
        static final int TRANSACTION_markClientForPendingRemoval = 8;
        static final int TRANSACTION_overridePid = 6;
        static final int TRANSACTION_overrideProcessInfo = 7;
        static final int TRANSACTION_reclaimResource = 5;
        static final int TRANSACTION_reclaimResourcesFromClientsPendingRemoval = 9;
        static final int TRANSACTION_removeClient = 4;
        static final int TRANSACTION_removeResource = 3;

        public Stub() {
            attachInterface(this, IResourceManagerService.DESCRIPTOR);
        }

        public static IResourceManagerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IResourceManagerService.DESCRIPTOR);
            if (iin != null && (iin instanceof IResourceManagerService)) {
                return (IResourceManagerService) iin;
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
                    return "config";
                case 2:
                    return "addResource";
                case 3:
                    return "removeResource";
                case 4:
                    return "removeClient";
                case 5:
                    return "reclaimResource";
                case 6:
                    return "overridePid";
                case 7:
                    return "overrideProcessInfo";
                case 8:
                    return "markClientForPendingRemoval";
                case 9:
                    return "reclaimResourcesFromClientsPendingRemoval";
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
                data.enforceInterface(IResourceManagerService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IResourceManagerService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            MediaResourcePolicyParcel[] _arg0 = (MediaResourcePolicyParcel[]) data.createTypedArray(MediaResourcePolicyParcel.CREATOR);
                            data.enforceNoDataAvail();
                            config(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            ClientInfoParcel _arg02 = (ClientInfoParcel) data.readTypedObject(ClientInfoParcel.CREATOR);
                            IResourceManagerClient _arg1 = IResourceManagerClient.Stub.asInterface(data.readStrongBinder());
                            MediaResourceParcel[] _arg2 = (MediaResourceParcel[]) data.createTypedArray(MediaResourceParcel.CREATOR);
                            data.enforceNoDataAvail();
                            addResource(_arg02, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 3:
                            ClientInfoParcel _arg03 = (ClientInfoParcel) data.readTypedObject(ClientInfoParcel.CREATOR);
                            MediaResourceParcel[] _arg12 = (MediaResourceParcel[]) data.createTypedArray(MediaResourceParcel.CREATOR);
                            data.enforceNoDataAvail();
                            removeResource(_arg03, _arg12);
                            reply.writeNoException();
                            break;
                        case 4:
                            ClientInfoParcel _arg04 = (ClientInfoParcel) data.readTypedObject(ClientInfoParcel.CREATOR);
                            data.enforceNoDataAvail();
                            removeClient(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            ClientInfoParcel _arg05 = (ClientInfoParcel) data.readTypedObject(ClientInfoParcel.CREATOR);
                            MediaResourceParcel[] _arg13 = (MediaResourceParcel[]) data.createTypedArray(MediaResourceParcel.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result = reclaimResource(_arg05, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            overridePid(_arg06, _arg14);
                            reply.writeNoException();
                            break;
                        case 7:
                            IResourceManagerClient _arg07 = IResourceManagerClient.Stub.asInterface(data.readStrongBinder());
                            int _arg15 = data.readInt();
                            int _arg22 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            overrideProcessInfo(_arg07, _arg15, _arg22, _arg3);
                            reply.writeNoException();
                            break;
                        case 8:
                            ClientInfoParcel _arg08 = (ClientInfoParcel) data.readTypedObject(ClientInfoParcel.CREATOR);
                            data.enforceNoDataAvail();
                            markClientForPendingRemoval(_arg08);
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            reclaimResourcesFromClientsPendingRemoval(_arg09);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IResourceManagerService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IResourceManagerService.DESCRIPTOR;
            }

            @Override // android.media.IResourceManagerService
            public void config(MediaResourcePolicyParcel[] policies) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IResourceManagerService.DESCRIPTOR);
                    _data.writeTypedArray(policies, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IResourceManagerService
            public void addResource(ClientInfoParcel clientInfo, IResourceManagerClient client, MediaResourceParcel[] resources) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IResourceManagerService.DESCRIPTOR);
                    _data.writeTypedObject(clientInfo, 0);
                    _data.writeStrongInterface(client);
                    _data.writeTypedArray(resources, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IResourceManagerService
            public void removeResource(ClientInfoParcel clientInfo, MediaResourceParcel[] resources) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IResourceManagerService.DESCRIPTOR);
                    _data.writeTypedObject(clientInfo, 0);
                    _data.writeTypedArray(resources, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IResourceManagerService
            public void removeClient(ClientInfoParcel clientInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IResourceManagerService.DESCRIPTOR);
                    _data.writeTypedObject(clientInfo, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IResourceManagerService
            public boolean reclaimResource(ClientInfoParcel clientInfo, MediaResourceParcel[] resources) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IResourceManagerService.DESCRIPTOR);
                    _data.writeTypedObject(clientInfo, 0);
                    _data.writeTypedArray(resources, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IResourceManagerService
            public void overridePid(int originalPid, int newPid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IResourceManagerService.DESCRIPTOR);
                    _data.writeInt(originalPid);
                    _data.writeInt(newPid);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IResourceManagerService
            public void overrideProcessInfo(IResourceManagerClient client, int pid, int procState, int oomScore) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IResourceManagerService.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeInt(pid);
                    _data.writeInt(procState);
                    _data.writeInt(oomScore);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IResourceManagerService
            public void markClientForPendingRemoval(ClientInfoParcel clientInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IResourceManagerService.DESCRIPTOR);
                    _data.writeTypedObject(clientInfo, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IResourceManagerService
            public void reclaimResourcesFromClientsPendingRemoval(int pid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IResourceManagerService.DESCRIPTOR);
                    _data.writeInt(pid);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
