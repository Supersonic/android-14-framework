package android.hardware.p005tv.tuner;

import android.hardware.p005tv.tuner.IFilter;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.hardware.tv.tuner.IDescrambler */
/* loaded from: classes2.dex */
public interface IDescrambler extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$tv$tuner$IDescrambler".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void addPid(DemuxPid demuxPid, IFilter iFilter) throws RemoteException;

    void close() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void removePid(DemuxPid demuxPid, IFilter iFilter) throws RemoteException;

    void setDemuxSource(int i) throws RemoteException;

    void setKeyToken(byte[] bArr) throws RemoteException;

    /* renamed from: android.hardware.tv.tuner.IDescrambler$Default */
    /* loaded from: classes2.dex */
    public static class Default implements IDescrambler {
        @Override // android.hardware.p005tv.tuner.IDescrambler
        public void setDemuxSource(int demuxId) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDescrambler
        public void setKeyToken(byte[] keyToken) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDescrambler
        public void addPid(DemuxPid pid, IFilter optionalSourceFilter) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDescrambler
        public void removePid(DemuxPid pid, IFilter optionalSourceFilter) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDescrambler
        public void close() throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.IDescrambler
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.p005tv.tuner.IDescrambler
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.hardware.tv.tuner.IDescrambler$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IDescrambler {
        static final int TRANSACTION_addPid = 3;
        static final int TRANSACTION_close = 5;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_removePid = 4;
        static final int TRANSACTION_setDemuxSource = 1;
        static final int TRANSACTION_setKeyToken = 2;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IDescrambler asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDescrambler)) {
                return (IDescrambler) iin;
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
                            data.enforceNoDataAvail();
                            setDemuxSource(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            byte[] _arg02 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setKeyToken(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            DemuxPid _arg03 = (DemuxPid) data.readTypedObject(DemuxPid.CREATOR);
                            IFilter _arg1 = IFilter.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addPid(_arg03, _arg1);
                            reply.writeNoException();
                            break;
                        case 4:
                            DemuxPid _arg04 = (DemuxPid) data.readTypedObject(DemuxPid.CREATOR);
                            IFilter _arg12 = IFilter.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removePid(_arg04, _arg12);
                            reply.writeNoException();
                            break;
                        case 5:
                            close();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.hardware.tv.tuner.IDescrambler$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements IDescrambler {
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

            @Override // android.hardware.p005tv.tuner.IDescrambler
            public void setDemuxSource(int demuxId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(demuxId);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setDemuxSource is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDescrambler
            public void setKeyToken(byte[] keyToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(keyToken);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setKeyToken is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDescrambler
            public void addPid(DemuxPid pid, IFilter optionalSourceFilter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(pid, 0);
                    _data.writeStrongInterface(optionalSourceFilter);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method addPid is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDescrambler
            public void removePid(DemuxPid pid, IFilter optionalSourceFilter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(pid, 0);
                    _data.writeStrongInterface(optionalSourceFilter);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method removePid is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDescrambler
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method close is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.IDescrambler
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

            @Override // android.hardware.p005tv.tuner.IDescrambler
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
