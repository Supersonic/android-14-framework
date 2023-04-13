package android.service.voice;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.Service;
import android.app.compat.CompatChanges;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.soundtrigger.KeyphraseEnrollmentInfo;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.permission.Identity;
import android.media.voice.KeyphraseModelManager;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.SharedMemory;
import android.provider.Settings;
import android.service.voice.AlwaysOnHotwordDetector;
import android.service.voice.HotwordDetector;
import android.service.voice.IVoiceInteractionService;
import android.service.voice.VisualQueryDetector;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.app.IVoiceActionCheckCallback;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public class VoiceInteractionService extends Service {
    static final long MULTIPLE_ACTIVE_HOTWORD_DETECTORS = 193232191;
    public static final String SERVICE_INTERFACE = "android.service.voice.VoiceInteractionService";
    public static final String SERVICE_META_DATA = "android.voice_interaction";
    static final String TAG = VoiceInteractionService.class.getSimpleName();
    private VisualQueryDetector mActiveVisualQueryDetector;
    private KeyphraseEnrollmentInfo mKeyphraseEnrollmentInfo;
    IVoiceInteractionManagerService mSystemService;
    IVoiceInteractionService mInterface = new BinderC26851();
    private final Object mLock = new Object();
    private final Set<HotwordDetector> mActiveDetectors = new ArraySet();
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: android.service.voice.VoiceInteractionService$$ExternalSyntheticLambda3
        @Override // android.p008os.IBinder.DeathRecipient
        public final void binderDied() {
            VoiceInteractionService.this.lambda$new$1();
        }
    };

    /* renamed from: android.service.voice.VoiceInteractionService$1 */
    /* loaded from: classes3.dex */
    class BinderC26851 extends IVoiceInteractionService.Stub {
        BinderC26851() {
            VoiceInteractionService.this = this$0;
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void ready() {
            Handler.getMain().executeOrSendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.voice.VoiceInteractionService$1$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((VoiceInteractionService) obj).onReady();
                }
            }, VoiceInteractionService.this));
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void shutdown() {
            Handler.getMain().executeOrSendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.voice.VoiceInteractionService$1$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((VoiceInteractionService) obj).onShutdownInternal();
                }
            }, VoiceInteractionService.this));
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void soundModelsChanged() {
            Handler.getMain().executeOrSendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.voice.VoiceInteractionService$1$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((VoiceInteractionService) obj).onSoundModelsChangedInternal();
                }
            }, VoiceInteractionService.this));
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void launchVoiceAssistFromKeyguard() {
            Handler.getMain().executeOrSendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.voice.VoiceInteractionService$1$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((VoiceInteractionService) obj).onLaunchVoiceAssistFromKeyguard();
                }
            }, VoiceInteractionService.this));
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void getActiveServiceSupportedActions(List<String> voiceActions, IVoiceActionCheckCallback callback) {
            Handler.getMain().executeOrSendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.voice.VoiceInteractionService$1$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((VoiceInteractionService) obj).onHandleVoiceActionCheck((List) obj2, (IVoiceActionCheckCallback) obj3);
                }
            }, VoiceInteractionService.this, voiceActions, callback));
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void prepareToShowSession(Bundle args, int flags) {
            Handler.getMain().executeOrSendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.voice.VoiceInteractionService$1$$ExternalSyntheticLambda2
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((VoiceInteractionService) obj).onPrepareToShowSession((Bundle) obj2, ((Integer) obj3).intValue());
                }
            }, VoiceInteractionService.this, args, Integer.valueOf(flags)));
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void showSessionFailed(Bundle args) {
            Handler.getMain().executeOrSendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.voice.VoiceInteractionService$1$$ExternalSyntheticLambda7
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((VoiceInteractionService) obj).onShowSessionFailed((Bundle) obj2);
                }
            }, VoiceInteractionService.this, args));
        }

        @Override // android.service.voice.IVoiceInteractionService
        public void detectorRemoteExceptionOccurred(IBinder token, int detectorType) {
            Log.m112d(VoiceInteractionService.TAG, "detectorRemoteExceptionOccurred");
            Handler.getMain().executeOrSendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.voice.VoiceInteractionService$1$$ExternalSyntheticLambda4
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((VoiceInteractionService) obj).onDetectorRemoteException((IBinder) obj2, ((Integer) obj3).intValue());
                }
            }, VoiceInteractionService.this, token, Integer.valueOf(detectorType)));
        }
    }

    public void onDetectorRemoteException(final IBinder token, final int detectorType) {
        Log.m112d(TAG, "onDetectorRemoteException for " + HotwordDetector.detectorTypeToString(detectorType));
        this.mActiveDetectors.forEach(new Consumer() { // from class: android.service.voice.VoiceInteractionService$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                VoiceInteractionService.lambda$onDetectorRemoteException$0(detectorType, token, (HotwordDetector) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$onDetectorRemoteException$0(int detectorType, IBinder token, HotwordDetector detector) {
        if (detectorType == 1 && (detector instanceof AlwaysOnHotwordDetector)) {
            AlwaysOnHotwordDetector alwaysOnDetector = (AlwaysOnHotwordDetector) detector;
            if (alwaysOnDetector.isSameToken(token)) {
                alwaysOnDetector.onDetectorRemoteException();
            }
        } else if (detectorType == 2 && (detector instanceof SoftwareHotwordDetector)) {
            SoftwareHotwordDetector softwareDetector = (SoftwareHotwordDetector) detector;
            if (softwareDetector.isSameToken(token)) {
                softwareDetector.onDetectorRemoteException();
            }
        }
    }

    public void onLaunchVoiceAssistFromKeyguard() {
    }

    public void onPrepareToShowSession(Bundle args, int flags) {
    }

    public void onShowSessionFailed(Bundle args) {
    }

    public static boolean isActiveService(Context context, ComponentName service) {
        ComponentName curComp;
        String cur = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.VOICE_INTERACTION_SERVICE);
        if (cur == null || cur.isEmpty() || (curComp = ComponentName.unflattenFromString(cur)) == null) {
            return false;
        }
        return curComp.equals(service);
    }

    public void setDisabledShowContext(int flags) {
        try {
            this.mSystemService.setDisabledShowContext(flags);
        } catch (RemoteException e) {
        }
    }

    public int getDisabledShowContext() {
        try {
            return this.mSystemService.getDisabledShowContext();
        } catch (RemoteException e) {
            return 0;
        }
    }

    public void showSession(Bundle args, int flags) {
        IVoiceInteractionManagerService iVoiceInteractionManagerService = this.mSystemService;
        if (iVoiceInteractionManagerService == null) {
            throw new IllegalStateException("Not available until onReady() is called");
        }
        try {
            iVoiceInteractionManagerService.showSession(args, flags, getAttributionTag());
        } catch (RemoteException e) {
        }
    }

    public Set<String> onGetSupportedVoiceActions(Set<String> voiceActions) {
        return Collections.emptySet();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mInterface.asBinder();
        }
        return null;
    }

    public void onReady() {
        IVoiceInteractionManagerService asInterface = IVoiceInteractionManagerService.Stub.asInterface(ServiceManager.getService(Context.VOICE_INTERACTION_MANAGER_SERVICE));
        this.mSystemService = asInterface;
        Objects.requireNonNull(asInterface);
        try {
            this.mSystemService.asBinder().linkToDeath(this.mDeathRecipient, 0);
        } catch (RemoteException e) {
            Log.wtf(TAG, "unable to link to death with system service");
        }
        this.mKeyphraseEnrollmentInfo = new KeyphraseEnrollmentInfo(getPackageManager());
    }

    public /* synthetic */ void lambda$new$1() {
        Log.m110e(TAG, "system service binder died shutting down");
        Handler.getMain().executeOrSendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.voice.VoiceInteractionService$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((VoiceInteractionService) obj).onShutdownInternal();
            }
        }, this));
    }

    public void onShutdownInternal() {
        onShutdown();
        safelyShutdownAllHotwordDetectors();
    }

    public void onShutdown() {
    }

    public void onSoundModelsChangedInternal() {
        synchronized (this) {
            this.mActiveDetectors.forEach(new Consumer() { // from class: android.service.voice.VoiceInteractionService$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    VoiceInteractionService.lambda$onSoundModelsChangedInternal$2((HotwordDetector) obj);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$onSoundModelsChangedInternal$2(HotwordDetector detector) {
        if (detector instanceof AlwaysOnHotwordDetector) {
            ((AlwaysOnHotwordDetector) detector).onSoundModelsChanged();
        }
    }

    public void onHandleVoiceActionCheck(List<String> voiceActions, IVoiceActionCheckCallback callback) {
        if (callback != null) {
            try {
                Set<String> voiceActionsSet = new ArraySet<>(voiceActions);
                Set<String> resultSet = onGetSupportedVoiceActions(voiceActionsSet);
                callback.onComplete(new ArrayList(resultSet));
            } catch (RemoteException e) {
            }
        }
    }

    public final List<SoundTrigger.ModuleProperties> listModuleProperties() {
        Identity identity = new Identity();
        identity.packageName = ActivityThread.currentOpPackageName();
        try {
            return this.mSystemService.listModuleProperties(identity);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public final AlwaysOnHotwordDetector createAlwaysOnHotwordDetector(String keyphrase, Locale locale, AlwaysOnHotwordDetector.Callback callback) {
        return createAlwaysOnHotwordDetectorInternal(keyphrase, locale, false, null, null, null, null, callback);
    }

    @SystemApi
    public final AlwaysOnHotwordDetector createAlwaysOnHotwordDetector(String keyphrase, Locale locale, Executor executor, AlwaysOnHotwordDetector.Callback callback) {
        Objects.requireNonNull(keyphrase);
        Objects.requireNonNull(locale);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        return createAlwaysOnHotwordDetectorInternal(keyphrase, locale, false, null, null, null, executor, callback);
    }

    public final AlwaysOnHotwordDetector createAlwaysOnHotwordDetectorForTest(String keyphrase, Locale locale, SoundTrigger.ModuleProperties moduleProperties, Executor executor, AlwaysOnHotwordDetector.Callback callback) {
        Objects.requireNonNull(keyphrase);
        Objects.requireNonNull(locale);
        Objects.requireNonNull(moduleProperties);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        return createAlwaysOnHotwordDetectorInternal(keyphrase, locale, false, null, null, moduleProperties, executor, callback);
    }

    @SystemApi
    @Deprecated
    public final AlwaysOnHotwordDetector createAlwaysOnHotwordDetector(String keyphrase, Locale locale, PersistableBundle options, SharedMemory sharedMemory, AlwaysOnHotwordDetector.Callback callback) {
        return createAlwaysOnHotwordDetectorInternal(keyphrase, locale, true, options, sharedMemory, null, null, callback);
    }

    @SystemApi
    public final AlwaysOnHotwordDetector createAlwaysOnHotwordDetector(String keyphrase, Locale locale, PersistableBundle options, SharedMemory sharedMemory, Executor executor, AlwaysOnHotwordDetector.Callback callback) {
        Objects.requireNonNull(keyphrase);
        Objects.requireNonNull(locale);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        return createAlwaysOnHotwordDetectorInternal(keyphrase, locale, true, options, sharedMemory, null, executor, callback);
    }

    public final AlwaysOnHotwordDetector createAlwaysOnHotwordDetectorForTest(String keyphrase, Locale locale, PersistableBundle options, SharedMemory sharedMemory, SoundTrigger.ModuleProperties moduleProperties, Executor executor, AlwaysOnHotwordDetector.Callback callback) {
        Objects.requireNonNull(keyphrase);
        Objects.requireNonNull(locale);
        Objects.requireNonNull(moduleProperties);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        return createAlwaysOnHotwordDetectorInternal(keyphrase, locale, true, options, sharedMemory, moduleProperties, executor, callback);
    }

    private AlwaysOnHotwordDetector createAlwaysOnHotwordDetectorInternal(String keyphrase, Locale locale, boolean supportHotwordDetectionService, PersistableBundle options, SharedMemory sharedMemory, SoundTrigger.ModuleProperties moduleProperties, Executor executor, AlwaysOnHotwordDetector.Callback callback) {
        if (this.mSystemService == null) {
            throw new IllegalStateException("Not available until onReady() is called");
        }
        synchronized (this.mLock) {
            try {
                try {
                    if (!CompatChanges.isChangeEnabled(MULTIPLE_ACTIVE_HOTWORD_DETECTORS)) {
                        safelyShutdownAllHotwordDetectors();
                    } else {
                        for (HotwordDetector detector : this.mActiveDetectors) {
                            if (detector.isUsingSandboxedDetectionService() != supportHotwordDetectionService) {
                                throw new IllegalStateException("It disallows to create trusted and non-trusted detectors at the same time.");
                            }
                            try {
                                if (detector instanceof AlwaysOnHotwordDetector) {
                                    throw new IllegalStateException("There is already an active AlwaysOnHotwordDetector. It must be destroyed to create a new one.");
                                }
                            } catch (Throwable th) {
                                e = th;
                                throw e;
                            }
                        }
                    }
                    AlwaysOnHotwordDetector dspDetector = new AlwaysOnHotwordDetector(keyphrase, locale, executor, callback, this.mKeyphraseEnrollmentInfo, this.mSystemService, getApplicationContext().getApplicationInfo().targetSdkVersion, supportHotwordDetectionService);
                    this.mActiveDetectors.add(dspDetector);
                    try {
                        dspDetector.registerOnDestroyListener(new VoiceInteractionService$$ExternalSyntheticLambda0(this));
                        try {
                            dspDetector.initialize(options, sharedMemory, moduleProperties);
                            return dspDetector;
                        } catch (Exception e) {
                            e = e;
                            this.mActiveDetectors.remove(dspDetector);
                            dspDetector.destroy();
                            throw e;
                        }
                    } catch (Exception e2) {
                        e = e2;
                    }
                } catch (Throwable th2) {
                    e = th2;
                    throw e;
                }
            } catch (Throwable th3) {
                e = th3;
            }
        }
    }

    @SystemApi
    @Deprecated
    public final HotwordDetector createHotwordDetector(PersistableBundle options, SharedMemory sharedMemory, HotwordDetector.Callback callback) {
        return createHotwordDetectorInternal(options, sharedMemory, null, callback);
    }

    @SystemApi
    public final HotwordDetector createHotwordDetector(PersistableBundle options, SharedMemory sharedMemory, Executor executor, HotwordDetector.Callback callback) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        return createHotwordDetectorInternal(options, sharedMemory, executor, callback);
    }

    private HotwordDetector createHotwordDetectorInternal(PersistableBundle options, SharedMemory sharedMemory, Executor executor, HotwordDetector.Callback callback) {
        SoftwareHotwordDetector softwareHotwordDetector;
        if (this.mSystemService == null) {
            throw new IllegalStateException("Not available until onReady() is called");
        }
        synchronized (this.mLock) {
            if (!CompatChanges.isChangeEnabled(MULTIPLE_ACTIVE_HOTWORD_DETECTORS)) {
                safelyShutdownAllHotwordDetectors();
            } else {
                for (HotwordDetector detector : this.mActiveDetectors) {
                    if (!detector.isUsingSandboxedDetectionService()) {
                        throw new IllegalStateException("It disallows to create trusted and non-trusted detectors at the same time.");
                    }
                    if (detector instanceof SoftwareHotwordDetector) {
                        throw new IllegalStateException("There is already an active SoftwareHotwordDetector. It must be destroyed to create a new one.");
                    }
                }
            }
            softwareHotwordDetector = new SoftwareHotwordDetector(this.mSystemService, null, executor, callback);
            this.mActiveDetectors.add(softwareHotwordDetector);
            try {
                softwareHotwordDetector.registerOnDestroyListener(new VoiceInteractionService$$ExternalSyntheticLambda0(this));
                softwareHotwordDetector.initialize(options, sharedMemory);
            } catch (Exception e) {
                this.mActiveDetectors.remove(softwareHotwordDetector);
                softwareHotwordDetector.destroy();
                throw e;
            }
        }
        return softwareHotwordDetector;
    }

    @SystemApi
    public final VisualQueryDetector createVisualQueryDetector(PersistableBundle options, SharedMemory sharedMemory, Executor executor, VisualQueryDetector.Callback callback) {
        VisualQueryDetector visualQueryDetector;
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        if (this.mSystemService == null) {
            throw new IllegalStateException("Not available until onReady() is called");
        }
        synchronized (this.mLock) {
            if (!CompatChanges.isChangeEnabled(MULTIPLE_ACTIVE_HOTWORD_DETECTORS)) {
                throw new UnsupportedOperationException("VisualQueryDetector is only available if multiple detectors are allowed");
            }
            if (this.mActiveVisualQueryDetector != null) {
                throw new IllegalStateException("There is already an active VisualQueryDetector. It must be destroyed to create a new one.");
            }
            for (HotwordDetector detector : this.mActiveDetectors) {
                if (!detector.isUsingSandboxedDetectionService()) {
                    throw new IllegalStateException("It disallows to create trusted and non-trusted detectors at the same time.");
                }
            }
            visualQueryDetector = new VisualQueryDetector(this.mSystemService, executor, callback);
            HotwordDetector visualQueryDetectorInitializationDelegate = visualQueryDetector.getInitializationDelegate();
            this.mActiveDetectors.add(visualQueryDetectorInitializationDelegate);
            try {
                visualQueryDetector.registerOnDestroyListener(new VoiceInteractionService$$ExternalSyntheticLambda0(this));
                visualQueryDetector.initialize(options, sharedMemory);
                this.mActiveVisualQueryDetector = visualQueryDetector;
            } catch (Exception e) {
                this.mActiveDetectors.remove(visualQueryDetectorInitializationDelegate);
                visualQueryDetector.destroy();
                throw e;
            }
        }
        return visualQueryDetector;
    }

    @SystemApi
    public final KeyphraseModelManager createKeyphraseModelManager() {
        KeyphraseModelManager keyphraseModelManager;
        if (this.mSystemService == null) {
            throw new IllegalStateException("Not available until onReady() is called");
        }
        synchronized (this.mLock) {
            keyphraseModelManager = new KeyphraseModelManager(this.mSystemService);
        }
        return keyphraseModelManager;
    }

    protected final KeyphraseEnrollmentInfo getKeyphraseEnrollmentInfo() {
        return this.mKeyphraseEnrollmentInfo;
    }

    public final boolean isKeyphraseAndLocaleSupportedForHotword(String keyphrase, Locale locale) {
        KeyphraseEnrollmentInfo keyphraseEnrollmentInfo = this.mKeyphraseEnrollmentInfo;
        return (keyphraseEnrollmentInfo == null || keyphraseEnrollmentInfo.getKeyphraseMetadata(keyphrase, locale) == null) ? false : true;
    }

    private void safelyShutdownAllHotwordDetectors() {
        synchronized (this.mLock) {
            this.mActiveDetectors.forEach(new Consumer() { // from class: android.service.voice.VoiceInteractionService$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    VoiceInteractionService.lambda$safelyShutdownAllHotwordDetectors$3((HotwordDetector) obj);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$safelyShutdownAllHotwordDetectors$3(HotwordDetector detector) {
        try {
            detector.destroy();
        } catch (Exception ex) {
            Log.m107i(TAG, "exception destroying HotwordDetector", ex);
        }
    }

    public void onHotwordDetectorDestroyed(HotwordDetector detector) {
        synchronized (this.mLock) {
            VisualQueryDetector visualQueryDetector = this.mActiveVisualQueryDetector;
            if (visualQueryDetector != null && detector == visualQueryDetector.getInitializationDelegate()) {
                this.mActiveVisualQueryDetector = null;
            }
            this.mActiveDetectors.remove(detector);
            shutdownHotwordDetectionServiceIfRequiredLocked();
        }
    }

    private void shutdownHotwordDetectionServiceIfRequiredLocked() {
        for (HotwordDetector detector : this.mActiveDetectors) {
            if (detector.isUsingSandboxedDetectionService()) {
                return;
            }
        }
        try {
            this.mSystemService.shutdownHotwordDetectionService();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public final void setUiHints(Bundle hints) {
        if (hints == null) {
            throw new IllegalArgumentException("Hints must be non-null");
        }
        try {
            this.mSystemService.setUiHints(hints);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.app.Service
    public void dump(FileDescriptor fd, final PrintWriter pw, String[] args) {
        pw.println("VOICE INTERACTION");
        synchronized (this.mLock) {
            pw.println("  Sandboxed Detector(s):");
            if (this.mActiveDetectors.size() == 0) {
                pw.println("    No detector.");
            } else {
                this.mActiveDetectors.forEach(new Consumer() { // from class: android.service.voice.VoiceInteractionService$$ExternalSyntheticLambda4
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        VoiceInteractionService.lambda$dump$4(pw, (HotwordDetector) obj);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$dump$4(PrintWriter pw, HotwordDetector detector) {
        pw.print("  Using sandboxed detection service=");
        pw.println(detector.isUsingSandboxedDetectionService());
        detector.dump("    ", pw);
        pw.println();
    }
}
