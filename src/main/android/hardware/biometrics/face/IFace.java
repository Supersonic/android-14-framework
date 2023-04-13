package android.hardware.biometrics.face;

import android.hardware.biometrics.face.ISession;
import android.hardware.biometrics.face.ISessionCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IFace extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$biometrics$face$IFace".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements IFace {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.hardware.biometrics.face.IFace
        public ISession createSession(int i, int i2, ISessionCallback iSessionCallback) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.face.IFace
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.biometrics.face.IFace
        public SensorProps[] getSensorProps() throws RemoteException {
            return null;
        }
    }

    ISession createSession(int i, int i2, ISessionCallback iSessionCallback) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    SensorProps[] getSensorProps() throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IFace {
        public static String getDefaultTransactionName(int i) {
            if (i != 1) {
                if (i != 2) {
                    switch (i) {
                        case 16777214:
                            return "getInterfaceHash";
                        case 16777215:
                            return "getInterfaceVersion";
                        default:
                            return null;
                    }
                }
                return "createSession";
            }
            return "getSensorProps";
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
            attachInterface(this, IFace.DESCRIPTOR);
        }

        public static IFace asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IFace.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IFace)) {
                return (IFace) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        public String getTransactionName(int i) {
            return getDefaultTransactionName(i);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IFace.DESCRIPTOR;
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
                        SensorProps[] sensorProps = getSensorProps();
                        parcel2.writeNoException();
                        parcel2.writeTypedArray(sensorProps, 1);
                    } else if (i == 2) {
                        int readInt = parcel.readInt();
                        int readInt2 = parcel.readInt();
                        ISessionCallback asInterface = ISessionCallback.Stub.asInterface(parcel.readStrongBinder());
                        parcel.enforceNoDataAvail();
                        ISession createSession = createSession(readInt, readInt2, asInterface);
                        parcel2.writeNoException();
                        parcel2.writeStrongInterface(createSession);
                    } else {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IFace {
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

            @Override // android.hardware.biometrics.face.IFace
            public SensorProps[] getSensorProps() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IFace.DESCRIPTOR);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getSensorProps is unimplemented.");
                    }
                    obtain2.readException();
                    return (SensorProps[]) obtain2.createTypedArray(SensorProps.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.IFace
            public ISession createSession(int i, int i2, ISessionCallback iSessionCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IFace.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeStrongInterface(iSessionCallback);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0)) {
                        throw new RemoteException("Method createSession is unimplemented.");
                    }
                    obtain2.readException();
                    return ISession.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.IFace
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel obtain = Parcel.obtain(asBinder());
                    Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken(IFace.DESCRIPTOR);
                        this.mRemote.transact(16777215, obtain, obtain2, 0);
                        obtain2.readException();
                        this.mCachedVersion = obtain2.readInt();
                    } finally {
                        obtain2.recycle();
                        obtain.recycle();
                    }
                }
                return this.mCachedVersion;
            }
        }
    }
}
