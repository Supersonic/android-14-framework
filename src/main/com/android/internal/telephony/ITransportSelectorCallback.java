package com.android.internal.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.telephony.IDomainSelector;
import com.android.internal.telephony.ITransportSelectorResultCallback;
import com.android.internal.telephony.IWwanSelectorCallback;
/* loaded from: classes3.dex */
public interface ITransportSelectorCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telephony.ITransportSelectorCallback";

    void onCreated(IDomainSelector iDomainSelector) throws RemoteException;

    void onSelectionTerminated(int i) throws RemoteException;

    void onWlanSelected(boolean z) throws RemoteException;

    IWwanSelectorCallback onWwanSelected() throws RemoteException;

    void onWwanSelectedAsync(ITransportSelectorResultCallback iTransportSelectorResultCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITransportSelectorCallback {
        @Override // com.android.internal.telephony.ITransportSelectorCallback
        public void onCreated(IDomainSelector selector) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITransportSelectorCallback
        public void onWlanSelected(boolean useEmergencyPdn) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITransportSelectorCallback
        public IWwanSelectorCallback onWwanSelected() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ITransportSelectorCallback
        public void onWwanSelectedAsync(ITransportSelectorResultCallback cb) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITransportSelectorCallback
        public void onSelectionTerminated(int cause) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITransportSelectorCallback {
        static final int TRANSACTION_onCreated = 1;
        static final int TRANSACTION_onSelectionTerminated = 5;
        static final int TRANSACTION_onWlanSelected = 2;
        static final int TRANSACTION_onWwanSelected = 3;
        static final int TRANSACTION_onWwanSelectedAsync = 4;

        public Stub() {
            attachInterface(this, ITransportSelectorCallback.DESCRIPTOR);
        }

        public static ITransportSelectorCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITransportSelectorCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ITransportSelectorCallback)) {
                return (ITransportSelectorCallback) iin;
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
                    return "onCreated";
                case 2:
                    return "onWlanSelected";
                case 3:
                    return "onWwanSelected";
                case 4:
                    return "onWwanSelectedAsync";
                case 5:
                    return "onSelectionTerminated";
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
                data.enforceInterface(ITransportSelectorCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITransportSelectorCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IDomainSelector _arg0 = IDomainSelector.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onCreated(_arg0);
                            break;
                        case 2:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onWlanSelected(_arg02);
                            break;
                        case 3:
                            IWwanSelectorCallback _result = onWwanSelected();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 4:
                            ITransportSelectorResultCallback _arg03 = ITransportSelectorResultCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onWwanSelectedAsync(_arg03);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            onSelectionTerminated(_arg04);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITransportSelectorCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITransportSelectorCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.telephony.ITransportSelectorCallback
            public void onCreated(IDomainSelector selector) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITransportSelectorCallback.DESCRIPTOR);
                    _data.writeStrongInterface(selector);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITransportSelectorCallback
            public void onWlanSelected(boolean useEmergencyPdn) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITransportSelectorCallback.DESCRIPTOR);
                    _data.writeBoolean(useEmergencyPdn);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITransportSelectorCallback
            public IWwanSelectorCallback onWwanSelected() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITransportSelectorCallback.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    IWwanSelectorCallback _result = IWwanSelectorCallback.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITransportSelectorCallback
            public void onWwanSelectedAsync(ITransportSelectorResultCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITransportSelectorCallback.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITransportSelectorCallback
            public void onSelectionTerminated(int cause) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITransportSelectorCallback.DESCRIPTOR);
                    _data.writeInt(cause);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
