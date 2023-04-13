package com.android.server.hdmi;

import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceVolumeManager;
import android.media.VolumeInfo;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public interface AudioDeviceVolumeManagerWrapperInterface {
    void addOnDeviceVolumeBehaviorChangedListener(Executor executor, AudioDeviceVolumeManager.OnDeviceVolumeBehaviorChangedListener onDeviceVolumeBehaviorChangedListener);

    void setDeviceAbsoluteVolumeBehavior(AudioDeviceAttributes audioDeviceAttributes, VolumeInfo volumeInfo, Executor executor, AudioDeviceVolumeManager.OnAudioDeviceVolumeChangedListener onAudioDeviceVolumeChangedListener, boolean z);
}
