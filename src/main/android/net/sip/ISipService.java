package android.net.sip;

import android.app.PendingIntent;
import android.net.sip.ISipSession;
import android.net.sip.ISipSessionListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface ISipService extends IInterface {
    void close(String str, String str2) throws RemoteException;

    ISipSession createSession(SipProfile sipProfile, ISipSessionListener iSipSessionListener, String str) throws RemoteException;

    ISipSession getPendingSession(String str, String str2) throws RemoteException;

    List<SipProfile> getProfiles(String str) throws RemoteException;

    boolean isOpened(String str, String str2) throws RemoteException;

    boolean isRegistered(String str, String str2) throws RemoteException;

    void open(SipProfile sipProfile, String str) throws RemoteException;

    void open3(SipProfile sipProfile, PendingIntent pendingIntent, ISipSessionListener iSipSessionListener, String str) throws RemoteException;

    void setRegistrationListener(String str, ISipSessionListener iSipSessionListener, String str2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISipService {
        @Override // android.net.sip.ISipService
        public void open(SipProfile localProfile, String opPackageName) throws RemoteException {
        }

        @Override // android.net.sip.ISipService
        public void open3(SipProfile localProfile, PendingIntent incomingCallPendingIntent, ISipSessionListener listener, String opPackageName) throws RemoteException {
        }

        @Override // android.net.sip.ISipService
        public void close(String localProfileUri, String opPackageName) throws RemoteException {
        }

        @Override // android.net.sip.ISipService
        public boolean isOpened(String localProfileUri, String opPackageName) throws RemoteException {
            return false;
        }

        @Override // android.net.sip.ISipService
        public boolean isRegistered(String localProfileUri, String opPackageName) throws RemoteException {
            return false;
        }

        @Override // android.net.sip.ISipService
        public void setRegistrationListener(String localProfileUri, ISipSessionListener listener, String opPackageName) throws RemoteException {
        }

        @Override // android.net.sip.ISipService
        public ISipSession createSession(SipProfile localProfile, ISipSessionListener listener, String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.net.sip.ISipService
        public ISipSession getPendingSession(String callId, String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.net.sip.ISipService
        public List<SipProfile> getProfiles(String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISipService {
        public static final String DESCRIPTOR = "android.net.sip.ISipService";
        static final int TRANSACTION_close = 3;
        static final int TRANSACTION_createSession = 7;
        static final int TRANSACTION_getPendingSession = 8;
        static final int TRANSACTION_getProfiles = 9;
        static final int TRANSACTION_isOpened = 4;
        static final int TRANSACTION_isRegistered = 5;
        static final int TRANSACTION_open = 1;
        static final int TRANSACTION_open3 = 2;
        static final int TRANSACTION_setRegistrationListener = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISipService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISipService)) {
                return (ISipService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SipProfile _arg0 = (SipProfile) data.readTypedObject(SipProfile.CREATOR);
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            open(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            SipProfile _arg02 = (SipProfile) data.readTypedObject(SipProfile.CREATOR);
                            PendingIntent _arg12 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            ISipSessionListener _arg2 = ISipSessionListener.Stub.asInterface(data.readStrongBinder());
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            open3(_arg02, _arg12, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            close(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = isOpened(_arg04, _arg14);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result2 = isRegistered(_arg05, _arg15);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            ISipSessionListener _arg16 = ISipSessionListener.Stub.asInterface(data.readStrongBinder());
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            setRegistrationListener(_arg06, _arg16, _arg22);
                            reply.writeNoException();
                            break;
                        case 7:
                            SipProfile _arg07 = (SipProfile) data.readTypedObject(SipProfile.CREATOR);
                            ISipSessionListener _arg17 = ISipSessionListener.Stub.asInterface(data.readStrongBinder());
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            ISipSession _result3 = createSession(_arg07, _arg17, _arg23);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            String _arg18 = data.readString();
                            data.enforceNoDataAvail();
                            ISipSession _result4 = getPendingSession(_arg08, _arg18);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result4);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            List<SipProfile> _result5 = getProfiles(_arg09);
                            reply.writeNoException();
                            reply.writeTypedList(_result5, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISipService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.net.sip.ISipService
            public void open(SipProfile localProfile, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(localProfile, 0);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipService
            public void open3(SipProfile localProfile, PendingIntent incomingCallPendingIntent, ISipSessionListener listener, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(localProfile, 0);
                    _data.writeTypedObject(incomingCallPendingIntent, 0);
                    _data.writeStrongInterface(listener);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipService
            public void close(String localProfileUri, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(localProfileUri);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipService
            public boolean isOpened(String localProfileUri, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(localProfileUri);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipService
            public boolean isRegistered(String localProfileUri, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(localProfileUri);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipService
            public void setRegistrationListener(String localProfileUri, ISipSessionListener listener, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(localProfileUri);
                    _data.writeStrongInterface(listener);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipService
            public ISipSession createSession(SipProfile localProfile, ISipSessionListener listener, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(localProfile, 0);
                    _data.writeStrongInterface(listener);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    ISipSession _result = ISipSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipService
            public ISipSession getPendingSession(String callId, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    ISipSession _result = ISipSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipService
            public List<SipProfile> getProfiles(String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    List<SipProfile> _result = _reply.createTypedArrayList(SipProfile.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
