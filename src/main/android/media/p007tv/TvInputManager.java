package android.media.p007tv;

import android.annotation.SystemApi;
import android.content.AttributionSource;
import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioDeviceInfo;
import android.media.AudioPresentation;
import android.media.PlaybackParams;
import android.media.p007tv.ITvInputClient;
import android.media.p007tv.ITvInputHardwareCallback;
import android.media.p007tv.ITvInputManagerCallback;
import android.media.p007tv.TvInputManager;
import android.media.p007tv.interactive.TvInteractiveAppManager;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pools;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventSender;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
/* renamed from: android.media.tv.TvInputManager */
/* loaded from: classes2.dex */
public final class TvInputManager {
    public static final String ACTION_BLOCKED_RATINGS_CHANGED = "android.media.tv.action.BLOCKED_RATINGS_CHANGED";
    public static final String ACTION_PARENTAL_CONTROLS_ENABLED_CHANGED = "android.media.tv.action.PARENTAL_CONTROLS_ENABLED_CHANGED";
    public static final String ACTION_QUERY_CONTENT_RATING_SYSTEMS = "android.media.tv.action.QUERY_CONTENT_RATING_SYSTEMS";
    public static final String ACTION_SETUP_INPUTS = "android.media.tv.action.SETUP_INPUTS";
    public static final String ACTION_VIEW_RECORDING_SCHEDULES = "android.media.tv.action.VIEW_RECORDING_SCHEDULES";
    public static final int BROADCAST_INFO_STREAM_EVENT = 5;
    public static final int BROADCAST_INFO_TYPE_COMMAND = 7;
    public static final int BROADCAST_INFO_TYPE_DSMCC = 6;
    public static final int BROADCAST_INFO_TYPE_PES = 4;
    public static final int BROADCAST_INFO_TYPE_SECTION = 3;
    public static final int BROADCAST_INFO_TYPE_TABLE = 2;
    public static final int BROADCAST_INFO_TYPE_TIMELINE = 8;
    public static final int BROADCAST_INFO_TYPE_TS = 1;
    public static final int DVB_DEVICE_DEMUX = 0;
    public static final int DVB_DEVICE_DVR = 1;
    static final int DVB_DEVICE_END = 2;
    public static final int DVB_DEVICE_FRONTEND = 2;
    static final int DVB_DEVICE_START = 0;
    public static final int INPUT_STATE_CONNECTED = 0;
    public static final int INPUT_STATE_CONNECTED_STANDBY = 1;
    public static final int INPUT_STATE_DISCONNECTED = 2;
    public static final String META_DATA_CONTENT_RATING_SYSTEMS = "android.media.tv.metadata.CONTENT_RATING_SYSTEMS";
    static final int RECORDING_ERROR_END = 2;
    public static final int RECORDING_ERROR_INSUFFICIENT_SPACE = 1;
    public static final int RECORDING_ERROR_RESOURCE_BUSY = 2;
    static final int RECORDING_ERROR_START = 0;
    public static final int RECORDING_ERROR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_LOST = 1;
    public static final int SIGNAL_STRENGTH_STRONG = 3;
    public static final int SIGNAL_STRENGTH_WEAK = 2;
    private static final String TAG = "TvInputManager";
    public static final long TIME_SHIFT_INVALID_TIME = Long.MIN_VALUE;
    public static final int TIME_SHIFT_MODE_AUTO = 4;
    public static final int TIME_SHIFT_MODE_LOCAL = 2;
    public static final int TIME_SHIFT_MODE_NETWORK = 3;
    public static final int TIME_SHIFT_MODE_OFF = 1;
    public static final int TIME_SHIFT_STATUS_AVAILABLE = 3;
    public static final int TIME_SHIFT_STATUS_UNAVAILABLE = 2;
    public static final int TIME_SHIFT_STATUS_UNKNOWN = 0;
    public static final int TIME_SHIFT_STATUS_UNSUPPORTED = 1;
    public static final String TV_MESSAGE_KEY_STREAM_ID = "android.media.tv.TvInputManager.stream_id";
    public static final String TV_MESSAGE_TYPE_CLOSED_CAPTION = "CC";
    public static final String TV_MESSAGE_TYPE_WATERMARK = "Watermark";
    public static final int UNKNOWN_CLIENT_PID = -1;
    public static final int VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY = 4;
    public static final int VIDEO_UNAVAILABLE_REASON_BUFFERING = 3;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_BLACKOUT = 16;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_CARD_INVALID = 15;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_CARD_MUTE = 14;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_INSUFFICIENT_OUTPUT_PROTECTION = 7;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_LICENSE_EXPIRED = 10;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_NEED_ACTIVATION = 11;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_NEED_PAIRING = 12;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_NO_CARD = 13;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_NO_LICENSE = 9;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_PVR_RECORDING_NOT_ALLOWED = 8;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_REBOOTING = 17;
    public static final int VIDEO_UNAVAILABLE_REASON_CAS_UNKNOWN = 18;
    static final int VIDEO_UNAVAILABLE_REASON_END = 18;
    public static final int VIDEO_UNAVAILABLE_REASON_INSUFFICIENT_RESOURCE = 6;
    public static final int VIDEO_UNAVAILABLE_REASON_NOT_CONNECTED = 5;
    static final int VIDEO_UNAVAILABLE_REASON_START = 0;
    public static final int VIDEO_UNAVAILABLE_REASON_TUNING = 1;
    public static final int VIDEO_UNAVAILABLE_REASON_UNKNOWN = 0;
    public static final int VIDEO_UNAVAILABLE_REASON_WEAK_SIGNAL = 2;
    private final List<TvInputCallbackRecord> mCallbackRecords;
    private final ITvInputClient mClient;
    private final Object mLock;
    private int mNextSeq;
    private final ITvInputManager mService;
    private final SparseArray<SessionCallbackRecord> mSessionCallbackRecordMap;
    private final Map<String, Integer> mStateMap;
    private final int mUserId;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvInputManager$BroadcastInfoType */
    /* loaded from: classes2.dex */
    public @interface BroadcastInfoType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvInputManager$DvbDeviceType */
    /* loaded from: classes2.dex */
    public @interface DvbDeviceType {
    }

    @SystemApi
    /* renamed from: android.media.tv.TvInputManager$HardwareCallback */
    /* loaded from: classes2.dex */
    public static abstract class HardwareCallback {
        public abstract void onReleased();

        public abstract void onStreamConfigChanged(TvStreamConfig[] tvStreamConfigArr);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvInputManager$InputState */
    /* loaded from: classes2.dex */
    public @interface InputState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvInputManager$RecordingError */
    /* loaded from: classes2.dex */
    public @interface RecordingError {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvInputManager$SignalStrength */
    /* loaded from: classes2.dex */
    public @interface SignalStrength {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvInputManager$TimeShiftMode */
    /* loaded from: classes2.dex */
    public @interface TimeShiftMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvInputManager$TimeShiftStatus */
    /* loaded from: classes2.dex */
    public @interface TimeShiftStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvInputManager$TvMessageType */
    /* loaded from: classes2.dex */
    public @interface TvMessageType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvInputManager$VideoUnavailableReason */
    /* loaded from: classes2.dex */
    public @interface VideoUnavailableReason {
    }

    /* renamed from: android.media.tv.TvInputManager$SessionCallback */
    /* loaded from: classes2.dex */
    public static abstract class SessionCallback {
        public void onSessionCreated(Session session) {
        }

        public void onSessionReleased(Session session) {
        }

        public void onChannelRetuned(Session session, Uri channelUri) {
        }

        public void onAudioPresentationsChanged(Session session, List<AudioPresentation> audioPresentations) {
        }

        public void onAudioPresentationSelected(Session session, int presentationId, int programId) {
        }

        public void onTracksChanged(Session session, List<TvTrackInfo> tracks) {
        }

        public void onTrackSelected(Session session, int type, String trackId) {
        }

        public void onVideoSizeChanged(Session session, int width, int height) {
        }

        public void onVideoAvailable(Session session) {
        }

        public void onVideoUnavailable(Session session, int reason) {
        }

        public void onContentAllowed(Session session) {
        }

        public void onContentBlocked(Session session, TvContentRating rating) {
        }

        public void onLayoutSurface(Session session, int left, int top, int right, int bottom) {
        }

        public void onSessionEvent(Session session, String eventType, Bundle eventArgs) {
        }

        public void onTimeShiftStatusChanged(Session session, int status) {
        }

        public void onTimeShiftStartPositionChanged(Session session, long timeMs) {
        }

        public void onTimeShiftCurrentPositionChanged(Session session, long timeMs) {
        }

        public void onAitInfoUpdated(Session session, AitInfo aitInfo) {
        }

        public void onSignalStrengthUpdated(Session session, int strength) {
        }

        public void onCueingMessageAvailability(Session session, boolean available) {
        }

        public void onTimeShiftMode(Session session, int mode) {
        }

        public void onAvailableSpeeds(Session session, float[] speeds) {
        }

        public void onTuned(Session session, Uri channelUri) {
        }

        public void onTvMessage(Session session, String type, Bundle data) {
        }

        void onRecordingStopped(Session session, Uri recordedProgramUri) {
        }

        void onError(Session session, int error) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.media.tv.TvInputManager$SessionCallbackRecord */
    /* loaded from: classes2.dex */
    public static final class SessionCallbackRecord {
        private final Handler mHandler;
        private Session mSession;
        private final SessionCallback mSessionCallback;

        SessionCallbackRecord(SessionCallback sessionCallback, Handler handler) {
            this.mSessionCallback = sessionCallback;
            this.mHandler = handler;
        }

        void postSessionCreated(final Session session) {
            this.mSession = session;
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.1
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onSessionCreated(session);
                }
            });
        }

        void postSessionReleased() {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.2
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onSessionReleased(SessionCallbackRecord.this.mSession);
                }
            });
        }

        void postChannelRetuned(final Uri channelUri) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.3
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onChannelRetuned(SessionCallbackRecord.this.mSession, channelUri);
                }
            });
        }

        void postAudioPresentationsChanged(final List<AudioPresentation> audioPresentations) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.4
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onAudioPresentationsChanged(SessionCallbackRecord.this.mSession, audioPresentations);
                }
            });
        }

        void postAudioPresentationSelected(final int presentationId, final int programId) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.5
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onAudioPresentationSelected(SessionCallbackRecord.this.mSession, presentationId, programId);
                }
            });
        }

        void postTracksChanged(final List<TvTrackInfo> tracks) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.6
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onTracksChanged(SessionCallbackRecord.this.mSession, tracks);
                    if (SessionCallbackRecord.this.mSession.mIAppNotificationEnabled && SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                        SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyTracksChanged(tracks);
                    }
                }
            });
        }

        void postTrackSelected(final int type, final String trackId) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.7
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onTrackSelected(SessionCallbackRecord.this.mSession, type, trackId);
                    if (SessionCallbackRecord.this.mSession.mIAppNotificationEnabled && SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                        SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyTrackSelected(type, trackId);
                    }
                }
            });
        }

        void postVideoSizeChanged(final int width, final int height) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.8
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onVideoSizeChanged(SessionCallbackRecord.this.mSession, width, height);
                }
            });
        }

        void postVideoAvailable() {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.9
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onVideoAvailable(SessionCallbackRecord.this.mSession);
                    if (SessionCallbackRecord.this.mSession.mIAppNotificationEnabled && SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                        SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyVideoAvailable();
                    }
                }
            });
        }

        void postVideoUnavailable(final int reason) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.10
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onVideoUnavailable(SessionCallbackRecord.this.mSession, reason);
                    if (SessionCallbackRecord.this.mSession.mIAppNotificationEnabled && SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                        SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyVideoUnavailable(reason);
                    }
                }
            });
        }

        void postContentAllowed() {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.11
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onContentAllowed(SessionCallbackRecord.this.mSession);
                    if (SessionCallbackRecord.this.mSession.mIAppNotificationEnabled && SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                        SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyContentAllowed();
                    }
                }
            });
        }

        void postContentBlocked(final TvContentRating rating) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.12
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onContentBlocked(SessionCallbackRecord.this.mSession, rating);
                    if (SessionCallbackRecord.this.mSession.mIAppNotificationEnabled && SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                        SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyContentBlocked(rating);
                    }
                }
            });
        }

        void postLayoutSurface(final int left, final int top, final int right, final int bottom) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.13
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onLayoutSurface(SessionCallbackRecord.this.mSession, left, top, right, bottom);
                }
            });
        }

        void postSessionEvent(final String eventType, final Bundle eventArgs) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.14
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onSessionEvent(SessionCallbackRecord.this.mSession, eventType, eventArgs);
                }
            });
        }

        void postTimeShiftStatusChanged(final int status) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.15
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onTimeShiftStatusChanged(SessionCallbackRecord.this.mSession, status);
                }
            });
        }

        void postTimeShiftStartPositionChanged(final long timeMs) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.16
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onTimeShiftStartPositionChanged(SessionCallbackRecord.this.mSession, timeMs);
                }
            });
        }

        void postTimeShiftCurrentPositionChanged(final long timeMs) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.17
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onTimeShiftCurrentPositionChanged(SessionCallbackRecord.this.mSession, timeMs);
                }
            });
        }

        void postAitInfoUpdated(final AitInfo aitInfo) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.18
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onAitInfoUpdated(SessionCallbackRecord.this.mSession, aitInfo);
                }
            });
        }

        void postSignalStrength(final int strength) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.19
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onSignalStrengthUpdated(SessionCallbackRecord.this.mSession, strength);
                    if (SessionCallbackRecord.this.mSession.mIAppNotificationEnabled && SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                        SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifySignalStrength(strength);
                    }
                }
            });
        }

        void postCueingMessageAvailability(final boolean available) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.20
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onCueingMessageAvailability(SessionCallbackRecord.this.mSession, available);
                }
            });
        }

        void postTimeShiftMode(final int mode) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.21
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onTimeShiftMode(SessionCallbackRecord.this.mSession, mode);
                }
            });
        }

        void postAvailableSpeeds(final float[] speeds) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.22
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onAvailableSpeeds(SessionCallbackRecord.this.mSession, speeds);
                }
            });
        }

        void postTuned(final Uri channelUri) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.23
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onTuned(SessionCallbackRecord.this.mSession, channelUri);
                    if (SessionCallbackRecord.this.mSession.mIAppNotificationEnabled && SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                        SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyTuned(channelUri);
                    }
                }
            });
        }

        void postTvMessage(final String type, final Bundle data) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.24
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onTvMessage(SessionCallbackRecord.this.mSession, type, data);
                    if (SessionCallbackRecord.this.mSession.mIAppNotificationEnabled && SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                        SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyTvMessage(type, data);
                    }
                }
            });
        }

        void postRecordingStopped(final Uri recordedProgramUri) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.25
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onRecordingStopped(SessionCallbackRecord.this.mSession, recordedProgramUri);
                }
            });
        }

        void postError(final int error) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.26
                @Override // java.lang.Runnable
                public void run() {
                    SessionCallbackRecord.this.mSessionCallback.onError(SessionCallbackRecord.this.mSession, error);
                }
            });
        }

        void postBroadcastInfoResponse(final BroadcastInfoResponse response) {
            if (this.mSession.mIAppNotificationEnabled) {
                this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.27
                    @Override // java.lang.Runnable
                    public void run() {
                        if (SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                            SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyBroadcastInfoResponse(response);
                        }
                    }
                });
            }
        }

        void postAdResponse(final AdResponse response) {
            if (this.mSession.mIAppNotificationEnabled) {
                this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.28
                    @Override // java.lang.Runnable
                    public void run() {
                        if (SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                            SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyAdResponse(response);
                        }
                    }
                });
            }
        }

        void postAdBufferConsumed(final AdBuffer buffer) {
            if (this.mSession.mIAppNotificationEnabled) {
                this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.SessionCallbackRecord.29
                    @Override // java.lang.Runnable
                    public void run() {
                        if (SessionCallbackRecord.this.mSession.getInteractiveAppSession() != null) {
                            SessionCallbackRecord.this.mSession.getInteractiveAppSession().notifyAdBufferConsumed(buffer);
                        }
                    }
                });
            }
        }
    }

    /* renamed from: android.media.tv.TvInputManager$TvInputCallback */
    /* loaded from: classes2.dex */
    public static abstract class TvInputCallback {
        public void onInputStateChanged(String inputId, int state) {
        }

        public void onInputAdded(String inputId) {
        }

        public void onInputRemoved(String inputId) {
        }

        public void onInputUpdated(String inputId) {
        }

        public void onTvInputInfoUpdated(TvInputInfo inputInfo) {
        }

        @SystemApi
        public void onCurrentTunedInfosUpdated(List<TunedInfo> tunedInfos) {
        }
    }

    /* renamed from: android.media.tv.TvInputManager$TvInputCallbackRecord */
    /* loaded from: classes2.dex */
    private static final class TvInputCallbackRecord {
        private final TvInputCallback mCallback;
        private final Handler mHandler;

        public TvInputCallbackRecord(TvInputCallback callback, Handler handler) {
            this.mCallback = callback;
            this.mHandler = handler;
        }

        public TvInputCallback getCallback() {
            return this.mCallback;
        }

        public void postInputAdded(final String inputId) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.TvInputCallbackRecord.1
                @Override // java.lang.Runnable
                public void run() {
                    TvInputCallbackRecord.this.mCallback.onInputAdded(inputId);
                }
            });
        }

        public void postInputRemoved(final String inputId) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.TvInputCallbackRecord.2
                @Override // java.lang.Runnable
                public void run() {
                    TvInputCallbackRecord.this.mCallback.onInputRemoved(inputId);
                }
            });
        }

        public void postInputUpdated(final String inputId) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.TvInputCallbackRecord.3
                @Override // java.lang.Runnable
                public void run() {
                    TvInputCallbackRecord.this.mCallback.onInputUpdated(inputId);
                }
            });
        }

        public void postInputStateChanged(final String inputId, final int state) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.TvInputCallbackRecord.4
                @Override // java.lang.Runnable
                public void run() {
                    TvInputCallbackRecord.this.mCallback.onInputStateChanged(inputId, state);
                }
            });
        }

        public void postTvInputInfoUpdated(final TvInputInfo inputInfo) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.TvInputCallbackRecord.5
                @Override // java.lang.Runnable
                public void run() {
                    TvInputCallbackRecord.this.mCallback.onTvInputInfoUpdated(inputInfo);
                }
            });
        }

        public void postCurrentTunedInfosUpdated(final List<TunedInfo> currentTunedInfos) {
            this.mHandler.post(new Runnable() { // from class: android.media.tv.TvInputManager.TvInputCallbackRecord.6
                @Override // java.lang.Runnable
                public void run() {
                    TvInputCallbackRecord.this.mCallback.onCurrentTunedInfosUpdated(currentTunedInfos);
                }
            });
        }
    }

    public TvInputManager(ITvInputManager service, int userId) {
        Object obj = new Object();
        this.mLock = obj;
        this.mCallbackRecords = new ArrayList();
        this.mStateMap = new ArrayMap();
        this.mSessionCallbackRecordMap = new SparseArray<>();
        this.mService = service;
        this.mUserId = userId;
        this.mClient = new ITvInputClient.Stub() { // from class: android.media.tv.TvInputManager.1
            @Override // android.media.p007tv.ITvInputClient
            public void onSessionCreated(String inputId, IBinder token, InputChannel channel, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for " + token);
                        return;
                    }
                    Session session = null;
                    if (token != null) {
                        session = new Session(token, channel, TvInputManager.this.mService, TvInputManager.this.mUserId, seq, TvInputManager.this.mSessionCallbackRecordMap);
                    } else {
                        TvInputManager.this.mSessionCallbackRecordMap.delete(seq);
                    }
                    record.postSessionCreated(session);
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onSessionReleased(int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    TvInputManager.this.mSessionCallbackRecordMap.delete(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq:" + seq);
                        return;
                    }
                    record.mSession.releaseInternal();
                    record.postSessionReleased();
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onChannelRetuned(Uri channelUri, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postChannelRetuned(channelUri);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onAudioPresentationsChanged(List<AudioPresentation> audioPresentations, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                        return;
                    }
                    if (record.mSession.updateAudioPresentations(audioPresentations)) {
                        record.postAudioPresentationsChanged(audioPresentations);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onAudioPresentationSelected(int presentationId, int programId, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                        return;
                    }
                    if (record.mSession.updateAudioPresentationSelection(presentationId, programId)) {
                        record.postAudioPresentationSelected(presentationId, programId);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onTracksChanged(List<TvTrackInfo> tracks, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                        return;
                    }
                    if (record.mSession.updateTracks(tracks)) {
                        record.postTracksChanged(tracks);
                        postVideoSizeChangedIfNeededLocked(record);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onTrackSelected(int type, String trackId, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                        return;
                    }
                    if (record.mSession.updateTrackSelection(type, trackId)) {
                        record.postTrackSelected(type, trackId);
                        postVideoSizeChangedIfNeededLocked(record);
                    }
                }
            }

            private void postVideoSizeChangedIfNeededLocked(SessionCallbackRecord record) {
                TvTrackInfo track = record.mSession.getVideoTrackToNotify();
                if (track != null) {
                    record.postVideoSizeChanged(track.getVideoWidth(), track.getVideoHeight());
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onVideoAvailable(int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postVideoAvailable();
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onVideoUnavailable(int reason, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postVideoUnavailable(reason);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onContentAllowed(int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postContentAllowed();
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onContentBlocked(String rating, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postContentBlocked(TvContentRating.unflattenFromString(rating));
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onLayoutSurface(int left, int top, int right, int bottom, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postLayoutSurface(left, top, right, bottom);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onSessionEvent(String eventType, Bundle eventArgs, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postSessionEvent(eventType, eventArgs);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onTimeShiftStatusChanged(int status, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postTimeShiftStatusChanged(status);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onTimeShiftStartPositionChanged(long timeMs, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postTimeShiftStartPositionChanged(timeMs);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onTimeShiftCurrentPositionChanged(long timeMs, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postTimeShiftCurrentPositionChanged(timeMs);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onAitInfoUpdated(AitInfo aitInfo, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postAitInfoUpdated(aitInfo);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onSignalStrength(int strength, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postSignalStrength(strength);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onCueingMessageAvailability(boolean available, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postCueingMessageAvailability(available);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onTimeShiftMode(int mode, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postTimeShiftMode(mode);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onAvailableSpeeds(float[] speeds, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postAvailableSpeeds(speeds);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onTuned(Uri channelUri, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postTuned(channelUri);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onTvMessage(String type, Bundle data, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postTvMessage(type, data);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onRecordingStopped(Uri recordedProgramUri, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postRecordingStopped(recordedProgramUri);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onError(int error, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postError(error);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onBroadcastInfoResponse(BroadcastInfoResponse response, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postBroadcastInfoResponse(response);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onAdResponse(AdResponse response, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postAdResponse(response);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputClient
            public void onAdBufferConsumed(AdBuffer buffer, int seq) {
                synchronized (TvInputManager.this.mSessionCallbackRecordMap) {
                    SessionCallbackRecord record = (SessionCallbackRecord) TvInputManager.this.mSessionCallbackRecordMap.get(seq);
                    if (record == null) {
                        Log.m110e(TvInputManager.TAG, "Callback not found for seq " + seq);
                    } else {
                        record.postAdBufferConsumed(buffer);
                    }
                }
            }
        };
        ITvInputManagerCallback managerCallback = new ITvInputManagerCallback.Stub() { // from class: android.media.tv.TvInputManager.2
            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onInputAdded(String inputId) {
                synchronized (TvInputManager.this.mLock) {
                    TvInputManager.this.mStateMap.put(inputId, 0);
                    for (TvInputCallbackRecord record : TvInputManager.this.mCallbackRecords) {
                        record.postInputAdded(inputId);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onInputRemoved(String inputId) {
                synchronized (TvInputManager.this.mLock) {
                    TvInputManager.this.mStateMap.remove(inputId);
                    for (TvInputCallbackRecord record : TvInputManager.this.mCallbackRecords) {
                        record.postInputRemoved(inputId);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onInputUpdated(String inputId) {
                synchronized (TvInputManager.this.mLock) {
                    for (TvInputCallbackRecord record : TvInputManager.this.mCallbackRecords) {
                        record.postInputUpdated(inputId);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onInputStateChanged(String inputId, int state) {
                synchronized (TvInputManager.this.mLock) {
                    TvInputManager.this.mStateMap.put(inputId, Integer.valueOf(state));
                    for (TvInputCallbackRecord record : TvInputManager.this.mCallbackRecords) {
                        record.postInputStateChanged(inputId, state);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onTvInputInfoUpdated(TvInputInfo inputInfo) {
                synchronized (TvInputManager.this.mLock) {
                    for (TvInputCallbackRecord record : TvInputManager.this.mCallbackRecords) {
                        record.postTvInputInfoUpdated(inputInfo);
                    }
                }
            }

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onCurrentTunedInfosUpdated(List<TunedInfo> currentTunedInfos) {
                synchronized (TvInputManager.this.mLock) {
                    for (TvInputCallbackRecord record : TvInputManager.this.mCallbackRecords) {
                        record.postCurrentTunedInfosUpdated(currentTunedInfos);
                    }
                }
            }
        };
        if (service != null) {
            try {
                service.registerCallback(managerCallback, userId);
                List<TvInputInfo> infos = service.getTvInputList(userId);
                synchronized (obj) {
                    for (TvInputInfo info : infos) {
                        String inputId = info.getId();
                        this.mStateMap.put(inputId, Integer.valueOf(this.mService.getTvInputState(inputId, this.mUserId)));
                    }
                }
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public List<TvInputInfo> getTvInputList() {
        try {
            return this.mService.getTvInputList(this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public TvInputInfo getTvInputInfo(String inputId) {
        Preconditions.checkNotNull(inputId);
        try {
            return this.mService.getTvInputInfo(inputId, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void updateTvInputInfo(TvInputInfo inputInfo) {
        Preconditions.checkNotNull(inputInfo);
        try {
            this.mService.updateTvInputInfo(inputInfo, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getInputState(String inputId) {
        Preconditions.checkNotNull(inputId);
        synchronized (this.mLock) {
            Integer state = this.mStateMap.get(inputId);
            if (state == null) {
                Log.m104w(TAG, "Unrecognized input ID: " + inputId);
                return 2;
            }
            return state.intValue();
        }
    }

    @SystemApi
    public List<String> getAvailableExtensionInterfaceNames(String inputId) {
        Preconditions.checkNotNull(inputId);
        try {
            return this.mService.getAvailableExtensionInterfaceNames(inputId, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public IBinder getExtensionInterface(String inputId, String name) {
        Preconditions.checkNotNull(inputId);
        Preconditions.checkNotNull(name);
        try {
            return this.mService.getExtensionInterface(inputId, name, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void registerCallback(TvInputCallback callback, Handler handler) {
        Preconditions.checkNotNull(callback);
        Preconditions.checkNotNull(handler);
        synchronized (this.mLock) {
            this.mCallbackRecords.add(new TvInputCallbackRecord(callback, handler));
        }
    }

    public void unregisterCallback(TvInputCallback callback) {
        Preconditions.checkNotNull(callback);
        synchronized (this.mLock) {
            Iterator<TvInputCallbackRecord> it = this.mCallbackRecords.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                TvInputCallbackRecord record = it.next();
                if (record.getCallback() == callback) {
                    it.remove();
                    break;
                }
            }
        }
    }

    public boolean isParentalControlsEnabled() {
        try {
            return this.mService.isParentalControlsEnabled(this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setParentalControlsEnabled(boolean enabled) {
        try {
            this.mService.setParentalControlsEnabled(enabled, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isRatingBlocked(TvContentRating rating) {
        Preconditions.checkNotNull(rating);
        try {
            return this.mService.isRatingBlocked(rating.flattenToString(), this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<TvContentRating> getBlockedRatings() {
        try {
            List<TvContentRating> ratings = new ArrayList<>();
            for (String rating : this.mService.getBlockedRatings(this.mUserId)) {
                ratings.add(TvContentRating.unflattenFromString(rating));
            }
            return ratings;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void addBlockedRating(TvContentRating rating) {
        Preconditions.checkNotNull(rating);
        try {
            this.mService.addBlockedRating(rating.flattenToString(), this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void removeBlockedRating(TvContentRating rating) {
        Preconditions.checkNotNull(rating);
        try {
            this.mService.removeBlockedRating(rating.flattenToString(), this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<TvContentRatingSystemInfo> getTvContentRatingSystemList() {
        try {
            return this.mService.getTvContentRatingSystemList(this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void notifyPreviewProgramBrowsableDisabled(String packageName, long programId) {
        Intent intent = new Intent();
        intent.setAction(TvContract.ACTION_PREVIEW_PROGRAM_BROWSABLE_DISABLED);
        intent.putExtra(TvContract.EXTRA_PREVIEW_PROGRAM_ID, programId);
        intent.setPackage(packageName);
        try {
            this.mService.sendTvInputNotifyIntent(intent, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void notifyWatchNextProgramBrowsableDisabled(String packageName, long programId) {
        Intent intent = new Intent();
        intent.setAction(TvContract.ACTION_WATCH_NEXT_PROGRAM_BROWSABLE_DISABLED);
        intent.putExtra(TvContract.EXTRA_WATCH_NEXT_PROGRAM_ID, programId);
        intent.setPackage(packageName);
        try {
            this.mService.sendTvInputNotifyIntent(intent, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void notifyPreviewProgramAddedToWatchNext(String packageName, long previewProgramId, long watchNextProgramId) {
        Intent intent = new Intent();
        intent.setAction(TvContract.ACTION_PREVIEW_PROGRAM_ADDED_TO_WATCH_NEXT);
        intent.putExtra(TvContract.EXTRA_PREVIEW_PROGRAM_ID, previewProgramId);
        intent.putExtra(TvContract.EXTRA_WATCH_NEXT_PROGRAM_ID, watchNextProgramId);
        intent.setPackage(packageName);
        try {
            this.mService.sendTvInputNotifyIntent(intent, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void createSession(String inputId, AttributionSource tvAppAttributionSource, SessionCallback callback, Handler handler) {
        createSessionInternal(inputId, tvAppAttributionSource, false, callback, handler);
    }

    @SystemApi
    public int getClientPid(String sessionId) {
        return getClientPidInternal(sessionId);
    }

    @SystemApi
    public int getClientPriority(int useCase, String sessionId) {
        Preconditions.checkNotNull(sessionId);
        if (!isValidUseCase(useCase)) {
            throw new IllegalArgumentException("Invalid use case: " + useCase);
        }
        return getClientPriorityInternal(useCase, sessionId);
    }

    @SystemApi
    public int getClientPriority(int useCase) {
        if (!isValidUseCase(useCase)) {
            throw new IllegalArgumentException("Invalid use case: " + useCase);
        }
        return getClientPriorityInternal(useCase, null);
    }

    public void createRecordingSession(String inputId, SessionCallback callback, Handler handler) {
        createSessionInternal(inputId, null, true, callback, handler);
    }

    private void createSessionInternal(String inputId, AttributionSource tvAppAttributionSource, boolean isRecordingSession, SessionCallback callback, Handler handler) {
        Preconditions.checkNotNull(inputId);
        Preconditions.checkNotNull(callback);
        Preconditions.checkNotNull(handler);
        SessionCallbackRecord record = new SessionCallbackRecord(callback, handler);
        synchronized (this.mSessionCallbackRecordMap) {
            int seq = this.mNextSeq;
            this.mNextSeq = seq + 1;
            this.mSessionCallbackRecordMap.put(seq, record);
            try {
                this.mService.createSession(this.mClient, inputId, tvAppAttributionSource, isRecordingSession, seq, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    private int getClientPidInternal(String sessionId) {
        Preconditions.checkNotNull(sessionId);
        try {
            int clientPid = this.mService.getClientPid(sessionId);
            return clientPid;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private int getClientPriorityInternal(int useCase, String sessionId) {
        try {
            return this.mService.getClientPriority(useCase, sessionId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private boolean isValidUseCase(int useCase) {
        return useCase == 100 || useCase == 200 || useCase == 300 || useCase == 400 || useCase == 500;
    }

    @SystemApi
    public List<TvStreamConfig> getAvailableTvStreamConfigList(String inputId) {
        try {
            return this.mService.getAvailableTvStreamConfigList(inputId, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean captureFrame(String inputId, Surface surface, TvStreamConfig config) {
        try {
            return this.mService.captureFrame(inputId, surface, config, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isSingleSessionActive() {
        try {
            return this.mService.isSingleSessionActive(this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<TvInputHardwareInfo> getHardwareList() {
        try {
            return this.mService.getHardwareList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public Hardware acquireTvInputHardware(int deviceId, HardwareCallback callback, TvInputInfo info) {
        return acquireTvInputHardware(deviceId, info, callback);
    }

    @SystemApi
    public Hardware acquireTvInputHardware(int deviceId, TvInputInfo info, HardwareCallback callback) {
        Preconditions.checkNotNull(info);
        Preconditions.checkNotNull(callback);
        return acquireTvInputHardwareInternal(deviceId, info, null, 400, new Executor() { // from class: android.media.tv.TvInputManager.3
            @Override // java.util.concurrent.Executor
            public void execute(Runnable r) {
                r.run();
            }
        }, callback);
    }

    @SystemApi
    public Hardware acquireTvInputHardware(int deviceId, TvInputInfo info, String tvInputSessionId, int priorityHint, Executor executor, HardwareCallback callback) {
        Preconditions.checkNotNull(info);
        Preconditions.checkNotNull(callback);
        return acquireTvInputHardwareInternal(deviceId, info, tvInputSessionId, priorityHint, executor, callback);
    }

    public void addHardwareDevice(int deviceId) {
        try {
            this.mService.addHardwareDevice(deviceId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeHardwareDevice(int deviceId) {
        try {
            this.mService.removeHardwareDevice(deviceId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.media.tv.TvInputManager$4 */
    /* loaded from: classes2.dex */
    public class BinderC19184 extends ITvInputHardwareCallback.Stub {
        final /* synthetic */ HardwareCallback val$callback;
        final /* synthetic */ Executor val$executor;

        BinderC19184(Executor executor, HardwareCallback hardwareCallback) {
            this.val$executor = executor;
            this.val$callback = hardwareCallback;
        }

        @Override // android.media.p007tv.ITvInputHardwareCallback
        public void onReleased() {
            long identity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final HardwareCallback hardwareCallback = this.val$callback;
                executor.execute(new Runnable() { // from class: android.media.tv.TvInputManager$4$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        TvInputManager.HardwareCallback.this.onReleased();
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        @Override // android.media.p007tv.ITvInputHardwareCallback
        public void onStreamConfigChanged(final TvStreamConfig[] configs) {
            long identity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final HardwareCallback hardwareCallback = this.val$callback;
                executor.execute(new Runnable() { // from class: android.media.tv.TvInputManager$4$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        TvInputManager.HardwareCallback.this.onStreamConfigChanged(configs);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    private Hardware acquireTvInputHardwareInternal(int deviceId, TvInputInfo info, String tvInputSessionId, int priorityHint, Executor executor, HardwareCallback callback) {
        try {
            ITvInputHardware hardware = this.mService.acquireTvInputHardware(deviceId, new BinderC19184(executor, callback), info, this.mUserId, tvInputSessionId, priorityHint);
            if (hardware == null) {
                return null;
            }
            return new Hardware(hardware);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void releaseTvInputHardware(int deviceId, Hardware hardware) {
        try {
            this.mService.releaseTvInputHardware(deviceId, hardware.getInterface(), this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<DvbDeviceInfo> getDvbDeviceList() {
        try {
            return this.mService.getDvbDeviceList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public ParcelFileDescriptor openDvbDevice(DvbDeviceInfo info, int deviceType) {
        try {
            if (deviceType < 0 || 2 < deviceType) {
                throw new IllegalArgumentException("Invalid DVB device: " + deviceType);
            }
            return this.mService.openDvbDevice(info, deviceType);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void requestChannelBrowsable(Uri channelUri) {
        try {
            this.mService.requestChannelBrowsable(channelUri, this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<TunedInfo> getCurrentTunedInfos() {
        try {
            return this.mService.getCurrentTunedInfos(this.mUserId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* renamed from: android.media.tv.TvInputManager$Session */
    /* loaded from: classes2.dex */
    public static final class Session {
        static final int DISPATCH_HANDLED = 1;
        static final int DISPATCH_IN_PROGRESS = -1;
        static final int DISPATCH_NOT_HANDLED = 0;
        private static final long INPUT_SESSION_NOT_RESPONDING_TIMEOUT = 2500;
        private final List<AudioPresentation> mAudioPresentations;
        private final List<TvTrackInfo> mAudioTracks;
        private InputChannel mChannel;
        private final InputEventHandler mHandler;
        private boolean mIAppNotificationEnabled;
        private TvInteractiveAppManager.Session mIAppSession;
        private final Object mMetadataLock;
        private final Pools.Pool<PendingEvent> mPendingEventPool;
        private final SparseArray<PendingEvent> mPendingEvents;
        private int mSelectedAudioPresentationId;
        private int mSelectedAudioProgramId;
        private String mSelectedAudioTrackId;
        private String mSelectedSubtitleTrackId;
        private String mSelectedVideoTrackId;
        private TvInputEventSender mSender;
        private final int mSeq;
        private final ITvInputManager mService;
        private final SparseArray<SessionCallbackRecord> mSessionCallbackRecordMap;
        private final List<TvTrackInfo> mSubtitleTracks;
        private IBinder mToken;
        private final int mUserId;
        private int mVideoHeight;
        private final List<TvTrackInfo> mVideoTracks;
        private int mVideoWidth;

        /* renamed from: android.media.tv.TvInputManager$Session$FinishedInputEventCallback */
        /* loaded from: classes2.dex */
        public interface FinishedInputEventCallback {
            void onFinishedInputEvent(Object obj, boolean z);
        }

        private Session(IBinder token, InputChannel channel, ITvInputManager service, int userId, int seq, SparseArray<SessionCallbackRecord> sessionCallbackRecordMap) {
            this.mHandler = new InputEventHandler(Looper.getMainLooper());
            this.mPendingEventPool = new Pools.SimplePool(20);
            this.mPendingEvents = new SparseArray<>(20);
            this.mMetadataLock = new Object();
            this.mAudioPresentations = new ArrayList();
            this.mAudioTracks = new ArrayList();
            this.mVideoTracks = new ArrayList();
            this.mSubtitleTracks = new ArrayList();
            this.mSelectedAudioProgramId = -1;
            this.mSelectedAudioPresentationId = -1;
            this.mIAppNotificationEnabled = false;
            this.mToken = token;
            this.mChannel = channel;
            this.mService = service;
            this.mUserId = userId;
            this.mSeq = seq;
            this.mSessionCallbackRecordMap = sessionCallbackRecordMap;
        }

        public TvInteractiveAppManager.Session getInteractiveAppSession() {
            return this.mIAppSession;
        }

        public void setInteractiveAppSession(TvInteractiveAppManager.Session iAppSession) {
            this.mIAppSession = iAppSession;
        }

        public void release() {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.releaseSession(iBinder, this.mUserId);
                releaseInternal();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void setMain() {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.setMainSession(iBinder, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void setSurface(Surface surface) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.setSurface(iBinder, surface, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void dispatchSurfaceChanged(int format, int width, int height) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.dispatchSurfaceChanged(iBinder, format, width, height, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void setStreamVolume(float volume) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                if (volume < 0.0f || volume > 1.0f) {
                    throw new IllegalArgumentException("volume should be between 0.0f and 1.0f");
                }
                this.mService.setVolume(iBinder, volume, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void tune(Uri channelUri) {
            tune(channelUri, null);
        }

        public void tune(Uri channelUri, Bundle params) {
            Preconditions.checkNotNull(channelUri);
            if (this.mToken == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            synchronized (this.mMetadataLock) {
                this.mAudioPresentations.clear();
                this.mAudioTracks.clear();
                this.mVideoTracks.clear();
                this.mSubtitleTracks.clear();
                this.mSelectedAudioProgramId = -1;
                this.mSelectedAudioPresentationId = -1;
                this.mSelectedAudioTrackId = null;
                this.mSelectedVideoTrackId = null;
                this.mSelectedSubtitleTrackId = null;
                this.mVideoWidth = 0;
                this.mVideoHeight = 0;
            }
            try {
                this.mService.tune(this.mToken, channelUri, params, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void setCaptionEnabled(boolean enabled) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.setCaptionEnabled(iBinder, enabled, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void selectAudioPresentation(int presentationId, int programId) {
            synchronized (this.mMetadataLock) {
                if (presentationId != -1) {
                    if (!containsAudioPresentation(this.mAudioPresentations, presentationId)) {
                        Log.m104w(TvInputManager.TAG, "Invalid audio presentation id: " + presentationId);
                        return;
                    }
                }
                IBinder iBinder = this.mToken;
                if (iBinder == null) {
                    Log.m104w(TvInputManager.TAG, "The session has been already released");
                    return;
                }
                try {
                    this.mService.selectAudioPresentation(iBinder, presentationId, programId, this.mUserId);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }

        private boolean containsAudioPresentation(List<AudioPresentation> audioPresentations, int presentationId) {
            synchronized (this.mMetadataLock) {
                for (AudioPresentation audioPresentation : audioPresentations) {
                    if (audioPresentation.getPresentationId() == presentationId) {
                        return true;
                    }
                }
                return false;
            }
        }

        public List<AudioPresentation> getAudioPresentations() {
            synchronized (this.mMetadataLock) {
                if (this.mAudioPresentations == null) {
                    return new ArrayList();
                }
                return new ArrayList(this.mAudioPresentations);
            }
        }

        public int getSelectedProgramId() {
            int i;
            synchronized (this.mMetadataLock) {
                i = this.mSelectedAudioProgramId;
            }
            return i;
        }

        public int getSelectedAudioPresentationId() {
            int i;
            synchronized (this.mMetadataLock) {
                i = this.mSelectedAudioPresentationId;
            }
            return i;
        }

        boolean updateAudioPresentations(List<AudioPresentation> audioPresentations) {
            boolean z;
            synchronized (this.mMetadataLock) {
                this.mAudioPresentations.clear();
                for (AudioPresentation presentation : audioPresentations) {
                    this.mAudioPresentations.add(presentation);
                }
                z = !this.mAudioPresentations.isEmpty();
            }
            return z;
        }

        boolean updateAudioPresentationSelection(int presentationId, int programId) {
            synchronized (this.mMetadataLock) {
                if (programId == this.mSelectedAudioProgramId && presentationId == this.mSelectedAudioPresentationId) {
                    return false;
                }
                this.mSelectedAudioPresentationId = presentationId;
                this.mSelectedAudioProgramId = programId;
                return true;
            }
        }

        public void selectTrack(int type, String trackId) {
            synchronized (this.mMetadataLock) {
                if (type == 0) {
                    if (trackId != null && !containsTrack(this.mAudioTracks, trackId)) {
                        Log.m104w(TvInputManager.TAG, "Invalid audio trackId: " + trackId);
                        return;
                    }
                } else if (type == 1) {
                    if (trackId != null && !containsTrack(this.mVideoTracks, trackId)) {
                        Log.m104w(TvInputManager.TAG, "Invalid video trackId: " + trackId);
                        return;
                    }
                } else if (type == 2) {
                    if (trackId != null && !containsTrack(this.mSubtitleTracks, trackId)) {
                        Log.m104w(TvInputManager.TAG, "Invalid subtitle trackId: " + trackId);
                        return;
                    }
                } else {
                    throw new IllegalArgumentException("invalid type: " + type);
                }
                IBinder iBinder = this.mToken;
                if (iBinder == null) {
                    Log.m104w(TvInputManager.TAG, "The session has been already released");
                    return;
                }
                try {
                    this.mService.selectTrack(iBinder, type, trackId, this.mUserId);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }

        private boolean containsTrack(List<TvTrackInfo> tracks, String trackId) {
            for (TvTrackInfo track : tracks) {
                if (track.getId().equals(trackId)) {
                    return true;
                }
            }
            return false;
        }

        public List<TvTrackInfo> getTracks(int type) {
            synchronized (this.mMetadataLock) {
                try {
                    if (type == 0) {
                        if (this.mAudioTracks == null) {
                            return null;
                        }
                        return new ArrayList(this.mAudioTracks);
                    } else if (type == 1) {
                        if (this.mVideoTracks == null) {
                            return null;
                        }
                        return new ArrayList(this.mVideoTracks);
                    } else if (type == 2) {
                        if (this.mSubtitleTracks == null) {
                            return null;
                        }
                        return new ArrayList(this.mSubtitleTracks);
                    } else {
                        throw new IllegalArgumentException("invalid type: " + type);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        public String getSelectedTrack(int type) {
            synchronized (this.mMetadataLock) {
                try {
                    if (type == 0) {
                        return this.mSelectedAudioTrackId;
                    } else if (type == 1) {
                        return this.mSelectedVideoTrackId;
                    } else if (type == 2) {
                        return this.mSelectedSubtitleTrackId;
                    } else {
                        throw new IllegalArgumentException("invalid type: " + type);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        public void setInteractiveAppNotificationEnabled(boolean enabled) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.setInteractiveAppNotificationEnabled(iBinder, enabled, this.mUserId);
                this.mIAppNotificationEnabled = enabled;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        boolean updateTracks(List<TvTrackInfo> tracks) {
            boolean z;
            synchronized (this.mMetadataLock) {
                this.mAudioTracks.clear();
                this.mVideoTracks.clear();
                this.mSubtitleTracks.clear();
                Iterator<TvTrackInfo> it = tracks.iterator();
                while (true) {
                    z = true;
                    if (!it.hasNext()) {
                        break;
                    }
                    TvTrackInfo track = it.next();
                    if (track.getType() == 0) {
                        this.mAudioTracks.add(track);
                    } else if (track.getType() == 1) {
                        this.mVideoTracks.add(track);
                    } else if (track.getType() == 2) {
                        this.mSubtitleTracks.add(track);
                    }
                }
                if (this.mAudioTracks.isEmpty() && this.mVideoTracks.isEmpty() && this.mSubtitleTracks.isEmpty()) {
                    z = false;
                }
            }
            return z;
        }

        boolean updateTrackSelection(int type, String trackId) {
            synchronized (this.mMetadataLock) {
                if (type == 0) {
                    try {
                        if (!TextUtils.equals(trackId, this.mSelectedAudioTrackId)) {
                            this.mSelectedAudioTrackId = trackId;
                            return true;
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                if (type == 1 && !TextUtils.equals(trackId, this.mSelectedVideoTrackId)) {
                    this.mSelectedVideoTrackId = trackId;
                    return true;
                } else if (type == 2 && !TextUtils.equals(trackId, this.mSelectedSubtitleTrackId)) {
                    this.mSelectedSubtitleTrackId = trackId;
                    return true;
                } else {
                    return false;
                }
            }
        }

        TvTrackInfo getVideoTrackToNotify() {
            synchronized (this.mMetadataLock) {
                if (!this.mVideoTracks.isEmpty() && this.mSelectedVideoTrackId != null) {
                    for (TvTrackInfo track : this.mVideoTracks) {
                        if (track.getId().equals(this.mSelectedVideoTrackId)) {
                            int videoWidth = track.getVideoWidth();
                            int videoHeight = track.getVideoHeight();
                            if (this.mVideoWidth != videoWidth || this.mVideoHeight != videoHeight) {
                                this.mVideoWidth = videoWidth;
                                this.mVideoHeight = videoHeight;
                                return track;
                            }
                        }
                    }
                }
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void timeShiftPlay(Uri recordedProgramUri) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.timeShiftPlay(iBinder, recordedProgramUri, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void timeShiftPause() {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.timeShiftPause(iBinder, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void timeShiftResume() {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.timeShiftResume(iBinder, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void timeShiftSeekTo(long timeMs) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.timeShiftSeekTo(iBinder, timeMs, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void timeShiftSetPlaybackParams(PlaybackParams params) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.timeShiftSetPlaybackParams(iBinder, params, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void timeShiftSetMode(int mode) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.timeShiftSetMode(iBinder, mode, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void timeShiftEnablePositionTracking(boolean enable) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.timeShiftEnablePositionTracking(iBinder, enable, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void notifyTvMessage(String type, Bundle data) {
            try {
                this.mService.notifyTvMessage(this.mToken, type, data, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        void startRecording(Uri programUri) {
            startRecording(programUri, null);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void startRecording(Uri programUri, Bundle params) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.startRecording(iBinder, programUri, params, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void stopRecording() {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.stopRecording(iBinder, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void pauseRecording(Bundle params) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.pauseRecording(iBinder, params, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void resumeRecording(Bundle params) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.resumeRecording(iBinder, params, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void sendAppPrivateCommand(String action, Bundle data) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.sendAppPrivateCommand(iBinder, action, data, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void createOverlayView(View view, Rect frame) {
            Preconditions.checkNotNull(view);
            Preconditions.checkNotNull(frame);
            if (view.getWindowToken() == null) {
                throw new IllegalStateException("view must be attached to a window");
            }
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.createOverlayView(iBinder, view.getWindowToken(), frame, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void relayoutOverlayView(Rect frame) {
            Preconditions.checkNotNull(frame);
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.relayoutOverlayView(iBinder, frame, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void removeOverlayView() {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.removeOverlayView(iBinder, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void unblockContent(TvContentRating unblockedRating) {
            Preconditions.checkNotNull(unblockedRating);
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.unblockContent(iBinder, unblockedRating.flattenToString(), this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public int dispatchInputEvent(InputEvent event, Object token, FinishedInputEventCallback callback, Handler handler) {
            Preconditions.checkNotNull(event);
            Preconditions.checkNotNull(callback);
            Preconditions.checkNotNull(handler);
            synchronized (this.mHandler) {
                if (this.mChannel == null) {
                    return 0;
                }
                PendingEvent p = obtainPendingEventLocked(event, token, callback, handler);
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    return sendInputEventOnMainLooperLocked(p);
                }
                Message msg = this.mHandler.obtainMessage(1, p);
                msg.setAsynchronous(true);
                this.mHandler.sendMessage(msg);
                return -1;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendInputEventAndReportResultOnMainLooper(PendingEvent p) {
            synchronized (this.mHandler) {
                int result = sendInputEventOnMainLooperLocked(p);
                if (result == -1) {
                    return;
                }
                invokeFinishedInputEventCallback(p, false);
            }
        }

        private int sendInputEventOnMainLooperLocked(PendingEvent p) {
            InputChannel inputChannel = this.mChannel;
            if (inputChannel != null) {
                if (this.mSender == null) {
                    this.mSender = new TvInputEventSender(inputChannel, this.mHandler.getLooper());
                }
                InputEvent event = p.mEvent;
                int seq = event.getSequenceNumber();
                if (this.mSender.sendInputEvent(seq, event)) {
                    this.mPendingEvents.put(seq, p);
                    Message msg = this.mHandler.obtainMessage(2, p);
                    msg.setAsynchronous(true);
                    this.mHandler.sendMessageDelayed(msg, INPUT_SESSION_NOT_RESPONDING_TIMEOUT);
                    return -1;
                }
                Log.m104w(TvInputManager.TAG, "Unable to send input event to session: " + this.mToken + " dropping:" + event);
                return 0;
            }
            return 0;
        }

        void finishedInputEvent(int seq, boolean handled, boolean timeout) {
            synchronized (this.mHandler) {
                int index = this.mPendingEvents.indexOfKey(seq);
                if (index < 0) {
                    return;
                }
                PendingEvent p = this.mPendingEvents.valueAt(index);
                this.mPendingEvents.removeAt(index);
                if (timeout) {
                    Log.m104w(TvInputManager.TAG, "Timeout waiting for session to handle input event after 2500 ms: " + this.mToken);
                } else {
                    this.mHandler.removeMessages(2, p);
                }
                invokeFinishedInputEventCallback(p, handled);
            }
        }

        void invokeFinishedInputEventCallback(PendingEvent p, boolean handled) {
            p.mHandled = handled;
            if (p.mEventHandler.getLooper().isCurrentThread()) {
                p.run();
                return;
            }
            Message msg = Message.obtain(p.mEventHandler, p);
            msg.setAsynchronous(true);
            msg.sendToTarget();
        }

        private void flushPendingEventsLocked() {
            this.mHandler.removeMessages(3);
            int count = this.mPendingEvents.size();
            for (int i = 0; i < count; i++) {
                int seq = this.mPendingEvents.keyAt(i);
                Message msg = this.mHandler.obtainMessage(3, seq, 0);
                msg.setAsynchronous(true);
                msg.sendToTarget();
            }
        }

        private PendingEvent obtainPendingEventLocked(InputEvent event, Object token, FinishedInputEventCallback callback, Handler handler) {
            PendingEvent p = this.mPendingEventPool.acquire();
            if (p == null) {
                p = new PendingEvent();
            }
            p.mEvent = event;
            p.mEventToken = token;
            p.mCallback = callback;
            p.mEventHandler = handler;
            return p;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void recyclePendingEventLocked(PendingEvent p) {
            p.recycle();
            this.mPendingEventPool.release(p);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public IBinder getToken() {
            return this.mToken;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void releaseInternal() {
            this.mToken = null;
            synchronized (this.mHandler) {
                if (this.mChannel != null) {
                    if (this.mSender != null) {
                        flushPendingEventsLocked();
                        this.mSender.dispose();
                        this.mSender = null;
                    }
                    this.mChannel.dispose();
                    this.mChannel = null;
                }
            }
            synchronized (this.mSessionCallbackRecordMap) {
                this.mSessionCallbackRecordMap.delete(this.mSeq);
            }
        }

        public void requestBroadcastInfo(BroadcastInfoRequest request) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.requestBroadcastInfo(iBinder, request, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void removeBroadcastInfo(int requestId) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.removeBroadcastInfo(iBinder, requestId, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void requestAd(AdRequest request) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.requestAd(iBinder, request, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void notifyAdBuffer(AdBuffer buffer) {
            IBinder iBinder = this.mToken;
            if (iBinder == null) {
                Log.m104w(TvInputManager.TAG, "The session has been already released");
                return;
            }
            try {
                this.mService.notifyAdBuffer(iBinder, buffer, this.mUserId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.media.tv.TvInputManager$Session$InputEventHandler */
        /* loaded from: classes2.dex */
        public final class InputEventHandler extends Handler {
            public static final int MSG_FLUSH_INPUT_EVENT = 3;
            public static final int MSG_SEND_INPUT_EVENT = 1;
            public static final int MSG_TIMEOUT_INPUT_EVENT = 2;

            InputEventHandler(Looper looper) {
                super(looper, null, true);
            }

            @Override // android.p008os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Session.this.sendInputEventAndReportResultOnMainLooper((PendingEvent) msg.obj);
                        return;
                    case 2:
                        Session.this.finishedInputEvent(msg.arg1, false, true);
                        return;
                    case 3:
                        Session.this.finishedInputEvent(msg.arg1, false, false);
                        return;
                    default:
                        return;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.media.tv.TvInputManager$Session$TvInputEventSender */
        /* loaded from: classes2.dex */
        public final class TvInputEventSender extends InputEventSender {
            public TvInputEventSender(InputChannel inputChannel, Looper looper) {
                super(inputChannel, looper);
            }

            @Override // android.view.InputEventSender
            public void onInputEventFinished(int seq, boolean handled) {
                Session.this.finishedInputEvent(seq, handled, false);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.media.tv.TvInputManager$Session$PendingEvent */
        /* loaded from: classes2.dex */
        public final class PendingEvent implements Runnable {
            public FinishedInputEventCallback mCallback;
            public InputEvent mEvent;
            public Handler mEventHandler;
            public Object mEventToken;
            public boolean mHandled;

            private PendingEvent() {
            }

            public void recycle() {
                this.mEvent = null;
                this.mEventToken = null;
                this.mCallback = null;
                this.mEventHandler = null;
                this.mHandled = false;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.mCallback.onFinishedInputEvent(this.mEventToken, this.mHandled);
                synchronized (this.mEventHandler) {
                    Session.this.recyclePendingEventLocked(this);
                }
            }
        }
    }

    @SystemApi
    /* renamed from: android.media.tv.TvInputManager$Hardware */
    /* loaded from: classes2.dex */
    public static final class Hardware {
        private final ITvInputHardware mInterface;

        private Hardware(ITvInputHardware hardwareInterface) {
            this.mInterface = hardwareInterface;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ITvInputHardware getInterface() {
            return this.mInterface;
        }

        public boolean setSurface(Surface surface, TvStreamConfig config) {
            try {
                return this.mInterface.setSurface(surface, config);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        public void setStreamVolume(float volume) {
            try {
                this.mInterface.setStreamVolume(volume);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @SystemApi
        public boolean dispatchKeyEventToHdmi(KeyEvent event) {
            return false;
        }

        public void overrideAudioSink(int audioType, String audioAddress, int samplingRate, int channelMask, int format) {
            try {
                this.mInterface.overrideAudioSink(audioType, audioAddress, samplingRate, channelMask, format);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        public void overrideAudioSink(AudioDeviceInfo device, int samplingRate, int channelMask, int format) {
            Objects.requireNonNull(device);
            try {
                this.mInterface.overrideAudioSink(AudioDeviceInfo.convertDeviceTypeToInternalDevice(device.getType()), device.getAddress(), samplingRate, channelMask, format);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
