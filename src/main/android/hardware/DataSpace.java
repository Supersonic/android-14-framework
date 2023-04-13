package android.hardware;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public final class DataSpace {
    public static final int DATASPACE_ADOBE_RGB = 151715840;
    public static final int DATASPACE_BT2020 = 147193856;
    public static final int DATASPACE_BT2020_HLG = 168165376;
    public static final int DATASPACE_BT2020_PQ = 163971072;
    public static final int DATASPACE_BT601_525 = 281280512;
    public static final int DATASPACE_BT601_625 = 281149440;
    public static final int DATASPACE_BT709 = 281083904;
    public static final int DATASPACE_DCI_P3 = 155844608;
    public static final int DATASPACE_DEPTH = 4096;
    public static final int DATASPACE_DISPLAY_P3 = 143261696;
    public static final int DATASPACE_DYNAMIC_DEPTH = 4098;
    public static final int DATASPACE_HEIF = 4100;
    public static final int DATASPACE_JFIF = 146931712;
    public static final int DATASPACE_JPEG_R = 4101;
    public static final int DATASPACE_SCRGB = 411107328;
    public static final int DATASPACE_SCRGB_LINEAR = 406913024;
    public static final int DATASPACE_SRGB = 142671872;
    public static final int DATASPACE_SRGB_LINEAR = 138477568;
    public static final int DATASPACE_UNKNOWN = 0;
    public static final int RANGE_EXTENDED = 402653184;
    public static final int RANGE_FULL = 134217728;
    public static final int RANGE_LIMITED = 268435456;
    private static final int RANGE_MASK = 939524096;
    public static final int RANGE_UNSPECIFIED = 0;
    public static final int STANDARD_ADOBE_RGB = 720896;
    public static final int STANDARD_BT2020 = 393216;
    public static final int STANDARD_BT2020_CONSTANT_LUMINANCE = 458752;
    public static final int STANDARD_BT470M = 524288;
    public static final int STANDARD_BT601_525 = 262144;
    public static final int STANDARD_BT601_525_UNADJUSTED = 327680;
    public static final int STANDARD_BT601_625 = 131072;
    public static final int STANDARD_BT601_625_UNADJUSTED = 196608;
    public static final int STANDARD_BT709 = 65536;
    public static final int STANDARD_DCI_P3 = 655360;
    public static final int STANDARD_FILM = 589824;
    private static final int STANDARD_MASK = 4128768;
    public static final int STANDARD_UNSPECIFIED = 0;
    public static final int TRANSFER_GAMMA2_2 = 16777216;
    public static final int TRANSFER_GAMMA2_6 = 20971520;
    public static final int TRANSFER_GAMMA2_8 = 25165824;
    public static final int TRANSFER_HLG = 33554432;
    public static final int TRANSFER_LINEAR = 4194304;
    private static final int TRANSFER_MASK = 130023424;
    public static final int TRANSFER_SMPTE_170M = 12582912;
    public static final int TRANSFER_SRGB = 8388608;
    public static final int TRANSFER_ST2084 = 29360128;
    public static final int TRANSFER_UNSPECIFIED = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ColorDataSpace {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DataSpaceRange {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DataSpaceStandard {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DataSpaceTransfer {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface NamedDataSpace {
    }

    private DataSpace() {
    }

    public static int pack(int standard, int transfer, int range) {
        if ((4128768 & standard) != standard) {
            throw new IllegalArgumentException("Invalid standard " + standard);
        }
        if ((130023424 & transfer) != transfer) {
            throw new IllegalArgumentException("Invalid transfer " + transfer);
        }
        if ((939524096 & range) != range) {
            throw new IllegalArgumentException("Invalid range " + range);
        }
        return standard | transfer | range;
    }

    public static int getStandard(int dataSpace) {
        int standard = 4128768 & dataSpace;
        return standard;
    }

    public static int getTransfer(int dataSpace) {
        int transfer = 130023424 & dataSpace;
        return transfer;
    }

    public static int getRange(int dataSpace) {
        int range = 939524096 & dataSpace;
        return range;
    }
}
