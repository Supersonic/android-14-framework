package com.android.internal.location.altitude;

import android.hardware.gnss.GnssSignalType;
import android.media.AudioSystem;
import android.view.View;
import java.util.Arrays;
import java.util.Locale;
/* loaded from: classes4.dex */
public final class S2CellIdUtils {
    private static final int INVERT_MASK = 2;
    private static final int I_SHIFT = 33;
    private static final long J_MASK = 2147483647L;
    private static final int J_SHIFT = 2;
    private static final int LEAF_MASK = 1;
    private static final int LOOKUP_BITS = 4;
    private static final int LOOKUP_MASK = 15;
    public static final int MAX_LEVEL = 30;
    private static final int MAX_SIZE = 1073741824;
    private static final int NUM_FACES = 6;
    private static final double ONE_OVER_MAX_SIZE = 9.313225746154785E-10d;
    private static final int POS_BITS = 61;
    private static final int SWAP_MASK = 1;
    private static final int[] LOOKUP_POS = new int[1024];
    private static final int[] LOOKUP_IJ = new int[1024];
    private static final int[] POS_TO_ORIENTATION = {1, 0, 0, 3};
    private static final int[][] POS_TO_IJ = {new int[]{0, 1, 3, 2}, new int[]{0, 2, 3, 1}, new int[]{3, 2, 0, 1}, new int[]{3, 1, 0, 2}};
    private static final double UV_LIMIT = calculateUvLimit();
    private static final UvTransform[] UV_TRANSFORMS = createUvTransforms();
    private static final XyzTransform[] XYZ_TRANSFORMS = createXyzTransforms();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface UvTransform {
        double xyzToU(double d, double d2, double d3);

        double xyzToV(double d, double d2, double d3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface XyzTransform {
        double uvToX(double d, double d2);

        double uvToY(double d, double d2);

        double uvToZ(double d, double d2);
    }

    static {
        initLookupCells();
    }

    private S2CellIdUtils() {
    }

    public static long fromLatLngDegrees(double latDegrees, double lngDegrees) {
        return fromLatLngRadians(Math.toRadians(latDegrees), Math.toRadians(lngDegrees));
    }

    public static long getParent(long s2CellId, int level) {
        long newLsb = getLowestOnBitForLevel(level);
        return ((-newLsb) & s2CellId) | newLsb;
    }

    public static void getEdgeNeighbors(long s2CellId, long[] neighbors) {
        int level = getLevel(s2CellId);
        int size = levelToSizeIj(level);
        int face = getFace(s2CellId);
        long ijo = toIjo(s2CellId);
        int i = ijoToI(ijo);
        int j = ijoToJ(ijo);
        int iPlusSize = i + size;
        int iMinusSize = i - size;
        int jPlusSize = j + size;
        int jMinusSize = j - size;
        boolean iPlusSizeLtMax = iPlusSize < 1073741824;
        boolean iMinusSizeGteZero = iMinusSize >= 0;
        boolean jPlusSizeLtMax = jPlusSize < 1073741824;
        boolean jMinusSizeGteZero = jMinusSize >= 0;
        int index = 0 + 1;
        neighbors[0] = getParent(fromFijSame(face, i, jMinusSize, jMinusSizeGteZero), level);
        int index2 = index + 1;
        neighbors[index] = getParent(fromFijSame(face, iPlusSize, j, iPlusSizeLtMax), level);
        int index3 = index2 + 1;
        neighbors[index2] = getParent(fromFijSame(face, i, jPlusSize, jPlusSizeLtMax), level);
        neighbors[index3] = getParent(fromFijSame(face, iMinusSize, j, iMinusSizeGteZero), level);
        Arrays.fill(neighbors, index3 + 1, neighbors.length, 0L);
    }

    public static int getI(long s2CellId) {
        return ijoToI(toIjo(s2CellId));
    }

    public static int getJ(long s2CellId) {
        return ijoToJ(toIjo(s2CellId));
    }

    private static long fromLatLngRadians(double latRadians, double lngRadians) {
        double cosLat = Math.cos(latRadians);
        double x = Math.cos(lngRadians) * cosLat;
        double y = Math.sin(lngRadians) * cosLat;
        double z = Math.sin(latRadians);
        return fromXyz(x, y, z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getLevel(long s2CellId) {
        if (isLeaf(s2CellId)) {
            return 30;
        }
        return 30 - (Long.numberOfTrailingZeros(s2CellId) >> 1);
    }

    static long getLowestOnBit(long s2CellId) {
        return (-s2CellId) & s2CellId;
    }

    static long getLowestOnBitForLevel(int level) {
        return 1 << ((30 - level) * 2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getTraversalStart(long s2CellId, int level) {
        return (s2CellId - getLowestOnBit(s2CellId)) + getLowestOnBitForLevel(level);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getTraversalNext(long s2CellId) {
        return (getLowestOnBit(s2CellId) << 1) + s2CellId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getToken(long s2CellId) {
        if (s2CellId == 0) {
            return GnssSignalType.CODE_TYPE_X;
        }
        String hex = Long.toHexString(s2CellId).toLowerCase(Locale.US);
        String padded = padStart(hex);
        return padded.replaceAll("0*$", "");
    }

    private static String padStart(String string) {
        if (string.length() >= 16) {
            return string;
        }
        return AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS.repeat(16 - string.length()) + string;
    }

    private static long fromXyz(double x, double y, double z) {
        int face = xyzToFace(x, y, z);
        UvTransform uvTransform = UV_TRANSFORMS[face];
        double u = uvTransform.xyzToU(x, y, z);
        double v = uvTransform.xyzToV(x, y, z);
        return fromFuv(face, u, v);
    }

    private static long fromFuv(int face, double u, double v) {
        int i = uToI(u);
        int j = vToJ(v);
        return fromFij(face, i, j);
    }

    private static long fromFij(int face, int i, int j) {
        int bits = face & 1;
        long msb = face << 28;
        for (int k = 7; k >= 4; k--) {
            int bits2 = lookupBits(i, j, k, bits);
            msb = updateBits(msb, k, bits2);
            bits = maskBits(bits2);
        }
        long lsb = 0;
        for (int k2 = 3; k2 >= 0; k2--) {
            int bits3 = lookupBits(i, j, k2, bits);
            lsb = updateBits(lsb, k2, bits3);
            bits = maskBits(bits3);
        }
        return (((msb << 32) + lsb) << 1) + 1;
    }

    private static long fromFijWrap(int face, int i, int j) {
        double u = iToU(i);
        double v = jToV(j);
        XyzTransform xyzTransform = XYZ_TRANSFORMS[face];
        double x = xyzTransform.uvToX(u, v);
        double y = xyzTransform.uvToY(u, v);
        double z = xyzTransform.uvToZ(u, v);
        int newFace = xyzToFace(x, y, z);
        UvTransform uvTransform = UV_TRANSFORMS[newFace];
        double newU = uvTransform.xyzToU(x, y, z);
        double newV = uvTransform.xyzToV(x, y, z);
        int newI = uShiftIntoI(newU);
        int newJ = vShiftIntoJ(newV);
        return fromFij(newFace, newI, newJ);
    }

    private static long fromFijSame(int face, int i, int j, boolean isSameFace) {
        if (isSameFace) {
            return fromFij(face, i, j);
        }
        return fromFijWrap(face, i, j);
    }

    private static int xyzToFace(double x, double y, double z) {
        double absX = Math.abs(x);
        double absY = Math.abs(y);
        double absZ = Math.abs(z);
        return absX > absY ? absX > absZ ? x < 0.0d ? 3 : 0 : z < 0.0d ? 5 : 2 : absY > absZ ? y < 0.0d ? 4 : 1 : z < 0.0d ? 5 : 2;
    }

    private static int uToI(double u) {
        double s;
        if (u >= 0.0d) {
            s = Math.sqrt((3.0d * u) + 1.0d) * 0.5d;
        } else {
            s = 1.0d - (Math.sqrt(1.0d - (3.0d * u)) * 0.5d);
        }
        return Math.max(0, Math.min((int) View.LAST_APP_AUTOFILL_ID, (int) Math.round((1.073741824E9d * s) - 0.5d)));
    }

    private static int vToJ(double v) {
        return uToI(v);
    }

    private static int lookupBits(int i, int j, int k, int bits) {
        return LOOKUP_POS[bits + (((i >> (k * 4)) & 15) << 6) + (((j >> (k * 4)) & 15) << 2)];
    }

    private static long updateBits(long sb, int k, int bits) {
        return ((bits >> 2) << (((k & 3) * 2) * 4)) | sb;
    }

    private static int maskBits(int bits) {
        return bits & 3;
    }

    private static int getFace(long s2CellId) {
        return (int) (s2CellId >>> 61);
    }

    private static boolean isLeaf(long s2CellId) {
        return (((int) s2CellId) & 1) != 0;
    }

    private static double iToU(int i) {
        int satI = Math.max(-1, Math.min(1073741824, i));
        double d = UV_LIMIT;
        return Math.max(-d, Math.min(d, (((satI << 1) + 1) - 1073741824) * ONE_OVER_MAX_SIZE));
    }

    private static double jToV(int j) {
        return iToU(j);
    }

    private static long toIjo(long s2CellId) {
        int face = getFace(s2CellId);
        int bits = face & 1;
        int i = 0;
        int j = 0;
        int k = 7;
        while (k >= 0) {
            int nbits = k == 7 ? 2 : 4;
            int bits2 = LOOKUP_IJ[bits + ((((int) (s2CellId >>> (((k * 2) * 4) + 1))) & ((1 << (nbits * 2)) - 1)) << 2)];
            i += (bits2 >> 6) << (k * 4);
            j += ((bits2 >> 2) & 15) << (k * 4);
            bits = bits2 & 3;
            k--;
        }
        int orientation = (getLowestOnBit(s2CellId) & 1229782938247303440L) != 0 ? bits ^ 1 : bits;
        return (i << 33) | (j << 2) | orientation;
    }

    private static int ijoToI(long ijo) {
        return (int) (ijo >>> 33);
    }

    private static int ijoToJ(long ijo) {
        return (int) ((ijo >>> 2) & J_MASK);
    }

    private static int uShiftIntoI(double u) {
        double s = (1.0d + u) * 0.5d;
        return Math.max(0, Math.min((int) View.LAST_APP_AUTOFILL_ID, (int) Math.round((1.073741824E9d * s) - 0.5d)));
    }

    private static int vShiftIntoJ(double v) {
        return uShiftIntoI(v);
    }

    private static int levelToSizeIj(int level) {
        return 1 << (30 - level);
    }

    private static void initLookupCells() {
        initLookupCell(0, 0, 0, 0, 0, 0);
        initLookupCell(0, 0, 0, 1, 0, 1);
        initLookupCell(0, 0, 0, 2, 0, 2);
        initLookupCell(0, 0, 0, 3, 0, 3);
    }

    private static void initLookupCell(int level, int i, int j, int origOrientation, int pos, int orientation) {
        if (level == 4) {
            int ij = (i << 4) + j;
            LOOKUP_POS[(ij << 2) + origOrientation] = (pos << 2) + orientation;
            LOOKUP_IJ[(pos << 2) + origOrientation] = (ij << 2) + orientation;
            return;
        }
        int level2 = level + 1;
        int i2 = i << 1;
        int j2 = j << 1;
        int pos2 = pos << 2;
        for (int subPos = 0; subPos < 4; subPos++) {
            int ij2 = POS_TO_IJ[orientation][subPos];
            int orientationMask = POS_TO_ORIENTATION[subPos];
            initLookupCell(level2, i2 + (ij2 >>> 1), j2 + (ij2 & 1), origOrientation, pos2 + subPos, orientation ^ orientationMask);
        }
    }

    private static double calculateUvLimit() {
        double machEps = 1.0d;
        do {
            machEps /= 2.0d;
        } while ((machEps / 2.0d) + 1.0d != 1.0d);
        return 1.0d + machEps;
    }

    private static UvTransform[] createUvTransforms() {
        UvTransform[] uvTransforms = {new UvTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.1
            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToU(double x, double y, double z) {
                return y / x;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToV(double x, double y, double z) {
                return z / x;
            }
        }, new UvTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.2
            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToU(double x, double y, double z) {
                return (-x) / y;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToV(double x, double y, double z) {
                return z / y;
            }
        }, new UvTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.3
            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToU(double x, double y, double z) {
                return (-x) / z;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToV(double x, double y, double z) {
                return (-y) / z;
            }
        }, new UvTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.4
            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToU(double x, double y, double z) {
                return z / x;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToV(double x, double y, double z) {
                return y / x;
            }
        }, new UvTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.5
            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToU(double x, double y, double z) {
                return z / y;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToV(double x, double y, double z) {
                return (-x) / y;
            }
        }, new UvTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.6
            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToU(double x, double y, double z) {
                return (-y) / z;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.UvTransform
            public double xyzToV(double x, double y, double z) {
                return (-x) / z;
            }
        }};
        return uvTransforms;
    }

    private static XyzTransform[] createXyzTransforms() {
        XyzTransform[] xyzTransforms = {new XyzTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.7
            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToX(double u, double v) {
                return 1.0d;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToY(double u, double v) {
                return u;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToZ(double u, double v) {
                return v;
            }
        }, new XyzTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.8
            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToX(double u, double v) {
                return -u;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToY(double u, double v) {
                return 1.0d;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToZ(double u, double v) {
                return v;
            }
        }, new XyzTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.9
            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToX(double u, double v) {
                return -u;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToY(double u, double v) {
                return -v;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToZ(double u, double v) {
                return 1.0d;
            }
        }, new XyzTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.10
            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToX(double u, double v) {
                return -1.0d;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToY(double u, double v) {
                return -v;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToZ(double u, double v) {
                return -u;
            }
        }, new XyzTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.11
            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToX(double u, double v) {
                return v;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToY(double u, double v) {
                return -1.0d;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToZ(double u, double v) {
                return -u;
            }
        }, new XyzTransform() { // from class: com.android.internal.location.altitude.S2CellIdUtils.12
            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToX(double u, double v) {
                return v;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToY(double u, double v) {
                return u;
            }

            @Override // com.android.internal.location.altitude.S2CellIdUtils.XyzTransform
            public double uvToZ(double u, double v) {
                return -1.0d;
            }
        }};
        return xyzTransforms;
    }
}
