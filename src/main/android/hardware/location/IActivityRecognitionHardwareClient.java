package android.hardware.location;

import android.hardware.location.IActivityRecognitionHardware;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IActivityRecognitionHardwareClient extends IInterface {
    void onAvailabilityChanged(boolean z, IActivityRecognitionHardware iActivityRecognitionHardware) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IActivityRecognitionHardwareClient {
        @Override // android.hardware.location.IActivityRecognitionHardwareClient
        public void onAvailabilityChanged(boolean isSupported, IActivityRecognitionHardware instance) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IActivityRecognitionHardwareClient {
        public static final String DESCRIPTOR = "android.hardware.location.IActivityRecognitionHardwareClient";
        static final int TRANSACTION_onAvailabilityChanged = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IActivityRecognitionHardwareClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IActivityRecognitionHardwareClient)) {
                return (IActivityRecognitionHardwareClient) iin;
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
                    return "onAvailabilityChanged";
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
                            boolean _arg0 = data.readBoolean();
                            IActivityRecognitionHardware _arg1 = IActivityRecognitionHardware.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onAvailabilityChanged(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IActivityRecognitionHardwareClient {
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

            @Override // android.hardware.location.IActivityRecognitionHardwareClient
            public void onAvailabilityChanged(boolean isSupported, IActivityRecognitionHardware instance) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isSupported);
                    _data.writeStrongInterface(instance);
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
