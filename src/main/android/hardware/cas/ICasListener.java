package android.hardware.cas;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ICasListener extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$cas$ICasListener".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 1;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void onEvent(int i, int i2, byte[] bArr) throws RemoteException;

    void onSessionEvent(byte[] bArr, int i, int i2, byte[] bArr2) throws RemoteException;

    void onStatusUpdate(byte b, int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICasListener {
        @Override // android.hardware.cas.ICasListener
        public void onEvent(int event, int arg, byte[] data) throws RemoteException {
        }

        @Override // android.hardware.cas.ICasListener
        public void onSessionEvent(byte[] sessionId, int event, int arg, byte[] data) throws RemoteException {
        }

        @Override // android.hardware.cas.ICasListener
        public void onStatusUpdate(byte event, int number) throws RemoteException {
        }

        @Override // android.hardware.cas.ICasListener
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.cas.ICasListener
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICasListener {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_onEvent = 1;
        static final int TRANSACTION_onSessionEvent = 2;
        static final int TRANSACTION_onStatusUpdate = 3;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ICasListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICasListener)) {
                return (ICasListener) iin;
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
                            int _arg1 = data.readInt();
                            byte[] _arg2 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onEvent(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            byte[] _arg02 = data.createByteArray();
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            byte[] _arg3 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onSessionEvent(_arg02, _arg12, _arg22, _arg3);
                            reply.writeNoException();
                            break;
                        case 3:
                            byte _arg03 = data.readByte();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            onStatusUpdate(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ICasListener {
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

            @Override // android.hardware.cas.ICasListener
            public void onEvent(int event, int arg, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(event);
                    _data.writeInt(arg);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onEvent is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICasListener
            public void onSessionEvent(byte[] sessionId, int event, int arg, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(sessionId);
                    _data.writeInt(event);
                    _data.writeInt(arg);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onSessionEvent is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICasListener
            public void onStatusUpdate(byte event, int number) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByte(event);
                    _data.writeInt(number);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onStatusUpdate is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.ICasListener
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

            @Override // android.hardware.cas.ICasListener
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
