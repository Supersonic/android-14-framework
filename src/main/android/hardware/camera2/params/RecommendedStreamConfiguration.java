package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
/* loaded from: classes.dex */
public final class RecommendedStreamConfiguration extends StreamConfiguration {
    private final int mUsecaseBitmap;

    public RecommendedStreamConfiguration(int format, int width, int height, boolean input, int usecaseBitmap) {
        super(format, width, height, input);
        this.mUsecaseBitmap = usecaseBitmap;
    }

    public int getUsecaseBitmap() {
        return this.mUsecaseBitmap;
    }

    @Override // android.hardware.camera2.params.StreamConfiguration
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RecommendedStreamConfiguration)) {
            return false;
        }
        RecommendedStreamConfiguration other = (RecommendedStreamConfiguration) obj;
        if (this.mFormat != other.mFormat || this.mWidth != other.mWidth || this.mHeight != other.mHeight || this.mUsecaseBitmap != other.mUsecaseBitmap || this.mInput != other.mInput) {
            return false;
        }
        return true;
    }

    @Override // android.hardware.camera2.params.StreamConfiguration
    public int hashCode() {
        return HashCodeHelpers.hashCode(this.mFormat, this.mWidth, this.mHeight, this.mInput ? 1 : 0, this.mUsecaseBitmap);
    }
}
