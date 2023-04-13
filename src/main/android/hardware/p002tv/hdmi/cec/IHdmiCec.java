package android.hardware.p002tv.hdmi.cec;

import android.hardware.p002tv.hdmi.cec.IHdmiCecCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* renamed from: android.hardware.tv.hdmi.cec.IHdmiCec */
/* loaded from: classes.dex */
public interface IHdmiCec extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$tv$hdmi$cec$IHdmiCec".replace('$', '.');

    /* renamed from: android.hardware.tv.hdmi.cec.IHdmiCec$Default */
    /* loaded from: classes.dex */
    public static class Default implements IHdmiCec {
        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public byte addLogicalAddress(byte b) throws RemoteException {
            return (byte) 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public void clearLogicalAddress() throws RemoteException {
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public void enableAudioReturnChannel(int i, boolean z) throws RemoteException {
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public void enableCec(boolean z) throws RemoteException {
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public void enableSystemCecControl(boolean z) throws RemoteException {
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public void enableWakeupByOtp(boolean z) throws RemoteException {
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public int getCecVersion() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public int getPhysicalAddress() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public int getVendorId() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public byte sendMessage(CecMessage cecMessage) throws RemoteException {
            return (byte) 0;
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public void setCallback(IHdmiCecCallback iHdmiCecCallback) throws RemoteException {
        }

        @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
        public void setLanguage(String str) throws RemoteException {
        }
    }

    byte addLogicalAddress(byte b) throws RemoteException;

    void clearLogicalAddress() throws RemoteException;

    void enableAudioReturnChannel(int i, boolean z) throws RemoteException;

    void enableCec(boolean z) throws RemoteException;

    void enableSystemCecControl(boolean z) throws RemoteException;

    void enableWakeupByOtp(boolean z) throws RemoteException;

    int getCecVersion() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    int getPhysicalAddress() throws RemoteException;

    int getVendorId() throws RemoteException;

    byte sendMessage(CecMessage cecMessage) throws RemoteException;

    void setCallback(IHdmiCecCallback iHdmiCecCallback) throws RemoteException;

    void setLanguage(String str) throws RemoteException;

    /* renamed from: android.hardware.tv.hdmi.cec.IHdmiCec$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IHdmiCec {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            markVintfStability();
            attachInterface(this, IHdmiCec.DESCRIPTOR);
        }

        public static IHdmiCec asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IHdmiCec.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IHdmiCec)) {
                return (IHdmiCec) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IHdmiCec.DESCRIPTOR;
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
                            byte readByte = parcel.readByte();
                            parcel.enforceNoDataAvail();
                            byte addLogicalAddress = addLogicalAddress(readByte);
                            parcel2.writeNoException();
                            parcel2.writeByte(addLogicalAddress);
                            break;
                        case 2:
                            clearLogicalAddress();
                            parcel2.writeNoException();
                            break;
                        case 3:
                            int readInt = parcel.readInt();
                            boolean readBoolean = parcel.readBoolean();
                            parcel.enforceNoDataAvail();
                            enableAudioReturnChannel(readInt, readBoolean);
                            parcel2.writeNoException();
                            break;
                        case 4:
                            int cecVersion = getCecVersion();
                            parcel2.writeNoException();
                            parcel2.writeInt(cecVersion);
                            break;
                        case 5:
                            int physicalAddress = getPhysicalAddress();
                            parcel2.writeNoException();
                            parcel2.writeInt(physicalAddress);
                            break;
                        case 6:
                            int vendorId = getVendorId();
                            parcel2.writeNoException();
                            parcel2.writeInt(vendorId);
                            break;
                        case 7:
                            parcel.enforceNoDataAvail();
                            byte sendMessage = sendMessage((CecMessage) parcel.readTypedObject(CecMessage.CREATOR));
                            parcel2.writeNoException();
                            parcel2.writeByte(sendMessage);
                            break;
                        case 8:
                            IHdmiCecCallback asInterface = IHdmiCecCallback.Stub.asInterface(parcel.readStrongBinder());
                            parcel.enforceNoDataAvail();
                            setCallback(asInterface);
                            parcel2.writeNoException();
                            break;
                        case 9:
                            String readString = parcel.readString();
                            parcel.enforceNoDataAvail();
                            setLanguage(readString);
                            parcel2.writeNoException();
                            break;
                        case 10:
                            boolean readBoolean2 = parcel.readBoolean();
                            parcel.enforceNoDataAvail();
                            enableWakeupByOtp(readBoolean2);
                            parcel2.writeNoException();
                            break;
                        case 11:
                            boolean readBoolean3 = parcel.readBoolean();
                            parcel.enforceNoDataAvail();
                            enableCec(readBoolean3);
                            parcel2.writeNoException();
                            break;
                        case 12:
                            boolean readBoolean4 = parcel.readBoolean();
                            parcel.enforceNoDataAvail();
                            enableSystemCecControl(readBoolean4);
                            parcel2.writeNoException();
                            break;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* renamed from: android.hardware.tv.hdmi.cec.IHdmiCec$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IHdmiCec {
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

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public byte addLogicalAddress(byte b) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    obtain.writeByte(b);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0)) {
                        throw new RemoteException("Method addLogicalAddress is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readByte();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public void clearLogicalAddress() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0)) {
                        throw new RemoteException("Method clearLogicalAddress is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public void enableAudioReturnChannel(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(3, obtain, obtain2, 0)) {
                        throw new RemoteException("Method enableAudioReturnChannel is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public int getCecVersion() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    if (!this.mRemote.transact(4, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getCecVersion is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public int getPhysicalAddress() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    if (!this.mRemote.transact(5, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getPhysicalAddress is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public int getVendorId() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    if (!this.mRemote.transact(6, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getVendorId is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public byte sendMessage(CecMessage cecMessage) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    obtain.writeTypedObject(cecMessage, 0);
                    if (!this.mRemote.transact(7, obtain, obtain2, 0)) {
                        throw new RemoteException("Method sendMessage is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readByte();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public void setCallback(IHdmiCecCallback iHdmiCecCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    obtain.writeStrongInterface(iHdmiCecCallback);
                    if (!this.mRemote.transact(8, obtain, obtain2, 0)) {
                        throw new RemoteException("Method setCallback is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public void setLanguage(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(9, obtain, obtain2, 0)) {
                        throw new RemoteException("Method setLanguage is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public void enableWakeupByOtp(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(10, obtain, obtain2, 0)) {
                        throw new RemoteException("Method enableWakeupByOtp is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public void enableCec(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(11, obtain, obtain2, 0)) {
                        throw new RemoteException("Method enableCec is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.p002tv.hdmi.cec.IHdmiCec
            public void enableSystemCecControl(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHdmiCec.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(12, obtain, obtain2, 0)) {
                        throw new RemoteException("Method enableSystemCecControl is unimplemented.");
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
