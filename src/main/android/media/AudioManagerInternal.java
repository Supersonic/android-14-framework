package android.media;

import android.util.IntArray;
/* loaded from: classes2.dex */
public abstract class AudioManagerInternal {

    /* loaded from: classes2.dex */
    public interface RingerModeDelegate {
        boolean canVolumeDownEnterSilent();

        int getRingerModeAffectedStreams(int i);

        int onSetRingerModeExternal(int i, int i2, String str, int i3, VolumePolicy volumePolicy);

        int onSetRingerModeInternal(int i, int i2, String str, int i3, VolumePolicy volumePolicy);
    }

    public abstract void addAssistantServiceUid(int i);

    public abstract int getRingerModeInternal();

    public abstract void removeAssistantServiceUid(int i);

    public abstract void setAccessibilityServiceUids(IntArray intArray);

    public abstract void setActiveAssistantServicesUids(IntArray intArray);

    public abstract void setInputMethodServiceUid(int i);

    public abstract void setRingerModeDelegate(RingerModeDelegate ringerModeDelegate);

    public abstract void setRingerModeInternal(int i, String str);

    public abstract void silenceRingerModeInternal(String str);

    public abstract void updateRingerModeAffectedStreamsInternal();
}
