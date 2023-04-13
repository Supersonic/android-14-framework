package android.hardware.light;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface ILights extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$light$ILights".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements ILights {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.hardware.light.ILights
        public HwLight[] getLights() throws RemoteException {
            return null;
        }

        @Override // android.hardware.light.ILights
        public void setLightState(int i, HwLightState hwLightState) throws RemoteException {
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    HwLight[] getLights() throws RemoteException;

    void setLightState(int i, HwLightState hwLightState) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ILights {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            markVintfStability();
            attachInterface(this, ILights.DESCRIPTOR);
        }

        public static ILights asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(ILights.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof ILights)) {
                return (ILights) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = ILights.DESCRIPTOR;
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
                        parcel.enforceNoDataAvail();
                        setLightState(parcel.readInt(), (HwLightState) parcel.readTypedObject(HwLightState.CREATOR));
                        parcel2.writeNoException();
                    } else if (i == 2) {
                        HwLight[] lights = getLights();
                        parcel2.writeNoException();
                        parcel2.writeTypedArray(lights, 1);
                    } else {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements ILights {
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

            @Override // android.hardware.light.ILights
            public void setLightState(int i, HwLightState hwLightState) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ILights.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeTypedObject(hwLightState, 0);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0)) {
                        throw new RemoteException("Method setLightState is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.light.ILights
            public HwLight[] getLights() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ILights.DESCRIPTOR);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getLights is unimplemented.");
                    }
                    obtain2.readException();
                    return (HwLight[]) obtain2.createTypedArray(HwLight.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
