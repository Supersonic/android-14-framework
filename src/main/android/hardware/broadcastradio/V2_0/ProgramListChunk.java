package android.hardware.broadcastradio.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ProgramListChunk {
    public boolean purge = false;
    public boolean complete = false;
    public ArrayList<ProgramInfo> modified = new ArrayList<>();
    public ArrayList<ProgramIdentifier> removed = new ArrayList<>();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == ProgramListChunk.class) {
            ProgramListChunk programListChunk = (ProgramListChunk) obj;
            return this.purge == programListChunk.purge && this.complete == programListChunk.complete && HidlSupport.deepEquals(this.modified, programListChunk.modified) && HidlSupport.deepEquals(this.removed, programListChunk.removed);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.purge))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.complete))), Integer.valueOf(HidlSupport.deepHashCode(this.modified)), Integer.valueOf(HidlSupport.deepHashCode(this.removed)));
    }

    public final String toString() {
        return "{.purge = " + this.purge + ", .complete = " + this.complete + ", .modified = " + this.modified + ", .removed = " + this.removed + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(40L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.purge = hwBlob.getBool(j + 0);
        this.complete = hwBlob.getBool(j + 1);
        long j2 = j + 8;
        int int32 = hwBlob.getInt32(j2 + 8);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 120, hwBlob.handle(), j2 + 0, true);
        this.modified.clear();
        for (int i = 0; i < int32; i++) {
            ProgramInfo programInfo = new ProgramInfo();
            programInfo.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 120);
            this.modified.add(programInfo);
        }
        long j3 = j + 24;
        int int322 = hwBlob.getInt32(8 + j3);
        HwBlob readEmbeddedBuffer2 = hwParcel.readEmbeddedBuffer(int322 * 16, hwBlob.handle(), j3 + 0, true);
        this.removed.clear();
        for (int i2 = 0; i2 < int322; i2++) {
            ProgramIdentifier programIdentifier = new ProgramIdentifier();
            programIdentifier.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer2, i2 * 16);
            this.removed.add(programIdentifier);
        }
    }
}
