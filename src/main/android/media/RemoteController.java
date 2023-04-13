package android.media;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.session.MediaController;
import android.media.session.MediaSessionLegacyHelper;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import java.util.List;
@Deprecated
/* loaded from: classes2.dex */
public final class RemoteController {
    private static final boolean DEBUG = false;
    private static final int MAX_BITMAP_DIMENSION = 512;
    private static final int MSG_CLIENT_CHANGE = 0;
    private static final int MSG_NEW_MEDIA_METADATA = 2;
    private static final int MSG_NEW_PLAYBACK_STATE = 1;
    public static final int POSITION_SYNCHRONIZATION_CHECK = 1;
    public static final int POSITION_SYNCHRONIZATION_NONE = 0;
    private static final int SENDMSG_NOOP = 1;
    private static final int SENDMSG_QUEUE = 2;
    private static final int SENDMSG_REPLACE = 0;
    private static final String TAG = "RemoteController";
    private static final Object mInfoLock = new Object();
    private int mArtworkHeight;
    private int mArtworkWidth;
    private final Context mContext;
    private MediaController mCurrentSession;
    private boolean mEnabled;
    private final EventHandler mEventHandler;
    private boolean mIsRegistered;
    private PlaybackInfo mLastPlaybackInfo;
    private final int mMaxBitmapDimension;
    private MetadataEditor mMetadataEditor;
    private OnClientUpdateListener mOnClientUpdateListener;
    private MediaController.Callback mSessionCb;
    private MediaSessionManager.OnActiveSessionsChangedListener mSessionListener;
    private MediaSessionManager mSessionManager;

    /* loaded from: classes2.dex */
    public interface OnClientUpdateListener {
        void onClientChange(boolean z);

        void onClientMetadataUpdate(MetadataEditor metadataEditor);

        void onClientPlaybackStateUpdate(int i);

        void onClientPlaybackStateUpdate(int i, long j, long j2, float f);

        void onClientTransportControlUpdate(int i);
    }

    public RemoteController(Context context, OnClientUpdateListener updateListener) throws IllegalArgumentException {
        this(context, updateListener, null);
    }

    public RemoteController(Context context, OnClientUpdateListener updateListener, Looper looper) throws IllegalArgumentException {
        this.mSessionCb = new MediaControllerCallback();
        this.mIsRegistered = false;
        this.mArtworkWidth = -1;
        this.mArtworkHeight = -1;
        this.mEnabled = true;
        if (context == null) {
            throw new IllegalArgumentException("Invalid null Context");
        }
        if (updateListener == null) {
            throw new IllegalArgumentException("Invalid null OnClientUpdateListener");
        }
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
        } else {
            Looper l = Looper.myLooper();
            if (l != null) {
                this.mEventHandler = new EventHandler(this, l);
            } else {
                throw new IllegalArgumentException("Calling thread not associated with a looper");
            }
        }
        this.mOnClientUpdateListener = updateListener;
        this.mContext = context;
        this.mSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        this.mSessionListener = new TopTransportSessionListener();
        if (ActivityManager.isLowRamDeviceStatic()) {
            this.mMaxBitmapDimension = 512;
            return;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.mMaxBitmapDimension = Math.max(dm.widthPixels, dm.heightPixels);
    }

    public long getEstimatedMediaPosition() {
        PlaybackState state;
        synchronized (mInfoLock) {
            MediaController mediaController = this.mCurrentSession;
            if (mediaController != null && (state = mediaController.getPlaybackState()) != null) {
                return state.getPosition();
            }
            return -1L;
        }
    }

    public boolean sendMediaKeyEvent(KeyEvent keyEvent) throws IllegalArgumentException {
        if (!KeyEvent.isMediaSessionKey(keyEvent.getKeyCode())) {
            throw new IllegalArgumentException("not a media key event");
        }
        synchronized (mInfoLock) {
            MediaController mediaController = this.mCurrentSession;
            if (mediaController != null) {
                return mediaController.dispatchMediaButtonEvent(keyEvent);
            }
            return false;
        }
    }

    public boolean seekTo(long timeMs) throws IllegalArgumentException {
        if (!this.mEnabled) {
            Log.m110e(TAG, "Cannot use seekTo() from a disabled RemoteController");
            return false;
        } else if (timeMs < 0) {
            throw new IllegalArgumentException("illegal negative time value");
        } else {
            synchronized (mInfoLock) {
                MediaController mediaController = this.mCurrentSession;
                if (mediaController != null) {
                    mediaController.getTransportControls().seekTo(timeMs);
                }
            }
            return true;
        }
    }

    public boolean setArtworkConfiguration(boolean wantBitmap, int width, int height) throws IllegalArgumentException {
        synchronized (mInfoLock) {
            if (wantBitmap) {
                if (width > 0 && height > 0) {
                    int i = this.mMaxBitmapDimension;
                    if (width > i) {
                        width = i;
                    }
                    if (height > i) {
                        height = i;
                    }
                    this.mArtworkWidth = width;
                    this.mArtworkHeight = height;
                } else {
                    throw new IllegalArgumentException("Invalid dimensions");
                }
            } else {
                this.mArtworkWidth = -1;
                this.mArtworkHeight = -1;
            }
        }
        return true;
    }

    public boolean setArtworkConfiguration(int width, int height) throws IllegalArgumentException {
        return setArtworkConfiguration(true, width, height);
    }

    public boolean clearArtworkConfiguration() {
        return setArtworkConfiguration(false, -1, -1);
    }

    public boolean setSynchronizationMode(int sync) throws IllegalArgumentException {
        if (sync != 0 && sync != 1) {
            throw new IllegalArgumentException("Unknown synchronization mode " + sync);
        }
        if (this.mIsRegistered) {
            return true;
        }
        Log.m110e(TAG, "Cannot set synchronization mode on an unregistered RemoteController");
        return false;
    }

    public MetadataEditor editMetadata() {
        MetadataEditor editor = new MetadataEditor();
        editor.mEditorMetadata = new Bundle();
        editor.mEditorArtwork = null;
        editor.mMetadataChanged = true;
        editor.mArtworkChanged = true;
        editor.mEditableKeys = 0L;
        return editor;
    }

    /* loaded from: classes2.dex */
    public class MetadataEditor extends MediaMetadataEditor {
        protected MetadataEditor() {
        }

        protected MetadataEditor(Bundle metadata, long editableKeys) {
            this.mEditorMetadata = metadata;
            this.mEditableKeys = editableKeys;
            this.mEditorArtwork = (Bitmap) metadata.getParcelable(String.valueOf(100), Bitmap.class);
            if (this.mEditorArtwork != null) {
                cleanupBitmapFromBundle(100);
            }
            this.mMetadataChanged = true;
            this.mArtworkChanged = true;
            this.mApplied = false;
        }

        private void cleanupBitmapFromBundle(int key) {
            if (METADATA_KEYS_TYPE.get(key, -1) == 2) {
                this.mEditorMetadata.remove(String.valueOf(key));
            }
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized void apply() {
            Rating rating;
            if (!this.mMetadataChanged) {
                return;
            }
            synchronized (RemoteController.mInfoLock) {
                try {
                    if (RemoteController.this.mCurrentSession != null) {
                        try {
                            if (this.mEditorMetadata.containsKey(String.valueOf((int) MediaMetadataEditor.RATING_KEY_BY_USER)) && (rating = (Rating) getObject(MediaMetadataEditor.RATING_KEY_BY_USER, null)) != null) {
                                RemoteController.this.mCurrentSession.getTransportControls().setRating(rating);
                            }
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                    this.mApplied = false;
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    private class MediaControllerCallback extends MediaController.Callback {
        private MediaControllerCallback() {
        }

        @Override // android.media.session.MediaController.Callback
        public void onPlaybackStateChanged(PlaybackState state) {
            RemoteController.this.onNewPlaybackState(state);
        }

        @Override // android.media.session.MediaController.Callback
        public void onMetadataChanged(MediaMetadata metadata) {
            RemoteController.this.onNewMediaMetadata(metadata);
        }
    }

    /* loaded from: classes2.dex */
    private class TopTransportSessionListener implements MediaSessionManager.OnActiveSessionsChangedListener {
        private TopTransportSessionListener() {
        }

        @Override // android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
        public void onActiveSessionsChanged(List<MediaController> controllers) {
            int size = controllers.size();
            for (int i = 0; i < size; i++) {
                MediaController controller = controllers.get(i);
                long flags = controller.getFlags();
                if ((2 & flags) != 0) {
                    RemoteController.this.updateController(controller);
                    return;
                }
            }
            RemoteController.this.updateController(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class EventHandler extends Handler {
        public EventHandler(RemoteController rc, Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    RemoteController.this.onClientChange(msg.arg2 == 1);
                    return;
                case 1:
                    RemoteController.this.onNewPlaybackState((PlaybackState) msg.obj);
                    return;
                case 2:
                    RemoteController.this.onNewMediaMetadata((MediaMetadata) msg.obj);
                    return;
                default:
                    Log.m110e(RemoteController.TAG, "unknown event " + msg.what);
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startListeningToSessions() {
        ComponentName listenerComponent = new ComponentName(this.mContext, this.mOnClientUpdateListener.getClass());
        Handler handler = null;
        if (Looper.myLooper() == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        this.mSessionManager.addOnActiveSessionsChangedListener(this.mSessionListener, listenerComponent, handler);
        this.mSessionListener.onActiveSessionsChanged(this.mSessionManager.getActiveSessions(listenerComponent));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopListeningToSessions() {
        this.mSessionManager.removeOnActiveSessionsChangedListener(this.mSessionListener);
    }

    private static void sendMsg(Handler handler, int msg, int existingMsgPolicy, int arg1, int arg2, Object obj, int delayMs) {
        if (handler == null) {
            Log.m110e(TAG, "null event handler, will not deliver message " + msg);
            return;
        }
        if (existingMsgPolicy == 0) {
            handler.removeMessages(msg);
        } else if (existingMsgPolicy == 1 && handler.hasMessages(msg)) {
            return;
        }
        handler.sendMessageDelayed(handler.obtainMessage(msg, arg1, arg2, obj), delayMs);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClientChange(boolean clearing) {
        OnClientUpdateListener l;
        synchronized (mInfoLock) {
            l = this.mOnClientUpdateListener;
            this.mMetadataEditor = null;
        }
        if (l != null) {
            l.onClientChange(clearing);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateController(MediaController controller) {
        synchronized (mInfoLock) {
            if (controller == null) {
                MediaController mediaController = this.mCurrentSession;
                if (mediaController != null) {
                    mediaController.unregisterCallback(this.mSessionCb);
                    this.mCurrentSession = null;
                    sendMsg(this.mEventHandler, 0, 0, 0, 1, null, 0);
                }
            } else if (this.mCurrentSession == null || !controller.getSessionToken().equals(this.mCurrentSession.getSessionToken())) {
                MediaController mediaController2 = this.mCurrentSession;
                if (mediaController2 != null) {
                    mediaController2.unregisterCallback(this.mSessionCb);
                }
                sendMsg(this.mEventHandler, 0, 0, 0, 0, null, 0);
                this.mCurrentSession = controller;
                controller.registerCallback(this.mSessionCb, this.mEventHandler);
                PlaybackState state = controller.getPlaybackState();
                sendMsg(this.mEventHandler, 1, 0, 0, 0, state, 0);
                MediaMetadata metadata = controller.getMetadata();
                sendMsg(this.mEventHandler, 2, 0, 0, 0, metadata, 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewPlaybackState(PlaybackState state) {
        OnClientUpdateListener l;
        synchronized (mInfoLock) {
            l = this.mOnClientUpdateListener;
        }
        if (l != null) {
            int playstate = state == null ? 0 : RemoteControlClient.getRccStateFromState(state.getState());
            if (state == null || state.getPosition() == -1) {
                l.onClientPlaybackStateUpdate(playstate);
            } else {
                l.onClientPlaybackStateUpdate(playstate, state.getLastPositionUpdateTime(), state.getPosition(), state.getPlaybackSpeed());
            }
            if (state != null) {
                l.onClientTransportControlUpdate(RemoteControlClient.getRccControlFlagsFromActions(state.getActions()));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewMediaMetadata(MediaMetadata metadata) {
        OnClientUpdateListener l;
        MetadataEditor metadataEditor;
        if (metadata == null) {
            return;
        }
        synchronized (mInfoLock) {
            l = this.mOnClientUpdateListener;
            MediaController mediaController = this.mCurrentSession;
            boolean canRate = (mediaController == null || mediaController.getRatingType() == 0) ? false : true;
            long editableKeys = canRate ? 268435457L : 0L;
            Bundle legacyMetadata = MediaSessionLegacyHelper.getOldMetadata(metadata, this.mArtworkWidth, this.mArtworkHeight);
            metadataEditor = new MetadataEditor(legacyMetadata, editableKeys);
            this.mMetadataEditor = metadataEditor;
        }
        if (l != null) {
            l.onClientMetadataUpdate(metadataEditor);
        }
    }

    /* loaded from: classes2.dex */
    private static class PlaybackInfo {
        long mCurrentPosMs;
        float mSpeed;
        int mState;
        long mStateChangeTimeMs;

        PlaybackInfo(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
            this.mState = state;
            this.mStateChangeTimeMs = stateChangeTimeMs;
            this.mCurrentPosMs = currentPosMs;
            this.mSpeed = speed;
        }
    }

    OnClientUpdateListener getUpdateListener() {
        return this.mOnClientUpdateListener;
    }
}
