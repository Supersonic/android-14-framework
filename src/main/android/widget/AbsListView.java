package android.widget;

import android.app.ActivityThread;
import android.app.slice.Slice;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.FontListParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.StrictMode;
import android.p008os.Trace;
import android.provider.DeviceConfig;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.StateSet;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View$InspectionCompanion$$ExternalSyntheticLambda0;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.view.ViewParent;
import android.view.ViewStructure;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.autofill.AutofillId;
import android.view.contentcapture.ContentCaptureManager;
import android.view.contentcapture.ContentCaptureSession;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.SurroundingText;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.RemoteViews;
import android.widget.RemoteViewsAdapter;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes4.dex */
public abstract class AbsListView extends AdapterView<ListAdapter> implements TextWatcher, ViewTreeObserver.OnGlobalLayoutListener, Filter.FilterListener, ViewTreeObserver.OnTouchModeChangeListener, RemoteViewsAdapter.RemoteAdapterConnectionCallback {
    private static final int CHECK_POSITION_SEARCH_DISTANCE = 20;
    public static final int CHOICE_MODE_MULTIPLE = 2;
    public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;
    public static final int CHOICE_MODE_NONE = 0;
    public static final int CHOICE_MODE_SINGLE = 1;
    private static final float FLING_DESTRETCH_FACTOR = 4.0f;
    private static final int INVALID_POINTER = -1;
    static final int LAYOUT_FORCE_BOTTOM = 3;
    static final int LAYOUT_FORCE_TOP = 1;
    static final int LAYOUT_MOVE_SELECTION = 6;
    static final int LAYOUT_NORMAL = 0;
    static final int LAYOUT_SET_SELECTION = 2;
    static final int LAYOUT_SPECIFIC = 4;
    static final int LAYOUT_SYNC = 5;
    static final int OVERSCROLL_LIMIT_DIVISOR = 3;
    private static final boolean PROFILE_FLINGING = false;
    private static final boolean PROFILE_SCROLLING = false;
    private static final String TAG = "AbsListView";
    static final int TOUCH_MODE_DONE_WAITING = 2;
    static final int TOUCH_MODE_DOWN = 0;
    static final int TOUCH_MODE_FLING = 4;
    private static final int TOUCH_MODE_OFF = 1;
    private static final int TOUCH_MODE_ON = 0;
    static final int TOUCH_MODE_OVERFLING = 6;
    static final int TOUCH_MODE_OVERSCROLL = 5;
    static final int TOUCH_MODE_REST = -1;
    static final int TOUCH_MODE_SCROLL = 3;
    static final int TOUCH_MODE_TAP = 1;
    private static final int TOUCH_MODE_UNKNOWN = -1;
    public static final int TRANSCRIPT_MODE_ALWAYS_SCROLL = 2;
    public static final int TRANSCRIPT_MODE_DISABLED = 0;
    public static final int TRANSCRIPT_MODE_NORMAL = 1;
    private static boolean sContentCaptureReportingEnabledByDeviceConfig = false;
    private static DeviceConfig.OnPropertiesChangedListener sDeviceConfigChangeListener = null;
    static final Interpolator sLinearInterpolator = new LinearInterpolator();
    private ListItemAccessibilityDelegate mAccessibilityDelegate;
    private int mActivePointerId;
    ListAdapter mAdapter;
    boolean mAdapterHasStableIds;
    private int mCacheColorHint;
    boolean mCachingActive;
    boolean mCachingStarted;
    SparseBooleanArray mCheckStates;
    LongSparseArray<Integer> mCheckedIdStates;
    int mCheckedItemCount;
    ActionMode mChoiceActionMode;
    int mChoiceMode;
    private Runnable mClearScrollingCache;
    private ContextMenu.ContextMenuInfo mContextMenuInfo;
    AdapterDataSetObserver mDataSetObserver;
    private InputConnection mDefInputConnection;
    private boolean mDeferNotifyDataSetChanged;
    private float mDensityScale;
    private int mDirection;
    boolean mDrawSelectorOnTop;
    public EdgeEffect mEdgeGlowBottom;
    public EdgeEffect mEdgeGlowTop;
    private FastScroller mFastScroll;
    boolean mFastScrollAlwaysVisible;
    boolean mFastScrollEnabled;
    private int mFastScrollStyle;
    private boolean mFiltered;
    private int mFirstPositionDistanceGuess;
    private boolean mFlingProfilingStarted;
    private FlingRunnable mFlingRunnable;
    private StrictMode.Span mFlingStrictSpan;
    private boolean mForceTranscriptScroll;
    private boolean mGlobalLayoutListenerAddedFilter;
    private boolean mHasPerformedLongPress;
    private boolean mIsChildViewEnabled;
    private boolean mIsDetaching;
    final boolean[] mIsScrap;
    private int mLastHandledItemCount;
    private int mLastPositionDistanceGuess;
    private int mLastScrollState;
    private int mLastTouchMode;
    int mLastY;
    int mLayoutMode;
    Rect mListPadding;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    int mMotionCorrection;
    int mMotionPosition;
    int mMotionViewNewTop;
    int mMotionViewOriginalTop;
    int mMotionX;
    int mMotionY;
    MultiChoiceModeWrapper mMultiChoiceModeCallback;
    private int mNestedYOffset;
    private OnScrollListener mOnScrollListener;
    int mOverflingDistance;
    int mOverscrollDistance;
    int mOverscrollMax;
    private final Thread mOwnerThread;
    private CheckForKeyLongPress mPendingCheckForKeyLongPress;
    private CheckForLongPress mPendingCheckForLongPress;
    private CheckForTap mPendingCheckForTap;
    private SavedState mPendingSync;
    private PerformClick mPerformClick;
    PopupWindow mPopup;
    private boolean mPopupHidden;
    Runnable mPositionScrollAfterLayout;
    AbsPositionScroller mPositionScroller;
    private InputConnectionWrapper mPublicInputConnection;
    final RecycleBin mRecycler;
    private RemoteViewsAdapter mRemoteAdapter;
    private boolean mReportChildrenToContentCaptureOnNextUpdate;
    int mResurrectToPosition;
    private final int[] mScrollConsumed;
    View mScrollDown;
    private final int[] mScrollOffset;
    private boolean mScrollProfilingStarted;
    private StrictMode.Span mScrollStrictSpan;
    View mScrollUp;
    boolean mScrollingCacheEnabled;
    int mSelectedTop;
    int mSelectionBottomPadding;
    int mSelectionLeftPadding;
    int mSelectionRightPadding;
    int mSelectionTopPadding;
    Drawable mSelector;
    int mSelectorPosition;
    Rect mSelectorRect;
    private int[] mSelectorState;
    private boolean mSmoothScrollbarEnabled;
    boolean mStackFromBottom;
    EditText mTextFilter;
    private boolean mTextFilterEnabled;
    private final float[] mTmpPoint;
    private Rect mTouchFrame;
    int mTouchMode;
    private Runnable mTouchModeReset;
    private int mTouchSlop;
    private int mTranscriptMode;
    private float mVelocityScale;
    private VelocityTracker mVelocityTracker;
    private float mVerticalScrollFactor;
    int mWidthMeasureSpec;

    /* loaded from: classes4.dex */
    public interface MultiChoiceModeListener extends ActionMode.Callback {
        void onItemCheckedStateChanged(ActionMode actionMode, int i, long j, boolean z);
    }

    /* loaded from: classes4.dex */
    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        void onScroll(AbsListView absListView, int i, int i2, int i3);

        void onScrollStateChanged(AbsListView absListView, int i);
    }

    /* loaded from: classes4.dex */
    public interface RecyclerListener {
        void onMovedToScrapHeap(View view);
    }

    /* loaded from: classes4.dex */
    public interface SelectionBoundsAdjuster {
        void adjustListItemSelectionBounds(Rect rect);
    }

    abstract void fillGap(boolean z);

    abstract int findMotionRow(int i);

    abstract void setSelectionInt(int i);

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<AbsListView> {
        private int mCacheColorHintId;
        private int mChoiceModeId;
        private int mDrawSelectorOnTopId;
        private int mFastScrollEnabledId;
        private int mListSelectorId;
        private boolean mPropertiesMapped = false;
        private int mScrollingCacheId;
        private int mSmoothScrollbarId;
        private int mStackFromBottomId;
        private int mTextFilterEnabledId;
        private int mTranscriptModeId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mCacheColorHintId = propertyMapper.mapColor("cacheColorHint", 16843009);
            SparseArray<String> choiceModeEnumMapping = new SparseArray<>();
            choiceModeEnumMapping.put(0, "none");
            choiceModeEnumMapping.put(1, "singleChoice");
            choiceModeEnumMapping.put(2, "multipleChoice");
            choiceModeEnumMapping.put(3, "multipleChoiceModal");
            Objects.requireNonNull(choiceModeEnumMapping);
            this.mChoiceModeId = propertyMapper.mapIntEnum("choiceMode", 16843051, new View$InspectionCompanion$$ExternalSyntheticLambda0(choiceModeEnumMapping));
            this.mDrawSelectorOnTopId = propertyMapper.mapBoolean("drawSelectorOnTop", 16843004);
            this.mFastScrollEnabledId = propertyMapper.mapBoolean("fastScrollEnabled", 16843302);
            this.mListSelectorId = propertyMapper.mapObject("listSelector", 16843003);
            this.mScrollingCacheId = propertyMapper.mapBoolean("scrollingCache", 16843006);
            this.mSmoothScrollbarId = propertyMapper.mapBoolean("smoothScrollbar", 16843313);
            this.mStackFromBottomId = propertyMapper.mapBoolean("stackFromBottom", 16843005);
            this.mTextFilterEnabledId = propertyMapper.mapBoolean("textFilterEnabled", 16843007);
            SparseArray<String> transcriptModeEnumMapping = new SparseArray<>();
            transcriptModeEnumMapping.put(0, "disabled");
            transcriptModeEnumMapping.put(1, FontListParser.STYLE_NORMAL);
            transcriptModeEnumMapping.put(2, "alwaysScroll");
            Objects.requireNonNull(transcriptModeEnumMapping);
            this.mTranscriptModeId = propertyMapper.mapIntEnum("transcriptMode", 16843008, new View$InspectionCompanion$$ExternalSyntheticLambda0(transcriptModeEnumMapping));
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(AbsListView node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readColor(this.mCacheColorHintId, node.getCacheColorHint());
            propertyReader.readIntEnum(this.mChoiceModeId, node.getChoiceMode());
            propertyReader.readBoolean(this.mDrawSelectorOnTopId, node.isDrawSelectorOnTop());
            propertyReader.readBoolean(this.mFastScrollEnabledId, node.isFastScrollEnabled());
            propertyReader.readObject(this.mListSelectorId, node.getSelector());
            propertyReader.readBoolean(this.mScrollingCacheId, node.isScrollingCacheEnabled());
            propertyReader.readBoolean(this.mSmoothScrollbarId, node.isSmoothScrollbarEnabled());
            propertyReader.readBoolean(this.mStackFromBottomId, node.isStackFromBottom());
            propertyReader.readBoolean(this.mTextFilterEnabledId, node.isTextFilterEnabled());
            propertyReader.readIntEnum(this.mTranscriptModeId, node.getTranscriptMode());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class DeviceConfigChangeListener implements DeviceConfig.OnPropertiesChangedListener {
        private DeviceConfigChangeListener() {
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            if (!Context.CONTENT_CAPTURE_MANAGER_SERVICE.equals(properties.getNamespace())) {
                return;
            }
            for (String key : properties.getKeyset()) {
                if (ContentCaptureManager.DEVICE_CONFIG_PROPERTY_REPORT_LIST_VIEW_CHILDREN.equals(key)) {
                    AbsListView.sContentCaptureReportingEnabledByDeviceConfig = properties.getBoolean(key, false);
                }
            }
        }
    }

    private static void setupDeviceConfigProperties() {
        if (sDeviceConfigChangeListener == null) {
            sContentCaptureReportingEnabledByDeviceConfig = DeviceConfig.getBoolean(Context.CONTENT_CAPTURE_MANAGER_SERVICE, ContentCaptureManager.DEVICE_CONFIG_PROPERTY_REPORT_LIST_VIEW_CHILDREN, false);
            sDeviceConfigChangeListener = new DeviceConfigChangeListener();
            DeviceConfig.addOnPropertiesChangedListener(Context.CONTENT_CAPTURE_MANAGER_SERVICE, ActivityThread.currentApplication().getMainExecutor(), sDeviceConfigChangeListener);
        }
    }

    public AbsListView(Context context) {
        super(context);
        this.mChoiceMode = 0;
        this.mLayoutMode = 0;
        this.mDeferNotifyDataSetChanged = false;
        this.mDrawSelectorOnTop = false;
        this.mSelectorPosition = -1;
        this.mSelectorRect = new Rect();
        this.mRecycler = new RecycleBin();
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mListPadding = new Rect();
        this.mWidthMeasureSpec = 0;
        this.mTouchMode = -1;
        this.mSelectedTop = 0;
        this.mSmoothScrollbarEnabled = true;
        this.mResurrectToPosition = -1;
        this.mContextMenuInfo = null;
        this.mLastTouchMode = -1;
        this.mScrollProfilingStarted = false;
        this.mFlingProfilingStarted = false;
        this.mScrollStrictSpan = null;
        this.mFlingStrictSpan = null;
        this.mLastScrollState = 0;
        this.mReportChildrenToContentCaptureOnNextUpdate = true;
        this.mVelocityScale = 1.0f;
        this.mIsScrap = new boolean[1];
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mTmpPoint = new float[2];
        this.mNestedYOffset = 0;
        this.mActivePointerId = -1;
        this.mDirection = 0;
        setupDeviceConfigProperties();
        this.mEdgeGlowBottom = new EdgeEffect(context);
        this.mEdgeGlowTop = new EdgeEffect(context);
        initAbsListView();
        this.mOwnerThread = Thread.currentThread();
        setVerticalScrollBarEnabled(true);
        TypedArray a = context.obtainStyledAttributes(C4057R.styleable.View);
        initializeScrollbarsInternal(a);
        a.recycle();
    }

    public AbsListView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842858);
    }

    public AbsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AbsListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mChoiceMode = 0;
        this.mLayoutMode = 0;
        this.mDeferNotifyDataSetChanged = false;
        this.mDrawSelectorOnTop = false;
        this.mSelectorPosition = -1;
        this.mSelectorRect = new Rect();
        this.mRecycler = new RecycleBin();
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mListPadding = new Rect();
        this.mWidthMeasureSpec = 0;
        this.mTouchMode = -1;
        this.mSelectedTop = 0;
        this.mSmoothScrollbarEnabled = true;
        this.mResurrectToPosition = -1;
        this.mContextMenuInfo = null;
        this.mLastTouchMode = -1;
        this.mScrollProfilingStarted = false;
        this.mFlingProfilingStarted = false;
        this.mScrollStrictSpan = null;
        this.mFlingStrictSpan = null;
        this.mLastScrollState = 0;
        this.mReportChildrenToContentCaptureOnNextUpdate = true;
        this.mVelocityScale = 1.0f;
        this.mIsScrap = new boolean[1];
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mTmpPoint = new float[2];
        this.mNestedYOffset = 0;
        this.mActivePointerId = -1;
        this.mDirection = 0;
        setupDeviceConfigProperties();
        this.mEdgeGlowBottom = new EdgeEffect(context, attrs);
        this.mEdgeGlowTop = new EdgeEffect(context, attrs);
        initAbsListView();
        this.mOwnerThread = Thread.currentThread();
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.AbsListView, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.AbsListView, attrs, a, defStyleAttr, defStyleRes);
        Drawable selector = a.getDrawable(0);
        if (selector != null) {
            setSelector(selector);
        }
        this.mDrawSelectorOnTop = a.getBoolean(1, false);
        setStackFromBottom(a.getBoolean(2, false));
        setScrollingCacheEnabled(a.getBoolean(3, true));
        setTextFilterEnabled(a.getBoolean(4, false));
        setTranscriptMode(a.getInt(5, 0));
        setCacheColorHint(a.getColor(6, 0));
        setSmoothScrollbarEnabled(a.getBoolean(9, true));
        setChoiceMode(a.getInt(7, 0));
        setFastScrollEnabled(a.getBoolean(8, false));
        setFastScrollStyle(a.getResourceId(11, 0));
        setFastScrollAlwaysVisible(a.getBoolean(10, false));
        a.recycle();
        if (context.getResources().getConfiguration().uiMode == 6) {
            setRevealOnFocusHint(false);
        }
    }

    private void initAbsListView() {
        setClickable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
        setScrollingCacheEnabled(true);
        ViewConfiguration configuration = ViewConfiguration.get(this.mContext);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mVerticalScrollFactor = configuration.getScaledVerticalScrollFactor();
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.mOverscrollDistance = configuration.getScaledOverscrollDistance();
        this.mOverflingDistance = configuration.getScaledOverflingDistance();
        this.mDensityScale = getContext().getResources().getDisplayMetrics().density;
    }

    @Override // android.widget.AdapterView
    public void setAdapter(ListAdapter adapter) {
        if (adapter != null) {
            boolean hasStableIds = this.mAdapter.hasStableIds();
            this.mAdapterHasStableIds = hasStableIds;
            if (this.mChoiceMode != 0 && hasStableIds && this.mCheckedIdStates == null) {
                this.mCheckedIdStates = new LongSparseArray<>();
            }
        }
        clearChoices();
    }

    public int getCheckedItemCount() {
        return this.mCheckedItemCount;
    }

    public boolean isItemChecked(int position) {
        SparseBooleanArray sparseBooleanArray;
        if (this.mChoiceMode != 0 && (sparseBooleanArray = this.mCheckStates) != null) {
            return sparseBooleanArray.get(position);
        }
        return false;
    }

    public int getCheckedItemPosition() {
        SparseBooleanArray sparseBooleanArray;
        if (this.mChoiceMode == 1 && (sparseBooleanArray = this.mCheckStates) != null && sparseBooleanArray.size() == 1) {
            return this.mCheckStates.keyAt(0);
        }
        return -1;
    }

    public SparseBooleanArray getCheckedItemPositions() {
        if (this.mChoiceMode != 0) {
            return this.mCheckStates;
        }
        return null;
    }

    public long[] getCheckedItemIds() {
        if (this.mChoiceMode == 0 || this.mCheckedIdStates == null || this.mAdapter == null) {
            return new long[0];
        }
        LongSparseArray<Integer> idStates = this.mCheckedIdStates;
        int count = idStates.size();
        long[] ids = new long[count];
        for (int i = 0; i < count; i++) {
            ids[i] = idStates.keyAt(i);
        }
        return ids;
    }

    public void clearChoices() {
        SparseBooleanArray sparseBooleanArray = this.mCheckStates;
        if (sparseBooleanArray != null) {
            sparseBooleanArray.clear();
        }
        LongSparseArray<Integer> longSparseArray = this.mCheckedIdStates;
        if (longSparseArray != null) {
            longSparseArray.clear();
        }
        this.mCheckedItemCount = 0;
    }

    public void setItemChecked(int position, boolean value) {
        boolean itemCheckChanged;
        int i = this.mChoiceMode;
        if (i == 0) {
            return;
        }
        if (value && i == 3 && this.mChoiceActionMode == null) {
            MultiChoiceModeWrapper multiChoiceModeWrapper = this.mMultiChoiceModeCallback;
            if (multiChoiceModeWrapper == null || !multiChoiceModeWrapper.hasWrappedCallback()) {
                throw new IllegalStateException("AbsListView: attempted to start selection mode for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was supplied. Call setMultiChoiceModeListener to set a callback.");
            }
            this.mChoiceActionMode = startActionMode(this.mMultiChoiceModeCallback);
        }
        int i2 = this.mChoiceMode;
        if (i2 == 2 || i2 == 3) {
            boolean oldValue = this.mCheckStates.get(position);
            this.mCheckStates.put(position, value);
            if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                if (value) {
                    this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                } else {
                    this.mCheckedIdStates.delete(this.mAdapter.getItemId(position));
                }
            }
            itemCheckChanged = oldValue != value;
            if (itemCheckChanged) {
                if (value) {
                    this.mCheckedItemCount++;
                } else {
                    this.mCheckedItemCount--;
                }
            }
            if (this.mChoiceActionMode != null) {
                long id = this.mAdapter.getItemId(position);
                this.mMultiChoiceModeCallback.onItemCheckedStateChanged(this.mChoiceActionMode, position, id, value);
            }
        } else {
            boolean updateIds = this.mCheckedIdStates != null && this.mAdapter.hasStableIds();
            itemCheckChanged = isItemChecked(position) != value;
            if (value || isItemChecked(position)) {
                this.mCheckStates.clear();
                if (updateIds) {
                    this.mCheckedIdStates.clear();
                }
            }
            if (value) {
                this.mCheckStates.put(position, true);
                if (updateIds) {
                    this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                }
                this.mCheckedItemCount = 1;
            } else if (this.mCheckStates.size() == 0 || !this.mCheckStates.valueAt(0)) {
                this.mCheckedItemCount = 0;
            }
        }
        if (!this.mInLayout && !this.mBlockLayoutRequests && itemCheckChanged) {
            this.mDataChanged = true;
            rememberSyncState();
            requestLayout();
        }
    }

    @Override // android.widget.AdapterView
    public boolean performItemClick(View view, int position, long id) {
        boolean handled = false;
        boolean dispatchItemClick = true;
        int i = this.mChoiceMode;
        if (i != 0) {
            boolean checkedStateChanged = false;
            if (i == 2 || (i == 3 && this.mChoiceActionMode != null)) {
                boolean checked = !this.mCheckStates.get(position, false);
                this.mCheckStates.put(position, checked);
                if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                    if (checked) {
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                    } else {
                        this.mCheckedIdStates.delete(this.mAdapter.getItemId(position));
                    }
                }
                if (checked) {
                    this.mCheckedItemCount++;
                } else {
                    this.mCheckedItemCount--;
                }
                ActionMode actionMode = this.mChoiceActionMode;
                if (actionMode != null) {
                    this.mMultiChoiceModeCallback.onItemCheckedStateChanged(actionMode, position, id, checked);
                    dispatchItemClick = false;
                }
                checkedStateChanged = true;
            } else if (i == 1) {
                if (!this.mCheckStates.get(position, false)) {
                    this.mCheckStates.clear();
                    this.mCheckStates.put(position, true);
                    if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                        this.mCheckedIdStates.clear();
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                    }
                    this.mCheckedItemCount = 1;
                } else if (this.mCheckStates.size() == 0 || !this.mCheckStates.valueAt(0)) {
                    this.mCheckedItemCount = 0;
                }
                checkedStateChanged = true;
            }
            if (checkedStateChanged) {
                updateOnScreenCheckedViews();
            }
            handled = true;
        }
        if (dispatchItemClick) {
            return handled | super.performItemClick(view, position, id);
        }
        return handled;
    }

    private void updateOnScreenCheckedViews() {
        int firstPos = this.mFirstPosition;
        int count = getChildCount();
        boolean useActivated = getContext().getApplicationInfo().targetSdkVersion >= 11;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int position = firstPos + i;
            if (child instanceof Checkable) {
                ((Checkable) child).setChecked(this.mCheckStates.get(position));
            } else if (useActivated) {
                child.setActivated(this.mCheckStates.get(position));
            }
        }
    }

    public int getChoiceMode() {
        return this.mChoiceMode;
    }

    public void setChoiceMode(int choiceMode) {
        ListAdapter listAdapter;
        this.mChoiceMode = choiceMode;
        ActionMode actionMode = this.mChoiceActionMode;
        if (actionMode != null) {
            actionMode.finish();
            this.mChoiceActionMode = null;
        }
        if (this.mChoiceMode != 0) {
            if (this.mCheckStates == null) {
                this.mCheckStates = new SparseBooleanArray(0);
            }
            if (this.mCheckedIdStates == null && (listAdapter = this.mAdapter) != null && listAdapter.hasStableIds()) {
                this.mCheckedIdStates = new LongSparseArray<>(0);
            }
            if (this.mChoiceMode == 3) {
                clearChoices();
                setLongClickable(true);
            }
        }
    }

    public void setMultiChoiceModeListener(MultiChoiceModeListener listener) {
        if (this.mMultiChoiceModeCallback == null) {
            this.mMultiChoiceModeCallback = new MultiChoiceModeWrapper();
        }
        this.mMultiChoiceModeCallback.setWrapped(listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean contentFits() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }
        if (childCount != this.mItemCount) {
            return false;
        }
        return getChildAt(0).getTop() >= this.mListPadding.top && getChildAt(childCount + (-1)).getBottom() <= getHeight() - this.mListPadding.bottom;
    }

    public void setFastScrollEnabled(final boolean enabled) {
        if (this.mFastScrollEnabled != enabled) {
            this.mFastScrollEnabled = enabled;
            if (isOwnerThread()) {
                setFastScrollerEnabledUiThread(enabled);
            } else {
                post(new Runnable() { // from class: android.widget.AbsListView.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AbsListView.this.setFastScrollerEnabledUiThread(enabled);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFastScrollerEnabledUiThread(boolean enabled) {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.setEnabled(enabled);
        } else if (enabled) {
            FastScroller fastScroller2 = new FastScroller(this, this.mFastScrollStyle);
            this.mFastScroll = fastScroller2;
            fastScroller2.setEnabled(true);
        }
        resolvePadding();
        FastScroller fastScroller3 = this.mFastScroll;
        if (fastScroller3 != null) {
            fastScroller3.updateLayout();
        }
    }

    public void setFastScrollStyle(int styleResId) {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller == null) {
            this.mFastScrollStyle = styleResId;
        } else {
            fastScroller.setStyle(styleResId);
        }
    }

    public void setFastScrollAlwaysVisible(final boolean alwaysShow) {
        if (this.mFastScrollAlwaysVisible != alwaysShow) {
            if (alwaysShow && !this.mFastScrollEnabled) {
                setFastScrollEnabled(true);
            }
            this.mFastScrollAlwaysVisible = alwaysShow;
            if (isOwnerThread()) {
                setFastScrollerAlwaysVisibleUiThread(alwaysShow);
            } else {
                post(new Runnable() { // from class: android.widget.AbsListView.2
                    @Override // java.lang.Runnable
                    public void run() {
                        AbsListView.this.setFastScrollerAlwaysVisibleUiThread(alwaysShow);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFastScrollerAlwaysVisibleUiThread(boolean alwaysShow) {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.setAlwaysShow(alwaysShow);
        }
    }

    private boolean isOwnerThread() {
        return this.mOwnerThread == Thread.currentThread();
    }

    public boolean isFastScrollAlwaysVisible() {
        FastScroller fastScroller = this.mFastScroll;
        return fastScroller == null ? this.mFastScrollEnabled && this.mFastScrollAlwaysVisible : fastScroller.isEnabled() && this.mFastScroll.isAlwaysShowEnabled();
    }

    @Override // android.view.View
    public int getVerticalScrollbarWidth() {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null && fastScroller.isEnabled()) {
            return Math.max(super.getVerticalScrollbarWidth(), this.mFastScroll.getWidth());
        }
        return super.getVerticalScrollbarWidth();
    }

    @ViewDebug.ExportedProperty
    public boolean isFastScrollEnabled() {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller == null) {
            return this.mFastScrollEnabled;
        }
        return fastScroller.isEnabled();
    }

    @Override // android.view.View
    public void setVerticalScrollbarPosition(int position) {
        super.setVerticalScrollbarPosition(position);
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.setScrollbarPosition(position);
        }
    }

    @Override // android.view.View
    public void setScrollBarStyle(int style) {
        super.setScrollBarStyle(style);
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.setScrollBarStyle(style);
        }
    }

    @Override // android.view.View
    protected boolean isVerticalScrollBarHidden() {
        return isFastScrollEnabled();
    }

    public void setSmoothScrollbarEnabled(boolean enabled) {
        this.mSmoothScrollbarEnabled = enabled;
    }

    @ViewDebug.ExportedProperty
    public boolean isSmoothScrollbarEnabled() {
        return this.mSmoothScrollbarEnabled;
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.mOnScrollListener = l;
        invokeOnItemScrollListener();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invokeOnItemScrollListener() {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.onScroll(this.mFirstPosition, getChildCount(), this.mItemCount);
        }
        OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScroll(this, this.mFirstPosition, getChildCount(), this.mItemCount);
        }
        onScrollChanged(0, 0, 0, 0);
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return AbsListView.class.getName();
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        if (isEnabled()) {
            if (canScrollUp()) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP);
                info.setScrollable(true);
            }
            if (canScrollDown()) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN);
                info.setScrollable(true);
            }
        }
        info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
        info.setClickable(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSelectionModeForAccessibility() {
        int choiceMode = getChoiceMode();
        switch (choiceMode) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
            case 3:
                return 2;
            default:
                return 0;
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        if (super.performAccessibilityActionInternal(action, arguments)) {
            return true;
        }
        switch (action) {
            case 4096:
            case 16908346:
                if (isEnabled() && canScrollDown()) {
                    int viewportHeight = (getHeight() - this.mListPadding.top) - this.mListPadding.bottom;
                    smoothScrollBy(viewportHeight, 200);
                    return true;
                }
                return false;
            case 8192:
            case 16908344:
                if (isEnabled() && canScrollUp()) {
                    int viewportHeight2 = (getHeight() - this.mListPadding.top) - this.mListPadding.bottom;
                    smoothScrollBy(-viewportHeight2, 200);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @ViewDebug.ExportedProperty
    public boolean isScrollingCacheEnabled() {
        return this.mScrollingCacheEnabled;
    }

    public void setScrollingCacheEnabled(boolean enabled) {
        if (this.mScrollingCacheEnabled && !enabled) {
            clearScrollingCache();
        }
        this.mScrollingCacheEnabled = enabled;
    }

    public void setTextFilterEnabled(boolean textFilterEnabled) {
        this.mTextFilterEnabled = textFilterEnabled;
    }

    @ViewDebug.ExportedProperty
    public boolean isTextFilterEnabled() {
        return this.mTextFilterEnabled;
    }

    @Override // android.view.View
    public void getFocusedRect(Rect r) {
        View view = getSelectedView();
        if (view != null && view.getParent() == this) {
            view.getFocusedRect(r);
            offsetDescendantRectToMyCoords(view, r);
            return;
        }
        super.getFocusedRect(r);
    }

    private void useDefaultSelector() {
        setSelector(getContext().getDrawable(17301602));
    }

    @ViewDebug.ExportedProperty
    public boolean isStackFromBottom() {
        return this.mStackFromBottom;
    }

    public void setStackFromBottom(boolean stackFromBottom) {
        if (this.mStackFromBottom != stackFromBottom) {
            this.mStackFromBottom = stackFromBottom;
            requestLayoutIfNecessary();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestLayoutIfNecessary() {
        if (getChildCount() > 0) {
            resetList();
            requestLayout();
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.AbsListView.SavedState.1
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
        LongSparseArray<Integer> checkIdState;
        SparseBooleanArray checkState;
        int checkedItemCount;
        String filter;
        long firstId;
        int height;
        boolean inActionMode;
        int position;
        long selectedId;
        int viewTop;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.selectedId = in.readLong();
            this.firstId = in.readLong();
            this.viewTop = in.readInt();
            this.position = in.readInt();
            this.height = in.readInt();
            this.filter = in.readString();
            this.inActionMode = in.readByte() != 0;
            this.checkedItemCount = in.readInt();
            this.checkState = in.readSparseBooleanArray();
            int N = in.readInt();
            if (N > 0) {
                this.checkIdState = new LongSparseArray<>();
                for (int i = 0; i < N; i++) {
                    long key = in.readLong();
                    int value = in.readInt();
                    this.checkIdState.put(key, Integer.valueOf(value));
                }
            }
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(this.selectedId);
            out.writeLong(this.firstId);
            out.writeInt(this.viewTop);
            out.writeInt(this.position);
            out.writeInt(this.height);
            out.writeString(this.filter);
            out.writeByte(this.inActionMode ? (byte) 1 : (byte) 0);
            out.writeInt(this.checkedItemCount);
            out.writeSparseBooleanArray(this.checkState);
            LongSparseArray<Integer> longSparseArray = this.checkIdState;
            int N = longSparseArray != null ? longSparseArray.size() : 0;
            out.writeInt(N);
            for (int i = 0; i < N; i++) {
                out.writeLong(this.checkIdState.keyAt(i));
                out.writeInt(this.checkIdState.valueAt(i).intValue());
            }
        }

        public String toString() {
            return "AbsListView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " selectedId=" + this.selectedId + " firstId=" + this.firstId + " viewTop=" + this.viewTop + " position=" + this.position + " height=" + this.height + " filter=" + this.filter + " checkState=" + this.checkState + "}";
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        EditText textFilter;
        Editable filterText;
        dismissPopup();
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        SavedState savedState = this.mPendingSync;
        if (savedState != null) {
            ss.selectedId = savedState.selectedId;
            ss.firstId = this.mPendingSync.firstId;
            ss.viewTop = this.mPendingSync.viewTop;
            ss.position = this.mPendingSync.position;
            ss.height = this.mPendingSync.height;
            ss.filter = this.mPendingSync.filter;
            ss.inActionMode = this.mPendingSync.inActionMode;
            ss.checkedItemCount = this.mPendingSync.checkedItemCount;
            ss.checkState = this.mPendingSync.checkState;
            ss.checkIdState = this.mPendingSync.checkIdState;
            return ss;
        }
        boolean z = true;
        boolean haveChildren = getChildCount() > 0 && this.mItemCount > 0;
        long selectedId = getSelectedItemId();
        ss.selectedId = selectedId;
        ss.height = getHeight();
        if (selectedId >= 0) {
            ss.viewTop = this.mSelectedTop;
            ss.position = getSelectedItemPosition();
            ss.firstId = -1L;
        } else if (haveChildren && this.mFirstPosition > 0) {
            View v = getChildAt(0);
            ss.viewTop = v.getTop();
            int firstPos = this.mFirstPosition;
            if (firstPos >= this.mItemCount) {
                firstPos = this.mItemCount - 1;
            }
            ss.position = firstPos;
            ss.firstId = this.mAdapter.getItemId(firstPos);
        } else {
            ss.viewTop = 0;
            ss.firstId = -1L;
            ss.position = 0;
        }
        ss.filter = null;
        if (this.mFiltered && (textFilter = this.mTextFilter) != null && (filterText = textFilter.getText()) != null) {
            ss.filter = filterText.toString();
        }
        ss.inActionMode = (this.mChoiceMode != 3 || this.mChoiceActionMode == null) ? false : false;
        SparseBooleanArray sparseBooleanArray = this.mCheckStates;
        if (sparseBooleanArray != null) {
            ss.checkState = sparseBooleanArray.m4830clone();
        }
        if (this.mCheckedIdStates != null) {
            LongSparseArray<Integer> idState = new LongSparseArray<>();
            int count = this.mCheckedIdStates.size();
            for (int i = 0; i < count; i++) {
                idState.put(this.mCheckedIdStates.keyAt(i), this.mCheckedIdStates.valueAt(i));
            }
            ss.checkIdState = idState;
        }
        ss.checkedItemCount = this.mCheckedItemCount;
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteAdapter;
        if (remoteViewsAdapter != null) {
            remoteViewsAdapter.saveRemoteViewsCache();
        }
        return ss;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        MultiChoiceModeWrapper multiChoiceModeWrapper;
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mDataChanged = true;
        this.mSyncHeight = ss.height;
        if (ss.selectedId >= 0) {
            this.mNeedSync = true;
            this.mPendingSync = ss;
            this.mSyncRowId = ss.selectedId;
            this.mSyncPosition = ss.position;
            this.mSpecificTop = ss.viewTop;
            this.mSyncMode = 0;
        } else if (ss.firstId >= 0) {
            setSelectedPositionInt(-1);
            setNextSelectedPositionInt(-1);
            this.mSelectorPosition = -1;
            this.mNeedSync = true;
            this.mPendingSync = ss;
            this.mSyncRowId = ss.firstId;
            this.mSyncPosition = ss.position;
            this.mSpecificTop = ss.viewTop;
            this.mSyncMode = 1;
        }
        setFilterText(ss.filter);
        if (ss.checkState != null) {
            this.mCheckStates = ss.checkState;
        }
        if (ss.checkIdState != null) {
            this.mCheckedIdStates = ss.checkIdState;
        }
        this.mCheckedItemCount = ss.checkedItemCount;
        if (ss.inActionMode && this.mChoiceMode == 3 && (multiChoiceModeWrapper = this.mMultiChoiceModeCallback) != null) {
            this.mChoiceActionMode = startActionMode(multiChoiceModeWrapper);
        }
        requestLayout();
    }

    private boolean acceptFilter() {
        return this.mTextFilterEnabled && (getAdapter() instanceof Filterable) && ((Filterable) getAdapter()).getFilter() != null;
    }

    public void setFilterText(String filterText) {
        if (this.mTextFilterEnabled && !TextUtils.isEmpty(filterText)) {
            createTextFilter(false);
            this.mTextFilter.setText(filterText);
            this.mTextFilter.setSelection(filterText.length());
            ListAdapter listAdapter = this.mAdapter;
            if (listAdapter instanceof Filterable) {
                if (this.mPopup == null) {
                    Filter f = ((Filterable) listAdapter).getFilter();
                    f.filter(filterText);
                }
                this.mFiltered = true;
                this.mDataSetObserver.clearSavedState();
            }
        }
    }

    public CharSequence getTextFilter() {
        EditText editText;
        if (this.mTextFilterEnabled && (editText = this.mTextFilter) != null) {
            return editText.getText();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && this.mSelectedPosition < 0 && !isInTouchMode()) {
            if (!isAttachedToWindow() && this.mAdapter != null) {
                this.mDataChanged = true;
                this.mOldItemCount = this.mItemCount;
                this.mItemCount = this.mAdapter.getCount();
            }
            resurrectSelection();
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (!this.mBlockLayoutRequests && !this.mInLayout) {
            super.requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetList() {
        removeAllViewsInLayout();
        this.mFirstPosition = 0;
        this.mDataChanged = false;
        this.mPositionScrollAfterLayout = null;
        this.mNeedSync = false;
        this.mPendingSync = null;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        setSelectedPositionInt(-1);
        setNextSelectedPositionInt(-1);
        this.mSelectedTop = 0;
        this.mSelectorPosition = -1;
        this.mSelectorRect.setEmpty();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollExtent() {
        int count = getChildCount();
        if (count <= 0) {
            return 0;
        }
        if (this.mSmoothScrollbarEnabled) {
            int extent = count * 100;
            View view = getChildAt(0);
            int top = view.getTop();
            int height = view.getHeight();
            if (height > 0) {
                extent += (top * 100) / height;
            }
            View view2 = getChildAt(count - 1);
            int bottom = view2.getBottom();
            int height2 = view2.getHeight();
            if (height2 > 0) {
                return extent - (((bottom - getHeight()) * 100) / height2);
            }
            return extent;
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollOffset() {
        int index;
        int firstPosition = this.mFirstPosition;
        int childCount = getChildCount();
        if (firstPosition >= 0 && childCount > 0) {
            if (this.mSmoothScrollbarEnabled) {
                View view = getChildAt(0);
                int top = view.getTop();
                int height = view.getHeight();
                if (height > 0) {
                    return Math.max(((firstPosition * 100) - ((top * 100) / height)) + ((int) ((this.mScrollY / getHeight()) * this.mItemCount * 100.0f)), 0);
                }
            } else {
                int count = this.mItemCount;
                if (firstPosition == 0) {
                    index = 0;
                } else {
                    int index2 = firstPosition + childCount;
                    if (index2 == count) {
                        index = count;
                    } else {
                        int index3 = childCount / 2;
                        index = index3 + firstPosition;
                    }
                }
                return (int) (firstPosition + (childCount * (index / count)));
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollRange() {
        if (this.mSmoothScrollbarEnabled) {
            int result = Math.max(this.mItemCount * 100, 0);
            if (this.mScrollY != 0) {
                return result + Math.abs((int) ((this.mScrollY / getHeight()) * this.mItemCount * 100.0f));
            }
            return result;
        }
        return this.mItemCount;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public float getTopFadingEdgeStrength() {
        int count = getChildCount();
        float fadeEdge = super.getTopFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        }
        if (this.mFirstPosition > 0) {
            return 1.0f;
        }
        int top = getChildAt(0).getTop();
        float fadeLength = getVerticalFadingEdgeLength();
        return top < this.mPaddingTop ? (-(top - this.mPaddingTop)) / fadeLength : fadeEdge;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public float getBottomFadingEdgeStrength() {
        int count = getChildCount();
        float fadeEdge = super.getBottomFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        }
        if ((this.mFirstPosition + count) - 1 < this.mItemCount - 1) {
            return 1.0f;
        }
        int bottom = getChildAt(count - 1).getBottom();
        int height = getHeight();
        float fadeLength = getVerticalFadingEdgeLength();
        return bottom > height - this.mPaddingBottom ? ((bottom - height) + this.mPaddingBottom) / fadeLength : fadeEdge;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mSelector == null) {
            useDefaultSelector();
        }
        Rect listPadding = this.mListPadding;
        listPadding.left = this.mSelectionLeftPadding + this.mPaddingLeft;
        listPadding.top = this.mSelectionTopPadding + this.mPaddingTop;
        listPadding.right = this.mSelectionRightPadding + this.mPaddingRight;
        listPadding.bottom = this.mSelectionBottomPadding + this.mPaddingBottom;
        boolean z = true;
        if (this.mTranscriptMode == 1) {
            int childCount = getChildCount();
            int listBottom = getHeight() - getPaddingBottom();
            View lastChild = getChildAt(childCount - 1);
            int lastBottom = lastChild != null ? lastChild.getBottom() : listBottom;
            this.mForceTranscriptScroll = (this.mFirstPosition + childCount < this.mLastHandledItemCount || lastBottom > listBottom) ? false : false;
        }
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mInLayout = true;
        int childCount = getChildCount();
        if (changed) {
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).forceLayout();
            }
            this.mRecycler.markChildrenDirty();
        }
        layoutChildren();
        this.mOverscrollMax = (b - t) / 3;
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.onItemCountChanged(getChildCount(), this.mItemCount);
        }
        this.mInLayout = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean setFrame(int left, int top, int right, int bottom) {
        PopupWindow popupWindow;
        boolean changed = super.setFrame(left, top, right, bottom);
        if (changed) {
            boolean visible = getWindowVisibility() == 0;
            if (this.mFiltered && visible && (popupWindow = this.mPopup) != null && popupWindow.isShowing()) {
                positionPopup();
            }
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void layoutChildren() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getAccessibilityFocusedChild(View focusedView) {
        ViewParent viewParent = focusedView.getParent();
        while ((viewParent instanceof View) && viewParent != this) {
            focusedView = (View) viewParent;
            viewParent = viewParent.getParent();
        }
        if (!(viewParent instanceof View)) {
            return null;
        }
        return focusedView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateScrollIndicators() {
        View view = this.mScrollUp;
        if (view != null) {
            view.setVisibility(canScrollUp() ? 0 : 4);
        }
        View view2 = this.mScrollDown;
        if (view2 != null) {
            view2.setVisibility(canScrollDown() ? 0 : 4);
        }
    }

    private boolean canScrollUp() {
        boolean canScrollUp = this.mFirstPosition > 0;
        if (!canScrollUp && getChildCount() > 0) {
            View child = getChildAt(0);
            return child.getTop() < this.mListPadding.top;
        }
        return canScrollUp;
    }

    private boolean canScrollDown() {
        int count = getChildCount();
        boolean canScrollDown = this.mFirstPosition + count < this.mItemCount;
        if (!canScrollDown && count > 0) {
            View child = getChildAt(count - 1);
            return child.getBottom() > this.mBottom - this.mListPadding.bottom;
        }
        return canScrollDown;
    }

    @Override // android.widget.AdapterView
    @ViewDebug.ExportedProperty
    public View getSelectedView() {
        if (this.mItemCount > 0 && this.mSelectedPosition >= 0) {
            return getChildAt(this.mSelectedPosition - this.mFirstPosition);
        }
        return null;
    }

    public int getListPaddingTop() {
        return this.mListPadding.top;
    }

    public int getListPaddingBottom() {
        return this.mListPadding.bottom;
    }

    public int getListPaddingLeft() {
        return this.mListPadding.left;
    }

    public int getListPaddingRight() {
        return this.mListPadding.right;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View obtainView(int position, boolean[] outMetadata) {
        View updatedView;
        Trace.traceBegin(8L, "obtainView");
        outMetadata[0] = false;
        View transientView = this.mRecycler.getTransientStateView(position);
        if (transientView != null) {
            LayoutParams params = (LayoutParams) transientView.getLayoutParams();
            if (params.viewType == this.mAdapter.getItemViewType(position) && (updatedView = this.mAdapter.getView(position, transientView, this)) != transientView) {
                setItemViewLayoutParams(updatedView, position);
                this.mRecycler.addScrapView(updatedView, position);
            }
            outMetadata[0] = true;
            transientView.dispatchFinishTemporaryDetach();
            return transientView;
        }
        View scrapView = this.mRecycler.getScrapView(position);
        View child = this.mAdapter.getView(position, scrapView, this);
        if (scrapView != null) {
            if (child != scrapView) {
                this.mRecycler.addScrapView(scrapView, position);
            } else if (child.isTemporarilyDetached()) {
                outMetadata[0] = true;
                child.dispatchFinishTemporaryDetach();
            }
        }
        int i = this.mCacheColorHint;
        if (i != 0) {
            child.setDrawingCacheBackgroundColor(i);
        }
        if (child.getImportantForAccessibility() == 0) {
            child.setImportantForAccessibility(1);
        }
        setItemViewLayoutParams(child, position);
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (this.mAccessibilityDelegate == null) {
                this.mAccessibilityDelegate = new ListItemAccessibilityDelegate();
            }
            if (child.getAccessibilityDelegate() == null) {
                child.setAccessibilityDelegate(this.mAccessibilityDelegate);
            }
        }
        Trace.traceEnd(8L);
        return child;
    }

    private void setItemViewLayoutParams(View child, int position) {
        LayoutParams lp;
        ViewGroup.LayoutParams vlp = child.getLayoutParams();
        if (vlp == null) {
            lp = (LayoutParams) generateDefaultLayoutParams();
        } else if (!checkLayoutParams(vlp)) {
            lp = (LayoutParams) generateLayoutParams(vlp);
        } else {
            lp = (LayoutParams) vlp;
        }
        if (this.mAdapterHasStableIds) {
            lp.itemId = this.mAdapter.getItemId(position);
        }
        lp.viewType = this.mAdapter.getItemViewType(position);
        lp.isEnabled = this.mAdapter.isEnabled(position);
        if (lp != vlp) {
            child.setLayoutParams(lp);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class ListItemAccessibilityDelegate extends View.AccessibilityDelegate {
        ListItemAccessibilityDelegate() {
        }

        @Override // android.view.View.AccessibilityDelegate
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            int position = AbsListView.this.getPositionForView(host);
            AbsListView.this.onInitializeAccessibilityNodeInfoForItem(host, position, info);
        }

        @Override // android.view.View.AccessibilityDelegate
        public boolean performAccessibilityAction(View host, int action, Bundle arguments) {
            boolean isItemEnabled;
            if (super.performAccessibilityAction(host, action, arguments)) {
                return true;
            }
            int position = AbsListView.this.getPositionForView(host);
            if (position == -1 || AbsListView.this.mAdapter == null || position >= AbsListView.this.mAdapter.getCount()) {
                return false;
            }
            ViewGroup.LayoutParams lp = host.getLayoutParams();
            if (lp instanceof LayoutParams) {
                isItemEnabled = ((LayoutParams) lp).isEnabled;
            } else {
                isItemEnabled = false;
            }
            if (AbsListView.this.isEnabled() && isItemEnabled) {
                switch (action) {
                    case 4:
                        if (AbsListView.this.getSelectedItemPosition() != position) {
                            AbsListView.this.setSelection(position);
                            return true;
                        }
                        return false;
                    case 8:
                        if (AbsListView.this.getSelectedItemPosition() == position) {
                            AbsListView.this.setSelection(-1);
                            return true;
                        }
                        return false;
                    case 16:
                        if (AbsListView.this.isItemClickable(host)) {
                            long id = AbsListView.this.getItemIdAtPosition(position);
                            return AbsListView.this.performItemClick(host, position, id);
                        }
                        return false;
                    case 32:
                        if (AbsListView.this.isLongClickable()) {
                            long id2 = AbsListView.this.getItemIdAtPosition(position);
                            return AbsListView.this.performLongPress(host, position, id2);
                        }
                        return false;
                    default:
                        return false;
                }
            }
            return false;
        }
    }

    public void onInitializeAccessibilityNodeInfoForItem(View view, int position, AccessibilityNodeInfo info) {
        if (position == -1) {
            return;
        }
        boolean isItemActionable = isEnabled();
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof LayoutParams) {
            isItemActionable &= ((LayoutParams) lp).isEnabled;
        }
        if (position == getSelectedItemPosition()) {
            info.setSelected(true);
            addAccessibilityActionIfEnabled(info, isItemActionable, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_SELECTION);
        } else {
            addAccessibilityActionIfEnabled(info, isItemActionable, AccessibilityNodeInfo.AccessibilityAction.ACTION_SELECT);
        }
        if (isItemClickable(view)) {
            addAccessibilityActionIfEnabled(info, isItemActionable, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            info.setClickable(isItemActionable);
        }
        if (isLongClickable()) {
            addAccessibilityActionIfEnabled(info, isItemActionable, AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
            info.setLongClickable(isItemActionable);
        }
    }

    private void addAccessibilityActionIfEnabled(AccessibilityNodeInfo info, boolean enabled, AccessibilityNodeInfo.AccessibilityAction action) {
        if (enabled) {
            info.addAction(action);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isItemClickable(View view) {
        return !view.hasExplicitFocusable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void positionSelectorLikeTouch(int position, View sel, float x, float y) {
        positionSelector(position, sel, true, x, y);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void positionSelectorLikeFocus(int position, View sel) {
        if (this.mSelector != null && this.mSelectorPosition != position && position != -1) {
            Rect bounds = this.mSelectorRect;
            float x = bounds.exactCenterX();
            float y = bounds.exactCenterY();
            positionSelector(position, sel, true, x, y);
            return;
        }
        positionSelector(position, sel);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void positionSelector(int position, View sel) {
        positionSelector(position, sel, false, -1.0f, -1.0f);
    }

    private void positionSelector(int position, View sel, boolean manageHotspot, float x, float y) {
        boolean positionChanged = position != this.mSelectorPosition;
        if (position != -1) {
            this.mSelectorPosition = position;
        }
        Rect selectorRect = this.mSelectorRect;
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        if (sel instanceof SelectionBoundsAdjuster) {
            ((SelectionBoundsAdjuster) sel).adjustListItemSelectionBounds(selectorRect);
        }
        selectorRect.left -= this.mSelectionLeftPadding;
        selectorRect.top -= this.mSelectionTopPadding;
        selectorRect.right += this.mSelectionRightPadding;
        selectorRect.bottom += this.mSelectionBottomPadding;
        boolean isChildViewEnabled = sel.isEnabled();
        if (this.mIsChildViewEnabled != isChildViewEnabled) {
            this.mIsChildViewEnabled = isChildViewEnabled;
        }
        Drawable selector = this.mSelector;
        if (selector != null) {
            if (positionChanged) {
                selector.setVisible(false, false);
                selector.setState(StateSet.NOTHING);
            }
            selector.setBounds(selectorRect);
            if (positionChanged) {
                if (getVisibility() == 0) {
                    selector.setVisible(true, false);
                }
                updateSelectorState();
            }
            if (manageHotspot) {
                selector.setHotspot(x, y);
            }
        }
    }

    public boolean isSelectedChildViewEnabled() {
        return this.mIsChildViewEnabled;
    }

    public void setSelectedChildViewEnabled(boolean selectedChildViewEnabled) {
        this.mIsChildViewEnabled = selectedChildViewEnabled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        int saveCount = 0;
        boolean clipToPadding = (this.mGroupFlags & 34) == 34;
        if (clipToPadding) {
            saveCount = canvas.save();
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            canvas.clipRect(this.mPaddingLeft + scrollX, this.mPaddingTop + scrollY, ((this.mRight + scrollX) - this.mLeft) - this.mPaddingRight, ((this.mBottom + scrollY) - this.mTop) - this.mPaddingBottom);
            this.mGroupFlags &= -35;
        }
        boolean drawSelectorOnTop = this.mDrawSelectorOnTop;
        if (!drawSelectorOnTop) {
            drawSelector(canvas);
        }
        super.dispatchDraw(canvas);
        if (drawSelectorOnTop) {
            drawSelector(canvas);
        }
        if (clipToPadding) {
            canvas.restoreToCount(saveCount);
            this.mGroupFlags = 34 | this.mGroupFlags;
        }
    }

    @Override // android.view.View
    protected boolean isPaddingOffsetRequired() {
        return (this.mGroupFlags & 34) != 34;
    }

    @Override // android.view.View
    protected int getLeftPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return -this.mPaddingLeft;
    }

    @Override // android.view.View
    protected int getTopPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return -this.mPaddingTop;
    }

    @Override // android.view.View
    protected int getRightPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return this.mPaddingRight;
    }

    @Override // android.view.View
    protected int getBottomPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return this.mPaddingBottom;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void internalSetPadding(int left, int top, int right, int bottom) {
        super.internalSetPadding(left, top, right, bottom);
        if (isLayoutRequested()) {
            handleBoundsChange();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        handleBoundsChange();
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.onSizeChanged(w, h, oldw, oldh);
        }
    }

    void handleBoundsChange() {
        int childCount;
        if (!this.mInLayout && (childCount = getChildCount()) > 0) {
            this.mDataChanged = true;
            rememberSyncState();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                ViewGroup.LayoutParams lp = child.getLayoutParams();
                if (lp == null || lp.width < 1 || lp.height < 1) {
                    child.forceLayout();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean touchModeDrawsInPressedState() {
        switch (this.mTouchMode) {
            case 1:
            case 2:
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldShowSelector() {
        return (isFocused() && !isInTouchMode()) || (touchModeDrawsInPressedState() && isPressed());
    }

    private void drawSelector(Canvas canvas) {
        if (shouldDrawSelector()) {
            Drawable selector = this.mSelector;
            selector.setBounds(this.mSelectorRect);
            selector.draw(canvas);
        }
    }

    public final boolean shouldDrawSelector() {
        return !this.mSelectorRect.isEmpty();
    }

    public void setDrawSelectorOnTop(boolean onTop) {
        this.mDrawSelectorOnTop = onTop;
    }

    public boolean isDrawSelectorOnTop() {
        return this.mDrawSelectorOnTop;
    }

    public void setSelector(int resID) {
        setSelector(getContext().getDrawable(resID));
    }

    public void setSelector(Drawable sel) {
        Drawable drawable = this.mSelector;
        if (drawable != null) {
            drawable.setCallback(null);
            unscheduleDrawable(this.mSelector);
        }
        this.mSelector = sel;
        Rect padding = new Rect();
        sel.getPadding(padding);
        this.mSelectionLeftPadding = padding.left;
        this.mSelectionTopPadding = padding.top;
        this.mSelectionRightPadding = padding.right;
        this.mSelectionBottomPadding = padding.bottom;
        sel.setCallback(this);
        updateSelectorState();
    }

    public Drawable getSelector() {
        return this.mSelector;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void keyPressed() {
        if (!isEnabled() || !isClickable()) {
            return;
        }
        Drawable selector = this.mSelector;
        Rect selectorRect = this.mSelectorRect;
        if (selector != null) {
            if ((isFocused() || touchModeDrawsInPressedState()) && !selectorRect.isEmpty()) {
                View v = getChildAt(this.mSelectedPosition - this.mFirstPosition);
                if (v != null) {
                    if (v.hasExplicitFocusable()) {
                        return;
                    }
                    v.setPressed(true);
                }
                setPressed(true);
                boolean longClickable = isLongClickable();
                Drawable d = selector.getCurrent();
                if (d != null && (d instanceof TransitionDrawable)) {
                    if (longClickable) {
                        ((TransitionDrawable) d).startTransition(ViewConfiguration.getLongPressTimeout());
                    } else {
                        ((TransitionDrawable) d).resetTransition();
                    }
                }
                if (longClickable && !this.mDataChanged) {
                    if (this.mPendingCheckForKeyLongPress == null) {
                        this.mPendingCheckForKeyLongPress = new CheckForKeyLongPress();
                    }
                    this.mPendingCheckForKeyLongPress.rememberWindowAttachCount();
                    postDelayed(this.mPendingCheckForKeyLongPress, ViewConfiguration.getLongPressTimeout());
                }
            }
        }
    }

    public void setScrollIndicators(View up, View down) {
        this.mScrollUp = up;
        this.mScrollDown = down;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSelectorState() {
        Drawable selector = this.mSelector;
        if (selector != null && selector.isStateful()) {
            if (shouldShowSelector()) {
                if (selector.setState(getDrawableStateForSelector())) {
                    invalidateDrawable(selector);
                    return;
                }
                return;
            }
            selector.setState(StateSet.NOTHING);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    private int[] getDrawableStateForSelector() {
        if (this.mIsChildViewEnabled) {
            return super.getDrawableState();
        }
        int enabledState = ENABLED_STATE_SET[0];
        int[] state = onCreateDrawableState(1);
        int enabledPos = -1;
        int i = state.length - 1;
        while (true) {
            if (i < 0) {
                break;
            } else if (state[i] != enabledState) {
                i--;
            } else {
                enabledPos = i;
                break;
            }
        }
        if (enabledPos >= 0) {
            System.arraycopy(state, enabledPos + 1, state, enabledPos, (state.length - enabledPos) - 1);
        }
        return state;
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable dr) {
        return this.mSelector == dr || super.verifyDrawable(dr);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mSelector;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewTreeObserver treeObserver = getViewTreeObserver();
        treeObserver.addOnTouchModeChangeListener(this);
        if (this.mTextFilterEnabled && this.mPopup != null && !this.mGlobalLayoutListenerAddedFilter) {
            treeObserver.addOnGlobalLayoutListener(this);
        }
        if (this.mAdapter != null && this.mDataSetObserver == null) {
            AdapterDataSetObserver adapterDataSetObserver = new AdapterDataSetObserver();
            this.mDataSetObserver = adapterDataSetObserver;
            this.mAdapter.registerDataSetObserver(adapterDataSetObserver);
            this.mDataChanged = true;
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        AdapterDataSetObserver adapterDataSetObserver;
        super.onDetachedFromWindow();
        this.mIsDetaching = true;
        dismissPopup();
        this.mRecycler.clear();
        ViewTreeObserver treeObserver = getViewTreeObserver();
        treeObserver.removeOnTouchModeChangeListener(this);
        if (this.mTextFilterEnabled && this.mPopup != null) {
            treeObserver.removeOnGlobalLayoutListener(this);
            this.mGlobalLayoutListenerAddedFilter = false;
        }
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter != null && (adapterDataSetObserver = this.mDataSetObserver) != null) {
            listAdapter.unregisterDataSetObserver(adapterDataSetObserver);
            this.mDataSetObserver = null;
        }
        StrictMode.Span span = this.mScrollStrictSpan;
        if (span != null) {
            span.finish();
            this.mScrollStrictSpan = null;
        }
        StrictMode.Span span2 = this.mFlingStrictSpan;
        if (span2 != null) {
            span2.finish();
            this.mFlingStrictSpan = null;
        }
        FlingRunnable flingRunnable = this.mFlingRunnable;
        if (flingRunnable != null) {
            removeCallbacks(flingRunnable);
        }
        AbsPositionScroller absPositionScroller = this.mPositionScroller;
        if (absPositionScroller != null) {
            absPositionScroller.stop();
        }
        Runnable runnable = this.mClearScrollingCache;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
        PerformClick performClick = this.mPerformClick;
        if (performClick != null) {
            removeCallbacks(performClick);
        }
        Runnable runnable2 = this.mTouchModeReset;
        if (runnable2 != null) {
            removeCallbacks(runnable2);
            this.mTouchModeReset.run();
        }
        this.mIsDetaching = false;
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        int touchMode = !isInTouchMode();
        if (!hasWindowFocus) {
            setChildrenDrawingCacheEnabled(false);
            FlingRunnable flingRunnable = this.mFlingRunnable;
            if (flingRunnable != null) {
                removeCallbacks(flingRunnable);
                this.mFlingRunnable.mSuppressIdleStateChangeCall = false;
                this.mFlingRunnable.endFling();
                AbsPositionScroller absPositionScroller = this.mPositionScroller;
                if (absPositionScroller != null) {
                    absPositionScroller.stop();
                }
                if (this.mScrollY != 0) {
                    this.mScrollY = 0;
                    invalidateParentCaches();
                    finishGlows();
                    invalidate();
                }
            }
            dismissPopup();
            if (touchMode == 1) {
                this.mResurrectToPosition = this.mSelectedPosition;
            }
        } else {
            if (this.mFiltered && !this.mPopupHidden) {
                showPopup();
            }
            int i = this.mLastTouchMode;
            if (touchMode != i && i != -1) {
                if (touchMode == 1) {
                    resurrectSelection();
                } else {
                    hideSelector();
                    this.mLayoutMode = 0;
                    layoutChildren();
                }
            }
        }
        this.mLastTouchMode = touchMode;
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.setScrollbarPosition(getVerticalScrollbarPosition());
        }
    }

    ContextMenu.ContextMenuInfo createContextMenuInfo(View view, int position, long id) {
        return new AdapterView.AdapterContextMenuInfo(view, position, id);
    }

    @Override // android.view.View
    public void onCancelPendingInputEvents() {
        super.onCancelPendingInputEvents();
        PerformClick performClick = this.mPerformClick;
        if (performClick != null) {
            removeCallbacks(performClick);
        }
        CheckForTap checkForTap = this.mPendingCheckForTap;
        if (checkForTap != null) {
            removeCallbacks(checkForTap);
        }
        CheckForLongPress checkForLongPress = this.mPendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
        CheckForKeyLongPress checkForKeyLongPress = this.mPendingCheckForKeyLongPress;
        if (checkForKeyLongPress != null) {
            removeCallbacks(checkForKeyLongPress);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class WindowRunnnable {
        private int mOriginalAttachCount;

        private WindowRunnnable() {
        }

        public void rememberWindowAttachCount() {
            this.mOriginalAttachCount = AbsListView.this.getWindowAttachCount();
        }

        public boolean sameWindow() {
            return AbsListView.this.getWindowAttachCount() == this.mOriginalAttachCount;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class PerformClick extends WindowRunnnable implements Runnable {
        int mClickMotionPosition;

        private PerformClick() {
            super();
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AbsListView.this.mDataChanged) {
                return;
            }
            ListAdapter adapter = AbsListView.this.mAdapter;
            int motionPosition = this.mClickMotionPosition;
            if (adapter != null && AbsListView.this.mItemCount > 0 && motionPosition != -1 && motionPosition < adapter.getCount() && sameWindow() && adapter.isEnabled(motionPosition)) {
                AbsListView absListView = AbsListView.this;
                View view = absListView.getChildAt(motionPosition - absListView.mFirstPosition);
                if (view != null) {
                    AbsListView.this.performItemClick(view, motionPosition, adapter.getItemId(motionPosition));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class CheckForLongPress extends WindowRunnnable implements Runnable {
        private static final int INVALID_COORD = -1;

        /* renamed from: mX */
        private float f516mX;

        /* renamed from: mY */
        private float f517mY;

        private CheckForLongPress() {
            super();
            this.f516mX = -1.0f;
            this.f517mY = -1.0f;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCoords(float x, float y) {
            this.f516mX = x;
            this.f517mY = y;
        }

        @Override // java.lang.Runnable
        public void run() {
            int motionPosition = AbsListView.this.mMotionPosition;
            AbsListView absListView = AbsListView.this;
            View child = absListView.getChildAt(motionPosition - absListView.mFirstPosition);
            if (child != null) {
                int longPressPosition = AbsListView.this.mMotionPosition;
                long longPressId = AbsListView.this.mAdapter.getItemId(AbsListView.this.mMotionPosition);
                boolean handled = false;
                if (sameWindow() && !AbsListView.this.mDataChanged) {
                    float f = this.f516mX;
                    if (f != -1.0f) {
                        float f2 = this.f517mY;
                        if (f2 != -1.0f) {
                            handled = AbsListView.this.performLongPress(child, longPressPosition, longPressId, f, f2);
                        }
                    }
                    handled = AbsListView.this.performLongPress(child, longPressPosition, longPressId);
                }
                if (handled) {
                    AbsListView.this.mHasPerformedLongPress = true;
                    AbsListView.this.mTouchMode = -1;
                    AbsListView.this.setPressed(false);
                    child.setPressed(false);
                    return;
                }
                AbsListView.this.mTouchMode = 2;
            }
        }
    }

    /* loaded from: classes4.dex */
    private class CheckForKeyLongPress extends WindowRunnnable implements Runnable {
        private CheckForKeyLongPress() {
            super();
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AbsListView.this.isPressed() && AbsListView.this.mSelectedPosition >= 0) {
                int index = AbsListView.this.mSelectedPosition - AbsListView.this.mFirstPosition;
                View v = AbsListView.this.getChildAt(index);
                if (!AbsListView.this.mDataChanged) {
                    boolean handled = false;
                    if (sameWindow()) {
                        AbsListView absListView = AbsListView.this;
                        handled = absListView.performLongPress(v, absListView.mSelectedPosition, AbsListView.this.mSelectedRowId);
                    }
                    if (handled) {
                        AbsListView.this.setPressed(false);
                        v.setPressed(false);
                        return;
                    }
                    return;
                }
                AbsListView.this.setPressed(false);
                if (v != null) {
                    v.setPressed(false);
                }
            }
        }
    }

    private boolean performStylusButtonPressAction(MotionEvent ev) {
        View child;
        if (this.mChoiceMode == 3 && this.mChoiceActionMode == null && (child = getChildAt(this.mMotionPosition - this.mFirstPosition)) != null) {
            int longPressPosition = this.mMotionPosition;
            long longPressId = this.mAdapter.getItemId(this.mMotionPosition);
            if (performLongPress(child, longPressPosition, longPressId)) {
                this.mTouchMode = -1;
                setPressed(false);
                child.setPressed(false);
                return true;
            }
        }
        return false;
    }

    boolean performLongPress(View child, int longPressPosition, long longPressId) {
        return performLongPress(child, longPressPosition, longPressId, -1.0f, -1.0f);
    }

    boolean performLongPress(View child, int longPressPosition, long longPressId, float x, float y) {
        if (this.mChoiceMode == 3) {
            if (this.mChoiceActionMode == null) {
                ActionMode startActionMode = startActionMode(this.mMultiChoiceModeCallback);
                this.mChoiceActionMode = startActionMode;
                if (startActionMode != null) {
                    setItemChecked(longPressPosition, true);
                    performHapticFeedback(0);
                }
            }
            return true;
        }
        boolean handled = false;
        if (this.mOnItemLongClickListener != null) {
            handled = this.mOnItemLongClickListener.onItemLongClick(this, child, longPressPosition, longPressId);
        }
        if (!handled) {
            this.mContextMenuInfo = createContextMenuInfo(child, longPressPosition, longPressId);
            if (x != -1.0f && y != -1.0f) {
                handled = super.showContextMenuForChild(this, x, y);
            } else {
                handled = super.showContextMenuForChild(this);
            }
        }
        if (handled) {
            performHapticFeedback(0);
        }
        return handled;
    }

    @Override // android.view.View
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return this.mContextMenuInfo;
    }

    @Override // android.view.View
    public boolean showContextMenu() {
        return showContextMenuInternal(0.0f, 0.0f, false);
    }

    @Override // android.view.View
    public boolean showContextMenu(float x, float y) {
        return showContextMenuInternal(x, y, true);
    }

    private boolean showContextMenuInternal(float x, float y, boolean useOffsets) {
        int position = pointToPosition((int) x, (int) y);
        if (position != -1) {
            long id = this.mAdapter.getItemId(position);
            View child = getChildAt(position - this.mFirstPosition);
            if (child != null) {
                this.mContextMenuInfo = createContextMenuInfo(child, position, id);
                if (useOffsets) {
                    return super.showContextMenuForChild(this, x, y);
                }
                return super.showContextMenuForChild(this);
            }
        }
        if (useOffsets) {
            return super.showContextMenu(x, y);
        }
        return super.showContextMenu();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean showContextMenuForChild(View originalView) {
        if (isShowingContextMenuWithCoords()) {
            return false;
        }
        return showContextMenuForChildInternal(originalView, 0.0f, 0.0f, false);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean showContextMenuForChild(View originalView, float x, float y) {
        return showContextMenuForChildInternal(originalView, x, y, true);
    }

    private boolean showContextMenuForChildInternal(View originalView, float x, float y, boolean useOffsets) {
        int longPressPosition = getPositionForView(originalView);
        if (longPressPosition < 0) {
            return false;
        }
        long longPressId = this.mAdapter.getItemId(longPressPosition);
        boolean handled = false;
        if (this.mOnItemLongClickListener != null) {
            handled = this.mOnItemLongClickListener.onItemLongClick(this, originalView, longPressPosition, longPressId);
        }
        if (!handled) {
            View child = getChildAt(longPressPosition - this.mFirstPosition);
            this.mContextMenuInfo = createContextMenuInfo(child, longPressPosition, longPressId);
            if (useOffsets) {
                boolean handled2 = super.showContextMenuForChild(originalView, x, y);
                return handled2;
            }
            boolean handled3 = super.showContextMenuForChild(originalView);
            return handled3;
        }
        return handled;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode)) {
            if (!isEnabled()) {
                return true;
            }
            if (isClickable() && isPressed() && this.mSelectedPosition >= 0 && this.mAdapter != null && this.mSelectedPosition < this.mAdapter.getCount()) {
                View view = getChildAt(this.mSelectedPosition - this.mFirstPosition);
                if (view != null) {
                    performItemClick(view, this.mSelectedPosition, this.mSelectedRowId);
                    view.setPressed(false);
                }
                setPressed(false);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSetPressed(boolean pressed) {
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDrawableHotspotChanged(float x, float y) {
    }

    public int pointToPosition(int x, int y) {
        Rect frame = this.mTouchFrame;
        if (frame == null) {
            this.mTouchFrame = new Rect();
            frame = this.mTouchFrame;
        }
        int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() == 0) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return this.mFirstPosition + i;
                }
            }
        }
        return -1;
    }

    public long pointToRowId(int x, int y) {
        int position = pointToPosition(x, y);
        if (position >= 0) {
            return this.mAdapter.getItemId(position);
        }
        return Long.MIN_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class CheckForTap implements Runnable {

        /* renamed from: x */
        float f518x;

        /* renamed from: y */
        float f519y;

        private CheckForTap() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AbsListView.this.mTouchMode == 0) {
                AbsListView.this.mTouchMode = 1;
                AbsListView absListView = AbsListView.this;
                View child = absListView.getChildAt(absListView.mMotionPosition - AbsListView.this.mFirstPosition);
                if (child != null && !child.hasExplicitFocusable()) {
                    AbsListView.this.mLayoutMode = 0;
                    if (!AbsListView.this.mDataChanged) {
                        float[] point = AbsListView.this.mTmpPoint;
                        point[0] = this.f518x;
                        point[1] = this.f519y;
                        AbsListView.this.transformPointToViewLocal(point, child);
                        child.drawableHotspotChanged(point[0], point[1]);
                        child.setPressed(true);
                        AbsListView.this.setPressed(true);
                        AbsListView.this.layoutChildren();
                        AbsListView absListView2 = AbsListView.this;
                        absListView2.positionSelector(absListView2.mMotionPosition, child);
                        AbsListView.this.refreshDrawableState();
                        int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                        boolean longClickable = AbsListView.this.isLongClickable();
                        if (AbsListView.this.mSelector != null) {
                            Drawable d = AbsListView.this.mSelector.getCurrent();
                            if (d != null && (d instanceof TransitionDrawable)) {
                                if (longClickable) {
                                    ((TransitionDrawable) d).startTransition(longPressTimeout);
                                } else {
                                    ((TransitionDrawable) d).resetTransition();
                                }
                            }
                            AbsListView.this.mSelector.setHotspot(this.f518x, this.f519y);
                        }
                        if (longClickable) {
                            if (AbsListView.this.mPendingCheckForLongPress == null) {
                                AbsListView absListView3 = AbsListView.this;
                                absListView3.mPendingCheckForLongPress = new CheckForLongPress();
                            }
                            AbsListView.this.mPendingCheckForLongPress.setCoords(this.f518x, this.f519y);
                            AbsListView.this.mPendingCheckForLongPress.rememberWindowAttachCount();
                            AbsListView absListView4 = AbsListView.this;
                            absListView4.postDelayed(absListView4.mPendingCheckForLongPress, longPressTimeout);
                            return;
                        }
                        AbsListView.this.mTouchMode = 2;
                        return;
                    }
                    AbsListView.this.mTouchMode = 2;
                }
            }
        }
    }

    private boolean startScrollIfNeeded(int x, int y, MotionEvent vtev) {
        int deltaY = y - this.mMotionY;
        int distance = Math.abs(deltaY);
        boolean overscroll = this.mScrollY != 0;
        if ((overscroll || distance > this.mTouchSlop) && (getNestedScrollAxes() & 2) == 0) {
            createScrollingCache();
            if (overscroll) {
                this.mTouchMode = 5;
                this.mMotionCorrection = 0;
            } else {
                this.mTouchMode = 3;
                int i = this.mTouchSlop;
                if (deltaY <= 0) {
                    i = -i;
                }
                this.mMotionCorrection = i;
            }
            removeCallbacks(this.mPendingCheckForLongPress);
            setPressed(false);
            View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
            if (motionView != null) {
                motionView.setPressed(false);
            }
            reportScrollStateChange(1);
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
            scrollIfNeeded(x, y, vtev);
            return true;
        }
        return false;
    }

    private void scrollIfNeeded(int x, int y, MotionEvent vtev) {
        int rawDeltaY;
        int scrollOffsetCorrection;
        int incrementalDeltaY;
        int incrementalDeltaY2;
        int overScrollDistance;
        int incrementalDeltaY3;
        int incrementalDeltaY4;
        int newDirection;
        int motionIndex;
        int motionViewPrevTop;
        boolean atEdge;
        int incrementalDeltaY5;
        VelocityTracker velocityTracker;
        ViewParent parent;
        int rawDeltaY2 = y - this.mMotionY;
        int i = this.mLastY;
        if (i == Integer.MIN_VALUE) {
            rawDeltaY2 -= this.mMotionCorrection;
        }
        int incrementalDeltaY6 = releaseGlow(i != Integer.MIN_VALUE ? y - i : rawDeltaY2, x);
        if (!dispatchNestedPreScroll(0, -incrementalDeltaY6, this.mScrollConsumed, this.mScrollOffset)) {
            rawDeltaY = rawDeltaY2;
            scrollOffsetCorrection = 0;
            incrementalDeltaY = incrementalDeltaY6;
        } else {
            int i2 = this.mScrollConsumed[1];
            int rawDeltaY3 = rawDeltaY2 + i2;
            int i3 = this.mScrollOffset[1];
            int scrollOffsetCorrection2 = -i3;
            int incrementalDeltaY7 = incrementalDeltaY6 + i2;
            if (vtev != null) {
                vtev.offsetLocation(0.0f, i3);
                this.mNestedYOffset += this.mScrollOffset[1];
            }
            rawDeltaY = rawDeltaY3;
            scrollOffsetCorrection = scrollOffsetCorrection2;
            incrementalDeltaY = incrementalDeltaY7;
        }
        int deltaY = rawDeltaY;
        int lastYCorrection = 0;
        int i4 = this.mTouchMode;
        if (i4 == 3) {
            if (this.mScrollStrictSpan == null) {
                this.mScrollStrictSpan = StrictMode.enterCriticalSpan("AbsListView-scroll");
            }
            if (y != this.mLastY) {
                if ((this.mGroupFlags & 524288) == 0 && Math.abs(rawDeltaY) > this.mTouchSlop && (parent = getParent()) != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                int i5 = this.mMotionPosition;
                if (i5 >= 0) {
                    motionIndex = i5 - this.mFirstPosition;
                } else {
                    int motionIndex2 = getChildCount();
                    motionIndex = motionIndex2 / 2;
                }
                View motionView = getChildAt(motionIndex);
                if (motionView == null) {
                    motionViewPrevTop = 0;
                } else {
                    int motionViewPrevTop2 = motionView.getTop();
                    motionViewPrevTop = motionViewPrevTop2;
                }
                if (incrementalDeltaY == 0) {
                    atEdge = false;
                } else {
                    boolean atEdge2 = trackMotionScroll(deltaY, incrementalDeltaY);
                    atEdge = atEdge2;
                }
                View motionView2 = getChildAt(motionIndex);
                if (motionView2 != null) {
                    int motionViewRealTop = motionView2.getTop();
                    if (atEdge) {
                        int overscroll = (-incrementalDeltaY) - (motionViewRealTop - motionViewPrevTop);
                        if (dispatchNestedScroll(0, overscroll - incrementalDeltaY, 0, overscroll, this.mScrollOffset)) {
                            int i6 = this.mScrollOffset[1];
                            lastYCorrection = 0 - i6;
                            if (vtev != null) {
                                vtev.offsetLocation(0.0f, i6);
                                this.mNestedYOffset += this.mScrollOffset[1];
                            }
                            incrementalDeltaY5 = incrementalDeltaY;
                        } else {
                            int incrementalDeltaY8 = incrementalDeltaY;
                            boolean atOverscrollEdge = overScrollBy(0, overscroll, 0, this.mScrollY, 0, 0, 0, this.mOverscrollDistance, true);
                            if (atOverscrollEdge && (velocityTracker = this.mVelocityTracker) != null) {
                                velocityTracker.clear();
                            }
                            int overscrollMode = getOverScrollMode();
                            if (overscrollMode != 0) {
                                if (overscrollMode != 1) {
                                    incrementalDeltaY5 = incrementalDeltaY8;
                                } else if (contentFits()) {
                                    incrementalDeltaY5 = incrementalDeltaY8;
                                }
                            }
                            if (!atOverscrollEdge) {
                                this.mDirection = 0;
                                this.mTouchMode = 5;
                            }
                            incrementalDeltaY5 = incrementalDeltaY8;
                            if (incrementalDeltaY5 > 0) {
                                this.mEdgeGlowTop.onPullDistance((-overscroll) / getHeight(), x / getWidth());
                                if (!this.mEdgeGlowBottom.isFinished()) {
                                    this.mEdgeGlowBottom.onRelease();
                                }
                                invalidateEdgeEffects();
                            } else if (incrementalDeltaY5 < 0) {
                                this.mEdgeGlowBottom.onPullDistance(overscroll / getHeight(), 1.0f - (x / getWidth()));
                                if (!this.mEdgeGlowTop.isFinished()) {
                                    this.mEdgeGlowTop.onRelease();
                                }
                                invalidateEdgeEffects();
                            }
                        }
                    } else {
                        incrementalDeltaY5 = incrementalDeltaY;
                    }
                    this.mMotionY = y + lastYCorrection + scrollOffsetCorrection;
                } else {
                    incrementalDeltaY5 = incrementalDeltaY;
                }
                this.mLastY = y + lastYCorrection + scrollOffsetCorrection;
                return;
            }
            incrementalDeltaY2 = incrementalDeltaY;
        } else {
            incrementalDeltaY2 = incrementalDeltaY;
            if (i4 == 5 && y != this.mLastY) {
                int oldScroll = this.mScrollY;
                int newScroll = oldScroll - incrementalDeltaY2;
                int newDirection2 = y > this.mLastY ? 1 : -1;
                if (this.mDirection == 0) {
                    this.mDirection = newDirection2;
                }
                int overScrollDistance2 = -incrementalDeltaY2;
                if ((newScroll < 0 && oldScroll >= 0) || (newScroll > 0 && oldScroll <= 0)) {
                    int overScrollDistance3 = -oldScroll;
                    overScrollDistance = overScrollDistance3;
                    incrementalDeltaY3 = incrementalDeltaY2 + overScrollDistance3;
                } else {
                    overScrollDistance = overScrollDistance2;
                    incrementalDeltaY3 = 0;
                }
                if (overScrollDistance == 0) {
                    incrementalDeltaY4 = incrementalDeltaY3;
                    newDirection = newDirection2;
                } else {
                    incrementalDeltaY4 = incrementalDeltaY3;
                    int overScrollDistance4 = overScrollDistance;
                    newDirection = newDirection2;
                    overScrollBy(0, overScrollDistance, 0, this.mScrollY, 0, 0, 0, this.mOverscrollDistance, true);
                    int overscrollMode2 = getOverScrollMode();
                    if (overscrollMode2 == 0 || (overscrollMode2 == 1 && !contentFits())) {
                        if (rawDeltaY > 0) {
                            this.mEdgeGlowTop.onPullDistance(overScrollDistance4 / getHeight(), x / getWidth());
                            if (!this.mEdgeGlowBottom.isFinished()) {
                                this.mEdgeGlowBottom.onRelease();
                            }
                            invalidateEdgeEffects();
                        } else if (rawDeltaY < 0) {
                            this.mEdgeGlowBottom.onPullDistance((-overScrollDistance4) / getHeight(), 1.0f - (x / getWidth()));
                            if (!this.mEdgeGlowTop.isFinished()) {
                                this.mEdgeGlowTop.onRelease();
                            }
                            invalidateEdgeEffects();
                        }
                    }
                }
                if (incrementalDeltaY4 != 0) {
                    if (this.mScrollY != 0) {
                        this.mScrollY = 0;
                        invalidateParentIfNeeded();
                    }
                    trackMotionScroll(incrementalDeltaY4, incrementalDeltaY4);
                    this.mTouchMode = 3;
                    int motionPosition = findClosestMotionRow(y);
                    this.mMotionCorrection = 0;
                    View motionView3 = getChildAt(motionPosition - this.mFirstPosition);
                    this.mMotionViewOriginalTop = motionView3 != null ? motionView3.getTop() : 0;
                    this.mMotionY = y + scrollOffsetCorrection;
                    this.mMotionPosition = motionPosition;
                }
                this.mLastY = y + 0 + scrollOffsetCorrection;
                this.mDirection = newDirection;
            }
        }
    }

    private int releaseGlow(int deltaY, int x) {
        float consumed = 0.0f;
        if (this.mEdgeGlowTop.getDistance() != 0.0f) {
            if (canScrollUp()) {
                this.mEdgeGlowTop.onRelease();
            } else {
                consumed = this.mEdgeGlowTop.onPullDistance(deltaY / getHeight(), x / getWidth());
            }
            invalidateEdgeEffects();
        } else if (this.mEdgeGlowBottom.getDistance() != 0.0f) {
            if (canScrollDown()) {
                this.mEdgeGlowBottom.onRelease();
            } else {
                consumed = -this.mEdgeGlowBottom.onPullDistance((-deltaY) / getHeight(), 1.0f - (x / getWidth()));
            }
            invalidateEdgeEffects();
        }
        int pixelsConsumed = Math.round(getHeight() * consumed);
        return deltaY - pixelsConsumed;
    }

    private boolean doesTouchStopStretch() {
        return ((this.mEdgeGlowBottom.getDistance() == 0.0f || canScrollDown()) && (this.mEdgeGlowTop.getDistance() == 0.0f || canScrollUp())) ? false : true;
    }

    private void invalidateEdgeEffects() {
        if (!shouldDisplayEdgeEffects()) {
            return;
        }
        invalidate();
    }

    @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
    public void onTouchModeChanged(boolean isInTouchMode) {
        if (isInTouchMode) {
            hideSelector();
            if (getHeight() > 0 && getChildCount() > 0) {
                layoutChildren();
            }
            updateSelectorState();
            return;
        }
        int touchMode = this.mTouchMode;
        if (touchMode == 5 || touchMode == 6) {
            FlingRunnable flingRunnable = this.mFlingRunnable;
            if (flingRunnable != null) {
                flingRunnable.endFling();
            }
            AbsPositionScroller absPositionScroller = this.mPositionScroller;
            if (absPositionScroller != null) {
                absPositionScroller.stop();
            }
            if (this.mScrollY != 0) {
                this.mScrollY = 0;
                invalidateParentCaches();
                finishGlows();
                invalidate();
            }
        }
    }

    @Override // android.view.View
    protected boolean handleScrollBarDragging(MotionEvent event) {
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return isClickable() || isLongClickable();
        }
        AbsPositionScroller absPositionScroller = this.mPositionScroller;
        if (absPositionScroller != null) {
            absPositionScroller.stop();
        }
        if (this.mIsDetaching || !isAttachedToWindow()) {
            return false;
        }
        startNestedScroll(2);
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller == null || !fastScroller.onTouchEvent(ev)) {
            initVelocityTrackerIfNotExists();
            MotionEvent vtev = MotionEvent.obtain(ev);
            int actionMasked = ev.getActionMasked();
            if (actionMasked == 0) {
                this.mNestedYOffset = 0;
            }
            vtev.offsetLocation(0.0f, this.mNestedYOffset);
            switch (actionMasked) {
                case 0:
                    onTouchDown(ev);
                    break;
                case 1:
                    onTouchUp(ev);
                    break;
                case 2:
                    onTouchMove(ev, vtev);
                    break;
                case 3:
                    onTouchCancel();
                    break;
                case 5:
                    int index = ev.getActionIndex();
                    int id = ev.getPointerId(index);
                    int x = (int) ev.getX(index);
                    int y = (int) ev.getY(index);
                    this.mMotionCorrection = 0;
                    this.mActivePointerId = id;
                    this.mMotionX = x;
                    this.mMotionY = y;
                    int motionPosition = pointToPosition(x, y);
                    if (motionPosition >= 0) {
                        View child = getChildAt(motionPosition - this.mFirstPosition);
                        this.mMotionViewOriginalTop = child.getTop();
                        this.mMotionPosition = motionPosition;
                    }
                    this.mLastY = y;
                    break;
                case 6:
                    onSecondaryPointerUp(ev);
                    int x2 = this.mMotionX;
                    int y2 = this.mMotionY;
                    int motionPosition2 = pointToPosition(x2, y2);
                    if (motionPosition2 >= 0) {
                        View child2 = getChildAt(motionPosition2 - this.mFirstPosition);
                        this.mMotionViewOriginalTop = child2.getTop();
                        this.mMotionPosition = motionPosition2;
                    }
                    this.mLastY = y2;
                    break;
            }
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.addMovement(vtev);
            }
            vtev.recycle();
            return true;
        }
        return true;
    }

    private void onTouchDown(MotionEvent ev) {
        this.mHasPerformedLongPress = false;
        this.mActivePointerId = ev.getPointerId(0);
        hideSelector();
        if (this.mTouchMode == 6) {
            FlingRunnable flingRunnable = this.mFlingRunnable;
            if (flingRunnable != null) {
                flingRunnable.endFling();
            }
            AbsPositionScroller absPositionScroller = this.mPositionScroller;
            if (absPositionScroller != null) {
                absPositionScroller.stop();
            }
            this.mTouchMode = 5;
            this.mMotionX = (int) ev.getX();
            int y = (int) ev.getY();
            this.mMotionY = y;
            this.mLastY = y;
            this.mMotionCorrection = 0;
            this.mDirection = 0;
            stopEdgeGlowRecede(ev.getX());
        } else {
            int x = (int) ev.getX();
            int y2 = (int) ev.getY();
            int motionPosition = pointToPosition(x, y2);
            if (!this.mDataChanged) {
                if (this.mTouchMode == 4) {
                    createScrollingCache();
                    this.mTouchMode = 3;
                    this.mMotionCorrection = 0;
                    motionPosition = findMotionRow(y2);
                    FlingRunnable flingRunnable2 = this.mFlingRunnable;
                    if (flingRunnable2 != null) {
                        flingRunnable2.flywheelTouch();
                    }
                    stopEdgeGlowRecede(x);
                } else if (motionPosition >= 0 && getAdapter().isEnabled(motionPosition)) {
                    this.mTouchMode = 0;
                    if (this.mPendingCheckForTap == null) {
                        this.mPendingCheckForTap = new CheckForTap();
                    }
                    this.mPendingCheckForTap.f518x = ev.getX();
                    this.mPendingCheckForTap.f519y = ev.getY();
                    postDelayed(this.mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                }
            }
            if (motionPosition >= 0) {
                View v = getChildAt(motionPosition - this.mFirstPosition);
                this.mMotionViewOriginalTop = v.getTop();
            }
            this.mMotionX = x;
            this.mMotionY = y2;
            this.mMotionPosition = motionPosition;
            this.mLastY = Integer.MIN_VALUE;
        }
        if (this.mTouchMode == 0 && this.mMotionPosition != -1 && performButtonActionOnTouchDown(ev)) {
            removeCallbacks(this.mPendingCheckForTap);
        }
    }

    private void stopEdgeGlowRecede(float x) {
        if (this.mEdgeGlowTop.getDistance() != 0.0f) {
            this.mEdgeGlowTop.onPullDistance(0.0f, x / getWidth());
        }
        if (this.mEdgeGlowBottom.getDistance() != 0.0f) {
            this.mEdgeGlowBottom.onPullDistance(0.0f, x / getWidth());
        }
    }

    private void onTouchMove(MotionEvent ev, MotionEvent vtev) {
        if (this.mHasPerformedLongPress) {
            return;
        }
        int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
        if (pointerIndex == -1) {
            pointerIndex = 0;
            this.mActivePointerId = ev.getPointerId(0);
        }
        if (this.mDataChanged) {
            layoutChildren();
        }
        int y = (int) ev.getY(pointerIndex);
        switch (this.mTouchMode) {
            case 0:
            case 1:
            case 2:
                if (!startScrollIfNeeded((int) ev.getX(pointerIndex), y, vtev)) {
                    View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
                    float x = ev.getX(pointerIndex);
                    if (!pointInView(x, y, this.mTouchSlop)) {
                        setPressed(false);
                        if (motionView != null) {
                            motionView.setPressed(false);
                        }
                        removeCallbacks(this.mTouchMode == 0 ? this.mPendingCheckForTap : this.mPendingCheckForLongPress);
                        this.mTouchMode = 2;
                        updateSelectorState();
                        return;
                    } else if (motionView != null) {
                        float[] point = this.mTmpPoint;
                        point[0] = x;
                        point[1] = y;
                        transformPointToViewLocal(point, motionView);
                        motionView.drawableHotspotChanged(point[0], point[1]);
                        return;
                    } else {
                        return;
                    }
                }
                return;
            case 3:
            case 5:
                scrollIfNeeded((int) ev.getX(pointerIndex), y, vtev);
                return;
            case 4:
            default:
                return;
        }
    }

    private void onTouchUp(MotionEvent ev) {
        Runnable runnable;
        switch (this.mTouchMode) {
            case 0:
            case 1:
            case 2:
                int childCount = this.mMotionPosition;
                final View child = getChildAt(childCount - this.mFirstPosition);
                if (child != null) {
                    if (this.mTouchMode != 0) {
                        child.setPressed(false);
                    }
                    float x = ev.getX();
                    boolean inList = x > ((float) this.mListPadding.left) && x < ((float) (getWidth() - this.mListPadding.right));
                    if (inList && !child.hasExplicitFocusable()) {
                        if (this.mPerformClick == null) {
                            this.mPerformClick = new PerformClick();
                        }
                        final PerformClick performClick = this.mPerformClick;
                        performClick.mClickMotionPosition = childCount;
                        performClick.rememberWindowAttachCount();
                        this.mResurrectToPosition = childCount;
                        int i = this.mTouchMode;
                        if (i == 0 || i == 1) {
                            if (i != 0) {
                                runnable = this.mPendingCheckForLongPress;
                            } else {
                                runnable = this.mPendingCheckForTap;
                            }
                            removeCallbacks(runnable);
                            this.mLayoutMode = 0;
                            if (!this.mDataChanged && this.mAdapter.isEnabled(childCount)) {
                                this.mTouchMode = 1;
                                setSelectedPositionInt(this.mMotionPosition);
                                layoutChildren();
                                child.setPressed(true);
                                positionSelector(this.mMotionPosition, child);
                                setPressed(true);
                                Drawable drawable = this.mSelector;
                                if (drawable != null) {
                                    Drawable d = drawable.getCurrent();
                                    if (d != null && (d instanceof TransitionDrawable)) {
                                        ((TransitionDrawable) d).resetTransition();
                                    }
                                    this.mSelector.setHotspot(x, ev.getY());
                                }
                                Runnable runnable2 = this.mTouchModeReset;
                                if (runnable2 != null) {
                                    removeCallbacks(runnable2);
                                }
                                Runnable runnable3 = new Runnable() { // from class: android.widget.AbsListView.3
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        AbsListView.this.mTouchModeReset = null;
                                        AbsListView.this.mTouchMode = -1;
                                        child.setPressed(false);
                                        AbsListView.this.setPressed(false);
                                        if (!AbsListView.this.mDataChanged && !AbsListView.this.mIsDetaching && AbsListView.this.isAttachedToWindow()) {
                                            performClick.run();
                                        }
                                    }
                                };
                                this.mTouchModeReset = runnable3;
                                postDelayed(runnable3, ViewConfiguration.getPressedStateDuration());
                                return;
                            }
                            this.mTouchMode = -1;
                            updateSelectorState();
                            return;
                        } else if (!this.mDataChanged && this.mAdapter.isEnabled(childCount)) {
                            performClick.run();
                        }
                    }
                }
                this.mTouchMode = -1;
                updateSelectorState();
                break;
            case 3:
                int childCount2 = getChildCount();
                if (childCount2 > 0) {
                    int firstChildTop = getChildAt(0).getTop();
                    int lastChildBottom = getChildAt(childCount2 - 1).getBottom();
                    int contentTop = this.mListPadding.top;
                    int contentBottom = getHeight() - this.mListPadding.bottom;
                    if (this.mFirstPosition == 0 && firstChildTop >= contentTop && this.mFirstPosition + childCount2 < this.mItemCount && lastChildBottom <= getHeight() - contentBottom) {
                        this.mTouchMode = -1;
                        reportScrollStateChange(0);
                        break;
                    } else {
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
                        int initialVelocity = (int) (velocityTracker.getYVelocity(this.mActivePointerId) * this.mVelocityScale);
                        boolean flingVelocity = Math.abs(initialVelocity) > this.mMinimumVelocity;
                        if (flingVelocity && !this.mEdgeGlowTop.isFinished()) {
                            if (shouldAbsorb(this.mEdgeGlowTop, initialVelocity)) {
                                this.mEdgeGlowTop.onAbsorb(initialVelocity);
                                break;
                            } else {
                                if (this.mFlingRunnable == null) {
                                    this.mFlingRunnable = new FlingRunnable();
                                }
                                this.mFlingRunnable.start(-initialVelocity);
                                break;
                            }
                        } else if (!flingVelocity || this.mEdgeGlowBottom.isFinished()) {
                            if (flingVelocity && ((this.mFirstPosition != 0 || firstChildTop != contentTop - this.mOverscrollDistance) && (this.mFirstPosition + childCount2 != this.mItemCount || lastChildBottom != this.mOverscrollDistance + contentBottom))) {
                                if (!dispatchNestedPreFling(0.0f, -initialVelocity)) {
                                    if (this.mFlingRunnable == null) {
                                        this.mFlingRunnable = new FlingRunnable();
                                    }
                                    reportScrollStateChange(2);
                                    this.mFlingRunnable.start(-initialVelocity);
                                    dispatchNestedFling(0.0f, -initialVelocity, true);
                                    break;
                                } else {
                                    this.mTouchMode = -1;
                                    reportScrollStateChange(0);
                                    break;
                                }
                            } else {
                                this.mTouchMode = -1;
                                reportScrollStateChange(0);
                                FlingRunnable flingRunnable = this.mFlingRunnable;
                                if (flingRunnable != null) {
                                    flingRunnable.endFling();
                                }
                                AbsPositionScroller absPositionScroller = this.mPositionScroller;
                                if (absPositionScroller != null) {
                                    absPositionScroller.stop();
                                }
                                if (flingVelocity && !dispatchNestedPreFling(0.0f, -initialVelocity)) {
                                    dispatchNestedFling(0.0f, -initialVelocity, false);
                                    break;
                                }
                            }
                        } else if (shouldAbsorb(this.mEdgeGlowBottom, -initialVelocity)) {
                            this.mEdgeGlowBottom.onAbsorb(-initialVelocity);
                            break;
                        } else {
                            if (this.mFlingRunnable == null) {
                                this.mFlingRunnable = new FlingRunnable();
                            }
                            this.mFlingRunnable.start(-initialVelocity);
                            break;
                        }
                    }
                } else {
                    this.mTouchMode = -1;
                    reportScrollStateChange(0);
                    break;
                }
                break;
            case 5:
                if (this.mFlingRunnable == null) {
                    this.mFlingRunnable = new FlingRunnable();
                }
                VelocityTracker velocityTracker2 = this.mVelocityTracker;
                velocityTracker2.computeCurrentVelocity(1000, this.mMaximumVelocity);
                int initialVelocity2 = (int) velocityTracker2.getYVelocity(this.mActivePointerId);
                reportScrollStateChange(2);
                if (Math.abs(initialVelocity2) > this.mMinimumVelocity) {
                    this.mFlingRunnable.startOverfling(-initialVelocity2);
                    break;
                } else {
                    this.mFlingRunnable.startSpringback();
                    break;
                }
        }
        setPressed(false);
        if (shouldDisplayEdgeEffects()) {
            this.mEdgeGlowTop.onRelease();
            this.mEdgeGlowBottom.onRelease();
        }
        invalidate();
        removeCallbacks(this.mPendingCheckForLongPress);
        recycleVelocityTracker();
        this.mActivePointerId = -1;
        StrictMode.Span span = this.mScrollStrictSpan;
        if (span != null) {
            span.finish();
            this.mScrollStrictSpan = null;
        }
    }

    private boolean shouldAbsorb(EdgeEffect edgeEffect, int velocity) {
        if (velocity > 0) {
            return true;
        }
        float distance = edgeEffect.getDistance() * getHeight();
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        float flingDistance = this.mFlingRunnable.getSplineFlingDistance(-velocity);
        return flingDistance < distance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int consumeFlingInStretch(int unconsumed) {
        EdgeEffect edgeEffect;
        EdgeEffect edgeEffect2;
        if (unconsumed < 0 && (edgeEffect2 = this.mEdgeGlowTop) != null && edgeEffect2.getDistance() != 0.0f) {
            int size = getHeight();
            float deltaDistance = (unconsumed * FLING_DESTRETCH_FACTOR) / size;
            int consumed = Math.round((size / FLING_DESTRETCH_FACTOR) * this.mEdgeGlowTop.onPullDistance(deltaDistance, 0.5f));
            if (consumed != unconsumed) {
                this.mEdgeGlowTop.finish();
            }
            return unconsumed - consumed;
        } else if (unconsumed > 0 && (edgeEffect = this.mEdgeGlowBottom) != null && edgeEffect.getDistance() != 0.0f) {
            int size2 = getHeight();
            float deltaDistance2 = ((-unconsumed) * FLING_DESTRETCH_FACTOR) / size2;
            int consumed2 = Math.round(((-size2) / FLING_DESTRETCH_FACTOR) * this.mEdgeGlowBottom.onPullDistance(deltaDistance2, 0.5f));
            if (consumed2 != unconsumed) {
                this.mEdgeGlowBottom.finish();
            }
            return unconsumed - consumed2;
        } else {
            return unconsumed;
        }
    }

    private boolean shouldDisplayEdgeEffects() {
        return getOverScrollMode() != 2;
    }

    private void onTouchCancel() {
        switch (this.mTouchMode) {
            case 5:
                if (this.mFlingRunnable == null) {
                    this.mFlingRunnable = new FlingRunnable();
                }
                this.mFlingRunnable.startSpringback();
                break;
            case 6:
                break;
            default:
                this.mTouchMode = -1;
                setPressed(false);
                View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
                if (motionView != null) {
                    motionView.setPressed(false);
                }
                clearScrollingCache();
                removeCallbacks(this.mPendingCheckForLongPress);
                recycleVelocityTracker();
                break;
        }
        if (shouldDisplayEdgeEffects()) {
            this.mEdgeGlowTop.onRelease();
            this.mEdgeGlowBottom.onRelease();
        }
        this.mActivePointerId = -1;
    }

    @Override // android.view.View
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (this.mScrollY != scrollY) {
            onScrollChanged(this.mScrollX, scrollY, this.mScrollX, this.mScrollY);
            this.mScrollY = scrollY;
            invalidateParentIfNeeded();
            awakenScrollBars();
        }
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        float axisValue;
        int actionButton;
        int i;
        switch (event.getAction()) {
            case 8:
                if (event.isFromSource(2)) {
                    axisValue = event.getAxisValue(9);
                } else if (event.isFromSource(4194304)) {
                    axisValue = event.getAxisValue(26);
                } else {
                    axisValue = 0.0f;
                }
                int delta = Math.round(this.mVerticalScrollFactor * axisValue);
                if (delta != 0) {
                    int motionIndex = delta > 0 ? 0 : getChildCount() - 1;
                    int motionViewPrevTop = 0;
                    View motionView = getChildAt(motionIndex);
                    if (motionView != null) {
                        motionViewPrevTop = motionView.getTop();
                    }
                    int overscrollMode = getOverScrollMode();
                    if (!trackMotionScroll(delta, delta)) {
                        return true;
                    }
                    if (!event.isFromSource(8194) && motionView != null && (overscrollMode == 0 || (overscrollMode == 1 && !contentFits()))) {
                        int motionViewRealTop = motionView.getTop();
                        float overscroll = (delta - (motionViewRealTop - motionViewPrevTop)) / getHeight();
                        if (delta > 0) {
                            this.mEdgeGlowTop.onPullDistance(overscroll, 0.5f);
                            this.mEdgeGlowTop.onRelease();
                        } else {
                            this.mEdgeGlowBottom.onPullDistance(-overscroll, 0.5f);
                            this.mEdgeGlowBottom.onRelease();
                        }
                        invalidate();
                        return true;
                    }
                }
                break;
            case 11:
                if (event.isFromSource(2) && (((actionButton = event.getActionButton()) == 32 || actionButton == 2) && (((i = this.mTouchMode) == 0 || i == 1) && performStylusButtonPressAction(event)))) {
                    removeCallbacks(this.mPendingCheckForLongPress);
                    removeCallbacks(this.mPendingCheckForTap);
                    break;
                }
                break;
        }
        return super.onGenericMotionEvent(event);
    }

    public void fling(int velocityY) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        reportScrollStateChange(2);
        this.mFlingRunnable.start(velocityY);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & 2) != 0;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(2);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        int myUnconsumed;
        int myConsumed;
        int motionIndex = getChildCount() / 2;
        View motionView = getChildAt(motionIndex);
        int oldTop = motionView != null ? motionView.getTop() : 0;
        if (motionView == null || trackMotionScroll(-dyUnconsumed, -dyUnconsumed)) {
            if (motionView == null) {
                myUnconsumed = dyUnconsumed;
                myConsumed = 0;
            } else {
                int myConsumed2 = motionView.getTop() - oldTop;
                int myUnconsumed2 = dyUnconsumed - myConsumed2;
                myUnconsumed = myUnconsumed2;
                myConsumed = myConsumed2;
            }
            dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        int childCount = getChildCount();
        if (!consumed && childCount > 0 && canScrollList((int) velocityY) && Math.abs(velocityY) > this.mMinimumVelocity) {
            reportScrollStateChange(2);
            if (this.mFlingRunnable == null) {
                this.mFlingRunnable = new FlingRunnable();
            }
            if (!dispatchNestedPreFling(0.0f, velocityY)) {
                this.mFlingRunnable.start((int) velocityY);
                return true;
            }
            return true;
        }
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        int width;
        int height;
        int translateX;
        int translateY;
        super.draw(canvas);
        if (shouldDisplayEdgeEffects()) {
            int scrollY = this.mScrollY;
            boolean clipToPadding = getClipToPadding();
            if (clipToPadding) {
                width = (getWidth() - this.mPaddingLeft) - this.mPaddingRight;
                height = (getHeight() - this.mPaddingTop) - this.mPaddingBottom;
                translateX = this.mPaddingLeft;
                translateY = this.mPaddingTop;
            } else {
                width = getWidth();
                height = getHeight();
                translateX = 0;
                translateY = 0;
            }
            this.mEdgeGlowTop.setSize(width, height);
            this.mEdgeGlowBottom.setSize(width, height);
            if (!this.mEdgeGlowTop.isFinished()) {
                int restoreCount = canvas.save();
                canvas.clipRect(translateX, translateY, translateX + width, this.mEdgeGlowTop.getMaxHeight() + translateY);
                int edgeY = Math.min(0, this.mFirstPositionDistanceGuess + scrollY) + translateY;
                canvas.translate(translateX, edgeY);
                if (this.mEdgeGlowTop.draw(canvas)) {
                    invalidateEdgeEffects();
                }
                canvas.restoreToCount(restoreCount);
            }
            if (!this.mEdgeGlowBottom.isFinished()) {
                int restoreCount2 = canvas.save();
                canvas.clipRect(translateX, (translateY + height) - this.mEdgeGlowBottom.getMaxHeight(), translateX + width, translateY + height);
                int edgeX = (-width) + translateX;
                int edgeY2 = Math.max(getHeight(), this.mLastPositionDistanceGuess + scrollY) - (clipToPadding ? this.mPaddingBottom : 0);
                canvas.translate(edgeX, edgeY2);
                canvas.rotate(180.0f, width, 0.0f);
                if (this.mEdgeGlowBottom.draw(canvas)) {
                    invalidateEdgeEffects();
                }
                canvas.restoreToCount(restoreCount2);
            }
        }
    }

    private void initOrResetVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptHoverEvent(MotionEvent event) {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null && fastScroller.onInterceptHoverEvent(event)) {
            return true;
        }
        return super.onInterceptHoverEvent(event);
    }

    @Override // android.view.ViewGroup, android.view.View
    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        PointerIcon pointerIcon;
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null && (pointerIcon = fastScroller.onResolvePointerIcon(event, pointerIndex)) != null) {
            return pointerIcon;
        }
        return super.onResolvePointerIcon(event, pointerIndex);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int actionMasked = ev.getActionMasked();
        AbsPositionScroller absPositionScroller = this.mPositionScroller;
        if (absPositionScroller != null) {
            absPositionScroller.stop();
        }
        if (this.mIsDetaching || !isAttachedToWindow()) {
            return false;
        }
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller == null || !fastScroller.onInterceptTouchEvent(ev)) {
            switch (actionMasked) {
                case 0:
                    int touchMode = this.mTouchMode;
                    if (touchMode == 6 || touchMode == 5) {
                        this.mMotionCorrection = 0;
                        return true;
                    }
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    this.mActivePointerId = ev.getPointerId(0);
                    int motionPosition = findMotionRow(y);
                    if (doesTouchStopStretch()) {
                        this.mTouchMode = 4;
                        touchMode = 4;
                    } else if (touchMode != 4 && motionPosition >= 0) {
                        View v = getChildAt(motionPosition - this.mFirstPosition);
                        this.mMotionViewOriginalTop = v.getTop();
                        this.mMotionX = x;
                        this.mMotionY = y;
                        this.mMotionPosition = motionPosition;
                        this.mTouchMode = 0;
                        clearScrollingCache();
                    }
                    this.mLastY = Integer.MIN_VALUE;
                    initOrResetVelocityTracker();
                    this.mVelocityTracker.addMovement(ev);
                    this.mNestedYOffset = 0;
                    startNestedScroll(2);
                    if (touchMode == 4) {
                        return true;
                    }
                    break;
                case 1:
                case 3:
                    this.mTouchMode = -1;
                    this.mActivePointerId = -1;
                    recycleVelocityTracker();
                    reportScrollStateChange(0);
                    stopNestedScroll();
                    break;
                case 2:
                    switch (this.mTouchMode) {
                        case 0:
                            int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                            if (pointerIndex == -1) {
                                pointerIndex = 0;
                                this.mActivePointerId = ev.getPointerId(0);
                            }
                            initVelocityTrackerIfNotExists();
                            this.mVelocityTracker.addMovement(ev);
                            if (startScrollIfNeeded((int) ev.getX(pointerIndex), (int) ev.getY(pointerIndex), null)) {
                                return true;
                            }
                            break;
                    }
                case 6:
                    onSecondaryPointerUp(ev);
                    break;
            }
            return false;
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = (ev.getAction() & 65280) >> 8;
        int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mMotionX = (int) ev.getX(newPointerIndex);
            this.mMotionY = (int) ev.getY(newPointerIndex);
            this.mMotionCorrection = 0;
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addTouchables(ArrayList<View> views) {
        int count = getChildCount();
        int firstPosition = this.mFirstPosition;
        ListAdapter adapter = this.mAdapter;
        if (adapter == null) {
            return;
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (adapter.isEnabled(firstPosition + i)) {
                views.add(child);
            }
            child.addTouchables(views);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reportScrollStateChange(int newState) {
        OnScrollListener onScrollListener;
        if (newState != this.mLastScrollState && (onScrollListener = this.mOnScrollListener) != null) {
            this.mLastScrollState = newState;
            onScrollListener.onScrollStateChanged(this, newState);
        }
        if (newState == 0 || newState == 2) {
            this.mReportChildrenToContentCaptureOnNextUpdate = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class FlingRunnable implements Runnable {
        private static final int FLYWHEEL_TIMEOUT = 40;
        private final Runnable mCheckFlywheel = new Runnable() { // from class: android.widget.AbsListView.FlingRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                int activeId = AbsListView.this.mActivePointerId;
                VelocityTracker vt = AbsListView.this.mVelocityTracker;
                OverScroller scroller = FlingRunnable.this.mScroller;
                if (vt == null || activeId == -1) {
                    return;
                }
                vt.computeCurrentVelocity(1000, AbsListView.this.mMaximumVelocity);
                float yvel = -vt.getYVelocity(activeId);
                if (Math.abs(yvel) >= AbsListView.this.mMinimumVelocity && scroller.isScrollingInDirection(0.0f, yvel)) {
                    AbsListView.this.postDelayed(this, 40L);
                    return;
                }
                FlingRunnable.this.endFling();
                AbsListView.this.mTouchMode = 3;
                AbsListView.this.reportScrollStateChange(1);
            }
        };
        private int mLastFlingY;
        private final OverScroller mScroller;
        private boolean mSuppressIdleStateChangeCall;

        FlingRunnable() {
            this.mScroller = new OverScroller(AbsListView.this.getContext());
        }

        float getSplineFlingDistance(int velocity) {
            return (float) this.mScroller.getSplineFlingDistance(velocity);
        }

        void start(int initialVelocity) {
            int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
            this.mLastFlingY = initialY;
            this.mScroller.setInterpolator(null);
            this.mScroller.fling(0, initialY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            AbsListView.this.mTouchMode = 4;
            this.mSuppressIdleStateChangeCall = false;
            AbsListView.this.removeCallbacks(this);
            AbsListView.this.postOnAnimation(this);
            if (AbsListView.this.mFlingStrictSpan == null) {
                AbsListView.this.mFlingStrictSpan = StrictMode.enterCriticalSpan("AbsListView-fling");
            }
        }

        void startSpringback() {
            this.mSuppressIdleStateChangeCall = false;
            if (this.mScroller.springBack(0, AbsListView.this.mScrollY, 0, 0, 0, 0)) {
                AbsListView.this.mTouchMode = 6;
                AbsListView.this.invalidate();
                AbsListView.this.postOnAnimation(this);
                return;
            }
            AbsListView.this.mTouchMode = -1;
            AbsListView.this.reportScrollStateChange(0);
        }

        void startOverfling(int initialVelocity) {
            this.mScroller.setInterpolator(null);
            this.mScroller.fling(0, AbsListView.this.mScrollY, 0, initialVelocity, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, AbsListView.this.getHeight());
            AbsListView.this.mTouchMode = 6;
            this.mSuppressIdleStateChangeCall = false;
            AbsListView.this.invalidate();
            AbsListView.this.postOnAnimation(this);
        }

        void edgeReached(int delta) {
            this.mScroller.notifyVerticalEdgeReached(AbsListView.this.mScrollY, 0, AbsListView.this.mOverflingDistance);
            int overscrollMode = AbsListView.this.getOverScrollMode();
            if (overscrollMode == 0 || (overscrollMode == 1 && !AbsListView.this.contentFits())) {
                AbsListView.this.mTouchMode = 6;
                int vel = (int) this.mScroller.getCurrVelocity();
                if (delta > 0) {
                    AbsListView.this.mEdgeGlowTop.onAbsorb(vel);
                } else {
                    AbsListView.this.mEdgeGlowBottom.onAbsorb(vel);
                }
            } else {
                AbsListView.this.mTouchMode = -1;
                if (AbsListView.this.mPositionScroller != null) {
                    AbsListView.this.mPositionScroller.stop();
                }
            }
            AbsListView.this.invalidate();
            AbsListView.this.postOnAnimation(this);
        }

        void startScroll(int distance, int duration, boolean linear, boolean suppressEndFlingStateChangeCall) {
            int initialY = distance < 0 ? Integer.MAX_VALUE : 0;
            this.mLastFlingY = initialY;
            this.mScroller.setInterpolator(linear ? AbsListView.sLinearInterpolator : null);
            this.mScroller.startScroll(0, initialY, 0, distance, duration);
            AbsListView.this.mTouchMode = 4;
            this.mSuppressIdleStateChangeCall = suppressEndFlingStateChangeCall;
            AbsListView.this.postOnAnimation(this);
        }

        void endFling() {
            AbsListView.this.mTouchMode = -1;
            AbsListView.this.removeCallbacks(this);
            AbsListView.this.removeCallbacks(this.mCheckFlywheel);
            if (!this.mSuppressIdleStateChangeCall) {
                AbsListView.this.reportScrollStateChange(0);
            }
            AbsListView.this.clearScrollingCache();
            this.mScroller.abortAnimation();
            if (AbsListView.this.mFlingStrictSpan != null) {
                AbsListView.this.mFlingStrictSpan.finish();
                AbsListView.this.mFlingStrictSpan = null;
            }
        }

        void flywheelTouch() {
            AbsListView.this.postDelayed(this.mCheckFlywheel, 40L);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // java.lang.Runnable
        public void run() {
            int delta;
            boolean crossUp = false;
            switch (AbsListView.this.mTouchMode) {
                case 3:
                    if (this.mScroller.isFinished()) {
                        return;
                    }
                    break;
                case 4:
                    break;
                case 5:
                default:
                    endFling();
                    return;
                case 6:
                    OverScroller scroller = this.mScroller;
                    if (scroller.computeScrollOffset()) {
                        int scrollY = AbsListView.this.mScrollY;
                        int currY = scroller.getCurrY();
                        int deltaY = currY - scrollY;
                        AbsListView absListView = AbsListView.this;
                        if (absListView.overScrollBy(0, deltaY, 0, scrollY, 0, 0, 0, absListView.mOverflingDistance, false)) {
                            boolean crossDown = scrollY <= 0 && currY > 0;
                            if (scrollY >= 0 && currY < 0) {
                                crossUp = true;
                            }
                            if (crossDown || crossUp) {
                                int velocity = (int) scroller.getCurrVelocity();
                                if (crossUp) {
                                    velocity = -velocity;
                                }
                                scroller.abortAnimation();
                                start(velocity);
                                return;
                            }
                            startSpringback();
                            return;
                        }
                        AbsListView.this.invalidate();
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    endFling();
                    return;
            }
            if (AbsListView.this.mDataChanged) {
                AbsListView.this.layoutChildren();
            }
            if (AbsListView.this.mItemCount == 0 || AbsListView.this.getChildCount() == 0) {
                AbsListView.this.mEdgeGlowBottom.onRelease();
                AbsListView.this.mEdgeGlowTop.onRelease();
                endFling();
                return;
            }
            OverScroller scroller2 = this.mScroller;
            boolean more = scroller2.computeScrollOffset();
            int y = scroller2.getCurrY();
            int delta2 = AbsListView.this.consumeFlingInStretch(this.mLastFlingY - y);
            if (delta2 > 0) {
                AbsListView absListView2 = AbsListView.this;
                absListView2.mMotionPosition = absListView2.mFirstPosition;
                View firstView = AbsListView.this.getChildAt(0);
                AbsListView.this.mMotionViewOriginalTop = firstView.getTop();
                delta = Math.min(((AbsListView.this.getHeight() - AbsListView.this.mPaddingBottom) - AbsListView.this.mPaddingTop) - 1, delta2);
            } else {
                int offsetToLast = AbsListView.this.getChildCount() - 1;
                AbsListView absListView3 = AbsListView.this;
                absListView3.mMotionPosition = absListView3.mFirstPosition + offsetToLast;
                View lastView = AbsListView.this.getChildAt(offsetToLast);
                AbsListView.this.mMotionViewOriginalTop = lastView.getTop();
                delta = Math.max(-(((AbsListView.this.getHeight() - AbsListView.this.mPaddingBottom) - AbsListView.this.mPaddingTop) - 1), delta2);
            }
            AbsListView absListView4 = AbsListView.this;
            View motionView = absListView4.getChildAt(absListView4.mMotionPosition - AbsListView.this.mFirstPosition);
            int oldTop = 0;
            if (motionView != null) {
                oldTop = motionView.getTop();
            }
            boolean atEdge = AbsListView.this.trackMotionScroll(delta, delta);
            if (atEdge && delta != 0) {
                crossUp = true;
            }
            if (crossUp) {
                if (motionView != null) {
                    int overshoot = -(delta - (motionView.getTop() - oldTop));
                    AbsListView absListView5 = AbsListView.this;
                    absListView5.overScrollBy(0, overshoot, 0, absListView5.mScrollY, 0, 0, 0, AbsListView.this.mOverflingDistance, false);
                }
                if (more) {
                    edgeReached(delta);
                }
            } else if (more && !crossUp) {
                if (atEdge) {
                    AbsListView.this.invalidate();
                }
                this.mLastFlingY = y;
                AbsListView.this.postOnAnimation(this);
            } else {
                endFling();
            }
        }
    }

    public void setFriction(float friction) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        this.mFlingRunnable.mScroller.setFriction(friction);
    }

    public void setVelocityScale(float scale) {
        this.mVelocityScale = scale;
    }

    AbsPositionScroller createPositionScroller() {
        return new PositionScroller();
    }

    public void smoothScrollToPosition(int position) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.start(position);
    }

    public void smoothScrollToPositionFromTop(int position, int offset, int duration) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.startWithOffset(position, offset, duration);
    }

    public void smoothScrollToPositionFromTop(int position, int offset) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.startWithOffset(position, offset);
    }

    public void smoothScrollToPosition(int position, int boundPosition) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.start(position, boundPosition);
    }

    public void smoothScrollBy(int distance, int duration) {
        smoothScrollBy(distance, duration, false, false);
    }

    void smoothScrollBy(int distance, int duration, boolean linear, boolean suppressEndFlingStateChangeCall) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        int firstPos = this.mFirstPosition;
        int childCount = getChildCount();
        int lastPos = firstPos + childCount;
        int topLimit = getPaddingTop();
        int bottomLimit = getHeight() - getPaddingBottom();
        if (distance == 0 || this.mItemCount == 0 || childCount == 0 || ((firstPos == 0 && getChildAt(0).getTop() == topLimit && distance < 0) || (lastPos == this.mItemCount && getChildAt(childCount - 1).getBottom() == bottomLimit && distance > 0))) {
            this.mFlingRunnable.endFling();
            AbsPositionScroller absPositionScroller = this.mPositionScroller;
            if (absPositionScroller != null) {
                absPositionScroller.stop();
                return;
            }
            return;
        }
        reportScrollStateChange(2);
        this.mFlingRunnable.startScroll(distance, duration, linear, suppressEndFlingStateChangeCall);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void smoothScrollByOffset(int position) {
        View child;
        int index = -1;
        if (position < 0) {
            index = getFirstVisiblePosition();
        } else if (position > 0) {
            index = getLastVisiblePosition();
        }
        if (index > -1 && (child = getChildAt(index - getFirstVisiblePosition())) != null) {
            Rect visibleRect = new Rect();
            if (child.getGlobalVisibleRect(visibleRect)) {
                int childRectArea = child.getWidth() * child.getHeight();
                int visibleRectArea = visibleRect.width() * visibleRect.height();
                float visibleArea = visibleRectArea / childRectArea;
                if (position < 0 && visibleArea < 0.75f) {
                    index++;
                } else if (position > 0 && visibleArea < 0.75f) {
                    index--;
                }
            }
            int childRectArea2 = getCount();
            smoothScrollToPosition(Math.max(0, Math.min(childRectArea2, index + position)));
        }
    }

    private void createScrollingCache() {
        if (this.mScrollingCacheEnabled && !this.mCachingStarted && !isHardwareAccelerated()) {
            setChildrenDrawnWithCacheEnabled(true);
            setChildrenDrawingCacheEnabled(true);
            this.mCachingActive = true;
            this.mCachingStarted = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearScrollingCache() {
        if (!isHardwareAccelerated()) {
            if (this.mClearScrollingCache == null) {
                this.mClearScrollingCache = new Runnable() { // from class: android.widget.AbsListView.4
                    @Override // java.lang.Runnable
                    public void run() {
                        if (AbsListView.this.mCachingStarted) {
                            AbsListView absListView = AbsListView.this;
                            absListView.mCachingActive = false;
                            absListView.mCachingStarted = false;
                            AbsListView.this.setChildrenDrawnWithCacheEnabled(false);
                            if ((AbsListView.this.mPersistentDrawingCache & 2) == 0) {
                                AbsListView.this.setChildrenDrawingCacheEnabled(false);
                            }
                            if (!AbsListView.this.isAlwaysDrawnWithCacheEnabled()) {
                                AbsListView.this.invalidate();
                            }
                        }
                    }
                };
            }
            post(this.mClearScrollingCache);
        }
    }

    public void scrollListBy(int y) {
        trackMotionScroll(-y, -y);
    }

    public boolean canScrollList(int direction) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return false;
        }
        int firstPosition = this.mFirstPosition;
        Rect listPadding = this.mListPadding;
        if (direction > 0) {
            int lastBottom = getChildAt(childCount - 1).getBottom();
            int lastPosition = firstPosition + childCount;
            if (lastPosition >= this.mItemCount && lastBottom <= getHeight() - listPadding.bottom) {
                return false;
            }
            return true;
        }
        int firstTop = getChildAt(0).getTop();
        if (firstPosition <= 0 && firstTop >= listPadding.top) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:113:0x01f2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean trackMotionScroll(int deltaY, int incrementalDeltaY) {
        int deltaY2;
        int incrementalDeltaY2;
        boolean z;
        boolean z2;
        int childCount;
        int count;
        int childCount2;
        int childIndex;
        int top;
        int childCount3 = getChildCount();
        if (childCount3 != 0) {
            int firstTop = getChildAt(0).getTop();
            int lastBottom = getChildAt(childCount3 - 1).getBottom();
            Rect listPadding = this.mListPadding;
            int effectivePaddingTop = 0;
            int effectivePaddingBottom = 0;
            if ((this.mGroupFlags & 34) == 34) {
                effectivePaddingTop = listPadding.top;
                effectivePaddingBottom = listPadding.bottom;
            }
            int spaceAbove = effectivePaddingTop - firstTop;
            int end = getHeight() - effectivePaddingBottom;
            int spaceBelow = lastBottom - end;
            int height = (getHeight() - this.mPaddingBottom) - this.mPaddingTop;
            if (deltaY < 0) {
                deltaY2 = Math.max(-(height - 1), deltaY);
            } else {
                deltaY2 = Math.min(height - 1, deltaY);
            }
            if (incrementalDeltaY < 0) {
                incrementalDeltaY2 = Math.max(-(height - 1), incrementalDeltaY);
            } else {
                incrementalDeltaY2 = Math.min(height - 1, incrementalDeltaY);
            }
            int firstPosition = this.mFirstPosition;
            if (firstPosition == 0) {
                this.mFirstPositionDistanceGuess = firstTop - listPadding.top;
            } else {
                this.mFirstPositionDistanceGuess += incrementalDeltaY2;
            }
            if (firstPosition + childCount3 == this.mItemCount) {
                this.mLastPositionDistanceGuess = listPadding.bottom + lastBottom;
            } else {
                this.mLastPositionDistanceGuess += incrementalDeltaY2;
            }
            boolean cannotScrollDown = firstPosition == 0 && firstTop >= listPadding.top && incrementalDeltaY2 >= 0;
            boolean cannotScrollUp = firstPosition + childCount3 == this.mItemCount && lastBottom <= getHeight() - listPadding.bottom && incrementalDeltaY2 <= 0;
            if (cannotScrollDown) {
                z = false;
                z2 = true;
            } else if (!cannotScrollUp) {
                boolean down = incrementalDeltaY2 < 0;
                boolean inTouchMode = isInTouchMode();
                if (inTouchMode) {
                    hideSelector();
                }
                int headerViewsCount = getHeaderViewsCount();
                int footerViewsStart = this.mItemCount - getFooterViewsCount();
                int start = 0;
                int count2 = 0;
                if (down) {
                    int top2 = -incrementalDeltaY2;
                    int effectivePaddingTop2 = this.mGroupFlags;
                    if ((effectivePaddingTop2 & 34) == 34) {
                        top2 += listPadding.top;
                    }
                    int i = 0;
                    while (i < childCount3) {
                        View child = getChildAt(i);
                        int end2 = end;
                        if (child.getBottom() >= top2) {
                            break;
                        }
                        count2++;
                        int position = firstPosition + i;
                        if (position < headerViewsCount || position >= footerViewsStart) {
                            top = top2;
                        } else {
                            child.clearAccessibilityFocus();
                            top = top2;
                            this.mRecycler.addScrapView(child, position);
                        }
                        i++;
                        top2 = top;
                        end = end2;
                    }
                    childCount = 0;
                    count = count2;
                } else {
                    int bottom = getHeight() - incrementalDeltaY2;
                    if ((this.mGroupFlags & 34) == 34) {
                        bottom -= listPadding.bottom;
                    }
                    int i2 = childCount3 - 1;
                    while (i2 >= 0) {
                        View child2 = getChildAt(i2);
                        if (child2.getTop() <= bottom) {
                            break;
                        }
                        start = i2;
                        count2++;
                        int position2 = firstPosition + i2;
                        if (position2 < headerViewsCount || position2 >= footerViewsStart) {
                            childCount2 = childCount3;
                        } else {
                            child2.clearAccessibilityFocus();
                            childCount2 = childCount3;
                            this.mRecycler.addScrapView(child2, position2);
                        }
                        i2--;
                        childCount3 = childCount2;
                    }
                    childCount = start;
                    count = count2;
                }
                this.mMotionViewNewTop = this.mMotionViewOriginalTop + deltaY2;
                this.mBlockLayoutRequests = true;
                if (count > 0) {
                    detachViewsFromParent(childCount, count);
                    this.mRecycler.removeSkippedScrap();
                }
                if (!awakenScrollBars()) {
                    invalidate();
                }
                offsetChildrenTopAndBottom(incrementalDeltaY2);
                if (down) {
                    this.mFirstPosition += count;
                }
                int absIncrementalDeltaY = Math.abs(incrementalDeltaY2);
                if (spaceAbove < absIncrementalDeltaY || spaceBelow < absIncrementalDeltaY) {
                    fillGap(down);
                }
                this.mRecycler.fullyDetachScrapViews();
                boolean selectorOnScreen = false;
                if (!inTouchMode && this.mSelectedPosition != -1) {
                    int childIndex2 = this.mSelectedPosition - this.mFirstPosition;
                    if (childIndex2 >= 0 && childIndex2 < getChildCount()) {
                        positionSelector(this.mSelectedPosition, getChildAt(childIndex2));
                        selectorOnScreen = true;
                    }
                    if (!selectorOnScreen) {
                        this.mSelectorRect.setEmpty();
                    }
                    this.mBlockLayoutRequests = false;
                    invokeOnItemScrollListener();
                    return false;
                }
                int deltaY3 = this.mSelectorPosition;
                if (deltaY3 != -1 && (childIndex = deltaY3 - this.mFirstPosition) >= 0 && childIndex < getChildCount()) {
                    positionSelector(this.mSelectorPosition, getChildAt(childIndex));
                    selectorOnScreen = true;
                }
                if (!selectorOnScreen) {
                }
                this.mBlockLayoutRequests = false;
                invokeOnItemScrollListener();
                return false;
            } else {
                z = false;
                z2 = true;
            }
            return incrementalDeltaY2 != 0 ? z2 : z;
        }
        return true;
    }

    int getHeaderViewsCount() {
        return 0;
    }

    int getFooterViewsCount() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideSelector() {
        if (this.mSelectedPosition != -1) {
            if (this.mLayoutMode != 4) {
                this.mResurrectToPosition = this.mSelectedPosition;
            }
            if (this.mNextSelectedPosition >= 0 && this.mNextSelectedPosition != this.mSelectedPosition) {
                this.mResurrectToPosition = this.mNextSelectedPosition;
            }
            setSelectedPositionInt(-1);
            setNextSelectedPositionInt(-1);
            this.mSelectedTop = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int reconcileSelectedPosition() {
        int position = this.mSelectedPosition;
        if (position < 0) {
            position = this.mResurrectToPosition;
        }
        return Math.min(Math.max(0, position), this.mItemCount - 1);
    }

    int findClosestMotionRow(int y) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return -1;
        }
        int motionRow = findMotionRow(y);
        return motionRow != -1 ? motionRow : (this.mFirstPosition + childCount) - 1;
    }

    public void invalidateViews() {
        this.mDataChanged = true;
        rememberSyncState();
        requestLayout();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean resurrectSelectionIfNeeded() {
        if (this.mSelectedPosition < 0 && resurrectSelection()) {
            updateSelectorState();
            return true;
        }
        return false;
    }

    boolean resurrectSelection() {
        int selectedPos;
        int childCount = getChildCount();
        if (childCount <= 0) {
            return false;
        }
        int selectedTop = 0;
        int childrenTop = this.mListPadding.top;
        int childrenBottom = (this.mBottom - this.mTop) - this.mListPadding.bottom;
        int firstPosition = this.mFirstPosition;
        int toPosition = this.mResurrectToPosition;
        boolean down = true;
        if (toPosition >= firstPosition && toPosition < firstPosition + childCount) {
            selectedPos = toPosition;
            View selected = getChildAt(selectedPos - this.mFirstPosition);
            selectedTop = selected.getTop();
            int selectedBottom = selected.getBottom();
            if (selectedTop < childrenTop) {
                selectedTop = childrenTop + getVerticalFadingEdgeLength();
            } else if (selectedBottom > childrenBottom) {
                selectedTop = (childrenBottom - selected.getMeasuredHeight()) - getVerticalFadingEdgeLength();
            }
        } else if (toPosition < firstPosition) {
            selectedPos = firstPosition;
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                int top = getChildAt(i).getTop();
                if (i == 0) {
                    selectedTop = top;
                    if (firstPosition > 0 || top < childrenTop) {
                        childrenTop += getVerticalFadingEdgeLength();
                    }
                }
                if (top < childrenTop) {
                    i++;
                } else {
                    selectedPos = firstPosition + i;
                    selectedTop = top;
                    break;
                }
            }
        } else {
            int selectedPos2 = this.mItemCount;
            down = false;
            int selectedPos3 = (firstPosition + childCount) - 1;
            int i2 = childCount - 1;
            while (true) {
                if (i2 < 0) {
                    selectedPos = selectedPos3;
                    break;
                }
                View v = getChildAt(i2);
                int top2 = v.getTop();
                int bottom = v.getBottom();
                if (i2 == childCount - 1) {
                    selectedTop = top2;
                    if (firstPosition + childCount < selectedPos2 || bottom > childrenBottom) {
                        childrenBottom -= getVerticalFadingEdgeLength();
                    }
                }
                if (bottom > childrenBottom) {
                    i2--;
                } else {
                    int selectedPos4 = firstPosition + i2;
                    selectedTop = top2;
                    selectedPos = selectedPos4;
                    break;
                }
            }
        }
        this.mResurrectToPosition = -1;
        removeCallbacks(this.mFlingRunnable);
        AbsPositionScroller absPositionScroller = this.mPositionScroller;
        if (absPositionScroller != null) {
            absPositionScroller.stop();
        }
        this.mTouchMode = -1;
        clearScrollingCache();
        this.mSpecificTop = selectedTop;
        int selectedPos5 = lookForSelectablePosition(selectedPos, down);
        if (selectedPos5 >= firstPosition && selectedPos5 <= getLastVisiblePosition()) {
            this.mLayoutMode = 4;
            updateSelectorState();
            setSelectionInt(selectedPos5);
            invokeOnItemScrollListener();
        } else {
            selectedPos5 = -1;
        }
        reportScrollStateChange(0);
        return selectedPos5 >= 0;
    }

    void confirmCheckedPositionsById() {
        ActionMode actionMode;
        int i;
        boolean found;
        MultiChoiceModeWrapper multiChoiceModeWrapper;
        this.mCheckStates.clear();
        boolean checkedCountChanged = false;
        int checkedIndex = 0;
        while (checkedIndex < this.mCheckedIdStates.size()) {
            long id = this.mCheckedIdStates.keyAt(checkedIndex);
            int lastPos = this.mCheckedIdStates.valueAt(checkedIndex).intValue();
            long lastPosId = this.mAdapter.getItemId(lastPos);
            boolean z = true;
            if (id != lastPosId) {
                int start = Math.max(0, lastPos - 20);
                int end = Math.min(lastPos + 20, this.mItemCount);
                int searchPos = start;
                while (true) {
                    if (searchPos >= end) {
                        found = false;
                        break;
                    }
                    long searchId = this.mAdapter.getItemId(searchPos);
                    if (id != searchId) {
                        searchPos++;
                        z = true;
                    } else {
                        this.mCheckStates.put(searchPos, z);
                        this.mCheckedIdStates.setValueAt(checkedIndex, Integer.valueOf(searchPos));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    this.mCheckedIdStates.delete(id);
                    checkedIndex--;
                    this.mCheckedItemCount--;
                    checkedCountChanged = true;
                    ActionMode actionMode2 = this.mChoiceActionMode;
                    if (actionMode2 != null && (multiChoiceModeWrapper = this.mMultiChoiceModeCallback) != null) {
                        multiChoiceModeWrapper.onItemCheckedStateChanged(actionMode2, lastPos, id, false);
                    }
                }
                i = 1;
            } else {
                i = 1;
                this.mCheckStates.put(lastPos, true);
            }
            checkedIndex += i;
        }
        if (checkedCountChanged && (actionMode = this.mChoiceActionMode) != null) {
            actionMode.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AdapterView
    public void handleDataChanged() {
        ListAdapter listAdapter;
        int count = this.mItemCount;
        int lastHandledItemCount = this.mLastHandledItemCount;
        this.mLastHandledItemCount = this.mItemCount;
        if (this.mChoiceMode != 0 && (listAdapter = this.mAdapter) != null && listAdapter.hasStableIds()) {
            confirmCheckedPositionsById();
        }
        this.mRecycler.clearTransientStateViews();
        if (count > 0) {
            if (this.mNeedSync) {
                this.mNeedSync = false;
                this.mPendingSync = null;
                int i = this.mTranscriptMode;
                if (i == 2) {
                    this.mLayoutMode = 3;
                    return;
                }
                if (i == 1) {
                    if (this.mForceTranscriptScroll) {
                        this.mForceTranscriptScroll = false;
                        this.mLayoutMode = 3;
                        return;
                    }
                    int childCount = getChildCount();
                    int listBottom = getHeight() - getPaddingBottom();
                    View lastChild = getChildAt(childCount - 1);
                    int lastBottom = lastChild != null ? lastChild.getBottom() : listBottom;
                    if (this.mFirstPosition + childCount >= lastHandledItemCount && lastBottom <= listBottom) {
                        this.mLayoutMode = 3;
                        return;
                    }
                    awakenScrollBars();
                }
                switch (this.mSyncMode) {
                    case 1:
                        this.mLayoutMode = 5;
                        this.mSyncPosition = Math.min(Math.max(0, this.mSyncPosition), count - 1);
                        return;
                    case 0:
                        if (isInTouchMode()) {
                            this.mLayoutMode = 5;
                            this.mSyncPosition = Math.min(Math.max(0, this.mSyncPosition), count - 1);
                            return;
                        }
                        int newPos = findSyncPosition();
                        if (newPos >= 0 && lookForSelectablePosition(newPos, true) == newPos) {
                            this.mSyncPosition = newPos;
                            if (this.mSyncHeight == getHeight()) {
                                this.mLayoutMode = 5;
                            } else {
                                this.mLayoutMode = 2;
                            }
                            setNextSelectedPositionInt(newPos);
                            return;
                        }
                        break;
                }
            }
            if (!isInTouchMode()) {
                int newPos2 = getSelectedItemPosition();
                if (newPos2 >= count) {
                    newPos2 = count - 1;
                }
                if (newPos2 < 0) {
                    newPos2 = 0;
                }
                int selectablePos = lookForSelectablePosition(newPos2, true);
                if (selectablePos >= 0) {
                    setNextSelectedPositionInt(selectablePos);
                    return;
                }
                int selectablePos2 = lookForSelectablePosition(newPos2, false);
                if (selectablePos2 >= 0) {
                    setNextSelectedPositionInt(selectablePos2);
                    return;
                }
            } else if (this.mResurrectToPosition >= 0) {
                return;
            }
        }
        this.mLayoutMode = this.mStackFromBottom ? 3 : 1;
        this.mSelectedPosition = -1;
        this.mSelectedRowId = Long.MIN_VALUE;
        this.mNextSelectedPosition = -1;
        this.mNextSelectedRowId = Long.MIN_VALUE;
        this.mNeedSync = false;
        this.mPendingSync = null;
        this.mSelectorPosition = -1;
        checkSelectionChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDisplayHint(int hint) {
        PopupWindow popupWindow;
        super.onDisplayHint(hint);
        switch (hint) {
            case 0:
                if (this.mFiltered && (popupWindow = this.mPopup) != null && !popupWindow.isShowing()) {
                    showPopup();
                    break;
                }
                break;
            case 4:
                PopupWindow popupWindow2 = this.mPopup;
                if (popupWindow2 != null && popupWindow2.isShowing()) {
                    dismissPopup();
                    break;
                }
                break;
        }
        this.mPopupHidden = hint == 4;
    }

    private void dismissPopup() {
        PopupWindow popupWindow = this.mPopup;
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private void showPopup() {
        if (getWindowVisibility() == 0) {
            createTextFilter(true);
            positionPopup();
            checkFocus();
        }
    }

    private void positionPopup() {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int[] xy = new int[2];
        getLocationOnScreen(xy);
        int bottomGap = ((screenHeight - xy[1]) - getHeight()) + ((int) (this.mDensityScale * 20.0f));
        if (!this.mPopup.isShowing()) {
            this.mPopup.showAtLocation(this, 81, xy[0], bottomGap);
        } else {
            this.mPopup.update(xy[0], bottomGap, -1, -1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getDistance(Rect source, Rect dest, int direction) {
        int sX;
        int sY;
        int dX;
        int dY;
        switch (direction) {
            case 1:
            case 2:
                int sX2 = source.right;
                sX = sX2 + (source.width() / 2);
                sY = source.top + (source.height() / 2);
                dX = dest.left + (dest.width() / 2);
                dY = dest.top + (dest.height() / 2);
                break;
            case 17:
                sX = source.left;
                sY = source.top + (source.height() / 2);
                dX = dest.right;
                dY = dest.top + (dest.height() / 2);
                break;
            case 33:
                int sX3 = source.left;
                sX = sX3 + (source.width() / 2);
                sY = source.top;
                dX = dest.left + (dest.width() / 2);
                dY = dest.bottom;
                break;
            case 66:
                sX = source.right;
                sY = source.top + (source.height() / 2);
                dX = dest.left;
                dY = dest.top + (dest.height() / 2);
                break;
            case 130:
                sX = source.left + (source.width() / 2);
                sY = source.bottom;
                dX = dest.left + (dest.width() / 2);
                dY = dest.top;
                break;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
        }
        int deltaX = dX - sX;
        int deltaY = dY - sY;
        return (deltaY * deltaY) + (deltaX * deltaX);
    }

    @Override // android.widget.AdapterView
    protected boolean isInFilterMode() {
        return this.mFiltered;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean sendToTextFilter(int keyCode, int count, KeyEvent event) {
        PopupWindow popupWindow;
        if (acceptFilter()) {
            boolean handled = false;
            boolean okToSend = true;
            switch (keyCode) {
                case 4:
                case 111:
                    if (this.mFiltered && (popupWindow = this.mPopup) != null && popupWindow.isShowing()) {
                        if (event.getAction() != 0 || event.getRepeatCount() != 0) {
                            if (event.getAction() == 1 && event.isTracking() && !event.isCanceled()) {
                                handled = true;
                                this.mTextFilter.setText("");
                            }
                        } else {
                            KeyEvent.DispatcherState state = getKeyDispatcherState();
                            if (state != null) {
                                state.startTracking(event, this);
                            }
                            handled = true;
                        }
                    }
                    okToSend = false;
                    break;
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 66:
                case 160:
                    okToSend = false;
                    break;
                case 62:
                    okToSend = this.mFiltered;
                    break;
            }
            if (okToSend) {
                createTextFilter(true);
                KeyEvent forwardEvent = event;
                if (forwardEvent.getRepeatCount() > 0) {
                    forwardEvent = KeyEvent.changeTimeRepeat(event, event.getEventTime(), 0);
                }
                int action = event.getAction();
                switch (action) {
                    case 0:
                        return this.mTextFilter.onKeyDown(keyCode, forwardEvent);
                    case 1:
                        return this.mTextFilter.onKeyUp(keyCode, forwardEvent);
                    case 2:
                        return this.mTextFilter.onKeyMultiple(keyCode, count, event);
                    default:
                        return handled;
                }
            }
            return handled;
        }
        return false;
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (isTextFilterEnabled()) {
            if (this.mPublicInputConnection == null) {
                this.mDefInputConnection = new BaseInputConnection((View) this, false);
                this.mPublicInputConnection = new InputConnectionWrapper(outAttrs);
            }
            outAttrs.inputType = 177;
            outAttrs.imeOptions = 6;
            return this.mPublicInputConnection;
        }
        return null;
    }

    /* loaded from: classes4.dex */
    private class InputConnectionWrapper implements InputConnection {
        private final EditorInfo mOutAttrs;
        private InputConnection mTarget;

        public InputConnectionWrapper(EditorInfo outAttrs) {
            this.mOutAttrs = outAttrs;
        }

        private InputConnection getTarget() {
            if (this.mTarget == null) {
                this.mTarget = AbsListView.this.getTextFilterInput().onCreateInputConnection(this.mOutAttrs);
            }
            return this.mTarget;
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean reportFullscreenMode(boolean enabled) {
            return AbsListView.this.mDefInputConnection.reportFullscreenMode(enabled);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean performEditorAction(int editorAction) {
            if (editorAction == 6) {
                InputMethodManager imm = (InputMethodManager) AbsListView.this.getContext().getSystemService(InputMethodManager.class);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(AbsListView.this.getWindowToken(), 0);
                    return true;
                }
                return true;
            }
            return false;
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean sendKeyEvent(KeyEvent event) {
            return AbsListView.this.mDefInputConnection.sendKeyEvent(event);
        }

        @Override // android.view.inputmethod.InputConnection
        public CharSequence getTextBeforeCursor(int n, int flags) {
            InputConnection inputConnection = this.mTarget;
            return inputConnection == null ? "" : inputConnection.getTextBeforeCursor(n, flags);
        }

        @Override // android.view.inputmethod.InputConnection
        public CharSequence getTextAfterCursor(int n, int flags) {
            InputConnection inputConnection = this.mTarget;
            return inputConnection == null ? "" : inputConnection.getTextAfterCursor(n, flags);
        }

        @Override // android.view.inputmethod.InputConnection
        public CharSequence getSelectedText(int flags) {
            InputConnection inputConnection = this.mTarget;
            return inputConnection == null ? "" : inputConnection.getSelectedText(flags);
        }

        @Override // android.view.inputmethod.InputConnection
        public SurroundingText getSurroundingText(int beforeLength, int afterLength, int flags) {
            InputConnection inputConnection = this.mTarget;
            if (inputConnection == null) {
                return null;
            }
            return inputConnection.getSurroundingText(beforeLength, afterLength, flags);
        }

        @Override // android.view.inputmethod.InputConnection
        public int getCursorCapsMode(int reqModes) {
            InputConnection inputConnection = this.mTarget;
            if (inputConnection == null) {
                return 16384;
            }
            return inputConnection.getCursorCapsMode(reqModes);
        }

        @Override // android.view.inputmethod.InputConnection
        public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
            return getTarget().getExtractedText(request, flags);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            return getTarget().deleteSurroundingText(beforeLength, afterLength);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) {
            return getTarget().deleteSurroundingTextInCodePoints(beforeLength, afterLength);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean setComposingText(CharSequence text, int newCursorPosition) {
            return getTarget().setComposingText(text, newCursorPosition);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean setComposingRegion(int start, int end) {
            return getTarget().setComposingRegion(start, end);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean finishComposingText() {
            InputConnection inputConnection = this.mTarget;
            return inputConnection == null || inputConnection.finishComposingText();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitText(CharSequence text, int newCursorPosition) {
            return getTarget().commitText(text, newCursorPosition);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitCompletion(CompletionInfo text) {
            return getTarget().commitCompletion(text);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitCorrection(CorrectionInfo correctionInfo) {
            return getTarget().commitCorrection(correctionInfo);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean setSelection(int start, int end) {
            return getTarget().setSelection(start, end);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean performContextMenuAction(int id) {
            return getTarget().performContextMenuAction(id);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean beginBatchEdit() {
            return getTarget().beginBatchEdit();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean endBatchEdit() {
            return getTarget().endBatchEdit();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean clearMetaKeyStates(int states) {
            return getTarget().clearMetaKeyStates(states);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean performPrivateCommand(String action, Bundle data) {
            return getTarget().performPrivateCommand(action, data);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean requestCursorUpdates(int cursorUpdateMode) {
            return getTarget().requestCursorUpdates(cursorUpdateMode);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean requestCursorUpdates(int cursorUpdateMode, int cursorUpdateFilter) {
            return getTarget().requestCursorUpdates(cursorUpdateMode, cursorUpdateFilter);
        }

        @Override // android.view.inputmethod.InputConnection
        public Handler getHandler() {
            return getTarget().getHandler();
        }

        @Override // android.view.inputmethod.InputConnection
        public void closeConnection() {
            getTarget().closeConnection();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitContent(InputContentInfo inputContentInfo, int flags, Bundle opts) {
            return getTarget().commitContent(inputContentInfo, flags, opts);
        }
    }

    @Override // android.view.View
    public boolean checkInputConnectionProxy(View view) {
        return view == this.mTextFilter;
    }

    private void createTextFilter(boolean animateEntrance) {
        if (this.mPopup == null) {
            PopupWindow p = new PopupWindow(getContext());
            p.setFocusable(false);
            p.setTouchable(false);
            p.setInputMethodMode(2);
            p.setContentView(getTextFilterInput());
            p.setWidth(-2);
            p.setHeight(-2);
            p.setBackgroundDrawable(null);
            this.mPopup = p;
            getViewTreeObserver().addOnGlobalLayoutListener(this);
            this.mGlobalLayoutListenerAddedFilter = true;
        }
        if (animateEntrance) {
            this.mPopup.setAnimationStyle(C4057R.C4062style.Animation_TypingFilter);
        } else {
            this.mPopup.setAnimationStyle(C4057R.C4062style.Animation_TypingFilterRestore);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public EditText getTextFilterInput() {
        if (this.mTextFilter == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            EditText editText = (EditText) layoutInflater.inflate(C4057R.layout.typing_filter, (ViewGroup) null);
            this.mTextFilter = editText;
            editText.setRawInputType(177);
            this.mTextFilter.setImeOptions(268435456);
            this.mTextFilter.addTextChangedListener(this);
        }
        return this.mTextFilter;
    }

    public void clearTextFilter() {
        if (this.mFiltered) {
            getTextFilterInput().setText("");
            this.mFiltered = false;
            PopupWindow popupWindow = this.mPopup;
            if (popupWindow != null && popupWindow.isShowing()) {
                dismissPopup();
            }
        }
    }

    public boolean hasTextFilter() {
        return this.mFiltered;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        PopupWindow popupWindow;
        if (isShown()) {
            if (this.mFiltered && (popupWindow = this.mPopup) != null && !popupWindow.isShowing() && !this.mPopupHidden) {
                showPopup();
                return;
            }
            return;
        }
        PopupWindow popupWindow2 = this.mPopup;
        if (popupWindow2 != null && popupWindow2.isShowing()) {
            dismissPopup();
        }
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isTextFilterEnabled()) {
            createTextFilter(true);
            int length = s.length();
            boolean showing = this.mPopup.isShowing();
            if (!showing && length > 0) {
                showPopup();
                this.mFiltered = true;
            } else if (showing && length == 0) {
                dismissPopup();
                this.mFiltered = false;
            }
            ListAdapter listAdapter = this.mAdapter;
            if (listAdapter instanceof Filterable) {
                Filter f = ((Filterable) listAdapter).getFilter();
                if (f != null) {
                    f.filter(s, this);
                    return;
                }
                throw new IllegalStateException("You cannot call onTextChanged with a non filterable adapter");
            }
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    @Override // android.widget.Filter.FilterListener
    public void onFilterComplete(int count) {
        if (this.mSelectedPosition < 0 && count > 0) {
            this.mResurrectToPosition = -1;
            resurrectSelection();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2, 0);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setTranscriptMode(int mode) {
        this.mTranscriptMode = mode;
    }

    public int getTranscriptMode() {
        return this.mTranscriptMode;
    }

    @Override // android.view.View
    public int getSolidColor() {
        return this.mCacheColorHint;
    }

    public void setCacheColorHint(int color) {
        if (color != this.mCacheColorHint) {
            this.mCacheColorHint = color;
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).setDrawingCacheBackgroundColor(color);
            }
            this.mRecycler.setCacheColorHint(color);
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public int getCacheColorHint() {
        return this.mCacheColorHint;
    }

    public void reclaimViews(List<View> views) {
        int childCount = getChildCount();
        RecyclerListener listener = this.mRecycler.mRecyclerListener;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp != null && this.mRecycler.shouldRecycleViewType(lp.viewType)) {
                views.add(child);
                child.setAccessibilityDelegate(null);
                child.resetSubtreeAutofillIds();
                if (listener != null) {
                    listener.onMovedToScrapHeap(child);
                }
            }
        }
        this.mRecycler.reclaimScrapViews(views);
        removeAllViewsInLayout();
    }

    private void finishGlows() {
        if (shouldDisplayEdgeEffects()) {
            this.mEdgeGlowTop.finish();
            this.mEdgeGlowBottom.finish();
        }
    }

    public void setRemoteViewsAdapter(Intent intent) {
        setRemoteViewsAdapter(intent, false);
    }

    public Runnable setRemoteViewsAdapterAsync(Intent intent) {
        return new RemoteViewsAdapter.AsyncRemoteAdapterAction(this, intent);
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void setRemoteViewsAdapter(Intent intent, boolean isAsync) {
        if (this.mRemoteAdapter != null) {
            Intent.FilterComparison fcNew = new Intent.FilterComparison(intent);
            Intent.FilterComparison fcOld = new Intent.FilterComparison(this.mRemoteAdapter.getRemoteViewsServiceIntent());
            if (fcNew.equals(fcOld)) {
                return;
            }
        }
        this.mDeferNotifyDataSetChanged = false;
        RemoteViewsAdapter remoteViewsAdapter = new RemoteViewsAdapter(getContext(), intent, this, isAsync);
        this.mRemoteAdapter = remoteViewsAdapter;
        if (remoteViewsAdapter.isDataReady()) {
            setAdapter((ListAdapter) this.mRemoteAdapter);
        }
    }

    public void setRemoteViewsInteractionHandler(RemoteViews.InteractionHandler handler) {
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteAdapter;
        if (remoteViewsAdapter != null) {
            remoteViewsAdapter.setRemoteViewsInteractionHandler(handler);
        }
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void deferNotifyDataSetChanged() {
        this.mDeferNotifyDataSetChanged = true;
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public boolean onRemoteAdapterConnected() {
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteAdapter;
        if (remoteViewsAdapter != this.mAdapter) {
            setAdapter((ListAdapter) remoteViewsAdapter);
            if (this.mDeferNotifyDataSetChanged) {
                this.mRemoteAdapter.notifyDataSetChanged();
                this.mDeferNotifyDataSetChanged = false;
            }
            return false;
        } else if (remoteViewsAdapter != null) {
            remoteViewsAdapter.superNotifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void onRemoteAdapterDisconnected() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setVisibleRangeHint(int start, int end) {
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteAdapter;
        if (remoteViewsAdapter != null) {
            remoteViewsAdapter.setVisibleRangeHint(start, end);
        }
    }

    public void setEdgeEffectColor(int color) {
        setTopEdgeEffectColor(color);
        setBottomEdgeEffectColor(color);
    }

    public void setBottomEdgeEffectColor(int color) {
        this.mEdgeGlowBottom.setColor(color);
        invalidateEdgeEffects();
    }

    public void setTopEdgeEffectColor(int color) {
        this.mEdgeGlowTop.setColor(color);
        invalidateEdgeEffects();
    }

    public int getTopEdgeEffectColor() {
        return this.mEdgeGlowTop.getColor();
    }

    public int getBottomEdgeEffectColor() {
        return this.mEdgeGlowBottom.getColor();
    }

    public void setRecyclerListener(RecyclerListener listener) {
        this.mRecycler.mRecyclerListener = listener;
    }

    @Override // android.view.View
    public void onProvideContentCaptureStructure(ViewStructure structure, int flags) {
        super.onProvideContentCaptureStructure(structure, flags);
        if (!sContentCaptureReportingEnabledByDeviceConfig) {
            return;
        }
        Bundle extras = structure.getExtras();
        if (extras == null) {
            Log.wtf(TAG, "Unexpected null extras Bundle in ViewStructure");
            return;
        }
        int childCount = getChildCount();
        ArrayList<AutofillId> idsList = new ArrayList<>(childCount);
        for (int i = 0; i < childCount; i++) {
            View activeView = getChildAt(i);
            if (activeView != null) {
                idsList.add(activeView.getAutofillId());
            }
        }
        extras.putParcelableArrayList(ViewStructure.EXTRA_ACTIVE_CHILDREN_IDS, idsList);
        extras.putInt(ViewStructure.EXTRA_FIRST_ACTIVE_POSITION, getFirstVisiblePosition());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportActiveViewsToContentCapture() {
        ContentCaptureSession session;
        if (sContentCaptureReportingEnabledByDeviceConfig && (session = getContentCaptureSession()) != null) {
            ViewStructure structure = session.newViewStructure(this);
            onProvideContentCaptureStructure(structure, 0);
            session.notifyViewAppeared(structure);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class AdapterDataSetObserver extends AdapterView<ListAdapter>.AdapterDataSetObserver {
        /* JADX INFO: Access modifiers changed from: package-private */
        public AdapterDataSetObserver() {
            super();
        }

        @Override // android.widget.AdapterView.AdapterDataSetObserver, android.database.DataSetObserver
        public void onChanged() {
            super.onChanged();
            AbsListView.this.mReportChildrenToContentCaptureOnNextUpdate = true;
            if (AbsListView.this.mFastScroll != null) {
                AbsListView.this.mFastScroll.onSectionsChanged();
            }
        }

        @Override // android.widget.AdapterView.AdapterDataSetObserver, android.database.DataSetObserver
        public void onInvalidated() {
            super.onInvalidated();
            AbsListView.this.mReportChildrenToContentCaptureOnNextUpdate = true;
            if (AbsListView.this.mFastScroll != null) {
                AbsListView.this.mFastScroll.onSectionsChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class MultiChoiceModeWrapper implements MultiChoiceModeListener {
        private MultiChoiceModeListener mWrapped;

        MultiChoiceModeWrapper() {
        }

        public void setWrapped(MultiChoiceModeListener wrapped) {
            this.mWrapped = wrapped;
        }

        public boolean hasWrappedCallback() {
            return this.mWrapped != null;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (this.mWrapped.onCreateActionMode(mode, menu)) {
                AbsListView.this.setLongClickable(false);
                return true;
            }
            return false;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(mode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return this.mWrapped.onActionItemClicked(mode, item);
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode mode) {
            this.mWrapped.onDestroyActionMode(mode);
            AbsListView.this.mChoiceActionMode = null;
            AbsListView.this.clearChoices();
            AbsListView.this.mDataChanged = true;
            AbsListView.this.rememberSyncState();
            AbsListView.this.requestLayout();
            AbsListView.this.setLongClickable(true);
        }

        @Override // android.widget.AbsListView.MultiChoiceModeListener
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            this.mWrapped.onItemCheckedStateChanged(mode, position, id, checked);
            if (AbsListView.this.getCheckedItemCount() == 0) {
                mode.finish();
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        @ViewDebug.ExportedProperty(category = Slice.HINT_LIST)
        boolean forceAdd;
        boolean isEnabled;
        long itemId;
        @ViewDebug.ExportedProperty(category = Slice.HINT_LIST)
        boolean recycledHeaderFooter;
        int scrappedFromPosition;
        @ViewDebug.ExportedProperty(category = Slice.HINT_LIST, mapping = {@ViewDebug.IntToString(from = -1, m86to = "ITEM_VIEW_TYPE_IGNORE"), @ViewDebug.IntToString(from = -2, m86to = "ITEM_VIEW_TYPE_HEADER_OR_FOOTER")})
        int viewType;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.itemId = -1L;
        }

        public LayoutParams(int w, int h) {
            super(w, h);
            this.itemId = -1L;
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.itemId = -1L;
            this.viewType = viewType;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.itemId = -1L;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup.LayoutParams
        public void encodeProperties(ViewHierarchyEncoder encoder) {
            super.encodeProperties(encoder);
            encoder.addProperty("list:viewType", this.viewType);
            encoder.addProperty("list:recycledHeaderFooter", this.recycledHeaderFooter);
            encoder.addProperty("list:forceAdd", this.forceAdd);
            encoder.addProperty("list:isEnabled", this.isEnabled);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class RecycleBin {
        private View[] mActiveViews = new View[0];
        private ArrayList<View> mCurrentScrap;
        private int mFirstActivePosition;
        private RecyclerListener mRecyclerListener;
        private ArrayList<View>[] mScrapViews;
        private ArrayList<View> mSkippedScrap;
        private SparseArray<View> mTransientStateViews;
        private LongSparseArray<View> mTransientStateViewsById;
        private int mViewTypeCount;

        RecycleBin() {
        }

        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
            for (int i = 0; i < viewTypeCount; i++) {
                scrapViews[i] = new ArrayList<>();
            }
            this.mViewTypeCount = viewTypeCount;
            this.mCurrentScrap = scrapViews[0];
            this.mScrapViews = scrapViews;
        }

        public void markChildrenDirty() {
            if (this.mViewTypeCount == 1) {
                ArrayList<View> scrap = this.mCurrentScrap;
                int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    scrap.get(i).forceLayout();
                }
            } else {
                int typeCount = this.mViewTypeCount;
                for (int i2 = 0; i2 < typeCount; i2++) {
                    ArrayList<View> scrap2 = this.mScrapViews[i2];
                    int scrapCount2 = scrap2.size();
                    for (int j = 0; j < scrapCount2; j++) {
                        scrap2.get(j).forceLayout();
                    }
                }
            }
            SparseArray<View> sparseArray = this.mTransientStateViews;
            if (sparseArray != null) {
                int count = sparseArray.size();
                for (int i3 = 0; i3 < count; i3++) {
                    this.mTransientStateViews.valueAt(i3).forceLayout();
                }
            }
            LongSparseArray<View> longSparseArray = this.mTransientStateViewsById;
            if (longSparseArray != null) {
                int count2 = longSparseArray.size();
                for (int i4 = 0; i4 < count2; i4++) {
                    this.mTransientStateViewsById.valueAt(i4).forceLayout();
                }
            }
        }

        public boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void clear() {
            if (this.mViewTypeCount == 1) {
                ArrayList<View> scrap = this.mCurrentScrap;
                clearScrap(scrap);
            } else {
                int typeCount = this.mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    ArrayList<View> scrap2 = this.mScrapViews[i];
                    clearScrap(scrap2);
                }
            }
            clearTransientStateViews();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void fillActiveViews(int childCount, int firstActivePosition) {
            if (this.mActiveViews.length < childCount) {
                this.mActiveViews = new View[childCount];
            }
            this.mFirstActivePosition = firstActivePosition;
            View[] activeViews = this.mActiveViews;
            for (int i = 0; i < childCount; i++) {
                View child = AbsListView.this.getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp != null && lp.viewType != -2) {
                    activeViews[i] = child;
                    lp.scrappedFromPosition = firstActivePosition + i;
                }
            }
            if (AbsListView.this.mReportChildrenToContentCaptureOnNextUpdate && childCount > 0) {
                AbsListView.this.reportActiveViewsToContentCapture();
                AbsListView.this.mReportChildrenToContentCaptureOnNextUpdate = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public View getActiveView(int position) {
            int index = position - this.mFirstActivePosition;
            View[] activeViews = this.mActiveViews;
            if (index < 0 || index >= activeViews.length) {
                return null;
            }
            View match = activeViews[index];
            activeViews[index] = null;
            return match;
        }

        View getTransientStateView(int position) {
            int index;
            if (AbsListView.this.mAdapter != null && AbsListView.this.mAdapterHasStableIds && this.mTransientStateViewsById != null) {
                long id = AbsListView.this.mAdapter.getItemId(position);
                View result = this.mTransientStateViewsById.get(id);
                this.mTransientStateViewsById.remove(id);
                return result;
            }
            SparseArray<View> sparseArray = this.mTransientStateViews;
            if (sparseArray != null && (index = sparseArray.indexOfKey(position)) >= 0) {
                View result2 = this.mTransientStateViews.valueAt(index);
                this.mTransientStateViews.removeAt(index);
                return result2;
            }
            return null;
        }

        void clearTransientStateViews() {
            SparseArray<View> viewsByPos = this.mTransientStateViews;
            if (viewsByPos != null) {
                int N = viewsByPos.size();
                for (int i = 0; i < N; i++) {
                    removeDetachedView(viewsByPos.valueAt(i), false);
                }
                viewsByPos.clear();
            }
            LongSparseArray<View> viewsById = this.mTransientStateViewsById;
            if (viewsById != null) {
                int N2 = viewsById.size();
                for (int i2 = 0; i2 < N2; i2++) {
                    removeDetachedView(viewsById.valueAt(i2), false);
                }
                viewsById.clear();
            }
        }

        View getScrapView(int position) {
            int whichScrap = AbsListView.this.mAdapter.getItemViewType(position);
            if (whichScrap < 0) {
                return null;
            }
            if (this.mViewTypeCount == 1) {
                return retrieveFromScrap(this.mCurrentScrap, position);
            }
            ArrayList<View>[] arrayListArr = this.mScrapViews;
            if (whichScrap >= arrayListArr.length) {
                return null;
            }
            return retrieveFromScrap(arrayListArr[whichScrap], position);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addScrapView(View scrap, int position) {
            LayoutParams lp = (LayoutParams) scrap.getLayoutParams();
            if (lp == null) {
                return;
            }
            lp.scrappedFromPosition = position;
            int viewType = lp.viewType;
            if (!shouldRecycleViewType(viewType)) {
                if (viewType != -2) {
                    getSkippedScrap().add(scrap);
                    return;
                }
                return;
            }
            scrap.dispatchStartTemporaryDetach();
            AbsListView.this.notifyViewAccessibilityStateChangedIfNeeded(1);
            boolean scrapHasTransientState = scrap.hasTransientState();
            if (scrapHasTransientState) {
                if (AbsListView.this.mAdapter != null && AbsListView.this.mAdapterHasStableIds) {
                    if (this.mTransientStateViewsById == null) {
                        this.mTransientStateViewsById = new LongSparseArray<>();
                    }
                    this.mTransientStateViewsById.put(lp.itemId, scrap);
                    return;
                } else if (!AbsListView.this.mDataChanged) {
                    if (this.mTransientStateViews == null) {
                        this.mTransientStateViews = new SparseArray<>();
                    }
                    this.mTransientStateViews.put(position, scrap);
                    return;
                } else {
                    clearScrapForRebind(scrap);
                    getSkippedScrap().add(scrap);
                    return;
                }
            }
            clearScrapForRebind(scrap);
            if (this.mViewTypeCount == 1) {
                this.mCurrentScrap.add(scrap);
            } else {
                this.mScrapViews[viewType].add(scrap);
            }
            RecyclerListener recyclerListener = this.mRecyclerListener;
            if (recyclerListener != null) {
                recyclerListener.onMovedToScrapHeap(scrap);
            }
        }

        private ArrayList<View> getSkippedScrap() {
            if (this.mSkippedScrap == null) {
                this.mSkippedScrap = new ArrayList<>();
            }
            return this.mSkippedScrap;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void removeSkippedScrap() {
            ArrayList<View> arrayList = this.mSkippedScrap;
            if (arrayList == null) {
                return;
            }
            int count = arrayList.size();
            for (int i = 0; i < count; i++) {
                removeDetachedView(this.mSkippedScrap.get(i), false);
            }
            this.mSkippedScrap.clear();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void scrapActiveViews() {
            View[] activeViews = this.mActiveViews;
            boolean hasListener = this.mRecyclerListener != null;
            boolean multipleScraps = this.mViewTypeCount > 1;
            ArrayList<View> scrapViews = this.mCurrentScrap;
            int count = activeViews.length;
            for (int i = count - 1; i >= 0; i--) {
                View victim = activeViews[i];
                if (victim != null) {
                    LayoutParams lp = (LayoutParams) victim.getLayoutParams();
                    int whichScrap = lp.viewType;
                    activeViews[i] = null;
                    if (victim.hasTransientState()) {
                        victim.dispatchStartTemporaryDetach();
                        if (AbsListView.this.mAdapter != null && AbsListView.this.mAdapterHasStableIds) {
                            if (this.mTransientStateViewsById == null) {
                                this.mTransientStateViewsById = new LongSparseArray<>();
                            }
                            long id = AbsListView.this.mAdapter.getItemId(this.mFirstActivePosition + i);
                            this.mTransientStateViewsById.put(id, victim);
                        } else if (!AbsListView.this.mDataChanged) {
                            if (this.mTransientStateViews == null) {
                                this.mTransientStateViews = new SparseArray<>();
                            }
                            this.mTransientStateViews.put(this.mFirstActivePosition + i, victim);
                        } else if (whichScrap != -2) {
                            removeDetachedView(victim, false);
                        }
                    } else if (!shouldRecycleViewType(whichScrap)) {
                        if (whichScrap != -2) {
                            removeDetachedView(victim, false);
                        }
                    } else {
                        if (multipleScraps) {
                            scrapViews = this.mScrapViews[whichScrap];
                        }
                        lp.scrappedFromPosition = this.mFirstActivePosition + i;
                        removeDetachedView(victim, false);
                        scrapViews.add(victim);
                        if (hasListener) {
                            this.mRecyclerListener.onMovedToScrapHeap(victim);
                        }
                    }
                }
            }
            pruneScrapViews();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void fullyDetachScrapViews() {
            int viewTypeCount = this.mViewTypeCount;
            ArrayList<View>[] scrapViews = this.mScrapViews;
            for (int i = 0; i < viewTypeCount; i++) {
                ArrayList<View> scrapPile = scrapViews[i];
                for (int j = scrapPile.size() - 1; j >= 0; j--) {
                    View view = scrapPile.get(j);
                    if (view.isTemporarilyDetached()) {
                        removeDetachedView(view, false);
                    }
                }
            }
        }

        private void pruneScrapViews() {
            int maxViews = this.mActiveViews.length;
            int viewTypeCount = this.mViewTypeCount;
            ArrayList<View>[] scrapViews = this.mScrapViews;
            for (int i = 0; i < viewTypeCount; i++) {
                ArrayList<View> scrapPile = scrapViews[i];
                int size = scrapPile.size();
                while (size > maxViews) {
                    size--;
                    scrapPile.remove(size);
                }
            }
            SparseArray<View> transViewsByPos = this.mTransientStateViews;
            if (transViewsByPos != null) {
                int i2 = 0;
                while (i2 < transViewsByPos.size()) {
                    View v = transViewsByPos.valueAt(i2);
                    if (!v.hasTransientState()) {
                        removeDetachedView(v, false);
                        transViewsByPos.removeAt(i2);
                        i2--;
                    }
                    i2++;
                }
            }
            LongSparseArray<View> transViewsById = this.mTransientStateViewsById;
            if (transViewsById != null) {
                int i3 = 0;
                while (i3 < transViewsById.size()) {
                    View v2 = transViewsById.valueAt(i3);
                    if (!v2.hasTransientState()) {
                        removeDetachedView(v2, false);
                        transViewsById.removeAt(i3);
                        i3--;
                    }
                    i3++;
                }
            }
        }

        void reclaimScrapViews(List<View> views) {
            if (this.mViewTypeCount == 1) {
                views.addAll(this.mCurrentScrap);
                return;
            }
            int viewTypeCount = this.mViewTypeCount;
            ArrayList<View>[] scrapViews = this.mScrapViews;
            for (int i = 0; i < viewTypeCount; i++) {
                ArrayList<View> scrapPile = scrapViews[i];
                views.addAll(scrapPile);
            }
        }

        void setCacheColorHint(int color) {
            if (this.mViewTypeCount == 1) {
                ArrayList<View> scrap = this.mCurrentScrap;
                int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    scrap.get(i).setDrawingCacheBackgroundColor(color);
                }
            } else {
                int typeCount = this.mViewTypeCount;
                for (int i2 = 0; i2 < typeCount; i2++) {
                    ArrayList<View> scrap2 = this.mScrapViews[i2];
                    int scrapCount2 = scrap2.size();
                    for (int j = 0; j < scrapCount2; j++) {
                        scrap2.get(j).setDrawingCacheBackgroundColor(color);
                    }
                }
            }
            View[] activeViews = this.mActiveViews;
            for (View victim : activeViews) {
                if (victim != null) {
                    victim.setDrawingCacheBackgroundColor(color);
                }
            }
        }

        private View retrieveFromScrap(ArrayList<View> scrapViews, int position) {
            int size = scrapViews.size();
            if (size > 0) {
                for (int i = size - 1; i >= 0; i--) {
                    View view = scrapViews.get(i);
                    LayoutParams params = (LayoutParams) view.getLayoutParams();
                    if (AbsListView.this.mAdapterHasStableIds) {
                        long id = AbsListView.this.mAdapter.getItemId(position);
                        if (id == params.itemId) {
                            return scrapViews.remove(i);
                        }
                    } else if (params.scrappedFromPosition == position) {
                        View scrap = scrapViews.remove(i);
                        clearScrapForRebind(scrap);
                        return scrap;
                    }
                }
                int i2 = size - 1;
                View scrap2 = scrapViews.remove(i2);
                clearScrapForRebind(scrap2);
                return scrap2;
            }
            return null;
        }

        private void clearScrap(ArrayList<View> scrap) {
            int scrapCount = scrap.size();
            for (int j = 0; j < scrapCount; j++) {
                removeDetachedView(scrap.remove((scrapCount - 1) - j), false);
            }
        }

        private void clearScrapForRebind(View view) {
            view.clearAccessibilityFocus();
            view.setAccessibilityDelegate(null);
            view.resetSubtreeAutofillIds();
        }

        private void removeDetachedView(View child, boolean animate) {
            child.setAccessibilityDelegate(null);
            child.resetSubtreeAutofillIds();
            AbsListView.this.removeDetachedView(child, animate);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getHeightForPosition(int position) {
        int firstVisiblePosition = getFirstVisiblePosition();
        int childCount = getChildCount();
        int index = position - firstVisiblePosition;
        if (index >= 0 && index < childCount) {
            return getChildAt(index).getHeight();
        }
        View view = obtainView(position, this.mIsScrap);
        view.measure(this.mWidthMeasureSpec, 0);
        int height = view.getMeasuredHeight();
        this.mRecycler.addScrapView(view, position);
        return height;
    }

    public void setSelectionFromTop(int position, int y) {
        if (this.mAdapter == null) {
            return;
        }
        if (!isInTouchMode()) {
            position = lookForSelectablePosition(position, true);
            if (position >= 0) {
                setNextSelectedPositionInt(position);
            }
        } else {
            this.mResurrectToPosition = position;
        }
        if (position >= 0) {
            this.mLayoutMode = 4;
            this.mSpecificTop = this.mListPadding.top + y;
            if (this.mNeedSync) {
                this.mSyncPosition = position;
                this.mSyncRowId = this.mAdapter.getItemId(position);
            }
            AbsPositionScroller absPositionScroller = this.mPositionScroller;
            if (absPositionScroller != null) {
                absPositionScroller.stop();
            }
            requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void encodeProperties(ViewHierarchyEncoder encoder) {
        super.encodeProperties(encoder);
        encoder.addProperty("drawing:cacheColorHint", getCacheColorHint());
        encoder.addProperty("list:fastScrollEnabled", isFastScrollEnabled());
        encoder.addProperty("list:scrollingCacheEnabled", isScrollingCacheEnabled());
        encoder.addProperty("list:smoothScrollbarEnabled", isSmoothScrollbarEnabled());
        encoder.addProperty("list:stackFromBottom", isStackFromBottom());
        encoder.addProperty("list:textFilterEnabled", isTextFilterEnabled());
        View selectedView = getSelectedView();
        if (selectedView != null) {
            encoder.addPropertyKey("selectedView");
            selectedView.encode(encoder);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static abstract class AbsPositionScroller {
        public abstract void start(int i);

        public abstract void start(int i, int i2);

        public abstract void startWithOffset(int i, int i2);

        public abstract void startWithOffset(int i, int i2, int i3);

        public abstract void stop();

        AbsPositionScroller() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class PositionScroller extends AbsPositionScroller implements Runnable {
        private static final int MOVE_DOWN_BOUND = 3;
        private static final int MOVE_DOWN_POS = 1;
        private static final int MOVE_OFFSET = 5;
        private static final int MOVE_UP_BOUND = 4;
        private static final int MOVE_UP_POS = 2;
        private static final int SCROLL_DURATION = 200;
        private int mBoundPos;
        private final int mExtraScroll;
        private int mLastSeenPos;
        private int mMode;
        private int mOffsetFromTop;
        private int mScrollDuration;
        private int mTargetPos;

        PositionScroller() {
            this.mExtraScroll = ViewConfiguration.get(AbsListView.this.mContext).getScaledFadingEdgeLength();
        }

        @Override // android.widget.AbsListView.AbsPositionScroller
        public void start(final int position) {
            int viewTravelCount;
            stop();
            if (AbsListView.this.mDataChanged) {
                AbsListView.this.mPositionScrollAfterLayout = new Runnable() { // from class: android.widget.AbsListView.PositionScroller.1
                    @Override // java.lang.Runnable
                    public void run() {
                        PositionScroller.this.start(position);
                    }
                };
                return;
            }
            int childCount = AbsListView.this.getChildCount();
            if (childCount == 0) {
                return;
            }
            int firstPos = AbsListView.this.mFirstPosition;
            int lastPos = (firstPos + childCount) - 1;
            int clampedPosition = Math.max(0, Math.min(AbsListView.this.getCount() - 1, position));
            if (clampedPosition < firstPos) {
                viewTravelCount = (firstPos - clampedPosition) + 1;
                this.mMode = 2;
            } else if (clampedPosition > lastPos) {
                viewTravelCount = (clampedPosition - lastPos) + 1;
                this.mMode = 1;
            } else {
                scrollToVisible(clampedPosition, -1, 200);
                return;
            }
            if (viewTravelCount > 0) {
                this.mScrollDuration = 200 / viewTravelCount;
            } else {
                this.mScrollDuration = 200;
            }
            this.mTargetPos = clampedPosition;
            this.mBoundPos = -1;
            this.mLastSeenPos = -1;
            AbsListView.this.postOnAnimation(this);
        }

        @Override // android.widget.AbsListView.AbsPositionScroller
        public void start(final int position, final int boundPosition) {
            int viewTravelCount;
            stop();
            if (boundPosition == -1) {
                start(position);
            } else if (AbsListView.this.mDataChanged) {
                AbsListView.this.mPositionScrollAfterLayout = new Runnable() { // from class: android.widget.AbsListView.PositionScroller.2
                    @Override // java.lang.Runnable
                    public void run() {
                        PositionScroller.this.start(position, boundPosition);
                    }
                };
            } else {
                int childCount = AbsListView.this.getChildCount();
                if (childCount == 0) {
                    return;
                }
                int firstPos = AbsListView.this.mFirstPosition;
                int lastPos = (firstPos + childCount) - 1;
                int clampedPosition = Math.max(0, Math.min(AbsListView.this.getCount() - 1, position));
                if (clampedPosition < firstPos) {
                    int boundPosFromLast = lastPos - boundPosition;
                    if (boundPosFromLast < 1) {
                        return;
                    }
                    int posTravel = (firstPos - clampedPosition) + 1;
                    int boundTravel = boundPosFromLast - 1;
                    if (boundTravel < posTravel) {
                        viewTravelCount = boundTravel;
                        this.mMode = 4;
                    } else {
                        viewTravelCount = posTravel;
                        this.mMode = 2;
                    }
                } else if (clampedPosition > lastPos) {
                    int boundPosFromFirst = boundPosition - firstPos;
                    if (boundPosFromFirst < 1) {
                        return;
                    }
                    int posTravel2 = (clampedPosition - lastPos) + 1;
                    viewTravelCount = boundPosFromFirst - 1;
                    if (viewTravelCount < posTravel2) {
                        this.mMode = 3;
                    } else {
                        this.mMode = 1;
                        viewTravelCount = posTravel2;
                    }
                } else {
                    scrollToVisible(clampedPosition, boundPosition, 200);
                    return;
                }
                if (viewTravelCount > 0) {
                    this.mScrollDuration = 200 / viewTravelCount;
                } else {
                    this.mScrollDuration = 200;
                }
                this.mTargetPos = clampedPosition;
                this.mBoundPos = boundPosition;
                this.mLastSeenPos = -1;
                AbsListView.this.postOnAnimation(this);
            }
        }

        @Override // android.widget.AbsListView.AbsPositionScroller
        public void startWithOffset(int position, int offset) {
            startWithOffset(position, offset, 200);
        }

        @Override // android.widget.AbsListView.AbsPositionScroller
        public void startWithOffset(final int position, final int offset, final int duration) {
            int viewTravelCount;
            stop();
            if (AbsListView.this.mDataChanged) {
                AbsListView.this.mPositionScrollAfterLayout = new Runnable() { // from class: android.widget.AbsListView.PositionScroller.3
                    @Override // java.lang.Runnable
                    public void run() {
                        PositionScroller.this.startWithOffset(position, offset, duration);
                    }
                };
                return;
            }
            int childCount = AbsListView.this.getChildCount();
            if (childCount == 0) {
                return;
            }
            int offset2 = offset + AbsListView.this.getPaddingTop();
            this.mTargetPos = Math.max(0, Math.min(AbsListView.this.getCount() - 1, position));
            this.mOffsetFromTop = offset2;
            this.mBoundPos = -1;
            this.mLastSeenPos = -1;
            this.mMode = 5;
            int firstPos = AbsListView.this.mFirstPosition;
            int lastPos = (firstPos + childCount) - 1;
            int i = this.mTargetPos;
            if (i < firstPos) {
                viewTravelCount = firstPos - i;
            } else if (i > lastPos) {
                viewTravelCount = i - lastPos;
            } else {
                int targetTop = AbsListView.this.getChildAt(i - firstPos).getTop();
                AbsListView.this.smoothScrollBy(targetTop - offset2, duration, true, false);
                return;
            }
            float screenTravelCount = viewTravelCount / childCount;
            this.mScrollDuration = screenTravelCount < 1.0f ? duration : (int) (duration / screenTravelCount);
            this.mLastSeenPos = -1;
            AbsListView.this.postOnAnimation(this);
        }

        private void scrollToVisible(int targetPos, int boundPos, int duration) {
            int boundPos2 = boundPos;
            int firstPos = AbsListView.this.mFirstPosition;
            int childCount = AbsListView.this.getChildCount();
            int lastPos = (firstPos + childCount) - 1;
            int paddedTop = AbsListView.this.mListPadding.top;
            int paddedBottom = AbsListView.this.getHeight() - AbsListView.this.mListPadding.bottom;
            if (targetPos < firstPos || targetPos > lastPos) {
                Log.m104w(AbsListView.TAG, "scrollToVisible called with targetPos " + targetPos + " not visible [" + firstPos + ", " + lastPos + NavigationBarInflaterView.SIZE_MOD_END);
            }
            boundPos2 = (boundPos2 < firstPos || boundPos2 > lastPos) ? -1 : -1;
            View targetChild = AbsListView.this.getChildAt(targetPos - firstPos);
            int targetTop = targetChild.getTop();
            int targetBottom = targetChild.getBottom();
            int scrollBy = 0;
            if (targetBottom > paddedBottom) {
                scrollBy = targetBottom - paddedBottom;
            }
            if (targetTop < paddedTop) {
                scrollBy = targetTop - paddedTop;
            }
            if (scrollBy == 0) {
                return;
            }
            if (boundPos2 >= 0) {
                View boundChild = AbsListView.this.getChildAt(boundPos2 - firstPos);
                int boundTop = boundChild.getTop();
                int boundBottom = boundChild.getBottom();
                int absScroll = Math.abs(scrollBy);
                if (scrollBy < 0 && boundBottom + absScroll > paddedBottom) {
                    scrollBy = Math.max(0, boundBottom - paddedBottom);
                } else if (scrollBy > 0 && boundTop - absScroll < paddedTop) {
                    scrollBy = Math.min(0, boundTop - paddedTop);
                }
            }
            AbsListView.this.smoothScrollBy(scrollBy, duration);
        }

        @Override // android.widget.AbsListView.AbsPositionScroller
        public void stop() {
            AbsListView.this.removeCallbacks(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            int i;
            int listHeight = AbsListView.this.getHeight();
            int firstPos = AbsListView.this.mFirstPosition;
            switch (this.mMode) {
                case 1:
                    int lastViewIndex = AbsListView.this.getChildCount() - 1;
                    int lastPos = firstPos + lastViewIndex;
                    if (lastViewIndex < 0) {
                        return;
                    }
                    if (lastPos == this.mLastSeenPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    View lastView = AbsListView.this.getChildAt(lastViewIndex);
                    int lastViewHeight = lastView.getHeight();
                    int lastViewPixelsShowing = listHeight - lastView.getTop();
                    int extraScroll = lastPos < AbsListView.this.mItemCount - 1 ? Math.max(AbsListView.this.mListPadding.bottom, this.mExtraScroll) : AbsListView.this.mListPadding.bottom;
                    int scrollBy = (lastViewHeight - lastViewPixelsShowing) + extraScroll;
                    AbsListView.this.smoothScrollBy(scrollBy, this.mScrollDuration, true, lastPos < this.mTargetPos);
                    this.mLastSeenPos = lastPos;
                    if (lastPos < this.mTargetPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    return;
                case 2:
                    int nextViewIndex = this.mLastSeenPos;
                    if (firstPos == nextViewIndex) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    View firstView = AbsListView.this.getChildAt(0);
                    if (firstView == null) {
                        return;
                    }
                    int firstViewTop = firstView.getTop();
                    int extraScroll2 = firstPos > 0 ? Math.max(this.mExtraScroll, AbsListView.this.mListPadding.top) : AbsListView.this.mListPadding.top;
                    AbsListView.this.smoothScrollBy(firstViewTop - extraScroll2, this.mScrollDuration, true, firstPos > this.mTargetPos);
                    this.mLastSeenPos = firstPos;
                    if (firstPos > this.mTargetPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    return;
                case 3:
                    int childCount = AbsListView.this.getChildCount();
                    if (firstPos != this.mBoundPos && childCount > 1) {
                        if (firstPos + childCount < AbsListView.this.mItemCount) {
                            int nextPos = firstPos + 1;
                            if (nextPos == this.mLastSeenPos) {
                                AbsListView.this.postOnAnimation(this);
                                return;
                            }
                            View nextView = AbsListView.this.getChildAt(1);
                            int nextViewHeight = nextView.getHeight();
                            int nextViewTop = nextView.getTop();
                            int extraScroll3 = Math.max(AbsListView.this.mListPadding.bottom, this.mExtraScroll);
                            if (nextPos < this.mBoundPos) {
                                AbsListView.this.smoothScrollBy(Math.max(0, (nextViewHeight + nextViewTop) - extraScroll3), this.mScrollDuration, true, true);
                                this.mLastSeenPos = nextPos;
                                AbsListView.this.postOnAnimation(this);
                                return;
                            } else if (nextViewTop > extraScroll3) {
                                AbsListView.this.smoothScrollBy(nextViewTop - extraScroll3, this.mScrollDuration, true, false);
                                return;
                            } else {
                                AbsListView.this.reportScrollStateChange(0);
                                return;
                            }
                        }
                        i = 0;
                    } else {
                        i = 0;
                    }
                    AbsListView.this.reportScrollStateChange(i);
                    return;
                case 4:
                    int lastViewIndex2 = AbsListView.this.getChildCount() - 2;
                    if (lastViewIndex2 < 0) {
                        return;
                    }
                    int lastPos2 = firstPos + lastViewIndex2;
                    if (lastPos2 == this.mLastSeenPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    View lastView2 = AbsListView.this.getChildAt(lastViewIndex2);
                    int lastViewHeight2 = lastView2.getHeight();
                    int lastViewTop = lastView2.getTop();
                    int lastViewPixelsShowing2 = listHeight - lastViewTop;
                    int extraScroll4 = Math.max(AbsListView.this.mListPadding.top, this.mExtraScroll);
                    this.mLastSeenPos = lastPos2;
                    if (lastPos2 > this.mBoundPos) {
                        AbsListView.this.smoothScrollBy(-(lastViewPixelsShowing2 - extraScroll4), this.mScrollDuration, true, true);
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    int bottom = listHeight - extraScroll4;
                    int lastViewBottom = lastViewTop + lastViewHeight2;
                    if (bottom > lastViewBottom) {
                        AbsListView.this.smoothScrollBy(-(bottom - lastViewBottom), this.mScrollDuration, true, false);
                        return;
                    } else {
                        AbsListView.this.reportScrollStateChange(0);
                        return;
                    }
                case 5:
                    if (this.mLastSeenPos == firstPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    this.mLastSeenPos = firstPos;
                    int childCount2 = AbsListView.this.getChildCount();
                    if (childCount2 <= 0) {
                        return;
                    }
                    int position = this.mTargetPos;
                    int lastPos3 = (firstPos + childCount2) - 1;
                    View firstChild = AbsListView.this.getChildAt(0);
                    int firstChildHeight = firstChild.getHeight();
                    View lastChild = AbsListView.this.getChildAt(childCount2 - 1);
                    int lastChildHeight = lastChild.getHeight();
                    float firstPositionVisiblePart = ((float) firstChildHeight) == 0.0f ? 1.0f : (firstChild.getTop() + firstChildHeight) / firstChildHeight;
                    float lastPositionVisiblePart = ((float) lastChildHeight) == 0.0f ? 1.0f : ((AbsListView.this.getHeight() + lastChildHeight) - lastChild.getBottom()) / lastChildHeight;
                    float viewTravelCount = 0.0f;
                    if (position < firstPos) {
                        viewTravelCount = (firstPos - position) + (1.0f - firstPositionVisiblePart) + 1.0f;
                    } else if (position > lastPos3) {
                        viewTravelCount = (position - lastPos3) + (1.0f - lastPositionVisiblePart);
                    }
                    float screenTravelCount = viewTravelCount / childCount2;
                    float modifier = Math.min(Math.abs(screenTravelCount), 1.0f);
                    if (position < firstPos) {
                        int duration = (int) (this.mScrollDuration * modifier);
                        AbsListView.this.smoothScrollBy((int) ((-AbsListView.this.getHeight()) * modifier), duration, true, true);
                        AbsListView.this.postOnAnimation(this);
                        return;
                    } else if (position > lastPos3) {
                        int duration2 = (int) (this.mScrollDuration * modifier);
                        AbsListView.this.smoothScrollBy((int) (AbsListView.this.getHeight() * modifier), duration2, true, true);
                        AbsListView.this.postOnAnimation(this);
                        return;
                    } else {
                        int targetTop = AbsListView.this.getChildAt(position - firstPos).getTop();
                        int distance = targetTop - this.mOffsetFromTop;
                        int duration3 = (int) (this.mScrollDuration * (Math.abs(distance) / AbsListView.this.getHeight()));
                        AbsListView.this.smoothScrollBy(distance, duration3, true, false);
                        return;
                    }
                default:
                    return;
            }
        }
    }
}
