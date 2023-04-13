package android.net.p003ip;

import android.net.DhcpResultsParcelable;
import android.net.Layer2PacketParcelable;
import android.net.LinkProperties;
import android.net.networkstack.aidl.p004ip.ReachabilityLossInfoParcelable;
import android.net.p003ip.IIpClient;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.util.List;
/* renamed from: android.net.ip.IIpClientCallbacks */
/* loaded from: classes.dex */
public interface IIpClientCallbacks extends IInterface {
    public static final String DESCRIPTOR = "android$net$ip$IIpClientCallbacks".replace('$', '.');
    public static final String HASH = "a7ed197af8532361170ac44595771304b7f04034";
    public static final int VERSION = 16;

    /* renamed from: android.net.ip.IIpClientCallbacks$Default */
    /* loaded from: classes.dex */
    public static class Default implements IIpClientCallbacks {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void installPacketFilter(byte[] bArr) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onIpClientCreated(IIpClient iIpClient) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onLinkPropertiesChange(LinkProperties linkProperties) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onNewDhcpResults(DhcpResultsParcelable dhcpResultsParcelable) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onPostDhcpAction() throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onPreDhcpAction() throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onPreconnectionStart(List<Layer2PacketParcelable> list) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onProvisioningFailure(LinkProperties linkProperties) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onProvisioningSuccess(LinkProperties linkProperties) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onQuit() throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onReachabilityFailure(ReachabilityLossInfoParcelable reachabilityLossInfoParcelable) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void onReachabilityLost(String str) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void setFallbackMulticastFilter(boolean z) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void setNeighborDiscoveryOffload(boolean z) throws RemoteException {
        }

        @Override // android.net.p003ip.IIpClientCallbacks
        public void startReadPacketFilter() throws RemoteException {
        }
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void installPacketFilter(byte[] bArr) throws RemoteException;

    void onIpClientCreated(IIpClient iIpClient) throws RemoteException;

    void onLinkPropertiesChange(LinkProperties linkProperties) throws RemoteException;

    void onNewDhcpResults(DhcpResultsParcelable dhcpResultsParcelable) throws RemoteException;

    void onPostDhcpAction() throws RemoteException;

    void onPreDhcpAction() throws RemoteException;

    void onPreconnectionStart(List<Layer2PacketParcelable> list) throws RemoteException;

    void onProvisioningFailure(LinkProperties linkProperties) throws RemoteException;

    void onProvisioningSuccess(LinkProperties linkProperties) throws RemoteException;

    void onQuit() throws RemoteException;

    void onReachabilityFailure(ReachabilityLossInfoParcelable reachabilityLossInfoParcelable) throws RemoteException;

    void onReachabilityLost(String str) throws RemoteException;

    void setFallbackMulticastFilter(boolean z) throws RemoteException;

    void setNeighborDiscoveryOffload(boolean z) throws RemoteException;

    void startReadPacketFilter() throws RemoteException;

    /* renamed from: android.net.ip.IIpClientCallbacks$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IIpClientCallbacks {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_installPacketFilter = 10;
        static final int TRANSACTION_onIpClientCreated = 1;
        static final int TRANSACTION_onLinkPropertiesChange = 7;
        static final int TRANSACTION_onNewDhcpResults = 4;
        static final int TRANSACTION_onPostDhcpAction = 3;
        static final int TRANSACTION_onPreDhcpAction = 2;
        static final int TRANSACTION_onPreconnectionStart = 14;
        static final int TRANSACTION_onProvisioningFailure = 6;
        static final int TRANSACTION_onProvisioningSuccess = 5;
        static final int TRANSACTION_onQuit = 9;
        static final int TRANSACTION_onReachabilityFailure = 15;
        static final int TRANSACTION_onReachabilityLost = 8;
        static final int TRANSACTION_setFallbackMulticastFilter = 12;
        static final int TRANSACTION_setNeighborDiscoveryOffload = 13;
        static final int TRANSACTION_startReadPacketFilter = 11;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IIpClientCallbacks.DESCRIPTOR);
        }

        public static IIpClientCallbacks asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IIpClientCallbacks.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IIpClientCallbacks)) {
                return (IIpClientCallbacks) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IIpClientCallbacks.DESCRIPTOR;
            if (i >= 1 && i <= TRANSACTION_getInterfaceVersion) {
                parcel.enforceInterface(str);
            }
            switch (i) {
                case TRANSACTION_getInterfaceHash /* 16777214 */:
                    parcel2.writeNoException();
                    parcel2.writeString(getInterfaceHash());
                    return true;
                case TRANSACTION_getInterfaceVersion /* 16777215 */:
                    parcel2.writeNoException();
                    parcel2.writeInt(getInterfaceVersion());
                    return true;
                case 1598968902:
                    parcel2.writeString(str);
                    return true;
                default:
                    switch (i) {
                        case 1:
                            onIpClientCreated(IIpClient.Stub.asInterface(parcel.readStrongBinder()));
                            break;
                        case 2:
                            onPreDhcpAction();
                            break;
                        case 3:
                            onPostDhcpAction();
                            break;
                        case 4:
                            onNewDhcpResults((DhcpResultsParcelable) parcel.readTypedObject(DhcpResultsParcelable.CREATOR));
                            break;
                        case 5:
                            onProvisioningSuccess((LinkProperties) parcel.readTypedObject(LinkProperties.CREATOR));
                            break;
                        case 6:
                            onProvisioningFailure((LinkProperties) parcel.readTypedObject(LinkProperties.CREATOR));
                            break;
                        case 7:
                            onLinkPropertiesChange((LinkProperties) parcel.readTypedObject(LinkProperties.CREATOR));
                            break;
                        case 8:
                            onReachabilityLost(parcel.readString());
                            break;
                        case 9:
                            onQuit();
                            break;
                        case 10:
                            installPacketFilter(parcel.createByteArray());
                            break;
                        case 11:
                            startReadPacketFilter();
                            break;
                        case 12:
                            setFallbackMulticastFilter(parcel.readBoolean());
                            break;
                        case 13:
                            setNeighborDiscoveryOffload(parcel.readBoolean());
                            break;
                        case 14:
                            onPreconnectionStart(parcel.createTypedArrayList(Layer2PacketParcelable.CREATOR));
                            break;
                        case 15:
                            onReachabilityFailure((ReachabilityLossInfoParcelable) parcel.readTypedObject(ReachabilityLossInfoParcelable.CREATOR));
                            break;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* renamed from: android.net.ip.IIpClientCallbacks$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IIpClientCallbacks {
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

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onIpClientCreated(IIpClient iIpClient) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    obtain.writeStrongInterface(iIpClient);
                    if (this.mRemote.transact(1, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onIpClientCreated is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onPreDhcpAction() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    if (this.mRemote.transact(2, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onPreDhcpAction is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onPostDhcpAction() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    if (this.mRemote.transact(3, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onPostDhcpAction is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onNewDhcpResults(DhcpResultsParcelable dhcpResultsParcelable) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    obtain.writeTypedObject(dhcpResultsParcelable, 0);
                    if (this.mRemote.transact(4, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onNewDhcpResults is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onProvisioningSuccess(LinkProperties linkProperties) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    obtain.writeTypedObject(linkProperties, 0);
                    if (this.mRemote.transact(5, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onProvisioningSuccess is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onProvisioningFailure(LinkProperties linkProperties) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    obtain.writeTypedObject(linkProperties, 0);
                    if (this.mRemote.transact(6, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onProvisioningFailure is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onLinkPropertiesChange(LinkProperties linkProperties) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    obtain.writeTypedObject(linkProperties, 0);
                    if (this.mRemote.transact(7, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onLinkPropertiesChange is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onReachabilityLost(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    obtain.writeString(str);
                    if (this.mRemote.transact(8, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onReachabilityLost is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onQuit() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    if (this.mRemote.transact(9, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onQuit is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void installPacketFilter(byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    obtain.writeByteArray(bArr);
                    if (this.mRemote.transact(10, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method installPacketFilter is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void startReadPacketFilter() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    if (this.mRemote.transact(11, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method startReadPacketFilter is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void setFallbackMulticastFilter(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    if (this.mRemote.transact(12, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method setFallbackMulticastFilter is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void setNeighborDiscoveryOffload(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    obtain.writeBoolean(z);
                    if (this.mRemote.transact(13, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method setNeighborDiscoveryOffload is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onPreconnectionStart(List<Layer2PacketParcelable> list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    _Parcel.writeTypedList(obtain, list, 0);
                    if (this.mRemote.transact(14, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onPreconnectionStart is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public void onReachabilityFailure(ReachabilityLossInfoParcelable reachabilityLossInfoParcelable) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    obtain.writeTypedObject(reachabilityLossInfoParcelable, 0);
                    if (this.mRemote.transact(15, obtain, null, 1)) {
                        return;
                    }
                    throw new RemoteException("Method onReachabilityFailure is unimplemented.");
                } finally {
                    obtain.recycle();
                }
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    try {
                        obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                        this.mRemote.transact(Stub.TRANSACTION_getInterfaceVersion, obtain, obtain2, 0);
                        obtain2.readException();
                        this.mCachedVersion = obtain2.readInt();
                    } finally {
                        obtain2.recycle();
                        obtain.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.net.p003ip.IIpClientCallbacks
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInterfaceToken(IIpClientCallbacks.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getInterfaceHash, obtain, obtain2, 0);
                    obtain2.readException();
                    this.mCachedHash = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                }
                return this.mCachedHash;
            }
        }
    }

    /* renamed from: android.net.ip.IIpClientCallbacks$_Parcel */
    /* loaded from: classes.dex */
    public static class _Parcel {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void writeTypedList(Parcel parcel, List<T> list, int i) {
            if (list == null) {
                parcel.writeInt(-1);
                return;
            }
            int size = list.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                parcel.writeTypedObject(list.get(i2), i);
            }
        }
    }
}
