package com.android.ims.internal.uce.uceservice;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.ims.internal.uce.common.UceLong;
import com.android.ims.internal.uce.options.IOptionsListener;
import com.android.ims.internal.uce.options.IOptionsService;
import com.android.ims.internal.uce.presence.IPresenceListener;
import com.android.ims.internal.uce.presence.IPresenceService;
import com.android.ims.internal.uce.uceservice.IUceListener;
/* loaded from: classes4.dex */
public interface IUceService extends IInterface {
    @Deprecated
    int createOptionsService(IOptionsListener iOptionsListener, UceLong uceLong) throws RemoteException;

    int createOptionsServiceForSubscription(IOptionsListener iOptionsListener, UceLong uceLong, String str) throws RemoteException;

    @Deprecated
    int createPresenceService(IPresenceListener iPresenceListener, UceLong uceLong) throws RemoteException;

    int createPresenceServiceForSubscription(IPresenceListener iPresenceListener, UceLong uceLong, String str) throws RemoteException;

    void destroyOptionsService(int i) throws RemoteException;

    void destroyPresenceService(int i) throws RemoteException;

    @Deprecated
    IOptionsService getOptionsService() throws RemoteException;

    IOptionsService getOptionsServiceForSubscription(String str) throws RemoteException;

    @Deprecated
    IPresenceService getPresenceService() throws RemoteException;

    IPresenceService getPresenceServiceForSubscription(String str) throws RemoteException;

    boolean getServiceStatus() throws RemoteException;

    boolean isServiceStarted() throws RemoteException;

    boolean startService(IUceListener iUceListener) throws RemoteException;

    boolean stopService() throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IUceService {
        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public boolean startService(IUceListener uceListener) throws RemoteException {
            return false;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public boolean stopService() throws RemoteException {
            return false;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public boolean isServiceStarted() throws RemoteException {
            return false;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public int createOptionsService(IOptionsListener optionsListener, UceLong optionsServiceListenerHdl) throws RemoteException {
            return 0;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public int createOptionsServiceForSubscription(IOptionsListener optionsListener, UceLong optionsServiceListenerHdl, String iccId) throws RemoteException {
            return 0;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public void destroyOptionsService(int optionsServiceHandle) throws RemoteException {
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public int createPresenceService(IPresenceListener presenceServiceListener, UceLong presenceServiceListenerHdl) throws RemoteException {
            return 0;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public int createPresenceServiceForSubscription(IPresenceListener presenceServiceListener, UceLong presenceServiceListenerHdl, String iccId) throws RemoteException {
            return 0;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public void destroyPresenceService(int presenceServiceHdl) throws RemoteException {
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public boolean getServiceStatus() throws RemoteException {
            return false;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public IPresenceService getPresenceService() throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public IPresenceService getPresenceServiceForSubscription(String iccId) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public IOptionsService getOptionsService() throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public IOptionsService getOptionsServiceForSubscription(String iccId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IUceService {
        public static final String DESCRIPTOR = "com.android.ims.internal.uce.uceservice.IUceService";
        static final int TRANSACTION_createOptionsService = 4;
        static final int TRANSACTION_createOptionsServiceForSubscription = 5;
        static final int TRANSACTION_createPresenceService = 7;
        static final int TRANSACTION_createPresenceServiceForSubscription = 8;
        static final int TRANSACTION_destroyOptionsService = 6;
        static final int TRANSACTION_destroyPresenceService = 9;
        static final int TRANSACTION_getOptionsService = 13;
        static final int TRANSACTION_getOptionsServiceForSubscription = 14;
        static final int TRANSACTION_getPresenceService = 11;
        static final int TRANSACTION_getPresenceServiceForSubscription = 12;
        static final int TRANSACTION_getServiceStatus = 10;
        static final int TRANSACTION_isServiceStarted = 3;
        static final int TRANSACTION_startService = 1;
        static final int TRANSACTION_stopService = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUceService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IUceService)) {
                return (IUceService) iin;
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
                    return "startService";
                case 2:
                    return "stopService";
                case 3:
                    return "isServiceStarted";
                case 4:
                    return "createOptionsService";
                case 5:
                    return "createOptionsServiceForSubscription";
                case 6:
                    return "destroyOptionsService";
                case 7:
                    return "createPresenceService";
                case 8:
                    return "createPresenceServiceForSubscription";
                case 9:
                    return "destroyPresenceService";
                case 10:
                    return "getServiceStatus";
                case 11:
                    return "getPresenceService";
                case 12:
                    return "getPresenceServiceForSubscription";
                case 13:
                    return "getOptionsService";
                case 14:
                    return "getOptionsServiceForSubscription";
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
                            IUceListener _arg0 = IUceListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result = startService(_arg0);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            boolean _result2 = stopService();
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 3:
                            boolean _result3 = isServiceStarted();
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            IOptionsListener _arg02 = IOptionsListener.Stub.asInterface(data.readStrongBinder());
                            UceLong _arg1 = (UceLong) data.readTypedObject(UceLong.CREATOR);
                            data.enforceNoDataAvail();
                            int _result4 = createOptionsService(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            reply.writeTypedObject(_arg1, 1);
                            break;
                        case 5:
                            IOptionsListener _arg03 = IOptionsListener.Stub.asInterface(data.readStrongBinder());
                            UceLong _arg12 = (UceLong) data.readTypedObject(UceLong.CREATOR);
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            int _result5 = createOptionsServiceForSubscription(_arg03, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            reply.writeTypedObject(_arg12, 1);
                            break;
                        case 6:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            destroyOptionsService(_arg04);
                            reply.writeNoException();
                            break;
                        case 7:
                            IPresenceListener _arg05 = IPresenceListener.Stub.asInterface(data.readStrongBinder());
                            UceLong _arg13 = (UceLong) data.readTypedObject(UceLong.CREATOR);
                            data.enforceNoDataAvail();
                            int _result6 = createPresenceService(_arg05, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            reply.writeTypedObject(_arg13, 1);
                            break;
                        case 8:
                            IPresenceListener _arg06 = IPresenceListener.Stub.asInterface(data.readStrongBinder());
                            UceLong _arg14 = (UceLong) data.readTypedObject(UceLong.CREATOR);
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            int _result7 = createPresenceServiceForSubscription(_arg06, _arg14, _arg22);
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            reply.writeTypedObject(_arg14, 1);
                            break;
                        case 9:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            destroyPresenceService(_arg07);
                            reply.writeNoException();
                            break;
                        case 10:
                            boolean _result8 = getServiceStatus();
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 11:
                            IPresenceService _result9 = getPresenceService();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result9);
                            break;
                        case 12:
                            String _arg08 = data.readString();
                            data.enforceNoDataAvail();
                            IPresenceService _result10 = getPresenceServiceForSubscription(_arg08);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result10);
                            break;
                        case 13:
                            IOptionsService _result11 = getOptionsService();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result11);
                            break;
                        case 14:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            IOptionsService _result12 = getOptionsServiceForSubscription(_arg09);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IUceService {
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

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public boolean startService(IUceListener uceListener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(uceListener);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public boolean stopService() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public boolean isServiceStarted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public int createOptionsService(IOptionsListener optionsListener, UceLong optionsServiceListenerHdl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(optionsListener);
                    _data.writeTypedObject(optionsServiceListenerHdl, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (_reply.readInt() != 0) {
                        optionsServiceListenerHdl.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public int createOptionsServiceForSubscription(IOptionsListener optionsListener, UceLong optionsServiceListenerHdl, String iccId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(optionsListener);
                    _data.writeTypedObject(optionsServiceListenerHdl, 0);
                    _data.writeString(iccId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (_reply.readInt() != 0) {
                        optionsServiceListenerHdl.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public void destroyOptionsService(int optionsServiceHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(optionsServiceHandle);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public int createPresenceService(IPresenceListener presenceServiceListener, UceLong presenceServiceListenerHdl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(presenceServiceListener);
                    _data.writeTypedObject(presenceServiceListenerHdl, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (_reply.readInt() != 0) {
                        presenceServiceListenerHdl.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public int createPresenceServiceForSubscription(IPresenceListener presenceServiceListener, UceLong presenceServiceListenerHdl, String iccId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(presenceServiceListener);
                    _data.writeTypedObject(presenceServiceListenerHdl, 0);
                    _data.writeString(iccId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (_reply.readInt() != 0) {
                        presenceServiceListenerHdl.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public void destroyPresenceService(int presenceServiceHdl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presenceServiceHdl);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public boolean getServiceStatus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public IPresenceService getPresenceService() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    IPresenceService _result = IPresenceService.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public IPresenceService getPresenceServiceForSubscription(String iccId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iccId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    IPresenceService _result = IPresenceService.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public IOptionsService getOptionsService() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    IOptionsService _result = IOptionsService.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.uceservice.IUceService
            public IOptionsService getOptionsServiceForSubscription(String iccId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iccId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    IOptionsService _result = IOptionsService.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 13;
        }
    }
}
