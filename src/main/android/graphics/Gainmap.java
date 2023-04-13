package android.graphics;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public final class Gainmap implements Parcelable {
    public static final Parcelable.Creator<Gainmap> CREATOR = new Parcelable.Creator<Gainmap>() { // from class: android.graphics.Gainmap.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Gainmap createFromParcel(Parcel in) {
            Gainmap gm = new Gainmap((Bitmap) in.readTypedObject(Bitmap.CREATOR));
            Gainmap.nReadGainmapFromParcel(gm.mNativePtr, in);
            return gm;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Gainmap[] newArray(int size) {
            return new Gainmap[size];
        }
    };
    private Bitmap mGainmapContents;
    final long mNativePtr;

    private static native long nCreateEmpty();

    private static native float nGetDisplayRatioHdr(long j);

    private static native float nGetDisplayRatioSdr(long j);

    private static native void nGetEpsilonHdr(long j, float[] fArr);

    private static native void nGetEpsilonSdr(long j, float[] fArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nGetFinalizer();

    private static native void nGetGamma(long j, float[] fArr);

    private static native void nGetRatioMax(long j, float[] fArr);

    private static native void nGetRatioMin(long j, float[] fArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nReadGainmapFromParcel(long j, Parcel parcel);

    private static native void nSetBitmap(long j, Bitmap bitmap);

    private static native void nSetDisplayRatioHdr(long j, float f);

    private static native void nSetDisplayRatioSdr(long j, float f);

    private static native void nSetEpsilonHdr(long j, float f, float f2, float f3);

    private static native void nSetEpsilonSdr(long j, float f, float f2, float f3);

    private static native void nSetGamma(long j, float f, float f2, float f3);

    private static native void nSetRatioMax(long j, float f, float f2, float f3);

    private static native void nSetRatioMin(long j, float f, float f2, float f3);

    private static native void nWriteGainmapToParcel(long j, Parcel parcel);

    /* loaded from: classes.dex */
    private static class NoImagePreloadHolder {
        public static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(Gainmap.class.getClassLoader(), Gainmap.nGetFinalizer());

        private NoImagePreloadHolder() {
        }
    }

    private Gainmap(Bitmap gainmapContents, long nativeGainmap) {
        if (nativeGainmap == 0) {
            throw new RuntimeException("internal error: native gainmap is 0");
        }
        this.mNativePtr = nativeGainmap;
        setGainmapContents(gainmapContents);
        NoImagePreloadHolder.sRegistry.registerNativeAllocation(this, nativeGainmap);
    }

    public Gainmap(Bitmap gainmapContents) {
        this(gainmapContents, nCreateEmpty());
    }

    public Bitmap getGainmapContents() {
        return this.mGainmapContents;
    }

    public void setGainmapContents(Bitmap bitmap) {
        if (bitmap.isRecycled()) {
            throw new IllegalArgumentException("Bitmap is recycled");
        }
        nSetBitmap(this.mNativePtr, bitmap);
        this.mGainmapContents = bitmap;
    }

    public void setRatioMin(float r, float g, float b) {
        nSetRatioMin(this.mNativePtr, r, g, b);
    }

    public float[] getRatioMin() {
        float[] ret = new float[3];
        nGetRatioMin(this.mNativePtr, ret);
        return ret;
    }

    public void setRatioMax(float r, float g, float b) {
        nSetRatioMax(this.mNativePtr, r, g, b);
    }

    public float[] getRatioMax() {
        float[] ret = new float[3];
        nGetRatioMax(this.mNativePtr, ret);
        return ret;
    }

    public void setGamma(float r, float g, float b) {
        nSetGamma(this.mNativePtr, r, g, b);
    }

    public float[] getGamma() {
        float[] ret = new float[3];
        nGetGamma(this.mNativePtr, ret);
        return ret;
    }

    public void setEpsilonSdr(float r, float g, float b) {
        nSetEpsilonSdr(this.mNativePtr, r, g, b);
    }

    public float[] getEpsilonSdr() {
        float[] ret = new float[3];
        nGetEpsilonSdr(this.mNativePtr, ret);
        return ret;
    }

    public void setEpsilonHdr(float r, float g, float b) {
        nSetEpsilonHdr(this.mNativePtr, r, g, b);
    }

    public float[] getEpsilonHdr() {
        float[] ret = new float[3];
        nGetEpsilonHdr(this.mNativePtr, ret);
        return ret;
    }

    public void setDisplayRatioForFullHdr(float max) {
        if (!Float.isFinite(max) || max < 1.0f) {
            throw new IllegalArgumentException("setDisplayRatioForFullHdr must be >= 1.0f, got = " + max);
        }
        nSetDisplayRatioHdr(this.mNativePtr, max);
    }

    public float getDisplayRatioForFullHdr() {
        return nGetDisplayRatioHdr(this.mNativePtr);
    }

    public void setMinDisplayRatioForHdrTransition(float min) {
        if (!Float.isFinite(min) || min < 1.0f) {
            throw new IllegalArgumentException("setMinDisplayRatioForHdrTransition must be >= 1.0f, got = " + min);
        }
        nSetDisplayRatioSdr(this.mNativePtr, min);
    }

    public float getMinDisplayRatioForHdrTransition() {
        return nGetDisplayRatioSdr(this.mNativePtr);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.mNativePtr == 0) {
            throw new IllegalStateException("Cannot be written to a parcel");
        }
        dest.writeTypedObject(this.mGainmapContents, flags);
        nWriteGainmapToParcel(this.mNativePtr, dest);
    }
}
