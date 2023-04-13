package android.hardware.power;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IPower extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$power$IPower".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements IPower {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    IPowerHintSession createHintSession(int i, int i2, int[] iArr, long j) throws RemoteException;

    long getHintSessionPreferredRate() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    boolean isBoostSupported(int i) throws RemoteException;

    boolean isModeSupported(int i) throws RemoteException;

    void setBoost(int i, int i2) throws RemoteException;

    void setMode(int i, boolean z) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IPower {
        public static String getDefaultTransactionName(int i) {
            switch (i) {
                case 1:
                    return "setMode";
                case 2:
                    return "isModeSupported";
                case 3:
                    return "setBoost";
                case 4:
                    return "isBoostSupported";
                case 5:
                    return "createHintSession";
                case 6:
                    return "getHintSessionPreferredRate";
                default:
                    switch (i) {
                        case 16777214:
                            return "getInterfaceHash";
                        case 16777215:
                            return "getInterfaceVersion";
                        default:
                            return null;
                    }
            }
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
            attachInterface(this, IPower.DESCRIPTOR);
        }

        public String getTransactionName(int i) {
            return getDefaultTransactionName(i);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IPower.DESCRIPTOR;
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
                    switch (i) {
                        case 1:
                            int readInt = parcel.readInt();
                            boolean readBoolean = parcel.readBoolean();
                            parcel.enforceNoDataAvail();
                            setMode(readInt, readBoolean);
                            break;
                        case 2:
                            int readInt2 = parcel.readInt();
                            parcel.enforceNoDataAvail();
                            boolean isModeSupported = isModeSupported(readInt2);
                            parcel2.writeNoException();
                            parcel2.writeBoolean(isModeSupported);
                            break;
                        case 3:
                            int readInt3 = parcel.readInt();
                            int readInt4 = parcel.readInt();
                            parcel.enforceNoDataAvail();
                            setBoost(readInt3, readInt4);
                            break;
                        case 4:
                            int readInt5 = parcel.readInt();
                            parcel.enforceNoDataAvail();
                            boolean isBoostSupported = isBoostSupported(readInt5);
                            parcel2.writeNoException();
                            parcel2.writeBoolean(isBoostSupported);
                            break;
                        case 5:
                            int readInt6 = parcel.readInt();
                            int readInt7 = parcel.readInt();
                            int[] createIntArray = parcel.createIntArray();
                            long readLong = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            IPowerHintSession createHintSession = createHintSession(readInt6, readInt7, createIntArray, readLong);
                            parcel2.writeNoException();
                            parcel2.writeStrongInterface(createHintSession);
                            break;
                        case 6:
                            long hintSessionPreferredRate = getHintSessionPreferredRate();
                            parcel2.writeNoException();
                            parcel2.writeLong(hintSessionPreferredRate);
                            break;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IPower {
            public IBinder mRemote;

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }
        }
    }
}
