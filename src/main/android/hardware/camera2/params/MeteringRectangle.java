package android.hardware.camera2.params;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.utils.HashCodeHelpers;
import android.util.Size;
import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public final class MeteringRectangle {
    public static final int METERING_WEIGHT_DONT_CARE = 0;
    public static final int METERING_WEIGHT_MAX = 1000;
    public static final int METERING_WEIGHT_MIN = 0;
    private final int mHeight;
    private final int mWeight;
    private final int mWidth;

    /* renamed from: mX */
    private final int f116mX;

    /* renamed from: mY */
    private final int f117mY;

    public MeteringRectangle(int x, int y, int width, int height, int meteringWeight) {
        this.f116mX = Preconditions.checkArgumentNonnegative(x, "x must be nonnegative");
        this.f117mY = Preconditions.checkArgumentNonnegative(y, "y must be nonnegative");
        this.mWidth = Preconditions.checkArgumentNonnegative(width, "width must be nonnegative");
        this.mHeight = Preconditions.checkArgumentNonnegative(height, "height must be nonnegative");
        this.mWeight = Preconditions.checkArgumentInRange(meteringWeight, 0, 1000, "meteringWeight");
    }

    public MeteringRectangle(Point xy, Size dimensions, int meteringWeight) {
        Preconditions.checkNotNull(xy, "xy must not be null");
        Preconditions.checkNotNull(dimensions, "dimensions must not be null");
        this.f116mX = Preconditions.checkArgumentNonnegative(xy.f76x, "x must be nonnegative");
        this.f117mY = Preconditions.checkArgumentNonnegative(xy.f77y, "y must be nonnegative");
        this.mWidth = Preconditions.checkArgumentNonnegative(dimensions.getWidth(), "width must be nonnegative");
        this.mHeight = Preconditions.checkArgumentNonnegative(dimensions.getHeight(), "height must be nonnegative");
        this.mWeight = Preconditions.checkArgumentNonnegative(meteringWeight, "meteringWeight must be nonnegative");
    }

    public MeteringRectangle(Rect rect, int meteringWeight) {
        Preconditions.checkNotNull(rect, "rect must not be null");
        this.f116mX = Preconditions.checkArgumentNonnegative(rect.left, "rect.left must be nonnegative");
        this.f117mY = Preconditions.checkArgumentNonnegative(rect.top, "rect.top must be nonnegative");
        this.mWidth = Preconditions.checkArgumentNonnegative(rect.width(), "rect.width must be nonnegative");
        this.mHeight = Preconditions.checkArgumentNonnegative(rect.height(), "rect.height must be nonnegative");
        this.mWeight = Preconditions.checkArgumentNonnegative(meteringWeight, "meteringWeight must be nonnegative");
    }

    public int getX() {
        return this.f116mX;
    }

    public int getY() {
        return this.f117mY;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getMeteringWeight() {
        return this.mWeight;
    }

    public Point getUpperLeftPoint() {
        return new Point(this.f116mX, this.f117mY);
    }

    public Size getSize() {
        return new Size(this.mWidth, this.mHeight);
    }

    public Rect getRect() {
        int i = this.f116mX;
        int i2 = this.f117mY;
        return new Rect(i, i2, this.mWidth + i, this.mHeight + i2);
    }

    public boolean equals(Object other) {
        return (other instanceof MeteringRectangle) && equals((MeteringRectangle) other);
    }

    public boolean equals(MeteringRectangle other) {
        return other != null && this.f116mX == other.f116mX && this.f117mY == other.f117mY && this.mWidth == other.mWidth && this.mHeight == other.mHeight && this.mWeight == other.mWeight;
    }

    public int hashCode() {
        return HashCodeHelpers.hashCode(this.f116mX, this.f117mY, this.mWidth, this.mHeight, this.mWeight);
    }

    public String toString() {
        return String.format("(x:%d, y:%d, w:%d, h:%d, wt:%d)", Integer.valueOf(this.f116mX), Integer.valueOf(this.f117mY), Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), Integer.valueOf(this.mWeight));
    }
}
