package android.speech.tts;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ITextToSpeechCallback extends IInterface {
    void onAudioAvailable(String str, byte[] bArr) throws RemoteException;

    void onBeginSynthesis(String str, int i, int i2, int i3) throws RemoteException;

    void onError(String str, int i) throws RemoteException;

    void onRangeStart(String str, int i, int i2, int i3) throws RemoteException;

    void onStart(String str) throws RemoteException;

    void onStop(String str, boolean z) throws RemoteException;

    void onSuccess(String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITextToSpeechCallback {
        @Override // android.speech.tts.ITextToSpeechCallback
        public void onStart(String utteranceId) throws RemoteException {
        }

        @Override // android.speech.tts.ITextToSpeechCallback
        public void onSuccess(String utteranceId) throws RemoteException {
        }

        @Override // android.speech.tts.ITextToSpeechCallback
        public void onStop(String utteranceId, boolean isStarted) throws RemoteException {
        }

        @Override // android.speech.tts.ITextToSpeechCallback
        public void onError(String utteranceId, int errorCode) throws RemoteException {
        }

        @Override // android.speech.tts.ITextToSpeechCallback
        public void onBeginSynthesis(String utteranceId, int sampleRateInHz, int audioFormat, int channelCount) throws RemoteException {
        }

        @Override // android.speech.tts.ITextToSpeechCallback
        public void onAudioAvailable(String utteranceId, byte[] audio) throws RemoteException {
        }

        @Override // android.speech.tts.ITextToSpeechCallback
        public void onRangeStart(String utteranceId, int start, int end, int frame) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITextToSpeechCallback {
        public static final String DESCRIPTOR = "android.speech.tts.ITextToSpeechCallback";
        static final int TRANSACTION_onAudioAvailable = 6;
        static final int TRANSACTION_onBeginSynthesis = 5;
        static final int TRANSACTION_onError = 4;
        static final int TRANSACTION_onRangeStart = 7;
        static final int TRANSACTION_onStart = 1;
        static final int TRANSACTION_onStop = 3;
        static final int TRANSACTION_onSuccess = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITextToSpeechCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITextToSpeechCallback)) {
                return (ITextToSpeechCallback) iin;
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
                    return "onStart";
                case 2:
                    return "onSuccess";
                case 3:
                    return "onStop";
                case 4:
                    return "onError";
                case 5:
                    return "onBeginSynthesis";
                case 6:
                    return "onAudioAvailable";
                case 7:
                    return "onRangeStart";
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
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            onStart(_arg0);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            onSuccess(_arg02);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onStop(_arg03, _arg1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onError(_arg04, _arg12);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            int _arg13 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            onBeginSynthesis(_arg05, _arg13, _arg2, _arg3);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            byte[] _arg14 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onAudioAvailable(_arg06, _arg14);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            int _arg15 = data.readInt();
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            onRangeStart(_arg07, _arg15, _arg22, _arg32);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITextToSpeechCallback {
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

            @Override // android.speech.tts.ITextToSpeechCallback
            public void onStart(String utteranceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(utteranceId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechCallback
            public void onSuccess(String utteranceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(utteranceId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechCallback
            public void onStop(String utteranceId, boolean isStarted) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(utteranceId);
                    _data.writeBoolean(isStarted);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechCallback
            public void onError(String utteranceId, int errorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(utteranceId);
                    _data.writeInt(errorCode);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechCallback
            public void onBeginSynthesis(String utteranceId, int sampleRateInHz, int audioFormat, int channelCount) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(utteranceId);
                    _data.writeInt(sampleRateInHz);
                    _data.writeInt(audioFormat);
                    _data.writeInt(channelCount);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechCallback
            public void onAudioAvailable(String utteranceId, byte[] audio) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(utteranceId);
                    _data.writeByteArray(audio);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechCallback
            public void onRangeStart(String utteranceId, int start, int end, int frame) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(utteranceId);
                    _data.writeInt(start);
                    _data.writeInt(end);
                    _data.writeInt(frame);
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
