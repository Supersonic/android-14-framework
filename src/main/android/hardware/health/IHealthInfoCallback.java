package android.hardware.health;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IHealthInfoCallback extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$health$IHealthInfoCallback".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements IHealthInfoCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void healthInfoChanged(HealthInfo healthInfo) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IHealthInfoCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            markVintfStability();
            attachInterface(this, IHealthInfoCallback.DESCRIPTOR);
        }

        public static IHealthInfoCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IHealthInfoCallback.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IHealthInfoCallback)) {
                return (IHealthInfoCallback) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IHealthInfoCallback.DESCRIPTOR;
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
                        healthInfoChanged((HealthInfo) parcel.readTypedObject(HealthInfo.CREATOR));
                        return true;
                    }
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IHealthInfoCallback {
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
        }
    }
}
