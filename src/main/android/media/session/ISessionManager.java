package android.media.session;

import android.content.ComponentName;
import android.media.IRemoteSessionCallback;
import android.media.session.IActiveSessionsListener;
import android.media.session.IOnMediaKeyEventDispatchedListener;
import android.media.session.IOnMediaKeyEventSessionChangedListener;
import android.media.session.IOnMediaKeyListener;
import android.media.session.IOnVolumeKeyLongPressListener;
import android.media.session.ISession;
import android.media.session.ISession2TokensListener;
import android.media.session.ISessionCallback;
import android.media.session.MediaSession;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.KeyEvent;
import java.util.List;
/* loaded from: classes2.dex */
public interface ISessionManager extends IInterface {
    void addOnMediaKeyEventDispatchedListener(IOnMediaKeyEventDispatchedListener iOnMediaKeyEventDispatchedListener) throws RemoteException;

    void addOnMediaKeyEventSessionChangedListener(IOnMediaKeyEventSessionChangedListener iOnMediaKeyEventSessionChangedListener, String str) throws RemoteException;

    void addSession2TokensListener(ISession2TokensListener iSession2TokensListener, int i) throws RemoteException;

    void addSessionsListener(IActiveSessionsListener iActiveSessionsListener, ComponentName componentName, int i) throws RemoteException;

    ISession createSession(String str, ISessionCallback iSessionCallback, String str2, Bundle bundle, int i) throws RemoteException;

    void dispatchAdjustVolume(String str, String str2, int i, int i2, int i3) throws RemoteException;

    void dispatchMediaKeyEvent(String str, boolean z, KeyEvent keyEvent, boolean z2) throws RemoteException;

    boolean dispatchMediaKeyEventToSessionAsSystemService(String str, KeyEvent keyEvent, MediaSession.Token token) throws RemoteException;

    void dispatchVolumeKeyEvent(String str, String str2, boolean z, KeyEvent keyEvent, int i, boolean z2) throws RemoteException;

    void dispatchVolumeKeyEventToSessionAsSystemService(String str, String str2, KeyEvent keyEvent, MediaSession.Token token) throws RemoteException;

    MediaSession.Token getMediaKeyEventSession(String str) throws RemoteException;

    String getMediaKeyEventSessionPackageName(String str) throws RemoteException;

    int getSessionPolicies(MediaSession.Token token) throws RemoteException;

    List<MediaSession.Token> getSessions(ComponentName componentName, int i) throws RemoteException;

    boolean hasCustomMediaKeyDispatcher(String str) throws RemoteException;

    boolean hasCustomMediaSessionPolicyProvider(String str) throws RemoteException;

    boolean isGlobalPriorityActive() throws RemoteException;

    boolean isTrusted(String str, int i, int i2) throws RemoteException;

    void registerRemoteSessionCallback(IRemoteSessionCallback iRemoteSessionCallback) throws RemoteException;

    void removeOnMediaKeyEventDispatchedListener(IOnMediaKeyEventDispatchedListener iOnMediaKeyEventDispatchedListener) throws RemoteException;

    void removeOnMediaKeyEventSessionChangedListener(IOnMediaKeyEventSessionChangedListener iOnMediaKeyEventSessionChangedListener) throws RemoteException;

    void removeSession2TokensListener(ISession2TokensListener iSession2TokensListener) throws RemoteException;

    void removeSessionsListener(IActiveSessionsListener iActiveSessionsListener) throws RemoteException;

    void setCustomMediaKeyDispatcher(String str) throws RemoteException;

    void setCustomMediaSessionPolicyProvider(String str) throws RemoteException;

    void setOnMediaKeyListener(IOnMediaKeyListener iOnMediaKeyListener) throws RemoteException;

    void setOnVolumeKeyLongPressListener(IOnVolumeKeyLongPressListener iOnVolumeKeyLongPressListener) throws RemoteException;

    void setSessionPolicies(MediaSession.Token token, int i) throws RemoteException;

    void unregisterRemoteSessionCallback(IRemoteSessionCallback iRemoteSessionCallback) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISessionManager {
        @Override // android.media.session.ISessionManager
        public ISession createSession(String packageName, ISessionCallback sessionCb, String tag, Bundle sessionInfo, int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionManager
        public List<MediaSession.Token> getSessions(ComponentName compName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionManager
        public MediaSession.Token getMediaKeyEventSession(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionManager
        public String getMediaKeyEventSessionPackageName(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.media.session.ISessionManager
        public void dispatchMediaKeyEvent(String packageName, boolean asSystemService, KeyEvent keyEvent, boolean needWakeLock) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public boolean dispatchMediaKeyEventToSessionAsSystemService(String packageName, KeyEvent keyEvent, MediaSession.Token sessionToken) throws RemoteException {
            return false;
        }

        @Override // android.media.session.ISessionManager
        public void dispatchVolumeKeyEvent(String packageName, String opPackageName, boolean asSystemService, KeyEvent keyEvent, int stream, boolean musicOnly) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void dispatchVolumeKeyEventToSessionAsSystemService(String packageName, String opPackageName, KeyEvent keyEvent, MediaSession.Token sessionToken) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void dispatchAdjustVolume(String packageName, String opPackageName, int suggestedStream, int delta, int flags) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void addSessionsListener(IActiveSessionsListener listener, ComponentName compName, int userId) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void removeSessionsListener(IActiveSessionsListener listener) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void addSession2TokensListener(ISession2TokensListener listener, int userId) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void removeSession2TokensListener(ISession2TokensListener listener) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void registerRemoteSessionCallback(IRemoteSessionCallback rvc) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void unregisterRemoteSessionCallback(IRemoteSessionCallback rvc) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public boolean isGlobalPriorityActive() throws RemoteException {
            return false;
        }

        @Override // android.media.session.ISessionManager
        public void addOnMediaKeyEventDispatchedListener(IOnMediaKeyEventDispatchedListener listener) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void removeOnMediaKeyEventDispatchedListener(IOnMediaKeyEventDispatchedListener listener) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void addOnMediaKeyEventSessionChangedListener(IOnMediaKeyEventSessionChangedListener listener, String packageName) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void removeOnMediaKeyEventSessionChangedListener(IOnMediaKeyEventSessionChangedListener listener) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void setOnVolumeKeyLongPressListener(IOnVolumeKeyLongPressListener listener) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void setOnMediaKeyListener(IOnMediaKeyListener listener) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public boolean isTrusted(String controllerPackageName, int controllerPid, int controllerUid) throws RemoteException {
            return false;
        }

        @Override // android.media.session.ISessionManager
        public void setCustomMediaKeyDispatcher(String name) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public void setCustomMediaSessionPolicyProvider(String name) throws RemoteException {
        }

        @Override // android.media.session.ISessionManager
        public boolean hasCustomMediaKeyDispatcher(String componentName) throws RemoteException {
            return false;
        }

        @Override // android.media.session.ISessionManager
        public boolean hasCustomMediaSessionPolicyProvider(String componentName) throws RemoteException {
            return false;
        }

        @Override // android.media.session.ISessionManager
        public int getSessionPolicies(MediaSession.Token token) throws RemoteException {
            return 0;
        }

        @Override // android.media.session.ISessionManager
        public void setSessionPolicies(MediaSession.Token token, int policies) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISessionManager {
        public static final String DESCRIPTOR = "android.media.session.ISessionManager";
        static final int TRANSACTION_addOnMediaKeyEventDispatchedListener = 17;
        static final int TRANSACTION_addOnMediaKeyEventSessionChangedListener = 19;
        static final int TRANSACTION_addSession2TokensListener = 12;
        static final int TRANSACTION_addSessionsListener = 10;
        static final int TRANSACTION_createSession = 1;
        static final int TRANSACTION_dispatchAdjustVolume = 9;
        static final int TRANSACTION_dispatchMediaKeyEvent = 5;
        static final int TRANSACTION_dispatchMediaKeyEventToSessionAsSystemService = 6;
        static final int TRANSACTION_dispatchVolumeKeyEvent = 7;
        static final int TRANSACTION_dispatchVolumeKeyEventToSessionAsSystemService = 8;
        static final int TRANSACTION_getMediaKeyEventSession = 3;
        static final int TRANSACTION_getMediaKeyEventSessionPackageName = 4;
        static final int TRANSACTION_getSessionPolicies = 28;
        static final int TRANSACTION_getSessions = 2;
        static final int TRANSACTION_hasCustomMediaKeyDispatcher = 26;
        static final int TRANSACTION_hasCustomMediaSessionPolicyProvider = 27;
        static final int TRANSACTION_isGlobalPriorityActive = 16;
        static final int TRANSACTION_isTrusted = 23;
        static final int TRANSACTION_registerRemoteSessionCallback = 14;
        static final int TRANSACTION_removeOnMediaKeyEventDispatchedListener = 18;
        static final int TRANSACTION_removeOnMediaKeyEventSessionChangedListener = 20;
        static final int TRANSACTION_removeSession2TokensListener = 13;
        static final int TRANSACTION_removeSessionsListener = 11;
        static final int TRANSACTION_setCustomMediaKeyDispatcher = 24;
        static final int TRANSACTION_setCustomMediaSessionPolicyProvider = 25;
        static final int TRANSACTION_setOnMediaKeyListener = 22;
        static final int TRANSACTION_setOnVolumeKeyLongPressListener = 21;
        static final int TRANSACTION_setSessionPolicies = 29;
        static final int TRANSACTION_unregisterRemoteSessionCallback = 15;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISessionManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISessionManager)) {
                return (ISessionManager) iin;
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
                    return "createSession";
                case 2:
                    return "getSessions";
                case 3:
                    return "getMediaKeyEventSession";
                case 4:
                    return "getMediaKeyEventSessionPackageName";
                case 5:
                    return "dispatchMediaKeyEvent";
                case 6:
                    return "dispatchMediaKeyEventToSessionAsSystemService";
                case 7:
                    return "dispatchVolumeKeyEvent";
                case 8:
                    return "dispatchVolumeKeyEventToSessionAsSystemService";
                case 9:
                    return "dispatchAdjustVolume";
                case 10:
                    return "addSessionsListener";
                case 11:
                    return "removeSessionsListener";
                case 12:
                    return "addSession2TokensListener";
                case 13:
                    return "removeSession2TokensListener";
                case 14:
                    return "registerRemoteSessionCallback";
                case 15:
                    return "unregisterRemoteSessionCallback";
                case 16:
                    return "isGlobalPriorityActive";
                case 17:
                    return "addOnMediaKeyEventDispatchedListener";
                case 18:
                    return "removeOnMediaKeyEventDispatchedListener";
                case 19:
                    return "addOnMediaKeyEventSessionChangedListener";
                case 20:
                    return "removeOnMediaKeyEventSessionChangedListener";
                case 21:
                    return "setOnVolumeKeyLongPressListener";
                case 22:
                    return "setOnMediaKeyListener";
                case 23:
                    return "isTrusted";
                case 24:
                    return "setCustomMediaKeyDispatcher";
                case 25:
                    return "setCustomMediaSessionPolicyProvider";
                case 26:
                    return "hasCustomMediaKeyDispatcher";
                case 27:
                    return "hasCustomMediaSessionPolicyProvider";
                case 28:
                    return "getSessionPolicies";
                case 29:
                    return "setSessionPolicies";
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
                            ISessionCallback _arg1 = ISessionCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg2 = data.readString();
                            Bundle _arg3 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            ISession _result = createSession(_arg0, _arg1, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            ComponentName _arg02 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            List<MediaSession.Token> _result2 = getSessions(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeTypedList(_result2, 1);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            MediaSession.Token _result3 = getMediaKeyEventSession(_arg03);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            String _result4 = getMediaKeyEventSessionPackageName(_arg04);
                            reply.writeNoException();
                            reply.writeString(_result4);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            boolean _arg13 = data.readBoolean();
                            KeyEvent _arg22 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            boolean _arg32 = data.readBoolean();
                            data.enforceNoDataAvail();
                            dispatchMediaKeyEvent(_arg05, _arg13, _arg22, _arg32);
                            reply.writeNoException();
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            KeyEvent _arg14 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            MediaSession.Token _arg23 = (MediaSession.Token) data.readTypedObject(MediaSession.Token.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result5 = dispatchMediaKeyEventToSessionAsSystemService(_arg06, _arg14, _arg23);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            String _arg15 = data.readString();
                            boolean _arg24 = data.readBoolean();
                            KeyEvent _arg33 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            int _arg42 = data.readInt();
                            boolean _arg5 = data.readBoolean();
                            data.enforceNoDataAvail();
                            dispatchVolumeKeyEvent(_arg07, _arg15, _arg24, _arg33, _arg42, _arg5);
                            reply.writeNoException();
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            String _arg16 = data.readString();
                            KeyEvent _arg25 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            MediaSession.Token _arg34 = (MediaSession.Token) data.readTypedObject(MediaSession.Token.CREATOR);
                            data.enforceNoDataAvail();
                            dispatchVolumeKeyEventToSessionAsSystemService(_arg08, _arg16, _arg25, _arg34);
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            String _arg17 = data.readString();
                            int _arg26 = data.readInt();
                            int _arg35 = data.readInt();
                            int _arg43 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchAdjustVolume(_arg09, _arg17, _arg26, _arg35, _arg43);
                            reply.writeNoException();
                            break;
                        case 10:
                            IActiveSessionsListener _arg010 = IActiveSessionsListener.Stub.asInterface(data.readStrongBinder());
                            ComponentName _arg18 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            addSessionsListener(_arg010, _arg18, _arg27);
                            reply.writeNoException();
                            break;
                        case 11:
                            IActiveSessionsListener _arg011 = IActiveSessionsListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeSessionsListener(_arg011);
                            reply.writeNoException();
                            break;
                        case 12:
                            ISession2TokensListener _arg012 = ISession2TokensListener.Stub.asInterface(data.readStrongBinder());
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            addSession2TokensListener(_arg012, _arg19);
                            reply.writeNoException();
                            break;
                        case 13:
                            ISession2TokensListener _arg013 = ISession2TokensListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeSession2TokensListener(_arg013);
                            reply.writeNoException();
                            break;
                        case 14:
                            IRemoteSessionCallback _arg014 = IRemoteSessionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerRemoteSessionCallback(_arg014);
                            reply.writeNoException();
                            break;
                        case 15:
                            IRemoteSessionCallback _arg015 = IRemoteSessionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterRemoteSessionCallback(_arg015);
                            reply.writeNoException();
                            break;
                        case 16:
                            boolean _result6 = isGlobalPriorityActive();
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 17:
                            IOnMediaKeyEventDispatchedListener _arg016 = IOnMediaKeyEventDispatchedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addOnMediaKeyEventDispatchedListener(_arg016);
                            reply.writeNoException();
                            break;
                        case 18:
                            IOnMediaKeyEventDispatchedListener _arg017 = IOnMediaKeyEventDispatchedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeOnMediaKeyEventDispatchedListener(_arg017);
                            reply.writeNoException();
                            break;
                        case 19:
                            IOnMediaKeyEventSessionChangedListener _arg018 = IOnMediaKeyEventSessionChangedListener.Stub.asInterface(data.readStrongBinder());
                            String _arg110 = data.readString();
                            data.enforceNoDataAvail();
                            addOnMediaKeyEventSessionChangedListener(_arg018, _arg110);
                            reply.writeNoException();
                            break;
                        case 20:
                            IOnMediaKeyEventSessionChangedListener _arg019 = IOnMediaKeyEventSessionChangedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeOnMediaKeyEventSessionChangedListener(_arg019);
                            reply.writeNoException();
                            break;
                        case 21:
                            IOnVolumeKeyLongPressListener _arg020 = IOnVolumeKeyLongPressListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setOnVolumeKeyLongPressListener(_arg020);
                            reply.writeNoException();
                            break;
                        case 22:
                            IOnMediaKeyListener _arg021 = IOnMediaKeyListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setOnMediaKeyListener(_arg021);
                            reply.writeNoException();
                            break;
                        case 23:
                            String _arg022 = data.readString();
                            int _arg111 = data.readInt();
                            int _arg28 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = isTrusted(_arg022, _arg111, _arg28);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 24:
                            String _arg023 = data.readString();
                            data.enforceNoDataAvail();
                            setCustomMediaKeyDispatcher(_arg023);
                            reply.writeNoException();
                            break;
                        case 25:
                            String _arg024 = data.readString();
                            data.enforceNoDataAvail();
                            setCustomMediaSessionPolicyProvider(_arg024);
                            reply.writeNoException();
                            break;
                        case 26:
                            String _arg025 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result8 = hasCustomMediaKeyDispatcher(_arg025);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 27:
                            String _arg026 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result9 = hasCustomMediaSessionPolicyProvider(_arg026);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 28:
                            MediaSession.Token _arg027 = (MediaSession.Token) data.readTypedObject(MediaSession.Token.CREATOR);
                            data.enforceNoDataAvail();
                            int _result10 = getSessionPolicies(_arg027);
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            break;
                        case 29:
                            MediaSession.Token _arg028 = (MediaSession.Token) data.readTypedObject(MediaSession.Token.CREATOR);
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            setSessionPolicies(_arg028, _arg112);
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
        public static class Proxy implements ISessionManager {
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

            @Override // android.media.session.ISessionManager
            public ISession createSession(String packageName, ISessionCallback sessionCb, String tag, Bundle sessionInfo, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(sessionCb);
                    _data.writeString(tag);
                    _data.writeTypedObject(sessionInfo, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ISession _result = ISession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public List<MediaSession.Token> getSessions(ComponentName compName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(compName, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    List<MediaSession.Token> _result = _reply.createTypedArrayList(MediaSession.Token.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public MediaSession.Token getMediaKeyEventSession(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    MediaSession.Token _result = (MediaSession.Token) _reply.readTypedObject(MediaSession.Token.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public String getMediaKeyEventSessionPackageName(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void dispatchMediaKeyEvent(String packageName, boolean asSystemService, KeyEvent keyEvent, boolean needWakeLock) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(asSystemService);
                    _data.writeTypedObject(keyEvent, 0);
                    _data.writeBoolean(needWakeLock);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public boolean dispatchMediaKeyEventToSessionAsSystemService(String packageName, KeyEvent keyEvent, MediaSession.Token sessionToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(keyEvent, 0);
                    _data.writeTypedObject(sessionToken, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void dispatchVolumeKeyEvent(String packageName, String opPackageName, boolean asSystemService, KeyEvent keyEvent, int stream, boolean musicOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(opPackageName);
                    _data.writeBoolean(asSystemService);
                    _data.writeTypedObject(keyEvent, 0);
                    _data.writeInt(stream);
                    _data.writeBoolean(musicOnly);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void dispatchVolumeKeyEventToSessionAsSystemService(String packageName, String opPackageName, KeyEvent keyEvent, MediaSession.Token sessionToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(opPackageName);
                    _data.writeTypedObject(keyEvent, 0);
                    _data.writeTypedObject(sessionToken, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void dispatchAdjustVolume(String packageName, String opPackageName, int suggestedStream, int delta, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(opPackageName);
                    _data.writeInt(suggestedStream);
                    _data.writeInt(delta);
                    _data.writeInt(flags);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void addSessionsListener(IActiveSessionsListener listener, ComponentName compName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeTypedObject(compName, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void removeSessionsListener(IActiveSessionsListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void addSession2TokensListener(ISession2TokensListener listener, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void removeSession2TokensListener(ISession2TokensListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void registerRemoteSessionCallback(IRemoteSessionCallback rvc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(rvc);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void unregisterRemoteSessionCallback(IRemoteSessionCallback rvc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(rvc);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public boolean isGlobalPriorityActive() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void addOnMediaKeyEventDispatchedListener(IOnMediaKeyEventDispatchedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void removeOnMediaKeyEventDispatchedListener(IOnMediaKeyEventDispatchedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void addOnMediaKeyEventSessionChangedListener(IOnMediaKeyEventSessionChangedListener listener, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeString(packageName);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void removeOnMediaKeyEventSessionChangedListener(IOnMediaKeyEventSessionChangedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void setOnVolumeKeyLongPressListener(IOnVolumeKeyLongPressListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void setOnMediaKeyListener(IOnMediaKeyListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public boolean isTrusted(String controllerPackageName, int controllerPid, int controllerUid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(controllerPackageName);
                    _data.writeInt(controllerPid);
                    _data.writeInt(controllerUid);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void setCustomMediaKeyDispatcher(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void setCustomMediaSessionPolicyProvider(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public boolean hasCustomMediaKeyDispatcher(String componentName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(componentName);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public boolean hasCustomMediaSessionPolicyProvider(String componentName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(componentName);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public int getSessionPolicies(MediaSession.Token token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(token, 0);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.session.ISessionManager
            public void setSessionPolicies(MediaSession.Token token, int policies) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(token, 0);
                    _data.writeInt(policies);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 28;
        }
    }
}
