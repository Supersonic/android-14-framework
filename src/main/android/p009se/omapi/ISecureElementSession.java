package android.p009se.omapi;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p009se.omapi.ISecureElementChannel;
import android.p009se.omapi.ISecureElementListener;
/* renamed from: android.se.omapi.ISecureElementSession */
/* loaded from: classes3.dex */
public interface ISecureElementSession extends IInterface {
    public static final String HASH = "894069bcfe4f35ceb2088278ddf87c83adee8014";
    public static final int VERSION = 1;

    void close() throws RemoteException;

    void closeChannels() throws RemoteException;

    byte[] getAtr() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    boolean isClosed() throws RemoteException;

    ISecureElementChannel openBasicChannel(byte[] bArr, byte b, ISecureElementListener iSecureElementListener) throws RemoteException;

    ISecureElementChannel openLogicalChannel(byte[] bArr, byte b, ISecureElementListener iSecureElementListener) throws RemoteException;

    /* renamed from: android.se.omapi.ISecureElementSession$Default */
    /* loaded from: classes3.dex */
    public static class Default implements ISecureElementSession {
        @Override // android.p009se.omapi.ISecureElementSession
        public byte[] getAtr() throws RemoteException {
            return null;
        }

        @Override // android.p009se.omapi.ISecureElementSession
        public void close() throws RemoteException {
        }

        @Override // android.p009se.omapi.ISecureElementSession
        public void closeChannels() throws RemoteException {
        }

        @Override // android.p009se.omapi.ISecureElementSession
        public boolean isClosed() throws RemoteException {
            return false;
        }

        @Override // android.p009se.omapi.ISecureElementSession
        public ISecureElementChannel openBasicChannel(byte[] aid, byte p2, ISecureElementListener listener) throws RemoteException {
            return null;
        }

        @Override // android.p009se.omapi.ISecureElementSession
        public ISecureElementChannel openLogicalChannel(byte[] aid, byte p2, ISecureElementListener listener) throws RemoteException {
            return null;
        }

        @Override // android.p009se.omapi.ISecureElementSession
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.p009se.omapi.ISecureElementSession
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.se.omapi.ISecureElementSession$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISecureElementSession {
        public static final String DESCRIPTOR = "android$se$omapi$ISecureElementSession".replace('$', '.');
        static final int TRANSACTION_close = 2;
        static final int TRANSACTION_closeChannels = 3;
        static final int TRANSACTION_getAtr = 1;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_isClosed = 4;
        static final int TRANSACTION_openBasicChannel = 5;
        static final int TRANSACTION_openLogicalChannel = 6;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ISecureElementSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISecureElementSession)) {
                return (ISecureElementSession) iin;
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
                            byte[] _result = getAtr();
                            reply.writeNoException();
                            reply.writeByteArray(_result);
                            break;
                        case 2:
                            close();
                            reply.writeNoException();
                            break;
                        case 3:
                            closeChannels();
                            reply.writeNoException();
                            break;
                        case 4:
                            boolean _result2 = isClosed();
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 5:
                            byte[] _arg0 = data.createByteArray();
                            byte _arg1 = data.readByte();
                            ISecureElementListener _arg2 = ISecureElementListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ISecureElementChannel _result3 = openBasicChannel(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 6:
                            byte[] _arg02 = data.createByteArray();
                            byte _arg12 = data.readByte();
                            ISecureElementListener _arg22 = ISecureElementListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ISecureElementChannel _result4 = openLogicalChannel(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result4);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.se.omapi.ISecureElementSession$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements ISecureElementSession {
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
                return Stub.DESCRIPTOR;
            }

            @Override // android.p009se.omapi.ISecureElementSession
            public byte[] getAtr() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getAtr is unimplemented.");
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementSession
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method close is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementSession
            public void closeChannels() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method closeChannels is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementSession
            public boolean isClosed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method isClosed is unimplemented.");
                    }
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementSession
            public ISecureElementChannel openBasicChannel(byte[] aid, byte p2, ISecureElementListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(aid);
                    _data.writeByte(p2);
                    _data.writeStrongInterface(listener);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method openBasicChannel is unimplemented.");
                    }
                    _reply.readException();
                    ISecureElementChannel _result = ISecureElementChannel.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementSession
            public ISecureElementChannel openLogicalChannel(byte[] aid, byte p2, ISecureElementListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(aid);
                    _data.writeByte(p2);
                    _data.writeStrongInterface(listener);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method openLogicalChannel is unimplemented.");
                    }
                    _reply.readException();
                    ISecureElementChannel _result = ISecureElementChannel.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementSession
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.p009se.omapi.ISecureElementSession
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
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
