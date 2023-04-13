package android.hardware.broadcastradio.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ProgramInfo {
    public int infoFlags;
    public ProgramSelector selector = new ProgramSelector();
    public ProgramIdentifier logicallyTunedTo = new ProgramIdentifier();
    public ProgramIdentifier physicallyTunedTo = new ProgramIdentifier();
    public ArrayList<ProgramIdentifier> relatedContent = new ArrayList<>();
    public int signalQuality = 0;
    public ArrayList<Metadata> metadata = new ArrayList<>();
    public ArrayList<VendorKeyValue> vendorInfo = new ArrayList<>();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == ProgramInfo.class) {
            ProgramInfo programInfo = (ProgramInfo) obj;
            return HidlSupport.deepEquals(this.selector, programInfo.selector) && HidlSupport.deepEquals(this.logicallyTunedTo, programInfo.logicallyTunedTo) && HidlSupport.deepEquals(this.physicallyTunedTo, programInfo.physicallyTunedTo) && HidlSupport.deepEquals(this.relatedContent, programInfo.relatedContent) && HidlSupport.deepEquals(Integer.valueOf(this.infoFlags), Integer.valueOf(programInfo.infoFlags)) && this.signalQuality == programInfo.signalQuality && HidlSupport.deepEquals(this.metadata, programInfo.metadata) && HidlSupport.deepEquals(this.vendorInfo, programInfo.vendorInfo);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.selector)), Integer.valueOf(HidlSupport.deepHashCode(this.logicallyTunedTo)), Integer.valueOf(HidlSupport.deepHashCode(this.physicallyTunedTo)), Integer.valueOf(HidlSupport.deepHashCode(this.relatedContent)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.infoFlags))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.signalQuality))), Integer.valueOf(HidlSupport.deepHashCode(this.metadata)), Integer.valueOf(HidlSupport.deepHashCode(this.vendorInfo)));
    }

    public final String toString() {
        return "{.selector = " + this.selector + ", .logicallyTunedTo = " + this.logicallyTunedTo + ", .physicallyTunedTo = " + this.physicallyTunedTo + ", .relatedContent = " + this.relatedContent + ", .infoFlags = " + ProgramInfoFlags.dumpBitfield(this.infoFlags) + ", .signalQuality = " + this.signalQuality + ", .metadata = " + this.metadata + ", .vendorInfo = " + this.vendorInfo + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(120L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.selector.readEmbeddedFromParcel(hwParcel, hwBlob, j + 0);
        this.logicallyTunedTo.readEmbeddedFromParcel(hwParcel, hwBlob, j + 32);
        this.physicallyTunedTo.readEmbeddedFromParcel(hwParcel, hwBlob, j + 48);
        long j2 = j + 64;
        int int32 = hwBlob.getInt32(j2 + 8);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 16, hwBlob.handle(), j2 + 0, true);
        this.relatedContent.clear();
        for (int i = 0; i < int32; i++) {
            ProgramIdentifier programIdentifier = new ProgramIdentifier();
            programIdentifier.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 16);
            this.relatedContent.add(programIdentifier);
        }
        this.infoFlags = hwBlob.getInt32(j + 80);
        this.signalQuality = hwBlob.getInt32(j + 84);
        long j3 = j + 88;
        int int322 = hwBlob.getInt32(j3 + 8);
        HwBlob readEmbeddedBuffer2 = hwParcel.readEmbeddedBuffer(int322 * 32, hwBlob.handle(), j3 + 0, true);
        this.metadata.clear();
        for (int i2 = 0; i2 < int322; i2++) {
            Metadata metadata = new Metadata();
            metadata.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer2, i2 * 32);
            this.metadata.add(metadata);
        }
        long j4 = j + 104;
        int int323 = hwBlob.getInt32(8 + j4);
        HwBlob readEmbeddedBuffer3 = hwParcel.readEmbeddedBuffer(int323 * 32, hwBlob.handle(), j4 + 0, true);
        this.vendorInfo.clear();
        for (int i3 = 0; i3 < int323; i3++) {
            VendorKeyValue vendorKeyValue = new VendorKeyValue();
            vendorKeyValue.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer3, i3 * 32);
            this.vendorInfo.add(vendorKeyValue);
        }
    }
}
