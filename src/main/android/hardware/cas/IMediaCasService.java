package android.hardware.cas;

import android.hardware.cas.ICas;
import android.hardware.cas.ICasListener;
import android.hardware.cas.IDescrambler;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IMediaCasService extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$cas$IMediaCasService".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 1;

    IDescrambler createDescrambler(int i) throws RemoteException;

    ICas createPlugin(int i, ICasListener iCasListener) throws RemoteException;

    AidlCasPluginDescriptor[] enumeratePlugins() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    boolean isDescramblerSupported(int i) throws RemoteException;

    boolean isSystemIdSupported(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IMediaCasService {
        @Override // android.hardware.cas.IMediaCasService
        public IDescrambler createDescrambler(int CA_system_id) throws RemoteException {
            return null;
        }

        @Override // android.hardware.cas.IMediaCasService
        public ICas createPlugin(int CA_system_id, ICasListener listener) throws RemoteException {
            return null;
        }

        @Override // android.hardware.cas.IMediaCasService
        public AidlCasPluginDescriptor[] enumeratePlugins() throws RemoteException {
            return null;
        }

        @Override // android.hardware.cas.IMediaCasService
        public boolean isDescramblerSupported(int CA_system_id) throws RemoteException {
            return false;
        }

        @Override // android.hardware.cas.IMediaCasService
        public boolean isSystemIdSupported(int CA_system_id) throws RemoteException {
            return false;
        }

        @Override // android.hardware.cas.IMediaCasService
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.cas.IMediaCasService
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IMediaCasService {
        static final int TRANSACTION_createDescrambler = 1;
        static final int TRANSACTION_createPlugin = 2;
        static final int TRANSACTION_enumeratePlugins = 3;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_isDescramblerSupported = 4;
        static final int TRANSACTION_isSystemIdSupported = 5;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaCasService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaCasService)) {
                return (IMediaCasService) iin;
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
                            IDescrambler _result = createDescrambler(_arg0);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            ICasListener _arg1 = ICasListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ICas _result2 = createPlugin(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            AidlCasPluginDescriptor[] _result3 = enumeratePlugins();
                            reply.writeNoException();
                            reply.writeTypedArray(_result3, 1);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = isDescramblerSupported(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = isSystemIdSupported(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IMediaCasService {
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

            @Override // android.hardware.cas.IMediaCasService
            public IDescrambler createDescrambler(int CA_system_id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(CA_system_id);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method createDescrambler is unimplemented.");
                    }
                    _reply.readException();
                    IDescrambler _result = IDescrambler.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.IMediaCasService
            public ICas createPlugin(int CA_system_id, ICasListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(CA_system_id);
                    _data.writeStrongInterface(listener);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method createPlugin is unimplemented.");
                    }
                    _reply.readException();
                    ICas _result = ICas.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.IMediaCasService
            public AidlCasPluginDescriptor[] enumeratePlugins() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method enumeratePlugins is unimplemented.");
                    }
                    _reply.readException();
                    AidlCasPluginDescriptor[] _result = (AidlCasPluginDescriptor[]) _reply.createTypedArray(AidlCasPluginDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.IMediaCasService
            public boolean isDescramblerSupported(int CA_system_id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(CA_system_id);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method isDescramblerSupported is unimplemented.");
                    }
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.IMediaCasService
            public boolean isSystemIdSupported(int CA_system_id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(CA_system_id);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method isSystemIdSupported is unimplemented.");
                    }
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.cas.IMediaCasService
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

            @Override // android.hardware.cas.IMediaCasService
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
