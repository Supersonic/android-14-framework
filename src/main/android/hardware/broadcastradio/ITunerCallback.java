package android.hardware.broadcastradio;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface ITunerCallback extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$broadcastradio$ITunerCallback".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements ITunerCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void onAntennaStateChange(boolean z) throws RemoteException;

    void onConfigFlagUpdated(int i, boolean z) throws RemoteException;

    void onCurrentProgramInfoChanged(ProgramInfo programInfo) throws RemoteException;

    void onParametersUpdated(VendorKeyValue[] vendorKeyValueArr) throws RemoteException;

    void onProgramListUpdated(ProgramListChunk programListChunk) throws RemoteException;

    void onTuneFailed(int i, ProgramSelector programSelector) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ITunerCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            markVintfStability();
            attachInterface(this, ITunerCallback.DESCRIPTOR);
        }

        public static ITunerCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(ITunerCallback.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof ITunerCallback)) {
                return (ITunerCallback) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = ITunerCallback.DESCRIPTOR;
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
                            parcel.enforceNoDataAvail();
                            onTuneFailed(parcel.readInt(), (ProgramSelector) parcel.readTypedObject(ProgramSelector.CREATOR));
                            break;
                        case 2:
                            parcel.enforceNoDataAvail();
                            onCurrentProgramInfoChanged((ProgramInfo) parcel.readTypedObject(ProgramInfo.CREATOR));
                            break;
                        case 3:
                            parcel.enforceNoDataAvail();
                            onProgramListUpdated((ProgramListChunk) parcel.readTypedObject(ProgramListChunk.CREATOR));
                            break;
                        case 4:
                            boolean readBoolean = parcel.readBoolean();
                            parcel.enforceNoDataAvail();
                            onAntennaStateChange(readBoolean);
                            break;
                        case 5:
                            int readInt = parcel.readInt();
                            boolean readBoolean2 = parcel.readBoolean();
                            parcel.enforceNoDataAvail();
                            onConfigFlagUpdated(readInt, readBoolean2);
                            break;
                        case 6:
                            parcel.enforceNoDataAvail();
                            onParametersUpdated((VendorKeyValue[]) parcel.createTypedArray(VendorKeyValue.CREATOR));
                            break;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements ITunerCallback {
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
