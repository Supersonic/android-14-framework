package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import com.android.internal.util.Preconditions;
import java.util.Collection;
/* loaded from: classes.dex */
public final class InputConfiguration {
    private final int mFormat;
    private final int mHeight;
    private final boolean mIsMultiResolution;
    private final int mWidth;

    public InputConfiguration(int width, int height, int format) {
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = format;
        this.mIsMultiResolution = false;
    }

    public InputConfiguration(Collection<MultiResolutionStreamInfo> multiResolutionInputs, int format) {
        Preconditions.checkCollectionNotEmpty(multiResolutionInputs, "Input multi-resolution stream info");
        MultiResolutionStreamInfo info = multiResolutionInputs.iterator().next();
        this.mWidth = info.getWidth();
        this.mHeight = info.getHeight();
        this.mFormat = format;
        this.mIsMultiResolution = true;
    }

    public InputConfiguration(int width, int height, int format, boolean isMultiResolution) {
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = format;
        this.mIsMultiResolution = isMultiResolution;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getFormat() {
        return this.mFormat;
    }

    public boolean isMultiResolution() {
        return this.mIsMultiResolution;
    }

    public boolean equals(Object obj) {
        if (obj instanceof InputConfiguration) {
            InputConfiguration otherInputConfig = (InputConfiguration) obj;
            return otherInputConfig.getWidth() == this.mWidth && otherInputConfig.getHeight() == this.mHeight && otherInputConfig.getFormat() == this.mFormat && otherInputConfig.isMultiResolution() == this.mIsMultiResolution;
        }
        return false;
    }

    public int hashCode() {
        return HashCodeHelpers.hashCode(this.mWidth, this.mHeight, this.mFormat, this.mIsMultiResolution ? 1 : 0);
    }

    public String toString() {
        return String.format("InputConfiguration(w:%d, h:%d, format:%d, isMultiResolution %b)", Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), Integer.valueOf(this.mFormat), Boolean.valueOf(this.mIsMultiResolution));
    }
}
