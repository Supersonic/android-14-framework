package com.android.internal.telecom;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telecom.PhoneAccountHandle;
import com.android.internal.telecom.ICallRedirectionAdapter;
/* loaded from: classes2.dex */
public interface ICallRedirectionService extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telecom.ICallRedirectionService";

    void notifyTimeout() throws RemoteException;

    void placeCall(ICallRedirectionAdapter iCallRedirectionAdapter, Uri uri, PhoneAccountHandle phoneAccountHandle, boolean z) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ICallRedirectionService {
        @Override // com.android.internal.telecom.ICallRedirectionService
        public void placeCall(ICallRedirectionAdapter adapter, Uri handle, PhoneAccountHandle initialPhoneAccount, boolean allowInteractiveResponse) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallRedirectionService
        public void notifyTimeout() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICallRedirectionService {
        static final int TRANSACTION_notifyTimeout = 2;
        static final int TRANSACTION_placeCall = 1;

        public Stub() {
            attachInterface(this, ICallRedirectionService.DESCRIPTOR);
        }

        public static ICallRedirectionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICallRedirectionService.DESCRIPTOR);
            if (iin != null && (iin instanceof ICallRedirectionService)) {
                return (ICallRedirectionService) iin;
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
                    return "placeCall";
                case 2:
                    return "notifyTimeout";
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
                data.enforceInterface(ICallRedirectionService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICallRedirectionService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ICallRedirectionAdapter _arg0 = ICallRedirectionAdapter.Stub.asInterface(data.readStrongBinder());
                            Uri _arg1 = (Uri) data.readTypedObject(Uri.CREATOR);
                            PhoneAccountHandle _arg2 = (PhoneAccountHandle) data.readTypedObject(PhoneAccountHandle.CREATOR);
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            placeCall(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            notifyTimeout();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ICallRedirectionService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICallRedirectionService.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.ICallRedirectionService
            public void placeCall(ICallRedirectionAdapter adapter, Uri handle, PhoneAccountHandle initialPhoneAccount, boolean allowInteractiveResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallRedirectionService.DESCRIPTOR);
                    _data.writeStrongInterface(adapter);
                    _data.writeTypedObject(handle, 0);
                    _data.writeTypedObject(initialPhoneAccount, 0);
                    _data.writeBoolean(allowInteractiveResponse);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallRedirectionService
            public void notifyTimeout() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallRedirectionService.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
