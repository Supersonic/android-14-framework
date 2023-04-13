package android.p008os;

import java.io.FileDescriptor;
/* renamed from: android.os.IVoldMountCallback */
/* loaded from: classes3.dex */
public interface IVoldMountCallback extends IInterface {
    public static final String DESCRIPTOR = "android.os.IVoldMountCallback";

    boolean onVolumeChecking(FileDescriptor fileDescriptor, String str, String str2) throws RemoteException;

    /* renamed from: android.os.IVoldMountCallback$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IVoldMountCallback {
        @Override // android.p008os.IVoldMountCallback
        public boolean onVolumeChecking(FileDescriptor fuseFd, String path, String internalPath) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IVoldMountCallback$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IVoldMountCallback {
        static final int TRANSACTION_onVolumeChecking = 1;

        public Stub() {
            attachInterface(this, IVoldMountCallback.DESCRIPTOR);
        }

        public static IVoldMountCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IVoldMountCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IVoldMountCallback)) {
                return (IVoldMountCallback) iin;
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
                    return "onVolumeChecking";
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
                data.enforceInterface(IVoldMountCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IVoldMountCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            FileDescriptor _arg0 = data.readRawFileDescriptor();
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = onVolumeChecking(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* renamed from: android.os.IVoldMountCallback$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IVoldMountCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IVoldMountCallback.DESCRIPTOR;
            }

            @Override // android.p008os.IVoldMountCallback
            public boolean onVolumeChecking(FileDescriptor fuseFd, String path, String internalPath) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVoldMountCallback.DESCRIPTOR);
                    _data.writeRawFileDescriptor(fuseFd);
                    _data.writeString(path);
                    _data.writeString(internalPath);
                    this.mRemote.transact(1, _data, _reply, 0);
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
            return 0;
        }
    }
}
