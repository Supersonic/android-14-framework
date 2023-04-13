package android.media.session;

import android.app.PendingIntent;
import android.content.p001pm.ParceledListSlice;
import android.media.MediaMetadata;
import android.media.Rating;
import android.media.p007tv.interactive.TvInteractiveAppService;
import android.media.session.ISessionControllerCallback;
import android.media.session.MediaController;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.KeyEvent;
/* loaded from: classes2.dex */
public interface ISessionController extends IInterface {
    void adjustVolume(String str, String str2, int i, int i2) throws RemoteException;

    void fastForward(String str) throws RemoteException;

    Bundle getExtras() throws RemoteException;

    long getFlags() throws RemoteException;

    PendingIntent getLaunchPendingIntent() throws RemoteException;

    MediaMetadata getMetadata() throws RemoteException;

    String getPackageName() throws RemoteException;

    PlaybackState getPlaybackState() throws RemoteException;

    ParceledListSlice getQueue() throws RemoteException;

    CharSequence getQueueTitle() throws RemoteException;

    int getRatingType() throws RemoteException;

    Bundle getSessionInfo() throws RemoteException;

    String getTag() throws RemoteException;

    MediaController.PlaybackInfo getVolumeAttributes() throws RemoteException;

    void next(String str) throws RemoteException;

    void pause(String str) throws RemoteException;

    void play(String str) throws RemoteException;

    void playFromMediaId(String str, String str2, Bundle bundle) throws RemoteException;

    void playFromSearch(String str, String str2, Bundle bundle) throws RemoteException;

    void playFromUri(String str, Uri uri, Bundle bundle) throws RemoteException;

    void prepare(String str) throws RemoteException;

    void prepareFromMediaId(String str, String str2, Bundle bundle) throws RemoteException;

    void prepareFromSearch(String str, String str2, Bundle bundle) throws RemoteException;

    void prepareFromUri(String str, Uri uri, Bundle bundle) throws RemoteException;

    void previous(String str) throws RemoteException;

    void rate(String str, Rating rating) throws RemoteException;

    void registerCallback(String str, ISessionControllerCallback iSessionControllerCallback) throws RemoteException;

    void rewind(String str) throws RemoteException;

    void seekTo(String str, long j) throws RemoteException;

    void sendCommand(String str, String str2, Bundle bundle, ResultReceiver resultReceiver) throws RemoteException;

    void sendCustomAction(String str, String str2, Bundle bundle) throws RemoteException;

    boolean sendMediaButton(String str, KeyEvent keyEvent) throws RemoteException;

    void setPlaybackSpeed(String str, float f) throws RemoteException;

    void setVolumeTo(String str, String str2, int i, int i2) throws RemoteException;

    void skipToQueueItem(String str, long j) throws RemoteException;

    void stop(String str) throws RemoteException;

    void unregisterCallback(ISessionControllerCallback iSessionControllerCallback) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISessionController {
        @Override // android.media.session.ISessionController
        public void sendCommand(String packageName, String command, Bundle args, ResultReceiver cb) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public boolean sendMediaButton(String packageName, KeyEvent mediaButton) throws RemoteException {
            return false;
        }

        @Override // android.media.session.ISessionController
        public void registerCallback(String packageName, ISessionControllerCallback cb) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void unregisterCallback(ISessionControllerCallback cb) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public String getPackageName() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionController
        public String getTag() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionController
        public Bundle getSessionInfo() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionController
        public PendingIntent getLaunchPendingIntent() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionController
        public long getFlags() throws RemoteException {
            return 0L;
        }

        @Override // android.media.session.ISessionController
        public MediaController.PlaybackInfo getVolumeAttributes() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionController
        public void adjustVolume(String packageName, String opPackageName, int direction, int flags) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void setVolumeTo(String packageName, String opPackageName, int value, int flags) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void prepare(String packageName) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void prepareFromMediaId(String packageName, String mediaId, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void prepareFromSearch(String packageName, String string, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void prepareFromUri(String packageName, Uri uri, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void play(String packageName) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void playFromMediaId(String packageName, String mediaId, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void playFromSearch(String packageName, String string, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void playFromUri(String packageName, Uri uri, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void skipToQueueItem(String packageName, long id) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void pause(String packageName) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void stop(String packageName) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void next(String packageName) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void previous(String packageName) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void fastForward(String packageName) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void rewind(String packageName) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void seekTo(String packageName, long pos) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void rate(String packageName, Rating rating) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void setPlaybackSpeed(String packageName, float speed) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public void sendCustomAction(String packageName, String action, Bundle args) throws RemoteException {
        }

        @Override // android.media.session.ISessionController
        public MediaMetadata getMetadata() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionController
        public PlaybackState getPlaybackState() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionController
        public ParceledListSlice getQueue() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionController
        public CharSequence getQueueTitle() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionController
        public Bundle getExtras() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionController
        public int getRatingType() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISessionController {
        public static final String DESCRIPTOR = "android.media.session.ISessionController";
        static final int TRANSACTION_adjustVolume = 11;
        static final int TRANSACTION_fastForward = 26;
        static final int TRANSACTION_getExtras = 36;
        static final int TRANSACTION_getFlags = 9;
        static final int TRANSACTION_getLaunchPendingIntent = 8;
        static final int TRANSACTION_getMetadata = 32;
        static final int TRANSACTION_getPackageName = 5;
        static final int TRANSACTION_getPlaybackState = 33;
        static final int TRANSACTION_getQueue = 34;
        static final int TRANSACTION_getQueueTitle = 35;
        static final int TRANSACTION_getRatingType = 37;
        static final int TRANSACTION_getSessionInfo = 7;
        static final int TRANSACTION_getTag = 6;
        static final int TRANSACTION_getVolumeAttributes = 10;
        static final int TRANSACTION_next = 24;
        static final int TRANSACTION_pause = 22;
        static final int TRANSACTION_play = 17;
        static final int TRANSACTION_playFromMediaId = 18;
        static final int TRANSACTION_playFromSearch = 19;
        static final int TRANSACTION_playFromUri = 20;
        static final int TRANSACTION_prepare = 13;
        static final int TRANSACTION_prepareFromMediaId = 14;
        static final int TRANSACTION_prepareFromSearch = 15;
        static final int TRANSACTION_prepareFromUri = 16;
        static final int TRANSACTION_previous = 25;
        static final int TRANSACTION_rate = 29;
        static final int TRANSACTION_registerCallback = 3;
        static final int TRANSACTION_rewind = 27;
        static final int TRANSACTION_seekTo = 28;
        static final int TRANSACTION_sendCommand = 1;
        static final int TRANSACTION_sendCustomAction = 31;
        static final int TRANSACTION_sendMediaButton = 2;
        static final int TRANSACTION_setPlaybackSpeed = 30;
        static final int TRANSACTION_setVolumeTo = 12;
        static final int TRANSACTION_skipToQueueItem = 21;
        static final int TRANSACTION_stop = 23;
        static final int TRANSACTION_unregisterCallback = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISessionController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISessionController)) {
                return (ISessionController) iin;
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
                    return "sendCommand";
                case 2:
                    return "sendMediaButton";
                case 3:
                    return "registerCallback";
                case 4:
                    return "unregisterCallback";
                case 5:
                    return "getPackageName";
                case 6:
                    return "getTag";
                case 7:
                    return "getSessionInfo";
                case 8:
                    return "getLaunchPendingIntent";
                case 9:
                    return "getFlags";
                case 10:
                    return "getVolumeAttributes";
                case 11:
                    return "adjustVolume";
                case 12:
                    return "setVolumeTo";
                case 13:
                    return "prepare";
                case 14:
                    return "prepareFromMediaId";
                case 15:
                    return "prepareFromSearch";
                case 16:
                    return "prepareFromUri";
                case 17:
                    return TvInteractiveAppService.TIME_SHIFT_COMMAND_TYPE_PLAY;
                case 18:
                    return "playFromMediaId";
                case 19:
                    return "playFromSearch";
                case 20:
                    return "playFromUri";
                case 21:
                    return "skipToQueueItem";
                case 22:
                    return TvInteractiveAppService.TIME_SHIFT_COMMAND_TYPE_PAUSE;
                case 23:
                    return "stop";
                case 24:
                    return "next";
                case 25:
                    return "previous";
                case 26:
                    return "fastForward";
                case 27:
                    return "rewind";
                case 28:
                    return "seekTo";
                case 29:
                    return TextToSpeech.Engine.KEY_PARAM_RATE;
                case 30:
                    return "setPlaybackSpeed";
                case 31:
                    return "sendCustomAction";
                case 32:
                    return "getMetadata";
                case 33:
                    return "getPlaybackState";
                case 34:
                    return "getQueue";
                case 35:
                    return "getQueueTitle";
                case 36:
                    return "getExtras";
                case 37:
                    return "getRatingType";
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
                            String _arg1 = data.readString();
                            Bundle _arg2 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            ResultReceiver _arg3 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            sendCommand(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            KeyEvent _arg12 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result = sendMediaButton(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            ISessionControllerCallback _arg13 = ISessionControllerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerCallback(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        case 4:
                            ISessionControllerCallback _arg04 = ISessionControllerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterCallback(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            String _result2 = getPackageName();
                            reply.writeNoException();
                            reply.writeString(_result2);
                            break;
                        case 6:
                            String _result3 = getTag();
                            reply.writeNoException();
                            reply.writeString(_result3);
                            break;
                        case 7:
                            Bundle _result4 = getSessionInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 8:
                            PendingIntent _result5 = getLaunchPendingIntent();
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 9:
                            long _result6 = getFlags();
                            reply.writeNoException();
                            reply.writeLong(_result6);
                            break;
                        case 10:
                            MediaController.PlaybackInfo _result7 = getVolumeAttributes();
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 11:
                            String _arg05 = data.readString();
                            String _arg14 = data.readString();
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            adjustVolume(_arg05, _arg14, _arg22, _arg32);
                            reply.writeNoException();
                            break;
                        case 12:
                            String _arg06 = data.readString();
                            String _arg15 = data.readString();
                            int _arg23 = data.readInt();
                            int _arg33 = data.readInt();
                            data.enforceNoDataAvail();
                            setVolumeTo(_arg06, _arg15, _arg23, _arg33);
                            reply.writeNoException();
                            break;
                        case 13:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            prepare(_arg07);
                            reply.writeNoException();
                            break;
                        case 14:
                            String _arg08 = data.readString();
                            String _arg16 = data.readString();
                            Bundle _arg24 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            prepareFromMediaId(_arg08, _arg16, _arg24);
                            reply.writeNoException();
                            break;
                        case 15:
                            String _arg09 = data.readString();
                            String _arg17 = data.readString();
                            Bundle _arg25 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            prepareFromSearch(_arg09, _arg17, _arg25);
                            reply.writeNoException();
                            break;
                        case 16:
                            String _arg010 = data.readString();
                            Uri _arg18 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg26 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            prepareFromUri(_arg010, _arg18, _arg26);
                            reply.writeNoException();
                            break;
                        case 17:
                            String _arg011 = data.readString();
                            data.enforceNoDataAvail();
                            play(_arg011);
                            reply.writeNoException();
                            break;
                        case 18:
                            String _arg012 = data.readString();
                            String _arg19 = data.readString();
                            Bundle _arg27 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            playFromMediaId(_arg012, _arg19, _arg27);
                            reply.writeNoException();
                            break;
                        case 19:
                            String _arg013 = data.readString();
                            String _arg110 = data.readString();
                            Bundle _arg28 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            playFromSearch(_arg013, _arg110, _arg28);
                            reply.writeNoException();
                            break;
                        case 20:
                            String _arg014 = data.readString();
                            Uri _arg111 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg29 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            playFromUri(_arg014, _arg111, _arg29);
                            reply.writeNoException();
                            break;
                        case 21:
                            String _arg015 = data.readString();
                            long _arg112 = data.readLong();
                            data.enforceNoDataAvail();
                            skipToQueueItem(_arg015, _arg112);
                            reply.writeNoException();
                            break;
                        case 22:
                            String _arg016 = data.readString();
                            data.enforceNoDataAvail();
                            pause(_arg016);
                            reply.writeNoException();
                            break;
                        case 23:
                            String _arg017 = data.readString();
                            data.enforceNoDataAvail();
                            stop(_arg017);
                            reply.writeNoException();
                            break;
                        case 24:
                            String _arg018 = data.readString();
                            data.enforceNoDataAvail();
                            next(_arg018);
                            reply.writeNoException();
                            break;
                        case 25:
                            String _arg019 = data.readString();
                            data.enforceNoDataAvail();
                            previous(_arg019);
                            reply.writeNoException();
                            break;
                        case 26:
                            String _arg020 = data.readString();
                            data.enforceNoDataAvail();
                            fastForward(_arg020);
                            reply.writeNoException();
                            break;
                        case 27:
                            String _arg021 = data.readString();
                            data.enforceNoDataAvail();
                            rewind(_arg021);
                            reply.writeNoException();
                            break;
                        case 28:
                            String _arg022 = data.readString();
                            long _arg113 = data.readLong();
                            data.enforceNoDataAvail();
                            seekTo(_arg022, _arg113);
                            reply.writeNoException();
                            break;
                        case 29:
                            String _arg023 = data.readString();
                            Rating _arg114 = (Rating) data.readTypedObject(Rating.CREATOR);
                            data.enforceNoDataAvail();
                            rate(_arg023, _arg114);
                            reply.writeNoException();
                            break;
                        case 30:
                            String _arg024 = data.readString();
                            float _arg115 = data.readFloat();
                            data.enforceNoDataAvail();
                            setPlaybackSpeed(_arg024, _arg115);
                            reply.writeNoException();
                            break;
                        case 31:
                            String _arg025 = data.readString();
                            String _arg116 = data.readString();
                            Bundle _arg210 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            sendCustomAction(_arg025, _arg116, _arg210);
                            reply.writeNoException();
                            break;
                        case 32:
                            MediaMetadata _result8 = getMetadata();
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 33:
                            PlaybackState _result9 = getPlaybackState();
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            break;
                        case 34:
                            ParceledListSlice _result10 = getQueue();
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            break;
                        case 35:
                            CharSequence _result11 = getQueueTitle();
                            reply.writeNoException();
                            if (_result11 != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result11, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        case 36:
                            Bundle _result12 = getExtras();
                            reply.writeNoException();
                            reply.writeTypedObject(_result12, 1);
                            break;
                        case 37:
                            int _result13 = getRatingType();
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ISessionController {
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

            @Override // android.media.session.ISessionController
            public void sendCommand(String packageName, String command, Bundle args, ResultReceiver cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(command);
                    _data.writeTypedObject(args, 0);
                    _data.writeTypedObject(cb, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public boolean sendMediaButton(String packageName, KeyEvent mediaButton) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(mediaButton, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void registerCallback(String packageName, ISessionControllerCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void unregisterCallback(ISessionControllerCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public String getPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public String getTag() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public Bundle getSessionInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public PendingIntent getLaunchPendingIntent() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    PendingIntent _result = (PendingIntent) _reply.readTypedObject(PendingIntent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public long getFlags() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public MediaController.PlaybackInfo getVolumeAttributes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    MediaController.PlaybackInfo _result = (MediaController.PlaybackInfo) _reply.readTypedObject(MediaController.PlaybackInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void adjustVolume(String packageName, String opPackageName, int direction, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(opPackageName);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void setVolumeTo(String packageName, String opPackageName, int value, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(opPackageName);
                    _data.writeInt(value);
                    _data.writeInt(flags);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void prepare(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void prepareFromMediaId(String packageName, String mediaId, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(mediaId);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void prepareFromSearch(String packageName, String string, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(string);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void prepareFromUri(String packageName, Uri uri, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(uri, 0);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void play(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void playFromMediaId(String packageName, String mediaId, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(mediaId);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void playFromSearch(String packageName, String string, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(string);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void playFromUri(String packageName, Uri uri, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(uri, 0);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void skipToQueueItem(String packageName, long id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeLong(id);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void pause(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void stop(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void next(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void previous(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void fastForward(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void rewind(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void seekTo(String packageName, long pos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeLong(pos);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void rate(String packageName, Rating rating) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(rating, 0);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void setPlaybackSpeed(String packageName, float speed) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeFloat(speed);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public void sendCustomAction(String packageName, String action, Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(action);
                    _data.writeTypedObject(args, 0);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public MediaMetadata getMetadata() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    MediaMetadata _result = (MediaMetadata) _reply.readTypedObject(MediaMetadata.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public PlaybackState getPlaybackState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    PlaybackState _result = (PlaybackState) _reply.readTypedObject(PlaybackState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public ParceledListSlice getQueue() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public CharSequence getQueueTitle() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public Bundle getExtras() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionController
            public int getRatingType() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 36;
        }
    }
}
