package com.android.server.hdmi;

import android.content.Context;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceVolumeManager;
import android.media.VolumeInfo;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class AudioDeviceVolumeManagerWrapper implements AudioDeviceVolumeManagerWrapperInterface {
    public final AudioDeviceVolumeManager mAudioDeviceVolumeManager;

    public AudioDeviceVolumeManagerWrapper(Context context) {
        this.mAudioDeviceVolumeManager = new AudioDeviceVolumeManager(context);
    }

    @Override // com.android.server.hdmi.AudioDeviceVolumeManagerWrapperInterface
    public void addOnDeviceVolumeBehaviorChangedListener(Executor executor, AudioDeviceVolumeManager.OnDeviceVolumeBehaviorChangedListener onDeviceVolumeBehaviorChangedListener) throws SecurityException {
        this.mAudioDeviceVolumeManager.addOnDeviceVolumeBehaviorChangedListener(executor, onDeviceVolumeBehaviorChangedListener);
    }

    @Override // com.android.server.hdmi.AudioDeviceVolumeManagerWrapperInterface
    public void setDeviceAbsoluteVolumeBehavior(AudioDeviceAttributes audioDeviceAttributes, VolumeInfo volumeInfo, Executor executor, AudioDeviceVolumeManager.OnAudioDeviceVolumeChangedListener onAudioDeviceVolumeChangedListener, boolean z) {
        this.mAudioDeviceVolumeManager.setDeviceAbsoluteVolumeBehavior(audioDeviceAttributes, volumeInfo, executor, onAudioDeviceVolumeChangedListener, z);
    }
}
