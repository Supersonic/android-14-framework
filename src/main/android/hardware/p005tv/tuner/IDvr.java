package android.hardware.p005tv.tuner;

import android.hardware.common.fmq.MQDescriptor;
import android.hardware.p005tv.tuner.IFilter;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.hardware.tv.tuner.IDvr */
/* loaded from: classes2.dex */
public interface IDvr extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$tv$tuner$IDvr".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void attachFilter(IFilter iFilter) throws RemoteException;

    void close() throws RemoteException;

    void configure(DvrSettings dvrSettings) throws RemoteException;

    void detachFilter(IFilter iFilter) throws RemoteException;

    void flush() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getQueueDesc(MQDescriptor<Byte, Byte> mQDescriptor) throws RemoteException;

    void setStatusCheckIntervalHint(long j) throws RemoteException;

    void start() throws RemoteException;

    void stop() throws RemoteException;

    /* renamed from: android.hardware.tv.tuner.IDvr$Default */
    /* loaded from: classes2.dex */
    public static class Default implements IDvr {
        @Override // android.hardware.p005tv.tuner.IDvr
        public void getQueueDesc(MQDescriptor<Byte, Byte> queue) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDvr
        public void configure(DvrSettings settings) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDvr
        public void attachFilter(IFilter filter) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDvr
        public void detachFilter(IFilter filter) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDvr
        public void start() throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDvr
        public void stop() throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDvr
        public void flush() throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDvr
        public void close() throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDvr
        public void setStatusCheckIntervalHint(long milliseconds) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDvr
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.p005tv.tuner.IDvr
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.hardware.tv.tuner.IDvr$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IDvr {
        static final int TRANSACTION_attachFilter = 3;
        static final int TRANSACTION_close = 8;
        static final int TRANSACTION_configure = 2;
        static final int TRANSACTION_detachFilter = 4;
        static final int TRANSACTION_flush = 7;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getQueueDesc = 1;
        static final int TRANSACTION_setStatusCheckIntervalHint = 9;
        static final int TRANSACTION_start = 5;
        static final int TRANSACTION_stop = 6;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IDvr asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDvr)) {
                return (IDvr) iin;
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
                            MQDescriptor<Byte, Byte> _arg0 = new MQDescriptor<>();
                            data.enforceNoDataAvail();
                            getQueueDesc(_arg0);
                            reply.writeNoException();
                            reply.writeTypedObject(_arg0, 1);
                            break;
                        case 2:
                            data.enforceNoDataAvail();
                            configure((DvrSettings) data.readTypedObject(DvrSettings.CREATOR));
                            reply.writeNoException();
                            break;
                        case 3:
                            IFilter _arg02 = IFilter.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            attachFilter(_arg02);
                            reply.writeNoException();
                            break;
                        case 4:
                            IFilter _arg03 = IFilter.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            detachFilter(_arg03);
                            reply.writeNoException();
                            break;
                        case 5:
                            start();
                            reply.writeNoException();
                            break;
                        case 6:
                            stop();
                            reply.writeNoException();
                            break;
                        case 7:
                            flush();
                            reply.writeNoException();
                            break;
                        case 8:
                            close();
                            reply.writeNoException();
                            break;
                        case 9:
                            long _arg04 = data.readLong();
                            data.enforceNoDataAvail();
                            setStatusCheckIntervalHint(_arg04);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.hardware.tv.tuner.IDvr$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements IDvr {
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

            @Override // android.hardware.p005tv.tuner.IDvr
            public void getQueueDesc(MQDescriptor<Byte, Byte> queue) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getQueueDesc is unimplemented.");
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        queue.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDvr
            public void configure(DvrSettings settings) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(settings, 0);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method configure is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDvr
            public void attachFilter(IFilter filter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(filter);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method attachFilter is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDvr
            public void detachFilter(IFilter filter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(filter);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method detachFilter is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDvr
            public void start() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method start is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDvr
            public void stop() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method stop is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDvr
            public void flush() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method flush is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDvr
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method close is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDvr
            public void setStatusCheckIntervalHint(long milliseconds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(milliseconds);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setStatusCheckIntervalHint is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDvr
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

            @Override // android.hardware.p005tv.tuner.IDvr
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
