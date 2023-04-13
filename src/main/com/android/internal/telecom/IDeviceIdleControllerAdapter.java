package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IDeviceIdleControllerAdapter extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telecom.IDeviceIdleControllerAdapter";

    void exemptAppTemporarilyForEvent(String str, long j, int i, String str2) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IDeviceIdleControllerAdapter {
        @Override // com.android.internal.telecom.IDeviceIdleControllerAdapter
        public void exemptAppTemporarilyForEvent(String packageName, long duration, int userHandle, String reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IDeviceIdleControllerAdapter {
        static final int TRANSACTION_exemptAppTemporarilyForEvent = 1;

        public Stub() {
            attachInterface(this, IDeviceIdleControllerAdapter.DESCRIPTOR);
        }

        public static IDeviceIdleControllerAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDeviceIdleControllerAdapter.DESCRIPTOR);
            if (iin != null && (iin instanceof IDeviceIdleControllerAdapter)) {
                return (IDeviceIdleControllerAdapter) iin;
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
                    return "exemptAppTemporarilyForEvent";
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
                data.enforceInterface(IDeviceIdleControllerAdapter.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDeviceIdleControllerAdapter.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            long _arg1 = data.readLong();
                            int _arg2 = data.readInt();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            exemptAppTemporarilyForEvent(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IDeviceIdleControllerAdapter {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDeviceIdleControllerAdapter.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.IDeviceIdleControllerAdapter
            public void exemptAppTemporarilyForEvent(String packageName, long duration, int userHandle, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDeviceIdleControllerAdapter.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeLong(duration);
                    _data.writeInt(userHandle);
                    _data.writeString(reason);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
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
