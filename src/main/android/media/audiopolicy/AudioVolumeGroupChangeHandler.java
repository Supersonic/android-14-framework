package android.media.audiopolicy;

import android.media.AudioManager;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.Message;
import com.android.internal.util.Preconditions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class AudioVolumeGroupChangeHandler {
    private static final int AUDIOVOLUMEGROUP_EVENT_NEW_LISTENER = 4;
    private static final int AUDIOVOLUMEGROUP_EVENT_VOLUME_CHANGED = 1000;
    private static final String TAG = "AudioVolumeGroupChangeHandler";
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private long mJniCallback;
    private final ArrayList<AudioManager.VolumeGroupCallback> mListeners = new ArrayList<>();

    private native void native_finalize();

    private native void native_setup(Object obj);

    public void init() {
        synchronized (this) {
            if (this.mHandler != null) {
                return;
            }
            HandlerThread handlerThread = new HandlerThread(TAG);
            this.mHandlerThread = handlerThread;
            handlerThread.start();
            if (this.mHandlerThread.getLooper() == null) {
                this.mHandler = null;
                return;
            }
            this.mHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: android.media.audiopolicy.AudioVolumeGroupChangeHandler.1
                @Override // android.p008os.Handler
                public void handleMessage(Message msg) {
                    ArrayList<AudioManager.VolumeGroupCallback> listeners;
                    synchronized (this) {
                        if (msg.what == 4) {
                            listeners = new ArrayList<>();
                            if (AudioVolumeGroupChangeHandler.this.mListeners.contains(msg.obj)) {
                                listeners.add((AudioManager.VolumeGroupCallback) msg.obj);
                            }
                        } else {
                            listeners = (ArrayList) AudioVolumeGroupChangeHandler.this.mListeners.clone();
                        }
                    }
                    if (listeners.isEmpty()) {
                        return;
                    }
                    switch (msg.what) {
                        case 1000:
                            for (int i = 0; i < listeners.size(); i++) {
                                listeners.get(i).onAudioVolumeGroupChanged(msg.arg1, msg.arg2);
                            }
                            return;
                        default:
                            return;
                    }
                }
            };
            native_setup(new WeakReference(this));
        }
    }

    protected void finalize() {
        native_finalize();
        if (this.mHandlerThread.isAlive()) {
            this.mHandlerThread.quit();
        }
    }

    public void registerListener(AudioManager.VolumeGroupCallback cb) {
        Preconditions.checkNotNull(cb, "volume group callback shall not be null");
        synchronized (this) {
            this.mListeners.add(cb);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            Message m = handler.obtainMessage(4, 0, 0, cb);
            this.mHandler.sendMessage(m);
        }
    }

    public void unregisterListener(AudioManager.VolumeGroupCallback cb) {
        Preconditions.checkNotNull(cb, "volume group callback shall not be null");
        synchronized (this) {
            this.mListeners.remove(cb);
        }
    }

    Handler handler() {
        return this.mHandler;
    }

    private static void postEventFromNative(Object moduleRef, int what, int arg1, int arg2, Object obj) {
        Handler handler;
        AudioVolumeGroupChangeHandler eventHandler = (AudioVolumeGroupChangeHandler) ((WeakReference) moduleRef).get();
        if (eventHandler != null && eventHandler != null && (handler = eventHandler.handler()) != null) {
            Message m = handler.obtainMessage(what, arg1, arg2, obj);
            handler.sendMessage(m);
        }
    }
}
