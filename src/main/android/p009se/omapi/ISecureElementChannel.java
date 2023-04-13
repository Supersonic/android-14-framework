package android.p009se.omapi;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.se.omapi.ISecureElementChannel */
/* loaded from: classes3.dex */
public interface ISecureElementChannel extends IInterface {
    public static final String HASH = "894069bcfe4f35ceb2088278ddf87c83adee8014";
    public static final int VERSION = 1;

    void close() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    byte[] getSelectResponse() throws RemoteException;

    boolean isBasicChannel() throws RemoteException;

    boolean isClosed() throws RemoteException;

    boolean selectNext() throws RemoteException;

    byte[] transmit(byte[] bArr) throws RemoteException;

    /* renamed from: android.se.omapi.ISecureElementChannel$Default */
    /* loaded from: classes3.dex */
    public static class Default implements ISecureElementChannel {
        @Override // android.p009se.omapi.ISecureElementChannel
        public void close() throws RemoteException {
        }

        @Override // android.p009se.omapi.ISecureElementChannel
        public boolean isClosed() throws RemoteException {
            return false;
        }

        @Override // android.p009se.omapi.ISecureElementChannel
        public boolean isBasicChannel() throws RemoteException {
            return false;
        }

        @Override // android.p009se.omapi.ISecureElementChannel
        public byte[] getSelectResponse() throws RemoteException {
            return null;
        }

        @Override // android.p009se.omapi.ISecureElementChannel
        public byte[] transmit(byte[] command) throws RemoteException {
            return null;
        }

        @Override // android.p009se.omapi.ISecureElementChannel
        public boolean selectNext() throws RemoteException {
            return false;
        }

        @Override // android.p009se.omapi.ISecureElementChannel
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.p009se.omapi.ISecureElementChannel
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.se.omapi.ISecureElementChannel$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISecureElementChannel {
        public static final String DESCRIPTOR = "android$se$omapi$ISecureElementChannel".replace('$', '.');
        static final int TRANSACTION_close = 1;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getSelectResponse = 4;
        static final int TRANSACTION_isBasicChannel = 3;
        static final int TRANSACTION_isClosed = 2;
        static final int TRANSACTION_selectNext = 6;
        static final int TRANSACTION_transmit = 5;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ISecureElementChannel asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISecureElementChannel)) {
                return (ISecureElementChannel) iin;
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
                            close();
                            reply.writeNoException();
                            break;
                        case 2:
                            boolean _result = isClosed();
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 3:
                            boolean _result2 = isBasicChannel();
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 4:
                            byte[] _result3 = getSelectResponse();
                            reply.writeNoException();
                            reply.writeByteArray(_result3);
                            break;
                        case 5:
                            byte[] _arg0 = data.createByteArray();
                            data.enforceNoDataAvail();
                            byte[] _result4 = transmit(_arg0);
                            reply.writeNoException();
                            reply.writeByteArray(_result4);
                            break;
                        case 6:
                            boolean _result5 = selectNext();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.se.omapi.ISecureElementChannel$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements ISecureElementChannel {
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

            @Override // android.p009se.omapi.ISecureElementChannel
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method close is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementChannel
            public boolean isClosed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
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

            @Override // android.p009se.omapi.ISecureElementChannel
            public boolean isBasicChannel() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method isBasicChannel is unimplemented.");
                    }
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementChannel
            public byte[] getSelectResponse() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getSelectResponse is unimplemented.");
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementChannel
            public byte[] transmit(byte[] command) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(command);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method transmit is unimplemented.");
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementChannel
            public boolean selectNext() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method selectNext is unimplemented.");
                    }
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementChannel
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

            @Override // android.p009se.omapi.ISecureElementChannel
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
