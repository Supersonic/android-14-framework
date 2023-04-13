package android.p008os;

import android.media.p007tv.interactive.TvInteractiveAppService;
import android.p008os.IUpdateEngineCallback;
/* renamed from: android.os.IUpdateEngine */
/* loaded from: classes3.dex */
public interface IUpdateEngine extends IInterface {
    long allocateSpaceForPayload(String str, String[] strArr) throws RemoteException;

    void applyPayload(String str, long j, long j2, String[] strArr) throws RemoteException;

    void applyPayloadFd(ParcelFileDescriptor parcelFileDescriptor, long j, long j2, String[] strArr) throws RemoteException;

    boolean bind(IUpdateEngineCallback iUpdateEngineCallback) throws RemoteException;

    void cancel() throws RemoteException;

    void cleanupSuccessfulUpdate(IUpdateEngineCallback iUpdateEngineCallback) throws RemoteException;

    void resetShouldSwitchSlotOnReboot() throws RemoteException;

    void resetStatus() throws RemoteException;

    void resume() throws RemoteException;

    void setShouldSwitchSlotOnReboot(String str) throws RemoteException;

    void suspend() throws RemoteException;

    boolean unbind(IUpdateEngineCallback iUpdateEngineCallback) throws RemoteException;

    boolean verifyPayloadApplicable(String str) throws RemoteException;

    /* renamed from: android.os.IUpdateEngine$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IUpdateEngine {
        @Override // android.p008os.IUpdateEngine
        public void applyPayload(String url, long payload_offset, long payload_size, String[] headerKeyValuePairs) throws RemoteException {
        }

        @Override // android.p008os.IUpdateEngine
        public void applyPayloadFd(ParcelFileDescriptor pfd, long payload_offset, long payload_size, String[] headerKeyValuePairs) throws RemoteException {
        }

        @Override // android.p008os.IUpdateEngine
        public boolean bind(IUpdateEngineCallback callback) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUpdateEngine
        public boolean unbind(IUpdateEngineCallback callback) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUpdateEngine
        public void suspend() throws RemoteException {
        }

        @Override // android.p008os.IUpdateEngine
        public void resume() throws RemoteException {
        }

        @Override // android.p008os.IUpdateEngine
        public void cancel() throws RemoteException {
        }

        @Override // android.p008os.IUpdateEngine
        public void resetStatus() throws RemoteException {
        }

        @Override // android.p008os.IUpdateEngine
        public void setShouldSwitchSlotOnReboot(String metadataFilename) throws RemoteException {
        }

        @Override // android.p008os.IUpdateEngine
        public void resetShouldSwitchSlotOnReboot() throws RemoteException {
        }

        @Override // android.p008os.IUpdateEngine
        public boolean verifyPayloadApplicable(String metadataFilename) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUpdateEngine
        public long allocateSpaceForPayload(String metadataFilename, String[] headerKeyValuePairs) throws RemoteException {
            return 0L;
        }

        @Override // android.p008os.IUpdateEngine
        public void cleanupSuccessfulUpdate(IUpdateEngineCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IUpdateEngine$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IUpdateEngine {
        public static final String DESCRIPTOR = "android.os.IUpdateEngine";
        static final int TRANSACTION_allocateSpaceForPayload = 12;
        static final int TRANSACTION_applyPayload = 1;
        static final int TRANSACTION_applyPayloadFd = 2;
        static final int TRANSACTION_bind = 3;
        static final int TRANSACTION_cancel = 7;
        static final int TRANSACTION_cleanupSuccessfulUpdate = 13;
        static final int TRANSACTION_resetShouldSwitchSlotOnReboot = 10;
        static final int TRANSACTION_resetStatus = 8;
        static final int TRANSACTION_resume = 6;
        static final int TRANSACTION_setShouldSwitchSlotOnReboot = 9;
        static final int TRANSACTION_suspend = 5;
        static final int TRANSACTION_unbind = 4;
        static final int TRANSACTION_verifyPayloadApplicable = 11;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUpdateEngine asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IUpdateEngine)) {
                return (IUpdateEngine) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "applyPayload";
                case 2:
                    return "applyPayloadFd";
                case 3:
                    return "bind";
                case 4:
                    return "unbind";
                case 5:
                    return "suspend";
                case 6:
                    return TvInteractiveAppService.TIME_SHIFT_COMMAND_TYPE_RESUME;
                case 7:
                    return "cancel";
                case 8:
                    return "resetStatus";
                case 9:
                    return "setShouldSwitchSlotOnReboot";
                case 10:
                    return "resetShouldSwitchSlotOnReboot";
                case 11:
                    return "verifyPayloadApplicable";
                case 12:
                    return "allocateSpaceForPayload";
                case 13:
                    return "cleanupSuccessfulUpdate";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            long _arg1 = data.readLong();
                            long _arg2 = data.readLong();
                            String[] _arg3 = data.createStringArray();
                            data.enforceNoDataAvail();
                            applyPayload(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 2:
                            ParcelFileDescriptor _arg02 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            long _arg12 = data.readLong();
                            long _arg22 = data.readLong();
                            String[] _arg32 = data.createStringArray();
                            data.enforceNoDataAvail();
                            applyPayloadFd(_arg02, _arg12, _arg22, _arg32);
                            reply.writeNoException();
                            break;
                        case 3:
                            IUpdateEngineCallback _arg03 = IUpdateEngineCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result = bind(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 4:
                            IUpdateEngineCallback _arg04 = IUpdateEngineCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result2 = unbind(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 5:
                            suspend();
                            reply.writeNoException();
                            break;
                        case 6:
                            resume();
                            reply.writeNoException();
                            break;
                        case 7:
                            cancel();
                            reply.writeNoException();
                            break;
                        case 8:
                            resetStatus();
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            setShouldSwitchSlotOnReboot(_arg05);
                            reply.writeNoException();
                            break;
                        case 10:
                            resetShouldSwitchSlotOnReboot();
                            reply.writeNoException();
                            break;
                        case 11:
                            String _arg06 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result3 = verifyPayloadApplicable(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 12:
                            String _arg07 = data.readString();
                            String[] _arg13 = data.createStringArray();
                            data.enforceNoDataAvail();
                            long _result4 = allocateSpaceForPayload(_arg07, _arg13);
                            reply.writeNoException();
                            reply.writeLong(_result4);
                            break;
                        case 13:
                            IUpdateEngineCallback _arg08 = IUpdateEngineCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            cleanupSuccessfulUpdate(_arg08);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IUpdateEngine$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IUpdateEngine {
            private IBinder mRemote;

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

            @Override // android.p008os.IUpdateEngine
            public void applyPayload(String url, long payload_offset, long payload_size, String[] headerKeyValuePairs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    _data.writeLong(payload_offset);
                    _data.writeLong(payload_size);
                    _data.writeStringArray(headerKeyValuePairs);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public void applyPayloadFd(ParcelFileDescriptor pfd, long payload_offset, long payload_size, String[] headerKeyValuePairs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(pfd, 0);
                    _data.writeLong(payload_offset);
                    _data.writeLong(payload_size);
                    _data.writeStringArray(headerKeyValuePairs);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public boolean bind(IUpdateEngineCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public boolean unbind(IUpdateEngineCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public void suspend() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public void resume() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public void cancel() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public void resetStatus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public void setShouldSwitchSlotOnReboot(String metadataFilename) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(metadataFilename);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public void resetShouldSwitchSlotOnReboot() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public boolean verifyPayloadApplicable(String metadataFilename) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(metadataFilename);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public long allocateSpaceForPayload(String metadataFilename, String[] headerKeyValuePairs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(metadataFilename);
                    _data.writeStringArray(headerKeyValuePairs);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUpdateEngine
            public void cleanupSuccessfulUpdate(IUpdateEngineCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 12;
        }
    }
}
