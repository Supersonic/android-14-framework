package android.graphics;

import android.graphics.Shader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class BitmapShader extends Shader {
    public static final int FILTER_MODE_DEFAULT = 0;
    public static final int FILTER_MODE_LINEAR = 2;
    public static final int FILTER_MODE_NEAREST = 1;
    Bitmap mBitmap;
    private boolean mFilterFromPaint;
    private int mFilterMode;
    private boolean mIsDirectSampled;
    private int mMaxAniso;
    private boolean mRequestDirectSampling;
    private int mTileX;
    private int mTileY;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FilterMode {
    }

    private static native long nativeCreate(long j, long j2, int i, int i2, boolean z, boolean z2);

    private static native long nativeCreateWithMaxAniso(long j, long j2, int i, int i2, int i3, boolean z);

    public BitmapShader(Bitmap bitmap, Shader.TileMode tileX, Shader.TileMode tileY) {
        this(bitmap, tileX.nativeInt, tileY.nativeInt);
    }

    private BitmapShader(Bitmap bitmap, int tileX, int tileY) {
        this.mMaxAniso = 0;
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap must be non-null");
        }
        this.mBitmap = bitmap;
        this.mTileX = tileX;
        this.mTileY = tileY;
        this.mFilterMode = 0;
        this.mFilterFromPaint = false;
        this.mIsDirectSampled = false;
        this.mRequestDirectSampling = false;
    }

    public int getFilterMode() {
        return this.mFilterMode;
    }

    public void setFilterMode(int mode) {
        if (mode != this.mFilterMode) {
            this.mFilterMode = mode;
            this.mMaxAniso = 0;
            discardNativeInstance();
        }
    }

    public void setMaxAnisotropy(int maxAnisotropy) {
        if (this.mMaxAniso != maxAnisotropy && maxAnisotropy > 0) {
            this.mMaxAniso = maxAnisotropy;
            this.mFilterMode = 0;
            discardNativeInstance();
        }
    }

    public int getMaxAnisotropy() {
        return this.mMaxAniso;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized long getNativeInstanceWithDirectSampling() {
        this.mRequestDirectSampling = true;
        return getNativeInstance();
    }

    @Override // android.graphics.Shader
    protected long createNativeInstance(long nativeMatrix, boolean filterFromPaint) {
        int i = this.mFilterMode;
        boolean enableLinearFilter = i == 2;
        if (i == 0) {
            this.mFilterFromPaint = filterFromPaint;
            enableLinearFilter = this.mFilterFromPaint;
        }
        this.mIsDirectSampled = this.mRequestDirectSampling;
        this.mRequestDirectSampling = false;
        if (this.mMaxAniso > 0) {
            return nativeCreateWithMaxAniso(nativeMatrix, this.mBitmap.getNativeInstance(), this.mTileX, this.mTileY, this.mMaxAniso, this.mIsDirectSampled);
        }
        return nativeCreate(nativeMatrix, this.mBitmap.getNativeInstance(), this.mTileX, this.mTileY, enableLinearFilter, this.mIsDirectSampled);
    }

    @Override // android.graphics.Shader
    protected boolean shouldDiscardNativeInstance(boolean filterFromPaint) {
        return this.mIsDirectSampled != this.mRequestDirectSampling || (this.mFilterMode == 0 && this.mFilterFromPaint != filterFromPaint);
    }
}
