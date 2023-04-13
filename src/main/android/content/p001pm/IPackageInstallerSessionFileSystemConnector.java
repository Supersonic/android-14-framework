package android.content.p001pm;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
/* renamed from: android.content.pm.IPackageInstallerSessionFileSystemConnector */
/* loaded from: classes.dex */
public interface IPackageInstallerSessionFileSystemConnector extends IInterface {
    public static final String DESCRIPTOR = "android.content.pm.IPackageInstallerSessionFileSystemConnector";

    void writeData(String str, long j, long j2, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    /* renamed from: android.content.pm.IPackageInstallerSessionFileSystemConnector$Default */
    /* loaded from: classes.dex */
    public static class Default implements IPackageInstallerSessionFileSystemConnector {
        @Override // android.content.p001pm.IPackageInstallerSessionFileSystemConnector
        public void writeData(String name, long offsetBytes, long lengthBytes, ParcelFileDescriptor fd) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.pm.IPackageInstallerSessionFileSystemConnector$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IPackageInstallerSessionFileSystemConnector {
        static final int TRANSACTION_writeData = 1;

        public Stub() {
            attachInterface(this, IPackageInstallerSessionFileSystemConnector.DESCRIPTOR);
        }

        public static IPackageInstallerSessionFileSystemConnector asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPackageInstallerSessionFileSystemConnector.DESCRIPTOR);
            if (iin != null && (iin instanceof IPackageInstallerSessionFileSystemConnector)) {
                return (IPackageInstallerSessionFileSystemConnector) iin;
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
                    return "writeData";
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
                data.enforceInterface(IPackageInstallerSessionFileSystemConnector.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPackageInstallerSessionFileSystemConnector.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            long _arg1 = data.readLong();
                            long _arg2 = data.readLong();
                            ParcelFileDescriptor _arg3 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            writeData(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.content.pm.IPackageInstallerSessionFileSystemConnector$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IPackageInstallerSessionFileSystemConnector {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPackageInstallerSessionFileSystemConnector.DESCRIPTOR;
            }

            @Override // android.content.p001pm.IPackageInstallerSessionFileSystemConnector
            public void writeData(String name, long offsetBytes, long lengthBytes, ParcelFileDescriptor fd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPackageInstallerSessionFileSystemConnector.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeLong(offsetBytes);
                    _data.writeLong(lengthBytes);
                    _data.writeTypedObject(fd, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
