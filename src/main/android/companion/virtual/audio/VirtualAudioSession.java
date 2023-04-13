package android.companion.virtual.audio;

import android.companion.virtual.audio.IAudioConfigChangedCallback;
import android.companion.virtual.audio.IAudioRoutingCallback;
import android.companion.virtual.audio.UserRestrictionsDetector;
import android.companion.virtual.audio.VirtualAudioDevice;
import android.companion.virtual.audio.VirtualAudioSession;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.media.AudioRecord;
import android.media.AudioRecordingConfiguration;
import android.media.AudioTrack;
import android.media.audiopolicy.AudioMix;
import android.media.audiopolicy.AudioMixingRule;
import android.media.audiopolicy.AudioPolicy;
import android.util.IntArray;
import android.util.Log;
import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class VirtualAudioSession extends IAudioRoutingCallback.Stub implements UserRestrictionsDetector.UserRestrictionsCallback, Closeable {
    private static final String TAG = "VirtualAudioSession";
    private AudioCapture mAudioCapture;
    private final AudioConfigChangedCallback mAudioConfigChangedCallback;
    private AudioInjection mAudioInjection;
    private AudioPolicy mAudioPolicy;
    private final Context mContext;
    private final Object mLock = new Object();
    private final IntArray mReroutedAppUids = new IntArray();
    private final UserRestrictionsDetector mUserRestrictionsDetector;

    /* loaded from: classes.dex */
    public static final class AudioConfigChangedCallback extends IAudioConfigChangedCallback.Stub {
        private final VirtualAudioDevice.AudioConfigurationChangeCallback mCallback;
        private final Executor mExecutor;

        AudioConfigChangedCallback(Context context, Executor executor, VirtualAudioDevice.AudioConfigurationChangeCallback callback) {
            this.mExecutor = executor != null ? executor : context.getMainExecutor();
            this.mCallback = callback;
        }

        @Override // android.companion.virtual.audio.IAudioConfigChangedCallback
        public void onPlaybackConfigChanged(final List<AudioPlaybackConfiguration> configs) {
            if (this.mCallback != null) {
                this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.audio.VirtualAudioSession$AudioConfigChangedCallback$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        VirtualAudioSession.AudioConfigChangedCallback.this.lambda$onPlaybackConfigChanged$0(configs);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPlaybackConfigChanged$0(List configs) {
            this.mCallback.onPlaybackConfigChanged(configs);
        }

        @Override // android.companion.virtual.audio.IAudioConfigChangedCallback
        public void onRecordingConfigChanged(final List<AudioRecordingConfiguration> configs) {
            if (this.mCallback != null) {
                this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.audio.VirtualAudioSession$AudioConfigChangedCallback$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        VirtualAudioSession.AudioConfigChangedCallback.this.lambda$onRecordingConfigChanged$1(configs);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRecordingConfigChanged$1(List configs) {
            this.mCallback.onRecordingConfigChanged(configs);
        }
    }

    public VirtualAudioSession(Context context, VirtualAudioDevice.AudioConfigurationChangeCallback callback, Executor executor) {
        this.mContext = context;
        this.mUserRestrictionsDetector = new UserRestrictionsDetector(context);
        this.mAudioConfigChangedCallback = callback == null ? null : new AudioConfigChangedCallback(context, executor, callback);
    }

    public AudioCapture startAudioCapture(AudioFormat captureFormat) {
        AudioCapture audioCapture;
        Objects.requireNonNull(captureFormat, "captureFormat must not be null");
        synchronized (this.mLock) {
            if (this.mAudioCapture != null) {
                throw new IllegalStateException("Cannot start capture while another capture is ongoing.");
            }
            AudioCapture audioCapture2 = new AudioCapture(captureFormat);
            this.mAudioCapture = audioCapture2;
            audioCapture2.startRecording();
            audioCapture = this.mAudioCapture;
        }
        return audioCapture;
    }

    public AudioInjection startAudioInjection(AudioFormat injectionFormat) {
        AudioInjection audioInjection;
        Objects.requireNonNull(injectionFormat, "injectionFormat must not be null");
        synchronized (this.mLock) {
            if (this.mAudioInjection != null) {
                throw new IllegalStateException("Cannot start injection while injection is already ongoing.");
            }
            AudioInjection audioInjection2 = new AudioInjection(injectionFormat);
            this.mAudioInjection = audioInjection2;
            audioInjection2.play();
            this.mUserRestrictionsDetector.register(this);
            this.mAudioInjection.setSilent(this.mUserRestrictionsDetector.isUnmuteMicrophoneDisallowed());
            audioInjection = this.mAudioInjection;
        }
        return audioInjection;
    }

    public AudioConfigChangedCallback getAudioConfigChangedListener() {
        return this.mAudioConfigChangedCallback;
    }

    public AudioCapture getAudioCapture() {
        AudioCapture audioCapture;
        synchronized (this.mLock) {
            audioCapture = this.mAudioCapture;
        }
        return audioCapture;
    }

    public AudioInjection getAudioInjection() {
        AudioInjection audioInjection;
        synchronized (this.mLock) {
            audioInjection = this.mAudioInjection;
        }
        return audioInjection;
    }

    @Override // android.companion.virtual.audio.IAudioRoutingCallback
    public void onAppsNeedingAudioRoutingChanged(int[] appUids) {
        synchronized (this.mLock) {
            if (Arrays.equals(this.mReroutedAppUids.toArray(), appUids)) {
                return;
            }
            releaseAudioStreams();
            if (appUids.length == 0) {
                return;
            }
            createAudioStreams(appUids);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.mUserRestrictionsDetector.unregister();
        releaseAudioStreams();
        synchronized (this.mLock) {
            AudioCapture audioCapture = this.mAudioCapture;
            if (audioCapture != null) {
                audioCapture.close();
                this.mAudioCapture = null;
            }
            AudioInjection audioInjection = this.mAudioInjection;
            if (audioInjection != null) {
                audioInjection.close();
                this.mAudioInjection = null;
            }
        }
    }

    @Override // android.companion.virtual.audio.UserRestrictionsDetector.UserRestrictionsCallback
    public void onMicrophoneRestrictionChanged(boolean isUnmuteMicDisallowed) {
        synchronized (this.mLock) {
            AudioInjection audioInjection = this.mAudioInjection;
            if (audioInjection != null) {
                audioInjection.setSilent(isUnmuteMicDisallowed);
            }
        }
    }

    private void createAudioStreams(int[] appUids) {
        synchronized (this.mLock) {
            if (this.mAudioCapture == null && this.mAudioInjection == null) {
                throw new IllegalStateException("At least one of AudioCapture and AudioInjection must be started.");
            }
            if (this.mAudioPolicy != null) {
                throw new IllegalStateException("Cannot create audio streams while the audio policy is registered. Call releaseAudioStreams() first to unregister the previous audio policy.");
            }
            this.mReroutedAppUids.clear();
            for (int appUid : appUids) {
                this.mReroutedAppUids.add(appUid);
            }
            AudioMix audioRecordMix = null;
            AudioMix audioTrackMix = null;
            AudioPolicy.Builder builder = new AudioPolicy.Builder(this.mContext);
            AudioCapture audioCapture = this.mAudioCapture;
            if (audioCapture != null) {
                audioRecordMix = createAudioRecordMix(audioCapture.getFormat(), appUids);
                builder.addMix(audioRecordMix);
            }
            AudioInjection audioInjection = this.mAudioInjection;
            if (audioInjection != null) {
                audioTrackMix = createAudioTrackMix(audioInjection.getFormat(), appUids);
                builder.addMix(audioTrackMix);
            }
            this.mAudioPolicy = builder.build();
            AudioManager audioManager = (AudioManager) this.mContext.getSystemService(AudioManager.class);
            if (audioManager.registerAudioPolicy(this.mAudioPolicy) == -1) {
                Log.m110e(TAG, "Failed to register audio policy!");
            }
            AudioRecord audioRecord = audioRecordMix != null ? this.mAudioPolicy.createAudioRecordSink(audioRecordMix) : null;
            AudioTrack audioTrack = audioTrackMix != null ? this.mAudioPolicy.createAudioTrackSource(audioTrackMix) : null;
            AudioCapture audioCapture2 = this.mAudioCapture;
            if (audioCapture2 != null) {
                audioCapture2.setAudioRecord(audioRecord);
            }
            AudioInjection audioInjection2 = this.mAudioInjection;
            if (audioInjection2 != null) {
                audioInjection2.setAudioTrack(audioTrack);
            }
        }
    }

    private void releaseAudioStreams() {
        synchronized (this.mLock) {
            AudioCapture audioCapture = this.mAudioCapture;
            if (audioCapture != null) {
                audioCapture.setAudioRecord(null);
            }
            AudioInjection audioInjection = this.mAudioInjection;
            if (audioInjection != null) {
                audioInjection.setAudioTrack(null);
            }
            this.mReroutedAppUids.clear();
            if (this.mAudioPolicy != null) {
                AudioManager audioManager = (AudioManager) this.mContext.getSystemService(AudioManager.class);
                audioManager.unregisterAudioPolicy(this.mAudioPolicy);
                this.mAudioPolicy = null;
                Log.m108i(TAG, "AudioPolicy unregistered");
            }
        }
    }

    public IntArray getReroutedAppUids() {
        IntArray intArray;
        synchronized (this.mLock) {
            intArray = this.mReroutedAppUids;
        }
        return intArray;
    }

    private static AudioMix createAudioRecordMix(AudioFormat audioFormat, int[] appUids) {
        AudioMixingRule.Builder builder = new AudioMixingRule.Builder();
        builder.setTargetMixRole(0);
        for (int uid : appUids) {
            builder.addMixRule(4, Integer.valueOf(uid));
        }
        AudioMixingRule audioMixingRule = builder.allowPrivilegedPlaybackCapture(false).build();
        AudioMix audioMix = new AudioMix.Builder(audioMixingRule).setFormat(audioFormat).setRouteFlags(2).build();
        return audioMix;
    }

    private static AudioMix createAudioTrackMix(AudioFormat audioFormat, int[] appUids) {
        AudioMixingRule.Builder builder = new AudioMixingRule.Builder();
        builder.setTargetMixRole(1);
        for (int uid : appUids) {
            builder.addMixRule(4, Integer.valueOf(uid));
        }
        AudioMixingRule audioMixingRule = builder.build();
        AudioMix audioMix = new AudioMix.Builder(audioMixingRule).setFormat(audioFormat).setRouteFlags(2).build();
        return audioMix;
    }
}
