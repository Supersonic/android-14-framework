package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import android.util.Size;
import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public final class StreamConfigurationDuration {
    private final long mDurationNs;
    private final int mFormat;
    private final int mHeight;
    private final int mWidth;

    public StreamConfigurationDuration(int format, int width, int height, long durationNs) {
        this.mFormat = StreamConfigurationMap.checkArgumentFormatInternal(format);
        this.mWidth = Preconditions.checkArgumentPositive(width, "width must be positive");
        this.mHeight = Preconditions.checkArgumentPositive(height, "height must be positive");
        this.mDurationNs = Preconditions.checkArgumentNonnegative(durationNs, "durationNs must be non-negative");
    }

    public final int getFormat() {
        return this.mFormat;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public Size getSize() {
        return new Size(this.mWidth, this.mHeight);
    }

    public long getDuration() {
        return this.mDurationNs;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StreamConfigurationDuration)) {
            return false;
        }
        StreamConfigurationDuration other = (StreamConfigurationDuration) obj;
        if (this.mFormat != other.mFormat || this.mWidth != other.mWidth || this.mHeight != other.mHeight || this.mDurationNs != other.mDurationNs) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int i = this.mFormat;
        int i2 = this.mWidth;
        int i3 = this.mHeight;
        long j = this.mDurationNs;
        return HashCodeHelpers.hashCode(i, i2, i3, (int) j, (int) (j >>> 32));
    }
}
