package android.hardware.camera2.params;

import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public final class BlackLevelPattern {
    public static final int COUNT = 4;
    private final int[] mCfaOffsets;

    public BlackLevelPattern(int[] offsets) {
        if (offsets == null) {
            throw new NullPointerException("Null offsets array passed to constructor");
        }
        if (offsets.length < 4) {
            throw new IllegalArgumentException("Invalid offsets array length");
        }
        this.mCfaOffsets = Arrays.copyOf(offsets, 4);
    }

    public int getOffsetForIndex(int column, int row) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("column, row arguments must be positive");
        }
        return this.mCfaOffsets[((row & 1) << 1) | (column & 1)];
    }

    public void copyTo(int[] destination, int offset) {
        Objects.requireNonNull(destination, "destination must not be null");
        if (offset < 0) {
            throw new IllegalArgumentException("Null offset passed to copyTo");
        }
        if (destination.length - offset < 4) {
            throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
        }
        for (int i = 0; i < 4; i++) {
            destination[offset + i] = this.mCfaOffsets[i];
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BlackLevelPattern)) {
            return false;
        }
        BlackLevelPattern other = (BlackLevelPattern) obj;
        return Arrays.equals(other.mCfaOffsets, this.mCfaOffsets);
    }

    public int hashCode() {
        return Arrays.hashCode(this.mCfaOffsets);
    }

    public String toString() {
        return String.format("BlackLevelPattern([%d, %d], [%d, %d])", Integer.valueOf(this.mCfaOffsets[0]), Integer.valueOf(this.mCfaOffsets[1]), Integer.valueOf(this.mCfaOffsets[2]), Integer.valueOf(this.mCfaOffsets[3]));
    }
}
