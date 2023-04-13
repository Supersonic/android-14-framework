package android.hardware.radio.satellite;

import android.hardware.radio.satellite.IRadioSatelliteIndication;
import android.hardware.radio.satellite.IRadioSatelliteResponse;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioSatellite extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$satellite$IRadioSatellite".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 1;

    void addAllowedSatelliteContacts(int i, String[] strArr) throws RemoteException;

    void getCapabilities(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getMaxCharactersPerTextMessage(int i) throws RemoteException;

    void getPendingMessages(int i) throws RemoteException;

    void getPowerState(int i) throws RemoteException;

    void getSatelliteMode(int i) throws RemoteException;

    void getTimeForNextSatelliteVisibility(int i) throws RemoteException;

    void provisionService(int i, String str, String str2, String str3, int[] iArr) throws RemoteException;

    void removeAllowedSatelliteContacts(int i, String[] strArr) throws RemoteException;

    void responseAcknowledgement() throws RemoteException;

    void sendMessages(int i, String[] strArr, String str, double d, double d2) throws RemoteException;

    void setIndicationFilter(int i, int i2) throws RemoteException;

    void setPower(int i, boolean z) throws RemoteException;

    void setResponseFunctions(IRadioSatelliteResponse iRadioSatelliteResponse, IRadioSatelliteIndication iRadioSatelliteIndication) throws RemoteException;

    void startSendingSatellitePointingInfo(int i) throws RemoteException;

    void stopSendingSatellitePointingInfo(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioSatellite {
        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void addAllowedSatelliteContacts(int serial, String[] contacts) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void getCapabilities(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void getMaxCharactersPerTextMessage(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void getPendingMessages(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void getPowerState(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void getSatelliteMode(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void getTimeForNextSatelliteVisibility(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void provisionService(int serial, String imei, String msisdn, String imsi, int[] features) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void removeAllowedSatelliteContacts(int serial, String[] contacts) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void responseAcknowledgement() throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void sendMessages(int serial, String[] messages, String destination, double latitude, double longitude) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void setIndicationFilter(int serial, int filterBitmask) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void setPower(int serial, boolean on) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void setResponseFunctions(IRadioSatelliteResponse satelliteResponse, IRadioSatelliteIndication satelliteIndication) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void startSendingSatellitePointingInfo(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public void stopSendingSatellitePointingInfo(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.satellite.IRadioSatellite
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioSatellite {
        static final int TRANSACTION_addAllowedSatelliteContacts = 1;
        static final int TRANSACTION_getCapabilities = 2;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getMaxCharactersPerTextMessage = 3;
        static final int TRANSACTION_getPendingMessages = 4;
        static final int TRANSACTION_getPowerState = 5;
        static final int TRANSACTION_getSatelliteMode = 6;
        static final int TRANSACTION_getTimeForNextSatelliteVisibility = 7;
        static final int TRANSACTION_provisionService = 8;
        static final int TRANSACTION_removeAllowedSatelliteContacts = 9;
        static final int TRANSACTION_responseAcknowledgement = 10;
        static final int TRANSACTION_sendMessages = 11;
        static final int TRANSACTION_setIndicationFilter = 12;
        static final int TRANSACTION_setPower = 13;
        static final int TRANSACTION_setResponseFunctions = 14;
        static final int TRANSACTION_startSendingSatellitePointingInfo = 15;
        static final int TRANSACTION_stopSendingSatellitePointingInfo = 16;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioSatellite asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioSatellite)) {
                return (IRadioSatellite) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case 16777214:
                    reply.writeNoException();
                    reply.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    reply.writeNoException();
                    reply.writeInt(getInterfaceVersion());
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            String[] _arg1 = data.createStringArray();
                            data.enforceNoDataAvail();
                            addAllowedSatelliteContacts(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            getCapabilities(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            getMaxCharactersPerTextMessage(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            getPendingMessages(_arg04);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            getPowerState(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            getSatelliteMode(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            getTimeForNextSatelliteVisibility(_arg07);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            String _arg12 = data.readString();
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            int[] _arg4 = data.createIntArray();
                            data.enforceNoDataAvail();
                            provisionService(_arg08, _arg12, _arg2, _arg3, _arg4);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            String[] _arg13 = data.createStringArray();
                            data.enforceNoDataAvail();
                            removeAllowedSatelliteContacts(_arg09, _arg13);
                            break;
                        case 10:
                            responseAcknowledgement();
                            break;
                        case 11:
                            int _arg010 = data.readInt();
                            String[] _arg14 = data.createStringArray();
                            String _arg22 = data.readString();
                            double _arg32 = data.readDouble();
                            double _arg42 = data.readDouble();
                            data.enforceNoDataAvail();
                            sendMessages(_arg010, _arg14, _arg22, _arg32, _arg42);
                            break;
                        case 12:
                            int _arg011 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            setIndicationFilter(_arg011, _arg15);
                            break;
                        case 13:
                            int _arg012 = data.readInt();
                            boolean _arg16 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPower(_arg012, _arg16);
                            break;
                        case 14:
                            IRadioSatelliteResponse _arg013 = IRadioSatelliteResponse.Stub.asInterface(data.readStrongBinder());
                            IRadioSatelliteIndication _arg17 = IRadioSatelliteIndication.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setResponseFunctions(_arg013, _arg17);
                            break;
                        case 15:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            startSendingSatellitePointingInfo(_arg014);
                            break;
                        case 16:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            stopSendingSatellitePointingInfo(_arg015);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioSatellite {
            private IBinder mRemote;
            private int mCachedVersion = -1;
            private String mCachedHash = "-1";

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void addAllowedSatelliteContacts(int serial, String[] contacts) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeStringArray(contacts);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method addAllowedSatelliteContacts is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void getCapabilities(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCapabilities is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void getMaxCharactersPerTextMessage(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getMaxCharactersPerTextMessage is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void getPendingMessages(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getPendingMessages is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void getPowerState(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getPowerState is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void getSatelliteMode(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSatelliteMode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void getTimeForNextSatelliteVisibility(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getTimeForNextSatelliteVisibility is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void provisionService(int serial, String imei, String msisdn, String imsi, int[] features) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(imei);
                    _data.writeString(msisdn);
                    _data.writeString(imsi);
                    _data.writeIntArray(features);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method provisionService is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void removeAllowedSatelliteContacts(int serial, String[] contacts) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeStringArray(contacts);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method removeAllowedSatelliteContacts is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void responseAcknowledgement() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method responseAcknowledgement is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void sendMessages(int serial, String[] messages, String destination, double latitude, double longitude) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeStringArray(messages);
                    _data.writeString(destination);
                    _data.writeDouble(latitude);
                    _data.writeDouble(longitude);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendMessages is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void setIndicationFilter(int serial, int filterBitmask) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(filterBitmask);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setIndicationFilter is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void setPower(int serial, boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(on);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setPower is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void setResponseFunctions(IRadioSatelliteResponse satelliteResponse, IRadioSatelliteIndication satelliteIndication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(satelliteResponse);
                    _data.writeStrongInterface(satelliteIndication);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setResponseFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void startSendingSatellitePointingInfo(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startSendingSatellitePointingInfo is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public void stopSendingSatellitePointingInfo(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stopSendingSatellitePointingInfo is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(DESCRIPTOR);
                        this.mRemote.transact(16777215, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.hardware.radio.satellite.IRadioSatellite
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(16777214, data, reply, 0);
                    reply.readException();
                    this.mCachedHash = reply.readString();
                    reply.recycle();
                    data.recycle();
                }
                return this.mCachedHash;
            }
        }
    }
}
