package android.hardware.p002tv.hdmi.cec;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* renamed from: android.hardware.tv.hdmi.cec.IHdmiCecCallback */
/* loaded from: classes.dex */
public interface IHdmiCecCallback extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$tv$hdmi$cec$IHdmiCecCallback".replace('$', '.');

    /* renamed from: android.hardware.tv.hdmi.cec.IHdmiCecCallback$Default */
    /* loaded from: classes.dex */
    public static class Default implements IHdmiCecCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void onCecMessage(CecMessage cecMessage) throws RemoteException;

    /* renamed from: android.hardware.tv.hdmi.cec.IHdmiCecCallback$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IHdmiCecCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            markVintfStability();
            attachInterface(this, IHdmiCecCallback.DESCRIPTOR);
        }

        public static IHdmiCecCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IHdmiCecCallback.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IHdmiCecCallback)) {
                return (IHdmiCecCallback) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IHdmiCecCallback.DESCRIPTOR;
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
                        onCecMessage((CecMessage) parcel.readTypedObject(CecMessage.CREATOR));
                        return true;
                    }
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* renamed from: android.hardware.tv.hdmi.cec.IHdmiCecCallback$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IHdmiCecCallback {
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
