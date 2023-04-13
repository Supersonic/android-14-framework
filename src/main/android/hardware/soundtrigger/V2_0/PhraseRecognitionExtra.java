package android.hardware.soundtrigger.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class PhraseRecognitionExtra {

    /* renamed from: id */
    public int f13id = 0;
    public int recognitionModes = 0;
    public int confidenceLevel = 0;
    public ArrayList<ConfidenceLevel> levels = new ArrayList<>();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == PhraseRecognitionExtra.class) {
            PhraseRecognitionExtra phraseRecognitionExtra = (PhraseRecognitionExtra) obj;
            return this.f13id == phraseRecognitionExtra.f13id && this.recognitionModes == phraseRecognitionExtra.recognitionModes && this.confidenceLevel == phraseRecognitionExtra.confidenceLevel && HidlSupport.deepEquals(this.levels, phraseRecognitionExtra.levels);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.f13id))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.recognitionModes))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.confidenceLevel))), Integer.valueOf(HidlSupport.deepHashCode(this.levels)));
    }

    public final String toString() {
        return "{.id = " + this.f13id + ", .recognitionModes = " + this.recognitionModes + ", .confidenceLevel = " + this.confidenceLevel + ", .levels = " + this.levels + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.f13id = hwBlob.getInt32(j + 0);
        this.recognitionModes = hwBlob.getInt32(j + 4);
        this.confidenceLevel = hwBlob.getInt32(j + 8);
        long j2 = j + 16;
        int int32 = hwBlob.getInt32(8 + j2);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 8, hwBlob.handle(), j2 + 0, true);
        this.levels.clear();
        for (int i = 0; i < int32; i++) {
            ConfidenceLevel confidenceLevel = new ConfidenceLevel();
            confidenceLevel.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 8);
            this.levels.add(confidenceLevel);
        }
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putInt32(j + 0, this.f13id);
        hwBlob.putInt32(4 + j, this.recognitionModes);
        hwBlob.putInt32(j + 8, this.confidenceLevel);
        int size = this.levels.size();
        long j2 = j + 16;
        hwBlob.putInt32(8 + j2, size);
        hwBlob.putBool(12 + j2, false);
        HwBlob hwBlob2 = new HwBlob(size * 8);
        for (int i = 0; i < size; i++) {
            this.levels.get(i).writeEmbeddedToBlob(hwBlob2, i * 8);
        }
        hwBlob.putBlob(j2 + 0, hwBlob2);
    }
}
