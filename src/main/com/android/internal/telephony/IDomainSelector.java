package com.android.internal.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.DomainSelectionService;
/* loaded from: classes3.dex */
public interface IDomainSelector extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telephony.IDomainSelector";

    void cancelSelection() throws RemoteException;

    void finishSelection() throws RemoteException;

    void reselectDomain(DomainSelectionService.SelectionAttributes selectionAttributes) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IDomainSelector {
        @Override // com.android.internal.telephony.IDomainSelector
        public void cancelSelection() throws RemoteException {
        }

        @Override // com.android.internal.telephony.IDomainSelector
        public void reselectDomain(DomainSelectionService.SelectionAttributes attr) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IDomainSelector
        public void finishSelection() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IDomainSelector {
        static final int TRANSACTION_cancelSelection = 1;
        static final int TRANSACTION_finishSelection = 3;
        static final int TRANSACTION_reselectDomain = 2;

        public Stub() {
            attachInterface(this, IDomainSelector.DESCRIPTOR);
        }

        public static IDomainSelector asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDomainSelector.DESCRIPTOR);
            if (iin != null && (iin instanceof IDomainSelector)) {
                return (IDomainSelector) iin;
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
                    return "cancelSelection";
                case 2:
                    return "reselectDomain";
                case 3:
                    return "finishSelection";
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
                data.enforceInterface(IDomainSelector.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDomainSelector.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            cancelSelection();
                            break;
                        case 2:
                            DomainSelectionService.SelectionAttributes _arg0 = (DomainSelectionService.SelectionAttributes) data.readTypedObject(DomainSelectionService.SelectionAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            reselectDomain(_arg0);
                            break;
                        case 3:
                            finishSelection();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IDomainSelector {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDomainSelector.DESCRIPTOR;
            }

            @Override // com.android.internal.telephony.IDomainSelector
            public void cancelSelection() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDomainSelector.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IDomainSelector
            public void reselectDomain(DomainSelectionService.SelectionAttributes attr) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDomainSelector.DESCRIPTOR);
                    _data.writeTypedObject(attr, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IDomainSelector
            public void finishSelection() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDomainSelector.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
