package android.speech.tts;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.speech.tts.ITextToSpeechCallback;
import android.text.TextUtils;
import java.util.List;
/* loaded from: classes3.dex */
public interface ITextToSpeechService extends IInterface {
    String[] getClientDefaultLanguage() throws RemoteException;

    String getDefaultVoiceNameFor(String str, String str2, String str3) throws RemoteException;

    String[] getFeaturesForLanguage(String str, String str2, String str3) throws RemoteException;

    String[] getLanguage() throws RemoteException;

    List<Voice> getVoices() throws RemoteException;

    int isLanguageAvailable(String str, String str2, String str3) throws RemoteException;

    boolean isSpeaking() throws RemoteException;

    int loadLanguage(IBinder iBinder, String str, String str2, String str3) throws RemoteException;

    int loadVoice(IBinder iBinder, String str) throws RemoteException;

    int playAudio(IBinder iBinder, Uri uri, int i, Bundle bundle, String str) throws RemoteException;

    int playSilence(IBinder iBinder, long j, int i, String str) throws RemoteException;

    void setCallback(IBinder iBinder, ITextToSpeechCallback iTextToSpeechCallback) throws RemoteException;

    int speak(IBinder iBinder, CharSequence charSequence, int i, Bundle bundle, String str) throws RemoteException;

    int stop(IBinder iBinder) throws RemoteException;

    int synthesizeToFileDescriptor(IBinder iBinder, CharSequence charSequence, ParcelFileDescriptor parcelFileDescriptor, Bundle bundle, String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITextToSpeechService {
        @Override // android.speech.tts.ITextToSpeechService
        public int speak(IBinder callingInstance, CharSequence text, int queueMode, Bundle params, String utteranceId) throws RemoteException {
            return 0;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int synthesizeToFileDescriptor(IBinder callingInstance, CharSequence text, ParcelFileDescriptor fileDescriptor, Bundle params, String utteranceId) throws RemoteException {
            return 0;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int playAudio(IBinder callingInstance, Uri audioUri, int queueMode, Bundle params, String utteranceId) throws RemoteException {
            return 0;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int playSilence(IBinder callingInstance, long duration, int queueMode, String utteranceId) throws RemoteException {
            return 0;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public boolean isSpeaking() throws RemoteException {
            return false;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int stop(IBinder callingInstance) throws RemoteException {
            return 0;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public String[] getLanguage() throws RemoteException {
            return null;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public String[] getClientDefaultLanguage() throws RemoteException {
            return null;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int isLanguageAvailable(String lang, String country, String variant) throws RemoteException {
            return 0;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public String[] getFeaturesForLanguage(String lang, String country, String variant) throws RemoteException {
            return null;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int loadLanguage(IBinder caller, String lang, String country, String variant) throws RemoteException {
            return 0;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public void setCallback(IBinder caller, ITextToSpeechCallback cb) throws RemoteException {
        }

        @Override // android.speech.tts.ITextToSpeechService
        public List<Voice> getVoices() throws RemoteException {
            return null;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int loadVoice(IBinder caller, String voiceName) throws RemoteException {
            return 0;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public String getDefaultVoiceNameFor(String lang, String country, String variant) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITextToSpeechService {
        public static final String DESCRIPTOR = "android.speech.tts.ITextToSpeechService";
        static final int TRANSACTION_getClientDefaultLanguage = 8;
        static final int TRANSACTION_getDefaultVoiceNameFor = 15;
        static final int TRANSACTION_getFeaturesForLanguage = 10;
        static final int TRANSACTION_getLanguage = 7;
        static final int TRANSACTION_getVoices = 13;
        static final int TRANSACTION_isLanguageAvailable = 9;
        static final int TRANSACTION_isSpeaking = 5;
        static final int TRANSACTION_loadLanguage = 11;
        static final int TRANSACTION_loadVoice = 14;
        static final int TRANSACTION_playAudio = 3;
        static final int TRANSACTION_playSilence = 4;
        static final int TRANSACTION_setCallback = 12;
        static final int TRANSACTION_speak = 1;
        static final int TRANSACTION_stop = 6;
        static final int TRANSACTION_synthesizeToFileDescriptor = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITextToSpeechService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITextToSpeechService)) {
                return (ITextToSpeechService) iin;
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
                    return "speak";
                case 2:
                    return "synthesizeToFileDescriptor";
                case 3:
                    return "playAudio";
                case 4:
                    return "playSilence";
                case 5:
                    return "isSpeaking";
                case 6:
                    return "stop";
                case 7:
                    return "getLanguage";
                case 8:
                    return "getClientDefaultLanguage";
                case 9:
                    return "isLanguageAvailable";
                case 10:
                    return "getFeaturesForLanguage";
                case 11:
                    return "loadLanguage";
                case 12:
                    return "setCallback";
                case 13:
                    return "getVoices";
                case 14:
                    return "loadVoice";
                case 15:
                    return "getDefaultVoiceNameFor";
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
                            IBinder _arg0 = data.readStrongBinder();
                            CharSequence _arg1 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            int _arg2 = data.readInt();
                            Bundle _arg3 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            String _arg4 = data.readString();
                            data.enforceNoDataAvail();
                            int _result = speak(_arg0, _arg1, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            IBinder _arg02 = data.readStrongBinder();
                            CharSequence _arg12 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            ParcelFileDescriptor _arg22 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            Bundle _arg32 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            String _arg42 = data.readString();
                            data.enforceNoDataAvail();
                            int _result2 = synthesizeToFileDescriptor(_arg02, _arg12, _arg22, _arg32, _arg42);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            IBinder _arg03 = data.readStrongBinder();
                            Uri _arg13 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg23 = data.readInt();
                            Bundle _arg33 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            String _arg43 = data.readString();
                            data.enforceNoDataAvail();
                            int _result3 = playAudio(_arg03, _arg13, _arg23, _arg33, _arg43);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 4:
                            IBinder _arg04 = data.readStrongBinder();
                            long _arg14 = data.readLong();
                            int _arg24 = data.readInt();
                            String _arg34 = data.readString();
                            data.enforceNoDataAvail();
                            int _result4 = playSilence(_arg04, _arg14, _arg24, _arg34);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 5:
                            boolean _result5 = isSpeaking();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 6:
                            IBinder _arg05 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            int _result6 = stop(_arg05);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 7:
                            String[] _result7 = getLanguage();
                            reply.writeNoException();
                            reply.writeStringArray(_result7);
                            break;
                        case 8:
                            String[] _result8 = getClientDefaultLanguage();
                            reply.writeNoException();
                            reply.writeStringArray(_result8);
                            break;
                        case 9:
                            String _arg06 = data.readString();
                            String _arg15 = data.readString();
                            String _arg25 = data.readString();
                            data.enforceNoDataAvail();
                            int _result9 = isLanguageAvailable(_arg06, _arg15, _arg25);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            break;
                        case 10:
                            String _arg07 = data.readString();
                            String _arg16 = data.readString();
                            String _arg26 = data.readString();
                            data.enforceNoDataAvail();
                            String[] _result10 = getFeaturesForLanguage(_arg07, _arg16, _arg26);
                            reply.writeNoException();
                            reply.writeStringArray(_result10);
                            break;
                        case 11:
                            IBinder _arg08 = data.readStrongBinder();
                            String _arg17 = data.readString();
                            String _arg27 = data.readString();
                            String _arg35 = data.readString();
                            data.enforceNoDataAvail();
                            int _result11 = loadLanguage(_arg08, _arg17, _arg27, _arg35);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 12:
                            IBinder _arg09 = data.readStrongBinder();
                            ITextToSpeechCallback _arg18 = ITextToSpeechCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setCallback(_arg09, _arg18);
                            reply.writeNoException();
                            break;
                        case 13:
                            List<Voice> _result12 = getVoices();
                            reply.writeNoException();
                            reply.writeTypedList(_result12, 1);
                            break;
                        case 14:
                            IBinder _arg010 = data.readStrongBinder();
                            String _arg19 = data.readString();
                            data.enforceNoDataAvail();
                            int _result13 = loadVoice(_arg010, _arg19);
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            break;
                        case 15:
                            String _arg011 = data.readString();
                            String _arg110 = data.readString();
                            String _arg28 = data.readString();
                            data.enforceNoDataAvail();
                            String _result14 = getDefaultVoiceNameFor(_arg011, _arg110, _arg28);
                            reply.writeNoException();
                            reply.writeString(_result14);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITextToSpeechService {
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

            @Override // android.speech.tts.ITextToSpeechService
            public int speak(IBinder callingInstance, CharSequence text, int queueMode, Bundle params, String utteranceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callingInstance);
                    if (text != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(queueMode);
                    _data.writeTypedObject(params, 0);
                    _data.writeString(utteranceId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public int synthesizeToFileDescriptor(IBinder callingInstance, CharSequence text, ParcelFileDescriptor fileDescriptor, Bundle params, String utteranceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callingInstance);
                    if (text != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeTypedObject(fileDescriptor, 0);
                    _data.writeTypedObject(params, 0);
                    _data.writeString(utteranceId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public int playAudio(IBinder callingInstance, Uri audioUri, int queueMode, Bundle params, String utteranceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callingInstance);
                    _data.writeTypedObject(audioUri, 0);
                    _data.writeInt(queueMode);
                    _data.writeTypedObject(params, 0);
                    _data.writeString(utteranceId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public int playSilence(IBinder callingInstance, long duration, int queueMode, String utteranceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callingInstance);
                    _data.writeLong(duration);
                    _data.writeInt(queueMode);
                    _data.writeString(utteranceId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public boolean isSpeaking() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public int stop(IBinder callingInstance) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callingInstance);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public String[] getLanguage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public String[] getClientDefaultLanguage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public int isLanguageAvailable(String lang, String country, String variant) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(lang);
                    _data.writeString(country);
                    _data.writeString(variant);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public String[] getFeaturesForLanguage(String lang, String country, String variant) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(lang);
                    _data.writeString(country);
                    _data.writeString(variant);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public int loadLanguage(IBinder caller, String lang, String country, String variant) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(caller);
                    _data.writeString(lang);
                    _data.writeString(country);
                    _data.writeString(variant);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public void setCallback(IBinder caller, ITextToSpeechCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(caller);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public List<Voice> getVoices() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    List<Voice> _result = _reply.createTypedArrayList(Voice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public int loadVoice(IBinder caller, String voiceName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(caller);
                    _data.writeString(voiceName);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.speech.tts.ITextToSpeechService
            public String getDefaultVoiceNameFor(String lang, String country, String variant) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(lang);
                    _data.writeString(country);
                    _data.writeString(variant);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 14;
        }
    }
}
