package android.hardware.broadcastradio.V2_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Properties {
    public String maker = new String();
    public String product = new String();
    public String version = new String();
    public String serial = new String();
    public ArrayList<Integer> supportedIdentifierTypes = new ArrayList<>();
    public ArrayList<VendorKeyValue> vendorInfo = new ArrayList<>();

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == Properties.class) {
            Properties properties = (Properties) obj;
            return HidlSupport.deepEquals(this.maker, properties.maker) && HidlSupport.deepEquals(this.product, properties.product) && HidlSupport.deepEquals(this.version, properties.version) && HidlSupport.deepEquals(this.serial, properties.serial) && HidlSupport.deepEquals(this.supportedIdentifierTypes, properties.supportedIdentifierTypes) && HidlSupport.deepEquals(this.vendorInfo, properties.vendorInfo);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.maker)), Integer.valueOf(HidlSupport.deepHashCode(this.product)), Integer.valueOf(HidlSupport.deepHashCode(this.version)), Integer.valueOf(HidlSupport.deepHashCode(this.serial)), Integer.valueOf(HidlSupport.deepHashCode(this.supportedIdentifierTypes)), Integer.valueOf(HidlSupport.deepHashCode(this.vendorInfo)));
    }

    public final String toString() {
        return "{.maker = " + this.maker + ", .product = " + this.product + ", .version = " + this.version + ", .serial = " + this.serial + ", .supportedIdentifierTypes = " + this.supportedIdentifierTypes + ", .vendorInfo = " + this.vendorInfo + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(96L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        String string;
        String string2;
        String string3;
        String string4;
        long j2 = j + 0;
        this.maker = hwBlob.getString(j2);
        hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
        long j3 = j + 16;
        this.product = hwBlob.getString(j3);
        hwParcel.readEmbeddedBuffer(string2.getBytes().length + 1, hwBlob.handle(), j3 + 0, false);
        long j4 = j + 32;
        this.version = hwBlob.getString(j4);
        hwParcel.readEmbeddedBuffer(string3.getBytes().length + 1, hwBlob.handle(), j4 + 0, false);
        long j5 = j + 48;
        this.serial = hwBlob.getString(j5);
        hwParcel.readEmbeddedBuffer(string4.getBytes().length + 1, hwBlob.handle(), j5 + 0, false);
        long j6 = j + 64;
        int int32 = hwBlob.getInt32(j6 + 8);
        HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 4, hwBlob.handle(), j6 + 0, true);
        this.supportedIdentifierTypes.clear();
        for (int i = 0; i < int32; i++) {
            this.supportedIdentifierTypes.add(Integer.valueOf(readEmbeddedBuffer.getInt32(i * 4)));
        }
        long j7 = j + 80;
        int int322 = hwBlob.getInt32(8 + j7);
        HwBlob readEmbeddedBuffer2 = hwParcel.readEmbeddedBuffer(int322 * 32, hwBlob.handle(), j7 + 0, true);
        this.vendorInfo.clear();
        for (int i2 = 0; i2 < int322; i2++) {
            VendorKeyValue vendorKeyValue = new VendorKeyValue();
            vendorKeyValue.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer2, i2 * 32);
            this.vendorInfo.add(vendorKeyValue);
        }
    }

    public final void writeToParcel(HwParcel hwParcel) {
        HwBlob hwBlob = new HwBlob(96);
        writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putString(j + 0, this.maker);
        hwBlob.putString(j + 16, this.product);
        hwBlob.putString(j + 32, this.version);
        hwBlob.putString(j + 48, this.serial);
        int size = this.supportedIdentifierTypes.size();
        long j2 = j + 64;
        hwBlob.putInt32(j2 + 8, size);
        hwBlob.putBool(j2 + 12, false);
        HwBlob hwBlob2 = new HwBlob(size * 4);
        for (int i = 0; i < size; i++) {
            hwBlob2.putInt32(i * 4, this.supportedIdentifierTypes.get(i).intValue());
        }
        hwBlob.putBlob(j2 + 0, hwBlob2);
        int size2 = this.vendorInfo.size();
        long j3 = j + 80;
        hwBlob.putInt32(8 + j3, size2);
        hwBlob.putBool(12 + j3, false);
        HwBlob hwBlob3 = new HwBlob(size2 * 32);
        for (int i2 = 0; i2 < size2; i2++) {
            this.vendorInfo.get(i2).writeEmbeddedToBlob(hwBlob3, i2 * 32);
        }
        hwBlob.putBlob(j3 + 0, hwBlob3);
    }
}
