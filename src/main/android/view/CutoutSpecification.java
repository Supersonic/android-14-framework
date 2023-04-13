package android.view;

import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.Log;
import android.util.PathParser;
import java.util.Objects;
/* loaded from: classes4.dex */
public class CutoutSpecification {
    private static final String BIND_LEFT_CUTOUT_MARKER = "@bind_left_cutout";
    private static final String BIND_RIGHT_CUTOUT_MARKER = "@bind_right_cutout";
    private static final String BOTTOM_MARKER = "@bottom";
    private static final String CENTER_VERTICAL_MARKER = "@center_vertical";
    private static final String CUTOUT_MARKER = "@cutout";
    private static final boolean DEBUG = false;
    private static final String DP_MARKER = "@dp";
    private static final String LEFT_MARKER = "@left";
    private static final char MARKER_START_CHAR = '@';
    private static final int MINIMAL_ACCEPTABLE_PATH_LENGTH = "H1V1Z".length();
    private static final String RIGHT_MARKER = "@right";
    private static final String TAG = "CutoutSpecification";
    private final Rect mBottomBound;
    private Insets mInsets;
    private final Rect mLeftBound;
    private final Path mPath;
    private final Rect mRightBound;
    private final Rect mTopBound;

    private CutoutSpecification(Parser parser) {
        this.mPath = parser.mPath;
        this.mLeftBound = parser.mLeftBound;
        this.mTopBound = parser.mTopBound;
        this.mRightBound = parser.mRightBound;
        this.mBottomBound = parser.mBottomBound;
        this.mInsets = parser.mInsets;
        applyPhysicalPixelDisplaySizeRatio(parser.mPhysicalPixelDisplaySizeRatio);
    }

    private void applyPhysicalPixelDisplaySizeRatio(float physicalPixelDisplaySizeRatio) {
        if (physicalPixelDisplaySizeRatio == 1.0f) {
            return;
        }
        Path path = this.mPath;
        if (path != null && !path.isEmpty()) {
            Matrix matrix = new Matrix();
            matrix.postScale(physicalPixelDisplaySizeRatio, physicalPixelDisplaySizeRatio);
            this.mPath.transform(matrix);
        }
        scaleBounds(this.mLeftBound, physicalPixelDisplaySizeRatio);
        scaleBounds(this.mTopBound, physicalPixelDisplaySizeRatio);
        scaleBounds(this.mRightBound, physicalPixelDisplaySizeRatio);
        scaleBounds(this.mBottomBound, physicalPixelDisplaySizeRatio);
        this.mInsets = scaleInsets(this.mInsets, physicalPixelDisplaySizeRatio);
    }

    private void scaleBounds(Rect r, float ratio) {
        if (r != null && !r.isEmpty()) {
            r.scale(ratio);
        }
    }

    private Insets scaleInsets(Insets insets, float ratio) {
        return Insets.m186of((int) ((insets.left * ratio) + 0.5f), (int) ((insets.top * ratio) + 0.5f), (int) ((insets.right * ratio) + 0.5f), (int) ((insets.bottom * ratio) + 0.5f));
    }

    public Path getPath() {
        return this.mPath;
    }

    public Rect getLeftBound() {
        return this.mLeftBound;
    }

    public Rect getTopBound() {
        return this.mTopBound;
    }

    public Rect getRightBound() {
        return this.mRightBound;
    }

    public Rect getBottomBound() {
        return this.mBottomBound;
    }

    public Rect getSafeInset() {
        return this.mInsets.toRect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int decideWhichEdge(boolean isTopEdgeShortEdge, boolean isShortEdge, boolean isStart) {
        return isTopEdgeShortEdge ? isShortEdge ? isStart ? 48 : 80 : isStart ? 3 : 5 : isShortEdge ? isStart ? 3 : 5 : isStart ? 48 : 80;
    }

    /* loaded from: classes4.dex */
    public static class Parser {
        private boolean mBindBottomCutout;
        private boolean mBindLeftCutout;
        private boolean mBindRightCutout;
        private Rect mBottomBound;
        private boolean mInDp;
        private Insets mInsets;
        private boolean mIsCloserToStartSide;
        private final boolean mIsShortEdgeOnTop;
        private boolean mIsTouchShortEdgeEnd;
        private boolean mIsTouchShortEdgeStart;
        private Rect mLeftBound;
        private final Matrix mMatrix;
        private Path mPath;
        private final int mPhysicalDisplayHeight;
        private final int mPhysicalDisplayWidth;
        private final float mPhysicalPixelDisplaySizeRatio;
        private boolean mPositionFromBottom;
        private boolean mPositionFromCenterVertical;
        private boolean mPositionFromLeft;
        private boolean mPositionFromRight;
        private Rect mRightBound;
        private int mSafeInsetBottom;
        private int mSafeInsetLeft;
        private int mSafeInsetRight;
        private int mSafeInsetTop;
        private final float mStableDensity;
        private final Rect mTmpRect;
        private final RectF mTmpRectF;
        private Rect mTopBound;

        public Parser(float stableDensity, int physicalDisplayWidth, int physicalDisplayHeight) {
            this(stableDensity, physicalDisplayWidth, physicalDisplayHeight, 1.0f);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Parser(float stableDensity, int physicalDisplayWidth, int physicalDisplayHeight, float physicalPixelDisplaySizeRatio) {
            this.mTmpRect = new Rect();
            this.mTmpRectF = new RectF();
            this.mPositionFromLeft = false;
            this.mPositionFromRight = false;
            this.mPositionFromBottom = false;
            this.mPositionFromCenterVertical = false;
            this.mBindLeftCutout = false;
            this.mBindRightCutout = false;
            this.mBindBottomCutout = false;
            this.mStableDensity = stableDensity;
            this.mPhysicalDisplayWidth = physicalDisplayWidth;
            this.mPhysicalDisplayHeight = physicalDisplayHeight;
            this.mPhysicalPixelDisplaySizeRatio = physicalPixelDisplaySizeRatio;
            this.mMatrix = new Matrix();
            this.mIsShortEdgeOnTop = physicalDisplayWidth < physicalDisplayHeight;
        }

        private void computeBoundsRectAndAddToRegion(Path p, Region inoutRegion, Rect inoutRect) {
            this.mTmpRectF.setEmpty();
            p.computeBounds(this.mTmpRectF, false);
            this.mTmpRectF.round(inoutRect);
            inoutRegion.m179op(inoutRect, Region.EnumC0813Op.UNION);
        }

        private void resetStatus(StringBuilder sb) {
            sb.setLength(0);
            this.mPositionFromBottom = false;
            this.mPositionFromLeft = false;
            this.mPositionFromRight = false;
            this.mPositionFromCenterVertical = false;
            this.mBindLeftCutout = false;
            this.mBindRightCutout = false;
            this.mBindBottomCutout = false;
        }

        private void translateMatrix() {
            float offsetX;
            float offsetY;
            if (this.mPositionFromRight) {
                offsetX = this.mPhysicalDisplayWidth;
            } else if (this.mPositionFromLeft) {
                offsetX = 0.0f;
            } else {
                offsetX = this.mPhysicalDisplayWidth / 2.0f;
            }
            if (this.mPositionFromBottom) {
                offsetY = this.mPhysicalDisplayHeight;
            } else if (this.mPositionFromCenterVertical) {
                offsetY = this.mPhysicalDisplayHeight / 2.0f;
            } else {
                offsetY = 0.0f;
            }
            this.mMatrix.reset();
            if (this.mInDp) {
                Matrix matrix = this.mMatrix;
                float f = this.mStableDensity;
                matrix.postScale(f, f);
            }
            this.mMatrix.postTranslate(offsetX, offsetY);
        }

        private int computeSafeInsets(int gravity, Rect rect) {
            if (gravity == 3 && rect.right > 0 && rect.right < this.mPhysicalDisplayWidth) {
                return rect.right;
            }
            if (gravity == 48 && rect.bottom > 0 && rect.bottom < this.mPhysicalDisplayHeight) {
                return rect.bottom;
            }
            if (gravity == 5 && rect.left > 0) {
                int i = rect.left;
                int i2 = this.mPhysicalDisplayWidth;
                if (i < i2) {
                    return i2 - rect.left;
                }
            }
            if (gravity != 80 || rect.top <= 0) {
                return 0;
            }
            int i3 = rect.top;
            int i4 = this.mPhysicalDisplayHeight;
            if (i3 < i4) {
                return i4 - rect.top;
            }
            return 0;
        }

        private void setSafeInset(int gravity, int inset) {
            if (gravity == 3) {
                this.mSafeInsetLeft = inset;
            } else if (gravity == 48) {
                this.mSafeInsetTop = inset;
            } else if (gravity == 5) {
                this.mSafeInsetRight = inset;
            } else if (gravity == 80) {
                this.mSafeInsetBottom = inset;
            }
        }

        private int getSafeInset(int gravity) {
            if (gravity == 3) {
                return this.mSafeInsetLeft;
            }
            if (gravity == 48) {
                return this.mSafeInsetTop;
            }
            if (gravity == 5) {
                return this.mSafeInsetRight;
            }
            if (gravity == 80) {
                return this.mSafeInsetBottom;
            }
            return 0;
        }

        private Rect onSetEdgeCutout(boolean isStart, boolean isShortEdge, Rect rect) {
            int gravity;
            if (isShortEdge) {
                gravity = CutoutSpecification.decideWhichEdge(this.mIsShortEdgeOnTop, true, isStart);
            } else {
                boolean z = this.mIsTouchShortEdgeStart;
                if (z && this.mIsTouchShortEdgeEnd) {
                    gravity = CutoutSpecification.decideWhichEdge(this.mIsShortEdgeOnTop, false, isStart);
                } else {
                    gravity = (z || this.mIsTouchShortEdgeEnd) ? CutoutSpecification.decideWhichEdge(this.mIsShortEdgeOnTop, true, this.mIsCloserToStartSide) : CutoutSpecification.decideWhichEdge(this.mIsShortEdgeOnTop, isShortEdge, isStart);
                }
            }
            int oldSafeInset = getSafeInset(gravity);
            int newSafeInset = computeSafeInsets(gravity, rect);
            if (oldSafeInset < newSafeInset) {
                setSafeInset(gravity, newSafeInset);
            }
            return new Rect(rect);
        }

        private void setEdgeCutout(Path newPath) {
            boolean z = this.mBindRightCutout;
            if (z && this.mRightBound == null) {
                this.mRightBound = onSetEdgeCutout(false, !this.mIsShortEdgeOnTop, this.mTmpRect);
            } else {
                boolean z2 = this.mBindLeftCutout;
                if (z2 && this.mLeftBound == null) {
                    this.mLeftBound = onSetEdgeCutout(true, !this.mIsShortEdgeOnTop, this.mTmpRect);
                } else {
                    boolean z3 = this.mBindBottomCutout;
                    if (z3 && this.mBottomBound == null) {
                        this.mBottomBound = onSetEdgeCutout(false, this.mIsShortEdgeOnTop, this.mTmpRect);
                    } else if (!z3 && !z2 && !z && this.mTopBound == null) {
                        this.mTopBound = onSetEdgeCutout(true, this.mIsShortEdgeOnTop, this.mTmpRect);
                    } else {
                        return;
                    }
                }
            }
            Path path = this.mPath;
            if (path != null) {
                path.addPath(newPath);
            } else {
                this.mPath = newPath;
            }
        }

        private void parseSvgPathSpec(Region region, String spec) {
            if (TextUtils.length(spec) < CutoutSpecification.MINIMAL_ACCEPTABLE_PATH_LENGTH) {
                Log.m110e(CutoutSpecification.TAG, "According to SVG definition, it shouldn't happen");
                return;
            }
            translateMatrix();
            Path newPath = PathParser.createPathFromPathData(spec);
            newPath.transform(this.mMatrix);
            computeBoundsRectAndAddToRegion(newPath, region, this.mTmpRect);
            if (this.mTmpRect.isEmpty()) {
                return;
            }
            if (this.mIsShortEdgeOnTop) {
                this.mIsTouchShortEdgeStart = this.mTmpRect.top <= 0;
                this.mIsTouchShortEdgeEnd = this.mTmpRect.bottom >= this.mPhysicalDisplayHeight;
                this.mIsCloserToStartSide = this.mTmpRect.centerY() < this.mPhysicalDisplayHeight / 2;
            } else {
                this.mIsTouchShortEdgeStart = this.mTmpRect.left <= 0;
                this.mIsTouchShortEdgeEnd = this.mTmpRect.right >= this.mPhysicalDisplayWidth;
                this.mIsCloserToStartSide = this.mTmpRect.centerX() < this.mPhysicalDisplayWidth / 2;
            }
            setEdgeCutout(newPath);
        }

        private void parseSpecWithoutDp(String specWithoutDp) {
            int currentIndex;
            Region region = Region.obtain();
            StringBuilder sb = null;
            int lastIndex = 0;
            while (true) {
                int currentIndex2 = specWithoutDp.indexOf(64, lastIndex);
                if (currentIndex2 == -1) {
                    break;
                }
                if (sb == null) {
                    sb = new StringBuilder(specWithoutDp.length());
                }
                sb.append((CharSequence) specWithoutDp, lastIndex, currentIndex2);
                if (specWithoutDp.startsWith(CutoutSpecification.LEFT_MARKER, currentIndex2)) {
                    if (!this.mPositionFromRight) {
                        this.mPositionFromLeft = true;
                    }
                    currentIndex = currentIndex2 + CutoutSpecification.LEFT_MARKER.length();
                } else if (specWithoutDp.startsWith(CutoutSpecification.RIGHT_MARKER, currentIndex2)) {
                    if (!this.mPositionFromLeft) {
                        this.mPositionFromRight = true;
                    }
                    currentIndex = currentIndex2 + CutoutSpecification.RIGHT_MARKER.length();
                } else if (specWithoutDp.startsWith(CutoutSpecification.BOTTOM_MARKER, currentIndex2)) {
                    parseSvgPathSpec(region, sb.toString());
                    currentIndex = currentIndex2 + CutoutSpecification.BOTTOM_MARKER.length();
                    resetStatus(sb);
                    this.mBindBottomCutout = true;
                    this.mPositionFromBottom = true;
                } else if (specWithoutDp.startsWith(CutoutSpecification.CENTER_VERTICAL_MARKER, currentIndex2)) {
                    parseSvgPathSpec(region, sb.toString());
                    currentIndex = currentIndex2 + CutoutSpecification.CENTER_VERTICAL_MARKER.length();
                    resetStatus(sb);
                    this.mPositionFromCenterVertical = true;
                } else if (specWithoutDp.startsWith(CutoutSpecification.CUTOUT_MARKER, currentIndex2)) {
                    parseSvgPathSpec(region, sb.toString());
                    currentIndex = currentIndex2 + CutoutSpecification.CUTOUT_MARKER.length();
                    resetStatus(sb);
                } else if (specWithoutDp.startsWith(CutoutSpecification.BIND_LEFT_CUTOUT_MARKER, currentIndex2)) {
                    this.mBindBottomCutout = false;
                    this.mBindRightCutout = false;
                    this.mBindLeftCutout = true;
                    currentIndex = currentIndex2 + CutoutSpecification.BIND_LEFT_CUTOUT_MARKER.length();
                } else if (specWithoutDp.startsWith(CutoutSpecification.BIND_RIGHT_CUTOUT_MARKER, currentIndex2)) {
                    this.mBindBottomCutout = false;
                    this.mBindLeftCutout = false;
                    this.mBindRightCutout = true;
                    currentIndex = currentIndex2 + CutoutSpecification.BIND_RIGHT_CUTOUT_MARKER.length();
                } else {
                    currentIndex = currentIndex2 + 1;
                }
                lastIndex = currentIndex;
            }
            if (sb == null) {
                parseSvgPathSpec(region, specWithoutDp);
            } else {
                sb.append((CharSequence) specWithoutDp, lastIndex, specWithoutDp.length());
                parseSvgPathSpec(region, sb.toString());
            }
            region.recycle();
        }

        public CutoutSpecification parse(String originalSpec) {
            String spec;
            Objects.requireNonNull(originalSpec);
            int dpIndex = originalSpec.lastIndexOf(CutoutSpecification.DP_MARKER);
            this.mInDp = dpIndex != -1;
            if (dpIndex != -1) {
                spec = originalSpec.substring(0, dpIndex) + originalSpec.substring(CutoutSpecification.DP_MARKER.length() + dpIndex);
            } else {
                spec = originalSpec;
            }
            parseSpecWithoutDp(spec);
            this.mInsets = Insets.m186of(this.mSafeInsetLeft, this.mSafeInsetTop, this.mSafeInsetRight, this.mSafeInsetBottom);
            return new CutoutSpecification(this);
        }
    }
}
