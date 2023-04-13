package android.hardware.broadcastradio.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ProgramFilter {
    public ArrayList<Integer> identifierTypes = new ArrayList<>();
    public ArrayList<ProgramIdentifier> identifiers = new ArrayList<>();
    public boolean includeCategories = false;
    public boolean excludeModifications = false;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == ProgramFilter.class) {
            ProgramFilter programFilter = (ProgramFilter) obj;
            return HidlSupport.deepEquals(this.identifierTypes, programFilter.identifierTypes) && HidlSupport.deepEquals(this.identifiers, programFilter.identifiers) && this.includeCategories == programFilter.includeCategories && this.excludeModifications == programFilter.excludeModifications;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.identifierTypes)), Integer.valueOf(HidlSupport.deepHashCode(this.identifiers)), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.includeCategories))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.excludeModifications))));
    }

    public final String toString() {
        return "{.identifierTypes = " + this.identifierTypes + ", .identifiers = " + this.identifiers + ", .includeCategories = " + this.includeCategories + ", .excludeModifications = " + this.excludeModifications + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(40L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        long j2 = j + 0;
        int int32 = hwBlob.getInt32(j2 + 8);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 4, hwBlob.handle(), j2 + 0, true);
        this.identifierTypes.clear();
        for (int i = 0; i < int32; i++) {
            this.identifierTypes.add(Integer.valueOf(readEmbeddedBuffer.getInt32(i * 4)));
        }
        long j3 = j + 16;
        int int322 = hwBlob.getInt32(8 + j3);
        HwBlob readEmbeddedBuffer2 = hwParcel.readEmbeddedBuffer(int322 * 16, hwBlob.handle(), j3 + 0, true);
        this.identifiers.clear();
        for (int i2 = 0; i2 < int322; i2++) {
            ProgramIdentifier programIdentifier = new ProgramIdentifier();
            programIdentifier.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer2, i2 * 16);
            this.identifiers.add(programIdentifier);
        }
        this.includeCategories = hwBlob.getBool(j + 32);
        this.excludeModifications = hwBlob.getBool(j + 33);
    }

    public final void writeToParcel(HwParcel hwParcel) {
        HwBlob hwBlob = new HwBlob(40);
        writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        int size = this.identifierTypes.size();
        long j2 = j + 0;
        hwBlob.putInt32(j2 + 8, size);
        hwBlob.putBool(j2 + 12, false);
        HwBlob hwBlob2 = new HwBlob(size * 4);
        for (int i = 0; i < size; i++) {
            hwBlob2.putInt32(i * 4, this.identifierTypes.get(i).intValue());
        }
        hwBlob.putBlob(j2 + 0, hwBlob2);
        int size2 = this.identifiers.size();
        long j3 = j + 16;
        hwBlob.putInt32(8 + j3, size2);
        hwBlob.putBool(12 + j3, false);
        HwBlob hwBlob3 = new HwBlob(size2 * 16);
        for (int i2 = 0; i2 < size2; i2++) {
            this.identifiers.get(i2).writeEmbeddedToBlob(hwBlob3, i2 * 16);
        }
        hwBlob.putBlob(j3 + 0, hwBlob3);
        hwBlob.putBool(j + 32, this.includeCategories);
        hwBlob.putBool(j + 33, this.excludeModifications);
    }
}
