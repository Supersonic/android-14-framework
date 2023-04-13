package android.graphics;

import android.graphics.ColorSpace;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.SparseIntArray;
import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public abstract class ColorSpace {
    private static final Rgb.TransferParameters BT2020_HLG_TRANSFER_PARAMETERS;
    private static final Rgb.TransferParameters BT2020_PQ_TRANSFER_PARAMETERS;
    private static final float[] BT2020_PRIMARIES;
    public static final float[] ILLUMINANT_C;
    public static final float[] ILLUMINANT_D50;
    public static final float[] ILLUMINANT_D60;
    public static final float[] ILLUMINANT_D65;
    public static final int MAX_ID = 63;
    public static final int MIN_ID = -1;
    private static final float[] NTSC_1953_PRIMARIES;
    private static final float[] SRGB_PRIMARIES;
    private static final Rgb.TransferParameters SRGB_TRANSFER_PARAMETERS;
    private static final SparseIntArray sDataToColorSpaces;
    private static final ColorSpace[] sNamedColorSpaces;
    private final int mId;
    private final Model mModel;
    private final String mName;
    public static final float[] ILLUMINANT_A = {0.44757f, 0.40745f};
    public static final float[] ILLUMINANT_B = {0.34842f, 0.35161f};
    public static final float[] ILLUMINANT_D55 = {0.33242f, 0.34743f};
    public static final float[] ILLUMINANT_D75 = {0.29902f, 0.31485f};
    public static final float[] ILLUMINANT_E = {0.33333f, 0.33333f};
    private static final float[] GRAY_PRIMARIES = {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
    private static final float[] ILLUMINANT_D50_XYZ = {0.964212f, 1.0f, 0.825188f};

    /* loaded from: classes.dex */
    public enum Named {
        SRGB,
        LINEAR_SRGB,
        EXTENDED_SRGB,
        LINEAR_EXTENDED_SRGB,
        BT709,
        BT2020,
        DCI_P3,
        DISPLAY_P3,
        NTSC_1953,
        SMPTE_C,
        ADOBE_RGB,
        PRO_PHOTO_RGB,
        ACES,
        ACESCG,
        CIE_XYZ,
        CIE_LAB,
        BT2020_HLG,
        BT2020_PQ
    }

    /* loaded from: classes.dex */
    public enum RenderIntent {
        PERCEPTUAL,
        RELATIVE,
        SATURATION,
        ABSOLUTE
    }

    public abstract float[] fromXyz(float[] fArr);

    public abstract float getMaxValue(int i);

    public abstract float getMinValue(int i);

    public abstract boolean isWideGamut();

    public abstract float[] toXyz(float[] fArr);

    static {
        float[] fArr = {0.31006f, 0.31616f};
        ILLUMINANT_C = fArr;
        float[] fArr2 = {0.34567f, 0.3585f};
        ILLUMINANT_D50 = fArr2;
        float[] fArr3 = {0.32168f, 0.33767f};
        ILLUMINANT_D60 = fArr3;
        float[] fArr4 = {0.31271f, 0.32902f};
        ILLUMINANT_D65 = fArr4;
        float[] fArr5 = {0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f};
        SRGB_PRIMARIES = fArr5;
        float[] fArr6 = {0.67f, 0.33f, 0.21f, 0.71f, 0.14f, 0.08f};
        NTSC_1953_PRIMARIES = fArr6;
        float[] fArr7 = {0.708f, 0.292f, 0.17f, 0.797f, 0.131f, 0.046f};
        BT2020_PRIMARIES = fArr7;
        Rgb.TransferParameters transferParameters = new Rgb.TransferParameters(0.9478672985781991d, 0.05213270142180095d, 0.07739938080495357d, 0.04045d, 2.4d);
        SRGB_TRANSFER_PARAMETERS = transferParameters;
        Rgb.TransferParameters transferParameters2 = new Rgb.TransferParameters(2.0d, 2.0d, 5.591816425323486d, 0.2846689224243164d, 0.5599107146263123d, -0.9166666865348816d, -3.0d, true);
        BT2020_HLG_TRANSFER_PARAMETERS = transferParameters2;
        Rgb.TransferParameters transferParameters3 = new Rgb.TransferParameters(-0.8359375d, 1.0d, 0.012683313339948654d, 18.8515625d, -18.6875d, 6.277394771575928d, -2.0d, true);
        BT2020_PQ_TRANSFER_PARAMETERS = transferParameters3;
        ColorSpace[] colorSpaceArr = new ColorSpace[Named.values().length];
        sNamedColorSpaces = colorSpaceArr;
        SparseIntArray sparseIntArray = new SparseIntArray();
        sDataToColorSpaces = sparseIntArray;
        colorSpaceArr[Named.SRGB.ordinal()] = new Rgb("sRGB IEC61966-2.1", fArr5, fArr4, (float[]) null, transferParameters, Named.SRGB.ordinal());
        sparseIntArray.put(142671872, Named.SRGB.ordinal());
        colorSpaceArr[Named.LINEAR_SRGB.ordinal()] = new Rgb("sRGB IEC61966-2.1 (Linear)", fArr5, fArr4, 1.0d, 0.0f, 1.0f, Named.LINEAR_SRGB.ordinal());
        sparseIntArray.put(138477568, Named.LINEAR_SRGB.ordinal());
        colorSpaceArr[Named.EXTENDED_SRGB.ordinal()] = new Rgb("scRGB-nl IEC 61966-2-2:2003", fArr5, fArr4, null, new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$$ExternalSyntheticLambda0
            @Override // java.util.function.DoubleUnaryOperator
            public final double applyAsDouble(double d) {
                double absRcpResponse;
                absRcpResponse = ColorSpace.absRcpResponse(d, 0.9478672985781991d, 0.05213270142180095d, 0.07739938080495357d, 0.04045d, 2.4d);
                return absRcpResponse;
            }
        }, new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$$ExternalSyntheticLambda1
            @Override // java.util.function.DoubleUnaryOperator
            public final double applyAsDouble(double d) {
                double absResponse;
                absResponse = ColorSpace.absResponse(d, 0.9478672985781991d, 0.05213270142180095d, 0.07739938080495357d, 0.04045d, 2.4d);
                return absResponse;
            }
        }, -0.799f, 2.399f, transferParameters, Named.EXTENDED_SRGB.ordinal());
        sparseIntArray.put(411107328, Named.EXTENDED_SRGB.ordinal());
        colorSpaceArr[Named.LINEAR_EXTENDED_SRGB.ordinal()] = new Rgb("scRGB IEC 61966-2-2:2003", fArr5, fArr4, 1.0d, -0.5f, 7.499f, Named.LINEAR_EXTENDED_SRGB.ordinal());
        sparseIntArray.put(406913024, Named.LINEAR_EXTENDED_SRGB.ordinal());
        colorSpaceArr[Named.BT709.ordinal()] = new Rgb("Rec. ITU-R BT.709-5", new float[]{0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f}, fArr4, (float[]) null, new Rgb.TransferParameters(0.9099181073703367d, 0.09008189262966333d, 0.2222222222222222d, 0.081d, 2.2222222222222223d), Named.BT709.ordinal());
        sparseIntArray.put(281083904, Named.BT709.ordinal());
        colorSpaceArr[Named.BT2020.ordinal()] = new Rgb("Rec. ITU-R BT.2020-1", fArr7, fArr4, (float[]) null, new Rgb.TransferParameters(0.9096697898662786d, 0.09033021013372146d, 0.2222222222222222d, 0.08145d, 2.2222222222222223d), Named.BT2020.ordinal());
        sparseIntArray.put(147193856, Named.BT2020.ordinal());
        colorSpaceArr[Named.DCI_P3.ordinal()] = new Rgb("SMPTE RP 431-2-2007 DCI (P3)", new float[]{0.68f, 0.32f, 0.265f, 0.69f, 0.15f, 0.06f}, new float[]{0.314f, 0.351f}, 2.6d, 0.0f, 1.0f, Named.DCI_P3.ordinal());
        sparseIntArray.put(155844608, Named.DCI_P3.ordinal());
        colorSpaceArr[Named.DISPLAY_P3.ordinal()] = new Rgb("Display P3", new float[]{0.68f, 0.32f, 0.265f, 0.69f, 0.15f, 0.06f}, fArr4, (float[]) null, transferParameters, Named.DISPLAY_P3.ordinal());
        sparseIntArray.put(143261696, Named.DISPLAY_P3.ordinal());
        colorSpaceArr[Named.NTSC_1953.ordinal()] = new Rgb("NTSC (1953)", fArr6, fArr, (float[]) null, new Rgb.TransferParameters(0.9099181073703367d, 0.09008189262966333d, 0.2222222222222222d, 0.081d, 2.2222222222222223d), Named.NTSC_1953.ordinal());
        colorSpaceArr[Named.SMPTE_C.ordinal()] = new Rgb("SMPTE-C RGB", new float[]{0.63f, 0.34f, 0.31f, 0.595f, 0.155f, 0.07f}, fArr4, (float[]) null, new Rgb.TransferParameters(0.9099181073703367d, 0.09008189262966333d, 0.2222222222222222d, 0.081d, 2.2222222222222223d), Named.SMPTE_C.ordinal());
        colorSpaceArr[Named.ADOBE_RGB.ordinal()] = new Rgb("Adobe RGB (1998)", new float[]{0.64f, 0.33f, 0.21f, 0.71f, 0.15f, 0.06f}, fArr4, 2.2d, 0.0f, 1.0f, Named.ADOBE_RGB.ordinal());
        sparseIntArray.put(151715840, Named.ADOBE_RGB.ordinal());
        colorSpaceArr[Named.PRO_PHOTO_RGB.ordinal()] = new Rgb("ROMM RGB ISO 22028-2:2013", new float[]{0.7347f, 0.2653f, 0.1596f, 0.8404f, 0.0366f, 1.0E-4f}, fArr2, (float[]) null, new Rgb.TransferParameters(1.0d, 0.0d, 0.0625d, 0.031248d, 1.8d), Named.PRO_PHOTO_RGB.ordinal());
        colorSpaceArr[Named.ACES.ordinal()] = new Rgb("SMPTE ST 2065-1:2012 ACES", new float[]{0.7347f, 0.2653f, 0.0f, 1.0f, 1.0E-4f, -0.077f}, fArr3, 1.0d, -65504.0f, 65504.0f, Named.ACES.ordinal());
        colorSpaceArr[Named.ACESCG.ordinal()] = new Rgb("Academy S-2014-004 ACEScg", new float[]{0.713f, 0.293f, 0.165f, 0.83f, 0.128f, 0.044f}, fArr3, 1.0d, -65504.0f, 65504.0f, Named.ACESCG.ordinal());
        colorSpaceArr[Named.CIE_XYZ.ordinal()] = new Xyz("Generic XYZ", Named.CIE_XYZ.ordinal());
        colorSpaceArr[Named.CIE_LAB.ordinal()] = new Lab("Generic L*a*b*", Named.CIE_LAB.ordinal());
        colorSpaceArr[Named.BT2020_HLG.ordinal()] = new Rgb("Hybrid Log Gamma encoding", fArr7, fArr4, null, new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$$ExternalSyntheticLambda2
            @Override // java.util.function.DoubleUnaryOperator
            public final double applyAsDouble(double d) {
                double transferHLGOETF;
                transferHLGOETF = ColorSpace.transferHLGOETF(d);
                return transferHLGOETF;
            }
        }, new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$$ExternalSyntheticLambda3
            @Override // java.util.function.DoubleUnaryOperator
            public final double applyAsDouble(double d) {
                double transferHLGEOTF;
                transferHLGEOTF = ColorSpace.transferHLGEOTF(d);
                return transferHLGEOTF;
            }
        }, 0.0f, 1.0f, transferParameters2, Named.BT2020_HLG.ordinal());
        sparseIntArray.put(168165376, Named.BT2020_HLG.ordinal());
        colorSpaceArr[Named.BT2020_PQ.ordinal()] = new Rgb("Perceptual Quantizer encoding", fArr7, fArr4, null, new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$$ExternalSyntheticLambda4
            @Override // java.util.function.DoubleUnaryOperator
            public final double applyAsDouble(double d) {
                double transferST2048OETF;
                transferST2048OETF = ColorSpace.transferST2048OETF(d);
                return transferST2048OETF;
            }
        }, new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$$ExternalSyntheticLambda5
            @Override // java.util.function.DoubleUnaryOperator
            public final double applyAsDouble(double d) {
                double transferST2048EOTF;
                transferST2048EOTF = ColorSpace.transferST2048EOTF(d);
                return transferST2048EOTF;
            }
        }, 0.0f, 1.0f, transferParameters3, Named.BT2020_PQ.ordinal());
        sparseIntArray.put(163971072, Named.BT2020_PQ.ordinal());
    }

    /* loaded from: classes.dex */
    public enum Adaptation {
        BRADFORD(new float[]{0.8951f, -0.7502f, 0.0389f, 0.2664f, 1.7135f, -0.0685f, -0.1614f, 0.0367f, 1.0296f}),
        VON_KRIES(new float[]{0.40024f, -0.2263f, 0.0f, 0.7076f, 1.16532f, 0.0f, -0.08081f, 0.0457f, 0.91822f}),
        CIECAT02(new float[]{0.7328f, -0.7036f, 0.003f, 0.4296f, 1.6975f, 0.0136f, -0.1624f, 0.0061f, 0.9834f});
        
        final float[] mTransform;

        Adaptation(float[] transform) {
            this.mTransform = transform;
        }
    }

    /* loaded from: classes.dex */
    public enum Model {
        RGB(3),
        XYZ(3),
        LAB(3),
        CMYK(4);
        
        private final int mComponentCount;

        Model(int componentCount) {
            this.mComponentCount = componentCount;
        }

        public int getComponentCount() {
            return this.mComponentCount;
        }
    }

    ColorSpace(String name, Model model, int id) {
        if (name == null || name.length() < 1) {
            throw new IllegalArgumentException("The name of a color space cannot be null and must contain at least 1 character");
        }
        if (model == null) {
            throw new IllegalArgumentException("A color space must have a model");
        }
        if (id < -1 || id > 63) {
            throw new IllegalArgumentException("The id must be between -1 and 63");
        }
        this.mName = name;
        this.mModel = model;
        this.mId = id;
    }

    public String getName() {
        return this.mName;
    }

    public int getId() {
        return this.mId;
    }

    public Model getModel() {
        return this.mModel;
    }

    public int getComponentCount() {
        return this.mModel.getComponentCount();
    }

    public boolean isSrgb() {
        return false;
    }

    public float[] toXyz(float r, float g, float b) {
        return toXyz(new float[]{r, g, b});
    }

    public float[] fromXyz(float x, float y, float z) {
        float[] xyz = new float[this.mModel.getComponentCount()];
        xyz[0] = x;
        xyz[1] = y;
        xyz[2] = z;
        return fromXyz(xyz);
    }

    public String toString() {
        return this.mName + " (id=" + this.mId + ", model=" + this.mModel + NavigationBarInflaterView.KEY_CODE_END;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColorSpace that = (ColorSpace) o;
        if (this.mId == that.mId && this.mName.equals(that.mName) && this.mModel == that.mModel) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.mName.hashCode();
        return (((result * 31) + this.mModel.hashCode()) * 31) + this.mId;
    }

    public static Connector connect(ColorSpace source, ColorSpace destination) {
        return connect(source, destination, RenderIntent.PERCEPTUAL);
    }

    public static Connector connect(ColorSpace source, ColorSpace destination, RenderIntent intent) {
        if (source.equals(destination)) {
            return Connector.identity(source);
        }
        if (source.getModel() == Model.RGB && destination.getModel() == Model.RGB) {
            return new Connector.Rgb((Rgb) source, (Rgb) destination, intent);
        }
        return new Connector(source, destination, intent);
    }

    public static Connector connect(ColorSpace source) {
        return connect(source, RenderIntent.PERCEPTUAL);
    }

    public static Connector connect(ColorSpace source, RenderIntent intent) {
        if (source.isSrgb()) {
            return Connector.identity(source);
        }
        if (source.getModel() == Model.RGB) {
            return new Connector.Rgb((Rgb) source, (Rgb) get(Named.SRGB), intent);
        }
        return new Connector(source, get(Named.SRGB), intent);
    }

    public static ColorSpace adapt(ColorSpace colorSpace, float[] whitePoint) {
        return adapt(colorSpace, whitePoint, Adaptation.BRADFORD);
    }

    public static ColorSpace adapt(ColorSpace colorSpace, float[] whitePoint, Adaptation adaptation) {
        if (colorSpace.getModel() == Model.RGB) {
            Rgb rgb = (Rgb) colorSpace;
            if (compare(rgb.mWhitePoint, whitePoint)) {
                return colorSpace;
            }
            float[] xyz = whitePoint.length == 3 ? Arrays.copyOf(whitePoint, 3) : xyYToXyz(whitePoint);
            float[] adaptationTransform = chromaticAdaptation(adaptation.mTransform, xyYToXyz(rgb.getWhitePoint()), xyz);
            float[] transform = mul3x3(adaptationTransform, rgb.mTransform);
            return new Rgb(rgb, transform, whitePoint);
        }
        return colorSpace;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] adaptToIlluminantD50(float[] origWhitePoint, float[] origTransform) {
        float[] desired = ILLUMINANT_D50;
        if (compare(origWhitePoint, desired)) {
            return origTransform;
        }
        float[] xyz = xyYToXyz(desired);
        float[] adaptationTransform = chromaticAdaptation(Adaptation.BRADFORD.mTransform, xyYToXyz(origWhitePoint), xyz);
        return mul3x3(adaptationTransform, origTransform);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ColorSpace get(int index) {
        if (index >= 0) {
            ColorSpace[] colorSpaceArr = sNamedColorSpaces;
            if (index < colorSpaceArr.length) {
                return colorSpaceArr[index];
            }
        }
        throw new IllegalArgumentException("Invalid ID, must be in the range [0.." + sNamedColorSpaces.length + NavigationBarInflaterView.KEY_CODE_END);
    }

    public static ColorSpace getFromDataSpace(int dataSpace) {
        int index = sDataToColorSpaces.get(dataSpace, -1);
        if (index != -1) {
            return get(index);
        }
        return null;
    }

    public int getDataSpace() {
        SparseIntArray sparseIntArray = sDataToColorSpaces;
        int index = sparseIntArray.indexOfValue(getId());
        if (index != -1) {
            return sparseIntArray.keyAt(index);
        }
        return 0;
    }

    public static ColorSpace get(Named name) {
        return sNamedColorSpaces[name.ordinal()];
    }

    public static ColorSpace match(float[] toXYZD50, Rgb.TransferParameters function) {
        ColorSpace[] colorSpaceArr;
        for (ColorSpace colorSpace : sNamedColorSpaces) {
            if (colorSpace.getModel() == Model.RGB) {
                Rgb rgb = (Rgb) adapt(colorSpace, ILLUMINANT_D50_XYZ);
                if (compare(toXYZD50, rgb.mTransform) && compare(function, rgb.mTransferParameters)) {
                    return colorSpace;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double transferHLGOETF(double x) {
        if (x > 1.0d) {
            return (Math.log(x - 0.28466892d) * 0.17883277d) + 0.55991073d;
        }
        return Math.sqrt(x) * 0.5d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double transferHLGEOTF(double x) {
        if (x > 0.5d) {
            return Math.exp(((x - 0.55991073d) / 0.17883277d) + 0.28466892d);
        }
        return (x * x) / (0.5d * 0.5d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double transferST2048OETF(double x) {
        double tmp = Math.pow(x, 0.1593017578125d);
        return Math.pow(((18.8515625d * tmp) + 0.8359375d) / ((18.6875d * tmp) + 1.0d), 78.84375d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double transferST2048EOTF(double x) {
        double tmp = Math.pow(Math.min(Math.max(x, 0.0d), 1.0d), 1.0d / 78.84375d);
        return Math.pow(Math.max(tmp - 0.8359375d, 0.0d) / (18.8515625d - (18.6875d * tmp)), 1.0d / 0.1593017578125d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double rcpResponse(double x, double a, double b, double c, double d, double g) {
        return x >= d * c ? (Math.pow(x, 1.0d / g) - b) / a : x / c;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double response(double x, double a, double b, double c, double d, double g) {
        return x >= d ? Math.pow((a * x) + b, g) : c * x;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double rcpResponse(double x, double a, double b, double c, double d, double e, double f, double g) {
        return x >= d * c ? (Math.pow(x - e, 1.0d / g) - b) / a : (x - f) / c;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double response(double x, double a, double b, double c, double d, double e, double f, double g) {
        return x >= d ? Math.pow((a * x) + b, g) + e : (c * x) + f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double absRcpResponse(double x, double a, double b, double c, double d, double g) {
        return Math.copySign(rcpResponse(x < 0.0d ? -x : x, a, b, c, d, g), x);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double absResponse(double x, double a, double b, double c, double d, double g) {
        return Math.copySign(response(x < 0.0d ? -x : x, a, b, c, d, g), x);
    }

    private static boolean compare(Rgb.TransferParameters a, Rgb.TransferParameters b) {
        if (a == null && b == null) {
            return true;
        }
        return a != null && b != null && Math.abs(a.f67a - b.f67a) < 0.001d && Math.abs(a.f68b - b.f68b) < 0.001d && Math.abs(a.f69c - b.f69c) < 0.001d && Math.abs(a.f70d - b.f70d) < 0.002d && Math.abs(a.f71e - b.f71e) < 0.001d && Math.abs(a.f72f - b.f72f) < 0.001d && Math.abs(a.f73g - b.f73g) < 0.001d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean compare(float[] a, float[] b) {
        if (a == b) {
            return true;
        }
        for (int i = 0; i < a.length; i++) {
            if (Float.compare(a[i], b[i]) != 0 && Math.abs(a[i] - b[i]) > 0.001f) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] inverse3x3(float[] m) {
        float a = m[0];
        float b = m[3];
        float c = m[6];
        float d = m[1];
        float e = m[4];
        float f = m[7];
        float g = m[2];
        float h = m[5];
        float i = m[8];
        float A = (e * i) - (f * h);
        float B = (f * g) - (d * i);
        float C = (d * h) - (e * g);
        float det = (a * A) + (b * B) + (c * C);
        float[] inverted = new float[m.length];
        inverted[0] = A / det;
        inverted[1] = B / det;
        inverted[2] = C / det;
        inverted[3] = ((c * h) - (b * i)) / det;
        inverted[4] = ((a * i) - (c * g)) / det;
        inverted[5] = ((b * g) - (a * h)) / det;
        inverted[6] = ((b * f) - (c * e)) / det;
        inverted[7] = ((c * d) - (a * f)) / det;
        inverted[8] = ((a * e) - (b * d)) / det;
        return inverted;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] mul3x3(float[] lhs, float[] rhs) {
        float[] r = {(lhs[0] * rhs[0]) + (lhs[3] * rhs[1]) + (lhs[6] * rhs[2]), (lhs[1] * rhs[0]) + (lhs[4] * rhs[1]) + (lhs[7] * rhs[2]), (lhs[2] * rhs[0]) + (lhs[5] * rhs[1]) + (lhs[8] * rhs[2]), (lhs[0] * rhs[3]) + (lhs[3] * rhs[4]) + (lhs[6] * rhs[5]), (lhs[1] * rhs[3]) + (lhs[4] * rhs[4]) + (lhs[7] * rhs[5]), (lhs[2] * rhs[3]) + (lhs[5] * rhs[4]) + (lhs[8] * rhs[5]), (lhs[0] * rhs[6]) + (lhs[3] * rhs[7]) + (lhs[6] * rhs[8]), (lhs[1] * rhs[6]) + (lhs[4] * rhs[7]) + (lhs[7] * rhs[8]), (lhs[2] * rhs[6]) + (lhs[5] * rhs[7]) + (lhs[8] * rhs[8])};
        return r;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] mul3x3Float3(float[] lhs, float[] rhs) {
        float r0 = rhs[0];
        float r1 = rhs[1];
        float r2 = rhs[2];
        rhs[0] = (lhs[0] * r0) + (lhs[3] * r1) + (lhs[6] * r2);
        rhs[1] = (lhs[1] * r0) + (lhs[4] * r1) + (lhs[7] * r2);
        rhs[2] = (lhs[2] * r0) + (lhs[5] * r1) + (lhs[8] * r2);
        return rhs;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] mul3x3Diag(float[] lhs, float[] rhs) {
        return new float[]{lhs[0] * rhs[0], lhs[1] * rhs[1], lhs[2] * rhs[2], lhs[0] * rhs[3], lhs[1] * rhs[4], lhs[2] * rhs[5], lhs[0] * rhs[6], lhs[1] * rhs[7], lhs[2] * rhs[8]};
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] xyYToXyz(float[] xyY) {
        return new float[]{xyY[0] / xyY[1], 1.0f, ((1.0f - xyY[0]) - xyY[1]) / xyY[1]};
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] chromaticAdaptation(float[] matrix, float[] srcWhitePoint, float[] dstWhitePoint) {
        float[] srcLMS = mul3x3Float3(matrix, srcWhitePoint);
        float[] dstLMS = mul3x3Float3(matrix, dstWhitePoint);
        float[] LMS = {dstLMS[0] / srcLMS[0], dstLMS[1] / srcLMS[1], dstLMS[2] / srcLMS[2]};
        return mul3x3(inverse3x3(matrix), mul3x3Diag(LMS, matrix));
    }

    public static float[] cctToXyz(int cct) {
        float x;
        float y;
        if (cct < 1) {
            throw new IllegalArgumentException("Temperature must be greater than 0");
        }
        float icct = 1000.0f / cct;
        float icct2 = icct * icct;
        if (cct <= 4000.0f) {
            x = (((0.8776956f * icct) + 0.17991f) - (0.2343589f * icct2)) - ((0.2661239f * icct2) * icct);
        } else {
            x = (((0.2226347f * icct) + 0.24039f) + (2.1070378f * icct2)) - ((3.025847f * icct2) * icct);
        }
        float x2 = x * x;
        if (cct <= 2222.0f) {
            y = (((2.1855583f * x) - 0.20219684f) - (1.3481102f * x2)) - ((1.1063814f * x2) * x);
        } else if (cct <= 4000.0f) {
            y = (((2.09137f * x) - 0.16748866f) - (1.3741859f * x2)) - ((0.9549476f * x2) * x);
        } else {
            y = (((3.7511299f * x) - 0.37001482f) - (5.873387f * x2)) + (3.081758f * x2 * x);
        }
        return xyYToXyz(new float[]{x, y});
    }

    public static float[] chromaticAdaptation(Adaptation adaptation, float[] srcWhitePoint, float[] dstWhitePoint) {
        if ((srcWhitePoint.length != 2 && srcWhitePoint.length != 3) || (dstWhitePoint.length != 2 && dstWhitePoint.length != 3)) {
            throw new IllegalArgumentException("A white point array must have 2 or 3 floats");
        }
        float[] srcXyz = srcWhitePoint.length == 3 ? Arrays.copyOf(srcWhitePoint, 3) : xyYToXyz(srcWhitePoint);
        float[] dstXyz = dstWhitePoint.length == 3 ? Arrays.copyOf(dstWhitePoint, 3) : xyYToXyz(dstWhitePoint);
        if (compare(srcXyz, dstXyz)) {
            return new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        }
        return chromaticAdaptation(adaptation.mTransform, srcXyz, dstXyz);
    }

    /* loaded from: classes.dex */
    private static final class Xyz extends ColorSpace {
        private Xyz(String name, int id) {
            super(name, Model.XYZ, id);
        }

        @Override // android.graphics.ColorSpace
        public boolean isWideGamut() {
            return true;
        }

        @Override // android.graphics.ColorSpace
        public float getMinValue(int component) {
            return -2.0f;
        }

        @Override // android.graphics.ColorSpace
        public float getMaxValue(int component) {
            return 2.0f;
        }

        @Override // android.graphics.ColorSpace
        public float[] toXyz(float[] v) {
            v[0] = clamp(v[0]);
            v[1] = clamp(v[1]);
            v[2] = clamp(v[2]);
            return v;
        }

        @Override // android.graphics.ColorSpace
        public float[] fromXyz(float[] v) {
            v[0] = clamp(v[0]);
            v[1] = clamp(v[1]);
            v[2] = clamp(v[2]);
            return v;
        }

        private static float clamp(float x) {
            if (x < -2.0f) {
                return -2.0f;
            }
            if (x > 2.0f) {
                return 2.0f;
            }
            return x;
        }
    }

    /* loaded from: classes.dex */
    private static final class Lab extends ColorSpace {

        /* renamed from: A */
        private static final float f63A = 0.008856452f;

        /* renamed from: B */
        private static final float f64B = 7.787037f;

        /* renamed from: C */
        private static final float f65C = 0.13793103f;

        /* renamed from: D */
        private static final float f66D = 0.20689656f;

        private Lab(String name, int id) {
            super(name, Model.LAB, id);
        }

        @Override // android.graphics.ColorSpace
        public boolean isWideGamut() {
            return true;
        }

        @Override // android.graphics.ColorSpace
        public float getMinValue(int component) {
            return component == 0 ? 0.0f : -128.0f;
        }

        @Override // android.graphics.ColorSpace
        public float getMaxValue(int component) {
            return component == 0 ? 100.0f : 128.0f;
        }

        @Override // android.graphics.ColorSpace
        public float[] toXyz(float[] v) {
            v[0] = clamp(v[0], 0.0f, 100.0f);
            v[1] = clamp(v[1], -128.0f, 128.0f);
            v[2] = clamp(v[2], -128.0f, 128.0f);
            float fy = (v[0] + 16.0f) / 116.0f;
            float fx = (v[1] * 0.002f) + fy;
            float fz = fy - (v[2] * 0.005f);
            float X = fx > f66D ? fx * fx * fx : (fx - f65C) * 0.12841855f;
            float Y = fy > f66D ? fy * fy * fy : (fy - f65C) * 0.12841855f;
            float Z = fz > f66D ? fz * fz * fz : (fz - f65C) * 0.12841855f;
            v[0] = ColorSpace.ILLUMINANT_D50_XYZ[0] * X;
            v[1] = ColorSpace.ILLUMINANT_D50_XYZ[1] * Y;
            v[2] = ColorSpace.ILLUMINANT_D50_XYZ[2] * Z;
            return v;
        }

        @Override // android.graphics.ColorSpace
        public float[] fromXyz(float[] v) {
            float X = v[0] / ColorSpace.ILLUMINANT_D50_XYZ[0];
            float Y = v[1] / ColorSpace.ILLUMINANT_D50_XYZ[1];
            float Z = v[2] / ColorSpace.ILLUMINANT_D50_XYZ[2];
            float fx = X > f63A ? (float) Math.pow(X, 0.3333333333333333d) : (X * f64B) + f65C;
            float fy = Y > f63A ? (float) Math.pow(Y, 0.3333333333333333d) : (Y * f64B) + f65C;
            float fz = Z > f63A ? (float) Math.pow(Z, 0.3333333333333333d) : (f64B * Z) + f65C;
            float L = (116.0f * fy) - 16.0f;
            float a = (fx - fy) * 500.0f;
            float b = (fy - fz) * 200.0f;
            v[0] = clamp(L, 0.0f, 100.0f);
            v[1] = clamp(a, -128.0f, 128.0f);
            v[2] = clamp(b, -128.0f, 128.0f);
            return v;
        }

        private static float clamp(float x, float min, float max) {
            return x < min ? min : x > max ? max : x;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getNativeInstance() {
        throw new IllegalArgumentException("colorSpace must be an RGB color space");
    }

    /* loaded from: classes.dex */
    public static class Rgb extends ColorSpace {
        private final DoubleUnaryOperator mClampedEotf;
        private final DoubleUnaryOperator mClampedOetf;
        private final DoubleUnaryOperator mEotf;
        private final float[] mInverseTransform;
        private final boolean mIsSrgb;
        private final boolean mIsWideGamut;
        private final float mMax;
        private final float mMin;
        private final long mNativePtr;
        private final DoubleUnaryOperator mOetf;
        private final float[] mPrimaries;
        private final TransferParameters mTransferParameters;
        private final float[] mTransform;
        private final float[] mWhitePoint;

        private static native long nativeCreate(float f, float f2, float f3, float f4, float f5, float f6, float f7, float[] fArr);

        /* JADX INFO: Access modifiers changed from: private */
        public static native long nativeGetNativeFinalizer();

        /* loaded from: classes.dex */
        public static class TransferParameters {

            /* renamed from: a */
            public final double f67a;

            /* renamed from: b */
            public final double f68b;

            /* renamed from: c */
            public final double f69c;

            /* renamed from: d */
            public final double f70d;

            /* renamed from: e */
            public final double f71e;

            /* renamed from: f */
            public final double f72f;

            /* renamed from: g */
            public final double f73g;

            private TransferParameters(double a, double b, double c, double d, double e, double f, double g, boolean nonCurveTransferParameters) {
                if (!nonCurveTransferParameters) {
                    if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c) || Double.isNaN(d) || Double.isNaN(e) || Double.isNaN(f) || Double.isNaN(g)) {
                        throw new IllegalArgumentException("Parameters cannot be NaN");
                    }
                    if (d < 0.0d || d > Math.ulp(1.0f) + 1.0f) {
                        throw new IllegalArgumentException("Parameter d must be in the range [0..1], was " + d);
                    }
                    if (d == 0.0d && (a == 0.0d || g == 0.0d)) {
                        throw new IllegalArgumentException("Parameter a or g is zero, the transfer function is constant");
                    }
                    if (d >= 1.0d && c == 0.0d) {
                        throw new IllegalArgumentException("Parameter c is zero, the transfer function is constant");
                    }
                    if ((a == 0.0d || g == 0.0d) && c == 0.0d) {
                        throw new IllegalArgumentException("Parameter a or g is zero, and c is zero, the transfer function is constant");
                    }
                    if (c < 0.0d) {
                        throw new IllegalArgumentException("The transfer function must be increasing");
                    }
                    if (a < 0.0d || g < 0.0d) {
                        throw new IllegalArgumentException("The transfer function must be positive or increasing");
                    }
                }
                this.f67a = a;
                this.f68b = b;
                this.f69c = c;
                this.f70d = d;
                this.f71e = e;
                this.f72f = f;
                this.f73g = g;
            }

            public TransferParameters(double a, double b, double c, double d, double g) {
                this(a, b, c, d, 0.0d, 0.0d, g, false);
            }

            public TransferParameters(double a, double b, double c, double d, double e, double f, double g) {
                this(a, b, c, d, e, f, g, false);
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                TransferParameters that = (TransferParameters) o;
                if (Double.compare(that.f67a, this.f67a) == 0 && Double.compare(that.f68b, this.f68b) == 0 && Double.compare(that.f69c, this.f69c) == 0 && Double.compare(that.f70d, this.f70d) == 0 && Double.compare(that.f71e, this.f71e) == 0 && Double.compare(that.f72f, this.f72f) == 0 && Double.compare(that.f73g, this.f73g) == 0) {
                    return true;
                }
                return false;
            }

            public int hashCode() {
                long temp = Double.doubleToLongBits(this.f67a);
                int result = (int) ((temp >>> 32) ^ temp);
                long temp2 = Double.doubleToLongBits(this.f68b);
                int result2 = (result * 31) + ((int) ((temp2 >>> 32) ^ temp2));
                long temp3 = Double.doubleToLongBits(this.f69c);
                int result3 = (result2 * 31) + ((int) ((temp3 >>> 32) ^ temp3));
                long temp4 = Double.doubleToLongBits(this.f70d);
                int result4 = (result3 * 31) + ((int) ((temp4 >>> 32) ^ temp4));
                long temp5 = Double.doubleToLongBits(this.f71e);
                int result5 = (result4 * 31) + ((int) ((temp5 >>> 32) ^ temp5));
                long temp6 = Double.doubleToLongBits(this.f72f);
                int result6 = (result5 * 31) + ((int) ((temp6 >>> 32) ^ temp6));
                long temp7 = Double.doubleToLongBits(this.f73g);
                return (result6 * 31) + ((int) ((temp7 >>> 32) ^ temp7));
            }
        }

        @Override // android.graphics.ColorSpace
        long getNativeInstance() {
            long j = this.mNativePtr;
            if (j == 0) {
                throw new IllegalArgumentException("ColorSpace must use an ICC parametric transfer function! used " + this);
            }
            return j;
        }

        private static DoubleUnaryOperator generateOETF(final TransferParameters function) {
            boolean isNonCurveTransferParameters = function.equals(ColorSpace.BT2020_HLG_TRANSFER_PARAMETERS) || function.equals(ColorSpace.BT2020_PQ_TRANSFER_PARAMETERS);
            if (isNonCurveTransferParameters) {
                return (function.f72f != 0.0d || function.f73g >= 0.0d) ? new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda2
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        double transferST2048OETF;
                        transferST2048OETF = ColorSpace.transferST2048OETF(d);
                        return transferST2048OETF;
                    }
                } : new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda1
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        double transferHLGOETF;
                        transferHLGOETF = ColorSpace.transferHLGOETF(d);
                        return transferHLGOETF;
                    }
                };
            } else if (function.f71e == 0.0d && function.f72f == 0.0d) {
                return new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda3
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        double rcpResponse;
                        rcpResponse = ColorSpace.rcpResponse(d, r0.f67a, r0.f68b, r0.f69c, r0.f70d, ColorSpace.Rgb.TransferParameters.this.f73g);
                        return rcpResponse;
                    }
                };
            } else {
                return new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda4
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        double rcpResponse;
                        rcpResponse = ColorSpace.rcpResponse(d, r0.f67a, r0.f68b, r0.f69c, r0.f70d, r0.f71e, r0.f72f, ColorSpace.Rgb.TransferParameters.this.f73g);
                        return rcpResponse;
                    }
                };
            }
        }

        private static DoubleUnaryOperator generateEOTF(final TransferParameters function) {
            boolean isNonCurveTransferParameters = function.equals(ColorSpace.BT2020_HLG_TRANSFER_PARAMETERS) || function.equals(ColorSpace.BT2020_PQ_TRANSFER_PARAMETERS);
            if (isNonCurveTransferParameters) {
                return (function.f72f != 0.0d || function.f73g >= 0.0d) ? new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda8
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        double transferST2048EOTF;
                        transferST2048EOTF = ColorSpace.transferST2048EOTF(d);
                        return transferST2048EOTF;
                    }
                } : new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda7
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        double transferHLGEOTF;
                        transferHLGEOTF = ColorSpace.transferHLGEOTF(d);
                        return transferHLGEOTF;
                    }
                };
            } else if (function.f71e == 0.0d && function.f72f == 0.0d) {
                return new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda9
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        double response;
                        response = ColorSpace.response(d, r0.f67a, r0.f68b, r0.f69c, r0.f70d, ColorSpace.Rgb.TransferParameters.this.f73g);
                        return response;
                    }
                };
            } else {
                return new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda10
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        double response;
                        response = ColorSpace.response(d, r0.f67a, r0.f68b, r0.f69c, r0.f70d, r0.f71e, r0.f72f, ColorSpace.Rgb.TransferParameters.this.f73g);
                        return response;
                    }
                };
            }
        }

        public Rgb(String name, float[] toXYZ, DoubleUnaryOperator oetf, DoubleUnaryOperator eotf) {
            this(name, computePrimaries(toXYZ), computeWhitePoint(toXYZ), null, oetf, eotf, 0.0f, 1.0f, null, -1);
        }

        public Rgb(String name, float[] primaries, float[] whitePoint, DoubleUnaryOperator oetf, DoubleUnaryOperator eotf, float min, float max) {
            this(name, primaries, whitePoint, null, oetf, eotf, min, max, null, -1);
        }

        public Rgb(String name, float[] toXYZ, TransferParameters function) {
            this(name, isGray(toXYZ) ? ColorSpace.GRAY_PRIMARIES : computePrimaries(toXYZ), computeWhitePoint(toXYZ), isGray(toXYZ) ? toXYZ : null, function, -1);
        }

        public Rgb(String name, float[] primaries, float[] whitePoint, TransferParameters function) {
            this(name, primaries, whitePoint, null, function, -1);
        }

        private Rgb(String name, float[] primaries, float[] whitePoint, float[] transform, TransferParameters function, int id) {
            this(name, primaries, whitePoint, transform, generateOETF(function), generateEOTF(function), 0.0f, 1.0f, function, id);
        }

        public Rgb(String name, float[] toXYZ, double gamma) {
            this(name, computePrimaries(toXYZ), computeWhitePoint(toXYZ), gamma, 0.0f, 1.0f, -1);
        }

        public Rgb(String name, float[] primaries, float[] whitePoint, double gamma) {
            this(name, primaries, whitePoint, gamma, 0.0f, 1.0f, -1);
        }

        private Rgb(String name, float[] primaries, float[] whitePoint, final double gamma, float min, float max, int id) {
            this(name, primaries, whitePoint, null, gamma == 1.0d ? DoubleUnaryOperator.identity() : new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda5
                @Override // java.util.function.DoubleUnaryOperator
                public final double applyAsDouble(double d) {
                    double pow;
                    pow = Math.pow(x >= 0.0d ? d : 0.0d, 1.0d / gamma);
                    return pow;
                }
            }, gamma == 1.0d ? DoubleUnaryOperator.identity() : new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda6
                @Override // java.util.function.DoubleUnaryOperator
                public final double applyAsDouble(double d) {
                    double pow;
                    pow = Math.pow(x >= 0.0d ? d : 0.0d, gamma);
                    return pow;
                }
            }, min, max, new TransferParameters(1.0d, 0.0d, 0.0d, 0.0d, gamma), id);
        }

        private Rgb(String name, float[] primaries, float[] whitePoint, float[] transform, DoubleUnaryOperator oetf, DoubleUnaryOperator eotf, float min, float max, TransferParameters transferParameters, int id) {
            super(name, Model.RGB, id);
            float[] fArr;
            if (primaries == null || (primaries.length != 6 && primaries.length != 9)) {
                throw new IllegalArgumentException("The color space's primaries must be defined as an array of 6 floats in xyY or 9 floats in XYZ");
            }
            if (whitePoint == null || (whitePoint.length != 2 && whitePoint.length != 3)) {
                throw new IllegalArgumentException("The color space's white point must be defined as an array of 2 floats in xyY or 3 float in XYZ");
            }
            if (oetf == null || eotf == null) {
                throw new IllegalArgumentException("The transfer functions of a color space cannot be null");
            }
            if (min >= max) {
                throw new IllegalArgumentException("Invalid range: min=" + min + ", max=" + max + "; min must be strictly < max");
            }
            float[] xyWhitePoint = xyWhitePoint(whitePoint);
            this.mWhitePoint = xyWhitePoint;
            float[] xyPrimaries = xyPrimaries(primaries);
            this.mPrimaries = xyPrimaries;
            if (transform == null) {
                this.mTransform = computeXYZMatrix(xyPrimaries, xyWhitePoint);
            } else if (transform.length != 9) {
                throw new IllegalArgumentException("Transform must have 9 entries! Has " + transform.length);
            } else {
                this.mTransform = transform;
            }
            this.mInverseTransform = ColorSpace.inverse3x3(this.mTransform);
            this.mOetf = oetf;
            this.mEotf = eotf;
            this.mMin = min;
            this.mMax = max;
            DoubleUnaryOperator clamp = new DoubleUnaryOperator() { // from class: android.graphics.ColorSpace$Rgb$$ExternalSyntheticLambda0
                @Override // java.util.function.DoubleUnaryOperator
                public final double applyAsDouble(double d) {
                    double clamp2;
                    clamp2 = ColorSpace.Rgb.this.clamp(d);
                    return clamp2;
                }
            };
            this.mClampedOetf = oetf.andThen(clamp);
            this.mClampedEotf = clamp.andThen(eotf);
            this.mTransferParameters = transferParameters;
            this.mIsWideGamut = isWideGamut(xyPrimaries, min, max);
            this.mIsSrgb = isSrgb(xyPrimaries, xyWhitePoint, oetf, eotf, min, max, id);
            if (transferParameters == null) {
                this.mNativePtr = 0L;
            } else if (xyWhitePoint == null || (fArr = this.mTransform) == null) {
                throw new IllegalStateException("ColorSpace (" + this + ") cannot create native object! mWhitePoint: " + Arrays.toString(xyWhitePoint) + " mTransform: " + Arrays.toString(this.mTransform));
            } else {
                float[] nativeTransform = ColorSpace.adaptToIlluminantD50(xyWhitePoint, fArr);
                long nativeCreate = nativeCreate((float) transferParameters.f67a, (float) transferParameters.f68b, (float) transferParameters.f69c, (float) transferParameters.f70d, (float) transferParameters.f71e, (float) transferParameters.f72f, (float) transferParameters.f73g, nativeTransform);
                this.mNativePtr = nativeCreate;
                NoImagePreloadHolder.sRegistry.registerNativeAllocation(this, nativeCreate);
            }
        }

        /* loaded from: classes.dex */
        private static class NoImagePreloadHolder {
            public static final NativeAllocationRegistry sRegistry = new NativeAllocationRegistry(Rgb.class.getClassLoader(), Rgb.nativeGetNativeFinalizer(), 0);

            private NoImagePreloadHolder() {
            }
        }

        private Rgb(Rgb colorSpace, float[] transform, float[] whitePoint) {
            this(colorSpace.getName(), colorSpace.mPrimaries, whitePoint, transform, colorSpace.mOetf, colorSpace.mEotf, colorSpace.mMin, colorSpace.mMax, colorSpace.mTransferParameters, -1);
        }

        public float[] getWhitePoint(float[] whitePoint) {
            float[] fArr = this.mWhitePoint;
            whitePoint[0] = fArr[0];
            whitePoint[1] = fArr[1];
            return whitePoint;
        }

        public float[] getWhitePoint() {
            float[] fArr = this.mWhitePoint;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public float[] getPrimaries(float[] primaries) {
            float[] fArr = this.mPrimaries;
            System.arraycopy(fArr, 0, primaries, 0, fArr.length);
            return primaries;
        }

        public float[] getPrimaries() {
            float[] fArr = this.mPrimaries;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public float[] getTransform(float[] transform) {
            float[] fArr = this.mTransform;
            System.arraycopy(fArr, 0, transform, 0, fArr.length);
            return transform;
        }

        public float[] getTransform() {
            float[] fArr = this.mTransform;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public float[] getInverseTransform(float[] inverseTransform) {
            float[] fArr = this.mInverseTransform;
            System.arraycopy(fArr, 0, inverseTransform, 0, fArr.length);
            return inverseTransform;
        }

        public float[] getInverseTransform() {
            float[] fArr = this.mInverseTransform;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public DoubleUnaryOperator getOetf() {
            return this.mClampedOetf;
        }

        public DoubleUnaryOperator getEotf() {
            return this.mClampedEotf;
        }

        public TransferParameters getTransferParameters() {
            TransferParameters transferParameters = this.mTransferParameters;
            if (transferParameters != null && !transferParameters.equals(ColorSpace.BT2020_PQ_TRANSFER_PARAMETERS) && !this.mTransferParameters.equals(ColorSpace.BT2020_HLG_TRANSFER_PARAMETERS)) {
                return this.mTransferParameters;
            }
            return null;
        }

        @Override // android.graphics.ColorSpace
        public boolean isSrgb() {
            return this.mIsSrgb;
        }

        @Override // android.graphics.ColorSpace
        public boolean isWideGamut() {
            return this.mIsWideGamut;
        }

        @Override // android.graphics.ColorSpace
        public float getMinValue(int component) {
            return this.mMin;
        }

        @Override // android.graphics.ColorSpace
        public float getMaxValue(int component) {
            return this.mMax;
        }

        public float[] toLinear(float r, float g, float b) {
            return toLinear(new float[]{r, g, b});
        }

        public float[] toLinear(float[] v) {
            v[0] = (float) this.mClampedEotf.applyAsDouble(v[0]);
            v[1] = (float) this.mClampedEotf.applyAsDouble(v[1]);
            v[2] = (float) this.mClampedEotf.applyAsDouble(v[2]);
            return v;
        }

        public float[] fromLinear(float r, float g, float b) {
            return fromLinear(new float[]{r, g, b});
        }

        public float[] fromLinear(float[] v) {
            v[0] = (float) this.mClampedOetf.applyAsDouble(v[0]);
            v[1] = (float) this.mClampedOetf.applyAsDouble(v[1]);
            v[2] = (float) this.mClampedOetf.applyAsDouble(v[2]);
            return v;
        }

        @Override // android.graphics.ColorSpace
        public float[] toXyz(float[] v) {
            v[0] = (float) this.mClampedEotf.applyAsDouble(v[0]);
            v[1] = (float) this.mClampedEotf.applyAsDouble(v[1]);
            v[2] = (float) this.mClampedEotf.applyAsDouble(v[2]);
            return ColorSpace.mul3x3Float3(this.mTransform, v);
        }

        @Override // android.graphics.ColorSpace
        public float[] fromXyz(float[] v) {
            ColorSpace.mul3x3Float3(this.mInverseTransform, v);
            v[0] = (float) this.mClampedOetf.applyAsDouble(v[0]);
            v[1] = (float) this.mClampedOetf.applyAsDouble(v[1]);
            v[2] = (float) this.mClampedOetf.applyAsDouble(v[2]);
            return v;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public double clamp(double x) {
            float f = this.mMin;
            if (x >= f) {
                f = this.mMax;
                if (x <= f) {
                    return x;
                }
            }
            return f;
        }

        @Override // android.graphics.ColorSpace
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass() || !super.equals(o)) {
                return false;
            }
            Rgb rgb = (Rgb) o;
            if (Float.compare(rgb.mMin, this.mMin) != 0 || Float.compare(rgb.mMax, this.mMax) != 0 || !Arrays.equals(this.mWhitePoint, rgb.mWhitePoint) || !Arrays.equals(this.mPrimaries, rgb.mPrimaries)) {
                return false;
            }
            TransferParameters transferParameters = this.mTransferParameters;
            if (transferParameters != null) {
                return transferParameters.equals(rgb.mTransferParameters);
            }
            if (rgb.mTransferParameters == null) {
                return true;
            }
            if (!this.mOetf.equals(rgb.mOetf)) {
                return false;
            }
            return this.mEotf.equals(rgb.mEotf);
        }

        @Override // android.graphics.ColorSpace
        public int hashCode() {
            int result = super.hashCode();
            int result2 = ((((result * 31) + Arrays.hashCode(this.mWhitePoint)) * 31) + Arrays.hashCode(this.mPrimaries)) * 31;
            float f = this.mMin;
            int result3 = (result2 + (f != 0.0f ? Float.floatToIntBits(f) : 0)) * 31;
            float f2 = this.mMax;
            int result4 = (result3 + (f2 != 0.0f ? Float.floatToIntBits(f2) : 0)) * 31;
            TransferParameters transferParameters = this.mTransferParameters;
            int result5 = result4 + (transferParameters != null ? transferParameters.hashCode() : 0);
            if (this.mTransferParameters == null) {
                return (((result5 * 31) + this.mOetf.hashCode()) * 31) + this.mEotf.hashCode();
            }
            return result5;
        }

        private static boolean isSrgb(float[] primaries, float[] whitePoint, DoubleUnaryOperator OETF, DoubleUnaryOperator EOTF, float min, float max, int id) {
            if (id == 0) {
                return true;
            }
            if (ColorSpace.compare(primaries, ColorSpace.SRGB_PRIMARIES) && ColorSpace.compare(whitePoint, ILLUMINANT_D65) && min == 0.0f && max == 1.0f) {
                Rgb srgb = (Rgb) get(Named.SRGB);
                for (double x = 0.0d; x <= 1.0d; x += 0.00392156862745098d) {
                    if (!compare(x, OETF, srgb.mOetf) || !compare(x, EOTF, srgb.mEotf)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        private static boolean isGray(float[] toXYZ) {
            return toXYZ.length == 9 && toXYZ[1] == 0.0f && toXYZ[2] == 0.0f && toXYZ[3] == 0.0f && toXYZ[5] == 0.0f && toXYZ[6] == 0.0f && toXYZ[7] == 0.0f;
        }

        private static boolean compare(double point, DoubleUnaryOperator a, DoubleUnaryOperator b) {
            double rA = a.applyAsDouble(point);
            double rB = b.applyAsDouble(point);
            return Math.abs(rA - rB) <= 0.001d;
        }

        private static boolean isWideGamut(float[] primaries, float min, float max) {
            return (area(primaries) / area(ColorSpace.NTSC_1953_PRIMARIES) > 0.9f && contains(primaries, ColorSpace.SRGB_PRIMARIES)) || (min < 0.0f && max > 1.0f);
        }

        private static float area(float[] primaries) {
            float Rx = primaries[0];
            float Ry = primaries[1];
            float Gx = primaries[2];
            float Gy = primaries[3];
            float Bx = primaries[4];
            float By = primaries[5];
            float det = (((((Rx * Gy) + (Ry * Bx)) + (Gx * By)) - (Gy * Bx)) - (Ry * Gx)) - (Rx * By);
            float r = 0.5f * det;
            return r < 0.0f ? -r : r;
        }

        private static float cross(float ax, float ay, float bx, float by) {
            return (ax * by) - (ay * bx);
        }

        private static boolean contains(float[] p1, float[] p2) {
            float[] p0 = {p1[0] - p2[0], p1[1] - p2[1], p1[2] - p2[2], p1[3] - p2[3], p1[4] - p2[4], p1[5] - p2[5]};
            return cross(p0[0], p0[1], p2[0] - p2[4], p2[1] - p2[5]) >= 0.0f && cross(p2[0] - p2[2], p2[1] - p2[3], p0[0], p0[1]) >= 0.0f && cross(p0[2], p0[3], p2[2] - p2[0], p2[3] - p2[1]) >= 0.0f && cross(p2[2] - p2[4], p2[3] - p2[5], p0[2], p0[3]) >= 0.0f && cross(p0[4], p0[5], p2[4] - p2[2], p2[5] - p2[3]) >= 0.0f && cross(p2[4] - p2[0], p2[5] - p2[1], p0[4], p0[5]) >= 0.0f;
        }

        private static float[] computePrimaries(float[] toXYZ) {
            float[] r = ColorSpace.mul3x3Float3(toXYZ, new float[]{1.0f, 0.0f, 0.0f});
            float[] g = ColorSpace.mul3x3Float3(toXYZ, new float[]{0.0f, 1.0f, 0.0f});
            float[] b = ColorSpace.mul3x3Float3(toXYZ, new float[]{0.0f, 0.0f, 1.0f});
            float rSum = r[0] + r[1] + r[2];
            float gSum = g[0] + g[1] + g[2];
            float bSum = b[0] + b[1] + b[2];
            return new float[]{r[0] / rSum, r[1] / rSum, g[0] / gSum, g[1] / gSum, b[0] / bSum, b[1] / bSum};
        }

        private static float[] computeWhitePoint(float[] toXYZ) {
            float[] w = ColorSpace.mul3x3Float3(toXYZ, new float[]{1.0f, 1.0f, 1.0f});
            float sum = w[0] + w[1] + w[2];
            return new float[]{w[0] / sum, w[1] / sum};
        }

        private static float[] xyPrimaries(float[] primaries) {
            float[] xyPrimaries = new float[6];
            if (primaries.length == 9) {
                float sum = primaries[0] + primaries[1] + primaries[2];
                xyPrimaries[0] = primaries[0] / sum;
                xyPrimaries[1] = primaries[1] / sum;
                float sum2 = primaries[3] + primaries[4] + primaries[5];
                xyPrimaries[2] = primaries[3] / sum2;
                xyPrimaries[3] = primaries[4] / sum2;
                float sum3 = primaries[6] + primaries[7] + primaries[8];
                xyPrimaries[4] = primaries[6] / sum3;
                xyPrimaries[5] = primaries[7] / sum3;
            } else {
                System.arraycopy(primaries, 0, xyPrimaries, 0, 6);
            }
            return xyPrimaries;
        }

        private static float[] xyWhitePoint(float[] whitePoint) {
            float[] xyWhitePoint = new float[2];
            if (whitePoint.length == 3) {
                float sum = whitePoint[0] + whitePoint[1] + whitePoint[2];
                xyWhitePoint[0] = whitePoint[0] / sum;
                xyWhitePoint[1] = whitePoint[1] / sum;
            } else {
                System.arraycopy(whitePoint, 0, xyWhitePoint, 0, 2);
            }
            return xyWhitePoint;
        }

        private static float[] computeXYZMatrix(float[] primaries, float[] whitePoint) {
            float Rx = primaries[0];
            float Ry = primaries[1];
            float Gx = primaries[2];
            float Gy = primaries[3];
            float Bx = primaries[4];
            float By = primaries[5];
            float Wx = whitePoint[0];
            float Wy = whitePoint[1];
            float oneRxRy = (1.0f - Rx) / Ry;
            float oneGxGy = (1.0f - Gx) / Gy;
            float oneBxBy = (1.0f - Bx) / By;
            float oneWxWy = (1.0f - Wx) / Wy;
            float RxRy = Rx / Ry;
            float GxGy = Gx / Gy;
            float BxBy = Bx / By;
            float WxWy = Wx / Wy;
            float BY = (((oneWxWy - oneRxRy) * (GxGy - RxRy)) - ((WxWy - RxRy) * (oneGxGy - oneRxRy))) / (((oneBxBy - oneRxRy) * (GxGy - RxRy)) - ((BxBy - RxRy) * (oneGxGy - oneRxRy)));
            float GY = ((WxWy - RxRy) - ((BxBy - RxRy) * BY)) / (GxGy - RxRy);
            float RY = (1.0f - GY) - BY;
            float RYRy = RY / Ry;
            float GYGy = GY / Gy;
            float BYBy = BY / By;
            return new float[]{RYRy * Rx, RY, ((1.0f - Rx) - Ry) * RYRy, GYGy * Gx, GY, ((1.0f - Gx) - Gy) * GYGy, BYBy * Bx, BY, ((1.0f - Bx) - By) * BYBy};
        }
    }

    /* loaded from: classes.dex */
    public static class Connector {
        private final ColorSpace mDestination;
        private final RenderIntent mIntent;
        private final ColorSpace mSource;
        private final float[] mTransform;
        private final ColorSpace mTransformDestination;
        private final ColorSpace mTransformSource;

        Connector(ColorSpace source, ColorSpace destination, RenderIntent intent) {
            this(source, destination, source.getModel() == Model.RGB ? ColorSpace.adapt(source, ColorSpace.ILLUMINANT_D50_XYZ) : source, destination.getModel() == Model.RGB ? ColorSpace.adapt(destination, ColorSpace.ILLUMINANT_D50_XYZ) : destination, intent, computeTransform(source, destination, intent));
        }

        private Connector(ColorSpace source, ColorSpace destination, ColorSpace transformSource, ColorSpace transformDestination, RenderIntent intent, float[] transform) {
            this.mSource = source;
            this.mDestination = destination;
            this.mTransformSource = transformSource;
            this.mTransformDestination = transformDestination;
            this.mIntent = intent;
            this.mTransform = transform;
        }

        private static float[] computeTransform(ColorSpace source, ColorSpace destination, RenderIntent intent) {
            if (intent != RenderIntent.ABSOLUTE) {
                return null;
            }
            boolean srcRGB = source.getModel() == Model.RGB;
            boolean dstRGB = destination.getModel() == Model.RGB;
            if (srcRGB && dstRGB) {
                return null;
            }
            if (srcRGB || dstRGB) {
                Rgb rgb = (Rgb) (srcRGB ? source : destination);
                float[] srcXYZ = srcRGB ? ColorSpace.xyYToXyz(rgb.mWhitePoint) : ColorSpace.ILLUMINANT_D50_XYZ;
                float[] dstXYZ = dstRGB ? ColorSpace.xyYToXyz(rgb.mWhitePoint) : ColorSpace.ILLUMINANT_D50_XYZ;
                return new float[]{srcXYZ[0] / dstXYZ[0], srcXYZ[1] / dstXYZ[1], srcXYZ[2] / dstXYZ[2]};
            }
            return null;
        }

        public ColorSpace getSource() {
            return this.mSource;
        }

        public ColorSpace getDestination() {
            return this.mDestination;
        }

        public RenderIntent getRenderIntent() {
            return this.mIntent;
        }

        public float[] transform(float r, float g, float b) {
            return transform(new float[]{r, g, b});
        }

        public float[] transform(float[] v) {
            float[] xyz = this.mTransformSource.toXyz(v);
            float[] fArr = this.mTransform;
            if (fArr != null) {
                xyz[0] = xyz[0] * fArr[0];
                xyz[1] = xyz[1] * fArr[1];
                xyz[2] = xyz[2] * fArr[2];
            }
            return this.mTransformDestination.fromXyz(xyz);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Rgb extends Connector {
            private final Rgb mDestination;
            private final Rgb mSource;
            private final float[] mTransform;

            Rgb(Rgb source, Rgb destination, RenderIntent intent) {
                super(destination, source, destination, intent, null);
                this.mSource = source;
                this.mDestination = destination;
                this.mTransform = computeTransform(source, destination, intent);
            }

            @Override // android.graphics.ColorSpace.Connector
            public float[] transform(float[] rgb) {
                rgb[0] = (float) this.mSource.mClampedEotf.applyAsDouble(rgb[0]);
                rgb[1] = (float) this.mSource.mClampedEotf.applyAsDouble(rgb[1]);
                rgb[2] = (float) this.mSource.mClampedEotf.applyAsDouble(rgb[2]);
                ColorSpace.mul3x3Float3(this.mTransform, rgb);
                rgb[0] = (float) this.mDestination.mClampedOetf.applyAsDouble(rgb[0]);
                rgb[1] = (float) this.mDestination.mClampedOetf.applyAsDouble(rgb[1]);
                rgb[2] = (float) this.mDestination.mClampedOetf.applyAsDouble(rgb[2]);
                return rgb;
            }

            private static float[] computeTransform(Rgb source, Rgb destination, RenderIntent intent) {
                if (ColorSpace.compare(source.mWhitePoint, destination.mWhitePoint)) {
                    return ColorSpace.mul3x3(destination.mInverseTransform, source.mTransform);
                }
                float[] transform = source.mTransform;
                float[] inverseTransform = destination.mInverseTransform;
                float[] srcXYZ = ColorSpace.xyYToXyz(source.mWhitePoint);
                float[] dstXYZ = ColorSpace.xyYToXyz(destination.mWhitePoint);
                if (!ColorSpace.compare(source.mWhitePoint, ColorSpace.ILLUMINANT_D50)) {
                    float[] srcAdaptation = ColorSpace.chromaticAdaptation(Adaptation.BRADFORD.mTransform, srcXYZ, Arrays.copyOf(ColorSpace.ILLUMINANT_D50_XYZ, 3));
                    transform = ColorSpace.mul3x3(srcAdaptation, source.mTransform);
                }
                float[] srcAdaptation2 = destination.mWhitePoint;
                if (!ColorSpace.compare(srcAdaptation2, ColorSpace.ILLUMINANT_D50)) {
                    float[] dstAdaptation = ColorSpace.chromaticAdaptation(Adaptation.BRADFORD.mTransform, dstXYZ, Arrays.copyOf(ColorSpace.ILLUMINANT_D50_XYZ, 3));
                    inverseTransform = ColorSpace.inverse3x3(ColorSpace.mul3x3(dstAdaptation, destination.mTransform));
                }
                if (intent == RenderIntent.ABSOLUTE) {
                    transform = ColorSpace.mul3x3Diag(new float[]{srcXYZ[0] / dstXYZ[0], srcXYZ[1] / dstXYZ[1], srcXYZ[2] / dstXYZ[2]}, transform);
                }
                return ColorSpace.mul3x3(inverseTransform, transform);
            }
        }

        static Connector identity(ColorSpace source) {
            return new Connector(source, source, RenderIntent.RELATIVE) { // from class: android.graphics.ColorSpace.Connector.1
                @Override // android.graphics.ColorSpace.Connector
                public float[] transform(float[] v) {
                    return v;
                }
            };
        }
    }
}
