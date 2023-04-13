package android.companion.virtual.audio;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IAudioRoutingCallback extends IInterface {
    public static final String DESCRIPTOR = "android.companion.virtual.audio.IAudioRoutingCallback";

    void onAppsNeedingAudioRoutingChanged(int[] iArr) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAudioRoutingCallback {
        @Override // android.companion.virtual.audio.IAudioRoutingCallback
        public void onAppsNeedingAudioRoutingChanged(int[] appUids) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAudioRoutingCallback {
        static final int TRANSACTION_onAppsNeedingAudioRoutingChanged = 1;

        public Stub() {
            attachInterface(this, IAudioRoutingCallback.DESCRIPTOR);
        }

        public static IAudioRoutingCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAudioRoutingCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IAudioRoutingCallback)) {
                return (IAudioRoutingCallback) iin;
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
                    return "onAppsNeedingAudioRoutingChanged";
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
                data.enforceInterface(IAudioRoutingCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAudioRoutingCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int[] _arg0 = data.createIntArray();
                            data.enforceNoDataAvail();
                            onAppsNeedingAudioRoutingChanged(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IAudioRoutingCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAudioRoutingCallback.DESCRIPTOR;
            }

            @Override // android.companion.virtual.audio.IAudioRoutingCallback
            public void onAppsNeedingAudioRoutingChanged(int[] appUids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAudioRoutingCallback.DESCRIPTOR);
                    _data.writeIntArray(appUids);
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
