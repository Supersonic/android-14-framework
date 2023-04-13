package android.hardware.p002tv.hdmi.connection;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* renamed from: android.hardware.tv.hdmi.connection.IHdmiConnectionCallback */
/* loaded from: classes.dex */
public interface IHdmiConnectionCallback extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$tv$hdmi$connection$IHdmiConnectionCallback".replace('$', '.');

    /* renamed from: android.hardware.tv.hdmi.connection.IHdmiConnectionCallback$Default */
    /* loaded from: classes.dex */
    public static class Default implements IHdmiConnectionCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void onHotplugEvent(boolean z, int i) throws RemoteException;

    /* renamed from: android.hardware.tv.hdmi.connection.IHdmiConnectionCallback$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IHdmiConnectionCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            markVintfStability();
            attachInterface(this, IHdmiConnectionCallback.DESCRIPTOR);
        }

        public static IHdmiConnectionCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IHdmiConnectionCallback.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IHdmiConnectionCallback)) {
                return (IHdmiConnectionCallback) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IHdmiConnectionCallback.DESCRIPTOR;
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
                        boolean readBoolean = parcel.readBoolean();
                        int readInt = parcel.readInt();
                        parcel.enforceNoDataAvail();
                        onHotplugEvent(readBoolean, readInt);
                        return true;
                    }
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* renamed from: android.hardware.tv.hdmi.connection.IHdmiConnectionCallback$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IHdmiConnectionCallback {
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
