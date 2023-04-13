package com.android.server.voiceinteraction;

import android.content.Context;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.permission.Identity;
import android.media.permission.PermissionUtil;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
import com.android.internal.app.IVoiceInteractionSoundTriggerSession;
/* loaded from: classes2.dex */
public final class SoundTriggerSessionPermissionsDecorator implements IVoiceInteractionSoundTriggerSession {
    public final Context mContext;
    public final IVoiceInteractionSoundTriggerSession mDelegate;
    public final Identity mOriginatorIdentity;

    public SoundTriggerSessionPermissionsDecorator(IVoiceInteractionSoundTriggerSession iVoiceInteractionSoundTriggerSession, Context context, Identity identity) {
        this.mDelegate = iVoiceInteractionSoundTriggerSession;
        this.mContext = context;
        this.mOriginatorIdentity = identity;
    }

    public SoundTrigger.ModuleProperties getDspModuleProperties() throws RemoteException {
        return this.mDelegate.getDspModuleProperties();
    }

    public int startRecognition(int i, String str, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback, SoundTrigger.RecognitionConfig recognitionConfig, boolean z) throws RemoteException {
        if (!isHoldingPermissions()) {
            return SoundTrigger.STATUS_PERMISSION_DENIED;
        }
        return this.mDelegate.startRecognition(i, str, iHotwordRecognitionStatusCallback, recognitionConfig, z);
    }

    public int stopRecognition(int i, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback) throws RemoteException {
        return this.mDelegate.stopRecognition(i, iHotwordRecognitionStatusCallback);
    }

    public int setParameter(int i, int i2, int i3) throws RemoteException {
        if (!isHoldingPermissions()) {
            return SoundTrigger.STATUS_PERMISSION_DENIED;
        }
        return this.mDelegate.setParameter(i, i2, i3);
    }

    public int getParameter(int i, int i2) throws RemoteException {
        return this.mDelegate.getParameter(i, i2);
    }

    public SoundTrigger.ModelParamRange queryParameter(int i, int i2) throws RemoteException {
        return this.mDelegate.queryParameter(i, i2);
    }

    public IBinder asBinder() {
        throw new UnsupportedOperationException("This object isn't intended to be used as a Binder.");
    }

    public final boolean isHoldingPermissions() {
        try {
            enforcePermissionForPreflight(this.mContext, this.mOriginatorIdentity, "android.permission.RECORD_AUDIO");
            enforcePermissionForPreflight(this.mContext, this.mOriginatorIdentity, "android.permission.CAPTURE_AUDIO_HOTWORD");
            return true;
        } catch (SecurityException e) {
            Slog.e("SoundTriggerSessionPermissionsDecorator", e.toString());
            return false;
        }
    }

    public static void enforcePermissionForPreflight(Context context, Identity identity, String str) {
        int checkPermissionForPreflight = PermissionUtil.checkPermissionForPreflight(context, identity, str);
        if (checkPermissionForPreflight == 0 || checkPermissionForPreflight == 1) {
            return;
        }
        if (checkPermissionForPreflight == 2) {
            throw new SecurityException(TextUtils.formatSimple("Failed to obtain permission %s for identity %s", new Object[]{str, toString(identity)}));
        }
        throw new RuntimeException("Unexpected permission check result.");
    }

    public static String toString(Identity identity) {
        return "{uid=" + identity.uid + " pid=" + identity.pid + " packageName=" + identity.packageName + " attributionTag=" + identity.attributionTag + "}";
    }
}
