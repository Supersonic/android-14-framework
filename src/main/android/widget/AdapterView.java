package android.widget;

import android.app.slice.Slice;
import android.content.Context;
import android.database.DataSetObserver;
import android.p008os.Parcelable;
import android.p008os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.autofill.AutofillManager;
import android.widget.Adapter;
/* loaded from: classes4.dex */
public abstract class AdapterView<T extends Adapter> extends ViewGroup {
    public static final int INVALID_POSITION = -1;
    public static final long INVALID_ROW_ID = Long.MIN_VALUE;
    public static final int ITEM_VIEW_TYPE_HEADER_OR_FOOTER = -2;
    public static final int ITEM_VIEW_TYPE_IGNORE = -1;
    static final int SYNC_FIRST_POSITION = 1;
    static final int SYNC_MAX_DURATION_MILLIS = 100;
    static final int SYNC_SELECTED_POSITION = 0;
    boolean mBlockLayoutRequests;
    boolean mDataChanged;
    private boolean mDesiredFocusableInTouchModeState;
    private int mDesiredFocusableState;
    private View mEmptyView;
    @ViewDebug.ExportedProperty(category = "scrolling")
    int mFirstPosition;
    boolean mInLayout;
    @ViewDebug.ExportedProperty(category = Slice.HINT_LIST)
    int mItemCount;
    private int mLayoutHeight;
    boolean mNeedSync;
    @ViewDebug.ExportedProperty(category = Slice.HINT_LIST)
    int mNextSelectedPosition;
    long mNextSelectedRowId;
    int mOldItemCount;
    int mOldSelectedPosition;
    long mOldSelectedRowId;
    OnItemClickListener mOnItemClickListener;
    OnItemLongClickListener mOnItemLongClickListener;
    OnItemSelectedListener mOnItemSelectedListener;
    private AdapterView<T>.SelectionNotifier mPendingSelectionNotifier;
    @ViewDebug.ExportedProperty(category = Slice.HINT_LIST)
    int mSelectedPosition;
    long mSelectedRowId;
    private AdapterView<T>.SelectionNotifier mSelectionNotifier;
    int mSpecificTop;
    long mSyncHeight;
    int mSyncMode;
    int mSyncPosition;
    long mSyncRowId;

    /* loaded from: classes4.dex */
    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> adapterView, View view, int i, long j);
    }

    /* loaded from: classes4.dex */
    public interface OnItemLongClickListener {
        boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j);
    }

    /* loaded from: classes4.dex */
    public interface OnItemSelectedListener {
        void onItemSelected(AdapterView<?> adapterView, View view, int i, long j);

        void onNothingSelected(AdapterView<?> adapterView);
    }

    public abstract T getAdapter();

    public abstract View getSelectedView();

    public abstract void setAdapter(T t);

    public abstract void setSelection(int i);

    public AdapterView(Context context) {
        this(context, null);
    }

    public AdapterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AdapterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mFirstPosition = 0;
        this.mSyncRowId = Long.MIN_VALUE;
        this.mNeedSync = false;
        this.mInLayout = false;
        this.mNextSelectedPosition = -1;
        this.mNextSelectedRowId = Long.MIN_VALUE;
        this.mSelectedPosition = -1;
        this.mSelectedRowId = Long.MIN_VALUE;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        this.mDesiredFocusableState = 16;
        this.mBlockLayoutRequests = false;
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
        int focusable = getFocusable();
        this.mDesiredFocusableState = focusable;
        if (focusable == 16) {
            super.setFocusable(0);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public final OnItemClickListener getOnItemClickListener() {
        return this.mOnItemClickListener;
    }

    public boolean performItemClick(View view, int position, long id) {
        boolean result;
        if (this.mOnItemClickListener != null) {
            playSoundEffect(0);
            this.mOnItemClickListener.onItemClick(this, view, position, id);
            result = true;
        } else {
            result = false;
        }
        if (view != null) {
            view.sendAccessibilityEvent(1);
        }
        return result;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        this.mOnItemLongClickListener = listener;
    }

    public final OnItemLongClickListener getOnItemLongClickListener() {
        return this.mOnItemLongClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    public final OnItemSelectedListener getOnItemSelectedListener() {
        return this.mOnItemSelectedListener;
    }

    /* loaded from: classes4.dex */
    public static class AdapterContextMenuInfo implements ContextMenu.ContextMenuInfo {

        /* renamed from: id */
        public long f523id;
        public int position;
        public View targetView;

        public AdapterContextMenuInfo(View targetView, int position, long id) {
            this.targetView = targetView;
            this.position = position;
            this.f523id = id;
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View child) {
        throw new UnsupportedOperationException("addView(View) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index) {
        throw new UnsupportedOperationException("addView(View, int) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void addView(View child, ViewGroup.LayoutParams params) {
        throw new UnsupportedOperationException("addView(View, LayoutParams) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        throw new UnsupportedOperationException("addView(View, int, LayoutParams) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View child) {
        throw new UnsupportedOperationException("removeView(View) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void removeViewAt(int index) {
        throw new UnsupportedOperationException("removeViewAt(int) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void removeAllViews() {
        throw new UnsupportedOperationException("removeAllViews() is not supported in AdapterView");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mLayoutHeight = getHeight();
    }

    @ViewDebug.CapturedViewProperty
    public int getSelectedItemPosition() {
        return this.mNextSelectedPosition;
    }

    @ViewDebug.CapturedViewProperty
    public long getSelectedItemId() {
        return this.mNextSelectedRowId;
    }

    public Object getSelectedItem() {
        T adapter = getAdapter();
        int selection = getSelectedItemPosition();
        if (adapter != null && adapter.getCount() > 0 && selection >= 0) {
            return adapter.getItem(selection);
        }
        return null;
    }

    @ViewDebug.CapturedViewProperty
    public int getCount() {
        return this.mItemCount;
    }

    public int getPositionForView(View view) {
        View listItem = view;
        while (true) {
            try {
                View v = (View) listItem.getParent();
                if (v == null || v.equals(this)) {
                    break;
                }
                listItem = v;
            } catch (ClassCastException e) {
                return -1;
            }
        }
        if (listItem != null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (getChildAt(i).equals(listItem)) {
                    return this.mFirstPosition + i;
                }
            }
        }
        return -1;
    }

    public int getFirstVisiblePosition() {
        return this.mFirstPosition;
    }

    public int getLastVisiblePosition() {
        return (this.mFirstPosition + getChildCount()) - 1;
    }

    @RemotableViewMethod
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        boolean empty = true;
        if (emptyView != null && emptyView.getImportantForAccessibility() == 0) {
            emptyView.setImportantForAccessibility(1);
        }
        T adapter = getAdapter();
        if (adapter != null && !adapter.isEmpty()) {
            empty = false;
        }
        updateEmptyStatus(empty);
    }

    public View getEmptyView() {
        return this.mEmptyView;
    }

    boolean isInFilterMode() {
        return false;
    }

    @Override // android.view.View
    public void setFocusable(int focusable) {
        T adapter = getAdapter();
        int i = 0;
        boolean empty = adapter == null || adapter.getCount() == 0;
        this.mDesiredFocusableState = focusable;
        if ((focusable & 17) == 0) {
            this.mDesiredFocusableInTouchModeState = false;
        }
        if (!empty || isInFilterMode()) {
            i = focusable;
        }
        super.setFocusable(i);
    }

    @Override // android.view.View
    public void setFocusableInTouchMode(boolean focusable) {
        T adapter = getAdapter();
        boolean z = false;
        boolean empty = adapter == null || adapter.getCount() == 0;
        this.mDesiredFocusableInTouchModeState = focusable;
        if (focusable) {
            this.mDesiredFocusableState = 1;
        }
        if (focusable && (!empty || isInFilterMode())) {
            z = true;
        }
        super.setFocusableInTouchMode(z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkFocus() {
        T adapter = getAdapter();
        boolean z = true;
        boolean empty = adapter == null || adapter.getCount() == 0;
        boolean focusable = !empty || isInFilterMode();
        super.setFocusableInTouchMode(focusable && this.mDesiredFocusableInTouchModeState);
        super.setFocusable(focusable ? this.mDesiredFocusableState : 0);
        if (this.mEmptyView != null) {
            if (adapter != null && !adapter.isEmpty()) {
                z = false;
            }
            updateEmptyStatus(z);
        }
    }

    private void updateEmptyStatus(boolean empty) {
        if (isInFilterMode()) {
            empty = false;
        }
        if (empty) {
            View view = this.mEmptyView;
            if (view != null) {
                view.setVisibility(0);
                setVisibility(8);
            } else {
                setVisibility(0);
            }
            if (this.mDataChanged) {
                onLayout(false, this.mLeft, this.mTop, this.mRight, this.mBottom);
                return;
            }
            return;
        }
        View view2 = this.mEmptyView;
        if (view2 != null) {
            view2.setVisibility(8);
        }
        setVisibility(0);
    }

    public Object getItemAtPosition(int position) {
        T adapter = getAdapter();
        if (adapter == null || position < 0) {
            return null;
        }
        return adapter.getItem(position);
    }

    public long getItemIdAtPosition(int position) {
        T adapter = getAdapter();
        if (adapter == null || position < 0) {
            return Long.MIN_VALUE;
        }
        return adapter.getItemId(position);
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener l) {
        throw new RuntimeException("Don't call setOnClickListener for an AdapterView. You probably want setOnItemClickListener instead");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class AdapterDataSetObserver extends DataSetObserver {
        private Parcelable mInstanceState = null;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AdapterDataSetObserver() {
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            AdapterView.this.mDataChanged = true;
            AdapterView adapterView = AdapterView.this;
            adapterView.mOldItemCount = adapterView.mItemCount;
            AdapterView adapterView2 = AdapterView.this;
            adapterView2.mItemCount = adapterView2.getAdapter().getCount();
            if (AdapterView.this.getAdapter().hasStableIds() && this.mInstanceState != null && AdapterView.this.mOldItemCount == 0 && AdapterView.this.mItemCount > 0) {
                AdapterView.this.onRestoreInstanceState(this.mInstanceState);
                this.mInstanceState = null;
            } else {
                AdapterView.this.rememberSyncState();
            }
            AdapterView.this.checkFocus();
            AdapterView.this.requestLayout();
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            AdapterView.this.mDataChanged = true;
            if (AdapterView.this.getAdapter().hasStableIds()) {
                this.mInstanceState = AdapterView.this.onSaveInstanceState();
            }
            AdapterView adapterView = AdapterView.this;
            adapterView.mOldItemCount = adapterView.mItemCount;
            AdapterView.this.mItemCount = 0;
            AdapterView.this.mSelectedPosition = -1;
            AdapterView.this.mSelectedRowId = Long.MIN_VALUE;
            AdapterView.this.mNextSelectedPosition = -1;
            AdapterView.this.mNextSelectedRowId = Long.MIN_VALUE;
            AdapterView.this.mNeedSync = false;
            AdapterView.this.checkFocus();
            AdapterView.this.requestLayout();
        }

        public void clearSavedState() {
            this.mInstanceState = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mSelectionNotifier);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SelectionNotifier implements Runnable {
        private SelectionNotifier() {
        }

        @Override // java.lang.Runnable
        public void run() {
            AdapterView.this.mPendingSelectionNotifier = null;
            if (AdapterView.this.mDataChanged && AdapterView.this.getViewRootImpl() != null && AdapterView.this.getViewRootImpl().isLayoutRequested()) {
                if (AdapterView.this.getAdapter() != null) {
                    AdapterView.this.mPendingSelectionNotifier = this;
                    return;
                }
                return;
            }
            AdapterView.this.dispatchOnItemSelected();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void selectionChanged() {
        this.mPendingSelectionNotifier = null;
        if (this.mOnItemSelectedListener != null || AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (this.mInLayout || this.mBlockLayoutRequests) {
                AdapterView<T>.SelectionNotifier selectionNotifier = this.mSelectionNotifier;
                if (selectionNotifier == null) {
                    this.mSelectionNotifier = new SelectionNotifier();
                } else {
                    removeCallbacks(selectionNotifier);
                }
                post(this.mSelectionNotifier);
            } else {
                dispatchOnItemSelected();
            }
        }
        AutofillManager afm = (AutofillManager) this.mContext.getSystemService(AutofillManager.class);
        if (afm != null) {
            afm.notifyValueChanged(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchOnItemSelected() {
        fireOnSelected();
        performAccessibilityActionsOnSelected();
    }

    private void fireOnSelected() {
        if (this.mOnItemSelectedListener == null) {
            return;
        }
        int selection = getSelectedItemPosition();
        if (selection >= 0) {
            View v = getSelectedView();
            this.mOnItemSelectedListener.onItemSelected(this, v, selection, getAdapter().getItemId(selection));
            return;
        }
        this.mOnItemSelectedListener.onNothingSelected(this);
    }

    private void performAccessibilityActionsOnSelected() {
        if (!AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            return;
        }
        int position = getSelectedItemPosition();
        if (position >= 0) {
            post(new Runnable() { // from class: android.widget.AdapterView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AdapterView.this.lambda$performAccessibilityActionsOnSelected$0();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$performAccessibilityActionsOnSelected$0() {
        sendAccessibilityEvent(4);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent event) {
        View selectedView = getSelectedView();
        if (selectedView != null && selectedView.getVisibility() == 0 && selectedView.dispatchPopulateAccessibilityEvent(event)) {
            return true;
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean onRequestSendAccessibilityEventInternal(View child, AccessibilityEvent event) {
        if (super.onRequestSendAccessibilityEventInternal(child, event)) {
            AccessibilityEvent record = AccessibilityEvent.obtain();
            onInitializeAccessibilityEvent(record);
            child.dispatchPopulateAccessibilityEvent(record);
            event.appendRecord(record);
            return true;
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return AdapterView.class.getName();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        info.setScrollable(isScrollableForAccessibility());
        View selectedView = getSelectedView();
        if (selectedView != null) {
            info.setEnabled(selectedView.isEnabled());
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEventInternal(AccessibilityEvent event) {
        super.onInitializeAccessibilityEventInternal(event);
        event.setScrollable(isScrollableForAccessibility());
        View selectedView = getSelectedView();
        if (selectedView != null) {
            event.setEnabled(selectedView.isEnabled());
        }
        event.setCurrentItemIndex(getSelectedItemPosition());
        event.setFromIndex(getFirstVisiblePosition());
        event.setToIndex(getLastVisiblePosition());
        event.setItemCount(getCount());
    }

    private boolean isScrollableForAccessibility() {
        int itemCount;
        T adapter = getAdapter();
        if (adapter == null || (itemCount = adapter.getCount()) <= 0) {
            return false;
        }
        return getFirstVisiblePosition() > 0 || getLastVisiblePosition() < itemCount + (-1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean canAnimate() {
        return super.canAnimate() && this.mItemCount > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleDataChanged() {
        int count = this.mItemCount;
        boolean found = false;
        if (count > 0) {
            if (this.mNeedSync) {
                this.mNeedSync = false;
                int newPos = findSyncPosition();
                if (newPos >= 0 && lookForSelectablePosition(newPos, true) == newPos) {
                    setNextSelectedPositionInt(newPos);
                    found = true;
                }
            }
            if (!found) {
                int newPos2 = getSelectedItemPosition();
                if (newPos2 >= count) {
                    newPos2 = count - 1;
                }
                if (newPos2 < 0) {
                    newPos2 = 0;
                }
                int selectablePos = lookForSelectablePosition(newPos2, true);
                if (selectablePos < 0) {
                    selectablePos = lookForSelectablePosition(newPos2, false);
                }
                if (selectablePos >= 0) {
                    setNextSelectedPositionInt(selectablePos);
                    checkSelectionChanged();
                    found = true;
                }
            }
        }
        if (!found) {
            this.mSelectedPosition = -1;
            this.mSelectedRowId = Long.MIN_VALUE;
            this.mNextSelectedPosition = -1;
            this.mNextSelectedRowId = Long.MIN_VALUE;
            this.mNeedSync = false;
            checkSelectionChanged();
        }
        notifySubtreeAccessibilityStateChangedIfNeeded();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkSelectionChanged() {
        if (this.mSelectedPosition != this.mOldSelectedPosition || this.mSelectedRowId != this.mOldSelectedRowId) {
            selectionChanged();
            this.mOldSelectedPosition = this.mSelectedPosition;
            this.mOldSelectedRowId = this.mSelectedRowId;
        }
        AdapterView<T>.SelectionNotifier selectionNotifier = this.mPendingSelectionNotifier;
        if (selectionNotifier != null) {
            selectionNotifier.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int findSyncPosition() {
        int count = this.mItemCount;
        if (count == 0) {
            return -1;
        }
        long idToMatch = this.mSyncRowId;
        int seed = this.mSyncPosition;
        if (idToMatch == Long.MIN_VALUE) {
            return -1;
        }
        int seed2 = Math.min(count - 1, Math.max(0, seed));
        long endTime = SystemClock.uptimeMillis() + 100;
        int first = seed2;
        int last = seed2;
        boolean next = false;
        T adapter = getAdapter();
        if (adapter == null) {
            return -1;
        }
        while (SystemClock.uptimeMillis() <= endTime) {
            long rowId = adapter.getItemId(seed2);
            if (rowId == idToMatch) {
                return seed2;
            }
            boolean hitLast = last == count + (-1);
            boolean hitFirst = first == 0;
            if (hitLast && hitFirst) {
                break;
            } else if (hitFirst || (next && !hitLast)) {
                last++;
                seed2 = last;
                next = false;
            } else if (hitLast || (!next && !hitFirst)) {
                first--;
                seed2 = first;
                next = true;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int lookForSelectablePosition(int position, boolean lookDown) {
        return position;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSelectedPositionInt(int position) {
        this.mSelectedPosition = position;
        this.mSelectedRowId = getItemIdAtPosition(position);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNextSelectedPositionInt(int position) {
        this.mNextSelectedPosition = position;
        long itemIdAtPosition = getItemIdAtPosition(position);
        this.mNextSelectedRowId = itemIdAtPosition;
        if (this.mNeedSync && this.mSyncMode == 0 && position >= 0) {
            this.mSyncPosition = position;
            this.mSyncRowId = itemIdAtPosition;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void rememberSyncState() {
        if (getChildCount() > 0) {
            this.mNeedSync = true;
            this.mSyncHeight = this.mLayoutHeight;
            int i = this.mSelectedPosition;
            if (i >= 0) {
                View v = getChildAt(i - this.mFirstPosition);
                this.mSyncRowId = this.mNextSelectedRowId;
                this.mSyncPosition = this.mNextSelectedPosition;
                if (v != null) {
                    this.mSpecificTop = v.getTop();
                }
                this.mSyncMode = 0;
                return;
            }
            View v2 = getChildAt(0);
            T adapter = getAdapter();
            int i2 = this.mFirstPosition;
            if (i2 >= 0 && i2 < adapter.getCount()) {
                this.mSyncRowId = adapter.getItemId(this.mFirstPosition);
            } else {
                this.mSyncRowId = -1L;
            }
            this.mSyncPosition = this.mFirstPosition;
            if (v2 != null) {
                this.mSpecificTop = v2.getTop();
            }
            this.mSyncMode = 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void encodeProperties(ViewHierarchyEncoder encoder) {
        super.encodeProperties(encoder);
        encoder.addProperty("scrolling:firstPosition", this.mFirstPosition);
        encoder.addProperty("list:nextSelectedPosition", this.mNextSelectedPosition);
        encoder.addProperty("list:nextSelectedRowId", (float) this.mNextSelectedRowId);
        encoder.addProperty("list:selectedPosition", this.mSelectedPosition);
        encoder.addProperty("list:itemCount", this.mItemCount);
    }

    @Override // android.view.View
    public void onProvideAutofillStructure(ViewStructure structure, int flags) {
        super.onProvideAutofillStructure(structure, flags);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onProvideStructure(ViewStructure structure, int viewFor, int flags) {
        Adapter adapter;
        CharSequence[] options;
        super.onProvideStructure(structure, viewFor, flags);
        if ((viewFor == 1 || viewFor == 2) && (adapter = getAdapter()) != null && (options = adapter.getAutofillOptions()) != null) {
            structure.setAutofillOptions(options);
        }
    }
}
