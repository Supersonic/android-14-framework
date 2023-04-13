package android.p005os;

import java.io.FileDescriptor;
/* renamed from: android.os.IVoldMountCallback */
/* loaded from: classes.dex */
public interface IVoldMountCallback extends IInterface {
    public static final String DESCRIPTOR = "android.os.IVoldMountCallback";

    /* renamed from: android.os.IVoldMountCallback$Default */
    /* loaded from: classes.dex */
    public static class Default implements IVoldMountCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.p005os.IVoldMountCallback
        public boolean onVolumeChecking(FileDescriptor fileDescriptor, String str, String str2) throws RemoteException {
            return false;
        }
    }

    boolean onVolumeChecking(FileDescriptor fileDescriptor, String str, String str2) throws RemoteException;

    /* renamed from: android.os.IVoldMountCallback$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IVoldMountCallback {
        public static final int TRANSACTION_onVolumeChecking = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IVoldMountCallback.DESCRIPTOR);
        }

        public static IVoldMountCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IVoldMountCallback.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IVoldMountCallback)) {
                return (IVoldMountCallback) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IVoldMountCallback.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IVoldMountCallback.DESCRIPTOR);
                return true;
            } else if (i == 1) {
                FileDescriptor readRawFileDescriptor = parcel.readRawFileDescriptor();
                String readString = parcel.readString();
                String readString2 = parcel.readString();
                parcel.enforceNoDataAvail();
                boolean onVolumeChecking = onVolumeChecking(readRawFileDescriptor, readString, readString2);
                parcel2.writeNoException();
                parcel2.writeBoolean(onVolumeChecking);
                return true;
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* renamed from: android.os.IVoldMountCallback$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IVoldMountCallback {
            public IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IVoldMountCallback.DESCRIPTOR;
            }

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.p005os.IVoldMountCallback
            public boolean onVolumeChecking(FileDescriptor fileDescriptor, String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IVoldMountCallback.DESCRIPTOR);
                    obtain.writeRawFileDescriptor(fileDescriptor);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
