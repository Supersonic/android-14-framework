package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import android.util.Range;
import android.util.Size;
import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public final class Capability {
    public static final int COUNT = 3;
    private final Size mMaxStreamingSize;
    private final int mMode;
    private final Range<Float> mZoomRatioRange;

    public Capability(int mode, Size maxStreamingSize, Range<Float> zoomRatioRange) {
        this.mMode = mode;
        Preconditions.checkArgumentNonnegative(maxStreamingSize.getWidth(), "maxStreamingSize.getWidth() must be nonnegative");
        Preconditions.checkArgumentNonnegative(maxStreamingSize.getHeight(), "maxStreamingSize.getHeight() must be nonnegative");
        this.mMaxStreamingSize = maxStreamingSize;
        if (zoomRatioRange.getLower().floatValue() > zoomRatioRange.getUpper().floatValue()) {
            throw new IllegalArgumentException("zoomRatioRange.getLower() " + zoomRatioRange.getLower() + " is greater than zoomRatioRange.getUpper() " + zoomRatioRange.getUpper());
        }
        Preconditions.checkArgumentPositive(zoomRatioRange.getLower().floatValue(), "zoomRatioRange.getLower() must be positive");
        Preconditions.checkArgumentPositive(zoomRatioRange.getUpper().floatValue(), "zoomRatioRange.getUpper() must be positive");
        this.mZoomRatioRange = zoomRatioRange;
    }

    public int getMode() {
        return this.mMode;
    }

    public Size getMaxStreamingSize() {
        return this.mMaxStreamingSize;
    }

    public Range<Float> getZoomRatioRange() {
        return this.mZoomRatioRange;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Capability)) {
            return false;
        }
        Capability other = (Capability) obj;
        if (this.mMode != other.mMode || !this.mMaxStreamingSize.equals(other.mMaxStreamingSize) || !this.mZoomRatioRange.equals(other.mZoomRatioRange)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return HashCodeHelpers.hashCode(this.mMode, this.mMaxStreamingSize.getWidth(), this.mMaxStreamingSize.getHeight(), this.mZoomRatioRange.getLower().floatValue(), this.mZoomRatioRange.getUpper().floatValue());
    }

    public String toString() {
        return String.format("(mode:%d, maxStreamingSize:%d x %d, zoomRatio: %f-%f)", Integer.valueOf(this.mMode), Integer.valueOf(this.mMaxStreamingSize.getWidth()), Integer.valueOf(this.mMaxStreamingSize.getHeight()), this.mZoomRatioRange.getLower(), this.mZoomRatioRange.getUpper());
    }
}
