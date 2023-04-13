package com.android.server.media.metrics;

import android.content.Context;
import android.media.MediaMetrics;
import android.media.metrics.IMediaMetricsManager;
import android.media.metrics.NetworkEvent;
import android.media.metrics.PlaybackErrorEvent;
import android.media.metrics.PlaybackMetrics;
import android.media.metrics.PlaybackStateEvent;
import android.media.metrics.TrackChangeEvent;
import android.os.Binder;
import android.os.PersistableBundle;
import android.provider.DeviceConfig;
import android.util.Base64;
import android.util.Slog;
import android.util.StatsEvent;
import android.util.StatsLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.SystemService;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public final class MediaMetricsManagerService extends SystemService {
    @GuardedBy({"mLock"})
    public List<String> mAllowlist;
    @GuardedBy({"mLock"})
    public List<String> mBlockList;
    public final Context mContext;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public Integer mMode;
    @GuardedBy({"mLock"})
    public List<String> mNoUidAllowlist;
    @GuardedBy({"mLock"})
    public List<String> mNoUidBlocklist;
    public final SecureRandom mSecureRandom;

    public MediaMetricsManagerService(Context context) {
        super(context);
        this.mMode = null;
        this.mAllowlist = null;
        this.mNoUidAllowlist = null;
        this.mBlockList = null;
        this.mNoUidBlocklist = null;
        this.mLock = new Object();
        this.mContext = context;
        this.mSecureRandom = new SecureRandom();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("media_metrics", new BinderService());
        DeviceConfig.addOnPropertiesChangedListener("media", this.mContext.getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.media.metrics.MediaMetricsManagerService$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                MediaMetricsManagerService.this.updateConfigs(properties);
            }
        });
    }

    public final void updateConfigs(DeviceConfig.Properties properties) {
        synchronized (this.mLock) {
            this.mMode = Integer.valueOf(properties.getInt("media_metrics_mode", 2));
            List<String> listLocked = getListLocked("player_metrics_app_allowlist");
            if (listLocked != null || this.mMode.intValue() != 3) {
                this.mAllowlist = listLocked;
            }
            List<String> listLocked2 = getListLocked("player_metrics_per_app_attribution_allowlist");
            if (listLocked2 != null || this.mMode.intValue() != 3) {
                this.mNoUidAllowlist = listLocked2;
            }
            List<String> listLocked3 = getListLocked("player_metrics_app_blocklist");
            if (listLocked3 != null || this.mMode.intValue() != 2) {
                this.mBlockList = listLocked3;
            }
            List<String> listLocked4 = getListLocked("player_metrics_per_app_attribution_blocklist");
            if (listLocked4 != null || this.mMode.intValue() != 2) {
                this.mNoUidBlocklist = listLocked4;
            }
        }
    }

    @GuardedBy({"mLock"})
    public final List<String> getListLocked(String str) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            String string = DeviceConfig.getString("media", str, "failed_to_get");
            Binder.restoreCallingIdentity(clearCallingIdentity);
            if (string.equals("failed_to_get")) {
                Slog.d("MediaMetricsManagerService", "failed to get " + str + " from DeviceConfig");
                return null;
            }
            return Arrays.asList(string.split(","));
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    /* loaded from: classes2.dex */
    public final class BinderService extends IMediaMetricsManager.Stub {
        public BinderService() {
        }

        public void reportPlaybackMetrics(String str, PlaybackMetrics playbackMetrics, int i) {
            int loggingLevel = loggingLevel();
            if (loggingLevel == 99999) {
                return;
            }
            StatsLog.write(StatsEvent.newBuilder().setAtomId(320).writeInt(loggingLevel == 0 ? Binder.getCallingUid() : 0).writeString(str).writeLong(playbackMetrics.getMediaDurationMillis()).writeInt(playbackMetrics.getStreamSource()).writeInt(playbackMetrics.getStreamType()).writeInt(playbackMetrics.getPlaybackType()).writeInt(playbackMetrics.getDrmType()).writeInt(playbackMetrics.getContentType()).writeString(playbackMetrics.getPlayerName()).writeString(playbackMetrics.getPlayerVersion()).writeByteArray(new byte[0]).writeInt(playbackMetrics.getVideoFramesPlayed()).writeInt(playbackMetrics.getVideoFramesDropped()).writeInt(playbackMetrics.getAudioUnderrunCount()).writeLong(playbackMetrics.getNetworkBytesRead()).writeLong(playbackMetrics.getLocalBytesRead()).writeLong(playbackMetrics.getNetworkTransferDurationMillis()).writeString(Base64.encodeToString(playbackMetrics.getDrmSessionId(), 0)).usePooledBuffer().build());
        }

        public void reportBundleMetrics(String str, PersistableBundle persistableBundle, int i) {
            if (loggingLevel() != 99999 && persistableBundle.getInt("bundlesession-statsd-atom") == 322) {
                String string = persistableBundle.getString("playbackstateevent-sessionid");
                int i2 = persistableBundle.getInt("playbackstateevent-state", -1);
                long j = persistableBundle.getLong("playbackstateevent-lifetime", -1L);
                if (string == null || i2 < 0 || j < 0) {
                    Slog.d("MediaMetricsManagerService", "dropping incomplete data for atom 322: _sessionId: " + string + " _state: " + i2 + " _lifetime: " + j);
                    return;
                }
                StatsLog.write(StatsEvent.newBuilder().setAtomId(322).writeString(string).writeInt(i2).writeLong(j).usePooledBuffer().build());
            }
        }

        public void reportPlaybackStateEvent(String str, PlaybackStateEvent playbackStateEvent, int i) {
            if (loggingLevel() == 99999) {
                return;
            }
            StatsLog.write(StatsEvent.newBuilder().setAtomId(322).writeString(str).writeInt(playbackStateEvent.getState()).writeLong(playbackStateEvent.getTimeSinceCreatedMillis()).usePooledBuffer().build());
        }

        public final String getSessionIdInternal(int i) {
            byte[] bArr = new byte[12];
            MediaMetricsManagerService.this.mSecureRandom.nextBytes(bArr);
            String encodeToString = Base64.encodeToString(bArr, 11);
            new MediaMetrics.Item("metrics.manager").set(MediaMetrics.Property.EVENT, "create").set(MediaMetrics.Property.LOG_SESSION_ID, encodeToString).record();
            return encodeToString;
        }

        public void releaseSessionId(String str, int i) {
            Slog.v("MediaMetricsManagerService", "Releasing sessionId " + str + " for userId " + i + " [NOP]");
        }

        public String getPlaybackSessionId(int i) {
            return getSessionIdInternal(i);
        }

        public String getRecordingSessionId(int i) {
            return getSessionIdInternal(i);
        }

        public String getTranscodingSessionId(int i) {
            return getSessionIdInternal(i);
        }

        public String getEditingSessionId(int i) {
            return getSessionIdInternal(i);
        }

        public String getBundleSessionId(int i) {
            return getSessionIdInternal(i);
        }

        public void reportPlaybackErrorEvent(String str, PlaybackErrorEvent playbackErrorEvent, int i) {
            if (loggingLevel() == 99999) {
                return;
            }
            StatsLog.write(StatsEvent.newBuilder().setAtomId(323).writeString(str).writeString(playbackErrorEvent.getExceptionStack()).writeInt(playbackErrorEvent.getErrorCode()).writeInt(playbackErrorEvent.getSubErrorCode()).writeLong(playbackErrorEvent.getTimeSinceCreatedMillis()).usePooledBuffer().build());
        }

        public void reportNetworkEvent(String str, NetworkEvent networkEvent, int i) {
            if (loggingLevel() == 99999) {
                return;
            }
            StatsLog.write(StatsEvent.newBuilder().setAtomId(321).writeString(str).writeInt(networkEvent.getNetworkType()).writeLong(networkEvent.getTimeSinceCreatedMillis()).usePooledBuffer().build());
        }

        public void reportTrackChangeEvent(String str, TrackChangeEvent trackChangeEvent, int i) {
            if (loggingLevel() == 99999) {
                return;
            }
            StatsLog.write(StatsEvent.newBuilder().setAtomId((int) FrameworkStatsLog.f56x60da79b1).writeString(str).writeInt(trackChangeEvent.getTrackState()).writeInt(trackChangeEvent.getTrackChangeReason()).writeString(trackChangeEvent.getContainerMimeType()).writeString(trackChangeEvent.getSampleMimeType()).writeString(trackChangeEvent.getCodecName()).writeInt(trackChangeEvent.getBitrate()).writeLong(trackChangeEvent.getTimeSinceCreatedMillis()).writeInt(trackChangeEvent.getTrackType()).writeString(trackChangeEvent.getLanguage()).writeString(trackChangeEvent.getLanguageRegion()).writeInt(trackChangeEvent.getChannelCount()).writeInt(trackChangeEvent.getAudioSampleRate()).writeInt(trackChangeEvent.getWidth()).writeInt(trackChangeEvent.getHeight()).writeFloat(trackChangeEvent.getVideoFrameRate()).usePooledBuffer().build());
        }

        public final int loggingLevel() {
            synchronized (MediaMetricsManagerService.this.mLock) {
                int callingUid = Binder.getCallingUid();
                if (MediaMetricsManagerService.this.mMode == null) {
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    MediaMetricsManagerService.this.mMode = Integer.valueOf(DeviceConfig.getInt("media", "media_metrics_mode", 2));
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
                if (MediaMetricsManagerService.this.mMode.intValue() == 1) {
                    return 0;
                }
                if (MediaMetricsManagerService.this.mMode.intValue() == 0) {
                    Slog.v("MediaMetricsManagerService", "Logging level blocked: MEDIA_METRICS_MODE_OFF");
                    return 99999;
                }
                String[] packagesForUid = MediaMetricsManagerService.this.getContext().getPackageManager().getPackagesForUid(callingUid);
                if (packagesForUid != null && packagesForUid.length != 0) {
                    if (MediaMetricsManagerService.this.mMode.intValue() == 2) {
                        if (MediaMetricsManagerService.this.mBlockList == null) {
                            MediaMetricsManagerService mediaMetricsManagerService = MediaMetricsManagerService.this;
                            mediaMetricsManagerService.mBlockList = mediaMetricsManagerService.getListLocked("player_metrics_app_blocklist");
                            if (MediaMetricsManagerService.this.mBlockList == null) {
                                Slog.v("MediaMetricsManagerService", "Logging level blocked: Failed to get PLAYER_METRICS_APP_BLOCKLIST.");
                                return 99999;
                            }
                        }
                        Integer loggingLevelInternal = loggingLevelInternal(packagesForUid, MediaMetricsManagerService.this.mBlockList, "player_metrics_app_blocklist");
                        if (loggingLevelInternal != null) {
                            return loggingLevelInternal.intValue();
                        }
                        if (MediaMetricsManagerService.this.mNoUidBlocklist == null) {
                            MediaMetricsManagerService mediaMetricsManagerService2 = MediaMetricsManagerService.this;
                            mediaMetricsManagerService2.mNoUidBlocklist = mediaMetricsManagerService2.getListLocked("player_metrics_per_app_attribution_blocklist");
                            if (MediaMetricsManagerService.this.mNoUidBlocklist == null) {
                                Slog.v("MediaMetricsManagerService", "Logging level blocked: Failed to get PLAYER_METRICS_PER_APP_ATTRIBUTION_BLOCKLIST.");
                                return 99999;
                            }
                        }
                        Integer loggingLevelInternal2 = loggingLevelInternal(packagesForUid, MediaMetricsManagerService.this.mNoUidBlocklist, "player_metrics_per_app_attribution_blocklist");
                        if (loggingLevelInternal2 != null) {
                            return loggingLevelInternal2.intValue();
                        }
                        return 0;
                    } else if (MediaMetricsManagerService.this.mMode.intValue() == 3) {
                        if (MediaMetricsManagerService.this.mNoUidAllowlist == null) {
                            MediaMetricsManagerService mediaMetricsManagerService3 = MediaMetricsManagerService.this;
                            mediaMetricsManagerService3.mNoUidAllowlist = mediaMetricsManagerService3.getListLocked("player_metrics_per_app_attribution_allowlist");
                            if (MediaMetricsManagerService.this.mNoUidAllowlist == null) {
                                Slog.v("MediaMetricsManagerService", "Logging level blocked: Failed to get PLAYER_METRICS_PER_APP_ATTRIBUTION_ALLOWLIST.");
                                return 99999;
                            }
                        }
                        Integer loggingLevelInternal3 = loggingLevelInternal(packagesForUid, MediaMetricsManagerService.this.mNoUidAllowlist, "player_metrics_per_app_attribution_allowlist");
                        if (loggingLevelInternal3 != null) {
                            return loggingLevelInternal3.intValue();
                        }
                        if (MediaMetricsManagerService.this.mAllowlist == null) {
                            MediaMetricsManagerService mediaMetricsManagerService4 = MediaMetricsManagerService.this;
                            mediaMetricsManagerService4.mAllowlist = mediaMetricsManagerService4.getListLocked("player_metrics_app_allowlist");
                            if (MediaMetricsManagerService.this.mAllowlist == null) {
                                Slog.v("MediaMetricsManagerService", "Logging level blocked: Failed to get PLAYER_METRICS_APP_ALLOWLIST.");
                                return 99999;
                            }
                        }
                        Integer loggingLevelInternal4 = loggingLevelInternal(packagesForUid, MediaMetricsManagerService.this.mAllowlist, "player_metrics_app_allowlist");
                        if (loggingLevelInternal4 != null) {
                            return loggingLevelInternal4.intValue();
                        }
                        Slog.v("MediaMetricsManagerService", "Logging level blocked: Not detected in any allowlist.");
                        return 99999;
                    } else {
                        Slog.v("MediaMetricsManagerService", "Logging level blocked: Blocked by default.");
                        return 99999;
                    }
                }
                Slog.d("MediaMetricsManagerService", "empty package from uid " + callingUid);
                return MediaMetricsManagerService.this.mMode.intValue() == 2 ? 1000 : 99999;
            }
        }

        public final Integer loggingLevelInternal(String[] strArr, List<String> list, String str) {
            if (inList(strArr, list)) {
                return Integer.valueOf(listNameToLoggingLevel(str));
            }
            return null;
        }

        public final boolean inList(String[] strArr, List<String> list) {
            for (String str : strArr) {
                for (String str2 : list) {
                    if (str.equals(str2)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public final int listNameToLoggingLevel(String str) {
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -1894232751:
                    if (str.equals("player_metrics_per_app_attribution_blocklist")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1289480849:
                    if (str.equals("player_metrics_app_allowlist")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1900310029:
                    if (str.equals("player_metrics_per_app_attribution_allowlist")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 2:
                    return 1000;
                case 1:
                    return 0;
                default:
                    return 99999;
            }
        }
    }
}
