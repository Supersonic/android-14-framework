package android.media.musicrecognition;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMusicRecognitionAttributionTagCallback extends IInterface {
    public static final String DESCRIPTOR = "android.media.musicrecognition.IMusicRecognitionAttributionTagCallback";

    void onAttributionTag(String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMusicRecognitionAttributionTagCallback {
        @Override // android.media.musicrecognition.IMusicRecognitionAttributionTagCallback
        public void onAttributionTag(String attributionTag) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMusicRecognitionAttributionTagCallback {
        static final int TRANSACTION_onAttributionTag = 1;

        public Stub() {
            attachInterface(this, IMusicRecognitionAttributionTagCallback.DESCRIPTOR);
        }

        public static IMusicRecognitionAttributionTagCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IMusicRecognitionAttributionTagCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IMusicRecognitionAttributionTagCallback)) {
                return (IMusicRecognitionAttributionTagCallback) iin;
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
                    return "onAttributionTag";
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
                data.enforceInterface(IMusicRecognitionAttributionTagCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IMusicRecognitionAttributionTagCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            onAttributionTag(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMusicRecognitionAttributionTagCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMusicRecognitionAttributionTagCallback.DESCRIPTOR;
            }

            @Override // android.media.musicrecognition.IMusicRecognitionAttributionTagCallback
            public void onAttributionTag(String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMusicRecognitionAttributionTagCallback.DESCRIPTOR);
                    _data.writeString(attributionTag);
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
