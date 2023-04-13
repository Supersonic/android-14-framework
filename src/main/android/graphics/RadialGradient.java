package android.graphics;

import android.graphics.ColorSpace;
import android.graphics.Shader;
/* loaded from: classes.dex */
public class RadialGradient extends Shader {
    private int mCenterColor;
    private final long[] mColorLongs;
    private int[] mColors;
    private int mEdgeColor;
    private final float mFocalRadius;
    private final float mFocalX;
    private final float mFocalY;
    private float[] mPositions;
    private float mRadius;
    private Shader.TileMode mTileMode;

    /* renamed from: mX */
    private float f82mX;

    /* renamed from: mY */
    private float f83mY;

    private static native long nativeCreate(long j, float f, float f2, float f3, float f4, float f5, float f6, long[] jArr, float[] fArr, int i, long j2);

    public RadialGradient(float centerX, float centerY, float radius, int[] colors, float[] stops, Shader.TileMode tileMode) {
        this(centerX, centerY, 0.0f, centerX, centerY, radius, convertColors(colors), stops, tileMode, ColorSpace.get(ColorSpace.Named.SRGB));
    }

    public RadialGradient(float centerX, float centerY, float radius, long[] colors, float[] stops, Shader.TileMode tileMode) {
        this(centerX, centerY, 0.0f, centerX, centerY, radius, (long[]) colors.clone(), stops, tileMode, detectColorSpace(colors));
    }

    public RadialGradient(float startX, float startY, float startRadius, float endX, float endY, float endRadius, long[] colors, float[] stops, Shader.TileMode tileMode) {
        this(startX, startY, startRadius, endX, endY, endRadius, (long[]) colors.clone(), stops, tileMode, detectColorSpace(colors));
    }

    private RadialGradient(float startX, float startY, float startRadius, float endX, float endY, float endRadius, long[] colors, float[] stops, Shader.TileMode tileMode, ColorSpace colorSpace) {
        super(colorSpace);
        if (startRadius < 0.0f) {
            throw new IllegalArgumentException("starting/focal radius must be >= 0");
        }
        if (endRadius <= 0.0f) {
            throw new IllegalArgumentException("ending radius must be > 0");
        }
        if (stops != null && colors.length != stops.length) {
            throw new IllegalArgumentException("color and position arrays must be of equal length");
        }
        this.f82mX = endX;
        this.f83mY = endY;
        this.mRadius = endRadius;
        this.mFocalX = startX;
        this.mFocalY = startY;
        this.mFocalRadius = startRadius;
        this.mColorLongs = colors;
        this.mPositions = stops != null ? (float[]) stops.clone() : null;
        this.mTileMode = tileMode;
    }

    public RadialGradient(float centerX, float centerY, float radius, int centerColor, int edgeColor, Shader.TileMode tileMode) {
        this(centerX, centerY, radius, Color.pack(centerColor), Color.pack(edgeColor), tileMode);
    }

    public RadialGradient(float centerX, float centerY, float radius, long centerColor, long edgeColor, Shader.TileMode tileMode) {
        this(centerX, centerY, radius, new long[]{centerColor, edgeColor}, (float[]) null, tileMode);
    }

    @Override // android.graphics.Shader
    protected long createNativeInstance(long nativeMatrix, boolean filterFromPaint) {
        return nativeCreate(nativeMatrix, this.mFocalX, this.mFocalY, this.mFocalRadius, this.f82mX, this.f83mY, this.mRadius, this.mColorLongs, this.mPositions, this.mTileMode.nativeInt, colorSpace().getNativeInstance());
    }
}
