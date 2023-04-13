package android.service.voice;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.compat.CompatChanges;
import android.content.Intent;
import android.hardware.soundtrigger.KeyphraseEnrollmentInfo;
import android.hardware.soundtrigger.KeyphraseMetadata;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.AudioFormat;
import android.media.permission.Identity;
import android.p008os.AsyncTask;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.SharedMemory;
import android.service.voice.AlwaysOnHotwordDetector;
import android.service.voice.HotwordDetector;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.app.IVoiceInteractionSoundTriggerSession;
import com.android.internal.util.FunctionalUtils;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes3.dex */
public class AlwaysOnHotwordDetector extends AbstractDetector {
    public static final int AUDIO_CAPABILITY_ECHO_CANCELLATION = 1;
    public static final int AUDIO_CAPABILITY_NOISE_SUPPRESSION = 2;
    static final boolean DBG = false;
    public static final int MODEL_PARAM_THRESHOLD_FACTOR = 0;
    private static final int MSG_AVAILABILITY_CHANGED = 1;
    private static final int MSG_DETECTION_ERROR = 3;
    private static final int MSG_DETECTION_PAUSE = 4;
    private static final int MSG_DETECTION_RESUME = 5;
    private static final int MSG_HOTWORD_DETECTED = 2;
    private static final int MSG_HOTWORD_REJECTED = 6;
    private static final int MSG_HOTWORD_STATUS_REPORTED = 7;
    private static final int MSG_PROCESS_RESTARTED = 8;
    public static final int RECOGNITION_FLAG_ALLOW_MULTIPLE_TRIGGERS = 2;
    public static final int RECOGNITION_FLAG_CAPTURE_TRIGGER_AUDIO = 1;
    public static final int RECOGNITION_FLAG_ENABLE_AUDIO_ECHO_CANCELLATION = 4;
    public static final int RECOGNITION_FLAG_ENABLE_AUDIO_NOISE_SUPPRESSION = 8;
    public static final int RECOGNITION_FLAG_NONE = 0;
    public static final int RECOGNITION_FLAG_RUN_IN_BATTERY_SAVER = 16;
    public static final int RECOGNITION_MODE_USER_IDENTIFICATION = 2;
    public static final int RECOGNITION_MODE_VOICE_TRIGGER = 1;
    public static final int STATE_ERROR = 3;
    public static final int STATE_HARDWARE_UNAVAILABLE = -2;
    private static final int STATE_INVALID = -3;
    public static final int STATE_KEYPHRASE_ENROLLED = 2;
    public static final int STATE_KEYPHRASE_UNENROLLED = 1;
    @Deprecated
    public static final int STATE_KEYPHRASE_UNSUPPORTED = -1;
    private static final int STATE_NOT_READY = 0;
    private static final int STATUS_ERROR = Integer.MIN_VALUE;
    private static final int STATUS_OK = 0;
    static final String TAG = "AlwaysOnHotwordDetector";
    private int mAvailability;
    private final IBinder mBinder;
    private final Callback mExternalCallback;
    private final Executor mExternalExecutor;
    private final Handler mHandler;
    private final SoundTriggerListener mInternalCallback;
    private boolean mIsAvailabilityOverriddenByTestApi;
    private final KeyphraseEnrollmentInfo mKeyphraseEnrollmentInfo;
    private KeyphraseMetadata mKeyphraseMetadata;
    private final Locale mLocale;
    private final IVoiceInteractionManagerService mModelManagementService;
    private IVoiceInteractionSoundTriggerSession mSoundTriggerSession;
    private final boolean mSupportSandboxedDetectionService;
    private final int mTargetSdkVersion;
    private final String mText;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface AudioCapabilities {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ModelParams {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RecognitionFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RecognitionModes {
    }

    @Override // android.service.voice.AbstractDetector, android.service.voice.HotwordDetector
    public /* bridge */ /* synthetic */ boolean startRecognition(ParcelFileDescriptor parcelFileDescriptor, AudioFormat audioFormat, PersistableBundle persistableBundle) throws HotwordDetector.IllegalDetectorStateException {
        return super.startRecognition(parcelFileDescriptor, audioFormat, persistableBundle);
    }

    /* loaded from: classes3.dex */
    public static final class ModelParamRange {
        private final SoundTrigger.ModelParamRange mModelParamRange;

        ModelParamRange(SoundTrigger.ModelParamRange modelParamRange) {
            this.mModelParamRange = modelParamRange;
        }

        public int getStart() {
            return this.mModelParamRange.getStart();
        }

        public int getEnd() {
            return this.mModelParamRange.getEnd();
        }

        public String toString() {
            return this.mModelParamRange.toString();
        }

        public boolean equals(Object obj) {
            return this.mModelParamRange.equals(obj);
        }

        public int hashCode() {
            return this.mModelParamRange.hashCode();
        }
    }

    /* loaded from: classes3.dex */
    public static class EventPayload {
        public static final int DATA_FORMAT_RAW = 0;
        public static final int DATA_FORMAT_TRIGGER_AUDIO = 1;
        private final AudioFormat mAudioFormat;
        private final ParcelFileDescriptor mAudioStream;
        private final boolean mCaptureAvailable;
        private final int mCaptureSession;
        private final byte[] mData;
        private final int mDataFormat;
        private final HotwordDetectedResult mHotwordDetectedResult;
        private final List<SoundTrigger.KeyphraseRecognitionExtra> mKephraseExtras;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface DataFormat {
        }

        private EventPayload(boolean captureAvailable, AudioFormat audioFormat, int captureSession, int dataFormat, byte[] data, HotwordDetectedResult hotwordDetectedResult, ParcelFileDescriptor audioStream, List<SoundTrigger.KeyphraseRecognitionExtra> keyphraseExtras) {
            this.mCaptureAvailable = captureAvailable;
            this.mCaptureSession = captureSession;
            this.mAudioFormat = audioFormat;
            this.mDataFormat = dataFormat;
            this.mData = data;
            this.mHotwordDetectedResult = hotwordDetectedResult;
            this.mAudioStream = audioStream;
            this.mKephraseExtras = keyphraseExtras;
        }

        public AudioFormat getCaptureAudioFormat() {
            return this.mAudioFormat;
        }

        @Deprecated
        public byte[] getTriggerAudio() {
            if (this.mDataFormat == 1) {
                return this.mData;
            }
            return null;
        }

        public int getDataFormat() {
            return this.mDataFormat;
        }

        public byte[] getData() {
            return this.mData;
        }

        public Integer getCaptureSession() {
            if (this.mCaptureAvailable) {
                return Integer.valueOf(this.mCaptureSession);
            }
            return null;
        }

        public HotwordDetectedResult getHotwordDetectedResult() {
            return this.mHotwordDetectedResult;
        }

        public ParcelFileDescriptor getAudioStream() {
            return this.mAudioStream;
        }

        public List<SoundTrigger.KeyphraseRecognitionExtra> getKeyphraseRecognitionExtras() {
            return this.mKephraseExtras;
        }

        /* loaded from: classes3.dex */
        public static final class Builder {
            private boolean mCaptureAvailable = false;
            private int mCaptureSession = -1;
            private AudioFormat mAudioFormat = null;
            private int mDataFormat = 0;
            private byte[] mData = null;
            private HotwordDetectedResult mHotwordDetectedResult = null;
            private ParcelFileDescriptor mAudioStream = null;
            private List<SoundTrigger.KeyphraseRecognitionExtra> mKeyphraseExtras = Collections.emptyList();

            public Builder() {
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public Builder(SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent) {
                setCaptureAvailable(keyphraseRecognitionEvent.isCaptureAvailable());
                setCaptureSession(keyphraseRecognitionEvent.getCaptureSession());
                if (keyphraseRecognitionEvent.getCaptureFormat() != null) {
                    setCaptureAudioFormat(keyphraseRecognitionEvent.getCaptureFormat());
                }
                setDataFormat(keyphraseRecognitionEvent.triggerInData ? 1 : 0);
                if (keyphraseRecognitionEvent.getData() != null) {
                    setData(keyphraseRecognitionEvent.getData());
                }
                if (keyphraseRecognitionEvent.keyphraseExtras != null) {
                    setKeyphraseRecognitionExtras(Arrays.asList(keyphraseRecognitionEvent.keyphraseExtras));
                }
            }

            public Builder setCaptureAvailable(boolean captureAvailable) {
                this.mCaptureAvailable = captureAvailable;
                return this;
            }

            public Builder setCaptureSession(int captureSession) {
                this.mCaptureSession = captureSession;
                return this;
            }

            public Builder setCaptureAudioFormat(AudioFormat audioFormat) {
                this.mAudioFormat = audioFormat;
                return this;
            }

            public Builder setDataFormat(int dataFormat) {
                this.mDataFormat = dataFormat;
                return this;
            }

            public Builder setData(byte[] data) {
                this.mData = data;
                return this;
            }

            public Builder setHotwordDetectedResult(HotwordDetectedResult hotwordDetectedResult) {
                this.mHotwordDetectedResult = hotwordDetectedResult;
                return this;
            }

            public Builder setAudioStream(ParcelFileDescriptor audioStream) {
                this.mAudioStream = audioStream;
                return this;
            }

            public Builder setKeyphraseRecognitionExtras(List<SoundTrigger.KeyphraseRecognitionExtra> keyphraseRecognitionExtras) {
                this.mKeyphraseExtras = keyphraseRecognitionExtras;
                return this;
            }

            public EventPayload build() {
                return new EventPayload(this.mCaptureAvailable, this.mAudioFormat, this.mCaptureSession, this.mDataFormat, this.mData, this.mHotwordDetectedResult, this.mAudioStream, this.mKeyphraseExtras);
            }
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Callback implements HotwordDetector.Callback {
        public abstract void onAvailabilityChanged(int i);

        @Override // android.service.voice.HotwordDetector.Callback
        public abstract void onDetected(EventPayload eventPayload);

        @Override // android.service.voice.HotwordDetector.Callback
        @Deprecated
        public abstract void onError();

        @Override // android.service.voice.HotwordDetector.Callback
        public abstract void onRecognitionPaused();

        @Override // android.service.voice.HotwordDetector.Callback
        public abstract void onRecognitionResumed();

        @Override // android.service.voice.HotwordDetector.Callback
        public void onRejected(HotwordRejectedResult result) {
        }

        @Override // android.service.voice.HotwordDetector.Callback
        public void onHotwordDetectionServiceInitialized(int status) {
        }

        @Override // android.service.voice.HotwordDetector.Callback
        public void onHotwordDetectionServiceRestarted() {
        }
    }

    public AlwaysOnHotwordDetector(String text, Locale locale, Executor executor, Callback callback, KeyphraseEnrollmentInfo keyphraseEnrollmentInfo, IVoiceInteractionManagerService modelManagementService, int targetSdkVersion, boolean supportSandboxedDetectionService) {
        super(modelManagementService, executor, callback);
        this.mBinder = new Binder();
        this.mIsAvailabilityOverriddenByTestApi = false;
        this.mAvailability = 0;
        MyHandler myHandler = new MyHandler(Looper.getMainLooper());
        this.mHandler = myHandler;
        this.mText = text;
        this.mLocale = locale;
        this.mKeyphraseEnrollmentInfo = keyphraseEnrollmentInfo;
        this.mExternalCallback = callback;
        this.mExternalExecutor = executor != null ? executor : new HandlerExecutor(new Handler(Looper.myLooper()));
        this.mInternalCallback = new SoundTriggerListener(myHandler);
        this.mModelManagementService = modelManagementService;
        this.mTargetSdkVersion = targetSdkVersion;
        this.mSupportSandboxedDetectionService = supportSandboxedDetectionService;
    }

    @Override // android.service.voice.AbstractDetector
    void initialize(PersistableBundle options, SharedMemory sharedMemory) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initialize(PersistableBundle options, SharedMemory sharedMemory, SoundTrigger.ModuleProperties moduleProperties) {
        if (this.mSupportSandboxedDetectionService) {
            initAndVerifyDetector(options, sharedMemory, this.mInternalCallback, 1);
        }
        try {
            Identity identity = new Identity();
            identity.packageName = ActivityThread.currentOpPackageName();
            if (moduleProperties == null) {
                List<SoundTrigger.ModuleProperties> modulePropList = this.mModelManagementService.listModuleProperties(identity);
                if (modulePropList.size() > 0) {
                    moduleProperties = modulePropList.get(0);
                }
            }
            this.mSoundTriggerSession = this.mModelManagementService.createSoundTriggerSessionAsOriginator(identity, this.mBinder, moduleProperties);
            new RefreshAvailabilityTask().execute(new Void[0]);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // android.service.voice.AbstractDetector, android.service.voice.HotwordDetector
    public final void updateState(PersistableBundle options, SharedMemory sharedMemory) throws HotwordDetector.IllegalDetectorStateException {
        synchronized (this.mLock) {
            if (!this.mSupportSandboxedDetectionService) {
                throw new IllegalStateException("updateState called, but it doesn't support hotword detection service");
            }
            int i = this.mAvailability;
            if (i != -3 && i != 3) {
            }
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("updateState called on an invalid detector or error state");
            }
            throw new IllegalStateException("updateState called on an invalid detector or error state");
        }
        super.updateState(options, sharedMemory);
    }

    public void overrideAvailability(int availability) {
        synchronized (this.mLock) {
            if (this.mKeyphraseMetadata == null && availability == 2) {
                Set<Locale> fakeSupportedLocales = new HashSet<>();
                fakeSupportedLocales.add(this.mLocale);
                this.mKeyphraseMetadata = new KeyphraseMetadata(1, this.mText, fakeSupportedLocales, 1);
            }
            this.mAvailability = availability;
            this.mIsAvailabilityOverriddenByTestApi = true;
            notifyStateChangedLocked();
        }
    }

    public void resetAvailability() {
        synchronized (this.mLock) {
            this.mIsAvailabilityOverriddenByTestApi = false;
        }
        new RefreshAvailabilityTask().execute(new Void[0]);
    }

    public void triggerHardwareRecognitionEventForTest(int status, int soundModelHandle, boolean captureAvailable, int captureSession, int captureDelayMs, int capturePreambleMs, boolean triggerInData, AudioFormat captureFormat, byte[] data, List<SoundTrigger.KeyphraseRecognitionExtra> keyphraseRecognitionExtras) {
        Log.m112d(TAG, "triggerHardwareRecognitionEventForTest()");
        synchronized (this.mLock) {
            try {
                try {
                    int i = this.mAvailability;
                    if (i == -3 || i == 3) {
                        throw new IllegalStateException("triggerHardwareRecognitionEventForTest called on an invalid detector or error state");
                    }
                    try {
                        try {
                            this.mModelManagementService.triggerHardwareRecognitionEventForTest(new SoundTrigger.KeyphraseRecognitionEvent(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, data, (SoundTrigger.KeyphraseRecognitionExtra[]) keyphraseRecognitionExtras.toArray(new SoundTrigger.KeyphraseRecognitionExtra[0])), this.mInternalCallback);
                        } catch (RemoteException e) {
                            e = e;
                            throw e.rethrowFromSystemServer();
                        }
                    } catch (RemoteException e2) {
                        e = e2;
                    }
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    public int getSupportedRecognitionModes() throws HotwordDetector.IllegalDetectorStateException {
        int supportedRecognitionModesLocked;
        synchronized (this.mLock) {
            supportedRecognitionModesLocked = getSupportedRecognitionModesLocked();
        }
        return supportedRecognitionModesLocked;
    }

    private int getSupportedRecognitionModesLocked() throws HotwordDetector.IllegalDetectorStateException {
        KeyphraseMetadata keyphraseMetadata;
        int i = this.mAvailability;
        if (i == -3 || i == 3) {
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("getSupportedRecognitionModes called on an invalid detector or error state");
            }
            throw new IllegalStateException("getSupportedRecognitionModes called on an invalid detector or error state");
        } else if (i != 2 || (keyphraseMetadata = this.mKeyphraseMetadata) == null) {
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("Getting supported recognition modes for the keyphrase is not supported");
            }
            throw new UnsupportedOperationException("Getting supported recognition modes for the keyphrase is not supported");
        } else {
            return keyphraseMetadata.getRecognitionModeFlags();
        }
    }

    public int getSupportedAudioCapabilities() {
        int supportedAudioCapabilitiesLocked;
        synchronized (this.mLock) {
            supportedAudioCapabilitiesLocked = getSupportedAudioCapabilitiesLocked();
        }
        return supportedAudioCapabilitiesLocked;
    }

    private int getSupportedAudioCapabilitiesLocked() {
        try {
            SoundTrigger.ModuleProperties properties = this.mSoundTriggerSession.getDspModuleProperties();
            if (properties != null) {
                return properties.getAudioCapabilities();
            }
            return 0;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean startRecognition(int recognitionFlags, byte[] data) throws HotwordDetector.IllegalDetectorStateException {
        boolean z;
        synchronized (this.mLock) {
            z = startRecognitionLocked(recognitionFlags, data) == 0;
        }
        return z;
    }

    public boolean startRecognition(int recognitionFlags) throws HotwordDetector.IllegalDetectorStateException {
        boolean z;
        synchronized (this.mLock) {
            z = startRecognitionLocked(recognitionFlags, null) == 0;
        }
        return z;
    }

    @Override // android.service.voice.HotwordDetector
    public boolean startRecognition() throws HotwordDetector.IllegalDetectorStateException {
        return startRecognition(0);
    }

    @Override // android.service.voice.HotwordDetector
    public boolean stopRecognition() throws HotwordDetector.IllegalDetectorStateException {
        boolean z;
        synchronized (this.mLock) {
            int i = this.mAvailability;
            if (i != -3 && i != 3) {
                if (i != 2) {
                    if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                        throw new HotwordDetector.IllegalDetectorStateException("Recognition for the given keyphrase is not supported");
                    }
                    throw new UnsupportedOperationException("Recognition for the given keyphrase is not supported");
                }
                z = stopRecognitionLocked() == 0;
            }
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("stopRecognition called on an invalid detector or error state");
            }
            throw new IllegalStateException("stopRecognition called on an invalid detector or error state");
        }
        return z;
    }

    public int setParameter(int modelParam, int value) throws HotwordDetector.IllegalDetectorStateException {
        int parameterLocked;
        synchronized (this.mLock) {
            int i = this.mAvailability;
            if (i != -3 && i != 3) {
                parameterLocked = setParameterLocked(modelParam, value);
            }
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("setParameter called on an invalid detector or error state");
            }
            throw new IllegalStateException("setParameter called on an invalid detector or error state");
        }
        return parameterLocked;
    }

    public int getParameter(int modelParam) throws HotwordDetector.IllegalDetectorStateException {
        int parameterLocked;
        synchronized (this.mLock) {
            int i = this.mAvailability;
            if (i != -3 && i != 3) {
                parameterLocked = getParameterLocked(modelParam);
            }
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("getParameter called on an invalid detector or error state");
            }
            throw new IllegalStateException("getParameter called on an invalid detector or error state");
        }
        return parameterLocked;
    }

    public ModelParamRange queryParameter(int modelParam) throws HotwordDetector.IllegalDetectorStateException {
        ModelParamRange queryParameterLocked;
        synchronized (this.mLock) {
            int i = this.mAvailability;
            if (i != -3 && i != 3) {
                queryParameterLocked = queryParameterLocked(modelParam);
            }
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("queryParameter called on an invalid detector or error state");
            }
            throw new IllegalStateException("queryParameter called on an invalid detector or error state");
        }
        return queryParameterLocked;
    }

    public Intent createEnrollIntent() throws HotwordDetector.IllegalDetectorStateException {
        Intent manageIntentLocked;
        synchronized (this.mLock) {
            manageIntentLocked = getManageIntentLocked(0);
        }
        return manageIntentLocked;
    }

    public Intent createUnEnrollIntent() throws HotwordDetector.IllegalDetectorStateException {
        Intent manageIntentLocked;
        synchronized (this.mLock) {
            manageIntentLocked = getManageIntentLocked(2);
        }
        return manageIntentLocked;
    }

    public Intent createReEnrollIntent() throws HotwordDetector.IllegalDetectorStateException {
        Intent manageIntentLocked;
        synchronized (this.mLock) {
            manageIntentLocked = getManageIntentLocked(1);
        }
        return manageIntentLocked;
    }

    private Intent getManageIntentLocked(int action) throws HotwordDetector.IllegalDetectorStateException {
        int i = this.mAvailability;
        if (i == -3 || i == 3) {
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("getManageIntent called on an invalid detector or error state");
            }
            throw new IllegalStateException("getManageIntent called on an invalid detector or error state");
        } else if (i != 2 && i != 1) {
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("Managing the given keyphrase is not supported");
            }
            throw new UnsupportedOperationException("Managing the given keyphrase is not supported");
        } else {
            return this.mKeyphraseEnrollmentInfo.getManageKeyphraseIntent(action, this.mText, this.mLocale);
        }
    }

    @Override // android.service.voice.AbstractDetector, android.service.voice.HotwordDetector
    public void destroy() {
        synchronized (this.mLock) {
            if (this.mAvailability == 2) {
                try {
                    stopRecognition();
                } catch (Exception e) {
                    Log.m107i(TAG, "failed to stopRecognition in destroy", e);
                }
            }
            this.mAvailability = -3;
            this.mIsAvailabilityOverriddenByTestApi = false;
            notifyStateChangedLocked();
        }
        super.destroy();
    }

    @Override // android.service.voice.HotwordDetector
    public boolean isUsingSandboxedDetectionService() {
        return this.mSupportSandboxedDetectionService;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onSoundModelsChanged() {
        synchronized (this.mLock) {
            int i = this.mAvailability;
            if (i != -3 && i != -2 && i != 3) {
                if (this.mIsAvailabilityOverriddenByTestApi) {
                    Slog.m90w(TAG, "Suppressing system availability update. Availability is overridden by test API.");
                    return;
                }
                if (i == 2) {
                    try {
                        stopRecognitionLocked();
                    } catch (SecurityException e) {
                        Slog.m89w(TAG, "Failed to Stop the recognition", e);
                        if (this.mTargetSdkVersion <= 30) {
                            throw e;
                        }
                        updateAndNotifyStateChangedLocked(3);
                        return;
                    }
                }
                new RefreshAvailabilityTask().execute(new Void[0]);
                return;
            }
            Slog.m90w(TAG, "Received onSoundModelsChanged for an unsupported keyphrase/config or in the error state");
        }
    }

    private int startRecognitionLocked(int recognitionFlags, byte[] data) throws HotwordDetector.IllegalDetectorStateException {
        int audioCapabilities;
        int i = this.mAvailability;
        if (i == -3 || i == 3) {
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("startRecognition called on an invalid detector or error state");
            }
            throw new IllegalStateException("startRecognition called on an invalid detector or error state");
        } else if (i != 2) {
            if (CompatChanges.isChangeEnabled(HotwordDetector.HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION)) {
                throw new HotwordDetector.IllegalDetectorStateException("Recognition for the given keyphrase is not supported");
            }
            throw new UnsupportedOperationException("Recognition for the given keyphrase is not supported");
        } else {
            SoundTrigger.KeyphraseRecognitionExtra[] recognitionExtra = {new SoundTrigger.KeyphraseRecognitionExtra(this.mKeyphraseMetadata.getId(), this.mKeyphraseMetadata.getRecognitionModeFlags(), 0, new SoundTrigger.ConfidenceLevel[0])};
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
                int code = this.mSoundTriggerSession.startRecognition(this.mKeyphraseMetadata.getId(), this.mLocale.toLanguageTag(), this.mInternalCallback, new SoundTrigger.RecognitionConfig(captureTriggerAudio, allowMultipleTriggers, recognitionExtra, data, audioCapabilities), runInBatterySaver);
                if (code != 0) {
                    Slog.m90w(TAG, "startRecognition() failed with error code " + code);
                }
                return code;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    private int stopRecognitionLocked() {
        try {
            int code = this.mSoundTriggerSession.stopRecognition(this.mKeyphraseMetadata.getId(), this.mInternalCallback);
            if (code != 0) {
                Slog.m90w(TAG, "stopRecognition() failed with error code " + code);
            }
            return code;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private int setParameterLocked(int modelParam, int value) {
        try {
            int code = this.mSoundTriggerSession.setParameter(this.mKeyphraseMetadata.getId(), modelParam, value);
            if (code != 0) {
                Slog.m90w(TAG, "setParameter failed with error code " + code);
            }
            return code;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private int getParameterLocked(int modelParam) {
        try {
            return this.mSoundTriggerSession.getParameter(this.mKeyphraseMetadata.getId(), modelParam);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private ModelParamRange queryParameterLocked(int modelParam) {
        try {
            SoundTrigger.ModelParamRange modelParamRange = this.mSoundTriggerSession.queryParameter(this.mKeyphraseMetadata.getId(), modelParam);
            if (modelParamRange == null) {
                return null;
            }
            return new ModelParamRange(modelParamRange);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAndNotifyStateChangedLocked(int availability) {
        if (!this.mIsAvailabilityOverriddenByTestApi) {
            this.mAvailability = availability;
        }
        notifyStateChangedLocked();
    }

    private void notifyStateChangedLocked() {
        Message message = Message.obtain(this.mHandler, 1);
        message.arg1 = this.mAvailability;
        message.sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class SoundTriggerListener extends IHotwordRecognitionStatusCallback.Stub {
        private final Handler mHandler;

        public SoundTriggerListener(Handler handler) {
            this.mHandler = handler;
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onKeyphraseDetected(SoundTrigger.KeyphraseRecognitionEvent event, HotwordDetectedResult result) {
            Slog.m94i(AlwaysOnHotwordDetector.TAG, "onDetected");
            Message.obtain(this.mHandler, 2, new EventPayload.Builder(event).setHotwordDetectedResult(result).build()).sendToTarget();
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onGenericSoundTriggerDetected(SoundTrigger.GenericRecognitionEvent event) {
            Slog.m90w(AlwaysOnHotwordDetector.TAG, "Generic sound trigger event detected at AOHD: " + event);
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onRejected(HotwordRejectedResult result) {
            Slog.m94i(AlwaysOnHotwordDetector.TAG, "onRejected");
            Message.obtain(this.mHandler, 6, result).sendToTarget();
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onError(int status) {
            Slog.m94i(AlwaysOnHotwordDetector.TAG, "onError: " + status);
            Message.obtain(this.mHandler, 3, new SoundTriggerFailure(status, "Sound trigger error")).sendToTarget();
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onDetectionFailure(DetectorFailure detectorFailure) {
            Slog.m92v(AlwaysOnHotwordDetector.TAG, "onDetectionFailure detectorFailure: " + detectorFailure);
            Message.obtain(this.mHandler, 3, detectorFailure != null ? detectorFailure : new UnknownFailure("Error data is null")).sendToTarget();
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onRecognitionPaused() {
            Slog.m94i(AlwaysOnHotwordDetector.TAG, "onRecognitionPaused");
            this.mHandler.sendEmptyMessage(4);
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onRecognitionResumed() {
            Slog.m94i(AlwaysOnHotwordDetector.TAG, "onRecognitionResumed");
            this.mHandler.sendEmptyMessage(5);
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onStatusReported(int status) {
            Slog.m94i(AlwaysOnHotwordDetector.TAG, "onStatusReported");
            Message message = Message.obtain(this.mHandler, 7);
            message.arg1 = status;
            message.sendToTarget();
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onProcessRestarted() {
            Slog.m94i(AlwaysOnHotwordDetector.TAG, "onProcessRestarted");
            this.mHandler.sendEmptyMessage(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDetectorRemoteException() {
        Message.obtain(this.mHandler, 3, new HotwordDetectionServiceFailure(7, "Detector remote exception occurs")).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            synchronized (AlwaysOnHotwordDetector.this.mLock) {
                if (AlwaysOnHotwordDetector.this.mAvailability == -3) {
                    Slog.m90w(AlwaysOnHotwordDetector.TAG, "Received message: " + msg.what + " for an invalid detector");
                    return;
                }
                final Message message = Message.obtain(msg);
                Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.AlwaysOnHotwordDetector$MyHandler$$ExternalSyntheticLambda0
                    @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                    public final void runOrThrow() {
                        AlwaysOnHotwordDetector.MyHandler.this.lambda$handleMessage$1(message);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleMessage$1(final Message message) throws Exception {
            AlwaysOnHotwordDetector.this.mExternalExecutor.execute(new Runnable() { // from class: android.service.voice.AlwaysOnHotwordDetector$MyHandler$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AlwaysOnHotwordDetector.MyHandler.this.lambda$handleMessage$0(message);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleMessage$0(Message message) {
            Slog.m94i(AlwaysOnHotwordDetector.TAG, "handle message " + message.what);
            switch (message.what) {
                case 1:
                    AlwaysOnHotwordDetector.this.mExternalCallback.onAvailabilityChanged(message.arg1);
                    break;
                case 2:
                    AlwaysOnHotwordDetector.this.mExternalCallback.onDetected((EventPayload) message.obj);
                    break;
                case 3:
                    AlwaysOnHotwordDetector.this.mExternalCallback.onFailure((DetectorFailure) message.obj);
                    break;
                case 4:
                    AlwaysOnHotwordDetector.this.mExternalCallback.onRecognitionPaused();
                    break;
                case 5:
                    AlwaysOnHotwordDetector.this.mExternalCallback.onRecognitionResumed();
                    break;
                case 6:
                    AlwaysOnHotwordDetector.this.mExternalCallback.onRejected((HotwordRejectedResult) message.obj);
                    break;
                case 7:
                    AlwaysOnHotwordDetector.this.mExternalCallback.onHotwordDetectionServiceInitialized(message.arg1);
                    break;
                case 8:
                    AlwaysOnHotwordDetector.this.mExternalCallback.onHotwordDetectionServiceRestarted();
                    break;
                default:
                    super.handleMessage(message);
                    break;
            }
            message.recycle();
        }
    }

    /* loaded from: classes3.dex */
    class RefreshAvailabilityTask extends AsyncTask<Void, Void, Void> {
        RefreshAvailabilityTask() {
        }

        @Override // android.p008os.AsyncTask
        public Void doInBackground(Void... params) {
            try {
                int availability = internalGetInitialAvailability();
                synchronized (AlwaysOnHotwordDetector.this.mLock) {
                    if (availability == 0) {
                        internalUpdateEnrolledKeyphraseMetadata();
                        if (AlwaysOnHotwordDetector.this.mKeyphraseMetadata != null) {
                            availability = 2;
                        } else {
                            availability = 1;
                        }
                    }
                    AlwaysOnHotwordDetector.this.updateAndNotifyStateChangedLocked(availability);
                }
                return null;
            } catch (SecurityException e) {
                Slog.m89w(AlwaysOnHotwordDetector.TAG, "Failed to refresh availability", e);
                if (AlwaysOnHotwordDetector.this.mTargetSdkVersion <= 30) {
                    throw e;
                }
                synchronized (AlwaysOnHotwordDetector.this.mLock) {
                    AlwaysOnHotwordDetector.this.updateAndNotifyStateChangedLocked(3);
                    return null;
                }
            }
        }

        private int internalGetInitialAvailability() {
            synchronized (AlwaysOnHotwordDetector.this.mLock) {
                if (AlwaysOnHotwordDetector.this.mAvailability == -3) {
                    return -3;
                }
                try {
                    SoundTrigger.ModuleProperties dspModuleProperties = AlwaysOnHotwordDetector.this.mSoundTriggerSession.getDspModuleProperties();
                    if (dspModuleProperties == null) {
                        return -2;
                    }
                    return 0;
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }

        private void internalUpdateEnrolledKeyphraseMetadata() {
            try {
                AlwaysOnHotwordDetector alwaysOnHotwordDetector = AlwaysOnHotwordDetector.this;
                alwaysOnHotwordDetector.mKeyphraseMetadata = alwaysOnHotwordDetector.mModelManagementService.getEnrolledKeyphraseMetadata(AlwaysOnHotwordDetector.this.mText, AlwaysOnHotwordDetector.this.mLocale.toLanguageTag());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean equals(Object obj) {
        if (CompatChanges.isChangeEnabled(193232191L)) {
            if (obj instanceof AlwaysOnHotwordDetector) {
                AlwaysOnHotwordDetector other = (AlwaysOnHotwordDetector) obj;
                return TextUtils.equals(this.mText, other.mText) && this.mLocale.equals(other.mLocale);
            }
            return false;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        return Objects.hash(this.mText, this.mLocale);
    }

    @Override // android.service.voice.HotwordDetector
    public void dump(String prefix, PrintWriter pw) {
        synchronized (this.mLock) {
            pw.print(prefix);
            pw.print("Text=");
            pw.println(this.mText);
            pw.print(prefix);
            pw.print("Locale=");
            pw.println(this.mLocale);
            pw.print(prefix);
            pw.print("Availability=");
            pw.println(this.mAvailability);
            pw.print(prefix);
            pw.print("KeyphraseMetadata=");
            pw.println(this.mKeyphraseMetadata);
            pw.print(prefix);
            pw.print("EnrollmentInfo=");
            pw.println(this.mKeyphraseEnrollmentInfo);
        }
    }
}
