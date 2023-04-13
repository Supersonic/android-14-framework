package android.media.midi;

import android.bluetooth.BluetoothDevice;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IBluetoothMidiService extends IInterface {
    IBinder addBluetoothDevice(BluetoothDevice bluetoothDevice) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IBluetoothMidiService {
        @Override // android.media.midi.IBluetoothMidiService
        public IBinder addBluetoothDevice(BluetoothDevice bluetoothDevice) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IBluetoothMidiService {
        public static final String DESCRIPTOR = "android.media.midi.IBluetoothMidiService";
        static final int TRANSACTION_addBluetoothDevice = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBluetoothMidiService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBluetoothMidiService)) {
                return (IBluetoothMidiService) iin;
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
                    return "addBluetoothDevice";
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
                            BluetoothDevice _arg0 = (BluetoothDevice) data.readTypedObject(BluetoothDevice.CREATOR);
                            data.enforceNoDataAvail();
                            IBinder _result = addBluetoothDevice(_arg0);
                            reply.writeNoException();
                            reply.writeStrongBinder(_result);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IBluetoothMidiService {
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

            @Override // android.media.midi.IBluetoothMidiService
            public IBinder addBluetoothDevice(BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(bluetoothDevice, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
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
