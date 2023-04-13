package android.media.session;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.Rating;
import android.media.VolumeProvider;
import android.media.session.ISessionCallback;
import android.media.session.ISessionController;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.p008os.BadParcelableException;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import com.android.internal.C4057R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class MediaSession {
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int FLAG_EXCLUSIVE_GLOBAL_PRIORITY = 65536;
    @Deprecated
    public static final int FLAG_HANDLES_MEDIA_BUTTONS = 1;
    @Deprecated
    public static final int FLAG_HANDLES_TRANSPORT_CONTROLS = 2;
    public static final int INVALID_PID = -1;
    public static final int INVALID_UID = -1;
    static final String TAG = "MediaSession";
    private boolean mActive;
    private final ISession mBinder;
    private CallbackMessageHandler mCallback;
    private final CallbackStub mCbStub;
    private Context mContext;
    private final MediaController mController;
    private final Object mLock;
    private final int mMaxBitmapSize;
    private PlaybackState mPlaybackState;
    private final Token mSessionToken;
    private VolumeProvider mVolumeProvider;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SessionFlags {
    }

    public MediaSession(Context context, String tag) {
        this(context, tag, null);
    }

    public MediaSession(Context context, String tag, Bundle sessionInfo) {
        this.mLock = new Object();
        this.mActive = false;
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null.");
        }
        if (TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("tag cannot be null or empty");
        }
        if (hasCustomParcelable(sessionInfo)) {
            throw new IllegalArgumentException("sessionInfo shouldn't contain any custom parcelables");
        }
        this.mContext = context;
        this.mMaxBitmapSize = context.getResources().getDimensionPixelSize(C4057R.dimen.config_mediaMetadataBitmapMaxSize);
        CallbackStub callbackStub = new CallbackStub(this);
        this.mCbStub = callbackStub;
        MediaSessionManager manager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        try {
            ISession createSession = manager.createSession(callbackStub, tag, sessionInfo);
            this.mBinder = createSession;
            Token token = new Token(Process.myUid(), createSession.getController());
            this.mSessionToken = token;
            this.mController = new MediaController(context, token);
        } catch (RemoteException e) {
            throw new RuntimeException("Remote error creating session.", e);
        }
    }

    public void setCallback(Callback callback) {
        setCallback(callback, null);
    }

    public void setCallback(Callback callback, Handler handler) {
        synchronized (this.mLock) {
            CallbackMessageHandler callbackMessageHandler = this.mCallback;
            if (callbackMessageHandler != null) {
                callbackMessageHandler.mCallback.mSession = null;
                this.mCallback.removeCallbacksAndMessages(null);
            }
            if (callback == null) {
                this.mCallback = null;
                return;
            }
            Looper looper = handler != null ? handler.getLooper() : Looper.myLooper();
            callback.mSession = this;
            CallbackMessageHandler msgHandler = new CallbackMessageHandler(looper, callback);
            this.mCallback = msgHandler;
        }
    }

    public void setSessionActivity(PendingIntent pi) {
        try {
            this.mBinder.setLaunchPendingIntent(pi);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Failure in setLaunchPendingIntent.", e);
        }
    }

    @Deprecated
    public void setMediaButtonReceiver(PendingIntent mbr) {
        try {
            this.mBinder.setMediaButtonReceiver(mbr);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Failure in setMediaButtonReceiver.", e);
        }
    }

    public void setMediaButtonBroadcastReceiver(ComponentName broadcastReceiver) {
        if (broadcastReceiver != null) {
            try {
                if (!TextUtils.equals(broadcastReceiver.getPackageName(), this.mContext.getPackageName())) {
                    throw new IllegalArgumentException("broadcastReceiver should belong to the same package as the context given when creating MediaSession.");
                }
            } catch (RemoteException e) {
                Log.wtf(TAG, "Failure in setMediaButtonBroadcastReceiver.", e);
                return;
            }
        }
        this.mBinder.setMediaButtonBroadcastReceiver(broadcastReceiver);
    }

    public void setFlags(int flags) {
        try {
            this.mBinder.setFlags(flags);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Failure in setFlags.", e);
        }
    }

    public void setPlaybackToLocal(AudioAttributes attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes cannot be null for local playback.");
        }
        try {
            this.mBinder.setPlaybackToLocal(attributes);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Failure in setPlaybackToLocal.", e);
        }
    }

    public void setPlaybackToRemote(VolumeProvider volumeProvider) {
        if (volumeProvider == null) {
            throw new IllegalArgumentException("volumeProvider may not be null!");
        }
        synchronized (this.mLock) {
            this.mVolumeProvider = volumeProvider;
        }
        volumeProvider.setCallback(new VolumeProvider.Callback() { // from class: android.media.session.MediaSession.1
            @Override // android.media.VolumeProvider.Callback
            public void onVolumeChanged(VolumeProvider volumeProvider2) {
                MediaSession.this.notifyRemoteVolumeChanged(volumeProvider2);
            }
        });
        try {
            this.mBinder.setPlaybackToRemote(volumeProvider.getVolumeControl(), volumeProvider.getMaxVolume(), volumeProvider.getVolumeControlId());
            this.mBinder.setCurrentVolume(volumeProvider.getCurrentVolume());
        } catch (RemoteException e) {
            Log.wtf(TAG, "Failure in setPlaybackToRemote.", e);
        }
    }

    public void setActive(boolean active) {
        if (this.mActive == active) {
            return;
        }
        try {
            this.mBinder.setActive(active);
            this.mActive = active;
        } catch (RemoteException e) {
            Log.wtf(TAG, "Failure in setActive.", e);
        }
    }

    public boolean isActive() {
        return this.mActive;
    }

    public void sendSessionEvent(String event, Bundle extras) {
        if (TextUtils.isEmpty(event)) {
            throw new IllegalArgumentException("event cannot be null or empty");
        }
        try {
            this.mBinder.sendEvent(event, extras);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Error sending event", e);
        }
    }

    public void release() {
        try {
            this.mBinder.destroySession();
        } catch (RemoteException e) {
            Log.wtf(TAG, "Error releasing session: ", e);
        }
    }

    public Token getSessionToken() {
        return this.mSessionToken;
    }

    public MediaController getController() {
        return this.mController;
    }

    public void setPlaybackState(PlaybackState state) {
        this.mPlaybackState = state;
        try {
            this.mBinder.setPlaybackState(state);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Dead object in setPlaybackState.", e);
        }
    }

    public void setMetadata(MediaMetadata metadata) {
        long duration = -1;
        int fields = 0;
        MediaDescription description = null;
        if (metadata != null) {
            metadata = new MediaMetadata.Builder(metadata).setBitmapDimensionLimit(this.mMaxBitmapSize).build();
            if (metadata.containsKey(MediaMetadata.METADATA_KEY_DURATION)) {
                duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
            }
            fields = metadata.size();
            description = metadata.getDescription();
        }
        String metadataDescription = "size=" + fields + ", description=" + description;
        try {
            this.mBinder.setMetadata(metadata, duration, metadataDescription);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Dead object in setPlaybackState.", e);
        }
    }

    public void setQueue(List<QueueItem> queue) {
        try {
            if (queue == null) {
                this.mBinder.resetQueue();
            } else {
                IBinder binder = this.mBinder.getBinderForSetQueue();
                ParcelableListBinder.send(binder, queue);
            }
        } catch (RemoteException e) {
            Log.wtf("Dead object in setQueue.", e);
        }
    }

    public void setQueueTitle(CharSequence title) {
        try {
            this.mBinder.setQueueTitle(title);
        } catch (RemoteException e) {
            Log.wtf("Dead object in setQueueTitle.", e);
        }
    }

    public void setRatingType(int type) {
        try {
            this.mBinder.setRatingType(type);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error in setRatingType.", e);
        }
    }

    public void setExtras(Bundle extras) {
        try {
            this.mBinder.setExtras(extras);
        } catch (RemoteException e) {
            Log.wtf("Dead object in setExtras.", e);
        }
    }

    public final MediaSessionManager.RemoteUserInfo getCurrentControllerInfo() {
        CallbackMessageHandler callbackMessageHandler = this.mCallback;
        if (callbackMessageHandler == null || callbackMessageHandler.mCurrentControllerInfo == null) {
            throw new IllegalStateException("This should be called inside of MediaSession.Callback methods");
        }
        return this.mCallback.mCurrentControllerInfo;
    }

    public void notifyRemoteVolumeChanged(VolumeProvider provider) {
        synchronized (this.mLock) {
            if (provider != null) {
                if (provider == this.mVolumeProvider) {
                    try {
                        this.mBinder.setCurrentVolume(provider.getCurrentVolume());
                        return;
                    } catch (RemoteException e) {
                        Log.m109e(TAG, "Error in notifyVolumeChanged", e);
                        return;
                    }
                }
            }
            Log.m104w(TAG, "Received update from stale volume provider");
        }
    }

    public String getCallingPackage() {
        CallbackMessageHandler callbackMessageHandler = this.mCallback;
        if (callbackMessageHandler != null && callbackMessageHandler.mCurrentControllerInfo != null) {
            return this.mCallback.mCurrentControllerInfo.getPackageName();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasCustomParcelable(Bundle bundle) {
        if (bundle == null) {
            return false;
        }
        Parcel parcel = null;
        try {
            try {
                parcel = Parcel.obtain();
                parcel.writeBundle(bundle);
                parcel.setDataPosition(0);
                Bundle out = parcel.readBundle(null);
                for (String key : out.keySet()) {
                    out.get(key);
                }
                if (parcel != null) {
                    parcel.recycle();
                }
                return false;
            } catch (BadParcelableException e) {
                Log.m111d(TAG, "Custom parcelable in bundle.", e);
                if (parcel != null) {
                    parcel.recycle();
                    return true;
                }
                return true;
            }
        } catch (Throwable th) {
            if (parcel != null) {
                parcel.recycle();
            }
            throw th;
        }
    }

    void dispatchPrepare(MediaSessionManager.RemoteUserInfo caller) {
        postToCallback(caller, 3, null, null);
    }

    void dispatchPrepareFromMediaId(MediaSessionManager.RemoteUserInfo caller, String mediaId, Bundle extras) {
        postToCallback(caller, 4, mediaId, extras);
    }

    void dispatchPrepareFromSearch(MediaSessionManager.RemoteUserInfo caller, String query, Bundle extras) {
        postToCallback(caller, 5, query, extras);
    }

    void dispatchPrepareFromUri(MediaSessionManager.RemoteUserInfo caller, Uri uri, Bundle extras) {
        postToCallback(caller, 6, uri, extras);
    }

    void dispatchPlay(MediaSessionManager.RemoteUserInfo caller) {
        postToCallback(caller, 7, null, null);
    }

    void dispatchPlayFromMediaId(MediaSessionManager.RemoteUserInfo caller, String mediaId, Bundle extras) {
        postToCallback(caller, 8, mediaId, extras);
    }

    void dispatchPlayFromSearch(MediaSessionManager.RemoteUserInfo caller, String query, Bundle extras) {
        postToCallback(caller, 9, query, extras);
    }

    void dispatchPlayFromUri(MediaSessionManager.RemoteUserInfo caller, Uri uri, Bundle extras) {
        postToCallback(caller, 10, uri, extras);
    }

    void dispatchSkipToItem(MediaSessionManager.RemoteUserInfo caller, long id) {
        postToCallback(caller, 11, Long.valueOf(id), null);
    }

    void dispatchPause(MediaSessionManager.RemoteUserInfo caller) {
        postToCallback(caller, 12, null, null);
    }

    void dispatchStop(MediaSessionManager.RemoteUserInfo caller) {
        postToCallback(caller, 13, null, null);
    }

    void dispatchNext(MediaSessionManager.RemoteUserInfo caller) {
        postToCallback(caller, 14, null, null);
    }

    void dispatchPrevious(MediaSessionManager.RemoteUserInfo caller) {
        postToCallback(caller, 15, null, null);
    }

    void dispatchFastForward(MediaSessionManager.RemoteUserInfo caller) {
        postToCallback(caller, 16, null, null);
    }

    void dispatchRewind(MediaSessionManager.RemoteUserInfo caller) {
        postToCallback(caller, 17, null, null);
    }

    void dispatchSeekTo(MediaSessionManager.RemoteUserInfo caller, long pos) {
        postToCallback(caller, 18, Long.valueOf(pos), null);
    }

    void dispatchRate(MediaSessionManager.RemoteUserInfo caller, Rating rating) {
        postToCallback(caller, 19, rating, null);
    }

    void dispatchSetPlaybackSpeed(MediaSessionManager.RemoteUserInfo caller, float speed) {
        postToCallback(caller, 20, Float.valueOf(speed), null);
    }

    void dispatchCustomAction(MediaSessionManager.RemoteUserInfo caller, String action, Bundle args) {
        postToCallback(caller, 21, action, args);
    }

    void dispatchMediaButton(MediaSessionManager.RemoteUserInfo caller, Intent mediaButtonIntent) {
        postToCallback(caller, 2, mediaButtonIntent, null);
    }

    void dispatchMediaButtonDelayed(MediaSessionManager.RemoteUserInfo info, Intent mediaButtonIntent, long delay) {
        postToCallbackDelayed(info, 24, mediaButtonIntent, null, delay);
    }

    void dispatchAdjustVolume(MediaSessionManager.RemoteUserInfo caller, int direction) {
        postToCallback(caller, 22, Integer.valueOf(direction), null);
    }

    void dispatchSetVolumeTo(MediaSessionManager.RemoteUserInfo caller, int volume) {
        postToCallback(caller, 23, Integer.valueOf(volume), null);
    }

    void dispatchCommand(MediaSessionManager.RemoteUserInfo caller, String command, Bundle args, ResultReceiver resultCb) {
        Command cmd = new Command(command, args, resultCb);
        postToCallback(caller, 1, cmd, null);
    }

    void postToCallback(MediaSessionManager.RemoteUserInfo caller, int what, Object obj, Bundle data) {
        postToCallbackDelayed(caller, what, obj, data, 0L);
    }

    void postToCallbackDelayed(MediaSessionManager.RemoteUserInfo caller, int what, Object obj, Bundle data, long delay) {
        synchronized (this.mLock) {
            CallbackMessageHandler callbackMessageHandler = this.mCallback;
            if (callbackMessageHandler != null) {
                callbackMessageHandler.post(caller, what, obj, data, delay);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class Token implements Parcelable {
        public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator<Token>() { // from class: android.media.session.MediaSession.Token.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Token createFromParcel(Parcel in) {
                return new Token(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Token[] newArray(int size) {
                return new Token[size];
            }
        };
        private final ISessionController mBinder;
        private final int mUid;

        public Token(int uid, ISessionController binder) {
            this.mUid = uid;
            this.mBinder = binder;
        }

        Token(Parcel in) {
            this.mUid = in.readInt();
            this.mBinder = ISessionController.Stub.asInterface(in.readStrongBinder());
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mUid);
            dest.writeStrongBinder(this.mBinder.asBinder());
        }

        public int hashCode() {
            int result = this.mUid;
            int i = result * 31;
            ISessionController iSessionController = this.mBinder;
            int result2 = i + (iSessionController == null ? 0 : iSessionController.asBinder().hashCode());
            return result2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Token other = (Token) obj;
            if (this.mUid != other.mUid) {
                return false;
            }
            ISessionController iSessionController = this.mBinder;
            if (iSessionController == null || other.mBinder == null) {
                if (iSessionController == other.mBinder) {
                    return true;
                }
                return false;
            }
            return Objects.equals(iSessionController.asBinder(), other.mBinder.asBinder());
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public int getUid() {
            return this.mUid;
        }

        public ISessionController getBinder() {
            return this.mBinder;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Callback {
        private CallbackMessageHandler mHandler;
        private boolean mMediaPlayPauseKeyPending;
        private MediaSession mSession;

        public void onCommand(String command, Bundle args, ResultReceiver cb) {
        }

        public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
            KeyEvent ke;
            if (this.mSession != null && this.mHandler != null && Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonIntent.getAction()) && (ke = (KeyEvent) mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent.class)) != null && ke.getAction() == 0) {
                PlaybackState state = this.mSession.mPlaybackState;
                long validActions = state == null ? 0L : state.getActions();
                switch (ke.getKeyCode()) {
                    case 79:
                    case 85:
                        if (ke.getRepeatCount() > 0) {
                            handleMediaPlayPauseKeySingleTapIfPending();
                        } else if (this.mMediaPlayPauseKeyPending) {
                            this.mHandler.removeMessages(24);
                            this.mMediaPlayPauseKeyPending = false;
                            if ((validActions & 32) != 0) {
                                onSkipToNext();
                            }
                        } else {
                            this.mMediaPlayPauseKeyPending = true;
                            MediaSession mediaSession = this.mSession;
                            mediaSession.dispatchMediaButtonDelayed(mediaSession.getCurrentControllerInfo(), mediaButtonIntent, ViewConfiguration.getDoubleTapTimeout());
                        }
                        return true;
                    default:
                        handleMediaPlayPauseKeySingleTapIfPending();
                        switch (ke.getKeyCode()) {
                            case 86:
                                if ((1 & validActions) != 0) {
                                    onStop();
                                    return true;
                                }
                                break;
                            case 87:
                                if ((validActions & 32) != 0) {
                                    onSkipToNext();
                                    return true;
                                }
                                break;
                            case 88:
                                if ((16 & validActions) != 0) {
                                    onSkipToPrevious();
                                    return true;
                                }
                                break;
                            case 89:
                                if ((8 & validActions) != 0) {
                                    onRewind();
                                    return true;
                                }
                                break;
                            case 90:
                                if ((64 & validActions) != 0) {
                                    onFastForward();
                                    return true;
                                }
                                break;
                            case 126:
                                if ((4 & validActions) != 0) {
                                    onPlay();
                                    return true;
                                }
                                break;
                            case 127:
                                if ((2 & validActions) != 0) {
                                    onPause();
                                    return true;
                                }
                                break;
                        }
                }
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleMediaPlayPauseKeySingleTapIfPending() {
            boolean isPlaying;
            boolean canPlay;
            if (!this.mMediaPlayPauseKeyPending) {
                return;
            }
            boolean canPause = false;
            this.mMediaPlayPauseKeyPending = false;
            this.mHandler.removeMessages(24);
            PlaybackState state = this.mSession.mPlaybackState;
            long validActions = state == null ? 0L : state.getActions();
            if (state == null || state.getState() != 3) {
                isPlaying = false;
            } else {
                isPlaying = true;
            }
            if ((516 & validActions) == 0) {
                canPlay = false;
            } else {
                canPlay = true;
            }
            if ((514 & validActions) != 0) {
                canPause = true;
            }
            if (isPlaying && canPause) {
                onPause();
            } else if (!isPlaying && canPlay) {
                onPlay();
            }
        }

        public void onPrepare() {
        }

        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
        }

        public void onPrepareFromSearch(String query, Bundle extras) {
        }

        public void onPrepareFromUri(Uri uri, Bundle extras) {
        }

        public void onPlay() {
        }

        public void onPlayFromSearch(String query, Bundle extras) {
        }

        public void onPlayFromMediaId(String mediaId, Bundle extras) {
        }

        public void onPlayFromUri(Uri uri, Bundle extras) {
        }

        public void onSkipToQueueItem(long id) {
        }

        public void onPause() {
        }

        public void onSkipToNext() {
        }

        public void onSkipToPrevious() {
        }

        public void onFastForward() {
        }

        public void onRewind() {
        }

        public void onStop() {
        }

        public void onSeekTo(long pos) {
        }

        public void onSetRating(Rating rating) {
        }

        public void onSetPlaybackSpeed(float speed) {
        }

        public void onCustomAction(String action, Bundle extras) {
        }
    }

    /* loaded from: classes2.dex */
    public static class CallbackStub extends ISessionCallback.Stub {
        private WeakReference<MediaSession> mMediaSession;

        public CallbackStub(MediaSession session) {
            this.mMediaSession = new WeakReference<>(session);
        }

        private static MediaSessionManager.RemoteUserInfo createRemoteUserInfo(String packageName, int pid, int uid) {
            return new MediaSessionManager.RemoteUserInfo(packageName, pid, uid);
        }

        @Override // android.media.session.ISessionCallback
        public void onCommand(String packageName, int pid, int uid, String command, Bundle args, ResultReceiver cb) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchCommand(createRemoteUserInfo(packageName, pid, uid), command, args, cb);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onMediaButton(String packageName, int pid, int uid, Intent mediaButtonIntent, int sequenceNumber, ResultReceiver cb) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                try {
                    session.dispatchMediaButton(createRemoteUserInfo(packageName, pid, uid), mediaButtonIntent);
                } finally {
                    if (cb != null) {
                        cb.send(sequenceNumber, null);
                    }
                }
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onMediaButtonFromController(String packageName, int pid, int uid, Intent mediaButtonIntent) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchMediaButton(createRemoteUserInfo(packageName, pid, uid), mediaButtonIntent);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onPrepare(String packageName, int pid, int uid) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchPrepare(createRemoteUserInfo(packageName, pid, uid));
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onPrepareFromMediaId(String packageName, int pid, int uid, String mediaId, Bundle extras) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchPrepareFromMediaId(createRemoteUserInfo(packageName, pid, uid), mediaId, extras);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onPrepareFromSearch(String packageName, int pid, int uid, String query, Bundle extras) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchPrepareFromSearch(createRemoteUserInfo(packageName, pid, uid), query, extras);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onPrepareFromUri(String packageName, int pid, int uid, Uri uri, Bundle extras) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchPrepareFromUri(createRemoteUserInfo(packageName, pid, uid), uri, extras);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onPlay(String packageName, int pid, int uid) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchPlay(createRemoteUserInfo(packageName, pid, uid));
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onPlayFromMediaId(String packageName, int pid, int uid, String mediaId, Bundle extras) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchPlayFromMediaId(createRemoteUserInfo(packageName, pid, uid), mediaId, extras);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onPlayFromSearch(String packageName, int pid, int uid, String query, Bundle extras) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchPlayFromSearch(createRemoteUserInfo(packageName, pid, uid), query, extras);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onPlayFromUri(String packageName, int pid, int uid, Uri uri, Bundle extras) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchPlayFromUri(createRemoteUserInfo(packageName, pid, uid), uri, extras);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onSkipToTrack(String packageName, int pid, int uid, long id) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchSkipToItem(createRemoteUserInfo(packageName, pid, uid), id);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onPause(String packageName, int pid, int uid) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchPause(createRemoteUserInfo(packageName, pid, uid));
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onStop(String packageName, int pid, int uid) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchStop(createRemoteUserInfo(packageName, pid, uid));
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onNext(String packageName, int pid, int uid) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchNext(createRemoteUserInfo(packageName, pid, uid));
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onPrevious(String packageName, int pid, int uid) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchPrevious(createRemoteUserInfo(packageName, pid, uid));
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onFastForward(String packageName, int pid, int uid) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchFastForward(createRemoteUserInfo(packageName, pid, uid));
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onRewind(String packageName, int pid, int uid) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchRewind(createRemoteUserInfo(packageName, pid, uid));
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onSeekTo(String packageName, int pid, int uid, long pos) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchSeekTo(createRemoteUserInfo(packageName, pid, uid), pos);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onRate(String packageName, int pid, int uid, Rating rating) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchRate(createRemoteUserInfo(packageName, pid, uid), rating);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onSetPlaybackSpeed(String packageName, int pid, int uid, float speed) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchSetPlaybackSpeed(createRemoteUserInfo(packageName, pid, uid), speed);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onCustomAction(String packageName, int pid, int uid, String action, Bundle args) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchCustomAction(createRemoteUserInfo(packageName, pid, uid), action, args);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onAdjustVolume(String packageName, int pid, int uid, int direction) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchAdjustVolume(createRemoteUserInfo(packageName, pid, uid), direction);
            }
        }

        @Override // android.media.session.ISessionCallback
        public void onSetVolumeTo(String packageName, int pid, int uid, int value) {
            MediaSession session = this.mMediaSession.get();
            if (session != null) {
                session.dispatchSetVolumeTo(createRemoteUserInfo(packageName, pid, uid), value);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class QueueItem implements Parcelable {
        public static final Parcelable.Creator<QueueItem> CREATOR = new Parcelable.Creator<QueueItem>() { // from class: android.media.session.MediaSession.QueueItem.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public QueueItem createFromParcel(Parcel p) {
                return new QueueItem(p);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public QueueItem[] newArray(int size) {
                return new QueueItem[size];
            }
        };
        public static final int UNKNOWN_ID = -1;
        private final MediaDescription mDescription;
        private final long mId;

        public QueueItem(MediaDescription description, long id) {
            if (description == null) {
                throw new IllegalArgumentException("Description cannot be null.");
            }
            if (id == -1) {
                throw new IllegalArgumentException("Id cannot be QueueItem.UNKNOWN_ID");
            }
            this.mDescription = description;
            this.mId = id;
        }

        private QueueItem(Parcel in) {
            this.mDescription = MediaDescription.CREATOR.createFromParcel(in);
            this.mId = in.readLong();
        }

        public MediaDescription getDescription() {
            return this.mDescription;
        }

        public long getQueueId() {
            return this.mId;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            this.mDescription.writeToParcel(dest, flags);
            dest.writeLong(this.mId);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "MediaSession.QueueItem {Description=" + this.mDescription + ", Id=" + this.mId + " }";
        }

        public boolean equals(Object o) {
            if (o == null || !(o instanceof QueueItem)) {
                return false;
            }
            QueueItem item = (QueueItem) o;
            if (this.mId != item.mId || !Objects.equals(this.mDescription, item.mDescription)) {
                return false;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class Command {
        public final String command;
        public final Bundle extras;
        public final ResultReceiver stub;

        Command(String command, Bundle extras, ResultReceiver stub) {
            this.command = command;
            this.extras = extras;
            this.stub = stub;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class CallbackMessageHandler extends Handler {
        private static final int MSG_ADJUST_VOLUME = 22;
        private static final int MSG_COMMAND = 1;
        private static final int MSG_CUSTOM_ACTION = 21;
        private static final int MSG_FAST_FORWARD = 16;
        private static final int MSG_MEDIA_BUTTON = 2;
        private static final int MSG_NEXT = 14;
        private static final int MSG_PAUSE = 12;
        private static final int MSG_PLAY = 7;
        private static final int MSG_PLAY_MEDIA_ID = 8;
        private static final int MSG_PLAY_PAUSE_KEY_DOUBLE_TAP_TIMEOUT = 24;
        private static final int MSG_PLAY_SEARCH = 9;
        private static final int MSG_PLAY_URI = 10;
        private static final int MSG_PREPARE = 3;
        private static final int MSG_PREPARE_MEDIA_ID = 4;
        private static final int MSG_PREPARE_SEARCH = 5;
        private static final int MSG_PREPARE_URI = 6;
        private static final int MSG_PREVIOUS = 15;
        private static final int MSG_RATE = 19;
        private static final int MSG_REWIND = 17;
        private static final int MSG_SEEK_TO = 18;
        private static final int MSG_SET_PLAYBACK_SPEED = 20;
        private static final int MSG_SET_VOLUME = 23;
        private static final int MSG_SKIP_TO_ITEM = 11;
        private static final int MSG_STOP = 13;
        private Callback mCallback;
        private MediaSessionManager.RemoteUserInfo mCurrentControllerInfo;

        CallbackMessageHandler(Looper looper, Callback callback) {
            super(looper);
            this.mCallback = callback;
            callback.mHandler = this;
        }

        void post(MediaSessionManager.RemoteUserInfo caller, int what, Object obj, Bundle data, long delayMs) {
            Pair<MediaSessionManager.RemoteUserInfo, Object> objWithCaller = Pair.create(caller, obj);
            Message msg = obtainMessage(what, objWithCaller);
            msg.setAsynchronous(true);
            msg.setData(data);
            if (delayMs > 0) {
                sendMessageDelayed(msg, delayMs);
            } else {
                sendMessage(msg);
            }
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            VolumeProvider vp;
            VolumeProvider vp2;
            this.mCurrentControllerInfo = (MediaSessionManager.RemoteUserInfo) ((Pair) msg.obj).first;
            Object obj = ((Pair) msg.obj).second;
            switch (msg.what) {
                case 1:
                    Command cmd = (Command) obj;
                    this.mCallback.onCommand(cmd.command, cmd.extras, cmd.stub);
                    break;
                case 2:
                    this.mCallback.onMediaButtonEvent((Intent) obj);
                    break;
                case 3:
                    this.mCallback.onPrepare();
                    break;
                case 4:
                    this.mCallback.onPrepareFromMediaId((String) obj, msg.getData());
                    break;
                case 5:
                    this.mCallback.onPrepareFromSearch((String) obj, msg.getData());
                    break;
                case 6:
                    this.mCallback.onPrepareFromUri((Uri) obj, msg.getData());
                    break;
                case 7:
                    this.mCallback.onPlay();
                    break;
                case 8:
                    this.mCallback.onPlayFromMediaId((String) obj, msg.getData());
                    break;
                case 9:
                    this.mCallback.onPlayFromSearch((String) obj, msg.getData());
                    break;
                case 10:
                    this.mCallback.onPlayFromUri((Uri) obj, msg.getData());
                    break;
                case 11:
                    this.mCallback.onSkipToQueueItem(((Long) obj).longValue());
                    break;
                case 12:
                    this.mCallback.onPause();
                    break;
                case 13:
                    this.mCallback.onStop();
                    break;
                case 14:
                    this.mCallback.onSkipToNext();
                    break;
                case 15:
                    this.mCallback.onSkipToPrevious();
                    break;
                case 16:
                    this.mCallback.onFastForward();
                    break;
                case 17:
                    this.mCallback.onRewind();
                    break;
                case 18:
                    this.mCallback.onSeekTo(((Long) obj).longValue());
                    break;
                case 19:
                    this.mCallback.onSetRating((Rating) obj);
                    break;
                case 20:
                    this.mCallback.onSetPlaybackSpeed(((Float) obj).floatValue());
                    break;
                case 21:
                    this.mCallback.onCustomAction((String) obj, msg.getData());
                    break;
                case 22:
                    synchronized (MediaSession.this.mLock) {
                        vp = MediaSession.this.mVolumeProvider;
                    }
                    if (vp != null) {
                        vp.onAdjustVolume(((Integer) obj).intValue());
                        break;
                    }
                    break;
                case 23:
                    synchronized (MediaSession.this.mLock) {
                        vp2 = MediaSession.this.mVolumeProvider;
                    }
                    if (vp2 != null) {
                        vp2.onSetVolumeTo(((Integer) obj).intValue());
                        break;
                    }
                    break;
                case 24:
                    this.mCallback.handleMediaPlayPauseKeySingleTapIfPending();
                    break;
            }
            this.mCurrentControllerInfo = null;
        }
    }
}
