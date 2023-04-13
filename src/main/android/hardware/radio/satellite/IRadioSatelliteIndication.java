package android.hardware.radio.satellite;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioSatelliteIndication extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$satellite$IRadioSatelliteIndication".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 1;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void onMessagesTransferComplete(int i, boolean z) throws RemoteException;

    void onNewMessages(int i, String[] strArr) throws RemoteException;

    void onPendingMessageCount(int i, int i2) throws RemoteException;

    void onProvisionStateChanged(int i, boolean z, int[] iArr) throws RemoteException;

    void onSatelliteModeChanged(int i, int i2) throws RemoteException;

    void onSatellitePointingInfoChanged(int i, PointingInfo pointingInfo) throws RemoteException;

    void onSatelliteRadioTechnologyChanged(int i, int i2) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioSatelliteIndication {
        @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
        public void onMessagesTransferComplete(int type, boolean complete) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
        public void onNewMessages(int type, String[] messages) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
        public void onPendingMessageCount(int type, int count) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
        public void onProvisionStateChanged(int type, boolean provisioned, int[] features) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
        public void onSatelliteModeChanged(int type, int mode) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
        public void onSatellitePointingInfoChanged(int type, PointingInfo pointingInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
        public void onSatelliteRadioTechnologyChanged(int type, int technology) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioSatelliteIndication {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_onMessagesTransferComplete = 1;
        static final int TRANSACTION_onNewMessages = 2;
        static final int TRANSACTION_onPendingMessageCount = 3;
        static final int TRANSACTION_onProvisionStateChanged = 4;
        static final int TRANSACTION_onSatelliteModeChanged = 5;
        static final int TRANSACTION_onSatellitePointingInfoChanged = 6;
        static final int TRANSACTION_onSatelliteRadioTechnologyChanged = 7;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioSatelliteIndication asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioSatelliteIndication)) {
                return (IRadioSatelliteIndication) iin;
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
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onMessagesTransferComplete(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String[] _arg12 = data.createStringArray();
                            data.enforceNoDataAvail();
                            onNewMessages(_arg02, _arg12);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            onPendingMessageCount(_arg03, _arg13);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            boolean _arg14 = data.readBoolean();
                            int[] _arg2 = data.createIntArray();
                            data.enforceNoDataAvail();
                            onProvisionStateChanged(_arg04, _arg14, _arg2);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            onSatelliteModeChanged(_arg05, _arg15);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            PointingInfo _arg16 = (PointingInfo) data.readTypedObject(PointingInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onSatellitePointingInfoChanged(_arg06, _arg16);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            onSatelliteRadioTechnologyChanged(_arg07, _arg17);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioSatelliteIndication {
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

            @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
            public void onMessagesTransferComplete(int type, boolean complete) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeBoolean(complete);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method onMessagesTransferComplete is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
            public void onNewMessages(int type, String[] messages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeStringArray(messages);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method onNewMessages is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
            public void onPendingMessageCount(int type, int count) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(count);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method onPendingMessageCount is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
            public void onProvisionStateChanged(int type, boolean provisioned, int[] features) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeBoolean(provisioned);
                    _data.writeIntArray(features);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method onProvisionStateChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
            public void onSatelliteModeChanged(int type, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method onSatelliteModeChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
            public void onSatellitePointingInfoChanged(int type, PointingInfo pointingInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedObject(pointingInfo, 0);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method onSatellitePointingInfoChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
            public void onSatelliteRadioTechnologyChanged(int type, int technology) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(technology);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method onSatelliteRadioTechnologyChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
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

            @Override // android.hardware.radio.satellite.IRadioSatelliteIndication
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
