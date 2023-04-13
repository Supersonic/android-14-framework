package android.hardware.radio.config.V1_1;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ModemInfo {
    public byte modemId = 0;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && obj.getClass() == ModemInfo.class && this.modemId == ((ModemInfo) obj).modemId;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.modemId))));
    }

    public final String toString() {
        return "{.modemId = " + ((int) this.modemId) + "}";
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        this.modemId = hwBlob.getInt8(j + 0);
    }
}
