package android.preference;

import android.app.NotificationManager;
import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.audiopolicy.AudioProductStrategy;
import android.net.Uri;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.Message;
import android.preference.VolumePreference;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import android.widget.SeekBar;
import com.android.internal.config.sysui.SystemUiDeviceConfigFlags;
import com.android.internal.p028os.SomeArgs;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
@Deprecated
/* loaded from: classes3.dex */
public class SeekBarVolumizer implements SeekBar.OnSeekBarChangeListener, Handler.Callback {
    private static final int CHECK_RINGTONE_PLAYBACK_DELAY_MS = 1000;
    private static final int MSG_GROUP_VOLUME_CHANGED = 1;
    private static final int MSG_INIT_SAMPLE = 3;
    private static final int MSG_SET_STREAM_VOLUME = 0;
    private static final int MSG_START_SAMPLE = 1;
    private static final int MSG_STOP_SAMPLE = 2;
    private static final String TAG = "SeekBarVolumizer";
    private boolean mAffectedByRingerMode;
    private boolean mAllowAlarms;
    private boolean mAllowMedia;
    private boolean mAllowRinger;
    private AudioAttributes mAttributes;
    private final AudioManager mAudioManager;
    private final Callback mCallback;
    private final Context mContext;
    private final Uri mDefaultUri;
    private Handler mHandler;
    private int mLastAudibleStreamVolume;
    private int mLastProgress;
    private final int mMaxStreamVolume;
    private boolean mMuted;
    private final NotificationManager mNotificationManager;
    private boolean mNotificationOrRing;
    private NotificationManager.Policy mNotificationPolicy;
    private int mOriginalStreamVolume;
    private boolean mPlaySample;
    private final Receiver mReceiver;
    private int mRingerMode;
    private Ringtone mRingtone;
    private SeekBar mSeekBar;
    private final int mStreamType;
    private final HandlerC2336H mUiHandler;
    private int mVolumeBeforeMute;
    private final AudioManager.VolumeGroupCallback mVolumeGroupCallback;
    private int mVolumeGroupId;
    private final Handler mVolumeHandler;
    private Observer mVolumeObserver;
    private int mZenMode;
    private static long sStopVolumeTime = 0;
    private static final long SET_STREAM_VOLUME_DELAY_MS = TimeUnit.MILLISECONDS.toMillis(500);
    private static final long START_SAMPLE_DELAY_MS = TimeUnit.MILLISECONDS.toMillis(500);
    private static final long DURATION_TO_START_DELAYING = TimeUnit.MILLISECONDS.toMillis(2000);

    /* loaded from: classes3.dex */
    public interface Callback {
        void onMuted(boolean z, boolean z2);

        void onProgressChanged(SeekBar seekBar, int i, boolean z);

        void onSampleStarting(SeekBarVolumizer seekBarVolumizer);

        void onStartTrackingTouch(SeekBarVolumizer seekBarVolumizer);

        default void onStopTrackingTouch(SeekBarVolumizer sbv) {
        }
    }

    public SeekBarVolumizer(Context context, int streamType, Uri defaultUri, Callback callback) {
        this(context, streamType, defaultUri, callback, true);
    }

    public SeekBarVolumizer(Context context, int streamType, Uri defaultUri, Callback callback, boolean playSample) {
        this.mVolumeHandler = new VolumeHandler();
        this.mVolumeGroupCallback = new AudioManager.VolumeGroupCallback() { // from class: android.preference.SeekBarVolumizer.1
            @Override // android.media.AudioManager.VolumeGroupCallback
            public void onAudioVolumeGroupChanged(int group, int flags) {
                if (SeekBarVolumizer.this.mHandler == null) {
                    return;
                }
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = Integer.valueOf(group);
                args.arg2 = Integer.valueOf(flags);
                SeekBarVolumizer.this.mVolumeHandler.sendMessage(SeekBarVolumizer.this.mHandler.obtainMessage(1, args));
            }
        };
        this.mUiHandler = new HandlerC2336H();
        this.mReceiver = new Receiver();
        this.mLastProgress = -1;
        this.mVolumeBeforeMute = -1;
        this.mContext = context;
        AudioManager audioManager = (AudioManager) context.getSystemService(AudioManager.class);
        this.mAudioManager = audioManager;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        this.mNotificationManager = notificationManager;
        NotificationManager.Policy consolidatedNotificationPolicy = notificationManager.getConsolidatedNotificationPolicy();
        this.mNotificationPolicy = consolidatedNotificationPolicy;
        this.mAllowAlarms = (consolidatedNotificationPolicy.priorityCategories & 32) != 0;
        this.mAllowMedia = (this.mNotificationPolicy.priorityCategories & 64) != 0;
        this.mAllowRinger = !ZenModeConfig.areAllPriorityOnlyRingerSoundsMuted(this.mNotificationPolicy);
        this.mStreamType = streamType;
        this.mAffectedByRingerMode = audioManager.isStreamAffectedByRingerMode(streamType);
        boolean isNotificationOrRing = isNotificationOrRing(streamType);
        this.mNotificationOrRing = isNotificationOrRing;
        if (isNotificationOrRing) {
            this.mRingerMode = audioManager.getRingerModeInternal();
        }
        this.mZenMode = notificationManager.getZenMode();
        if (hasAudioProductStrategies()) {
            this.mVolumeGroupId = getVolumeGroupIdForLegacyStreamType(streamType);
            this.mAttributes = getAudioAttributesForLegacyStreamType(streamType);
        }
        this.mMaxStreamVolume = audioManager.getStreamMaxVolume(streamType);
        this.mCallback = callback;
        this.mOriginalStreamVolume = audioManager.getStreamVolume(streamType);
        this.mLastAudibleStreamVolume = audioManager.getLastAudibleStreamVolume(streamType);
        boolean isStreamMute = audioManager.isStreamMute(streamType);
        this.mMuted = isStreamMute;
        this.mPlaySample = playSample;
        if (callback != null) {
            callback.onMuted(isStreamMute, isZenMuted());
        }
        if (defaultUri == null) {
            if (streamType == 2) {
                defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
            } else if (streamType == 5) {
                defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
            } else {
                defaultUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
            }
        }
        this.mDefaultUri = defaultUri;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasAudioProductStrategies() {
        return AudioManager.getAudioProductStrategies().size() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getVolumeGroupIdForLegacyStreamType(int streamType) {
        for (AudioProductStrategy productStrategy : AudioManager.getAudioProductStrategies()) {
            int volumeGroupId = productStrategy.getVolumeGroupIdForLegacyStreamType(streamType);
            if (volumeGroupId != -1) {
                return volumeGroupId;
            }
        }
        return ((Integer) AudioManager.getAudioProductStrategies().stream().map(new Function() { // from class: android.preference.SeekBarVolumizer$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer valueOf;
                valueOf = Integer.valueOf(((AudioProductStrategy) obj).getVolumeGroupIdForAudioAttributes(AudioProductStrategy.getDefaultAttributes()));
                return valueOf;
            }
        }).filter(new Predicate() { // from class: android.preference.SeekBarVolumizer$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return SeekBarVolumizer.lambda$getVolumeGroupIdForLegacyStreamType$1((Integer) obj);
            }
        }).findFirst().orElse(-1)).intValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getVolumeGroupIdForLegacyStreamType$1(Integer volumeGroupId) {
        return volumeGroupId.intValue() != -1;
    }

    private AudioAttributes getAudioAttributesForLegacyStreamType(int streamType) {
        for (AudioProductStrategy productStrategy : AudioManager.getAudioProductStrategies()) {
            AudioAttributes aa = productStrategy.getAudioAttributesForLegacyStreamType(streamType);
            if (aa != null) {
                return aa;
            }
        }
        return new AudioAttributes.Builder().setContentType(0).setUsage(0).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isNotificationOrRing(int stream) {
        return stream == 2 || stream == 5;
    }

    private static boolean isAlarmsStream(int stream) {
        return stream == 4;
    }

    private static boolean isMediaStream(int stream) {
        return stream == 3;
    }

    public void setSeekBar(SeekBar seekBar) {
        SeekBar seekBar2 = this.mSeekBar;
        if (seekBar2 != null) {
            seekBar2.setOnSeekBarChangeListener(null);
        }
        this.mSeekBar = seekBar;
        seekBar.setOnSeekBarChangeListener(null);
        this.mSeekBar.setMax(this.mMaxStreamVolume);
        updateSeekBar();
        this.mSeekBar.setOnSeekBarChangeListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isZenMuted() {
        int i;
        if ((this.mNotificationOrRing && this.mZenMode == 3) || (i = this.mZenMode) == 2) {
            return true;
        }
        if (i == 1) {
            if (!this.mAllowAlarms && isAlarmsStream(this.mStreamType)) {
                return true;
            }
            if (!this.mAllowMedia && isMediaStream(this.mStreamType)) {
                return true;
            }
            if (!this.mAllowRinger && isNotificationOrRing(this.mStreamType)) {
                return true;
            }
        }
        return false;
    }

    protected void updateSeekBar() {
        int i;
        boolean zenMuted = isZenMuted();
        this.mSeekBar.setEnabled(!zenMuted);
        if (zenMuted) {
            this.mSeekBar.setProgress(this.mLastAudibleStreamVolume, true);
        } else if (this.mNotificationOrRing && this.mRingerMode == 1) {
            if (!DeviceConfig.getBoolean("systemui", SystemUiDeviceConfigFlags.VOLUME_SEPARATE_NOTIFICATION, false) || (i = this.mStreamType) == 2 || (i == 5 && this.mMuted)) {
                this.mSeekBar.setProgress(0, true);
            }
        } else if (this.mMuted) {
            this.mSeekBar.setProgress(0, true);
        } else {
            SeekBar seekBar = this.mSeekBar;
            int i2 = this.mLastProgress;
            if (i2 <= -1) {
                i2 = this.mOriginalStreamVolume;
            }
            seekBar.setProgress(i2, true);
        }
    }

    @Override // android.p008os.Handler.Callback
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                boolean z = this.mMuted;
                if (z && this.mLastProgress > 0) {
                    this.mAudioManager.adjustStreamVolume(this.mStreamType, 100, 0);
                } else if (!z && this.mLastProgress == 0) {
                    this.mAudioManager.adjustStreamVolume(this.mStreamType, -100, 0);
                }
                this.mAudioManager.setStreamVolume(this.mStreamType, this.mLastProgress, 1024);
                return true;
            case 1:
                if (this.mPlaySample) {
                    onStartSample();
                    return true;
                }
                return true;
            case 2:
                if (this.mPlaySample) {
                    onStopSample();
                    return true;
                }
                return true;
            case 3:
                if (this.mPlaySample) {
                    onInitSample();
                    return true;
                }
                return true;
            default:
                Log.m110e(TAG, "invalid SeekBarVolumizer message: " + msg.what);
                return true;
        }
    }

    private void onInitSample() {
        synchronized (this) {
            Ringtone ringtone = RingtoneManager.getRingtone(this.mContext, this.mDefaultUri);
            this.mRingtone = ringtone;
            if (ringtone != null) {
                ringtone.setStreamType(this.mStreamType);
            }
        }
    }

    private void postStartSample() {
        long j;
        Handler handler = this.mHandler;
        if (handler == null) {
            return;
        }
        handler.removeMessages(1);
        Handler handler2 = this.mHandler;
        Message obtainMessage = handler2.obtainMessage(1);
        if (isSamplePlaying()) {
            j = 1000;
        } else {
            j = isDelay() ? START_SAMPLE_DELAY_MS : 0L;
        }
        handler2.sendMessageDelayed(obtainMessage, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDelay() {
        long durationTime = System.currentTimeMillis() - sStopVolumeTime;
        return durationTime >= 0 && durationTime < DURATION_TO_START_DELAYING;
    }

    private void setStopVolumeTime() {
        int i = this.mStreamType;
        if (i == 0 || i == 2 || ((DeviceConfig.getBoolean("systemui", SystemUiDeviceConfigFlags.VOLUME_SEPARATE_NOTIFICATION, false) && this.mStreamType == 5) || this.mStreamType == 4)) {
            sStopVolumeTime = System.currentTimeMillis();
        }
    }

    private void onStartSample() {
        if (!isSamplePlaying()) {
            Callback callback = this.mCallback;
            if (callback != null) {
                callback.onSampleStarting(this);
            }
            synchronized (this) {
                Ringtone ringtone = this.mRingtone;
                if (ringtone != null) {
                    ringtone.setAudioAttributes(new AudioAttributes.Builder(ringtone.getAudioAttributes()).setFlags(128).build());
                    this.mRingtone.play();
                }
            }
        }
    }

    private void postStopSample() {
        if (this.mHandler == null) {
            return;
        }
        setStopVolumeTime();
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(2));
    }

    private void onStopSample() {
        synchronized (this) {
            Ringtone ringtone = this.mRingtone;
            if (ringtone != null) {
                ringtone.stop();
            }
        }
    }

    public void stop() {
        if (this.mHandler == null) {
            return;
        }
        postStopSample();
        this.mContext.getContentResolver().unregisterContentObserver(this.mVolumeObserver);
        this.mReceiver.setListening(false);
        if (hasAudioProductStrategies()) {
            unregisterVolumeGroupCb();
        }
        this.mSeekBar.setOnSeekBarChangeListener(null);
        this.mHandler.getLooper().quitSafely();
        this.mHandler = null;
        this.mVolumeObserver = null;
    }

    public void start() {
        if (this.mHandler != null) {
            return;
        }
        HandlerThread thread = new HandlerThread("SeekBarVolumizer.CallbackHandler");
        thread.start();
        Handler handler = new Handler(thread.getLooper(), this);
        this.mHandler = handler;
        handler.sendEmptyMessage(3);
        this.mVolumeObserver = new Observer(this.mHandler);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.VOLUME_SETTINGS_INT[this.mStreamType]), false, this.mVolumeObserver);
        this.mReceiver.setListening(true);
        if (hasAudioProductStrategies()) {
            registerVolumeGroupCb();
        }
    }

    public void revertVolume() {
        this.mAudioManager.setStreamVolume(this.mStreamType, this.mOriginalStreamVolume, 0);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        if (fromTouch) {
            postSetVolume(progress);
        }
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onProgressChanged(seekBar, progress, fromTouch);
        }
    }

    private void postSetVolume(int progress) {
        Handler handler = this.mHandler;
        if (handler == null) {
            return;
        }
        this.mLastProgress = progress;
        handler.removeMessages(0);
        this.mHandler.removeMessages(1);
        Handler handler2 = this.mHandler;
        handler2.sendMessageDelayed(handler2.obtainMessage(0), isDelay() ? SET_STREAM_VOLUME_DELAY_MS : 0L);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onStartTrackingTouch(this);
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        postStartSample();
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onStopTrackingTouch(this);
        }
    }

    public boolean isSamplePlaying() {
        boolean z;
        synchronized (this) {
            Ringtone ringtone = this.mRingtone;
            z = ringtone != null && ringtone.isPlaying();
        }
        return z;
    }

    public void startSample() {
        postStartSample();
    }

    public void stopSample() {
        postStopSample();
    }

    public SeekBar getSeekBar() {
        return this.mSeekBar;
    }

    public void changeVolumeBy(int amount) {
        this.mSeekBar.incrementProgressBy(amount);
        postSetVolume(this.mSeekBar.getProgress());
        postStartSample();
        this.mVolumeBeforeMute = -1;
    }

    public void muteVolume() {
        int i = this.mVolumeBeforeMute;
        if (i != -1) {
            this.mSeekBar.setProgress(i, true);
            postSetVolume(this.mVolumeBeforeMute);
            postStartSample();
            this.mVolumeBeforeMute = -1;
            return;
        }
        this.mVolumeBeforeMute = this.mSeekBar.getProgress();
        this.mSeekBar.setProgress(0, true);
        postStopSample();
        postSetVolume(0);
    }

    public void onSaveInstanceState(VolumePreference.VolumeStore volumeStore) {
        int i = this.mLastProgress;
        if (i >= 0) {
            volumeStore.volume = i;
            volumeStore.originalVolume = this.mOriginalStreamVolume;
        }
    }

    public void onRestoreInstanceState(VolumePreference.VolumeStore volumeStore) {
        if (volumeStore.volume != -1) {
            this.mOriginalStreamVolume = volumeStore.originalVolume;
            int i = volumeStore.volume;
            this.mLastProgress = i;
            postSetVolume(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.preference.SeekBarVolumizer$H */
    /* loaded from: classes3.dex */
    public final class HandlerC2336H extends Handler {
        private static final int UPDATE_SLIDER = 1;

        private HandlerC2336H() {
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1 && SeekBarVolumizer.this.mSeekBar != null) {
                SeekBarVolumizer.this.mLastProgress = msg.arg1;
                SeekBarVolumizer.this.mLastAudibleStreamVolume = msg.arg2;
                boolean muted = ((Boolean) msg.obj).booleanValue();
                if (muted != SeekBarVolumizer.this.mMuted) {
                    SeekBarVolumizer.this.mMuted = muted;
                    if (SeekBarVolumizer.this.mCallback != null) {
                        SeekBarVolumizer.this.mCallback.onMuted(SeekBarVolumizer.this.mMuted, SeekBarVolumizer.this.isZenMuted());
                    }
                }
                SeekBarVolumizer.this.updateSeekBar();
            }
        }

        public void postUpdateSlider(int volume, int lastAudibleVolume, boolean mute) {
            obtainMessage(1, volume, lastAudibleVolume, Boolean.valueOf(mute)).sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSlider() {
        AudioManager audioManager;
        if (this.mSeekBar != null && (audioManager = this.mAudioManager) != null) {
            int volume = audioManager.getStreamVolume(this.mStreamType);
            int lastAudibleVolume = this.mAudioManager.getLastAudibleStreamVolume(this.mStreamType);
            boolean mute = this.mAudioManager.isStreamMute(this.mStreamType);
            this.mUiHandler.postUpdateSlider(volume, lastAudibleVolume, mute);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class Observer extends ContentObserver {
        public Observer(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            SeekBarVolumizer.this.updateSlider();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class Receiver extends BroadcastReceiver {
        private boolean mListening;

        private Receiver() {
        }

        public void setListening(boolean listening) {
            if (this.mListening == listening) {
                return;
            }
            this.mListening = listening;
            if (listening) {
                IntentFilter filter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
                filter.addAction(AudioManager.INTERNAL_RINGER_MODE_CHANGED_ACTION);
                filter.addAction(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED);
                filter.addAction(NotificationManager.ACTION_NOTIFICATION_POLICY_CHANGED);
                filter.addAction(AudioManager.STREAM_DEVICES_CHANGED_ACTION);
                SeekBarVolumizer.this.mContext.registerReceiver(this, filter);
                return;
            }
            SeekBarVolumizer.this.mContext.unregisterReceiver(this);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.media.VOLUME_CHANGED_ACTION".equals(action)) {
                int streamType = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, -1);
                int streamValue = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE, -1);
                if (SeekBarVolumizer.this.hasAudioProductStrategies() && !SeekBarVolumizer.this.isDelay()) {
                    updateVolumeSlider(streamType, streamValue);
                }
            } else if (AudioManager.INTERNAL_RINGER_MODE_CHANGED_ACTION.equals(action)) {
                if (SeekBarVolumizer.this.mNotificationOrRing) {
                    SeekBarVolumizer seekBarVolumizer = SeekBarVolumizer.this;
                    seekBarVolumizer.mRingerMode = seekBarVolumizer.mAudioManager.getRingerModeInternal();
                }
                if (SeekBarVolumizer.this.mAffectedByRingerMode) {
                    SeekBarVolumizer.this.updateSlider();
                }
            } else if (AudioManager.STREAM_DEVICES_CHANGED_ACTION.equals(action)) {
                int streamType2 = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, -1);
                if (SeekBarVolumizer.this.hasAudioProductStrategies() && !SeekBarVolumizer.this.isDelay()) {
                    int streamVolume = SeekBarVolumizer.this.mAudioManager.getStreamVolume(streamType2);
                    updateVolumeSlider(streamType2, streamVolume);
                    return;
                }
                int volumeGroup = SeekBarVolumizer.this.getVolumeGroupIdForLegacyStreamType(streamType2);
                if (volumeGroup != -1 && volumeGroup == SeekBarVolumizer.this.mVolumeGroupId) {
                    int streamVolume2 = SeekBarVolumizer.this.mAudioManager.getStreamVolume(streamType2);
                    if (!SeekBarVolumizer.this.isDelay()) {
                        updateVolumeSlider(streamType2, streamVolume2);
                    }
                }
            } else if (NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED.equals(action)) {
                SeekBarVolumizer seekBarVolumizer2 = SeekBarVolumizer.this;
                seekBarVolumizer2.mZenMode = seekBarVolumizer2.mNotificationManager.getZenMode();
                SeekBarVolumizer.this.updateSlider();
            } else if (NotificationManager.ACTION_NOTIFICATION_POLICY_CHANGED.equals(action)) {
                SeekBarVolumizer seekBarVolumizer3 = SeekBarVolumizer.this;
                seekBarVolumizer3.mNotificationPolicy = seekBarVolumizer3.mNotificationManager.getConsolidatedNotificationPolicy();
                SeekBarVolumizer seekBarVolumizer4 = SeekBarVolumizer.this;
                seekBarVolumizer4.mAllowAlarms = (seekBarVolumizer4.mNotificationPolicy.priorityCategories & 32) != 0;
                SeekBarVolumizer seekBarVolumizer5 = SeekBarVolumizer.this;
                seekBarVolumizer5.mAllowMedia = (seekBarVolumizer5.mNotificationPolicy.priorityCategories & 64) != 0;
                SeekBarVolumizer seekBarVolumizer6 = SeekBarVolumizer.this;
                seekBarVolumizer6.mAllowRinger = !ZenModeConfig.areAllPriorityOnlyRingerSoundsMuted(seekBarVolumizer6.mNotificationPolicy);
                SeekBarVolumizer.this.updateSlider();
            }
        }

        private void updateVolumeSlider(int streamType, int streamValue) {
            boolean streamMatch;
            boolean z = false;
            if (!DeviceConfig.getBoolean("systemui", SystemUiDeviceConfigFlags.VOLUME_SEPARATE_NOTIFICATION, false) && SeekBarVolumizer.this.mNotificationOrRing) {
                streamMatch = SeekBarVolumizer.isNotificationOrRing(streamType);
            } else {
                streamMatch = streamType == SeekBarVolumizer.this.mStreamType;
            }
            if (SeekBarVolumizer.this.mSeekBar != null && streamMatch && streamValue != -1) {
                if (SeekBarVolumizer.this.mAudioManager.isStreamMute(SeekBarVolumizer.this.mStreamType) || streamValue == 0) {
                    z = true;
                }
                boolean muted = z;
                SeekBarVolumizer.this.mUiHandler.postUpdateSlider(streamValue, SeekBarVolumizer.this.mLastAudibleStreamVolume, muted);
            }
        }
    }

    private void registerVolumeGroupCb() {
        if (this.mVolumeGroupId != -1) {
            this.mAudioManager.registerVolumeGroupCallback(new PendingIntent$$ExternalSyntheticLambda1(), this.mVolumeGroupCallback);
            updateSlider();
        }
    }

    private void unregisterVolumeGroupCb() {
        if (this.mVolumeGroupId != -1) {
            this.mAudioManager.unregisterVolumeGroupCallback(this.mVolumeGroupCallback);
        }
    }

    /* loaded from: classes3.dex */
    private class VolumeHandler extends Handler {
        private VolumeHandler() {
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            SomeArgs args = (SomeArgs) msg.obj;
            switch (msg.what) {
                case 1:
                    int group = ((Integer) args.arg1).intValue();
                    if (SeekBarVolumizer.this.mVolumeGroupId != group || SeekBarVolumizer.this.mVolumeGroupId == -1) {
                        return;
                    }
                    SeekBarVolumizer.this.updateSlider();
                    return;
                default:
                    return;
            }
        }
    }
}
