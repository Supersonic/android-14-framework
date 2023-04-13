package android.graphics;

import android.graphics.ColorSpace;
/* loaded from: classes.dex */
public class SweepGradient extends Shader {
    private int mColor0;
    private int mColor1;
    private final long[] mColorLongs;
    private int[] mColors;
    private float mCx;
    private float mCy;
    private float[] mPositions;

    private static native long nativeCreate(long j, float f, float f2, long[] jArr, float[] fArr, long j2);

    public SweepGradient(float cx, float cy, int[] colors, float[] positions) {
        this(cx, cy, convertColors(colors), positions, ColorSpace.get(ColorSpace.Named.SRGB));
    }

    public SweepGradient(float cx, float cy, long[] colors, float[] positions) {
        this(cx, cy, (long[]) colors.clone(), positions, detectColorSpace(colors));
    }

    private SweepGradient(float cx, float cy, long[] colors, float[] positions, ColorSpace colorSpace) {
        super(colorSpace);
        if (positions != null && colors.length != positions.length) {
            throw new IllegalArgumentException("color and position arrays must be of equal length");
        }
        this.mCx = cx;
        this.mCy = cy;
        this.mColorLongs = colors;
        this.mPositions = positions != null ? (float[]) positions.clone() : null;
    }

    public SweepGradient(float cx, float cy, int color0, int color1) {
        this(cx, cy, Color.pack(color0), Color.pack(color1));
    }

    public SweepGradient(float cx, float cy, long color0, long color1) {
        this(cx, cy, new long[]{color0, color1}, (float[]) null);
    }

    @Override // android.graphics.Shader
    protected long createNativeInstance(long nativeMatrix, boolean filterFromPaint) {
        return nativeCreate(nativeMatrix, this.mCx, this.mCy, this.mColorLongs, this.mPositions, colorSpace().getNativeInstance());
    }
}
