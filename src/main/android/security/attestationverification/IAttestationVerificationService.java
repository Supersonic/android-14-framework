package android.security.attestationverification;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.infra.AndroidFuture;
/* loaded from: classes3.dex */
public interface IAttestationVerificationService extends IInterface {
    public static final String DESCRIPTOR = "android.security.attestationverification.IAttestationVerificationService";

    void onVerifyAttestation(Bundle bundle, byte[] bArr, AndroidFuture androidFuture) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IAttestationVerificationService {
        @Override // android.security.attestationverification.IAttestationVerificationService
        public void onVerifyAttestation(Bundle requirements, byte[] attestation, AndroidFuture callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IAttestationVerificationService {
        static final int TRANSACTION_onVerifyAttestation = 1;

        public Stub() {
            attachInterface(this, IAttestationVerificationService.DESCRIPTOR);
        }

        public static IAttestationVerificationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAttestationVerificationService.DESCRIPTOR);
            if (iin != null && (iin instanceof IAttestationVerificationService)) {
                return (IAttestationVerificationService) iin;
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
                    return "onVerifyAttestation";
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
                data.enforceInterface(IAttestationVerificationService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAttestationVerificationService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            Bundle _arg0 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            byte[] _arg1 = data.createByteArray();
                            AndroidFuture _arg2 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            onVerifyAttestation(_arg0, _arg1, _arg2);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IAttestationVerificationService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAttestationVerificationService.DESCRIPTOR;
            }

            @Override // android.security.attestationverification.IAttestationVerificationService
            public void onVerifyAttestation(Bundle requirements, byte[] attestation, AndroidFuture callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAttestationVerificationService.DESCRIPTOR);
                    _data.writeTypedObject(requirements, 0);
                    _data.writeByteArray(attestation);
                    _data.writeTypedObject(callback, 0);
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
