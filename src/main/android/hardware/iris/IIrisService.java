package android.hardware.iris;

import android.Manifest;
import android.app.ActivityThread;
import android.content.AttributionSource;
import android.hardware.biometrics.SensorPropertiesInternal;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface IIrisService extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.iris.IIrisService";

    void registerAuthenticators(List<SensorPropertiesInternal> list) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IIrisService {
        @Override // android.hardware.iris.IIrisService
        public void registerAuthenticators(List<SensorPropertiesInternal> hidlSensors) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IIrisService {
        static final int TRANSACTION_registerAuthenticators = 1;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, IIrisService.DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IIrisService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IIrisService.DESCRIPTOR);
            if (iin != null && (iin instanceof IIrisService)) {
                return (IIrisService) iin;
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
                    return "registerAuthenticators";
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
                data.enforceInterface(IIrisService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IIrisService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<SensorPropertiesInternal> _arg0 = data.createTypedArrayList(SensorPropertiesInternal.CREATOR);
                            data.enforceNoDataAvail();
                            registerAuthenticators(_arg0);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IIrisService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IIrisService.DESCRIPTOR;
            }

            @Override // android.hardware.iris.IIrisService
            public void registerAuthenticators(List<SensorPropertiesInternal> hidlSensors) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IIrisService.DESCRIPTOR);
                    _data.writeTypedList(hidlSensors, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void registerAuthenticators_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
