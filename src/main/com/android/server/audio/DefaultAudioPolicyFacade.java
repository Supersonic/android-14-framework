package com.android.server.audio;

import android.media.IAudioPolicyService;
import android.media.permission.ClearCallingIdentityContext;
import android.media.permission.SafeCloseable;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
/* loaded from: classes.dex */
public class DefaultAudioPolicyFacade implements AudioPolicyFacade, IBinder.DeathRecipient {
    @GuardedBy({"mServiceLock"})
    public IAudioPolicyService mAudioPolicy;
    public final Object mServiceLock = new Object();

    public DefaultAudioPolicyFacade() {
        try {
            getAudioPolicyOrInit();
        } catch (IllegalStateException e) {
            Log.e("DefaultAudioPolicyFacade", "Failed to initialize APM connection", e);
        }
    }

    @Override // com.android.server.audio.AudioPolicyFacade
    public boolean isHotwordStreamSupported(boolean z) {
        IAudioPolicyService audioPolicyOrInit = getAudioPolicyOrInit();
        try {
            SafeCloseable create = ClearCallingIdentityContext.create();
            boolean isHotwordStreamSupported = audioPolicyOrInit.isHotwordStreamSupported(z);
            if (create != null) {
                create.close();
            }
            return isHotwordStreamSupported;
        } catch (RemoteException e) {
            resetServiceConnection(audioPolicyOrInit.asBinder());
            throw new IllegalStateException(e);
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        Log.wtf("DefaultAudioPolicyFacade", "Unexpected binderDied without IBinder object");
    }

    public void binderDied(IBinder iBinder) {
        resetServiceConnection(iBinder);
    }

    public final void resetServiceConnection(IBinder iBinder) {
        synchronized (this.mServiceLock) {
            IAudioPolicyService iAudioPolicyService = this.mAudioPolicy;
            if (iAudioPolicyService != null && iAudioPolicyService.asBinder().equals(iBinder)) {
                this.mAudioPolicy.asBinder().unlinkToDeath(this, 0);
                this.mAudioPolicy = null;
            }
        }
    }

    public final IAudioPolicyService getAudioPolicyOrInit() {
        synchronized (this.mServiceLock) {
            IAudioPolicyService iAudioPolicyService = this.mAudioPolicy;
            if (iAudioPolicyService != null) {
                return iAudioPolicyService;
            }
            IAudioPolicyService asInterface = IAudioPolicyService.Stub.asInterface(ServiceManager.checkService("media.audio_policy"));
            if (asInterface == null) {
                throw new IllegalStateException("DefaultAudioPolicyFacade: Unable to connect to AudioPolicy");
            }
            try {
                asInterface.asBinder().linkToDeath(this, 0);
                this.mAudioPolicy = asInterface;
                return asInterface;
            } catch (RemoteException e) {
                throw new IllegalStateException("DefaultAudioPolicyFacade: Unable to link deathListener to AudioPolicy", e);
            }
        }
    }
}
