package android.widget;

import android.content.Context;
import android.content.res.ResourceId;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.media.TtmlUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Pools;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.view.accessibility.AccessibilityEvent;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
@RemoteViews.RemoteView
/* loaded from: classes4.dex */
public class RelativeLayout extends ViewGroup {
    public static final int ABOVE = 2;
    public static final int ALIGN_BASELINE = 4;
    public static final int ALIGN_BOTTOM = 8;
    public static final int ALIGN_END = 19;
    public static final int ALIGN_LEFT = 5;
    public static final int ALIGN_PARENT_BOTTOM = 12;
    public static final int ALIGN_PARENT_END = 21;
    public static final int ALIGN_PARENT_LEFT = 9;
    public static final int ALIGN_PARENT_RIGHT = 11;
    public static final int ALIGN_PARENT_START = 20;
    public static final int ALIGN_PARENT_TOP = 10;
    public static final int ALIGN_RIGHT = 7;
    public static final int ALIGN_START = 18;
    public static final int ALIGN_TOP = 6;
    public static final int BELOW = 3;
    public static final int CENTER_HORIZONTAL = 14;
    public static final int CENTER_IN_PARENT = 13;
    public static final int CENTER_VERTICAL = 15;
    private static final int DEFAULT_WIDTH = 65536;
    public static final int END_OF = 17;
    public static final int LEFT_OF = 0;
    public static final int RIGHT_OF = 1;
    public static final int START_OF = 16;
    public static final int TRUE = -1;
    private static final int VALUE_NOT_SET = Integer.MIN_VALUE;
    private static final int VERB_COUNT = 22;
    private boolean mAllowBrokenMeasureSpecs;
    private View mBaselineView;
    private final Rect mContentBounds;
    private boolean mDirtyHierarchy;
    private final DependencyGraph mGraph;
    private int mGravity;
    private int mIgnoreGravity;
    private boolean mMeasureVerticalWithPaddingMargin;
    private final Rect mSelfBounds;
    private View[] mSortedHorizontalChildren;
    private View[] mSortedVerticalChildren;
    private SortedSet<View> mTopToBottomLeftToRightSet;
    private static final int[] RULES_VERTICAL = {2, 3, 4, 6, 8};
    private static final int[] RULES_HORIZONTAL = {0, 1, 5, 7, 16, 17, 18, 19};

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<RelativeLayout> {
        private int mGravityId;
        private int mIgnoreGravityId;
        private boolean mPropertiesMapped = false;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mGravityId = propertyMapper.mapGravity("gravity", 16842927);
            this.mIgnoreGravityId = propertyMapper.mapInt("ignoreGravity", 16843263);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(RelativeLayout node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readGravity(this.mGravityId, node.getGravity());
            propertyReader.readInt(this.mIgnoreGravityId, node.getIgnoreGravity());
        }
    }

    public RelativeLayout(Context context) {
        this(context, null);
    }

    public RelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mBaselineView = null;
        this.mGravity = 8388659;
        this.mContentBounds = new Rect();
        this.mSelfBounds = new Rect();
        this.mTopToBottomLeftToRightSet = null;
        this.mGraph = new DependencyGraph();
        this.mAllowBrokenMeasureSpecs = false;
        this.mMeasureVerticalWithPaddingMargin = false;
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
        queryCompatibilityModes(context);
    }

    private void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.RelativeLayout, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.RelativeLayout, attrs, a, defStyleAttr, defStyleRes);
        this.mIgnoreGravity = a.getResourceId(1, -1);
        this.mGravity = a.getInt(0, this.mGravity);
        a.recycle();
    }

    private void queryCompatibilityModes(Context context) {
        int version = context.getApplicationInfo().targetSdkVersion;
        this.mAllowBrokenMeasureSpecs = version <= 17;
        this.mMeasureVerticalWithPaddingMargin = version >= 18;
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @RemotableViewMethod
    public void setIgnoreGravity(int viewId) {
        this.mIgnoreGravity = viewId;
    }

    public int getIgnoreGravity() {
        return this.mIgnoreGravity;
    }

    public int getGravity() {
        return this.mGravity;
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

    @Override // android.view.View
    public int getBaseline() {
        View view = this.mBaselineView;
        return view != null ? view.getBaseline() : super.getBaseline();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = true;
    }

    private void sortChildren() {
        int count = getChildCount();
        View[] viewArr = this.mSortedVerticalChildren;
        if (viewArr == null || viewArr.length != count) {
            this.mSortedVerticalChildren = new View[count];
        }
        View[] viewArr2 = this.mSortedHorizontalChildren;
        if (viewArr2 == null || viewArr2.length != count) {
            this.mSortedHorizontalChildren = new View[count];
        }
        DependencyGraph graph = this.mGraph;
        graph.clear();
        for (int i = 0; i < count; i++) {
            graph.add(getChildAt(i));
        }
        graph.getSortedViews(this.mSortedVerticalChildren, RULES_VERTICAL);
        graph.getSortedViews(this.mSortedHorizontalChildren, RULES_HORIZONTAL);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int width;
        int myWidth;
        int myWidth2;
        int layoutDirection;
        View ignore;
        boolean isWrapContentHeight;
        int layoutDirection2;
        int myHeight;
        if (this.mDirtyHierarchy) {
            this.mDirtyHierarchy = false;
            sortChildren();
        }
        int myWidth3 = -1;
        int myHeight2 = -1;
        int width2 = 0;
        int height = 0;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != 0) {
            myWidth3 = widthSize;
        }
        if (heightMode != 0) {
            myHeight2 = heightSize;
        }
        if (widthMode == 1073741824) {
            width2 = myWidth3;
        }
        if (heightMode == 1073741824) {
            height = myHeight2;
        }
        View ignore2 = null;
        int i2 = this.mGravity;
        int gravity = 8388615 & i2;
        boolean horizontalGravity = (gravity == 8388611 || gravity == 0) ? false : true;
        int gravity2 = i2 & 112;
        boolean verticalGravity = (gravity2 == 48 || gravity2 == 0) ? false : true;
        boolean offsetHorizontalAxis = false;
        boolean offsetVerticalAxis = false;
        if ((horizontalGravity || verticalGravity) && (i = this.mIgnoreGravity) != -1) {
            ignore2 = findViewById(i);
        }
        boolean isWrapContentWidth = widthMode != 1073741824;
        boolean isWrapContentHeight2 = heightMode != 1073741824;
        int layoutDirection3 = getLayoutDirection();
        if (isLayoutRtl()) {
            width = width2;
            if (myWidth3 == -1) {
                myWidth3 = 65536;
            }
        } else {
            width = width2;
        }
        View[] views = this.mSortedHorizontalChildren;
        int height2 = height;
        int count = views.length;
        int widthMode2 = 0;
        while (true) {
            int heightMode2 = heightMode;
            if (widthMode2 >= count) {
                break;
            }
            View child = views[widthMode2];
            View[] views2 = views;
            int count2 = count;
            if (child.getVisibility() != 8) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                applyHorizontalSizeRules(params, myWidth3, params.getRules(layoutDirection3));
                measureChildHorizontal(child, params, myWidth3, myHeight2);
                if (positionChildHorizontal(child, params, myWidth3, isWrapContentWidth)) {
                    offsetHorizontalAxis = true;
                }
            }
            widthMode2++;
            views = views2;
            count = count2;
            heightMode = heightMode2;
        }
        View[] views3 = this.mSortedVerticalChildren;
        int count3 = views3.length;
        int targetSdkVersion = getContext().getApplicationInfo().targetSdkVersion;
        int right = Integer.MIN_VALUE;
        int bottom = Integer.MIN_VALUE;
        int layoutDirection4 = layoutDirection3;
        int left = Integer.MAX_VALUE;
        int bottom2 = Integer.MAX_VALUE;
        int height3 = height2;
        int heightSize2 = 0;
        int width3 = width;
        while (heightSize2 < count3) {
            int count4 = count3;
            View child2 = views3[heightSize2];
            View[] views4 = views3;
            int i3 = heightSize2;
            if (child2.getVisibility() == 8) {
                myHeight = myHeight2;
            } else {
                LayoutParams params2 = (LayoutParams) child2.getLayoutParams();
                applyVerticalSizeRules(params2, myHeight2, child2.getBaseline());
                measureChild(child2, params2, myWidth3, myHeight2);
                if (positionChildVertical(child2, params2, myHeight2, isWrapContentHeight2)) {
                    offsetVerticalAxis = true;
                }
                if (!isWrapContentWidth) {
                    myHeight = myHeight2;
                } else if (isLayoutRtl()) {
                    if (targetSdkVersion < 19) {
                        width3 = Math.max(width3, myWidth3 - params2.mLeft);
                        myHeight = myHeight2;
                    } else {
                        myHeight = myHeight2;
                        int myHeight3 = params2.leftMargin;
                        width3 = Math.max(width3, (myWidth3 - params2.mLeft) + myHeight3);
                    }
                } else {
                    myHeight = myHeight2;
                    if (targetSdkVersion < 19) {
                        width3 = Math.max(width3, params2.mRight);
                    } else {
                        width3 = Math.max(width3, params2.mRight + params2.rightMargin);
                    }
                }
                if (isWrapContentHeight2) {
                    if (targetSdkVersion < 19) {
                        height3 = Math.max(height3, params2.mBottom);
                    } else {
                        height3 = Math.max(height3, params2.mBottom + params2.bottomMargin);
                    }
                }
                if (child2 != ignore2 || verticalGravity) {
                    left = Math.min(left, params2.mLeft - params2.leftMargin);
                    bottom2 = Math.min(bottom2, params2.mTop - params2.topMargin);
                }
                if (child2 != ignore2 || horizontalGravity) {
                    int right2 = Math.max(right, params2.mRight + params2.rightMargin);
                    int right3 = params2.mBottom;
                    bottom = Math.max(bottom, right3 + params2.bottomMargin);
                    right = right2;
                    bottom2 = bottom2;
                }
            }
            heightSize2 = i3 + 1;
            count3 = count4;
            views3 = views4;
            myHeight2 = myHeight;
        }
        View[] views5 = views3;
        int count5 = count3;
        int i4 = right;
        int bottom3 = bottom;
        View baselineView = null;
        LayoutParams baselineParams = null;
        int targetSdkVersion2 = 0;
        while (true) {
            myWidth = myWidth3;
            myWidth2 = count5;
            if (targetSdkVersion2 >= myWidth2) {
                break;
            }
            View child3 = views5[targetSdkVersion2];
            View ignore3 = ignore2;
            int top = bottom2;
            if (child3.getVisibility() != 8) {
                LayoutParams childParams = (LayoutParams) child3.getLayoutParams();
                if (baselineView == null || baselineParams == null || compareLayoutPosition(childParams, baselineParams) < 0) {
                    baselineView = child3;
                    baselineParams = childParams;
                }
            }
            targetSdkVersion2++;
            ignore2 = ignore3;
            bottom2 = top;
            count5 = myWidth2;
            myWidth3 = myWidth;
        }
        int top2 = bottom2;
        View ignore4 = ignore2;
        this.mBaselineView = baselineView;
        if (isWrapContentWidth) {
            int width4 = width3 + this.mPaddingRight;
            if (this.mLayoutParams != null && this.mLayoutParams.width >= 0) {
                width4 = Math.max(width4, this.mLayoutParams.width);
            }
            width3 = resolveSize(Math.max(width4, getSuggestedMinimumWidth()), widthMeasureSpec);
            if (offsetHorizontalAxis) {
                int i5 = 0;
                while (i5 < myWidth2) {
                    View child4 = views5[i5];
                    View baselineView2 = baselineView;
                    LayoutParams baselineParams2 = baselineParams;
                    if (child4.getVisibility() == 8) {
                        layoutDirection2 = layoutDirection4;
                    } else {
                        LayoutParams params3 = (LayoutParams) child4.getLayoutParams();
                        layoutDirection2 = layoutDirection4;
                        int[] rules = params3.getRules(layoutDirection2);
                        if (rules[13] != 0 || rules[14] != 0) {
                            centerHorizontal(child4, params3, width3);
                        } else if (rules[11] != 0) {
                            int childWidth = child4.getMeasuredWidth();
                            params3.mLeft = (width3 - this.mPaddingRight) - childWidth;
                            params3.mRight = params3.mLeft + childWidth;
                        }
                    }
                    i5++;
                    layoutDirection4 = layoutDirection2;
                    baselineView = baselineView2;
                    baselineParams = baselineParams2;
                }
                layoutDirection = layoutDirection4;
            } else {
                layoutDirection = layoutDirection4;
            }
        } else {
            layoutDirection = layoutDirection4;
        }
        if (isWrapContentHeight2) {
            int height4 = height3 + this.mPaddingBottom;
            if (this.mLayoutParams != null && this.mLayoutParams.height >= 0) {
                height4 = Math.max(height4, this.mLayoutParams.height);
            }
            height3 = resolveSize(Math.max(height4, getSuggestedMinimumHeight()), heightMeasureSpec);
            if (offsetVerticalAxis) {
                int i6 = 0;
                while (i6 < myWidth2) {
                    View child5 = views5[i6];
                    if (child5.getVisibility() == 8) {
                        isWrapContentHeight = isWrapContentHeight2;
                    } else {
                        LayoutParams params4 = (LayoutParams) child5.getLayoutParams();
                        int[] rules2 = params4.getRules(layoutDirection);
                        if (rules2[13] != 0) {
                            isWrapContentHeight = isWrapContentHeight2;
                        } else if (rules2[15] != 0) {
                            isWrapContentHeight = isWrapContentHeight2;
                        } else if (rules2[12] == 0) {
                            isWrapContentHeight = isWrapContentHeight2;
                        } else {
                            int childHeight = child5.getMeasuredHeight();
                            isWrapContentHeight = isWrapContentHeight2;
                            params4.mTop = (height3 - this.mPaddingBottom) - childHeight;
                            params4.mBottom = params4.mTop + childHeight;
                        }
                        centerVertical(child5, params4, height3);
                    }
                    i6++;
                    isWrapContentHeight2 = isWrapContentHeight;
                }
            }
        }
        if (horizontalGravity || verticalGravity) {
            Rect selfBounds = this.mSelfBounds;
            selfBounds.set(this.mPaddingLeft, this.mPaddingTop, width3 - this.mPaddingRight, height3 - this.mPaddingBottom);
            Rect contentBounds = this.mContentBounds;
            Gravity.apply(this.mGravity, i4 - left, bottom3 - top2, selfBounds, contentBounds, layoutDirection);
            int horizontalOffset = contentBounds.left - left;
            int verticalOffset = contentBounds.top - top2;
            if (horizontalOffset != 0 || verticalOffset != 0) {
                int i7 = 0;
                while (i7 < myWidth2) {
                    Rect selfBounds2 = selfBounds;
                    View child6 = views5[i7];
                    int bottom4 = bottom3;
                    int bottom5 = child6.getVisibility();
                    Rect contentBounds2 = contentBounds;
                    if (bottom5 != 8) {
                        ignore = ignore4;
                        if (child6 != ignore) {
                            LayoutParams params5 = (LayoutParams) child6.getLayoutParams();
                            if (horizontalGravity) {
                                params5.mLeft += horizontalOffset;
                                params5.mRight += horizontalOffset;
                            }
                            if (verticalGravity) {
                                params5.mTop += verticalOffset;
                                params5.mBottom += verticalOffset;
                            }
                        }
                    } else {
                        ignore = ignore4;
                    }
                    i7++;
                    ignore4 = ignore;
                    selfBounds = selfBounds2;
                    bottom3 = bottom4;
                    contentBounds = contentBounds2;
                }
            }
        }
        if (isLayoutRtl()) {
            int offsetWidth = myWidth - width3;
            for (int i8 = 0; i8 < myWidth2; i8++) {
                View child7 = views5[i8];
                if (child7.getVisibility() != 8) {
                    LayoutParams params6 = (LayoutParams) child7.getLayoutParams();
                    params6.mLeft -= offsetWidth;
                    params6.mRight -= offsetWidth;
                }
            }
        }
        setMeasuredDimension(width3, height3);
    }

    private int compareLayoutPosition(LayoutParams p1, LayoutParams p2) {
        int topDiff = p1.mTop - p2.mTop;
        if (topDiff != 0) {
            return topDiff;
        }
        return p1.mLeft - p2.mLeft;
    }

    private void measureChild(View child, LayoutParams params, int myWidth, int myHeight) {
        int childWidthMeasureSpec = getChildMeasureSpec(params.mLeft, params.mRight, params.width, params.leftMargin, params.rightMargin, this.mPaddingLeft, this.mPaddingRight, myWidth);
        int childHeightMeasureSpec = getChildMeasureSpec(params.mTop, params.mBottom, params.height, params.topMargin, params.bottomMargin, this.mPaddingTop, this.mPaddingBottom, myHeight);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private void measureChildHorizontal(View child, LayoutParams params, int myWidth, int myHeight) {
        int maxHeight;
        int heightMode;
        int maxHeight2;
        int childWidthMeasureSpec = getChildMeasureSpec(params.mLeft, params.mRight, params.width, params.leftMargin, params.rightMargin, this.mPaddingLeft, this.mPaddingRight, myWidth);
        if (myHeight < 0 && !this.mAllowBrokenMeasureSpecs) {
            if (params.height >= 0) {
                maxHeight2 = View.MeasureSpec.makeMeasureSpec(params.height, 1073741824);
            } else {
                maxHeight2 = View.MeasureSpec.makeMeasureSpec(0, 0);
            }
        } else {
            if (this.mMeasureVerticalWithPaddingMargin) {
                maxHeight = Math.max(0, (((myHeight - this.mPaddingTop) - this.mPaddingBottom) - params.topMargin) - params.bottomMargin);
            } else {
                maxHeight = Math.max(0, myHeight);
            }
            if (params.height == -1) {
                heightMode = 1073741824;
            } else {
                heightMode = Integer.MIN_VALUE;
            }
            maxHeight2 = View.MeasureSpec.makeMeasureSpec(maxHeight, heightMode);
        }
        child.measure(childWidthMeasureSpec, maxHeight2);
    }

    private int getChildMeasureSpec(int childStart, int childEnd, int childSize, int startMargin, int endMargin, int startPadding, int endPadding, int mySize) {
        int childSpecSize;
        int childSpecMode;
        int childSpecMode2 = 0;
        int childSpecSize2 = 0;
        boolean isUnspecified = mySize < 0;
        if (isUnspecified && !this.mAllowBrokenMeasureSpecs) {
            if (childStart != Integer.MIN_VALUE && childEnd != Integer.MIN_VALUE) {
                childSpecSize = Math.max(0, childEnd - childStart);
                childSpecMode = 1073741824;
            } else if (childSize >= 0) {
                childSpecSize = childSize;
                childSpecMode = 1073741824;
            } else {
                childSpecSize = 0;
                childSpecMode = 0;
            }
            return View.MeasureSpec.makeMeasureSpec(childSpecSize, childSpecMode);
        }
        int tempStart = childStart;
        int tempEnd = childEnd;
        if (tempStart == Integer.MIN_VALUE) {
            tempStart = startPadding + startMargin;
        }
        if (tempEnd == Integer.MIN_VALUE) {
            tempEnd = (mySize - endPadding) - endMargin;
        }
        int maxAvailable = tempEnd - tempStart;
        int i = 1073741824;
        if (childStart != Integer.MIN_VALUE && childEnd != Integer.MIN_VALUE) {
            if (isUnspecified) {
                i = 0;
            }
            childSpecMode2 = i;
            childSpecSize2 = Math.max(0, maxAvailable);
        } else if (childSize >= 0) {
            childSpecMode2 = 1073741824;
            if (maxAvailable >= 0) {
                childSpecSize2 = Math.min(maxAvailable, childSize);
            } else {
                childSpecSize2 = childSize;
            }
        } else if (childSize == -1) {
            if (isUnspecified) {
                i = 0;
            }
            childSpecMode2 = i;
            childSpecSize2 = Math.max(0, maxAvailable);
        } else if (childSize == -2) {
            if (maxAvailable >= 0) {
                childSpecMode2 = Integer.MIN_VALUE;
                childSpecSize2 = maxAvailable;
            } else {
                childSpecMode2 = 0;
                childSpecSize2 = 0;
            }
        }
        return View.MeasureSpec.makeMeasureSpec(childSpecSize2, childSpecMode2);
    }

    private boolean positionChildHorizontal(View child, LayoutParams params, int myWidth, boolean wrapContent) {
        int layoutDirection = getLayoutDirection();
        int[] rules = params.getRules(layoutDirection);
        if (params.mLeft == Integer.MIN_VALUE && params.mRight != Integer.MIN_VALUE) {
            params.mLeft = params.mRight - child.getMeasuredWidth();
        } else if (params.mLeft != Integer.MIN_VALUE && params.mRight == Integer.MIN_VALUE) {
            params.mRight = params.mLeft + child.getMeasuredWidth();
        } else if (params.mLeft == Integer.MIN_VALUE && params.mRight == Integer.MIN_VALUE) {
            if (rules[13] != 0 || rules[14] != 0) {
                if (!wrapContent) {
                    centerHorizontal(child, params, myWidth);
                } else {
                    positionAtEdge(child, params, myWidth);
                }
                return true;
            }
            positionAtEdge(child, params, myWidth);
        }
        return rules[21] != 0;
    }

    private void positionAtEdge(View child, LayoutParams params, int myWidth) {
        if (isLayoutRtl()) {
            params.mRight = (myWidth - this.mPaddingRight) - params.rightMargin;
            params.mLeft = params.mRight - child.getMeasuredWidth();
            return;
        }
        params.mLeft = this.mPaddingLeft + params.leftMargin;
        params.mRight = params.mLeft + child.getMeasuredWidth();
    }

    private boolean positionChildVertical(View child, LayoutParams params, int myHeight, boolean wrapContent) {
        int[] rules = params.getRules();
        if (params.mTop == Integer.MIN_VALUE && params.mBottom != Integer.MIN_VALUE) {
            params.mTop = params.mBottom - child.getMeasuredHeight();
        } else if (params.mTop != Integer.MIN_VALUE && params.mBottom == Integer.MIN_VALUE) {
            params.mBottom = params.mTop + child.getMeasuredHeight();
        } else if (params.mTop == Integer.MIN_VALUE && params.mBottom == Integer.MIN_VALUE) {
            if (rules[13] != 0 || rules[15] != 0) {
                if (!wrapContent) {
                    centerVertical(child, params, myHeight);
                } else {
                    params.mTop = this.mPaddingTop + params.topMargin;
                    params.mBottom = params.mTop + child.getMeasuredHeight();
                }
                return true;
            }
            params.mTop = this.mPaddingTop + params.topMargin;
            params.mBottom = params.mTop + child.getMeasuredHeight();
        }
        return rules[12] != 0;
    }

    private void applyHorizontalSizeRules(LayoutParams childParams, int myWidth, int[] rules) {
        childParams.mLeft = Integer.MIN_VALUE;
        childParams.mRight = Integer.MIN_VALUE;
        LayoutParams anchorParams = getRelatedViewParams(rules, 0);
        if (anchorParams != null) {
            childParams.mRight = anchorParams.mLeft - (anchorParams.leftMargin + childParams.rightMargin);
        } else if (childParams.alignWithParent && rules[0] != 0 && myWidth >= 0) {
            childParams.mRight = (myWidth - this.mPaddingRight) - childParams.rightMargin;
        }
        LayoutParams anchorParams2 = getRelatedViewParams(rules, 1);
        if (anchorParams2 != null) {
            childParams.mLeft = anchorParams2.mRight + anchorParams2.rightMargin + childParams.leftMargin;
        } else if (childParams.alignWithParent && rules[1] != 0) {
            childParams.mLeft = this.mPaddingLeft + childParams.leftMargin;
        }
        LayoutParams anchorParams3 = getRelatedViewParams(rules, 5);
        if (anchorParams3 != null) {
            childParams.mLeft = anchorParams3.mLeft + childParams.leftMargin;
        } else if (childParams.alignWithParent && rules[5] != 0) {
            childParams.mLeft = this.mPaddingLeft + childParams.leftMargin;
        }
        LayoutParams anchorParams4 = getRelatedViewParams(rules, 7);
        if (anchorParams4 != null) {
            childParams.mRight = anchorParams4.mRight - childParams.rightMargin;
        } else if (childParams.alignWithParent && rules[7] != 0 && myWidth >= 0) {
            childParams.mRight = (myWidth - this.mPaddingRight) - childParams.rightMargin;
        }
        if (rules[9] != 0) {
            childParams.mLeft = this.mPaddingLeft + childParams.leftMargin;
        }
        if (rules[11] != 0 && myWidth >= 0) {
            childParams.mRight = (myWidth - this.mPaddingRight) - childParams.rightMargin;
        }
    }

    private void applyVerticalSizeRules(LayoutParams childParams, int myHeight, int myBaseline) {
        int[] rules = childParams.getRules();
        int baselineOffset = getRelatedViewBaselineOffset(rules);
        if (baselineOffset != -1) {
            if (myBaseline != -1) {
                baselineOffset -= myBaseline;
            }
            childParams.mTop = baselineOffset;
            childParams.mBottom = Integer.MIN_VALUE;
            return;
        }
        childParams.mTop = Integer.MIN_VALUE;
        childParams.mBottom = Integer.MIN_VALUE;
        LayoutParams anchorParams = getRelatedViewParams(rules, 2);
        if (anchorParams != null) {
            childParams.mBottom = anchorParams.mTop - (anchorParams.topMargin + childParams.bottomMargin);
        } else if (childParams.alignWithParent && rules[2] != 0 && myHeight >= 0) {
            childParams.mBottom = (myHeight - this.mPaddingBottom) - childParams.bottomMargin;
        }
        LayoutParams anchorParams2 = getRelatedViewParams(rules, 3);
        if (anchorParams2 != null) {
            childParams.mTop = anchorParams2.mBottom + anchorParams2.bottomMargin + childParams.topMargin;
        } else if (childParams.alignWithParent && rules[3] != 0) {
            childParams.mTop = this.mPaddingTop + childParams.topMargin;
        }
        LayoutParams anchorParams3 = getRelatedViewParams(rules, 6);
        if (anchorParams3 != null) {
            childParams.mTop = anchorParams3.mTop + childParams.topMargin;
        } else if (childParams.alignWithParent && rules[6] != 0) {
            childParams.mTop = this.mPaddingTop + childParams.topMargin;
        }
        LayoutParams anchorParams4 = getRelatedViewParams(rules, 8);
        if (anchorParams4 != null) {
            childParams.mBottom = anchorParams4.mBottom - childParams.bottomMargin;
        } else if (childParams.alignWithParent && rules[8] != 0 && myHeight >= 0) {
            childParams.mBottom = (myHeight - this.mPaddingBottom) - childParams.bottomMargin;
        }
        if (rules[10] != 0) {
            childParams.mTop = this.mPaddingTop + childParams.topMargin;
        }
        if (rules[12] != 0 && myHeight >= 0) {
            childParams.mBottom = (myHeight - this.mPaddingBottom) - childParams.bottomMargin;
        }
    }

    private View getRelatedView(int[] rules, int relation) {
        DependencyGraph.Node node;
        int id = rules[relation];
        if (id == 0 || (node = (DependencyGraph.Node) this.mGraph.mKeyNodes.get(id)) == null) {
            return null;
        }
        View v = node.view;
        while (v.getVisibility() == 8) {
            int[] rules2 = ((LayoutParams) v.getLayoutParams()).getRules(v.getLayoutDirection());
            DependencyGraph.Node node2 = (DependencyGraph.Node) this.mGraph.mKeyNodes.get(rules2[relation]);
            if (node2 == null || v == node2.view) {
                return null;
            }
            v = node2.view;
        }
        return v;
    }

    private LayoutParams getRelatedViewParams(int[] rules, int relation) {
        View v = getRelatedView(rules, relation);
        if (v != null) {
            ViewGroup.LayoutParams params = v.getLayoutParams();
            if (params instanceof LayoutParams) {
                return (LayoutParams) v.getLayoutParams();
            }
            return null;
        }
        return null;
    }

    private int getRelatedViewBaselineOffset(int[] rules) {
        int baseline;
        View v = getRelatedView(rules, 4);
        if (v != null && (baseline = v.getBaseline()) != -1) {
            ViewGroup.LayoutParams params = v.getLayoutParams();
            if (params instanceof LayoutParams) {
                LayoutParams anchorParams = (LayoutParams) v.getLayoutParams();
                return anchorParams.mTop + baseline;
            }
        }
        return -1;
    }

    private static void centerHorizontal(View child, LayoutParams params, int myWidth) {
        int childWidth = child.getMeasuredWidth();
        int left = (myWidth - childWidth) / 2;
        params.mLeft = left;
        params.mRight = left + childWidth;
    }

    private static void centerVertical(View child, LayoutParams params, int myHeight) {
        int childHeight = child.getMeasuredHeight();
        int top = (myHeight - childHeight) / 2;
        params.mTop = top;
        params.mBottom = top + childHeight;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams st = (LayoutParams) child.getLayoutParams();
                child.layout(st.mLeft, st.mTop, st.mRight, st.mBottom);
            }
        }
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
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

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent event) {
        if (this.mTopToBottomLeftToRightSet == null) {
            this.mTopToBottomLeftToRightSet = new TreeSet(new TopToBottomLeftToRightComparator());
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            this.mTopToBottomLeftToRightSet.add(getChildAt(i));
        }
        for (View view : this.mTopToBottomLeftToRightSet) {
            if (view.getVisibility() == 0 && view.dispatchPopulateAccessibilityEvent(event)) {
                this.mTopToBottomLeftToRightSet.clear();
                return true;
            }
        }
        this.mTopToBottomLeftToRightSet.clear();
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return RelativeLayout.class.getName();
    }

    /* loaded from: classes4.dex */
    private class TopToBottomLeftToRightComparator implements Comparator<View> {
        private TopToBottomLeftToRightComparator() {
        }

        @Override // java.util.Comparator
        public int compare(View first, View second) {
            int topDifference = first.getTop() - second.getTop();
            if (topDifference != 0) {
                return topDifference;
            }
            int leftDifference = first.getLeft() - second.getLeft();
            if (leftDifference != 0) {
                return leftDifference;
            }
            int heightDiference = first.getHeight() - second.getHeight();
            if (heightDiference != 0) {
                return heightDiference;
            }
            int widthDiference = first.getWidth() - second.getWidth();
            if (widthDiference != 0) {
                return widthDiference;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public boolean alignWithParent;
        private int mBottom;
        private int[] mInitialRules;
        private boolean mIsRtlCompatibilityMode;
        private int mLeft;
        private boolean mNeedsLayoutResolution;
        private int mRight;
        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT, indexMapping = {@ViewDebug.IntToString(from = 2, m86to = "above"), @ViewDebug.IntToString(from = 4, m86to = "alignBaseline"), @ViewDebug.IntToString(from = 8, m86to = "alignBottom"), @ViewDebug.IntToString(from = 5, m86to = "alignLeft"), @ViewDebug.IntToString(from = 12, m86to = "alignParentBottom"), @ViewDebug.IntToString(from = 9, m86to = "alignParentLeft"), @ViewDebug.IntToString(from = 11, m86to = "alignParentRight"), @ViewDebug.IntToString(from = 10, m86to = "alignParentTop"), @ViewDebug.IntToString(from = 7, m86to = "alignRight"), @ViewDebug.IntToString(from = 6, m86to = "alignTop"), @ViewDebug.IntToString(from = 3, m86to = "below"), @ViewDebug.IntToString(from = 14, m86to = "centerHorizontal"), @ViewDebug.IntToString(from = 13, m86to = "center"), @ViewDebug.IntToString(from = 15, m86to = "centerVertical"), @ViewDebug.IntToString(from = 0, m86to = "leftOf"), @ViewDebug.IntToString(from = 1, m86to = "rightOf"), @ViewDebug.IntToString(from = 18, m86to = "alignStart"), @ViewDebug.IntToString(from = 19, m86to = "alignEnd"), @ViewDebug.IntToString(from = 20, m86to = "alignParentStart"), @ViewDebug.IntToString(from = 21, m86to = "alignParentEnd"), @ViewDebug.IntToString(from = 16, m86to = "startOf"), @ViewDebug.IntToString(from = 17, m86to = "endOf")}, mapping = {@ViewDebug.IntToString(from = -1, m86to = "true"), @ViewDebug.IntToString(from = 0, m86to = "false/NO_ID")}, resolveId = true)
        private int[] mRules;
        private boolean mRulesChanged;
        private int mTop;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            boolean z;
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
            TypedArray a = c.obtainStyledAttributes(attrs, C4057R.styleable.RelativeLayout_Layout);
            int targetSdkVersion = c.getApplicationInfo().targetSdkVersion;
            if (targetSdkVersion >= 17 && c.getApplicationInfo().hasRtlSupport()) {
                z = false;
            } else {
                z = true;
            }
            this.mIsRtlCompatibilityMode = z;
            int[] rules = this.mRules;
            int[] initialRules = this.mInitialRules;
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case 0:
                        rules[0] = a.getResourceId(attr, 0);
                        break;
                    case 1:
                        rules[1] = a.getResourceId(attr, 0);
                        break;
                    case 2:
                        rules[2] = a.getResourceId(attr, 0);
                        break;
                    case 3:
                        rules[3] = a.getResourceId(attr, 0);
                        break;
                    case 4:
                        rules[4] = a.getResourceId(attr, 0);
                        break;
                    case 5:
                        rules[5] = a.getResourceId(attr, 0);
                        break;
                    case 6:
                        rules[6] = a.getResourceId(attr, 0);
                        break;
                    case 7:
                        rules[7] = a.getResourceId(attr, 0);
                        break;
                    case 8:
                        rules[8] = a.getResourceId(attr, 0);
                        break;
                    case 9:
                        rules[9] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 10:
                        rules[10] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 11:
                        rules[11] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 12:
                        rules[12] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 13:
                        rules[13] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 14:
                        rules[14] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 15:
                        rules[15] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 16:
                        this.alignWithParent = a.getBoolean(attr, false);
                        break;
                    case 17:
                        rules[16] = a.getResourceId(attr, 0);
                        break;
                    case 18:
                        rules[17] = a.getResourceId(attr, 0);
                        break;
                    case 19:
                        rules[18] = a.getResourceId(attr, 0);
                        break;
                    case 20:
                        rules[19] = a.getResourceId(attr, 0);
                        break;
                    case 21:
                        rules[20] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 22:
                        rules[21] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                }
            }
            this.mRulesChanged = true;
            System.arraycopy(rules, 0, initialRules, 0, 22);
            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            int[] iArr = new int[22];
            this.mRules = iArr;
            this.mInitialRules = new int[22];
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
            this.mIsRtlCompatibilityMode = source.mIsRtlCompatibilityMode;
            this.mRulesChanged = source.mRulesChanged;
            this.alignWithParent = source.alignWithParent;
            System.arraycopy(source.mRules, 0, iArr, 0, 22);
            System.arraycopy(source.mInitialRules, 0, this.mInitialRules, 0, 22);
        }

        @Override // android.view.ViewGroup.LayoutParams
        public String debug(String output) {
            return output + "ViewGroup.LayoutParams={ width=" + sizeToString(this.width) + ", height=" + sizeToString(this.height) + " }";
        }

        public void addRule(int verb) {
            addRule(verb, -1);
        }

        public void addRule(int verb, int subject) {
            if (!this.mNeedsLayoutResolution && isRelativeRule(verb) && this.mInitialRules[verb] != 0 && subject == 0) {
                this.mNeedsLayoutResolution = true;
            }
            this.mRules[verb] = subject;
            this.mInitialRules[verb] = subject;
            this.mRulesChanged = true;
        }

        public void removeRule(int verb) {
            addRule(verb, 0);
        }

        public int getRule(int verb) {
            return this.mRules[verb];
        }

        private boolean hasRelativeRules() {
            int[] iArr = this.mInitialRules;
            return (iArr[16] == 0 && iArr[17] == 0 && iArr[18] == 0 && iArr[19] == 0 && iArr[20] == 0 && iArr[21] == 0) ? false : true;
        }

        private boolean isRelativeRule(int rule) {
            return rule == 16 || rule == 17 || rule == 18 || rule == 19 || rule == 20 || rule == 21;
        }

        private void resolveRules(int layoutDirection) {
            boolean isLayoutRtl = layoutDirection == 1;
            System.arraycopy(this.mInitialRules, 0, this.mRules, 0, 22);
            if (this.mIsRtlCompatibilityMode) {
                int[] iArr = this.mRules;
                int i = iArr[18];
                if (i != 0) {
                    if (iArr[5] == 0) {
                        iArr[5] = i;
                    }
                    iArr[18] = 0;
                }
                int i2 = iArr[19];
                if (i2 != 0) {
                    if (iArr[7] == 0) {
                        iArr[7] = i2;
                    }
                    iArr[19] = 0;
                }
                int i3 = iArr[16];
                if (i3 != 0) {
                    if (iArr[0] == 0) {
                        iArr[0] = i3;
                    }
                    iArr[16] = 0;
                }
                int i4 = iArr[17];
                if (i4 != 0) {
                    if (iArr[1] == 0) {
                        iArr[1] = i4;
                    }
                    iArr[17] = 0;
                }
                int i5 = iArr[20];
                if (i5 != 0) {
                    if (iArr[9] == 0) {
                        iArr[9] = i5;
                    }
                    iArr[20] = 0;
                }
                int i6 = iArr[21];
                if (i6 != 0) {
                    if (iArr[11] == 0) {
                        iArr[11] = i6;
                    }
                    iArr[21] = 0;
                }
            } else {
                int[] iArr2 = this.mRules;
                int i7 = iArr2[18];
                if ((i7 != 0 || iArr2[19] != 0) && (iArr2[5] != 0 || iArr2[7] != 0)) {
                    iArr2[5] = 0;
                    iArr2[7] = 0;
                }
                if (i7 != 0) {
                    iArr2[isLayoutRtl ? (char) 7 : (char) 5] = i7;
                    iArr2[18] = 0;
                }
                int i8 = iArr2[19];
                if (i8 != 0) {
                    iArr2[isLayoutRtl ? (char) 5 : (char) 7] = i8;
                    iArr2[19] = 0;
                }
                int i9 = iArr2[16];
                if ((i9 != 0 || iArr2[17] != 0) && (iArr2[0] != 0 || iArr2[1] != 0)) {
                    iArr2[0] = 0;
                    iArr2[1] = 0;
                }
                if (i9 != 0) {
                    iArr2[isLayoutRtl ? (char) 1 : (char) 0] = i9;
                    iArr2[16] = 0;
                }
                int i10 = iArr2[17];
                if (i10 != 0) {
                    iArr2[isLayoutRtl ? (char) 0 : (char) 1] = i10;
                    iArr2[17] = 0;
                }
                int i11 = iArr2[20];
                if ((i11 != 0 || iArr2[21] != 0) && (iArr2[9] != 0 || iArr2[11] != 0)) {
                    iArr2[9] = 0;
                    iArr2[11] = 0;
                }
                if (i11 != 0) {
                    iArr2[isLayoutRtl ? (char) 11 : '\t'] = i11;
                    iArr2[20] = 0;
                }
                int i12 = iArr2[21];
                if (i12 != 0) {
                    iArr2[isLayoutRtl ? '\t' : (char) 11] = i12;
                    iArr2[21] = 0;
                }
            }
            this.mRulesChanged = false;
            this.mNeedsLayoutResolution = false;
        }

        public int[] getRules(int layoutDirection) {
            resolveLayoutDirection(layoutDirection);
            return this.mRules;
        }

        public int[] getRules() {
            return this.mRules;
        }

        @Override // android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        public void resolveLayoutDirection(int layoutDirection) {
            if (shouldResolveLayoutDirection(layoutDirection)) {
                resolveRules(layoutDirection);
            }
            super.resolveLayoutDirection(layoutDirection);
        }

        private boolean shouldResolveLayoutDirection(int layoutDirection) {
            return (this.mNeedsLayoutResolution || hasRelativeRules()) && (this.mRulesChanged || layoutDirection != getLayoutDirection());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        public void encodeProperties(ViewHierarchyEncoder encoder) {
            super.encodeProperties(encoder);
            encoder.addProperty("layout:alignWithParent", this.alignWithParent);
        }

        /* loaded from: classes4.dex */
        public static final class InspectionCompanion implements android.view.inspector.InspectionCompanion<LayoutParams> {
            private int mAboveId;
            private int mAlignBaselineId;
            private int mAlignBottomId;
            private int mAlignEndId;
            private int mAlignLeftId;
            private int mAlignParentBottomId;
            private int mAlignParentEndId;
            private int mAlignParentLeftId;
            private int mAlignParentRightId;
            private int mAlignParentStartId;
            private int mAlignParentTopId;
            private int mAlignRightId;
            private int mAlignStartId;
            private int mAlignTopId;
            private int mAlignWithParentIfMissingId;
            private int mBelowId;
            private int mCenterHorizontalId;
            private int mCenterInParentId;
            private int mCenterVerticalId;
            private boolean mPropertiesMapped;
            private int mToEndOfId;
            private int mToLeftOfId;
            private int mToRightOfId;
            private int mToStartOfId;

            @Override // android.view.inspector.InspectionCompanion
            public void mapProperties(PropertyMapper propertyMapper) {
                this.mPropertiesMapped = true;
                this.mAboveId = propertyMapper.mapResourceId("layout_above", 16843140);
                this.mAlignBaselineId = propertyMapper.mapResourceId("layout_alignBaseline", 16843142);
                this.mAlignBottomId = propertyMapper.mapResourceId("layout_alignBottom", 16843146);
                this.mAlignEndId = propertyMapper.mapResourceId("layout_alignEnd", 16843706);
                this.mAlignLeftId = propertyMapper.mapResourceId("layout_alignLeft", 16843143);
                this.mAlignParentBottomId = propertyMapper.mapBoolean("layout_alignParentBottom", 16843150);
                this.mAlignParentEndId = propertyMapper.mapBoolean("layout_alignParentEnd", 16843708);
                this.mAlignParentLeftId = propertyMapper.mapBoolean("layout_alignParentLeft", 16843147);
                this.mAlignParentRightId = propertyMapper.mapBoolean("layout_alignParentRight", 16843149);
                this.mAlignParentStartId = propertyMapper.mapBoolean("layout_alignParentStart", 16843707);
                this.mAlignParentTopId = propertyMapper.mapBoolean("layout_alignParentTop", 16843148);
                this.mAlignRightId = propertyMapper.mapResourceId("layout_alignRight", 16843145);
                this.mAlignStartId = propertyMapper.mapResourceId("layout_alignStart", 16843705);
                this.mAlignTopId = propertyMapper.mapResourceId("layout_alignTop", 16843144);
                this.mAlignWithParentIfMissingId = propertyMapper.mapBoolean("layout_alignWithParentIfMissing", 16843154);
                this.mBelowId = propertyMapper.mapResourceId("layout_below", 16843141);
                this.mCenterHorizontalId = propertyMapper.mapBoolean("layout_centerHorizontal", 16843152);
                this.mCenterInParentId = propertyMapper.mapBoolean("layout_centerInParent", 16843151);
                this.mCenterVerticalId = propertyMapper.mapBoolean("layout_centerVertical", 16843153);
                this.mToEndOfId = propertyMapper.mapResourceId("layout_toEndOf", 16843704);
                this.mToLeftOfId = propertyMapper.mapResourceId("layout_toLeftOf", 16843138);
                this.mToRightOfId = propertyMapper.mapResourceId("layout_toRightOf", 16843139);
                this.mToStartOfId = propertyMapper.mapResourceId("layout_toStartOf", 16843703);
            }

            @Override // android.view.inspector.InspectionCompanion
            public void readProperties(LayoutParams node, PropertyReader propertyReader) {
                if (!this.mPropertiesMapped) {
                    throw new InspectionCompanion.UninitializedPropertyMapException();
                }
                int[] rules = node.getRules();
                propertyReader.readResourceId(this.mAboveId, rules[2]);
                propertyReader.readResourceId(this.mAlignBaselineId, rules[4]);
                propertyReader.readResourceId(this.mAlignBottomId, rules[8]);
                propertyReader.readResourceId(this.mAlignEndId, rules[19]);
                propertyReader.readResourceId(this.mAlignLeftId, rules[5]);
                propertyReader.readBoolean(this.mAlignParentBottomId, rules[12] == -1);
                propertyReader.readBoolean(this.mAlignParentEndId, rules[21] == -1);
                propertyReader.readBoolean(this.mAlignParentLeftId, rules[9] == -1);
                propertyReader.readBoolean(this.mAlignParentRightId, rules[11] == -1);
                propertyReader.readBoolean(this.mAlignParentStartId, rules[20] == -1);
                propertyReader.readBoolean(this.mAlignParentTopId, rules[10] == -1);
                propertyReader.readResourceId(this.mAlignRightId, rules[7]);
                propertyReader.readResourceId(this.mAlignStartId, rules[18]);
                propertyReader.readResourceId(this.mAlignTopId, rules[6]);
                propertyReader.readBoolean(this.mAlignWithParentIfMissingId, node.alignWithParent);
                propertyReader.readResourceId(this.mBelowId, rules[3]);
                propertyReader.readBoolean(this.mCenterHorizontalId, rules[14] == -1);
                propertyReader.readBoolean(this.mCenterInParentId, rules[13] == -1);
                propertyReader.readBoolean(this.mCenterVerticalId, rules[15] == -1);
                propertyReader.readResourceId(this.mToEndOfId, rules[17]);
                propertyReader.readResourceId(this.mToLeftOfId, rules[0]);
                propertyReader.readResourceId(this.mToRightOfId, rules[1]);
                propertyReader.readResourceId(this.mToStartOfId, rules[16]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class DependencyGraph {
        private SparseArray<Node> mKeyNodes;
        private ArrayList<Node> mNodes;
        private ArrayDeque<Node> mRoots;

        private DependencyGraph() {
            this.mNodes = new ArrayList<>();
            this.mKeyNodes = new SparseArray<>();
            this.mRoots = new ArrayDeque<>();
        }

        void clear() {
            ArrayList<Node> nodes = this.mNodes;
            int count = nodes.size();
            for (int i = 0; i < count; i++) {
                nodes.get(i).release();
            }
            nodes.clear();
            this.mKeyNodes.clear();
            this.mRoots.clear();
        }

        void add(View view) {
            int id = view.getId();
            Node node = Node.acquire(view);
            if (id != -1) {
                this.mKeyNodes.put(id, node);
            }
            this.mNodes.add(node);
        }

        void getSortedViews(View[] sorted, int... rules) {
            ArrayDeque<Node> roots = findRoots(rules);
            int index = 0;
            while (true) {
                Node node = roots.pollLast();
                if (node == null) {
                    break;
                }
                View view = node.view;
                int key = view.getId();
                int index2 = index + 1;
                sorted[index] = view;
                ArrayMap<Node, DependencyGraph> dependents = node.dependents;
                int count = dependents.size();
                for (int i = 0; i < count; i++) {
                    Node dependent = dependents.keyAt(i);
                    SparseArray<Node> dependencies = dependent.dependencies;
                    dependencies.remove(key);
                    if (dependencies.size() == 0) {
                        roots.add(dependent);
                    }
                }
                index = index2;
            }
            if (index < sorted.length) {
                throw new IllegalStateException("Circular dependencies cannot exist in RelativeLayout");
            }
        }

        private ArrayDeque<Node> findRoots(int[] rulesFilter) {
            Node dependency;
            SparseArray<Node> keyNodes = this.mKeyNodes;
            ArrayList<Node> nodes = this.mNodes;
            int count = nodes.size();
            for (int i = 0; i < count; i++) {
                Node node = nodes.get(i);
                node.dependents.clear();
                node.dependencies.clear();
            }
            for (int i2 = 0; i2 < count; i2++) {
                Node node2 = nodes.get(i2);
                LayoutParams layoutParams = (LayoutParams) node2.view.getLayoutParams();
                int[] rules = layoutParams.mRules;
                for (int i3 : rulesFilter) {
                    int rule = rules[i3];
                    if ((rule > 0 || ResourceId.isValid(rule)) && (dependency = keyNodes.get(rule)) != null && dependency != node2) {
                        dependency.dependents.put(node2, this);
                        node2.dependencies.put(rule, dependency);
                    }
                }
            }
            ArrayDeque<Node> roots = this.mRoots;
            roots.clear();
            for (int i4 = 0; i4 < count; i4++) {
                Node node3 = nodes.get(i4);
                if (node3.dependencies.size() == 0) {
                    roots.addLast(node3);
                }
            }
            return roots;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes4.dex */
        public static class Node {
            private static final int POOL_LIMIT = 100;
            private static final Pools.SynchronizedPool<Node> sPool = new Pools.SynchronizedPool<>(100);
            View view;
            final ArrayMap<Node, DependencyGraph> dependents = new ArrayMap<>();
            final SparseArray<Node> dependencies = new SparseArray<>();

            Node() {
            }

            static Node acquire(View view) {
                Node node = sPool.acquire();
                if (node == null) {
                    node = new Node();
                }
                node.view = view;
                return node;
            }

            void release() {
                this.view = null;
                this.dependents.clear();
                this.dependencies.clear();
                sPool.release(this);
            }
        }
    }
}
