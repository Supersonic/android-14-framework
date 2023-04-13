package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public final class OisSample {
    private final long mTimestampNs;
    private final float mXShift;
    private final float mYShift;

    public OisSample(long timestamp, float xShift, float yShift) {
        this.mTimestampNs = timestamp;
        this.mXShift = Preconditions.checkArgumentFinite(xShift, "xShift must be finite");
        this.mYShift = Preconditions.checkArgumentFinite(yShift, "yShift must be finite");
    }

    public long getTimestamp() {
        return this.mTimestampNs;
    }

    public float getXshift() {
        return this.mXShift;
    }

    public float getYshift() {
        return this.mYShift;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OisSample)) {
            return false;
        }
        OisSample other = (OisSample) obj;
        if (this.mTimestampNs != other.mTimestampNs || this.mXShift != other.mXShift || this.mYShift != other.mYShift) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int timestampHash = HashCodeHelpers.hashCode((float) this.mTimestampNs);
        return HashCodeHelpers.hashCode(this.mXShift, this.mYShift, timestampHash);
    }

    public String toString() {
        return String.format("OisSample{timestamp:%d, shift_x:%f, shift_y:%f}", Long.valueOf(this.mTimestampNs), Float.valueOf(this.mXShift), Float.valueOf(this.mYShift));
    }
}
