package android.hardware.gnss.measurement_corrections;

import android.hardware.gnss.measurement_corrections.IMeasurementCorrectionsCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMeasurementCorrectionsInterface extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$gnss$measurement_corrections$IMeasurementCorrectionsInterface".replace('$', '.');
    public static final String HASH = "fc957f1d3d261d065ff5e5415f2d21caa79c310f";
    public static final int VERSION = 2;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void setCallback(IMeasurementCorrectionsCallback iMeasurementCorrectionsCallback) throws RemoteException;

    void setCorrections(MeasurementCorrections measurementCorrections) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMeasurementCorrectionsInterface {
        @Override // android.hardware.gnss.measurement_corrections.IMeasurementCorrectionsInterface
        public void setCorrections(MeasurementCorrections corrections) throws RemoteException {
        }

        @Override // android.hardware.gnss.measurement_corrections.IMeasurementCorrectionsInterface
        public void setCallback(IMeasurementCorrectionsCallback callback) throws RemoteException {
        }

        @Override // android.hardware.gnss.measurement_corrections.IMeasurementCorrectionsInterface
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.gnss.measurement_corrections.IMeasurementCorrectionsInterface
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMeasurementCorrectionsInterface {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_setCallback = 2;
        static final int TRANSACTION_setCorrections = 1;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IMeasurementCorrectionsInterface asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMeasurementCorrectionsInterface)) {
                return (IMeasurementCorrectionsInterface) iin;
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
                    return "setCorrections";
                case 2:
                    return "setCallback";
                case 16777214:
                    return "getInterfaceHash";
                case 16777215:
                    return "getInterfaceVersion";
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
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case 16777214:
                    reply.writeNoException();
                    reply.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    reply.writeNoException();
                    reply.writeInt(getInterfaceVersion());
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            MeasurementCorrections _arg0 = (MeasurementCorrections) data.readTypedObject(MeasurementCorrections.CREATOR);
                            data.enforceNoDataAvail();
                            setCorrections(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            IMeasurementCorrectionsCallback _arg02 = IMeasurementCorrectionsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setCallback(_arg02);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMeasurementCorrectionsInterface {
            private IBinder mRemote;
            private int mCachedVersion = -1;
            private String mCachedHash = "-1";

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.hardware.gnss.measurement_corrections.IMeasurementCorrectionsInterface
            public void setCorrections(MeasurementCorrections corrections) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(corrections, 0);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setCorrections is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.measurement_corrections.IMeasurementCorrectionsInterface
            public void setCallback(IMeasurementCorrectionsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setCallback is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.measurement_corrections.IMeasurementCorrectionsInterface
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(DESCRIPTOR);
                        this.mRemote.transact(16777215, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.hardware.gnss.measurement_corrections.IMeasurementCorrectionsInterface
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(16777214, data, reply, 0);
                    reply.readException();
                    this.mCachedHash = reply.readString();
                    reply.recycle();
                    data.recycle();
                }
                return this.mCachedHash;
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 16777214;
        }
    }
}
