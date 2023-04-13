package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface IDevicesForAttributesCallback extends IInterface {
    public static final String DESCRIPTOR = "android.media.IDevicesForAttributesCallback";

    void onDevicesForAttributesChanged(AudioAttributes audioAttributes, boolean z, List<AudioDeviceAttributes> list) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IDevicesForAttributesCallback {
        @Override // android.media.IDevicesForAttributesCallback
        public void onDevicesForAttributesChanged(AudioAttributes attributes, boolean forVolume, List<AudioDeviceAttributes> devices) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IDevicesForAttributesCallback {
        static final int TRANSACTION_onDevicesForAttributesChanged = 1;

        public Stub() {
            attachInterface(this, IDevicesForAttributesCallback.DESCRIPTOR);
        }

        public static IDevicesForAttributesCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDevicesForAttributesCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IDevicesForAttributesCallback)) {
                return (IDevicesForAttributesCallback) iin;
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
                    return "onDevicesForAttributesChanged";
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
                data.enforceInterface(IDevicesForAttributesCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDevicesForAttributesCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            AudioAttributes _arg0 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            boolean _arg1 = data.readBoolean();
                            List<AudioDeviceAttributes> _arg2 = data.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            onDevicesForAttributesChanged(_arg0, _arg1, _arg2);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IDevicesForAttributesCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDevicesForAttributesCallback.DESCRIPTOR;
            }

            @Override // android.media.IDevicesForAttributesCallback
            public void onDevicesForAttributesChanged(AudioAttributes attributes, boolean forVolume, List<AudioDeviceAttributes> devices) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDevicesForAttributesCallback.DESCRIPTOR);
                    _data.writeTypedObject(attributes, 0);
                    _data.writeBoolean(forVolume);
                    _data.writeTypedList(devices, 0);
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
