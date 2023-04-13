package android.media.session;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaCommunicationManager;
import android.media.MediaMetadata;
import android.media.MediaMetadataEditor;
import android.media.MediaMetrics;
import android.media.Rating;
import android.media.session.MediaSession;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.Looper;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
/* loaded from: classes2.dex */
public class MediaSessionLegacyHelper {
    private static MediaSessionLegacyHelper sInstance;
    private MediaCommunicationManager mCommunicationManager;
    private Context mContext;
    private MediaSessionManager mSessionManager;
    private static final String TAG = "MediaSessionHelper";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final Object sLock = new Object();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ArrayMap<PendingIntent, SessionHolder> mSessions = new ArrayMap<>();

    private MediaSessionLegacyHelper(Context context) {
        this.mContext = context;
        this.mSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        this.mCommunicationManager = (MediaCommunicationManager) context.getSystemService(MediaCommunicationManager.class);
    }

    public static MediaSessionLegacyHelper getHelper(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new MediaSessionLegacyHelper(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    public static Bundle getOldMetadata(MediaMetadata metadata, int artworkWidth, int artworkHeight) {
        boolean includeArtwork = (artworkWidth == -1 || artworkHeight == -1) ? false : true;
        Bundle oldMetadata = new Bundle();
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_ALBUM)) {
            oldMetadata.putString(String.valueOf(1), metadata.getString(MediaMetadata.METADATA_KEY_ALBUM));
        }
        if (includeArtwork && metadata.containsKey(MediaMetadata.METADATA_KEY_ART)) {
            Bitmap art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART);
            oldMetadata.putParcelable(String.valueOf(100), scaleBitmapIfTooBig(art, artworkWidth, artworkHeight));
        } else if (includeArtwork && metadata.containsKey(MediaMetadata.METADATA_KEY_ALBUM_ART)) {
            Bitmap art2 = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
            oldMetadata.putParcelable(String.valueOf(100), scaleBitmapIfTooBig(art2, artworkWidth, artworkHeight));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)) {
            oldMetadata.putString(String.valueOf(13), metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_ARTIST)) {
            oldMetadata.putString(String.valueOf(2), metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_AUTHOR)) {
            oldMetadata.putString(String.valueOf(3), metadata.getString(MediaMetadata.METADATA_KEY_AUTHOR));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_COMPILATION)) {
            oldMetadata.putString(String.valueOf(15), metadata.getString(MediaMetadata.METADATA_KEY_COMPILATION));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_COMPOSER)) {
            oldMetadata.putString(String.valueOf(4), metadata.getString(MediaMetadata.METADATA_KEY_COMPOSER));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_DATE)) {
            oldMetadata.putString(String.valueOf(5), metadata.getString(MediaMetadata.METADATA_KEY_DATE));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_DISC_NUMBER)) {
            oldMetadata.putLong(String.valueOf(14), metadata.getLong(MediaMetadata.METADATA_KEY_DISC_NUMBER));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_DURATION)) {
            oldMetadata.putLong(String.valueOf(9), metadata.getLong(MediaMetadata.METADATA_KEY_DURATION));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_GENRE)) {
            oldMetadata.putString(String.valueOf(6), metadata.getString(MediaMetadata.METADATA_KEY_GENRE));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_NUM_TRACKS)) {
            oldMetadata.putLong(String.valueOf(10), metadata.getLong(MediaMetadata.METADATA_KEY_NUM_TRACKS));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_RATING)) {
            oldMetadata.putParcelable(String.valueOf(101), metadata.getRating(MediaMetadata.METADATA_KEY_RATING));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_USER_RATING)) {
            oldMetadata.putParcelable(String.valueOf((int) MediaMetadataEditor.RATING_KEY_BY_USER), metadata.getRating(MediaMetadata.METADATA_KEY_USER_RATING));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_TITLE)) {
            oldMetadata.putString(String.valueOf(7), metadata.getString(MediaMetadata.METADATA_KEY_TITLE));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_TRACK_NUMBER)) {
            oldMetadata.putLong(String.valueOf(0), metadata.getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_WRITER)) {
            oldMetadata.putString(String.valueOf(11), metadata.getString(MediaMetadata.METADATA_KEY_WRITER));
        }
        if (metadata.containsKey(MediaMetadata.METADATA_KEY_YEAR)) {
            oldMetadata.putLong(String.valueOf(8), metadata.getLong(MediaMetadata.METADATA_KEY_YEAR));
        }
        return oldMetadata;
    }

    public MediaSession getSession(PendingIntent pi) {
        SessionHolder holder = this.mSessions.get(pi);
        if (holder == null) {
            return null;
        }
        return holder.mSession;
    }

    public void sendMediaButtonEvent(KeyEvent keyEvent, boolean needWakeLock) {
        if (keyEvent == null) {
            Log.m104w(TAG, "Tried to send a null key event. Ignoring.");
            return;
        }
        this.mCommunicationManager.dispatchMediaKeyEvent(keyEvent, needWakeLock);
        if (DEBUG) {
            Log.m112d(TAG, "dispatched media key " + keyEvent);
        }
    }

    public void sendVolumeKeyEvent(KeyEvent keyEvent, int stream, boolean musicOnly) {
        if (keyEvent == null) {
            Log.m104w(TAG, "Tried to send a null key event. Ignoring.");
        } else {
            this.mSessionManager.dispatchVolumeKeyEvent(keyEvent, stream, musicOnly);
        }
    }

    public void sendAdjustVolumeBy(int suggestedStream, int delta, int flags) {
        this.mSessionManager.dispatchAdjustVolume(suggestedStream, delta, flags);
        if (DEBUG) {
            Log.m112d(TAG, "dispatched volume adjustment");
        }
    }

    public boolean isGlobalPriorityActive() {
        return this.mSessionManager.isGlobalPriorityActive();
    }

    public void addRccListener(PendingIntent pi, MediaSession.Callback listener) {
        if (pi == null) {
            Log.m104w(TAG, "Pending intent was null, can't add rcc listener.");
            return;
        }
        SessionHolder holder = getHolder(pi, true);
        if (holder == null) {
            return;
        }
        if (holder.mRccListener != null && holder.mRccListener == listener) {
            if (DEBUG) {
                Log.m112d(TAG, "addRccListener listener already added.");
                return;
            }
            return;
        }
        holder.mRccListener = listener;
        holder.mFlags |= 2;
        holder.mSession.setFlags(holder.mFlags);
        holder.update();
        if (DEBUG) {
            Log.m112d(TAG, "Added rcc listener for " + pi + MediaMetrics.SEPARATOR);
        }
    }

    public void removeRccListener(PendingIntent pi) {
        SessionHolder holder;
        if (pi != null && (holder = getHolder(pi, false)) != null && holder.mRccListener != null) {
            holder.mRccListener = null;
            holder.mFlags &= -3;
            holder.mSession.setFlags(holder.mFlags);
            holder.update();
            if (DEBUG) {
                Log.m112d(TAG, "Removed rcc listener for " + pi + MediaMetrics.SEPARATOR);
            }
        }
    }

    public void addMediaButtonListener(PendingIntent pi, ComponentName mbrComponent, Context context) {
        if (pi == null) {
            Log.m104w(TAG, "Pending intent was null, can't addMediaButtonListener.");
            return;
        }
        SessionHolder holder = getHolder(pi, true);
        if (holder == null) {
            return;
        }
        if (holder.mMediaButtonListener != null && DEBUG) {
            Log.m112d(TAG, "addMediaButtonListener already added " + pi);
        }
        holder.mMediaButtonListener = new MediaButtonListener(pi, context);
        holder.mFlags = 1 | holder.mFlags;
        holder.mSession.setFlags(holder.mFlags);
        holder.mSession.setMediaButtonReceiver(pi);
        holder.update();
        if (DEBUG) {
            Log.m112d(TAG, "addMediaButtonListener added " + pi);
        }
    }

    public void removeMediaButtonListener(PendingIntent pi) {
        SessionHolder holder;
        if (pi != null && (holder = getHolder(pi, false)) != null && holder.mMediaButtonListener != null) {
            holder.mFlags &= -2;
            holder.mSession.setFlags(holder.mFlags);
            holder.mMediaButtonListener = null;
            holder.update();
            if (DEBUG) {
                Log.m112d(TAG, "removeMediaButtonListener removed " + pi);
            }
        }
    }

    private static Bitmap scaleBitmapIfTooBig(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > maxWidth || height > maxHeight) {
                float scale = Math.min(maxWidth / width, maxHeight / height);
                int newWidth = Math.round(width * scale);
                int newHeight = Math.round(height * scale);
                Bitmap.Config newConfig = bitmap.getConfig();
                if (newConfig == null) {
                    newConfig = Bitmap.Config.ARGB_8888;
                }
                Bitmap outBitmap = Bitmap.createBitmap(newWidth, newHeight, newConfig);
                Canvas canvas = new Canvas(outBitmap);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                canvas.drawBitmap(bitmap, (Rect) null, new RectF(0.0f, 0.0f, outBitmap.getWidth(), outBitmap.getHeight()), paint);
                return outBitmap;
            }
            return bitmap;
        }
        return bitmap;
    }

    private SessionHolder getHolder(PendingIntent pi, boolean createIfMissing) {
        SessionHolder holder = this.mSessions.get(pi);
        if (holder == null && createIfMissing) {
            MediaSession session = new MediaSession(this.mContext, "MediaSessionHelper-" + pi.getCreatorPackage());
            session.setActive(true);
            SessionHolder holder2 = new SessionHolder(session, pi);
            this.mSessions.put(pi, holder2);
            return holder2;
        }
        return holder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendKeyEvent(PendingIntent pi, Context context, Intent intent) {
        try {
            pi.send(context, 0, intent);
        } catch (PendingIntent.CanceledException e) {
            Log.m109e(TAG, "Error sending media key down event:", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class MediaButtonListener extends MediaSession.Callback {
        private final Context mContext;
        private final PendingIntent mPendingIntent;

        public MediaButtonListener(PendingIntent pi, Context context) {
            this.mPendingIntent = pi;
            this.mContext = context;
        }

        @Override // android.media.session.MediaSession.Callback
        public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
            MediaSessionLegacyHelper.sendKeyEvent(this.mPendingIntent, this.mContext, mediaButtonIntent);
            return true;
        }

        @Override // android.media.session.MediaSession.Callback
        public void onPlay() {
            sendKeyEvent(126);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onPause() {
            sendKeyEvent(127);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onSkipToNext() {
            sendKeyEvent(87);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onSkipToPrevious() {
            sendKeyEvent(88);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onFastForward() {
            sendKeyEvent(90);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onRewind() {
            sendKeyEvent(89);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onStop() {
            sendKeyEvent(86);
        }

        private void sendKeyEvent(int keyCode) {
            KeyEvent ke = new KeyEvent(0, keyCode);
            Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            intent.addFlags(268435456);
            intent.putExtra(Intent.EXTRA_KEY_EVENT, ke);
            MediaSessionLegacyHelper.sendKeyEvent(this.mPendingIntent, this.mContext, intent);
            KeyEvent ke2 = new KeyEvent(1, keyCode);
            intent.putExtra(Intent.EXTRA_KEY_EVENT, ke2);
            MediaSessionLegacyHelper.sendKeyEvent(this.mPendingIntent, this.mContext, intent);
            if (MediaSessionLegacyHelper.DEBUG) {
                Log.m112d(MediaSessionLegacyHelper.TAG, "Sent " + keyCode + " to pending intent " + this.mPendingIntent);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class SessionHolder {
        public SessionCallback mCb;
        public int mFlags;
        public MediaButtonListener mMediaButtonListener;
        public final PendingIntent mPi;
        public MediaSession.Callback mRccListener;
        public final MediaSession mSession;

        public SessionHolder(MediaSession session, PendingIntent pi) {
            this.mSession = session;
            this.mPi = pi;
        }

        public void update() {
            if (this.mMediaButtonListener == null && this.mRccListener == null) {
                this.mSession.setCallback(null);
                this.mSession.release();
                this.mCb = null;
                MediaSessionLegacyHelper.this.mSessions.remove(this.mPi);
            } else if (this.mCb == null) {
                this.mCb = new SessionCallback();
                Handler handler = new Handler(Looper.getMainLooper());
                this.mSession.setCallback(this.mCb, handler);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class SessionCallback extends MediaSession.Callback {
            private SessionCallback() {
            }

            @Override // android.media.session.MediaSession.Callback
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                if (SessionHolder.this.mMediaButtonListener != null) {
                    SessionHolder.this.mMediaButtonListener.onMediaButtonEvent(mediaButtonIntent);
                    return true;
                }
                return true;
            }

            @Override // android.media.session.MediaSession.Callback
            public void onPlay() {
                if (SessionHolder.this.mMediaButtonListener != null) {
                    SessionHolder.this.mMediaButtonListener.onPlay();
                }
            }

            @Override // android.media.session.MediaSession.Callback
            public void onPause() {
                if (SessionHolder.this.mMediaButtonListener != null) {
                    SessionHolder.this.mMediaButtonListener.onPause();
                }
            }

            @Override // android.media.session.MediaSession.Callback
            public void onSkipToNext() {
                if (SessionHolder.this.mMediaButtonListener != null) {
                    SessionHolder.this.mMediaButtonListener.onSkipToNext();
                }
            }

            @Override // android.media.session.MediaSession.Callback
            public void onSkipToPrevious() {
                if (SessionHolder.this.mMediaButtonListener != null) {
                    SessionHolder.this.mMediaButtonListener.onSkipToPrevious();
                }
            }

            @Override // android.media.session.MediaSession.Callback
            public void onFastForward() {
                if (SessionHolder.this.mMediaButtonListener != null) {
                    SessionHolder.this.mMediaButtonListener.onFastForward();
                }
            }

            @Override // android.media.session.MediaSession.Callback
            public void onRewind() {
                if (SessionHolder.this.mMediaButtonListener != null) {
                    SessionHolder.this.mMediaButtonListener.onRewind();
                }
            }

            @Override // android.media.session.MediaSession.Callback
            public void onStop() {
                if (SessionHolder.this.mMediaButtonListener != null) {
                    SessionHolder.this.mMediaButtonListener.onStop();
                }
            }

            @Override // android.media.session.MediaSession.Callback
            public void onSeekTo(long pos) {
                if (SessionHolder.this.mRccListener != null) {
                    SessionHolder.this.mRccListener.onSeekTo(pos);
                }
            }

            @Override // android.media.session.MediaSession.Callback
            public void onSetRating(Rating rating) {
                if (SessionHolder.this.mRccListener != null) {
                    SessionHolder.this.mRccListener.onSetRating(rating);
                }
            }
        }
    }
}
