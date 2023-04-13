package android.hardware.p005tv.tuner;

import android.hardware.p005tv.tuner.ILnbCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.hardware.tv.tuner.ILnb */
/* loaded from: classes2.dex */
public interface ILnb extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$tv$tuner$ILnb".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void close() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void sendDiseqcMessage(byte[] bArr) throws RemoteException;

    void setCallback(ILnbCallback iLnbCallback) throws RemoteException;

    void setSatellitePosition(int i) throws RemoteException;

    void setTone(int i) throws RemoteException;

    void setVoltage(int i) throws RemoteException;

    /* renamed from: android.hardware.tv.tuner.ILnb$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ILnb {
        @Override // android.hardware.p005tv.tuner.ILnb
        public void setCallback(ILnbCallback callback) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.ILnb
        public void setVoltage(int voltage) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.ILnb
        public void setTone(int tone) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.ILnb
        public void setSatellitePosition(int position) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.ILnb
        public void sendDiseqcMessage(byte[] diseqcMessage) throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.ILnb
        public void close() throws RemoteException {
        }

        @Override // android.hardware.p005tv.tuner.ILnb
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.p005tv.tuner.ILnb
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.hardware.tv.tuner.ILnb$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ILnb {
        static final int TRANSACTION_close = 6;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_sendDiseqcMessage = 5;
        static final int TRANSACTION_setCallback = 1;
        static final int TRANSACTION_setSatellitePosition = 4;
        static final int TRANSACTION_setTone = 3;
        static final int TRANSACTION_setVoltage = 2;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ILnb asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ILnb)) {
                return (ILnb) iin;
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
                            ILnbCallback _arg0 = ILnbCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setCallback(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            setVoltage(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            setTone(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            setSatellitePosition(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            byte[] _arg05 = data.createByteArray();
                            data.enforceNoDataAvail();
                            sendDiseqcMessage(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            close();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.hardware.tv.tuner.ILnb$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ILnb {
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

            @Override // android.hardware.p005tv.tuner.ILnb
            public void setCallback(ILnbCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setCallback is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ILnb
            public void setVoltage(int voltage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(voltage);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setVoltage is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ILnb
            public void setTone(int tone) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(tone);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setTone is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ILnb
            public void setSatellitePosition(int position) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(position);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setSatellitePosition is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ILnb
            public void sendDiseqcMessage(byte[] diseqcMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(diseqcMessage);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method sendDiseqcMessage is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ILnb
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method close is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.p005tv.tuner.ILnb
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

            @Override // android.hardware.p005tv.tuner.ILnb
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
