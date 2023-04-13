package android.graphics.drawable;

import android.app.ActivityThread;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.PathParser;
import com.android.internal.C4057R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AdaptiveIconDrawable extends Drawable implements Drawable.Callback {
    private static final int BACKGROUND_ID = 0;
    private static final float DEFAULT_VIEW_PORT_SCALE = 0.6666667f;
    private static final float EXTRA_INSET_PERCENTAGE = 0.25f;
    private static final int FOREGROUND_ID = 1;
    public static final float MASK_SIZE = 100.0f;
    private static final int MONOCHROME_ID = 2;
    private static final float SAFEZONE_SCALE = 0.9166667f;
    private static Path sMask;
    private final Canvas mCanvas;
    private boolean mChildRequestedInvalidation;
    private Rect mHotspotBounds;
    LayerState mLayerState;
    private Bitmap mLayersBitmap;
    private Shader mLayersShader;
    private final Path mMask;
    private final Matrix mMaskMatrix;
    private final Path mMaskScaleOnly;
    private boolean mMutated;
    private Paint mPaint;
    private boolean mSuspendChildInvalidation;
    private final Rect mTmpOutRect;
    private final Region mTransparentRegion;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdaptiveIconDrawable() {
        this((LayerState) null, (Resources) null);
    }

    AdaptiveIconDrawable(LayerState state, Resources res) {
        Resources r;
        this.mTmpOutRect = new Rect();
        this.mPaint = new Paint(7);
        this.mLayerState = createConstantState(state, res);
        if (ActivityThread.currentActivityThread() == null) {
            r = Resources.getSystem();
        } else {
            r = ActivityThread.currentActivityThread().getApplication().getResources();
        }
        sMask = PathParser.createPathFromPathData(r.getString(C4057R.string.config_icon_mask));
        Path path = new Path(sMask);
        this.mMask = path;
        this.mMaskScaleOnly = new Path(path);
        this.mMaskMatrix = new Matrix();
        this.mCanvas = new Canvas();
        this.mTransparentRegion = new Region();
    }

    private ChildDrawable createChildDrawable(Drawable drawable) {
        ChildDrawable layer = new ChildDrawable(this.mLayerState.mDensity);
        layer.mDrawable = drawable;
        layer.mDrawable.setCallback(this);
        this.mLayerState.mChildrenChangingConfigurations |= layer.mDrawable.getChangingConfigurations();
        return layer;
    }

    LayerState createConstantState(LayerState state, Resources res) {
        return new LayerState(state, this, res);
    }

    public AdaptiveIconDrawable(Drawable backgroundDrawable, Drawable foregroundDrawable) {
        this(backgroundDrawable, foregroundDrawable, null);
    }

    public AdaptiveIconDrawable(Drawable backgroundDrawable, Drawable foregroundDrawable, Drawable monochromeDrawable) {
        this((LayerState) null, (Resources) null);
        if (backgroundDrawable != null) {
            addLayer(0, createChildDrawable(backgroundDrawable));
        }
        if (foregroundDrawable != null) {
            addLayer(1, createChildDrawable(foregroundDrawable));
        }
        if (monochromeDrawable != null) {
            addLayer(2, createChildDrawable(monochromeDrawable));
        }
    }

    private void addLayer(int index, ChildDrawable layer) {
        this.mLayerState.mChildren[index] = layer;
        this.mLayerState.invalidateCache();
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        LayerState state = this.mLayerState;
        if (state == null) {
            return;
        }
        int deviceDensity = Drawable.resolveDensity(r, 0);
        state.setDensity(deviceDensity);
        state.mSrcDensityOverride = this.mSrcDensityOverride;
        state.mSourceDrawableId = Resources.getAttributeSetSourceResId(attrs);
        ChildDrawable[] array = state.mChildren;
        for (ChildDrawable childDrawable : array) {
            childDrawable.setDensity(deviceDensity);
        }
        inflateLayers(r, parser, attrs, theme);
    }

    public static float getExtraInsetFraction() {
        return 0.25f;
    }

    public static float getExtraInsetPercentage() {
        return 0.25f;
    }

    public Path getIconMask() {
        return this.mMask;
    }

    public Drawable getForeground() {
        return this.mLayerState.mChildren[1].mDrawable;
    }

    public Drawable getBackground() {
        return this.mLayerState.mChildren[0].mDrawable;
    }

    public Drawable getMonochrome() {
        return this.mLayerState.mChildren[2].mDrawable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        if (bounds.isEmpty()) {
            return;
        }
        updateLayerBounds(bounds);
    }

    private void updateLayerBounds(Rect bounds) {
        if (bounds.isEmpty()) {
            return;
        }
        try {
            suspendChildInvalidation();
            updateLayerBoundsInternal(bounds);
            updateMaskBoundsInternal(bounds);
        } finally {
            resumeChildInvalidation();
        }
    }

    private void updateLayerBoundsInternal(Rect bounds) {
        int cX = bounds.width() / 2;
        int cY = bounds.height() / 2;
        for (int i = 0; i < 3; i++) {
            ChildDrawable r = this.mLayerState.mChildren[i];
            Drawable d = r.mDrawable;
            if (d != null) {
                int insetWidth = (int) (bounds.width() / 1.3333334f);
                int insetHeight = (int) (bounds.height() / 1.3333334f);
                Rect outRect = this.mTmpOutRect;
                outRect.set(cX - insetWidth, cY - insetHeight, cX + insetWidth, cY + insetHeight);
                d.setBounds(outRect);
            }
        }
    }

    private void updateMaskBoundsInternal(Rect b) {
        this.mMaskMatrix.setScale(b.width() / 100.0f, b.height() / 100.0f);
        sMask.transform(this.mMaskMatrix, this.mMaskScaleOnly);
        this.mMaskMatrix.postTranslate(b.left, b.top);
        sMask.transform(this.mMaskMatrix, this.mMask);
        Bitmap bitmap = this.mLayersBitmap;
        if (bitmap == null || bitmap.getWidth() != b.width() || this.mLayersBitmap.getHeight() != b.height()) {
            this.mLayersBitmap = Bitmap.createBitmap(b.width(), b.height(), Bitmap.Config.ARGB_8888);
        }
        this.mPaint.setShader(null);
        this.mTransparentRegion.setEmpty();
        this.mLayersShader = null;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Bitmap bitmap = this.mLayersBitmap;
        if (bitmap == null) {
            return;
        }
        if (this.mLayersShader == null) {
            this.mCanvas.setBitmap(bitmap);
            this.mCanvas.drawColor(-16777216);
            if (this.mLayerState.mChildren[0].mDrawable != null) {
                this.mLayerState.mChildren[0].mDrawable.draw(this.mCanvas);
            }
            if (this.mLayerState.mChildren[1].mDrawable != null) {
                this.mLayerState.mChildren[1].mDrawable.draw(this.mCanvas);
            }
            BitmapShader bitmapShader = new BitmapShader(this.mLayersBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            this.mLayersShader = bitmapShader;
            this.mPaint.setShader(bitmapShader);
        }
        if (this.mMaskScaleOnly != null) {
            Rect bounds = getBounds();
            canvas.translate(bounds.left, bounds.top);
            canvas.drawPath(this.mMaskScaleOnly, this.mPaint);
            canvas.translate(-bounds.left, -bounds.top);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void invalidateSelf() {
        this.mLayersShader = null;
        super.invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        outline.setPath(this.mMask);
    }

    public Region getSafeZone() {
        Path mask = getIconMask();
        this.mMaskMatrix.setScale(SAFEZONE_SCALE, SAFEZONE_SCALE, getBounds().centerX(), getBounds().centerY());
        Path p = new Path();
        mask.transform(this.mMaskMatrix, p);
        Region safezoneRegion = new Region(getBounds());
        safezoneRegion.setPath(p, safezoneRegion);
        return safezoneRegion;
    }

    @Override // android.graphics.drawable.Drawable
    public Region getTransparentRegion() {
        if (this.mTransparentRegion.isEmpty()) {
            this.mMask.toggleInverseFillType();
            this.mTransparentRegion.set(getBounds());
            Region region = this.mTransparentRegion;
            region.setPath(this.mMask, region);
            this.mMask.toggleInverseFillType();
        }
        return this.mTransparentRegion;
    }

    @Override // android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme t) {
        super.applyTheme(t);
        LayerState state = this.mLayerState;
        if (state == null) {
            return;
        }
        int density = Drawable.resolveDensity(t.getResources(), 0);
        state.setDensity(density);
        ChildDrawable[] array = state.mChildren;
        for (int i = 0; i < 3; i++) {
            ChildDrawable layer = array[i];
            layer.setDensity(density);
            if (layer.mThemeAttrs != null) {
                TypedArray a = t.resolveAttributes(layer.mThemeAttrs, C4057R.styleable.AdaptiveIconDrawableLayer);
                updateLayerFromTypedArray(layer, a);
                a.recycle();
            }
            Drawable d = layer.mDrawable;
            if (d != null && d.canApplyTheme()) {
                d.applyTheme(t);
                state.mChildrenChangingConfigurations |= d.getChangingConfigurations();
            }
        }
    }

    public int getSourceDrawableResId() {
        LayerState state = this.mLayerState;
        if (state == null) {
            return 0;
        }
        return state.mSourceDrawableId;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0081  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void inflateLayers(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        char c;
        int childIndex;
        ChildDrawable layer;
        int type;
        LayerState state = this.mLayerState;
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int type2 = parser.next();
            if (type2 != 1) {
                int depth = parser.getDepth();
                if (depth >= innerDepth || type2 != 3) {
                    if (type2 == 2 && depth <= innerDepth) {
                        String tagName = parser.getName();
                        switch (tagName.hashCode()) {
                            case -1905977571:
                                if (tagName.equals("monochrome")) {
                                    c = 2;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1332194002:
                                if (tagName.equals("background")) {
                                    c = 0;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1984457027:
                                if (tagName.equals("foreground")) {
                                    c = 1;
                                    break;
                                }
                                c = 65535;
                                break;
                            default:
                                c = 65535;
                                break;
                        }
                        switch (c) {
                            case 0:
                                childIndex = 0;
                                layer = new ChildDrawable(state.mDensity);
                                TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.AdaptiveIconDrawableLayer);
                                updateLayerFromTypedArray(layer, a);
                                a.recycle();
                                if (layer.mDrawable != null && layer.mThemeAttrs == null) {
                                    do {
                                        type = parser.next();
                                    } while (type == 4);
                                    if (type != 2) {
                                        throw new XmlPullParserException(parser.getPositionDescription() + ": <foreground> or <background> tag requires a 'drawable'attribute or child tag defining a drawable");
                                    }
                                    layer.mDrawable = Drawable.createFromXmlInnerForDensity(r, parser, attrs, this.mLayerState.mSrcDensityOverride, theme);
                                    layer.mDrawable.setCallback(this);
                                    state.mChildrenChangingConfigurations |= layer.mDrawable.getChangingConfigurations();
                                }
                                addLayer(childIndex, layer);
                                break;
                            case 1:
                                childIndex = 1;
                                layer = new ChildDrawable(state.mDensity);
                                TypedArray a2 = obtainAttributes(r, theme, attrs, C4057R.styleable.AdaptiveIconDrawableLayer);
                                updateLayerFromTypedArray(layer, a2);
                                a2.recycle();
                                if (layer.mDrawable != null) {
                                    break;
                                }
                                addLayer(childIndex, layer);
                                break;
                            case 2:
                                childIndex = 2;
                                layer = new ChildDrawable(state.mDensity);
                                TypedArray a22 = obtainAttributes(r, theme, attrs, C4057R.styleable.AdaptiveIconDrawableLayer);
                                updateLayerFromTypedArray(layer, a22);
                                a22.recycle();
                                if (layer.mDrawable != null) {
                                }
                                addLayer(childIndex, layer);
                                break;
                        }
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private void updateLayerFromTypedArray(ChildDrawable layer, TypedArray a) {
        LayerState state = this.mLayerState;
        state.mChildrenChangingConfigurations |= a.getChangingConfigurations();
        layer.mThemeAttrs = a.extractThemeAttrs();
        Drawable dr = a.getDrawableForDensity(0, state.mSrcDensityOverride);
        if (dr != null) {
            if (layer.mDrawable != null) {
                layer.mDrawable.setCallback(null);
            }
            layer.mDrawable = dr;
            layer.mDrawable.setCallback(this);
            state.mChildrenChangingConfigurations |= layer.mDrawable.getChangingConfigurations();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        LayerState layerState = this.mLayerState;
        return (layerState != null && layerState.canApplyTheme()) || super.canApplyTheme();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isProjected() {
        if (super.isProjected()) {
            return true;
        }
        ChildDrawable[] layers = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            if (layers[i].mDrawable != null && layers[i].mDrawable.isProjected()) {
                return true;
            }
        }
        return false;
    }

    private void suspendChildInvalidation() {
        this.mSuspendChildInvalidation = true;
    }

    private void resumeChildInvalidation() {
        this.mSuspendChildInvalidation = false;
        if (this.mChildRequestedInvalidation) {
            this.mChildRequestedInvalidation = false;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable who) {
        if (this.mSuspendChildInvalidation) {
            this.mChildRequestedInvalidation = true;
        } else {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mLayerState.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public void setHotspot(float x, float y) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null) {
                dr.setHotspot(x, y);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setHotspotBounds(int left, int top, int right, int bottom) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null) {
                dr.setHotspotBounds(left, top, right, bottom);
            }
        }
        Rect rect = this.mHotspotBounds;
        if (rect == null) {
            this.mHotspotBounds = new Rect(left, top, right, bottom);
        } else {
            rect.set(left, top, right, bottom);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void getHotspotBounds(Rect outRect) {
        Rect rect = this.mHotspotBounds;
        if (rect != null) {
            outRect.set(rect);
        } else {
            super.getHotspotBounds(outRect);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null) {
                dr.setVisible(visible, restart);
            }
        }
        return changed;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean dither) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null) {
                dr.setDither(dither);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mPaint.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null) {
                dr.setColorFilter(colorFilter);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList tint) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null) {
                dr.setTintList(tint);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintBlendMode(BlendMode blendMode) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null) {
                dr.setTintBlendMode(blendMode);
            }
        }
    }

    public void setOpacity(int opacity) {
        this.mLayerState.mOpacityOverride = opacity;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean mirrored) {
        this.mLayerState.mAutoMirrored = mirrored;
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null) {
                dr.setAutoMirrored(mirrored);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isAutoMirrored() {
        return this.mLayerState.mAutoMirrored;
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null) {
                dr.jumpToCurrentState();
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mLayerState.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean hasFocusStateSpecified() {
        return this.mLayerState.hasFocusStateSpecified();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onStateChange(int[] state) {
        boolean changed = false;
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null && dr.isStateful() && dr.setState(state)) {
                changed = true;
            }
        }
        if (changed) {
            updateLayerBounds(getBounds());
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        boolean changed = false;
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null && dr.setLevel(level)) {
                changed = true;
            }
        }
        if (changed) {
            updateLayerBounds(getBounds());
        }
        return changed;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return (int) (getMaxIntrinsicWidth() * DEFAULT_VIEW_PORT_SCALE);
    }

    private int getMaxIntrinsicWidth() {
        int w;
        int width = -1;
        int i = 0;
        while (true) {
            LayerState layerState = this.mLayerState;
            if (i < 3) {
                ChildDrawable r = layerState.mChildren[i];
                if (r.mDrawable != null && (w = r.mDrawable.getIntrinsicWidth()) > width) {
                    width = w;
                }
                i++;
            } else {
                return width;
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return (int) (getMaxIntrinsicHeight() * DEFAULT_VIEW_PORT_SCALE);
    }

    private int getMaxIntrinsicHeight() {
        int h;
        int height = -1;
        int i = 0;
        while (true) {
            LayerState layerState = this.mLayerState;
            if (i < 3) {
                ChildDrawable r = layerState.mChildren[i];
                if (r.mDrawable != null && (h = r.mDrawable.getIntrinsicHeight()) > height) {
                    height = h;
                }
                i++;
            } else {
                return height;
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (this.mLayerState.canConstantState()) {
            this.mLayerState.mChangingConfigurations = getChangingConfigurations();
            return this.mLayerState;
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mLayerState = createConstantState(this.mLayerState, null);
            int i = 0;
            while (true) {
                LayerState layerState = this.mLayerState;
                if (i >= 3) {
                    break;
                }
                Drawable dr = layerState.mChildren[i].mDrawable;
                if (dr != null) {
                    dr.mutate();
                }
                i++;
            }
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void clearMutated() {
        super.clearMutated();
        ChildDrawable[] array = this.mLayerState.mChildren;
        for (int i = 0; i < 3; i++) {
            Drawable dr = array[i].mDrawable;
            if (dr != null) {
                dr.clearMutated();
            }
        }
        this.mMutated = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ChildDrawable {
        public int mDensity;
        public Drawable mDrawable;
        public int[] mThemeAttrs;

        ChildDrawable(int density) {
            this.mDensity = 160;
            this.mDensity = density;
        }

        ChildDrawable(ChildDrawable orig, AdaptiveIconDrawable owner, Resources res) {
            Drawable clone;
            this.mDensity = 160;
            Drawable dr = orig.mDrawable;
            if (dr != null) {
                Drawable.ConstantState cs = dr.getConstantState();
                if (cs == null) {
                    clone = dr;
                } else if (res != null) {
                    clone = cs.newDrawable(res);
                } else {
                    clone = cs.newDrawable();
                }
                clone.setCallback(owner);
                clone.setBounds(dr.getBounds());
                clone.setLevel(dr.getLevel());
            } else {
                clone = null;
            }
            this.mDrawable = clone;
            this.mThemeAttrs = orig.mThemeAttrs;
            this.mDensity = Drawable.resolveDensity(res, orig.mDensity);
        }

        public boolean canApplyTheme() {
            Drawable drawable;
            return this.mThemeAttrs != null || ((drawable = this.mDrawable) != null && drawable.canApplyTheme());
        }

        public final void setDensity(int targetDensity) {
            if (this.mDensity != targetDensity) {
                this.mDensity = targetDensity;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class LayerState extends Drawable.ConstantState {
        static final int N_CHILDREN = 3;
        private boolean mAutoMirrored;
        int mChangingConfigurations;
        private boolean mCheckedOpacity;
        private boolean mCheckedStateful;
        ChildDrawable[] mChildren;
        int mChildrenChangingConfigurations;
        int mDensity;
        private boolean mIsStateful;
        private int mOpacity;
        int mOpacityOverride;
        int mSourceDrawableId;
        int mSrcDensityOverride;
        private int[] mThemeAttrs;

        LayerState(LayerState orig, AdaptiveIconDrawable owner, Resources res) {
            this.mSrcDensityOverride = 0;
            this.mOpacityOverride = 0;
            this.mSourceDrawableId = 0;
            this.mAutoMirrored = false;
            this.mDensity = Drawable.resolveDensity(res, orig != null ? orig.mDensity : 0);
            this.mChildren = new ChildDrawable[3];
            if (orig != null) {
                ChildDrawable[] origChildDrawable = orig.mChildren;
                this.mChangingConfigurations = orig.mChangingConfigurations;
                this.mChildrenChangingConfigurations = orig.mChildrenChangingConfigurations;
                this.mSourceDrawableId = orig.mSourceDrawableId;
                for (int i = 0; i < 3; i++) {
                    ChildDrawable or = origChildDrawable[i];
                    this.mChildren[i] = new ChildDrawable(or, owner, res);
                }
                this.mCheckedOpacity = orig.mCheckedOpacity;
                this.mOpacity = orig.mOpacity;
                this.mCheckedStateful = orig.mCheckedStateful;
                this.mIsStateful = orig.mIsStateful;
                this.mAutoMirrored = orig.mAutoMirrored;
                this.mThemeAttrs = orig.mThemeAttrs;
                this.mOpacityOverride = orig.mOpacityOverride;
                this.mSrcDensityOverride = orig.mSrcDensityOverride;
                return;
            }
            for (int i2 = 0; i2 < 3; i2++) {
                this.mChildren[i2] = new ChildDrawable(this.mDensity);
            }
        }

        public final void setDensity(int targetDensity) {
            if (this.mDensity != targetDensity) {
                this.mDensity = targetDensity;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            if (this.mThemeAttrs != null || super.canApplyTheme()) {
                return true;
            }
            ChildDrawable[] array = this.mChildren;
            for (int i = 0; i < 3; i++) {
                ChildDrawable layer = array[i];
                if (layer.canApplyTheme()) {
                    return true;
                }
            }
            return false;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new AdaptiveIconDrawable(this, (Resources) null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new AdaptiveIconDrawable(this, res);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations | this.mChildrenChangingConfigurations;
        }

        public final int getOpacity() {
            int op;
            if (this.mCheckedOpacity) {
                return this.mOpacity;
            }
            ChildDrawable[] array = this.mChildren;
            int firstIndex = -1;
            int i = 0;
            while (true) {
                if (i < 3) {
                    if (array[i].mDrawable == null) {
                        i++;
                    } else {
                        firstIndex = i;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (firstIndex >= 0) {
                op = array[firstIndex].mDrawable.getOpacity();
            } else {
                op = -2;
            }
            for (int i2 = firstIndex + 1; i2 < 3; i2++) {
                Drawable dr = array[i2].mDrawable;
                if (dr != null) {
                    op = Drawable.resolveOpacity(op, dr.getOpacity());
                }
            }
            this.mOpacity = op;
            this.mCheckedOpacity = true;
            return op;
        }

        public final boolean isStateful() {
            if (this.mCheckedStateful) {
                return this.mIsStateful;
            }
            ChildDrawable[] array = this.mChildren;
            boolean isStateful = false;
            int i = 0;
            while (true) {
                if (i < 3) {
                    Drawable dr = array[i].mDrawable;
                    if (dr == null || !dr.isStateful()) {
                        i++;
                    } else {
                        isStateful = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            this.mIsStateful = isStateful;
            this.mCheckedStateful = true;
            return isStateful;
        }

        public final boolean hasFocusStateSpecified() {
            ChildDrawable[] array = this.mChildren;
            for (int i = 0; i < 3; i++) {
                Drawable dr = array[i].mDrawable;
                if (dr != null && dr.hasFocusStateSpecified()) {
                    return true;
                }
            }
            return false;
        }

        public final boolean canConstantState() {
            ChildDrawable[] array = this.mChildren;
            for (int i = 0; i < 3; i++) {
                Drawable dr = array[i].mDrawable;
                if (dr != null && dr.getConstantState() == null) {
                    return false;
                }
            }
            return true;
        }

        public void invalidateCache() {
            this.mCheckedOpacity = false;
            this.mCheckedStateful = false;
        }
    }
}
