package android.media.session;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.media.IRemoteSessionCallback;
import android.media.MediaCommunicationManager;
import android.media.MediaFrameworkPlatformInitializer;
import android.media.Session2Token;
import android.media.session.IActiveSessionsListener;
import android.media.session.IOnMediaKeyEventDispatchedListener;
import android.media.session.IOnMediaKeyEventSessionChangedListener;
import android.media.session.IOnMediaKeyListener;
import android.media.session.IOnVolumeKeyLongPressListener;
import android.media.session.ISession2TokensListener;
import android.media.session.ISessionManager;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes2.dex */
public final class MediaSessionManager {
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int RESULT_MEDIA_KEY_HANDLED = 1;
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int RESULT_MEDIA_KEY_NOT_HANDLED = 0;
    private static final String TAG = "SessionManager";
    private final MediaCommunicationManager mCommunicationManager;
    private Context mContext;
    private MediaSession.Token mCurMediaKeyEventSession;
    private String mCurMediaKeyEventSessionPackage;
    private OnMediaKeyListenerImpl mOnMediaKeyListener;
    private OnVolumeKeyLongPressListenerImpl mOnVolumeKeyLongPressListener;
    private final OnMediaKeyEventDispatchedListenerStub mOnMediaKeyEventDispatchedListenerStub = new OnMediaKeyEventDispatchedListenerStub();
    private final OnMediaKeyEventSessionChangedListenerStub mOnMediaKeyEventSessionChangedListenerStub = new OnMediaKeyEventSessionChangedListenerStub();
    private final RemoteSessionCallbackStub mRemoteSessionCallbackStub = new RemoteSessionCallbackStub();
    private final Object mLock = new Object();
    private final ArrayMap<OnActiveSessionsChangedListener, SessionsChangedWrapper> mListeners = new ArrayMap<>();
    private final ArrayMap<OnSession2TokensChangedListener, Session2TokensChangedWrapper> mSession2TokensListeners = new ArrayMap<>();
    private final Map<OnMediaKeyEventDispatchedListener, Executor> mOnMediaKeyEventDispatchedListeners = new HashMap();
    private final Map<OnMediaKeyEventSessionChangedListener, Executor> mMediaKeyEventSessionChangedCallbacks = new HashMap();
    private final Map<RemoteSessionCallback, Executor> mRemoteSessionCallbacks = new ArrayMap();
    private final ISessionManager mService = ISessionManager.Stub.asInterface(MediaFrameworkPlatformInitializer.getMediaServiceManager().getMediaSessionServiceRegisterer().get());

    /* loaded from: classes2.dex */
    public interface OnActiveSessionsChangedListener {
        void onActiveSessionsChanged(List<MediaController> list);
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public interface OnMediaKeyEventDispatchedListener {
        void onMediaKeyEventDispatched(KeyEvent keyEvent, String str, MediaSession.Token token);
    }

    /* loaded from: classes2.dex */
    public interface OnMediaKeyEventSessionChangedListener {
        void onMediaKeyEventSessionChanged(String str, MediaSession.Token token);
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public interface OnMediaKeyListener {
        boolean onMediaKey(KeyEvent keyEvent);
    }

    /* loaded from: classes2.dex */
    public interface OnSession2TokensChangedListener {
        void onSession2TokensChanged(List<Session2Token> list);
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public interface OnVolumeKeyLongPressListener {
        void onVolumeKeyLongPress(KeyEvent keyEvent);
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    /* loaded from: classes2.dex */
    public interface RemoteSessionCallback {
        void onDefaultRemoteSessionChanged(MediaSession.Token token);

        void onVolumeChanged(MediaSession.Token token, int i);
    }

    public MediaSessionManager(Context context) {
        this.mContext = context;
        this.mCommunicationManager = (MediaCommunicationManager) context.getSystemService(Context.MEDIA_COMMUNICATION_SERVICE);
    }

    public ISession createSession(MediaSession.CallbackStub cbStub, String tag, Bundle sessionInfo) {
        Objects.requireNonNull(cbStub, "cbStub shouldn't be null");
        Objects.requireNonNull(tag, "tag shouldn't be null");
        try {
            return this.mService.createSession(this.mContext.getPackageName(), cbStub, tag, sessionInfo, UserHandle.myUserId());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public void notifySession2Created(Session2Token token) {
    }

    public List<MediaController> getActiveSessions(ComponentName notificationListener) {
        return getActiveSessionsForUser(notificationListener, UserHandle.myUserId());
    }

    public MediaSession.Token getMediaKeyEventSession() {
        try {
            return this.mService.getMediaKeyEventSession(this.mContext.getPackageName());
        } catch (RemoteException ex) {
            Log.m109e(TAG, "Failed to get media key event session", ex);
            return null;
        }
    }

    public String getMediaKeyEventSessionPackageName() {
        try {
            String packageName = this.mService.getMediaKeyEventSessionPackageName(this.mContext.getPackageName());
            return packageName != null ? packageName : "";
        } catch (RemoteException ex) {
            Log.m109e(TAG, "Failed to get media key event session package name", ex);
            return "";
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public List<MediaController> getActiveSessionsForUser(ComponentName notificationListener, UserHandle userHandle) {
        Objects.requireNonNull(userHandle, "userHandle shouldn't be null");
        return getActiveSessionsForUser(notificationListener, userHandle.getIdentifier());
    }

    private List<MediaController> getActiveSessionsForUser(ComponentName notificationListener, int userId) {
        ArrayList<MediaController> controllers = new ArrayList<>();
        try {
            List<MediaSession.Token> tokens = this.mService.getSessions(notificationListener, userId);
            int size = tokens.size();
            for (int i = 0; i < size; i++) {
                MediaController controller = new MediaController(this.mContext, tokens.get(i));
                controllers.add(controller);
            }
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to get active sessions: ", e);
        }
        return controllers;
    }

    public List<Session2Token> getSession2Tokens() {
        return this.mCommunicationManager.getSession2Tokens();
    }

    public void addOnActiveSessionsChangedListener(OnActiveSessionsChangedListener sessionListener, ComponentName notificationListener) {
        addOnActiveSessionsChangedListener(sessionListener, notificationListener, null);
    }

    public void addOnActiveSessionsChangedListener(OnActiveSessionsChangedListener sessionListener, ComponentName notificationListener, Handler handler) {
        addOnActiveSessionsChangedListener(sessionListener, notificationListener, UserHandle.myUserId(), handler == null ? null : new HandlerExecutor(handler));
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void addOnActiveSessionsChangedListener(ComponentName notificationListener, UserHandle userHandle, Executor executor, OnActiveSessionsChangedListener sessionListener) {
        Objects.requireNonNull(userHandle, "userHandle shouldn't be null");
        Objects.requireNonNull(executor, "executor shouldn't be null");
        addOnActiveSessionsChangedListener(sessionListener, notificationListener, userHandle.getIdentifier(), executor);
    }

    private void addOnActiveSessionsChangedListener(OnActiveSessionsChangedListener sessionListener, ComponentName notificationListener, int userId, Executor executor) {
        Objects.requireNonNull(sessionListener, "sessionListener shouldn't be null");
        if (executor == null) {
            executor = new HandlerExecutor(new Handler());
        }
        synchronized (this.mLock) {
            if (this.mListeners.get(sessionListener) != null) {
                Log.m104w(TAG, "Attempted to add session listener twice, ignoring.");
                return;
            }
            SessionsChangedWrapper wrapper = new SessionsChangedWrapper(this.mContext, sessionListener, executor);
            try {
                this.mService.addSessionsListener(wrapper.mStub, notificationListener, userId);
                this.mListeners.put(sessionListener, wrapper);
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error in addOnActiveSessionsChangedListener.", e);
            }
        }
    }

    public void removeOnActiveSessionsChangedListener(OnActiveSessionsChangedListener sessionListener) {
        Objects.requireNonNull(sessionListener, "sessionListener shouldn't be null");
        synchronized (this.mLock) {
            SessionsChangedWrapper wrapper = this.mListeners.remove(sessionListener);
            if (wrapper != null) {
                try {
                    this.mService.removeSessionsListener(wrapper.mStub);
                } catch (RemoteException e) {
                    Log.m109e(TAG, "Error in removeOnActiveSessionsChangedListener.", e);
                }
                wrapper.release();
            }
        }
    }

    public void addOnSession2TokensChangedListener(OnSession2TokensChangedListener listener) {
        addOnSession2TokensChangedListener(UserHandle.myUserId(), listener, new HandlerExecutor(new Handler()));
    }

    public void addOnSession2TokensChangedListener(OnSession2TokensChangedListener listener, Handler handler) {
        Objects.requireNonNull(handler, "handler shouldn't be null");
        addOnSession2TokensChangedListener(UserHandle.myUserId(), listener, new HandlerExecutor(handler));
    }

    public void addOnSession2TokensChangedListener(UserHandle userHandle, OnSession2TokensChangedListener listener, Executor executor) {
        Objects.requireNonNull(userHandle, "userHandle shouldn't be null");
        Objects.requireNonNull(executor, "executor shouldn't be null");
        addOnSession2TokensChangedListener(userHandle.getIdentifier(), listener, executor);
    }

    private void addOnSession2TokensChangedListener(int userId, OnSession2TokensChangedListener listener, Executor executor) {
        Objects.requireNonNull(listener, "listener shouldn't be null");
        synchronized (this.mLock) {
            if (this.mSession2TokensListeners.get(listener) != null) {
                Log.m104w(TAG, "Attempted to add session listener twice, ignoring.");
                return;
            }
            Session2TokensChangedWrapper wrapper = new Session2TokensChangedWrapper(listener, executor);
            try {
                this.mService.addSession2TokensListener(wrapper.getStub(), userId);
                this.mSession2TokensListeners.put(listener, wrapper);
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error in addSessionTokensListener.", e);
                e.rethrowFromSystemServer();
            }
        }
    }

    public void removeOnSession2TokensChangedListener(OnSession2TokensChangedListener listener) {
        Session2TokensChangedWrapper wrapper;
        Objects.requireNonNull(listener, "listener shouldn't be null");
        synchronized (this.mLock) {
            wrapper = this.mSession2TokensListeners.remove(listener);
        }
        if (wrapper != null) {
            try {
                this.mService.removeSession2TokensListener(wrapper.getStub());
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error in removeSessionTokensListener.", e);
                e.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void registerRemoteSessionCallback(Executor executor, RemoteSessionCallback callback) {
        Objects.requireNonNull(executor, "executor shouldn't be null");
        Objects.requireNonNull(callback, "callback shouldn't be null");
        boolean shouldRegisterCallback = false;
        synchronized (this.mLock) {
            int prevCallbackCount = this.mRemoteSessionCallbacks.size();
            this.mRemoteSessionCallbacks.put(callback, executor);
            if (prevCallbackCount == 0 && this.mRemoteSessionCallbacks.size() == 1) {
                shouldRegisterCallback = true;
            }
        }
        if (shouldRegisterCallback) {
            try {
                this.mService.registerRemoteSessionCallback(this.mRemoteSessionCallbackStub);
            } catch (RemoteException e) {
                Log.m109e(TAG, "Failed to register remote volume controller callback", e);
            }
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void unregisterRemoteSessionCallback(RemoteSessionCallback callback) {
        Objects.requireNonNull(callback, "callback shouldn't be null");
        boolean shouldUnregisterCallback = false;
        synchronized (this.mLock) {
            if (this.mRemoteSessionCallbacks.remove(callback) != null && this.mRemoteSessionCallbacks.size() == 0) {
                shouldUnregisterCallback = true;
            }
        }
        if (shouldUnregisterCallback) {
            try {
                this.mService.unregisterRemoteSessionCallback(this.mRemoteSessionCallbackStub);
            } catch (RemoteException e) {
                Log.m109e(TAG, "Failed to unregister remote volume controller callback", e);
            }
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void dispatchMediaKeyEvent(KeyEvent keyEvent, boolean needWakeLock) {
        dispatchMediaKeyEventInternal(keyEvent, false, needWakeLock);
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void dispatchMediaKeyEventAsSystemService(KeyEvent keyEvent) {
        dispatchMediaKeyEventInternal(keyEvent, true, true);
    }

    private void dispatchMediaKeyEventInternal(KeyEvent keyEvent, boolean asSystemService, boolean needWakeLock) {
        Objects.requireNonNull(keyEvent, "keyEvent shouldn't be null");
        try {
            this.mService.dispatchMediaKeyEvent(this.mContext.getPackageName(), asSystemService, keyEvent, needWakeLock);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to send key event.", e);
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public boolean dispatchMediaKeyEventToSessionAsSystemService(KeyEvent keyEvent, MediaSession.Token sessionToken) {
        Objects.requireNonNull(sessionToken, "sessionToken shouldn't be null");
        Objects.requireNonNull(keyEvent, "keyEvent shouldn't be null");
        if (KeyEvent.isMediaSessionKey(keyEvent.getKeyCode())) {
            try {
                return this.mService.dispatchMediaKeyEventToSessionAsSystemService(this.mContext.getPackageName(), keyEvent, sessionToken);
            } catch (RemoteException e) {
                Log.m109e(TAG, "Failed to send key event.", e);
                return false;
            }
        }
        return false;
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void dispatchVolumeKeyEvent(KeyEvent keyEvent, int streamType, boolean musicOnly) {
        dispatchVolumeKeyEventInternal(keyEvent, streamType, musicOnly, false);
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void dispatchVolumeKeyEventAsSystemService(KeyEvent keyEvent, int streamType) {
        dispatchVolumeKeyEventInternal(keyEvent, streamType, false, true);
    }

    private void dispatchVolumeKeyEventInternal(KeyEvent keyEvent, int stream, boolean musicOnly, boolean asSystemService) {
        Objects.requireNonNull(keyEvent, "keyEvent shouldn't be null");
        try {
            this.mService.dispatchVolumeKeyEvent(this.mContext.getPackageName(), this.mContext.getOpPackageName(), asSystemService, keyEvent, stream, musicOnly);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to send volume key event.", e);
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void dispatchVolumeKeyEventToSessionAsSystemService(KeyEvent keyEvent, MediaSession.Token sessionToken) {
        Objects.requireNonNull(sessionToken, "sessionToken shouldn't be null");
        Objects.requireNonNull(keyEvent, "keyEvent shouldn't be null");
        try {
            this.mService.dispatchVolumeKeyEventToSessionAsSystemService(this.mContext.getPackageName(), this.mContext.getOpPackageName(), keyEvent, sessionToken);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Error calling dispatchVolumeKeyEventAsSystemService", e);
        }
    }

    public void dispatchAdjustVolume(int suggestedStream, int direction, int flags) {
        try {
            this.mService.dispatchAdjustVolume(this.mContext.getPackageName(), this.mContext.getOpPackageName(), suggestedStream, direction, flags);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to send adjust volume.", e);
        }
    }

    public boolean isTrustedForMediaControl(RemoteUserInfo userInfo) {
        Objects.requireNonNull(userInfo, "userInfo shouldn't be null");
        if (userInfo.getPackageName() == null) {
            return false;
        }
        try {
            return this.mService.isTrusted(userInfo.getPackageName(), userInfo.getPid(), userInfo.getUid());
        } catch (RemoteException e) {
            Log.wtf(TAG, "Cannot communicate with the service.", e);
            return false;
        }
    }

    public boolean isGlobalPriorityActive() {
        try {
            return this.mService.isGlobalPriorityActive();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to check if the global priority is active.", e);
            return false;
        }
    }

    @SystemApi
    public void setOnVolumeKeyLongPressListener(OnVolumeKeyLongPressListener listener, Handler handler) {
        synchronized (this.mLock) {
            try {
                try {
                    if (listener == null) {
                        this.mOnVolumeKeyLongPressListener = null;
                        this.mService.setOnVolumeKeyLongPressListener(null);
                    } else {
                        if (handler == null) {
                            handler = new Handler();
                        }
                        OnVolumeKeyLongPressListenerImpl onVolumeKeyLongPressListenerImpl = new OnVolumeKeyLongPressListenerImpl(listener, handler);
                        this.mOnVolumeKeyLongPressListener = onVolumeKeyLongPressListenerImpl;
                        this.mService.setOnVolumeKeyLongPressListener(onVolumeKeyLongPressListenerImpl);
                    }
                } catch (RemoteException e) {
                    Log.m109e(TAG, "Failed to set volume key long press listener", e);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @SystemApi
    public void setOnMediaKeyListener(OnMediaKeyListener listener, Handler handler) {
        synchronized (this.mLock) {
            try {
                try {
                    if (listener == null) {
                        this.mOnMediaKeyListener = null;
                        this.mService.setOnMediaKeyListener(null);
                    } else {
                        if (handler == null) {
                            handler = new Handler();
                        }
                        OnMediaKeyListenerImpl onMediaKeyListenerImpl = new OnMediaKeyListenerImpl(listener, handler);
                        this.mOnMediaKeyListener = onMediaKeyListenerImpl;
                        this.mService.setOnMediaKeyListener(onMediaKeyListenerImpl);
                    }
                } catch (RemoteException e) {
                    Log.m109e(TAG, "Failed to set media key listener", e);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @SystemApi
    public void addOnMediaKeyEventDispatchedListener(Executor executor, OnMediaKeyEventDispatchedListener listener) {
        Objects.requireNonNull(executor, "executor shouldn't be null");
        Objects.requireNonNull(listener, "listener shouldn't be null");
        synchronized (this.mLock) {
            try {
                this.mOnMediaKeyEventDispatchedListeners.put(listener, executor);
                if (this.mOnMediaKeyEventDispatchedListeners.size() == 1) {
                    this.mService.addOnMediaKeyEventDispatchedListener(this.mOnMediaKeyEventDispatchedListenerStub);
                }
            } catch (RemoteException e) {
                Log.m109e(TAG, "Failed to set media key listener", e);
            }
        }
    }

    @SystemApi
    public void removeOnMediaKeyEventDispatchedListener(OnMediaKeyEventDispatchedListener listener) {
        Objects.requireNonNull(listener, "listener shouldn't be null");
        synchronized (this.mLock) {
            try {
                this.mOnMediaKeyEventDispatchedListeners.remove(listener);
                if (this.mOnMediaKeyEventDispatchedListeners.size() == 0) {
                    this.mService.removeOnMediaKeyEventDispatchedListener(this.mOnMediaKeyEventDispatchedListenerStub);
                }
            } catch (RemoteException e) {
                Log.m109e(TAG, "Failed to set media key event dispatched listener", e);
            }
        }
    }

    public void addOnMediaKeyEventSessionChangedListener(Executor executor, final OnMediaKeyEventSessionChangedListener listener) {
        Objects.requireNonNull(executor, "executor shouldn't be null");
        Objects.requireNonNull(listener, "listener shouldn't be null");
        synchronized (this.mLock) {
            try {
                if (this.mMediaKeyEventSessionChangedCallbacks.isEmpty()) {
                    this.mService.addOnMediaKeyEventSessionChangedListener(this.mOnMediaKeyEventSessionChangedListenerStub, this.mContext.getPackageName());
                }
                this.mMediaKeyEventSessionChangedCallbacks.put(listener, executor);
                executor.execute(new Runnable() { // from class: android.media.session.MediaSessionManager$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSessionManager.this.lambda$addOnMediaKeyEventSessionChangedListener$0(listener);
                    }
                });
            } catch (RemoteException e) {
                Log.m109e(TAG, "Failed to add MediaKeyEventSessionChangedListener", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addOnMediaKeyEventSessionChangedListener$0(OnMediaKeyEventSessionChangedListener listener) {
        listener.onMediaKeyEventSessionChanged(this.mCurMediaKeyEventSessionPackage, this.mCurMediaKeyEventSession);
    }

    public void removeOnMediaKeyEventSessionChangedListener(OnMediaKeyEventSessionChangedListener listener) {
        Objects.requireNonNull(listener, "listener shouldn't be null");
        synchronized (this.mLock) {
            try {
                if (this.mMediaKeyEventSessionChangedCallbacks.remove(listener) != null && this.mMediaKeyEventSessionChangedCallbacks.isEmpty()) {
                    this.mService.removeOnMediaKeyEventSessionChangedListener(this.mOnMediaKeyEventSessionChangedListenerStub);
                }
            } catch (RemoteException e) {
                Log.m109e(TAG, "Failed to remove MediaKeyEventSessionChangedListener", e);
            }
        }
    }

    public void setCustomMediaKeyDispatcher(String name) {
        try {
            this.mService.setCustomMediaKeyDispatcher(name);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to set custom media key dispatcher name", e);
        }
    }

    public void setCustomMediaSessionPolicyProvider(String name) {
        try {
            this.mService.setCustomMediaSessionPolicyProvider(name);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to set custom session policy provider name", e);
        }
    }

    public boolean hasCustomMediaKeyDispatcher(String componentName) {
        Objects.requireNonNull(componentName, "componentName shouldn't be null");
        try {
            return this.mService.hasCustomMediaKeyDispatcher(componentName);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to check if custom media key dispatcher with given component name exists", e);
            return false;
        }
    }

    public boolean hasCustomMediaSessionPolicyProvider(String componentName) {
        Objects.requireNonNull(componentName, "componentName shouldn't be null");
        try {
            return this.mService.hasCustomMediaSessionPolicyProvider(componentName);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to check if custom media session policy provider with given component name exists", e);
            return false;
        }
    }

    public int getSessionPolicies(MediaSession.Token token) {
        try {
            return this.mService.getSessionPolicies(token);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to get session policies", e);
            return 0;
        }
    }

    public void setSessionPolicies(MediaSession.Token token, int policies) {
        try {
            this.mService.setSessionPolicies(token, policies);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to set session policies", e);
        }
    }

    /* loaded from: classes2.dex */
    public static final class RemoteUserInfo {
        private final String mPackageName;
        private final int mPid;
        private final int mUid;

        public RemoteUserInfo(String packageName, int pid, int uid) {
            this.mPackageName = packageName;
            this.mPid = pid;
            this.mUid = uid;
        }

        public String getPackageName() {
            return this.mPackageName;
        }

        public int getPid() {
            return this.mPid;
        }

        public int getUid() {
            return this.mUid;
        }

        public boolean equals(Object obj) {
            if (obj instanceof RemoteUserInfo) {
                if (this == obj) {
                    return true;
                }
                RemoteUserInfo otherUserInfo = (RemoteUserInfo) obj;
                return TextUtils.equals(this.mPackageName, otherUserInfo.mPackageName) && this.mPid == otherUserInfo.mPid && this.mUid == otherUserInfo.mUid;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mPackageName, Integer.valueOf(this.mPid), Integer.valueOf(this.mUid));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class SessionsChangedWrapper {
        private Context mContext;
        private Executor mExecutor;
        private OnActiveSessionsChangedListener mListener;
        private final IActiveSessionsListener.Stub mStub = new BinderC18671();

        public SessionsChangedWrapper(Context context, OnActiveSessionsChangedListener listener, Executor executor) {
            this.mContext = context;
            this.mListener = listener;
            this.mExecutor = executor;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: android.media.session.MediaSessionManager$SessionsChangedWrapper$1 */
        /* loaded from: classes2.dex */
        public class BinderC18671 extends IActiveSessionsListener.Stub {
            BinderC18671() {
            }

            @Override // android.media.session.IActiveSessionsListener
            public void onActiveSessionsChanged(final List<MediaSession.Token> tokens) {
                if (SessionsChangedWrapper.this.mExecutor != null) {
                    Executor executor = SessionsChangedWrapper.this.mExecutor;
                    executor.execute(new Runnable() { // from class: android.media.session.MediaSessionManager$SessionsChangedWrapper$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaSessionManager.SessionsChangedWrapper.BinderC18671.this.lambda$onActiveSessionsChanged$0(tokens);
                        }
                    });
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onActiveSessionsChanged$0(List tokens) {
                SessionsChangedWrapper.this.callOnActiveSessionsChangedListener(tokens);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void callOnActiveSessionsChangedListener(List<MediaSession.Token> tokens) {
            Context context = this.mContext;
            if (context != null) {
                ArrayList<MediaController> controllers = new ArrayList<>();
                int size = tokens.size();
                for (int i = 0; i < size; i++) {
                    controllers.add(new MediaController(context, tokens.get(i)));
                }
                OnActiveSessionsChangedListener listener = this.mListener;
                if (listener != null) {
                    listener.onActiveSessionsChanged(controllers);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void release() {
            this.mListener = null;
            this.mContext = null;
            this.mExecutor = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class Session2TokensChangedWrapper {
        private final Executor mExecutor;
        private final OnSession2TokensChangedListener mListener;
        private final ISession2TokensListener.Stub mStub = new BinderC18661();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: android.media.session.MediaSessionManager$Session2TokensChangedWrapper$1 */
        /* loaded from: classes2.dex */
        public class BinderC18661 extends ISession2TokensListener.Stub {
            BinderC18661() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onSession2TokensChanged$0(List tokens) {
                Session2TokensChangedWrapper.this.mListener.onSession2TokensChanged(tokens);
            }

            @Override // android.media.session.ISession2TokensListener
            public void onSession2TokensChanged(final List<Session2Token> tokens) {
                Session2TokensChangedWrapper.this.mExecutor.execute(new Runnable() { // from class: android.media.session.MediaSessionManager$Session2TokensChangedWrapper$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaSessionManager.Session2TokensChangedWrapper.BinderC18661.this.lambda$onSession2TokensChanged$0(tokens);
                    }
                });
            }
        }

        Session2TokensChangedWrapper(OnSession2TokensChangedListener listener, Executor executor) {
            this.mListener = listener;
            this.mExecutor = executor;
        }

        public ISession2TokensListener.Stub getStub() {
            return this.mStub;
        }
    }

    /* loaded from: classes2.dex */
    private static final class OnVolumeKeyLongPressListenerImpl extends IOnVolumeKeyLongPressListener.Stub {
        private Handler mHandler;
        private OnVolumeKeyLongPressListener mListener;

        public OnVolumeKeyLongPressListenerImpl(OnVolumeKeyLongPressListener listener, Handler handler) {
            this.mListener = listener;
            this.mHandler = handler;
        }

        @Override // android.media.session.IOnVolumeKeyLongPressListener
        public void onVolumeKeyLongPress(final KeyEvent event) {
            Handler handler;
            if (this.mListener == null || (handler = this.mHandler) == null) {
                Log.m104w(MediaSessionManager.TAG, "Failed to call volume key long-press listener. Either mListener or mHandler is null");
            } else {
                handler.post(new Runnable() { // from class: android.media.session.MediaSessionManager.OnVolumeKeyLongPressListenerImpl.1
                    @Override // java.lang.Runnable
                    public void run() {
                        OnVolumeKeyLongPressListenerImpl.this.mListener.onVolumeKeyLongPress(event);
                    }
                });
            }
        }
    }

    /* loaded from: classes2.dex */
    private static final class OnMediaKeyListenerImpl extends IOnMediaKeyListener.Stub {
        private Handler mHandler;
        private OnMediaKeyListener mListener;

        public OnMediaKeyListenerImpl(OnMediaKeyListener listener, Handler handler) {
            this.mListener = listener;
            this.mHandler = handler;
        }

        @Override // android.media.session.IOnMediaKeyListener
        public void onMediaKey(final KeyEvent event, final ResultReceiver result) {
            Handler handler;
            if (this.mListener == null || (handler = this.mHandler) == null) {
                Log.m104w(MediaSessionManager.TAG, "Failed to call media key listener. Either mListener or mHandler is null");
            } else {
                handler.post(new Runnable() { // from class: android.media.session.MediaSessionManager.OnMediaKeyListenerImpl.1
                    @Override // java.lang.Runnable
                    public void run() {
                        boolean handled = OnMediaKeyListenerImpl.this.mListener.onMediaKey(event);
                        Log.m112d(MediaSessionManager.TAG, "The media key listener is returned " + handled);
                        ResultReceiver resultReceiver = result;
                        if (resultReceiver != null) {
                            resultReceiver.send(handled ? 1 : 0, null);
                        }
                    }
                });
            }
        }
    }

    /* loaded from: classes2.dex */
    private final class OnMediaKeyEventDispatchedListenerStub extends IOnMediaKeyEventDispatchedListener.Stub {
        private OnMediaKeyEventDispatchedListenerStub() {
        }

        @Override // android.media.session.IOnMediaKeyEventDispatchedListener
        public void onMediaKeyEventDispatched(final KeyEvent event, final String packageName, final MediaSession.Token sessionToken) {
            synchronized (MediaSessionManager.this.mLock) {
                for (final Map.Entry<OnMediaKeyEventDispatchedListener, Executor> e : MediaSessionManager.this.mOnMediaKeyEventDispatchedListeners.entrySet()) {
                    e.getValue().execute(new Runnable() { // from class: android.media.session.MediaSessionManager$OnMediaKeyEventDispatchedListenerStub$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ((MediaSessionManager.OnMediaKeyEventDispatchedListener) e.getKey()).onMediaKeyEventDispatched(event, packageName, sessionToken);
                        }
                    });
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    private final class OnMediaKeyEventSessionChangedListenerStub extends IOnMediaKeyEventSessionChangedListener.Stub {
        private OnMediaKeyEventSessionChangedListenerStub() {
        }

        @Override // android.media.session.IOnMediaKeyEventSessionChangedListener
        public void onMediaKeyEventSessionChanged(final String packageName, final MediaSession.Token sessionToken) {
            synchronized (MediaSessionManager.this.mLock) {
                MediaSessionManager.this.mCurMediaKeyEventSessionPackage = packageName;
                MediaSessionManager.this.mCurMediaKeyEventSession = sessionToken;
                for (final Map.Entry<OnMediaKeyEventSessionChangedListener, Executor> e : MediaSessionManager.this.mMediaKeyEventSessionChangedCallbacks.entrySet()) {
                    e.getValue().execute(new Runnable() { // from class: android.media.session.MediaSessionManager$OnMediaKeyEventSessionChangedListenerStub$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ((MediaSessionManager.OnMediaKeyEventSessionChangedListener) e.getKey()).onMediaKeyEventSessionChanged(packageName, sessionToken);
                        }
                    });
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    private final class RemoteSessionCallbackStub extends IRemoteSessionCallback.Stub {
        private RemoteSessionCallbackStub() {
        }

        @Override // android.media.IRemoteSessionCallback
        public void onVolumeChanged(final MediaSession.Token sessionToken, final int flags) {
            Map<RemoteSessionCallback, Executor> callbacks = new ArrayMap<>();
            synchronized (MediaSessionManager.this.mLock) {
                callbacks.putAll(MediaSessionManager.this.mRemoteSessionCallbacks);
            }
            for (final Map.Entry<RemoteSessionCallback, Executor> e : callbacks.entrySet()) {
                e.getValue().execute(new Runnable() { // from class: android.media.session.MediaSessionManager$RemoteSessionCallbackStub$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ((MediaSessionManager.RemoteSessionCallback) e.getKey()).onVolumeChanged(sessionToken, flags);
                    }
                });
            }
        }

        @Override // android.media.IRemoteSessionCallback
        public void onSessionChanged(final MediaSession.Token sessionToken) {
            Map<RemoteSessionCallback, Executor> callbacks = new ArrayMap<>();
            synchronized (MediaSessionManager.this.mLock) {
                callbacks.putAll(MediaSessionManager.this.mRemoteSessionCallbacks);
            }
            for (final Map.Entry<RemoteSessionCallback, Executor> e : callbacks.entrySet()) {
                e.getValue().execute(new Runnable() { // from class: android.media.session.MediaSessionManager$RemoteSessionCallbackStub$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ((MediaSessionManager.RemoteSessionCallback) e.getKey()).onDefaultRemoteSessionChanged(sessionToken);
                    }
                });
            }
        }
    }
}
