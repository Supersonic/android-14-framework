package android.graphics;

import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public class Path {
    public final long mNativePath;
    private static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(Path.class.getClassLoader(), nGetFinalizer());
    static final FillType[] sFillTypeArray = {FillType.WINDING, FillType.EVEN_ODD, FillType.INVERSE_WINDING, FillType.INVERSE_EVEN_ODD};

    /* renamed from: android.graphics.Path$Op */
    /* loaded from: classes.dex */
    public enum EnumC0807Op {
        DIFFERENCE,
        INTERSECT,
        UNION,
        XOR,
        REVERSE_DIFFERENCE
    }

    private static native void nAddArc(long j, float f, float f2, float f3, float f4, float f5, float f6);

    private static native void nAddCircle(long j, float f, float f2, float f3, int i);

    private static native void nAddOval(long j, float f, float f2, float f3, float f4, int i);

    private static native void nAddPath(long j, long j2);

    private static native void nAddPath(long j, long j2, float f, float f2);

    private static native void nAddPath(long j, long j2, long j3);

    private static native void nAddRect(long j, float f, float f2, float f3, float f4, int i);

    private static native void nAddRoundRect(long j, float f, float f2, float f3, float f4, float f5, float f6, int i);

    private static native void nAddRoundRect(long j, float f, float f2, float f3, float f4, float[] fArr, int i);

    private static native float[] nApproximate(long j, float f);

    private static native void nArcTo(long j, float f, float f2, float f3, float f4, float f5, float f6, boolean z);

    private static native void nClose(long j);

    private static native void nComputeBounds(long j, RectF rectF);

    private static native void nConicTo(long j, float f, float f2, float f3, float f4, float f5);

    private static native void nCubicTo(long j, float f, float f2, float f3, float f4, float f5, float f6);

    @CriticalNative
    private static native int nGetFillType(long j);

    private static native long nGetFinalizer();

    @CriticalNative
    private static native int nGetGenerationID(long j);

    private static native void nIncReserve(long j, int i);

    private static native long nInit();

    private static native long nInit(long j);

    private static native boolean nInterpolate(long j, long j2, float f, long j3);

    @CriticalNative
    private static native boolean nIsConvex(long j);

    @CriticalNative
    private static native boolean nIsEmpty(long j);

    @CriticalNative
    private static native boolean nIsInterpolatable(long j, long j2);

    @FastNative
    private static native boolean nIsRect(long j, RectF rectF);

    private static native void nLineTo(long j, float f, float f2);

    private static native void nMoveTo(long j, float f, float f2);

    private static native void nOffset(long j, float f, float f2);

    private static native boolean nOp(long j, long j2, int i, long j3);

    private static native void nQuadTo(long j, float f, float f2, float f3, float f4);

    private static native void nRConicTo(long j, float f, float f2, float f3, float f4, float f5);

    private static native void nRCubicTo(long j, float f, float f2, float f3, float f4, float f5, float f6);

    private static native void nRLineTo(long j, float f, float f2);

    private static native void nRMoveTo(long j, float f, float f2);

    private static native void nRQuadTo(long j, float f, float f2, float f3, float f4);

    @CriticalNative
    private static native void nReset(long j);

    @CriticalNative
    private static native void nRewind(long j);

    private static native void nSet(long j, long j2);

    @CriticalNative
    private static native void nSetFillType(long j, int i);

    private static native void nSetLastPoint(long j, float f, float f2);

    private static native void nTransform(long j, long j2);

    private static native void nTransform(long j, long j2, long j3);

    public Path() {
        long nInit = nInit();
        this.mNativePath = nInit;
        sRegistry.registerNativeAllocation(this, nInit);
    }

    public Path(Path src) {
        long nInit = nInit(src != null ? src.mNativePath : 0L);
        this.mNativePath = nInit;
        sRegistry.registerNativeAllocation(this, nInit);
    }

    public void reset() {
        FillType fillType = getFillType();
        nReset(this.mNativePath);
        setFillType(fillType);
    }

    public void rewind() {
        nRewind(this.mNativePath);
    }

    public void set(Path src) {
        if (this == src) {
            return;
        }
        nSet(this.mNativePath, src.mNativePath);
    }

    public PathIterator getPathIterator() {
        return new PathIterator(this);
    }

    /* renamed from: op */
    public boolean m183op(Path path, EnumC0807Op op) {
        return m182op(this, path, op);
    }

    /* renamed from: op */
    public boolean m182op(Path path1, Path path2, EnumC0807Op op) {
        return nOp(path1.mNativePath, path2.mNativePath, op.ordinal(), this.mNativePath);
    }

    @Deprecated
    public boolean isConvex() {
        return nIsConvex(this.mNativePath);
    }

    /* loaded from: classes.dex */
    public enum FillType {
        WINDING(0),
        EVEN_ODD(1),
        INVERSE_WINDING(2),
        INVERSE_EVEN_ODD(3);
        
        final int nativeInt;

        FillType(int ni) {
            this.nativeInt = ni;
        }
    }

    public FillType getFillType() {
        return sFillTypeArray[nGetFillType(this.mNativePath)];
    }

    public void setFillType(FillType ft) {
        nSetFillType(this.mNativePath, ft.nativeInt);
    }

    public boolean isInverseFillType() {
        int ft = nGetFillType(this.mNativePath);
        return (FillType.INVERSE_WINDING.nativeInt & ft) != 0;
    }

    public void toggleInverseFillType() {
        int ft = nGetFillType(this.mNativePath);
        nSetFillType(this.mNativePath, ft ^ FillType.INVERSE_WINDING.nativeInt);
    }

    public boolean isEmpty() {
        return nIsEmpty(this.mNativePath);
    }

    public boolean isRect(RectF rect) {
        return nIsRect(this.mNativePath, rect);
    }

    public void computeBounds(RectF bounds, boolean exact) {
        nComputeBounds(this.mNativePath, bounds);
    }

    public void incReserve(int extraPtCount) {
        nIncReserve(this.mNativePath, extraPtCount);
    }

    public void moveTo(float x, float y) {
        nMoveTo(this.mNativePath, x, y);
    }

    public void rMoveTo(float dx, float dy) {
        nRMoveTo(this.mNativePath, dx, dy);
    }

    public void lineTo(float x, float y) {
        nLineTo(this.mNativePath, x, y);
    }

    public void rLineTo(float dx, float dy) {
        nRLineTo(this.mNativePath, dx, dy);
    }

    public void quadTo(float x1, float y1, float x2, float y2) {
        nQuadTo(this.mNativePath, x1, y1, x2, y2);
    }

    public void rQuadTo(float dx1, float dy1, float dx2, float dy2) {
        nRQuadTo(this.mNativePath, dx1, dy1, dx2, dy2);
    }

    public void conicTo(float x1, float y1, float x2, float y2, float weight) {
        nConicTo(this.mNativePath, x1, y1, x2, y2, weight);
    }

    public void rConicTo(float dx1, float dy1, float dx2, float dy2, float weight) {
        nRConicTo(this.mNativePath, dx1, dy1, dx2, dy2, weight);
    }

    public void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        nCubicTo(this.mNativePath, x1, y1, x2, y2, x3, y3);
    }

    public void rCubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        nRCubicTo(this.mNativePath, x1, y1, x2, y2, x3, y3);
    }

    public void arcTo(RectF oval, float startAngle, float sweepAngle, boolean forceMoveTo) {
        arcTo(oval.left, oval.top, oval.right, oval.bottom, startAngle, sweepAngle, forceMoveTo);
    }

    public void arcTo(RectF oval, float startAngle, float sweepAngle) {
        arcTo(oval.left, oval.top, oval.right, oval.bottom, startAngle, sweepAngle, false);
    }

    public void arcTo(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean forceMoveTo) {
        nArcTo(this.mNativePath, left, top, right, bottom, startAngle, sweepAngle, forceMoveTo);
    }

    public void close() {
        nClose(this.mNativePath);
    }

    /* loaded from: classes.dex */
    public enum Direction {
        CW(0),
        CCW(1);
        
        final int nativeInt;

        Direction(int ni) {
            this.nativeInt = ni;
        }
    }

    public void addRect(RectF rect, Direction dir) {
        addRect(rect.left, rect.top, rect.right, rect.bottom, dir);
    }

    public void addRect(float left, float top, float right, float bottom, Direction dir) {
        nAddRect(this.mNativePath, left, top, right, bottom, dir.nativeInt);
    }

    public void addOval(RectF oval, Direction dir) {
        addOval(oval.left, oval.top, oval.right, oval.bottom, dir);
    }

    public void addOval(float left, float top, float right, float bottom, Direction dir) {
        nAddOval(this.mNativePath, left, top, right, bottom, dir.nativeInt);
    }

    public void addCircle(float x, float y, float radius, Direction dir) {
        nAddCircle(this.mNativePath, x, y, radius, dir.nativeInt);
    }

    public void addArc(RectF oval, float startAngle, float sweepAngle) {
        addArc(oval.left, oval.top, oval.right, oval.bottom, startAngle, sweepAngle);
    }

    public void addArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle) {
        nAddArc(this.mNativePath, left, top, right, bottom, startAngle, sweepAngle);
    }

    public void addRoundRect(RectF rect, float rx, float ry, Direction dir) {
        addRoundRect(rect.left, rect.top, rect.right, rect.bottom, rx, ry, dir);
    }

    public void addRoundRect(float left, float top, float right, float bottom, float rx, float ry, Direction dir) {
        nAddRoundRect(this.mNativePath, left, top, right, bottom, rx, ry, dir.nativeInt);
    }

    public void addRoundRect(RectF rect, float[] radii, Direction dir) {
        if (rect == null) {
            throw new NullPointerException("need rect parameter");
        }
        addRoundRect(rect.left, rect.top, rect.right, rect.bottom, radii, dir);
    }

    public void addRoundRect(float left, float top, float right, float bottom, float[] radii, Direction dir) {
        if (radii.length < 8) {
            throw new ArrayIndexOutOfBoundsException("radii[] needs 8 values");
        }
        nAddRoundRect(this.mNativePath, left, top, right, bottom, radii, dir.nativeInt);
    }

    public void addPath(Path src, float dx, float dy) {
        nAddPath(this.mNativePath, src.mNativePath, dx, dy);
    }

    public void addPath(Path src) {
        nAddPath(this.mNativePath, src.mNativePath);
    }

    public void addPath(Path src, Matrix matrix) {
        nAddPath(this.mNativePath, src.mNativePath, matrix.m184ni());
    }

    public void offset(float dx, float dy, Path dst) {
        if (dst != null) {
            dst.set(this);
        } else {
            dst = this;
        }
        dst.offset(dx, dy);
    }

    public void offset(float dx, float dy) {
        nOffset(this.mNativePath, dx, dy);
    }

    public void setLastPoint(float dx, float dy) {
        nSetLastPoint(this.mNativePath, dx, dy);
    }

    public void transform(Matrix matrix, Path dst) {
        nTransform(this.mNativePath, matrix.m184ni(), dst != null ? dst.mNativePath : 0L);
    }

    public void transform(Matrix matrix) {
        nTransform(this.mNativePath, matrix.m184ni());
    }

    public final long readOnlyNI() {
        return this.mNativePath;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long mutateNI() {
        return this.mNativePath;
    }

    public float[] approximate(float acceptableError) {
        if (acceptableError < 0.0f) {
            throw new IllegalArgumentException("AcceptableError must be greater than or equal to 0");
        }
        return nApproximate(this.mNativePath, acceptableError);
    }

    public int getGenerationId() {
        return nGetGenerationID(this.mNativePath);
    }

    public boolean isInterpolatable(Path otherPath) {
        return nIsInterpolatable(this.mNativePath, otherPath.mNativePath);
    }

    public boolean interpolate(Path otherPath, float t, Path interpolatedPath) {
        return nInterpolate(this.mNativePath, otherPath.mNativePath, t, interpolatedPath.mNativePath);
    }
}
