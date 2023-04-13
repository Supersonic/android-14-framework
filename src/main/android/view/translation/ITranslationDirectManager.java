package android.view.translation;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.translation.ITranslationCallback;
/* loaded from: classes4.dex */
public interface ITranslationDirectManager extends IInterface {
    public static final String DESCRIPTOR = "android.view.translation.ITranslationDirectManager";

    void onFinishTranslationSession(int i) throws RemoteException;

    void onTranslationRequest(TranslationRequest translationRequest, int i, ICancellationSignal iCancellationSignal, ITranslationCallback iTranslationCallback) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ITranslationDirectManager {
        @Override // android.view.translation.ITranslationDirectManager
        public void onTranslationRequest(TranslationRequest request, int sessionId, ICancellationSignal transport, ITranslationCallback callback) throws RemoteException {
        }

        @Override // android.view.translation.ITranslationDirectManager
        public void onFinishTranslationSession(int sessionId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ITranslationDirectManager {
        static final int TRANSACTION_onFinishTranslationSession = 2;
        static final int TRANSACTION_onTranslationRequest = 1;

        public Stub() {
            attachInterface(this, ITranslationDirectManager.DESCRIPTOR);
        }

        public static ITranslationDirectManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITranslationDirectManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ITranslationDirectManager)) {
                return (ITranslationDirectManager) iin;
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
                    return "onTranslationRequest";
                case 2:
                    return "onFinishTranslationSession";
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
                data.enforceInterface(ITranslationDirectManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITranslationDirectManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            TranslationRequest _arg0 = (TranslationRequest) data.readTypedObject(TranslationRequest.CREATOR);
                            int _arg1 = data.readInt();
                            ICancellationSignal _arg2 = ICancellationSignal.Stub.asInterface(data.readStrongBinder());
                            ITranslationCallback _arg3 = ITranslationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onTranslationRequest(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onFinishTranslationSession(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements ITranslationDirectManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITranslationDirectManager.DESCRIPTOR;
            }

            @Override // android.view.translation.ITranslationDirectManager
            public void onTranslationRequest(TranslationRequest request, int sessionId, ICancellationSignal transport, ITranslationCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationDirectManager.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(sessionId);
                    _data.writeStrongInterface(transport);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.translation.ITranslationDirectManager
            public void onFinishTranslationSession(int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationDirectManager.DESCRIPTOR);
                    _data.writeInt(sessionId);
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
