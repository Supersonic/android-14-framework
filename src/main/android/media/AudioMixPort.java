package android.media;

import java.util.List;
/* loaded from: classes2.dex */
public class AudioMixPort extends AudioPort {
    private final int mIoHandle;

    AudioMixPort(AudioHandle handle, int ioHandle, int role, String deviceName, int[] samplingRates, int[] channelMasks, int[] channelIndexMasks, int[] formats, AudioGain[] gains) {
        super(handle, role, deviceName, samplingRates, channelMasks, channelIndexMasks, formats, gains);
        this.mIoHandle = ioHandle;
    }

    AudioMixPort(AudioHandle handle, int ioHandle, int role, String deviceName, List<AudioProfile> profiles, AudioGain[] gains) {
        super(handle, role, deviceName, profiles, gains, null);
        this.mIoHandle = ioHandle;
    }

    @Override // android.media.AudioPort
    public AudioMixPortConfig buildConfig(int samplingRate, int channelMask, int format, AudioGainConfig gain) {
        return new AudioMixPortConfig(this, samplingRate, channelMask, format, gain);
    }

    public int ioHandle() {
        return this.mIoHandle;
    }

    @Override // android.media.AudioPort
    public boolean equals(Object o) {
        if (o == null || !(o instanceof AudioMixPort)) {
            return false;
        }
        AudioMixPort other = (AudioMixPort) o;
        if (this.mIoHandle != other.ioHandle()) {
            return false;
        }
        return super.equals(o);
    }
}
