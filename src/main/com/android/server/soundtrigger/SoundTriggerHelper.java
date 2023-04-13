package com.android.server.soundtrigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.ModelParams;
import android.hardware.soundtrigger.SoundTrigger;
import android.hardware.soundtrigger.SoundTriggerModule;
import android.os.Binder;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class SoundTriggerHelper implements SoundTrigger.StatusListener {
    public final Context mContext;
    public final Handler mHandler;
    public SoundTriggerModule mModule;
    public final int mModuleId;
    public final Supplier<List<SoundTrigger.ModuleProperties>> mModulePropertiesProvider;
    public final Function<SoundTrigger.StatusListener, SoundTriggerModule> mModuleProvider;
    public final PhoneStateListener mPhoneStateListener;
    public final PowerManager mPowerManager;
    public PowerSaveModeListener mPowerSaveModeListener;
    public final TelephonyManager mTelephonyManager;
    public final Object mLock = new Object();
    public boolean mCallActive = false;
    public int mSoundTriggerPowerSaveMode = 0;
    public boolean mRecognitionRequested = false;
    public final HashMap<UUID, ModelData> mModelDataMap = new HashMap<>();
    public HashMap<Integer, UUID> mKeyphraseUuidMap = new HashMap<>();

    public SoundTriggerHelper(Context context, Function<SoundTrigger.StatusListener, SoundTriggerModule> function, int i, Supplier<List<SoundTrigger.ModuleProperties>> supplier) {
        this.mModuleId = i;
        this.mContext = context;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        this.mModuleProvider = function;
        this.mModulePropertiesProvider = supplier;
        if (i == -1) {
            this.mModule = null;
        } else {
            this.mModule = function.apply(this);
        }
        Looper myLooper = Looper.myLooper();
        myLooper = myLooper == null ? Looper.getMainLooper() : myLooper;
        this.mPhoneStateListener = new MyCallStateListener(myLooper);
        if (myLooper != null) {
            this.mHandler = new Handler(myLooper) { // from class: com.android.server.soundtrigger.SoundTriggerHelper.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    if (message.what == 0) {
                        synchronized (SoundTriggerHelper.this.mLock) {
                            SoundTriggerHelper.this.onCallStateChangedLocked(2 == message.arg1);
                        }
                        return;
                    }
                    Slog.e("SoundTriggerHelper", "unknown message in handler:" + message.what);
                }
            };
        } else {
            this.mHandler = null;
        }
    }

    public int startGenericRecognition(UUID uuid, SoundTrigger.GenericSoundModel genericSoundModel, IRecognitionStatusCallback iRecognitionStatusCallback, SoundTrigger.RecognitionConfig recognitionConfig, boolean z) {
        MetricsLogger.count(this.mContext, "sth_start_recognition", 1);
        if (uuid == null || genericSoundModel == null || iRecognitionStatusCallback == null || recognitionConfig == null) {
            Slog.w("SoundTriggerHelper", "Passed in bad data to startGenericRecognition().");
            return Integer.MIN_VALUE;
        }
        synchronized (this.mLock) {
            ModelData orCreateGenericModelDataLocked = getOrCreateGenericModelDataLocked(uuid);
            if (orCreateGenericModelDataLocked == null) {
                Slog.w("SoundTriggerHelper", "Irrecoverable error occurred, check UUID / sound model data.");
                return Integer.MIN_VALUE;
            }
            return startRecognition(genericSoundModel, orCreateGenericModelDataLocked, iRecognitionStatusCallback, recognitionConfig, Integer.MIN_VALUE, z);
        }
    }

    public int startKeyphraseRecognition(int i, SoundTrigger.KeyphraseSoundModel keyphraseSoundModel, IRecognitionStatusCallback iRecognitionStatusCallback, SoundTrigger.RecognitionConfig recognitionConfig, boolean z) {
        synchronized (this.mLock) {
            MetricsLogger.count(this.mContext, "sth_start_recognition", 1);
            if (keyphraseSoundModel != null && iRecognitionStatusCallback != null && recognitionConfig != null) {
                ModelData keyphraseModelDataLocked = getKeyphraseModelDataLocked(i);
                if (keyphraseModelDataLocked != null && !keyphraseModelDataLocked.isKeyphraseModel()) {
                    Slog.e("SoundTriggerHelper", "Generic model with same UUID exists.");
                    return Integer.MIN_VALUE;
                }
                if (keyphraseModelDataLocked != null && !keyphraseModelDataLocked.getModelId().equals(keyphraseSoundModel.getUuid())) {
                    int cleanUpExistingKeyphraseModelLocked = cleanUpExistingKeyphraseModelLocked(keyphraseModelDataLocked);
                    if (cleanUpExistingKeyphraseModelLocked != 0) {
                        return cleanUpExistingKeyphraseModelLocked;
                    }
                    removeKeyphraseModelLocked(i);
                    keyphraseModelDataLocked = null;
                }
                if (keyphraseModelDataLocked == null) {
                    keyphraseModelDataLocked = createKeyphraseModelDataLocked(keyphraseSoundModel.getUuid(), i);
                }
                return startRecognition(keyphraseSoundModel, keyphraseModelDataLocked, iRecognitionStatusCallback, recognitionConfig, i, z);
            }
            return Integer.MIN_VALUE;
        }
    }

    public final int cleanUpExistingKeyphraseModelLocked(ModelData modelData) {
        int tryStopAndUnloadLocked = tryStopAndUnloadLocked(modelData, true, true);
        if (tryStopAndUnloadLocked != 0) {
            Slog.w("SoundTriggerHelper", "Unable to stop or unload previous model: " + modelData.toString());
        }
        return tryStopAndUnloadLocked;
    }

    public final int prepareForRecognition(ModelData modelData) {
        if (this.mModule == null) {
            Slog.w("SoundTriggerHelper", "prepareForRecognition: cannot attach to sound trigger module");
            return Integer.MIN_VALUE;
        }
        if (!modelData.isModelLoaded()) {
            stopAndUnloadDeadModelsLocked();
            int[] iArr = {0};
            int loadSoundModel = this.mModule.loadSoundModel(modelData.getSoundModel(), iArr);
            if (loadSoundModel != 0) {
                Slog.w("SoundTriggerHelper", "prepareForRecognition: loadSoundModel failed with status: " + loadSoundModel);
                return loadSoundModel;
            }
            modelData.setHandle(iArr[0]);
            modelData.setLoaded();
        }
        return 0;
    }

    public int startRecognition(SoundTrigger.SoundModel soundModel, ModelData modelData, IRecognitionStatusCallback iRecognitionStatusCallback, SoundTrigger.RecognitionConfig recognitionConfig, int i, boolean z) {
        boolean z2;
        boolean z3;
        int tryStopAndUnloadLocked;
        synchronized (this.mLock) {
            IRecognitionStatusCallback callback = modelData.getCallback();
            if (callback != null && callback.asBinder() != iRecognitionStatusCallback.asBinder()) {
                Slog.w("SoundTriggerHelper", "Canceling previous recognition for model id: " + modelData.getModelId());
                try {
                    callback.onError(Integer.MIN_VALUE);
                } catch (RemoteException e) {
                    Slog.w("SoundTriggerHelper", "RemoteException in onDetectionStopped", e);
                }
                modelData.clearCallback();
            }
            if (modelData.getSoundModel() != null) {
                if (modelData.getSoundModel().equals(soundModel) && modelData.isModelStarted()) {
                    z2 = true;
                    z3 = false;
                } else if (modelData.getSoundModel().equals(soundModel)) {
                    z2 = false;
                    z3 = false;
                } else {
                    z2 = modelData.isModelStarted();
                    z3 = modelData.isModelLoaded();
                }
                if ((z2 || z3) && (tryStopAndUnloadLocked = tryStopAndUnloadLocked(modelData, z2, z3)) != 0) {
                    Slog.w("SoundTriggerHelper", "Unable to stop or unload previous model: " + modelData.toString());
                    return tryStopAndUnloadLocked;
                }
            }
            modelData.setCallback(iRecognitionStatusCallback);
            modelData.setRequested(true);
            modelData.setRecognitionConfig(recognitionConfig);
            modelData.setRunInBatterySaverMode(z);
            modelData.setSoundModel(soundModel);
            if (!isRecognitionAllowedByDeviceState(modelData)) {
                initializeDeviceStateListeners();
                return 0;
            }
            return updateRecognitionLocked(modelData, false);
        }
    }

    public int stopGenericRecognition(UUID uuid, IRecognitionStatusCallback iRecognitionStatusCallback) {
        synchronized (this.mLock) {
            MetricsLogger.count(this.mContext, "sth_stop_recognition", 1);
            if (iRecognitionStatusCallback != null && uuid != null) {
                ModelData modelData = this.mModelDataMap.get(uuid);
                if (modelData != null && modelData.isGenericModel()) {
                    int stopRecognition = stopRecognition(modelData, iRecognitionStatusCallback);
                    if (stopRecognition != 0) {
                        Slog.w("SoundTriggerHelper", "stopGenericRecognition failed: " + stopRecognition);
                    }
                    return stopRecognition;
                }
                Slog.w("SoundTriggerHelper", "Attempting stopRecognition on invalid model with id:" + uuid);
                return Integer.MIN_VALUE;
            }
            Slog.e("SoundTriggerHelper", "Null callbackreceived for stopGenericRecognition() for modelid:" + uuid);
            return Integer.MIN_VALUE;
        }
    }

    public int stopKeyphraseRecognition(int i, IRecognitionStatusCallback iRecognitionStatusCallback) {
        synchronized (this.mLock) {
            MetricsLogger.count(this.mContext, "sth_stop_recognition", 1);
            if (iRecognitionStatusCallback == null) {
                Slog.e("SoundTriggerHelper", "Null callback received for stopKeyphraseRecognition() for keyphraseId:" + i);
                return Integer.MIN_VALUE;
            }
            ModelData keyphraseModelDataLocked = getKeyphraseModelDataLocked(i);
            if (keyphraseModelDataLocked != null && keyphraseModelDataLocked.isKeyphraseModel()) {
                int stopRecognition = stopRecognition(keyphraseModelDataLocked, iRecognitionStatusCallback);
                return stopRecognition != 0 ? stopRecognition : stopRecognition;
            }
            Slog.w("SoundTriggerHelper", "No model exists for given keyphrase Id " + i);
            return Integer.MIN_VALUE;
        }
    }

    public final int stopRecognition(ModelData modelData, IRecognitionStatusCallback iRecognitionStatusCallback) {
        synchronized (this.mLock) {
            if (iRecognitionStatusCallback == null) {
                return Integer.MIN_VALUE;
            }
            if (this.mModule == null) {
                Slog.w("SoundTriggerHelper", "Attempting stopRecognition after detach");
                return Integer.MIN_VALUE;
            }
            IRecognitionStatusCallback callback = modelData.getCallback();
            if (callback != null && (modelData.isRequested() || modelData.isModelStarted())) {
                if (callback.asBinder() != iRecognitionStatusCallback.asBinder()) {
                    Slog.w("SoundTriggerHelper", "Attempting stopRecognition for another recognition");
                    return Integer.MIN_VALUE;
                }
                modelData.setRequested(false);
                int updateRecognitionLocked = updateRecognitionLocked(modelData, false);
                if (updateRecognitionLocked != 0) {
                    return updateRecognitionLocked;
                }
                modelData.setLoaded();
                modelData.clearCallback();
                modelData.setRecognitionConfig(null);
                if (!computeRecognitionRequestedLocked()) {
                    internalClearGlobalStateLocked();
                }
                return updateRecognitionLocked;
            }
            Slog.w("SoundTriggerHelper", "Attempting stopRecognition without a successful startRecognition");
            return Integer.MIN_VALUE;
        }
    }

    public final int tryStopAndUnloadLocked(ModelData modelData, boolean z, boolean z2) {
        int i = 0;
        if (modelData.isModelNotLoaded()) {
            return 0;
        }
        if (z && modelData.isModelStarted() && (i = stopRecognitionLocked(modelData, false)) != 0) {
            Slog.w("SoundTriggerHelper", "stopRecognition failed: " + i);
            return i;
        }
        if (z2 && (modelData.isModelLoaded() || modelData.isStopPending())) {
            Slog.d("SoundTriggerHelper", "Unloading previously loaded stale model.");
            SoundTriggerModule soundTriggerModule = this.mModule;
            if (soundTriggerModule == null) {
                return Integer.MIN_VALUE;
            }
            i = soundTriggerModule.unloadSoundModel(modelData.getHandle());
            MetricsLogger.count(this.mContext, "sth_unloading_stale_model", 1);
            if (i != 0) {
                Slog.w("SoundTriggerHelper", "unloadSoundModel call failed with " + i);
            } else {
                modelData.clearState();
            }
        }
        return i;
    }

    public SoundTrigger.ModuleProperties getModuleProperties() {
        for (SoundTrigger.ModuleProperties moduleProperties : this.mModulePropertiesProvider.get()) {
            if (moduleProperties.getId() == this.mModuleId) {
                return moduleProperties;
            }
        }
        Slog.e("SoundTriggerHelper", "Module properties not found for existing moduleId " + this.mModuleId);
        return null;
    }

    public int unloadKeyphraseSoundModel(int i) {
        synchronized (this.mLock) {
            MetricsLogger.count(this.mContext, "sth_unload_keyphrase_sound_model", 1);
            ModelData keyphraseModelDataLocked = getKeyphraseModelDataLocked(i);
            if (this.mModule != null && keyphraseModelDataLocked != null && keyphraseModelDataLocked.isModelLoaded() && keyphraseModelDataLocked.isKeyphraseModel()) {
                keyphraseModelDataLocked.setRequested(false);
                int updateRecognitionLocked = updateRecognitionLocked(keyphraseModelDataLocked, false);
                if (updateRecognitionLocked != 0) {
                    Slog.w("SoundTriggerHelper", "Stop recognition failed for keyphrase ID:" + updateRecognitionLocked);
                }
                int unloadSoundModel = this.mModule.unloadSoundModel(keyphraseModelDataLocked.getHandle());
                if (unloadSoundModel != 0) {
                    Slog.w("SoundTriggerHelper", "unloadKeyphraseSoundModel call failed with " + unloadSoundModel);
                }
                removeKeyphraseModelLocked(i);
                return unloadSoundModel;
            }
            return Integer.MIN_VALUE;
        }
    }

    public int unloadGenericSoundModel(UUID uuid) {
        int stopRecognitionLocked;
        synchronized (this.mLock) {
            MetricsLogger.count(this.mContext, "sth_unload_generic_sound_model", 1);
            if (uuid != null && this.mModule != null) {
                ModelData modelData = this.mModelDataMap.get(uuid);
                if (modelData != null && modelData.isGenericModel()) {
                    if (!modelData.isModelLoaded()) {
                        Slog.i("SoundTriggerHelper", "Unload: Given generic model is not loaded:" + uuid);
                        return 0;
                    }
                    if (modelData.isModelStarted() && (stopRecognitionLocked = stopRecognitionLocked(modelData, false)) != 0) {
                        Slog.w("SoundTriggerHelper", "stopGenericRecognition failed: " + stopRecognitionLocked);
                    }
                    SoundTriggerModule soundTriggerModule = this.mModule;
                    if (soundTriggerModule == null) {
                        return Integer.MIN_VALUE;
                    }
                    int unloadSoundModel = soundTriggerModule.unloadSoundModel(modelData.getHandle());
                    if (unloadSoundModel != 0) {
                        Slog.w("SoundTriggerHelper", "unloadGenericSoundModel() call failed with " + unloadSoundModel);
                        Slog.w("SoundTriggerHelper", "unloadGenericSoundModel() force-marking model as unloaded.");
                    }
                    this.mModelDataMap.remove(uuid);
                    return unloadSoundModel;
                }
                Slog.w("SoundTriggerHelper", "Unload error: Attempting unload invalid generic model with id:" + uuid);
                return Integer.MIN_VALUE;
            }
            return Integer.MIN_VALUE;
        }
    }

    public boolean isRecognitionRequested(UUID uuid) {
        boolean z;
        synchronized (this.mLock) {
            ModelData modelData = this.mModelDataMap.get(uuid);
            z = modelData != null && modelData.isRequested();
        }
        return z;
    }

    public int getGenericModelState(UUID uuid) {
        synchronized (this.mLock) {
            MetricsLogger.count(this.mContext, "sth_get_generic_model_state", 1);
            if (uuid != null && this.mModule != null) {
                ModelData modelData = this.mModelDataMap.get(uuid);
                if (modelData != null && modelData.isGenericModel()) {
                    if (!modelData.isModelLoaded()) {
                        Slog.i("SoundTriggerHelper", "GetGenericModelState: Given generic model is not loaded:" + uuid);
                        return Integer.MIN_VALUE;
                    } else if (!modelData.isModelStarted()) {
                        Slog.i("SoundTriggerHelper", "GetGenericModelState: Given generic model is not started:" + uuid);
                        return Integer.MIN_VALUE;
                    } else {
                        return this.mModule.getModelState(modelData.getHandle());
                    }
                }
                Slog.w("SoundTriggerHelper", "GetGenericModelState error: Invalid generic model id:" + uuid);
                return Integer.MIN_VALUE;
            }
            return Integer.MIN_VALUE;
        }
    }

    public int setParameter(UUID uuid, @ModelParams int i, int i2) {
        int parameterLocked;
        synchronized (this.mLock) {
            parameterLocked = setParameterLocked(this.mModelDataMap.get(uuid), i, i2);
        }
        return parameterLocked;
    }

    public int setKeyphraseParameter(int i, @ModelParams int i2, int i3) {
        int parameterLocked;
        synchronized (this.mLock) {
            parameterLocked = setParameterLocked(getKeyphraseModelDataLocked(i), i2, i3);
        }
        return parameterLocked;
    }

    public final int setParameterLocked(ModelData modelData, @ModelParams int i, int i2) {
        MetricsLogger.count(this.mContext, "sth_set_parameter", 1);
        if (this.mModule == null) {
            return SoundTrigger.STATUS_NO_INIT;
        }
        if (modelData == null || !modelData.isModelLoaded()) {
            Slog.i("SoundTriggerHelper", "SetParameter: Given model is not loaded:" + modelData);
            return SoundTrigger.STATUS_BAD_VALUE;
        }
        return this.mModule.setParameter(modelData.getHandle(), i, i2);
    }

    public int getParameter(UUID uuid, @ModelParams int i) {
        int parameterLocked;
        synchronized (this.mLock) {
            parameterLocked = getParameterLocked(this.mModelDataMap.get(uuid), i);
        }
        return parameterLocked;
    }

    public int getKeyphraseParameter(int i, @ModelParams int i2) {
        int parameterLocked;
        synchronized (this.mLock) {
            parameterLocked = getParameterLocked(getKeyphraseModelDataLocked(i), i2);
        }
        return parameterLocked;
    }

    public final int getParameterLocked(ModelData modelData, @ModelParams int i) {
        MetricsLogger.count(this.mContext, "sth_get_parameter", 1);
        if (this.mModule != null) {
            if (modelData == null) {
                throw new IllegalArgumentException("Invalid model id");
            }
            if (!modelData.isModelLoaded()) {
                throw new UnsupportedOperationException("Given model is not loaded:" + modelData);
            }
            return this.mModule.getParameter(modelData.getHandle(), i);
        }
        throw new UnsupportedOperationException("SoundTriggerModule not initialized");
    }

    public SoundTrigger.ModelParamRange queryParameter(UUID uuid, @ModelParams int i) {
        SoundTrigger.ModelParamRange queryParameterLocked;
        synchronized (this.mLock) {
            queryParameterLocked = queryParameterLocked(this.mModelDataMap.get(uuid), i);
        }
        return queryParameterLocked;
    }

    public SoundTrigger.ModelParamRange queryKeyphraseParameter(int i, @ModelParams int i2) {
        SoundTrigger.ModelParamRange queryParameterLocked;
        synchronized (this.mLock) {
            queryParameterLocked = queryParameterLocked(getKeyphraseModelDataLocked(i), i2);
        }
        return queryParameterLocked;
    }

    public final SoundTrigger.ModelParamRange queryParameterLocked(ModelData modelData, @ModelParams int i) {
        MetricsLogger.count(this.mContext, "sth_query_parameter", 1);
        if (this.mModule == null) {
            return null;
        }
        if (modelData == null) {
            Slog.w("SoundTriggerHelper", "queryParameter: Invalid model id");
            return null;
        } else if (!modelData.isModelLoaded()) {
            Slog.i("SoundTriggerHelper", "queryParameter: Given model is not loaded:" + modelData);
            return null;
        } else {
            return this.mModule.queryParameter(modelData.getHandle(), i);
        }
    }

    public void onRecognition(SoundTrigger.RecognitionEvent recognitionEvent) {
        if (recognitionEvent == null) {
            Slog.w("SoundTriggerHelper", "Null recognition event!");
        } else if (!(recognitionEvent instanceof SoundTrigger.KeyphraseRecognitionEvent) && !(recognitionEvent instanceof SoundTrigger.GenericRecognitionEvent)) {
            Slog.w("SoundTriggerHelper", "Invalid recognition event type (not one of generic or keyphrase)!");
        } else {
            synchronized (this.mLock) {
                int i = recognitionEvent.status;
                if (i != 0) {
                    if (i == 1) {
                        onRecognitionAbortLocked(recognitionEvent);
                    } else if (i == 2) {
                        onRecognitionFailureLocked();
                    } else if (i != 3) {
                    }
                }
                if (isKeyphraseRecognitionEvent(recognitionEvent)) {
                    onKeyphraseRecognitionSuccessLocked((SoundTrigger.KeyphraseRecognitionEvent) recognitionEvent);
                } else {
                    onGenericRecognitionSuccessLocked((SoundTrigger.GenericRecognitionEvent) recognitionEvent);
                }
            }
        }
    }

    public final boolean isKeyphraseRecognitionEvent(SoundTrigger.RecognitionEvent recognitionEvent) {
        return recognitionEvent instanceof SoundTrigger.KeyphraseRecognitionEvent;
    }

    public final void onGenericRecognitionSuccessLocked(SoundTrigger.GenericRecognitionEvent genericRecognitionEvent) {
        MetricsLogger.count(this.mContext, "sth_generic_recognition_event", 1);
        int i = genericRecognitionEvent.status;
        if (i == 0 || i == 3) {
            ModelData modelDataForLocked = getModelDataForLocked(genericRecognitionEvent.soundModelHandle);
            if (modelDataForLocked == null || !modelDataForLocked.isGenericModel()) {
                Slog.w("SoundTriggerHelper", "Generic recognition event: Model does not exist for handle: " + genericRecognitionEvent.soundModelHandle);
                return;
            }
            IRecognitionStatusCallback callback = modelDataForLocked.getCallback();
            if (callback == null) {
                Slog.w("SoundTriggerHelper", "Generic recognition event: Null callback for model handle: " + genericRecognitionEvent.soundModelHandle);
                return;
            }
            if (!genericRecognitionEvent.recognitionStillActive) {
                modelDataForLocked.setStopped();
            }
            try {
                callback.onGenericSoundTriggerDetected(genericRecognitionEvent);
            } catch (DeadObjectException e) {
                forceStopAndUnloadModelLocked(modelDataForLocked, e);
                return;
            } catch (RemoteException e2) {
                Slog.w("SoundTriggerHelper", "RemoteException in onGenericSoundTriggerDetected", e2);
            }
            SoundTrigger.RecognitionConfig recognitionConfig = modelDataForLocked.getRecognitionConfig();
            if (recognitionConfig == null) {
                Slog.w("SoundTriggerHelper", "Generic recognition event: Null RecognitionConfig for model handle: " + genericRecognitionEvent.soundModelHandle);
                return;
            }
            modelDataForLocked.setRequested(recognitionConfig.allowMultipleTriggers);
            if (modelDataForLocked.isRequested()) {
                updateRecognitionLocked(modelDataForLocked, true);
            }
        }
    }

    public void onModelUnloaded(int i) {
        synchronized (this.mLock) {
            MetricsLogger.count(this.mContext, "sth_sound_model_updated", 1);
            onModelUnloadedLocked(i);
        }
    }

    public void onResourcesAvailable() {
        synchronized (this.mLock) {
            onResourcesAvailableLocked();
        }
    }

    public void onServiceDied() {
        Slog.e("SoundTriggerHelper", "onServiceDied!!");
        MetricsLogger.count(this.mContext, "sth_service_died", 1);
        synchronized (this.mLock) {
            onServiceDiedLocked();
        }
    }

    public final void onCallStateChangedLocked(boolean z) {
        if (this.mCallActive == z) {
            return;
        }
        this.mCallActive = z;
        updateAllRecognitionsLocked();
    }

    public final void onPowerSaveModeChangedLocked(int i) {
        if (this.mSoundTriggerPowerSaveMode == i) {
            return;
        }
        this.mSoundTriggerPowerSaveMode = i;
        updateAllRecognitionsLocked();
    }

    public final void onModelUnloadedLocked(int i) {
        ModelData modelDataForLocked = getModelDataForLocked(i);
        if (modelDataForLocked != null) {
            modelDataForLocked.setNotLoaded();
        }
    }

    public final void onResourcesAvailableLocked() {
        updateAllRecognitionsLocked();
    }

    public final void onRecognitionAbortLocked(SoundTrigger.RecognitionEvent recognitionEvent) {
        Slog.w("SoundTriggerHelper", "Recognition aborted");
        MetricsLogger.count(this.mContext, "sth_recognition_aborted", 1);
        ModelData modelDataForLocked = getModelDataForLocked(recognitionEvent.soundModelHandle);
        if (modelDataForLocked != null) {
            if (modelDataForLocked.isModelStarted() || modelDataForLocked.isStopPending()) {
                modelDataForLocked.setStopped();
                try {
                    IRecognitionStatusCallback callback = modelDataForLocked.getCallback();
                    if (callback != null) {
                        callback.onRecognitionPaused();
                    }
                } catch (DeadObjectException e) {
                    forceStopAndUnloadModelLocked(modelDataForLocked, e);
                } catch (RemoteException e2) {
                    Slog.w("SoundTriggerHelper", "RemoteException in onRecognitionPaused", e2);
                }
                updateRecognitionLocked(modelDataForLocked, true);
            }
        }
    }

    public final void onRecognitionFailureLocked() {
        Slog.w("SoundTriggerHelper", "Recognition failure");
        MetricsLogger.count(this.mContext, "sth_recognition_failure_event", 1);
        try {
            sendErrorCallbacksToAllLocked(Integer.MIN_VALUE);
        } finally {
            internalClearModelStateLocked();
            internalClearGlobalStateLocked();
        }
    }

    public final int getKeyphraseIdFromEvent(SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent) {
        if (keyphraseRecognitionEvent == null) {
            Slog.w("SoundTriggerHelper", "Null RecognitionEvent received.");
            return Integer.MIN_VALUE;
        }
        SoundTrigger.KeyphraseRecognitionExtra[] keyphraseRecognitionExtraArr = keyphraseRecognitionEvent.keyphraseExtras;
        if (keyphraseRecognitionExtraArr == null || keyphraseRecognitionExtraArr.length == 0) {
            Slog.w("SoundTriggerHelper", "Invalid keyphrase recognition event!");
            return Integer.MIN_VALUE;
        }
        return keyphraseRecognitionExtraArr[0].id;
    }

    public final void onKeyphraseRecognitionSuccessLocked(SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent) {
        Slog.i("SoundTriggerHelper", "Recognition success");
        MetricsLogger.count(this.mContext, "sth_keyphrase_recognition_event", 1);
        int keyphraseIdFromEvent = getKeyphraseIdFromEvent(keyphraseRecognitionEvent);
        ModelData keyphraseModelDataLocked = getKeyphraseModelDataLocked(keyphraseIdFromEvent);
        if (keyphraseModelDataLocked == null || !keyphraseModelDataLocked.isKeyphraseModel()) {
            Slog.e("SoundTriggerHelper", "Keyphase model data does not exist for ID:" + keyphraseIdFromEvent);
        } else if (keyphraseModelDataLocked.getCallback() == null) {
            Slog.w("SoundTriggerHelper", "Received onRecognition event without callback for keyphrase model.");
        } else {
            if (!keyphraseRecognitionEvent.recognitionStillActive) {
                keyphraseModelDataLocked.setStopped();
            }
            try {
                keyphraseModelDataLocked.getCallback().onKeyphraseDetected(keyphraseRecognitionEvent);
            } catch (DeadObjectException e) {
                forceStopAndUnloadModelLocked(keyphraseModelDataLocked, e);
                return;
            } catch (RemoteException e2) {
                Slog.w("SoundTriggerHelper", "RemoteException in onKeyphraseDetected", e2);
            }
            SoundTrigger.RecognitionConfig recognitionConfig = keyphraseModelDataLocked.getRecognitionConfig();
            if (recognitionConfig != null) {
                keyphraseModelDataLocked.setRequested(recognitionConfig.allowMultipleTriggers);
            }
            if (keyphraseModelDataLocked.isRequested()) {
                updateRecognitionLocked(keyphraseModelDataLocked, true);
            }
        }
    }

    public final void updateAllRecognitionsLocked() {
        Iterator it = new ArrayList(this.mModelDataMap.values()).iterator();
        while (it.hasNext()) {
            updateRecognitionLocked((ModelData) it.next(), true);
        }
    }

    public final int updateRecognitionLocked(ModelData modelData, boolean z) {
        boolean z2 = modelData.isRequested() && isRecognitionAllowedByDeviceState(modelData);
        if (z2 == modelData.isModelStarted() || modelData.isStopPending()) {
            return 0;
        }
        if (z2) {
            int prepareForRecognition = prepareForRecognition(modelData);
            if (prepareForRecognition != 0) {
                Slog.w("SoundTriggerHelper", "startRecognition failed to prepare model for recognition");
                return prepareForRecognition;
            }
            int startRecognitionLocked = startRecognitionLocked(modelData, z);
            if (startRecognitionLocked == 0) {
                initializeDeviceStateListeners();
            }
            return startRecognitionLocked;
        }
        return stopRecognitionLocked(modelData, z);
    }

    public final void onServiceDiedLocked() {
        try {
            MetricsLogger.count(this.mContext, "sth_service_died", 1);
            sendErrorCallbacksToAllLocked(SoundTrigger.STATUS_DEAD_OBJECT);
        } finally {
            internalClearModelStateLocked();
            internalClearGlobalStateLocked();
            SoundTriggerModule soundTriggerModule = this.mModule;
            if (soundTriggerModule != null) {
                soundTriggerModule.detach();
                this.mModule = this.mModuleProvider.apply(this);
            }
        }
    }

    public final void internalClearGlobalStateLocked() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            PowerSaveModeListener powerSaveModeListener = this.mPowerSaveModeListener;
            if (powerSaveModeListener != null) {
                this.mContext.unregisterReceiver(powerSaveModeListener);
                this.mPowerSaveModeListener = null;
            }
            this.mRecognitionRequested = false;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public final void internalClearModelStateLocked() {
        for (ModelData modelData : this.mModelDataMap.values()) {
            modelData.clearState();
        }
    }

    /* loaded from: classes2.dex */
    public class MyCallStateListener extends PhoneStateListener {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public MyCallStateListener(Looper looper) {
            super(looper);
            Objects.requireNonNull(looper);
        }

        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(int i, String str) {
            if (SoundTriggerHelper.this.mHandler != null) {
                synchronized (SoundTriggerHelper.this.mLock) {
                    SoundTriggerHelper.this.mHandler.removeMessages(0);
                    SoundTriggerHelper.this.mHandler.sendMessageDelayed(SoundTriggerHelper.this.mHandler.obtainMessage(0, i, 0), 2 == i ? 0L : 1000L);
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public class PowerSaveModeListener extends BroadcastReceiver {
        public PowerSaveModeListener() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                int soundTriggerPowerSaveMode = SoundTriggerHelper.this.mPowerManager.getSoundTriggerPowerSaveMode();
                synchronized (SoundTriggerHelper.this.mLock) {
                    SoundTriggerHelper.this.onPowerSaveModeChangedLocked(soundTriggerPowerSaveMode);
                }
            }
        }
    }

    public final void initializeDeviceStateListeners() {
        if (this.mRecognitionRequested) {
            return;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mCallActive = this.mTelephonyManager.getCallState() == 2;
            this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
            if (this.mPowerSaveModeListener == null) {
                PowerSaveModeListener powerSaveModeListener = new PowerSaveModeListener();
                this.mPowerSaveModeListener = powerSaveModeListener;
                this.mContext.registerReceiver(powerSaveModeListener, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED"));
            }
            this.mSoundTriggerPowerSaveMode = this.mPowerManager.getSoundTriggerPowerSaveMode();
            this.mRecognitionRequested = true;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void sendErrorCallbacksToAllLocked(int i) {
        for (ModelData modelData : this.mModelDataMap.values()) {
            IRecognitionStatusCallback callback = modelData.getCallback();
            if (callback != null) {
                try {
                    callback.onError(i);
                } catch (RemoteException e) {
                    Slog.w("SoundTriggerHelper", "RemoteException sendErrorCallbacksToAllLocked for model handle " + modelData.getHandle(), e);
                }
            }
        }
    }

    public void detach() {
        synchronized (this.mLock) {
            for (ModelData modelData : this.mModelDataMap.values()) {
                forceStopAndUnloadModelLocked(modelData, null);
            }
            this.mModelDataMap.clear();
            internalClearGlobalStateLocked();
            SoundTriggerModule soundTriggerModule = this.mModule;
            if (soundTriggerModule != null) {
                soundTriggerModule.detach();
                this.mModule = null;
            }
        }
    }

    public final void forceStopAndUnloadModelLocked(ModelData modelData, Exception exc) {
        forceStopAndUnloadModelLocked(modelData, exc, null);
    }

    public final void forceStopAndUnloadModelLocked(ModelData modelData, Exception exc, Iterator it) {
        if (exc != null) {
            Slog.e("SoundTriggerHelper", "forceStopAndUnloadModel", exc);
        }
        if (this.mModule == null) {
            return;
        }
        if (modelData.isStopPending()) {
            modelData.setStopped();
        } else if (modelData.isModelStarted()) {
            Slog.d("SoundTriggerHelper", "Stopping previously started dangling model " + modelData.getHandle());
            if (this.mModule.stopRecognition(modelData.getHandle()) == 0) {
                modelData.setStopped();
                modelData.setRequested(false);
            } else {
                Slog.e("SoundTriggerHelper", "Failed to stop model " + modelData.getHandle());
            }
        }
        if (modelData.isModelLoaded()) {
            Slog.d("SoundTriggerHelper", "Unloading previously loaded dangling model " + modelData.getHandle());
            if (this.mModule.unloadSoundModel(modelData.getHandle()) == 0) {
                if (it != null) {
                    it.remove();
                } else {
                    this.mModelDataMap.remove(modelData.getModelId());
                }
                Iterator<Map.Entry<Integer, UUID>> it2 = this.mKeyphraseUuidMap.entrySet().iterator();
                while (it2.hasNext()) {
                    if (it2.next().getValue().equals(modelData.getModelId())) {
                        it2.remove();
                    }
                }
                modelData.clearState();
                return;
            }
            Slog.e("SoundTriggerHelper", "Failed to unload model " + modelData.getHandle());
        }
    }

    public final void stopAndUnloadDeadModelsLocked() {
        Iterator<Map.Entry<UUID, ModelData>> it = this.mModelDataMap.entrySet().iterator();
        while (it.hasNext()) {
            ModelData value = it.next().getValue();
            if (value.isModelLoaded() && (value.getCallback() == null || (value.getCallback().asBinder() != null && !value.getCallback().asBinder().pingBinder()))) {
                Slog.w("SoundTriggerHelper", "Removing model " + value.getHandle() + " that has no clients");
                forceStopAndUnloadModelLocked(value, null, it);
            }
        }
    }

    public final ModelData getOrCreateGenericModelDataLocked(UUID uuid) {
        ModelData modelData = this.mModelDataMap.get(uuid);
        if (modelData == null) {
            ModelData createGenericModelData = ModelData.createGenericModelData(uuid);
            this.mModelDataMap.put(uuid, createGenericModelData);
            return createGenericModelData;
        } else if (modelData.isGenericModel()) {
            return modelData;
        } else {
            Slog.e("SoundTriggerHelper", "UUID already used for non-generic model.");
            return null;
        }
    }

    public final void removeKeyphraseModelLocked(int i) {
        UUID uuid = this.mKeyphraseUuidMap.get(Integer.valueOf(i));
        if (uuid == null) {
            return;
        }
        this.mModelDataMap.remove(uuid);
        this.mKeyphraseUuidMap.remove(Integer.valueOf(i));
    }

    public final ModelData getKeyphraseModelDataLocked(int i) {
        UUID uuid = this.mKeyphraseUuidMap.get(Integer.valueOf(i));
        if (uuid == null) {
            return null;
        }
        return this.mModelDataMap.get(uuid);
    }

    public final ModelData createKeyphraseModelDataLocked(UUID uuid, int i) {
        this.mKeyphraseUuidMap.remove(Integer.valueOf(i));
        this.mModelDataMap.remove(uuid);
        this.mKeyphraseUuidMap.put(Integer.valueOf(i), uuid);
        ModelData createKeyphraseModelData = ModelData.createKeyphraseModelData(uuid);
        this.mModelDataMap.put(uuid, createKeyphraseModelData);
        return createKeyphraseModelData;
    }

    public final ModelData getModelDataForLocked(int i) {
        for (ModelData modelData : this.mModelDataMap.values()) {
            if (modelData.getHandle() == i) {
                return modelData;
            }
        }
        return null;
    }

    public final boolean isRecognitionAllowedByDeviceState(ModelData modelData) {
        if (!this.mRecognitionRequested) {
            this.mCallActive = this.mTelephonyManager.getCallState() == 2;
            this.mSoundTriggerPowerSaveMode = this.mPowerManager.getSoundTriggerPowerSaveMode();
        }
        return !this.mCallActive && isRecognitionAllowedByPowerState(modelData);
    }

    public boolean isRecognitionAllowedByPowerState(ModelData modelData) {
        int i = this.mSoundTriggerPowerSaveMode;
        if (i != 0) {
            return i == 1 && modelData.shouldRunInBatterySaverMode();
        }
        return true;
    }

    public final int startRecognitionLocked(ModelData modelData, boolean z) {
        IRecognitionStatusCallback callback = modelData.getCallback();
        SoundTrigger.RecognitionConfig recognitionConfig = modelData.getRecognitionConfig();
        if (callback == null || !modelData.isModelLoaded() || recognitionConfig == null) {
            Slog.w("SoundTriggerHelper", "startRecognition: Bad data passed in.");
            MetricsLogger.count(this.mContext, "sth_start_recognition_error", 1);
            return Integer.MIN_VALUE;
        } else if (!isRecognitionAllowedByDeviceState(modelData)) {
            Slog.w("SoundTriggerHelper", "startRecognition requested but not allowed.");
            MetricsLogger.count(this.mContext, "sth_start_recognition_not_allowed", 1);
            return 0;
        } else {
            SoundTriggerModule soundTriggerModule = this.mModule;
            if (soundTriggerModule == null) {
                return Integer.MIN_VALUE;
            }
            int startRecognition = soundTriggerModule.startRecognition(modelData.getHandle(), recognitionConfig);
            if (startRecognition != 0) {
                Slog.w("SoundTriggerHelper", "startRecognition failed with " + startRecognition);
                MetricsLogger.count(this.mContext, "sth_start_recognition_error", 1);
                if (z) {
                    try {
                        callback.onError(startRecognition);
                    } catch (DeadObjectException e) {
                        forceStopAndUnloadModelLocked(modelData, e);
                    } catch (RemoteException e2) {
                        Slog.w("SoundTriggerHelper", "RemoteException in onError", e2);
                    }
                }
            } else {
                Slog.i("SoundTriggerHelper", "startRecognition successful.");
                MetricsLogger.count(this.mContext, "sth_start_recognition_success", 1);
                modelData.setStarted();
                if (z) {
                    try {
                        callback.onRecognitionResumed();
                    } catch (DeadObjectException e3) {
                        forceStopAndUnloadModelLocked(modelData, e3);
                    } catch (RemoteException e4) {
                        Slog.w("SoundTriggerHelper", "RemoteException in onRecognitionResumed", e4);
                    }
                }
            }
            return startRecognition;
        }
    }

    public final int stopRecognitionLocked(ModelData modelData, boolean z) {
        if (this.mModule == null) {
            return Integer.MIN_VALUE;
        }
        IRecognitionStatusCallback callback = modelData.getCallback();
        int stopRecognition = this.mModule.stopRecognition(modelData.getHandle());
        if (stopRecognition != 0) {
            Slog.w("SoundTriggerHelper", "stopRecognition call failed with " + stopRecognition);
            MetricsLogger.count(this.mContext, "sth_stop_recognition_error", 1);
            if (z) {
                try {
                    callback.onError(stopRecognition);
                } catch (DeadObjectException e) {
                    forceStopAndUnloadModelLocked(modelData, e);
                } catch (RemoteException e2) {
                    Slog.w("SoundTriggerHelper", "RemoteException in onError", e2);
                }
            }
        } else {
            modelData.setStopPending();
            MetricsLogger.count(this.mContext, "sth_stop_recognition_success", 1);
            if (z) {
                try {
                    callback.onRecognitionPaused();
                } catch (DeadObjectException e3) {
                    forceStopAndUnloadModelLocked(modelData, e3);
                } catch (RemoteException e4) {
                    Slog.w("SoundTriggerHelper", "RemoteException in onRecognitionPaused", e4);
                }
            }
        }
        return stopRecognition;
    }

    public final boolean computeRecognitionRequestedLocked() {
        if (this.mModule == null) {
            this.mRecognitionRequested = false;
            return false;
        }
        for (ModelData modelData : this.mModelDataMap.values()) {
            if (modelData.isRequested()) {
                this.mRecognitionRequested = true;
                return true;
            }
        }
        this.mRecognitionRequested = false;
        return false;
    }

    /* loaded from: classes2.dex */
    public static class ModelData {
        public int mModelHandle;
        public UUID mModelId;
        public int mModelState;
        public int mModelType;
        public boolean mRequested = false;
        public IRecognitionStatusCallback mCallback = null;
        public SoundTrigger.RecognitionConfig mRecognitionConfig = null;
        public boolean mRunInBatterySaverMode = false;
        public SoundTrigger.SoundModel mSoundModel = null;

        public ModelData(UUID uuid, int i) {
            this.mModelId = uuid;
            this.mModelType = i;
        }

        public static ModelData createKeyphraseModelData(UUID uuid) {
            return new ModelData(uuid, 0);
        }

        public static ModelData createGenericModelData(UUID uuid) {
            return new ModelData(uuid, 1);
        }

        public synchronized void setCallback(IRecognitionStatusCallback iRecognitionStatusCallback) {
            this.mCallback = iRecognitionStatusCallback;
        }

        public synchronized IRecognitionStatusCallback getCallback() {
            return this.mCallback;
        }

        public synchronized boolean isModelLoaded() {
            boolean z;
            int i = this.mModelState;
            z = true;
            if (i != 1 && i != 2) {
                z = false;
            }
            return z;
        }

        public synchronized boolean isModelNotLoaded() {
            return this.mModelState == 0;
        }

        public synchronized boolean isStopPending() {
            return this.mModelState == 3;
        }

        public synchronized void setStarted() {
            this.mModelState = 2;
        }

        public synchronized void setStopped() {
            this.mModelState = 1;
        }

        public synchronized void setStopPending() {
            this.mModelState = 3;
        }

        public synchronized void setLoaded() {
            this.mModelState = 1;
        }

        public synchronized void setNotLoaded() {
            this.mModelState = 0;
        }

        public synchronized boolean isModelStarted() {
            return this.mModelState == 2;
        }

        public synchronized void clearState() {
            this.mModelState = 0;
            this.mRecognitionConfig = null;
            this.mRequested = false;
            this.mCallback = null;
        }

        public synchronized void clearCallback() {
            this.mCallback = null;
        }

        public synchronized void setHandle(int i) {
            this.mModelHandle = i;
        }

        public synchronized void setRecognitionConfig(SoundTrigger.RecognitionConfig recognitionConfig) {
            this.mRecognitionConfig = recognitionConfig;
        }

        public synchronized void setRunInBatterySaverMode(boolean z) {
            this.mRunInBatterySaverMode = z;
        }

        public synchronized boolean shouldRunInBatterySaverMode() {
            return this.mRunInBatterySaverMode;
        }

        public synchronized int getHandle() {
            return this.mModelHandle;
        }

        public synchronized UUID getModelId() {
            return this.mModelId;
        }

        public synchronized SoundTrigger.RecognitionConfig getRecognitionConfig() {
            return this.mRecognitionConfig;
        }

        public synchronized boolean isRequested() {
            return this.mRequested;
        }

        public synchronized void setRequested(boolean z) {
            this.mRequested = z;
        }

        public synchronized void setSoundModel(SoundTrigger.SoundModel soundModel) {
            this.mSoundModel = soundModel;
        }

        public synchronized SoundTrigger.SoundModel getSoundModel() {
            return this.mSoundModel;
        }

        public synchronized boolean isKeyphraseModel() {
            return this.mModelType == 0;
        }

        public synchronized boolean isGenericModel() {
            return this.mModelType == 1;
        }

        public synchronized String stateToString() {
            int i = this.mModelState;
            return i != 0 ? i != 1 ? i != 2 ? "Unknown state" : "STARTED" : "LOADED" : "NOT_LOADED";
        }

        public synchronized String requestedToString() {
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Requested: ");
            sb.append(this.mRequested ? "Yes" : "No");
            return sb.toString();
        }

        public synchronized String callbackToString() {
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Callback: ");
            IRecognitionStatusCallback iRecognitionStatusCallback = this.mCallback;
            sb.append(iRecognitionStatusCallback != null ? iRecognitionStatusCallback.asBinder() : "null");
            return sb.toString();
        }

        public synchronized String uuidToString() {
            return "UUID: " + this.mModelId;
        }

        public synchronized String toString() {
            return "Handle: " + this.mModelHandle + "\nModelState: " + stateToString() + "\n" + requestedToString() + "\n" + callbackToString() + "\n" + uuidToString() + "\n" + modelTypeToString() + "RunInBatterySaverMode=" + this.mRunInBatterySaverMode;
        }

        public synchronized String modelTypeToString() {
            String str;
            int i = this.mModelType;
            str = i != -1 ? i != 0 ? i != 1 ? null : "Generic" : "Keyphrase" : "Unknown";
            return "Model type: " + str + "\n";
        }
    }
}
