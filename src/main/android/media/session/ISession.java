package android.media.session;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.media.AudioAttributes;
import android.media.MediaMetadata;
import android.media.session.ISessionController;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.text.TextUtils;
/* loaded from: classes2.dex */
public interface ISession extends IInterface {
    void destroySession() throws RemoteException;

    IBinder getBinderForSetQueue() throws RemoteException;

    ISessionController getController() throws RemoteException;

    void resetQueue() throws RemoteException;

    void sendEvent(String str, Bundle bundle) throws RemoteException;

    void setActive(boolean z) throws RemoteException;

    void setCurrentVolume(int i) throws RemoteException;

    void setExtras(Bundle bundle) throws RemoteException;

    void setFlags(int i) throws RemoteException;

    void setLaunchPendingIntent(PendingIntent pendingIntent) throws RemoteException;

    void setMediaButtonBroadcastReceiver(ComponentName componentName) throws RemoteException;

    void setMediaButtonReceiver(PendingIntent pendingIntent) throws RemoteException;

    void setMetadata(MediaMetadata mediaMetadata, long j, String str) throws RemoteException;

    void setPlaybackState(PlaybackState playbackState) throws RemoteException;

    void setPlaybackToLocal(AudioAttributes audioAttributes) throws RemoteException;

    void setPlaybackToRemote(int i, int i2, String str) throws RemoteException;

    void setQueueTitle(CharSequence charSequence) throws RemoteException;

    void setRatingType(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISession {
        @Override // android.media.session.ISession
        public void sendEvent(String event, Bundle data) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public ISessionController getController() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISession
        public void setFlags(int flags) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setActive(boolean active) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setMediaButtonReceiver(PendingIntent mbr) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setMediaButtonBroadcastReceiver(ComponentName broadcastReceiver) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setLaunchPendingIntent(PendingIntent pi) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void destroySession() throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setMetadata(MediaMetadata metadata, long duration, String metadataDescription) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setPlaybackState(PlaybackState state) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void resetQueue() throws RemoteException {
        }

        @Override // android.media.session.ISession
        public IBinder getBinderForSetQueue() throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISession
        public void setQueueTitle(CharSequence title) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setExtras(Bundle extras) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setRatingType(int type) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setPlaybackToLocal(AudioAttributes attributes) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setPlaybackToRemote(int control, int max, String controlId) throws RemoteException {
        }

        @Override // android.media.session.ISession
        public void setCurrentVolume(int currentVolume) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISession {
        public static final String DESCRIPTOR = "android.media.session.ISession";
        static final int TRANSACTION_destroySession = 8;
        static final int TRANSACTION_getBinderForSetQueue = 12;
        static final int TRANSACTION_getController = 2;
        static final int TRANSACTION_resetQueue = 11;
        static final int TRANSACTION_sendEvent = 1;
        static final int TRANSACTION_setActive = 4;
        static final int TRANSACTION_setCurrentVolume = 18;
        static final int TRANSACTION_setExtras = 14;
        static final int TRANSACTION_setFlags = 3;
        static final int TRANSACTION_setLaunchPendingIntent = 7;
        static final int TRANSACTION_setMediaButtonBroadcastReceiver = 6;
        static final int TRANSACTION_setMediaButtonReceiver = 5;
        static final int TRANSACTION_setMetadata = 9;
        static final int TRANSACTION_setPlaybackState = 10;
        static final int TRANSACTION_setPlaybackToLocal = 16;
        static final int TRANSACTION_setPlaybackToRemote = 17;
        static final int TRANSACTION_setQueueTitle = 13;
        static final int TRANSACTION_setRatingType = 15;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISession)) {
                return (ISession) iin;
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
                    return "sendEvent";
                case 2:
                    return "getController";
                case 3:
                    return "setFlags";
                case 4:
                    return "setActive";
                case 5:
                    return "setMediaButtonReceiver";
                case 6:
                    return "setMediaButtonBroadcastReceiver";
                case 7:
                    return "setLaunchPendingIntent";
                case 8:
                    return "destroySession";
                case 9:
                    return "setMetadata";
                case 10:
                    return "setPlaybackState";
                case 11:
                    return "resetQueue";
                case 12:
                    return "getBinderForSetQueue";
                case 13:
                    return "setQueueTitle";
                case 14:
                    return "setExtras";
                case 15:
                    return "setRatingType";
                case 16:
                    return "setPlaybackToLocal";
                case 17:
                    return "setPlaybackToRemote";
                case 18:
                    return "setCurrentVolume";
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
                            Bundle _arg1 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            sendEvent(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            ISessionController _result = getController();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 3:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            setFlags(_arg02);
                            reply.writeNoException();
                            break;
                        case 4:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setActive(_arg03);
                            reply.writeNoException();
                            break;
                        case 5:
                            PendingIntent _arg04 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            setMediaButtonReceiver(_arg04);
                            reply.writeNoException();
                            break;
                        case 6:
                            ComponentName _arg05 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            setMediaButtonBroadcastReceiver(_arg05);
                            reply.writeNoException();
                            break;
                        case 7:
                            PendingIntent _arg06 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            setLaunchPendingIntent(_arg06);
                            reply.writeNoException();
                            break;
                        case 8:
                            destroySession();
                            reply.writeNoException();
                            break;
                        case 9:
                            MediaMetadata _arg07 = (MediaMetadata) data.readTypedObject(MediaMetadata.CREATOR);
                            long _arg12 = data.readLong();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            setMetadata(_arg07, _arg12, _arg2);
                            reply.writeNoException();
                            break;
                        case 10:
                            PlaybackState _arg08 = (PlaybackState) data.readTypedObject(PlaybackState.CREATOR);
                            data.enforceNoDataAvail();
                            setPlaybackState(_arg08);
                            reply.writeNoException();
                            break;
                        case 11:
                            resetQueue();
                            reply.writeNoException();
                            break;
                        case 12:
                            IBinder _result2 = getBinderForSetQueue();
                            reply.writeNoException();
                            reply.writeStrongBinder(_result2);
                            break;
                        case 13:
                            CharSequence _arg09 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            setQueueTitle(_arg09);
                            reply.writeNoException();
                            break;
                        case 14:
                            Bundle _arg010 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            setExtras(_arg010);
                            reply.writeNoException();
                            break;
                        case 15:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            setRatingType(_arg011);
                            reply.writeNoException();
                            break;
                        case 16:
                            AudioAttributes _arg012 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            setPlaybackToLocal(_arg012);
                            reply.writeNoException();
                            break;
                        case 17:
                            int _arg013 = data.readInt();
                            int _arg13 = data.readInt();
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            setPlaybackToRemote(_arg013, _arg13, _arg22);
                            reply.writeNoException();
                            break;
                        case 18:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            setCurrentVolume(_arg014);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ISession {
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

            @Override // android.media.session.ISession
            public void sendEvent(String event, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(event);
                    _data.writeTypedObject(data, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public ISessionController getController() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ISessionController _result = ISessionController.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setFlags(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setActive(boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(active);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setMediaButtonReceiver(PendingIntent mbr) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(mbr, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setMediaButtonBroadcastReceiver(ComponentName broadcastReceiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(broadcastReceiver, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setLaunchPendingIntent(PendingIntent pi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(pi, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void destroySession() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setMetadata(MediaMetadata metadata, long duration, String metadataDescription) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(metadata, 0);
                    _data.writeLong(duration);
                    _data.writeString(metadataDescription);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setPlaybackState(PlaybackState state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(state, 0);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void resetQueue() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public IBinder getBinderForSetQueue() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setQueueTitle(CharSequence title) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (title != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(title, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setExtras(Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setRatingType(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setPlaybackToLocal(AudioAttributes attributes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(attributes, 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setPlaybackToRemote(int control, int max, String controlId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(control);
                    _data.writeInt(max);
                    _data.writeString(controlId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISession
            public void setCurrentVolume(int currentVolume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(currentVolume);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 17;
        }
    }
}
