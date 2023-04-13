package android.window;

import android.content.res.Configuration;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.SparseIntArray;
import java.util.Arrays;
/* loaded from: classes4.dex */
public final class SizeConfigurationBuckets implements Parcelable {
    public static final Parcelable.Creator<SizeConfigurationBuckets> CREATOR = new Parcelable.Creator<SizeConfigurationBuckets>() { // from class: android.window.SizeConfigurationBuckets.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SizeConfigurationBuckets[] newArray(int size) {
            return new SizeConfigurationBuckets[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SizeConfigurationBuckets createFromParcel(Parcel in) {
            return new SizeConfigurationBuckets(in);
        }
    };
    private final int[] mHorizontal;
    private final boolean mScreenLayoutLongSet;
    private final int[] mScreenLayoutSize;
    private final int[] mSmallest;
    private final int[] mVertical;

    public SizeConfigurationBuckets(Configuration[] sizeConfigurations) {
        SparseIntArray horizontal = new SparseIntArray();
        SparseIntArray vertical = new SparseIntArray();
        SparseIntArray smallest = new SparseIntArray();
        SparseIntArray screenLayoutSize = new SparseIntArray();
        boolean screenLayoutLongSet = false;
        for (int i = sizeConfigurations.length - 1; i >= 0; i--) {
            Configuration config = sizeConfigurations[i];
            if (config.screenHeightDp != 0) {
                vertical.put(config.screenHeightDp, 0);
            }
            if (config.screenWidthDp != 0) {
                horizontal.put(config.screenWidthDp, 0);
            }
            if (config.smallestScreenWidthDp != 0) {
                smallest.put(config.smallestScreenWidthDp, 0);
            }
            int curScreenLayoutSize = config.screenLayout & 15;
            if (curScreenLayoutSize != 0) {
                screenLayoutSize.put(curScreenLayoutSize, 0);
            }
            if (!screenLayoutLongSet && (config.screenLayout & 48) != 0) {
                screenLayoutLongSet = true;
            }
        }
        this.mHorizontal = horizontal.copyKeys();
        this.mVertical = vertical.copyKeys();
        this.mSmallest = smallest.copyKeys();
        this.mScreenLayoutSize = screenLayoutSize.copyKeys();
        this.mScreenLayoutLongSet = screenLayoutLongSet;
    }

    public static int filterDiff(int diff, Configuration oldConfig, Configuration newConfig, SizeConfigurationBuckets buckets) {
        if (buckets == null) {
            return diff;
        }
        boolean nonSizeLayoutFieldsUnchanged = areNonSizeLayoutFieldsUnchanged(oldConfig.screenLayout, newConfig.screenLayout);
        if ((diff & 1024) != 0) {
            boolean crosses = buckets.crossesHorizontalSizeThreshold(oldConfig.screenWidthDp, newConfig.screenWidthDp) || buckets.crossesVerticalSizeThreshold(oldConfig.screenHeightDp, newConfig.screenHeightDp);
            if (!crosses) {
                diff &= -1025;
            }
        }
        if ((diff & 2048) != 0) {
            int oldSmallest = oldConfig.smallestScreenWidthDp;
            int newSmallest = newConfig.smallestScreenWidthDp;
            if (!buckets.crossesSmallestSizeThreshold(oldSmallest, newSmallest)) {
                diff &= -2049;
            }
        }
        int oldSmallest2 = diff & 256;
        if (oldSmallest2 != 0 && nonSizeLayoutFieldsUnchanged && !buckets.crossesScreenLayoutSizeThreshold(oldConfig, newConfig) && !buckets.crossesScreenLayoutLongThreshold(oldConfig.screenLayout, newConfig.screenLayout)) {
            return diff & (-257);
        }
        return diff;
    }

    private boolean crossesHorizontalSizeThreshold(int firstDp, int secondDp) {
        return crossesSizeThreshold(this.mHorizontal, firstDp, secondDp);
    }

    private boolean crossesVerticalSizeThreshold(int firstDp, int secondDp) {
        return crossesSizeThreshold(this.mVertical, firstDp, secondDp);
    }

    private boolean crossesSmallestSizeThreshold(int firstDp, int secondDp) {
        return crossesSizeThreshold(this.mSmallest, firstDp, secondDp);
    }

    public boolean crossesScreenLayoutSizeThreshold(Configuration firstConfig, Configuration secondConfig) {
        if ((firstConfig.screenLayout & 15) == (secondConfig.screenLayout & 15)) {
            return false;
        }
        if (secondConfig.isLayoutSizeAtLeast(firstConfig.screenLayout & 15)) {
            int[] iArr = this.mScreenLayoutSize;
            if (iArr != null) {
                for (int screenLayoutSize : iArr) {
                    if (firstConfig.isLayoutSizeAtLeast(screenLayoutSize) != secondConfig.isLayoutSizeAtLeast(screenLayoutSize)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    private boolean crossesScreenLayoutLongThreshold(int firstScreenLayout, int secondScreenLayout) {
        int firstScreenLayoutLongValue = firstScreenLayout & 48;
        int secondScreenLayoutLongValue = secondScreenLayout & 48;
        return this.mScreenLayoutLongSet && firstScreenLayoutLongValue != secondScreenLayoutLongValue;
    }

    public static boolean areNonSizeLayoutFieldsUnchanged(int oldScreenLayout, int newScreenLayout) {
        return (oldScreenLayout & 268436416) == (268436416 & newScreenLayout);
    }

    public static boolean crossesSizeThreshold(int[] thresholds, int firstDp, int secondDp) {
        if (thresholds == null) {
            return false;
        }
        for (int i = thresholds.length - 1; i >= 0; i--) {
            int threshold = thresholds[i];
            if ((firstDp < threshold && secondDp >= threshold) || (firstDp >= threshold && secondDp < threshold)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return Arrays.toString(this.mHorizontal) + " " + Arrays.toString(this.mVertical) + " " + Arrays.toString(this.mSmallest) + " " + Arrays.toString(this.mScreenLayoutSize) + " " + this.mScreenLayoutLongSet;
    }

    public SizeConfigurationBuckets(int[] horizontal, int[] vertical, int[] smallest, int[] screenLayoutSize, boolean screenLayoutLongSet) {
        this.mHorizontal = horizontal;
        this.mVertical = vertical;
        this.mSmallest = smallest;
        this.mScreenLayoutSize = screenLayoutSize;
        this.mScreenLayoutLongSet = screenLayoutLongSet;
    }

    public int[] getHorizontal() {
        return this.mHorizontal;
    }

    public int[] getVertical() {
        return this.mVertical;
    }

    public int[] getSmallest() {
        return this.mSmallest;
    }

    public int[] getScreenLayoutSize() {
        return this.mScreenLayoutSize;
    }

    public boolean isScreenLayoutLongSet() {
        return this.mScreenLayoutLongSet;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mScreenLayoutLongSet ? (byte) (0 | 16) : (byte) 0;
        if (this.mHorizontal != null) {
            flg = (byte) (flg | 1);
        }
        if (this.mVertical != null) {
            flg = (byte) (flg | 2);
        }
        if (this.mSmallest != null) {
            flg = (byte) (flg | 4);
        }
        if (this.mScreenLayoutSize != null) {
            flg = (byte) (flg | 8);
        }
        dest.writeByte(flg);
        int[] iArr = this.mHorizontal;
        if (iArr != null) {
            dest.writeIntArray(iArr);
        }
        int[] iArr2 = this.mVertical;
        if (iArr2 != null) {
            dest.writeIntArray(iArr2);
        }
        int[] iArr3 = this.mSmallest;
        if (iArr3 != null) {
            dest.writeIntArray(iArr3);
        }
        int[] iArr4 = this.mScreenLayoutSize;
        if (iArr4 != null) {
            dest.writeIntArray(iArr4);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    SizeConfigurationBuckets(Parcel in) {
        byte flg = in.readByte();
        boolean screenLayoutLongSet = (flg & 16) != 0;
        int[] horizontal = (flg & 1) == 0 ? null : in.createIntArray();
        int[] vertical = (flg & 2) == 0 ? null : in.createIntArray();
        int[] smallest = (flg & 4) == 0 ? null : in.createIntArray();
        int[] screenLayoutSize = (flg & 8) != 0 ? in.createIntArray() : null;
        this.mHorizontal = horizontal;
        this.mVertical = vertical;
        this.mSmallest = smallest;
        this.mScreenLayoutSize = screenLayoutSize;
        this.mScreenLayoutLongSet = screenLayoutLongSet;
    }

    @Deprecated
    private void __metadata() {
    }
}
