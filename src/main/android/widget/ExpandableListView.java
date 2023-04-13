package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.ExpandableListConnector;
import com.android.internal.C4057R;
import java.util.ArrayList;
/* loaded from: classes4.dex */
public class ExpandableListView extends ListView {
    public static final int CHILD_INDICATOR_INHERIT = -1;
    private static final int[] CHILD_LAST_STATE_SET = {16842918};
    private static final int[] EMPTY_STATE_SET;
    private static final int[] GROUP_EMPTY_STATE_SET;
    private static final int[] GROUP_EXPANDED_EMPTY_STATE_SET;
    private static final int[] GROUP_EXPANDED_STATE_SET;
    private static final int[][] GROUP_STATE_SETS;
    private static final int INDICATOR_UNDEFINED = -2;
    private static final long PACKED_POSITION_INT_MASK_CHILD = -1;
    private static final long PACKED_POSITION_INT_MASK_GROUP = 2147483647L;
    private static final long PACKED_POSITION_MASK_CHILD = 4294967295L;
    private static final long PACKED_POSITION_MASK_GROUP = 9223372032559808512L;
    private static final long PACKED_POSITION_MASK_TYPE = Long.MIN_VALUE;
    private static final long PACKED_POSITION_SHIFT_GROUP = 32;
    private static final long PACKED_POSITION_SHIFT_TYPE = 63;
    public static final int PACKED_POSITION_TYPE_CHILD = 1;
    public static final int PACKED_POSITION_TYPE_GROUP = 0;
    public static final int PACKED_POSITION_TYPE_NULL = 2;
    public static final long PACKED_POSITION_VALUE_NULL = 4294967295L;
    private ExpandableListAdapter mAdapter;
    private Drawable mChildDivider;
    private Drawable mChildIndicator;
    private int mChildIndicatorEnd;
    private int mChildIndicatorLeft;
    private int mChildIndicatorRight;
    private int mChildIndicatorStart;
    private ExpandableListConnector mConnector;
    private Drawable mGroupIndicator;
    private int mIndicatorEnd;
    private int mIndicatorLeft;
    private final Rect mIndicatorRect;
    private int mIndicatorRight;
    private int mIndicatorStart;
    private OnChildClickListener mOnChildClickListener;
    private OnGroupClickListener mOnGroupClickListener;
    private OnGroupCollapseListener mOnGroupCollapseListener;
    private OnGroupExpandListener mOnGroupExpandListener;

    /* loaded from: classes4.dex */
    public interface OnChildClickListener {
        boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j);
    }

    /* loaded from: classes4.dex */
    public interface OnGroupClickListener {
        boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long j);
    }

    /* loaded from: classes4.dex */
    public interface OnGroupCollapseListener {
        void onGroupCollapse(int i);
    }

    /* loaded from: classes4.dex */
    public interface OnGroupExpandListener {
        void onGroupExpand(int i);
    }

    static {
        int[] iArr = new int[0];
        EMPTY_STATE_SET = iArr;
        int[] iArr2 = {16842920};
        GROUP_EXPANDED_STATE_SET = iArr2;
        int[] iArr3 = {16842921};
        GROUP_EMPTY_STATE_SET = iArr3;
        int[] iArr4 = {16842920, 16842921};
        GROUP_EXPANDED_EMPTY_STATE_SET = iArr4;
        GROUP_STATE_SETS = new int[][]{iArr, iArr2, iArr3, iArr4};
    }

    public ExpandableListView(Context context) {
        this(context, null);
    }

    public ExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842863);
    }

    public ExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ExpandableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Drawable drawable;
        this.mIndicatorRect = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.ExpandableListView, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.ExpandableListView, attrs, a, defStyleAttr, defStyleRes);
        this.mGroupIndicator = a.getDrawable(0);
        this.mChildIndicator = a.getDrawable(1);
        this.mIndicatorLeft = a.getDimensionPixelSize(2, 0);
        int dimensionPixelSize = a.getDimensionPixelSize(3, 0);
        this.mIndicatorRight = dimensionPixelSize;
        if (dimensionPixelSize == 0 && (drawable = this.mGroupIndicator) != null) {
            this.mIndicatorRight = this.mIndicatorLeft + drawable.getIntrinsicWidth();
        }
        this.mChildIndicatorLeft = a.getDimensionPixelSize(4, -1);
        this.mChildIndicatorRight = a.getDimensionPixelSize(5, -1);
        this.mChildDivider = a.getDrawable(6);
        if (!isRtlCompatibilityMode()) {
            this.mIndicatorStart = a.getDimensionPixelSize(7, -2);
            this.mIndicatorEnd = a.getDimensionPixelSize(8, -2);
            this.mChildIndicatorStart = a.getDimensionPixelSize(9, -1);
            this.mChildIndicatorEnd = a.getDimensionPixelSize(10, -1);
        }
        a.recycle();
    }

    private boolean isRtlCompatibilityMode() {
        int targetSdkVersion = this.mContext.getApplicationInfo().targetSdkVersion;
        return targetSdkVersion < 17 || !hasRtlSupport();
    }

    private boolean hasRtlSupport() {
        return this.mContext.getApplicationInfo().hasRtlSupport();
    }

    @Override // android.widget.AbsListView, android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        resolveIndicator();
        resolveChildIndicator();
    }

    private void resolveIndicator() {
        Drawable drawable;
        boolean isLayoutRtl = isLayoutRtl();
        if (isLayoutRtl) {
            int i = this.mIndicatorStart;
            if (i >= 0) {
                this.mIndicatorRight = i;
            }
            int i2 = this.mIndicatorEnd;
            if (i2 >= 0) {
                this.mIndicatorLeft = i2;
            }
        } else {
            int i3 = this.mIndicatorStart;
            if (i3 >= 0) {
                this.mIndicatorLeft = i3;
            }
            int i4 = this.mIndicatorEnd;
            if (i4 >= 0) {
                this.mIndicatorRight = i4;
            }
        }
        if (this.mIndicatorRight == 0 && (drawable = this.mGroupIndicator) != null) {
            this.mIndicatorRight = this.mIndicatorLeft + drawable.getIntrinsicWidth();
        }
    }

    private void resolveChildIndicator() {
        boolean isLayoutRtl = isLayoutRtl();
        if (isLayoutRtl) {
            int i = this.mChildIndicatorStart;
            if (i >= -1) {
                this.mChildIndicatorRight = i;
            }
            int i2 = this.mChildIndicatorEnd;
            if (i2 >= -1) {
                this.mChildIndicatorLeft = i2;
                return;
            }
            return;
        }
        int i3 = this.mChildIndicatorStart;
        if (i3 >= -1) {
            this.mChildIndicatorLeft = i3;
        }
        int i4 = this.mChildIndicatorEnd;
        if (i4 >= -1) {
            this.mChildIndicatorRight = i4;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ListView, android.widget.AbsListView, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        int headerViewsCount;
        int lastChildFlPos;
        super.dispatchDraw(canvas);
        if (this.mChildIndicator == null && this.mGroupIndicator == null) {
            return;
        }
        int saveCount = 0;
        boolean clipToPadding = (this.mGroupFlags & 34) == 34;
        if (clipToPadding) {
            saveCount = canvas.save();
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            canvas.clipRect(this.mPaddingLeft + scrollX, this.mPaddingTop + scrollY, ((this.mRight + scrollX) - this.mLeft) - this.mPaddingRight, ((this.mBottom + scrollY) - this.mTop) - this.mPaddingBottom);
        }
        int headerViewsCount2 = getHeaderViewsCount();
        int lastChildFlPos2 = ((this.mItemCount - getFooterViewsCount()) - headerViewsCount2) - 1;
        int myB = this.mBottom;
        int lastItemType = -4;
        Rect indicatorRect = this.mIndicatorRect;
        int childCount = getChildCount();
        int i = 0;
        int childFlPos = this.mFirstPosition - headerViewsCount2;
        while (i < childCount) {
            if (childFlPos < 0) {
                headerViewsCount = headerViewsCount2;
                lastChildFlPos = lastChildFlPos2;
            } else if (childFlPos > lastChildFlPos2) {
                break;
            } else {
                View item = getChildAt(i);
                int t = item.getTop();
                int b = item.getBottom();
                if (b < 0) {
                    headerViewsCount = headerViewsCount2;
                    lastChildFlPos = lastChildFlPos2;
                } else if (t > myB) {
                    headerViewsCount = headerViewsCount2;
                    lastChildFlPos = lastChildFlPos2;
                } else {
                    ExpandableListConnector.PositionMetadata pos = this.mConnector.getUnflattenedPos(childFlPos);
                    boolean isLayoutRtl = isLayoutRtl();
                    int width = getWidth();
                    headerViewsCount = headerViewsCount2;
                    if (pos.position.type == lastItemType) {
                        lastChildFlPos = lastChildFlPos2;
                    } else {
                        lastChildFlPos = lastChildFlPos2;
                        if (pos.position.type == 1) {
                            int i2 = this.mChildIndicatorLeft;
                            if (i2 == -1) {
                                i2 = this.mIndicatorLeft;
                            }
                            indicatorRect.left = i2;
                            int i3 = this.mChildIndicatorRight;
                            if (i3 == -1) {
                                i3 = this.mIndicatorRight;
                            }
                            indicatorRect.right = i3;
                        } else {
                            indicatorRect.left = this.mIndicatorLeft;
                            indicatorRect.right = this.mIndicatorRight;
                        }
                        if (!isLayoutRtl) {
                            indicatorRect.left += this.mPaddingLeft;
                            indicatorRect.right += this.mPaddingLeft;
                        } else {
                            int temp = indicatorRect.left;
                            indicatorRect.left = width - indicatorRect.right;
                            indicatorRect.right = width - temp;
                            int i4 = indicatorRect.left;
                            int temp2 = this.mPaddingRight;
                            indicatorRect.left = i4 - temp2;
                            indicatorRect.right -= this.mPaddingRight;
                        }
                        lastItemType = pos.position.type;
                    }
                    if (indicatorRect.left != indicatorRect.right) {
                        if (this.mStackFromBottom) {
                            indicatorRect.top = t;
                            indicatorRect.bottom = b;
                        } else {
                            indicatorRect.top = t;
                            indicatorRect.bottom = b;
                        }
                        Drawable indicator = getIndicator(pos);
                        if (indicator != null) {
                            indicator.setBounds(indicatorRect);
                            indicator.draw(canvas);
                        }
                    }
                    pos.recycle();
                }
            }
            i++;
            childFlPos++;
            headerViewsCount2 = headerViewsCount;
            lastChildFlPos2 = lastChildFlPos;
        }
        if (clipToPadding) {
            canvas.restoreToCount(saveCount);
        }
    }

    private Drawable getIndicator(ExpandableListConnector.PositionMetadata pos) {
        Drawable indicator;
        int[] stateSet;
        if (pos.position.type == 2) {
            indicator = this.mGroupIndicator;
            if (indicator != null && indicator.isStateful()) {
                boolean isEmpty = pos.groupMetadata == null || pos.groupMetadata.lastChildFlPos == pos.groupMetadata.flPos;
                int stateSetIndex = (isEmpty ? (char) 2 : (char) 0) | pos.isExpanded();
                indicator.setState(GROUP_STATE_SETS[stateSetIndex]);
            }
        } else {
            indicator = this.mChildIndicator;
            if (indicator != null && indicator.isStateful()) {
                if (pos.position.flatListPos == pos.groupMetadata.lastChildFlPos) {
                    stateSet = CHILD_LAST_STATE_SET;
                } else {
                    stateSet = EMPTY_STATE_SET;
                }
                indicator.setState(stateSet);
            }
        }
        return indicator;
    }

    public void setChildDivider(Drawable childDivider) {
        this.mChildDivider = childDivider;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.ListView
    public void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
        int flatListPosition = this.mFirstPosition + childIndex;
        if (flatListPosition >= 0) {
            int adjustedPosition = getFlatPositionForConnector(flatListPosition);
            ExpandableListConnector.PositionMetadata pos = this.mConnector.getUnflattenedPos(adjustedPosition);
            if (pos.position.type == 1 || (pos.isExpanded() && pos.groupMetadata.lastChildFlPos != pos.groupMetadata.flPos)) {
                Drawable divider = this.mChildDivider;
                divider.setBounds(bounds);
                divider.draw(canvas);
                pos.recycle();
                return;
            }
            pos.recycle();
        }
        super.drawDivider(canvas, bounds, flatListPosition);
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.widget.AdapterView
    public void setAdapter(ListAdapter adapter) {
        throw new RuntimeException("For ExpandableListView, use setAdapter(ExpandableListAdapter) instead of setAdapter(ListAdapter)");
    }

    @Override // android.widget.ListView, android.widget.AdapterView
    public ListAdapter getAdapter() {
        return super.getAdapter();
    }

    @Override // android.widget.AdapterView
    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        super.setOnItemClickListener(l);
    }

    public void setAdapter(ExpandableListAdapter adapter) {
        this.mAdapter = adapter;
        if (adapter != null) {
            this.mConnector = new ExpandableListConnector(adapter);
        } else {
            this.mConnector = null;
        }
        super.setAdapter((ListAdapter) this.mConnector);
    }

    public ExpandableListAdapter getExpandableListAdapter() {
        return this.mAdapter;
    }

    private boolean isHeaderOrFooterPosition(int position) {
        int footerViewsStart = this.mItemCount - getFooterViewsCount();
        return position < getHeaderViewsCount() || position >= footerViewsStart;
    }

    private int getFlatPositionForConnector(int flatListPosition) {
        return flatListPosition - getHeaderViewsCount();
    }

    private int getAbsoluteFlatPosition(int flatListPosition) {
        return getHeaderViewsCount() + flatListPosition;
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView
    public boolean performItemClick(View v, int position, long id) {
        if (isHeaderOrFooterPosition(position)) {
            return super.performItemClick(v, position, id);
        }
        int adjustedPosition = getFlatPositionForConnector(position);
        return handleItemClick(v, adjustedPosition, id);
    }

    boolean handleItemClick(View v, int position, long id) {
        boolean returnValue;
        ExpandableListConnector.PositionMetadata posMetadata = this.mConnector.getUnflattenedPos(position);
        long id2 = getChildOrGroupId(posMetadata.position);
        if (posMetadata.position.type == 2) {
            OnGroupClickListener onGroupClickListener = this.mOnGroupClickListener;
            if (onGroupClickListener != null && onGroupClickListener.onGroupClick(this, v, posMetadata.position.groupPos, id2)) {
                posMetadata.recycle();
                return true;
            }
            if (posMetadata.isExpanded()) {
                this.mConnector.collapseGroup(posMetadata);
                playSoundEffect(0);
                OnGroupCollapseListener onGroupCollapseListener = this.mOnGroupCollapseListener;
                if (onGroupCollapseListener != null) {
                    onGroupCollapseListener.onGroupCollapse(posMetadata.position.groupPos);
                }
            } else {
                this.mConnector.expandGroup(posMetadata);
                playSoundEffect(0);
                OnGroupExpandListener onGroupExpandListener = this.mOnGroupExpandListener;
                if (onGroupExpandListener != null) {
                    onGroupExpandListener.onGroupExpand(posMetadata.position.groupPos);
                }
                int groupPos = posMetadata.position.groupPos;
                int groupFlatPos = posMetadata.position.flatListPos;
                int shiftedGroupPosition = getHeaderViewsCount() + groupFlatPos;
                smoothScrollToPosition(this.mAdapter.getChildrenCount(groupPos) + shiftedGroupPosition, shiftedGroupPosition);
            }
            returnValue = true;
        } else if (this.mOnChildClickListener != null) {
            playSoundEffect(0);
            return this.mOnChildClickListener.onChildClick(this, v, posMetadata.position.groupPos, posMetadata.position.childPos, id2);
        } else {
            returnValue = false;
        }
        posMetadata.recycle();
        return returnValue;
    }

    public boolean expandGroup(int groupPos) {
        return expandGroup(groupPos, false);
    }

    public boolean expandGroup(int groupPos, boolean animate) {
        ExpandableListPosition elGroupPos = ExpandableListPosition.obtain(2, groupPos, -1, -1);
        ExpandableListConnector.PositionMetadata pm = this.mConnector.getFlattenedPos(elGroupPos);
        elGroupPos.recycle();
        boolean retValue = this.mConnector.expandGroup(pm);
        OnGroupExpandListener onGroupExpandListener = this.mOnGroupExpandListener;
        if (onGroupExpandListener != null) {
            onGroupExpandListener.onGroupExpand(groupPos);
        }
        if (animate) {
            int groupFlatPos = pm.position.flatListPos;
            int shiftedGroupPosition = getHeaderViewsCount() + groupFlatPos;
            smoothScrollToPosition(this.mAdapter.getChildrenCount(groupPos) + shiftedGroupPosition, shiftedGroupPosition);
        }
        pm.recycle();
        return retValue;
    }

    public boolean collapseGroup(int groupPos) {
        boolean retValue = this.mConnector.collapseGroup(groupPos);
        OnGroupCollapseListener onGroupCollapseListener = this.mOnGroupCollapseListener;
        if (onGroupCollapseListener != null) {
            onGroupCollapseListener.onGroupCollapse(groupPos);
        }
        return retValue;
    }

    public void setOnGroupCollapseListener(OnGroupCollapseListener onGroupCollapseListener) {
        this.mOnGroupCollapseListener = onGroupCollapseListener;
    }

    public void setOnGroupExpandListener(OnGroupExpandListener onGroupExpandListener) {
        this.mOnGroupExpandListener = onGroupExpandListener;
    }

    public void setOnGroupClickListener(OnGroupClickListener onGroupClickListener) {
        this.mOnGroupClickListener = onGroupClickListener;
    }

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        this.mOnChildClickListener = onChildClickListener;
    }

    public long getExpandableListPosition(int flatListPosition) {
        if (isHeaderOrFooterPosition(flatListPosition)) {
            return 4294967295L;
        }
        int adjustedPosition = getFlatPositionForConnector(flatListPosition);
        ExpandableListConnector.PositionMetadata pm = this.mConnector.getUnflattenedPos(adjustedPosition);
        long packedPos = pm.position.getPackedPosition();
        pm.recycle();
        return packedPos;
    }

    public int getFlatListPosition(long packedPosition) {
        ExpandableListPosition elPackedPos = ExpandableListPosition.obtainPosition(packedPosition);
        ExpandableListConnector.PositionMetadata pm = this.mConnector.getFlattenedPos(elPackedPos);
        elPackedPos.recycle();
        int flatListPosition = pm.position.flatListPos;
        pm.recycle();
        return getAbsoluteFlatPosition(flatListPosition);
    }

    public long getSelectedPosition() {
        int selectedPos = getSelectedItemPosition();
        return getExpandableListPosition(selectedPos);
    }

    public long getSelectedId() {
        long packedPos = getSelectedPosition();
        if (packedPos == 4294967295L) {
            return -1L;
        }
        int groupPos = getPackedPositionGroup(packedPos);
        if (getPackedPositionType(packedPos) == 0) {
            return this.mAdapter.getGroupId(groupPos);
        }
        return this.mAdapter.getChildId(groupPos, getPackedPositionChild(packedPos));
    }

    public void setSelectedGroup(int groupPosition) {
        ExpandableListPosition elGroupPos = ExpandableListPosition.obtainGroupPosition(groupPosition);
        ExpandableListConnector.PositionMetadata pm = this.mConnector.getFlattenedPos(elGroupPos);
        elGroupPos.recycle();
        int absoluteFlatPosition = getAbsoluteFlatPosition(pm.position.flatListPos);
        super.setSelection(absoluteFlatPosition);
        pm.recycle();
    }

    public boolean setSelectedChild(int groupPosition, int childPosition, boolean shouldExpandGroup) {
        ExpandableListPosition elChildPos = ExpandableListPosition.obtainChildPosition(groupPosition, childPosition);
        ExpandableListConnector.PositionMetadata flatChildPos = this.mConnector.getFlattenedPos(elChildPos);
        if (flatChildPos == null) {
            if (!shouldExpandGroup) {
                return false;
            }
            expandGroup(groupPosition);
            flatChildPos = this.mConnector.getFlattenedPos(elChildPos);
            if (flatChildPos == null) {
                throw new IllegalStateException("Could not find child");
            }
        }
        int absoluteFlatPosition = getAbsoluteFlatPosition(flatChildPos.position.flatListPos);
        super.setSelection(absoluteFlatPosition);
        elChildPos.recycle();
        flatChildPos.recycle();
        return true;
    }

    public boolean isGroupExpanded(int groupPosition) {
        return this.mConnector.isGroupExpanded(groupPosition);
    }

    public static int getPackedPositionType(long packedPosition) {
        if (packedPosition == 4294967295L) {
            return 2;
        }
        if ((packedPosition & Long.MIN_VALUE) == Long.MIN_VALUE) {
            return 1;
        }
        return 0;
    }

    public static int getPackedPositionGroup(long packedPosition) {
        if (packedPosition == 4294967295L) {
            return -1;
        }
        return (int) ((PACKED_POSITION_MASK_GROUP & packedPosition) >> 32);
    }

    public static int getPackedPositionChild(long packedPosition) {
        if (packedPosition != 4294967295L && (packedPosition & Long.MIN_VALUE) == Long.MIN_VALUE) {
            return (int) (4294967295L & packedPosition);
        }
        return -1;
    }

    public static long getPackedPositionForChild(int groupPosition, int childPosition) {
        return ((groupPosition & PACKED_POSITION_INT_MASK_GROUP) << 32) | Long.MIN_VALUE | (childPosition & (-1));
    }

    public static long getPackedPositionForGroup(int groupPosition) {
        return (groupPosition & PACKED_POSITION_INT_MASK_GROUP) << 32;
    }

    @Override // android.widget.AbsListView
    ContextMenu.ContextMenuInfo createContextMenuInfo(View view, int flatListPosition, long id) {
        if (!isHeaderOrFooterPosition(flatListPosition)) {
            int adjustedPosition = getFlatPositionForConnector(flatListPosition);
            ExpandableListConnector.PositionMetadata pm = this.mConnector.getUnflattenedPos(adjustedPosition);
            ExpandableListPosition pos = pm.position;
            long id2 = getChildOrGroupId(pos);
            long packedPosition = pos.getPackedPosition();
            pm.recycle();
            return new ExpandableListContextMenuInfo(view, packedPosition, id2);
        }
        return new AdapterView.AdapterContextMenuInfo(view, flatListPosition, id);
    }

    @Override // android.widget.ListView, android.widget.AbsListView
    public void onInitializeAccessibilityNodeInfoForItem(View view, int position, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoForItem(view, position, info);
        ExpandableListConnector.PositionMetadata metadata = this.mConnector.getUnflattenedPos(position);
        if (metadata.position.type == 2) {
            if (isGroupExpanded(metadata.position.groupPos)) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE);
            } else {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND);
            }
        }
        metadata.recycle();
    }

    private long getChildOrGroupId(ExpandableListPosition position) {
        if (position.type == 1) {
            return this.mAdapter.getChildId(position.groupPos, position.childPos);
        }
        return this.mAdapter.getGroupId(position.groupPos);
    }

    public void setChildIndicator(Drawable childIndicator) {
        this.mChildIndicator = childIndicator;
    }

    public void setChildIndicatorBounds(int left, int right) {
        this.mChildIndicatorLeft = left;
        this.mChildIndicatorRight = right;
        resolveChildIndicator();
    }

    public void setChildIndicatorBoundsRelative(int start, int end) {
        this.mChildIndicatorStart = start;
        this.mChildIndicatorEnd = end;
        resolveChildIndicator();
    }

    public void setGroupIndicator(Drawable groupIndicator) {
        this.mGroupIndicator = groupIndicator;
        if (this.mIndicatorRight == 0 && groupIndicator != null) {
            this.mIndicatorRight = this.mIndicatorLeft + groupIndicator.getIntrinsicWidth();
        }
    }

    public void setIndicatorBounds(int left, int right) {
        this.mIndicatorLeft = left;
        this.mIndicatorRight = right;
        resolveIndicator();
    }

    public void setIndicatorBoundsRelative(int start, int end) {
        this.mIndicatorStart = start;
        this.mIndicatorEnd = end;
        resolveIndicator();
    }

    /* loaded from: classes4.dex */
    public static class ExpandableListContextMenuInfo implements ContextMenu.ContextMenuInfo {

        /* renamed from: id */
        public long f524id;
        public long packedPosition;
        public View targetView;

        public ExpandableListContextMenuInfo(View targetView, long packedPosition, long id) {
            this.targetView = targetView;
            this.packedPosition = packedPosition;
            this.f524id = id;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.ExpandableListView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        ArrayList<ExpandableListConnector.GroupMetadata> expandedGroupMetadataList;

        SavedState(Parcelable superState, ArrayList<ExpandableListConnector.GroupMetadata> expandedGroupMetadataList) {
            super(superState);
            this.expandedGroupMetadataList = expandedGroupMetadataList;
        }

        private SavedState(Parcel in) {
            super(in);
            ArrayList<ExpandableListConnector.GroupMetadata> arrayList = new ArrayList<>();
            this.expandedGroupMetadataList = arrayList;
            in.readList(arrayList, ExpandableListConnector.class.getClassLoader(), ExpandableListConnector.GroupMetadata.class);
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(this.expandedGroupMetadataList);
        }
    }

    @Override // android.widget.AbsListView, android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        ExpandableListConnector expandableListConnector = this.mConnector;
        return new SavedState(superState, expandableListConnector != null ? expandableListConnector.getExpandedGroupMetadataList() : null);
    }

    @Override // android.widget.AbsListView, android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (this.mConnector != null && ss.expandedGroupMetadataList != null) {
            this.mConnector.setExpandedGroupMetadataList(ss.expandedGroupMetadataList);
        }
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ExpandableListView.class.getName();
    }
}
