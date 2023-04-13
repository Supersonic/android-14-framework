package android.nfc;

import android.app.PendingIntent;
import android.content.IntentFilter;
import android.nfc.IAppCallback;
import android.nfc.INfcAdapterExtras;
import android.nfc.INfcCardEmulation;
import android.nfc.INfcControllerAlwaysOnListener;
import android.nfc.INfcDta;
import android.nfc.INfcFCardEmulation;
import android.nfc.INfcTag;
import android.nfc.INfcUnlockHandler;
import android.nfc.ITagRemovedCallback;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.Map;
/* loaded from: classes2.dex */
public interface INfcAdapter extends IInterface {
    void addNfcUnlockHandler(INfcUnlockHandler iNfcUnlockHandler, int[] iArr) throws RemoteException;

    boolean deviceSupportsNfcSecure() throws RemoteException;

    boolean disable(boolean z) throws RemoteException;

    void dispatch(Tag tag) throws RemoteException;

    boolean enable() throws RemoteException;

    INfcAdapterExtras getNfcAdapterExtrasInterface(String str) throws RemoteException;

    NfcAntennaInfo getNfcAntennaInfo() throws RemoteException;

    INfcCardEmulation getNfcCardEmulationInterface() throws RemoteException;

    INfcDta getNfcDtaInterface(String str) throws RemoteException;

    INfcFCardEmulation getNfcFCardEmulationInterface() throws RemoteException;

    INfcTag getNfcTagInterface() throws RemoteException;

    int getState() throws RemoteException;

    Map getTagIntentAppPreferenceForUser(int i) throws RemoteException;

    boolean ignore(int i, int i2, ITagRemovedCallback iTagRemovedCallback) throws RemoteException;

    boolean isControllerAlwaysOn() throws RemoteException;

    boolean isControllerAlwaysOnSupported() throws RemoteException;

    boolean isNfcSecureEnabled() throws RemoteException;

    boolean isTagIntentAppPreferenceSupported() throws RemoteException;

    void pausePolling(int i) throws RemoteException;

    void registerControllerAlwaysOnListener(INfcControllerAlwaysOnListener iNfcControllerAlwaysOnListener) throws RemoteException;

    void removeNfcUnlockHandler(INfcUnlockHandler iNfcUnlockHandler) throws RemoteException;

    void resumePolling() throws RemoteException;

    void setAppCallback(IAppCallback iAppCallback) throws RemoteException;

    boolean setControllerAlwaysOn(boolean z) throws RemoteException;

    void setForegroundDispatch(PendingIntent pendingIntent, IntentFilter[] intentFilterArr, TechListParcel techListParcel) throws RemoteException;

    boolean setNfcSecure(boolean z) throws RemoteException;

    void setReaderMode(IBinder iBinder, IAppCallback iAppCallback, int i, Bundle bundle) throws RemoteException;

    int setTagIntentAppPreferenceForUser(int i, String str, boolean z) throws RemoteException;

    void unregisterControllerAlwaysOnListener(INfcControllerAlwaysOnListener iNfcControllerAlwaysOnListener) throws RemoteException;

    void verifyNfcPermission() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements INfcAdapter {
        @Override // android.nfc.INfcAdapter
        public INfcTag getNfcTagInterface() throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcAdapter
        public INfcCardEmulation getNfcCardEmulationInterface() throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcAdapter
        public INfcFCardEmulation getNfcFCardEmulationInterface() throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcAdapter
        public INfcAdapterExtras getNfcAdapterExtrasInterface(String pkg) throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcAdapter
        public INfcDta getNfcDtaInterface(String pkg) throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcAdapter
        public int getState() throws RemoteException {
            return 0;
        }

        @Override // android.nfc.INfcAdapter
        public boolean disable(boolean saveState) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcAdapter
        public boolean enable() throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcAdapter
        public void pausePolling(int timeoutInMs) throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public void resumePolling() throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public void setForegroundDispatch(PendingIntent intent, IntentFilter[] filters, TechListParcel techLists) throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public void setAppCallback(IAppCallback callback) throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public boolean ignore(int nativeHandle, int debounceMs, ITagRemovedCallback callback) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcAdapter
        public void dispatch(Tag tag) throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public void setReaderMode(IBinder b, IAppCallback callback, int flags, Bundle extras) throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public void addNfcUnlockHandler(INfcUnlockHandler unlockHandler, int[] techList) throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public void removeNfcUnlockHandler(INfcUnlockHandler unlockHandler) throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public void verifyNfcPermission() throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public boolean isNfcSecureEnabled() throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcAdapter
        public boolean deviceSupportsNfcSecure() throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcAdapter
        public boolean setNfcSecure(boolean enable) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcAdapter
        public NfcAntennaInfo getNfcAntennaInfo() throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcAdapter
        public boolean setControllerAlwaysOn(boolean value) throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcAdapter
        public boolean isControllerAlwaysOn() throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcAdapter
        public boolean isControllerAlwaysOnSupported() throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcAdapter
        public void registerControllerAlwaysOnListener(INfcControllerAlwaysOnListener listener) throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public void unregisterControllerAlwaysOnListener(INfcControllerAlwaysOnListener listener) throws RemoteException {
        }

        @Override // android.nfc.INfcAdapter
        public boolean isTagIntentAppPreferenceSupported() throws RemoteException {
            return false;
        }

        @Override // android.nfc.INfcAdapter
        public Map getTagIntentAppPreferenceForUser(int userId) throws RemoteException {
            return null;
        }

        @Override // android.nfc.INfcAdapter
        public int setTagIntentAppPreferenceForUser(int userId, String pkg, boolean allow) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements INfcAdapter {
        public static final String DESCRIPTOR = "android.nfc.INfcAdapter";
        static final int TRANSACTION_addNfcUnlockHandler = 16;
        static final int TRANSACTION_deviceSupportsNfcSecure = 20;
        static final int TRANSACTION_disable = 7;
        static final int TRANSACTION_dispatch = 14;
        static final int TRANSACTION_enable = 8;
        static final int TRANSACTION_getNfcAdapterExtrasInterface = 4;
        static final int TRANSACTION_getNfcAntennaInfo = 22;
        static final int TRANSACTION_getNfcCardEmulationInterface = 2;
        static final int TRANSACTION_getNfcDtaInterface = 5;
        static final int TRANSACTION_getNfcFCardEmulationInterface = 3;
        static final int TRANSACTION_getNfcTagInterface = 1;
        static final int TRANSACTION_getState = 6;
        static final int TRANSACTION_getTagIntentAppPreferenceForUser = 29;
        static final int TRANSACTION_ignore = 13;
        static final int TRANSACTION_isControllerAlwaysOn = 24;
        static final int TRANSACTION_isControllerAlwaysOnSupported = 25;
        static final int TRANSACTION_isNfcSecureEnabled = 19;
        static final int TRANSACTION_isTagIntentAppPreferenceSupported = 28;
        static final int TRANSACTION_pausePolling = 9;
        static final int TRANSACTION_registerControllerAlwaysOnListener = 26;
        static final int TRANSACTION_removeNfcUnlockHandler = 17;
        static final int TRANSACTION_resumePolling = 10;
        static final int TRANSACTION_setAppCallback = 12;
        static final int TRANSACTION_setControllerAlwaysOn = 23;
        static final int TRANSACTION_setForegroundDispatch = 11;
        static final int TRANSACTION_setNfcSecure = 21;
        static final int TRANSACTION_setReaderMode = 15;
        static final int TRANSACTION_setTagIntentAppPreferenceForUser = 30;
        static final int TRANSACTION_unregisterControllerAlwaysOnListener = 27;
        static final int TRANSACTION_verifyNfcPermission = 18;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INfcAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INfcAdapter)) {
                return (INfcAdapter) iin;
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
                    return "getNfcTagInterface";
                case 2:
                    return "getNfcCardEmulationInterface";
                case 3:
                    return "getNfcFCardEmulationInterface";
                case 4:
                    return "getNfcAdapterExtrasInterface";
                case 5:
                    return "getNfcDtaInterface";
                case 6:
                    return "getState";
                case 7:
                    return "disable";
                case 8:
                    return "enable";
                case 9:
                    return "pausePolling";
                case 10:
                    return "resumePolling";
                case 11:
                    return "setForegroundDispatch";
                case 12:
                    return "setAppCallback";
                case 13:
                    return "ignore";
                case 14:
                    return "dispatch";
                case 15:
                    return "setReaderMode";
                case 16:
                    return "addNfcUnlockHandler";
                case 17:
                    return "removeNfcUnlockHandler";
                case 18:
                    return "verifyNfcPermission";
                case 19:
                    return "isNfcSecureEnabled";
                case 20:
                    return "deviceSupportsNfcSecure";
                case 21:
                    return "setNfcSecure";
                case 22:
                    return "getNfcAntennaInfo";
                case 23:
                    return "setControllerAlwaysOn";
                case 24:
                    return "isControllerAlwaysOn";
                case 25:
                    return "isControllerAlwaysOnSupported";
                case 26:
                    return "registerControllerAlwaysOnListener";
                case 27:
                    return "unregisterControllerAlwaysOnListener";
                case 28:
                    return "isTagIntentAppPreferenceSupported";
                case 29:
                    return "getTagIntentAppPreferenceForUser";
                case 30:
                    return "setTagIntentAppPreferenceForUser";
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
                            INfcTag _result = getNfcTagInterface();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            INfcCardEmulation _result2 = getNfcCardEmulationInterface();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            INfcFCardEmulation _result3 = getNfcFCardEmulationInterface();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 4:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            INfcAdapterExtras _result4 = getNfcAdapterExtrasInterface(_arg0);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result4);
                            break;
                        case 5:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            INfcDta _result5 = getNfcDtaInterface(_arg02);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result5);
                            break;
                        case 6:
                            int _result6 = getState();
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 7:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result7 = disable(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 8:
                            boolean _result8 = enable();
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 9:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            pausePolling(_arg04);
                            reply.writeNoException();
                            break;
                        case 10:
                            resumePolling();
                            reply.writeNoException();
                            break;
                        case 11:
                            PendingIntent _arg05 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            IntentFilter[] _arg1 = (IntentFilter[]) data.createTypedArray(IntentFilter.CREATOR);
                            TechListParcel _arg2 = (TechListParcel) data.readTypedObject(TechListParcel.CREATOR);
                            data.enforceNoDataAvail();
                            setForegroundDispatch(_arg05, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 12:
                            IAppCallback _arg06 = IAppCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setAppCallback(_arg06);
                            reply.writeNoException();
                            break;
                        case 13:
                            int _arg07 = data.readInt();
                            int _arg12 = data.readInt();
                            ITagRemovedCallback _arg22 = ITagRemovedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result9 = ignore(_arg07, _arg12, _arg22);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 14:
                            Tag _arg08 = (Tag) data.readTypedObject(Tag.CREATOR);
                            data.enforceNoDataAvail();
                            dispatch(_arg08);
                            reply.writeNoException();
                            break;
                        case 15:
                            IBinder _arg09 = data.readStrongBinder();
                            IAppCallback _arg13 = IAppCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg23 = data.readInt();
                            Bundle _arg3 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            setReaderMode(_arg09, _arg13, _arg23, _arg3);
                            reply.writeNoException();
                            break;
                        case 16:
                            INfcUnlockHandler _arg010 = INfcUnlockHandler.Stub.asInterface(data.readStrongBinder());
                            int[] _arg14 = data.createIntArray();
                            data.enforceNoDataAvail();
                            addNfcUnlockHandler(_arg010, _arg14);
                            reply.writeNoException();
                            break;
                        case 17:
                            INfcUnlockHandler _arg011 = INfcUnlockHandler.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeNfcUnlockHandler(_arg011);
                            reply.writeNoException();
                            break;
                        case 18:
                            verifyNfcPermission();
                            reply.writeNoException();
                            break;
                        case 19:
                            boolean _result10 = isNfcSecureEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 20:
                            boolean _result11 = deviceSupportsNfcSecure();
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 21:
                            boolean _arg012 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result12 = setNfcSecure(_arg012);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        case 22:
                            NfcAntennaInfo _result13 = getNfcAntennaInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result13, 1);
                            break;
                        case 23:
                            boolean _arg013 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result14 = setControllerAlwaysOn(_arg013);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            break;
                        case 24:
                            boolean _result15 = isControllerAlwaysOn();
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 25:
                            boolean _result16 = isControllerAlwaysOnSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result16);
                            break;
                        case 26:
                            INfcControllerAlwaysOnListener _arg014 = INfcControllerAlwaysOnListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerControllerAlwaysOnListener(_arg014);
                            reply.writeNoException();
                            break;
                        case 27:
                            INfcControllerAlwaysOnListener _arg015 = INfcControllerAlwaysOnListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterControllerAlwaysOnListener(_arg015);
                            reply.writeNoException();
                            break;
                        case 28:
                            boolean _result17 = isTagIntentAppPreferenceSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            break;
                        case 29:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            Map _result18 = getTagIntentAppPreferenceForUser(_arg016);
                            reply.writeNoException();
                            reply.writeMap(_result18);
                            break;
                        case 30:
                            int _arg017 = data.readInt();
                            String _arg15 = data.readString();
                            boolean _arg24 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result19 = setTagIntentAppPreferenceForUser(_arg017, _arg15, _arg24);
                            reply.writeNoException();
                            reply.writeInt(_result19);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements INfcAdapter {
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

            @Override // android.nfc.INfcAdapter
            public INfcTag getNfcTagInterface() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    INfcTag _result = INfcTag.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public INfcCardEmulation getNfcCardEmulationInterface() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    INfcCardEmulation _result = INfcCardEmulation.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public INfcFCardEmulation getNfcFCardEmulationInterface() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    INfcFCardEmulation _result = INfcFCardEmulation.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public INfcAdapterExtras getNfcAdapterExtrasInterface(String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    INfcAdapterExtras _result = INfcAdapterExtras.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public INfcDta getNfcDtaInterface(String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    INfcDta _result = INfcDta.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public int getState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public boolean disable(boolean saveState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(saveState);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public boolean enable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public void pausePolling(int timeoutInMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(timeoutInMs);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public void resumePolling() throws RemoteException {
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

            @Override // android.nfc.INfcAdapter
            public void setForegroundDispatch(PendingIntent intent, IntentFilter[] filters, TechListParcel techLists) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeTypedArray(filters, 0);
                    _data.writeTypedObject(techLists, 0);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public void setAppCallback(IAppCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public boolean ignore(int nativeHandle, int debounceMs, ITagRemovedCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nativeHandle);
                    _data.writeInt(debounceMs);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public void dispatch(Tag tag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(tag, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public void setReaderMode(IBinder b, IAppCallback callback, int flags, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(b);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(flags);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public void addNfcUnlockHandler(INfcUnlockHandler unlockHandler, int[] techList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(unlockHandler);
                    _data.writeIntArray(techList);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public void removeNfcUnlockHandler(INfcUnlockHandler unlockHandler) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(unlockHandler);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public void verifyNfcPermission() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public boolean isNfcSecureEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public boolean deviceSupportsNfcSecure() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public boolean setNfcSecure(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public NfcAntennaInfo getNfcAntennaInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    NfcAntennaInfo _result = (NfcAntennaInfo) _reply.readTypedObject(NfcAntennaInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public boolean setControllerAlwaysOn(boolean value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(value);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public boolean isControllerAlwaysOn() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public boolean isControllerAlwaysOnSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public void registerControllerAlwaysOnListener(INfcControllerAlwaysOnListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public void unregisterControllerAlwaysOnListener(INfcControllerAlwaysOnListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public boolean isTagIntentAppPreferenceSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public Map getTagIntentAppPreferenceForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    Map _result = _reply.readHashMap(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.nfc.INfcAdapter
            public int setTagIntentAppPreferenceForUser(int userId, String pkg, boolean allow) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(pkg);
                    _data.writeBoolean(allow);
                    this.mRemote.transact(30, _data, _reply, 0);
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
            return 29;
        }
    }
}
