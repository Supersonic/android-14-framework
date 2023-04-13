package android.service.translation;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.translation.TranslationResponse;
/* loaded from: classes3.dex */
public interface ITranslationCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.translation.ITranslationCallback";

    void onTranslationResponse(TranslationResponse translationResponse) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITranslationCallback {
        @Override // android.service.translation.ITranslationCallback
        public void onTranslationResponse(TranslationResponse translationResponse) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITranslationCallback {
        static final int TRANSACTION_onTranslationResponse = 1;

        public Stub() {
            attachInterface(this, ITranslationCallback.DESCRIPTOR);
        }

        public static ITranslationCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITranslationCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ITranslationCallback)) {
                return (ITranslationCallback) iin;
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
                    return "onTranslationResponse";
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
                data.enforceInterface(ITranslationCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITranslationCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            TranslationResponse _arg0 = (TranslationResponse) data.readTypedObject(TranslationResponse.CREATOR);
                            data.enforceNoDataAvail();
                            onTranslationResponse(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITranslationCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITranslationCallback.DESCRIPTOR;
            }

            @Override // android.service.translation.ITranslationCallback
            public void onTranslationResponse(TranslationResponse translationResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationCallback.DESCRIPTOR);
                    _data.writeTypedObject(translationResponse, 0);
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
