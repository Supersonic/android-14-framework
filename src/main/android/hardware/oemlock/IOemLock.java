package android.hardware.oemlock;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IOemLock extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$oemlock$IOemLock".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements IOemLock {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.hardware.oemlock.IOemLock
        public String getName() throws RemoteException {
            return null;
        }

        @Override // android.hardware.oemlock.IOemLock
        public boolean isOemUnlockAllowedByCarrier() throws RemoteException {
            return false;
        }

        @Override // android.hardware.oemlock.IOemLock
        public boolean isOemUnlockAllowedByDevice() throws RemoteException {
            return false;
        }

        @Override // android.hardware.oemlock.IOemLock
        public int setOemUnlockAllowedByCarrier(boolean z, byte[] bArr) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.oemlock.IOemLock
        public void setOemUnlockAllowedByDevice(boolean z) throws RemoteException {
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    String getName() throws RemoteException;

    boolean isOemUnlockAllowedByCarrier() throws RemoteException;

    boolean isOemUnlockAllowedByDevice() throws RemoteException;

    int setOemUnlockAllowedByCarrier(boolean z, byte[] bArr) throws RemoteException;

    void setOemUnlockAllowedByDevice(boolean z) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IOemLock {
        public static String getDefaultTransactionName(int i) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i != 5) {
                                switch (i) {
                                    case 16777214:
                                        return "getInterfaceHash";
                                    case 16777215:
                                        return "getInterfaceVersion";
                                    default:
                                        return null;
                                }
                            }
                            return "setOemUnlockAllowedByDevice";
                        }
                        return "setOemUnlockAllowedByCarrier";
                    }
                    return "isOemUnlockAllowedByDevice";
                }
                return "isOemUnlockAllowedByCarrier";
            }
            return "getName";
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public int getMaxTransactionId() {
            return 16777214;
        }

        public Stub() {
            markVintfStability();
            attachInterface(this, IOemLock.DESCRIPTOR);
        }

        public static IOemLock asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IOemLock.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IOemLock)) {
                return (IOemLock) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        public String getTransactionName(int i) {
            return getDefaultTransactionName(i);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IOemLock.DESCRIPTOR;
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(str);
            }
            switch (i) {
                case 16777214:
                    parcel2.writeNoException();
                    parcel2.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    parcel2.writeNoException();
                    parcel2.writeInt(getInterfaceVersion());
                    return true;
                case 1598968902:
                    parcel2.writeString(str);
                    return true;
                default:
                    if (i == 1) {
                        String name = getName();
                        parcel2.writeNoException();
                        parcel2.writeString(name);
                    } else if (i == 2) {
                        boolean isOemUnlockAllowedByCarrier = isOemUnlockAllowedByCarrier();
                        parcel2.writeNoException();
                        parcel2.writeBoolean(isOemUnlockAllowedByCarrier);
                    } else if (i == 3) {
                        boolean isOemUnlockAllowedByDevice = isOemUnlockAllowedByDevice();
                        parcel2.writeNoException();
                        parcel2.writeBoolean(isOemUnlockAllowedByDevice);
                    } else if (i == 4) {
                        boolean readBoolean = parcel.readBoolean();
                        byte[] createByteArray = parcel.createByteArray();
                        parcel.enforceNoDataAvail();
                        int oemUnlockAllowedByCarrier = setOemUnlockAllowedByCarrier(readBoolean, createByteArray);
                        parcel2.writeNoException();
                        parcel2.writeInt(oemUnlockAllowedByCarrier);
                    } else if (i == 5) {
                        boolean readBoolean2 = parcel.readBoolean();
                        parcel.enforceNoDataAvail();
                        setOemUnlockAllowedByDevice(readBoolean2);
                        parcel2.writeNoException();
                    } else {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IOemLock {
            public IBinder mRemote;
            public int mCachedVersion = -1;
            public String mCachedHash = "-1";

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.hardware.oemlock.IOemLock
            public String getName() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IOemLock.DESCRIPTOR);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getName is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.oemlock.IOemLock
            public boolean isOemUnlockAllowedByCarrier() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IOemLock.DESCRIPTOR);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0)) {
                        throw new RemoteException("Method isOemUnlockAllowedByCarrier is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.oemlock.IOemLock
            public boolean isOemUnlockAllowedByDevice() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IOemLock.DESCRIPTOR);
                    if (!this.mRemote.transact(3, obtain, obtain2, 0)) {
                        throw new RemoteException("Method isOemUnlockAllowedByDevice is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.oemlock.IOemLock
            public int setOemUnlockAllowedByCarrier(boolean z, byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IOemLock.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    obtain.writeByteArray(bArr);
                    if (!this.mRemote.transact(4, obtain, obtain2, 0)) {
                        throw new RemoteException("Method setOemUnlockAllowedByCarrier is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.oemlock.IOemLock
            public void setOemUnlockAllowedByDevice(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IOemLock.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(5, obtain, obtain2, 0)) {
                        throw new RemoteException("Method setOemUnlockAllowedByDevice is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
