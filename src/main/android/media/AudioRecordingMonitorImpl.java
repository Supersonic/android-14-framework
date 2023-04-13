package android.media;

import android.media.AudioManager;
import android.media.AudioRecordingMonitorImpl;
import android.media.IAudioService;
import android.media.IRecordingConfigDispatcher;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes2.dex */
public class AudioRecordingMonitorImpl implements AudioRecordingMonitor {
    private static final int MSG_RECORDING_CONFIG_CHANGE = 1;
    private static final String TAG = "android.media.AudioRecordingMonitor";
    private static IAudioService sService;
    private final AudioRecordingMonitorClient mClient;
    private volatile Handler mRecordingCallbackHandler;
    private HandlerThread mRecordingCallbackHandlerThread;
    private final Object mRecordCallbackLock = new Object();
    private LinkedList<AudioRecordingCallbackInfo> mRecordCallbackList = new LinkedList<>();
    private final IRecordingConfigDispatcher mRecordingCallback = new IRecordingConfigDispatcher.Stub() { // from class: android.media.AudioRecordingMonitorImpl.1
        @Override // android.media.IRecordingConfigDispatcher
        public void dispatchRecordingConfigChange(List<AudioRecordingConfiguration> configs) {
            AudioRecordingConfiguration config = AudioRecordingMonitorImpl.this.getMyConfig(configs);
            if (config != null) {
                synchronized (AudioRecordingMonitorImpl.this.mRecordCallbackLock) {
                    if (AudioRecordingMonitorImpl.this.mRecordingCallbackHandler != null) {
                        Message m = AudioRecordingMonitorImpl.this.mRecordingCallbackHandler.obtainMessage(1, config);
                        AudioRecordingMonitorImpl.this.mRecordingCallbackHandler.sendMessage(m);
                    }
                }
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioRecordingMonitorImpl(AudioRecordingMonitorClient client) {
        this.mClient = client;
    }

    @Override // android.media.AudioRecordingMonitor
    public void registerAudioRecordingCallback(Executor executor, AudioManager.AudioRecordingCallback cb) {
        if (cb == null) {
            throw new IllegalArgumentException("Illegal null AudioRecordingCallback");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Illegal null Executor");
        }
        synchronized (this.mRecordCallbackLock) {
            Iterator<AudioRecordingCallbackInfo> it = this.mRecordCallbackList.iterator();
            while (it.hasNext()) {
                AudioRecordingCallbackInfo arci = it.next();
                if (arci.mCb == cb) {
                    throw new IllegalArgumentException("AudioRecordingCallback already registered");
                }
            }
            beginRecordingCallbackHandling();
            this.mRecordCallbackList.add(new AudioRecordingCallbackInfo(executor, cb));
        }
    }

    @Override // android.media.AudioRecordingMonitor
    public void unregisterAudioRecordingCallback(AudioManager.AudioRecordingCallback cb) {
        if (cb == null) {
            throw new IllegalArgumentException("Illegal null AudioRecordingCallback argument");
        }
        synchronized (this.mRecordCallbackLock) {
            Iterator<AudioRecordingCallbackInfo> it = this.mRecordCallbackList.iterator();
            while (it.hasNext()) {
                AudioRecordingCallbackInfo arci = it.next();
                if (arci.mCb == cb) {
                    this.mRecordCallbackList.remove(arci);
                    if (this.mRecordCallbackList.size() == 0) {
                        endRecordingCallbackHandling();
                    }
                }
            }
            throw new IllegalArgumentException("AudioRecordingCallback was not registered");
        }
    }

    @Override // android.media.AudioRecordingMonitor
    public AudioRecordingConfiguration getActiveRecordingConfiguration() {
        IAudioService service = getService();
        try {
            List<AudioRecordingConfiguration> configs = service.getActiveRecordingConfigurations();
            return getMyConfig(configs);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class AudioRecordingCallbackInfo {
        final AudioManager.AudioRecordingCallback mCb;
        final Executor mExecutor;

        AudioRecordingCallbackInfo(Executor e, AudioManager.AudioRecordingCallback cb) {
            this.mExecutor = e;
            this.mCb = cb;
        }
    }

    private void beginRecordingCallbackHandling() {
        if (this.mRecordingCallbackHandlerThread == null) {
            HandlerThread handlerThread = new HandlerThread("android.media.AudioRecordingMonitor.RecordingCallback");
            this.mRecordingCallbackHandlerThread = handlerThread;
            handlerThread.start();
            Looper looper = this.mRecordingCallbackHandlerThread.getLooper();
            if (looper != null) {
                this.mRecordingCallbackHandler = new HandlerC16502(looper);
                IAudioService service = getService();
                try {
                    service.registerRecordingCallback(this.mRecordingCallback);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.media.AudioRecordingMonitorImpl$2 */
    /* loaded from: classes2.dex */
    public class HandlerC16502 extends Handler {
        HandlerC16502(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (msg.obj == null) {
                        return;
                    }
                    final ArrayList<AudioRecordingConfiguration> configs = new ArrayList<>();
                    configs.add((AudioRecordingConfiguration) msg.obj);
                    synchronized (AudioRecordingMonitorImpl.this.mRecordCallbackLock) {
                        if (AudioRecordingMonitorImpl.this.mRecordCallbackList.size() == 0) {
                            return;
                        }
                        LinkedList<AudioRecordingCallbackInfo> cbInfoList = new LinkedList<>(AudioRecordingMonitorImpl.this.mRecordCallbackList);
                        long identity = Binder.clearCallingIdentity();
                        try {
                            Iterator<AudioRecordingCallbackInfo> it = cbInfoList.iterator();
                            while (it.hasNext()) {
                                final AudioRecordingCallbackInfo cbi = it.next();
                                cbi.mExecutor.execute(new Runnable() { // from class: android.media.AudioRecordingMonitorImpl$2$$ExternalSyntheticLambda0
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        AudioRecordingMonitorImpl.AudioRecordingCallbackInfo.this.mCb.onRecordingConfigChanged(configs);
                                    }
                                });
                            }
                            return;
                        } finally {
                            Binder.restoreCallingIdentity(identity);
                        }
                    }
                default:
                    Log.m110e(AudioRecordingMonitorImpl.TAG, "Unknown event " + msg.what);
                    return;
            }
        }
    }

    private void endRecordingCallbackHandling() {
        if (this.mRecordingCallbackHandlerThread != null) {
            IAudioService service = getService();
            try {
                service.unregisterRecordingCallback(this.mRecordingCallback);
                this.mRecordingCallbackHandlerThread.quit();
                this.mRecordingCallbackHandlerThread = null;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    AudioRecordingConfiguration getMyConfig(List<AudioRecordingConfiguration> configs) {
        int portId = this.mClient.getPortId();
        for (AudioRecordingConfiguration config : configs) {
            if (config.getClientPortId() == portId) {
                return config;
            }
        }
        return null;
    }

    private static IAudioService getService() {
        IAudioService iAudioService = sService;
        if (iAudioService != null) {
            return iAudioService;
        }
        IBinder b = ServiceManager.getService("audio");
        IAudioService asInterface = IAudioService.Stub.asInterface(b);
        sService = asInterface;
        return asInterface;
    }
}
