package android.view;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface ICrossWindowBlurEnabledListener extends IInterface {
    public static final String DESCRIPTOR = "android.view.ICrossWindowBlurEnabledListener";

    void onCrossWindowBlurEnabledChanged(boolean z) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ICrossWindowBlurEnabledListener {
        @Override // android.view.ICrossWindowBlurEnabledListener
        public void onCrossWindowBlurEnabledChanged(boolean enabled) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ICrossWindowBlurEnabledListener {
        static final int TRANSACTION_onCrossWindowBlurEnabledChanged = 1;

        public Stub() {
            attachInterface(this, ICrossWindowBlurEnabledListener.DESCRIPTOR);
        }

        public static ICrossWindowBlurEnabledListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICrossWindowBlurEnabledListener.DESCRIPTOR);
            if (iin != null && (iin instanceof ICrossWindowBlurEnabledListener)) {
                return (ICrossWindowBlurEnabledListener) iin;
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
                    return "onCrossWindowBlurEnabledChanged";
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
                data.enforceInterface(ICrossWindowBlurEnabledListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICrossWindowBlurEnabledListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onCrossWindowBlurEnabledChanged(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements ICrossWindowBlurEnabledListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICrossWindowBlurEnabledListener.DESCRIPTOR;
            }

            @Override // android.view.ICrossWindowBlurEnabledListener
            public void onCrossWindowBlurEnabledChanged(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICrossWindowBlurEnabledListener.DESCRIPTOR);
                    _data.writeBoolean(enabled);
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
