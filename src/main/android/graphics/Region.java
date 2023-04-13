package android.graphics;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Pools;
/* loaded from: classes.dex */
public class Region implements Parcelable {
    private static final int MAX_POOL_SIZE = 10;
    public long mNativeRegion;
    private static final Pools.SynchronizedPool<Region> sPool = new Pools.SynchronizedPool<>(10);
    public static final Parcelable.Creator<Region> CREATOR = new Parcelable.Creator<Region>() { // from class: android.graphics.Region.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Region createFromParcel(Parcel p) {
            long ni = Region.nativeCreateFromParcel(p);
            if (ni == 0) {
                throw new RuntimeException();
            }
            return new Region(ni);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };

    private static native long nativeConstructor();

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeCreateFromParcel(Parcel parcel);

    private static native void nativeDestructor(long j);

    private static native boolean nativeEquals(long j, long j2);

    private static native boolean nativeGetBoundaryPath(long j, long j2);

    private static native boolean nativeGetBounds(long j, Rect rect);

    private static native boolean nativeOp(long j, int i, int i2, int i3, int i4, int i5);

    private static native boolean nativeOp(long j, long j2, long j3, int i);

    private static native boolean nativeOp(long j, Rect rect, long j2, int i);

    private static native boolean nativeSetPath(long j, long j2, long j3);

    private static native boolean nativeSetRect(long j, int i, int i2, int i3, int i4);

    private static native void nativeSetRegion(long j, long j2);

    private static native String nativeToString(long j);

    private static native boolean nativeWriteToParcel(long j, Parcel parcel);

    public native boolean contains(int i, int i2);

    public native boolean isComplex();

    public native boolean isEmpty();

    public native boolean isRect();

    public native boolean quickContains(int i, int i2, int i3, int i4);

    public native boolean quickReject(int i, int i2, int i3, int i4);

    public native boolean quickReject(Region region);

    public native void scale(float f, Region region);

    public native void translate(int i, int i2, Region region);

    /* renamed from: android.graphics.Region$Op */
    /* loaded from: classes.dex */
    public enum EnumC0813Op {
        DIFFERENCE(0),
        INTERSECT(1),
        UNION(2),
        XOR(3),
        REVERSE_DIFFERENCE(4),
        REPLACE(5);
        
        public final int nativeInt;

        EnumC0813Op(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public Region() {
        this(nativeConstructor());
    }

    public Region(Region region) {
        this(nativeConstructor());
        nativeSetRegion(this.mNativeRegion, region.mNativeRegion);
    }

    public Region(Rect r) {
        long nativeConstructor = nativeConstructor();
        this.mNativeRegion = nativeConstructor;
        nativeSetRect(nativeConstructor, r.left, r.top, r.right, r.bottom);
    }

    public Region(int left, int top, int right, int bottom) {
        long nativeConstructor = nativeConstructor();
        this.mNativeRegion = nativeConstructor;
        nativeSetRect(nativeConstructor, left, top, right, bottom);
    }

    public void setEmpty() {
        nativeSetRect(this.mNativeRegion, 0, 0, 0, 0);
    }

    public boolean set(Region region) {
        nativeSetRegion(this.mNativeRegion, region.mNativeRegion);
        return true;
    }

    public boolean set(Rect r) {
        return nativeSetRect(this.mNativeRegion, r.left, r.top, r.right, r.bottom);
    }

    public boolean set(int left, int top, int right, int bottom) {
        return nativeSetRect(this.mNativeRegion, left, top, right, bottom);
    }

    public boolean setPath(Path path, Region clip) {
        return nativeSetPath(this.mNativeRegion, path.readOnlyNI(), clip.mNativeRegion);
    }

    public Rect getBounds() {
        Rect r = new Rect();
        nativeGetBounds(this.mNativeRegion, r);
        return r;
    }

    public boolean getBounds(Rect r) {
        if (r == null) {
            throw new NullPointerException();
        }
        return nativeGetBounds(this.mNativeRegion, r);
    }

    public Path getBoundaryPath() {
        Path path = new Path();
        nativeGetBoundaryPath(this.mNativeRegion, path.mutateNI());
        return path;
    }

    public boolean getBoundaryPath(Path path) {
        return nativeGetBoundaryPath(this.mNativeRegion, path.mutateNI());
    }

    public boolean quickContains(Rect r) {
        return quickContains(r.left, r.top, r.right, r.bottom);
    }

    public boolean quickReject(Rect r) {
        return quickReject(r.left, r.top, r.right, r.bottom);
    }

    public void translate(int dx, int dy) {
        translate(dx, dy, null);
    }

    public void scale(float scale) {
        scale(scale, null);
    }

    public final boolean union(Rect r) {
        return m179op(r, EnumC0813Op.UNION);
    }

    /* renamed from: op */
    public boolean m179op(Rect r, EnumC0813Op op) {
        return nativeOp(this.mNativeRegion, r.left, r.top, r.right, r.bottom, op.nativeInt);
    }

    /* renamed from: op */
    public boolean m180op(int left, int top, int right, int bottom, EnumC0813Op op) {
        return nativeOp(this.mNativeRegion, left, top, right, bottom, op.nativeInt);
    }

    /* renamed from: op */
    public boolean m177op(Region region, EnumC0813Op op) {
        return m176op(this, region, op);
    }

    /* renamed from: op */
    public boolean m178op(Rect rect, Region region, EnumC0813Op op) {
        return nativeOp(this.mNativeRegion, rect, region.mNativeRegion, op.nativeInt);
    }

    /* renamed from: op */
    public boolean m176op(Region region1, Region region2, EnumC0813Op op) {
        return nativeOp(this.mNativeRegion, region1.mNativeRegion, region2.mNativeRegion, op.nativeInt);
    }

    public String toString() {
        return nativeToString(this.mNativeRegion);
    }

    public static Region obtain() {
        Region region = sPool.acquire();
        return region != null ? region : new Region();
    }

    public static Region obtain(Region other) {
        Region region = obtain();
        region.set(other);
        return region;
    }

    public void recycle() {
        setEmpty();
        sPool.release(this);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel p, int flags) {
        if (!nativeWriteToParcel(this.mNativeRegion, p)) {
            throw new RuntimeException();
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Region)) {
            return false;
        }
        Region peer = (Region) obj;
        return nativeEquals(this.mNativeRegion, peer.mNativeRegion);
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeRegion);
            this.mNativeRegion = 0L;
        } finally {
            super.finalize();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Region(long ni) {
        if (ni == 0) {
            throw new RuntimeException();
        }
        this.mNativeRegion = ni;
    }

    private Region(long ni, int unused) {
        this(ni);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: ni */
    public final long m181ni() {
        return this.mNativeRegion;
    }
}
