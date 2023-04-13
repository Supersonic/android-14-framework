package android.media.musicrecognition;

import android.media.AudioFormat;
import android.media.musicrecognition.IMusicRecognitionAttributionTagCallback;
import android.media.musicrecognition.IMusicRecognitionServiceCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMusicRecognitionService extends IInterface {
    public static final String DESCRIPTOR = "android.media.musicrecognition.IMusicRecognitionService";

    void getAttributionTag(IMusicRecognitionAttributionTagCallback iMusicRecognitionAttributionTagCallback) throws RemoteException;

    void onAudioStreamStarted(ParcelFileDescriptor parcelFileDescriptor, AudioFormat audioFormat, IMusicRecognitionServiceCallback iMusicRecognitionServiceCallback) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMusicRecognitionService {
        @Override // android.media.musicrecognition.IMusicRecognitionService
        public void onAudioStreamStarted(ParcelFileDescriptor fd, AudioFormat audioFormat, IMusicRecognitionServiceCallback callback) throws RemoteException {
        }

        @Override // android.media.musicrecognition.IMusicRecognitionService
        public void getAttributionTag(IMusicRecognitionAttributionTagCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMusicRecognitionService {
        static final int TRANSACTION_getAttributionTag = 2;
        static final int TRANSACTION_onAudioStreamStarted = 1;

        public Stub() {
            attachInterface(this, IMusicRecognitionService.DESCRIPTOR);
        }

        public static IMusicRecognitionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IMusicRecognitionService.DESCRIPTOR);
            if (iin != null && (iin instanceof IMusicRecognitionService)) {
                return (IMusicRecognitionService) iin;
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
                    return "onAudioStreamStarted";
                case 2:
                    return "getAttributionTag";
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
                data.enforceInterface(IMusicRecognitionService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IMusicRecognitionService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ParcelFileDescriptor _arg0 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            AudioFormat _arg1 = (AudioFormat) data.readTypedObject(AudioFormat.CREATOR);
                            IMusicRecognitionServiceCallback _arg2 = IMusicRecognitionServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onAudioStreamStarted(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            IMusicRecognitionAttributionTagCallback _arg02 = IMusicRecognitionAttributionTagCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getAttributionTag(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMusicRecognitionService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMusicRecognitionService.DESCRIPTOR;
            }

            @Override // android.media.musicrecognition.IMusicRecognitionService
            public void onAudioStreamStarted(ParcelFileDescriptor fd, AudioFormat audioFormat, IMusicRecognitionServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMusicRecognitionService.DESCRIPTOR);
                    _data.writeTypedObject(fd, 0);
                    _data.writeTypedObject(audioFormat, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.musicrecognition.IMusicRecognitionService
            public void getAttributionTag(IMusicRecognitionAttributionTagCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMusicRecognitionService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
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
