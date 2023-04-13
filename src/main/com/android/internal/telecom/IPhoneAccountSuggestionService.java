package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.telecom.IPhoneAccountSuggestionCallback;
/* loaded from: classes2.dex */
public interface IPhoneAccountSuggestionService extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telecom.IPhoneAccountSuggestionService";

    void onAccountSuggestionRequest(IPhoneAccountSuggestionCallback iPhoneAccountSuggestionCallback, String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IPhoneAccountSuggestionService {
        @Override // com.android.internal.telecom.IPhoneAccountSuggestionService
        public void onAccountSuggestionRequest(IPhoneAccountSuggestionCallback callback, String number) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IPhoneAccountSuggestionService {
        static final int TRANSACTION_onAccountSuggestionRequest = 1;

        public Stub() {
            attachInterface(this, IPhoneAccountSuggestionService.DESCRIPTOR);
        }

        public static IPhoneAccountSuggestionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPhoneAccountSuggestionService.DESCRIPTOR);
            if (iin != null && (iin instanceof IPhoneAccountSuggestionService)) {
                return (IPhoneAccountSuggestionService) iin;
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
                    return "onAccountSuggestionRequest";
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
                data.enforceInterface(IPhoneAccountSuggestionService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPhoneAccountSuggestionService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IPhoneAccountSuggestionCallback _arg0 = IPhoneAccountSuggestionCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            onAccountSuggestionRequest(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IPhoneAccountSuggestionService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPhoneAccountSuggestionService.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.IPhoneAccountSuggestionService
            public void onAccountSuggestionRequest(IPhoneAccountSuggestionCallback callback, String number) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPhoneAccountSuggestionService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeString(number);
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
