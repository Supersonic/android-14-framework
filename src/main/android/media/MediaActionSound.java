package android.media;

import android.media.AudioAttributes;
import android.media.IAudioService;
import android.media.SoundPool;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Log;
/* loaded from: classes2.dex */
public class MediaActionSound {
    public static final int FOCUS_COMPLETE = 1;
    private static final int NUM_MEDIA_SOUND_STREAMS = 1;
    public static final int SHUTTER_CLICK = 0;
    private static final String[] SOUND_DIRS = {"/product/media/audio/ui/", "/system/media/audio/ui/"};
    private static final String[] SOUND_FILES = {"camera_click.ogg", "camera_focus.ogg", "VideoRecord.ogg", "VideoStop.ogg"};
    public static final int START_VIDEO_RECORDING = 2;
    private static final int STATE_LOADED = 3;
    private static final int STATE_LOADING = 1;
    private static final int STATE_LOADING_PLAY_REQUESTED = 2;
    private static final int STATE_NOT_LOADED = 0;
    public static final int STOP_VIDEO_RECORDING = 3;
    private static final String TAG = "MediaActionSound";
    private SoundPool.OnLoadCompleteListener mLoadCompleteListener = new SoundPool.OnLoadCompleteListener() { // from class: android.media.MediaActionSound.1
        @Override // android.media.SoundPool.OnLoadCompleteListener
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            SoundState[] soundStateArr;
            for (SoundState sound : MediaActionSound.this.mSounds) {
                if (sound.f267id == sampleId) {
                    int playSoundId = 0;
                    synchronized (sound) {
                        if (status != 0) {
                            sound.state = 0;
                            sound.f267id = 0;
                            Log.m110e(MediaActionSound.TAG, "OnLoadCompleteListener() error: " + status + " loading sound: " + sound.name);
                            return;
                        }
                        switch (sound.state) {
                            case 1:
                                sound.state = 3;
                                break;
                            case 2:
                                playSoundId = sound.f267id;
                                sound.state = 3;
                                break;
                            default:
                                Log.m110e(MediaActionSound.TAG, "OnLoadCompleteListener() called in wrong state: " + sound.state + " for sound: " + sound.name);
                                break;
                        }
                        if (playSoundId != 0) {
                            soundPool.play(playSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
                            return;
                        }
                        return;
                    }
                }
            }
        }
    };
    private SoundPool mSoundPool;
    private SoundState[] mSounds;

    public static boolean mustPlayShutterSound() {
        IBinder b = ServiceManager.getService("audio");
        IAudioService audioService = IAudioService.Stub.asInterface(b);
        try {
            boolean result = audioService.isCameraSoundForced();
            return result;
        } catch (RemoteException e) {
            Log.m110e(TAG, "audio service is unavailable for queries, defaulting to false");
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class SoundState {
        public final int name;

        /* renamed from: id */
        public int f267id = 0;
        public int state = 0;

        public SoundState(int name) {
            this.name = name;
        }
    }

    public MediaActionSound() {
        SoundPool build = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder().setUsage(13).setFlags(1).setContentType(4).build()).build();
        this.mSoundPool = build;
        build.setOnLoadCompleteListener(this.mLoadCompleteListener);
        this.mSounds = new SoundState[SOUND_FILES.length];
        int i = 0;
        while (true) {
            SoundState[] soundStateArr = this.mSounds;
            if (i < soundStateArr.length) {
                soundStateArr[i] = new SoundState(i);
                i++;
            } else {
                return;
            }
        }
    }

    private int loadSound(SoundState sound) {
        String[] strArr;
        String soundFileName = SOUND_FILES[sound.name];
        for (String soundDir : SOUND_DIRS) {
            int id = this.mSoundPool.load(soundDir + soundFileName, 1);
            if (id > 0) {
                sound.state = 1;
                sound.f267id = id;
                return id;
            }
        }
        return 0;
    }

    public void load(int soundName) {
        if (soundName < 0 || soundName >= SOUND_FILES.length) {
            throw new RuntimeException("Unknown sound requested: " + soundName);
        }
        SoundState sound = this.mSounds[soundName];
        synchronized (sound) {
            switch (sound.state) {
                case 0:
                    if (loadSound(sound) <= 0) {
                        Log.m110e(TAG, "load() error loading sound: " + soundName);
                        break;
                    }
                    break;
                default:
                    Log.m110e(TAG, "load() called in wrong state: " + sound + " for sound: " + soundName);
                    break;
            }
        }
    }

    public void play(int soundName) {
        if (soundName < 0 || soundName >= SOUND_FILES.length) {
            throw new RuntimeException("Unknown sound requested: " + soundName);
        }
        SoundState sound = this.mSounds[soundName];
        synchronized (sound) {
            switch (sound.state) {
                case 0:
                    loadSound(sound);
                    if (loadSound(sound) <= 0) {
                        Log.m110e(TAG, "play() error loading sound: " + soundName);
                        break;
                    }
                case 1:
                    sound.state = 2;
                    break;
                case 2:
                default:
                    Log.m110e(TAG, "play() called in wrong state: " + sound.state + " for sound: " + soundName);
                    break;
                case 3:
                    this.mSoundPool.play(sound.f267id, 1.0f, 1.0f, 0, 0, 1.0f);
                    break;
            }
        }
    }

    public void release() {
        SoundState[] soundStateArr;
        if (this.mSoundPool != null) {
            for (SoundState sound : this.mSounds) {
                synchronized (sound) {
                    sound.state = 0;
                    sound.f267id = 0;
                }
            }
            this.mSoundPool.release();
            this.mSoundPool = null;
        }
    }
}
