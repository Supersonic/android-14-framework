package android.hardware.cas;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IDescrambler extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$cas$IDescrambler".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 1;

    int descramble(int i, SubSample[] subSampleArr, SharedBuffer sharedBuffer, long j, DestinationBuffer destinationBuffer, long j2) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void release() throws RemoteException;

    boolean requiresSecureDecoderComponent(String str) throws RemoteException;

    void setMediaCasSession(byte[] bArr) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IDescrambler {
        @Override // android.hardware.cas.IDescrambler
        public int descramble(int scramblingControl, SubSample[] subSamples, SharedBuffer srcBuffer, long srcOffset, DestinationBuffer dstBuffer, long dstOffset) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.cas.IDescrambler
        public void release() throws RemoteException {
        }

        @Override // android.hardware.cas.IDescrambler
        public boolean requiresSecureDecoderComponent(String mime) throws RemoteException {
            return false;
        }

        @Override // android.hardware.cas.IDescrambler
        public void setMediaCasSession(byte[] sessionId) throws RemoteException {
        }

        @Override // android.hardware.cas.IDescrambler
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.cas.IDescrambler
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IDescrambler {
        static final int TRANSACTION_descramble = 1;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_release = 2;
        static final int TRANSACTION_requiresSecureDecoderComponent = 3;
        static final int TRANSACTION_setMediaCasSession = 4;

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
                            SubSample[] _arg1 = (SubSample[]) data.createTypedArray(SubSample.CREATOR);
                            SharedBuffer _arg2 = (SharedBuffer) data.readTypedObject(SharedBuffer.CREATOR);
                            long _arg3 = data.readLong();
                            DestinationBuffer _arg4 = (DestinationBuffer) data.readTypedObject(DestinationBuffer.CREATOR);
                            long _arg5 = data.readLong();
                            data.enforceNoDataAvail();
                            int _result = descramble(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            release();
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result2 = requiresSecureDecoderComponent(_arg02);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 4:
                            byte[] _arg03 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setMediaCasSession(_arg03);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
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

            @Override // android.hardware.cas.IDescrambler
            public int descramble(int scramblingControl, SubSample[] subSamples, SharedBuffer srcBuffer, long srcOffset, DestinationBuffer dstBuffer, long dstOffset) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(scramblingControl);
                    _data.writeTypedArray(subSamples, 0);
                    _data.writeTypedObject(srcBuffer, 0);
                    _data.writeLong(srcOffset);
                    _data.writeTypedObject(dstBuffer, 0);
                    _data.writeLong(dstOffset);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method descramble is unimplemented.");
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.IDescrambler
            public void release() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method release is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.IDescrambler
            public boolean requiresSecureDecoderComponent(String mime) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(mime);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method requiresSecureDecoderComponent is unimplemented.");
                    }
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.IDescrambler
            public void setMediaCasSession(byte[] sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(sessionId);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setMediaCasSession is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.IDescrambler
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

            @Override // android.hardware.cas.IDescrambler
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
