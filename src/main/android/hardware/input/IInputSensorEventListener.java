package android.hardware.input;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IInputSensorEventListener extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.input.IInputSensorEventListener";

    void onInputSensorAccuracyChanged(int i, int i2, int i3) throws RemoteException;

    void onInputSensorChanged(int i, int i2, int i3, long j, float[] fArr) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IInputSensorEventListener {
        @Override // android.hardware.input.IInputSensorEventListener
        public void onInputSensorChanged(int deviceId, int sensorId, int accuracy, long timestamp, float[] values) throws RemoteException {
        }

        @Override // android.hardware.input.IInputSensorEventListener
        public void onInputSensorAccuracyChanged(int deviceId, int sensorId, int accuracy) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IInputSensorEventListener {
        static final int TRANSACTION_onInputSensorAccuracyChanged = 2;
        static final int TRANSACTION_onInputSensorChanged = 1;

        public Stub() {
            attachInterface(this, IInputSensorEventListener.DESCRIPTOR);
        }

        public static IInputSensorEventListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInputSensorEventListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IInputSensorEventListener)) {
                return (IInputSensorEventListener) iin;
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
                    return "onInputSensorChanged";
                case 2:
                    return "onInputSensorAccuracyChanged";
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
                data.enforceInterface(IInputSensorEventListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInputSensorEventListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            long _arg3 = data.readLong();
                            float[] _arg4 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            onInputSensorChanged(_arg0, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            onInputSensorAccuracyChanged(_arg02, _arg12, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IInputSensorEventListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInputSensorEventListener.DESCRIPTOR;
            }

            @Override // android.hardware.input.IInputSensorEventListener
            public void onInputSensorChanged(int deviceId, int sensorId, int accuracy, long timestamp, float[] values) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputSensorEventListener.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(sensorId);
                    _data.writeInt(accuracy);
                    _data.writeLong(timestamp);
                    _data.writeFloatArray(values);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputSensorEventListener
            public void onInputSensorAccuracyChanged(int deviceId, int sensorId, int accuracy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputSensorEventListener.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(sensorId);
                    _data.writeInt(accuracy);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
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
