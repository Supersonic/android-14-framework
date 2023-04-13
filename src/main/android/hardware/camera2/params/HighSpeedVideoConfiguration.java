package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import android.util.Range;
import android.util.Size;
import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public final class HighSpeedVideoConfiguration {
    private static final int HIGH_SPEED_MAX_MINIMAL_FPS = 120;
    private final int mBatchSizeMax;
    private final int mFpsMax;
    private final int mFpsMin;
    private final Range<Integer> mFpsRange;
    private final int mHeight;
    private final Size mSize;
    private final int mWidth;

    public HighSpeedVideoConfiguration(int width, int height, int fpsMin, int fpsMax, int batchSizeMax) {
        if (fpsMax < 120) {
            throw new IllegalArgumentException("fpsMax must be at least 120");
        }
        this.mFpsMax = fpsMax;
        int checkArgumentPositive = Preconditions.checkArgumentPositive(width, "width must be positive");
        this.mWidth = checkArgumentPositive;
        int checkArgumentPositive2 = Preconditions.checkArgumentPositive(height, "height must be positive");
        this.mHeight = checkArgumentPositive2;
        int checkArgumentPositive3 = Preconditions.checkArgumentPositive(fpsMin, "fpsMin must be positive");
        this.mFpsMin = checkArgumentPositive3;
        this.mSize = new Size(checkArgumentPositive, checkArgumentPositive2);
        this.mBatchSizeMax = Preconditions.checkArgumentPositive(batchSizeMax, "batchSizeMax must be positive");
        this.mFpsRange = new Range<>(Integer.valueOf(checkArgumentPositive3), Integer.valueOf(fpsMax));
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getFpsMin() {
        return this.mFpsMin;
    }

    public int getFpsMax() {
        return this.mFpsMax;
    }

    public Size getSize() {
        return this.mSize;
    }

    public int getBatchSizeMax() {
        return this.mBatchSizeMax;
    }

    public Range<Integer> getFpsRange() {
        return this.mFpsRange;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HighSpeedVideoConfiguration)) {
            return false;
        }
        HighSpeedVideoConfiguration other = (HighSpeedVideoConfiguration) obj;
        if (this.mWidth != other.mWidth || this.mHeight != other.mHeight || this.mFpsMin != other.mFpsMin || this.mFpsMax != other.mFpsMax || this.mBatchSizeMax != other.mBatchSizeMax) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return HashCodeHelpers.hashCode(this.mWidth, this.mHeight, this.mFpsMin, this.mFpsMax);
    }
}
