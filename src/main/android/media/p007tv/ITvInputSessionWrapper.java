package android.media.p007tv;

import android.content.Context;
import android.graphics.Rect;
import android.media.PlaybackParams;
import android.media.p007tv.ITvInputSession;
import android.media.p007tv.TvInputService;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.util.Log;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.Surface;
import com.android.internal.p028os.HandlerCaller;
import com.android.internal.p028os.SomeArgs;
/* renamed from: android.media.tv.ITvInputSessionWrapper */
/* loaded from: classes2.dex */
public class ITvInputSessionWrapper extends ITvInputSession.Stub implements HandlerCaller.Callback {
    private static final int DO_APP_PRIVATE_COMMAND = 9;
    private static final int DO_CREATE_OVERLAY_VIEW = 10;
    private static final int DO_DISPATCH_SURFACE_CHANGED = 4;
    private static final int DO_NOTIFY_AD_BUFFER = 28;
    private static final int DO_NOTIFY_TV_MESSAGE = 32;
    private static final int DO_PAUSE_RECORDING = 22;
    private static final int DO_RELAYOUT_OVERLAY_VIEW = 11;
    private static final int DO_RELEASE = 1;
    private static final int DO_REMOVE_BROADCAST_INFO = 25;
    private static final int DO_REMOVE_OVERLAY_VIEW = 12;
    private static final int DO_REQUEST_AD = 27;
    private static final int DO_REQUEST_BROADCAST_INFO = 24;
    private static final int DO_RESUME_RECORDING = 23;
    private static final int DO_SELECT_AUDIO_PRESENTATION = 29;
    private static final int DO_SELECT_TRACK = 8;
    private static final int DO_SET_CAPTION_ENABLED = 7;
    private static final int DO_SET_IAPP_NOTIFICATION_ENABLED = 26;
    private static final int DO_SET_MAIN = 2;
    private static final int DO_SET_STREAM_VOLUME = 5;
    private static final int DO_SET_SURFACE = 3;
    private static final int DO_SET_TV_MESSAGE_ENABLED = 31;
    private static final int DO_START_RECORDING = 20;
    private static final int DO_STOP_RECORDING = 21;
    private static final int DO_TIME_SHIFT_ENABLE_POSITION_TRACKING = 19;
    private static final int DO_TIME_SHIFT_PAUSE = 15;
    private static final int DO_TIME_SHIFT_PLAY = 14;
    private static final int DO_TIME_SHIFT_RESUME = 16;
    private static final int DO_TIME_SHIFT_SEEK_TO = 17;
    private static final int DO_TIME_SHIFT_SET_MODE = 30;
    private static final int DO_TIME_SHIFT_SET_PLAYBACK_PARAMS = 18;
    private static final int DO_TUNE = 6;
    private static final int DO_UNBLOCK_CONTENT = 13;
    private static final int EXECUTE_MESSAGE_TIMEOUT_LONG_MILLIS = 5000;
    private static final int EXECUTE_MESSAGE_TIMEOUT_SHORT_MILLIS = 50;
    private static final int EXECUTE_MESSAGE_TUNE_TIMEOUT_MILLIS = 2000;
    private static final String TAG = "TvInputSessionWrapper";
    private final HandlerCaller mCaller;
    private InputChannel mChannel;
    private final boolean mIsRecordingSession = true;
    private TvInputEventReceiver mReceiver;
    private TvInputService.RecordingSession mTvInputRecordingSessionImpl;
    private TvInputService.Session mTvInputSessionImpl;

    public ITvInputSessionWrapper(Context context, TvInputService.Session sessionImpl, InputChannel channel) {
        this.mCaller = new HandlerCaller(context, null, this, true);
        this.mTvInputSessionImpl = sessionImpl;
        this.mChannel = channel;
        if (channel != null) {
            this.mReceiver = new TvInputEventReceiver(channel, context.getMainLooper());
        }
    }

    public ITvInputSessionWrapper(Context context, TvInputService.RecordingSession recordingSessionImpl) {
        this.mCaller = new HandlerCaller(context, null, this, true);
        this.mTvInputRecordingSessionImpl = recordingSessionImpl;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.android.internal.p028os.HandlerCaller.Callback
    public void executeMessage(Message msg) {
        boolean z = this.mIsRecordingSession;
        if (z && this.mTvInputRecordingSessionImpl == null) {
            return;
        }
        if (!z && this.mTvInputSessionImpl == null) {
            return;
        }
        long startTime = System.nanoTime();
        switch (msg.what) {
            case 1:
                if (this.mIsRecordingSession) {
                    this.mTvInputRecordingSessionImpl.release();
                    this.mTvInputRecordingSessionImpl = null;
                    break;
                } else {
                    this.mTvInputSessionImpl.release();
                    this.mTvInputSessionImpl = null;
                    TvInputEventReceiver tvInputEventReceiver = this.mReceiver;
                    if (tvInputEventReceiver != null) {
                        tvInputEventReceiver.dispose();
                        this.mReceiver = null;
                    }
                    InputChannel inputChannel = this.mChannel;
                    if (inputChannel != null) {
                        inputChannel.dispose();
                        this.mChannel = null;
                        break;
                    }
                }
                break;
            case 2:
                this.mTvInputSessionImpl.setMain(((Boolean) msg.obj).booleanValue());
                break;
            case 3:
                this.mTvInputSessionImpl.setSurface((Surface) msg.obj);
                break;
            case 4:
                SomeArgs args = (SomeArgs) msg.obj;
                this.mTvInputSessionImpl.dispatchSurfaceChanged(args.argi1, args.argi2, args.argi3);
                args.recycle();
                break;
            case 5:
                this.mTvInputSessionImpl.setStreamVolume(((Float) msg.obj).floatValue());
                break;
            case 6:
                SomeArgs args2 = (SomeArgs) msg.obj;
                if (this.mIsRecordingSession) {
                    this.mTvInputRecordingSessionImpl.tune((Uri) args2.arg1, (Bundle) args2.arg2);
                } else {
                    this.mTvInputSessionImpl.tune((Uri) args2.arg1, (Bundle) args2.arg2);
                }
                args2.recycle();
                break;
            case 7:
                this.mTvInputSessionImpl.setCaptionEnabled(((Boolean) msg.obj).booleanValue());
                break;
            case 8:
                SomeArgs args3 = (SomeArgs) msg.obj;
                this.mTvInputSessionImpl.selectTrack(((Integer) args3.arg1).intValue(), (String) args3.arg2);
                args3.recycle();
                break;
            case 9:
                SomeArgs args4 = (SomeArgs) msg.obj;
                if (this.mIsRecordingSession) {
                    this.mTvInputRecordingSessionImpl.appPrivateCommand((String) args4.arg1, (Bundle) args4.arg2);
                } else {
                    this.mTvInputSessionImpl.appPrivateCommand((String) args4.arg1, (Bundle) args4.arg2);
                }
                args4.recycle();
                break;
            case 10:
                SomeArgs args5 = (SomeArgs) msg.obj;
                this.mTvInputSessionImpl.createOverlayView((IBinder) args5.arg1, (Rect) args5.arg2);
                args5.recycle();
                break;
            case 11:
                this.mTvInputSessionImpl.relayoutOverlayView((Rect) msg.obj);
                break;
            case 12:
                this.mTvInputSessionImpl.removeOverlayView(true);
                break;
            case 13:
                this.mTvInputSessionImpl.unblockContent((String) msg.obj);
                break;
            case 14:
                this.mTvInputSessionImpl.timeShiftPlay((Uri) msg.obj);
                break;
            case 15:
                this.mTvInputSessionImpl.timeShiftPause();
                break;
            case 16:
                this.mTvInputSessionImpl.timeShiftResume();
                break;
            case 17:
                this.mTvInputSessionImpl.timeShiftSeekTo(((Long) msg.obj).longValue());
                break;
            case 18:
                this.mTvInputSessionImpl.timeShiftSetPlaybackParams((PlaybackParams) msg.obj);
                break;
            case 19:
                this.mTvInputSessionImpl.timeShiftEnablePositionTracking(((Boolean) msg.obj).booleanValue());
                break;
            case 20:
                SomeArgs args6 = (SomeArgs) msg.obj;
                this.mTvInputRecordingSessionImpl.startRecording((Uri) args6.arg1, (Bundle) args6.arg2);
                args6.recycle();
                break;
            case 21:
                this.mTvInputRecordingSessionImpl.stopRecording();
                break;
            case 22:
                this.mTvInputRecordingSessionImpl.pauseRecording((Bundle) msg.obj);
                break;
            case 23:
                this.mTvInputRecordingSessionImpl.resumeRecording((Bundle) msg.obj);
                break;
            case 24:
                this.mTvInputSessionImpl.requestBroadcastInfo((BroadcastInfoRequest) msg.obj);
                break;
            case 25:
                this.mTvInputSessionImpl.removeBroadcastInfo(msg.arg1);
                break;
            case 26:
                this.mTvInputSessionImpl.setInteractiveAppNotificationEnabled(((Boolean) msg.obj).booleanValue());
                break;
            case 27:
                this.mTvInputSessionImpl.requestAd((AdRequest) msg.obj);
                break;
            case 28:
                this.mTvInputSessionImpl.notifyAdBuffer((AdBuffer) msg.obj);
                break;
            case 29:
                SomeArgs args7 = (SomeArgs) msg.obj;
                this.mTvInputSessionImpl.selectAudioPresentation(args7.argi1, args7.argi2);
                args7.recycle();
                break;
            case 30:
                this.mTvInputSessionImpl.timeShiftSetMode(((Integer) msg.obj).intValue());
                this.mTvInputSessionImpl.timeShiftEnablePositionTracking(((Boolean) msg.obj).booleanValue());
                break;
            case 31:
                SomeArgs args8 = (SomeArgs) msg.obj;
                this.mTvInputSessionImpl.setTvMessageEnabled((String) args8.arg1, ((Boolean) args8.arg2).booleanValue());
                break;
            case 32:
                SomeArgs args9 = (SomeArgs) msg.obj;
                this.mTvInputSessionImpl.onTvMessageReceived((String) args9.arg1, (Bundle) args9.arg2);
                break;
            default:
                Log.m104w(TAG, "Unhandled message code: " + msg.what);
                break;
        }
        long durationMs = (System.nanoTime() - startTime) / 1000000;
        if (durationMs > 50) {
            Log.m104w(TAG, "Handling message (" + msg.what + ") took too long time (duration=" + durationMs + "ms)");
            if (msg.what == 6 && durationMs > 2000) {
                throw new RuntimeException("Too much time to handle tune request. (" + durationMs + "ms > 2000ms) Consider handling the tune request in a separate thread.");
            }
            if (durationMs > 5000) {
                throw new RuntimeException("Too much time to handle a request. (type=" + msg.what + ", " + durationMs + "ms > 5000ms).");
            }
        }
    }

    @Override // android.media.p007tv.ITvInputSession
    public void release() {
        if (!this.mIsRecordingSession) {
            this.mTvInputSessionImpl.scheduleOverlayViewCleanup();
        }
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessage(1));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void setMain(boolean isMain) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(2, Boolean.valueOf(isMain)));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void setSurface(Surface surface) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(3, surface));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void dispatchSurfaceChanged(int format, int width, int height) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageIIII(4, format, width, height, 0));
    }

    @Override // android.media.p007tv.ITvInputSession
    public final void setVolume(float volume) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(5, Float.valueOf(volume)));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void tune(Uri channelUri, Bundle params) {
        this.mCaller.removeMessages(6);
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageOO(6, channelUri, params));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void setCaptionEnabled(boolean enabled) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(7, Boolean.valueOf(enabled)));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void selectAudioPresentation(int presentationId, int programId) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageII(29, presentationId, programId));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void selectTrack(int type, String trackId) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageOO(8, Integer.valueOf(type), trackId));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void setInteractiveAppNotificationEnabled(boolean enabled) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(26, Boolean.valueOf(enabled)));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void appPrivateCommand(String action, Bundle data) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageOO(9, action, data));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void createOverlayView(IBinder windowToken, Rect frame) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageOO(10, windowToken, frame));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void relayoutOverlayView(Rect frame) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(11, frame));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void removeOverlayView() {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessage(12));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void unblockContent(String unblockedRating) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(13, unblockedRating));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void timeShiftPlay(Uri recordedProgramUri) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(14, recordedProgramUri));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void timeShiftPause() {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessage(15));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void timeShiftResume() {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessage(16));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void timeShiftSeekTo(long timeMs) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(17, Long.valueOf(timeMs)));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void timeShiftSetPlaybackParams(PlaybackParams params) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(18, params));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void timeShiftSetMode(int mode) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageI(30, mode));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void timeShiftEnablePositionTracking(boolean enable) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(19, Boolean.valueOf(enable)));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void startRecording(Uri programUri, Bundle params) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageOO(20, programUri, params));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void stopRecording() {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessage(21));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void pauseRecording(Bundle params) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(22, params));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void resumeRecording(Bundle params) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(23, params));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void requestBroadcastInfo(BroadcastInfoRequest request) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(24, request));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void removeBroadcastInfo(int requestId) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageI(25, requestId));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void requestAd(AdRequest request) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(27, request));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void notifyAdBuffer(AdBuffer buffer) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(28, buffer));
    }

    @Override // android.media.p007tv.ITvInputSession
    public void notifyTvMessage(String type, Bundle data) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageOO(32, type, data));
    }

    /* renamed from: android.media.tv.ITvInputSessionWrapper$TvInputEventReceiver */
    /* loaded from: classes2.dex */
    private final class TvInputEventReceiver extends InputEventReceiver {
        TvInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        @Override // android.view.InputEventReceiver
        public void onInputEvent(InputEvent event) {
            if (ITvInputSessionWrapper.this.mTvInputSessionImpl == null) {
                finishInputEvent(event, false);
                return;
            }
            int handled = ITvInputSessionWrapper.this.mTvInputSessionImpl.dispatchInputEvent(event, this);
            if (handled != -1) {
                finishInputEvent(event, handled == 1);
            }
        }
    }
}
