package android.media.soundtrigger;

import android.annotation.SystemApi;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.AudioFormat;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.ParcelUuid;
import android.p008os.RemoteException;
import android.util.Slog;
import com.android.internal.app.ISoundTriggerSession;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public final class SoundTriggerDetector {
    private static final boolean DBG = false;
    private static final int MSG_AVAILABILITY_CHANGED = 1;
    private static final int MSG_DETECTION_ERROR = 3;
    private static final int MSG_DETECTION_PAUSE = 4;
    private static final int MSG_DETECTION_RESUME = 5;
    private static final int MSG_SOUND_TRIGGER_DETECTED = 2;
    public static final int RECOGNITION_FLAG_ALLOW_MULTIPLE_TRIGGERS = 2;
    public static final int RECOGNITION_FLAG_CAPTURE_TRIGGER_AUDIO = 1;
    public static final int RECOGNITION_FLAG_ENABLE_AUDIO_ECHO_CANCELLATION = 4;
    public static final int RECOGNITION_FLAG_ENABLE_AUDIO_NOISE_SUPPRESSION = 8;
    public static final int RECOGNITION_FLAG_NONE = 0;
    public static final int RECOGNITION_FLAG_RUN_IN_BATTERY_SAVER = 16;
    private static final String TAG = "SoundTriggerDetector";
    private final Callback mCallback;
    private final Handler mHandler;
    private final Object mLock = new Object();
    private final RecognitionCallback mRecognitionCallback;
    private final SoundTrigger.GenericSoundModel mSoundModel;
    private final ISoundTriggerSession mSoundTriggerSession;

    /* loaded from: classes2.dex */
    public static abstract class Callback {
        public abstract void onAvailabilityChanged(int i);

        public abstract void onDetected(EventPayload eventPayload);

        public abstract void onError();

        public abstract void onRecognitionPaused();

        public abstract void onRecognitionResumed();
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface RecognitionFlags {
    }

    /* loaded from: classes2.dex */
    public static class EventPayload {
        private final AudioFormat mAudioFormat;
        private final boolean mCaptureAvailable;
        private final int mCaptureSession;
        private final byte[] mData;
        private final boolean mTriggerAvailable;

        private EventPayload(boolean triggerAvailable, boolean captureAvailable, AudioFormat audioFormat, int captureSession, byte[] data) {
            this.mTriggerAvailable = triggerAvailable;
            this.mCaptureAvailable = captureAvailable;
            this.mCaptureSession = captureSession;
            this.mAudioFormat = audioFormat;
            this.mData = data;
        }

        public AudioFormat getCaptureAudioFormat() {
            return this.mAudioFormat;
        }

        public byte[] getTriggerAudio() {
            if (this.mTriggerAvailable) {
                return this.mData;
            }
            return null;
        }

        public byte[] getData() {
            if (!this.mTriggerAvailable) {
                return this.mData;
            }
            return null;
        }

        public Integer getCaptureSession() {
            if (this.mCaptureAvailable) {
                return Integer.valueOf(this.mCaptureSession);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SoundTriggerDetector(ISoundTriggerSession soundTriggerSession, SoundTrigger.GenericSoundModel soundModel, Callback callback, Handler handler) {
        this.mSoundTriggerSession = soundTriggerSession;
        this.mSoundModel = soundModel;
        this.mCallback = callback;
        if (handler == null) {
            this.mHandler = new MyHandler();
        } else {
            this.mHandler = new MyHandler(handler.getLooper());
        }
        this.mRecognitionCallback = new RecognitionCallback();
    }

    @Deprecated
    public boolean startRecognition(int recognitionFlags) {
        int audioCapabilities;
        boolean captureTriggerAudio = (recognitionFlags & 1) != 0;
        boolean allowMultipleTriggers = (recognitionFlags & 2) != 0;
        boolean runInBatterySaver = (recognitionFlags & 16) != 0;
        int audioCapabilities2 = 0;
        if ((recognitionFlags & 4) != 0) {
            audioCapabilities2 = 0 | 1;
        }
        if ((recognitionFlags & 8) == 0) {
            audioCapabilities = audioCapabilities2;
        } else {
            audioCapabilities = audioCapabilities2 | 2;
        }
        try {
            int status = this.mSoundTriggerSession.startRecognition(this.mSoundModel, this.mRecognitionCallback, new SoundTrigger.RecognitionConfig(captureTriggerAudio, allowMultipleTriggers, null, null, audioCapabilities), runInBatterySaver);
            return status == 0;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Deprecated
    public boolean stopRecognition() {
        try {
            int status = this.mSoundTriggerSession.stopRecognition(new ParcelUuid(this.mSoundModel.getUuid()), this.mRecognitionCallback);
            return status == 0;
        } catch (RemoteException e) {
            return false;
        }
    }

    public void dump(String prefix, PrintWriter pw) {
        synchronized (this.mLock) {
        }
    }

    /* loaded from: classes2.dex */
    private class RecognitionCallback extends IRecognitionStatusCallback.Stub {
        private RecognitionCallback() {
        }

        @Override // android.hardware.soundtrigger.IRecognitionStatusCallback
        public void onGenericSoundTriggerDetected(SoundTrigger.GenericRecognitionEvent event) {
            Slog.m98d(SoundTriggerDetector.TAG, "onGenericSoundTriggerDetected()" + event);
            Message.obtain(SoundTriggerDetector.this.mHandler, 2, new EventPayload(event.triggerInData, event.captureAvailable, event.captureFormat, event.captureSession, event.data)).sendToTarget();
        }

        @Override // android.hardware.soundtrigger.IRecognitionStatusCallback
        public void onKeyphraseDetected(SoundTrigger.KeyphraseRecognitionEvent event) {
            Slog.m96e(SoundTriggerDetector.TAG, "Ignoring onKeyphraseDetected() called for " + event);
        }

        @Override // android.hardware.soundtrigger.IRecognitionStatusCallback
        public void onError(int status) {
            Slog.m98d(SoundTriggerDetector.TAG, "onError()" + status);
            SoundTriggerDetector.this.mHandler.sendEmptyMessage(3);
        }

        @Override // android.hardware.soundtrigger.IRecognitionStatusCallback
        public void onRecognitionPaused() {
            Slog.m98d(SoundTriggerDetector.TAG, "onRecognitionPaused()");
            SoundTriggerDetector.this.mHandler.sendEmptyMessage(4);
        }

        @Override // android.hardware.soundtrigger.IRecognitionStatusCallback
        public void onRecognitionResumed() {
            Slog.m98d(SoundTriggerDetector.TAG, "onRecognitionResumed()");
            SoundTriggerDetector.this.mHandler.sendEmptyMessage(5);
        }
    }

    /* loaded from: classes2.dex */
    private class MyHandler extends Handler {
        MyHandler() {
        }

        MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            if (SoundTriggerDetector.this.mCallback == null) {
                Slog.m90w(SoundTriggerDetector.TAG, "Received message: " + msg.what + " for NULL callback.");
                return;
            }
            switch (msg.what) {
                case 2:
                    SoundTriggerDetector.this.mCallback.onDetected((EventPayload) msg.obj);
                    return;
                case 3:
                    SoundTriggerDetector.this.mCallback.onError();
                    return;
                case 4:
                    SoundTriggerDetector.this.mCallback.onRecognitionPaused();
                    return;
                case 5:
                    SoundTriggerDetector.this.mCallback.onRecognitionResumed();
                    return;
                default:
                    super.handleMessage(msg);
                    return;
            }
        }
    }
}
