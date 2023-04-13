package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class ReprocessFormatsMap {
    private final int[] mEntry;
    private final int mInputCount;

    public ReprocessFormatsMap(int[] entry) {
        Preconditions.checkNotNull(entry, "entry must not be null");
        int numInputs = 0;
        int left = entry.length;
        int i = 0;
        while (i < entry.length) {
            int inputFormat = StreamConfigurationMap.checkArgumentFormatInternal(entry[i]);
            int left2 = left - 1;
            int i2 = i + 1;
            if (left2 < 1) {
                throw new IllegalArgumentException(String.format("Input %x had no output format length listed", Integer.valueOf(inputFormat)));
            }
            int length = entry[i2];
            left = left2 - 1;
            i = i2 + 1;
            for (int j = 0; j < length; j++) {
                int outputFormat = entry[i + j];
                StreamConfigurationMap.checkArgumentFormatInternal(outputFormat);
            }
            if (length > 0) {
                if (left < length) {
                    throw new IllegalArgumentException(String.format("Input %x had too few output formats listed (actual: %d, expected: %d)", Integer.valueOf(inputFormat), Integer.valueOf(left), Integer.valueOf(length)));
                }
                i += length;
                left -= length;
            }
            numInputs++;
        }
        this.mEntry = entry;
        this.mInputCount = numInputs;
    }

    public int[] getInputs() {
        int[] inputs = new int[this.mInputCount];
        int left = this.mEntry.length;
        int i = 0;
        int j = 0;
        while (true) {
            int[] iArr = this.mEntry;
            if (i < iArr.length) {
                int format = iArr[i];
                int left2 = left - 1;
                int i2 = i + 1;
                if (left2 < 1) {
                    throw new AssertionError(String.format("Input %x had no output format length listed", Integer.valueOf(format)));
                }
                int length = iArr[i2];
                left = left2 - 1;
                i = i2 + 1;
                if (length > 0) {
                    if (left < length) {
                        throw new AssertionError(String.format("Input %x had too few output formats listed (actual: %d, expected: %d)", Integer.valueOf(format), Integer.valueOf(left), Integer.valueOf(length)));
                    }
                    i += length;
                    left -= length;
                }
                inputs[j] = format;
                j++;
            } else {
                return StreamConfigurationMap.imageFormatToPublic(inputs);
            }
        }
    }

    public int[] getOutputs(int format) {
        int left = this.mEntry.length;
        int i = 0;
        while (true) {
            int[] iArr = this.mEntry;
            if (i < iArr.length) {
                int inputFormat = iArr[i];
                int left2 = left - 1;
                int i2 = i + 1;
                if (left2 < 1) {
                    throw new AssertionError(String.format("Input %x had no output format length listed", Integer.valueOf(format)));
                }
                int length = iArr[i2];
                int left3 = left2 - 1;
                int i3 = i2 + 1;
                if (length > 0 && left3 < length) {
                    throw new AssertionError(String.format("Input %x had too few output formats listed (actual: %d, expected: %d)", Integer.valueOf(format), Integer.valueOf(left3), Integer.valueOf(length)));
                }
                if (inputFormat == format) {
                    int[] outputs = new int[length];
                    for (int k = 0; k < length; k++) {
                        outputs[k] = this.mEntry[i3 + k];
                    }
                    return StreamConfigurationMap.imageFormatToPublic(outputs);
                }
                i = i3 + length;
                left = left3 - length;
            } else {
                throw new IllegalArgumentException(String.format("Input format %x was not one in #getInputs", Integer.valueOf(format)));
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ReprocessFormatsMap)) {
            return false;
        }
        ReprocessFormatsMap other = (ReprocessFormatsMap) obj;
        return Arrays.equals(this.mEntry, other.mEntry);
    }

    public int hashCode() {
        return HashCodeHelpers.hashCode(this.mEntry);
    }
}
