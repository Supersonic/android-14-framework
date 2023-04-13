package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telecom.PhoneAccountSuggestion;
import java.util.List;
/* loaded from: classes2.dex */
public interface IPhoneAccountSuggestionCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telecom.IPhoneAccountSuggestionCallback";

    void suggestPhoneAccounts(String str, List<PhoneAccountSuggestion> list) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IPhoneAccountSuggestionCallback {
        @Override // com.android.internal.telecom.IPhoneAccountSuggestionCallback
        public void suggestPhoneAccounts(String number, List<PhoneAccountSuggestion> suggestions) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IPhoneAccountSuggestionCallback {
        static final int TRANSACTION_suggestPhoneAccounts = 1;

        public Stub() {
            attachInterface(this, IPhoneAccountSuggestionCallback.DESCRIPTOR);
        }

        public static IPhoneAccountSuggestionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPhoneAccountSuggestionCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IPhoneAccountSuggestionCallback)) {
                return (IPhoneAccountSuggestionCallback) iin;
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
                    return "suggestPhoneAccounts";
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
                data.enforceInterface(IPhoneAccountSuggestionCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPhoneAccountSuggestionCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            List<PhoneAccountSuggestion> _arg1 = data.createTypedArrayList(PhoneAccountSuggestion.CREATOR);
                            data.enforceNoDataAvail();
                            suggestPhoneAccounts(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IPhoneAccountSuggestionCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPhoneAccountSuggestionCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.IPhoneAccountSuggestionCallback
            public void suggestPhoneAccounts(String number, List<PhoneAccountSuggestion> suggestions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPhoneAccountSuggestionCallback.DESCRIPTOR);
                    _data.writeString(number);
                    _data.writeTypedList(suggestions, 0);
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
