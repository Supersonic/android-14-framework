package android.hardware.camera2;

import android.media.MediaMetrics;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ICameraOfflineSession extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.ICameraOfflineSession";

    void disconnect() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICameraOfflineSession {
        @Override // android.hardware.camera2.ICameraOfflineSession
        public void disconnect() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICameraOfflineSession {
        static final int TRANSACTION_disconnect = 1;

        public Stub() {
            attachInterface(this, ICameraOfflineSession.DESCRIPTOR);
        }

        public static ICameraOfflineSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICameraOfflineSession.DESCRIPTOR);
            if (iin != null && (iin instanceof ICameraOfflineSession)) {
                return (ICameraOfflineSession) iin;
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
                    return MediaMetrics.Value.DISCONNECT;
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
                data.enforceInterface(ICameraOfflineSession.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICameraOfflineSession.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            disconnect();
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements ICameraOfflineSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICameraOfflineSession.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.ICameraOfflineSession
            public void disconnect() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICameraOfflineSession.DESCRIPTOR);
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
