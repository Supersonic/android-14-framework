package android.hardware.power;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IPowerHintSession extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$power$IPowerHintSession".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements IPowerHintSession {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    void close() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void pause() throws RemoteException;

    void reportActualWorkDuration(WorkDuration[] workDurationArr) throws RemoteException;

    void resume() throws RemoteException;

    void sendHint(int i) throws RemoteException;

    void setThreads(int[] iArr) throws RemoteException;

    void updateTargetWorkDuration(long j) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IPowerHintSession {
        public static String getDefaultTransactionName(int i) {
            switch (i) {
                case 1:
                    return "updateTargetWorkDuration";
                case 2:
                    return "reportActualWorkDuration";
                case 3:
                    return "pause";
                case 4:
                    return "resume";
                case 5:
                    return "close";
                case 6:
                    return "sendHint";
                case 7:
                    return "setThreads";
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
            attachInterface(this, IPowerHintSession.DESCRIPTOR);
        }

        public String getTransactionName(int i) {
            return getDefaultTransactionName(i);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IPowerHintSession.DESCRIPTOR;
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
                            long readLong = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            updateTargetWorkDuration(readLong);
                            break;
                        case 2:
                            parcel.enforceNoDataAvail();
                            reportActualWorkDuration((WorkDuration[]) parcel.createTypedArray(WorkDuration.CREATOR));
                            break;
                        case 3:
                            pause();
                            break;
                        case 4:
                            resume();
                            break;
                        case 5:
                            close();
                            break;
                        case 6:
                            int readInt = parcel.readInt();
                            parcel.enforceNoDataAvail();
                            sendHint(readInt);
                            break;
                        case 7:
                            int[] createIntArray = parcel.createIntArray();
                            parcel.enforceNoDataAvail();
                            setThreads(createIntArray);
                            parcel2.writeNoException();
                            break;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IPowerHintSession {
            public IBinder mRemote;

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }
        }
    }
}
