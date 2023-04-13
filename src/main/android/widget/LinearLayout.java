package android.widget;

import android.app.slice.Slice;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.TtmlUtils;
import android.security.keystore.KeyProperties;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View$InspectionCompanion$$ExternalSyntheticLambda0;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@RemoteViews.RemoteView
/* loaded from: classes4.dex */
public class LinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_FILL = 3;
    private static final int INDEX_TOP = 1;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private static boolean sCompatibilityDone = false;
    private static boolean sRemeasureWeightedChildren = true;
    private final boolean mAllowInconsistentMeasurement;
    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private boolean mBaselineAligned;
    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private int mBaselineAlignedChildIndex;
    @ViewDebug.ExportedProperty(category = "measurement")
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;
    @ViewDebug.ExportedProperty(category = "measurement", flagMapping = {@ViewDebug.FlagToString(equals = -1, mask = -1, name = KeyProperties.DIGEST_NONE), @ViewDebug.FlagToString(equals = 0, mask = 0, name = KeyProperties.DIGEST_NONE), @ViewDebug.FlagToString(equals = 48, mask = 48, name = "TOP"), @ViewDebug.FlagToString(equals = 80, mask = 80, name = "BOTTOM"), @ViewDebug.FlagToString(equals = 3, mask = 3, name = "LEFT"), @ViewDebug.FlagToString(equals = 5, mask = 5, name = "RIGHT"), @ViewDebug.FlagToString(equals = Gravity.START, mask = Gravity.START, name = "START"), @ViewDebug.FlagToString(equals = Gravity.END, mask = Gravity.END, name = "END"), @ViewDebug.FlagToString(equals = 16, mask = 16, name = "CENTER_VERTICAL"), @ViewDebug.FlagToString(equals = 112, mask = 112, name = "FILL_VERTICAL"), @ViewDebug.FlagToString(equals = 1, mask = 1, name = "CENTER_HORIZONTAL"), @ViewDebug.FlagToString(equals = 7, mask = 7, name = "FILL_HORIZONTAL"), @ViewDebug.FlagToString(equals = 17, mask = 17, name = "CENTER"), @ViewDebug.FlagToString(equals = 119, mask = 119, name = "FILL"), @ViewDebug.FlagToString(equals = 8388608, mask = 8388608, name = "RELATIVE")}, formatToHexString = true)
    private int mGravity;
    private int mLayoutDirection;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    @ViewDebug.ExportedProperty(category = "measurement")
    private int mOrientation;
    private int mShowDividers;
    @ViewDebug.ExportedProperty(category = "measurement")
    private int mTotalLength;
    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private boolean mUseLargestChild;
    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private float mWeightSum;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface DividerMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface OrientationMode {
    }

    /* loaded from: classes4.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT, mapping = {@ViewDebug.IntToString(from = -1, m86to = KeyProperties.DIGEST_NONE), @ViewDebug.IntToString(from = 0, m86to = KeyProperties.DIGEST_NONE), @ViewDebug.IntToString(from = 48, m86to = "TOP"), @ViewDebug.IntToString(from = 80, m86to = "BOTTOM"), @ViewDebug.IntToString(from = 3, m86to = "LEFT"), @ViewDebug.IntToString(from = 5, m86to = "RIGHT"), @ViewDebug.IntToString(from = Gravity.START, m86to = "START"), @ViewDebug.IntToString(from = Gravity.END, m86to = "END"), @ViewDebug.IntToString(from = 16, m86to = "CENTER_VERTICAL"), @ViewDebug.IntToString(from = 112, m86to = "FILL_VERTICAL"), @ViewDebug.IntToString(from = 1, m86to = "CENTER_HORIZONTAL"), @ViewDebug.IntToString(from = 7, m86to = "FILL_HORIZONTAL"), @ViewDebug.IntToString(from = 17, m86to = "CENTER"), @ViewDebug.IntToString(from = 119, m86to = "FILL")})
        public int gravity;
        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public float weight;

        /* loaded from: classes4.dex */
        public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<LayoutParams> {
            private int mLayout_gravityId;
            private int mLayout_weightId;
            private boolean mPropertiesMapped = false;

            @Override // android.view.inspector.InspectionCompanion
            public void mapProperties(PropertyMapper propertyMapper) {
                this.mLayout_gravityId = propertyMapper.mapGravity("layout_gravity", 16842931);
                this.mLayout_weightId = propertyMapper.mapFloat("layout_weight", 16843137);
                this.mPropertiesMapped = true;
            }

            @Override // android.view.inspector.InspectionCompanion
            public void readProperties(LayoutParams node, PropertyReader propertyReader) {
                if (!this.mPropertiesMapped) {
                    throw new InspectionCompanion.UninitializedPropertyMapException();
                }
                propertyReader.readGravity(this.mLayout_gravityId, node.gravity);
                propertyReader.readFloat(this.mLayout_weightId, node.weight);
            }
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.gravity = -1;
            TypedArray a = c.obtainStyledAttributes(attrs, C4057R.styleable.LinearLayout_Layout);
            this.weight = a.getFloat(3, 0.0f);
            this.gravity = a.getInt(0, -1);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.gravity = -1;
            this.weight = 0.0f;
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.gravity = -1;
            this.weight = weight;
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
            this.gravity = -1;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.gravity = -1;
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.gravity = -1;
            this.weight = source.weight;
            this.gravity = source.gravity;
        }

        @Override // android.view.ViewGroup.LayoutParams
        public String debug(String output) {
            return output + "LinearLayout.LayoutParams={width=" + sizeToString(this.width) + ", height=" + sizeToString(this.height) + " weight=" + this.weight + "}";
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        public void encodeProperties(ViewHierarchyEncoder encoder) {
            super.encodeProperties(encoder);
            encoder.addProperty("layout:weight", this.weight);
            encoder.addProperty("layout:gravity", this.gravity);
        }
    }

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<LinearLayout> {
        private int mBaselineAlignedChildIndexId;
        private int mBaselineAlignedId;
        private int mDividerId;
        private int mGravityId;
        private int mMeasureWithLargestChildId;
        private int mOrientationId;
        private boolean mPropertiesMapped = false;
        private int mWeightSumId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mBaselineAlignedId = propertyMapper.mapBoolean("baselineAligned", 16843046);
            this.mBaselineAlignedChildIndexId = propertyMapper.mapInt("baselineAlignedChildIndex", 16843047);
            this.mDividerId = propertyMapper.mapObject("divider", 16843049);
            this.mGravityId = propertyMapper.mapGravity("gravity", 16842927);
            this.mMeasureWithLargestChildId = propertyMapper.mapBoolean("measureWithLargestChild", 16843476);
            SparseArray<String> orientationEnumMapping = new SparseArray<>();
            orientationEnumMapping.put(0, Slice.HINT_HORIZONTAL);
            orientationEnumMapping.put(1, "vertical");
            Objects.requireNonNull(orientationEnumMapping);
            this.mOrientationId = propertyMapper.mapIntEnum("orientation", 16842948, new View$InspectionCompanion$$ExternalSyntheticLambda0(orientationEnumMapping));
            this.mWeightSumId = propertyMapper.mapFloat("weightSum", 16843048);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(LinearLayout node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readBoolean(this.mBaselineAlignedId, node.isBaselineAligned());
            propertyReader.readInt(this.mBaselineAlignedChildIndexId, node.getBaselineAlignedChildIndex());
            propertyReader.readObject(this.mDividerId, node.getDividerDrawable());
            propertyReader.readGravity(this.mGravityId, node.getGravity());
            propertyReader.readBoolean(this.mMeasureWithLargestChildId, node.isMeasureWithLargestChildEnabled());
            propertyReader.readIntEnum(this.mOrientationId, node.getOrientation());
            propertyReader.readFloat(this.mWeightSumId, node.getWeightSum());
        }
    }

    public LinearLayout(Context context) {
        this(context, null);
    }

    public LinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        boolean z;
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        this.mLayoutDirection = -1;
        if (!sCompatibilityDone && context != null) {
            int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
            if (targetSdkVersion >= 28) {
                z = true;
            } else {
                z = false;
            }
            sRemeasureWeightedChildren = z;
            sCompatibilityDone = true;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.LinearLayout, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.LinearLayout, attrs, a, defStyleAttr, defStyleRes);
        int index = a.getInt(1, -1);
        if (index >= 0) {
            setOrientation(index);
        }
        int index2 = a.getInt(0, -1);
        if (index2 >= 0) {
            setGravity(index2);
        }
        boolean baselineAligned = a.getBoolean(2, true);
        if (!baselineAligned) {
            setBaselineAligned(baselineAligned);
        }
        this.mWeightSum = a.getFloat(4, -1.0f);
        this.mBaselineAlignedChildIndex = a.getInt(3, -1);
        this.mUseLargestChild = a.getBoolean(6, false);
        this.mShowDividers = a.getInt(7, 0);
        this.mDividerPadding = a.getDimensionPixelSize(8, 0);
        setDividerDrawable(a.getDrawable(5));
        int version = context.getApplicationInfo().targetSdkVersion;
        this.mAllowInconsistentMeasurement = version <= 23;
        a.recycle();
    }

    private boolean isShowingDividers() {
        return (this.mShowDividers == 0 || this.mDivider == null) ? false : true;
    }

    public void setShowDividers(int showDividers) {
        if (showDividers == this.mShowDividers) {
            return;
        }
        this.mShowDividers = showDividers;
        setWillNotDraw(!isShowingDividers());
        requestLayout();
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public void setDividerDrawable(Drawable divider) {
        if (divider == this.mDivider) {
            return;
        }
        this.mDivider = divider;
        if (divider != null) {
            this.mDividerWidth = divider.getIntrinsicWidth();
            this.mDividerHeight = divider.getIntrinsicHeight();
        } else {
            this.mDividerWidth = 0;
            this.mDividerHeight = 0;
        }
        setWillNotDraw(!isShowingDividers());
        requestLayout();
    }

    public void setDividerPadding(int padding) {
        if (padding == this.mDividerPadding) {
            return;
        }
        this.mDividerPadding = padding;
        if (isShowingDividers()) {
            requestLayout();
            invalidate();
        }
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.mDivider == null) {
            return;
        }
        if (this.mOrientation == 1) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
        }
    }

    void drawDividersVertical(Canvas canvas) {
        int bottom;
        int count = getVirtualChildCount();
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int top = (child.getTop() - lp.topMargin) - this.mDividerHeight;
                drawHorizontalDivider(canvas, top);
            }
        }
        if (hasDividerBeforeChildAt(count)) {
            View child2 = getLastNonGoneChild();
            if (child2 == null) {
                bottom = (getHeight() - getPaddingBottom()) - this.mDividerHeight;
            } else {
                LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                int bottom2 = child2.getBottom() + lp2.bottomMargin;
                bottom = bottom2;
            }
            drawHorizontalDivider(canvas, bottom);
        }
    }

    private View getLastNonGoneChild() {
        for (int i = getVirtualChildCount() - 1; i >= 0; i--) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                return child;
            }
        }
        return null;
    }

    void drawDividersHorizontal(Canvas canvas) {
        int position;
        int position2;
        int count = getVirtualChildCount();
        boolean isLayoutRtl = isLayoutRtl();
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (isLayoutRtl) {
                    position2 = child.getRight() + lp.rightMargin;
                } else {
                    int position3 = child.getLeft();
                    position2 = (position3 - lp.leftMargin) - this.mDividerWidth;
                }
                drawVerticalDivider(canvas, position2);
            }
        }
        if (hasDividerBeforeChildAt(count)) {
            View child2 = getLastNonGoneChild();
            if (child2 == null) {
                if (isLayoutRtl) {
                    position = getPaddingLeft();
                } else {
                    int position4 = getWidth();
                    position = (position4 - getPaddingRight()) - this.mDividerWidth;
                }
            } else {
                LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                if (isLayoutRtl) {
                    position = (child2.getLeft() - lp2.leftMargin) - this.mDividerWidth;
                } else {
                    int position5 = child2.getRight();
                    position = position5 + lp2.rightMargin;
                }
            }
            drawVerticalDivider(canvas, position);
        }
    }

    void drawHorizontalDivider(Canvas canvas, int top) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, top, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + top);
        this.mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int left) {
        this.mDivider.setBounds(left, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + left, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }

    @RemotableViewMethod
    public void setBaselineAligned(boolean baselineAligned) {
        this.mBaselineAligned = baselineAligned;
    }

    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }

    @RemotableViewMethod
    public void setMeasureWithLargestChildEnabled(boolean enabled) {
        this.mUseLargestChild = enabled;
    }

    @Override // android.view.View
    public int getBaseline() {
        int majorGravity;
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        int childCount = getChildCount();
        int i = this.mBaselineAlignedChildIndex;
        if (childCount <= i) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
        View child = getChildAt(i);
        int childBaseline = child.getBaseline();
        if (childBaseline == -1) {
            if (this.mBaselineAlignedChildIndex == 0) {
                return -1;
            }
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
        }
        int childTop = this.mBaselineChildTop;
        if (this.mOrientation == 1 && (majorGravity = this.mGravity & 112) != 48) {
            switch (majorGravity) {
                case 16:
                    childTop += ((((this.mBottom - this.mTop) - this.mPaddingTop) - this.mPaddingBottom) - this.mTotalLength) / 2;
                    break;
                case 80:
                    childTop = ((this.mBottom - this.mTop) - this.mPaddingBottom) - this.mTotalLength;
                    break;
            }
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return lp.topMargin + childTop + childBaseline;
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    @RemotableViewMethod
    public void setBaselineAlignedChildIndex(int i) {
        if (i < 0 || i >= getChildCount()) {
            throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mBaselineAlignedChildIndex = i;
    }

    View getVirtualChildAt(int index) {
        return getChildAt(index);
    }

    int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    @RemotableViewMethod
    public void setWeightSum(float weightSum) {
        this.mWeightSum = Math.max(0.0f, weightSum);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mOrientation == 1) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasDividerBeforeChildAt(int childIndex) {
        if (this.mShowDividers == 0) {
            return false;
        }
        if (childIndex == getVirtualChildCount()) {
            return (this.mShowDividers & 4) != 0;
        }
        boolean allViewsAreGoneBefore = allViewsAreGoneBefore(childIndex);
        return allViewsAreGoneBefore ? (this.mShowDividers & 1) != 0 : (this.mShowDividers & 2) != 0;
    }

    private boolean hasDividerAfterChildAt(int childIndex) {
        if (this.mShowDividers == 0) {
            return false;
        }
        return allViewsAreGoneAfter(childIndex) ? (this.mShowDividers & 4) != 0 : (this.mShowDividers & 2) != 0;
    }

    private boolean allViewsAreGoneBefore(int childIndex) {
        for (int i = childIndex - 1; i >= 0; i--) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                return false;
            }
        }
        return true;
    }

    private boolean allViewsAreGoneAfter(int childIndex) {
        int count = getVirtualChildCount();
        for (int i = childIndex + 1; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:168:0x03dd  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x03e0  */
    /* JADX WARN: Removed duplicated region for block: B:187:0x0469  */
    /* JADX WARN: Removed duplicated region for block: B:188:0x046f  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0190  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x019c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int count;
        int heightMode;
        float totalWeight;
        int childState;
        int childState2;
        int count2;
        int heightMode2;
        int alternativeMaxWidth;
        int largestChildHeight;
        int count3;
        int heightMode3;
        boolean useLargestChild;
        int baselineChildIndex;
        int margin;
        boolean matchWidthLocally;
        int allFillParent;
        int childHeight;
        int alternativeMaxWidth2;
        int childState3;
        int alternativeMaxWidth3;
        int childState4;
        int weightedMaxWidth;
        int nonSkippedChildCount;
        int i;
        int i2;
        int i3;
        LayoutParams lp;
        int weightedMaxWidth2;
        int count4;
        int largestChildHeight2;
        int heightMode4;
        int count5;
        int childState5;
        View child;
        int i4;
        int margin2;
        int allFillParent2;
        this.mTotalLength = 0;
        int weightedMaxWidth3 = 0;
        int count6 = getVirtualChildCount();
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode5 = View.MeasureSpec.getMode(heightMeasureSpec);
        int baselineChildIndex2 = this.mBaselineAlignedChildIndex;
        boolean useLargestChild2 = this.mUseLargestChild;
        int maxWidth = 0;
        float totalWeight2 = 0.0f;
        int measuredWidth = 0;
        int childState6 = 0;
        int nonSkippedChildCount2 = 0;
        int i5 = 0;
        int largestChildHeight3 = Integer.MIN_VALUE;
        int largestChildHeight4 = 1;
        int consumedExcessSpace = 0;
        int i6 = 0;
        int consumedExcessSpace2 = 0;
        while (true) {
            int alternativeMaxWidth4 = i5;
            if (i6 < count6) {
                View child2 = getVirtualChildAt(i6);
                if (child2 == null) {
                    int largestChildHeight5 = largestChildHeight3;
                    int largestChildHeight6 = this.mTotalLength;
                    this.mTotalLength = largestChildHeight6 + measureNullChild(i6);
                    count4 = count6;
                    i5 = alternativeMaxWidth4;
                    largestChildHeight3 = largestChildHeight5;
                    largestChildHeight2 = heightMode5;
                } else {
                    int largestChildHeight7 = largestChildHeight3;
                    int largestChildHeight8 = child2.getVisibility();
                    int weightedMaxWidth4 = weightedMaxWidth3;
                    if (largestChildHeight8 == 8) {
                        i6 += getChildrenSkipCount(child2, i6);
                        count4 = count6;
                        i5 = alternativeMaxWidth4;
                        largestChildHeight3 = largestChildHeight7;
                        weightedMaxWidth3 = weightedMaxWidth4;
                        largestChildHeight2 = heightMode5;
                    } else {
                        int nonSkippedChildCount3 = childState6 + 1;
                        if (hasDividerBeforeChildAt(i6)) {
                            this.mTotalLength += this.mDividerHeight;
                        }
                        LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                        float totalWeight3 = totalWeight2 + lp2.weight;
                        boolean useExcessSpace = lp2.height == 0 && lp2.weight > 0.0f;
                        if (heightMode5 == 1073741824 && useExcessSpace) {
                            int totalLength = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength, lp2.topMargin + totalLength + lp2.bottomMargin);
                            nonSkippedChildCount2 = 1;
                            lp = lp2;
                            child = child2;
                            childState5 = measuredWidth;
                            i3 = i6;
                            count4 = count6;
                            largestChildHeight3 = largestChildHeight7;
                            weightedMaxWidth2 = weightedMaxWidth4;
                            count5 = 1073741824;
                            largestChildHeight2 = heightMode5;
                            heightMode4 = alternativeMaxWidth4;
                        } else {
                            if (useExcessSpace) {
                                lp2.height = -2;
                            }
                            int i7 = i6;
                            int i8 = totalWeight3 == 0.0f ? this.mTotalLength : 0;
                            i3 = i7;
                            lp = lp2;
                            weightedMaxWidth2 = weightedMaxWidth4;
                            count4 = count6;
                            largestChildHeight2 = heightMode5;
                            heightMode4 = alternativeMaxWidth4;
                            count5 = 1073741824;
                            childState5 = measuredWidth;
                            measureChildBeforeLayout(child2, i3, widthMeasureSpec, 0, heightMeasureSpec, i8);
                            int childHeight2 = child2.getMeasuredHeight();
                            if (useExcessSpace) {
                                lp.height = 0;
                                consumedExcessSpace2 += childHeight2;
                            }
                            int totalLength2 = this.mTotalLength;
                            child = child2;
                            this.mTotalLength = Math.max(totalLength2, totalLength2 + childHeight2 + lp.topMargin + lp.bottomMargin + getNextLocationOffset(child));
                            if (!useLargestChild2) {
                                largestChildHeight3 = largestChildHeight7;
                            } else {
                                largestChildHeight3 = Math.max(childHeight2, largestChildHeight7);
                            }
                        }
                        if (baselineChildIndex2 >= 0) {
                            i4 = i3;
                            if (baselineChildIndex2 == i4 + 1) {
                                this.mBaselineChildTop = this.mTotalLength;
                            }
                        } else {
                            i4 = i3;
                        }
                        if (i4 < baselineChildIndex2 && lp.weight > 0.0f) {
                            throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                        }
                        boolean matchWidthLocally2 = false;
                        if (widthMode != count5 && lp.width == -1) {
                            consumedExcessSpace = 1;
                            matchWidthLocally2 = true;
                        }
                        int margin3 = lp.leftMargin + lp.rightMargin;
                        int measuredWidth2 = child.getMeasuredWidth() + margin3;
                        int maxWidth2 = Math.max(maxWidth, measuredWidth2);
                        int childState7 = combineMeasuredStates(childState5, child.getMeasuredState());
                        if (largestChildHeight4 != 0) {
                            margin2 = margin3;
                            if (lp.width == -1) {
                                allFillParent2 = 1;
                                if (lp.weight <= 0.0f) {
                                    weightedMaxWidth2 = Math.max(weightedMaxWidth2, matchWidthLocally2 ? margin2 : measuredWidth2);
                                } else {
                                    heightMode4 = Math.max(heightMode4, matchWidthLocally2 ? margin2 : measuredWidth2);
                                }
                                int i9 = i4 + getChildrenSkipCount(child, i4);
                                largestChildHeight4 = allFillParent2;
                                maxWidth = maxWidth2;
                                weightedMaxWidth3 = weightedMaxWidth2;
                                measuredWidth = childState7;
                                childState6 = nonSkippedChildCount3;
                                totalWeight2 = totalWeight3;
                                i6 = i9;
                                i5 = heightMode4;
                            }
                        } else {
                            margin2 = margin3;
                        }
                        allFillParent2 = 0;
                        if (lp.weight <= 0.0f) {
                        }
                        int i92 = i4 + getChildrenSkipCount(child, i4);
                        largestChildHeight4 = allFillParent2;
                        maxWidth = maxWidth2;
                        weightedMaxWidth3 = weightedMaxWidth2;
                        measuredWidth = childState7;
                        childState6 = nonSkippedChildCount3;
                        totalWeight2 = totalWeight3;
                        i6 = i92;
                        i5 = heightMode4;
                    }
                }
                i6++;
                count6 = count4;
                heightMode5 = largestChildHeight2;
            } else {
                int weightedMaxWidth5 = weightedMaxWidth3;
                int count7 = count6;
                int heightMode6 = heightMode5;
                int alternativeMaxWidth5 = alternativeMaxWidth4;
                if (childState6 > 0) {
                    count = count7;
                    if (hasDividerBeforeChildAt(count)) {
                        this.mTotalLength += this.mDividerHeight;
                    }
                } else {
                    count = count7;
                }
                if (useLargestChild2) {
                    heightMode = heightMode6;
                    if (heightMode == Integer.MIN_VALUE || heightMode == 0) {
                        this.mTotalLength = 0;
                        int i10 = 0;
                        while (i10 < count) {
                            View child3 = getVirtualChildAt(i10);
                            if (child3 == null) {
                                this.mTotalLength += measureNullChild(i10);
                                nonSkippedChildCount = childState6;
                                i = i10;
                            } else {
                                nonSkippedChildCount = childState6;
                                if (child3.getVisibility() == 8) {
                                    i2 = i10 + getChildrenSkipCount(child3, i10);
                                    i10 = i2 + 1;
                                    childState6 = nonSkippedChildCount;
                                } else {
                                    LayoutParams lp3 = (LayoutParams) child3.getLayoutParams();
                                    int totalLength3 = this.mTotalLength;
                                    i = i10;
                                    int i11 = lp3.topMargin;
                                    this.mTotalLength = Math.max(totalLength3, totalLength3 + largestChildHeight3 + i11 + lp3.bottomMargin + getNextLocationOffset(child3));
                                }
                            }
                            i2 = i;
                            i10 = i2 + 1;
                            childState6 = nonSkippedChildCount;
                        }
                    }
                } else {
                    heightMode = heightMode6;
                }
                int nonSkippedChildCount4 = this.mTotalLength;
                this.mTotalLength = nonSkippedChildCount4 + this.mPaddingTop + this.mPaddingBottom;
                int heightSize = this.mTotalLength;
                int heightSizeAndState = resolveSizeAndState(Math.max(heightSize, getSuggestedMinimumHeight()), heightMeasureSpec, 0);
                int heightSize2 = heightSizeAndState & 16777215;
                int remainingExcess = (heightSize2 - this.mTotalLength) + (this.mAllowInconsistentMeasurement ? 0 : consumedExcessSpace2);
                if (nonSkippedChildCount2 != 0) {
                    totalWeight = totalWeight2;
                    childState = measuredWidth;
                } else if ((!sRemeasureWeightedChildren && remainingExcess == 0) || totalWeight2 <= 0.0f) {
                    int alternativeMaxWidth6 = Math.max(alternativeMaxWidth5, weightedMaxWidth5);
                    if (!useLargestChild2 || heightMode == 1073741824) {
                        alternativeMaxWidth2 = alternativeMaxWidth6;
                        childState3 = measuredWidth;
                    } else {
                        int i12 = 0;
                        while (i12 < count) {
                            float totalWeight4 = totalWeight2;
                            View child4 = getVirtualChildAt(i12);
                            if (child4 != null) {
                                alternativeMaxWidth3 = alternativeMaxWidth6;
                                childState4 = measuredWidth;
                                if (child4.getVisibility() == 8) {
                                    weightedMaxWidth = weightedMaxWidth5;
                                } else {
                                    float childExtra = ((LayoutParams) child4.getLayoutParams()).weight;
                                    if (childExtra > 0.0f) {
                                        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(child4.getMeasuredWidth(), 1073741824);
                                        weightedMaxWidth = weightedMaxWidth5;
                                        int weightedMaxWidth6 = View.MeasureSpec.makeMeasureSpec(largestChildHeight3, 1073741824);
                                        child4.measure(makeMeasureSpec, weightedMaxWidth6);
                                    } else {
                                        weightedMaxWidth = weightedMaxWidth5;
                                    }
                                }
                            } else {
                                alternativeMaxWidth3 = alternativeMaxWidth6;
                                childState4 = measuredWidth;
                                weightedMaxWidth = weightedMaxWidth5;
                            }
                            i12++;
                            alternativeMaxWidth6 = alternativeMaxWidth3;
                            totalWeight2 = totalWeight4;
                            weightedMaxWidth5 = weightedMaxWidth;
                            measuredWidth = childState4;
                        }
                        alternativeMaxWidth2 = alternativeMaxWidth6;
                        childState3 = measuredWidth;
                    }
                    count2 = count;
                    alternativeMaxWidth = alternativeMaxWidth2;
                    childState2 = childState3;
                    heightMode2 = widthMeasureSpec;
                    if (largestChildHeight4 == 0 && widthMode != 1073741824) {
                        maxWidth = alternativeMaxWidth;
                    }
                    setMeasuredDimension(resolveSizeAndState(Math.max(maxWidth + this.mPaddingLeft + this.mPaddingRight, getSuggestedMinimumWidth()), heightMode2, childState2), heightSizeAndState);
                    if (consumedExcessSpace == 0) {
                        forceUniformWidth(count2, heightMeasureSpec);
                        return;
                    }
                    return;
                } else {
                    totalWeight = totalWeight2;
                    childState = measuredWidth;
                }
                float remainingWeightSum = this.mWeightSum;
                if (remainingWeightSum <= 0.0f) {
                    remainingWeightSum = totalWeight;
                }
                this.mTotalLength = 0;
                int i13 = 0;
                childState2 = childState;
                while (i13 < count) {
                    View child5 = getVirtualChildAt(i13);
                    if (child5 != null) {
                        useLargestChild = useLargestChild2;
                        baselineChildIndex = baselineChildIndex2;
                        if (child5.getVisibility() == 8) {
                            largestChildHeight = largestChildHeight3;
                            count3 = count;
                            heightMode3 = heightMode;
                        } else {
                            LayoutParams lp4 = (LayoutParams) child5.getLayoutParams();
                            float childWeight = lp4.weight;
                            if (childWeight <= 0.0f) {
                                largestChildHeight = largestChildHeight3;
                                count3 = count;
                                heightMode3 = heightMode;
                            } else {
                                count3 = count;
                                int share = (int) ((remainingExcess * childWeight) / remainingWeightSum);
                                remainingExcess -= share;
                                float remainingWeightSum2 = remainingWeightSum - childWeight;
                                if (this.mUseLargestChild && heightMode != 1073741824) {
                                    childHeight = largestChildHeight3;
                                } else {
                                    int childHeight3 = lp4.height;
                                    if (childHeight3 == 0 && (!this.mAllowInconsistentMeasurement || heightMode == 1073741824)) {
                                        childHeight = share;
                                    } else {
                                        int childHeight4 = child5.getMeasuredHeight();
                                        childHeight = childHeight4 + share;
                                    }
                                }
                                largestChildHeight = largestChildHeight3;
                                int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(Math.max(0, childHeight), 1073741824);
                                int i14 = this.mPaddingLeft;
                                int childHeight5 = this.mPaddingRight;
                                heightMode3 = heightMode;
                                int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, i14 + childHeight5 + lp4.leftMargin + lp4.rightMargin, lp4.width);
                                child5.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                                childState2 = combineMeasuredStates(childState2, child5.getMeasuredState() & (-256));
                                remainingWeightSum = remainingWeightSum2;
                            }
                            int largestChildHeight9 = lp4.leftMargin;
                            int margin4 = largestChildHeight9 + lp4.rightMargin;
                            int measuredWidth3 = child5.getMeasuredWidth() + margin4;
                            maxWidth = Math.max(maxWidth, measuredWidth3);
                            float remainingWeightSum3 = remainingWeightSum;
                            if (widthMode != 1073741824) {
                                margin = margin4;
                                if (lp4.width == -1) {
                                    matchWidthLocally = true;
                                    int alternativeMaxWidth7 = Math.max(alternativeMaxWidth5, !matchWidthLocally ? margin : measuredWidth3);
                                    if (largestChildHeight4 != 0 && lp4.width == -1) {
                                        allFillParent = 1;
                                        int totalLength4 = this.mTotalLength;
                                        this.mTotalLength = Math.max(totalLength4, totalLength4 + child5.getMeasuredHeight() + lp4.topMargin + lp4.bottomMargin + getNextLocationOffset(child5));
                                        largestChildHeight4 = allFillParent;
                                        remainingWeightSum = remainingWeightSum3;
                                        alternativeMaxWidth5 = alternativeMaxWidth7;
                                    }
                                    allFillParent = 0;
                                    int totalLength42 = this.mTotalLength;
                                    this.mTotalLength = Math.max(totalLength42, totalLength42 + child5.getMeasuredHeight() + lp4.topMargin + lp4.bottomMargin + getNextLocationOffset(child5));
                                    largestChildHeight4 = allFillParent;
                                    remainingWeightSum = remainingWeightSum3;
                                    alternativeMaxWidth5 = alternativeMaxWidth7;
                                }
                            } else {
                                margin = margin4;
                            }
                            matchWidthLocally = false;
                            int alternativeMaxWidth72 = Math.max(alternativeMaxWidth5, !matchWidthLocally ? margin : measuredWidth3);
                            if (largestChildHeight4 != 0) {
                                allFillParent = 1;
                                int totalLength422 = this.mTotalLength;
                                this.mTotalLength = Math.max(totalLength422, totalLength422 + child5.getMeasuredHeight() + lp4.topMargin + lp4.bottomMargin + getNextLocationOffset(child5));
                                largestChildHeight4 = allFillParent;
                                remainingWeightSum = remainingWeightSum3;
                                alternativeMaxWidth5 = alternativeMaxWidth72;
                            }
                            allFillParent = 0;
                            int totalLength4222 = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength4222, totalLength4222 + child5.getMeasuredHeight() + lp4.topMargin + lp4.bottomMargin + getNextLocationOffset(child5));
                            largestChildHeight4 = allFillParent;
                            remainingWeightSum = remainingWeightSum3;
                            alternativeMaxWidth5 = alternativeMaxWidth72;
                        }
                    } else {
                        largestChildHeight = largestChildHeight3;
                        count3 = count;
                        heightMode3 = heightMode;
                        useLargestChild = useLargestChild2;
                        baselineChildIndex = baselineChildIndex2;
                    }
                    i13++;
                    useLargestChild2 = useLargestChild;
                    baselineChildIndex2 = baselineChildIndex;
                    count = count3;
                    largestChildHeight3 = largestChildHeight;
                    heightMode = heightMode3;
                }
                count2 = count;
                heightMode2 = widthMeasureSpec;
                int i15 = this.mTotalLength;
                this.mTotalLength = i15 + this.mPaddingTop + this.mPaddingBottom;
                alternativeMaxWidth = alternativeMaxWidth5;
                if (largestChildHeight4 == 0) {
                    maxWidth = alternativeMaxWidth;
                }
                setMeasuredDimension(resolveSizeAndState(Math.max(maxWidth + this.mPaddingLeft + this.mPaddingRight, getSuggestedMinimumWidth()), heightMode2, childState2), heightSizeAndState);
                if (consumedExcessSpace == 0) {
                }
            }
        }
    }

    private void forceUniformWidth(int count, int heightMeasureSpec) {
        int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.width == -1) {
                    int oldHeight = lp.height;
                    lp.height = child.getMeasuredHeight();
                    measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec, 0);
                    lp.height = oldHeight;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:221:0x0588  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x05c0  */
    /* JADX WARN: Removed duplicated region for block: B:258:0x0681  */
    /* JADX WARN: Removed duplicated region for block: B:259:0x0687  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int count;
        int childState;
        int descent;
        int widthMode;
        int widthMode2;
        float totalWeight;
        int remainingExcess;
        int childState2;
        int widthSizeAndState;
        int widthMode3;
        int maxHeight;
        int largestChildWidth;
        int count2;
        int widthMode4;
        int largestChildWidth2;
        int widthSizeAndState2;
        int largestChildWidth3;
        float remainingWeightSum;
        int alternativeMaxHeight;
        boolean allFillParent;
        int remainingExcess2;
        int childWidth;
        int alternativeMaxHeight2;
        int remainingExcess3;
        int alternativeMaxHeight3;
        int widthSize;
        int remainingExcess4;
        int maxHeight2;
        int i;
        int weightedMaxHeight;
        int alternativeMaxHeight4;
        int childState3;
        int weightedMaxHeight2;
        int widthMode5;
        boolean baselineAligned;
        int count3;
        int count4;
        LayoutParams lp;
        int margin;
        int largestChildWidth4;
        int weightedMaxHeight3;
        int childState4;
        this.mTotalLength = 0;
        int alternativeMaxHeight5 = 0;
        int count5 = getVirtualChildCount();
        int widthMode6 = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        int[] maxAscent = this.mMaxAscent;
        int[] maxDescent = this.mMaxDescent;
        maxAscent[3] = -1;
        maxAscent[2] = -1;
        maxAscent[1] = -1;
        maxAscent[0] = -1;
        maxDescent[3] = -1;
        maxDescent[2] = -1;
        maxDescent[1] = -1;
        maxDescent[0] = -1;
        boolean baselineAligned2 = this.mBaselineAligned;
        boolean useLargestChild = this.mUseLargestChild;
        boolean isExactly = widthMode6 == 1073741824;
        int usedExcessSpace = 0;
        int nonSkippedChildCount = 0;
        int i2 = 0;
        int childState5 = 0;
        float totalWeight2 = 0.0f;
        int weightedMaxHeight4 = 0;
        int childState6 = 0;
        boolean matchHeight = true;
        int largestChildWidth5 = Integer.MIN_VALUE;
        int largestChildWidth6 = 0;
        boolean skippedMeasure = false;
        while (i2 < count5) {
            View child = getVirtualChildAt(i2);
            if (child == null) {
                int weightedMaxHeight5 = childState6;
                int weightedMaxHeight6 = this.mTotalLength;
                this.mTotalLength = weightedMaxHeight6 + measureNullChild(i2);
                baselineAligned = baselineAligned2;
                count3 = count5;
                childState6 = weightedMaxHeight5;
                weightedMaxHeight2 = widthMode6;
            } else {
                int weightedMaxHeight7 = childState6;
                int weightedMaxHeight8 = child.getVisibility();
                int alternativeMaxHeight6 = alternativeMaxHeight5;
                if (weightedMaxHeight8 == 8) {
                    i2 += getChildrenSkipCount(child, i2);
                    baselineAligned = baselineAligned2;
                    childState6 = weightedMaxHeight7;
                    alternativeMaxHeight5 = alternativeMaxHeight6;
                    count3 = count5;
                    weightedMaxHeight2 = widthMode6;
                } else {
                    nonSkippedChildCount++;
                    if (hasDividerBeforeChildAt(i2)) {
                        this.mTotalLength += this.mDividerWidth;
                    }
                    LayoutParams lp2 = (LayoutParams) child.getLayoutParams();
                    float totalWeight3 = totalWeight2 + lp2.weight;
                    boolean useExcessSpace = lp2.width == 0 && lp2.weight > 0.0f;
                    if (widthMode6 != 1073741824 || !useExcessSpace) {
                        int childState7 = weightedMaxHeight4;
                        if (useExcessSpace) {
                            lp2.width = -2;
                        }
                        int largestChildWidth7 = largestChildWidth5;
                        int largestChildWidth8 = totalWeight3 == 0.0f ? this.mTotalLength : 0;
                        weightedMaxHeight = weightedMaxHeight7;
                        alternativeMaxHeight4 = alternativeMaxHeight6;
                        childState3 = childState7;
                        weightedMaxHeight2 = widthMode6;
                        widthMode5 = childState5;
                        baselineAligned = baselineAligned2;
                        count3 = count5;
                        count4 = -1;
                        measureChildBeforeLayout(child, i2, widthMeasureSpec, largestChildWidth8, heightMeasureSpec, 0);
                        int childWidth2 = child.getMeasuredWidth();
                        if (!useExcessSpace) {
                            lp = lp2;
                        } else {
                            lp = lp2;
                            lp.width = 0;
                            usedExcessSpace += childWidth2;
                        }
                        if (isExactly) {
                            this.mTotalLength += lp.leftMargin + childWidth2 + lp.rightMargin + getNextLocationOffset(child);
                        } else {
                            int totalLength = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength, totalLength + childWidth2 + lp.leftMargin + lp.rightMargin + getNextLocationOffset(child));
                        }
                        if (!useLargestChild) {
                            largestChildWidth5 = largestChildWidth7;
                        } else {
                            largestChildWidth5 = Math.max(childWidth2, largestChildWidth7);
                        }
                    } else {
                        if (isExactly) {
                            int i3 = this.mTotalLength;
                            int i4 = lp2.leftMargin;
                            childState4 = weightedMaxHeight4;
                            int childState8 = lp2.rightMargin;
                            this.mTotalLength = i3 + i4 + childState8;
                        } else {
                            childState4 = weightedMaxHeight4;
                            int totalLength2 = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength2, lp2.leftMargin + totalLength2 + lp2.rightMargin);
                        }
                        if (baselineAligned2) {
                            int freeWidthSpec = View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 0);
                            int freeHeightSpec = View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), 0);
                            child.measure(freeWidthSpec, freeHeightSpec);
                            lp = lp2;
                            baselineAligned = baselineAligned2;
                            weightedMaxHeight = weightedMaxHeight7;
                            alternativeMaxHeight4 = alternativeMaxHeight6;
                            childState3 = childState4;
                            count3 = count5;
                            weightedMaxHeight2 = widthMode6;
                            count4 = -1;
                            widthMode5 = childState5;
                        } else {
                            largestChildWidth6 = 1;
                            lp = lp2;
                            baselineAligned = baselineAligned2;
                            weightedMaxHeight = weightedMaxHeight7;
                            alternativeMaxHeight4 = alternativeMaxHeight6;
                            childState3 = childState4;
                            count3 = count5;
                            weightedMaxHeight2 = widthMode6;
                            count4 = -1;
                            widthMode5 = childState5;
                        }
                    }
                    boolean matchHeightLocally = false;
                    if (heightMode != 1073741824 && lp.height == count4) {
                        skippedMeasure = true;
                        matchHeightLocally = true;
                    }
                    int margin2 = lp.topMargin + lp.bottomMargin;
                    int childHeight = child.getMeasuredHeight() + margin2;
                    int childState9 = combineMeasuredStates(childState3, child.getMeasuredState());
                    if (!baselineAligned) {
                        margin = margin2;
                        largestChildWidth4 = largestChildWidth5;
                    } else {
                        int childBaseline = child.getBaseline();
                        if (childBaseline == count4) {
                            margin = margin2;
                            largestChildWidth4 = largestChildWidth5;
                        } else {
                            int gravity = (lp.gravity < 0 ? this.mGravity : lp.gravity) & 112;
                            int index = ((gravity >> 4) & (-2)) >> 1;
                            margin = margin2;
                            maxAscent[index] = Math.max(maxAscent[index], childBaseline);
                            largestChildWidth4 = largestChildWidth5;
                            int largestChildWidth9 = childHeight - childBaseline;
                            maxDescent[index] = Math.max(maxDescent[index], largestChildWidth9);
                        }
                    }
                    int maxHeight3 = Math.max(widthMode5, childHeight);
                    boolean allFillParent2 = matchHeight && lp.height == -1;
                    if (lp.weight > 0.0f) {
                        weightedMaxHeight3 = Math.max(weightedMaxHeight, matchHeightLocally ? margin : childHeight);
                    } else {
                        int weightedMaxHeight9 = weightedMaxHeight;
                        alternativeMaxHeight4 = Math.max(alternativeMaxHeight4, matchHeightLocally ? margin : childHeight);
                        weightedMaxHeight3 = weightedMaxHeight9;
                    }
                    int weightedMaxHeight10 = getChildrenSkipCount(child, i2);
                    i2 += weightedMaxHeight10;
                    matchHeight = allFillParent2;
                    weightedMaxHeight4 = childState9;
                    childState6 = weightedMaxHeight3;
                    totalWeight2 = totalWeight3;
                    largestChildWidth5 = largestChildWidth4;
                    childState5 = maxHeight3;
                    alternativeMaxHeight5 = alternativeMaxHeight4;
                }
            }
            i2++;
            baselineAligned2 = baselineAligned;
            widthMode6 = weightedMaxHeight2;
            count5 = count3;
        }
        boolean baselineAligned3 = baselineAligned2;
        int count6 = count5;
        int widthMode7 = widthMode6;
        int count7 = childState6;
        int alternativeMaxHeight7 = alternativeMaxHeight5;
        int childState10 = weightedMaxHeight4;
        int largestChildWidth10 = largestChildWidth5;
        int widthMode8 = childState5;
        if (nonSkippedChildCount > 0) {
            count = count6;
            if (hasDividerBeforeChildAt(count)) {
                this.mTotalLength += this.mDividerWidth;
            }
        } else {
            count = count6;
        }
        if (maxAscent[1] == -1 && maxAscent[0] == -1 && maxAscent[2] == -1 && maxAscent[3] == -1) {
            childState = childState10;
            descent = widthMode8;
        } else {
            int ascent = Math.max(maxAscent[3], Math.max(maxAscent[0], Math.max(maxAscent[1], maxAscent[2])));
            int i5 = maxDescent[3];
            int i6 = maxDescent[0];
            int i7 = maxDescent[1];
            childState = childState10;
            int childState11 = maxDescent[2];
            int descent2 = Math.max(i5, Math.max(i6, Math.max(i7, childState11)));
            descent = Math.max(widthMode8, ascent + descent2);
        }
        if (useLargestChild) {
            widthMode = widthMode7;
            if (widthMode == Integer.MIN_VALUE || widthMode == 0) {
                this.mTotalLength = 0;
                int i8 = 0;
                int nonSkippedChildCount2 = 0;
                while (i8 < count) {
                    View child2 = getVirtualChildAt(i8);
                    if (child2 == null) {
                        this.mTotalLength += measureNullChild(i8);
                        maxHeight2 = descent;
                    } else if (child2.getVisibility() == 8) {
                        i8 += getChildrenSkipCount(child2, i8);
                        maxHeight2 = descent;
                    } else {
                        nonSkippedChildCount2++;
                        if (hasDividerBeforeChildAt(i8)) {
                            this.mTotalLength += this.mDividerWidth;
                        }
                        LayoutParams lp3 = (LayoutParams) child2.getLayoutParams();
                        if (isExactly) {
                            int i9 = this.mTotalLength;
                            maxHeight2 = descent;
                            int maxHeight4 = lp3.leftMargin;
                            i = i8;
                            int i10 = lp3.rightMargin;
                            this.mTotalLength = i9 + maxHeight4 + largestChildWidth10 + i10 + getNextLocationOffset(child2);
                        } else {
                            maxHeight2 = descent;
                            i = i8;
                            int maxHeight5 = this.mTotalLength;
                            this.mTotalLength = Math.max(maxHeight5, maxHeight5 + largestChildWidth10 + lp3.leftMargin + lp3.rightMargin + getNextLocationOffset(child2));
                        }
                        i8 = i;
                    }
                    i8++;
                    descent = maxHeight2;
                }
                widthMode2 = descent;
                if (nonSkippedChildCount2 > 0 && hasDividerBeforeChildAt(count)) {
                    this.mTotalLength += this.mDividerWidth;
                }
            } else {
                widthMode2 = descent;
            }
        } else {
            widthMode = widthMode7;
            widthMode2 = descent;
        }
        this.mTotalLength += this.mPaddingLeft + this.mPaddingRight;
        int widthSizeAndState3 = resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumWidth()), widthMeasureSpec, 0);
        int widthSize2 = widthSizeAndState3 & 16777215;
        int remainingExcess5 = (widthSize2 - this.mTotalLength) + (this.mAllowInconsistentMeasurement ? 0 : usedExcessSpace);
        if (largestChildWidth6 != 0) {
            totalWeight = totalWeight2;
            remainingExcess = remainingExcess5;
        } else if ((!sRemeasureWeightedChildren && remainingExcess5 == 0) || totalWeight2 <= 0.0f) {
            int alternativeMaxHeight8 = Math.max(alternativeMaxHeight7, count7);
            if (!useLargestChild || widthMode == 1073741824) {
                alternativeMaxHeight2 = alternativeMaxHeight8;
                remainingExcess3 = remainingExcess5;
            } else {
                int i11 = 0;
                while (i11 < count) {
                    float totalWeight4 = totalWeight2;
                    View child3 = getVirtualChildAt(i11);
                    if (child3 != null) {
                        alternativeMaxHeight3 = alternativeMaxHeight8;
                        int alternativeMaxHeight9 = child3.getVisibility();
                        widthSize = widthSize2;
                        if (alternativeMaxHeight9 == 8) {
                            remainingExcess4 = remainingExcess5;
                        } else {
                            float childExtra = ((LayoutParams) child3.getLayoutParams()).weight;
                            if (childExtra > 0.0f) {
                                remainingExcess4 = remainingExcess5;
                                child3.measure(View.MeasureSpec.makeMeasureSpec(largestChildWidth10, 1073741824), View.MeasureSpec.makeMeasureSpec(child3.getMeasuredHeight(), 1073741824));
                            } else {
                                remainingExcess4 = remainingExcess5;
                            }
                        }
                    } else {
                        alternativeMaxHeight3 = alternativeMaxHeight8;
                        widthSize = widthSize2;
                        remainingExcess4 = remainingExcess5;
                    }
                    i11++;
                    alternativeMaxHeight8 = alternativeMaxHeight3;
                    totalWeight2 = totalWeight4;
                    widthSize2 = widthSize;
                    remainingExcess5 = remainingExcess4;
                }
                alternativeMaxHeight2 = alternativeMaxHeight8;
                remainingExcess3 = remainingExcess5;
            }
            widthSizeAndState = widthSizeAndState3;
            alternativeMaxHeight7 = alternativeMaxHeight2;
            maxHeight = widthMode2;
            childState2 = childState;
            widthMode3 = heightMeasureSpec;
            if (!matchHeight && heightMode != 1073741824) {
                maxHeight = alternativeMaxHeight7;
            }
            setMeasuredDimension(widthSizeAndState | ((-16777216) & childState2), resolveSizeAndState(Math.max(maxHeight + this.mPaddingTop + this.mPaddingBottom, getSuggestedMinimumHeight()), widthMode3, childState2 << 16));
            if (!skippedMeasure) {
                forceUniformHeight(count, widthMeasureSpec);
                return;
            }
            return;
        } else {
            totalWeight = totalWeight2;
            remainingExcess = remainingExcess5;
        }
        float remainingWeightSum2 = this.mWeightSum;
        if (remainingWeightSum2 <= 0.0f) {
            remainingWeightSum2 = totalWeight;
        }
        maxAscent[3] = -1;
        maxAscent[2] = -1;
        maxAscent[1] = -1;
        maxAscent[0] = -1;
        maxDescent[3] = -1;
        maxDescent[2] = -1;
        maxDescent[1] = -1;
        maxDescent[0] = -1;
        this.mTotalLength = 0;
        int i12 = 0;
        int nonSkippedChildCount3 = 0;
        int maxHeight6 = -1;
        childState2 = childState;
        int remainingExcess6 = remainingExcess;
        while (i12 < count) {
            boolean useLargestChild2 = useLargestChild;
            View child4 = getVirtualChildAt(i12);
            if (child4 != null) {
                widthSizeAndState2 = widthSizeAndState3;
                if (child4.getVisibility() == 8) {
                    largestChildWidth = largestChildWidth10;
                    count2 = count;
                    widthMode4 = widthMode;
                    largestChildWidth2 = remainingExcess6;
                } else {
                    nonSkippedChildCount3++;
                    if (hasDividerBeforeChildAt(i12)) {
                        this.mTotalLength += this.mDividerWidth;
                    }
                    LayoutParams lp4 = (LayoutParams) child4.getLayoutParams();
                    float childWeight = lp4.weight;
                    if (childWeight <= 0.0f) {
                        largestChildWidth = largestChildWidth10;
                        count2 = count;
                        widthMode4 = widthMode;
                        largestChildWidth3 = remainingExcess6;
                    } else {
                        count2 = count;
                        int share = (int) ((remainingExcess6 * childWeight) / remainingWeightSum2);
                        int remainingExcess7 = remainingExcess6 - share;
                        float remainingWeightSum3 = remainingWeightSum2 - childWeight;
                        if (this.mUseLargestChild && widthMode != 1073741824) {
                            childWidth = largestChildWidth10;
                        } else {
                            int childWidth3 = lp4.width;
                            if (childWidth3 == 0 && (!this.mAllowInconsistentMeasurement || widthMode == 1073741824)) {
                                childWidth = share;
                            } else {
                                int childWidth4 = child4.getMeasuredWidth();
                                childWidth = childWidth4 + share;
                            }
                        }
                        largestChildWidth = largestChildWidth10;
                        int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(Math.max(0, childWidth), 1073741824);
                        int i13 = this.mPaddingTop;
                        int childWidth5 = this.mPaddingBottom;
                        widthMode4 = widthMode;
                        int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, i13 + childWidth5 + lp4.topMargin + lp4.bottomMargin, lp4.height);
                        child4.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                        childState2 = combineMeasuredStates(childState2, child4.getMeasuredState() & (-16777216));
                        largestChildWidth3 = remainingExcess7;
                        remainingWeightSum2 = remainingWeightSum3;
                    }
                    if (isExactly) {
                        this.mTotalLength += child4.getMeasuredWidth() + lp4.leftMargin + lp4.rightMargin + getNextLocationOffset(child4);
                        remainingWeightSum = remainingWeightSum2;
                    } else {
                        int totalLength3 = this.mTotalLength;
                        remainingWeightSum = remainingWeightSum2;
                        this.mTotalLength = Math.max(totalLength3, child4.getMeasuredWidth() + totalLength3 + lp4.leftMargin + lp4.rightMargin + getNextLocationOffset(child4));
                    }
                    boolean matchHeightLocally2 = heightMode != 1073741824 && lp4.height == -1;
                    int margin3 = lp4.topMargin + lp4.bottomMargin;
                    int childHeight2 = child4.getMeasuredHeight() + margin3;
                    maxHeight6 = Math.max(maxHeight6, childHeight2);
                    int alternativeMaxHeight10 = Math.max(alternativeMaxHeight7, matchHeightLocally2 ? margin3 : childHeight2);
                    if (matchHeight) {
                        alternativeMaxHeight = alternativeMaxHeight10;
                        if (lp4.height == -1) {
                            allFillParent = true;
                            if (baselineAligned3) {
                                matchHeight = allFillParent;
                                remainingExcess2 = largestChildWidth3;
                            } else {
                                int childBaseline2 = child4.getBaseline();
                                matchHeight = allFillParent;
                                if (childBaseline2 == -1) {
                                    remainingExcess2 = largestChildWidth3;
                                } else {
                                    int gravity2 = (lp4.gravity < 0 ? this.mGravity : lp4.gravity) & 112;
                                    int index2 = ((gravity2 >> 4) & (-2)) >> 1;
                                    int gravity3 = maxAscent[index2];
                                    maxAscent[index2] = Math.max(gravity3, childBaseline2);
                                    remainingExcess2 = largestChildWidth3;
                                    maxDescent[index2] = Math.max(maxDescent[index2], childHeight2 - childBaseline2);
                                }
                            }
                            remainingWeightSum2 = remainingWeightSum;
                            alternativeMaxHeight7 = alternativeMaxHeight;
                            remainingExcess6 = remainingExcess2;
                            i12++;
                            useLargestChild = useLargestChild2;
                            widthSizeAndState3 = widthSizeAndState2;
                            count = count2;
                            largestChildWidth10 = largestChildWidth;
                            widthMode = widthMode4;
                        }
                    } else {
                        alternativeMaxHeight = alternativeMaxHeight10;
                    }
                    allFillParent = false;
                    if (baselineAligned3) {
                    }
                    remainingWeightSum2 = remainingWeightSum;
                    alternativeMaxHeight7 = alternativeMaxHeight;
                    remainingExcess6 = remainingExcess2;
                    i12++;
                    useLargestChild = useLargestChild2;
                    widthSizeAndState3 = widthSizeAndState2;
                    count = count2;
                    largestChildWidth10 = largestChildWidth;
                    widthMode = widthMode4;
                }
            } else {
                largestChildWidth = largestChildWidth10;
                count2 = count;
                widthMode4 = widthMode;
                largestChildWidth2 = remainingExcess6;
                widthSizeAndState2 = widthSizeAndState3;
            }
            remainingExcess6 = largestChildWidth2;
            i12++;
            useLargestChild = useLargestChild2;
            widthSizeAndState3 = widthSizeAndState2;
            count = count2;
            largestChildWidth10 = largestChildWidth;
            widthMode = widthMode4;
        }
        int count8 = count;
        widthSizeAndState = widthSizeAndState3;
        widthMode3 = heightMeasureSpec;
        if (nonSkippedChildCount3 > 0) {
            count = count8;
            if (hasDividerBeforeChildAt(count)) {
                this.mTotalLength += this.mDividerWidth;
            }
        } else {
            count = count8;
        }
        this.mTotalLength += this.mPaddingLeft + this.mPaddingRight;
        if (maxAscent[1] == -1 && maxAscent[0] == -1 && maxAscent[2] == -1 && maxAscent[3] == -1) {
            maxHeight = maxHeight6;
        } else {
            int ascent2 = Math.max(maxAscent[3], Math.max(maxAscent[0], Math.max(maxAscent[1], maxAscent[2])));
            int descent3 = Math.max(maxDescent[3], Math.max(maxDescent[0], Math.max(maxDescent[1], maxDescent[2])));
            maxHeight = Math.max(maxHeight6, ascent2 + descent3);
        }
        if (!matchHeight) {
            maxHeight = alternativeMaxHeight7;
        }
        setMeasuredDimension(widthSizeAndState | ((-16777216) & childState2), resolveSizeAndState(Math.max(maxHeight + this.mPaddingTop + this.mPaddingBottom, getSuggestedMinimumHeight()), widthMode3, childState2 << 16));
        if (!skippedMeasure) {
        }
    }

    private void forceUniformHeight(int count, int widthMeasureSpec) {
        int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.height == -1) {
                    int oldWidth = lp.width;
                    lp.width = child.getMeasuredWidth();
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                    lp.width = oldWidth;
                }
            }
        }
    }

    int getChildrenSkipCount(View child, int index) {
        return 0;
    }

    int measureNullChild(int childIndex) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void measureChildBeforeLayout(View child, int childIndex, int widthMeasureSpec, int totalWidth, int heightMeasureSpec, int totalHeight) {
        measureChildWithMargins(child, widthMeasureSpec, totalWidth, heightMeasureSpec, totalHeight);
    }

    int getLocationOffset(View child) {
        return 0;
    }

    int getNextLocationOffset(View child) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.mOrientation == 1) {
            layoutVertical(l, t, r, b);
        } else {
            layoutHorizontal(l, t, r, b);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void layoutVertical(int left, int top, int right, int bottom) {
        int childTop;
        int paddingLeft;
        int gravity;
        int childLeft;
        int paddingLeft2 = this.mPaddingLeft;
        int width = right - left;
        int childRight = width - this.mPaddingRight;
        int childSpace = (width - paddingLeft2) - this.mPaddingRight;
        int count = getVirtualChildCount();
        int i = this.mGravity;
        int majorGravity = i & 112;
        int minorGravity = i & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        switch (majorGravity) {
            case 16:
                int childTop2 = this.mPaddingTop;
                childTop = childTop2 + (((bottom - top) - this.mTotalLength) / 2);
                break;
            case 80:
                int childTop3 = this.mPaddingTop;
                childTop = ((childTop3 + bottom) - top) - this.mTotalLength;
                break;
            default:
                childTop = this.mPaddingTop;
                break;
        }
        int i2 = 0;
        while (i2 < count) {
            View child = getVirtualChildAt(i2);
            if (child == null) {
                childTop += measureNullChild(i2);
                paddingLeft = paddingLeft2;
            } else if (child.getVisibility() == 8) {
                paddingLeft = paddingLeft2;
            } else {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int gravity2 = lp.gravity;
                if (gravity2 >= 0) {
                    gravity = gravity2;
                } else {
                    gravity = minorGravity;
                }
                int layoutDirection = getLayoutDirection();
                int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                switch (absoluteGravity & 7) {
                    case 1:
                        int childLeft2 = childSpace - childWidth;
                        childLeft = (((childLeft2 / 2) + paddingLeft2) + lp.leftMargin) - lp.rightMargin;
                        break;
                    case 5:
                        int childLeft3 = childRight - childWidth;
                        childLeft = childLeft3 - lp.rightMargin;
                        break;
                    default:
                        childLeft = lp.leftMargin + paddingLeft2;
                        break;
                }
                if (hasDividerBeforeChildAt(i2)) {
                    childTop += this.mDividerHeight;
                }
                int childTop4 = childTop + lp.topMargin;
                int childTop5 = getLocationOffset(child);
                paddingLeft = paddingLeft2;
                setChildFrame(child, childLeft, childTop4 + childTop5, childWidth, childHeight);
                int childTop6 = childTop4 + childHeight + lp.bottomMargin + getNextLocationOffset(child);
                i2 += getChildrenSkipCount(child, i2);
                childTop = childTop6;
            }
            i2++;
            paddingLeft2 = paddingLeft;
        }
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        if (layoutDirection != this.mLayoutDirection) {
            this.mLayoutDirection = layoutDirection;
            if (this.mOrientation == 0) {
                requestLayout();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:27:0x00b8  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00bc  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00c3  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00c8  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00e1  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00f1  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0101  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x010b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void layoutHorizontal(int left, int top, int right, int bottom) {
        int childLeft;
        int start;
        int dir;
        int layoutDirection;
        int[] maxDescent;
        int[] maxAscent;
        boolean isLayoutRtl;
        int paddingTop;
        int childBottom;
        int count;
        int childBaseline;
        int gravity;
        int gravity2;
        int childTop;
        boolean isLayoutRtl2 = isLayoutRtl();
        int paddingTop2 = this.mPaddingTop;
        int height = bottom - top;
        int childBottom2 = height - this.mPaddingBottom;
        int childSpace = (height - paddingTop2) - this.mPaddingBottom;
        int count2 = getVirtualChildCount();
        int i = this.mGravity;
        int majorGravity = i & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        int minorGravity = i & 112;
        boolean baselineAligned = this.mBaselineAligned;
        int[] maxAscent2 = this.mMaxAscent;
        int[] maxDescent2 = this.mMaxDescent;
        int layoutDirection2 = getLayoutDirection();
        switch (Gravity.getAbsoluteGravity(majorGravity, layoutDirection2)) {
            case 1:
                int childLeft2 = this.mPaddingLeft;
                childLeft = childLeft2 + (((right - left) - this.mTotalLength) / 2);
                break;
            case 5:
                int childLeft3 = this.mPaddingLeft;
                childLeft = ((childLeft3 + right) - left) - this.mTotalLength;
                break;
            default:
                childLeft = this.mPaddingLeft;
                break;
        }
        if (!isLayoutRtl2) {
            start = 0;
            dir = 1;
        } else {
            int start2 = count2 - 1;
            start = start2;
            dir = -1;
        }
        int i2 = 0;
        while (i2 < count2) {
            int childIndex = start + (dir * i2);
            int height2 = height;
            View child = getVirtualChildAt(childIndex);
            if (child == null) {
                childLeft += measureNullChild(childIndex);
                layoutDirection = layoutDirection2;
                maxDescent = maxDescent2;
                maxAscent = maxAscent2;
                isLayoutRtl = isLayoutRtl2;
                paddingTop = paddingTop2;
                childBottom = childBottom2;
                count = count2;
            } else {
                int i3 = i2;
                int i4 = child.getVisibility();
                layoutDirection = layoutDirection2;
                if (i4 == 8) {
                    maxDescent = maxDescent2;
                    maxAscent = maxAscent2;
                    isLayoutRtl = isLayoutRtl2;
                    paddingTop = paddingTop2;
                    childBottom = childBottom2;
                    count = count2;
                    i2 = i3;
                } else {
                    int childWidth = child.getMeasuredWidth();
                    int childHeight = child.getMeasuredHeight();
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    if (baselineAligned) {
                        count = count2;
                        if (lp.height != -1) {
                            childBaseline = child.getBaseline();
                            gravity = lp.gravity;
                            if (gravity < 0) {
                                gravity2 = gravity;
                            } else {
                                gravity2 = minorGravity;
                            }
                            switch (gravity2 & 112) {
                                case 16:
                                    childBottom = childBottom2;
                                    int childTop2 = ((((childSpace - childHeight) / 2) + paddingTop2) + lp.topMargin) - lp.bottomMargin;
                                    childTop = childTop2;
                                    break;
                                case 48:
                                    childBottom = childBottom2;
                                    int childTop3 = lp.topMargin + paddingTop2;
                                    if (childBaseline == -1) {
                                        childTop = childTop3;
                                        break;
                                    } else {
                                        childTop = childTop3 + (maxAscent2[1] - childBaseline);
                                        break;
                                    }
                                case 80:
                                    int childTop4 = childBottom2 - childHeight;
                                    childBottom = childBottom2;
                                    int childBottom3 = lp.bottomMargin;
                                    int childTop5 = childTop4 - childBottom3;
                                    if (childBaseline == -1) {
                                        childTop = childTop5;
                                        break;
                                    } else {
                                        int descent = child.getMeasuredHeight() - childBaseline;
                                        childTop = childTop5 - (maxDescent2[2] - descent);
                                        break;
                                    }
                                default:
                                    childBottom = childBottom2;
                                    childTop = paddingTop2;
                                    break;
                            }
                            if (!isLayoutRtl2) {
                                if (hasDividerAfterChildAt(childIndex)) {
                                    childLeft += this.mDividerWidth;
                                }
                            } else if (hasDividerBeforeChildAt(childIndex)) {
                                childLeft += this.mDividerWidth;
                            }
                            int childLeft4 = childLeft + lp.leftMargin;
                            int childLeft5 = getLocationOffset(child);
                            isLayoutRtl = isLayoutRtl2;
                            paddingTop = paddingTop2;
                            maxDescent = maxDescent2;
                            maxAscent = maxAscent2;
                            setChildFrame(child, childLeft4 + childLeft5, childTop, childWidth, childHeight);
                            int childLeft6 = childLeft4 + childWidth + lp.rightMargin + getNextLocationOffset(child);
                            i2 = i3 + getChildrenSkipCount(child, childIndex);
                            childLeft = childLeft6;
                        }
                    } else {
                        count = count2;
                    }
                    childBaseline = -1;
                    gravity = lp.gravity;
                    if (gravity < 0) {
                    }
                    switch (gravity2 & 112) {
                        case 16:
                            break;
                        case 48:
                            break;
                        case 80:
                            break;
                    }
                    if (!isLayoutRtl2) {
                    }
                    int childLeft42 = childLeft + lp.leftMargin;
                    int childLeft52 = getLocationOffset(child);
                    isLayoutRtl = isLayoutRtl2;
                    paddingTop = paddingTop2;
                    maxDescent = maxDescent2;
                    maxAscent = maxAscent2;
                    setChildFrame(child, childLeft42 + childLeft52, childTop, childWidth, childHeight);
                    int childLeft62 = childLeft42 + childWidth + lp.rightMargin + getNextLocationOffset(child);
                    i2 = i3 + getChildrenSkipCount(child, childIndex);
                    childLeft = childLeft62;
                }
            }
            i2++;
            height = height2;
            layoutDirection2 = layoutDirection;
            count2 = count;
            childBottom2 = childBottom;
            isLayoutRtl2 = isLayoutRtl;
            paddingTop2 = paddingTop;
            maxDescent2 = maxDescent;
            maxAscent2 = maxAscent;
        }
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    public void setOrientation(int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            requestLayout();
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    @RemotableViewMethod
    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            if ((8388615 & gravity) == 0) {
                gravity |= Gravity.START;
            }
            if ((gravity & 112) == 0) {
                gravity |= 48;
            }
            this.mGravity = gravity;
            requestLayout();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    @RemotableViewMethod
    public void setHorizontalGravity(int horizontalGravity) {
        int gravity = horizontalGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        int i = this.mGravity;
        if ((8388615 & i) != gravity) {
            this.mGravity = ((-8388616) & i) | gravity;
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setVerticalGravity(int verticalGravity) {
        int gravity = verticalGravity & 112;
        int i = this.mGravity;
        if ((i & 112) != gravity) {
            this.mGravity = (i & (-113)) | gravity;
            requestLayout();
        }
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        int i = this.mOrientation;
        if (i == 0) {
            return new LayoutParams(-2, -2);
        }
        if (i == 1) {
            return new LayoutParams(-1, -2);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (sPreserveMarginParamsInLayoutParamConversion) {
            if (lp instanceof LayoutParams) {
                return new LayoutParams((LayoutParams) lp);
            }
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
            }
        }
        return new LayoutParams(lp);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return LinearLayout.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void encodeProperties(ViewHierarchyEncoder encoder) {
        super.encodeProperties(encoder);
        encoder.addProperty("layout:baselineAligned", this.mBaselineAligned);
        encoder.addProperty("layout:baselineAlignedChildIndex", this.mBaselineAlignedChildIndex);
        encoder.addProperty("measurement:baselineChildTop", this.mBaselineChildTop);
        encoder.addProperty("measurement:orientation", this.mOrientation);
        encoder.addProperty("measurement:gravity", this.mGravity);
        encoder.addProperty("measurement:totalLength", this.mTotalLength);
        encoder.addProperty("layout:totalLength", this.mTotalLength);
        encoder.addProperty("layout:useLargestChild", this.mUseLargestChild);
    }
}
