package android.view;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.DisplayUtils;
import android.util.Pair;
import android.util.RotationUtils;
import android.util.proto.ProtoOutputStream;
import android.view.CutoutSpecification;
import com.android.internal.C4057R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes4.dex */
public final class DisplayCutout {
    public static final int BOUNDS_POSITION_BOTTOM = 3;
    public static final int BOUNDS_POSITION_LEFT = 0;
    public static final int BOUNDS_POSITION_LENGTH = 4;
    public static final int BOUNDS_POSITION_RIGHT = 2;
    public static final int BOUNDS_POSITION_TOP = 1;
    private static final Object CACHE_LOCK;
    private static final CutoutPathParserInfo EMPTY_PARSER_INFO;
    public static final String EMULATION_OVERLAY_CATEGORY = "com.android.internal.display_cutout_emulation";
    public static final DisplayCutout NO_CUTOUT;
    private static final Pair<Path, DisplayCutout> NULL_PAIR;
    private static final String TAG = "DisplayCutout";
    private static final Rect ZERO_RECT;
    private static Pair<Path, DisplayCutout> sCachedCutout;
    private static Path sCachedCutoutPath;
    private static CutoutPathParserInfo sCachedCutoutPathParserInfo;
    private static float sCachedDensity;
    private static int sCachedDisplayHeight;
    private static int sCachedDisplayWidth;
    private static float sCachedPhysicalPixelDisplaySizeRatio;
    private static String sCachedSpec;
    private static Insets sCachedWaterfallInsets;
    private final Bounds mBounds;
    private final CutoutPathParserInfo mCutoutPathParserInfo;
    private final Rect mSafeInsets;
    private final Insets mWaterfallInsets;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface BoundsPosition {
    }

    static {
        Rect rect = new Rect();
        ZERO_RECT = rect;
        CutoutPathParserInfo cutoutPathParserInfo = new CutoutPathParserInfo(0, 0, 0, 0, 0.0f, "", 0, 0.0f, 0.0f);
        EMPTY_PARSER_INFO = cutoutPathParserInfo;
        NO_CUTOUT = new DisplayCutout(rect, Insets.NONE, rect, rect, rect, rect, cutoutPathParserInfo, false);
        Pair<Path, DisplayCutout> pair = new Pair<>(null, null);
        NULL_PAIR = pair;
        CACHE_LOCK = new Object();
        sCachedCutout = pair;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class Bounds {
        private final Rect[] mRects;

        private Bounds(Rect left, Rect top, Rect right, Rect bottom, boolean copyArguments) {
            this.mRects = r0;
            Rect[] rectArr = {DisplayCutout.getCopyOrRef(left, copyArguments), DisplayCutout.getCopyOrRef(top, copyArguments), DisplayCutout.getCopyOrRef(right, copyArguments), DisplayCutout.getCopyOrRef(bottom, copyArguments)};
        }

        private Bounds(Rect[] rects, boolean copyArguments) {
            if (rects.length != 4) {
                throw new IllegalArgumentException("rects must have exactly 4 elements: rects=" + Arrays.toString(rects));
            }
            if (copyArguments) {
                this.mRects = new Rect[4];
                for (int i = 0; i < 4; i++) {
                    this.mRects[i] = new Rect(rects[i]);
                }
                return;
            }
            for (Rect rect : rects) {
                if (rect == null) {
                    throw new IllegalArgumentException("rects must have non-null elements: rects=" + Arrays.toString(rects));
                }
            }
            this.mRects = rects;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isEmpty() {
            Rect[] rectArr;
            for (Rect rect : this.mRects) {
                if (!rect.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Rect getRect(int pos) {
            return new Rect(this.mRects[pos]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Rect[] getRects() {
            Rect[] rects = new Rect[4];
            for (int i = 0; i < 4; i++) {
                rects[i] = new Rect(this.mRects[i]);
            }
            return rects;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void scale(float scale) {
            for (int i = 0; i < 4; i++) {
                this.mRects[i].scale(scale);
            }
        }

        public int hashCode() {
            Rect[] rectArr;
            int result = 0;
            for (Rect rect : this.mRects) {
                result = (48271 * result) + rect.hashCode();
            }
            return result;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Bounds) {
                Bounds b = (Bounds) o;
                return Arrays.deepEquals(this.mRects, b.mRects);
            }
            return false;
        }

        public String toString() {
            return "Bounds=" + Arrays.toString(this.mRects);
        }
    }

    /* loaded from: classes4.dex */
    public static class CutoutPathParserInfo {
        private final String mCutoutSpec;
        private final float mDensity;
        private final int mDisplayHeight;
        private final int mDisplayWidth;
        private final int mPhysicalDisplayHeight;
        private final int mPhysicalDisplayWidth;
        private final float mPhysicalPixelDisplaySizeRatio;
        private final int mRotation;
        private final float mScale;

        public CutoutPathParserInfo(int displayWidth, int displayHeight, int physicalDisplayWidth, int physicalDisplayHeight, float density, String cutoutSpec, int rotation, float scale, float physicalPixelDisplaySizeRatio) {
            this.mDisplayWidth = displayWidth;
            this.mDisplayHeight = displayHeight;
            this.mPhysicalDisplayWidth = physicalDisplayWidth;
            this.mPhysicalDisplayHeight = physicalDisplayHeight;
            this.mDensity = density;
            this.mCutoutSpec = cutoutSpec == null ? "" : cutoutSpec;
            this.mRotation = rotation;
            this.mScale = scale;
            this.mPhysicalPixelDisplaySizeRatio = physicalPixelDisplaySizeRatio;
        }

        public CutoutPathParserInfo(CutoutPathParserInfo cutoutPathParserInfo) {
            this.mDisplayWidth = cutoutPathParserInfo.mDisplayWidth;
            this.mDisplayHeight = cutoutPathParserInfo.mDisplayHeight;
            this.mPhysicalDisplayWidth = cutoutPathParserInfo.mPhysicalDisplayWidth;
            this.mPhysicalDisplayHeight = cutoutPathParserInfo.mPhysicalDisplayHeight;
            this.mDensity = cutoutPathParserInfo.mDensity;
            this.mCutoutSpec = cutoutPathParserInfo.mCutoutSpec;
            this.mRotation = cutoutPathParserInfo.mRotation;
            this.mScale = cutoutPathParserInfo.mScale;
            this.mPhysicalPixelDisplaySizeRatio = cutoutPathParserInfo.mPhysicalPixelDisplaySizeRatio;
        }

        public int getDisplayWidth() {
            return this.mDisplayWidth;
        }

        public int getDisplayHeight() {
            return this.mDisplayHeight;
        }

        public int getPhysicalDisplayWidth() {
            return this.mPhysicalDisplayWidth;
        }

        public int getPhysicalDisplayHeight() {
            return this.mPhysicalDisplayHeight;
        }

        public float getDensity() {
            return this.mDensity;
        }

        public String getCutoutSpec() {
            return this.mCutoutSpec;
        }

        public int getRotation() {
            return this.mRotation;
        }

        public float getScale() {
            return this.mScale;
        }

        public float getPhysicalPixelDisplaySizeRatio() {
            return this.mPhysicalPixelDisplaySizeRatio;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean hasCutout() {
            return !this.mCutoutSpec.isEmpty();
        }

        public int hashCode() {
            int result = (0 * 48271) + Integer.hashCode(this.mDisplayWidth);
            return (48271 * ((((((((((((((result * 48271) + Integer.hashCode(this.mDisplayHeight)) * 48271) + Float.hashCode(this.mDensity)) * 48271) + this.mCutoutSpec.hashCode()) * 48271) + Integer.hashCode(this.mRotation)) * 48271) + Float.hashCode(this.mScale)) * 48271) + Float.hashCode(this.mPhysicalPixelDisplaySizeRatio)) * 48271) + Integer.hashCode(this.mPhysicalDisplayWidth))) + Integer.hashCode(this.mPhysicalDisplayHeight);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof CutoutPathParserInfo) {
                CutoutPathParserInfo c = (CutoutPathParserInfo) o;
                return this.mDisplayWidth == c.mDisplayWidth && this.mDisplayHeight == c.mDisplayHeight && this.mPhysicalDisplayWidth == c.mPhysicalDisplayWidth && this.mPhysicalDisplayHeight == c.mPhysicalDisplayHeight && this.mDensity == c.mDensity && this.mCutoutSpec.equals(c.mCutoutSpec) && this.mRotation == c.mRotation && this.mScale == c.mScale && this.mPhysicalPixelDisplaySizeRatio == c.mPhysicalPixelDisplaySizeRatio;
            }
            return false;
        }

        public String toString() {
            return "CutoutPathParserInfo{displayWidth=" + this.mDisplayWidth + " displayHeight=" + this.mDisplayHeight + " physicalDisplayWidth=" + this.mPhysicalDisplayWidth + " physicalDisplayHeight=" + this.mPhysicalDisplayHeight + " density={" + this.mDensity + "} cutoutSpec={" + this.mCutoutSpec + "} rotation={" + this.mRotation + "} scale={" + this.mScale + "} physicalPixelDisplaySizeRatio={" + this.mPhysicalPixelDisplaySizeRatio + "}}";
        }
    }

    public DisplayCutout(Insets safeInsets, Rect boundLeft, Rect boundTop, Rect boundRight, Rect boundBottom) {
        this(safeInsets.toRect(), Insets.NONE, boundLeft, boundTop, boundRight, boundBottom, null, true);
    }

    public DisplayCutout(Insets safeInsets, Rect boundLeft, Rect boundTop, Rect boundRight, Rect boundBottom, Insets waterfallInsets, CutoutPathParserInfo info) {
        this(safeInsets.toRect(), waterfallInsets, boundLeft, boundTop, boundRight, boundBottom, info, true);
    }

    public DisplayCutout(Insets safeInsets, Rect boundLeft, Rect boundTop, Rect boundRight, Rect boundBottom, Insets waterfallInsets) {
        this(safeInsets.toRect(), waterfallInsets, boundLeft, boundTop, boundRight, boundBottom, null, true);
    }

    @Deprecated
    public DisplayCutout(Rect safeInsets, List<Rect> boundingRects) {
        this(safeInsets, Insets.NONE, extractBoundsFromList(safeInsets, boundingRects), (CutoutPathParserInfo) null, true);
    }

    private DisplayCutout(Rect safeInsets, Insets waterfallInsets, Rect boundLeft, Rect boundTop, Rect boundRight, Rect boundBottom, CutoutPathParserInfo info, boolean copyArguments) {
        this.mSafeInsets = getCopyOrRef(safeInsets, copyArguments);
        this.mWaterfallInsets = waterfallInsets == null ? Insets.NONE : waterfallInsets;
        this.mBounds = new Bounds(boundLeft, boundTop, boundRight, boundBottom, copyArguments);
        this.mCutoutPathParserInfo = info == null ? EMPTY_PARSER_INFO : info;
    }

    private DisplayCutout(Rect safeInsets, Insets waterfallInsets, Rect[] bounds, CutoutPathParserInfo info, boolean copyArguments) {
        this.mSafeInsets = getCopyOrRef(safeInsets, copyArguments);
        this.mWaterfallInsets = waterfallInsets == null ? Insets.NONE : waterfallInsets;
        this.mBounds = new Bounds(bounds, copyArguments);
        this.mCutoutPathParserInfo = info == null ? EMPTY_PARSER_INFO : info;
    }

    private DisplayCutout(Rect safeInsets, Insets waterfallInsets, Bounds bounds, CutoutPathParserInfo info) {
        this.mSafeInsets = safeInsets;
        this.mWaterfallInsets = waterfallInsets == null ? Insets.NONE : waterfallInsets;
        this.mBounds = bounds;
        this.mCutoutPathParserInfo = info == null ? EMPTY_PARSER_INFO : info;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Rect getCopyOrRef(Rect r, boolean copyArguments) {
        if (r == null) {
            return ZERO_RECT;
        }
        if (copyArguments) {
            return new Rect(r);
        }
        return r;
    }

    public Insets getWaterfallInsets() {
        return this.mWaterfallInsets;
    }

    public static Rect[] extractBoundsFromList(Rect safeInsets, List<Rect> boundingRects) {
        Rect[] sortedBounds = new Rect[4];
        for (int i = 0; i < sortedBounds.length; i++) {
            sortedBounds[i] = ZERO_RECT;
        }
        if (safeInsets != null && boundingRects != null) {
            boolean topBottomInset = safeInsets.top > 0 || safeInsets.bottom > 0;
            for (Rect bound : boundingRects) {
                if (topBottomInset) {
                    if (bound.top == 0) {
                        sortedBounds[1] = bound;
                    } else {
                        sortedBounds[3] = bound;
                    }
                } else if (bound.left == 0) {
                    sortedBounds[0] = bound;
                } else {
                    sortedBounds[2] = bound;
                }
            }
        }
        return sortedBounds;
    }

    public boolean isBoundsEmpty() {
        return this.mBounds.isEmpty();
    }

    public boolean isEmpty() {
        return this.mSafeInsets.equals(ZERO_RECT);
    }

    public int getSafeInsetTop() {
        return this.mSafeInsets.top;
    }

    public int getSafeInsetBottom() {
        return this.mSafeInsets.bottom;
    }

    public int getSafeInsetLeft() {
        return this.mSafeInsets.left;
    }

    public int getSafeInsetRight() {
        return this.mSafeInsets.right;
    }

    public Rect getSafeInsets() {
        return new Rect(this.mSafeInsets);
    }

    public List<Rect> getBoundingRects() {
        Rect[] boundingRectsAll;
        List<Rect> result = new ArrayList<>();
        for (Rect bound : getBoundingRectsAll()) {
            if (!bound.isEmpty()) {
                result.add(new Rect(bound));
            }
        }
        return result;
    }

    public Rect[] getBoundingRectsAll() {
        return this.mBounds.getRects();
    }

    public Rect getBoundingRectLeft() {
        return this.mBounds.getRect(0);
    }

    public Rect getBoundingRectTop() {
        return this.mBounds.getRect(1);
    }

    public Rect getBoundingRectRight() {
        return this.mBounds.getRect(2);
    }

    public Rect getBoundingRectBottom() {
        return this.mBounds.getRect(3);
    }

    public Path getCutoutPath() {
        if (this.mCutoutPathParserInfo.hasCutout()) {
            Object obj = CACHE_LOCK;
            synchronized (obj) {
                if (this.mCutoutPathParserInfo.equals(sCachedCutoutPathParserInfo)) {
                    return sCachedCutoutPath;
                }
                CutoutSpecification cutoutSpec = new CutoutSpecification.Parser(this.mCutoutPathParserInfo.getDensity(), this.mCutoutPathParserInfo.getPhysicalDisplayWidth(), this.mCutoutPathParserInfo.getPhysicalDisplayHeight(), this.mCutoutPathParserInfo.getPhysicalPixelDisplaySizeRatio()).parse(this.mCutoutPathParserInfo.getCutoutSpec());
                Path cutoutPath = cutoutSpec.getPath();
                if (cutoutPath == null || cutoutPath.isEmpty()) {
                    return null;
                }
                Matrix matrix = new Matrix();
                if (this.mCutoutPathParserInfo.getRotation() != 0) {
                    RotationUtils.transformPhysicalToLogicalCoordinates(this.mCutoutPathParserInfo.getRotation(), this.mCutoutPathParserInfo.getDisplayWidth(), this.mCutoutPathParserInfo.getDisplayHeight(), matrix);
                }
                matrix.postScale(this.mCutoutPathParserInfo.getScale(), this.mCutoutPathParserInfo.getScale());
                cutoutPath.transform(matrix);
                synchronized (obj) {
                    sCachedCutoutPathParserInfo = new CutoutPathParserInfo(this.mCutoutPathParserInfo);
                    sCachedCutoutPath = cutoutPath;
                }
                return cutoutPath;
            }
        }
        return null;
    }

    public CutoutPathParserInfo getCutoutPathParserInfo() {
        return this.mCutoutPathParserInfo;
    }

    public int hashCode() {
        int result = (0 * 48271) + this.mSafeInsets.hashCode();
        return (48271 * ((((result * 48271) + this.mBounds.hashCode()) * 48271) + this.mWaterfallInsets.hashCode())) + this.mCutoutPathParserInfo.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DisplayCutout) {
            DisplayCutout c = (DisplayCutout) o;
            return this.mSafeInsets.equals(c.mSafeInsets) && this.mBounds.equals(c.mBounds) && this.mWaterfallInsets.equals(c.mWaterfallInsets) && this.mCutoutPathParserInfo.equals(c.mCutoutPathParserInfo);
        }
        return false;
    }

    public String toString() {
        return "DisplayCutout{insets=" + this.mSafeInsets + " waterfall=" + this.mWaterfallInsets + " boundingRect={" + this.mBounds + "} cutoutPathParserInfo={" + this.mCutoutPathParserInfo + "}}";
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        this.mSafeInsets.dumpDebug(proto, 1146756268033L);
        this.mBounds.getRect(0).dumpDebug(proto, 1146756268035L);
        this.mBounds.getRect(1).dumpDebug(proto, 1146756268036L);
        this.mBounds.getRect(2).dumpDebug(proto, 1146756268037L);
        this.mBounds.getRect(3).dumpDebug(proto, 1146756268038L);
        this.mWaterfallInsets.toRect().dumpDebug(proto, 1146756268039L);
        proto.end(token);
    }

    public DisplayCutout inset(int insetLeft, int insetTop, int insetRight, int insetBottom) {
        if ((insetLeft == 0 && insetTop == 0 && insetRight == 0 && insetBottom == 0) || (isBoundsEmpty() && this.mWaterfallInsets.equals(Insets.NONE))) {
            return this;
        }
        Rect safeInsets = insetInsets(insetLeft, insetTop, insetRight, insetBottom, new Rect(this.mSafeInsets));
        if (insetLeft == 0 && insetTop == 0 && this.mSafeInsets.equals(safeInsets)) {
            return this;
        }
        Rect waterfallInsets = insetInsets(insetLeft, insetTop, insetRight, insetBottom, this.mWaterfallInsets.toRect());
        Rect[] bounds = this.mBounds.getRects();
        for (int i = 0; i < bounds.length; i++) {
            if (!bounds[i].equals(ZERO_RECT)) {
                bounds[i].offset(-insetLeft, -insetTop);
            }
        }
        return new DisplayCutout(safeInsets, Insets.m185of(waterfallInsets), bounds, this.mCutoutPathParserInfo, false);
    }

    private Rect insetInsets(int insetLeft, int insetTop, int insetRight, int insetBottom, Rect insets) {
        if (insetTop > 0 || insets.top > 0) {
            insets.top = atLeastZero(insets.top - insetTop);
        }
        if (insetBottom > 0 || insets.bottom > 0) {
            insets.bottom = atLeastZero(insets.bottom - insetBottom);
        }
        if (insetLeft > 0 || insets.left > 0) {
            insets.left = atLeastZero(insets.left - insetLeft);
        }
        if (insetRight > 0 || insets.right > 0) {
            insets.right = atLeastZero(insets.right - insetRight);
        }
        return insets;
    }

    public DisplayCutout replaceSafeInsets(Rect safeInsets) {
        return new DisplayCutout(new Rect(safeInsets), this.mWaterfallInsets, this.mBounds, this.mCutoutPathParserInfo);
    }

    private static int atLeastZero(int value) {
        if (value < 0) {
            return 0;
        }
        return value;
    }

    public static DisplayCutout fromBoundingRect(int left, int top, int right, int bottom, int pos) {
        Rect[] bounds = new Rect[4];
        int i = 0;
        while (i < 4) {
            bounds[i] = pos == i ? new Rect(left, top, right, bottom) : new Rect();
            i++;
        }
        return new DisplayCutout(ZERO_RECT, Insets.NONE, bounds, (CutoutPathParserInfo) null, false);
    }

    public static DisplayCutout constructDisplayCutout(Rect[] bounds, Insets waterfallInsets, CutoutPathParserInfo info) {
        return new DisplayCutout(ZERO_RECT, waterfallInsets, bounds, info, false);
    }

    public static DisplayCutout fromBounds(Rect[] bounds) {
        return new DisplayCutout(ZERO_RECT, Insets.NONE, bounds, (CutoutPathParserInfo) null, false);
    }

    private static String getDisplayCutoutPath(Resources res, String displayUniqueId) {
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        String[] array = res.getStringArray(C4057R.array.config_displayCutoutPathArray);
        if (index >= 0 && index < array.length) {
            return array[index];
        }
        return res.getString(C4057R.string.config_mainBuiltInDisplayCutout);
    }

    private static String getDisplayCutoutApproximationRect(Resources res, String displayUniqueId) {
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        String[] array = res.getStringArray(C4057R.array.config_displayCutoutApproximationRectArray);
        if (index >= 0 && index < array.length) {
            return array[index];
        }
        return res.getString(C4057R.string.config_mainBuiltInDisplayCutoutRectApproximation);
    }

    public static boolean getMaskBuiltInDisplayCutout(Resources res, String displayUniqueId) {
        boolean maskCutout;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_maskBuiltInDisplayCutoutArray);
        if (index >= 0 && index < array.length()) {
            maskCutout = array.getBoolean(index, false);
        } else {
            maskCutout = res.getBoolean(C4057R.bool.config_maskMainBuiltInDisplayCutout);
        }
        array.recycle();
        return maskCutout;
    }

    public static boolean getFillBuiltInDisplayCutout(Resources res, String displayUniqueId) {
        boolean fillCutout;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_fillBuiltInDisplayCutoutArray);
        if (index >= 0 && index < array.length()) {
            fillCutout = array.getBoolean(index, false);
        } else {
            fillCutout = res.getBoolean(C4057R.bool.config_fillMainBuiltInDisplayCutout);
        }
        array.recycle();
        return fillCutout;
    }

    private static Insets getWaterfallInsets(Resources res, String displayUniqueId) {
        Insets insets;
        int index = DisplayUtils.getDisplayUniqueIdConfigIndex(res, displayUniqueId);
        TypedArray array = res.obtainTypedArray(C4057R.array.config_waterfallCutoutArray);
        if (index >= 0 && index < array.length() && array.getResourceId(index, 0) > 0) {
            int resourceId = array.getResourceId(index, 0);
            TypedArray waterfall = res.obtainTypedArray(resourceId);
            insets = Insets.m186of(waterfall.getDimensionPixelSize(0, 0), waterfall.getDimensionPixelSize(1, 0), waterfall.getDimensionPixelSize(2, 0), waterfall.getDimensionPixelSize(3, 0));
            waterfall.recycle();
        } else {
            insets = loadWaterfallInset(res);
        }
        array.recycle();
        return insets;
    }

    public static DisplayCutout fromResourcesRectApproximation(Resources res, String displayUniqueId, int physicalDisplayWidth, int physicalDisplayHeight, int displayWidth, int displayHeight) {
        return pathAndDisplayCutoutFromSpec(getDisplayCutoutPath(res, displayUniqueId), getDisplayCutoutApproximationRect(res, displayUniqueId), physicalDisplayWidth, physicalDisplayHeight, displayWidth, displayHeight, DisplayMetrics.DENSITY_DEVICE_STABLE / 160.0f, getWaterfallInsets(res, displayUniqueId)).second;
    }

    public static DisplayCutout fromSpec(String pathSpec, int displayWidth, int displayHeight, float density, Insets waterfallInsets) {
        return pathAndDisplayCutoutFromSpec(pathSpec, null, displayWidth, displayHeight, displayWidth, displayHeight, density, waterfallInsets).second;
    }

    private static Pair<Path, DisplayCutout> pathAndDisplayCutoutFromSpec(String pathSpec, String rectSpec, int physicalDisplayWidth, int physicalDisplayHeight, int displayWidth, int displayHeight, float density, Insets waterfallInsets) {
        String spec = rectSpec != null ? rectSpec : pathSpec;
        if (TextUtils.isEmpty(spec) && waterfallInsets.equals(Insets.NONE)) {
            return NULL_PAIR;
        }
        float physicalPixelDisplaySizeRatio = DisplayUtils.getPhysicalPixelDisplaySizeRatio(physicalDisplayWidth, physicalDisplayHeight, displayWidth, displayHeight);
        Object obj = CACHE_LOCK;
        synchronized (obj) {
            if (spec.equals(sCachedSpec) && sCachedDisplayWidth == displayWidth && sCachedDisplayHeight == displayHeight && sCachedDensity == density && waterfallInsets.equals(sCachedWaterfallInsets) && sCachedPhysicalPixelDisplaySizeRatio == physicalPixelDisplaySizeRatio) {
                return sCachedCutout;
            }
            String spec2 = spec.trim();
            CutoutSpecification cutoutSpec = new CutoutSpecification.Parser(density, physicalDisplayWidth, physicalDisplayHeight, physicalPixelDisplaySizeRatio).parse(spec2);
            Rect safeInset = cutoutSpec.getSafeInset();
            Rect boundLeft = cutoutSpec.getLeftBound();
            Rect boundTop = cutoutSpec.getTopBound();
            Rect boundRight = cutoutSpec.getRightBound();
            Rect boundBottom = cutoutSpec.getBottomBound();
            if (!waterfallInsets.equals(Insets.NONE)) {
                safeInset.set(Math.max(waterfallInsets.left, safeInset.left), Math.max(waterfallInsets.top, safeInset.top), Math.max(waterfallInsets.right, safeInset.right), Math.max(waterfallInsets.bottom, safeInset.bottom));
            }
            CutoutPathParserInfo cutoutPathParserInfo = new CutoutPathParserInfo(displayWidth, displayHeight, physicalDisplayWidth, physicalDisplayHeight, density, pathSpec.trim(), 0, 1.0f, physicalPixelDisplaySizeRatio);
            DisplayCutout cutout = new DisplayCutout(safeInset, waterfallInsets, boundLeft, boundTop, boundRight, boundBottom, cutoutPathParserInfo, false);
            Pair<Path, DisplayCutout> result = new Pair<>(cutoutSpec.getPath(), cutout);
            synchronized (obj) {
                sCachedSpec = spec2;
                sCachedDisplayWidth = displayWidth;
                sCachedDisplayHeight = displayHeight;
                sCachedDensity = density;
                sCachedCutout = result;
                sCachedWaterfallInsets = waterfallInsets;
                sCachedPhysicalPixelDisplaySizeRatio = physicalPixelDisplaySizeRatio;
            }
            return result;
        }
    }

    private static Insets loadWaterfallInset(Resources res) {
        return Insets.m186of(res.getDimensionPixelSize(C4057R.dimen.waterfall_display_left_edge_size), res.getDimensionPixelSize(C4057R.dimen.waterfall_display_top_edge_size), res.getDimensionPixelSize(C4057R.dimen.waterfall_display_right_edge_size), res.getDimensionPixelSize(C4057R.dimen.waterfall_display_bottom_edge_size));
    }

    public DisplayCutout getRotated(int startWidth, int startHeight, int fromRotation, int toRotation) {
        DisplayCutout displayCutout = NO_CUTOUT;
        if (this == displayCutout) {
            return displayCutout;
        }
        int rotation = RotationUtils.deltaRotation(fromRotation, toRotation);
        if (rotation == 0) {
            return this;
        }
        Insets waterfallInsets = RotationUtils.rotateInsets(getWaterfallInsets(), rotation);
        Rect[] newBounds = getBoundingRectsAll();
        Rect displayBounds = new Rect(0, 0, startWidth, startHeight);
        for (int i = 0; i < newBounds.length; i++) {
            if (!newBounds[i].isEmpty()) {
                RotationUtils.rotateBounds(newBounds[i], displayBounds, rotation);
            }
        }
        Collections.rotate(Arrays.asList(newBounds), -rotation);
        CutoutPathParserInfo info = getCutoutPathParserInfo();
        CutoutPathParserInfo newInfo = new CutoutPathParserInfo(info.getDisplayWidth(), info.getDisplayHeight(), info.getPhysicalDisplayWidth(), info.getPhysicalDisplayHeight(), info.getDensity(), info.getCutoutSpec(), toRotation, info.getScale(), info.getPhysicalPixelDisplaySizeRatio());
        boolean swapAspect = rotation % 2 != 0;
        int endWidth = swapAspect ? startHeight : startWidth;
        int endHeight = swapAspect ? startWidth : startHeight;
        DisplayCutout tmp = constructDisplayCutout(newBounds, waterfallInsets, newInfo);
        Rect safeInsets = computeSafeInsets(endWidth, endHeight, tmp);
        return tmp.replaceSafeInsets(safeInsets);
    }

    public static Rect computeSafeInsets(int displayW, int displayH, DisplayCutout cutout) {
        if (displayW == displayH) {
            throw new UnsupportedOperationException("not implemented: display=" + displayW + "x" + displayH + " cutout=" + cutout);
        }
        int leftInset = Math.max(cutout.getWaterfallInsets().left, findCutoutInsetForSide(displayW, displayH, cutout.getBoundingRectLeft(), 3));
        int topInset = Math.max(cutout.getWaterfallInsets().top, findCutoutInsetForSide(displayW, displayH, cutout.getBoundingRectTop(), 48));
        int rightInset = Math.max(cutout.getWaterfallInsets().right, findCutoutInsetForSide(displayW, displayH, cutout.getBoundingRectRight(), 5));
        int bottomInset = Math.max(cutout.getWaterfallInsets().bottom, findCutoutInsetForSide(displayW, displayH, cutout.getBoundingRectBottom(), 80));
        return new Rect(leftInset, topInset, rightInset, bottomInset);
    }

    private static int findCutoutInsetForSide(int displayW, int displayH, Rect boundingRect, int gravity) {
        if (boundingRect.isEmpty()) {
            return 0;
        }
        switch (gravity) {
            case 3:
                return Math.max(0, boundingRect.right);
            case 5:
                return Math.max(0, displayW - boundingRect.left);
            case 48:
                return Math.max(0, boundingRect.bottom);
            case 80:
                return Math.max(0, displayH - boundingRect.top);
            default:
                throw new IllegalArgumentException("unknown gravity: " + gravity);
        }
    }

    /* loaded from: classes4.dex */
    public static final class ParcelableWrapper implements Parcelable {
        public static final Parcelable.Creator<ParcelableWrapper> CREATOR = new Parcelable.Creator<ParcelableWrapper>() { // from class: android.view.DisplayCutout.ParcelableWrapper.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ParcelableWrapper createFromParcel(Parcel in) {
                return new ParcelableWrapper(ParcelableWrapper.readCutoutFromParcel(in));
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ParcelableWrapper[] newArray(int size) {
                return new ParcelableWrapper[size];
            }
        };
        private DisplayCutout mInner;

        public ParcelableWrapper() {
            this(DisplayCutout.NO_CUTOUT);
        }

        public ParcelableWrapper(DisplayCutout cutout) {
            this.mInner = cutout;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            writeCutoutToParcel(this.mInner, out, flags);
        }

        public static void writeCutoutToParcel(DisplayCutout cutout, Parcel out, int flags) {
            if (cutout == null) {
                out.writeInt(-1);
            } else if (cutout == DisplayCutout.NO_CUTOUT) {
                out.writeInt(0);
            } else {
                out.writeInt(1);
                out.writeTypedObject(cutout.mSafeInsets, flags);
                out.writeTypedArray(cutout.mBounds.getRects(), flags);
                out.writeTypedObject(cutout.mWaterfallInsets, flags);
                out.writeInt(cutout.mCutoutPathParserInfo.getDisplayWidth());
                out.writeInt(cutout.mCutoutPathParserInfo.getDisplayHeight());
                out.writeInt(cutout.mCutoutPathParserInfo.getPhysicalDisplayWidth());
                out.writeInt(cutout.mCutoutPathParserInfo.getPhysicalDisplayHeight());
                out.writeFloat(cutout.mCutoutPathParserInfo.getDensity());
                out.writeString(cutout.mCutoutPathParserInfo.getCutoutSpec());
                out.writeInt(cutout.mCutoutPathParserInfo.getRotation());
                out.writeFloat(cutout.mCutoutPathParserInfo.getScale());
                out.writeFloat(cutout.mCutoutPathParserInfo.getPhysicalPixelDisplaySizeRatio());
            }
        }

        public void readFromParcel(Parcel in) {
            this.mInner = readCutoutFromParcel(in);
        }

        public static DisplayCutout readCutoutFromParcel(Parcel in) {
            int variant = in.readInt();
            if (variant == -1) {
                return null;
            }
            if (variant != 0) {
                Rect safeInsets = (Rect) in.readTypedObject(Rect.CREATOR);
                Rect[] bounds = new Rect[4];
                in.readTypedArray(bounds, Rect.CREATOR);
                Insets waterfallInsets = (Insets) in.readTypedObject(Insets.CREATOR);
                int displayWidth = in.readInt();
                int displayHeight = in.readInt();
                int physicalDisplayWidth = in.readInt();
                int physicalDisplayHeight = in.readInt();
                float density = in.readFloat();
                String cutoutSpec = in.readString();
                int rotation = in.readInt();
                float scale = in.readFloat();
                float physicalPixelDisplaySizeRatio = in.readFloat();
                CutoutPathParserInfo info = new CutoutPathParserInfo(displayWidth, displayHeight, physicalDisplayWidth, physicalDisplayHeight, density, cutoutSpec, rotation, scale, physicalPixelDisplaySizeRatio);
                return new DisplayCutout(safeInsets, waterfallInsets, bounds, info, false);
            }
            return DisplayCutout.NO_CUTOUT;
        }

        public DisplayCutout get() {
            return this.mInner;
        }

        public void set(ParcelableWrapper cutout) {
            this.mInner = cutout.get();
        }

        public void set(DisplayCutout cutout) {
            this.mInner = cutout;
        }

        public void scale(float scale) {
            Rect safeInsets = this.mInner.getSafeInsets();
            safeInsets.scale(scale);
            Bounds bounds = new Bounds(this.mInner.mBounds.mRects, true);
            bounds.scale(scale);
            Rect waterfallInsets = this.mInner.mWaterfallInsets.toRect();
            waterfallInsets.scale(scale);
            CutoutPathParserInfo info = new CutoutPathParserInfo(this.mInner.mCutoutPathParserInfo.getDisplayWidth(), this.mInner.mCutoutPathParserInfo.getDisplayHeight(), this.mInner.mCutoutPathParserInfo.getPhysicalDisplayWidth(), this.mInner.mCutoutPathParserInfo.getPhysicalDisplayHeight(), this.mInner.mCutoutPathParserInfo.getDensity(), this.mInner.mCutoutPathParserInfo.getCutoutSpec(), this.mInner.mCutoutPathParserInfo.getRotation(), scale, this.mInner.mCutoutPathParserInfo.getPhysicalPixelDisplaySizeRatio());
            this.mInner = new DisplayCutout(safeInsets, Insets.m185of(waterfallInsets), bounds, info);
        }

        public int hashCode() {
            return this.mInner.hashCode();
        }

        public boolean equals(Object o) {
            return (o instanceof ParcelableWrapper) && this.mInner.equals(((ParcelableWrapper) o).mInner);
        }

        public String toString() {
            return String.valueOf(this.mInner);
        }
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private Path mCutoutPath;
        private Insets mSafeInsets = Insets.NONE;
        private Insets mWaterfallInsets = Insets.NONE;
        private final Rect mBoundingRectLeft = new Rect();
        private final Rect mBoundingRectTop = new Rect();
        private final Rect mBoundingRectRight = new Rect();
        private final Rect mBoundingRectBottom = new Rect();

        public DisplayCutout build() {
            CutoutPathParserInfo info;
            if (this.mCutoutPath != null) {
                info = new CutoutPathParserInfo(0, 0, 0, 0, 0.0f, "test", 0, 1.0f, 1.0f);
                synchronized (DisplayCutout.CACHE_LOCK) {
                    DisplayCutout.sCachedCutoutPathParserInfo = info;
                    DisplayCutout.sCachedCutoutPath = this.mCutoutPath;
                }
            } else {
                info = null;
            }
            return new DisplayCutout(this.mSafeInsets.toRect(), this.mWaterfallInsets, this.mBoundingRectLeft, this.mBoundingRectTop, this.mBoundingRectRight, this.mBoundingRectBottom, info, false);
        }

        public Builder setSafeInsets(Insets safeInsets) {
            this.mSafeInsets = safeInsets;
            return this;
        }

        public Builder setWaterfallInsets(Insets waterfallInsets) {
            this.mWaterfallInsets = waterfallInsets;
            return this;
        }

        public Builder setBoundingRectLeft(Rect boundingRectLeft) {
            this.mBoundingRectLeft.set(boundingRectLeft);
            return this;
        }

        public Builder setBoundingRectTop(Rect boundingRectTop) {
            this.mBoundingRectTop.set(boundingRectTop);
            return this;
        }

        public Builder setBoundingRectRight(Rect boundingRectRight) {
            this.mBoundingRectRight.set(boundingRectRight);
            return this;
        }

        public Builder setBoundingRectBottom(Rect boundingRectBottom) {
            this.mBoundingRectBottom.set(boundingRectBottom);
            return this;
        }

        public Builder setCutoutPath(Path cutoutPath) {
            this.mCutoutPath = cutoutPath;
            return this;
        }
    }
}
