package android.service.voice;

import android.media.AudioFormat;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IMicrophoneHotwordDetectionVoiceInteractionCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback";

    void onDetected(HotwordDetectedResult hotwordDetectedResult, AudioFormat audioFormat, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    void onError(DetectorFailure detectorFailure) throws RemoteException;

    void onRejected(HotwordRejectedResult hotwordRejectedResult) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IMicrophoneHotwordDetectionVoiceInteractionCallback {
        @Override // android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback
        public void onDetected(HotwordDetectedResult hotwordDetectedResult, AudioFormat audioFormat, ParcelFileDescriptor audioStream) throws RemoteException {
        }

        @Override // android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback
        public void onError(DetectorFailure detectorFailure) throws RemoteException {
        }

        @Override // android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback
        public void onRejected(HotwordRejectedResult hotwordRejectedResult) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IMicrophoneHotwordDetectionVoiceInteractionCallback {
        static final int TRANSACTION_onDetected = 1;
        static final int TRANSACTION_onError = 2;
        static final int TRANSACTION_onRejected = 3;

        public Stub() {
            attachInterface(this, IMicrophoneHotwordDetectionVoiceInteractionCallback.DESCRIPTOR);
        }

        public static IMicrophoneHotwordDetectionVoiceInteractionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IMicrophoneHotwordDetectionVoiceInteractionCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IMicrophoneHotwordDetectionVoiceInteractionCallback)) {
                return (IMicrophoneHotwordDetectionVoiceInteractionCallback) iin;
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
                    return "onDetected";
                case 2:
                    return "onError";
                case 3:
                    return "onRejected";
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
                data.enforceInterface(IMicrophoneHotwordDetectionVoiceInteractionCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IMicrophoneHotwordDetectionVoiceInteractionCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            HotwordDetectedResult _arg0 = (HotwordDetectedResult) data.readTypedObject(HotwordDetectedResult.CREATOR);
                            AudioFormat _arg1 = (AudioFormat) data.readTypedObject(AudioFormat.CREATOR);
                            ParcelFileDescriptor _arg2 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            onDetected(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            DetectorFailure _arg02 = (DetectorFailure) data.readTypedObject(DetectorFailure.CREATOR);
                            data.enforceNoDataAvail();
                            onError(_arg02);
                            break;
                        case 3:
                            HotwordRejectedResult _arg03 = (HotwordRejectedResult) data.readTypedObject(HotwordRejectedResult.CREATOR);
                            data.enforceNoDataAvail();
                            onRejected(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IMicrophoneHotwordDetectionVoiceInteractionCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMicrophoneHotwordDetectionVoiceInteractionCallback.DESCRIPTOR;
            }

            @Override // android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback
            public void onDetected(HotwordDetectedResult hotwordDetectedResult, AudioFormat audioFormat, ParcelFileDescriptor audioStream) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMicrophoneHotwordDetectionVoiceInteractionCallback.DESCRIPTOR);
                    _data.writeTypedObject(hotwordDetectedResult, 0);
                    _data.writeTypedObject(audioFormat, 0);
                    _data.writeTypedObject(audioStream, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback
            public void onError(DetectorFailure detectorFailure) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMicrophoneHotwordDetectionVoiceInteractionCallback.DESCRIPTOR);
                    _data.writeTypedObject(detectorFailure, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback
            public void onRejected(HotwordRejectedResult hotwordRejectedResult) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMicrophoneHotwordDetectionVoiceInteractionCallback.DESCRIPTOR);
                    _data.writeTypedObject(hotwordRejectedResult, 0);
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
