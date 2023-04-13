package android.media.session;

import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
/* loaded from: classes2.dex */
public interface ISessionCallback extends IInterface {
    void onAdjustVolume(String str, int i, int i2, int i3) throws RemoteException;

    void onCommand(String str, int i, int i2, String str2, Bundle bundle, ResultReceiver resultReceiver) throws RemoteException;

    void onCustomAction(String str, int i, int i2, String str2, Bundle bundle) throws RemoteException;

    void onFastForward(String str, int i, int i2) throws RemoteException;

    void onMediaButton(String str, int i, int i2, Intent intent, int i3, ResultReceiver resultReceiver) throws RemoteException;

    void onMediaButtonFromController(String str, int i, int i2, Intent intent) throws RemoteException;

    void onNext(String str, int i, int i2) throws RemoteException;

    void onPause(String str, int i, int i2) throws RemoteException;

    void onPlay(String str, int i, int i2) throws RemoteException;

    void onPlayFromMediaId(String str, int i, int i2, String str2, Bundle bundle) throws RemoteException;

    void onPlayFromSearch(String str, int i, int i2, String str2, Bundle bundle) throws RemoteException;

    void onPlayFromUri(String str, int i, int i2, Uri uri, Bundle bundle) throws RemoteException;

    void onPrepare(String str, int i, int i2) throws RemoteException;

    void onPrepareFromMediaId(String str, int i, int i2, String str2, Bundle bundle) throws RemoteException;

    void onPrepareFromSearch(String str, int i, int i2, String str2, Bundle bundle) throws RemoteException;

    void onPrepareFromUri(String str, int i, int i2, Uri uri, Bundle bundle) throws RemoteException;

    void onPrevious(String str, int i, int i2) throws RemoteException;

    void onRate(String str, int i, int i2, Rating rating) throws RemoteException;

    void onRewind(String str, int i, int i2) throws RemoteException;

    void onSeekTo(String str, int i, int i2, long j) throws RemoteException;

    void onSetPlaybackSpeed(String str, int i, int i2, float f) throws RemoteException;

    void onSetVolumeTo(String str, int i, int i2, int i3) throws RemoteException;

    void onSkipToTrack(String str, int i, int i2, long j) throws RemoteException;

    void onStop(String str, int i, int i2) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISessionCallback {
        @Override // android.media.session.ISessionCallback
        public void onCommand(String packageName, int pid, int uid, String command, Bundle args, ResultReceiver cb) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onMediaButton(String packageName, int pid, int uid, Intent mediaButtonIntent, int sequenceNumber, ResultReceiver cb) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onMediaButtonFromController(String packageName, int pid, int uid, Intent mediaButtonIntent) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onPrepare(String packageName, int pid, int uid) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onPrepareFromMediaId(String packageName, int pid, int uid, String mediaId, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onPrepareFromSearch(String packageName, int pid, int uid, String query, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onPrepareFromUri(String packageName, int pid, int uid, Uri uri, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onPlay(String packageName, int pid, int uid) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onPlayFromMediaId(String packageName, int pid, int uid, String mediaId, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onPlayFromSearch(String packageName, int pid, int uid, String query, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onPlayFromUri(String packageName, int pid, int uid, Uri uri, Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onSkipToTrack(String packageName, int pid, int uid, long id) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onPause(String packageName, int pid, int uid) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onStop(String packageName, int pid, int uid) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onNext(String packageName, int pid, int uid) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onPrevious(String packageName, int pid, int uid) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onFastForward(String packageName, int pid, int uid) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onRewind(String packageName, int pid, int uid) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onSeekTo(String packageName, int pid, int uid, long pos) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onRate(String packageName, int pid, int uid, Rating rating) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onSetPlaybackSpeed(String packageName, int pid, int uid, float speed) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onCustomAction(String packageName, int pid, int uid, String action, Bundle args) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onAdjustVolume(String packageName, int pid, int uid, int direction) throws RemoteException {
        }

        @Override // android.media.session.ISessionCallback
        public void onSetVolumeTo(String packageName, int pid, int uid, int value) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISessionCallback {
        public static final String DESCRIPTOR = "android.media.session.ISessionCallback";
        static final int TRANSACTION_onAdjustVolume = 23;
        static final int TRANSACTION_onCommand = 1;
        static final int TRANSACTION_onCustomAction = 22;
        static final int TRANSACTION_onFastForward = 17;
        static final int TRANSACTION_onMediaButton = 2;
        static final int TRANSACTION_onMediaButtonFromController = 3;
        static final int TRANSACTION_onNext = 15;
        static final int TRANSACTION_onPause = 13;
        static final int TRANSACTION_onPlay = 8;
        static final int TRANSACTION_onPlayFromMediaId = 9;
        static final int TRANSACTION_onPlayFromSearch = 10;
        static final int TRANSACTION_onPlayFromUri = 11;
        static final int TRANSACTION_onPrepare = 4;
        static final int TRANSACTION_onPrepareFromMediaId = 5;
        static final int TRANSACTION_onPrepareFromSearch = 6;
        static final int TRANSACTION_onPrepareFromUri = 7;
        static final int TRANSACTION_onPrevious = 16;
        static final int TRANSACTION_onRate = 20;
        static final int TRANSACTION_onRewind = 18;
        static final int TRANSACTION_onSeekTo = 19;
        static final int TRANSACTION_onSetPlaybackSpeed = 21;
        static final int TRANSACTION_onSetVolumeTo = 24;
        static final int TRANSACTION_onSkipToTrack = 12;
        static final int TRANSACTION_onStop = 14;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISessionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISessionCallback)) {
                return (ISessionCallback) iin;
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
                    return "onCommand";
                case 2:
                    return "onMediaButton";
                case 3:
                    return "onMediaButtonFromController";
                case 4:
                    return "onPrepare";
                case 5:
                    return "onPrepareFromMediaId";
                case 6:
                    return "onPrepareFromSearch";
                case 7:
                    return "onPrepareFromUri";
                case 8:
                    return "onPlay";
                case 9:
                    return "onPlayFromMediaId";
                case 10:
                    return "onPlayFromSearch";
                case 11:
                    return "onPlayFromUri";
                case 12:
                    return "onSkipToTrack";
                case 13:
                    return "onPause";
                case 14:
                    return "onStop";
                case 15:
                    return "onNext";
                case 16:
                    return "onPrevious";
                case 17:
                    return "onFastForward";
                case 18:
                    return "onRewind";
                case 19:
                    return "onSeekTo";
                case 20:
                    return "onRate";
                case 21:
                    return "onSetPlaybackSpeed";
                case 22:
                    return "onCustomAction";
                case 23:
                    return "onAdjustVolume";
                case 24:
                    return "onSetVolumeTo";
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
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            String _arg3 = data.readString();
                            Bundle _arg4 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            ResultReceiver _arg5 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            onCommand(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            Intent _arg32 = (Intent) data.readTypedObject(Intent.CREATOR);
                            int _arg42 = data.readInt();
                            ResultReceiver _arg52 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            onMediaButton(_arg02, _arg12, _arg22, _arg32, _arg42, _arg52);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            int _arg13 = data.readInt();
                            int _arg23 = data.readInt();
                            Intent _arg33 = (Intent) data.readTypedObject(Intent.CREATOR);
                            data.enforceNoDataAvail();
                            onMediaButtonFromController(_arg03, _arg13, _arg23, _arg33);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg14 = data.readInt();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            onPrepare(_arg04, _arg14, _arg24);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            int _arg15 = data.readInt();
                            int _arg25 = data.readInt();
                            String _arg34 = data.readString();
                            Bundle _arg43 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onPrepareFromMediaId(_arg05, _arg15, _arg25, _arg34, _arg43);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            int _arg16 = data.readInt();
                            int _arg26 = data.readInt();
                            String _arg35 = data.readString();
                            Bundle _arg44 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onPrepareFromSearch(_arg06, _arg16, _arg26, _arg35, _arg44);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            int _arg17 = data.readInt();
                            int _arg27 = data.readInt();
                            Uri _arg36 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg45 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onPrepareFromUri(_arg07, _arg17, _arg27, _arg36, _arg45);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            int _arg18 = data.readInt();
                            int _arg28 = data.readInt();
                            data.enforceNoDataAvail();
                            onPlay(_arg08, _arg18, _arg28);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            int _arg19 = data.readInt();
                            int _arg29 = data.readInt();
                            String _arg37 = data.readString();
                            Bundle _arg46 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onPlayFromMediaId(_arg09, _arg19, _arg29, _arg37, _arg46);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            int _arg110 = data.readInt();
                            int _arg210 = data.readInt();
                            String _arg38 = data.readString();
                            Bundle _arg47 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onPlayFromSearch(_arg010, _arg110, _arg210, _arg38, _arg47);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            int _arg111 = data.readInt();
                            int _arg211 = data.readInt();
                            Uri _arg39 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg48 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onPlayFromUri(_arg011, _arg111, _arg211, _arg39, _arg48);
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            int _arg112 = data.readInt();
                            int _arg212 = data.readInt();
                            long _arg310 = data.readLong();
                            data.enforceNoDataAvail();
                            onSkipToTrack(_arg012, _arg112, _arg212, _arg310);
                            break;
                        case 13:
                            String _arg013 = data.readString();
                            int _arg113 = data.readInt();
                            int _arg213 = data.readInt();
                            data.enforceNoDataAvail();
                            onPause(_arg013, _arg113, _arg213);
                            break;
                        case 14:
                            String _arg014 = data.readString();
                            int _arg114 = data.readInt();
                            int _arg214 = data.readInt();
                            data.enforceNoDataAvail();
                            onStop(_arg014, _arg114, _arg214);
                            break;
                        case 15:
                            String _arg015 = data.readString();
                            int _arg115 = data.readInt();
                            int _arg215 = data.readInt();
                            data.enforceNoDataAvail();
                            onNext(_arg015, _arg115, _arg215);
                            break;
                        case 16:
                            String _arg016 = data.readString();
                            int _arg116 = data.readInt();
                            int _arg216 = data.readInt();
                            data.enforceNoDataAvail();
                            onPrevious(_arg016, _arg116, _arg216);
                            break;
                        case 17:
                            String _arg017 = data.readString();
                            int _arg117 = data.readInt();
                            int _arg217 = data.readInt();
                            data.enforceNoDataAvail();
                            onFastForward(_arg017, _arg117, _arg217);
                            break;
                        case 18:
                            String _arg018 = data.readString();
                            int _arg118 = data.readInt();
                            int _arg218 = data.readInt();
                            data.enforceNoDataAvail();
                            onRewind(_arg018, _arg118, _arg218);
                            break;
                        case 19:
                            String _arg019 = data.readString();
                            int _arg119 = data.readInt();
                            int _arg219 = data.readInt();
                            long _arg311 = data.readLong();
                            data.enforceNoDataAvail();
                            onSeekTo(_arg019, _arg119, _arg219, _arg311);
                            break;
                        case 20:
                            String _arg020 = data.readString();
                            int _arg120 = data.readInt();
                            int _arg220 = data.readInt();
                            Rating _arg312 = (Rating) data.readTypedObject(Rating.CREATOR);
                            data.enforceNoDataAvail();
                            onRate(_arg020, _arg120, _arg220, _arg312);
                            break;
                        case 21:
                            String _arg021 = data.readString();
                            int _arg121 = data.readInt();
                            int _arg221 = data.readInt();
                            float _arg313 = data.readFloat();
                            data.enforceNoDataAvail();
                            onSetPlaybackSpeed(_arg021, _arg121, _arg221, _arg313);
                            break;
                        case 22:
                            String _arg022 = data.readString();
                            int _arg122 = data.readInt();
                            int _arg222 = data.readInt();
                            String _arg314 = data.readString();
                            Bundle _arg49 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onCustomAction(_arg022, _arg122, _arg222, _arg314, _arg49);
                            break;
                        case 23:
                            String _arg023 = data.readString();
                            int _arg123 = data.readInt();
                            int _arg223 = data.readInt();
                            int _arg315 = data.readInt();
                            data.enforceNoDataAvail();
                            onAdjustVolume(_arg023, _arg123, _arg223, _arg315);
                            break;
                        case 24:
                            String _arg024 = data.readString();
                            int _arg124 = data.readInt();
                            int _arg224 = data.readInt();
                            int _arg316 = data.readInt();
                            data.enforceNoDataAvail();
                            onSetVolumeTo(_arg024, _arg124, _arg224, _arg316);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ISessionCallback {
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

            @Override // android.media.session.ISessionCallback
            public void onCommand(String packageName, int pid, int uid, String command, Bundle args, ResultReceiver cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeString(command);
                    _data.writeTypedObject(args, 0);
                    _data.writeTypedObject(cb, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onMediaButton(String packageName, int pid, int uid, Intent mediaButtonIntent, int sequenceNumber, ResultReceiver cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeTypedObject(mediaButtonIntent, 0);
                    _data.writeInt(sequenceNumber);
                    _data.writeTypedObject(cb, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onMediaButtonFromController(String packageName, int pid, int uid, Intent mediaButtonIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeTypedObject(mediaButtonIntent, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onPrepare(String packageName, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onPrepareFromMediaId(String packageName, int pid, int uid, String mediaId, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeString(mediaId);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onPrepareFromSearch(String packageName, int pid, int uid, String query, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeString(query);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onPrepareFromUri(String packageName, int pid, int uid, Uri uri, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeTypedObject(uri, 0);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onPlay(String packageName, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onPlayFromMediaId(String packageName, int pid, int uid, String mediaId, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeString(mediaId);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onPlayFromSearch(String packageName, int pid, int uid, String query, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeString(query);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onPlayFromUri(String packageName, int pid, int uid, Uri uri, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeTypedObject(uri, 0);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onSkipToTrack(String packageName, int pid, int uid, long id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeLong(id);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onPause(String packageName, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onStop(String packageName, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onNext(String packageName, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onPrevious(String packageName, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onFastForward(String packageName, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onRewind(String packageName, int pid, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onSeekTo(String packageName, int pid, int uid, long pos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeLong(pos);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onRate(String packageName, int pid, int uid, Rating rating) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeTypedObject(rating, 0);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onSetPlaybackSpeed(String packageName, int pid, int uid, float speed) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeFloat(speed);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onCustomAction(String packageName, int pid, int uid, String action, Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeString(action);
                    _data.writeTypedObject(args, 0);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onAdjustVolume(String packageName, int pid, int uid, int direction) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeInt(direction);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionCallback
            public void onSetVolumeTo(String packageName, int pid, int uid, int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(pid);
                    _data.writeInt(uid);
                    _data.writeInt(value);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 23;
        }
    }
}
