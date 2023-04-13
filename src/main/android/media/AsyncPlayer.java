package android.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.p008os.PowerManager;
import android.p008os.SystemClock;
import android.util.Log;
import java.util.LinkedList;
/* loaded from: classes2.dex */
public class AsyncPlayer {
    private static final int PLAY = 1;
    private static final int STOP = 2;
    private static final boolean mDebug = false;
    private MediaPlayer mPlayer;
    private String mTag;
    private Thread mThread;
    private PowerManager.WakeLock mWakeLock;
    private final LinkedList<Command> mCmdQueue = new LinkedList<>();
    private int mState = 2;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class Command {
        AudioAttributes attributes;
        int code;
        Context context;
        boolean looping;
        long requestTime;
        Uri uri;

        private Command() {
        }

        public String toString() {
            return "{ code=" + this.code + " looping=" + this.looping + " attr=" + this.attributes + " uri=" + this.uri + " }";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSound(Command cmd) {
        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioAttributes(cmd.attributes);
            player.setDataSource(cmd.context, cmd.uri);
            player.setLooping(cmd.looping);
            player.prepare();
            player.start();
            MediaPlayer mediaPlayer = this.mPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            this.mPlayer = player;
            long delay = SystemClock.uptimeMillis() - cmd.requestTime;
            if (delay > 1000) {
                Log.m104w(this.mTag, "Notification sound delayed by " + delay + "msecs");
            }
        } catch (Exception e) {
            Log.m103w(this.mTag, "error loading sound for " + cmd.uri, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class Thread extends java.lang.Thread {
        Thread() {
            super("AsyncPlayer-" + AsyncPlayer.this.mTag);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Command cmd;
            while (true) {
                synchronized (AsyncPlayer.this.mCmdQueue) {
                    cmd = (Command) AsyncPlayer.this.mCmdQueue.removeFirst();
                }
                switch (cmd.code) {
                    case 1:
                        AsyncPlayer.this.startSound(cmd);
                        break;
                    case 2:
                        if (AsyncPlayer.this.mPlayer != null) {
                            long delay = SystemClock.uptimeMillis() - cmd.requestTime;
                            if (delay > 1000) {
                                Log.m104w(AsyncPlayer.this.mTag, "Notification stop delayed by " + delay + "msecs");
                            }
                            AsyncPlayer.this.mPlayer.stop();
                            AsyncPlayer.this.mPlayer.release();
                            AsyncPlayer.this.mPlayer = null;
                            break;
                        } else {
                            Log.m104w(AsyncPlayer.this.mTag, "STOP command without a player");
                            break;
                        }
                }
                synchronized (AsyncPlayer.this.mCmdQueue) {
                    if (AsyncPlayer.this.mCmdQueue.size() == 0) {
                        AsyncPlayer.this.mThread = null;
                        AsyncPlayer.this.releaseWakeLock();
                        return;
                    }
                }
            }
        }
    }

    public AsyncPlayer(String tag) {
        if (tag != null) {
            this.mTag = tag;
        } else {
            this.mTag = "AsyncPlayer";
        }
    }

    public void play(Context context, Uri uri, boolean looping, int stream) {
        PlayerBase.deprecateStreamTypeForPlayback(stream, "AsyncPlayer", "play()");
        if (context == null || uri == null) {
            return;
        }
        try {
            play(context, uri, looping, new AudioAttributes.Builder().setInternalLegacyStreamType(stream).build());
        } catch (IllegalArgumentException e) {
            Log.m109e(this.mTag, "Call to deprecated AsyncPlayer.play() method caused:", e);
        }
    }

    public void play(Context context, Uri uri, boolean looping, AudioAttributes attributes) throws IllegalArgumentException {
        if (context == null || uri == null || attributes == null) {
            throw new IllegalArgumentException("Illegal null AsyncPlayer.play() argument");
        }
        Command cmd = new Command();
        cmd.requestTime = SystemClock.uptimeMillis();
        cmd.code = 1;
        cmd.context = context;
        cmd.uri = uri;
        cmd.looping = looping;
        cmd.attributes = attributes;
        synchronized (this.mCmdQueue) {
            enqueueLocked(cmd);
            this.mState = 1;
        }
    }

    public void stop() {
        synchronized (this.mCmdQueue) {
            if (this.mState != 2) {
                Command cmd = new Command();
                cmd.requestTime = SystemClock.uptimeMillis();
                cmd.code = 2;
                enqueueLocked(cmd);
                this.mState = 2;
            }
        }
    }

    private void enqueueLocked(Command cmd) {
        this.mCmdQueue.add(cmd);
        if (this.mThread == null) {
            acquireWakeLock();
            Thread thread = new Thread();
            this.mThread = thread;
            thread.start();
        }
    }

    public void setUsesWakeLock(Context context) {
        if (this.mWakeLock != null || this.mThread != null) {
            throw new RuntimeException("assertion failed mWakeLock=" + this.mWakeLock + " mThread=" + this.mThread);
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(1, this.mTag);
    }

    private void acquireWakeLock() {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseWakeLock() {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            wakeLock.release();
        }
    }
}
