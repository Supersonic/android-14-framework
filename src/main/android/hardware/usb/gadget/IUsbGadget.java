package android.hardware.usb.gadget;

import android.hardware.usb.gadget.IUsbGadgetCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IUsbGadget extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$usb$gadget$IUsbGadget".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 1;

    void getCurrentUsbFunctions(IUsbGadgetCallback iUsbGadgetCallback, long j) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getUsbSpeed(IUsbGadgetCallback iUsbGadgetCallback, long j) throws RemoteException;

    void reset(IUsbGadgetCallback iUsbGadgetCallback, long j) throws RemoteException;

    void setCurrentUsbFunctions(long j, IUsbGadgetCallback iUsbGadgetCallback, long j2, long j3) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IUsbGadget {
        @Override // android.hardware.usb.gadget.IUsbGadget
        public void setCurrentUsbFunctions(long functions, IUsbGadgetCallback callback, long timeoutMs, long transactionId) throws RemoteException {
        }

        @Override // android.hardware.usb.gadget.IUsbGadget
        public void getCurrentUsbFunctions(IUsbGadgetCallback callback, long transactionId) throws RemoteException {
        }

        @Override // android.hardware.usb.gadget.IUsbGadget
        public void getUsbSpeed(IUsbGadgetCallback callback, long transactionId) throws RemoteException {
        }

        @Override // android.hardware.usb.gadget.IUsbGadget
        public void reset(IUsbGadgetCallback callback, long transactionId) throws RemoteException {
        }

        @Override // android.hardware.usb.gadget.IUsbGadget
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.usb.gadget.IUsbGadget
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IUsbGadget {
        static final int TRANSACTION_getCurrentUsbFunctions = 2;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getUsbSpeed = 3;
        static final int TRANSACTION_reset = 4;
        static final int TRANSACTION_setCurrentUsbFunctions = 1;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IUsbGadget asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IUsbGadget)) {
                return (IUsbGadget) iin;
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
                            long _arg0 = data.readLong();
                            IUsbGadgetCallback _arg1 = IUsbGadgetCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg2 = data.readLong();
                            long _arg3 = data.readLong();
                            data.enforceNoDataAvail();
                            setCurrentUsbFunctions(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            IUsbGadgetCallback _arg02 = IUsbGadgetCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg12 = data.readLong();
                            data.enforceNoDataAvail();
                            getCurrentUsbFunctions(_arg02, _arg12);
                            break;
                        case 3:
                            IUsbGadgetCallback _arg03 = IUsbGadgetCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg13 = data.readLong();
                            data.enforceNoDataAvail();
                            getUsbSpeed(_arg03, _arg13);
                            break;
                        case 4:
                            IUsbGadgetCallback _arg04 = IUsbGadgetCallback.Stub.asInterface(data.readStrongBinder());
                            long _arg14 = data.readLong();
                            data.enforceNoDataAvail();
                            reset(_arg04, _arg14);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IUsbGadget {
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

            @Override // android.hardware.usb.gadget.IUsbGadget
            public void setCurrentUsbFunctions(long functions, IUsbGadgetCallback callback, long timeoutMs, long transactionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(functions);
                    _data.writeStrongInterface(callback);
                    _data.writeLong(timeoutMs);
                    _data.writeLong(transactionId);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCurrentUsbFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.usb.gadget.IUsbGadget
            public void getCurrentUsbFunctions(IUsbGadgetCallback callback, long transactionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeLong(transactionId);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCurrentUsbFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.usb.gadget.IUsbGadget
            public void getUsbSpeed(IUsbGadgetCallback callback, long transactionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeLong(transactionId);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getUsbSpeed is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.usb.gadget.IUsbGadget
            public void reset(IUsbGadgetCallback callback, long transactionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeLong(transactionId);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method reset is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.usb.gadget.IUsbGadget
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

            @Override // android.hardware.usb.gadget.IUsbGadget
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
