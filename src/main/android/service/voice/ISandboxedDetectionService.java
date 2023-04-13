package android.service.voice;

import android.content.ContentCaptureOptions;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.AudioFormat;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.IRemoteCallback;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.SharedMemory;
import android.service.voice.IDetectorSessionVisualQueryDetectionCallback;
import android.service.voice.IDspHotwordDetectionCallback;
import android.speech.IRecognitionServiceManager;
import android.view.contentcapture.IContentCaptureManager;
/* loaded from: classes3.dex */
public interface ISandboxedDetectionService extends IInterface {
    public static final String DESCRIPTOR = "android.service.voice.ISandboxedDetectionService";

    void detectFromDspSource(SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent, AudioFormat audioFormat, long j, IDspHotwordDetectionCallback iDspHotwordDetectionCallback) throws RemoteException;

    void detectFromMicrophoneSource(ParcelFileDescriptor parcelFileDescriptor, int i, AudioFormat audioFormat, PersistableBundle persistableBundle, IDspHotwordDetectionCallback iDspHotwordDetectionCallback) throws RemoteException;

    void detectWithVisualSignals(IDetectorSessionVisualQueryDetectionCallback iDetectorSessionVisualQueryDetectionCallback) throws RemoteException;

    void ping(IRemoteCallback iRemoteCallback) throws RemoteException;

    void stopDetection() throws RemoteException;

    void updateAudioFlinger(IBinder iBinder) throws RemoteException;

    void updateContentCaptureManager(IContentCaptureManager iContentCaptureManager, ContentCaptureOptions contentCaptureOptions) throws RemoteException;

    void updateRecognitionServiceManager(IRecognitionServiceManager iRecognitionServiceManager) throws RemoteException;

    void updateState(PersistableBundle persistableBundle, SharedMemory sharedMemory, IRemoteCallback iRemoteCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISandboxedDetectionService {
        @Override // android.service.voice.ISandboxedDetectionService
        public void detectFromDspSource(SoundTrigger.KeyphraseRecognitionEvent event, AudioFormat audioFormat, long timeoutMillis, IDspHotwordDetectionCallback callback) throws RemoteException {
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void detectFromMicrophoneSource(ParcelFileDescriptor audioStream, int audioSource, AudioFormat audioFormat, PersistableBundle options, IDspHotwordDetectionCallback callback) throws RemoteException {
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void detectWithVisualSignals(IDetectorSessionVisualQueryDetectionCallback callback) throws RemoteException {
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateState(PersistableBundle options, SharedMemory sharedMemory, IRemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateAudioFlinger(IBinder audioFlinger) throws RemoteException {
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateContentCaptureManager(IContentCaptureManager contentCaptureManager, ContentCaptureOptions options) throws RemoteException {
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateRecognitionServiceManager(IRecognitionServiceManager recognitionServiceManager) throws RemoteException {
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void ping(IRemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void stopDetection() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISandboxedDetectionService {
        static final int TRANSACTION_detectFromDspSource = 1;
        static final int TRANSACTION_detectFromMicrophoneSource = 2;
        static final int TRANSACTION_detectWithVisualSignals = 3;
        static final int TRANSACTION_ping = 8;
        static final int TRANSACTION_stopDetection = 9;
        static final int TRANSACTION_updateAudioFlinger = 5;
        static final int TRANSACTION_updateContentCaptureManager = 6;
        static final int TRANSACTION_updateRecognitionServiceManager = 7;
        static final int TRANSACTION_updateState = 4;

        public Stub() {
            attachInterface(this, ISandboxedDetectionService.DESCRIPTOR);
        }

        public static ISandboxedDetectionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISandboxedDetectionService.DESCRIPTOR);
            if (iin != null && (iin instanceof ISandboxedDetectionService)) {
                return (ISandboxedDetectionService) iin;
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
                    return "detectFromDspSource";
                case 2:
                    return "detectFromMicrophoneSource";
                case 3:
                    return "detectWithVisualSignals";
                case 4:
                    return "updateState";
                case 5:
                    return "updateAudioFlinger";
                case 6:
                    return "updateContentCaptureManager";
                case 7:
                    return "updateRecognitionServiceManager";
                case 8:
                    return "ping";
                case 9:
                    return "stopDetection";
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
                data.enforceInterface(ISandboxedDetectionService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISandboxedDetectionService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SoundTrigger.KeyphraseRecognitionEvent _arg0 = (SoundTrigger.KeyphraseRecognitionEvent) data.readTypedObject(SoundTrigger.KeyphraseRecognitionEvent.CREATOR);
                            AudioFormat _arg1 = (AudioFormat) data.readTypedObject(AudioFormat.CREATOR);
                            long _arg2 = data.readLong();
                            IDspHotwordDetectionCallback _arg3 = IDspHotwordDetectionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            detectFromDspSource(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            ParcelFileDescriptor _arg02 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            int _arg12 = data.readInt();
                            AudioFormat _arg22 = (AudioFormat) data.readTypedObject(AudioFormat.CREATOR);
                            PersistableBundle _arg32 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            IDspHotwordDetectionCallback _arg4 = IDspHotwordDetectionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            detectFromMicrophoneSource(_arg02, _arg12, _arg22, _arg32, _arg4);
                            break;
                        case 3:
                            IDetectorSessionVisualQueryDetectionCallback _arg03 = IDetectorSessionVisualQueryDetectionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            detectWithVisualSignals(_arg03);
                            break;
                        case 4:
                            PersistableBundle _arg04 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            SharedMemory _arg13 = (SharedMemory) data.readTypedObject(SharedMemory.CREATOR);
                            IRemoteCallback _arg23 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            updateState(_arg04, _arg13, _arg23);
                            break;
                        case 5:
                            IBinder _arg05 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            updateAudioFlinger(_arg05);
                            break;
                        case 6:
                            IContentCaptureManager _arg06 = IContentCaptureManager.Stub.asInterface(data.readStrongBinder());
                            ContentCaptureOptions _arg14 = (ContentCaptureOptions) data.readTypedObject(ContentCaptureOptions.CREATOR);
                            data.enforceNoDataAvail();
                            updateContentCaptureManager(_arg06, _arg14);
                            break;
                        case 7:
                            IRecognitionServiceManager _arg07 = IRecognitionServiceManager.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            updateRecognitionServiceManager(_arg07);
                            break;
                        case 8:
                            IRemoteCallback _arg08 = IRemoteCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ping(_arg08);
                            break;
                        case 9:
                            stopDetection();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISandboxedDetectionService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISandboxedDetectionService.DESCRIPTOR;
            }

            @Override // android.service.voice.ISandboxedDetectionService
            public void detectFromDspSource(SoundTrigger.KeyphraseRecognitionEvent event, AudioFormat audioFormat, long timeoutMillis, IDspHotwordDetectionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISandboxedDetectionService.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    _data.writeTypedObject(audioFormat, 0);
                    _data.writeLong(timeoutMillis);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.ISandboxedDetectionService
            public void detectFromMicrophoneSource(ParcelFileDescriptor audioStream, int audioSource, AudioFormat audioFormat, PersistableBundle options, IDspHotwordDetectionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISandboxedDetectionService.DESCRIPTOR);
                    _data.writeTypedObject(audioStream, 0);
                    _data.writeInt(audioSource);
                    _data.writeTypedObject(audioFormat, 0);
                    _data.writeTypedObject(options, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.ISandboxedDetectionService
            public void detectWithVisualSignals(IDetectorSessionVisualQueryDetectionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISandboxedDetectionService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.ISandboxedDetectionService
            public void updateState(PersistableBundle options, SharedMemory sharedMemory, IRemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISandboxedDetectionService.DESCRIPTOR);
                    _data.writeTypedObject(options, 0);
                    _data.writeTypedObject(sharedMemory, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.ISandboxedDetectionService
            public void updateAudioFlinger(IBinder audioFlinger) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISandboxedDetectionService.DESCRIPTOR);
                    _data.writeStrongBinder(audioFlinger);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.ISandboxedDetectionService
            public void updateContentCaptureManager(IContentCaptureManager contentCaptureManager, ContentCaptureOptions options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISandboxedDetectionService.DESCRIPTOR);
                    _data.writeStrongInterface(contentCaptureManager);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.ISandboxedDetectionService
            public void updateRecognitionServiceManager(IRecognitionServiceManager recognitionServiceManager) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISandboxedDetectionService.DESCRIPTOR);
                    _data.writeStrongInterface(recognitionServiceManager);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.ISandboxedDetectionService
            public void ping(IRemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISandboxedDetectionService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.ISandboxedDetectionService
            public void stopDetection() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISandboxedDetectionService.DESCRIPTOR);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
