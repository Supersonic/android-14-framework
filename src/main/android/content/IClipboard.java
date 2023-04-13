package android.content;

import android.content.IOnPrimaryClipChangedListener;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IClipboard extends IInterface {
    void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener iOnPrimaryClipChangedListener, String str, String str2, int i, int i2) throws RemoteException;

    boolean areClipboardAccessNotificationsEnabledForUser(int i) throws RemoteException;

    void clearPrimaryClip(String str, String str2, int i, int i2) throws RemoteException;

    ClipData getPrimaryClip(String str, String str2, int i, int i2) throws RemoteException;

    ClipDescription getPrimaryClipDescription(String str, String str2, int i, int i2) throws RemoteException;

    String getPrimaryClipSource(String str, String str2, int i, int i2) throws RemoteException;

    boolean hasClipboardText(String str, String str2, int i, int i2) throws RemoteException;

    boolean hasPrimaryClip(String str, String str2, int i, int i2) throws RemoteException;

    void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener iOnPrimaryClipChangedListener, String str, String str2, int i, int i2) throws RemoteException;

    void setClipboardAccessNotificationsEnabledForUser(boolean z, int i) throws RemoteException;

    void setPrimaryClip(ClipData clipData, String str, String str2, int i, int i2) throws RemoteException;

    void setPrimaryClipAsPackage(ClipData clipData, String str, String str2, int i, int i2, String str3) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IClipboard {
        @Override // android.content.IClipboard
        public void setPrimaryClip(ClipData clip, String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
        }

        @Override // android.content.IClipboard
        public void setPrimaryClipAsPackage(ClipData clip, String callingPackage, String attributionTag, int userId, int deviceId, String sourcePackage) throws RemoteException {
        }

        @Override // android.content.IClipboard
        public void clearPrimaryClip(String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
        }

        @Override // android.content.IClipboard
        public ClipData getPrimaryClip(String pkg, String attributionTag, int userId, int deviceId) throws RemoteException {
            return null;
        }

        @Override // android.content.IClipboard
        public ClipDescription getPrimaryClipDescription(String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
            return null;
        }

        @Override // android.content.IClipboard
        public boolean hasPrimaryClip(String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
            return false;
        }

        @Override // android.content.IClipboard
        public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
        }

        @Override // android.content.IClipboard
        public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
        }

        @Override // android.content.IClipboard
        public boolean hasClipboardText(String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
            return false;
        }

        @Override // android.content.IClipboard
        public String getPrimaryClipSource(String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
            return null;
        }

        @Override // android.content.IClipboard
        public boolean areClipboardAccessNotificationsEnabledForUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.IClipboard
        public void setClipboardAccessNotificationsEnabledForUser(boolean enable, int userId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IClipboard {
        public static final String DESCRIPTOR = "android.content.IClipboard";
        static final int TRANSACTION_addPrimaryClipChangedListener = 7;
        static final int TRANSACTION_areClipboardAccessNotificationsEnabledForUser = 11;
        static final int TRANSACTION_clearPrimaryClip = 3;
        static final int TRANSACTION_getPrimaryClip = 4;
        static final int TRANSACTION_getPrimaryClipDescription = 5;
        static final int TRANSACTION_getPrimaryClipSource = 10;
        static final int TRANSACTION_hasClipboardText = 9;
        static final int TRANSACTION_hasPrimaryClip = 6;
        static final int TRANSACTION_removePrimaryClipChangedListener = 8;
        static final int TRANSACTION_setClipboardAccessNotificationsEnabledForUser = 12;
        static final int TRANSACTION_setPrimaryClip = 1;
        static final int TRANSACTION_setPrimaryClipAsPackage = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IClipboard asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IClipboard)) {
                return (IClipboard) iin;
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
                    return "setPrimaryClip";
                case 2:
                    return "setPrimaryClipAsPackage";
                case 3:
                    return "clearPrimaryClip";
                case 4:
                    return "getPrimaryClip";
                case 5:
                    return "getPrimaryClipDescription";
                case 6:
                    return "hasPrimaryClip";
                case 7:
                    return "addPrimaryClipChangedListener";
                case 8:
                    return "removePrimaryClipChangedListener";
                case 9:
                    return "hasClipboardText";
                case 10:
                    return "getPrimaryClipSource";
                case 11:
                    return "areClipboardAccessNotificationsEnabledForUser";
                case 12:
                    return "setClipboardAccessNotificationsEnabledForUser";
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
                            ClipData _arg0 = (ClipData) data.readTypedObject(ClipData.CREATOR);
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            setPrimaryClip(_arg0, _arg1, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            break;
                        case 2:
                            ClipData _arg02 = (ClipData) data.readTypedObject(ClipData.CREATOR);
                            String _arg12 = data.readString();
                            String _arg22 = data.readString();
                            int _arg32 = data.readInt();
                            int _arg42 = data.readInt();
                            String _arg5 = data.readString();
                            data.enforceNoDataAvail();
                            setPrimaryClipAsPackage(_arg02, _arg12, _arg22, _arg32, _arg42, _arg5);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg13 = data.readString();
                            int _arg23 = data.readInt();
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            clearPrimaryClip(_arg03, _arg13, _arg23, _arg33);
                            reply.writeNoException();
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            String _arg14 = data.readString();
                            int _arg24 = data.readInt();
                            int _arg34 = data.readInt();
                            data.enforceNoDataAvail();
                            ClipData _result = getPrimaryClip(_arg04, _arg14, _arg24, _arg34);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            String _arg15 = data.readString();
                            int _arg25 = data.readInt();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            ClipDescription _result2 = getPrimaryClipDescription(_arg05, _arg15, _arg25, _arg35);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            String _arg16 = data.readString();
                            int _arg26 = data.readInt();
                            int _arg36 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result3 = hasPrimaryClip(_arg06, _arg16, _arg26, _arg36);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 7:
                            IOnPrimaryClipChangedListener _arg07 = IOnPrimaryClipChangedListener.Stub.asInterface(data.readStrongBinder());
                            String _arg17 = data.readString();
                            String _arg27 = data.readString();
                            int _arg37 = data.readInt();
                            int _arg43 = data.readInt();
                            data.enforceNoDataAvail();
                            addPrimaryClipChangedListener(_arg07, _arg17, _arg27, _arg37, _arg43);
                            reply.writeNoException();
                            break;
                        case 8:
                            IOnPrimaryClipChangedListener _arg08 = IOnPrimaryClipChangedListener.Stub.asInterface(data.readStrongBinder());
                            String _arg18 = data.readString();
                            String _arg28 = data.readString();
                            int _arg38 = data.readInt();
                            int _arg44 = data.readInt();
                            data.enforceNoDataAvail();
                            removePrimaryClipChangedListener(_arg08, _arg18, _arg28, _arg38, _arg44);
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            String _arg19 = data.readString();
                            int _arg29 = data.readInt();
                            int _arg39 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = hasClipboardText(_arg09, _arg19, _arg29, _arg39);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            String _arg110 = data.readString();
                            int _arg210 = data.readInt();
                            int _arg310 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result5 = getPrimaryClipSource(_arg010, _arg110, _arg210, _arg310);
                            reply.writeNoException();
                            reply.writeString(_result5);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result6 = areClipboardAccessNotificationsEnabledForUser(_arg011);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 12:
                            boolean _arg012 = data.readBoolean();
                            int _arg111 = data.readInt();
                            data.enforceNoDataAvail();
                            setClipboardAccessNotificationsEnabledForUser(_arg012, _arg111);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IClipboard {
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

            @Override // android.content.IClipboard
            public void setPrimaryClip(ClipData clip, String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(clip, 0);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    _data.writeInt(userId);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public void setPrimaryClipAsPackage(ClipData clip, String callingPackage, String attributionTag, int userId, int deviceId, String sourcePackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(clip, 0);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    _data.writeInt(userId);
                    _data.writeInt(deviceId);
                    _data.writeString(sourcePackage);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public void clearPrimaryClip(String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    _data.writeInt(userId);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public ClipData getPrimaryClip(String pkg, String attributionTag, int userId, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(attributionTag);
                    _data.writeInt(userId);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    ClipData _result = (ClipData) _reply.readTypedObject(ClipData.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public ClipDescription getPrimaryClipDescription(String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    _data.writeInt(userId);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    ClipDescription _result = (ClipDescription) _reply.readTypedObject(ClipDescription.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public boolean hasPrimaryClip(String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    _data.writeInt(userId);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    _data.writeInt(userId);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    _data.writeInt(userId);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public boolean hasClipboardText(String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    _data.writeInt(userId);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public String getPrimaryClipSource(String callingPackage, String attributionTag, int userId, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    _data.writeInt(userId);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public boolean areClipboardAccessNotificationsEnabledForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IClipboard
            public void setClipboardAccessNotificationsEnabledForUser(boolean enable, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 11;
        }
    }
}
