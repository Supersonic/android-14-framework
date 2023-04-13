package com.android.server.soundtrigger_middleware;

import android.hardware.soundtrigger.V2_0.ISoundTriggerHw;
import android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback;
import android.hardware.soundtrigger.V2_1.ISoundTriggerHw;
import android.hardware.soundtrigger.V2_1.ISoundTriggerHwCallback;
import android.hardware.soundtrigger.V2_3.ISoundTriggerHw;
import android.hardware.soundtrigger.V2_3.OptionalModelParameterRange;
import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.Properties;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.SoundModel;
import android.os.HidlMemory;
import android.os.IBinder;
import android.os.IHwBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.soundtrigger_middleware.ISoundTriggerHal;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes2.dex */
public final class SoundTriggerHw2Compat implements ISoundTriggerHal {
    public final IHwBinder mBinder;
    public final Properties mProperties;
    public final Runnable mRebootRunnable;
    public ISoundTriggerHw mUnderlying_2_0;
    public android.hardware.soundtrigger.V2_1.ISoundTriggerHw mUnderlying_2_1;
    public android.hardware.soundtrigger.V2_2.ISoundTriggerHw mUnderlying_2_2;
    public android.hardware.soundtrigger.V2_3.ISoundTriggerHw mUnderlying_2_3;
    public final ConcurrentMap<Integer, ISoundTriggerHal.ModelCallback> mModelCallbacks = new ConcurrentHashMap();
    public final Map<IBinder.DeathRecipient, IHwBinder.DeathRecipient> mDeathRecipientMap = new HashMap();

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void detach() {
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void registerCallback(ISoundTriggerHal.GlobalCallback globalCallback) {
    }

    public static ISoundTriggerHal create(ISoundTriggerHw iSoundTriggerHw, Runnable runnable, ICaptureStateNotifier iCaptureStateNotifier) {
        return create(iSoundTriggerHw.asBinder(), runnable, iCaptureStateNotifier);
    }

    public static ISoundTriggerHal create(IHwBinder iHwBinder, Runnable runnable, ICaptureStateNotifier iCaptureStateNotifier) {
        SoundTriggerHw2Compat soundTriggerHw2Compat = new SoundTriggerHw2Compat(iHwBinder, runnable);
        SoundTriggerHalMaxModelLimiter soundTriggerHalMaxModelLimiter = new SoundTriggerHalMaxModelLimiter(soundTriggerHw2Compat, soundTriggerHw2Compat.mProperties.maxSoundModels);
        return !soundTriggerHw2Compat.mProperties.concurrentCapture ? new SoundTriggerHalConcurrentCaptureHandler(soundTriggerHalMaxModelLimiter, iCaptureStateNotifier) : soundTriggerHalMaxModelLimiter;
    }

    public SoundTriggerHw2Compat(IHwBinder iHwBinder, Runnable runnable) {
        Objects.requireNonNull(runnable);
        this.mRebootRunnable = runnable;
        Objects.requireNonNull(iHwBinder);
        this.mBinder = iHwBinder;
        initUnderlying(iHwBinder);
        Properties propertiesInternal = getPropertiesInternal();
        Objects.requireNonNull(propertiesInternal);
        this.mProperties = propertiesInternal;
    }

    public final void initUnderlying(IHwBinder iHwBinder) {
        android.hardware.soundtrigger.V2_3.ISoundTriggerHw asInterface = android.hardware.soundtrigger.V2_3.ISoundTriggerHw.asInterface(iHwBinder);
        if (asInterface != null) {
            this.mUnderlying_2_3 = asInterface;
            this.mUnderlying_2_2 = asInterface;
            this.mUnderlying_2_1 = asInterface;
            this.mUnderlying_2_0 = asInterface;
            return;
        }
        android.hardware.soundtrigger.V2_2.ISoundTriggerHw asInterface2 = android.hardware.soundtrigger.V2_2.ISoundTriggerHw.asInterface(iHwBinder);
        if (asInterface2 != null) {
            this.mUnderlying_2_2 = asInterface2;
            this.mUnderlying_2_1 = asInterface2;
            this.mUnderlying_2_0 = asInterface2;
            this.mUnderlying_2_3 = null;
            return;
        }
        android.hardware.soundtrigger.V2_1.ISoundTriggerHw asInterface3 = android.hardware.soundtrigger.V2_1.ISoundTriggerHw.asInterface(iHwBinder);
        if (asInterface3 != null) {
            this.mUnderlying_2_1 = asInterface3;
            this.mUnderlying_2_0 = asInterface3;
            this.mUnderlying_2_3 = null;
            this.mUnderlying_2_2 = null;
            return;
        }
        ISoundTriggerHw asInterface4 = ISoundTriggerHw.asInterface(iHwBinder);
        if (asInterface4 != null) {
            this.mUnderlying_2_0 = asInterface4;
            this.mUnderlying_2_3 = null;
            this.mUnderlying_2_2 = null;
            this.mUnderlying_2_1 = null;
            return;
        }
        throw new RuntimeException("Binder doesn't support ISoundTriggerHw@2.0");
    }

    public static void handleHalStatus(int i, String str) {
        if (i != 0) {
            throw new HalException(i, str);
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void reboot() {
        this.mRebootRunnable.run();
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public Properties getProperties() {
        return this.mProperties;
    }

    public final Properties getPropertiesInternal() {
        try {
            final AtomicInteger atomicInteger = new AtomicInteger(-1);
            final AtomicReference atomicReference = new AtomicReference();
            try {
                as2_3().getProperties_2_3(new ISoundTriggerHw.getProperties_2_3Callback() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHw2Compat$$ExternalSyntheticLambda0
                    @Override // android.hardware.soundtrigger.V2_3.ISoundTriggerHw.getProperties_2_3Callback
                    public final void onValues(int i, android.hardware.soundtrigger.V2_3.Properties properties) {
                        SoundTriggerHw2Compat.lambda$getPropertiesInternal$0(atomicInteger, atomicReference, i, properties);
                    }
                });
                handleHalStatus(atomicInteger.get(), "getProperties_2_3");
                return ConversionUtil.hidl2aidlProperties((android.hardware.soundtrigger.V2_3.Properties) atomicReference.get());
            } catch (NotSupported unused) {
                return getProperties_2_0();
            }
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public static /* synthetic */ void lambda$getPropertiesInternal$0(AtomicInteger atomicInteger, AtomicReference atomicReference, int i, android.hardware.soundtrigger.V2_3.Properties properties) {
        atomicInteger.set(i);
        atomicReference.set(properties);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int loadSoundModel(SoundModel soundModel, ISoundTriggerHal.ModelCallback modelCallback) {
        ISoundTriggerHw.SoundModel aidl2hidlSoundModel = ConversionUtil.aidl2hidlSoundModel(soundModel);
        try {
            try {
                final AtomicInteger atomicInteger = new AtomicInteger(-1);
                final AtomicInteger atomicInteger2 = new AtomicInteger(0);
                try {
                    as2_1().loadSoundModel_2_1(aidl2hidlSoundModel, new ModelCallbackWrapper(modelCallback), 0, new ISoundTriggerHw.loadSoundModel_2_1Callback() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHw2Compat$$ExternalSyntheticLambda2
                        @Override // android.hardware.soundtrigger.V2_1.ISoundTriggerHw.loadSoundModel_2_1Callback
                        public final void onValues(int i, int i2) {
                            SoundTriggerHw2Compat.lambda$loadSoundModel$1(atomicInteger, atomicInteger2, i, i2);
                        }
                    });
                    handleHalStatus(atomicInteger.get(), "loadSoundModel_2_1");
                    this.mModelCallbacks.put(Integer.valueOf(atomicInteger2.get()), modelCallback);
                    return atomicInteger2.get();
                } catch (NotSupported unused) {
                    int loadSoundModel_2_0 = loadSoundModel_2_0(aidl2hidlSoundModel, modelCallback);
                    HidlMemory hidlMemory = aidl2hidlSoundModel.data;
                    if (hidlMemory != null) {
                        try {
                            hidlMemory.close();
                        } catch (IOException e) {
                            Log.e("SoundTriggerHw2Compat", "Failed to close file", e);
                        }
                    }
                    return loadSoundModel_2_0;
                }
            } catch (RemoteException e2) {
                throw e2.rethrowAsRuntimeException();
            }
        } finally {
            HidlMemory hidlMemory2 = aidl2hidlSoundModel.data;
            if (hidlMemory2 != null) {
                try {
                    hidlMemory2.close();
                } catch (IOException e3) {
                    Log.e("SoundTriggerHw2Compat", "Failed to close file", e3);
                }
            }
        }
    }

    public static /* synthetic */ void lambda$loadSoundModel$1(AtomicInteger atomicInteger, AtomicInteger atomicInteger2, int i, int i2) {
        atomicInteger.set(i);
        atomicInteger2.set(i2);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int loadPhraseSoundModel(PhraseSoundModel phraseSoundModel, ISoundTriggerHal.ModelCallback modelCallback) {
        ISoundTriggerHw.PhraseSoundModel aidl2hidlPhraseSoundModel = ConversionUtil.aidl2hidlPhraseSoundModel(phraseSoundModel);
        try {
            try {
                final AtomicInteger atomicInteger = new AtomicInteger(-1);
                final AtomicInteger atomicInteger2 = new AtomicInteger(0);
                try {
                    as2_1().loadPhraseSoundModel_2_1(aidl2hidlPhraseSoundModel, new ModelCallbackWrapper(modelCallback), 0, new ISoundTriggerHw.loadPhraseSoundModel_2_1Callback() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHw2Compat$$ExternalSyntheticLambda4
                        @Override // android.hardware.soundtrigger.V2_1.ISoundTriggerHw.loadPhraseSoundModel_2_1Callback
                        public final void onValues(int i, int i2) {
                            SoundTriggerHw2Compat.lambda$loadPhraseSoundModel$2(atomicInteger, atomicInteger2, i, i2);
                        }
                    });
                    handleHalStatus(atomicInteger.get(), "loadPhraseSoundModel_2_1");
                    this.mModelCallbacks.put(Integer.valueOf(atomicInteger2.get()), modelCallback);
                    return atomicInteger2.get();
                } catch (NotSupported unused) {
                    int loadPhraseSoundModel_2_0 = loadPhraseSoundModel_2_0(aidl2hidlPhraseSoundModel, modelCallback);
                    HidlMemory hidlMemory = aidl2hidlPhraseSoundModel.common.data;
                    if (hidlMemory != null) {
                        try {
                            hidlMemory.close();
                        } catch (IOException e) {
                            Log.e("SoundTriggerHw2Compat", "Failed to close file", e);
                        }
                    }
                    return loadPhraseSoundModel_2_0;
                }
            } finally {
                HidlMemory hidlMemory2 = aidl2hidlPhraseSoundModel.common.data;
                if (hidlMemory2 != null) {
                    try {
                        hidlMemory2.close();
                    } catch (IOException e2) {
                        Log.e("SoundTriggerHw2Compat", "Failed to close file", e2);
                    }
                }
            }
        } catch (RemoteException e3) {
            throw e3.rethrowAsRuntimeException();
        }
    }

    public static /* synthetic */ void lambda$loadPhraseSoundModel$2(AtomicInteger atomicInteger, AtomicInteger atomicInteger2, int i, int i2) {
        atomicInteger.set(i);
        atomicInteger2.set(i2);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void unloadSoundModel(int i) {
        try {
            this.mModelCallbacks.remove(Integer.valueOf(i));
            handleHalStatus(as2_0().unloadSoundModel(i), "unloadSoundModel");
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void stopRecognition(int i) {
        try {
            handleHalStatus(as2_0().stopRecognition(i), "stopRecognition");
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void startRecognition(int i, int i2, int i3, RecognitionConfig recognitionConfig) {
        android.hardware.soundtrigger.V2_3.RecognitionConfig aidl2hidlRecognitionConfig = ConversionUtil.aidl2hidlRecognitionConfig(recognitionConfig, i2, i3);
        try {
            try {
                handleHalStatus(as2_3().startRecognition_2_3(i, aidl2hidlRecognitionConfig), "startRecognition_2_3");
            } catch (NotSupported unused) {
                startRecognition_2_1(i, aidl2hidlRecognitionConfig);
            }
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void forceRecognitionEvent(int i) {
        try {
            handleHalStatus(as2_2().getModelState(i), "getModelState");
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (NotSupported e2) {
            throw e2.throwAsRecoverableException();
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public int getModelParameter(int i, int i2) {
        final AtomicInteger atomicInteger = new AtomicInteger(-1);
        final AtomicInteger atomicInteger2 = new AtomicInteger(0);
        try {
            as2_3().getParameter(i, i2, new ISoundTriggerHw.getParameterCallback() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHw2Compat$$ExternalSyntheticLambda1
                @Override // android.hardware.soundtrigger.V2_3.ISoundTriggerHw.getParameterCallback
                public final void onValues(int i3, int i4) {
                    SoundTriggerHw2Compat.lambda$getModelParameter$3(atomicInteger, atomicInteger2, i3, i4);
                }
            });
            handleHalStatus(atomicInteger.get(), "getParameter");
            return atomicInteger2.get();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (NotSupported e2) {
            throw e2.throwAsRecoverableException();
        }
    }

    public static /* synthetic */ void lambda$getModelParameter$3(AtomicInteger atomicInteger, AtomicInteger atomicInteger2, int i, int i2) {
        atomicInteger.set(i);
        atomicInteger2.set(i2);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void setModelParameter(int i, int i2, int i3) {
        try {
            handleHalStatus(as2_3().setParameter(i, i2, i3), "setParameter");
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (NotSupported e2) {
            throw e2.throwAsRecoverableException();
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public ModelParameterRange queryParameter(int i, int i2) {
        final AtomicInteger atomicInteger = new AtomicInteger(-1);
        final AtomicReference atomicReference = new AtomicReference();
        try {
            as2_3().queryParameter(i, i2, new ISoundTriggerHw.queryParameterCallback() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHw2Compat$$ExternalSyntheticLambda5
                @Override // android.hardware.soundtrigger.V2_3.ISoundTriggerHw.queryParameterCallback
                public final void onValues(int i3, OptionalModelParameterRange optionalModelParameterRange) {
                    SoundTriggerHw2Compat.lambda$queryParameter$4(atomicInteger, atomicReference, i3, optionalModelParameterRange);
                }
            });
            handleHalStatus(atomicInteger.get(), "queryParameter");
            if (((OptionalModelParameterRange) atomicReference.get()).getDiscriminator() == 1) {
                return ConversionUtil.hidl2aidlModelParameterRange(((OptionalModelParameterRange) atomicReference.get()).range());
            }
            return null;
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (NotSupported unused) {
            return null;
        }
    }

    public static /* synthetic */ void lambda$queryParameter$4(AtomicInteger atomicInteger, AtomicReference atomicReference, int i, OptionalModelParameterRange optionalModelParameterRange) {
        atomicInteger.set(i);
        atomicReference.set(optionalModelParameterRange);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerHal
    public void linkToDeath(final IBinder.DeathRecipient deathRecipient) {
        IHwBinder.DeathRecipient deathRecipient2 = new IHwBinder.DeathRecipient() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHw2Compat$$ExternalSyntheticLambda3
            public final void serviceDied(long j) {
                deathRecipient.binderDied();
            }
        };
        this.mDeathRecipientMap.put(deathRecipient, deathRecipient2);
        this.mBinder.linkToDeath(deathRecipient2, 0L);
    }

    public final Properties getProperties_2_0() throws RemoteException {
        final AtomicInteger atomicInteger = new AtomicInteger(-1);
        final AtomicReference atomicReference = new AtomicReference();
        as2_0().getProperties(new ISoundTriggerHw.getPropertiesCallback() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHw2Compat$$ExternalSyntheticLambda8
            @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw.getPropertiesCallback
            public final void onValues(int i, ISoundTriggerHw.Properties properties) {
                SoundTriggerHw2Compat.lambda$getProperties_2_0$6(atomicInteger, atomicReference, i, properties);
            }
        });
        handleHalStatus(atomicInteger.get(), "getProperties");
        return ConversionUtil.hidl2aidlProperties(Hw2CompatUtil.convertProperties_2_0_to_2_3((ISoundTriggerHw.Properties) atomicReference.get()));
    }

    public static /* synthetic */ void lambda$getProperties_2_0$6(AtomicInteger atomicInteger, AtomicReference atomicReference, int i, ISoundTriggerHw.Properties properties) {
        atomicInteger.set(i);
        atomicReference.set(properties);
    }

    public final int loadSoundModel_2_0(ISoundTriggerHw.SoundModel soundModel, ISoundTriggerHal.ModelCallback modelCallback) throws RemoteException {
        ISoundTriggerHw.SoundModel convertSoundModel_2_1_to_2_0 = Hw2CompatUtil.convertSoundModel_2_1_to_2_0(soundModel);
        final AtomicInteger atomicInteger = new AtomicInteger(-1);
        final AtomicInteger atomicInteger2 = new AtomicInteger(0);
        as2_0().loadSoundModel(convertSoundModel_2_1_to_2_0, new ModelCallbackWrapper(modelCallback), 0, new ISoundTriggerHw.loadSoundModelCallback() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHw2Compat$$ExternalSyntheticLambda7
            @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw.loadSoundModelCallback
            public final void onValues(int i, int i2) {
                SoundTriggerHw2Compat.lambda$loadSoundModel_2_0$7(atomicInteger, atomicInteger2, i, i2);
            }
        });
        handleHalStatus(atomicInteger.get(), "loadSoundModel");
        this.mModelCallbacks.put(Integer.valueOf(atomicInteger2.get()), modelCallback);
        return atomicInteger2.get();
    }

    public static /* synthetic */ void lambda$loadSoundModel_2_0$7(AtomicInteger atomicInteger, AtomicInteger atomicInteger2, int i, int i2) {
        atomicInteger.set(i);
        atomicInteger2.set(i2);
    }

    public final int loadPhraseSoundModel_2_0(ISoundTriggerHw.PhraseSoundModel phraseSoundModel, ISoundTriggerHal.ModelCallback modelCallback) throws RemoteException {
        ISoundTriggerHw.PhraseSoundModel convertPhraseSoundModel_2_1_to_2_0 = Hw2CompatUtil.convertPhraseSoundModel_2_1_to_2_0(phraseSoundModel);
        final AtomicInteger atomicInteger = new AtomicInteger(-1);
        final AtomicInteger atomicInteger2 = new AtomicInteger(0);
        as2_0().loadPhraseSoundModel(convertPhraseSoundModel_2_1_to_2_0, new ModelCallbackWrapper(modelCallback), 0, new ISoundTriggerHw.loadPhraseSoundModelCallback() { // from class: com.android.server.soundtrigger_middleware.SoundTriggerHw2Compat$$ExternalSyntheticLambda6
            @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw.loadPhraseSoundModelCallback
            public final void onValues(int i, int i2) {
                SoundTriggerHw2Compat.lambda$loadPhraseSoundModel_2_0$8(atomicInteger, atomicInteger2, i, i2);
            }
        });
        handleHalStatus(atomicInteger.get(), "loadSoundModel");
        this.mModelCallbacks.put(Integer.valueOf(atomicInteger2.get()), modelCallback);
        return atomicInteger2.get();
    }

    public static /* synthetic */ void lambda$loadPhraseSoundModel_2_0$8(AtomicInteger atomicInteger, AtomicInteger atomicInteger2, int i, int i2) {
        atomicInteger.set(i);
        atomicInteger2.set(i2);
    }

    public final void startRecognition_2_1(int i, android.hardware.soundtrigger.V2_3.RecognitionConfig recognitionConfig) {
        try {
            try {
                handleHalStatus(as2_1().startRecognition_2_1(i, Hw2CompatUtil.convertRecognitionConfig_2_3_to_2_1(recognitionConfig), new ModelCallbackWrapper(this.mModelCallbacks.get(Integer.valueOf(i))), 0), "startRecognition_2_1");
            } catch (NotSupported unused) {
                startRecognition_2_0(i, recognitionConfig);
            }
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public final void startRecognition_2_0(int i, android.hardware.soundtrigger.V2_3.RecognitionConfig recognitionConfig) throws RemoteException {
        handleHalStatus(as2_0().startRecognition(i, Hw2CompatUtil.convertRecognitionConfig_2_3_to_2_0(recognitionConfig), new ModelCallbackWrapper(this.mModelCallbacks.get(Integer.valueOf(i))), 0), "startRecognition");
    }

    public final android.hardware.soundtrigger.V2_0.ISoundTriggerHw as2_0() {
        return this.mUnderlying_2_0;
    }

    public final android.hardware.soundtrigger.V2_1.ISoundTriggerHw as2_1() throws NotSupported {
        android.hardware.soundtrigger.V2_1.ISoundTriggerHw iSoundTriggerHw = this.mUnderlying_2_1;
        if (iSoundTriggerHw != null) {
            return iSoundTriggerHw;
        }
        throw new NotSupported("Underlying driver version < 2.1");
    }

    public final android.hardware.soundtrigger.V2_2.ISoundTriggerHw as2_2() throws NotSupported {
        android.hardware.soundtrigger.V2_2.ISoundTriggerHw iSoundTriggerHw = this.mUnderlying_2_2;
        if (iSoundTriggerHw != null) {
            return iSoundTriggerHw;
        }
        throw new NotSupported("Underlying driver version < 2.2");
    }

    public final android.hardware.soundtrigger.V2_3.ISoundTriggerHw as2_3() throws NotSupported {
        android.hardware.soundtrigger.V2_3.ISoundTriggerHw iSoundTriggerHw = this.mUnderlying_2_3;
        if (iSoundTriggerHw != null) {
            return iSoundTriggerHw;
        }
        throw new NotSupported("Underlying driver version < 2.3");
    }

    /* loaded from: classes2.dex */
    public static class NotSupported extends Exception {
        public NotSupported(String str) {
            super(str);
        }

        public RecoverableException throwAsRecoverableException() {
            throw new RecoverableException(2, getMessage());
        }
    }

    /* loaded from: classes2.dex */
    public static class ModelCallbackWrapper extends ISoundTriggerHwCallback.Stub {
        public final ISoundTriggerHal.ModelCallback mDelegate;

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback
        public void soundModelCallback(ISoundTriggerHwCallback.ModelEvent modelEvent, int i) {
        }

        @Override // android.hardware.soundtrigger.V2_1.ISoundTriggerHwCallback
        public void soundModelCallback_2_1(ISoundTriggerHwCallback.ModelEvent modelEvent, int i) {
        }

        public ModelCallbackWrapper(ISoundTriggerHal.ModelCallback modelCallback) {
            Objects.requireNonNull(modelCallback);
            this.mDelegate = modelCallback;
        }

        @Override // android.hardware.soundtrigger.V2_1.ISoundTriggerHwCallback
        public void recognitionCallback_2_1(ISoundTriggerHwCallback.RecognitionEvent recognitionEvent, int i) {
            this.mDelegate.recognitionCallback(recognitionEvent.header.model, ConversionUtil.hidl2aidlRecognitionEvent(recognitionEvent));
        }

        @Override // android.hardware.soundtrigger.V2_1.ISoundTriggerHwCallback
        public void phraseRecognitionCallback_2_1(ISoundTriggerHwCallback.PhraseRecognitionEvent phraseRecognitionEvent, int i) {
            this.mDelegate.phraseRecognitionCallback(phraseRecognitionEvent.common.header.model, ConversionUtil.hidl2aidlPhraseRecognitionEvent(phraseRecognitionEvent));
        }

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback
        public void recognitionCallback(ISoundTriggerHwCallback.RecognitionEvent recognitionEvent, int i) {
            recognitionCallback_2_1(Hw2CompatUtil.convertRecognitionEvent_2_0_to_2_1(recognitionEvent), i);
        }

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback
        public void phraseRecognitionCallback(ISoundTriggerHwCallback.PhraseRecognitionEvent phraseRecognitionEvent, int i) {
            phraseRecognitionCallback_2_1(Hw2CompatUtil.convertPhraseRecognitionEvent_2_0_to_2_1(phraseRecognitionEvent), i);
        }
    }
}
