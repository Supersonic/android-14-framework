package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ICommunicationDeviceDispatcher extends IInterface {
    public static final String DESCRIPTOR = "android.media.ICommunicationDeviceDispatcher";

    void dispatchCommunicationDeviceChanged(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ICommunicationDeviceDispatcher {
        @Override // android.media.ICommunicationDeviceDispatcher
        public void dispatchCommunicationDeviceChanged(int portId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICommunicationDeviceDispatcher {
        static final int TRANSACTION_dispatchCommunicationDeviceChanged = 1;

        public Stub() {
            attachInterface(this, ICommunicationDeviceDispatcher.DESCRIPTOR);
        }

        public static ICommunicationDeviceDispatcher asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICommunicationDeviceDispatcher.DESCRIPTOR);
            if (iin != null && (iin instanceof ICommunicationDeviceDispatcher)) {
                return (ICommunicationDeviceDispatcher) iin;
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
                    return "dispatchCommunicationDeviceChanged";
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
                data.enforceInterface(ICommunicationDeviceDispatcher.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICommunicationDeviceDispatcher.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchCommunicationDeviceChanged(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ICommunicationDeviceDispatcher {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICommunicationDeviceDispatcher.DESCRIPTOR;
            }

            @Override // android.media.ICommunicationDeviceDispatcher
            public void dispatchCommunicationDeviceChanged(int portId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICommunicationDeviceDispatcher.DESCRIPTOR);
                    _data.writeInt(portId);
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
