package android.telephony.mbms.vendor;

import android.content.ContentResolver;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.mbms.IMbmsStreamingSessionCallback;
import android.telephony.mbms.IStreamingServiceCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface IMbmsStreamingService extends IInterface {
    void dispose(int i) throws RemoteException;

    Uri getPlaybackUri(int i, String str) throws RemoteException;

    int initialize(IMbmsStreamingSessionCallback iMbmsStreamingSessionCallback, int i) throws RemoteException;

    int requestUpdateStreamingServices(int i, List<String> list) throws RemoteException;

    int startStreaming(int i, String str, IStreamingServiceCallback iStreamingServiceCallback) throws RemoteException;

    void stopStreaming(int i, String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IMbmsStreamingService {
        @Override // android.telephony.mbms.vendor.IMbmsStreamingService
        public int initialize(IMbmsStreamingSessionCallback callback, int subId) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsStreamingService
        public int requestUpdateStreamingServices(int subId, List<String> serviceClasses) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsStreamingService
        public int startStreaming(int subId, String serviceId, IStreamingServiceCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsStreamingService
        public Uri getPlaybackUri(int subId, String serviceId) throws RemoteException {
            return null;
        }

        @Override // android.telephony.mbms.vendor.IMbmsStreamingService
        public void stopStreaming(int subId, String serviceId) throws RemoteException {
        }

        @Override // android.telephony.mbms.vendor.IMbmsStreamingService
        public void dispose(int subId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IMbmsStreamingService {
        public static final String DESCRIPTOR = "android.telephony.mbms.vendor.IMbmsStreamingService";
        static final int TRANSACTION_dispose = 6;
        static final int TRANSACTION_getPlaybackUri = 4;
        static final int TRANSACTION_initialize = 1;
        static final int TRANSACTION_requestUpdateStreamingServices = 2;
        static final int TRANSACTION_startStreaming = 3;
        static final int TRANSACTION_stopStreaming = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMbmsStreamingService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMbmsStreamingService)) {
                return (IMbmsStreamingService) iin;
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
                    return ContentResolver.SYNC_EXTRAS_INITIALIZE;
                case 2:
                    return "requestUpdateStreamingServices";
                case 3:
                    return "startStreaming";
                case 4:
                    return "getPlaybackUri";
                case 5:
                    return "stopStreaming";
                case 6:
                    return "dispose";
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
                            IMbmsStreamingSessionCallback _arg0 = IMbmsStreamingSessionCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result = initialize(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            List<String> _arg12 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            int _result2 = requestUpdateStreamingServices(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            String _arg13 = data.readString();
                            IStreamingServiceCallback _arg2 = IStreamingServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result3 = startStreaming(_arg03, _arg13, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            Uri _result4 = getPlaybackUri(_arg04, _arg14);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            stopStreaming(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            dispose(_arg06);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IMbmsStreamingService {
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

            @Override // android.telephony.mbms.vendor.IMbmsStreamingService
            public int initialize(IMbmsStreamingSessionCallback callback, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(subId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsStreamingService
            public int requestUpdateStreamingServices(int subId, List<String> serviceClasses) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeStringList(serviceClasses);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsStreamingService
            public int startStreaming(int subId, String serviceId, IStreamingServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(serviceId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsStreamingService
            public Uri getPlaybackUri(int subId, String serviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(serviceId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    Uri _result = (Uri) _reply.readTypedObject(Uri.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsStreamingService
            public void stopStreaming(int subId, String serviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(serviceId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsStreamingService
            public void dispose(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
