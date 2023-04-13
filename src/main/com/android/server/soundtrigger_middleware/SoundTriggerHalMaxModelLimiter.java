package com.android.server.soundtrigger_middleware;

import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.Properties;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.SoundModel;
import android.os.IBinder;
import com.android.server.soundtrigger_middleware.ISoundTriggerHal;
/* loaded from: classes2.dex */
public class SoundTriggerHalMaxModelLimiter implements ISoundTriggerHal {
    public final ISoundTriggerHal mDelegate;
    public ISoundTriggerHal.GlobalCallback mGlobalCallback;
    public final int mMaxModels;
    public int mNumLoadedModels = 0;

    public SoundTriggerHalMaxModelLimiter(ISoundTriggerHal iSoundTriggerHal, int i) {
        this.mDelegate = iSoundTriggerHal;
        this.mMaxModels = i;
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void reboot() {
        this.mDelegate.reboot();
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void detach() {
        this.mDelegate.detach();
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public Properties getProperties() {
        return this.mDelegate.getProperties();
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void registerCallback(ISoundTriggerHal.GlobalCallback globalCallback) {
        this.mGlobalCallback = globalCallback;
        this.mDelegate.registerCallback(globalCallback);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int loadSoundModel(SoundModel soundModel, ISoundTriggerHal.ModelCallback modelCallback) {
        int loadSoundModel;
        synchronized (this) {
            if (this.mNumLoadedModels == this.mMaxModels) {
                throw new RecoverableException(1);
            }
            loadSoundModel = this.mDelegate.loadSoundModel(soundModel, modelCallback);
            this.mNumLoadedModels++;
        }
        return loadSoundModel;
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int loadPhraseSoundModel(PhraseSoundModel phraseSoundModel, ISoundTriggerHal.ModelCallback modelCallback) {
        int loadPhraseSoundModel;
        synchronized (this) {
            if (this.mNumLoadedModels == this.mMaxModels) {
                throw new RecoverableException(1);
            }
            loadPhraseSoundModel = this.mDelegate.loadPhraseSoundModel(phraseSoundModel, modelCallback);
            this.mNumLoadedModels++;
        }
        return loadPhraseSoundModel;
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void unloadSoundModel(int i) {
        boolean z;
        synchronized (this) {
            int i2 = this.mNumLoadedModels;
            this.mNumLoadedModels = i2 - 1;
            z = i2 == this.mMaxModels;
        }
        try {
            this.mDelegate.unloadSoundModel(i);
            if (z) {
                this.mGlobalCallback.onResourcesAvailable();
            }
        } catch (Exception e) {
            synchronized (this) {
                this.mNumLoadedModels++;
                throw e;
            }
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void stopRecognition(int i) {
        this.mDelegate.stopRecognition(i);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void startRecognition(int i, int i2, int i3, RecognitionConfig recognitionConfig) {
        this.mDelegate.startRecognition(i, i2, i3, recognitionConfig);
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

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void linkToDeath(IBinder.DeathRecipient deathRecipient) {
        this.mDelegate.linkToDeath(deathRecipient);
    }
}
