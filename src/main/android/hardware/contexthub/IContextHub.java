package android.hardware.contexthub;

import android.hardware.contexthub.IContextHubCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IContextHub extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$contexthub$IContextHub".replace('$', '.');
    public static final int EX_CONTEXT_HUB_UNSPECIFIED = -1;
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void disableNanoapp(int i, long j, int i2) throws RemoteException;

    void enableNanoapp(int i, long j, int i2) throws RemoteException;

    List<ContextHubInfo> getContextHubs() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    long[] getPreloadedNanoappIds(int i) throws RemoteException;

    void loadNanoapp(int i, NanoappBinary nanoappBinary, int i2) throws RemoteException;

    void onHostEndpointConnected(HostEndpointInfo hostEndpointInfo) throws RemoteException;

    void onHostEndpointDisconnected(char c) throws RemoteException;

    void onNanSessionStateChanged(NanSessionStateUpdate nanSessionStateUpdate) throws RemoteException;

    void onSettingChanged(byte b, boolean z) throws RemoteException;

    void queryNanoapps(int i) throws RemoteException;

    void registerCallback(int i, IContextHubCallback iContextHubCallback) throws RemoteException;

    void sendMessageToHub(int i, ContextHubMessage contextHubMessage) throws RemoteException;

    void setTestMode(boolean z) throws RemoteException;

    void unloadNanoapp(int i, long j, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IContextHub {
        @Override // android.hardware.contexthub.IContextHub
        public List<ContextHubInfo> getContextHubs() throws RemoteException {
            return null;
        }

        @Override // android.hardware.contexthub.IContextHub
        public void loadNanoapp(int contextHubId, NanoappBinary appBinary, int transactionId) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public void unloadNanoapp(int contextHubId, long appId, int transactionId) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public void disableNanoapp(int contextHubId, long appId, int transactionId) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public void enableNanoapp(int contextHubId, long appId, int transactionId) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public void onSettingChanged(byte setting, boolean enabled) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public void queryNanoapps(int contextHubId) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public void registerCallback(int contextHubId, IContextHubCallback cb) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public void sendMessageToHub(int contextHubId, ContextHubMessage message) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public void onHostEndpointConnected(HostEndpointInfo hostEndpointInfo) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public void onHostEndpointDisconnected(char hostEndpointId) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public long[] getPreloadedNanoappIds(int contextHubId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.contexthub.IContextHub
        public void onNanSessionStateChanged(NanSessionStateUpdate update) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public void setTestMode(boolean enable) throws RemoteException {
        }

        @Override // android.hardware.contexthub.IContextHub
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.contexthub.IContextHub
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IContextHub {
        static final int TRANSACTION_disableNanoapp = 4;
        static final int TRANSACTION_enableNanoapp = 5;
        static final int TRANSACTION_getContextHubs = 1;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getPreloadedNanoappIds = 12;
        static final int TRANSACTION_loadNanoapp = 2;
        static final int TRANSACTION_onHostEndpointConnected = 10;
        static final int TRANSACTION_onHostEndpointDisconnected = 11;
        static final int TRANSACTION_onNanSessionStateChanged = 13;
        static final int TRANSACTION_onSettingChanged = 6;
        static final int TRANSACTION_queryNanoapps = 7;
        static final int TRANSACTION_registerCallback = 8;
        static final int TRANSACTION_sendMessageToHub = 9;
        static final int TRANSACTION_setTestMode = 14;
        static final int TRANSACTION_unloadNanoapp = 3;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IContextHub asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IContextHub)) {
                return (IContextHub) iin;
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
                            List<ContextHubInfo> _result = getContextHubs();
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            break;
                        case 2:
                            int _arg0 = data.readInt();
                            NanoappBinary _arg1 = (NanoappBinary) data.readTypedObject(NanoappBinary.CREATOR);
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            loadNanoapp(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg02 = data.readInt();
                            long _arg12 = data.readLong();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            unloadNanoapp(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            long _arg13 = data.readLong();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            disableNanoapp(_arg03, _arg13, _arg23);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            long _arg14 = data.readLong();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            enableNanoapp(_arg04, _arg14, _arg24);
                            reply.writeNoException();
                            break;
                        case 6:
                            byte _arg05 = data.readByte();
                            boolean _arg15 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onSettingChanged(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            queryNanoapps(_arg06);
                            reply.writeNoException();
                            break;
                        case 8:
                            int _arg07 = data.readInt();
                            IContextHubCallback _arg16 = IContextHubCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerCallback(_arg07, _arg16);
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            ContextHubMessage _arg17 = (ContextHubMessage) data.readTypedObject(ContextHubMessage.CREATOR);
                            data.enforceNoDataAvail();
                            sendMessageToHub(_arg08, _arg17);
                            reply.writeNoException();
                            break;
                        case 10:
                            HostEndpointInfo _arg09 = (HostEndpointInfo) data.readTypedObject(HostEndpointInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onHostEndpointConnected(_arg09);
                            reply.writeNoException();
                            break;
                        case 11:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            onHostEndpointDisconnected((char) _arg010);
                            reply.writeNoException();
                            break;
                        case 12:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            long[] _result2 = getPreloadedNanoappIds(_arg011);
                            reply.writeNoException();
                            reply.writeLongArray(_result2);
                            break;
                        case 13:
                            NanSessionStateUpdate _arg012 = (NanSessionStateUpdate) data.readTypedObject(NanSessionStateUpdate.CREATOR);
                            data.enforceNoDataAvail();
                            onNanSessionStateChanged(_arg012);
                            reply.writeNoException();
                            break;
                        case 14:
                            boolean _arg013 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setTestMode(_arg013);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IContextHub {
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

            @Override // android.hardware.contexthub.IContextHub
            public List<ContextHubInfo> getContextHubs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getContextHubs is unimplemented.");
                    }
                    _reply.readException();
                    List<ContextHubInfo> _result = _reply.createTypedArrayList(ContextHubInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void loadNanoapp(int contextHubId, NanoappBinary appBinary, int transactionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeTypedObject(appBinary, 0);
                    _data.writeInt(transactionId);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method loadNanoapp is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void unloadNanoapp(int contextHubId, long appId, int transactionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeLong(appId);
                    _data.writeInt(transactionId);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method unloadNanoapp is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void disableNanoapp(int contextHubId, long appId, int transactionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeLong(appId);
                    _data.writeInt(transactionId);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method disableNanoapp is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void enableNanoapp(int contextHubId, long appId, int transactionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeLong(appId);
                    _data.writeInt(transactionId);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method enableNanoapp is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void onSettingChanged(byte setting, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByte(setting);
                    _data.writeBoolean(enabled);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onSettingChanged is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void queryNanoapps(int contextHubId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method queryNanoapps is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void registerCallback(int contextHubId, IContextHubCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeStrongInterface(cb);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method registerCallback is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void sendMessageToHub(int contextHubId, ContextHubMessage message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    _data.writeTypedObject(message, 0);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method sendMessageToHub is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void onHostEndpointConnected(HostEndpointInfo hostEndpointInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(hostEndpointInfo, 0);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onHostEndpointConnected is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void onHostEndpointDisconnected(char hostEndpointId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(hostEndpointId);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onHostEndpointDisconnected is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public long[] getPreloadedNanoappIds(int contextHubId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(contextHubId);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getPreloadedNanoappIds is unimplemented.");
                    }
                    _reply.readException();
                    long[] _result = _reply.createLongArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void onNanSessionStateChanged(NanSessionStateUpdate update) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(update, 0);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onNanSessionStateChanged is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
            public void setTestMode(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setTestMode is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.contexthub.IContextHub
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

            @Override // android.hardware.contexthub.IContextHub
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
