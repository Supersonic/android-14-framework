package android.media;

import android.media.AudioManager;
import java.util.concurrent.Executor;
/* loaded from: classes2.dex */
public interface AudioRecordingMonitor {
    AudioRecordingConfiguration getActiveRecordingConfiguration();

    void registerAudioRecordingCallback(Executor executor, AudioManager.AudioRecordingCallback audioRecordingCallback);

    void unregisterAudioRecordingCallback(AudioManager.AudioRecordingCallback audioRecordingCallback);
}
