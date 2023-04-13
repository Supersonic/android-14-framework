package android.media;

import android.media.AudioManager;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.Message;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class AudioPortEventHandler {
    private static final int AUDIOPORT_EVENT_NEW_LISTENER = 4;
    private static final int AUDIOPORT_EVENT_PATCH_LIST_UPDATED = 2;
    private static final int AUDIOPORT_EVENT_PORT_LIST_UPDATED = 1;
    private static final int AUDIOPORT_EVENT_SERVICE_DIED = 3;
    private static final long RESCHEDULE_MESSAGE_DELAY_MS = 100;
    private static final String TAG = "AudioPortEventHandler";
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private long mJniCallback;
    private final Object mLock = new Object();
    private final ArrayList<AudioManager.OnAudioPortUpdateListener> mListeners = new ArrayList<>();

    private native void native_finalize();

    private native void native_setup(Object obj);

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init() {
        synchronized (this.mLock) {
            if (this.mHandler != null) {
                return;
            }
            HandlerThread handlerThread = new HandlerThread(TAG);
            this.mHandlerThread = handlerThread;
            handlerThread.start();
            if (this.mHandlerThread.getLooper() != null) {
                this.mHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: android.media.AudioPortEventHandler.1
                    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                    @Override // android.p008os.Handler
                    public void handleMessage(Message msg) {
                        ArrayList<AudioManager.OnAudioPortUpdateListener> listeners;
                        synchronized (AudioPortEventHandler.this.mLock) {
                            if (msg.what == 4) {
                                listeners = new ArrayList<>();
                                if (AudioPortEventHandler.this.mListeners.contains(msg.obj)) {
                                    listeners.add((AudioManager.OnAudioPortUpdateListener) msg.obj);
                                }
                            } else {
                                listeners = (ArrayList) AudioPortEventHandler.this.mListeners.clone();
                            }
                        }
                        if (msg.what == 1 || msg.what == 2 || msg.what == 3) {
                            AudioManager.resetAudioPortGeneration();
                        }
                        if (listeners.isEmpty()) {
                            return;
                        }
                        ArrayList<AudioPort> ports = new ArrayList<>();
                        ArrayList<AudioPatch> patches = new ArrayList<>();
                        if (msg.what != 3) {
                            int status = AudioManager.updateAudioPortCache(ports, patches, null);
                            if (status != 0) {
                                sendMessageDelayed(obtainMessage(msg.what, msg.obj), AudioPortEventHandler.RESCHEDULE_MESSAGE_DELAY_MS);
                                return;
                            }
                        }
                        int status2 = msg.what;
                        switch (status2) {
                            case 1:
                            case 4:
                                AudioPort[] portList = (AudioPort[]) ports.toArray(new AudioPort[0]);
                                for (int i = 0; i < listeners.size(); i++) {
                                    listeners.get(i).onAudioPortListUpdate(portList);
                                }
                                int i2 = msg.what;
                                if (i2 == 1) {
                                    return;
                                }
                                break;
                            case 2:
                                break;
                            case 3:
                                for (int i3 = 0; i3 < listeners.size(); i3++) {
                                    listeners.get(i3).onServiceDied();
                                }
                                return;
                            default:
                                return;
                        }
                        AudioPatch[] patchList = (AudioPatch[]) patches.toArray(new AudioPatch[0]);
                        for (int i4 = 0; i4 < listeners.size(); i4++) {
                            listeners.get(i4).onAudioPatchListUpdate(patchList);
                        }
                    }
                };
                native_setup(new WeakReference(this));
            } else {
                this.mHandler = null;
            }
        }
    }

    protected void finalize() {
        native_finalize();
        if (this.mHandlerThread.isAlive()) {
            this.mHandlerThread.quit();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerListener(AudioManager.OnAudioPortUpdateListener l) {
        synchronized (this.mLock) {
            this.mListeners.add(l);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            Message m = handler.obtainMessage(4, 0, 0, l);
            this.mHandler.sendMessage(m);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unregisterListener(AudioManager.OnAudioPortUpdateListener l) {
        synchronized (this.mLock) {
            this.mListeners.remove(l);
        }
    }

    Handler handler() {
        return this.mHandler;
    }

    private static void postEventFromNative(Object module_ref, int what, int arg1, int arg2, Object obj) {
        Handler handler;
        AudioPortEventHandler eventHandler = (AudioPortEventHandler) ((WeakReference) module_ref).get();
        if (eventHandler != null && eventHandler != null && (handler = eventHandler.handler()) != null) {
            Message m = handler.obtainMessage(what, arg1, arg2, obj);
            if (what != 4) {
                handler.removeMessages(what);
            }
            handler.sendMessage(m);
        }
    }
}
