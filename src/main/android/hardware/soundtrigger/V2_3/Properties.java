package android.hardware.soundtrigger.V2_3;

import android.hardware.soundtrigger.V2_0.ISoundTriggerHw;
import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Properties {
    public int audioCapabilities;
    public ISoundTriggerHw.Properties base = new ISoundTriggerHw.Properties();
    public String supportedModelArch = new String();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == Properties.class) {
            Properties properties = (Properties) obj;
            return HidlSupport.deepEquals(this.base, properties.base) && HidlSupport.deepEquals(this.supportedModelArch, properties.supportedModelArch) && HidlSupport.deepEquals(Integer.valueOf(this.audioCapabilities), Integer.valueOf(properties.audioCapabilities));
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.base)), Integer.valueOf(HidlSupport.deepHashCode(this.supportedModelArch)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.audioCapabilities))));
    }

    public final String toString() {
        return "{.base = " + this.base + ", .supportedModelArch = " + this.supportedModelArch + ", .audioCapabilities = " + AudioCapabilities.dumpBitfield(this.audioCapabilities) + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(112L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.base.readEmbeddedFromParcel(hwParcel, hwBlob, j + 0);
        long j2 = j + 88;
        String string = hwBlob.getString(j2);
        this.supportedModelArch = string;
        hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
        this.audioCapabilities = hwBlob.getInt32(j + 104);
    }

    public final void writeToParcel(HwParcel hwParcel) {
        HwBlob hwBlob = new HwBlob(112);
        writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        this.base.writeEmbeddedToBlob(hwBlob, 0 + j);
        hwBlob.putString(88 + j, this.supportedModelArch);
        hwBlob.putInt32(j + 104, this.audioCapabilities);
    }
}
