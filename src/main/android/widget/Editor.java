package android.widget;

import android.C0001R;
import android.animation.ValueAnimator;
import android.app.AppGlobals;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.UndoManager;
import android.content.UndoOperation;
import android.content.UndoOwner;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.RenderNode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.LocaleList;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ParcelableParcel;
import android.p008os.SystemClock;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.InsertModeTransformationMethod;
import android.text.method.KeyListener;
import android.text.method.MetaKeyKeyListener;
import android.text.method.MovementMethod;
import android.text.method.OffsetMapping;
import android.text.method.TransformationMethod;
import android.text.method.WordIterator;
import android.text.style.EasyEditSpan;
import android.text.style.SuggestionRangeSpan;
import android.text.style.SuggestionSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContentInfo;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassificationManager;
import android.widget.AdapterView;
import android.widget.Editor;
import android.widget.Magnifier;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.window.OnBackInvokedCallback;
import com.android.internal.C4057R;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.inputmethod.EditableInputConnection;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.transition.EpicenterTranslateClipReveal;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.view.FloatingActionMode;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes4.dex */
public class Editor {
    private static final int ACTION_MODE_MENU_ITEM_ORDER_ASSIST = 0;
    private static final int ACTION_MODE_MENU_ITEM_ORDER_AUTOFILL = 10;
    private static final int ACTION_MODE_MENU_ITEM_ORDER_COPY = 5;
    private static final int ACTION_MODE_MENU_ITEM_ORDER_CUT = 4;
    private static final int ACTION_MODE_MENU_ITEM_ORDER_PASTE = 6;
    private static final int ACTION_MODE_MENU_ITEM_ORDER_PASTE_AS_PLAIN_TEXT = 11;
    private static final int ACTION_MODE_MENU_ITEM_ORDER_PROCESS_TEXT_INTENT_ACTIONS_START = 100;
    private static final int ACTION_MODE_MENU_ITEM_ORDER_REPLACE = 9;
    private static final int ACTION_MODE_MENU_ITEM_ORDER_SECONDARY_ASSIST_ACTIONS_START = 50;
    private static final int ACTION_MODE_MENU_ITEM_ORDER_SELECT_ALL = 8;
    private static final int ACTION_MODE_MENU_ITEM_ORDER_SHARE = 7;
    static final int BLINK = 500;
    private static final int CONTEXT_MENU_GROUP_CLIPBOARD = 2;
    private static final int CONTEXT_MENU_GROUP_MISC = 3;
    private static final int CONTEXT_MENU_GROUP_UNDO_REDO = 1;
    private static final int CONTEXT_MENU_ITEM_ORDER_REPLACE = 11;
    private static final int CURSOR_START_FLOAT_DISTANCE_PX = 20;
    private static final boolean DEBUG_UNDO = false;
    private static final int DELAY_BEFORE_HANDLE_FADES_OUT = 4000;
    private static final int DRAG_SHADOW_MAX_TEXT_LENGTH = 20;
    static final int EXTRACT_NOTHING = -2;
    static final int EXTRACT_UNKNOWN = -1;
    private static final int FLAG_MISSPELLED_OR_GRAMMAR_ERROR = 10;
    private static final boolean FLAG_USE_MAGNIFIER = true;
    private static final boolean FLAG_USE_NEW_CONTEXT_MENU = false;
    public static final int HANDLE_TYPE_SELECTION_END = 1;
    public static final int HANDLE_TYPE_SELECTION_START = 0;
    private static final int LINE_CHANGE_SLOP_MAX_DP = 45;
    private static final int LINE_CHANGE_SLOP_MIN_DP = 8;
    private static final int MAX_LINE_HEIGHT_FOR_MAGNIFIER = 32;
    private static final int MIN_LINE_HEIGHT_FOR_MAGNIFIER = 20;
    private static final int RECENT_CUT_COPY_DURATION_MS = 15000;
    private static final String TAG = "Editor";
    private static final String UNDO_OWNER_TAG = "Editor";
    private static final int UNSET_LINE = -1;
    private static final int UNSET_X_VALUE = -1;
    private final AccessibilitySmartActions mA11ySmartActions;
    boolean mAllowUndo;
    private final OnBackInvokedCallback mBackCallback;
    private boolean mBackCallbackRegistered;
    private Blink mBlink;
    private float mContextMenuAnchorX;
    private float mContextMenuAnchorY;
    private CorrectionHighlighter mCorrectionHighlighter;
    boolean mCreatedWithASelection;
    private final CursorAnchorInfoNotifier mCursorAnchorInfoNotifier;
    private float mCursorDragDirectionMinXYRatio;
    boolean mCursorVisible;
    ActionMode.Callback mCustomInsertionActionModeCallback;
    ActionMode.Callback mCustomSelectionActionModeCallback;
    private final TextViewOnReceiveContentListener mDefaultOnReceiveContentListener = new TextViewOnReceiveContentListener();
    boolean mDiscardNextActionUp;
    private boolean mDrawCursorOnMagnifier;
    Drawable mDrawableForCursor;
    CharSequence mError;
    private ErrorPopup mErrorPopup;
    boolean mErrorWasChanged;
    private boolean mFlagCursorDragFromAnywhereEnabled;
    private boolean mFlagInsertionHandleGesturesEnabled;
    boolean mFrozenWithFocus;
    private final boolean mHapticTextHandleEnabled;
    private boolean mHasPendingRestartInputForSetText;
    boolean mIgnoreActionUpEvent;
    boolean mInBatchEditControllers;
    private float mInitialZoom;
    InputContentType mInputContentType;
    InputMethodState mInputMethodState;
    int mInputType;
    private InsertModeController mInsertModeController;
    private Runnable mInsertionActionModeRunnable;
    private boolean mInsertionControllerEnabled;
    InsertionPointCursorController mInsertionPointCursorController;
    boolean mIsBeingLongClicked;
    boolean mIsBeingLongClickedByAccessibility;
    boolean mIsInsertionActionModeStartPending;
    KeyListener mKeyListener;
    private int mLastButtonState;
    private int mLineChangeSlopMax;
    private int mLineChangeSlopMin;
    private final float mLineSlopRatio;
    private MagnifierMotionAnimator mMagnifierAnimator;
    private final ViewTreeObserver.OnDrawListener mMagnifierOnDrawListener;
    private int mMaxLineHeightForMagnifier;
    private final MetricsLogger mMetricsLogger;
    private int mMinLineHeightForMagnifier;
    private final boolean mNewMagnifierEnabled;
    private final MenuItem.OnMenuItemClickListener mOnContextMenuItemClickListener;
    private PositionListener mPositionListener;
    private boolean mPreserveSelection;
    final ProcessTextIntentActionsHandler mProcessTextIntentActionsHandler;
    private boolean mRenderCursorRegardlessTiming;
    private boolean mRequestingLinkActionMode;
    private boolean mRestartActionModeOnNextRefresh;
    boolean mSelectAllOnFocus;
    Drawable mSelectHandleCenter;
    Drawable mSelectHandleLeft;
    Drawable mSelectHandleRight;
    private SelectionActionModeHelper mSelectionActionModeHelper;
    private boolean mSelectionControllerEnabled;
    SelectionModifierCursorController mSelectionModifierCursorController;
    boolean mSelectionMoved;
    private long mShowCursor;
    private boolean mShowErrorAfterAttach;
    private final Runnable mShowFloatingToolbar;
    boolean mShowSoftInputOnFocus;
    private Runnable mShowSuggestionRunnable;
    private SpanController mSpanController;
    SpellChecker mSpellChecker;
    private final SuggestionHelper mSuggestionHelper;
    SuggestionRangeSpan mSuggestionRangeSpan;
    private SuggestionsPopupWindow mSuggestionsPopupWindow;
    private Rect mTempRect;
    private ActionMode mTextActionMode;
    boolean mTextIsSelectable;
    private TextRenderNode[] mTextRenderNodes;
    private final TextView mTextView;
    boolean mTouchFocusSelected;
    private final EditorTouchState mTouchState;
    final UndoInputFilter mUndoInputFilter;
    private final UndoManager mUndoManager;
    private UndoOwner mUndoOwner;
    private final Runnable mUpdateMagnifierRunnable;
    private boolean mUpdateWordIteratorText;
    private WordIterator mWordIterator;
    private WordIterator mWordIteratorWithText;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface CursorController extends ViewTreeObserver.OnTouchModeChangeListener {
        void hide();

        boolean isActive();

        boolean isCursorBeingModified();

        void onDetached();

        void show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface EasyEditDeleteListener {
        void onDeleteClick(EasyEditSpan easyEditSpan);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface HandleType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    private @interface MagnifierHandleTrigger {
        public static final int INSERTION = 0;
        public static final int SELECTION_END = 2;
        public static final int SELECTION_START = 1;
    }

    /* loaded from: classes4.dex */
    @interface TextActionMode {
        public static final int INSERTION = 1;
        public static final int SELECTION = 0;
        public static final int TEXT_LINK = 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface TextViewPositionListener {
        void updatePosition(int i, int i2, boolean z, boolean z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class TextRenderNode {
        boolean isDirty = true;
        boolean needsToBeShifted = true;
        RenderNode renderNode;

        public TextRenderNode(String name) {
            this.renderNode = RenderNode.create(name, null);
        }

        boolean needsRecord() {
            return this.isDirty || !this.renderNode.hasDisplayList();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Editor(TextView textView) {
        boolean z;
        boolean z2;
        UndoManager undoManager = new UndoManager();
        this.mUndoManager = undoManager;
        this.mUndoOwner = undoManager.getOwner("Editor", this);
        this.mUndoInputFilter = new UndoInputFilter(this);
        this.mAllowUndo = true;
        this.mMetricsLogger = new MetricsLogger();
        this.mBackCallback = new OnBackInvokedCallback() { // from class: android.widget.Editor$$ExternalSyntheticLambda2
            @Override // android.window.OnBackInvokedCallback
            public final void onBackInvoked() {
                Editor.this.lambda$startActionModeInternal$0();
            }
        };
        this.mUpdateMagnifierRunnable = new Runnable() { // from class: android.widget.Editor.1
            @Override // java.lang.Runnable
            public void run() {
                Editor.this.mMagnifierAnimator.update();
            }
        };
        this.mMagnifierOnDrawListener = new ViewTreeObserver.OnDrawListener() { // from class: android.widget.Editor.2
            @Override // android.view.ViewTreeObserver.OnDrawListener
            public void onDraw() {
                if (Editor.this.mMagnifierAnimator != null) {
                    Editor.this.mTextView.post(Editor.this.mUpdateMagnifierRunnable);
                }
            }
        };
        this.mHasPendingRestartInputForSetText = false;
        this.mInputType = 0;
        this.mCursorVisible = true;
        this.mShowSoftInputOnFocus = true;
        this.mDrawableForCursor = null;
        this.mTouchState = new EditorTouchState();
        this.mCursorAnchorInfoNotifier = new CursorAnchorInfoNotifier();
        this.mShowFloatingToolbar = new Runnable() { // from class: android.widget.Editor.3
            @Override // java.lang.Runnable
            public void run() {
                if (Editor.this.mTextActionMode != null) {
                    Editor.this.mTextActionMode.hide(0L);
                }
            }
        };
        this.mIsInsertionActionModeStartPending = false;
        this.mSuggestionHelper = new SuggestionHelper();
        this.mInitialZoom = 1.0f;
        this.mOnContextMenuItemClickListener = new MenuItem.OnMenuItemClickListener() { // from class: android.widget.Editor.5
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem item) {
                if (Editor.this.mProcessTextIntentActionsHandler.performMenuItemAction(item)) {
                    return true;
                }
                return Editor.this.mTextView.onTextContextMenuItem(item.getItemId());
            }
        };
        this.mTextView = textView;
        textView.setFilters(textView.getFilters());
        this.mProcessTextIntentActionsHandler = new ProcessTextIntentActionsHandler();
        this.mA11ySmartActions = new AccessibilitySmartActions(textView);
        this.mHapticTextHandleEnabled = textView.getContext().getResources().getBoolean(C4057R.bool.config_enableHapticTextHandle);
        if (AppGlobals.getIntCoreSetting(WidgetFlags.KEY_ENABLE_CURSOR_DRAG_FROM_ANYWHERE, 1) != 0) {
            z = true;
        } else {
            z = false;
        }
        this.mFlagCursorDragFromAnywhereEnabled = z;
        int cursorDragMinAngleFromVertical = AppGlobals.getIntCoreSetting(WidgetFlags.KEY_CURSOR_DRAG_MIN_ANGLE_FROM_VERTICAL, 45);
        this.mCursorDragDirectionMinXYRatio = EditorTouchState.getXYRatio(cursorDragMinAngleFromVertical);
        if (AppGlobals.getIntCoreSetting(WidgetFlags.KEY_ENABLE_INSERTION_HANDLE_GESTURES, 0) != 0) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.mFlagInsertionHandleGesturesEnabled = z2;
        this.mNewMagnifierEnabled = AppGlobals.getIntCoreSetting(WidgetFlags.KEY_ENABLE_NEW_MAGNIFIER, 0) != 0;
        this.mLineSlopRatio = AppGlobals.getFloatCoreSetting(WidgetFlags.KEY_LINE_SLOP_RATIO, 0.5f);
        this.mLineChangeSlopMax = (int) TypedValue.applyDimension(1, 45.0f, textView.getContext().getResources().getDisplayMetrics());
        this.mLineChangeSlopMin = (int) TypedValue.applyDimension(1, 8.0f, textView.getContext().getResources().getDisplayMetrics());
    }

    public boolean getFlagCursorDragFromAnywhereEnabled() {
        return this.mFlagCursorDragFromAnywhereEnabled;
    }

    public void setFlagCursorDragFromAnywhereEnabled(boolean enabled) {
        this.mFlagCursorDragFromAnywhereEnabled = enabled;
    }

    public void setCursorDragMinAngleFromVertical(int degreesFromVertical) {
        this.mCursorDragDirectionMinXYRatio = EditorTouchState.getXYRatio(degreesFromVertical);
    }

    public boolean getFlagInsertionHandleGesturesEnabled() {
        return this.mFlagInsertionHandleGesturesEnabled;
    }

    public void setFlagInsertionHandleGesturesEnabled(boolean enabled) {
        this.mFlagInsertionHandleGesturesEnabled = enabled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MagnifierMotionAnimator getMagnifierAnimator() {
        Magnifier.Builder builder;
        if (this.mMagnifierAnimator == null) {
            if (this.mNewMagnifierEnabled) {
                builder = createBuilderWithInlineMagnifierDefaults();
            } else {
                builder = Magnifier.createBuilderWithOldMagnifierDefaults(this.mTextView);
            }
            this.mMagnifierAnimator = new MagnifierMotionAnimator(builder.build());
        }
        return this.mMagnifierAnimator;
    }

    private Magnifier.Builder createBuilderWithInlineMagnifierDefaults() {
        Magnifier.Builder params = new Magnifier.Builder(this.mTextView);
        float zoom = AppGlobals.getFloatCoreSetting(WidgetFlags.KEY_MAGNIFIER_ZOOM_FACTOR, 1.5f);
        float aspectRatio = AppGlobals.getFloatCoreSetting(WidgetFlags.KEY_MAGNIFIER_ASPECT_RATIO, 5.5f);
        zoom = (zoom < 1.2f || zoom > 1.8f) ? 1.5f : 1.5f;
        aspectRatio = (aspectRatio < 3.0f || aspectRatio > 8.0f) ? 5.5f : 5.5f;
        this.mInitialZoom = zoom;
        this.mMinLineHeightForMagnifier = (int) TypedValue.applyDimension(1, 20.0f, this.mTextView.getContext().getResources().getDisplayMetrics());
        this.mMaxLineHeightForMagnifier = (int) TypedValue.applyDimension(1, 32.0f, this.mTextView.getContext().getResources().getDisplayMetrics());
        Layout layout = this.mTextView.getLayout();
        int line = layout.getLineForOffset(this.mTextView.getSelectionStartTransformed());
        int sourceHeight = layout.getLineBottom(line, false) - layout.getLineTop(line);
        int height = (int) (sourceHeight * zoom);
        int width = (int) (Math.max(sourceHeight, this.mMinLineHeightForMagnifier) * aspectRatio);
        params.setFishEyeStyle().setSize(width, height).setSourceSize(width, sourceHeight).setElevation(0.0f).setInitialZoom(zoom).setClippingEnabled(false);
        Context context = this.mTextView.getContext();
        TypedArray a = context.obtainStyledAttributes(null, C4057R.styleable.Magnifier, C4057R.attr.magnifierStyle, 0);
        params.setDefaultSourceToMagnifierOffset(a.getDimensionPixelSize(3, 0), a.getDimensionPixelSize(4, 0));
        a.recycle();
        return params.setSourceBounds(1, 0, 1, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParcelableParcel saveInstanceState() {
        ParcelableParcel state = new ParcelableParcel(getClass().getClassLoader());
        Parcel parcel = state.getParcel();
        this.mUndoManager.saveInstanceState(parcel);
        this.mUndoInputFilter.saveInstanceState(parcel);
        return state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restoreInstanceState(ParcelableParcel state) {
        Parcel parcel = state.getParcel();
        this.mUndoManager.restoreInstanceState(parcel, state.getClassLoader());
        this.mUndoInputFilter.restoreInstanceState(parcel);
        this.mUndoOwner = this.mUndoManager.getOwner("Editor", this);
    }

    public TextViewOnReceiveContentListener getDefaultOnReceiveContentListener() {
        return this.mDefaultOnReceiveContentListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void forgetUndoRedo() {
        UndoOwner[] owners = {this.mUndoOwner};
        this.mUndoManager.forgetUndos(owners, -1);
        this.mUndoManager.forgetRedos(owners, -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canUndo() {
        UndoOwner[] owners = {this.mUndoOwner};
        return this.mAllowUndo && this.mUndoManager.countUndos(owners) > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canRedo() {
        UndoOwner[] owners = {this.mUndoOwner};
        return this.mAllowUndo && this.mUndoManager.countRedos(owners) > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void undo() {
        if (!this.mAllowUndo) {
            return;
        }
        UndoOwner[] owners = {this.mUndoOwner};
        this.mUndoManager.undo(owners, 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void redo() {
        if (!this.mAllowUndo) {
            return;
        }
        UndoOwner[] owners = {this.mUndoOwner};
        this.mUndoManager.redo(owners, 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void replace() {
        if (this.mSuggestionsPopupWindow == null) {
            this.mSuggestionsPopupWindow = new SuggestionsPopupWindow();
        }
        hideCursorAndSpanControllers();
        this.mSuggestionsPopupWindow.show();
        int middle = (this.mTextView.getSelectionStart() + this.mTextView.getSelectionEnd()) / 2;
        Selection.setSelection((Spannable) this.mTextView.getText(), middle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAttachedToWindow() {
        if (this.mShowErrorAfterAttach) {
            showError();
            this.mShowErrorAfterAttach = false;
        }
        ViewTreeObserver observer = this.mTextView.getViewTreeObserver();
        if (observer.isAlive()) {
            InsertionPointCursorController insertionPointCursorController = this.mInsertionPointCursorController;
            if (insertionPointCursorController != null) {
                observer.addOnTouchModeChangeListener(insertionPointCursorController);
            }
            SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
            if (selectionModifierCursorController != null) {
                selectionModifierCursorController.resetTouchOffsets();
                observer.addOnTouchModeChangeListener(this.mSelectionModifierCursorController);
            }
            observer.addOnDrawListener(this.mMagnifierOnDrawListener);
        }
        updateSpellCheckSpans(0, this.mTextView.getText().length(), true);
        if (this.mTextView.hasSelection()) {
            refreshTextActionMode();
        }
        getPositionListener().addSubscriber(this.mCursorAnchorInfoNotifier, true);
        makeBlink();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDetachedFromWindow() {
        getPositionListener().removeSubscriber(this.mCursorAnchorInfoNotifier);
        if (this.mError != null) {
            hideError();
        }
        suspendBlink();
        InsertionPointCursorController insertionPointCursorController = this.mInsertionPointCursorController;
        if (insertionPointCursorController != null) {
            insertionPointCursorController.onDetached();
        }
        SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
        if (selectionModifierCursorController != null) {
            selectionModifierCursorController.onDetached();
        }
        Runnable runnable = this.mShowSuggestionRunnable;
        if (runnable != null) {
            this.mTextView.removeCallbacks(runnable);
        }
        Runnable runnable2 = this.mInsertionActionModeRunnable;
        if (runnable2 != null) {
            this.mTextView.removeCallbacks(runnable2);
        }
        this.mTextView.removeCallbacks(this.mShowFloatingToolbar);
        discardTextDisplayLists();
        SpellChecker spellChecker = this.mSpellChecker;
        if (spellChecker != null) {
            spellChecker.closeSession();
            this.mSpellChecker = null;
        }
        ViewTreeObserver observer = this.mTextView.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.removeOnDrawListener(this.mMagnifierOnDrawListener);
        }
        hideCursorAndSpanControllers();
        stopTextActionModeWithPreservingSelection();
        this.mDefaultOnReceiveContentListener.clearInputConnectionInfo();
        unregisterOnBackInvokedCallback();
    }

    private void unregisterOnBackInvokedCallback() {
        ViewRootImpl viewRootImpl;
        if (this.mBackCallbackRegistered && (viewRootImpl = getTextView().getViewRootImpl()) != null && viewRootImpl.getOnBackInvokedDispatcher().isOnBackInvokedCallbackEnabled()) {
            viewRootImpl.getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(this.mBackCallback);
            this.mBackCallbackRegistered = false;
        }
    }

    private void registerOnBackInvokedCallback() {
        ViewRootImpl viewRootImpl;
        if (!this.mBackCallbackRegistered && (viewRootImpl = this.mTextView.getViewRootImpl()) != null && viewRootImpl.getOnBackInvokedDispatcher().isOnBackInvokedCallbackEnabled()) {
            viewRootImpl.getOnBackInvokedDispatcher().registerOnBackInvokedCallback(0, this.mBackCallback);
            this.mBackCallbackRegistered = true;
        }
    }

    private void discardTextDisplayLists() {
        if (this.mTextRenderNodes != null) {
            int i = 0;
            while (true) {
                TextRenderNode[] textRenderNodeArr = this.mTextRenderNodes;
                if (i < textRenderNodeArr.length) {
                    TextRenderNode textRenderNode = textRenderNodeArr[i];
                    RenderNode displayList = textRenderNode != null ? textRenderNode.renderNode : null;
                    if (displayList != null && displayList.hasDisplayList()) {
                        displayList.discardDisplayList();
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private void showError() {
        if (this.mTextView.getWindowToken() == null) {
            this.mShowErrorAfterAttach = true;
            return;
        }
        if (this.mErrorPopup == null) {
            LayoutInflater inflater = LayoutInflater.from(this.mTextView.getContext());
            TextView err = (TextView) inflater.inflate(C4057R.layout.textview_hint, (ViewGroup) null);
            float scale = this.mTextView.getResources().getDisplayMetrics().density;
            ErrorPopup errorPopup = new ErrorPopup(err, (int) ((200.0f * scale) + 0.5f), (int) ((50.0f * scale) + 0.5f));
            this.mErrorPopup = errorPopup;
            errorPopup.setFocusable(false);
            this.mErrorPopup.setInputMethodMode(1);
        }
        TextView tv = (TextView) this.mErrorPopup.getContentView();
        chooseSize(this.mErrorPopup, this.mError, tv);
        tv.setText(this.mError);
        this.mErrorPopup.showAsDropDown(this.mTextView, getErrorX(), getErrorY(), 51);
        ErrorPopup errorPopup2 = this.mErrorPopup;
        errorPopup2.fixDirection(errorPopup2.isAboveAnchor());
    }

    public void setError(CharSequence error, Drawable icon) {
        CharSequence stringOrSpannedString = TextUtils.stringOrSpannedString(error);
        this.mError = stringOrSpannedString;
        this.mErrorWasChanged = true;
        if (stringOrSpannedString == null) {
            setErrorIcon(null);
            ErrorPopup errorPopup = this.mErrorPopup;
            if (errorPopup != null) {
                if (errorPopup.isShowing()) {
                    this.mErrorPopup.dismiss();
                }
                this.mErrorPopup = null;
            }
            this.mShowErrorAfterAttach = false;
            return;
        }
        setErrorIcon(icon);
        if (this.mTextView.isFocused()) {
            showError();
        }
    }

    private void setErrorIcon(Drawable icon) {
        TextView.Drawables dr = this.mTextView.mDrawables;
        if (dr == null) {
            TextView textView = this.mTextView;
            TextView.Drawables drawables = new TextView.Drawables(textView.getContext());
            dr = drawables;
            textView.mDrawables = drawables;
        }
        dr.setErrorDrawable(icon, this.mTextView);
        this.mTextView.resetResolvedDrawables();
        this.mTextView.invalidate();
        this.mTextView.requestLayout();
    }

    private void hideError() {
        ErrorPopup errorPopup = this.mErrorPopup;
        if (errorPopup != null && errorPopup.isShowing()) {
            this.mErrorPopup.dismiss();
        }
        this.mShowErrorAfterAttach = false;
    }

    private int getErrorX() {
        int offset;
        float scale = this.mTextView.getResources().getDisplayMetrics().density;
        TextView.Drawables dr = this.mTextView.mDrawables;
        int layoutDirection = this.mTextView.getLayoutDirection();
        switch (layoutDirection) {
            case 1:
                offset = dr != null ? dr.mDrawableSizeLeft : 0;
                int errorX = this.mTextView.getPaddingLeft() + ((offset / 2) - ((int) ((25.0f * scale) + 0.5f)));
                return errorX;
            default:
                offset = dr != null ? dr.mDrawableSizeRight : 0;
                int errorX2 = ((this.mTextView.getWidth() - this.mErrorPopup.getWidth()) - this.mTextView.getPaddingRight()) + ((-offset) / 2) + ((int) ((25.0f * scale) + 0.5f));
                return errorX2;
        }
    }

    private int getErrorY() {
        int compoundPaddingTop = this.mTextView.getCompoundPaddingTop();
        int vspace = ((this.mTextView.getBottom() - this.mTextView.getTop()) - this.mTextView.getCompoundPaddingBottom()) - compoundPaddingTop;
        TextView.Drawables dr = this.mTextView.mDrawables;
        int layoutDirection = this.mTextView.getLayoutDirection();
        int height = 0;
        switch (layoutDirection) {
            case 1:
                if (dr != null) {
                    height = dr.mDrawableHeightLeft;
                    break;
                }
                break;
            default:
                if (dr != null) {
                    height = dr.mDrawableHeightRight;
                    break;
                }
                break;
        }
        int icontop = ((vspace - height) / 2) + compoundPaddingTop;
        float scale = this.mTextView.getResources().getDisplayMetrics().density;
        return ((icontop + height) - this.mTextView.getHeight()) - ((int) ((2.0f * scale) + 0.5f));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void createInputContentTypeIfNeeded() {
        if (this.mInputContentType == null) {
            this.mInputContentType = new InputContentType();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void createInputMethodStateIfNeeded() {
        if (this.mInputMethodState == null) {
            this.mInputMethodState = new InputMethodState();
        }
    }

    private boolean isCursorVisible() {
        return this.mCursorVisible && this.mTextView.isTextEditable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldRenderCursor() {
        if (isCursorVisible()) {
            if (this.mRenderCursorRegardlessTiming) {
                return true;
            }
            long showCursorDelta = SystemClock.uptimeMillis() - this.mShowCursor;
            return showCursorDelta % 1000 < 500;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void prepareCursorControllers() {
        boolean windowSupportsHandles = false;
        ViewGroup.LayoutParams params = this.mTextView.getRootView().getLayoutParams();
        boolean z = true;
        if (params instanceof WindowManager.LayoutParams) {
            WindowManager.LayoutParams windowParams = (WindowManager.LayoutParams) params;
            windowSupportsHandles = windowParams.type < 1000 || windowParams.type > 1999;
        }
        boolean enabled = windowSupportsHandles && this.mTextView.getLayout() != null;
        this.mInsertionControllerEnabled = enabled && (this.mDrawCursorOnMagnifier || isCursorVisible());
        if (!enabled || !this.mTextView.textCanBeSelected()) {
            z = false;
        }
        this.mSelectionControllerEnabled = z;
        if (!this.mInsertionControllerEnabled) {
            hideInsertionPointCursorController();
            InsertionPointCursorController insertionPointCursorController = this.mInsertionPointCursorController;
            if (insertionPointCursorController != null) {
                insertionPointCursorController.onDetached();
                this.mInsertionPointCursorController = null;
            }
        }
        if (!this.mSelectionControllerEnabled) {
            lambda$startActionModeInternal$0();
            SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
            if (selectionModifierCursorController != null) {
                selectionModifierCursorController.onDetached();
                this.mSelectionModifierCursorController = null;
            }
        }
    }

    void hideInsertionPointCursorController() {
        InsertionPointCursorController insertionPointCursorController = this.mInsertionPointCursorController;
        if (insertionPointCursorController != null) {
            insertionPointCursorController.hide();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideCursorAndSpanControllers() {
        hideCursorControllers();
        hideSpanControllers();
    }

    private void hideSpanControllers() {
        SpanController spanController = this.mSpanController;
        if (spanController != null) {
            spanController.hide();
        }
    }

    private void hideCursorControllers() {
        if (this.mSuggestionsPopupWindow != null && (this.mTextView.isInExtractedMode() || !this.mSuggestionsPopupWindow.isShowingUp())) {
            this.mSuggestionsPopupWindow.hide();
        }
        hideInsertionPointCursorController();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSpellCheckSpans(int start, int end, boolean createSpellChecker) {
        this.mTextView.removeAdjacentSuggestionSpans(start);
        this.mTextView.removeAdjacentSuggestionSpans(end);
        if (this.mTextView.isTextEditable() && this.mTextView.isSuggestionsEnabled() && !this.mTextView.isInExtractedMode()) {
            InputMethodManager imm = getInputMethodManager();
            if (imm != null && imm.isInputMethodSuppressingSpellChecker()) {
                return;
            }
            if (this.mSpellChecker == null && createSpellChecker) {
                this.mSpellChecker = new SpellChecker(this.mTextView);
            }
            SpellChecker spellChecker = this.mSpellChecker;
            if (spellChecker != null) {
                spellChecker.spellCheck(start, end);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onScreenStateChanged(int screenState) {
        switch (screenState) {
            case 0:
                suspendBlink();
                return;
            case 1:
                resumeBlink();
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void suspendBlink() {
        Blink blink = this.mBlink;
        if (blink != null) {
            blink.cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resumeBlink() {
        Blink blink = this.mBlink;
        if (blink != null) {
            blink.uncancel();
            makeBlink();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void adjustInputType(boolean password, boolean passwordInputType, boolean webPasswordInputType, boolean numberPasswordInputType) {
        int i = this.mInputType;
        if ((i & 15) == 1) {
            if (password || passwordInputType) {
                this.mInputType = (i & (-4081)) | 128;
            }
            if (webPasswordInputType) {
                this.mInputType = (this.mInputType & (-4081)) | 224;
            }
        } else if ((i & 15) == 2 && numberPasswordInputType) {
            this.mInputType = (i & (-4081)) | 16;
        }
    }

    private void chooseSize(PopupWindow pop, CharSequence text, TextView tv) {
        int wid = tv.getPaddingLeft() + tv.getPaddingRight();
        int ht = tv.getPaddingTop() + tv.getPaddingBottom();
        int defaultWidthInPixels = this.mTextView.getResources().getDimensionPixelSize(C4057R.dimen.textview_error_popup_default_width);
        StaticLayout l = StaticLayout.Builder.obtain(text, 0, text.length(), tv.getPaint(), defaultWidthInPixels).setUseLineSpacingFromFallbacks(tv.isFallbackLineSpacingForStaticLayout()).build();
        float max = 0.0f;
        for (int i = 0; i < l.getLineCount(); i++) {
            max = Math.max(max, l.getLineWidth(i));
        }
        pop.setWidth(((int) Math.ceil(max)) + wid);
        pop.setHeight(l.getHeight() + ht);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFrame() {
        ErrorPopup errorPopup = this.mErrorPopup;
        if (errorPopup != null) {
            TextView tv = (TextView) errorPopup.getContentView();
            chooseSize(this.mErrorPopup, this.mError, tv);
            this.mErrorPopup.update(this.mTextView, getErrorX(), getErrorY(), this.mErrorPopup.getWidth(), this.mErrorPopup.getHeight());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getWordStart(int offset) {
        int retOffset;
        int retOffset2 = getWordIteratorWithText().prevBoundary(offset);
        if (getWordIteratorWithText().isOnPunctuation(retOffset2)) {
            retOffset = getWordIteratorWithText().getPunctuationBeginning(offset);
        } else {
            retOffset = getWordIteratorWithText().getPrevWordBeginningOnTwoWordsBoundary(offset);
        }
        if (retOffset == -1) {
            return offset;
        }
        return retOffset;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getWordEnd(int offset) {
        int retOffset;
        int retOffset2 = getWordIteratorWithText().nextBoundary(offset);
        if (getWordIteratorWithText().isAfterPunctuation(retOffset2)) {
            retOffset = getWordIteratorWithText().getPunctuationEnd(offset);
        } else {
            retOffset = getWordIteratorWithText().getNextWordEndOnTwoWordBoundary(offset);
        }
        if (retOffset == -1) {
            return offset;
        }
        return retOffset;
    }

    private boolean needsToSelectAllToSelectWordOrParagraph() {
        if (this.mTextView.hasPasswordTransformationMethod()) {
            return true;
        }
        int inputType = this.mTextView.getInputType();
        int klass = inputType & 15;
        int variation = inputType & InputType.TYPE_MASK_VARIATION;
        return klass == 2 || klass == 3 || klass == 4 || variation == 16 || variation == 32 || variation == 208 || variation == 176;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean selectCurrentWord() {
        int selectionStart;
        int selectionEnd;
        if (this.mTextView.canSelectText()) {
            if (needsToSelectAllToSelectWordOrParagraph()) {
                return this.mTextView.selectAllText();
            }
            long lastTouchOffsets = getLastTouchOffsets();
            int minOffset = TextUtils.unpackRangeStartFromLong(lastTouchOffsets);
            int maxOffset = TextUtils.unpackRangeEndFromLong(lastTouchOffsets);
            if (minOffset < 0 || minOffset > this.mTextView.getText().length() || maxOffset < 0 || maxOffset > this.mTextView.getText().length()) {
                return false;
            }
            URLSpan[] urlSpans = (URLSpan[]) ((Spanned) this.mTextView.getText()).getSpans(minOffset, maxOffset, URLSpan.class);
            if (urlSpans.length >= 1) {
                URLSpan urlSpan = urlSpans[0];
                selectionStart = ((Spanned) this.mTextView.getText()).getSpanStart(urlSpan);
                selectionEnd = ((Spanned) this.mTextView.getText()).getSpanEnd(urlSpan);
            } else {
                WordIterator wordIterator = getWordIterator();
                wordIterator.setCharSequence(this.mTextView.getText(), minOffset, maxOffset);
                selectionStart = wordIterator.getBeginning(minOffset);
                int selectionEnd2 = wordIterator.getEnd(maxOffset);
                if (selectionStart == -1 || selectionEnd2 == -1 || selectionStart == selectionEnd2) {
                    long range = getCharClusterRange(minOffset);
                    selectionStart = TextUtils.unpackRangeStartFromLong(range);
                    selectionEnd = TextUtils.unpackRangeEndFromLong(range);
                } else {
                    selectionEnd = selectionEnd2;
                }
            }
            Selection.setSelection((Spannable) this.mTextView.getText(), selectionStart, selectionEnd);
            return selectionEnd > selectionStart;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean selectCurrentParagraph() {
        if (this.mTextView.canSelectText()) {
            if (needsToSelectAllToSelectWordOrParagraph()) {
                return this.mTextView.selectAllText();
            }
            long lastTouchOffsets = getLastTouchOffsets();
            int minLastTouchOffset = TextUtils.unpackRangeStartFromLong(lastTouchOffsets);
            int maxLastTouchOffset = TextUtils.unpackRangeEndFromLong(lastTouchOffsets);
            long paragraphsRange = getParagraphsRange(minLastTouchOffset, maxLastTouchOffset);
            int start = TextUtils.unpackRangeStartFromLong(paragraphsRange);
            int end = TextUtils.unpackRangeEndFromLong(paragraphsRange);
            if (start < end) {
                Selection.setSelection((Spannable) this.mTextView.getText(), start, end);
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getParagraphsRange(int startOffset, int endOffset) {
        int startOffsetTransformed = this.mTextView.originalToTransformed(startOffset, 1);
        int endOffsetTransformed = this.mTextView.originalToTransformed(endOffset, 1);
        Layout layout = this.mTextView.getLayout();
        if (layout == null) {
            return TextUtils.packRangeInLong(-1, -1);
        }
        CharSequence text = layout.getText();
        int minLine = layout.getLineForOffset(startOffsetTransformed);
        while (minLine > 0) {
            int prevLineEndOffset = layout.getLineEnd(minLine - 1);
            if (text.charAt(prevLineEndOffset - 1) == '\n') {
                break;
            }
            minLine--;
        }
        int maxLine = layout.getLineForOffset(endOffsetTransformed);
        while (maxLine < layout.getLineCount() - 1) {
            int lineEndOffset = layout.getLineEnd(maxLine);
            if (text.charAt(lineEndOffset - 1) == '\n') {
                break;
            }
            maxLine++;
        }
        int paragraphStart = this.mTextView.transformedToOriginal(layout.getLineStart(minLine), 1);
        int paragraphEnd = this.mTextView.transformedToOriginal(layout.getLineEnd(maxLine), 1);
        return TextUtils.packRangeInLong(paragraphStart, paragraphEnd);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onLocaleChanged() {
        this.mWordIterator = null;
        this.mWordIteratorWithText = null;
    }

    public WordIterator getWordIterator() {
        if (this.mWordIterator == null) {
            this.mWordIterator = new WordIterator(this.mTextView.getTextServicesLocale());
        }
        return this.mWordIterator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public WordIterator getWordIteratorWithText() {
        if (this.mWordIteratorWithText == null) {
            this.mWordIteratorWithText = new WordIterator(this.mTextView.getTextServicesLocale());
            this.mUpdateWordIteratorText = true;
        }
        if (this.mUpdateWordIteratorText) {
            CharSequence text = this.mTextView.getText();
            this.mWordIteratorWithText.setCharSequence(text, 0, text.length());
            this.mUpdateWordIteratorText = false;
        }
        return this.mWordIteratorWithText;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getNextCursorOffset(int offset, boolean findAfterGivenOffset) {
        int nextCursor;
        Layout layout = this.mTextView.getLayout();
        if (layout == null) {
            return offset;
        }
        int offsetTransformed = this.mTextView.originalToTransformed(offset, 1);
        if (findAfterGivenOffset == layout.isRtlCharAt(offsetTransformed)) {
            nextCursor = layout.getOffsetToLeftOf(offsetTransformed);
        } else {
            nextCursor = layout.getOffsetToRightOf(offsetTransformed);
        }
        return this.mTextView.transformedToOriginal(nextCursor, 1);
    }

    private long getCharClusterRange(int offset) {
        int textLength = this.mTextView.getText().length();
        if (offset < textLength) {
            int clusterEndOffset = getNextCursorOffset(offset, true);
            return TextUtils.packRangeInLong(getNextCursorOffset(clusterEndOffset, false), clusterEndOffset);
        } else if (offset - 1 >= 0) {
            int clusterStartOffset = getNextCursorOffset(offset, false);
            return TextUtils.packRangeInLong(clusterStartOffset, getNextCursorOffset(clusterStartOffset, true));
        } else {
            return TextUtils.packRangeInLong(offset, offset);
        }
    }

    private boolean touchPositionIsInSelection() {
        int selectionStart = this.mTextView.getSelectionStart();
        int selectionEnd = this.mTextView.getSelectionEnd();
        if (selectionStart == selectionEnd) {
            return false;
        }
        if (selectionStart > selectionEnd) {
            selectionStart = selectionEnd;
            selectionEnd = selectionStart;
            Selection.setSelection((Spannable) this.mTextView.getText(), selectionStart, selectionEnd);
        }
        SelectionModifierCursorController selectionController = getSelectionController();
        int minOffset = selectionController.getMinTouchOffset();
        int maxOffset = selectionController.getMaxTouchOffset();
        return minOffset >= selectionStart && maxOffset < selectionEnd;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PositionListener getPositionListener() {
        if (this.mPositionListener == null) {
            this.mPositionListener = new PositionListener();
        }
        return this.mPositionListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isOffsetVisible(int offset) {
        Layout layout = this.mTextView.getLayout();
        if (layout == null) {
            return false;
        }
        int offsetTransformed = this.mTextView.originalToTransformed(offset, 1);
        int line = layout.getLineForOffset(offsetTransformed);
        int lineBottom = layout.getLineBottom(line);
        int primaryHorizontal = (int) layout.getPrimaryHorizontal(offsetTransformed);
        TextView textView = this.mTextView;
        return textView.isPositionVisible(textView.viewportToContentHorizontalOffset() + primaryHorizontal, this.mTextView.viewportToContentVerticalOffset() + lineBottom);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPositionOnText(float x, float y) {
        Layout layout = this.mTextView.getLayout();
        if (layout == null) {
            return false;
        }
        int line = this.mTextView.getLineAtCoordinate(y);
        float x2 = this.mTextView.convertToLocalHorizontalCoordinate(x);
        if (x2 < layout.getLineLeft(line) || x2 > layout.getLineRight(line)) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDragAndDrop() {
        getSelectionActionModeHelper().onSelectionDrag();
        if (this.mTextView.isInExtractedMode()) {
            return;
        }
        int start = this.mTextView.getSelectionStart();
        int end = this.mTextView.getSelectionEnd();
        CharSequence selectedText = this.mTextView.getTransformedText(start, end);
        ClipData data = ClipData.newPlainText(null, selectedText);
        DragLocalState localState = new DragLocalState(this.mTextView, start, end);
        this.mTextView.startDragAndDrop(data, getTextThumbnailBuilder(start, end), localState, 256);
        lambda$startActionModeInternal$0();
        if (hasSelectionController()) {
            getSelectionController().resetTouchOffsets();
        }
    }

    public boolean performLongClick(boolean handled) {
        if (this.mIsBeingLongClickedByAccessibility) {
            if (!handled) {
                toggleInsertionActionMode();
            }
            return true;
        }
        if (!handled && !isPositionOnText(this.mTouchState.getLastDownX(), this.mTouchState.getLastDownY()) && !this.mTouchState.isOnHandle() && this.mInsertionControllerEnabled) {
            int offset = this.mTextView.getOffsetForPosition(this.mTouchState.getLastDownX(), this.mTouchState.getLastDownY());
            Selection.setSelection((Spannable) this.mTextView.getText(), offset);
            getInsertionController().show();
            this.mIsInsertionActionModeStartPending = true;
            handled = true;
            MetricsLogger.action(this.mTextView.getContext(), (int) MetricsProto.MetricsEvent.TEXT_LONGPRESS, 0);
        }
        if (!handled && this.mTextActionMode != null) {
            if (touchPositionIsInSelection()) {
                startDragAndDrop();
                MetricsLogger.action(this.mTextView.getContext(), (int) MetricsProto.MetricsEvent.TEXT_LONGPRESS, 2);
            } else {
                lambda$startActionModeInternal$0();
                selectCurrentWordAndStartDrag();
                MetricsLogger.action(this.mTextView.getContext(), (int) MetricsProto.MetricsEvent.TEXT_LONGPRESS, 1);
            }
            handled = true;
        }
        if (!handled && (handled = selectCurrentWordAndStartDrag())) {
            MetricsLogger.action(this.mTextView.getContext(), (int) MetricsProto.MetricsEvent.TEXT_LONGPRESS, 1);
        }
        return handled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleInsertionActionMode() {
        if (this.mTextActionMode != null) {
            lambda$startActionModeInternal$0();
        } else {
            startInsertionActionMode();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getLastUpPositionX() {
        return this.mTouchState.getLastUpX();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getLastUpPositionY() {
        return this.mTouchState.getLastUpY();
    }

    private long getLastTouchOffsets() {
        SelectionModifierCursorController selectionController = getSelectionController();
        int minOffset = selectionController.getMinTouchOffset();
        int maxOffset = selectionController.getMaxTouchOffset();
        return TextUtils.packRangeInLong(minOffset, maxOffset);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onFocusChanged(boolean focused, int direction) {
        this.mShowCursor = SystemClock.uptimeMillis();
        ensureEndedBatchEdit();
        if (focused) {
            int selStart = this.mTextView.getSelectionStart();
            int selEnd = this.mTextView.getSelectionEnd();
            boolean isFocusHighlighted = this.mSelectAllOnFocus && selStart == 0 && selEnd == this.mTextView.getText().length();
            this.mCreatedWithASelection = this.mFrozenWithFocus && this.mTextView.hasSelection() && !isFocusHighlighted;
            if (!this.mFrozenWithFocus || selStart < 0 || selEnd < 0) {
                int lastTapPosition = getLastTapPosition();
                if (lastTapPosition >= 0) {
                    Selection.setSelection((Spannable) this.mTextView.getText(), lastTapPosition);
                }
                MovementMethod mMovement = this.mTextView.getMovementMethod();
                if (mMovement != null) {
                    TextView textView = this.mTextView;
                    mMovement.onTakeFocus(textView, (Spannable) textView.getText(), direction);
                }
                if ((this.mTextView.isInExtractedMode() || this.mSelectionMoved) && selStart >= 0 && selEnd >= 0) {
                    Selection.setSelection((Spannable) this.mTextView.getText(), selStart, selEnd);
                }
                if (this.mSelectAllOnFocus) {
                    this.mTextView.selectAllText();
                }
                this.mTouchFocusSelected = true;
            }
            this.mFrozenWithFocus = false;
            this.mSelectionMoved = false;
            if (this.mError != null) {
                showError();
            }
            makeBlink();
            return;
        }
        if (this.mError != null) {
            hideError();
        }
        this.mTextView.onEndBatchEdit();
        if (this.mTextView.isInExtractedMode()) {
            hideCursorAndSpanControllers();
            stopTextActionModeWithPreservingSelection();
        } else {
            hideCursorAndSpanControllers();
            if (this.mTextView.isTemporarilyDetached()) {
                stopTextActionModeWithPreservingSelection();
            } else {
                lambda$startActionModeInternal$0();
            }
            downgradeEasyCorrectionSpans();
        }
        SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
        if (selectionModifierCursorController != null) {
            selectionModifierCursorController.resetTouchOffsets();
        }
        InsertModeController insertModeController = this.mInsertModeController;
        if (insertModeController != null) {
            insertModeController.exitInsertMode();
        }
        ensureNoSelectionIfNonSelectable();
    }

    private void ensureNoSelectionIfNonSelectable() {
        if (!this.mTextView.textCanBeSelected() && this.mTextView.hasSelection()) {
            Selection.setSelection((Spannable) this.mTextView.getText(), this.mTextView.length(), this.mTextView.length());
        }
    }

    private void downgradeEasyCorrectionSpans() {
        CharSequence text = this.mTextView.getText();
        if (text instanceof Spannable) {
            Spannable spannable = (Spannable) text;
            SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) spannable.getSpans(0, spannable.length(), SuggestionSpan.class);
            for (int i = 0; i < suggestionSpans.length; i++) {
                int flags = suggestionSpans[i].getFlags();
                if ((flags & 1) != 0 && (flags & 10) == 0) {
                    suggestionSpans[i].setFlags(flags & (-2));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendOnTextChanged(int start, int before, int after) {
        getSelectionActionModeHelper().onTextChanged(start, start + before);
        updateSpellCheckSpans(start, start + after, false);
        this.mUpdateWordIteratorText = true;
        hideCursorControllers();
        SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
        if (selectionModifierCursorController != null) {
            selectionModifierCursorController.resetTouchOffsets();
        }
        lambda$startActionModeInternal$0();
    }

    private int getLastTapPosition() {
        int lastTapPosition;
        SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
        if (selectionModifierCursorController != null && (lastTapPosition = selectionModifierCursorController.getMinTouchOffset()) >= 0) {
            if (lastTapPosition > this.mTextView.getText().length()) {
                return this.mTextView.getText().length();
            }
            return lastTapPosition;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            resumeBlink();
            if (this.mTextView.hasSelection() && !extractedTextModeWillBeStarted()) {
                refreshTextActionMode();
                return;
            }
            return;
        }
        suspendBlink();
        InputContentType inputContentType = this.mInputContentType;
        if (inputContentType != null) {
            inputContentType.enterDown = false;
        }
        hideCursorAndSpanControllers();
        stopTextActionModeWithPreservingSelection();
        SuggestionsPopupWindow suggestionsPopupWindow = this.mSuggestionsPopupWindow;
        if (suggestionsPopupWindow != null) {
            suggestionsPopupWindow.onParentLostFocus();
        }
        ensureEndedBatchEdit();
        ensureNoSelectionIfNonSelectable();
    }

    private boolean shouldFilterOutTouchEvent(MotionEvent event) {
        if (event.isFromSource(8194)) {
            boolean primaryButtonStateChanged = ((this.mLastButtonState ^ event.getButtonState()) & 1) != 0;
            int action = event.getActionMasked();
            if ((action == 0 || action == 1) && !primaryButtonStateChanged) {
                return true;
            }
            return action == 2 && !event.isButtonPressed(1);
        }
        return false;
    }

    public void onTouchEvent(MotionEvent event) {
        boolean filterOutEvent = shouldFilterOutTouchEvent(event);
        this.mLastButtonState = event.getButtonState();
        if (filterOutEvent) {
            if (event.getActionMasked() == 1) {
                this.mDiscardNextActionUp = true;
                return;
            }
            return;
        }
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this.mTextView.getContext());
        this.mTouchState.update(event, viewConfiguration);
        updateFloatingToolbarVisibility(event);
        if (hasInsertionController()) {
            getInsertionController().onTouchEvent(event);
        }
        if (hasSelectionController()) {
            getSelectionController().onTouchEvent(event);
        }
        Runnable runnable = this.mShowSuggestionRunnable;
        if (runnable != null) {
            this.mTextView.removeCallbacks(runnable);
            this.mShowSuggestionRunnable = null;
        }
        if (event.getActionMasked() == 0) {
            this.mTouchFocusSelected = false;
            this.mIgnoreActionUpEvent = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFloatingToolbarVisibility(MotionEvent event) {
        if (this.mTextActionMode != null) {
            switch (event.getActionMasked()) {
                case 1:
                case 3:
                    showFloatingToolbar();
                    return;
                case 2:
                    hideFloatingToolbar(-1);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideFloatingToolbar(int duration) {
        if (this.mTextActionMode != null) {
            this.mTextView.removeCallbacks(this.mShowFloatingToolbar);
            this.mTextActionMode.hide(duration);
        }
    }

    private void showFloatingToolbar() {
        if (this.mTextActionMode != null && this.mTextView.showUIForTouchScreen()) {
            int delay = ViewConfiguration.getDoubleTapTimeout();
            this.mTextView.postDelayed(this.mShowFloatingToolbar, delay);
            invalidateActionModeAsync();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InputMethodManager getInputMethodManager() {
        return (InputMethodManager) this.mTextView.getContext().getSystemService(InputMethodManager.class);
    }

    public void beginBatchEdit() {
        this.mInBatchEditControllers = true;
        InputMethodState ims = this.mInputMethodState;
        if (ims != null) {
            int nesting = ims.mBatchEditNesting + 1;
            ims.mBatchEditNesting = nesting;
            if (nesting == 1) {
                ims.mCursorChanged = false;
                ims.mChangedDelta = 0;
                if (ims.mContentChanged) {
                    ims.mChangedStart = 0;
                    ims.mChangedEnd = this.mTextView.getText().length();
                } else {
                    ims.mChangedStart = -1;
                    ims.mChangedEnd = -1;
                    ims.mContentChanged = false;
                }
                this.mUndoInputFilter.beginBatchEdit();
                this.mTextView.onBeginBatchEdit();
            }
        }
    }

    public void endBatchEdit() {
        this.mInBatchEditControllers = false;
        InputMethodState ims = this.mInputMethodState;
        if (ims != null) {
            int nesting = ims.mBatchEditNesting - 1;
            ims.mBatchEditNesting = nesting;
            if (nesting == 0) {
                finishBatchEdit(ims);
            }
        }
    }

    void ensureEndedBatchEdit() {
        InputMethodState ims = this.mInputMethodState;
        if (ims != null && ims.mBatchEditNesting != 0) {
            ims.mBatchEditNesting = 0;
            finishBatchEdit(ims);
        }
    }

    void finishBatchEdit(InputMethodState ims) {
        this.mTextView.onEndBatchEdit();
        this.mUndoInputFilter.endBatchEdit();
        if (ims.mContentChanged || ims.mSelectionModeChanged) {
            this.mTextView.updateAfterEdit();
            reportExtractedText();
        } else if (ims.mCursorChanged) {
            this.mTextView.invalidateCursor();
        }
        sendUpdateSelection();
        if (this.mTextActionMode != null) {
            CursorController cursorController = this.mTextView.hasSelection() ? getSelectionController() : getInsertionController();
            if (cursorController != null && !cursorController.isActive() && !cursorController.isCursorBeingModified() && this.mTextView.showUIForTouchScreen()) {
                cursorController.show();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void scheduleRestartInputForSetText() {
        this.mHasPendingRestartInputForSetText = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void maybeFireScheduledRestartInputForSetText() {
        if (this.mHasPendingRestartInputForSetText) {
            InputMethodManager imm = getInputMethodManager();
            if (imm != null) {
                imm.invalidateInput(this.mTextView);
            }
            this.mHasPendingRestartInputForSetText = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean extractText(ExtractedTextRequest request, ExtractedText outText) {
        return extractTextInternal(request, -1, -1, -1, outText);
    }

    private boolean extractTextInternal(ExtractedTextRequest request, int partialStartOffset, int partialEndOffset, int delta, ExtractedText outText) {
        CharSequence content;
        int partialEndOffset2;
        if (request == null || outText == null || (content = this.mTextView.getText()) == null) {
            return false;
        }
        if (partialStartOffset != -2) {
            int N = content.length();
            if (partialStartOffset < 0) {
                outText.partialEndOffset = -1;
                outText.partialStartOffset = -1;
                partialStartOffset = 0;
                partialEndOffset2 = N;
            } else {
                partialEndOffset2 = partialEndOffset + delta;
                if (content instanceof Spanned) {
                    Spanned spanned = (Spanned) content;
                    Object[] spans = spanned.getSpans(partialStartOffset, partialEndOffset2, ParcelableSpan.class);
                    int i = spans.length;
                    while (i > 0) {
                        i--;
                        int j = spanned.getSpanStart(spans[i]);
                        if (j < partialStartOffset) {
                            partialStartOffset = j;
                        }
                        int j2 = spanned.getSpanEnd(spans[i]);
                        if (j2 > partialEndOffset2) {
                            partialEndOffset2 = j2;
                        }
                    }
                }
                outText.partialStartOffset = partialStartOffset;
                outText.partialEndOffset = partialEndOffset2 - delta;
                if (partialStartOffset > N) {
                    partialStartOffset = N;
                } else if (partialStartOffset < 0) {
                    partialStartOffset = 0;
                }
                if (partialEndOffset2 > N) {
                    partialEndOffset2 = N;
                } else if (partialEndOffset2 < 0) {
                    partialEndOffset2 = 0;
                }
            }
            if ((request.flags & 1) != 0) {
                outText.text = content.subSequence(partialStartOffset, partialEndOffset2);
            } else {
                outText.text = TextUtils.substring(content, partialStartOffset, partialEndOffset2);
            }
        } else {
            outText.partialStartOffset = 0;
            outText.partialEndOffset = 0;
            outText.text = "";
        }
        outText.flags = 0;
        if (MetaKeyKeyListener.getMetaState(content, 2048) != 0) {
            outText.flags |= 2;
        }
        if (this.mTextView.isSingleLine()) {
            outText.flags |= 1;
        }
        outText.startOffset = 0;
        outText.selectionStart = this.mTextView.getSelectionStart();
        outText.selectionEnd = this.mTextView.getSelectionEnd();
        outText.hint = this.mTextView.getHint();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean reportExtractedText() {
        InputMethodManager imm;
        InputMethodState ims = this.mInputMethodState;
        if (ims == null) {
            return false;
        }
        boolean wasContentChanged = ims.mContentChanged;
        if (!wasContentChanged && !ims.mSelectionModeChanged) {
            return false;
        }
        ims.mContentChanged = false;
        ims.mSelectionModeChanged = false;
        ExtractedTextRequest req = ims.mExtractedTextRequest;
        if (req == null || (imm = getInputMethodManager()) == null) {
            return false;
        }
        if (ims.mChangedStart < 0 && !wasContentChanged) {
            ims.mChangedStart = -2;
        }
        if (!extractTextInternal(req, ims.mChangedStart, ims.mChangedEnd, ims.mChangedDelta, ims.mExtractedText)) {
            return false;
        }
        imm.updateExtractedText(this.mTextView, req.token, ims.mExtractedText);
        ims.mChangedStart = -1;
        ims.mChangedEnd = -1;
        ims.mChangedDelta = 0;
        ims.mContentChanged = false;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendUpdateSelection() {
        InputMethodManager imm;
        int candStart;
        int candEnd;
        InputMethodState inputMethodState = this.mInputMethodState;
        if (inputMethodState != null && inputMethodState.mBatchEditNesting <= 0 && !this.mHasPendingRestartInputForSetText && (imm = getInputMethodManager()) != null) {
            int selectionStart = this.mTextView.getSelectionStart();
            int selectionEnd = this.mTextView.getSelectionEnd();
            if (!(this.mTextView.getText() instanceof Spannable)) {
                candStart = -1;
                candEnd = -1;
            } else {
                Spannable sp = (Spannable) this.mTextView.getText();
                int candStart2 = EditableInputConnection.getComposingSpanStart(sp);
                int candEnd2 = EditableInputConnection.getComposingSpanEnd(sp);
                candStart = candStart2;
                candEnd = candEnd2;
            }
            imm.updateSelection(this.mTextView, selectionStart, selectionEnd, candStart, candEnd);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0067  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onDraw(Canvas canvas, Layout layout, List<Path> highlightPaths, List<Paint> highlightPaints, Path selectionHighlight, Paint selectionHighlightPaint, int cursorOffsetVertical) {
        Path selectionHighlight2;
        Path selectionHighlight3;
        InsertModeController insertModeController;
        InputMethodManager imm;
        int selectionStart = this.mTextView.getSelectionStart();
        int selectionEnd = this.mTextView.getSelectionEnd();
        InputMethodState ims = this.mInputMethodState;
        if (ims != null && ims.mBatchEditNesting == 0 && (imm = getInputMethodManager()) != null && imm.isActive(this.mTextView) && (ims.mContentChanged || ims.mSelectionModeChanged)) {
            reportExtractedText();
        }
        CorrectionHighlighter correctionHighlighter = this.mCorrectionHighlighter;
        if (correctionHighlighter != null) {
            correctionHighlighter.draw(canvas, cursorOffsetVertical);
        }
        if (selectionHighlight != null && selectionStart == selectionEnd && this.mDrawableForCursor != null && !this.mTextView.hasGesturePreviewHighlight()) {
            drawCursor(canvas, cursorOffsetVertical);
            selectionHighlight2 = null;
        } else {
            selectionHighlight2 = selectionHighlight;
        }
        SelectionActionModeHelper selectionActionModeHelper = this.mSelectionActionModeHelper;
        if (selectionActionModeHelper != null) {
            selectionActionModeHelper.onDraw(canvas);
            if (this.mSelectionActionModeHelper.isDrawingHighlight()) {
                selectionHighlight3 = null;
                insertModeController = this.mInsertModeController;
                if (insertModeController != null) {
                    insertModeController.onDraw(canvas);
                }
                if (!this.mTextView.canHaveDisplayList() && canvas.isHardwareAccelerated()) {
                    drawHardwareAccelerated(canvas, layout, highlightPaths, highlightPaints, selectionHighlight3, selectionHighlightPaint, cursorOffsetVertical);
                    return;
                } else {
                    layout.draw(canvas, highlightPaths, highlightPaints, selectionHighlight3, selectionHighlightPaint, cursorOffsetVertical);
                }
            }
        }
        selectionHighlight3 = selectionHighlight2;
        insertModeController = this.mInsertModeController;
        if (insertModeController != null) {
        }
        if (!this.mTextView.canHaveDisplayList()) {
        }
        layout.draw(canvas, highlightPaths, highlightPaints, selectionHighlight3, selectionHighlightPaint, cursorOffsetVertical);
    }

    private void drawHardwareAccelerated(Canvas canvas, Layout layout, List<Path> highlightPaths, List<Paint> highlightPaints, Path selectionHighlight, Paint selectionHighlightPaint, int cursorOffsetVertical) {
        int numberOfBlocks;
        int[] blockEndLines;
        DynamicLayout dynamicLayout;
        int lastLine;
        int firstLine;
        ArraySet<Integer> blockSet;
        int lastIndex;
        int lastIndex2;
        int i;
        int lastIndex3;
        TextRenderNode textRenderNode;
        boolean z;
        long lineRange;
        int i2;
        int indexFirstChangedBlock;
        TextRenderNode textRenderNode2;
        TextRenderNode textRenderNode3;
        Editor editor = this;
        long lineRange2 = layout.getLineRangeForDraw(canvas);
        int firstLine2 = TextUtils.unpackRangeStartFromLong(lineRange2);
        int lastLine2 = TextUtils.unpackRangeEndFromLong(lineRange2);
        if (lastLine2 < 0) {
            return;
        }
        layout.drawWithoutText(canvas, highlightPaths, highlightPaints, selectionHighlight, selectionHighlightPaint, cursorOffsetVertical, firstLine2, lastLine2);
        if (layout instanceof DynamicLayout) {
            if (editor.mTextRenderNodes == null) {
                editor.mTextRenderNodes = (TextRenderNode[]) ArrayUtils.emptyArray(TextRenderNode.class);
            }
            DynamicLayout dynamicLayout2 = (DynamicLayout) layout;
            int[] blockEndLines2 = dynamicLayout2.getBlockEndLines();
            int[] blockIndices = dynamicLayout2.getBlockIndices();
            int numberOfBlocks2 = dynamicLayout2.getNumberOfBlocks();
            int indexFirstChangedBlock2 = dynamicLayout2.getIndexFirstChangedBlock();
            ArraySet<Integer> blockSet2 = dynamicLayout2.getBlocksAlwaysNeedToBeRedrawn();
            int i3 = -1;
            boolean z2 = true;
            if (blockSet2 != null) {
                int i4 = 0;
                while (i4 < blockSet2.size()) {
                    int blockIndex = dynamicLayout2.getBlockIndex(blockSet2.valueAt(i4).intValue());
                    if (blockIndex != i3 && (textRenderNode3 = editor.mTextRenderNodes[blockIndex]) != null) {
                        textRenderNode3.needsToBeShifted = true;
                    }
                    i4++;
                    i3 = -1;
                }
            }
            int startBlock = Arrays.binarySearch(blockEndLines2, 0, numberOfBlocks2, firstLine2);
            if (startBlock < 0) {
                startBlock = -(startBlock + 1);
            }
            int startIndexToFindAvailableRenderNode = 0;
            int i5 = Math.min(indexFirstChangedBlock2, startBlock);
            while (true) {
                if (i5 >= numberOfBlocks2) {
                    numberOfBlocks = numberOfBlocks2;
                    blockEndLines = blockEndLines2;
                    dynamicLayout = dynamicLayout2;
                    lastLine = lastLine2;
                    firstLine = firstLine2;
                    blockSet = blockSet2;
                    lastIndex = numberOfBlocks2;
                    break;
                }
                int blockIndex2 = blockIndices[i5];
                if (i5 >= indexFirstChangedBlock2 && blockIndex2 != -1 && (textRenderNode2 = editor.mTextRenderNodes[blockIndex2]) != null) {
                    textRenderNode2.needsToBeShifted = z2;
                }
                if (blockEndLines2[i5] < firstLine2) {
                    z = z2;
                    i2 = i5;
                    numberOfBlocks = numberOfBlocks2;
                    blockEndLines = blockEndLines2;
                    dynamicLayout = dynamicLayout2;
                    lastLine = lastLine2;
                    firstLine = firstLine2;
                    lineRange = lineRange2;
                    blockSet = blockSet2;
                    indexFirstChangedBlock = indexFirstChangedBlock2;
                } else {
                    z = z2;
                    lineRange = lineRange2;
                    i2 = i5;
                    blockSet = blockSet2;
                    indexFirstChangedBlock = indexFirstChangedBlock2;
                    numberOfBlocks = numberOfBlocks2;
                    blockEndLines = blockEndLines2;
                    dynamicLayout = dynamicLayout2;
                    lastLine = lastLine2;
                    firstLine = firstLine2;
                    startIndexToFindAvailableRenderNode = drawHardwareAcceleratedInner(canvas, layout, selectionHighlight, selectionHighlightPaint, cursorOffsetVertical, blockEndLines2, blockIndices, i2, numberOfBlocks, startIndexToFindAvailableRenderNode);
                    if (blockEndLines[i2] >= lastLine) {
                        int lastIndex4 = Math.max(indexFirstChangedBlock, i2 + 1);
                        lastIndex = lastIndex4;
                        break;
                    }
                }
                i5 = i2 + 1;
                dynamicLayout2 = dynamicLayout;
                lastLine2 = lastLine;
                indexFirstChangedBlock2 = indexFirstChangedBlock;
                blockSet2 = blockSet;
                z2 = z;
                lineRange2 = lineRange;
                numberOfBlocks2 = numberOfBlocks;
                blockEndLines2 = blockEndLines;
                firstLine2 = firstLine;
            }
            if (blockSet == null) {
                lastIndex2 = lastIndex;
            } else {
                int i6 = 0;
                while (i6 < blockSet.size()) {
                    int block = blockSet.valueAt(i6).intValue();
                    int blockIndex3 = dynamicLayout.getBlockIndex(block);
                    if (blockIndex3 == -1 || (textRenderNode = editor.mTextRenderNodes[blockIndex3]) == null || textRenderNode.needsToBeShifted) {
                        i = i6;
                        int i7 = numberOfBlocks;
                        lastIndex3 = lastIndex;
                        int lastIndex5 = startIndexToFindAvailableRenderNode;
                        startIndexToFindAvailableRenderNode = drawHardwareAcceleratedInner(canvas, layout, selectionHighlight, selectionHighlightPaint, cursorOffsetVertical, blockEndLines, blockIndices, block, i7, lastIndex5);
                    } else {
                        i = i6;
                        lastIndex3 = lastIndex;
                    }
                    i6 = i + 1;
                    lastIndex = lastIndex3;
                    editor = this;
                }
                lastIndex2 = lastIndex;
            }
            dynamicLayout.setIndexFirstChangedBlock(lastIndex2);
            return;
        }
        layout.drawText(canvas, firstLine2, lastLine2);
    }

    private int drawHardwareAcceleratedInner(Canvas canvas, Layout layout, Path highlight, Paint highlightPaint, int cursorOffsetVertical, int[] blockEndLines, int[] blockIndices, int blockInfoIndex, int numberOfBlocks, int startIndexToFindAvailableRenderNode) {
        int startIndexToFindAvailableRenderNode2;
        int blockIndex;
        int line;
        boolean z;
        int blockEndLine = blockEndLines[blockInfoIndex];
        int blockIndex2 = blockIndices[blockInfoIndex];
        boolean blockIsInvalid = blockIndex2 == -1;
        if (blockIsInvalid) {
            int blockIndex3 = getAvailableDisplayListIndex(blockIndices, numberOfBlocks, startIndexToFindAvailableRenderNode);
            blockIndices[blockInfoIndex] = blockIndex3;
            TextRenderNode textRenderNode = this.mTextRenderNodes[blockIndex3];
            if (textRenderNode != null) {
                textRenderNode.isDirty = true;
            }
            startIndexToFindAvailableRenderNode2 = blockIndex3 + 1;
            blockIndex = blockIndex3;
        } else {
            startIndexToFindAvailableRenderNode2 = startIndexToFindAvailableRenderNode;
            blockIndex = blockIndex2;
        }
        TextRenderNode[] textRenderNodeArr = this.mTextRenderNodes;
        if (textRenderNodeArr[blockIndex] == null) {
            textRenderNodeArr[blockIndex] = new TextRenderNode("Text " + blockIndex);
        }
        boolean blockDisplayListIsInvalid = this.mTextRenderNodes[blockIndex].needsRecord();
        RenderNode blockDisplayList = this.mTextRenderNodes[blockIndex].renderNode;
        if (this.mTextRenderNodes[blockIndex].needsToBeShifted || blockDisplayListIsInvalid) {
            int blockBeginLine = blockInfoIndex == 0 ? 0 : blockEndLines[blockInfoIndex - 1] + 1;
            int top = layout.getLineTop(blockBeginLine);
            int bottom = layout.getLineBottom(blockEndLine);
            int right = this.mTextView.getWidth();
            if (!this.mTextView.getHorizontallyScrolling()) {
                line = 0;
            } else {
                float min = Float.MAX_VALUE;
                float max = Float.MIN_VALUE;
                int line2 = blockBeginLine;
                while (line2 <= blockEndLine) {
                    min = Math.min(min, layout.getLineLeft(line2));
                    max = Math.max(max, layout.getLineRight(line2));
                    line2++;
                    blockIsInvalid = blockIsInvalid;
                }
                line = (int) min;
                right = (int) (0.5f + max);
            }
            if (!blockDisplayListIsInvalid) {
                z = false;
            } else {
                RecordingCanvas recordingCanvas = blockDisplayList.beginRecording(right - line, bottom - top);
                try {
                    recordingCanvas.translate(-line, -top);
                    layout.drawText(recordingCanvas, blockBeginLine, blockEndLine);
                    this.mTextRenderNodes[blockIndex].isDirty = false;
                    blockDisplayList.endRecording();
                    blockDisplayList.setClipToBounds(false);
                    z = false;
                } catch (Throwable th) {
                    blockDisplayList.endRecording();
                    blockDisplayList.setClipToBounds(false);
                    throw th;
                }
            }
            blockDisplayList.setLeftTopRightBottom(line, top, right, bottom);
            this.mTextRenderNodes[blockIndex].needsToBeShifted = z;
        }
        ((RecordingCanvas) canvas).drawRenderNode(blockDisplayList);
        return startIndexToFindAvailableRenderNode2;
    }

    private int getAvailableDisplayListIndex(int[] blockIndices, int numberOfBlocks, int searchStartIndex) {
        int length = this.mTextRenderNodes.length;
        for (int i = searchStartIndex; i < length; i++) {
            boolean blockIndexFound = false;
            int j = 0;
            while (true) {
                if (j < numberOfBlocks) {
                    if (blockIndices[j] != i) {
                        j++;
                    } else {
                        blockIndexFound = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!blockIndexFound) {
                return i;
            }
        }
        this.mTextRenderNodes = (TextRenderNode[]) GrowingArrayUtils.append(this.mTextRenderNodes, length, (Object) null);
        return length;
    }

    private void drawCursor(Canvas canvas, int cursorOffsetVertical) {
        boolean translate = cursorOffsetVertical != 0;
        if (translate) {
            canvas.translate(0.0f, cursorOffsetVertical);
        }
        Drawable drawable = this.mDrawableForCursor;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        if (translate) {
            canvas.translate(0.0f, -cursorOffsetVertical);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateHandlesAndActionMode() {
        SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
        if (selectionModifierCursorController != null) {
            selectionModifierCursorController.invalidateHandles();
        }
        InsertionPointCursorController insertionPointCursorController = this.mInsertionPointCursorController;
        if (insertionPointCursorController != null) {
            insertionPointCursorController.invalidateHandle();
        }
        if (this.mTextActionMode != null) {
            invalidateActionMode();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateTextDisplayList(Layout layout, int start, int end) {
        if (this.mTextRenderNodes != null && (layout instanceof DynamicLayout)) {
            int startTransformed = this.mTextView.originalToTransformed(start, 0);
            int endTransformed = this.mTextView.originalToTransformed(end, 0);
            int firstLine = layout.getLineForOffset(startTransformed);
            int lastLine = layout.getLineForOffset(endTransformed);
            DynamicLayout dynamicLayout = (DynamicLayout) layout;
            int[] blockEndLines = dynamicLayout.getBlockEndLines();
            int[] blockIndices = dynamicLayout.getBlockIndices();
            int numberOfBlocks = dynamicLayout.getNumberOfBlocks();
            int i = 0;
            while (i < numberOfBlocks && blockEndLines[i] < firstLine) {
                i++;
            }
            while (i < numberOfBlocks) {
                int blockIndex = blockIndices[i];
                if (blockIndex != -1) {
                    this.mTextRenderNodes[blockIndex].isDirty = true;
                }
                if (blockEndLines[i] >= lastLine) {
                    return;
                }
                i++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateTextDisplayList() {
        if (this.mTextRenderNodes != null) {
            int i = 0;
            while (true) {
                TextRenderNode[] textRenderNodeArr = this.mTextRenderNodes;
                if (i < textRenderNodeArr.length) {
                    TextRenderNode textRenderNode = textRenderNodeArr[i];
                    if (textRenderNode != null) {
                        textRenderNode.isDirty = true;
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateCursorPosition() {
        loadCursorDrawable();
        if (this.mDrawableForCursor == null) {
            return;
        }
        Layout layout = this.mTextView.getLayout();
        int offset = this.mTextView.getSelectionStart();
        int transformedOffset = this.mTextView.originalToTransformed(offset, 1);
        int line = layout.getLineForOffset(transformedOffset);
        int top = layout.getLineTop(line);
        int bottom = layout.getLineBottom(line, false);
        boolean clamped = layout.shouldClampCursor(line);
        updateCursorPosition(top, bottom, layout.getPrimaryHorizontal(transformedOffset, clamped));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refreshTextActionMode() {
        if (extractedTextModeWillBeStarted()) {
            this.mRestartActionModeOnNextRefresh = false;
            return;
        }
        boolean hasSelection = this.mTextView.hasSelection();
        SelectionModifierCursorController selectionController = getSelectionController();
        InsertionPointCursorController insertionController = getInsertionController();
        if ((selectionController != null && selectionController.isCursorBeingModified()) || (insertionController != null && insertionController.isCursorBeingModified())) {
            this.mRestartActionModeOnNextRefresh = false;
            return;
        }
        if (hasSelection) {
            hideInsertionPointCursorController();
            if (this.mTextActionMode == null) {
                if (this.mRestartActionModeOnNextRefresh) {
                    startSelectionActionModeAsync(false);
                }
            } else if (selectionController == null || !selectionController.isActive()) {
                stopTextActionModeWithPreservingSelection();
                startSelectionActionModeAsync(false);
            } else {
                this.mTextActionMode.invalidateContentRect();
            }
        } else if (insertionController == null || !insertionController.isActive()) {
            lambda$startActionModeInternal$0();
        } else {
            ActionMode actionMode = this.mTextActionMode;
            if (actionMode != null) {
                actionMode.invalidateContentRect();
            }
        }
        this.mRestartActionModeOnNextRefresh = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startInsertionActionMode() {
        Runnable runnable = this.mInsertionActionModeRunnable;
        if (runnable != null) {
            this.mTextView.removeCallbacks(runnable);
        }
        if (extractedTextModeWillBeStarted()) {
            return;
        }
        lambda$startActionModeInternal$0();
        ActionMode.Callback actionModeCallback = new TextActionModeCallback(1);
        this.mTextActionMode = this.mTextView.startActionMode(actionModeCallback, 1);
        registerOnBackInvokedCallback();
        if (this.mTextActionMode != null && getInsertionController() != null) {
            getInsertionController().show();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextView getTextView() {
        return this.mTextView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActionMode getTextActionMode() {
        return this.mTextActionMode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRestartActionModeOnNextRefresh(boolean value) {
        this.mRestartActionModeOnNextRefresh = value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startSelectionActionModeAsync(boolean adjustSelection) {
        getSelectionActionModeHelper().startSelectionActionModeAsync(adjustSelection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startLinkActionModeAsync(int start, int end) {
        if (!(this.mTextView.getText() instanceof Spannable)) {
            return;
        }
        lambda$startActionModeInternal$0();
        this.mRequestingLinkActionMode = true;
        getSelectionActionModeHelper().startLinkActionModeAsync(start, end);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateActionModeAsync() {
        getSelectionActionModeHelper().invalidateActionModeAsync();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateActionMode() {
        ActionMode actionMode = this.mTextActionMode;
        if (actionMode != null) {
            actionMode.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SelectionActionModeHelper getSelectionActionModeHelper() {
        if (this.mSelectionActionModeHelper == null) {
            this.mSelectionActionModeHelper = new SelectionActionModeHelper(this);
        }
        return this.mSelectionActionModeHelper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean selectCurrentWordAndStartDrag() {
        Runnable runnable = this.mInsertionActionModeRunnable;
        if (runnable != null) {
            this.mTextView.removeCallbacks(runnable);
        }
        if (!extractedTextModeWillBeStarted() && checkField()) {
            if (this.mTextView.hasSelection() || selectCurrentWord()) {
                stopTextActionModeWithPreservingSelection();
                getSelectionController().enterDrag(2);
                return true;
            }
            return false;
        }
        return false;
    }

    boolean checkField() {
        if (!this.mTextView.canSelectText() || !this.mTextView.requestFocus()) {
            Log.m104w("TextView", "TextView does not support text selection. Selection cancelled.");
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean startActionModeInternal(int actionMode) {
        InputMethodManager imm;
        if (extractedTextModeWillBeStarted()) {
            return false;
        }
        if (this.mTextActionMode != null) {
            invalidateActionMode();
            return false;
        } else if ((actionMode == 2 || (checkField() && this.mTextView.hasSelection())) && this.mTextView.showUIForTouchScreen()) {
            ActionMode.Callback actionModeCallback = new TextActionModeCallback(actionMode);
            this.mTextActionMode = this.mTextView.startActionMode(actionModeCallback, 1);
            registerOnBackInvokedCallback();
            boolean selectableText = this.mTextView.isTextEditable() || this.mTextView.isTextSelectable();
            if (actionMode == 2 && !selectableText) {
                ActionMode actionMode2 = this.mTextActionMode;
                if (actionMode2 instanceof FloatingActionMode) {
                    ((FloatingActionMode) actionMode2).setOutsideTouchable(true, new PopupWindow.OnDismissListener() { // from class: android.widget.Editor$$ExternalSyntheticLambda0
                        @Override // android.widget.PopupWindow.OnDismissListener
                        public final void onDismiss() {
                            Editor.this.lambda$startActionModeInternal$0();
                        }
                    });
                }
            }
            boolean selectionStarted = this.mTextActionMode != null;
            if (selectionStarted && this.mTextView.isTextEditable() && !this.mTextView.isTextSelectable() && this.mShowSoftInputOnFocus && (imm = getInputMethodManager()) != null) {
                imm.showSoftInput(this.mTextView, 0, null);
            }
            return selectionStarted;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean extractedTextModeWillBeStarted() {
        InputMethodManager imm;
        return (this.mTextView.isInExtractedMode() || (imm = getInputMethodManager()) == null || !imm.isFullscreenMode()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldOfferToShowSuggestions() {
        CharSequence text = this.mTextView.getText();
        if (text instanceof Spannable) {
            Spannable spannable = (Spannable) text;
            int selectionStart = this.mTextView.getSelectionStart();
            int selectionEnd = this.mTextView.getSelectionEnd();
            SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) spannable.getSpans(selectionStart, selectionEnd, SuggestionSpan.class);
            if (suggestionSpans.length == 0) {
                return false;
            }
            if (selectionStart == selectionEnd) {
                for (SuggestionSpan suggestionSpan : suggestionSpans) {
                    if (suggestionSpan.getSuggestions().length > 0) {
                        return true;
                    }
                }
                return false;
            }
            int minSpanStart = this.mTextView.getText().length();
            int maxSpanEnd = 0;
            int unionOfSpansCoveringSelectionStartStart = this.mTextView.getText().length();
            int unionOfSpansCoveringSelectionStartEnd = 0;
            boolean hasValidSuggestions = false;
            for (int i = 0; i < suggestionSpans.length; i++) {
                int spanStart = spannable.getSpanStart(suggestionSpans[i]);
                int spanEnd = spannable.getSpanEnd(suggestionSpans[i]);
                minSpanStart = Math.min(minSpanStart, spanStart);
                maxSpanEnd = Math.max(maxSpanEnd, spanEnd);
                if (selectionStart >= spanStart && selectionStart <= spanEnd) {
                    boolean hasValidSuggestions2 = hasValidSuggestions || suggestionSpans[i].getSuggestions().length > 0;
                    unionOfSpansCoveringSelectionStartStart = Math.min(unionOfSpansCoveringSelectionStartStart, spanStart);
                    unionOfSpansCoveringSelectionStartEnd = Math.max(unionOfSpansCoveringSelectionStartEnd, spanEnd);
                    hasValidSuggestions = hasValidSuggestions2;
                }
            }
            return hasValidSuggestions && unionOfSpansCoveringSelectionStartStart < unionOfSpansCoveringSelectionStartEnd && minSpanStart >= unionOfSpansCoveringSelectionStartStart && maxSpanEnd <= unionOfSpansCoveringSelectionStartEnd;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCursorInsideEasyCorrectionSpan() {
        Spannable spannable = (Spannable) this.mTextView.getText();
        SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) spannable.getSpans(this.mTextView.getSelectionStart(), this.mTextView.getSelectionEnd(), SuggestionSpan.class);
        for (SuggestionSpan suggestionSpan : suggestionSpans) {
            if ((suggestionSpan.getFlags() & 1) != 0) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTouchUpEvent(MotionEvent event) {
        if (getSelectionActionModeHelper().resetSelection(getTextView().getOffsetForPosition(event.getX(), event.getY()))) {
            return;
        }
        boolean selectAllGotFocus = this.mSelectAllOnFocus && this.mTextView.didTouchFocusSelect();
        hideCursorAndSpanControllers();
        lambda$startActionModeInternal$0();
        CharSequence text = this.mTextView.getText();
        if (!selectAllGotFocus && text.length() > 0) {
            int offset = this.mTextView.getOffsetForPosition(event.getX(), event.getY());
            boolean shouldInsertCursor = true ^ this.mRequestingLinkActionMode;
            if (shouldInsertCursor) {
                Selection.setSelection((Spannable) text, offset);
                SpellChecker spellChecker = this.mSpellChecker;
                if (spellChecker != null) {
                    spellChecker.onSelectionChanged();
                }
            }
            if (!extractedTextModeWillBeStarted()) {
                if (isCursorInsideEasyCorrectionSpan()) {
                    Runnable runnable = this.mInsertionActionModeRunnable;
                    if (runnable != null) {
                        this.mTextView.removeCallbacks(runnable);
                    }
                    Runnable runnable2 = new Runnable() { // from class: android.widget.Editor$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            Editor.this.replace();
                        }
                    };
                    this.mShowSuggestionRunnable = runnable2;
                    this.mTextView.postDelayed(runnable2, ViewConfiguration.getDoubleTapTimeout());
                } else if (hasInsertionController()) {
                    if (shouldInsertCursor && this.mTextView.showUIForTouchScreen()) {
                        getInsertionController().show();
                    } else {
                        getInsertionController().hide();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void onTextOperationUserChanged() {
        SpellChecker spellChecker = this.mSpellChecker;
        if (spellChecker != null) {
            spellChecker.resetSession();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: stopTextActionMode */
    public void lambda$startActionModeInternal$0() {
        ActionMode actionMode = this.mTextActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
        unregisterOnBackInvokedCallback();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopTextActionModeWithPreservingSelection() {
        if (this.mTextActionMode != null) {
            this.mRestartActionModeOnNextRefresh = true;
        }
        this.mPreserveSelection = true;
        lambda$startActionModeInternal$0();
        this.mPreserveSelection = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasInsertionController() {
        return this.mInsertionControllerEnabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasSelectionController() {
        return this.mSelectionControllerEnabled;
    }

    public InsertionPointCursorController getInsertionController() {
        if (!this.mInsertionControllerEnabled) {
            return null;
        }
        if (this.mInsertionPointCursorController == null) {
            this.mInsertionPointCursorController = new InsertionPointCursorController();
            ViewTreeObserver observer = this.mTextView.getViewTreeObserver();
            observer.addOnTouchModeChangeListener(this.mInsertionPointCursorController);
        }
        return this.mInsertionPointCursorController;
    }

    public SelectionModifierCursorController getSelectionController() {
        if (!this.mSelectionControllerEnabled) {
            return null;
        }
        if (this.mSelectionModifierCursorController == null) {
            this.mSelectionModifierCursorController = new SelectionModifierCursorController();
            ViewTreeObserver observer = this.mTextView.getViewTreeObserver();
            observer.addOnTouchModeChangeListener(this.mSelectionModifierCursorController);
        }
        return this.mSelectionModifierCursorController;
    }

    public Drawable getCursorDrawable() {
        return this.mDrawableForCursor;
    }

    private void updateCursorPosition(int top, int bottom, float horizontal) {
        loadCursorDrawable();
        int left = clampHorizontalPosition(this.mDrawableForCursor, horizontal);
        int width = this.mDrawableForCursor.getIntrinsicWidth();
        this.mDrawableForCursor.setBounds(left, top - this.mTempRect.top, left + width, this.mTempRect.bottom + bottom);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int clampHorizontalPosition(Drawable drawable, float horizontal) {
        float horizontal2 = Math.max(0.5f, horizontal - 0.5f);
        if (this.mTempRect == null) {
            this.mTempRect = new Rect();
        }
        int drawableWidth = 0;
        if (drawable != null) {
            drawable.getPadding(this.mTempRect);
            drawableWidth = drawable.getIntrinsicWidth();
        } else {
            this.mTempRect.setEmpty();
        }
        int scrollX = this.mTextView.getScrollX();
        float horizontalDiff = horizontal2 - scrollX;
        int viewClippedWidth = (this.mTextView.getWidth() - this.mTextView.getCompoundPaddingLeft()) - this.mTextView.getCompoundPaddingRight();
        if (horizontalDiff >= viewClippedWidth - 1.0f) {
            int left = (viewClippedWidth + scrollX) - (drawableWidth - this.mTempRect.right);
            return left;
        } else if (Math.abs(horizontalDiff) <= 1.0f || (TextUtils.isEmpty(this.mTextView.getText()) && 1048576 - scrollX <= viewClippedWidth + 1.0f && horizontal2 <= 1.0f)) {
            int left2 = scrollX - this.mTempRect.left;
            return left2;
        } else {
            int left3 = ((int) horizontal2) - this.mTempRect.left;
            return left3;
        }
    }

    public void onCommitCorrection(CorrectionInfo info) {
        CorrectionHighlighter correctionHighlighter = this.mCorrectionHighlighter;
        if (correctionHighlighter == null) {
            this.mCorrectionHighlighter = new CorrectionHighlighter();
        } else {
            correctionHighlighter.invalidate(false);
        }
        this.mCorrectionHighlighter.highlight(info);
        this.mUndoInputFilter.freezeLastEdit();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onScrollChanged() {
        PositionListener positionListener = this.mPositionListener;
        if (positionListener != null) {
            positionListener.onScrollChanged();
        }
        ActionMode actionMode = this.mTextActionMode;
        if (actionMode != null) {
            actionMode.invalidateContentRect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldBlink() {
        int start;
        int end;
        if (isCursorVisible() && this.mTextView.isFocused()) {
            return this.mTextView.getWindowVisibility() == 0 && (start = this.mTextView.getSelectionStart()) >= 0 && (end = this.mTextView.getSelectionEnd()) >= 0 && start == end;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void makeBlink() {
        if (shouldBlink()) {
            this.mShowCursor = SystemClock.uptimeMillis();
            if (this.mBlink == null) {
                this.mBlink = new Blink();
            }
            this.mTextView.removeCallbacks(this.mBlink);
            this.mTextView.postDelayed(this.mBlink, 500L);
            return;
        }
        Blink blink = this.mBlink;
        if (blink != null) {
            this.mTextView.removeCallbacks(blink);
        }
    }

    public boolean isBlinking() {
        Blink blink = this.mBlink;
        if (blink == null) {
            return false;
        }
        return !blink.mCancelled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class Blink implements Runnable {
        private boolean mCancelled;

        private Blink() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mCancelled) {
                return;
            }
            Editor.this.mTextView.removeCallbacks(this);
            if (Editor.this.shouldBlink()) {
                if (Editor.this.mTextView.getLayout() != null) {
                    Editor.this.mTextView.invalidateCursorPath();
                }
                Editor.this.mTextView.postDelayed(this, 500L);
            }
        }

        void cancel() {
            if (!this.mCancelled) {
                Editor.this.mTextView.removeCallbacks(this);
                this.mCancelled = true;
            }
        }

        void uncancel() {
            this.mCancelled = false;
        }
    }

    private View.DragShadowBuilder getTextThumbnailBuilder(int start, int end) {
        TextView shadowView = (TextView) View.inflate(this.mTextView.getContext(), C4057R.layout.text_drag_thumbnail, null);
        if (shadowView == null) {
            throw new IllegalArgumentException("Unable to inflate text drag thumbnail");
        }
        if (end - start > 20) {
            long range = getCharClusterRange(start + 20);
            end = TextUtils.unpackRangeEndFromLong(range);
        }
        CharSequence text = this.mTextView.getTransformedText(start, end);
        shadowView.setText(text);
        shadowView.setTextColor(this.mTextView.getTextColors());
        shadowView.setTextAppearance(16);
        shadowView.setGravity(17);
        shadowView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        int size = View.MeasureSpec.makeMeasureSpec(0, 0);
        shadowView.measure(size, size);
        shadowView.layout(0, 0, shadowView.getMeasuredWidth(), shadowView.getMeasuredHeight());
        shadowView.invalidate();
        return new View.DragShadowBuilder(shadowView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class DragLocalState {
        public int end;
        public TextView sourceTextView;
        public int start;

        public DragLocalState(TextView sourceTextView, int start, int end) {
            this.sourceTextView = sourceTextView;
            this.start = start;
            this.end = end;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDrop(DragEvent event) {
        int offset = this.mTextView.getOffsetForPosition(event.getX(), event.getY());
        Object localState = event.getLocalState();
        DragLocalState dragLocalState = null;
        if (localState instanceof DragLocalState) {
            dragLocalState = (DragLocalState) localState;
        }
        boolean dragDropIntoItself = dragLocalState != null && dragLocalState.sourceTextView == this.mTextView;
        if (dragDropIntoItself && offset >= dragLocalState.start && offset < dragLocalState.end) {
            return;
        }
        DragAndDropPermissions permissions = DragAndDropPermissions.obtain(event);
        if (permissions != null) {
            permissions.takeTransient();
        }
        this.mTextView.beginBatchEdit();
        this.mUndoInputFilter.freezeLastEdit();
        try {
            int originalLength = this.mTextView.getText().length();
            Selection.setSelection((Spannable) this.mTextView.getText(), offset);
            ClipData clip = event.getClipData();
            ContentInfo payload = new ContentInfo.Builder(clip, 3).setDragAndDropPermissions(permissions).build();
            this.mTextView.performReceiveContent(payload);
            if (dragDropIntoItself) {
                deleteSourceAfterLocalDrop(dragLocalState, offset, originalLength);
            }
        } finally {
            this.mTextView.endBatchEdit();
            this.mUndoInputFilter.freezeLastEdit();
        }
    }

    private void deleteSourceAfterLocalDrop(DragLocalState dragLocalState, int dropOffset, int lengthBeforeDrop) {
        int dragSourceStart = dragLocalState.start;
        int dragSourceEnd = dragLocalState.end;
        if (dropOffset <= dragSourceStart) {
            int shift = this.mTextView.getText().length() - lengthBeforeDrop;
            dragSourceStart += shift;
            dragSourceEnd += shift;
        }
        this.mTextView.deleteText_internal(dragSourceStart, dragSourceEnd);
        int prevCharIdx = Math.max(0, dragSourceStart - 1);
        int nextCharIdx = Math.min(this.mTextView.getText().length(), dragSourceStart + 1);
        if (nextCharIdx > prevCharIdx + 1) {
            CharSequence t = this.mTextView.getTransformedText(prevCharIdx, nextCharIdx);
            if (Character.isSpaceChar(t.charAt(0)) && Character.isSpaceChar(t.charAt(1))) {
                this.mTextView.deleteText_internal(prevCharIdx, prevCharIdx + 1);
            }
        }
    }

    public void addSpanWatchers(Spannable text) {
        int textLength = text.length();
        KeyListener keyListener = this.mKeyListener;
        if (keyListener != null) {
            text.setSpan(keyListener, 0, textLength, 18);
        }
        if (this.mSpanController == null) {
            this.mSpanController = new SpanController();
        }
        text.setSpan(this.mSpanController, 0, textLength, 18);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContextMenuAnchor(float x, float y) {
        this.mContextMenuAnchorX = x;
        this.mContextMenuAnchorY = y;
    }

    private void setAssistContextMenuItems(Menu menu) {
        TextClassification textClassification = getSelectionActionModeHelper().getTextClassification();
        if (textClassification == null) {
            return;
        }
        final AssistantCallbackHelper helper = new AssistantCallbackHelper(getSelectionActionModeHelper());
        helper.updateAssistMenuItems(menu, new MenuItem.OnMenuItemClickListener() { // from class: android.widget.Editor$$ExternalSyntheticLambda3
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public final boolean onMenuItemClick(MenuItem menuItem) {
                boolean lambda$setAssistContextMenuItems$1;
                lambda$setAssistContextMenuItems$1 = Editor.this.lambda$setAssistContextMenuItems$1(helper, menuItem);
                return lambda$setAssistContextMenuItems$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setAssistContextMenuItems$1(AssistantCallbackHelper helper, MenuItem item) {
        getSelectionActionModeHelper().onSelectionAction(item.getItemId(), item.getTitle().toString());
        if (this.mProcessTextIntentActionsHandler.performMenuItemAction(item)) {
            return true;
        }
        if (item.getGroupId() == 16908353 && helper.onAssistMenuItemClicked(item)) {
            return true;
        }
        return this.mTextView.onTextContextMenuItem(item.getItemId());
    }

    public void onCreateContextMenu(ContextMenu menu) {
        int offset;
        if (this.mIsBeingLongClicked || Float.isNaN(this.mContextMenuAnchorX) || Float.isNaN(this.mContextMenuAnchorY) || (offset = this.mTextView.getOffsetForPosition(this.mContextMenuAnchorX, this.mContextMenuAnchorY)) == -1) {
            return;
        }
        stopTextActionModeWithPreservingSelection();
        if (this.mTextView.canSelectText()) {
            boolean isOnSelection = this.mTextView.hasSelection() && offset >= this.mTextView.getSelectionStart() && offset <= this.mTextView.getSelectionEnd();
            if (!isOnSelection) {
                Selection.setSelection((Spannable) this.mTextView.getText(), offset);
                lambda$startActionModeInternal$0();
            }
        }
        boolean isOnSelection2 = shouldOfferToShowSuggestions();
        if (isOnSelection2) {
            SuggestionInfo[] suggestionInfoArray = new SuggestionInfo[5];
            for (int i = 0; i < suggestionInfoArray.length; i++) {
                suggestionInfoArray[i] = new SuggestionInfo();
            }
            SubMenu subMenu = menu.addSubMenu(0, 0, 11, C4057R.string.replace);
            int numItems = this.mSuggestionHelper.getSuggestionInfo(suggestionInfoArray, null);
            for (int i2 = 0; i2 < numItems; i2++) {
                final SuggestionInfo info = suggestionInfoArray[i2];
                subMenu.add(0, 0, i2, info.mText).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: android.widget.Editor.4
                    @Override // android.view.MenuItem.OnMenuItemClickListener
                    public boolean onMenuItemClick(MenuItem item) {
                        Editor.this.replaceWithSuggestion(info);
                        return true;
                    }
                });
            }
        }
        menu.add(1, 16908338, 2, C4057R.string.undo).setAlphabeticShortcut(DateFormat.TIME_ZONE).setOnMenuItemClickListener(this.mOnContextMenuItemClickListener).setEnabled(this.mTextView.canUndo());
        menu.add(1, 16908339, 3, C4057R.string.redo).setAlphabeticShortcut(DateFormat.TIME_ZONE, 4097).setOnMenuItemClickListener(this.mOnContextMenuItemClickListener).setEnabled(this.mTextView.canRedo());
        menu.add(2, 16908320, 4, 17039363).setAlphabeticShortcut(EpicenterTranslateClipReveal.StateProperty.TARGET_X).setOnMenuItemClickListener(this.mOnContextMenuItemClickListener).setEnabled(this.mTextView.canCut());
        menu.add(2, 16908321, 5, 17039361).setAlphabeticShortcut('c').setOnMenuItemClickListener(this.mOnContextMenuItemClickListener).setEnabled(this.mTextView.canCopy());
        menu.add(2, 16908322, 6, 17039371).setAlphabeticShortcut('v').setEnabled(this.mTextView.canPaste()).setOnMenuItemClickListener(this.mOnContextMenuItemClickListener);
        menu.add(2, 16908337, 11, 17039385).setAlphabeticShortcut('v', 4097).setEnabled(this.mTextView.canPasteAsPlainText()).setOnMenuItemClickListener(this.mOnContextMenuItemClickListener);
        menu.add(2, 16908319, 8, 17039373).setAlphabeticShortcut(DateFormat.AM_PM).setEnabled(this.mTextView.canSelectAllText()).setOnMenuItemClickListener(this.mOnContextMenuItemClickListener);
        menu.add(3, 16908341, 7, C4057R.string.share).setEnabled(this.mTextView.canShare()).setOnMenuItemClickListener(this.mOnContextMenuItemClickListener);
        menu.add(3, 16908355, 10, 17039386).setEnabled(this.mTextView.canRequestAutofill()).setOnMenuItemClickListener(this.mOnContextMenuItemClickListener);
        this.mPreserveSelection = true;
        adjustIconSpacing(menu);
    }

    public void adjustIconSpacing(ContextMenu menu) {
        int width = -1;
        int height = -1;
        for (int i = 0; i < menu.size(); i++) {
            Drawable d = menu.getItem(i).getIcon();
            if (d != null) {
                width = Math.max(width, d.getIntrinsicWidth());
                height = Math.max(height, d.getIntrinsicHeight());
            }
        }
        if (width < 0 || height < 0) {
            return;
        }
        GradientDrawable paddingDrawable = new GradientDrawable();
        paddingDrawable.setSize(width, height);
        for (int i2 = 0; i2 < menu.size(); i2++) {
            MenuItem item = menu.getItem(i2);
            if (item.getIcon() == null) {
                item.setIcon(paddingDrawable);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SuggestionSpan findEquivalentSuggestionSpan(SuggestionSpanInfo suggestionSpanInfo) {
        Editable editable = (Editable) this.mTextView.getText();
        if (editable.getSpanStart(suggestionSpanInfo.mSuggestionSpan) >= 0) {
            return suggestionSpanInfo.mSuggestionSpan;
        }
        SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) editable.getSpans(suggestionSpanInfo.mSpanStart, suggestionSpanInfo.mSpanEnd, SuggestionSpan.class);
        for (SuggestionSpan suggestionSpan : suggestionSpans) {
            int start = editable.getSpanStart(suggestionSpan);
            if (start == suggestionSpanInfo.mSpanStart) {
                int end = editable.getSpanEnd(suggestionSpan);
                if (end == suggestionSpanInfo.mSpanEnd && suggestionSpan.equals(suggestionSpanInfo.mSuggestionSpan)) {
                    return suggestionSpan;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replaceWithSuggestion(SuggestionInfo suggestionInfo) {
        int spanStart;
        String originalText;
        SuggestionSpan[] suggestionSpans;
        int length;
        SuggestionSpan targetSuggestionSpan = findEquivalentSuggestionSpan(suggestionInfo.mSuggestionSpanInfo);
        if (targetSuggestionSpan == null) {
            return;
        }
        Editable editable = (Editable) this.mTextView.getText();
        int spanStart2 = editable.getSpanStart(targetSuggestionSpan);
        int spanEnd = editable.getSpanEnd(targetSuggestionSpan);
        if (spanStart2 >= 0 && spanEnd > spanStart2) {
            String originalText2 = TextUtils.substring(editable, spanStart2, spanEnd);
            SuggestionSpan[] suggestionSpans2 = (SuggestionSpan[]) editable.getSpans(spanStart2, spanEnd, SuggestionSpan.class);
            int length2 = suggestionSpans2.length;
            int[] suggestionSpansStarts = new int[length2];
            int[] suggestionSpansEnds = new int[length2];
            int[] suggestionSpansFlags = new int[length2];
            for (int i = 0; i < length2; i++) {
                SuggestionSpan suggestionSpan = suggestionSpans2[i];
                suggestionSpansStarts[i] = editable.getSpanStart(suggestionSpan);
                suggestionSpansEnds[i] = editable.getSpanEnd(suggestionSpan);
                suggestionSpansFlags[i] = editable.getSpanFlags(suggestionSpan);
                int suggestionSpanFlags = suggestionSpan.getFlags();
                if ((suggestionSpanFlags & 10) != 0) {
                    suggestionSpan.setFlags(suggestionSpanFlags & (-3) & (-9) & (-2));
                }
            }
            int i2 = suggestionInfo.mSuggestionStart;
            int suggestionEnd = suggestionInfo.mSuggestionEnd;
            String suggestion = suggestionInfo.mText.subSequence(i2, suggestionEnd).toString();
            this.mTextView.replaceText_internal(spanStart2, spanEnd, suggestion);
            String[] suggestions = targetSuggestionSpan.getSuggestions();
            suggestions[suggestionInfo.mSuggestionIndex] = originalText2;
            int lengthDelta = suggestion.length() - (spanEnd - spanStart2);
            int i3 = 0;
            while (i3 < length2) {
                Editable editable2 = editable;
                if (suggestionSpansStarts[i3] > spanStart2 || suggestionSpansEnds[i3] < spanEnd) {
                    spanStart = spanStart2;
                    originalText = originalText2;
                    suggestionSpans = suggestionSpans2;
                    length = length2;
                } else {
                    spanStart = spanStart2;
                    originalText = originalText2;
                    suggestionSpans = suggestionSpans2;
                    length = length2;
                    this.mTextView.setSpan_internal(suggestionSpans2[i3], suggestionSpansStarts[i3], suggestionSpansEnds[i3] + lengthDelta, suggestionSpansFlags[i3]);
                }
                i3++;
                editable = editable2;
                spanStart2 = spanStart;
                originalText2 = originalText;
                length2 = length;
                suggestionSpans2 = suggestionSpans;
            }
            int i4 = spanEnd + lengthDelta;
            this.mTextView.setCursorPosition_internal(i4, i4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SpanController implements SpanWatcher {
        private static final int DISPLAY_TIMEOUT_MS = 3000;
        private Runnable mHidePopup;
        private EasyEditPopupWindow mPopupWindow;

        private SpanController() {
        }

        private boolean isNonIntermediateSelectionSpan(Spannable text, Object span) {
            return (Selection.SELECTION_START == span || Selection.SELECTION_END == span) && (text.getSpanFlags(span) & 512) == 0;
        }

        @Override // android.text.SpanWatcher
        public void onSpanAdded(Spannable text, Object span, int start, int end) {
            if (isNonIntermediateSelectionSpan(text, span)) {
                Editor.this.sendUpdateSelection();
            } else if (span instanceof EasyEditSpan) {
                if (this.mPopupWindow == null) {
                    this.mPopupWindow = new EasyEditPopupWindow();
                    this.mHidePopup = new Runnable() { // from class: android.widget.Editor.SpanController.1
                        @Override // java.lang.Runnable
                        public void run() {
                            SpanController.this.hide();
                        }
                    };
                }
                if (this.mPopupWindow.mEasyEditSpan != null) {
                    this.mPopupWindow.mEasyEditSpan.setDeleteEnabled(false);
                }
                this.mPopupWindow.setEasyEditSpan((EasyEditSpan) span);
                this.mPopupWindow.setOnDeleteListener(new EasyEditDeleteListener() { // from class: android.widget.Editor.SpanController.2
                    @Override // android.widget.Editor.EasyEditDeleteListener
                    public void onDeleteClick(EasyEditSpan span2) {
                        Editable editable = (Editable) Editor.this.mTextView.getText();
                        int start2 = editable.getSpanStart(span2);
                        int end2 = editable.getSpanEnd(span2);
                        if (start2 >= 0 && end2 >= 0) {
                            SpanController.this.sendEasySpanNotification(1, span2);
                            Editor.this.mTextView.deleteText_internal(start2, end2);
                        }
                        editable.removeSpan(span2);
                    }
                });
                if (Editor.this.mTextView.getWindowVisibility() != 0 || Editor.this.mTextView.getLayout() == null || Editor.this.extractedTextModeWillBeStarted()) {
                    return;
                }
                this.mPopupWindow.show();
                Editor.this.mTextView.removeCallbacks(this.mHidePopup);
                Editor.this.mTextView.postDelayed(this.mHidePopup, TelecomManager.VERY_SHORT_CALL_TIME_MS);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanRemoved(Spannable text, Object span, int start, int end) {
            if (isNonIntermediateSelectionSpan(text, span)) {
                Editor.this.sendUpdateSelection();
                return;
            }
            EasyEditPopupWindow easyEditPopupWindow = this.mPopupWindow;
            if (easyEditPopupWindow != null && span == easyEditPopupWindow.mEasyEditSpan) {
                hide();
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanChanged(Spannable text, Object span, int previousStart, int previousEnd, int newStart, int newEnd) {
            if (isNonIntermediateSelectionSpan(text, span)) {
                Editor.this.sendUpdateSelection();
            } else if (this.mPopupWindow != null && (span instanceof EasyEditSpan)) {
                EasyEditSpan easyEditSpan = (EasyEditSpan) span;
                sendEasySpanNotification(2, easyEditSpan);
                text.removeSpan(easyEditSpan);
            }
        }

        public void hide() {
            EasyEditPopupWindow easyEditPopupWindow = this.mPopupWindow;
            if (easyEditPopupWindow != null) {
                easyEditPopupWindow.hide();
                Editor.this.mTextView.removeCallbacks(this.mHidePopup);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendEasySpanNotification(int textChangedType, EasyEditSpan span) {
            try {
                PendingIntent pendingIntent = span.getPendingIntent();
                if (pendingIntent != null) {
                    Intent intent = new Intent();
                    intent.putExtra(EasyEditSpan.EXTRA_TEXT_CHANGED_TYPE, textChangedType);
                    pendingIntent.send(Editor.this.mTextView.getContext(), 0, intent);
                }
            } catch (PendingIntent.CanceledException e) {
                Log.m103w("Editor", "PendingIntent for notification cannot be sent", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class EasyEditPopupWindow extends PinnedPopupWindow implements View.OnClickListener {
        private static final int POPUP_TEXT_LAYOUT = 17367348;
        private TextView mDeleteTextView;
        private EasyEditSpan mEasyEditSpan;
        private EasyEditDeleteListener mOnDeleteListener;

        private EasyEditPopupWindow() {
            super();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void createPopupWindow() {
            this.mPopupWindow = new PopupWindow(Editor.this.mTextView.getContext(), (AttributeSet) null, 16843464);
            this.mPopupWindow.setInputMethodMode(2);
            this.mPopupWindow.setClippingEnabled(true);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void initContentView() {
            LinearLayout linearLayout = new LinearLayout(Editor.this.mTextView.getContext());
            linearLayout.setOrientation(0);
            this.mContentView = linearLayout;
            this.mContentView.setBackgroundResource(C4057R.C4058drawable.text_edit_side_paste_window);
            LayoutInflater inflater = (LayoutInflater) Editor.this.mTextView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams wrapContent = new ViewGroup.LayoutParams(-2, -2);
            TextView textView = (TextView) inflater.inflate(17367348, (ViewGroup) null);
            this.mDeleteTextView = textView;
            textView.setLayoutParams(wrapContent);
            this.mDeleteTextView.setText(C4057R.string.delete);
            this.mDeleteTextView.setOnClickListener(this);
            this.mContentView.addView(this.mDeleteTextView);
        }

        public void setEasyEditSpan(EasyEditSpan easyEditSpan) {
            this.mEasyEditSpan = easyEditSpan;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setOnDeleteListener(EasyEditDeleteListener listener) {
            this.mOnDeleteListener = listener;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            EasyEditSpan easyEditSpan;
            EasyEditDeleteListener easyEditDeleteListener;
            if (view == this.mDeleteTextView && (easyEditSpan = this.mEasyEditSpan) != null && easyEditSpan.isDeleteEnabled() && (easyEditDeleteListener = this.mOnDeleteListener) != null) {
                easyEditDeleteListener.onDeleteClick(this.mEasyEditSpan);
            }
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        public void hide() {
            EasyEditSpan easyEditSpan = this.mEasyEditSpan;
            if (easyEditSpan != null) {
                easyEditSpan.setDeleteEnabled(false);
            }
            this.mOnDeleteListener = null;
            super.hide();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getTextOffset() {
            Editable editable = (Editable) Editor.this.mTextView.getText();
            return editable.getSpanEnd(this.mEasyEditSpan);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getVerticalLocalPosition(int line) {
            Layout layout = Editor.this.mTextView.getLayout();
            return layout.getLineBottom(line, false);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int clipVertically(int positionY) {
            return positionY;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class PositionListener implements ViewTreeObserver.OnPreDrawListener {
        private static final int MAXIMUM_NUMBER_OF_LISTENERS = 7;
        private boolean[] mCanMove;
        private int mNumberOfListeners;
        private boolean mPositionHasChanged;
        private TextViewPositionListener[] mPositionListeners;
        private int mPositionX;
        private int mPositionXOnScreen;
        private int mPositionY;
        private int mPositionYOnScreen;
        private boolean mScrollHasChanged;
        final int[] mTempCoords;

        private PositionListener() {
            this.mPositionListeners = new TextViewPositionListener[7];
            this.mCanMove = new boolean[7];
            this.mPositionHasChanged = true;
            this.mTempCoords = new int[2];
        }

        public void addSubscriber(TextViewPositionListener positionListener, boolean canMove) {
            if (this.mNumberOfListeners == 0) {
                updatePosition();
                ViewTreeObserver vto = Editor.this.mTextView.getViewTreeObserver();
                vto.addOnPreDrawListener(this);
            }
            int emptySlotIndex = -1;
            for (int i = 0; i < 7; i++) {
                TextViewPositionListener listener = this.mPositionListeners[i];
                if (listener == positionListener) {
                    return;
                }
                if (emptySlotIndex < 0 && listener == null) {
                    emptySlotIndex = i;
                }
            }
            this.mPositionListeners[emptySlotIndex] = positionListener;
            this.mCanMove[emptySlotIndex] = canMove;
            this.mNumberOfListeners++;
        }

        public void removeSubscriber(TextViewPositionListener positionListener) {
            int i = 0;
            while (true) {
                if (i >= 7) {
                    break;
                }
                TextViewPositionListener[] textViewPositionListenerArr = this.mPositionListeners;
                if (textViewPositionListenerArr[i] != positionListener) {
                    i++;
                } else {
                    textViewPositionListenerArr[i] = null;
                    this.mNumberOfListeners--;
                    break;
                }
            }
            int i2 = this.mNumberOfListeners;
            if (i2 == 0) {
                ViewTreeObserver vto = Editor.this.mTextView.getViewTreeObserver();
                vto.removeOnPreDrawListener(this);
            }
        }

        public int getPositionX() {
            return this.mPositionX;
        }

        public int getPositionY() {
            return this.mPositionY;
        }

        public int getPositionXOnScreen() {
            return this.mPositionXOnScreen;
        }

        public int getPositionYOnScreen() {
            return this.mPositionYOnScreen;
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            TextViewPositionListener positionListener;
            updatePosition();
            for (int i = 0; i < 7; i++) {
                boolean z = this.mPositionHasChanged;
                if ((z || this.mScrollHasChanged || this.mCanMove[i]) && (positionListener = this.mPositionListeners[i]) != null) {
                    positionListener.updatePosition(this.mPositionX, this.mPositionY, z, this.mScrollHasChanged);
                }
            }
            this.mScrollHasChanged = false;
            return true;
        }

        private void updatePosition() {
            Editor.this.mTextView.getLocationInWindow(this.mTempCoords);
            int[] iArr = this.mTempCoords;
            int i = iArr[0];
            this.mPositionHasChanged = (i == this.mPositionX && iArr[1] == this.mPositionY) ? false : true;
            this.mPositionX = i;
            this.mPositionY = iArr[1];
            Editor.this.mTextView.getLocationOnScreen(this.mTempCoords);
            int[] iArr2 = this.mTempCoords;
            this.mPositionXOnScreen = iArr2[0];
            this.mPositionYOnScreen = iArr2[1];
        }

        public void onScrollChanged() {
            this.mScrollHasChanged = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public abstract class PinnedPopupWindow implements TextViewPositionListener {
        int mClippingLimitLeft;
        int mClippingLimitRight;
        protected ViewGroup mContentView;
        protected PopupWindow mPopupWindow;
        int mPositionX;
        int mPositionY;

        protected abstract int clipVertically(int i);

        protected abstract void createPopupWindow();

        protected abstract int getTextOffset();

        protected abstract int getVerticalLocalPosition(int i);

        protected abstract void initContentView();

        protected void setUp() {
        }

        public PinnedPopupWindow() {
            setUp();
            createPopupWindow();
            this.mPopupWindow.setWindowLayoutType(1005);
            this.mPopupWindow.setWidth(-2);
            this.mPopupWindow.setHeight(-2);
            initContentView();
            ViewGroup.LayoutParams wrapContent = new ViewGroup.LayoutParams(-2, -2);
            this.mContentView.setLayoutParams(wrapContent);
            this.mPopupWindow.setContentView(this.mContentView);
        }

        public void show() {
            Editor.this.getPositionListener().addSubscriber(this, false);
            computeLocalPosition();
            PositionListener positionListener = Editor.this.getPositionListener();
            updatePosition(positionListener.getPositionX(), positionListener.getPositionY());
        }

        protected void measureContent() {
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, Integer.MIN_VALUE));
        }

        private void computeLocalPosition() {
            measureContent();
            int width = this.mContentView.getMeasuredWidth();
            int offset = getTextOffset();
            int transformedOffset = Editor.this.mTextView.originalToTransformed(offset, 1);
            Layout layout = Editor.this.mTextView.getLayout();
            int primaryHorizontal = (int) (layout.getPrimaryHorizontal(transformedOffset) - (width / 2.0f));
            this.mPositionX = primaryHorizontal;
            this.mPositionX = primaryHorizontal + Editor.this.mTextView.viewportToContentHorizontalOffset();
            int line = layout.getLineForOffset(transformedOffset);
            int verticalLocalPosition = getVerticalLocalPosition(line);
            this.mPositionY = verticalLocalPosition;
            this.mPositionY = verticalLocalPosition + Editor.this.mTextView.viewportToContentVerticalOffset();
        }

        private void updatePosition(int parentPositionX, int parentPositionY) {
            int positionX = this.mPositionX + parentPositionX;
            int positionY = clipVertically(this.mPositionY + parentPositionY);
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            int width = this.mContentView.getMeasuredWidth();
            int positionX2 = Math.max(-this.mClippingLimitLeft, Math.min((displayMetrics.widthPixels - width) + this.mClippingLimitRight, positionX));
            if (isShowing()) {
                this.mPopupWindow.update(positionX2, positionY, -1, -1);
            } else {
                this.mPopupWindow.showAtLocation(Editor.this.mTextView, 0, positionX2, positionY);
            }
        }

        public void hide() {
            if (!isShowing()) {
                return;
            }
            this.mPopupWindow.dismiss();
            Editor.this.getPositionListener().removeSubscriber(this);
        }

        @Override // android.widget.Editor.TextViewPositionListener
        public void updatePosition(int parentPositionX, int parentPositionY, boolean parentPositionChanged, boolean parentScrolled) {
            if (isShowing() && Editor.this.isOffsetVisible(getTextOffset())) {
                if (parentScrolled) {
                    computeLocalPosition();
                }
                updatePosition(parentPositionX, parentPositionY);
                return;
            }
            hide();
        }

        public boolean isShowing() {
            return this.mPopupWindow.isShowing();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class SuggestionInfo {
        int mSuggestionEnd;
        int mSuggestionIndex;
        final SuggestionSpanInfo mSuggestionSpanInfo;
        int mSuggestionStart;
        final SpannableStringBuilder mText;

        private SuggestionInfo() {
            this.mSuggestionSpanInfo = new SuggestionSpanInfo();
            this.mText = new SpannableStringBuilder();
        }

        void clear() {
            this.mSuggestionSpanInfo.clear();
            this.mText.clear();
        }

        void setSpanInfo(SuggestionSpan span, int spanStart, int spanEnd) {
            this.mSuggestionSpanInfo.mSuggestionSpan = span;
            this.mSuggestionSpanInfo.mSpanStart = spanStart;
            this.mSuggestionSpanInfo.mSpanEnd = spanEnd;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class SuggestionSpanInfo {
        int mSpanEnd;
        int mSpanStart;
        SuggestionSpan mSuggestionSpan;

        private SuggestionSpanInfo() {
        }

        void clear() {
            this.mSuggestionSpan = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SuggestionHelper {
        private final HashMap<SuggestionSpan, Integer> mSpansLengths;
        private final Comparator<SuggestionSpan> mSuggestionSpanComparator;

        private SuggestionHelper() {
            this.mSuggestionSpanComparator = new SuggestionSpanComparator();
            this.mSpansLengths = new HashMap<>();
        }

        /* loaded from: classes4.dex */
        private class SuggestionSpanComparator implements Comparator<SuggestionSpan> {
            private SuggestionSpanComparator() {
            }

            @Override // java.util.Comparator
            public int compare(SuggestionSpan span1, SuggestionSpan span2) {
                int flag1 = span1.getFlags();
                int flag2 = span2.getFlags();
                if (flag1 != flag2) {
                    int easy = compareFlag(1, flag1, flag2);
                    if (easy != 0) {
                        return easy;
                    }
                    int misspelled = compareFlag(2, flag1, flag2);
                    if (misspelled != 0) {
                        return misspelled;
                    }
                    int grammarError = compareFlag(8, flag1, flag2);
                    if (grammarError != 0) {
                        return grammarError;
                    }
                }
                return ((Integer) SuggestionHelper.this.mSpansLengths.get(span1)).intValue() - ((Integer) SuggestionHelper.this.mSpansLengths.get(span2)).intValue();
            }

            private int compareFlag(int flagToCompare, int flags1, int flags2) {
                boolean hasFlag1 = (flags1 & flagToCompare) != 0;
                boolean hasFlag2 = (flags2 & flagToCompare) != 0;
                if (hasFlag1 == hasFlag2) {
                    return 0;
                }
                return hasFlag1 ? -1 : 1;
            }
        }

        private SuggestionSpan[] getSortedSuggestionSpans() {
            int pos = Editor.this.mTextView.getSelectionStart();
            Spannable spannable = (Spannable) Editor.this.mTextView.getText();
            SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) spannable.getSpans(pos, pos, SuggestionSpan.class);
            this.mSpansLengths.clear();
            for (SuggestionSpan suggestionSpan : suggestionSpans) {
                int start = spannable.getSpanStart(suggestionSpan);
                int end = spannable.getSpanEnd(suggestionSpan);
                this.mSpansLengths.put(suggestionSpan, Integer.valueOf(end - start));
            }
            Arrays.sort(suggestionSpans, this.mSuggestionSpanComparator);
            this.mSpansLengths.clear();
            return suggestionSpans;
        }

        public int getSuggestionInfo(SuggestionInfo[] suggestionInfos, SuggestionSpanInfo misspelledSpanInfo) {
            Spannable spannable;
            SuggestionSpan[] suggestionSpans;
            boolean z;
            SuggestionSpanInfo suggestionSpanInfo = misspelledSpanInfo;
            Spannable spannable2 = (Spannable) Editor.this.mTextView.getText();
            SuggestionSpan[] suggestionSpans2 = getSortedSuggestionSpans();
            int nbSpans = suggestionSpans2.length;
            boolean z2 = false;
            if (nbSpans == 0) {
                return 0;
            }
            int numberOfSuggestions = 0;
            int length = suggestionSpans2.length;
            int i = 0;
            while (i < length) {
                SuggestionSpan suggestionSpan = suggestionSpans2[i];
                int spanStart = spannable2.getSpanStart(suggestionSpan);
                int spanEnd = spannable2.getSpanEnd(suggestionSpan);
                if (suggestionSpanInfo != null && (suggestionSpan.getFlags() & 10) != 0) {
                    suggestionSpanInfo.mSuggestionSpan = suggestionSpan;
                    suggestionSpanInfo.mSpanStart = spanStart;
                    suggestionSpanInfo.mSpanEnd = spanEnd;
                }
                String[] suggestions = suggestionSpan.getSuggestions();
                int nbSuggestions = suggestions.length;
                int suggestionIndex = 0;
                while (suggestionIndex < nbSuggestions) {
                    String suggestion = suggestions[suggestionIndex];
                    int i2 = 0;
                    while (true) {
                        if (i2 < numberOfSuggestions) {
                            SuggestionInfo otherSuggestionInfo = suggestionInfos[i2];
                            spannable = spannable2;
                            Spannable spannable3 = otherSuggestionInfo.mText;
                            if (!spannable3.toString().equals(suggestion)) {
                                suggestionSpans = suggestionSpans2;
                            } else {
                                int otherSpanStart = otherSuggestionInfo.mSuggestionSpanInfo.mSpanStart;
                                suggestionSpans = suggestionSpans2;
                                int otherSpanEnd = otherSuggestionInfo.mSuggestionSpanInfo.mSpanEnd;
                                if (spanStart == otherSpanStart && spanEnd == otherSpanEnd) {
                                    z = false;
                                    break;
                                }
                            }
                            i2++;
                            spannable2 = spannable;
                            suggestionSpans2 = suggestionSpans;
                        } else {
                            spannable = spannable2;
                            suggestionSpans = suggestionSpans2;
                            SuggestionInfo suggestionInfo = suggestionInfos[numberOfSuggestions];
                            suggestionInfo.setSpanInfo(suggestionSpan, spanStart, spanEnd);
                            suggestionInfo.mSuggestionIndex = suggestionIndex;
                            z = false;
                            suggestionInfo.mSuggestionStart = 0;
                            suggestionInfo.mSuggestionEnd = suggestion.length();
                            suggestionInfo.mText.replace(0, suggestionInfo.mText.length(), (CharSequence) suggestion);
                            numberOfSuggestions++;
                            if (numberOfSuggestions >= suggestionInfos.length) {
                                return numberOfSuggestions;
                            }
                        }
                    }
                    suggestionIndex++;
                    z2 = z;
                    spannable2 = spannable;
                    suggestionSpans2 = suggestionSpans;
                }
                i++;
                suggestionSpanInfo = misspelledSpanInfo;
            }
            return numberOfSuggestions;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class SuggestionsPopupWindow extends PinnedPopupWindow implements AdapterView.OnItemClickListener {
        private static final int MAX_NUMBER_SUGGESTIONS = 5;
        private static final String USER_DICTIONARY_EXTRA_LOCALE = "locale";
        private static final String USER_DICTIONARY_EXTRA_WORD = "word";
        private TextView mAddToDictionaryButton;
        private int mContainerMarginTop;
        private int mContainerMarginWidth;
        private LinearLayout mContainerView;
        private Context mContext;
        private boolean mCursorWasVisibleBeforeSuggestions;
        private TextView mDeleteButton;
        private TextAppearanceSpan mHighlightSpan;
        private boolean mIsShowingUp;
        private final SuggestionSpanInfo mMisspelledSpanInfo;
        private int mNumberOfSuggestions;
        private SuggestionInfo[] mSuggestionInfos;
        private ListView mSuggestionListView;
        private SuggestionAdapter mSuggestionsAdapter;

        /* loaded from: classes4.dex */
        private class CustomPopupWindow extends PopupWindow {
            private CustomPopupWindow() {
            }

            @Override // android.widget.PopupWindow
            public void dismiss() {
                if (!isShowing()) {
                    return;
                }
                super.dismiss();
                Editor.this.getPositionListener().removeSubscriber(SuggestionsPopupWindow.this);
                ((Spannable) Editor.this.mTextView.getText()).removeSpan(Editor.this.mSuggestionRangeSpan);
                Editor.this.mTextView.setCursorVisible(SuggestionsPopupWindow.this.mCursorWasVisibleBeforeSuggestions);
                if (Editor.this.hasInsertionController() && !Editor.this.extractedTextModeWillBeStarted()) {
                    Editor.this.getInsertionController().show();
                }
            }
        }

        public SuggestionsPopupWindow() {
            super();
            this.mIsShowingUp = false;
            this.mMisspelledSpanInfo = new SuggestionSpanInfo();
            this.mCursorWasVisibleBeforeSuggestions = Editor.this.mTextView.isCursorVisibleFromAttr();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void setUp() {
            Context applyDefaultTheme = applyDefaultTheme(Editor.this.mTextView.getContext());
            this.mContext = applyDefaultTheme;
            this.mHighlightSpan = new TextAppearanceSpan(applyDefaultTheme, Editor.this.mTextView.mTextEditSuggestionHighlightStyle);
        }

        private Context applyDefaultTheme(Context originalContext) {
            TypedArray a = originalContext.obtainStyledAttributes(new int[]{16844176});
            boolean isLightTheme = a.getBoolean(0, true);
            int themeId = isLightTheme ? 16974410 : 16974411;
            a.recycle();
            return new ContextThemeWrapper(originalContext, themeId);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void createPopupWindow() {
            this.mPopupWindow = new CustomPopupWindow();
            this.mPopupWindow.setInputMethodMode(2);
            this.mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            this.mPopupWindow.setFocusable(true);
            this.mPopupWindow.setClippingEnabled(false);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void initContentView() {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mContentView = (ViewGroup) inflater.inflate(Editor.this.mTextView.mTextEditSuggestionContainerLayout, (ViewGroup) null);
            LinearLayout linearLayout = (LinearLayout) this.mContentView.findViewById(C4057R.C4059id.suggestionWindowContainer);
            this.mContainerView = linearLayout;
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
            this.mContainerMarginWidth = lp.leftMargin + lp.rightMargin;
            this.mContainerMarginTop = lp.topMargin;
            this.mClippingLimitLeft = lp.leftMargin;
            this.mClippingLimitRight = lp.rightMargin;
            this.mSuggestionListView = (ListView) this.mContentView.findViewById(C4057R.C4059id.suggestionContainer);
            SuggestionAdapter suggestionAdapter = new SuggestionAdapter();
            this.mSuggestionsAdapter = suggestionAdapter;
            this.mSuggestionListView.setAdapter((ListAdapter) suggestionAdapter);
            this.mSuggestionListView.setOnItemClickListener(this);
            this.mSuggestionInfos = new SuggestionInfo[5];
            int i = 0;
            while (true) {
                SuggestionInfo[] suggestionInfoArr = this.mSuggestionInfos;
                if (i < suggestionInfoArr.length) {
                    suggestionInfoArr[i] = new SuggestionInfo();
                    i++;
                } else {
                    TextView textView = (TextView) this.mContentView.findViewById(C4057R.C4059id.addToDictionaryButton);
                    this.mAddToDictionaryButton = textView;
                    textView.setOnClickListener(new View.OnClickListener() { // from class: android.widget.Editor.SuggestionsPopupWindow.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            SuggestionSpan misspelledSpan = Editor.this.findEquivalentSuggestionSpan(SuggestionsPopupWindow.this.mMisspelledSpanInfo);
                            if (misspelledSpan == null) {
                                return;
                            }
                            Editable editable = (Editable) Editor.this.mTextView.getText();
                            int spanStart = editable.getSpanStart(misspelledSpan);
                            int spanEnd = editable.getSpanEnd(misspelledSpan);
                            if (spanStart < 0 || spanEnd <= spanStart) {
                                return;
                            }
                            String originalText = TextUtils.substring(editable, spanStart, spanEnd);
                            Intent intent = new Intent(Settings.ACTION_USER_DICTIONARY_INSERT);
                            intent.putExtra("word", originalText);
                            intent.putExtra("locale", Editor.this.mTextView.getTextServicesLocale().toString());
                            intent.setFlags(intent.getFlags() | 268435456);
                            Editor.this.mTextView.startActivityAsTextOperationUserIfNecessary(intent);
                            editable.removeSpan(SuggestionsPopupWindow.this.mMisspelledSpanInfo.mSuggestionSpan);
                            Selection.setSelection(editable, spanEnd);
                            Editor.this.updateSpellCheckSpans(spanStart, spanEnd, false);
                            SuggestionsPopupWindow.this.hideWithCleanUp();
                        }
                    });
                    TextView textView2 = (TextView) this.mContentView.findViewById(C4057R.C4059id.deleteButton);
                    this.mDeleteButton = textView2;
                    textView2.setOnClickListener(new View.OnClickListener() { // from class: android.widget.Editor.SuggestionsPopupWindow.2
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            Editable editable = (Editable) Editor.this.mTextView.getText();
                            int spanUnionStart = editable.getSpanStart(Editor.this.mSuggestionRangeSpan);
                            int spanUnionEnd = editable.getSpanEnd(Editor.this.mSuggestionRangeSpan);
                            if (spanUnionStart >= 0 && spanUnionEnd > spanUnionStart) {
                                if (spanUnionEnd < editable.length() && Character.isSpaceChar(editable.charAt(spanUnionEnd)) && (spanUnionStart == 0 || Character.isSpaceChar(editable.charAt(spanUnionStart - 1)))) {
                                    spanUnionEnd++;
                                }
                                Editor.this.mTextView.deleteText_internal(spanUnionStart, spanUnionEnd);
                            }
                            SuggestionsPopupWindow.this.hideWithCleanUp();
                        }
                    });
                    return;
                }
            }
        }

        public boolean isShowingUp() {
            return this.mIsShowingUp;
        }

        public void onParentLostFocus() {
            this.mIsShowingUp = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public class SuggestionAdapter extends BaseAdapter {
            private LayoutInflater mInflater;

            private SuggestionAdapter() {
                this.mInflater = (LayoutInflater) SuggestionsPopupWindow.this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override // android.widget.Adapter
            public int getCount() {
                return SuggestionsPopupWindow.this.mNumberOfSuggestions;
            }

            @Override // android.widget.Adapter
            public Object getItem(int position) {
                return SuggestionsPopupWindow.this.mSuggestionInfos[position];
            }

            @Override // android.widget.Adapter
            public long getItemId(int position) {
                return position;
            }

            @Override // android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) convertView;
                if (textView == null) {
                    textView = (TextView) this.mInflater.inflate(Editor.this.mTextView.mTextEditSuggestionItemLayout, parent, false);
                }
                SuggestionInfo suggestionInfo = SuggestionsPopupWindow.this.mSuggestionInfos[position];
                textView.setText(suggestionInfo.mText);
                return textView;
            }
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        public void show() {
            if (!(Editor.this.mTextView.getText() instanceof Editable) || Editor.this.extractedTextModeWillBeStarted()) {
                return;
            }
            if (updateSuggestions()) {
                this.mCursorWasVisibleBeforeSuggestions = Editor.this.mTextView.isCursorVisibleFromAttr();
                Editor.this.mTextView.setCursorVisible(false);
                this.mIsShowingUp = true;
                super.show();
            }
            this.mSuggestionListView.setVisibility(this.mNumberOfSuggestions == 0 ? 8 : 0);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void measureContent() {
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            int horizontalMeasure = View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, Integer.MIN_VALUE);
            int verticalMeasure = View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, Integer.MIN_VALUE);
            int width = 0;
            View view = null;
            for (int i = 0; i < this.mNumberOfSuggestions; i++) {
                view = this.mSuggestionsAdapter.getView(i, view, this.mContentView);
                view.getLayoutParams().width = -2;
                view.measure(horizontalMeasure, verticalMeasure);
                width = Math.max(width, view.getMeasuredWidth());
            }
            if (this.mAddToDictionaryButton.getVisibility() != 8) {
                this.mAddToDictionaryButton.measure(horizontalMeasure, verticalMeasure);
                width = Math.max(width, this.mAddToDictionaryButton.getMeasuredWidth());
            }
            this.mDeleteButton.measure(horizontalMeasure, verticalMeasure);
            int width2 = Math.max(width, this.mDeleteButton.getMeasuredWidth()) + this.mContainerView.getPaddingLeft() + this.mContainerView.getPaddingRight() + this.mContainerMarginWidth;
            this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(width2, 1073741824), verticalMeasure);
            Drawable popupBackground = this.mPopupWindow.getBackground();
            if (popupBackground != null) {
                if (Editor.this.mTempRect == null) {
                    Editor.this.mTempRect = new Rect();
                }
                popupBackground.getPadding(Editor.this.mTempRect);
                width2 += Editor.this.mTempRect.left + Editor.this.mTempRect.right;
            }
            this.mPopupWindow.setWidth(width2);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getTextOffset() {
            return (Editor.this.mTextView.getSelectionStart() + Editor.this.mTextView.getSelectionStart()) / 2;
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getVerticalLocalPosition(int line) {
            Layout layout = Editor.this.mTextView.getLayout();
            return layout.getLineBottom(line, false) - this.mContainerMarginTop;
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int clipVertically(int positionY) {
            int height = this.mContentView.getMeasuredHeight();
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            return Math.min(positionY, displayMetrics.heightPixels - height);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void hideWithCleanUp() {
            SuggestionInfo[] suggestionInfoArr;
            for (SuggestionInfo info : this.mSuggestionInfos) {
                info.clear();
            }
            this.mMisspelledSpanInfo.clear();
            hide();
        }

        private boolean updateSuggestions() {
            int underlineColor;
            Spannable spannable = (Spannable) Editor.this.mTextView.getText();
            int suggestionInfo = Editor.this.mSuggestionHelper.getSuggestionInfo(this.mSuggestionInfos, this.mMisspelledSpanInfo);
            this.mNumberOfSuggestions = suggestionInfo;
            if (suggestionInfo == 0 && this.mMisspelledSpanInfo.mSuggestionSpan == null) {
                return false;
            }
            int spanUnionStart = Editor.this.mTextView.getText().length();
            int spanUnionEnd = 0;
            for (int i = 0; i < this.mNumberOfSuggestions; i++) {
                SuggestionSpanInfo spanInfo = this.mSuggestionInfos[i].mSuggestionSpanInfo;
                spanUnionStart = Math.min(spanUnionStart, spanInfo.mSpanStart);
                spanUnionEnd = Math.max(spanUnionEnd, spanInfo.mSpanEnd);
            }
            if (this.mMisspelledSpanInfo.mSuggestionSpan != null) {
                spanUnionStart = Math.min(spanUnionStart, this.mMisspelledSpanInfo.mSpanStart);
                spanUnionEnd = Math.max(spanUnionEnd, this.mMisspelledSpanInfo.mSpanEnd);
            }
            for (int i2 = 0; i2 < this.mNumberOfSuggestions; i2++) {
                highlightTextDifferences(this.mSuggestionInfos[i2], spanUnionStart, spanUnionEnd);
            }
            int addToDictionaryButtonVisibility = 8;
            if (this.mMisspelledSpanInfo.mSuggestionSpan != null && this.mMisspelledSpanInfo.mSpanStart >= 0 && this.mMisspelledSpanInfo.mSpanEnd > this.mMisspelledSpanInfo.mSpanStart) {
                addToDictionaryButtonVisibility = 0;
            }
            this.mAddToDictionaryButton.setVisibility(addToDictionaryButtonVisibility);
            if (Editor.this.mSuggestionRangeSpan == null) {
                Editor.this.mSuggestionRangeSpan = new SuggestionRangeSpan();
            }
            if (this.mNumberOfSuggestions != 0) {
                underlineColor = this.mSuggestionInfos[0].mSuggestionSpanInfo.mSuggestionSpan.getUnderlineColor();
            } else {
                underlineColor = this.mMisspelledSpanInfo.mSuggestionSpan.getUnderlineColor();
            }
            if (underlineColor == 0) {
                Editor.this.mSuggestionRangeSpan.setBackgroundColor(Editor.this.mTextView.mHighlightColor);
            } else {
                int newAlpha = (int) (Color.alpha(underlineColor) * 0.4f);
                Editor.this.mSuggestionRangeSpan.setBackgroundColor((16777215 & underlineColor) + (newAlpha << 24));
            }
            boolean sendAccessibilityEvent = Editor.this.mTextView.isVisibleToAccessibility();
            CharSequence beforeText = sendAccessibilityEvent ? new SpannedString(spannable, true) : null;
            spannable.setSpan(Editor.this.mSuggestionRangeSpan, spanUnionStart, spanUnionEnd, 33);
            if (sendAccessibilityEvent) {
                Editor.this.mTextView.sendAccessibilityEventTypeViewTextChanged(beforeText, spanUnionStart, spanUnionEnd);
            }
            this.mSuggestionsAdapter.notifyDataSetChanged();
            return true;
        }

        private void highlightTextDifferences(SuggestionInfo suggestionInfo, int unionStart, int unionEnd) {
            Spannable text = (Spannable) Editor.this.mTextView.getText();
            int spanStart = suggestionInfo.mSuggestionSpanInfo.mSpanStart;
            int spanEnd = suggestionInfo.mSuggestionSpanInfo.mSpanEnd;
            suggestionInfo.mSuggestionStart = spanStart - unionStart;
            suggestionInfo.mSuggestionEnd = suggestionInfo.mSuggestionStart + suggestionInfo.mText.length();
            suggestionInfo.mText.setSpan(this.mHighlightSpan, 0, suggestionInfo.mText.length(), 33);
            String textAsString = text.toString();
            suggestionInfo.mText.insert(0, (CharSequence) textAsString.substring(unionStart, spanStart));
            suggestionInfo.mText.append((CharSequence) textAsString.substring(spanEnd, unionEnd));
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SuggestionInfo suggestionInfo = this.mSuggestionInfos[position];
            Editor.this.replaceWithSuggestion(suggestionInfo);
            hideWithCleanUp();
        }
    }

    /* loaded from: classes4.dex */
    public class AssistantCallbackHelper {
        private final Map<MenuItem, View.OnClickListener> mAssistClickHandlers = new HashMap();
        private final SelectionActionModeHelper mHelper;
        private TextClassification mPrevTextClassification;

        public AssistantCallbackHelper(SelectionActionModeHelper helper) {
            this.mHelper = helper;
        }

        public void clearCallbackHandlers() {
            this.mAssistClickHandlers.clear();
        }

        public View.OnClickListener getOnClickListener(MenuItem key) {
            return this.mAssistClickHandlers.get(key);
        }

        public void updateAssistMenuItems(Menu menu, MenuItem.OnMenuItemClickListener listener) {
            TextClassification textClassification = this.mHelper.getTextClassification();
            if (this.mPrevTextClassification == textClassification) {
                return;
            }
            clearAssistMenuItems(menu);
            if (textClassification == null || !shouldEnableAssistMenuItems()) {
                return;
            }
            if (!textClassification.getActions().isEmpty()) {
                addAssistMenuItem(menu, textClassification.getActions().get(0), 16908353, 0, 2, listener).setIntent(textClassification.getIntent());
            } else if (hasLegacyAssistItem(textClassification)) {
                MenuItem item = menu.add(16908353, 16908353, 0, textClassification.getLabel()).setIcon(textClassification.getIcon()).setIntent(textClassification.getIntent());
                item.setShowAsAction(2);
                this.mAssistClickHandlers.put(item, TextClassification.createIntentOnClickListener(TextClassification.createPendingIntent(Editor.this.mTextView.getContext(), textClassification.getIntent(), createAssistMenuItemPendingIntentRequestCode())));
            }
            int count = textClassification.getActions().size();
            for (int i = 1; i < count; i++) {
                addAssistMenuItem(menu, textClassification.getActions().get(i), 0, (i + 50) - 1, 0, listener);
            }
            this.mPrevTextClassification = textClassification;
        }

        private MenuItem addAssistMenuItem(Menu menu, RemoteAction action, int itemId, int order, int showAsAction, MenuItem.OnMenuItemClickListener listener) {
            MenuItem item = menu.add(16908353, itemId, order, action.getTitle()).setContentDescription(action.getContentDescription());
            if (action.shouldShowIcon()) {
                item.setIcon(action.getIcon().loadDrawable(Editor.this.mTextView.getContext()));
            }
            item.setShowAsAction(showAsAction);
            this.mAssistClickHandlers.put(item, TextClassification.createIntentOnClickListener(action.getActionIntent()));
            Editor.this.mA11ySmartActions.addAction(action);
            if (listener != null) {
                item.setOnMenuItemClickListener(listener);
            }
            return item;
        }

        private void clearAssistMenuItems(Menu menu) {
            int i = 0;
            while (i < menu.size()) {
                MenuItem menuItem = menu.getItem(i);
                if (menuItem.getGroupId() == 16908353) {
                    menu.removeItem(menuItem.getItemId());
                } else {
                    i++;
                }
            }
            Editor.this.mA11ySmartActions.reset();
        }

        private boolean hasLegacyAssistItem(TextClassification classification) {
            return ((classification.getIcon() == null && TextUtils.isEmpty(classification.getLabel())) || (classification.getIntent() == null && classification.getOnClickListener() == null)) ? false : true;
        }

        private boolean shouldEnableAssistMenuItems() {
            return Editor.this.mTextView.isDeviceProvisioned() && TextClassificationManager.getSettings(Editor.this.mTextView.getContext()).isSmartTextShareEnabled();
        }

        private int createAssistMenuItemPendingIntentRequestCode() {
            if (Editor.this.mTextView.hasSelection()) {
                return Editor.this.mTextView.getText().subSequence(Editor.this.mTextView.getSelectionStart(), Editor.this.mTextView.getSelectionEnd()).hashCode();
            }
            return 0;
        }

        public boolean onAssistMenuItemClicked(MenuItem assistMenuItem) {
            Intent intent;
            Preconditions.checkArgument(assistMenuItem.getGroupId() == 16908353);
            TextClassification textClassification = Editor.this.getSelectionActionModeHelper().getTextClassification();
            if (!shouldEnableAssistMenuItems() || textClassification == null) {
                return true;
            }
            View.OnClickListener onClickListener = getOnClickListener(assistMenuItem);
            if (onClickListener == null && (intent = assistMenuItem.getIntent()) != null) {
                onClickListener = TextClassification.createIntentOnClickListener(TextClassification.createPendingIntent(Editor.this.mTextView.getContext(), intent, createAssistMenuItemPendingIntentRequestCode()));
            }
            if (onClickListener != null) {
                onClickListener.onClick(Editor.this.mTextView);
                Editor.this.lambda$startActionModeInternal$0();
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class TextActionModeCallback extends ActionMode.Callback2 {
        private final int mHandleHeight;
        private final boolean mHasSelection;
        private final AssistantCallbackHelper mHelper;
        private final Path mSelectionPath = new Path();
        private final RectF mSelectionBounds = new RectF();

        TextActionModeCallback(int mode) {
            this.mHelper = new AssistantCallbackHelper(Editor.this.getSelectionActionModeHelper());
            boolean z = mode == 0 || (Editor.this.mTextIsSelectable && mode == 2);
            this.mHasSelection = z;
            if (z) {
                SelectionModifierCursorController selectionController = Editor.this.getSelectionController();
                if (selectionController.mStartHandle == null) {
                    Editor.this.loadHandleDrawables(false);
                    selectionController.initHandles();
                    selectionController.hide();
                }
                this.mHandleHeight = Math.max(Editor.this.mSelectHandleLeft.getMinimumHeight(), Editor.this.mSelectHandleRight.getMinimumHeight());
                return;
            }
            InsertionPointCursorController insertionController = Editor.this.getInsertionController();
            if (insertionController != null) {
                insertionController.getHandle();
                this.mHandleHeight = Editor.this.mSelectHandleCenter.getMinimumHeight();
                return;
            }
            this.mHandleHeight = 0;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            this.mHelper.clearCallbackHandlers();
            mode.setTitle((CharSequence) null);
            mode.setSubtitle((CharSequence) null);
            mode.setTitleOptionalHint(true);
            populateMenuWithItems(menu);
            ActionMode.Callback customCallback = getCustomCallback();
            if (customCallback != null && !customCallback.onCreateActionMode(mode, menu)) {
                Selection.setSelection((Spannable) Editor.this.mTextView.getText(), Editor.this.mTextView.getSelectionEnd());
                return false;
            }
            if (Editor.this.mTextView.canProcessText()) {
                Editor.this.mProcessTextIntentActionsHandler.onInitializeMenu(menu);
            }
            if (this.mHasSelection && !Editor.this.mTextView.hasTransientState()) {
                Editor.this.mTextView.setHasTransientState(true);
            }
            return true;
        }

        private ActionMode.Callback getCustomCallback() {
            if (this.mHasSelection) {
                return Editor.this.mCustomSelectionActionModeCallback;
            }
            return Editor.this.mCustomInsertionActionModeCallback;
        }

        private void populateMenuWithItems(Menu menu) {
            String selected;
            if (Editor.this.mTextView.canCut()) {
                menu.add(0, 16908320, 4, 17039363).setAlphabeticShortcut(EpicenterTranslateClipReveal.StateProperty.TARGET_X).setShowAsAction(2);
            }
            if (Editor.this.mTextView.canCopy()) {
                menu.add(0, 16908321, 5, 17039361).setAlphabeticShortcut('c').setShowAsAction(2);
            }
            if (Editor.this.mTextView.canPaste()) {
                menu.add(0, 16908322, 6, 17039371).setAlphabeticShortcut('v').setShowAsAction(2);
            }
            if (Editor.this.mTextView.canShare()) {
                menu.add(0, 16908341, 7, C4057R.string.share).setShowAsAction(1);
            }
            if (Editor.this.mTextView.canRequestAutofill() && ((selected = Editor.this.mTextView.getSelectedText()) == null || selected.isEmpty())) {
                menu.add(0, 16908355, 10, 17039386).setShowAsAction(0);
            }
            if (Editor.this.mTextView.canPasteAsPlainText()) {
                menu.add(0, 16908337, 11, 17039385).setShowAsAction(1);
            }
            updateSelectAllItem(menu);
            updateReplaceItem(menu);
            this.mHelper.updateAssistMenuItems(menu, null);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            updateSelectAllItem(menu);
            updateReplaceItem(menu);
            this.mHelper.updateAssistMenuItems(menu, null);
            ActionMode.Callback customCallback = getCustomCallback();
            if (customCallback != null) {
                return customCallback.onPrepareActionMode(mode, menu);
            }
            return true;
        }

        private void updateSelectAllItem(Menu menu) {
            boolean canSelectAll = Editor.this.mTextView.canSelectAllText();
            boolean selectAllItemExists = menu.findItem(16908319) != null;
            if (canSelectAll && !selectAllItemExists) {
                menu.add(0, 16908319, 8, 17039373).setShowAsAction(1);
            } else if (!canSelectAll && selectAllItemExists) {
                menu.removeItem(16908319);
            }
        }

        private void updateReplaceItem(Menu menu) {
            boolean canReplace = Editor.this.mTextView.isSuggestionsEnabled() && Editor.this.shouldOfferToShowSuggestions();
            boolean replaceItemExists = menu.findItem(16908340) != null;
            if (canReplace && !replaceItemExists) {
                menu.add(0, 16908340, 9, C4057R.string.replace).setShowAsAction(1);
            } else if (!canReplace && replaceItemExists) {
                menu.removeItem(16908340);
            }
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Editor.this.getSelectionActionModeHelper().onSelectionAction(item.getItemId(), item.getTitle().toString());
            if (Editor.this.mProcessTextIntentActionsHandler.performMenuItemAction(item)) {
                return true;
            }
            ActionMode.Callback customCallback = getCustomCallback();
            if (customCallback == null || !customCallback.onActionItemClicked(mode, item)) {
                if (item.getGroupId() == 16908353 && this.mHelper.onAssistMenuItemClicked(item)) {
                    return true;
                }
                return Editor.this.mTextView.onTextContextMenuItem(item.getItemId());
            }
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode mode) {
            Editor.this.getSelectionActionModeHelper().onDestroyActionMode();
            Editor.this.mTextActionMode = null;
            ActionMode.Callback customCallback = getCustomCallback();
            if (customCallback != null) {
                customCallback.onDestroyActionMode(mode);
            }
            if (!Editor.this.mPreserveSelection) {
                Selection.setSelection((Spannable) Editor.this.mTextView.getText(), Editor.this.mTextView.getSelectionEnd());
            }
            if (Editor.this.mSelectionModifierCursorController != null) {
                Editor.this.mSelectionModifierCursorController.hide();
            }
            this.mHelper.clearCallbackHandlers();
            Editor.this.mRequestingLinkActionMode = false;
        }

        @Override // android.view.ActionMode.Callback2
        public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
            if (!view.equals(Editor.this.mTextView) || Editor.this.mTextView.getLayout() == null) {
                super.onGetContentRect(mode, view, outRect);
                return;
            }
            int selectionStart = Editor.this.mTextView.getSelectionStartTransformed();
            int selectionEnd = Editor.this.mTextView.getSelectionEndTransformed();
            Layout layout = Editor.this.mTextView.getLayout();
            if (selectionStart != selectionEnd) {
                this.mSelectionPath.reset();
                layout.getSelectionPath(selectionStart, selectionEnd, this.mSelectionPath);
                this.mSelectionPath.computeBounds(this.mSelectionBounds, true);
                this.mSelectionBounds.bottom += this.mHandleHeight;
            } else {
                int line = layout.getLineForOffset(selectionStart);
                float primaryHorizontal = Editor.this.clampHorizontalPosition(null, layout.getPrimaryHorizontal(selectionEnd));
                this.mSelectionBounds.set(primaryHorizontal, layout.getLineTop(line), primaryHorizontal, layout.getLineBottom(line) + this.mHandleHeight);
            }
            int textHorizontalOffset = Editor.this.mTextView.viewportToContentHorizontalOffset();
            int textVerticalOffset = Editor.this.mTextView.viewportToContentVerticalOffset();
            outRect.set((int) Math.floor(this.mSelectionBounds.left + textHorizontalOffset), (int) Math.floor(this.mSelectionBounds.top + textVerticalOffset), (int) Math.ceil(this.mSelectionBounds.right + textHorizontalOffset), (int) Math.ceil(this.mSelectionBounds.bottom + textVerticalOffset));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class CursorAnchorInfoNotifier implements TextViewPositionListener {
        final CursorAnchorInfo.Builder mCursorAnchorInfoBuilder;
        final Matrix mViewToScreenMatrix;

        private CursorAnchorInfoNotifier() {
            this.mCursorAnchorInfoBuilder = new CursorAnchorInfo.Builder();
            this.mViewToScreenMatrix = new Matrix();
        }

        @Override // android.widget.Editor.TextViewPositionListener
        public void updatePosition(int parentPositionX, int parentPositionY, boolean parentPositionChanged, boolean parentScrolled) {
            InputMethodManager imm;
            CursorAnchorInfo cursorAnchorInfo;
            InputMethodState ims = Editor.this.mInputMethodState;
            if (ims != null && ims.mBatchEditNesting <= 0 && (imm = Editor.this.getInputMethodManager()) != null && imm.isActive(Editor.this.mTextView) && (ims.mUpdateCursorAnchorInfoMode & 3) != 0 && (cursorAnchorInfo = Editor.this.mTextView.getCursorAnchorInfo(ims.mUpdateCursorAnchorInfoFilter, this.mCursorAnchorInfoBuilder, this.mViewToScreenMatrix)) != null) {
                imm.updateCursorAnchorInfo(Editor.this.mTextView, cursorAnchorInfo);
                Editor.this.mInputMethodState.mUpdateCursorAnchorInfoMode &= -2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class MagnifierMotionAnimator {
        private static final long DURATION = 100;
        private float mAnimationCurrentX;
        private float mAnimationCurrentY;
        private float mAnimationStartX;
        private float mAnimationStartY;
        private final ValueAnimator mAnimator;
        private float mLastX;
        private float mLastY;
        private final Magnifier mMagnifier;
        private boolean mMagnifierIsShowing;

        private MagnifierMotionAnimator(Magnifier magnifier) {
            this.mMagnifier = magnifier;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mAnimator = ofFloat;
            ofFloat.setDuration(DURATION);
            ofFloat.setInterpolator(new LinearInterpolator());
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.widget.Editor$MagnifierMotionAnimator$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    Editor.MagnifierMotionAnimator.this.lambda$new$0(valueAnimator);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(ValueAnimator animation) {
            float f = this.mAnimationStartX;
            this.mAnimationCurrentX = f + ((this.mLastX - f) * animation.getAnimatedFraction());
            float f2 = this.mAnimationStartY;
            float animatedFraction = f2 + ((this.mLastY - f2) * animation.getAnimatedFraction());
            this.mAnimationCurrentY = animatedFraction;
            this.mMagnifier.show(this.mAnimationCurrentX, animatedFraction);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void show(float x, float y) {
            boolean startNewAnimation = this.mMagnifierIsShowing && y != this.mLastY;
            if (startNewAnimation) {
                if (this.mAnimator.isRunning()) {
                    this.mAnimator.cancel();
                    this.mAnimationStartX = this.mAnimationCurrentX;
                    this.mAnimationStartY = this.mAnimationCurrentY;
                } else {
                    this.mAnimationStartX = this.mLastX;
                    this.mAnimationStartY = this.mLastY;
                }
                this.mAnimator.start();
            } else if (!this.mAnimator.isRunning()) {
                this.mMagnifier.show(x, y);
            }
            this.mLastX = x;
            this.mLastY = y;
            this.mMagnifierIsShowing = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void update() {
            this.mMagnifier.update();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dismiss() {
            this.mMagnifier.dismiss();
            this.mAnimator.cancel();
            this.mMagnifierIsShowing = false;
        }
    }

    /* loaded from: classes4.dex */
    public abstract class HandleView extends View implements TextViewPositionListener {
        private static final int HISTORY_SIZE = 5;
        private static final int TOUCH_UP_FILTER_DELAY_AFTER = 150;
        private static final int TOUCH_UP_FILTER_DELAY_BEFORE = 350;
        private final PopupWindow mContainer;
        private float mCurrentDragInitialTouchRawX;
        protected Drawable mDrawable;
        protected Drawable mDrawableLtr;
        protected Drawable mDrawableRtl;
        protected int mHorizontalGravity;
        protected int mHotspotX;
        private final int mIdealFingerToCursorOffset;
        private final float mIdealVerticalOffset;
        private boolean mIsDragging;
        private int mLastParentX;
        private int mLastParentXOnScreen;
        private int mLastParentY;
        private int mLastParentYOnScreen;
        private int mMinSize;
        private int mNumberPreviousOffsets;
        private boolean mPositionHasChanged;
        private int mPositionX;
        private int mPositionY;
        protected int mPrevLine;
        protected int mPreviousLineTouched;
        protected int mPreviousOffset;
        private int mPreviousOffsetIndex;
        private final int[] mPreviousOffsets;
        private final long[] mPreviousOffsetsTimes;
        private float mTextViewScaleX;
        private float mTextViewScaleY;
        private float mTouchOffsetY;
        private float mTouchToWindowOffsetX;
        private float mTouchToWindowOffsetY;

        public abstract int getCurrentCursorOffset();

        protected abstract int getHorizontalGravity(boolean z);

        protected abstract int getHotspotX(Drawable drawable, boolean z);

        protected abstract int getMagnifierHandleTrigger();

        protected abstract void updatePosition(float f, float f2, boolean z);

        protected abstract void updateSelection(int i);

        private HandleView(Drawable drawableLtr, Drawable drawableRtl, int id) {
            super(Editor.this.mTextView.getContext());
            this.mPreviousOffset = -1;
            this.mPositionHasChanged = true;
            this.mPrevLine = -1;
            this.mPreviousLineTouched = -1;
            this.mCurrentDragInitialTouchRawX = -1.0f;
            this.mPreviousOffsetsTimes = new long[5];
            this.mPreviousOffsets = new int[5];
            this.mPreviousOffsetIndex = 0;
            this.mNumberPreviousOffsets = 0;
            setId(id);
            PopupWindow popupWindow = new PopupWindow(Editor.this.mTextView.getContext(), (AttributeSet) null, 16843464);
            this.mContainer = popupWindow;
            popupWindow.setSplitTouchEnabled(true);
            popupWindow.setClippingEnabled(false);
            popupWindow.setWindowLayoutType(1002);
            popupWindow.setWidth(-2);
            popupWindow.setHeight(-2);
            popupWindow.setContentView(this);
            setDrawables(drawableLtr, drawableRtl);
            this.mMinSize = Editor.this.mTextView.getContext().getResources().getDimensionPixelSize(C4057R.dimen.text_handle_min_size);
            int handleHeight = getPreferredHeight();
            this.mTouchOffsetY = handleHeight * (-0.3f);
            int distance = AppGlobals.getIntCoreSetting(WidgetFlags.KEY_FINGER_TO_CURSOR_DISTANCE, -1);
            if (distance < 0 || distance > 100) {
                float f = handleHeight * 0.7f;
                this.mIdealVerticalOffset = f;
                this.mIdealFingerToCursorOffset = (int) (f - this.mTouchOffsetY);
                return;
            }
            int applyDimension = (int) TypedValue.applyDimension(1, distance, Editor.this.mTextView.getContext().getResources().getDisplayMetrics());
            this.mIdealFingerToCursorOffset = applyDimension;
            this.mIdealVerticalOffset = applyDimension + this.mTouchOffsetY;
        }

        public float getIdealVerticalOffset() {
            return this.mIdealVerticalOffset;
        }

        final int getIdealFingerToCursorOffset() {
            return this.mIdealFingerToCursorOffset;
        }

        void setDrawables(Drawable drawableLtr, Drawable drawableRtl) {
            this.mDrawableLtr = drawableLtr;
            this.mDrawableRtl = drawableRtl;
            updateDrawable(true);
        }

        protected void updateDrawable(boolean updateDrawableWhenDragging) {
            Layout layout;
            if ((!updateDrawableWhenDragging && this.mIsDragging) || (layout = Editor.this.mTextView.getLayout()) == null) {
                return;
            }
            int offset = getCurrentCursorOffset();
            boolean isRtlCharAtOffset = isAtRtlRun(layout, offset);
            Drawable oldDrawable = this.mDrawable;
            Drawable drawable = isRtlCharAtOffset ? this.mDrawableRtl : this.mDrawableLtr;
            this.mDrawable = drawable;
            this.mHotspotX = getHotspotX(drawable, isRtlCharAtOffset);
            this.mHorizontalGravity = getHorizontalGravity(isRtlCharAtOffset);
            if (oldDrawable != this.mDrawable && isShowing()) {
                int cursorHorizontalPosition = ((getCursorHorizontalPosition(layout, offset) - this.mHotspotX) - getHorizontalOffset()) + getCursorOffset();
                this.mPositionX = cursorHorizontalPosition;
                this.mPositionX = cursorHorizontalPosition + Editor.this.mTextView.viewportToContentHorizontalOffset();
                this.mPositionHasChanged = true;
                updatePosition(this.mLastParentX, this.mLastParentY, false, false);
                postInvalidate();
            }
        }

        private void startTouchUpFilter(int offset) {
            this.mNumberPreviousOffsets = 0;
            addPositionToTouchUpFilter(offset);
        }

        private void addPositionToTouchUpFilter(int offset) {
            int i = (this.mPreviousOffsetIndex + 1) % 5;
            this.mPreviousOffsetIndex = i;
            this.mPreviousOffsets[i] = offset;
            this.mPreviousOffsetsTimes[i] = SystemClock.uptimeMillis();
            this.mNumberPreviousOffsets++;
        }

        private void filterOnTouchUp(boolean fromTouchScreen) {
            long now = SystemClock.uptimeMillis();
            int i = 0;
            int index = this.mPreviousOffsetIndex;
            int iMax = Math.min(this.mNumberPreviousOffsets, 5);
            while (i < iMax && now - this.mPreviousOffsetsTimes[index] < 150) {
                i++;
                index = ((this.mPreviousOffsetIndex - i) + 5) % 5;
            }
            if (i > 0 && i < iMax && now - this.mPreviousOffsetsTimes[index] > 350) {
                positionAtCursorOffset(this.mPreviousOffsets[index], false, fromTouchScreen);
            }
        }

        public boolean offsetHasBeenChanged() {
            return this.mNumberPreviousOffsets > 1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(getPreferredWidth(), getPreferredHeight());
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            if (isShowing()) {
                positionAtCursorOffset(getCurrentCursorOffset(), true, false);
            }
        }

        protected final int getPreferredWidth() {
            return Math.max(this.mDrawable.getIntrinsicWidth(), this.mMinSize);
        }

        protected final int getPreferredHeight() {
            return Math.max(this.mDrawable.getIntrinsicHeight(), this.mMinSize);
        }

        public void show() {
            if (isShowing()) {
                return;
            }
            Editor.this.getPositionListener().addSubscriber(this, true);
            this.mPreviousOffset = -1;
            positionAtCursorOffset(getCurrentCursorOffset(), false, false);
        }

        protected void dismiss() {
            this.mIsDragging = false;
            this.mContainer.dismiss();
            onDetached();
        }

        public void hide() {
            dismiss();
            Editor.this.getPositionListener().removeSubscriber(this);
        }

        public boolean isShowing() {
            return this.mContainer.isShowing();
        }

        private boolean shouldShow() {
            if (this.mIsDragging) {
                return true;
            }
            if (Editor.this.mTextView.isInBatchEditMode()) {
                return false;
            }
            return Editor.this.mTextView.isPositionVisible(this.mPositionX + this.mHotspotX + getHorizontalOffset(), this.mPositionY);
        }

        private void setVisible(boolean visible) {
            this.mContainer.getContentView().setVisibility(visible ? 0 : 4);
        }

        protected boolean isAtRtlRun(Layout layout, int offset) {
            int transformedOffset = Editor.this.mTextView.originalToTransformed(offset, 1);
            return layout.isRtlCharAt(transformedOffset);
        }

        public float getHorizontal(Layout layout, int offset) {
            int transformedOffset = Editor.this.mTextView.originalToTransformed(offset, 1);
            return layout.getPrimaryHorizontal(transformedOffset);
        }

        public int getLineForOffset(Layout layout, int offset) {
            int transformedOffset = Editor.this.mTextView.originalToTransformed(offset, 1);
            return layout.getLineForOffset(transformedOffset);
        }

        protected int getOffsetAtCoordinate(Layout layout, int line, float x) {
            return Editor.this.mTextView.getOffsetAtCoordinate(line, x);
        }

        protected void positionAtCursorOffset(int offset, boolean forceUpdatePosition, boolean fromTouchScreen) {
            Layout layout = Editor.this.mTextView.getLayout();
            if (layout == null) {
                Editor.this.prepareCursorControllers();
                return;
            }
            boolean offsetChanged = offset != this.mPreviousOffset;
            if (offsetChanged || forceUpdatePosition) {
                if (offsetChanged) {
                    updateSelection(offset);
                    if (fromTouchScreen && Editor.this.mHapticTextHandleEnabled) {
                        Editor.this.mTextView.performHapticFeedback(9);
                    }
                    addPositionToTouchUpFilter(offset);
                }
                int line = getLineForOffset(layout, offset);
                this.mPrevLine = line;
                this.mPositionX = ((getCursorHorizontalPosition(layout, offset) - this.mHotspotX) - getHorizontalOffset()) + getCursorOffset();
                this.mPositionY = layout.getLineBottom(line, false);
                this.mPositionX += Editor.this.mTextView.viewportToContentHorizontalOffset();
                this.mPositionY += Editor.this.mTextView.viewportToContentVerticalOffset();
                this.mPreviousOffset = offset;
                this.mPositionHasChanged = true;
            }
        }

        int getCursorHorizontalPosition(Layout layout, int offset) {
            return (int) (getHorizontal(layout, offset) - 0.5f);
        }

        @Override // android.widget.Editor.TextViewPositionListener
        public void updatePosition(int parentPositionX, int parentPositionY, boolean parentPositionChanged, boolean parentScrolled) {
            int i;
            positionAtCursorOffset(getCurrentCursorOffset(), parentScrolled, false);
            if (parentPositionChanged || this.mPositionHasChanged) {
                if (this.mIsDragging) {
                    if (parentPositionX != this.mLastParentX || parentPositionY != this.mLastParentY) {
                        this.mTouchToWindowOffsetX += parentPositionX - i;
                        this.mTouchToWindowOffsetY += parentPositionY - this.mLastParentY;
                        this.mLastParentX = parentPositionX;
                        this.mLastParentY = parentPositionY;
                    }
                    onHandleMoved();
                }
                if (shouldShow()) {
                    int[] pts = {this.mPositionX + this.mHotspotX + getHorizontalOffset(), this.mPositionY};
                    Editor.this.mTextView.transformFromViewToWindowSpace(pts);
                    pts[0] = pts[0] - (this.mHotspotX + getHorizontalOffset());
                    if (isShowing()) {
                        this.mContainer.update(pts[0], pts[1], -1, -1);
                    } else {
                        this.mContainer.showAtLocation(Editor.this.mTextView, 0, pts[0], pts[1]);
                    }
                } else if (isShowing()) {
                    dismiss();
                }
                this.mPositionHasChanged = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onDraw(Canvas c) {
            int drawWidth = this.mDrawable.getIntrinsicWidth();
            int left = getHorizontalOffset();
            Drawable drawable = this.mDrawable;
            drawable.setBounds(left, 0, left + drawWidth, drawable.getIntrinsicHeight());
            this.mDrawable.draw(c);
        }

        private int getHorizontalOffset() {
            int width = getPreferredWidth();
            int drawWidth = this.mDrawable.getIntrinsicWidth();
            switch (this.mHorizontalGravity) {
                case 3:
                    return 0;
                case 4:
                default:
                    int left = (width - drawWidth) / 2;
                    return left;
                case 5:
                    int left2 = width - drawWidth;
                    return left2;
            }
        }

        protected int getCursorOffset() {
            return 0;
        }

        private boolean tooLargeTextForMagnifier() {
            if (Editor.this.mNewMagnifierEnabled) {
                Layout layout = Editor.this.mTextView.getLayout();
                int line = getLineForOffset(layout, getCurrentCursorOffset());
                return layout.getLineBottom(line, false) - layout.getLineTop(line) >= Editor.this.mMaxLineHeightForMagnifier;
            }
            float magnifierContentHeight = Math.round(Editor.this.mMagnifierAnimator.mMagnifier.getHeight() / Editor.this.mMagnifierAnimator.mMagnifier.getZoom());
            Paint.FontMetrics fontMetrics = Editor.this.mTextView.getPaint().getFontMetrics();
            float glyphHeight = fontMetrics.descent - fontMetrics.ascent;
            return this.mTextViewScaleY * glyphHeight > magnifierContentHeight;
        }

        private boolean checkForTransforms() {
            if (Editor.this.mMagnifierAnimator.mMagnifierIsShowing) {
                return true;
            }
            if (Editor.this.mTextView.getRotation() == 0.0f && Editor.this.mTextView.getRotationX() == 0.0f && Editor.this.mTextView.getRotationY() == 0.0f) {
                this.mTextViewScaleX = Editor.this.mTextView.getScaleX();
                this.mTextViewScaleY = Editor.this.mTextView.getScaleY();
                for (ViewParent viewParent = Editor.this.mTextView.getParent(); viewParent != null; viewParent = viewParent.getParent()) {
                    if (viewParent instanceof View) {
                        View view = (View) viewParent;
                        if (view.getRotation() != 0.0f || view.getRotationX() != 0.0f || view.getRotationY() != 0.0f) {
                            return false;
                        }
                        this.mTextViewScaleX *= view.getScaleX();
                        this.mTextViewScaleY *= view.getScaleY();
                    }
                }
                return true;
            }
            return false;
        }

        /* JADX WARN: Removed duplicated region for block: B:36:0x00c5  */
        /* JADX WARN: Removed duplicated region for block: B:41:0x00da  */
        /* JADX WARN: Removed duplicated region for block: B:51:0x0131  */
        /* JADX WARN: Removed duplicated region for block: B:60:0x0189  */
        /* JADX WARN: Removed duplicated region for block: B:66:0x019d  */
        /* JADX WARN: Removed duplicated region for block: B:67:0x019f  */
        /* JADX WARN: Removed duplicated region for block: B:70:0x01fc A[ADDED_TO_REGION] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        private boolean obtainMagnifierShowCoordinates(MotionEvent event, PointF showPosInView) {
            int offset;
            int otherHandleOffset;
            boolean rtl;
            float leftBound;
            float rightBound;
            float leftBound2;
            float rightBound2;
            float contentWidth;
            float contentWidth2;
            float scaledTouchXInView;
            int trigger = getMagnifierHandleTrigger();
            switch (trigger) {
                case 0:
                    offset = Editor.this.mTextView.getSelectionStart();
                    otherHandleOffset = -1;
                    break;
                case 1:
                    offset = Editor.this.mTextView.getSelectionStart();
                    otherHandleOffset = Editor.this.mTextView.getSelectionEnd();
                    break;
                case 2:
                    offset = Editor.this.mTextView.getSelectionEnd();
                    otherHandleOffset = Editor.this.mTextView.getSelectionStart();
                    break;
                default:
                    offset = -1;
                    otherHandleOffset = -1;
                    break;
            }
            if (offset == -1) {
                return false;
            }
            if (event.getActionMasked() == 0) {
                this.mCurrentDragInitialTouchRawX = event.getRawX();
            } else if (event.getActionMasked() == 1) {
                this.mCurrentDragInitialTouchRawX = -1.0f;
            }
            Layout layout = Editor.this.mTextView.getLayout();
            int lineNumber = getLineForOffset(layout, offset);
            boolean sameLineSelection = otherHandleOffset != -1 && lineNumber == getLineForOffset(layout, offset);
            if (sameLineSelection) {
                if ((offset < otherHandleOffset) != (getHorizontal(Editor.this.mTextView.getLayout(), offset) < getHorizontal(Editor.this.mTextView.getLayout(), otherHandleOffset))) {
                    rtl = true;
                    int[] textViewLocationOnScreen = new int[2];
                    Editor.this.mTextView.getLocationOnScreen(textViewLocationOnScreen);
                    float touchXInView = event.getRawX() - textViewLocationOnScreen[0];
                    if (Editor.this.mNewMagnifierEnabled) {
                        float leftBound3 = Editor.this.mTextView.getTotalPaddingLeft() - Editor.this.mTextView.getScrollX();
                        float rightBound3 = Editor.this.mTextView.getTotalPaddingLeft() - Editor.this.mTextView.getScrollX();
                        if (sameLineSelection) {
                            if ((trigger == 2) ^ rtl) {
                                leftBound = leftBound3 + getHorizontal(Editor.this.mTextView.getLayout(), otherHandleOffset);
                                if (sameLineSelection) {
                                    if ((trigger == 1) ^ rtl) {
                                        rightBound = rightBound3 + getHorizontal(Editor.this.mTextView.getLayout(), otherHandleOffset);
                                        float f = this.mTextViewScaleX;
                                        leftBound2 = leftBound * f;
                                        rightBound2 = rightBound * f;
                                        contentWidth = Math.round(Editor.this.mMagnifierAnimator.mMagnifier.getWidth() / Editor.this.mMagnifierAnimator.mMagnifier.getZoom());
                                        if (touchXInView >= leftBound2 - (contentWidth / 2.0f) && touchXInView <= rightBound2 + (contentWidth / 2.0f)) {
                                            contentWidth2 = leftBound2;
                                        }
                                        return false;
                                    }
                                }
                                rightBound = rightBound3 + Editor.this.mTextView.getLayout().getLineRight(lineNumber);
                                float f2 = this.mTextViewScaleX;
                                leftBound2 = leftBound * f2;
                                rightBound2 = rightBound * f2;
                                contentWidth = Math.round(Editor.this.mMagnifierAnimator.mMagnifier.getWidth() / Editor.this.mMagnifierAnimator.mMagnifier.getZoom());
                                if (touchXInView >= leftBound2 - (contentWidth / 2.0f)) {
                                    return false;
                                }
                                contentWidth2 = leftBound2;
                            }
                        }
                        leftBound = leftBound3 + Editor.this.mTextView.getLayout().getLineLeft(lineNumber);
                        if (sameLineSelection) {
                        }
                        rightBound = rightBound3 + Editor.this.mTextView.getLayout().getLineRight(lineNumber);
                        float f22 = this.mTextViewScaleX;
                        leftBound2 = leftBound * f22;
                        rightBound2 = rightBound * f22;
                        contentWidth = Math.round(Editor.this.mMagnifierAnimator.mMagnifier.getWidth() / Editor.this.mMagnifierAnimator.mMagnifier.getZoom());
                        if (touchXInView >= leftBound2 - (contentWidth / 2.0f)) {
                        }
                    } else {
                        contentWidth2 = 0.0f;
                        rightBound2 = Editor.this.mTextView.getWidth();
                        if (touchXInView < 0.0f || touchXInView > rightBound2) {
                            return false;
                        }
                    }
                    if (this.mTextViewScaleX != 1.0f) {
                        scaledTouchXInView = touchXInView;
                    } else {
                        float scaledTouchXInView2 = event.getRawX();
                        float f3 = this.mCurrentDragInitialTouchRawX;
                        scaledTouchXInView = (((scaledTouchXInView2 - f3) * this.mTextViewScaleX) + f3) - textViewLocationOnScreen[0];
                    }
                    showPosInView.f78x = Math.max(contentWidth2, Math.min(rightBound2, scaledTouchXInView));
                    showPosInView.f79y = ((((Editor.this.mTextView.getLayout().getLineTop(lineNumber) + Editor.this.mTextView.getLayout().getLineBottom(lineNumber, false)) / 2.0f) + Editor.this.mTextView.getTotalPaddingTop()) - Editor.this.mTextView.getScrollY()) * this.mTextViewScaleY;
                    return true;
                }
            }
            rtl = false;
            int[] textViewLocationOnScreen2 = new int[2];
            Editor.this.mTextView.getLocationOnScreen(textViewLocationOnScreen2);
            float touchXInView2 = event.getRawX() - textViewLocationOnScreen2[0];
            if (Editor.this.mNewMagnifierEnabled) {
            }
            if (this.mTextViewScaleX != 1.0f) {
            }
            showPosInView.f78x = Math.max(contentWidth2, Math.min(rightBound2, scaledTouchXInView));
            showPosInView.f79y = ((((Editor.this.mTextView.getLayout().getLineTop(lineNumber) + Editor.this.mTextView.getLayout().getLineBottom(lineNumber, false)) / 2.0f) + Editor.this.mTextView.getTotalPaddingTop()) - Editor.this.mTextView.getScrollY()) * this.mTextViewScaleY;
            return true;
        }

        private boolean handleOverlapsMagnifier(HandleView handle, Rect magnifierRect) {
            PopupWindow window = handle.mContainer;
            if (!window.hasDecorView()) {
                return false;
            }
            Rect handleRect = new Rect(window.getDecorViewLayoutParams().f504x, window.getDecorViewLayoutParams().f505y, window.getDecorViewLayoutParams().f504x + window.getContentView().getWidth(), window.getDecorViewLayoutParams().f505y + window.getContentView().getHeight());
            return Rect.intersects(handleRect, magnifierRect);
        }

        private HandleView getOtherSelectionHandle() {
            SelectionModifierCursorController controller = Editor.this.getSelectionController();
            if (controller == null || !controller.isActive()) {
                return null;
            }
            if (controller.mStartHandle != this) {
                return controller.mStartHandle;
            }
            return controller.mEndHandle;
        }

        private void updateHandlesVisibility() {
            Point magnifierTopLeft = Editor.this.mMagnifierAnimator.mMagnifier.getPosition();
            if (magnifierTopLeft == null) {
                return;
            }
            Rect magnifierRect = new Rect(magnifierTopLeft.f76x, magnifierTopLeft.f77y, magnifierTopLeft.f76x + Editor.this.mMagnifierAnimator.mMagnifier.getWidth(), magnifierTopLeft.f77y + Editor.this.mMagnifierAnimator.mMagnifier.getHeight());
            setVisible((handleOverlapsMagnifier(this, magnifierRect) || Editor.this.mDrawCursorOnMagnifier) ? false : true);
            HandleView otherHandle = getOtherSelectionHandle();
            if (otherHandle != null) {
                otherHandle.setVisible(true ^ handleOverlapsMagnifier(otherHandle, magnifierRect));
            }
        }

        protected final void updateMagnifier(MotionEvent event) {
            if (Editor.this.getMagnifierAnimator() == null) {
                return;
            }
            PointF showPosInView = new PointF();
            boolean shouldShow = checkForTransforms() && !tooLargeTextForMagnifier() && obtainMagnifierShowCoordinates(event, showPosInView) && Editor.this.mTextView.showUIForTouchScreen();
            if (shouldShow) {
                Editor.this.mRenderCursorRegardlessTiming = true;
                Editor.this.mTextView.invalidateCursorPath();
                Editor.this.suspendBlink();
                if (Editor.this.mNewMagnifierEnabled) {
                    Layout layout = Editor.this.mTextView.getLayout();
                    int line = getLineForOffset(layout, getCurrentCursorOffset());
                    int lineLeft = (int) layout.getLineLeft(line);
                    int lineLeft2 = lineLeft + (Editor.this.mTextView.getTotalPaddingLeft() - Editor.this.mTextView.getScrollX());
                    int lineRight = (int) layout.getLineRight(line);
                    int lineRight2 = lineRight + (Editor.this.mTextView.getTotalPaddingLeft() - Editor.this.mTextView.getScrollX());
                    Editor.this.mDrawCursorOnMagnifier = showPosInView.f78x < ((float) (lineLeft2 + (-20))) || showPosInView.f78x > ((float) (lineRight2 + 20));
                    Editor.this.mMagnifierAnimator.mMagnifier.setDrawCursor(Editor.this.mDrawCursorOnMagnifier, Editor.this.mDrawableForCursor);
                    boolean cursorVisible = Editor.this.mCursorVisible;
                    Editor editor = Editor.this;
                    editor.mCursorVisible = true ^ editor.mDrawCursorOnMagnifier;
                    if (Editor.this.mCursorVisible && !cursorVisible) {
                        Editor.this.updateCursorPosition();
                    }
                    int lineHeight = layout.getLineBottom(line, false) - layout.getLineTop(line);
                    float zoom = Editor.this.mInitialZoom;
                    if (lineHeight < Editor.this.mMinLineHeightForMagnifier) {
                        zoom = (Editor.this.mMinLineHeightForMagnifier * zoom) / lineHeight;
                    }
                    Editor.this.mMagnifierAnimator.mMagnifier.updateSourceFactors(lineHeight, zoom);
                    Editor.this.mMagnifierAnimator.mMagnifier.show(showPosInView.f78x, showPosInView.f79y);
                } else {
                    Editor.this.mMagnifierAnimator.show(showPosInView.f78x, showPosInView.f79y);
                }
                updateHandlesVisibility();
                return;
            }
            dismissMagnifier();
        }

        protected final void dismissMagnifier() {
            if (Editor.this.mMagnifierAnimator != null) {
                Editor.this.mMagnifierAnimator.dismiss();
                Editor.this.mRenderCursorRegardlessTiming = false;
                Editor.this.mDrawCursorOnMagnifier = false;
                if (!Editor.this.mCursorVisible) {
                    Editor.this.mCursorVisible = true;
                    Editor.this.mTextView.invalidate();
                }
                Editor.this.resumeBlink();
                setVisible(true);
                HandleView otherHandle = getOtherSelectionHandle();
                if (otherHandle != null) {
                    otherHandle.setVisible(true);
                }
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent ev) {
            float newVerticalOffset;
            Editor.this.updateFloatingToolbarVisibility(ev);
            switch (ev.getActionMasked()) {
                case 0:
                    startTouchUpFilter(getCurrentCursorOffset());
                    PositionListener positionListener = Editor.this.getPositionListener();
                    this.mLastParentX = positionListener.getPositionX();
                    this.mLastParentY = positionListener.getPositionY();
                    this.mLastParentXOnScreen = positionListener.getPositionXOnScreen();
                    this.mLastParentYOnScreen = positionListener.getPositionYOnScreen();
                    float xInWindow = (ev.getRawX() - this.mLastParentXOnScreen) + this.mLastParentX;
                    float yInWindow = (ev.getRawY() - this.mLastParentYOnScreen) + this.mLastParentY;
                    this.mTouchToWindowOffsetX = xInWindow - this.mPositionX;
                    this.mTouchToWindowOffsetY = yInWindow - this.mPositionY;
                    this.mIsDragging = true;
                    this.mPreviousLineTouched = -1;
                    break;
                case 1:
                    filterOnTouchUp(ev.isFromSource(4098));
                    this.mIsDragging = false;
                    updateDrawable(false);
                    break;
                case 2:
                    float xInWindow2 = (ev.getRawX() - this.mLastParentXOnScreen) + this.mLastParentX;
                    float rawY = ev.getRawY() - this.mLastParentYOnScreen;
                    int i = this.mLastParentY;
                    float yInWindow2 = rawY + i;
                    float previousVerticalOffset = this.mTouchToWindowOffsetY - i;
                    float currentVerticalOffset = (yInWindow2 - this.mPositionY) - i;
                    float newVerticalOffset2 = this.mIdealVerticalOffset;
                    if (previousVerticalOffset < newVerticalOffset2) {
                        newVerticalOffset = Math.max(Math.min(currentVerticalOffset, newVerticalOffset2), previousVerticalOffset);
                    } else {
                        newVerticalOffset = Math.min(Math.max(currentVerticalOffset, newVerticalOffset2), previousVerticalOffset);
                    }
                    this.mTouchToWindowOffsetY = this.mLastParentY + newVerticalOffset;
                    float newPosX = (xInWindow2 - this.mTouchToWindowOffsetX) + this.mHotspotX + getHorizontalOffset();
                    float newPosY = (yInWindow2 - this.mTouchToWindowOffsetY) + this.mTouchOffsetY;
                    updatePosition(newPosX, newPosY, ev.isFromSource(4098));
                    break;
                case 3:
                    this.mIsDragging = false;
                    updateDrawable(false);
                    break;
            }
            return true;
        }

        public boolean isDragging() {
            return this.mIsDragging;
        }

        void onHandleMoved() {
        }

        public void onDetached() {
            dismissMagnifier();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            setSystemGestureExclusionRects(Collections.singletonList(new Rect(0, 0, w, h)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class InsertionHandleView extends HandleView {
        private final int mDeltaHeight;
        private final int mDrawableOpacity;
        private Runnable mHider;
        private boolean mIsInActionMode;
        private boolean mIsTouchDown;
        private float mLastDownRawX;
        private float mLastDownRawY;
        private long mLastUpTime;
        private boolean mOffsetChanged;
        private int mOffsetDown;
        private boolean mPendingDismissOnUp;
        private float mTouchDownX;
        private float mTouchDownY;

        InsertionHandleView(Drawable drawable) {
            super(drawable, drawable, C4057R.C4059id.insertion_handle);
            this.mIsTouchDown = false;
            this.mPendingDismissOnUp = false;
            int opacity = 0;
            int opacity2 = 255;
            if (Editor.this.mFlagInsertionHandleGesturesEnabled) {
                int deltaHeight = AppGlobals.getIntCoreSetting(WidgetFlags.KEY_INSERTION_HANDLE_DELTA_HEIGHT, 25);
                int opacity3 = AppGlobals.getIntCoreSetting(WidgetFlags.KEY_INSERTION_HANDLE_OPACITY, 50);
                deltaHeight = (deltaHeight < -25 || deltaHeight > 50) ? 25 : 25;
                opacity3 = (opacity3 < 10 || opacity3 > 100) ? 50 : 50;
                opacity2 = (opacity3 * 255) / 100;
                opacity = deltaHeight;
            }
            this.mDeltaHeight = opacity;
            this.mDrawableOpacity = opacity2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void hideAfterDelay() {
            if (this.mHider == null) {
                this.mHider = new Runnable() { // from class: android.widget.Editor.InsertionHandleView.1
                    @Override // java.lang.Runnable
                    public void run() {
                        InsertionHandleView.this.hide();
                    }
                };
            } else {
                removeHiderCallback();
            }
            Editor.this.mTextView.postDelayed(this.mHider, 4000L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeHiderCallback() {
            if (this.mHider != null) {
                Editor.this.mTextView.removeCallbacks(this.mHider);
            }
        }

        @Override // android.widget.Editor.HandleView
        protected int getHotspotX(Drawable drawable, boolean isRtlRun) {
            return drawable.getIntrinsicWidth() / 2;
        }

        @Override // android.widget.Editor.HandleView
        protected int getHorizontalGravity(boolean isRtlRun) {
            return 1;
        }

        @Override // android.widget.Editor.HandleView
        protected int getCursorOffset() {
            int offset = super.getCursorOffset();
            if (Editor.this.mDrawableForCursor != null) {
                Editor.this.mDrawableForCursor.getPadding(Editor.this.mTempRect);
                return offset + (((Editor.this.mDrawableForCursor.getIntrinsicWidth() - Editor.this.mTempRect.left) - Editor.this.mTempRect.right) / 2);
            }
            return offset;
        }

        @Override // android.widget.Editor.HandleView
        int getCursorHorizontalPosition(Layout layout, int offset) {
            if (Editor.this.mDrawableForCursor != null) {
                float horizontal = getHorizontal(layout, offset);
                Editor editor = Editor.this;
                return editor.clampHorizontalPosition(editor.mDrawableForCursor, horizontal) + Editor.this.mTempRect.left;
            }
            return super.getCursorHorizontalPosition(layout, offset);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.Editor.HandleView, android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (Editor.this.mFlagInsertionHandleGesturesEnabled) {
                int height = Math.max(getPreferredHeight() + this.mDeltaHeight, this.mDrawable.getIntrinsicHeight());
                setMeasuredDimension(getPreferredWidth(), height);
                return;
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.widget.Editor.HandleView, android.view.View
        public boolean onTouchEvent(MotionEvent ev) {
            if (Editor.this.mTextView.isFromPrimePointer(ev, true)) {
                if (Editor.this.mFlagInsertionHandleGesturesEnabled && Editor.this.mFlagCursorDragFromAnywhereEnabled) {
                    return touchThrough(ev);
                }
                boolean result = super.onTouchEvent(ev);
                switch (ev.getActionMasked()) {
                    case 0:
                        this.mLastDownRawX = ev.getRawX();
                        this.mLastDownRawY = ev.getRawY();
                        updateMagnifier(ev);
                        break;
                    case 1:
                        if (!offsetHasBeenChanged()) {
                            ViewConfiguration config = ViewConfiguration.get(Editor.this.mTextView.getContext());
                            boolean isWithinTouchSlop = EditorTouchState.isDistanceWithin(this.mLastDownRawX, this.mLastDownRawY, ev.getRawX(), ev.getRawY(), config.getScaledTouchSlop());
                            if (isWithinTouchSlop) {
                                Editor.this.toggleInsertionActionMode();
                            }
                        } else if (Editor.this.mTextActionMode != null) {
                            Editor.this.mTextActionMode.invalidateContentRect();
                        }
                        hideAfterDelay();
                        dismissMagnifier();
                        break;
                    case 2:
                        updateMagnifier(ev);
                        break;
                    case 3:
                        hideAfterDelay();
                        dismissMagnifier();
                        break;
                }
                return result;
            }
            return true;
        }

        private boolean touchThrough(MotionEvent ev) {
            int actionType = ev.getActionMasked();
            switch (actionType) {
                case 0:
                    this.mIsTouchDown = true;
                    this.mOffsetChanged = false;
                    this.mOffsetDown = Editor.this.mTextView.getSelectionStart();
                    this.mTouchDownX = ev.getX();
                    this.mTouchDownY = ev.getY();
                    this.mIsInActionMode = Editor.this.mTextActionMode != null;
                    if (ev.getEventTime() - this.mLastUpTime < ViewConfiguration.getDoubleTapTimeout()) {
                        Editor.this.lambda$startActionModeInternal$0();
                    }
                    Editor.this.mTouchState.setIsOnHandle(true);
                    break;
                case 1:
                    this.mLastUpTime = ev.getEventTime();
                    break;
            }
            boolean ret = Editor.this.mTextView.onTouchEvent(transformEventForTouchThrough(ev));
            if (actionType == 1 || actionType == 3) {
                this.mIsTouchDown = false;
                if (this.mPendingDismissOnUp) {
                    dismiss();
                }
                Editor.this.mTouchState.setIsOnHandle(false);
            }
            if (!this.mOffsetChanged) {
                int start = Editor.this.mTextView.getSelectionStart();
                int end = Editor.this.mTextView.getSelectionEnd();
                if (start != end || this.mOffsetDown != start) {
                    this.mOffsetChanged = true;
                }
            }
            if (!this.mOffsetChanged && actionType == 1) {
                if (this.mIsInActionMode) {
                    Editor.this.lambda$startActionModeInternal$0();
                } else {
                    Editor.this.startInsertionActionMode();
                }
            }
            return ret;
        }

        private MotionEvent transformEventForTouchThrough(MotionEvent ev) {
            Layout layout = Editor.this.mTextView.getLayout();
            int line = getLineForOffset(layout, getCurrentCursorOffset());
            int textHeight = layout.getLineBottom(line, false) - layout.getLineTop(line);
            Matrix m = new Matrix();
            m.setTranslate(((ev.getRawX() - ev.getX()) + (getMeasuredWidth() >> 1)) - this.mTouchDownX, ((ev.getRawY() - ev.getY()) - (textHeight >> 1)) - this.mTouchDownY);
            ev.transform(m);
            Editor.this.mTextView.toLocalMotionEvent(ev);
            return ev;
        }

        @Override // android.widget.Editor.HandleView
        public boolean isShowing() {
            if (this.mPendingDismissOnUp) {
                return false;
            }
            return super.isShowing();
        }

        @Override // android.widget.Editor.HandleView
        public void show() {
            super.show();
            this.mPendingDismissOnUp = false;
            this.mDrawable.setAlpha(this.mDrawableOpacity);
        }

        @Override // android.widget.Editor.HandleView
        public void dismiss() {
            if (this.mIsTouchDown) {
                this.mPendingDismissOnUp = true;
                this.mDrawable.setAlpha(0);
                return;
            }
            super.dismiss();
            this.mPendingDismissOnUp = false;
        }

        @Override // android.widget.Editor.HandleView
        protected void updateDrawable(boolean updateDrawableWhenDragging) {
            super.updateDrawable(updateDrawableWhenDragging);
            this.mDrawable.setAlpha(this.mDrawableOpacity);
        }

        @Override // android.widget.Editor.HandleView
        public int getCurrentCursorOffset() {
            return Editor.this.mTextView.getSelectionStart();
        }

        @Override // android.widget.Editor.HandleView
        public void updateSelection(int offset) {
            Selection.setSelection((Spannable) Editor.this.mTextView.getText(), offset);
        }

        @Override // android.widget.Editor.HandleView
        protected void updatePosition(float x, float y, boolean fromTouchScreen) {
            int offset;
            Layout layout = Editor.this.mTextView.getLayout();
            if (layout != null) {
                if (this.mPreviousLineTouched == -1) {
                    this.mPreviousLineTouched = Editor.this.mTextView.getLineAtCoordinate(y);
                }
                int currLine = Editor.this.getCurrentLineAdjustedForSlop(layout, this.mPreviousLineTouched, y);
                offset = getOffsetAtCoordinate(layout, currLine, x);
                this.mPreviousLineTouched = currLine;
            } else {
                offset = -1;
            }
            positionAtCursorOffset(offset, false, fromTouchScreen);
            if (Editor.this.mTextActionMode != null) {
                Editor.this.invalidateActionMode();
            }
        }

        @Override // android.widget.Editor.HandleView
        void onHandleMoved() {
            super.onHandleMoved();
            removeHiderCallback();
        }

        @Override // android.widget.Editor.HandleView
        public void onDetached() {
            super.onDetached();
            removeHiderCallback();
        }

        @Override // android.widget.Editor.HandleView
        protected int getMagnifierHandleTrigger() {
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public final class SelectionHandleView extends HandleView {
        private final int mHandleType;
        private boolean mInWord;
        private boolean mLanguageDirectionChanged;
        private float mPrevX;
        private final float mTextViewEdgeSlop;
        private final int[] mTextViewLocation;
        private float mTouchWordDelta;

        public SelectionHandleView(Drawable drawableLtr, Drawable drawableRtl, int id, int handleType) {
            super(drawableLtr, drawableRtl, id);
            this.mInWord = false;
            this.mLanguageDirectionChanged = false;
            this.mTextViewLocation = new int[2];
            this.mHandleType = handleType;
            ViewConfiguration viewConfiguration = ViewConfiguration.get(Editor.this.mTextView.getContext());
            this.mTextViewEdgeSlop = viewConfiguration.getScaledTouchSlop() * 4;
        }

        private boolean isStartHandle() {
            return this.mHandleType == 0;
        }

        @Override // android.widget.Editor.HandleView
        protected int getHotspotX(Drawable drawable, boolean isRtlRun) {
            if (isRtlRun == isStartHandle()) {
                return drawable.getIntrinsicWidth() / 4;
            }
            return (drawable.getIntrinsicWidth() * 3) / 4;
        }

        @Override // android.widget.Editor.HandleView
        protected int getHorizontalGravity(boolean isRtlRun) {
            return isRtlRun == isStartHandle() ? 3 : 5;
        }

        @Override // android.widget.Editor.HandleView
        public int getCurrentCursorOffset() {
            return isStartHandle() ? Editor.this.mTextView.getSelectionStart() : Editor.this.mTextView.getSelectionEnd();
        }

        @Override // android.widget.Editor.HandleView
        protected void updateSelection(int offset) {
            if (isStartHandle()) {
                Selection.setSelection((Spannable) Editor.this.mTextView.getText(), offset, Editor.this.mTextView.getSelectionEnd());
            } else {
                Selection.setSelection((Spannable) Editor.this.mTextView.getText(), Editor.this.mTextView.getSelectionStart(), offset);
            }
            updateDrawable(false);
            if (Editor.this.mTextActionMode != null) {
                Editor.this.invalidateActionMode();
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:129:0x01da, code lost:
            if (r8 < r23.mPrevLine) goto L114;
         */
        /* JADX WARN: Code restructure failed: missing block: B:76:0x013d, code lost:
            if (r23.this$0.mTextView.canScrollHorizontally(r15 ? -1 : 1) != false) goto L52;
         */
        @Override // android.widget.Editor.HandleView
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        protected void updatePosition(float x, float y, boolean fromTouchScreen) {
            boolean isExpanding;
            boolean isExpanding2;
            boolean z;
            boolean positionCursor;
            int offsetThresholdToSnap;
            int wordBoundary;
            int nextOffset;
            Layout layout = Editor.this.mTextView.getLayout();
            if (layout == null) {
                positionAndAdjustForCrossingHandles(Editor.this.mTextView.getOffsetForPosition(x, y), fromTouchScreen);
                return;
            }
            if (this.mPreviousLineTouched == -1) {
                this.mPreviousLineTouched = Editor.this.mTextView.getLineAtCoordinate(y);
            }
            int anotherHandleOffset = isStartHandle() ? Editor.this.mTextView.getSelectionEnd() : Editor.this.mTextView.getSelectionStart();
            int currLine = Editor.this.getCurrentLineAdjustedForSlop(layout, this.mPreviousLineTouched, y);
            int initialOffset = getOffsetAtCoordinate(layout, currLine, x);
            if ((isStartHandle() && initialOffset >= anotherHandleOffset) || (!isStartHandle() && initialOffset <= anotherHandleOffset)) {
                currLine = getLineForOffset(layout, anotherHandleOffset);
                initialOffset = getOffsetAtCoordinate(layout, currLine, x);
            }
            int offset = initialOffset;
            int wordEnd = Editor.this.getWordEnd(offset);
            int wordStart = Editor.this.getWordStart(offset);
            if (this.mPrevX == -1.0f) {
                this.mPrevX = x;
            }
            int currentOffset = getCurrentCursorOffset();
            boolean rtlAtCurrentOffset = isAtRtlRun(layout, currentOffset);
            boolean atRtl = isAtRtlRun(layout, offset);
            boolean isLvlBoundary = layout.isLevelBoundary(Editor.this.mTextView.originalToTransformed(offset, 1));
            if (!isLvlBoundary) {
                if ((!rtlAtCurrentOffset || atRtl) && (rtlAtCurrentOffset || !atRtl)) {
                    if (this.mLanguageDirectionChanged) {
                        positionAndAdjustForCrossingHandles(offset, fromTouchScreen);
                        this.mTouchWordDelta = 0.0f;
                        this.mLanguageDirectionChanged = false;
                        return;
                    }
                    float xDiff = x - this.mPrevX;
                    if (isStartHandle()) {
                        isExpanding = currLine < this.mPreviousLineTouched;
                    } else {
                        isExpanding = currLine > this.mPreviousLineTouched;
                    }
                    boolean isLvlBoundary2 = isStartHandle();
                    if (atRtl == isLvlBoundary2) {
                        isExpanding2 = isExpanding | (xDiff > 0.0f);
                    } else {
                        isExpanding2 = isExpanding | (xDiff < 0.0f);
                    }
                    if (Editor.this.mTextView.getHorizontallyScrolling() && positionNearEdgeOfScrollingView(x, atRtl)) {
                        if (!isStartHandle() || Editor.this.mTextView.getScrollX() == 0) {
                            if (!isStartHandle()) {
                            }
                        }
                        if ((isExpanding2 && ((isStartHandle() && offset < currentOffset) || (!isStartHandle() && offset > currentOffset))) || !isExpanding2) {
                            this.mTouchWordDelta = 0.0f;
                            if (atRtl == isStartHandle()) {
                                nextOffset = layout.getOffsetToRightOf(this.mPreviousOffset);
                            } else {
                                nextOffset = layout.getOffsetToLeftOf(this.mPreviousOffset);
                            }
                            positionAndAdjustForCrossingHandles(nextOffset, fromTouchScreen);
                            return;
                        }
                    }
                    if (isExpanding2) {
                        int wordBoundary2 = isStartHandle() ? wordStart : wordEnd;
                        boolean snapToWord = (!this.mInWord || (!isStartHandle() ? currLine <= this.mPrevLine : currLine >= this.mPrevLine)) && atRtl == isAtRtlRun(layout, wordBoundary2);
                        if (snapToWord) {
                            if (getLineForOffset(layout, wordBoundary2) != currLine) {
                                wordBoundary2 = isStartHandle() ? layout.getLineStart(currLine) : layout.getLineEnd(currLine);
                            }
                            if (isStartHandle()) {
                                offsetThresholdToSnap = wordEnd - ((wordEnd - wordBoundary2) / 2);
                            } else {
                                offsetThresholdToSnap = ((wordBoundary2 - wordStart) / 2) + wordStart;
                            }
                            if (!isStartHandle()) {
                                wordBoundary = wordBoundary2;
                            } else {
                                if (offset > offsetThresholdToSnap) {
                                    wordBoundary = wordBoundary2;
                                } else {
                                    wordBoundary = wordBoundary2;
                                }
                                offset = wordStart;
                                wordBoundary2 = wordBoundary;
                            }
                            if (!isStartHandle() && (offset >= offsetThresholdToSnap || currLine > this.mPrevLine)) {
                                offset = wordEnd;
                                wordBoundary2 = wordBoundary;
                            } else {
                                offset = this.mPreviousOffset;
                                wordBoundary2 = wordBoundary;
                            }
                        }
                        boolean isExpanding3 = isStartHandle();
                        if ((!isExpanding3 || offset >= initialOffset) && (isStartHandle() || offset <= initialOffset)) {
                            this.mTouchWordDelta = 0.0f;
                        } else {
                            float adjustedX = getHorizontal(layout, offset);
                            this.mTouchWordDelta = Editor.this.mTextView.convertToLocalHorizontalCoordinate(x) - adjustedX;
                        }
                        positionCursor = true;
                    } else {
                        int adjustedOffset = getOffsetAtCoordinate(layout, currLine, x - this.mTouchWordDelta);
                        if (isStartHandle()) {
                            z = adjustedOffset > this.mPreviousOffset || currLine > this.mPrevLine;
                        } else {
                            z = adjustedOffset < this.mPreviousOffset || currLine < this.mPrevLine;
                        }
                        boolean shrinking = z;
                        if (!shrinking) {
                            boolean shrinking2 = isStartHandle();
                            if ((shrinking2 && adjustedOffset < this.mPreviousOffset) || (!isStartHandle() && adjustedOffset > this.mPreviousOffset)) {
                                this.mTouchWordDelta = Editor.this.mTextView.convertToLocalHorizontalCoordinate(x) - getHorizontal(layout, this.mPreviousOffset);
                            }
                            positionCursor = false;
                        } else {
                            if (currLine != this.mPrevLine) {
                                int offset2 = isStartHandle() ? wordStart : wordEnd;
                                if ((!isStartHandle() || offset2 >= initialOffset) && (isStartHandle() || offset2 <= initialOffset)) {
                                    this.mTouchWordDelta = 0.0f;
                                } else {
                                    float adjustedX2 = getHorizontal(layout, offset2);
                                    this.mTouchWordDelta = Editor.this.mTextView.convertToLocalHorizontalCoordinate(x) - adjustedX2;
                                }
                                offset = offset2;
                            } else {
                                offset = adjustedOffset;
                            }
                            positionCursor = true;
                        }
                    }
                    if (positionCursor) {
                        this.mPreviousLineTouched = currLine;
                        positionAndAdjustForCrossingHandles(offset, fromTouchScreen);
                    }
                    this.mPrevX = x;
                    return;
                }
            }
            this.mLanguageDirectionChanged = true;
            this.mTouchWordDelta = 0.0f;
            positionAndAdjustForCrossingHandles(offset, fromTouchScreen);
        }

        @Override // android.widget.Editor.HandleView
        protected void positionAtCursorOffset(int offset, boolean forceUpdatePosition, boolean fromTouchScreen) {
            super.positionAtCursorOffset(offset, forceUpdatePosition, fromTouchScreen);
            this.mInWord = (offset == -1 || Editor.this.getWordIteratorWithText().isBoundary(offset)) ? false : true;
        }

        @Override // android.widget.Editor.HandleView, android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (Editor.this.mTextView.isFromPrimePointer(event, true)) {
                boolean superResult = super.onTouchEvent(event);
                switch (event.getActionMasked()) {
                    case 0:
                        this.mTouchWordDelta = 0.0f;
                        this.mPrevX = -1.0f;
                        updateMagnifier(event);
                        break;
                    case 1:
                    case 3:
                        dismissMagnifier();
                        break;
                    case 2:
                        updateMagnifier(event);
                        break;
                }
                return superResult;
            }
            return true;
        }

        private void positionAndAdjustForCrossingHandles(int offset, boolean fromTouchScreen) {
            int offset2;
            int anotherHandleOffset = isStartHandle() ? Editor.this.mTextView.getSelectionEnd() : Editor.this.mTextView.getSelectionStart();
            if ((isStartHandle() && offset >= anotherHandleOffset) || (!isStartHandle() && offset <= anotherHandleOffset)) {
                this.mTouchWordDelta = 0.0f;
                Layout layout = Editor.this.mTextView.getLayout();
                if (layout != null && offset != anotherHandleOffset) {
                    float horiz = getHorizontal(layout, offset);
                    float anotherHandleHoriz = getHorizontal(layout, anotherHandleOffset, !isStartHandle());
                    float currentHoriz = getHorizontal(layout, this.mPreviousOffset);
                    if ((currentHoriz < anotherHandleHoriz && horiz < anotherHandleHoriz) || (currentHoriz > anotherHandleHoriz && horiz > anotherHandleHoriz)) {
                        int currentOffset = getCurrentCursorOffset();
                        int offsetToGetRunRange = isStartHandle() ? currentOffset : Math.max(currentOffset - 1, 0);
                        long range = layout.getRunRange(Editor.this.mTextView.originalToTransformed(offsetToGetRunRange, 1));
                        if (isStartHandle()) {
                            offset2 = TextUtils.unpackRangeStartFromLong(range);
                        } else {
                            offset2 = TextUtils.unpackRangeEndFromLong(range);
                        }
                        positionAtCursorOffset(Editor.this.mTextView.transformedToOriginal(offset2, 1), false, fromTouchScreen);
                        return;
                    }
                }
                offset = Editor.this.getNextCursorOffset(anotherHandleOffset, true ^ isStartHandle());
            }
            positionAtCursorOffset(offset, false, fromTouchScreen);
        }

        private boolean positionNearEdgeOfScrollingView(float x, boolean atRtl) {
            Editor.this.mTextView.getLocationOnScreen(this.mTextViewLocation);
            if (atRtl == isStartHandle()) {
                int rightEdge = (this.mTextViewLocation[0] + Editor.this.mTextView.getWidth()) - Editor.this.mTextView.getPaddingRight();
                boolean nearEdge = x > ((float) rightEdge) - this.mTextViewEdgeSlop;
                return nearEdge;
            }
            int leftEdge = this.mTextViewLocation[0] + Editor.this.mTextView.getPaddingLeft();
            boolean nearEdge2 = x < ((float) leftEdge) + this.mTextViewEdgeSlop;
            return nearEdge2;
        }

        @Override // android.widget.Editor.HandleView
        protected boolean isAtRtlRun(Layout layout, int offset) {
            int transformedOffset = Editor.this.mTextView.transformedToOriginal(offset, 0);
            int offsetToCheck = isStartHandle() ? transformedOffset : Math.max(transformedOffset - 1, 0);
            return layout.isRtlCharAt(offsetToCheck);
        }

        @Override // android.widget.Editor.HandleView
        public float getHorizontal(Layout layout, int offset) {
            return getHorizontal(layout, offset, isStartHandle());
        }

        private float getHorizontal(Layout layout, int offset, boolean startHandle) {
            int offsetTransformed = Editor.this.mTextView.originalToTransformed(offset, 1);
            int line = layout.getLineForOffset(offsetTransformed);
            int offsetToCheck = startHandle ? offsetTransformed : Math.max(offsetTransformed - 1, 0);
            boolean isRtlChar = layout.isRtlCharAt(offsetToCheck);
            boolean isRtlParagraph = layout.getParagraphDirection(line) == -1;
            if (isRtlChar != isRtlParagraph) {
                return layout.getSecondaryHorizontal(offsetTransformed);
            }
            return layout.getPrimaryHorizontal(offsetTransformed);
        }

        @Override // android.widget.Editor.HandleView
        protected int getOffsetAtCoordinate(Layout layout, int line, float x) {
            int offsetToCheck;
            int offset;
            float localX = Editor.this.mTextView.convertToLocalHorizontalCoordinate(x);
            int primaryOffset = layout.getOffsetForHorizontal(line, localX, true);
            if (!layout.isLevelBoundary(primaryOffset)) {
                return Editor.this.mTextView.transformedToOriginal(primaryOffset, 1);
            }
            boolean isRtlParagraph = false;
            int secondaryOffset = layout.getOffsetForHorizontal(line, localX, false);
            int currentOffset = Editor.this.mTextView.originalToTransformed(getCurrentCursorOffset(), 1);
            int primaryDiff = Math.abs(primaryOffset - currentOffset);
            int secondaryDiff = Math.abs(secondaryOffset - currentOffset);
            if (primaryDiff < secondaryDiff) {
                offset = primaryOffset;
            } else if (primaryDiff > secondaryDiff) {
                offset = secondaryOffset;
            } else {
                if (!isStartHandle()) {
                    offsetToCheck = Math.max(currentOffset - 1, 0);
                } else {
                    offsetToCheck = currentOffset;
                }
                boolean isRtlChar = layout.isRtlCharAt(offsetToCheck);
                if (layout.getParagraphDirection(line) == -1) {
                    isRtlParagraph = true;
                }
                offset = isRtlChar == isRtlParagraph ? primaryOffset : secondaryOffset;
            }
            return Editor.this.mTextView.transformedToOriginal(offset, 1);
        }

        @Override // android.widget.Editor.HandleView
        protected int getMagnifierHandleTrigger() {
            if (isStartHandle()) {
                return 1;
            }
            return 2;
        }
    }

    public void setLineChangeSlopMinMaxForTesting(int min, int max) {
        this.mLineChangeSlopMin = min;
        this.mLineChangeSlopMax = max;
    }

    public int getCurrentLineAdjustedForSlop(Layout layout, int prevLine, float y) {
        int trueLine = this.mTextView.getLineAtCoordinate(y);
        if (layout == null || prevLine > layout.getLineCount() || layout.getLineCount() <= 0 || prevLine < 0) {
            return trueLine;
        }
        if (Math.abs(trueLine - prevLine) >= 2) {
            return trueLine;
        }
        int lineHeight = this.mTextView.getLineHeight();
        int slop = Math.max(0, Math.max(this.mLineChangeSlopMin, Math.min(this.mLineChangeSlopMax, lineHeight + ((int) (this.mLineSlopRatio * lineHeight)))) - lineHeight);
        float verticalOffset = this.mTextView.viewportToContentVerticalOffset();
        if (trueLine > prevLine && y >= layout.getLineBottom(prevLine) + slop + verticalOffset) {
            return trueLine;
        }
        if (trueLine < prevLine && y <= (layout.getLineTop(prevLine) - slop) + verticalOffset) {
            return trueLine;
        }
        return prevLine;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadCursorDrawable() {
        if (this.mDrawableForCursor == null) {
            this.mDrawableForCursor = this.mTextView.getTextCursorDrawable();
        }
    }

    /* loaded from: classes4.dex */
    public class InsertionPointCursorController implements CursorController {
        private InsertionHandleView mHandle;
        private boolean mIsDraggingCursor;
        private boolean mIsTouchSnappedToHandleDuringDrag;
        private int mPrevLineDuringDrag;

        public InsertionPointCursorController() {
        }

        public void onTouchEvent(MotionEvent event) {
            if (Editor.this.hasSelectionController() && Editor.this.getSelectionController().isCursorBeingModified()) {
                return;
            }
            switch (event.getActionMasked()) {
                case 1:
                case 3:
                    if (this.mIsDraggingCursor) {
                        endCursorDrag(event);
                        return;
                    }
                    return;
                case 2:
                    if (!event.isFromSource(8194)) {
                        if (!Editor.this.mTextView.isAutoHandwritingEnabled() || !isFromStylus(event)) {
                            if (this.mIsDraggingCursor) {
                                performCursorDrag(event);
                                return;
                            } else if (Editor.this.mFlagCursorDragFromAnywhereEnabled && Editor.this.mTextView.getLayout() != null && Editor.this.mTextView.isFocused() && Editor.this.mTouchState.isMovedEnoughForDrag()) {
                                if (Editor.this.mTouchState.getInitialDragDirectionXYRatio() > Editor.this.mCursorDragDirectionMinXYRatio || Editor.this.mTouchState.isOnHandle()) {
                                    startCursorDrag(event);
                                    return;
                                }
                                return;
                            } else {
                                return;
                            }
                        }
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        private boolean isFromStylus(MotionEvent motionEvent) {
            int pointerIndex = motionEvent.getActionIndex();
            return motionEvent.getToolType(pointerIndex) == 2;
        }

        private void positionCursorDuringDrag(MotionEvent event) {
            this.mPrevLineDuringDrag = getLineDuringDrag(event);
            int offset = Editor.this.mTextView.getOffsetAtCoordinate(this.mPrevLineDuringDrag, event.getX());
            int oldSelectionStart = Editor.this.mTextView.getSelectionStart();
            int oldSelectionEnd = Editor.this.mTextView.getSelectionEnd();
            if (offset == oldSelectionStart && offset == oldSelectionEnd) {
                return;
            }
            Selection.setSelection((Spannable) Editor.this.mTextView.getText(), offset);
            Editor.this.updateCursorPosition();
            if (Editor.this.mHapticTextHandleEnabled) {
                Editor.this.mTextView.performHapticFeedback(9);
            }
        }

        private int getLineDuringDrag(MotionEvent event) {
            float fingerY;
            Layout layout = Editor.this.mTextView.getLayout();
            int i = this.mPrevLineDuringDrag;
            if (i == -1) {
                return Editor.this.getCurrentLineAdjustedForSlop(layout, i, event.getY());
            }
            if (Editor.this.mTouchState.isOnHandle()) {
                fingerY = event.getRawY() - Editor.this.mTextView.getLocationOnScreen()[1];
            } else {
                fingerY = event.getY();
            }
            float cursorY = fingerY - getHandle().getIdealFingerToCursorOffset();
            int line = Editor.this.getCurrentLineAdjustedForSlop(layout, this.mPrevLineDuringDrag, cursorY);
            if (this.mIsTouchSnappedToHandleDuringDrag) {
                return line;
            }
            int i2 = this.mPrevLineDuringDrag;
            if (line < i2) {
                return Math.min(i2, Editor.this.getCurrentLineAdjustedForSlop(layout, i2, fingerY));
            }
            this.mIsTouchSnappedToHandleDuringDrag = true;
            return line;
        }

        private void startCursorDrag(MotionEvent event) {
            this.mIsDraggingCursor = true;
            this.mIsTouchSnappedToHandleDuringDrag = false;
            this.mPrevLineDuringDrag = -1;
            Editor.this.mTextView.getParent().requestDisallowInterceptTouchEvent(true);
            Editor.this.mTextView.cancelLongPress();
            positionCursorDuringDrag(event);
            show();
            getHandle().removeHiderCallback();
            getHandle().updateMagnifier(event);
        }

        private void performCursorDrag(MotionEvent event) {
            positionCursorDuringDrag(event);
            getHandle().updateMagnifier(event);
        }

        private void endCursorDrag(MotionEvent event) {
            this.mIsDraggingCursor = false;
            this.mIsTouchSnappedToHandleDuringDrag = false;
            this.mPrevLineDuringDrag = -1;
            getHandle().dismissMagnifier();
            getHandle().hideAfterDelay();
            Editor.this.mTextView.getParent().requestDisallowInterceptTouchEvent(false);
        }

        @Override // android.widget.Editor.CursorController
        public void show() {
            getHandle().show();
            long durationSinceCutOrCopy = SystemClock.uptimeMillis() - TextView.sLastCutCopyOrTextChangedTime;
            if (Editor.this.mInsertionActionModeRunnable != null && (this.mIsDraggingCursor || Editor.this.mTouchState.isMultiTap() || Editor.this.isCursorInsideEasyCorrectionSpan())) {
                Editor.this.mTextView.removeCallbacks(Editor.this.mInsertionActionModeRunnable);
            }
            if (!this.mIsDraggingCursor && !Editor.this.mTouchState.isMultiTap() && !Editor.this.isCursorInsideEasyCorrectionSpan() && durationSinceCutOrCopy < 15000 && Editor.this.mTextActionMode == null) {
                if (Editor.this.mInsertionActionModeRunnable == null) {
                    Editor.this.mInsertionActionModeRunnable = new Runnable() { // from class: android.widget.Editor.InsertionPointCursorController.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Editor.this.startInsertionActionMode();
                        }
                    };
                }
                Editor.this.mTextView.postDelayed(Editor.this.mInsertionActionModeRunnable, ViewConfiguration.getDoubleTapTimeout() + 1);
            }
            if (!this.mIsDraggingCursor) {
                getHandle().hideAfterDelay();
            }
            if (Editor.this.mSelectionModifierCursorController != null) {
                Editor.this.mSelectionModifierCursorController.hide();
            }
        }

        @Override // android.widget.Editor.CursorController
        public void hide() {
            InsertionHandleView insertionHandleView = this.mHandle;
            if (insertionHandleView != null) {
                insertionHandleView.hide();
            }
        }

        @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
        public void onTouchModeChanged(boolean isInTouchMode) {
            if (!isInTouchMode) {
                hide();
            }
        }

        public InsertionHandleView getHandle() {
            if (this.mHandle == null) {
                Editor.this.loadHandleDrawables(false);
                Editor editor = Editor.this;
                this.mHandle = new InsertionHandleView(editor.mSelectHandleCenter);
            }
            return this.mHandle;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void reloadHandleDrawable() {
            InsertionHandleView insertionHandleView = this.mHandle;
            if (insertionHandleView == null) {
                return;
            }
            insertionHandleView.setDrawables(Editor.this.mSelectHandleCenter, Editor.this.mSelectHandleCenter);
        }

        @Override // android.widget.Editor.CursorController
        public void onDetached() {
            ViewTreeObserver observer = Editor.this.mTextView.getViewTreeObserver();
            observer.removeOnTouchModeChangeListener(this);
            InsertionHandleView insertionHandleView = this.mHandle;
            if (insertionHandleView != null) {
                insertionHandleView.onDetached();
            }
        }

        @Override // android.widget.Editor.CursorController
        public boolean isCursorBeingModified() {
            InsertionHandleView insertionHandleView;
            return this.mIsDraggingCursor || ((insertionHandleView = this.mHandle) != null && insertionHandleView.isDragging());
        }

        @Override // android.widget.Editor.CursorController
        public boolean isActive() {
            InsertionHandleView insertionHandleView = this.mHandle;
            return insertionHandleView != null && insertionHandleView.isShowing();
        }

        public void invalidateHandle() {
            InsertionHandleView insertionHandleView = this.mHandle;
            if (insertionHandleView != null) {
                insertionHandleView.invalidate();
            }
        }
    }

    /* loaded from: classes4.dex */
    public class SelectionModifierCursorController implements CursorController {
        private static final int DRAG_ACCELERATOR_MODE_CHARACTER = 1;
        private static final int DRAG_ACCELERATOR_MODE_INACTIVE = 0;
        private static final int DRAG_ACCELERATOR_MODE_PARAGRAPH = 3;
        private static final int DRAG_ACCELERATOR_MODE_WORD = 2;
        private SelectionHandleView mEndHandle;
        private boolean mGestureStayedInTapRegion;
        private boolean mHaventMovedEnoughToStartDrag;
        private int mMaxTouchOffset;
        private int mMinTouchOffset;
        private SelectionHandleView mStartHandle;
        private int mStartOffset = -1;
        private int mLineSelectionIsOn = -1;
        private boolean mSwitchedLines = false;
        private int mDragAcceleratorMode = 0;

        SelectionModifierCursorController() {
            resetTouchOffsets();
        }

        @Override // android.widget.Editor.CursorController
        public void show() {
            if (Editor.this.mTextView.isInBatchEditMode()) {
                return;
            }
            Editor.this.loadHandleDrawables(false);
            initHandles();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initHandles() {
            if (this.mStartHandle == null) {
                Editor editor = Editor.this;
                this.mStartHandle = new SelectionHandleView(editor.mSelectHandleLeft, Editor.this.mSelectHandleRight, C4057R.C4059id.selection_start_handle, 0);
            }
            if (this.mEndHandle == null) {
                Editor editor2 = Editor.this;
                this.mEndHandle = new SelectionHandleView(editor2.mSelectHandleRight, Editor.this.mSelectHandleLeft, C4057R.C4059id.selection_end_handle, 1);
            }
            this.mStartHandle.show();
            this.mEndHandle.show();
            Editor.this.hideInsertionPointCursorController();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void reloadHandleDrawables() {
            SelectionHandleView selectionHandleView = this.mStartHandle;
            if (selectionHandleView == null) {
                return;
            }
            selectionHandleView.setDrawables(Editor.this.mSelectHandleLeft, Editor.this.mSelectHandleRight);
            this.mEndHandle.setDrawables(Editor.this.mSelectHandleRight, Editor.this.mSelectHandleLeft);
        }

        @Override // android.widget.Editor.CursorController
        public void hide() {
            SelectionHandleView selectionHandleView = this.mStartHandle;
            if (selectionHandleView != null) {
                selectionHandleView.hide();
            }
            SelectionHandleView selectionHandleView2 = this.mEndHandle;
            if (selectionHandleView2 != null) {
                selectionHandleView2.hide();
            }
        }

        public void enterDrag(int dragAcceleratorMode) {
            show();
            this.mDragAcceleratorMode = dragAcceleratorMode;
            this.mStartOffset = Editor.this.mTextView.getOffsetForPosition(Editor.this.mTouchState.getLastDownX(), Editor.this.mTouchState.getLastDownY());
            this.mLineSelectionIsOn = Editor.this.mTextView.getLineAtCoordinate(Editor.this.mTouchState.getLastDownY());
            hide();
            Editor.this.mTextView.getParent().requestDisallowInterceptTouchEvent(true);
            Editor.this.mTextView.cancelLongPress();
        }

        public void onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            boolean isMouse = event.isFromSource(8194);
            switch (event.getActionMasked()) {
                case 0:
                    if (Editor.this.extractedTextModeWillBeStarted()) {
                        hide();
                        return;
                    }
                    int offsetForPosition = Editor.this.mTextView.getOffsetForPosition(eventX, eventY);
                    this.mMaxTouchOffset = offsetForPosition;
                    this.mMinTouchOffset = offsetForPosition;
                    if (this.mGestureStayedInTapRegion && Editor.this.mTouchState.isMultiTapInSameArea() && (isMouse || Editor.this.isPositionOnText(eventX, eventY) || Editor.this.mTouchState.isOnHandle())) {
                        if (Editor.this.mTouchState.isDoubleTap()) {
                            Editor.this.selectCurrentWordAndStartDrag();
                        } else if (Editor.this.mTouchState.isTripleClick()) {
                            selectCurrentParagraphAndStartDrag();
                        }
                        Editor.this.mDiscardNextActionUp = true;
                    }
                    this.mGestureStayedInTapRegion = true;
                    this.mHaventMovedEnoughToStartDrag = true;
                    return;
                case 1:
                    SelectionHandleView selectionHandleView = this.mEndHandle;
                    if (selectionHandleView != null) {
                        selectionHandleView.dismissMagnifier();
                    }
                    if (isDragAcceleratorActive()) {
                        updateSelection(event);
                        Editor.this.mTextView.getParent().requestDisallowInterceptTouchEvent(false);
                        resetDragAcceleratorState();
                        if (Editor.this.mTextView.hasSelection()) {
                            Editor.this.startSelectionActionModeAsync(this.mHaventMovedEnoughToStartDrag);
                            return;
                        }
                        return;
                    }
                    return;
                case 2:
                    if (this.mGestureStayedInTapRegion) {
                        ViewConfiguration viewConfig = ViewConfiguration.get(Editor.this.mTextView.getContext());
                        this.mGestureStayedInTapRegion = EditorTouchState.isDistanceWithin(Editor.this.mTouchState.getLastDownX(), Editor.this.mTouchState.getLastDownY(), eventX, eventY, viewConfig.getScaledDoubleTapTouchSlop());
                    }
                    if (this.mHaventMovedEnoughToStartDrag) {
                        this.mHaventMovedEnoughToStartDrag = !Editor.this.mTouchState.isMovedEnoughForDrag();
                    }
                    if (isMouse && !isDragAcceleratorActive()) {
                        int offset = Editor.this.mTextView.getOffsetForPosition(eventX, eventY);
                        if (Editor.this.mTextView.hasSelection() && ((!this.mHaventMovedEnoughToStartDrag || this.mStartOffset != offset) && offset >= Editor.this.mTextView.getSelectionStart() && offset <= Editor.this.mTextView.getSelectionEnd())) {
                            Editor.this.startDragAndDrop();
                            return;
                        } else if (this.mStartOffset != offset) {
                            Editor.this.lambda$startActionModeInternal$0();
                            enterDrag(1);
                            Editor.this.mDiscardNextActionUp = true;
                            this.mHaventMovedEnoughToStartDrag = false;
                        }
                    }
                    SelectionHandleView selectionHandleView2 = this.mStartHandle;
                    if (selectionHandleView2 == null || !selectionHandleView2.isShowing()) {
                        updateSelection(event);
                        if (Editor.this.mTextView.hasSelection() && this.mEndHandle != null && isDragAcceleratorActive()) {
                            this.mEndHandle.updateMagnifier(event);
                            return;
                        }
                        return;
                    }
                    return;
                case 3:
                case 4:
                default:
                    return;
                case 5:
                case 6:
                    if (Editor.this.mTextView.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT)) {
                        updateMinAndMaxOffsets(event);
                        return;
                    }
                    return;
            }
        }

        private void updateSelection(MotionEvent event) {
            if (Editor.this.mTextView.getLayout() != null) {
                switch (this.mDragAcceleratorMode) {
                    case 1:
                        updateCharacterBasedSelection(event);
                        return;
                    case 2:
                        updateWordBasedSelection(event);
                        return;
                    case 3:
                        updateParagraphBasedSelection(event);
                        return;
                    default:
                        return;
                }
            }
        }

        private boolean selectCurrentParagraphAndStartDrag() {
            if (Editor.this.mInsertionActionModeRunnable != null) {
                Editor.this.mTextView.removeCallbacks(Editor.this.mInsertionActionModeRunnable);
            }
            Editor.this.lambda$startActionModeInternal$0();
            if (!Editor.this.selectCurrentParagraph()) {
                return false;
            }
            enterDrag(3);
            return true;
        }

        private void updateCharacterBasedSelection(MotionEvent event) {
            int offset = Editor.this.mTextView.getOffsetForPosition(event.getX(), event.getY());
            updateSelectionInternal(this.mStartOffset, offset, event.isFromSource(4098));
        }

        private void updateWordBasedSelection(MotionEvent event) {
            int currLine;
            float fingerOffset;
            int offset;
            int startOffset;
            if (this.mHaventMovedEnoughToStartDrag) {
                return;
            }
            boolean isMouse = event.isFromSource(8194);
            ViewConfiguration viewConfig = ViewConfiguration.get(Editor.this.mTextView.getContext());
            float eventX = event.getX();
            float eventY = event.getY();
            if (isMouse) {
                currLine = Editor.this.mTextView.getLineAtCoordinate(eventY);
            } else {
                float y = eventY;
                if (this.mSwitchedLines) {
                    int touchSlop = viewConfig.getScaledTouchSlop();
                    SelectionHandleView selectionHandleView = this.mStartHandle;
                    if (selectionHandleView != null) {
                        fingerOffset = selectionHandleView.getIdealVerticalOffset();
                    } else {
                        fingerOffset = touchSlop;
                    }
                    y = eventY - fingerOffset;
                }
                Editor editor = Editor.this;
                int currLine2 = editor.getCurrentLineAdjustedForSlop(editor.mTextView.getLayout(), this.mLineSelectionIsOn, y);
                if (!this.mSwitchedLines && currLine2 != this.mLineSelectionIsOn) {
                    this.mSwitchedLines = true;
                    return;
                }
                currLine = currLine2;
            }
            int offset2 = Editor.this.mTextView.getOffsetAtCoordinate(currLine, eventX);
            if (this.mStartOffset < offset2) {
                offset = Editor.this.getWordEnd(offset2);
                startOffset = Editor.this.getWordStart(this.mStartOffset);
            } else {
                offset = Editor.this.getWordStart(offset2);
                startOffset = Editor.this.getWordEnd(this.mStartOffset);
                if (startOffset == offset) {
                    offset = Editor.this.getNextCursorOffset(offset, false);
                }
            }
            this.mLineSelectionIsOn = currLine;
            updateSelectionInternal(startOffset, offset, event.isFromSource(4098));
        }

        private void updateParagraphBasedSelection(MotionEvent event) {
            int offset = Editor.this.mTextView.getOffsetForPosition(event.getX(), event.getY());
            int start = Math.min(offset, this.mStartOffset);
            int end = Math.max(offset, this.mStartOffset);
            long paragraphsRange = Editor.this.getParagraphsRange(start, end);
            int selectionStart = TextUtils.unpackRangeStartFromLong(paragraphsRange);
            int selectionEnd = TextUtils.unpackRangeEndFromLong(paragraphsRange);
            updateSelectionInternal(selectionStart, selectionEnd, event.isFromSource(4098));
        }

        private void updateSelectionInternal(int selectionStart, int selectionEnd, boolean fromTouchScreen) {
            boolean performHapticFeedback = fromTouchScreen && Editor.this.mHapticTextHandleEnabled && !(Editor.this.mTextView.getSelectionStart() == selectionStart && Editor.this.mTextView.getSelectionEnd() == selectionEnd);
            Selection.setSelection((Spannable) Editor.this.mTextView.getText(), selectionStart, selectionEnd);
            if (performHapticFeedback) {
                Editor.this.mTextView.performHapticFeedback(9);
            }
        }

        private void updateMinAndMaxOffsets(MotionEvent event) {
            int pointerCount = event.getPointerCount();
            for (int index = 0; index < pointerCount; index++) {
                int offset = Editor.this.mTextView.getOffsetForPosition(event.getX(index), event.getY(index));
                if (offset < this.mMinTouchOffset) {
                    this.mMinTouchOffset = offset;
                }
                if (offset > this.mMaxTouchOffset) {
                    this.mMaxTouchOffset = offset;
                }
            }
        }

        public int getMinTouchOffset() {
            return this.mMinTouchOffset;
        }

        public int getMaxTouchOffset() {
            return this.mMaxTouchOffset;
        }

        public void resetTouchOffsets() {
            this.mMaxTouchOffset = -1;
            this.mMinTouchOffset = -1;
            resetDragAcceleratorState();
        }

        private void resetDragAcceleratorState() {
            this.mStartOffset = -1;
            this.mDragAcceleratorMode = 0;
            this.mSwitchedLines = false;
            int selectionStart = Editor.this.mTextView.getSelectionStart();
            int selectionEnd = Editor.this.mTextView.getSelectionEnd();
            if (selectionStart < 0 || selectionEnd < 0) {
                Selection.removeSelection((Spannable) Editor.this.mTextView.getText());
            } else if (selectionStart > selectionEnd) {
                Selection.setSelection((Spannable) Editor.this.mTextView.getText(), selectionEnd, selectionStart);
            }
        }

        public boolean isSelectionStartDragged() {
            SelectionHandleView selectionHandleView = this.mStartHandle;
            return selectionHandleView != null && selectionHandleView.isDragging();
        }

        @Override // android.widget.Editor.CursorController
        public boolean isCursorBeingModified() {
            SelectionHandleView selectionHandleView;
            return isDragAcceleratorActive() || isSelectionStartDragged() || ((selectionHandleView = this.mEndHandle) != null && selectionHandleView.isDragging());
        }

        public boolean isDragAcceleratorActive() {
            return this.mDragAcceleratorMode != 0;
        }

        @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
        public void onTouchModeChanged(boolean isInTouchMode) {
            if (!isInTouchMode) {
                hide();
            }
        }

        @Override // android.widget.Editor.CursorController
        public void onDetached() {
            ViewTreeObserver observer = Editor.this.mTextView.getViewTreeObserver();
            observer.removeOnTouchModeChangeListener(this);
            SelectionHandleView selectionHandleView = this.mStartHandle;
            if (selectionHandleView != null) {
                selectionHandleView.onDetached();
            }
            SelectionHandleView selectionHandleView2 = this.mEndHandle;
            if (selectionHandleView2 != null) {
                selectionHandleView2.onDetached();
            }
        }

        @Override // android.widget.Editor.CursorController
        public boolean isActive() {
            SelectionHandleView selectionHandleView = this.mStartHandle;
            return selectionHandleView != null && selectionHandleView.isShowing();
        }

        public void invalidateHandles() {
            SelectionHandleView selectionHandleView = this.mStartHandle;
            if (selectionHandleView != null) {
                selectionHandleView.invalidate();
            }
            SelectionHandleView selectionHandleView2 = this.mEndHandle;
            if (selectionHandleView2 != null) {
                selectionHandleView2.invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadHandleDrawables(boolean overwrite) {
        if (this.mSelectHandleCenter == null || overwrite) {
            this.mSelectHandleCenter = this.mTextView.getTextSelectHandle();
            if (hasInsertionController()) {
                getInsertionController().reloadHandleDrawable();
            }
        }
        if (this.mSelectHandleLeft == null || this.mSelectHandleRight == null || overwrite) {
            this.mSelectHandleLeft = this.mTextView.getTextSelectHandleLeft();
            this.mSelectHandleRight = this.mTextView.getTextSelectHandleRight();
            if (hasSelectionController()) {
                getSelectionController().reloadHandleDrawables();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class CorrectionHighlighter {
        private static final int FADE_OUT_DURATION = 400;
        private int mEnd;
        private long mFadingStartTime;
        private final Paint mPaint;
        private final Path mPath = new Path();
        private int mStart;
        private RectF mTempRectF;

        public CorrectionHighlighter() {
            Paint paint = new Paint(1);
            this.mPaint = paint;
            paint.setCompatibilityScaling(Editor.this.mTextView.getResources().getCompatibilityInfo().applicationScale);
            paint.setStyle(Paint.Style.FILL);
        }

        public void highlight(CorrectionInfo info) {
            int offset = info.getOffset();
            this.mStart = offset;
            this.mEnd = offset + info.getNewText().length();
            this.mFadingStartTime = SystemClock.uptimeMillis();
            if (this.mStart < 0 || this.mEnd < 0) {
                stopAnimation();
            }
        }

        public void draw(Canvas canvas, int cursorOffsetVertical) {
            if (updatePath() && updatePaint()) {
                if (cursorOffsetVertical != 0) {
                    canvas.translate(0.0f, cursorOffsetVertical);
                }
                canvas.drawPath(this.mPath, this.mPaint);
                if (cursorOffsetVertical != 0) {
                    canvas.translate(0.0f, -cursorOffsetVertical);
                }
                invalidate(true);
                return;
            }
            stopAnimation();
            invalidate(false);
        }

        private boolean updatePaint() {
            long duration = SystemClock.uptimeMillis() - this.mFadingStartTime;
            if (duration > 400) {
                return false;
            }
            float coef = 1.0f - (((float) duration) / 400.0f);
            int highlightColorAlpha = Color.alpha(Editor.this.mTextView.mHighlightColor);
            int color = (Editor.this.mTextView.mHighlightColor & 16777215) + (((int) (highlightColorAlpha * coef)) << 24);
            this.mPaint.setColor(color);
            return true;
        }

        private boolean updatePath() {
            Layout layout = Editor.this.mTextView.getLayout();
            if (layout == null) {
                return false;
            }
            int length = Editor.this.mTextView.getText().length();
            int start = Math.min(length, this.mStart);
            int end = Math.min(length, this.mEnd);
            this.mPath.reset();
            layout.getSelectionPath(Editor.this.mTextView.originalToTransformed(start, 0), Editor.this.mTextView.originalToTransformed(end, 0), this.mPath);
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void invalidate(boolean delayed) {
            if (Editor.this.mTextView.getLayout() == null) {
                return;
            }
            if (this.mTempRectF == null) {
                this.mTempRectF = new RectF();
            }
            this.mPath.computeBounds(this.mTempRectF, false);
            int left = Editor.this.mTextView.getCompoundPaddingLeft();
            int top = Editor.this.mTextView.getExtendedPaddingTop() + Editor.this.mTextView.getVerticalOffset(true);
            if (delayed) {
                Editor.this.mTextView.postInvalidateOnAnimation(((int) this.mTempRectF.left) + left, ((int) this.mTempRectF.top) + top, ((int) this.mTempRectF.right) + left, ((int) this.mTempRectF.bottom) + top);
            } else {
                Editor.this.mTextView.postInvalidate((int) this.mTempRectF.left, (int) this.mTempRectF.top, (int) this.mTempRectF.right, (int) this.mTempRectF.bottom);
            }
        }

        private void stopAnimation() {
            Editor.this.mCorrectionHighlighter = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class ErrorPopup extends PopupWindow {
        private boolean mAbove;
        private int mPopupInlineErrorAboveBackgroundId;
        private int mPopupInlineErrorBackgroundId;
        private final TextView mView;

        ErrorPopup(TextView v, int width, int height) {
            super(v, width, height);
            this.mAbove = false;
            this.mPopupInlineErrorBackgroundId = 0;
            this.mPopupInlineErrorAboveBackgroundId = 0;
            this.mView = v;
            int resourceId = getResourceId(0, 311);
            this.mPopupInlineErrorBackgroundId = resourceId;
            v.setBackgroundResource(resourceId);
        }

        void fixDirection(boolean above) {
            this.mAbove = above;
            if (above) {
                this.mPopupInlineErrorAboveBackgroundId = getResourceId(this.mPopupInlineErrorAboveBackgroundId, 310);
            } else {
                this.mPopupInlineErrorBackgroundId = getResourceId(this.mPopupInlineErrorBackgroundId, 311);
            }
            this.mView.setBackgroundResource(above ? this.mPopupInlineErrorAboveBackgroundId : this.mPopupInlineErrorBackgroundId);
        }

        private int getResourceId(int currentId, int index) {
            if (currentId == 0) {
                TypedArray styledAttributes = this.mView.getContext().obtainStyledAttributes(C0001R.styleable.Theme);
                int currentId2 = styledAttributes.getResourceId(index, 0);
                styledAttributes.recycle();
                return currentId2;
            }
            return currentId;
        }

        @Override // android.widget.PopupWindow
        public void update(int x, int y, int w, int h, boolean force) {
            super.update(x, y, w, h, force);
            boolean above = isAboveAnchor();
            if (above != this.mAbove) {
                fixDirection(above);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class InputContentType {
        boolean enterDown;
        Bundle extras;
        int imeActionId;
        CharSequence imeActionLabel;
        LocaleList imeHintLocales;
        int imeOptions = 0;
        TextView.OnEditorActionListener onEditorActionListener;
        String privateImeOptions;

        InputContentType() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class InputMethodState {
        int mBatchEditNesting;
        int mChangedDelta;
        int mChangedEnd;
        int mChangedStart;
        boolean mContentChanged;
        boolean mCursorChanged;
        final ExtractedText mExtractedText = new ExtractedText();
        ExtractedTextRequest mExtractedTextRequest;
        boolean mSelectionModeChanged;
        int mUpdateCursorAnchorInfoFilter;
        int mUpdateCursorAnchorInfoMode;

        InputMethodState() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isValidRange(CharSequence text, int start, int end) {
        return start >= 0 && start <= end && end <= text.length();
    }

    /* loaded from: classes4.dex */
    public static class UndoInputFilter implements InputFilter {
        private static final int MERGE_EDIT_MODE_FORCE_MERGE = 0;
        private static final int MERGE_EDIT_MODE_NEVER_MERGE = 1;
        private static final int MERGE_EDIT_MODE_NORMAL = 2;
        private final Editor mEditor;
        private boolean mExpanding;
        private boolean mHasComposition;
        private boolean mIsUserEdit;
        private boolean mPreviousOperationWasInSameBatchEdit;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes4.dex */
        private @interface MergeMode {
        }

        public UndoInputFilter(Editor editor) {
            this.mEditor = editor;
        }

        public void saveInstanceState(Parcel parcel) {
            parcel.writeInt(this.mIsUserEdit ? 1 : 0);
            parcel.writeInt(this.mHasComposition ? 1 : 0);
            parcel.writeInt(this.mExpanding ? 1 : 0);
            parcel.writeInt(this.mPreviousOperationWasInSameBatchEdit ? 1 : 0);
        }

        public void restoreInstanceState(Parcel parcel) {
            this.mIsUserEdit = parcel.readInt() != 0;
            this.mHasComposition = parcel.readInt() != 0;
            this.mExpanding = parcel.readInt() != 0;
            this.mPreviousOperationWasInSameBatchEdit = parcel.readInt() != 0;
        }

        public void beginBatchEdit() {
            this.mIsUserEdit = true;
        }

        public void endBatchEdit() {
            this.mIsUserEdit = false;
            this.mPreviousOperationWasInSameBatchEdit = false;
        }

        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            boolean shouldCreateSeparateState;
            if (canUndoEdit(source, start, end, dest, dstart, dend)) {
                boolean hadComposition = this.mHasComposition;
                this.mHasComposition = isComposition(source);
                boolean wasExpanding = this.mExpanding;
                if (end - start != dend - dstart) {
                    boolean z = end - start > dend - dstart;
                    this.mExpanding = z;
                    if (hadComposition && z != wasExpanding) {
                        shouldCreateSeparateState = true;
                        handleEdit(source, start, end, dest, dstart, dend, shouldCreateSeparateState);
                        return null;
                    }
                }
                shouldCreateSeparateState = false;
                handleEdit(source, start, end, dest, dstart, dend, shouldCreateSeparateState);
                return null;
            }
            return null;
        }

        void freezeLastEdit() {
            this.mEditor.mUndoManager.beginUpdate("Edit text");
            EditOperation lastEdit = getLastEdit();
            if (lastEdit != null) {
                lastEdit.mFrozen = true;
            }
            this.mEditor.mUndoManager.endUpdate();
        }

        private void handleEdit(CharSequence source, int start, int end, Spanned dest, int dstart, int dend, boolean shouldCreateSeparateState) {
            int mergeMode;
            if (isInTextWatcher() || this.mPreviousOperationWasInSameBatchEdit) {
                mergeMode = 0;
            } else if (shouldCreateSeparateState) {
                mergeMode = 1;
            } else {
                mergeMode = 2;
            }
            String newText = TextUtils.substring(source, start, end);
            String oldText = TextUtils.substring(dest, dstart, dend);
            EditOperation edit = new EditOperation(this.mEditor, oldText, dstart, newText, this.mHasComposition);
            if (this.mHasComposition && TextUtils.equals(edit.mNewText, edit.mOldText)) {
                return;
            }
            recordEdit(edit, mergeMode);
        }

        private EditOperation getLastEdit() {
            UndoManager um = this.mEditor.mUndoManager;
            return (EditOperation) um.getLastOperation(EditOperation.class, this.mEditor.mUndoOwner, 1);
        }

        private void recordEdit(EditOperation edit, int mergeMode) {
            UndoManager um = this.mEditor.mUndoManager;
            um.beginUpdate("Edit text");
            EditOperation lastEdit = getLastEdit();
            if (lastEdit == null) {
                um.addOperation(edit, 0);
            } else if (mergeMode == 0) {
                lastEdit.forceMergeWith(edit);
            } else if (!this.mIsUserEdit) {
                um.commitState(this.mEditor.mUndoOwner);
                um.addOperation(edit, 0);
            } else if (mergeMode != 2 || !lastEdit.mergeWith(edit)) {
                um.commitState(this.mEditor.mUndoOwner);
                um.addOperation(edit, 0);
            }
            this.mPreviousOperationWasInSameBatchEdit = this.mIsUserEdit;
            um.endUpdate();
        }

        private boolean canUndoEdit(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (this.mEditor.mAllowUndo && !this.mEditor.mUndoManager.isInUndo() && Editor.isValidRange(source, start, end) && Editor.isValidRange(dest, dstart, dend)) {
                return (start == end && dstart == dend) ? false : true;
            }
            return false;
        }

        private static boolean isComposition(CharSequence source) {
            if (source instanceof Spannable) {
                Spannable text = (Spannable) source;
                int composeBegin = EditableInputConnection.getComposingSpanStart(text);
                int composeEnd = EditableInputConnection.getComposingSpanEnd(text);
                return composeBegin < composeEnd;
            }
            return false;
        }

        private boolean isInTextWatcher() {
            CharSequence text = this.mEditor.mTextView.getText();
            return (text instanceof SpannableStringBuilder) && ((SpannableStringBuilder) text).getTextWatcherDepth() > 0;
        }
    }

    /* loaded from: classes4.dex */
    public static class EditOperation extends UndoOperation<Editor> {
        public static final Parcelable.ClassLoaderCreator<EditOperation> CREATOR = new Parcelable.ClassLoaderCreator<EditOperation>() { // from class: android.widget.Editor.EditOperation.1
            @Override // android.p008os.Parcelable.Creator
            public EditOperation createFromParcel(Parcel in) {
                return new EditOperation(in, null);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.ClassLoaderCreator
            public EditOperation createFromParcel(Parcel in, ClassLoader loader) {
                return new EditOperation(in, loader);
            }

            @Override // android.p008os.Parcelable.Creator
            public EditOperation[] newArray(int size) {
                return new EditOperation[size];
            }
        };
        private static final int TYPE_DELETE = 1;
        private static final int TYPE_INSERT = 0;
        private static final int TYPE_REPLACE = 2;
        private boolean mFrozen;
        private boolean mIsComposition;
        private int mNewCursorPos;
        private String mNewText;
        private int mOldCursorPos;
        private String mOldText;
        private int mStart;
        private int mType;

        public EditOperation(Editor editor, String oldText, int dstart, String newText, boolean isComposition) {
            super(editor.mUndoOwner);
            this.mOldText = oldText;
            this.mNewText = newText;
            if (newText.length() > 0 && this.mOldText.length() == 0) {
                this.mType = 0;
            } else if (this.mNewText.length() == 0 && this.mOldText.length() > 0) {
                this.mType = 1;
            } else {
                this.mType = 2;
            }
            this.mStart = dstart;
            this.mOldCursorPos = editor.mTextView.getSelectionStart();
            this.mNewCursorPos = this.mNewText.length() + dstart;
            this.mIsComposition = isComposition;
        }

        public EditOperation(Parcel src, ClassLoader loader) {
            super(src, loader);
            this.mType = src.readInt();
            this.mOldText = src.readString();
            this.mNewText = src.readString();
            this.mStart = src.readInt();
            this.mOldCursorPos = src.readInt();
            this.mNewCursorPos = src.readInt();
            this.mFrozen = src.readInt() == 1;
            this.mIsComposition = src.readInt() == 1;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mType);
            dest.writeString(this.mOldText);
            dest.writeString(this.mNewText);
            dest.writeInt(this.mStart);
            dest.writeInt(this.mOldCursorPos);
            dest.writeInt(this.mNewCursorPos);
            dest.writeInt(this.mFrozen ? 1 : 0);
            dest.writeInt(this.mIsComposition ? 1 : 0);
        }

        private int getNewTextEnd() {
            return this.mStart + this.mNewText.length();
        }

        private int getOldTextEnd() {
            return this.mStart + this.mOldText.length();
        }

        @Override // android.content.UndoOperation
        public void commit() {
        }

        @Override // android.content.UndoOperation
        public void undo() {
            Editor editor = getOwnerData();
            Editable text = (Editable) editor.mTextView.getText();
            modifyText(text, this.mStart, getNewTextEnd(), this.mOldText, this.mStart, this.mOldCursorPos);
        }

        @Override // android.content.UndoOperation
        public void redo() {
            Editor editor = getOwnerData();
            Editable text = (Editable) editor.mTextView.getText();
            modifyText(text, this.mStart, getOldTextEnd(), this.mNewText, this.mStart, this.mNewCursorPos);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean mergeWith(EditOperation edit) {
            if (this.mFrozen) {
                return false;
            }
            switch (this.mType) {
                case 0:
                    return mergeInsertWith(edit);
                case 1:
                    return mergeDeleteWith(edit);
                case 2:
                    return mergeReplaceWith(edit);
                default:
                    return false;
            }
        }

        private boolean mergeInsertWith(EditOperation edit) {
            int i = edit.mType;
            if (i == 0) {
                if (getNewTextEnd() != edit.mStart) {
                    return false;
                }
                this.mNewText += edit.mNewText;
                this.mNewCursorPos = edit.mNewCursorPos;
                this.mFrozen = edit.mFrozen;
                this.mIsComposition = edit.mIsComposition;
                return true;
            } else if (!this.mIsComposition || i != 2 || this.mStart > edit.mStart || getNewTextEnd() < edit.getOldTextEnd()) {
                return false;
            } else {
                this.mNewText = this.mNewText.substring(0, edit.mStart - this.mStart) + edit.mNewText + this.mNewText.substring(edit.getOldTextEnd() - this.mStart, this.mNewText.length());
                this.mNewCursorPos = edit.mNewCursorPos;
                this.mIsComposition = edit.mIsComposition;
                return true;
            }
        }

        private boolean mergeDeleteWith(EditOperation edit) {
            if (edit.mType == 1 && this.mStart == edit.getOldTextEnd()) {
                this.mStart = edit.mStart;
                this.mOldText = edit.mOldText + this.mOldText;
                this.mNewCursorPos = edit.mNewCursorPos;
                this.mIsComposition = edit.mIsComposition;
                return true;
            }
            return false;
        }

        private boolean mergeReplaceWith(EditOperation edit) {
            if (edit.mType == 0 && getNewTextEnd() == edit.mStart) {
                this.mNewText += edit.mNewText;
                this.mNewCursorPos = edit.mNewCursorPos;
                return true;
            } else if (this.mIsComposition) {
                if (edit.mType == 1 && this.mStart <= edit.mStart && getNewTextEnd() >= edit.getOldTextEnd()) {
                    String str = this.mNewText.substring(0, edit.mStart - this.mStart) + this.mNewText.substring(edit.getOldTextEnd() - this.mStart, this.mNewText.length());
                    this.mNewText = str;
                    if (str.isEmpty()) {
                        this.mType = 1;
                    }
                    this.mNewCursorPos = edit.mNewCursorPos;
                    this.mIsComposition = edit.mIsComposition;
                    return true;
                } else if (edit.mType == 2 && this.mStart == edit.mStart && TextUtils.equals(this.mNewText, edit.mOldText)) {
                    this.mNewText = edit.mNewText;
                    this.mNewCursorPos = edit.mNewCursorPos;
                    this.mIsComposition = edit.mIsComposition;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        public void forceMergeWith(EditOperation edit) {
            if (mergeWith(edit)) {
                return;
            }
            Editor editor = getOwnerData();
            Editable editable = (Editable) editor.mTextView.getText();
            Editable originalText = new SpannableStringBuilder(editable.toString());
            modifyText(originalText, this.mStart, getNewTextEnd(), this.mOldText, this.mStart, this.mOldCursorPos);
            Editable finalText = new SpannableStringBuilder(editable.toString());
            modifyText(finalText, edit.mStart, edit.getOldTextEnd(), edit.mNewText, edit.mStart, edit.mNewCursorPos);
            this.mType = 2;
            this.mNewText = finalText.toString();
            this.mOldText = originalText.toString();
            this.mStart = 0;
            this.mNewCursorPos = edit.mNewCursorPos;
            this.mIsComposition = edit.mIsComposition;
        }

        private static void modifyText(Editable text, int deleteFrom, int deleteTo, CharSequence newText, int newTextInsertAt, int newCursorPos) {
            if (Editor.isValidRange(text, deleteFrom, deleteTo) && newTextInsertAt <= text.length() - (deleteTo - deleteFrom)) {
                if (deleteFrom != deleteTo) {
                    text.delete(deleteFrom, deleteTo);
                }
                if (newText.length() != 0) {
                    text.insert(newTextInsertAt, newText);
                }
            }
            if (newCursorPos >= 0 && newCursorPos <= text.length()) {
                Selection.setSelection(text, newCursorPos);
            }
        }

        private String getTypeString() {
            switch (this.mType) {
                case 0:
                    return "insert";
                case 1:
                    return "delete";
                case 2:
                    return "replace";
                default:
                    return "";
            }
        }

        public String toString() {
            return "[mType=" + getTypeString() + ", mOldText=" + this.mOldText + ", mNewText=" + this.mNewText + ", mStart=" + this.mStart + ", mOldCursorPos=" + this.mOldCursorPos + ", mNewCursorPos=" + this.mNewCursorPos + ", mFrozen=" + this.mFrozen + ", mIsComposition=" + this.mIsComposition + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static final class ProcessTextIntentActionsHandler {
        private final SparseArray<AccessibilityNodeInfo.AccessibilityAction> mAccessibilityActions;
        private final SparseArray<Intent> mAccessibilityIntents;
        private final Context mContext;
        private final Editor mEditor;
        private final PackageManager mPackageManager;
        private final String mPackageName;
        private final List<ResolveInfo> mSupportedActivities;
        private final TextView mTextView;

        private ProcessTextIntentActionsHandler(Editor editor) {
            this.mAccessibilityIntents = new SparseArray<>();
            this.mAccessibilityActions = new SparseArray<>();
            this.mSupportedActivities = new ArrayList();
            Editor editor2 = (Editor) Objects.requireNonNull(editor);
            this.mEditor = editor2;
            TextView textView = (TextView) Objects.requireNonNull(editor2.mTextView);
            this.mTextView = textView;
            Context context = (Context) Objects.requireNonNull(textView.getContext());
            this.mContext = context;
            this.mPackageManager = (PackageManager) Objects.requireNonNull(context.getPackageManager());
            this.mPackageName = (String) Objects.requireNonNull(context.getPackageName());
        }

        public void onInitializeMenu(Menu menu) {
            loadSupportedActivities();
            int size = this.mSupportedActivities.size();
            for (int i = 0; i < size; i++) {
                ResolveInfo resolveInfo = this.mSupportedActivities.get(i);
                menu.add(0, 0, i + 100, getLabel(resolveInfo)).setIntent(createProcessTextIntentForResolveInfo(resolveInfo)).setShowAsAction(0);
            }
        }

        public boolean performMenuItemAction(MenuItem item) {
            return fireIntent(item.getIntent());
        }

        public void initializeAccessibilityActions() {
            this.mAccessibilityIntents.clear();
            this.mAccessibilityActions.clear();
            int actionId = 0;
            loadSupportedActivities();
            for (ResolveInfo resolveInfo : this.mSupportedActivities) {
                int i = actionId + 1;
                int actionId2 = actionId + 268435712;
                this.mAccessibilityActions.put(actionId2, new AccessibilityNodeInfo.AccessibilityAction(actionId2, getLabel(resolveInfo)));
                this.mAccessibilityIntents.put(actionId2, createProcessTextIntentForResolveInfo(resolveInfo));
                actionId = i;
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo nodeInfo) {
            for (int i = 0; i < this.mAccessibilityActions.size(); i++) {
                nodeInfo.addAction(this.mAccessibilityActions.valueAt(i));
            }
        }

        public boolean performAccessibilityAction(int actionId) {
            return fireIntent(this.mAccessibilityIntents.get(actionId));
        }

        private boolean fireIntent(Intent intent) {
            if (intent != null && Intent.ACTION_PROCESS_TEXT.equals(intent.getAction())) {
                String selectedText = this.mTextView.getSelectedText();
                intent.putExtra(Intent.EXTRA_PROCESS_TEXT, (String) TextUtils.trimToParcelableSize(selectedText));
                this.mEditor.mPreserveSelection = true;
                this.mTextView.startActivityForResult(intent, 100);
                return true;
            }
            return false;
        }

        private void loadSupportedActivities() {
            this.mSupportedActivities.clear();
            if (!this.mContext.canStartActivityForResult()) {
                return;
            }
            PackageManager packageManager = this.mTextView.getContext().getPackageManager();
            List<ResolveInfo> unfiltered = packageManager.queryIntentActivities(createProcessTextIntent(), 0);
            for (ResolveInfo info : unfiltered) {
                if (isSupportedActivity(info)) {
                    this.mSupportedActivities.add(info);
                }
            }
        }

        private boolean isSupportedActivity(ResolveInfo info) {
            return this.mPackageName.equals(info.activityInfo.packageName) || (info.activityInfo.exported && (info.activityInfo.permission == null || this.mContext.checkSelfPermission(info.activityInfo.permission) == 0));
        }

        private Intent createProcessTextIntentForResolveInfo(ResolveInfo info) {
            return createProcessTextIntent().putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, !this.mTextView.isTextEditable()).setClassName(info.activityInfo.packageName, info.activityInfo.name);
        }

        private Intent createProcessTextIntent() {
            return new Intent().setAction(Intent.ACTION_PROCESS_TEXT).setType("text/plain");
        }

        private CharSequence getLabel(ResolveInfo resolveInfo) {
            return resolveInfo.loadLabel(this.mPackageManager);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class AccessibilitySmartActions {
        private final SparseArray<Pair<AccessibilityNodeInfo.AccessibilityAction, RemoteAction>> mActions;
        private final TextView mTextView;

        private AccessibilitySmartActions(TextView textView) {
            this.mActions = new SparseArray<>();
            this.mTextView = (TextView) Objects.requireNonNull(textView);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addAction(RemoteAction action) {
            int actionId = this.mActions.size() + 268439552;
            this.mActions.put(actionId, new Pair<>(new AccessibilityNodeInfo.AccessibilityAction(actionId, action.getTitle()), action));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void reset() {
            this.mActions.clear();
        }

        void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo nodeInfo) {
            for (int i = 0; i < this.mActions.size(); i++) {
                nodeInfo.addAction(this.mActions.valueAt(i).first);
            }
        }

        boolean performAccessibilityAction(int actionId) {
            Pair<AccessibilityNodeInfo.AccessibilityAction, RemoteAction> pair = this.mActions.get(actionId);
            if (pair != null) {
                TextClassification.createIntentOnClickListener(pair.second.getActionIntent()).onClick(this.mTextView);
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class InsertModeController {
        private final Paint mHighlightPaint;
        private InsertModeTransformationMethod mInsertModeTransformationMethod;
        private boolean mIsInsertModeActive;
        private final TextView mTextView;

        InsertModeController(TextView textView) {
            TextView textView2 = (TextView) Objects.requireNonNull(textView);
            this.mTextView = textView2;
            this.mIsInsertModeActive = false;
            this.mInsertModeTransformationMethod = null;
            Paint paint = new Paint();
            this.mHighlightPaint = paint;
            TypedValue typedValue = new TypedValue();
            textView2.getContext().getTheme().resolveAttribute(16843827, typedValue, true);
            int colorPrimary = typedValue.data;
            int highlightColor = ColorUtils.setAlphaComponent(colorPrimary, (int) (Color.alpha(colorPrimary) * 0.12f));
            paint.setColor(highlightColor);
        }

        boolean enterInsertMode(int offset) {
            if (this.mIsInsertModeActive) {
                return false;
            }
            TransformationMethod oldTransformationMethod = this.mTextView.getTransformationMethod();
            if (oldTransformationMethod instanceof OffsetMapping) {
                return false;
            }
            boolean isSingleLine = this.mTextView.isSingleLine();
            InsertModeTransformationMethod insertModeTransformationMethod = new InsertModeTransformationMethod(offset, isSingleLine, oldTransformationMethod);
            this.mInsertModeTransformationMethod = insertModeTransformationMethod;
            this.mTextView.setTransformationMethodInternal(insertModeTransformationMethod);
            Selection.setSelection((Spannable) this.mTextView.getText(), offset);
            this.mIsInsertModeActive = true;
            return true;
        }

        void exitInsertMode() {
            if (this.mIsInsertModeActive) {
                InsertModeTransformationMethod insertModeTransformationMethod = this.mInsertModeTransformationMethod;
                if (insertModeTransformationMethod == null || insertModeTransformationMethod != this.mTextView.getTransformationMethod()) {
                    this.mIsInsertModeActive = false;
                    return;
                }
                int selectionStart = this.mTextView.getSelectionStart();
                int selectionEnd = this.mTextView.getSelectionEnd();
                TransformationMethod oldTransformationMethod = this.mInsertModeTransformationMethod.getOldTransformationMethod();
                this.mTextView.setTransformationMethodInternal(oldTransformationMethod);
                Selection.setSelection((Spannable) this.mTextView.getText(), selectionStart, selectionEnd);
                this.mIsInsertModeActive = false;
            }
        }

        void onDraw(final Canvas canvas) {
            Layout layout;
            if (this.mIsInsertModeActive) {
                CharSequence transformedText = this.mTextView.getTransformed();
                if (!(transformedText instanceof InsertModeTransformationMethod.TransformedText) || (layout = this.mTextView.getLayout()) == null) {
                    return;
                }
                InsertModeTransformationMethod.TransformedText insertModeTransformedText = (InsertModeTransformationMethod.TransformedText) transformedText;
                int highlightStart = insertModeTransformedText.getHighlightStart();
                int highlightEnd = insertModeTransformedText.getHighlightEnd();
                Layout.SelectionRectangleConsumer consumer = new Layout.SelectionRectangleConsumer() { // from class: android.widget.Editor$InsertModeController$$ExternalSyntheticLambda0
                    @Override // android.text.Layout.SelectionRectangleConsumer
                    public final void accept(float f, float f2, float f3, float f4, int i) {
                        Editor.InsertModeController.this.lambda$onDraw$0(canvas, f, f2, f3, f4, i);
                    }
                };
                layout.getSelection(highlightStart, highlightEnd, consumer);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDraw$0(Canvas canvas, float left, float top, float right, float bottom, int textSelectionLayout) {
            canvas.drawRect(left, top, right, bottom, this.mHighlightPaint);
        }

        TransformationMethod updateTransformationMethod(TransformationMethod oldTransformationMethod) {
            if (this.mIsInsertModeActive) {
                InsertModeTransformationMethod update = this.mInsertModeTransformationMethod.update(oldTransformationMethod, this.mTextView.isSingleLine());
                this.mInsertModeTransformationMethod = update;
                return update;
            }
            return oldTransformationMethod;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean enterInsertMode(int offset) {
        if (this.mInsertModeController == null) {
            TextView textView = this.mTextView;
            if (textView == null) {
                return false;
            }
            this.mInsertModeController = new InsertModeController(textView);
        }
        return this.mInsertModeController.enterInsertMode(offset);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void exitInsertMode() {
        InsertModeController insertModeController = this.mInsertModeController;
        if (insertModeController == null) {
            return;
        }
        insertModeController.exitInsertMode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTransformationMethod(TransformationMethod method) {
        InsertModeController insertModeController = this.mInsertModeController;
        if (insertModeController == null || !insertModeController.mIsInsertModeActive) {
            this.mTextView.setTransformationMethodInternal(method);
            return;
        }
        int selectionStart = this.mTextView.getSelectionStart();
        int selectionEnd = this.mTextView.getSelectionEnd();
        this.mTextView.setTransformationMethodInternal(this.mInsertModeController.updateTransformationMethod(method));
        Selection.setSelection((Spannable) this.mTextView.getText(), selectionStart, selectionEnd);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onInitializeSmartActionsAccessibilityNodeInfo(AccessibilityNodeInfo nodeInfo) {
        this.mA11ySmartActions.onInitializeAccessibilityNodeInfo(nodeInfo);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean performSmartActionsAccessibilityAction(int actionId) {
        return this.mA11ySmartActions.performAccessibilityAction(actionId);
    }

    static void logCursor(String location, String msgFormat, Object... msgArgs) {
        if (msgFormat != null) {
            Log.m112d("Editor", location + ": " + String.format(msgFormat, msgArgs));
        } else {
            Log.m112d("Editor", location);
        }
    }
}
