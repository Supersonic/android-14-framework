package android.security.attestationverification;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelDuration;
import android.p008os.RemoteException;
import com.android.internal.infra.AndroidFuture;
/* loaded from: classes3.dex */
public interface IAttestationVerificationManagerService extends IInterface {
    public static final String DESCRIPTOR = "android.security.attestationverification.IAttestationVerificationManagerService";

    void verifyAttestation(AttestationProfile attestationProfile, int i, Bundle bundle, byte[] bArr, AndroidFuture androidFuture) throws RemoteException;

    void verifyToken(VerificationToken verificationToken, ParcelDuration parcelDuration, AndroidFuture androidFuture) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IAttestationVerificationManagerService {
        @Override // android.security.attestationverification.IAttestationVerificationManagerService
        public void verifyAttestation(AttestationProfile profile, int localBindingType, Bundle requirements, byte[] attestation, AndroidFuture resultCallback) throws RemoteException {
        }

        @Override // android.security.attestationverification.IAttestationVerificationManagerService
        public void verifyToken(VerificationToken token, ParcelDuration maximumTokenAge, AndroidFuture resultCallback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IAttestationVerificationManagerService {
        static final int TRANSACTION_verifyAttestation = 1;
        static final int TRANSACTION_verifyToken = 2;

        public Stub() {
            attachInterface(this, IAttestationVerificationManagerService.DESCRIPTOR);
        }

        public static IAttestationVerificationManagerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAttestationVerificationManagerService.DESCRIPTOR);
            if (iin != null && (iin instanceof IAttestationVerificationManagerService)) {
                return (IAttestationVerificationManagerService) iin;
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
                    return "verifyAttestation";
                case 2:
                    return "verifyToken";
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
                data.enforceInterface(IAttestationVerificationManagerService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAttestationVerificationManagerService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            AttestationProfile _arg0 = (AttestationProfile) data.readTypedObject(AttestationProfile.CREATOR);
                            int _arg1 = data.readInt();
                            Bundle _arg2 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            byte[] _arg3 = data.createByteArray();
                            AndroidFuture _arg4 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            verifyAttestation(_arg0, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 2:
                            VerificationToken _arg02 = (VerificationToken) data.readTypedObject(VerificationToken.CREATOR);
                            ParcelDuration _arg12 = (ParcelDuration) data.readTypedObject(ParcelDuration.CREATOR);
                            AndroidFuture _arg22 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            verifyToken(_arg02, _arg12, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IAttestationVerificationManagerService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAttestationVerificationManagerService.DESCRIPTOR;
            }

            @Override // android.security.attestationverification.IAttestationVerificationManagerService
            public void verifyAttestation(AttestationProfile profile, int localBindingType, Bundle requirements, byte[] attestation, AndroidFuture resultCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAttestationVerificationManagerService.DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    _data.writeInt(localBindingType);
                    _data.writeTypedObject(requirements, 0);
                    _data.writeByteArray(attestation);
                    _data.writeTypedObject(resultCallback, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.security.attestationverification.IAttestationVerificationManagerService
            public void verifyToken(VerificationToken token, ParcelDuration maximumTokenAge, AndroidFuture resultCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAttestationVerificationManagerService.DESCRIPTOR);
                    _data.writeTypedObject(token, 0);
                    _data.writeTypedObject(maximumTokenAge, 0);
                    _data.writeTypedObject(resultCallback, 0);
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
