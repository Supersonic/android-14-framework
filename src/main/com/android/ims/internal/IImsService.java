package com.android.ims.internal;

import android.app.PendingIntent;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Message;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.RcsContactPresenceTuple;
import com.android.ims.internal.IImsCallSession;
import com.android.ims.internal.IImsCallSessionListener;
import com.android.ims.internal.IImsConfig;
import com.android.ims.internal.IImsEcbm;
import com.android.ims.internal.IImsMultiEndpoint;
import com.android.ims.internal.IImsRegistrationListener;
import com.android.ims.internal.IImsUt;
/* loaded from: classes4.dex */
public interface IImsService extends IInterface {
    void addRegistrationListener(int i, int i2, IImsRegistrationListener iImsRegistrationListener) throws RemoteException;

    void close(int i) throws RemoteException;

    ImsCallProfile createCallProfile(int i, int i2, int i3) throws RemoteException;

    IImsCallSession createCallSession(int i, ImsCallProfile imsCallProfile, IImsCallSessionListener iImsCallSessionListener) throws RemoteException;

    IImsConfig getConfigInterface(int i) throws RemoteException;

    IImsEcbm getEcbmInterface(int i) throws RemoteException;

    IImsMultiEndpoint getMultiEndpointInterface(int i) throws RemoteException;

    IImsCallSession getPendingCallSession(int i, String str) throws RemoteException;

    IImsUt getUtInterface(int i) throws RemoteException;

    boolean isConnected(int i, int i2, int i3) throws RemoteException;

    boolean isOpened(int i) throws RemoteException;

    int open(int i, int i2, PendingIntent pendingIntent, IImsRegistrationListener iImsRegistrationListener) throws RemoteException;

    void setRegistrationListener(int i, IImsRegistrationListener iImsRegistrationListener) throws RemoteException;

    void setUiTTYMode(int i, int i2, Message message) throws RemoteException;

    void turnOffIms(int i) throws RemoteException;

    void turnOnIms(int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IImsService {
        @Override // com.android.ims.internal.IImsService
        public int open(int phoneId, int serviceClass, PendingIntent incomingCallIntent, IImsRegistrationListener listener) throws RemoteException {
            return 0;
        }

        @Override // com.android.ims.internal.IImsService
        public void close(int serviceId) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsService
        public boolean isConnected(int serviceId, int serviceType, int callType) throws RemoteException {
            return false;
        }

        @Override // com.android.ims.internal.IImsService
        public boolean isOpened(int serviceId) throws RemoteException {
            return false;
        }

        @Override // com.android.ims.internal.IImsService
        public void setRegistrationListener(int serviceId, IImsRegistrationListener listener) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsService
        public void addRegistrationListener(int phoneId, int serviceClass, IImsRegistrationListener listener) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsService
        public ImsCallProfile createCallProfile(int serviceId, int serviceType, int callType) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.IImsService
        public IImsCallSession createCallSession(int serviceId, ImsCallProfile profile, IImsCallSessionListener listener) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.IImsService
        public IImsCallSession getPendingCallSession(int serviceId, String callId) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.IImsService
        public IImsUt getUtInterface(int serviceId) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.IImsService
        public IImsConfig getConfigInterface(int phoneId) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.IImsService
        public void turnOnIms(int phoneId) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsService
        public void turnOffIms(int phoneId) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsService
        public IImsEcbm getEcbmInterface(int serviceId) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.IImsService
        public void setUiTTYMode(int serviceId, int uiTtyMode, Message onComplete) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsService
        public IImsMultiEndpoint getMultiEndpointInterface(int serviceId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IImsService {
        public static final String DESCRIPTOR = "com.android.ims.internal.IImsService";
        static final int TRANSACTION_addRegistrationListener = 6;
        static final int TRANSACTION_close = 2;
        static final int TRANSACTION_createCallProfile = 7;
        static final int TRANSACTION_createCallSession = 8;
        static final int TRANSACTION_getConfigInterface = 11;
        static final int TRANSACTION_getEcbmInterface = 14;
        static final int TRANSACTION_getMultiEndpointInterface = 16;
        static final int TRANSACTION_getPendingCallSession = 9;
        static final int TRANSACTION_getUtInterface = 10;
        static final int TRANSACTION_isConnected = 3;
        static final int TRANSACTION_isOpened = 4;
        static final int TRANSACTION_open = 1;
        static final int TRANSACTION_setRegistrationListener = 5;
        static final int TRANSACTION_setUiTTYMode = 15;
        static final int TRANSACTION_turnOffIms = 13;
        static final int TRANSACTION_turnOnIms = 12;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IImsService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IImsService)) {
                return (IImsService) iin;
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
                    return RcsContactPresenceTuple.TUPLE_BASIC_STATUS_OPEN;
                case 2:
                    return "close";
                case 3:
                    return "isConnected";
                case 4:
                    return "isOpened";
                case 5:
                    return "setRegistrationListener";
                case 6:
                    return "addRegistrationListener";
                case 7:
                    return "createCallProfile";
                case 8:
                    return "createCallSession";
                case 9:
                    return "getPendingCallSession";
                case 10:
                    return "getUtInterface";
                case 11:
                    return "getConfigInterface";
                case 12:
                    return "turnOnIms";
                case 13:
                    return "turnOffIms";
                case 14:
                    return "getEcbmInterface";
                case 15:
                    return "setUiTTYMode";
                case 16:
                    return "getMultiEndpointInterface";
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
                            PendingIntent _arg2 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            IImsRegistrationListener _arg3 = IImsRegistrationListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result = open(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            close(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = isConnected(_arg03, _arg12, _arg22);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result3 = isOpened(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            IImsRegistrationListener _arg13 = IImsRegistrationListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setRegistrationListener(_arg05, _arg13);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg14 = data.readInt();
                            IImsRegistrationListener _arg23 = IImsRegistrationListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addRegistrationListener(_arg06, _arg14, _arg23);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            int _arg15 = data.readInt();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            ImsCallProfile _result4 = createCallProfile(_arg07, _arg15, _arg24);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            ImsCallProfile _arg16 = (ImsCallProfile) data.readTypedObject(ImsCallProfile.CREATOR);
                            IImsCallSessionListener _arg25 = IImsCallSessionListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            IImsCallSession _result5 = createCallSession(_arg08, _arg16, _arg25);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result5);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            IImsCallSession _result6 = getPendingCallSession(_arg09, _arg17);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result6);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsUt _result7 = getUtInterface(_arg010);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result7);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsConfig _result8 = getConfigInterface(_arg011);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result8);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            turnOnIms(_arg012);
                            reply.writeNoException();
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            data.enforceNoDataAvail();
                            turnOffIms(_arg013);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsEcbm _result9 = getEcbmInterface(_arg014);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result9);
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            int _arg18 = data.readInt();
                            Message _arg26 = (Message) data.readTypedObject(Message.CREATOR);
                            data.enforceNoDataAvail();
                            setUiTTYMode(_arg015, _arg18, _arg26);
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsMultiEndpoint _result10 = getMultiEndpointInterface(_arg016);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result10);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IImsService {
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

            @Override // com.android.ims.internal.IImsService
            public int open(int phoneId, int serviceClass, PendingIntent incomingCallIntent, IImsRegistrationListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(serviceClass);
                    _data.writeTypedObject(incomingCallIntent, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public void close(int serviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public boolean isConnected(int serviceId, int serviceType, int callType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    _data.writeInt(serviceType);
                    _data.writeInt(callType);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public boolean isOpened(int serviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public void setRegistrationListener(int serviceId, IImsRegistrationListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public void addRegistrationListener(int phoneId, int serviceClass, IImsRegistrationListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(serviceClass);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public ImsCallProfile createCallProfile(int serviceId, int serviceType, int callType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    _data.writeInt(serviceType);
                    _data.writeInt(callType);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    ImsCallProfile _result = (ImsCallProfile) _reply.readTypedObject(ImsCallProfile.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public IImsCallSession createCallSession(int serviceId, ImsCallProfile profile, IImsCallSessionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    _data.writeTypedObject(profile, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    IImsCallSession _result = IImsCallSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public IImsCallSession getPendingCallSession(int serviceId, String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    _data.writeString(callId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    IImsCallSession _result = IImsCallSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public IImsUt getUtInterface(int serviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    IImsUt _result = IImsUt.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public IImsConfig getConfigInterface(int phoneId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    IImsConfig _result = IImsConfig.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public void turnOnIms(int phoneId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public void turnOffIms(int phoneId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public IImsEcbm getEcbmInterface(int serviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    IImsEcbm _result = IImsEcbm.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public void setUiTTYMode(int serviceId, int uiTtyMode, Message onComplete) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    _data.writeInt(uiTtyMode);
                    _data.writeTypedObject(onComplete, 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsService
            public IImsMultiEndpoint getMultiEndpointInterface(int serviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(serviceId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    IImsMultiEndpoint _result = IImsMultiEndpoint.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 15;
        }
    }
}
