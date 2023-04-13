package android.telephony.satellite.stub;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.satellite.stub.ISatelliteCapabilitiesConsumer;
import android.telephony.satellite.stub.ISatelliteListener;
import com.android.internal.telephony.IBooleanConsumer;
import com.android.internal.telephony.IIntegerConsumer;
/* loaded from: classes3.dex */
public interface ISatellite extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.satellite.stub.ISatellite";

    void deprovisionSatelliteService(String str, IIntegerConsumer iIntegerConsumer) throws RemoteException;

    void pollPendingSatelliteDatagrams(IIntegerConsumer iIntegerConsumer) throws RemoteException;

    void provisionSatelliteService(String str, IIntegerConsumer iIntegerConsumer) throws RemoteException;

    void requestIsSatelliteCommunicationAllowedForCurrentLocation(IIntegerConsumer iIntegerConsumer, IBooleanConsumer iBooleanConsumer) throws RemoteException;

    void requestIsSatelliteEnabled(IIntegerConsumer iIntegerConsumer, IBooleanConsumer iBooleanConsumer) throws RemoteException;

    void requestIsSatelliteProvisioned(IIntegerConsumer iIntegerConsumer, IBooleanConsumer iBooleanConsumer) throws RemoteException;

    void requestIsSatelliteSupported(IIntegerConsumer iIntegerConsumer, IBooleanConsumer iBooleanConsumer) throws RemoteException;

    void requestMaxCharactersPerMOTextMessage(IIntegerConsumer iIntegerConsumer, IIntegerConsumer iIntegerConsumer2) throws RemoteException;

    void requestSatelliteCapabilities(IIntegerConsumer iIntegerConsumer, ISatelliteCapabilitiesConsumer iSatelliteCapabilitiesConsumer) throws RemoteException;

    void requestSatelliteEnabled(boolean z, IIntegerConsumer iIntegerConsumer) throws RemoteException;

    void requestSatelliteListeningEnabled(boolean z, boolean z2, int i, IIntegerConsumer iIntegerConsumer) throws RemoteException;

    void requestSatelliteModemState(IIntegerConsumer iIntegerConsumer, IIntegerConsumer iIntegerConsumer2) throws RemoteException;

    void requestTimeForNextSatelliteVisibility(IIntegerConsumer iIntegerConsumer, IIntegerConsumer iIntegerConsumer2) throws RemoteException;

    void sendSatelliteDatagram(SatelliteDatagram satelliteDatagram, boolean z, boolean z2, IIntegerConsumer iIntegerConsumer) throws RemoteException;

    void setSatelliteListener(ISatelliteListener iSatelliteListener) throws RemoteException;

    void startSendingSatellitePointingInfo(IIntegerConsumer iIntegerConsumer) throws RemoteException;

    void stopSendingSatellitePointingInfo(IIntegerConsumer iIntegerConsumer) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISatellite {
        @Override // android.telephony.satellite.stub.ISatellite
        public void setSatelliteListener(ISatelliteListener listener) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void requestSatelliteListeningEnabled(boolean enable, boolean isDemoMode, int timeout, IIntegerConsumer errorCallback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void requestSatelliteEnabled(boolean enabled, IIntegerConsumer errorCallback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void requestIsSatelliteEnabled(IIntegerConsumer errorCallback, IBooleanConsumer callback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void requestIsSatelliteSupported(IIntegerConsumer errorCallback, IBooleanConsumer callback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void requestSatelliteCapabilities(IIntegerConsumer errorCallback, ISatelliteCapabilitiesConsumer callback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void startSendingSatellitePointingInfo(IIntegerConsumer errorCallback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void stopSendingSatellitePointingInfo(IIntegerConsumer errorCallback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void requestMaxCharactersPerMOTextMessage(IIntegerConsumer errorCallback, IIntegerConsumer callback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void provisionSatelliteService(String token, IIntegerConsumer errorCallback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void deprovisionSatelliteService(String token, IIntegerConsumer errorCallback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void requestIsSatelliteProvisioned(IIntegerConsumer errorCallback, IBooleanConsumer callback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void pollPendingSatelliteDatagrams(IIntegerConsumer errorCallback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void sendSatelliteDatagram(SatelliteDatagram datagram, boolean isDemoMode, boolean isEmergency, IIntegerConsumer errorCallback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void requestSatelliteModemState(IIntegerConsumer errorCallback, IIntegerConsumer callback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void requestIsSatelliteCommunicationAllowedForCurrentLocation(IIntegerConsumer errorCallback, IBooleanConsumer callback) throws RemoteException {
        }

        @Override // android.telephony.satellite.stub.ISatellite
        public void requestTimeForNextSatelliteVisibility(IIntegerConsumer errorCallback, IIntegerConsumer callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISatellite {
        static final int TRANSACTION_deprovisionSatelliteService = 11;
        static final int TRANSACTION_pollPendingSatelliteDatagrams = 13;
        static final int TRANSACTION_provisionSatelliteService = 10;

        /* renamed from: TRANSACTION_requestIsSatelliteCommunicationAllowedForCurrentLocation */
        static final int f460xd8afe6fa = 16;
        static final int TRANSACTION_requestIsSatelliteEnabled = 4;
        static final int TRANSACTION_requestIsSatelliteProvisioned = 12;
        static final int TRANSACTION_requestIsSatelliteSupported = 5;
        static final int TRANSACTION_requestMaxCharactersPerMOTextMessage = 9;
        static final int TRANSACTION_requestSatelliteCapabilities = 6;
        static final int TRANSACTION_requestSatelliteEnabled = 3;
        static final int TRANSACTION_requestSatelliteListeningEnabled = 2;
        static final int TRANSACTION_requestSatelliteModemState = 15;
        static final int TRANSACTION_requestTimeForNextSatelliteVisibility = 17;
        static final int TRANSACTION_sendSatelliteDatagram = 14;
        static final int TRANSACTION_setSatelliteListener = 1;
        static final int TRANSACTION_startSendingSatellitePointingInfo = 7;
        static final int TRANSACTION_stopSendingSatellitePointingInfo = 8;

        public Stub() {
            attachInterface(this, ISatellite.DESCRIPTOR);
        }

        public static ISatellite asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISatellite.DESCRIPTOR);
            if (iin != null && (iin instanceof ISatellite)) {
                return (ISatellite) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "setSatelliteListener";
                case 2:
                    return "requestSatelliteListeningEnabled";
                case 3:
                    return "requestSatelliteEnabled";
                case 4:
                    return "requestIsSatelliteEnabled";
                case 5:
                    return "requestIsSatelliteSupported";
                case 6:
                    return "requestSatelliteCapabilities";
                case 7:
                    return "startSendingSatellitePointingInfo";
                case 8:
                    return "stopSendingSatellitePointingInfo";
                case 9:
                    return "requestMaxCharactersPerMOTextMessage";
                case 10:
                    return "provisionSatelliteService";
                case 11:
                    return "deprovisionSatelliteService";
                case 12:
                    return "requestIsSatelliteProvisioned";
                case 13:
                    return "pollPendingSatelliteDatagrams";
                case 14:
                    return "sendSatelliteDatagram";
                case 15:
                    return "requestSatelliteModemState";
                case 16:
                    return "requestIsSatelliteCommunicationAllowedForCurrentLocation";
                case 17:
                    return "requestTimeForNextSatelliteVisibility";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(ISatellite.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISatellite.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ISatelliteListener _arg0 = ISatelliteListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setSatelliteListener(_arg0);
                            break;
                        case 2:
                            boolean _arg02 = data.readBoolean();
                            boolean _arg1 = data.readBoolean();
                            int _arg2 = data.readInt();
                            IIntegerConsumer _arg3 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestSatelliteListeningEnabled(_arg02, _arg1, _arg2, _arg3);
                            break;
                        case 3:
                            boolean _arg03 = data.readBoolean();
                            IIntegerConsumer _arg12 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestSatelliteEnabled(_arg03, _arg12);
                            break;
                        case 4:
                            IIntegerConsumer _arg04 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            IBooleanConsumer _arg13 = IBooleanConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestIsSatelliteEnabled(_arg04, _arg13);
                            break;
                        case 5:
                            IIntegerConsumer _arg05 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            IBooleanConsumer _arg14 = IBooleanConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestIsSatelliteSupported(_arg05, _arg14);
                            break;
                        case 6:
                            IIntegerConsumer _arg06 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            ISatelliteCapabilitiesConsumer _arg15 = ISatelliteCapabilitiesConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestSatelliteCapabilities(_arg06, _arg15);
                            break;
                        case 7:
                            IIntegerConsumer _arg07 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startSendingSatellitePointingInfo(_arg07);
                            break;
                        case 8:
                            IIntegerConsumer _arg08 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            stopSendingSatellitePointingInfo(_arg08);
                            break;
                        case 9:
                            IIntegerConsumer _arg09 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            IIntegerConsumer _arg16 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestMaxCharactersPerMOTextMessage(_arg09, _arg16);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            IIntegerConsumer _arg17 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            provisionSatelliteService(_arg010, _arg17);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            IIntegerConsumer _arg18 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            deprovisionSatelliteService(_arg011, _arg18);
                            break;
                        case 12:
                            IIntegerConsumer _arg012 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            IBooleanConsumer _arg19 = IBooleanConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestIsSatelliteProvisioned(_arg012, _arg19);
                            break;
                        case 13:
                            IIntegerConsumer _arg013 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            pollPendingSatelliteDatagrams(_arg013);
                            break;
                        case 14:
                            SatelliteDatagram _arg014 = (SatelliteDatagram) data.readTypedObject(SatelliteDatagram.CREATOR);
                            boolean _arg110 = data.readBoolean();
                            boolean _arg22 = data.readBoolean();
                            IIntegerConsumer _arg32 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            sendSatelliteDatagram(_arg014, _arg110, _arg22, _arg32);
                            break;
                        case 15:
                            IIntegerConsumer _arg015 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            IIntegerConsumer _arg111 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestSatelliteModemState(_arg015, _arg111);
                            break;
                        case 16:
                            IIntegerConsumer _arg016 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            IBooleanConsumer _arg112 = IBooleanConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestIsSatelliteCommunicationAllowedForCurrentLocation(_arg016, _arg112);
                            break;
                        case 17:
                            IIntegerConsumer _arg017 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            IIntegerConsumer _arg113 = IIntegerConsumer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestTimeForNextSatelliteVisibility(_arg017, _arg113);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISatellite {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISatellite.DESCRIPTOR;
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void setSatelliteListener(ISatelliteListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void requestSatelliteListeningEnabled(boolean enable, boolean isDemoMode, int timeout, IIntegerConsumer errorCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    _data.writeBoolean(isDemoMode);
                    _data.writeInt(timeout);
                    _data.writeStrongInterface(errorCallback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void requestSatelliteEnabled(boolean enabled, IIntegerConsumer errorCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeStrongInterface(errorCallback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void requestIsSatelliteEnabled(IIntegerConsumer errorCallback, IBooleanConsumer callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void requestIsSatelliteSupported(IIntegerConsumer errorCallback, IBooleanConsumer callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void requestSatelliteCapabilities(IIntegerConsumer errorCallback, ISatelliteCapabilitiesConsumer callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void startSendingSatellitePointingInfo(IIntegerConsumer errorCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void stopSendingSatellitePointingInfo(IIntegerConsumer errorCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void requestMaxCharactersPerMOTextMessage(IIntegerConsumer errorCallback, IIntegerConsumer callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void provisionSatelliteService(String token, IIntegerConsumer errorCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeString(token);
                    _data.writeStrongInterface(errorCallback);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void deprovisionSatelliteService(String token, IIntegerConsumer errorCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeString(token);
                    _data.writeStrongInterface(errorCallback);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void requestIsSatelliteProvisioned(IIntegerConsumer errorCallback, IBooleanConsumer callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void pollPendingSatelliteDatagrams(IIntegerConsumer errorCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void sendSatelliteDatagram(SatelliteDatagram datagram, boolean isDemoMode, boolean isEmergency, IIntegerConsumer errorCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeTypedObject(datagram, 0);
                    _data.writeBoolean(isDemoMode);
                    _data.writeBoolean(isEmergency);
                    _data.writeStrongInterface(errorCallback);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void requestSatelliteModemState(IIntegerConsumer errorCallback, IIntegerConsumer callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void requestIsSatelliteCommunicationAllowedForCurrentLocation(IIntegerConsumer errorCallback, IBooleanConsumer callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.satellite.stub.ISatellite
            public void requestTimeForNextSatelliteVisibility(IIntegerConsumer errorCallback, IIntegerConsumer callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISatellite.DESCRIPTOR);
                    _data.writeStrongInterface(errorCallback);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 16;
        }
    }
}
