package android.hardware.cas;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ICas extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$cas$ICas".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 1;

    void closeSession(byte[] bArr) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    byte[] openSession(int i, int i2) throws RemoteException;

    byte[] openSessionDefault() throws RemoteException;

    void processEcm(byte[] bArr, byte[] bArr2) throws RemoteException;

    void processEmm(byte[] bArr) throws RemoteException;

    void provision(String str) throws RemoteException;

    void refreshEntitlements(int i, byte[] bArr) throws RemoteException;

    void release() throws RemoteException;

    void sendEvent(int i, int i2, byte[] bArr) throws RemoteException;

    void sendSessionEvent(byte[] bArr, int i, int i2, byte[] bArr2) throws RemoteException;

    void setPrivateData(byte[] bArr) throws RemoteException;

    void setSessionPrivateData(byte[] bArr, byte[] bArr2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICas {
        @Override // android.hardware.cas.ICas
        public void closeSession(byte[] sessionId) throws RemoteException {
        }

        @Override // android.hardware.cas.ICas
        public byte[] openSessionDefault() throws RemoteException {
            return null;
        }

        @Override // android.hardware.cas.ICas
        public byte[] openSession(int intent, int mode) throws RemoteException {
            return null;
        }

        @Override // android.hardware.cas.ICas
        public void processEcm(byte[] sessionId, byte[] ecm) throws RemoteException {
        }

        @Override // android.hardware.cas.ICas
        public void processEmm(byte[] emm) throws RemoteException {
        }

        @Override // android.hardware.cas.ICas
        public void provision(String provisionString) throws RemoteException {
        }

        @Override // android.hardware.cas.ICas
        public void refreshEntitlements(int refreshType, byte[] refreshData) throws RemoteException {
        }

        @Override // android.hardware.cas.ICas
        public void release() throws RemoteException {
        }

        @Override // android.hardware.cas.ICas
        public void sendEvent(int event, int arg, byte[] eventData) throws RemoteException {
        }

        @Override // android.hardware.cas.ICas
        public void sendSessionEvent(byte[] sessionId, int event, int arg, byte[] eventData) throws RemoteException {
        }

        @Override // android.hardware.cas.ICas
        public void setPrivateData(byte[] pvtData) throws RemoteException {
        }

        @Override // android.hardware.cas.ICas
        public void setSessionPrivateData(byte[] sessionId, byte[] pvtData) throws RemoteException {
        }

        @Override // android.hardware.cas.ICas
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.cas.ICas
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICas {
        static final int TRANSACTION_closeSession = 1;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_openSession = 3;
        static final int TRANSACTION_openSessionDefault = 2;
        static final int TRANSACTION_processEcm = 4;
        static final int TRANSACTION_processEmm = 5;
        static final int TRANSACTION_provision = 6;
        static final int TRANSACTION_refreshEntitlements = 7;
        static final int TRANSACTION_release = 8;
        static final int TRANSACTION_sendEvent = 9;
        static final int TRANSACTION_sendSessionEvent = 10;
        static final int TRANSACTION_setPrivateData = 11;
        static final int TRANSACTION_setSessionPrivateData = 12;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ICas asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICas)) {
                return (ICas) iin;
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
                            byte[] _arg0 = data.createByteArray();
                            data.enforceNoDataAvail();
                            closeSession(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            byte[] _result = openSessionDefault();
                            reply.writeNoException();
                            reply.writeByteArray(_result);
                            break;
                        case 3:
                            int _arg02 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            byte[] _result2 = openSession(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeByteArray(_result2);
                            break;
                        case 4:
                            byte[] _arg03 = data.createByteArray();
                            byte[] _arg12 = data.createByteArray();
                            data.enforceNoDataAvail();
                            processEcm(_arg03, _arg12);
                            reply.writeNoException();
                            break;
                        case 5:
                            byte[] _arg04 = data.createByteArray();
                            data.enforceNoDataAvail();
                            processEmm(_arg04);
                            reply.writeNoException();
                            break;
                        case 6:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            provision(_arg05);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg06 = data.readInt();
                            byte[] _arg13 = data.createByteArray();
                            data.enforceNoDataAvail();
                            refreshEntitlements(_arg06, _arg13);
                            reply.writeNoException();
                            break;
                        case 8:
                            release();
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg07 = data.readInt();
                            int _arg14 = data.readInt();
                            byte[] _arg2 = data.createByteArray();
                            data.enforceNoDataAvail();
                            sendEvent(_arg07, _arg14, _arg2);
                            reply.writeNoException();
                            break;
                        case 10:
                            byte[] _arg08 = data.createByteArray();
                            int _arg15 = data.readInt();
                            int _arg22 = data.readInt();
                            byte[] _arg3 = data.createByteArray();
                            data.enforceNoDataAvail();
                            sendSessionEvent(_arg08, _arg15, _arg22, _arg3);
                            reply.writeNoException();
                            break;
                        case 11:
                            byte[] _arg09 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setPrivateData(_arg09);
                            reply.writeNoException();
                            break;
                        case 12:
                            byte[] _arg010 = data.createByteArray();
                            byte[] _arg16 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setSessionPrivateData(_arg010, _arg16);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ICas {
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

            @Override // android.hardware.cas.ICas
            public void closeSession(byte[] sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(sessionId);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method closeSession is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public byte[] openSessionDefault() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method openSessionDefault is unimplemented.");
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public byte[] openSession(int intent, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(intent);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method openSession is unimplemented.");
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public void processEcm(byte[] sessionId, byte[] ecm) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(sessionId);
                    _data.writeByteArray(ecm);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method processEcm is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public void processEmm(byte[] emm) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(emm);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method processEmm is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public void provision(String provisionString) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(provisionString);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method provision is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public void refreshEntitlements(int refreshType, byte[] refreshData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(refreshType);
                    _data.writeByteArray(refreshData);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method refreshEntitlements is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public void release() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method release is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public void sendEvent(int event, int arg, byte[] eventData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(event);
                    _data.writeInt(arg);
                    _data.writeByteArray(eventData);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method sendEvent is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public void sendSessionEvent(byte[] sessionId, int event, int arg, byte[] eventData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(sessionId);
                    _data.writeInt(event);
                    _data.writeInt(arg);
                    _data.writeByteArray(eventData);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method sendSessionEvent is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public void setPrivateData(byte[] pvtData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(pvtData);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setPrivateData is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
            public void setSessionPrivateData(byte[] sessionId, byte[] pvtData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(sessionId);
                    _data.writeByteArray(pvtData);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setSessionPrivateData is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICas
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

            @Override // android.hardware.cas.ICas
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
