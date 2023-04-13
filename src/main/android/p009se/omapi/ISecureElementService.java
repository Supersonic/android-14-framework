package android.p009se.omapi;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p009se.omapi.ISecureElementReader;
/* renamed from: android.se.omapi.ISecureElementService */
/* loaded from: classes3.dex */
public interface ISecureElementService extends IInterface {
    public static final String HASH = "894069bcfe4f35ceb2088278ddf87c83adee8014";
    public static final int VERSION = 1;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    ISecureElementReader getReader(String str) throws RemoteException;

    String[] getReaders() throws RemoteException;

    boolean[] isNfcEventAllowed(String str, byte[] bArr, String[] strArr, int i) throws RemoteException;

    /* renamed from: android.se.omapi.ISecureElementService$Default */
    /* loaded from: classes3.dex */
    public static class Default implements ISecureElementService {
        @Override // android.p009se.omapi.ISecureElementService
        public String[] getReaders() throws RemoteException {
            return null;
        }

        @Override // android.p009se.omapi.ISecureElementService
        public ISecureElementReader getReader(String reader) throws RemoteException {
            return null;
        }

        @Override // android.p009se.omapi.ISecureElementService
        public boolean[] isNfcEventAllowed(String reader, byte[] aid, String[] packageNames, int userId) throws RemoteException {
            return null;
        }

        @Override // android.p009se.omapi.ISecureElementService
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.p009se.omapi.ISecureElementService
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.se.omapi.ISecureElementService$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISecureElementService {
        public static final String DESCRIPTOR = "android$se$omapi$ISecureElementService".replace('$', '.');
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getReader = 2;
        static final int TRANSACTION_getReaders = 1;
        static final int TRANSACTION_isNfcEventAllowed = 3;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ISecureElementService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISecureElementService)) {
                return (ISecureElementService) iin;
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
                            String[] _result = getReaders();
                            reply.writeNoException();
                            reply.writeStringArray(_result);
                            break;
                        case 2:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            ISecureElementReader _result2 = getReader(_arg0);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            String _arg02 = data.readString();
                            byte[] _arg1 = data.createByteArray();
                            String[] _arg2 = data.createStringArray();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean[] _result3 = isNfcEventAllowed(_arg02, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeBooleanArray(_result3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.se.omapi.ISecureElementService$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements ISecureElementService {
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

            @Override // android.p009se.omapi.ISecureElementService
            public String[] getReaders() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getReaders is unimplemented.");
                    }
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementService
            public ISecureElementReader getReader(String reader) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reader);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getReader is unimplemented.");
                    }
                    _reply.readException();
                    ISecureElementReader _result = ISecureElementReader.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementService
            public boolean[] isNfcEventAllowed(String reader, byte[] aid, String[] packageNames, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reader);
                    _data.writeByteArray(aid);
                    _data.writeStringArray(packageNames);
                    _data.writeInt(userId);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method isNfcEventAllowed is unimplemented.");
                    }
                    _reply.readException();
                    boolean[] _result = _reply.createBooleanArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p009se.omapi.ISecureElementService
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

            @Override // android.p009se.omapi.ISecureElementService
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
