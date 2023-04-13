package android.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.VolumeShaper;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.ParcelFileDescriptor;
import android.util.AndroidRuntimeException;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes2.dex */
public class SoundPool extends PlayerBase {
    private static final boolean DEBUG;
    private static final int SAMPLE_LOADED = 1;
    private static final String TAG = "SoundPool";
    private final AudioAttributes mAttributes;
    private final AtomicReference<EventHandler> mEventHandler;
    private boolean mHasAppOpsPlayAudio;
    private long mNativeContext;

    /* loaded from: classes2.dex */
    public interface OnLoadCompleteListener {
        void onLoadComplete(SoundPool soundPool, int i, int i2);
    }

    private final native int _load(FileDescriptor fileDescriptor, long j, long j2, int i);

    private final native void _mute(boolean z);

    private final native int _play(int i, float f, float f2, int i2, int i3, float f3, int i4);

    private final native void _setVolume(int i, float f, float f2);

    private final native void native_release();

    private native int native_setup(int i, Object obj, String str);

    public final native void autoPause();

    public final native void autoResume();

    public final native void pause(int i);

    public final native void resume(int i);

    public final native void setLoop(int i, int i2);

    public final native void setPriority(int i, int i2);

    public final native void setRate(int i, float f);

    public final native void stop(int i);

    public final native boolean unload(int i);

    static {
        System.loadLibrary("soundpool");
        DEBUG = Log.isLoggable(TAG, 3);
    }

    public SoundPool(int maxStreams, int streamType, int srcQuality) {
        this(null, maxStreams, new AudioAttributes.Builder().setInternalLegacyStreamType(streamType).build(), 0);
        PlayerBase.deprecateStreamTypeForPlayback(streamType, TAG, "SoundPool()");
    }

    private SoundPool(Context context, int maxStreams, AudioAttributes attributes, int sessionId) {
        super(attributes, 3);
        this.mEventHandler = new AtomicReference<>(null);
        if (native_setup(maxStreams, attributes, getCurrentOpPackageName()) != 0) {
            throw new RuntimeException("Native setup failed");
        }
        this.mAttributes = attributes;
        baseRegisterPlayer(resolvePlaybackSessionId(context, sessionId));
    }

    public final void release() {
        baseRelease();
        native_release();
    }

    protected void finalize() {
        release();
    }

    public int load(String path, int priority) {
        int id = 0;
        try {
            File f = new File(path);
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(f, 268435456);
            if (fd == null) {
                return 0;
            }
            id = _load(fd.getFileDescriptor(), 0L, f.length(), priority);
            fd.close();
            return id;
        } catch (IOException e) {
            Log.m110e(TAG, "error loading " + path);
            return id;
        }
    }

    public int load(Context context, int resId, int priority) {
        AssetFileDescriptor afd = context.getResources().openRawResourceFd(resId);
        int id = 0;
        if (afd != null) {
            id = _load(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength(), priority);
            try {
                afd.close();
            } catch (IOException e) {
            }
        }
        return id;
    }

    public int load(AssetFileDescriptor afd, int priority) {
        if (afd != null) {
            long len = afd.getLength();
            if (len < 0) {
                throw new AndroidRuntimeException("no length for fd");
            }
            return _load(afd.getFileDescriptor(), afd.getStartOffset(), len, priority);
        }
        return 0;
    }

    public int load(FileDescriptor fd, long offset, long length, int priority) {
        return _load(fd, offset, length, priority);
    }

    public final int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
        baseStart(0);
        return _play(soundID, leftVolume, rightVolume, priority, loop, rate, getPlayerIId());
    }

    public final void setVolume(int streamID, float leftVolume, float rightVolume) {
        _setVolume(streamID, leftVolume, rightVolume);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.media.PlayerBase
    public int playerApplyVolumeShaper(VolumeShaper.Configuration configuration, VolumeShaper.Operation operation) {
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.media.PlayerBase
    public VolumeShaper.State playerGetVolumeShaperState(int id) {
        return null;
    }

    @Override // android.media.PlayerBase
    void playerSetVolume(boolean muting, float leftVolume, float rightVolume) {
        _mute(muting);
    }

    @Override // android.media.PlayerBase
    int playerSetAuxEffectSendLevel(boolean muting, float level) {
        return 0;
    }

    @Override // android.media.PlayerBase
    void playerStart() {
    }

    @Override // android.media.PlayerBase
    void playerPause() {
    }

    @Override // android.media.PlayerBase
    void playerStop() {
    }

    public void setVolume(int streamID, float volume) {
        setVolume(streamID, volume, volume);
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
        if (listener == null) {
            this.mEventHandler.set(null);
            return;
        }
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this.mEventHandler.set(new EventHandler(looper, listener));
            return;
        }
        Looper looper2 = Looper.getMainLooper();
        if (looper2 == null) {
            this.mEventHandler.set(null);
        } else {
            this.mEventHandler.set(new EventHandler(looper2, listener));
        }
    }

    private void postEventFromNative(int msg, int arg1, int arg2, Object obj) {
        Handler eventHandler = this.mEventHandler.get();
        if (eventHandler == null) {
            return;
        }
        Message message = eventHandler.obtainMessage(msg, arg1, arg2, obj);
        eventHandler.sendMessage(message);
    }

    /* loaded from: classes2.dex */
    private final class EventHandler extends Handler {
        private final OnLoadCompleteListener mOnLoadCompleteListener;

        EventHandler(Looper looper, OnLoadCompleteListener onLoadCompleteListener) {
            super(looper);
            this.mOnLoadCompleteListener = onLoadCompleteListener;
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            if (msg.what != 1) {
                Log.m110e(SoundPool.TAG, "Unknown message type " + msg.what);
                return;
            }
            if (SoundPool.DEBUG) {
                Log.m112d(SoundPool.TAG, "Sample " + msg.arg1 + " loaded");
            }
            this.mOnLoadCompleteListener.onLoadComplete(SoundPool.this, msg.arg1, msg.arg2);
        }
    }

    /* loaded from: classes2.dex */
    public static class Builder {
        private AudioAttributes mAudioAttributes;
        private Context mContext;
        private int mMaxStreams = 1;
        private int mSessionId = 0;

        public Builder setMaxStreams(int maxStreams) throws IllegalArgumentException {
            if (maxStreams <= 0) {
                throw new IllegalArgumentException("Strictly positive value required for the maximum number of streams");
            }
            this.mMaxStreams = maxStreams;
            return this;
        }

        public Builder setAudioAttributes(AudioAttributes attributes) throws IllegalArgumentException {
            if (attributes == null) {
                throw new IllegalArgumentException("Invalid null AudioAttributes");
            }
            this.mAudioAttributes = attributes;
            return this;
        }

        public Builder setAudioSessionId(int sessionId) {
            if (sessionId != 0 && sessionId < 1) {
                throw new IllegalArgumentException("Invalid audio session ID " + sessionId);
            }
            this.mSessionId = sessionId;
            return this;
        }

        public Builder setContext(Context context) {
            this.mContext = (Context) Objects.requireNonNull(context);
            return this;
        }

        public SoundPool build() {
            if (this.mAudioAttributes == null) {
                this.mAudioAttributes = new AudioAttributes.Builder().setUsage(1).build();
            }
            return new SoundPool(this.mContext, this.mMaxStreams, this.mAudioAttributes, this.mSessionId);
        }
    }
}
