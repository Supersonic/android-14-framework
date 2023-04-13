package android.p008os;

import android.content.IntentSender;
import android.p008os.IRecoverySystemProgressListener;
/* renamed from: android.os.IRecoverySystem */
/* loaded from: classes3.dex */
public interface IRecoverySystem extends IInterface {
    boolean allocateSpaceForUpdate(String str) throws RemoteException;

    boolean clearBcb() throws RemoteException;

    boolean clearLskf(String str) throws RemoteException;

    boolean isLskfCaptured(String str) throws RemoteException;

    void rebootRecoveryWithCommand(String str) throws RemoteException;

    int rebootWithLskf(String str, String str2, boolean z) throws RemoteException;

    int rebootWithLskfAssumeSlotSwitch(String str, String str2) throws RemoteException;

    boolean requestLskf(String str, IntentSender intentSender) throws RemoteException;

    boolean setupBcb(String str) throws RemoteException;

    boolean uncrypt(String str, IRecoverySystemProgressListener iRecoverySystemProgressListener) throws RemoteException;

    /* renamed from: android.os.IRecoverySystem$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IRecoverySystem {
        @Override // android.p008os.IRecoverySystem
        public boolean allocateSpaceForUpdate(String packageFilePath) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IRecoverySystem
        public boolean uncrypt(String packageFile, IRecoverySystemProgressListener listener) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IRecoverySystem
        public boolean setupBcb(String command) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IRecoverySystem
        public boolean clearBcb() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IRecoverySystem
        public void rebootRecoveryWithCommand(String command) throws RemoteException {
        }

        @Override // android.p008os.IRecoverySystem
        public boolean requestLskf(String packageName, IntentSender sender) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IRecoverySystem
        public boolean clearLskf(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IRecoverySystem
        public boolean isLskfCaptured(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IRecoverySystem
        public int rebootWithLskfAssumeSlotSwitch(String packageName, String reason) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IRecoverySystem
        public int rebootWithLskf(String packageName, String reason, boolean slotSwitch) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IRecoverySystem$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IRecoverySystem {
        public static final String DESCRIPTOR = "android.os.IRecoverySystem";
        static final int TRANSACTION_allocateSpaceForUpdate = 1;
        static final int TRANSACTION_clearBcb = 4;
        static final int TRANSACTION_clearLskf = 7;
        static final int TRANSACTION_isLskfCaptured = 8;
        static final int TRANSACTION_rebootRecoveryWithCommand = 5;
        static final int TRANSACTION_rebootWithLskf = 10;
        static final int TRANSACTION_rebootWithLskfAssumeSlotSwitch = 9;
        static final int TRANSACTION_requestLskf = 6;
        static final int TRANSACTION_setupBcb = 3;
        static final int TRANSACTION_uncrypt = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRecoverySystem asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRecoverySystem)) {
                return (IRecoverySystem) iin;
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
                    return "allocateSpaceForUpdate";
                case 2:
                    return "uncrypt";
                case 3:
                    return "setupBcb";
                case 4:
                    return "clearBcb";
                case 5:
                    return "rebootRecoveryWithCommand";
                case 6:
                    return "requestLskf";
                case 7:
                    return "clearLskf";
                case 8:
                    return "isLskfCaptured";
                case 9:
                    return "rebootWithLskfAssumeSlotSwitch";
                case 10:
                    return "rebootWithLskf";
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
                            data.enforceNoDataAvail();
                            boolean _result = allocateSpaceForUpdate(_arg0);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            IRecoverySystemProgressListener _arg1 = IRecoverySystemProgressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result2 = uncrypt(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result3 = setupBcb(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            boolean _result4 = clearBcb();
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 5:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            rebootRecoveryWithCommand(_arg04);
                            reply.writeNoException();
                            break;
                        case 6:
                            String _arg05 = data.readString();
                            IntentSender _arg12 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result5 = requestLskf(_arg05, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 7:
                            String _arg06 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result6 = clearLskf(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 8:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result7 = isLskfCaptured(_arg07);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 9:
                            String _arg08 = data.readString();
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            int _result8 = rebootWithLskfAssumeSlotSwitch(_arg08, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 10:
                            String _arg09 = data.readString();
                            String _arg14 = data.readString();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result9 = rebootWithLskf(_arg09, _arg14, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IRecoverySystem$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IRecoverySystem {
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

            @Override // android.p008os.IRecoverySystem
            public boolean allocateSpaceForUpdate(String packageFilePath) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageFilePath);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IRecoverySystem
            public boolean uncrypt(String packageFile, IRecoverySystemProgressListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageFile);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IRecoverySystem
            public boolean setupBcb(String command) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(command);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IRecoverySystem
            public boolean clearBcb() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IRecoverySystem
            public void rebootRecoveryWithCommand(String command) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(command);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IRecoverySystem
            public boolean requestLskf(String packageName, IntentSender sender) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(sender, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IRecoverySystem
            public boolean clearLskf(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IRecoverySystem
            public boolean isLskfCaptured(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IRecoverySystem
            public int rebootWithLskfAssumeSlotSwitch(String packageName, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(reason);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IRecoverySystem
            public int rebootWithLskf(String packageName, String reason, boolean slotSwitch) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(reason);
                    _data.writeBoolean(slotSwitch);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
