package android.media;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.MediaSessionLegacyHelper;
import android.media.session.PlaybackState;
import android.p008os.Bundle;
import android.p008os.Looper;
import android.p008os.SystemClock;
import android.util.Log;
@Deprecated
/* loaded from: classes2.dex */
public class RemoteControlClient {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_PLAYBACK_VOLUME = 15;
    public static final int DEFAULT_PLAYBACK_VOLUME_HANDLING = 1;
    public static final int FLAGS_KEY_MEDIA_NONE = 0;
    public static final int FLAG_INFORMATION_REQUEST_ALBUM_ART = 8;
    public static final int FLAG_INFORMATION_REQUEST_KEY_MEDIA = 2;
    public static final int FLAG_INFORMATION_REQUEST_METADATA = 1;
    public static final int FLAG_INFORMATION_REQUEST_PLAYSTATE = 4;
    public static final int FLAG_KEY_MEDIA_FAST_FORWARD = 64;
    public static final int FLAG_KEY_MEDIA_NEXT = 128;
    public static final int FLAG_KEY_MEDIA_PAUSE = 16;
    public static final int FLAG_KEY_MEDIA_PLAY = 4;
    public static final int FLAG_KEY_MEDIA_PLAY_PAUSE = 8;
    public static final int FLAG_KEY_MEDIA_POSITION_UPDATE = 256;
    public static final int FLAG_KEY_MEDIA_PREVIOUS = 1;
    public static final int FLAG_KEY_MEDIA_RATING = 512;
    public static final int FLAG_KEY_MEDIA_REWIND = 2;
    public static final int FLAG_KEY_MEDIA_STOP = 32;
    public static int MEDIA_POSITION_READABLE = 1;
    public static int MEDIA_POSITION_WRITABLE = 2;
    public static final int PLAYBACKINFO_INVALID_VALUE = Integer.MIN_VALUE;
    public static final int PLAYBACKINFO_PLAYBACK_TYPE = 1;
    public static final int PLAYBACKINFO_USES_STREAM = 5;
    public static final int PLAYBACKINFO_VOLUME = 2;
    public static final int PLAYBACKINFO_VOLUME_HANDLING = 4;
    public static final int PLAYBACKINFO_VOLUME_MAX = 3;
    public static final long PLAYBACK_POSITION_ALWAYS_UNKNOWN = -9216204211029966080L;
    public static final long PLAYBACK_POSITION_INVALID = -1;
    public static final float PLAYBACK_SPEED_1X = 1.0f;
    public static final int PLAYBACK_TYPE_LOCAL = 0;
    private static final int PLAYBACK_TYPE_MAX = 1;
    private static final int PLAYBACK_TYPE_MIN = 0;
    public static final int PLAYBACK_TYPE_REMOTE = 1;
    public static final int PLAYBACK_VOLUME_FIXED = 0;
    public static final int PLAYBACK_VOLUME_VARIABLE = 1;
    public static final int PLAYSTATE_BUFFERING = 8;
    public static final int PLAYSTATE_ERROR = 9;
    public static final int PLAYSTATE_FAST_FORWARDING = 4;
    public static final int PLAYSTATE_NONE = 0;
    public static final int PLAYSTATE_PAUSED = 2;
    public static final int PLAYSTATE_PLAYING = 3;
    public static final int PLAYSTATE_REWINDING = 5;
    public static final int PLAYSTATE_SKIPPING_BACKWARDS = 7;
    public static final int PLAYSTATE_SKIPPING_FORWARDS = 6;
    public static final int PLAYSTATE_STOPPED = 1;
    private static final long POSITION_DRIFT_MAX_MS = 500;
    private static final long POSITION_REFRESH_PERIOD_MIN_MS = 2000;
    private static final long POSITION_REFRESH_PERIOD_PLAYING_MS = 15000;
    public static final int RCSE_ID_UNREGISTERED = -1;
    private static final String TAG = "RemoteControlClient";
    private MediaMetadata mMediaMetadata;
    private OnMetadataUpdateListener mMetadataUpdateListener;
    private Bitmap mOriginalArtwork;
    private OnGetPlaybackPositionListener mPositionProvider;
    private OnPlaybackPositionUpdateListener mPositionUpdateListener;
    private final PendingIntent mRcMediaIntent;
    private MediaSession mSession;
    private final Object mCacheLock = new Object();
    private int mPlaybackState = 0;
    private long mPlaybackStateChangeTimeMs = 0;
    private long mPlaybackPositionMs = -1;
    private float mPlaybackSpeed = 1.0f;
    private int mTransportControlFlags = 0;
    private Bundle mMetadata = new Bundle();
    private int mCurrentClientGenId = -1;
    private boolean mNeedsPositionSync = false;
    private PlaybackState mSessionPlaybackState = null;
    private MediaSession.Callback mTransportListener = new MediaSession.Callback() { // from class: android.media.RemoteControlClient.1
        @Override // android.media.session.MediaSession.Callback
        public void onSeekTo(long pos) {
            RemoteControlClient remoteControlClient = RemoteControlClient.this;
            remoteControlClient.onSeekTo(remoteControlClient.mCurrentClientGenId, pos);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onSetRating(Rating rating) {
            if ((RemoteControlClient.this.mTransportControlFlags & 512) != 0) {
                RemoteControlClient remoteControlClient = RemoteControlClient.this;
                remoteControlClient.onUpdateMetadata(remoteControlClient.mCurrentClientGenId, MediaMetadataEditor.RATING_KEY_BY_USER, rating);
            }
        }
    };

    /* loaded from: classes2.dex */
    public interface OnGetPlaybackPositionListener {
        long onGetPlaybackPosition();
    }

    /* loaded from: classes2.dex */
    public interface OnMetadataUpdateListener {
        void onMetadataUpdate(int i, Object obj);
    }

    /* loaded from: classes2.dex */
    public interface OnPlaybackPositionUpdateListener {
        void onPlaybackPositionUpdate(long j);
    }

    public RemoteControlClient(PendingIntent mediaButtonIntent) {
        this.mRcMediaIntent = mediaButtonIntent;
    }

    public RemoteControlClient(PendingIntent mediaButtonIntent, Looper looper) {
        this.mRcMediaIntent = mediaButtonIntent;
    }

    public void registerWithSession(MediaSessionLegacyHelper helper) {
        helper.addRccListener(this.mRcMediaIntent, this.mTransportListener);
        this.mSession = helper.getSession(this.mRcMediaIntent);
        setTransportControlFlags(this.mTransportControlFlags);
    }

    public void unregisterWithSession(MediaSessionLegacyHelper helper) {
        helper.removeRccListener(this.mRcMediaIntent);
        this.mSession = null;
    }

    public MediaSession getMediaSession() {
        return this.mSession;
    }

    @Deprecated
    /* loaded from: classes2.dex */
    public class MetadataEditor extends MediaMetadataEditor {
        public static final int BITMAP_KEY_ARTWORK = 100;
        public static final int METADATA_KEY_ARTWORK = 100;

        private MetadataEditor() {
        }

        public Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized MetadataEditor putString(int key, String value) throws IllegalArgumentException {
            String metadataKey;
            super.putString(key, value);
            if (this.mMetadataBuilder != null && (metadataKey = MediaMetadata.getKeyFromMetadataEditorKey(key)) != null) {
                this.mMetadataBuilder.putText(metadataKey, value);
            }
            return this;
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized MetadataEditor putLong(int key, long value) throws IllegalArgumentException {
            String metadataKey;
            super.putLong(key, value);
            if (this.mMetadataBuilder != null && (metadataKey = MediaMetadata.getKeyFromMetadataEditorKey(key)) != null) {
                this.mMetadataBuilder.putLong(metadataKey, value);
            }
            return this;
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized MetadataEditor putBitmap(int key, Bitmap bitmap) throws IllegalArgumentException {
            String metadataKey;
            super.putBitmap(key, bitmap);
            if (this.mMetadataBuilder != null && (metadataKey = MediaMetadata.getKeyFromMetadataEditorKey(key)) != null) {
                this.mMetadataBuilder.putBitmap(metadataKey, bitmap);
            }
            return this;
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized MetadataEditor putObject(int key, Object object) throws IllegalArgumentException {
            String metadataKey;
            super.putObject(key, object);
            if (this.mMetadataBuilder != null && ((key == 268435457 || key == 101) && (metadataKey = MediaMetadata.getKeyFromMetadataEditorKey(key)) != null)) {
                this.mMetadataBuilder.putRating(metadataKey, (Rating) object);
            }
            return this;
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized void clear() {
            super.clear();
        }

        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:28:0x008b -> B:26:0x0089). Please submit an issue!!! */
        @Override // android.media.MediaMetadataEditor
        public synchronized void apply() {
            if (this.mApplied) {
                Log.m110e(RemoteControlClient.TAG, "Can't apply a previously applied MetadataEditor");
                return;
            }
            synchronized (RemoteControlClient.this.mCacheLock) {
                try {
                    RemoteControlClient.this.mMetadata = new Bundle(this.mEditorMetadata);
                    RemoteControlClient.this.mMetadata.putLong(String.valueOf((int) MediaMetadataEditor.KEY_EDITABLE_MASK), this.mEditableKeys);
                    if (RemoteControlClient.this.mOriginalArtwork != null) {
                        try {
                            if (!RemoteControlClient.this.mOriginalArtwork.equals(this.mEditorArtwork)) {
                                RemoteControlClient.this.mOriginalArtwork.recycle();
                            }
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                    RemoteControlClient.this.mOriginalArtwork = this.mEditorArtwork;
                    this.mEditorArtwork = null;
                    if (RemoteControlClient.this.mSession != null && this.mMetadataBuilder != null) {
                        RemoteControlClient.this.mMediaMetadata = this.mMetadataBuilder.build();
                        RemoteControlClient.this.mSession.setMetadata(RemoteControlClient.this.mMediaMetadata);
                    }
                    this.mApplied = true;
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            }
        }
    }

    public MetadataEditor editMetadata(boolean startEmpty) {
        MediaMetadata mediaMetadata;
        MetadataEditor editor = new MetadataEditor();
        if (startEmpty) {
            editor.mEditorMetadata = new Bundle();
            editor.mEditorArtwork = null;
            editor.mMetadataChanged = true;
            editor.mArtworkChanged = true;
            editor.mEditableKeys = 0L;
        } else {
            editor.mEditorMetadata = new Bundle(this.mMetadata);
            editor.mEditorArtwork = this.mOriginalArtwork;
            editor.mMetadataChanged = false;
            editor.mArtworkChanged = false;
        }
        if (startEmpty || (mediaMetadata = this.mMediaMetadata) == null) {
            editor.mMetadataBuilder = new MediaMetadata.Builder();
        } else {
            editor.mMetadataBuilder = new MediaMetadata.Builder(mediaMetadata);
        }
        return editor;
    }

    public void setPlaybackState(int state) {
        setPlaybackStateInt(state, PLAYBACK_POSITION_ALWAYS_UNKNOWN, 1.0f, false);
    }

    public void setPlaybackState(int state, long timeInMs, float playbackSpeed) {
        setPlaybackStateInt(state, timeInMs, playbackSpeed, true);
    }

    private void setPlaybackStateInt(int state, long timeInMs, float playbackSpeed, boolean hasPosition) {
        synchronized (this.mCacheLock) {
            if (this.mPlaybackState != state || this.mPlaybackPositionMs != timeInMs || this.mPlaybackSpeed != playbackSpeed) {
                this.mPlaybackState = state;
                long position = -1;
                if (!hasPosition) {
                    this.mPlaybackPositionMs = PLAYBACK_POSITION_ALWAYS_UNKNOWN;
                } else if (timeInMs < 0) {
                    this.mPlaybackPositionMs = -1L;
                } else {
                    this.mPlaybackPositionMs = timeInMs;
                }
                this.mPlaybackSpeed = playbackSpeed;
                this.mPlaybackStateChangeTimeMs = SystemClock.elapsedRealtime();
                if (this.mSession != null) {
                    int pbState = getStateFromRccState(state);
                    if (hasPosition) {
                        position = this.mPlaybackPositionMs;
                    }
                    PlaybackState.Builder bob = new PlaybackState.Builder(this.mSessionPlaybackState);
                    bob.setState(pbState, position, playbackSpeed, SystemClock.elapsedRealtime());
                    bob.setErrorMessage(null);
                    PlaybackState build = bob.build();
                    this.mSessionPlaybackState = build;
                    this.mSession.setPlaybackState(build);
                }
            }
        }
    }

    public void setTransportControlFlags(int transportControlFlags) {
        synchronized (this.mCacheLock) {
            this.mTransportControlFlags = transportControlFlags;
            if (this.mSession != null) {
                PlaybackState.Builder bob = new PlaybackState.Builder(this.mSessionPlaybackState);
                bob.setActions(getActionsFromRccControlFlags(transportControlFlags));
                PlaybackState build = bob.build();
                this.mSessionPlaybackState = build;
                this.mSession.setPlaybackState(build);
            }
        }
    }

    public void setMetadataUpdateListener(OnMetadataUpdateListener l) {
        synchronized (this.mCacheLock) {
            this.mMetadataUpdateListener = l;
        }
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener l) {
        synchronized (this.mCacheLock) {
            this.mPositionUpdateListener = l;
        }
    }

    public void setOnGetPlaybackPositionListener(OnGetPlaybackPositionListener l) {
        synchronized (this.mCacheLock) {
            this.mPositionProvider = l;
        }
    }

    public PendingIntent getRcMediaIntent() {
        return this.mRcMediaIntent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSeekTo(int generationId, long timeMs) {
        OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener;
        synchronized (this.mCacheLock) {
            if (this.mCurrentClientGenId == generationId && (onPlaybackPositionUpdateListener = this.mPositionUpdateListener) != null) {
                onPlaybackPositionUpdateListener.onPlaybackPositionUpdate(timeMs);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUpdateMetadata(int generationId, int key, Object value) {
        OnMetadataUpdateListener onMetadataUpdateListener;
        synchronized (this.mCacheLock) {
            if (this.mCurrentClientGenId == generationId && (onMetadataUpdateListener = this.mMetadataUpdateListener) != null) {
                onMetadataUpdateListener.onMetadataUpdate(key, value);
            }
        }
    }

    static boolean playbackPositionShouldMove(int playstate) {
        switch (playstate) {
            case 1:
            case 2:
            case 6:
            case 7:
            case 8:
            case 9:
                return false;
            case 3:
            case 4:
            case 5:
            default:
                return true;
        }
    }

    private static long getCheckPeriodFromSpeed(float speed) {
        if (Math.abs(speed) <= 1.0f) {
            return POSITION_REFRESH_PERIOD_PLAYING_MS;
        }
        return Math.max(15000.0f / Math.abs(speed), (long) POSITION_REFRESH_PERIOD_MIN_MS);
    }

    private static int getStateFromRccState(int rccState) {
        switch (rccState) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 10;
            case 7:
                return 9;
            case 8:
                return 6;
            case 9:
                return 7;
            default:
                return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getRccStateFromState(int state) {
        switch (state) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 8;
            case 7:
                return 9;
            case 8:
            default:
                return -1;
            case 9:
                return 7;
            case 10:
                return 6;
        }
    }

    private static long getActionsFromRccControlFlags(int rccFlags) {
        long actions = 0;
        for (long flag = 1; flag <= rccFlags; flag <<= 1) {
            if ((rccFlags & flag) != 0) {
                actions |= getActionForRccFlag((int) flag);
            }
        }
        return actions;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getRccControlFlagsFromActions(long actions) {
        int rccFlags = 0;
        for (long action = 1; action <= actions && action < 2147483647L; action <<= 1) {
            if ((action & actions) != 0) {
                rccFlags |= getRccFlagForAction(action);
            }
        }
        return rccFlags;
    }

    private static long getActionForRccFlag(int flag) {
        switch (flag) {
            case 1:
                return 16L;
            case 2:
                return 8L;
            case 4:
                return 4L;
            case 8:
                return 512L;
            case 16:
                return 2L;
            case 32:
                return 1L;
            case 64:
                return 64L;
            case 128:
                return 32L;
            case 256:
                return 256L;
            case 512:
                return 128L;
            default:
                return 0L;
        }
    }

    private static int getRccFlagForAction(long action) {
        int testAction = action < 2147483647L ? (int) action : 0;
        switch (testAction) {
            case 1:
                return 32;
            case 2:
                return 16;
            case 4:
                return 4;
            case 8:
                return 2;
            case 16:
                return 1;
            case 32:
                return 128;
            case 64:
                return 64;
            case 128:
                return 512;
            case 256:
                return 256;
            case 512:
                return 8;
            default:
                return 0;
        }
    }
}
