package android.media.p007tv.interactive;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.media.tv.interactive.ITvInteractiveAppServiceCallback */
/* loaded from: classes2.dex */
public interface ITvInteractiveAppServiceCallback extends IInterface {
    public static final String DESCRIPTOR = "android.media.tv.interactive.ITvInteractiveAppServiceCallback";

    void onStateChanged(int i, int i2, int i3) throws RemoteException;

    /* renamed from: android.media.tv.interactive.ITvInteractiveAppServiceCallback$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITvInteractiveAppServiceCallback {
        @Override // android.media.p007tv.interactive.ITvInteractiveAppServiceCallback
        public void onStateChanged(int type, int state, int error) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.interactive.ITvInteractiveAppServiceCallback$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITvInteractiveAppServiceCallback {
        static final int TRANSACTION_onStateChanged = 1;

        public Stub() {
            attachInterface(this, ITvInteractiveAppServiceCallback.DESCRIPTOR);
        }

        public static ITvInteractiveAppServiceCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITvInteractiveAppServiceCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ITvInteractiveAppServiceCallback)) {
                return (ITvInteractiveAppServiceCallback) iin;
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
                    return "onStateChanged";
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
                data.enforceInterface(ITvInteractiveAppServiceCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITvInteractiveAppServiceCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            onStateChanged(_arg0, _arg1, _arg2);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* renamed from: android.media.tv.interactive.ITvInteractiveAppServiceCallback$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ITvInteractiveAppServiceCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITvInteractiveAppServiceCallback.DESCRIPTOR;
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppServiceCallback
            public void onStateChanged(int type, int state, int error) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppServiceCallback.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(state);
                    _data.writeInt(error);
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
