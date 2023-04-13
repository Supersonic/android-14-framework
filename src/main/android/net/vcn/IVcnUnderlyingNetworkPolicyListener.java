package android.net.vcn;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IVcnUnderlyingNetworkPolicyListener extends IInterface {
    public static final String DESCRIPTOR = "android.net.vcn.IVcnUnderlyingNetworkPolicyListener";

    void onPolicyChanged() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IVcnUnderlyingNetworkPolicyListener {
        @Override // android.net.vcn.IVcnUnderlyingNetworkPolicyListener
        public void onPolicyChanged() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IVcnUnderlyingNetworkPolicyListener {
        static final int TRANSACTION_onPolicyChanged = 1;

        public Stub() {
            attachInterface(this, IVcnUnderlyingNetworkPolicyListener.DESCRIPTOR);
        }

        public static IVcnUnderlyingNetworkPolicyListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IVcnUnderlyingNetworkPolicyListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IVcnUnderlyingNetworkPolicyListener)) {
                return (IVcnUnderlyingNetworkPolicyListener) iin;
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
                    return "onPolicyChanged";
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
                data.enforceInterface(IVcnUnderlyingNetworkPolicyListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IVcnUnderlyingNetworkPolicyListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onPolicyChanged();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IVcnUnderlyingNetworkPolicyListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IVcnUnderlyingNetworkPolicyListener.DESCRIPTOR;
            }

            @Override // android.net.vcn.IVcnUnderlyingNetworkPolicyListener
            public void onPolicyChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVcnUnderlyingNetworkPolicyListener.DESCRIPTOR);
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
