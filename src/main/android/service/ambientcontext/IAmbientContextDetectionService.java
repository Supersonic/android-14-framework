package android.service.ambientcontext;

import android.app.ambientcontext.AmbientContextEventRequest;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IAmbientContextDetectionService extends IInterface {
    public static final String DESCRIPTOR = "android.service.ambientcontext.IAmbientContextDetectionService";

    void queryServiceStatus(int[] iArr, String str, RemoteCallback remoteCallback) throws RemoteException;

    void startDetection(AmbientContextEventRequest ambientContextEventRequest, String str, RemoteCallback remoteCallback, RemoteCallback remoteCallback2) throws RemoteException;

    void stopDetection(String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IAmbientContextDetectionService {
        @Override // android.service.ambientcontext.IAmbientContextDetectionService
        public void startDetection(AmbientContextEventRequest request, String packageName, RemoteCallback detectionResultCallback, RemoteCallback statusCallback) throws RemoteException {
        }

        @Override // android.service.ambientcontext.IAmbientContextDetectionService
        public void stopDetection(String packageName) throws RemoteException {
        }

        @Override // android.service.ambientcontext.IAmbientContextDetectionService
        public void queryServiceStatus(int[] eventTypes, String packageName, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IAmbientContextDetectionService {
        static final int TRANSACTION_queryServiceStatus = 3;
        static final int TRANSACTION_startDetection = 1;
        static final int TRANSACTION_stopDetection = 2;

        public Stub() {
            attachInterface(this, IAmbientContextDetectionService.DESCRIPTOR);
        }

        public static IAmbientContextDetectionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAmbientContextDetectionService.DESCRIPTOR);
            if (iin != null && (iin instanceof IAmbientContextDetectionService)) {
                return (IAmbientContextDetectionService) iin;
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
                    return "startDetection";
                case 2:
                    return "stopDetection";
                case 3:
                    return "queryServiceStatus";
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
                data.enforceInterface(IAmbientContextDetectionService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAmbientContextDetectionService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            AmbientContextEventRequest _arg0 = (AmbientContextEventRequest) data.readTypedObject(AmbientContextEventRequest.CREATOR);
                            String _arg1 = data.readString();
                            RemoteCallback _arg2 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            RemoteCallback _arg3 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            startDetection(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            stopDetection(_arg02);
                            break;
                        case 3:
                            int[] _arg03 = data.createIntArray();
                            String _arg12 = data.readString();
                            RemoteCallback _arg22 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            queryServiceStatus(_arg03, _arg12, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IAmbientContextDetectionService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAmbientContextDetectionService.DESCRIPTOR;
            }

            @Override // android.service.ambientcontext.IAmbientContextDetectionService
            public void startDetection(AmbientContextEventRequest request, String packageName, RemoteCallback detectionResultCallback, RemoteCallback statusCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAmbientContextDetectionService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeString(packageName);
                    _data.writeTypedObject(detectionResultCallback, 0);
                    _data.writeTypedObject(statusCallback, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.ambientcontext.IAmbientContextDetectionService
            public void stopDetection(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAmbientContextDetectionService.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.ambientcontext.IAmbientContextDetectionService
            public void queryServiceStatus(int[] eventTypes, String packageName, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAmbientContextDetectionService.DESCRIPTOR);
                    _data.writeIntArray(eventTypes);
                    _data.writeString(packageName);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
