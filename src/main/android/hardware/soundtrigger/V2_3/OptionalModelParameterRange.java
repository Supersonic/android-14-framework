package android.hardware.soundtrigger.V2_3;

import android.hidl.safe_union.V1_0.Monostate;
import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class OptionalModelParameterRange {
    public byte hidl_d = 0;
    public Object hidl_o;

    /* loaded from: classes.dex */
    public static final class hidl_discriminator {
        public static final String getName(byte b) {
            return b != 0 ? b != 1 ? "Unknown" : "range" : "noinit";
        }
    }

    public OptionalModelParameterRange() {
        this.hidl_o = null;
        this.hidl_o = new Monostate();
    }

    public Monostate noinit() {
        if (this.hidl_d != 0) {
            Object obj = this.hidl_o;
            String name = obj != null ? obj.getClass().getName() : "null";
            throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + name + ".");
        }
        Object obj2 = this.hidl_o;
        if (obj2 != null && !Monostate.class.isInstance(obj2)) {
            throw new Error("Union is in a corrupted state.");
        }
        return (Monostate) this.hidl_o;
    }

    public ModelParameterRange range() {
        if (this.hidl_d != 1) {
            Object obj = this.hidl_o;
            String name = obj != null ? obj.getClass().getName() : "null";
            throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + name + ".");
        }
        Object obj2 = this.hidl_o;
        if (obj2 != null && !ModelParameterRange.class.isInstance(obj2)) {
            throw new Error("Union is in a corrupted state.");
        }
        return (ModelParameterRange) this.hidl_o;
    }

    public byte getDiscriminator() {
        return this.hidl_d;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == OptionalModelParameterRange.class) {
            OptionalModelParameterRange optionalModelParameterRange = (OptionalModelParameterRange) obj;
            return this.hidl_d == optionalModelParameterRange.hidl_d && HidlSupport.deepEquals(this.hidl_o, optionalModelParameterRange.hidl_o);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.hidl_o)), Integer.valueOf(Objects.hashCode(Byte.valueOf(this.hidl_d))));
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        byte b = this.hidl_d;
        if (b == 0) {
            sb.append(".noinit = ");
            sb.append(noinit());
        } else if (b == 1) {
            sb.append(".range = ");
            sb.append(range());
        } else {
            throw new Error("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
        }
        sb.append("}");
        return sb.toString();
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(12L), 0L);
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        byte int8 = hwBlob.getInt8(0 + j);
        this.hidl_d = int8;
        if (int8 == 0) {
            Monostate monostate = new Monostate();
            this.hidl_o = monostate;
            monostate.readEmbeddedFromParcel(hwParcel, hwBlob, j + 4);
        } else if (int8 == 1) {
            ModelParameterRange modelParameterRange = new ModelParameterRange();
            this.hidl_o = modelParameterRange;
            modelParameterRange.readEmbeddedFromParcel(hwParcel, hwBlob, j + 4);
        } else {
            throw new IllegalStateException("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
        }
    }

    public final void writeToParcel(HwParcel hwParcel) {
        HwBlob hwBlob = new HwBlob(12);
        writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putInt8(0 + j, this.hidl_d);
        byte b = this.hidl_d;
        if (b == 0) {
            noinit().writeEmbeddedToBlob(hwBlob, j + 4);
        } else if (b == 1) {
            range().writeEmbeddedToBlob(hwBlob, j + 4);
        } else {
            throw new Error("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
        }
    }
}
