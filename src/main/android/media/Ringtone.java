package android.media;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.VolumeShaper;
import android.media.audiofx.HapticGenerator;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.RemoteException;
import android.p008os.Trace;
import android.util.Log;
import com.android.internal.C4057R;
import java.io.IOException;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class Ringtone {
    private static final boolean LOGD = true;
    private static final String MEDIA_SELECTION = "mime_type LIKE 'audio/%' OR mime_type IN ('application/ogg', 'application/x-flac')";
    private static final String TAG = "Ringtone";
    private final boolean mAllowRemote;
    private final AudioManager mAudioManager;
    private final Context mContext;
    private HapticGenerator mHapticGenerator;
    private MediaPlayer mLocalPlayer;
    private boolean mPreferBuiltinDevice;
    private final IRingtonePlayer mRemotePlayer;
    private final Binder mRemoteToken;
    private String mTitle;
    private Uri mUri;
    private VolumeShaper mVolumeShaper;
    private VolumeShaper.Configuration mVolumeShaperConfig;
    private static final String[] MEDIA_COLUMNS = {"_id", "title"};
    private static final ArrayList<Ringtone> sActiveRingtones = new ArrayList<>();
    private final MyOnCompletionListener mCompletionListener = new MyOnCompletionListener();
    private AudioAttributes mAudioAttributes = new AudioAttributes.Builder().setUsage(6).setContentType(4).build();
    private boolean mIsLooping = false;
    private float mVolume = 1.0f;
    private boolean mHapticGeneratorEnabled = false;
    private final Object mPlaybackSettingsLock = new Object();

    public Ringtone(Context context, boolean allowRemote) {
        this.mContext = context;
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        this.mAudioManager = audioManager;
        this.mAllowRemote = allowRemote;
        this.mRemotePlayer = allowRemote ? audioManager.getRingtonePlayer() : null;
        this.mRemoteToken = allowRemote ? new Binder() : null;
    }

    @Deprecated
    public void setStreamType(int streamType) {
        PlayerBase.deprecateStreamTypeForPlayback(streamType, TAG, "setStreamType()");
        setAudioAttributes(new AudioAttributes.Builder().setInternalLegacyStreamType(streamType).build());
    }

    @Deprecated
    public int getStreamType() {
        return AudioAttributes.toLegacyStreamType(this.mAudioAttributes);
    }

    public void setAudioAttributes(AudioAttributes attributes) throws IllegalArgumentException {
        setAudioAttributesField(attributes);
        setUri(this.mUri, this.mVolumeShaperConfig);
        createLocalMediaPlayer();
    }

    public void setAudioAttributesField(AudioAttributes attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Invalid null AudioAttributes for Ringtone");
        }
        this.mAudioAttributes = attributes;
    }

    private AudioDeviceInfo getBuiltinDevice(AudioManager audioManager) {
        AudioDeviceInfo[] deviceList = audioManager.getDevices(2);
        for (AudioDeviceInfo device : deviceList) {
            if (device.getType() == 2) {
                return device;
            }
        }
        return null;
    }

    public boolean preferBuiltinDevice(boolean enable) {
        this.mPreferBuiltinDevice = enable;
        MediaPlayer mediaPlayer = this.mLocalPlayer;
        if (mediaPlayer == null) {
            return true;
        }
        return mediaPlayer.setPreferredDevice(getBuiltinDevice(this.mAudioManager));
    }

    public boolean createLocalMediaPlayer() {
        Trace.beginSection("createLocalMediaPlayer");
        if (this.mUri == null) {
            Log.m110e(TAG, "Could not create media player as no URI was provided.");
            return this.mAllowRemote && this.mRemotePlayer != null;
        }
        destroyLocalPlayer();
        MediaPlayer mediaPlayer = new MediaPlayer();
        this.mLocalPlayer = mediaPlayer;
        try {
            mediaPlayer.setDataSource(this.mContext, this.mUri);
            this.mLocalPlayer.setAudioAttributes(this.mAudioAttributes);
            this.mLocalPlayer.setPreferredDevice(this.mPreferBuiltinDevice ? getBuiltinDevice(this.mAudioManager) : null);
            synchronized (this.mPlaybackSettingsLock) {
                applyPlaybackProperties_sync();
            }
            VolumeShaper.Configuration configuration = this.mVolumeShaperConfig;
            if (configuration != null) {
                this.mVolumeShaper = this.mLocalPlayer.createVolumeShaper(configuration);
            }
            this.mLocalPlayer.prepare();
        } catch (IOException | SecurityException e) {
            destroyLocalPlayer();
            if (!this.mAllowRemote) {
                Log.m104w(TAG, "Remote playback not allowed: " + e);
            }
        }
        if (this.mLocalPlayer != null) {
            Log.m112d(TAG, "Successfully created local player");
        } else {
            Log.m112d(TAG, "Problem opening; delegating to remote player");
        }
        Trace.endSection();
        if (this.mLocalPlayer == null) {
            return this.mAllowRemote && this.mRemotePlayer != null;
        }
        return true;
    }

    public boolean hasHapticChannels() {
        MediaPlayer.TrackInfo[] trackInfo;
        try {
            Trace.beginSection("Ringtone.hasHapticChannels");
            MediaPlayer mediaPlayer = this.mLocalPlayer;
            if (mediaPlayer != null) {
                for (MediaPlayer.TrackInfo trackInfo2 : mediaPlayer.getTrackInfo()) {
                    if (trackInfo2.hasHapticChannels()) {
                        Trace.endSection();
                        return true;
                    }
                }
            }
            return false;
        } finally {
            Trace.endSection();
        }
    }

    public boolean hasLocalPlayer() {
        return this.mLocalPlayer != null;
    }

    public AudioAttributes getAudioAttributes() {
        return this.mAudioAttributes;
    }

    public void setLooping(boolean looping) {
        synchronized (this.mPlaybackSettingsLock) {
            this.mIsLooping = looping;
            applyPlaybackProperties_sync();
        }
    }

    public boolean isLooping() {
        boolean z;
        synchronized (this.mPlaybackSettingsLock) {
            z = this.mIsLooping;
        }
        return z;
    }

    public void setVolume(float volume) {
        synchronized (this.mPlaybackSettingsLock) {
            if (volume < 0.0f) {
                volume = 0.0f;
            }
            if (volume > 1.0f) {
                volume = 1.0f;
            }
            this.mVolume = volume;
            applyPlaybackProperties_sync();
        }
    }

    public float getVolume() {
        float f;
        synchronized (this.mPlaybackSettingsLock) {
            f = this.mVolume;
        }
        return f;
    }

    public boolean setHapticGeneratorEnabled(boolean enabled) {
        if (!HapticGenerator.isAvailable()) {
            return false;
        }
        synchronized (this.mPlaybackSettingsLock) {
            this.mHapticGeneratorEnabled = enabled;
            applyPlaybackProperties_sync();
        }
        return true;
    }

    public boolean isHapticGeneratorEnabled() {
        boolean z;
        synchronized (this.mPlaybackSettingsLock) {
            z = this.mHapticGeneratorEnabled;
        }
        return z;
    }

    private void applyPlaybackProperties_sync() {
        IRingtonePlayer iRingtonePlayer;
        MediaPlayer mediaPlayer = this.mLocalPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(this.mVolume);
            this.mLocalPlayer.setLooping(this.mIsLooping);
            if (this.mHapticGenerator == null && this.mHapticGeneratorEnabled) {
                this.mHapticGenerator = HapticGenerator.create(this.mLocalPlayer.getAudioSessionId());
            }
            HapticGenerator hapticGenerator = this.mHapticGenerator;
            if (hapticGenerator != null) {
                hapticGenerator.setEnabled(this.mHapticGeneratorEnabled);
            }
        } else if (this.mAllowRemote && (iRingtonePlayer = this.mRemotePlayer) != null) {
            try {
                iRingtonePlayer.setPlaybackProperties(this.mRemoteToken, this.mVolume, this.mIsLooping, this.mHapticGeneratorEnabled);
            } catch (RemoteException e) {
                Log.m103w(TAG, "Problem setting playback properties: ", e);
            }
        } else {
            Log.m104w(TAG, "Neither local nor remote player available when applying playback properties");
        }
    }

    public String getTitle(Context context) {
        String str = this.mTitle;
        if (str != null) {
            return str;
        }
        String title = getTitle(context, this.mUri, true, this.mAllowRemote);
        this.mTitle = title;
        return title;
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x0066, code lost:
        if (r9 != null) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0068, code lost:
        r9.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0090, code lost:
        if (r9 == null) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0093, code lost:
        if (r7 != null) goto L7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0095, code lost:
        r7 = r11.getLastPathSegment();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String getTitle(Context context, Uri uri, boolean followSettingsUri, boolean allowRemote) {
        ContentResolver res = context.getContentResolver();
        String title = null;
        if (uri != null) {
            String authority = ContentProvider.getAuthorityWithoutUserId(uri.getAuthority());
            if (!"settings".equals(authority)) {
                Cursor cursor = null;
                try {
                    try {
                        if ("media".equals(authority)) {
                            String mediaSelection = allowRemote ? null : MEDIA_SELECTION;
                            cursor = res.query(uri, MEDIA_COLUMNS, mediaSelection, null, null);
                            if (cursor != null && cursor.getCount() == 1) {
                                cursor.moveToFirst();
                                return cursor.getString(1);
                            }
                        }
                    } catch (SecurityException e) {
                        IRingtonePlayer mRemotePlayer = null;
                        if (allowRemote) {
                            AudioManager audioManager = (AudioManager) context.getSystemService("audio");
                            mRemotePlayer = audioManager.getRingtonePlayer();
                        }
                        if (mRemotePlayer != null) {
                            try {
                                title = mRemotePlayer.getTitle(uri);
                            } catch (RemoteException e2) {
                            }
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } else if (followSettingsUri) {
                Uri actualUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.getDefaultType(uri));
                String actualTitle = getTitle(context, actualUri, false, allowRemote);
                title = context.getString(C4057R.string.ringtone_default_with_actual, actualTitle);
            }
        } else {
            title = context.getString(C4057R.string.ringtone_silent);
        }
        if (title == null) {
            String title2 = context.getString(C4057R.string.ringtone_unknown);
            return title2 == null ? "" : title2;
        }
        return title;
    }

    public void setUri(Uri uri) {
        setUri(uri, null);
    }

    public void setVolumeShaperConfig(VolumeShaper.Configuration volumeShaperConfig) {
        this.mVolumeShaperConfig = volumeShaperConfig;
    }

    public void setUri(Uri uri, VolumeShaper.Configuration volumeShaperConfig) {
        this.mVolumeShaperConfig = volumeShaperConfig;
        this.mUri = uri;
        if (uri == null) {
            destroyLocalPlayer();
        }
    }

    public Uri getUri() {
        return this.mUri;
    }

    public void play() {
        Uri uri;
        boolean looping;
        float volume;
        if (this.mLocalPlayer != null) {
            if (this.mAudioManager.getStreamVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttributes)) != 0) {
                startLocalPlayer();
            } else if (!this.mAudioAttributes.areHapticChannelsMuted() && hasHapticChannels()) {
                startLocalPlayer();
            }
        } else if (this.mAllowRemote && this.mRemotePlayer != null && (uri = this.mUri) != null) {
            Uri canonicalUri = uri.getCanonicalUri();
            synchronized (this.mPlaybackSettingsLock) {
                looping = this.mIsLooping;
                volume = this.mVolume;
            }
            try {
                this.mRemotePlayer.playWithVolumeShaping(this.mRemoteToken, canonicalUri, this.mAudioAttributes, volume, looping, this.mVolumeShaperConfig);
            } catch (RemoteException e) {
                if (!playFallbackRingtone()) {
                    Log.m104w(TAG, "Problem playing ringtone: " + e);
                }
            }
        } else if (!playFallbackRingtone()) {
            Log.m104w(TAG, "Neither local nor remote playback available");
        }
    }

    public void stop() {
        IRingtonePlayer iRingtonePlayer;
        if (this.mLocalPlayer != null) {
            destroyLocalPlayer();
        } else if (this.mAllowRemote && (iRingtonePlayer = this.mRemotePlayer) != null) {
            try {
                iRingtonePlayer.stop(this.mRemoteToken);
            } catch (RemoteException e) {
                Log.m104w(TAG, "Problem stopping ringtone: " + e);
            }
        }
    }

    private void destroyLocalPlayer() {
        if (this.mLocalPlayer != null) {
            HapticGenerator hapticGenerator = this.mHapticGenerator;
            if (hapticGenerator != null) {
                hapticGenerator.release();
                this.mHapticGenerator = null;
            }
            this.mLocalPlayer.setOnCompletionListener(null);
            this.mLocalPlayer.reset();
            this.mLocalPlayer.release();
            this.mLocalPlayer = null;
            this.mVolumeShaper = null;
            ArrayList<Ringtone> arrayList = sActiveRingtones;
            synchronized (arrayList) {
                arrayList.remove(this);
            }
        }
    }

    private void startLocalPlayer() {
        if (this.mLocalPlayer == null) {
            return;
        }
        ArrayList<Ringtone> arrayList = sActiveRingtones;
        synchronized (arrayList) {
            arrayList.add(this);
        }
        this.mLocalPlayer.setOnCompletionListener(this.mCompletionListener);
        this.mLocalPlayer.start();
        VolumeShaper volumeShaper = this.mVolumeShaper;
        if (volumeShaper != null) {
            volumeShaper.apply(VolumeShaper.Operation.PLAY);
        }
    }

    public boolean isPlaying() {
        IRingtonePlayer iRingtonePlayer;
        MediaPlayer mediaPlayer = this.mLocalPlayer;
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        if (this.mAllowRemote && (iRingtonePlayer = this.mRemotePlayer) != null) {
            try {
                return iRingtonePlayer.isPlaying(this.mRemoteToken);
            } catch (RemoteException e) {
                Log.m104w(TAG, "Problem checking ringtone: " + e);
                return false;
            }
        }
        Log.m104w(TAG, "Neither local nor remote playback available");
        return false;
    }

    private boolean playFallbackRingtone() {
        int streamType = AudioAttributes.toLegacyStreamType(this.mAudioAttributes);
        if (this.mAudioManager.getStreamVolume(streamType) == 0) {
            return false;
        }
        int ringtoneType = RingtoneManager.getDefaultType(this.mUri);
        if (ringtoneType != -1 && RingtoneManager.getActualDefaultRingtoneUri(this.mContext, ringtoneType) == null) {
            Log.m104w(TAG, "not playing fallback for " + this.mUri);
            return false;
        }
        try {
            AssetFileDescriptor afd = this.mContext.getResources().openRawResourceFd(C4057R.C4061raw.fallbackring);
            if (afd == null) {
                Log.m110e(TAG, "Could not load fallback ringtone");
                return false;
            }
            this.mLocalPlayer = new MediaPlayer();
            if (afd.getDeclaredLength() < 0) {
                this.mLocalPlayer.setDataSource(afd.getFileDescriptor());
            } else {
                this.mLocalPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            }
            this.mLocalPlayer.setAudioAttributes(this.mAudioAttributes);
            synchronized (this.mPlaybackSettingsLock) {
                applyPlaybackProperties_sync();
            }
            VolumeShaper.Configuration configuration = this.mVolumeShaperConfig;
            if (configuration != null) {
                this.mVolumeShaper = this.mLocalPlayer.createVolumeShaper(configuration);
            }
            this.mLocalPlayer.prepare();
            startLocalPlayer();
            afd.close();
            return true;
        } catch (Resources.NotFoundException e) {
            Log.m110e(TAG, "Fallback ringtone does not exist");
            return false;
        } catch (IOException e2) {
            destroyLocalPlayer();
            Log.m110e(TAG, "Failed to open fallback ringtone");
            return false;
        }
    }

    void setTitle(String title) {
        this.mTitle = title;
    }

    protected void finalize() {
        MediaPlayer mediaPlayer = this.mLocalPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        MyOnCompletionListener() {
        }

        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mp) {
            synchronized (Ringtone.sActiveRingtones) {
                Ringtone.sActiveRingtones.remove(Ringtone.this);
            }
            mp.setOnCompletionListener(null);
        }
    }
}
