package android.nfc;

import android.media.MediaMetrics;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface INfcTag extends IInterface {
    boolean canMakeReadOnly(int i) throws RemoteException;

    int connect(int i, int i2) throws RemoteException;

    int formatNdef(int i, byte[] bArr) throws RemoteException;

    boolean getExtendedLengthApdusSupported() throws RemoteException;

    int getMaxTransceiveLength(int i) throws RemoteException;

    int[] getTechList(int i) throws RemoteException;

    int getTimeout(int i) throws RemoteException;

    boolean isNdef(int i) throws RemoteException;

    boolean isPresent(int i) throws RemoteException;

    boolean isTagUpToDate(long j) throws RemoteException;

    boolean ndefIsWritable(int i) throws RemoteException;

    int ndefMakeReadOnly(int i) throws RemoteException;

    NdefMessage ndefRead(int i) throws RemoteException;

    int ndefWrite(int i, NdefMessage ndefMessage) throws RemoteException;

    int reconnect(int i) throws RemoteException;

    Tag rediscover(int i) throws RemoteException;

    void resetTimeouts() throws RemoteException;

    int setTimeout(int i, int i2) throws RemoteException;

    TransceiveResult transceive(int i, byte[] bArr, boolean z) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements INfcTag {
        @Override // android.nfc.INfcTag
        public int connect(int nativeHandle, int technology) throws RemoteException {
            return 0;
        }

        @Override // android.nfc.INfcTag
        public int reconnect(int nativeHandle) throws RemoteException {
            return 0;
        }

        @Override // android.nfc.INfcTag
        public int[] getTechList(int nativeHandle) throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcTag
        public boolean isNdef(int nativeHandle) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcTag
        public boolean isPresent(int nativeHandle) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcTag
        public TransceiveResult transceive(int nativeHandle, byte[] data, boolean raw) throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcTag
        public NdefMessage ndefRead(int nativeHandle) throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcTag
        public int ndefWrite(int nativeHandle, NdefMessage msg) throws RemoteException {
            return 0;
        }

        @Override // android.nfc.INfcTag
        public int ndefMakeReadOnly(int nativeHandle) throws RemoteException {
            return 0;
        }

        @Override // android.nfc.INfcTag
        public boolean ndefIsWritable(int nativeHandle) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcTag
        public int formatNdef(int nativeHandle, byte[] key) throws RemoteException {
            return 0;
        }

        @Override // android.nfc.INfcTag
        public Tag rediscover(int nativehandle) throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcTag
        public int setTimeout(int technology, int timeout) throws RemoteException {
            return 0;
        }

        @Override // android.nfc.INfcTag
        public int getTimeout(int technology) throws RemoteException {
            return 0;
        }

        @Override // android.nfc.INfcTag
        public void resetTimeouts() throws RemoteException {
        }

        @Override // android.nfc.INfcTag
        public boolean canMakeReadOnly(int ndefType) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcTag
        public int getMaxTransceiveLength(int technology) throws RemoteException {
            return 0;
        }

        @Override // android.nfc.INfcTag
        public boolean getExtendedLengthApdusSupported() throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcTag
        public boolean isTagUpToDate(long cookie) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements INfcTag {
        public static final String DESCRIPTOR = "android.nfc.INfcTag";
        static final int TRANSACTION_canMakeReadOnly = 16;
        static final int TRANSACTION_connect = 1;
        static final int TRANSACTION_formatNdef = 11;
        static final int TRANSACTION_getExtendedLengthApdusSupported = 18;
        static final int TRANSACTION_getMaxTransceiveLength = 17;
        static final int TRANSACTION_getTechList = 3;
        static final int TRANSACTION_getTimeout = 14;
        static final int TRANSACTION_isNdef = 4;
        static final int TRANSACTION_isPresent = 5;
        static final int TRANSACTION_isTagUpToDate = 19;
        static final int TRANSACTION_ndefIsWritable = 10;
        static final int TRANSACTION_ndefMakeReadOnly = 9;
        static final int TRANSACTION_ndefRead = 7;
        static final int TRANSACTION_ndefWrite = 8;
        static final int TRANSACTION_reconnect = 2;
        static final int TRANSACTION_rediscover = 12;
        static final int TRANSACTION_resetTimeouts = 15;
        static final int TRANSACTION_setTimeout = 13;
        static final int TRANSACTION_transceive = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INfcTag asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INfcTag)) {
                return (INfcTag) iin;
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
                    return MediaMetrics.Value.CONNECT;
                case 2:
                    return "reconnect";
                case 3:
                    return "getTechList";
                case 4:
                    return "isNdef";
                case 5:
                    return "isPresent";
                case 6:
                    return "transceive";
                case 7:
                    return "ndefRead";
                case 8:
                    return "ndefWrite";
                case 9:
                    return "ndefMakeReadOnly";
                case 10:
                    return "ndefIsWritable";
                case 11:
                    return "formatNdef";
                case 12:
                    return "rediscover";
                case 13:
                    return "setTimeout";
                case 14:
                    return "getTimeout";
                case 15:
                    return "resetTimeouts";
                case 16:
                    return "canMakeReadOnly";
                case 17:
                    return "getMaxTransceiveLength";
                case 18:
                    return "getExtendedLengthApdusSupported";
                case 19:
                    return "isTagUpToDate";
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
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result = connect(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result2 = reconnect(_arg02);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            int[] _result3 = getTechList(_arg03);
                            reply.writeNoException();
                            reply.writeIntArray(_result3);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = isNdef(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = isPresent(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            byte[] _arg12 = data.createByteArray();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            TransceiveResult _result6 = transceive(_arg06, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            NdefMessage _result7 = ndefRead(_arg07);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            NdefMessage _arg13 = (NdefMessage) data.readTypedObject(NdefMessage.CREATOR);
                            data.enforceNoDataAvail();
                            int _result8 = ndefWrite(_arg08, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result9 = ndefMakeReadOnly(_arg09);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result10 = ndefIsWritable(_arg010);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            byte[] _arg14 = data.createByteArray();
                            data.enforceNoDataAvail();
                            int _result11 = formatNdef(_arg011, _arg14);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            Tag _result12 = rediscover(_arg012);
                            reply.writeNoException();
                            reply.writeTypedObject(_result12, 1);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result13 = setTimeout(_arg013, _arg15);
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result14 = getTimeout(_arg014);
                            reply.writeNoException();
                            reply.writeInt(_result14);
                            break;
                        case 15:
                            resetTimeouts();
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result15 = canMakeReadOnly(_arg015);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 17:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result16 = getMaxTransceiveLength(_arg016);
                            reply.writeNoException();
                            reply.writeInt(_result16);
                            break;
                        case 18:
                            boolean _result17 = getExtendedLengthApdusSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            break;
                        case 19:
                            long _arg017 = data.readLong();
                            data.enforceNoDataAvail();
                            boolean _result18 = isTagUpToDate(_arg017);
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements INfcTag {
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

            @Override // android.nfc.INfcTag
            public int connect(int nativeHandle, int technology) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    _data.writeInt(technology);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public int reconnect(int nativeHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public int[] getTechList(int nativeHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public boolean isNdef(int nativeHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public boolean isPresent(int nativeHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public TransceiveResult transceive(int nativeHandle, byte[] data, boolean raw) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    _data.writeByteArray(data);
                    _data.writeBoolean(raw);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    TransceiveResult _result = (TransceiveResult) _reply.readTypedObject(TransceiveResult.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public NdefMessage ndefRead(int nativeHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    NdefMessage _result = (NdefMessage) _reply.readTypedObject(NdefMessage.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public int ndefWrite(int nativeHandle, NdefMessage msg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    _data.writeTypedObject(msg, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public int ndefMakeReadOnly(int nativeHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public boolean ndefIsWritable(int nativeHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public int formatNdef(int nativeHandle, byte[] key) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    _data.writeByteArray(key);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public Tag rediscover(int nativehandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativehandle);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    Tag _result = (Tag) _reply.readTypedObject(Tag.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public int setTimeout(int technology, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(technology);
                    _data.writeInt(timeout);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public int getTimeout(int technology) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(technology);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public void resetTimeouts() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public boolean canMakeReadOnly(int ndefType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(ndefType);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public int getMaxTransceiveLength(int technology) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(technology);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public boolean getExtendedLengthApdusSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcTag
            public boolean isTagUpToDate(long cookie) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(cookie);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 18;
        }
    }
}
