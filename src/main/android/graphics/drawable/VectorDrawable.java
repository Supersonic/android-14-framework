package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.ComplexColor;
import android.content.res.GradientColor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.p008os.Trace;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Log;
import android.util.PathParser;
import android.util.Property;
import android.util.Xml;
import com.android.internal.C4057R;
import com.android.internal.util.VirtualRefBasePtr;
import dalvik.annotation.optimization.FastNative;
import dalvik.system.VMRuntime;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class VectorDrawable extends Drawable {
    private static final String LOGTAG = VectorDrawable.class.getSimpleName();
    private static final String SHAPE_CLIP_PATH = "clip-path";
    private static final String SHAPE_GROUP = "group";
    private static final String SHAPE_PATH = "path";
    private static final String SHAPE_VECTOR = "vector";
    private BlendModeColorFilter mBlendModeColorFilter;
    private ColorFilter mColorFilter;
    private boolean mDpiScaledDirty;
    private int mDpiScaledHeight;
    private Insets mDpiScaledInsets;
    private int mDpiScaledWidth;
    private boolean mMutated;
    private int mTargetDensity;
    private PorterDuffColorFilter mTintFilter;
    private final Rect mTmpBounds;
    private VectorDrawableState mVectorState;

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nAddChild(long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreateClipPath();

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreateClipPath(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreateFullPath();

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreateFullPath(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreateGroup();

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreateGroup(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreateTree(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreateTreeFromCopy(long j, long j2);

    private static native int nDraw(long j, long j2, long j3, Rect rect, boolean z, boolean z2);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetFillAlpha(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native int nGetFillColor(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean nGetFullPathProperties(long j, byte[] bArr, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean nGetGroupProperties(long j, float[] fArr, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetPivotX(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetPivotY(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetRootAlpha(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetRotation(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetScaleX(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetScaleY(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetStrokeAlpha(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native int nGetStrokeColor(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetStrokeWidth(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetTranslateX(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetTranslateY(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetTrimPathEnd(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetTrimPathOffset(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native float nGetTrimPathStart(long j);

    @FastNative
    private static native void nSetAllowCaching(long j, boolean z);

    @FastNative
    private static native void nSetAntiAlias(long j, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetFillAlpha(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetFillColor(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nSetName(long j, String str);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetPathData(long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nSetPathString(long j, String str, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetPivotX(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetPivotY(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetRendererViewportSize(long j, float f, float f2);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native boolean nSetRootAlpha(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetRotation(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetScaleX(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetScaleY(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetStrokeAlpha(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetStrokeColor(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetStrokeWidth(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetTranslateX(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetTranslateY(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetTrimPathEnd(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetTrimPathOffset(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nSetTrimPathStart(long j, float f);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nUpdateFullPathFillGradient(long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nUpdateFullPathProperties(long j, float f, int i, float f2, int i2, float f3, float f4, float f5, float f6, float f7, int i3, int i4, int i5);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nUpdateFullPathStrokeGradient(long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nUpdateGroupProperties(long j, float f, float f2, float f3, float f4, float f5, float f6, float f7);

    public VectorDrawable() {
        this(null, null);
    }

    private VectorDrawable(VectorDrawableState state, Resources res) {
        this.mDpiScaledWidth = 0;
        this.mDpiScaledHeight = 0;
        this.mDpiScaledInsets = Insets.NONE;
        this.mDpiScaledDirty = true;
        this.mTmpBounds = new Rect();
        this.mVectorState = new VectorDrawableState(state);
        updateLocalState(res);
    }

    private void updateLocalState(Resources res) {
        int density = Drawable.resolveDensity(res, this.mVectorState.mDensity);
        if (this.mTargetDensity != density) {
            this.mTargetDensity = density;
            this.mDpiScaledDirty = true;
        }
        updateColorFilters(this.mVectorState.mBlendMode, this.mVectorState.mTint);
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mVectorState = new VectorDrawableState(this.mVectorState);
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void clearMutated() {
        super.clearMutated();
        this.mMutated = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object getTargetByName(String name) {
        return this.mVectorState.mVGTargetsMap.get(name);
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        this.mVectorState.mChangingConfigurations = getChangingConfigurations();
        return this.mVectorState;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int deltaInBytes;
        copyBounds(this.mTmpBounds);
        if (this.mTmpBounds.width() <= 0 || this.mTmpBounds.height() <= 0) {
            return;
        }
        ColorFilter colorFilter = this.mColorFilter;
        if (colorFilter == null) {
            colorFilter = this.mBlendModeColorFilter;
        }
        long colorFilterNativeInstance = colorFilter == null ? 0L : colorFilter.getNativeInstance();
        boolean canReuseCache = this.mVectorState.canReuseCache();
        int pixelCount = nDraw(this.mVectorState.getNativeRenderer(), canvas.getNativeCanvasWrapper(), colorFilterNativeInstance, this.mTmpBounds, needMirroring(), canReuseCache);
        if (pixelCount == 0) {
            return;
        }
        if (canvas.isHardwareAccelerated()) {
            deltaInBytes = (pixelCount - this.mVectorState.mLastHWCachePixelCount) * 4;
            this.mVectorState.mLastHWCachePixelCount = pixelCount;
        } else {
            deltaInBytes = (pixelCount - this.mVectorState.mLastSWCachePixelCount) * 4;
            this.mVectorState.mLastSWCachePixelCount = pixelCount;
        }
        if (deltaInBytes > 0) {
            VMRuntime.getRuntime().registerNativeAllocation(deltaInBytes);
        } else if (deltaInBytes < 0) {
            VMRuntime.getRuntime().registerNativeFree(-deltaInBytes);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return (int) (this.mVectorState.getAlpha() * 255.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (this.mVectorState.setAlpha(alpha / 255.0f)) {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mColorFilter = colorFilter;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList tint) {
        VectorDrawableState state = this.mVectorState;
        if (state.mTint != tint) {
            state.mTint = tint;
            updateColorFilters(this.mVectorState.mBlendMode, tint);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintBlendMode(BlendMode blendMode) {
        VectorDrawableState state = this.mVectorState;
        if (state.mBlendMode != blendMode) {
            state.mBlendMode = blendMode;
            updateColorFilters(state.mBlendMode, state.mTint);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        VectorDrawableState vectorDrawableState;
        return super.isStateful() || ((vectorDrawableState = this.mVectorState) != null && vectorDrawableState.isStateful());
    }

    @Override // android.graphics.drawable.Drawable
    public boolean hasFocusStateSpecified() {
        VectorDrawableState vectorDrawableState = this.mVectorState;
        return vectorDrawableState != null && vectorDrawableState.hasFocusStateSpecified();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onStateChange(int[] stateSet) {
        boolean changed = false;
        if (isStateful()) {
            mutate();
        }
        VectorDrawableState state = this.mVectorState;
        if (state.onStateChange(stateSet)) {
            changed = true;
            state.mCacheDirty = true;
        }
        if (state.mTint != null && state.mBlendMode != null) {
            BlendMode blendMode = state.mBlendMode;
            ColorStateList tint = state.mTint;
            updateColorFilters(blendMode, tint);
            return true;
        }
        return changed;
    }

    private void updateColorFilters(BlendMode blendMode, ColorStateList tint) {
        PorterDuff.Mode mode = BlendMode.blendModeToPorterDuffMode(blendMode);
        this.mTintFilter = updateTintFilter(this.mTintFilter, tint, mode);
        this.mBlendModeColorFilter = updateBlendModeFilter(this.mBlendModeColorFilter, tint, blendMode);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return getAlpha() == 0 ? -2 : -3;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        if (this.mDpiScaledDirty) {
            computeVectorSize();
        }
        return this.mDpiScaledWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        if (this.mDpiScaledDirty) {
            computeVectorSize();
        }
        return this.mDpiScaledHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public Insets getOpticalInsets() {
        if (this.mDpiScaledDirty) {
            computeVectorSize();
        }
        return this.mDpiScaledInsets;
    }

    void computeVectorSize() {
        Insets opticalInsets = this.mVectorState.mOpticalInsets;
        int sourceDensity = this.mVectorState.mDensity;
        int targetDensity = this.mTargetDensity;
        if (targetDensity != sourceDensity) {
            this.mDpiScaledWidth = Drawable.scaleFromDensity(this.mVectorState.mBaseWidth, sourceDensity, targetDensity, true);
            this.mDpiScaledHeight = Drawable.scaleFromDensity(this.mVectorState.mBaseHeight, sourceDensity, targetDensity, true);
            int left = Drawable.scaleFromDensity(opticalInsets.left, sourceDensity, targetDensity, false);
            int right = Drawable.scaleFromDensity(opticalInsets.right, sourceDensity, targetDensity, false);
            int top = Drawable.scaleFromDensity(opticalInsets.top, sourceDensity, targetDensity, false);
            int bottom = Drawable.scaleFromDensity(opticalInsets.bottom, sourceDensity, targetDensity, false);
            this.mDpiScaledInsets = Insets.m186of(left, top, right, bottom);
        } else {
            this.mDpiScaledWidth = this.mVectorState.mBaseWidth;
            this.mDpiScaledHeight = this.mVectorState.mBaseHeight;
            this.mDpiScaledInsets = opticalInsets;
        }
        this.mDpiScaledDirty = false;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        VectorDrawableState vectorDrawableState = this.mVectorState;
        return (vectorDrawableState != null && vectorDrawableState.canApplyTheme()) || super.canApplyTheme();
    }

    @Override // android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme t) {
        super.applyTheme(t);
        VectorDrawableState state = this.mVectorState;
        if (state == null) {
            return;
        }
        boolean changedDensity = this.mVectorState.setDensity(Drawable.resolveDensity(t.getResources(), 0));
        this.mDpiScaledDirty |= changedDensity;
        if (state.mThemeAttrs != null) {
            TypedArray a = t.resolveAttributes(state.mThemeAttrs, C4057R.styleable.VectorDrawable);
            try {
                try {
                    state.mCacheDirty = true;
                    updateStateFromTypedArray(a);
                    a.recycle();
                    this.mDpiScaledDirty = true;
                } catch (XmlPullParserException e) {
                    throw new RuntimeException(e);
                }
            } catch (Throwable th) {
                a.recycle();
                throw th;
            }
        }
        if (state.mTint != null && state.mTint.canApplyTheme()) {
            state.mTint = state.mTint.obtainForTheme(t);
        }
        VectorDrawableState vectorDrawableState = this.mVectorState;
        if (vectorDrawableState != null && vectorDrawableState.canApplyTheme()) {
            this.mVectorState.applyTheme(t);
        }
        updateLocalState(t.getResources());
    }

    public float getPixelSize() {
        VectorDrawableState vectorDrawableState = this.mVectorState;
        if (vectorDrawableState == null || vectorDrawableState.mBaseWidth == 0 || this.mVectorState.mBaseHeight == 0 || this.mVectorState.mViewportHeight == 0.0f || this.mVectorState.mViewportWidth == 0.0f) {
            return 1.0f;
        }
        float intrinsicWidth = this.mVectorState.mBaseWidth;
        float intrinsicHeight = this.mVectorState.mBaseHeight;
        float viewportWidth = this.mVectorState.mViewportWidth;
        float viewportHeight = this.mVectorState.mViewportHeight;
        float scaleX = viewportWidth / intrinsicWidth;
        float scaleY = viewportHeight / intrinsicHeight;
        return Math.min(scaleX, scaleY);
    }

    public static VectorDrawable create(Resources resources, int rid) {
        int type;
        try {
            XmlPullParser parser = resources.getXml(rid);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            while (true) {
                type = parser.next();
                if (type == 2 || type == 1) {
                    break;
                }
            }
            if (type != 2) {
                throw new XmlPullParserException("No start tag found");
            }
            VectorDrawable drawable = new VectorDrawable();
            drawable.inflate(resources, parser, attrs);
            return drawable;
        } catch (IOException e) {
            Log.m109e(LOGTAG, "parser error", e);
            return null;
        } catch (XmlPullParserException e2) {
            Log.m109e(LOGTAG, "parser error", e2);
            return null;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        try {
            Trace.traceBegin(8192L, "VectorDrawable#inflate");
            if (this.mVectorState.mRootGroup != null || this.mVectorState.mNativeTree != null) {
                if (this.mVectorState.mRootGroup != null) {
                    VMRuntime.getRuntime().registerNativeFree(this.mVectorState.mRootGroup.getNativeSize());
                    this.mVectorState.mRootGroup.setTree(null);
                }
                this.mVectorState.mRootGroup = new VGroup();
                if (this.mVectorState.mNativeTree != null) {
                    VMRuntime.getRuntime().registerNativeFree(316);
                    this.mVectorState.mNativeTree.release();
                }
                VectorDrawableState vectorDrawableState = this.mVectorState;
                vectorDrawableState.createNativeTree(vectorDrawableState.mRootGroup);
            }
            VectorDrawableState state = this.mVectorState;
            state.setDensity(Drawable.resolveDensity(r, 0));
            TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.VectorDrawable);
            updateStateFromTypedArray(a);
            a.recycle();
            this.mDpiScaledDirty = true;
            state.mCacheDirty = true;
            inflateChildElements(r, parser, attrs, theme);
            state.onTreeConstructionFinished();
            updateLocalState(r);
        } finally {
            Trace.traceEnd(8192L);
        }
    }

    private void updateStateFromTypedArray(TypedArray a) throws XmlPullParserException {
        VectorDrawableState state = this.mVectorState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        int tintMode = a.getInt(6, -1);
        if (tintMode != -1) {
            state.mBlendMode = Drawable.parseBlendMode(tintMode, BlendMode.SRC_IN);
        }
        ColorStateList tint = a.getColorStateList(1);
        if (tint != null) {
            state.mTint = tint;
        }
        state.mAutoMirrored = a.getBoolean(5, state.mAutoMirrored);
        float viewportWidth = a.getFloat(7, state.mViewportWidth);
        float viewportHeight = a.getFloat(8, state.mViewportHeight);
        state.setViewportSize(viewportWidth, viewportHeight);
        if (state.mViewportWidth <= 0.0f) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires viewportWidth > 0");
        }
        if (state.mViewportHeight <= 0.0f) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires viewportHeight > 0");
        }
        state.mBaseWidth = a.getDimensionPixelSize(3, state.mBaseWidth);
        state.mBaseHeight = a.getDimensionPixelSize(2, state.mBaseHeight);
        if (state.mBaseWidth <= 0) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires width > 0");
        }
        if (state.mBaseHeight <= 0) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires height > 0");
        }
        int insetLeft = a.getDimensionPixelOffset(9, state.mOpticalInsets.left);
        int insetTop = a.getDimensionPixelOffset(10, state.mOpticalInsets.top);
        int insetRight = a.getDimensionPixelOffset(11, state.mOpticalInsets.right);
        int insetBottom = a.getDimensionPixelOffset(12, state.mOpticalInsets.bottom);
        state.mOpticalInsets = Insets.m186of(insetLeft, insetTop, insetRight, insetBottom);
        float alphaInFloat = a.getFloat(4, state.getAlpha());
        state.setAlpha(alphaInFloat);
        String name = a.getString(0);
        if (name != null) {
            state.mRootName = name;
            state.mVGTargetsMap.put(name, state);
        }
    }

    private void inflateChildElements(Resources res, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        VectorDrawableState state = this.mVectorState;
        boolean noPathTag = true;
        Stack<VGroup> groupStack = new Stack<>();
        groupStack.push(state.mRootGroup);
        int eventType = parser.getEventType();
        int innerDepth = parser.getDepth() + 1;
        while (eventType != 1 && (parser.getDepth() >= innerDepth || eventType != 3)) {
            if (eventType == 2) {
                String tagName = parser.getName();
                VGroup currentGroup = groupStack.peek();
                if (SHAPE_PATH.equals(tagName)) {
                    VFullPath path = new VFullPath();
                    path.inflate(res, attrs, theme);
                    currentGroup.addChild(path);
                    if (path.getPathName() != null) {
                        state.mVGTargetsMap.put(path.getPathName(), path);
                    }
                    noPathTag = false;
                    state.mChangingConfigurations |= path.mChangingConfigurations;
                } else if (SHAPE_CLIP_PATH.equals(tagName)) {
                    VClipPath path2 = new VClipPath();
                    path2.inflate(res, attrs, theme);
                    currentGroup.addChild(path2);
                    if (path2.getPathName() != null) {
                        state.mVGTargetsMap.put(path2.getPathName(), path2);
                    }
                    state.mChangingConfigurations |= path2.mChangingConfigurations;
                } else if (SHAPE_GROUP.equals(tagName)) {
                    VGroup newChildGroup = new VGroup();
                    newChildGroup.inflate(res, attrs, theme);
                    currentGroup.addChild(newChildGroup);
                    groupStack.push(newChildGroup);
                    if (newChildGroup.getGroupName() != null) {
                        state.mVGTargetsMap.put(newChildGroup.getGroupName(), newChildGroup);
                    }
                    state.mChangingConfigurations |= newChildGroup.mChangingConfigurations;
                }
            } else if (eventType == 3 && SHAPE_GROUP.equals(parser.getName())) {
                groupStack.pop();
            }
            eventType = parser.next();
        }
        if (noPathTag) {
            StringBuffer tag = new StringBuffer();
            if (tag.length() > 0) {
                tag.append(" or ");
            }
            tag.append(SHAPE_PATH);
            throw new XmlPullParserException("no " + ((Object) tag) + " defined");
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mVectorState.getChangingConfigurations();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAllowCaching(boolean allowCaching) {
        nSetAllowCaching(this.mVectorState.getNativeRenderer(), allowCaching);
    }

    private boolean needMirroring() {
        return isAutoMirrored() && getLayoutDirection() == 1;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean mirrored) {
        if (this.mVectorState.mAutoMirrored != mirrored) {
            this.mVectorState.mAutoMirrored = mirrored;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isAutoMirrored() {
        return this.mVectorState.mAutoMirrored;
    }

    public long getNativeTree() {
        return this.mVectorState.getNativeRenderer();
    }

    public void setAntiAlias(boolean aa) {
        nSetAntiAlias(this.mVectorState.mNativeTree.get(), aa);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class VectorDrawableState extends Drawable.ConstantState {
        static final Property<VectorDrawableState, Float> ALPHA = new FloatProperty<VectorDrawableState>("alpha") { // from class: android.graphics.drawable.VectorDrawable.VectorDrawableState.1
            @Override // android.util.FloatProperty
            public void setValue(VectorDrawableState state, float value) {
                state.setAlpha(value);
            }

            @Override // android.util.Property
            public Float get(VectorDrawableState state) {
                return Float.valueOf(state.getAlpha());
            }
        };
        private static final int NATIVE_ALLOCATION_SIZE = 316;
        private int mAllocationOfAllNodes;
        boolean mAutoMirrored;
        int mBaseHeight;
        int mBaseWidth;
        BlendMode mBlendMode;
        boolean mCacheDirty;
        boolean mCachedAutoMirrored;
        BlendMode mCachedBlendMode;
        int[] mCachedThemeAttrs;
        ColorStateList mCachedTint;
        int mChangingConfigurations;
        int mDensity;
        int mLastHWCachePixelCount;
        int mLastSWCachePixelCount;
        Insets mOpticalInsets;
        VGroup mRootGroup;
        String mRootName;
        int[] mThemeAttrs;
        ColorStateList mTint;
        final ArrayMap<String, Object> mVGTargetsMap;
        float mViewportWidth = 0.0f;
        float mViewportHeight = 0.0f;
        VirtualRefBasePtr mNativeTree = null;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Property getProperty(String propertyName) {
            Property<VectorDrawableState, Float> property = ALPHA;
            if (property.getName().equals(propertyName)) {
                return property;
            }
            return null;
        }

        public VectorDrawableState(VectorDrawableState copy) {
            this.mTint = null;
            this.mBlendMode = Drawable.DEFAULT_BLEND_MODE;
            this.mBaseWidth = 0;
            this.mBaseHeight = 0;
            this.mOpticalInsets = Insets.NONE;
            this.mRootName = null;
            this.mDensity = 160;
            ArrayMap<String, Object> arrayMap = new ArrayMap<>();
            this.mVGTargetsMap = arrayMap;
            this.mLastSWCachePixelCount = 0;
            this.mLastHWCachePixelCount = 0;
            this.mAllocationOfAllNodes = 0;
            if (copy != null) {
                this.mThemeAttrs = copy.mThemeAttrs;
                this.mChangingConfigurations = copy.mChangingConfigurations;
                this.mTint = copy.mTint;
                this.mBlendMode = copy.mBlendMode;
                this.mAutoMirrored = copy.mAutoMirrored;
                VGroup vGroup = new VGroup(copy.mRootGroup, arrayMap);
                this.mRootGroup = vGroup;
                createNativeTreeFromCopy(copy, vGroup);
                this.mBaseWidth = copy.mBaseWidth;
                this.mBaseHeight = copy.mBaseHeight;
                setViewportSize(copy.mViewportWidth, copy.mViewportHeight);
                this.mOpticalInsets = copy.mOpticalInsets;
                this.mRootName = copy.mRootName;
                this.mDensity = copy.mDensity;
                String str = copy.mRootName;
                if (str != null) {
                    arrayMap.put(str, this);
                }
            } else {
                VGroup vGroup2 = new VGroup();
                this.mRootGroup = vGroup2;
                createNativeTree(vGroup2);
            }
            onTreeConstructionFinished();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void createNativeTree(VGroup rootGroup) {
            this.mNativeTree = new VirtualRefBasePtr(VectorDrawable.nCreateTree(rootGroup.mNativePtr));
            VMRuntime.getRuntime().registerNativeAllocation(316);
        }

        private void createNativeTreeFromCopy(VectorDrawableState copy, VGroup rootGroup) {
            this.mNativeTree = new VirtualRefBasePtr(VectorDrawable.nCreateTreeFromCopy(copy.mNativeTree.get(), rootGroup.mNativePtr));
            VMRuntime.getRuntime().registerNativeAllocation(316);
        }

        void onTreeConstructionFinished() {
            this.mRootGroup.setTree(this.mNativeTree);
            this.mAllocationOfAllNodes = this.mRootGroup.getNativeSize();
            VMRuntime.getRuntime().registerNativeAllocation(this.mAllocationOfAllNodes);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public long getNativeRenderer() {
            VirtualRefBasePtr virtualRefBasePtr = this.mNativeTree;
            if (virtualRefBasePtr == null) {
                return 0L;
            }
            return virtualRefBasePtr.get();
        }

        public boolean canReuseCache() {
            if (!this.mCacheDirty && this.mCachedThemeAttrs == this.mThemeAttrs && this.mCachedTint == this.mTint && this.mCachedBlendMode == this.mBlendMode && this.mCachedAutoMirrored == this.mAutoMirrored) {
                return true;
            }
            updateCacheStates();
            return false;
        }

        public void updateCacheStates() {
            this.mCachedThemeAttrs = this.mThemeAttrs;
            this.mCachedTint = this.mTint;
            this.mCachedBlendMode = this.mBlendMode;
            this.mCachedAutoMirrored = this.mAutoMirrored;
            this.mCacheDirty = false;
        }

        public void applyTheme(Resources.Theme t) {
            this.mRootGroup.applyTheme(t);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            VGroup vGroup;
            ColorStateList colorStateList;
            return this.mThemeAttrs != null || ((vGroup = this.mRootGroup) != null && vGroup.canApplyTheme()) || (((colorStateList = this.mTint) != null && colorStateList.canApplyTheme()) || super.canApplyTheme());
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new VectorDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new VectorDrawable(this, res);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            int i = this.mChangingConfigurations;
            ColorStateList colorStateList = this.mTint;
            return i | (colorStateList != null ? colorStateList.getChangingConfigurations() : 0);
        }

        public boolean isStateful() {
            VGroup vGroup;
            ColorStateList colorStateList = this.mTint;
            return (colorStateList != null && colorStateList.isStateful()) || ((vGroup = this.mRootGroup) != null && vGroup.isStateful());
        }

        public boolean hasFocusStateSpecified() {
            VGroup vGroup;
            ColorStateList colorStateList = this.mTint;
            return (colorStateList != null && colorStateList.hasFocusStateSpecified()) || ((vGroup = this.mRootGroup) != null && vGroup.hasFocusStateSpecified());
        }

        void setViewportSize(float viewportWidth, float viewportHeight) {
            this.mViewportWidth = viewportWidth;
            this.mViewportHeight = viewportHeight;
            VectorDrawable.nSetRendererViewportSize(getNativeRenderer(), viewportWidth, viewportHeight);
        }

        public final boolean setDensity(int targetDensity) {
            if (this.mDensity != targetDensity) {
                int sourceDensity = this.mDensity;
                this.mDensity = targetDensity;
                applyDensityScaling(sourceDensity, targetDensity);
                return true;
            }
            return false;
        }

        private void applyDensityScaling(int sourceDensity, int targetDensity) {
            this.mBaseWidth = Drawable.scaleFromDensity(this.mBaseWidth, sourceDensity, targetDensity, true);
            this.mBaseHeight = Drawable.scaleFromDensity(this.mBaseHeight, sourceDensity, targetDensity, true);
            int insetLeft = Drawable.scaleFromDensity(this.mOpticalInsets.left, sourceDensity, targetDensity, false);
            int insetTop = Drawable.scaleFromDensity(this.mOpticalInsets.top, sourceDensity, targetDensity, false);
            int insetRight = Drawable.scaleFromDensity(this.mOpticalInsets.right, sourceDensity, targetDensity, false);
            int insetBottom = Drawable.scaleFromDensity(this.mOpticalInsets.bottom, sourceDensity, targetDensity, false);
            this.mOpticalInsets = Insets.m186of(insetLeft, insetTop, insetRight, insetBottom);
        }

        public boolean onStateChange(int[] stateSet) {
            return this.mRootGroup.onStateChange(stateSet);
        }

        public void finalize() throws Throwable {
            super.finalize();
            int bitmapCacheSize = (this.mLastHWCachePixelCount * 4) + (this.mLastSWCachePixelCount * 4);
            VMRuntime.getRuntime().registerNativeFree(this.mAllocationOfAllNodes + 316 + bitmapCacheSize);
        }

        public boolean setAlpha(float alpha) {
            return VectorDrawable.nSetRootAlpha(this.mNativeTree.get(), alpha);
        }

        public float getAlpha() {
            return VectorDrawable.nGetRootAlpha(this.mNativeTree.get());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class VGroup extends VObject {
        private static final int NATIVE_ALLOCATION_SIZE = 100;
        private static final Property<VGroup, Float> PIVOT_X;
        private static final int PIVOT_X_INDEX = 1;
        private static final Property<VGroup, Float> PIVOT_Y;
        private static final int PIVOT_Y_INDEX = 2;
        private static final Property<VGroup, Float> ROTATION;
        private static final int ROTATION_INDEX = 0;
        private static final Property<VGroup, Float> SCALE_X;
        private static final int SCALE_X_INDEX = 3;
        private static final Property<VGroup, Float> SCALE_Y;
        private static final int SCALE_Y_INDEX = 4;
        private static final int TRANSFORM_PROPERTY_COUNT = 7;
        private static final Property<VGroup, Float> TRANSLATE_X;
        private static final int TRANSLATE_X_INDEX = 5;
        private static final Property<VGroup, Float> TRANSLATE_Y;
        private static final int TRANSLATE_Y_INDEX = 6;
        private static final Map<String, Integer> sPropertyIndexMap = Map.of("translateX", 5, "translateY", 6, "scaleX", 3, "scaleY", 4, "pivotX", 1, "pivotY", 2, "rotation", 0);
        private static final Map<String, Property> sPropertyMap;
        private int mChangingConfigurations;
        private final ArrayList<VObject> mChildren;
        private String mGroupName;
        private boolean mIsStateful;
        private final long mNativePtr;
        private int[] mThemeAttrs;
        private float[] mTransform;

        static {
            FloatProperty<VGroup> floatProperty = new FloatProperty<VGroup>("translateX") { // from class: android.graphics.drawable.VectorDrawable.VGroup.1
                @Override // android.util.FloatProperty
                public void setValue(VGroup object, float value) {
                    object.setTranslateX(value);
                }

                @Override // android.util.Property
                public Float get(VGroup object) {
                    return Float.valueOf(object.getTranslateX());
                }
            };
            TRANSLATE_X = floatProperty;
            FloatProperty<VGroup> floatProperty2 = new FloatProperty<VGroup>("translateY") { // from class: android.graphics.drawable.VectorDrawable.VGroup.2
                @Override // android.util.FloatProperty
                public void setValue(VGroup object, float value) {
                    object.setTranslateY(value);
                }

                @Override // android.util.Property
                public Float get(VGroup object) {
                    return Float.valueOf(object.getTranslateY());
                }
            };
            TRANSLATE_Y = floatProperty2;
            FloatProperty<VGroup> floatProperty3 = new FloatProperty<VGroup>("scaleX") { // from class: android.graphics.drawable.VectorDrawable.VGroup.3
                @Override // android.util.FloatProperty
                public void setValue(VGroup object, float value) {
                    object.setScaleX(value);
                }

                @Override // android.util.Property
                public Float get(VGroup object) {
                    return Float.valueOf(object.getScaleX());
                }
            };
            SCALE_X = floatProperty3;
            FloatProperty<VGroup> floatProperty4 = new FloatProperty<VGroup>("scaleY") { // from class: android.graphics.drawable.VectorDrawable.VGroup.4
                @Override // android.util.FloatProperty
                public void setValue(VGroup object, float value) {
                    object.setScaleY(value);
                }

                @Override // android.util.Property
                public Float get(VGroup object) {
                    return Float.valueOf(object.getScaleY());
                }
            };
            SCALE_Y = floatProperty4;
            FloatProperty<VGroup> floatProperty5 = new FloatProperty<VGroup>("pivotX") { // from class: android.graphics.drawable.VectorDrawable.VGroup.5
                @Override // android.util.FloatProperty
                public void setValue(VGroup object, float value) {
                    object.setPivotX(value);
                }

                @Override // android.util.Property
                public Float get(VGroup object) {
                    return Float.valueOf(object.getPivotX());
                }
            };
            PIVOT_X = floatProperty5;
            FloatProperty<VGroup> floatProperty6 = new FloatProperty<VGroup>("pivotY") { // from class: android.graphics.drawable.VectorDrawable.VGroup.6
                @Override // android.util.FloatProperty
                public void setValue(VGroup object, float value) {
                    object.setPivotY(value);
                }

                @Override // android.util.Property
                public Float get(VGroup object) {
                    return Float.valueOf(object.getPivotY());
                }
            };
            PIVOT_Y = floatProperty6;
            FloatProperty<VGroup> floatProperty7 = new FloatProperty<VGroup>("rotation") { // from class: android.graphics.drawable.VectorDrawable.VGroup.7
                @Override // android.util.FloatProperty
                public void setValue(VGroup object, float value) {
                    object.setRotation(value);
                }

                @Override // android.util.Property
                public Float get(VGroup object) {
                    return Float.valueOf(object.getRotation());
                }
            };
            ROTATION = floatProperty7;
            sPropertyMap = Map.of("translateX", floatProperty, "translateY", floatProperty2, "scaleX", floatProperty3, "scaleY", floatProperty4, "pivotX", floatProperty5, "pivotY", floatProperty6, "rotation", floatProperty7);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static int getPropertyIndex(String propertyName) {
            Map<String, Integer> map = sPropertyIndexMap;
            if (map.containsKey(propertyName)) {
                return map.get(propertyName).intValue();
            }
            return -1;
        }

        public VGroup(VGroup copy, ArrayMap<String, Object> targetsMap) {
            VPath newPath;
            this.mChildren = new ArrayList<>();
            this.mGroupName = null;
            this.mIsStateful = copy.mIsStateful;
            this.mThemeAttrs = copy.mThemeAttrs;
            String str = copy.mGroupName;
            this.mGroupName = str;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            if (str != null) {
                targetsMap.put(str, this);
            }
            this.mNativePtr = VectorDrawable.nCreateGroup(copy.mNativePtr);
            ArrayList<VObject> children = copy.mChildren;
            for (int i = 0; i < children.size(); i++) {
                VObject copyChild = children.get(i);
                if (copyChild instanceof VGroup) {
                    VGroup copyGroup = (VGroup) copyChild;
                    addChild(new VGroup(copyGroup, targetsMap));
                } else {
                    if (copyChild instanceof VFullPath) {
                        newPath = new VFullPath((VFullPath) copyChild);
                    } else if (copyChild instanceof VClipPath) {
                        newPath = new VClipPath((VClipPath) copyChild);
                    } else {
                        throw new IllegalStateException("Unknown object in the tree!");
                    }
                    addChild(newPath);
                    if (newPath.mPathName != null) {
                        targetsMap.put(newPath.mPathName, newPath);
                    }
                }
            }
        }

        public VGroup() {
            this.mChildren = new ArrayList<>();
            this.mGroupName = null;
            this.mNativePtr = VectorDrawable.nCreateGroup();
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        Property getProperty(String propertyName) {
            Map<String, Property> map = sPropertyMap;
            if (map.containsKey(propertyName)) {
                return map.get(propertyName);
            }
            return null;
        }

        public String getGroupName() {
            return this.mGroupName;
        }

        public void addChild(VObject child) {
            VectorDrawable.nAddChild(this.mNativePtr, child.getNativePtr());
            this.mChildren.add(child);
            this.mIsStateful |= child.isStateful();
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public void setTree(VirtualRefBasePtr treeRoot) {
            super.setTree(treeRoot);
            for (int i = 0; i < this.mChildren.size(); i++) {
                this.mChildren.get(i).setTree(treeRoot);
            }
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public long getNativePtr() {
            return this.mNativePtr;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public void inflate(Resources res, AttributeSet attrs, Resources.Theme theme) {
            TypedArray a = Drawable.obtainAttributes(res, theme, attrs, C4057R.styleable.VectorDrawableGroup);
            updateStateFromTypedArray(a);
            a.recycle();
        }

        void updateStateFromTypedArray(TypedArray a) {
            this.mChangingConfigurations |= a.getChangingConfigurations();
            this.mThemeAttrs = a.extractThemeAttrs();
            if (this.mTransform == null) {
                this.mTransform = new float[7];
            }
            boolean success = VectorDrawable.nGetGroupProperties(this.mNativePtr, this.mTransform, 7);
            if (!success) {
                throw new RuntimeException("Error: inconsistent property count");
            }
            float rotate = a.getFloat(5, this.mTransform[0]);
            float pivotX = a.getFloat(1, this.mTransform[1]);
            float pivotY = a.getFloat(2, this.mTransform[2]);
            float scaleX = a.getFloat(3, this.mTransform[3]);
            float scaleY = a.getFloat(4, this.mTransform[4]);
            float translateX = a.getFloat(6, this.mTransform[5]);
            float translateY = a.getFloat(7, this.mTransform[6]);
            String groupName = a.getString(0);
            if (groupName != null) {
                this.mGroupName = groupName;
                VectorDrawable.nSetName(this.mNativePtr, groupName);
            }
            VectorDrawable.nUpdateGroupProperties(this.mNativePtr, rotate, pivotX, pivotY, scaleX, scaleY, translateX, translateY);
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean onStateChange(int[] stateSet) {
            boolean changed = false;
            ArrayList<VObject> children = this.mChildren;
            int count = children.size();
            for (int i = 0; i < count; i++) {
                VObject child = children.get(i);
                if (child.isStateful()) {
                    changed |= child.onStateChange(stateSet);
                }
            }
            return changed;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean isStateful() {
            return this.mIsStateful;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean hasFocusStateSpecified() {
            boolean result = false;
            ArrayList<VObject> children = this.mChildren;
            int count = children.size();
            for (int i = 0; i < count; i++) {
                VObject child = children.get(i);
                if (child.isStateful()) {
                    result |= child.hasFocusStateSpecified();
                }
            }
            return result;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        int getNativeSize() {
            int size = 100;
            for (int i = 0; i < this.mChildren.size(); i++) {
                size += this.mChildren.get(i).getNativeSize();
            }
            return size;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean canApplyTheme() {
            if (this.mThemeAttrs != null) {
                return true;
            }
            ArrayList<VObject> children = this.mChildren;
            int count = children.size();
            for (int i = 0; i < count; i++) {
                VObject child = children.get(i);
                if (child.canApplyTheme()) {
                    return true;
                }
            }
            return false;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public void applyTheme(Resources.Theme t) {
            int[] iArr = this.mThemeAttrs;
            if (iArr != null) {
                TypedArray a = t.resolveAttributes(iArr, C4057R.styleable.VectorDrawableGroup);
                updateStateFromTypedArray(a);
                a.recycle();
            }
            ArrayList<VObject> children = this.mChildren;
            int count = children.size();
            for (int i = 0; i < count; i++) {
                VObject child = children.get(i);
                if (child.canApplyTheme()) {
                    child.applyTheme(t);
                    this.mIsStateful |= child.isStateful();
                }
            }
        }

        public float getRotation() {
            if (isTreeValid()) {
                return VectorDrawable.nGetRotation(this.mNativePtr);
            }
            return 0.0f;
        }

        public void setRotation(float rotation) {
            if (isTreeValid()) {
                VectorDrawable.nSetRotation(this.mNativePtr, rotation);
            }
        }

        public float getPivotX() {
            if (isTreeValid()) {
                return VectorDrawable.nGetPivotX(this.mNativePtr);
            }
            return 0.0f;
        }

        public void setPivotX(float pivotX) {
            if (isTreeValid()) {
                VectorDrawable.nSetPivotX(this.mNativePtr, pivotX);
            }
        }

        public float getPivotY() {
            if (isTreeValid()) {
                return VectorDrawable.nGetPivotY(this.mNativePtr);
            }
            return 0.0f;
        }

        public void setPivotY(float pivotY) {
            if (isTreeValid()) {
                VectorDrawable.nSetPivotY(this.mNativePtr, pivotY);
            }
        }

        public float getScaleX() {
            if (isTreeValid()) {
                return VectorDrawable.nGetScaleX(this.mNativePtr);
            }
            return 0.0f;
        }

        public void setScaleX(float scaleX) {
            if (isTreeValid()) {
                VectorDrawable.nSetScaleX(this.mNativePtr, scaleX);
            }
        }

        public float getScaleY() {
            if (isTreeValid()) {
                return VectorDrawable.nGetScaleY(this.mNativePtr);
            }
            return 0.0f;
        }

        public void setScaleY(float scaleY) {
            if (isTreeValid()) {
                VectorDrawable.nSetScaleY(this.mNativePtr, scaleY);
            }
        }

        public float getTranslateX() {
            if (isTreeValid()) {
                return VectorDrawable.nGetTranslateX(this.mNativePtr);
            }
            return 0.0f;
        }

        public void setTranslateX(float translateX) {
            if (isTreeValid()) {
                VectorDrawable.nSetTranslateX(this.mNativePtr, translateX);
            }
        }

        public float getTranslateY() {
            if (isTreeValid()) {
                return VectorDrawable.nGetTranslateY(this.mNativePtr);
            }
            return 0.0f;
        }

        public void setTranslateY(float translateY) {
            if (isTreeValid()) {
                VectorDrawable.nSetTranslateY(this.mNativePtr, translateY);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class VPath extends VObject {
        private static final Property<VPath, PathParser.PathData> PATH_DATA = new Property<VPath, PathParser.PathData>(PathParser.PathData.class, "pathData") { // from class: android.graphics.drawable.VectorDrawable.VPath.1
            @Override // android.util.Property
            public void set(VPath object, PathParser.PathData data) {
                object.setPathData(data);
            }

            @Override // android.util.Property
            public PathParser.PathData get(VPath object) {
                return object.getPathData();
            }
        };
        int mChangingConfigurations;
        protected PathParser.PathData mPathData;
        String mPathName;

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.graphics.drawable.VectorDrawable.VObject
        public Property getProperty(String propertyName) {
            Property<VPath, PathParser.PathData> property = PATH_DATA;
            if (property.getName().equals(propertyName)) {
                return property;
            }
            return null;
        }

        public VPath() {
            this.mPathData = null;
        }

        public VPath(VPath copy) {
            this.mPathData = null;
            this.mPathName = copy.mPathName;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            PathParser.PathData pathData = copy.mPathData;
            this.mPathData = pathData != null ? new PathParser.PathData(pathData) : null;
        }

        public String getPathName() {
            return this.mPathName;
        }

        public PathParser.PathData getPathData() {
            return this.mPathData;
        }

        public void setPathData(PathParser.PathData pathData) {
            this.mPathData.setPathData(pathData);
            if (isTreeValid()) {
                VectorDrawable.nSetPathData(getNativePtr(), this.mPathData.getNativePtr());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VClipPath extends VPath {
        private static final int NATIVE_ALLOCATION_SIZE = 120;
        private final long mNativePtr;

        public VClipPath() {
            this.mNativePtr = VectorDrawable.nCreateClipPath();
        }

        public VClipPath(VClipPath copy) {
            super(copy);
            this.mNativePtr = VectorDrawable.nCreateClipPath(copy.mNativePtr);
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public long getNativePtr() {
            return this.mNativePtr;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public void inflate(Resources r, AttributeSet attrs, Resources.Theme theme) {
            TypedArray a = Drawable.obtainAttributes(r, theme, attrs, C4057R.styleable.VectorDrawableClipPath);
            updateStateFromTypedArray(a);
            a.recycle();
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean canApplyTheme() {
            return false;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public void applyTheme(Resources.Theme theme) {
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean onStateChange(int[] stateSet) {
            return false;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean isStateful() {
            return false;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean hasFocusStateSpecified() {
            return false;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        int getNativeSize() {
            return 120;
        }

        private void updateStateFromTypedArray(TypedArray a) {
            this.mChangingConfigurations |= a.getChangingConfigurations();
            String pathName = a.getString(0);
            if (pathName != null) {
                this.mPathName = pathName;
                VectorDrawable.nSetName(this.mNativePtr, this.mPathName);
            }
            String pathDataString = a.getString(1);
            if (pathDataString != null) {
                this.mPathData = new PathParser.PathData(pathDataString);
                VectorDrawable.nSetPathString(this.mNativePtr, pathDataString, pathDataString.length());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class VFullPath extends VPath {
        private static final Property<VFullPath, Float> FILL_ALPHA;
        private static final int FILL_ALPHA_INDEX = 4;
        private static final Property<VFullPath, Integer> FILL_COLOR;
        private static final int FILL_COLOR_INDEX = 3;
        private static final int FILL_TYPE_INDEX = 11;
        private static final int NATIVE_ALLOCATION_SIZE = 264;
        private static final Property<VFullPath, Float> STROKE_ALPHA;
        private static final int STROKE_ALPHA_INDEX = 2;
        private static final Property<VFullPath, Integer> STROKE_COLOR;
        private static final int STROKE_COLOR_INDEX = 1;
        private static final int STROKE_LINE_CAP_INDEX = 8;
        private static final int STROKE_LINE_JOIN_INDEX = 9;
        private static final int STROKE_MITER_LIMIT_INDEX = 10;
        private static final Property<VFullPath, Float> STROKE_WIDTH;
        private static final int STROKE_WIDTH_INDEX = 0;
        private static final int TOTAL_PROPERTY_COUNT = 12;
        private static final Property<VFullPath, Float> TRIM_PATH_END;
        private static final int TRIM_PATH_END_INDEX = 6;
        private static final Property<VFullPath, Float> TRIM_PATH_OFFSET;
        private static final int TRIM_PATH_OFFSET_INDEX = 7;
        private static final Property<VFullPath, Float> TRIM_PATH_START;
        private static final int TRIM_PATH_START_INDEX = 5;
        private static final Map<String, Integer> sPropertyIndexMap = Map.of("strokeWidth", 0, "strokeColor", 1, "strokeAlpha", 2, "fillColor", 3, "fillAlpha", 4, "trimPathStart", 5, "trimPathEnd", 6, "trimPathOffset", 7);
        private static final Map<String, Property> sPropertyMap;
        ComplexColor mFillColors;
        private final long mNativePtr;
        private byte[] mPropertyData;
        ComplexColor mStrokeColors;
        private int[] mThemeAttrs;

        static {
            FloatProperty<VFullPath> floatProperty = new FloatProperty<VFullPath>("strokeWidth") { // from class: android.graphics.drawable.VectorDrawable.VFullPath.1
                @Override // android.util.FloatProperty
                public void setValue(VFullPath object, float value) {
                    object.setStrokeWidth(value);
                }

                @Override // android.util.Property
                public Float get(VFullPath object) {
                    return Float.valueOf(object.getStrokeWidth());
                }
            };
            STROKE_WIDTH = floatProperty;
            IntProperty<VFullPath> intProperty = new IntProperty<VFullPath>("strokeColor") { // from class: android.graphics.drawable.VectorDrawable.VFullPath.2
                @Override // android.util.IntProperty
                public void setValue(VFullPath object, int value) {
                    object.setStrokeColor(value);
                }

                @Override // android.util.Property
                public Integer get(VFullPath object) {
                    return Integer.valueOf(object.getStrokeColor());
                }
            };
            STROKE_COLOR = intProperty;
            FloatProperty<VFullPath> floatProperty2 = new FloatProperty<VFullPath>("strokeAlpha") { // from class: android.graphics.drawable.VectorDrawable.VFullPath.3
                @Override // android.util.FloatProperty
                public void setValue(VFullPath object, float value) {
                    object.setStrokeAlpha(value);
                }

                @Override // android.util.Property
                public Float get(VFullPath object) {
                    return Float.valueOf(object.getStrokeAlpha());
                }
            };
            STROKE_ALPHA = floatProperty2;
            IntProperty<VFullPath> intProperty2 = new IntProperty<VFullPath>("fillColor") { // from class: android.graphics.drawable.VectorDrawable.VFullPath.4
                @Override // android.util.IntProperty
                public void setValue(VFullPath object, int value) {
                    object.setFillColor(value);
                }

                @Override // android.util.Property
                public Integer get(VFullPath object) {
                    return Integer.valueOf(object.getFillColor());
                }
            };
            FILL_COLOR = intProperty2;
            FloatProperty<VFullPath> floatProperty3 = new FloatProperty<VFullPath>("fillAlpha") { // from class: android.graphics.drawable.VectorDrawable.VFullPath.5
                @Override // android.util.FloatProperty
                public void setValue(VFullPath object, float value) {
                    object.setFillAlpha(value);
                }

                @Override // android.util.Property
                public Float get(VFullPath object) {
                    return Float.valueOf(object.getFillAlpha());
                }
            };
            FILL_ALPHA = floatProperty3;
            FloatProperty<VFullPath> floatProperty4 = new FloatProperty<VFullPath>("trimPathStart") { // from class: android.graphics.drawable.VectorDrawable.VFullPath.6
                @Override // android.util.FloatProperty
                public void setValue(VFullPath object, float value) {
                    object.setTrimPathStart(value);
                }

                @Override // android.util.Property
                public Float get(VFullPath object) {
                    return Float.valueOf(object.getTrimPathStart());
                }
            };
            TRIM_PATH_START = floatProperty4;
            FloatProperty<VFullPath> floatProperty5 = new FloatProperty<VFullPath>("trimPathEnd") { // from class: android.graphics.drawable.VectorDrawable.VFullPath.7
                @Override // android.util.FloatProperty
                public void setValue(VFullPath object, float value) {
                    object.setTrimPathEnd(value);
                }

                @Override // android.util.Property
                public Float get(VFullPath object) {
                    return Float.valueOf(object.getTrimPathEnd());
                }
            };
            TRIM_PATH_END = floatProperty5;
            FloatProperty<VFullPath> floatProperty6 = new FloatProperty<VFullPath>("trimPathOffset") { // from class: android.graphics.drawable.VectorDrawable.VFullPath.8
                @Override // android.util.FloatProperty
                public void setValue(VFullPath object, float value) {
                    object.setTrimPathOffset(value);
                }

                @Override // android.util.Property
                public Float get(VFullPath object) {
                    return Float.valueOf(object.getTrimPathOffset());
                }
            };
            TRIM_PATH_OFFSET = floatProperty6;
            sPropertyMap = Map.of("strokeWidth", floatProperty, "strokeColor", intProperty, "strokeAlpha", floatProperty2, "fillColor", intProperty2, "fillAlpha", floatProperty3, "trimPathStart", floatProperty4, "trimPathEnd", floatProperty5, "trimPathOffset", floatProperty6);
        }

        public VFullPath() {
            this.mStrokeColors = null;
            this.mFillColors = null;
            this.mNativePtr = VectorDrawable.nCreateFullPath();
        }

        public VFullPath(VFullPath copy) {
            super(copy);
            this.mStrokeColors = null;
            this.mFillColors = null;
            this.mNativePtr = VectorDrawable.nCreateFullPath(copy.mNativePtr);
            this.mThemeAttrs = copy.mThemeAttrs;
            this.mStrokeColors = copy.mStrokeColors;
            this.mFillColors = copy.mFillColors;
        }

        @Override // android.graphics.drawable.VectorDrawable.VPath, android.graphics.drawable.VectorDrawable.VObject
        Property getProperty(String propertyName) {
            Property p = super.getProperty(propertyName);
            if (p != null) {
                return p;
            }
            Map<String, Property> map = sPropertyMap;
            if (map.containsKey(propertyName)) {
                return map.get(propertyName);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int getPropertyIndex(String propertyName) {
            Map<String, Integer> map = sPropertyIndexMap;
            if (!map.containsKey(propertyName)) {
                return -1;
            }
            return map.get(propertyName).intValue();
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean onStateChange(int[] stateSet) {
            boolean changed = false;
            ComplexColor complexColor = this.mStrokeColors;
            if (complexColor != null && (complexColor instanceof ColorStateList)) {
                int oldStrokeColor = getStrokeColor();
                int newStrokeColor = ((ColorStateList) this.mStrokeColors).getColorForState(stateSet, oldStrokeColor);
                changed = false | (oldStrokeColor != newStrokeColor);
                if (oldStrokeColor != newStrokeColor) {
                    VectorDrawable.nSetStrokeColor(this.mNativePtr, newStrokeColor);
                }
            }
            ComplexColor complexColor2 = this.mFillColors;
            if (complexColor2 != null && (complexColor2 instanceof ColorStateList)) {
                int oldFillColor = getFillColor();
                int newFillColor = ((ColorStateList) this.mFillColors).getColorForState(stateSet, oldFillColor);
                changed |= oldFillColor != newFillColor;
                if (oldFillColor != newFillColor) {
                    VectorDrawable.nSetFillColor(this.mNativePtr, newFillColor);
                }
            }
            return changed;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean isStateful() {
            return (this.mStrokeColors == null && this.mFillColors == null) ? false : true;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean hasFocusStateSpecified() {
            ComplexColor complexColor;
            ComplexColor complexColor2 = this.mStrokeColors;
            return complexColor2 != null && (complexColor2 instanceof ColorStateList) && ((ColorStateList) complexColor2).hasFocusStateSpecified() && (complexColor = this.mFillColors) != null && (complexColor instanceof ColorStateList) && ((ColorStateList) complexColor).hasFocusStateSpecified();
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        int getNativeSize() {
            return 264;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public long getNativePtr() {
            return this.mNativePtr;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public void inflate(Resources r, AttributeSet attrs, Resources.Theme theme) {
            TypedArray a = Drawable.obtainAttributes(r, theme, attrs, C4057R.styleable.VectorDrawablePath);
            updateStateFromTypedArray(a);
            a.recycle();
        }

        private void updateStateFromTypedArray(TypedArray a) {
            int strokeColor;
            float trimPathOffset;
            int fillColor;
            int fillColor2;
            int strokeColor2;
            int fillType;
            int strokeColor3;
            long j;
            if (this.mPropertyData == null) {
                this.mPropertyData = new byte[48];
            }
            boolean success = VectorDrawable.nGetFullPathProperties(this.mNativePtr, this.mPropertyData, 48);
            if (!success) {
                throw new RuntimeException("Error: inconsistent property count");
            }
            ByteBuffer properties = ByteBuffer.wrap(this.mPropertyData);
            properties.order(ByteOrder.nativeOrder());
            float strokeWidth = properties.getFloat(0);
            int strokeColor4 = properties.getInt(4);
            float strokeAlpha = properties.getFloat(8);
            int fillColor3 = properties.getInt(12);
            float fillAlpha = properties.getFloat(16);
            float trimPathStart = properties.getFloat(20);
            float trimPathEnd = properties.getFloat(24);
            float trimPathOffset2 = properties.getFloat(28);
            int strokeLineCap = properties.getInt(32);
            int strokeLineJoin = properties.getInt(36);
            float strokeMiterLimit = properties.getFloat(40);
            int fillType2 = properties.getInt(44);
            Shader fillGradient = null;
            Shader strokeGradient = null;
            this.mChangingConfigurations |= a.getChangingConfigurations();
            this.mThemeAttrs = a.extractThemeAttrs();
            String pathName = a.getString(0);
            if (pathName != null) {
                this.mPathName = pathName;
                strokeColor = strokeColor4;
                VectorDrawable.nSetName(this.mNativePtr, this.mPathName);
            } else {
                strokeColor = strokeColor4;
            }
            String pathString = a.getString(2);
            if (pathString != null) {
                this.mPathData = new PathParser.PathData(pathString);
                trimPathOffset = trimPathOffset2;
                fillColor = fillColor3;
                VectorDrawable.nSetPathString(this.mNativePtr, pathString, pathString.length());
            } else {
                trimPathOffset = trimPathOffset2;
                fillColor = fillColor3;
            }
            ComplexColor fillColors = a.getComplexColor(1);
            if (fillColors == null) {
                fillColor2 = fillColor;
            } else {
                if (fillColors instanceof GradientColor) {
                    this.mFillColors = fillColors;
                    fillGradient = ((GradientColor) fillColors).getShader();
                } else if (fillColors.isStateful() || fillColors.canApplyTheme()) {
                    this.mFillColors = fillColors;
                } else {
                    this.mFillColors = null;
                }
                fillColor2 = fillColors.getDefaultColor();
            }
            ComplexColor strokeColors = a.getComplexColor(3);
            if (strokeColors != null) {
                if (strokeColors instanceof GradientColor) {
                    this.mStrokeColors = strokeColors;
                    strokeGradient = ((GradientColor) strokeColors).getShader();
                } else if (!strokeColors.isStateful() && !strokeColors.canApplyTheme()) {
                    this.mStrokeColors = null;
                } else {
                    this.mStrokeColors = strokeColors;
                }
                strokeColor2 = strokeColors.getDefaultColor();
            } else {
                strokeColor2 = strokeColor;
            }
            long j2 = this.mNativePtr;
            if (fillGradient != null) {
                strokeColor3 = strokeColor2;
                fillType = fillType2;
                j = fillGradient.getNativeInstance();
            } else {
                fillType = fillType2;
                strokeColor3 = strokeColor2;
                j = 0;
            }
            VectorDrawable.nUpdateFullPathFillGradient(j2, j);
            VectorDrawable.nUpdateFullPathStrokeGradient(this.mNativePtr, strokeGradient != null ? strokeGradient.getNativeInstance() : 0L);
            float fillAlpha2 = a.getFloat(12, fillAlpha);
            int strokeLineCap2 = a.getInt(8, strokeLineCap);
            int strokeLineJoin2 = a.getInt(9, strokeLineJoin);
            float strokeMiterLimit2 = a.getFloat(10, strokeMiterLimit);
            VectorDrawable.nUpdateFullPathProperties(this.mNativePtr, a.getFloat(4, strokeWidth), strokeColor3, a.getFloat(11, strokeAlpha), fillColor2, fillAlpha2, a.getFloat(5, trimPathStart), a.getFloat(6, trimPathEnd), a.getFloat(7, trimPathOffset), strokeMiterLimit2, strokeLineCap2, strokeLineJoin2, a.getInt(13, fillType));
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public boolean canApplyTheme() {
            if (this.mThemeAttrs != null) {
                return true;
            }
            boolean fillCanApplyTheme = canComplexColorApplyTheme(this.mFillColors);
            boolean strokeCanApplyTheme = canComplexColorApplyTheme(this.mStrokeColors);
            return fillCanApplyTheme || strokeCanApplyTheme;
        }

        @Override // android.graphics.drawable.VectorDrawable.VObject
        public void applyTheme(Resources.Theme t) {
            int[] iArr = this.mThemeAttrs;
            if (iArr != null) {
                TypedArray a = t.resolveAttributes(iArr, C4057R.styleable.VectorDrawablePath);
                updateStateFromTypedArray(a);
                a.recycle();
            }
            boolean fillCanApplyTheme = canComplexColorApplyTheme(this.mFillColors);
            boolean strokeCanApplyTheme = canComplexColorApplyTheme(this.mStrokeColors);
            if (fillCanApplyTheme) {
                ComplexColor obtainForTheme = this.mFillColors.obtainForTheme(t);
                this.mFillColors = obtainForTheme;
                if (obtainForTheme instanceof GradientColor) {
                    VectorDrawable.nUpdateFullPathFillGradient(this.mNativePtr, ((GradientColor) obtainForTheme).getShader().getNativeInstance());
                } else if (obtainForTheme instanceof ColorStateList) {
                    VectorDrawable.nSetFillColor(this.mNativePtr, obtainForTheme.getDefaultColor());
                }
            }
            if (strokeCanApplyTheme) {
                ComplexColor obtainForTheme2 = this.mStrokeColors.obtainForTheme(t);
                this.mStrokeColors = obtainForTheme2;
                if (obtainForTheme2 instanceof GradientColor) {
                    VectorDrawable.nUpdateFullPathStrokeGradient(this.mNativePtr, ((GradientColor) obtainForTheme2).getShader().getNativeInstance());
                } else if (obtainForTheme2 instanceof ColorStateList) {
                    VectorDrawable.nSetStrokeColor(this.mNativePtr, obtainForTheme2.getDefaultColor());
                }
            }
        }

        private boolean canComplexColorApplyTheme(ComplexColor complexColor) {
            return complexColor != null && complexColor.canApplyTheme();
        }

        int getStrokeColor() {
            if (isTreeValid()) {
                return VectorDrawable.nGetStrokeColor(this.mNativePtr);
            }
            return 0;
        }

        void setStrokeColor(int strokeColor) {
            this.mStrokeColors = null;
            if (isTreeValid()) {
                VectorDrawable.nSetStrokeColor(this.mNativePtr, strokeColor);
            }
        }

        float getStrokeWidth() {
            if (isTreeValid()) {
                return VectorDrawable.nGetStrokeWidth(this.mNativePtr);
            }
            return 0.0f;
        }

        void setStrokeWidth(float strokeWidth) {
            if (isTreeValid()) {
                VectorDrawable.nSetStrokeWidth(this.mNativePtr, strokeWidth);
            }
        }

        float getStrokeAlpha() {
            if (isTreeValid()) {
                return VectorDrawable.nGetStrokeAlpha(this.mNativePtr);
            }
            return 0.0f;
        }

        void setStrokeAlpha(float strokeAlpha) {
            if (isTreeValid()) {
                VectorDrawable.nSetStrokeAlpha(this.mNativePtr, strokeAlpha);
            }
        }

        int getFillColor() {
            if (isTreeValid()) {
                return VectorDrawable.nGetFillColor(this.mNativePtr);
            }
            return 0;
        }

        void setFillColor(int fillColor) {
            this.mFillColors = null;
            if (isTreeValid()) {
                VectorDrawable.nSetFillColor(this.mNativePtr, fillColor);
            }
        }

        float getFillAlpha() {
            if (isTreeValid()) {
                return VectorDrawable.nGetFillAlpha(this.mNativePtr);
            }
            return 0.0f;
        }

        void setFillAlpha(float fillAlpha) {
            if (isTreeValid()) {
                VectorDrawable.nSetFillAlpha(this.mNativePtr, fillAlpha);
            }
        }

        float getTrimPathStart() {
            if (isTreeValid()) {
                return VectorDrawable.nGetTrimPathStart(this.mNativePtr);
            }
            return 0.0f;
        }

        void setTrimPathStart(float trimPathStart) {
            if (isTreeValid()) {
                VectorDrawable.nSetTrimPathStart(this.mNativePtr, trimPathStart);
            }
        }

        float getTrimPathEnd() {
            if (isTreeValid()) {
                return VectorDrawable.nGetTrimPathEnd(this.mNativePtr);
            }
            return 0.0f;
        }

        void setTrimPathEnd(float trimPathEnd) {
            if (isTreeValid()) {
                VectorDrawable.nSetTrimPathEnd(this.mNativePtr, trimPathEnd);
            }
        }

        float getTrimPathOffset() {
            if (isTreeValid()) {
                return VectorDrawable.nGetTrimPathOffset(this.mNativePtr);
            }
            return 0.0f;
        }

        void setTrimPathOffset(float trimPathOffset) {
            if (isTreeValid()) {
                VectorDrawable.nSetTrimPathOffset(this.mNativePtr, trimPathOffset);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class VObject {
        VirtualRefBasePtr mTreePtr = null;

        abstract void applyTheme(Resources.Theme theme);

        abstract boolean canApplyTheme();

        /* JADX INFO: Access modifiers changed from: package-private */
        public abstract long getNativePtr();

        abstract int getNativeSize();

        /* JADX INFO: Access modifiers changed from: package-private */
        public abstract Property getProperty(String str);

        abstract boolean hasFocusStateSpecified();

        abstract void inflate(Resources resources, AttributeSet attributeSet, Resources.Theme theme);

        abstract boolean isStateful();

        abstract boolean onStateChange(int[] iArr);

        VObject() {
        }

        boolean isTreeValid() {
            VirtualRefBasePtr virtualRefBasePtr = this.mTreePtr;
            return (virtualRefBasePtr == null || virtualRefBasePtr.get() == 0) ? false : true;
        }

        void setTree(VirtualRefBasePtr ptr) {
            this.mTreePtr = ptr;
        }
    }
}
