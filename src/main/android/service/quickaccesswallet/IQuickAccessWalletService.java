package android.service.quickaccesswallet;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.quickaccesswallet.IQuickAccessWalletServiceCallbacks;
/* loaded from: classes3.dex */
public interface IQuickAccessWalletService extends IInterface {
    public static final String DESCRIPTOR = "android.service.quickaccesswallet.IQuickAccessWalletService";

    void onTargetActivityIntentRequested(IQuickAccessWalletServiceCallbacks iQuickAccessWalletServiceCallbacks) throws RemoteException;

    void onWalletCardSelected(SelectWalletCardRequest selectWalletCardRequest) throws RemoteException;

    void onWalletCardsRequested(GetWalletCardsRequest getWalletCardsRequest, IQuickAccessWalletServiceCallbacks iQuickAccessWalletServiceCallbacks) throws RemoteException;

    void onWalletDismissed() throws RemoteException;

    void registerWalletServiceEventListener(WalletServiceEventListenerRequest walletServiceEventListenerRequest, IQuickAccessWalletServiceCallbacks iQuickAccessWalletServiceCallbacks) throws RemoteException;

    void unregisterWalletServiceEventListener(WalletServiceEventListenerRequest walletServiceEventListenerRequest) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IQuickAccessWalletService {
        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void onWalletCardsRequested(GetWalletCardsRequest request, IQuickAccessWalletServiceCallbacks callback) throws RemoteException {
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void onWalletCardSelected(SelectWalletCardRequest request) throws RemoteException {
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void onWalletDismissed() throws RemoteException {
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void registerWalletServiceEventListener(WalletServiceEventListenerRequest request, IQuickAccessWalletServiceCallbacks callback) throws RemoteException {
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void unregisterWalletServiceEventListener(WalletServiceEventListenerRequest request) throws RemoteException {
        }

        @Override // android.service.quickaccesswallet.IQuickAccessWalletService
        public void onTargetActivityIntentRequested(IQuickAccessWalletServiceCallbacks callbacks) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IQuickAccessWalletService {
        static final int TRANSACTION_onTargetActivityIntentRequested = 6;
        static final int TRANSACTION_onWalletCardSelected = 2;
        static final int TRANSACTION_onWalletCardsRequested = 1;
        static final int TRANSACTION_onWalletDismissed = 3;
        static final int TRANSACTION_registerWalletServiceEventListener = 4;
        static final int TRANSACTION_unregisterWalletServiceEventListener = 5;

        public Stub() {
            attachInterface(this, IQuickAccessWalletService.DESCRIPTOR);
        }

        public static IQuickAccessWalletService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IQuickAccessWalletService.DESCRIPTOR);
            if (iin != null && (iin instanceof IQuickAccessWalletService)) {
                return (IQuickAccessWalletService) iin;
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
                    return "onWalletCardsRequested";
                case 2:
                    return "onWalletCardSelected";
                case 3:
                    return "onWalletDismissed";
                case 4:
                    return "registerWalletServiceEventListener";
                case 5:
                    return "unregisterWalletServiceEventListener";
                case 6:
                    return "onTargetActivityIntentRequested";
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
                data.enforceInterface(IQuickAccessWalletService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IQuickAccessWalletService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            GetWalletCardsRequest _arg0 = (GetWalletCardsRequest) data.readTypedObject(GetWalletCardsRequest.CREATOR);
                            IQuickAccessWalletServiceCallbacks _arg1 = IQuickAccessWalletServiceCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onWalletCardsRequested(_arg0, _arg1);
                            break;
                        case 2:
                            SelectWalletCardRequest _arg02 = (SelectWalletCardRequest) data.readTypedObject(SelectWalletCardRequest.CREATOR);
                            data.enforceNoDataAvail();
                            onWalletCardSelected(_arg02);
                            break;
                        case 3:
                            onWalletDismissed();
                            break;
                        case 4:
                            WalletServiceEventListenerRequest _arg03 = (WalletServiceEventListenerRequest) data.readTypedObject(WalletServiceEventListenerRequest.CREATOR);
                            IQuickAccessWalletServiceCallbacks _arg12 = IQuickAccessWalletServiceCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerWalletServiceEventListener(_arg03, _arg12);
                            break;
                        case 5:
                            WalletServiceEventListenerRequest _arg04 = (WalletServiceEventListenerRequest) data.readTypedObject(WalletServiceEventListenerRequest.CREATOR);
                            data.enforceNoDataAvail();
                            unregisterWalletServiceEventListener(_arg04);
                            break;
                        case 6:
                            IQuickAccessWalletServiceCallbacks _arg05 = IQuickAccessWalletServiceCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onTargetActivityIntentRequested(_arg05);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IQuickAccessWalletService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IQuickAccessWalletService.DESCRIPTOR;
            }

            @Override // android.service.quickaccesswallet.IQuickAccessWalletService
            public void onWalletCardsRequested(GetWalletCardsRequest request, IQuickAccessWalletServiceCallbacks callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQuickAccessWalletService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.quickaccesswallet.IQuickAccessWalletService
            public void onWalletCardSelected(SelectWalletCardRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQuickAccessWalletService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.quickaccesswallet.IQuickAccessWalletService
            public void onWalletDismissed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQuickAccessWalletService.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.quickaccesswallet.IQuickAccessWalletService
            public void registerWalletServiceEventListener(WalletServiceEventListenerRequest request, IQuickAccessWalletServiceCallbacks callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQuickAccessWalletService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.quickaccesswallet.IQuickAccessWalletService
            public void unregisterWalletServiceEventListener(WalletServiceEventListenerRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQuickAccessWalletService.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.quickaccesswallet.IQuickAccessWalletService
            public void onTargetActivityIntentRequested(IQuickAccessWalletServiceCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IQuickAccessWalletService.DESCRIPTOR);
                    _data.writeStrongInterface(callbacks);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
