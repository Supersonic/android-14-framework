package android.app;

import android.content.p001pm.InstantAppRequestInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.IRemoteCallback;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IInstantAppResolver extends IInterface {
    void getInstantAppIntentFilterList(InstantAppRequestInfo instantAppRequestInfo, IRemoteCallback iRemoteCallback) throws RemoteException;

    void getInstantAppResolveInfoList(InstantAppRequestInfo instantAppRequestInfo, int i, IRemoteCallback iRemoteCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IInstantAppResolver {
        @Override // android.app.IInstantAppResolver
        public void getInstantAppResolveInfoList(InstantAppRequestInfo request, int sequence, IRemoteCallback callback) throws RemoteException {
        }

        @Override // android.app.IInstantAppResolver
        public void getInstantAppIntentFilterList(InstantAppRequestInfo request, IRemoteCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IInstantAppResolver {
        public static final String DESCRIPTOR = "android.app.IInstantAppResolver";
        static final int TRANSACTION_getInstantAppIntentFilterList = 2;
        static final int TRANSACTION_getInstantAppResolveInfoList = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInstantAppResolver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IInstantAppResolver)) {
                return (IInstantAppResolver) iin;
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
                    return "getInstantAppResolveInfoList";
                case 2:
                    return "getInstantAppIntentFilterList";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            InstantAppRequestInfo _arg0 = (InstantAppRequestInfo) data.readTypedObject(InstantAppRequestInfo.CREATOR);
                            int _arg1 = data.readInt();
                            IRemoteCallback _arg2 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getInstantAppResolveInfoList(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            InstantAppRequestInfo _arg02 = (InstantAppRequestInfo) data.readTypedObject(InstantAppRequestInfo.CREATOR);
                            IRemoteCallback _arg12 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getInstantAppIntentFilterList(_arg02, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IInstantAppResolver {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.app.IInstantAppResolver
            public void getInstantAppResolveInfoList(InstantAppRequestInfo request, int sequence, IRemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(sequence);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.IInstantAppResolver
            public void getInstantAppIntentFilterList(InstantAppRequestInfo request, IRemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
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
