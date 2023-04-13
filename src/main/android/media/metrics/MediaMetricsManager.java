package android.media.metrics;

import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public final class MediaMetricsManager {
    public static final long INVALID_TIMESTAMP = -1;
    private static final String TAG = "MediaMetricsManager";
    private IMediaMetricsManager mService;
    private int mUserId;

    public MediaMetricsManager(IMediaMetricsManager service, int userId) {
        this.mService = service;
        this.mUserId = userId;
    }

    public void reportPlaybackMetrics(String sessionId, PlaybackMetrics metrics) {
        try {
            this.mService.reportPlaybackMetrics(sessionId, metrics, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportBundleMetrics(String sessionId, PersistableBundle metrics) {
        try {
            this.mService.reportBundleMetrics(sessionId, metrics, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportNetworkEvent(String sessionId, NetworkEvent event) {
        try {
            this.mService.reportNetworkEvent(sessionId, event, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportPlaybackStateEvent(String sessionId, PlaybackStateEvent event) {
        try {
            this.mService.reportPlaybackStateEvent(sessionId, event, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportTrackChangeEvent(String sessionId, TrackChangeEvent event) {
        try {
            this.mService.reportTrackChangeEvent(sessionId, event, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public PlaybackSession createPlaybackSession() {
        try {
            String id = this.mService.getPlaybackSessionId(this.mUserId);
            PlaybackSession session = new PlaybackSession(id, this);
            return session;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public RecordingSession createRecordingSession() {
        try {
            String id = this.mService.getRecordingSessionId(this.mUserId);
            RecordingSession session = new RecordingSession(id, this);
            return session;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public TranscodingSession createTranscodingSession() {
        try {
            String id = this.mService.getTranscodingSessionId(this.mUserId);
            TranscodingSession session = new TranscodingSession(id, this);
            return session;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public EditingSession createEditingSession() {
        try {
            String id = this.mService.getEditingSessionId(this.mUserId);
            EditingSession session = new EditingSession(id, this);
            return session;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public BundleSession createBundleSession() {
        try {
            String id = this.mService.getBundleSessionId(this.mUserId);
            BundleSession session = new BundleSession(id, this);
            return session;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void releaseSessionId(String sessionId) {
        try {
            this.mService.releaseSessionId(sessionId, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportPlaybackErrorEvent(String sessionId, PlaybackErrorEvent event) {
        try {
            this.mService.reportPlaybackErrorEvent(sessionId, event, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
