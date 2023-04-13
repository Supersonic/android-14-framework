package android.graphics.drawable;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.graphics.drawable.Animatable2;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.android.internal.C4057R;
import dalvik.annotation.optimization.FastNative;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import libcore.util.NativeAllocationRegistry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AnimatedImageDrawable extends Drawable implements Animatable2 {
    private static final int FINISHED = -1;
    @Deprecated
    public static final int LOOP_INFINITE = -1;
    public static final int REPEAT_INFINITE = -1;
    private static final int REPEAT_UNDEFINED = -2;
    private ArrayList<Animatable2.AnimationCallback> mAnimationCallbacks;
    private ColorFilter mColorFilter;
    private Handler mHandler;
    private int mIntrinsicHeight;
    private int mIntrinsicWidth;
    private Runnable mRunnable;
    private boolean mStarting;
    private State mState;

    private static native long nCreate(long j, ImageDecoder imageDecoder, int i, int i2, long j2, boolean z, Rect rect) throws IOException;

    private static native long nDraw(long j, long j2);

    @FastNative
    private static native int nGetAlpha(long j);

    @FastNative
    private static native long nGetNativeFinalizer();

    @FastNative
    private static native int nGetRepeatCount(long j);

    @FastNative
    private static native boolean nIsRunning(long j);

    @FastNative
    private static native long nNativeByteSize(long j);

    @FastNative
    private static native void nSetAlpha(long j, int i);

    @FastNative
    private static native void nSetBounds(long j, Rect rect);

    @FastNative
    private static native void nSetColorFilter(long j, long j2);

    @FastNative
    private static native void nSetMirrored(long j, boolean z);

    private static native void nSetOnAnimationEndListener(long j, WeakReference<AnimatedImageDrawable> weakReference);

    @FastNative
    private static native void nSetRepeatCount(long j, int i);

    @FastNative
    private static native boolean nStart(long j);

    @FastNative
    private static native boolean nStop(long j);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class State {
        private final AssetFileDescriptor mAssetFd;
        private final InputStream mInputStream;
        final long mNativePtr;
        int[] mThemeAttrs = null;
        boolean mAutoMirrored = false;
        int mRepeatCount = -2;

        State(long nativePtr, InputStream is, AssetFileDescriptor afd) {
            this.mNativePtr = nativePtr;
            this.mInputStream = is;
            this.mAssetFd = afd;
        }
    }

    public void setRepeatCount(int repeatCount) {
        if (repeatCount < -1) {
            throw new IllegalArgumentException("invalid value passed to setRepeatCount" + repeatCount);
        }
        if (this.mState.mRepeatCount != repeatCount) {
            this.mState.mRepeatCount = repeatCount;
            if (this.mState.mNativePtr != 0) {
                nSetRepeatCount(this.mState.mNativePtr, repeatCount);
            }
        }
    }

    @Deprecated
    public void setLoopCount(int loopCount) {
        setRepeatCount(loopCount);
    }

    public int getRepeatCount() {
        if (this.mState.mNativePtr == 0) {
            throw new IllegalStateException("called getRepeatCount on empty AnimatedImageDrawable");
        }
        if (this.mState.mRepeatCount == -2) {
            State state = this.mState;
            state.mRepeatCount = nGetRepeatCount(state.mNativePtr);
        }
        return this.mState.mRepeatCount;
    }

    @Deprecated
    public int getLoopCount(int loopCount) {
        return getRepeatCount();
    }

    public AnimatedImageDrawable() {
        this.mAnimationCallbacks = null;
        this.mState = new State(0L, null, null);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.AnimatedImageDrawable);
        updateStateFromTypedArray(a, this.mSrcDensityOverride);
    }

    private void updateStateFromTypedArray(TypedArray a, int srcDensityOverride) throws XmlPullParserException {
        State oldState = this.mState;
        Resources r = a.getResources();
        int srcResId = a.getResourceId(0, 0);
        if (srcResId != 0) {
            TypedValue value = new TypedValue();
            r.getValueForDensity(srcResId, srcDensityOverride, value, true);
            if (srcDensityOverride > 0 && value.density > 0 && value.density != 65535) {
                if (value.density == srcDensityOverride) {
                    value.density = r.getDisplayMetrics().densityDpi;
                } else {
                    value.density = (value.density * r.getDisplayMetrics().densityDpi) / srcDensityOverride;
                }
            }
            int density = 0;
            if (value.density != 0) {
                if (value.density != 65535) {
                    density = value.density;
                }
            } else {
                density = 160;
            }
            try {
                InputStream is = r.openRawResource(srcResId, value);
                ImageDecoder.Source source = ImageDecoder.createSource(r, is, density);
                Drawable drawable = ImageDecoder.decodeDrawable(source, new ImageDecoder.OnHeaderDecodedListener() { // from class: android.graphics.drawable.AnimatedImageDrawable$$ExternalSyntheticLambda1
                    @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source2) {
                        AnimatedImageDrawable.lambda$updateStateFromTypedArray$0(imageDecoder, imageInfo, source2);
                    }
                });
                if (!(drawable instanceof AnimatedImageDrawable)) {
                    throw new XmlPullParserException(a.getPositionDescription() + ": <animated-image> did not decode animated");
                }
                int repeatCount = this.mState.mRepeatCount;
                AnimatedImageDrawable other = (AnimatedImageDrawable) drawable;
                this.mState = other.mState;
                other.mState = null;
                this.mIntrinsicWidth = other.mIntrinsicWidth;
                this.mIntrinsicHeight = other.mIntrinsicHeight;
                if (repeatCount != -2) {
                    setRepeatCount(repeatCount);
                }
            } catch (IOException e) {
                throw new XmlPullParserException(a.getPositionDescription() + ": <animated-image> requires a valid 'src' attribute", null, e);
            }
        }
        this.mState.mThemeAttrs = a.extractThemeAttrs();
        if (this.mState.mNativePtr == 0 && (this.mState.mThemeAttrs == null || this.mState.mThemeAttrs[0] == 0)) {
            throw new XmlPullParserException(a.getPositionDescription() + ": <animated-image> requires a valid 'src' attribute");
        }
        this.mState.mAutoMirrored = a.getBoolean(3, oldState.mAutoMirrored);
        int repeatCount2 = a.getInt(1, -2);
        if (repeatCount2 != -2) {
            setRepeatCount(repeatCount2);
        }
        boolean autoStart = a.getBoolean(2, false);
        if (autoStart && this.mState.mNativePtr != 0) {
            start();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$updateStateFromTypedArray$0(ImageDecoder decoder, ImageDecoder.ImageInfo info, ImageDecoder.Source src) {
        if (!info.isAnimated()) {
            throw new IllegalArgumentException("image is not animated");
        }
    }

    public AnimatedImageDrawable(long nativeImageDecoder, ImageDecoder decoder, int width, int height, long colorSpaceHandle, boolean extended, int srcDensity, int dstDensity, Rect cropRect, InputStream inputStream, AssetFileDescriptor afd) throws IOException {
        this.mAnimationCallbacks = null;
        int width2 = Bitmap.scaleFromDensity(width, srcDensity, dstDensity);
        int height2 = Bitmap.scaleFromDensity(height, srcDensity, dstDensity);
        if (cropRect == null) {
            this.mIntrinsicWidth = width2;
            this.mIntrinsicHeight = height2;
        } else {
            cropRect.set(Bitmap.scaleFromDensity(cropRect.left, srcDensity, dstDensity), Bitmap.scaleFromDensity(cropRect.top, srcDensity, dstDensity), Bitmap.scaleFromDensity(cropRect.right, srcDensity, dstDensity), Bitmap.scaleFromDensity(cropRect.bottom, srcDensity, dstDensity));
            this.mIntrinsicWidth = cropRect.width();
            this.mIntrinsicHeight = cropRect.height();
        }
        State state = new State(nCreate(nativeImageDecoder, decoder, width2, height2, colorSpaceHandle, extended, cropRect), inputStream, afd);
        this.mState = state;
        long nativeSize = nNativeByteSize(state.mNativePtr);
        NativeAllocationRegistry registry = NativeAllocationRegistry.createMalloced(AnimatedImageDrawable.class.getClassLoader(), nGetNativeFinalizer(), nativeSize);
        State state2 = this.mState;
        registry.registerNativeAllocation(state2, state2.mNativePtr);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mState.mNativePtr == 0) {
            throw new IllegalStateException("called draw on empty AnimatedImageDrawable");
        }
        if (this.mStarting) {
            this.mStarting = false;
            postOnAnimationStart();
        }
        long nextUpdate = nDraw(this.mState.mNativePtr, canvas.getNativeCanvasWrapper());
        if (nextUpdate > 0) {
            if (this.mRunnable == null) {
                this.mRunnable = new Runnable() { // from class: android.graphics.drawable.AnimatedImageDrawable$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        AnimatedImageDrawable.this.invalidateSelf();
                    }
                };
            }
            scheduleSelf(this.mRunnable, SystemClock.uptimeMillis() + nextUpdate);
        } else if (nextUpdate == -1) {
            postOnAnimationEnd();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("Alpha must be between 0 and 255! provided " + alpha);
        }
        if (this.mState.mNativePtr == 0) {
            throw new IllegalStateException("called setAlpha on empty AnimatedImageDrawable");
        }
        nSetAlpha(this.mState.mNativePtr, alpha);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        if (this.mState.mNativePtr == 0) {
            throw new IllegalStateException("called getAlpha on empty AnimatedImageDrawable");
        }
        return nGetAlpha(this.mState.mNativePtr);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        if (this.mState.mNativePtr == 0) {
            throw new IllegalStateException("called setColorFilter on empty AnimatedImageDrawable");
        }
        if (colorFilter != this.mColorFilter) {
            this.mColorFilter = colorFilter;
            long nativeFilter = colorFilter != null ? colorFilter.getNativeInstance() : 0L;
            nSetColorFilter(this.mState.mNativePtr, nativeFilter);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean mirrored) {
        if (this.mState.mAutoMirrored != mirrored) {
            this.mState.mAutoMirrored = mirrored;
            if (getLayoutDirection() == 1 && this.mState.mNativePtr != 0) {
                nSetMirrored(this.mState.mNativePtr, mirrored);
                invalidateSelf();
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean onLayoutDirectionChanged(int layoutDirection) {
        if (!this.mState.mAutoMirrored || this.mState.mNativePtr == 0) {
            return false;
        }
        boolean mirror = layoutDirection == 1;
        nSetMirrored(this.mState.mNativePtr, mirror);
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isAutoMirrored() {
        return this.mState.mAutoMirrored;
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        if (this.mState.mNativePtr == 0) {
            throw new IllegalStateException("called isRunning on empty AnimatedImageDrawable");
        }
        return nIsRunning(this.mState.mNativePtr);
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        if (this.mState.mNativePtr == 0) {
            throw new IllegalStateException("called start on empty AnimatedImageDrawable");
        }
        if (nStart(this.mState.mNativePtr)) {
            this.mStarting = true;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        if (this.mState.mNativePtr == 0) {
            throw new IllegalStateException("called stop on empty AnimatedImageDrawable");
        }
        if (nStop(this.mState.mNativePtr)) {
            postOnAnimationEnd();
        }
    }

    @Override // android.graphics.drawable.Animatable2
    public void registerAnimationCallback(Animatable2.AnimationCallback callback) {
        if (callback == null) {
            return;
        }
        if (this.mAnimationCallbacks == null) {
            this.mAnimationCallbacks = new ArrayList<>();
            nSetOnAnimationEndListener(this.mState.mNativePtr, new WeakReference(this));
        }
        if (!this.mAnimationCallbacks.contains(callback)) {
            this.mAnimationCallbacks.add(callback);
        }
    }

    @Override // android.graphics.drawable.Animatable2
    public boolean unregisterAnimationCallback(Animatable2.AnimationCallback callback) {
        ArrayList<Animatable2.AnimationCallback> arrayList;
        if (callback == null || (arrayList = this.mAnimationCallbacks) == null || !arrayList.remove(callback)) {
            return false;
        }
        if (this.mAnimationCallbacks.isEmpty()) {
            clearAnimationCallbacks();
            return true;
        }
        return true;
    }

    @Override // android.graphics.drawable.Animatable2
    public void clearAnimationCallbacks() {
        if (this.mAnimationCallbacks != null) {
            this.mAnimationCallbacks = null;
            nSetOnAnimationEndListener(this.mState.mNativePtr, null);
        }
    }

    private void postOnAnimationStart() {
        if (this.mAnimationCallbacks == null) {
            return;
        }
        getHandler().post(new Runnable() { // from class: android.graphics.drawable.AnimatedImageDrawable$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AnimatedImageDrawable.this.lambda$postOnAnimationStart$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$postOnAnimationStart$1() {
        Iterator<Animatable2.AnimationCallback> it = this.mAnimationCallbacks.iterator();
        while (it.hasNext()) {
            Animatable2.AnimationCallback callback = it.next();
            callback.onAnimationStart(this);
        }
    }

    private void postOnAnimationEnd() {
        if (this.mAnimationCallbacks == null) {
            return;
        }
        getHandler().post(new Runnable() { // from class: android.graphics.drawable.AnimatedImageDrawable$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AnimatedImageDrawable.this.lambda$postOnAnimationEnd$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$postOnAnimationEnd$2() {
        Iterator<Animatable2.AnimationCallback> it = this.mAnimationCallbacks.iterator();
        while (it.hasNext()) {
            Animatable2.AnimationCallback callback = it.next();
            callback.onAnimationEnd(this);
        }
    }

    private Handler getHandler() {
        if (this.mHandler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        }
        return this.mHandler;
    }

    private static void callOnAnimationEnd(WeakReference<AnimatedImageDrawable> weakDrawable) {
        AnimatedImageDrawable drawable = weakDrawable.get();
        if (drawable != null) {
            drawable.onAnimationEnd();
        }
    }

    private void onAnimationEnd() {
        ArrayList<Animatable2.AnimationCallback> arrayList = this.mAnimationCallbacks;
        if (arrayList != null) {
            Iterator<Animatable2.AnimationCallback> it = arrayList.iterator();
            while (it.hasNext()) {
                Animatable2.AnimationCallback callback = it.next();
                callback.onAnimationEnd(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        if (this.mState.mNativePtr != 0) {
            nSetBounds(this.mState.mNativePtr, bounds);
        }
    }
}
