package android.graphics;

import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import java.io.PrintWriter;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public class Matrix {
    public static final Matrix IDENTITY_MATRIX = new Matrix() { // from class: android.graphics.Matrix.1
        void oops() {
            throw new IllegalStateException("Matrix can not be modified");
        }

        @Override // android.graphics.Matrix
        public void set(Matrix src) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void reset() {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setTranslate(float dx, float dy) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setScale(float sx, float sy, float px, float py) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setScale(float sx, float sy) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setRotate(float degrees, float px, float py) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setRotate(float degrees) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setSinCos(float sinValue, float cosValue, float px, float py) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setSinCos(float sinValue, float cosValue) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setSkew(float kx, float ky, float px, float py) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setSkew(float kx, float ky) {
            oops();
        }

        @Override // android.graphics.Matrix
        public boolean setConcat(Matrix a, Matrix b) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preTranslate(float dx, float dy) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preScale(float sx, float sy, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preScale(float sx, float sy) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preRotate(float degrees, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preRotate(float degrees) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preSkew(float kx, float ky, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preSkew(float kx, float ky) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preConcat(Matrix other) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postTranslate(float dx, float dy) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postScale(float sx, float sy, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postScale(float sx, float sy) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postRotate(float degrees, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postRotate(float degrees) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postSkew(float kx, float ky, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postSkew(float kx, float ky) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postConcat(Matrix other) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean setRectToRect(RectF src, RectF dst, ScaleToFit stf) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean setPolyToPoly(float[] src, int srcIndex, float[] dst, int dstIndex, int pointCount) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public void setValues(float[] values) {
            oops();
        }
    };
    public static final int MPERSP_0 = 6;
    public static final int MPERSP_1 = 7;
    public static final int MPERSP_2 = 8;
    public static final int MSCALE_X = 0;
    public static final int MSCALE_Y = 4;
    public static final int MSKEW_X = 1;
    public static final int MSKEW_Y = 3;
    public static final int MTRANS_X = 2;
    public static final int MTRANS_Y = 5;
    private final long native_instance;

    private static native long nCreate(long j);

    @CriticalNative
    private static native boolean nEquals(long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nGetNativeFinalizer();

    @FastNative
    private static native void nGetValues(long j, float[] fArr);

    @CriticalNative
    private static native boolean nInvert(long j, long j2);

    @CriticalNative
    private static native boolean nIsAffine(long j);

    @CriticalNative
    private static native boolean nIsIdentity(long j);

    @FastNative
    private static native void nMapPoints(long j, float[] fArr, int i, float[] fArr2, int i2, int i3, boolean z);

    @CriticalNative
    private static native float nMapRadius(long j, float f);

    @FastNative
    private static native boolean nMapRect(long j, RectF rectF, RectF rectF2);

    @CriticalNative
    private static native void nPostConcat(long j, long j2);

    @CriticalNative
    private static native void nPostRotate(long j, float f);

    @CriticalNative
    private static native void nPostRotate(long j, float f, float f2, float f3);

    @CriticalNative
    private static native void nPostScale(long j, float f, float f2);

    @CriticalNative
    private static native void nPostScale(long j, float f, float f2, float f3, float f4);

    @CriticalNative
    private static native void nPostSkew(long j, float f, float f2);

    @CriticalNative
    private static native void nPostSkew(long j, float f, float f2, float f3, float f4);

    @CriticalNative
    private static native void nPostTranslate(long j, float f, float f2);

    @CriticalNative
    private static native void nPreConcat(long j, long j2);

    @CriticalNative
    private static native void nPreRotate(long j, float f);

    @CriticalNative
    private static native void nPreRotate(long j, float f, float f2, float f3);

    @CriticalNative
    private static native void nPreScale(long j, float f, float f2);

    @CriticalNative
    private static native void nPreScale(long j, float f, float f2, float f3, float f4);

    @CriticalNative
    private static native void nPreSkew(long j, float f, float f2);

    @CriticalNative
    private static native void nPreSkew(long j, float f, float f2, float f3, float f4);

    @CriticalNative
    private static native void nPreTranslate(long j, float f, float f2);

    @CriticalNative
    private static native boolean nRectStaysRect(long j);

    @CriticalNative
    private static native void nReset(long j);

    @CriticalNative
    private static native void nSet(long j, long j2);

    @CriticalNative
    private static native void nSetConcat(long j, long j2, long j3);

    @FastNative
    private static native boolean nSetPolyToPoly(long j, float[] fArr, int i, float[] fArr2, int i2, int i3);

    @FastNative
    private static native boolean nSetRectToRect(long j, RectF rectF, RectF rectF2, int i);

    @CriticalNative
    private static native void nSetRotate(long j, float f);

    @CriticalNative
    private static native void nSetRotate(long j, float f, float f2, float f3);

    @CriticalNative
    private static native void nSetScale(long j, float f, float f2);

    @CriticalNative
    private static native void nSetScale(long j, float f, float f2, float f3, float f4);

    @CriticalNative
    private static native void nSetSinCos(long j, float f, float f2);

    @CriticalNative
    private static native void nSetSinCos(long j, float f, float f2, float f3, float f4);

    @CriticalNative
    private static native void nSetSkew(long j, float f, float f2);

    @CriticalNative
    private static native void nSetSkew(long j, float f, float f2, float f3, float f4);

    @CriticalNative
    private static native void nSetTranslate(long j, float f, float f2);

    @FastNative
    private static native void nSetValues(long j, float[] fArr);

    /* loaded from: classes.dex */
    private static class NoImagePreloadHolder {
        public static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(Matrix.class.getClassLoader(), Matrix.nGetNativeFinalizer());

        private NoImagePreloadHolder() {
        }
    }

    public Matrix() {
        long nCreate = nCreate(0L);
        this.native_instance = nCreate;
        NoImagePreloadHolder.sRegistry.registerNativeAllocation(this, nCreate);
    }

    public Matrix(Matrix src) {
        long nCreate = nCreate(src != null ? src.native_instance : 0L);
        this.native_instance = nCreate;
        NoImagePreloadHolder.sRegistry.registerNativeAllocation(this, nCreate);
    }

    public boolean isIdentity() {
        return nIsIdentity(this.native_instance);
    }

    public boolean isAffine() {
        return nIsAffine(this.native_instance);
    }

    public boolean rectStaysRect() {
        return nRectStaysRect(this.native_instance);
    }

    public void set(Matrix src) {
        if (src == null) {
            reset();
        } else {
            nSet(this.native_instance, src.native_instance);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Matrix)) {
            return false;
        }
        return nEquals(this.native_instance, ((Matrix) obj).native_instance);
    }

    public int hashCode() {
        return 44;
    }

    public void reset() {
        nReset(this.native_instance);
    }

    public void setTranslate(float dx, float dy) {
        nSetTranslate(this.native_instance, dx, dy);
    }

    public void setScale(float sx, float sy, float px, float py) {
        nSetScale(this.native_instance, sx, sy, px, py);
    }

    public void setScale(float sx, float sy) {
        nSetScale(this.native_instance, sx, sy);
    }

    public void setRotate(float degrees, float px, float py) {
        nSetRotate(this.native_instance, degrees, px, py);
    }

    public void setRotate(float degrees) {
        nSetRotate(this.native_instance, degrees);
    }

    public void setSinCos(float sinValue, float cosValue, float px, float py) {
        nSetSinCos(this.native_instance, sinValue, cosValue, px, py);
    }

    public void setSinCos(float sinValue, float cosValue) {
        nSetSinCos(this.native_instance, sinValue, cosValue);
    }

    public void setSkew(float kx, float ky, float px, float py) {
        nSetSkew(this.native_instance, kx, ky, px, py);
    }

    public void setSkew(float kx, float ky) {
        nSetSkew(this.native_instance, kx, ky);
    }

    public boolean setConcat(Matrix a, Matrix b) {
        nSetConcat(this.native_instance, a.native_instance, b.native_instance);
        return true;
    }

    public boolean preTranslate(float dx, float dy) {
        nPreTranslate(this.native_instance, dx, dy);
        return true;
    }

    public boolean preScale(float sx, float sy, float px, float py) {
        nPreScale(this.native_instance, sx, sy, px, py);
        return true;
    }

    public boolean preScale(float sx, float sy) {
        nPreScale(this.native_instance, sx, sy);
        return true;
    }

    public boolean preRotate(float degrees, float px, float py) {
        nPreRotate(this.native_instance, degrees, px, py);
        return true;
    }

    public boolean preRotate(float degrees) {
        nPreRotate(this.native_instance, degrees);
        return true;
    }

    public boolean preSkew(float kx, float ky, float px, float py) {
        nPreSkew(this.native_instance, kx, ky, px, py);
        return true;
    }

    public boolean preSkew(float kx, float ky) {
        nPreSkew(this.native_instance, kx, ky);
        return true;
    }

    public boolean preConcat(Matrix other) {
        nPreConcat(this.native_instance, other.native_instance);
        return true;
    }

    public boolean postTranslate(float dx, float dy) {
        nPostTranslate(this.native_instance, dx, dy);
        return true;
    }

    public boolean postScale(float sx, float sy, float px, float py) {
        nPostScale(this.native_instance, sx, sy, px, py);
        return true;
    }

    public boolean postScale(float sx, float sy) {
        nPostScale(this.native_instance, sx, sy);
        return true;
    }

    public boolean postRotate(float degrees, float px, float py) {
        nPostRotate(this.native_instance, degrees, px, py);
        return true;
    }

    public boolean postRotate(float degrees) {
        nPostRotate(this.native_instance, degrees);
        return true;
    }

    public boolean postSkew(float kx, float ky, float px, float py) {
        nPostSkew(this.native_instance, kx, ky, px, py);
        return true;
    }

    public boolean postSkew(float kx, float ky) {
        nPostSkew(this.native_instance, kx, ky);
        return true;
    }

    public boolean postConcat(Matrix other) {
        nPostConcat(this.native_instance, other.native_instance);
        return true;
    }

    /* loaded from: classes.dex */
    public enum ScaleToFit {
        FILL(0),
        START(1),
        CENTER(2),
        END(3);
        
        final int nativeInt;

        ScaleToFit(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public boolean setRectToRect(RectF src, RectF dst, ScaleToFit stf) {
        if (dst == null || src == null) {
            throw new NullPointerException();
        }
        return nSetRectToRect(this.native_instance, src, dst, stf.nativeInt);
    }

    private static void checkPointArrays(float[] src, int srcIndex, float[] dst, int dstIndex, int pointCount) {
        int srcStop = (pointCount << 1) + srcIndex;
        int dstStop = (pointCount << 1) + dstIndex;
        if ((pointCount | srcIndex | dstIndex | srcStop | dstStop) < 0 || srcStop > src.length || dstStop > dst.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public boolean setPolyToPoly(float[] src, int srcIndex, float[] dst, int dstIndex, int pointCount) {
        if (pointCount > 4) {
            throw new IllegalArgumentException();
        }
        checkPointArrays(src, srcIndex, dst, dstIndex, pointCount);
        return nSetPolyToPoly(this.native_instance, src, srcIndex, dst, dstIndex, pointCount);
    }

    public boolean invert(Matrix inverse) {
        return nInvert(this.native_instance, inverse.native_instance);
    }

    public void mapPoints(float[] dst, int dstIndex, float[] src, int srcIndex, int pointCount) {
        checkPointArrays(src, srcIndex, dst, dstIndex, pointCount);
        nMapPoints(this.native_instance, dst, dstIndex, src, srcIndex, pointCount, true);
    }

    public void mapVectors(float[] dst, int dstIndex, float[] src, int srcIndex, int vectorCount) {
        checkPointArrays(src, srcIndex, dst, dstIndex, vectorCount);
        nMapPoints(this.native_instance, dst, dstIndex, src, srcIndex, vectorCount, false);
    }

    public void mapPoints(float[] dst, float[] src) {
        if (dst.length != src.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        mapPoints(dst, 0, src, 0, dst.length >> 1);
    }

    public void mapVectors(float[] dst, float[] src) {
        if (dst.length != src.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        mapVectors(dst, 0, src, 0, dst.length >> 1);
    }

    public void mapPoints(float[] pts) {
        mapPoints(pts, 0, pts, 0, pts.length >> 1);
    }

    public void mapVectors(float[] vecs) {
        mapVectors(vecs, 0, vecs, 0, vecs.length >> 1);
    }

    public boolean mapRect(RectF dst, RectF src) {
        if (dst == null || src == null) {
            throw new NullPointerException();
        }
        return nMapRect(this.native_instance, dst, src);
    }

    public boolean mapRect(RectF rect) {
        return mapRect(rect, rect);
    }

    public float mapRadius(float radius) {
        return nMapRadius(this.native_instance, radius);
    }

    public void getValues(float[] values) {
        if (values.length < 9) {
            throw new ArrayIndexOutOfBoundsException();
        }
        nGetValues(this.native_instance, values);
    }

    public void setValues(float[] values) {
        if (values.length < 9) {
            throw new ArrayIndexOutOfBoundsException();
        }
        nSetValues(this.native_instance, values);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("Matrix{");
        toShortString(sb);
        sb.append('}');
        return sb.toString();
    }

    public String toShortString() {
        StringBuilder sb = new StringBuilder(64);
        toShortString(sb);
        return sb.toString();
    }

    private void toShortString(StringBuilder sb) {
        float[] values = new float[9];
        getValues(values);
        sb.append('[');
        sb.append(values[0]);
        sb.append(", ");
        sb.append(values[1]);
        sb.append(", ");
        sb.append(values[2]);
        sb.append("][");
        sb.append(values[3]);
        sb.append(", ");
        sb.append(values[4]);
        sb.append(", ");
        sb.append(values[5]);
        sb.append("][");
        sb.append(values[6]);
        sb.append(", ");
        sb.append(values[7]);
        sb.append(", ");
        sb.append(values[8]);
        sb.append(']');
    }

    public final void dump(PrintWriter pw) {
        float[] values = new float[9];
        getValues(values);
        pw.print('[');
        pw.print(values[0]);
        pw.print(", ");
        pw.print(values[1]);
        pw.print(", ");
        pw.print(values[2]);
        pw.print("][");
        pw.print(values[3]);
        pw.print(", ");
        pw.print(values[4]);
        pw.print(", ");
        pw.print(values[5]);
        pw.print("][");
        pw.print(values[6]);
        pw.print(", ");
        pw.print(values[7]);
        pw.print(", ");
        pw.print(values[8]);
        pw.print(']');
    }

    /* renamed from: ni */
    public final long m184ni() {
        return this.native_instance;
    }
}
