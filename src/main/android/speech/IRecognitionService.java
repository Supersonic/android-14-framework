package android.speech;

import android.content.AttributionSource;
import android.content.Intent;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.speech.IModelDownloadListener;
import android.speech.IRecognitionListener;
import android.speech.IRecognitionSupportCallback;
/* loaded from: classes3.dex */
public interface IRecognitionService extends IInterface {
    void cancel(IRecognitionListener iRecognitionListener, boolean z) throws RemoteException;

    void checkRecognitionSupport(Intent intent, AttributionSource attributionSource, IRecognitionSupportCallback iRecognitionSupportCallback) throws RemoteException;

    void clearModelDownloadListener(Intent intent, AttributionSource attributionSource) throws RemoteException;

    void setModelDownloadListener(Intent intent, AttributionSource attributionSource, IModelDownloadListener iModelDownloadListener) throws RemoteException;

    void startListening(Intent intent, IRecognitionListener iRecognitionListener, AttributionSource attributionSource) throws RemoteException;

    void stopListening(IRecognitionListener iRecognitionListener) throws RemoteException;

    void triggerModelDownload(Intent intent, AttributionSource attributionSource) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IRecognitionService {
        @Override // android.speech.IRecognitionService
        public void startListening(Intent recognizerIntent, IRecognitionListener listener, AttributionSource attributionSource) throws RemoteException {
        }

        @Override // android.speech.IRecognitionService
        public void stopListening(IRecognitionListener listener) throws RemoteException {
        }

        @Override // android.speech.IRecognitionService
        public void cancel(IRecognitionListener listener, boolean isShutdown) throws RemoteException {
        }

        @Override // android.speech.IRecognitionService
        public void checkRecognitionSupport(Intent recognizerIntent, AttributionSource attributionSource, IRecognitionSupportCallback listener) throws RemoteException {
        }

        @Override // android.speech.IRecognitionService
        public void triggerModelDownload(Intent recognizerIntent, AttributionSource attributionSource) throws RemoteException {
        }

        @Override // android.speech.IRecognitionService
        public void setModelDownloadListener(Intent recognizerIntent, AttributionSource attributionSource, IModelDownloadListener listener) throws RemoteException {
        }

        @Override // android.speech.IRecognitionService
        public void clearModelDownloadListener(Intent recognizerIntent, AttributionSource attributionSource) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IRecognitionService {
        public static final String DESCRIPTOR = "android.speech.IRecognitionService";
        static final int TRANSACTION_cancel = 3;
        static final int TRANSACTION_checkRecognitionSupport = 4;
        static final int TRANSACTION_clearModelDownloadListener = 7;
        static final int TRANSACTION_setModelDownloadListener = 6;
        static final int TRANSACTION_startListening = 1;
        static final int TRANSACTION_stopListening = 2;
        static final int TRANSACTION_triggerModelDownload = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRecognitionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRecognitionService)) {
                return (IRecognitionService) iin;
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
                    return "startListening";
                case 2:
                    return "stopListening";
                case 3:
                    return "cancel";
                case 4:
                    return "checkRecognitionSupport";
                case 5:
                    return "triggerModelDownload";
                case 6:
                    return "setModelDownloadListener";
                case 7:
                    return "clearModelDownloadListener";
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
                            Intent _arg0 = (Intent) data.readTypedObject(Intent.CREATOR);
                            IRecognitionListener _arg1 = IRecognitionListener.Stub.asInterface(data.readStrongBinder());
                            AttributionSource _arg2 = (AttributionSource) data.readTypedObject(AttributionSource.CREATOR);
                            data.enforceNoDataAvail();
                            startListening(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            IRecognitionListener _arg02 = IRecognitionListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            stopListening(_arg02);
                            break;
                        case 3:
                            IRecognitionListener _arg03 = IRecognitionListener.Stub.asInterface(data.readStrongBinder());
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            cancel(_arg03, _arg12);
                            break;
                        case 4:
                            Intent _arg04 = (Intent) data.readTypedObject(Intent.CREATOR);
                            AttributionSource _arg13 = (AttributionSource) data.readTypedObject(AttributionSource.CREATOR);
                            IRecognitionSupportCallback _arg22 = IRecognitionSupportCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            checkRecognitionSupport(_arg04, _arg13, _arg22);
                            break;
                        case 5:
                            Intent _arg05 = (Intent) data.readTypedObject(Intent.CREATOR);
                            AttributionSource _arg14 = (AttributionSource) data.readTypedObject(AttributionSource.CREATOR);
                            data.enforceNoDataAvail();
                            triggerModelDownload(_arg05, _arg14);
                            break;
                        case 6:
                            Intent _arg06 = (Intent) data.readTypedObject(Intent.CREATOR);
                            AttributionSource _arg15 = (AttributionSource) data.readTypedObject(AttributionSource.CREATOR);
                            IModelDownloadListener _arg23 = IModelDownloadListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setModelDownloadListener(_arg06, _arg15, _arg23);
                            break;
                        case 7:
                            Intent _arg07 = (Intent) data.readTypedObject(Intent.CREATOR);
                            AttributionSource _arg16 = (AttributionSource) data.readTypedObject(AttributionSource.CREATOR);
                            data.enforceNoDataAvail();
                            clearModelDownloadListener(_arg07, _arg16);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IRecognitionService {
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

            @Override // android.speech.IRecognitionService
            public void startListening(Intent recognizerIntent, IRecognitionListener listener, AttributionSource attributionSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(recognizerIntent, 0);
                    _data.writeStrongInterface(listener);
                    _data.writeTypedObject(attributionSource, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionService
            public void stopListening(IRecognitionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionService
            public void cancel(IRecognitionListener listener, boolean isShutdown) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeBoolean(isShutdown);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionService
            public void checkRecognitionSupport(Intent recognizerIntent, AttributionSource attributionSource, IRecognitionSupportCallback listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(recognizerIntent, 0);
                    _data.writeTypedObject(attributionSource, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionService
            public void triggerModelDownload(Intent recognizerIntent, AttributionSource attributionSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(recognizerIntent, 0);
                    _data.writeTypedObject(attributionSource, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionService
            public void setModelDownloadListener(Intent recognizerIntent, AttributionSource attributionSource, IModelDownloadListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(recognizerIntent, 0);
                    _data.writeTypedObject(attributionSource, 0);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionService
            public void clearModelDownloadListener(Intent recognizerIntent, AttributionSource attributionSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(recognizerIntent, 0);
                    _data.writeTypedObject(attributionSource, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
