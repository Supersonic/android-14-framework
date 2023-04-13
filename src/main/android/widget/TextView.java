package android.widget;

import android.C0001R;
import android.Manifest;
import android.app.PendingIntent;
import android.app.compat.CompatChanges;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.UndoManager;
import android.content.p001pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BaseCanvas;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.FontListParser;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.text.LineBreakConfig;
import android.icu.text.DecimalFormatSymbols;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.AsyncTask;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.DropBoxManager;
import android.p008os.Handler;
import android.p008os.LocaleList;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ParcelableParcel;
import android.p008os.Process;
import android.p008os.SystemClock;
import android.p008os.UserHandle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.BoringLayout;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.GetChars;
import android.text.GraphemeClusterSegmentFinder;
import android.text.GraphicsOperations;
import android.text.Highlights;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.ParcelableSpan;
import android.text.PrecomputedText;
import android.text.SegmentFinder;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.WordSegmentFinder;
import android.text.method.AllCapsTransformationMethod;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.DateKeyListener;
import android.text.method.DateTimeKeyListener;
import android.text.method.DialerKeyListener;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.LinkMovementMethod;
import android.text.method.MetaKeyKeyListener;
import android.text.method.MovementMethod;
import android.text.method.OffsetMapping;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TextKeyListener;
import android.text.method.TimeKeyListener;
import android.text.method.TransformationMethod;
import android.text.method.TransformationMethod2;
import android.text.method.WordIterator;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ParagraphStyle;
import android.text.style.SpellCheckSpan;
import android.text.style.SuggestionSpan;
import android.text.style.URLSpan;
import android.text.style.UpdateAppearance;
import android.text.util.Linkify;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FeatureFlagUtils;
import android.util.IntArray;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.AccessibilityIterators;
import android.view.ActionMode;
import android.view.Choreographer;
import android.view.ContentInfo;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View$InspectionCompanion$$ExternalSyntheticLambda0;
import android.view.View$InspectionCompanion$$ExternalSyntheticLambda1;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewHierarchyEncoder;
import android.view.ViewParent;
import android.view.ViewRootImpl;
import android.view.ViewStructure;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.view.autofill.Helper;
import android.view.contentcapture.ContentCaptureManager;
import android.view.contentcapture.ContentCaptureSession;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.DeleteGesture;
import android.view.inputmethod.DeleteRangeGesture;
import android.view.inputmethod.EditorBoundsInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.HandwritingGesture;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InsertGesture;
import android.view.inputmethod.InsertModeGesture;
import android.view.inputmethod.JoinOrSplitGesture;
import android.view.inputmethod.PreviewableHandwritingGesture;
import android.view.inputmethod.RemoveSpaceGesture;
import android.view.inputmethod.SelectGesture;
import android.view.inputmethod.SelectRangeGesture;
import android.view.inputmethod.TextAppearanceInfo;
import android.view.inputmethod.TextBoundsInfo;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.IntFlagMapping;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassificationContext;
import android.view.textclassifier.TextClassificationManager;
import android.view.textclassifier.TextClassifier;
import android.view.textclassifier.TextLinks;
import android.view.textservice.SpellCheckerSubtype;
import android.view.textservice.TextServicesManager;
import android.view.translation.TranslationRequestValue;
import android.view.translation.UiTranslationController;
import android.view.translation.ViewTranslationCallback;
import android.view.translation.ViewTranslationRequest;
import android.widget.AccessibilityIterators;
import android.widget.Editor;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
import com.android.internal.accessibility.util.AccessibilityUtils;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.inputmethod.EditableInputConnection;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastMath;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParserException;
@RemoteViews.RemoteView
/* loaded from: classes4.dex */
public class TextView extends View implements ViewTreeObserver.OnPreDrawListener {
    static final int ACCESSIBILITY_ACTION_PROCESS_TEXT_START_ID = 268435712;
    private static final int ACCESSIBILITY_ACTION_SHARE = 268435456;
    static final int ACCESSIBILITY_ACTION_SMART_START_ID = 268439552;
    private static final int ANIMATED_SCROLL_GAP = 250;
    public static final int AUTO_SIZE_TEXT_TYPE_NONE = 0;
    public static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 1;
    public static final long BORINGLAYOUT_FALLBACK_LINESPACING = 210923482;
    private static final int CHANGE_WATCHER_PRIORITY = 100;
    static final boolean DEBUG_CURSOR = false;
    static final boolean DEBUG_EXTRACT = false;
    private static final int DECIMAL = 4;
    private static final int DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX = 1;
    private static final int DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP = 112;
    private static final int DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP = 12;
    private static final int DEFAULT_LINE_BREAK_STYLE = 0;
    private static final int DEFAULT_LINE_BREAK_WORD_STYLE = 0;
    private static final int DEFAULT_TYPEFACE = -1;
    private static final int DEVICE_PROVISIONED_NO = 1;
    private static final int DEVICE_PROVISIONED_UNKNOWN = 0;
    private static final int DEVICE_PROVISIONED_YES = 2;
    private static final int ELLIPSIZE_END = 3;
    private static final int ELLIPSIZE_MARQUEE = 4;
    private static final int ELLIPSIZE_MIDDLE = 2;
    private static final int ELLIPSIZE_NONE = 0;
    private static final int ELLIPSIZE_NOT_SET = -1;
    private static final int ELLIPSIZE_START = 1;
    private static final int EMS = 1;
    private static final int FALLBACK_LINE_SPACING_ALL = 2;
    private static final int FALLBACK_LINE_SPACING_NONE = 0;
    private static final int FALLBACK_LINE_SPACING_STATIC_LAYOUT_ONLY = 1;
    private static final int FLOATING_TOOLBAR_SELECT_ALL_REFRESH_DELAY = 500;
    public static final int FOCUSED_SEARCH_RESULT_INDEX_NONE = -1;
    static final int ID_ASSIST = 16908353;
    static final int ID_AUTOFILL = 16908355;
    static final int ID_COPY = 16908321;
    static final int ID_CUT = 16908320;
    static final int ID_PASTE = 16908322;
    static final int ID_PASTE_AS_PLAIN_TEXT = 16908337;
    static final int ID_REDO = 16908339;
    static final int ID_REPLACE = 16908340;
    static final int ID_SELECT_ALL = 16908319;
    static final int ID_SHARE = 16908341;
    static final int ID_UNDO = 16908338;
    private static final int KEY_DOWN_HANDLED_BY_KEY_LISTENER = 1;
    private static final int KEY_DOWN_HANDLED_BY_MOVEMENT_METHOD = 2;
    private static final int KEY_EVENT_HANDLED = -1;
    private static final int KEY_EVENT_NOT_HANDLED = 0;
    private static final int LINES = 1;
    static final String LOG_TAG = "TextView";
    private static final int MARQUEE_FADE_NORMAL = 0;
    private static final int MARQUEE_FADE_SWITCH_SHOW_ELLIPSIS = 1;
    private static final int MARQUEE_FADE_SWITCH_SHOW_FADE = 2;
    private static final int MAX_LENGTH_FOR_SINGLE_LINE_EDIT_TEXT = 5000;
    private static final int MONOSPACE = 3;
    private static final int NO_POINTER_ID = -1;
    private static final int PIXELS = 2;
    public static final int PROCESS_TEXT_REQUEST_CODE = 100;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int SIGNED = 2;
    public static final long STATICLAYOUT_FALLBACK_LINESPACING = 37756858;
    private static final int TRANSFORMATION_SPAN_PRIORITY = 200;
    public static final BoringLayout.Metrics UNKNOWN_BORING;
    private static final float UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE = -1.0f;
    static final int VERY_WIDE = 1048576;
    private static final SparseIntArray sAppearanceValues;
    static long sLastCutCopyOrTextChangedTime;
    private boolean mAllowTransformationLengthChange;
    private int mAutoLinkMask;
    private float mAutoSizeMaxTextSizeInPx;
    private float mAutoSizeMinTextSizeInPx;
    private float mAutoSizeStepGranularityInPx;
    private int[] mAutoSizeTextSizesInPx;
    private int mAutoSizeTextType;
    private BoringLayout.Metrics mBoring;
    private int mBreakStrategy;
    private BufferType mBufferType;
    private ChangeWatcher mChangeWatcher;
    private CharWrapper mCharWrapper;
    private int mCurHintTextColor;
    @ViewDebug.ExportedProperty(category = "text")
    private int mCurTextColor;
    private volatile Locale mCurrentSpellCheckerLocaleCache;
    private Drawable mCursorDrawable;
    int mCursorDrawableRes;
    private boolean mCursorVisibleFromAttr;
    private int mDeferScroll;
    private int mDesiredHeightAtMeasure;
    private int mDeviceProvisionedState;
    Drawables mDrawables;
    private Editable.Factory mEditableFactory;
    private Editor mEditor;
    private TextUtils.TruncateAt mEllipsize;
    private InputFilter[] mFilters;
    private int mFocusedSearchResultHighlightColor;
    private Paint mFocusedSearchResultHighlightPaint;
    private int mFocusedSearchResultIndex;
    private int mFontWeightAdjustment;
    private boolean mFreezesText;
    private int mGesturePreviewHighlightEnd;
    private Paint mGesturePreviewHighlightPaint;
    private int mGesturePreviewHighlightStart;
    @ViewDebug.ExportedProperty(category = "text")
    private int mGravity;
    private boolean mHasPresetAutoSizeValues;
    int mHighlightColor;
    private final Paint mHighlightPaint;
    private List<Paint> mHighlightPaints;
    private Path mHighlightPath;
    private boolean mHighlightPathBogus;
    private List<Path> mHighlightPaths;
    private boolean mHighlightPathsBogus;
    private Highlights mHighlights;
    private CharSequence mHint;
    private BoringLayout.Metrics mHintBoring;
    private int mHintId;
    private Layout mHintLayout;
    private ColorStateList mHintTextColor;
    private boolean mHorizontallyScrolling;
    private int mHyphenationFrequency;
    private boolean mImeIsConsumingInput;
    private boolean mIncludePad;
    private boolean mIsPrimePointerFromHandleView;
    private int mJustificationMode;
    private int mLastInputSource;
    private int mLastLayoutDirection;
    private long mLastScroll;
    private Layout mLayout;
    private int mLineBreakStyle;
    private int mLineBreakWordStyle;
    private ColorStateList mLinkTextColor;
    private boolean mLinksClickable;
    private boolean mListenerChanged;
    private ArrayList<TextWatcher> mListeners;
    private boolean mLocalesChanged;
    private Marquee mMarquee;
    private int mMarqueeFadeMode;
    private int mMarqueeRepeatLimit;
    private int mMaxMode;
    private int mMaxWidth;
    private int mMaxWidthMode;
    private int mMaximum;
    private int mMinMode;
    private int mMinWidth;
    private int mMinWidthMode;
    private int mMinimum;
    private MovementMethod mMovement;
    private boolean mNeedsAutoSizeText;
    private int mOldMaxMode;
    private int mOldMaximum;
    private Typeface mOriginalTypeface;
    private final List<Path> mPathRecyclePool;
    private boolean mPreDrawListenerDetached;
    private boolean mPreDrawRegistered;
    private PrecomputedText mPrecomputed;
    private boolean mPreventDefaultMovement;
    private int mPrimePointerId;
    private boolean mRestartMarquee;
    private BoringLayout mSavedHintLayout;
    private BoringLayout mSavedLayout;
    private Layout mSavedMarqueeModeLayout;
    private Scroller mScroller;
    private int mSearchResultHighlightColor;
    private Paint mSearchResultHighlightPaint;
    private int[] mSearchResultHighlights;
    private int mShadowColor;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private boolean mSingleLine;
    private InputFilter.LengthFilter mSingleLineLengthFilter;
    private float mSpacingAdd;
    private float mSpacingMult;
    private Spannable mSpannable;
    private Spannable.Factory mSpannableFactory;
    private Object mTempCursor;
    private Rect mTempRect;
    private TextPaint mTempTextPaint;
    @ViewDebug.ExportedProperty(category = "text")
    private CharSequence mText;
    private TextClassificationContext mTextClassificationContext;
    private TextClassifier mTextClassificationSession;
    private TextClassifier mTextClassifier;
    private ColorStateList mTextColor;
    private TextDirectionHeuristic mTextDir;
    int mTextEditSuggestionContainerLayout;
    int mTextEditSuggestionHighlightStyle;
    int mTextEditSuggestionItemLayout;
    private int mTextId;
    private UserHandle mTextOperationUser;
    private final TextPaint mTextPaint;
    private Drawable mTextSelectHandle;
    private Drawable mTextSelectHandleLeft;
    int mTextSelectHandleLeftRes;
    int mTextSelectHandleRes;
    private Drawable mTextSelectHandleRight;
    int mTextSelectHandleRightRes;
    private boolean mTextSetFromXmlOrResourceId;
    private int mTextSizeUnit;
    private TransformationMethod mTransformation;
    private CharSequence mTransformed;
    private int mUseFallbackLineSpacing;
    private final boolean mUseInternationalizedInput;
    private final boolean mUseTextPaddingForUiTranslation;
    private boolean mUserSetTextScaleX;
    private boolean mUserSpeficiedLineBreakwordStyle;
    private Pattern mWhitespacePattern;
    private static final float[] TEMP_POSITION = new float[2];
    private static final RectF TEMP_RECTF = new RectF();
    private static final InputFilter[] NO_FILTERS = new InputFilter[0];
    private static final Spanned EMPTY_SPANNED = new SpannedString("");
    private static final int[] MULTILINE_STATE_SET = {16843597};

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface AutoSizeTextType {
    }

    /* loaded from: classes4.dex */
    public enum BufferType {
        NORMAL,
        SPANNABLE,
        EDITABLE
    }

    /* loaded from: classes4.dex */
    public interface OnEditorActionListener {
        boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface XMLTypefaceAttr {
    }

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<TextView> {
        private int mAutoLinkId;
        private int mAutoSizeMaxTextSizeId;
        private int mAutoSizeMinTextSizeId;
        private int mAutoSizeStepGranularityId;
        private int mAutoSizeTextTypeId;
        private int mBreakStrategyId;
        private int mCursorVisibleId;
        private int mDrawableBlendModeId;
        private int mDrawablePaddingId;
        private int mDrawableTintId;
        private int mDrawableTintModeId;
        private int mElegantTextHeightId;
        private int mEllipsizeId;
        private int mFallbackLineSpacingId;
        private int mFirstBaselineToTopHeightId;
        private int mFontFeatureSettingsId;
        private int mFreezesTextId;
        private int mGravityId;
        private int mHintId;
        private int mHyphenationFrequencyId;
        private int mImeActionIdId;
        private int mImeActionLabelId;
        private int mImeOptionsId;
        private int mIncludeFontPaddingId;
        private int mInputTypeId;
        private int mJustificationModeId;
        private int mLastBaselineToBottomHeightId;
        private int mLetterSpacingId;
        private int mLineHeightId;
        private int mLineSpacingExtraId;
        private int mLineSpacingMultiplierId;
        private int mLinksClickableId;
        private int mMarqueeRepeatLimitId;
        private int mMaxEmsId;
        private int mMaxHeightId;
        private int mMaxLinesId;
        private int mMaxWidthId;
        private int mMinEmsId;
        private int mMinLinesId;
        private int mMinWidthId;
        private int mPrivateImeOptionsId;
        private boolean mPropertiesMapped = false;
        private int mScrollHorizontallyId;
        private int mShadowColorId;
        private int mShadowDxId;
        private int mShadowDyId;
        private int mShadowRadiusId;
        private int mSingleLineId;
        private int mTextAllCapsId;
        private int mTextColorHighlightId;
        private int mTextColorHintId;
        private int mTextColorId;
        private int mTextColorLinkId;
        private int mTextId;
        private int mTextIsSelectableId;
        private int mTextScaleXId;
        private int mTextSizeId;
        private int mTypefaceId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            IntFlagMapping autoLinkFlagMapping = new IntFlagMapping();
            autoLinkFlagMapping.add(2, 2, "email");
            autoLinkFlagMapping.add(8, 8, "map");
            autoLinkFlagMapping.add(4, 4, "phone");
            autoLinkFlagMapping.add(1, 1, "web");
            Objects.requireNonNull(autoLinkFlagMapping);
            this.mAutoLinkId = propertyMapper.mapIntFlag("autoLink", 16842928, new View$InspectionCompanion$$ExternalSyntheticLambda1(autoLinkFlagMapping));
            this.mAutoSizeMaxTextSizeId = propertyMapper.mapInt("autoSizeMaxTextSize", 16844102);
            this.mAutoSizeMinTextSizeId = propertyMapper.mapInt("autoSizeMinTextSize", 16844088);
            this.mAutoSizeStepGranularityId = propertyMapper.mapInt("autoSizeStepGranularity", 16844086);
            SparseArray<String> autoSizeTextTypeEnumMapping = new SparseArray<>();
            autoSizeTextTypeEnumMapping.put(0, "none");
            autoSizeTextTypeEnumMapping.put(1, "uniform");
            Objects.requireNonNull(autoSizeTextTypeEnumMapping);
            this.mAutoSizeTextTypeId = propertyMapper.mapIntEnum("autoSizeTextType", 16844085, new View$InspectionCompanion$$ExternalSyntheticLambda0(autoSizeTextTypeEnumMapping));
            SparseArray<String> breakStrategyEnumMapping = new SparseArray<>();
            breakStrategyEnumMapping.put(0, "simple");
            breakStrategyEnumMapping.put(1, "high_quality");
            breakStrategyEnumMapping.put(2, RecognizerIntent.LANGUAGE_SWITCH_BALANCED);
            Objects.requireNonNull(breakStrategyEnumMapping);
            this.mBreakStrategyId = propertyMapper.mapIntEnum("breakStrategy", 16843997, new View$InspectionCompanion$$ExternalSyntheticLambda0(breakStrategyEnumMapping));
            this.mCursorVisibleId = propertyMapper.mapBoolean("cursorVisible", 16843090);
            this.mDrawableBlendModeId = propertyMapper.mapObject("drawableBlendMode", 80);
            this.mDrawablePaddingId = propertyMapper.mapInt("drawablePadding", 16843121);
            this.mDrawableTintId = propertyMapper.mapObject("drawableTint", 16843990);
            this.mDrawableTintModeId = propertyMapper.mapObject("drawableTintMode", 16843991);
            this.mElegantTextHeightId = propertyMapper.mapBoolean("elegantTextHeight", 16843869);
            this.mEllipsizeId = propertyMapper.mapObject("ellipsize", 16842923);
            this.mFallbackLineSpacingId = propertyMapper.mapBoolean("fallbackLineSpacing", 16844155);
            this.mFirstBaselineToTopHeightId = propertyMapper.mapInt("firstBaselineToTopHeight", 16844157);
            this.mFontFeatureSettingsId = propertyMapper.mapObject("fontFeatureSettings", 16843959);
            this.mFreezesTextId = propertyMapper.mapBoolean("freezesText", 16843116);
            this.mGravityId = propertyMapper.mapGravity("gravity", 16842927);
            this.mHintId = propertyMapper.mapObject("hint", 16843088);
            SparseArray<String> hyphenationFrequencyEnumMapping = new SparseArray<>();
            hyphenationFrequencyEnumMapping.put(0, "none");
            hyphenationFrequencyEnumMapping.put(1, FontListParser.STYLE_NORMAL);
            hyphenationFrequencyEnumMapping.put(2, "full");
            Objects.requireNonNull(hyphenationFrequencyEnumMapping);
            this.mHyphenationFrequencyId = propertyMapper.mapIntEnum("hyphenationFrequency", 16843998, new View$InspectionCompanion$$ExternalSyntheticLambda0(hyphenationFrequencyEnumMapping));
            this.mImeActionIdId = propertyMapper.mapInt("imeActionId", 16843366);
            this.mImeActionLabelId = propertyMapper.mapObject("imeActionLabel", 16843365);
            IntFlagMapping imeOptionsFlagMapping = new IntFlagMapping();
            imeOptionsFlagMapping.add(255, 6, "actionDone");
            imeOptionsFlagMapping.add(255, 2, "actionGo");
            imeOptionsFlagMapping.add(255, 5, "actionNext");
            imeOptionsFlagMapping.add(255, 1, "actionNone");
            imeOptionsFlagMapping.add(255, 7, "actionPrevious");
            imeOptionsFlagMapping.add(255, 3, "actionSearch");
            imeOptionsFlagMapping.add(255, 4, "actionSend");
            imeOptionsFlagMapping.add(255, 0, "actionUnspecified");
            imeOptionsFlagMapping.add(Integer.MIN_VALUE, Integer.MIN_VALUE, "flagForceAscii");
            imeOptionsFlagMapping.add(134217728, 134217728, "flagNavigateNext");
            imeOptionsFlagMapping.add(67108864, 67108864, "flagNavigatePrevious");
            imeOptionsFlagMapping.add(536870912, 536870912, "flagNoAccessoryAction");
            imeOptionsFlagMapping.add(1073741824, 1073741824, "flagNoEnterAction");
            imeOptionsFlagMapping.add(268435456, 268435456, "flagNoExtractUi");
            imeOptionsFlagMapping.add(33554432, 33554432, "flagNoFullscreen");
            imeOptionsFlagMapping.add(16777216, 16777216, "flagNoPersonalizedLearning");
            imeOptionsFlagMapping.add(-1, 0, FontListParser.STYLE_NORMAL);
            Objects.requireNonNull(imeOptionsFlagMapping);
            this.mImeOptionsId = propertyMapper.mapIntFlag("imeOptions", 16843364, new View$InspectionCompanion$$ExternalSyntheticLambda1(imeOptionsFlagMapping));
            this.mIncludeFontPaddingId = propertyMapper.mapBoolean("includeFontPadding", 16843103);
            IntFlagMapping inputTypeFlagMapping = new IntFlagMapping();
            inputTypeFlagMapping.add(4095, 20, "date");
            inputTypeFlagMapping.add(4095, 4, TextClassifier.TYPE_DATE_TIME);
            inputTypeFlagMapping.add(-1, 0, "none");
            inputTypeFlagMapping.add(4095, 2, "number");
            inputTypeFlagMapping.add(16773135, 8194, "numberDecimal");
            inputTypeFlagMapping.add(4095, 18, "numberPassword");
            inputTypeFlagMapping.add(16773135, 4098, "numberSigned");
            inputTypeFlagMapping.add(4095, 3, "phone");
            inputTypeFlagMapping.add(4095, 1, "text");
            inputTypeFlagMapping.add(16773135, 65537, "textAutoComplete");
            inputTypeFlagMapping.add(16773135, 32769, "textAutoCorrect");
            inputTypeFlagMapping.add(16773135, 4097, "textCapCharacters");
            inputTypeFlagMapping.add(16773135, 16385, "textCapSentences");
            inputTypeFlagMapping.add(16773135, 8193, "textCapWords");
            inputTypeFlagMapping.add(4095, 33, "textEmailAddress");
            inputTypeFlagMapping.add(4095, 49, "textEmailSubject");
            inputTypeFlagMapping.add(4095, 177, "textFilter");
            inputTypeFlagMapping.add(16773135, 262145, "textImeMultiLine");
            inputTypeFlagMapping.add(4095, 81, "textLongMessage");
            inputTypeFlagMapping.add(16773135, 131073, "textMultiLine");
            inputTypeFlagMapping.add(16773135, 524289, "textNoSuggestions");
            inputTypeFlagMapping.add(4095, 129, "textPassword");
            inputTypeFlagMapping.add(4095, 97, "textPersonName");
            inputTypeFlagMapping.add(4095, 193, "textPhonetic");
            inputTypeFlagMapping.add(4095, 113, "textPostalAddress");
            inputTypeFlagMapping.add(4095, 65, "textShortMessage");
            inputTypeFlagMapping.add(4095, 17, "textUri");
            inputTypeFlagMapping.add(4095, 145, "textVisiblePassword");
            inputTypeFlagMapping.add(4095, 161, "textWebEditText");
            inputTypeFlagMapping.add(4095, 209, "textWebEmailAddress");
            inputTypeFlagMapping.add(4095, 225, "textWebPassword");
            inputTypeFlagMapping.add(4095, 36, DropBoxManager.EXTRA_TIME);
            Objects.requireNonNull(inputTypeFlagMapping);
            this.mInputTypeId = propertyMapper.mapIntFlag("inputType", 16843296, new View$InspectionCompanion$$ExternalSyntheticLambda1(inputTypeFlagMapping));
            SparseArray<String> justificationModeEnumMapping = new SparseArray<>();
            justificationModeEnumMapping.put(0, "none");
            justificationModeEnumMapping.put(1, "inter_word");
            Objects.requireNonNull(justificationModeEnumMapping);
            this.mJustificationModeId = propertyMapper.mapIntEnum("justificationMode", 16844135, new View$InspectionCompanion$$ExternalSyntheticLambda0(justificationModeEnumMapping));
            this.mLastBaselineToBottomHeightId = propertyMapper.mapInt("lastBaselineToBottomHeight", 16844158);
            this.mLetterSpacingId = propertyMapper.mapFloat("letterSpacing", 16843958);
            this.mLineHeightId = propertyMapper.mapInt("lineHeight", 16844159);
            this.mLineSpacingExtraId = propertyMapper.mapFloat("lineSpacingExtra", 16843287);
            this.mLineSpacingMultiplierId = propertyMapper.mapFloat("lineSpacingMultiplier", 16843288);
            this.mLinksClickableId = propertyMapper.mapBoolean("linksClickable", 16842929);
            this.mMarqueeRepeatLimitId = propertyMapper.mapInt("marqueeRepeatLimit", 16843293);
            this.mMaxEmsId = propertyMapper.mapInt("maxEms", 16843095);
            this.mMaxHeightId = propertyMapper.mapInt("maxHeight", 16843040);
            this.mMaxLinesId = propertyMapper.mapInt("maxLines", 16843091);
            this.mMaxWidthId = propertyMapper.mapInt("maxWidth", 16843039);
            this.mMinEmsId = propertyMapper.mapInt("minEms", 16843098);
            this.mMinLinesId = propertyMapper.mapInt("minLines", 16843094);
            this.mMinWidthId = propertyMapper.mapInt("minWidth", 16843071);
            this.mPrivateImeOptionsId = propertyMapper.mapObject("privateImeOptions", 16843299);
            this.mScrollHorizontallyId = propertyMapper.mapBoolean("scrollHorizontally", 16843099);
            this.mShadowColorId = propertyMapper.mapColor("shadowColor", 16843105);
            this.mShadowDxId = propertyMapper.mapFloat("shadowDx", 16843106);
            this.mShadowDyId = propertyMapper.mapFloat("shadowDy", 16843107);
            this.mShadowRadiusId = propertyMapper.mapFloat("shadowRadius", 16843108);
            this.mSingleLineId = propertyMapper.mapBoolean("singleLine", 16843101);
            this.mTextId = propertyMapper.mapObject("text", 16843087);
            this.mTextAllCapsId = propertyMapper.mapBoolean("textAllCaps", 16843660);
            this.mTextColorId = propertyMapper.mapObject("textColor", 16842904);
            this.mTextColorHighlightId = propertyMapper.mapColor("textColorHighlight", 16842905);
            this.mTextColorHintId = propertyMapper.mapObject("textColorHint", 16842906);
            this.mTextColorLinkId = propertyMapper.mapObject("textColorLink", 16842907);
            this.mTextIsSelectableId = propertyMapper.mapBoolean("textIsSelectable", 16843542);
            this.mTextScaleXId = propertyMapper.mapFloat("textScaleX", 16843089);
            this.mTextSizeId = propertyMapper.mapFloat("textSize", 16842901);
            this.mTypefaceId = propertyMapper.mapObject("typeface", 16842902);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(TextView node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readIntFlag(this.mAutoLinkId, node.getAutoLinkMask());
            propertyReader.readInt(this.mAutoSizeMaxTextSizeId, node.getAutoSizeMaxTextSize());
            propertyReader.readInt(this.mAutoSizeMinTextSizeId, node.getAutoSizeMinTextSize());
            propertyReader.readInt(this.mAutoSizeStepGranularityId, node.getAutoSizeStepGranularity());
            propertyReader.readIntEnum(this.mAutoSizeTextTypeId, node.getAutoSizeTextType());
            propertyReader.readIntEnum(this.mBreakStrategyId, node.getBreakStrategy());
            propertyReader.readBoolean(this.mCursorVisibleId, node.isCursorVisible());
            propertyReader.readObject(this.mDrawableBlendModeId, node.getCompoundDrawableTintBlendMode());
            propertyReader.readInt(this.mDrawablePaddingId, node.getCompoundDrawablePadding());
            propertyReader.readObject(this.mDrawableTintId, node.getCompoundDrawableTintList());
            propertyReader.readObject(this.mDrawableTintModeId, node.getCompoundDrawableTintMode());
            propertyReader.readBoolean(this.mElegantTextHeightId, node.isElegantTextHeight());
            propertyReader.readObject(this.mEllipsizeId, node.getEllipsize());
            propertyReader.readBoolean(this.mFallbackLineSpacingId, node.isFallbackLineSpacing());
            propertyReader.readInt(this.mFirstBaselineToTopHeightId, node.getFirstBaselineToTopHeight());
            propertyReader.readObject(this.mFontFeatureSettingsId, node.getFontFeatureSettings());
            propertyReader.readBoolean(this.mFreezesTextId, node.getFreezesText());
            propertyReader.readGravity(this.mGravityId, node.getGravity());
            propertyReader.readObject(this.mHintId, node.getHint());
            propertyReader.readIntEnum(this.mHyphenationFrequencyId, node.getHyphenationFrequency());
            propertyReader.readInt(this.mImeActionIdId, node.getImeActionId());
            propertyReader.readObject(this.mImeActionLabelId, node.getImeActionLabel());
            propertyReader.readIntFlag(this.mImeOptionsId, node.getImeOptions());
            propertyReader.readBoolean(this.mIncludeFontPaddingId, node.getIncludeFontPadding());
            propertyReader.readIntFlag(this.mInputTypeId, node.getInputType());
            propertyReader.readIntEnum(this.mJustificationModeId, node.getJustificationMode());
            propertyReader.readInt(this.mLastBaselineToBottomHeightId, node.getLastBaselineToBottomHeight());
            propertyReader.readFloat(this.mLetterSpacingId, node.getLetterSpacing());
            propertyReader.readInt(this.mLineHeightId, node.getLineHeight());
            propertyReader.readFloat(this.mLineSpacingExtraId, node.getLineSpacingExtra());
            propertyReader.readFloat(this.mLineSpacingMultiplierId, node.getLineSpacingMultiplier());
            propertyReader.readBoolean(this.mLinksClickableId, node.getLinksClickable());
            propertyReader.readInt(this.mMarqueeRepeatLimitId, node.getMarqueeRepeatLimit());
            propertyReader.readInt(this.mMaxEmsId, node.getMaxEms());
            propertyReader.readInt(this.mMaxHeightId, node.getMaxHeight());
            propertyReader.readInt(this.mMaxLinesId, node.getMaxLines());
            propertyReader.readInt(this.mMaxWidthId, node.getMaxWidth());
            propertyReader.readInt(this.mMinEmsId, node.getMinEms());
            propertyReader.readInt(this.mMinLinesId, node.getMinLines());
            propertyReader.readInt(this.mMinWidthId, node.getMinWidth());
            propertyReader.readObject(this.mPrivateImeOptionsId, node.getPrivateImeOptions());
            propertyReader.readBoolean(this.mScrollHorizontallyId, node.isHorizontallyScrollable());
            propertyReader.readColor(this.mShadowColorId, node.getShadowColor());
            propertyReader.readFloat(this.mShadowDxId, node.getShadowDx());
            propertyReader.readFloat(this.mShadowDyId, node.getShadowDy());
            propertyReader.readFloat(this.mShadowRadiusId, node.getShadowRadius());
            propertyReader.readBoolean(this.mSingleLineId, node.isSingleLine());
            propertyReader.readObject(this.mTextId, node.getText());
            propertyReader.readBoolean(this.mTextAllCapsId, node.isAllCaps());
            propertyReader.readObject(this.mTextColorId, node.getTextColors());
            propertyReader.readColor(this.mTextColorHighlightId, node.getHighlightColor());
            propertyReader.readObject(this.mTextColorHintId, node.getHintTextColors());
            propertyReader.readObject(this.mTextColorLinkId, node.getLinkTextColors());
            propertyReader.readBoolean(this.mTextIsSelectableId, node.isTextSelectable());
            propertyReader.readFloat(this.mTextScaleXId, node.getTextScaleX());
            propertyReader.readFloat(this.mTextSizeId, node.getTextSize());
            propertyReader.readObject(this.mTypefaceId, node.getTypeface());
        }
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sAppearanceValues = sparseIntArray;
        sparseIntArray.put(6, 4);
        sparseIntArray.put(101, 22);
        sparseIntArray.put(102, 23);
        sparseIntArray.put(5, 3);
        sparseIntArray.put(7, 5);
        sparseIntArray.put(8, 6);
        sparseIntArray.put(2, 0);
        sparseIntArray.put(96, 19);
        sparseIntArray.put(3, 1);
        sparseIntArray.put(75, 12);
        sparseIntArray.put(4, 2);
        sparseIntArray.put(95, 18);
        sparseIntArray.put(72, 11);
        sparseIntArray.put(36, 7);
        sparseIntArray.put(37, 8);
        sparseIntArray.put(38, 9);
        sparseIntArray.put(39, 10);
        sparseIntArray.put(76, 13);
        sparseIntArray.put(91, 17);
        sparseIntArray.put(77, 14);
        sparseIntArray.put(78, 15);
        sparseIntArray.put(90, 16);
        sparseIntArray.put(97, 20);
        sparseIntArray.put(98, 21);
        UNKNOWN_BORING = new BoringLayout.Metrics();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class Drawables {
        static final int BOTTOM = 3;
        static final int DRAWABLE_LEFT = 1;
        static final int DRAWABLE_NONE = -1;
        static final int DRAWABLE_RIGHT = 0;
        static final int LEFT = 0;
        static final int RIGHT = 2;
        static final int TOP = 1;
        BlendMode mBlendMode;
        Drawable mDrawableEnd;
        Drawable mDrawableError;
        int mDrawableHeightEnd;
        int mDrawableHeightError;
        int mDrawableHeightLeft;
        int mDrawableHeightRight;
        int mDrawableHeightStart;
        int mDrawableHeightTemp;
        Drawable mDrawableLeftInitial;
        int mDrawablePadding;
        Drawable mDrawableRightInitial;
        int mDrawableSizeBottom;
        int mDrawableSizeEnd;
        int mDrawableSizeError;
        int mDrawableSizeLeft;
        int mDrawableSizeRight;
        int mDrawableSizeStart;
        int mDrawableSizeTemp;
        int mDrawableSizeTop;
        Drawable mDrawableStart;
        Drawable mDrawableTemp;
        int mDrawableWidthBottom;
        int mDrawableWidthTop;
        boolean mHasTint;
        boolean mHasTintMode;
        boolean mIsRtlCompatibilityMode;
        boolean mOverride;
        ColorStateList mTintList;
        final Rect mCompoundRect = new Rect();
        final Drawable[] mShowing = new Drawable[4];
        int mDrawableSaved = -1;

        public Drawables(Context context) {
            int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
            this.mIsRtlCompatibilityMode = targetSdkVersion < 17 || !context.getApplicationInfo().hasRtlSupport();
            this.mOverride = false;
        }

        public boolean hasMetadata() {
            return this.mDrawablePadding != 0 || this.mHasTintMode || this.mHasTint;
        }

        public boolean resolveWithLayoutDirection(int layoutDirection) {
            Drawable[] drawableArr = this.mShowing;
            Drawable previousLeft = drawableArr[0];
            Drawable previousRight = drawableArr[2];
            Drawable drawable = this.mDrawableLeftInitial;
            drawableArr[0] = drawable;
            Drawable drawable2 = this.mDrawableRightInitial;
            drawableArr[2] = drawable2;
            if (this.mIsRtlCompatibilityMode) {
                Drawable drawable3 = this.mDrawableStart;
                if (drawable3 != null && drawable == null) {
                    drawableArr[0] = drawable3;
                    this.mDrawableSizeLeft = this.mDrawableSizeStart;
                    this.mDrawableHeightLeft = this.mDrawableHeightStart;
                }
                Drawable drawable4 = this.mDrawableEnd;
                if (drawable4 != null && drawable2 == null) {
                    drawableArr[2] = drawable4;
                    this.mDrawableSizeRight = this.mDrawableSizeEnd;
                    this.mDrawableHeightRight = this.mDrawableHeightEnd;
                }
            } else {
                switch (layoutDirection) {
                    case 1:
                        if (this.mOverride) {
                            drawableArr[2] = this.mDrawableStart;
                            this.mDrawableSizeRight = this.mDrawableSizeStart;
                            this.mDrawableHeightRight = this.mDrawableHeightStart;
                            drawableArr[0] = this.mDrawableEnd;
                            this.mDrawableSizeLeft = this.mDrawableSizeEnd;
                            this.mDrawableHeightLeft = this.mDrawableHeightEnd;
                            break;
                        }
                        break;
                    default:
                        if (this.mOverride) {
                            drawableArr[0] = this.mDrawableStart;
                            this.mDrawableSizeLeft = this.mDrawableSizeStart;
                            this.mDrawableHeightLeft = this.mDrawableHeightStart;
                            drawableArr[2] = this.mDrawableEnd;
                            this.mDrawableSizeRight = this.mDrawableSizeEnd;
                            this.mDrawableHeightRight = this.mDrawableHeightEnd;
                            break;
                        }
                        break;
                }
            }
            applyErrorDrawableIfNeeded(layoutDirection);
            Drawable[] drawableArr2 = this.mShowing;
            return (drawableArr2[0] == previousLeft && drawableArr2[2] == previousRight) ? false : true;
        }

        public void setErrorDrawable(Drawable dr, TextView tv) {
            Drawable drawable = this.mDrawableError;
            if (drawable != dr && drawable != null) {
                drawable.setCallback(null);
            }
            this.mDrawableError = dr;
            if (dr != null) {
                Rect compoundRect = this.mCompoundRect;
                int[] state = tv.getDrawableState();
                this.mDrawableError.setState(state);
                this.mDrawableError.copyBounds(compoundRect);
                this.mDrawableError.setCallback(tv);
                this.mDrawableSizeError = compoundRect.width();
                this.mDrawableHeightError = compoundRect.height();
                return;
            }
            this.mDrawableHeightError = 0;
            this.mDrawableSizeError = 0;
        }

        private void applyErrorDrawableIfNeeded(int layoutDirection) {
            switch (this.mDrawableSaved) {
                case 0:
                    this.mShowing[2] = this.mDrawableTemp;
                    this.mDrawableSizeRight = this.mDrawableSizeTemp;
                    this.mDrawableHeightRight = this.mDrawableHeightTemp;
                    break;
                case 1:
                    this.mShowing[0] = this.mDrawableTemp;
                    this.mDrawableSizeLeft = this.mDrawableSizeTemp;
                    this.mDrawableHeightLeft = this.mDrawableHeightTemp;
                    break;
            }
            Drawable drawable = this.mDrawableError;
            if (drawable != null) {
                switch (layoutDirection) {
                    case 1:
                        this.mDrawableSaved = 1;
                        Drawable[] drawableArr = this.mShowing;
                        this.mDrawableTemp = drawableArr[0];
                        this.mDrawableSizeTemp = this.mDrawableSizeLeft;
                        this.mDrawableHeightTemp = this.mDrawableHeightLeft;
                        drawableArr[0] = drawable;
                        this.mDrawableSizeLeft = this.mDrawableSizeError;
                        this.mDrawableHeightLeft = this.mDrawableHeightError;
                        return;
                    default:
                        this.mDrawableSaved = 0;
                        Drawable[] drawableArr2 = this.mShowing;
                        this.mDrawableTemp = drawableArr2[2];
                        this.mDrawableSizeTemp = this.mDrawableSizeRight;
                        this.mDrawableHeightTemp = this.mDrawableHeightRight;
                        drawableArr2[2] = drawable;
                        this.mDrawableSizeRight = this.mDrawableSizeError;
                        this.mDrawableHeightRight = this.mDrawableHeightError;
                        return;
                }
            }
        }
    }

    public static void preloadFontCache() {
    }

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842884);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:282:0x1329, code lost:
        if ((r6.mInputType & 4095) == 129) goto L339;
     */
    /* JADX WARN: Removed duplicated region for block: B:233:0x1249  */
    /* JADX WARN: Removed duplicated region for block: B:234:0x1255  */
    /* JADX WARN: Removed duplicated region for block: B:236:0x125f  */
    /* JADX WARN: Removed duplicated region for block: B:239:0x1270  */
    /* JADX WARN: Removed duplicated region for block: B:247:0x1280  */
    /* JADX WARN: Removed duplicated region for block: B:249:0x1289  */
    /* JADX WARN: Removed duplicated region for block: B:250:0x1295  */
    /* JADX WARN: Removed duplicated region for block: B:252:0x129a  */
    /* JADX WARN: Removed duplicated region for block: B:263:0x12de  */
    /* JADX WARN: Removed duplicated region for block: B:268:0x12fd  */
    /* JADX WARN: Removed duplicated region for block: B:269:0x1303  */
    /* JADX WARN: Removed duplicated region for block: B:270:0x1309  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x131d  */
    /* JADX WARN: Removed duplicated region for block: B:286:0x1330  */
    /* JADX WARN: Removed duplicated region for block: B:289:0x1335  */
    /* JADX WARN: Removed duplicated region for block: B:290:0x133d  */
    /* JADX WARN: Removed duplicated region for block: B:293:0x1356  */
    /* JADX WARN: Removed duplicated region for block: B:296:0x1361 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:304:0x1383  */
    /* JADX WARN: Removed duplicated region for block: B:305:0x1390  */
    /* JADX WARN: Removed duplicated region for block: B:311:0x13b2  */
    /* JADX WARN: Removed duplicated region for block: B:312:0x13b7  */
    /* JADX WARN: Removed duplicated region for block: B:315:0x13bd  */
    /* JADX WARN: Removed duplicated region for block: B:317:0x13c1  */
    /* JADX WARN: Removed duplicated region for block: B:320:0x13c8  */
    /* JADX WARN: Removed duplicated region for block: B:323:0x13e5  */
    /* JADX WARN: Removed duplicated region for block: B:329:0x13f1  */
    /* JADX WARN: Removed duplicated region for block: B:335:0x13ff  */
    /* JADX WARN: Removed duplicated region for block: B:342:0x1425  */
    /* JADX WARN: Removed duplicated region for block: B:361:0x1492  */
    /* JADX WARN: Removed duplicated region for block: B:364:0x149f  */
    /* JADX WARN: Removed duplicated region for block: B:367:0x14a8  */
    /* JADX WARN: Removed duplicated region for block: B:368:0x14ad  */
    /* JADX WARN: Removed duplicated region for block: B:371:0x14b4  */
    /* JADX WARN: Removed duplicated region for block: B:389:0x1500  */
    /* JADX WARN: Removed duplicated region for block: B:391:0x1505  */
    /* JADX WARN: Removed duplicated region for block: B:392:0x150b  */
    /* JADX WARN: Removed duplicated region for block: B:394:0x150f  */
    /* JADX WARN: Removed duplicated region for block: B:395:0x1515  */
    /* JADX WARN: Removed duplicated region for block: B:397:0x1519  */
    /* JADX WARN: Removed duplicated region for block: B:398:0x151f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public TextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray appearance;
        boolean phone;
        int inputType;
        BufferType bufferType;
        boolean singleLine;
        Editor editor;
        boolean passwordInputType;
        boolean webPasswordInputType;
        boolean numberPasswordInputType;
        ColorStateList drawableTint;
        boolean z;
        int ellipsize;
        boolean isPassword;
        boolean numberPasswordInputType2;
        TextAppearanceAttributes attributes;
        int maxlength;
        InputFilter.LengthFilter lengthFilter;
        CharSequence charSequence;
        CharSequence hint;
        int n;
        int i;
        int focusable;
        Editor editor2;
        int i2;
        int i3;
        float autoSizeMinTextSizeInPx;
        float autoSizeMaxTextSizeInPx;
        float autoSizeStepGranularityInPx;
        TextAppearanceAttributes attributes2;
        boolean password;
        int i4;
        TextKeyListener.Capitalize cap;
        int firstBaselineToTopHeight;
        int lastBaselineToBottomHeight;
        int n2;
        boolean textIsSetFromXml;
        TextAppearanceAttributes attributes3;
        Resources.Theme theme;
        String str;
        boolean editable;
        int numeric;
        boolean phone2;
        boolean autotext;
        int firstBaselineToTopHeight2;
        int buffertype;
        boolean selectallonfocus;
        BlendMode drawableTintMode;
        int drawablePadding;
        boolean singleLine2;
        boolean textIsSetFromXml2;
        int lineHeight;
        int lineHeight2;
        String str2;
        String str3;
        this.mSingleLineLengthFilter = null;
        this.mEditableFactory = Editable.Factory.getInstance();
        this.mSpannableFactory = Spannable.Factory.getInstance();
        this.mCursorVisibleFromAttr = true;
        this.mMarqueeRepeatLimit = 3;
        this.mLastLayoutDirection = -1;
        this.mMarqueeFadeMode = 0;
        this.mBufferType = BufferType.NORMAL;
        this.mLocalesChanged = false;
        this.mTextSizeUnit = -1;
        this.mLineBreakStyle = 0;
        this.mLineBreakWordStyle = 0;
        this.mUserSpeficiedLineBreakwordStyle = false;
        this.mListenerChanged = false;
        this.mGravity = 8388659;
        this.mLinksClickable = true;
        this.mSpacingMult = 1.0f;
        this.mSpacingAdd = 0.0f;
        this.mMaximum = Integer.MAX_VALUE;
        this.mMaxMode = 1;
        this.mMinimum = 0;
        this.mMinMode = 1;
        this.mOldMaximum = Integer.MAX_VALUE;
        this.mOldMaxMode = 1;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxWidthMode = 2;
        this.mMinWidth = 0;
        this.mMinWidthMode = 2;
        this.mDesiredHeightAtMeasure = -1;
        this.mIncludePad = true;
        this.mDeferScroll = -1;
        this.mFilters = NO_FILTERS;
        this.mHighlightColor = 1714664933;
        this.mHighlightPathBogus = true;
        this.mSearchResultHighlights = null;
        this.mSearchResultHighlightPaint = null;
        this.mFocusedSearchResultHighlightPaint = null;
        this.mFocusedSearchResultHighlightColor = -27086;
        this.mSearchResultHighlightColor = -256;
        this.mFocusedSearchResultIndex = -1;
        this.mGesturePreviewHighlightStart = -1;
        this.mGesturePreviewHighlightEnd = -1;
        this.mPathRecyclePool = new ArrayList();
        this.mHighlightPathsBogus = true;
        this.mPrimePointerId = -1;
        this.mDeviceProvisionedState = 0;
        this.mLastInputSource = 4098;
        this.mAutoSizeTextType = 0;
        this.mNeedsAutoSizeText = false;
        this.mAutoSizeStepGranularityInPx = -1.0f;
        this.mAutoSizeMinTextSizeInPx = -1.0f;
        this.mAutoSizeMaxTextSizeInPx = -1.0f;
        this.mAutoSizeTextSizesInPx = EmptyArray.INT;
        this.mHasPresetAutoSizeValues = false;
        this.mTextSetFromXmlOrResourceId = false;
        this.mTextId = 0;
        this.mHintId = 0;
        if (getImportantForAutofill() == 0) {
            setImportantForAutofill(1);
        }
        if (getImportantForContentCapture() == 0) {
            setImportantForContentCapture(1);
        }
        setTextInternal("");
        Resources res = getResources();
        CompatibilityInfo compat = res.getCompatibilityInfo();
        TextPaint textPaint = new TextPaint(1);
        this.mTextPaint = textPaint;
        textPaint.density = res.getDisplayMetrics().density;
        textPaint.setCompatibilityScaling(compat.applicationScale);
        Paint paint = new Paint(1);
        this.mHighlightPaint = paint;
        paint.setCompatibilityScaling(compat.applicationScale);
        this.mMovement = getDefaultMovementMethod();
        this.mTransformation = null;
        TextAppearanceAttributes attributes4 = new TextAppearanceAttributes();
        attributes4.mTextColor = ColorStateList.valueOf(-16777216);
        attributes4.mTextSize = 15;
        this.mBreakStrategy = 0;
        this.mHyphenationFrequency = 0;
        this.mJustificationMode = 0;
        Resources.Theme theme2 = context.getTheme();
        TypedArray a = theme2.obtainStyledAttributes(attrs, C4057R.styleable.TextViewAppearance, defStyleAttr, defStyleRes);
        int[] iArr = C4057R.styleable.TextViewAppearance;
        String str4 = "Failure reading input extras";
        String str5 = LOG_TAG;
        TextAppearanceAttributes attributes5 = attributes4;
        saveAttributeDataForStyleable(context, iArr, attrs, a, defStyleAttr, defStyleRes);
        int ap = a.getResourceId(0, -1);
        a.recycle();
        if (ap == -1) {
            appearance = null;
        } else {
            TypedArray appearance2 = theme2.obtainStyledAttributes(ap, C4057R.styleable.TextAppearance);
            saveAttributeDataForStyleable(context, C4057R.styleable.TextAppearance, null, appearance2, 0, ap);
            appearance = appearance2;
        }
        if (appearance != null) {
            readTextAppearance(context, appearance, attributes5, false);
            attributes5.mFontFamilyExplicit = false;
            appearance.recycle();
        }
        boolean editable2 = getDefaultEditable();
        CharSequence inputMethod = null;
        CharSequence digits = null;
        float autoSizeMinTextSizeInPx2 = -1.0f;
        float autoSizeMaxTextSizeInPx2 = -1.0f;
        float autoSizeStepGranularityInPx2 = -1.0f;
        TypedArray a2 = theme2.obtainStyledAttributes(attrs, C4057R.styleable.TextView, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.TextView, attrs, a2, defStyleAttr, defStyleRes);
        readTextAppearance(context, a2, attributes5, true);
        int n3 = a2.getIndexCount();
        int i5 = 0;
        boolean editable3 = editable2;
        int numeric2 = 0;
        boolean phone3 = false;
        boolean autotext2 = false;
        int autocap = -1;
        int buffertype2 = 0;
        boolean selectallonfocus2 = false;
        Drawable drawableLeft = null;
        Drawable drawableTop = null;
        Drawable drawableRight = null;
        Drawable drawableBottom = null;
        Drawable drawableStart = null;
        Drawable drawableEnd = null;
        ColorStateList drawableTint2 = null;
        BlendMode drawableTintMode2 = null;
        int drawablePadding2 = 0;
        int ellipsize2 = -1;
        boolean singleLine3 = false;
        int maxlength2 = -1;
        CharSequence text = "";
        CharSequence hint2 = null;
        boolean password2 = false;
        int inputType2 = 0;
        boolean password3 = false;
        int ellipsize3 = -1;
        int lineHeight3 = -1;
        int autocap2 = -1;
        while (i5 < n3) {
            int attr = a2.getIndex(i5);
            switch (attr) {
                case 0:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    setEnabled(a2.getBoolean(attr, isEnabled()));
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 36:
                case 37:
                case 38:
                case 39:
                case 65:
                case 66:
                case 68:
                case 69:
                case 72:
                case 75:
                case 76:
                case 77:
                case 78:
                case 90:
                case 91:
                case 95:
                case 96:
                default:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 9:
                    n2 = n3;
                    boolean textIsSetFromXml3 = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    int lineHeight4 = ellipsize3;
                    int lineHeight5 = ellipsize2;
                    int ellipsize4 = a2.getInt(attr, lineHeight5);
                    ellipsize2 = ellipsize4;
                    ellipsize3 = lineHeight4;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    password3 = textIsSetFromXml3;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 10:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    setGravity(a2.getInt(attr, -1));
                    lineHeight2 = ellipsize2;
                    break;
                case 11:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    this.mAutoLinkMask = a2.getInt(attr, 0);
                    lineHeight2 = ellipsize2;
                    break;
                case 12:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    this.mLinksClickable = a2.getBoolean(attr, true);
                    lineHeight2 = ellipsize2;
                    break;
                case 13:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    setMaxWidth(a2.getDimensionPixelSize(attr, -1));
                    lineHeight2 = ellipsize2;
                    break;
                case 14:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    setMaxHeight(a2.getDimensionPixelSize(attr, -1));
                    lineHeight2 = ellipsize2;
                    break;
                case 15:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    setMinWidth(a2.getDimensionPixelSize(attr, -1));
                    lineHeight2 = ellipsize2;
                    break;
                case 16:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight6 = drawablePadding2;
                    drawablePadding = lineHeight6;
                    setMinHeight(a2.getDimensionPixelSize(attr, -1));
                    lineHeight2 = ellipsize2;
                    break;
                case 17:
                    n2 = n3;
                    boolean textIsSetFromXml4 = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    int lineHeight7 = ellipsize3;
                    buffertype2 = a2.getInt(attr, buffertype2);
                    ellipsize3 = lineHeight7;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    password3 = textIsSetFromXml4;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 18:
                    n2 = n3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    int lineHeight8 = ellipsize3;
                    this.mTextId = a2.getResourceId(attr, 0);
                    CharSequence text2 = a2.getText(attr);
                    text = text2;
                    ellipsize3 = lineHeight8;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    password3 = true;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 19:
                    n2 = n3;
                    boolean textIsSetFromXml5 = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    int lineHeight9 = ellipsize3;
                    this.mHintId = a2.getResourceId(attr, 0);
                    CharSequence hint3 = a2.getText(attr);
                    hint2 = hint3;
                    ellipsize3 = lineHeight9;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    password3 = textIsSetFromXml5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 20:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight10 = drawablePadding2;
                    setTextScaleX(a2.getFloat(attr, 1.0f));
                    drawablePadding = lineHeight10;
                    buffertype = buffertype2;
                    lineHeight2 = ellipsize2;
                    break;
                case 21:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight11 = drawablePadding2;
                    if (!a2.getBoolean(attr, true)) {
                        setCursorVisible(false);
                        drawablePadding = lineHeight11;
                        buffertype = buffertype2;
                        lineHeight2 = ellipsize2;
                        break;
                    } else {
                        drawablePadding = lineHeight11;
                        buffertype = buffertype2;
                        lineHeight2 = ellipsize2;
                        break;
                    }
                case 22:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight12 = drawablePadding2;
                    setMaxLines(a2.getInt(attr, -1));
                    drawablePadding = lineHeight12;
                    buffertype = buffertype2;
                    lineHeight2 = ellipsize2;
                    break;
                case 23:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight13 = drawablePadding2;
                    setLines(a2.getInt(attr, -1));
                    drawablePadding = lineHeight13;
                    buffertype = buffertype2;
                    lineHeight2 = ellipsize2;
                    break;
                case 24:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight14 = drawablePadding2;
                    setHeight(a2.getDimensionPixelSize(attr, -1));
                    drawablePadding = lineHeight14;
                    buffertype = buffertype2;
                    lineHeight2 = ellipsize2;
                    break;
                case 25:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight15 = drawablePadding2;
                    setMinLines(a2.getInt(attr, -1));
                    drawablePadding = lineHeight15;
                    buffertype = buffertype2;
                    lineHeight2 = ellipsize2;
                    break;
                case 26:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight16 = drawablePadding2;
                    setMaxEms(a2.getInt(attr, -1));
                    drawablePadding = lineHeight16;
                    buffertype = buffertype2;
                    lineHeight2 = ellipsize2;
                    break;
                case 27:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight17 = drawablePadding2;
                    setEms(a2.getInt(attr, -1));
                    drawablePadding = lineHeight17;
                    buffertype = buffertype2;
                    lineHeight2 = ellipsize2;
                    break;
                case 28:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight18 = drawablePadding2;
                    setWidth(a2.getDimensionPixelSize(attr, -1));
                    drawablePadding = lineHeight18;
                    buffertype = buffertype2;
                    lineHeight2 = ellipsize2;
                    break;
                case 29:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight19 = drawablePadding2;
                    setMinEms(a2.getInt(attr, -1));
                    drawablePadding = lineHeight19;
                    buffertype = buffertype2;
                    lineHeight2 = ellipsize2;
                    break;
                case 30:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    textIsSetFromXml = password3;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    int lineHeight20 = drawablePadding2;
                    attributes3 = attributes5;
                    if (a2.getBoolean(attr, false)) {
                        setHorizontallyScrolling(true);
                        drawablePadding = lineHeight20;
                        buffertype = buffertype2;
                        lineHeight2 = ellipsize2;
                        break;
                    } else {
                        drawablePadding = lineHeight20;
                        buffertype = buffertype2;
                        lineHeight2 = ellipsize2;
                        break;
                    }
                case 31:
                    n2 = n3;
                    boolean textIsSetFromXml6 = password3;
                    theme = theme2;
                    str = str5;
                    boolean textIsSetFromXml7 = singleLine3;
                    int lineHeight21 = ellipsize3;
                    boolean singleLine4 = password2;
                    boolean password4 = a2.getBoolean(attr, singleLine4);
                    password2 = password4;
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight21;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    password3 = textIsSetFromXml6;
                    singleLine3 = textIsSetFromXml7;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 32:
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    int lineHeight22 = ellipsize3;
                    boolean textIsSetFromXml8 = password3;
                    boolean textIsSetFromXml9 = singleLine3;
                    boolean singleLine5 = a2.getBoolean(attr, textIsSetFromXml9);
                    singleLine3 = singleLine5;
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight22;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    password3 = textIsSetFromXml8;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 33:
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    int lineHeight23 = ellipsize3;
                    selectallonfocus2 = a2.getBoolean(attr, selectallonfocus2);
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight23;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 34:
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    drawableTintMode = drawableTintMode2;
                    lineHeight = ellipsize3;
                    int lineHeight24 = drawablePadding2;
                    if (!a2.getBoolean(attr, true)) {
                        setIncludeFontPadding(false);
                        drawablePadding = lineHeight24;
                        textIsSetFromXml = password3;
                        attributes3 = attributes5;
                        buffertype = buffertype2;
                        selectallonfocus = selectallonfocus2;
                        lineHeight2 = ellipsize2;
                        singleLine2 = singleLine3;
                        textIsSetFromXml2 = password2;
                        break;
                    } else {
                        drawablePadding = lineHeight24;
                        textIsSetFromXml = password3;
                        attributes3 = attributes5;
                        buffertype = buffertype2;
                        selectallonfocus = selectallonfocus2;
                        lineHeight2 = ellipsize2;
                        singleLine2 = singleLine3;
                        textIsSetFromXml2 = password2;
                        break;
                    }
                case 35:
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    int lineHeight25 = ellipsize3;
                    int maxlength3 = a2.getInt(attr, -1);
                    maxlength2 = maxlength3;
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight25;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 40:
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    int lineHeight26 = ellipsize3;
                    numeric2 = a2.getInt(attr, numeric2);
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight26;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 41:
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    int lineHeight27 = ellipsize3;
                    CharSequence digits2 = a2.getText(attr);
                    digits = digits2;
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight27;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 42:
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    int lineHeight28 = ellipsize3;
                    phone3 = a2.getBoolean(attr, phone3);
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight28;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 43:
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    int lineHeight29 = ellipsize3;
                    CharSequence inputMethod2 = a2.getText(attr);
                    inputMethod = inputMethod2;
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight29;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 44:
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    int lineHeight30 = ellipsize3;
                    autocap = a2.getInt(attr, autocap);
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight30;
                    lineHeight3 = lineHeight3;
                    autocap2 = autocap2;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 45:
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    int lineHeight31 = ellipsize3;
                    autotext2 = a2.getBoolean(attr, autotext2);
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight31;
                    lineHeight3 = lineHeight3;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 46:
                    n2 = n3;
                    theme = theme2;
                    str = str5;
                    int lineHeight32 = ellipsize3;
                    editable3 = a2.getBoolean(attr, editable3);
                    attributes3 = attributes5;
                    ellipsize3 = lineHeight32;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 47:
                    n2 = n3;
                    theme = theme2;
                    String str6 = str5;
                    drawableTintMode = drawableTintMode2;
                    lineHeight = ellipsize3;
                    int lineHeight33 = drawablePadding2;
                    str = str6;
                    this.mFreezesText = a2.getBoolean(attr, false);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    drawablePadding = lineHeight33;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    lineHeight2 = ellipsize2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    break;
                case 48:
                    n2 = n3;
                    theme = theme2;
                    String str7 = str5;
                    int lineHeight34 = ellipsize3;
                    Drawable drawableTop2 = a2.getDrawable(attr);
                    drawableTop = drawableTop2;
                    attributes3 = attributes5;
                    str = str7;
                    ellipsize3 = lineHeight34;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 49:
                    n2 = n3;
                    theme = theme2;
                    String str8 = str5;
                    int lineHeight35 = ellipsize3;
                    Drawable drawableBottom2 = a2.getDrawable(attr);
                    drawableBottom = drawableBottom2;
                    attributes3 = attributes5;
                    str = str8;
                    ellipsize3 = lineHeight35;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 50:
                    n2 = n3;
                    theme = theme2;
                    String str9 = str5;
                    int lineHeight36 = ellipsize3;
                    Drawable drawableLeft2 = a2.getDrawable(attr);
                    drawableLeft = drawableLeft2;
                    attributes3 = attributes5;
                    str = str9;
                    ellipsize3 = lineHeight36;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 51:
                    n2 = n3;
                    theme = theme2;
                    String str10 = str5;
                    int lineHeight37 = ellipsize3;
                    Drawable drawableRight2 = a2.getDrawable(attr);
                    drawableRight = drawableRight2;
                    attributes3 = attributes5;
                    str = str10;
                    ellipsize3 = lineHeight37;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 52:
                    n2 = n3;
                    theme = theme2;
                    String str11 = str5;
                    int lineHeight38 = ellipsize3;
                    int lineHeight39 = drawablePadding2;
                    int drawablePadding3 = a2.getDimensionPixelSize(attr, lineHeight39);
                    drawablePadding2 = drawablePadding3;
                    attributes3 = attributes5;
                    str = str11;
                    ellipsize3 = lineHeight38;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 53:
                    n2 = n3;
                    theme = theme2;
                    String str12 = str5;
                    drawableTintMode = drawableTintMode2;
                    lineHeight = ellipsize3;
                    this.mSpacingAdd = a2.getDimensionPixelSize(attr, (int) this.mSpacingAdd);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str12;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    lineHeight2 = ellipsize2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    break;
                case 54:
                    n2 = n3;
                    theme = theme2;
                    String str13 = str5;
                    drawableTintMode = drawableTintMode2;
                    lineHeight = ellipsize3;
                    this.mSpacingMult = a2.getFloat(attr, this.mSpacingMult);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str13;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    lineHeight2 = ellipsize2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    break;
                case 55:
                    n2 = n3;
                    theme = theme2;
                    String str14 = str5;
                    drawableTintMode = drawableTintMode2;
                    lineHeight = ellipsize3;
                    setMarqueeRepeatLimit(a2.getInt(attr, this.mMarqueeRepeatLimit));
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str14;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    lineHeight2 = ellipsize2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    break;
                case 56:
                    n2 = n3;
                    theme = theme2;
                    String str15 = str5;
                    int lineHeight40 = ellipsize3;
                    inputType2 = a2.getInt(attr, 0);
                    attributes3 = attributes5;
                    str = str15;
                    ellipsize3 = lineHeight40;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 57:
                    n2 = n3;
                    theme = theme2;
                    String str16 = str5;
                    drawableTintMode = drawableTintMode2;
                    lineHeight = ellipsize3;
                    setPrivateImeOptions(a2.getString(attr));
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str16;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    lineHeight2 = ellipsize2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    break;
                case 58:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode = drawableTintMode2;
                    try {
                        try {
                            try {
                                setInputExtras(a2.getResourceId(attr, 0));
                                firstBaselineToTopHeight = autocap2;
                                lastBaselineToBottomHeight = lineHeight3;
                                textIsSetFromXml = password3;
                                attributes3 = attributes5;
                                str = str5;
                                editable = editable3;
                                numeric = numeric2;
                                phone2 = phone3;
                                autotext = autotext2;
                                firstBaselineToTopHeight2 = autocap;
                                buffertype = buffertype2;
                                selectallonfocus = selectallonfocus2;
                                drawablePadding = drawablePadding2;
                                singleLine2 = singleLine3;
                                textIsSetFromXml2 = password2;
                                lineHeight = ellipsize3;
                                lineHeight2 = ellipsize2;
                            } catch (XmlPullParserException e) {
                                e = e;
                                str2 = str5;
                                lineHeight = ellipsize3;
                                str3 = str4;
                                Log.m103w(str2, str3, e);
                                firstBaselineToTopHeight = autocap2;
                                lastBaselineToBottomHeight = lineHeight3;
                                str4 = str3;
                                textIsSetFromXml = password3;
                                attributes3 = attributes5;
                                str = str2;
                                editable = editable3;
                                numeric = numeric2;
                                phone2 = phone3;
                                autotext = autotext2;
                                firstBaselineToTopHeight2 = autocap;
                                buffertype = buffertype2;
                                selectallonfocus = selectallonfocus2;
                                drawablePadding = drawablePadding2;
                                lineHeight2 = ellipsize2;
                                singleLine2 = singleLine3;
                                textIsSetFromXml2 = password2;
                                autocap = firstBaselineToTopHeight2;
                                autotext2 = autotext;
                                drawableTintMode2 = drawableTintMode;
                                ellipsize2 = lineHeight2;
                                password2 = textIsSetFromXml2;
                                phone3 = phone2;
                                numeric2 = numeric;
                                selectallonfocus2 = selectallonfocus;
                                buffertype2 = buffertype;
                                editable3 = editable;
                                ellipsize3 = lineHeight;
                                lineHeight3 = lastBaselineToBottomHeight;
                                autocap2 = firstBaselineToTopHeight;
                                password3 = textIsSetFromXml;
                                singleLine3 = singleLine2;
                                drawablePadding2 = drawablePadding;
                                i5++;
                                n3 = n2;
                                theme2 = theme;
                                str5 = str;
                                attributes5 = attributes3;
                            }
                        } catch (XmlPullParserException e2) {
                            e = e2;
                            str2 = str5;
                            lineHeight = ellipsize3;
                            str3 = str4;
                        }
                    } catch (IOException e3) {
                        String str17 = str5;
                        lineHeight = ellipsize3;
                        Log.m103w(str17, str4, e3);
                        firstBaselineToTopHeight = autocap2;
                        lastBaselineToBottomHeight = lineHeight3;
                        textIsSetFromXml = password3;
                        attributes3 = attributes5;
                        str = str17;
                        editable = editable3;
                        numeric = numeric2;
                        phone2 = phone3;
                        autotext = autotext2;
                        firstBaselineToTopHeight2 = autocap;
                        buffertype = buffertype2;
                        selectallonfocus = selectallonfocus2;
                        drawablePadding = drawablePadding2;
                        lineHeight2 = ellipsize2;
                        singleLine2 = singleLine3;
                        textIsSetFromXml2 = password2;
                    }
                case 59:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode = drawableTintMode2;
                    createEditorIfNeeded();
                    this.mEditor.createInputContentTypeIfNeeded();
                    this.mEditor.mInputContentType.imeOptions = a2.getInt(attr, this.mEditor.mInputContentType.imeOptions);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 60:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode = drawableTintMode2;
                    createEditorIfNeeded();
                    this.mEditor.createInputContentTypeIfNeeded();
                    this.mEditor.mInputContentType.imeActionLabel = a2.getText(attr);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 61:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode = drawableTintMode2;
                    createEditorIfNeeded();
                    this.mEditor.createInputContentTypeIfNeeded();
                    this.mEditor.mInputContentType.imeActionId = a2.getInt(attr, this.mEditor.mInputContentType.imeActionId);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 62:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode = drawableTintMode2;
                    this.mTextSelectHandleLeftRes = a2.getResourceId(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 63:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode = drawableTintMode2;
                    this.mTextSelectHandleRightRes = a2.getResourceId(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 64:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode = drawableTintMode2;
                    this.mTextSelectHandleRes = a2.getResourceId(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 67:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode = drawableTintMode2;
                    setTextIsSelectable(a2.getBoolean(attr, false));
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 70:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode = drawableTintMode2;
                    this.mCursorDrawableRes = a2.getResourceId(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 71:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode = drawableTintMode2;
                    this.mTextEditSuggestionItemLayout = a2.getResourceId(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 73:
                    n2 = n3;
                    theme = theme2;
                    Drawable drawableStart2 = a2.getDrawable(attr);
                    drawableStart = drawableStart2;
                    attributes3 = attributes5;
                    str = str5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 74:
                    n2 = n3;
                    theme = theme2;
                    Drawable drawableEnd2 = a2.getDrawable(attr);
                    drawableEnd = drawableEnd2;
                    attributes3 = attributes5;
                    str = str5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 79:
                    n2 = n3;
                    theme = theme2;
                    ColorStateList drawableTint3 = a2.getColorStateList(attr);
                    drawableTint2 = drawableTint3;
                    attributes3 = attributes5;
                    str = str5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 80:
                    n2 = n3;
                    theme = theme2;
                    drawableTintMode2 = Drawable.parseBlendMode(a2.getInt(attr, -1), drawableTintMode2);
                    attributes3 = attributes5;
                    str = str5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 81:
                    n2 = n3;
                    theme = theme2;
                    this.mBreakStrategy = a2.getInt(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 82:
                    n2 = n3;
                    theme = theme2;
                    this.mHyphenationFrequency = a2.getInt(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 83:
                    n2 = n3;
                    createEditorIfNeeded();
                    theme = theme2;
                    this.mEditor.mAllowUndo = a2.getBoolean(attr, true);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 84:
                    n2 = n3;
                    this.mAutoSizeTextType = a2.getInt(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 85:
                    n2 = n3;
                    float autoSizeStepGranularityInPx3 = a2.getDimension(attr, -1.0f);
                    autoSizeStepGranularityInPx2 = autoSizeStepGranularityInPx3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 86:
                    n2 = n3;
                    int autoSizeStepSizeArrayResId = a2.getResourceId(attr, 0);
                    if (autoSizeStepSizeArrayResId <= 0) {
                        firstBaselineToTopHeight = autocap2;
                        lastBaselineToBottomHeight = lineHeight3;
                        textIsSetFromXml = password3;
                        attributes3 = attributes5;
                        theme = theme2;
                        str = str5;
                        editable = editable3;
                        numeric = numeric2;
                        phone2 = phone3;
                        autotext = autotext2;
                        firstBaselineToTopHeight2 = autocap;
                        buffertype = buffertype2;
                        selectallonfocus = selectallonfocus2;
                        drawableTintMode = drawableTintMode2;
                        drawablePadding = drawablePadding2;
                        singleLine2 = singleLine3;
                        textIsSetFromXml2 = password2;
                        lineHeight = ellipsize3;
                        lineHeight2 = ellipsize2;
                        break;
                    } else {
                        TypedArray autoSizePresetTextSizes = a2.getResources().obtainTypedArray(autoSizeStepSizeArrayResId);
                        setupAutoSizeUniformPresetSizes(autoSizePresetTextSizes);
                        autoSizePresetTextSizes.recycle();
                        firstBaselineToTopHeight = autocap2;
                        lastBaselineToBottomHeight = lineHeight3;
                        textIsSetFromXml = password3;
                        attributes3 = attributes5;
                        theme = theme2;
                        str = str5;
                        editable = editable3;
                        numeric = numeric2;
                        phone2 = phone3;
                        autotext = autotext2;
                        firstBaselineToTopHeight2 = autocap;
                        buffertype = buffertype2;
                        selectallonfocus = selectallonfocus2;
                        drawableTintMode = drawableTintMode2;
                        drawablePadding = drawablePadding2;
                        singleLine2 = singleLine3;
                        textIsSetFromXml2 = password2;
                        lineHeight = ellipsize3;
                        lineHeight2 = ellipsize2;
                        break;
                    }
                case 87:
                    n2 = n3;
                    float autoSizeMinTextSizeInPx3 = a2.getDimension(attr, -1.0f);
                    autoSizeMinTextSizeInPx2 = autoSizeMinTextSizeInPx3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 88:
                    n2 = n3;
                    float autoSizeMaxTextSizeInPx3 = a2.getDimension(attr, -1.0f);
                    autoSizeMaxTextSizeInPx2 = autoSizeMaxTextSizeInPx3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 89:
                    n2 = n3;
                    this.mJustificationMode = a2.getInt(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 92:
                    n2 = n3;
                    autocap2 = a2.getDimensionPixelSize(attr, -1);
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 93:
                    n2 = n3;
                    lineHeight3 = a2.getDimensionPixelSize(attr, -1);
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 94:
                    n2 = n3;
                    ellipsize3 = a2.getDimensionPixelSize(attr, -1);
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    continue;
                    i5++;
                    n3 = n2;
                    theme2 = theme;
                    str5 = str;
                    attributes5 = attributes3;
                case 97:
                    n2 = n3;
                    this.mLineBreakStyle = a2.getInt(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 98:
                    n2 = n3;
                    if (a2.hasValue(attr)) {
                        this.mUserSpeficiedLineBreakwordStyle = true;
                    }
                    this.mLineBreakWordStyle = a2.getInt(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 99:
                    n2 = n3;
                    this.mTextEditSuggestionContainerLayout = a2.getResourceId(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
                case 100:
                    n2 = n3;
                    this.mTextEditSuggestionHighlightStyle = a2.getResourceId(attr, 0);
                    firstBaselineToTopHeight = autocap2;
                    lastBaselineToBottomHeight = lineHeight3;
                    textIsSetFromXml = password3;
                    attributes3 = attributes5;
                    theme = theme2;
                    str = str5;
                    editable = editable3;
                    numeric = numeric2;
                    phone2 = phone3;
                    autotext = autotext2;
                    firstBaselineToTopHeight2 = autocap;
                    buffertype = buffertype2;
                    selectallonfocus = selectallonfocus2;
                    drawableTintMode = drawableTintMode2;
                    drawablePadding = drawablePadding2;
                    singleLine2 = singleLine3;
                    textIsSetFromXml2 = password2;
                    lineHeight = ellipsize3;
                    lineHeight2 = ellipsize2;
                    break;
            }
            autocap = firstBaselineToTopHeight2;
            autotext2 = autotext;
            drawableTintMode2 = drawableTintMode;
            ellipsize2 = lineHeight2;
            password2 = textIsSetFromXml2;
            phone3 = phone2;
            numeric2 = numeric;
            selectallonfocus2 = selectallonfocus;
            buffertype2 = buffertype;
            editable3 = editable;
            ellipsize3 = lineHeight;
            lineHeight3 = lastBaselineToBottomHeight;
            autocap2 = firstBaselineToTopHeight;
            password3 = textIsSetFromXml;
            singleLine3 = singleLine2;
            drawablePadding2 = drawablePadding;
            i5++;
            n3 = n2;
            theme2 = theme;
            str5 = str;
            attributes5 = attributes3;
        }
        int firstBaselineToTopHeight3 = autocap2;
        int lastBaselineToBottomHeight2 = lineHeight3;
        int lineHeight41 = ellipsize3;
        boolean textIsSetFromXml10 = password3;
        TextAppearanceAttributes attributes6 = attributes5;
        boolean editable4 = editable3;
        int numeric3 = numeric2;
        boolean phone4 = phone3;
        boolean autotext3 = autotext2;
        int firstBaselineToTopHeight4 = autocap;
        int buffertype3 = buffertype2;
        boolean selectallonfocus3 = selectallonfocus2;
        BlendMode drawableTintMode3 = drawableTintMode2;
        int drawablePadding4 = drawablePadding2;
        int lineHeight42 = ellipsize2;
        boolean singleLine6 = singleLine3;
        boolean password5 = password2;
        a2.recycle();
        BufferType bufferType2 = BufferType.EDITABLE;
        int inputType3 = inputType2;
        int variation = inputType3 & 4095;
        boolean passwordInputType2 = variation == 129;
        boolean webPasswordInputType2 = variation == 225;
        boolean numberPasswordInputType3 = variation == 18;
        int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        this.mUseInternationalizedInput = targetSdkVersion >= 26;
        if (CompatChanges.isChangeEnabled(BORINGLAYOUT_FALLBACK_LINESPACING)) {
            this.mUseFallbackLineSpacing = 2;
        } else if (CompatChanges.isChangeEnabled(STATICLAYOUT_FALLBACK_LINESPACING)) {
            this.mUseFallbackLineSpacing = 1;
        } else {
            this.mUseFallbackLineSpacing = 0;
        }
        this.mUseTextPaddingForUiTranslation = targetSdkVersion <= 30;
        if (inputMethod != null) {
            try {
                Class<?> c = Class.forName(inputMethod.toString());
                try {
                    createEditorIfNeeded();
                } catch (IllegalAccessException e4) {
                    ex = e4;
                } catch (InstantiationException e5) {
                    ex = e5;
                }
                try {
                    this.mEditor.mKeyListener = (KeyListener) c.newInstance();
                    try {
                        Editor editor3 = this.mEditor;
                        if (inputType3 != 0) {
                            inputType = inputType3;
                        } else {
                            inputType = editor3.mKeyListener.getInputType();
                        }
                        editor3.mInputType = inputType;
                    } catch (IncompatibleClassChangeError e6) {
                        this.mEditor.mInputType = 1;
                    }
                    phone = phone4;
                } catch (IllegalAccessException e7) {
                    ex = e7;
                    throw new RuntimeException(ex);
                } catch (InstantiationException e8) {
                    ex = e8;
                    throw new RuntimeException(ex);
                }
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        } else if (digits != null) {
            createEditorIfNeeded();
            this.mEditor.mKeyListener = DigitsKeyListener.getInstance(digits.toString());
            this.mEditor.mInputType = inputType3 != 0 ? inputType3 : 1;
            phone = phone4;
        } else {
            if (inputType3 != 0) {
                setInputType(inputType3, true);
                singleLine = !isMultilineInputType(inputType3);
                phone = phone4;
                bufferType = bufferType2;
            } else if (!phone4) {
                if (numeric3 != 0) {
                    createEditorIfNeeded();
                    phone = phone4;
                    this.mEditor.mKeyListener = DigitsKeyListener.getInstance(null, (numeric3 & 2) != 0, (numeric3 & 4) != 0);
                    int inputType4 = this.mEditor.mKeyListener.getInputType();
                    this.mEditor.mInputType = inputType4;
                    bufferType = bufferType2;
                    singleLine = singleLine6;
                } else {
                    phone = phone4;
                    if (autotext3 || firstBaselineToTopHeight4 != -1) {
                        int inputType5 = 1;
                        switch (firstBaselineToTopHeight4) {
                            case 1:
                                cap = TextKeyListener.Capitalize.SENTENCES;
                                inputType5 = 1 | 16384;
                                break;
                            case 2:
                                cap = TextKeyListener.Capitalize.WORDS;
                                inputType5 = 1 | 8192;
                                break;
                            case 3:
                                cap = TextKeyListener.Capitalize.CHARACTERS;
                                inputType5 = 1 | 4096;
                                break;
                            default:
                                cap = TextKeyListener.Capitalize.NONE;
                                break;
                        }
                        createEditorIfNeeded();
                        this.mEditor.mKeyListener = TextKeyListener.getInstance(autotext3, cap);
                        this.mEditor.mInputType = inputType5;
                        bufferType = bufferType2;
                        singleLine = singleLine6;
                    } else if (editable4) {
                        createEditorIfNeeded();
                        this.mEditor.mKeyListener = TextKeyListener.getInstance();
                        this.mEditor.mInputType = 1;
                    } else if (isTextSelectable()) {
                        Editor editor4 = this.mEditor;
                        if (editor4 != null) {
                            editor4.mKeyListener = null;
                            this.mEditor.mInputType = 0;
                        }
                        bufferType = BufferType.SPANNABLE;
                        setMovementMethod(ArrowKeyMovementMethod.getInstance());
                        singleLine = singleLine6;
                    } else {
                        Editor editor5 = this.mEditor;
                        if (editor5 != null) {
                            editor5.mKeyListener = null;
                        }
                        switch (buffertype3) {
                            case 0:
                                bufferType = BufferType.NORMAL;
                                singleLine = singleLine6;
                                break;
                            case 1:
                                bufferType = BufferType.SPANNABLE;
                                singleLine = singleLine6;
                                break;
                            case 2:
                                bufferType = BufferType.EDITABLE;
                                singleLine = singleLine6;
                                break;
                        }
                    }
                }
            } else {
                createEditorIfNeeded();
                this.mEditor.mKeyListener = DialerKeyListener.getInstance();
                this.mEditor.mInputType = 3;
                phone = phone4;
                bufferType = bufferType2;
                singleLine = singleLine6;
            }
            editor = this.mEditor;
            if (editor == null) {
                passwordInputType = passwordInputType2;
                webPasswordInputType = webPasswordInputType2;
                numberPasswordInputType = numberPasswordInputType3;
                editor.adjustInputType(password5, passwordInputType, webPasswordInputType, numberPasswordInputType);
            } else {
                passwordInputType = passwordInputType2;
                webPasswordInputType = webPasswordInputType2;
                numberPasswordInputType = numberPasswordInputType3;
            }
            if (!selectallonfocus3) {
                createEditorIfNeeded();
                this.mEditor.mSelectAllOnFocus = true;
                if (bufferType == BufferType.NORMAL) {
                    bufferType = BufferType.SPANNABLE;
                }
            }
            drawableTint = drawableTint2;
            if (drawableTint == null || drawableTintMode3 != null) {
                if (this.mDrawables == null) {
                    this.mDrawables = new Drawables(context);
                }
                if (drawableTint == null) {
                    this.mDrawables.mTintList = drawableTint;
                    z = true;
                    this.mDrawables.mHasTint = true;
                } else {
                    z = true;
                }
                if (drawableTintMode3 != null) {
                    this.mDrawables.mBlendMode = drawableTintMode3;
                    this.mDrawables.mHasTintMode = z;
                }
            }
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
            Drawable drawableTop3 = drawableStart;
            Drawable drawableBottom3 = drawableEnd;
            setRelativeDrawablesIfNeeded(drawableTop3, drawableBottom3);
            setCompoundDrawablePadding(drawablePadding4);
            setInputTypeSingleLine(singleLine);
            applySingleLine(singleLine, singleLine, singleLine, false);
            if (!singleLine && getKeyListener() == null && lineHeight42 == -1) {
                ellipsize = 3;
            } else {
                ellipsize = lineHeight42;
            }
            switch (ellipsize) {
                case 1:
                    setEllipsize(TextUtils.TruncateAt.START);
                    break;
                case 2:
                    setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    break;
                case 3:
                    setEllipsize(TextUtils.TruncateAt.END);
                    break;
                case 4:
                    if (ViewConfiguration.get(context).isFadingMarqueeEnabled()) {
                        setHorizontalFadingEdgeEnabled(true);
                        this.mMarqueeFadeMode = 0;
                    } else {
                        setHorizontalFadingEdgeEnabled(false);
                        this.mMarqueeFadeMode = 1;
                    }
                    setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    break;
            }
            isPassword = !password5 || passwordInputType || webPasswordInputType || numberPasswordInputType;
            if (!isPassword) {
                Editor editor6 = this.mEditor;
                if (editor6 != null) {
                }
                numberPasswordInputType2 = false;
                if (numberPasswordInputType2) {
                    attributes = attributes6;
                    attributes.mTypefaceIndex = 3;
                } else {
                    attributes = attributes6;
                }
                this.mFontWeightAdjustment = getContext().getResources().getConfiguration().fontWeightAdjustment;
                applyTextAppearance(attributes);
                if (isPassword) {
                    setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                if (bufferType == BufferType.EDITABLE || !singleLine) {
                    maxlength = maxlength2;
                } else {
                    maxlength = maxlength2;
                    if (maxlength == -1) {
                        this.mSingleLineLengthFilter = new InputFilter.LengthFilter(5000);
                    }
                }
                lengthFilter = this.mSingleLineLengthFilter;
                if (lengthFilter != null) {
                    setFilters(new InputFilter[]{lengthFilter});
                } else if (maxlength >= 0) {
                    setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxlength)});
                } else {
                    setFilters(NO_FILTERS);
                }
                setText(text, bufferType);
                if (this.mText == null) {
                    charSequence = "";
                    this.mText = charSequence;
                } else {
                    charSequence = "";
                }
                if (this.mTransformed == null) {
                    this.mTransformed = charSequence;
                }
                if (textIsSetFromXml10) {
                    this.mTextSetFromXmlOrResourceId = true;
                }
                hint = hint2;
                if (hint != null) {
                    setHint(hint);
                }
                TypedArray a3 = context.obtainStyledAttributes(attrs, C4057R.styleable.View, defStyleAttr, defStyleRes);
                boolean canInputOrMove = this.mMovement == null || getKeyListener() != null;
                boolean clickable = !canInputOrMove || isClickable();
                boolean longClickable = !canInputOrMove || isLongClickable();
                int focusable2 = getFocusable();
                n = a3.getIndexCount();
                i = 0;
                boolean clickable2 = clickable;
                boolean longClickable2 = longClickable;
                focusable = focusable2;
                while (i < n) {
                    int n4 = n;
                    int attr2 = a3.getIndex(i);
                    switch (attr2) {
                        case 19:
                            TypedValue val = new TypedValue();
                            attributes2 = attributes;
                            if (!a3.getValue(attr2, val)) {
                                password = password5;
                                break;
                            } else {
                                password = password5;
                                if (val.type == 18) {
                                    i4 = val.data == 0 ? 0 : 1;
                                } else {
                                    i4 = val.data;
                                }
                                focusable = i4;
                                break;
                            }
                        case 30:
                            clickable2 = a3.getBoolean(attr2, clickable2);
                            attributes2 = attributes;
                            password = password5;
                            break;
                        case 31:
                            longClickable2 = a3.getBoolean(attr2, longClickable2);
                            attributes2 = attributes;
                            password = password5;
                            break;
                        default:
                            attributes2 = attributes;
                            password = password5;
                            break;
                    }
                    i++;
                    n = n4;
                    attributes = attributes2;
                    password5 = password;
                }
                a3.recycle();
                if (focusable != getFocusable()) {
                    setFocusable(focusable);
                }
                setClickable(clickable2);
                setLongClickable(longClickable2);
                editor2 = this.mEditor;
                if (editor2 != null) {
                    editor2.prepareCursorControllers();
                }
                if (getImportantForAccessibility() != 0) {
                    i2 = 1;
                } else {
                    i2 = 1;
                    setImportantForAccessibility(1);
                }
                if (!supportsAutoSizeText()) {
                    this.mAutoSizeTextType = 0;
                } else if (this.mAutoSizeTextType == i2) {
                    if (!this.mHasPresetAutoSizeValues) {
                        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                        if (autoSizeMinTextSizeInPx2 != -1.0f) {
                            i3 = 2;
                            autoSizeMinTextSizeInPx = autoSizeMinTextSizeInPx2;
                        } else {
                            i3 = 2;
                            float autoSizeMinTextSizeInPx4 = TypedValue.applyDimension(2, 12.0f, displayMetrics);
                            autoSizeMinTextSizeInPx = autoSizeMinTextSizeInPx4;
                        }
                        if (autoSizeMaxTextSizeInPx2 != -1.0f) {
                            autoSizeMaxTextSizeInPx = autoSizeMaxTextSizeInPx2;
                        } else {
                            float autoSizeMaxTextSizeInPx4 = TypedValue.applyDimension(i3, 112.0f, displayMetrics);
                            autoSizeMaxTextSizeInPx = autoSizeMaxTextSizeInPx4;
                        }
                        if (autoSizeStepGranularityInPx2 != -1.0f) {
                            autoSizeStepGranularityInPx = autoSizeStepGranularityInPx2;
                        } else {
                            autoSizeStepGranularityInPx = 1.0f;
                        }
                        validateAndSetAutoSizeTextTypeUniformConfiguration(autoSizeMinTextSizeInPx, autoSizeMaxTextSizeInPx, autoSizeStepGranularityInPx);
                    }
                    setupAutoSizeText();
                }
                if (firstBaselineToTopHeight3 >= 0) {
                    setFirstBaselineToTopHeight(firstBaselineToTopHeight3);
                }
                if (lastBaselineToBottomHeight2 >= 0) {
                    setLastBaselineToBottomHeight(lastBaselineToBottomHeight2);
                }
                if (lineHeight41 >= 0) {
                    setLineHeight(lineHeight41);
                    return;
                }
                return;
            }
            numberPasswordInputType2 = true;
            if (numberPasswordInputType2) {
            }
            this.mFontWeightAdjustment = getContext().getResources().getConfiguration().fontWeightAdjustment;
            applyTextAppearance(attributes);
            if (isPassword) {
            }
            if (bufferType == BufferType.EDITABLE) {
            }
            maxlength = maxlength2;
            lengthFilter = this.mSingleLineLengthFilter;
            if (lengthFilter != null) {
            }
            setText(text, bufferType);
            if (this.mText == null) {
            }
            if (this.mTransformed == null) {
            }
            if (textIsSetFromXml10) {
            }
            hint = hint2;
            if (hint != null) {
            }
            TypedArray a32 = context.obtainStyledAttributes(attrs, C4057R.styleable.View, defStyleAttr, defStyleRes);
            if (this.mMovement == null) {
            }
            if (canInputOrMove) {
            }
            if (canInputOrMove) {
            }
            int focusable22 = getFocusable();
            n = a32.getIndexCount();
            i = 0;
            boolean clickable22 = clickable;
            boolean longClickable22 = longClickable;
            focusable = focusable22;
            while (i < n) {
            }
            a32.recycle();
            if (focusable != getFocusable()) {
            }
            setClickable(clickable22);
            setLongClickable(longClickable22);
            editor2 = this.mEditor;
            if (editor2 != null) {
            }
            if (getImportantForAccessibility() != 0) {
            }
            if (!supportsAutoSizeText()) {
            }
            if (firstBaselineToTopHeight3 >= 0) {
            }
            if (lastBaselineToBottomHeight2 >= 0) {
            }
            if (lineHeight41 >= 0) {
            }
        }
        bufferType = bufferType2;
        singleLine = singleLine6;
        editor = this.mEditor;
        if (editor == null) {
        }
        if (!selectallonfocus3) {
        }
        drawableTint = drawableTint2;
        if (drawableTint == null) {
        }
        if (this.mDrawables == null) {
        }
        if (drawableTint == null) {
        }
        if (drawableTintMode3 != null) {
        }
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        Drawable drawableTop32 = drawableStart;
        Drawable drawableBottom32 = drawableEnd;
        setRelativeDrawablesIfNeeded(drawableTop32, drawableBottom32);
        setCompoundDrawablePadding(drawablePadding4);
        setInputTypeSingleLine(singleLine);
        applySingleLine(singleLine, singleLine, singleLine, false);
        if (!singleLine) {
        }
        ellipsize = lineHeight42;
        switch (ellipsize) {
        }
        if (password5) {
        }
        if (!isPassword) {
        }
        numberPasswordInputType2 = true;
        if (numberPasswordInputType2) {
        }
        this.mFontWeightAdjustment = getContext().getResources().getConfiguration().fontWeightAdjustment;
        applyTextAppearance(attributes);
        if (isPassword) {
        }
        if (bufferType == BufferType.EDITABLE) {
        }
        maxlength = maxlength2;
        lengthFilter = this.mSingleLineLengthFilter;
        if (lengthFilter != null) {
        }
        setText(text, bufferType);
        if (this.mText == null) {
        }
        if (this.mTransformed == null) {
        }
        if (textIsSetFromXml10) {
        }
        hint = hint2;
        if (hint != null) {
        }
        TypedArray a322 = context.obtainStyledAttributes(attrs, C4057R.styleable.View, defStyleAttr, defStyleRes);
        if (this.mMovement == null) {
        }
        if (canInputOrMove) {
        }
        if (canInputOrMove) {
        }
        int focusable222 = getFocusable();
        n = a322.getIndexCount();
        i = 0;
        boolean clickable222 = clickable;
        boolean longClickable222 = longClickable;
        focusable = focusable222;
        while (i < n) {
        }
        a322.recycle();
        if (focusable != getFocusable()) {
        }
        setClickable(clickable222);
        setLongClickable(longClickable222);
        editor2 = this.mEditor;
        if (editor2 != null) {
        }
        if (getImportantForAccessibility() != 0) {
        }
        if (!supportsAutoSizeText()) {
        }
        if (firstBaselineToTopHeight3 >= 0) {
        }
        if (lastBaselineToBottomHeight2 >= 0) {
        }
        if (lineHeight41 >= 0) {
        }
    }

    private void setTextInternal(CharSequence text) {
        this.mText = text;
        this.mSpannable = text instanceof Spannable ? (Spannable) text : null;
        this.mPrecomputed = text instanceof PrecomputedText ? (PrecomputedText) text : null;
    }

    public void setAutoSizeTextTypeWithDefaults(int autoSizeTextType) {
        if (supportsAutoSizeText()) {
            switch (autoSizeTextType) {
                case 0:
                    clearAutoSizeConfiguration();
                    return;
                case 1:
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    float autoSizeMinTextSizeInPx = TypedValue.applyDimension(2, 12.0f, displayMetrics);
                    float autoSizeMaxTextSizeInPx = TypedValue.applyDimension(2, 112.0f, displayMetrics);
                    validateAndSetAutoSizeTextTypeUniformConfiguration(autoSizeMinTextSizeInPx, autoSizeMaxTextSizeInPx, 1.0f);
                    if (setupAutoSizeText()) {
                        autoSizeText();
                        invalidate();
                        return;
                    }
                    return;
                default:
                    throw new IllegalArgumentException("Unknown auto-size text type: " + autoSizeTextType);
            }
        }
    }

    public void setAutoSizeTextTypeUniformWithConfiguration(int autoSizeMinTextSize, int autoSizeMaxTextSize, int autoSizeStepGranularity, int unit) {
        if (supportsAutoSizeText()) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float autoSizeMinTextSizeInPx = TypedValue.applyDimension(unit, autoSizeMinTextSize, displayMetrics);
            float autoSizeMaxTextSizeInPx = TypedValue.applyDimension(unit, autoSizeMaxTextSize, displayMetrics);
            float autoSizeStepGranularityInPx = TypedValue.applyDimension(unit, autoSizeStepGranularity, displayMetrics);
            validateAndSetAutoSizeTextTypeUniformConfiguration(autoSizeMinTextSizeInPx, autoSizeMaxTextSizeInPx, autoSizeStepGranularityInPx);
            if (setupAutoSizeText()) {
                autoSizeText();
                invalidate();
            }
        }
    }

    public void setAutoSizeTextTypeUniformWithPresetSizes(int[] presetSizes, int unit) {
        if (supportsAutoSizeText()) {
            int presetSizesLength = presetSizes.length;
            if (presetSizesLength > 0) {
                int[] presetSizesInPx = new int[presetSizesLength];
                if (unit == 0) {
                    presetSizesInPx = Arrays.copyOf(presetSizes, presetSizesLength);
                } else {
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    for (int i = 0; i < presetSizesLength; i++) {
                        presetSizesInPx[i] = Math.round(TypedValue.applyDimension(unit, presetSizes[i], displayMetrics));
                    }
                }
                this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(presetSizesInPx);
                if (!setupAutoSizeUniformPresetSizesConfiguration()) {
                    throw new IllegalArgumentException("None of the preset sizes is valid: " + Arrays.toString(presetSizes));
                }
            } else {
                this.mHasPresetAutoSizeValues = false;
            }
            if (setupAutoSizeText()) {
                autoSizeText();
                invalidate();
            }
        }
    }

    public int getAutoSizeTextType() {
        return this.mAutoSizeTextType;
    }

    public int getAutoSizeStepGranularity() {
        return Math.round(this.mAutoSizeStepGranularityInPx);
    }

    public int getAutoSizeMinTextSize() {
        return Math.round(this.mAutoSizeMinTextSizeInPx);
    }

    public int getAutoSizeMaxTextSize() {
        return Math.round(this.mAutoSizeMaxTextSizeInPx);
    }

    public int[] getAutoSizeTextAvailableSizes() {
        return this.mAutoSizeTextSizesInPx;
    }

    private void setupAutoSizeUniformPresetSizes(TypedArray textSizes) {
        int textSizesLength = textSizes.length();
        int[] parsedSizes = new int[textSizesLength];
        if (textSizesLength > 0) {
            for (int i = 0; i < textSizesLength; i++) {
                parsedSizes[i] = textSizes.getDimensionPixelSize(i, -1);
            }
            this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(parsedSizes);
            setupAutoSizeUniformPresetSizesConfiguration();
        }
    }

    private boolean setupAutoSizeUniformPresetSizesConfiguration() {
        int[] iArr = this.mAutoSizeTextSizesInPx;
        int sizesLength = iArr.length;
        boolean z = sizesLength > 0;
        this.mHasPresetAutoSizeValues = z;
        if (z) {
            this.mAutoSizeTextType = 1;
            this.mAutoSizeMinTextSizeInPx = iArr[0];
            this.mAutoSizeMaxTextSizeInPx = iArr[sizesLength - 1];
            this.mAutoSizeStepGranularityInPx = -1.0f;
        }
        return z;
    }

    private void validateAndSetAutoSizeTextTypeUniformConfiguration(float autoSizeMinTextSizeInPx, float autoSizeMaxTextSizeInPx, float autoSizeStepGranularityInPx) {
        if (autoSizeMinTextSizeInPx <= 0.0f) {
            throw new IllegalArgumentException("Minimum auto-size text size (" + autoSizeMinTextSizeInPx + "px) is less or equal to (0px)");
        }
        if (autoSizeMaxTextSizeInPx <= autoSizeMinTextSizeInPx) {
            throw new IllegalArgumentException("Maximum auto-size text size (" + autoSizeMaxTextSizeInPx + "px) is less or equal to minimum auto-size text size (" + autoSizeMinTextSizeInPx + "px)");
        }
        if (autoSizeStepGranularityInPx <= 0.0f) {
            throw new IllegalArgumentException("The auto-size step granularity (" + autoSizeStepGranularityInPx + "px) is less or equal to (0px)");
        }
        this.mAutoSizeTextType = 1;
        this.mAutoSizeMinTextSizeInPx = autoSizeMinTextSizeInPx;
        this.mAutoSizeMaxTextSizeInPx = autoSizeMaxTextSizeInPx;
        this.mAutoSizeStepGranularityInPx = autoSizeStepGranularityInPx;
        this.mHasPresetAutoSizeValues = false;
    }

    private void clearAutoSizeConfiguration() {
        this.mAutoSizeTextType = 0;
        this.mAutoSizeMinTextSizeInPx = -1.0f;
        this.mAutoSizeMaxTextSizeInPx = -1.0f;
        this.mAutoSizeStepGranularityInPx = -1.0f;
        this.mAutoSizeTextSizesInPx = EmptyArray.INT;
        this.mNeedsAutoSizeText = false;
    }

    private int[] cleanupAutoSizePresetSizes(int[] presetValues) {
        int presetValuesLength = presetValues.length;
        if (presetValuesLength == 0) {
            return presetValues;
        }
        Arrays.sort(presetValues);
        IntArray uniqueValidSizes = new IntArray();
        for (int currentPresetValue : presetValues) {
            if (currentPresetValue > 0 && uniqueValidSizes.binarySearch(currentPresetValue) < 0) {
                uniqueValidSizes.add(currentPresetValue);
            }
        }
        int i = uniqueValidSizes.size();
        if (presetValuesLength == i) {
            return presetValues;
        }
        return uniqueValidSizes.toArray();
    }

    private boolean setupAutoSizeText() {
        if (supportsAutoSizeText() && this.mAutoSizeTextType == 1) {
            if (!this.mHasPresetAutoSizeValues || this.mAutoSizeTextSizesInPx.length == 0) {
                int autoSizeValuesLength = ((int) Math.floor((this.mAutoSizeMaxTextSizeInPx - this.mAutoSizeMinTextSizeInPx) / this.mAutoSizeStepGranularityInPx)) + 1;
                int[] autoSizeTextSizesInPx = new int[autoSizeValuesLength];
                for (int i = 0; i < autoSizeValuesLength; i++) {
                    autoSizeTextSizesInPx[i] = Math.round(this.mAutoSizeMinTextSizeInPx + (i * this.mAutoSizeStepGranularityInPx));
                }
                this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(autoSizeTextSizesInPx);
            }
            this.mNeedsAutoSizeText = true;
        } else {
            this.mNeedsAutoSizeText = false;
        }
        return this.mNeedsAutoSizeText;
    }

    private int[] parseDimensionArray(TypedArray dimens) {
        if (dimens == null) {
            return null;
        }
        int[] result = new int[dimens.length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = dimens.getDimensionPixelSize(i, 0);
        }
        return result;
    }

    @Override // android.view.View
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == -1 && data != null) {
                CharSequence result = data.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
                if (result != null) {
                    if (isTextEditable()) {
                        ClipData clip = ClipData.newPlainText("", result);
                        ContentInfo payload = new ContentInfo.Builder(clip, 5).build();
                        performReceiveContent(payload);
                        Editor editor = this.mEditor;
                        if (editor != null) {
                            editor.refreshTextActionMode();
                            return;
                        }
                        return;
                    } else if (result.length() > 0) {
                        Toast.makeText(getContext(), String.valueOf(result), 1).show();
                        return;
                    } else {
                        return;
                    }
                }
                return;
            }
            Spannable spannable = this.mSpannable;
            if (spannable != null) {
                Selection.setSelection(spannable, getSelectionEnd());
            }
        }
    }

    private void setTypefaceFromAttrs(Typeface typeface, String familyName, int typefaceIndex, int style, int weight) {
        if (typeface == null && familyName != null) {
            Typeface normalTypeface = Typeface.create(familyName, 0);
            resolveStyleAndSetTypeface(normalTypeface, style, weight);
        } else if (typeface != null) {
            resolveStyleAndSetTypeface(typeface, style, weight);
        } else {
            switch (typefaceIndex) {
                case 1:
                    resolveStyleAndSetTypeface(Typeface.SANS_SERIF, style, weight);
                    return;
                case 2:
                    resolveStyleAndSetTypeface(Typeface.SERIF, style, weight);
                    return;
                case 3:
                    resolveStyleAndSetTypeface(Typeface.MONOSPACE, style, weight);
                    return;
                default:
                    resolveStyleAndSetTypeface(null, style, weight);
                    return;
            }
        }
    }

    private void resolveStyleAndSetTypeface(Typeface typeface, int style, int weight) {
        if (weight >= 0) {
            int weight2 = Math.min(1000, weight);
            boolean italic = (style & 2) != 0;
            setTypeface(Typeface.create(typeface, weight2, italic));
            return;
        }
        setTypeface(typeface, style);
    }

    private void setRelativeDrawablesIfNeeded(Drawable start, Drawable end) {
        boolean hasRelativeDrawables = (start == null && end == null) ? false : true;
        if (hasRelativeDrawables) {
            Drawables dr = this.mDrawables;
            if (dr == null) {
                Drawables drawables = new Drawables(getContext());
                dr = drawables;
                this.mDrawables = drawables;
            }
            this.mDrawables.mOverride = true;
            Rect compoundRect = dr.mCompoundRect;
            int[] state = getDrawableState();
            if (start != null) {
                start.setBounds(0, 0, start.getIntrinsicWidth(), start.getIntrinsicHeight());
                start.setState(state);
                start.copyBounds(compoundRect);
                start.setCallback(this);
                dr.mDrawableStart = start;
                dr.mDrawableSizeStart = compoundRect.width();
                dr.mDrawableHeightStart = compoundRect.height();
            } else {
                dr.mDrawableHeightStart = 0;
                dr.mDrawableSizeStart = 0;
            }
            if (end != null) {
                end.setBounds(0, 0, end.getIntrinsicWidth(), end.getIntrinsicHeight());
                end.setState(state);
                end.copyBounds(compoundRect);
                end.setCallback(this);
                dr.mDrawableEnd = end;
                dr.mDrawableSizeEnd = compoundRect.width();
                dr.mDrawableHeightEnd = compoundRect.height();
            } else {
                dr.mDrawableHeightEnd = 0;
                dr.mDrawableSizeEnd = 0;
            }
            resetResolvedDrawables();
            resolveDrawables();
            applyCompoundDrawableTint();
        }
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setEnabled(boolean enabled) {
        InputMethodManager imm;
        InputMethodManager imm2;
        if (enabled == isEnabled()) {
            return;
        }
        if (!enabled && (imm2 = getInputMethodManager()) != null && imm2.isActive(this)) {
            imm2.hideSoftInputFromWindow(getWindowToken(), 0);
        }
        super.setEnabled(enabled);
        if (enabled && (imm = getInputMethodManager()) != null) {
            imm.restartInput(this);
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.invalidateTextDisplayList();
            this.mEditor.prepareCursorControllers();
            this.mEditor.makeBlink();
        }
    }

    public void setTypeface(Typeface tf, int style) {
        Typeface tf2;
        if (style > 0) {
            if (tf == null) {
                tf2 = Typeface.defaultFromStyle(style);
            } else {
                tf2 = Typeface.create(tf, style);
            }
            setTypeface(tf2);
            int typefaceStyle = tf2 != null ? tf2.getStyle() : 0;
            int need = (~typefaceStyle) & style;
            this.mTextPaint.setFakeBoldText((need & 1) != 0);
            this.mTextPaint.setTextSkewX((need & 2) != 0 ? -0.25f : 0.0f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setTypeface(tf);
    }

    protected boolean getDefaultEditable() {
        return false;
    }

    protected MovementMethod getDefaultMovementMethod() {
        return null;
    }

    @ViewDebug.CapturedViewProperty
    public CharSequence getText() {
        ViewTranslationCallback callback;
        if (this.mUseTextPaddingForUiTranslation && (callback = getViewTranslationCallback()) != null && (callback instanceof TextViewTranslationCallback)) {
            TextViewTranslationCallback defaultCallback = (TextViewTranslationCallback) callback;
            if (defaultCallback.isTextPaddingEnabled() && defaultCallback.isShowingTranslation()) {
                return defaultCallback.getPaddedText(this.mText, this.mTransformed);
            }
        }
        return this.mText;
    }

    public int length() {
        return this.mText.length();
    }

    public Editable getEditableText() {
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Editable) {
            return (Editable) charSequence;
        }
        return null;
    }

    public CharSequence getTransformed() {
        return this.mTransformed;
    }

    public int getLineHeight() {
        return FastMath.round((this.mTextPaint.getFontMetricsInt(null) * this.mSpacingMult) + this.mSpacingAdd);
    }

    public final Layout getLayout() {
        return this.mLayout;
    }

    final Layout getHintLayout() {
        return this.mHintLayout;
    }

    public final UndoManager getUndoManager() {
        throw new UnsupportedOperationException("not implemented");
    }

    public final Editor getEditorForTesting() {
        return this.mEditor;
    }

    public final void setUndoManager(UndoManager undoManager, String tag) {
        throw new UnsupportedOperationException("not implemented");
    }

    public final KeyListener getKeyListener() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return null;
        }
        return editor.mKeyListener;
    }

    public void setKeyListener(KeyListener input) {
        this.mListenerChanged = true;
        setKeyListenerOnly(input);
        fixFocusableAndClickableSettings();
        if (input != null) {
            createEditorIfNeeded();
            setInputTypeFromEditor();
        } else {
            Editor editor = this.mEditor;
            if (editor != null) {
                editor.mInputType = 0;
            }
        }
        InputMethodManager imm = getInputMethodManager();
        if (imm != null) {
            imm.restartInput(this);
        }
    }

    private void setInputTypeFromEditor() {
        try {
            Editor editor = this.mEditor;
            editor.mInputType = editor.mKeyListener.getInputType();
        } catch (IncompatibleClassChangeError e) {
            this.mEditor.mInputType = 1;
        }
        setInputTypeSingleLine(this.mSingleLine);
    }

    private void setKeyListenerOnly(KeyListener input) {
        if (this.mEditor == null && input == null) {
            return;
        }
        createEditorIfNeeded();
        if (this.mEditor.mKeyListener != input) {
            this.mEditor.mKeyListener = input;
            if (input != null) {
                CharSequence charSequence = this.mText;
                if (!(charSequence instanceof Editable)) {
                    setText(charSequence);
                }
            }
            setFilters((Editable) this.mText, this.mFilters);
        }
    }

    public final MovementMethod getMovementMethod() {
        return this.mMovement;
    }

    public final void setMovementMethod(MovementMethod movement) {
        if (this.mMovement != movement) {
            this.mMovement = movement;
            if (movement != null && this.mSpannable == null) {
                setText(this.mText);
            }
            fixFocusableAndClickableSettings();
            Editor editor = this.mEditor;
            if (editor != null) {
                editor.prepareCursorControllers();
            }
        }
    }

    private void fixFocusableAndClickableSettings() {
        Editor editor;
        if (this.mMovement != null || ((editor = this.mEditor) != null && editor.mKeyListener != null)) {
            setFocusable(1);
            setClickable(true);
            setLongClickable(true);
            return;
        }
        setFocusable(16);
        setClickable(false);
        setLongClickable(false);
    }

    public final TransformationMethod getTransformationMethod() {
        return this.mTransformation;
    }

    public final void setTransformationMethod(TransformationMethod method) {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.setTransformationMethod(method);
        } else {
            setTransformationMethodInternal(method);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTransformationMethodInternal(TransformationMethod method) {
        Spannable spannable;
        TransformationMethod transformationMethod = this.mTransformation;
        if (method == transformationMethod) {
            return;
        }
        if (transformationMethod != null && (spannable = this.mSpannable) != null) {
            spannable.removeSpan(transformationMethod);
        }
        this.mTransformation = method;
        if (method instanceof TransformationMethod2) {
            TransformationMethod2 method2 = (TransformationMethod2) method;
            boolean z = (isTextSelectable() || (this.mText instanceof Editable)) ? false : true;
            this.mAllowTransformationLengthChange = z;
            method2.setLengthChangesAllowed(z);
        } else {
            this.mAllowTransformationLengthChange = false;
        }
        setText(this.mText);
        if (hasPasswordTransformationMethod()) {
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
        this.mTextDir = getTextDirectionHeuristic();
    }

    public int getCompoundPaddingTop() {
        Drawables dr = this.mDrawables;
        if (dr == null || dr.mShowing[1] == null) {
            return this.mPaddingTop;
        }
        return this.mPaddingTop + dr.mDrawablePadding + dr.mDrawableSizeTop;
    }

    public int getCompoundPaddingBottom() {
        Drawables dr = this.mDrawables;
        if (dr == null || dr.mShowing[3] == null) {
            return this.mPaddingBottom;
        }
        return this.mPaddingBottom + dr.mDrawablePadding + dr.mDrawableSizeBottom;
    }

    public int getCompoundPaddingLeft() {
        Drawables dr = this.mDrawables;
        if (dr == null || dr.mShowing[0] == null) {
            return this.mPaddingLeft;
        }
        return this.mPaddingLeft + dr.mDrawablePadding + dr.mDrawableSizeLeft;
    }

    public int getCompoundPaddingRight() {
        Drawables dr = this.mDrawables;
        if (dr == null || dr.mShowing[2] == null) {
            return this.mPaddingRight;
        }
        return this.mPaddingRight + dr.mDrawablePadding + dr.mDrawableSizeRight;
    }

    public int getCompoundPaddingStart() {
        resolveDrawables();
        switch (getLayoutDirection()) {
            case 1:
                return getCompoundPaddingRight();
            default:
                return getCompoundPaddingLeft();
        }
    }

    public int getCompoundPaddingEnd() {
        resolveDrawables();
        switch (getLayoutDirection()) {
            case 1:
                return getCompoundPaddingLeft();
            default:
                return getCompoundPaddingRight();
        }
    }

    public int getExtendedPaddingTop() {
        if (this.mMaxMode != 1) {
            return getCompoundPaddingTop();
        }
        if (this.mLayout == null) {
            assumeLayout();
        }
        if (this.mLayout.getLineCount() <= this.mMaximum) {
            return getCompoundPaddingTop();
        }
        int top = getCompoundPaddingTop();
        int bottom = getCompoundPaddingBottom();
        int viewht = (getHeight() - top) - bottom;
        int layoutht = this.mLayout.getLineTop(this.mMaximum);
        if (layoutht >= viewht) {
            return top;
        }
        int gravity = this.mGravity & 112;
        if (gravity == 48) {
            return top;
        }
        if (gravity == 80) {
            return (top + viewht) - layoutht;
        }
        return ((viewht - layoutht) / 2) + top;
    }

    public int getExtendedPaddingBottom() {
        if (this.mMaxMode != 1) {
            return getCompoundPaddingBottom();
        }
        if (this.mLayout == null) {
            assumeLayout();
        }
        if (this.mLayout.getLineCount() <= this.mMaximum) {
            return getCompoundPaddingBottom();
        }
        int top = getCompoundPaddingTop();
        int bottom = getCompoundPaddingBottom();
        int viewht = (getHeight() - top) - bottom;
        int layoutht = this.mLayout.getLineTop(this.mMaximum);
        if (layoutht >= viewht) {
            return bottom;
        }
        int gravity = this.mGravity & 112;
        if (gravity == 48) {
            return (bottom + viewht) - layoutht;
        }
        if (gravity == 80) {
            return bottom;
        }
        return ((viewht - layoutht) / 2) + bottom;
    }

    public int getTotalPaddingLeft() {
        return getCompoundPaddingLeft();
    }

    public int getTotalPaddingRight() {
        return getCompoundPaddingRight();
    }

    public int getTotalPaddingStart() {
        return getCompoundPaddingStart();
    }

    public int getTotalPaddingEnd() {
        return getCompoundPaddingEnd();
    }

    public int getTotalPaddingTop() {
        return getExtendedPaddingTop() + getVerticalOffset(true);
    }

    public int getTotalPaddingBottom() {
        return getExtendedPaddingBottom() + getBottomVerticalOffset(true);
    }

    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        Drawables dr = this.mDrawables;
        if (dr != null) {
            if (dr.mDrawableStart != null) {
                dr.mDrawableStart.setCallback(null);
            }
            dr.mDrawableStart = null;
            if (dr.mDrawableEnd != null) {
                dr.mDrawableEnd.setCallback(null);
            }
            dr.mDrawableEnd = null;
            dr.mDrawableHeightStart = 0;
            dr.mDrawableSizeStart = 0;
            dr.mDrawableHeightEnd = 0;
            dr.mDrawableSizeEnd = 0;
        }
        boolean drawables = (left == null && top == null && right == null && bottom == null) ? false : true;
        if (!drawables) {
            if (dr != null) {
                if (dr.hasMetadata()) {
                    for (int i = dr.mShowing.length - 1; i >= 0; i--) {
                        if (dr.mShowing[i] != null) {
                            dr.mShowing[i].setCallback(null);
                        }
                        dr.mShowing[i] = null;
                    }
                    dr.mDrawableHeightLeft = 0;
                    dr.mDrawableSizeLeft = 0;
                    dr.mDrawableHeightRight = 0;
                    dr.mDrawableSizeRight = 0;
                    dr.mDrawableWidthTop = 0;
                    dr.mDrawableSizeTop = 0;
                    dr.mDrawableWidthBottom = 0;
                    dr.mDrawableSizeBottom = 0;
                } else {
                    this.mDrawables = null;
                }
            }
        } else {
            if (dr == null) {
                Drawables drawables2 = new Drawables(getContext());
                dr = drawables2;
                this.mDrawables = drawables2;
            }
            this.mDrawables.mOverride = false;
            if (dr.mShowing[0] != left && dr.mShowing[0] != null) {
                dr.mShowing[0].setCallback(null);
            }
            dr.mShowing[0] = left;
            if (dr.mShowing[1] != top && dr.mShowing[1] != null) {
                dr.mShowing[1].setCallback(null);
            }
            dr.mShowing[1] = top;
            if (dr.mShowing[2] != right && dr.mShowing[2] != null) {
                dr.mShowing[2].setCallback(null);
            }
            dr.mShowing[2] = right;
            if (dr.mShowing[3] != bottom && dr.mShowing[3] != null) {
                dr.mShowing[3].setCallback(null);
            }
            dr.mShowing[3] = bottom;
            Rect compoundRect = dr.mCompoundRect;
            int[] state = getDrawableState();
            if (left != null) {
                left.setState(state);
                left.copyBounds(compoundRect);
                left.setCallback(this);
                dr.mDrawableSizeLeft = compoundRect.width();
                dr.mDrawableHeightLeft = compoundRect.height();
            } else {
                dr.mDrawableHeightLeft = 0;
                dr.mDrawableSizeLeft = 0;
            }
            if (right != null) {
                right.setState(state);
                right.copyBounds(compoundRect);
                right.setCallback(this);
                dr.mDrawableSizeRight = compoundRect.width();
                dr.mDrawableHeightRight = compoundRect.height();
            } else {
                dr.mDrawableHeightRight = 0;
                dr.mDrawableSizeRight = 0;
            }
            if (top != null) {
                top.setState(state);
                top.copyBounds(compoundRect);
                top.setCallback(this);
                dr.mDrawableSizeTop = compoundRect.height();
                dr.mDrawableWidthTop = compoundRect.width();
            } else {
                dr.mDrawableWidthTop = 0;
                dr.mDrawableSizeTop = 0;
            }
            if (bottom != null) {
                bottom.setState(state);
                bottom.copyBounds(compoundRect);
                bottom.setCallback(this);
                dr.mDrawableSizeBottom = compoundRect.height();
                dr.mDrawableWidthBottom = compoundRect.width();
            } else {
                dr.mDrawableWidthBottom = 0;
                dr.mDrawableSizeBottom = 0;
            }
        }
        if (dr != null) {
            dr.mDrawableLeftInitial = left;
            dr.mDrawableRightInitial = right;
        }
        resetResolvedDrawables();
        resolveDrawables();
        applyCompoundDrawableTint();
        invalidate();
        requestLayout();
    }

    @RemotableViewMethod
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
        Context context = getContext();
        setCompoundDrawablesWithIntrinsicBounds(left != 0 ? context.getDrawable(left) : null, top != 0 ? context.getDrawable(top) : null, right != 0 ? context.getDrawable(right) : null, bottom != 0 ? context.getDrawable(bottom) : null);
    }

    @RemotableViewMethod
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (left != null) {
            left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
        }
        if (right != null) {
            right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
        }
        if (top != null) {
            top.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, bottom.getIntrinsicWidth(), bottom.getIntrinsicHeight());
        }
        setCompoundDrawables(left, top, right, bottom);
    }

    @RemotableViewMethod
    public void setCompoundDrawablesRelative(Drawable start, Drawable top, Drawable end, Drawable bottom) {
        boolean drawables;
        Drawables dr = this.mDrawables;
        if (dr != null) {
            if (dr.mShowing[0] != null) {
                dr.mShowing[0].setCallback(null);
            }
            Drawable[] drawableArr = dr.mShowing;
            dr.mDrawableLeftInitial = null;
            drawableArr[0] = null;
            if (dr.mShowing[2] != null) {
                dr.mShowing[2].setCallback(null);
            }
            Drawable[] drawableArr2 = dr.mShowing;
            dr.mDrawableRightInitial = null;
            drawableArr2[2] = null;
            dr.mDrawableHeightLeft = 0;
            dr.mDrawableSizeLeft = 0;
            dr.mDrawableHeightRight = 0;
            dr.mDrawableSizeRight = 0;
        }
        if (start == null && top == null && end == null && bottom == null) {
            drawables = false;
        } else {
            drawables = true;
        }
        if (!drawables) {
            if (dr != null) {
                if (!dr.hasMetadata()) {
                    this.mDrawables = null;
                } else {
                    if (dr.mDrawableStart != null) {
                        dr.mDrawableStart.setCallback(null);
                    }
                    dr.mDrawableStart = null;
                    if (dr.mShowing[1] != null) {
                        dr.mShowing[1].setCallback(null);
                    }
                    dr.mShowing[1] = null;
                    if (dr.mDrawableEnd != null) {
                        dr.mDrawableEnd.setCallback(null);
                    }
                    dr.mDrawableEnd = null;
                    if (dr.mShowing[3] != null) {
                        dr.mShowing[3].setCallback(null);
                    }
                    dr.mShowing[3] = null;
                    dr.mDrawableHeightStart = 0;
                    dr.mDrawableSizeStart = 0;
                    dr.mDrawableHeightEnd = 0;
                    dr.mDrawableSizeEnd = 0;
                    dr.mDrawableWidthTop = 0;
                    dr.mDrawableSizeTop = 0;
                    dr.mDrawableWidthBottom = 0;
                    dr.mDrawableSizeBottom = 0;
                }
            }
        } else {
            if (dr == null) {
                Drawables drawables2 = new Drawables(getContext());
                dr = drawables2;
                this.mDrawables = drawables2;
            }
            this.mDrawables.mOverride = true;
            if (dr.mDrawableStart != start && dr.mDrawableStart != null) {
                dr.mDrawableStart.setCallback(null);
            }
            dr.mDrawableStart = start;
            if (dr.mShowing[1] != top && dr.mShowing[1] != null) {
                dr.mShowing[1].setCallback(null);
            }
            dr.mShowing[1] = top;
            if (dr.mDrawableEnd != end && dr.mDrawableEnd != null) {
                dr.mDrawableEnd.setCallback(null);
            }
            dr.mDrawableEnd = end;
            if (dr.mShowing[3] != bottom && dr.mShowing[3] != null) {
                dr.mShowing[3].setCallback(null);
            }
            dr.mShowing[3] = bottom;
            Rect compoundRect = dr.mCompoundRect;
            int[] state = getDrawableState();
            if (start != null) {
                start.setState(state);
                start.copyBounds(compoundRect);
                start.setCallback(this);
                dr.mDrawableSizeStart = compoundRect.width();
                dr.mDrawableHeightStart = compoundRect.height();
            } else {
                dr.mDrawableHeightStart = 0;
                dr.mDrawableSizeStart = 0;
            }
            if (end != null) {
                end.setState(state);
                end.copyBounds(compoundRect);
                end.setCallback(this);
                dr.mDrawableSizeEnd = compoundRect.width();
                dr.mDrawableHeightEnd = compoundRect.height();
            } else {
                dr.mDrawableHeightEnd = 0;
                dr.mDrawableSizeEnd = 0;
            }
            if (top != null) {
                top.setState(state);
                top.copyBounds(compoundRect);
                top.setCallback(this);
                dr.mDrawableSizeTop = compoundRect.height();
                dr.mDrawableWidthTop = compoundRect.width();
            } else {
                dr.mDrawableWidthTop = 0;
                dr.mDrawableSizeTop = 0;
            }
            if (bottom != null) {
                bottom.setState(state);
                bottom.copyBounds(compoundRect);
                bottom.setCallback(this);
                dr.mDrawableSizeBottom = compoundRect.height();
                dr.mDrawableWidthBottom = compoundRect.width();
            } else {
                dr.mDrawableWidthBottom = 0;
                dr.mDrawableSizeBottom = 0;
            }
        }
        resetResolvedDrawables();
        resolveDrawables();
        invalidate();
        requestLayout();
    }

    @RemotableViewMethod
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end, int bottom) {
        Context context = getContext();
        setCompoundDrawablesRelativeWithIntrinsicBounds(start != 0 ? context.getDrawable(start) : null, top != 0 ? context.getDrawable(top) : null, end != 0 ? context.getDrawable(end) : null, bottom != 0 ? context.getDrawable(bottom) : null);
    }

    @RemotableViewMethod
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(Drawable start, Drawable top, Drawable end, Drawable bottom) {
        if (start != null) {
            start.setBounds(0, 0, start.getIntrinsicWidth(), start.getIntrinsicHeight());
        }
        if (end != null) {
            end.setBounds(0, 0, end.getIntrinsicWidth(), end.getIntrinsicHeight());
        }
        if (top != null) {
            top.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, bottom.getIntrinsicWidth(), bottom.getIntrinsicHeight());
        }
        setCompoundDrawablesRelative(start, top, end, bottom);
    }

    public Drawable[] getCompoundDrawables() {
        Drawables dr = this.mDrawables;
        if (dr != null) {
            return (Drawable[]) dr.mShowing.clone();
        }
        return new Drawable[]{null, null, null, null};
    }

    public Drawable[] getCompoundDrawablesRelative() {
        Drawables dr = this.mDrawables;
        if (dr != null) {
            return new Drawable[]{dr.mDrawableStart, dr.mShowing[1], dr.mDrawableEnd, dr.mShowing[3]};
        }
        return new Drawable[]{null, null, null, null};
    }

    @RemotableViewMethod
    public void setCompoundDrawablePadding(int pad) {
        Drawables dr = this.mDrawables;
        if (pad == 0) {
            if (dr != null) {
                dr.mDrawablePadding = pad;
            }
        } else {
            if (dr == null) {
                Drawables drawables = new Drawables(getContext());
                dr = drawables;
                this.mDrawables = drawables;
            }
            dr.mDrawablePadding = pad;
        }
        invalidate();
        requestLayout();
    }

    public int getCompoundDrawablePadding() {
        Drawables dr = this.mDrawables;
        if (dr != null) {
            return dr.mDrawablePadding;
        }
        return 0;
    }

    public void setCompoundDrawableTintList(ColorStateList tint) {
        if (this.mDrawables == null) {
            this.mDrawables = new Drawables(getContext());
        }
        this.mDrawables.mTintList = tint;
        this.mDrawables.mHasTint = true;
        applyCompoundDrawableTint();
    }

    public ColorStateList getCompoundDrawableTintList() {
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            return drawables.mTintList;
        }
        return null;
    }

    public void setCompoundDrawableTintMode(PorterDuff.Mode tintMode) {
        setCompoundDrawableTintBlendMode(tintMode != null ? BlendMode.fromValue(tintMode.nativeInt) : null);
    }

    public void setCompoundDrawableTintBlendMode(BlendMode blendMode) {
        if (this.mDrawables == null) {
            this.mDrawables = new Drawables(getContext());
        }
        this.mDrawables.mBlendMode = blendMode;
        this.mDrawables.mHasTintMode = true;
        applyCompoundDrawableTint();
    }

    public PorterDuff.Mode getCompoundDrawableTintMode() {
        BlendMode mode = getCompoundDrawableTintBlendMode();
        if (mode != null) {
            return BlendMode.blendModeToPorterDuffMode(mode);
        }
        return null;
    }

    public BlendMode getCompoundDrawableTintBlendMode() {
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            return drawables.mBlendMode;
        }
        return null;
    }

    private void applyCompoundDrawableTint() {
        Drawable[] drawableArr;
        Drawables drawables = this.mDrawables;
        if (drawables == null) {
            return;
        }
        if (drawables.mHasTint || this.mDrawables.mHasTintMode) {
            ColorStateList tintList = this.mDrawables.mTintList;
            BlendMode blendMode = this.mDrawables.mBlendMode;
            boolean hasTint = this.mDrawables.mHasTint;
            boolean hasTintMode = this.mDrawables.mHasTintMode;
            int[] state = getDrawableState();
            for (Drawable dr : this.mDrawables.mShowing) {
                if (dr != null && dr != this.mDrawables.mDrawableError) {
                    dr.mutate();
                    if (hasTint) {
                        dr.setTintList(tintList);
                    }
                    if (hasTintMode) {
                        dr.setTintBlendMode(blendMode);
                    }
                    if (dr.isStateful()) {
                        dr.setState(state);
                    }
                }
            }
        }
    }

    @Override // android.view.View
    public void setPadding(int left, int top, int right, int bottom) {
        if (left != this.mPaddingLeft || right != this.mPaddingRight || top != this.mPaddingTop || bottom != this.mPaddingBottom) {
            nullLayouts();
        }
        super.setPadding(left, top, right, bottom);
        invalidate();
    }

    @Override // android.view.View
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        if (start != getPaddingStart() || end != getPaddingEnd() || top != this.mPaddingTop || bottom != this.mPaddingBottom) {
            nullLayouts();
        }
        super.setPaddingRelative(start, top, end, bottom);
        invalidate();
    }

    public void setFirstBaselineToTopHeight(int firstBaselineToTopHeight) {
        int fontMetricsTop;
        Preconditions.checkArgumentNonnegative(firstBaselineToTopHeight);
        Paint.FontMetricsInt fontMetrics = getPaint().getFontMetricsInt();
        if (getIncludeFontPadding()) {
            fontMetricsTop = fontMetrics.top;
        } else {
            fontMetricsTop = fontMetrics.ascent;
        }
        if (firstBaselineToTopHeight > Math.abs(fontMetricsTop)) {
            int paddingTop = firstBaselineToTopHeight - (-fontMetricsTop);
            setPadding(getPaddingLeft(), paddingTop, getPaddingRight(), getPaddingBottom());
        }
    }

    public void setLastBaselineToBottomHeight(int lastBaselineToBottomHeight) {
        int fontMetricsBottom;
        Preconditions.checkArgumentNonnegative(lastBaselineToBottomHeight);
        Paint.FontMetricsInt fontMetrics = getPaint().getFontMetricsInt();
        if (getIncludeFontPadding()) {
            fontMetricsBottom = fontMetrics.bottom;
        } else {
            fontMetricsBottom = fontMetrics.descent;
        }
        if (lastBaselineToBottomHeight > Math.abs(fontMetricsBottom)) {
            int paddingBottom = lastBaselineToBottomHeight - fontMetricsBottom;
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), paddingBottom);
        }
    }

    public int getFirstBaselineToTopHeight() {
        return getPaddingTop() - getPaint().getFontMetricsInt().top;
    }

    public int getLastBaselineToBottomHeight() {
        return getPaddingBottom() + getPaint().getFontMetricsInt().bottom;
    }

    public final int getAutoLinkMask() {
        return this.mAutoLinkMask;
    }

    @RemotableViewMethod
    public void setTextSelectHandle(Drawable textSelectHandle) {
        Preconditions.checkNotNull(textSelectHandle, "The text select handle should not be null.");
        this.mTextSelectHandle = textSelectHandle;
        this.mTextSelectHandleRes = 0;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.loadHandleDrawables(true);
        }
    }

    @RemotableViewMethod
    public void setTextSelectHandle(int textSelectHandle) {
        Preconditions.checkArgument(textSelectHandle != 0, "The text select handle should be a valid drawable resource id.");
        setTextSelectHandle(this.mContext.getDrawable(textSelectHandle));
    }

    public Drawable getTextSelectHandle() {
        if (this.mTextSelectHandle == null && this.mTextSelectHandleRes != 0) {
            this.mTextSelectHandle = this.mContext.getDrawable(this.mTextSelectHandleRes);
        }
        return this.mTextSelectHandle;
    }

    @RemotableViewMethod
    public void setTextSelectHandleLeft(Drawable textSelectHandleLeft) {
        Preconditions.checkNotNull(textSelectHandleLeft, "The left text select handle should not be null.");
        this.mTextSelectHandleLeft = textSelectHandleLeft;
        this.mTextSelectHandleLeftRes = 0;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.loadHandleDrawables(true);
        }
    }

    @RemotableViewMethod
    public void setTextSelectHandleLeft(int textSelectHandleLeft) {
        Preconditions.checkArgument(textSelectHandleLeft != 0, "The text select left handle should be a valid drawable resource id.");
        setTextSelectHandleLeft(this.mContext.getDrawable(textSelectHandleLeft));
    }

    public Drawable getTextSelectHandleLeft() {
        if (this.mTextSelectHandleLeft == null && this.mTextSelectHandleLeftRes != 0) {
            this.mTextSelectHandleLeft = this.mContext.getDrawable(this.mTextSelectHandleLeftRes);
        }
        return this.mTextSelectHandleLeft;
    }

    @RemotableViewMethod
    public void setTextSelectHandleRight(Drawable textSelectHandleRight) {
        Preconditions.checkNotNull(textSelectHandleRight, "The right text select handle should not be null.");
        this.mTextSelectHandleRight = textSelectHandleRight;
        this.mTextSelectHandleRightRes = 0;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.loadHandleDrawables(true);
        }
    }

    @RemotableViewMethod
    public void setTextSelectHandleRight(int textSelectHandleRight) {
        Preconditions.checkArgument(textSelectHandleRight != 0, "The text select right handle should be a valid drawable resource id.");
        setTextSelectHandleRight(this.mContext.getDrawable(textSelectHandleRight));
    }

    public Drawable getTextSelectHandleRight() {
        if (this.mTextSelectHandleRight == null && this.mTextSelectHandleRightRes != 0) {
            this.mTextSelectHandleRight = this.mContext.getDrawable(this.mTextSelectHandleRightRes);
        }
        return this.mTextSelectHandleRight;
    }

    public void setTextCursorDrawable(Drawable textCursorDrawable) {
        this.mCursorDrawable = textCursorDrawable;
        this.mCursorDrawableRes = 0;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.loadCursorDrawable();
        }
    }

    public void setTextCursorDrawable(int textCursorDrawable) {
        setTextCursorDrawable(textCursorDrawable != 0 ? this.mContext.getDrawable(textCursorDrawable) : null);
    }

    public Drawable getTextCursorDrawable() {
        if (this.mCursorDrawable == null && this.mCursorDrawableRes != 0) {
            this.mCursorDrawable = this.mContext.getDrawable(this.mCursorDrawableRes);
        }
        return this.mCursorDrawable;
    }

    public void setTextAppearance(int resId) {
        setTextAppearance(this.mContext, resId);
    }

    @Deprecated
    public void setTextAppearance(Context context, int resId) {
        TypedArray ta = context.obtainStyledAttributes(resId, C0001R.styleable.TextAppearance);
        TextAppearanceAttributes attributes = new TextAppearanceAttributes();
        readTextAppearance(context, ta, attributes, false);
        ta.recycle();
        applyTextAppearance(attributes);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class TextAppearanceAttributes {
        boolean mAllCaps;
        boolean mElegant;
        boolean mFallbackLineSpacing;
        int mFocusedSearchResultHighlightColor;
        String mFontFamily;
        boolean mFontFamilyExplicit;
        String mFontFeatureSettings;
        Typeface mFontTypeface;
        String mFontVariationSettings;
        int mFontWeight;
        boolean mHasElegant;
        boolean mHasFallbackLineSpacing;
        boolean mHasLetterSpacing;
        boolean mHasLineBreakStyle;
        boolean mHasLineBreakWordStyle;
        float mLetterSpacing;
        int mLineBreakStyle;
        int mLineBreakWordStyle;
        int mSearchResultHighlightColor;
        int mShadowColor;
        float mShadowDx;
        float mShadowDy;
        float mShadowRadius;
        ColorStateList mTextColor;
        int mTextColorHighlight;
        ColorStateList mTextColorHint;
        ColorStateList mTextColorLink;
        LocaleList mTextLocales;
        int mTextSize;
        int mTextSizeUnit;
        int mTextStyle;
        int mTypefaceIndex;

        private TextAppearanceAttributes() {
            this.mTextColorHighlight = 0;
            this.mSearchResultHighlightColor = 0;
            this.mFocusedSearchResultHighlightColor = 0;
            this.mTextColor = null;
            this.mTextColorHint = null;
            this.mTextColorLink = null;
            this.mTextSize = -1;
            this.mTextSizeUnit = -1;
            this.mTextLocales = null;
            this.mFontFamily = null;
            this.mFontTypeface = null;
            this.mFontFamilyExplicit = false;
            this.mTypefaceIndex = -1;
            this.mTextStyle = 0;
            this.mFontWeight = -1;
            this.mAllCaps = false;
            this.mShadowColor = 0;
            this.mShadowDx = 0.0f;
            this.mShadowDy = 0.0f;
            this.mShadowRadius = 0.0f;
            this.mHasElegant = false;
            this.mElegant = false;
            this.mHasFallbackLineSpacing = false;
            this.mFallbackLineSpacing = false;
            this.mHasLetterSpacing = false;
            this.mLetterSpacing = 0.0f;
            this.mFontFeatureSettings = null;
            this.mFontVariationSettings = null;
            this.mHasLineBreakStyle = false;
            this.mHasLineBreakWordStyle = false;
            this.mLineBreakStyle = 0;
            this.mLineBreakWordStyle = 0;
        }

        public String toString() {
            return "TextAppearanceAttributes {\n    mTextColorHighlight:" + this.mTextColorHighlight + "\n    mSearchResultHighlightColor: " + this.mSearchResultHighlightColor + "\n    mFocusedSearchResultHighlightColor: " + this.mFocusedSearchResultHighlightColor + "\n    mTextColor:" + this.mTextColor + "\n    mTextColorHint:" + this.mTextColorHint + "\n    mTextColorLink:" + this.mTextColorLink + "\n    mTextSize:" + this.mTextSize + "\n    mTextSizeUnit:" + this.mTextSizeUnit + "\n    mTextLocales:" + this.mTextLocales + "\n    mFontFamily:" + this.mFontFamily + "\n    mFontTypeface:" + this.mFontTypeface + "\n    mFontFamilyExplicit:" + this.mFontFamilyExplicit + "\n    mTypefaceIndex:" + this.mTypefaceIndex + "\n    mTextStyle:" + this.mTextStyle + "\n    mFontWeight:" + this.mFontWeight + "\n    mAllCaps:" + this.mAllCaps + "\n    mShadowColor:" + this.mShadowColor + "\n    mShadowDx:" + this.mShadowDx + "\n    mShadowDy:" + this.mShadowDy + "\n    mShadowRadius:" + this.mShadowRadius + "\n    mHasElegant:" + this.mHasElegant + "\n    mElegant:" + this.mElegant + "\n    mHasFallbackLineSpacing:" + this.mHasFallbackLineSpacing + "\n    mFallbackLineSpacing:" + this.mFallbackLineSpacing + "\n    mHasLetterSpacing:" + this.mHasLetterSpacing + "\n    mLetterSpacing:" + this.mLetterSpacing + "\n    mFontFeatureSettings:" + this.mFontFeatureSettings + "\n    mFontVariationSettings:" + this.mFontVariationSettings + "\n    mHasLineBreakStyle:" + this.mHasLineBreakStyle + "\n    mHasLineBreakWordStyle:" + this.mHasLineBreakWordStyle + "\n    mLineBreakStyle:" + this.mLineBreakStyle + "\n    mLineBreakWordStyle:" + this.mLineBreakWordStyle + "\n}";
        }
    }

    private void readTextAppearance(Context context, TypedArray appearance, TextAppearanceAttributes attributes, boolean styleArray) {
        int n = appearance.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = appearance.getIndex(i);
            int index = attr;
            if (!styleArray || (index = sAppearanceValues.get(attr, -1)) != -1) {
                switch (index) {
                    case 0:
                        attributes.mTextSize = appearance.getDimensionPixelSize(attr, attributes.mTextSize);
                        attributes.mTextSizeUnit = appearance.peekValue(attr).getComplexUnit();
                        continue;
                    case 1:
                        attributes.mTypefaceIndex = appearance.getInt(attr, attributes.mTypefaceIndex);
                        if (attributes.mTypefaceIndex != -1) {
                            if (attributes.mFontFamilyExplicit) {
                                break;
                            } else {
                                attributes.mFontFamily = null;
                                break;
                            }
                        } else {
                            continue;
                        }
                    case 2:
                        attributes.mTextStyle = appearance.getInt(attr, attributes.mTextStyle);
                        continue;
                    case 3:
                        attributes.mTextColor = appearance.getColorStateList(attr);
                        continue;
                    case 4:
                        attributes.mTextColorHighlight = appearance.getColor(attr, attributes.mTextColorHighlight);
                        continue;
                    case 5:
                        attributes.mTextColorHint = appearance.getColorStateList(attr);
                        continue;
                    case 6:
                        attributes.mTextColorLink = appearance.getColorStateList(attr);
                        continue;
                    case 7:
                        attributes.mShadowColor = appearance.getInt(attr, attributes.mShadowColor);
                        continue;
                    case 8:
                        attributes.mShadowDx = appearance.getFloat(attr, attributes.mShadowDx);
                        continue;
                    case 9:
                        attributes.mShadowDy = appearance.getFloat(attr, attributes.mShadowDy);
                        continue;
                    case 10:
                        attributes.mShadowRadius = appearance.getFloat(attr, attributes.mShadowRadius);
                        continue;
                    case 11:
                        attributes.mAllCaps = appearance.getBoolean(attr, attributes.mAllCaps);
                        continue;
                    case 12:
                        if (!context.isRestricted() && context.canLoadUnsafeResources()) {
                            try {
                                attributes.mFontTypeface = appearance.getFont(attr);
                            } catch (Resources.NotFoundException | UnsupportedOperationException e) {
                            }
                        }
                        if (attributes.mFontTypeface == null) {
                            attributes.mFontFamily = appearance.getString(attr);
                        }
                        attributes.mFontFamilyExplicit = true;
                        continue;
                    case 13:
                        attributes.mHasElegant = true;
                        attributes.mElegant = appearance.getBoolean(attr, attributes.mElegant);
                        continue;
                    case 14:
                        attributes.mHasLetterSpacing = true;
                        attributes.mLetterSpacing = appearance.getFloat(attr, attributes.mLetterSpacing);
                        continue;
                    case 15:
                        attributes.mFontFeatureSettings = appearance.getString(attr);
                        continue;
                    case 16:
                        attributes.mFontVariationSettings = appearance.getString(attr);
                        continue;
                    case 17:
                        attributes.mHasFallbackLineSpacing = true;
                        attributes.mFallbackLineSpacing = appearance.getBoolean(attr, attributes.mFallbackLineSpacing);
                        continue;
                    case 18:
                        attributes.mFontWeight = appearance.getInt(attr, attributes.mFontWeight);
                        continue;
                    case 19:
                        String localeString = appearance.getString(attr);
                        if (localeString != null) {
                            LocaleList localeList = LocaleList.forLanguageTags(localeString);
                            if (localeList.isEmpty()) {
                                break;
                            } else {
                                attributes.mTextLocales = localeList;
                                break;
                            }
                        } else {
                            continue;
                        }
                    case 20:
                        attributes.mHasLineBreakStyle = true;
                        attributes.mLineBreakStyle = appearance.getInt(attr, attributes.mLineBreakStyle);
                        continue;
                    case 21:
                        attributes.mHasLineBreakWordStyle = true;
                        this.mUserSpeficiedLineBreakwordStyle = true;
                        attributes.mLineBreakWordStyle = appearance.getInt(attr, attributes.mLineBreakWordStyle);
                        continue;
                    case 22:
                        attributes.mSearchResultHighlightColor = appearance.getColor(attr, attributes.mSearchResultHighlightColor);
                        continue;
                    case 23:
                        attributes.mFocusedSearchResultHighlightColor = appearance.getColor(attr, attributes.mFocusedSearchResultHighlightColor);
                        continue;
                }
            }
        }
    }

    private void applyTextAppearance(TextAppearanceAttributes attributes) {
        if (attributes.mTextColor != null) {
            setTextColor(attributes.mTextColor);
        }
        if (attributes.mTextColorHint != null) {
            setHintTextColor(attributes.mTextColorHint);
        }
        if (attributes.mTextColorLink != null) {
            setLinkTextColor(attributes.mTextColorLink);
        }
        if (attributes.mTextColorHighlight != 0) {
            setHighlightColor(attributes.mTextColorHighlight);
        }
        if (attributes.mSearchResultHighlightColor != 0) {
            setSearchResultHighlightColor(attributes.mSearchResultHighlightColor);
        }
        if (attributes.mFocusedSearchResultHighlightColor != 0) {
            setFocusedSearchResultHighlightColor(attributes.mFocusedSearchResultHighlightColor);
        }
        if (attributes.mTextSize != -1) {
            this.mTextSizeUnit = attributes.mTextSizeUnit;
            setRawTextSize(attributes.mTextSize, true);
        }
        if (attributes.mTextLocales != null) {
            setTextLocales(attributes.mTextLocales);
        }
        if (attributes.mTypefaceIndex != -1 && !attributes.mFontFamilyExplicit) {
            attributes.mFontFamily = null;
        }
        setTypefaceFromAttrs(attributes.mFontTypeface, attributes.mFontFamily, attributes.mTypefaceIndex, attributes.mTextStyle, attributes.mFontWeight);
        if (attributes.mShadowColor != 0) {
            setShadowLayer(attributes.mShadowRadius, attributes.mShadowDx, attributes.mShadowDy, attributes.mShadowColor);
        }
        if (attributes.mAllCaps) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        }
        if (attributes.mHasElegant) {
            setElegantTextHeight(attributes.mElegant);
        }
        if (attributes.mHasFallbackLineSpacing) {
            setFallbackLineSpacing(attributes.mFallbackLineSpacing);
        }
        if (attributes.mHasLetterSpacing) {
            setLetterSpacing(attributes.mLetterSpacing);
        }
        if (attributes.mFontFeatureSettings != null) {
            setFontFeatureSettings(attributes.mFontFeatureSettings);
        }
        if (attributes.mFontVariationSettings != null) {
            setFontVariationSettings(attributes.mFontVariationSettings);
        }
        if (attributes.mHasLineBreakStyle || attributes.mHasLineBreakWordStyle) {
            updateLineBreakConfigFromTextAppearance(attributes.mHasLineBreakStyle, attributes.mHasLineBreakWordStyle, attributes.mLineBreakStyle, attributes.mLineBreakWordStyle);
        }
    }

    private void updateLineBreakConfigFromTextAppearance(boolean isLineBreakStyleSpecified, boolean isLineBreakWordStyleSpecified, int lineBreakStyle, int lineBreakWordStyle) {
        boolean updated = false;
        if (isLineBreakStyleSpecified && this.mLineBreakStyle != lineBreakStyle) {
            this.mLineBreakStyle = lineBreakStyle;
            updated = true;
        }
        if (isLineBreakWordStyleSpecified && this.mLineBreakWordStyle != lineBreakWordStyle) {
            this.mLineBreakWordStyle = lineBreakWordStyle;
            updated = true;
        }
        if (updated && this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public Locale getTextLocale() {
        return this.mTextPaint.getTextLocale();
    }

    public LocaleList getTextLocales() {
        return this.mTextPaint.getTextLocales();
    }

    private void changeListenerLocaleTo(Locale locale) {
        Editor editor;
        KeyListener listener;
        if (!this.mListenerChanged && (editor = this.mEditor) != null) {
            KeyListener listener2 = editor.mKeyListener;
            if (listener2 instanceof DigitsKeyListener) {
                listener = DigitsKeyListener.getInstance(locale, (DigitsKeyListener) listener2);
            } else if (listener2 instanceof DateKeyListener) {
                listener = DateKeyListener.getInstance(locale);
            } else if (listener2 instanceof TimeKeyListener) {
                listener = TimeKeyListener.getInstance(locale);
            } else if (listener2 instanceof DateTimeKeyListener) {
                listener = DateTimeKeyListener.getInstance(locale);
            } else {
                return;
            }
            boolean wasPasswordType = isPasswordInputType(this.mEditor.mInputType);
            setKeyListenerOnly(listener);
            setInputTypeFromEditor();
            if (wasPasswordType) {
                int newInputClass = this.mEditor.mInputType & 15;
                if (newInputClass == 1) {
                    this.mEditor.mInputType |= 128;
                } else if (newInputClass == 2) {
                    this.mEditor.mInputType |= 16;
                }
            }
        }
    }

    public void setTextLocale(Locale locale) {
        this.mLocalesChanged = true;
        this.mTextPaint.setTextLocale(locale);
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public void setTextLocales(LocaleList locales) {
        this.mLocalesChanged = true;
        this.mTextPaint.setTextLocales(locales);
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.mLocalesChanged) {
            this.mTextPaint.setTextLocales(LocaleList.getDefault());
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
        if (this.mFontWeightAdjustment != newConfig.fontWeightAdjustment) {
            this.mFontWeightAdjustment = newConfig.fontWeightAdjustment;
            setTypeface(getTypeface());
        }
    }

    @ViewDebug.ExportedProperty(category = "text")
    public float getTextSize() {
        return this.mTextPaint.getTextSize();
    }

    @ViewDebug.ExportedProperty(category = "text")
    public float getScaledTextSize() {
        return this.mTextPaint.getTextSize() / this.mTextPaint.density;
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, m86to = SQLiteDatabase.SYNC_MODE_NORMAL), @ViewDebug.IntToString(from = 1, m86to = "BOLD"), @ViewDebug.IntToString(from = 2, m86to = "ITALIC"), @ViewDebug.IntToString(from = 3, m86to = "BOLD_ITALIC")})
    public int getTypefaceStyle() {
        Typeface typeface = this.mTextPaint.getTypeface();
        if (typeface != null) {
            return typeface.getStyle();
        }
        return 0;
    }

    @RemotableViewMethod
    public void setTextSize(float size) {
        setTextSize(2, size);
    }

    public void setTextSize(int unit, float size) {
        if (!isAutoSizeEnabled()) {
            setTextSizeInternal(unit, size, true);
        }
    }

    private void setTextSizeInternal(int unit, float size, boolean shouldRequestLayout) {
        Resources r;
        Context c = getContext();
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        this.mTextSizeUnit = unit;
        setRawTextSize(TypedValue.applyDimension(unit, size, r.getDisplayMetrics()), shouldRequestLayout);
    }

    private void setRawTextSize(float size, boolean shouldRequestLayout) {
        if (size != this.mTextPaint.getTextSize()) {
            this.mTextPaint.setTextSize(size);
            if (shouldRequestLayout && this.mLayout != null) {
                this.mNeedsAutoSizeText = false;
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public int getTextSizeUnit() {
        return this.mTextSizeUnit;
    }

    public float getTextScaleX() {
        return this.mTextPaint.getTextScaleX();
    }

    @RemotableViewMethod
    public void setTextScaleX(float size) {
        if (size != this.mTextPaint.getTextScaleX()) {
            this.mUserSetTextScaleX = true;
            this.mTextPaint.setTextScaleX(size);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setTypeface(Typeface tf) {
        this.mOriginalTypeface = tf;
        int i = this.mFontWeightAdjustment;
        if (i != 0 && i != Integer.MAX_VALUE) {
            if (tf == null) {
                tf = Typeface.DEFAULT;
            } else {
                int newWeight = Math.min(Math.max(tf.getWeight() + this.mFontWeightAdjustment, 1), 1000);
                int typefaceStyle = tf != null ? tf.getStyle() : 0;
                boolean italic = (typefaceStyle & 2) != 0;
                tf = Typeface.create(tf, newWeight, italic);
            }
        }
        if (this.mTextPaint.getTypeface() != tf) {
            this.mTextPaint.setTypeface(tf);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public Typeface getTypeface() {
        return this.mOriginalTypeface;
    }

    public void setElegantTextHeight(boolean elegant) {
        if (elegant != this.mTextPaint.isElegantTextHeight()) {
            this.mTextPaint.setElegantTextHeight(elegant);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setFallbackLineSpacing(boolean enabled) {
        int fallbackStrategy;
        if (enabled) {
            if (CompatChanges.isChangeEnabled(BORINGLAYOUT_FALLBACK_LINESPACING)) {
                fallbackStrategy = 2;
            } else {
                fallbackStrategy = 1;
            }
        } else {
            fallbackStrategy = 0;
        }
        if (this.mUseFallbackLineSpacing != fallbackStrategy) {
            this.mUseFallbackLineSpacing = fallbackStrategy;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public boolean isFallbackLineSpacing() {
        return this.mUseFallbackLineSpacing != 0;
    }

    private boolean isFallbackLineSpacingForBoringLayout() {
        return this.mUseFallbackLineSpacing == 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFallbackLineSpacingForStaticLayout() {
        int i = this.mUseFallbackLineSpacing;
        return i == 2 || i == 1;
    }

    public boolean isElegantTextHeight() {
        return this.mTextPaint.isElegantTextHeight();
    }

    public float getLetterSpacing() {
        return this.mTextPaint.getLetterSpacing();
    }

    @RemotableViewMethod
    public void setLetterSpacing(float letterSpacing) {
        if (letterSpacing != this.mTextPaint.getLetterSpacing()) {
            this.mTextPaint.setLetterSpacing(letterSpacing);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public String getFontFeatureSettings() {
        return this.mTextPaint.getFontFeatureSettings();
    }

    public String getFontVariationSettings() {
        return this.mTextPaint.getFontVariationSettings();
    }

    public void setBreakStrategy(int breakStrategy) {
        this.mBreakStrategy = breakStrategy;
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public int getBreakStrategy() {
        return this.mBreakStrategy;
    }

    public void setHyphenationFrequency(int hyphenationFrequency) {
        this.mHyphenationFrequency = hyphenationFrequency;
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public int getHyphenationFrequency() {
        return this.mHyphenationFrequency;
    }

    public void setLineBreakStyle(int lineBreakStyle) {
        if (this.mLineBreakStyle != lineBreakStyle) {
            this.mLineBreakStyle = lineBreakStyle;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setLineBreakWordStyle(int lineBreakWordStyle) {
        this.mUserSpeficiedLineBreakwordStyle = true;
        if (this.mLineBreakWordStyle != lineBreakWordStyle) {
            this.mLineBreakWordStyle = lineBreakWordStyle;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public int getLineBreakStyle() {
        return this.mLineBreakStyle;
    }

    public int getLineBreakWordStyle() {
        return this.mLineBreakWordStyle;
    }

    public PrecomputedText.Params getTextMetricsParams() {
        boolean autoPhraseBreaking = !this.mUserSpeficiedLineBreakwordStyle && FeatureFlagUtils.isEnabled(this.mContext, FeatureFlagUtils.SETTINGS_AUTO_TEXT_WRAPPING);
        return new PrecomputedText.Params(new TextPaint(this.mTextPaint), LineBreakConfig.getLineBreakConfig(this.mLineBreakStyle, this.mLineBreakWordStyle, autoPhraseBreaking), getTextDirectionHeuristic(), this.mBreakStrategy, this.mHyphenationFrequency);
    }

    public void setTextMetricsParams(PrecomputedText.Params params) {
        this.mTextPaint.set(params.getTextPaint());
        this.mUserSetTextScaleX = true;
        this.mTextDir = params.getTextDirection();
        this.mBreakStrategy = params.getBreakStrategy();
        this.mHyphenationFrequency = params.getHyphenationFrequency();
        LineBreakConfig lineBreakConfig = params.getLineBreakConfig();
        this.mLineBreakStyle = lineBreakConfig.getLineBreakStyle();
        this.mLineBreakWordStyle = lineBreakConfig.getLineBreakWordStyle();
        this.mUserSpeficiedLineBreakwordStyle = true;
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    @RemotableViewMethod
    public void setJustificationMode(int justificationMode) {
        this.mJustificationMode = justificationMode;
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public int getJustificationMode() {
        return this.mJustificationMode;
    }

    @RemotableViewMethod
    public void setFontFeatureSettings(String fontFeatureSettings) {
        if (fontFeatureSettings != this.mTextPaint.getFontFeatureSettings()) {
            this.mTextPaint.setFontFeatureSettings(fontFeatureSettings);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public boolean setFontVariationSettings(String fontVariationSettings) {
        String existingSettings = this.mTextPaint.getFontVariationSettings();
        if (fontVariationSettings != existingSettings) {
            if (fontVariationSettings != null && fontVariationSettings.equals(existingSettings)) {
                return true;
            }
            boolean effective = this.mTextPaint.setFontVariationSettings(fontVariationSettings);
            if (effective && this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
            return effective;
        }
        return true;
    }

    @RemotableViewMethod
    public void setTextColor(int color) {
        this.mTextColor = ColorStateList.valueOf(color);
        updateTextColors();
    }

    @RemotableViewMethod
    public void setTextColor(ColorStateList colors) {
        if (colors == null) {
            throw new NullPointerException();
        }
        this.mTextColor = colors;
        updateTextColors();
    }

    public final ColorStateList getTextColors() {
        return this.mTextColor;
    }

    public final int getCurrentTextColor() {
        return this.mCurTextColor;
    }

    @RemotableViewMethod
    public void setHighlightColor(int color) {
        if (this.mHighlightColor != color) {
            this.mHighlightColor = color;
            invalidate();
        }
    }

    public int getHighlightColor() {
        return this.mHighlightColor;
    }

    @RemotableViewMethod
    public final void setShowSoftInputOnFocus(boolean show) {
        createEditorIfNeeded();
        this.mEditor.mShowSoftInputOnFocus = show;
    }

    public final boolean getShowSoftInputOnFocus() {
        Editor editor = this.mEditor;
        return editor == null || editor.mShowSoftInputOnFocus;
    }

    public void setShadowLayer(float radius, float dx, float dy, int color) {
        this.mTextPaint.setShadowLayer(radius, dx, dy, color);
        this.mShadowRadius = radius;
        this.mShadowDx = dx;
        this.mShadowDy = dy;
        this.mShadowColor = color;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.invalidateTextDisplayList();
            this.mEditor.invalidateHandlesAndActionMode();
        }
        invalidate();
    }

    public float getShadowRadius() {
        return this.mShadowRadius;
    }

    public float getShadowDx() {
        return this.mShadowDx;
    }

    public float getShadowDy() {
        return this.mShadowDy;
    }

    public int getShadowColor() {
        return this.mShadowColor;
    }

    public TextPaint getPaint() {
        return this.mTextPaint;
    }

    @RemotableViewMethod
    public final void setAutoLinkMask(int mask) {
        this.mAutoLinkMask = mask;
    }

    @RemotableViewMethod
    public final void setLinksClickable(boolean whether) {
        this.mLinksClickable = whether;
    }

    public final boolean getLinksClickable() {
        return this.mLinksClickable;
    }

    public URLSpan[] getUrls() {
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Spanned) {
            return (URLSpan[]) ((Spanned) charSequence).getSpans(0, charSequence.length(), URLSpan.class);
        }
        return new URLSpan[0];
    }

    @RemotableViewMethod
    public final void setHintTextColor(int color) {
        this.mHintTextColor = ColorStateList.valueOf(color);
        updateTextColors();
    }

    public final void setHintTextColor(ColorStateList colors) {
        this.mHintTextColor = colors;
        updateTextColors();
    }

    public final ColorStateList getHintTextColors() {
        return this.mHintTextColor;
    }

    public final int getCurrentHintTextColor() {
        return this.mHintTextColor != null ? this.mCurHintTextColor : this.mCurTextColor;
    }

    @RemotableViewMethod
    public final void setLinkTextColor(int color) {
        this.mLinkTextColor = ColorStateList.valueOf(color);
        updateTextColors();
    }

    public final void setLinkTextColor(ColorStateList colors) {
        this.mLinkTextColor = colors;
        updateTextColors();
    }

    public final ColorStateList getLinkTextColors() {
        return this.mLinkTextColor;
    }

    @RemotableViewMethod
    public void setGravity(int gravity) {
        if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.START;
        }
        if ((gravity & 112) == 0) {
            gravity |= 48;
        }
        boolean newLayout = false;
        int i = gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        int i2 = this.mGravity;
        if (i != (8388615 & i2)) {
            newLayout = true;
        }
        if (gravity != i2) {
            invalidate();
        }
        this.mGravity = gravity;
        Layout layout = this.mLayout;
        if (layout != null && newLayout) {
            int want = layout.getWidth();
            Layout layout2 = this.mHintLayout;
            int hintWant = layout2 == null ? 0 : layout2.getWidth();
            BoringLayout.Metrics metrics = UNKNOWN_BORING;
            makeNewLayout(want, hintWant, metrics, metrics, ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight(), true);
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    public int getPaintFlags() {
        return this.mTextPaint.getFlags();
    }

    @RemotableViewMethod
    public void setPaintFlags(int flags) {
        if (this.mTextPaint.getFlags() != flags) {
            this.mTextPaint.setFlags(flags);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setHorizontallyScrolling(boolean whether) {
        if (this.mHorizontallyScrolling != whether) {
            this.mHorizontallyScrolling = whether;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public final boolean isHorizontallyScrollable() {
        return this.mHorizontallyScrolling;
    }

    public boolean getHorizontallyScrolling() {
        return this.mHorizontallyScrolling;
    }

    @RemotableViewMethod
    public void setMinLines(int minLines) {
        this.mMinimum = minLines;
        this.mMinMode = 1;
        requestLayout();
        invalidate();
    }

    public int getMinLines() {
        if (this.mMinMode == 1) {
            return this.mMinimum;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMinHeight(int minPixels) {
        this.mMinimum = minPixels;
        this.mMinMode = 2;
        requestLayout();
        invalidate();
    }

    public int getMinHeight() {
        if (this.mMinMode == 2) {
            return this.mMinimum;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMaxLines(int maxLines) {
        this.mMaximum = maxLines;
        this.mMaxMode = 1;
        requestLayout();
        invalidate();
    }

    public int getMaxLines() {
        if (this.mMaxMode == 1) {
            return this.mMaximum;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMaxHeight(int maxPixels) {
        this.mMaximum = maxPixels;
        this.mMaxMode = 2;
        requestLayout();
        invalidate();
    }

    public int getMaxHeight() {
        if (this.mMaxMode == 2) {
            return this.mMaximum;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setLines(int lines) {
        this.mMinimum = lines;
        this.mMaximum = lines;
        this.mMinMode = 1;
        this.mMaxMode = 1;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setHeight(int pixels) {
        this.mMinimum = pixels;
        this.mMaximum = pixels;
        this.mMinMode = 2;
        this.mMaxMode = 2;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setMinEms(int minEms) {
        this.mMinWidth = minEms;
        this.mMinWidthMode = 1;
        requestLayout();
        invalidate();
    }

    public int getMinEms() {
        if (this.mMinWidthMode == 1) {
            return this.mMinWidth;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMinWidth(int minPixels) {
        this.mMinWidth = minPixels;
        this.mMinWidthMode = 2;
        requestLayout();
        invalidate();
    }

    public int getMinWidth() {
        if (this.mMinWidthMode == 2) {
            return this.mMinWidth;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMaxEms(int maxEms) {
        this.mMaxWidth = maxEms;
        this.mMaxWidthMode = 1;
        requestLayout();
        invalidate();
    }

    public int getMaxEms() {
        if (this.mMaxWidthMode == 1) {
            return this.mMaxWidth;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMaxWidth(int maxPixels) {
        this.mMaxWidth = maxPixels;
        this.mMaxWidthMode = 2;
        requestLayout();
        invalidate();
    }

    public int getMaxWidth() {
        if (this.mMaxWidthMode == 2) {
            return this.mMaxWidth;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setEms(int ems) {
        this.mMinWidth = ems;
        this.mMaxWidth = ems;
        this.mMinWidthMode = 1;
        this.mMaxWidthMode = 1;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setWidth(int pixels) {
        this.mMinWidth = pixels;
        this.mMaxWidth = pixels;
        this.mMinWidthMode = 2;
        this.mMaxWidthMode = 2;
        requestLayout();
        invalidate();
    }

    public void setLineSpacing(float add, float mult) {
        if (this.mSpacingAdd != add || this.mSpacingMult != mult) {
            this.mSpacingAdd = add;
            this.mSpacingMult = mult;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public float getLineSpacingMultiplier() {
        return this.mSpacingMult;
    }

    public float getLineSpacingExtra() {
        return this.mSpacingAdd;
    }

    @RemotableViewMethod
    public void setLineHeight(int lineHeight) {
        Preconditions.checkArgumentNonnegative(lineHeight);
        int fontHeight = getPaint().getFontMetricsInt(null);
        if (lineHeight != fontHeight) {
            setLineSpacing(lineHeight - fontHeight, 1.0f);
        }
    }

    public void setHighlights(Highlights highlights) {
        this.mHighlights = highlights;
        this.mHighlightPathsBogus = true;
        invalidate();
    }

    public Highlights getHighlights() {
        return this.mHighlights;
    }

    public void setSearchResultHighlights(int... ranges) {
        if (ranges != null) {
            if (ranges.length % 2 == 1) {
                throw new IllegalArgumentException("Flatten ranges must have even numbered elements");
            }
            for (int j = 0; j < ranges.length / 2; j++) {
                int start = ranges[j * 2];
                int end = ranges[(j * 2) + 1];
                if (start > end) {
                    throw new IllegalArgumentException("Reverse range found in the flatten range: " + start + ", " + end + " at " + j + "-th range");
                }
            }
            this.mHighlightPathsBogus = true;
            this.mSearchResultHighlights = ranges;
            this.mFocusedSearchResultIndex = -1;
            invalidate();
            return;
        }
        this.mSearchResultHighlights = null;
        this.mHighlightPathsBogus = true;
    }

    public int[] getSearchResultHighlights() {
        return this.mSearchResultHighlights;
    }

    public void setFocusedSearchResultIndex(int index) {
        int[] iArr = this.mSearchResultHighlights;
        if (iArr == null) {
            throw new IllegalArgumentException("Search result range must be set beforehand.");
        }
        if (index < -1 || index >= iArr.length / 2) {
            throw new IllegalArgumentException("Focused index(" + index + ") must be larger than -1 and less than range count(" + (this.mSearchResultHighlights.length / 2) + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mFocusedSearchResultIndex = index;
        this.mHighlightPathsBogus = true;
        invalidate();
    }

    public int getFocusedSearchResultIndex() {
        return this.mFocusedSearchResultIndex;
    }

    public void setSearchResultHighlightColor(int color) {
        this.mSearchResultHighlightColor = color;
    }

    public int getSearchResultHighlightColor() {
        return this.mSearchResultHighlightColor;
    }

    public void setFocusedSearchResultHighlightColor(int color) {
        this.mFocusedSearchResultHighlightColor = color;
    }

    public int getFocusedSearchResultHighlightColor() {
        return this.mFocusedSearchResultHighlightColor;
    }

    private void setSelectGesturePreviewHighlight(int start, int end) {
        setGesturePreviewHighlight(start, end, this.mHighlightColor);
    }

    private void setDeleteGesturePreviewHighlight(int start, int end) {
        int color = this.mTextColor.getDefaultColor();
        setGesturePreviewHighlight(start, end, ColorUtils.setAlphaComponent(color, (int) (Color.alpha(color) * 0.2f)));
    }

    private void setGesturePreviewHighlight(int start, int end, int color) {
        this.mGesturePreviewHighlightStart = start;
        this.mGesturePreviewHighlightEnd = end;
        if (this.mGesturePreviewHighlightPaint == null) {
            Paint paint = new Paint();
            this.mGesturePreviewHighlightPaint = paint;
            paint.setStyle(Paint.Style.FILL);
        }
        this.mGesturePreviewHighlightPaint.setColor(color);
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.hideCursorAndSpanControllers();
            this.mEditor.stopTextActionModeWithPreservingSelection();
        }
        this.mHighlightPathsBogus = true;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearGesturePreviewHighlight() {
        this.mGesturePreviewHighlightStart = -1;
        this.mGesturePreviewHighlightEnd = -1;
        this.mHighlightPathsBogus = true;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasGesturePreviewHighlight() {
        return this.mGesturePreviewHighlightStart >= 0;
    }

    public final void append(CharSequence text) {
        append(text, 0, text.length());
    }

    public void append(CharSequence text, int start, int end) {
        CharSequence charSequence = this.mText;
        if (!(charSequence instanceof Editable)) {
            setText(charSequence, BufferType.EDITABLE);
        }
        ((Editable) this.mText).append(text, start, end);
        int i = this.mAutoLinkMask;
        if (i != 0) {
            boolean linksWereAdded = Linkify.addLinks(this.mSpannable, i);
            if (linksWereAdded && this.mLinksClickable && !textCanBeSelected()) {
                setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    private void updateTextColors() {
        int color;
        int color2;
        boolean inval = false;
        int[] drawableState = getDrawableState();
        int color3 = this.mTextColor.getColorForState(drawableState, 0);
        if (color3 != this.mCurTextColor) {
            this.mCurTextColor = color3;
            inval = true;
        }
        ColorStateList colorStateList = this.mLinkTextColor;
        if (colorStateList != null && (color2 = colorStateList.getColorForState(drawableState, 0)) != this.mTextPaint.linkColor) {
            this.mTextPaint.linkColor = color2;
            inval = true;
        }
        ColorStateList colorStateList2 = this.mHintTextColor;
        if (colorStateList2 != null && (color = colorStateList2.getColorForState(drawableState, 0)) != this.mCurHintTextColor) {
            this.mCurHintTextColor = color;
            if (this.mText.length() == 0) {
                inval = true;
            }
        }
        if (inval) {
            Editor editor = this.mEditor;
            if (editor != null) {
                editor.invalidateTextDisplayList();
            }
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void drawableStateChanged() {
        ColorStateList colorStateList;
        ColorStateList colorStateList2;
        Drawable[] drawableArr;
        super.drawableStateChanged();
        ColorStateList colorStateList3 = this.mTextColor;
        if ((colorStateList3 != null && colorStateList3.isStateful()) || (((colorStateList = this.mHintTextColor) != null && colorStateList.isStateful()) || ((colorStateList2 = this.mLinkTextColor) != null && colorStateList2.isStateful()))) {
            updateTextColors();
        }
        if (this.mDrawables != null) {
            int[] state = getDrawableState();
            for (Drawable dr : this.mDrawables.mShowing) {
                if (dr != null && dr.isStateful() && dr.setState(state)) {
                    invalidateDrawable(dr);
                }
            }
        }
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float x, float y) {
        Drawable[] drawableArr;
        super.drawableHotspotChanged(x, y);
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            for (Drawable dr : drawables.mShowing) {
                if (dr != null) {
                    dr.setHotspot(x, y);
                }
            }
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        boolean freezesText = getFreezesText();
        boolean hasSelection = false;
        int start = -1;
        int end = -1;
        if (this.mText != null) {
            start = getSelectionStart();
            end = getSelectionEnd();
            if (start >= 0 || end >= 0) {
                hasSelection = true;
            }
        }
        if (freezesText || hasSelection) {
            SavedState ss = new SavedState(superState);
            if (freezesText) {
                CharSequence charSequence = this.mText;
                if (charSequence instanceof Spanned) {
                    Spannable sp = new SpannableStringBuilder(this.mText);
                    if (this.mEditor != null) {
                        removeMisspelledSpans(sp);
                        sp.removeSpan(this.mEditor.mSuggestionRangeSpan);
                    }
                    ss.text = sp;
                } else {
                    ss.text = charSequence.toString();
                }
            }
            if (hasSelection) {
                ss.selStart = start;
                ss.selEnd = end;
            }
            if (isFocused() && start >= 0 && end >= 0) {
                ss.frozenWithFocus = true;
            }
            ss.error = getError();
            Editor editor = this.mEditor;
            if (editor != null) {
                ss.editorState = editor.saveInstanceState();
            }
            return ss;
        }
        return superState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeMisspelledSpans(Spannable spannable) {
        SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) spannable.getSpans(0, spannable.length(), SuggestionSpan.class);
        for (int i = 0; i < suggestionSpans.length; i++) {
            int flags = suggestionSpans[i].getFlags();
            if ((flags & 1) != 0 && (flags & 2) != 0) {
                spannable.removeSpan(suggestionSpans[i]);
            }
        }
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (ss.text != null) {
            setText(ss.text);
        }
        if (ss.selStart >= 0 && ss.selEnd >= 0 && this.mSpannable != null) {
            int len = this.mText.length();
            if (ss.selStart > len || ss.selEnd > len) {
                String restored = "";
                if (ss.text != null) {
                    restored = "(restored) ";
                }
                Log.m110e(LOG_TAG, "Saved cursor position " + ss.selStart + "/" + ss.selEnd + " out of range for " + restored + "text " + ((Object) this.mText));
            } else {
                Selection.setSelection(this.mSpannable, ss.selStart, ss.selEnd);
                if (ss.frozenWithFocus) {
                    createEditorIfNeeded();
                    this.mEditor.mFrozenWithFocus = true;
                }
            }
        }
        if (ss.error != null) {
            final CharSequence error = ss.error;
            post(new Runnable() { // from class: android.widget.TextView.1
                @Override // java.lang.Runnable
                public void run() {
                    if (TextView.this.mEditor == null || !TextView.this.mEditor.mErrorWasChanged) {
                        TextView.this.setError(error);
                    }
                }
            });
        }
        if (ss.editorState != null) {
            createEditorIfNeeded();
            this.mEditor.restoreInstanceState(ss.editorState);
        }
    }

    @RemotableViewMethod
    public void setFreezesText(boolean freezesText) {
        this.mFreezesText = freezesText;
    }

    public boolean getFreezesText() {
        return this.mFreezesText;
    }

    public final void setEditableFactory(Editable.Factory factory) {
        this.mEditableFactory = factory;
        setText(this.mText);
    }

    public final void setSpannableFactory(Spannable.Factory factory) {
        this.mSpannableFactory = factory;
        setText(this.mText);
    }

    @RemotableViewMethod
    public final void setText(CharSequence text) {
        setText(text, this.mBufferType);
    }

    @RemotableViewMethod
    public final void setTextKeepState(CharSequence text) {
        setTextKeepState(text, this.mBufferType);
    }

    public void setText(CharSequence text, BufferType type) {
        setText(text, type, true, 0);
        this.mCharWrapper = null;
    }

    /* JADX WARN: Removed duplicated region for block: B:104:0x01b3  */
    /* JADX WARN: Removed duplicated region for block: B:105:0x01b6  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x01c0  */
    /* JADX WARN: Removed duplicated region for block: B:117:0x01e6 A[LOOP:1: B:116:0x01e4->B:117:0x01e6, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:120:0x01f2  */
    /* JADX WARN: Removed duplicated region for block: B:123:0x0207  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x020e  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0219  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x022a  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x0238  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x023c  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x0243  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x024a  */
    /* JADX WARN: Removed duplicated region for block: B:145:0x0251  */
    /* JADX WARN: Removed duplicated region for block: B:151:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x009c  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x0163  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x016d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void setText(CharSequence text, BufferType type, boolean notifyBefore, int oldlen) {
        CharSequence text2;
        int oldlen2;
        boolean needEditableForNotification;
        ArrayList<TextWatcher> arrayList;
        int a11yTextChangeType;
        TransformationMethod transformationMethod;
        int textLength;
        Editor editor;
        Editor editor2;
        TransformationMethod transformationMethod2;
        MovementMethod movementMethod;
        Spannable s2;
        BufferType type2 = type;
        this.mTextSetFromXmlOrResourceId = false;
        if (text != null) {
            text2 = text;
        } else {
            text2 = "";
        }
        if (!isSuggestionsEnabled()) {
            text2 = removeSuggestionSpans(text2);
        }
        if (!this.mUserSetTextScaleX) {
            this.mTextPaint.setTextScaleX(1.0f);
        }
        if ((text2 instanceof Spanned) && ((Spanned) text2).getSpanStart(TextUtils.TruncateAt.MARQUEE) >= 0) {
            if (ViewConfiguration.get(this.mContext).isFadingMarqueeEnabled()) {
                setHorizontalFadingEdgeEnabled(true);
                this.mMarqueeFadeMode = 0;
            } else {
                setHorizontalFadingEdgeEnabled(false);
                this.mMarqueeFadeMode = 1;
            }
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
        }
        int n = this.mFilters.length;
        for (int i = 0; i < n; i++) {
            CharSequence out = this.mFilters[i].filter(text2, 0, text2.length(), EMPTY_SPANNED, 0, 0);
            if (out != null) {
                text2 = out;
            }
        }
        if (notifyBefore) {
            CharSequence charSequence = this.mText;
            if (charSequence != null) {
                oldlen2 = charSequence.length();
                sendBeforeTextChanged(this.mText, 0, oldlen2, text2.length());
                needEditableForNotification = false;
                arrayList = this.mListeners;
                if (arrayList != null && arrayList.size() != 0) {
                    needEditableForNotification = true;
                }
                PrecomputedText precomputed = !(text2 instanceof PrecomputedText) ? (PrecomputedText) text2 : null;
                if (type2 != BufferType.EDITABLE || getKeyListener() != null || needEditableForNotification) {
                    createEditorIfNeeded();
                    this.mEditor.forgetUndoRedo();
                    this.mEditor.scheduleRestartInputForSetText();
                    Editable t = this.mEditableFactory.newEditable(text2);
                    text2 = t;
                    setFilters(t, this.mFilters);
                } else if (precomputed == null) {
                    if (type2 == BufferType.SPANNABLE || this.mMovement != null) {
                        text2 = this.mSpannableFactory.newSpannable(text2);
                    } else if (!(text2 instanceof CharWrapper)) {
                        text2 = TextUtils.stringOrSpannedString(text2);
                    }
                } else {
                    if (this.mTextDir == null) {
                        this.mTextDir = getTextDirectionHeuristic();
                    }
                    boolean autoPhraseBreaking = !this.mUserSpeficiedLineBreakwordStyle && FeatureFlagUtils.isEnabled(this.mContext, FeatureFlagUtils.SETTINGS_AUTO_TEXT_WRAPPING);
                    int checkResult = precomputed.getParams().checkResultUsable(getPaint(), this.mTextDir, this.mBreakStrategy, this.mHyphenationFrequency, LineBreakConfig.getLineBreakConfig(this.mLineBreakStyle, this.mLineBreakWordStyle, autoPhraseBreaking));
                    switch (checkResult) {
                        case 0:
                            throw new IllegalArgumentException("PrecomputedText's Parameters don't match the parameters of this TextView.Consider using setTextMetricsParams(precomputedText.getParams()) to override the settings of this TextView: PrecomputedText: " + precomputed.getParams() + "TextView: " + getTextMetricsParams());
                        case 1:
                            PrecomputedText.create(precomputed, getTextMetricsParams());
                            break;
                    }
                }
                a11yTextChangeType = 0;
                if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
                    a11yTextChangeType = AccessibilityUtils.textOrSpanChanged(text2, this.mText);
                }
                if (this.mAutoLinkMask != 0) {
                    if (type2 == BufferType.EDITABLE || (text2 instanceof Spannable)) {
                        s2 = (Spannable) text2;
                    } else {
                        s2 = this.mSpannableFactory.newSpannable(text2);
                    }
                    if (Linkify.addLinks(s2, this.mAutoLinkMask)) {
                        text2 = s2;
                        type2 = type2 == BufferType.EDITABLE ? BufferType.EDITABLE : BufferType.SPANNABLE;
                        setTextInternal(text2);
                        if (a11yTextChangeType == 0) {
                            a11yTextChangeType = 2;
                        }
                        if (this.mLinksClickable && !textCanBeSelected()) {
                            setMovementMethod(LinkMovementMethod.getInstance());
                        }
                    }
                }
                this.mBufferType = type2;
                setTextInternal(text2);
                transformationMethod = this.mTransformation;
                if (transformationMethod == null) {
                    this.mTransformed = transformationMethod.getTransformation(text2, this);
                } else {
                    this.mTransformed = text2;
                }
                if (this.mTransformed == null) {
                    this.mTransformed = "";
                }
                textLength = text2.length();
                if ((text2 instanceof Spannable) && (!this.mAllowTransformationLengthChange || (text2 instanceof OffsetMapping))) {
                    Spannable sp = (Spannable) text2;
                    ChangeWatcher[] watchers = (ChangeWatcher[]) sp.getSpans(0, sp.length(), ChangeWatcher.class);
                    for (ChangeWatcher changeWatcher : watchers) {
                        sp.removeSpan(changeWatcher);
                    }
                    if (this.mChangeWatcher == null) {
                        this.mChangeWatcher = new ChangeWatcher();
                    }
                    sp.setSpan(this.mChangeWatcher, 0, textLength, 6553618);
                    editor2 = this.mEditor;
                    if (editor2 != null) {
                        editor2.addSpanWatchers(sp);
                    }
                    transformationMethod2 = this.mTransformation;
                    if (transformationMethod2 != null) {
                        sp.setSpan(transformationMethod2, 0, textLength, 13107218);
                    }
                    movementMethod = this.mMovement;
                    if (movementMethod != null) {
                        movementMethod.initialize(this, (Spannable) text2);
                        Editor editor3 = this.mEditor;
                        if (editor3 != null) {
                            editor3.mSelectionMoved = false;
                        }
                    }
                }
                if (this.mLayout != null) {
                    checkForRelayout();
                }
                sendOnTextChanged(text2, 0, oldlen2, textLength);
                onTextChanged(text2, 0, oldlen2, textLength);
                if (a11yTextChangeType != 1) {
                    notifyViewAccessibilityStateChangedIfNeeded(2);
                } else if (a11yTextChangeType == 2) {
                    notifyViewAccessibilityStateChangedIfNeeded(0);
                }
                if (!needEditableForNotification) {
                    sendAfterTextChanged((Editable) text2);
                } else {
                    notifyListeningManagersAfterTextChanged();
                }
                editor = this.mEditor;
                if (editor == null) {
                    editor.prepareCursorControllers();
                    this.mEditor.maybeFireScheduledRestartInputForSetText();
                    return;
                }
                return;
            }
            int oldlen3 = text2.length();
            sendBeforeTextChanged("", 0, 0, oldlen3);
        }
        oldlen2 = oldlen;
        needEditableForNotification = false;
        arrayList = this.mListeners;
        if (arrayList != null) {
            needEditableForNotification = true;
        }
        if (!(text2 instanceof PrecomputedText)) {
        }
        if (type2 != BufferType.EDITABLE) {
        }
        createEditorIfNeeded();
        this.mEditor.forgetUndoRedo();
        this.mEditor.scheduleRestartInputForSetText();
        Editable t2 = this.mEditableFactory.newEditable(text2);
        text2 = t2;
        setFilters(t2, this.mFilters);
        a11yTextChangeType = 0;
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
        }
        if (this.mAutoLinkMask != 0) {
        }
        this.mBufferType = type2;
        setTextInternal(text2);
        transformationMethod = this.mTransformation;
        if (transformationMethod == null) {
        }
        if (this.mTransformed == null) {
        }
        textLength = text2.length();
        if (text2 instanceof Spannable) {
            Spannable sp2 = (Spannable) text2;
            ChangeWatcher[] watchers2 = (ChangeWatcher[]) sp2.getSpans(0, sp2.length(), ChangeWatcher.class);
            while (i < count) {
            }
            if (this.mChangeWatcher == null) {
            }
            sp2.setSpan(this.mChangeWatcher, 0, textLength, 6553618);
            editor2 = this.mEditor;
            if (editor2 != null) {
            }
            transformationMethod2 = this.mTransformation;
            if (transformationMethod2 != null) {
            }
            movementMethod = this.mMovement;
            if (movementMethod != null) {
            }
        }
        if (this.mLayout != null) {
        }
        sendOnTextChanged(text2, 0, oldlen2, textLength);
        onTextChanged(text2, 0, oldlen2, textLength);
        if (a11yTextChangeType != 1) {
        }
        if (!needEditableForNotification) {
        }
        editor = this.mEditor;
        if (editor == null) {
        }
    }

    public final void setText(char[] text, int start, int len) {
        int oldlen = 0;
        if (start < 0 || len < 0 || start + len > text.length) {
            throw new IndexOutOfBoundsException(start + ", " + len);
        }
        CharSequence charSequence = this.mText;
        if (charSequence != null) {
            oldlen = charSequence.length();
            sendBeforeTextChanged(this.mText, 0, oldlen, len);
        } else {
            sendBeforeTextChanged("", 0, 0, len);
        }
        CharWrapper charWrapper = this.mCharWrapper;
        if (charWrapper == null) {
            this.mCharWrapper = new CharWrapper(text, start, len);
        } else {
            charWrapper.set(text, start, len);
        }
        setText(this.mCharWrapper, this.mBufferType, false, oldlen);
    }

    public final void setTextKeepState(CharSequence text, BufferType type) {
        Spannable spannable;
        int start = getSelectionStart();
        int end = getSelectionEnd();
        int len = text.length();
        setText(text, type);
        if ((start >= 0 || end >= 0) && (spannable = this.mSpannable) != null) {
            Selection.setSelection(spannable, Math.max(0, Math.min(start, len)), Math.max(0, Math.min(end, len)));
        }
    }

    @RemotableViewMethod
    public final void setText(int resid) {
        setText(getContext().getResources().getText(resid));
        this.mTextSetFromXmlOrResourceId = true;
        this.mTextId = resid;
    }

    public final void setText(int resid, BufferType type) {
        setText(getContext().getResources().getText(resid), type);
        this.mTextSetFromXmlOrResourceId = true;
        this.mTextId = resid;
    }

    @RemotableViewMethod
    public final void setHint(CharSequence hint) {
        setHintInternal(hint);
        if (this.mEditor != null && isInputMethodTarget()) {
            this.mEditor.reportExtractedText();
        }
    }

    private void setHintInternal(CharSequence hint) {
        this.mHint = TextUtils.stringOrSpannedString(hint);
        if (this.mLayout != null) {
            checkForRelayout();
        }
        if (this.mText.length() == 0) {
            invalidate();
        }
        if (this.mEditor != null && this.mText.length() == 0 && this.mHint != null) {
            this.mEditor.invalidateTextDisplayList();
        }
    }

    @RemotableViewMethod
    public final void setHint(int resid) {
        this.mHintId = resid;
        setHint(getContext().getResources().getText(resid));
    }

    @ViewDebug.CapturedViewProperty
    public CharSequence getHint() {
        return this.mHint;
    }

    public boolean isSingleLine() {
        return this.mSingleLine;
    }

    private static boolean isMultilineInputType(int type) {
        return (131087 & type) == 131073;
    }

    CharSequence removeSuggestionSpans(CharSequence text) {
        Spannable spannable;
        if (text instanceof Spanned) {
            if (text instanceof Spannable) {
                spannable = (Spannable) text;
            } else {
                spannable = this.mSpannableFactory.newSpannable(text);
            }
            SuggestionSpan[] spans = (SuggestionSpan[]) spannable.getSpans(0, text.length(), SuggestionSpan.class);
            if (spans.length == 0) {
                return text;
            }
            text = spannable;
            for (SuggestionSpan suggestionSpan : spans) {
                spannable.removeSpan(suggestionSpan);
            }
        }
        return text;
    }

    public void setInputType(int type) {
        boolean wasPassword = isPasswordInputType(getInputType());
        boolean wasVisiblePassword = isVisiblePasswordInputType(getInputType());
        setInputType(type, false);
        boolean isPassword = isPasswordInputType(type);
        boolean isVisiblePassword = isVisiblePasswordInputType(type);
        boolean forceUpdate = false;
        if (isPassword) {
            setTransformationMethod(PasswordTransformationMethod.getInstance());
            setTypefaceFromAttrs(null, null, 3, 0, -1);
        } else if (isVisiblePassword) {
            if (this.mTransformation == PasswordTransformationMethod.getInstance()) {
                forceUpdate = true;
            }
            setTypefaceFromAttrs(null, null, 3, 0, -1);
        } else if (wasPassword || wasVisiblePassword) {
            setTypefaceFromAttrs(null, null, -1, 0, -1);
            if (this.mTransformation == PasswordTransformationMethod.getInstance()) {
                forceUpdate = true;
            }
        }
        boolean singleLine = !isMultilineInputType(type);
        if (this.mSingleLine != singleLine || forceUpdate) {
            applySingleLine(singleLine, !isPassword, true, true);
        }
        if (!isSuggestionsEnabled()) {
            setTextInternal(removeSuggestionSpans(this.mText));
        }
        InputMethodManager imm = getInputMethodManager();
        if (imm != null) {
            imm.restartInput(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasPasswordTransformationMethod() {
        return this.mTransformation instanceof PasswordTransformationMethod;
    }

    public boolean isAnyPasswordInputType() {
        int inputType = getInputType();
        return isPasswordInputType(inputType) || isVisiblePasswordInputType(inputType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isPasswordInputType(int inputType) {
        int variation = inputType & 4095;
        return variation == 129 || variation == 225 || variation == 18;
    }

    private static boolean isVisiblePasswordInputType(int inputType) {
        int variation = inputType & 4095;
        return variation == 145;
    }

    public void setRawInputType(int type) {
        if (type == 0 && this.mEditor == null) {
            return;
        }
        createEditorIfNeeded();
        this.mEditor.mInputType = type;
    }

    @Override // android.view.View
    public String[] getAutofillHints() {
        String[] hints = super.getAutofillHints();
        if (isAnyPasswordInputType() && !ArrayUtils.contains(hints, View.AUTOFILL_HINT_PASSWORD_AUTO)) {
            return (String[]) ArrayUtils.appendElement(String.class, hints, View.AUTOFILL_HINT_PASSWORD_AUTO);
        }
        return hints;
    }

    private Locale getCustomLocaleForKeyListenerOrNull() {
        LocaleList locales;
        if (this.mUseInternationalizedInput && (locales = getImeHintLocales()) != null) {
            return locales.get(0);
        }
        return null;
    }

    private void setInputType(int type, boolean direct) {
        KeyListener input;
        KeyListener input2;
        boolean autotext;
        TextKeyListener.Capitalize cap;
        int cls = type & 15;
        if (cls == 1) {
            autotext = (32768 & type) != 0;
            if ((type & 4096) != 0) {
                cap = TextKeyListener.Capitalize.CHARACTERS;
            } else if ((type & 8192) != 0) {
                cap = TextKeyListener.Capitalize.WORDS;
            } else if ((type & 16384) != 0) {
                cap = TextKeyListener.Capitalize.SENTENCES;
            } else {
                cap = TextKeyListener.Capitalize.NONE;
            }
            input = TextKeyListener.getInstance(autotext, cap);
        } else if (cls == 2) {
            Locale locale = getCustomLocaleForKeyListenerOrNull();
            boolean z = (type & 4096) != 0;
            autotext = (type & 8192) != 0;
            input = DigitsKeyListener.getInstance(locale, z, autotext);
            if (locale != null) {
                int newType = input.getInputType();
                int newClass = newType & 15;
                if (newClass != 2) {
                    if ((type & 16) != 0) {
                        newType |= 128;
                    }
                    type = newType;
                }
            }
        } else if (cls == 4) {
            Locale locale2 = getCustomLocaleForKeyListenerOrNull();
            switch (type & InputType.TYPE_MASK_VARIATION) {
                case 16:
                    input2 = DateKeyListener.getInstance(locale2);
                    break;
                case 32:
                    input2 = TimeKeyListener.getInstance(locale2);
                    break;
                default:
                    input2 = DateTimeKeyListener.getInstance(locale2);
                    break;
            }
            if (this.mUseInternationalizedInput) {
                type = input2.getInputType();
            }
            input = input2;
        } else if (cls == 3) {
            input = DialerKeyListener.getInstance();
        } else {
            input = TextKeyListener.getInstance();
        }
        setRawInputType(type);
        this.mListenerChanged = false;
        if (direct) {
            createEditorIfNeeded();
            this.mEditor.mKeyListener = input;
            return;
        }
        setKeyListenerOnly(input);
    }

    public int getInputType() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return 0;
        }
        return editor.mInputType;
    }

    public void setImeOptions(int imeOptions) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.imeOptions = imeOptions;
    }

    public int getImeOptions() {
        Editor editor = this.mEditor;
        if (editor == null || editor.mInputContentType == null) {
            return 0;
        }
        return this.mEditor.mInputContentType.imeOptions;
    }

    public void setImeActionLabel(CharSequence label, int actionId) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.imeActionLabel = label;
        this.mEditor.mInputContentType.imeActionId = actionId;
    }

    public CharSequence getImeActionLabel() {
        Editor editor = this.mEditor;
        if (editor == null || editor.mInputContentType == null) {
            return null;
        }
        return this.mEditor.mInputContentType.imeActionLabel;
    }

    public int getImeActionId() {
        Editor editor = this.mEditor;
        if (editor == null || editor.mInputContentType == null) {
            return 0;
        }
        return this.mEditor.mInputContentType.imeActionId;
    }

    public void setOnEditorActionListener(OnEditorActionListener l) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.onEditorActionListener = l;
    }

    public void onEditorAction(int actionCode) {
        Editor editor = this.mEditor;
        Editor.InputContentType ict = editor == null ? null : editor.mInputContentType;
        if (ict != null) {
            if (ict.onEditorActionListener != null && ict.onEditorActionListener.onEditorAction(this, actionCode, null)) {
                return;
            }
            if (actionCode == 5) {
                View v = focusSearch(2);
                if (v != null && !v.requestFocus(2)) {
                    throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                }
                return;
            } else if (actionCode == 7) {
                View v2 = focusSearch(1);
                if (v2 != null && !v2.requestFocus(1)) {
                    throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                }
                return;
            } else if (actionCode == 6) {
                InputMethodManager imm = getInputMethodManager();
                if (imm != null && imm.isActive(this)) {
                    imm.hideSoftInputFromWindow(getWindowToken(), 0);
                    return;
                }
                return;
            }
        }
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            long eventTime = SystemClock.uptimeMillis();
            viewRootImpl.dispatchKeyFromIme(new KeyEvent(eventTime, eventTime, 0, 66, 0, 0, -1, 0, 22));
            viewRootImpl.dispatchKeyFromIme(new KeyEvent(SystemClock.uptimeMillis(), eventTime, 1, 66, 0, 0, -1, 0, 22));
        }
    }

    public void setPrivateImeOptions(String type) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.privateImeOptions = type;
    }

    public String getPrivateImeOptions() {
        Editor editor = this.mEditor;
        if (editor == null || editor.mInputContentType == null) {
            return null;
        }
        return this.mEditor.mInputContentType.privateImeOptions;
    }

    public void setInputExtras(int xmlResId) throws XmlPullParserException, IOException {
        createEditorIfNeeded();
        XmlResourceParser parser = getResources().getXml(xmlResId);
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.extras = new Bundle();
        getResources().parseBundleExtras(parser, this.mEditor.mInputContentType.extras);
    }

    public Bundle getInputExtras(boolean create) {
        if (this.mEditor != null || create) {
            createEditorIfNeeded();
            if (this.mEditor.mInputContentType == null) {
                if (!create) {
                    return null;
                }
                this.mEditor.createInputContentTypeIfNeeded();
            }
            if (this.mEditor.mInputContentType.extras == null) {
                if (!create) {
                    return null;
                }
                this.mEditor.mInputContentType.extras = new Bundle();
            }
            return this.mEditor.mInputContentType.extras;
        }
        return null;
    }

    public void setImeHintLocales(LocaleList hintLocales) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.imeHintLocales = hintLocales;
        if (this.mUseInternationalizedInput) {
            changeListenerLocaleTo(hintLocales == null ? null : hintLocales.get(0));
        }
    }

    public LocaleList getImeHintLocales() {
        Editor editor = this.mEditor;
        if (editor == null || editor.mInputContentType == null) {
            return null;
        }
        return this.mEditor.mInputContentType.imeHintLocales;
    }

    public CharSequence getError() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return null;
        }
        return editor.mError;
    }

    @RemotableViewMethod
    public void setError(CharSequence error) {
        if (error == null) {
            setError(null, null);
            return;
        }
        Drawable dr = getContext().getDrawable(C4057R.C4058drawable.indicator_input_error);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        setError(error, dr);
    }

    public void setError(CharSequence error, Drawable icon) {
        createEditorIfNeeded();
        this.mEditor.setError(error, icon);
        notifyViewAccessibilityStateChangedIfNeeded(3072);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean setFrame(int l, int t, int r, int b) {
        boolean result = super.setFrame(l, t, r, b);
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.setFrame();
        }
        restartMarqueeIfNeeded();
        return result;
    }

    private void restartMarqueeIfNeeded() {
        if (this.mRestartMarquee && this.mEllipsize == TextUtils.TruncateAt.MARQUEE) {
            this.mRestartMarquee = false;
            startMarquee();
        }
    }

    public void setFilters(InputFilter[] filters) {
        if (filters == null) {
            throw new IllegalArgumentException();
        }
        this.mFilters = filters;
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Editable) {
            setFilters((Editable) charSequence, filters);
        }
    }

    private void setFilters(Editable e, InputFilter[] filters) {
        Editor editor = this.mEditor;
        if (editor != null) {
            boolean undoFilter = editor.mUndoInputFilter != null;
            boolean keyFilter = this.mEditor.mKeyListener instanceof InputFilter;
            int num = undoFilter ? 0 + 1 : 0;
            if (keyFilter) {
                num++;
            }
            if (num > 0) {
                InputFilter[] nf = new InputFilter[filters.length + num];
                System.arraycopy(filters, 0, nf, 0, filters.length);
                int num2 = 0;
                if (undoFilter) {
                    nf[filters.length] = this.mEditor.mUndoInputFilter;
                    num2 = 0 + 1;
                }
                if (keyFilter) {
                    nf[filters.length + num2] = (InputFilter) this.mEditor.mKeyListener;
                }
                e.setFilters(nf);
                return;
            }
        }
        e.setFilters(filters);
    }

    public InputFilter[] getFilters() {
        return this.mFilters;
    }

    private int getBoxHeight(Layout l) {
        int padding;
        Insets opticalInsets = isLayoutModeOptical(this.mParent) ? getOpticalInsets() : Insets.NONE;
        if (l == this.mHintLayout) {
            padding = getCompoundPaddingTop() + getCompoundPaddingBottom();
        } else {
            padding = getExtendedPaddingTop() + getExtendedPaddingBottom();
        }
        return (getMeasuredHeight() - padding) + opticalInsets.top + opticalInsets.bottom;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getVerticalOffset(boolean forceNormal) {
        int boxht;
        int textht;
        int gravity = this.mGravity & 112;
        Layout l = this.mLayout;
        if (!forceNormal && this.mText.length() == 0 && this.mHintLayout != null) {
            l = this.mHintLayout;
        }
        if (gravity == 48 || (textht = l.getHeight()) >= (boxht = getBoxHeight(l))) {
            return 0;
        }
        if (gravity == 80) {
            int voffset = boxht - textht;
            return voffset;
        }
        int voffset2 = (boxht - textht) >> 1;
        return voffset2;
    }

    private int getBottomVerticalOffset(boolean forceNormal) {
        int boxht;
        int textht;
        int gravity = this.mGravity & 112;
        Layout l = this.mLayout;
        if (!forceNormal && this.mText.length() == 0 && this.mHintLayout != null) {
            l = this.mHintLayout;
        }
        if (gravity == 80 || (textht = l.getHeight()) >= (boxht = getBoxHeight(l))) {
            return 0;
        }
        if (gravity == 48) {
            int voffset = boxht - textht;
            return voffset;
        }
        int voffset2 = (boxht - textht) >> 1;
        return voffset2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateCursorPath() {
        if (this.mHighlightPathBogus) {
            invalidateCursor();
            return;
        }
        int horizontalPadding = getCompoundPaddingLeft();
        int verticalPadding = getExtendedPaddingTop() + getVerticalOffset(true);
        if (this.mEditor.mDrawableForCursor == null) {
            RectF rectF = TEMP_RECTF;
            synchronized (rectF) {
                float thick = (float) Math.ceil(this.mTextPaint.getStrokeWidth());
                if (thick < 1.0f) {
                    thick = 1.0f;
                }
                float thick2 = thick / 2.0f;
                this.mHighlightPath.computeBounds(rectF, false);
                invalidate((int) Math.floor((horizontalPadding + rectF.left) - thick2), (int) Math.floor((verticalPadding + rectF.top) - thick2), (int) Math.ceil(horizontalPadding + rectF.right + thick2), (int) Math.ceil(verticalPadding + rectF.bottom + thick2));
            }
            return;
        }
        Rect bounds = this.mEditor.mDrawableForCursor.getBounds();
        invalidate(bounds.left + horizontalPadding, bounds.top + verticalPadding, bounds.right + horizontalPadding, bounds.bottom + verticalPadding);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateCursor() {
        int where = getSelectionEnd();
        invalidateCursor(where, where, where);
    }

    private void invalidateCursor(int a, int b, int c) {
        if (a >= 0 || b >= 0 || c >= 0) {
            int start = Math.min(Math.min(a, b), c);
            int end = Math.max(Math.max(a, b), c);
            invalidateRegion(start, end, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateRegion(int start, int end, boolean invalidateCursor) {
        int lineEnd;
        int left;
        int right;
        Editor editor;
        if (this.mLayout == null) {
            invalidate();
            return;
        }
        int start2 = originalToTransformed(start, 1);
        int end2 = originalToTransformed(end, 1);
        int lineStart = this.mLayout.getLineForOffset(start2);
        int top = this.mLayout.getLineTop(lineStart);
        if (lineStart > 0) {
            top -= this.mLayout.getLineDescent(lineStart - 1);
        }
        if (start2 == end2) {
            lineEnd = lineStart;
        } else {
            lineEnd = this.mLayout.getLineForOffset(end2);
        }
        int bottom = this.mLayout.getLineBottom(lineEnd);
        if (invalidateCursor && (editor = this.mEditor) != null && editor.mDrawableForCursor != null) {
            Rect bounds = this.mEditor.mDrawableForCursor.getBounds();
            top = Math.min(top, bounds.top);
            bottom = Math.max(bottom, bounds.bottom);
        }
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int verticalPadding = getExtendedPaddingTop() + getVerticalOffset(true);
        if (lineStart == lineEnd && !invalidateCursor) {
            int left2 = (int) this.mLayout.getPrimaryHorizontal(start2);
            int right2 = (int) (this.mLayout.getPrimaryHorizontal(end2) + 1.0d);
            left = left2 + compoundPaddingLeft;
            right = right2 + compoundPaddingLeft;
        } else {
            left = compoundPaddingLeft;
            right = getWidth() - getCompoundPaddingRight();
        }
        invalidate(this.mScrollX + left, verticalPadding + top, this.mScrollX + right, verticalPadding + bottom);
    }

    private void registerForPreDraw() {
        if (!this.mPreDrawRegistered) {
            getViewTreeObserver().addOnPreDrawListener(this);
            this.mPreDrawRegistered = true;
        }
    }

    private void unregisterForPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        this.mPreDrawRegistered = false;
        this.mPreDrawListenerDetached = false;
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        if (this.mLayout == null) {
            assumeLayout();
        }
        if (this.mMovement != null) {
            int curs = getSelectionEnd();
            Editor editor = this.mEditor;
            if (editor != null && editor.mSelectionModifierCursorController != null && this.mEditor.mSelectionModifierCursorController.isSelectionStartDragged()) {
                curs = getSelectionStart();
            }
            if (curs < 0 && (this.mGravity & 112) == 80) {
                curs = this.mText.length();
            }
            if (curs >= 0) {
                bringPointIntoView(curs);
            }
        } else {
            bringTextIntoView();
        }
        Editor editor2 = this.mEditor;
        if (editor2 != null && editor2.mCreatedWithASelection) {
            this.mEditor.refreshTextActionMode();
            this.mEditor.mCreatedWithASelection = false;
        }
        unregisterForPreDraw();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onAttachedToWindow();
        }
        if (this.mPreDrawListenerDetached) {
            getViewTreeObserver().addOnPreDrawListener(this);
            this.mPreDrawListenerDetached = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindowInternal() {
        if (this.mPreDrawRegistered) {
            getViewTreeObserver().removeOnPreDrawListener(this);
            this.mPreDrawListenerDetached = true;
        }
        resetResolvedDrawables();
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onDetachedFromWindow();
        }
        super.onDetachedFromWindowInternal();
    }

    @Override // android.view.View
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onScreenStateChanged(screenState);
        }
    }

    @Override // android.view.View
    protected boolean isPaddingOffsetRequired() {
        return (this.mShadowRadius == 0.0f && this.mDrawables == null) ? false : true;
    }

    @Override // android.view.View
    protected int getLeftPaddingOffset() {
        return (getCompoundPaddingLeft() - this.mPaddingLeft) + ((int) Math.min(0.0f, this.mShadowDx - this.mShadowRadius));
    }

    @Override // android.view.View
    protected int getTopPaddingOffset() {
        return (int) Math.min(0.0f, this.mShadowDy - this.mShadowRadius);
    }

    @Override // android.view.View
    protected int getBottomPaddingOffset() {
        return (int) Math.max(0.0f, this.mShadowDy + this.mShadowRadius);
    }

    @Override // android.view.View
    protected int getRightPaddingOffset() {
        return (-(getCompoundPaddingRight() - this.mPaddingRight)) + ((int) Math.max(0.0f, this.mShadowDx + this.mShadowRadius));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean verifyDrawable(Drawable who) {
        Drawables drawables;
        Drawable[] drawableArr;
        boolean verified = super.verifyDrawable(who);
        if (!verified && (drawables = this.mDrawables) != null) {
            for (Drawable dr : drawables.mShowing) {
                if (who == dr) {
                    return true;
                }
            }
        }
        return verified;
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        Drawable[] drawableArr;
        super.jumpDrawablesToCurrentState();
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            for (Drawable dr : drawables.mShowing) {
                if (dr != null) {
                    dr.jumpToCurrentState();
                }
            }
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        boolean handled = false;
        if (verifyDrawable(drawable)) {
            Rect dirty = drawable.getBounds();
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            Drawables drawables = this.mDrawables;
            if (drawables != null) {
                if (drawable == drawables.mShowing[0]) {
                    int compoundPaddingTop = getCompoundPaddingTop();
                    int compoundPaddingBottom = getCompoundPaddingBottom();
                    int vspace = ((this.mBottom - this.mTop) - compoundPaddingBottom) - compoundPaddingTop;
                    scrollX += this.mPaddingLeft;
                    scrollY += ((vspace - drawables.mDrawableHeightLeft) / 2) + compoundPaddingTop;
                    handled = true;
                } else if (drawable == drawables.mShowing[2]) {
                    int compoundPaddingTop2 = getCompoundPaddingTop();
                    int compoundPaddingBottom2 = getCompoundPaddingBottom();
                    int vspace2 = ((this.mBottom - this.mTop) - compoundPaddingBottom2) - compoundPaddingTop2;
                    scrollX += ((this.mRight - this.mLeft) - this.mPaddingRight) - drawables.mDrawableSizeRight;
                    scrollY += ((vspace2 - drawables.mDrawableHeightRight) / 2) + compoundPaddingTop2;
                    handled = true;
                } else if (drawable == drawables.mShowing[1]) {
                    int compoundPaddingLeft = getCompoundPaddingLeft();
                    int compoundPaddingRight = getCompoundPaddingRight();
                    int hspace = ((this.mRight - this.mLeft) - compoundPaddingRight) - compoundPaddingLeft;
                    scrollX += ((hspace - drawables.mDrawableWidthTop) / 2) + compoundPaddingLeft;
                    scrollY += this.mPaddingTop;
                    handled = true;
                } else if (drawable == drawables.mShowing[3]) {
                    int compoundPaddingLeft2 = getCompoundPaddingLeft();
                    int compoundPaddingRight2 = getCompoundPaddingRight();
                    int hspace2 = ((this.mRight - this.mLeft) - compoundPaddingRight2) - compoundPaddingLeft2;
                    scrollX += ((hspace2 - drawables.mDrawableWidthBottom) / 2) + compoundPaddingLeft2;
                    scrollY += ((this.mBottom - this.mTop) - this.mPaddingBottom) - drawables.mDrawableSizeBottom;
                    handled = true;
                }
            }
            if (handled) {
                invalidate(dirty.left + scrollX, dirty.top + scrollY, dirty.right + scrollX, dirty.bottom + scrollY);
            }
        }
        if (!handled) {
            super.invalidateDrawable(drawable);
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return ((getBackground() == null || getBackground().getCurrent() == null) && this.mSpannable == null && !hasSelection() && !isHorizontalFadingEdgeEnabled() && this.mShadowColor == 0) ? false : true;
    }

    public boolean isTextSelectable() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return false;
        }
        return editor.mTextIsSelectable;
    }

    public void setTextIsSelectable(boolean selectable) {
        if (selectable || this.mEditor != null) {
            createEditorIfNeeded();
            if (this.mEditor.mTextIsSelectable == selectable) {
                return;
            }
            this.mEditor.mTextIsSelectable = selectable;
            setFocusableInTouchMode(selectable);
            setFocusable(16);
            setClickable(selectable);
            setLongClickable(selectable);
            setMovementMethod(selectable ? ArrowKeyMovementMethod.getInstance() : null);
            setText(this.mText, selectable ? BufferType.SPANNABLE : BufferType.NORMAL);
            this.mEditor.prepareCursorControllers();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState;
        if (this.mSingleLine) {
            drawableState = super.onCreateDrawableState(extraSpace);
        } else {
            drawableState = super.onCreateDrawableState(extraSpace + 1);
            mergeDrawableStates(drawableState, MULTILINE_STATE_SET);
        }
        if (isTextSelectable()) {
            int length = drawableState.length;
            for (int i = 0; i < length; i++) {
                if (drawableState[i] == 16842919) {
                    int[] nonPressedState = new int[length - 1];
                    System.arraycopy(drawableState, 0, nonPressedState, 0, i);
                    System.arraycopy(drawableState, i + 1, nonPressedState, i, (length - i) - 1);
                    return nonPressedState;
                }
            }
        }
        return drawableState;
    }

    private void maybeUpdateHighlightPaths() {
        Path path;
        final Path path2;
        if (!this.mHighlightPathsBogus) {
            return;
        }
        List<Path> list = this.mHighlightPaths;
        if (list != null) {
            this.mPathRecyclePool.addAll(list);
            this.mHighlightPaths.clear();
            this.mHighlightPaints.clear();
        } else {
            this.mHighlightPaths = new ArrayList();
            this.mHighlightPaints = new ArrayList();
        }
        if (this.mHighlights != null) {
            for (int i = 0; i < this.mHighlights.getSize(); i++) {
                int[] ranges = this.mHighlights.getRanges(i);
                Paint paint = this.mHighlights.getPaint(i);
                if (this.mPathRecyclePool.isEmpty()) {
                    path2 = new Path();
                } else {
                    List<Path> list2 = this.mPathRecyclePool;
                    path2 = list2.get(list2.size() - 1);
                    List<Path> list3 = this.mPathRecyclePool;
                    list3.remove(list3.size() - 1);
                    path2.reset();
                }
                boolean atLeastOnePathAdded = false;
                for (int j = 0; j < ranges.length / 2; j++) {
                    int start = ranges[j * 2];
                    int end = ranges[(j * 2) + 1];
                    if (start < end) {
                        this.mLayout.getSelection(start, end, new Layout.SelectionRectangleConsumer() { // from class: android.widget.TextView$$ExternalSyntheticLambda2
                            @Override // android.text.Layout.SelectionRectangleConsumer
                            public final void accept(float f, float f2, float f3, float f4, int i2) {
                                Path.this.addRect(f, f2, f3, f4, Path.Direction.CW);
                            }
                        });
                        atLeastOnePathAdded = true;
                    }
                }
                if (atLeastOnePathAdded) {
                    this.mHighlightPaths.add(path2);
                    this.mHighlightPaints.add(paint);
                }
            }
        }
        addSearchHighlightPaths();
        if (hasGesturePreviewHighlight()) {
            if (this.mPathRecyclePool.isEmpty()) {
                path = new Path();
            } else {
                List<Path> list4 = this.mPathRecyclePool;
                path = list4.get(list4.size() - 1);
                List<Path> list5 = this.mPathRecyclePool;
                list5.remove(list5.size() - 1);
                path.reset();
            }
            this.mLayout.getSelectionPath(this.mGesturePreviewHighlightStart, this.mGesturePreviewHighlightEnd, path);
            this.mHighlightPaths.add(path);
            this.mHighlightPaints.add(this.mGesturePreviewHighlightPaint);
        }
        this.mHighlightPathsBogus = false;
    }

    private void addSearchHighlightPaths() {
        final Path searchResultPath;
        final Path focusedSearchResultPath;
        if (this.mSearchResultHighlights != null) {
            if (this.mPathRecyclePool.isEmpty()) {
                searchResultPath = new Path();
            } else {
                List<Path> list = this.mPathRecyclePool;
                searchResultPath = list.get(list.size() - 1);
                List<Path> list2 = this.mPathRecyclePool;
                list2.remove(list2.size() - 1);
                searchResultPath.reset();
            }
            if (this.mFocusedSearchResultIndex == -1) {
                focusedSearchResultPath = null;
            } else if (this.mPathRecyclePool.isEmpty()) {
                focusedSearchResultPath = new Path();
            } else {
                List<Path> list3 = this.mPathRecyclePool;
                focusedSearchResultPath = list3.get(list3.size() - 1);
                List<Path> list4 = this.mPathRecyclePool;
                list4.remove(list4.size() - 1);
                focusedSearchResultPath.reset();
            }
            boolean atLeastOnePathAdded = false;
            int j = 0;
            while (true) {
                int[] iArr = this.mSearchResultHighlights;
                if (j >= iArr.length / 2) {
                    break;
                }
                int start = iArr[j * 2];
                int end = iArr[(j * 2) + 1];
                if (start < end) {
                    if (j == this.mFocusedSearchResultIndex) {
                        this.mLayout.getSelection(start, end, new Layout.SelectionRectangleConsumer() { // from class: android.widget.TextView$$ExternalSyntheticLambda3
                            @Override // android.text.Layout.SelectionRectangleConsumer
                            public final void accept(float f, float f2, float f3, float f4, int i) {
                                Path.this.addRect(f, f2, f3, f4, Path.Direction.CW);
                            }
                        });
                    } else {
                        this.mLayout.getSelection(start, end, new Layout.SelectionRectangleConsumer() { // from class: android.widget.TextView$$ExternalSyntheticLambda4
                            @Override // android.text.Layout.SelectionRectangleConsumer
                            public final void accept(float f, float f2, float f3, float f4, int i) {
                                Path.this.addRect(f, f2, f3, f4, Path.Direction.CW);
                            }
                        });
                        atLeastOnePathAdded = true;
                    }
                }
                j++;
            }
            if (atLeastOnePathAdded) {
                if (this.mSearchResultHighlightPaint == null) {
                    this.mSearchResultHighlightPaint = new Paint();
                }
                this.mSearchResultHighlightPaint.setColor(this.mSearchResultHighlightColor);
                this.mSearchResultHighlightPaint.setStyle(Paint.Style.FILL);
                this.mHighlightPaths.add(searchResultPath);
                this.mHighlightPaints.add(this.mSearchResultHighlightPaint);
            }
            if (focusedSearchResultPath != null) {
                if (this.mFocusedSearchResultHighlightPaint == null) {
                    this.mFocusedSearchResultHighlightPaint = new Paint();
                }
                this.mFocusedSearchResultHighlightPaint.setColor(this.mFocusedSearchResultHighlightColor);
                this.mFocusedSearchResultHighlightPaint.setStyle(Paint.Style.FILL);
                this.mHighlightPaths.add(focusedSearchResultPath);
                this.mHighlightPaints.add(this.mFocusedSearchResultHighlightPaint);
            }
        }
    }

    private Path getUpdatedHighlightPath() {
        Paint highlightPaint = this.mHighlightPaint;
        int selStart = getSelectionStartTransformed();
        int selEnd = getSelectionEndTransformed();
        if (this.mMovement != null) {
            if ((isFocused() || isPressed()) && selStart >= 0) {
                if (selStart == selEnd) {
                    Editor editor = this.mEditor;
                    if (editor == null || !editor.shouldRenderCursor()) {
                        return null;
                    }
                    if (this.mHighlightPathBogus) {
                        if (this.mHighlightPath == null) {
                            this.mHighlightPath = new Path();
                        }
                        this.mHighlightPath.reset();
                        this.mLayout.getCursorPath(selStart, this.mHighlightPath, this.mText);
                        this.mEditor.updateCursorPosition();
                        this.mHighlightPathBogus = false;
                    }
                    highlightPaint.setColor(this.mCurTextColor);
                    highlightPaint.setStyle(Paint.Style.STROKE);
                    Path highlight = this.mHighlightPath;
                    return highlight;
                }
                if (this.mHighlightPathBogus) {
                    if (this.mHighlightPath == null) {
                        this.mHighlightPath = new Path();
                    }
                    this.mHighlightPath.reset();
                    this.mLayout.getSelectionPath(selStart, selEnd, this.mHighlightPath);
                    this.mHighlightPathBogus = false;
                }
                highlightPaint.setColor(this.mHighlightColor);
                highlightPaint.setStyle(Paint.Style.FILL);
                Path highlight2 = this.mHighlightPath;
                return highlight2;
            }
            return null;
        }
        return null;
    }

    public int getHorizontalOffsetForDrawables() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        int color;
        Layout layout;
        float clipLeft;
        float clipTop;
        float clipBottom;
        float clipBottom2;
        int voffsetCursor;
        float clipRight;
        int i;
        boolean z;
        Layout layout2;
        int compoundPaddingLeft;
        Layout layout3;
        restartMarqueeIfNeeded();
        super.onDraw(canvas);
        int compoundPaddingLeft2 = getCompoundPaddingLeft();
        int compoundPaddingTop = getCompoundPaddingTop();
        int compoundPaddingRight = getCompoundPaddingRight();
        int compoundPaddingBottom = getCompoundPaddingBottom();
        int scrollX = this.mScrollX;
        int scrollY = this.mScrollY;
        int right = this.mRight;
        int left = this.mLeft;
        int bottom = this.mBottom;
        int top = this.mTop;
        boolean isLayoutRtl = isLayoutRtl();
        int offset = getHorizontalOffsetForDrawables();
        int leftOffset = isLayoutRtl ? 0 : offset;
        int rightOffset = isLayoutRtl ? offset : 0;
        Drawables dr = this.mDrawables;
        if (dr != null) {
            int vspace = ((bottom - top) - compoundPaddingBottom) - compoundPaddingTop;
            int hspace = ((right - left) - compoundPaddingRight) - compoundPaddingLeft2;
            if (dr.mShowing[0] != null) {
                canvas.save();
                canvas.translate(this.mPaddingLeft + scrollX + leftOffset, scrollY + compoundPaddingTop + ((vspace - dr.mDrawableHeightLeft) / 2));
                dr.mShowing[0].draw(canvas);
                canvas.restore();
            }
            if (dr.mShowing[2] != null) {
                canvas.save();
                canvas.translate(((((scrollX + right) - left) - this.mPaddingRight) - dr.mDrawableSizeRight) - rightOffset, scrollY + compoundPaddingTop + ((vspace - dr.mDrawableHeightRight) / 2));
                dr.mShowing[2].draw(canvas);
                canvas.restore();
            }
            if (dr.mShowing[1] != null) {
                canvas.save();
                canvas.translate(scrollX + compoundPaddingLeft2 + ((hspace - dr.mDrawableWidthTop) / 2), this.mPaddingTop + scrollY);
                dr.mShowing[1].draw(canvas);
                canvas.restore();
            }
            if (dr.mShowing[3] != null) {
                canvas.save();
                canvas.translate(scrollX + compoundPaddingLeft2 + ((hspace - dr.mDrawableWidthBottom) / 2), (((scrollY + bottom) - top) - this.mPaddingBottom) - dr.mDrawableSizeBottom);
                dr.mShowing[3].draw(canvas);
                canvas.restore();
            }
        }
        int color2 = this.mCurTextColor;
        if (this.mLayout == null) {
            assumeLayout();
        }
        Layout layout4 = this.mLayout;
        if (this.mHint != null && this.mText.length() == 0) {
            if (this.mHintTextColor != null) {
                color2 = this.mCurHintTextColor;
            }
            Layout layout5 = this.mHintLayout;
            color = color2;
            layout = layout5;
        } else {
            color = color2;
            layout = layout4;
        }
        this.mTextPaint.setColor(color);
        this.mTextPaint.drawableState = getDrawableState();
        canvas.save();
        int extendedPaddingTop = getExtendedPaddingTop();
        int extendedPaddingBottom = getExtendedPaddingBottom();
        int maxScrollY = this.mLayout.getHeight() - (((this.mBottom - this.mTop) - compoundPaddingBottom) - compoundPaddingTop);
        float clipLeft2 = compoundPaddingLeft2 + scrollX;
        float clipTop2 = scrollY == 0 ? 0.0f : extendedPaddingTop + scrollY;
        float clipRight2 = ((right - left) - getCompoundPaddingRight()) + scrollX;
        int maxScrollY2 = ((bottom - top) + scrollY) - (scrollY == maxScrollY ? 0 : extendedPaddingBottom);
        float clipBottom3 = maxScrollY2;
        float f = this.mShadowRadius;
        if (f != 0.0f) {
            float clipLeft3 = clipLeft2 + Math.min(0.0f, this.mShadowDx - f);
            float clipRight3 = clipRight2 + Math.max(0.0f, this.mShadowDx + this.mShadowRadius);
            clipLeft = clipLeft3;
            clipTop = clipTop2 + Math.min(0.0f, this.mShadowDy - this.mShadowRadius);
            clipBottom = clipBottom3 + Math.max(0.0f, this.mShadowDy + this.mShadowRadius);
            clipBottom2 = clipRight3;
        } else {
            clipLeft = clipLeft2;
            clipTop = clipTop2;
            clipBottom = clipBottom3;
            clipBottom2 = clipRight2;
        }
        canvas.clipRect(clipLeft, clipTop, clipBottom2, clipBottom);
        int voffsetText = 0;
        if ((this.mGravity & 112) != 48) {
            int voffsetText2 = getVerticalOffset(false);
            int voffsetCursor2 = getVerticalOffset(true);
            voffsetCursor = voffsetCursor2;
            voffsetText = voffsetText2;
        } else {
            voffsetCursor = 0;
        }
        canvas.translate(compoundPaddingLeft2, extendedPaddingTop + voffsetText);
        int layoutDirection = getLayoutDirection();
        int absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, layoutDirection);
        if (isMarqueeFadeEnabled()) {
            if (this.mSingleLine || getLineCount() != 1 || !canMarquee() || (absoluteGravity & 7) == 3) {
                clipRight = clipBottom2;
            } else {
                int width = this.mRight - this.mLeft;
                int padding = getCompoundPaddingLeft() + getCompoundPaddingRight();
                clipRight = clipBottom2;
                float dx = this.mLayout.getLineRight(0) - (width - padding);
                int width2 = layout.getParagraphDirection(0);
                canvas.translate(width2 * dx, 0.0f);
            }
            Marquee marquee = this.mMarquee;
            if (marquee == null || !marquee.isRunning()) {
                i = 0;
                z = false;
            } else {
                float dx2 = -this.mMarquee.getScroll();
                i = 0;
                z = false;
                canvas.translate(layout.getParagraphDirection(0) * dx2, 0.0f);
            }
        } else {
            clipRight = clipBottom2;
            i = 0;
            z = false;
        }
        int cursorOffsetVertical = voffsetCursor - voffsetText;
        maybeUpdateHighlightPaths();
        Path highlight = hasGesturePreviewHighlight() ? null : getUpdatedHighlightPath();
        Editor editor = this.mEditor;
        if (editor != null) {
            layout2 = layout;
            compoundPaddingLeft = i;
            editor.onDraw(canvas, layout, this.mHighlightPaths, this.mHighlightPaints, highlight, this.mHighlightPaint, cursorOffsetVertical);
        } else {
            layout2 = layout;
            compoundPaddingLeft = i;
            layout2.draw(canvas, this.mHighlightPaths, this.mHighlightPaints, highlight, this.mHighlightPaint, cursorOffsetVertical);
        }
        Marquee marquee2 = this.mMarquee;
        if (marquee2 != null && marquee2.shouldDrawGhost()) {
            float dx3 = this.mMarquee.getGhostOffset();
            canvas.translate(layout3.getParagraphDirection(compoundPaddingLeft) * dx3, 0.0f);
            layout2.draw(canvas, this.mHighlightPaths, this.mHighlightPaints, highlight, this.mHighlightPaint, cursorOffsetVertical);
        }
        canvas.restore();
    }

    @Override // android.view.View
    public void getFocusedRect(Rect r) {
        if (this.mLayout == null) {
            super.getFocusedRect(r);
            return;
        }
        int selEnd = getSelectionEndTransformed();
        if (selEnd < 0) {
            super.getFocusedRect(r);
            return;
        }
        int selStart = getSelectionStartTransformed();
        if (selStart < 0 || selStart >= selEnd) {
            int line = this.mLayout.getLineForOffset(selEnd);
            r.top = this.mLayout.getLineTop(line);
            r.bottom = this.mLayout.getLineBottom(line);
            r.left = ((int) this.mLayout.getPrimaryHorizontal(selEnd)) - 2;
            r.right = r.left + 4;
        } else {
            int lineStart = this.mLayout.getLineForOffset(selStart);
            int lineEnd = this.mLayout.getLineForOffset(selEnd);
            r.top = this.mLayout.getLineTop(lineStart);
            r.bottom = this.mLayout.getLineBottom(lineEnd);
            if (lineStart == lineEnd) {
                r.left = (int) this.mLayout.getPrimaryHorizontal(selStart);
                r.right = (int) this.mLayout.getPrimaryHorizontal(selEnd);
            } else {
                if (this.mHighlightPathBogus) {
                    if (this.mHighlightPath == null) {
                        this.mHighlightPath = new Path();
                    }
                    this.mHighlightPath.reset();
                    this.mLayout.getSelectionPath(selStart, selEnd, this.mHighlightPath);
                    this.mHighlightPathBogus = false;
                }
                RectF rectF = TEMP_RECTF;
                synchronized (rectF) {
                    this.mHighlightPath.computeBounds(rectF, true);
                    r.left = ((int) rectF.left) - 1;
                    r.right = ((int) rectF.right) + 1;
                }
            }
        }
        int paddingLeft = getCompoundPaddingLeft();
        int paddingTop = getExtendedPaddingTop();
        if ((this.mGravity & 112) != 48) {
            paddingTop += getVerticalOffset(false);
        }
        r.offset(paddingLeft, paddingTop);
        int paddingBottom = getExtendedPaddingBottom();
        r.bottom += paddingBottom;
    }

    public int getLineCount() {
        Layout layout = this.mLayout;
        if (layout != null) {
            return layout.getLineCount();
        }
        return 0;
    }

    public int getLineBounds(int line, Rect bounds) {
        Layout layout = this.mLayout;
        if (layout == null) {
            if (bounds != null) {
                bounds.set(0, 0, 0, 0);
            }
            return 0;
        }
        int baseline = layout.getLineBounds(line, bounds);
        int voffset = getExtendedPaddingTop();
        if ((this.mGravity & 112) != 48) {
            voffset += getVerticalOffset(true);
        }
        if (bounds != null) {
            bounds.offset(getCompoundPaddingLeft(), voffset);
        }
        return baseline + voffset;
    }

    @Override // android.view.View
    public int getBaseline() {
        if (this.mLayout == null) {
            return super.getBaseline();
        }
        return getBaselineOffset() + this.mLayout.getLineBaseline(0);
    }

    int getBaselineOffset() {
        int voffset = 0;
        if ((this.mGravity & 112) != 48) {
            voffset = getVerticalOffset(true);
        }
        if (isLayoutModeOptical(this.mParent)) {
            voffset -= getOpticalInsets().top;
        }
        return getExtendedPaddingTop() + voffset;
    }

    @Override // android.view.View
    protected int getFadeTop(boolean offsetRequired) {
        if (this.mLayout == null) {
            return 0;
        }
        int voffset = 0;
        if ((this.mGravity & 112) != 48) {
            voffset = getVerticalOffset(true);
        }
        if (offsetRequired) {
            voffset += getTopPaddingOffset();
        }
        return getExtendedPaddingTop() + voffset;
    }

    @Override // android.view.View
    protected int getFadeHeight(boolean offsetRequired) {
        Layout layout = this.mLayout;
        if (layout != null) {
            return layout.getHeight();
        }
        return 0;
    }

    @Override // android.view.View
    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        if (this.mSpannable != null && this.mLinksClickable) {
            float x = event.getX(pointerIndex);
            float y = event.getY(pointerIndex);
            int offset = getOffsetForPosition(x, y);
            ClickableSpan[] clickables = (ClickableSpan[]) this.mSpannable.getSpans(offset, offset, ClickableSpan.class);
            if (clickables.length > 0) {
                return PointerIcon.getSystemIcon(this.mContext, 1002);
            }
        }
        if (isTextSelectable() || isTextEditable()) {
            return PointerIcon.getSystemIcon(this.mContext, 1008);
        }
        return super.onResolvePointerIcon(event, pointerIndex);
    }

    @Override // android.view.View
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == 4 && handleBackInTextActionModeIfNeeded(event)) {
            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public boolean handleBackInTextActionModeIfNeeded(KeyEvent event) {
        Editor editor = this.mEditor;
        if (editor == null || editor.getTextActionMode() == null) {
            return false;
        }
        if (event.getAction() == 0 && event.getRepeatCount() == 0) {
            KeyEvent.DispatcherState state = getKeyDispatcherState();
            if (state != null) {
                state.startTracking(event, this);
            }
            return true;
        }
        if (event.getAction() == 1) {
            KeyEvent.DispatcherState state2 = getKeyDispatcherState();
            if (state2 != null) {
                state2.handleUpEvent(event);
            }
            if (event.isTracking() && !event.isCanceled()) {
                stopTextActionMode();
                return true;
            }
        }
        return false;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int which = doKeyDown(keyCode, event, null);
        if (which == 0) {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        KeyEvent down = KeyEvent.changeAction(event, 0);
        int which = doKeyDown(keyCode, down, event);
        if (which == 0) {
            return super.onKeyMultiple(keyCode, repeatCount, event);
        }
        if (which == -1) {
            return true;
        }
        int repeatCount2 = repeatCount - 1;
        KeyEvent up = KeyEvent.changeAction(event, 1);
        if (which == 1) {
            this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, keyCode, up);
            while (true) {
                repeatCount2--;
                if (repeatCount2 <= 0) {
                    break;
                }
                this.mEditor.mKeyListener.onKeyDown(this, (Editable) this.mText, keyCode, down);
                this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, keyCode, up);
            }
            hideErrorIfUnchanged();
        } else if (which == 2) {
            this.mMovement.onKeyUp(this, this.mSpannable, keyCode, up);
            while (true) {
                repeatCount2--;
                if (repeatCount2 <= 0) {
                    break;
                }
                this.mMovement.onKeyDown(this, this.mSpannable, keyCode, down);
                this.mMovement.onKeyUp(this, this.mSpannable, keyCode, up);
            }
        }
        return true;
    }

    private boolean shouldAdvanceFocusOnEnter() {
        int variation;
        if (getKeyListener() == null) {
            return false;
        }
        if (this.mSingleLine) {
            return true;
        }
        Editor editor = this.mEditor;
        return editor != null && (editor.mInputType & 15) == 1 && ((variation = this.mEditor.mInputType & InputType.TYPE_MASK_VARIATION) == 32 || variation == 48);
    }

    private boolean isDirectionalNavigationKey(int keyCode) {
        switch (keyCode) {
            case 19:
            case 20:
            case 21:
            case 22:
                return true;
            default:
                return false;
        }
    }

    private int doKeyDown(int keyCode, KeyEvent event, KeyEvent otherEvent) {
        if (isEnabled()) {
            if (event.getRepeatCount() == 0 && !KeyEvent.isModifierKey(keyCode)) {
                this.mPreventDefaultMovement = false;
            }
            switch (keyCode) {
                case 4:
                    Editor editor = this.mEditor;
                    if (editor != null && editor.getTextActionMode() != null) {
                        stopTextActionMode();
                        return -1;
                    }
                    break;
                case 23:
                    if (event.hasNoModifiers() && shouldAdvanceFocusOnEnter()) {
                        return 0;
                    }
                    break;
                case 61:
                    if (event.hasNoModifiers() || event.hasModifiers(1)) {
                        return 0;
                    }
                    break;
                case 66:
                case 160:
                    if (event.hasNoModifiers()) {
                        Editor editor2 = this.mEditor;
                        if (editor2 != null && editor2.mInputContentType != null && this.mEditor.mInputContentType.onEditorActionListener != null && this.mEditor.mInputContentType.onEditorActionListener.onEditorAction(this, getActionIdForEnterEvent(), event)) {
                            this.mEditor.mInputContentType.enterDown = true;
                            return -1;
                        } else if ((event.getFlags() & 16) != 0 || shouldAdvanceFocusOnEnter()) {
                            return hasOnClickListeners() ? 0 : -1;
                        }
                    }
                    break;
                case 112:
                    if (event.hasModifiers(1) && canCut() && onTextContextMenuItem(16908320)) {
                        return -1;
                    }
                    break;
                case 124:
                    if (event.hasModifiers(4096) && canCopy()) {
                        if (onTextContextMenuItem(16908321)) {
                            return -1;
                        }
                    } else if (event.hasModifiers(1) && canPaste() && onTextContextMenuItem(16908322)) {
                        return -1;
                    }
                    break;
                case 277:
                    if (event.hasNoModifiers() && canCut() && onTextContextMenuItem(16908320)) {
                        return -1;
                    }
                    break;
                case 278:
                    if (event.hasNoModifiers() && canCopy() && onTextContextMenuItem(16908321)) {
                        return -1;
                    }
                    break;
                case 279:
                    if (event.hasNoModifiers() && canPaste() && onTextContextMenuItem(16908322)) {
                        return -1;
                    }
                    break;
            }
            Editor editor3 = this.mEditor;
            if (editor3 != null && editor3.mKeyListener != null) {
                boolean doDown = true;
                if (otherEvent != null) {
                    try {
                        beginBatchEdit();
                        boolean handled = this.mEditor.mKeyListener.onKeyOther(this, (Editable) this.mText, otherEvent);
                        hideErrorIfUnchanged();
                        doDown = false;
                        if (handled) {
                            endBatchEdit();
                            return -1;
                        }
                    } catch (AbstractMethodError e) {
                    } catch (Throwable th) {
                        endBatchEdit();
                        throw th;
                    }
                    endBatchEdit();
                }
                if (doDown) {
                    beginBatchEdit();
                    boolean handled2 = this.mEditor.mKeyListener.onKeyDown(this, (Editable) this.mText, keyCode, event);
                    endBatchEdit();
                    hideErrorIfUnchanged();
                    if (handled2) {
                        return 1;
                    }
                }
            }
            MovementMethod movementMethod = this.mMovement;
            if (movementMethod != null && this.mLayout != null) {
                boolean doDown2 = true;
                if (otherEvent != null) {
                    try {
                        boolean handled3 = movementMethod.onKeyOther(this, this.mSpannable, otherEvent);
                        doDown2 = false;
                        if (handled3) {
                            return -1;
                        }
                    } catch (AbstractMethodError e2) {
                    }
                }
                if (doDown2 && this.mMovement.onKeyDown(this, this.mSpannable, keyCode, event)) {
                    if (event.getRepeatCount() == 0 && !KeyEvent.isModifierKey(keyCode)) {
                        this.mPreventDefaultMovement = true;
                        return 2;
                    }
                    return 2;
                } else if (event.getSource() == 257 && isDirectionalNavigationKey(keyCode)) {
                    return -1;
                }
            }
            return (!this.mPreventDefaultMovement || KeyEvent.isModifierKey(keyCode)) ? 0 : -1;
        }
        return 0;
    }

    public void resetErrorChangedFlag() {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.mErrorWasChanged = false;
        }
    }

    public void hideErrorIfUnchanged() {
        Editor editor = this.mEditor;
        if (editor != null && editor.mError != null && !this.mEditor.mErrorWasChanged) {
            setError(null, null);
        }
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        InputMethodManager imm;
        if (!isEnabled()) {
            return super.onKeyUp(keyCode, event);
        }
        if (!KeyEvent.isModifierKey(keyCode)) {
            this.mPreventDefaultMovement = false;
        }
        switch (keyCode) {
            case 23:
                if (event.hasNoModifiers() && !hasOnClickListeners() && this.mMovement != null && (this.mText instanceof Editable) && this.mLayout != null && onCheckIsTextEditor()) {
                    InputMethodManager imm2 = getInputMethodManager();
                    viewClicked(imm2);
                    if (imm2 != null && getShowSoftInputOnFocus()) {
                        imm2.showSoftInput(this, 0);
                    }
                }
                return super.onKeyUp(keyCode, event);
            case 66:
            case 160:
                if (event.hasNoModifiers()) {
                    Editor editor = this.mEditor;
                    if (editor != null && editor.mInputContentType != null && this.mEditor.mInputContentType.onEditorActionListener != null && this.mEditor.mInputContentType.enterDown) {
                        this.mEditor.mInputContentType.enterDown = false;
                        if (this.mEditor.mInputContentType.onEditorActionListener.onEditorAction(this, getActionIdForEnterEvent(), event)) {
                            return true;
                        }
                    }
                    if (((event.getFlags() & 16) != 0 || shouldAdvanceFocusOnEnter()) && !hasOnClickListeners()) {
                        View v = focusSearch(130);
                        if (v != null) {
                            if (!v.requestFocus(130)) {
                                throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                            }
                            super.onKeyUp(keyCode, event);
                            return true;
                        } else if ((event.getFlags() & 16) != 0 && (imm = getInputMethodManager()) != null && imm.isActive(this)) {
                            imm.hideSoftInputFromWindow(getWindowToken(), 0);
                        }
                    }
                    return super.onKeyUp(keyCode, event);
                }
                break;
        }
        Editor editor2 = this.mEditor;
        if (editor2 != null && editor2.mKeyListener != null && this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, keyCode, event)) {
            return true;
        }
        MovementMethod movementMethod = this.mMovement;
        if (movementMethod != null && this.mLayout != null && movementMethod.onKeyUp(this, this.mSpannable, keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private int getActionIdForEnterEvent() {
        if (!isSingleLine()) {
            return 0;
        }
        return getImeOptions() & 255;
    }

    @Override // android.view.View
    public boolean onCheckIsTextEditor() {
        Editor editor = this.mEditor;
        return (editor == null || editor.mInputType == 0) ? false : true;
    }

    private boolean hasEditorInFocusSearchDirection(int direction) {
        View nextView = focusSearch(direction);
        return nextView != null && nextView.onCheckIsTextEditor();
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (onCheckIsTextEditor() && isEnabled()) {
            this.mEditor.createInputMethodStateIfNeeded();
            this.mEditor.mInputMethodState.mUpdateCursorAnchorInfoMode = 0;
            this.mEditor.mInputMethodState.mUpdateCursorAnchorInfoFilter = 0;
            outAttrs.inputType = getInputType();
            if (this.mEditor.mInputContentType != null) {
                outAttrs.imeOptions = this.mEditor.mInputContentType.imeOptions;
                outAttrs.privateImeOptions = this.mEditor.mInputContentType.privateImeOptions;
                outAttrs.actionLabel = this.mEditor.mInputContentType.imeActionLabel;
                outAttrs.actionId = this.mEditor.mInputContentType.imeActionId;
                outAttrs.extras = this.mEditor.mInputContentType.extras;
                outAttrs.hintLocales = this.mEditor.mInputContentType.imeHintLocales;
            } else {
                outAttrs.imeOptions = 0;
                outAttrs.hintLocales = null;
            }
            if (hasEditorInFocusSearchDirection(130)) {
                outAttrs.imeOptions |= 134217728;
            }
            if (hasEditorInFocusSearchDirection(33)) {
                outAttrs.imeOptions |= 67108864;
            }
            if ((outAttrs.imeOptions & 255) == 0) {
                if ((outAttrs.imeOptions & 134217728) != 0) {
                    outAttrs.imeOptions |= 5;
                } else {
                    outAttrs.imeOptions |= 6;
                }
                if (!shouldAdvanceFocusOnEnter()) {
                    outAttrs.imeOptions |= 1073741824;
                }
            }
            if (getResources().getConfiguration().orientation == 1) {
                outAttrs.internalImeOptions |= 1;
            }
            if (isMultilineInputType(outAttrs.inputType)) {
                outAttrs.imeOptions |= 1073741824;
            }
            outAttrs.hintText = this.mHint;
            outAttrs.targetInputMethodUser = this.mTextOperationUser;
            if (this.mText instanceof Editable) {
                InputConnection ic = new EditableInputConnection(this);
                outAttrs.initialSelStart = getSelectionStart();
                outAttrs.initialSelEnd = getSelectionEnd();
                outAttrs.initialCapsMode = ic.getCursorCapsMode(getInputType());
                outAttrs.setInitialSurroundingText(this.mText);
                outAttrs.contentMimeTypes = getReceiveContentMimeTypes();
                ArrayList<Class<? extends HandwritingGesture>> gestures = new ArrayList<>();
                gestures.add(SelectGesture.class);
                gestures.add(SelectRangeGesture.class);
                gestures.add(DeleteGesture.class);
                gestures.add(DeleteRangeGesture.class);
                gestures.add(InsertGesture.class);
                gestures.add(RemoveSpaceGesture.class);
                gestures.add(JoinOrSplitGesture.class);
                gestures.add(InsertModeGesture.class);
                outAttrs.setSupportedHandwritingGestures(gestures);
                Set<Class<? extends PreviewableHandwritingGesture>> previews = new ArraySet<>();
                previews.add(SelectGesture.class);
                previews.add(SelectRangeGesture.class);
                previews.add(DeleteGesture.class);
                previews.add(DeleteRangeGesture.class);
                outAttrs.setSupportedHandwritingGesturePreviews(previews);
                return ic;
            }
        }
        return null;
    }

    public void onRequestCursorUpdatesInternal(int cursorUpdateMode, int cursorUpdateFilter) {
        this.mEditor.mInputMethodState.mUpdateCursorAnchorInfoMode = cursorUpdateMode;
        this.mEditor.mInputMethodState.mUpdateCursorAnchorInfoFilter = cursorUpdateFilter;
        if ((cursorUpdateMode & 1) != 0 && !isInLayout()) {
            requestLayout();
        }
    }

    public boolean extractText(ExtractedTextRequest request, ExtractedText outText) {
        createEditorIfNeeded();
        return this.mEditor.extractText(request, outText);
    }

    static void removeParcelableSpans(Spannable spannable, int start, int end) {
        Object[] spans = spannable.getSpans(start, end, ParcelableSpan.class);
        int i = spans.length;
        while (i > 0) {
            i--;
            spannable.removeSpan(spans[i]);
        }
    }

    public void setExtractedText(ExtractedText text) {
        int start;
        int end;
        Editable content = getEditableText();
        if (text.text != null) {
            if (content == null) {
                setText(text.text, BufferType.EDITABLE);
            } else {
                int end2 = content.length();
                if (text.partialStartOffset < 0) {
                    start = 0;
                    end = end2;
                } else {
                    int N = content.length();
                    int start2 = text.partialStartOffset;
                    if (start2 > N) {
                        start2 = N;
                    }
                    int end3 = text.partialEndOffset;
                    if (end3 > N) {
                        end3 = N;
                    }
                    start = start2;
                    end = end3;
                }
                removeParcelableSpans(content, start, end);
                if (TextUtils.equals(content.subSequence(start, end), text.text)) {
                    if (text.text instanceof Spanned) {
                        TextUtils.copySpansFrom((Spanned) text.text, 0, end - start, Object.class, content, start);
                    }
                } else {
                    content.replace(start, end, text.text);
                }
            }
        }
        Spannable sp = (Spannable) getText();
        int N2 = sp.length();
        int start3 = text.selectionStart;
        if (start3 < 0) {
            start3 = 0;
        } else if (start3 > N2) {
            start3 = N2;
        }
        int end4 = text.selectionEnd;
        if (end4 < 0) {
            end4 = 0;
        } else if (end4 > N2) {
            end4 = N2;
        }
        Selection.setSelection(sp, start3, end4);
        if ((text.flags & 2) != 0) {
            MetaKeyKeyListener.startSelecting(this, sp);
        } else {
            MetaKeyKeyListener.stopSelecting(this, sp);
        }
        setHintInternal(text.hint);
    }

    public void setExtracting(ExtractedTextRequest req) {
        if (this.mEditor.mInputMethodState != null) {
            this.mEditor.mInputMethodState.mExtractedTextRequest = req;
        }
        this.mEditor.hideCursorAndSpanControllers();
        stopTextActionMode();
        if (this.mEditor.mSelectionModifierCursorController != null) {
            this.mEditor.mSelectionModifierCursorController.resetTouchOffsets();
        }
    }

    public void onCommitCompletion(CompletionInfo text) {
    }

    public void onCommitCorrection(CorrectionInfo info) {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onCommitCorrection(info);
        }
    }

    public void beginBatchEdit() {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.beginBatchEdit();
        }
    }

    public void endBatchEdit() {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.endBatchEdit();
        }
    }

    public void onBeginBatchEdit() {
    }

    public void onEndBatchEdit() {
    }

    public void onPerformSpellCheck() {
        Editor editor = this.mEditor;
        if (editor != null && editor.mSpellChecker != null) {
            this.mEditor.mSpellChecker.onPerformSpellCheck();
        }
    }

    public boolean onPrivateIMECommand(String action, Bundle data) {
        return false;
    }

    public boolean isOffsetMappingAvailable() {
        return this.mTransformation != null && (this.mTransformed instanceof OffsetMapping);
    }

    public boolean previewHandwritingGesture(PreviewableHandwritingGesture gesture, CancellationSignal cancellationSignal) {
        if (gesture instanceof SelectGesture) {
            performHandwritingSelectGesture((SelectGesture) gesture, true);
        } else if (gesture instanceof SelectRangeGesture) {
            performHandwritingSelectRangeGesture((SelectRangeGesture) gesture, true);
        } else if (gesture instanceof DeleteGesture) {
            performHandwritingDeleteGesture((DeleteGesture) gesture, true);
        } else if (gesture instanceof DeleteRangeGesture) {
            performHandwritingDeleteRangeGesture((DeleteRangeGesture) gesture, true);
        } else {
            return false;
        }
        if (cancellationSignal != null) {
            cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: android.widget.TextView$$ExternalSyntheticLambda1
                @Override // android.p008os.CancellationSignal.OnCancelListener
                public final void onCancel() {
                    TextView.this.clearGesturePreviewHighlight();
                }
            });
        }
        return true;
    }

    public int performHandwritingSelectGesture(SelectGesture gesture) {
        return performHandwritingSelectGesture(gesture, false);
    }

    private int performHandwritingSelectGesture(SelectGesture gesture, boolean isPreview) {
        if (isOffsetMappingAvailable()) {
            return 3;
        }
        int[] range = getRangeForRect(convertFromScreenToContentCoordinates(gesture.getSelectionArea()), gesture.getGranularity());
        if (range == null) {
            return handleGestureFailure(gesture, isPreview);
        }
        return performHandwritingSelectGesture(range, isPreview);
    }

    private int performHandwritingSelectGesture(int[] range, boolean isPreview) {
        if (isPreview) {
            setSelectGesturePreviewHighlight(range[0], range[1]);
        } else {
            Selection.setSelection(getEditableText(), range[0], range[1]);
            this.mEditor.startSelectionActionModeAsync(false);
        }
        return 1;
    }

    public int performHandwritingSelectRangeGesture(SelectRangeGesture gesture) {
        return performHandwritingSelectRangeGesture(gesture, false);
    }

    private int performHandwritingSelectRangeGesture(SelectRangeGesture gesture, boolean isPreview) {
        if (isOffsetMappingAvailable()) {
            return 3;
        }
        int[] startRange = getRangeForRect(convertFromScreenToContentCoordinates(gesture.getSelectionStartArea()), gesture.getGranularity());
        if (startRange == null) {
            return handleGestureFailure(gesture, isPreview);
        }
        int[] endRange = getRangeForRect(convertFromScreenToContentCoordinates(gesture.getSelectionEndArea()), gesture.getGranularity());
        if (endRange == null) {
            return handleGestureFailure(gesture, isPreview);
        }
        int[] range = {Math.min(startRange[0], endRange[0]), Math.max(startRange[1], endRange[1])};
        return performHandwritingSelectGesture(range, isPreview);
    }

    public int performHandwritingDeleteGesture(DeleteGesture gesture) {
        return performHandwritingDeleteGesture(gesture, false);
    }

    private int performHandwritingDeleteGesture(DeleteGesture gesture, boolean isPreview) {
        if (isOffsetMappingAvailable()) {
            return 3;
        }
        int[] range = getRangeForRect(convertFromScreenToContentCoordinates(gesture.getDeletionArea()), gesture.getGranularity());
        if (range == null) {
            return handleGestureFailure(gesture, isPreview);
        }
        return performHandwritingDeleteGesture(range, gesture.getGranularity(), isPreview);
    }

    private int performHandwritingDeleteGesture(int[] range, int granularity, boolean isPreview) {
        if (isPreview) {
            setDeleteGesturePreviewHighlight(range[0], range[1]);
        } else {
            if (granularity == 1) {
                range = adjustHandwritingDeleteGestureRange(range);
            }
            Selection.setSelection(getEditableText(), range[0]);
            getEditableText().delete(range[0], range[1]);
        }
        return 1;
    }

    public int performHandwritingDeleteRangeGesture(DeleteRangeGesture gesture) {
        return performHandwritingDeleteRangeGesture(gesture, false);
    }

    private int performHandwritingDeleteRangeGesture(DeleteRangeGesture gesture, boolean isPreview) {
        if (isOffsetMappingAvailable()) {
            return 3;
        }
        int[] startRange = getRangeForRect(convertFromScreenToContentCoordinates(gesture.getDeletionStartArea()), gesture.getGranularity());
        if (startRange == null) {
            return handleGestureFailure(gesture, isPreview);
        }
        int[] endRange = getRangeForRect(convertFromScreenToContentCoordinates(gesture.getDeletionEndArea()), gesture.getGranularity());
        if (endRange == null) {
            return handleGestureFailure(gesture, isPreview);
        }
        int[] range = {Math.min(startRange[0], endRange[0]), Math.max(startRange[1], endRange[1])};
        return performHandwritingDeleteGesture(range, gesture.getGranularity(), isPreview);
    }

    private int[] adjustHandwritingDeleteGestureRange(int[] range) {
        int codePointBeforeStart;
        int start = range[0];
        int end = range[1];
        int codePointAtEnd = 10;
        if (start <= 0) {
            codePointBeforeStart = 10;
        } else {
            codePointBeforeStart = Character.codePointBefore(this.mText, start);
        }
        if (end < this.mText.length()) {
            codePointAtEnd = Character.codePointAt(this.mText, end);
        }
        if (TextUtils.isWhitespaceExceptNewline(codePointBeforeStart) && (TextUtils.isWhitespace(codePointAtEnd) || TextUtils.isPunctuation(codePointAtEnd))) {
            do {
                start -= Character.charCount(codePointBeforeStart);
                if (start == 0) {
                    break;
                }
                codePointBeforeStart = Character.codePointBefore(this.mText, start);
            } while (TextUtils.isWhitespaceExceptNewline(codePointBeforeStart));
            return new int[]{start, end};
        } else if (TextUtils.isWhitespaceExceptNewline(codePointAtEnd) && (TextUtils.isWhitespace(codePointBeforeStart) || TextUtils.isPunctuation(codePointBeforeStart))) {
            do {
                end += Character.charCount(codePointAtEnd);
                if (end == this.mText.length()) {
                    break;
                }
                codePointAtEnd = Character.codePointAt(this.mText, end);
            } while (TextUtils.isWhitespaceExceptNewline(codePointAtEnd));
            return new int[]{start, end};
        } else {
            return range;
        }
    }

    public int performHandwritingInsertGesture(InsertGesture gesture) {
        if (isOffsetMappingAvailable()) {
            return 3;
        }
        PointF point = convertFromScreenToContentCoordinates(gesture.getInsertionPoint());
        int line = getLineForHandwritingGesture(point);
        if (line == -1) {
            return handleGestureFailure(gesture);
        }
        int offset = this.mLayout.getOffsetForHorizontal(line, point.f78x);
        String textToInsert = gesture.getTextToInsert();
        return tryInsertTextForHandwritingGesture(offset, textToInsert, gesture);
    }

    public int performHandwritingRemoveSpaceGesture(RemoveSpaceGesture gesture) {
        int line;
        if (!isOffsetMappingAvailable()) {
            PointF startPoint = convertFromScreenToContentCoordinates(gesture.getStartPoint());
            PointF endPoint = convertFromScreenToContentCoordinates(gesture.getEndPoint());
            int startPointLine = getLineForHandwritingGesture(startPoint);
            int endPointLine = getLineForHandwritingGesture(endPoint);
            if (startPointLine == -1) {
                if (endPointLine == -1) {
                    return handleGestureFailure(gesture);
                }
                line = endPointLine;
            } else {
                line = endPointLine == -1 ? startPointLine : Math.min(startPointLine, endPointLine);
            }
            float lineVerticalCenter = (this.mLayout.getLineTop(line) + this.mLayout.getLineBottom(line, false)) / 2.0f;
            RectF area = new RectF(Math.min(startPoint.f78x, endPoint.f78x), lineVerticalCenter + 0.1f, Math.max(startPoint.f78x, endPoint.f78x), lineVerticalCenter - 0.1f);
            int[] range = this.mLayout.getRangeForRect(area, new GraphemeClusterSegmentFinder(this.mText, this.mTextPaint), Layout.INCLUSION_STRATEGY_ANY_OVERLAP);
            if (range == null) {
                return handleGestureFailure(gesture);
            }
            int startOffset = range[0];
            int endOffset = range[1];
            Pattern whitespacePattern = getWhitespacePattern();
            Matcher matcher = whitespacePattern.matcher(this.mText.subSequence(startOffset, endOffset));
            int lastRemoveOffset = -1;
            while (matcher.find()) {
                lastRemoveOffset = startOffset + matcher.start();
                getEditableText().delete(lastRemoveOffset, startOffset + matcher.end());
                startOffset = lastRemoveOffset;
                endOffset -= matcher.end() - matcher.start();
                if (startOffset == endOffset) {
                    break;
                }
                matcher = whitespacePattern.matcher(this.mText.subSequence(startOffset, endOffset));
            }
            if (lastRemoveOffset == -1) {
                return handleGestureFailure(gesture);
            }
            Selection.setSelection(getEditableText(), lastRemoveOffset);
            return 1;
        }
        return 3;
    }

    public int performHandwritingJoinOrSplitGesture(JoinOrSplitGesture gesture) {
        if (isOffsetMappingAvailable()) {
            return 3;
        }
        PointF point = convertFromScreenToContentCoordinates(gesture.getJoinOrSplitPoint());
        int line = getLineForHandwritingGesture(point);
        if (line == -1) {
            return handleGestureFailure(gesture);
        }
        int startOffset = this.mLayout.getOffsetForHorizontal(line, point.f78x);
        if (this.mLayout.isLevelBoundary(startOffset)) {
            return handleGestureFailure(gesture);
        }
        int endOffset = startOffset;
        while (startOffset > 0) {
            int codePointBeforeStart = Character.codePointBefore(this.mText, startOffset);
            if (!TextUtils.isWhitespace(codePointBeforeStart)) {
                break;
            }
            startOffset -= Character.charCount(codePointBeforeStart);
        }
        while (endOffset < this.mText.length()) {
            int codePointAtEnd = Character.codePointAt(this.mText, endOffset);
            if (!TextUtils.isWhitespace(codePointAtEnd)) {
                break;
            }
            endOffset += Character.charCount(codePointAtEnd);
        }
        if (startOffset < endOffset) {
            Selection.setSelection(getEditableText(), startOffset);
            getEditableText().delete(startOffset, endOffset);
            return 1;
        }
        return tryInsertTextForHandwritingGesture(startOffset, " ", gesture);
    }

    public int performHandwritingInsertModeGesture(InsertModeGesture gesture) {
        PointF insertPoint = convertFromScreenToContentCoordinates(gesture.getInsertionPoint());
        int line = getLineForHandwritingGesture(insertPoint);
        CancellationSignal cancellationSignal = gesture.getCancellationSignal();
        if (line == -1 || cancellationSignal == null) {
            int offset = handleGestureFailure(gesture);
            return offset;
        }
        int offset2 = this.mLayout.getOffsetForHorizontal(line, insertPoint.f78x);
        if (!this.mEditor.enterInsertMode(offset2)) {
            return 3;
        }
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: android.widget.TextView$$ExternalSyntheticLambda0
            @Override // android.p008os.CancellationSignal.OnCancelListener
            public final void onCancel() {
                TextView.this.lambda$performHandwritingInsertModeGesture$3();
            }
        });
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$performHandwritingInsertModeGesture$3() {
        this.mEditor.exitInsertMode();
    }

    private int handleGestureFailure(HandwritingGesture gesture) {
        return handleGestureFailure(gesture, false);
    }

    private int handleGestureFailure(HandwritingGesture gesture, boolean isPreview) {
        clearGesturePreviewHighlight();
        if (!isPreview && !TextUtils.isEmpty(gesture.getFallbackText())) {
            getEditableText().replace(getSelectionStart(), getSelectionEnd(), gesture.getFallbackText());
            return 5;
        }
        return 3;
    }

    private int getLineForHandwritingGesture(PointF point) {
        int line = this.mLayout.getLineForVertical((int) point.f79y);
        int lineMargin = ViewConfiguration.get(this.mContext).getScaledHandwritingGestureLineMargin();
        if (line < this.mLayout.getLineCount() - 1 && point.f79y > this.mLayout.getLineBottom(line) - lineMargin && point.f79y > (this.mLayout.getLineBottom(line, false) + this.mLayout.getLineBottom(line)) / 2.0f) {
            line++;
        } else if (point.f79y < this.mLayout.getLineTop(line) - lineMargin || point.f79y > this.mLayout.getLineBottom(line, false) + lineMargin) {
            return -1;
        }
        if (point.f78x < (-lineMargin) || point.f78x > this.mLayout.getWidth() + lineMargin) {
            return -1;
        }
        return line;
    }

    private int[] getRangeForRect(RectF area, int granularity) {
        SegmentFinder segmentFinder;
        if (granularity == 1) {
            WordIterator wordIterator = getWordIterator();
            CharSequence charSequence = this.mText;
            wordIterator.setCharSequence(charSequence, 0, charSequence.length());
            segmentFinder = new WordSegmentFinder(this.mText, wordIterator);
        } else {
            segmentFinder = new GraphemeClusterSegmentFinder(this.mText, this.mTextPaint);
        }
        return this.mLayout.getRangeForRect(area, segmentFinder, Layout.INCLUSION_STRATEGY_CONTAINS_CENTER);
    }

    private int tryInsertTextForHandwritingGesture(int offset, String textToInsert, HandwritingGesture gesture) {
        Editable editableText = getEditableText();
        if (this.mTempCursor == null) {
            this.mTempCursor = new NoCopySpan.Concrete();
        }
        editableText.setSpan(this.mTempCursor, offset, offset, 34);
        editableText.insert(offset, textToInsert);
        int newOffset = editableText.getSpanStart(this.mTempCursor);
        editableText.removeSpan(this.mTempCursor);
        if (newOffset == offset) {
            return handleGestureFailure(gesture);
        }
        Selection.setSelection(editableText, newOffset);
        return 1;
    }

    private Pattern getWhitespacePattern() {
        if (this.mWhitespacePattern == null) {
            this.mWhitespacePattern = Pattern.compile("\\s+");
        }
        return this.mWhitespacePattern;
    }

    public void nullLayouts() {
        Layout layout = this.mLayout;
        if ((layout instanceof BoringLayout) && this.mSavedLayout == null) {
            this.mSavedLayout = (BoringLayout) layout;
        }
        Layout layout2 = this.mHintLayout;
        if ((layout2 instanceof BoringLayout) && this.mSavedHintLayout == null) {
            this.mSavedHintLayout = (BoringLayout) layout2;
        }
        this.mHintLayout = null;
        this.mLayout = null;
        this.mSavedMarqueeModeLayout = null;
        this.mHintBoring = null;
        this.mBoring = null;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.prepareCursorControllers();
        }
    }

    private void assumeLayout() {
        int width = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (width < 1) {
            width = 0;
        }
        int physicalWidth = width;
        if (this.mHorizontallyScrolling) {
            width = 1048576;
        }
        BoringLayout.Metrics metrics = UNKNOWN_BORING;
        makeNewLayout(width, physicalWidth, metrics, metrics, physicalWidth, false);
    }

    private Layout.Alignment getLayoutAlignment() {
        switch (getTextAlignment()) {
            case 1:
                switch (this.mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
                    case 1:
                        Layout.Alignment alignment = Layout.Alignment.ALIGN_CENTER;
                        return alignment;
                    case 3:
                        Layout.Alignment alignment2 = Layout.Alignment.ALIGN_LEFT;
                        return alignment2;
                    case 5:
                        Layout.Alignment alignment3 = Layout.Alignment.ALIGN_RIGHT;
                        return alignment3;
                    case Gravity.START /* 8388611 */:
                        Layout.Alignment alignment4 = Layout.Alignment.ALIGN_NORMAL;
                        return alignment4;
                    case Gravity.END /* 8388613 */:
                        Layout.Alignment alignment5 = Layout.Alignment.ALIGN_OPPOSITE;
                        return alignment5;
                    default:
                        Layout.Alignment alignment6 = Layout.Alignment.ALIGN_NORMAL;
                        return alignment6;
                }
            case 2:
                Layout.Alignment alignment7 = Layout.Alignment.ALIGN_NORMAL;
                return alignment7;
            case 3:
                Layout.Alignment alignment8 = Layout.Alignment.ALIGN_OPPOSITE;
                return alignment8;
            case 4:
                Layout.Alignment alignment9 = Layout.Alignment.ALIGN_CENTER;
                return alignment9;
            case 5:
                if (getLayoutDirection() == 1) {
                    Layout.Alignment alignment10 = Layout.Alignment.ALIGN_RIGHT;
                    return alignment10;
                }
                Layout.Alignment alignment11 = Layout.Alignment.ALIGN_LEFT;
                return alignment11;
            case 6:
                if (getLayoutDirection() == 1) {
                    Layout.Alignment alignment12 = Layout.Alignment.ALIGN_LEFT;
                    return alignment12;
                }
                Layout.Alignment alignment13 = Layout.Alignment.ALIGN_RIGHT;
                return alignment13;
            default:
                Layout.Alignment alignment14 = Layout.Alignment.ALIGN_NORMAL;
                return alignment14;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:119:0x0242, code lost:
        if (r21 == r22.mLayout.getParagraphDirection(r7)) goto L94;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:136:0x026e  */
    /* JADX WARN: Removed duplicated region for block: B:138:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void makeNewLayout(int wantWidth, int hintWidth, BoringLayout.Metrics boring, BoringLayout.Metrics hintBoring, int ellipsisWidth, boolean bringIntoView) {
        int wantWidth2;
        int hintWidth2;
        TextUtils.TruncateAt effectiveEllipsize;
        int oldDir;
        boolean z;
        Layout.Alignment alignment;
        boolean z2;
        int i;
        int oldDir2;
        int i2;
        boolean z3;
        Editor editor;
        int height;
        BoringLayout.Metrics hintBoring2;
        Layout.Alignment alignment2;
        int i3;
        boolean z4;
        stopMarquee();
        this.mOldMaximum = this.mMaximum;
        this.mOldMaxMode = this.mMaxMode;
        this.mHighlightPathBogus = true;
        this.mHighlightPathsBogus = true;
        if (wantWidth >= 0) {
            wantWidth2 = wantWidth;
        } else {
            wantWidth2 = 0;
        }
        if (hintWidth >= 0) {
            hintWidth2 = hintWidth;
        } else {
            hintWidth2 = 0;
        }
        Layout.Alignment alignment3 = getLayoutAlignment();
        boolean testDirChange = this.mSingleLine && this.mLayout != null && (alignment3 == Layout.Alignment.ALIGN_NORMAL || alignment3 == Layout.Alignment.ALIGN_OPPOSITE);
        int oldDir3 = 0;
        if (testDirChange) {
            oldDir3 = this.mLayout.getParagraphDirection(0);
        }
        int oldDir4 = oldDir3;
        boolean shouldEllipsize = this.mEllipsize != null && getKeyListener() == null;
        boolean switchEllipsize = this.mEllipsize == TextUtils.TruncateAt.MARQUEE && this.mMarqueeFadeMode != 0;
        TextUtils.TruncateAt effectiveEllipsize2 = this.mEllipsize;
        if (this.mEllipsize == TextUtils.TruncateAt.MARQUEE && this.mMarqueeFadeMode == 1) {
            TextUtils.TruncateAt effectiveEllipsize3 = TextUtils.TruncateAt.END_SMALL;
            effectiveEllipsize = effectiveEllipsize3;
        } else {
            effectiveEllipsize = effectiveEllipsize2;
        }
        if (this.mTextDir == null) {
            this.mTextDir = getTextDirectionHeuristic();
        }
        this.mLayout = makeSingleLayout(wantWidth2, boring, ellipsisWidth, alignment3, shouldEllipsize, effectiveEllipsize, effectiveEllipsize == this.mEllipsize);
        if (!switchEllipsize) {
            oldDir = oldDir4;
            z = 0;
            alignment = alignment3;
            z2 = 1;
        } else {
            TextUtils.TruncateAt oppositeEllipsize = effectiveEllipsize == TextUtils.TruncateAt.MARQUEE ? TextUtils.TruncateAt.END : TextUtils.TruncateAt.MARQUEE;
            oldDir = oldDir4;
            z = 0;
            alignment = alignment3;
            z2 = 1;
            this.mSavedMarqueeModeLayout = makeSingleLayout(wantWidth2, boring, ellipsisWidth, alignment3, shouldEllipsize, oppositeEllipsize, effectiveEllipsize != this.mEllipsize);
        }
        boolean shouldEllipsize2 = this.mEllipsize != null ? z2 : z;
        this.mHintLayout = null;
        CharSequence charSequence = this.mHint;
        if (charSequence != null) {
            if (shouldEllipsize2) {
                hintWidth2 = wantWidth2;
            }
            int hintWidth3 = hintWidth2;
            if (hintBoring != UNKNOWN_BORING) {
                hintBoring2 = hintBoring;
            } else {
                BoringLayout.Metrics hintBoring3 = BoringLayout.isBoring(charSequence, this.mTextPaint, this.mTextDir, isFallbackLineSpacingForBoringLayout(), this.mHintBoring);
                if (hintBoring3 != null) {
                    this.mHintBoring = hintBoring3;
                }
                hintBoring2 = hintBoring3;
            }
            if (hintBoring2 == null) {
                alignment2 = alignment;
                i3 = z;
                oldDir2 = oldDir;
            } else {
                if (hintBoring2.width > hintWidth3) {
                    alignment2 = alignment;
                    i3 = z;
                    oldDir2 = oldDir;
                } else if (!shouldEllipsize2 || hintBoring2.width <= ellipsisWidth) {
                    BoringLayout boringLayout = this.mSavedHintLayout;
                    if (boringLayout != null) {
                        Layout.Alignment alignment4 = alignment;
                        oldDir2 = oldDir;
                        alignment2 = alignment4;
                        i3 = z;
                        this.mHintLayout = boringLayout.replaceOrMake(this.mHint, this.mTextPaint, hintWidth3, alignment4, this.mSpacingMult, this.mSpacingAdd, hintBoring2, this.mIncludePad);
                    } else {
                        alignment2 = alignment;
                        i3 = z;
                        oldDir2 = oldDir;
                        this.mHintLayout = BoringLayout.make(this.mHint, this.mTextPaint, hintWidth3, alignment2, this.mSpacingMult, this.mSpacingAdd, hintBoring2, this.mIncludePad);
                    }
                    this.mSavedHintLayout = (BoringLayout) this.mHintLayout;
                } else {
                    alignment2 = alignment;
                    i3 = z;
                    oldDir2 = oldDir;
                }
                if (shouldEllipsize2 && hintBoring2.width <= hintWidth3) {
                    BoringLayout boringLayout2 = this.mSavedHintLayout;
                    if (boringLayout2 != null) {
                        this.mHintLayout = boringLayout2.replaceOrMake(this.mHint, this.mTextPaint, hintWidth3, alignment2, this.mSpacingMult, this.mSpacingAdd, hintBoring2, this.mIncludePad, this.mEllipsize, ellipsisWidth);
                    } else {
                        this.mHintLayout = BoringLayout.make(this.mHint, this.mTextPaint, hintWidth3, alignment2, this.mSpacingMult, this.mSpacingAdd, hintBoring2, this.mIncludePad, this.mEllipsize, ellipsisWidth);
                    }
                }
            }
            if (this.mHintLayout == null) {
                boolean autoPhraseBreaking = (this.mUserSpeficiedLineBreakwordStyle || !FeatureFlagUtils.isEnabled(this.mContext, FeatureFlagUtils.SETTINGS_AUTO_TEXT_WRAPPING)) ? i3 : 1;
                CharSequence charSequence2 = this.mHint;
                z4 = true;
                StaticLayout.Builder builder = StaticLayout.Builder.obtain(charSequence2, i3, charSequence2.length(), this.mTextPaint, hintWidth3).setAlignment(alignment2).setTextDirection(this.mTextDir).setLineSpacing(this.mSpacingAdd, this.mSpacingMult).setIncludePad(this.mIncludePad).setUseLineSpacingFromFallbacks(isFallbackLineSpacingForStaticLayout()).setBreakStrategy(this.mBreakStrategy).setHyphenationFrequency(this.mHyphenationFrequency).setJustificationMode(this.mJustificationMode).setMaxLines(this.mMaxMode == 1 ? this.mMaximum : Integer.MAX_VALUE).setLineBreakConfig(LineBreakConfig.getLineBreakConfig(this.mLineBreakStyle, this.mLineBreakWordStyle, autoPhraseBreaking));
                if (!shouldEllipsize2) {
                    i2 = ellipsisWidth;
                    i = i3;
                } else {
                    i2 = ellipsisWidth;
                    i = i3;
                    builder.setEllipsize(this.mEllipsize).setEllipsizedWidth(i2);
                }
                this.mHintLayout = builder.build();
            } else {
                i2 = ellipsisWidth;
                i = i3;
                z4 = true;
            }
            z3 = z4;
        } else {
            i = z;
            oldDir2 = oldDir;
            i2 = ellipsisWidth;
            z3 = z2;
        }
        if (!bringIntoView) {
            if (testDirChange) {
            }
            if (this.mEllipsize == TextUtils.TruncateAt.MARQUEE && !compressText(i2)) {
                height = this.mLayoutParams.height;
                if (height == -2 && height != -1) {
                    startMarquee();
                } else {
                    this.mRestartMarquee = z3;
                }
            }
            editor = this.mEditor;
            if (editor == null) {
                editor.prepareCursorControllers();
                return;
            }
            return;
        }
        registerForPreDraw();
        if (this.mEllipsize == TextUtils.TruncateAt.MARQUEE) {
            height = this.mLayoutParams.height;
            if (height == -2) {
            }
            this.mRestartMarquee = z3;
        }
        editor = this.mEditor;
        if (editor == null) {
        }
    }

    public boolean useDynamicLayout() {
        return isTextSelectable() || (this.mSpannable != null && this.mPrecomputed == null);
    }

    protected Layout makeSingleLayout(int wantWidth, BoringLayout.Metrics boring, int ellipsisWidth, Layout.Alignment alignment, boolean shouldEllipsize, TextUtils.TruncateAt effectiveEllipsize, boolean useSaved) {
        BoringLayout.Metrics boring2;
        BoringLayout boringLayout;
        BoringLayout boringLayout2;
        Layout result = null;
        if (useDynamicLayout()) {
            result = DynamicLayout.Builder.obtain(this.mText, this.mTextPaint, wantWidth).setDisplayText(this.mTransformed).setAlignment(alignment).setTextDirection(this.mTextDir).setLineSpacing(this.mSpacingAdd, this.mSpacingMult).setIncludePad(this.mIncludePad).setUseLineSpacingFromFallbacks(isFallbackLineSpacingForStaticLayout()).setBreakStrategy(this.mBreakStrategy).setHyphenationFrequency(this.mHyphenationFrequency).setJustificationMode(this.mJustificationMode).setEllipsize(getKeyListener() == null ? effectiveEllipsize : null).setEllipsizedWidth(ellipsisWidth).build();
        } else {
            if (boring != UNKNOWN_BORING) {
                boring2 = boring;
            } else {
                BoringLayout.Metrics boring3 = BoringLayout.isBoring(this.mTransformed, this.mTextPaint, this.mTextDir, isFallbackLineSpacingForBoringLayout(), this.mBoring);
                if (boring3 != null) {
                    this.mBoring = boring3;
                }
                boring2 = boring3;
            }
            if (boring2 != null) {
                if (boring2.width <= wantWidth && (effectiveEllipsize == null || boring2.width <= ellipsisWidth)) {
                    result = (!useSaved || (boringLayout2 = this.mSavedLayout) == null) ? BoringLayout.make(this.mTransformed, this.mTextPaint, wantWidth, alignment, this.mSpacingMult, this.mSpacingAdd, boring2, this.mIncludePad) : boringLayout2.replaceOrMake(this.mTransformed, this.mTextPaint, wantWidth, alignment, this.mSpacingMult, this.mSpacingAdd, boring2, this.mIncludePad);
                    if (useSaved) {
                        this.mSavedLayout = (BoringLayout) result;
                    }
                } else if (shouldEllipsize && boring2.width <= wantWidth) {
                    result = (!useSaved || (boringLayout = this.mSavedLayout) == null) ? BoringLayout.make(this.mTransformed, this.mTextPaint, wantWidth, alignment, this.mSpacingMult, this.mSpacingAdd, boring2, this.mIncludePad, effectiveEllipsize, ellipsisWidth) : boringLayout.replaceOrMake(this.mTransformed, this.mTextPaint, wantWidth, alignment, this.mSpacingMult, this.mSpacingAdd, boring2, this.mIncludePad, effectiveEllipsize, ellipsisWidth);
                }
            }
        }
        if (result == null) {
            boolean autoPhraseBreaking = !this.mUserSpeficiedLineBreakwordStyle && FeatureFlagUtils.isEnabled(this.mContext, FeatureFlagUtils.SETTINGS_AUTO_TEXT_WRAPPING);
            CharSequence charSequence = this.mTransformed;
            StaticLayout.Builder builder = StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), this.mTextPaint, wantWidth).setAlignment(alignment).setTextDirection(this.mTextDir).setLineSpacing(this.mSpacingAdd, this.mSpacingMult).setIncludePad(this.mIncludePad).setUseLineSpacingFromFallbacks(isFallbackLineSpacingForStaticLayout()).setBreakStrategy(this.mBreakStrategy).setHyphenationFrequency(this.mHyphenationFrequency).setJustificationMode(this.mJustificationMode).setMaxLines(this.mMaxMode == 1 ? this.mMaximum : Integer.MAX_VALUE).setLineBreakConfig(LineBreakConfig.getLineBreakConfig(this.mLineBreakStyle, this.mLineBreakWordStyle, autoPhraseBreaking));
            if (shouldEllipsize) {
                builder.setEllipsize(effectiveEllipsize).setEllipsizedWidth(ellipsisWidth);
            }
            return builder.build();
        }
        return result;
    }

    private boolean compressText(float width) {
        if (!isHardwareAccelerated() && width > 0.0f && this.mLayout != null && getLineCount() == 1 && !this.mUserSetTextScaleX && this.mTextPaint.getTextScaleX() == 1.0f) {
            float textWidth = this.mLayout.getLineWidth(0);
            float overflow = ((textWidth + 1.0f) - width) / width;
            if (overflow > 0.0f && overflow <= 0.07f) {
                this.mTextPaint.setTextScaleX((1.0f - overflow) - 0.005f);
                post(new Runnable() { // from class: android.widget.TextView.2
                    @Override // java.lang.Runnable
                    public void run() {
                        TextView.this.requestLayout();
                    }
                });
                return true;
            }
        }
        return false;
    }

    private static int desired(Layout layout) {
        int n = layout.getLineCount();
        CharSequence text = layout.getText();
        float max = 0.0f;
        for (int i = 0; i < n - 1; i++) {
            if (text.charAt(layout.getLineEnd(i) - 1) != '\n') {
                return -1;
            }
        }
        for (int i2 = 0; i2 < n; i2++) {
            max = Math.max(max, layout.getLineMax(i2));
        }
        return (int) Math.ceil(max);
    }

    public void setIncludeFontPadding(boolean includepad) {
        if (this.mIncludePad != includepad) {
            this.mIncludePad = includepad;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public boolean getIncludeFontPadding() {
        return this.mIncludePad;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        BoringLayout.Metrics boring;
        int width2;
        boolean z;
        int width3;
        BoringLayout.Metrics hintBoring;
        int des;
        boolean fromexisting;
        int width4;
        int hintWidth;
        int unpaddedWidth;
        int widthMode;
        int hintWidth2;
        int hintWant;
        boolean z2;
        boolean layoutChanged;
        boolean maximumChanged;
        int desired;
        int widthMode2 = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        BoringLayout.Metrics boring2 = UNKNOWN_BORING;
        BoringLayout.Metrics hintBoring2 = UNKNOWN_BORING;
        if (this.mTextDir == null) {
            this.mTextDir = getTextDirectionHeuristic();
        }
        int des2 = -1;
        boolean fromexisting2 = false;
        float widthLimit = widthMode2 == Integer.MIN_VALUE ? widthSize : Float.MAX_VALUE;
        if (widthMode2 == 1073741824) {
            boring = boring2;
            hintBoring = hintBoring2;
            des = -1;
            fromexisting = false;
            width4 = widthSize;
            z = true;
        } else {
            Layout layout = this.mLayout;
            if (layout != null && this.mEllipsize == null) {
                des2 = desired(layout);
            }
            if (des2 < 0) {
                boring2 = BoringLayout.isBoring(this.mTransformed, this.mTextPaint, this.mTextDir, isFallbackLineSpacingForBoringLayout(), this.mBoring);
                if (boring2 != null) {
                    this.mBoring = boring2;
                }
            } else {
                fromexisting2 = true;
            }
            if (boring2 == null || boring2 == UNKNOWN_BORING) {
                if (des2 < 0) {
                    CharSequence charSequence = this.mTransformed;
                    des2 = (int) Math.ceil(Layout.getDesiredWidthWithLimit(charSequence, 0, charSequence.length(), this.mTextPaint, this.mTextDir, widthLimit));
                }
                width = des2;
            } else {
                width = boring2.width;
            }
            Drawables dr = this.mDrawables;
            if (dr != null) {
                width = Math.max(Math.max(width, dr.mDrawableWidthTop), dr.mDrawableWidthBottom);
            }
            if (this.mHint == null) {
                boring = boring2;
            } else {
                int hintDes = -1;
                Layout layout2 = this.mHintLayout;
                if (layout2 != null && this.mEllipsize == null) {
                    hintDes = desired(layout2);
                }
                if (hintDes >= 0) {
                    boring = boring2;
                } else {
                    boring = boring2;
                    hintBoring2 = BoringLayout.isBoring(this.mHint, this.mTextPaint, this.mTextDir, isFallbackLineSpacingForBoringLayout(), this.mHintBoring);
                    if (hintBoring2 != null) {
                        this.mHintBoring = hintBoring2;
                    }
                }
                if (hintBoring2 == null || hintBoring2 == UNKNOWN_BORING) {
                    if (hintDes < 0) {
                        CharSequence charSequence2 = this.mHint;
                        hintDes = (int) Math.ceil(Layout.getDesiredWidthWithLimit(charSequence2, 0, charSequence2.length(), this.mTextPaint, this.mTextDir, widthLimit));
                    }
                    hintWidth = hintDes;
                } else {
                    hintWidth = hintBoring2.width;
                }
                if (hintWidth > width) {
                    width = hintWidth;
                }
            }
            int width5 = width + getCompoundPaddingLeft() + getCompoundPaddingRight();
            if (this.mMaxWidthMode == 1) {
                width2 = Math.min(width5, this.mMaxWidth * getLineHeight());
            } else {
                int width6 = this.mMaxWidth;
                width2 = Math.min(width5, width6);
            }
            int width7 = this.mMinWidthMode;
            z = true;
            if (width7 == 1) {
                width3 = Math.max(width2, this.mMinWidth * getLineHeight());
            } else {
                width3 = Math.max(width2, this.mMinWidth);
            }
            int width8 = Math.max(width3, getSuggestedMinimumWidth());
            if (widthMode2 != Integer.MIN_VALUE) {
                hintBoring = hintBoring2;
                des = des2;
                fromexisting = fromexisting2;
                width4 = width8;
            } else {
                hintBoring = hintBoring2;
                des = des2;
                fromexisting = fromexisting2;
                width4 = Math.min(widthSize, width8);
            }
        }
        int want = (width4 - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (this.mHorizontallyScrolling) {
            want = 1048576;
        }
        int want2 = want;
        Layout layout3 = this.mHintLayout;
        int hintWidth3 = layout3 == null ? want2 : layout3.getWidth();
        Layout layout4 = this.mLayout;
        if (layout4 == null) {
            unpaddedWidth = want;
            widthMode = 1073741824;
            makeNewLayout(want2, want2, boring, hintBoring, (width4 - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
        } else {
            unpaddedWidth = want;
            widthMode = 1073741824;
            if (layout4.getWidth() == want2) {
                hintWidth2 = hintWidth3;
                hintWant = want2;
                if (hintWidth2 == hintWant && this.mLayout.getEllipsizedWidth() == (width4 - getCompoundPaddingLeft()) - getCompoundPaddingRight()) {
                    z2 = false;
                    layoutChanged = z2;
                    boolean widthChanged = this.mHint != null && this.mEllipsize == null && want2 > this.mLayout.getWidth() && ((this.mLayout instanceof BoringLayout) || (fromexisting && des >= 0 && des <= want2));
                    maximumChanged = this.mMaxMode == this.mOldMaxMode || this.mMaximum != this.mOldMaximum;
                    if (!layoutChanged || maximumChanged) {
                        if (maximumChanged && widthChanged) {
                            this.mLayout.increaseWidthTo(want2);
                        } else {
                            makeNewLayout(want2, hintWant, boring, hintBoring, (width4 - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
                        }
                    }
                }
            } else {
                hintWidth2 = hintWidth3;
                hintWant = want2;
            }
            z2 = true;
            layoutChanged = z2;
            boolean widthChanged2 = this.mHint != null && this.mEllipsize == null && want2 > this.mLayout.getWidth() && ((this.mLayout instanceof BoringLayout) || (fromexisting && des >= 0 && des <= want2));
            maximumChanged = this.mMaxMode == this.mOldMaxMode || this.mMaximum != this.mOldMaximum;
            if (!layoutChanged) {
            }
            if (maximumChanged) {
            }
            makeNewLayout(want2, hintWant, boring, hintBoring, (width4 - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
        }
        if (heightMode == widthMode) {
            desired = heightSize;
            this.mDesiredHeightAtMeasure = -1;
        } else {
            int desired2 = getDesiredHeight();
            this.mDesiredHeightAtMeasure = desired2;
            if (heightMode != Integer.MIN_VALUE) {
                desired = desired2;
            } else {
                int height = Math.min(desired2, heightSize);
                desired = height;
            }
        }
        int height2 = getCompoundPaddingTop();
        int unpaddedHeight = (desired - height2) - getCompoundPaddingBottom();
        if (this.mMaxMode == 1) {
            int lineCount = this.mLayout.getLineCount();
            int i = this.mMaximum;
            if (lineCount > i) {
                unpaddedHeight = Math.min(unpaddedHeight, this.mLayout.getLineTop(i));
            }
        }
        if (this.mMovement == null && this.mLayout.getWidth() <= unpaddedWidth && this.mLayout.getHeight() <= unpaddedHeight) {
            scrollTo(0, 0);
            setMeasuredDimension(width4, desired);
        }
        registerForPreDraw();
        setMeasuredDimension(width4, desired);
    }

    private void autoSizeText() {
        int availableWidth;
        if (!isAutoSizeEnabled()) {
            return;
        }
        if (this.mNeedsAutoSizeText) {
            if (getMeasuredWidth() <= 0 || getMeasuredHeight() <= 0) {
                return;
            }
            if (this.mHorizontallyScrolling) {
                availableWidth = 1048576;
            } else {
                availableWidth = (getMeasuredWidth() - getTotalPaddingLeft()) - getTotalPaddingRight();
            }
            int availableHeight = (getMeasuredHeight() - getExtendedPaddingBottom()) - getExtendedPaddingTop();
            if (availableWidth <= 0 || availableHeight <= 0) {
                return;
            }
            RectF rectF = TEMP_RECTF;
            synchronized (rectF) {
                rectF.setEmpty();
                rectF.right = availableWidth;
                rectF.bottom = availableHeight;
                float optimalTextSize = findLargestTextSizeWhichFits(rectF);
                if (optimalTextSize != getTextSize()) {
                    setTextSizeInternal(0, optimalTextSize, false);
                    BoringLayout.Metrics metrics = UNKNOWN_BORING;
                    makeNewLayout(availableWidth, 0, metrics, metrics, ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
                }
            }
        }
        this.mNeedsAutoSizeText = true;
    }

    private int findLargestTextSizeWhichFits(RectF availableSpace) {
        int sizesCount = this.mAutoSizeTextSizesInPx.length;
        if (sizesCount == 0) {
            throw new IllegalStateException("No available text sizes to choose from.");
        }
        int bestSizeIndex = 0;
        int lowIndex = 0 + 1;
        int highIndex = sizesCount - 1;
        while (lowIndex <= highIndex) {
            int sizeToTryIndex = (lowIndex + highIndex) / 2;
            if (suggestedSizeFitsInSpace(this.mAutoSizeTextSizesInPx[sizeToTryIndex], availableSpace)) {
                bestSizeIndex = lowIndex;
                lowIndex = sizeToTryIndex + 1;
            } else {
                highIndex = sizeToTryIndex - 1;
                bestSizeIndex = highIndex;
            }
        }
        return this.mAutoSizeTextSizesInPx[bestSizeIndex];
    }

    private boolean suggestedSizeFitsInSpace(int suggestedSizeInPx, RectF availableSpace) {
        CharSequence text = this.mTransformed;
        if (text == null) {
            text = getText();
        }
        int maxLines = getMaxLines();
        TextPaint textPaint = this.mTempTextPaint;
        if (textPaint == null) {
            this.mTempTextPaint = new TextPaint();
        } else {
            textPaint.reset();
        }
        this.mTempTextPaint.set(getPaint());
        this.mTempTextPaint.setTextSize(suggestedSizeInPx);
        StaticLayout.Builder layoutBuilder = StaticLayout.Builder.obtain(text, 0, text.length(), this.mTempTextPaint, Math.round(availableSpace.right));
        boolean autoPhraseBreaking = !this.mUserSpeficiedLineBreakwordStyle && FeatureFlagUtils.isEnabled(this.mContext, FeatureFlagUtils.SETTINGS_AUTO_TEXT_WRAPPING);
        layoutBuilder.setAlignment(getLayoutAlignment()).setLineSpacing(getLineSpacingExtra(), getLineSpacingMultiplier()).setIncludePad(getIncludeFontPadding()).setUseLineSpacingFromFallbacks(isFallbackLineSpacingForStaticLayout()).setBreakStrategy(getBreakStrategy()).setHyphenationFrequency(getHyphenationFrequency()).setJustificationMode(getJustificationMode()).setMaxLines(this.mMaxMode == 1 ? this.mMaximum : Integer.MAX_VALUE).setTextDirection(getTextDirectionHeuristic()).setLineBreakConfig(LineBreakConfig.getLineBreakConfig(this.mLineBreakStyle, this.mLineBreakWordStyle, autoPhraseBreaking));
        StaticLayout layout = layoutBuilder.build();
        return (maxLines == -1 || layout.getLineCount() <= maxLines) && ((float) layout.getHeight()) <= availableSpace.bottom;
    }

    private int getDesiredHeight() {
        return Math.max(getDesiredHeight(this.mLayout, true), getDesiredHeight(this.mHintLayout, this.mEllipsize != null));
    }

    private int getDesiredHeight(Layout layout, boolean cap) {
        int i;
        if (layout == null) {
            return 0;
        }
        int desired = layout.getHeight(cap);
        Drawables dr = this.mDrawables;
        if (dr != null) {
            desired = Math.max(Math.max(desired, dr.mDrawableHeightLeft), dr.mDrawableHeightRight);
        }
        int linecount = layout.getLineCount();
        int padding = getCompoundPaddingTop() + getCompoundPaddingBottom();
        int desired2 = desired + padding;
        if (this.mMaxMode != 1) {
            desired2 = Math.min(desired2, this.mMaximum);
        } else if (cap && linecount > (i = this.mMaximum) && ((layout instanceof DynamicLayout) || (layout instanceof BoringLayout))) {
            int desired3 = layout.getLineTop(i);
            if (dr != null) {
                desired3 = Math.max(Math.max(desired3, dr.mDrawableHeightLeft), dr.mDrawableHeightRight);
            }
            desired2 = desired3 + padding;
            linecount = this.mMaximum;
        }
        if (this.mMinMode == 1) {
            if (linecount < this.mMinimum) {
                desired2 += getLineHeight() * (this.mMinimum - linecount);
            }
        } else {
            desired2 = Math.max(desired2, this.mMinimum);
        }
        return Math.max(desired2, getSuggestedMinimumHeight());
    }

    private void checkForResize() {
        boolean sizeChanged = false;
        if (this.mLayout != null) {
            if (this.mLayoutParams.width == -2) {
                sizeChanged = true;
                invalidate();
            }
            if (this.mLayoutParams.height == -2) {
                int desiredHeight = getDesiredHeight();
                if (desiredHeight != getHeight()) {
                    sizeChanged = true;
                }
            } else if (this.mLayoutParams.height == -1 && this.mDesiredHeightAtMeasure >= 0) {
                int desiredHeight2 = getDesiredHeight();
                if (desiredHeight2 != this.mDesiredHeightAtMeasure) {
                    sizeChanged = true;
                }
            }
        }
        if (sizeChanged) {
            requestLayout();
        }
    }

    private void checkForRelayout() {
        Layout layout;
        if ((this.mLayoutParams.width != -2 || (this.mMaxWidthMode == this.mMinWidthMode && this.mMaxWidth == this.mMinWidth)) && ((this.mHint == null || this.mHintLayout != null) && ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight() > 0)) {
            int oldht = this.mLayout.getHeight();
            int want = this.mLayout.getWidth();
            Layout layout2 = this.mHintLayout;
            int hintWant = layout2 == null ? 0 : layout2.getWidth();
            BoringLayout.Metrics metrics = UNKNOWN_BORING;
            makeNewLayout(want, hintWant, metrics, metrics, ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
            if (this.mEllipsize != TextUtils.TruncateAt.MARQUEE) {
                if (this.mLayoutParams.height != -2 && this.mLayoutParams.height != -1) {
                    autoSizeText();
                    invalidate();
                    return;
                } else if (this.mLayout.getHeight() == oldht && ((layout = this.mHintLayout) == null || layout.getHeight() == oldht)) {
                    autoSizeText();
                    invalidate();
                    return;
                }
            }
            requestLayout();
            invalidate();
            return;
        }
        nullLayouts();
        requestLayout();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mDeferScroll >= 0) {
            int curs = this.mDeferScroll;
            this.mDeferScroll = -1;
            bringPointIntoView(Math.min(curs, this.mText.length()));
        }
        autoSizeText();
    }

    private boolean isShowingHint() {
        return TextUtils.isEmpty(this.mText) && !TextUtils.isEmpty(this.mHint);
    }

    private boolean bringTextIntoView() {
        int scrollx;
        int scrolly;
        Layout layout = isShowingHint() ? this.mHintLayout : this.mLayout;
        int line = 0;
        if ((this.mGravity & 112) == 80) {
            line = layout.getLineCount() - 1;
        }
        Layout.Alignment a = layout.getParagraphAlignment(line);
        int dir = layout.getParagraphDirection(line);
        int hspace = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int vspace = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
        int ht = layout.getHeight();
        if (a == Layout.Alignment.ALIGN_NORMAL) {
            a = dir == 1 ? Layout.Alignment.ALIGN_LEFT : Layout.Alignment.ALIGN_RIGHT;
        } else if (a == Layout.Alignment.ALIGN_OPPOSITE) {
            a = dir == 1 ? Layout.Alignment.ALIGN_RIGHT : Layout.Alignment.ALIGN_LEFT;
        }
        if (a == Layout.Alignment.ALIGN_CENTER) {
            int left = (int) Math.floor(layout.getLineLeft(line));
            int right = (int) Math.ceil(layout.getLineRight(line));
            if (right - left < hspace) {
                scrollx = ((right + left) / 2) - (hspace / 2);
            } else if (dir < 0) {
                scrollx = right - hspace;
            } else {
                scrollx = left;
            }
        } else if (a == Layout.Alignment.ALIGN_RIGHT) {
            scrollx = ((int) Math.ceil(layout.getLineRight(line))) - hspace;
        } else {
            scrollx = (int) Math.floor(layout.getLineLeft(line));
        }
        if (ht < vspace) {
            scrolly = 0;
        } else if ((this.mGravity & 112) == 80) {
            scrolly = ht - vspace;
        } else {
            scrolly = 0;
        }
        if (scrollx != this.mScrollX || scrolly != this.mScrollY) {
            scrollTo(scrollx, scrolly);
            return true;
        }
        return false;
    }

    public boolean bringPointIntoView(int offset) {
        return bringPointIntoView(offset, false);
    }

    public boolean bringPointIntoView(int offset, boolean requestRectWithoutFocus) {
        int grav;
        int vs;
        if (!isLayoutRequested()) {
            int offsetTransformed = originalToTransformed(offset, 1);
            Layout layout = isShowingHint() ? this.mHintLayout : this.mLayout;
            if (layout == null) {
                return false;
            }
            int line = layout.getLineForOffset(offsetTransformed);
            switch (C38844.$SwitchMap$android$text$Layout$Alignment[layout.getParagraphAlignment(line).ordinal()]) {
                case 1:
                    grav = 1;
                    break;
                case 2:
                    grav = -1;
                    break;
                case 3:
                    grav = layout.getParagraphDirection(line);
                    break;
                case 4:
                    int grav2 = layout.getParagraphDirection(line);
                    grav = -grav2;
                    break;
                default:
                    grav = 0;
                    break;
            }
            boolean clamped = grav > 0;
            int x = (int) layout.getPrimaryHorizontal(offsetTransformed, clamped);
            int top = layout.getLineTop(line);
            int bottom = layout.getLineTop(line + 1);
            int left = (int) Math.floor(layout.getLineLeft(line));
            int right = (int) Math.ceil(layout.getLineRight(line));
            int ht = layout.getHeight();
            int hspace = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
            int vspace = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
            if (!this.mHorizontallyScrolling && right - left > hspace && right > x) {
                right = Math.max(x, left + hspace);
            }
            int hslack = (bottom - top) / 2;
            int vslack = hslack;
            if (vslack > vspace / 4) {
                vslack = vspace / 4;
            }
            if (hslack > hspace / 4) {
                hslack = hspace / 4;
            }
            int hs = this.mScrollX;
            boolean changed = false;
            int vs2 = this.mScrollY;
            if (top - vs2 < vslack) {
                vs2 = top - vslack;
            }
            int vs3 = vs2;
            if (bottom - vs2 <= vspace - vslack) {
                vs = vs3;
            } else {
                vs = bottom - (vspace - vslack);
            }
            if (ht - vs < vspace) {
                vs = ht - vspace;
            }
            if (0 - vs > 0) {
                vs = 0;
            }
            if (grav != 0) {
                if (x - hs < hslack) {
                    hs = x - hslack;
                }
                int hs2 = hs;
                if (x - hs <= hspace - hslack) {
                    hs = hs2;
                } else {
                    hs = x - (hspace - hslack);
                }
            }
            if (grav < 0) {
                if (left - hs > 0) {
                    hs = left;
                }
                if (right - hs < hspace) {
                    hs = right - hspace;
                }
            } else if (grav > 0) {
                if (right - hs < hspace) {
                    hs = right - hspace;
                }
                if (left - hs > 0) {
                    hs = left;
                }
            } else if (right - left <= hspace) {
                hs = left - ((hspace - (right - left)) / 2);
            } else if (x > right - hslack) {
                hs = right - hspace;
            } else if (x < left + hslack) {
                hs = left;
            } else if (left > hs) {
                hs = left;
            } else if (right < hs + hspace) {
                hs = right - hspace;
            } else {
                if (x - hs < hslack) {
                    hs = x - hslack;
                }
                int hs3 = hs;
                if (x - hs <= hspace - hslack) {
                    hs = hs3;
                } else {
                    hs = x - (hspace - hslack);
                }
            }
            if (hs != this.mScrollX || vs != this.mScrollY) {
                if (this.mScroller != null) {
                    long duration = AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll;
                    int dx = hs - this.mScrollX;
                    int dy = vs - this.mScrollY;
                    if (duration > 250) {
                        this.mScroller.startScroll(this.mScrollX, this.mScrollY, dx, dy);
                        awakenScrollBars(this.mScroller.getDuration());
                        invalidate();
                    } else {
                        if (!this.mScroller.isFinished()) {
                            this.mScroller.abortAnimation();
                        }
                        scrollBy(dx, dy);
                    }
                    this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
                } else {
                    scrollTo(hs, vs);
                }
                changed = true;
            }
            if (requestRectWithoutFocus && isFocused()) {
                if (this.mTempRect == null) {
                    this.mTempRect = new Rect();
                }
                this.mTempRect.set(x - 2, top, x + 2, bottom);
                getInterestingRect(this.mTempRect, line);
                this.mTempRect.offset(this.mScrollX, this.mScrollY);
                if (requestRectangleOnScreen(this.mTempRect)) {
                    return true;
                }
                return changed;
            }
            return changed;
        }
        this.mDeferScroll = offset;
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.widget.TextView$4 */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class C38844 {
        static final /* synthetic */ int[] $SwitchMap$android$text$Layout$Alignment;

        static {
            int[] iArr = new int[Layout.Alignment.values().length];
            $SwitchMap$android$text$Layout$Alignment = iArr;
            try {
                iArr[Layout.Alignment.ALIGN_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Layout.Alignment.ALIGN_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Layout.Alignment.ALIGN_NORMAL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Layout.Alignment.ALIGN_OPPOSITE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Layout.Alignment.ALIGN_CENTER.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public boolean moveCursorToVisibleOffset() {
        if (this.mText instanceof Spannable) {
            int start = getSelectionStartTransformed();
            int end = getSelectionEndTransformed();
            if (start != end) {
                return false;
            }
            int line = this.mLayout.getLineForOffset(start);
            int top = this.mLayout.getLineTop(line);
            int bottom = this.mLayout.getLineTop(line + 1);
            int vspace = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
            int vslack = (bottom - top) / 2;
            if (vslack > vspace / 4) {
                vslack = vspace / 4;
            }
            int vs = this.mScrollY;
            if (top < vs + vslack) {
                line = this.mLayout.getLineForVertical(vs + vslack + (bottom - top));
            } else if (bottom > (vspace + vs) - vslack) {
                line = this.mLayout.getLineForVertical(((vspace + vs) - vslack) - (bottom - top));
            }
            int hspace = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
            int hs = this.mScrollX;
            int leftChar = this.mLayout.getOffsetForHorizontal(line, hs);
            int rightChar = this.mLayout.getOffsetForHorizontal(line, hspace + hs);
            int lowChar = leftChar < rightChar ? leftChar : rightChar;
            int highChar = leftChar > rightChar ? leftChar : rightChar;
            int newStart = start;
            if (newStart < lowChar) {
                newStart = lowChar;
            } else if (newStart > highChar) {
                newStart = highChar;
            }
            if (newStart != start) {
                Selection.setSelection(this.mSpannable, transformedToOriginal(newStart, 1));
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // android.view.View
    public void computeScroll() {
        Scroller scroller = this.mScroller;
        if (scroller != null && scroller.computeScrollOffset()) {
            this.mScrollX = this.mScroller.getCurrX();
            this.mScrollY = this.mScroller.getCurrY();
            invalidateParentCaches();
            postInvalidate();
        }
    }

    private void getInterestingRect(Rect r, int line) {
        convertFromViewportToContentCoordinates(r);
        if (line == 0) {
            r.top -= getExtendedPaddingTop();
        }
        if (line == this.mLayout.getLineCount() - 1) {
            r.bottom += getExtendedPaddingBottom();
        }
    }

    private void convertFromViewportToContentCoordinates(Rect r) {
        int horizontalOffset = viewportToContentHorizontalOffset();
        r.left += horizontalOffset;
        r.right += horizontalOffset;
        int verticalOffset = viewportToContentVerticalOffset();
        r.top += verticalOffset;
        r.bottom += verticalOffset;
    }

    private PointF convertFromScreenToContentCoordinates(PointF point) {
        int[] screenToViewport = getLocationOnScreen();
        PointF copy = new PointF(point);
        copy.offset(-(screenToViewport[0] + viewportToContentHorizontalOffset()), -(screenToViewport[1] + viewportToContentVerticalOffset()));
        return copy;
    }

    private RectF convertFromScreenToContentCoordinates(RectF rect) {
        int[] screenToViewport = getLocationOnScreen();
        RectF copy = new RectF(rect);
        copy.offset(-(screenToViewport[0] + viewportToContentHorizontalOffset()), -(screenToViewport[1] + viewportToContentVerticalOffset()));
        return copy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int viewportToContentHorizontalOffset() {
        return getCompoundPaddingLeft() - this.mScrollX;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int viewportToContentVerticalOffset() {
        int offset = getExtendedPaddingTop() - this.mScrollY;
        if ((this.mGravity & 112) != 48) {
            return offset + getVerticalOffset(false);
        }
        return offset;
    }

    @Override // android.view.View
    public void debug(int depth) {
        String output;
        super.debug(depth);
        String output2 = debugIndent(depth) + "frame={" + this.mLeft + ", " + this.mTop + ", " + this.mRight + ", " + this.mBottom + "} scroll={" + this.mScrollX + ", " + this.mScrollY + "} ";
        if (this.mText != null) {
            output = output2 + "mText=\"" + ((Object) this.mText) + "\" ";
            if (this.mLayout != null) {
                output = output + "mLayout width=" + this.mLayout.getWidth() + " height=" + this.mLayout.getHeight();
            }
        } else {
            output = output2 + "mText=NULL";
        }
        Log.m112d("View", output);
    }

    @ViewDebug.ExportedProperty(category = "text")
    public int getSelectionStart() {
        return Selection.getSelectionStart(getText());
    }

    @ViewDebug.ExportedProperty(category = "text")
    public int getSelectionEnd() {
        return Selection.getSelectionEnd(getText());
    }

    public void getSelection(int start, int end, Layout.SelectionRectangleConsumer consumer) {
        int transformedStart = originalToTransformed(start, 1);
        int transformedEnd = originalToTransformed(end, 1);
        this.mLayout.getSelection(transformedStart, transformedEnd, consumer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSelectionStartTransformed() {
        int start = getSelectionStart();
        return start < 0 ? start : originalToTransformed(start, 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSelectionEndTransformed() {
        int end = getSelectionEnd();
        return end < 0 ? end : originalToTransformed(end, 1);
    }

    public boolean hasSelection() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        return selectionStart >= 0 && selectionEnd > 0 && selectionStart != selectionEnd;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getSelectedText() {
        if (!hasSelection()) {
            return null;
        }
        int start = getSelectionStart();
        int end = getSelectionEnd();
        CharSequence charSequence = this.mText;
        return String.valueOf(start > end ? charSequence.subSequence(end, start) : charSequence.subSequence(start, end));
    }

    public void setSingleLine() {
        setSingleLine(true);
    }

    @RemotableViewMethod
    public void setAllCaps(boolean allCaps) {
        if (allCaps) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        } else {
            setTransformationMethod(null);
        }
    }

    public boolean isAllCaps() {
        TransformationMethod method = getTransformationMethod();
        return method != null && (method instanceof AllCapsTransformationMethod);
    }

    @RemotableViewMethod
    public void setSingleLine(boolean singleLine) {
        setInputTypeSingleLine(singleLine);
        applySingleLine(singleLine, true, true, true);
    }

    private void setInputTypeSingleLine(boolean singleLine) {
        Editor editor = this.mEditor;
        if (editor != null && (editor.mInputType & 15) == 1) {
            if (singleLine) {
                this.mEditor.mInputType &= -131073;
                return;
            }
            this.mEditor.mInputType |= 131072;
        }
    }

    private void applySingleLine(boolean singleLine, boolean applyTransformation, boolean changeMaxLines, boolean changeMaxLength) {
        InputFilter[] filters;
        this.mSingleLine = singleLine;
        if (singleLine) {
            setLines(1);
            setHorizontallyScrolling(true);
            if (applyTransformation) {
                setTransformationMethod(SingleLineTransformationMethod.getInstance());
            }
            if (changeMaxLength && this.mBufferType == BufferType.EDITABLE) {
                InputFilter[] prevFilters = getFilters();
                for (InputFilter filter : getFilters()) {
                    if (filter instanceof InputFilter.LengthFilter) {
                        return;
                    }
                }
                if (this.mSingleLineLengthFilter == null) {
                    this.mSingleLineLengthFilter = new InputFilter.LengthFilter(5000);
                }
                InputFilter[] newFilters = new InputFilter[prevFilters.length + 1];
                System.arraycopy(prevFilters, 0, newFilters, 0, prevFilters.length);
                newFilters[prevFilters.length] = this.mSingleLineLengthFilter;
                setFilters(newFilters);
                setText(getText());
                return;
            }
            return;
        }
        if (changeMaxLines) {
            setMaxLines(Integer.MAX_VALUE);
        }
        setHorizontallyScrolling(false);
        if (applyTransformation) {
            setTransformationMethod(null);
        }
        if (changeMaxLength && this.mBufferType == BufferType.EDITABLE) {
            InputFilter[] prevFilters2 = getFilters();
            if (prevFilters2.length == 0 || this.mSingleLineLengthFilter == null) {
                return;
            }
            int targetIndex = -1;
            int i = 0;
            while (true) {
                if (i < prevFilters2.length) {
                    if (prevFilters2[i] != this.mSingleLineLengthFilter) {
                        i++;
                    } else {
                        targetIndex = i;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (targetIndex == -1) {
                return;
            }
            if (prevFilters2.length == 1) {
                setFilters(NO_FILTERS);
                return;
            }
            InputFilter[] newFilters2 = new InputFilter[prevFilters2.length - 1];
            System.arraycopy(prevFilters2, 0, newFilters2, 0, targetIndex);
            System.arraycopy(prevFilters2, targetIndex + 1, newFilters2, targetIndex, (prevFilters2.length - targetIndex) - 1);
            setFilters(newFilters2);
            this.mSingleLineLengthFilter = null;
        }
    }

    public void setEllipsize(TextUtils.TruncateAt where) {
        if (this.mEllipsize != where) {
            this.mEllipsize = where;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setMarqueeRepeatLimit(int marqueeLimit) {
        this.mMarqueeRepeatLimit = marqueeLimit;
    }

    public int getMarqueeRepeatLimit() {
        return this.mMarqueeRepeatLimit;
    }

    @ViewDebug.ExportedProperty
    public TextUtils.TruncateAt getEllipsize() {
        return this.mEllipsize;
    }

    @RemotableViewMethod
    public void setSelectAllOnFocus(boolean selectAllOnFocus) {
        createEditorIfNeeded();
        this.mEditor.mSelectAllOnFocus = selectAllOnFocus;
        if (selectAllOnFocus) {
            CharSequence charSequence = this.mText;
            if (!(charSequence instanceof Spannable)) {
                setText(charSequence, BufferType.SPANNABLE);
            }
        }
    }

    @RemotableViewMethod
    public void setCursorVisible(boolean visible) {
        this.mCursorVisibleFromAttr = visible;
        updateCursorVisibleInternal();
    }

    public void setImeConsumesInput(boolean imeConsumesInput) {
        this.mImeIsConsumingInput = imeConsumesInput;
        updateCursorVisibleInternal();
    }

    private void updateCursorVisibleInternal() {
        boolean visible = this.mCursorVisibleFromAttr && !this.mImeIsConsumingInput;
        if (visible && this.mEditor == null) {
            return;
        }
        createEditorIfNeeded();
        if (this.mEditor.mCursorVisible != visible) {
            this.mEditor.mCursorVisible = visible;
            invalidate();
            this.mEditor.makeBlink();
            this.mEditor.prepareCursorControllers();
        }
    }

    public boolean isCursorVisible() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return true;
        }
        return editor.mCursorVisible;
    }

    public boolean isCursorVisibleFromAttr() {
        return this.mCursorVisibleFromAttr;
    }

    private boolean canMarquee() {
        Layout layout;
        int width = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (width > 0) {
            return this.mLayout.getLineWidth(0) > ((float) width) || !(this.mMarqueeFadeMode == 0 || (layout = this.mSavedMarqueeModeLayout) == null || layout.getLineWidth(0) <= ((float) width));
        }
        return false;
    }

    protected void startMarquee() {
        if (getKeyListener() != null || compressText((getWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight())) {
            return;
        }
        Marquee marquee = this.mMarquee;
        if ((marquee == null || marquee.isStopped()) && isAggregatedVisible()) {
            if ((isFocused() || isSelected()) && getLineCount() == 1 && canMarquee()) {
                if (this.mMarqueeFadeMode == 1) {
                    this.mMarqueeFadeMode = 2;
                    Layout tmp = this.mLayout;
                    this.mLayout = this.mSavedMarqueeModeLayout;
                    this.mSavedMarqueeModeLayout = tmp;
                    setHorizontalFadingEdgeEnabled(true);
                    requestLayout();
                    invalidate();
                }
                if (this.mMarquee == null) {
                    this.mMarquee = new Marquee(this);
                }
                this.mMarquee.start(this.mMarqueeRepeatLimit);
            }
        }
    }

    protected void stopMarquee() {
        Marquee marquee = this.mMarquee;
        if (marquee != null && !marquee.isStopped()) {
            this.mMarquee.stop();
        }
        if (this.mMarqueeFadeMode == 2) {
            this.mMarqueeFadeMode = 1;
            Layout tmp = this.mSavedMarqueeModeLayout;
            this.mSavedMarqueeModeLayout = this.mLayout;
            this.mLayout = tmp;
            setHorizontalFadingEdgeEnabled(false);
            requestLayout();
            invalidate();
        }
    }

    private void startStopMarquee(boolean start) {
        if (this.mEllipsize == TextUtils.TruncateAt.MARQUEE) {
            if (start) {
                startMarquee();
            } else {
                stopMarquee();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
    }

    protected void onSelectionChanged(int selStart, int selEnd) {
        sendAccessibilityEvent(8192);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(watcher);
    }

    public void removeTextChangedListener(TextWatcher watcher) {
        int i;
        ArrayList<TextWatcher> arrayList = this.mListeners;
        if (arrayList != null && (i = arrayList.indexOf(watcher)) >= 0) {
            this.mListeners.remove(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBeforeTextChanged(CharSequence text, int start, int before, int after) {
        if (this.mListeners != null) {
            ArrayList<TextWatcher> list = this.mListeners;
            int count = list.size();
            for (int i = 0; i < count; i++) {
                list.get(i).beforeTextChanged(text, start, before, after);
            }
        }
        removeIntersectingNonAdjacentSpans(start, start + before, SpellCheckSpan.class);
        removeIntersectingNonAdjacentSpans(start, start + before, SuggestionSpan.class);
    }

    private <T> void removeIntersectingNonAdjacentSpans(int start, int end, Class<T> type) {
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Editable) {
            Editable text = (Editable) charSequence;
            Object[] spans = text.getSpans(start, end, type);
            ArrayList arrayList = new ArrayList();
            for (Object obj : spans) {
                int spanStart = text.getSpanStart(obj);
                int spanEnd = text.getSpanEnd(obj);
                if (spanEnd != start && spanStart != end) {
                    arrayList.add(obj);
                }
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                text.removeSpan(it.next());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeAdjacentSuggestionSpans(int pos) {
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Editable) {
            Editable text = (Editable) charSequence;
            SuggestionSpan[] spans = (SuggestionSpan[]) text.getSpans(pos, pos, SuggestionSpan.class);
            int length = spans.length;
            for (int i = 0; i < length; i++) {
                int spanStart = text.getSpanStart(spans[i]);
                int spanEnd = text.getSpanEnd(spans[i]);
                if ((spanEnd == pos || spanStart == pos) && SpellChecker.haveWordBoundariesChanged(text, pos, pos, spanStart, spanEnd)) {
                    text.removeSpan(spans[i]);
                }
            }
        }
    }

    void sendOnTextChanged(CharSequence text, int start, int before, int after) {
        if (this.mListeners != null) {
            ArrayList<TextWatcher> list = this.mListeners;
            int count = list.size();
            for (int i = 0; i < count; i++) {
                list.get(i).onTextChanged(text, start, before, after);
            }
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.sendOnTextChanged(start, before, after);
        }
    }

    void sendAfterTextChanged(Editable text) {
        if (this.mListeners != null) {
            ArrayList<TextWatcher> list = this.mListeners;
            int count = list.size();
            for (int i = 0; i < count; i++) {
                list.get(i).afterTextChanged(text);
            }
        }
        notifyListeningManagersAfterTextChanged();
        hideErrorIfUnchanged();
    }

    private void notifyListeningManagersAfterTextChanged() {
        AutofillManager afm;
        if (isAutofillable() && (afm = (AutofillManager) this.mContext.getSystemService(AutofillManager.class)) != null) {
            if (Helper.sVerbose) {
                Log.m106v(LOG_TAG, "notifyAutoFillManagerAfterTextChanged");
            }
            afm.notifyValueChanged(this);
        }
        notifyContentCaptureTextChanged();
    }

    public void notifyContentCaptureTextChanged() {
        ContentCaptureManager cm;
        ContentCaptureSession session;
        if (isLaidOut() && isImportantForContentCapture() && getNotifiedContentCaptureAppeared() && (cm = (ContentCaptureManager) this.mContext.getSystemService(ContentCaptureManager.class)) != null && cm.isContentCaptureEnabled() && (session = getContentCaptureSession()) != null) {
            session.notifyViewTextChanged(getAutofillId(), getText());
        }
    }

    private boolean isAutofillable() {
        return getAutofillType() != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateAfterEdit() {
        invalidate();
        int curs = getSelectionStart();
        if (curs >= 0 || (this.mGravity & 112) == 80) {
            registerForPreDraw();
        }
        checkForResize();
        if (curs >= 0) {
            this.mHighlightPathBogus = true;
            Editor editor = this.mEditor;
            if (editor != null) {
                editor.makeBlink();
            }
            bringPointIntoView(curs);
        }
    }

    void handleTextChanged(CharSequence buffer, int start, int before, int after) {
        sLastCutCopyOrTextChangedTime = 0L;
        Editor editor = this.mEditor;
        Editor.InputMethodState ims = editor == null ? null : editor.mInputMethodState;
        if (ims == null || ims.mBatchEditNesting == 0) {
            updateAfterEdit();
        }
        if (ims != null) {
            ims.mContentChanged = true;
            if (ims.mChangedStart < 0) {
                ims.mChangedStart = start;
                ims.mChangedEnd = start + before;
            } else {
                ims.mChangedStart = Math.min(ims.mChangedStart, start);
                ims.mChangedEnd = Math.max(ims.mChangedEnd, (start + before) - ims.mChangedDelta);
            }
            ims.mChangedDelta += after - before;
        }
        resetErrorChangedFlag();
        sendOnTextChanged(buffer, start, before, after);
        onTextChanged(buffer, start, before, after);
        clearGesturePreviewHighlight();
    }

    void spanChange(Spanned buf, Object what, int oldStart, int newStart, int oldEnd, int newEnd) {
        boolean selChanged = false;
        int newSelStart = -1;
        int newSelEnd = -1;
        Editor editor = this.mEditor;
        Editor.InputMethodState ims = editor == null ? null : editor.mInputMethodState;
        if (what == Selection.SELECTION_END) {
            selChanged = true;
            newSelEnd = newStart;
            if (oldStart >= 0 || newStart >= 0) {
                invalidateCursor(Selection.getSelectionStart(buf), oldStart, newStart);
                checkForResize();
                registerForPreDraw();
                Editor editor2 = this.mEditor;
                if (editor2 != null) {
                    editor2.makeBlink();
                }
            }
        }
        if (what == Selection.SELECTION_START) {
            selChanged = true;
            newSelStart = newStart;
            if (oldStart >= 0 || newStart >= 0) {
                int end = Selection.getSelectionEnd(buf);
                invalidateCursor(end, oldStart, newStart);
            }
        }
        if (selChanged) {
            clearGesturePreviewHighlight();
            this.mHighlightPathBogus = true;
            if (this.mEditor != null && !isFocused()) {
                this.mEditor.mSelectionMoved = true;
            }
            if ((buf.getSpanFlags(what) & 512) == 0) {
                if (newSelStart < 0) {
                    newSelStart = Selection.getSelectionStart(buf);
                }
                if (newSelEnd < 0) {
                    newSelEnd = Selection.getSelectionEnd(buf);
                }
                Editor editor3 = this.mEditor;
                if (editor3 != null) {
                    editor3.refreshTextActionMode();
                    if (!hasSelection() && this.mEditor.getTextActionMode() == null && hasTransientState()) {
                        setHasTransientState(false);
                    }
                }
                onSelectionChanged(newSelStart, newSelEnd);
            }
        }
        if ((what instanceof UpdateAppearance) || (what instanceof ParagraphStyle) || (what instanceof CharacterStyle)) {
            if (ims == null || ims.mBatchEditNesting == 0) {
                invalidate();
                this.mHighlightPathBogus = true;
                checkForResize();
            } else {
                ims.mContentChanged = true;
            }
            Editor editor4 = this.mEditor;
            if (editor4 != null) {
                if (oldStart >= 0) {
                    editor4.invalidateTextDisplayList(this.mLayout, oldStart, oldEnd);
                }
                if (newStart >= 0) {
                    this.mEditor.invalidateTextDisplayList(this.mLayout, newStart, newEnd);
                }
                this.mEditor.invalidateHandlesAndActionMode();
            }
        }
        if (MetaKeyKeyListener.isMetaTracker(buf, what)) {
            this.mHighlightPathBogus = true;
            if (ims != null && MetaKeyKeyListener.isSelectingMetaTracker(buf, what)) {
                ims.mSelectionModeChanged = true;
            }
            if (Selection.getSelectionStart(buf) >= 0) {
                if (ims == null || ims.mBatchEditNesting == 0) {
                    invalidateCursor();
                } else {
                    ims.mCursorChanged = true;
                }
            }
        }
        if ((what instanceof ParcelableSpan) && ims != null && ims.mExtractedTextRequest != null) {
            if (ims.mBatchEditNesting != 0) {
                if (oldStart >= 0) {
                    if (ims.mChangedStart > oldStart) {
                        ims.mChangedStart = oldStart;
                    }
                    if (ims.mChangedStart > oldEnd) {
                        ims.mChangedStart = oldEnd;
                    }
                }
                if (newStart >= 0) {
                    if (ims.mChangedStart > newStart) {
                        ims.mChangedStart = newStart;
                    }
                    if (ims.mChangedStart > newEnd) {
                        ims.mChangedStart = newEnd;
                    }
                }
            } else {
                ims.mContentChanged = true;
            }
        }
        Editor editor5 = this.mEditor;
        if (editor5 != null && editor5.mSpellChecker != null && newStart < 0 && (what instanceof SpellCheckSpan)) {
            this.mEditor.mSpellChecker.onSpellCheckSpanRemoved((SpellCheckSpan) what);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        Spannable spannable;
        if (isTemporarilyDetached()) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
            return;
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onFocusChanged(focused, direction);
        }
        if (focused && (spannable = this.mSpannable) != null) {
            MetaKeyKeyListener.resetMetaState(spannable);
        }
        startStopMarquee(focused);
        TransformationMethod transformationMethod = this.mTransformation;
        if (transformationMethod != null) {
            transformationMethod.onFocusChanged(this, this.mText, focused, direction, previouslyFocusedRect);
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onWindowFocusChanged(hasWindowFocus);
        }
        startStopMarquee(hasWindowFocus);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Editor editor = this.mEditor;
        if (editor != null && visibility != 0) {
            editor.hideCursorAndSpanControllers();
            stopTextActionMode();
        }
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        startStopMarquee(isVisible);
    }

    public void clearComposingText() {
        if (this.mText instanceof Spannable) {
            BaseInputConnection.removeComposingSpans(this.mSpannable);
        }
    }

    @Override // android.view.View
    public void setSelected(boolean selected) {
        boolean wasSelected = isSelected();
        super.setSelected(selected);
        if (selected != wasSelected && this.mEllipsize == TextUtils.TruncateAt.MARQUEE) {
            if (selected) {
                startMarquee();
            } else {
                stopMarquee();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFromPrimePointer(MotionEvent event, boolean fromHandleView) {
        boolean res = true;
        int i = this.mPrimePointerId;
        boolean z = false;
        if (i == -1) {
            this.mPrimePointerId = event.getPointerId(0);
            this.mIsPrimePointerFromHandleView = fromHandleView;
        } else if (i != event.getPointerId(0)) {
            if (this.mIsPrimePointerFromHandleView && fromHandleView) {
                z = true;
            }
            res = z;
        }
        if (event.getActionMasked() == 1 || event.getActionMasked() == 3) {
            this.mPrimePointerId = -1;
        }
        return res;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        Editor editor;
        this.mLastInputSource = event.getSource();
        int action = event.getActionMasked();
        if (this.mEditor != null) {
            if (!isFromPrimePointer(event, false)) {
                return true;
            }
            this.mEditor.onTouchEvent(event);
            if (this.mEditor.mInsertionPointCursorController != null && this.mEditor.mInsertionPointCursorController.isCursorBeingModified()) {
                return true;
            }
            if (this.mEditor.mSelectionModifierCursorController != null && this.mEditor.mSelectionModifierCursorController.isDragAcceleratorActive()) {
                return true;
            }
        }
        boolean superResult = super.onTouchEvent(event);
        Editor editor2 = this.mEditor;
        if (editor2 != null && editor2.mDiscardNextActionUp && action == 1) {
            this.mEditor.mDiscardNextActionUp = false;
            if (this.mEditor.mIsInsertionActionModeStartPending) {
                this.mEditor.startInsertionActionMode();
                this.mEditor.mIsInsertionActionModeStartPending = false;
            }
            return superResult;
        }
        boolean touchIsFinished = action == 1 && ((editor = this.mEditor) == null || !editor.mIgnoreActionUpEvent) && isFocused();
        if ((this.mMovement != null || onCheckIsTextEditor()) && isEnabled() && (this.mText instanceof Spannable) && this.mLayout != null) {
            MovementMethod movementMethod = this.mMovement;
            boolean handled = movementMethod != null ? false | movementMethod.onTouchEvent(this, this.mSpannable, event) : false;
            boolean textIsSelectable = isTextSelectable();
            if (touchIsFinished && this.mLinksClickable && this.mAutoLinkMask != 0 && textIsSelectable) {
                ClickableSpan[] links = (ClickableSpan[]) this.mSpannable.getSpans(getSelectionStart(), getSelectionEnd(), ClickableSpan.class);
                if (links.length > 0) {
                    links[0].onClick(this);
                    handled = true;
                }
            }
            if (touchIsFinished && (isTextEditable() || textIsSelectable)) {
                InputMethodManager imm = getInputMethodManager();
                viewClicked(imm);
                if (isTextEditable() && this.mEditor.mShowSoftInputOnFocus && imm != null && !showAutofillDialog()) {
                    imm.showSoftInput(this, 0);
                }
                this.mEditor.onTouchUpEvent(event);
                handled = true;
            }
            if (handled) {
                return true;
            }
        }
        return superResult;
    }

    public final boolean showUIForTouchScreen() {
        return (this.mLastInputSource & 4098) == 4098;
    }

    private boolean showAutofillDialog() {
        AutofillManager afm = (AutofillManager) this.mContext.getSystemService(AutofillManager.class);
        if (afm != null) {
            return afm.showAutofillDialog(this);
        }
        return false;
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        MovementMethod movementMethod = this.mMovement;
        if (movementMethod != null && (this.mText instanceof Spannable) && this.mLayout != null) {
            try {
                if (movementMethod.onGenericMotionEvent(this, this.mSpannable, event)) {
                    return true;
                }
            } catch (AbstractMethodError e) {
            }
        }
        return super.onGenericMotionEvent(event);
    }

    @Override // android.view.View
    protected void onCreateContextMenu(ContextMenu menu) {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onCreateContextMenu(menu);
        }
    }

    @Override // android.view.View
    public boolean showContextMenu() {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.setContextMenuAnchor(Float.NaN, Float.NaN);
        }
        return super.showContextMenu();
    }

    @Override // android.view.View
    public boolean showContextMenu(float x, float y) {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.setContextMenuAnchor(x, y);
        }
        return super.showContextMenu(x, y);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTextEditable() {
        return (this.mText instanceof Editable) && onCheckIsTextEditor() && isEnabled();
    }

    public boolean didTouchFocusSelect() {
        Editor editor = this.mEditor;
        return editor != null && editor.mTouchFocusSelected;
    }

    @Override // android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.mIgnoreActionUpEvent = true;
        }
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent event) {
        Spannable spannable;
        MovementMethod movementMethod = this.mMovement;
        if (movementMethod != null && (spannable = this.mSpannable) != null && this.mLayout != null && movementMethod.onTrackballEvent(this, spannable, event)) {
            return true;
        }
        return super.onTrackballEvent(event);
    }

    public void setScroller(Scroller s) {
        this.mScroller = s;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public float getLeftFadingEdgeStrength() {
        Marquee marquee;
        if (isMarqueeFadeEnabled() && (marquee = this.mMarquee) != null && !marquee.isStopped()) {
            Marquee marquee2 = this.mMarquee;
            if (marquee2.shouldDrawLeftFade()) {
                return getHorizontalFadingEdgeStrength(marquee2.getScroll(), 0.0f);
            }
            return 0.0f;
        } else if (getLineCount() == 1) {
            float lineLeft = getLayout().getLineLeft(0);
            if (lineLeft > this.mScrollX) {
                return 0.0f;
            }
            return getHorizontalFadingEdgeStrength(this.mScrollX, lineLeft);
        } else {
            return super.getLeftFadingEdgeStrength();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public float getRightFadingEdgeStrength() {
        Marquee marquee;
        if (isMarqueeFadeEnabled() && (marquee = this.mMarquee) != null && !marquee.isStopped()) {
            Marquee marquee2 = this.mMarquee;
            return getHorizontalFadingEdgeStrength(marquee2.getMaxFadeScroll(), marquee2.getScroll());
        } else if (getLineCount() == 1) {
            float rightEdge = this.mScrollX + ((getWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight());
            float lineRight = getLayout().getLineRight(0);
            if (lineRight < rightEdge) {
                return 0.0f;
            }
            return getHorizontalFadingEdgeStrength(rightEdge, lineRight);
        } else {
            return super.getRightFadingEdgeStrength();
        }
    }

    private float getHorizontalFadingEdgeStrength(float position1, float position2) {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        if (horizontalFadingEdgeLength == 0) {
            return 0.0f;
        }
        float diff = Math.abs(position1 - position2);
        if (diff > horizontalFadingEdgeLength) {
            return 1.0f;
        }
        return diff / horizontalFadingEdgeLength;
    }

    private boolean isMarqueeFadeEnabled() {
        return this.mEllipsize == TextUtils.TruncateAt.MARQUEE && this.mMarqueeFadeMode != 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeHorizontalScrollRange() {
        Layout layout = this.mLayout;
        if (layout != null) {
            return (this.mSingleLine && (this.mGravity & 7) == 3) ? (int) layout.getLineWidth(0) : layout.getWidth();
        }
        return super.computeHorizontalScrollRange();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollRange() {
        Layout layout = this.mLayout;
        if (layout != null) {
            return layout.getHeight();
        }
        return super.computeVerticalScrollRange();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollExtent() {
        return (getHeight() - getCompoundPaddingTop()) - getCompoundPaddingBottom();
    }

    @Override // android.view.View
    public void findViewsWithText(ArrayList<View> outViews, CharSequence searched, int flags) {
        super.findViewsWithText(outViews, searched, flags);
        if (!outViews.contains(this) && (flags & 1) != 0 && !TextUtils.isEmpty(searched) && !TextUtils.isEmpty(this.mText)) {
            String searchedLowerCase = searched.toString().toLowerCase();
            String textLowerCase = this.mText.toString().toLowerCase();
            if (textLowerCase.contains(searchedLowerCase)) {
                outViews.add(this);
            }
        }
    }

    public static ColorStateList getTextColors(Context context, TypedArray attrs) {
        int ap;
        if (attrs == null) {
            throw new NullPointerException();
        }
        TypedArray a = context.obtainStyledAttributes(C0001R.styleable.TextView);
        ColorStateList colors = a.getColorStateList(5);
        if (colors == null && (ap = a.getResourceId(1, 0)) != 0) {
            TypedArray appearance = context.obtainStyledAttributes(ap, C0001R.styleable.TextAppearance);
            colors = appearance.getColorStateList(3);
            appearance.recycle();
        }
        a.recycle();
        return colors;
    }

    public static int getTextColor(Context context, TypedArray attrs, int def) {
        ColorStateList colors = getTextColors(context, attrs);
        if (colors == null) {
            return def;
        }
        return colors.getDefaultColor();
    }

    @Override // android.view.View
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        if (event.hasModifiers(4096)) {
            switch (keyCode) {
                case 29:
                    if (canSelectText()) {
                        return onTextContextMenuItem(16908319);
                    }
                    break;
                case 31:
                    if (canCopy()) {
                        return onTextContextMenuItem(16908321);
                    }
                    break;
                case 50:
                    if (canPaste()) {
                        return onTextContextMenuItem(16908322);
                    }
                    break;
                case 52:
                    if (canCut()) {
                        return onTextContextMenuItem(16908320);
                    }
                    break;
                case 53:
                    if (canRedo()) {
                        return onTextContextMenuItem(16908339);
                    }
                    break;
                case 54:
                    if (canUndo()) {
                        return onTextContextMenuItem(16908338);
                    }
                    break;
            }
        } else if (event.hasModifiers(4097)) {
            switch (keyCode) {
                case 50:
                    if (canPaste()) {
                        return onTextContextMenuItem(16908337);
                    }
                    break;
                case 54:
                    if (canRedo()) {
                        return onTextContextMenuItem(16908339);
                    }
                    break;
            }
        }
        return super.onKeyShortcut(keyCode, event);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canSelectText() {
        Editor editor;
        return (this.mText.length() == 0 || (editor = this.mEditor) == null || !editor.hasSelectionController()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean textCanBeSelected() {
        MovementMethod movementMethod = this.mMovement;
        if (movementMethod == null || !movementMethod.canSelectArbitrarily()) {
            return false;
        }
        return isTextEditable() || (isTextSelectable() && (this.mText instanceof Spannable) && isEnabled());
    }

    private Locale getTextServicesLocale(boolean allowNullLocale) {
        updateTextServicesLocaleAsync();
        return (this.mCurrentSpellCheckerLocaleCache != null || allowNullLocale) ? this.mCurrentSpellCheckerLocaleCache : Locale.getDefault();
    }

    public final void setTextOperationUser(UserHandle user) {
        if (Objects.equals(this.mTextOperationUser, user)) {
            return;
        }
        if (user != null && !Process.myUserHandle().equals(user) && getContext().checkSelfPermission(Manifest.C0000permission.INTERACT_ACROSS_USERS_FULL) != 0) {
            throw new SecurityException("INTERACT_ACROSS_USERS_FULL is required. userId=" + user.getIdentifier() + " callingUserId" + UserHandle.myUserId());
        }
        this.mTextOperationUser = user;
        this.mCurrentSpellCheckerLocaleCache = null;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onTextOperationUserChanged();
        }
    }

    @Override // android.view.View
    public boolean isAutoHandwritingEnabled() {
        return super.isAutoHandwritingEnabled() && !isAnyPasswordInputType();
    }

    @Override // android.view.View
    public boolean isStylusHandwritingAvailable() {
        UserHandle userHandle = this.mTextOperationUser;
        if (userHandle == null) {
            return super.isStylusHandwritingAvailable();
        }
        int userId = userHandle.getIdentifier();
        InputMethodManager imm = getInputMethodManager();
        return imm.isStylusHandwritingAvailableAsUser(userId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final TextServicesManager getTextServicesManagerForUser() {
        return (TextServicesManager) getServiceManagerForUser("android", TextServicesManager.class);
    }

    final ClipboardManager getClipboardManagerForUser() {
        return (ClipboardManager) getServiceManagerForUser(getContext().getPackageName(), ClipboardManager.class);
    }

    final TextClassificationManager getTextClassificationManagerForUser() {
        return (TextClassificationManager) getServiceManagerForUser(getContext().getPackageName(), TextClassificationManager.class);
    }

    final <T> T getServiceManagerForUser(String packageName, Class<T> managerClazz) {
        if (this.mTextOperationUser == null) {
            return (T) getContext().getSystemService(managerClazz);
        }
        try {
            Context context = getContext().createPackageContextAsUser(packageName, 0, this.mTextOperationUser);
            return (T) context.getSystemService(managerClazz);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startActivityAsTextOperationUserIfNecessary(Intent intent) {
        if (this.mTextOperationUser != null) {
            getContext().startActivityAsUser(intent, this.mTextOperationUser);
        } else {
            getContext().startActivity(intent);
        }
    }

    public Locale getTextServicesLocale() {
        return getTextServicesLocale(false);
    }

    public boolean isInExtractedMode() {
        return false;
    }

    private boolean isAutoSizeEnabled() {
        return supportsAutoSizeText() && this.mAutoSizeTextType != 0;
    }

    protected boolean supportsAutoSizeText() {
        return true;
    }

    public Locale getSpellCheckerLocale() {
        return getTextServicesLocale(true);
    }

    private void updateTextServicesLocaleAsync() {
        AsyncTask.execute(new Runnable() { // from class: android.widget.TextView.3
            @Override // java.lang.Runnable
            public void run() {
                TextView.this.updateTextServicesLocaleLocked();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTextServicesLocaleLocked() {
        Locale locale;
        TextServicesManager textServicesManager = getTextServicesManagerForUser();
        if (textServicesManager == null) {
            return;
        }
        SpellCheckerSubtype subtype = textServicesManager.getCurrentSpellCheckerSubtype(true);
        if (subtype != null) {
            locale = subtype.getLocaleObject();
        } else {
            locale = null;
        }
        this.mCurrentSpellCheckerLocaleCache = locale;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onLocaleChanged() {
        this.mEditor.onLocaleChanged();
    }

    public WordIterator getWordIterator() {
        Editor editor = this.mEditor;
        if (editor != null) {
            return editor.getWordIterator();
        }
        return null;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEventInternal(AccessibilityEvent event) {
        super.onPopulateAccessibilityEventInternal(event);
        if (isAccessibilityDataSensitive() && !event.isAccessibilityDataSensitive()) {
            return;
        }
        CharSequence text = getTextForAccessibility();
        if (!TextUtils.isEmpty(text)) {
            event.getText().add(text);
        }
    }

    @Override // android.view.View
    public CharSequence getAccessibilityClassName() {
        return TextView.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x0151, code lost:
        if (r11 >= r12.length()) goto L94;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onProvideStructure(ViewStructure structure, int viewFor, int flags) {
        Editor editor;
        int topLine;
        int bottomLine;
        int expandedTopLine;
        int expandedBottomChar;
        int i;
        super.onProvideStructure(structure, viewFor, flags);
        boolean isPassword = hasPasswordTransformationMethod() || isPasswordInputType(getInputType());
        if (viewFor == 1 || viewFor == 2) {
            if (viewFor == 1) {
                structure.setDataIsSensitive(!this.mTextSetFromXmlOrResourceId);
            }
            if (this.mTextId != 0) {
                try {
                    structure.setTextIdEntry(getResources().getResourceEntryName(this.mTextId));
                } catch (Resources.NotFoundException e) {
                    if (Helper.sVerbose) {
                        Log.m106v(LOG_TAG, "onProvideAutofillStructure(): cannot set name for text id " + this.mTextId + ": " + e.getMessage());
                    }
                }
            }
            String[] mimeTypes = getReceiveContentMimeTypes();
            if (mimeTypes == null && (editor = this.mEditor) != null) {
                mimeTypes = editor.getDefaultOnReceiveContentListener().getFallbackMimeTypesForAutofill(this);
            }
            structure.setReceiveContentMimeTypes(mimeTypes);
        }
        if (!isPassword || viewFor == 1 || viewFor == 2) {
            if (this.mLayout == null) {
                if (viewFor == 2) {
                    Log.m104w(LOG_TAG, "onProvideContentCaptureStructure(): calling assumeLayout()");
                }
                assumeLayout();
            }
            Layout layout = this.mLayout;
            int lineCount = layout.getLineCount();
            if (lineCount <= 1) {
                CharSequence text = getText();
                if (viewFor == 1) {
                    structure.setText(text);
                } else {
                    structure.setText(text, getSelectionStart(), getSelectionEnd());
                }
                expandedBottomChar = 0;
            } else {
                int[] tmpCords = new int[2];
                getLocationInWindow(tmpCords);
                int topWindowLocation = tmpCords[1];
                View root = this;
                ViewParent viewParent = getParent();
                while (viewParent instanceof View) {
                    root = (View) viewParent;
                    viewParent = root.getParent();
                }
                int windowHeight = root.getHeight();
                if (topWindowLocation >= 0) {
                    topLine = getLineAtCoordinateUnclamped(0.0f);
                    bottomLine = getLineAtCoordinateUnclamped(windowHeight - 1);
                } else {
                    int bottomLine2 = -topWindowLocation;
                    topLine = getLineAtCoordinateUnclamped(bottomLine2);
                    bottomLine = getLineAtCoordinateUnclamped((windowHeight - 1) - topWindowLocation);
                }
                int expandedTopLine2 = topLine - ((bottomLine - topLine) / 2);
                if (expandedTopLine2 >= 0) {
                    expandedTopLine = expandedTopLine2;
                } else {
                    expandedTopLine = 0;
                }
                int expandedBottomLine = bottomLine + ((bottomLine - topLine) / 2);
                if (expandedBottomLine >= lineCount) {
                    expandedBottomLine = lineCount - 1;
                }
                int expandedTopChar = transformedToOriginal(layout.getLineStart(expandedTopLine), 0);
                int expandedBottomChar2 = transformedToOriginal(layout.getLineEnd(expandedBottomLine), 0);
                int selStart = getSelectionStart();
                int selEnd = getSelectionEnd();
                if (selStart < selEnd) {
                    if (selStart < expandedTopChar) {
                        expandedTopChar = selStart;
                    }
                    if (selEnd > expandedBottomChar2) {
                        expandedBottomChar2 = selEnd;
                    }
                }
                CharSequence text2 = getText();
                if (text2 != null) {
                    if (expandedTopChar > 0) {
                    }
                    expandedTopChar = Math.min(expandedTopChar, text2.length());
                    expandedBottomChar2 = Math.min(expandedBottomChar2, text2.length());
                    text2 = text2.subSequence(expandedTopChar, expandedBottomChar2);
                    if (viewFor == 1) {
                        structure.setText(text2);
                        expandedBottomChar = 0;
                    } else {
                        structure.setText(text2, selStart - expandedTopChar, selEnd - expandedTopChar);
                        int[] lineOffsets = new int[(bottomLine - topLine) + 1];
                        int[] lineBaselines = new int[(bottomLine - topLine) + 1];
                        int baselineOffset = getBaselineOffset();
                        int expandedTopChar2 = topLine;
                        while (expandedTopChar2 <= bottomLine) {
                            int bottomLine3 = bottomLine;
                            int bottomLine4 = layout.getLineStart(expandedTopChar2);
                            lineOffsets[expandedTopChar2 - topLine] = transformedToOriginal(bottomLine4, 0);
                            lineBaselines[expandedTopChar2 - topLine] = layout.getLineBaseline(expandedTopChar2) + baselineOffset;
                            expandedTopChar2++;
                            bottomLine = bottomLine3;
                            expandedBottomChar2 = expandedBottomChar2;
                        }
                        expandedBottomChar = 0;
                        structure.setTextLines(lineOffsets, lineBaselines);
                    }
                } else {
                    expandedBottomChar = 0;
                }
            }
            if (viewFor == 0 || viewFor == 2) {
                int style = 0;
                int typefaceStyle = getTypefaceStyle();
                if ((typefaceStyle & 1) != 0) {
                    style = 0 | 1;
                }
                if ((typefaceStyle & 2) != 0) {
                    style |= 2;
                }
                int paintFlags = this.mTextPaint.getFlags();
                if ((paintFlags & 32) != 0) {
                    style |= 1;
                }
                if ((paintFlags & 8) != 0) {
                    style |= 4;
                }
                if ((paintFlags & 16) != 0) {
                    style |= 8;
                }
                i = 1;
                structure.setTextStyle(getTextSize(), getCurrentTextColor(), 1, style);
            } else {
                i = 1;
            }
            if (viewFor == i || viewFor == 2) {
                structure.setMinTextEms(getMinEms());
                structure.setMaxTextEms(getMaxEms());
                int maxLength = -1;
                InputFilter[] filters = getFilters();
                int length = filters.length;
                while (true) {
                    if (expandedBottomChar >= length) {
                        break;
                    }
                    InputFilter filter = filters[expandedBottomChar];
                    if (!(filter instanceof InputFilter.LengthFilter)) {
                        expandedBottomChar++;
                    } else {
                        maxLength = ((InputFilter.LengthFilter) filter).getMax();
                        break;
                    }
                }
                structure.setMaxTextLength(maxLength);
            }
        }
        if (this.mHintId != 0) {
            try {
                structure.setHintIdEntry(getResources().getResourceEntryName(this.mHintId));
            } catch (Resources.NotFoundException e2) {
                if (Helper.sVerbose) {
                    Log.m106v(LOG_TAG, "onProvideAutofillStructure(): cannot set name for hint id " + this.mHintId + ": " + e2.getMessage());
                }
            }
        }
        structure.setHint(getHint());
        structure.setInputType(getInputType());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canRequestAutofill() {
        AutofillManager afm;
        if (isAutofillable() && (afm = (AutofillManager) this.mContext.getSystemService(AutofillManager.class)) != null) {
            return afm.isEnabled();
        }
        return false;
    }

    private void requestAutofill() {
        AutofillManager afm = (AutofillManager) this.mContext.getSystemService(AutofillManager.class);
        if (afm != null) {
            afm.requestAutofill(this);
        }
    }

    @Override // android.view.View
    public void autofill(AutofillValue value) {
        if (!isTextEditable()) {
            Log.m104w(LOG_TAG, "cannot autofill non-editable TextView: " + this);
        } else if (!value.isText()) {
            Log.m104w(LOG_TAG, "value of type " + value.describeContents() + " cannot be autofilled into " + this);
        } else {
            ClipData clip = ClipData.newPlainText("", value.getTextValue());
            ContentInfo payload = new ContentInfo.Builder(clip, 4).build();
            performReceiveContent(payload);
        }
    }

    @Override // android.view.View
    public int getAutofillType() {
        return isTextEditable() ? 1 : 0;
    }

    @Override // android.view.View
    public AutofillValue getAutofillValue() {
        if (isTextEditable()) {
            CharSequence text = TextUtils.trimToParcelableSize(getText());
            return AutofillValue.forText(text);
        }
        return null;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEventInternal(AccessibilityEvent event) {
        super.onInitializeAccessibilityEventInternal(event);
        boolean isPassword = hasPasswordTransformationMethod();
        event.setPassword(isPassword);
        if (event.getEventType() == 8192) {
            event.setFromIndex(Selection.getSelectionStart(this.mText));
            event.setToIndex(Selection.getSelectionEnd(this.mText));
            event.setItemCount(this.mText.length());
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        boolean isPassword = hasPasswordTransformationMethod();
        info.setPassword(isPassword);
        info.setText(getTextForAccessibility());
        info.setHintText(this.mHint);
        info.setShowingHintText(isShowingHint());
        if (this.mBufferType == BufferType.EDITABLE) {
            info.setEditable(true);
            if (isEnabled()) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT);
            }
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            info.setInputType(editor.mInputType);
            if (this.mEditor.mError != null) {
                info.setContentInvalid(true);
                info.setError(this.mEditor.mError);
            }
            if (isTextEditable() && isFocused()) {
                CharSequence imeActionLabel = this.mContext.getResources().getString(C4057R.string.keyboardview_keycode_enter);
                if (getImeActionLabel() != null) {
                    imeActionLabel = getImeActionLabel();
                }
                AccessibilityNodeInfo.AccessibilityAction action = new AccessibilityNodeInfo.AccessibilityAction(16908372, imeActionLabel);
                info.addAction(action);
            }
        }
        CharSequence imeActionLabel2 = this.mText;
        if (!TextUtils.isEmpty(imeActionLabel2)) {
            info.addAction(256);
            info.addAction(512);
            info.setMovementGranularities(31);
            info.addAction(131072);
            info.setAvailableExtraData(Arrays.asList(AccessibilityNodeInfo.EXTRA_DATA_RENDERING_INFO_KEY, AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY));
            info.setTextSelectable(isTextSelectable() || isTextEditable());
        } else {
            info.setAvailableExtraData(Arrays.asList(AccessibilityNodeInfo.EXTRA_DATA_RENDERING_INFO_KEY));
        }
        if (isFocused()) {
            if (canCopy()) {
                info.addAction(16384);
            }
            if (canPaste()) {
                info.addAction(32768);
            }
            if (canCut()) {
                info.addAction(65536);
            }
            if (canReplace()) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_TEXT_SUGGESTIONS);
            }
            if (canShare()) {
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(268435456, getResources().getString(C4057R.string.share)));
            }
            if (canProcessText()) {
                this.mEditor.mProcessTextIntentActionsHandler.onInitializeAccessibilityNodeInfo(info);
                this.mEditor.onInitializeSmartActionsAccessibilityNodeInfo(info);
            }
        }
        int numFilters = this.mFilters.length;
        for (int i = 0; i < numFilters; i++) {
            InputFilter filter = this.mFilters[i];
            if (filter instanceof InputFilter.LengthFilter) {
                info.setMaxTextLength(((InputFilter.LengthFilter) filter).getMax());
            }
        }
        if (!isSingleLine()) {
            info.setMultiLine(true);
        }
        if (info.isClickable() || info.isLongClickable()) {
            if ((this.mMovement instanceof LinkMovementMethod) || (isTextSelectable() && !isTextEditable())) {
                if (!hasOnClickListeners()) {
                    info.setClickable(false);
                    info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                }
                if (!hasOnLongClickListeners()) {
                    info.setLongClickable(false);
                    info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
                }
            }
        }
    }

    @Override // android.view.View
    public void addExtraDataToAccessibilityNodeInfo(AccessibilityNodeInfo info, String extraDataKey, Bundle arguments) {
        RectF bounds;
        if (arguments != null && extraDataKey.equals(AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY)) {
            int positionInfoStartIndex = arguments.getInt(AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX, -1);
            int positionInfoLength = arguments.getInt(AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH, -1);
            if (positionInfoLength <= 0 || positionInfoStartIndex < 0 || positionInfoStartIndex >= this.mText.length()) {
                Log.m110e(LOG_TAG, "Invalid arguments for accessibility character locations");
                return;
            }
            RectF[] boundingRects = new RectF[positionInfoLength];
            CursorAnchorInfo.Builder builder = new CursorAnchorInfo.Builder();
            populateCharacterBounds(builder, positionInfoStartIndex, Math.min(positionInfoStartIndex + positionInfoLength, length()), viewportToContentHorizontalOffset(), viewportToContentVerticalOffset());
            CursorAnchorInfo cursorAnchorInfo = builder.setMatrix(null).build();
            for (int i = 0; i < positionInfoLength; i++) {
                int flags = cursorAnchorInfo.getCharacterBoundsFlags(positionInfoStartIndex + i);
                if ((flags & 1) == 1 && (bounds = cursorAnchorInfo.getCharacterBounds(positionInfoStartIndex + i)) != null) {
                    mapRectFromViewToScreenCoords(bounds, true);
                    boundingRects[i] = bounds;
                }
            }
            info.getExtras().putParcelableArray(extraDataKey, boundingRects);
        } else if (extraDataKey.equals(AccessibilityNodeInfo.EXTRA_DATA_RENDERING_INFO_KEY)) {
            AccessibilityNodeInfo.ExtraRenderingInfo extraRenderingInfo = AccessibilityNodeInfo.ExtraRenderingInfo.obtain();
            extraRenderingInfo.setLayoutSize(getLayoutParams().width, getLayoutParams().height);
            extraRenderingInfo.setTextSizeInPx(getTextSize());
            extraRenderingInfo.setTextSizeUnit(getTextSizeUnit());
            info.setExtraRenderingInfo(extraRenderingInfo);
        }
    }

    private boolean getContentVisibleRect(Rect rect) {
        if (!getLocalVisibleRect(rect)) {
            return false;
        }
        rect.offset(-getScrollX(), -getScrollY());
        return rect.intersect(getCompoundPaddingLeft(), getCompoundPaddingTop(), getWidth() - getCompoundPaddingRight(), getHeight() - getCompoundPaddingBottom());
    }

    public void populateCharacterBounds(CursorAnchorInfo.Builder builder, int startIndex, int endIndex, float viewportToContentHorizontalOffset, float viewportToContentVerticalOffset) {
        if (isOffsetMappingAvailable()) {
            return;
        }
        Rect rect = new Rect();
        getContentVisibleRect(rect);
        RectF visibleRect = new RectF(rect);
        float[] characterBounds = getCharacterBounds(startIndex, endIndex, viewportToContentHorizontalOffset, viewportToContentVerticalOffset);
        int limit = endIndex - startIndex;
        for (int offset = 0; offset < limit; offset++) {
            float left = characterBounds[offset * 4];
            float top = characterBounds[(offset * 4) + 1];
            float right = characterBounds[(offset * 4) + 2];
            float bottom = characterBounds[(offset * 4) + 3];
            boolean hasVisibleRegion = visibleRect.intersects(left, top, right, bottom);
            boolean hasInVisibleRegion = !visibleRect.contains(left, top, right, bottom);
            int characterBoundsFlags = 0;
            if (hasVisibleRegion) {
                characterBoundsFlags = 0 | 1;
            }
            if (hasInVisibleRegion) {
                characterBoundsFlags |= 2;
            }
            if (this.mLayout.isRtlCharAt(offset)) {
                characterBoundsFlags |= 4;
            }
            builder.addCharacterBounds(offset + startIndex, left, top, right, bottom, characterBoundsFlags);
        }
    }

    private float[] getCharacterBounds(int start, int end, float layoutLeft, float layoutTop) {
        float[] characterBounds = new float[(end - start) * 4];
        this.mLayout.fillCharacterBounds(start, end, characterBounds, 0);
        for (int offset = 0; offset < end - start; offset++) {
            int i = offset * 4;
            characterBounds[i] = characterBounds[i] + layoutLeft;
            int i2 = (offset * 4) + 1;
            characterBounds[i2] = characterBounds[i2] + layoutTop;
            int i3 = (offset * 4) + 2;
            characterBounds[i3] = characterBounds[i3] + layoutLeft;
            int i4 = (offset * 4) + 3;
            characterBounds[i4] = characterBounds[i4] + layoutTop;
        }
        return characterBounds;
    }

    public CursorAnchorInfo getCursorAnchorInfo(int filter, CursorAnchorInfo.Builder cursorAnchorInfoBuilder, Matrix viewToScreenMatrix) {
        int selectionStart;
        CursorAnchorInfo.Builder builder;
        CursorAnchorInfo.Builder builder2;
        int insertionMarkerFlags;
        int composingTextStart;
        Layout layout = getLayout();
        if (layout == null) {
            return null;
        }
        boolean includeEditorBounds = (filter & 4) != 0;
        boolean includeCharacterBounds = (filter & 8) != 0;
        boolean includeInsertionMarker = (filter & 16) != 0;
        boolean includeVisibleLineBounds = (filter & 32) != 0;
        boolean includeTextAppearance = (filter & 64) != 0;
        boolean includeAll = (includeEditorBounds || includeCharacterBounds || includeInsertionMarker || includeVisibleLineBounds || includeTextAppearance) ? false : true;
        boolean includeEditorBounds2 = includeEditorBounds | includeAll;
        boolean includeCharacterBounds2 = includeCharacterBounds | includeAll;
        boolean includeInsertionMarker2 = includeInsertionMarker | includeAll;
        boolean includeVisibleLineBounds2 = includeVisibleLineBounds | includeAll;
        boolean includeTextAppearance2 = includeTextAppearance | includeAll;
        cursorAnchorInfoBuilder.reset();
        int selectionStart2 = getSelectionStart();
        cursorAnchorInfoBuilder.setSelectionRange(selectionStart2, getSelectionEnd());
        viewToScreenMatrix.reset();
        transformMatrixToGlobal(viewToScreenMatrix);
        cursorAnchorInfoBuilder.setMatrix(viewToScreenMatrix);
        if (includeEditorBounds2) {
            RectF editorBounds = new RectF();
            editorBounds.set(0.0f, 0.0f, getWidth(), getHeight());
            RectF handwritingBounds = new RectF(-getHandwritingBoundsOffsetLeft(), -getHandwritingBoundsOffsetTop(), getWidth() + getHandwritingBoundsOffsetRight(), getHeight() + getHandwritingBoundsOffsetBottom());
            EditorBoundsInfo.Builder boundsBuilder = new EditorBoundsInfo.Builder();
            EditorBoundsInfo editorBoundsInfo = boundsBuilder.setEditorBounds(editorBounds).setHandwritingBounds(handwritingBounds).build();
            cursorAnchorInfoBuilder.setEditorBoundsInfo(editorBoundsInfo);
        }
        if (!includeCharacterBounds2 && !includeInsertionMarker2 && !includeVisibleLineBounds2) {
            builder2 = cursorAnchorInfoBuilder;
        } else {
            float viewportToContentHorizontalOffset = viewportToContentHorizontalOffset();
            float viewportToContentVerticalOffset = viewportToContentVerticalOffset();
            boolean isTextTransformed = getTransformationMethod() != null && (getTransformed() instanceof OffsetMapping);
            if (!includeCharacterBounds2 || isTextTransformed) {
                selectionStart = selectionStart2;
                builder = cursorAnchorInfoBuilder;
            } else {
                CharSequence text = getText();
                if (!(text instanceof Spannable)) {
                    selectionStart = selectionStart2;
                    builder = cursorAnchorInfoBuilder;
                } else {
                    Spannable sp = (Spannable) text;
                    int composingTextStart2 = EditableInputConnection.getComposingSpanStart(sp);
                    int composingTextEnd = EditableInputConnection.getComposingSpanEnd(sp);
                    if (composingTextEnd >= composingTextStart2) {
                        composingTextStart = composingTextStart2;
                    } else {
                        composingTextStart = composingTextEnd;
                        composingTextEnd = composingTextStart2;
                    }
                    boolean hasComposingText = composingTextStart >= 0 && composingTextStart < composingTextEnd;
                    if (!hasComposingText) {
                        selectionStart = selectionStart2;
                        builder = cursorAnchorInfoBuilder;
                    } else {
                        CharSequence composingText = text.subSequence(composingTextStart, composingTextEnd);
                        cursorAnchorInfoBuilder.setComposingText(composingTextStart, composingText);
                        selectionStart = selectionStart2;
                        builder = cursorAnchorInfoBuilder;
                        populateCharacterBounds(cursorAnchorInfoBuilder, composingTextStart, composingTextEnd, viewportToContentHorizontalOffset, viewportToContentVerticalOffset);
                    }
                }
            }
            if (includeInsertionMarker2 && selectionStart >= 0) {
                int offsetTransformed = originalToTransformed(selectionStart, 1);
                int line = layout.getLineForOffset(offsetTransformed);
                float insertionMarkerX = layout.getPrimaryHorizontal(offsetTransformed) + viewportToContentHorizontalOffset;
                float insertionMarkerTop = layout.getLineTop(line) + viewportToContentVerticalOffset;
                float insertionMarkerBaseline = layout.getLineBaseline(line) + viewportToContentVerticalOffset;
                float insertionMarkerBottom = layout.getLineBottom(line, false) + viewportToContentVerticalOffset;
                boolean isTopVisible = isPositionVisible(insertionMarkerX, insertionMarkerTop);
                boolean isBottomVisible = isPositionVisible(insertionMarkerX, insertionMarkerBottom);
                int insertionMarkerFlags2 = 0;
                if (isTopVisible || isBottomVisible) {
                    insertionMarkerFlags2 = 0 | 1;
                }
                if (!isTopVisible || !isBottomVisible) {
                    insertionMarkerFlags2 |= 2;
                }
                if (!layout.isRtlCharAt(offsetTransformed)) {
                    insertionMarkerFlags = insertionMarkerFlags2;
                } else {
                    insertionMarkerFlags = insertionMarkerFlags2 | 4;
                }
                builder.setInsertionMarkerLocation(insertionMarkerX, insertionMarkerTop, insertionMarkerBaseline, insertionMarkerBottom, insertionMarkerFlags);
            }
            if (!includeVisibleLineBounds2) {
                builder2 = builder;
            } else {
                Rect visibleRect = new Rect();
                if (getContentVisibleRect(visibleRect)) {
                    float visibleTop = visibleRect.top - viewportToContentVerticalOffset;
                    float visibleBottom = visibleRect.bottom - viewportToContentVerticalOffset;
                    int firstLine = layout.getLineForVertical((int) Math.floor(visibleTop));
                    int lastLine = layout.getLineForVertical((int) Math.ceil(visibleBottom));
                    int line2 = firstLine;
                    while (line2 <= lastLine) {
                        Rect visibleRect2 = visibleRect;
                        float left = layout.getLineLeft(line2) + viewportToContentHorizontalOffset;
                        float visibleTop2 = visibleTop;
                        float top = layout.getLineTop(line2) + viewportToContentVerticalOffset;
                        float visibleBottom2 = visibleBottom;
                        float visibleBottom3 = layout.getLineRight(line2) + viewportToContentHorizontalOffset;
                        int lastLine2 = lastLine;
                        int lastLine3 = layout.getLineBottom(line2, false);
                        float bottom = lastLine3 + viewportToContentVerticalOffset;
                        builder.addVisibleLineBounds(left, top, visibleBottom3, bottom);
                        line2++;
                        lastLine = lastLine2;
                        visibleTop = visibleTop2;
                        visibleRect = visibleRect2;
                        firstLine = firstLine;
                        visibleBottom = visibleBottom2;
                    }
                    builder2 = builder;
                } else {
                    builder2 = builder;
                }
            }
        }
        if (includeTextAppearance2) {
            builder2.setTextAppearanceInfo(TextAppearanceInfo.createFromTextView(this));
        }
        return builder2.build();
    }

    public TextBoundsInfo getTextBoundsInfo(RectF bounds) {
        CharSequence text;
        RectF localBounds;
        Layout layout = getLayout();
        if (layout != null && (text = layout.getText()) != null && !isOffsetMappingAvailable()) {
            Matrix localToGlobalMatrix = new Matrix();
            transformMatrixToGlobal(localToGlobalMatrix);
            Matrix globalToLocalMatrix = new Matrix();
            if (!localToGlobalMatrix.invert(globalToLocalMatrix)) {
                return null;
            }
            float layoutLeft = viewportToContentHorizontalOffset();
            float layoutTop = viewportToContentVerticalOffset();
            RectF localBounds2 = new RectF(bounds);
            globalToLocalMatrix.mapRect(localBounds2);
            localBounds2.offset(-layoutLeft, -layoutTop);
            if (localBounds2.intersects(0.0f, 0.0f, layout.getWidth(), layout.getHeight()) && text.length() != 0) {
                int startLine = layout.getLineForVertical((int) Math.floor(localBounds2.top));
                int endLine = layout.getLineForVertical((int) Math.floor(localBounds2.bottom));
                int start = layout.getLineStart(startLine);
                int end = layout.getLineEnd(endLine);
                float[] characterBounds = getCharacterBounds(start, end, layoutLeft, layoutTop);
                int[] characterFlags = new int[end - start];
                int[] characterBidiLevels = new int[end - start];
                int line = startLine;
                while (true) {
                    if (line > endLine) {
                        break;
                    }
                    int lineStart = layout.getLineStart(line);
                    float layoutLeft2 = layoutLeft;
                    int lineEnd = layout.getLineEnd(line);
                    Matrix globalToLocalMatrix2 = globalToLocalMatrix;
                    Layout.Directions directions = layout.getLineDirections(line);
                    float layoutTop2 = layoutTop;
                    int i = 0;
                    while (true) {
                        localBounds = localBounds2;
                        if (i >= directions.getRunCount()) {
                            break;
                        }
                        int runStart = directions.getRunStart(i) + lineStart;
                        int runEnd = Math.min(runStart + directions.getRunLength(i), lineEnd);
                        float[] characterBounds2 = characterBounds;
                        int runLevel = directions.getRunLevel(i);
                        Arrays.fill(characterBidiLevels, runStart - start, runEnd - start, runLevel);
                        i++;
                        localBounds2 = localBounds;
                        characterBounds = characterBounds2;
                        directions = directions;
                    }
                    float[] characterBounds3 = characterBounds;
                    boolean lineIsRtl = layout.getParagraphDirection(line) == -1;
                    for (int index = lineStart; index < lineEnd; index++) {
                        int flags = 0;
                        if (TextUtils.isWhitespace(text.charAt(index))) {
                            flags = 0 | 1;
                        }
                        if (TextUtils.isPunctuation(Character.codePointAt(text, index))) {
                            flags |= 4;
                        }
                        if (TextUtils.isNewline(Character.codePointAt(text, index))) {
                            flags |= 2;
                        }
                        if (lineIsRtl) {
                            flags |= 8;
                        }
                        characterFlags[index - start] = flags;
                    }
                    line++;
                    layoutLeft = layoutLeft2;
                    globalToLocalMatrix = globalToLocalMatrix2;
                    localBounds2 = localBounds;
                    layoutTop = layoutTop2;
                    characterBounds = characterBounds3;
                }
                float[] characterBounds4 = characterBounds;
                SegmentFinder graphemeSegmentFinder = new GraphemeClusterSegmentFinder(text, layout.getPaint());
                WordIterator wordIterator = getWordIterator();
                wordIterator.setCharSequence(text, 0, text.length());
                SegmentFinder wordSegmentFinder = new WordSegmentFinder(text, wordIterator);
                int lineCount = (endLine - startLine) + 1;
                int[] lineRanges = new int[lineCount * 2];
                for (int line2 = startLine; line2 <= endLine; line2++) {
                    int offset = line2 - startLine;
                    lineRanges[offset * 2] = layout.getLineStart(line2);
                    lineRanges[(offset * 2) + 1] = layout.getLineEnd(line2);
                }
                SegmentFinder lineSegmentFinder = new SegmentFinder.PrescribedSegmentFinder(lineRanges);
                return new TextBoundsInfo.Builder(start, end).setMatrix(localToGlobalMatrix).setCharacterBounds(characterBounds4).setCharacterBidiLevel(characterBidiLevels).setCharacterFlags(characterFlags).setGraphemeSegmentFinder(graphemeSegmentFinder).setLineSegmentFinder(lineSegmentFinder).setWordSegmentFinder(wordSegmentFinder).build();
            }
            TextBoundsInfo.Builder builder = new TextBoundsInfo.Builder(0, 0);
            SegmentFinder emptySegmentFinder = new SegmentFinder.PrescribedSegmentFinder(new int[0]);
            builder.setMatrix(localToGlobalMatrix).setCharacterBounds(new float[0]).setCharacterBidiLevel(new int[0]).setCharacterFlags(new int[0]).setGraphemeSegmentFinder(emptySegmentFinder).setLineSegmentFinder(emptySegmentFinder).setWordSegmentFinder(emptySegmentFinder);
            return builder.build();
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean isPositionVisible(float positionX, float positionY) {
        float[] position = TEMP_POSITION;
        synchronized (position) {
            position[0] = positionX;
            position[1] = positionY;
            View view = this;
            while (view != null) {
                if (view != this) {
                    position[0] = position[0] - view.getScrollX();
                    position[1] = position[1] - view.getScrollY();
                }
                if (position[0] >= 0.0f && position[1] >= 0.0f && position[0] <= view.getWidth() && position[1] <= view.getHeight()) {
                    if (!view.getMatrix().isIdentity()) {
                        view.getMatrix().mapPoints(position);
                    }
                    position[0] = position[0] + view.getLeft();
                    position[1] = position[1] + view.getTop();
                    ViewParent parent = view.getParent();
                    if (parent instanceof View) {
                        view = (View) parent;
                    } else {
                        view = null;
                    }
                }
                return false;
            }
            return true;
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        int start;
        int end;
        int updatedTextLength;
        Editor editor = this.mEditor;
        if (editor == null || !(editor.mProcessTextIntentActionsHandler.performAccessibilityAction(action) || this.mEditor.performSmartActionsAccessibilityAction(action))) {
            switch (action) {
                case 16:
                    return performAccessibilityActionClick(arguments);
                case 32:
                    if (!isLongClickable()) {
                        return false;
                    }
                    if (isEnabled() && this.mBufferType == BufferType.EDITABLE) {
                        this.mEditor.mIsBeingLongClickedByAccessibility = true;
                        try {
                            boolean handled = performLongClick();
                            return handled;
                        } finally {
                            this.mEditor.mIsBeingLongClickedByAccessibility = false;
                        }
                    }
                    boolean handled2 = performLongClick();
                    return handled2;
                case 256:
                case 512:
                    ensureIterableTextForAccessibilitySelectable();
                    return super.performAccessibilityActionInternal(action, arguments);
                case 16384:
                    return isFocused() && canCopy() && onTextContextMenuItem(16908321);
                case 32768:
                    return isFocused() && canPaste() && onTextContextMenuItem(16908322);
                case 65536:
                    return isFocused() && canCut() && onTextContextMenuItem(16908320);
                case 131072:
                    ensureIterableTextForAccessibilitySelectable();
                    CharSequence text = getIterableTextForAccessibility();
                    if (text == null) {
                        return false;
                    }
                    if (arguments != null) {
                        start = arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, -1);
                    } else {
                        start = -1;
                    }
                    if (arguments != null) {
                        end = arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, -1);
                    } else {
                        end = -1;
                    }
                    if (getSelectionStart() != start || getSelectionEnd() != end) {
                        if (start == end && end == -1) {
                            Selection.removeSelection((Spannable) text);
                            return true;
                        } else if (start >= 0 && start <= end && end <= text.length()) {
                            requestFocusOnNonEditableSelectableText();
                            Selection.setSelection((Spannable) text, start, end);
                            Editor editor2 = this.mEditor;
                            if (editor2 != null) {
                                editor2.startSelectionActionModeAsync(false);
                            }
                            return true;
                        }
                    }
                    return false;
                case 2097152:
                    if (!isEnabled() || this.mBufferType != BufferType.EDITABLE) {
                        return false;
                    }
                    setText(arguments != null ? arguments.getCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE) : null);
                    CharSequence charSequence = this.mText;
                    if (charSequence != null && (updatedTextLength = charSequence.length()) > 0) {
                        Selection.setSelection(this.mSpannable, updatedTextLength);
                    }
                    return true;
                case 16908372:
                    if (isFocused() && isTextEditable()) {
                        onEditorAction(getImeActionId());
                    }
                    return true;
                case 268435456:
                    return isFocused() && canShare() && onTextContextMenuItem(16908341);
                default:
                    if (action == 16908376) {
                        return isFocused() && canReplace() && onTextContextMenuItem(16908340);
                    }
                    return super.performAccessibilityActionInternal(action, arguments);
            }
        }
        return true;
    }

    private boolean performAccessibilityActionClick(Bundle arguments) {
        boolean handled = false;
        if (isEnabled()) {
            if (isClickable() || isLongClickable()) {
                if (isFocusable() && !isFocused()) {
                    requestFocus();
                }
                performClick();
                handled = true;
            }
            if ((this.mMovement != null || onCheckIsTextEditor()) && hasSpannableText() && this.mLayout != null) {
                if ((isTextEditable() || isTextSelectable()) && isFocused()) {
                    InputMethodManager imm = getInputMethodManager();
                    viewClicked(imm);
                    if (!isTextSelectable() && this.mEditor.mShowSoftInputOnFocus && imm != null) {
                        return handled | imm.showSoftInput(this, 0);
                    }
                    return handled;
                }
                return handled;
            }
            return handled;
        }
        return false;
    }

    private void requestFocusOnNonEditableSelectableText() {
        if (!isTextEditable() && isTextSelectable() && isEnabled() && isFocusable() && !isFocused()) {
            requestFocus();
        }
    }

    private boolean hasSpannableText() {
        CharSequence charSequence = this.mText;
        return charSequence != null && (charSequence instanceof Spannable);
    }

    @Override // android.view.View
    public void sendAccessibilityEventInternal(int eventType) {
        Editor editor;
        if (eventType == 32768 && (editor = this.mEditor) != null) {
            editor.mProcessTextIntentActionsHandler.initializeAccessibilityActions();
        }
        super.sendAccessibilityEventInternal(eventType);
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        if (event.getEventType() == 4096) {
            return;
        }
        super.sendAccessibilityEventUnchecked(event);
    }

    private CharSequence getTextForAccessibility() {
        if (TextUtils.isEmpty(this.mText)) {
            return this.mHint;
        }
        return TextUtils.trimToParcelableSize(this.mTransformed);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isVisibleToAccessibility() {
        return AccessibilityManager.getInstance(this.mContext).isEnabled() && (isFocused() || (isSelected() && isShown()));
    }

    void sendAccessibilityEventTypeViewTextChanged(CharSequence beforeText, int fromIndex, int removedCount, int addedCount) {
        AccessibilityEvent event = AccessibilityEvent.obtain(16);
        event.setFromIndex(fromIndex);
        event.setRemovedCount(removedCount);
        event.setAddedCount(addedCount);
        event.setBeforeText(beforeText);
        sendAccessibilityEventUnchecked(event);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendAccessibilityEventTypeViewTextChanged(CharSequence beforeText, int fromIndex, int toIndex) {
        AccessibilityEvent event = AccessibilityEvent.obtain(16);
        event.setFromIndex(fromIndex);
        event.setToIndex(toIndex);
        event.setBeforeText(beforeText);
        sendAccessibilityEventUnchecked(event);
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getContext().getSystemService(InputMethodManager.class);
    }

    public boolean isInputMethodTarget() {
        InputMethodManager imm = getInputMethodManager();
        return imm != null && imm.isActive(this);
    }

    public boolean onTextContextMenuItem(int id) {
        int min = 0;
        int max = this.mText.length();
        if (isFocused()) {
            int selStart = getSelectionStart();
            int selEnd = getSelectionEnd();
            min = Math.max(0, Math.min(selStart, selEnd));
            max = Math.max(0, Math.max(selStart, selEnd));
        }
        switch (id) {
            case 16908319:
                boolean hadSelection = hasSelection();
                selectAllText();
                Editor editor = this.mEditor;
                if (editor != null && hadSelection) {
                    editor.invalidateActionModeAsync();
                }
                return true;
            case 16908320:
                ClipData cutData = ClipData.newPlainText(null, getTransformedText(min, max));
                if (setPrimaryClip(cutData)) {
                    deleteText_internal(min, max);
                } else {
                    Toast.makeText(getContext(), (int) C4057R.string.failed_to_copy_to_clipboard, 0).show();
                }
                return true;
            case 16908321:
                int selStart2 = getSelectionStart();
                int selEnd2 = getSelectionEnd();
                int min2 = Math.max(0, Math.min(selStart2, selEnd2));
                int max2 = Math.max(0, Math.max(selStart2, selEnd2));
                ClipData copyData = ClipData.newPlainText(null, getTransformedText(min2, max2));
                if (setPrimaryClip(copyData)) {
                    stopTextActionMode();
                } else {
                    Toast.makeText(getContext(), (int) C4057R.string.failed_to_copy_to_clipboard, 0).show();
                }
                return true;
            case 16908322:
                paste(true);
                return true;
            case 16908337:
                paste(false);
                return true;
            case 16908338:
                Editor editor2 = this.mEditor;
                if (editor2 != null) {
                    editor2.undo();
                }
                return true;
            case 16908339:
                Editor editor3 = this.mEditor;
                if (editor3 != null) {
                    editor3.redo();
                }
                return true;
            case 16908340:
                Editor editor4 = this.mEditor;
                if (editor4 != null) {
                    editor4.replace();
                }
                return true;
            case 16908341:
                shareSelectedText();
                return true;
            case 16908355:
                requestAutofill();
                stopTextActionMode();
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getTransformedText(int start, int end) {
        return removeSuggestionSpans(this.mTransformed.subSequence(start, end));
    }

    @Override // android.view.View
    public boolean performLongClick() {
        boolean handled = false;
        boolean performedHapticFeedback = false;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.mIsBeingLongClicked = true;
        }
        if (super.performLongClick()) {
            handled = true;
            performedHapticFeedback = true;
        }
        Editor editor2 = this.mEditor;
        if (editor2 != null) {
            handled |= editor2.performLongClick(handled);
            this.mEditor.mIsBeingLongClicked = false;
        }
        if (handled) {
            if (!performedHapticFeedback) {
                performHapticFeedback(0);
            }
            Editor editor3 = this.mEditor;
            if (editor3 != null) {
                editor3.mDiscardNextActionUp = true;
            }
        } else {
            MetricsLogger.action(this.mContext, (int) MetricsProto.MetricsEvent.TEXT_LONGPRESS, 0);
        }
        return handled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onScrollChanged();
        }
    }

    public boolean isSuggestionsEnabled() {
        Editor editor = this.mEditor;
        if (editor != null && (editor.mInputType & 15) == 1 && (this.mEditor.mInputType & 524288) <= 0) {
            int variation = this.mEditor.mInputType & InputType.TYPE_MASK_VARIATION;
            return variation == 0 || variation == 48 || variation == 80 || variation == 64 || variation == 160;
        }
        return false;
    }

    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        createEditorIfNeeded();
        this.mEditor.mCustomSelectionActionModeCallback = actionModeCallback;
    }

    public ActionMode.Callback getCustomSelectionActionModeCallback() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return null;
        }
        return editor.mCustomSelectionActionModeCallback;
    }

    public void setCustomInsertionActionModeCallback(ActionMode.Callback actionModeCallback) {
        createEditorIfNeeded();
        this.mEditor.mCustomInsertionActionModeCallback = actionModeCallback;
    }

    public ActionMode.Callback getCustomInsertionActionModeCallback() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return null;
        }
        return editor.mCustomInsertionActionModeCallback;
    }

    public void setTextClassifier(TextClassifier textClassifier) {
        this.mTextClassifier = textClassifier;
    }

    public TextClassifier getTextClassifier() {
        TextClassifier textClassifier = this.mTextClassifier;
        if (textClassifier == null) {
            TextClassificationManager tcm = getTextClassificationManagerForUser();
            if (tcm != null) {
                return tcm.getTextClassifier();
            }
            return TextClassifier.NO_OP;
        }
        return textClassifier;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextClassifier getTextClassificationSession() {
        String widgetType;
        TextClassifier textClassifier = this.mTextClassificationSession;
        if (textClassifier == null || textClassifier.isDestroyed()) {
            TextClassificationManager tcm = getTextClassificationManagerForUser();
            if (tcm != null) {
                if (isTextEditable()) {
                    widgetType = TextClassifier.WIDGET_TYPE_EDITTEXT;
                } else if (isTextSelectable()) {
                    widgetType = TextClassifier.WIDGET_TYPE_TEXTVIEW;
                } else {
                    widgetType = TextClassifier.WIDGET_TYPE_UNSELECTABLE_TEXTVIEW;
                }
                TextClassificationContext build = new TextClassificationContext.Builder(this.mContext.getPackageName(), widgetType).build();
                this.mTextClassificationContext = build;
                TextClassifier textClassifier2 = this.mTextClassifier;
                if (textClassifier2 != null) {
                    this.mTextClassificationSession = tcm.createTextClassificationSession(build, textClassifier2);
                } else {
                    this.mTextClassificationSession = tcm.createTextClassificationSession(build);
                }
            } else {
                this.mTextClassificationSession = TextClassifier.NO_OP;
            }
        }
        return this.mTextClassificationSession;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextClassificationContext getTextClassificationContext() {
        return this.mTextClassificationContext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean usesNoOpTextClassifier() {
        return getTextClassifier() == TextClassifier.NO_OP;
    }

    public boolean requestActionMode(TextLinks.TextLinkSpan clickedSpan) {
        Preconditions.checkNotNull(clickedSpan);
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Spanned) {
            int start = ((Spanned) charSequence).getSpanStart(clickedSpan);
            int end = ((Spanned) this.mText).getSpanEnd(clickedSpan);
            if (start < 0 || end > this.mText.length() || start >= end) {
                return false;
            }
            createEditorIfNeeded();
            this.mEditor.startLinkActionModeAsync(start, end);
            return true;
        }
        return false;
    }

    public boolean handleClick(TextLinks.TextLinkSpan clickedSpan) {
        Preconditions.checkNotNull(clickedSpan);
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence;
            int start = spanned.getSpanStart(clickedSpan);
            int end = spanned.getSpanEnd(clickedSpan);
            if (start >= 0 && end <= this.mText.length() && start < end) {
                final TextClassification.Request request = new TextClassification.Request.Builder(this.mText, start, end).setDefaultLocales(getTextLocales()).build();
                Supplier<TextClassification> supplier = new Supplier() { // from class: android.widget.TextView$$ExternalSyntheticLambda5
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        TextClassification lambda$handleClick$4;
                        lambda$handleClick$4 = TextView.this.lambda$handleClick$4(request);
                        return lambda$handleClick$4;
                    }
                };
                Consumer<TextClassification> consumer = new Consumer() { // from class: android.widget.TextView$$ExternalSyntheticLambda6
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        TextView.lambda$handleClick$5((TextClassification) obj);
                    }
                };
                CompletableFuture.supplyAsync(supplier).completeOnTimeout(null, 1L, TimeUnit.SECONDS).thenAccept((Consumer) consumer);
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ TextClassification lambda$handleClick$4(TextClassification.Request request) {
        return getTextClassificationSession().classifyText(request);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$handleClick$5(TextClassification classification) {
        if (classification == null) {
            Log.m112d(LOG_TAG, "Timeout while classifying text");
        } else if (classification.getActions().isEmpty()) {
            Log.m112d(LOG_TAG, "No link action to perform");
        } else {
            try {
                classification.getActions().get(0).getActionIntent().send();
            } catch (PendingIntent.CanceledException e) {
                Log.m109e(LOG_TAG, "Error sending PendingIntent", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stopTextActionMode() {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.lambda$startActionModeInternal$0();
        }
    }

    public void hideFloatingToolbar(int durationMs) {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.hideFloatingToolbar(durationMs);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canUndo() {
        Editor editor = this.mEditor;
        return editor != null && editor.canUndo();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canRedo() {
        Editor editor = this.mEditor;
        return editor != null && editor.canRedo();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canCut() {
        Editor editor;
        return !hasPasswordTransformationMethod() && this.mText.length() > 0 && hasSelection() && (this.mText instanceof Editable) && (editor = this.mEditor) != null && editor.mKeyListener != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canCopy() {
        return !hasPasswordTransformationMethod() && this.mText.length() > 0 && hasSelection() && this.mEditor != null;
    }

    boolean canReplace() {
        return !hasPasswordTransformationMethod() && this.mText.length() > 0 && (this.mText instanceof Editable) && this.mEditor != null && isSuggestionsEnabled() && this.mEditor.shouldOfferToShowSuggestions();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canShare() {
        if (!getContext().canStartActivityForResult() || !isDeviceProvisioned()) {
            return false;
        }
        return canCopy();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDeviceProvisioned() {
        int i;
        if (this.mDeviceProvisionedState == 0) {
            if (Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0) {
                i = 2;
            } else {
                i = 1;
            }
            this.mDeviceProvisionedState = i;
        }
        return this.mDeviceProvisionedState == 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canPaste() {
        Editor editor;
        return (this.mText instanceof Editable) && (editor = this.mEditor) != null && editor.mKeyListener != null && getSelectionStart() >= 0 && getSelectionEnd() >= 0 && getClipboardManagerForUser().hasPrimaryClip();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canPasteAsPlainText() {
        if (canPaste()) {
            ClipDescription description = getClipboardManagerForUser().getPrimaryClipDescription();
            boolean isPlainType = description.hasMimeType("text/plain");
            return (isPlainType && description.isStyledText()) || description.hasMimeType("text/html");
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canProcessText() {
        if (getId() == -1) {
            return false;
        }
        return canShare();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canSelectAllText() {
        return (!canSelectText() || hasPasswordTransformationMethod() || (getSelectionStart() == 0 && getSelectionEnd() == this.mText.length())) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean selectAllText() {
        if (this.mEditor != null) {
            hideFloatingToolbar(500);
        }
        int length = this.mText.length();
        Selection.setSelection(this.mSpannable, 0, length);
        return length > 0;
    }

    private void paste(boolean withFormatting) {
        ClipboardManager clipboard = getClipboardManagerForUser();
        ClipData clip = clipboard.getPrimaryClip();
        if (clip == null) {
            return;
        }
        ContentInfo payload = new ContentInfo.Builder(clip, 1).setFlags(!withFormatting ? 1 : 0).build();
        performReceiveContent(payload);
        sLastCutCopyOrTextChangedTime = 0L;
    }

    private void shareSelectedText() {
        String selectedText = getSelectedText();
        if (selectedText != null && !selectedText.isEmpty()) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.removeExtra(Intent.EXTRA_TEXT);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, (String) TextUtils.trimToParcelableSize(selectedText));
            getContext().startActivity(Intent.createChooser(sharingIntent, null));
            Selection.setSelection(this.mSpannable, getSelectionEnd());
        }
    }

    private boolean setPrimaryClip(ClipData clip) {
        ClipboardManager clipboard = getClipboardManagerForUser();
        try {
            clipboard.setPrimaryClip(clip);
            sLastCutCopyOrTextChangedTime = SystemClock.uptimeMillis();
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    public int getOffsetForPosition(float x, float y) {
        if (getLayout() == null) {
            return -1;
        }
        int line = getLineAtCoordinate(y);
        int offset = getOffsetAtCoordinate(line, x);
        return offset;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float convertToLocalHorizontalCoordinate(float x) {
        return Math.min((getWidth() - getTotalPaddingRight()) - 1, Math.max(0.0f, x - getTotalPaddingLeft())) + getScrollX();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getLineAtCoordinate(float y) {
        return getLayout().getLineForVertical((int) (Math.min((getHeight() - getTotalPaddingBottom()) - 1, Math.max(0.0f, y - getTotalPaddingTop())) + getScrollY()));
    }

    int getLineAtCoordinateUnclamped(float y) {
        return getLayout().getLineForVertical((int) ((y - getTotalPaddingTop()) + getScrollY()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getOffsetAtCoordinate(int line, float x) {
        int offset = getLayout().getOffsetForHorizontal(line, convertToLocalHorizontalCoordinate(x));
        return transformedToOriginal(offset, 1);
    }

    public int transformedToOriginal(int offset, int strategy) {
        if (getTransformationMethod() == null) {
            return offset;
        }
        CharSequence charSequence = this.mTransformed;
        if (charSequence instanceof OffsetMapping) {
            OffsetMapping transformedText = (OffsetMapping) charSequence;
            return transformedText.transformedToOriginal(offset, strategy);
        }
        return offset;
    }

    public int originalToTransformed(int offset, int strategy) {
        if (getTransformationMethod() == null) {
            return offset;
        }
        CharSequence charSequence = this.mTransformed;
        if (charSequence instanceof OffsetMapping) {
            OffsetMapping transformedText = (OffsetMapping) charSequence;
            return transformedText.originalToTransformed(offset, strategy);
        }
        return offset;
    }

    @Override // android.view.View
    public boolean onDragEvent(DragEvent event) {
        Editor editor = this.mEditor;
        if (editor == null || !editor.hasInsertionController()) {
            return super.onDragEvent(event);
        }
        switch (event.getAction()) {
            case 1:
                return true;
            case 2:
                if (this.mText instanceof Spannable) {
                    int offset = getOffsetForPosition(event.getX(), event.getY());
                    Selection.setSelection(this.mSpannable, offset);
                }
                return true;
            case 3:
                Editor editor2 = this.mEditor;
                if (editor2 != null) {
                    editor2.onDrop(event);
                }
                return true;
            case 4:
            default:
                return true;
            case 5:
                requestFocus();
                return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInBatchEditMode() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return false;
        }
        Editor.InputMethodState ims = editor.mInputMethodState;
        if (ims != null) {
            return ims.mBatchEditNesting > 0;
        }
        return this.mEditor.mInBatchEditControllers;
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        TextDirectionHeuristic newTextDir = getTextDirectionHeuristic();
        if (this.mTextDir != newTextDir) {
            this.mTextDir = newTextDir;
            if (this.mLayout != null) {
                checkForRelayout();
            }
        }
    }

    public TextDirectionHeuristic getTextDirectionHeuristic() {
        if (hasPasswordTransformationMethod()) {
            return TextDirectionHeuristics.LTR;
        }
        Editor editor = this.mEditor;
        if (editor != null && (editor.mInputType & 15) == 3) {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(getTextLocale());
            String zero = symbols.getDigitStrings()[0];
            int firstCodepoint = zero.codePointAt(0);
            byte digitDirection = Character.getDirectionality(firstCodepoint);
            if (digitDirection == 1 || digitDirection == 2) {
                return TextDirectionHeuristics.RTL;
            }
            return TextDirectionHeuristics.LTR;
        }
        boolean defaultIsRtl = getLayoutDirection() == 1;
        switch (getTextDirection()) {
            case 2:
                return TextDirectionHeuristics.ANYRTL_LTR;
            case 3:
                return TextDirectionHeuristics.LTR;
            case 4:
                return TextDirectionHeuristics.RTL;
            case 5:
                return TextDirectionHeuristics.LOCALE;
            case 6:
                return TextDirectionHeuristics.FIRSTSTRONG_LTR;
            case 7:
                return TextDirectionHeuristics.FIRSTSTRONG_RTL;
            default:
                return defaultIsRtl ? TextDirectionHeuristics.FIRSTSTRONG_RTL : TextDirectionHeuristics.FIRSTSTRONG_LTR;
        }
    }

    @Override // android.view.View
    public void onResolveDrawables(int layoutDirection) {
        if (this.mLastLayoutDirection == layoutDirection) {
            return;
        }
        this.mLastLayoutDirection = layoutDirection;
        Drawables drawables = this.mDrawables;
        if (drawables != null && drawables.resolveWithLayoutDirection(layoutDirection)) {
            prepareDrawableForDisplay(this.mDrawables.mShowing[0]);
            prepareDrawableForDisplay(this.mDrawables.mShowing[2]);
            applyCompoundDrawableTint();
        }
    }

    private void prepareDrawableForDisplay(Drawable dr) {
        if (dr == null) {
            return;
        }
        dr.setLayoutDirection(getLayoutDirection());
        if (dr.isStateful()) {
            dr.setState(getDrawableState());
            dr.jumpToCurrentState();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void resetResolvedDrawables() {
        super.resetResolvedDrawables();
        this.mLastLayoutDirection = -1;
    }

    protected void viewClicked(InputMethodManager imm) {
        if (imm != null) {
            imm.viewClicked(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void deleteText_internal(int start, int end) {
        ((Editable) this.mText).delete(start, end);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void replaceText_internal(int start, int end, CharSequence text) {
        ((Editable) this.mText).replace(start, end, text);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setSpan_internal(Object span, int start, int end, int flags) {
        ((Editable) this.mText).setSpan(span, start, end, flags);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCursorPosition_internal(int start, int end) {
        Selection.setSelection((Editable) this.mText, start, end);
    }

    private void createEditorIfNeeded() {
        if (this.mEditor == null) {
            this.mEditor = new Editor(this);
        }
    }

    @Override // android.view.View
    public CharSequence getIterableTextForAccessibility() {
        return this.mText;
    }

    private void ensureIterableTextForAccessibilitySelectable() {
        CharSequence charSequence = this.mText;
        if (!(charSequence instanceof Spannable)) {
            setText(charSequence, BufferType.SPANNABLE);
        }
    }

    @Override // android.view.View
    public AccessibilityIterators.TextSegmentIterator getIteratorForGranularity(int granularity) {
        switch (granularity) {
            case 4:
                Spannable text = (Spannable) getIterableTextForAccessibility();
                if (!TextUtils.isEmpty(text) && getLayout() != null) {
                    AccessibilityIterators.LineTextSegmentIterator iterator = AccessibilityIterators.LineTextSegmentIterator.getInstance();
                    iterator.initialize(text, getLayout());
                    return iterator;
                }
                break;
            case 16:
                if (!TextUtils.isEmpty((Spannable) getIterableTextForAccessibility()) && getLayout() != null) {
                    AccessibilityIterators.PageTextSegmentIterator iterator2 = AccessibilityIterators.PageTextSegmentIterator.getInstance();
                    iterator2.initialize(this);
                    return iterator2;
                }
                break;
        }
        return super.getIteratorForGranularity(granularity);
    }

    @Override // android.view.View
    public int getAccessibilitySelectionStart() {
        return getSelectionStart();
    }

    @Override // android.view.View
    public boolean isAccessibilitySelectionExtendable() {
        return true;
    }

    @Override // android.view.View
    public void prepareForExtendedAccessibilitySelection() {
        requestFocusOnNonEditableSelectableText();
    }

    @Override // android.view.View
    public int getAccessibilitySelectionEnd() {
        return getSelectionEnd();
    }

    @Override // android.view.View
    public void setAccessibilitySelection(int start, int end) {
        if (getAccessibilitySelectionStart() == start && getAccessibilitySelectionEnd() == end) {
            return;
        }
        CharSequence text = getIterableTextForAccessibility();
        if (Math.min(start, end) >= 0 && Math.max(start, end) <= text.length()) {
            Selection.setSelection((Spannable) text, start, end);
        } else {
            Selection.removeSelection((Spannable) text);
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.hideCursorAndSpanControllers();
            this.mEditor.lambda$startActionModeInternal$0();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void encodeProperties(ViewHierarchyEncoder stream) {
        super.encodeProperties(stream);
        TextUtils.TruncateAt ellipsize = getEllipsize();
        stream.addProperty("text:ellipsize", ellipsize == null ? null : ellipsize.name());
        stream.addProperty("text:textSize", getTextSize());
        stream.addProperty("text:scaledTextSize", getScaledTextSize());
        stream.addProperty("text:typefaceStyle", getTypefaceStyle());
        stream.addProperty("text:selectionStart", getSelectionStart());
        stream.addProperty("text:selectionEnd", getSelectionEnd());
        stream.addProperty("text:curTextColor", this.mCurTextColor);
        CharSequence charSequence = this.mText;
        stream.addUserProperty("text:text", charSequence != null ? charSequence.toString() : null);
        stream.addProperty("text:gravity", this.mGravity);
    }

    /* loaded from: classes4.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.TextView.SavedState.1
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
        ParcelableParcel editorState;
        CharSequence error;
        boolean frozenWithFocus;
        int selEnd;
        int selStart;
        CharSequence text;

        SavedState(Parcelable superState) {
            super(superState);
            this.selStart = -1;
            this.selEnd = -1;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.selStart);
            out.writeInt(this.selEnd);
            out.writeInt(this.frozenWithFocus ? 1 : 0);
            TextUtils.writeToParcel(this.text, out, flags);
            if (this.error == null) {
                out.writeInt(0);
            } else {
                out.writeInt(1);
                TextUtils.writeToParcel(this.error, out, flags);
            }
            if (this.editorState == null) {
                out.writeInt(0);
                return;
            }
            out.writeInt(1);
            this.editorState.writeToParcel(out, flags);
        }

        public String toString() {
            String str = "TextView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " start=" + this.selStart + " end=" + this.selEnd;
            if (this.text != null) {
                str = str + " text=" + ((Object) this.text);
            }
            return str + "}";
        }

        private SavedState(Parcel in) {
            super(in);
            this.selStart = -1;
            this.selEnd = -1;
            this.selStart = in.readInt();
            this.selEnd = in.readInt();
            this.frozenWithFocus = in.readInt() != 0;
            this.text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            if (in.readInt() != 0) {
                this.error = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            }
            if (in.readInt() != 0) {
                this.editorState = ParcelableParcel.CREATOR.createFromParcel(in);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class CharWrapper implements CharSequence, GetChars, GraphicsOperations {
        private char[] mChars;
        private int mLength;
        private int mStart;

        CharWrapper(char[] chars, int start, int len) {
            this.mChars = chars;
            this.mStart = start;
            this.mLength = len;
        }

        void set(char[] chars, int start, int len) {
            this.mChars = chars;
            this.mStart = start;
            this.mLength = len;
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.mLength;
        }

        @Override // java.lang.CharSequence
        public char charAt(int off) {
            return this.mChars[this.mStart + off];
        }

        @Override // java.lang.CharSequence
        public String toString() {
            return new String(this.mChars, this.mStart, this.mLength);
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int start, int end) {
            int i;
            if (start < 0 || end < 0 || start > (i = this.mLength) || end > i) {
                throw new IndexOutOfBoundsException(start + ", " + end);
            }
            return new String(this.mChars, this.mStart + start, end - start);
        }

        @Override // android.text.GetChars
        public void getChars(int start, int end, char[] buf, int off) {
            int i;
            if (start < 0 || end < 0 || start > (i = this.mLength) || end > i) {
                throw new IndexOutOfBoundsException(start + ", " + end);
            }
            System.arraycopy(this.mChars, this.mStart + start, buf, off, end - start);
        }

        @Override // android.text.GraphicsOperations
        public void drawText(BaseCanvas c, int start, int end, float x, float y, Paint p) {
            c.drawText(this.mChars, start + this.mStart, end - start, x, y, p);
        }

        @Override // android.text.GraphicsOperations
        public void drawTextRun(BaseCanvas c, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, Paint p) {
            int count = end - start;
            int contextCount = contextEnd - contextStart;
            char[] cArr = this.mChars;
            int i = this.mStart;
            c.drawTextRun(cArr, start + i, count, contextStart + i, contextCount, x, y, isRtl, p);
        }

        @Override // android.text.GraphicsOperations
        public float measureText(int start, int end, Paint p) {
            return p.measureText(this.mChars, this.mStart + start, end - start);
        }

        @Override // android.text.GraphicsOperations
        public int getTextWidths(int start, int end, float[] widths, Paint p) {
            return p.getTextWidths(this.mChars, this.mStart + start, end - start, widths);
        }

        @Override // android.text.GraphicsOperations
        public float getTextRunAdvances(int start, int end, int contextStart, int contextEnd, boolean isRtl, float[] advances, int advancesIndex, Paint p) {
            int count = end - start;
            int contextCount = contextEnd - contextStart;
            char[] cArr = this.mChars;
            int i = this.mStart;
            return p.getTextRunAdvances(cArr, start + i, count, contextStart + i, contextCount, isRtl, advances, advancesIndex);
        }

        @Override // android.text.GraphicsOperations
        public int getTextRunCursor(int contextStart, int contextEnd, boolean isRtl, int offset, int cursorOpt, Paint p) {
            int contextCount = contextEnd - contextStart;
            char[] cArr = this.mChars;
            int i = this.mStart;
            return p.getTextRunCursor(cArr, contextStart + i, contextCount, isRtl, offset + i, cursorOpt);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class Marquee {
        private static final int MARQUEE_DELAY = 1200;
        private static final float MARQUEE_DELTA_MAX = 0.07f;
        private static final int MARQUEE_DP_PER_SECOND = 30;
        private static final byte MARQUEE_RUNNING = 2;
        private static final byte MARQUEE_STARTING = 1;
        private static final byte MARQUEE_STOPPED = 0;
        private final Choreographer mChoreographer;
        private float mFadeStop;
        private float mGhostOffset;
        private float mGhostStart;
        private long mLastAnimationMs;
        private float mMaxFadeScroll;
        private float mMaxScroll;
        private final float mPixelsPerMs;
        private int mRepeatLimit;
        private float mScroll;
        private final WeakReference<TextView> mView;
        private byte mStatus = 0;
        private Choreographer.FrameCallback mTickCallback = new Choreographer.FrameCallback() { // from class: android.widget.TextView.Marquee.1
            @Override // android.view.Choreographer.FrameCallback
            public void doFrame(long frameTimeNanos) {
                Marquee.this.tick();
            }
        };
        private Choreographer.FrameCallback mStartCallback = new Choreographer.FrameCallback() { // from class: android.widget.TextView.Marquee.2
            @Override // android.view.Choreographer.FrameCallback
            public void doFrame(long frameTimeNanos) {
                Marquee.this.mStatus = (byte) 2;
                Marquee marquee = Marquee.this;
                marquee.mLastAnimationMs = marquee.mChoreographer.getFrameTime();
                Marquee.this.tick();
            }
        };
        private Choreographer.FrameCallback mRestartCallback = new Choreographer.FrameCallback() { // from class: android.widget.TextView.Marquee.3
            @Override // android.view.Choreographer.FrameCallback
            public void doFrame(long frameTimeNanos) {
                if (Marquee.this.mStatus == 2) {
                    if (Marquee.this.mRepeatLimit >= 0) {
                        Marquee marquee = Marquee.this;
                        marquee.mRepeatLimit--;
                    }
                    Marquee marquee2 = Marquee.this;
                    marquee2.start(marquee2.mRepeatLimit);
                }
            }
        };

        Marquee(TextView v) {
            float density = v.getContext().getResources().getDisplayMetrics().density;
            this.mPixelsPerMs = (30.0f * density) / 1000.0f;
            this.mView = new WeakReference<>(v);
            this.mChoreographer = Choreographer.getInstance();
        }

        void tick() {
            if (this.mStatus != 2) {
                return;
            }
            this.mChoreographer.removeFrameCallback(this.mTickCallback);
            TextView textView = this.mView.get();
            if (textView != null && textView.isAggregatedVisible()) {
                if (textView.isFocused() || textView.isSelected()) {
                    long currentMs = this.mChoreographer.getFrameTime();
                    long deltaMs = currentMs - this.mLastAnimationMs;
                    this.mLastAnimationMs = currentMs;
                    float deltaPx = ((float) deltaMs) * this.mPixelsPerMs;
                    float f = this.mScroll + deltaPx;
                    this.mScroll = f;
                    float f2 = this.mMaxScroll;
                    if (f > f2) {
                        this.mScroll = f2;
                        this.mChoreographer.postFrameCallbackDelayed(this.mRestartCallback, 1200L);
                    } else {
                        this.mChoreographer.postFrameCallback(this.mTickCallback);
                    }
                    textView.invalidate();
                }
            }
        }

        void stop() {
            this.mStatus = (byte) 0;
            this.mChoreographer.removeFrameCallback(this.mStartCallback);
            this.mChoreographer.removeFrameCallback(this.mRestartCallback);
            this.mChoreographer.removeFrameCallback(this.mTickCallback);
            resetScroll();
        }

        private void resetScroll() {
            this.mScroll = 0.0f;
            TextView textView = this.mView.get();
            if (textView != null) {
                textView.invalidate();
            }
        }

        void start(int repeatLimit) {
            if (repeatLimit == 0) {
                stop();
                return;
            }
            this.mRepeatLimit = repeatLimit;
            TextView textView = this.mView.get();
            if (textView != null && textView.mLayout != null) {
                this.mStatus = (byte) 1;
                this.mScroll = 0.0f;
                int textWidth = (textView.getWidth() - textView.getCompoundPaddingLeft()) - textView.getCompoundPaddingRight();
                float lineWidth = textView.mLayout.getLineWidth(0);
                float gap = textWidth / 3.0f;
                float f = (lineWidth - textWidth) + gap;
                this.mGhostStart = f;
                this.mMaxScroll = textWidth + f;
                this.mGhostOffset = lineWidth + gap;
                this.mFadeStop = (textWidth / 6.0f) + lineWidth;
                this.mMaxFadeScroll = f + lineWidth + lineWidth;
                textView.invalidate();
                this.mChoreographer.postFrameCallback(this.mStartCallback);
            }
        }

        float getGhostOffset() {
            return this.mGhostOffset;
        }

        float getScroll() {
            return this.mScroll;
        }

        float getMaxFadeScroll() {
            return this.mMaxFadeScroll;
        }

        boolean shouldDrawLeftFade() {
            return this.mScroll <= this.mFadeStop;
        }

        boolean shouldDrawGhost() {
            return this.mStatus == 2 && this.mScroll > this.mGhostStart;
        }

        boolean isRunning() {
            return this.mStatus == 2;
        }

        boolean isStopped() {
            return this.mStatus == 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ChangeWatcher implements TextWatcher, SpanWatcher {
        private CharSequence mBeforeText;

        private ChangeWatcher() {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence buffer, int start, int before, int after) {
            if (AccessibilityManager.getInstance(TextView.this.mContext).isEnabled() && TextView.this.mTransformed != null) {
                this.mBeforeText = TextView.this.mTransformed.toString();
            }
            TextView.this.sendBeforeTextChanged(buffer, start, before, after);
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence buffer, int start, int before, int after) {
            TextView.this.handleTextChanged(buffer, start, before, after);
            if (TextView.this.isVisibleToAccessibility()) {
                TextView.this.sendAccessibilityEventTypeViewTextChanged(this.mBeforeText, start, before, after);
                this.mBeforeText = null;
            }
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable buffer) {
            TextView.this.sendAfterTextChanged(buffer);
            if (MetaKeyKeyListener.getMetaState(buffer, 2048) != 0) {
                MetaKeyKeyListener.stopSelecting(TextView.this, buffer);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanChanged(Spannable buf, Object what, int s, int e, int st, int en) {
            TextView.this.spanChange(buf, what, s, st, e, en);
        }

        @Override // android.text.SpanWatcher
        public void onSpanAdded(Spannable buf, Object what, int s, int e) {
            TextView.this.spanChange(buf, what, -1, s, -1, e);
        }

        @Override // android.text.SpanWatcher
        public void onSpanRemoved(Spannable buf, Object what, int s, int e) {
            TextView.this.spanChange(buf, what, s, -1, e, -1);
        }
    }

    @Override // android.view.View
    public void onInputConnectionOpenedInternal(InputConnection ic, EditorInfo editorInfo, Handler handler) {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.getDefaultOnReceiveContentListener().setInputConnectionInfo(this, ic, editorInfo);
        }
    }

    @Override // android.view.View
    public void onInputConnectionClosedInternal() {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.getDefaultOnReceiveContentListener().clearInputConnectionInfo();
        }
    }

    @Override // android.view.View
    public ContentInfo onReceiveContent(ContentInfo payload) {
        Editor editor = this.mEditor;
        if (editor != null) {
            return editor.getDefaultOnReceiveContentListener().onReceiveContent(this, payload);
        }
        return payload;
    }

    private static void logCursor(String location, String msgFormat, Object... msgArgs) {
        if (msgFormat != null) {
            Log.m112d(LOG_TAG, location + ": " + String.format(msgFormat, msgArgs));
        } else {
            Log.m112d(LOG_TAG, location);
        }
    }

    @Override // android.view.View
    public void onCreateViewTranslationRequest(int[] supportedFormats, Consumer<ViewTranslationRequest> requestsCollector) {
        if (supportedFormats == null || supportedFormats.length == 0) {
            if (UiTranslationController.DEBUG) {
                Log.m104w(LOG_TAG, "Do not provide the support translation formats.");
                return;
            }
            return;
        }
        ViewTranslationRequest.Builder requestBuilder = new ViewTranslationRequest.Builder(getAutofillId());
        boolean isPassword = true;
        if (ArrayUtils.contains(supportedFormats, 1)) {
            CharSequence charSequence = this.mText;
            if (charSequence == null || charSequence.length() == 0) {
                if (UiTranslationController.DEBUG) {
                    Log.m104w(LOG_TAG, "Cannot create translation request for the empty text.");
                    return;
                }
                return;
            }
            if (!isAnyPasswordInputType() && !hasPasswordTransformationMethod()) {
                isPassword = false;
            }
            if (isTextEditable() || isPassword) {
                Log.m104w(LOG_TAG, "Cannot create translation request. editable = " + isTextEditable() + ", isPassword = " + isPassword);
                return;
            }
            requestBuilder.setValue(ViewTranslationRequest.ID_TEXT, TranslationRequestValue.forText(this.mText));
            if (!TextUtils.isEmpty(getContentDescription())) {
                requestBuilder.setValue(ViewTranslationRequest.ID_CONTENT_DESCRIPTION, TranslationRequestValue.forText(getContentDescription()));
            }
        }
        requestsCollector.accept(requestBuilder.build());
    }
}
