package android.hardware.audio.common.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AudioConfig {
    public int sampleRateHz = 0;
    public int channelMask = 0;
    public int format = 0;
    public AudioOffloadInfo offloadInfo = new AudioOffloadInfo();
    public long frameCount = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == AudioConfig.class) {
            AudioConfig audioConfig = (AudioConfig) obj;
            return this.sampleRateHz == audioConfig.sampleRateHz && this.channelMask == audioConfig.channelMask && this.format == audioConfig.format && HidlSupport.deepEquals(this.offloadInfo, audioConfig.offloadInfo) && this.frameCount == audioConfig.frameCount;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.sampleRateHz))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.channelMask))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.format))), Integer.valueOf(HidlSupport.deepHashCode(this.offloadInfo)), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.frameCount))));
    }

    public final String toString() {
        return "{.sampleRateHz = " + this.sampleRateHz + ", .channelMask = " + AudioChannelMask.toString(this.channelMask) + ", .format = " + AudioFormat.toString(this.format) + ", .offloadInfo = " + this.offloadInfo + ", .frameCount = " + this.frameCount + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.sampleRateHz = hwBlob.getInt32(0 + j);
        this.channelMask = hwBlob.getInt32(4 + j);
        this.format = hwBlob.getInt32(8 + j);
        this.offloadInfo.readEmbeddedFromParcel(hwParcel, hwBlob, 16 + j);
        this.frameCount = hwBlob.getInt64(j + 64);
    }
}
