package com.android.server.soundtrigger_middleware;

import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.PhraseRecognitionEvent;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.Properties;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.RecognitionEvent;
import android.media.soundtrigger.SoundModel;
import android.media.soundtrigger_middleware.ISoundTriggerCallback;
import android.media.soundtrigger_middleware.ISoundTriggerModule;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.soundtrigger_middleware.ISoundTriggerHal;
import com.android.server.soundtrigger_middleware.SoundTriggerMiddlewareImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes2.dex */
public class SoundTriggerModule implements IBinder.DeathRecipient, ISoundTriggerHal.GlobalCallback {
    public final Set<Session> mActiveSessions = new HashSet();
    public final SoundTriggerMiddlewareImpl.AudioSessionProvider mAudioSessionProvider;
    public final HalFactory mHalFactory;
    public ISoundTriggerHal mHalService;
    public Properties mProperties;

    /* loaded from: classes2.dex */
    public enum ModelState {
        INIT,
        LOADED,
        ACTIVE
    }

    public SoundTriggerModule(HalFactory halFactory, SoundTriggerMiddlewareImpl.AudioSessionProvider audioSessionProvider) {
        this.mHalFactory = halFactory;
        this.mAudioSessionProvider = audioSessionProvider;
        attachToHal();
    }

    public synchronized ISoundTriggerModule attach(ISoundTriggerCallback iSoundTriggerCallback) {
        Session session;
        session = new Session(iSoundTriggerCallback);
        this.mActiveSessions.add(session);
        return session;
    }

    public synchronized Properties getProperties() {
        return this.mProperties;
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        ArrayList<ISoundTriggerCallback> arrayList;
        Log.w("SoundTriggerModule", "Underlying HAL driver died.");
        synchronized (this) {
            arrayList = new ArrayList(this.mActiveSessions.size());
            for (Session session : this.mActiveSessions) {
                arrayList.add(session.moduleDied());
            }
            this.mActiveSessions.clear();
            reset();
        }
        for (ISoundTriggerCallback iSoundTriggerCallback : arrayList) {
            try {
                iSoundTriggerCallback.onModuleDied();
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
    }

    public final void reset() {
        this.mHalService.detach();
        attachToHal();
    }

    public final void attachToHal() {
        SoundTriggerHalEnforcer soundTriggerHalEnforcer = new SoundTriggerHalEnforcer(new SoundTriggerHalWatchdog(this.mHalFactory.create()));
        this.mHalService = soundTriggerHalEnforcer;
        soundTriggerHalEnforcer.linkToDeath(this);
        this.mHalService.registerCallback(this);
        this.mProperties = this.mHalService.getProperties();
    }

    public final void removeSession(Session session) {
        this.mActiveSessions.remove(session);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal.GlobalCallback
    public void onResourcesAvailable() {
        ArrayList<ISoundTriggerCallback> arrayList;
        synchronized (this) {
            arrayList = new ArrayList(this.mActiveSessions.size());
            for (Session session : this.mActiveSessions) {
                arrayList.add(session.mCallback);
            }
        }
        for (ISoundTriggerCallback iSoundTriggerCallback : arrayList) {
            try {
                iSoundTriggerCallback.onResourcesAvailable();
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
    }

    /* loaded from: classes2.dex */
    public class Session implements ISoundTriggerModule {
        public ISoundTriggerCallback mCallback;
        public final Map<Integer, Model> mLoadedModels;

        public Session(ISoundTriggerCallback iSoundTriggerCallback) {
            this.mLoadedModels = new HashMap();
            this.mCallback = iSoundTriggerCallback;
        }

        public void detach() {
            synchronized (SoundTriggerModule.this) {
                if (this.mCallback == null) {
                    return;
                }
                SoundTriggerModule.this.removeSession(this);
                this.mCallback = null;
            }
        }

        public int loadModel(SoundModel soundModel) {
            int load;
            synchronized (SoundTriggerModule.this) {
                SoundTriggerMiddlewareImpl.AudioSessionProvider.AudioSession acquireSession = SoundTriggerModule.this.mAudioSessionProvider.acquireSession();
                try {
                    checkValid();
                    load = new Model().load(soundModel, acquireSession);
                } catch (Exception e) {
                    try {
                        SoundTriggerModule.this.mAudioSessionProvider.releaseSession(acquireSession.mSessionHandle);
                    } catch (Exception e2) {
                        Log.e("SoundTriggerModule", "Failed to release session.", e2);
                    }
                    throw e;
                }
            }
            return load;
        }

        public int loadPhraseModel(PhraseSoundModel phraseSoundModel) {
            int load;
            synchronized (SoundTriggerModule.this) {
                SoundTriggerMiddlewareImpl.AudioSessionProvider.AudioSession acquireSession = SoundTriggerModule.this.mAudioSessionProvider.acquireSession();
                try {
                    checkValid();
                    load = new Model().load(phraseSoundModel, acquireSession);
                    Log.d("SoundTriggerModule", String.format("loadPhraseModel()->%d", Integer.valueOf(load)));
                } catch (Exception e) {
                    try {
                        SoundTriggerModule.this.mAudioSessionProvider.releaseSession(acquireSession.mSessionHandle);
                    } catch (Exception e2) {
                        Log.e("SoundTriggerModule", "Failed to release session.", e2);
                    }
                    throw e;
                }
            }
            return load;
        }

        public void unloadModel(int i) {
            synchronized (SoundTriggerModule.this) {
                checkValid();
                SoundTriggerModule.this.mAudioSessionProvider.releaseSession(this.mLoadedModels.get(Integer.valueOf(i)).unload());
            }
        }

        public void startRecognition(int i, RecognitionConfig recognitionConfig) {
            synchronized (SoundTriggerModule.this) {
                checkValid();
                this.mLoadedModels.get(Integer.valueOf(i)).startRecognition(recognitionConfig);
            }
        }

        public void stopRecognition(int i) {
            Model model;
            synchronized (SoundTriggerModule.this) {
                checkValid();
                model = this.mLoadedModels.get(Integer.valueOf(i));
            }
            model.stopRecognition();
        }

        public void forceRecognitionEvent(int i) {
            synchronized (SoundTriggerModule.this) {
                checkValid();
                this.mLoadedModels.get(Integer.valueOf(i)).forceRecognitionEvent();
            }
        }

        public void setModelParameter(int i, int i2, int i3) {
            synchronized (SoundTriggerModule.this) {
                checkValid();
                this.mLoadedModels.get(Integer.valueOf(i)).setParameter(i2, i3);
            }
        }

        public int getModelParameter(int i, int i2) {
            int parameter;
            synchronized (SoundTriggerModule.this) {
                checkValid();
                parameter = this.mLoadedModels.get(Integer.valueOf(i)).getParameter(i2);
            }
            return parameter;
        }

        public ModelParameterRange queryModelParameterSupport(int i, int i2) {
            ModelParameterRange queryModelParameterSupport;
            synchronized (SoundTriggerModule.this) {
                checkValid();
                queryModelParameterSupport = this.mLoadedModels.get(Integer.valueOf(i)).queryModelParameterSupport(i2);
            }
            return queryModelParameterSupport;
        }

        public final ISoundTriggerCallback moduleDied() {
            ISoundTriggerCallback iSoundTriggerCallback = this.mCallback;
            this.mCallback = null;
            return iSoundTriggerCallback;
        }

        public final void checkValid() {
            if (this.mCallback == null) {
                throw new RecoverableException(4);
            }
        }

        public IBinder asBinder() {
            throw new UnsupportedOperationException("This implementation is not intended to be used directly with Binder.");
        }

        /* loaded from: classes2.dex */
        public class Model implements ISoundTriggerHal.ModelCallback {
            public int mHandle;
            public SoundTriggerMiddlewareImpl.AudioSessionProvider.AudioSession mSession;
            public ModelState mState;
            public int mType;

            public Model() {
                this.mState = ModelState.INIT;
                this.mType = -1;
            }

            public final ModelState getState() {
                return this.mState;
            }

            public final void setState(ModelState modelState) {
                this.mState = modelState;
                SoundTriggerModule.this.notifyAll();
            }

            public final int load(SoundModel soundModel, SoundTriggerMiddlewareImpl.AudioSessionProvider.AudioSession audioSession) {
                this.mSession = audioSession;
                this.mHandle = SoundTriggerModule.this.mHalService.loadSoundModel(soundModel, this);
                this.mType = 1;
                setState(ModelState.LOADED);
                Session.this.mLoadedModels.put(Integer.valueOf(this.mHandle), this);
                return this.mHandle;
            }

            public final int load(PhraseSoundModel phraseSoundModel, SoundTriggerMiddlewareImpl.AudioSessionProvider.AudioSession audioSession) {
                this.mSession = audioSession;
                this.mHandle = SoundTriggerModule.this.mHalService.loadPhraseSoundModel(phraseSoundModel, this);
                this.mType = 0;
                setState(ModelState.LOADED);
                Session.this.mLoadedModels.put(Integer.valueOf(this.mHandle), this);
                return this.mHandle;
            }

            public final int unload() {
                SoundTriggerModule.this.mHalService.unloadSoundModel(this.mHandle);
                Session.this.mLoadedModels.remove(Integer.valueOf(this.mHandle));
                return this.mSession.mSessionHandle;
            }

            public final void startRecognition(RecognitionConfig recognitionConfig) {
                ISoundTriggerHal iSoundTriggerHal = SoundTriggerModule.this.mHalService;
                int i = this.mHandle;
                SoundTriggerMiddlewareImpl.AudioSessionProvider.AudioSession audioSession = this.mSession;
                iSoundTriggerHal.startRecognition(i, audioSession.mDeviceHandle, audioSession.mIoHandle, recognitionConfig);
                setState(ModelState.ACTIVE);
            }

            public final void stopRecognition() {
                synchronized (SoundTriggerModule.this) {
                    if (getState() == ModelState.LOADED) {
                        return;
                    }
                    SoundTriggerModule.this.mHalService.stopRecognition(this.mHandle);
                    synchronized (SoundTriggerModule.this) {
                        if (getState() == ModelState.ACTIVE) {
                            if (Session.this.mCallback != null) {
                                try {
                                    int i = this.mType;
                                    if (i == 0) {
                                        Session.this.mCallback.onPhraseRecognition(this.mHandle, AidlUtil.newAbortPhraseEvent(), this.mSession.mSessionHandle);
                                    } else if (i == 1) {
                                        Session.this.mCallback.onRecognition(this.mHandle, AidlUtil.newAbortEvent(), this.mSession.mSessionHandle);
                                    } else {
                                        throw new RuntimeException("Unexpected model type: " + this.mType);
                                    }
                                } catch (RemoteException unused) {
                                }
                            }
                            setState(ModelState.LOADED);
                        }
                    }
                }
            }

            public final void forceRecognitionEvent() {
                if (getState() != ModelState.ACTIVE) {
                    return;
                }
                SoundTriggerModule.this.mHalService.forceRecognitionEvent(this.mHandle);
            }

            public final void setParameter(int i, int i2) {
                SoundTriggerModule.this.mHalService.setModelParameter(this.mHandle, ConversionUtil.aidl2hidlModelParameter(i), i2);
            }

            public final int getParameter(int i) {
                return SoundTriggerModule.this.mHalService.getModelParameter(this.mHandle, ConversionUtil.aidl2hidlModelParameter(i));
            }

            public final ModelParameterRange queryModelParameterSupport(int i) {
                return SoundTriggerModule.this.mHalService.queryParameter(this.mHandle, i);
            }

            @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal.ModelCallback
            public void recognitionCallback(int i, RecognitionEvent recognitionEvent) {
                ISoundTriggerCallback iSoundTriggerCallback;
                synchronized (SoundTriggerModule.this) {
                    if (!recognitionEvent.recognitionStillActive) {
                        setState(ModelState.LOADED);
                    }
                    iSoundTriggerCallback = Session.this.mCallback;
                }
                if (iSoundTriggerCallback != null) {
                    try {
                        iSoundTriggerCallback.onRecognition(this.mHandle, recognitionEvent, this.mSession.mSessionHandle);
                    } catch (RemoteException e) {
                        throw e.rethrowAsRuntimeException();
                    }
                }
            }

            @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal.ModelCallback
            public void phraseRecognitionCallback(int i, PhraseRecognitionEvent phraseRecognitionEvent) {
                ISoundTriggerCallback iSoundTriggerCallback;
                synchronized (SoundTriggerModule.this) {
                    if (!phraseRecognitionEvent.common.recognitionStillActive) {
                        setState(ModelState.LOADED);
                    }
                    iSoundTriggerCallback = Session.this.mCallback;
                }
                if (iSoundTriggerCallback != null) {
                    try {
                        Session.this.mCallback.onPhraseRecognition(this.mHandle, phraseRecognitionEvent, this.mSession.mSessionHandle);
                    } catch (RemoteException e) {
                        throw e.rethrowAsRuntimeException();
                    }
                }
            }

            @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal.ModelCallback
            public void modelUnloaded(int i) {
                ISoundTriggerCallback iSoundTriggerCallback;
                synchronized (SoundTriggerModule.this) {
                    iSoundTriggerCallback = Session.this.mCallback;
                }
                if (iSoundTriggerCallback != null) {
                    try {
                        iSoundTriggerCallback.onModelUnloaded(i);
                    } catch (RemoteException e) {
                        throw e.rethrowAsRuntimeException();
                    }
                }
            }
        }
    }
}
