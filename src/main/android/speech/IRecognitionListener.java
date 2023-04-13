package android.speech;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IRecognitionListener extends IInterface {
    void onBeginningOfSpeech() throws RemoteException;

    void onBufferReceived(byte[] bArr) throws RemoteException;

    void onEndOfSegmentedSession() throws RemoteException;

    void onEndOfSpeech() throws RemoteException;

    void onError(int i) throws RemoteException;

    void onEvent(int i, Bundle bundle) throws RemoteException;

    void onLanguageDetection(Bundle bundle) throws RemoteException;

    void onPartialResults(Bundle bundle) throws RemoteException;

    void onReadyForSpeech(Bundle bundle) throws RemoteException;

    void onResults(Bundle bundle) throws RemoteException;

    void onRmsChanged(float f) throws RemoteException;

    void onSegmentResults(Bundle bundle) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IRecognitionListener {
        @Override // android.speech.IRecognitionListener
        public void onReadyForSpeech(Bundle params) throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onBeginningOfSpeech() throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onRmsChanged(float rmsdB) throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onBufferReceived(byte[] buffer) throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onEndOfSpeech() throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onError(int error) throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onResults(Bundle results) throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onPartialResults(Bundle results) throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onSegmentResults(Bundle results) throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onEndOfSegmentedSession() throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onLanguageDetection(Bundle results) throws RemoteException {
        }

        @Override // android.speech.IRecognitionListener
        public void onEvent(int eventType, Bundle params) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IRecognitionListener {
        public static final String DESCRIPTOR = "android.speech.IRecognitionListener";
        static final int TRANSACTION_onBeginningOfSpeech = 2;
        static final int TRANSACTION_onBufferReceived = 4;
        static final int TRANSACTION_onEndOfSegmentedSession = 10;
        static final int TRANSACTION_onEndOfSpeech = 5;
        static final int TRANSACTION_onError = 6;
        static final int TRANSACTION_onEvent = 12;
        static final int TRANSACTION_onLanguageDetection = 11;
        static final int TRANSACTION_onPartialResults = 8;
        static final int TRANSACTION_onReadyForSpeech = 1;
        static final int TRANSACTION_onResults = 7;
        static final int TRANSACTION_onRmsChanged = 3;
        static final int TRANSACTION_onSegmentResults = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRecognitionListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRecognitionListener)) {
                return (IRecognitionListener) iin;
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
                    return "onReadyForSpeech";
                case 2:
                    return "onBeginningOfSpeech";
                case 3:
                    return "onRmsChanged";
                case 4:
                    return "onBufferReceived";
                case 5:
                    return "onEndOfSpeech";
                case 6:
                    return "onError";
                case 7:
                    return "onResults";
                case 8:
                    return "onPartialResults";
                case 9:
                    return "onSegmentResults";
                case 10:
                    return "onEndOfSegmentedSession";
                case 11:
                    return "onLanguageDetection";
                case 12:
                    return "onEvent";
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
                            Bundle _arg0 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onReadyForSpeech(_arg0);
                            break;
                        case 2:
                            onBeginningOfSpeech();
                            break;
                        case 3:
                            float _arg02 = data.readFloat();
                            data.enforceNoDataAvail();
                            onRmsChanged(_arg02);
                            break;
                        case 4:
                            byte[] _arg03 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onBufferReceived(_arg03);
                            break;
                        case 5:
                            onEndOfSpeech();
                            break;
                        case 6:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            onError(_arg04);
                            break;
                        case 7:
                            Bundle _arg05 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onResults(_arg05);
                            break;
                        case 8:
                            Bundle _arg06 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onPartialResults(_arg06);
                            break;
                        case 9:
                            Bundle _arg07 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onSegmentResults(_arg07);
                            break;
                        case 10:
                            onEndOfSegmentedSession();
                            break;
                        case 11:
                            Bundle _arg08 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onLanguageDetection(_arg08);
                            break;
                        case 12:
                            int _arg09 = data.readInt();
                            Bundle _arg1 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onEvent(_arg09, _arg1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IRecognitionListener {
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

            @Override // android.speech.IRecognitionListener
            public void onReadyForSpeech(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onBeginningOfSpeech() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onRmsChanged(float rmsdB) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(rmsdB);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onBufferReceived(byte[] buffer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(buffer);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onEndOfSpeech() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onError(int error) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(error);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onResults(Bundle results) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(results, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onPartialResults(Bundle results) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(results, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onSegmentResults(Bundle results) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(results, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onEndOfSegmentedSession() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onLanguageDetection(Bundle results) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(results, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.speech.IRecognitionListener
            public void onEvent(int eventType, Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(eventType);
                    _data.writeTypedObject(params, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 11;
        }
    }
}
