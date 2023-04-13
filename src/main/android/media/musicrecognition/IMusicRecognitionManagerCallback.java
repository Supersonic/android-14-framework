package android.media.musicrecognition;

import android.media.MediaMetadata;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMusicRecognitionManagerCallback extends IInterface {
    public static final String DESCRIPTOR = "android.media.musicrecognition.IMusicRecognitionManagerCallback";

    void onAudioStreamClosed() throws RemoteException;

    void onRecognitionFailed(int i) throws RemoteException;

    void onRecognitionSucceeded(MediaMetadata mediaMetadata, Bundle bundle) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMusicRecognitionManagerCallback {
        @Override // android.media.musicrecognition.IMusicRecognitionManagerCallback
        public void onRecognitionSucceeded(MediaMetadata result, Bundle extras) throws RemoteException {
        }

        @Override // android.media.musicrecognition.IMusicRecognitionManagerCallback
        public void onRecognitionFailed(int failureCode) throws RemoteException {
        }

        @Override // android.media.musicrecognition.IMusicRecognitionManagerCallback
        public void onAudioStreamClosed() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMusicRecognitionManagerCallback {
        static final int TRANSACTION_onAudioStreamClosed = 3;
        static final int TRANSACTION_onRecognitionFailed = 2;
        static final int TRANSACTION_onRecognitionSucceeded = 1;

        public Stub() {
            attachInterface(this, IMusicRecognitionManagerCallback.DESCRIPTOR);
        }

        public static IMusicRecognitionManagerCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IMusicRecognitionManagerCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IMusicRecognitionManagerCallback)) {
                return (IMusicRecognitionManagerCallback) iin;
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
                    return "onRecognitionSucceeded";
                case 2:
                    return "onRecognitionFailed";
                case 3:
                    return "onAudioStreamClosed";
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
                data.enforceInterface(IMusicRecognitionManagerCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IMusicRecognitionManagerCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            MediaMetadata _arg0 = (MediaMetadata) data.readTypedObject(MediaMetadata.CREATOR);
                            Bundle _arg1 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onRecognitionSucceeded(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onRecognitionFailed(_arg02);
                            break;
                        case 3:
                            onAudioStreamClosed();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMusicRecognitionManagerCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMusicRecognitionManagerCallback.DESCRIPTOR;
            }

            @Override // android.media.musicrecognition.IMusicRecognitionManagerCallback
            public void onRecognitionSucceeded(MediaMetadata result, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMusicRecognitionManagerCallback.DESCRIPTOR);
                    _data.writeTypedObject(result, 0);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.musicrecognition.IMusicRecognitionManagerCallback
            public void onRecognitionFailed(int failureCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMusicRecognitionManagerCallback.DESCRIPTOR);
                    _data.writeInt(failureCode);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.musicrecognition.IMusicRecognitionManagerCallback
            public void onAudioStreamClosed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMusicRecognitionManagerCallback.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
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
