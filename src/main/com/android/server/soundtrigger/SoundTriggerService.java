package com.android.server.soundtrigger;

import android.app.ActivityThread;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.ModelParams;
import android.hardware.soundtrigger.SoundTrigger;
import android.hardware.soundtrigger.SoundTriggerModule;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.permission.ClearCallingIdentityContext;
import android.media.permission.Identity;
import android.media.permission.IdentityContext;
import android.media.permission.PermissionUtil;
import android.media.permission.SafeCloseable;
import android.media.soundtrigger.ISoundTriggerDetectionService;
import android.media.soundtrigger.ISoundTriggerDetectionServiceClient;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.ISoundTriggerService;
import com.android.internal.app.ISoundTriggerSession;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.SystemService;
import com.android.server.soundtrigger.SoundTriggerInternal;
import com.android.server.soundtrigger.SoundTriggerService;
import com.android.server.utils.EventLogger;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class SoundTriggerService extends SystemService {
    public static final EventLogger sEventLogger = new EventLogger(200, "SoundTrigger activity");
    public final Context mContext;
    public SoundTriggerDbHelper mDbHelper;
    public final LocalSoundTriggerService mLocalSoundTriggerService;
    public Object mLock;
    @GuardedBy({"mLock"})
    public final ArrayMap<String, NumOps> mNumOpsPerPackage;
    public final SoundTriggerServiceStub mServiceStub;
    public final SoundModelStatTracker mSoundModelStatTracker;

    /* loaded from: classes2.dex */
    public class SoundModelStatTracker {
        public final TreeMap<UUID, SoundModelStat> mModelStats = new TreeMap<>();

        /* loaded from: classes2.dex */
        public class SoundModelStat {
            public long mStartCount = 0;
            public long mTotalTimeMsec = 0;
            public long mLastStartTimestampMsec = 0;
            public long mLastStopTimestampMsec = 0;
            public boolean mIsStarted = false;

            public SoundModelStat() {
            }
        }

        public SoundModelStatTracker() {
        }

        public synchronized void onStart(UUID uuid) {
            SoundModelStat soundModelStat = this.mModelStats.get(uuid);
            if (soundModelStat == null) {
                soundModelStat = new SoundModelStat();
                this.mModelStats.put(uuid, soundModelStat);
            }
            if (soundModelStat.mIsStarted) {
                Slog.w("SoundTriggerService", "error onStart(): Model " + uuid + " already started");
                return;
            }
            soundModelStat.mStartCount++;
            soundModelStat.mLastStartTimestampMsec = SystemClock.elapsedRealtime();
            soundModelStat.mIsStarted = true;
        }

        public synchronized void onStop(UUID uuid) {
            SoundModelStat soundModelStat = this.mModelStats.get(uuid);
            if (soundModelStat == null) {
                Slog.w("SoundTriggerService", "error onStop(): Model " + uuid + " has no stats available");
            } else if (!soundModelStat.mIsStarted) {
                Slog.w("SoundTriggerService", "error onStop(): Model " + uuid + " already stopped");
            } else {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                soundModelStat.mLastStopTimestampMsec = elapsedRealtime;
                soundModelStat.mTotalTimeMsec += elapsedRealtime - soundModelStat.mLastStartTimestampMsec;
                soundModelStat.mIsStarted = false;
            }
        }

        public synchronized void dump(PrintWriter printWriter) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            printWriter.println("Model Stats:");
            for (Map.Entry<UUID, SoundModelStat> entry : this.mModelStats.entrySet()) {
                UUID key = entry.getKey();
                SoundModelStat value = entry.getValue();
                long j = value.mTotalTimeMsec;
                if (value.mIsStarted) {
                    j += elapsedRealtime - value.mLastStartTimestampMsec;
                }
                printWriter.println(key + ", total_time(msec)=" + j + ", total_count=" + value.mStartCount + ", last_start=" + value.mLastStartTimestampMsec + ", last_stop=" + value.mLastStopTimestampMsec);
            }
        }
    }

    public SoundTriggerService(Context context) {
        super(context);
        this.mNumOpsPerPackage = new ArrayMap<>();
        this.mContext = context;
        this.mServiceStub = new SoundTriggerServiceStub();
        this.mLocalSoundTriggerService = new LocalSoundTriggerService(context);
        this.mLock = new Object();
        this.mSoundModelStatTracker = new SoundModelStatTracker();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("soundtrigger", this.mServiceStub);
        publishLocalService(SoundTriggerInternal.class, this.mLocalSoundTriggerService);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        Slog.d("SoundTriggerService", "onBootPhase: " + i + " : " + isSafeMode());
        if (600 == i) {
            this.mDbHelper = new SoundTriggerDbHelper(this.mContext);
        }
    }

    public final SoundTriggerHelper newSoundTriggerHelper(SoundTrigger.ModuleProperties moduleProperties) {
        final Identity identity = new Identity();
        identity.packageName = ActivityThread.currentOpPackageName();
        final Identity nonNull = IdentityContext.getNonNull();
        ArrayList arrayList = new ArrayList();
        SoundTrigger.listModulesAsMiddleman(arrayList, identity, nonNull);
        final int id = moduleProperties != null ? moduleProperties.getId() : -1;
        if (id != -1 && !arrayList.contains(moduleProperties)) {
            throw new IllegalArgumentException("Invalid module properties");
        }
        return new SoundTriggerHelper(this.mContext, new Function() { // from class: com.android.server.soundtrigger.SoundTriggerService$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SoundTriggerModule attachModuleAsMiddleman;
                attachModuleAsMiddleman = SoundTrigger.attachModuleAsMiddleman(id, (SoundTrigger.StatusListener) obj, null, identity, nonNull);
                return attachModuleAsMiddleman;
            }
        }, id, new Supplier() { // from class: com.android.server.soundtrigger.SoundTriggerService$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                List lambda$newSoundTriggerHelper$1;
                lambda$newSoundTriggerHelper$1 = SoundTriggerService.lambda$newSoundTriggerHelper$1(identity, nonNull);
                return lambda$newSoundTriggerHelper$1;
            }
        });
    }

    public static /* synthetic */ List lambda$newSoundTriggerHelper$1(Identity identity, Identity identity2) {
        ArrayList arrayList = new ArrayList();
        SoundTrigger.listModulesAsMiddleman(arrayList, identity, identity2);
        return arrayList;
    }

    /* loaded from: classes2.dex */
    public class SoundTriggerServiceStub extends ISoundTriggerService.Stub {
        public SoundTriggerServiceStub() {
        }

        public ISoundTriggerSession attachAsOriginator(Identity identity, SoundTrigger.ModuleProperties moduleProperties, IBinder iBinder) {
            SafeCloseable establishIdentityDirect = PermissionUtil.establishIdentityDirect(identity);
            try {
                SoundTriggerService soundTriggerService = SoundTriggerService.this;
                SoundTriggerSessionStub soundTriggerSessionStub = new SoundTriggerSessionStub(iBinder, soundTriggerService.newSoundTriggerHelper(moduleProperties));
                if (establishIdentityDirect != null) {
                    establishIdentityDirect.close();
                }
                return soundTriggerSessionStub;
            } catch (Throwable th) {
                if (establishIdentityDirect != null) {
                    try {
                        establishIdentityDirect.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public ISoundTriggerSession attachAsMiddleman(Identity identity, Identity identity2, SoundTrigger.ModuleProperties moduleProperties, IBinder iBinder) {
            SafeCloseable establishIdentityIndirect = PermissionUtil.establishIdentityIndirect(SoundTriggerService.this.mContext, "android.permission.SOUNDTRIGGER_DELEGATE_IDENTITY", identity2, identity);
            try {
                SoundTriggerService soundTriggerService = SoundTriggerService.this;
                SoundTriggerSessionStub soundTriggerSessionStub = new SoundTriggerSessionStub(iBinder, soundTriggerService.newSoundTriggerHelper(moduleProperties));
                if (establishIdentityIndirect != null) {
                    establishIdentityIndirect.close();
                }
                return soundTriggerSessionStub;
            } catch (Throwable th) {
                if (establishIdentityIndirect != null) {
                    try {
                        establishIdentityIndirect.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public List<SoundTrigger.ModuleProperties> listModuleProperties(Identity identity) {
            SafeCloseable establishIdentityDirect = PermissionUtil.establishIdentityDirect(identity);
            try {
                Identity identity2 = new Identity();
                identity2.packageName = ActivityThread.currentOpPackageName();
                ArrayList arrayList = new ArrayList();
                SoundTrigger.listModulesAsMiddleman(arrayList, identity2, identity);
                if (establishIdentityDirect != null) {
                    establishIdentityDirect.close();
                }
                return arrayList;
            } catch (Throwable th) {
                if (establishIdentityDirect != null) {
                    try {
                        establishIdentityDirect.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }

    /* loaded from: classes2.dex */
    public class SoundTriggerSessionStub extends ISoundTriggerSession.Stub {
        public final IBinder mClient;
        public final SoundTriggerHelper mSoundTriggerHelper;
        public final TreeMap<UUID, SoundTrigger.SoundModel> mLoadedModels = new TreeMap<>();
        public final Object mCallbacksLock = new Object();
        public final TreeMap<UUID, IRecognitionStatusCallback> mCallbacks = new TreeMap<>();
        public final Identity mOriginatorIdentity = IdentityContext.getNonNull();

        public SoundTriggerSessionStub(IBinder iBinder, SoundTriggerHelper soundTriggerHelper) {
            this.mSoundTriggerHelper = soundTriggerHelper;
            this.mClient = iBinder;
            try {
                iBinder.linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.soundtrigger.SoundTriggerService$SoundTriggerSessionStub$$ExternalSyntheticLambda0
                    @Override // android.os.IBinder.DeathRecipient
                    public final void binderDied() {
                        SoundTriggerService.SoundTriggerSessionStub.this.lambda$new$0();
                    }
                }, 0);
            } catch (RemoteException e) {
                Slog.e("SoundTriggerService", "Failed to register death listener.", e);
            }
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            try {
                return super.onTransact(i, parcel, parcel2, i2);
            } catch (RuntimeException e) {
                if (!(e instanceof SecurityException)) {
                    Slog.wtf("SoundTriggerService", "SoundTriggerService Crash", e);
                }
                throw e;
            }
        }

        public int startRecognition(SoundTrigger.GenericSoundModel genericSoundModel, IRecognitionStatusCallback iRecognitionStatusCallback, SoundTrigger.RecognitionConfig recognitionConfig, boolean z) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                if (genericSoundModel == null) {
                    Slog.e("SoundTriggerService", "Null model passed to startRecognition");
                    if (create != null) {
                        create.close();
                        return Integer.MIN_VALUE;
                    }
                    return Integer.MIN_VALUE;
                }
                if (z) {
                    enforceCallingPermission("android.permission.SOUND_TRIGGER_RUN_IN_BATTERY_SAVER");
                }
                Slog.i("SoundTriggerService", "startRecognition(): Uuid : " + genericSoundModel.toString());
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("startRecognition(): Uuid : " + genericSoundModel.getUuid().toString()));
                int startGenericRecognition = this.mSoundTriggerHelper.startGenericRecognition(genericSoundModel.getUuid(), genericSoundModel, iRecognitionStatusCallback, recognitionConfig, z);
                if (startGenericRecognition == 0) {
                    SoundTriggerService.this.mSoundModelStatTracker.onStart(genericSoundModel.getUuid());
                }
                if (create != null) {
                    create.close();
                }
                return startGenericRecognition;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int stopRecognition(ParcelUuid parcelUuid, IRecognitionStatusCallback iRecognitionStatusCallback) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.i("SoundTriggerService", "stopRecognition(): Uuid : " + parcelUuid);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("stopRecognition(): Uuid : " + parcelUuid));
                int stopGenericRecognition = this.mSoundTriggerHelper.stopGenericRecognition(parcelUuid.getUuid(), iRecognitionStatusCallback);
                if (stopGenericRecognition == 0) {
                    SoundTriggerService.this.mSoundModelStatTracker.onStop(parcelUuid.getUuid());
                }
                if (create != null) {
                    create.close();
                }
                return stopGenericRecognition;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public SoundTrigger.GenericSoundModel getSoundModel(ParcelUuid parcelUuid) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.i("SoundTriggerService", "getSoundModel(): id = " + parcelUuid);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("getSoundModel(): id = " + parcelUuid));
                SoundTrigger.GenericSoundModel genericSoundModel = SoundTriggerService.this.mDbHelper.getGenericSoundModel(parcelUuid.getUuid());
                if (create != null) {
                    create.close();
                }
                return genericSoundModel;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public void updateSoundModel(SoundTrigger.GenericSoundModel genericSoundModel) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.i("SoundTriggerService", "updateSoundModel(): model = " + genericSoundModel);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("updateSoundModel(): model = " + genericSoundModel));
                SoundTriggerService.this.mDbHelper.updateGenericSoundModel(genericSoundModel);
                if (create != null) {
                    create.close();
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public void deleteSoundModel(ParcelUuid parcelUuid) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.i("SoundTriggerService", "deleteSoundModel(): id = " + parcelUuid);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("deleteSoundModel(): id = " + parcelUuid));
                this.mSoundTriggerHelper.unloadGenericSoundModel(parcelUuid.getUuid());
                SoundTriggerService.this.mSoundModelStatTracker.onStop(parcelUuid.getUuid());
                SoundTriggerService.this.mDbHelper.deleteGenericSoundModel(parcelUuid.getUuid());
                if (create != null) {
                    create.close();
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int loadGenericSoundModel(SoundTrigger.GenericSoundModel genericSoundModel) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                if (genericSoundModel != null && genericSoundModel.getUuid() != null) {
                    Slog.i("SoundTriggerService", "loadGenericSoundModel(): id = " + genericSoundModel.getUuid());
                    EventLogger eventLogger = SoundTriggerService.sEventLogger;
                    eventLogger.enqueue(new EventLogger.StringEvent("loadGenericSoundModel(): id = " + genericSoundModel.getUuid()));
                    synchronized (SoundTriggerService.this.mLock) {
                        SoundTrigger.SoundModel soundModel = this.mLoadedModels.get(genericSoundModel.getUuid());
                        if (soundModel != null && !soundModel.equals(genericSoundModel)) {
                            this.mSoundTriggerHelper.unloadGenericSoundModel(genericSoundModel.getUuid());
                            synchronized (this.mCallbacksLock) {
                                this.mCallbacks.remove(genericSoundModel.getUuid());
                            }
                        }
                        this.mLoadedModels.put(genericSoundModel.getUuid(), genericSoundModel);
                    }
                    if (create != null) {
                        create.close();
                        return 0;
                    }
                    return 0;
                }
                Slog.w("SoundTriggerService", "Invalid sound model");
                SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("loadGenericSoundModel(): Invalid sound model"));
                if (create != null) {
                    create.close();
                    return Integer.MIN_VALUE;
                }
                return Integer.MIN_VALUE;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int loadKeyphraseSoundModel(SoundTrigger.KeyphraseSoundModel keyphraseSoundModel) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                if (keyphraseSoundModel != null && keyphraseSoundModel.getUuid() != null) {
                    if (keyphraseSoundModel.getKeyphrases() != null && keyphraseSoundModel.getKeyphrases().length == 1) {
                        Slog.i("SoundTriggerService", "loadKeyphraseSoundModel(): id = " + keyphraseSoundModel.getUuid());
                        EventLogger eventLogger = SoundTriggerService.sEventLogger;
                        eventLogger.enqueue(new EventLogger.StringEvent("loadKeyphraseSoundModel(): id = " + keyphraseSoundModel.getUuid()));
                        synchronized (SoundTriggerService.this.mLock) {
                            SoundTrigger.SoundModel soundModel = this.mLoadedModels.get(keyphraseSoundModel.getUuid());
                            if (soundModel != null && !soundModel.equals(keyphraseSoundModel)) {
                                this.mSoundTriggerHelper.unloadKeyphraseSoundModel(keyphraseSoundModel.getKeyphrases()[0].getId());
                                synchronized (this.mCallbacksLock) {
                                    this.mCallbacks.remove(keyphraseSoundModel.getUuid());
                                }
                            }
                            this.mLoadedModels.put(keyphraseSoundModel.getUuid(), keyphraseSoundModel);
                        }
                        if (create != null) {
                            create.close();
                        }
                        return 0;
                    }
                    Slog.w("SoundTriggerService", "Only one keyphrase per model is currently supported.");
                    SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("loadKeyphraseSoundModel(): Only one keyphrase per model is currently supported."));
                    if (create != null) {
                        create.close();
                    }
                    return Integer.MIN_VALUE;
                }
                Slog.w("SoundTriggerService", "Invalid sound model");
                SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("loadKeyphraseSoundModel(): Invalid sound model"));
                if (create != null) {
                    create.close();
                }
                return Integer.MIN_VALUE;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int startRecognitionForService(ParcelUuid parcelUuid, Bundle bundle, ComponentName componentName, SoundTrigger.RecognitionConfig recognitionConfig) {
            IRecognitionStatusCallback iRecognitionStatusCallback;
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                Objects.requireNonNull(parcelUuid);
                Objects.requireNonNull(componentName);
                Objects.requireNonNull(recognitionConfig);
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                enforceDetectionPermissions(componentName);
                Slog.i("SoundTriggerService", "startRecognition(): id = " + parcelUuid);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("startRecognitionForService(): id = " + parcelUuid));
                IRecognitionStatusCallback remoteSoundTriggerDetectionService = new RemoteSoundTriggerDetectionService(parcelUuid.getUuid(), bundle, componentName, Binder.getCallingUserHandle(), recognitionConfig);
                synchronized (SoundTriggerService.this.mLock) {
                    SoundTrigger.GenericSoundModel genericSoundModel = (SoundTrigger.SoundModel) this.mLoadedModels.get(parcelUuid.getUuid());
                    if (genericSoundModel == null) {
                        Slog.w("SoundTriggerService", parcelUuid + " is not loaded");
                        EventLogger eventLogger2 = SoundTriggerService.sEventLogger;
                        eventLogger2.enqueue(new EventLogger.StringEvent("startRecognitionForService():" + parcelUuid + " is not loaded"));
                        if (create != null) {
                            create.close();
                        }
                        return Integer.MIN_VALUE;
                    }
                    synchronized (this.mCallbacksLock) {
                        iRecognitionStatusCallback = this.mCallbacks.get(parcelUuid.getUuid());
                    }
                    if (iRecognitionStatusCallback != null) {
                        Slog.w("SoundTriggerService", parcelUuid + " is already running");
                        EventLogger eventLogger3 = SoundTriggerService.sEventLogger;
                        eventLogger3.enqueue(new EventLogger.StringEvent("startRecognitionForService():" + parcelUuid + " is already running"));
                        if (create != null) {
                            create.close();
                        }
                        return Integer.MIN_VALUE;
                    } else if (genericSoundModel.getType() != 1) {
                        Slog.e("SoundTriggerService", "Unknown model type");
                        SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("startRecognitionForService(): Unknown model type"));
                        if (create != null) {
                            create.close();
                        }
                        return Integer.MIN_VALUE;
                    } else {
                        int startGenericRecognition = this.mSoundTriggerHelper.startGenericRecognition(genericSoundModel.getUuid(), genericSoundModel, remoteSoundTriggerDetectionService, recognitionConfig, false);
                        if (startGenericRecognition != 0) {
                            Slog.e("SoundTriggerService", "Failed to start model: " + startGenericRecognition);
                            SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("startRecognitionForService(): Failed to start model:"));
                            if (create != null) {
                                create.close();
                            }
                            return startGenericRecognition;
                        }
                        synchronized (this.mCallbacksLock) {
                            this.mCallbacks.put(parcelUuid.getUuid(), remoteSoundTriggerDetectionService);
                        }
                        SoundTriggerService.this.mSoundModelStatTracker.onStart(parcelUuid.getUuid());
                        if (create != null) {
                            create.close();
                            return 0;
                        }
                        return 0;
                    }
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int stopRecognitionForService(ParcelUuid parcelUuid) {
            IRecognitionStatusCallback iRecognitionStatusCallback;
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.i("SoundTriggerService", "stopRecognition(): id = " + parcelUuid);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("stopRecognitionForService(): id = " + parcelUuid));
                synchronized (SoundTriggerService.this.mLock) {
                    SoundTrigger.SoundModel soundModel = this.mLoadedModels.get(parcelUuid.getUuid());
                    if (soundModel == null) {
                        Slog.w("SoundTriggerService", parcelUuid + " is not loaded");
                        EventLogger eventLogger2 = SoundTriggerService.sEventLogger;
                        eventLogger2.enqueue(new EventLogger.StringEvent("stopRecognitionForService(): " + parcelUuid + " is not loaded"));
                        if (create != null) {
                            create.close();
                        }
                        return Integer.MIN_VALUE;
                    }
                    synchronized (this.mCallbacksLock) {
                        iRecognitionStatusCallback = this.mCallbacks.get(parcelUuid.getUuid());
                    }
                    if (iRecognitionStatusCallback == null) {
                        Slog.w("SoundTriggerService", parcelUuid + " is not running");
                        EventLogger eventLogger3 = SoundTriggerService.sEventLogger;
                        eventLogger3.enqueue(new EventLogger.StringEvent("stopRecognitionForService(): " + parcelUuid + " is not running"));
                        if (create != null) {
                            create.close();
                        }
                        return Integer.MIN_VALUE;
                    } else if (soundModel.getType() != 1) {
                        Slog.e("SoundTriggerService", "Unknown model type");
                        SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("stopRecognitionForService(): Unknown model type"));
                        if (create != null) {
                            create.close();
                        }
                        return Integer.MIN_VALUE;
                    } else {
                        int stopGenericRecognition = this.mSoundTriggerHelper.stopGenericRecognition(soundModel.getUuid(), iRecognitionStatusCallback);
                        if (stopGenericRecognition == 0) {
                            synchronized (this.mCallbacksLock) {
                                this.mCallbacks.remove(parcelUuid.getUuid());
                            }
                            SoundTriggerService.this.mSoundModelStatTracker.onStop(parcelUuid.getUuid());
                            if (create != null) {
                                create.close();
                                return 0;
                            }
                            return 0;
                        }
                        Slog.e("SoundTriggerService", "Failed to stop model: " + stopGenericRecognition);
                        EventLogger eventLogger4 = SoundTriggerService.sEventLogger;
                        eventLogger4.enqueue(new EventLogger.StringEvent("stopRecognitionForService(): Failed to stop model: " + stopGenericRecognition));
                        if (create != null) {
                            create.close();
                        }
                        return stopGenericRecognition;
                    }
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int unloadSoundModel(ParcelUuid parcelUuid) {
            int unloadKeyphraseSoundModel;
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.i("SoundTriggerService", "unloadSoundModel(): id = " + parcelUuid);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("unloadSoundModel(): id = " + parcelUuid));
                synchronized (SoundTriggerService.this.mLock) {
                    SoundTrigger.KeyphraseSoundModel keyphraseSoundModel = (SoundTrigger.SoundModel) this.mLoadedModels.get(parcelUuid.getUuid());
                    if (keyphraseSoundModel == null) {
                        Slog.w("SoundTriggerService", parcelUuid + " is not loaded");
                        EventLogger eventLogger2 = SoundTriggerService.sEventLogger;
                        eventLogger2.enqueue(new EventLogger.StringEvent("unloadSoundModel(): " + parcelUuid + " is not loaded"));
                        if (create != null) {
                            create.close();
                        }
                        return Integer.MIN_VALUE;
                    }
                    int type = keyphraseSoundModel.getType();
                    if (type == 0) {
                        unloadKeyphraseSoundModel = this.mSoundTriggerHelper.unloadKeyphraseSoundModel(keyphraseSoundModel.getKeyphrases()[0].getId());
                    } else if (type != 1) {
                        Slog.e("SoundTriggerService", "Unknown model type");
                        SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("unloadSoundModel(): Unknown model type"));
                        if (create != null) {
                            create.close();
                        }
                        return Integer.MIN_VALUE;
                    } else {
                        unloadKeyphraseSoundModel = this.mSoundTriggerHelper.unloadGenericSoundModel(keyphraseSoundModel.getUuid());
                    }
                    if (unloadKeyphraseSoundModel == 0) {
                        this.mLoadedModels.remove(parcelUuid.getUuid());
                        if (create != null) {
                            create.close();
                        }
                        return 0;
                    }
                    Slog.e("SoundTriggerService", "Failed to unload model");
                    SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("unloadSoundModel(): Failed to unload model"));
                    if (create != null) {
                        create.close();
                    }
                    return unloadKeyphraseSoundModel;
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public boolean isRecognitionActive(ParcelUuid parcelUuid) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                synchronized (this.mCallbacksLock) {
                    if (this.mCallbacks.get(parcelUuid.getUuid()) == null) {
                        if (create != null) {
                            create.close();
                            return false;
                        }
                        return false;
                    }
                    boolean isRecognitionRequested = this.mSoundTriggerHelper.isRecognitionRequested(parcelUuid.getUuid());
                    if (create != null) {
                        create.close();
                    }
                    return isRecognitionRequested;
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int getModelState(ParcelUuid parcelUuid) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.i("SoundTriggerService", "getModelState(): id = " + parcelUuid);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("getModelState(): id = " + parcelUuid));
                synchronized (SoundTriggerService.this.mLock) {
                    SoundTrigger.SoundModel soundModel = this.mLoadedModels.get(parcelUuid.getUuid());
                    int i = Integer.MIN_VALUE;
                    if (soundModel == null) {
                        Slog.w("SoundTriggerService", parcelUuid + " is not loaded");
                        EventLogger eventLogger2 = SoundTriggerService.sEventLogger;
                        eventLogger2.enqueue(new EventLogger.StringEvent("getModelState(): " + parcelUuid + " is not loaded"));
                        if (create != null) {
                            create.close();
                        }
                        return Integer.MIN_VALUE;
                    }
                    if (soundModel.getType() == 1) {
                        i = this.mSoundTriggerHelper.getGenericModelState(soundModel.getUuid());
                    } else {
                        Slog.e("SoundTriggerService", "Unsupported model type, " + soundModel.getType());
                        EventLogger eventLogger3 = SoundTriggerService.sEventLogger;
                        eventLogger3.enqueue(new EventLogger.StringEvent("getModelState(): Unsupported model type, " + soundModel.getType()));
                    }
                    if (create != null) {
                        create.close();
                    }
                    return i;
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public SoundTrigger.ModuleProperties getModuleProperties() {
            SoundTrigger.ModuleProperties moduleProperties;
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.i("SoundTriggerService", "getModuleProperties()");
                synchronized (SoundTriggerService.this.mLock) {
                    moduleProperties = this.mSoundTriggerHelper.getModuleProperties();
                    EventLogger eventLogger = SoundTriggerService.sEventLogger;
                    eventLogger.enqueue(new EventLogger.StringEvent("getModuleProperties(): " + moduleProperties));
                }
                if (create != null) {
                    create.close();
                }
                return moduleProperties;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int setParameter(ParcelUuid parcelUuid, @ModelParams int i, int i2) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.d("SoundTriggerService", "setParameter(): id=" + parcelUuid + ", param=" + i + ", value=" + i2);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("setParameter(): id=" + parcelUuid + ", param=" + i + ", value=" + i2));
                synchronized (SoundTriggerService.this.mLock) {
                    SoundTrigger.SoundModel soundModel = this.mLoadedModels.get(parcelUuid.getUuid());
                    if (soundModel == null) {
                        Slog.w("SoundTriggerService", parcelUuid + " is not loaded. Loaded models: " + this.mLoadedModels.toString());
                        EventLogger eventLogger2 = SoundTriggerService.sEventLogger;
                        eventLogger2.enqueue(new EventLogger.StringEvent("setParameter(): " + parcelUuid + " is not loaded"));
                        int i3 = SoundTrigger.STATUS_BAD_VALUE;
                        if (create != null) {
                            create.close();
                        }
                        return i3;
                    }
                    int parameter = this.mSoundTriggerHelper.setParameter(soundModel.getUuid(), i, i2);
                    if (create != null) {
                        create.close();
                    }
                    return parameter;
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public int getParameter(ParcelUuid parcelUuid, @ModelParams int i) throws UnsupportedOperationException, IllegalArgumentException {
            int parameter;
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.d("SoundTriggerService", "getParameter(): id=" + parcelUuid + ", param=" + i);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("getParameter(): id=" + parcelUuid + ", param=" + i));
                synchronized (SoundTriggerService.this.mLock) {
                    SoundTrigger.SoundModel soundModel = this.mLoadedModels.get(parcelUuid.getUuid());
                    if (soundModel == null) {
                        Slog.w("SoundTriggerService", parcelUuid + " is not loaded");
                        EventLogger eventLogger2 = SoundTriggerService.sEventLogger;
                        eventLogger2.enqueue(new EventLogger.StringEvent("getParameter(): " + parcelUuid + " is not loaded"));
                        throw new IllegalArgumentException("sound model is not loaded");
                    }
                    parameter = this.mSoundTriggerHelper.getParameter(soundModel.getUuid(), i);
                }
                if (create != null) {
                    create.close();
                }
                return parameter;
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        public SoundTrigger.ModelParamRange queryParameter(ParcelUuid parcelUuid, @ModelParams int i) {
            SafeCloseable create = ClearCallingIdentityContext.create();
            try {
                enforceCallingPermission("android.permission.MANAGE_SOUND_TRIGGER");
                Slog.d("SoundTriggerService", "queryParameter(): id=" + parcelUuid + ", param=" + i);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("queryParameter(): id=" + parcelUuid + ", param=" + i));
                synchronized (SoundTriggerService.this.mLock) {
                    SoundTrigger.SoundModel soundModel = this.mLoadedModels.get(parcelUuid.getUuid());
                    if (soundModel == null) {
                        Slog.w("SoundTriggerService", parcelUuid + " is not loaded");
                        EventLogger eventLogger2 = SoundTriggerService.sEventLogger;
                        eventLogger2.enqueue(new EventLogger.StringEvent("queryParameter(): " + parcelUuid + " is not loaded"));
                        if (create != null) {
                            create.close();
                            return null;
                        }
                        return null;
                    }
                    SoundTrigger.ModelParamRange queryParameter = this.mSoundTriggerHelper.queryParameter(soundModel.getUuid(), i);
                    if (create != null) {
                        create.close();
                    }
                    return queryParameter;
                }
            } catch (Throwable th) {
                if (create != null) {
                    try {
                        create.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        /* renamed from: clientDied */
        public final void lambda$new$0() {
            Slog.w("SoundTriggerService", "Client died, cleaning up session.");
            SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("Client died, cleaning up session."));
            this.mSoundTriggerHelper.detach();
        }

        public final void enforceCallingPermission(String str) {
            if (PermissionUtil.checkPermissionForPreflight(SoundTriggerService.this.mContext, this.mOriginatorIdentity, str) == 0) {
                return;
            }
            throw new SecurityException("Identity " + this.mOriginatorIdentity + " does not have permission " + str);
        }

        public final void enforceDetectionPermissions(ComponentName componentName) {
            if (SoundTriggerService.this.mContext.getPackageManager().checkPermission("android.permission.CAPTURE_AUDIO_HOTWORD", componentName.getPackageName()) == 0) {
                return;
            }
            throw new SecurityException(componentName.getPackageName() + " does not have permission android.permission.CAPTURE_AUDIO_HOTWORD");
        }

        /* loaded from: classes2.dex */
        public class RemoteSoundTriggerDetectionService extends IRecognitionStatusCallback.Stub implements ServiceConnection {
            public final ISoundTriggerDetectionServiceClient mClient;
            @GuardedBy({"mRemoteServiceLock"})
            public boolean mDestroyOnceRunningOpsDone;
            @GuardedBy({"mRemoteServiceLock"})
            public boolean mIsBound;
            @GuardedBy({"mRemoteServiceLock"})
            public boolean mIsDestroyed;
            public final NumOps mNumOps;
            @GuardedBy({"mRemoteServiceLock"})
            public int mNumTotalOpsPerformed;
            public final Bundle mParams;
            public final ParcelUuid mPuuid;
            public final SoundTrigger.RecognitionConfig mRecognitionConfig;
            public final PowerManager.WakeLock mRemoteServiceWakeLock;
            @GuardedBy({"mRemoteServiceLock"})
            public ISoundTriggerDetectionService mService;
            public final ComponentName mServiceName;
            public final UserHandle mUser;
            public final Object mRemoteServiceLock = new Object();
            @GuardedBy({"mRemoteServiceLock"})
            public final ArrayList<Operation> mPendingOps = new ArrayList<>();
            @GuardedBy({"mRemoteServiceLock"})
            public final ArraySet<Integer> mRunningOpIds = new ArraySet<>();
            public final Handler mHandler = new Handler(Looper.getMainLooper());

            public RemoteSoundTriggerDetectionService(UUID uuid, Bundle bundle, ComponentName componentName, UserHandle userHandle, SoundTrigger.RecognitionConfig recognitionConfig) {
                this.mPuuid = new ParcelUuid(uuid);
                this.mParams = bundle;
                this.mServiceName = componentName;
                this.mUser = userHandle;
                this.mRecognitionConfig = recognitionConfig;
                this.mRemoteServiceWakeLock = ((PowerManager) SoundTriggerService.this.mContext.getSystemService("power")).newWakeLock(1, "RemoteSoundTriggerDetectionService " + componentName.getPackageName() + XmlUtils.STRING_ARRAY_SEPARATOR + componentName.getClassName());
                synchronized (SoundTriggerService.this.mLock) {
                    NumOps numOps = (NumOps) SoundTriggerService.this.mNumOpsPerPackage.get(componentName.getPackageName());
                    if (numOps == null) {
                        numOps = new NumOps();
                        SoundTriggerService.this.mNumOpsPerPackage.put(componentName.getPackageName(), numOps);
                    }
                    this.mNumOps = numOps;
                }
                this.mClient = new ISoundTriggerDetectionServiceClient.Stub() { // from class: com.android.server.soundtrigger.SoundTriggerService.SoundTriggerSessionStub.RemoteSoundTriggerDetectionService.1
                    public void onOpFinished(int i) {
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        try {
                            synchronized (RemoteSoundTriggerDetectionService.this.mRemoteServiceLock) {
                                RemoteSoundTriggerDetectionService.this.mRunningOpIds.remove(Integer.valueOf(i));
                                if (RemoteSoundTriggerDetectionService.this.mRunningOpIds.isEmpty() && RemoteSoundTriggerDetectionService.this.mPendingOps.isEmpty()) {
                                    if (RemoteSoundTriggerDetectionService.this.mDestroyOnceRunningOpsDone) {
                                        RemoteSoundTriggerDetectionService.this.destroy();
                                    } else {
                                        RemoteSoundTriggerDetectionService.this.disconnectLocked();
                                    }
                                }
                            }
                        } finally {
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                        }
                    }
                };
            }

            public boolean pingBinder() {
                return (this.mIsDestroyed || this.mDestroyOnceRunningOpsDone) ? false : true;
            }

            @GuardedBy({"mRemoteServiceLock"})
            public final void disconnectLocked() {
                ISoundTriggerDetectionService iSoundTriggerDetectionService = this.mService;
                if (iSoundTriggerDetectionService != null) {
                    try {
                        iSoundTriggerDetectionService.removeClient(this.mPuuid);
                    } catch (Exception e) {
                        Slog.e("SoundTriggerService", this.mPuuid + ": Cannot remove client", e);
                        EventLogger eventLogger = SoundTriggerService.sEventLogger;
                        eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": Cannot remove client"));
                    }
                    this.mService = null;
                }
                if (this.mIsBound) {
                    SoundTriggerService.this.mContext.unbindService(this);
                    this.mIsBound = false;
                    synchronized (SoundTriggerSessionStub.this.mCallbacksLock) {
                        this.mRemoteServiceWakeLock.release();
                    }
                }
            }

            public final void destroy() {
                Slog.v("SoundTriggerService", this.mPuuid + ": destroy");
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": destroy"));
                synchronized (this.mRemoteServiceLock) {
                    disconnectLocked();
                    this.mIsDestroyed = true;
                }
                if (this.mDestroyOnceRunningOpsDone) {
                    return;
                }
                synchronized (SoundTriggerSessionStub.this.mCallbacksLock) {
                    SoundTriggerSessionStub.this.mCallbacks.remove(this.mPuuid.getUuid());
                }
            }

            public final void stopAllPendingOperations() {
                synchronized (this.mRemoteServiceLock) {
                    if (this.mIsDestroyed) {
                        return;
                    }
                    if (this.mService != null) {
                        int size = this.mRunningOpIds.size();
                        for (int i = 0; i < size; i++) {
                            try {
                                this.mService.onStopOperation(this.mPuuid, this.mRunningOpIds.valueAt(i).intValue());
                            } catch (Exception e) {
                                Slog.e("SoundTriggerService", this.mPuuid + ": Could not stop operation " + this.mRunningOpIds.valueAt(i), e);
                                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                                eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": Could not stop operation " + this.mRunningOpIds.valueAt(i)));
                            }
                        }
                        this.mRunningOpIds.clear();
                    }
                    disconnectLocked();
                }
            }

            public final void bind() {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    Intent intent = new Intent();
                    intent.setComponent(this.mServiceName);
                    ResolveInfo resolveServiceAsUser = SoundTriggerService.this.mContext.getPackageManager().resolveServiceAsUser(intent, 268435588, this.mUser.getIdentifier());
                    if (resolveServiceAsUser == null) {
                        Slog.w("SoundTriggerService", this.mPuuid + ": " + this.mServiceName + " not found");
                        EventLogger eventLogger = SoundTriggerService.sEventLogger;
                        eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": " + this.mServiceName + " not found"));
                    } else if (!"android.permission.BIND_SOUND_TRIGGER_DETECTION_SERVICE".equals(resolveServiceAsUser.serviceInfo.permission)) {
                        Slog.w("SoundTriggerService", this.mPuuid + ": " + this.mServiceName + " does not require android.permission.BIND_SOUND_TRIGGER_DETECTION_SERVICE");
                        EventLogger eventLogger2 = SoundTriggerService.sEventLogger;
                        eventLogger2.enqueue(new EventLogger.StringEvent(this.mPuuid + ": " + this.mServiceName + " does not require android.permission.BIND_SOUND_TRIGGER_DETECTION_SERVICE"));
                    } else {
                        boolean bindServiceAsUser = SoundTriggerService.this.mContext.bindServiceAsUser(intent, this, 67112961, this.mUser);
                        this.mIsBound = bindServiceAsUser;
                        if (bindServiceAsUser) {
                            this.mRemoteServiceWakeLock.acquire();
                        } else {
                            Slog.w("SoundTriggerService", this.mPuuid + ": Could not bind to " + this.mServiceName);
                            EventLogger eventLogger3 = SoundTriggerService.sEventLogger;
                            eventLogger3.enqueue(new EventLogger.StringEvent(this.mPuuid + ": Could not bind to " + this.mServiceName));
                        }
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }

            public final void runOrAddOperation(Operation operation) {
                synchronized (this.mRemoteServiceLock) {
                    if (!this.mIsDestroyed && !this.mDestroyOnceRunningOpsDone) {
                        if (this.mService == null) {
                            this.mPendingOps.add(operation);
                            if (!this.mIsBound) {
                                bind();
                            }
                        } else {
                            long nanoTime = System.nanoTime();
                            this.mNumOps.clearOldOps(nanoTime);
                            Settings.Global.getInt(SoundTriggerService.this.mContext.getContentResolver(), "max_sound_trigger_detection_service_ops_per_day", Integer.MAX_VALUE);
                            this.mNumOps.getOpsAdded();
                            this.mNumOps.addOp(nanoTime);
                            int i = this.mNumTotalOpsPerformed;
                            do {
                                this.mNumTotalOpsPerformed++;
                            } while (this.mRunningOpIds.contains(Integer.valueOf(i)));
                            try {
                                Slog.v("SoundTriggerService", this.mPuuid + ": runOp " + i);
                                SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": runOp " + i));
                                operation.run(i, this.mService);
                                this.mRunningOpIds.add(Integer.valueOf(i));
                            } catch (Exception e) {
                                Slog.e("SoundTriggerService", this.mPuuid + ": Could not run operation " + i, e);
                                SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": Could not run operation " + i));
                            }
                            if (this.mPendingOps.isEmpty() && this.mRunningOpIds.isEmpty()) {
                                if (this.mDestroyOnceRunningOpsDone) {
                                    destroy();
                                } else {
                                    disconnectLocked();
                                }
                            } else {
                                this.mHandler.removeMessages(1);
                                this.mHandler.sendMessageDelayed(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.soundtrigger.SoundTriggerService$SoundTriggerSessionStub$RemoteSoundTriggerDetectionService$$ExternalSyntheticLambda5
                                    @Override // java.util.function.Consumer
                                    public final void accept(Object obj) {
                                        ((SoundTriggerService.SoundTriggerSessionStub.RemoteSoundTriggerDetectionService) obj).stopAllPendingOperations();
                                    }
                                }, this).setWhat(1), Settings.Global.getLong(SoundTriggerService.this.mContext.getContentResolver(), "sound_trigger_detection_service_op_timeout", Long.MAX_VALUE));
                            }
                        }
                        return;
                    }
                    Slog.w("SoundTriggerService", this.mPuuid + ": Dropped operation as already destroyed or marked for destruction");
                    SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ":Dropped operation as already destroyed or marked for destruction"));
                    operation.drop();
                }
            }

            public void onKeyphraseDetected(SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent) {
                Slog.w("SoundTriggerService", this.mPuuid + "->" + this.mServiceName + ": IGNORED onKeyphraseDetected(" + keyphraseRecognitionEvent + ")");
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + "->" + this.mServiceName + ": IGNORED onKeyphraseDetected(" + keyphraseRecognitionEvent + ")"));
            }

            public final AudioRecord createAudioRecordForEvent(SoundTrigger.GenericRecognitionEvent genericRecognitionEvent) throws IllegalArgumentException, UnsupportedOperationException {
                AudioAttributes.Builder builder = new AudioAttributes.Builder();
                builder.setInternalCapturePreset(1999);
                AudioAttributes build = builder.build();
                AudioFormat captureFormat = genericRecognitionEvent.getCaptureFormat();
                SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("createAudioRecordForEvent"));
                return new AudioRecord.Builder().setAudioAttributes(build).setAudioFormat(new AudioFormat.Builder().setChannelMask(captureFormat.getChannelMask()).setEncoding(captureFormat.getEncoding()).setSampleRate(captureFormat.getSampleRate()).build()).setSessionId(genericRecognitionEvent.getCaptureSession()).build();
            }

            public void onGenericSoundTriggerDetected(final SoundTrigger.GenericRecognitionEvent genericRecognitionEvent) {
                Slog.v("SoundTriggerService", this.mPuuid + ": Generic sound trigger event: " + genericRecognitionEvent);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": Generic sound trigger event: " + genericRecognitionEvent));
                runOrAddOperation(new Operation(new Runnable() { // from class: com.android.server.soundtrigger.SoundTriggerService$SoundTriggerSessionStub$RemoteSoundTriggerDetectionService$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SoundTriggerService.SoundTriggerSessionStub.RemoteSoundTriggerDetectionService.this.lambda$onGenericSoundTriggerDetected$0();
                    }
                }, new Operation.ExecuteOp() { // from class: com.android.server.soundtrigger.SoundTriggerService$SoundTriggerSessionStub$RemoteSoundTriggerDetectionService$$ExternalSyntheticLambda1
                    @Override // com.android.server.soundtrigger.SoundTriggerService.Operation.ExecuteOp
                    public final void run(int i, ISoundTriggerDetectionService iSoundTriggerDetectionService) {
                        SoundTriggerService.SoundTriggerSessionStub.RemoteSoundTriggerDetectionService.this.lambda$onGenericSoundTriggerDetected$1(genericRecognitionEvent, i, iSoundTriggerDetectionService);
                    }
                }, new Runnable() { // from class: com.android.server.soundtrigger.SoundTriggerService$SoundTriggerSessionStub$RemoteSoundTriggerDetectionService$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        SoundTriggerService.SoundTriggerSessionStub.RemoteSoundTriggerDetectionService.this.lambda$onGenericSoundTriggerDetected$2(genericRecognitionEvent);
                    }
                }));
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onGenericSoundTriggerDetected$0() {
                if (this.mRecognitionConfig.allowMultipleTriggers) {
                    return;
                }
                synchronized (SoundTriggerSessionStub.this.mCallbacksLock) {
                    SoundTriggerSessionStub.this.mCallbacks.remove(this.mPuuid.getUuid());
                }
                this.mDestroyOnceRunningOpsDone = true;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onGenericSoundTriggerDetected$1(SoundTrigger.GenericRecognitionEvent genericRecognitionEvent, int i, ISoundTriggerDetectionService iSoundTriggerDetectionService) throws RemoteException {
                iSoundTriggerDetectionService.onGenericRecognitionEvent(this.mPuuid, i, genericRecognitionEvent);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onGenericSoundTriggerDetected$2(SoundTrigger.GenericRecognitionEvent genericRecognitionEvent) {
                if (genericRecognitionEvent.isCaptureAvailable()) {
                    try {
                        AudioRecord createAudioRecordForEvent = createAudioRecordForEvent(genericRecognitionEvent);
                        createAudioRecordForEvent.startRecording();
                        createAudioRecordForEvent.release();
                    } catch (IllegalArgumentException | UnsupportedOperationException unused) {
                        Slog.w("SoundTriggerService", this.mPuuid + ": createAudioRecordForEvent(" + genericRecognitionEvent + "), failed to create AudioRecord");
                    }
                }
            }

            public void onError(final int i) {
                Slog.v("SoundTriggerService", this.mPuuid + ": onError: " + i);
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": onError: " + i));
                runOrAddOperation(new Operation(new Runnable() { // from class: com.android.server.soundtrigger.SoundTriggerService$SoundTriggerSessionStub$RemoteSoundTriggerDetectionService$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        SoundTriggerService.SoundTriggerSessionStub.RemoteSoundTriggerDetectionService.this.lambda$onError$3();
                    }
                }, new Operation.ExecuteOp() { // from class: com.android.server.soundtrigger.SoundTriggerService$SoundTriggerSessionStub$RemoteSoundTriggerDetectionService$$ExternalSyntheticLambda4
                    @Override // com.android.server.soundtrigger.SoundTriggerService.Operation.ExecuteOp
                    public final void run(int i2, ISoundTriggerDetectionService iSoundTriggerDetectionService) {
                        SoundTriggerService.SoundTriggerSessionStub.RemoteSoundTriggerDetectionService.this.lambda$onError$4(i, i2, iSoundTriggerDetectionService);
                    }
                }, null));
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onError$3() {
                synchronized (SoundTriggerSessionStub.this.mCallbacksLock) {
                    SoundTriggerSessionStub.this.mCallbacks.remove(this.mPuuid.getUuid());
                }
                this.mDestroyOnceRunningOpsDone = true;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onError$4(int i, int i2, ISoundTriggerDetectionService iSoundTriggerDetectionService) throws RemoteException {
                iSoundTriggerDetectionService.onError(this.mPuuid, i2, i);
            }

            public void onRecognitionPaused() {
                Slog.i("SoundTriggerService", this.mPuuid + "->" + this.mServiceName + ": IGNORED onRecognitionPaused");
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + "->" + this.mServiceName + ": IGNORED onRecognitionPaused"));
            }

            public void onRecognitionResumed() {
                Slog.i("SoundTriggerService", this.mPuuid + "->" + this.mServiceName + ": IGNORED onRecognitionResumed");
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + "->" + this.mServiceName + ": IGNORED onRecognitionResumed"));
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Slog.v("SoundTriggerService", this.mPuuid + ": onServiceConnected(" + iBinder + ")");
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": onServiceConnected(" + iBinder + ")"));
                synchronized (this.mRemoteServiceLock) {
                    ISoundTriggerDetectionService asInterface = ISoundTriggerDetectionService.Stub.asInterface(iBinder);
                    this.mService = asInterface;
                    try {
                        asInterface.setClient(this.mPuuid, this.mParams, this.mClient);
                        while (!this.mPendingOps.isEmpty()) {
                            runOrAddOperation(this.mPendingOps.remove(0));
                        }
                    } catch (Exception e) {
                        Slog.e("SoundTriggerService", this.mPuuid + ": Could not init " + this.mServiceName, e);
                    }
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                Slog.v("SoundTriggerService", this.mPuuid + ": onServiceDisconnected");
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": onServiceDisconnected"));
                synchronized (this.mRemoteServiceLock) {
                    this.mService = null;
                }
            }

            @Override // android.content.ServiceConnection
            public void onBindingDied(ComponentName componentName) {
                Slog.v("SoundTriggerService", this.mPuuid + ": onBindingDied");
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent(this.mPuuid + ": onBindingDied"));
                synchronized (this.mRemoteServiceLock) {
                    destroy();
                }
            }

            @Override // android.content.ServiceConnection
            public void onNullBinding(ComponentName componentName) {
                Slog.w("SoundTriggerService", componentName + " for model " + this.mPuuid + " returned a null binding");
                EventLogger eventLogger = SoundTriggerService.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent(componentName + " for model " + this.mPuuid + " returned a null binding"));
                synchronized (this.mRemoteServiceLock) {
                    disconnectLocked();
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class NumOps {
        @GuardedBy({"mLock"})
        public long mLastOpsHourSinceBoot;
        public final Object mLock;
        @GuardedBy({"mLock"})
        public int[] mNumOps;

        public NumOps() {
            this.mLock = new Object();
            this.mNumOps = new int[24];
        }

        public void clearOldOps(long j) {
            synchronized (this.mLock) {
                long convert = TimeUnit.HOURS.convert(j, TimeUnit.NANOSECONDS);
                long j2 = this.mLastOpsHourSinceBoot;
                if (j2 != 0) {
                    while (true) {
                        j2++;
                        if (j2 > convert) {
                            break;
                        }
                        this.mNumOps[(int) (j2 % 24)] = 0;
                    }
                }
            }
        }

        public void addOp(long j) {
            synchronized (this.mLock) {
                long convert = TimeUnit.HOURS.convert(j, TimeUnit.NANOSECONDS);
                int[] iArr = this.mNumOps;
                int i = (int) (convert % 24);
                iArr[i] = iArr[i] + 1;
                this.mLastOpsHourSinceBoot = convert;
            }
        }

        public int getOpsAdded() {
            int i;
            synchronized (this.mLock) {
                i = 0;
                for (int i2 = 0; i2 < 24; i2++) {
                    i += this.mNumOps[i2];
                }
            }
            return i;
        }
    }

    /* loaded from: classes2.dex */
    public static class Operation {
        public final Runnable mDropOp;
        public final ExecuteOp mExecuteOp;
        public final Runnable mSetupOp;

        /* loaded from: classes2.dex */
        public interface ExecuteOp {
            void run(int i, ISoundTriggerDetectionService iSoundTriggerDetectionService) throws RemoteException;
        }

        public Operation(Runnable runnable, ExecuteOp executeOp, Runnable runnable2) {
            this.mSetupOp = runnable;
            this.mExecuteOp = executeOp;
            this.mDropOp = runnable2;
        }

        public final void setup() {
            Runnable runnable = this.mSetupOp;
            if (runnable != null) {
                runnable.run();
            }
        }

        public void run(int i, ISoundTriggerDetectionService iSoundTriggerDetectionService) throws RemoteException {
            setup();
            this.mExecuteOp.run(i, iSoundTriggerDetectionService);
        }

        public void drop() {
            setup();
            Runnable runnable = this.mDropOp;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class LocalSoundTriggerService implements SoundTriggerInternal {
        public final Context mContext;

        public LocalSoundTriggerService(Context context) {
            this.mContext = context;
        }

        /* loaded from: classes2.dex */
        public class SessionImpl implements SoundTriggerInternal.Session {
            public final IBinder mClient;
            public final SoundTriggerHelper mSoundTriggerHelper;

            public SessionImpl(SoundTriggerHelper soundTriggerHelper, IBinder iBinder) {
                this.mSoundTriggerHelper = soundTriggerHelper;
                this.mClient = iBinder;
                try {
                    iBinder.linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.soundtrigger.SoundTriggerService$LocalSoundTriggerService$SessionImpl$$ExternalSyntheticLambda0
                        @Override // android.os.IBinder.DeathRecipient
                        public final void binderDied() {
                            SoundTriggerService.LocalSoundTriggerService.SessionImpl.this.lambda$new$0();
                        }
                    }, 0);
                } catch (RemoteException e) {
                    Slog.e("SoundTriggerService", "Failed to register death listener.", e);
                }
            }

            @Override // com.android.server.soundtrigger.SoundTriggerInternal.Session
            public int startRecognition(int i, SoundTrigger.KeyphraseSoundModel keyphraseSoundModel, IRecognitionStatusCallback iRecognitionStatusCallback, SoundTrigger.RecognitionConfig recognitionConfig, boolean z) {
                return this.mSoundTriggerHelper.startKeyphraseRecognition(i, keyphraseSoundModel, iRecognitionStatusCallback, recognitionConfig, z);
            }

            @Override // com.android.server.soundtrigger.SoundTriggerInternal.Session
            public synchronized int stopRecognition(int i, IRecognitionStatusCallback iRecognitionStatusCallback) {
                return this.mSoundTriggerHelper.stopKeyphraseRecognition(i, iRecognitionStatusCallback);
            }

            @Override // com.android.server.soundtrigger.SoundTriggerInternal.Session
            public SoundTrigger.ModuleProperties getModuleProperties() {
                return this.mSoundTriggerHelper.getModuleProperties();
            }

            @Override // com.android.server.soundtrigger.SoundTriggerInternal.Session
            public int setParameter(int i, @ModelParams int i2, int i3) {
                return this.mSoundTriggerHelper.setKeyphraseParameter(i, i2, i3);
            }

            @Override // com.android.server.soundtrigger.SoundTriggerInternal.Session
            public int getParameter(int i, @ModelParams int i2) {
                return this.mSoundTriggerHelper.getKeyphraseParameter(i, i2);
            }

            @Override // com.android.server.soundtrigger.SoundTriggerInternal.Session
            public SoundTrigger.ModelParamRange queryParameter(int i, @ModelParams int i2) {
                return this.mSoundTriggerHelper.queryKeyphraseParameter(i, i2);
            }

            @Override // com.android.server.soundtrigger.SoundTriggerInternal.Session
            public int unloadKeyphraseModel(int i) {
                return this.mSoundTriggerHelper.unloadKeyphraseSoundModel(i);
            }

            /* renamed from: clientDied */
            public final void lambda$new$0() {
                Slog.w("SoundTriggerService", "Client died, cleaning up session.");
                SoundTriggerService.sEventLogger.enqueue(new EventLogger.StringEvent("Client died, cleaning up session."));
                this.mSoundTriggerHelper.detach();
            }
        }

        @Override // com.android.server.soundtrigger.SoundTriggerInternal
        public SoundTriggerInternal.Session attach(IBinder iBinder, SoundTrigger.ModuleProperties moduleProperties) {
            return new SessionImpl(SoundTriggerService.this.newSoundTriggerHelper(moduleProperties), iBinder);
        }

        @Override // com.android.server.soundtrigger.SoundTriggerInternal
        public List<SoundTrigger.ModuleProperties> listModuleProperties(Identity identity) {
            ArrayList arrayList = new ArrayList();
            SafeCloseable establishIdentityDirect = PermissionUtil.establishIdentityDirect(identity);
            try {
                Identity identity2 = new Identity();
                identity2.uid = Binder.getCallingUid();
                identity2.pid = Binder.getCallingPid();
                identity2.packageName = ActivityThread.currentOpPackageName();
                SoundTrigger.listModulesAsMiddleman(arrayList, identity2, identity);
                if (establishIdentityDirect != null) {
                    establishIdentityDirect.close();
                }
                return arrayList;
            } catch (Throwable th) {
                if (establishIdentityDirect != null) {
                    try {
                        establishIdentityDirect.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        @Override // com.android.server.soundtrigger.SoundTriggerInternal
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            SoundTriggerService.sEventLogger.dump(printWriter);
            SoundTriggerService.this.mDbHelper.dump(printWriter);
            SoundTriggerService.this.mSoundModelStatTracker.dump(printWriter);
        }
    }
}
