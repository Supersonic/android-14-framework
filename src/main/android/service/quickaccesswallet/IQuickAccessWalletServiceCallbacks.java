package android.service.quickaccesswallet;

import android.app.PendingIntent;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IQuickAccessWalletServiceCallbacks extends IInterface {
    public static final String DESCRIPTOR = "android.service.quickaccesswallet.IQuickAccessWalletServiceCallbacks";

    void onGetWalletCardsFailure(GetWalletCardsError getWalletCardsError) throws RemoteException;

    void onGetWalletCardsSuccess(GetWalletCardsResponse getWalletCardsResponse) throws RemoteException;

    void onTargetActivityPendingIntentReceived(PendingIntent pendingIntent) throws RemoteException;

    void onWalletServiceEvent(WalletServiceEvent walletServiceEvent) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IQuickAccessWalletServiceCallbacks {
        @Override // android.service.quickaccesswallet.IQuickAccessWalletServiceCallbacks
        public void onGetWalletCardsSuccess(GetWalletCardsResponse response) throws RemoteException {
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletServiceCallbacks
        public void onGetWalletCardsFailure(GetWalletCardsError error) throws RemoteException {
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletServiceCallbacks
        public void onWalletServiceEvent(WalletServiceEvent event) throws RemoteException {
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletServiceCallbacks
        public void onTargetActivityPendingIntentReceived(PendingIntent pendingIntent) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IQuickAccessWalletServiceCallbacks {
        static final int TRANSACTION_onGetWalletCardsFailure = 2;
        static final int TRANSACTION_onGetWalletCardsSuccess = 1;
        static final int TRANSACTION_onTargetActivityPendingIntentReceived = 4;
        static final int TRANSACTION_onWalletServiceEvent = 3;

        public Stub() {
            attachInterface(this, IQuickAccessWalletServiceCallbacks.DESCRIPTOR);
        }

        public static IQuickAccessWalletServiceCallbacks asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IQuickAccessWalletServiceCallbacks.DESCRIPTOR);
            if (iin != null && (iin instanceof IQuickAccessWalletServiceCallbacks)) {
                return (IQuickAccessWalletServiceCallbacks) iin;
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
                    return "onGetWalletCardsSuccess";
                case 2:
                    return "onGetWalletCardsFailure";
                case 3:
                    return "onWalletServiceEvent";
                case 4:
                    return "onTargetActivityPendingIntentReceived";
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
                data.enforceInterface(IQuickAccessWalletServiceCallbacks.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IQuickAccessWalletServiceCallbacks.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            GetWalletCardsResponse _arg0 = (GetWalletCardsResponse) data.readTypedObject(GetWalletCardsResponse.CREATOR);
                            data.enforceNoDataAvail();
                            onGetWalletCardsSuccess(_arg0);
                            break;
                        case 2:
                            GetWalletCardsError _arg02 = (GetWalletCardsError) data.readTypedObject(GetWalletCardsError.CREATOR);
                            data.enforceNoDataAvail();
                            onGetWalletCardsFailure(_arg02);
                            break;
                        case 3:
                            WalletServiceEvent _arg03 = (WalletServiceEvent) data.readTypedObject(WalletServiceEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onWalletServiceEvent(_arg03);
                            break;
                        case 4:
                            PendingIntent _arg04 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            onTargetActivityPendingIntentReceived(_arg04);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IQuickAccessWalletServiceCallbacks {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IQuickAccessWalletServiceCallbacks.DESCRIPTOR;
            }

            @Override // android.service.quickaccesswallet.IQuickAccessWalletServiceCallbacks
            public void onGetWalletCardsSuccess(GetWalletCardsResponse response) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQuickAccessWalletServiceCallbacks.DESCRIPTOR);
                    _data.writeTypedObject(response, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.quickaccesswallet.IQuickAccessWalletServiceCallbacks
            public void onGetWalletCardsFailure(GetWalletCardsError error) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQuickAccessWalletServiceCallbacks.DESCRIPTOR);
                    _data.writeTypedObject(error, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.quickaccesswallet.IQuickAccessWalletServiceCallbacks
            public void onWalletServiceEvent(WalletServiceEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQuickAccessWalletServiceCallbacks.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.quickaccesswallet.IQuickAccessWalletServiceCallbacks
            public void onTargetActivityPendingIntentReceived(PendingIntent pendingIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQuickAccessWalletServiceCallbacks.DESCRIPTOR);
                    _data.writeTypedObject(pendingIntent, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
