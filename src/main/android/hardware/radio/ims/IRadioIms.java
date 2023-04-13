package android.hardware.radio.ims;

import android.hardware.radio.ims.IRadioImsIndication;
import android.hardware.radio.ims.IRadioImsResponse;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioIms extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$ims$IRadioIms".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 1;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void sendAnbrQuery(int i, int i2, int i3, int i4) throws RemoteException;

    void setResponseFunctions(IRadioImsResponse iRadioImsResponse, IRadioImsIndication iRadioImsIndication) throws RemoteException;

    void setSrvccCallInfo(int i, SrvccCall[] srvccCallArr) throws RemoteException;

    void startImsTraffic(int i, int i2, int i3, int i4, int i5) throws RemoteException;

    void stopImsTraffic(int i, int i2) throws RemoteException;

    void triggerEpsFallback(int i, int i2) throws RemoteException;

    void updateImsCallStatus(int i, ImsCall[] imsCallArr) throws RemoteException;

    void updateImsRegistrationInfo(int i, ImsRegistration imsRegistration) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioIms {
        @Override // android.hardware.radio.ims.IRadioIms
        public void setSrvccCallInfo(int serial, SrvccCall[] srvccCalls) throws RemoteException {
        }

        @Override // android.hardware.radio.ims.IRadioIms
        public void updateImsRegistrationInfo(int serial, ImsRegistration imsRegistration) throws RemoteException {
        }

        @Override // android.hardware.radio.ims.IRadioIms
        public void startImsTraffic(int serial, int token, int imsTrafficType, int accessNetworkType, int trafficDirection) throws RemoteException {
        }

        @Override // android.hardware.radio.ims.IRadioIms
        public void stopImsTraffic(int serial, int token) throws RemoteException {
        }

        @Override // android.hardware.radio.ims.IRadioIms
        public void triggerEpsFallback(int serial, int reason) throws RemoteException {
        }

        @Override // android.hardware.radio.ims.IRadioIms
        public void setResponseFunctions(IRadioImsResponse radioImsResponse, IRadioImsIndication radioImsIndication) throws RemoteException {
        }

        @Override // android.hardware.radio.ims.IRadioIms
        public void sendAnbrQuery(int serial, int mediaType, int direction, int bitsPerSecond) throws RemoteException {
        }

        @Override // android.hardware.radio.ims.IRadioIms
        public void updateImsCallStatus(int serial, ImsCall[] imsCalls) throws RemoteException {
        }

        @Override // android.hardware.radio.ims.IRadioIms
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.ims.IRadioIms
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioIms {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_sendAnbrQuery = 7;
        static final int TRANSACTION_setResponseFunctions = 6;
        static final int TRANSACTION_setSrvccCallInfo = 1;
        static final int TRANSACTION_startImsTraffic = 3;
        static final int TRANSACTION_stopImsTraffic = 4;
        static final int TRANSACTION_triggerEpsFallback = 5;
        static final int TRANSACTION_updateImsCallStatus = 8;
        static final int TRANSACTION_updateImsRegistrationInfo = 2;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioIms asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioIms)) {
                return (IRadioIms) iin;
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
                            SrvccCall[] _arg1 = (SrvccCall[]) data.createTypedArray(SrvccCall.CREATOR);
                            data.enforceNoDataAvail();
                            setSrvccCallInfo(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            ImsRegistration _arg12 = (ImsRegistration) data.readTypedObject(ImsRegistration.CREATOR);
                            data.enforceNoDataAvail();
                            updateImsRegistrationInfo(_arg02, _arg12);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg13 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            startImsTraffic(_arg03, _arg13, _arg2, _arg3, _arg4);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            stopImsTraffic(_arg04, _arg14);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            triggerEpsFallback(_arg05, _arg15);
                            break;
                        case 6:
                            IRadioImsResponse _arg06 = IRadioImsResponse.Stub.asInterface(data.readStrongBinder());
                            IRadioImsIndication _arg16 = IRadioImsIndication.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setResponseFunctions(_arg06, _arg16);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            int _arg17 = data.readInt();
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            sendAnbrQuery(_arg07, _arg17, _arg22, _arg32);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            ImsCall[] _arg18 = (ImsCall[]) data.createTypedArray(ImsCall.CREATOR);
                            data.enforceNoDataAvail();
                            updateImsCallStatus(_arg08, _arg18);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioIms {
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

            @Override // android.hardware.radio.ims.IRadioIms
            public void setSrvccCallInfo(int serial, SrvccCall[] srvccCalls) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedArray(srvccCalls, 0);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSrvccCallInfo is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ims.IRadioIms
            public void updateImsRegistrationInfo(int serial, ImsRegistration imsRegistration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(imsRegistration, 0);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method updateImsRegistrationInfo is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ims.IRadioIms
            public void startImsTraffic(int serial, int token, int imsTrafficType, int accessNetworkType, int trafficDirection) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(token);
                    _data.writeInt(imsTrafficType);
                    _data.writeInt(accessNetworkType);
                    _data.writeInt(trafficDirection);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startImsTraffic is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ims.IRadioIms
            public void stopImsTraffic(int serial, int token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(token);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stopImsTraffic is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ims.IRadioIms
            public void triggerEpsFallback(int serial, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(reason);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method triggerEpsFallback is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ims.IRadioIms
            public void setResponseFunctions(IRadioImsResponse radioImsResponse, IRadioImsIndication radioImsIndication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(radioImsResponse);
                    _data.writeStrongInterface(radioImsIndication);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setResponseFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ims.IRadioIms
            public void sendAnbrQuery(int serial, int mediaType, int direction, int bitsPerSecond) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(mediaType);
                    _data.writeInt(direction);
                    _data.writeInt(bitsPerSecond);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendAnbrQuery is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ims.IRadioIms
            public void updateImsCallStatus(int serial, ImsCall[] imsCalls) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedArray(imsCalls, 0);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method updateImsCallStatus is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.ims.IRadioIms
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

            @Override // android.hardware.radio.ims.IRadioIms
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
