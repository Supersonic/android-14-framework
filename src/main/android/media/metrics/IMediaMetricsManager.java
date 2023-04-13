package android.media.metrics;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMediaMetricsManager extends IInterface {
    public static final String DESCRIPTOR = "android.media.metrics.IMediaMetricsManager";

    String getBundleSessionId(int i) throws RemoteException;

    String getEditingSessionId(int i) throws RemoteException;

    String getPlaybackSessionId(int i) throws RemoteException;

    String getRecordingSessionId(int i) throws RemoteException;

    String getTranscodingSessionId(int i) throws RemoteException;

    void releaseSessionId(String str, int i) throws RemoteException;

    void reportBundleMetrics(String str, PersistableBundle persistableBundle, int i) throws RemoteException;

    void reportNetworkEvent(String str, NetworkEvent networkEvent, int i) throws RemoteException;

    void reportPlaybackErrorEvent(String str, PlaybackErrorEvent playbackErrorEvent, int i) throws RemoteException;

    void reportPlaybackMetrics(String str, PlaybackMetrics playbackMetrics, int i) throws RemoteException;

    void reportPlaybackStateEvent(String str, PlaybackStateEvent playbackStateEvent, int i) throws RemoteException;

    void reportTrackChangeEvent(String str, TrackChangeEvent trackChangeEvent, int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMediaMetricsManager {
        @Override // android.media.metrics.IMediaMetricsManager
        public void reportPlaybackMetrics(String sessionId, PlaybackMetrics metrics, int userId) throws RemoteException {
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public String getPlaybackSessionId(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public String getRecordingSessionId(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public void reportNetworkEvent(String sessionId, NetworkEvent event, int userId) throws RemoteException {
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public void reportPlaybackErrorEvent(String sessionId, PlaybackErrorEvent event, int userId) throws RemoteException {
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public void reportPlaybackStateEvent(String sessionId, PlaybackStateEvent event, int userId) throws RemoteException {
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public void reportTrackChangeEvent(String sessionId, TrackChangeEvent event, int userId) throws RemoteException {
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public String getTranscodingSessionId(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public String getEditingSessionId(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public String getBundleSessionId(int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public void reportBundleMetrics(String sessionId, PersistableBundle metrics, int userId) throws RemoteException {
        }

        @Override // android.media.metrics.IMediaMetricsManager
        public void releaseSessionId(String sessionId, int userId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMediaMetricsManager {
        static final int TRANSACTION_getBundleSessionId = 10;
        static final int TRANSACTION_getEditingSessionId = 9;
        static final int TRANSACTION_getPlaybackSessionId = 2;
        static final int TRANSACTION_getRecordingSessionId = 3;
        static final int TRANSACTION_getTranscodingSessionId = 8;
        static final int TRANSACTION_releaseSessionId = 12;
        static final int TRANSACTION_reportBundleMetrics = 11;
        static final int TRANSACTION_reportNetworkEvent = 4;
        static final int TRANSACTION_reportPlaybackErrorEvent = 5;
        static final int TRANSACTION_reportPlaybackMetrics = 1;
        static final int TRANSACTION_reportPlaybackStateEvent = 6;
        static final int TRANSACTION_reportTrackChangeEvent = 7;

        public Stub() {
            attachInterface(this, IMediaMetricsManager.DESCRIPTOR);
        }

        public static IMediaMetricsManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IMediaMetricsManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaMetricsManager)) {
                return (IMediaMetricsManager) iin;
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
                    return "reportPlaybackMetrics";
                case 2:
                    return "getPlaybackSessionId";
                case 3:
                    return "getRecordingSessionId";
                case 4:
                    return "reportNetworkEvent";
                case 5:
                    return "reportPlaybackErrorEvent";
                case 6:
                    return "reportPlaybackStateEvent";
                case 7:
                    return "reportTrackChangeEvent";
                case 8:
                    return "getTranscodingSessionId";
                case 9:
                    return "getEditingSessionId";
                case 10:
                    return "getBundleSessionId";
                case 11:
                    return "reportBundleMetrics";
                case 12:
                    return "releaseSessionId";
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
                data.enforceInterface(IMediaMetricsManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IMediaMetricsManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            PlaybackMetrics _arg1 = (PlaybackMetrics) data.readTypedObject(PlaybackMetrics.CREATOR);
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            reportPlaybackMetrics(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result = getPlaybackSessionId(_arg02);
                            reply.writeNoException();
                            reply.writeString(_result);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result2 = getRecordingSessionId(_arg03);
                            reply.writeNoException();
                            reply.writeString(_result2);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            NetworkEvent _arg12 = (NetworkEvent) data.readTypedObject(NetworkEvent.CREATOR);
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            reportNetworkEvent(_arg04, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            PlaybackErrorEvent _arg13 = (PlaybackErrorEvent) data.readTypedObject(PlaybackErrorEvent.CREATOR);
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            reportPlaybackErrorEvent(_arg05, _arg13, _arg23);
                            reply.writeNoException();
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            PlaybackStateEvent _arg14 = (PlaybackStateEvent) data.readTypedObject(PlaybackStateEvent.CREATOR);
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            reportPlaybackStateEvent(_arg06, _arg14, _arg24);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            TrackChangeEvent _arg15 = (TrackChangeEvent) data.readTypedObject(TrackChangeEvent.CREATOR);
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            reportTrackChangeEvent(_arg07, _arg15, _arg25);
                            reply.writeNoException();
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result3 = getTranscodingSessionId(_arg08);
                            reply.writeNoException();
                            reply.writeString(_result3);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result4 = getEditingSessionId(_arg09);
                            reply.writeNoException();
                            reply.writeString(_result4);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result5 = getBundleSessionId(_arg010);
                            reply.writeNoException();
                            reply.writeString(_result5);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            PersistableBundle _arg16 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            reportBundleMetrics(_arg011, _arg16, _arg26);
                            reply.writeNoException();
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseSessionId(_arg012, _arg17);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMediaMetricsManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMediaMetricsManager.DESCRIPTOR;
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public void reportPlaybackMetrics(String sessionId, PlaybackMetrics metrics, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(metrics, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public String getPlaybackSessionId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public String getRecordingSessionId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public void reportNetworkEvent(String sessionId, NetworkEvent event, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(event, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public void reportPlaybackErrorEvent(String sessionId, PlaybackErrorEvent event, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(event, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public void reportPlaybackStateEvent(String sessionId, PlaybackStateEvent event, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(event, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public void reportTrackChangeEvent(String sessionId, TrackChangeEvent event, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(event, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public String getTranscodingSessionId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public String getEditingSessionId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public String getBundleSessionId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public void reportBundleMetrics(String sessionId, PersistableBundle metrics, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(metrics, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.metrics.IMediaMetricsManager
            public void releaseSessionId(String sessionId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMediaMetricsManager.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
