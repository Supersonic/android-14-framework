package android.hardware.input;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IInputDeviceBatteryListener extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.input.IInputDeviceBatteryListener";

    void onBatteryStateChanged(IInputDeviceBatteryState iInputDeviceBatteryState) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IInputDeviceBatteryListener {
        @Override // android.hardware.input.IInputDeviceBatteryListener
        public void onBatteryStateChanged(IInputDeviceBatteryState batteryState) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IInputDeviceBatteryListener {
        static final int TRANSACTION_onBatteryStateChanged = 1;

        public Stub() {
            attachInterface(this, IInputDeviceBatteryListener.DESCRIPTOR);
        }

        public static IInputDeviceBatteryListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInputDeviceBatteryListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IInputDeviceBatteryListener)) {
                return (IInputDeviceBatteryListener) iin;
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
                    return "onBatteryStateChanged";
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
                data.enforceInterface(IInputDeviceBatteryListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInputDeviceBatteryListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IInputDeviceBatteryState _arg0 = (IInputDeviceBatteryState) data.readTypedObject(IInputDeviceBatteryState.CREATOR);
                            data.enforceNoDataAvail();
                            onBatteryStateChanged(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IInputDeviceBatteryListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInputDeviceBatteryListener.DESCRIPTOR;
            }

            @Override // android.hardware.input.IInputDeviceBatteryListener
            public void onBatteryStateChanged(IInputDeviceBatteryState batteryState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputDeviceBatteryListener.DESCRIPTOR);
                    _data.writeTypedObject(batteryState, 0);
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
