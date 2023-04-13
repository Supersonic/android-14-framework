package android.service.voice;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IVisualQueryDetectionVoiceInteractionCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.voice.IVisualQueryDetectionVoiceInteractionCallback";

    void onDetectionFailure(DetectorFailure detectorFailure) throws RemoteException;

    void onQueryDetected(String str) throws RemoteException;

    void onQueryFinished() throws RemoteException;

    void onQueryRejected() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IVisualQueryDetectionVoiceInteractionCallback {
        @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
        public void onQueryDetected(String partialQuery) throws RemoteException {
        }

        @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
        public void onQueryFinished() throws RemoteException {
        }

        @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
        public void onQueryRejected() throws RemoteException {
        }

        @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
        public void onDetectionFailure(DetectorFailure detectorFailure) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IVisualQueryDetectionVoiceInteractionCallback {
        static final int TRANSACTION_onDetectionFailure = 4;
        static final int TRANSACTION_onQueryDetected = 1;
        static final int TRANSACTION_onQueryFinished = 2;
        static final int TRANSACTION_onQueryRejected = 3;

        public Stub() {
            attachInterface(this, IVisualQueryDetectionVoiceInteractionCallback.DESCRIPTOR);
        }

        public static IVisualQueryDetectionVoiceInteractionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IVisualQueryDetectionVoiceInteractionCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IVisualQueryDetectionVoiceInteractionCallback)) {
                return (IVisualQueryDetectionVoiceInteractionCallback) iin;
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
                    return "onQueryDetected";
                case 2:
                    return "onQueryFinished";
                case 3:
                    return "onQueryRejected";
                case 4:
                    return "onDetectionFailure";
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
                data.enforceInterface(IVisualQueryDetectionVoiceInteractionCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IVisualQueryDetectionVoiceInteractionCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            onQueryDetected(_arg0);
                            break;
                        case 2:
                            onQueryFinished();
                            break;
                        case 3:
                            onQueryRejected();
                            break;
                        case 4:
                            DetectorFailure _arg02 = (DetectorFailure) data.readTypedObject(DetectorFailure.CREATOR);
                            data.enforceNoDataAvail();
                            onDetectionFailure(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IVisualQueryDetectionVoiceInteractionCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IVisualQueryDetectionVoiceInteractionCallback.DESCRIPTOR;
            }

            @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
            public void onQueryDetected(String partialQuery) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVisualQueryDetectionVoiceInteractionCallback.DESCRIPTOR);
                    _data.writeString(partialQuery);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
            public void onQueryFinished() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVisualQueryDetectionVoiceInteractionCallback.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
            public void onQueryRejected() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVisualQueryDetectionVoiceInteractionCallback.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
            public void onDetectionFailure(DetectorFailure detectorFailure) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVisualQueryDetectionVoiceInteractionCallback.DESCRIPTOR);
                    _data.writeTypedObject(detectorFailure, 0);
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
