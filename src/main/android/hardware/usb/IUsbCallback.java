package android.hardware.usb;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IUsbCallback extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$usb$IUsbCallback".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements IUsbCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void notifyContaminantEnabledStatus(String str, boolean z, int i, long j) throws RemoteException;

    void notifyEnableUsbDataStatus(String str, boolean z, int i, long j) throws RemoteException;

    void notifyEnableUsbDataWhileDockedStatus(String str, int i, long j) throws RemoteException;

    void notifyLimitPowerTransferStatus(String str, boolean z, int i, long j) throws RemoteException;

    void notifyPortStatusChange(PortStatus[] portStatusArr, int i) throws RemoteException;

    void notifyQueryPortStatus(String str, int i, long j) throws RemoteException;

    void notifyResetUsbPortStatus(String str, int i, long j) throws RemoteException;

    void notifyRoleSwitchStatus(String str, PortRole portRole, int i, long j) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IUsbCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            markVintfStability();
            attachInterface(this, IUsbCallback.DESCRIPTOR);
        }

        public static IUsbCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IUsbCallback.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IUsbCallback)) {
                return (IUsbCallback) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IUsbCallback.DESCRIPTOR;
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
                            parcel.enforceNoDataAvail();
                            notifyPortStatusChange((PortStatus[]) parcel.createTypedArray(PortStatus.CREATOR), readInt);
                            break;
                        case 2:
                            int readInt2 = parcel.readInt();
                            long readLong = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            notifyRoleSwitchStatus(parcel.readString(), (PortRole) parcel.readTypedObject(PortRole.CREATOR), readInt2, readLong);
                            break;
                        case 3:
                            String readString = parcel.readString();
                            boolean readBoolean = parcel.readBoolean();
                            int readInt3 = parcel.readInt();
                            long readLong2 = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            notifyEnableUsbDataStatus(readString, readBoolean, readInt3, readLong2);
                            break;
                        case 4:
                            String readString2 = parcel.readString();
                            int readInt4 = parcel.readInt();
                            long readLong3 = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            notifyEnableUsbDataWhileDockedStatus(readString2, readInt4, readLong3);
                            break;
                        case 5:
                            String readString3 = parcel.readString();
                            boolean readBoolean2 = parcel.readBoolean();
                            int readInt5 = parcel.readInt();
                            long readLong4 = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            notifyContaminantEnabledStatus(readString3, readBoolean2, readInt5, readLong4);
                            break;
                        case 6:
                            String readString4 = parcel.readString();
                            int readInt6 = parcel.readInt();
                            long readLong5 = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            notifyQueryPortStatus(readString4, readInt6, readLong5);
                            break;
                        case 7:
                            String readString5 = parcel.readString();
                            boolean readBoolean3 = parcel.readBoolean();
                            int readInt7 = parcel.readInt();
                            long readLong6 = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            notifyLimitPowerTransferStatus(readString5, readBoolean3, readInt7, readLong6);
                            break;
                        case 8:
                            String readString6 = parcel.readString();
                            int readInt8 = parcel.readInt();
                            long readLong7 = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            notifyResetUsbPortStatus(readString6, readInt8, readLong7);
                            break;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IUsbCallback {
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
