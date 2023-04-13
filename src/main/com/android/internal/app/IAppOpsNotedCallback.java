package com.android.internal.app;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IAppOpsNotedCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.app.IAppOpsNotedCallback";

    void opNoted(int i, int i2, String str, String str2, int i3, int i4) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAppOpsNotedCallback {
        @Override // com.android.internal.app.IAppOpsNotedCallback
        public void opNoted(int op, int uid, String packageName, String attributionTag, int flags, int mode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAppOpsNotedCallback {
        static final int TRANSACTION_opNoted = 1;

        public Stub() {
            attachInterface(this, IAppOpsNotedCallback.DESCRIPTOR);
        }

        public static IAppOpsNotedCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAppOpsNotedCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IAppOpsNotedCallback)) {
                return (IAppOpsNotedCallback) iin;
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
                    return "opNoted";
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
                data.enforceInterface(IAppOpsNotedCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAppOpsNotedCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            int _arg4 = data.readInt();
                            int _arg5 = data.readInt();
                            data.enforceNoDataAvail();
                            opNoted(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IAppOpsNotedCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAppOpsNotedCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.app.IAppOpsNotedCallback
            public void opNoted(int op, int uid, String packageName, String attributionTag, int flags, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAppOpsNotedCallback.DESCRIPTOR);
                    _data.writeInt(op);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    _data.writeInt(flags);
                    _data.writeInt(mode);
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
