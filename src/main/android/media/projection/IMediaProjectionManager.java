package android.media.projection;

import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionWatcherCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.ContentRecordingSession;
/* loaded from: classes2.dex */
public interface IMediaProjectionManager extends IInterface {
    void addCallback(IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) throws RemoteException;

    IMediaProjection createProjection(int i, String str, int i2, boolean z) throws RemoteException;

    MediaProjectionInfo getActiveProjectionInfo() throws RemoteException;

    boolean hasProjectionPermission(int i, String str) throws RemoteException;

    boolean isCurrentProjection(IMediaProjection iMediaProjection) throws RemoteException;

    void notifyActiveProjectionCapturedContentResized(int i, int i2) throws RemoteException;

    void notifyActiveProjectionCapturedContentVisibilityChanged(boolean z) throws RemoteException;

    void removeCallback(IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) throws RemoteException;

    void setContentRecordingSession(ContentRecordingSession contentRecordingSession, IMediaProjection iMediaProjection) throws RemoteException;

    void stopActiveProjection() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMediaProjectionManager {
        @Override // android.media.projection.IMediaProjectionManager
        public boolean hasProjectionPermission(int uid, String packageName) throws RemoteException {
            return false;
        }

        @Override // android.media.projection.IMediaProjectionManager
        public IMediaProjection createProjection(int uid, String packageName, int type, boolean permanentGrant) throws RemoteException {
            return null;
        }

        @Override // android.media.projection.IMediaProjectionManager
        public boolean isCurrentProjection(IMediaProjection projection) throws RemoteException {
            return false;
        }

        @Override // android.media.projection.IMediaProjectionManager
        public MediaProjectionInfo getActiveProjectionInfo() throws RemoteException {
            return null;
        }

        @Override // android.media.projection.IMediaProjectionManager
        public void stopActiveProjection() throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjectionManager
        public void notifyActiveProjectionCapturedContentResized(int width, int height) throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjectionManager
        public void notifyActiveProjectionCapturedContentVisibilityChanged(boolean isVisible) throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjectionManager
        public void addCallback(IMediaProjectionWatcherCallback callback) throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjectionManager
        public void removeCallback(IMediaProjectionWatcherCallback callback) throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjectionManager
        public void setContentRecordingSession(ContentRecordingSession incomingSession, IMediaProjection projection) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMediaProjectionManager {
        public static final String DESCRIPTOR = "android.media.projection.IMediaProjectionManager";
        static final int TRANSACTION_addCallback = 8;
        static final int TRANSACTION_createProjection = 2;
        static final int TRANSACTION_getActiveProjectionInfo = 4;
        static final int TRANSACTION_hasProjectionPermission = 1;
        static final int TRANSACTION_isCurrentProjection = 3;
        static final int TRANSACTION_notifyActiveProjectionCapturedContentResized = 6;

        /* renamed from: TRANSACTION_notifyActiveProjectionCapturedContentVisibilityChanged */
        static final int f295x2822a1e6 = 7;
        static final int TRANSACTION_removeCallback = 9;
        static final int TRANSACTION_setContentRecordingSession = 10;
        static final int TRANSACTION_stopActiveProjection = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaProjectionManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaProjectionManager)) {
                return (IMediaProjectionManager) iin;
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
                    return "hasProjectionPermission";
                case 2:
                    return "createProjection";
                case 3:
                    return "isCurrentProjection";
                case 4:
                    return "getActiveProjectionInfo";
                case 5:
                    return "stopActiveProjection";
                case 6:
                    return "notifyActiveProjectionCapturedContentResized";
                case 7:
                    return "notifyActiveProjectionCapturedContentVisibilityChanged";
                case 8:
                    return "addCallback";
                case 9:
                    return "removeCallback";
                case 10:
                    return "setContentRecordingSession";
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
                            int _arg0 = data.readInt();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = hasProjectionPermission(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            int _arg2 = data.readInt();
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            IMediaProjection _result2 = createProjection(_arg02, _arg12, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            IMediaProjection _arg03 = IMediaProjection.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result3 = isCurrentProjection(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            MediaProjectionInfo _result4 = getActiveProjectionInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            stopActiveProjection();
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg04 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyActiveProjectionCapturedContentResized(_arg04, _arg13);
                            reply.writeNoException();
                            break;
                        case 7:
                            boolean _arg05 = data.readBoolean();
                            data.enforceNoDataAvail();
                            notifyActiveProjectionCapturedContentVisibilityChanged(_arg05);
                            reply.writeNoException();
                            break;
                        case 8:
                            IMediaProjectionWatcherCallback _arg06 = IMediaProjectionWatcherCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addCallback(_arg06);
                            reply.writeNoException();
                            break;
                        case 9:
                            IMediaProjectionWatcherCallback _arg07 = IMediaProjectionWatcherCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeCallback(_arg07);
                            reply.writeNoException();
                            break;
                        case 10:
                            ContentRecordingSession _arg08 = (ContentRecordingSession) data.readTypedObject(ContentRecordingSession.CREATOR);
                            IMediaProjection _arg14 = IMediaProjection.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setContentRecordingSession(_arg08, _arg14);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IMediaProjectionManager {
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

            @Override // android.media.projection.IMediaProjectionManager
            public boolean hasProjectionPermission(int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionManager
            public IMediaProjection createProjection(int uid, String packageName, int type, boolean permanentGrant) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeInt(type);
                    _data.writeBoolean(permanentGrant);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    IMediaProjection _result = IMediaProjection.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionManager
            public boolean isCurrentProjection(IMediaProjection projection) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(projection);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionManager
            public MediaProjectionInfo getActiveProjectionInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    MediaProjectionInfo _result = (MediaProjectionInfo) _reply.readTypedObject(MediaProjectionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionManager
            public void stopActiveProjection() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionManager
            public void notifyActiveProjectionCapturedContentResized(int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionManager
            public void notifyActiveProjectionCapturedContentVisibilityChanged(boolean isVisible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isVisible);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionManager
            public void addCallback(IMediaProjectionWatcherCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionManager
            public void removeCallback(IMediaProjectionWatcherCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjectionManager
            public void setContentRecordingSession(ContentRecordingSession incomingSession, IMediaProjection projection) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(incomingSession, 0);
                    _data.writeStrongInterface(projection);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
