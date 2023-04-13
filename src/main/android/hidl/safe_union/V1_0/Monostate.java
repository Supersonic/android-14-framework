package android.hidl.safe_union.V1_0;

import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class Monostate {
    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == Monostate.class) {
            Monostate monostate = (Monostate) obj;
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(new Object[0]);
    }

    public final String toString() {
        return "{}";
    }
}
