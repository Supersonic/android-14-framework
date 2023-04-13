package com.android.server.soundtrigger_middleware;

import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.PhraseRecognitionEvent;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.Properties;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.RecognitionEvent;
import android.media.soundtrigger.SoundModel;
import android.os.IBinder;
import com.android.server.soundtrigger_middleware.ICaptureStateNotifier;
import com.android.server.soundtrigger_middleware.ISoundTriggerHal;
import com.android.server.soundtrigger_middleware.SoundTriggerHalConcurrentCaptureHandler;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes2.dex */
public class SoundTriggerHalConcurrentCaptureHandler implements ISoundTriggerHal, ICaptureStateNotifier.Listener {
    public boolean mCaptureState;
    public final ISoundTriggerHal mDelegate;
    public ISoundTriggerHal.GlobalCallback mGlobalCallback;
    public final ICaptureStateNotifier mNotifier;
    public final Object mStartStopLock = new Object();
    public final Map<Integer, LoadedModel> mLoadedModels = new ConcurrentHashMap();
    public final Set<Integer> mActiveModels = new HashSet();
    public final Map<IBinder.DeathRecipient, IBinder.DeathRecipient> mDeathRecipientMap = new ConcurrentHashMap();
    public final CallbackThread mCallbackThread = new CallbackThread();

    /* loaded from: classes2.dex */
    public static class LoadedModel {
        public final ISoundTriggerHal.ModelCallback callback;
        public final int type;

        public LoadedModel(int i, ISoundTriggerHal.ModelCallback modelCallback) {
            this.type = i;
            this.callback = modelCallback;
        }
    }

    public SoundTriggerHalConcurrentCaptureHandler(ISoundTriggerHal iSoundTriggerHal, ICaptureStateNotifier iCaptureStateNotifier) {
        this.mDelegate = iSoundTriggerHal;
        this.mNotifier = iCaptureStateNotifier;
        this.mCaptureState = iCaptureStateNotifier.registerListener(this);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void startRecognition(int i, int i2, int i3, RecognitionConfig recognitionConfig) {
        synchronized (this.mStartStopLock) {
            synchronized (this.mActiveModels) {
                if (this.mCaptureState) {
                    throw new RecoverableException(1);
                }
                this.mDelegate.startRecognition(i, i2, i3, recognitionConfig);
                this.mActiveModels.add(Integer.valueOf(i));
            }
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void stopRecognition(int i) {
        boolean remove;
        synchronized (this.mStartStopLock) {
            synchronized (this.mActiveModels) {
                remove = this.mActiveModels.remove(Integer.valueOf(i));
            }
            if (remove) {
                this.mDelegate.stopRecognition(i);
            }
        }
        this.mCallbackThread.flush();
    }

    @Override // com.android.server.soundtrigger_middleware.ICaptureStateNotifier.Listener
    public void onCaptureStateChange(boolean z) {
        synchronized (this.mStartStopLock) {
            if (z) {
                abortAllActiveModels();
            } else {
                ISoundTriggerHal.GlobalCallback globalCallback = this.mGlobalCallback;
                if (globalCallback != null) {
                    globalCallback.onResourcesAvailable();
                }
            }
            this.mCaptureState = z;
        }
    }

    public final void abortAllActiveModels() {
        final int intValue;
        while (true) {
            synchronized (this.mActiveModels) {
                Iterator<Integer> it = this.mActiveModels.iterator();
                if (!it.hasNext()) {
                    return;
                }
                intValue = it.next().intValue();
                this.mActiveModels.remove(Integer.valueOf(intValue));
            }
            this.mDelegate.stopRecognition(intValue);
            final LoadedModel loadedModel = this.mLoadedModels.get(Integer.valueOf(intValue));
            this.mCallbackThread.push(new Runnable() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHalConcurrentCaptureHandler$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SoundTriggerHalConcurrentCaptureHandler.notifyAbort(intValue, loadedModel);
                }
            });
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int loadSoundModel(SoundModel soundModel, ISoundTriggerHal.ModelCallback modelCallback) {
        int loadSoundModel = this.mDelegate.loadSoundModel(soundModel, new CallbackWrapper(modelCallback));
        this.mLoadedModels.put(Integer.valueOf(loadSoundModel), new LoadedModel(1, modelCallback));
        return loadSoundModel;
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int loadPhraseSoundModel(PhraseSoundModel phraseSoundModel, ISoundTriggerHal.ModelCallback modelCallback) {
        int loadPhraseSoundModel = this.mDelegate.loadPhraseSoundModel(phraseSoundModel, new CallbackWrapper(modelCallback));
        this.mLoadedModels.put(Integer.valueOf(loadPhraseSoundModel), new LoadedModel(0, modelCallback));
        return loadPhraseSoundModel;
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void unloadSoundModel(int i) {
        this.mLoadedModels.remove(Integer.valueOf(i));
        this.mDelegate.unloadSoundModel(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerCallback$1(final ISoundTriggerHal.GlobalCallback globalCallback) {
        CallbackThread callbackThread = this.mCallbackThread;
        Objects.requireNonNull(globalCallback);
        callbackThread.push(new Runnable() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHalConcurrentCaptureHandler$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ISoundTriggerHal.GlobalCallback.this.onResourcesAvailable();
            }
        });
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void registerCallback(final ISoundTriggerHal.GlobalCallback globalCallback) {
        ISoundTriggerHal.GlobalCallback globalCallback2 = new ISoundTriggerHal.GlobalCallback() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHalConcurrentCaptureHandler$$ExternalSyntheticLambda1
            @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal.GlobalCallback
            public final void onResourcesAvailable() {
                SoundTriggerHalConcurrentCaptureHandler.this.lambda$registerCallback$1(globalCallback);
            }
        };
        this.mGlobalCallback = globalCallback2;
        this.mDelegate.registerCallback(globalCallback2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$linkToDeath$2(final IBinder.DeathRecipient deathRecipient) {
        CallbackThread callbackThread = this.mCallbackThread;
        Objects.requireNonNull(deathRecipient);
        callbackThread.push(new Runnable() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHalConcurrentCaptureHandler$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                deathRecipient.binderDied();
            }
        });
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void linkToDeath(final IBinder.DeathRecipient deathRecipient) {
        IBinder.DeathRecipient deathRecipient2 = new IBinder.DeathRecipient() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHalConcurrentCaptureHandler$$ExternalSyntheticLambda0
            @Override // android.os.IBinder.DeathRecipient
            public final void binderDied() {
                SoundTriggerHalConcurrentCaptureHandler.this.lambda$linkToDeath$2(deathRecipient);
            }
        };
        this.mDelegate.linkToDeath(deathRecipient2);
        this.mDeathRecipientMap.put(deathRecipient, deathRecipient2);
    }

    /* loaded from: classes2.dex */
    public class CallbackWrapper implements ISoundTriggerHal.ModelCallback {
        public final ISoundTriggerHal.ModelCallback mDelegateCallback;

        public CallbackWrapper(ISoundTriggerHal.ModelCallback modelCallback) {
            this.mDelegateCallback = modelCallback;
        }

        @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal.ModelCallback
        public void recognitionCallback(final int i, final RecognitionEvent recognitionEvent) {
            synchronized (SoundTriggerHalConcurrentCaptureHandler.this.mActiveModels) {
                if (SoundTriggerHalConcurrentCaptureHandler.this.mActiveModels.contains(Integer.valueOf(i))) {
                    if (!recognitionEvent.recognitionStillActive) {
                        SoundTriggerHalConcurrentCaptureHandler.this.mActiveModels.remove(Integer.valueOf(i));
                    }
                    SoundTriggerHalConcurrentCaptureHandler.this.mCallbackThread.push(new Runnable() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHalConcurrentCaptureHandler$CallbackWrapper$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            SoundTriggerHalConcurrentCaptureHandler.CallbackWrapper.this.lambda$recognitionCallback$0(i, recognitionEvent);
                        }
                    });
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$recognitionCallback$0(int i, RecognitionEvent recognitionEvent) {
            this.mDelegateCallback.recognitionCallback(i, recognitionEvent);
        }

        @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal.ModelCallback
        public void phraseRecognitionCallback(final int i, final PhraseRecognitionEvent phraseRecognitionEvent) {
            synchronized (SoundTriggerHalConcurrentCaptureHandler.this.mActiveModels) {
                if (SoundTriggerHalConcurrentCaptureHandler.this.mActiveModels.contains(Integer.valueOf(i))) {
                    if (!phraseRecognitionEvent.common.recognitionStillActive) {
                        SoundTriggerHalConcurrentCaptureHandler.this.mActiveModels.remove(Integer.valueOf(i));
                    }
                    SoundTriggerHalConcurrentCaptureHandler.this.mCallbackThread.push(new Runnable() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHalConcurrentCaptureHandler$CallbackWrapper$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            SoundTriggerHalConcurrentCaptureHandler.CallbackWrapper.this.lambda$phraseRecognitionCallback$1(i, phraseRecognitionEvent);
                        }
                    });
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$phraseRecognitionCallback$1(int i, PhraseRecognitionEvent phraseRecognitionEvent) {
            this.mDelegateCallback.phraseRecognitionCallback(i, phraseRecognitionEvent);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$modelUnloaded$2(int i) {
            this.mDelegateCallback.modelUnloaded(i);
        }

        @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal.ModelCallback
        public void modelUnloaded(final int i) {
            SoundTriggerHalConcurrentCaptureHandler.this.mCallbackThread.push(new Runnable() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHalConcurrentCaptureHandler$CallbackWrapper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SoundTriggerHalConcurrentCaptureHandler.CallbackWrapper.this.lambda$modelUnloaded$2(i);
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    public static class CallbackThread implements Runnable {
        public final Thread mThread;
        public final Queue<Runnable> mList = new LinkedList();
        public int mPushCount = 0;
        public int mProcessedCount = 0;
        public boolean mQuitting = false;

        public CallbackThread() {
            Thread thread = new Thread(this, "STHAL Concurrent Capture Handler Callback");
            this.mThread = thread;
            thread.start();
        }

        @Override // java.lang.Runnable
        public void run() {
            while (true) {
                try {
                    Runnable pop = pop();
                    if (pop == null) {
                        return;
                    }
                    pop.run();
                    synchronized (this.mList) {
                        this.mProcessedCount++;
                        this.mList.notifyAll();
                    }
                } catch (InterruptedException unused) {
                    return;
                }
            }
        }

        public boolean push(Runnable runnable) {
            synchronized (this.mList) {
                if (this.mQuitting) {
                    return false;
                }
                this.mList.add(runnable);
                this.mPushCount++;
                this.mList.notifyAll();
                return true;
            }
        }

        public void flush() {
            try {
                synchronized (this.mList) {
                    int i = this.mPushCount;
                    while (this.mProcessedCount != i) {
                        this.mList.wait();
                    }
                }
            } catch (InterruptedException unused) {
            }
        }

        public void quit() {
            synchronized (this.mList) {
                this.mQuitting = true;
                this.mList.notifyAll();
            }
        }

        public final Runnable pop() throws InterruptedException {
            synchronized (this.mList) {
                while (this.mList.isEmpty() && !this.mQuitting) {
                    this.mList.wait();
                }
                if (this.mList.isEmpty() && this.mQuitting) {
                    return null;
                }
                return this.mList.remove();
            }
        }
    }

    public static void notifyAbort(int i, LoadedModel loadedModel) {
        int i2 = loadedModel.type;
        if (i2 == 0) {
            loadedModel.callback.phraseRecognitionCallback(i, AidlUtil.newAbortPhraseEvent());
        } else if (i2 != 1) {
        } else {
            loadedModel.callback.recognitionCallback(i, AidlUtil.newAbortEvent());
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void detach() {
        this.mDelegate.detach();
        this.mNotifier.unregisterListener(this);
        this.mCallbackThread.quit();
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void reboot() {
        this.mDelegate.reboot();
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public Properties getProperties() {
        return this.mDelegate.getProperties();
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void forceRecognitionEvent(int i) {
        this.mDelegate.forceRecognitionEvent(i);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int getModelParameter(int i, int i2) {
        return this.mDelegate.getModelParameter(i, i2);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void setModelParameter(int i, int i2, int i3) {
        this.mDelegate.setModelParameter(i, i2, i3);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public ModelParameterRange queryParameter(int i, int i2) {
        return this.mDelegate.queryParameter(i, i2);
    }
}
