package android.location;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface IGeocodeListener extends IInterface {
    public static final String DESCRIPTOR = "android.location.IGeocodeListener";

    void onResults(String str, List<Address> list) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IGeocodeListener {
        @Override // android.location.IGeocodeListener
        public void onResults(String error, List<Address> results) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IGeocodeListener {
        static final int TRANSACTION_onResults = 1;

        public Stub() {
            attachInterface(this, IGeocodeListener.DESCRIPTOR);
        }

        public static IGeocodeListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGeocodeListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IGeocodeListener)) {
                return (IGeocodeListener) iin;
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
                    return "onResults";
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
                data.enforceInterface(IGeocodeListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IGeocodeListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            List<Address> _arg1 = data.createTypedArrayList(Address.CREATOR);
                            data.enforceNoDataAvail();
                            onResults(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IGeocodeListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGeocodeListener.DESCRIPTOR;
            }

            @Override // android.location.IGeocodeListener
            public void onResults(String error, List<Address> results) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGeocodeListener.DESCRIPTOR);
                    _data.writeString(error);
                    _data.writeTypedList(results, 0);
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
