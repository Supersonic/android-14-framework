package android.hardware.soundtrigger.V2_3;

import android.hardware.soundtrigger.V2_1.ISoundTriggerHw;
import android.os.HwBlob;
import android.os.HwParcel;
/* loaded from: classes.dex */
public final class RecognitionConfig {
    public int audioCapabilities;
    public ISoundTriggerHw.RecognitionConfig base = new ISoundTriggerHw.RecognitionConfig();

    public final String toString() {
        return "{.base = " + this.base + ", .audioCapabilities = " + AudioCapabilities.dumpBitfield(this.audioCapabilities) + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(96L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.base.readEmbeddedFromParcel(hwParcel, hwBlob, 0 + j);
        this.audioCapabilities = hwBlob.getInt32(j + 88);
    }

    public final void writeToParcel(HwParcel hwParcel) {
        HwBlob hwBlob = new HwBlob(96);
        writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        this.base.writeEmbeddedToBlob(hwBlob, 0 + j);
        hwBlob.putInt32(j + 88, this.audioCapabilities);
    }
}
