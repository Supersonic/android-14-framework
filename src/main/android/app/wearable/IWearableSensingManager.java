package android.app.wearable;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.SharedMemory;
/* loaded from: classes.dex */
public interface IWearableSensingManager extends IInterface {
    public static final String DESCRIPTOR = "android.app.wearable.IWearableSensingManager";

    void provideData(PersistableBundle persistableBundle, SharedMemory sharedMemory, RemoteCallback remoteCallback) throws RemoteException;

    void provideDataStream(ParcelFileDescriptor parcelFileDescriptor, RemoteCallback remoteCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IWearableSensingManager {
        @Override // android.app.wearable.IWearableSensingManager
        public void provideDataStream(ParcelFileDescriptor parcelFileDescriptor, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.app.wearable.IWearableSensingManager
        public void provideData(PersistableBundle data, SharedMemory sharedMemory, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IWearableSensingManager {
        static final int TRANSACTION_provideData = 2;
        static final int TRANSACTION_provideDataStream = 1;

        public Stub() {
            attachInterface(this, IWearableSensingManager.DESCRIPTOR);
        }

        public static IWearableSensingManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWearableSensingManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IWearableSensingManager)) {
                return (IWearableSensingManager) iin;
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
                data.enforceInterface(IWearableSensingManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWearableSensingManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ParcelFileDescriptor _arg0 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            RemoteCallback _arg1 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            provideDataStream(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            PersistableBundle _arg02 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            SharedMemory _arg12 = (SharedMemory) data.readTypedObject(SharedMemory.CREATOR);
                            RemoteCallback _arg2 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            provideData(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IWearableSensingManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWearableSensingManager.DESCRIPTOR;
            }

            @Override // android.app.wearable.IWearableSensingManager
            public void provideDataStream(ParcelFileDescriptor parcelFileDescriptor, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWearableSensingManager.DESCRIPTOR);
                    _data.writeTypedObject(parcelFileDescriptor, 0);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.wearable.IWearableSensingManager
            public void provideData(PersistableBundle data, SharedMemory sharedMemory, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWearableSensingManager.DESCRIPTOR);
                    _data.writeTypedObject(data, 0);
                    _data.writeTypedObject(sharedMemory, 0);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
