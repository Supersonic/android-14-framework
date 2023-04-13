package com.android.server.soundtrigger_middleware;

import android.content.Context;
import android.media.permission.Identity;
import android.media.permission.IdentityContext;
import android.media.permission.PermissionUtil;
import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.PhraseRecognitionEvent;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.RecognitionEvent;
import android.media.soundtrigger.SoundModel;
import android.media.soundtrigger_middleware.ISoundTriggerCallback;
import android.media.soundtrigger_middleware.ISoundTriggerModule;
import android.media.soundtrigger_middleware.SoundTriggerModuleDescriptor;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.server.LocalServices;
import com.android.server.p011pm.permission.LegacyPermissionManagerInternal;
import java.io.PrintWriter;
import java.util.Objects;
/* loaded from: classes2.dex */
public class SoundTriggerMiddlewarePermission implements ISoundTriggerMiddlewareInternal, Dumpable {
    public final Context mContext;
    public final ISoundTriggerMiddlewareInternal mDelegate;

    public SoundTriggerMiddlewarePermission(ISoundTriggerMiddlewareInternal iSoundTriggerMiddlewareInternal, Context context) {
        this.mDelegate = iSoundTriggerMiddlewareInternal;
        this.mContext = context;
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerMiddlewareInternal
    public SoundTriggerModuleDescriptor[] listModules() {
        enforcePermissionsForPreflight(getIdentity());
        return this.mDelegate.listModules();
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerMiddlewareInternal
    public ISoundTriggerModule attach(int i, ISoundTriggerCallback iSoundTriggerCallback) {
        Identity identity = getIdentity();
        enforcePermissionsForPreflight(identity);
        ModuleWrapper moduleWrapper = new ModuleWrapper(identity, iSoundTriggerCallback);
        return moduleWrapper.attach(this.mDelegate.attach(i, moduleWrapper.getCallbackWrapper()));
    }

    public String toString() {
        return Objects.toString(this.mDelegate);
    }

    public static Identity getIdentity() {
        return IdentityContext.getNonNull();
    }

    public final void enforcePermissionsForPreflight(Identity identity) {
        enforcePermissionForPreflight(this.mContext, identity, "android.permission.RECORD_AUDIO");
        enforcePermissionForPreflight(this.mContext, identity, "android.permission.CAPTURE_AUDIO_HOTWORD");
    }

    public void enforcePermissionsForDataDelivery(Identity identity, String str) {
        enforceSoundTriggerRecordAudioPermissionForDataDelivery(identity, str);
        enforcePermissionForDataDelivery(this.mContext, identity, "android.permission.CAPTURE_AUDIO_HOTWORD", str);
    }

    public static void enforcePermissionForDataDelivery(Context context, Identity identity, String str, String str2) {
        if (PermissionUtil.checkPermissionForDataDelivery(context, identity, str, str2) != 0) {
            throw new SecurityException(String.format("Failed to obtain permission %s for identity %s", str, ObjectPrinter.print(identity, 16)));
        }
    }

    public static void enforceSoundTriggerRecordAudioPermissionForDataDelivery(Identity identity, String str) {
        if (((LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class)).checkSoundTriggerRecordAudioPermissionForDataDelivery(identity.uid, identity.packageName, identity.attributionTag, str) != 0) {
            throw new SecurityException(String.format("Failed to obtain permission RECORD_AUDIO for identity %s", ObjectPrinter.print(identity, 16)));
        }
    }

    public static void enforcePermissionForPreflight(Context context, Identity identity, String str) {
        int checkPermissionForPreflight = PermissionUtil.checkPermissionForPreflight(context, identity, str);
        if (checkPermissionForPreflight == 0 || checkPermissionForPreflight == 1) {
            return;
        }
        if (checkPermissionForPreflight == 2) {
            throw new SecurityException(String.format("Failed to obtain permission %s for identity %s", str, ObjectPrinter.print(identity, 16)));
        }
        throw new RuntimeException("Unexpected perimission check result.");
    }

    @Override // com.android.server.soundtrigger_middleware.Dumpable
    public void dump(PrintWriter printWriter) {
        ISoundTriggerMiddlewareInternal iSoundTriggerMiddlewareInternal = this.mDelegate;
        if (iSoundTriggerMiddlewareInternal instanceof Dumpable) {
            ((Dumpable) iSoundTriggerMiddlewareInternal).dump(printWriter);
        }
    }

    /* loaded from: classes2.dex */
    public class ModuleWrapper extends ISoundTriggerModule.Stub {
        public final CallbackWrapper mCallbackWrapper;
        public ISoundTriggerModule mDelegate;
        public final Identity mOriginatorIdentity;

        public ModuleWrapper(Identity identity, ISoundTriggerCallback iSoundTriggerCallback) {
            this.mOriginatorIdentity = identity;
            this.mCallbackWrapper = new CallbackWrapper(iSoundTriggerCallback);
        }

        public ModuleWrapper attach(ISoundTriggerModule iSoundTriggerModule) {
            this.mDelegate = iSoundTriggerModule;
            return this;
        }

        public ISoundTriggerCallback getCallbackWrapper() {
            return this.mCallbackWrapper;
        }

        public int loadModel(SoundModel soundModel) throws RemoteException {
            enforcePermissions();
            return this.mDelegate.loadModel(soundModel);
        }

        public int loadPhraseModel(PhraseSoundModel phraseSoundModel) throws RemoteException {
            enforcePermissions();
            return this.mDelegate.loadPhraseModel(phraseSoundModel);
        }

        public void unloadModel(int i) throws RemoteException {
            this.mDelegate.unloadModel(i);
        }

        public void startRecognition(int i, RecognitionConfig recognitionConfig) throws RemoteException {
            enforcePermissions();
            this.mDelegate.startRecognition(i, recognitionConfig);
        }

        public void stopRecognition(int i) throws RemoteException {
            this.mDelegate.stopRecognition(i);
        }

        public void forceRecognitionEvent(int i) throws RemoteException {
            enforcePermissions();
            this.mDelegate.forceRecognitionEvent(i);
        }

        public void setModelParameter(int i, int i2, int i3) throws RemoteException {
            enforcePermissions();
            this.mDelegate.setModelParameter(i, i2, i3);
        }

        public int getModelParameter(int i, int i2) throws RemoteException {
            enforcePermissions();
            return this.mDelegate.getModelParameter(i, i2);
        }

        public ModelParameterRange queryModelParameterSupport(int i, int i2) throws RemoteException {
            enforcePermissions();
            return this.mDelegate.queryModelParameterSupport(i, i2);
        }

        public void detach() throws RemoteException {
            this.mDelegate.detach();
        }

        public String toString() {
            return Objects.toString(this.mDelegate);
        }

        public final void enforcePermissions() {
            SoundTriggerMiddlewarePermission.this.enforcePermissionsForPreflight(this.mOriginatorIdentity);
        }

        /* loaded from: classes2.dex */
        public class CallbackWrapper implements ISoundTriggerCallback {
            public final ISoundTriggerCallback mDelegate;

            public CallbackWrapper(ISoundTriggerCallback iSoundTriggerCallback) {
                this.mDelegate = iSoundTriggerCallback;
            }

            public void onRecognition(int i, RecognitionEvent recognitionEvent, int i2) throws RemoteException {
                enforcePermissions("Sound trigger recognition.");
                this.mDelegate.onRecognition(i, recognitionEvent, i2);
            }

            public void onPhraseRecognition(int i, PhraseRecognitionEvent phraseRecognitionEvent, int i2) throws RemoteException {
                enforcePermissions("Sound trigger phrase recognition.");
                this.mDelegate.onPhraseRecognition(i, phraseRecognitionEvent, i2);
            }

            public void onResourcesAvailable() throws RemoteException {
                this.mDelegate.onResourcesAvailable();
            }

            public void onModelUnloaded(int i) throws RemoteException {
                this.mDelegate.onModelUnloaded(i);
            }

            public void onModuleDied() throws RemoteException {
                this.mDelegate.onModuleDied();
            }

            public IBinder asBinder() {
                return this.mDelegate.asBinder();
            }

            public String toString() {
                return this.mDelegate.toString();
            }

            public final void enforcePermissions(String str) {
                ModuleWrapper moduleWrapper = ModuleWrapper.this;
                SoundTriggerMiddlewarePermission.this.enforcePermissionsForDataDelivery(moduleWrapper.mOriginatorIdentity, str);
            }
        }
    }
}
