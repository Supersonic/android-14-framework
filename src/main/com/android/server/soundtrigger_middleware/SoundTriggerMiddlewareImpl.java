package com.android.server.soundtrigger_middleware;

import android.media.soundtrigger_middleware.ISoundTriggerCallback;
import android.media.soundtrigger_middleware.ISoundTriggerModule;
import android.media.soundtrigger_middleware.SoundTriggerModuleDescriptor;
import android.util.Log;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class SoundTriggerMiddlewareImpl implements ISoundTriggerMiddlewareInternal {
    public final SoundTriggerModule[] mModules;

    /* loaded from: classes2.dex */
    public static abstract class AudioSessionProvider {
        public abstract AudioSession acquireSession();

        public abstract void releaseSession(int i);

        /* loaded from: classes2.dex */
        public static final class AudioSession {
            public final int mDeviceHandle;
            public final int mIoHandle;
            public final int mSessionHandle;

            public AudioSession(int i, int i2, int i3) {
                this.mSessionHandle = i;
                this.mIoHandle = i2;
                this.mDeviceHandle = i3;
            }
        }
    }

    public SoundTriggerMiddlewareImpl(HalFactory[] halFactoryArr, AudioSessionProvider audioSessionProvider) {
        ArrayList arrayList = new ArrayList(halFactoryArr.length);
        for (HalFactory halFactory : halFactoryArr) {
            try {
                arrayList.add(new SoundTriggerModule(halFactory, audioSessionProvider));
            } catch (Exception e) {
                Log.e("SoundTriggerMiddlewareImpl", "Failed to add a SoundTriggerModule instance", e);
            }
        }
        this.mModules = (SoundTriggerModule[]) arrayList.toArray(new SoundTriggerModule[0]);
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerMiddlewareInternal
    public SoundTriggerModuleDescriptor[] listModules() {
        SoundTriggerModuleDescriptor[] soundTriggerModuleDescriptorArr = new SoundTriggerModuleDescriptor[this.mModules.length];
        for (int i = 0; i < this.mModules.length; i++) {
            SoundTriggerModuleDescriptor soundTriggerModuleDescriptor = new SoundTriggerModuleDescriptor();
            soundTriggerModuleDescriptor.handle = i;
            soundTriggerModuleDescriptor.properties = this.mModules[i].getProperties();
            soundTriggerModuleDescriptorArr[i] = soundTriggerModuleDescriptor;
        }
        return soundTriggerModuleDescriptorArr;
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerMiddlewareInternal
    public ISoundTriggerModule attach(int i, ISoundTriggerCallback iSoundTriggerCallback) {
        return this.mModules[i].attach(iSoundTriggerCallback);
    }
}
