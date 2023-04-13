package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IDeviceVolumeBehaviorDispatcher extends IInterface {
    public static final String DESCRIPTOR = "android.media.IDeviceVolumeBehaviorDispatcher";

    void dispatchDeviceVolumeBehaviorChanged(AudioDeviceAttributes audioDeviceAttributes, int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IDeviceVolumeBehaviorDispatcher {
        @Override // android.media.IDeviceVolumeBehaviorDispatcher
        public void dispatchDeviceVolumeBehaviorChanged(AudioDeviceAttributes device, int deviceVolumeBehavior) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IDeviceVolumeBehaviorDispatcher {
        static final int TRANSACTION_dispatchDeviceVolumeBehaviorChanged = 1;

        public Stub() {
            attachInterface(this, IDeviceVolumeBehaviorDispatcher.DESCRIPTOR);
        }

        public static IDeviceVolumeBehaviorDispatcher asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDeviceVolumeBehaviorDispatcher.DESCRIPTOR);
            if (iin != null && (iin instanceof IDeviceVolumeBehaviorDispatcher)) {
                return (IDeviceVolumeBehaviorDispatcher) iin;
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
                    return "dispatchDeviceVolumeBehaviorChanged";
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
                data.enforceInterface(IDeviceVolumeBehaviorDispatcher.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDeviceVolumeBehaviorDispatcher.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            AudioDeviceAttributes _arg0 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchDeviceVolumeBehaviorChanged(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IDeviceVolumeBehaviorDispatcher {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDeviceVolumeBehaviorDispatcher.DESCRIPTOR;
            }

            @Override // android.media.IDeviceVolumeBehaviorDispatcher
            public void dispatchDeviceVolumeBehaviorChanged(AudioDeviceAttributes device, int deviceVolumeBehavior) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDeviceVolumeBehaviorDispatcher.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    _data.writeInt(deviceVolumeBehavior);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
