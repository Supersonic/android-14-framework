package android.nfc;

import android.content.ComponentName;
import android.nfc.cardemulation.NfcFServiceInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface INfcFCardEmulation extends IInterface {
    boolean disableNfcFForegroundService() throws RemoteException;

    boolean enableNfcFForegroundService(ComponentName componentName) throws RemoteException;

    int getMaxNumOfRegisterableSystemCodes() throws RemoteException;

    List<NfcFServiceInfo> getNfcFServices(int i) throws RemoteException;

    String getNfcid2ForService(int i, ComponentName componentName) throws RemoteException;

    String getSystemCodeForService(int i, ComponentName componentName) throws RemoteException;

    boolean registerSystemCodeForService(int i, ComponentName componentName, String str) throws RemoteException;

    boolean removeSystemCodeForService(int i, ComponentName componentName) throws RemoteException;

    boolean setNfcid2ForService(int i, ComponentName componentName, String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements INfcFCardEmulation {
        @Override // android.nfc.INfcFCardEmulation
        public String getSystemCodeForService(int userHandle, ComponentName service) throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcFCardEmulation
        public boolean registerSystemCodeForService(int userHandle, ComponentName service, String systemCode) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcFCardEmulation
        public boolean removeSystemCodeForService(int userHandle, ComponentName service) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcFCardEmulation
        public String getNfcid2ForService(int userHandle, ComponentName service) throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcFCardEmulation
        public boolean setNfcid2ForService(int userHandle, ComponentName service, String nfcid2) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcFCardEmulation
        public boolean enableNfcFForegroundService(ComponentName service) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcFCardEmulation
        public boolean disableNfcFForegroundService() throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcFCardEmulation
        public List<NfcFServiceInfo> getNfcFServices(int userHandle) throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcFCardEmulation
        public int getMaxNumOfRegisterableSystemCodes() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements INfcFCardEmulation {
        public static final String DESCRIPTOR = "android.nfc.INfcFCardEmulation";
        static final int TRANSACTION_disableNfcFForegroundService = 7;
        static final int TRANSACTION_enableNfcFForegroundService = 6;
        static final int TRANSACTION_getMaxNumOfRegisterableSystemCodes = 9;
        static final int TRANSACTION_getNfcFServices = 8;
        static final int TRANSACTION_getNfcid2ForService = 4;
        static final int TRANSACTION_getSystemCodeForService = 1;
        static final int TRANSACTION_registerSystemCodeForService = 2;
        static final int TRANSACTION_removeSystemCodeForService = 3;
        static final int TRANSACTION_setNfcid2ForService = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INfcFCardEmulation asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INfcFCardEmulation)) {
                return (INfcFCardEmulation) iin;
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
                    return "getSystemCodeForService";
                case 2:
                    return "registerSystemCodeForService";
                case 3:
                    return "removeSystemCodeForService";
                case 4:
                    return "getNfcid2ForService";
                case 5:
                    return "setNfcid2ForService";
                case 6:
                    return "enableNfcFForegroundService";
                case 7:
                    return "disableNfcFForegroundService";
                case 8:
                    return "getNfcFServices";
                case 9:
                    return "getMaxNumOfRegisterableSystemCodes";
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
                            ComponentName _arg1 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            String _result = getSystemCodeForService(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeString(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            ComponentName _arg12 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result2 = registerSystemCodeForService(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            ComponentName _arg13 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result3 = removeSystemCodeForService(_arg03, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            ComponentName _arg14 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            String _result4 = getNfcid2ForService(_arg04, _arg14);
                            reply.writeNoException();
                            reply.writeString(_result4);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            ComponentName _arg15 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result5 = setNfcid2ForService(_arg05, _arg15, _arg22);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 6:
                            ComponentName _arg06 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result6 = enableNfcFForegroundService(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 7:
                            boolean _result7 = disableNfcFForegroundService();
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 8:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            List<NfcFServiceInfo> _result8 = getNfcFServices(_arg07);
                            reply.writeNoException();
                            reply.writeTypedList(_result8, 1);
                            break;
                        case 9:
                            int _result9 = getMaxNumOfRegisterableSystemCodes();
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements INfcFCardEmulation {
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

            @Override // android.nfc.INfcFCardEmulation
            public String getSystemCodeForService(int userHandle, ComponentName service) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeTypedObject(service, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcFCardEmulation
            public boolean registerSystemCodeForService(int userHandle, ComponentName service, String systemCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeTypedObject(service, 0);
                    _data.writeString(systemCode);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcFCardEmulation
            public boolean removeSystemCodeForService(int userHandle, ComponentName service) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeTypedObject(service, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcFCardEmulation
            public String getNfcid2ForService(int userHandle, ComponentName service) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeTypedObject(service, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcFCardEmulation
            public boolean setNfcid2ForService(int userHandle, ComponentName service, String nfcid2) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeTypedObject(service, 0);
                    _data.writeString(nfcid2);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcFCardEmulation
            public boolean enableNfcFForegroundService(ComponentName service) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(service, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcFCardEmulation
            public boolean disableNfcFForegroundService() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcFCardEmulation
            public List<NfcFServiceInfo> getNfcFServices(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    List<NfcFServiceInfo> _result = _reply.createTypedArrayList(NfcFServiceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcFCardEmulation
            public int getMaxNumOfRegisterableSystemCodes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
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
            return 8;
        }
    }
}
