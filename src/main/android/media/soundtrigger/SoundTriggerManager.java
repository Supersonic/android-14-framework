package android.media.soundtrigger;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.permission.ClearCallingIdentityContext;
import android.media.permission.Identity;
import android.media.permission.SafeCloseable;
import android.media.soundtrigger.SoundTriggerDetector;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.ParcelUuid;
import android.p008os.RemoteException;
import android.provider.Settings;
import android.util.Slog;
import com.android.internal.app.ISoundTriggerService;
import com.android.internal.app.ISoundTriggerSession;
import com.android.internal.util.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
@SystemApi
/* loaded from: classes2.dex */
public final class SoundTriggerManager {
    private static final boolean DBG = false;
    public static final String EXTRA_MESSAGE_TYPE = "android.media.soundtrigger.MESSAGE_TYPE";
    public static final String EXTRA_RECOGNITION_EVENT = "android.media.soundtrigger.RECOGNITION_EVENT";
    public static final String EXTRA_STATUS = "android.media.soundtrigger.STATUS";
    public static final int FLAG_MESSAGE_TYPE_RECOGNITION_ERROR = 1;
    public static final int FLAG_MESSAGE_TYPE_RECOGNITION_EVENT = 0;
    public static final int FLAG_MESSAGE_TYPE_RECOGNITION_PAUSED = 2;
    public static final int FLAG_MESSAGE_TYPE_RECOGNITION_RESUMED = 3;
    public static final int FLAG_MESSAGE_TYPE_UNKNOWN = -1;
    private static final String TAG = "SoundTriggerManager";
    private final IBinder mBinderToken;
    private final Context mContext;
    private final HashMap<UUID, SoundTriggerDetector> mReceiverInstanceMap;
    private final ISoundTriggerSession mSoundTriggerSession;

    public SoundTriggerManager(Context context, ISoundTriggerService soundTriggerService) {
        Binder binder = new Binder();
        this.mBinderToken = binder;
        try {
            Identity originatorIdentity = new Identity();
            originatorIdentity.packageName = ActivityThread.currentOpPackageName();
            SafeCloseable ignored = ClearCallingIdentityContext.create();
            List<SoundTrigger.ModuleProperties> modulePropertiesList = soundTriggerService.listModuleProperties(originatorIdentity);
            if (!modulePropertiesList.isEmpty()) {
                this.mSoundTriggerSession = soundTriggerService.attachAsOriginator(originatorIdentity, modulePropertiesList.get(0), binder);
            } else {
                this.mSoundTriggerSession = null;
            }
            if (ignored != null) {
                ignored.close();
            }
            this.mContext = context;
            this.mReceiverInstanceMap = new HashMap<>();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Deprecated
    public void updateModel(Model model) {
        ISoundTriggerSession iSoundTriggerSession = this.mSoundTriggerSession;
        if (iSoundTriggerSession == null) {
            throw new IllegalStateException("No underlying SoundTriggerModule available");
        }
        try {
            iSoundTriggerSession.updateSoundModel(model.getGenericSoundModel());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public Model getModel(UUID soundModelId) {
        ISoundTriggerSession iSoundTriggerSession = this.mSoundTriggerSession;
        if (iSoundTriggerSession == null) {
            throw new IllegalStateException("No underlying SoundTriggerModule available");
        }
        try {
            SoundTrigger.GenericSoundModel model = iSoundTriggerSession.getSoundModel(new ParcelUuid(soundModelId));
            if (model == null) {
                return null;
            }
            return new Model(model);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void deleteModel(UUID soundModelId) {
        ISoundTriggerSession iSoundTriggerSession = this.mSoundTriggerSession;
        if (iSoundTriggerSession == null) {
            throw new IllegalStateException("No underlying SoundTriggerModule available");
        }
        try {
            iSoundTriggerSession.deleteSoundModel(new ParcelUuid(soundModelId));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public SoundTriggerDetector createSoundTriggerDetector(UUID soundModelId, SoundTriggerDetector.Callback callback, Handler handler) {
        if (soundModelId == null || this.mSoundTriggerSession == null) {
            return null;
        }
        this.mReceiverInstanceMap.get(soundModelId);
        try {
            ISoundTriggerSession iSoundTriggerSession = this.mSoundTriggerSession;
            SoundTriggerDetector newInstance = new SoundTriggerDetector(iSoundTriggerSession, iSoundTriggerSession.getSoundModel(new ParcelUuid(soundModelId)), callback, handler);
            this.mReceiverInstanceMap.put(soundModelId, newInstance);
            return newInstance;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes2.dex */
    public static class Model {
        private SoundTrigger.GenericSoundModel mGenericSoundModel;

        Model(SoundTrigger.GenericSoundModel soundTriggerModel) {
            this.mGenericSoundModel = soundTriggerModel;
        }

        public static Model create(UUID modelUuid, UUID vendorUuid, byte[] data, int version) {
            Objects.requireNonNull(modelUuid);
            Objects.requireNonNull(vendorUuid);
            return new Model(new SoundTrigger.GenericSoundModel(modelUuid, vendorUuid, data, version));
        }

        public static Model create(UUID modelUuid, UUID vendorUuid, byte[] data) {
            return create(modelUuid, vendorUuid, data, -1);
        }

        public UUID getModelUuid() {
            return this.mGenericSoundModel.getUuid();
        }

        public UUID getVendorUuid() {
            return this.mGenericSoundModel.getVendorUuid();
        }

        public int getVersion() {
            return this.mGenericSoundModel.getVersion();
        }

        public byte[] getModelData() {
            return this.mGenericSoundModel.getData();
        }

        SoundTrigger.GenericSoundModel getGenericSoundModel() {
            return this.mGenericSoundModel;
        }
    }

    public int loadSoundModel(SoundTrigger.SoundModel soundModel) {
        if (soundModel == null || this.mSoundTriggerSession == null) {
            return Integer.MIN_VALUE;
        }
        try {
            switch (soundModel.getType()) {
                case 0:
                    return this.mSoundTriggerSession.loadKeyphraseSoundModel((SoundTrigger.KeyphraseSoundModel) soundModel);
                case 1:
                    return this.mSoundTriggerSession.loadGenericSoundModel((SoundTrigger.GenericSoundModel) soundModel);
                default:
                    Slog.m96e(TAG, "Unkown model type");
                    return Integer.MIN_VALUE;
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int startRecognition(UUID soundModelId, Bundle params, ComponentName detectionService, SoundTrigger.RecognitionConfig config) {
        Preconditions.checkNotNull(soundModelId);
        Preconditions.checkNotNull(detectionService);
        Preconditions.checkNotNull(config);
        ISoundTriggerSession iSoundTriggerSession = this.mSoundTriggerSession;
        if (iSoundTriggerSession == null) {
            return Integer.MIN_VALUE;
        }
        try {
            return iSoundTriggerSession.startRecognitionForService(new ParcelUuid(soundModelId), params, detectionService, config);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int stopRecognition(UUID soundModelId) {
        ISoundTriggerSession iSoundTriggerSession;
        if (soundModelId == null || (iSoundTriggerSession = this.mSoundTriggerSession) == null) {
            return Integer.MIN_VALUE;
        }
        try {
            return iSoundTriggerSession.stopRecognitionForService(new ParcelUuid(soundModelId));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int unloadSoundModel(UUID soundModelId) {
        ISoundTriggerSession iSoundTriggerSession;
        if (soundModelId == null || (iSoundTriggerSession = this.mSoundTriggerSession) == null) {
            return Integer.MIN_VALUE;
        }
        try {
            return iSoundTriggerSession.unloadSoundModel(new ParcelUuid(soundModelId));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isRecognitionActive(UUID soundModelId) {
        ISoundTriggerSession iSoundTriggerSession;
        if (soundModelId == null || (iSoundTriggerSession = this.mSoundTriggerSession) == null) {
            return false;
        }
        try {
            return iSoundTriggerSession.isRecognitionActive(new ParcelUuid(soundModelId));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getDetectionServiceOperationsTimeout() {
        try {
            return Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.SOUND_TRIGGER_DETECTION_SERVICE_OP_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            return Integer.MAX_VALUE;
        }
    }

    public int getModelState(UUID soundModelId) {
        ISoundTriggerSession iSoundTriggerSession;
        if (soundModelId == null || (iSoundTriggerSession = this.mSoundTriggerSession) == null) {
            return Integer.MIN_VALUE;
        }
        try {
            return iSoundTriggerSession.getModelState(new ParcelUuid(soundModelId));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public SoundTrigger.ModuleProperties getModuleProperties() {
        ISoundTriggerSession iSoundTriggerSession = this.mSoundTriggerSession;
        if (iSoundTriggerSession == null) {
            return null;
        }
        try {
            return iSoundTriggerSession.getModuleProperties();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int setParameter(UUID soundModelId, int modelParam, int value) {
        ISoundTriggerSession iSoundTriggerSession = this.mSoundTriggerSession;
        if (iSoundTriggerSession == null) {
            return SoundTrigger.STATUS_INVALID_OPERATION;
        }
        try {
            return iSoundTriggerSession.setParameter(new ParcelUuid(soundModelId), modelParam, value);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getParameter(UUID soundModelId, int modelParam) {
        ISoundTriggerSession iSoundTriggerSession = this.mSoundTriggerSession;
        if (iSoundTriggerSession == null) {
            throw new IllegalArgumentException("Sound model is not loaded: " + soundModelId.toString());
        }
        try {
            return iSoundTriggerSession.getParameter(new ParcelUuid(soundModelId), modelParam);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public SoundTrigger.ModelParamRange queryParameter(UUID soundModelId, int modelParam) {
        ISoundTriggerSession iSoundTriggerSession = this.mSoundTriggerSession;
        if (iSoundTriggerSession == null) {
            return null;
        }
        try {
            return iSoundTriggerSession.queryParameter(new ParcelUuid(soundModelId), modelParam);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
