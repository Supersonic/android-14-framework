package android.service.dreams;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.dreams.IDreamOverlayCallback;
import android.view.WindowManager;
/* loaded from: classes3.dex */
public interface IDreamOverlayClient extends IInterface {
    public static final String DESCRIPTOR = "android.service.dreams.IDreamOverlayClient";

    void endDream() throws RemoteException;

    void startDream(WindowManager.LayoutParams layoutParams, IDreamOverlayCallback iDreamOverlayCallback, String str, boolean z) throws RemoteException;

    void wakeUp() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IDreamOverlayClient {
        @Override // android.service.dreams.IDreamOverlayClient
        public void startDream(WindowManager.LayoutParams params, IDreamOverlayCallback callback, String dreamComponent, boolean shouldShowComplications) throws RemoteException {
        }

        @Override // android.service.dreams.IDreamOverlayClient
        public void wakeUp() throws RemoteException {
        }

        @Override // android.service.dreams.IDreamOverlayClient
        public void endDream() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IDreamOverlayClient {
        static final int TRANSACTION_endDream = 3;
        static final int TRANSACTION_startDream = 1;
        static final int TRANSACTION_wakeUp = 2;

        public Stub() {
            attachInterface(this, IDreamOverlayClient.DESCRIPTOR);
        }

        public static IDreamOverlayClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDreamOverlayClient.DESCRIPTOR);
            if (iin != null && (iin instanceof IDreamOverlayClient)) {
                return (IDreamOverlayClient) iin;
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
                    return "startDream";
                case 2:
                    return "wakeUp";
                case 3:
                    return "endDream";
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
                data.enforceInterface(IDreamOverlayClient.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDreamOverlayClient.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            WindowManager.LayoutParams _arg0 = (WindowManager.LayoutParams) data.readTypedObject(WindowManager.LayoutParams.CREATOR);
                            IDreamOverlayCallback _arg1 = IDreamOverlayCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg2 = data.readString();
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            startDream(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 2:
                            wakeUp();
                            reply.writeNoException();
                            break;
                        case 3:
                            endDream();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IDreamOverlayClient {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDreamOverlayClient.DESCRIPTOR;
            }

            @Override // android.service.dreams.IDreamOverlayClient
            public void startDream(WindowManager.LayoutParams params, IDreamOverlayCallback callback, String dreamComponent, boolean shouldShowComplications) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDreamOverlayClient.DESCRIPTOR);
                    _data.writeTypedObject(params, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeString(dreamComponent);
                    _data.writeBoolean(shouldShowComplications);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.dreams.IDreamOverlayClient
            public void wakeUp() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDreamOverlayClient.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.dreams.IDreamOverlayClient
            public void endDream() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDreamOverlayClient.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
