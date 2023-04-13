package android.hardware.audio.common.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AudioOffloadInfo {
    public int sampleRateHz = 0;
    public int channelMask = 0;
    public int format = 0;
    public int streamType = 0;
    public int bitRatePerSecond = 0;
    public long durationMicroseconds = 0;
    public boolean hasVideo = false;
    public boolean isStreaming = false;
    public int bitWidth = 0;
    public int bufferSize = 0;
    public int usage = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == AudioOffloadInfo.class) {
            AudioOffloadInfo audioOffloadInfo = (AudioOffloadInfo) obj;
            return this.sampleRateHz == audioOffloadInfo.sampleRateHz && this.channelMask == audioOffloadInfo.channelMask && this.format == audioOffloadInfo.format && this.streamType == audioOffloadInfo.streamType && this.bitRatePerSecond == audioOffloadInfo.bitRatePerSecond && this.durationMicroseconds == audioOffloadInfo.durationMicroseconds && this.hasVideo == audioOffloadInfo.hasVideo && this.isStreaming == audioOffloadInfo.isStreaming && this.bitWidth == audioOffloadInfo.bitWidth && this.bufferSize == audioOffloadInfo.bufferSize && this.usage == audioOffloadInfo.usage;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.sampleRateHz))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.channelMask))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.format))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.streamType))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.bitRatePerSecond))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.durationMicroseconds))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.hasVideo))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isStreaming))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.bitWidth))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.bufferSize))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.usage))));
    }

    public final String toString() {
        return "{.sampleRateHz = " + this.sampleRateHz + ", .channelMask = " + AudioChannelMask.toString(this.channelMask) + ", .format = " + AudioFormat.toString(this.format) + ", .streamType = " + AudioStreamType.toString(this.streamType) + ", .bitRatePerSecond = " + this.bitRatePerSecond + ", .durationMicroseconds = " + this.durationMicroseconds + ", .hasVideo = " + this.hasVideo + ", .isStreaming = " + this.isStreaming + ", .bitWidth = " + this.bitWidth + ", .bufferSize = " + this.bufferSize + ", .usage = " + AudioUsage.toString(this.usage) + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.sampleRateHz = hwBlob.getInt32(0 + j);
        this.channelMask = hwBlob.getInt32(4 + j);
        this.format = hwBlob.getInt32(8 + j);
        this.streamType = hwBlob.getInt32(12 + j);
        this.bitRatePerSecond = hwBlob.getInt32(16 + j);
        this.durationMicroseconds = hwBlob.getInt64(24 + j);
        this.hasVideo = hwBlob.getBool(32 + j);
        this.isStreaming = hwBlob.getBool(33 + j);
        this.bitWidth = hwBlob.getInt32(36 + j);
        this.bufferSize = hwBlob.getInt32(40 + j);
        this.usage = hwBlob.getInt32(j + 44);
    }
}
