package android.service.translation;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.view.translation.TranslationContext;
import com.android.internal.p028os.IResultReceiver;
/* loaded from: classes3.dex */
public interface ITranslationService extends IInterface {
    public static final String DESCRIPTOR = "android.service.translation.ITranslationService";

    void onConnected(IBinder iBinder) throws RemoteException;

    void onCreateTranslationSession(TranslationContext translationContext, int i, IResultReceiver iResultReceiver) throws RemoteException;

    void onDisconnected() throws RemoteException;

    void onTranslationCapabilitiesRequest(int i, int i2, ResultReceiver resultReceiver) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITranslationService {
        @Override // android.service.translation.ITranslationService
        public void onConnected(IBinder callback) throws RemoteException {
        }

        @Override // android.service.translation.ITranslationService
        public void onDisconnected() throws RemoteException {
        }

        @Override // android.service.translation.ITranslationService
        public void onCreateTranslationSession(TranslationContext translationContext, int sessionId, IResultReceiver receiver) throws RemoteException {
        }

        @Override // android.service.translation.ITranslationService
        public void onTranslationCapabilitiesRequest(int sourceFormat, int targetFormat, ResultReceiver receiver) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITranslationService {
        static final int TRANSACTION_onConnected = 1;
        static final int TRANSACTION_onCreateTranslationSession = 3;
        static final int TRANSACTION_onDisconnected = 2;
        static final int TRANSACTION_onTranslationCapabilitiesRequest = 4;

        public Stub() {
            attachInterface(this, ITranslationService.DESCRIPTOR);
        }

        public static ITranslationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITranslationService.DESCRIPTOR);
            if (iin != null && (iin instanceof ITranslationService)) {
                return (ITranslationService) iin;
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
                    return "onConnected";
                case 2:
                    return "onDisconnected";
                case 3:
                    return "onCreateTranslationSession";
                case 4:
                    return "onTranslationCapabilitiesRequest";
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
                data.enforceInterface(ITranslationService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITranslationService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            onConnected(_arg0);
                            break;
                        case 2:
                            onDisconnected();
                            break;
                        case 3:
                            TranslationContext _arg02 = (TranslationContext) data.readTypedObject(TranslationContext.CREATOR);
                            int _arg1 = data.readInt();
                            IResultReceiver _arg2 = IResultReceiver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onCreateTranslationSession(_arg02, _arg1, _arg2);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            int _arg12 = data.readInt();
                            ResultReceiver _arg22 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            onTranslationCapabilitiesRequest(_arg03, _arg12, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITranslationService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITranslationService.DESCRIPTOR;
            }

            @Override // android.service.translation.ITranslationService
            public void onConnected(IBinder callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationService.DESCRIPTOR);
                    _data.writeStrongBinder(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.translation.ITranslationService
            public void onDisconnected() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationService.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.translation.ITranslationService
            public void onCreateTranslationSession(TranslationContext translationContext, int sessionId, IResultReceiver receiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationService.DESCRIPTOR);
                    _data.writeTypedObject(translationContext, 0);
                    _data.writeInt(sessionId);
                    _data.writeStrongInterface(receiver);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.translation.ITranslationService
            public void onTranslationCapabilitiesRequest(int sourceFormat, int targetFormat, ResultReceiver receiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITranslationService.DESCRIPTOR);
                    _data.writeInt(sourceFormat);
                    _data.writeInt(targetFormat);
                    _data.writeTypedObject(receiver, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
