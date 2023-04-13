package com.android.server.soundtrigger_middleware;

import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.Properties;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.SoundModel;
import android.os.IBinder;
import android.util.Log;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.soundtrigger_middleware.ISoundTriggerHal;
import com.android.server.soundtrigger_middleware.SoundTriggerHalWatchdog;
import com.android.server.soundtrigger_middleware.UptimeTimer;
import java.util.Objects;
/* loaded from: classes2.dex */
public class SoundTriggerHalWatchdog implements ISoundTriggerHal {
    public final UptimeTimer mTimer;
    public final ISoundTriggerHal mUnderlying;

    public SoundTriggerHalWatchdog(ISoundTriggerHal iSoundTriggerHal) {
        Objects.requireNonNull(iSoundTriggerHal);
        this.mUnderlying = iSoundTriggerHal;
        this.mTimer = new UptimeTimer("SoundTriggerHalWatchdog");
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public Properties getProperties() {
        Watchdog watchdog = new Watchdog();
        try {
            Properties properties = this.mUnderlying.getProperties();
            watchdog.close();
            return properties;
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void registerCallback(ISoundTriggerHal.GlobalCallback globalCallback) {
        Watchdog watchdog = new Watchdog();
        try {
            this.mUnderlying.registerCallback(globalCallback);
            watchdog.close();
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int loadSoundModel(SoundModel soundModel, ISoundTriggerHal.ModelCallback modelCallback) {
        Watchdog watchdog = new Watchdog();
        try {
            int loadSoundModel = this.mUnderlying.loadSoundModel(soundModel, modelCallback);
            watchdog.close();
            return loadSoundModel;
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int loadPhraseSoundModel(PhraseSoundModel phraseSoundModel, ISoundTriggerHal.ModelCallback modelCallback) {
        Watchdog watchdog = new Watchdog();
        try {
            int loadPhraseSoundModel = this.mUnderlying.loadPhraseSoundModel(phraseSoundModel, modelCallback);
            watchdog.close();
            return loadPhraseSoundModel;
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void unloadSoundModel(int i) {
        Watchdog watchdog = new Watchdog();
        try {
            this.mUnderlying.unloadSoundModel(i);
            watchdog.close();
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void stopRecognition(int i) {
        Watchdog watchdog = new Watchdog();
        try {
            this.mUnderlying.stopRecognition(i);
            watchdog.close();
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void startRecognition(int i, int i2, int i3, RecognitionConfig recognitionConfig) {
        Watchdog watchdog = new Watchdog();
        try {
            this.mUnderlying.startRecognition(i, i2, i3, recognitionConfig);
            watchdog.close();
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void forceRecognitionEvent(int i) {
        Watchdog watchdog = new Watchdog();
        try {
            this.mUnderlying.forceRecognitionEvent(i);
            watchdog.close();
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int getModelParameter(int i, int i2) {
        Watchdog watchdog = new Watchdog();
        try {
            int modelParameter = this.mUnderlying.getModelParameter(i, i2);
            watchdog.close();
            return modelParameter;
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void setModelParameter(int i, int i2, int i3) {
        Watchdog watchdog = new Watchdog();
        try {
            this.mUnderlying.setModelParameter(i, i2, i3);
            watchdog.close();
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public ModelParameterRange queryParameter(int i, int i2) {
        Watchdog watchdog = new Watchdog();
        try {
            ModelParameterRange queryParameter = this.mUnderlying.queryParameter(i, i2);
            watchdog.close();
            return queryParameter;
        } catch (Throwable th) {
            try {
                watchdog.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void linkToDeath(IBinder.DeathRecipient deathRecipient) {
        this.mUnderlying.linkToDeath(deathRecipient);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void reboot() {
        this.mUnderlying.reboot();
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void detach() {
        this.mUnderlying.detach();
        this.mTimer.quit();
    }

    /* loaded from: classes2.dex */
    public class Watchdog implements AutoCloseable {
        public final Exception mException = new Exception();
        public final UptimeTimer.Task mTask;

        public Watchdog() {
            this.mTask = SoundTriggerHalWatchdog.this.mTimer.createTask(new Runnable() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHalWatchdog$Watchdog$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SoundTriggerHalWatchdog.Watchdog.this.lambda$new$0();
                }
            }, BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            Log.e("SoundTriggerHalWatchdog", "HAL deadline expired. Rebooting.", this.mException);
            SoundTriggerHalWatchdog.this.reboot();
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            this.mTask.cancel();
        }
    }
}
