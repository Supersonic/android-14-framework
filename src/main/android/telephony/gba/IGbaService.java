package android.telephony.gba;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IGbaService extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.gba.IGbaService";

    void authenticationRequest(GbaAuthRequest gbaAuthRequest) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IGbaService {
        @Override // android.telephony.gba.IGbaService
        public void authenticationRequest(GbaAuthRequest request) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IGbaService {
        static final int TRANSACTION_authenticationRequest = 1;

        public Stub() {
            attachInterface(this, IGbaService.DESCRIPTOR);
        }

        public static IGbaService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGbaService.DESCRIPTOR);
            if (iin != null && (iin instanceof IGbaService)) {
                return (IGbaService) iin;
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
                    return "authenticationRequest";
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
                data.enforceInterface(IGbaService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IGbaService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            GbaAuthRequest _arg0 = (GbaAuthRequest) data.readTypedObject(GbaAuthRequest.CREATOR);
                            data.enforceNoDataAvail();
                            authenticationRequest(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IGbaService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGbaService.DESCRIPTOR;
            }

            @Override // android.telephony.gba.IGbaService
            public void authenticationRequest(GbaAuthRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGbaService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
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
