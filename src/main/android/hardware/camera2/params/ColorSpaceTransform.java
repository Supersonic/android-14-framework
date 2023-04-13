package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.Rational;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class ColorSpaceTransform {
    private static final int COLUMNS = 3;
    private static final int COUNT = 9;
    private static final int COUNT_INT = 18;
    private static final int OFFSET_DENOMINATOR = 1;
    private static final int OFFSET_NUMERATOR = 0;
    private static final int RATIONAL_SIZE = 2;
    private static final int ROWS = 3;
    private final int[] mElements;

    public ColorSpaceTransform(Rational[] elements) {
        Preconditions.checkNotNull(elements, "elements must not be null");
        if (elements.length != 9) {
            throw new IllegalArgumentException("elements must be 9 length");
        }
        this.mElements = new int[18];
        for (int i = 0; i < elements.length; i++) {
            Preconditions.checkNotNull(elements, "element[%d] must not be null", Integer.valueOf(i));
            this.mElements[(i * 2) + 0] = elements[i].getNumerator();
            this.mElements[(i * 2) + 1] = elements[i].getDenominator();
        }
    }

    public ColorSpaceTransform(int[] elements) {
        Preconditions.checkNotNull(elements, "elements must not be null");
        if (elements.length != 18) {
            throw new IllegalArgumentException("elements must be 18 length");
        }
        for (int i = 0; i < elements.length; i++) {
            Preconditions.checkNotNull(elements, "element %d must not be null", Integer.valueOf(i));
        }
        int i2 = elements.length;
        this.mElements = Arrays.copyOf(elements, i2);
    }

    public Rational getElement(int column, int row) {
        if (column < 0 || column >= 3) {
            throw new IllegalArgumentException("column out of range");
        }
        if (row < 0 || row >= 3) {
            throw new IllegalArgumentException("row out of range");
        }
        int[] iArr = this.mElements;
        int numerator = iArr[(((row * 3) + column) * 2) + 0];
        int denominator = iArr[(((row * 3) + column) * 2) + 1];
        return new Rational(numerator, denominator);
    }

    public void copyElements(Rational[] destination, int offset) {
        Preconditions.checkArgumentNonnegative(offset, "offset must not be negative");
        Preconditions.checkNotNull(destination, "destination must not be null");
        if (destination.length - offset < 9) {
            throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
        }
        int i = 0;
        int j = 0;
        while (i < 9) {
            int[] iArr = this.mElements;
            int numerator = iArr[j + 0];
            int denominator = iArr[j + 1];
            destination[i + offset] = new Rational(numerator, denominator);
            i++;
            j += 2;
        }
    }

    public void copyElements(int[] destination, int offset) {
        Preconditions.checkArgumentNonnegative(offset, "offset must not be negative");
        Preconditions.checkNotNull(destination, "destination must not be null");
        if (destination.length - offset < 18) {
            throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
        }
        for (int i = 0; i < 18; i++) {
            destination[i + offset] = this.mElements[i];
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ColorSpaceTransform)) {
            return false;
        }
        ColorSpaceTransform other = (ColorSpaceTransform) obj;
        int i = 0;
        int j = 0;
        while (i < 9) {
            int[] iArr = this.mElements;
            int numerator = iArr[j + 0];
            int denominator = iArr[j + 1];
            int[] iArr2 = other.mElements;
            int numeratorOther = iArr2[j + 0];
            int denominatorOther = iArr2[j + 1];
            Rational r = new Rational(numerator, denominator);
            Rational rOther = new Rational(numeratorOther, denominatorOther);
            if (!r.equals((Object) rOther)) {
                return false;
            }
            i++;
            j += 2;
        }
        return true;
    }

    public int hashCode() {
        return HashCodeHelpers.hashCode(this.mElements);
    }

    public String toString() {
        return String.format("ColorSpaceTransform%s", toShortString());
    }

    private String toShortString() {
        StringBuilder sb = new StringBuilder(NavigationBarInflaterView.KEY_CODE_START);
        int i = 0;
        for (int row = 0; row < 3; row++) {
            sb.append(NavigationBarInflaterView.SIZE_MOD_START);
            int col = 0;
            while (col < 3) {
                int[] iArr = this.mElements;
                int numerator = iArr[i + 0];
                int denominator = iArr[i + 1];
                sb.append(numerator);
                sb.append("/");
                sb.append(denominator);
                if (col < 2) {
                    sb.append(", ");
                }
                col++;
                i += 2;
            }
            sb.append(NavigationBarInflaterView.SIZE_MOD_END);
            if (row < 2) {
                sb.append(", ");
            }
        }
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
        return sb.toString();
    }
}
