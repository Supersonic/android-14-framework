package com.android.internal.compat;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IOverrideValidator extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.compat.IOverrideValidator";

    OverrideAllowedState getOverrideAllowedState(long j, String str) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IOverrideValidator {
        @Override // com.android.internal.compat.IOverrideValidator
        public OverrideAllowedState getOverrideAllowedState(long changeId, String packageName) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IOverrideValidator {
        static final int TRANSACTION_getOverrideAllowedState = 1;

        public Stub() {
            attachInterface(this, IOverrideValidator.DESCRIPTOR);
        }

        public static IOverrideValidator asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IOverrideValidator.DESCRIPTOR);
            if (iin != null && (iin instanceof IOverrideValidator)) {
                return (IOverrideValidator) iin;
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
                    return "getOverrideAllowedState";
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
                data.enforceInterface(IOverrideValidator.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IOverrideValidator.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            long _arg0 = data.readLong();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            OverrideAllowedState _result = getOverrideAllowedState(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IOverrideValidator {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IOverrideValidator.DESCRIPTOR;
            }

            @Override // com.android.internal.compat.IOverrideValidator
            public OverrideAllowedState getOverrideAllowedState(long changeId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IOverrideValidator.DESCRIPTOR);
                    _data.writeLong(changeId);
                    _data.writeString(packageName);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    OverrideAllowedState _result = (OverrideAllowedState) _reply.readTypedObject(OverrideAllowedState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
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
