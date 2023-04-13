package com.android.internal.widget;

import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaMetrics;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemClock;
import android.p008os.Trace;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.AbsSavedState;
import android.view.Display;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.OverScroller;
import com.android.internal.C4057R;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.widget.AdapterHelper;
import com.android.internal.widget.ChildHelper;
import com.android.internal.widget.GapWorker;
import com.android.internal.widget.ViewInfoStore;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes5.dex */
public class RecyclerView extends ViewGroup implements ScrollingView, NestedScrollingChild {
    static final boolean DEBUG = false;
    static final boolean DISPATCH_TEMP_DETACH = false;
    static final long FOREVER_NS = Long.MAX_VALUE;
    public static final int HORIZONTAL = 0;
    private static final int INVALID_POINTER = -1;
    public static final int INVALID_TYPE = -1;
    static final int MAX_SCROLL_DURATION = 2000;
    public static final long NO_ID = -1;
    public static final int NO_POSITION = -1;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    static final String TAG = "RecyclerView";
    public static final int TOUCH_SLOP_DEFAULT = 0;
    public static final int TOUCH_SLOP_PAGING = 1;
    static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
    static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
    private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
    static final String TRACE_NESTED_PREFETCH_TAG = "RV Nested Prefetch";
    private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
    private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
    static final String TRACE_PREFETCH_TAG = "RV Prefetch";
    static final String TRACE_SCROLL_TAG = "RV Scroll";
    public static final int VERTICAL = 1;
    RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
    private final AccessibilityManager mAccessibilityManager;
    private OnItemTouchListener mActiveOnItemTouchListener;
    Adapter mAdapter;
    AdapterHelper mAdapterHelper;
    boolean mAdapterUpdateDuringMeasure;
    private EdgeEffect mBottomGlow;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback;
    ChildHelper mChildHelper;
    boolean mClipToPadding;
    boolean mDataSetHasChangedAfterLayout;
    private int mDispatchScrollCounter;
    private int mEatRequestLayout;
    private int mEatenAccessibilityChangeFlags;
    boolean mFirstLayoutComplete;
    GapWorker mGapWorker;
    boolean mHasFixedSize;
    private boolean mIgnoreMotionEventTillDown;
    private int mInitialTouchX;
    private int mInitialTouchY;
    boolean mIsAttached;
    ItemAnimator mItemAnimator;
    private ItemAnimator.ItemAnimatorListener mItemAnimatorListener;
    private Runnable mItemAnimatorRunner;
    final ArrayList<ItemDecoration> mItemDecorations;
    boolean mItemsAddedOrRemoved;
    boolean mItemsChanged;
    private int mLastTouchX;
    private int mLastTouchY;
    LayoutManager mLayout;
    boolean mLayoutFrozen;
    private int mLayoutOrScrollCounter;
    boolean mLayoutRequestEaten;
    private EdgeEffect mLeftGlow;
    private final int mMaxFlingVelocity;
    private final int mMinFlingVelocity;
    private final int[] mMinMaxLayoutPositions;
    private final int[] mNestedOffsets;
    private final RecyclerViewDataObserver mObserver;
    private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
    private OnFlingListener mOnFlingListener;
    private final ArrayList<OnItemTouchListener> mOnItemTouchListeners;
    final List<ViewHolder> mPendingAccessibilityImportanceChange;
    private SavedState mPendingSavedState;
    boolean mPostedAnimatorRunner;
    GapWorker.LayoutPrefetchRegistryImpl mPrefetchRegistry;
    private boolean mPreserveFocusAfterLayout;
    final Recycler mRecycler;
    RecyclerListener mRecyclerListener;
    private EdgeEffect mRightGlow;
    private final int[] mScrollConsumed;
    private float mScrollFactor;
    private OnScrollListener mScrollListener;
    private List<OnScrollListener> mScrollListeners;
    private final int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    final State mState;
    final Rect mTempRect;
    private final Rect mTempRect2;
    final RectF mTempRectF;
    private EdgeEffect mTopGlow;
    private int mTouchSlop;
    final Runnable mUpdateChildViewsRunnable;
    private VelocityTracker mVelocityTracker;
    final ViewFlinger mViewFlinger;
    private final ViewInfoStore.ProcessCallback mViewInfoProcessCallback;
    final ViewInfoStore mViewInfoStore;
    private static final int[] NESTED_SCROLLING_ATTRS = {16843830};
    private static final int[] CLIP_TO_PADDING_ATTR = {16842987};
    static final boolean FORCE_INVALIDATE_DISPLAY_LIST = false;
    static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC = true;
    static final boolean POST_UPDATES_ON_ANIMATION = true;
    private static final boolean ALLOW_THREAD_GAP_WORK = true;
    private static final boolean FORCE_ABS_FOCUS_SEARCH_DIRECTION = false;
    private static final boolean IGNORE_DETACHED_FOCUSED_CHILD = false;
    private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE};
    static final Interpolator sQuinticInterpolator = new Interpolator() { // from class: com.android.internal.widget.RecyclerView.3
        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float t) {
            float t2 = t - 1.0f;
            return (t2 * t2 * t2 * t2 * t2) + 1.0f;
        }
    };

    /* loaded from: classes5.dex */
    public interface ChildDrawingOrderCallback {
        int onGetChildDrawingOrder(int i, int i2);
    }

    /* loaded from: classes5.dex */
    public interface OnChildAttachStateChangeListener {
        void onChildViewAttachedToWindow(View view);

        void onChildViewDetachedFromWindow(View view);
    }

    /* loaded from: classes5.dex */
    public static abstract class OnFlingListener {
        public abstract boolean onFling(int i, int i2);
    }

    /* loaded from: classes5.dex */
    public interface OnItemTouchListener {
        boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent);

        void onRequestDisallowInterceptTouchEvent(boolean z);

        void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent);
    }

    /* loaded from: classes5.dex */
    public interface RecyclerListener {
        void onViewRecycled(ViewHolder viewHolder);
    }

    /* loaded from: classes5.dex */
    public static abstract class ViewCacheExtension {
        public abstract View getViewForPositionAndType(Recycler recycler, int i, int i2);
    }

    public RecyclerView(Context context) {
        this(context, null);
    }

    public RecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        boolean z;
        this.mObserver = new RecyclerViewDataObserver();
        this.mRecycler = new Recycler();
        this.mViewInfoStore = new ViewInfoStore();
        this.mUpdateChildViewsRunnable = new Runnable() { // from class: com.android.internal.widget.RecyclerView.1
            @Override // java.lang.Runnable
            public void run() {
                if (!RecyclerView.this.mFirstLayoutComplete || RecyclerView.this.isLayoutRequested()) {
                    return;
                }
                if (!RecyclerView.this.mIsAttached) {
                    RecyclerView.this.requestLayout();
                } else if (RecyclerView.this.mLayoutFrozen) {
                    RecyclerView.this.mLayoutRequestEaten = true;
                } else {
                    RecyclerView.this.consumePendingUpdateOperations();
                }
            }
        };
        this.mTempRect = new Rect();
        this.mTempRect2 = new Rect();
        this.mTempRectF = new RectF();
        this.mItemDecorations = new ArrayList<>();
        this.mOnItemTouchListeners = new ArrayList<>();
        this.mEatRequestLayout = 0;
        this.mDataSetHasChangedAfterLayout = false;
        this.mLayoutOrScrollCounter = 0;
        this.mDispatchScrollCounter = 0;
        this.mItemAnimator = new DefaultItemAnimator();
        this.mScrollState = 0;
        this.mScrollPointerId = -1;
        this.mScrollFactor = Float.MIN_VALUE;
        this.mPreserveFocusAfterLayout = true;
        this.mViewFlinger = new ViewFlinger();
        this.mPrefetchRegistry = ALLOW_THREAD_GAP_WORK ? new GapWorker.LayoutPrefetchRegistryImpl() : null;
        this.mState = new State();
        this.mItemsAddedOrRemoved = false;
        this.mItemsChanged = false;
        this.mItemAnimatorListener = new ItemAnimatorRestoreListener();
        this.mPostedAnimatorRunner = false;
        this.mMinMaxLayoutPositions = new int[2];
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mNestedOffsets = new int[2];
        this.mPendingAccessibilityImportanceChange = new ArrayList();
        this.mItemAnimatorRunner = new Runnable() { // from class: com.android.internal.widget.RecyclerView.2
            @Override // java.lang.Runnable
            public void run() {
                if (RecyclerView.this.mItemAnimator != null) {
                    RecyclerView.this.mItemAnimator.runPendingAnimations();
                }
                RecyclerView.this.mPostedAnimatorRunner = false;
            }
        };
        this.mViewInfoProcessCallback = new ViewInfoStore.ProcessCallback() { // from class: com.android.internal.widget.RecyclerView.4
            @Override // com.android.internal.widget.ViewInfoStore.ProcessCallback
            public void processDisappeared(ViewHolder viewHolder, ItemAnimator.ItemHolderInfo info, ItemAnimator.ItemHolderInfo postInfo) {
                RecyclerView.this.mRecycler.unscrapView(viewHolder);
                RecyclerView.this.animateDisappearance(viewHolder, info, postInfo);
            }

            @Override // com.android.internal.widget.ViewInfoStore.ProcessCallback
            public void processAppeared(ViewHolder viewHolder, ItemAnimator.ItemHolderInfo preInfo, ItemAnimator.ItemHolderInfo info) {
                RecyclerView.this.animateAppearance(viewHolder, preInfo, info);
            }

            @Override // com.android.internal.widget.ViewInfoStore.ProcessCallback
            public void processPersistent(ViewHolder viewHolder, ItemAnimator.ItemHolderInfo preInfo, ItemAnimator.ItemHolderInfo postInfo) {
                viewHolder.setIsRecyclable(false);
                if (RecyclerView.this.mDataSetHasChangedAfterLayout) {
                    if (RecyclerView.this.mItemAnimator.animateChange(viewHolder, viewHolder, preInfo, postInfo)) {
                        RecyclerView.this.postAnimationRunner();
                    }
                } else if (RecyclerView.this.mItemAnimator.animatePersistence(viewHolder, preInfo, postInfo)) {
                    RecyclerView.this.postAnimationRunner();
                }
            }

            @Override // com.android.internal.widget.ViewInfoStore.ProcessCallback
            public void unused(ViewHolder viewHolder) {
                RecyclerView.this.mLayout.removeAndRecycleView(viewHolder.itemView, RecyclerView.this.mRecycler);
            }
        };
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, CLIP_TO_PADDING_ATTR, defStyle, 0);
            this.mClipToPadding = a.getBoolean(0, true);
            a.recycle();
        } else {
            this.mClipToPadding = true;
        }
        setScrollContainer(true);
        setFocusableInTouchMode(true);
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.mTouchSlop = vc.getScaledTouchSlop();
        this.mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        if (getOverScrollMode() != 2) {
            z = false;
        } else {
            z = true;
        }
        setWillNotDraw(z);
        this.mItemAnimator.setListener(this.mItemAnimatorListener);
        initAdapterManager();
        initChildrenHelper();
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
        boolean nestedScrollingEnabled = true;
        if (attrs != null) {
            TypedArray a2 = context.obtainStyledAttributes(attrs, C4057R.styleable.RecyclerView, defStyle, 0);
            String layoutManagerName = a2.getString(2);
            int descendantFocusability = a2.getInt(1, -1);
            if (descendantFocusability == -1) {
                setDescendantFocusability(262144);
            }
            a2.recycle();
            createLayoutManager(context, layoutManagerName, attrs, defStyle, 0);
            TypedArray a3 = context.obtainStyledAttributes(attrs, NESTED_SCROLLING_ATTRS, defStyle, 0);
            nestedScrollingEnabled = a3.getBoolean(0, true);
            a3.recycle();
        } else {
            setDescendantFocusability(262144);
        }
        context.obtainStyledAttributes(attrs, C4057R.styleable.EdgeEffect).recycle();
        setNestedScrollingEnabled(nestedScrollingEnabled);
    }

    public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public void setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate accessibilityDelegate) {
        this.mAccessibilityDelegate = accessibilityDelegate;
        setAccessibilityDelegate(accessibilityDelegate);
    }

    private void createLayoutManager(Context context, String className, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        ClassLoader classLoader;
        Constructor constructor;
        if (className != null) {
            String className2 = className.trim();
            if (className2.length() != 0) {
                String className3 = getFullClassName(context, className2);
                try {
                    if (isInEditMode()) {
                        classLoader = getClass().getClassLoader();
                    } else {
                        classLoader = context.getClassLoader();
                    }
                    Class<? extends U> asSubclass = classLoader.loadClass(className3).asSubclass(LayoutManager.class);
                    Object[] constructorArgs = null;
                    try {
                        constructor = asSubclass.getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
                        constructorArgs = new Object[]{context, attrs, Integer.valueOf(defStyleAttr), Integer.valueOf(defStyleRes)};
                    } catch (NoSuchMethodException e) {
                        try {
                            constructor = asSubclass.getConstructor(new Class[0]);
                        } catch (NoSuchMethodException e1) {
                            e1.initCause(e);
                            throw new IllegalStateException(attrs.getPositionDescription() + ": Error creating LayoutManager " + className3, e1);
                        }
                    }
                    constructor.setAccessible(true);
                    setLayoutManager((LayoutManager) constructor.newInstance(constructorArgs));
                } catch (ClassCastException e2) {
                    throw new IllegalStateException(attrs.getPositionDescription() + ": Class is not a LayoutManager " + className3, e2);
                } catch (ClassNotFoundException e3) {
                    throw new IllegalStateException(attrs.getPositionDescription() + ": Unable to find LayoutManager " + className3, e3);
                } catch (IllegalAccessException e4) {
                    throw new IllegalStateException(attrs.getPositionDescription() + ": Cannot access non-public constructor " + className3, e4);
                } catch (InstantiationException e5) {
                    throw new IllegalStateException(attrs.getPositionDescription() + ": Could not instantiate the LayoutManager: " + className3, e5);
                } catch (InvocationTargetException e6) {
                    throw new IllegalStateException(attrs.getPositionDescription() + ": Could not instantiate the LayoutManager: " + className3, e6);
                }
            }
        }
    }

    private String getFullClassName(Context context, String className) {
        if (className.charAt(0) == '.') {
            return context.getPackageName() + className;
        }
        if (className.contains(MediaMetrics.SEPARATOR)) {
            return className;
        }
        return RecyclerView.class.getPackage().getName() + '.' + className;
    }

    private void initChildrenHelper() {
        this.mChildHelper = new ChildHelper(new ChildHelper.Callback() { // from class: com.android.internal.widget.RecyclerView.5
            @Override // com.android.internal.widget.ChildHelper.Callback
            public int getChildCount() {
                return RecyclerView.this.getChildCount();
            }

            @Override // com.android.internal.widget.ChildHelper.Callback
            public void addView(View child, int index) {
                RecyclerView.this.addView(child, index);
                RecyclerView.this.dispatchChildAttached(child);
            }

            @Override // com.android.internal.widget.ChildHelper.Callback
            public int indexOfChild(View view) {
                return RecyclerView.this.indexOfChild(view);
            }

            @Override // com.android.internal.widget.ChildHelper.Callback
            public void removeViewAt(int index) {
                View child = RecyclerView.this.getChildAt(index);
                if (child != null) {
                    RecyclerView.this.dispatchChildDetached(child);
                }
                RecyclerView.this.removeViewAt(index);
            }

            @Override // com.android.internal.widget.ChildHelper.Callback
            public View getChildAt(int offset) {
                return RecyclerView.this.getChildAt(offset);
            }

            @Override // com.android.internal.widget.ChildHelper.Callback
            public void removeAllViews() {
                int count = getChildCount();
                for (int i = 0; i < count; i++) {
                    RecyclerView.this.dispatchChildDetached(getChildAt(i));
                }
                RecyclerView.this.removeAllViews();
            }

            @Override // com.android.internal.widget.ChildHelper.Callback
            public ViewHolder getChildViewHolder(View view) {
                return RecyclerView.getChildViewHolderInt(view);
            }

            @Override // com.android.internal.widget.ChildHelper.Callback
            public void attachViewToParent(View child, int index, ViewGroup.LayoutParams layoutParams) {
                ViewHolder vh = RecyclerView.getChildViewHolderInt(child);
                if (vh != null) {
                    if (!vh.isTmpDetached() && !vh.shouldIgnore()) {
                        throw new IllegalArgumentException("Called attach on a child which is not detached: " + vh);
                    }
                    vh.clearTmpDetachFlag();
                }
                RecyclerView.this.attachViewToParent(child, index, layoutParams);
            }

            @Override // com.android.internal.widget.ChildHelper.Callback
            public void detachViewFromParent(int offset) {
                ViewHolder vh;
                View view = getChildAt(offset);
                if (view != null && (vh = RecyclerView.getChildViewHolderInt(view)) != null) {
                    if (vh.isTmpDetached() && !vh.shouldIgnore()) {
                        throw new IllegalArgumentException("called detach on an already detached child " + vh);
                    }
                    vh.addFlags(256);
                }
                RecyclerView.this.detachViewFromParent(offset);
            }

            @Override // com.android.internal.widget.ChildHelper.Callback
            public void onEnteredHiddenState(View child) {
                ViewHolder vh = RecyclerView.getChildViewHolderInt(child);
                if (vh != null) {
                    vh.onEnteredHiddenState(RecyclerView.this);
                }
            }

            @Override // com.android.internal.widget.ChildHelper.Callback
            public void onLeftHiddenState(View child) {
                ViewHolder vh = RecyclerView.getChildViewHolderInt(child);
                if (vh != null) {
                    vh.onLeftHiddenState(RecyclerView.this);
                }
            }
        });
    }

    void initAdapterManager() {
        this.mAdapterHelper = new AdapterHelper(new AdapterHelper.Callback() { // from class: com.android.internal.widget.RecyclerView.6
            @Override // com.android.internal.widget.AdapterHelper.Callback
            public ViewHolder findViewHolder(int position) {
                ViewHolder vh = RecyclerView.this.findViewHolderForPosition(position, true);
                if (vh == null || RecyclerView.this.mChildHelper.isHidden(vh.itemView)) {
                    return null;
                }
                return vh;
            }

            @Override // com.android.internal.widget.AdapterHelper.Callback
            public void offsetPositionsForRemovingInvisible(int start, int count) {
                RecyclerView.this.offsetPositionRecordsForRemove(start, count, true);
                RecyclerView.this.mItemsAddedOrRemoved = true;
                RecyclerView.this.mState.mDeletedInvisibleItemCountSincePreviousLayout += count;
            }

            @Override // com.android.internal.widget.AdapterHelper.Callback
            public void offsetPositionsForRemovingLaidOutOrNewView(int positionStart, int itemCount) {
                RecyclerView.this.offsetPositionRecordsForRemove(positionStart, itemCount, false);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }

            @Override // com.android.internal.widget.AdapterHelper.Callback
            public void markViewHoldersUpdated(int positionStart, int itemCount, Object payload) {
                RecyclerView.this.viewRangeUpdate(positionStart, itemCount, payload);
                RecyclerView.this.mItemsChanged = true;
            }

            @Override // com.android.internal.widget.AdapterHelper.Callback
            public void onDispatchFirstPass(AdapterHelper.UpdateOp op) {
                dispatchUpdate(op);
            }

            void dispatchUpdate(AdapterHelper.UpdateOp op) {
                switch (op.cmd) {
                    case 1:
                        RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, op.positionStart, op.itemCount);
                        return;
                    case 2:
                        RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, op.positionStart, op.itemCount);
                        return;
                    case 4:
                        RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, op.positionStart, op.itemCount, op.payload);
                        return;
                    case 8:
                        RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, op.positionStart, op.itemCount, 1);
                        return;
                    default:
                        return;
                }
            }

            @Override // com.android.internal.widget.AdapterHelper.Callback
            public void onDispatchSecondPass(AdapterHelper.UpdateOp op) {
                dispatchUpdate(op);
            }

            @Override // com.android.internal.widget.AdapterHelper.Callback
            public void offsetPositionsForAdd(int positionStart, int itemCount) {
                RecyclerView.this.offsetPositionRecordsForInsert(positionStart, itemCount);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }

            @Override // com.android.internal.widget.AdapterHelper.Callback
            public void offsetPositionsForMove(int from, int to) {
                RecyclerView.this.offsetPositionRecordsForMove(from, to);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }
        });
    }

    public void setHasFixedSize(boolean hasFixedSize) {
        this.mHasFixedSize = hasFixedSize;
    }

    public boolean hasFixedSize() {
        return this.mHasFixedSize;
    }

    @Override // android.view.ViewGroup
    public void setClipToPadding(boolean clipToPadding) {
        if (clipToPadding != this.mClipToPadding) {
            invalidateGlows();
        }
        this.mClipToPadding = clipToPadding;
        super.setClipToPadding(clipToPadding);
        if (this.mFirstLayoutComplete) {
            requestLayout();
        }
    }

    @Override // android.view.ViewGroup
    public boolean getClipToPadding() {
        return this.mClipToPadding;
    }

    public void setScrollingTouchSlop(int slopConstant) {
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        switch (slopConstant) {
            case 0:
                break;
            case 1:
                this.mTouchSlop = vc.getScaledPagingTouchSlop();
                return;
            default:
                Log.m104w(TAG, "setScrollingTouchSlop(): bad argument constant " + slopConstant + "; using default value");
                break;
        }
        this.mTouchSlop = vc.getScaledTouchSlop();
    }

    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        setLayoutFrozen(false);
        setAdapterInternal(adapter, true, removeAndRecycleExistingViews);
        setDataSetChangedAfterLayout();
        requestLayout();
    }

    public void setAdapter(Adapter adapter) {
        setLayoutFrozen(false);
        setAdapterInternal(adapter, false, true);
        requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeAndRecycleViews() {
        ItemAnimator itemAnimator = this.mItemAnimator;
        if (itemAnimator != null) {
            itemAnimator.endAnimations();
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.removeAndRecycleAllViews(this.mRecycler);
            this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        }
        this.mRecycler.clear();
    }

    private void setAdapterInternal(Adapter adapter, boolean compatibleWithPrevious, boolean removeAndRecycleViews) {
        Adapter adapter2 = this.mAdapter;
        if (adapter2 != null) {
            adapter2.unregisterAdapterDataObserver(this.mObserver);
            this.mAdapter.onDetachedFromRecyclerView(this);
        }
        if (!compatibleWithPrevious || removeAndRecycleViews) {
            removeAndRecycleViews();
        }
        this.mAdapterHelper.reset();
        Adapter oldAdapter = this.mAdapter;
        this.mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mObserver);
            adapter.onAttachedToRecyclerView(this);
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.onAdapterChanged(oldAdapter, this.mAdapter);
        }
        this.mRecycler.onAdapterChanged(oldAdapter, this.mAdapter, compatibleWithPrevious);
        this.mState.mStructureChanged = true;
        markKnownViewsInvalid();
    }

    public Adapter getAdapter() {
        return this.mAdapter;
    }

    public void setRecyclerListener(RecyclerListener listener) {
        this.mRecyclerListener = listener;
    }

    @Override // android.view.View
    public int getBaseline() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            return layoutManager.getBaseline();
        }
        return super.getBaseline();
    }

    public void addOnChildAttachStateChangeListener(OnChildAttachStateChangeListener listener) {
        if (this.mOnChildAttachStateListeners == null) {
            this.mOnChildAttachStateListeners = new ArrayList();
        }
        this.mOnChildAttachStateListeners.add(listener);
    }

    public void removeOnChildAttachStateChangeListener(OnChildAttachStateChangeListener listener) {
        List<OnChildAttachStateChangeListener> list = this.mOnChildAttachStateListeners;
        if (list == null) {
            return;
        }
        list.remove(listener);
    }

    public void clearOnChildAttachStateChangeListeners() {
        List<OnChildAttachStateChangeListener> list = this.mOnChildAttachStateListeners;
        if (list != null) {
            list.clear();
        }
    }

    public void setLayoutManager(LayoutManager layout) {
        if (layout == this.mLayout) {
            return;
        }
        stopScroll();
        if (this.mLayout != null) {
            ItemAnimator itemAnimator = this.mItemAnimator;
            if (itemAnimator != null) {
                itemAnimator.endAnimations();
            }
            this.mLayout.removeAndRecycleAllViews(this.mRecycler);
            this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
            this.mRecycler.clear();
            if (this.mIsAttached) {
                this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
            }
            this.mLayout.setRecyclerView(null);
            this.mLayout = null;
        } else {
            this.mRecycler.clear();
        }
        this.mChildHelper.removeAllViewsUnfiltered();
        this.mLayout = layout;
        if (layout != null) {
            if (layout.mRecyclerView != null) {
                throw new IllegalArgumentException("LayoutManager " + layout + " is already attached to a RecyclerView: " + layout.mRecyclerView);
            }
            this.mLayout.setRecyclerView(this);
            if (this.mIsAttached) {
                this.mLayout.dispatchAttachedToWindow(this);
            }
        }
        this.mRecycler.updateViewCacheSize();
        requestLayout();
    }

    public void setOnFlingListener(OnFlingListener onFlingListener) {
        this.mOnFlingListener = onFlingListener;
    }

    public OnFlingListener getOnFlingListener() {
        return this.mOnFlingListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null) {
            state.copyFrom(savedState);
        } else {
            LayoutManager layoutManager = this.mLayout;
            if (layoutManager != null) {
                state.mLayoutState = layoutManager.onSaveInstanceState();
            } else {
                state.mLayoutState = null;
            }
        }
        return state;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        this.mPendingSavedState = savedState;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (this.mLayout != null && this.mPendingSavedState.mLayoutState != null) {
            this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    private void addAnimatingView(ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        boolean alreadyParented = view.getParent() == this;
        this.mRecycler.unscrapView(getChildViewHolder(view));
        if (viewHolder.isTmpDetached()) {
            this.mChildHelper.attachViewToParent(view, -1, view.getLayoutParams(), true);
        } else if (!alreadyParented) {
            this.mChildHelper.addView(view, true);
        } else {
            this.mChildHelper.hide(view);
        }
    }

    boolean removeAnimatingView(View view) {
        eatRequestLayout();
        boolean removed = this.mChildHelper.removeViewIfHidden(view);
        if (removed) {
            ViewHolder viewHolder = getChildViewHolderInt(view);
            this.mRecycler.unscrapView(viewHolder);
            this.mRecycler.recycleViewHolderInternal(viewHolder);
        }
        resumeRequestLayout(!removed);
        return removed;
    }

    public LayoutManager getLayoutManager() {
        return this.mLayout;
    }

    public RecycledViewPool getRecycledViewPool() {
        return this.mRecycler.getRecycledViewPool();
    }

    public void setRecycledViewPool(RecycledViewPool pool) {
        this.mRecycler.setRecycledViewPool(pool);
    }

    public void setViewCacheExtension(ViewCacheExtension extension) {
        this.mRecycler.setViewCacheExtension(extension);
    }

    public void setItemViewCacheSize(int size) {
        this.mRecycler.setViewCacheSize(size);
    }

    public int getScrollState() {
        return this.mScrollState;
    }

    void setScrollState(int state) {
        if (state == this.mScrollState) {
            return;
        }
        this.mScrollState = state;
        if (state != 2) {
            stopScrollersInternal();
        }
        dispatchOnScrollStateChanged(state);
    }

    public void addItemDecoration(ItemDecoration decor, int index) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
        }
        if (this.mItemDecorations.isEmpty()) {
            setWillNotDraw(false);
        }
        if (index < 0) {
            this.mItemDecorations.add(decor);
        } else {
            this.mItemDecorations.add(index, decor);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public void addItemDecoration(ItemDecoration decor) {
        addItemDecoration(decor, -1);
    }

    public void removeItemDecoration(ItemDecoration decor) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
        }
        this.mItemDecorations.remove(decor);
        if (this.mItemDecorations.isEmpty()) {
            setWillNotDraw(getOverScrollMode() == 2);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public void setChildDrawingOrderCallback(ChildDrawingOrderCallback childDrawingOrderCallback) {
        if (childDrawingOrderCallback == this.mChildDrawingOrderCallback) {
            return;
        }
        this.mChildDrawingOrderCallback = childDrawingOrderCallback;
        setChildrenDrawingOrderEnabled(childDrawingOrderCallback != null);
    }

    @Deprecated
    public void setOnScrollListener(OnScrollListener listener) {
        this.mScrollListener = listener;
    }

    public void addOnScrollListener(OnScrollListener listener) {
        if (this.mScrollListeners == null) {
            this.mScrollListeners = new ArrayList();
        }
        this.mScrollListeners.add(listener);
    }

    public void removeOnScrollListener(OnScrollListener listener) {
        List<OnScrollListener> list = this.mScrollListeners;
        if (list != null) {
            list.remove(listener);
        }
    }

    public void clearOnScrollListeners() {
        List<OnScrollListener> list = this.mScrollListeners;
        if (list != null) {
            list.clear();
        }
    }

    public void scrollToPosition(int position) {
        if (this.mLayoutFrozen) {
            return;
        }
        stopScroll();
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            Log.m110e(TAG, "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return;
        }
        layoutManager.scrollToPosition(position);
        awakenScrollBars();
    }

    void jumpToPositionForSmoothScroller(int position) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            return;
        }
        layoutManager.scrollToPosition(position);
        awakenScrollBars();
    }

    public void smoothScrollToPosition(int position) {
        if (this.mLayoutFrozen) {
            return;
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            Log.m110e(TAG, "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else {
            layoutManager.smoothScrollToPosition(this, this.mState, position);
        }
    }

    @Override // android.view.View
    public void scrollTo(int x, int y) {
        Log.m104w(TAG, "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
    }

    @Override // android.view.View
    public void scrollBy(int x, int y) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            Log.m110e(TAG, "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else if (this.mLayoutFrozen) {
        } else {
            boolean canScrollHorizontal = layoutManager.canScrollHorizontally();
            boolean canScrollVertical = this.mLayout.canScrollVertically();
            if (canScrollHorizontal || canScrollVertical) {
                scrollByInternal(canScrollHorizontal ? x : 0, canScrollVertical ? y : 0, null);
            }
        }
    }

    void consumePendingUpdateOperations() {
        if (!this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout) {
            Trace.beginSection(TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG);
            dispatchLayout();
            Trace.endSection();
        } else if (!this.mAdapterHelper.hasPendingUpdates()) {
        } else {
            if (this.mAdapterHelper.hasAnyUpdateTypes(4) && !this.mAdapterHelper.hasAnyUpdateTypes(11)) {
                Trace.beginSection(TRACE_HANDLE_ADAPTER_UPDATES_TAG);
                eatRequestLayout();
                onEnterLayoutOrScroll();
                this.mAdapterHelper.preProcess();
                if (!this.mLayoutRequestEaten) {
                    if (hasUpdatedView()) {
                        dispatchLayout();
                    } else {
                        this.mAdapterHelper.consumePostponedUpdates();
                    }
                }
                resumeRequestLayout(true);
                onExitLayoutOrScroll();
                Trace.endSection();
            } else if (this.mAdapterHelper.hasPendingUpdates()) {
                Trace.beginSection(TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG);
                dispatchLayout();
                Trace.endSection();
            }
        }
    }

    private boolean hasUpdatedView() {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (holder != null && !holder.shouldIgnore() && holder.isUpdated()) {
                return true;
            }
        }
        return false;
    }

    boolean scrollByInternal(int x, int y, MotionEvent ev) {
        int unconsumedX = 0;
        int unconsumedY = 0;
        int consumedX = 0;
        int consumedY = 0;
        consumePendingUpdateOperations();
        if (this.mAdapter != null) {
            eatRequestLayout();
            onEnterLayoutOrScroll();
            Trace.beginSection(TRACE_SCROLL_TAG);
            if (x != 0) {
                consumedX = this.mLayout.scrollHorizontallyBy(x, this.mRecycler, this.mState);
                unconsumedX = x - consumedX;
            }
            if (y != 0) {
                consumedY = this.mLayout.scrollVerticallyBy(y, this.mRecycler, this.mState);
                unconsumedY = y - consumedY;
            }
            Trace.endSection();
            repositionShadowingViews();
            onExitLayoutOrScroll();
            resumeRequestLayout(false);
        }
        if (!this.mItemDecorations.isEmpty()) {
            invalidate();
        }
        if (dispatchNestedScroll(consumedX, consumedY, unconsumedX, unconsumedY, this.mScrollOffset)) {
            int i = this.mLastTouchX;
            int[] iArr = this.mScrollOffset;
            int i2 = iArr[0];
            this.mLastTouchX = i - i2;
            int i3 = this.mLastTouchY;
            int i4 = iArr[1];
            this.mLastTouchY = i3 - i4;
            if (ev != null) {
                ev.offsetLocation(i2, i4);
            }
            int[] iArr2 = this.mNestedOffsets;
            int i5 = iArr2[0];
            int[] iArr3 = this.mScrollOffset;
            iArr2[0] = i5 + iArr3[0];
            iArr2[1] = iArr2[1] + iArr3[1];
        } else if (getOverScrollMode() != 2) {
            if (ev != null) {
                pullGlows(ev.getX(), unconsumedX, ev.getY(), unconsumedY);
            }
            considerReleasingGlowsOnScroll(x, y);
        }
        if (consumedX != 0 || consumedY != 0) {
            dispatchOnScrolled(consumedX, consumedY);
        }
        if (!awakenScrollBars()) {
            invalidate();
        }
        return (consumedX == 0 && consumedY == 0) ? false : true;
    }

    @Override // android.view.View
    public int computeHorizontalScrollOffset() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollHorizontally()) {
            return this.mLayout.computeHorizontalScrollOffset(this.mState);
        }
        return 0;
    }

    @Override // android.view.View
    public int computeHorizontalScrollExtent() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollHorizontally()) {
            return this.mLayout.computeHorizontalScrollExtent(this.mState);
        }
        return 0;
    }

    @Override // android.view.View
    public int computeHorizontalScrollRange() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollHorizontally()) {
            return this.mLayout.computeHorizontalScrollRange(this.mState);
        }
        return 0;
    }

    @Override // android.view.View
    public int computeVerticalScrollOffset() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollVertically()) {
            return this.mLayout.computeVerticalScrollOffset(this.mState);
        }
        return 0;
    }

    @Override // android.view.View
    public int computeVerticalScrollExtent() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollVertically()) {
            return this.mLayout.computeVerticalScrollExtent(this.mState);
        }
        return 0;
    }

    @Override // android.view.View
    public int computeVerticalScrollRange() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null && layoutManager.canScrollVertically()) {
            return this.mLayout.computeVerticalScrollRange(this.mState);
        }
        return 0;
    }

    void eatRequestLayout() {
        int i = this.mEatRequestLayout + 1;
        this.mEatRequestLayout = i;
        if (i == 1 && !this.mLayoutFrozen) {
            this.mLayoutRequestEaten = false;
        }
    }

    void resumeRequestLayout(boolean performLayoutChildren) {
        if (this.mEatRequestLayout < 1) {
            this.mEatRequestLayout = 1;
        }
        if (!performLayoutChildren) {
            this.mLayoutRequestEaten = false;
        }
        if (this.mEatRequestLayout == 1) {
            if (performLayoutChildren && this.mLayoutRequestEaten && !this.mLayoutFrozen && this.mLayout != null && this.mAdapter != null) {
                dispatchLayout();
            }
            if (!this.mLayoutFrozen) {
                this.mLayoutRequestEaten = false;
            }
        }
        this.mEatRequestLayout--;
    }

    public void setLayoutFrozen(boolean frozen) {
        if (frozen != this.mLayoutFrozen) {
            assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
            if (!frozen) {
                this.mLayoutFrozen = false;
                if (this.mLayoutRequestEaten && this.mLayout != null && this.mAdapter != null) {
                    requestLayout();
                }
                this.mLayoutRequestEaten = false;
                return;
            }
            long now = SystemClock.uptimeMillis();
            MotionEvent cancelEvent = MotionEvent.obtain(now, now, 3, 0.0f, 0.0f, 0);
            onTouchEvent(cancelEvent);
            this.mLayoutFrozen = true;
            this.mIgnoreMotionEventTillDown = true;
            stopScroll();
        }
    }

    public boolean isLayoutFrozen() {
        return this.mLayoutFrozen;
    }

    public void smoothScrollBy(int dx, int dy) {
        smoothScrollBy(dx, dy, null);
    }

    public void smoothScrollBy(int dx, int dy, Interpolator interpolator) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            Log.m110e(TAG, "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else if (this.mLayoutFrozen) {
        } else {
            if (!layoutManager.canScrollHorizontally()) {
                dx = 0;
            }
            if (!this.mLayout.canScrollVertically()) {
                dy = 0;
            }
            if (dx != 0 || dy != 0) {
                this.mViewFlinger.smoothScrollBy(dx, dy, interpolator);
            }
        }
    }

    public boolean fling(int velocityX, int velocityY) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            Log.m110e(TAG, "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return false;
        } else if (this.mLayoutFrozen) {
            return false;
        } else {
            boolean canScrollHorizontal = layoutManager.canScrollHorizontally();
            boolean canScrollVertical = this.mLayout.canScrollVertically();
            velocityX = (!canScrollHorizontal || Math.abs(velocityX) < this.mMinFlingVelocity) ? 0 : 0;
            velocityY = (!canScrollVertical || Math.abs(velocityY) < this.mMinFlingVelocity) ? 0 : 0;
            if ((velocityX != 0 || velocityY != 0) && !dispatchNestedPreFling(velocityX, velocityY)) {
                boolean consumed = false;
                if (this.mLayout.getChildCount() > 0) {
                    View firstChild = this.mLayout.getChildAt(0);
                    LayoutManager layoutManager2 = this.mLayout;
                    View lastChild = layoutManager2.getChildAt(layoutManager2.getChildCount() - 1);
                    if (velocityY < 0) {
                        consumed = getChildAdapterPosition(firstChild) > 0 || firstChild.getTop() < getPaddingTop();
                    }
                    if (velocityY > 0) {
                        consumed = getChildAdapterPosition(lastChild) < this.mAdapter.getItemCount() - 1 || lastChild.getBottom() > getHeight() - getPaddingBottom();
                    }
                }
                dispatchNestedFling(velocityX, velocityY, consumed);
                OnFlingListener onFlingListener = this.mOnFlingListener;
                if (onFlingListener != null && onFlingListener.onFling(velocityX, velocityY)) {
                    return true;
                }
                boolean canScroll = canScrollHorizontal || canScrollVertical;
                if (canScroll) {
                    int i = this.mMaxFlingVelocity;
                    int velocityX2 = Math.max(-i, Math.min(velocityX, i));
                    int i2 = this.mMaxFlingVelocity;
                    this.mViewFlinger.fling(velocityX2, Math.max(-i2, Math.min(velocityY, i2)));
                    return true;
                }
            }
            return false;
        }
    }

    public void stopScroll() {
        setScrollState(0);
        stopScrollersInternal();
    }

    private void stopScrollersInternal() {
        this.mViewFlinger.stop();
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.stopSmoothScroller();
        }
    }

    public int getMinFlingVelocity() {
        return this.mMinFlingVelocity;
    }

    public int getMaxFlingVelocity() {
        return this.mMaxFlingVelocity;
    }

    private void pullGlows(float x, float overscrollX, float y, float overscrollY) {
        boolean invalidate = false;
        if (overscrollX < 0.0f) {
            ensureLeftGlow();
            this.mLeftGlow.onPull((-overscrollX) / getWidth(), 1.0f - (y / getHeight()));
            invalidate = true;
        } else if (overscrollX > 0.0f) {
            ensureRightGlow();
            this.mRightGlow.onPull(overscrollX / getWidth(), y / getHeight());
            invalidate = true;
        }
        if (overscrollY < 0.0f) {
            ensureTopGlow();
            this.mTopGlow.onPull((-overscrollY) / getHeight(), x / getWidth());
            invalidate = true;
        } else if (overscrollY > 0.0f) {
            ensureBottomGlow();
            this.mBottomGlow.onPull(overscrollY / getHeight(), 1.0f - (x / getWidth()));
            invalidate = true;
        }
        if (invalidate || overscrollX != 0.0f || overscrollY != 0.0f) {
            postInvalidateOnAnimation();
        }
    }

    private void releaseGlows() {
        boolean needsInvalidate = false;
        EdgeEffect edgeEffect = this.mLeftGlow;
        if (edgeEffect != null) {
            edgeEffect.onRelease();
            needsInvalidate = true;
        }
        EdgeEffect edgeEffect2 = this.mTopGlow;
        if (edgeEffect2 != null) {
            edgeEffect2.onRelease();
            needsInvalidate = true;
        }
        EdgeEffect edgeEffect3 = this.mRightGlow;
        if (edgeEffect3 != null) {
            edgeEffect3.onRelease();
            needsInvalidate = true;
        }
        EdgeEffect edgeEffect4 = this.mBottomGlow;
        if (edgeEffect4 != null) {
            edgeEffect4.onRelease();
            needsInvalidate = true;
        }
        if (needsInvalidate) {
            postInvalidateOnAnimation();
        }
    }

    void considerReleasingGlowsOnScroll(int dx, int dy) {
        boolean needsInvalidate = false;
        EdgeEffect edgeEffect = this.mLeftGlow;
        if (edgeEffect != null && !edgeEffect.isFinished() && dx > 0) {
            this.mLeftGlow.onRelease();
            needsInvalidate = true;
        }
        EdgeEffect edgeEffect2 = this.mRightGlow;
        if (edgeEffect2 != null && !edgeEffect2.isFinished() && dx < 0) {
            this.mRightGlow.onRelease();
            needsInvalidate = true;
        }
        EdgeEffect edgeEffect3 = this.mTopGlow;
        if (edgeEffect3 != null && !edgeEffect3.isFinished() && dy > 0) {
            this.mTopGlow.onRelease();
            needsInvalidate = true;
        }
        EdgeEffect edgeEffect4 = this.mBottomGlow;
        if (edgeEffect4 != null && !edgeEffect4.isFinished() && dy < 0) {
            this.mBottomGlow.onRelease();
            needsInvalidate = true;
        }
        if (needsInvalidate) {
            postInvalidateOnAnimation();
        }
    }

    void absorbGlows(int velocityX, int velocityY) {
        if (velocityX < 0) {
            ensureLeftGlow();
            this.mLeftGlow.onAbsorb(-velocityX);
        } else if (velocityX > 0) {
            ensureRightGlow();
            this.mRightGlow.onAbsorb(velocityX);
        }
        if (velocityY < 0) {
            ensureTopGlow();
            this.mTopGlow.onAbsorb(-velocityY);
        } else if (velocityY > 0) {
            ensureBottomGlow();
            this.mBottomGlow.onAbsorb(velocityY);
        }
        if (velocityX != 0 || velocityY != 0) {
            postInvalidateOnAnimation();
        }
    }

    void ensureLeftGlow() {
        if (this.mLeftGlow != null) {
            return;
        }
        EdgeEffect edgeEffect = new EdgeEffect(getContext());
        this.mLeftGlow = edgeEffect;
        if (this.mClipToPadding) {
            edgeEffect.setSize((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        } else {
            edgeEffect.setSize(getMeasuredHeight(), getMeasuredWidth());
        }
    }

    void ensureRightGlow() {
        if (this.mRightGlow != null) {
            return;
        }
        EdgeEffect edgeEffect = new EdgeEffect(getContext());
        this.mRightGlow = edgeEffect;
        if (this.mClipToPadding) {
            edgeEffect.setSize((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        } else {
            edgeEffect.setSize(getMeasuredHeight(), getMeasuredWidth());
        }
    }

    void ensureTopGlow() {
        if (this.mTopGlow != null) {
            return;
        }
        EdgeEffect edgeEffect = new EdgeEffect(getContext());
        this.mTopGlow = edgeEffect;
        if (this.mClipToPadding) {
            edgeEffect.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
        } else {
            edgeEffect.setSize(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    void ensureBottomGlow() {
        if (this.mBottomGlow != null) {
            return;
        }
        EdgeEffect edgeEffect = new EdgeEffect(getContext());
        this.mBottomGlow = edgeEffect;
        if (this.mClipToPadding) {
            edgeEffect.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
        } else {
            edgeEffect.setSize(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    void invalidateGlows() {
        this.mBottomGlow = null;
        this.mTopGlow = null;
        this.mRightGlow = null;
        this.mLeftGlow = null;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public View focusSearch(View focused, int direction) {
        View result;
        View result2 = this.mLayout.onInterceptFocusSearch(focused, direction);
        if (result2 != null) {
            return result2;
        }
        boolean canRunFocusFailure = (this.mAdapter == null || this.mLayout == null || isComputingLayout() || this.mLayoutFrozen) ? false : true;
        FocusFinder ff = FocusFinder.getInstance();
        if (canRunFocusFailure && (direction == 2 || direction == 1)) {
            boolean needsFocusFailureLayout = false;
            if (this.mLayout.canScrollVertically()) {
                int absDir = direction == 2 ? 130 : 33;
                View found = ff.findNextFocus(this, focused, absDir);
                needsFocusFailureLayout = found == null;
                if (FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
                    direction = absDir;
                }
            }
            if (!needsFocusFailureLayout && this.mLayout.canScrollHorizontally()) {
                boolean rtl = this.mLayout.getLayoutDirection() == 1;
                int absDir2 = (direction == 2) ^ rtl ? 66 : 17;
                View found2 = ff.findNextFocus(this, focused, absDir2);
                needsFocusFailureLayout = found2 == null;
                if (FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
                    direction = absDir2;
                }
            }
            if (needsFocusFailureLayout) {
                consumePendingUpdateOperations();
                View focusedItemView = findContainingItemView(focused);
                if (focusedItemView == null) {
                    return null;
                }
                eatRequestLayout();
                this.mLayout.onFocusSearchFailed(focused, direction, this.mRecycler, this.mState);
                resumeRequestLayout(false);
            }
            result = ff.findNextFocus(this, focused, direction);
        } else {
            result = ff.findNextFocus(this, focused, direction);
            if (result == null && canRunFocusFailure) {
                consumePendingUpdateOperations();
                View focusedItemView2 = findContainingItemView(focused);
                if (focusedItemView2 == null) {
                    return null;
                }
                eatRequestLayout();
                result = this.mLayout.onFocusSearchFailed(focused, direction, this.mRecycler, this.mState);
                resumeRequestLayout(false);
            }
        }
        return isPreferredNextFocus(focused, result, direction) ? result : super.focusSearch(focused, direction);
    }

    private boolean isPreferredNextFocus(View focused, View next, int direction) {
        boolean rtl;
        boolean z = false;
        if (next == null || next == this) {
            return false;
        }
        if (focused == null) {
            return true;
        }
        if (direction == 2 || direction == 1) {
            if (this.mLayout.getLayoutDirection() != 1) {
                rtl = false;
            } else {
                rtl = true;
            }
            if (direction == 2) {
                z = true;
            }
            int absHorizontal = z ^ rtl ? 66 : 17;
            if (isPreferredNextFocusAbsolute(focused, next, absHorizontal)) {
                return true;
            }
            if (direction == 2) {
                return isPreferredNextFocusAbsolute(focused, next, 130);
            }
            return isPreferredNextFocusAbsolute(focused, next, 33);
        }
        return isPreferredNextFocusAbsolute(focused, next, direction);
    }

    private boolean isPreferredNextFocusAbsolute(View focused, View next, int direction) {
        this.mTempRect.set(0, 0, focused.getWidth(), focused.getHeight());
        this.mTempRect2.set(0, 0, next.getWidth(), next.getHeight());
        offsetDescendantRectToMyCoords(focused, this.mTempRect);
        offsetDescendantRectToMyCoords(next, this.mTempRect2);
        switch (direction) {
            case 17:
                return (this.mTempRect.right > this.mTempRect2.right || this.mTempRect.left >= this.mTempRect2.right) && this.mTempRect.left > this.mTempRect2.left;
            case 33:
                return (this.mTempRect.bottom > this.mTempRect2.bottom || this.mTempRect.top >= this.mTempRect2.bottom) && this.mTempRect.top > this.mTempRect2.top;
            case 66:
                return (this.mTempRect.left < this.mTempRect2.left || this.mTempRect.right <= this.mTempRect2.left) && this.mTempRect.right < this.mTempRect2.right;
            case 130:
                return (this.mTempRect.top < this.mTempRect2.top || this.mTempRect.bottom <= this.mTempRect2.top) && this.mTempRect.bottom < this.mTempRect2.bottom;
            default:
                throw new IllegalArgumentException("direction must be absolute. received:" + direction);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        if (!this.mLayout.onRequestChildFocus(this, this.mState, child, focused) && focused != null) {
            this.mTempRect.set(0, 0, focused.getWidth(), focused.getHeight());
            ViewGroup.LayoutParams focusedLayoutParams = focused.getLayoutParams();
            if (focusedLayoutParams instanceof LayoutParams) {
                LayoutParams lp = (LayoutParams) focusedLayoutParams;
                if (!lp.mInsetsDirty) {
                    Rect insets = lp.mDecorInsets;
                    this.mTempRect.left -= insets.left;
                    this.mTempRect.right += insets.right;
                    this.mTempRect.top -= insets.top;
                    this.mTempRect.bottom += insets.bottom;
                }
            }
            offsetDescendantRectToMyCoords(focused, this.mTempRect);
            offsetRectIntoDescendantCoords(child, this.mTempRect);
            requestChildRectangleOnScreen(child, this.mTempRect, !this.mFirstLayoutComplete);
        }
        super.requestChildFocus(child, focused);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        return this.mLayout.requestChildRectangleOnScreen(this, child, rect, immediate);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null || !layoutManager.onAddFocusables(this, views, direction, focusableMode)) {
            super.addFocusables(views, direction, focusableMode);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if (isComputingLayout()) {
            return false;
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLayoutOrScrollCounter = 0;
        boolean z = true;
        this.mIsAttached = true;
        if (!this.mFirstLayoutComplete || isLayoutRequested()) {
            z = false;
        }
        this.mFirstLayoutComplete = z;
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.dispatchAttachedToWindow(this);
        }
        this.mPostedAnimatorRunner = false;
        if (ALLOW_THREAD_GAP_WORK) {
            GapWorker gapWorker = GapWorker.sGapWorker.get();
            this.mGapWorker = gapWorker;
            if (gapWorker == null) {
                this.mGapWorker = new GapWorker();
                Display display = getDisplay();
                float refreshRate = 60.0f;
                if (!isInEditMode() && display != null) {
                    float displayRefreshRate = display.getRefreshRate();
                    if (displayRefreshRate >= 30.0f) {
                        refreshRate = displayRefreshRate;
                    }
                }
                this.mGapWorker.mFrameIntervalNs = 1.0E9f / refreshRate;
                GapWorker.sGapWorker.set(this.mGapWorker);
            }
            this.mGapWorker.add(this);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ItemAnimator itemAnimator = this.mItemAnimator;
        if (itemAnimator != null) {
            itemAnimator.endAnimations();
        }
        stopScroll();
        this.mIsAttached = false;
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.dispatchDetachedFromWindow(this, this.mRecycler);
        }
        this.mPendingAccessibilityImportanceChange.clear();
        removeCallbacks(this.mItemAnimatorRunner);
        this.mViewInfoStore.onDetach();
        if (ALLOW_THREAD_GAP_WORK) {
            this.mGapWorker.remove(this);
            this.mGapWorker = null;
        }
    }

    @Override // android.view.View
    public boolean isAttachedToWindow() {
        return this.mIsAttached;
    }

    void assertInLayoutOrScroll(String message) {
        if (!isComputingLayout()) {
            if (message == null) {
                throw new IllegalStateException("Cannot call this method unless RecyclerView is computing a layout or scrolling");
            }
            throw new IllegalStateException(message);
        }
    }

    void assertNotInLayoutOrScroll(String message) {
        if (isComputingLayout()) {
            if (message == null) {
                throw new IllegalStateException("Cannot call this method while RecyclerView is computing a layout or scrolling");
            }
            throw new IllegalStateException(message);
        } else if (this.mDispatchScrollCounter > 0) {
            Log.m103w(TAG, "Cannot call this method in a scroll callback. Scroll callbacks might be run during a measure & layout pass where you cannot change the RecyclerView data. Any method call that might change the structure of the RecyclerView or the adapter contents should be postponed to the next frame.", new IllegalStateException(""));
        }
    }

    public void addOnItemTouchListener(OnItemTouchListener listener) {
        this.mOnItemTouchListeners.add(listener);
    }

    public void removeOnItemTouchListener(OnItemTouchListener listener) {
        this.mOnItemTouchListeners.remove(listener);
        if (this.mActiveOnItemTouchListener == listener) {
            this.mActiveOnItemTouchListener = null;
        }
    }

    private boolean dispatchOnItemTouchIntercept(MotionEvent e) {
        int action = e.getAction();
        if (action == 3 || action == 0) {
            this.mActiveOnItemTouchListener = null;
        }
        int listenerCount = this.mOnItemTouchListeners.size();
        for (int i = 0; i < listenerCount; i++) {
            OnItemTouchListener listener = this.mOnItemTouchListeners.get(i);
            if (listener.onInterceptTouchEvent(this, e) && action != 3) {
                this.mActiveOnItemTouchListener = listener;
                return true;
            }
        }
        return false;
    }

    private boolean dispatchOnItemTouch(MotionEvent e) {
        int action = e.getAction();
        OnItemTouchListener onItemTouchListener = this.mActiveOnItemTouchListener;
        if (onItemTouchListener != null) {
            if (action == 0) {
                this.mActiveOnItemTouchListener = null;
            } else {
                onItemTouchListener.onTouchEvent(this, e);
                if (action == 3 || action == 1) {
                    this.mActiveOnItemTouchListener = null;
                }
                return true;
            }
        }
        if (action != 0) {
            int listenerCount = this.mOnItemTouchListeners.size();
            for (int i = 0; i < listenerCount; i++) {
                OnItemTouchListener listener = this.mOnItemTouchListeners.get(i);
                if (listener.onInterceptTouchEvent(this, e)) {
                    this.mActiveOnItemTouchListener = listener;
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (this.mLayoutFrozen) {
            return false;
        }
        if (dispatchOnItemTouchIntercept(e)) {
            cancelTouch();
            return true;
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            return false;
        }
        boolean canScrollHorizontally = layoutManager.canScrollHorizontally();
        boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(e);
        int action = e.getActionMasked();
        int actionIndex = e.getActionIndex();
        switch (action) {
            case 0:
                if (this.mIgnoreMotionEventTillDown) {
                    this.mIgnoreMotionEventTillDown = false;
                }
                this.mScrollPointerId = e.getPointerId(0);
                int x = (int) (e.getX() + 0.5f);
                this.mLastTouchX = x;
                this.mInitialTouchX = x;
                int y = (int) (e.getY() + 0.5f);
                this.mLastTouchY = y;
                this.mInitialTouchY = y;
                if (stopGlowAnimations(e) || this.mScrollState == 2) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setScrollState(1);
                }
                int[] iArr = this.mNestedOffsets;
                iArr[1] = 0;
                iArr[0] = 0;
                int nestedScrollAxis = 0;
                if (canScrollHorizontally) {
                    nestedScrollAxis = 0 | 1;
                }
                if (canScrollVertically) {
                    nestedScrollAxis |= 2;
                }
                startNestedScroll(nestedScrollAxis);
                break;
            case 1:
                this.mVelocityTracker.clear();
                stopNestedScroll();
                break;
            case 2:
                int index = e.findPointerIndex(this.mScrollPointerId);
                if (index < 0) {
                    Log.m110e(TAG, "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }
                int x2 = (int) (e.getX(index) + 0.5f);
                int y2 = (int) (e.getY(index) + 0.5f);
                if (this.mScrollState != 1) {
                    int dx = x2 - this.mInitialTouchX;
                    int dy = y2 - this.mInitialTouchY;
                    boolean startScroll = false;
                    if (canScrollHorizontally) {
                        int abs = Math.abs(dx);
                        int i = this.mTouchSlop;
                        if (abs > i) {
                            this.mLastTouchX = this.mInitialTouchX + (i * (dx < 0 ? -1 : 1));
                            startScroll = true;
                        }
                    }
                    if (canScrollVertically) {
                        int abs2 = Math.abs(dy);
                        int i2 = this.mTouchSlop;
                        if (abs2 > i2) {
                            this.mLastTouchY = this.mInitialTouchY + (i2 * (dy < 0 ? -1 : 1));
                            startScroll = true;
                        }
                    }
                    if (startScroll) {
                        setScrollState(1);
                        break;
                    }
                }
                break;
            case 3:
                cancelTouch();
                break;
            case 5:
                this.mScrollPointerId = e.getPointerId(actionIndex);
                int x3 = (int) (e.getX(actionIndex) + 0.5f);
                this.mLastTouchX = x3;
                this.mInitialTouchX = x3;
                int y3 = (int) (e.getY(actionIndex) + 0.5f);
                this.mLastTouchY = y3;
                this.mInitialTouchY = y3;
                break;
            case 6:
                onPointerUp(e);
                break;
        }
        int nestedScrollAxis2 = this.mScrollState;
        return nestedScrollAxis2 == 1;
    }

    private boolean stopGlowAnimations(MotionEvent e) {
        boolean stopped = false;
        EdgeEffect edgeEffect = this.mLeftGlow;
        if (edgeEffect != null && edgeEffect.getDistance() != 0.0f) {
            this.mLeftGlow.onPullDistance(0.0f, 1.0f - (e.getY() / getHeight()));
            stopped = true;
        }
        EdgeEffect edgeEffect2 = this.mRightGlow;
        if (edgeEffect2 != null && edgeEffect2.getDistance() != 0.0f) {
            this.mRightGlow.onPullDistance(0.0f, e.getY() / getHeight());
            stopped = true;
        }
        EdgeEffect edgeEffect3 = this.mTopGlow;
        if (edgeEffect3 != null && edgeEffect3.getDistance() != 0.0f) {
            this.mTopGlow.onPullDistance(0.0f, e.getX() / getWidth());
            stopped = true;
        }
        EdgeEffect edgeEffect4 = this.mBottomGlow;
        if (edgeEffect4 != null && edgeEffect4.getDistance() != 0.0f) {
            this.mBottomGlow.onPullDistance(0.0f, 1.0f - (e.getX() / getWidth()));
            return true;
        }
        return stopped;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        int listenerCount = this.mOnItemTouchListeners.size();
        for (int i = 0; i < listenerCount; i++) {
            OnItemTouchListener listener = this.mOnItemTouchListeners.get(i);
            listener.onRequestDisallowInterceptTouchEvent(disallowIntercept);
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent e) {
        boolean eventAddedToVelocityTracker;
        boolean eventAddedToVelocityTracker2;
        float xvel;
        float yvel;
        int i;
        if (this.mLayoutFrozen || this.mIgnoreMotionEventTillDown) {
            return false;
        }
        if (dispatchOnItemTouch(e)) {
            cancelTouch();
            return true;
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            return false;
        }
        boolean canScrollHorizontally = layoutManager.canScrollHorizontally();
        boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        MotionEvent vtev = MotionEvent.obtain(e);
        int action = e.getActionMasked();
        int actionIndex = e.getActionIndex();
        if (action == 0) {
            int[] iArr = this.mNestedOffsets;
            iArr[1] = 0;
            iArr[0] = 0;
        }
        int[] iArr2 = this.mNestedOffsets;
        vtev.offsetLocation(iArr2[0], iArr2[1]);
        switch (action) {
            case 0:
                eventAddedToVelocityTracker = false;
                this.mScrollPointerId = e.getPointerId(0);
                int x = (int) (e.getX() + 0.5f);
                this.mLastTouchX = x;
                this.mInitialTouchX = x;
                int y = (int) (e.getY() + 0.5f);
                this.mLastTouchY = y;
                this.mInitialTouchY = y;
                int nestedScrollAxis = 0;
                if (canScrollHorizontally) {
                    nestedScrollAxis = 0 | 1;
                }
                if (canScrollVertically) {
                    nestedScrollAxis |= 2;
                }
                startNestedScroll(nestedScrollAxis);
                eventAddedToVelocityTracker2 = eventAddedToVelocityTracker;
                break;
            case 1:
                this.mVelocityTracker.addMovement(vtev);
                eventAddedToVelocityTracker2 = true;
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
                if (canScrollHorizontally) {
                    xvel = -this.mVelocityTracker.getXVelocity(this.mScrollPointerId);
                } else {
                    xvel = 0.0f;
                }
                if (canScrollVertically) {
                    yvel = -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);
                } else {
                    yvel = 0.0f;
                }
                if ((xvel == 0.0f && yvel == 0.0f) || !fling((int) xvel, (int) yvel)) {
                    setScrollState(0);
                }
                resetTouch();
                break;
            case 2:
                int index = e.findPointerIndex(this.mScrollPointerId);
                if (index < 0) {
                    Log.m110e(TAG, "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    vtev.recycle();
                    return false;
                }
                int x2 = (int) (e.getX(index) + 0.5f);
                int y2 = (int) (e.getY(index) + 0.5f);
                int dx = this.mLastTouchX - x2;
                int dy = this.mLastTouchY - y2;
                int dx2 = dx - releaseHorizontalGlow(dx, e.getY());
                int dy2 = dy - releaseVerticalGlow(dy, e.getX());
                if (!dispatchNestedPreScroll(dx2, dy2, this.mScrollConsumed, this.mScrollOffset)) {
                    eventAddedToVelocityTracker = false;
                    i = 1;
                } else {
                    int[] iArr3 = this.mScrollConsumed;
                    dx2 -= iArr3[0];
                    dy2 -= iArr3[1];
                    int[] iArr4 = this.mScrollOffset;
                    eventAddedToVelocityTracker = false;
                    vtev.offsetLocation(iArr4[0], iArr4[1]);
                    int[] iArr5 = this.mNestedOffsets;
                    int i2 = iArr5[0];
                    int[] iArr6 = this.mScrollOffset;
                    iArr5[0] = i2 + iArr6[0];
                    i = 1;
                    iArr5[1] = iArr5[1] + iArr6[1];
                }
                if (this.mScrollState != i) {
                    boolean startScroll = false;
                    if (canScrollHorizontally) {
                        int abs = Math.abs(dx2);
                        int i3 = this.mTouchSlop;
                        if (abs > i3) {
                            if (dx2 > 0) {
                                dx2 -= i3;
                            } else {
                                dx2 += i3;
                            }
                            startScroll = true;
                        }
                    }
                    if (canScrollVertically) {
                        int abs2 = Math.abs(dy2);
                        int i4 = this.mTouchSlop;
                        if (abs2 > i4) {
                            if (dy2 > 0) {
                                dy2 -= i4;
                            } else {
                                dy2 += i4;
                            }
                            startScroll = true;
                        }
                    }
                    if (!startScroll) {
                        i = 1;
                    } else {
                        i = 1;
                        setScrollState(1);
                    }
                }
                if (this.mScrollState == i) {
                    int[] iArr7 = this.mScrollOffset;
                    this.mLastTouchX = x2 - iArr7[0];
                    this.mLastTouchY = y2 - iArr7[i];
                    if (scrollByInternal(canScrollHorizontally ? dx2 : 0, canScrollVertically ? dy2 : 0, vtev)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    GapWorker gapWorker = this.mGapWorker;
                    if (gapWorker != null && (dx2 != 0 || dy2 != 0)) {
                        gapWorker.postFromTraversal(this, dx2, dy2);
                    }
                }
                eventAddedToVelocityTracker2 = eventAddedToVelocityTracker;
                break;
            case 3:
                cancelTouch();
                eventAddedToVelocityTracker = false;
                eventAddedToVelocityTracker2 = eventAddedToVelocityTracker;
                break;
            case 4:
            default:
                eventAddedToVelocityTracker = false;
                eventAddedToVelocityTracker2 = eventAddedToVelocityTracker;
                break;
            case 5:
                this.mScrollPointerId = e.getPointerId(actionIndex);
                int x3 = (int) (e.getX(actionIndex) + 0.5f);
                this.mLastTouchX = x3;
                this.mInitialTouchX = x3;
                int y3 = (int) (e.getY(actionIndex) + 0.5f);
                this.mLastTouchY = y3;
                this.mInitialTouchY = y3;
                eventAddedToVelocityTracker = false;
                eventAddedToVelocityTracker2 = eventAddedToVelocityTracker;
                break;
            case 6:
                onPointerUp(e);
                eventAddedToVelocityTracker = false;
                eventAddedToVelocityTracker2 = eventAddedToVelocityTracker;
                break;
        }
        if (!eventAddedToVelocityTracker2) {
            this.mVelocityTracker.addMovement(vtev);
        }
        vtev.recycle();
        return true;
    }

    private int releaseHorizontalGlow(int deltaX, float y) {
        float consumed = 0.0f;
        float displacement = y / getHeight();
        float pullDistance = deltaX / getWidth();
        EdgeEffect edgeEffect = this.mLeftGlow;
        if (edgeEffect != null && edgeEffect.getDistance() != 0.0f) {
            consumed = -this.mLeftGlow.onPullDistance(-pullDistance, 1.0f - displacement);
            if (this.mLeftGlow.getDistance() == 0.0f) {
                this.mLeftGlow.onRelease();
            }
        } else {
            EdgeEffect edgeEffect2 = this.mRightGlow;
            if (edgeEffect2 != null && edgeEffect2.getDistance() != 0.0f) {
                consumed = this.mRightGlow.onPullDistance(pullDistance, displacement);
                if (this.mRightGlow.getDistance() == 0.0f) {
                    this.mRightGlow.onRelease();
                }
            }
        }
        int pixelsConsumed = Math.round(getWidth() * consumed);
        if (pixelsConsumed != 0) {
            invalidate();
        }
        return pixelsConsumed;
    }

    private int releaseVerticalGlow(int deltaY, float x) {
        float consumed = 0.0f;
        float displacement = x / getWidth();
        float pullDistance = deltaY / getHeight();
        EdgeEffect edgeEffect = this.mTopGlow;
        if (edgeEffect != null && edgeEffect.getDistance() != 0.0f) {
            consumed = -this.mTopGlow.onPullDistance(-pullDistance, displacement);
            if (this.mTopGlow.getDistance() == 0.0f) {
                this.mTopGlow.onRelease();
            }
        } else {
            EdgeEffect edgeEffect2 = this.mBottomGlow;
            if (edgeEffect2 != null && edgeEffect2.getDistance() != 0.0f) {
                consumed = this.mBottomGlow.onPullDistance(pullDistance, 1.0f - displacement);
                if (this.mBottomGlow.getDistance() == 0.0f) {
                    this.mBottomGlow.onRelease();
                }
            }
        }
        int pixelsConsumed = Math.round(getHeight() * consumed);
        if (pixelsConsumed != 0) {
            invalidate();
        }
        return pixelsConsumed;
    }

    private void resetTouch() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
        }
        stopNestedScroll();
        releaseGlows();
    }

    private void cancelTouch() {
        resetTouch();
        setScrollState(0);
    }

    private void onPointerUp(MotionEvent e) {
        int actionIndex = e.getActionIndex();
        if (e.getPointerId(actionIndex) == this.mScrollPointerId) {
            int newIndex = actionIndex == 0 ? 1 : 0;
            this.mScrollPointerId = e.getPointerId(newIndex);
            int x = (int) (e.getX(newIndex) + 0.5f);
            this.mLastTouchX = x;
            this.mInitialTouchX = x;
            int y = (int) (e.getY(newIndex) + 0.5f);
            this.mLastTouchY = y;
            this.mInitialTouchY = y;
        }
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        float vScroll;
        float hScroll;
        if (this.mLayout != null && !this.mLayoutFrozen && (event.getSource() & 2) != 0 && event.getAction() == 8) {
            if (this.mLayout.canScrollVertically()) {
                vScroll = -event.getAxisValue(9);
            } else {
                vScroll = 0.0f;
            }
            if (this.mLayout.canScrollHorizontally()) {
                hScroll = event.getAxisValue(10);
            } else {
                hScroll = 0.0f;
            }
            if (vScroll != 0.0f || hScroll != 0.0f) {
                float scrollFactor = getScrollFactor();
                scrollByInternal((int) (hScroll * scrollFactor), (int) (vScroll * scrollFactor), event);
            }
        }
        return false;
    }

    private float getScrollFactor() {
        if (this.mScrollFactor == Float.MIN_VALUE) {
            TypedValue outValue = new TypedValue();
            if (getContext().getTheme().resolveAttribute(16842829, outValue, true)) {
                this.mScrollFactor = outValue.getDimension(getContext().getResources().getDisplayMetrics());
            } else {
                return 0.0f;
            }
        }
        return this.mScrollFactor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthSpec, int heightSpec) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            defaultOnMeasure(widthSpec, heightSpec);
            return;
        }
        boolean skipMeasure = false;
        if (layoutManager.mAutoMeasure) {
            int widthMode = View.MeasureSpec.getMode(widthSpec);
            int heightMode = View.MeasureSpec.getMode(heightSpec);
            if (widthMode == 1073741824 && heightMode == 1073741824) {
                skipMeasure = true;
            }
            this.mLayout.onMeasure(this.mRecycler, this.mState, widthSpec, heightSpec);
            if (skipMeasure || this.mAdapter == null) {
                return;
            }
            if (this.mState.mLayoutStep == 1) {
                dispatchLayoutStep1();
            }
            this.mLayout.setMeasureSpecs(widthSpec, heightSpec);
            this.mState.mIsMeasuring = true;
            dispatchLayoutStep2();
            this.mLayout.setMeasuredDimensionFromChildren(widthSpec, heightSpec);
            if (this.mLayout.shouldMeasureTwice()) {
                this.mLayout.setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
                this.mState.mIsMeasuring = true;
                dispatchLayoutStep2();
                this.mLayout.setMeasuredDimensionFromChildren(widthSpec, heightSpec);
            }
        } else if (this.mHasFixedSize) {
            this.mLayout.onMeasure(this.mRecycler, this.mState, widthSpec, heightSpec);
        } else {
            if (this.mAdapterUpdateDuringMeasure) {
                eatRequestLayout();
                onEnterLayoutOrScroll();
                processAdapterUpdatesAndSetAnimationFlags();
                onExitLayoutOrScroll();
                if (this.mState.mRunPredictiveAnimations) {
                    this.mState.mInPreLayout = true;
                } else {
                    this.mAdapterHelper.consumeUpdatesInOnePass();
                    this.mState.mInPreLayout = false;
                }
                this.mAdapterUpdateDuringMeasure = false;
                resumeRequestLayout(false);
            }
            Adapter adapter = this.mAdapter;
            if (adapter != null) {
                this.mState.mItemCount = adapter.getItemCount();
            } else {
                this.mState.mItemCount = 0;
            }
            eatRequestLayout();
            this.mLayout.onMeasure(this.mRecycler, this.mState, widthSpec, heightSpec);
            resumeRequestLayout(false);
            this.mState.mInPreLayout = false;
        }
    }

    void defaultOnMeasure(int widthSpec, int heightSpec) {
        int width = LayoutManager.chooseSize(widthSpec, getPaddingLeft() + getPaddingRight(), getMinimumWidth());
        int height = LayoutManager.chooseSize(heightSpec, getPaddingTop() + getPaddingBottom(), getMinimumHeight());
        setMeasuredDimension(width, height);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            invalidateGlows();
        }
    }

    public void setItemAnimator(ItemAnimator animator) {
        ItemAnimator itemAnimator = this.mItemAnimator;
        if (itemAnimator != null) {
            itemAnimator.endAnimations();
            this.mItemAnimator.setListener(null);
        }
        this.mItemAnimator = animator;
        if (animator != null) {
            animator.setListener(this.mItemAnimatorListener);
        }
    }

    void onEnterLayoutOrScroll() {
        this.mLayoutOrScrollCounter++;
    }

    void onExitLayoutOrScroll() {
        int i = this.mLayoutOrScrollCounter - 1;
        this.mLayoutOrScrollCounter = i;
        if (i < 1) {
            this.mLayoutOrScrollCounter = 0;
            dispatchContentChangedIfNecessary();
            dispatchPendingImportantForAccessibilityChanges();
        }
    }

    boolean isAccessibilityEnabled() {
        AccessibilityManager accessibilityManager = this.mAccessibilityManager;
        return accessibilityManager != null && accessibilityManager.isEnabled();
    }

    private void dispatchContentChangedIfNecessary() {
        int flags = this.mEatenAccessibilityChangeFlags;
        this.mEatenAccessibilityChangeFlags = 0;
        if (flags != 0 && isAccessibilityEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setEventType(2048);
            event.setContentChangeTypes(flags);
            sendAccessibilityEventUnchecked(event);
        }
    }

    public boolean isComputingLayout() {
        return this.mLayoutOrScrollCounter > 0;
    }

    boolean shouldDeferAccessibilityEvent(AccessibilityEvent event) {
        if (isComputingLayout()) {
            int type = 0;
            if (event != null) {
                type = event.getContentChangeTypes();
            }
            if (type == 0) {
                type = 0;
            }
            this.mEatenAccessibilityChangeFlags |= type;
            return true;
        }
        return false;
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        if (shouldDeferAccessibilityEvent(event)) {
            return;
        }
        super.sendAccessibilityEventUnchecked(event);
    }

    public ItemAnimator getItemAnimator() {
        return this.mItemAnimator;
    }

    void postAnimationRunner() {
        if (!this.mPostedAnimatorRunner && this.mIsAttached) {
            postOnAnimation(this.mItemAnimatorRunner);
            this.mPostedAnimatorRunner = true;
        }
    }

    private boolean predictiveItemAnimationsEnabled() {
        return this.mItemAnimator != null && this.mLayout.supportsPredictiveItemAnimations();
    }

    private void processAdapterUpdatesAndSetAnimationFlags() {
        if (this.mDataSetHasChangedAfterLayout) {
            this.mAdapterHelper.reset();
            this.mLayout.onItemsChanged(this);
        }
        if (predictiveItemAnimationsEnabled()) {
            this.mAdapterHelper.preProcess();
        } else {
            this.mAdapterHelper.consumeUpdatesInOnePass();
        }
        boolean z = false;
        boolean animationTypeSupported = this.mItemsAddedOrRemoved || this.mItemsChanged;
        this.mState.mRunSimpleAnimations = this.mFirstLayoutComplete && this.mItemAnimator != null && (this.mDataSetHasChangedAfterLayout || animationTypeSupported || this.mLayout.mRequestedSimpleAnimations) && (!this.mDataSetHasChangedAfterLayout || this.mAdapter.hasStableIds());
        State state = this.mState;
        if (state.mRunSimpleAnimations && animationTypeSupported && !this.mDataSetHasChangedAfterLayout && predictiveItemAnimationsEnabled()) {
            z = true;
        }
        state.mRunPredictiveAnimations = z;
    }

    void dispatchLayout() {
        if (this.mAdapter == null) {
            Log.m110e(TAG, "No adapter attached; skipping layout");
        } else if (this.mLayout == null) {
            Log.m110e(TAG, "No layout manager attached; skipping layout");
        } else {
            this.mState.mIsMeasuring = false;
            if (this.mState.mLayoutStep == 1) {
                dispatchLayoutStep1();
                this.mLayout.setExactMeasureSpecsFrom(this);
                dispatchLayoutStep2();
            } else if (this.mAdapterHelper.hasUpdates() || this.mLayout.getWidth() != getWidth() || this.mLayout.getHeight() != getHeight()) {
                this.mLayout.setExactMeasureSpecsFrom(this);
                dispatchLayoutStep2();
            } else {
                this.mLayout.setExactMeasureSpecsFrom(this);
            }
            dispatchLayoutStep3();
        }
    }

    private void saveFocusInfo() {
        int adapterPosition;
        View child = null;
        if (this.mPreserveFocusAfterLayout && hasFocus() && this.mAdapter != null) {
            child = getFocusedChild();
        }
        ViewHolder focusedVh = child == null ? null : findContainingViewHolder(child);
        if (focusedVh == null) {
            resetFocusInfo();
            return;
        }
        this.mState.mFocusedItemId = this.mAdapter.hasStableIds() ? focusedVh.getItemId() : -1L;
        State state = this.mState;
        if (this.mDataSetHasChangedAfterLayout) {
            adapterPosition = -1;
        } else {
            adapterPosition = focusedVh.isRemoved() ? focusedVh.mOldPosition : focusedVh.getAdapterPosition();
        }
        state.mFocusedItemPosition = adapterPosition;
        this.mState.mFocusedSubChildId = getDeepestFocusedViewWithId(focusedVh.itemView);
    }

    private void resetFocusInfo() {
        this.mState.mFocusedItemId = -1L;
        this.mState.mFocusedItemPosition = -1;
        this.mState.mFocusedSubChildId = -1;
    }

    private View findNextViewToFocus() {
        ViewHolder nextFocus;
        int startFocusSearchIndex = this.mState.mFocusedItemPosition != -1 ? this.mState.mFocusedItemPosition : 0;
        int itemCount = this.mState.getItemCount();
        for (int i = startFocusSearchIndex; i < itemCount; i++) {
            ViewHolder nextFocus2 = findViewHolderForAdapterPosition(i);
            if (nextFocus2 == null) {
                break;
            } else if (nextFocus2.itemView.hasFocusable()) {
                return nextFocus2.itemView;
            }
        }
        int limit = Math.min(itemCount, startFocusSearchIndex);
        for (int i2 = limit - 1; i2 >= 0 && (nextFocus = findViewHolderForAdapterPosition(i2)) != null; i2--) {
            if (nextFocus.itemView.hasFocusable()) {
                return nextFocus.itemView;
            }
        }
        return null;
    }

    private void recoverFocusFromState() {
        View child;
        if (this.mPreserveFocusAfterLayout && this.mAdapter != null && hasFocus() && getDescendantFocusability() != 393216) {
            if (getDescendantFocusability() == 131072 && isFocused()) {
                return;
            }
            if (!isFocused()) {
                View focusedChild = getFocusedChild();
                if (IGNORE_DETACHED_FOCUSED_CHILD && (focusedChild.getParent() == null || !focusedChild.hasFocus())) {
                    if (this.mChildHelper.getChildCount() == 0) {
                        requestFocus();
                        return;
                    }
                } else if (!this.mChildHelper.isHidden(focusedChild)) {
                    return;
                }
            }
            ViewHolder focusTarget = null;
            if (this.mState.mFocusedItemId != -1 && this.mAdapter.hasStableIds()) {
                focusTarget = findViewHolderForItemId(this.mState.mFocusedItemId);
            }
            View viewToFocus = null;
            if (focusTarget == null || this.mChildHelper.isHidden(focusTarget.itemView) || !focusTarget.itemView.hasFocusable()) {
                if (this.mChildHelper.getChildCount() > 0) {
                    viewToFocus = findNextViewToFocus();
                }
            } else {
                viewToFocus = focusTarget.itemView;
            }
            if (viewToFocus != null) {
                if (this.mState.mFocusedSubChildId != -1 && (child = viewToFocus.findViewById(this.mState.mFocusedSubChildId)) != null && child.isFocusable()) {
                    viewToFocus = child;
                }
                viewToFocus.requestFocus();
            }
        }
    }

    private int getDeepestFocusedViewWithId(View view) {
        int lastKnownId = view.getId();
        while (!view.isFocused() && (view instanceof ViewGroup) && view.hasFocus()) {
            view = ((ViewGroup) view).getFocusedChild();
            int id = view.getId();
            if (id != -1) {
                lastKnownId = view.getId();
            }
        }
        return lastKnownId;
    }

    private void dispatchLayoutStep1() {
        boolean z = true;
        this.mState.assertLayoutStep(1);
        this.mState.mIsMeasuring = false;
        eatRequestLayout();
        this.mViewInfoStore.clear();
        onEnterLayoutOrScroll();
        processAdapterUpdatesAndSetAnimationFlags();
        saveFocusInfo();
        State state = this.mState;
        state.mTrackOldChangeHolders = (state.mRunSimpleAnimations && this.mItemsChanged) ? false : false;
        this.mItemsChanged = false;
        this.mItemsAddedOrRemoved = false;
        State state2 = this.mState;
        state2.mInPreLayout = state2.mRunPredictiveAnimations;
        this.mState.mItemCount = this.mAdapter.getItemCount();
        findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        if (this.mState.mRunSimpleAnimations) {
            int count = this.mChildHelper.getChildCount();
            for (int i = 0; i < count; i++) {
                ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!holder.shouldIgnore() && (!holder.isInvalid() || this.mAdapter.hasStableIds())) {
                    this.mViewInfoStore.addToPreLayout(holder, this.mItemAnimator.recordPreLayoutInformation(this.mState, holder, ItemAnimator.buildAdapterChangeFlagsForAnimations(holder), holder.getUnmodifiedPayloads()));
                    if (this.mState.mTrackOldChangeHolders && holder.isUpdated() && !holder.isRemoved() && !holder.shouldIgnore() && !holder.isInvalid()) {
                        long key = getChangedHolderKey(holder);
                        this.mViewInfoStore.addToOldChangeHolders(key, holder);
                    }
                }
            }
        }
        if (this.mState.mRunPredictiveAnimations) {
            saveOldPositions();
            boolean didStructureChange = this.mState.mStructureChanged;
            this.mState.mStructureChanged = false;
            this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
            this.mState.mStructureChanged = didStructureChange;
            for (int i2 = 0; i2 < this.mChildHelper.getChildCount(); i2++) {
                View child = this.mChildHelper.getChildAt(i2);
                ViewHolder viewHolder = getChildViewHolderInt(child);
                if (!viewHolder.shouldIgnore() && !this.mViewInfoStore.isInPreLayout(viewHolder)) {
                    int flags = ItemAnimator.buildAdapterChangeFlagsForAnimations(viewHolder);
                    boolean wasHidden = viewHolder.hasAnyOfTheFlags(8192);
                    if (!wasHidden) {
                        flags |= 4096;
                    }
                    ItemAnimator.ItemHolderInfo animationInfo = this.mItemAnimator.recordPreLayoutInformation(this.mState, viewHolder, flags, viewHolder.getUnmodifiedPayloads());
                    if (wasHidden) {
                        recordAnimationInfoIfBouncedHiddenView(viewHolder, animationInfo);
                    } else {
                        this.mViewInfoStore.addToAppearedInPreLayoutHolders(viewHolder, animationInfo);
                    }
                }
            }
            clearOldPositions();
        } else {
            clearOldPositions();
        }
        onExitLayoutOrScroll();
        resumeRequestLayout(false);
        this.mState.mLayoutStep = 2;
    }

    private void dispatchLayoutStep2() {
        eatRequestLayout();
        onEnterLayoutOrScroll();
        this.mState.assertLayoutStep(6);
        this.mAdapterHelper.consumeUpdatesInOnePass();
        this.mState.mItemCount = this.mAdapter.getItemCount();
        this.mState.mDeletedInvisibleItemCountSincePreviousLayout = 0;
        this.mState.mInPreLayout = false;
        this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
        this.mState.mStructureChanged = false;
        this.mPendingSavedState = null;
        State state = this.mState;
        state.mRunSimpleAnimations = state.mRunSimpleAnimations && this.mItemAnimator != null;
        this.mState.mLayoutStep = 4;
        onExitLayoutOrScroll();
        resumeRequestLayout(false);
    }

    private void dispatchLayoutStep3() {
        this.mState.assertLayoutStep(4);
        eatRequestLayout();
        onEnterLayoutOrScroll();
        this.mState.mLayoutStep = 1;
        if (this.mState.mRunSimpleAnimations) {
            for (int i = this.mChildHelper.getChildCount() - 1; i >= 0; i--) {
                ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!holder.shouldIgnore()) {
                    long key = getChangedHolderKey(holder);
                    ItemAnimator.ItemHolderInfo animationInfo = this.mItemAnimator.recordPostLayoutInformation(this.mState, holder);
                    ViewHolder oldChangeViewHolder = this.mViewInfoStore.getFromOldChangeHolders(key);
                    if (oldChangeViewHolder != null && !oldChangeViewHolder.shouldIgnore()) {
                        boolean oldDisappearing = this.mViewInfoStore.isDisappearing(oldChangeViewHolder);
                        boolean newDisappearing = this.mViewInfoStore.isDisappearing(holder);
                        if (oldDisappearing && oldChangeViewHolder == holder) {
                            this.mViewInfoStore.addToPostLayout(holder, animationInfo);
                        } else {
                            ItemAnimator.ItemHolderInfo preInfo = this.mViewInfoStore.popFromPreLayout(oldChangeViewHolder);
                            this.mViewInfoStore.addToPostLayout(holder, animationInfo);
                            ItemAnimator.ItemHolderInfo postInfo = this.mViewInfoStore.popFromPostLayout(holder);
                            if (preInfo == null) {
                                handleMissingPreInfoForChangeError(key, holder, oldChangeViewHolder);
                            } else {
                                animateChange(oldChangeViewHolder, holder, preInfo, postInfo, oldDisappearing, newDisappearing);
                            }
                        }
                    } else {
                        this.mViewInfoStore.addToPostLayout(holder, animationInfo);
                    }
                }
            }
            this.mViewInfoStore.process(this.mViewInfoProcessCallback);
        }
        this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        State state = this.mState;
        state.mPreviousLayoutItemCount = state.mItemCount;
        this.mDataSetHasChangedAfterLayout = false;
        this.mState.mRunSimpleAnimations = false;
        this.mState.mRunPredictiveAnimations = false;
        this.mLayout.mRequestedSimpleAnimations = false;
        if (this.mRecycler.mChangedScrap != null) {
            this.mRecycler.mChangedScrap.clear();
        }
        if (this.mLayout.mPrefetchMaxObservedInInitialPrefetch) {
            this.mLayout.mPrefetchMaxCountObserved = 0;
            this.mLayout.mPrefetchMaxObservedInInitialPrefetch = false;
            this.mRecycler.updateViewCacheSize();
        }
        this.mLayout.onLayoutCompleted(this.mState);
        onExitLayoutOrScroll();
        resumeRequestLayout(false);
        this.mViewInfoStore.clear();
        int[] iArr = this.mMinMaxLayoutPositions;
        if (didChildRangeChange(iArr[0], iArr[1])) {
            dispatchOnScrolled(0, 0);
        }
        recoverFocusFromState();
        resetFocusInfo();
    }

    private void handleMissingPreInfoForChangeError(long key, ViewHolder holder, ViewHolder oldChangeViewHolder) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = this.mChildHelper.getChildAt(i);
            ViewHolder other = getChildViewHolderInt(view);
            if (other != holder) {
                long otherKey = getChangedHolderKey(other);
                if (otherKey == key) {
                    Adapter adapter = this.mAdapter;
                    if (adapter != null && adapter.hasStableIds()) {
                        throw new IllegalStateException("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:" + other + " \n View Holder 2:" + holder);
                    }
                    throw new IllegalStateException("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:" + other + " \n View Holder 2:" + holder);
                }
            }
        }
        Log.m110e(TAG, "Problem while matching changed view holders with the newones. The pre-layout information for the change holder " + oldChangeViewHolder + " cannot be found but it is necessary for " + holder);
    }

    void recordAnimationInfoIfBouncedHiddenView(ViewHolder viewHolder, ItemAnimator.ItemHolderInfo animationInfo) {
        viewHolder.setFlags(0, 8192);
        if (this.mState.mTrackOldChangeHolders && viewHolder.isUpdated() && !viewHolder.isRemoved() && !viewHolder.shouldIgnore()) {
            long key = getChangedHolderKey(viewHolder);
            this.mViewInfoStore.addToOldChangeHolders(key, viewHolder);
        }
        this.mViewInfoStore.addToPreLayout(viewHolder, animationInfo);
    }

    private void findMinMaxChildLayoutPositions(int[] into) {
        int count = this.mChildHelper.getChildCount();
        if (count == 0) {
            into[0] = -1;
            into[1] = -1;
            return;
        }
        int minPositionPreLayout = Integer.MAX_VALUE;
        int maxPositionPreLayout = Integer.MIN_VALUE;
        for (int i = 0; i < count; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (!holder.shouldIgnore()) {
                int pos = holder.getLayoutPosition();
                if (pos < minPositionPreLayout) {
                    minPositionPreLayout = pos;
                }
                if (pos > maxPositionPreLayout) {
                    maxPositionPreLayout = pos;
                }
            }
        }
        into[0] = minPositionPreLayout;
        into[1] = maxPositionPreLayout;
    }

    private boolean didChildRangeChange(int minPositionPreLayout, int maxPositionPreLayout) {
        findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        int[] iArr = this.mMinMaxLayoutPositions;
        return (iArr[0] == minPositionPreLayout && iArr[1] == maxPositionPreLayout) ? false : true;
    }

    @Override // android.view.ViewGroup
    protected void removeDetachedView(View child, boolean animate) {
        ViewHolder vh = getChildViewHolderInt(child);
        if (vh != null) {
            if (vh.isTmpDetached()) {
                vh.clearTmpDetachFlag();
            } else if (!vh.shouldIgnore()) {
                throw new IllegalArgumentException("Called removeDetachedView with a view which is not flagged as tmp detached." + vh);
            }
        }
        dispatchChildDetached(child);
        super.removeDetachedView(child, animate);
    }

    long getChangedHolderKey(ViewHolder holder) {
        return this.mAdapter.hasStableIds() ? holder.getItemId() : holder.mPosition;
    }

    void animateAppearance(ViewHolder itemHolder, ItemAnimator.ItemHolderInfo preLayoutInfo, ItemAnimator.ItemHolderInfo postLayoutInfo) {
        itemHolder.setIsRecyclable(false);
        if (this.mItemAnimator.animateAppearance(itemHolder, preLayoutInfo, postLayoutInfo)) {
            postAnimationRunner();
        }
    }

    void animateDisappearance(ViewHolder holder, ItemAnimator.ItemHolderInfo preLayoutInfo, ItemAnimator.ItemHolderInfo postLayoutInfo) {
        addAnimatingView(holder);
        holder.setIsRecyclable(false);
        if (this.mItemAnimator.animateDisappearance(holder, preLayoutInfo, postLayoutInfo)) {
            postAnimationRunner();
        }
    }

    private void animateChange(ViewHolder oldHolder, ViewHolder newHolder, ItemAnimator.ItemHolderInfo preInfo, ItemAnimator.ItemHolderInfo postInfo, boolean oldHolderDisappearing, boolean newHolderDisappearing) {
        oldHolder.setIsRecyclable(false);
        if (oldHolderDisappearing) {
            addAnimatingView(oldHolder);
        }
        if (oldHolder != newHolder) {
            if (newHolderDisappearing) {
                addAnimatingView(newHolder);
            }
            oldHolder.mShadowedHolder = newHolder;
            addAnimatingView(oldHolder);
            this.mRecycler.unscrapView(oldHolder);
            newHolder.setIsRecyclable(false);
            newHolder.mShadowingHolder = oldHolder;
        }
        if (this.mItemAnimator.animateChange(oldHolder, newHolder, preInfo, postInfo)) {
            postAnimationRunner();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Trace.beginSection(TRACE_ON_LAYOUT_TAG);
        dispatchLayout();
        Trace.endSection();
        this.mFirstLayoutComplete = true;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.mEatRequestLayout == 0 && !this.mLayoutFrozen) {
            super.requestLayout();
        } else {
            this.mLayoutRequestEaten = true;
        }
    }

    void markItemDecorInsetsDirty() {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.mChildHelper.getUnfilteredChildAt(i);
            ((LayoutParams) child.getLayoutParams()).mInsetsDirty = true;
        }
        this.mRecycler.markItemDecorInsetsDirty();
    }

    @Override // android.view.View
    public void draw(Canvas c) {
        super.draw(c);
        int count = this.mItemDecorations.size();
        for (int i = 0; i < count; i++) {
            this.mItemDecorations.get(i).onDrawOver(c, this, this.mState);
        }
        boolean needsInvalidate = false;
        EdgeEffect edgeEffect = this.mLeftGlow;
        boolean z = true;
        if (edgeEffect != null && !edgeEffect.isFinished()) {
            int restore = c.save();
            int padding = this.mClipToPadding ? getPaddingBottom() : 0;
            c.rotate(270.0f);
            c.translate((-getHeight()) + padding, 0.0f);
            EdgeEffect edgeEffect2 = this.mLeftGlow;
            needsInvalidate = edgeEffect2 != null && edgeEffect2.draw(c);
            c.restoreToCount(restore);
        }
        EdgeEffect edgeEffect3 = this.mTopGlow;
        if (edgeEffect3 != null && !edgeEffect3.isFinished()) {
            int restore2 = c.save();
            if (this.mClipToPadding) {
                c.translate(getPaddingLeft(), getPaddingTop());
            }
            EdgeEffect edgeEffect4 = this.mTopGlow;
            needsInvalidate |= edgeEffect4 != null && edgeEffect4.draw(c);
            c.restoreToCount(restore2);
        }
        EdgeEffect edgeEffect5 = this.mRightGlow;
        if (edgeEffect5 != null && !edgeEffect5.isFinished()) {
            int restore3 = c.save();
            int width = getWidth();
            int padding2 = this.mClipToPadding ? getPaddingTop() : 0;
            c.rotate(90.0f);
            c.translate(-padding2, -width);
            EdgeEffect edgeEffect6 = this.mRightGlow;
            needsInvalidate |= edgeEffect6 != null && edgeEffect6.draw(c);
            c.restoreToCount(restore3);
        }
        EdgeEffect edgeEffect7 = this.mBottomGlow;
        if (edgeEffect7 != null && !edgeEffect7.isFinished()) {
            int restore4 = c.save();
            c.rotate(180.0f);
            if (this.mClipToPadding) {
                c.translate((-getWidth()) + getPaddingRight(), (-getHeight()) + getPaddingBottom());
            } else {
                c.translate(-getWidth(), -getHeight());
            }
            EdgeEffect edgeEffect8 = this.mBottomGlow;
            needsInvalidate |= (edgeEffect8 == null || !edgeEffect8.draw(c)) ? false : false;
            c.restoreToCount(restore4);
        }
        if (!needsInvalidate && this.mItemAnimator != null && this.mItemDecorations.size() > 0 && this.mItemAnimator.isRunning()) {
            needsInvalidate = true;
        }
        if (needsInvalidate) {
            postInvalidateOnAnimation();
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas c) {
        super.onDraw(c);
        int count = this.mItemDecorations.size();
        for (int i = 0; i < count; i++) {
            this.mItemDecorations.get(i).onDraw(c, this, this.mState);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return (p instanceof LayoutParams) && this.mLayout.checkLayoutParams((LayoutParams) p);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            throw new IllegalStateException("RecyclerView has no LayoutManager");
        }
        return layoutManager.generateDefaultLayoutParams();
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            throw new IllegalStateException("RecyclerView has no LayoutManager");
        }
        return layoutManager.generateLayoutParams(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager == null) {
            throw new IllegalStateException("RecyclerView has no LayoutManager");
        }
        return layoutManager.generateLayoutParams(p);
    }

    public boolean isAnimating() {
        ItemAnimator itemAnimator = this.mItemAnimator;
        return itemAnimator != null && itemAnimator.isRunning();
    }

    void saveOldPositions() {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!holder.shouldIgnore()) {
                holder.saveOldPosition();
            }
        }
    }

    void clearOldPositions() {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!holder.shouldIgnore()) {
                holder.clearOldPosition();
            }
        }
        this.mRecycler.clearOldPositions();
    }

    void offsetPositionRecordsForMove(int from, int to) {
        int start;
        int end;
        int inBetweenOffset;
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        if (from < to) {
            start = from;
            end = to;
            inBetweenOffset = -1;
        } else {
            start = to;
            end = from;
            inBetweenOffset = 1;
        }
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && holder.mPosition >= start && holder.mPosition <= end) {
                if (holder.mPosition == from) {
                    holder.offsetPosition(to - from, false);
                } else {
                    holder.offsetPosition(inBetweenOffset, false);
                }
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForMove(from, to);
        requestLayout();
    }

    void offsetPositionRecordsForInsert(int positionStart, int itemCount) {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.shouldIgnore() && holder.mPosition >= positionStart) {
                holder.offsetPosition(itemCount, false);
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForInsert(positionStart, itemCount);
        requestLayout();
    }

    void offsetPositionRecordsForRemove(int positionStart, int itemCount, boolean applyToPreLayout) {
        int positionEnd = positionStart + itemCount;
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.shouldIgnore()) {
                if (holder.mPosition >= positionEnd) {
                    holder.offsetPosition(-itemCount, applyToPreLayout);
                    this.mState.mStructureChanged = true;
                } else if (holder.mPosition >= positionStart) {
                    holder.flagRemovedAndOffsetPosition(positionStart - 1, -itemCount, applyToPreLayout);
                    this.mState.mStructureChanged = true;
                }
            }
        }
        this.mRecycler.offsetPositionRecordsForRemove(positionStart, itemCount, applyToPreLayout);
        requestLayout();
    }

    void viewRangeUpdate(int positionStart, int itemCount, Object payload) {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        int positionEnd = positionStart + itemCount;
        for (int i = 0; i < childCount; i++) {
            View child = this.mChildHelper.getUnfilteredChildAt(i);
            ViewHolder holder = getChildViewHolderInt(child);
            if (holder != null && !holder.shouldIgnore() && holder.mPosition >= positionStart && holder.mPosition < positionEnd) {
                holder.addFlags(2);
                holder.addChangePayload(payload);
                ((LayoutParams) child.getLayoutParams()).mInsetsDirty = true;
            }
        }
        this.mRecycler.viewRangeUpdate(positionStart, itemCount);
    }

    boolean canReuseUpdatedViewHolder(ViewHolder viewHolder) {
        ItemAnimator itemAnimator = this.mItemAnimator;
        return itemAnimator == null || itemAnimator.canReuseUpdatedViewHolder(viewHolder, viewHolder.getUnmodifiedPayloads());
    }

    void setDataSetChangedAfterLayout() {
        if (this.mDataSetHasChangedAfterLayout) {
            return;
        }
        this.mDataSetHasChangedAfterLayout = true;
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.shouldIgnore()) {
                holder.addFlags(512);
            }
        }
        this.mRecycler.setAdapterPositionsAsUnknown();
        markKnownViewsInvalid();
    }

    void markKnownViewsInvalid() {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.shouldIgnore()) {
                holder.addFlags(6);
            }
        }
        markItemDecorInsetsDirty();
        this.mRecycler.markKnownViewsInvalid();
    }

    public void invalidateItemDecorations() {
        if (this.mItemDecorations.size() == 0) {
            return;
        }
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public boolean getPreserveFocusAfterLayout() {
        return this.mPreserveFocusAfterLayout;
    }

    public void setPreserveFocusAfterLayout(boolean preserveFocusAfterLayout) {
        this.mPreserveFocusAfterLayout = preserveFocusAfterLayout;
    }

    public ViewHolder getChildViewHolder(View child) {
        ViewParent parent = child.getParent();
        if (parent != null && parent != this) {
            throw new IllegalArgumentException("View " + child + " is not a direct child of " + this);
        }
        return getChildViewHolderInt(child);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:?, code lost:
        return r3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public View findContainingItemView(View view) {
        ViewParent parent = view.getParent();
        while (parent != null && parent != this && (parent instanceof View)) {
            view = (View) parent;
            parent = view.getParent();
        }
        return null;
    }

    public ViewHolder findContainingViewHolder(View view) {
        View itemView = findContainingItemView(view);
        if (itemView == null) {
            return null;
        }
        return getChildViewHolder(itemView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ViewHolder getChildViewHolderInt(View child) {
        if (child == null) {
            return null;
        }
        return ((LayoutParams) child.getLayoutParams()).mViewHolder;
    }

    @Deprecated
    public int getChildPosition(View child) {
        return getChildAdapterPosition(child);
    }

    public int getChildAdapterPosition(View child) {
        ViewHolder holder = getChildViewHolderInt(child);
        if (holder != null) {
            return holder.getAdapterPosition();
        }
        return -1;
    }

    public int getChildLayoutPosition(View child) {
        ViewHolder holder = getChildViewHolderInt(child);
        if (holder != null) {
            return holder.getLayoutPosition();
        }
        return -1;
    }

    public long getChildItemId(View child) {
        ViewHolder holder;
        Adapter adapter = this.mAdapter;
        if (adapter == null || !adapter.hasStableIds() || (holder = getChildViewHolderInt(child)) == null) {
            return -1L;
        }
        return holder.getItemId();
    }

    @Deprecated
    public ViewHolder findViewHolderForPosition(int position) {
        return findViewHolderForPosition(position, false);
    }

    public ViewHolder findViewHolderForLayoutPosition(int position) {
        return findViewHolderForPosition(position, false);
    }

    public ViewHolder findViewHolderForAdapterPosition(int position) {
        if (this.mDataSetHasChangedAfterLayout) {
            return null;
        }
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        ViewHolder hidden = null;
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.isRemoved() && getAdapterPositionFor(holder) == position) {
                if (this.mChildHelper.isHidden(holder.itemView)) {
                    hidden = holder;
                } else {
                    return holder;
                }
            }
        }
        return hidden;
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0036 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    ViewHolder findViewHolderForPosition(int position, boolean checkNewPosition) {
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        ViewHolder hidden = null;
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.isRemoved()) {
                if (checkNewPosition) {
                    if (holder.mPosition != position) {
                        continue;
                    }
                    if (!this.mChildHelper.isHidden(holder.itemView)) {
                        hidden = holder;
                    } else {
                        return holder;
                    }
                } else {
                    if (holder.getLayoutPosition() != position) {
                        continue;
                    }
                    if (!this.mChildHelper.isHidden(holder.itemView)) {
                    }
                }
            }
        }
        return hidden;
    }

    public ViewHolder findViewHolderForItemId(long id) {
        Adapter adapter = this.mAdapter;
        if (adapter == null || !adapter.hasStableIds()) {
            return null;
        }
        int childCount = this.mChildHelper.getUnfilteredChildCount();
        ViewHolder hidden = null;
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (holder != null && !holder.isRemoved() && holder.getItemId() == id) {
                if (this.mChildHelper.isHidden(holder.itemView)) {
                    hidden = holder;
                } else {
                    return holder;
                }
            }
        }
        return hidden;
    }

    public View findChildViewUnder(float x, float y) {
        int count = this.mChildHelper.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View child = this.mChildHelper.getChildAt(i);
            float translationX = child.getTranslationX();
            float translationY = child.getTranslationY();
            if (x >= child.getLeft() + translationX && x <= child.getRight() + translationX && y >= child.getTop() + translationY && y <= child.getBottom() + translationY) {
                return child;
            }
        }
        return null;
    }

    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public void offsetChildrenVertical(int dy) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mChildHelper.getChildAt(i).offsetTopAndBottom(dy);
        }
    }

    public void onChildAttachedToWindow(View child) {
    }

    public void onChildDetachedFromWindow(View child) {
    }

    public void offsetChildrenHorizontal(int dx) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mChildHelper.getChildAt(i).offsetLeftAndRight(dx);
        }
    }

    public void getDecoratedBoundsWithMargins(View view, Rect outBounds) {
        getDecoratedBoundsWithMarginsInt(view, outBounds);
    }

    static void getDecoratedBoundsWithMarginsInt(View view, Rect outBounds) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        Rect insets = lp.mDecorInsets;
        outBounds.set((view.getLeft() - insets.left) - lp.leftMargin, (view.getTop() - insets.top) - lp.topMargin, view.getRight() + insets.right + lp.rightMargin, view.getBottom() + insets.bottom + lp.bottomMargin);
    }

    Rect getItemDecorInsetsForChild(View child) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (!lp.mInsetsDirty) {
            return lp.mDecorInsets;
        }
        if (this.mState.isPreLayout() && (lp.isItemChanged() || lp.isViewInvalid())) {
            return lp.mDecorInsets;
        }
        Rect insets = lp.mDecorInsets;
        insets.set(0, 0, 0, 0);
        int decorCount = this.mItemDecorations.size();
        for (int i = 0; i < decorCount; i++) {
            this.mTempRect.set(0, 0, 0, 0);
            this.mItemDecorations.get(i).getItemOffsets(this.mTempRect, child, this, this.mState);
            insets.left += this.mTempRect.left;
            insets.top += this.mTempRect.top;
            insets.right += this.mTempRect.right;
            insets.bottom += this.mTempRect.bottom;
        }
        lp.mInsetsDirty = false;
        return insets;
    }

    public void onScrolled(int dx, int dy) {
    }

    void dispatchOnScrolled(int hresult, int vresult) {
        this.mDispatchScrollCounter++;
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        onScrollChanged(scrollX, scrollY, scrollX, scrollY);
        onScrolled(hresult, vresult);
        OnScrollListener onScrollListener = this.mScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrolled(this, hresult, vresult);
        }
        List<OnScrollListener> list = this.mScrollListeners;
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                this.mScrollListeners.get(i).onScrolled(this, hresult, vresult);
            }
        }
        int i2 = this.mDispatchScrollCounter;
        this.mDispatchScrollCounter = i2 - 1;
    }

    public void onScrollStateChanged(int state) {
    }

    void dispatchOnScrollStateChanged(int state) {
        LayoutManager layoutManager = this.mLayout;
        if (layoutManager != null) {
            layoutManager.onScrollStateChanged(state);
        }
        onScrollStateChanged(state);
        OnScrollListener onScrollListener = this.mScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(this, state);
        }
        List<OnScrollListener> list = this.mScrollListeners;
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                this.mScrollListeners.get(i).onScrollStateChanged(this, state);
            }
        }
    }

    public boolean hasPendingAdapterUpdates() {
        return !this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout || this.mAdapterHelper.hasPendingUpdates();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class ViewFlinger implements Runnable {
        private int mLastFlingX;
        private int mLastFlingY;
        private OverScroller mScroller;
        Interpolator mInterpolator = RecyclerView.sQuinticInterpolator;
        private boolean mEatRunOnAnimationRequest = false;
        private boolean mReSchedulePostAnimationCallback = false;

        ViewFlinger() {
            this.mScroller = new OverScroller(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (RecyclerView.this.mLayout == null) {
                stop();
                return;
            }
            disableRunOnAnimationRequests();
            RecyclerView.this.consumePendingUpdateOperations();
            OverScroller scroller = this.mScroller;
            SmoothScroller smoothScroller = RecyclerView.this.mLayout.mSmoothScroller;
            if (scroller.computeScrollOffset()) {
                int x = scroller.getCurrX();
                int y = scroller.getCurrY();
                int dx = x - this.mLastFlingX;
                int dy = y - this.mLastFlingY;
                int hresult = 0;
                int vresult = 0;
                this.mLastFlingX = x;
                this.mLastFlingY = y;
                int overscrollX = 0;
                int overscrollY = 0;
                if (RecyclerView.this.mAdapter != null) {
                    RecyclerView.this.eatRequestLayout();
                    RecyclerView.this.onEnterLayoutOrScroll();
                    Trace.beginSection(RecyclerView.TRACE_SCROLL_TAG);
                    if (dx != 0) {
                        hresult = RecyclerView.this.mLayout.scrollHorizontallyBy(dx, RecyclerView.this.mRecycler, RecyclerView.this.mState);
                        overscrollX = dx - hresult;
                    }
                    if (dy != 0) {
                        vresult = RecyclerView.this.mLayout.scrollVerticallyBy(dy, RecyclerView.this.mRecycler, RecyclerView.this.mState);
                        overscrollY = dy - vresult;
                    }
                    Trace.endSection();
                    RecyclerView.this.repositionShadowingViews();
                    RecyclerView.this.onExitLayoutOrScroll();
                    RecyclerView.this.resumeRequestLayout(false);
                    if (smoothScroller != null && !smoothScroller.isPendingInitialRun() && smoothScroller.isRunning()) {
                        int adapterSize = RecyclerView.this.mState.getItemCount();
                        if (adapterSize == 0) {
                            smoothScroller.stop();
                        } else if (smoothScroller.getTargetPosition() >= adapterSize) {
                            smoothScroller.setTargetPosition(adapterSize - 1);
                            smoothScroller.onAnimation(dx - overscrollX, dy - overscrollY);
                        } else {
                            smoothScroller.onAnimation(dx - overscrollX, dy - overscrollY);
                        }
                    }
                }
                if (!RecyclerView.this.mItemDecorations.isEmpty()) {
                    RecyclerView.this.invalidate();
                }
                if (RecyclerView.this.getOverScrollMode() != 2) {
                    RecyclerView.this.considerReleasingGlowsOnScroll(dx, dy);
                }
                if (overscrollX != 0 || overscrollY != 0) {
                    int vel = (int) scroller.getCurrVelocity();
                    int velX = 0;
                    if (overscrollX != x) {
                        velX = overscrollX < 0 ? -vel : overscrollX > 0 ? vel : 0;
                    }
                    int velY = 0;
                    if (overscrollY != y) {
                        velY = overscrollY < 0 ? -vel : overscrollY > 0 ? vel : 0;
                    }
                    if (RecyclerView.this.getOverScrollMode() != 2) {
                        RecyclerView.this.absorbGlows(velX, velY);
                    }
                    if ((velX != 0 || overscrollX == x || scroller.getFinalX() == 0) && (velY != 0 || overscrollY == y || scroller.getFinalY() == 0)) {
                        scroller.abortAnimation();
                    }
                }
                if (hresult != 0 || vresult != 0) {
                    RecyclerView.this.dispatchOnScrolled(hresult, vresult);
                }
                if (!RecyclerView.this.awakenScrollBars()) {
                    RecyclerView.this.invalidate();
                }
                boolean fullyConsumedAny = true;
                boolean fullyConsumedVertical = dy != 0 && RecyclerView.this.mLayout.canScrollVertically() && vresult == dy;
                boolean fullyConsumedHorizontal = dx != 0 && RecyclerView.this.mLayout.canScrollHorizontally() && hresult == dx;
                if ((dx != 0 || dy != 0) && !fullyConsumedHorizontal && !fullyConsumedVertical) {
                    fullyConsumedAny = false;
                }
                if (scroller.isFinished() || !fullyConsumedAny) {
                    RecyclerView.this.setScrollState(0);
                    if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                        RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
                    }
                } else {
                    postOnAnimation();
                    if (RecyclerView.this.mGapWorker != null) {
                        RecyclerView.this.mGapWorker.postFromTraversal(RecyclerView.this, dx, dy);
                    }
                }
            }
            if (smoothScroller != null) {
                if (smoothScroller.isPendingInitialRun()) {
                    smoothScroller.onAnimation(0, 0);
                }
                if (!this.mReSchedulePostAnimationCallback) {
                    smoothScroller.stop();
                }
            }
            enableRunOnAnimationRequests();
        }

        private void disableRunOnAnimationRequests() {
            this.mReSchedulePostAnimationCallback = false;
            this.mEatRunOnAnimationRequest = true;
        }

        private void enableRunOnAnimationRequests() {
            this.mEatRunOnAnimationRequest = false;
            if (this.mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        void postOnAnimation() {
            if (this.mEatRunOnAnimationRequest) {
                this.mReSchedulePostAnimationCallback = true;
                return;
            }
            RecyclerView.this.removeCallbacks(this);
            RecyclerView.this.postOnAnimation(this);
        }

        public void fling(int velocityX, int velocityY) {
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mScroller.fling(0, 0, velocityX, velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        public void smoothScrollBy(int dx, int dy) {
            smoothScrollBy(dx, dy, 0, 0);
        }

        public void smoothScrollBy(int dx, int dy, int vx, int vy) {
            smoothScrollBy(dx, dy, computeScrollDuration(dx, dy, vx, vy));
        }

        private float distanceInfluenceForSnapDuration(float f) {
            return (float) Math.sin((float) ((f - 0.5f) * 0.4712389167638204d));
        }

        private int computeScrollDuration(int dx, int dy, int vx, int vy) {
            int duration;
            int absDx = Math.abs(dx);
            int absDy = Math.abs(dy);
            boolean horizontal = absDx > absDy;
            int velocity = (int) Math.sqrt((vx * vx) + (vy * vy));
            int delta = (int) Math.sqrt((dx * dx) + (dy * dy));
            RecyclerView recyclerView = RecyclerView.this;
            int containerSize = horizontal ? recyclerView.getWidth() : recyclerView.getHeight();
            int halfContainerSize = containerSize / 2;
            float distanceRatio = Math.min(1.0f, (delta * 1.0f) / containerSize);
            float distance = halfContainerSize + (halfContainerSize * distanceInfluenceForSnapDuration(distanceRatio));
            if (velocity > 0) {
                duration = Math.round(Math.abs(distance / velocity) * 1000.0f) * 4;
            } else {
                float absDelta = horizontal ? absDx : absDy;
                duration = (int) (((absDelta / containerSize) + 1.0f) * 300.0f);
            }
            return Math.min(duration, 2000);
        }

        public void smoothScrollBy(int dx, int dy, int duration) {
            smoothScrollBy(dx, dy, duration, RecyclerView.sQuinticInterpolator);
        }

        public void smoothScrollBy(int dx, int dy, Interpolator interpolator) {
            smoothScrollBy(dx, dy, computeScrollDuration(dx, dy, 0, 0), interpolator == null ? RecyclerView.sQuinticInterpolator : interpolator);
        }

        public void smoothScrollBy(int dx, int dy, int duration, Interpolator interpolator) {
            if (this.mInterpolator != interpolator) {
                this.mInterpolator = interpolator;
                this.mScroller = new OverScroller(RecyclerView.this.getContext(), interpolator);
            }
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mScroller.startScroll(0, 0, dx, dy, duration);
            postOnAnimation();
        }

        public void stop() {
            RecyclerView.this.removeCallbacks(this);
            this.mScroller.abortAnimation();
        }
    }

    void repositionShadowingViews() {
        int count = this.mChildHelper.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = this.mChildHelper.getChildAt(i);
            ViewHolder holder = getChildViewHolder(view);
            if (holder != null && holder.mShadowingHolder != null) {
                View shadowingView = holder.mShadowingHolder.itemView;
                int left = view.getLeft();
                int top = view.getTop();
                if (left != shadowingView.getLeft() || top != shadowingView.getTop()) {
                    shadowingView.layout(left, top, shadowingView.getWidth() + left, shadowingView.getHeight() + top);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class RecyclerViewDataObserver extends AdapterDataObserver {
        RecyclerViewDataObserver() {
        }

        @Override // com.android.internal.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            RecyclerView.this.mState.mStructureChanged = true;
            RecyclerView.this.setDataSetChangedAfterLayout();
            if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
                RecyclerView.this.requestLayout();
            }
        }

        @Override // com.android.internal.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(positionStart, itemCount, payload)) {
                triggerUpdateProcessor();
            }
        }

        @Override // com.android.internal.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeInserted(int positionStart, int itemCount) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(positionStart, itemCount)) {
                triggerUpdateProcessor();
            }
        }

        @Override // com.android.internal.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(positionStart, itemCount)) {
                triggerUpdateProcessor();
            }
        }

        @Override // com.android.internal.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(fromPosition, toPosition, itemCount)) {
                triggerUpdateProcessor();
            }
        }

        void triggerUpdateProcessor() {
            if (RecyclerView.POST_UPDATES_ON_ANIMATION && RecyclerView.this.mHasFixedSize && RecyclerView.this.mIsAttached) {
                RecyclerView recyclerView = RecyclerView.this;
                recyclerView.postOnAnimation(recyclerView.mUpdateChildViewsRunnable);
                return;
            }
            RecyclerView.this.mAdapterUpdateDuringMeasure = true;
            RecyclerView.this.requestLayout();
        }
    }

    /* loaded from: classes5.dex */
    public static class RecycledViewPool {
        private static final int DEFAULT_MAX_SCRAP = 5;
        SparseArray<ScrapData> mScrap = new SparseArray<>();
        private int mAttachCount = 0;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes5.dex */
        public static class ScrapData {
            ArrayList<ViewHolder> mScrapHeap = new ArrayList<>();
            int mMaxScrap = 5;
            long mCreateRunningAverageNs = 0;
            long mBindRunningAverageNs = 0;

            ScrapData() {
            }
        }

        public void clear() {
            for (int i = 0; i < this.mScrap.size(); i++) {
                ScrapData data = this.mScrap.valueAt(i);
                data.mScrapHeap.clear();
            }
        }

        public void setMaxRecycledViews(int viewType, int max) {
            ScrapData scrapData = getScrapDataForType(viewType);
            scrapData.mMaxScrap = max;
            ArrayList<ViewHolder> scrapHeap = scrapData.mScrapHeap;
            if (scrapHeap != null) {
                while (scrapHeap.size() > max) {
                    scrapHeap.remove(scrapHeap.size() - 1);
                }
            }
        }

        public int getRecycledViewCount(int viewType) {
            return getScrapDataForType(viewType).mScrapHeap.size();
        }

        public ViewHolder getRecycledView(int viewType) {
            ScrapData scrapData = this.mScrap.get(viewType);
            if (scrapData != null && !scrapData.mScrapHeap.isEmpty()) {
                ArrayList<ViewHolder> scrapHeap = scrapData.mScrapHeap;
                return scrapHeap.remove(scrapHeap.size() - 1);
            }
            return null;
        }

        int size() {
            int count = 0;
            for (int i = 0; i < this.mScrap.size(); i++) {
                ArrayList<ViewHolder> viewHolders = this.mScrap.valueAt(i).mScrapHeap;
                if (viewHolders != null) {
                    count += viewHolders.size();
                }
            }
            return count;
        }

        public void putRecycledView(ViewHolder scrap) {
            int viewType = scrap.getItemViewType();
            ArrayList scrapHeap = getScrapDataForType(viewType).mScrapHeap;
            if (this.mScrap.get(viewType).mMaxScrap <= scrapHeap.size()) {
                return;
            }
            scrap.resetInternal();
            scrapHeap.add(scrap);
        }

        long runningAverage(long oldAverage, long newValue) {
            if (oldAverage == 0) {
                return newValue;
            }
            return ((oldAverage / 4) * 3) + (newValue / 4);
        }

        void factorInCreateTime(int viewType, long createTimeNs) {
            ScrapData scrapData = getScrapDataForType(viewType);
            scrapData.mCreateRunningAverageNs = runningAverage(scrapData.mCreateRunningAverageNs, createTimeNs);
        }

        void factorInBindTime(int viewType, long bindTimeNs) {
            ScrapData scrapData = getScrapDataForType(viewType);
            scrapData.mBindRunningAverageNs = runningAverage(scrapData.mBindRunningAverageNs, bindTimeNs);
        }

        boolean willCreateInTime(int viewType, long approxCurrentNs, long deadlineNs) {
            long expectedDurationNs = getScrapDataForType(viewType).mCreateRunningAverageNs;
            return expectedDurationNs == 0 || approxCurrentNs + expectedDurationNs < deadlineNs;
        }

        boolean willBindInTime(int viewType, long approxCurrentNs, long deadlineNs) {
            long expectedDurationNs = getScrapDataForType(viewType).mBindRunningAverageNs;
            return expectedDurationNs == 0 || approxCurrentNs + expectedDurationNs < deadlineNs;
        }

        void attach(Adapter adapter) {
            this.mAttachCount++;
        }

        void detach() {
            this.mAttachCount--;
        }

        void onAdapterChanged(Adapter oldAdapter, Adapter newAdapter, boolean compatibleWithPrevious) {
            if (oldAdapter != null) {
                detach();
            }
            if (!compatibleWithPrevious && this.mAttachCount == 0) {
                clear();
            }
            if (newAdapter != null) {
                attach(newAdapter);
            }
        }

        private ScrapData getScrapDataForType(int viewType) {
            ScrapData scrapData = this.mScrap.get(viewType);
            if (scrapData == null) {
                ScrapData scrapData2 = new ScrapData();
                this.mScrap.put(viewType, scrapData2);
                return scrapData2;
            }
            return scrapData;
        }
    }

    static RecyclerView findNestedRecyclerView(View view) {
        if (view instanceof ViewGroup) {
            if (view instanceof RecyclerView) {
                return (RecyclerView) view;
            }
            ViewGroup parent = (ViewGroup) view;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                RecyclerView descendant = findNestedRecyclerView(child);
                if (descendant != null) {
                    return descendant;
                }
            }
            return null;
        }
        return null;
    }

    static void clearNestedRecyclerViewIfNotNested(ViewHolder holder) {
        if (holder.mNestedRecyclerView != null) {
            RecyclerView item = holder.mNestedRecyclerView.get();
            while (item != null) {
                if (item == holder.itemView) {
                    return;
                }
                ViewParent parent = item.getParent();
                if (parent instanceof View) {
                    item = (View) parent;
                } else {
                    item = null;
                }
            }
            holder.mNestedRecyclerView = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getNanoTime() {
        if (ALLOW_THREAD_GAP_WORK) {
            return System.nanoTime();
        }
        return 0L;
    }

    /* loaded from: classes5.dex */
    public final class Recycler {
        static final int DEFAULT_CACHE_SIZE = 2;
        final ArrayList<ViewHolder> mAttachedScrap;
        final ArrayList<ViewHolder> mCachedViews;
        ArrayList<ViewHolder> mChangedScrap;
        RecycledViewPool mRecyclerPool;
        private int mRequestedCacheMax;
        private final List<ViewHolder> mUnmodifiableAttachedScrap;
        private ViewCacheExtension mViewCacheExtension;
        int mViewCacheMax;

        public Recycler() {
            ArrayList<ViewHolder> arrayList = new ArrayList<>();
            this.mAttachedScrap = arrayList;
            this.mChangedScrap = null;
            this.mCachedViews = new ArrayList<>();
            this.mUnmodifiableAttachedScrap = Collections.unmodifiableList(arrayList);
            this.mRequestedCacheMax = 2;
            this.mViewCacheMax = 2;
        }

        public void clear() {
            this.mAttachedScrap.clear();
            recycleAndClearCachedViews();
        }

        public void setViewCacheSize(int viewCount) {
            this.mRequestedCacheMax = viewCount;
            updateViewCacheSize();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void updateViewCacheSize() {
            int extraCache = RecyclerView.this.mLayout != null ? RecyclerView.this.mLayout.mPrefetchMaxCountObserved : 0;
            this.mViewCacheMax = this.mRequestedCacheMax + extraCache;
            for (int i = this.mCachedViews.size() - 1; i >= 0 && this.mCachedViews.size() > this.mViewCacheMax; i--) {
                recycleCachedViewAt(i);
            }
        }

        public List<ViewHolder> getScrapList() {
            return this.mUnmodifiableAttachedScrap;
        }

        boolean validateViewHolderForOffsetPosition(ViewHolder holder) {
            if (holder.isRemoved()) {
                return RecyclerView.this.mState.isPreLayout();
            }
            if (holder.mPosition < 0 || holder.mPosition >= RecyclerView.this.mAdapter.getItemCount()) {
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + holder);
            }
            if (!RecyclerView.this.mState.isPreLayout()) {
                int type = RecyclerView.this.mAdapter.getItemViewType(holder.mPosition);
                if (type != holder.getItemViewType()) {
                    return false;
                }
            }
            return !RecyclerView.this.mAdapter.hasStableIds() || holder.getItemId() == RecyclerView.this.mAdapter.getItemId(holder.mPosition);
        }

        private boolean tryBindViewHolderByDeadline(ViewHolder holder, int offsetPosition, int position, long deadlineNs) {
            holder.mOwnerRecyclerView = RecyclerView.this;
            int viewType = holder.getItemViewType();
            long startBindNs = RecyclerView.this.getNanoTime();
            if (deadlineNs != Long.MAX_VALUE && !this.mRecyclerPool.willBindInTime(viewType, startBindNs, deadlineNs)) {
                return false;
            }
            RecyclerView.this.mAdapter.bindViewHolder(holder, offsetPosition);
            long endBindNs = RecyclerView.this.getNanoTime();
            this.mRecyclerPool.factorInBindTime(holder.getItemViewType(), endBindNs - startBindNs);
            attachAccessibilityDelegate(holder.itemView);
            if (RecyclerView.this.mState.isPreLayout()) {
                holder.mPreLayoutPosition = position;
                return true;
            }
            return true;
        }

        public void bindViewToPosition(View view, int position) {
            LayoutParams rvLayoutParams;
            ViewHolder holder = RecyclerView.getChildViewHolderInt(view);
            if (holder == null) {
                throw new IllegalArgumentException("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter");
            }
            int offsetPosition = RecyclerView.this.mAdapterHelper.findPositionOffset(position);
            if (offsetPosition < 0 || offsetPosition >= RecyclerView.this.mAdapter.getItemCount()) {
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + position + "(offset:" + offsetPosition + ").state:" + RecyclerView.this.mState.getItemCount());
            }
            tryBindViewHolderByDeadline(holder, offsetPosition, position, Long.MAX_VALUE);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp == null) {
                rvLayoutParams = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                holder.itemView.setLayoutParams(rvLayoutParams);
            } else if (!RecyclerView.this.checkLayoutParams(lp)) {
                rvLayoutParams = (LayoutParams) RecyclerView.this.generateLayoutParams(lp);
                holder.itemView.setLayoutParams(rvLayoutParams);
            } else {
                rvLayoutParams = (LayoutParams) lp;
            }
            rvLayoutParams.mInsetsDirty = true;
            rvLayoutParams.mViewHolder = holder;
            rvLayoutParams.mPendingInvalidate = holder.itemView.getParent() == null;
        }

        public int convertPreLayoutPositionToPostLayout(int position) {
            if (position < 0 || position >= RecyclerView.this.mState.getItemCount()) {
                throw new IndexOutOfBoundsException("invalid position " + position + ". State item count is " + RecyclerView.this.mState.getItemCount());
            }
            if (!RecyclerView.this.mState.isPreLayout()) {
                return position;
            }
            return RecyclerView.this.mAdapterHelper.findPositionOffset(position);
        }

        public View getViewForPosition(int position) {
            return getViewForPosition(position, false);
        }

        View getViewForPosition(int position, boolean dryRun) {
            return tryGetViewHolderForPositionByDeadline(position, dryRun, Long.MAX_VALUE).itemView;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ViewHolder tryGetViewHolderForPositionByDeadline(int position, boolean dryRun, long deadlineNs) {
            boolean fromScrapOrHiddenOrCache;
            ViewHolder holder;
            LayoutParams rvLayoutParams;
            RecyclerView innerView;
            ViewCacheExtension viewCacheExtension;
            View view;
            if (position < 0 || position >= RecyclerView.this.mState.getItemCount()) {
                throw new IndexOutOfBoundsException("Invalid item position " + position + NavigationBarInflaterView.KEY_CODE_START + position + "). Item count:" + RecyclerView.this.mState.getItemCount());
            }
            boolean fromScrapOrHiddenOrCache2 = false;
            ViewHolder holder2 = null;
            boolean z = true;
            if (RecyclerView.this.mState.isPreLayout()) {
                holder2 = getChangedScrapViewForPosition(position);
                fromScrapOrHiddenOrCache2 = holder2 != null;
            }
            if (holder2 == null && (holder2 = getScrapOrHiddenOrCachedHolderForPosition(position, dryRun)) != null) {
                if (!validateViewHolderForOffsetPosition(holder2)) {
                    if (!dryRun) {
                        holder2.addFlags(4);
                        if (holder2.isScrap()) {
                            RecyclerView.this.removeDetachedView(holder2.itemView, false);
                            holder2.unScrap();
                        } else if (holder2.wasReturnedFromScrap()) {
                            holder2.clearReturnedFromScrapFlag();
                        }
                        recycleViewHolderInternal(holder2);
                    }
                    holder2 = null;
                } else {
                    fromScrapOrHiddenOrCache2 = true;
                }
            }
            if (holder2 != null) {
                fromScrapOrHiddenOrCache = fromScrapOrHiddenOrCache2;
                holder = holder2;
            } else {
                int offsetPosition = RecyclerView.this.mAdapterHelper.findPositionOffset(position);
                if (offsetPosition < 0 || offsetPosition >= RecyclerView.this.mAdapter.getItemCount()) {
                    throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + position + "(offset:" + offsetPosition + ").state:" + RecyclerView.this.mState.getItemCount());
                }
                int type = RecyclerView.this.mAdapter.getItemViewType(offsetPosition);
                if (RecyclerView.this.mAdapter.hasStableIds() && (holder2 = getScrapOrCachedViewForId(RecyclerView.this.mAdapter.getItemId(offsetPosition), type, dryRun)) != null) {
                    holder2.mPosition = offsetPosition;
                    fromScrapOrHiddenOrCache2 = true;
                }
                if (holder2 == null && (viewCacheExtension = this.mViewCacheExtension) != null && (view = viewCacheExtension.getViewForPositionAndType(this, position, type)) != null) {
                    holder2 = RecyclerView.this.getChildViewHolder(view);
                    if (holder2 == null) {
                        throw new IllegalArgumentException("getViewForPositionAndType returned a view which does not have a ViewHolder");
                    }
                    if (holder2.shouldIgnore()) {
                        throw new IllegalArgumentException("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view.");
                    }
                }
                if (holder2 == null && (holder2 = getRecycledViewPool().getRecycledView(type)) != null) {
                    holder2.resetInternal();
                    if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST) {
                        invalidateDisplayListInt(holder2);
                    }
                }
                if (holder2 != null) {
                    fromScrapOrHiddenOrCache = fromScrapOrHiddenOrCache2;
                    holder = holder2;
                } else {
                    long start = RecyclerView.this.getNanoTime();
                    if (deadlineNs == Long.MAX_VALUE || this.mRecyclerPool.willCreateInTime(type, start, deadlineNs)) {
                        ViewHolder holder3 = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, type);
                        if (RecyclerView.ALLOW_THREAD_GAP_WORK && (innerView = RecyclerView.findNestedRecyclerView(holder3.itemView)) != null) {
                            holder3.mNestedRecyclerView = new WeakReference<>(innerView);
                        }
                        long end = RecyclerView.this.getNanoTime();
                        this.mRecyclerPool.factorInCreateTime(type, end - start);
                        fromScrapOrHiddenOrCache = fromScrapOrHiddenOrCache2;
                        holder = holder3;
                    } else {
                        return null;
                    }
                }
            }
            if (fromScrapOrHiddenOrCache && !RecyclerView.this.mState.isPreLayout() && holder.hasAnyOfTheFlags(8192)) {
                holder.setFlags(0, 8192);
                if (RecyclerView.this.mState.mRunSimpleAnimations) {
                    int changeFlags = ItemAnimator.buildAdapterChangeFlagsForAnimations(holder);
                    ItemAnimator.ItemHolderInfo info = RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, holder, changeFlags | 4096, holder.getUnmodifiedPayloads());
                    RecyclerView.this.recordAnimationInfoIfBouncedHiddenView(holder, info);
                }
            }
            boolean bound = false;
            if (RecyclerView.this.mState.isPreLayout() && holder.isBound()) {
                holder.mPreLayoutPosition = position;
            } else if (!holder.isBound() || holder.needsUpdate() || holder.isInvalid()) {
                bound = tryBindViewHolderByDeadline(holder, RecyclerView.this.mAdapterHelper.findPositionOffset(position), position, deadlineNs);
            }
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp == null) {
                rvLayoutParams = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                holder.itemView.setLayoutParams(rvLayoutParams);
            } else if (!RecyclerView.this.checkLayoutParams(lp)) {
                rvLayoutParams = (LayoutParams) RecyclerView.this.generateLayoutParams(lp);
                holder.itemView.setLayoutParams(rvLayoutParams);
            } else {
                rvLayoutParams = (LayoutParams) lp;
            }
            rvLayoutParams.mViewHolder = holder;
            if (!fromScrapOrHiddenOrCache || !bound) {
                z = false;
            }
            rvLayoutParams.mPendingInvalidate = z;
            return holder;
        }

        private void attachAccessibilityDelegate(View itemView) {
            if (RecyclerView.this.isAccessibilityEnabled()) {
                if (itemView.getImportantForAccessibility() == 0) {
                    itemView.setImportantForAccessibility(1);
                }
                if (itemView.getAccessibilityDelegate() == null) {
                    itemView.setAccessibilityDelegate(RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
                }
            }
        }

        private void invalidateDisplayListInt(ViewHolder holder) {
            if (holder.itemView instanceof ViewGroup) {
                invalidateDisplayListInt((ViewGroup) holder.itemView, false);
            }
        }

        private void invalidateDisplayListInt(ViewGroup viewGroup, boolean invalidateThis) {
            for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
                View view = viewGroup.getChildAt(i);
                if (view instanceof ViewGroup) {
                    invalidateDisplayListInt((ViewGroup) view, true);
                }
            }
            if (!invalidateThis) {
                return;
            }
            if (viewGroup.getVisibility() == 4) {
                viewGroup.setVisibility(0);
                viewGroup.setVisibility(4);
                return;
            }
            int visibility = viewGroup.getVisibility();
            viewGroup.setVisibility(4);
            viewGroup.setVisibility(visibility);
        }

        public void recycleView(View view) {
            ViewHolder holder = RecyclerView.getChildViewHolderInt(view);
            if (holder.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(view, false);
            }
            if (holder.isScrap()) {
                holder.unScrap();
            } else if (holder.wasReturnedFromScrap()) {
                holder.clearReturnedFromScrapFlag();
            }
            recycleViewHolderInternal(holder);
        }

        void recycleViewInternal(View view) {
            recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(view));
        }

        void recycleAndClearCachedViews() {
            int count = this.mCachedViews.size();
            for (int i = count - 1; i >= 0; i--) {
                recycleCachedViewAt(i);
            }
            this.mCachedViews.clear();
            if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
            }
        }

        void recycleCachedViewAt(int cachedViewIndex) {
            ViewHolder viewHolder = this.mCachedViews.get(cachedViewIndex);
            addViewHolderToRecycledViewPool(viewHolder, true);
            this.mCachedViews.remove(cachedViewIndex);
        }

        void recycleViewHolderInternal(ViewHolder holder) {
            if (holder.isScrap() || holder.itemView.getParent() != null) {
                throw new IllegalArgumentException("Scrapped or attached views may not be recycled. isScrap:" + holder.isScrap() + " isAttached:" + (holder.itemView.getParent() != null));
            } else if (holder.isTmpDetached()) {
                throw new IllegalArgumentException("Tmp detached view should be removed from RecyclerView before it can be recycled: " + holder);
            } else {
                if (holder.shouldIgnore()) {
                    throw new IllegalArgumentException("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle.");
                }
                boolean transientStatePreventsRecycling = holder.doesTransientStatePreventRecycling();
                boolean forceRecycle = RecyclerView.this.mAdapter != null && transientStatePreventsRecycling && RecyclerView.this.mAdapter.onFailedToRecycleView(holder);
                boolean cached = false;
                boolean recycled = false;
                if (forceRecycle || holder.isRecyclable()) {
                    if (this.mViewCacheMax > 0 && !holder.hasAnyOfTheFlags(MetricsProto.MetricsEvent.DIALOG_SUPPORT_DISCLAIMER)) {
                        int cachedViewSize = this.mCachedViews.size();
                        if (cachedViewSize >= this.mViewCacheMax && cachedViewSize > 0) {
                            recycleCachedViewAt(0);
                            cachedViewSize--;
                        }
                        int targetCacheIndex = cachedViewSize;
                        if (RecyclerView.ALLOW_THREAD_GAP_WORK && cachedViewSize > 0 && !RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(holder.mPosition)) {
                            int cacheIndex = cachedViewSize - 1;
                            while (cacheIndex >= 0) {
                                int cachedPos = this.mCachedViews.get(cacheIndex).mPosition;
                                if (!RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(cachedPos)) {
                                    break;
                                }
                                cacheIndex--;
                            }
                            targetCacheIndex = cacheIndex + 1;
                        }
                        this.mCachedViews.add(targetCacheIndex, holder);
                        cached = true;
                    }
                    if (!cached) {
                        addViewHolderToRecycledViewPool(holder, true);
                        recycled = true;
                    }
                }
                RecyclerView.this.mViewInfoStore.removeViewHolder(holder);
                if (!cached && !recycled && transientStatePreventsRecycling) {
                    holder.mOwnerRecyclerView = null;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addViewHolderToRecycledViewPool(ViewHolder holder, boolean dispatchRecycled) {
            RecyclerView.clearNestedRecyclerViewIfNotNested(holder);
            holder.itemView.setAccessibilityDelegate(null);
            if (dispatchRecycled) {
                dispatchViewRecycled(holder);
            }
            holder.mOwnerRecyclerView = null;
            getRecycledViewPool().putRecycledView(holder);
        }

        void quickRecycleScrapView(View view) {
            ViewHolder holder = RecyclerView.getChildViewHolderInt(view);
            holder.mScrapContainer = null;
            holder.mInChangeScrap = false;
            holder.clearReturnedFromScrapFlag();
            recycleViewHolderInternal(holder);
        }

        void scrapView(View view) {
            ViewHolder holder = RecyclerView.getChildViewHolderInt(view);
            if (holder.hasAnyOfTheFlags(12) || !holder.isUpdated() || RecyclerView.this.canReuseUpdatedViewHolder(holder)) {
                if (holder.isInvalid() && !holder.isRemoved() && !RecyclerView.this.mAdapter.hasStableIds()) {
                    throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool.");
                }
                holder.setScrapContainer(this, false);
                this.mAttachedScrap.add(holder);
                return;
            }
            if (this.mChangedScrap == null) {
                this.mChangedScrap = new ArrayList<>();
            }
            holder.setScrapContainer(this, true);
            this.mChangedScrap.add(holder);
        }

        void unscrapView(ViewHolder holder) {
            if (holder.mInChangeScrap) {
                this.mChangedScrap.remove(holder);
            } else {
                this.mAttachedScrap.remove(holder);
            }
            holder.mScrapContainer = null;
            holder.mInChangeScrap = false;
            holder.clearReturnedFromScrapFlag();
        }

        int getScrapCount() {
            return this.mAttachedScrap.size();
        }

        View getScrapViewAt(int index) {
            return this.mAttachedScrap.get(index).itemView;
        }

        void clearScrap() {
            this.mAttachedScrap.clear();
            ArrayList<ViewHolder> arrayList = this.mChangedScrap;
            if (arrayList != null) {
                arrayList.clear();
            }
        }

        ViewHolder getChangedScrapViewForPosition(int position) {
            int changedScrapSize;
            int offsetPosition;
            ArrayList<ViewHolder> arrayList = this.mChangedScrap;
            if (arrayList == null || (changedScrapSize = arrayList.size()) == 0) {
                return null;
            }
            for (int i = 0; i < changedScrapSize; i++) {
                ViewHolder holder = this.mChangedScrap.get(i);
                if (!holder.wasReturnedFromScrap() && holder.getLayoutPosition() == position) {
                    holder.addFlags(32);
                    return holder;
                }
            }
            if (RecyclerView.this.mAdapter.hasStableIds() && (offsetPosition = RecyclerView.this.mAdapterHelper.findPositionOffset(position)) > 0 && offsetPosition < RecyclerView.this.mAdapter.getItemCount()) {
                long id = RecyclerView.this.mAdapter.getItemId(offsetPosition);
                for (int i2 = 0; i2 < changedScrapSize; i2++) {
                    ViewHolder holder2 = this.mChangedScrap.get(i2);
                    if (!holder2.wasReturnedFromScrap() && holder2.getItemId() == id) {
                        holder2.addFlags(32);
                        return holder2;
                    }
                }
            }
            return null;
        }

        ViewHolder getScrapOrHiddenOrCachedHolderForPosition(int position, boolean dryRun) {
            View view;
            int scrapCount = this.mAttachedScrap.size();
            for (int i = 0; i < scrapCount; i++) {
                ViewHolder holder = this.mAttachedScrap.get(i);
                if (!holder.wasReturnedFromScrap() && holder.getLayoutPosition() == position && !holder.isInvalid() && (RecyclerView.this.mState.mInPreLayout || !holder.isRemoved())) {
                    holder.addFlags(32);
                    return holder;
                }
            }
            if (!dryRun && (view = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(position)) != null) {
                ViewHolder vh = RecyclerView.getChildViewHolderInt(view);
                RecyclerView.this.mChildHelper.unhide(view);
                int layoutIndex = RecyclerView.this.mChildHelper.indexOfChild(view);
                if (layoutIndex == -1) {
                    throw new IllegalStateException("layout index should not be -1 after unhiding a view:" + vh);
                }
                RecyclerView.this.mChildHelper.detachViewFromParent(layoutIndex);
                scrapView(view);
                vh.addFlags(8224);
                return vh;
            }
            int cacheSize = this.mCachedViews.size();
            for (int i2 = 0; i2 < cacheSize; i2++) {
                ViewHolder holder2 = this.mCachedViews.get(i2);
                if (!holder2.isInvalid() && holder2.getLayoutPosition() == position) {
                    if (!dryRun) {
                        this.mCachedViews.remove(i2);
                    }
                    return holder2;
                }
            }
            return null;
        }

        ViewHolder getScrapOrCachedViewForId(long id, int type, boolean dryRun) {
            int count = this.mAttachedScrap.size();
            for (int i = count - 1; i >= 0; i--) {
                ViewHolder holder = this.mAttachedScrap.get(i);
                if (holder.getItemId() == id && !holder.wasReturnedFromScrap()) {
                    if (type == holder.getItemViewType()) {
                        holder.addFlags(32);
                        if (holder.isRemoved() && !RecyclerView.this.mState.isPreLayout()) {
                            holder.setFlags(2, 14);
                        }
                        return holder;
                    } else if (!dryRun) {
                        this.mAttachedScrap.remove(i);
                        RecyclerView.this.removeDetachedView(holder.itemView, false);
                        quickRecycleScrapView(holder.itemView);
                    }
                }
            }
            int cacheSize = this.mCachedViews.size();
            for (int i2 = cacheSize - 1; i2 >= 0; i2--) {
                ViewHolder holder2 = this.mCachedViews.get(i2);
                if (holder2.getItemId() == id) {
                    if (type == holder2.getItemViewType()) {
                        if (!dryRun) {
                            this.mCachedViews.remove(i2);
                        }
                        return holder2;
                    } else if (!dryRun) {
                        recycleCachedViewAt(i2);
                        return null;
                    }
                }
            }
            return null;
        }

        void dispatchViewRecycled(ViewHolder holder) {
            if (RecyclerView.this.mRecyclerListener != null) {
                RecyclerView.this.mRecyclerListener.onViewRecycled(holder);
            }
            if (RecyclerView.this.mAdapter != null) {
                RecyclerView.this.mAdapter.onViewRecycled(holder);
            }
            if (RecyclerView.this.mState != null) {
                RecyclerView.this.mViewInfoStore.removeViewHolder(holder);
            }
        }

        void onAdapterChanged(Adapter oldAdapter, Adapter newAdapter, boolean compatibleWithPrevious) {
            clear();
            getRecycledViewPool().onAdapterChanged(oldAdapter, newAdapter, compatibleWithPrevious);
        }

        void offsetPositionRecordsForMove(int from, int to) {
            int start;
            int end;
            int inBetweenOffset;
            if (from < to) {
                start = from;
                end = to;
                inBetweenOffset = -1;
            } else {
                start = to;
                end = from;
                inBetweenOffset = 1;
            }
            int cachedCount = this.mCachedViews.size();
            for (int i = 0; i < cachedCount; i++) {
                ViewHolder holder = this.mCachedViews.get(i);
                if (holder != null && holder.mPosition >= start && holder.mPosition <= end) {
                    if (holder.mPosition == from) {
                        holder.offsetPosition(to - from, false);
                    } else {
                        holder.offsetPosition(inBetweenOffset, false);
                    }
                }
            }
        }

        void offsetPositionRecordsForInsert(int insertedAt, int count) {
            int cachedCount = this.mCachedViews.size();
            for (int i = 0; i < cachedCount; i++) {
                ViewHolder holder = this.mCachedViews.get(i);
                if (holder != null && holder.mPosition >= insertedAt) {
                    holder.offsetPosition(count, true);
                }
            }
        }

        void offsetPositionRecordsForRemove(int removedFrom, int count, boolean applyToPreLayout) {
            int removedEnd = removedFrom + count;
            int cachedCount = this.mCachedViews.size();
            for (int i = cachedCount - 1; i >= 0; i--) {
                ViewHolder holder = this.mCachedViews.get(i);
                if (holder != null) {
                    if (holder.mPosition >= removedEnd) {
                        holder.offsetPosition(-count, applyToPreLayout);
                    } else if (holder.mPosition >= removedFrom) {
                        holder.addFlags(8);
                        recycleCachedViewAt(i);
                    }
                }
            }
        }

        void setViewCacheExtension(ViewCacheExtension extension) {
            this.mViewCacheExtension = extension;
        }

        void setRecycledViewPool(RecycledViewPool pool) {
            RecycledViewPool recycledViewPool = this.mRecyclerPool;
            if (recycledViewPool != null) {
                recycledViewPool.detach();
            }
            this.mRecyclerPool = pool;
            if (pool != null) {
                pool.attach(RecyclerView.this.getAdapter());
            }
        }

        RecycledViewPool getRecycledViewPool() {
            if (this.mRecyclerPool == null) {
                this.mRecyclerPool = new RecycledViewPool();
            }
            return this.mRecyclerPool;
        }

        void viewRangeUpdate(int positionStart, int itemCount) {
            int pos;
            int positionEnd = positionStart + itemCount;
            int cachedCount = this.mCachedViews.size();
            for (int i = cachedCount - 1; i >= 0; i--) {
                ViewHolder holder = this.mCachedViews.get(i);
                if (holder != null && (pos = holder.getLayoutPosition()) >= positionStart && pos < positionEnd) {
                    holder.addFlags(2);
                    recycleCachedViewAt(i);
                }
            }
        }

        void setAdapterPositionsAsUnknown() {
            int cachedCount = this.mCachedViews.size();
            for (int i = 0; i < cachedCount; i++) {
                ViewHolder holder = this.mCachedViews.get(i);
                if (holder != null) {
                    holder.addFlags(512);
                }
            }
        }

        void markKnownViewsInvalid() {
            if (RecyclerView.this.mAdapter != null && RecyclerView.this.mAdapter.hasStableIds()) {
                int cachedCount = this.mCachedViews.size();
                for (int i = 0; i < cachedCount; i++) {
                    ViewHolder holder = this.mCachedViews.get(i);
                    if (holder != null) {
                        holder.addFlags(6);
                        holder.addChangePayload(null);
                    }
                }
                return;
            }
            recycleAndClearCachedViews();
        }

        void clearOldPositions() {
            int cachedCount = this.mCachedViews.size();
            for (int i = 0; i < cachedCount; i++) {
                ViewHolder holder = this.mCachedViews.get(i);
                holder.clearOldPosition();
            }
            int scrapCount = this.mAttachedScrap.size();
            for (int i2 = 0; i2 < scrapCount; i2++) {
                this.mAttachedScrap.get(i2).clearOldPosition();
            }
            ArrayList<ViewHolder> arrayList = this.mChangedScrap;
            if (arrayList != null) {
                int changedScrapCount = arrayList.size();
                for (int i3 = 0; i3 < changedScrapCount; i3++) {
                    this.mChangedScrap.get(i3).clearOldPosition();
                }
            }
        }

        void markItemDecorInsetsDirty() {
            int cachedCount = this.mCachedViews.size();
            for (int i = 0; i < cachedCount; i++) {
                ViewHolder holder = this.mCachedViews.get(i);
                LayoutParams layoutParams = (LayoutParams) holder.itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.mInsetsDirty = true;
                }
            }
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class Adapter<VH extends ViewHolder> {
        private final AdapterDataObservable mObservable = new AdapterDataObservable();
        private boolean mHasStableIds = false;

        public abstract int getItemCount();

        public abstract void onBindViewHolder(VH vh, int i);

        public abstract VH onCreateViewHolder(ViewGroup viewGroup, int i);

        public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
            onBindViewHolder(holder, position);
        }

        public final VH createViewHolder(ViewGroup parent, int viewType) {
            Trace.beginSection(RecyclerView.TRACE_CREATE_VIEW_TAG);
            VH holder = onCreateViewHolder(parent, viewType);
            holder.mItemViewType = viewType;
            Trace.endSection();
            return holder;
        }

        public final void bindViewHolder(VH holder, int position) {
            holder.mPosition = position;
            if (hasStableIds()) {
                holder.mItemId = getItemId(position);
            }
            holder.setFlags(1, 519);
            Trace.beginSection(RecyclerView.TRACE_BIND_VIEW_TAG);
            onBindViewHolder(holder, position, holder.getUnmodifiedPayloads());
            holder.clearPayload();
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof LayoutParams) {
                ((LayoutParams) layoutParams).mInsetsDirty = true;
            }
            Trace.endSection();
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public void setHasStableIds(boolean hasStableIds) {
            if (hasObservers()) {
                throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
            }
            this.mHasStableIds = hasStableIds;
        }

        public long getItemId(int position) {
            return -1L;
        }

        public final boolean hasStableIds() {
            return this.mHasStableIds;
        }

        public void onViewRecycled(VH holder) {
        }

        public boolean onFailedToRecycleView(VH holder) {
            return false;
        }

        public void onViewAttachedToWindow(VH holder) {
        }

        public void onViewDetachedFromWindow(VH holder) {
        }

        public final boolean hasObservers() {
            return this.mObservable.hasObservers();
        }

        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            this.mObservable.registerObserver(observer);
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            this.mObservable.unregisterObserver(observer);
        }

        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        }

        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        }

        public final void notifyDataSetChanged() {
            this.mObservable.notifyChanged();
        }

        public final void notifyItemChanged(int position) {
            this.mObservable.notifyItemRangeChanged(position, 1);
        }

        public final void notifyItemChanged(int position, Object payload) {
            this.mObservable.notifyItemRangeChanged(position, 1, payload);
        }

        public final void notifyItemRangeChanged(int positionStart, int itemCount) {
            this.mObservable.notifyItemRangeChanged(positionStart, itemCount);
        }

        public final void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
            this.mObservable.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        public final void notifyItemInserted(int position) {
            this.mObservable.notifyItemRangeInserted(position, 1);
        }

        public final void notifyItemMoved(int fromPosition, int toPosition) {
            this.mObservable.notifyItemMoved(fromPosition, toPosition);
        }

        public final void notifyItemRangeInserted(int positionStart, int itemCount) {
            this.mObservable.notifyItemRangeInserted(positionStart, itemCount);
        }

        public final void notifyItemRemoved(int position) {
            this.mObservable.notifyItemRangeRemoved(position, 1);
        }

        public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
            this.mObservable.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    void dispatchChildDetached(View child) {
        ViewHolder viewHolder = getChildViewHolderInt(child);
        onChildDetachedFromWindow(child);
        Adapter adapter = this.mAdapter;
        if (adapter != null && viewHolder != null) {
            adapter.onViewDetachedFromWindow(viewHolder);
        }
        List<OnChildAttachStateChangeListener> list = this.mOnChildAttachStateListeners;
        if (list != null) {
            int cnt = list.size();
            for (int i = cnt - 1; i >= 0; i--) {
                this.mOnChildAttachStateListeners.get(i).onChildViewDetachedFromWindow(child);
            }
        }
    }

    void dispatchChildAttached(View child) {
        ViewHolder viewHolder = getChildViewHolderInt(child);
        onChildAttachedToWindow(child);
        Adapter adapter = this.mAdapter;
        if (adapter != null && viewHolder != null) {
            adapter.onViewAttachedToWindow(viewHolder);
        }
        List<OnChildAttachStateChangeListener> list = this.mOnChildAttachStateListeners;
        if (list != null) {
            int cnt = list.size();
            for (int i = cnt - 1; i >= 0; i--) {
                this.mOnChildAttachStateListeners.get(i).onChildViewAttachedToWindow(child);
            }
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class LayoutManager {
        ChildHelper mChildHelper;
        private int mHeight;
        private int mHeightMode;
        int mPrefetchMaxCountObserved;
        boolean mPrefetchMaxObservedInInitialPrefetch;
        RecyclerView mRecyclerView;
        SmoothScroller mSmoothScroller;
        private int mWidth;
        private int mWidthMode;
        boolean mRequestedSimpleAnimations = false;
        boolean mIsAttachedToWindow = false;
        boolean mAutoMeasure = false;
        private boolean mMeasurementCacheEnabled = true;
        private boolean mItemPrefetchEnabled = true;

        /* loaded from: classes5.dex */
        public interface LayoutPrefetchRegistry {
            void addPosition(int i, int i2);
        }

        /* loaded from: classes5.dex */
        public static class Properties {
            public int orientation;
            public boolean reverseLayout;
            public int spanCount;
            public boolean stackFromEnd;
        }

        public abstract LayoutParams generateDefaultLayoutParams();

        void setRecyclerView(RecyclerView recyclerView) {
            if (recyclerView == null) {
                this.mRecyclerView = null;
                this.mChildHelper = null;
                this.mWidth = 0;
                this.mHeight = 0;
            } else {
                this.mRecyclerView = recyclerView;
                this.mChildHelper = recyclerView.mChildHelper;
                this.mWidth = recyclerView.getWidth();
                this.mHeight = recyclerView.getHeight();
            }
            this.mWidthMode = 1073741824;
            this.mHeightMode = 1073741824;
        }

        void setMeasureSpecs(int wSpec, int hSpec) {
            this.mWidth = View.MeasureSpec.getSize(wSpec);
            int mode = View.MeasureSpec.getMode(wSpec);
            this.mWidthMode = mode;
            if (mode == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC) {
                this.mWidth = 0;
            }
            this.mHeight = View.MeasureSpec.getSize(hSpec);
            int mode2 = View.MeasureSpec.getMode(hSpec);
            this.mHeightMode = mode2;
            if (mode2 == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC) {
                this.mHeight = 0;
            }
        }

        void setMeasuredDimensionFromChildren(int widthSpec, int heightSpec) {
            int count = getChildCount();
            if (count == 0) {
                this.mRecyclerView.defaultOnMeasure(widthSpec, heightSpec);
                return;
            }
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                Rect bounds = this.mRecyclerView.mTempRect;
                getDecoratedBoundsWithMargins(child, bounds);
                if (bounds.left < minX) {
                    minX = bounds.left;
                }
                if (bounds.right > maxX) {
                    maxX = bounds.right;
                }
                if (bounds.top < minY) {
                    minY = bounds.top;
                }
                if (bounds.bottom > maxY) {
                    maxY = bounds.bottom;
                }
            }
            this.mRecyclerView.mTempRect.set(minX, minY, maxX, maxY);
            setMeasuredDimension(this.mRecyclerView.mTempRect, widthSpec, heightSpec);
        }

        public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
            int usedWidth = childrenBounds.width() + getPaddingLeft() + getPaddingRight();
            int usedHeight = childrenBounds.height() + getPaddingTop() + getPaddingBottom();
            int width = chooseSize(wSpec, usedWidth, getMinimumWidth());
            int height = chooseSize(hSpec, usedHeight, getMinimumHeight());
            setMeasuredDimension(width, height);
        }

        public void requestLayout() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.requestLayout();
            }
        }

        public void assertInLayoutOrScroll(String message) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.assertInLayoutOrScroll(message);
            }
        }

        public static int chooseSize(int spec, int desired, int min) {
            int mode = View.MeasureSpec.getMode(spec);
            int size = View.MeasureSpec.getSize(spec);
            switch (mode) {
                case Integer.MIN_VALUE:
                    return Math.min(size, Math.max(desired, min));
                case 1073741824:
                    return size;
                default:
                    return Math.max(desired, min);
            }
        }

        public void assertNotInLayoutOrScroll(String message) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.assertNotInLayoutOrScroll(message);
            }
        }

        public void setAutoMeasureEnabled(boolean enabled) {
            this.mAutoMeasure = enabled;
        }

        public boolean isAutoMeasureEnabled() {
            return this.mAutoMeasure;
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public final void setItemPrefetchEnabled(boolean enabled) {
            if (enabled != this.mItemPrefetchEnabled) {
                this.mItemPrefetchEnabled = enabled;
                this.mPrefetchMaxCountObserved = 0;
                RecyclerView recyclerView = this.mRecyclerView;
                if (recyclerView != null) {
                    recyclerView.mRecycler.updateViewCacheSize();
                }
            }
        }

        public final boolean isItemPrefetchEnabled() {
            return this.mItemPrefetchEnabled;
        }

        public void collectAdjacentPrefetchPositions(int dx, int dy, State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        }

        public void collectInitialPrefetchPositions(int adapterItemCount, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        }

        void dispatchAttachedToWindow(RecyclerView view) {
            this.mIsAttachedToWindow = true;
            onAttachedToWindow(view);
        }

        void dispatchDetachedFromWindow(RecyclerView view, Recycler recycler) {
            this.mIsAttachedToWindow = false;
            onDetachedFromWindow(view, recycler);
        }

        public boolean isAttachedToWindow() {
            return this.mIsAttachedToWindow;
        }

        public void postOnAnimation(Runnable action) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.postOnAnimation(action);
            }
        }

        public boolean removeCallbacks(Runnable action) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.removeCallbacks(action);
            }
            return false;
        }

        public void onAttachedToWindow(RecyclerView view) {
        }

        @Deprecated
        public void onDetachedFromWindow(RecyclerView view) {
        }

        public void onDetachedFromWindow(RecyclerView view, Recycler recycler) {
            onDetachedFromWindow(view);
        }

        public boolean getClipToPadding() {
            RecyclerView recyclerView = this.mRecyclerView;
            return recyclerView != null && recyclerView.mClipToPadding;
        }

        public void onLayoutChildren(Recycler recycler, State state) {
            Log.m110e(RecyclerView.TAG, "You must override onLayoutChildren(Recycler recycler, State state) ");
        }

        public void onLayoutCompleted(State state) {
        }

        public boolean checkLayoutParams(LayoutParams lp) {
            return lp != null;
        }

        public LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
            if (lp instanceof LayoutParams) {
                return new LayoutParams((LayoutParams) lp);
            }
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
            }
            return new LayoutParams(lp);
        }

        public LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
            return new LayoutParams(c, attrs);
        }

        public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
            return 0;
        }

        public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
            return 0;
        }

        public boolean canScrollHorizontally() {
            return false;
        }

        public boolean canScrollVertically() {
            return false;
        }

        public void scrollToPosition(int position) {
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
            Log.m110e(RecyclerView.TAG, "You must override smoothScrollToPosition to support smooth scrolling");
        }

        public void startSmoothScroll(SmoothScroller smoothScroller) {
            SmoothScroller smoothScroller2 = this.mSmoothScroller;
            if (smoothScroller2 != null && smoothScroller != smoothScroller2 && smoothScroller2.isRunning()) {
                this.mSmoothScroller.stop();
            }
            this.mSmoothScroller = smoothScroller;
            smoothScroller.start(this.mRecyclerView, this);
        }

        public boolean isSmoothScrolling() {
            SmoothScroller smoothScroller = this.mSmoothScroller;
            return smoothScroller != null && smoothScroller.isRunning();
        }

        public int getLayoutDirection() {
            return this.mRecyclerView.getLayoutDirection();
        }

        public void endAnimation(View view) {
            if (this.mRecyclerView.mItemAnimator != null) {
                this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(view));
            }
        }

        public void addDisappearingView(View child) {
            addDisappearingView(child, -1);
        }

        public void addDisappearingView(View child, int index) {
            addViewInt(child, index, true);
        }

        public void addView(View child) {
            addView(child, -1);
        }

        public void addView(View child, int index) {
            addViewInt(child, index, false);
        }

        private void addViewInt(View child, int index, boolean disappearing) {
            ViewHolder holder = RecyclerView.getChildViewHolderInt(child);
            if (disappearing || holder.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(holder);
            } else {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(holder);
            }
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (holder.wasReturnedFromScrap() || holder.isScrap()) {
                if (holder.isScrap()) {
                    holder.unScrap();
                } else {
                    holder.clearReturnedFromScrapFlag();
                }
                this.mChildHelper.attachViewToParent(child, index, child.getLayoutParams(), false);
            } else if (child.getParent() == this.mRecyclerView) {
                int currentIndex = this.mChildHelper.indexOfChild(child);
                if (index == -1) {
                    index = this.mChildHelper.getChildCount();
                }
                if (currentIndex == -1) {
                    throw new IllegalStateException("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:" + this.mRecyclerView.indexOfChild(child));
                }
                if (currentIndex != index) {
                    this.mRecyclerView.mLayout.moveView(currentIndex, index);
                }
            } else {
                this.mChildHelper.addView(child, index, false);
                lp.mInsetsDirty = true;
                SmoothScroller smoothScroller = this.mSmoothScroller;
                if (smoothScroller != null && smoothScroller.isRunning()) {
                    this.mSmoothScroller.onChildAttachedToWindow(child);
                }
            }
            if (lp.mPendingInvalidate) {
                holder.itemView.invalidate();
                lp.mPendingInvalidate = false;
            }
        }

        public void removeView(View child) {
            this.mChildHelper.removeView(child);
        }

        public void removeViewAt(int index) {
            View child = getChildAt(index);
            if (child != null) {
                this.mChildHelper.removeViewAt(index);
            }
        }

        public void removeAllViews() {
            int childCount = getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                this.mChildHelper.removeViewAt(i);
            }
        }

        public int getBaseline() {
            return -1;
        }

        public int getPosition(View view) {
            return ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        }

        public int getItemViewType(View view) {
            return RecyclerView.getChildViewHolderInt(view).getItemViewType();
        }

        public View findContainingItemView(View view) {
            View found;
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null || (found = recyclerView.findContainingItemView(view)) == null || this.mChildHelper.isHidden(found)) {
                return null;
            }
            return found;
        }

        public View findViewByPosition(int position) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                ViewHolder vh = RecyclerView.getChildViewHolderInt(child);
                if (vh != null && vh.getLayoutPosition() == position && !vh.shouldIgnore() && (this.mRecyclerView.mState.isPreLayout() || !vh.isRemoved())) {
                    return child;
                }
            }
            return null;
        }

        public void detachView(View child) {
            int ind = this.mChildHelper.indexOfChild(child);
            if (ind >= 0) {
                detachViewInternal(ind, child);
            }
        }

        public void detachViewAt(int index) {
            detachViewInternal(index, getChildAt(index));
        }

        private void detachViewInternal(int index, View view) {
            this.mChildHelper.detachViewFromParent(index);
        }

        public void attachView(View child, int index, LayoutParams lp) {
            ViewHolder vh = RecyclerView.getChildViewHolderInt(child);
            if (vh.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(vh);
            } else {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(vh);
            }
            this.mChildHelper.attachViewToParent(child, index, lp, vh.isRemoved());
        }

        public void attachView(View child, int index) {
            attachView(child, index, (LayoutParams) child.getLayoutParams());
        }

        public void attachView(View child) {
            attachView(child, -1);
        }

        public void removeDetachedView(View child) {
            this.mRecyclerView.removeDetachedView(child, false);
        }

        public void moveView(int fromIndex, int toIndex) {
            View view = getChildAt(fromIndex);
            if (view == null) {
                throw new IllegalArgumentException("Cannot move a child from non-existing index:" + fromIndex);
            }
            detachViewAt(fromIndex);
            attachView(view, toIndex);
        }

        public void detachAndScrapView(View child, Recycler recycler) {
            int index = this.mChildHelper.indexOfChild(child);
            scrapOrRecycleView(recycler, index, child);
        }

        public void detachAndScrapViewAt(int index, Recycler recycler) {
            View child = getChildAt(index);
            scrapOrRecycleView(recycler, index, child);
        }

        public void removeAndRecycleView(View child, Recycler recycler) {
            removeView(child);
            recycler.recycleView(child);
        }

        public void removeAndRecycleViewAt(int index, Recycler recycler) {
            View view = getChildAt(index);
            removeViewAt(index);
            recycler.recycleView(view);
        }

        public int getChildCount() {
            ChildHelper childHelper = this.mChildHelper;
            if (childHelper != null) {
                return childHelper.getChildCount();
            }
            return 0;
        }

        public View getChildAt(int index) {
            ChildHelper childHelper = this.mChildHelper;
            if (childHelper != null) {
                return childHelper.getChildAt(index);
            }
            return null;
        }

        public int getWidthMode() {
            return this.mWidthMode;
        }

        public int getHeightMode() {
            return this.mHeightMode;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public int getPaddingLeft() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.getPaddingLeft();
            }
            return 0;
        }

        public int getPaddingTop() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.getPaddingTop();
            }
            return 0;
        }

        public int getPaddingRight() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.getPaddingRight();
            }
            return 0;
        }

        public int getPaddingBottom() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.getPaddingBottom();
            }
            return 0;
        }

        public int getPaddingStart() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.getPaddingStart();
            }
            return 0;
        }

        public int getPaddingEnd() {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                return recyclerView.getPaddingEnd();
            }
            return 0;
        }

        public boolean isFocused() {
            RecyclerView recyclerView = this.mRecyclerView;
            return recyclerView != null && recyclerView.isFocused();
        }

        public boolean hasFocus() {
            RecyclerView recyclerView = this.mRecyclerView;
            return recyclerView != null && recyclerView.hasFocus();
        }

        public View getFocusedChild() {
            View focused;
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null || (focused = recyclerView.getFocusedChild()) == null || this.mChildHelper.isHidden(focused)) {
                return null;
            }
            return focused;
        }

        public int getItemCount() {
            RecyclerView recyclerView = this.mRecyclerView;
            Adapter a = recyclerView != null ? recyclerView.getAdapter() : null;
            if (a != null) {
                return a.getItemCount();
            }
            return 0;
        }

        public void offsetChildrenHorizontal(int dx) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.offsetChildrenHorizontal(dx);
            }
        }

        public void offsetChildrenVertical(int dy) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.offsetChildrenVertical(dy);
            }
        }

        public void ignoreView(View view) {
            ViewParent parent = view.getParent();
            RecyclerView recyclerView = this.mRecyclerView;
            if (parent != recyclerView || recyclerView.indexOfChild(view) == -1) {
                throw new IllegalArgumentException("View should be fully attached to be ignored");
            }
            ViewHolder vh = RecyclerView.getChildViewHolderInt(view);
            vh.addFlags(128);
            this.mRecyclerView.mViewInfoStore.removeViewHolder(vh);
        }

        public void stopIgnoringView(View view) {
            ViewHolder vh = RecyclerView.getChildViewHolderInt(view);
            vh.stopIgnoring();
            vh.resetInternal();
            vh.addFlags(4);
        }

        public void detachAndScrapAttachedViews(Recycler recycler) {
            int childCount = getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                View v = getChildAt(i);
                scrapOrRecycleView(recycler, i, v);
            }
        }

        private void scrapOrRecycleView(Recycler recycler, int index, View view) {
            ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(view);
            if (viewHolder.shouldIgnore()) {
                return;
            }
            if (viewHolder.isInvalid() && !viewHolder.isRemoved() && !this.mRecyclerView.mAdapter.hasStableIds()) {
                removeViewAt(index);
                recycler.recycleViewHolderInternal(viewHolder);
                return;
            }
            detachViewAt(index);
            recycler.scrapView(view);
            this.mRecyclerView.mViewInfoStore.onViewDetached(viewHolder);
        }

        void removeAndRecycleScrapInt(Recycler recycler) {
            int scrapCount = recycler.getScrapCount();
            for (int i = scrapCount - 1; i >= 0; i--) {
                View scrap = recycler.getScrapViewAt(i);
                ViewHolder vh = RecyclerView.getChildViewHolderInt(scrap);
                if (!vh.shouldIgnore()) {
                    vh.setIsRecyclable(false);
                    if (vh.isTmpDetached()) {
                        this.mRecyclerView.removeDetachedView(scrap, false);
                    }
                    if (this.mRecyclerView.mItemAnimator != null) {
                        this.mRecyclerView.mItemAnimator.endAnimation(vh);
                    }
                    vh.setIsRecyclable(true);
                    recycler.quickRecycleScrapView(scrap);
                }
            }
            recycler.clearScrap();
            if (scrapCount > 0) {
                this.mRecyclerView.invalidate();
            }
        }

        public void measureChild(View child, int widthUsed, int heightUsed) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            Rect insets = this.mRecyclerView.getItemDecorInsetsForChild(child);
            int widthUsed2 = widthUsed + insets.left + insets.right;
            int heightUsed2 = heightUsed + insets.top + insets.bottom;
            int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + widthUsed2, lp.width, canScrollHorizontally());
            int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + heightUsed2, lp.height, canScrollVertically());
            if (shouldMeasureChild(child, widthSpec, heightSpec, lp)) {
                child.measure(widthSpec, heightSpec);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean shouldReMeasureChild(View child, int widthSpec, int heightSpec, LayoutParams lp) {
            return (this.mMeasurementCacheEnabled && isMeasurementUpToDate(child.getMeasuredWidth(), widthSpec, lp.width) && isMeasurementUpToDate(child.getMeasuredHeight(), heightSpec, lp.height)) ? false : true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean shouldMeasureChild(View child, int widthSpec, int heightSpec, LayoutParams lp) {
            return (!child.isLayoutRequested() && this.mMeasurementCacheEnabled && isMeasurementUpToDate(child.getWidth(), widthSpec, lp.width) && isMeasurementUpToDate(child.getHeight(), heightSpec, lp.height)) ? false : true;
        }

        public boolean isMeasurementCacheEnabled() {
            return this.mMeasurementCacheEnabled;
        }

        public void setMeasurementCacheEnabled(boolean measurementCacheEnabled) {
            this.mMeasurementCacheEnabled = measurementCacheEnabled;
        }

        private static boolean isMeasurementUpToDate(int childSize, int spec, int dimension) {
            int specMode = View.MeasureSpec.getMode(spec);
            int specSize = View.MeasureSpec.getSize(spec);
            if (dimension > 0 && childSize != dimension) {
                return false;
            }
            switch (specMode) {
                case Integer.MIN_VALUE:
                    if (specSize < childSize) {
                        return false;
                    }
                    return true;
                case 0:
                    return true;
                case 1073741824:
                    if (specSize != childSize) {
                        return false;
                    }
                    return true;
                default:
                    return false;
            }
        }

        public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            Rect insets = this.mRecyclerView.getItemDecorInsetsForChild(child);
            int widthUsed2 = widthUsed + insets.left + insets.right;
            int heightUsed2 = heightUsed + insets.top + insets.bottom;
            int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed2, lp.width, canScrollHorizontally());
            int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin + heightUsed2, lp.height, canScrollVertically());
            if (shouldMeasureChild(child, widthSpec, heightSpec, lp)) {
                child.measure(widthSpec, heightSpec);
            }
        }

        @Deprecated
        public static int getChildMeasureSpec(int parentSize, int padding, int childDimension, boolean canScroll) {
            int size = Math.max(0, parentSize - padding);
            int resultSize = 0;
            int resultMode = 0;
            if (canScroll) {
                if (childDimension >= 0) {
                    resultSize = childDimension;
                    resultMode = 1073741824;
                } else {
                    resultSize = 0;
                    resultMode = 0;
                }
            } else if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = 1073741824;
            } else if (childDimension == -1) {
                resultSize = size;
                resultMode = 1073741824;
            } else if (childDimension == -2) {
                resultSize = size;
                resultMode = Integer.MIN_VALUE;
            }
            return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode);
        }

        public static int getChildMeasureSpec(int parentSize, int parentMode, int padding, int childDimension, boolean canScroll) {
            int size = Math.max(0, parentSize - padding);
            int resultSize = 0;
            int resultMode = 0;
            if (canScroll) {
                if (childDimension >= 0) {
                    resultSize = childDimension;
                    resultMode = 1073741824;
                } else if (childDimension == -1) {
                    switch (parentMode) {
                        case Integer.MIN_VALUE:
                        case 1073741824:
                            resultSize = size;
                            resultMode = parentMode;
                            break;
                        case 0:
                            resultSize = 0;
                            resultMode = 0;
                            break;
                    }
                } else if (childDimension == -2) {
                    resultSize = 0;
                    resultMode = 0;
                }
            } else if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = 1073741824;
            } else if (childDimension == -1) {
                resultSize = size;
                resultMode = parentMode;
            } else if (childDimension == -2) {
                resultSize = size;
                resultMode = (parentMode == Integer.MIN_VALUE || parentMode == 1073741824) ? Integer.MIN_VALUE : 0;
            }
            return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode);
        }

        public int getDecoratedMeasuredWidth(View child) {
            Rect insets = ((LayoutParams) child.getLayoutParams()).mDecorInsets;
            return child.getMeasuredWidth() + insets.left + insets.right;
        }

        public int getDecoratedMeasuredHeight(View child) {
            Rect insets = ((LayoutParams) child.getLayoutParams()).mDecorInsets;
            return child.getMeasuredHeight() + insets.top + insets.bottom;
        }

        public void layoutDecorated(View child, int left, int top, int right, int bottom) {
            Rect insets = ((LayoutParams) child.getLayoutParams()).mDecorInsets;
            child.layout(insets.left + left, insets.top + top, right - insets.right, bottom - insets.bottom);
        }

        public void layoutDecoratedWithMargins(View child, int left, int top, int right, int bottom) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            Rect insets = lp.mDecorInsets;
            child.layout(insets.left + left + lp.leftMargin, insets.top + top + lp.topMargin, (right - insets.right) - lp.rightMargin, (bottom - insets.bottom) - lp.bottomMargin);
        }

        public void getTransformedBoundingBox(View child, boolean includeDecorInsets, Rect out) {
            Matrix childMatrix;
            if (includeDecorInsets) {
                Rect insets = ((LayoutParams) child.getLayoutParams()).mDecorInsets;
                out.set(-insets.left, -insets.top, child.getWidth() + insets.right, child.getHeight() + insets.bottom);
            } else {
                out.set(0, 0, child.getWidth(), child.getHeight());
            }
            if (this.mRecyclerView != null && (childMatrix = child.getMatrix()) != null && !childMatrix.isIdentity()) {
                RectF tempRectF = this.mRecyclerView.mTempRectF;
                tempRectF.set(out);
                childMatrix.mapRect(tempRectF);
                out.set((int) Math.floor(tempRectF.left), (int) Math.floor(tempRectF.top), (int) Math.ceil(tempRectF.right), (int) Math.ceil(tempRectF.bottom));
            }
            out.offset(child.getLeft(), child.getTop());
        }

        public void getDecoratedBoundsWithMargins(View view, Rect outBounds) {
            RecyclerView.getDecoratedBoundsWithMarginsInt(view, outBounds);
        }

        public int getDecoratedLeft(View child) {
            return child.getLeft() - getLeftDecorationWidth(child);
        }

        public int getDecoratedTop(View child) {
            return child.getTop() - getTopDecorationHeight(child);
        }

        public int getDecoratedRight(View child) {
            return child.getRight() + getRightDecorationWidth(child);
        }

        public int getDecoratedBottom(View child) {
            return child.getBottom() + getBottomDecorationHeight(child);
        }

        public void calculateItemDecorationsForChild(View child, Rect outRect) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null) {
                outRect.set(0, 0, 0, 0);
                return;
            }
            Rect insets = recyclerView.getItemDecorInsetsForChild(child);
            outRect.set(insets);
        }

        public int getTopDecorationHeight(View child) {
            return ((LayoutParams) child.getLayoutParams()).mDecorInsets.top;
        }

        public int getBottomDecorationHeight(View child) {
            return ((LayoutParams) child.getLayoutParams()).mDecorInsets.bottom;
        }

        public int getLeftDecorationWidth(View child) {
            return ((LayoutParams) child.getLayoutParams()).mDecorInsets.left;
        }

        public int getRightDecorationWidth(View child) {
            return ((LayoutParams) child.getLayoutParams()).mDecorInsets.right;
        }

        public View onFocusSearchFailed(View focused, int direction, Recycler recycler, State state) {
            return null;
        }

        public View onInterceptFocusSearch(View focused, int direction) {
            return null;
        }

        public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate) {
            int dx;
            int parentLeft = getPaddingLeft();
            int parentTop = getPaddingTop();
            int parentRight = getWidth() - getPaddingRight();
            int parentBottom = getHeight() - getPaddingBottom();
            int childLeft = (child.getLeft() + rect.left) - child.getScrollX();
            int childTop = (child.getTop() + rect.top) - child.getScrollY();
            int childRight = rect.width() + childLeft;
            int childBottom = rect.height() + childTop;
            int offScreenLeft = Math.min(0, childLeft - parentLeft);
            int offScreenTop = Math.min(0, childTop - parentTop);
            int offScreenRight = Math.max(0, childRight - parentRight);
            int offScreenBottom = Math.max(0, childBottom - parentBottom);
            if (getLayoutDirection() == 1) {
                dx = offScreenRight != 0 ? offScreenRight : Math.max(offScreenLeft, childRight - parentRight);
            } else {
                dx = offScreenLeft != 0 ? offScreenLeft : Math.min(childLeft - parentLeft, offScreenRight);
            }
            int dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);
            if (dx != 0 || dy != 0) {
                if (immediate) {
                    parent.scrollBy(dx, dy);
                    return true;
                }
                parent.smoothScrollBy(dx, dy);
                return true;
            }
            return false;
        }

        @Deprecated
        public boolean onRequestChildFocus(RecyclerView parent, View child, View focused) {
            return isSmoothScrolling() || parent.isComputingLayout();
        }

        public boolean onRequestChildFocus(RecyclerView parent, State state, View child, View focused) {
            return onRequestChildFocus(parent, child, focused);
        }

        public void onAdapterChanged(Adapter oldAdapter, Adapter newAdapter) {
        }

        public boolean onAddFocusables(RecyclerView recyclerView, ArrayList<View> views, int direction, int focusableMode) {
            return false;
        }

        public void onItemsChanged(RecyclerView recyclerView) {
        }

        public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        }

        public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        }

        public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        }

        public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
            onItemsUpdated(recyclerView, positionStart, itemCount);
        }

        public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        }

        public int computeHorizontalScrollExtent(State state) {
            return 0;
        }

        public int computeHorizontalScrollOffset(State state) {
            return 0;
        }

        public int computeHorizontalScrollRange(State state) {
            return 0;
        }

        public int computeVerticalScrollExtent(State state) {
            return 0;
        }

        public int computeVerticalScrollOffset(State state) {
            return 0;
        }

        public int computeVerticalScrollRange(State state) {
            return 0;
        }

        public void onMeasure(Recycler recycler, State state, int widthSpec, int heightSpec) {
            this.mRecyclerView.defaultOnMeasure(widthSpec, heightSpec);
        }

        public void setMeasuredDimension(int widthSize, int heightSize) {
            this.mRecyclerView.setMeasuredDimension(widthSize, heightSize);
        }

        public int getMinimumWidth() {
            return this.mRecyclerView.getMinimumWidth();
        }

        public int getMinimumHeight() {
            return this.mRecyclerView.getMinimumHeight();
        }

        public Parcelable onSaveInstanceState() {
            return null;
        }

        public void onRestoreInstanceState(Parcelable state) {
        }

        void stopSmoothScroller() {
            SmoothScroller smoothScroller = this.mSmoothScroller;
            if (smoothScroller != null) {
                smoothScroller.stop();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onSmoothScrollerStopped(SmoothScroller smoothScroller) {
            if (this.mSmoothScroller == smoothScroller) {
                this.mSmoothScroller = null;
            }
        }

        public void onScrollStateChanged(int state) {
        }

        public void removeAndRecycleAllViews(Recycler recycler) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View view = getChildAt(i);
                if (!RecyclerView.getChildViewHolderInt(view).shouldIgnore()) {
                    removeAndRecycleViewAt(i, recycler);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, info);
        }

        public void onInitializeAccessibilityNodeInfo(Recycler recycler, State state, AccessibilityNodeInfo info) {
            if (this.mRecyclerView.canScrollVertically(-1) || this.mRecyclerView.canScrollHorizontally(-1)) {
                info.addAction(8192);
                info.setScrollable(true);
            }
            if (this.mRecyclerView.canScrollVertically(1) || this.mRecyclerView.canScrollHorizontally(1)) {
                info.addAction(4096);
                info.setScrollable(true);
            }
            AccessibilityNodeInfo.CollectionInfo collectionInfo = AccessibilityNodeInfo.CollectionInfo.obtain(getRowCountForAccessibility(recycler, state), getColumnCountForAccessibility(recycler, state), isLayoutHierarchical(recycler, state), getSelectionModeForAccessibility(recycler, state));
            info.setCollectionInfo(collectionInfo);
        }

        public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
            onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, event);
        }

        public void onInitializeAccessibilityEvent(Recycler recycler, State state, AccessibilityEvent event) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null || event == null) {
                return;
            }
            boolean z = true;
            if (!recyclerView.canScrollVertically(1) && !this.mRecyclerView.canScrollVertically(-1) && !this.mRecyclerView.canScrollHorizontally(-1) && !this.mRecyclerView.canScrollHorizontally(1)) {
                z = false;
            }
            event.setScrollable(z);
            if (this.mRecyclerView.mAdapter != null) {
                event.setItemCount(this.mRecyclerView.mAdapter.getItemCount());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onInitializeAccessibilityNodeInfoForItem(View host, AccessibilityNodeInfo info) {
            ViewHolder vh = RecyclerView.getChildViewHolderInt(host);
            if (vh != null && !vh.isRemoved() && !this.mChildHelper.isHidden(vh.itemView)) {
                onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, host, info);
            }
        }

        public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View host, AccessibilityNodeInfo info) {
            int rowIndexGuess = canScrollVertically() ? getPosition(host) : 0;
            int columnIndexGuess = canScrollHorizontally() ? getPosition(host) : 0;
            AccessibilityNodeInfo.CollectionItemInfo itemInfo = AccessibilityNodeInfo.CollectionItemInfo.obtain(rowIndexGuess, 1, columnIndexGuess, 1, false, false);
            info.setCollectionItemInfo(itemInfo);
        }

        public void requestSimpleAnimationsInNextLayout() {
            this.mRequestedSimpleAnimations = true;
        }

        public int getSelectionModeForAccessibility(Recycler recycler, State state) {
            return 0;
        }

        public int getRowCountForAccessibility(Recycler recycler, State state) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null || recyclerView.mAdapter == null || !canScrollVertically()) {
                return 1;
            }
            return this.mRecyclerView.mAdapter.getItemCount();
        }

        public int getColumnCountForAccessibility(Recycler recycler, State state) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null || recyclerView.mAdapter == null || !canScrollHorizontally()) {
                return 1;
            }
            return this.mRecyclerView.mAdapter.getItemCount();
        }

        public boolean isLayoutHierarchical(Recycler recycler, State state) {
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean performAccessibilityAction(int action, Bundle args) {
            return performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, action, args);
        }

        public boolean performAccessibilityAction(Recycler recycler, State state, int action, Bundle args) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView == null) {
                return false;
            }
            int vScroll = 0;
            int hScroll = 0;
            switch (action) {
                case 4096:
                    if (recyclerView.canScrollVertically(1)) {
                        vScroll = (getHeight() - getPaddingTop()) - getPaddingBottom();
                    }
                    if (this.mRecyclerView.canScrollHorizontally(1)) {
                        hScroll = (getWidth() - getPaddingLeft()) - getPaddingRight();
                        break;
                    }
                    break;
                case 8192:
                    if (recyclerView.canScrollVertically(-1)) {
                        vScroll = -((getHeight() - getPaddingTop()) - getPaddingBottom());
                    }
                    if (this.mRecyclerView.canScrollHorizontally(-1)) {
                        hScroll = -((getWidth() - getPaddingLeft()) - getPaddingRight());
                        break;
                    }
                    break;
            }
            if (vScroll == 0 && hScroll == 0) {
                return false;
            }
            this.mRecyclerView.smoothScrollBy(hScroll, vScroll);
            return true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean performAccessibilityActionForItem(View view, int action, Bundle args) {
            return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, view, action, args);
        }

        public boolean performAccessibilityActionForItem(Recycler recycler, State state, View view, int action, Bundle args) {
            return false;
        }

        public static Properties getProperties(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            Properties properties = new Properties();
            TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.RecyclerView, defStyleAttr, defStyleRes);
            properties.orientation = a.getInt(0, 1);
            properties.spanCount = a.getInt(4, 1);
            properties.reverseLayout = a.getBoolean(3, false);
            properties.stackFromEnd = a.getBoolean(5, false);
            a.recycle();
            return properties;
        }

        void setExactMeasureSpecsFrom(RecyclerView recyclerView) {
            setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(recyclerView.getHeight(), 1073741824));
        }

        boolean shouldMeasureTwice() {
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean hasFlexibleChildInBothOrientations() {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                ViewGroup.LayoutParams lp = child.getLayoutParams();
                if (lp.width < 0 && lp.height < 0) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class ItemDecoration {
        public void onDraw(Canvas c, RecyclerView parent, State state) {
            onDraw(c, parent);
        }

        @Deprecated
        public void onDraw(Canvas c, RecyclerView parent) {
        }

        public void onDrawOver(Canvas c, RecyclerView parent, State state) {
            onDrawOver(c, parent);
        }

        @Deprecated
        public void onDrawOver(Canvas c, RecyclerView parent) {
        }

        @Deprecated
        public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
            outRect.set(0, 0, 0, 0);
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            getItemOffsets(outRect, ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition(), parent);
        }
    }

    /* loaded from: classes5.dex */
    public static class SimpleOnItemTouchListener implements OnItemTouchListener {
        @Override // com.android.internal.widget.RecyclerView.OnItemTouchListener
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return false;
        }

        @Override // com.android.internal.widget.RecyclerView.OnItemTouchListener
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override // com.android.internal.widget.RecyclerView.OnItemTouchListener
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class OnScrollListener {
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class ViewHolder {
        static final int FLAG_ADAPTER_FULLUPDATE = 1024;
        static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
        static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
        static final int FLAG_BOUND = 1;
        static final int FLAG_IGNORE = 128;
        static final int FLAG_INVALID = 4;
        static final int FLAG_MOVED = 2048;
        static final int FLAG_NOT_RECYCLABLE = 16;
        static final int FLAG_REMOVED = 8;
        static final int FLAG_RETURNED_FROM_SCRAP = 32;
        static final int FLAG_TMP_DETACHED = 256;
        static final int FLAG_UPDATE = 2;
        private static final List<Object> FULLUPDATE_PAYLOADS = Collections.EMPTY_LIST;
        static final int PENDING_ACCESSIBILITY_STATE_NOT_SET = -1;
        public final View itemView;
        private int mFlags;
        WeakReference<RecyclerView> mNestedRecyclerView;
        RecyclerView mOwnerRecyclerView;
        int mPosition = -1;
        int mOldPosition = -1;
        long mItemId = -1;
        int mItemViewType = -1;
        int mPreLayoutPosition = -1;
        ViewHolder mShadowedHolder = null;
        ViewHolder mShadowingHolder = null;
        List<Object> mPayloads = null;
        List<Object> mUnmodifiedPayloads = null;
        private int mIsRecyclableCount = 0;
        private Recycler mScrapContainer = null;
        private boolean mInChangeScrap = false;
        private int mWasImportantForAccessibilityBeforeHidden = 0;
        int mPendingAccessibilityState = -1;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }

        void flagRemovedAndOffsetPosition(int mNewPosition, int offset, boolean applyToPreLayout) {
            addFlags(8);
            offsetPosition(offset, applyToPreLayout);
            this.mPosition = mNewPosition;
        }

        void offsetPosition(int offset, boolean applyToPreLayout) {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
            if (this.mPreLayoutPosition == -1) {
                this.mPreLayoutPosition = this.mPosition;
            }
            if (applyToPreLayout) {
                this.mPreLayoutPosition += offset;
            }
            this.mPosition += offset;
            if (this.itemView.getLayoutParams() != null) {
                ((LayoutParams) this.itemView.getLayoutParams()).mInsetsDirty = true;
            }
        }

        void clearOldPosition() {
            this.mOldPosition = -1;
            this.mPreLayoutPosition = -1;
        }

        void saveOldPosition() {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean shouldIgnore() {
            return (this.mFlags & 128) != 0;
        }

        @Deprecated
        public final int getPosition() {
            int i = this.mPreLayoutPosition;
            return i == -1 ? this.mPosition : i;
        }

        public final int getLayoutPosition() {
            int i = this.mPreLayoutPosition;
            return i == -1 ? this.mPosition : i;
        }

        public final int getAdapterPosition() {
            RecyclerView recyclerView = this.mOwnerRecyclerView;
            if (recyclerView == null) {
                return -1;
            }
            return recyclerView.getAdapterPositionFor(this);
        }

        public final int getOldPosition() {
            return this.mOldPosition;
        }

        public final long getItemId() {
            return this.mItemId;
        }

        public final int getItemViewType() {
            return this.mItemViewType;
        }

        boolean isScrap() {
            return this.mScrapContainer != null;
        }

        void unScrap() {
            this.mScrapContainer.unscrapView(this);
        }

        boolean wasReturnedFromScrap() {
            return (this.mFlags & 32) != 0;
        }

        void clearReturnedFromScrapFlag() {
            this.mFlags &= -33;
        }

        void clearTmpDetachFlag() {
            this.mFlags &= -257;
        }

        void stopIgnoring() {
            this.mFlags &= PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
        }

        void setScrapContainer(Recycler recycler, boolean isChangeScrap) {
            this.mScrapContainer = recycler;
            this.mInChangeScrap = isChangeScrap;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isInvalid() {
            return (this.mFlags & 4) != 0;
        }

        boolean needsUpdate() {
            return (this.mFlags & 2) != 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isBound() {
            return (this.mFlags & 1) != 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isRemoved() {
            return (this.mFlags & 8) != 0;
        }

        boolean hasAnyOfTheFlags(int flags) {
            return (this.mFlags & flags) != 0;
        }

        boolean isTmpDetached() {
            return (this.mFlags & 256) != 0;
        }

        boolean isAdapterPositionUnknown() {
            return (this.mFlags & 512) != 0 || isInvalid();
        }

        void setFlags(int flags, int mask) {
            this.mFlags = (this.mFlags & (~mask)) | (flags & mask);
        }

        void addFlags(int flags) {
            this.mFlags |= flags;
        }

        void addChangePayload(Object payload) {
            if (payload != null) {
                if ((1024 & this.mFlags) == 0) {
                    createPayloadsIfNeeded();
                    this.mPayloads.add(payload);
                    return;
                }
                return;
            }
            addFlags(1024);
        }

        private void createPayloadsIfNeeded() {
            if (this.mPayloads == null) {
                ArrayList arrayList = new ArrayList();
                this.mPayloads = arrayList;
                this.mUnmodifiedPayloads = Collections.unmodifiableList(arrayList);
            }
        }

        void clearPayload() {
            List<Object> list = this.mPayloads;
            if (list != null) {
                list.clear();
            }
            this.mFlags &= -1025;
        }

        List<Object> getUnmodifiedPayloads() {
            if ((this.mFlags & 1024) == 0) {
                List<Object> list = this.mPayloads;
                if (list == null || list.size() == 0) {
                    return FULLUPDATE_PAYLOADS;
                }
                return this.mUnmodifiedPayloads;
            }
            return FULLUPDATE_PAYLOADS;
        }

        void resetInternal() {
            this.mFlags = 0;
            this.mPosition = -1;
            this.mOldPosition = -1;
            this.mItemId = -1L;
            this.mPreLayoutPosition = -1;
            this.mIsRecyclableCount = 0;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            clearPayload();
            this.mWasImportantForAccessibilityBeforeHidden = 0;
            this.mPendingAccessibilityState = -1;
            RecyclerView.clearNestedRecyclerViewIfNotNested(this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onEnteredHiddenState(RecyclerView parent) {
            this.mWasImportantForAccessibilityBeforeHidden = this.itemView.getImportantForAccessibility();
            parent.setChildImportantForAccessibilityInternal(this, 4);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onLeftHiddenState(RecyclerView parent) {
            parent.setChildImportantForAccessibilityInternal(this, this.mWasImportantForAccessibilityBeforeHidden);
            this.mWasImportantForAccessibilityBeforeHidden = 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("ViewHolder{" + Integer.toHexString(hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
            if (isScrap()) {
                sb.append(" scrap ").append(this.mInChangeScrap ? "[changeScrap]" : "[attachedScrap]");
            }
            if (isInvalid()) {
                sb.append(" invalid");
            }
            if (!isBound()) {
                sb.append(" unbound");
            }
            if (needsUpdate()) {
                sb.append(" update");
            }
            if (isRemoved()) {
                sb.append(" removed");
            }
            if (shouldIgnore()) {
                sb.append(" ignored");
            }
            if (isTmpDetached()) {
                sb.append(" tmpDetached");
            }
            if (!isRecyclable()) {
                sb.append(" not recyclable(" + this.mIsRecyclableCount + NavigationBarInflaterView.KEY_CODE_END);
            }
            if (isAdapterPositionUnknown()) {
                sb.append(" undefined adapter position");
            }
            if (this.itemView.getParent() == null) {
                sb.append(" no parent");
            }
            sb.append("}");
            return sb.toString();
        }

        public final void setIsRecyclable(boolean recyclable) {
            int i = this.mIsRecyclableCount;
            int i2 = recyclable ? i - 1 : i + 1;
            this.mIsRecyclableCount = i2;
            if (i2 < 0) {
                this.mIsRecyclableCount = 0;
                Log.m110e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
            } else if (!recyclable && i2 == 1) {
                this.mFlags |= 16;
            } else if (recyclable && i2 == 0) {
                this.mFlags &= -17;
            }
        }

        public final boolean isRecyclable() {
            return (this.mFlags & 16) == 0 && !this.itemView.hasTransientState();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean shouldBeKeptAsChild() {
            return (this.mFlags & 16) != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean doesTransientStatePreventRecycling() {
            return (this.mFlags & 16) == 0 && this.itemView.hasTransientState();
        }

        boolean isUpdated() {
            return (this.mFlags & 2) != 0;
        }
    }

    boolean setChildImportantForAccessibilityInternal(ViewHolder viewHolder, int importantForAccessibility) {
        if (isComputingLayout()) {
            viewHolder.mPendingAccessibilityState = importantForAccessibility;
            this.mPendingAccessibilityImportanceChange.add(viewHolder);
            return false;
        }
        viewHolder.itemView.setImportantForAccessibility(importantForAccessibility);
        return true;
    }

    void dispatchPendingImportantForAccessibilityChanges() {
        int state;
        for (int i = this.mPendingAccessibilityImportanceChange.size() - 1; i >= 0; i--) {
            ViewHolder viewHolder = this.mPendingAccessibilityImportanceChange.get(i);
            if (viewHolder.itemView.getParent() == this && !viewHolder.shouldIgnore() && (state = viewHolder.mPendingAccessibilityState) != -1) {
                viewHolder.itemView.setImportantForAccessibility(state);
                viewHolder.mPendingAccessibilityState = -1;
            }
        }
        this.mPendingAccessibilityImportanceChange.clear();
    }

    int getAdapterPositionFor(ViewHolder viewHolder) {
        if (viewHolder.hasAnyOfTheFlags(524) || !viewHolder.isBound()) {
            return -1;
        }
        return this.mAdapterHelper.applyPendingUpdatesToPosition(viewHolder.mPosition);
    }

    /* loaded from: classes5.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        final Rect mDecorInsets;
        boolean mInsetsDirty;
        boolean mPendingInvalidate;
        ViewHolder mViewHolder;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.LayoutParams) source);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }

        public boolean viewNeedsUpdate() {
            return this.mViewHolder.needsUpdate();
        }

        public boolean isViewInvalid() {
            return this.mViewHolder.isInvalid();
        }

        public boolean isItemRemoved() {
            return this.mViewHolder.isRemoved();
        }

        public boolean isItemChanged() {
            return this.mViewHolder.isUpdated();
        }

        @Deprecated
        public int getViewPosition() {
            return this.mViewHolder.getPosition();
        }

        public int getViewLayoutPosition() {
            return this.mViewHolder.getLayoutPosition();
        }

        public int getViewAdapterPosition() {
            return this.mViewHolder.getAdapterPosition();
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class AdapterDataObserver {
        public void onChanged() {
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onItemRangeChanged(positionStart, itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class SmoothScroller {
        private LayoutManager mLayoutManager;
        private boolean mPendingInitialRun;
        private RecyclerView mRecyclerView;
        private boolean mRunning;
        private View mTargetView;
        private int mTargetPosition = -1;
        private final Action mRecyclingAction = new Action(0, 0);

        /* loaded from: classes5.dex */
        public interface ScrollVectorProvider {
            PointF computeScrollVectorForPosition(int i);
        }

        protected abstract void onSeekTargetStep(int i, int i2, State state, Action action);

        protected abstract void onStart();

        protected abstract void onStop();

        protected abstract void onTargetFound(View view, State state, Action action);

        void start(RecyclerView recyclerView, LayoutManager layoutManager) {
            this.mRecyclerView = recyclerView;
            this.mLayoutManager = layoutManager;
            if (this.mTargetPosition == -1) {
                throw new IllegalArgumentException("Invalid target position");
            }
            recyclerView.mState.mTargetPosition = this.mTargetPosition;
            this.mRunning = true;
            this.mPendingInitialRun = true;
            this.mTargetView = findViewByPosition(getTargetPosition());
            onStart();
            this.mRecyclerView.mViewFlinger.postOnAnimation();
        }

        public void setTargetPosition(int targetPosition) {
            this.mTargetPosition = targetPosition;
        }

        public LayoutManager getLayoutManager() {
            return this.mLayoutManager;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public final void stop() {
            if (!this.mRunning) {
                return;
            }
            onStop();
            this.mRecyclerView.mState.mTargetPosition = -1;
            this.mTargetView = null;
            this.mTargetPosition = -1;
            this.mPendingInitialRun = false;
            this.mRunning = false;
            this.mLayoutManager.onSmoothScrollerStopped(this);
            this.mLayoutManager = null;
            this.mRecyclerView = null;
        }

        public boolean isPendingInitialRun() {
            return this.mPendingInitialRun;
        }

        public boolean isRunning() {
            return this.mRunning;
        }

        public int getTargetPosition() {
            return this.mTargetPosition;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onAnimation(int dx, int dy) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (!this.mRunning || this.mTargetPosition == -1 || recyclerView == null) {
                stop();
            }
            this.mPendingInitialRun = false;
            View view = this.mTargetView;
            if (view != null) {
                if (getChildPosition(view) == this.mTargetPosition) {
                    onTargetFound(this.mTargetView, recyclerView.mState, this.mRecyclingAction);
                    this.mRecyclingAction.runIfNecessary(recyclerView);
                    stop();
                } else {
                    Log.m110e(RecyclerView.TAG, "Passed over target position while smooth scrolling.");
                    this.mTargetView = null;
                }
            }
            if (this.mRunning) {
                onSeekTargetStep(dx, dy, recyclerView.mState, this.mRecyclingAction);
                boolean hadJumpTarget = this.mRecyclingAction.hasJumpTarget();
                this.mRecyclingAction.runIfNecessary(recyclerView);
                if (hadJumpTarget) {
                    if (this.mRunning) {
                        this.mPendingInitialRun = true;
                        recyclerView.mViewFlinger.postOnAnimation();
                        return;
                    }
                    stop();
                }
            }
        }

        public int getChildPosition(View view) {
            return this.mRecyclerView.getChildLayoutPosition(view);
        }

        public int getChildCount() {
            return this.mRecyclerView.mLayout.getChildCount();
        }

        public View findViewByPosition(int position) {
            return this.mRecyclerView.mLayout.findViewByPosition(position);
        }

        @Deprecated
        public void instantScrollToPosition(int position) {
            this.mRecyclerView.scrollToPosition(position);
        }

        protected void onChildAttachedToWindow(View child) {
            if (getChildPosition(child) == getTargetPosition()) {
                this.mTargetView = child;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void normalize(PointF scrollVector) {
            double magnitude = Math.sqrt((scrollVector.f78x * scrollVector.f78x) + (scrollVector.f79y * scrollVector.f79y));
            scrollVector.f78x = (float) (scrollVector.f78x / magnitude);
            scrollVector.f79y = (float) (scrollVector.f79y / magnitude);
        }

        /* loaded from: classes5.dex */
        public static class Action {
            public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
            private boolean mChanged;
            private int mConsecutiveUpdates;
            private int mDuration;
            private int mDx;
            private int mDy;
            private Interpolator mInterpolator;
            private int mJumpToPosition;

            public Action(int dx, int dy) {
                this(dx, dy, Integer.MIN_VALUE, null);
            }

            public Action(int dx, int dy, int duration) {
                this(dx, dy, duration, null);
            }

            public Action(int dx, int dy, int duration, Interpolator interpolator) {
                this.mJumpToPosition = -1;
                this.mChanged = false;
                this.mConsecutiveUpdates = 0;
                this.mDx = dx;
                this.mDy = dy;
                this.mDuration = duration;
                this.mInterpolator = interpolator;
            }

            public void jumpTo(int targetPosition) {
                this.mJumpToPosition = targetPosition;
            }

            boolean hasJumpTarget() {
                return this.mJumpToPosition >= 0;
            }

            void runIfNecessary(RecyclerView recyclerView) {
                if (this.mJumpToPosition >= 0) {
                    int position = this.mJumpToPosition;
                    this.mJumpToPosition = -1;
                    recyclerView.jumpToPositionForSmoothScroller(position);
                    this.mChanged = false;
                } else if (this.mChanged) {
                    validate();
                    if (this.mInterpolator == null) {
                        if (this.mDuration == Integer.MIN_VALUE) {
                            recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
                        } else {
                            recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
                        }
                    } else {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
                    }
                    int i = this.mConsecutiveUpdates + 1;
                    this.mConsecutiveUpdates = i;
                    if (i > 10) {
                        Log.m110e(RecyclerView.TAG, "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
                    }
                    this.mChanged = false;
                } else {
                    this.mConsecutiveUpdates = 0;
                }
            }

            private void validate() {
                if (this.mInterpolator != null && this.mDuration < 1) {
                    throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
                }
                if (this.mDuration < 1) {
                    throw new IllegalStateException("Scroll duration must be a positive number");
                }
            }

            public int getDx() {
                return this.mDx;
            }

            public void setDx(int dx) {
                this.mChanged = true;
                this.mDx = dx;
            }

            public int getDy() {
                return this.mDy;
            }

            public void setDy(int dy) {
                this.mChanged = true;
                this.mDy = dy;
            }

            public int getDuration() {
                return this.mDuration;
            }

            public void setDuration(int duration) {
                this.mChanged = true;
                this.mDuration = duration;
            }

            public Interpolator getInterpolator() {
                return this.mInterpolator;
            }

            public void setInterpolator(Interpolator interpolator) {
                this.mChanged = true;
                this.mInterpolator = interpolator;
            }

            public void update(int dx, int dy, int duration, Interpolator interpolator) {
                this.mDx = dx;
                this.mDy = dy;
                this.mDuration = duration;
                this.mInterpolator = interpolator;
                this.mChanged = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        AdapterDataObservable() {
        }

        public boolean hasObservers() {
            return !this.mObservers.isEmpty();
        }

        public void notifyChanged() {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                ((AdapterDataObserver) this.mObservers.get(i)).onChanged();
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount, null);
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                ((AdapterDataObserver) this.mObservers.get(i)).onItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                ((AdapterDataObserver) this.mObservers.get(i)).onItemRangeInserted(positionStart, itemCount);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                ((AdapterDataObserver) this.mObservers.get(i)).onItemRangeRemoved(positionStart, itemCount);
            }
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            for (int i = this.mObservers.size() - 1; i >= 0; i--) {
                ((AdapterDataObserver) this.mObservers.get(i)).onItemRangeMoved(fromPosition, toPosition, 1);
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.internal.widget.RecyclerView.SavedState.1
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
        Parcelable mLayoutState;

        SavedState(Parcel in) {
            super(in);
            this.mLayoutState = in.readParcelable(LayoutManager.class.getClassLoader());
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override // android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeParcelable(this.mLayoutState, 0);
        }

        void copyFrom(SavedState other) {
            this.mLayoutState = other.mLayoutState;
        }
    }

    /* loaded from: classes5.dex */
    public static class State {
        static final int STEP_ANIMATIONS = 4;
        static final int STEP_LAYOUT = 2;
        static final int STEP_START = 1;
        private SparseArray<Object> mData;
        long mFocusedItemId;
        int mFocusedItemPosition;
        int mFocusedSubChildId;
        private int mTargetPosition = -1;
        int mPreviousLayoutItemCount = 0;
        int mDeletedInvisibleItemCountSincePreviousLayout = 0;
        int mLayoutStep = 1;
        int mItemCount = 0;
        boolean mStructureChanged = false;
        boolean mInPreLayout = false;
        boolean mTrackOldChangeHolders = false;
        boolean mIsMeasuring = false;
        boolean mRunSimpleAnimations = false;
        boolean mRunPredictiveAnimations = false;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes5.dex */
        @interface LayoutState {
        }

        void assertLayoutStep(int accepted) {
            if ((this.mLayoutStep & accepted) == 0) {
                throw new IllegalStateException("Layout state should be one of " + Integer.toBinaryString(accepted) + " but it is " + Integer.toBinaryString(this.mLayoutStep));
            }
        }

        State reset() {
            this.mTargetPosition = -1;
            SparseArray<Object> sparseArray = this.mData;
            if (sparseArray != null) {
                sparseArray.clear();
            }
            this.mItemCount = 0;
            this.mStructureChanged = false;
            this.mIsMeasuring = false;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void prepareForNestedPrefetch(Adapter adapter) {
            this.mLayoutStep = 1;
            this.mItemCount = adapter.getItemCount();
            this.mStructureChanged = false;
            this.mInPreLayout = false;
            this.mTrackOldChangeHolders = false;
            this.mIsMeasuring = false;
        }

        public boolean isMeasuring() {
            return this.mIsMeasuring;
        }

        public boolean isPreLayout() {
            return this.mInPreLayout;
        }

        public boolean willRunPredictiveAnimations() {
            return this.mRunPredictiveAnimations;
        }

        public boolean willRunSimpleAnimations() {
            return this.mRunSimpleAnimations;
        }

        public void remove(int resourceId) {
            SparseArray<Object> sparseArray = this.mData;
            if (sparseArray == null) {
                return;
            }
            sparseArray.remove(resourceId);
        }

        public <T> T get(int resourceId) {
            SparseArray<Object> sparseArray = this.mData;
            if (sparseArray == null) {
                return null;
            }
            return (T) sparseArray.get(resourceId);
        }

        public void put(int resourceId, Object data) {
            if (this.mData == null) {
                this.mData = new SparseArray<>();
            }
            this.mData.put(resourceId, data);
        }

        public int getTargetScrollPosition() {
            return this.mTargetPosition;
        }

        public boolean hasTargetScrollPosition() {
            return this.mTargetPosition != -1;
        }

        public boolean didStructureChange() {
            return this.mStructureChanged;
        }

        public int getItemCount() {
            if (this.mInPreLayout) {
                return this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout;
            }
            return this.mItemCount;
        }

        public String toString() {
            return "State{mTargetPosition=" + this.mTargetPosition + ", mData=" + this.mData + ", mItemCount=" + this.mItemCount + ", mPreviousLayoutItemCount=" + this.mPreviousLayoutItemCount + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.mDeletedInvisibleItemCountSincePreviousLayout + ", mStructureChanged=" + this.mStructureChanged + ", mInPreLayout=" + this.mInPreLayout + ", mRunSimpleAnimations=" + this.mRunSimpleAnimations + ", mRunPredictiveAnimations=" + this.mRunPredictiveAnimations + '}';
        }
    }

    /* loaded from: classes5.dex */
    private class ItemAnimatorRestoreListener implements ItemAnimator.ItemAnimatorListener {
        ItemAnimatorRestoreListener() {
        }

        @Override // com.android.internal.widget.RecyclerView.ItemAnimator.ItemAnimatorListener
        public void onAnimationFinished(ViewHolder item) {
            item.setIsRecyclable(true);
            if (item.mShadowedHolder != null && item.mShadowingHolder == null) {
                item.mShadowedHolder = null;
            }
            item.mShadowingHolder = null;
            if (!item.shouldBeKeptAsChild() && !RecyclerView.this.removeAnimatingView(item.itemView) && item.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(item.itemView, false);
            }
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class ItemAnimator {
        public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        public static final int FLAG_CHANGED = 2;
        public static final int FLAG_INVALIDATED = 4;
        public static final int FLAG_MOVED = 2048;
        public static final int FLAG_REMOVED = 8;
        private ItemAnimatorListener mListener = null;
        private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners = new ArrayList<>();
        private long mAddDuration = 120;
        private long mRemoveDuration = 120;
        private long mMoveDuration = 250;
        private long mChangeDuration = 250;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes5.dex */
        public @interface AdapterChanges {
        }

        /* loaded from: classes5.dex */
        public interface ItemAnimatorFinishedListener {
            void onAnimationsFinished();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes5.dex */
        public interface ItemAnimatorListener {
            void onAnimationFinished(ViewHolder viewHolder);
        }

        public abstract boolean animateAppearance(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        public abstract boolean animateChange(ViewHolder viewHolder, ViewHolder viewHolder2, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        public abstract boolean animateDisappearance(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        public abstract boolean animatePersistence(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        public abstract void endAnimation(ViewHolder viewHolder);

        public abstract void endAnimations();

        public abstract boolean isRunning();

        public abstract void runPendingAnimations();

        public long getMoveDuration() {
            return this.mMoveDuration;
        }

        public void setMoveDuration(long moveDuration) {
            this.mMoveDuration = moveDuration;
        }

        public long getAddDuration() {
            return this.mAddDuration;
        }

        public void setAddDuration(long addDuration) {
            this.mAddDuration = addDuration;
        }

        public long getRemoveDuration() {
            return this.mRemoveDuration;
        }

        public void setRemoveDuration(long removeDuration) {
            this.mRemoveDuration = removeDuration;
        }

        public long getChangeDuration() {
            return this.mChangeDuration;
        }

        public void setChangeDuration(long changeDuration) {
            this.mChangeDuration = changeDuration;
        }

        void setListener(ItemAnimatorListener listener) {
            this.mListener = listener;
        }

        public ItemHolderInfo recordPreLayoutInformation(State state, ViewHolder viewHolder, int changeFlags, List<Object> payloads) {
            return obtainHolderInfo().setFrom(viewHolder);
        }

        public ItemHolderInfo recordPostLayoutInformation(State state, ViewHolder viewHolder) {
            return obtainHolderInfo().setFrom(viewHolder);
        }

        static int buildAdapterChangeFlagsForAnimations(ViewHolder viewHolder) {
            int flags = viewHolder.mFlags & 14;
            if (viewHolder.isInvalid()) {
                return 4;
            }
            if ((flags & 4) == 0) {
                int oldPos = viewHolder.getOldPosition();
                int pos = viewHolder.getAdapterPosition();
                if (oldPos != -1 && pos != -1 && oldPos != pos) {
                    return flags | 2048;
                }
                return flags;
            }
            return flags;
        }

        public final void dispatchAnimationFinished(ViewHolder viewHolder) {
            onAnimationFinished(viewHolder);
            ItemAnimatorListener itemAnimatorListener = this.mListener;
            if (itemAnimatorListener != null) {
                itemAnimatorListener.onAnimationFinished(viewHolder);
            }
        }

        public void onAnimationFinished(ViewHolder viewHolder) {
        }

        public final void dispatchAnimationStarted(ViewHolder viewHolder) {
            onAnimationStarted(viewHolder);
        }

        public void onAnimationStarted(ViewHolder viewHolder) {
        }

        public final boolean isRunning(ItemAnimatorFinishedListener listener) {
            boolean running = isRunning();
            if (listener != null) {
                if (!running) {
                    listener.onAnimationsFinished();
                } else {
                    this.mFinishedListeners.add(listener);
                }
            }
            return running;
        }

        public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder) {
            return true;
        }

        public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder, List<Object> payloads) {
            return canReuseUpdatedViewHolder(viewHolder);
        }

        public final void dispatchAnimationsFinished() {
            int count = this.mFinishedListeners.size();
            for (int i = 0; i < count; i++) {
                this.mFinishedListeners.get(i).onAnimationsFinished();
            }
            this.mFinishedListeners.clear();
        }

        public ItemHolderInfo obtainHolderInfo() {
            return new ItemHolderInfo();
        }

        /* loaded from: classes5.dex */
        public static class ItemHolderInfo {
            public int bottom;
            public int changeFlags;
            public int left;
            public int right;
            public int top;

            public ItemHolderInfo setFrom(ViewHolder holder) {
                return setFrom(holder, 0);
            }

            public ItemHolderInfo setFrom(ViewHolder holder, int flags) {
                View view = holder.itemView;
                this.left = view.getLeft();
                this.top = view.getTop();
                this.right = view.getRight();
                this.bottom = view.getBottom();
                return this;
            }
        }
    }

    @Override // android.view.ViewGroup
    protected int getChildDrawingOrder(int childCount, int i) {
        ChildDrawingOrderCallback childDrawingOrderCallback = this.mChildDrawingOrderCallback;
        if (childDrawingOrderCallback == null) {
            return super.getChildDrawingOrder(childCount, i);
        }
        return childDrawingOrderCallback.onGetChildDrawingOrder(childCount, i);
    }
}
