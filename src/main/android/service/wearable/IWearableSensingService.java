package android.service.wearable;

import android.app.ambientcontext.AmbientContextEventRequest;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.SharedMemory;
/* loaded from: classes3.dex */
public interface IWearableSensingService extends IInterface {
    public static final String DESCRIPTOR = "android.service.wearable.IWearableSensingService";

    void provideData(PersistableBundle persistableBundle, SharedMemory sharedMemory, RemoteCallback remoteCallback) throws RemoteException;

    void provideDataStream(ParcelFileDescriptor parcelFileDescriptor, RemoteCallback remoteCallback) throws RemoteException;

    void queryServiceStatus(int[] iArr, String str, RemoteCallback remoteCallback) throws RemoteException;

    void startDetection(AmbientContextEventRequest ambientContextEventRequest, String str, RemoteCallback remoteCallback, RemoteCallback remoteCallback2) throws RemoteException;

    void stopDetection(String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IWearableSensingService {
        @Override // android.service.wearable.IWearableSensingService
        public void provideDataStream(ParcelFileDescriptor parcelFileDescriptor, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.wearable.IWearableSensingService
        public void provideData(PersistableBundle data, SharedMemory sharedMemory, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.wearable.IWearableSensingService
        public void startDetection(AmbientContextEventRequest request, String packageName, RemoteCallback detectionResultCallback, RemoteCallback statusCallback) throws RemoteException {
        }

        @Override // android.service.wearable.IWearableSensingService
        public void stopDetection(String packageName) throws RemoteException {
        }

        @Override // android.service.wearable.IWearableSensingService
        public void queryServiceStatus(int[] eventTypes, String packageName, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IWearableSensingService {
        static final int TRANSACTION_provideData = 2;
        static final int TRANSACTION_provideDataStream = 1;
        static final int TRANSACTION_queryServiceStatus = 5;
        static final int TRANSACTION_startDetection = 3;
        static final int TRANSACTION_stopDetection = 4;

        public Stub() {
            attachInterface(this, IWearableSensingService.DESCRIPTOR);
        }

        public static IWearableSensingService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWearableSensingService.DESCRIPTOR);
            if (iin != null && (iin instanceof IWearableSensingService)) {
                return (IWearableSensingService) iin;
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
                    return "provideDataStream";
                case 2:
                    return "provideData";
                case 3:
                    return "startDetection";
                case 4:
                    return "stopDetection";
                case 5:
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
                data.enforceInterface(IWearableSensingService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWearableSensingService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ParcelFileDescriptor _arg0 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            RemoteCallback _arg1 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            provideDataStream(_arg0, _arg1);
                            break;
                        case 2:
                            PersistableBundle _arg02 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            SharedMemory _arg12 = (SharedMemory) data.readTypedObject(SharedMemory.CREATOR);
                            RemoteCallback _arg2 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            provideData(_arg02, _arg12, _arg2);
                            break;
                        case 3:
                            AmbientContextEventRequest _arg03 = (AmbientContextEventRequest) data.readTypedObject(AmbientContextEventRequest.CREATOR);
                            String _arg13 = data.readString();
                            RemoteCallback _arg22 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            RemoteCallback _arg3 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            startDetection(_arg03, _arg13, _arg22, _arg3);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            stopDetection(_arg04);
                            break;
                        case 5:
                            int[] _arg05 = data.createIntArray();
                            String _arg14 = data.readString();
                            RemoteCallback _arg23 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            queryServiceStatus(_arg05, _arg14, _arg23);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IWearableSensingService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWearableSensingService.DESCRIPTOR;
            }

            @Override // android.service.wearable.IWearableSensingService
            public void provideDataStream(ParcelFileDescriptor parcelFileDescriptor, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWearableSensingService.DESCRIPTOR);
                    _data.writeTypedObject(parcelFileDescriptor, 0);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wearable.IWearableSensingService
            public void provideData(PersistableBundle data, SharedMemory sharedMemory, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWearableSensingService.DESCRIPTOR);
                    _data.writeTypedObject(data, 0);
                    _data.writeTypedObject(sharedMemory, 0);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wearable.IWearableSensingService
            public void startDetection(AmbientContextEventRequest request, String packageName, RemoteCallback detectionResultCallback, RemoteCallback statusCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWearableSensingService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeString(packageName);
                    _data.writeTypedObject(detectionResultCallback, 0);
                    _data.writeTypedObject(statusCallback, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wearable.IWearableSensingService
            public void stopDetection(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWearableSensingService.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.wearable.IWearableSensingService
            public void queryServiceStatus(int[] eventTypes, String packageName, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWearableSensingService.DESCRIPTOR);
                    _data.writeIntArray(eventTypes);
                    _data.writeString(packageName);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
