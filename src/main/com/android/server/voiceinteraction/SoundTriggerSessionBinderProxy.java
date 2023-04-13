package com.android.server.voiceinteraction;

import android.hardware.soundtrigger.SoundTrigger;
import android.os.RemoteException;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
import com.android.internal.app.IVoiceInteractionSoundTriggerSession;
/* loaded from: classes2.dex */
public final class SoundTriggerSessionBinderProxy extends IVoiceInteractionSoundTriggerSession.Stub {
    public final IVoiceInteractionSoundTriggerSession mDelegate;

    public SoundTriggerSessionBinderProxy(IVoiceInteractionSoundTriggerSession iVoiceInteractionSoundTriggerSession) {
        this.mDelegate = iVoiceInteractionSoundTriggerSession;
    }

    public SoundTrigger.ModuleProperties getDspModuleProperties() throws RemoteException {
        return this.mDelegate.getDspModuleProperties();
    }

    public int startRecognition(int i, String str, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback, SoundTrigger.RecognitionConfig recognitionConfig, boolean z) throws RemoteException {
        return this.mDelegate.startRecognition(i, str, iHotwordRecognitionStatusCallback, recognitionConfig, z);
    }

    public int stopRecognition(int i, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback) throws RemoteException {
        return this.mDelegate.stopRecognition(i, iHotwordRecognitionStatusCallback);
    }

    public int setParameter(int i, int i2, int i3) throws RemoteException {
        return this.mDelegate.setParameter(i, i2, i3);
    }

    public int getParameter(int i, int i2) throws RemoteException {
        return this.mDelegate.getParameter(i, i2);
    }

    public SoundTrigger.ModelParamRange queryParameter(int i, int i2) throws RemoteException {
        return this.mDelegate.queryParameter(i, i2);
    }
}
