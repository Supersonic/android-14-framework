package android.hardware.weaver;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IWeaver extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$weaver$IWeaver".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements IWeaver {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.hardware.weaver.IWeaver
        public WeaverConfig getConfig() throws RemoteException {
            return null;
        }

        @Override // android.hardware.weaver.IWeaver
        public WeaverReadResponse read(int i, byte[] bArr) throws RemoteException {
            return null;
        }

        @Override // android.hardware.weaver.IWeaver
        public void write(int i, byte[] bArr, byte[] bArr2) throws RemoteException {
        }
    }

    WeaverConfig getConfig() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    WeaverReadResponse read(int i, byte[] bArr) throws RemoteException;

    void write(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IWeaver {
        public static String getDefaultTransactionName(int i) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        switch (i) {
                            case 16777214:
                                return "getInterfaceHash";
                            case 16777215:
                                return "getInterfaceVersion";
                            default:
                                return null;
                        }
                    }
                    return "write";
                }
                return "read";
            }
            return "getConfig";
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
            attachInterface(this, IWeaver.DESCRIPTOR);
        }

        public static IWeaver asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IWeaver.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IWeaver)) {
                return (IWeaver) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        public String getTransactionName(int i) {
            return getDefaultTransactionName(i);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IWeaver.DESCRIPTOR;
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
                        WeaverConfig config = getConfig();
                        parcel2.writeNoException();
                        parcel2.writeTypedObject(config, 1);
                    } else if (i == 2) {
                        int readInt = parcel.readInt();
                        byte[] createByteArray = parcel.createByteArray();
                        parcel.enforceNoDataAvail();
                        WeaverReadResponse read = read(readInt, createByteArray);
                        parcel2.writeNoException();
                        parcel2.writeTypedObject(read, 1);
                    } else if (i == 3) {
                        int readInt2 = parcel.readInt();
                        byte[] createByteArray2 = parcel.createByteArray();
                        byte[] createByteArray3 = parcel.createByteArray();
                        parcel.enforceNoDataAvail();
                        write(readInt2, createByteArray2, createByteArray3);
                        parcel2.writeNoException();
                    } else {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IWeaver {
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

            @Override // android.hardware.weaver.IWeaver
            public WeaverConfig getConfig() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IWeaver.DESCRIPTOR);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getConfig is unimplemented.");
                    }
                    obtain2.readException();
                    return (WeaverConfig) obtain2.readTypedObject(WeaverConfig.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.weaver.IWeaver
            public WeaverReadResponse read(int i, byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IWeaver.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeByteArray(bArr);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0)) {
                        throw new RemoteException("Method read is unimplemented.");
                    }
                    obtain2.readException();
                    return (WeaverReadResponse) obtain2.readTypedObject(WeaverReadResponse.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.weaver.IWeaver
            public void write(int i, byte[] bArr, byte[] bArr2) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IWeaver.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeByteArray(bArr);
                    obtain.writeByteArray(bArr2);
                    if (!this.mRemote.transact(3, obtain, obtain2, 0)) {
                        throw new RemoteException("Method write is unimplemented.");
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
