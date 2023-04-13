package android.view;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.annotation.SystemApi;
import android.app.slice.Slice;
import android.content.AutofillOptions;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.p001pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Interpolator;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.display.DisplayManagerGlobal;
import android.hardware.usb.UsbManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaMetrics;
import android.media.TtmlUtils;
import android.media.audio.common.AudioChannelLayout;
import android.p008os.BatteryStats;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Message;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.SystemClock;
import android.p008os.Trace;
import android.provider.CalendarContract;
import android.provider.Downloads;
import android.security.keystore.KeyProperties;
import android.sysprop.DisplayProperties;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.LongSparseLongArray;
import android.util.Pair;
import android.util.Pools;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.util.SuperNotCalledException;
import android.util.TypedValue;
import android.view.AccessibilityIterators;
import android.view.ActionMode;
import android.view.ContentInfo;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.SurfaceControl;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsetsAnimation;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityEventSource;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeIdManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.autofill.AutofillFeatureFlags;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.view.contentcapture.ContentCaptureManager;
import android.view.contentcapture.ContentCaptureSession;
import android.view.displayhash.DisplayHash;
import android.view.displayhash.DisplayHashResultCallback;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.IntFlagMapping;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.view.translation.TranslationCapability;
import android.view.translation.ViewTranslationCallback;
import android.view.translation.ViewTranslationRequest;
import android.view.translation.ViewTranslationResponse;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ScrollBarDrawable;
import android.window.OnBackInvokedDispatcher;
import com.android.internal.C4057R;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.Preconditions;
import com.android.internal.view.ScrollCaptureInternal;
import com.android.internal.view.TooltipPopup;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.widget.ScrollBarUtils;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public class View implements Drawable.Callback, KeyEvent.Callback, AccessibilityEventSource {
    public static final int ACCESSIBILITY_CURSOR_POSITION_UNDEFINED = -1;
    public static final int ACCESSIBILITY_DATA_SENSITIVE_AUTO = 0;
    public static final int ACCESSIBILITY_DATA_SENSITIVE_NO = 2;
    public static final int ACCESSIBILITY_DATA_SENSITIVE_YES = 1;
    public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2;
    static final int ACCESSIBILITY_LIVE_REGION_DEFAULT = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1;
    static final int ALL_RTL_PROPERTIES_RESOLVED = 1610678816;
    public static final int AUTOFILL_FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 1;
    public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE = "creditCardExpirationDate";
    public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY = "creditCardExpirationDay";
    public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH = "creditCardExpirationMonth";
    public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR = "creditCardExpirationYear";
    public static final String AUTOFILL_HINT_CREDIT_CARD_NUMBER = "creditCardNumber";
    public static final String AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE = "creditCardSecurityCode";
    public static final String AUTOFILL_HINT_EMAIL_ADDRESS = "emailAddress";
    public static final String AUTOFILL_HINT_NAME = "name";
    public static final String AUTOFILL_HINT_PASSWORD = "password";
    public static final String AUTOFILL_HINT_PASSWORD_AUTO = "passwordAuto";
    public static final String AUTOFILL_HINT_PHONE = "phone";
    public static final String AUTOFILL_HINT_POSTAL_ADDRESS = "postalAddress";
    public static final String AUTOFILL_HINT_POSTAL_CODE = "postalCode";
    public static final String AUTOFILL_HINT_USERNAME = "username";
    private static final String AUTOFILL_LOG_TAG = "View.Autofill";
    public static final int AUTOFILL_TYPE_DATE = 4;
    public static final int AUTOFILL_TYPE_LIST = 3;
    public static final int AUTOFILL_TYPE_NONE = 0;
    public static final int AUTOFILL_TYPE_TEXT = 1;
    public static final int AUTOFILL_TYPE_TOGGLE = 2;
    static final int CLICKABLE = 16384;
    private static final String CONTENT_CAPTURE_LOG_TAG = "View.ContentCapture";
    static final int CONTEXT_CLICKABLE = 8388608;
    private static final boolean DBG = false;
    private static final boolean DEBUG_CONTENT_CAPTURE = false;
    static final int DEBUG_CORNERS_SIZE_DIP = 8;
    static final int DISABLED = 32;
    public static final int DRAG_FLAG_ACCESSIBILITY_ACTION = 1024;
    public static final int DRAG_FLAG_GLOBAL = 256;
    public static final int DRAG_FLAG_GLOBAL_PERSISTABLE_URI_PERMISSION = 64;
    public static final int DRAG_FLAG_GLOBAL_PREFIX_URI_PERMISSION = 128;
    public static final int DRAG_FLAG_GLOBAL_URI_READ = 1;
    public static final int DRAG_FLAG_GLOBAL_URI_WRITE = 2;
    public static final int DRAG_FLAG_OPAQUE = 512;
    public static final int DRAG_FLAG_REQUEST_SURFACE_FOR_RETURN_ANIMATION = 2048;
    static final int DRAG_MASK = 3;
    static final int DRAWING_CACHE_ENABLED = 32768;
    @Deprecated
    public static final int DRAWING_CACHE_QUALITY_AUTO = 0;
    @Deprecated
    public static final int DRAWING_CACHE_QUALITY_HIGH = 1048576;
    @Deprecated
    public static final int DRAWING_CACHE_QUALITY_LOW = 524288;
    static final int DRAWING_CACHE_QUALITY_MASK = 1572864;
    static final int DRAW_MASK = 128;
    static final int DUPLICATE_PARENT_STATE = 4194304;
    static final int ENABLED = 0;
    static final int ENABLED_MASK = 32;
    static final int FADING_EDGE_HORIZONTAL = 4096;
    static final int FADING_EDGE_MASK = 12288;
    static final int FADING_EDGE_NONE = 0;
    static final int FADING_EDGE_VERTICAL = 8192;
    static final int FILTER_TOUCHES_WHEN_OBSCURED = 1024;
    public static final int FIND_VIEWS_WITH_ACCESSIBILITY_NODE_PROVIDERS = 4;
    public static final int FIND_VIEWS_WITH_CONTENT_DESCRIPTION = 2;
    public static final int FIND_VIEWS_WITH_TEXT = 1;
    private static final int FITS_SYSTEM_WINDOWS = 2;
    public static final int FOCUSABLE = 1;
    public static final int FOCUSABLES_ALL = 0;
    public static final int FOCUSABLES_TOUCH_MODE = 1;
    public static final int FOCUSABLE_AUTO = 16;
    static final int FOCUSABLE_IN_TOUCH_MODE = 262144;
    private static final int FOCUSABLE_MASK = 17;
    public static final int FOCUS_BACKWARD = 1;
    public static final int FOCUS_DOWN = 130;
    public static final int FOCUS_FORWARD = 2;
    public static final int FOCUS_LEFT = 17;
    public static final int FOCUS_RIGHT = 66;
    public static final int FOCUS_UP = 33;
    public static final int GONE = 8;
    public static final int HAPTIC_FEEDBACK_ENABLED = 268435456;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
    static final int IMPORTANT_FOR_ACCESSIBILITY_DEFAULT = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
    public static final int IMPORTANT_FOR_AUTOFILL_AUTO = 0;
    public static final int IMPORTANT_FOR_AUTOFILL_NO = 2;
    public static final int IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS = 8;
    public static final int IMPORTANT_FOR_AUTOFILL_YES = 1;
    public static final int IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS = 4;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_AUTO = 0;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_NO = 2;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_NO_EXCLUDE_DESCENDANTS = 8;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_YES = 1;
    public static final int IMPORTANT_FOR_CONTENT_CAPTURE_YES_EXCLUDE_DESCENDANTS = 4;
    public static final int INVISIBLE = 4;
    public static final int KEEP_SCREEN_ON = 67108864;
    public static final int LAST_APP_AUTOFILL_ID = 1073741823;
    public static final int LAYER_TYPE_HARDWARE = 2;
    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 1;
    private static final int LAYOUT_DIRECTION_DEFAULT = 2;
    public static final int LAYOUT_DIRECTION_INHERIT = 2;
    public static final int LAYOUT_DIRECTION_LOCALE = 3;
    public static final int LAYOUT_DIRECTION_LTR = 0;
    static final int LAYOUT_DIRECTION_RESOLVED_DEFAULT = 0;
    public static final int LAYOUT_DIRECTION_RTL = 1;
    public static final int LAYOUT_DIRECTION_UNDEFINED = -1;
    static final int LONG_CLICKABLE = 2097152;
    public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
    public static final int MEASURED_SIZE_MASK = 16777215;
    public static final int MEASURED_STATE_MASK = -16777216;
    public static final int MEASURED_STATE_TOO_SMALL = 16777216;
    public static final int NOT_FOCUSABLE = 0;
    public static final int NO_ID = -1;
    static final int OPTIONAL_FITS_SYSTEM_WINDOWS = 2048;
    public static final int OVER_SCROLL_ALWAYS = 0;
    public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
    public static final int OVER_SCROLL_NEVER = 2;
    static final int PARENT_SAVE_DISABLED = 536870912;
    static final int PARENT_SAVE_DISABLED_MASK = 536870912;
    static final int PFLAG2_ACCESSIBILITY_FOCUSED = 67108864;
    static final int PFLAG2_ACCESSIBILITY_LIVE_REGION_MASK = 25165824;
    static final int PFLAG2_ACCESSIBILITY_LIVE_REGION_SHIFT = 23;
    static final int PFLAG2_DRAG_CAN_ACCEPT = 1;
    static final int PFLAG2_DRAG_HOVERED = 2;
    static final int PFLAG2_DRAWABLE_RESOLVED = 1073741824;
    static final int PFLAG2_HAS_TRANSIENT_STATE = Integer.MIN_VALUE;
    static final int PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK = 7340032;
    static final int PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_SHIFT = 20;
    static final int PFLAG2_LAYOUT_DIRECTION_MASK = 12;
    static final int PFLAG2_LAYOUT_DIRECTION_MASK_SHIFT = 2;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED = 32;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_MASK = 48;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_RTL = 16;
    static final int PFLAG2_PADDING_RESOLVED = 536870912;
    static final int PFLAG2_SUBTREE_ACCESSIBILITY_STATE_CHANGED = 134217728;
    static final int PFLAG2_TEXT_ALIGNMENT_MASK = 57344;
    static final int PFLAG2_TEXT_ALIGNMENT_MASK_SHIFT = 13;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED = 65536;
    private static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_DEFAULT = 131072;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK = 917504;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT = 17;
    static final int PFLAG2_TEXT_DIRECTION_MASK = 448;
    static final int PFLAG2_TEXT_DIRECTION_MASK_SHIFT = 6;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED = 512;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_DEFAULT = 1024;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_MASK = 7168;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT = 10;
    static final int PFLAG2_VIEW_QUICK_REJECTED = 268435456;
    private static final int PFLAG3_ACCESSIBILITY_HEADING = Integer.MIN_VALUE;
    private static final int PFLAG3_AGGREGATED_VISIBLE = 536870912;
    static final int PFLAG3_APPLYING_INSETS = 32;
    static final int PFLAG3_ASSIST_BLOCKED = 16384;
    private static final int PFLAG3_AUTOFILLID_EXPLICITLY_SET = 1073741824;
    static final int PFLAG3_CALLED_SUPER = 16;
    private static final int PFLAG3_CLUSTER = 32768;
    private static final int PFLAG3_FINGER_DOWN = 131072;
    static final int PFLAG3_FITTING_SYSTEM_WINDOWS = 64;
    private static final int PFLAG3_FOCUSED_BY_DEFAULT = 262144;
    private static final int PFLAG3_HAS_OVERLAPPING_RENDERING_FORCED = 16777216;
    static final int PFLAG3_IMPORTANT_FOR_AUTOFILL_MASK = 7864320;
    static final int PFLAG3_IMPORTANT_FOR_AUTOFILL_SHIFT = 19;
    private static final int PFLAG3_IS_AUTOFILLED = 65536;
    static final int PFLAG3_IS_LAID_OUT = 4;
    static final int PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT = 8;
    static final int PFLAG3_NESTED_SCROLLING_ENABLED = 128;
    static final int PFLAG3_NOTIFY_AUTOFILL_ENTER_ON_LAYOUT = 134217728;
    private static final int PFLAG3_NO_REVEAL_ON_FOCUS = 67108864;
    private static final int PFLAG3_OVERLAPPING_RENDERING_FORCED_VALUE = 8388608;
    private static final int PFLAG3_SCREEN_READER_FOCUSABLE = 268435456;
    static final int PFLAG3_SCROLL_INDICATOR_BOTTOM = 512;
    static final int PFLAG3_SCROLL_INDICATOR_END = 8192;
    static final int PFLAG3_SCROLL_INDICATOR_LEFT = 1024;
    static final int PFLAG3_SCROLL_INDICATOR_RIGHT = 2048;
    static final int PFLAG3_SCROLL_INDICATOR_START = 4096;
    static final int PFLAG3_SCROLL_INDICATOR_TOP = 256;
    static final int PFLAG3_TEMPORARY_DETACH = 33554432;
    static final int PFLAG3_VIEW_IS_ANIMATING_ALPHA = 2;
    static final int PFLAG3_VIEW_IS_ANIMATING_TRANSFORM = 1;
    private static final int PFLAG4_ALLOW_CLICK_WHEN_DISABLED = 4096;
    private static final int PFLAG4_AUTOFILL_HIDE_HIGHLIGHT = 512;
    private static final int PFLAG4_AUTO_HANDWRITING_ENABLED = 65536;
    private static final int PFLAG4_CONTENT_CAPTURE_IMPORTANCE_CACHED_VALUE = 128;
    private static final int PFLAG4_CONTENT_CAPTURE_IMPORTANCE_IS_CACHED = 64;
    private static final int PFLAG4_CONTENT_CAPTURE_IMPORTANCE_MASK = 192;
    private static final int PFLAG4_DETACHED = 8192;
    private static final int PFLAG4_DRAG_A11Y_STARTED = 32768;
    static final int PFLAG4_FRAMEWORK_OPTIONAL_FITS_SYSTEM_WINDOWS = 256;
    private static final int PFLAG4_HAS_TRANSLATION_TRANSIENT_STATE = 16384;
    private static final int PFLAG4_IMPORTANT_FOR_CONTENT_CAPTURE_MASK = 15;
    private static final int PFLAG4_IMPORTANT_FOR_CREDENTIAL_MANAGER = 131072;
    private static final int PFLAG4_NOTIFIED_CONTENT_CAPTURE_APPEARED = 16;
    private static final int PFLAG4_NOTIFIED_CONTENT_CAPTURE_DISAPPEARED = 32;
    private static final int PFLAG4_RELAYOUT_TRACING_ENABLED = 524288;
    static final int PFLAG4_SCROLL_CAPTURE_HINT_MASK = 7168;
    static final int PFLAG4_SCROLL_CAPTURE_HINT_SHIFT = 10;
    private static final int PFLAG4_TRAVERSAL_TRACING_ENABLED = 262144;
    static final int PFLAG_ACTIVATED = 1073741824;
    static final int PFLAG_ALPHA_SET = 262144;
    static final int PFLAG_ANIMATION_STARTED = 65536;
    private static final int PFLAG_AWAKEN_SCROLL_BARS_ON_ATTACH = 134217728;
    static final int PFLAG_CANCEL_NEXT_UP_EVENT = 67108864;
    static final int PFLAG_DIRTY = 2097152;
    static final int PFLAG_DIRTY_MASK = 2097152;
    static final int PFLAG_DRAWABLE_STATE_DIRTY = 1024;
    static final int PFLAG_DRAWING_CACHE_VALID = 32768;
    static final int PFLAG_DRAWN = 32;
    static final int PFLAG_DRAW_ANIMATION = 64;
    static final int PFLAG_FOCUSED = 2;
    static final int PFLAG_FORCE_LAYOUT = 4096;
    static final int PFLAG_HAS_BOUNDS = 16;
    private static final int PFLAG_HOVERED = 268435456;
    static final int PFLAG_INVALIDATED = Integer.MIN_VALUE;
    static final int PFLAG_IS_ROOT_NAMESPACE = 8;
    static final int PFLAG_LAYOUT_REQUIRED = 8192;
    static final int PFLAG_MEASURED_DIMENSION_SET = 2048;
    private static final int PFLAG_NOTIFY_AUTOFILL_MANAGER_ON_CLICK = 536870912;
    static final int PFLAG_OPAQUE_BACKGROUND = 8388608;
    static final int PFLAG_OPAQUE_MASK = 25165824;
    static final int PFLAG_OPAQUE_SCROLLBARS = 16777216;
    private static final int PFLAG_PREPRESSED = 33554432;
    private static final int PFLAG_PRESSED = 16384;
    static final int PFLAG_REQUEST_TRANSPARENT_REGIONS = 512;
    private static final int PFLAG_SAVE_STATE_CALLED = 131072;
    static final int PFLAG_SCROLL_CONTAINER = 524288;
    static final int PFLAG_SCROLL_CONTAINER_ADDED = 1048576;
    static final int PFLAG_SELECTED = 4;
    static final int PFLAG_SKIP_DRAW = 128;
    static final int PFLAG_WANTS_FOCUS = 1;
    private static final int POPULATING_ACCESSIBILITY_EVENT_TYPES = 172479;
    private static final int PROVIDER_BACKGROUND = 0;
    private static final int PROVIDER_BOUNDS = 2;
    private static final int PROVIDER_NONE = 1;
    private static final int PROVIDER_PADDED_BOUNDS = 3;
    public static final int PUBLIC_STATUS_BAR_VISIBILITY_MASK = 16375;
    static final int SAVE_DISABLED = 65536;
    static final int SAVE_DISABLED_MASK = 65536;
    public static final int SCREEN_STATE_OFF = 0;
    public static final int SCREEN_STATE_ON = 1;
    static final int SCROLLBARS_HORIZONTAL = 256;
    static final int SCROLLBARS_INSET_MASK = 16777216;
    public static final int SCROLLBARS_INSIDE_INSET = 16777216;
    public static final int SCROLLBARS_INSIDE_OVERLAY = 0;
    static final int SCROLLBARS_MASK = 768;
    static final int SCROLLBARS_NONE = 0;
    public static final int SCROLLBARS_OUTSIDE_INSET = 50331648;
    static final int SCROLLBARS_OUTSIDE_MASK = 33554432;
    public static final int SCROLLBARS_OUTSIDE_OVERLAY = 33554432;
    static final int SCROLLBARS_STYLE_MASK = 50331648;
    static final int SCROLLBARS_VERTICAL = 512;
    public static final int SCROLLBAR_POSITION_DEFAULT = 0;
    public static final int SCROLLBAR_POSITION_LEFT = 1;
    public static final int SCROLLBAR_POSITION_RIGHT = 2;
    public static final int SCROLL_AXIS_HORIZONTAL = 1;
    public static final int SCROLL_AXIS_NONE = 0;
    public static final int SCROLL_AXIS_VERTICAL = 2;
    public static final int SCROLL_CAPTURE_HINT_AUTO = 0;
    public static final int SCROLL_CAPTURE_HINT_EXCLUDE = 1;
    public static final int SCROLL_CAPTURE_HINT_EXCLUDE_DESCENDANTS = 4;
    public static final int SCROLL_CAPTURE_HINT_INCLUDE = 2;
    static final int SCROLL_INDICATORS_NONE = 0;
    static final int SCROLL_INDICATORS_PFLAG3_MASK = 16128;
    static final int SCROLL_INDICATORS_TO_PFLAGS3_LSHIFT = 8;
    public static final int SCROLL_INDICATOR_BOTTOM = 2;
    public static final int SCROLL_INDICATOR_END = 32;
    public static final int SCROLL_INDICATOR_LEFT = 4;
    public static final int SCROLL_INDICATOR_RIGHT = 8;
    public static final int SCROLL_INDICATOR_START = 16;
    public static final int SCROLL_INDICATOR_TOP = 1;
    public static final int SOUND_EFFECTS_ENABLED = 134217728;
    public static final int STATUS_BAR_DISABLE_BACK = 4194304;
    public static final int STATUS_BAR_DISABLE_CLOCK = 8388608;
    public static final int STATUS_BAR_DISABLE_EXPAND = 65536;
    public static final int STATUS_BAR_DISABLE_HOME = 2097152;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_ALERTS = 262144;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_ICONS = 131072;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_TICKER = 524288;
    public static final int STATUS_BAR_DISABLE_ONGOING_CALL_CHIP = 67108864;
    public static final int STATUS_BAR_DISABLE_RECENT = 16777216;
    public static final int STATUS_BAR_DISABLE_SEARCH = 33554432;
    public static final int STATUS_BAR_DISABLE_SYSTEM_INFO = 1048576;
    @Deprecated
    public static final int STATUS_BAR_HIDDEN = 1;
    @Deprecated
    public static final int STATUS_BAR_VISIBLE = 0;
    public static final int SYSTEM_UI_CLEARABLE_FLAGS = 7;
    @Deprecated
    public static final int SYSTEM_UI_FLAG_FULLSCREEN = 4;
    @Deprecated
    public static final int SYSTEM_UI_FLAG_HIDE_NAVIGATION = 2;
    @Deprecated
    public static final int SYSTEM_UI_FLAG_IMMERSIVE = 2048;
    @Deprecated
    public static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 4096;
    @Deprecated
    public static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 1024;
    public static final int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 512;
    @Deprecated
    public static final int SYSTEM_UI_FLAG_LAYOUT_STABLE = 256;
    @Deprecated
    public static final int SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR = 16;
    @Deprecated
    public static final int SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 8192;
    @Deprecated
    public static final int SYSTEM_UI_FLAG_LOW_PROFILE = 1;
    @Deprecated
    public static final int SYSTEM_UI_FLAG_VISIBLE = 0;
    @Deprecated
    public static final int SYSTEM_UI_LAYOUT_FLAGS = 1536;
    private static final int SYSTEM_UI_RESERVED_LEGACY1 = 16384;
    private static final int SYSTEM_UI_RESERVED_LEGACY2 = 65536;
    public static final int TEXT_ALIGNMENT_CENTER = 4;
    private static final int TEXT_ALIGNMENT_DEFAULT = 1;
    public static final int TEXT_ALIGNMENT_GRAVITY = 1;
    public static final int TEXT_ALIGNMENT_INHERIT = 0;
    static final int TEXT_ALIGNMENT_RESOLVED_DEFAULT = 1;
    public static final int TEXT_ALIGNMENT_TEXT_END = 3;
    public static final int TEXT_ALIGNMENT_TEXT_START = 2;
    public static final int TEXT_ALIGNMENT_VIEW_END = 6;
    public static final int TEXT_ALIGNMENT_VIEW_START = 5;
    public static final int TEXT_DIRECTION_ANY_RTL = 2;
    private static final int TEXT_DIRECTION_DEFAULT = 0;
    public static final int TEXT_DIRECTION_FIRST_STRONG = 1;
    public static final int TEXT_DIRECTION_FIRST_STRONG_LTR = 6;
    public static final int TEXT_DIRECTION_FIRST_STRONG_RTL = 7;
    public static final int TEXT_DIRECTION_INHERIT = 0;
    public static final int TEXT_DIRECTION_LOCALE = 5;
    public static final int TEXT_DIRECTION_LTR = 3;
    static final int TEXT_DIRECTION_RESOLVED_DEFAULT = 1;
    public static final int TEXT_DIRECTION_RTL = 4;
    static final int TOOLTIP = 1073741824;
    private static final int UNDEFINED_PADDING = Integer.MIN_VALUE;
    protected static final String VIEW_LOG_TAG = "View";
    protected static final int VIEW_STRUCTURE_FOR_ASSIST = 0;
    protected static final int VIEW_STRUCTURE_FOR_AUTOFILL = 1;
    protected static final int VIEW_STRUCTURE_FOR_CONTENT_CAPTURE = 2;
    static final int VISIBILITY_MASK = 12;
    public static final int VISIBLE = 0;
    static final int WILL_NOT_CACHE_DRAWING = 131072;
    static final int WILL_NOT_DRAW = 128;
    private static SparseArray<String> mAttributeMap;
    private static boolean sAcceptZeroSizeDragShadow;
    private static boolean sAlwaysAssignFocus;
    private static boolean sAutoFocusableOffUIThreadWontNotifyParents;
    static boolean sBrokenInsetsDispatch;
    protected static boolean sBrokenWindowBackground;
    private static boolean sCanFocusZeroSized;
    static boolean sCascadedDragDrop;
    private static Paint sDebugPaint;
    public static String sDebugViewAttributesApplicationPackage;
    static boolean sForceLayoutWhenInsetsChanged;
    static boolean sHasFocusableExcludeAutoFocusable;
    private static int sNextAccessibilityViewId;
    protected static boolean sPreserveMarginParamsInLayoutParamConversion;
    private static boolean sThrowOnInvalidFloatProperties;
    private static boolean sTraceLayoutSteps;
    private static String sTraceRequestLayoutClass;
    private static boolean sUseDefaultFocusHighlight;
    private int mAccessibilityCursorPosition;
    AccessibilityDelegate mAccessibilityDelegate;
    private CharSequence mAccessibilityPaneTitle;
    private int mAccessibilityTraversalAfterId;
    private int mAccessibilityTraversalBeforeId;
    private int mAccessibilityViewId;
    private String mAllowedHandwritingDelegatePackageName;
    private String mAllowedHandwritingDelegatorPackageName;
    private float mAmbiguousGestureMultiplier;
    private ViewPropertyAnimator mAnimator;
    AttachInfo mAttachInfo;
    private SparseArray<int[]> mAttributeResolutionStacks;
    private SparseIntArray mAttributeSourceResId;
    @ViewDebug.ExportedProperty(category = "attributes", hasAdjacentMapping = true)
    public String[] mAttributes;
    private String[] mAutofillHints;
    private AutofillId mAutofillId;
    private int mAutofillViewId;
    @ViewDebug.ExportedProperty(deepExport = true, prefix = "bg_")
    private Drawable mBackground;
    RenderNode mBackgroundRenderNode;
    private int mBackgroundResource;
    private boolean mBackgroundSizeChanged;
    private TintInfo mBackgroundTint;
    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    protected int mBottom;
    public boolean mCachingFailed;
    @ViewDebug.ExportedProperty(category = "drawing")
    Rect mClipBounds;
    private ContentCaptureSession mContentCaptureSession;
    private boolean mContentCaptureSessionCached;
    private CharSequence mContentDescription;
    @ViewDebug.ExportedProperty(deepExport = true)
    protected Context mContext;
    protected Animation mCurrentAnimation;
    private Drawable mDefaultFocusHighlight;
    private Drawable mDefaultFocusHighlightCache;
    boolean mDefaultFocusHighlightEnabled;
    private boolean mDefaultFocusHighlightSizeChanged;
    private int[] mDrawableState;
    private Bitmap mDrawingCache;
    private int mDrawingCacheBackgroundColor;
    private int mExplicitAccessibilityDataSensitive;
    private int mExplicitStyle;
    private ViewTreeObserver mFloatingTreeObserver;
    @ViewDebug.ExportedProperty(deepExport = true, prefix = "fg_")
    private ForegroundInfo mForegroundInfo;
    private ArrayList<FrameMetricsObserver> mFrameMetricsObservers;
    GhostView mGhostView;
    private float mHandwritingBoundsOffsetBottom;
    private float mHandwritingBoundsOffsetLeft;
    private float mHandwritingBoundsOffsetRight;
    private float mHandwritingBoundsOffsetTop;
    private Runnable mHandwritingDelegatorCallback;
    private boolean mHasPerformedLongPress;
    private boolean mHoveringTouchDelegate;
    @ViewDebug.ExportedProperty(resolveId = true)
    int mID;
    private boolean mIgnoreNextUpEvent;
    private boolean mInContextButtonPress;
    private int mInferredAccessibilityDataSensitive;
    protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    private boolean mIsHandwritingDelegate;
    private SparseArray<Object> mKeyedTags;
    private int mLabelForId;
    private boolean mLastIsOpaque;
    Paint mLayerPaint;
    @ViewDebug.ExportedProperty(category = "drawing", mapping = {@ViewDebug.IntToString(from = 0, m86to = KeyProperties.DIGEST_NONE), @ViewDebug.IntToString(from = 1, m86to = "SOFTWARE"), @ViewDebug.IntToString(from = 2, m86to = "HARDWARE")})
    int mLayerType;
    private Insets mLayoutInsets;
    protected ViewGroup.LayoutParams mLayoutParams;
    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    protected int mLeft;
    private boolean mLeftPaddingDefined;
    ListenerInfo mListenerInfo;
    private float mLongClickX;
    private float mLongClickY;
    private MatchIdPredicate mMatchIdPredicate;
    private MatchLabelForPredicate mMatchLabelForPredicate;
    private LongSparseLongArray mMeasureCache;
    @ViewDebug.ExportedProperty(category = "measurement")
    int mMeasuredHeight;
    @ViewDebug.ExportedProperty(category = "measurement")
    int mMeasuredWidth;
    @ViewDebug.ExportedProperty(category = "measurement")
    private int mMinHeight;
    @ViewDebug.ExportedProperty(category = "measurement")
    private int mMinWidth;
    private ViewParent mNestedScrollingParent;
    int mNextClusterForwardId;
    private int mNextFocusDownId;
    int mNextFocusForwardId;
    private int mNextFocusLeftId;
    private int mNextFocusRightId;
    private int mNextFocusUpId;
    int mOldHeightMeasureSpec;
    int mOldWidthMeasureSpec;
    ViewOutlineProvider mOutlineProvider;
    private int mOverScrollMode;
    ViewOverlay mOverlay;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingBottom;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingLeft;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingRight;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mPaddingTop;
    protected ViewParent mParent;
    private CheckForLongPress mPendingCheckForLongPress;
    private CheckForTap mPendingCheckForTap;
    private PerformClick mPerformClick;
    private PointerIcon mPointerIcon;
    @ViewDebug.ExportedProperty(flagMapping = {@ViewDebug.FlagToString(equals = 4096, mask = 4096, name = "FORCE_LAYOUT"), @ViewDebug.FlagToString(equals = 8192, mask = 8192, name = "LAYOUT_REQUIRED"), @ViewDebug.FlagToString(equals = 32768, mask = 32768, name = "DRAWING_CACHE_INVALID", outputIf = false), @ViewDebug.FlagToString(equals = 32, mask = 32, name = "DRAWN", outputIf = true), @ViewDebug.FlagToString(equals = 32, mask = 32, name = "NOT_DRAWN", outputIf = false), @ViewDebug.FlagToString(equals = 2097152, mask = 2097152, name = "DIRTY")}, formatToHexString = true)
    public int mPrivateFlags;
    int mPrivateFlags2;
    int mPrivateFlags3;
    private int mPrivateFlags4;
    private String[] mReceiveContentMimeTypes;
    boolean mRecreateDisplayList;
    final RenderNode mRenderNode;
    private final Resources mResources;
    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    protected int mRight;
    private boolean mRightPaddingDefined;
    private RoundScrollbarRenderer mRoundScrollbarRenderer;
    private HandlerActionQueue mRunQueue;
    private ScrollabilityCache mScrollCache;
    private Drawable mScrollIndicatorDrawable;
    @ViewDebug.ExportedProperty(category = "scrolling")
    protected int mScrollX;
    @ViewDebug.ExportedProperty(category = "scrolling")
    protected int mScrollY;
    private SendAccessibilityEventThrottle mSendStateChangedAccessibilityEvent;
    private SendViewScrolledAccessibilityEvent mSendViewScrolledAccessibilityEvent;
    private boolean mSendingHoverAccessibilityEvents;
    private boolean mShouldFakeFocus;
    private int mSourceLayoutId;
    String mStartActivityRequestWho;
    private CharSequence mStateDescription;
    private StateListAnimator mStateListAnimator;
    @ViewDebug.ExportedProperty(flagMapping = {@ViewDebug.FlagToString(equals = 1, mask = 1, name = "LOW_PROFILE"), @ViewDebug.FlagToString(equals = 2, mask = 2, name = "HIDE_NAVIGATION"), @ViewDebug.FlagToString(equals = 4, mask = 4, name = "FULLSCREEN"), @ViewDebug.FlagToString(equals = 256, mask = 256, name = "LAYOUT_STABLE"), @ViewDebug.FlagToString(equals = 512, mask = 512, name = "LAYOUT_HIDE_NAVIGATION"), @ViewDebug.FlagToString(equals = 1024, mask = 1024, name = "LAYOUT_FULLSCREEN"), @ViewDebug.FlagToString(equals = 2048, mask = 2048, name = "IMMERSIVE"), @ViewDebug.FlagToString(equals = 4096, mask = 4096, name = "IMMERSIVE_STICKY"), @ViewDebug.FlagToString(equals = 8192, mask = 8192, name = "LIGHT_STATUS_BAR"), @ViewDebug.FlagToString(equals = 16, mask = 16, name = "LIGHT_NAVIGATION_BAR"), @ViewDebug.FlagToString(equals = 65536, mask = 65536, name = "STATUS_BAR_DISABLE_EXPAND"), @ViewDebug.FlagToString(equals = 131072, mask = 131072, name = "STATUS_BAR_DISABLE_NOTIFICATION_ICONS"), @ViewDebug.FlagToString(equals = 262144, mask = 262144, name = "STATUS_BAR_DISABLE_NOTIFICATION_ALERTS"), @ViewDebug.FlagToString(equals = 524288, mask = 524288, name = "STATUS_BAR_DISABLE_NOTIFICATION_TICKER"), @ViewDebug.FlagToString(equals = 1048576, mask = 1048576, name = "STATUS_BAR_DISABLE_SYSTEM_INFO"), @ViewDebug.FlagToString(equals = 2097152, mask = 2097152, name = "STATUS_BAR_DISABLE_HOME"), @ViewDebug.FlagToString(equals = 4194304, mask = 4194304, name = "STATUS_BAR_DISABLE_BACK"), @ViewDebug.FlagToString(equals = 8388608, mask = 8388608, name = "STATUS_BAR_DISABLE_CLOCK"), @ViewDebug.FlagToString(equals = 16777216, mask = 16777216, name = "STATUS_BAR_DISABLE_RECENT"), @ViewDebug.FlagToString(equals = 33554432, mask = 33554432, name = "STATUS_BAR_DISABLE_SEARCH"), @ViewDebug.FlagToString(equals = 67108864, mask = 67108864, name = "STATUS_BAR_DISABLE_ONGOING_CALL_CHIP")}, formatToHexString = true)
    int mSystemUiVisibility;
    protected Object mTag;
    private int[] mTempNestedScrollConsumed;
    TooltipInfo mTooltipInfo;
    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    protected int mTop;
    private TouchDelegate mTouchDelegate;
    private int mTouchSlop;
    private ViewTraversalTracingStrings mTracingStrings;
    public TransformationInfo mTransformationInfo;
    int mTransientStateCount;
    private String mTransitionName;
    int mUnbufferedInputSource;
    private Bitmap mUnscaledDrawingCache;
    private UnsetPressedState mUnsetPressedState;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mUserPaddingBottom;
    @ViewDebug.ExportedProperty(category = "padding")
    int mUserPaddingEnd;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mUserPaddingLeft;
    int mUserPaddingLeftInitial;
    @ViewDebug.ExportedProperty(category = "padding")
    protected int mUserPaddingRight;
    int mUserPaddingRightInitial;
    @ViewDebug.ExportedProperty(category = "padding")
    int mUserPaddingStart;
    private float mVerticalScrollFactor;
    private int mVerticalScrollbarPosition;
    @ViewDebug.ExportedProperty(formatToHexString = true)
    int mViewFlags;
    private ViewTranslationCallback mViewTranslationCallback;
    private ViewTranslationResponse mViewTranslationResponse;
    private Handler mVisibilityChangeForAutofillHandler;
    int mWindowAttachCount;
    public static boolean DEBUG_DRAW = false;
    public static boolean sDebugViewAttributes = false;
    private static final int[] AUTOFILL_HIGHLIGHT_ATTR = {16844136};
    private static boolean sCompatibilityDone = false;
    private static boolean sUseBrokenMakeMeasureSpec = false;
    static boolean sUseZeroUnspecifiedMeasureSpec = false;
    private static boolean sIgnoreMeasureCache = false;
    private static boolean sAlwaysRemeasureExactly = false;
    static boolean sTextureViewIgnoresDrawableSetters = false;
    private static final int[] VISIBILITY_FLAGS = {0, 4, 8};
    private static final int[] DRAWING_CACHE_QUALITY_FLAGS = {0, 524288, 1048576};
    protected static final int[] EMPTY_STATE_SET = StateSet.get(0);
    protected static final int[] WINDOW_FOCUSED_STATE_SET = StateSet.get(1);
    protected static final int[] SELECTED_STATE_SET = StateSet.get(2);
    protected static final int[] SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(3);
    protected static final int[] FOCUSED_STATE_SET = StateSet.get(4);
    protected static final int[] FOCUSED_WINDOW_FOCUSED_STATE_SET = StateSet.get(5);
    protected static final int[] FOCUSED_SELECTED_STATE_SET = StateSet.get(6);
    protected static final int[] FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(7);
    protected static final int[] ENABLED_STATE_SET = StateSet.get(8);
    protected static final int[] ENABLED_WINDOW_FOCUSED_STATE_SET = StateSet.get(9);
    protected static final int[] ENABLED_SELECTED_STATE_SET = StateSet.get(10);
    protected static final int[] ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(11);
    protected static final int[] ENABLED_FOCUSED_STATE_SET = StateSet.get(12);
    protected static final int[] ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = StateSet.get(13);
    protected static final int[] ENABLED_FOCUSED_SELECTED_STATE_SET = StateSet.get(14);
    protected static final int[] ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(15);
    protected static final int[] PRESSED_STATE_SET = StateSet.get(16);
    protected static final int[] PRESSED_WINDOW_FOCUSED_STATE_SET = StateSet.get(17);
    protected static final int[] PRESSED_SELECTED_STATE_SET = StateSet.get(18);
    protected static final int[] PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(19);
    protected static final int[] PRESSED_FOCUSED_STATE_SET = StateSet.get(20);
    protected static final int[] PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = StateSet.get(21);
    protected static final int[] PRESSED_FOCUSED_SELECTED_STATE_SET = StateSet.get(22);
    protected static final int[] PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(23);
    protected static final int[] PRESSED_ENABLED_STATE_SET = StateSet.get(24);
    protected static final int[] PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = StateSet.get(25);
    protected static final int[] PRESSED_ENABLED_SELECTED_STATE_SET = StateSet.get(26);
    protected static final int[] PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(27);
    protected static final int[] PRESSED_ENABLED_FOCUSED_STATE_SET = StateSet.get(28);
    protected static final int[] PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = StateSet.get(29);
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = StateSet.get(30);
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(31);
    static final int DEBUG_CORNERS_COLOR = Color.rgb(63, 127, 255);
    static final ThreadLocal<Rect> sThreadLocal = ThreadLocal.withInitial(new Supplier() { // from class: android.view.View$$ExternalSyntheticLambda12
        @Override // java.util.function.Supplier
        public final Object get() {
            return new Rect();
        }
    });
    private static final int[] LAYOUT_DIRECTION_FLAGS = {0, 1, 2, 3};
    private static final int[] PFLAG2_TEXT_DIRECTION_FLAGS = {0, 64, 128, 192, 256, 320, 384, 448};
    private static final int[] PFLAG2_TEXT_ALIGNMENT_FLAGS = {0, 8192, 16384, 24576, 32768, UsbManager.USB_DATA_TRANSFER_RATE_40G, AudioChannelLayout.VOICE_CALL_MONO};
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static final Property<View, Float> ALPHA = new FloatProperty<View>("alpha") { // from class: android.view.View.2
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setAlpha(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getAlpha());
        }
    };
    public static final Property<View, Float> TRANSLATION_X = new FloatProperty<View>("translationX") { // from class: android.view.View.3
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setTranslationX(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getTranslationX());
        }
    };
    public static final Property<View, Float> TRANSLATION_Y = new FloatProperty<View>("translationY") { // from class: android.view.View.4
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setTranslationY(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getTranslationY());
        }
    };
    public static final Property<View, Float> TRANSLATION_Z = new FloatProperty<View>("translationZ") { // from class: android.view.View.5
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setTranslationZ(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getTranslationZ());
        }
    };

    /* renamed from: X */
    public static final Property<View, Float> f489X = new FloatProperty<View>("x") { // from class: android.view.View.6
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setX(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getX());
        }
    };

    /* renamed from: Y */
    public static final Property<View, Float> f490Y = new FloatProperty<View>("y") { // from class: android.view.View.7
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setY(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getY());
        }
    };

    /* renamed from: Z */
    public static final Property<View, Float> f491Z = new FloatProperty<View>("z") { // from class: android.view.View.8
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setZ(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getZ());
        }
    };
    public static final Property<View, Float> ROTATION = new FloatProperty<View>("rotation") { // from class: android.view.View.9
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setRotation(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getRotation());
        }
    };
    public static final Property<View, Float> ROTATION_X = new FloatProperty<View>("rotationX") { // from class: android.view.View.10
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setRotationX(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getRotationX());
        }
    };
    public static final Property<View, Float> ROTATION_Y = new FloatProperty<View>("rotationY") { // from class: android.view.View.11
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setRotationY(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getRotationY());
        }
    };
    public static final Property<View, Float> SCALE_X = new FloatProperty<View>("scaleX") { // from class: android.view.View.12
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setScaleX(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getScaleX());
        }
    };
    public static final Property<View, Float> SCALE_Y = new FloatProperty<View>("scaleY") { // from class: android.view.View.13
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            object.setScaleY(value);
        }

        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(object.getScaleY());
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface AccessibilityDataSensitive {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface AutofillFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface AutofillImportance {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface AutofillType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ContentCaptureImportance {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface DrawingCacheQuality {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface FindViewFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface FocusDirection {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface FocusRealDirection {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Focusable {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface FocusableMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface LayerType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface LayoutDir {
    }

    /* loaded from: classes4.dex */
    public interface OnApplyWindowInsetsListener {
        WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets);
    }

    /* loaded from: classes4.dex */
    public interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View view);

        void onViewDetachedFromWindow(View view);
    }

    /* loaded from: classes4.dex */
    public interface OnCapturedPointerListener {
        boolean onCapturedPointer(View view, MotionEvent motionEvent);
    }

    /* loaded from: classes4.dex */
    public interface OnClickListener {
        void onClick(View view);
    }

    /* loaded from: classes4.dex */
    public interface OnContextClickListener {
        boolean onContextClick(View view);
    }

    /* loaded from: classes4.dex */
    public interface OnCreateContextMenuListener {
        void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo);
    }

    /* loaded from: classes4.dex */
    public interface OnDragListener {
        boolean onDrag(View view, DragEvent dragEvent);
    }

    /* loaded from: classes4.dex */
    public interface OnFocusChangeListener {
        void onFocusChange(View view, boolean z);
    }

    /* loaded from: classes4.dex */
    public interface OnGenericMotionListener {
        boolean onGenericMotion(View view, MotionEvent motionEvent);
    }

    /* loaded from: classes4.dex */
    public interface OnHoverListener {
        boolean onHover(View view, MotionEvent motionEvent);
    }

    /* loaded from: classes4.dex */
    public interface OnKeyListener {
        boolean onKey(View view, int i, KeyEvent keyEvent);
    }

    /* loaded from: classes4.dex */
    public interface OnLayoutChangeListener {
        void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);
    }

    /* loaded from: classes4.dex */
    public interface OnScrollChangeListener {
        void onScrollChange(View view, int i, int i2, int i3, int i4);
    }

    @Deprecated
    /* loaded from: classes4.dex */
    public interface OnSystemUiVisibilityChangeListener {
        void onSystemUiVisibilityChange(int i);
    }

    /* loaded from: classes4.dex */
    public interface OnTouchListener {
        boolean onTouch(View view, MotionEvent motionEvent);
    }

    /* loaded from: classes4.dex */
    public interface OnUnhandledKeyEventListener {
        boolean onUnhandledKeyEvent(View view, KeyEvent keyEvent);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ResolvedLayoutDir {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ScrollBarStyle {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ScrollCaptureHint {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ScrollIndicators {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface SystemUiVisibility {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface TextAlignment {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ViewStructureType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Visibility {
    }

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<View> {
        private int mAccessibilityFocusedId;
        private int mAccessibilityHeadingId;
        private int mAccessibilityLiveRegionId;
        private int mAccessibilityPaneTitleId;
        private int mAccessibilityTraversalAfterId;
        private int mAccessibilityTraversalBeforeId;
        private int mActivatedId;
        private int mAlphaId;
        private int mAutofillHintsId;
        private int mBackgroundId;
        private int mBackgroundTintId;
        private int mBackgroundTintModeId;
        private int mBaselineId;
        private int mClickableId;
        private int mContentDescriptionId;
        private int mContextClickableId;
        private int mDefaultFocusHighlightEnabledId;
        private int mDrawingCacheQualityId;
        private int mDuplicateParentStateId;
        private int mElevationId;
        private int mEnabledId;
        private int mFadingEdgeLengthId;
        private int mFilterTouchesWhenObscuredId;
        private int mFitsSystemWindowsId;
        private int mFocusableId;
        private int mFocusableInTouchModeId;
        private int mFocusedByDefaultId;
        private int mFocusedId;
        private int mForceDarkAllowedId;
        private int mForegroundGravityId;
        private int mForegroundId;
        private int mForegroundTintId;
        private int mForegroundTintModeId;
        private int mHapticFeedbackEnabledId;
        private int mIdId;
        private int mImportantForAccessibilityId;
        private int mImportantForAutofillId;
        private int mImportantForContentCaptureId;
        private int mIsScrollContainerId;
        private int mKeepScreenOnId;
        private int mKeyboardNavigationClusterId;
        private int mLabelForId;
        private int mLayerTypeId;
        private int mLayoutDirectionId;
        private int mLongClickableId;
        private int mMinHeightId;
        private int mMinWidthId;
        private int mNestedScrollingEnabledId;
        private int mNextClusterForwardId;
        private int mNextFocusDownId;
        private int mNextFocusForwardId;
        private int mNextFocusLeftId;
        private int mNextFocusRightId;
        private int mNextFocusUpId;
        private int mOutlineAmbientShadowColorId;
        private int mOutlineProviderId;
        private int mOutlineSpotShadowColorId;
        private int mOverScrollModeId;
        private int mPaddingBottomId;
        private int mPaddingLeftId;
        private int mPaddingRightId;
        private int mPaddingTopId;
        private int mPointerIconId;
        private int mPressedId;
        private boolean mPropertiesMapped = false;
        private int mRawLayoutDirectionId;
        private int mRawTextAlignmentId;
        private int mRawTextDirectionId;
        private int mRequiresFadingEdgeId;
        private int mRotationId;
        private int mRotationXId;
        private int mRotationYId;
        private int mSaveEnabledId;
        private int mScaleXId;
        private int mScaleYId;
        private int mScreenReaderFocusableId;
        private int mScrollIndicatorsId;
        private int mScrollXId;
        private int mScrollYId;
        private int mScrollbarDefaultDelayBeforeFadeId;
        private int mScrollbarFadeDurationId;
        private int mScrollbarSizeId;
        private int mScrollbarStyleId;
        private int mSelectedId;
        private int mSolidColorId;
        private int mSoundEffectsEnabledId;
        private int mStateListAnimatorId;
        private int mTagId;
        private int mTextAlignmentId;
        private int mTextDirectionId;
        private int mTooltipTextId;
        private int mTransformPivotXId;
        private int mTransformPivotYId;
        private int mTransitionNameId;
        private int mTranslationXId;
        private int mTranslationYId;
        private int mTranslationZId;
        private int mVisibilityId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mAccessibilityFocusedId = propertyMapper.mapBoolean("accessibilityFocused", 0);
            this.mAccessibilityHeadingId = propertyMapper.mapBoolean("accessibilityHeading", 16844160);
            SparseArray<String> accessibilityLiveRegionEnumMapping = new SparseArray<>();
            accessibilityLiveRegionEnumMapping.put(0, "none");
            accessibilityLiveRegionEnumMapping.put(1, "polite");
            accessibilityLiveRegionEnumMapping.put(2, "assertive");
            Objects.requireNonNull(accessibilityLiveRegionEnumMapping);
            this.mAccessibilityLiveRegionId = propertyMapper.mapIntEnum("accessibilityLiveRegion", 16843758, new View$InspectionCompanion$$ExternalSyntheticLambda0(accessibilityLiveRegionEnumMapping));
            this.mAccessibilityPaneTitleId = propertyMapper.mapObject("accessibilityPaneTitle", 16844156);
            this.mAccessibilityTraversalAfterId = propertyMapper.mapResourceId("accessibilityTraversalAfter", 16843986);
            this.mAccessibilityTraversalBeforeId = propertyMapper.mapResourceId("accessibilityTraversalBefore", 16843985);
            this.mActivatedId = propertyMapper.mapBoolean("activated", 0);
            this.mAlphaId = propertyMapper.mapFloat("alpha", 16843551);
            this.mAutofillHintsId = propertyMapper.mapObject("autofillHints", 16844118);
            this.mBackgroundId = propertyMapper.mapObject("background", 16842964);
            this.mBackgroundTintId = propertyMapper.mapObject("backgroundTint", 16843883);
            this.mBackgroundTintModeId = propertyMapper.mapObject("backgroundTintMode", 16843884);
            this.mBaselineId = propertyMapper.mapInt("baseline", 16843548);
            this.mClickableId = propertyMapper.mapBoolean("clickable", 16842981);
            this.mContentDescriptionId = propertyMapper.mapObject("contentDescription", 16843379);
            this.mContextClickableId = propertyMapper.mapBoolean("contextClickable", 16844007);
            this.mDefaultFocusHighlightEnabledId = propertyMapper.mapBoolean("defaultFocusHighlightEnabled", 16844130);
            SparseArray<String> drawingCacheQualityEnumMapping = new SparseArray<>();
            drawingCacheQualityEnumMapping.put(0, "auto");
            drawingCacheQualityEnumMapping.put(524288, "low");
            drawingCacheQualityEnumMapping.put(1048576, "high");
            Objects.requireNonNull(drawingCacheQualityEnumMapping);
            this.mDrawingCacheQualityId = propertyMapper.mapIntEnum("drawingCacheQuality", 16842984, new View$InspectionCompanion$$ExternalSyntheticLambda0(drawingCacheQualityEnumMapping));
            this.mDuplicateParentStateId = propertyMapper.mapBoolean("duplicateParentState", 16842985);
            this.mElevationId = propertyMapper.mapFloat("elevation", 16843840);
            this.mEnabledId = propertyMapper.mapBoolean("enabled", 16842766);
            this.mFadingEdgeLengthId = propertyMapper.mapInt("fadingEdgeLength", 16842976);
            this.mFilterTouchesWhenObscuredId = propertyMapper.mapBoolean("filterTouchesWhenObscured", 16843460);
            this.mFitsSystemWindowsId = propertyMapper.mapBoolean("fitsSystemWindows", 16842973);
            SparseArray<String> focusableEnumMapping = new SparseArray<>();
            focusableEnumMapping.put(0, "false");
            focusableEnumMapping.put(1, "true");
            focusableEnumMapping.put(16, "auto");
            Objects.requireNonNull(focusableEnumMapping);
            this.mFocusableId = propertyMapper.mapIntEnum("focusable", 16842970, new View$InspectionCompanion$$ExternalSyntheticLambda0(focusableEnumMapping));
            this.mFocusableInTouchModeId = propertyMapper.mapBoolean("focusableInTouchMode", 16842971);
            this.mFocusedId = propertyMapper.mapBoolean("focused", 0);
            this.mFocusedByDefaultId = propertyMapper.mapBoolean("focusedByDefault", 16844100);
            this.mForceDarkAllowedId = propertyMapper.mapBoolean("forceDarkAllowed", 16844172);
            this.mForegroundId = propertyMapper.mapObject("foreground", 16843017);
            this.mForegroundGravityId = propertyMapper.mapGravity("foregroundGravity", 16843264);
            this.mForegroundTintId = propertyMapper.mapObject("foregroundTint", 16843885);
            this.mForegroundTintModeId = propertyMapper.mapObject("foregroundTintMode", 16843886);
            this.mHapticFeedbackEnabledId = propertyMapper.mapBoolean("hapticFeedbackEnabled", 16843358);
            this.mIdId = propertyMapper.mapResourceId("id", 16842960);
            SparseArray<String> importantForAccessibilityEnumMapping = new SparseArray<>();
            importantForAccessibilityEnumMapping.put(0, "auto");
            importantForAccessibilityEnumMapping.put(1, MediaMetrics.Value.YES);
            importantForAccessibilityEnumMapping.put(2, MediaMetrics.Value.f269NO);
            importantForAccessibilityEnumMapping.put(4, "noHideDescendants");
            Objects.requireNonNull(importantForAccessibilityEnumMapping);
            this.mImportantForAccessibilityId = propertyMapper.mapIntEnum("importantForAccessibility", 16843690, new View$InspectionCompanion$$ExternalSyntheticLambda0(importantForAccessibilityEnumMapping));
            SparseArray<String> importantForAutofillEnumMapping = new SparseArray<>();
            importantForAutofillEnumMapping.put(0, "auto");
            importantForAutofillEnumMapping.put(1, MediaMetrics.Value.YES);
            importantForAutofillEnumMapping.put(2, MediaMetrics.Value.f269NO);
            importantForAutofillEnumMapping.put(4, "yesExcludeDescendants");
            importantForAutofillEnumMapping.put(8, "noExcludeDescendants");
            Objects.requireNonNull(importantForAutofillEnumMapping);
            this.mImportantForAutofillId = propertyMapper.mapIntEnum("importantForAutofill", 16844120, new View$InspectionCompanion$$ExternalSyntheticLambda0(importantForAutofillEnumMapping));
            SparseArray<String> importantForContentCaptureEnumMapping = new SparseArray<>();
            importantForContentCaptureEnumMapping.put(0, "auto");
            importantForContentCaptureEnumMapping.put(1, MediaMetrics.Value.YES);
            importantForContentCaptureEnumMapping.put(2, MediaMetrics.Value.f269NO);
            importantForContentCaptureEnumMapping.put(4, "yesExcludeDescendants");
            importantForContentCaptureEnumMapping.put(8, "noExcludeDescendants");
            Objects.requireNonNull(importantForContentCaptureEnumMapping);
            this.mImportantForContentCaptureId = propertyMapper.mapIntEnum("importantForContentCapture", 16844295, new View$InspectionCompanion$$ExternalSyntheticLambda0(importantForContentCaptureEnumMapping));
            this.mIsScrollContainerId = propertyMapper.mapBoolean("isScrollContainer", 16843342);
            this.mKeepScreenOnId = propertyMapper.mapBoolean("keepScreenOn", 16843286);
            this.mKeyboardNavigationClusterId = propertyMapper.mapBoolean("keyboardNavigationCluster", 16844096);
            this.mLabelForId = propertyMapper.mapResourceId("labelFor", 16843718);
            SparseArray<String> layerTypeEnumMapping = new SparseArray<>();
            layerTypeEnumMapping.put(0, "none");
            layerTypeEnumMapping.put(1, "software");
            layerTypeEnumMapping.put(2, "hardware");
            Objects.requireNonNull(layerTypeEnumMapping);
            this.mLayerTypeId = propertyMapper.mapIntEnum("layerType", 16843604, new View$InspectionCompanion$$ExternalSyntheticLambda0(layerTypeEnumMapping));
            SparseArray<String> layoutDirectionEnumMapping = new SparseArray<>();
            layoutDirectionEnumMapping.put(0, "ltr");
            layoutDirectionEnumMapping.put(1, "rtl");
            Objects.requireNonNull(layoutDirectionEnumMapping);
            this.mLayoutDirectionId = propertyMapper.mapIntEnum("layoutDirection", 16843698, new View$InspectionCompanion$$ExternalSyntheticLambda0(layoutDirectionEnumMapping));
            this.mLongClickableId = propertyMapper.mapBoolean("longClickable", 16842982);
            this.mMinHeightId = propertyMapper.mapInt("minHeight", 16843072);
            this.mMinWidthId = propertyMapper.mapInt("minWidth", 16843071);
            this.mNestedScrollingEnabledId = propertyMapper.mapBoolean("nestedScrollingEnabled", 16843830);
            this.mNextClusterForwardId = propertyMapper.mapResourceId("nextClusterForward", 16844098);
            this.mNextFocusDownId = propertyMapper.mapResourceId("nextFocusDown", 16842980);
            this.mNextFocusForwardId = propertyMapper.mapResourceId("nextFocusForward", 16843580);
            this.mNextFocusLeftId = propertyMapper.mapResourceId("nextFocusLeft", 16842977);
            this.mNextFocusRightId = propertyMapper.mapResourceId("nextFocusRight", 16842978);
            this.mNextFocusUpId = propertyMapper.mapResourceId("nextFocusUp", 16842979);
            this.mOutlineAmbientShadowColorId = propertyMapper.mapColor("outlineAmbientShadowColor", 16844162);
            this.mOutlineProviderId = propertyMapper.mapObject("outlineProvider", 16843960);
            this.mOutlineSpotShadowColorId = propertyMapper.mapColor("outlineSpotShadowColor", 16844161);
            SparseArray<String> overScrollModeEnumMapping = new SparseArray<>();
            overScrollModeEnumMapping.put(0, "always");
            overScrollModeEnumMapping.put(1, "ifContentScrolls");
            overScrollModeEnumMapping.put(2, "never");
            Objects.requireNonNull(overScrollModeEnumMapping);
            this.mOverScrollModeId = propertyMapper.mapIntEnum("overScrollMode", 16843457, new View$InspectionCompanion$$ExternalSyntheticLambda0(overScrollModeEnumMapping));
            this.mPaddingBottomId = propertyMapper.mapInt("paddingBottom", 16842969);
            this.mPaddingLeftId = propertyMapper.mapInt("paddingLeft", 16842966);
            this.mPaddingRightId = propertyMapper.mapInt("paddingRight", 16842968);
            this.mPaddingTopId = propertyMapper.mapInt("paddingTop", 16842967);
            this.mPointerIconId = propertyMapper.mapObject("pointerIcon", 16844041);
            this.mPressedId = propertyMapper.mapBoolean("pressed", 0);
            SparseArray<String> rawLayoutDirectionEnumMapping = new SparseArray<>();
            rawLayoutDirectionEnumMapping.put(0, "ltr");
            rawLayoutDirectionEnumMapping.put(1, "rtl");
            rawLayoutDirectionEnumMapping.put(2, "inherit");
            rawLayoutDirectionEnumMapping.put(3, "locale");
            Objects.requireNonNull(rawLayoutDirectionEnumMapping);
            this.mRawLayoutDirectionId = propertyMapper.mapIntEnum("rawLayoutDirection", 0, new View$InspectionCompanion$$ExternalSyntheticLambda0(rawLayoutDirectionEnumMapping));
            SparseArray<String> rawTextAlignmentEnumMapping = new SparseArray<>();
            rawTextAlignmentEnumMapping.put(0, "inherit");
            rawTextAlignmentEnumMapping.put(1, "gravity");
            rawTextAlignmentEnumMapping.put(2, "textStart");
            rawTextAlignmentEnumMapping.put(3, "textEnd");
            rawTextAlignmentEnumMapping.put(4, "center");
            rawTextAlignmentEnumMapping.put(5, "viewStart");
            rawTextAlignmentEnumMapping.put(6, "viewEnd");
            Objects.requireNonNull(rawTextAlignmentEnumMapping);
            this.mRawTextAlignmentId = propertyMapper.mapIntEnum("rawTextAlignment", 0, new View$InspectionCompanion$$ExternalSyntheticLambda0(rawTextAlignmentEnumMapping));
            SparseArray<String> rawTextDirectionEnumMapping = new SparseArray<>();
            rawTextDirectionEnumMapping.put(0, "inherit");
            rawTextDirectionEnumMapping.put(1, "firstStrong");
            rawTextDirectionEnumMapping.put(2, "anyRtl");
            rawTextDirectionEnumMapping.put(3, "ltr");
            rawTextDirectionEnumMapping.put(4, "rtl");
            rawTextDirectionEnumMapping.put(5, "locale");
            rawTextDirectionEnumMapping.put(6, "firstStrongLtr");
            rawTextDirectionEnumMapping.put(7, "firstStrongRtl");
            Objects.requireNonNull(rawTextDirectionEnumMapping);
            this.mRawTextDirectionId = propertyMapper.mapIntEnum("rawTextDirection", 0, new View$InspectionCompanion$$ExternalSyntheticLambda0(rawTextDirectionEnumMapping));
            IntFlagMapping requiresFadingEdgeFlagMapping = new IntFlagMapping();
            requiresFadingEdgeFlagMapping.add(4096, 4096, Slice.HINT_HORIZONTAL);
            requiresFadingEdgeFlagMapping.add(12288, 0, "none");
            requiresFadingEdgeFlagMapping.add(8192, 8192, "vertical");
            Objects.requireNonNull(requiresFadingEdgeFlagMapping);
            this.mRequiresFadingEdgeId = propertyMapper.mapIntFlag("requiresFadingEdge", 16843685, new View$InspectionCompanion$$ExternalSyntheticLambda1(requiresFadingEdgeFlagMapping));
            this.mRotationId = propertyMapper.mapFloat("rotation", 16843558);
            this.mRotationXId = propertyMapper.mapFloat("rotationX", 16843559);
            this.mRotationYId = propertyMapper.mapFloat("rotationY", 16843560);
            this.mSaveEnabledId = propertyMapper.mapBoolean("saveEnabled", 16842983);
            this.mScaleXId = propertyMapper.mapFloat("scaleX", 16843556);
            this.mScaleYId = propertyMapper.mapFloat("scaleY", 16843557);
            this.mScreenReaderFocusableId = propertyMapper.mapBoolean("screenReaderFocusable", 16844148);
            IntFlagMapping scrollIndicatorsFlagMapping = new IntFlagMapping();
            scrollIndicatorsFlagMapping.add(2, 2, "bottom");
            scrollIndicatorsFlagMapping.add(32, 32, "end");
            scrollIndicatorsFlagMapping.add(4, 4, NavigationBarInflaterView.LEFT);
            scrollIndicatorsFlagMapping.add(-1, 0, "none");
            scrollIndicatorsFlagMapping.add(8, 8, NavigationBarInflaterView.RIGHT);
            scrollIndicatorsFlagMapping.add(16, 16, "start");
            scrollIndicatorsFlagMapping.add(1, 1, "top");
            Objects.requireNonNull(scrollIndicatorsFlagMapping);
            this.mScrollIndicatorsId = propertyMapper.mapIntFlag("scrollIndicators", 16844006, new View$InspectionCompanion$$ExternalSyntheticLambda1(scrollIndicatorsFlagMapping));
            this.mScrollXId = propertyMapper.mapInt("scrollX", 16842962);
            this.mScrollYId = propertyMapper.mapInt("scrollY", 16842963);
            this.mScrollbarDefaultDelayBeforeFadeId = propertyMapper.mapInt("scrollbarDefaultDelayBeforeFade", 16843433);
            this.mScrollbarFadeDurationId = propertyMapper.mapInt("scrollbarFadeDuration", 16843432);
            this.mScrollbarSizeId = propertyMapper.mapInt("scrollbarSize", 16842851);
            SparseArray<String> scrollbarStyleEnumMapping = new SparseArray<>();
            scrollbarStyleEnumMapping.put(0, "insideOverlay");
            scrollbarStyleEnumMapping.put(16777216, "insideInset");
            scrollbarStyleEnumMapping.put(33554432, "outsideOverlay");
            scrollbarStyleEnumMapping.put(50331648, "outsideInset");
            Objects.requireNonNull(scrollbarStyleEnumMapping);
            this.mScrollbarStyleId = propertyMapper.mapIntEnum("scrollbarStyle", 16842879, new View$InspectionCompanion$$ExternalSyntheticLambda0(scrollbarStyleEnumMapping));
            this.mSelectedId = propertyMapper.mapBoolean(Slice.HINT_SELECTED, 0);
            this.mSolidColorId = propertyMapper.mapColor("solidColor", 16843594);
            this.mSoundEffectsEnabledId = propertyMapper.mapBoolean("soundEffectsEnabled", 16843285);
            this.mStateListAnimatorId = propertyMapper.mapObject("stateListAnimator", 16843848);
            this.mTagId = propertyMapper.mapObject("tag", 16842961);
            SparseArray<String> textAlignmentEnumMapping = new SparseArray<>();
            textAlignmentEnumMapping.put(1, "gravity");
            textAlignmentEnumMapping.put(2, "textStart");
            textAlignmentEnumMapping.put(3, "textEnd");
            textAlignmentEnumMapping.put(4, "center");
            textAlignmentEnumMapping.put(5, "viewStart");
            textAlignmentEnumMapping.put(6, "viewEnd");
            Objects.requireNonNull(textAlignmentEnumMapping);
            this.mTextAlignmentId = propertyMapper.mapIntEnum("textAlignment", 16843697, new View$InspectionCompanion$$ExternalSyntheticLambda0(textAlignmentEnumMapping));
            SparseArray<String> textDirectionEnumMapping = new SparseArray<>();
            textDirectionEnumMapping.put(1, "firstStrong");
            textDirectionEnumMapping.put(2, "anyRtl");
            textDirectionEnumMapping.put(3, "ltr");
            textDirectionEnumMapping.put(4, "rtl");
            textDirectionEnumMapping.put(5, "locale");
            textDirectionEnumMapping.put(6, "firstStrongLtr");
            textDirectionEnumMapping.put(7, "firstStrongRtl");
            Objects.requireNonNull(textDirectionEnumMapping);
            this.mTextDirectionId = propertyMapper.mapIntEnum("textDirection", 0, new View$InspectionCompanion$$ExternalSyntheticLambda0(textDirectionEnumMapping));
            this.mTooltipTextId = propertyMapper.mapObject("tooltipText", 16844084);
            this.mTransformPivotXId = propertyMapper.mapFloat("transformPivotX", 16843552);
            this.mTransformPivotYId = propertyMapper.mapFloat("transformPivotY", 16843553);
            this.mTransitionNameId = propertyMapper.mapObject("transitionName", 16843776);
            this.mTranslationXId = propertyMapper.mapFloat("translationX", 16843554);
            this.mTranslationYId = propertyMapper.mapFloat("translationY", 16843555);
            this.mTranslationZId = propertyMapper.mapFloat("translationZ", 16843770);
            SparseArray<String> visibilityEnumMapping = new SparseArray<>();
            visibilityEnumMapping.put(0, CalendarContract.CalendarColumns.VISIBLE);
            visibilityEnumMapping.put(4, "invisible");
            visibilityEnumMapping.put(8, "gone");
            Objects.requireNonNull(visibilityEnumMapping);
            this.mVisibilityId = propertyMapper.mapIntEnum(Downloads.Impl.COLUMN_VISIBILITY, 16842972, new View$InspectionCompanion$$ExternalSyntheticLambda0(visibilityEnumMapping));
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(View node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readBoolean(this.mAccessibilityFocusedId, node.isAccessibilityFocused());
            propertyReader.readBoolean(this.mAccessibilityHeadingId, node.isAccessibilityHeading());
            propertyReader.readIntEnum(this.mAccessibilityLiveRegionId, node.getAccessibilityLiveRegion());
            propertyReader.readObject(this.mAccessibilityPaneTitleId, node.getAccessibilityPaneTitle());
            propertyReader.readResourceId(this.mAccessibilityTraversalAfterId, node.getAccessibilityTraversalAfter());
            propertyReader.readResourceId(this.mAccessibilityTraversalBeforeId, node.getAccessibilityTraversalBefore());
            propertyReader.readBoolean(this.mActivatedId, node.isActivated());
            propertyReader.readFloat(this.mAlphaId, node.getAlpha());
            propertyReader.readObject(this.mAutofillHintsId, node.getAutofillHints());
            propertyReader.readObject(this.mBackgroundId, node.getBackground());
            propertyReader.readObject(this.mBackgroundTintId, node.getBackgroundTintList());
            propertyReader.readObject(this.mBackgroundTintModeId, node.getBackgroundTintMode());
            propertyReader.readInt(this.mBaselineId, node.getBaseline());
            propertyReader.readBoolean(this.mClickableId, node.isClickable());
            propertyReader.readObject(this.mContentDescriptionId, node.getContentDescription());
            propertyReader.readBoolean(this.mContextClickableId, node.isContextClickable());
            propertyReader.readBoolean(this.mDefaultFocusHighlightEnabledId, node.getDefaultFocusHighlightEnabled());
            propertyReader.readIntEnum(this.mDrawingCacheQualityId, node.getDrawingCacheQuality());
            propertyReader.readBoolean(this.mDuplicateParentStateId, node.isDuplicateParentStateEnabled());
            propertyReader.readFloat(this.mElevationId, node.getElevation());
            propertyReader.readBoolean(this.mEnabledId, node.isEnabled());
            propertyReader.readInt(this.mFadingEdgeLengthId, node.getFadingEdgeLength());
            propertyReader.readBoolean(this.mFilterTouchesWhenObscuredId, node.getFilterTouchesWhenObscured());
            propertyReader.readBoolean(this.mFitsSystemWindowsId, node.getFitsSystemWindows());
            propertyReader.readIntEnum(this.mFocusableId, node.getFocusable());
            propertyReader.readBoolean(this.mFocusableInTouchModeId, node.isFocusableInTouchMode());
            propertyReader.readBoolean(this.mFocusedId, node.isFocused());
            propertyReader.readBoolean(this.mFocusedByDefaultId, node.isFocusedByDefault());
            propertyReader.readBoolean(this.mForceDarkAllowedId, node.isForceDarkAllowed());
            propertyReader.readObject(this.mForegroundId, node.getForeground());
            propertyReader.readGravity(this.mForegroundGravityId, node.getForegroundGravity());
            propertyReader.readObject(this.mForegroundTintId, node.getForegroundTintList());
            propertyReader.readObject(this.mForegroundTintModeId, node.getForegroundTintMode());
            propertyReader.readBoolean(this.mHapticFeedbackEnabledId, node.isHapticFeedbackEnabled());
            propertyReader.readResourceId(this.mIdId, node.getId());
            propertyReader.readIntEnum(this.mImportantForAccessibilityId, node.getImportantForAccessibility());
            propertyReader.readIntEnum(this.mImportantForAutofillId, node.getImportantForAutofill());
            propertyReader.readIntEnum(this.mImportantForContentCaptureId, node.getImportantForContentCapture());
            propertyReader.readBoolean(this.mIsScrollContainerId, node.isScrollContainer());
            propertyReader.readBoolean(this.mKeepScreenOnId, node.getKeepScreenOn());
            propertyReader.readBoolean(this.mKeyboardNavigationClusterId, node.isKeyboardNavigationCluster());
            propertyReader.readResourceId(this.mLabelForId, node.getLabelFor());
            propertyReader.readIntEnum(this.mLayerTypeId, node.getLayerType());
            propertyReader.readIntEnum(this.mLayoutDirectionId, node.getLayoutDirection());
            propertyReader.readBoolean(this.mLongClickableId, node.isLongClickable());
            propertyReader.readInt(this.mMinHeightId, node.getMinimumHeight());
            propertyReader.readInt(this.mMinWidthId, node.getMinimumWidth());
            propertyReader.readBoolean(this.mNestedScrollingEnabledId, node.isNestedScrollingEnabled());
            propertyReader.readResourceId(this.mNextClusterForwardId, node.getNextClusterForwardId());
            propertyReader.readResourceId(this.mNextFocusDownId, node.getNextFocusDownId());
            propertyReader.readResourceId(this.mNextFocusForwardId, node.getNextFocusForwardId());
            propertyReader.readResourceId(this.mNextFocusLeftId, node.getNextFocusLeftId());
            propertyReader.readResourceId(this.mNextFocusRightId, node.getNextFocusRightId());
            propertyReader.readResourceId(this.mNextFocusUpId, node.getNextFocusUpId());
            propertyReader.readColor(this.mOutlineAmbientShadowColorId, node.getOutlineAmbientShadowColor());
            propertyReader.readObject(this.mOutlineProviderId, node.getOutlineProvider());
            propertyReader.readColor(this.mOutlineSpotShadowColorId, node.getOutlineSpotShadowColor());
            propertyReader.readIntEnum(this.mOverScrollModeId, node.getOverScrollMode());
            propertyReader.readInt(this.mPaddingBottomId, node.getPaddingBottom());
            propertyReader.readInt(this.mPaddingLeftId, node.getPaddingLeft());
            propertyReader.readInt(this.mPaddingRightId, node.getPaddingRight());
            propertyReader.readInt(this.mPaddingTopId, node.getPaddingTop());
            propertyReader.readObject(this.mPointerIconId, node.getPointerIcon());
            propertyReader.readBoolean(this.mPressedId, node.isPressed());
            propertyReader.readIntEnum(this.mRawLayoutDirectionId, node.getRawLayoutDirection());
            propertyReader.readIntEnum(this.mRawTextAlignmentId, node.getRawTextAlignment());
            propertyReader.readIntEnum(this.mRawTextDirectionId, node.getRawTextDirection());
            propertyReader.readIntFlag(this.mRequiresFadingEdgeId, node.getFadingEdge());
            propertyReader.readFloat(this.mRotationId, node.getRotation());
            propertyReader.readFloat(this.mRotationXId, node.getRotationX());
            propertyReader.readFloat(this.mRotationYId, node.getRotationY());
            propertyReader.readBoolean(this.mSaveEnabledId, node.isSaveEnabled());
            propertyReader.readFloat(this.mScaleXId, node.getScaleX());
            propertyReader.readFloat(this.mScaleYId, node.getScaleY());
            propertyReader.readBoolean(this.mScreenReaderFocusableId, node.isScreenReaderFocusable());
            propertyReader.readIntFlag(this.mScrollIndicatorsId, node.getScrollIndicators());
            propertyReader.readInt(this.mScrollXId, node.getScrollX());
            propertyReader.readInt(this.mScrollYId, node.getScrollY());
            propertyReader.readInt(this.mScrollbarDefaultDelayBeforeFadeId, node.getScrollBarDefaultDelayBeforeFade());
            propertyReader.readInt(this.mScrollbarFadeDurationId, node.getScrollBarFadeDuration());
            propertyReader.readInt(this.mScrollbarSizeId, node.getScrollBarSize());
            propertyReader.readIntEnum(this.mScrollbarStyleId, node.getScrollBarStyle());
            propertyReader.readBoolean(this.mSelectedId, node.isSelected());
            propertyReader.readColor(this.mSolidColorId, node.getSolidColor());
            propertyReader.readBoolean(this.mSoundEffectsEnabledId, node.isSoundEffectsEnabled());
            propertyReader.readObject(this.mStateListAnimatorId, node.getStateListAnimator());
            propertyReader.readObject(this.mTagId, node.getTag());
            propertyReader.readIntEnum(this.mTextAlignmentId, node.getTextAlignment());
            propertyReader.readIntEnum(this.mTextDirectionId, node.getTextDirection());
            propertyReader.readObject(this.mTooltipTextId, node.getTooltipText());
            propertyReader.readFloat(this.mTransformPivotXId, node.getPivotX());
            propertyReader.readFloat(this.mTransformPivotYId, node.getPivotY());
            propertyReader.readObject(this.mTransitionNameId, node.getTransitionName());
            propertyReader.readFloat(this.mTranslationXId, node.getTranslationX());
            propertyReader.readFloat(this.mTranslationYId, node.getTranslationY());
            propertyReader.readFloat(this.mTranslationZId, node.getTranslationZ());
            propertyReader.readIntEnum(this.mVisibilityId, node.getVisibility());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class TransformationInfo {
        private Matrix mInverseMatrix;
        private final Matrix mMatrix = new Matrix();
        @ViewDebug.ExportedProperty
        private float mAlpha = 1.0f;
        float mTransitionAlpha = 1.0f;

        TransformationInfo() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class TintInfo {
        BlendMode mBlendMode;
        boolean mHasTintList;
        boolean mHasTintMode;
        ColorStateList mTintList;

        TintInfo() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class ForegroundInfo {
        private boolean mBoundsChanged;
        private Drawable mDrawable;
        private int mGravity;
        private boolean mInsidePadding;
        private final Rect mOverlayBounds;
        private final Rect mSelfBounds;
        private TintInfo mTintInfo;

        private ForegroundInfo() {
            this.mGravity = 119;
            this.mInsidePadding = true;
            this.mBoundsChanged = true;
            this.mSelfBounds = new Rect();
            this.mOverlayBounds = new Rect();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class ListenerInfo {
        OnApplyWindowInsetsListener mOnApplyWindowInsetsListener;
        private CopyOnWriteArrayList<OnAttachStateChangeListener> mOnAttachStateChangeListeners;
        OnCapturedPointerListener mOnCapturedPointerListener;
        public OnClickListener mOnClickListener;
        protected OnContextClickListener mOnContextClickListener;
        protected OnCreateContextMenuListener mOnCreateContextMenuListener;
        private OnDragListener mOnDragListener;
        protected OnFocusChangeListener mOnFocusChangeListener;
        private OnGenericMotionListener mOnGenericMotionListener;
        private OnHoverListener mOnHoverListener;
        private OnKeyListener mOnKeyListener;
        private ArrayList<OnLayoutChangeListener> mOnLayoutChangeListeners;
        protected OnLongClickListener mOnLongClickListener;
        private OnReceiveContentListener mOnReceiveContentListener;
        protected OnScrollChangeListener mOnScrollChangeListener;
        private OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;
        private OnTouchListener mOnTouchListener;
        private Runnable mPositionChangedUpdate;
        public RenderNode.PositionUpdateListener mPositionUpdateListener;
        ScrollCaptureCallback mScrollCaptureCallback;
        private ArrayList<OnUnhandledKeyEventListener> mUnhandledKeyListeners;
        WindowInsetsAnimation.Callback mWindowInsetsAnimationCallback;
        private List<Rect> mSystemGestureExclusionRects = null;
        private List<Rect> mKeepClearRects = null;
        private List<Rect> mUnrestrictedKeepClearRects = null;
        private boolean mPreferKeepClear = false;
        private Rect mHandwritingArea = null;

        ListenerInfo() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class TooltipInfo {
        int mAnchorX;
        int mAnchorY;
        Runnable mHideTooltipRunnable;
        int mHoverSlop;
        Runnable mShowTooltipRunnable;
        boolean mTooltipFromLongClick;
        TooltipPopup mTooltipPopup;
        CharSequence mTooltipText;

        private TooltipInfo() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean updateAnchorPos(MotionEvent event) {
            int newAnchorX = (int) event.getX();
            int newAnchorY = (int) event.getY();
            if (Math.abs(newAnchorX - this.mAnchorX) <= this.mHoverSlop && Math.abs(newAnchorY - this.mAnchorY) <= this.mHoverSlop) {
                return false;
            }
            this.mAnchorX = newAnchorX;
            this.mAnchorY = newAnchorY;
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearAnchorPos() {
            this.mAnchorX = Integer.MAX_VALUE;
            this.mAnchorY = Integer.MAX_VALUE;
        }
    }

    public View(Context context) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        boolean z9;
        boolean z10;
        boolean z11;
        boolean z12;
        boolean z13;
        boolean z14;
        boolean z15;
        boolean z16;
        this.mCurrentAnimation = null;
        this.mRecreateDisplayList = false;
        this.mID = -1;
        this.mAutofillViewId = -1;
        this.mAccessibilityViewId = -1;
        this.mAccessibilityCursorPosition = -1;
        this.mTag = null;
        this.mTransientStateCount = 0;
        this.mClipBounds = null;
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mExplicitAccessibilityDataSensitive = 0;
        this.mInferredAccessibilityDataSensitive = 0;
        this.mLabelForId = -1;
        this.mAccessibilityTraversalBeforeId = -1;
        this.mAccessibilityTraversalAfterId = -1;
        this.mLeftPaddingDefined = false;
        this.mRightPaddingDefined = false;
        this.mOldWidthMeasureSpec = Integer.MIN_VALUE;
        this.mOldHeightMeasureSpec = Integer.MIN_VALUE;
        this.mLongClickX = Float.NaN;
        this.mLongClickY = Float.NaN;
        this.mDrawableState = null;
        this.mOutlineProvider = ViewOutlineProvider.BACKGROUND;
        this.mNextFocusLeftId = -1;
        this.mNextFocusRightId = -1;
        this.mNextFocusUpId = -1;
        this.mNextFocusDownId = -1;
        this.mNextFocusForwardId = -1;
        this.mNextClusterForwardId = -1;
        this.mDefaultFocusHighlightEnabled = true;
        this.mPendingCheckForTap = null;
        this.mTouchDelegate = null;
        this.mHoveringTouchDelegate = false;
        this.mDrawingCacheBackgroundColor = 0;
        this.mAnimator = null;
        this.mLayerType = 0;
        if (!InputEventConsistencyVerifier.isInstrumentationEnabled()) {
            inputEventConsistencyVerifier = null;
        } else {
            inputEventConsistencyVerifier = new InputEventConsistencyVerifier(this, 0);
        }
        this.mInputEventConsistencyVerifier = inputEventConsistencyVerifier;
        this.mSourceLayoutId = 0;
        this.mUnbufferedInputSource = 0;
        this.mShouldFakeFocus = false;
        this.mContext = context;
        this.mResources = context != null ? context.getResources() : null;
        this.mViewFlags = 402653200;
        this.mPrivateFlags2 = 140296;
        this.mPrivateFlags4 = 65536;
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mAmbiguousGestureMultiplier = configuration.getScaledAmbiguousGestureMultiplier();
        setOverScrollMode(1);
        this.mUserPaddingStart = Integer.MIN_VALUE;
        this.mUserPaddingEnd = Integer.MIN_VALUE;
        this.mRenderNode = RenderNode.create(getClass().getName(), new ViewAnimationHostBridge(this));
        if (!sCompatibilityDone && context != null) {
            int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
            if (targetSdkVersion > 17) {
                z = false;
            } else {
                z = true;
            }
            sUseBrokenMakeMeasureSpec = z;
            if (targetSdkVersion >= 19) {
                z2 = false;
            } else {
                z2 = true;
            }
            sIgnoreMeasureCache = z2;
            if (targetSdkVersion >= 23) {
                z3 = false;
            } else {
                z3 = true;
            }
            sUseZeroUnspecifiedMeasureSpec = z3;
            if (targetSdkVersion > 23) {
                z4 = false;
            } else {
                z4 = true;
            }
            sAlwaysRemeasureExactly = z4;
            if (targetSdkVersion > 23) {
                z5 = false;
            } else {
                z5 = true;
            }
            sTextureViewIgnoresDrawableSetters = z5;
            if (targetSdkVersion < 24) {
                z6 = false;
            } else {
                z6 = true;
            }
            sPreserveMarginParamsInLayoutParamConversion = z6;
            if (targetSdkVersion >= 24) {
                z7 = false;
            } else {
                z7 = true;
            }
            sCascadedDragDrop = z7;
            if (targetSdkVersion >= 26) {
                z8 = false;
            } else {
                z8 = true;
            }
            sHasFocusableExcludeAutoFocusable = z8;
            if (targetSdkVersion >= 26) {
                z9 = false;
            } else {
                z9 = true;
            }
            sAutoFocusableOffUIThreadWontNotifyParents = z9;
            sUseDefaultFocusHighlight = context.getResources().getBoolean(C4057R.bool.config_useDefaultFocusHighlight);
            if (targetSdkVersion < 28) {
                z10 = false;
            } else {
                z10 = true;
            }
            sThrowOnInvalidFloatProperties = z10;
            if (targetSdkVersion >= 28) {
                z11 = false;
            } else {
                z11 = true;
            }
            sCanFocusZeroSized = z11;
            if (targetSdkVersion >= 28) {
                z12 = false;
            } else {
                z12 = true;
            }
            sAlwaysAssignFocus = z12;
            if (targetSdkVersion >= 28) {
                z13 = false;
            } else {
                z13 = true;
            }
            sAcceptZeroSizeDragShadow = z13;
            if (targetSdkVersion >= 30) {
                z14 = false;
            } else {
                z14 = true;
            }
            sBrokenInsetsDispatch = z14;
            if (targetSdkVersion >= 29) {
                z15 = false;
            } else {
                z15 = true;
            }
            sBrokenWindowBackground = z15;
            if (targetSdkVersion < 29) {
                z16 = false;
            } else {
                z16 = true;
            }
            GradientDrawable.sWrapNegativeAngleMeasurements = z16;
            sForceLayoutWhenInsetsChanged = targetSdkVersion < 30;
            sCompatibilityDone = true;
        }
    }

    public View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public View(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:227:0x0899, code lost:
        if (r1 >= 14) goto L9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public View(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context);
        int targetSdkVersion;
        int leftPadding;
        int rightPadding;
        int bottomPadding;
        int rightPadding2;
        int x;
        int padding;
        boolean rightPaddingDefined;
        boolean leftPaddingDefined;
        String rawString;
        CharSequence[] charSequenceArr;
        this.mSourceLayoutId = Resources.getAttributeSetSourceResId(attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.View, defStyleAttr, defStyleRes);
        retrieveExplicitStyle(context.getTheme(), attrs);
        saveAttributeDataForStyleable(context, C4057R.styleable.View, attrs, a, defStyleAttr, defStyleRes);
        if (sDebugViewAttributes) {
            saveAttributeData(attrs, a);
        }
        boolean transformSet = false;
        int scrollbarStyle = 0;
        int overScrollMode = this.mOverScrollMode;
        boolean initializeScrollbars = false;
        boolean initializeScrollIndicators = false;
        boolean startPaddingDefined = false;
        boolean endPaddingDefined = false;
        int leftPadding2 = -1;
        int leftPadding3 = context.getApplicationInfo().targetSdkVersion;
        int viewFlagValues = 0 | 16;
        int viewFlagValues2 = 0 | 16;
        int topPadding = -1;
        int N = a.getIndexCount();
        int viewFlagMasks = viewFlagValues2;
        float tx = 0.0f;
        float ty = 0.0f;
        float tz = 0.0f;
        float elevation = 0.0f;
        float rotation = 0.0f;
        float rotationX = 0.0f;
        float rotationY = 0.0f;
        float sx = 1.0f;
        float sy = 1.0f;
        boolean leftPaddingDefined2 = false;
        boolean rightPaddingDefined2 = false;
        int viewFlagMasks2 = -1;
        int paddingHorizontal = -1;
        int x2 = 0;
        int y = 0;
        int rightPadding3 = overScrollMode;
        int paddingHorizontal2 = Integer.MIN_VALUE;
        int x3 = -1;
        int y2 = 0;
        int startPadding = viewFlagValues;
        int bottomPadding2 = -1;
        int paddingVertical = Integer.MIN_VALUE;
        int padding2 = -1;
        Drawable background = null;
        int padding3 = 0;
        while (padding3 < N) {
            int N2 = N;
            int attr = a.getIndex(padding3);
            switch (attr) {
                case 8:
                    int padding4 = padding2;
                    boolean rightPaddingDefined3 = rightPaddingDefined2;
                    boolean leftPaddingDefined3 = leftPaddingDefined2;
                    int scrollbarStyle2 = a.getInt(attr, 0);
                    if (scrollbarStyle2 == 0) {
                        scrollbarStyle = scrollbarStyle2;
                        rightPaddingDefined2 = rightPaddingDefined3;
                        padding2 = padding4;
                        leftPaddingDefined2 = leftPaddingDefined3;
                        break;
                    } else {
                        startPadding |= scrollbarStyle2 & 50331648;
                        scrollbarStyle = scrollbarStyle2;
                        viewFlagMasks = 50331648 | viewFlagMasks;
                        rightPaddingDefined2 = rightPaddingDefined3;
                        padding2 = padding4;
                        leftPaddingDefined2 = leftPaddingDefined3;
                        break;
                    }
                case 9:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mID = a.getResourceId(attr, -1);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 10:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mTag = a.getText(attr);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 11:
                    int x4 = a.getDimensionPixelOffset(attr, 0);
                    x2 = x4;
                    break;
                case 12:
                    int y3 = a.getDimensionPixelOffset(attr, 0);
                    y = y3;
                    break;
                case 13:
                    Drawable background2 = a.getDrawable(attr);
                    background = background2;
                    break;
                case 14:
                    int padding5 = a.getDimensionPixelSize(attr, -1);
                    this.mUserPaddingLeftInitial = padding5;
                    this.mUserPaddingRightInitial = padding5;
                    rightPaddingDefined2 = true;
                    leftPaddingDefined2 = true;
                    padding2 = padding5;
                    break;
                case 15:
                    int leftPadding4 = a.getDimensionPixelSize(attr, -1);
                    this.mUserPaddingLeftInitial = leftPadding4;
                    leftPadding2 = leftPadding4;
                    leftPaddingDefined2 = true;
                    padding2 = padding2;
                    break;
                case 16:
                    int topPadding2 = a.getDimensionPixelSize(attr, -1);
                    topPadding = topPadding2;
                    break;
                case 17:
                    int rightPadding4 = a.getDimensionPixelSize(attr, -1);
                    this.mUserPaddingRightInitial = rightPadding4;
                    viewFlagMasks2 = rightPadding4;
                    rightPaddingDefined2 = true;
                    padding2 = padding2;
                    break;
                case 18:
                    int bottomPadding3 = a.getDimensionPixelSize(attr, -1);
                    bottomPadding2 = bottomPadding3;
                    break;
                case 19:
                    int padding6 = padding2;
                    boolean rightPaddingDefined4 = rightPaddingDefined2;
                    boolean leftPaddingDefined4 = leftPaddingDefined2;
                    int viewFlagValues3 = (startPadding & (-18)) | getFocusableAttribute(a);
                    if ((viewFlagValues3 & 16) != 0) {
                        startPadding = viewFlagValues3;
                        rightPaddingDefined2 = rightPaddingDefined4;
                        padding2 = padding6;
                        leftPaddingDefined2 = leftPaddingDefined4;
                        break;
                    } else {
                        viewFlagMasks |= 17;
                        rightPaddingDefined2 = rightPaddingDefined4;
                        padding2 = padding6;
                        leftPaddingDefined2 = leftPaddingDefined4;
                        startPadding = viewFlagValues3;
                        break;
                    }
                case 20:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.getBoolean(attr, false)) {
                        int viewFlagValues4 = startPadding & (-17);
                        viewFlagMasks = 262161 | viewFlagMasks;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        startPadding = viewFlagValues4 | 262145;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 21:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    int visibility = a.getInt(attr, 0);
                    if (visibility != 0) {
                        startPadding |= VISIBILITY_FLAGS[visibility];
                        viewFlagMasks |= 12;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 22:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.getBoolean(attr, false)) {
                        int viewFlagValues5 = startPadding | 2;
                        int viewFlagValues6 = viewFlagMasks | 2;
                        viewFlagMasks = viewFlagValues6;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        startPadding = viewFlagValues5;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 23:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    int scrollbars = a.getInt(attr, 0);
                    if (scrollbars != 0) {
                        startPadding |= scrollbars;
                        initializeScrollbars = true;
                        rightPaddingDefined2 = rightPaddingDefined;
                        leftPaddingDefined2 = leftPaddingDefined;
                        viewFlagMasks |= 768;
                        padding2 = padding;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 24:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    break;
                case 25:
                case 45:
                case 46:
                case 47:
                case 108:
                case 109:
                case 110:
                case 111:
                case 112:
                default:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 26:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mNextFocusLeftId = a.getResourceId(attr, -1);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 27:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mNextFocusRightId = a.getResourceId(attr, -1);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 28:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mNextFocusUpId = a.getResourceId(attr, -1);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 29:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mNextFocusDownId = a.getResourceId(attr, -1);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 30:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.getBoolean(attr, false)) {
                        int viewFlagValues7 = startPadding | 16384;
                        int viewFlagValues8 = viewFlagMasks | 16384;
                        viewFlagMasks = viewFlagValues8;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        startPadding = viewFlagValues7;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 31:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.getBoolean(attr, false)) {
                        startPadding |= 2097152;
                        viewFlagMasks = 2097152 | viewFlagMasks;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 32:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (!a.getBoolean(attr, true)) {
                        startPadding |= 65536;
                        viewFlagMasks = 65536 | viewFlagMasks;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 33:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    int cacheQuality = a.getInt(attr, 0);
                    if (cacheQuality != 0) {
                        startPadding |= DRAWING_CACHE_QUALITY_FLAGS[cacheQuality];
                        viewFlagMasks = 1572864 | viewFlagMasks;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 34:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.getBoolean(attr, false)) {
                        startPadding |= 4194304;
                        viewFlagMasks = 4194304 | viewFlagMasks;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 35:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (leftPadding3 >= 23 || (this instanceof FrameLayout)) {
                        setForeground(a.getDrawable(attr));
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 36:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mMinWidth = a.getDimensionPixelSize(attr, 0);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 37:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mMinHeight = a.getDimensionPixelSize(attr, 0);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 38:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (leftPadding3 >= 23 || (this instanceof FrameLayout)) {
                        setForegroundGravity(a.getInt(attr, 0));
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 39:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (!a.getBoolean(attr, true)) {
                        int viewFlagValues9 = (-134217729) & startPadding;
                        viewFlagMasks = 134217728 | viewFlagMasks;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        startPadding = viewFlagValues9;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 40:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.getBoolean(attr, false)) {
                        startPadding |= 67108864;
                        viewFlagMasks = 67108864 | viewFlagMasks;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 41:
                    int padding7 = padding2;
                    boolean rightPaddingDefined5 = rightPaddingDefined2;
                    boolean leftPaddingDefined5 = leftPaddingDefined2;
                    if (a.getBoolean(attr, false)) {
                        setScrollContainer(true);
                    }
                    y2 = 1;
                    rightPaddingDefined2 = rightPaddingDefined5;
                    padding2 = padding7;
                    leftPaddingDefined2 = leftPaddingDefined5;
                    break;
                case 42:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (!a.getBoolean(attr, true)) {
                        int viewFlagValues10 = (-268435457) & startPadding;
                        viewFlagMasks = 268435456 | viewFlagMasks;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        startPadding = viewFlagValues10;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 43:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (context.isRestricted()) {
                        throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
                    }
                    String handlerName = a.getString(attr);
                    if (handlerName != null) {
                        setOnClickListener(new DeclaredOnClickListener(this, handlerName));
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 44:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setContentDescription(a.getString(attr));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 48:
                    int overScrollMode2 = a.getInt(attr, 1);
                    rightPadding3 = overScrollMode2;
                    break;
                case 49:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.getBoolean(attr, false)) {
                        int viewFlagValues11 = startPadding | 1024;
                        int viewFlagValues12 = viewFlagMasks | 1024;
                        viewFlagMasks = viewFlagValues12;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        startPadding = viewFlagValues11;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 50:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setAlpha(a.getFloat(attr, 1.0f));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 51:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setPivotX(a.getDimension(attr, 0.0f));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 52:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setPivotY(a.getDimension(attr, 0.0f));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 53:
                    float tx2 = a.getDimension(attr, 0.0f);
                    tx = tx2;
                    transformSet = true;
                    padding2 = padding2;
                    break;
                case 54:
                    float ty2 = a.getDimension(attr, 0.0f);
                    ty = ty2;
                    transformSet = true;
                    padding2 = padding2;
                    break;
                case 55:
                    float sx2 = a.getFloat(attr, 1.0f);
                    sx = sx2;
                    transformSet = true;
                    padding2 = padding2;
                    break;
                case 56:
                    float sy2 = a.getFloat(attr, 1.0f);
                    sy = sy2;
                    transformSet = true;
                    padding2 = padding2;
                    break;
                case 57:
                    float rotation2 = a.getFloat(attr, 0.0f);
                    rotation = rotation2;
                    transformSet = true;
                    padding2 = padding2;
                    break;
                case 58:
                    float rotationX2 = a.getFloat(attr, 0.0f);
                    rotationX = rotationX2;
                    transformSet = true;
                    padding2 = padding2;
                    break;
                case 59:
                    float rotationY2 = a.getFloat(attr, 0.0f);
                    rotationY = rotationY2;
                    transformSet = true;
                    padding2 = padding2;
                    break;
                case 60:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mVerticalScrollbarPosition = a.getInt(attr, 0);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 61:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mNextFocusForwardId = a.getResourceId(attr, -1);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 62:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setLayerType(a.getInt(attr, 0), null);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 63:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    int fadingEdge = a.getInt(attr, 0);
                    if (fadingEdge != 0) {
                        startPadding |= fadingEdge;
                        initializeFadingEdgeInternal(a);
                        viewFlagMasks |= 12288;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 64:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setImportantForAccessibility(a.getInt(attr, 0));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 65:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    int padding8 = this.mPrivateFlags2;
                    this.mPrivateFlags2 = padding8 & (-449);
                    int textDirection = a.getInt(attr, -1);
                    if (textDirection != -1) {
                        this.mPrivateFlags2 |= PFLAG2_TEXT_DIRECTION_FLAGS[textDirection];
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 66:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mPrivateFlags2 &= -57345;
                    int textAlignment = a.getInt(attr, 1);
                    this.mPrivateFlags2 |= PFLAG2_TEXT_ALIGNMENT_FLAGS[textAlignment];
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 67:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    int padding9 = this.mPrivateFlags2;
                    this.mPrivateFlags2 = padding9 & (-61);
                    int layoutDirection = a.getInt(attr, -1);
                    int value = layoutDirection != -1 ? LAYOUT_DIRECTION_FLAGS[layoutDirection] : 2;
                    this.mPrivateFlags2 |= value << 2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 68:
                    int padding10 = padding2;
                    boolean rightPaddingDefined6 = rightPaddingDefined2;
                    boolean leftPaddingDefined6 = leftPaddingDefined2;
                    int startPadding2 = a.getDimensionPixelSize(attr, Integer.MIN_VALUE);
                    boolean startPaddingDefined2 = startPadding2 != Integer.MIN_VALUE;
                    startPaddingDefined = startPaddingDefined2;
                    paddingHorizontal2 = startPadding2;
                    rightPaddingDefined2 = rightPaddingDefined6;
                    padding2 = padding10;
                    leftPaddingDefined2 = leftPaddingDefined6;
                    break;
                case 69:
                    int padding11 = padding2;
                    boolean rightPaddingDefined7 = rightPaddingDefined2;
                    boolean leftPaddingDefined7 = leftPaddingDefined2;
                    int endPadding = a.getDimensionPixelSize(attr, Integer.MIN_VALUE);
                    boolean endPaddingDefined2 = endPadding != Integer.MIN_VALUE;
                    endPaddingDefined = endPaddingDefined2;
                    paddingVertical = endPadding;
                    rightPaddingDefined2 = rightPaddingDefined7;
                    padding2 = padding11;
                    leftPaddingDefined2 = leftPaddingDefined7;
                    break;
                case 70:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setLabelFor(a.getResourceId(attr, -1));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 71:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setAccessibilityLiveRegion(a.getInt(attr, 0));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 72:
                    float tz2 = a.getDimension(attr, 0.0f);
                    tz = tz2;
                    transformSet = true;
                    padding2 = padding2;
                    break;
                case 73:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setTransitionName(a.getString(attr));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 74:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setNestedScrollingEnabled(a.getBoolean(attr, false));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 75:
                    float elevation2 = a.getDimension(attr, 0.0f);
                    elevation = elevation2;
                    transformSet = true;
                    padding2 = padding2;
                    break;
                case 76:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setStateListAnimator(AnimatorInflater.loadStateListAnimator(context, a.getResourceId(attr, 0)));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 77:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (this.mBackgroundTint == null) {
                        this.mBackgroundTint = new TintInfo();
                    }
                    this.mBackgroundTint.mTintList = a.getColorStateList(77);
                    this.mBackgroundTint.mHasTintList = true;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 78:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (this.mBackgroundTint == null) {
                        this.mBackgroundTint = new TintInfo();
                    }
                    this.mBackgroundTint.mBlendMode = Drawable.parseBlendMode(a.getInt(78, -1), null);
                    this.mBackgroundTint.mHasTintMode = true;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 79:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (leftPadding3 >= 23 || (this instanceof FrameLayout)) {
                        setForegroundTintList(a.getColorStateList(attr));
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 80:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (leftPadding3 >= 23 || (this instanceof FrameLayout)) {
                        setForegroundTintBlendMode(Drawable.parseBlendMode(a.getInt(attr, -1), null));
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 81:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setOutlineProviderFromAttribute(a.getInt(81, 0));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 82:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setAccessibilityTraversalBefore(a.getResourceId(attr, -1));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 83:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setAccessibilityTraversalAfter(a.getResourceId(attr, -1));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 84:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    int scrollIndicators = (a.getInt(attr, 0) << 8) & SCROLL_INDICATORS_PFLAG3_MASK;
                    if (scrollIndicators != 0) {
                        this.mPrivateFlags3 |= scrollIndicators;
                        initializeScrollIndicators = true;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 85:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.getBoolean(attr, false)) {
                        startPadding |= 8388608;
                        viewFlagMasks = 8388608 | viewFlagMasks;
                        rightPaddingDefined2 = rightPaddingDefined;
                        padding2 = padding;
                        leftPaddingDefined2 = leftPaddingDefined;
                        break;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 86:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    int resourceId = a.getResourceId(attr, 0);
                    if (resourceId != 0) {
                        setPointerIcon(PointerIcon.load(context.getResources(), resourceId));
                    } else {
                        int pointerType = a.getInt(attr, 1);
                        if (pointerType != 1) {
                            setPointerIcon(PointerIcon.getSystemIcon(context, pointerType));
                        }
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 87:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.peekValue(attr) != null) {
                        forceHasOverlappingRendering(a.getBoolean(attr, true));
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 88:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    setTooltipText(a.getText(attr));
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 89:
                    int paddingHorizontal3 = a.getDimensionPixelSize(attr, -1);
                    this.mUserPaddingLeftInitial = paddingHorizontal3;
                    this.mUserPaddingRightInitial = paddingHorizontal3;
                    rightPaddingDefined2 = true;
                    paddingHorizontal = paddingHorizontal3;
                    leftPaddingDefined2 = true;
                    padding2 = padding2;
                    break;
                case 90:
                    int paddingVertical2 = a.getDimensionPixelSize(attr, -1);
                    x3 = paddingVertical2;
                    break;
                case 91:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.peekValue(attr) != null) {
                        setKeyboardNavigationCluster(a.getBoolean(attr, true));
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 92:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    this.mNextClusterForwardId = a.getResourceId(attr, -1);
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 93:
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    if (a.peekValue(attr) != null) {
                        setFocusedByDefault(a.getBoolean(attr, true));
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 94:
                    padding = padding2;
                    if (a.peekValue(attr) == null) {
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    } else {
                        String rawString2 = null;
                        if (a.getType(attr) == 1) {
                            int resId = a.getResourceId(attr, 0);
                            try {
                                CharSequence[] rawHints = a.getTextArray(attr);
                                charSequenceArr = rawHints;
                            } catch (Resources.NotFoundException e) {
                                rawString2 = getResources().getString(resId);
                                charSequenceArr = null;
                            }
                            rawString = rawString2;
                        } else {
                            String rawString3 = a.getString(attr);
                            rawString = rawString3;
                            charSequenceArr = null;
                        }
                        if (charSequenceArr == null) {
                            if (rawString == null) {
                                throw new IllegalArgumentException("Could not resolve autofillHints");
                            }
                            charSequenceArr = rawString.split(",");
                        }
                        String[] hints = new String[charSequenceArr.length];
                        rightPaddingDefined = rightPaddingDefined2;
                        int numHints = charSequenceArr.length;
                        leftPaddingDefined = leftPaddingDefined2;
                        for (int rawHintNum = 0; rawHintNum < numHints; rawHintNum++) {
                            hints[rawHintNum] = charSequenceArr[rawHintNum].toString().trim();
                        }
                        setAutofillHints(hints);
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 95:
                    padding = padding2;
                    if (a.peekValue(attr) != null) {
                        setImportantForAutofill(a.getInt(attr, 0));
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    } else {
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 96:
                    padding = padding2;
                    if (a.peekValue(attr) != null) {
                        setDefaultFocusHighlightEnabled(a.getBoolean(attr, true));
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    } else {
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 97:
                    padding = padding2;
                    if (a.peekValue(attr) != null) {
                        setScreenReaderFocusable(a.getBoolean(attr, false));
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    } else {
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 98:
                    padding = padding2;
                    if (a.peekValue(attr) != null) {
                        setAccessibilityPaneTitle(a.getString(attr));
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    } else {
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 99:
                    padding = padding2;
                    setAccessibilityHeading(a.getBoolean(attr, false));
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 100:
                    padding = padding2;
                    setOutlineSpotShadowColor(a.getColor(attr, -16777216));
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 101:
                    padding = padding2;
                    setOutlineAmbientShadowColor(a.getColor(attr, -16777216));
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 102:
                    padding = padding2;
                    this.mRenderNode.setForceDarkAllowed(a.getBoolean(attr, true));
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 103:
                    padding = padding2;
                    if (a.peekValue(attr) != null) {
                        setImportantForContentCapture(a.getInt(attr, 0));
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    } else {
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 104:
                    padding = padding2;
                    setAllowClickWhenDisabled(a.getBoolean(attr, false));
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 105:
                    padding = padding2;
                    setClipToOutline(a.getBoolean(attr, false));
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 106:
                    padding = padding2;
                    setPreferKeepClear(a.getBoolean(attr, false));
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 107:
                    padding = padding2;
                    setAutoHandwritingEnabled(a.getBoolean(attr, true));
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 113:
                    if (leftPadding3 >= 23 || (this instanceof FrameLayout)) {
                        if (this.mForegroundInfo == null) {
                            padding = padding2;
                            this.mForegroundInfo = new ForegroundInfo();
                        } else {
                            padding = padding2;
                        }
                        ForegroundInfo foregroundInfo = this.mForegroundInfo;
                        foregroundInfo.mInsidePadding = a.getBoolean(attr, foregroundInfo.mInsidePadding);
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    } else {
                        padding = padding2;
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 114:
                    setScrollCaptureHint(a.getInt(attr, 0));
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 115:
                    this.mHandwritingBoundsOffsetLeft = a.getDimension(attr, 0.0f);
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 116:
                    this.mHandwritingBoundsOffsetTop = a.getDimension(attr, 0.0f);
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 117:
                    this.mHandwritingBoundsOffsetRight = a.getDimension(attr, 0.0f);
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 118:
                    this.mHandwritingBoundsOffsetBottom = a.getDimension(attr, 0.0f);
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 119:
                    setAccessibilityDataSensitive(a.getInt(attr, 0));
                    padding = padding2;
                    rightPaddingDefined = rightPaddingDefined2;
                    leftPaddingDefined = leftPaddingDefined2;
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
                case 120:
                    if (a.peekValue(attr) != null) {
                        setIsCredential(a.getBoolean(attr, false));
                        padding = padding2;
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    } else {
                        padding = padding2;
                        rightPaddingDefined = rightPaddingDefined2;
                        leftPaddingDefined = leftPaddingDefined2;
                    }
                    rightPaddingDefined2 = rightPaddingDefined;
                    padding2 = padding;
                    leftPaddingDefined2 = leftPaddingDefined;
                    break;
            }
            padding3++;
            N = N2;
        }
        int padding12 = padding2;
        boolean rightPaddingDefined8 = rightPaddingDefined2;
        boolean leftPaddingDefined8 = leftPaddingDefined2;
        boolean hasRelativePadding = false;
        setOverScrollMode(rightPadding3);
        this.mUserPaddingStart = paddingHorizontal2;
        this.mUserPaddingEnd = paddingVertical;
        if (background != null) {
            setBackground(background);
        }
        this.mLeftPaddingDefined = leftPaddingDefined8;
        this.mRightPaddingDefined = rightPaddingDefined8;
        if (padding12 >= 0) {
            topPadding = padding12;
            targetSdkVersion = padding12;
            this.mUserPaddingLeftInitial = targetSdkVersion;
            this.mUserPaddingRightInitial = targetSdkVersion;
            bottomPadding2 = padding12;
            bottomPadding = padding12;
            rightPadding2 = padding12;
            leftPadding = paddingHorizontal;
        } else {
            targetSdkVersion = padding12;
            if (paddingHorizontal < 0) {
                leftPadding = paddingHorizontal;
                rightPadding = viewFlagMasks2;
            } else {
                leftPadding2 = paddingHorizontal;
                rightPadding = paddingHorizontal;
                leftPadding = paddingHorizontal;
                this.mUserPaddingLeftInitial = leftPadding;
                this.mUserPaddingRightInitial = leftPadding;
            }
            if (x3 < 0) {
                bottomPadding = rightPadding;
                rightPadding2 = leftPadding2;
            } else {
                topPadding = x3;
                bottomPadding2 = x3;
                bottomPadding = rightPadding;
                rightPadding2 = leftPadding2;
            }
        }
        if (isRtlCompatibilityMode()) {
            if (!this.mLeftPaddingDefined && startPaddingDefined) {
                rightPadding2 = paddingHorizontal2;
            }
            this.mUserPaddingLeftInitial = rightPadding2 >= 0 ? rightPadding2 : this.mUserPaddingLeftInitial;
            if (!this.mRightPaddingDefined && endPaddingDefined) {
                bottomPadding = paddingVertical;
            }
            this.mUserPaddingRightInitial = bottomPadding >= 0 ? bottomPadding : this.mUserPaddingRightInitial;
        } else {
            hasRelativePadding = (startPaddingDefined || endPaddingDefined) ? true : true;
            if (this.mLeftPaddingDefined && !hasRelativePadding) {
                this.mUserPaddingLeftInitial = rightPadding2;
            }
            if (this.mRightPaddingDefined && !hasRelativePadding) {
                this.mUserPaddingRightInitial = bottomPadding;
            }
        }
        int i = this.mUserPaddingLeftInitial;
        int i2 = topPadding >= 0 ? topPadding : this.mPaddingTop;
        int paddingHorizontal4 = this.mUserPaddingRightInitial;
        int overScrollMode3 = bottomPadding2 >= 0 ? bottomPadding2 : this.mPaddingBottom;
        internalSetPadding(i, i2, paddingHorizontal4, overScrollMode3);
        if (viewFlagMasks != 0) {
            setFlags(startPadding, viewFlagMasks);
        }
        if (initializeScrollbars) {
            initializeScrollbarsInternal(a);
        }
        if (initializeScrollIndicators) {
            initializeScrollIndicatorsInternal();
        }
        a.recycle();
        if (scrollbarStyle != 0) {
            recomputePadding();
        }
        if (x2 == 0 && y == 0) {
            x = x2;
        } else {
            x = x2;
            scrollTo(x, y);
        }
        if (transformSet) {
            setTranslationX(tx);
            setTranslationY(ty);
            float ty3 = tz;
            setTranslationZ(ty3);
            float tz3 = elevation;
            setElevation(tz3);
            float elevation3 = rotation;
            setRotation(elevation3);
            float rotation3 = rotationX;
            setRotationX(rotation3);
            float rotationX3 = rotationY;
            setRotationY(rotationX3);
            float rotationY3 = sx;
            setScaleX(rotationY3);
            float sx3 = sy;
            setScaleY(sx3);
        }
        if (y2 == 0 && (startPadding & 512) != 0) {
            setScrollContainer(true);
        }
        computeOpaqueFlags();
    }

    public int[] getAttributeResolutionStack(int attribute) {
        SparseArray<int[]> sparseArray;
        if (!sDebugViewAttributes || (sparseArray = this.mAttributeResolutionStacks) == null || sparseArray.get(attribute) == null) {
            return new int[0];
        }
        int[] attributeResolutionStack = this.mAttributeResolutionStacks.get(attribute);
        int stackSize = attributeResolutionStack.length;
        int i = this.mSourceLayoutId;
        if (i != 0) {
            stackSize++;
        }
        int currentIndex = 0;
        int[] stack = new int[stackSize];
        if (i != 0) {
            stack[0] = i;
            currentIndex = 0 + 1;
        }
        for (int i2 : attributeResolutionStack) {
            stack[currentIndex] = i2;
            currentIndex++;
        }
        return stack;
    }

    public Map<Integer, Integer> getAttributeSourceResourceMap() {
        HashMap<Integer, Integer> map = new HashMap<>();
        if (!sDebugViewAttributes || this.mAttributeSourceResId == null) {
            return map;
        }
        for (int i = 0; i < this.mAttributeSourceResId.size(); i++) {
            map.put(Integer.valueOf(this.mAttributeSourceResId.keyAt(i)), Integer.valueOf(this.mAttributeSourceResId.valueAt(i)));
        }
        return map;
    }

    public int getExplicitStyle() {
        if (!sDebugViewAttributes) {
            return 0;
        }
        return this.mExplicitStyle;
    }

    /* loaded from: classes4.dex */
    private static class DeclaredOnClickListener implements OnClickListener {
        private final View mHostView;
        private final String mMethodName;
        private Context mResolvedContext;
        private Method mResolvedMethod;

        public DeclaredOnClickListener(View hostView, String methodName) {
            this.mHostView = hostView;
            this.mMethodName = methodName;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (this.mResolvedMethod == null) {
                resolveMethod(this.mHostView.getContext(), this.mMethodName);
            }
            try {
                this.mResolvedMethod.invoke(this.mResolvedContext, v);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not execute non-public method for android:onClick", e);
            } catch (InvocationTargetException e2) {
                throw new IllegalStateException("Could not execute method for android:onClick", e2);
            }
        }

        private void resolveMethod(Context context, String name) {
            Method method;
            while (context != null) {
                try {
                    if (!context.isRestricted() && (method = context.getClass().getMethod(this.mMethodName, View.class)) != null) {
                        this.mResolvedMethod = method;
                        this.mResolvedContext = context;
                        return;
                    }
                } catch (NoSuchMethodException e) {
                }
                if (context instanceof ContextWrapper) {
                    context = ((ContextWrapper) context).getBaseContext();
                } else {
                    context = null;
                }
            }
            int id = this.mHostView.getId();
            String idText = id == -1 ? "" : " with id '" + this.mHostView.getContext().getResources().getResourceEntryName(id) + "'";
            throw new IllegalStateException("Could not find method " + this.mMethodName + "(View) in a parent or ancestor Context for android:onClick attribute defined on view " + this.mHostView.getClass() + idText);
        }
    }

    View() {
        InputEventConsistencyVerifier inputEventConsistencyVerifier;
        this.mCurrentAnimation = null;
        this.mRecreateDisplayList = false;
        this.mID = -1;
        this.mAutofillViewId = -1;
        this.mAccessibilityViewId = -1;
        this.mAccessibilityCursorPosition = -1;
        this.mTag = null;
        this.mTransientStateCount = 0;
        this.mClipBounds = null;
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mExplicitAccessibilityDataSensitive = 0;
        this.mInferredAccessibilityDataSensitive = 0;
        this.mLabelForId = -1;
        this.mAccessibilityTraversalBeforeId = -1;
        this.mAccessibilityTraversalAfterId = -1;
        this.mLeftPaddingDefined = false;
        this.mRightPaddingDefined = false;
        this.mOldWidthMeasureSpec = Integer.MIN_VALUE;
        this.mOldHeightMeasureSpec = Integer.MIN_VALUE;
        this.mLongClickX = Float.NaN;
        this.mLongClickY = Float.NaN;
        this.mDrawableState = null;
        this.mOutlineProvider = ViewOutlineProvider.BACKGROUND;
        this.mNextFocusLeftId = -1;
        this.mNextFocusRightId = -1;
        this.mNextFocusUpId = -1;
        this.mNextFocusDownId = -1;
        this.mNextFocusForwardId = -1;
        this.mNextClusterForwardId = -1;
        this.mDefaultFocusHighlightEnabled = true;
        this.mPendingCheckForTap = null;
        this.mTouchDelegate = null;
        this.mHoveringTouchDelegate = false;
        this.mDrawingCacheBackgroundColor = 0;
        this.mAnimator = null;
        this.mLayerType = 0;
        if (!InputEventConsistencyVerifier.isInstrumentationEnabled()) {
            inputEventConsistencyVerifier = null;
        } else {
            inputEventConsistencyVerifier = new InputEventConsistencyVerifier(this, 0);
        }
        this.mInputEventConsistencyVerifier = inputEventConsistencyVerifier;
        this.mSourceLayoutId = 0;
        this.mUnbufferedInputSource = 0;
        this.mShouldFakeFocus = false;
        this.mResources = null;
        this.mRenderNode = RenderNode.create(getClass().getName(), new ViewAnimationHostBridge(this));
    }

    public final boolean isShowingLayoutBounds() {
        AttachInfo attachInfo;
        return DEBUG_DRAW || ((attachInfo = this.mAttachInfo) != null && attachInfo.mDebugLayout);
    }

    public final void setShowingLayoutBounds(boolean debugLayout) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mDebugLayout = debugLayout;
        }
    }

    private static SparseArray<String> getAttributeMap() {
        if (mAttributeMap == null) {
            mAttributeMap = new SparseArray<>();
        }
        return mAttributeMap;
    }

    private void retrieveExplicitStyle(Resources.Theme theme, AttributeSet attrs) {
        if (!sDebugViewAttributes) {
            return;
        }
        this.mExplicitStyle = theme.getExplicitStyle(attrs);
    }

    public final void saveAttributeDataForStyleable(Context context, int[] styleable, AttributeSet attrs, TypedArray t, int defStyleAttr, int defStyleRes) {
        if (!sDebugViewAttributes) {
            return;
        }
        int[] attributeResolutionStack = context.getTheme().getAttributeResolutionStack(defStyleAttr, defStyleRes, this.mExplicitStyle);
        if (this.mAttributeResolutionStacks == null) {
            this.mAttributeResolutionStacks = new SparseArray<>();
        }
        if (this.mAttributeSourceResId == null) {
            this.mAttributeSourceResId = new SparseIntArray();
        }
        int indexCount = t.getIndexCount();
        for (int j = 0; j < indexCount; j++) {
            int index = t.getIndex(j);
            this.mAttributeSourceResId.append(styleable[index], t.getSourceResourceId(index, 0));
            this.mAttributeResolutionStacks.append(styleable[index], attributeResolutionStack);
        }
    }

    private void saveAttributeData(AttributeSet attrs, TypedArray t) {
        int resourceId;
        int attrsCount = attrs == null ? 0 : attrs.getAttributeCount();
        int indexCount = t.getIndexCount();
        String[] attributes = new String[(attrsCount + indexCount) * 2];
        int i = 0;
        for (int j = 0; j < attrsCount; j++) {
            attributes[i] = attrs.getAttributeName(j);
            attributes[i + 1] = attrs.getAttributeValue(j);
            i += 2;
        }
        Resources res = t.getResources();
        SparseArray<String> attributeMap = getAttributeMap();
        int i2 = i;
        for (int j2 = 0; j2 < indexCount; j2++) {
            int index = t.getIndex(j2);
            if (t.hasValueOrEmpty(index) && (resourceId = t.getResourceId(index, 0)) != 0) {
                String resourceName = attributeMap.get(resourceId);
                if (resourceName == null) {
                    try {
                        resourceName = res.getResourceName(resourceId);
                    } catch (Resources.NotFoundException e) {
                        resourceName = "0x" + Integer.toHexString(resourceId);
                    }
                    attributeMap.put(resourceId, resourceName);
                }
                attributes[i2] = resourceName;
                attributes[i2 + 1] = t.getString(index);
                i2 += 2;
            }
        }
        String[] trimmed = new String[i2];
        System.arraycopy(attributes, 0, trimmed, 0, i2);
        this.mAttributes = trimmed;
    }

    public String toString() {
        StringBuilder out = new StringBuilder(256);
        out.append(getClass().getName());
        out.append('{');
        out.append(Integer.toHexString(System.identityHashCode(this)));
        out.append(' ');
        switch (this.mViewFlags & 12) {
            case 0:
                out.append('V');
                break;
            case 4:
                out.append('I');
                break;
            case 8:
                out.append('G');
                break;
            default:
                out.append('.');
                break;
        }
        out.append((this.mViewFlags & 1) == 1 ? 'F' : '.');
        out.append((this.mViewFlags & 32) == 0 ? DateFormat.DAY : '.');
        out.append((this.mViewFlags & 128) == 128 ? '.' : 'D');
        out.append((256 & this.mViewFlags) != 0 ? 'H' : '.');
        out.append((this.mViewFlags & 512) == 0 ? '.' : 'V');
        out.append((this.mViewFlags & 16384) != 0 ? 'C' : '.');
        out.append((this.mViewFlags & 2097152) != 0 ? DateFormat.STANDALONE_MONTH : '.');
        out.append((this.mViewFlags & 8388608) != 0 ? 'X' : '.');
        out.append(' ');
        out.append((this.mPrivateFlags & 8) != 0 ? 'R' : '.');
        out.append((this.mPrivateFlags & 2) == 0 ? '.' : 'F');
        out.append((this.mPrivateFlags & 4) != 0 ? 'S' : '.');
        int i = this.mPrivateFlags;
        if ((33554432 & i) != 0) {
            out.append('p');
        } else {
            out.append((i & 16384) != 0 ? 'P' : '.');
        }
        out.append((this.mPrivateFlags & 268435456) == 0 ? '.' : 'H');
        out.append((this.mPrivateFlags & 1073741824) != 0 ? DateFormat.CAPITAL_AM_PM : '.');
        out.append((this.mPrivateFlags & Integer.MIN_VALUE) == 0 ? '.' : 'I');
        out.append((this.mPrivateFlags & 2097152) != 0 ? 'D' : '.');
        out.append(' ');
        out.append(this.mLeft);
        out.append(',');
        out.append(this.mTop);
        out.append('-');
        out.append(this.mRight);
        out.append(',');
        out.append(this.mBottom);
        appendId(out);
        if (this.mAutofillId != null) {
            out.append(" aid=");
            out.append(this.mAutofillId);
        }
        out.append("}");
        return out.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void appendId(StringBuilder out) {
        String pkgname;
        int id = getId();
        if (id != -1) {
            out.append(" #");
            out.append(Integer.toHexString(id));
            Resources r = this.mResources;
            if (id > 0 && Resources.resourceHasPackage(id) && r != null) {
                switch ((-16777216) & id) {
                    case 16777216:
                        pkgname = "android";
                        String typename = r.getResourceTypeName(id);
                        String entryname = r.getResourceEntryName(id);
                        out.append(" ");
                        out.append(pkgname);
                        out.append(":");
                        out.append(typename);
                        out.append("/");
                        out.append(entryname);
                        return;
                    case 2130706432:
                        pkgname = "app";
                        String typename2 = r.getResourceTypeName(id);
                        String entryname2 = r.getResourceEntryName(id);
                        out.append(" ");
                        out.append(pkgname);
                        out.append(":");
                        out.append(typename2);
                        out.append("/");
                        out.append(entryname2);
                        return;
                    default:
                        try {
                            pkgname = r.getResourcePackageName(id);
                            String typename22 = r.getResourceTypeName(id);
                            String entryname22 = r.getResourceEntryName(id);
                            out.append(" ");
                            out.append(pkgname);
                            out.append(":");
                            out.append(typename22);
                            out.append("/");
                            out.append(entryname22);
                            return;
                        } catch (Resources.NotFoundException e) {
                            return;
                        }
                }
            }
        }
    }

    protected void initializeFadingEdge(TypedArray a) {
        TypedArray arr = this.mContext.obtainStyledAttributes(C4057R.styleable.View);
        initializeFadingEdgeInternal(arr);
        arr.recycle();
    }

    protected void initializeFadingEdgeInternal(TypedArray a) {
        initScrollCache();
        this.mScrollCache.fadingEdgeLength = a.getDimensionPixelSize(25, ViewConfiguration.get(this.mContext).getScaledFadingEdgeLength());
    }

    public int getVerticalFadingEdgeLength() {
        ScrollabilityCache cache;
        if (isVerticalFadingEdgeEnabled() && (cache = this.mScrollCache) != null) {
            return cache.fadingEdgeLength;
        }
        return 0;
    }

    public void setFadingEdgeLength(int length) {
        initScrollCache();
        this.mScrollCache.fadingEdgeLength = length;
    }

    public int getHorizontalFadingEdgeLength() {
        ScrollabilityCache cache;
        if (isHorizontalFadingEdgeEnabled() && (cache = this.mScrollCache) != null) {
            return cache.fadingEdgeLength;
        }
        return 0;
    }

    public int getVerticalScrollbarWidth() {
        ScrollBarDrawable scrollBar;
        ScrollabilityCache cache = this.mScrollCache;
        if (cache == null || (scrollBar = cache.scrollBar) == null) {
            return 0;
        }
        int size = scrollBar.getSize(true);
        if (size <= 0) {
            return cache.scrollBarSize;
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getHorizontalScrollbarHeight() {
        ScrollBarDrawable scrollBar;
        ScrollabilityCache cache = this.mScrollCache;
        if (cache == null || (scrollBar = cache.scrollBar) == null) {
            return 0;
        }
        int size = scrollBar.getSize(false);
        if (size <= 0) {
            return cache.scrollBarSize;
        }
        return size;
    }

    protected void initializeScrollbars(TypedArray a) {
        TypedArray arr = this.mContext.obtainStyledAttributes(C4057R.styleable.View);
        initializeScrollbarsInternal(arr);
        arr.recycle();
    }

    private void initializeScrollBarDrawable() {
        initScrollCache();
        if (this.mScrollCache.scrollBar == null) {
            this.mScrollCache.scrollBar = new ScrollBarDrawable();
            this.mScrollCache.scrollBar.setState(getDrawableState());
            this.mScrollCache.scrollBar.setCallback(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initializeScrollbarsInternal(TypedArray a) {
        initScrollCache();
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache.scrollBar == null) {
            scrollabilityCache.scrollBar = new ScrollBarDrawable();
            scrollabilityCache.scrollBar.setState(getDrawableState());
            scrollabilityCache.scrollBar.setCallback(this);
        }
        boolean fadeScrollbars = a.getBoolean(47, true);
        if (!fadeScrollbars) {
            scrollabilityCache.state = 1;
        }
        scrollabilityCache.fadeScrollBars = fadeScrollbars;
        scrollabilityCache.scrollBarFadeDuration = a.getInt(45, ViewConfiguration.getScrollBarFadeDuration());
        scrollabilityCache.scrollBarDefaultDelayBeforeFade = a.getInt(46, ViewConfiguration.getScrollDefaultDelay());
        scrollabilityCache.scrollBarSize = a.getDimensionPixelSize(1, ViewConfiguration.get(this.mContext).getScaledScrollBarSize());
        scrollabilityCache.scrollBar.setHorizontalTrackDrawable(a.getDrawable(4));
        Drawable thumb = a.getDrawable(2);
        if (thumb != null) {
            scrollabilityCache.scrollBar.setHorizontalThumbDrawable(thumb);
        }
        boolean alwaysDraw = a.getBoolean(6, false);
        if (alwaysDraw) {
            scrollabilityCache.scrollBar.setAlwaysDrawHorizontalTrack(true);
        }
        Drawable track = a.getDrawable(5);
        scrollabilityCache.scrollBar.setVerticalTrackDrawable(track);
        Drawable thumb2 = a.getDrawable(3);
        if (thumb2 != null) {
            scrollabilityCache.scrollBar.setVerticalThumbDrawable(thumb2);
        }
        boolean alwaysDraw2 = a.getBoolean(7, false);
        if (alwaysDraw2) {
            scrollabilityCache.scrollBar.setAlwaysDrawVerticalTrack(true);
        }
        int layoutDirection = getLayoutDirection();
        if (track != null) {
            track.setLayoutDirection(layoutDirection);
        }
        if (thumb2 != null) {
            thumb2.setLayoutDirection(layoutDirection);
        }
        resolvePadding();
    }

    public void setVerticalScrollbarThumbDrawable(Drawable drawable) {
        initializeScrollBarDrawable();
        this.mScrollCache.scrollBar.setVerticalThumbDrawable(drawable);
    }

    public void setVerticalScrollbarTrackDrawable(Drawable drawable) {
        initializeScrollBarDrawable();
        this.mScrollCache.scrollBar.setVerticalTrackDrawable(drawable);
    }

    public void setHorizontalScrollbarThumbDrawable(Drawable drawable) {
        initializeScrollBarDrawable();
        this.mScrollCache.scrollBar.setHorizontalThumbDrawable(drawable);
    }

    public void setHorizontalScrollbarTrackDrawable(Drawable drawable) {
        initializeScrollBarDrawable();
        this.mScrollCache.scrollBar.setHorizontalTrackDrawable(drawable);
    }

    public Drawable getVerticalScrollbarThumbDrawable() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache != null) {
            return scrollabilityCache.scrollBar.getVerticalThumbDrawable();
        }
        return null;
    }

    public Drawable getVerticalScrollbarTrackDrawable() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache != null) {
            return scrollabilityCache.scrollBar.getVerticalTrackDrawable();
        }
        return null;
    }

    public Drawable getHorizontalScrollbarThumbDrawable() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache != null) {
            return scrollabilityCache.scrollBar.getHorizontalThumbDrawable();
        }
        return null;
    }

    public Drawable getHorizontalScrollbarTrackDrawable() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache != null) {
            return scrollabilityCache.scrollBar.getHorizontalTrackDrawable();
        }
        return null;
    }

    private void initializeScrollIndicatorsInternal() {
        if (this.mScrollIndicatorDrawable == null) {
            this.mScrollIndicatorDrawable = this.mContext.getDrawable(C4057R.C4058drawable.scroll_indicator_material);
        }
    }

    private void initScrollCache() {
        if (this.mScrollCache == null) {
            this.mScrollCache = new ScrollabilityCache(ViewConfiguration.get(this.mContext), this);
        }
    }

    private ScrollabilityCache getScrollCache() {
        initScrollCache();
        return this.mScrollCache;
    }

    public void setVerticalScrollbarPosition(int position) {
        if (this.mVerticalScrollbarPosition != position) {
            this.mVerticalScrollbarPosition = position;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    public int getVerticalScrollbarPosition() {
        return this.mVerticalScrollbarPosition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOnScrollbar(float x, float y) {
        if (this.mScrollCache == null) {
            return false;
        }
        float x2 = x + getScrollX();
        float y2 = y + getScrollY();
        boolean canScrollVertically = computeVerticalScrollRange() > computeVerticalScrollExtent();
        if (isVerticalScrollBarEnabled() && !isVerticalScrollBarHidden() && canScrollVertically) {
            Rect touchBounds = this.mScrollCache.mScrollBarTouchBounds;
            getVerticalScrollBarBounds(null, touchBounds);
            if (touchBounds.contains((int) x2, (int) y2)) {
                return true;
            }
        }
        boolean canScrollHorizontally = computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        if (isHorizontalScrollBarEnabled() && canScrollHorizontally) {
            Rect touchBounds2 = this.mScrollCache.mScrollBarTouchBounds;
            getHorizontalScrollBarBounds(null, touchBounds2);
            if (touchBounds2.contains((int) x2, (int) y2)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOnScrollbarThumb(float x, float y) {
        return isOnVerticalScrollbarThumb(x, y) || isOnHorizontalScrollbarThumb(x, y);
    }

    private boolean isOnVerticalScrollbarThumb(float x, float y) {
        int range;
        int extent;
        if (this.mScrollCache != null && isVerticalScrollBarEnabled() && !isVerticalScrollBarHidden() && (range = computeVerticalScrollRange()) > (extent = computeVerticalScrollExtent())) {
            float x2 = x + getScrollX();
            float y2 = y + getScrollY();
            Rect bounds = this.mScrollCache.mScrollBarBounds;
            Rect touchBounds = this.mScrollCache.mScrollBarTouchBounds;
            getVerticalScrollBarBounds(bounds, touchBounds);
            int offset = computeVerticalScrollOffset();
            int thumbLength = ScrollBarUtils.getThumbLength(bounds.height(), bounds.width(), extent, range);
            int thumbOffset = ScrollBarUtils.getThumbOffset(bounds.height(), thumbLength, extent, range, offset);
            int thumbTop = bounds.top + thumbOffset;
            int adjust = Math.max(this.mScrollCache.scrollBarMinTouchTarget - thumbLength, 0) / 2;
            if (x2 >= touchBounds.left && x2 <= touchBounds.right && y2 >= thumbTop - adjust && y2 <= thumbTop + thumbLength + adjust) {
                return true;
            }
        }
        return false;
    }

    private boolean isOnHorizontalScrollbarThumb(float x, float y) {
        int range;
        int extent;
        if (this.mScrollCache != null && isHorizontalScrollBarEnabled() && (range = computeHorizontalScrollRange()) > (extent = computeHorizontalScrollExtent())) {
            float x2 = x + getScrollX();
            float y2 = y + getScrollY();
            Rect bounds = this.mScrollCache.mScrollBarBounds;
            Rect touchBounds = this.mScrollCache.mScrollBarTouchBounds;
            getHorizontalScrollBarBounds(bounds, touchBounds);
            int offset = computeHorizontalScrollOffset();
            int thumbLength = ScrollBarUtils.getThumbLength(bounds.width(), bounds.height(), extent, range);
            int thumbOffset = ScrollBarUtils.getThumbOffset(bounds.width(), thumbLength, extent, range, offset);
            int thumbLeft = bounds.left + thumbOffset;
            int adjust = Math.max(this.mScrollCache.scrollBarMinTouchTarget - thumbLength, 0) / 2;
            if (x2 >= thumbLeft - adjust && x2 <= thumbLeft + thumbLength + adjust && y2 >= touchBounds.top && y2 <= touchBounds.bottom) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDraggingScrollBar() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return (scrollabilityCache == null || scrollabilityCache.mScrollBarDraggingState == 0) ? false : true;
    }

    @RemotableViewMethod
    public void setScrollIndicators(int indicators) {
        setScrollIndicators(indicators, 63);
    }

    public void setScrollIndicators(int indicators, int mask) {
        int mask2 = (mask << 8) & SCROLL_INDICATORS_PFLAG3_MASK;
        int indicators2 = (indicators << 8) & mask2;
        int i = this.mPrivateFlags3;
        int updatedFlags = ((~mask2) & i) | indicators2;
        if (i != updatedFlags) {
            this.mPrivateFlags3 = updatedFlags;
            if (indicators2 != 0) {
                initializeScrollIndicatorsInternal();
            }
            invalidate();
        }
    }

    public int getScrollIndicators() {
        return (this.mPrivateFlags3 & SCROLL_INDICATORS_PFLAG3_MASK) >>> 8;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ListenerInfo getListenerInfo() {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null) {
            return listenerInfo;
        }
        ListenerInfo listenerInfo2 = new ListenerInfo();
        this.mListenerInfo = listenerInfo2;
        return listenerInfo2;
    }

    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        getListenerInfo().mOnScrollChangeListener = l;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        getListenerInfo().mOnFocusChangeListener = l;
    }

    public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
        ListenerInfo li = getListenerInfo();
        if (li.mOnLayoutChangeListeners == null) {
            li.mOnLayoutChangeListeners = new ArrayList();
        }
        if (!li.mOnLayoutChangeListeners.contains(listener)) {
            li.mOnLayoutChangeListeners.add(listener);
        }
    }

    public void removeOnLayoutChangeListener(OnLayoutChangeListener listener) {
        ListenerInfo li = this.mListenerInfo;
        if (li == null || li.mOnLayoutChangeListeners == null) {
            return;
        }
        li.mOnLayoutChangeListeners.remove(listener);
    }

    public void addOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        ListenerInfo li = getListenerInfo();
        if (li.mOnAttachStateChangeListeners == null) {
            li.mOnAttachStateChangeListeners = new CopyOnWriteArrayList();
        }
        li.mOnAttachStateChangeListeners.add(listener);
    }

    public void removeOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        ListenerInfo li = this.mListenerInfo;
        if (li == null || li.mOnAttachStateChangeListeners == null) {
            return;
        }
        li.mOnAttachStateChangeListeners.remove(listener);
    }

    public OnFocusChangeListener getOnFocusChangeListener() {
        ListenerInfo li = this.mListenerInfo;
        if (li != null) {
            return li.mOnFocusChangeListener;
        }
        return null;
    }

    public void setOnClickListener(OnClickListener l) {
        if (!isClickable()) {
            setClickable(true);
        }
        getListenerInfo().mOnClickListener = l;
    }

    public boolean hasOnClickListeners() {
        ListenerInfo li = this.mListenerInfo;
        return (li == null || li.mOnClickListener == null) ? false : true;
    }

    public void setOnLongClickListener(OnLongClickListener l) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        getListenerInfo().mOnLongClickListener = l;
    }

    public boolean hasOnLongClickListeners() {
        ListenerInfo li = this.mListenerInfo;
        return (li == null || li.mOnLongClickListener == null) ? false : true;
    }

    public OnLongClickListener getOnLongClickListener() {
        ListenerInfo li = this.mListenerInfo;
        if (li != null) {
            return li.mOnLongClickListener;
        }
        return null;
    }

    public void setOnContextClickListener(OnContextClickListener l) {
        if (!isContextClickable()) {
            setContextClickable(true);
        }
        getListenerInfo().mOnContextClickListener = l;
    }

    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        getListenerInfo().mOnCreateContextMenuListener = l;
    }

    public void addFrameMetricsListener(Window window, Window.OnFrameMetricsAvailableListener listener, Handler handler) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            if (attachInfo.mThreadedRenderer != null) {
                if (this.mFrameMetricsObservers == null) {
                    this.mFrameMetricsObservers = new ArrayList<>();
                }
                FrameMetricsObserver fmo = new FrameMetricsObserver(window, handler, listener);
                this.mFrameMetricsObservers.add(fmo);
                this.mAttachInfo.mThreadedRenderer.addObserver(fmo.getRendererObserver());
                return;
            }
            Log.m104w(VIEW_LOG_TAG, "View not hardware-accelerated. Unable to observe frame stats");
            return;
        }
        if (this.mFrameMetricsObservers == null) {
            this.mFrameMetricsObservers = new ArrayList<>();
        }
        this.mFrameMetricsObservers.add(new FrameMetricsObserver(window, handler, listener));
    }

    public void removeFrameMetricsListener(Window.OnFrameMetricsAvailableListener listener) {
        ThreadedRenderer renderer = getThreadedRenderer();
        FrameMetricsObserver fmo = findFrameMetricsObserver(listener);
        if (fmo == null) {
            throw new IllegalArgumentException("attempt to remove OnFrameMetricsAvailableListener that was never added");
        }
        ArrayList<FrameMetricsObserver> arrayList = this.mFrameMetricsObservers;
        if (arrayList != null) {
            arrayList.remove(fmo);
            if (renderer != null) {
                renderer.removeObserver(fmo.getRendererObserver());
            }
        }
    }

    private void registerPendingFrameMetricsObservers() {
        if (this.mFrameMetricsObservers != null) {
            ThreadedRenderer renderer = getThreadedRenderer();
            if (renderer != null) {
                Iterator<FrameMetricsObserver> it = this.mFrameMetricsObservers.iterator();
                while (it.hasNext()) {
                    FrameMetricsObserver fmo = it.next();
                    renderer.addObserver(fmo.getRendererObserver());
                }
                return;
            }
            Log.m104w(VIEW_LOG_TAG, "View not hardware-accelerated. Unable to observe frame stats");
        }
    }

    private FrameMetricsObserver findFrameMetricsObserver(Window.OnFrameMetricsAvailableListener listener) {
        if (this.mFrameMetricsObservers != null) {
            for (int i = 0; i < this.mFrameMetricsObservers.size(); i++) {
                FrameMetricsObserver observer = this.mFrameMetricsObservers.get(i);
                if (observer.mListener == listener) {
                    return observer;
                }
            }
            return null;
        }
        return null;
    }

    public void setNotifyAutofillManagerOnClick(boolean notify) {
        if (notify) {
            this.mPrivateFlags |= 536870912;
        } else {
            this.mPrivateFlags &= -536870913;
        }
    }

    private void notifyAutofillManagerOnClick() {
        if ((this.mPrivateFlags & 536870912) != 0) {
            try {
                getAutofillManager().notifyViewClicked(this);
            } finally {
                this.mPrivateFlags = (-536870913) & this.mPrivateFlags;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean performClickInternal() {
        notifyAutofillManagerOnClick();
        return performClick();
    }

    public boolean performClick() {
        boolean result;
        notifyAutofillManagerOnClick();
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnClickListener != null) {
            playSoundEffect(0);
            li.mOnClickListener.onClick(this);
            result = true;
        } else {
            result = false;
        }
        sendAccessibilityEvent(1);
        notifyEnterOrExitForAutoFillIfNeeded(true);
        return result;
    }

    public boolean callOnClick() {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnClickListener != null) {
            li.mOnClickListener.onClick(this);
            return true;
        }
        return false;
    }

    public boolean performLongClick() {
        return performLongClickInternal(this.mLongClickX, this.mLongClickY);
    }

    public boolean performLongClick(float x, float y) {
        this.mLongClickX = x;
        this.mLongClickY = y;
        boolean handled = performLongClick();
        this.mLongClickX = Float.NaN;
        this.mLongClickY = Float.NaN;
        return handled;
    }

    private boolean performLongClickInternal(float x, float y) {
        sendAccessibilityEvent(2);
        boolean handled = false;
        ListenerInfo li = this.mListenerInfo;
        boolean shouldPerformHapticFeedback = true;
        if (li != null && li.mOnLongClickListener != null && (handled = li.mOnLongClickListener.onLongClick(this))) {
            shouldPerformHapticFeedback = li.mOnLongClickListener.onLongClickUseDefaultHapticFeedback(this);
        }
        if (!handled) {
            boolean isAnchored = (Float.isNaN(x) || Float.isNaN(y)) ? false : true;
            handled = isAnchored ? showContextMenu(x, y) : showContextMenu();
        }
        if ((this.mViewFlags & 1073741824) == 1073741824 && !handled) {
            handled = showLongClickTooltip((int) x, (int) y);
        }
        if (handled && shouldPerformHapticFeedback) {
            performHapticFeedback(0);
        }
        return handled;
    }

    public boolean performContextClick(float x, float y) {
        return performContextClick();
    }

    public boolean performContextClick() {
        sendAccessibilityEvent(8388608);
        boolean handled = false;
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnContextClickListener != null) {
            handled = li.mOnContextClickListener.onContextClick(this);
        }
        if (handled) {
            performHapticFeedback(6);
        }
        return handled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean performButtonActionOnTouchDown(MotionEvent event) {
        if (event.isFromSource(8194) && (event.getButtonState() & 2) != 0) {
            showContextMenu(event.getX(), event.getY());
            this.mPrivateFlags |= 67108864;
            return true;
        }
        return false;
    }

    public boolean showContextMenu() {
        return getParent().showContextMenuForChild(this);
    }

    public boolean showContextMenu(float x, float y) {
        return getParent().showContextMenuForChild(this, x, y);
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        return startActionMode(callback, 0);
    }

    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        ViewParent parent = getParent();
        if (parent == null) {
            return null;
        }
        try {
            return parent.startActionModeForChild(this, callback, type);
        } catch (AbstractMethodError e) {
            return parent.startActionModeForChild(this, callback);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        this.mStartActivityRequestWho = "@android:view:" + System.identityHashCode(this);
        getContext().startActivityForResult(this.mStartActivityRequestWho, intent, requestCode, null);
    }

    public boolean dispatchActivityResult(String who, int requestCode, int resultCode, Intent data) {
        String str = this.mStartActivityRequestWho;
        if (str != null && str.equals(who)) {
            onActivityResult(requestCode, resultCode, data);
            this.mStartActivityRequestWho = null;
            return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void setOnKeyListener(OnKeyListener l) {
        getListenerInfo().mOnKeyListener = l;
    }

    public void setOnTouchListener(OnTouchListener l) {
        getListenerInfo().mOnTouchListener = l;
    }

    public void setOnGenericMotionListener(OnGenericMotionListener l) {
        getListenerInfo().mOnGenericMotionListener = l;
    }

    public void setOnHoverListener(OnHoverListener l) {
        getListenerInfo().mOnHoverListener = l;
    }

    public void setOnDragListener(OnDragListener l) {
        getListenerInfo().mOnDragListener = l;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleFocusGainInternal(int direction, Rect previouslyFocusedRect) {
        int i = this.mPrivateFlags;
        if ((i & 2) == 0) {
            this.mPrivateFlags = i | 2;
            View oldFocus = this.mAttachInfo != null ? getRootView().findFocus() : null;
            ViewParent viewParent = this.mParent;
            if (viewParent != null) {
                viewParent.requestChildFocus(this, this);
                updateFocusedInCluster(oldFocus, direction);
            }
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null) {
                attachInfo.mTreeObserver.dispatchOnGlobalFocusChange(oldFocus, this);
            }
            onFocusChanged(true, direction, previouslyFocusedRect);
            refreshDrawableState();
        }
    }

    public final void setRevealOnFocusHint(boolean revealOnFocus) {
        if (revealOnFocus) {
            this.mPrivateFlags3 &= -67108865;
        } else {
            this.mPrivateFlags3 |= 67108864;
        }
    }

    public final boolean getRevealOnFocusHint() {
        return (this.mPrivateFlags3 & 67108864) == 0;
    }

    public void getHotspotBounds(Rect outRect) {
        Drawable background = getBackground();
        if (background != null) {
            background.getHotspotBounds(outRect);
        } else {
            getBoundsOnScreen(outRect);
        }
    }

    public boolean requestRectangleOnScreen(Rect rectangle) {
        return requestRectangleOnScreen(rectangle, false);
    }

    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        if (this.mParent == null) {
            return false;
        }
        View child = this;
        AttachInfo attachInfo = this.mAttachInfo;
        RectF position = attachInfo != null ? attachInfo.mTmpTransformRect : new RectF();
        position.set(rectangle);
        ViewParent parent = this.mParent;
        boolean scrolled = false;
        while (parent != null) {
            rectangle.set((int) position.left, (int) position.top, (int) position.right, (int) position.bottom);
            scrolled |= parent.requestChildRectangleOnScreen(child, rectangle, immediate);
            if (!(parent instanceof View)) {
                break;
            }
            position.offset(child.mLeft - child.getScrollX(), child.mTop - child.getScrollY());
            View child2 = parent;
            child = child2;
            parent = child.getParent();
        }
        return scrolled;
    }

    public void clearFocus() {
        boolean refocus = sAlwaysAssignFocus || !isInTouchMode();
        clearFocusInternal(null, true, refocus);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearFocusInternal(View focused, boolean propagate, boolean refocus) {
        ViewParent viewParent;
        int i = this.mPrivateFlags;
        if ((i & 2) != 0) {
            this.mPrivateFlags = i & (-3);
            clearParentsWantFocus();
            if (propagate && (viewParent = this.mParent) != null) {
                viewParent.clearChildFocus(this);
            }
            onFocusChanged(false, 0, null);
            refreshDrawableState();
            if (propagate) {
                if (!refocus || !rootViewRequestFocus()) {
                    notifyGlobalFocusCleared(this);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyGlobalFocusCleared(View oldFocus) {
        AttachInfo attachInfo;
        if (oldFocus != null && (attachInfo = this.mAttachInfo) != null) {
            attachInfo.mTreeObserver.dispatchOnGlobalFocusChange(oldFocus, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean rootViewRequestFocus() {
        View root = getRootView();
        return root != null && root.requestFocus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unFocus(View focused) {
        clearFocusInternal(focused, false, false);
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public boolean hasFocus() {
        return (this.mPrivateFlags & 2) != 0;
    }

    public boolean hasFocusable() {
        return hasFocusable(!sHasFocusableExcludeAutoFocusable, false);
    }

    public boolean hasExplicitFocusable() {
        return hasFocusable(false, true);
    }

    boolean hasFocusable(boolean allowAutoFocus, boolean dispatchExplicit) {
        if (!isFocusableInTouchMode()) {
            for (ViewParent p = this.mParent; p instanceof ViewGroup; p = p.getParent()) {
                ViewGroup g = (ViewGroup) p;
                if (g.shouldBlockFocusForTouchscreen()) {
                    return false;
                }
            }
        }
        int i = this.mViewFlags;
        if ((i & 12) == 0 && (i & 32) == 0) {
            return (allowAutoFocus || getFocusable() != 16) && isFocusable();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus) {
            sendAccessibilityEvent(8);
        } else {
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
        switchDefaultFocusHighlight();
        if (!gainFocus) {
            if (isPressed()) {
                setPressed(false);
            }
            if (hasWindowFocus()) {
                notifyFocusChangeToImeFocusController(false);
            }
            onFocusLost();
        } else if (hasWindowFocus()) {
            notifyFocusChangeToImeFocusController(true);
        }
        invalidate(true);
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnFocusChangeListener != null) {
            li.mOnFocusChangeListener.onFocusChange(this, gainFocus);
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mKeyDispatchState.reset(this);
        }
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.onDescendantUnbufferedRequested();
        }
        notifyEnterOrExitForAutoFillIfNeeded(gainFocus);
        updatePreferKeepClearForFocus();
    }

    private void notifyFocusChangeToImeFocusController(boolean hasFocus) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return;
        }
        attachInfo.mViewRootImpl.getImeFocusController().onViewFocusChanged(this, hasFocus);
    }

    public void notifyEnterOrExitForAutoFillIfNeeded(boolean enter) {
        AutofillManager afm;
        if (canNotifyAutofillEnterExitEvent() && (afm = getAutofillManager()) != null) {
            if (enter) {
                if (!isLaidOut() || !isVisibleToUser()) {
                    this.mPrivateFlags3 |= 134217728;
                } else if (isVisibleToUser()) {
                    if (isFocused()) {
                        afm.notifyViewEntered(this);
                    } else {
                        afm.notifyViewEnteredForFillDialog(this);
                    }
                }
            } else if (!isFocused()) {
                afm.notifyViewExited(this);
            }
        }
    }

    public void setAccessibilityPaneTitle(CharSequence accessibilityPaneTitle) {
        if (!TextUtils.equals(accessibilityPaneTitle, this.mAccessibilityPaneTitle)) {
            boolean currentPaneTitleEmpty = this.mAccessibilityPaneTitle == null;
            boolean newPaneTitleEmpty = accessibilityPaneTitle == null;
            this.mAccessibilityPaneTitle = accessibilityPaneTitle;
            if (accessibilityPaneTitle != null && getImportantForAccessibility() == 0) {
                setImportantForAccessibility(1);
            }
            if (currentPaneTitleEmpty) {
                notifyViewAccessibilityStateChangedIfNeeded(16);
            } else if (newPaneTitleEmpty) {
                notifyViewAccessibilityStateChangedIfNeeded(32);
            } else {
                notifyViewAccessibilityStateChangedIfNeeded(8);
            }
        }
    }

    public CharSequence getAccessibilityPaneTitle() {
        return this.mAccessibilityPaneTitle;
    }

    private boolean isAccessibilityPane() {
        return this.mAccessibilityPaneTitle != null;
    }

    @Override // android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEvent(int eventType) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.sendAccessibilityEvent(this, eventType);
        } else {
            sendAccessibilityEventInternal(eventType);
        }
    }

    public void announceForAccessibility(CharSequence text) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled() && this.mParent != null) {
            AccessibilityEvent event = AccessibilityEvent.obtain(16384);
            onInitializeAccessibilityEvent(event);
            event.getText().add(text);
            event.setContentDescription(null);
            this.mParent.requestSendAccessibilityEvent(this, event);
        }
    }

    public void sendAccessibilityEventInternal(int eventType) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            sendAccessibilityEventUnchecked(AccessibilityEvent.obtain(eventType));
        }
    }

    @Override // android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.sendAccessibilityEventUnchecked(this, event);
        } else {
            sendAccessibilityEventUncheckedInternal(event);
        }
    }

    public void sendAccessibilityEventUncheckedInternal(final AccessibilityEvent event) {
        boolean isWindowDisappearedEvent = true;
        boolean isWindowStateChanged = event.getEventType() == 32;
        if (!isWindowStateChanged || (32 & event.getContentChangeTypes()) == 0) {
            isWindowDisappearedEvent = false;
        }
        boolean detached = detached();
        if (!isShown() && !isWindowDisappearedEvent && !detached) {
            return;
        }
        onInitializeAccessibilityEvent(event);
        if ((event.getEventType() & POPULATING_ACCESSIBILITY_EVENT_TYPES) != 0) {
            dispatchPopulateAccessibilityEvent(event);
        }
        SendAccessibilityEventThrottle throttle = getThrottleForAccessibilityEvent(event);
        if (throttle != null) {
            throttle.post(event);
        } else if (!isWindowDisappearedEvent && detached) {
            postDelayed(new Runnable() { // from class: android.view.View$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    View.this.lambda$sendAccessibilityEventUncheckedInternal$0(event);
                }
            }, ViewConfiguration.getSendRecurringAccessibilityEventsInterval());
        } else {
            requestParentSendAccessibilityEvent(event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendAccessibilityEventUncheckedInternal$0(AccessibilityEvent event) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled() && isShown()) {
            requestParentSendAccessibilityEvent(event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestParentSendAccessibilityEvent(AccessibilityEvent event) {
        ViewParent parent = getParent();
        if (parent != null) {
            getParent().requestSendAccessibilityEvent(this, event);
        }
    }

    private SendAccessibilityEventThrottle getThrottleForAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == 4096) {
            if (this.mSendViewScrolledAccessibilityEvent == null) {
                this.mSendViewScrolledAccessibilityEvent = new SendViewScrolledAccessibilityEvent();
            }
            return this.mSendViewScrolledAccessibilityEvent;
        }
        boolean isStateContentChanged = (event.getContentChangeTypes() & 64) != 0;
        if (event.getEventType() == 2048 && isStateContentChanged) {
            if (this.mSendStateChangedAccessibilityEvent == null) {
                this.mSendStateChangedAccessibilityEvent = new SendAccessibilityEventThrottle();
            }
            return this.mSendStateChangedAccessibilityEvent;
        }
        return null;
    }

    private void clearAccessibilityThrottles() {
        cancel(this.mSendViewScrolledAccessibilityEvent);
        cancel(this.mSendStateChangedAccessibilityEvent);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            return accessibilityDelegate.dispatchPopulateAccessibilityEvent(this, event);
        }
        return dispatchPopulateAccessibilityEventInternal(event);
    }

    public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return false;
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.onPopulateAccessibilityEvent(this, event);
        } else {
            onPopulateAccessibilityEventInternal(event);
        }
    }

    public void onPopulateAccessibilityEventInternal(AccessibilityEvent event) {
        if (event.getEventType() == 32 && isAccessibilityPane()) {
            event.getText().add(getAccessibilityPaneTitle());
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.onInitializeAccessibilityEvent(this, event);
        } else {
            onInitializeAccessibilityEventInternal(event);
        }
    }

    public void onInitializeAccessibilityEventInternal(AccessibilityEvent event) {
        event.setSource(this);
        event.setClassName(getAccessibilityClassName());
        event.setPackageName(getContext().getPackageName());
        event.setEnabled(isEnabled());
        event.setContentDescription(this.mContentDescription);
        event.setScrollX(getScrollX());
        event.setScrollY(getScrollY());
        switch (event.getEventType()) {
            case 8:
                AttachInfo attachInfo = this.mAttachInfo;
                ArrayList<View> focusablesTempList = attachInfo != null ? attachInfo.mTempArrayList : new ArrayList<>();
                getRootView().addFocusables(focusablesTempList, 2, 0);
                event.setItemCount(focusablesTempList.size());
                event.setCurrentItemIndex(focusablesTempList.indexOf(this));
                if (this.mAttachInfo != null) {
                    focusablesTempList.clear();
                    return;
                }
                return;
            case 8192:
                CharSequence text = getIterableTextForAccessibility();
                if (text != null && text.length() > 0) {
                    event.setFromIndex(getAccessibilitySelectionStart());
                    event.setToIndex(getAccessibilitySelectionEnd());
                    event.setItemCount(text.length());
                    return;
                }
                return;
            default:
                return;
        }
    }

    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            return accessibilityDelegate.createAccessibilityNodeInfo(this);
        }
        return createAccessibilityNodeInfoInternal();
    }

    public AccessibilityNodeInfo createAccessibilityNodeInfoInternal() {
        AccessibilityNodeProvider provider = getAccessibilityNodeProvider();
        if (provider != null) {
            return provider.createAccessibilityNodeInfo(-1);
        }
        AccessibilityNodeInfo info = AccessibilityNodeInfo.obtain(this);
        onInitializeAccessibilityNodeInfo(info);
        return info;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.onInitializeAccessibilityNodeInfo(this, info);
        } else {
            onInitializeAccessibilityNodeInfoInternal(info);
        }
    }

    public void getBoundsOnScreen(Rect outRect) {
        getBoundsOnScreen(outRect, false);
    }

    public void getBoundsOnScreen(Rect outRect, boolean clipToParent) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return;
        }
        RectF position = attachInfo.mTmpTransformRect;
        getBoundsToScreenInternal(position, clipToParent);
        outRect.set(Math.round(position.left), Math.round(position.top), Math.round(position.right), Math.round(position.bottom));
        this.mAttachInfo.mViewRootImpl.applyViewBoundsSandboxingIfNeeded(outRect);
    }

    public void getBoundsOnScreen(RectF outRect, boolean clipToParent) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return;
        }
        RectF position = attachInfo.mTmpTransformRect;
        getBoundsToScreenInternal(position, clipToParent);
        outRect.set(position.left, position.top, position.right, position.bottom);
    }

    public void getBoundsInWindow(Rect outRect, boolean clipToParent) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return;
        }
        RectF position = attachInfo.mTmpTransformRect;
        getBoundsToWindowInternal(position, clipToParent);
        outRect.set(Math.round(position.left), Math.round(position.top), Math.round(position.right), Math.round(position.bottom));
    }

    private void getBoundsToScreenInternal(RectF position, boolean clipToParent) {
        position.set(0.0f, 0.0f, this.mRight - this.mLeft, this.mBottom - this.mTop);
        mapRectFromViewToScreenCoords(position, clipToParent);
    }

    private void getBoundsToWindowInternal(RectF position, boolean clipToParent) {
        position.set(0.0f, 0.0f, this.mRight - this.mLeft, this.mBottom - this.mTop);
        mapRectFromViewToWindowCoords(position, clipToParent);
    }

    public void mapRectFromViewToScreenCoords(RectF rect, boolean clipToParent) {
        mapRectFromViewToWindowCoords(rect, clipToParent);
        rect.offset(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
    }

    public void mapRectFromViewToWindowCoords(RectF rect, boolean clipToParent) {
        if (!hasIdentityMatrix()) {
            getMatrix().mapRect(rect);
        }
        rect.offset(this.mLeft, this.mTop);
        ViewParent parent = this.mParent;
        while (parent instanceof View) {
            View parentView = (View) parent;
            rect.offset(-parentView.mScrollX, -parentView.mScrollY);
            if (clipToParent) {
                rect.left = Math.max(rect.left, 0.0f);
                rect.top = Math.max(rect.top, 0.0f);
                rect.right = Math.min(rect.right, parentView.getWidth());
                rect.bottom = Math.min(rect.bottom, parentView.getHeight());
            }
            if (!parentView.hasIdentityMatrix()) {
                parentView.getMatrix().mapRect(rect);
            }
            rect.offset(parentView.mLeft, parentView.mTop);
            parent = parentView.mParent;
        }
        if (parent instanceof ViewRootImpl) {
            ViewRootImpl viewRootImpl = (ViewRootImpl) parent;
            rect.offset(0.0f, -viewRootImpl.mCurScrollY);
        }
    }

    public CharSequence getAccessibilityClassName() {
        return View.class.getName();
    }

    public void onProvideStructure(ViewStructure structure) {
        onProvideStructure(structure, 0, 0);
    }

    public void onProvideAutofillStructure(ViewStructure structure, int flags) {
        onProvideStructure(structure, 1, flags);
    }

    public void onProvideContentCaptureStructure(ViewStructure structure, int flags) {
        onProvideStructure(structure, 2, flags);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onProvideStructure(ViewStructure structure, int viewFor, int flags) {
        String pkg;
        String type;
        String pkg2;
        int id = this.mID;
        if (id != -1 && !isViewIdGenerated(id)) {
            try {
                Resources res = getResources();
                pkg = res.getResourceEntryName(id);
                type = res.getResourceTypeName(id);
                pkg2 = res.getResourcePackageName(id);
            } catch (Resources.NotFoundException e) {
                pkg = null;
                type = null;
                pkg2 = null;
            }
            structure.setId(id, pkg2, type, pkg);
        } else {
            structure.setId(id, null, null, null);
        }
        if (viewFor == 1 || viewFor == 2) {
            int autofillType = getAutofillType();
            if (autofillType != 0) {
                structure.setAutofillType(autofillType);
                structure.setAutofillHints(getAutofillHints());
                structure.setAutofillValue(getAutofillValue());
            }
            structure.setImportantForAutofill(getImportantForAutofill());
            structure.setReceiveContentMimeTypes(getReceiveContentMimeTypes());
        }
        int ignoredParentLeft = 0;
        int ignoredParentTop = 0;
        if (viewFor == 1 && (flags & 1) == 0) {
            View parentGroup = null;
            ViewParent viewParent = getParent();
            if (viewParent instanceof View) {
                parentGroup = (View) viewParent;
            }
            while (parentGroup != null && !parentGroup.isImportantForAutofill()) {
                ignoredParentLeft += parentGroup.mLeft;
                ignoredParentTop += parentGroup.mTop;
                ViewParent viewParent2 = parentGroup.getParent();
                if (!(viewParent2 instanceof View)) {
                    break;
                }
                parentGroup = (View) viewParent2;
            }
        }
        int i = this.mLeft;
        int i2 = this.mTop;
        structure.setDimens(ignoredParentLeft + i, ignoredParentTop + i2, this.mScrollX, this.mScrollY, this.mRight - i, this.mBottom - i2);
        if (viewFor == 0) {
            if (!hasIdentityMatrix()) {
                structure.setTransformation(getMatrix());
            }
            structure.setElevation(getZ());
        }
        structure.setVisibility(getVisibility());
        structure.setEnabled(isEnabled());
        if (isClickable()) {
            structure.setClickable(true);
        }
        if (isFocusable()) {
            structure.setFocusable(true);
        }
        if (isFocused()) {
            structure.setFocused(true);
        }
        if (isAccessibilityFocused()) {
            structure.setAccessibilityFocused(true);
        }
        if (isSelected()) {
            structure.setSelected(true);
        }
        if (isActivated()) {
            structure.setActivated(true);
        }
        if (isLongClickable()) {
            structure.setLongClickable(true);
        }
        if (this instanceof Checkable) {
            structure.setCheckable(true);
            if (((Checkable) this).isChecked()) {
                structure.setChecked(true);
            }
        }
        if (isOpaque()) {
            structure.setOpaque(true);
        }
        if (isContextClickable()) {
            structure.setContextClickable(true);
        }
        structure.setClassName(getAccessibilityClassName().toString());
        structure.setContentDescription(getContentDescription());
    }

    public void onProvideVirtualStructure(ViewStructure structure) {
        onProvideVirtualStructureCompat(structure, false);
    }

    private void onProvideVirtualStructureCompat(ViewStructure structure, boolean forAutofill) {
        AccessibilityNodeProvider provider = getAccessibilityNodeProvider();
        if (provider != null) {
            if (forAutofill && Log.isLoggable(AUTOFILL_LOG_TAG, 2)) {
                Log.m106v(AUTOFILL_LOG_TAG, "onProvideVirtualStructureCompat() for " + this);
            }
            AccessibilityNodeInfo info = createAccessibilityNodeInfo();
            structure.setChildCount(1);
            ViewStructure root = structure.newChild(0);
            populateVirtualStructure(root, provider, info, forAutofill);
            info.recycle();
        }
    }

    public void onProvideAutofillVirtualStructure(ViewStructure structure, int flags) {
        if (this.mContext.isAutofillCompatibilityEnabled()) {
            onProvideVirtualStructureCompat(structure, true);
        }
    }

    public void setOnReceiveContentListener(String[] mimeTypes, OnReceiveContentListener listener) {
        if (listener != null) {
            Preconditions.checkArgument(mimeTypes != null && mimeTypes.length > 0, "When the listener is set, MIME types must also be set");
        }
        if (mimeTypes != null) {
            Preconditions.checkArgument(Arrays.stream(mimeTypes).noneMatch(new Predicate() { // from class: android.view.View$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean startsWith;
                    startsWith = ((String) obj).startsWith("*");
                    return startsWith;
                }
            }), "A MIME type set here must not start with *: " + Arrays.toString(mimeTypes));
        }
        this.mReceiveContentMimeTypes = ArrayUtils.isEmpty(mimeTypes) ? null : mimeTypes;
        getListenerInfo().mOnReceiveContentListener = listener;
    }

    public ContentInfo performReceiveContent(ContentInfo payload) {
        OnReceiveContentListener listener = this.mListenerInfo == null ? null : getListenerInfo().mOnReceiveContentListener;
        if (listener != null) {
            ContentInfo remaining = listener.onReceiveContent(this, payload);
            if (remaining == null) {
                return null;
            }
            return onReceiveContent(remaining);
        }
        return onReceiveContent(payload);
    }

    public ContentInfo onReceiveContent(ContentInfo payload) {
        return payload;
    }

    public String[] getReceiveContentMimeTypes() {
        return this.mReceiveContentMimeTypes;
    }

    public void autofill(AutofillValue value) {
    }

    public void autofill(SparseArray<AutofillValue> values) {
        AccessibilityNodeProvider provider;
        if (!this.mContext.isAutofillCompatibilityEnabled() || (provider = getAccessibilityNodeProvider()) == null) {
            return;
        }
        int valueCount = values.size();
        for (int i = 0; i < valueCount; i++) {
            AutofillValue value = values.valueAt(i);
            if (value.isText()) {
                int virtualId = values.keyAt(i);
                CharSequence text = value.getTextValue();
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
                provider.performAction(virtualId, 2097152, arguments);
            }
        }
    }

    public final AutofillId getAutofillId() {
        if (this.mAutofillId == null) {
            this.mAutofillId = new AutofillId(getAutofillViewId());
        }
        return this.mAutofillId;
    }

    public void setAutofillId(AutofillId id) {
        if (Log.isLoggable(AUTOFILL_LOG_TAG, 2)) {
            Log.m106v(AUTOFILL_LOG_TAG, "setAutofill(): from " + this.mAutofillId + " to " + id);
        }
        if (isAttachedToWindow()) {
            throw new IllegalStateException("Cannot set autofill id when view is attached");
        }
        if (id != null && !id.isNonVirtual()) {
            throw new IllegalStateException("Cannot set autofill id assigned to virtual views");
        }
        if (id == null && (this.mPrivateFlags3 & 1073741824) == 0) {
            return;
        }
        this.mAutofillId = id;
        if (id != null) {
            this.mAutofillViewId = id.getViewId();
            this.mPrivateFlags3 = 1073741824 | this.mPrivateFlags3;
            return;
        }
        this.mAutofillViewId = -1;
        this.mPrivateFlags3 &= -1073741825;
    }

    public void resetSubtreeAutofillIds() {
        if (this.mAutofillViewId == -1) {
            return;
        }
        if (Log.isLoggable(CONTENT_CAPTURE_LOG_TAG, 2)) {
            Log.m106v(CONTENT_CAPTURE_LOG_TAG, "resetAutofillId() for " + this.mAutofillViewId);
        } else if (Log.isLoggable(AUTOFILL_LOG_TAG, 2)) {
            Log.m106v(AUTOFILL_LOG_TAG, "resetAutofillId() for " + this.mAutofillViewId);
        }
        this.mAutofillId = null;
        this.mAutofillViewId = -1;
        this.mPrivateFlags3 &= -1073741825;
    }

    public int getAutofillType() {
        return 0;
    }

    @ViewDebug.ExportedProperty
    public String[] getAutofillHints() {
        return this.mAutofillHints;
    }

    public boolean isAutofilled() {
        return (this.mPrivateFlags3 & 65536) != 0;
    }

    public boolean hideAutofillHighlight() {
        return (this.mPrivateFlags4 & 512) != 0;
    }

    public AutofillValue getAutofillValue() {
        return null;
    }

    @ViewDebug.ExportedProperty(mapping = {@ViewDebug.IntToString(from = 0, m86to = "auto"), @ViewDebug.IntToString(from = 1, m86to = MediaMetrics.Value.YES), @ViewDebug.IntToString(from = 2, m86to = MediaMetrics.Value.f269NO), @ViewDebug.IntToString(from = 4, m86to = "yesExcludeDescendants"), @ViewDebug.IntToString(from = 8, m86to = "noExcludeDescendants")})
    public int getImportantForAutofill() {
        return (this.mPrivateFlags3 & PFLAG3_IMPORTANT_FOR_AUTOFILL_MASK) >> 19;
    }

    public void setImportantForAutofill(int mode) {
        int i = this.mPrivateFlags3 & (-7864321);
        this.mPrivateFlags3 = i;
        this.mPrivateFlags3 = i | ((mode << 19) & PFLAG3_IMPORTANT_FOR_AUTOFILL_MASK);
    }

    public final boolean isImportantForAutofill() {
        for (ViewParent parent = this.mParent; parent instanceof View; parent = parent.getParent()) {
            int parentImportance = ((View) parent).getImportantForAutofill();
            if (parentImportance == 8 || parentImportance == 4) {
                if (Log.isLoggable(AUTOFILL_LOG_TAG, 2)) {
                    Log.m106v(AUTOFILL_LOG_TAG, "View (" + this + ") is not important for autofill because parent " + parent + "'s importance is " + parentImportance);
                }
                return false;
            }
        }
        int importance = getImportantForAutofill();
        if (importance == 4 || importance == 1) {
            return true;
        }
        if (importance == 8 || importance == 2) {
            if (Log.isLoggable(AUTOFILL_LOG_TAG, 2)) {
                Log.m106v(AUTOFILL_LOG_TAG, "View (" + this + ") is not important for autofill because its importance is " + importance);
            }
            return false;
        } else if (importance != 0) {
            Log.m104w(AUTOFILL_LOG_TAG, "invalid autofill importance (" + importance + " on view " + this);
            return false;
        } else {
            int id = this.mID;
            if (id != -1 && !isViewIdGenerated(id)) {
                Resources res = getResources();
                String entry = null;
                String pkg = null;
                try {
                    entry = res.getResourceEntryName(id);
                    pkg = res.getResourcePackageName(id);
                } catch (Resources.NotFoundException e) {
                }
                if (entry != null && pkg != null && pkg.equals(this.mContext.getPackageName())) {
                    return true;
                }
            }
            return getAutofillHints() != null;
        }
    }

    @ViewDebug.ExportedProperty(mapping = {@ViewDebug.IntToString(from = 0, m86to = "auto"), @ViewDebug.IntToString(from = 1, m86to = MediaMetrics.Value.YES), @ViewDebug.IntToString(from = 2, m86to = MediaMetrics.Value.f269NO), @ViewDebug.IntToString(from = 4, m86to = "yesExcludeDescendants"), @ViewDebug.IntToString(from = 8, m86to = "noExcludeDescendants")})
    public int getImportantForContentCapture() {
        return this.mPrivateFlags4 & 15;
    }

    public void setImportantForContentCapture(int mode) {
        int i = this.mPrivateFlags4 & (-16);
        this.mPrivateFlags4 = i;
        this.mPrivateFlags4 = i | (mode & 15);
    }

    public final boolean isImportantForContentCapture() {
        int i = this.mPrivateFlags4;
        if ((i & 64) != 0) {
            return (i & 128) != 0;
        }
        boolean isImportant = calculateIsImportantForContentCapture();
        int i2 = this.mPrivateFlags4 & PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
        this.mPrivateFlags4 = i2;
        if (isImportant) {
            this.mPrivateFlags4 = i2 | 128;
        }
        this.mPrivateFlags4 |= 64;
        return isImportant;
    }

    private boolean calculateIsImportantForContentCapture() {
        for (ViewParent parent = this.mParent; parent instanceof View; parent = parent.getParent()) {
            int parentImportance = ((View) parent).getImportantForContentCapture();
            if (parentImportance == 8 || parentImportance == 4) {
                if (Log.isLoggable(CONTENT_CAPTURE_LOG_TAG, 2)) {
                    Log.m106v(CONTENT_CAPTURE_LOG_TAG, "View (" + this + ") is not important for content capture because parent " + parent + "'s importance is " + parentImportance);
                }
                return false;
            }
        }
        int importance = getImportantForContentCapture();
        if (importance == 4 || importance == 1) {
            return true;
        }
        if (importance == 8 || importance == 2) {
            if (Log.isLoggable(CONTENT_CAPTURE_LOG_TAG, 2)) {
                Log.m106v(CONTENT_CAPTURE_LOG_TAG, "View (" + this + ") is not important for content capture because its importance is " + importance);
            }
            return false;
        } else if (importance != 0) {
            Log.m104w(CONTENT_CAPTURE_LOG_TAG, "invalid content capture importance (" + importance + " on view " + this);
            return false;
        } else {
            if (this instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) this;
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    if (child.isImportantForContentCapture()) {
                        return true;
                    }
                }
            }
            return getAutofillHints() != null;
        }
    }

    private void notifyAppearedOrDisappearedForContentCaptureIfNeeded(boolean appeared) {
        AttachInfo ai = this.mAttachInfo;
        if ((ai == null || ai.mReadyForContentCaptureUpdates) && this.mContext.getContentCaptureOptions() != null) {
            if (appeared) {
                boolean isRecycledWithoutRelayout = getNotifiedContentCaptureDisappeared() && getVisibility() == 0 && !isLayoutRequested();
                if (getVisibility() == 0 && !getNotifiedContentCaptureAppeared()) {
                    if (!isLaidOut() && !isRecycledWithoutRelayout) {
                        return;
                    }
                } else {
                    return;
                }
            } else {
                boolean isRecycledWithoutRelayout2 = getNotifiedContentCaptureAppeared();
                if (!isRecycledWithoutRelayout2 || getNotifiedContentCaptureDisappeared()) {
                    return;
                }
            }
            ContentCaptureSession session = getContentCaptureSession();
            if (session != null && isImportantForContentCapture()) {
                if (appeared) {
                    setNotifiedContentCaptureAppeared();
                    if (ai != null) {
                        makeParentImportantAndNotifyAppearedEventIfNeed();
                        ai.delayNotifyContentCaptureEvent(session, this, appeared);
                        return;
                    }
                    return;
                }
                int i = this.mPrivateFlags4 | 32;
                this.mPrivateFlags4 = i;
                this.mPrivateFlags4 = i & (-17);
                if (ai != null) {
                    ai.delayNotifyContentCaptureEvent(session, this, appeared);
                }
                if (!isTemporarilyDetached()) {
                    clearTranslationState();
                }
            }
        }
    }

    private void makeParentImportantAndNotifyAppearedEventIfNeed() {
        ViewParent parent = getParent();
        if (parent instanceof View) {
            View p = (View) parent;
            if (p.getNotifiedContentCaptureAppeared()) {
                return;
            }
            p.mPrivateFlags4 |= 192;
            p.notifyAppearedOrDisappearedForContentCaptureIfNeeded(true);
        }
    }

    private void setNotifiedContentCaptureAppeared() {
        int i = this.mPrivateFlags4 | 16;
        this.mPrivateFlags4 = i;
        this.mPrivateFlags4 = i & (-33);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getNotifiedContentCaptureAppeared() {
        return (this.mPrivateFlags4 & 16) != 0;
    }

    private boolean getNotifiedContentCaptureDisappeared() {
        return (this.mPrivateFlags4 & 32) != 0;
    }

    public void setContentCaptureSession(ContentCaptureSession contentCaptureSession) {
        this.mContentCaptureSession = contentCaptureSession;
    }

    public final ContentCaptureSession getContentCaptureSession() {
        if (this.mContentCaptureSessionCached) {
            return this.mContentCaptureSession;
        }
        ContentCaptureSession andCacheContentCaptureSession = getAndCacheContentCaptureSession();
        this.mContentCaptureSession = andCacheContentCaptureSession;
        this.mContentCaptureSessionCached = true;
        return andCacheContentCaptureSession;
    }

    private ContentCaptureSession getAndCacheContentCaptureSession() {
        ContentCaptureSession contentCaptureSession = this.mContentCaptureSession;
        if (contentCaptureSession != null) {
            return contentCaptureSession;
        }
        ContentCaptureSession session = null;
        ViewParent viewParent = this.mParent;
        if (viewParent instanceof View) {
            session = ((View) viewParent).getContentCaptureSession();
        }
        if (session == null) {
            ContentCaptureManager ccm = (ContentCaptureManager) this.mContext.getSystemService(ContentCaptureManager.class);
            if (ccm == null) {
                return null;
            }
            return ccm.getMainContentCaptureSession();
        }
        return session;
    }

    private AutofillManager getAutofillManager() {
        return (AutofillManager) this.mContext.getSystemService(AutofillManager.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isActivityDeniedForAutofillForUnimportantView() {
        AutofillManager afm = getAutofillManager();
        if (afm == null) {
            return true;
        }
        return afm.isActivityDeniedForAutofillForUnimportantView();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isMatchingAutofillableHeuristics() {
        AutofillManager afm = getAutofillManager();
        if (afm == null) {
            return false;
        }
        return afm.isMatchingAutofillableHeuristics(this);
    }

    private boolean isAutofillable() {
        AutofillManager afm;
        if (getAutofillType() == 0) {
            return false;
        }
        if (AutofillFeatureFlags.shouldIgnoreCredentialViews() && isCredential()) {
            return false;
        }
        if (!isImportantForAutofill()) {
            if (isMatchingAutofillableHeuristics() && !isActivityDeniedForAutofillForUnimportantView()) {
                return getAutofillViewId() > 1073741823;
            }
            AutofillOptions options = this.mContext.getAutofillOptions();
            if (options == null || !options.isAugmentedAutofillEnabled(this.mContext) || (afm = getAutofillManager()) == null) {
                return false;
            }
            afm.notifyViewEnteredForAugmentedAutofill(this);
        }
        return getAutofillViewId() > 1073741823;
    }

    public boolean canNotifyAutofillEnterExitEvent() {
        return isAutofillable() && isAttachedToWindow();
    }

    private void populateVirtualStructure(ViewStructure structure, AccessibilityNodeProvider provider, AccessibilityNodeInfo info, boolean forAutofill) {
        structure.setId(AccessibilityNodeInfo.getVirtualDescendantId(info.getSourceNodeId()), null, null, info.getViewIdResourceName());
        Rect rect = structure.getTempRect();
        info.getBoundsInParent(rect);
        structure.setDimens(rect.left, rect.top, 0, 0, rect.width(), rect.height());
        structure.setVisibility(0);
        structure.setEnabled(info.isEnabled());
        if (info.isClickable()) {
            structure.setClickable(true);
        }
        if (info.isFocusable()) {
            structure.setFocusable(true);
        }
        if (info.isFocused()) {
            structure.setFocused(true);
        }
        if (info.isAccessibilityFocused()) {
            structure.setAccessibilityFocused(true);
        }
        if (info.isSelected()) {
            structure.setSelected(true);
        }
        if (info.isLongClickable()) {
            structure.setLongClickable(true);
        }
        if (info.isCheckable()) {
            structure.setCheckable(true);
            if (info.isChecked()) {
                structure.setChecked(true);
            }
        }
        if (info.isContextClickable()) {
            structure.setContextClickable(true);
        }
        if (forAutofill) {
            structure.setAutofillId(new AutofillId(getAutofillId(), AccessibilityNodeInfo.getVirtualDescendantId(info.getSourceNodeId())));
        }
        CharSequence cname = info.getClassName();
        structure.setClassName(cname != null ? cname.toString() : null);
        structure.setContentDescription(info.getContentDescription());
        if (forAutofill) {
            int maxTextLength = info.getMaxTextLength();
            if (maxTextLength != -1) {
                structure.setMaxTextLength(maxTextLength);
            }
            structure.setHint(info.getHintText());
        }
        CharSequence text = info.getText();
        boolean hasText = (text == null && info.getError() == null) ? false : true;
        if (hasText) {
            structure.setText(text, info.getTextSelectionStart(), info.getTextSelectionEnd());
        }
        if (forAutofill) {
            if (info.isEditable()) {
                structure.setDataIsSensitive(true);
                if (hasText) {
                    structure.setAutofillType(1);
                    structure.setAutofillValue(AutofillValue.forText(text));
                }
                int inputType = info.getInputType();
                if (inputType == 0 && info.isPassword()) {
                    inputType = 129;
                }
                structure.setInputType(inputType);
            } else {
                structure.setDataIsSensitive(false);
            }
        }
        int NCHILDREN = info.getChildCount();
        if (NCHILDREN > 0) {
            structure.setChildCount(NCHILDREN);
            for (int i = 0; i < NCHILDREN; i++) {
                if (AccessibilityNodeInfo.getVirtualDescendantId(info.getChildNodeIds().get(i)) == -1) {
                    Log.m110e(VIEW_LOG_TAG, "Virtual view pointing to its host. Ignoring");
                } else {
                    AccessibilityNodeInfo cinfo = provider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(info.getChildId(i)));
                    if (cinfo != null) {
                        ViewStructure child = structure.newChild(i);
                        populateVirtualStructure(child, provider, cinfo, forAutofill);
                        cinfo.recycle();
                    }
                }
            }
        }
    }

    public void dispatchProvideStructure(ViewStructure structure) {
        dispatchProvideStructure(structure, 0, 0);
    }

    public void dispatchProvideAutofillStructure(ViewStructure structure, int flags) {
        dispatchProvideStructure(structure, 1, flags);
    }

    private void dispatchProvideStructure(ViewStructure structure, int viewFor, int flags) {
        if (viewFor == 1) {
            structure.setAutofillId(getAutofillId());
            onProvideAutofillStructure(structure, flags);
            onProvideAutofillVirtualStructure(structure, flags);
        } else if (!isAssistBlocked()) {
            onProvideStructure(structure);
            onProvideVirtualStructure(structure);
        } else {
            structure.setClassName(getAccessibilityClassName().toString());
            structure.setAssistBlocked(true);
        }
    }

    public void dispatchInitialProvideContentCaptureStructure() {
        AttachInfo ai = this.mAttachInfo;
        if (ai == null) {
            Log.m104w(CONTENT_CAPTURE_LOG_TAG, "dispatchProvideContentCaptureStructure(): no AttachInfo for " + this);
            return;
        }
        ContentCaptureManager ccm = ai.mContentCaptureManager;
        if (ccm == null) {
            Log.m104w(CONTENT_CAPTURE_LOG_TAG, "dispatchProvideContentCaptureStructure(): no ContentCaptureManager for " + this);
            return;
        }
        ai.mReadyForContentCaptureUpdates = true;
        if (!isImportantForContentCapture()) {
            if (Log.isLoggable(CONTENT_CAPTURE_LOG_TAG, 3)) {
                Log.m112d(CONTENT_CAPTURE_LOG_TAG, "dispatchProvideContentCaptureStructure(): decorView is not important");
                return;
            }
            return;
        }
        ai.mContentCaptureManager = ccm;
        ContentCaptureSession session = getContentCaptureSession();
        if (session == null) {
            if (Log.isLoggable(CONTENT_CAPTURE_LOG_TAG, 3)) {
                Log.m112d(CONTENT_CAPTURE_LOG_TAG, "dispatchProvideContentCaptureStructure(): no session for " + this);
                return;
            }
            return;
        }
        session.internalNotifyViewTreeEvent(true);
        try {
            dispatchProvideContentCaptureStructure();
        } finally {
            session.internalNotifyViewTreeEvent(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchProvideContentCaptureStructure() {
        ContentCaptureSession session = getContentCaptureSession();
        if (session != null) {
            ViewStructure structure = session.newViewStructure(this);
            onProvideContentCaptureStructure(structure, 0);
            setNotifiedContentCaptureAppeared();
            session.notifyViewAppeared(structure);
        }
    }

    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction;
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return;
        }
        Rect bounds = attachInfo.mTmpInvalRect;
        getDrawingRect(bounds);
        info.setBoundsInParent(bounds);
        getBoundsOnScreen(bounds, true);
        info.setBoundsInScreen(bounds);
        getBoundsInWindow(bounds, true);
        info.setBoundsInWindow(bounds);
        ViewParent parent = getParentForAccessibility();
        if (parent instanceof View) {
            info.setParent((View) parent);
        }
        if (this.mID != -1) {
            View rootView = getRootView();
            if (rootView == null) {
                rootView = this;
            }
            View label = rootView.findLabelForView(this, this.mID);
            if (label != null) {
                info.setLabeledBy(label);
            }
            if ((this.mAttachInfo.mAccessibilityFetchFlags & 256) != 0 && Resources.resourceHasPackage(this.mID)) {
                try {
                    String viewId = getResources().getResourceName(this.mID);
                    info.setViewIdResourceName(viewId);
                } catch (Resources.NotFoundException e) {
                }
            }
        }
        if (this.mLabelForId != -1) {
            View rootView2 = getRootView();
            if (rootView2 == null) {
                rootView2 = this;
            }
            View labeled = rootView2.findViewInsideOutShouldExist(this, this.mLabelForId);
            if (labeled != null) {
                info.setLabelFor(labeled);
            }
        }
        if (this.mAccessibilityTraversalBeforeId != -1) {
            View rootView3 = getRootView();
            if (rootView3 == null) {
                rootView3 = this;
            }
            View next = rootView3.findViewInsideOutShouldExist(this, this.mAccessibilityTraversalBeforeId);
            if (next != null && next.includeForAccessibility()) {
                info.setTraversalBefore(next);
            }
        }
        if (this.mAccessibilityTraversalAfterId != -1) {
            View rootView4 = getRootView();
            if (rootView4 == null) {
                rootView4 = this;
            }
            View next2 = rootView4.findViewInsideOutShouldExist(this, this.mAccessibilityTraversalAfterId);
            if (next2 != null && next2.includeForAccessibility()) {
                info.setTraversalAfter(next2);
            }
        }
        info.setVisibleToUser(isVisibleToUser());
        info.setImportantForAccessibility(isImportantForAccessibility());
        info.setAccessibilityDataSensitive(isAccessibilityDataSensitive());
        info.setPackageName(this.mContext.getPackageName());
        info.setClassName(getAccessibilityClassName());
        info.setStateDescription(getStateDescription());
        info.setContentDescription(getContentDescription());
        info.setEnabled(isEnabled());
        info.setClickable(isClickable());
        info.setFocusable(isFocusable());
        info.setScreenReaderFocusable(isScreenReaderFocusable());
        info.setFocused(isFocused());
        info.setAccessibilityFocused(isAccessibilityFocused());
        info.setSelected(isSelected());
        info.setLongClickable(isLongClickable());
        info.setContextClickable(isContextClickable());
        info.setLiveRegion(getAccessibilityLiveRegion());
        TooltipInfo tooltipInfo = this.mTooltipInfo;
        if (tooltipInfo != null && tooltipInfo.mTooltipText != null) {
            info.setTooltipText(this.mTooltipInfo.mTooltipText);
            if (this.mTooltipInfo.mTooltipPopup == null) {
                accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_TOOLTIP;
            } else {
                accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_HIDE_TOOLTIP;
            }
            info.addAction(accessibilityAction);
        }
        info.addAction(4);
        info.addAction(8);
        if (isFocusable()) {
            if (isFocused()) {
                info.addAction(2);
            } else {
                info.addAction(1);
            }
        }
        if (!isAccessibilityFocused()) {
            info.addAction(64);
        } else {
            info.addAction(128);
        }
        if (isClickable() && isEnabled()) {
            info.addAction(16);
        }
        if (isLongClickable() && isEnabled()) {
            info.addAction(32);
        }
        if (isContextClickable() && isEnabled()) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CONTEXT_CLICK);
        }
        CharSequence text = getIterableTextForAccessibility();
        if (text != null && text.length() > 0) {
            info.setTextSelection(getAccessibilitySelectionStart(), getAccessibilitySelectionEnd());
            info.addAction(131072);
            info.addAction(256);
            info.addAction(512);
            info.setMovementGranularities(11);
        }
        info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_ON_SCREEN);
        populateAccessibilityNodeInfoDrawingOrderInParent(info);
        info.setPaneTitle(this.mAccessibilityPaneTitle);
        info.setHeading(isAccessibilityHeading());
        TouchDelegate touchDelegate = this.mTouchDelegate;
        if (touchDelegate != null) {
            info.setTouchDelegateInfo(touchDelegate.getTouchDelegateInfo());
        }
        if (startedSystemDragForAccessibility()) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_DRAG_CANCEL);
        }
        if (canAcceptAccessibilityDrop()) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_DRAG_DROP);
        }
    }

    public void addExtraDataToAccessibilityNodeInfo(AccessibilityNodeInfo info, String extraDataKey, Bundle arguments) {
    }

    private void populateAccessibilityNodeInfoDrawingOrderInParent(AccessibilityNodeInfo info) {
        if ((this.mPrivateFlags & 16) == 0) {
            info.setDrawingOrder(0);
            return;
        }
        int drawingOrderInParent = 1;
        View viewAtDrawingLevel = this;
        ViewParent parent = getParentForAccessibility();
        while (true) {
            if (viewAtDrawingLevel == parent) {
                break;
            }
            ViewParent currentParent = viewAtDrawingLevel.getParent();
            if (!(currentParent instanceof ViewGroup)) {
                drawingOrderInParent = 0;
                break;
            }
            ViewGroup parentGroup = (ViewGroup) currentParent;
            int childCount = parentGroup.getChildCount();
            if (childCount > 1) {
                List<View> preorderedList = parentGroup.buildOrderedChildList();
                if (preorderedList != null) {
                    int childDrawIndex = preorderedList.indexOf(viewAtDrawingLevel);
                    for (int i = 0; i < childDrawIndex; i++) {
                        drawingOrderInParent += numViewsForAccessibility(preorderedList.get(i));
                    }
                    preorderedList.clear();
                } else {
                    int childIndex = parentGroup.indexOfChild(viewAtDrawingLevel);
                    boolean customOrder = parentGroup.isChildrenDrawingOrderEnabled();
                    int childDrawIndex2 = (childIndex < 0 || !customOrder) ? childIndex : parentGroup.getChildDrawingOrder(childCount, childIndex);
                    int numChildrenToIterate = customOrder ? childCount : childDrawIndex2;
                    if (childDrawIndex2 != 0) {
                        for (int i2 = 0; i2 < numChildrenToIterate; i2++) {
                            int otherDrawIndex = customOrder ? parentGroup.getChildDrawingOrder(childCount, i2) : i2;
                            if (otherDrawIndex < childDrawIndex2) {
                                drawingOrderInParent += numViewsForAccessibility(parentGroup.getChildAt(i2));
                            }
                        }
                    }
                }
            }
            viewAtDrawingLevel = (View) currentParent;
        }
        info.setDrawingOrder(drawingOrderInParent);
    }

    private static int numViewsForAccessibility(View view) {
        if (view != null) {
            if (view.includeForAccessibility()) {
                return 1;
            }
            if (view instanceof ViewGroup) {
                return ((ViewGroup) view).getNumChildrenForAccessibility();
            }
            return 0;
        }
        return 0;
    }

    private View findLabelForView(View view, int labeledId) {
        if (this.mMatchLabelForPredicate == null) {
            this.mMatchLabelForPredicate = new MatchLabelForPredicate();
        }
        this.mMatchLabelForPredicate.mLabeledId = labeledId;
        return findViewByPredicateInsideOut(view, this.mMatchLabelForPredicate);
    }

    public boolean isVisibleToUserForAutofill(int virtualId) {
        if (this.mContext.isAutofillCompatibilityEnabled()) {
            AccessibilityNodeProvider provider = getAccessibilityNodeProvider();
            if (provider != null) {
                AccessibilityNodeInfo node = provider.createAccessibilityNodeInfo(virtualId);
                if (node != null) {
                    return node.isVisibleToUser();
                }
                return false;
            }
            Log.m104w(VIEW_LOG_TAG, "isVisibleToUserForAutofill(" + virtualId + "): no provider");
            return false;
        }
        return true;
    }

    public boolean isVisibleToUser() {
        return isVisibleToUser(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public boolean isVisibleToUser(Rect boundInView) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || attachInfo.mWindowVisibility != 0) {
            return false;
        }
        Object current = this;
        while (current instanceof View) {
            View view = (View) current;
            if (view.getAlpha() <= 0.0f || view.getTransitionAlpha() <= 0.0f || view.getVisibility() != 0) {
                return false;
            }
            Object current2 = view.mParent;
            current = current2;
        }
        Rect visibleRect = this.mAttachInfo.mTmpInvalRect;
        Point offset = this.mAttachInfo.mPoint;
        if (getGlobalVisibleRect(visibleRect, offset)) {
            if (boundInView != null) {
                visibleRect.offset(-offset.f76x, -offset.f77y);
                return boundInView.intersect(visibleRect);
            }
            return true;
        }
        return false;
    }

    public AccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public void setAccessibilityDelegate(AccessibilityDelegate delegate) {
        this.mAccessibilityDelegate = delegate;
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            return accessibilityDelegate.getAccessibilityNodeProvider(this);
        }
        return null;
    }

    public int getAccessibilityViewId() {
        if (this.mAccessibilityViewId == -1) {
            int i = sNextAccessibilityViewId;
            sNextAccessibilityViewId = i + 1;
            this.mAccessibilityViewId = i;
        }
        return this.mAccessibilityViewId;
    }

    public int getAutofillViewId() {
        if (this.mAutofillViewId == -1) {
            this.mAutofillViewId = this.mContext.getNextAutofillId();
        }
        return this.mAutofillViewId;
    }

    public int getAccessibilityWindowId() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mAccessibilityWindowId;
        }
        return -1;
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE)
    public final CharSequence getStateDescription() {
        return this.mStateDescription;
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE)
    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    @RemotableViewMethod
    public void setStateDescription(CharSequence stateDescription) {
        CharSequence charSequence = this.mStateDescription;
        if (charSequence == null) {
            if (stateDescription == null) {
                return;
            }
        } else if (charSequence.equals(stateDescription)) {
            return;
        }
        this.mStateDescription = stateDescription;
        if (!TextUtils.isEmpty(stateDescription) && getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setEventType(2048);
            event.setContentChangeTypes(64);
            sendAccessibilityEventUnchecked(event);
        }
    }

    @RemotableViewMethod
    public void setContentDescription(CharSequence contentDescription) {
        CharSequence charSequence = this.mContentDescription;
        if (charSequence == null) {
            if (contentDescription == null) {
                return;
            }
        } else if (charSequence.equals(contentDescription)) {
            return;
        }
        this.mContentDescription = contentDescription;
        boolean nonEmptyDesc = contentDescription != null && contentDescription.length() > 0;
        if (nonEmptyDesc && getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
            notifySubtreeAccessibilityStateChangedIfNeeded();
            return;
        }
        notifyViewAccessibilityStateChangedIfNeeded(4);
    }

    @RemotableViewMethod
    public void setAccessibilityTraversalBefore(int beforeId) {
        if (this.mAccessibilityTraversalBeforeId == beforeId) {
            return;
        }
        this.mAccessibilityTraversalBeforeId = beforeId;
        notifyViewAccessibilityStateChangedIfNeeded(0);
    }

    public int getAccessibilityTraversalBefore() {
        return this.mAccessibilityTraversalBeforeId;
    }

    @RemotableViewMethod
    public void setAccessibilityTraversalAfter(int afterId) {
        if (this.mAccessibilityTraversalAfterId == afterId) {
            return;
        }
        this.mAccessibilityTraversalAfterId = afterId;
        notifyViewAccessibilityStateChangedIfNeeded(0);
    }

    public int getAccessibilityTraversalAfter() {
        return this.mAccessibilityTraversalAfterId;
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE)
    public int getLabelFor() {
        return this.mLabelForId;
    }

    @RemotableViewMethod
    public void setLabelFor(int id) {
        if (this.mLabelForId == id) {
            return;
        }
        this.mLabelForId = id;
        if (id != -1 && this.mID == -1) {
            this.mID = generateViewId();
        }
        notifyViewAccessibilityStateChangedIfNeeded(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFocusLost() {
        resetPressedState();
    }

    private void resetPressedState() {
        if ((this.mViewFlags & 32) != 32 && isPressed()) {
            setPressed(false);
            if (!this.mHasPerformedLongPress) {
                removeLongPressCallback();
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public boolean isFocused() {
        return (this.mPrivateFlags & 2) != 0;
    }

    public View findFocus() {
        if ((this.mPrivateFlags & 2) != 0) {
            return this;
        }
        return null;
    }

    public boolean isScrollContainer() {
        return (this.mPrivateFlags & 1048576) != 0;
    }

    public void setScrollContainer(boolean isScrollContainer) {
        if (!isScrollContainer) {
            if ((1048576 & this.mPrivateFlags) != 0) {
                this.mAttachInfo.mScrollContainers.remove(this);
            }
            this.mPrivateFlags &= -1572865;
            return;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && (this.mPrivateFlags & 1048576) == 0) {
            attachInfo.mScrollContainers.add(this);
            this.mPrivateFlags = 1048576 | this.mPrivateFlags;
        }
        this.mPrivateFlags |= 524288;
    }

    @Deprecated
    public int getDrawingCacheQuality() {
        return this.mViewFlags & 1572864;
    }

    @Deprecated
    public void setDrawingCacheQuality(int quality) {
        setFlags(quality, 1572864);
    }

    public boolean getKeepScreenOn() {
        return (this.mViewFlags & 67108864) != 0;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        setFlags(keepScreenOn ? 67108864 : 0, 67108864);
    }

    public int getNextFocusLeftId() {
        return this.mNextFocusLeftId;
    }

    public void setNextFocusLeftId(int nextFocusLeftId) {
        this.mNextFocusLeftId = nextFocusLeftId;
    }

    public int getNextFocusRightId() {
        return this.mNextFocusRightId;
    }

    public void setNextFocusRightId(int nextFocusRightId) {
        this.mNextFocusRightId = nextFocusRightId;
    }

    public int getNextFocusUpId() {
        return this.mNextFocusUpId;
    }

    public void setNextFocusUpId(int nextFocusUpId) {
        this.mNextFocusUpId = nextFocusUpId;
    }

    public int getNextFocusDownId() {
        return this.mNextFocusDownId;
    }

    public void setNextFocusDownId(int nextFocusDownId) {
        this.mNextFocusDownId = nextFocusDownId;
    }

    public int getNextFocusForwardId() {
        return this.mNextFocusForwardId;
    }

    public void setNextFocusForwardId(int nextFocusForwardId) {
        this.mNextFocusForwardId = nextFocusForwardId;
    }

    public int getNextClusterForwardId() {
        return this.mNextClusterForwardId;
    }

    public void setNextClusterForwardId(int nextClusterForwardId) {
        this.mNextClusterForwardId = nextClusterForwardId;
    }

    public boolean isShown() {
        ViewParent parent;
        View current = this;
        while ((current.mViewFlags & 12) == 0 && (parent = current.mParent) != null) {
            if (!(parent instanceof View)) {
                return true;
            }
            current = (View) parent;
            if (current == null) {
                return false;
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x0009  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean detached() {
        View current = this;
        while ((current.mPrivateFlags4 & 8192) == 0) {
            ViewParent parent = current.mParent;
            if (parent == null || !(parent instanceof View) || (current = (View) parent) == null) {
                return false;
            }
            while ((current.mPrivateFlags4 & 8192) == 0) {
            }
        }
        return true;
    }

    @Deprecated
    protected boolean fitSystemWindows(Rect insets) {
        int i = this.mPrivateFlags3;
        if ((i & 32) == 0) {
            if (insets == null) {
                return false;
            }
            try {
                this.mPrivateFlags3 = i | 64;
                return dispatchApplyWindowInsets(new WindowInsets(insets)).isConsumed();
            } finally {
                this.mPrivateFlags3 &= -65;
            }
        }
        return fitSystemWindowsInt(insets);
    }

    private boolean fitSystemWindowsInt(Rect insets) {
        if ((this.mViewFlags & 2) == 2) {
            Rect localInsets = sThreadLocal.get();
            boolean res = computeFitSystemWindows(insets, localInsets);
            applyInsets(localInsets);
            return res;
        }
        return false;
    }

    private void applyInsets(Rect insets) {
        this.mUserPaddingStart = Integer.MIN_VALUE;
        this.mUserPaddingEnd = Integer.MIN_VALUE;
        this.mUserPaddingLeftInitial = insets.left;
        this.mUserPaddingRightInitial = insets.right;
        internalSetPadding(insets.left, insets.top, insets.right, insets.bottom);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if ((this.mPrivateFlags4 & 256) != 0 && (this.mViewFlags & 2) != 0) {
            return onApplyFrameworkOptionalFitSystemWindows(insets);
        }
        if ((this.mPrivateFlags3 & 64) == 0) {
            if (fitSystemWindows(insets.getSystemWindowInsetsAsRect())) {
                return insets.consumeSystemWindowInsets();
            }
        } else if (fitSystemWindowsInt(insets.getSystemWindowInsetsAsRect())) {
            return insets.consumeSystemWindowInsets();
        }
        return insets;
    }

    private WindowInsets onApplyFrameworkOptionalFitSystemWindows(WindowInsets insets) {
        Rect localInsets = sThreadLocal.get();
        WindowInsets result = computeSystemWindowInsets(insets, localInsets);
        applyInsets(localInsets);
        return result;
    }

    public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {
        getListenerInfo().mOnApplyWindowInsetsListener = listener;
    }

    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        try {
            this.mPrivateFlags3 |= 32;
            ListenerInfo listenerInfo = this.mListenerInfo;
            if (listenerInfo != null && listenerInfo.mOnApplyWindowInsetsListener != null) {
                return this.mListenerInfo.mOnApplyWindowInsetsListener.onApplyWindowInsets(this, insets);
            }
            return onApplyWindowInsets(insets);
        } finally {
            this.mPrivateFlags3 &= -33;
        }
    }

    public void setWindowInsetsAnimationCallback(WindowInsetsAnimation.Callback callback) {
        getListenerInfo().mWindowInsetsAnimationCallback = callback;
    }

    public boolean hasWindowInsetsAnimationCallback() {
        return getListenerInfo().mWindowInsetsAnimationCallback != null;
    }

    public void dispatchWindowInsetsAnimationPrepare(WindowInsetsAnimation animation) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null && listenerInfo.mWindowInsetsAnimationCallback != null) {
            this.mListenerInfo.mWindowInsetsAnimationCallback.onPrepare(animation);
        }
    }

    public WindowInsetsAnimation.Bounds dispatchWindowInsetsAnimationStart(WindowInsetsAnimation animation, WindowInsetsAnimation.Bounds bounds) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null && listenerInfo.mWindowInsetsAnimationCallback != null) {
            return this.mListenerInfo.mWindowInsetsAnimationCallback.onStart(animation, bounds);
        }
        return bounds;
    }

    public WindowInsets dispatchWindowInsetsAnimationProgress(WindowInsets insets, List<WindowInsetsAnimation> runningAnimations) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null && listenerInfo.mWindowInsetsAnimationCallback != null) {
            return this.mListenerInfo.mWindowInsetsAnimationCallback.onProgress(insets, runningAnimations);
        }
        return insets;
    }

    public void dispatchWindowInsetsAnimationEnd(WindowInsetsAnimation animation) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null && listenerInfo.mWindowInsetsAnimationCallback != null) {
            this.mListenerInfo.mWindowInsetsAnimationCallback.onEnd(animation);
        }
    }

    public void setSystemGestureExclusionRects(List<Rect> rects) {
        if (rects.isEmpty() && this.mListenerInfo == null) {
            return;
        }
        ListenerInfo info = getListenerInfo();
        if (info.mSystemGestureExclusionRects != null) {
            info.mSystemGestureExclusionRects.clear();
            info.mSystemGestureExclusionRects.addAll(rects);
        } else {
            info.mSystemGestureExclusionRects = new ArrayList(rects);
        }
        updatePositionUpdateListener();
        postUpdate(new View$$ExternalSyntheticLambda10(this));
    }

    private void updatePositionUpdateListener() {
        final ListenerInfo info = getListenerInfo();
        if (getSystemGestureExclusionRects().isEmpty() && collectPreferKeepClearRects().isEmpty() && collectUnrestrictedPreferKeepClearRects().isEmpty() && (info.mHandwritingArea == null || !isAutoHandwritingEnabled())) {
            if (info.mPositionUpdateListener != null) {
                this.mRenderNode.removePositionUpdateListener(info.mPositionUpdateListener);
                info.mPositionUpdateListener = null;
                info.mPositionChangedUpdate = null;
            }
        } else if (info.mPositionUpdateListener == null) {
            info.mPositionChangedUpdate = new Runnable() { // from class: android.view.View$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    View.this.lambda$updatePositionUpdateListener$2();
                }
            };
            info.mPositionUpdateListener = new RenderNode.PositionUpdateListener() { // from class: android.view.View.1
                @Override // android.graphics.RenderNode.PositionUpdateListener
                public void positionChanged(long n, int l, int t, int r, int b) {
                    View.this.postUpdate(info.mPositionChangedUpdate);
                }

                @Override // android.graphics.RenderNode.PositionUpdateListener
                public void positionLost(long frameNumber) {
                    View.this.postUpdate(info.mPositionChangedUpdate);
                }
            };
            this.mRenderNode.addPositionUpdateListener(info.mPositionUpdateListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePositionUpdateListener$2() {
        updateSystemGestureExclusionRects();
        updateKeepClearRects();
        updateHandwritingArea();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postUpdate(Runnable r) {
        Handler h = getHandler();
        if (h != null) {
            h.postAtFrontOfQueue(r);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSystemGestureExclusionRects() {
        AttachInfo ai = this.mAttachInfo;
        if (ai != null) {
            ai.mViewRootImpl.updateSystemGestureExclusionRectsForView(this);
        }
    }

    public List<Rect> getSystemGestureExclusionRects() {
        List<Rect> list;
        ListenerInfo info = this.mListenerInfo;
        if (info != null && (list = info.mSystemGestureExclusionRects) != null) {
            return list;
        }
        return Collections.emptyList();
    }

    public final void setPreferKeepClear(boolean preferKeepClear) {
        getListenerInfo().mPreferKeepClear = preferKeepClear;
        updatePositionUpdateListener();
        postUpdate(new View$$ExternalSyntheticLambda1(this));
    }

    public final boolean isPreferKeepClear() {
        ListenerInfo listenerInfo = this.mListenerInfo;
        return listenerInfo != null && listenerInfo.mPreferKeepClear;
    }

    public final void setPreferKeepClearRects(List<Rect> rects) {
        ListenerInfo info = getListenerInfo();
        if (info.mKeepClearRects != null) {
            info.mKeepClearRects.clear();
            info.mKeepClearRects.addAll(rects);
        } else {
            info.mKeepClearRects = new ArrayList(rects);
        }
        updatePositionUpdateListener();
        postUpdate(new View$$ExternalSyntheticLambda1(this));
    }

    public final List<Rect> getPreferKeepClearRects() {
        ListenerInfo info = this.mListenerInfo;
        if (info != null && info.mKeepClearRects != null) {
            return new ArrayList(info.mKeepClearRects);
        }
        return Collections.emptyList();
    }

    @SystemApi
    public final void setUnrestrictedPreferKeepClearRects(List<Rect> rects) {
        ListenerInfo info = getListenerInfo();
        if (info.mUnrestrictedKeepClearRects != null) {
            info.mUnrestrictedKeepClearRects.clear();
            info.mUnrestrictedKeepClearRects.addAll(rects);
        } else {
            info.mUnrestrictedKeepClearRects = new ArrayList(rects);
        }
        updatePositionUpdateListener();
        postUpdate(new View$$ExternalSyntheticLambda1(this));
    }

    @SystemApi
    public final List<Rect> getUnrestrictedPreferKeepClearRects() {
        ListenerInfo info = this.mListenerInfo;
        if (info != null && info.mUnrestrictedKeepClearRects != null) {
            return new ArrayList(info.mUnrestrictedKeepClearRects);
        }
        return Collections.emptyList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateKeepClearRects() {
        AttachInfo ai = this.mAttachInfo;
        if (ai != null) {
            ai.mViewRootImpl.updateKeepClearRectsForView(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<Rect> collectPreferKeepClearRects() {
        ListenerInfo info = this.mListenerInfo;
        boolean hasCustomKeepClearRects = true;
        boolean keepClearForFocus = isFocused() && ViewConfiguration.get(this.mContext).isPreferKeepClearForFocusEnabled();
        boolean keepBoundsClear = (info != null && info.mPreferKeepClear) || keepClearForFocus;
        if (info == null || info.mKeepClearRects == null) {
            hasCustomKeepClearRects = false;
        }
        if (!keepBoundsClear && !hasCustomKeepClearRects) {
            return Collections.emptyList();
        }
        if (keepBoundsClear && !hasCustomKeepClearRects) {
            return Collections.singletonList(new Rect(0, 0, getWidth(), getHeight()));
        }
        List<Rect> list = new ArrayList<>();
        if (keepBoundsClear) {
            list.add(new Rect(0, 0, getWidth(), getHeight()));
        }
        if (hasCustomKeepClearRects) {
            list.addAll(info.mKeepClearRects);
        }
        return list;
    }

    private void updatePreferKeepClearForFocus() {
        if (ViewConfiguration.get(this.mContext).isPreferKeepClearForFocusEnabled()) {
            updatePositionUpdateListener();
            post(new View$$ExternalSyntheticLambda1(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<Rect> collectUnrestrictedPreferKeepClearRects() {
        ListenerInfo info = this.mListenerInfo;
        if (info != null && info.mUnrestrictedKeepClearRects != null) {
            return info.mUnrestrictedKeepClearRects;
        }
        return Collections.emptyList();
    }

    public void setHandwritingBoundsOffsets(float offsetLeft, float offsetTop, float offsetRight, float offsetBottom) {
        this.mHandwritingBoundsOffsetLeft = offsetLeft;
        this.mHandwritingBoundsOffsetTop = offsetTop;
        this.mHandwritingBoundsOffsetRight = offsetRight;
        this.mHandwritingBoundsOffsetBottom = offsetBottom;
    }

    public float getHandwritingBoundsOffsetLeft() {
        return this.mHandwritingBoundsOffsetLeft;
    }

    public float getHandwritingBoundsOffsetTop() {
        return this.mHandwritingBoundsOffsetTop;
    }

    public float getHandwritingBoundsOffsetRight() {
        return this.mHandwritingBoundsOffsetRight;
    }

    public float getHandwritingBoundsOffsetBottom() {
        return this.mHandwritingBoundsOffsetBottom;
    }

    public void setHandwritingArea(Rect rect) {
        ListenerInfo info = getListenerInfo();
        info.mHandwritingArea = rect;
        updatePositionUpdateListener();
        postUpdate(new View$$ExternalSyntheticLambda7(this));
    }

    public Rect getHandwritingArea() {
        ListenerInfo info = this.mListenerInfo;
        if (info != null && info.mHandwritingArea != null) {
            return new Rect(info.mHandwritingArea);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateHandwritingArea() {
        AttachInfo ai;
        if (isAutoHandwritingEnabled() && (ai = this.mAttachInfo) != null) {
            ai.mViewRootImpl.getHandwritingInitiator().updateHandwritingAreasForView(this);
        }
    }

    public void setHandwritingDelegatorCallback(Runnable callback) {
        this.mHandwritingDelegatorCallback = callback;
        if (callback != null) {
            setHandwritingArea(new Rect(0, 0, getWidth(), getHeight()));
        }
    }

    public Runnable getHandwritingDelegatorCallback() {
        return this.mHandwritingDelegatorCallback;
    }

    public void setAllowedHandwritingDelegatePackage(String allowedPackageName) {
        this.mAllowedHandwritingDelegatePackageName = allowedPackageName;
    }

    public String getAllowedHandwritingDelegatePackageName() {
        return this.mAllowedHandwritingDelegatePackageName;
    }

    public void setIsHandwritingDelegate(boolean isHandwritingDelegate) {
        this.mIsHandwritingDelegate = isHandwritingDelegate;
    }

    public boolean isHandwritingDelegate() {
        return this.mIsHandwritingDelegate;
    }

    public void setAllowedHandwritingDelegatorPackage(String allowedPackageName) {
        this.mAllowedHandwritingDelegatorPackageName = allowedPackageName;
    }

    public String getAllowedHandwritingDelegatorPackageName() {
        return this.mAllowedHandwritingDelegatorPackageName;
    }

    public void getLocationInSurface(int[] location) {
        getLocationInWindow(location);
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && attachInfo.mViewRootImpl != null) {
            location[0] = location[0] + this.mAttachInfo.mViewRootImpl.mWindowAttributes.surfaceInsets.left;
            location[1] = location[1] + this.mAttachInfo.mViewRootImpl.mWindowAttributes.surfaceInsets.top;
        }
    }

    public WindowInsets getRootWindowInsets() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mViewRootImpl.getWindowInsets(false);
        }
        return null;
    }

    public WindowInsetsController getWindowInsetsController() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mViewRootImpl.getInsetsController();
        }
        ViewParent parent = getParent();
        if (parent instanceof View) {
            return ((View) parent).getWindowInsetsController();
        }
        if (parent instanceof ViewRootImpl) {
            return ((ViewRootImpl) parent).getInsetsController();
        }
        return null;
    }

    public final OnBackInvokedDispatcher findOnBackInvokedDispatcher() {
        ViewParent parent = getParent();
        if (parent != null) {
            return parent.findOnBackInvokedDispatcherForChild(this, this);
        }
        return null;
    }

    @Deprecated
    protected boolean computeFitSystemWindows(Rect inoutInsets, Rect outLocalInsets) {
        WindowInsets innerInsets = computeSystemWindowInsets(new WindowInsets(inoutInsets), outLocalInsets);
        inoutInsets.set(innerInsets.getSystemWindowInsetsAsRect());
        return innerInsets.isSystemWindowInsetsConsumed();
    }

    public WindowInsets computeSystemWindowInsets(WindowInsets in, Rect outLocalInsets) {
        AttachInfo attachInfo;
        boolean isOptionalFitSystemWindows = ((this.mViewFlags & 2048) == 0 && (this.mPrivateFlags4 & 256) == 0) ? false : true;
        if (isOptionalFitSystemWindows && (attachInfo = this.mAttachInfo) != null) {
            Window.OnContentApplyWindowInsetsListener listener = attachInfo.mContentOnApplyWindowInsetsListener;
            if (listener == null) {
                outLocalInsets.setEmpty();
                return in;
            }
            Pair<Insets, WindowInsets> result = listener.onContentApplyWindowInsets(this, in);
            outLocalInsets.set(result.first.toRect());
            return result.second;
        }
        outLocalInsets.set(in.getSystemWindowInsetsAsRect());
        return in.consumeSystemWindowInsets().inset(outLocalInsets);
    }

    public void setFitsSystemWindows(boolean fitSystemWindows) {
        setFlags(fitSystemWindows ? 2 : 0, 2);
    }

    @ViewDebug.ExportedProperty
    public boolean getFitsSystemWindows() {
        return (this.mViewFlags & 2) == 2;
    }

    public boolean fitsSystemWindows() {
        return getFitsSystemWindows();
    }

    @Deprecated
    public void requestFitSystemWindows() {
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.requestFitSystemWindows();
        }
    }

    public void requestApplyInsets() {
        requestFitSystemWindows();
    }

    public void makeOptionalFitsSystemWindows() {
        setFlags(2048, 2048);
    }

    public void makeFrameworkOptionalFitsSystemWindows() {
        this.mPrivateFlags4 |= 256;
    }

    public boolean isFrameworkOptionalFitsSystemWindows() {
        return (this.mPrivateFlags4 & 256) != 0;
    }

    @ViewDebug.ExportedProperty(mapping = {@ViewDebug.IntToString(from = 0, m86to = "VISIBLE"), @ViewDebug.IntToString(from = 4, m86to = "INVISIBLE"), @ViewDebug.IntToString(from = 8, m86to = "GONE")})
    public int getVisibility() {
        return this.mViewFlags & 12;
    }

    @RemotableViewMethod
    public void setVisibility(int visibility) {
        setFlags(visibility, 12);
    }

    @ViewDebug.ExportedProperty
    public boolean isEnabled() {
        return (this.mViewFlags & 32) == 0;
    }

    @RemotableViewMethod
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) {
            return;
        }
        setFlags(enabled ? 0 : 32, 32);
        refreshDrawableState();
        invalidate(true);
        if (!enabled) {
            cancelPendingInputEvents();
        }
        notifyViewAccessibilityStateChangedIfNeeded(4096);
    }

    @RemotableViewMethod
    public void setFocusable(boolean focusable) {
        setFocusable(focusable ? 1 : 0);
    }

    @RemotableViewMethod
    public void setFocusable(int focusable) {
        if ((focusable & 17) == 0) {
            setFlags(0, 262144);
        }
        setFlags(focusable, 17);
    }

    @RemotableViewMethod
    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        setFlags(focusableInTouchMode ? 262144 : 0, 262144);
        if (focusableInTouchMode) {
            setFlags(1, 17);
        }
    }

    public void setAutofillHints(String... autofillHints) {
        if (autofillHints == null || autofillHints.length == 0) {
            this.mAutofillHints = null;
        } else {
            this.mAutofillHints = autofillHints;
        }
    }

    public void setAutofilled(boolean isAutofilled, boolean hideHighlight) {
        boolean wasChanged = isAutofilled != isAutofilled();
        if (wasChanged) {
            if (isAutofilled) {
                this.mPrivateFlags3 |= 65536;
            } else {
                this.mPrivateFlags3 &= -65537;
            }
            if (hideHighlight) {
                this.mPrivateFlags4 |= 512;
            } else {
                this.mPrivateFlags4 &= -513;
            }
            invalidate();
        }
    }

    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        setFlags(soundEffectsEnabled ? 134217728 : 0, 134217728);
    }

    @ViewDebug.ExportedProperty
    public boolean isSoundEffectsEnabled() {
        return 134217728 == (this.mViewFlags & 134217728);
    }

    public void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) {
        setFlags(hapticFeedbackEnabled ? 268435456 : 0, 268435456);
    }

    @ViewDebug.ExportedProperty
    public boolean isHapticFeedbackEnabled() {
        return 268435456 == (this.mViewFlags & 268435456);
    }

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT, mapping = {@ViewDebug.IntToString(from = 0, m86to = "LTR"), @ViewDebug.IntToString(from = 1, m86to = "RTL"), @ViewDebug.IntToString(from = 2, m86to = "INHERIT"), @ViewDebug.IntToString(from = 3, m86to = "LOCALE")})
    public int getRawLayoutDirection() {
        return (this.mPrivateFlags2 & 12) >> 2;
    }

    @RemotableViewMethod
    public void setLayoutDirection(int layoutDirection) {
        if (getRawLayoutDirection() != layoutDirection) {
            this.mPrivateFlags2 &= -13;
            resetRtlProperties();
            this.mPrivateFlags2 |= (layoutDirection << 2) & 12;
            resolveRtlPropertiesIfNeeded();
            requestLayout();
            invalidate(true);
        }
    }

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT, mapping = {@ViewDebug.IntToString(from = 0, m86to = "RESOLVED_DIRECTION_LTR"), @ViewDebug.IntToString(from = 1, m86to = "RESOLVED_DIRECTION_RTL")})
    public int getLayoutDirection() {
        int targetSdkVersion = getContext().getApplicationInfo().targetSdkVersion;
        if (targetSdkVersion >= 17) {
            return (this.mPrivateFlags2 & 16) == 16 ? 1 : 0;
        }
        this.mPrivateFlags2 |= 32;
        return 0;
    }

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    public boolean isLayoutRtl() {
        return getLayoutDirection() == 1;
    }

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    public boolean hasTransientState() {
        return (this.mPrivateFlags2 & Integer.MIN_VALUE) == Integer.MIN_VALUE;
    }

    public void setHasTransientState(boolean hasTransientState) {
        boolean oldHasTransientState = hasTransientState();
        int i = hasTransientState ? this.mTransientStateCount + 1 : this.mTransientStateCount - 1;
        this.mTransientStateCount = i;
        if (i < 0) {
            this.mTransientStateCount = 0;
            Log.m110e(VIEW_LOG_TAG, "hasTransientState decremented below 0: unmatched pair of setHasTransientState calls");
        } else if ((hasTransientState && i == 1) || (!hasTransientState && i == 0)) {
            this.mPrivateFlags2 = (this.mPrivateFlags2 & Integer.MAX_VALUE) | (hasTransientState ? Integer.MIN_VALUE : 0);
            boolean newHasTransientState = hasTransientState();
            ViewParent viewParent = this.mParent;
            if (viewParent != null && newHasTransientState != oldHasTransientState) {
                try {
                    viewParent.childHasTransientStateChanged(this, newHasTransientState);
                } catch (AbstractMethodError e) {
                    Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                }
            }
        }
    }

    public void setHasTranslationTransientState(boolean hasTranslationTransientState) {
        if (hasTranslationTransientState) {
            this.mPrivateFlags4 |= 16384;
        } else {
            this.mPrivateFlags4 &= -16385;
        }
    }

    public boolean hasTranslationTransientState() {
        return (this.mPrivateFlags4 & 16384) == 16384;
    }

    public void clearTranslationState() {
        ViewTranslationCallback viewTranslationCallback = this.mViewTranslationCallback;
        if (viewTranslationCallback != null) {
            viewTranslationCallback.onClearTranslation(this);
        }
        clearViewTranslationResponse();
        if (hasTranslationTransientState()) {
            setHasTransientState(false);
            setHasTranslationTransientState(false);
        }
    }

    public boolean isAttachedToWindow() {
        return this.mAttachInfo != null;
    }

    public boolean isLaidOut() {
        return (this.mPrivateFlags3 & 4) == 4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLayoutValid() {
        return isLaidOut() && (this.mPrivateFlags & 4096) == 0;
    }

    public void setWillNotDraw(boolean willNotDraw) {
        setFlags(willNotDraw ? 128 : 0, 128);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean willNotDraw() {
        return (this.mViewFlags & 128) == 128;
    }

    @Deprecated
    public void setWillNotCacheDrawing(boolean willNotCacheDrawing) {
        setFlags(willNotCacheDrawing ? 131072 : 0, 131072);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    @Deprecated
    public boolean willNotCacheDrawing() {
        return (this.mViewFlags & 131072) == 131072;
    }

    @ViewDebug.ExportedProperty
    public boolean isClickable() {
        return (this.mViewFlags & 16384) == 16384;
    }

    public void setClickable(boolean clickable) {
        setFlags(clickable ? 16384 : 0, 16384);
    }

    public void setAllowClickWhenDisabled(boolean clickableWhenDisabled) {
        if (clickableWhenDisabled) {
            this.mPrivateFlags4 |= 4096;
        } else {
            this.mPrivateFlags4 &= -4097;
        }
    }

    public boolean isLongClickable() {
        return (this.mViewFlags & 2097152) == 2097152;
    }

    public void setLongClickable(boolean longClickable) {
        setFlags(longClickable ? 2097152 : 0, 2097152);
    }

    public boolean isContextClickable() {
        return (this.mViewFlags & 8388608) == 8388608;
    }

    public void setContextClickable(boolean contextClickable) {
        setFlags(contextClickable ? 8388608 : 0, 8388608);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPressed(boolean pressed, float x, float y) {
        if (pressed) {
            drawableHotspotChanged(x, y);
        }
        setPressed(pressed);
    }

    public void setPressed(boolean pressed) {
        int i = this.mPrivateFlags;
        boolean needsRefresh = pressed != ((i & 16384) == 16384);
        if (pressed) {
            this.mPrivateFlags = i | 16384;
        } else {
            this.mPrivateFlags = i & (-16385);
        }
        if (needsRefresh) {
            refreshDrawableState();
        }
        dispatchSetPressed(pressed);
    }

    protected void dispatchSetPressed(boolean pressed) {
    }

    @ViewDebug.ExportedProperty
    public boolean isPressed() {
        return (this.mPrivateFlags & 16384) == 16384;
    }

    public boolean isAssistBlocked() {
        return (this.mPrivateFlags3 & 16384) != 0;
    }

    public void setAssistBlocked(boolean enabled) {
        if (enabled) {
            this.mPrivateFlags3 |= 16384;
        } else {
            this.mPrivateFlags3 &= -16385;
        }
    }

    public boolean isSaveEnabled() {
        return (this.mViewFlags & 65536) != 65536;
    }

    public void setSaveEnabled(boolean enabled) {
        setFlags(enabled ? 0 : 65536, 65536);
    }

    @ViewDebug.ExportedProperty
    public boolean getFilterTouchesWhenObscured() {
        return (this.mViewFlags & 1024) != 0;
    }

    public void setFilterTouchesWhenObscured(boolean enabled) {
        setFlags(enabled ? 1024 : 0, 1024);
        calculateAccessibilityDataSensitive();
    }

    public boolean isSaveFromParentEnabled() {
        return (this.mViewFlags & 536870912) != 536870912;
    }

    public void setSaveFromParentEnabled(boolean enabled) {
        setFlags(enabled ? 0 : 536870912, 536870912);
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public final boolean isFocusable() {
        return 1 == (this.mViewFlags & 1);
    }

    @ViewDebug.ExportedProperty(category = "focus", mapping = {@ViewDebug.IntToString(from = 0, m86to = "NOT_FOCUSABLE"), @ViewDebug.IntToString(from = 1, m86to = "FOCUSABLE"), @ViewDebug.IntToString(from = 16, m86to = "FOCUSABLE_AUTO")})
    public int getFocusable() {
        int i = this.mViewFlags;
        if ((i & 16) > 0) {
            return 16;
        }
        return i & 1;
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public final boolean isFocusableInTouchMode() {
        return 262144 == (this.mViewFlags & 262144);
    }

    public boolean isScreenReaderFocusable() {
        return (this.mPrivateFlags3 & 268435456) != 0;
    }

    public void setScreenReaderFocusable(boolean screenReaderFocusable) {
        updatePflags3AndNotifyA11yIfChanged(268435456, screenReaderFocusable);
    }

    public boolean isAccessibilityHeading() {
        return (this.mPrivateFlags3 & Integer.MIN_VALUE) != 0;
    }

    public void setAccessibilityHeading(boolean isHeading) {
        updatePflags3AndNotifyA11yIfChanged(Integer.MIN_VALUE, isHeading);
    }

    private void updatePflags3AndNotifyA11yIfChanged(int mask, boolean newValue) {
        int pflags3;
        int pflags32 = this.mPrivateFlags3;
        if (newValue) {
            pflags3 = pflags32 | mask;
        } else {
            pflags3 = pflags32 & (~mask);
        }
        if (pflags3 != this.mPrivateFlags3) {
            this.mPrivateFlags3 = pflags3;
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
    }

    public View focusSearch(int direction) {
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            return viewParent.focusSearch(this, direction);
        }
        return null;
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public final boolean isKeyboardNavigationCluster() {
        return (this.mPrivateFlags3 & 32768) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View findKeyboardNavigationCluster() {
        ViewParent viewParent = this.mParent;
        if (viewParent instanceof View) {
            View cluster = ((View) viewParent).findKeyboardNavigationCluster();
            if (cluster != null) {
                return cluster;
            }
            if (isKeyboardNavigationCluster()) {
                return this;
            }
            return null;
        }
        return null;
    }

    public void setKeyboardNavigationCluster(boolean isCluster) {
        if (isCluster) {
            this.mPrivateFlags3 |= 32768;
        } else {
            this.mPrivateFlags3 &= -32769;
        }
    }

    public final void setFocusedInCluster() {
        setFocusedInCluster(findKeyboardNavigationCluster());
    }

    private void setFocusedInCluster(View cluster) {
        if (this instanceof ViewGroup) {
            ((ViewGroup) this).mFocusedInCluster = null;
        }
        if (cluster == this) {
            return;
        }
        View child = this;
        for (ViewParent parent = this.mParent; parent instanceof ViewGroup; parent = parent.getParent()) {
            ((ViewGroup) parent).mFocusedInCluster = child;
            if (parent != cluster) {
                child = (View) parent;
            } else {
                return;
            }
        }
    }

    private void updateFocusedInCluster(View oldFocus, int direction) {
        if (oldFocus != null) {
            View oldCluster = oldFocus.findKeyboardNavigationCluster();
            View cluster = findKeyboardNavigationCluster();
            if (oldCluster != cluster) {
                oldFocus.setFocusedInCluster(oldCluster);
                ViewParent viewParent = oldFocus.mParent;
                if (!(viewParent instanceof ViewGroup)) {
                    return;
                }
                if (direction == 2 || direction == 1) {
                    ((ViewGroup) viewParent).clearFocusedInCluster(oldFocus);
                } else if ((oldFocus instanceof ViewGroup) && ((ViewGroup) oldFocus).getDescendantFocusability() == 262144 && ViewRootImpl.isViewDescendantOf(this, oldFocus)) {
                    ((ViewGroup) oldFocus.mParent).clearFocusedInCluster(oldFocus);
                }
            }
        }
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public final boolean isFocusedByDefault() {
        return (this.mPrivateFlags3 & 262144) != 0;
    }

    @RemotableViewMethod
    public void setFocusedByDefault(boolean isFocusedByDefault) {
        int i = this.mPrivateFlags3;
        if (isFocusedByDefault == ((i & 262144) != 0)) {
            return;
        }
        if (isFocusedByDefault) {
            this.mPrivateFlags3 = i | 262144;
        } else {
            this.mPrivateFlags3 = i & (-262145);
        }
        ViewParent viewParent = this.mParent;
        if (viewParent instanceof ViewGroup) {
            if (isFocusedByDefault) {
                ((ViewGroup) viewParent).setDefaultFocus(this);
            } else {
                ((ViewGroup) viewParent).clearDefaultFocus(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasDefaultFocus() {
        return isFocusedByDefault();
    }

    public View keyboardNavigationClusterSearch(View currentCluster, int direction) {
        if (isKeyboardNavigationCluster()) {
            currentCluster = this;
        }
        if (isRootNamespace()) {
            return FocusFinder.getInstance().findNextKeyboardNavigationCluster(this, currentCluster, direction);
        }
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            return viewParent.keyboardNavigationClusterSearch(currentCluster, direction);
        }
        return null;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return false;
    }

    public void setDefaultFocusHighlightEnabled(boolean defaultFocusHighlightEnabled) {
        this.mDefaultFocusHighlightEnabled = defaultFocusHighlightEnabled;
    }

    @ViewDebug.ExportedProperty(category = "focus")
    public final boolean getDefaultFocusHighlightEnabled() {
        return this.mDefaultFocusHighlightEnabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View findUserSetNextFocus(final View root, int direction) {
        switch (direction) {
            case 1:
                if (this.mID == -1) {
                    return null;
                }
                return root.findViewByPredicateInsideOut(this, new Predicate() { // from class: android.view.View$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$findUserSetNextFocus$3;
                        lambda$findUserSetNextFocus$3 = View.this.lambda$findUserSetNextFocus$3(root, startView, (View) obj);
                        return lambda$findUserSetNextFocus$3;
                    }
                });
            case 2:
                int i = this.mNextFocusForwardId;
                if (i == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, i);
            case 17:
                int i2 = this.mNextFocusLeftId;
                if (i2 == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, i2);
            case 33:
                int i3 = this.mNextFocusUpId;
                if (i3 == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, i3);
            case 66:
                int i4 = this.mNextFocusRightId;
                if (i4 == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, i4);
            case 130:
                int i5 = this.mNextFocusDownId;
                if (i5 == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, i5);
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$findUserSetNextFocus$3(View rootView, View startView, View t) {
        return findViewInsideOutShouldExist(rootView, t, t.mNextFocusForwardId) == startView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View findUserSetNextKeyboardNavigationCluster(View root, int direction) {
        switch (direction) {
            case 1:
                if (this.mID == -1) {
                    return null;
                }
                final int id = this.mID;
                return root.findViewByPredicateInsideOut(this, new Predicate() { // from class: android.view.View$$ExternalSyntheticLambda9
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return View.lambda$findUserSetNextKeyboardNavigationCluster$4(id, (View) obj);
                    }
                });
            case 2:
                int i = this.mNextClusterForwardId;
                if (i == -1) {
                    return null;
                }
                return findViewInsideOutShouldExist(root, i);
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$findUserSetNextKeyboardNavigationCluster$4(int id, View t) {
        return t.mNextClusterForwardId == id;
    }

    private View findViewInsideOutShouldExist(View root, int id) {
        return findViewInsideOutShouldExist(root, this, id);
    }

    private View findViewInsideOutShouldExist(View root, View start, int id) {
        if (this.mMatchIdPredicate == null) {
            this.mMatchIdPredicate = new MatchIdPredicate();
        }
        this.mMatchIdPredicate.mId = id;
        View result = root.findViewByPredicateInsideOut(start, this.mMatchIdPredicate);
        if (result == null) {
            Log.m104w(VIEW_LOG_TAG, "couldn't find view with id " + id);
        }
        return result;
    }

    public ArrayList<View> getFocusables(int direction) {
        ArrayList<View> result = new ArrayList<>(24);
        addFocusables(result, direction);
        return result;
    }

    public void addFocusables(ArrayList<View> views, int direction) {
        addFocusables(views, direction, isInTouchMode() ? 1 : 0);
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (views == null || !canTakeFocus()) {
            return;
        }
        if ((focusableMode & 1) == 1 && !isFocusableInTouchMode()) {
            return;
        }
        views.add(this);
    }

    public void addKeyboardNavigationClusters(Collection<View> views, int direction) {
        if (!isKeyboardNavigationCluster() || !hasFocusable()) {
            return;
        }
        views.add(this);
    }

    public void findViewsWithText(ArrayList<View> outViews, CharSequence searched, int flags) {
        CharSequence charSequence;
        if (getAccessibilityNodeProvider() != null) {
            if ((flags & 4) != 0) {
                outViews.add(this);
            }
        } else if ((flags & 2) != 0 && searched != null && searched.length() > 0 && (charSequence = this.mContentDescription) != null && charSequence.length() > 0) {
            String searchedLowerCase = searched.toString().toLowerCase();
            String contentDescriptionLowerCase = this.mContentDescription.toString().toLowerCase();
            if (contentDescriptionLowerCase.contains(searchedLowerCase)) {
                outViews.add(this);
            }
        }
    }

    public ArrayList<View> getTouchables() {
        ArrayList<View> result = new ArrayList<>();
        addTouchables(result);
        return result;
    }

    public void addTouchables(ArrayList<View> views) {
        int viewFlags = this.mViewFlags;
        if (((viewFlags & 16384) == 16384 || (viewFlags & 2097152) == 2097152 || (viewFlags & 8388608) == 8388608) && (viewFlags & 32) == 0) {
            views.add(this);
        }
    }

    public boolean isAccessibilityFocused() {
        return (this.mPrivateFlags2 & 67108864) != 0;
    }

    public boolean requestAccessibilityFocus() {
        AccessibilityManager manager = AccessibilityManager.getInstance(this.mContext);
        if (manager.isEnabled() && manager.isTouchExplorationEnabled() && (this.mViewFlags & 12) == 0) {
            int i = this.mPrivateFlags2;
            if ((i & 67108864) == 0) {
                this.mPrivateFlags2 = i | 67108864;
                ViewRootImpl viewRootImpl = getViewRootImpl();
                if (viewRootImpl != null) {
                    viewRootImpl.setAccessibilityFocus(this, null);
                }
                invalidate();
                sendAccessibilityEvent(32768);
                return true;
            }
            return false;
        }
        return false;
    }

    public void clearAccessibilityFocus() {
        View focusHost;
        clearAccessibilityFocusNoCallbacks(0);
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null && (focusHost = viewRootImpl.getAccessibilityFocusedHost()) != null && ViewRootImpl.isViewDescendantOf(focusHost, this)) {
            viewRootImpl.setAccessibilityFocus(null, null);
        }
    }

    private void sendAccessibilityHoverEvent(int eventType) {
        View source = this;
        while (!source.includeForAccessibility(false)) {
            ViewParent parent = source.getParent();
            if (parent instanceof View) {
                source = (View) parent;
            } else {
                return;
            }
        }
        source.sendAccessibilityEvent(eventType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearAccessibilityFocusNoCallbacks(int action) {
        int i = this.mPrivateFlags2;
        if ((67108864 & i) != 0) {
            this.mPrivateFlags2 = i & (-67108865);
            invalidate();
            if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
                AccessibilityEvent event = AccessibilityEvent.obtain(65536);
                event.setAction(action);
                AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
                if (accessibilityDelegate != null) {
                    accessibilityDelegate.sendAccessibilityEventUnchecked(this, event);
                } else {
                    sendAccessibilityEventUnchecked(event);
                }
            }
            updatePreferKeepClearForFocus();
        }
    }

    public final boolean requestFocus() {
        return requestFocus(130);
    }

    public boolean restoreFocusInCluster(int direction) {
        if (restoreDefaultFocus()) {
            return true;
        }
        return requestFocus(direction);
    }

    public boolean restoreFocusNotInCluster() {
        return requestFocus(130);
    }

    public boolean restoreDefaultFocus() {
        return requestFocus(130);
    }

    public final boolean requestFocus(int direction) {
        return requestFocus(direction, null);
    }

    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return requestFocusNoSearch(direction, previouslyFocusedRect);
    }

    private boolean requestFocusNoSearch(int direction, Rect previouslyFocusedRect) {
        if (canTakeFocus()) {
            if ((!isInTouchMode() || 262144 == (this.mViewFlags & 262144)) && !hasAncestorThatBlocksDescendantFocus()) {
                if (!isLayoutValid()) {
                    this.mPrivateFlags |= 1;
                } else {
                    clearParentsWantFocus();
                }
                handleFocusGainInternal(direction, previouslyFocusedRect);
                return true;
            }
            return false;
        }
        return false;
    }

    void clearParentsWantFocus() {
        ViewParent viewParent = this.mParent;
        if (viewParent instanceof View) {
            ((View) viewParent).mPrivateFlags &= -2;
            ((View) viewParent).clearParentsWantFocus();
        }
    }

    public final boolean requestFocusFromTouch() {
        ViewRootImpl viewRoot;
        if (isInTouchMode() && (viewRoot = getViewRootImpl()) != null) {
            viewRoot.ensureTouchMode(false);
        }
        return requestFocus(130);
    }

    private boolean hasAncestorThatBlocksDescendantFocus() {
        boolean focusableInTouchMode = isFocusableInTouchMode();
        ViewParent ancestor = this.mParent;
        while (ancestor instanceof ViewGroup) {
            ViewGroup vgAncestor = (ViewGroup) ancestor;
            if (vgAncestor.getDescendantFocusability() != 393216) {
                if (!focusableInTouchMode && vgAncestor.shouldBlockFocusForTouchscreen()) {
                    return true;
                }
                ancestor = vgAncestor.getParent();
            } else {
                return true;
            }
        }
        return false;
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE, mapping = {@ViewDebug.IntToString(from = 0, m86to = "auto"), @ViewDebug.IntToString(from = 1, m86to = MediaMetrics.Value.YES), @ViewDebug.IntToString(from = 2, m86to = MediaMetrics.Value.f269NO), @ViewDebug.IntToString(from = 4, m86to = "noHideDescendants")})
    public int getImportantForAccessibility() {
        return (this.mPrivateFlags2 & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK) >> 20;
    }

    public void setAccessibilityLiveRegion(int mode) {
        if (mode != getAccessibilityLiveRegion()) {
            int i = this.mPrivateFlags2 & (-25165825);
            this.mPrivateFlags2 = i;
            this.mPrivateFlags2 = i | ((mode << 23) & 25165824);
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
    }

    public int getAccessibilityLiveRegion() {
        return (this.mPrivateFlags2 & 25165824) >> 23;
    }

    public void setImportantForAccessibility(int mode) {
        View focusHost;
        int oldMode = getImportantForAccessibility();
        if (mode != oldMode) {
            boolean oldIncludeForAccessibility = true;
            boolean hideDescendants = mode == 4;
            if ((mode == 2 || hideDescendants) && (focusHost = findAccessibilityFocusHost(hideDescendants)) != null) {
                focusHost.clearAccessibilityFocus();
            }
            boolean maySkipNotify = oldMode == 0 || mode == 0;
            if (!maySkipNotify || !includeForAccessibility(false)) {
                oldIncludeForAccessibility = false;
            }
            int i = this.mPrivateFlags2 & (-7340033);
            this.mPrivateFlags2 = i;
            this.mPrivateFlags2 = i | ((mode << 20) & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK);
            if (!maySkipNotify || oldIncludeForAccessibility != includeForAccessibility(false)) {
                notifySubtreeAccessibilityStateChangedIfNeeded();
            } else {
                notifyViewAccessibilityStateChangedIfNeeded(0);
            }
        }
    }

    private View findAccessibilityFocusHost(boolean searchDescendants) {
        ViewRootImpl viewRoot;
        View focusHost;
        if (isAccessibilityFocusedViewOrHost()) {
            return this;
        }
        if (searchDescendants && (viewRoot = getViewRootImpl()) != null && (focusHost = viewRoot.getAccessibilityFocusedHost()) != null && ViewRootImpl.isViewDescendantOf(focusHost, this)) {
            return focusHost;
        }
        return null;
    }

    public boolean isImportantForAccessibility() {
        int mode = getImportantForAccessibility();
        if (mode == 2 || mode == 4) {
            return false;
        }
        for (ViewParent parent = this.mParent; parent instanceof View; parent = parent.getParent()) {
            if (((View) parent).getImportantForAccessibility() == 4) {
                return false;
            }
        }
        return mode == 1 || isActionableForAccessibility() || hasListenersForAccessibility() || getAccessibilityNodeProvider() != null || getAccessibilityLiveRegion() != 0 || isAccessibilityPane();
    }

    public ViewParent getParentForAccessibility() {
        ViewParent viewParent = this.mParent;
        if (viewParent instanceof View) {
            View parentView = (View) viewParent;
            if (parentView.includeForAccessibility()) {
                return this.mParent;
            }
            return this.mParent.getParentForAccessibility();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getSelfOrParentImportantForA11y() {
        if (isImportantForAccessibility()) {
            return this;
        }
        ViewParent parent = getParentForAccessibility();
        if (parent instanceof View) {
            return (View) parent;
        }
        return null;
    }

    public void addChildrenForAccessibility(ArrayList<View> outChildren) {
    }

    public boolean includeForAccessibility() {
        return includeForAccessibility(true);
    }

    public boolean includeForAccessibility(boolean forNodeTree) {
        if (this.mAttachInfo == null) {
            return false;
        }
        if (forNodeTree && !AccessibilityManager.getInstance(this.mContext).isRequestFromAccessibilityTool() && isAccessibilityDataSensitive()) {
            return false;
        }
        return (this.mAttachInfo.mAccessibilityFetchFlags & 128) != 0 || isImportantForAccessibility();
    }

    @ViewDebug.ExportedProperty(category = Context.ACCESSIBILITY_SERVICE)
    public boolean isAccessibilityDataSensitive() {
        if (this.mInferredAccessibilityDataSensitive == 0) {
            calculateAccessibilityDataSensitive();
        }
        return this.mInferredAccessibilityDataSensitive == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void calculateAccessibilityDataSensitive() {
        int i = this.mExplicitAccessibilityDataSensitive;
        if (i != 0) {
            this.mInferredAccessibilityDataSensitive = i;
        } else if (getFilterTouchesWhenObscured()) {
            this.mInferredAccessibilityDataSensitive = 1;
        } else {
            ViewParent viewParent = this.mParent;
            if ((viewParent instanceof View) && ((View) viewParent).isAccessibilityDataSensitive()) {
                this.mInferredAccessibilityDataSensitive = 1;
            } else {
                this.mInferredAccessibilityDataSensitive = 2;
            }
        }
    }

    public void setAccessibilityDataSensitive(int accessibilityDataSensitive) {
        this.mExplicitAccessibilityDataSensitive = accessibilityDataSensitive;
        calculateAccessibilityDataSensitive();
    }

    public boolean isActionableForAccessibility() {
        return isClickable() || isLongClickable() || isFocusable();
    }

    private boolean hasListenersForAccessibility() {
        ListenerInfo info = getListenerInfo();
        return (this.mTouchDelegate == null && info.mOnKeyListener == null && info.mOnTouchListener == null && info.mOnGenericMotionListener == null && info.mOnHoverListener == null && info.mOnDragListener == null) ? false : true;
    }

    public void notifyViewAccessibilityStateChangedIfNeeded(int changeType) {
        if (!AccessibilityManager.getInstance(this.mContext).isEnabled() || this.mAttachInfo == null) {
            return;
        }
        if (changeType != 1 && ((isAccessibilityPane() || (changeType == 32 && isAggregatedVisible())) && (isAggregatedVisible() || changeType == 32))) {
            AccessibilityEvent event = AccessibilityEvent.obtain();
            onInitializeAccessibilityEvent(event);
            event.setEventType(32);
            event.setContentChangeTypes(changeType);
            event.setSource(this);
            onPopulateAccessibilityEvent(event);
            ViewParent viewParent = this.mParent;
            if (viewParent != null) {
                try {
                    viewParent.requestSendAccessibilityEvent(this, event);
                } catch (AbstractMethodError e) {
                    Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                }
            }
        } else if (getAccessibilityLiveRegion() != 0) {
            AccessibilityEvent event2 = AccessibilityEvent.obtain();
            event2.setEventType(2048);
            event2.setContentChangeTypes(changeType);
            sendAccessibilityEventUnchecked(event2);
        } else {
            ViewParent viewParent2 = this.mParent;
            if (viewParent2 != null) {
                try {
                    viewParent2.notifySubtreeAccessibilityStateChanged(this, this, changeType);
                } catch (AbstractMethodError e2) {
                    Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e2);
                }
            }
        }
    }

    public void notifySubtreeAccessibilityStateChangedIfNeeded() {
        if (!AccessibilityManager.getInstance(this.mContext).isEnabled() || this.mAttachInfo == null) {
            return;
        }
        int i = this.mPrivateFlags2;
        if ((i & 134217728) == 0) {
            this.mPrivateFlags2 = i | 134217728;
            ViewParent viewParent = this.mParent;
            if (viewParent != null) {
                try {
                    viewParent.notifySubtreeAccessibilityStateChanged(this, this, 1);
                } catch (AbstractMethodError e) {
                    Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                }
            }
        }
    }

    private void notifySubtreeAccessibilityStateChangedByParentIfNeeded() {
        View sendA11yEventView;
        if (AccessibilityManager.getInstance(this.mContext).isEnabled() && (sendA11yEventView = (View) getParentForAccessibility()) != null && sendA11yEventView.isShown()) {
            sendA11yEventView.notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    public void setTransitionVisibility(int visibility) {
        this.mViewFlags = (this.mViewFlags & (-13)) | visibility;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetSubtreeAccessibilityStateChanged() {
        this.mPrivateFlags2 &= -134217729;
    }

    public boolean dispatchNestedPrePerformAccessibilityAction(int action, Bundle arguments) {
        for (ViewParent p = getParent(); p != null; p = p.getParent()) {
            if (p.onNestedPrePerformAccessibilityAction(this, action, arguments)) {
                return true;
            }
        }
        return false;
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            return accessibilityDelegate.performAccessibilityAction(this, action, arguments);
        }
        return performAccessibilityActionInternal(action, arguments);
    }

    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        int start;
        AttachInfo attachInfo;
        if (isNestedScrollingEnabled() && ((action == 8192 || action == 4096 || action == 16908344 || action == 16908345 || action == 16908346 || action == 16908347) && dispatchNestedPrePerformAccessibilityAction(action, arguments))) {
            return true;
        }
        switch (action) {
            case 1:
                if (!hasFocus()) {
                    getViewRootImpl().ensureTouchMode(false);
                    return requestFocus();
                }
                break;
            case 2:
                if (hasFocus()) {
                    clearFocus();
                    return !isFocused();
                }
                break;
            case 4:
                if (!isSelected()) {
                    setSelected(true);
                    return isSelected();
                }
                break;
            case 8:
                if (isSelected()) {
                    setSelected(false);
                    return !isSelected();
                }
                break;
            case 16:
                if (isClickable()) {
                    performClickInternal();
                    return true;
                }
                break;
            case 32:
                if (isLongClickable()) {
                    performLongClick();
                    return true;
                }
                break;
            case 64:
                if (!isAccessibilityFocused()) {
                    return requestAccessibilityFocus();
                }
                break;
            case 128:
                boolean extendSelection = isAccessibilityFocused();
                if (extendSelection) {
                    clearAccessibilityFocus();
                    return true;
                }
                break;
            case 256:
                if (arguments != null) {
                    int granularity = arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT);
                    boolean extendSelection2 = arguments.getBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN);
                    return traverseAtGranularity(granularity, true, extendSelection2);
                }
                break;
            case 512:
                if (arguments != null) {
                    int granularity2 = arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT);
                    boolean extendSelection3 = arguments.getBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN);
                    return traverseAtGranularity(granularity2, false, extendSelection3);
                }
                break;
            case 131072:
                CharSequence text = getIterableTextForAccessibility();
                if (text == null) {
                    return false;
                }
                if (arguments != null) {
                    start = arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, -1);
                } else {
                    start = -1;
                }
                int end = arguments != null ? arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, -1) : -1;
                if ((getAccessibilitySelectionStart() != start || getAccessibilitySelectionEnd() != end) && start == end) {
                    setAccessibilitySelection(start, end);
                    notifyViewAccessibilityStateChangedIfNeeded(0);
                    return true;
                }
                break;
            case 16908342:
                AttachInfo attachInfo2 = this.mAttachInfo;
                if (attachInfo2 != null) {
                    Rect r = attachInfo2.mTmpInvalRect;
                    getDrawingRect(r);
                    return requestRectangleOnScreen(r, true);
                }
                break;
            case 16908348:
                if (isContextClickable()) {
                    performContextClick();
                    return true;
                }
                break;
            case 16908356:
                TooltipInfo tooltipInfo = this.mTooltipInfo;
                if (tooltipInfo == null || tooltipInfo.mTooltipPopup == null) {
                    return showLongClickTooltip(0, 0);
                }
                return false;
            case 16908357:
                TooltipInfo tooltipInfo2 = this.mTooltipInfo;
                if (tooltipInfo2 == null || tooltipInfo2.mTooltipPopup == null) {
                    return false;
                }
                hideTooltip();
                return true;
            case 16908374:
                if (canAcceptAccessibilityDrop()) {
                    try {
                        AttachInfo attachInfo3 = this.mAttachInfo;
                        if (attachInfo3 != null && attachInfo3.mSession != null) {
                            int[] location = new int[2];
                            getLocationInWindow(location);
                            int centerX = location[0] + (getWidth() / 2);
                            int centerY = location[1] + (getHeight() / 2);
                            return this.mAttachInfo.mSession.dropForAccessibility(this.mAttachInfo.mWindow, centerX, centerY);
                        }
                    } catch (RemoteException e) {
                        Log.m109e(VIEW_LOG_TAG, "Unable to drop for accessibility", e);
                    }
                    return false;
                }
                return false;
            case 16908375:
                if (!startedSystemDragForAccessibility() || (attachInfo = this.mAttachInfo) == null || attachInfo.mDragToken == null) {
                    return false;
                }
                cancelDragAndDrop();
                return true;
        }
        return false;
    }

    private boolean canAcceptAccessibilityDrop() {
        ListenerInfo li;
        if (canAcceptDrag() && (li = this.mListenerInfo) != null) {
            return (li.mOnDragListener == null && li.mOnReceiveContentListener == null) ? false : true;
        }
        return false;
    }

    private boolean traverseAtGranularity(int granularity, boolean forward, boolean extendSelection) {
        AccessibilityIterators.TextSegmentIterator iterator;
        int selectionStart;
        int selectionEnd;
        CharSequence text = getIterableTextForAccessibility();
        if (text == null || text.length() == 0 || (iterator = getIteratorForGranularity(granularity)) == null) {
            return false;
        }
        int current = getAccessibilitySelectionEnd();
        if (current == -1) {
            current = forward ? 0 : text.length();
        }
        int[] range = forward ? iterator.following(current) : iterator.preceding(current);
        if (range == null) {
            return false;
        }
        int segmentStart = range[0];
        int segmentEnd = range[1];
        if (extendSelection && isAccessibilitySelectionExtendable()) {
            prepareForExtendedAccessibilitySelection();
            selectionStart = getAccessibilitySelectionStart();
            if (selectionStart == -1) {
                selectionStart = forward ? segmentStart : segmentEnd;
            }
            selectionEnd = forward ? segmentEnd : segmentStart;
        } else {
            selectionStart = forward ? segmentEnd : segmentStart;
            selectionEnd = selectionStart;
        }
        setAccessibilitySelection(selectionStart, selectionEnd);
        int action = forward ? 256 : 512;
        sendViewTextTraversedAtGranularityEvent(action, granularity, segmentStart, segmentEnd);
        return true;
    }

    public CharSequence getIterableTextForAccessibility() {
        return getContentDescription();
    }

    public boolean isAccessibilitySelectionExtendable() {
        return false;
    }

    public void prepareForExtendedAccessibilitySelection() {
    }

    public int getAccessibilitySelectionStart() {
        return this.mAccessibilityCursorPosition;
    }

    public int getAccessibilitySelectionEnd() {
        return getAccessibilitySelectionStart();
    }

    public void setAccessibilitySelection(int start, int end) {
        if (start == end && end == this.mAccessibilityCursorPosition) {
            return;
        }
        if (start >= 0 && start == end && end <= getIterableTextForAccessibility().length()) {
            this.mAccessibilityCursorPosition = start;
        } else {
            this.mAccessibilityCursorPosition = -1;
        }
        sendAccessibilityEvent(8192);
    }

    private void sendViewTextTraversedAtGranularityEvent(int action, int granularity, int fromIndex, int toIndex) {
        if (this.mParent == null) {
            return;
        }
        AccessibilityEvent event = AccessibilityEvent.obtain(131072);
        onInitializeAccessibilityEvent(event);
        onPopulateAccessibilityEvent(event);
        event.setFromIndex(fromIndex);
        event.setToIndex(toIndex);
        event.setAction(action);
        event.setMovementGranularity(granularity);
        this.mParent.requestSendAccessibilityEvent(this, event);
    }

    public AccessibilityIterators.TextSegmentIterator getIteratorForGranularity(int granularity) {
        switch (granularity) {
            case 1:
                CharSequence text = getIterableTextForAccessibility();
                if (text != null && text.length() > 0) {
                    AccessibilityIterators.CharacterTextSegmentIterator iterator = AccessibilityIterators.CharacterTextSegmentIterator.getInstance(this.mContext.getResources().getConfiguration().locale);
                    iterator.initialize(text.toString());
                    return iterator;
                }
                return null;
            case 2:
                CharSequence text2 = getIterableTextForAccessibility();
                if (text2 != null && text2.length() > 0) {
                    AccessibilityIterators.WordTextSegmentIterator iterator2 = AccessibilityIterators.WordTextSegmentIterator.getInstance(this.mContext.getResources().getConfiguration().locale);
                    iterator2.initialize(text2.toString());
                    return iterator2;
                }
                return null;
            case 8:
                CharSequence text3 = getIterableTextForAccessibility();
                if (text3 != null && text3.length() > 0) {
                    AccessibilityIterators.ParagraphTextSegmentIterator iterator3 = AccessibilityIterators.ParagraphTextSegmentIterator.getInstance();
                    iterator3.initialize(text3.toString());
                    return iterator3;
                }
                return null;
            default:
                return null;
        }
    }

    public final boolean isTemporarilyDetached() {
        return (this.mPrivateFlags3 & 33554432) != 0;
    }

    public void dispatchStartTemporaryDetach() {
        this.mPrivateFlags3 |= 33554432;
        notifyEnterOrExitForAutoFillIfNeeded(false);
        notifyAppearedOrDisappearedForContentCaptureIfNeeded(false);
        onStartTemporaryDetach();
    }

    public void onStartTemporaryDetach() {
        removeUnsetPressCallback();
        this.mPrivateFlags |= 67108864;
    }

    public void dispatchFinishTemporaryDetach() {
        this.mPrivateFlags3 &= -33554433;
        onFinishTemporaryDetach();
        if (hasWindowFocus() && hasFocus()) {
            notifyFocusChangeToImeFocusController(true);
        }
        notifyEnterOrExitForAutoFillIfNeeded(true);
        notifyAppearedOrDisappearedForContentCaptureIfNeeded(true);
    }

    public void onFinishTemporaryDetach() {
    }

    public KeyEvent.DispatcherState getKeyDispatcherState() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mKeyDispatchState;
        }
        return null;
    }

    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        return onKeyPreIme(event.getKeyCode(), event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onKeyEvent(event, 0);
        }
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnKeyListener != null && (this.mViewFlags & 32) == 0 && li.mOnKeyListener.onKey(this, event.getKeyCode(), event)) {
            return true;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (event.dispatch(this, attachInfo != null ? attachInfo.mKeyDispatchState : null, this)) {
            return true;
        }
        InputEventConsistencyVerifier inputEventConsistencyVerifier2 = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier2 != null) {
            inputEventConsistencyVerifier2.onUnhandledEvent(event, 0);
        }
        return false;
    }

    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return onKeyShortcut(event.getKeyCode(), event);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier;
        if (event.isTargetAccessibilityFocus()) {
            if (!isAccessibilityFocusedViewOrHost()) {
                return false;
            }
            event.setTargetAccessibilityFocus(false);
        }
        boolean result = false;
        InputEventConsistencyVerifier inputEventConsistencyVerifier2 = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier2 != null) {
            inputEventConsistencyVerifier2.onTouchEvent(event, 0);
        }
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            stopNestedScroll();
        }
        if (onFilterTouchEventForSecurity(event)) {
            if ((this.mViewFlags & 32) == 0 && handleScrollBarDragging(event)) {
                result = true;
            }
            ListenerInfo li = this.mListenerInfo;
            if (li != null && li.mOnTouchListener != null && (this.mViewFlags & 32) == 0 && li.mOnTouchListener.onTouch(this, event)) {
                result = true;
            }
            if (!result && onTouchEvent(event)) {
                result = true;
            }
        }
        if (!result && (inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier) != null) {
            inputEventConsistencyVerifier.onUnhandledEvent(event, 0);
        }
        if (actionMasked == 1 || actionMasked == 3 || (actionMasked == 0 && !result)) {
            stopNestedScroll();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAccessibilityFocusedViewOrHost() {
        return isAccessibilityFocused() || (getViewRootImpl() != null && getViewRootImpl().getAccessibilityFocusedHost() == this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canReceivePointerEvents() {
        return (this.mViewFlags & 12) == 0 || getAnimation() != null;
    }

    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        return (this.mViewFlags & 1024) == 0 || (event.getFlags() & 1) == 0;
    }

    public boolean dispatchTrackballEvent(MotionEvent event) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onTrackballEvent(event, 0);
        }
        return onTrackballEvent(event);
    }

    public boolean dispatchCapturedPointerEvent(MotionEvent event) {
        if (!hasPointerCapture()) {
            return false;
        }
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnCapturedPointerListener != null && li.mOnCapturedPointerListener.onCapturedPointer(this, event)) {
            return true;
        }
        return onCapturedPointerEvent(event);
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onGenericMotionEvent(event, 0);
        }
        int source = event.getSource();
        if ((source & 2) != 0) {
            int action = event.getAction();
            if (action == 9 || action == 7 || action == 10) {
                if (dispatchHoverEvent(event)) {
                    return true;
                }
            } else if (dispatchGenericPointerEvent(event)) {
                return true;
            }
        } else if (dispatchGenericFocusedEvent(event)) {
            return true;
        }
        if (dispatchGenericMotionEventInternal(event)) {
            return true;
        }
        InputEventConsistencyVerifier inputEventConsistencyVerifier2 = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier2 != null) {
            inputEventConsistencyVerifier2.onUnhandledEvent(event, 0);
        }
        return false;
    }

    private boolean dispatchGenericMotionEventInternal(MotionEvent event) {
        ListenerInfo li = this.mListenerInfo;
        if ((li == null || li.mOnGenericMotionListener == null || (this.mViewFlags & 32) != 0 || !li.mOnGenericMotionListener.onGenericMotion(this, event)) && !onGenericMotionEvent(event)) {
            int actionButton = event.getActionButton();
            switch (event.getActionMasked()) {
                case 11:
                    if (isContextClickable() && !this.mInContextButtonPress && !this.mHasPerformedLongPress && ((actionButton == 32 || actionButton == 2) && performContextClick(event.getX(), event.getY()))) {
                        this.mInContextButtonPress = true;
                        setPressed(true, event.getX(), event.getY());
                        removeTapCallback();
                        removeLongPressCallback();
                        return true;
                    }
                    break;
                case 12:
                    if (this.mInContextButtonPress && (actionButton == 32 || actionButton == 2)) {
                        this.mInContextButtonPress = false;
                        this.mIgnoreNextUpEvent = true;
                        break;
                    }
                    break;
            }
            InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
            if (inputEventConsistencyVerifier != null) {
                inputEventConsistencyVerifier.onUnhandledEvent(event, 0);
            }
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean dispatchHoverEvent(MotionEvent event) {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnHoverListener != null && (this.mViewFlags & 32) == 0 && li.mOnHoverListener.onHover(this, event)) {
            return true;
        }
        return onHoverEvent(event);
    }

    protected boolean hasHoveredChild() {
        return false;
    }

    protected boolean pointInHoveredChild(MotionEvent event) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean dispatchGenericPointerEvent(MotionEvent event) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean dispatchGenericFocusedEvent(MotionEvent event) {
        return false;
    }

    public final boolean dispatchPointerEvent(MotionEvent event) {
        if (event.isTouchEvent()) {
            return dispatchTouchEvent(event);
        }
        return dispatchGenericMotionEvent(event);
    }

    public void dispatchWindowFocusChanged(boolean hasFocus) {
        onWindowFocusChanged(hasFocus);
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
            if (isPressed()) {
                setPressed(false);
            }
            this.mPrivateFlags3 &= -131073;
            if ((this.mPrivateFlags & 2) != 0) {
                notifyFocusChangeToImeFocusController(false);
            }
            removeLongPressCallback();
            removeTapCallback();
            onFocusLost();
        } else if ((this.mPrivateFlags & 2) != 0) {
            notifyFocusChangeToImeFocusController(true);
        }
        refreshDrawableState();
    }

    public boolean hasWindowFocus() {
        AttachInfo attachInfo = this.mAttachInfo;
        return attachInfo != null && attachInfo.mHasWindowFocus;
    }

    public boolean hasImeFocus() {
        return getViewRootImpl() != null && getViewRootImpl().getImeFocusController().hasImeFocus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchVisibilityChanged(View changedView, int visibility) {
        onVisibilityChanged(changedView, visibility);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onVisibilityChanged(View changedView, int visibility) {
    }

    public void dispatchDisplayHint(int hint) {
        onDisplayHint(hint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDisplayHint(int hint) {
    }

    public void dispatchWindowVisibilityChanged(int visibility) {
        onWindowVisibilityChanged(visibility);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int visibility) {
        if (visibility == 0) {
            initialAwakenScrollBars();
        }
    }

    public boolean isAggregatedVisible() {
        return (this.mPrivateFlags3 & 536870912) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean dispatchVisibilityAggregated(boolean isVisible) {
        boolean thisVisible = getVisibility() == 0;
        if (thisVisible || !isVisible) {
            onVisibilityAggregated(isVisible);
        }
        return thisVisible && isVisible;
    }

    public void onVisibilityAggregated(boolean isVisible) {
        int i;
        AutofillManager afm;
        boolean oldVisible = isAggregatedVisible();
        this.mPrivateFlags3 = isVisible ? this.mPrivateFlags3 | 536870912 : this.mPrivateFlags3 & (-536870913);
        if (isVisible && this.mAttachInfo != null) {
            initialAwakenScrollBars();
        }
        Drawable dr = this.mBackground;
        if (dr != null && isVisible != dr.isVisible()) {
            dr.setVisible(isVisible, false);
        }
        Drawable hl = this.mDefaultFocusHighlight;
        if (hl != null && isVisible != hl.isVisible()) {
            hl.setVisible(isVisible, false);
        }
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        Drawable fg = foregroundInfo != null ? foregroundInfo.mDrawable : null;
        if (fg != null && isVisible != fg.isVisible()) {
            fg.setVisible(isVisible, false);
        }
        if (isAutofillable() && (afm = getAutofillManager()) != null && getAutofillViewId() > 1073741823) {
            Handler handler = this.mVisibilityChangeForAutofillHandler;
            if (handler != null) {
                handler.removeMessages(0);
            }
            if (isVisible) {
                afm.notifyViewVisibilityChanged(this, true);
            } else {
                if (this.mVisibilityChangeForAutofillHandler == null) {
                    this.mVisibilityChangeForAutofillHandler = new VisibilityChangeForAutofillHandler(afm, this);
                }
                this.mVisibilityChangeForAutofillHandler.obtainMessage(0, this).sendToTarget();
            }
        }
        if (isVisible != oldVisible) {
            if (isAccessibilityPane()) {
                if (isVisible) {
                    i = 16;
                } else {
                    i = 32;
                }
                notifyViewAccessibilityStateChangedIfNeeded(i);
            }
            notifyAppearedOrDisappearedForContentCaptureIfNeeded(isVisible);
            if (!getSystemGestureExclusionRects().isEmpty()) {
                postUpdate(new View$$ExternalSyntheticLambda10(this));
            }
            if (!collectPreferKeepClearRects().isEmpty()) {
                postUpdate(new View$$ExternalSyntheticLambda1(this));
            }
        }
    }

    public int getWindowVisibility() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mWindowVisibility;
        }
        return 8;
    }

    public void getWindowVisibleDisplayFrame(Rect outRect) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.getWindowVisibleDisplayFrame(outRect);
            return;
        }
        Display d = DisplayManagerGlobal.getInstance().getRealDisplay(0);
        d.getRectSize(outRect);
    }

    public void getWindowDisplayFrame(Rect outRect) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.getDisplayFrame(outRect);
            return;
        }
        Display d = DisplayManagerGlobal.getInstance().getRealDisplay(0);
        d.getRectSize(outRect);
    }

    public void dispatchConfigurationChanged(Configuration newConfig) {
        onConfigurationChanged(newConfig);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchCollectViewAttributes(AttachInfo attachInfo, int visibility) {
        performCollectViewAttributes(attachInfo, visibility);
    }

    void performCollectViewAttributes(AttachInfo attachInfo, int visibility) {
        if ((visibility & 12) == 0) {
            if ((this.mViewFlags & 67108864) == 67108864) {
                attachInfo.mKeepScreenOn = true;
            }
            attachInfo.mSystemUiVisibility |= this.mSystemUiVisibility;
            ListenerInfo li = this.mListenerInfo;
            if (li != null && li.mOnSystemUiVisibilityChangeListener != null) {
                attachInfo.mHasSystemUiListeners = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void needGlobalAttributesUpdate(boolean force) {
        AttachInfo ai = this.mAttachInfo;
        if (ai != null && !ai.mRecomputeGlobalAttributes) {
            if (force || ai.mKeepScreenOn || ai.mSystemUiVisibility != 0 || ai.mHasSystemUiListeners) {
                ai.mRecomputeGlobalAttributes = true;
            }
        }
    }

    @ViewDebug.ExportedProperty
    public boolean isInTouchMode() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mInTouchMode;
        }
        return this.mResources.getBoolean(C4057R.bool.config_defaultInTouchMode);
    }

    @ViewDebug.CapturedViewProperty
    public final Context getContext() {
        return this.mContext;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode) && event.hasNoModifiers()) {
            if ((this.mViewFlags & 32) == 32) {
                return true;
            }
            if (event.getRepeatCount() == 0) {
                int i = this.mViewFlags;
                boolean clickable = (i & 16384) == 16384 || (i & 2097152) == 2097152;
                if (clickable || (i & 1073741824) == 1073741824) {
                    float x = getWidth() / 2.0f;
                    float y = getHeight() / 2.0f;
                    if (clickable) {
                        setPressed(true, x, y);
                    }
                    checkForLongClick(ViewConfiguration.getLongPressTimeout(), x, y, 0);
                    return true;
                }
            }
        }
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode) && event.hasNoModifiers()) {
            int i = this.mViewFlags;
            if ((i & 32) == 32) {
                return true;
            }
            if ((i & 16384) == 16384 && isPressed()) {
                setPressed(false);
                if (!this.mHasPerformedLongPress) {
                    removeLongPressCallback();
                    if (!event.isCanceled()) {
                        return performClickInternal();
                    }
                }
            }
        }
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return false;
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onCheckIsTextEditor() {
        return false;
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return null;
    }

    public void onInputConnectionOpenedInternal(InputConnection inputConnection, EditorInfo editorInfo, Handler handler) {
    }

    public void onInputConnectionClosedInternal() {
    }

    public boolean checkInputConnectionProxy(View view) {
        return false;
    }

    public void createContextMenu(ContextMenu menu) {
        ContextMenu.ContextMenuInfo menuInfo = getContextMenuInfo();
        ((MenuBuilder) menu).setCurrentMenuInfo(menuInfo);
        onCreateContextMenu(menu);
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnCreateContextMenuListener != null) {
            li.mOnCreateContextMenuListener.onCreateContextMenu(menu, this, menuInfo);
        }
        ((MenuBuilder) menu).setCurrentMenuInfo(null);
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.createContextMenu(menu);
        }
    }

    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return null;
    }

    protected void onCreateContextMenu(ContextMenu menu) {
    }

    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return false;
    }

    private boolean dispatchTouchExplorationHoverEvent(MotionEvent event) {
        AccessibilityManager manager = AccessibilityManager.getInstance(this.mContext);
        if (manager.isEnabled() && manager.isTouchExplorationEnabled()) {
            boolean oldHoveringTouchDelegate = this.mHoveringTouchDelegate;
            int action = event.getActionMasked();
            boolean pointInDelegateRegion = false;
            AccessibilityNodeInfo.TouchDelegateInfo info = this.mTouchDelegate.getTouchDelegateInfo();
            for (int i = 0; i < info.getRegionCount(); i++) {
                Region r = info.getRegionAt(i);
                if (r.contains((int) event.getX(), (int) event.getY())) {
                    pointInDelegateRegion = true;
                }
            }
            if (!oldHoveringTouchDelegate) {
                if ((action == 9 || action == 7) && !pointInHoveredChild(event) && pointInDelegateRegion) {
                    this.mHoveringTouchDelegate = true;
                }
            } else if (action == 10 || (action == 7 && (pointInHoveredChild(event) || !pointInDelegateRegion))) {
                this.mHoveringTouchDelegate = false;
            }
            switch (action) {
                case 7:
                    if (oldHoveringTouchDelegate && this.mHoveringTouchDelegate) {
                        boolean handled = this.mTouchDelegate.onTouchExplorationHoverEvent(event);
                        return handled;
                    } else if (!oldHoveringTouchDelegate && this.mHoveringTouchDelegate) {
                        MotionEvent eventNoHistory = event.getHistorySize() == 0 ? event : MotionEvent.obtainNoHistory(event);
                        eventNoHistory.setAction(9);
                        boolean handled2 = this.mTouchDelegate.onTouchExplorationHoverEvent(eventNoHistory);
                        eventNoHistory.setAction(action);
                        return handled2 | this.mTouchDelegate.onTouchExplorationHoverEvent(eventNoHistory);
                    } else if (!oldHoveringTouchDelegate || this.mHoveringTouchDelegate) {
                        return false;
                    } else {
                        boolean hoverExitPending = event.isHoverExitPending();
                        event.setHoverExitPending(true);
                        this.mTouchDelegate.onTouchExplorationHoverEvent(event);
                        MotionEvent eventNoHistory2 = event.getHistorySize() == 0 ? event : MotionEvent.obtainNoHistory(event);
                        eventNoHistory2.setHoverExitPending(hoverExitPending);
                        eventNoHistory2.setAction(10);
                        this.mTouchDelegate.onTouchExplorationHoverEvent(eventNoHistory2);
                        return false;
                    }
                case 8:
                default:
                    return false;
                case 9:
                    if (oldHoveringTouchDelegate || !this.mHoveringTouchDelegate) {
                        return false;
                    }
                    boolean handled3 = this.mTouchDelegate.onTouchExplorationHoverEvent(event);
                    return handled3;
                case 10:
                    if (!oldHoveringTouchDelegate) {
                        return false;
                    }
                    this.mTouchDelegate.onTouchExplorationHoverEvent(event);
                    return false;
            }
        }
        return false;
    }

    public boolean onHoverEvent(MotionEvent event) {
        if (this.mTouchDelegate == null || !dispatchTouchExplorationHoverEvent(event)) {
            int action = event.getActionMasked();
            if (!this.mSendingHoverAccessibilityEvents) {
                if ((action == 9 || action == 7) && !hasHoveredChild() && pointInView(event.getX(), event.getY())) {
                    sendAccessibilityHoverEvent(128);
                    this.mSendingHoverAccessibilityEvents = true;
                }
            } else if (action == 10 || (action == 7 && !pointInView(event.getX(), event.getY()))) {
                this.mSendingHoverAccessibilityEvents = false;
                sendAccessibilityHoverEvent(256);
            }
            if ((action == 9 || action == 7) && event.isFromSource(8194) && isOnScrollbar(event.getX(), event.getY())) {
                awakenScrollBars();
            }
            if (isHoverable() || isHovered()) {
                switch (action) {
                    case 9:
                        setHovered(true);
                        break;
                    case 10:
                        setHovered(false);
                        break;
                }
                dispatchGenericMotionEventInternal(event);
                return true;
            }
            return false;
        }
        return true;
    }

    private boolean isHoverable() {
        int viewFlags = this.mViewFlags;
        if ((viewFlags & 32) == 32) {
            return false;
        }
        return (viewFlags & 16384) == 16384 || (viewFlags & 2097152) == 2097152 || (viewFlags & 8388608) == 8388608;
    }

    @ViewDebug.ExportedProperty
    public boolean isHovered() {
        return (this.mPrivateFlags & 268435456) != 0;
    }

    public void setHovered(boolean hovered) {
        if (hovered) {
            int i = this.mPrivateFlags;
            if ((i & 268435456) == 0) {
                this.mPrivateFlags = 268435456 | i;
                refreshDrawableState();
                onHoverChanged(true);
                return;
            }
            return;
        }
        int i2 = this.mPrivateFlags;
        if ((268435456 & i2) != 0) {
            this.mPrivateFlags = (-268435457) & i2;
            refreshDrawableState();
            onHoverChanged(false);
        }
    }

    public void onHoverChanged(boolean hovered) {
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x0138 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0139  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected boolean handleScrollBarDragging(MotionEvent event) {
        if (this.mScrollCache == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        if ((this.mScrollCache.mScrollBarDraggingState == 0 && action != 0) || !event.isFromSource(8194) || !event.isButtonPressed(1)) {
            this.mScrollCache.mScrollBarDraggingState = 0;
            return false;
        }
        switch (action) {
            case 0:
                if (this.mScrollCache.state != 0) {
                    return false;
                }
                if (!isOnVerticalScrollbarThumb(x, y)) {
                    if (isOnHorizontalScrollbarThumb(x, y)) {
                        this.mScrollCache.mScrollBarDraggingState = 2;
                        this.mScrollCache.mScrollBarDraggingPos = x;
                        return true;
                    }
                    this.mScrollCache.mScrollBarDraggingState = 0;
                    return false;
                }
                this.mScrollCache.mScrollBarDraggingState = 1;
                this.mScrollCache.mScrollBarDraggingPos = y;
                return true;
            case 1:
            default:
                this.mScrollCache.mScrollBarDraggingState = 0;
                return false;
            case 2:
                if (this.mScrollCache.mScrollBarDraggingState == 0) {
                    return false;
                }
                if (this.mScrollCache.mScrollBarDraggingState != 1) {
                    if (this.mScrollCache.mScrollBarDraggingState == 2) {
                        Rect bounds = this.mScrollCache.mScrollBarBounds;
                        getHorizontalScrollBarBounds(bounds, null);
                        int range = computeHorizontalScrollRange();
                        int offset = computeHorizontalScrollOffset();
                        int extent = computeHorizontalScrollExtent();
                        int thumbLength = ScrollBarUtils.getThumbLength(bounds.width(), bounds.height(), extent, range);
                        int thumbOffset = ScrollBarUtils.getThumbOffset(bounds.width(), thumbLength, extent, range, offset);
                        float diff = x - this.mScrollCache.mScrollBarDraggingPos;
                        float maxThumbOffset = bounds.width() - thumbLength;
                        float newThumbOffset = Math.min(Math.max(thumbOffset + diff, 0.0f), maxThumbOffset);
                        int width = getWidth();
                        if (Math.round(newThumbOffset) != thumbOffset && maxThumbOffset > 0.0f && width > 0 && extent > 0) {
                            int newX = Math.round(((range - extent) / (extent / width)) * (newThumbOffset / maxThumbOffset));
                            if (newX != getScrollX()) {
                                this.mScrollCache.mScrollBarDraggingPos = x;
                                setScrollX(newX);
                                return true;
                            }
                            return true;
                        }
                        return true;
                    }
                    if (this.mScrollCache.state != 0) {
                    }
                } else {
                    Rect bounds2 = this.mScrollCache.mScrollBarBounds;
                    getVerticalScrollBarBounds(bounds2, null);
                    int range2 = computeVerticalScrollRange();
                    int offset2 = computeVerticalScrollOffset();
                    int extent2 = computeVerticalScrollExtent();
                    int thumbLength2 = ScrollBarUtils.getThumbLength(bounds2.height(), bounds2.width(), extent2, range2);
                    int thumbOffset2 = ScrollBarUtils.getThumbOffset(bounds2.height(), thumbLength2, extent2, range2, offset2);
                    float diff2 = y - this.mScrollCache.mScrollBarDraggingPos;
                    float maxThumbOffset2 = bounds2.height() - thumbLength2;
                    float newThumbOffset2 = Math.min(Math.max(thumbOffset2 + diff2, 0.0f), maxThumbOffset2);
                    int height = getHeight();
                    if (Math.round(newThumbOffset2) != thumbOffset2 && maxThumbOffset2 > 0.0f && height > 0 && extent2 > 0) {
                        int newY = Math.round(((range2 - extent2) / (extent2 / height)) * (newThumbOffset2 / maxThumbOffset2));
                        if (newY != getScrollY()) {
                            this.mScrollCache.mScrollBarDraggingPos = y;
                            setScrollY(newY);
                            return true;
                        }
                        return true;
                    }
                    return true;
                }
                break;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int touchSlop;
        int motionClassification;
        int touchSlop2;
        float x = event.getX();
        float y = event.getY();
        int viewFlags = this.mViewFlags;
        int action = event.getAction();
        boolean clickable = (viewFlags & 16384) == 16384 || (viewFlags & 2097152) == 2097152 || (viewFlags & 8388608) == 8388608;
        if ((viewFlags & 32) == 32 && (this.mPrivateFlags4 & 4096) == 0) {
            if (action == 1 && (this.mPrivateFlags & 16384) != 0) {
                setPressed(false);
            }
            this.mPrivateFlags3 &= -131073;
            return clickable;
        }
        TouchDelegate touchDelegate = this.mTouchDelegate;
        if (touchDelegate != null && touchDelegate.onTouchEvent(event)) {
            return true;
        }
        if (clickable || (viewFlags & 1073741824) == 1073741824) {
            switch (action) {
                case 0:
                    if (event.getSource() == 4098) {
                        this.mPrivateFlags3 |= 131072;
                    }
                    this.mHasPerformedLongPress = false;
                    if (!clickable) {
                        checkForLongClick(ViewConfiguration.getLongPressTimeout(), x, y, 3);
                        return true;
                    } else if (!performButtonActionOnTouchDown(event)) {
                        boolean isInScrollingContainer = isInScrollingContainer();
                        if (isInScrollingContainer) {
                            this.mPrivateFlags |= 33554432;
                            if (this.mPendingCheckForTap == null) {
                                this.mPendingCheckForTap = new CheckForTap();
                            }
                            this.mPendingCheckForTap.f494x = event.getX();
                            this.mPendingCheckForTap.f495y = event.getY();
                            postDelayed(this.mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                            return true;
                        }
                        setPressed(true, x, y);
                        checkForLongClick(ViewConfiguration.getLongPressTimeout(), x, y, 3);
                        return true;
                    } else {
                        return true;
                    }
                case 1:
                    this.mPrivateFlags3 &= -131073;
                    if ((viewFlags & 1073741824) == 1073741824) {
                        handleTooltipUp();
                    }
                    if (!clickable) {
                        removeTapCallback();
                        removeLongPressCallback();
                        this.mInContextButtonPress = false;
                        this.mHasPerformedLongPress = false;
                        this.mIgnoreNextUpEvent = false;
                        return true;
                    }
                    int i = this.mPrivateFlags;
                    boolean prepressed = (33554432 & i) != 0;
                    if ((i & 16384) != 0 || prepressed) {
                        boolean focusTaken = false;
                        if (isFocusable() && isFocusableInTouchMode() && !isFocused()) {
                            focusTaken = requestFocus();
                        }
                        if (prepressed) {
                            setPressed(true, x, y);
                        }
                        if (!this.mHasPerformedLongPress && !this.mIgnoreNextUpEvent) {
                            removeLongPressCallback();
                            if (!focusTaken) {
                                if (this.mPerformClick == null) {
                                    this.mPerformClick = new PerformClick();
                                }
                                if (!post(this.mPerformClick)) {
                                    performClickInternal();
                                }
                            }
                        }
                        if (this.mUnsetPressedState == null) {
                            this.mUnsetPressedState = new UnsetPressedState();
                        }
                        if (!prepressed) {
                            if (!post(this.mUnsetPressedState)) {
                                this.mUnsetPressedState.run();
                            }
                        } else {
                            postDelayed(this.mUnsetPressedState, ViewConfiguration.getPressedStateDuration());
                        }
                        removeTapCallback();
                    }
                    this.mIgnoreNextUpEvent = false;
                    return true;
                case 2:
                    if (clickable) {
                        drawableHotspotChanged(x, y);
                    }
                    int motionClassification2 = event.getClassification();
                    boolean ambiguousGesture = motionClassification2 == 1;
                    int touchSlop3 = this.mTouchSlop;
                    if (ambiguousGesture && hasPendingLongPressCallback()) {
                        if (pointInView(x, y, touchSlop3)) {
                            touchSlop2 = touchSlop3;
                            motionClassification = motionClassification2;
                        } else {
                            removeLongPressCallback();
                            long delay = ViewConfiguration.getLongPressTimeout() * this.mAmbiguousGestureMultiplier;
                            touchSlop2 = touchSlop3;
                            motionClassification = motionClassification2;
                            checkForLongClick(delay - (event.getEventTime() - event.getDownTime()), x, y, 3);
                        }
                        touchSlop = (int) (touchSlop2 * this.mAmbiguousGestureMultiplier);
                    } else {
                        touchSlop = touchSlop3;
                        motionClassification = motionClassification2;
                    }
                    if (!pointInView(x, y, touchSlop)) {
                        removeTapCallback();
                        removeLongPressCallback();
                        if ((this.mPrivateFlags & 16384) != 0) {
                            setPressed(false);
                        }
                        this.mPrivateFlags3 &= -131073;
                    }
                    boolean deepPress = motionClassification == 2;
                    if (deepPress && hasPendingLongPressCallback()) {
                        removeLongPressCallback();
                        checkForLongClick(0L, x, y, 4);
                        return true;
                    }
                    return true;
                case 3:
                    if (clickable) {
                        setPressed(false);
                    }
                    removeTapCallback();
                    removeLongPressCallback();
                    this.mInContextButtonPress = false;
                    this.mHasPerformedLongPress = false;
                    this.mIgnoreNextUpEvent = false;
                    this.mPrivateFlags3 &= -131073;
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    public boolean isInScrollingContainer() {
        for (ViewParent p = getParent(); p != null && (p instanceof ViewGroup); p = p.getParent()) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
        }
        return false;
    }

    private void removeLongPressCallback() {
        CheckForLongPress checkForLongPress = this.mPendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
    }

    private boolean hasPendingLongPressCallback() {
        AttachInfo attachInfo;
        if (this.mPendingCheckForLongPress == null || (attachInfo = this.mAttachInfo) == null) {
            return false;
        }
        return attachInfo.mHandler.hasCallbacks(this.mPendingCheckForLongPress);
    }

    private void removePerformClickCallback() {
        PerformClick performClick = this.mPerformClick;
        if (performClick != null) {
            removeCallbacks(performClick);
        }
    }

    private void removeUnsetPressCallback() {
        if ((this.mPrivateFlags & 16384) != 0 && this.mUnsetPressedState != null) {
            setPressed(false);
            removeCallbacks(this.mUnsetPressedState);
        }
    }

    private void removeTapCallback() {
        CheckForTap checkForTap = this.mPendingCheckForTap;
        if (checkForTap != null) {
            this.mPrivateFlags &= -33554433;
            removeCallbacks(checkForTap);
        }
    }

    public void cancelLongPress() {
        removeLongPressCallback();
        removeTapCallback();
    }

    public void setTouchDelegate(TouchDelegate delegate) {
        this.mTouchDelegate = delegate;
    }

    public TouchDelegate getTouchDelegate() {
        return this.mTouchDelegate;
    }

    public final void requestUnbufferedDispatch(MotionEvent event) {
        int action = event.getAction();
        if (this.mAttachInfo != null) {
            if ((action != 0 && action != 2) || !event.isTouchEvent()) {
                return;
            }
            this.mAttachInfo.mUnbufferedDispatchRequested = true;
        }
    }

    public final void requestUnbufferedDispatch(int source) {
        if (this.mUnbufferedInputSource == source) {
            return;
        }
        this.mUnbufferedInputSource = source;
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.onDescendantUnbufferedRequested();
        }
    }

    private boolean hasSize() {
        return this.mBottom > this.mTop && this.mRight > this.mLeft;
    }

    private boolean canTakeFocus() {
        int i = this.mViewFlags;
        return (i & 12) == 0 && (i & 1) == 1 && (i & 32) == 0 && (sCanFocusZeroSized || !isLayoutValid() || hasSize());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlags(int flags, int mask) {
        AttachInfo attachInfo;
        ForegroundInfo foregroundInfo;
        ViewParent viewParent;
        int newFocus;
        boolean accessibilityEnabled = AccessibilityManager.getInstance(this.mContext).isEnabled();
        boolean oldIncludeForAccessibility = accessibilityEnabled && includeForAccessibility(false);
        int old = this.mViewFlags;
        int i = (this.mViewFlags & (~mask)) | (flags & mask);
        this.mViewFlags = i;
        int changed = i ^ old;
        if (changed == 0) {
            return;
        }
        int privateFlags = this.mPrivateFlags;
        boolean shouldNotifyFocusableAvailable = false;
        int focusableChangedByAuto = 0;
        if ((i & 16) != 0 && (changed & BatteryStats.HistoryItem.EVENT_TEMP_WHITELIST_FINISH) != 0) {
            if ((i & 16384) != 0) {
                newFocus = 1;
            } else {
                newFocus = 0;
            }
            this.mViewFlags = (i & (-2)) | newFocus;
            focusableChangedByAuto = (old & 1) ^ (newFocus & 1);
            changed = (changed & (-2)) | focusableChangedByAuto;
        }
        if ((changed & 1) != 0 && (privateFlags & 16) != 0) {
            if ((old & 1) == 1 && (privateFlags & 2) != 0) {
                clearFocus();
                ViewParent viewParent2 = this.mParent;
                if (viewParent2 instanceof ViewGroup) {
                    ((ViewGroup) viewParent2).clearFocusedInCluster();
                }
            } else if ((old & 1) == 0 && (privateFlags & 2) == 0 && this.mParent != null) {
                ViewRootImpl viewRootImpl = getViewRootImpl();
                if (!sAutoFocusableOffUIThreadWontNotifyParents || focusableChangedByAuto == 0 || viewRootImpl == null || viewRootImpl.mThread == Thread.currentThread()) {
                    shouldNotifyFocusableAvailable = canTakeFocus();
                }
            }
        }
        int newVisibility = flags & 12;
        if (newVisibility == 0 && (changed & 12) != 0) {
            this.mPrivateFlags |= 32;
            invalidate(true);
            needGlobalAttributesUpdate(true);
            shouldNotifyFocusableAvailable = hasSize();
        }
        if ((changed & 32) != 0) {
            if ((this.mViewFlags & 32) == 0) {
                shouldNotifyFocusableAvailable = canTakeFocus();
            } else if (isFocused()) {
                clearFocus();
            }
        }
        if (shouldNotifyFocusableAvailable && (viewParent = this.mParent) != null) {
            viewParent.focusableViewAvailable(this);
        }
        if ((changed & 8) != 0) {
            needGlobalAttributesUpdate(false);
            requestLayout();
            if ((this.mViewFlags & 12) == 8) {
                if (hasFocus()) {
                    clearFocus();
                    ViewParent viewParent3 = this.mParent;
                    if (viewParent3 instanceof ViewGroup) {
                        ((ViewGroup) viewParent3).clearFocusedInCluster();
                    }
                }
                clearAccessibilityFocus();
                destroyDrawingCache();
                ViewParent viewParent4 = this.mParent;
                if (viewParent4 instanceof View) {
                    ((View) viewParent4).invalidate(true);
                }
                this.mPrivateFlags |= 32;
            }
            AttachInfo attachInfo2 = this.mAttachInfo;
            if (attachInfo2 != null) {
                attachInfo2.mViewVisibilityChanged = true;
            }
        }
        if ((changed & 4) != 0) {
            needGlobalAttributesUpdate(false);
            this.mPrivateFlags |= 32;
            if ((this.mViewFlags & 12) == 4 && getRootView() != this) {
                if (hasFocus()) {
                    clearFocus();
                    ViewParent viewParent5 = this.mParent;
                    if (viewParent5 instanceof ViewGroup) {
                        ((ViewGroup) viewParent5).clearFocusedInCluster();
                    }
                }
                clearAccessibilityFocus();
            }
            AttachInfo attachInfo3 = this.mAttachInfo;
            if (attachInfo3 != null) {
                attachInfo3.mViewVisibilityChanged = true;
            }
        }
        if ((changed & 12) != 0) {
            if (newVisibility != 0 && this.mAttachInfo != null) {
                cleanupDraw();
            }
            ViewParent viewParent6 = this.mParent;
            if (viewParent6 instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) viewParent6;
                parent.onChildVisibilityChanged(this, changed & 12, newVisibility);
                parent.invalidate(true);
            } else if (viewParent6 != null) {
                viewParent6.invalidateChild(this, null);
            }
            if (this.mAttachInfo != null) {
                dispatchVisibilityChanged(this, newVisibility);
                if (this.mParent != null && getWindowVisibility() == 0) {
                    ViewParent viewParent7 = this.mParent;
                    if (!(viewParent7 instanceof ViewGroup) || ((ViewGroup) viewParent7).isShown()) {
                        dispatchVisibilityAggregated(newVisibility == 0);
                    }
                }
                if ((old & 12) == 0) {
                    notifySubtreeAccessibilityStateChangedByParentIfNeeded();
                } else {
                    notifySubtreeAccessibilityStateChangedIfNeeded();
                }
            }
        }
        if ((131072 & changed) != 0) {
            destroyDrawingCache();
        }
        if ((32768 & changed) != 0) {
            destroyDrawingCache();
            this.mPrivateFlags &= -32769;
            invalidateParentCaches();
        }
        if ((1572864 & changed) != 0) {
            destroyDrawingCache();
            this.mPrivateFlags &= -32769;
        }
        if ((changed & 128) != 0) {
            if ((this.mViewFlags & 128) != 0) {
                if (this.mBackground != null || this.mDefaultFocusHighlight != null || ((foregroundInfo = this.mForegroundInfo) != null && foregroundInfo.mDrawable != null)) {
                    this.mPrivateFlags &= PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
                } else {
                    this.mPrivateFlags |= 128;
                }
            } else {
                this.mPrivateFlags &= PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
            }
            requestLayout();
            invalidate(true);
        }
        if ((67108864 & changed) != 0 && this.mParent != null && (attachInfo = this.mAttachInfo) != null && !attachInfo.mRecomputeGlobalAttributes) {
            this.mParent.recomputeViewAttributes(this);
        }
        if (accessibilityEnabled) {
            if (isAccessibilityPane()) {
                changed &= -13;
            }
            if ((changed & 1) != 0 || (changed & 12) != 0 || (changed & 16384) != 0 || (2097152 & changed) != 0 || (8388608 & changed) != 0) {
                if (oldIncludeForAccessibility != includeForAccessibility(false)) {
                    notifySubtreeAccessibilityStateChangedIfNeeded();
                } else {
                    notifyViewAccessibilityStateChangedIfNeeded(0);
                }
            } else if ((changed & 32) != 0) {
                notifyViewAccessibilityStateChangedIfNeeded(0);
            }
        }
    }

    public void bringToFront() {
        ViewParent viewParent = this.mParent;
        if (viewParent != null) {
            viewParent.bringChildToFront(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        notifySubtreeAccessibilityStateChangedIfNeeded();
        postSendViewScrolledAccessibilityEventCallback(l - oldl, t - oldt);
        this.mBackgroundSizeChanged = true;
        this.mDefaultFocusHighlightSizeChanged = true;
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        if (foregroundInfo != null) {
            foregroundInfo.mBoundsChanged = true;
        }
        AttachInfo ai = this.mAttachInfo;
        if (ai != null) {
            ai.mViewScrollChanged = true;
        }
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null && listenerInfo.mOnScrollChangeListener != null) {
            this.mListenerInfo.mOnScrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
    }

    public final ViewParent getParent() {
        return this.mParent;
    }

    public void setScrollX(int value) {
        scrollTo(value, this.mScrollY);
    }

    public void setScrollY(int value) {
        scrollTo(this.mScrollX, value);
    }

    public final int getScrollX() {
        return this.mScrollX;
    }

    public final int getScrollY() {
        return this.mScrollY;
    }

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    public final int getWidth() {
        return this.mRight - this.mLeft;
    }

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    public final int getHeight() {
        return this.mBottom - this.mTop;
    }

    public void getDrawingRect(Rect outRect) {
        outRect.left = this.mScrollX;
        outRect.top = this.mScrollY;
        outRect.right = this.mScrollX + (this.mRight - this.mLeft);
        outRect.bottom = this.mScrollY + (this.mBottom - this.mTop);
    }

    public final int getMeasuredWidth() {
        return this.mMeasuredWidth & 16777215;
    }

    @ViewDebug.ExportedProperty(category = "measurement", flagMapping = {@ViewDebug.FlagToString(equals = 16777216, mask = -16777216, name = "MEASURED_STATE_TOO_SMALL")})
    public final int getMeasuredWidthAndState() {
        return this.mMeasuredWidth;
    }

    public final int getMeasuredHeight() {
        return this.mMeasuredHeight & 16777215;
    }

    @ViewDebug.ExportedProperty(category = "measurement", flagMapping = {@ViewDebug.FlagToString(equals = 16777216, mask = -16777216, name = "MEASURED_STATE_TOO_SMALL")})
    public final int getMeasuredHeightAndState() {
        return this.mMeasuredHeight;
    }

    public final int getMeasuredState() {
        return (this.mMeasuredWidth & (-16777216)) | ((this.mMeasuredHeight >> 16) & (-256));
    }

    public Matrix getMatrix() {
        ensureTransformationInfo();
        Matrix matrix = this.mTransformationInfo.mMatrix;
        this.mRenderNode.getMatrix(matrix);
        return matrix;
    }

    public final boolean hasIdentityMatrix() {
        return this.mRenderNode.hasIdentityMatrix();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void ensureTransformationInfo() {
        if (this.mTransformationInfo == null) {
            this.mTransformationInfo = new TransformationInfo();
        }
    }

    public final Matrix getInverseMatrix() {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mInverseMatrix == null) {
            this.mTransformationInfo.mInverseMatrix = new Matrix();
        }
        Matrix matrix = this.mTransformationInfo.mInverseMatrix;
        this.mRenderNode.getInverseMatrix(matrix);
        return matrix;
    }

    public float getCameraDistance() {
        float dpi = this.mResources.getDisplayMetrics().densityDpi;
        return this.mRenderNode.getCameraDistance() * dpi;
    }

    public void setCameraDistance(float distance) {
        float dpi = this.mResources.getDisplayMetrics().densityDpi;
        invalidateViewProperty(true, false);
        this.mRenderNode.setCameraDistance(Math.abs(distance) / dpi);
        invalidateViewProperty(false, false);
        invalidateParentIfNeededAndWasQuickRejected();
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getRotation() {
        return this.mRenderNode.getRotationZ();
    }

    @RemotableViewMethod
    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            invalidateViewProperty(true, false);
            this.mRenderNode.setRotationZ(rotation);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getRotationY() {
        return this.mRenderNode.getRotationY();
    }

    @RemotableViewMethod
    public void setRotationY(float rotationY) {
        if (rotationY != getRotationY()) {
            invalidateViewProperty(true, false);
            this.mRenderNode.setRotationY(rotationY);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getRotationX() {
        return this.mRenderNode.getRotationX();
    }

    @RemotableViewMethod
    public void setRotationX(float rotationX) {
        if (rotationX != getRotationX()) {
            invalidateViewProperty(true, false);
            this.mRenderNode.setRotationX(rotationX);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getScaleX() {
        return this.mRenderNode.getScaleX();
    }

    @RemotableViewMethod
    public void setScaleX(float scaleX) {
        if (scaleX != getScaleX()) {
            float scaleX2 = sanitizeFloatPropertyValue(scaleX, "scaleX");
            invalidateViewProperty(true, false);
            this.mRenderNode.setScaleX(scaleX2);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getScaleY() {
        return this.mRenderNode.getScaleY();
    }

    @RemotableViewMethod
    public void setScaleY(float scaleY) {
        if (scaleY != getScaleY()) {
            float scaleY2 = sanitizeFloatPropertyValue(scaleY, "scaleY");
            invalidateViewProperty(true, false);
            this.mRenderNode.setScaleY(scaleY2);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getPivotX() {
        return this.mRenderNode.getPivotX();
    }

    @RemotableViewMethod
    public void setPivotX(float pivotX) {
        if (!this.mRenderNode.isPivotExplicitlySet() || pivotX != getPivotX()) {
            invalidateViewProperty(true, false);
            this.mRenderNode.setPivotX(pivotX);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getPivotY() {
        return this.mRenderNode.getPivotY();
    }

    @RemotableViewMethod
    public void setPivotY(float pivotY) {
        if (!this.mRenderNode.isPivotExplicitlySet() || pivotY != getPivotY()) {
            invalidateViewProperty(true, false);
            this.mRenderNode.setPivotY(pivotY);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
        }
    }

    public boolean isPivotSet() {
        return this.mRenderNode.isPivotExplicitlySet();
    }

    public void resetPivot() {
        if (this.mRenderNode.resetPivot()) {
            invalidateViewProperty(false, false);
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getAlpha() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mAlpha;
        }
        return 1.0f;
    }

    public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
        int i = this.mPrivateFlags3 | 16777216;
        this.mPrivateFlags3 = i;
        if (hasOverlappingRendering) {
            this.mPrivateFlags3 = i | 8388608;
        } else {
            this.mPrivateFlags3 = i & (-8388609);
        }
    }

    public final boolean getHasOverlappingRendering() {
        int i = this.mPrivateFlags3;
        if ((16777216 & i) != 0) {
            return (i & 8388608) != 0;
        }
        return hasOverlappingRendering();
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean hasOverlappingRendering() {
        return true;
    }

    @RemotableViewMethod
    public void setAlpha(float alpha) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mAlpha != alpha) {
            setAlphaInternal(alpha);
            if (onSetAlpha((int) (255.0f * alpha))) {
                this.mPrivateFlags |= 262144;
                invalidateParentCaches();
                invalidate(true);
                return;
            }
            this.mPrivateFlags &= -262145;
            invalidateViewProperty(true, false);
            this.mRenderNode.setAlpha(getFinalAlpha());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setAlphaNoInvalidation(float alpha) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mAlpha != alpha) {
            setAlphaInternal(alpha);
            boolean subclassHandlesAlpha = onSetAlpha((int) (255.0f * alpha));
            if (subclassHandlesAlpha) {
                this.mPrivateFlags |= 262144;
                return true;
            }
            this.mPrivateFlags &= -262145;
            this.mRenderNode.setAlpha(getFinalAlpha());
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAlphaInternal(float alpha) {
        float oldAlpha = this.mTransformationInfo.mAlpha;
        this.mTransformationInfo.mAlpha = alpha;
        if ((alpha == 0.0f) ^ (oldAlpha == 0.0f)) {
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    public void setTransitionAlpha(float alpha) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mTransitionAlpha != alpha) {
            this.mTransformationInfo.mTransitionAlpha = alpha;
            this.mPrivateFlags &= -262145;
            invalidateViewProperty(true, false);
            this.mRenderNode.setAlpha(getFinalAlpha());
        }
    }

    private float getFinalAlpha() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mAlpha * this.mTransformationInfo.mTransitionAlpha;
        }
        return 1.0f;
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getTransitionAlpha() {
        TransformationInfo transformationInfo = this.mTransformationInfo;
        if (transformationInfo != null) {
            return transformationInfo.mTransitionAlpha;
        }
        return 1.0f;
    }

    public void setForceDarkAllowed(boolean allow) {
        if (this.mRenderNode.setForceDarkAllowed(allow)) {
            invalidate();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean isForceDarkAllowed() {
        return this.mRenderNode.isForceDarkAllowed();
    }

    @ViewDebug.CapturedViewProperty
    public final int getTop() {
        return this.mTop;
    }

    public final void setTop(int top) {
        int minTop;
        int yLoc;
        if (top != this.mTop) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (matrixIsIdentity) {
                if (this.mAttachInfo != null) {
                    int i = this.mTop;
                    if (top < i) {
                        minTop = top;
                        yLoc = top - i;
                    } else {
                        minTop = this.mTop;
                        yLoc = 0;
                    }
                    invalidate(0, yLoc, this.mRight - this.mLeft, this.mBottom - minTop);
                }
            } else {
                invalidate(true);
            }
            int width = this.mRight - this.mLeft;
            int oldHeight = this.mBottom - this.mTop;
            this.mTop = top;
            this.mRenderNode.setTop(top);
            sizeChange(width, this.mBottom - this.mTop, width, oldHeight);
            if (!matrixIsIdentity) {
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            this.mDefaultFocusHighlightSizeChanged = true;
            ForegroundInfo foregroundInfo = this.mForegroundInfo;
            if (foregroundInfo != null) {
                foregroundInfo.mBoundsChanged = true;
            }
            invalidateParentIfNeeded();
        }
    }

    @ViewDebug.CapturedViewProperty
    public final int getBottom() {
        return this.mBottom;
    }

    public boolean isDirty() {
        return (this.mPrivateFlags & 2097152) != 0;
    }

    public final void setBottom(int bottom) {
        int maxBottom;
        if (bottom != this.mBottom) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (matrixIsIdentity) {
                if (this.mAttachInfo != null) {
                    if (bottom < this.mBottom) {
                        maxBottom = this.mBottom;
                    } else {
                        maxBottom = bottom;
                    }
                    invalidate(0, 0, this.mRight - this.mLeft, maxBottom - this.mTop);
                }
            } else {
                invalidate(true);
            }
            int width = this.mRight - this.mLeft;
            int oldHeight = this.mBottom - this.mTop;
            this.mBottom = bottom;
            this.mRenderNode.setBottom(bottom);
            sizeChange(width, this.mBottom - this.mTop, width, oldHeight);
            if (!matrixIsIdentity) {
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            this.mDefaultFocusHighlightSizeChanged = true;
            ForegroundInfo foregroundInfo = this.mForegroundInfo;
            if (foregroundInfo != null) {
                foregroundInfo.mBoundsChanged = true;
            }
            invalidateParentIfNeeded();
        }
    }

    @ViewDebug.CapturedViewProperty
    public final int getLeft() {
        return this.mLeft;
    }

    public final void setLeft(int left) {
        int minLeft;
        int xLoc;
        if (left != this.mLeft) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (matrixIsIdentity) {
                if (this.mAttachInfo != null) {
                    int i = this.mLeft;
                    if (left < i) {
                        minLeft = left;
                        xLoc = left - i;
                    } else {
                        minLeft = this.mLeft;
                        xLoc = 0;
                    }
                    invalidate(xLoc, 0, this.mRight - minLeft, this.mBottom - this.mTop);
                }
            } else {
                invalidate(true);
            }
            int oldWidth = this.mRight - this.mLeft;
            int height = this.mBottom - this.mTop;
            this.mLeft = left;
            this.mRenderNode.setLeft(left);
            sizeChange(this.mRight - this.mLeft, height, oldWidth, height);
            if (!matrixIsIdentity) {
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            this.mDefaultFocusHighlightSizeChanged = true;
            ForegroundInfo foregroundInfo = this.mForegroundInfo;
            if (foregroundInfo != null) {
                foregroundInfo.mBoundsChanged = true;
            }
            invalidateParentIfNeeded();
        }
    }

    @ViewDebug.CapturedViewProperty
    public final int getRight() {
        return this.mRight;
    }

    public final void setRight(int right) {
        int maxRight;
        if (right != this.mRight) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (matrixIsIdentity) {
                if (this.mAttachInfo != null) {
                    if (right < this.mRight) {
                        maxRight = this.mRight;
                    } else {
                        maxRight = right;
                    }
                    invalidate(0, 0, maxRight - this.mLeft, this.mBottom - this.mTop);
                }
            } else {
                invalidate(true);
            }
            int oldWidth = this.mRight - this.mLeft;
            int height = this.mBottom - this.mTop;
            this.mRight = right;
            this.mRenderNode.setRight(right);
            sizeChange(this.mRight - this.mLeft, height, oldWidth, height);
            if (!matrixIsIdentity) {
                this.mPrivateFlags |= 32;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            this.mDefaultFocusHighlightSizeChanged = true;
            ForegroundInfo foregroundInfo = this.mForegroundInfo;
            if (foregroundInfo != null) {
                foregroundInfo.mBoundsChanged = true;
            }
            invalidateParentIfNeeded();
        }
    }

    private static float sanitizeFloatPropertyValue(float value, String propertyName) {
        return sanitizeFloatPropertyValue(value, propertyName, -3.4028235E38f, Float.MAX_VALUE);
    }

    private static float sanitizeFloatPropertyValue(float value, String propertyName, float min, float max) {
        if (value < min || value > max) {
            if (value < min || value == Float.NEGATIVE_INFINITY) {
                if (sThrowOnInvalidFloatProperties) {
                    throw new IllegalArgumentException("Cannot set '" + propertyName + "' to " + value + ", the value must be >= " + min);
                }
                return min;
            } else if (value > max || value == Float.POSITIVE_INFINITY) {
                if (sThrowOnInvalidFloatProperties) {
                    throw new IllegalArgumentException("Cannot set '" + propertyName + "' to " + value + ", the value must be <= " + max);
                }
                return max;
            } else if (Float.isNaN(value)) {
                if (sThrowOnInvalidFloatProperties) {
                    throw new IllegalArgumentException("Cannot set '" + propertyName + "' to Float.NaN");
                }
                return 0.0f;
            } else {
                throw new IllegalStateException("How do you get here?? " + value);
            }
        }
        return value;
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getX() {
        return this.mLeft + getTranslationX();
    }

    public void setX(float x) {
        setTranslationX(x - this.mLeft);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getY() {
        return this.mTop + getTranslationY();
    }

    public void setY(float y) {
        setTranslationY(y - this.mTop);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getZ() {
        return getElevation() + getTranslationZ();
    }

    public void setZ(float z) {
        setTranslationZ(z - getElevation());
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getElevation() {
        return this.mRenderNode.getElevation();
    }

    @RemotableViewMethod
    public void setElevation(float elevation) {
        if (elevation != getElevation()) {
            float elevation2 = sanitizeFloatPropertyValue(elevation, "elevation");
            invalidateViewProperty(true, false);
            this.mRenderNode.setElevation(elevation2);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getTranslationX() {
        return this.mRenderNode.getTranslationX();
    }

    @RemotableViewMethod
    public void setTranslationX(float translationX) {
        if (translationX != getTranslationX()) {
            invalidateViewProperty(true, false);
            this.mRenderNode.setTranslationX(translationX);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getTranslationY() {
        return this.mRenderNode.getTranslationY();
    }

    @RemotableViewMethod
    public void setTranslationY(float translationY) {
        if (translationY != getTranslationY()) {
            invalidateViewProperty(true, false);
            this.mRenderNode.setTranslationY(translationY);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public float getTranslationZ() {
        return this.mRenderNode.getTranslationZ();
    }

    @RemotableViewMethod
    public void setTranslationZ(float translationZ) {
        if (translationZ != getTranslationZ()) {
            float translationZ2 = sanitizeFloatPropertyValue(translationZ, "translationZ");
            invalidateViewProperty(true, false);
            this.mRenderNode.setTranslationZ(translationZ2);
            invalidateViewProperty(false, true);
            invalidateParentIfNeededAndWasQuickRejected();
        }
    }

    public void setAnimationMatrix(Matrix matrix) {
        invalidateViewProperty(true, false);
        this.mRenderNode.setAnimationMatrix(matrix);
        invalidateViewProperty(false, true);
        invalidateParentIfNeededAndWasQuickRejected();
    }

    public Matrix getAnimationMatrix() {
        return this.mRenderNode.getAnimationMatrix();
    }

    public StateListAnimator getStateListAnimator() {
        return this.mStateListAnimator;
    }

    public void setStateListAnimator(StateListAnimator stateListAnimator) {
        StateListAnimator stateListAnimator2 = this.mStateListAnimator;
        if (stateListAnimator2 == stateListAnimator) {
            return;
        }
        if (stateListAnimator2 != null) {
            stateListAnimator2.setTarget(null);
        }
        this.mStateListAnimator = stateListAnimator;
        if (stateListAnimator != null) {
            stateListAnimator.setTarget(this);
            if (isAttachedToWindow()) {
                stateListAnimator.setState(getDrawableState());
            }
        }
    }

    public final boolean getClipToOutline() {
        return this.mRenderNode.getClipToOutline();
    }

    @RemotableViewMethod
    public void setClipToOutline(boolean clipToOutline) {
        damageInParent();
        if (getClipToOutline() != clipToOutline) {
            this.mRenderNode.setClipToOutline(clipToOutline);
        }
    }

    private void setOutlineProviderFromAttribute(int providerInt) {
        switch (providerInt) {
            case 0:
                setOutlineProvider(ViewOutlineProvider.BACKGROUND);
                return;
            case 1:
                setOutlineProvider(null);
                return;
            case 2:
                setOutlineProvider(ViewOutlineProvider.BOUNDS);
                return;
            case 3:
                setOutlineProvider(ViewOutlineProvider.PADDED_BOUNDS);
                return;
            default:
                return;
        }
    }

    public void setOutlineProvider(ViewOutlineProvider provider) {
        if (this.mOutlineProvider != provider) {
            this.mOutlineProvider = provider;
            invalidateOutline();
        }
    }

    public ViewOutlineProvider getOutlineProvider() {
        return this.mOutlineProvider;
    }

    public void invalidateOutline() {
        rebuildOutline();
        notifySubtreeAccessibilityStateChangedIfNeeded();
        invalidateViewProperty(false, false);
    }

    private void rebuildOutline() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return;
        }
        if (this.mOutlineProvider == null) {
            this.mRenderNode.setOutline(null);
            return;
        }
        Outline outline = attachInfo.mTmpOutline;
        outline.setEmpty();
        outline.setAlpha(1.0f);
        this.mOutlineProvider.getOutline(this, outline);
        this.mRenderNode.setOutline(outline);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean hasShadow() {
        return this.mRenderNode.hasShadow();
    }

    public void setOutlineSpotShadowColor(int color) {
        if (this.mRenderNode.setSpotShadowColor(color)) {
            invalidateViewProperty(true, true);
        }
    }

    public int getOutlineSpotShadowColor() {
        return this.mRenderNode.getSpotShadowColor();
    }

    public void setOutlineAmbientShadowColor(int color) {
        if (this.mRenderNode.setAmbientShadowColor(color)) {
            invalidateViewProperty(true, true);
        }
    }

    public int getOutlineAmbientShadowColor() {
        return this.mRenderNode.getAmbientShadowColor();
    }

    public void setRevealClip(boolean shouldClip, float x, float y, float radius) {
        this.mRenderNode.setRevealClip(shouldClip, x, y, radius);
        invalidateViewProperty(false, false);
    }

    public void getHitRect(Rect outRect) {
        AttachInfo attachInfo;
        if (hasIdentityMatrix() || (attachInfo = this.mAttachInfo) == null) {
            outRect.set(this.mLeft, this.mTop, this.mRight, this.mBottom);
            return;
        }
        RectF tmpRect = attachInfo.mTmpTransformRect;
        tmpRect.set(0.0f, 0.0f, getWidth(), getHeight());
        getMatrix().mapRect(tmpRect);
        outRect.set(((int) tmpRect.left) + this.mLeft, ((int) tmpRect.top) + this.mTop, ((int) tmpRect.right) + this.mLeft, ((int) tmpRect.bottom) + this.mTop);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean pointInView(float localX, float localY) {
        return pointInView(localX, localY, 0.0f);
    }

    public boolean pointInView(float localX, float localY, float slop) {
        return localX >= (-slop) && localY >= (-slop) && localX < ((float) (this.mRight - this.mLeft)) + slop && localY < ((float) (this.mBottom - this.mTop)) + slop;
    }

    public void getFocusedRect(Rect r) {
        getDrawingRect(r);
    }

    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        if (width <= 0 || height <= 0) {
            return false;
        }
        r.set(0, 0, width, height);
        if (globalOffset != null) {
            globalOffset.set(-this.mScrollX, -this.mScrollY);
        }
        ViewParent viewParent = this.mParent;
        return viewParent == null || viewParent.getChildVisibleRect(this, r, globalOffset);
    }

    public final boolean getGlobalVisibleRect(Rect r) {
        return getGlobalVisibleRect(r, null);
    }

    public final boolean getLocalVisibleRect(Rect r) {
        AttachInfo attachInfo = this.mAttachInfo;
        Point offset = attachInfo != null ? attachInfo.mPoint : new Point();
        if (getGlobalVisibleRect(r, offset)) {
            r.offset(-offset.f76x, -offset.f77y);
            return true;
        }
        return false;
    }

    public void offsetTopAndBottom(int offset) {
        AttachInfo attachInfo;
        int minTop;
        int maxBottom;
        int yLoc;
        if (offset != 0) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (matrixIsIdentity) {
                if (isHardwareAccelerated()) {
                    invalidateViewProperty(false, false);
                } else {
                    ViewParent p = this.mParent;
                    if (p != null && (attachInfo = this.mAttachInfo) != null) {
                        Rect r = attachInfo.mTmpInvalRect;
                        if (offset < 0) {
                            minTop = this.mTop + offset;
                            maxBottom = this.mBottom;
                            yLoc = offset;
                        } else {
                            minTop = this.mTop;
                            maxBottom = this.mBottom + offset;
                            yLoc = 0;
                        }
                        r.set(0, yLoc, this.mRight - this.mLeft, maxBottom - minTop);
                        p.invalidateChild(this, r);
                    }
                }
            } else {
                invalidateViewProperty(false, false);
            }
            this.mTop += offset;
            this.mBottom += offset;
            this.mRenderNode.offsetTopAndBottom(offset);
            if (isHardwareAccelerated()) {
                invalidateViewProperty(false, false);
                invalidateParentIfNeededAndWasQuickRejected();
            } else {
                if (!matrixIsIdentity) {
                    invalidateViewProperty(false, true);
                }
                invalidateParentIfNeeded();
            }
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    public void offsetLeftAndRight(int offset) {
        AttachInfo attachInfo;
        int minLeft;
        int maxRight;
        if (offset != 0) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (matrixIsIdentity) {
                if (isHardwareAccelerated()) {
                    invalidateViewProperty(false, false);
                } else {
                    ViewParent p = this.mParent;
                    if (p != null && (attachInfo = this.mAttachInfo) != null) {
                        Rect r = attachInfo.mTmpInvalRect;
                        if (offset < 0) {
                            minLeft = this.mLeft + offset;
                            maxRight = this.mRight;
                        } else {
                            minLeft = this.mLeft;
                            maxRight = this.mRight + offset;
                        }
                        r.set(0, 0, maxRight - minLeft, this.mBottom - this.mTop);
                        p.invalidateChild(this, r);
                    }
                }
            } else {
                invalidateViewProperty(false, false);
            }
            this.mLeft += offset;
            this.mRight += offset;
            this.mRenderNode.offsetLeftAndRight(offset);
            if (isHardwareAccelerated()) {
                invalidateViewProperty(false, false);
                invalidateParentIfNeededAndWasQuickRejected();
            } else {
                if (!matrixIsIdentity) {
                    invalidateViewProperty(false, true);
                }
                invalidateParentIfNeeded();
            }
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(deepExport = true, prefix = "layout_")
    public ViewGroup.LayoutParams getLayoutParams() {
        return this.mLayoutParams;
    }

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params == null) {
            throw new NullPointerException("Layout parameters cannot be null");
        }
        this.mLayoutParams = params;
        resolveLayoutParams();
        ViewParent viewParent = this.mParent;
        if (viewParent instanceof ViewGroup) {
            ((ViewGroup) viewParent).onSetLayoutParams(this, params);
        }
        requestLayout();
    }

    public void resolveLayoutParams() {
        ViewGroup.LayoutParams layoutParams = this.mLayoutParams;
        if (layoutParams != null) {
            layoutParams.resolveLayoutDirection(getLayoutDirection());
        }
    }

    public void scrollTo(int x, int y) {
        if (this.mScrollX != x || this.mScrollY != y) {
            int oldX = this.mScrollX;
            int oldY = this.mScrollY;
            this.mScrollX = x;
            this.mScrollY = y;
            invalidateParentCaches();
            onScrollChanged(this.mScrollX, this.mScrollY, oldX, oldY);
            if (!awakenScrollBars()) {
                postInvalidateOnAnimation();
            }
        }
    }

    public void scrollBy(int x, int y) {
        scrollTo(this.mScrollX + x, this.mScrollY + y);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean awakenScrollBars() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache != null && awakenScrollBars(scrollabilityCache.scrollBarDefaultDelayBeforeFade, true);
    }

    private boolean initialAwakenScrollBars() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache != null && awakenScrollBars(scrollabilityCache.scrollBarDefaultDelayBeforeFade * 4, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean awakenScrollBars(int startDelay) {
        return awakenScrollBars(startDelay, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean awakenScrollBars(int startDelay, boolean invalidate) {
        ScrollabilityCache scrollCache = this.mScrollCache;
        if (scrollCache == null || !scrollCache.fadeScrollBars) {
            return false;
        }
        if (scrollCache.scrollBar == null) {
            scrollCache.scrollBar = new ScrollBarDrawable();
            scrollCache.scrollBar.setState(getDrawableState());
            scrollCache.scrollBar.setCallback(this);
        }
        if (!isHorizontalScrollBarEnabled() && !isVerticalScrollBarEnabled()) {
            return false;
        }
        if (invalidate) {
            postInvalidateOnAnimation();
        }
        if (scrollCache.state == 0) {
            startDelay = Math.max(750, startDelay);
        }
        long fadeStartTime = AnimationUtils.currentAnimationTimeMillis() + startDelay;
        scrollCache.fadeStartTime = fadeStartTime;
        scrollCache.state = 1;
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mHandler.removeCallbacks(scrollCache);
            this.mAttachInfo.mHandler.postAtTime(scrollCache, fadeStartTime);
        }
        return true;
    }

    private boolean skipInvalidate() {
        if ((this.mViewFlags & 12) != 0 && this.mCurrentAnimation == null) {
            ViewParent viewParent = this.mParent;
            if (!(viewParent instanceof ViewGroup) || !((ViewGroup) viewParent).isViewTransitioning(this)) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public void invalidate(Rect dirty) {
        int scrollX = this.mScrollX;
        int scrollY = this.mScrollY;
        invalidateInternal(dirty.left - scrollX, dirty.top - scrollY, dirty.right - scrollX, dirty.bottom - scrollY, true, false);
    }

    @Deprecated
    public void invalidate(int l, int t, int r, int b) {
        int scrollX = this.mScrollX;
        int scrollY = this.mScrollY;
        invalidateInternal(l - scrollX, t - scrollY, r - scrollX, b - scrollY, true, false);
    }

    public void invalidate() {
        invalidate(true);
    }

    public void invalidate(boolean invalidateCache) {
        invalidateInternal(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop, invalidateCache, true);
    }

    void invalidateInternal(int l, int t, int r, int b, boolean invalidateCache, boolean fullInvalidate) {
        View receiver;
        GhostView ghostView = this.mGhostView;
        if (ghostView != null) {
            ghostView.invalidate(true);
        } else if (skipInvalidate()) {
        } else {
            this.mPrivateFlags4 &= -193;
            this.mContentCaptureSessionCached = false;
            int i = this.mPrivateFlags;
            if ((i & 48) == 48 || ((invalidateCache && (i & 32768) == 32768) || (i & Integer.MIN_VALUE) != Integer.MIN_VALUE || (fullInvalidate && isOpaque() != this.mLastIsOpaque))) {
                if (fullInvalidate) {
                    this.mLastIsOpaque = isOpaque();
                    this.mPrivateFlags &= -33;
                }
                int i2 = this.mPrivateFlags | 2097152;
                this.mPrivateFlags = i2;
                if (invalidateCache) {
                    int i3 = i2 | Integer.MIN_VALUE;
                    this.mPrivateFlags = i3;
                    this.mPrivateFlags = i3 & (-32769);
                }
                AttachInfo ai = this.mAttachInfo;
                ViewParent p = this.mParent;
                if (p != null && ai != null && l < r && t < b) {
                    Rect damage = ai.mTmpInvalRect;
                    damage.set(l, t, r, b);
                    p.invalidateChild(this, damage);
                }
                Drawable drawable = this.mBackground;
                if (drawable != null && drawable.isProjected() && (receiver = getProjectionReceiver()) != null) {
                    receiver.damageInParent();
                }
            }
        }
    }

    private View getProjectionReceiver() {
        for (ViewParent p = getParent(); p != null && (p instanceof View); p = p.getParent()) {
            View v = (View) p;
            if (v.isProjectionReceiver()) {
                return v;
            }
        }
        return null;
    }

    private boolean isProjectionReceiver() {
        return this.mBackground != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateViewProperty(boolean invalidateParent, boolean forceRedraw) {
        if (!isHardwareAccelerated() || !this.mRenderNode.hasDisplayList() || (this.mPrivateFlags & 64) != 0) {
            if (invalidateParent) {
                invalidateParentCaches();
            }
            if (forceRedraw) {
                this.mPrivateFlags |= 32;
            }
            invalidate(false);
            return;
        }
        damageInParent();
    }

    protected void damageInParent() {
        ViewParent viewParent = this.mParent;
        if (viewParent != null && this.mAttachInfo != null) {
            viewParent.onDescendantInvalidated(this, this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void invalidateParentCaches() {
        ViewParent viewParent = this.mParent;
        if (viewParent instanceof View) {
            ((View) viewParent).mPrivateFlags |= Integer.MIN_VALUE;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void invalidateParentIfNeeded() {
        if (isHardwareAccelerated()) {
            ViewParent viewParent = this.mParent;
            if (viewParent instanceof View) {
                ((View) viewParent).invalidate(true);
            }
        }
    }

    protected void invalidateParentIfNeededAndWasQuickRejected() {
        if ((this.mPrivateFlags2 & 268435456) != 0) {
            invalidateParentIfNeeded();
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean isOpaque() {
        return (this.mPrivateFlags & 25165824) == 25165824 && getFinalAlpha() >= 1.0f;
    }

    protected void computeOpaqueFlags() {
        Drawable drawable = this.mBackground;
        if (drawable != null && drawable.getOpacity() == -1) {
            this.mPrivateFlags |= 8388608;
        } else {
            this.mPrivateFlags &= -8388609;
        }
        int flags = this.mViewFlags;
        if (((flags & 512) == 0 && (flags & 256) == 0) || (flags & 50331648) == 0 || (50331648 & flags) == 33554432) {
            this.mPrivateFlags |= 16777216;
        } else {
            this.mPrivateFlags &= -16777217;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasOpaqueScrollbars() {
        return (this.mPrivateFlags & 16777216) == 16777216;
    }

    public Handler getHandler() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler;
        }
        return null;
    }

    private HandlerActionQueue getRunQueue() {
        if (this.mRunQueue == null) {
            this.mRunQueue = new HandlerActionQueue();
        }
        return this.mRunQueue;
    }

    public ViewRootImpl getViewRootImpl() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mViewRootImpl;
        }
        return null;
    }

    public ThreadedRenderer getThreadedRenderer() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mThreadedRenderer;
        }
        return null;
    }

    public boolean post(Runnable action) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler.post(action);
        }
        getRunQueue().post(action);
        return true;
    }

    public boolean postDelayed(Runnable action, long delayMillis) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler.postDelayed(action, delayMillis);
        }
        getRunQueue().postDelayed(action, delayMillis);
        return true;
    }

    public void postOnAnimation(Runnable action) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.mChoreographer.postCallback(1, action, null);
        } else {
            getRunQueue().post(action);
        }
    }

    public void postOnAnimationDelayed(Runnable action, long delayMillis) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.mChoreographer.postCallbackDelayed(1, action, null, delayMillis);
        } else {
            getRunQueue().postDelayed(action, delayMillis);
        }
    }

    public boolean removeCallbacks(Runnable action) {
        if (action != null) {
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null) {
                attachInfo.mHandler.removeCallbacks(action);
                attachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, action, null);
            }
            getRunQueue().removeCallbacks(action);
        }
        return true;
    }

    public void postInvalidate() {
        postInvalidateDelayed(0L);
    }

    public void postInvalidate(int left, int top, int right, int bottom) {
        postInvalidateDelayed(0L, left, top, right, bottom);
    }

    public void postInvalidateDelayed(long delayMilliseconds) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.dispatchInvalidateDelayed(this, delayMilliseconds);
        }
    }

    public void postInvalidateDelayed(long delayMilliseconds, int left, int top, int right, int bottom) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            AttachInfo.InvalidateInfo info = AttachInfo.InvalidateInfo.obtain();
            info.target = this;
            info.left = left;
            info.top = top;
            info.right = right;
            info.bottom = bottom;
            attachInfo.mViewRootImpl.dispatchInvalidateRectDelayed(info, delayMilliseconds);
        }
    }

    public void postInvalidateOnAnimation() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.dispatchInvalidateOnAnimation(this);
        }
    }

    public void postInvalidateOnAnimation(int left, int top, int right, int bottom) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            AttachInfo.InvalidateInfo info = AttachInfo.InvalidateInfo.obtain();
            info.target = this;
            info.left = left;
            info.top = top;
            info.right = right;
            info.bottom = bottom;
            attachInfo.mViewRootImpl.dispatchInvalidateRectOnAnimation(info);
        }
    }

    private void postSendViewScrolledAccessibilityEventCallback(int dx, int dy) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain(4096);
            event.setScrollDeltaX(dx);
            event.setScrollDeltaY(dy);
            sendAccessibilityEventUnchecked(event);
        }
    }

    public void computeScroll() {
    }

    public boolean isHorizontalFadingEdgeEnabled() {
        return (this.mViewFlags & 4096) == 4096;
    }

    public void setHorizontalFadingEdgeEnabled(boolean horizontalFadingEdgeEnabled) {
        if (isHorizontalFadingEdgeEnabled() != horizontalFadingEdgeEnabled) {
            if (horizontalFadingEdgeEnabled) {
                initScrollCache();
            }
            this.mViewFlags ^= 4096;
        }
    }

    public boolean isVerticalFadingEdgeEnabled() {
        return (this.mViewFlags & 8192) == 8192;
    }

    public void setVerticalFadingEdgeEnabled(boolean verticalFadingEdgeEnabled) {
        if (isVerticalFadingEdgeEnabled() != verticalFadingEdgeEnabled) {
            if (verticalFadingEdgeEnabled) {
                initScrollCache();
            }
            this.mViewFlags ^= 8192;
        }
    }

    public int getFadingEdge() {
        return this.mViewFlags & 12288;
    }

    public int getFadingEdgeLength() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache != null && (this.mViewFlags & 12288) != 0) {
            return scrollabilityCache.fadingEdgeLength;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getTopFadingEdgeStrength() {
        return computeVerticalScrollOffset() > 0 ? 1.0f : 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getBottomFadingEdgeStrength() {
        return computeVerticalScrollOffset() + computeVerticalScrollExtent() < computeVerticalScrollRange() ? 1.0f : 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getLeftFadingEdgeStrength() {
        return computeHorizontalScrollOffset() > 0 ? 1.0f : 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getRightFadingEdgeStrength() {
        return computeHorizontalScrollOffset() + computeHorizontalScrollExtent() < computeHorizontalScrollRange() ? 1.0f : 0.0f;
    }

    public boolean isHorizontalScrollBarEnabled() {
        return (this.mViewFlags & 256) == 256;
    }

    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        if (isHorizontalScrollBarEnabled() != horizontalScrollBarEnabled) {
            this.mViewFlags ^= 256;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    public boolean isVerticalScrollBarEnabled() {
        return (this.mViewFlags & 512) == 512;
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if (isVerticalScrollBarEnabled() != verticalScrollBarEnabled) {
            this.mViewFlags ^= 512;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    protected void recomputePadding() {
        internalSetPadding(this.mUserPaddingLeft, this.mPaddingTop, this.mUserPaddingRight, this.mUserPaddingBottom);
    }

    public void setScrollbarFadingEnabled(boolean fadeScrollbars) {
        initScrollCache();
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        scrollabilityCache.fadeScrollBars = fadeScrollbars;
        if (fadeScrollbars) {
            scrollabilityCache.state = 0;
        } else {
            scrollabilityCache.state = 1;
        }
    }

    public boolean isScrollbarFadingEnabled() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache != null && scrollabilityCache.fadeScrollBars;
    }

    public int getScrollBarDefaultDelayBeforeFade() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache == null ? ViewConfiguration.getScrollDefaultDelay() : scrollabilityCache.scrollBarDefaultDelayBeforeFade;
    }

    public void setScrollBarDefaultDelayBeforeFade(int scrollBarDefaultDelayBeforeFade) {
        getScrollCache().scrollBarDefaultDelayBeforeFade = scrollBarDefaultDelayBeforeFade;
    }

    public int getScrollBarFadeDuration() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache == null ? ViewConfiguration.getScrollBarFadeDuration() : scrollabilityCache.scrollBarFadeDuration;
    }

    public void setScrollBarFadeDuration(int scrollBarFadeDuration) {
        getScrollCache().scrollBarFadeDuration = scrollBarFadeDuration;
    }

    public int getScrollBarSize() {
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        return scrollabilityCache == null ? ViewConfiguration.get(this.mContext).getScaledScrollBarSize() : scrollabilityCache.scrollBarSize;
    }

    public void setScrollBarSize(int scrollBarSize) {
        getScrollCache().scrollBarSize = scrollBarSize;
    }

    public void setScrollBarStyle(int style) {
        int i = this.mViewFlags;
        if (style != (i & 50331648)) {
            this.mViewFlags = (i & (-50331649)) | (50331648 & style);
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    @ViewDebug.ExportedProperty(mapping = {@ViewDebug.IntToString(from = 0, m86to = "INSIDE_OVERLAY"), @ViewDebug.IntToString(from = 16777216, m86to = "INSIDE_INSET"), @ViewDebug.IntToString(from = 33554432, m86to = "OUTSIDE_OVERLAY"), @ViewDebug.IntToString(from = 50331648, m86to = "OUTSIDE_INSET")})
    public int getScrollBarStyle() {
        return this.mViewFlags & 50331648;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int computeHorizontalScrollRange() {
        return getWidth();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int computeHorizontalScrollOffset() {
        return this.mScrollX;
    }

    protected int computeHorizontalScrollExtent() {
        return getWidth();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int computeVerticalScrollRange() {
        return getHeight();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int computeVerticalScrollOffset() {
        return this.mScrollY;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int computeVerticalScrollExtent() {
        return getHeight();
    }

    public boolean canScrollHorizontally(int direction) {
        int offset = computeHorizontalScrollOffset();
        int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (range == 0) {
            return false;
        }
        if (direction < 0) {
            if (offset <= 0) {
                return false;
            }
            return true;
        } else if (offset >= range - 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canScrollVertically(int direction) {
        int offset = computeVerticalScrollOffset();
        int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
        if (range == 0) {
            return false;
        }
        if (direction < 0) {
            if (offset <= 0) {
                return false;
            }
            return true;
        } else if (offset >= range - 1) {
            return false;
        } else {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getScrollIndicatorBounds(Rect out) {
        out.left = this.mScrollX;
        out.right = (this.mScrollX + this.mRight) - this.mLeft;
        out.top = this.mScrollY;
        out.bottom = (this.mScrollY + this.mBottom) - this.mTop;
    }

    private void onDrawScrollIndicators(Canvas c) {
        Drawable dr;
        int leftRtl;
        int rightRtl;
        if ((this.mPrivateFlags3 & SCROLL_INDICATORS_PFLAG3_MASK) == 0 || (dr = this.mScrollIndicatorDrawable) == null || this.mAttachInfo == null) {
            return;
        }
        int h = dr.getIntrinsicHeight();
        int w = dr.getIntrinsicWidth();
        Rect rect = this.mAttachInfo.mTmpInvalRect;
        getScrollIndicatorBounds(rect);
        if ((this.mPrivateFlags3 & 256) != 0) {
            boolean canScrollUp = canScrollVertically(-1);
            if (canScrollUp) {
                dr.setBounds(rect.left, rect.top, rect.right, rect.top + h);
                dr.draw(c);
            }
        }
        if ((this.mPrivateFlags3 & 512) != 0) {
            boolean canScrollDown = canScrollVertically(1);
            if (canScrollDown) {
                dr.setBounds(rect.left, rect.bottom - h, rect.right, rect.bottom);
                dr.draw(c);
            }
        }
        if (getLayoutDirection() == 1) {
            leftRtl = 8192;
            rightRtl = 4096;
        } else {
            leftRtl = 4096;
            rightRtl = 8192;
        }
        int leftMask = leftRtl | 1024;
        if ((this.mPrivateFlags3 & leftMask) != 0) {
            boolean canScrollLeft = canScrollHorizontally(-1);
            if (canScrollLeft) {
                dr.setBounds(rect.left, rect.top, rect.left + w, rect.bottom);
                dr.draw(c);
            }
        }
        int rightMask = rightRtl | 2048;
        if ((this.mPrivateFlags3 & rightMask) != 0) {
            boolean canScrollRight = canScrollHorizontally(1);
            if (canScrollRight) {
                dr.setBounds(rect.right - w, rect.top, rect.right, rect.bottom);
                dr.draw(c);
            }
        }
    }

    private void getHorizontalScrollBarBounds(Rect drawBounds, Rect touchBounds) {
        Rect bounds = drawBounds != null ? drawBounds : touchBounds;
        if (bounds == null) {
            return;
        }
        int inside = (this.mViewFlags & 33554432) == 0 ? -1 : 0;
        boolean drawVerticalScrollBar = isVerticalScrollBarEnabled() && !isVerticalScrollBarHidden();
        int size = getHorizontalScrollbarHeight();
        int verticalScrollBarGap = drawVerticalScrollBar ? getVerticalScrollbarWidth() : 0;
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        bounds.top = ((this.mScrollY + height) - size) - (this.mUserPaddingBottom & inside);
        bounds.left = this.mScrollX + (this.mPaddingLeft & inside);
        bounds.right = ((this.mScrollX + width) - (this.mUserPaddingRight & inside)) - verticalScrollBarGap;
        bounds.bottom = bounds.top + size;
        if (touchBounds == null) {
            return;
        }
        if (touchBounds != bounds) {
            touchBounds.set(bounds);
        }
        int minTouchTarget = this.mScrollCache.scrollBarMinTouchTarget;
        if (touchBounds.height() < minTouchTarget) {
            int adjust = (minTouchTarget - touchBounds.height()) / 2;
            touchBounds.bottom = Math.min(touchBounds.bottom + adjust, this.mScrollY + height);
            touchBounds.top = touchBounds.bottom - minTouchTarget;
        }
        int adjust2 = touchBounds.width();
        if (adjust2 < minTouchTarget) {
            int adjust3 = (minTouchTarget - touchBounds.width()) / 2;
            touchBounds.left -= adjust3;
            touchBounds.right = touchBounds.left + minTouchTarget;
        }
    }

    private void getVerticalScrollBarBounds(Rect bounds, Rect touchBounds) {
        if (this.mRoundScrollbarRenderer == null) {
            getStraightVerticalScrollBarBounds(bounds, touchBounds);
        } else {
            getRoundVerticalScrollBarBounds(bounds != null ? bounds : touchBounds);
        }
    }

    private void getRoundVerticalScrollBarBounds(Rect bounds) {
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        bounds.left = this.mScrollX;
        bounds.top = this.mScrollY;
        bounds.right = bounds.left + width;
        bounds.bottom = this.mScrollY + height;
    }

    private void getStraightVerticalScrollBarBounds(Rect drawBounds, Rect touchBounds) {
        int i;
        Rect bounds = drawBounds != null ? drawBounds : touchBounds;
        if (bounds == null) {
            return;
        }
        int inside = (this.mViewFlags & 33554432) == 0 ? -1 : 0;
        int size = getVerticalScrollbarWidth();
        int verticalScrollbarPosition = this.mVerticalScrollbarPosition;
        if (verticalScrollbarPosition == 0) {
            if (!isLayoutRtl()) {
                i = 2;
            } else {
                i = 1;
            }
            verticalScrollbarPosition = i;
        }
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        switch (verticalScrollbarPosition) {
            case 1:
                bounds.left = this.mScrollX + (this.mUserPaddingLeft & inside);
                break;
            default:
                bounds.left = ((this.mScrollX + width) - size) - (this.mUserPaddingRight & inside);
                break;
        }
        bounds.top = this.mScrollY + (this.mPaddingTop & inside);
        bounds.right = bounds.left + size;
        bounds.bottom = (this.mScrollY + height) - (this.mUserPaddingBottom & inside);
        if (touchBounds == null) {
            return;
        }
        if (touchBounds != bounds) {
            touchBounds.set(bounds);
        }
        int minTouchTarget = this.mScrollCache.scrollBarMinTouchTarget;
        if (touchBounds.width() < minTouchTarget) {
            int adjust = (minTouchTarget - touchBounds.width()) / 2;
            if (verticalScrollbarPosition == 2) {
                touchBounds.right = Math.min(touchBounds.right + adjust, this.mScrollX + width);
                touchBounds.left = touchBounds.right - minTouchTarget;
            } else {
                touchBounds.left = Math.max(touchBounds.left + adjust, this.mScrollX);
                touchBounds.right = touchBounds.left + minTouchTarget;
            }
        }
        int adjust2 = touchBounds.height();
        if (adjust2 < minTouchTarget) {
            int adjust3 = (minTouchTarget - touchBounds.height()) / 2;
            touchBounds.top -= adjust3;
            touchBounds.bottom = touchBounds.top + minTouchTarget;
        }
    }

    protected final void onDrawScrollBars(Canvas canvas) {
        int state;
        boolean invalidate;
        ScrollBarDrawable scrollBar;
        ScrollabilityCache cache = this.mScrollCache;
        if (cache == null || (state = cache.state) == 0) {
            return;
        }
        if (state == 2) {
            if (cache.interpolatorValues == null) {
                cache.interpolatorValues = new float[1];
            }
            float[] values = cache.interpolatorValues;
            if (cache.scrollBarInterpolator.timeToValues(values) == Interpolator.Result.FREEZE_END) {
                cache.state = 0;
            } else {
                cache.scrollBar.mutate().setAlpha(Math.round(values[0]));
            }
            invalidate = true;
        } else {
            cache.scrollBar.mutate().setAlpha(255);
            invalidate = false;
        }
        boolean drawHorizontalScrollBar = isHorizontalScrollBarEnabled();
        boolean drawVerticalScrollBar = isVerticalScrollBarEnabled() && !isVerticalScrollBarHidden();
        if (this.mRoundScrollbarRenderer != null) {
            if (drawVerticalScrollBar) {
                Rect bounds = cache.mScrollBarBounds;
                getVerticalScrollBarBounds(bounds, null);
                this.mRoundScrollbarRenderer.drawRoundScrollbars(canvas, cache.scrollBar.getAlpha() / 255.0f, bounds);
                if (invalidate) {
                    invalidate();
                }
            }
        } else if (drawVerticalScrollBar || drawHorizontalScrollBar) {
            ScrollBarDrawable scrollBar2 = cache.scrollBar;
            if (drawHorizontalScrollBar) {
                scrollBar2.setParameters(computeHorizontalScrollRange(), computeHorizontalScrollOffset(), computeHorizontalScrollExtent(), false);
                Rect bounds2 = cache.mScrollBarBounds;
                getHorizontalScrollBarBounds(bounds2, null);
                scrollBar = scrollBar2;
                onDrawHorizontalScrollBar(canvas, scrollBar2, bounds2.left, bounds2.top, bounds2.right, bounds2.bottom);
                if (invalidate) {
                    invalidate(bounds2);
                }
            } else {
                scrollBar = scrollBar2;
            }
            if (drawVerticalScrollBar) {
                scrollBar.setParameters(computeVerticalScrollRange(), computeVerticalScrollOffset(), computeVerticalScrollExtent(), true);
                Rect bounds3 = cache.mScrollBarBounds;
                getVerticalScrollBarBounds(bounds3, null);
                onDrawVerticalScrollBar(canvas, scrollBar, bounds3.left, bounds3.top, bounds3.right, bounds3.bottom);
                if (invalidate) {
                    invalidate(bounds3);
                }
            }
        }
    }

    protected boolean isVerticalScrollBarHidden() {
        return false;
    }

    protected void onDrawHorizontalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDrawVerticalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void assignParent(ViewParent parent) {
        if (this.mParent == null) {
            this.mParent = parent;
        } else if (parent == null) {
            this.mParent = null;
        } else {
            throw new RuntimeException("view " + this + " being added, but it already has a parent");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onAttachedToWindow() {
        ViewParent viewParent = this.mParent;
        if (viewParent != null && (this.mPrivateFlags & 512) != 0) {
            viewParent.requestTransparentRegion(this);
        }
        this.mPrivateFlags3 &= -5;
        jumpDrawablesToCurrentState();
        AccessibilityNodeIdManager.getInstance().registerViewWithId(this, getAccessibilityViewId());
        resetSubtreeAccessibilityStateChanged();
        rebuildOutline();
        if (isFocused()) {
            notifyFocusChangeToImeFocusController(true);
        }
        if (sTraceLayoutSteps) {
            setTraversalTracingEnabled(true);
        }
        String str = sTraceRequestLayoutClass;
        if (str != null && str.equals(getClass().getSimpleName())) {
            setRelayoutTracingEnabled(true);
        }
    }

    public boolean resolveRtlPropertiesIfNeeded() {
        if (needRtlPropertiesResolution()) {
            if (!isLayoutDirectionResolved()) {
                resolveLayoutDirection();
                resolveLayoutParams();
            }
            if (!isTextDirectionResolved()) {
                resolveTextDirection();
            }
            if (!isTextAlignmentResolved()) {
                resolveTextAlignment();
            }
            if (!areDrawablesResolved()) {
                resolveDrawables();
            }
            if (!isPaddingResolved()) {
                resolvePadding();
            }
            onRtlPropertiesChanged(getLayoutDirection());
            return true;
        }
        return false;
    }

    public void resetRtlProperties() {
        resetResolvedLayoutDirection();
        resetResolvedTextDirection();
        resetResolvedTextAlignment();
        resetResolvedPadding();
        resetResolvedDrawables();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchScreenStateChanged(int screenState) {
        onScreenStateChanged(screenState);
    }

    public void onScreenStateChanged(int screenState) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchMovedToDisplay(Display display, Configuration config) {
        this.mAttachInfo.mDisplay = display;
        this.mAttachInfo.mDisplayState = display.getState();
        onMovedToDisplay(display.getDisplayId(), config);
    }

    public void onMovedToDisplay(int displayId, Configuration config) {
    }

    private boolean hasRtlSupport() {
        return this.mContext.getApplicationInfo().hasRtlSupport();
    }

    private boolean isRtlCompatibilityMode() {
        int targetSdkVersion = getContext().getApplicationInfo().targetSdkVersion;
        return targetSdkVersion < 17 || !hasRtlSupport();
    }

    private boolean needRtlPropertiesResolution() {
        return (this.mPrivateFlags2 & ALL_RTL_PROPERTIES_RESOLVED) != ALL_RTL_PROPERTIES_RESOLVED;
    }

    public void onRtlPropertiesChanged(int layoutDirection) {
    }

    public boolean resolveLayoutDirection() {
        this.mPrivateFlags2 &= -49;
        if (hasRtlSupport()) {
            int i = this.mPrivateFlags2;
            switch ((i & 12) >> 2) {
                case 1:
                    this.mPrivateFlags2 = i | 16;
                    break;
                case 2:
                    if (!canResolveLayoutDirection()) {
                        return false;
                    }
                    try {
                        if (!this.mParent.isLayoutDirectionResolved()) {
                            return false;
                        }
                        if (this.mParent.getLayoutDirection() == 1) {
                            this.mPrivateFlags2 |= 16;
                            break;
                        }
                    } catch (AbstractMethodError e) {
                        Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                        break;
                    }
                    break;
                case 3:
                    if (1 == TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())) {
                        this.mPrivateFlags2 |= 16;
                        break;
                    }
                    break;
            }
        }
        this.mPrivateFlags2 |= 32;
        return true;
    }

    public boolean canResolveLayoutDirection() {
        switch (getRawLayoutDirection()) {
            case 2:
                ViewParent viewParent = this.mParent;
                if (viewParent != null) {
                    try {
                        return viewParent.canResolveLayoutDirection();
                    } catch (AbstractMethodError e) {
                        Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                        return false;
                    }
                }
                return false;
            default:
                return true;
        }
    }

    public void resetResolvedLayoutDirection() {
        this.mPrivateFlags2 &= -49;
    }

    public boolean isLayoutDirectionInherited() {
        return getRawLayoutDirection() == 2;
    }

    public boolean isLayoutDirectionResolved() {
        return (this.mPrivateFlags2 & 32) == 32;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isPaddingResolved() {
        return (this.mPrivateFlags2 & 536870912) == 536870912;
    }

    public void resolvePadding() {
        int resolvedLayoutDirection = getLayoutDirection();
        if (!isRtlCompatibilityMode()) {
            if (this.mBackground != null && (!this.mLeftPaddingDefined || !this.mRightPaddingDefined)) {
                ThreadLocal<Rect> threadLocal = sThreadLocal;
                Rect padding = threadLocal.get();
                if (padding == null) {
                    padding = new Rect();
                    threadLocal.set(padding);
                }
                this.mBackground.getPadding(padding);
                if (!this.mLeftPaddingDefined) {
                    this.mUserPaddingLeftInitial = padding.left;
                }
                if (!this.mRightPaddingDefined) {
                    this.mUserPaddingRightInitial = padding.right;
                }
            }
            switch (resolvedLayoutDirection) {
                case 1:
                    int i = this.mUserPaddingStart;
                    if (i != Integer.MIN_VALUE) {
                        this.mUserPaddingRight = i;
                    } else {
                        this.mUserPaddingRight = this.mUserPaddingRightInitial;
                    }
                    int i2 = this.mUserPaddingEnd;
                    if (i2 != Integer.MIN_VALUE) {
                        this.mUserPaddingLeft = i2;
                        break;
                    } else {
                        this.mUserPaddingLeft = this.mUserPaddingLeftInitial;
                        break;
                    }
                default:
                    int i3 = this.mUserPaddingStart;
                    if (i3 != Integer.MIN_VALUE) {
                        this.mUserPaddingLeft = i3;
                    } else {
                        this.mUserPaddingLeft = this.mUserPaddingLeftInitial;
                    }
                    int i4 = this.mUserPaddingEnd;
                    if (i4 != Integer.MIN_VALUE) {
                        this.mUserPaddingRight = i4;
                        break;
                    } else {
                        this.mUserPaddingRight = this.mUserPaddingRightInitial;
                        break;
                    }
            }
            int i5 = this.mUserPaddingBottom;
            if (i5 < 0) {
                i5 = this.mPaddingBottom;
            }
            this.mUserPaddingBottom = i5;
        }
        internalSetPadding(this.mUserPaddingLeft, this.mPaddingTop, this.mUserPaddingRight, this.mUserPaddingBottom);
        onRtlPropertiesChanged(resolvedLayoutDirection);
        this.mPrivateFlags2 |= 536870912;
    }

    public void resetResolvedPadding() {
        resetResolvedPaddingInternal();
    }

    void resetResolvedPaddingInternal() {
        this.mPrivateFlags2 &= -536870913;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDetachedFromWindow() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDetachedFromWindowInternal() {
        this.mPrivateFlags &= -67108865;
        int i = this.mPrivateFlags3 & (-5);
        this.mPrivateFlags3 = i;
        this.mPrivateFlags3 = i & (-33554433);
        removeUnsetPressCallback();
        removeLongPressCallback();
        removePerformClickCallback();
        clearAccessibilityThrottles();
        stopNestedScroll();
        jumpDrawablesToCurrentState();
        destroyDrawingCache();
        cleanupDraw();
        this.mCurrentAnimation = null;
        if ((this.mViewFlags & 1073741824) == 1073741824) {
            hideTooltip();
        }
        AccessibilityNodeIdManager.getInstance().unregisterViewWithId(getAccessibilityViewId());
        RenderNode renderNode = this.mBackgroundRenderNode;
        if (renderNode != null) {
            renderNode.forceEndAnimators();
        }
        this.mRenderNode.forceEndAnimators();
    }

    private void cleanupDraw() {
        resetDisplayList();
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.cancelInvalidate(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateInheritedLayoutMode(int layoutModeOfRoot) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getWindowAttachCount() {
        return this.mWindowAttachCount;
    }

    public IBinder getWindowToken() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mWindowToken;
        }
        return null;
    }

    public WindowId getWindowId() {
        AttachInfo ai = this.mAttachInfo;
        if (ai == null) {
            return null;
        }
        if (ai.mWindowId == null) {
            try {
                ai.mIWindowId = ai.mSession.getWindowId(ai.mWindowToken);
                if (ai.mIWindowId != null) {
                    ai.mWindowId = new WindowId(ai.mIWindowId);
                }
            } catch (RemoteException e) {
            }
        }
        return ai.mWindowId;
    }

    public IBinder getApplicationWindowToken() {
        AttachInfo ai = this.mAttachInfo;
        if (ai != null) {
            IBinder appWindowToken = ai.mPanelParentWindowToken;
            if (appWindowToken == null) {
                return ai.mWindowToken;
            }
            return appWindowToken;
        }
        return null;
    }

    public Display getDisplay() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mDisplay;
        }
        return null;
    }

    IWindowSession getWindowSession() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mSession;
        }
        return null;
    }

    protected IWindow getWindow() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mWindow;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int combineVisibility(int vis1, int vis2) {
        return Math.max(vis1, vis2);
    }

    public void fakeFocusAfterAttachingToWindow() {
        this.mShouldFakeFocus = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchAttachedToWindow(AttachInfo info, int visibility) {
        this.mAttachInfo = info;
        ViewOverlay viewOverlay = this.mOverlay;
        if (viewOverlay != null) {
            viewOverlay.getOverlayView().dispatchAttachedToWindow(info, visibility);
        }
        this.mWindowAttachCount++;
        this.mPrivateFlags |= 1024;
        if (this.mFloatingTreeObserver != null) {
            info.mTreeObserver.merge(this.mFloatingTreeObserver);
            this.mFloatingTreeObserver = null;
        }
        registerPendingFrameMetricsObservers();
        if ((this.mPrivateFlags & 524288) != 0) {
            this.mAttachInfo.mScrollContainers.add(this);
            this.mPrivateFlags |= 1048576;
        }
        HandlerActionQueue handlerActionQueue = this.mRunQueue;
        if (handlerActionQueue != null) {
            handlerActionQueue.executeActions(info.mHandler);
            this.mRunQueue = null;
        }
        performCollectViewAttributes(this.mAttachInfo, visibility);
        onAttachedToWindow();
        ListenerInfo li = this.mListenerInfo;
        CopyOnWriteArrayList<OnAttachStateChangeListener> listeners = li != null ? li.mOnAttachStateChangeListeners : null;
        if (listeners != null && listeners.size() > 0) {
            Iterator<OnAttachStateChangeListener> it = listeners.iterator();
            while (it.hasNext()) {
                OnAttachStateChangeListener listener = it.next();
                listener.onViewAttachedToWindow(this);
            }
        }
        int vis = info.mWindowVisibility;
        if (vis != 8) {
            onWindowVisibilityChanged(vis);
            if (isShown()) {
                onVisibilityAggregated(vis == 0);
            }
        }
        onVisibilityChanged(this, visibility);
        if ((this.mPrivateFlags & 1024) != 0) {
            refreshDrawableState();
        }
        needGlobalAttributesUpdate(false);
        notifyEnterOrExitForAutoFillIfNeeded(true);
        notifyAppearedOrDisappearedForContentCaptureIfNeeded(true);
        if (this.mShouldFakeFocus) {
            getViewRootImpl().dispatchCompatFakeFocus();
            this.mShouldFakeFocus = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchDetachedFromWindow() {
        AttachInfo info = this.mAttachInfo;
        if (info != null) {
            int vis = info.mWindowVisibility;
            if (vis != 8) {
                onWindowVisibilityChanged(8);
                if (isShown()) {
                    onVisibilityAggregated(false);
                }
            }
        }
        onDetachedFromWindow();
        onDetachedFromWindowInternal();
        if (info != null) {
            info.mViewRootImpl.getImeFocusController().onViewDetachedFromWindow(this);
        }
        ListenerInfo li = this.mListenerInfo;
        CopyOnWriteArrayList<OnAttachStateChangeListener> listeners = li != null ? li.mOnAttachStateChangeListeners : null;
        if (listeners != null && listeners.size() > 0) {
            Iterator<OnAttachStateChangeListener> it = listeners.iterator();
            while (it.hasNext()) {
                OnAttachStateChangeListener listener = it.next();
                listener.onViewDetachedFromWindow(this);
            }
        }
        if ((this.mPrivateFlags & 1048576) != 0) {
            this.mAttachInfo.mScrollContainers.remove(this);
            this.mPrivateFlags &= -1048577;
        }
        notifyAppearedOrDisappearedForContentCaptureIfNeeded(false);
        this.mAttachInfo = null;
        ViewOverlay viewOverlay = this.mOverlay;
        if (viewOverlay != null) {
            viewOverlay.getOverlayView().dispatchDetachedFromWindow();
        }
        notifyEnterOrExitForAutoFillIfNeeded(false);
        if (info != null && !collectPreferKeepClearRects().isEmpty()) {
            info.mViewRootImpl.updateKeepClearRectsForView(this);
        }
    }

    public final void cancelPendingInputEvents() {
        dispatchCancelPendingInputEvents();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchCancelPendingInputEvents() {
        this.mPrivateFlags3 &= -17;
        onCancelPendingInputEvents();
        if ((this.mPrivateFlags3 & 16) != 16) {
            throw new SuperNotCalledException("View " + getClass().getSimpleName() + " did not call through to super.onCancelPendingInputEvents()");
        }
    }

    public void onCancelPendingInputEvents() {
        removePerformClickCallback();
        cancelLongPress();
        this.mPrivateFlags3 |= 16;
    }

    public void saveHierarchyState(SparseArray<Parcelable> container) {
        dispatchSaveInstanceState(container);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        if (this.mID != -1 && (this.mViewFlags & 65536) == 0) {
            this.mPrivateFlags &= -131073;
            Parcelable state = onSaveInstanceState();
            if ((this.mPrivateFlags & 131072) == 0) {
                throw new IllegalStateException("Derived class did not call super.onSaveInstanceState()");
            }
            if (state != null) {
                container.put(this.mID, state);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        this.mPrivateFlags |= 131072;
        if (this.mStartActivityRequestWho != null || isAutofilled() || this.mAutofillViewId > 1073741823) {
            BaseSavedState state = new BaseSavedState(AbsSavedState.EMPTY_STATE);
            if (this.mStartActivityRequestWho != null) {
                state.mSavedData |= 1;
            }
            if (isAutofilled()) {
                state.mSavedData |= 2;
            }
            if (this.mAutofillViewId > 1073741823) {
                state.mSavedData |= 4;
            }
            state.mStartActivityRequestWhoSaved = this.mStartActivityRequestWho;
            state.mIsAutofilled = isAutofilled();
            state.mHideHighlight = hideAutofillHighlight();
            state.mAutofillViewId = this.mAutofillViewId;
            return state;
        }
        return BaseSavedState.EMPTY_STATE;
    }

    public void restoreHierarchyState(SparseArray<Parcelable> container) {
        dispatchRestoreInstanceState(container);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        Parcelable state;
        int i = this.mID;
        if (i != -1 && (state = container.get(i)) != null) {
            this.mPrivateFlags &= -131073;
            onRestoreInstanceState(state);
            if ((this.mPrivateFlags & 131072) == 0) {
                throw new IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        this.mPrivateFlags |= 131072;
        if (state != null && !(state instanceof AbsSavedState)) {
            throw new IllegalArgumentException("Wrong state class, expecting View State but received " + state.getClass().toString() + " instead. This usually happens when two views of different type have the same id in the same hierarchy. This view's id is " + ViewDebug.resolveId(this.mContext, getId()) + ". Make sure other views do not use the same id.");
        }
        if (state != null && (state instanceof BaseSavedState)) {
            BaseSavedState baseState = (BaseSavedState) state;
            if ((baseState.mSavedData & 1) != 0) {
                this.mStartActivityRequestWho = baseState.mStartActivityRequestWhoSaved;
            }
            if ((baseState.mSavedData & 2) != 0) {
                setAutofilled(baseState.mIsAutofilled, baseState.mHideHighlight);
            }
            if ((baseState.mSavedData & 4) != 0) {
                ((BaseSavedState) state).mSavedData &= -5;
                if ((this.mPrivateFlags3 & 1073741824) != 0) {
                    if (Log.isLoggable(AUTOFILL_LOG_TAG, 3)) {
                        Log.m112d(AUTOFILL_LOG_TAG, "onRestoreInstanceState(): not setting autofillId to " + baseState.mAutofillViewId + " because view explicitly set it to " + this.mAutofillId);
                        return;
                    }
                    return;
                }
                this.mAutofillViewId = baseState.mAutofillViewId;
                this.mAutofillId = null;
            }
        }
    }

    public long getDrawingTime() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mDrawingTime;
        }
        return 0L;
    }

    public void setDuplicateParentStateEnabled(boolean enabled) {
        setFlags(enabled ? 4194304 : 0, 4194304);
    }

    public boolean isDuplicateParentStateEnabled() {
        return (this.mViewFlags & 4194304) == 4194304;
    }

    public void setLayerType(int layerType, Paint paint) {
        if (layerType < 0 || layerType > 2) {
            throw new IllegalArgumentException("Layer type can only be one of: LAYER_TYPE_NONE, LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE");
        }
        boolean typeChanged = this.mRenderNode.setLayerType(layerType);
        if (!typeChanged) {
            setLayerPaint(paint);
            return;
        }
        if (layerType != 1) {
            destroyDrawingCache();
        }
        this.mLayerType = layerType;
        Paint paint2 = layerType == 0 ? null : paint;
        this.mLayerPaint = paint2;
        this.mRenderNode.setLayerPaint(paint2);
        invalidateParentCaches();
        invalidate(true);
    }

    public void setRenderEffect(RenderEffect renderEffect) {
        if (this.mRenderNode.setRenderEffect(renderEffect)) {
            invalidateViewProperty(true, true);
        }
    }

    public void setLayerPaint(Paint paint) {
        int layerType = getLayerType();
        if (layerType != 0) {
            this.mLayerPaint = paint;
            if (layerType == 2) {
                if (this.mRenderNode.setLayerPaint(paint)) {
                    invalidateViewProperty(false, false);
                    return;
                }
                return;
            }
            invalidate();
        }
    }

    public int getLayerType() {
        return this.mLayerType;
    }

    public void buildLayer() {
        if (this.mLayerType == 0) {
            return;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            throw new IllegalStateException("This view must be attached to a window first");
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        switch (this.mLayerType) {
            case 1:
                buildDrawingCache(true);
                return;
            case 2:
                updateDisplayListIfDirty();
                if (attachInfo.mThreadedRenderer != null && this.mRenderNode.hasDisplayList()) {
                    attachInfo.mThreadedRenderer.buildLayer(this.mRenderNode);
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void destroyHardwareResources() {
        ViewOverlay viewOverlay = this.mOverlay;
        if (viewOverlay != null) {
            viewOverlay.getOverlayView().destroyHardwareResources();
        }
        GhostView ghostView = this.mGhostView;
        if (ghostView != null) {
            ghostView.destroyHardwareResources();
        }
    }

    @Deprecated
    public void setDrawingCacheEnabled(boolean enabled) {
        int i = 0;
        this.mCachingFailed = false;
        if (enabled) {
            i = 32768;
        }
        setFlags(i, 32768);
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    @Deprecated
    public boolean isDrawingCacheEnabled() {
        return (this.mViewFlags & 32768) == 32768;
    }

    public void outputDirtyFlags(String indent, boolean clear, int clearMask) {
        Log.m112d(VIEW_LOG_TAG, indent + this + "             DIRTY(" + (this.mPrivateFlags & 2097152) + ") DRAWN(" + (this.mPrivateFlags & 32) + ") CACHE_VALID(" + (this.mPrivateFlags & 32768) + ") INVALIDATED(" + (this.mPrivateFlags & Integer.MIN_VALUE) + NavigationBarInflaterView.KEY_CODE_END);
        if (clear) {
            this.mPrivateFlags &= clearMask;
        }
        if (this instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) this;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                child.outputDirtyFlags(indent + "  ", clear, clearMask);
            }
        }
    }

    protected void dispatchGetDisplayList() {
    }

    public boolean canHaveDisplayList() {
        AttachInfo attachInfo = this.mAttachInfo;
        return (attachInfo == null || attachInfo.mThreadedRenderer == null) ? false : true;
    }

    public RenderNode updateDisplayListIfDirty() {
        RenderNode renderNode = this.mRenderNode;
        if (!canHaveDisplayList()) {
            return renderNode;
        }
        if ((this.mPrivateFlags & 32768) == 0 || !renderNode.hasDisplayList() || this.mRecreateDisplayList) {
            if (renderNode.hasDisplayList() && !this.mRecreateDisplayList) {
                int i = this.mPrivateFlags | 32800;
                this.mPrivateFlags = i;
                this.mPrivateFlags = i & (-2097153);
                dispatchGetDisplayList();
                return renderNode;
            }
            this.mRecreateDisplayList = true;
            int width = this.mRight - this.mLeft;
            int height = this.mBottom - this.mTop;
            int layerType = getLayerType();
            renderNode.clearStretch();
            RecordingCanvas canvas = renderNode.beginRecording(width, height);
            try {
                if (layerType == 1) {
                    buildDrawingCache(true);
                    Bitmap cache = getDrawingCache(true);
                    if (cache != null) {
                        canvas.drawBitmap(cache, 0.0f, 0.0f, this.mLayerPaint);
                    }
                } else {
                    computeScroll();
                    canvas.translate(-this.mScrollX, -this.mScrollY);
                    int i2 = this.mPrivateFlags | 32800;
                    this.mPrivateFlags = i2;
                    int i3 = i2 & (-2097153);
                    this.mPrivateFlags = i3;
                    if ((i3 & 128) == 128) {
                        dispatchDraw(canvas);
                        drawAutofilledHighlight(canvas);
                        ViewOverlay viewOverlay = this.mOverlay;
                        if (viewOverlay != null && !viewOverlay.isEmpty()) {
                            this.mOverlay.getOverlayView().draw(canvas);
                        }
                        if (isShowingLayoutBounds()) {
                            debugDrawFocus(canvas);
                        }
                    } else {
                        draw(canvas);
                    }
                }
            } finally {
                renderNode.endRecording();
                setDisplayListProperties(renderNode);
            }
        } else {
            int i4 = this.mPrivateFlags | 32800;
            this.mPrivateFlags = i4;
            this.mPrivateFlags = i4 & (-2097153);
        }
        return renderNode;
    }

    private void resetDisplayList() {
        this.mRenderNode.discardDisplayList();
        RenderNode renderNode = this.mBackgroundRenderNode;
        if (renderNode != null) {
            renderNode.discardDisplayList();
        }
    }

    @Deprecated
    public Bitmap getDrawingCache() {
        return getDrawingCache(false);
    }

    @Deprecated
    public Bitmap getDrawingCache(boolean autoScale) {
        int i = this.mViewFlags;
        if ((i & 131072) == 131072) {
            return null;
        }
        if ((i & 32768) == 32768) {
            buildDrawingCache(autoScale);
        }
        return autoScale ? this.mDrawingCache : this.mUnscaledDrawingCache;
    }

    @Deprecated
    public void destroyDrawingCache() {
        Bitmap bitmap = this.mDrawingCache;
        if (bitmap != null) {
            bitmap.recycle();
            this.mDrawingCache = null;
        }
        Bitmap bitmap2 = this.mUnscaledDrawingCache;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.mUnscaledDrawingCache = null;
        }
    }

    @Deprecated
    public void setDrawingCacheBackgroundColor(int color) {
        if (color != this.mDrawingCacheBackgroundColor) {
            this.mDrawingCacheBackgroundColor = color;
            this.mPrivateFlags &= -32769;
        }
    }

    @Deprecated
    public int getDrawingCacheBackgroundColor() {
        return this.mDrawingCacheBackgroundColor;
    }

    @Deprecated
    public void buildDrawingCache() {
        buildDrawingCache(false);
    }

    @Deprecated
    public void buildDrawingCache(boolean autoScale) {
        if ((this.mPrivateFlags & 32768) != 0) {
            if (autoScale) {
                if (this.mDrawingCache != null) {
                    return;
                }
            } else if (this.mUnscaledDrawingCache != null) {
                return;
            }
        }
        if (Trace.isTagEnabled(8L)) {
            Trace.traceBegin(8L, "buildDrawingCache/SW Layer for " + getClass().getSimpleName());
        }
        try {
            buildDrawingCacheImpl(autoScale);
        } finally {
            Trace.traceEnd(8L);
        }
    }

    private void buildDrawingCacheImpl(boolean autoScale) {
        Bitmap.Config quality;
        boolean z;
        Canvas canvas;
        this.mCachingFailed = false;
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        AttachInfo attachInfo = this.mAttachInfo;
        boolean scalingRequired = attachInfo != null && attachInfo.mScalingRequired;
        if (autoScale && scalingRequired) {
            width = (int) ((width * attachInfo.mApplicationScale) + 0.5f);
            height = (int) ((height * attachInfo.mApplicationScale) + 0.5f);
        }
        int drawingCacheBackgroundColor = this.mDrawingCacheBackgroundColor;
        boolean opaque = drawingCacheBackgroundColor != 0 || isOpaque();
        boolean use32BitCache = attachInfo != null && attachInfo.mUse32BitDrawingCache;
        long projectedBitmapSize = width * height * ((!opaque || use32BitCache) ? 4 : 2);
        long drawingCacheSize = ViewConfiguration.get(this.mContext).getScaledMaximumDrawingCacheSize();
        if (width > 0 && height > 0) {
            if (projectedBitmapSize <= drawingCacheSize) {
                boolean clear = true;
                Bitmap bitmap = autoScale ? this.mDrawingCache : this.mUnscaledDrawingCache;
                if (bitmap == null || bitmap.getWidth() != width || bitmap.getHeight() != height) {
                    if (!opaque) {
                        quality = Bitmap.Config.ARGB_8888;
                    } else {
                        quality = use32BitCache ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                    }
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    try {
                        bitmap = Bitmap.createBitmap(this.mResources.getDisplayMetrics(), width, height, quality);
                        bitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
                        if (autoScale) {
                            try {
                                this.mDrawingCache = bitmap;
                            } catch (OutOfMemoryError e) {
                                if (autoScale) {
                                    this.mDrawingCache = null;
                                } else {
                                    this.mUnscaledDrawingCache = null;
                                }
                                this.mCachingFailed = true;
                                return;
                            }
                        } else {
                            this.mUnscaledDrawingCache = bitmap;
                        }
                        if (opaque && use32BitCache) {
                            z = false;
                            bitmap.setHasAlpha(false);
                        } else {
                            z = false;
                        }
                        if (drawingCacheBackgroundColor != 0) {
                            z = true;
                        }
                        clear = z;
                    } catch (OutOfMemoryError e2) {
                    }
                }
                if (attachInfo != null) {
                    canvas = attachInfo.mCanvas;
                    if (canvas == null) {
                        canvas = new Canvas();
                    }
                    canvas.setBitmap(bitmap);
                    attachInfo.mCanvas = null;
                } else {
                    canvas = new Canvas(bitmap);
                }
                if (clear) {
                    bitmap.eraseColor(drawingCacheBackgroundColor);
                }
                computeScroll();
                int restoreCount = canvas.save();
                if (autoScale && scalingRequired) {
                    float scale = attachInfo.mApplicationScale;
                    canvas.scale(scale, scale);
                }
                canvas.translate(-this.mScrollX, -this.mScrollY);
                this.mPrivateFlags |= 32;
                AttachInfo attachInfo2 = this.mAttachInfo;
                if (attachInfo2 == null || !attachInfo2.mHardwareAccelerated || this.mLayerType != 0) {
                    this.mPrivateFlags |= 32768;
                }
                int i = this.mPrivateFlags;
                if ((i & 128) == 128) {
                    this.mPrivateFlags = i & (-2097153);
                    dispatchDraw(canvas);
                    drawAutofilledHighlight(canvas);
                    ViewOverlay viewOverlay = this.mOverlay;
                    if (viewOverlay != null && !viewOverlay.isEmpty()) {
                        this.mOverlay.getOverlayView().draw(canvas);
                    }
                } else {
                    draw(canvas);
                }
                canvas.restoreToCount(restoreCount);
                canvas.setBitmap(null);
                if (attachInfo != null) {
                    attachInfo.mCanvas = canvas;
                    return;
                }
                return;
            }
        }
        if (width > 0 && height > 0) {
            Log.m104w(VIEW_LOG_TAG, getClass().getSimpleName() + " not displayed because it is too large to fit into a software layer (or drawing cache), needs " + projectedBitmapSize + " bytes, only " + drawingCacheSize + " available");
        }
        destroyDrawingCache();
        this.mCachingFailed = true;
    }

    public Bitmap createSnapshot(ViewDebug.CanvasProvider canvasProvider, boolean skipChildren) {
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        AttachInfo attachInfo = this.mAttachInfo;
        float scale = attachInfo != null ? attachInfo.mApplicationScale : 1.0f;
        int width2 = (int) ((width * scale) + 0.5f);
        int height2 = (int) ((height * scale) + 0.5f);
        Canvas oldCanvas = null;
        try {
            Canvas canvas = canvasProvider.getCanvas(this, width2 > 0 ? width2 : 1, height2 > 0 ? height2 : 1);
            if (attachInfo != null) {
                oldCanvas = attachInfo.mCanvas;
                attachInfo.mCanvas = null;
            }
            computeScroll();
            int restoreCount = canvas.save();
            canvas.scale(scale, scale);
            canvas.translate(-this.mScrollX, -this.mScrollY);
            int flags = this.mPrivateFlags;
            int i = flags & (-2097153);
            this.mPrivateFlags = i;
            if ((i & 128) == 128) {
                dispatchDraw(canvas);
                drawAutofilledHighlight(canvas);
                ViewOverlay viewOverlay = this.mOverlay;
                if (viewOverlay != null && !viewOverlay.isEmpty()) {
                    this.mOverlay.getOverlayView().draw(canvas);
                }
            } else {
                draw(canvas);
            }
            this.mPrivateFlags = flags;
            canvas.restoreToCount(restoreCount);
            return canvasProvider.createBitmap();
        } finally {
            if (oldCanvas != null) {
                attachInfo.mCanvas = oldCanvas;
            }
        }
    }

    public boolean isInEditMode() {
        return false;
    }

    protected boolean isPaddingOffsetRequired() {
        return false;
    }

    protected int getLeftPaddingOffset() {
        return 0;
    }

    protected int getRightPaddingOffset() {
        return 0;
    }

    protected int getTopPaddingOffset() {
        return 0;
    }

    protected int getBottomPaddingOffset() {
        return 0;
    }

    protected int getFadeTop(boolean offsetRequired) {
        int top = this.mPaddingTop;
        return offsetRequired ? top + getTopPaddingOffset() : top;
    }

    protected int getFadeHeight(boolean offsetRequired) {
        int padding = this.mPaddingTop;
        if (offsetRequired) {
            padding += getTopPaddingOffset();
        }
        return ((this.mBottom - this.mTop) - this.mPaddingBottom) - padding;
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public boolean isHardwareAccelerated() {
        AttachInfo attachInfo = this.mAttachInfo;
        return attachInfo != null && attachInfo.mHardwareAccelerated;
    }

    public void setClipBounds(Rect clipBounds) {
        Rect rect = this.mClipBounds;
        if (clipBounds != rect) {
            if (clipBounds != null && clipBounds.equals(rect)) {
                return;
            }
            if (clipBounds != null) {
                Rect rect2 = this.mClipBounds;
                if (rect2 == null) {
                    this.mClipBounds = new Rect(clipBounds);
                } else {
                    rect2.set(clipBounds);
                }
            } else {
                this.mClipBounds = null;
            }
            this.mRenderNode.setClipRect(this.mClipBounds);
            invalidateViewProperty(false, false);
        }
    }

    public Rect getClipBounds() {
        if (this.mClipBounds != null) {
            return new Rect(this.mClipBounds);
        }
        return null;
    }

    public boolean getClipBounds(Rect outRect) {
        Rect rect = this.mClipBounds;
        if (rect != null) {
            outRect.set(rect);
            return true;
        }
        return false;
    }

    private boolean applyLegacyAnimation(ViewGroup parent, long drawingTime, Animation a, boolean scalingRequired) {
        Transformation invalidationTransform;
        int flags = parent.mGroupFlags;
        boolean initialized = a.isInitialized();
        if (!initialized) {
            a.initialize(this.mRight - this.mLeft, this.mBottom - this.mTop, parent.getWidth(), parent.getHeight());
            a.initializeInvalidateRegion(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null) {
                a.setListenerHandler(attachInfo.mHandler);
            }
            onAnimationStart();
        }
        Transformation t = parent.getChildTransformation();
        boolean more = a.getTransformation(drawingTime, t, 1.0f);
        if (scalingRequired && this.mAttachInfo.mApplicationScale != 1.0f) {
            if (parent.mInvalidationTransformation == null) {
                parent.mInvalidationTransformation = new Transformation();
            }
            Transformation invalidationTransform2 = parent.mInvalidationTransformation;
            a.getTransformation(drawingTime, invalidationTransform2, 1.0f);
            invalidationTransform = invalidationTransform2;
        } else {
            invalidationTransform = t;
        }
        if (more) {
            if (!a.willChangeBounds()) {
                if ((flags & 144) == 128) {
                    parent.mGroupFlags |= 4;
                } else if ((flags & 4) == 0) {
                    parent.mPrivateFlags |= 64;
                    parent.invalidate(this.mLeft, this.mTop, this.mRight, this.mBottom);
                }
            } else {
                if (parent.mInvalidateRegion == null) {
                    parent.mInvalidateRegion = new RectF();
                }
                RectF region = parent.mInvalidateRegion;
                a.getInvalidateRegion(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop, region, invalidationTransform);
                parent.mPrivateFlags |= 64;
                int left = this.mLeft + ((int) region.left);
                int top = this.mTop + ((int) region.top);
                parent.invalidate(left, top, ((int) (region.width() + 0.5f)) + left, ((int) (region.height() + 0.5f)) + top);
            }
        }
        return more;
    }

    void setDisplayListProperties(RenderNode renderNode) {
        int transformType;
        if (renderNode != null) {
            renderNode.setHasOverlappingRendering(getHasOverlappingRendering());
            ViewParent viewParent = this.mParent;
            renderNode.setClipToBounds((viewParent instanceof ViewGroup) && ((ViewGroup) viewParent).getClipChildren());
            float alpha = 1.0f;
            ViewParent viewParent2 = this.mParent;
            if ((viewParent2 instanceof ViewGroup) && (((ViewGroup) viewParent2).mGroupFlags & 2048) != 0) {
                ViewGroup parentVG = (ViewGroup) this.mParent;
                Transformation t = parentVG.getChildTransformation();
                if (parentVG.getChildStaticTransformation(this, t) && (transformType = t.getTransformationType()) != 0) {
                    if ((transformType & 1) != 0) {
                        alpha = t.getAlpha();
                    }
                    if ((transformType & 2) != 0) {
                        renderNode.setStaticMatrix(t.getMatrix());
                    }
                }
            }
            if (this.mTransformationInfo != null) {
                float alpha2 = alpha * getFinalAlpha();
                if (alpha2 < 1.0f) {
                    int multipliedAlpha = (int) (255.0f * alpha2);
                    if (onSetAlpha(multipliedAlpha)) {
                        alpha2 = 1.0f;
                    }
                }
                renderNode.setAlpha(alpha2);
            } else if (alpha < 1.0f) {
                renderNode.setAlpha(alpha);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean drawsWithRenderNode(Canvas canvas) {
        AttachInfo attachInfo = this.mAttachInfo;
        return attachInfo != null && attachInfo.mHardwareAccelerated && canvas.isHardwareAccelerated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:113:0x01c7  */
    /* JADX WARN: Removed duplicated region for block: B:114:0x01ca  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x01ce  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x0210  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x024b  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x0252  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0260  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x02e4  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x02f6  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x0338  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x033e  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x035f  */
    /* JADX WARN: Removed duplicated region for block: B:195:0x03a8  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0174  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x0177  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean draw(Canvas canvas, ViewGroup parent, long drawingTime) {
        Animation a;
        boolean more;
        boolean more2;
        Bitmap cache;
        int layerType;
        boolean drawingWithRenderNode;
        RenderNode renderNode;
        int sx;
        int sy;
        int restoreTo;
        float alpha;
        int transX;
        int transY;
        int restoreTo2;
        Bitmap cache2;
        float alpha2;
        float alpha3;
        int restoreTo3;
        Bitmap cache3;
        int sx2;
        int sy2;
        boolean z;
        Paint paint;
        Animation a2;
        boolean hardwareAcceleratedCanvas = canvas.isHardwareAccelerated();
        boolean drawingWithRenderNode2 = drawsWithRenderNode(canvas);
        boolean childHasIdentityMatrix = hasIdentityMatrix();
        int parentFlags = parent.mGroupFlags;
        if ((parentFlags & 256) != 0) {
            parent.getChildTransformation().clear();
            parent.mGroupFlags &= -257;
        }
        Transformation transformToApply = null;
        boolean concatMatrix = false;
        AttachInfo attachInfo = this.mAttachInfo;
        boolean scalingRequired = attachInfo != null && attachInfo.mScalingRequired;
        Animation a3 = getAnimation();
        if (a3 != null) {
            a = a3;
            more2 = true;
            boolean more3 = applyLegacyAnimation(parent, drawingTime, a, scalingRequired);
            concatMatrix = a.willChangeTransformationMatrix();
            if (concatMatrix) {
                this.mPrivateFlags3 |= 1;
            }
            transformToApply = parent.getChildTransformation();
            more = more3;
        } else {
            a = a3;
            more = false;
            more2 = true;
            if ((this.mPrivateFlags3 & 1) != 0) {
                this.mRenderNode.setAnimationMatrix(null);
                this.mPrivateFlags3 &= -2;
            }
            if (!drawingWithRenderNode2 && (parentFlags & 2048) != 0) {
                Transformation t = parent.getChildTransformation();
                boolean hasTransform = parent.getChildStaticTransformation(this, t);
                if (hasTransform) {
                    int transformType = t.getTransformationType();
                    transformToApply = transformType != 0 ? t : null;
                    concatMatrix = (transformType & 2) != 0;
                }
            }
        }
        boolean concatMatrix2 = concatMatrix | (!childHasIdentityMatrix);
        this.mPrivateFlags |= 32;
        if (concatMatrix2 || (parentFlags & 2049) != more2 || !canvas.quickReject(this.mLeft, this.mTop, this.mRight, this.mBottom) || (this.mPrivateFlags & 64) != 0) {
            this.mPrivateFlags2 &= -268435457;
            if (hardwareAcceleratedCanvas) {
                int i = this.mPrivateFlags;
                this.mRecreateDisplayList = (Integer.MIN_VALUE & i) != 0 ? more2 : false;
                this.mPrivateFlags = i & Integer.MAX_VALUE;
            }
            int layerType2 = getLayerType();
            if (layerType2 != more2 && drawingWithRenderNode2) {
                cache = null;
                layerType = layerType2;
            } else {
                if (layerType2 != 0) {
                    layerType2 = 1;
                    buildDrawingCache(more2);
                }
                Bitmap cache4 = getDrawingCache(more2);
                cache = cache4;
                layerType = layerType2;
            }
            if (!drawingWithRenderNode2) {
                drawingWithRenderNode = drawingWithRenderNode2;
                renderNode = null;
            } else {
                RenderNode renderNode2 = updateDisplayListIfDirty();
                if (renderNode2.hasDisplayList()) {
                    drawingWithRenderNode = drawingWithRenderNode2;
                    renderNode = renderNode2;
                } else {
                    drawingWithRenderNode = false;
                    renderNode = null;
                }
            }
            if (drawingWithRenderNode) {
                sx = 0;
                sy = 0;
            } else {
                computeScroll();
                int sx3 = this.mScrollX;
                int sy3 = this.mScrollY;
                sx = sx3;
                sy = sy3;
            }
            boolean drawingWithDrawingCache = (cache == null || drawingWithRenderNode) ? false : more2;
            boolean offsetForScroll = (cache != null || drawingWithRenderNode) ? false : more2;
            int restoreTo4 = -1;
            if (!drawingWithRenderNode || transformToApply != null) {
                restoreTo4 = canvas.save();
            }
            if (offsetForScroll) {
                canvas.translate(this.mLeft - sx, this.mTop - sy);
            } else {
                if (!drawingWithRenderNode) {
                    canvas.translate(this.mLeft, this.mTop);
                }
                if (scalingRequired) {
                    if (drawingWithRenderNode) {
                        restoreTo4 = canvas.save();
                    }
                    float scale = 1.0f / this.mAttachInfo.mApplicationScale;
                    canvas.scale(scale, scale);
                    restoreTo = restoreTo4;
                    alpha = !drawingWithRenderNode ? 1.0f : getAlpha() * getTransitionAlpha();
                    if (transformToApply == null || alpha < 1.0f || !hasIdentityMatrix() || (this.mPrivateFlags3 & 2) != 0) {
                        if (transformToApply == null || !childHasIdentityMatrix) {
                            transX = 0;
                            if (offsetForScroll) {
                                transY = 0;
                            } else {
                                transX = -sx;
                                transY = -sy;
                            }
                            if (transformToApply != null) {
                                restoreTo2 = restoreTo;
                                cache2 = cache;
                            } else {
                                if (!concatMatrix2) {
                                    restoreTo2 = restoreTo;
                                    cache2 = cache;
                                } else {
                                    if (drawingWithRenderNode) {
                                        restoreTo2 = restoreTo;
                                        renderNode.setAnimationMatrix(transformToApply.getMatrix());
                                        cache2 = cache;
                                    } else {
                                        restoreTo2 = restoreTo;
                                        int restoreTo5 = -transX;
                                        cache2 = cache;
                                        canvas.translate(restoreTo5, -transY);
                                        canvas.concat(transformToApply.getMatrix());
                                        canvas.translate(transX, transY);
                                    }
                                    parent.mGroupFlags |= 256;
                                }
                                float transformAlpha = transformToApply.getAlpha();
                                if (transformAlpha < 1.0f) {
                                    alpha *= transformAlpha;
                                    parent.mGroupFlags |= 256;
                                }
                            }
                            if (!childHasIdentityMatrix && !drawingWithRenderNode) {
                                canvas.translate(-transX, -transY);
                                canvas.concat(getMatrix());
                                canvas.translate(transX, transY);
                            }
                            alpha2 = alpha;
                        } else {
                            alpha2 = alpha;
                            restoreTo2 = restoreTo;
                            cache2 = cache;
                        }
                        if (alpha2 >= 1.0f || (this.mPrivateFlags3 & 2) != 0) {
                            if (alpha2 >= 1.0f) {
                                this.mPrivateFlags3 |= 2;
                            } else {
                                this.mPrivateFlags3 &= -3;
                            }
                            parent.mGroupFlags |= 256;
                            if (!drawingWithDrawingCache) {
                                alpha3 = alpha2;
                                restoreTo3 = restoreTo2;
                                cache3 = cache2;
                                sx2 = sx;
                                sy2 = sy;
                            } else {
                                int multipliedAlpha = (int) (alpha2 * 255.0f);
                                if (!onSetAlpha(multipliedAlpha)) {
                                    if (drawingWithRenderNode) {
                                        renderNode.setAlpha(getAlpha() * alpha2 * getTransitionAlpha());
                                        alpha3 = alpha2;
                                        restoreTo3 = restoreTo2;
                                        cache3 = cache2;
                                        sx2 = sx;
                                        sy2 = sy;
                                    } else if (layerType != 0) {
                                        alpha3 = alpha2;
                                        restoreTo3 = restoreTo2;
                                        cache3 = cache2;
                                        sx2 = sx;
                                        sy2 = sy;
                                    } else {
                                        alpha3 = alpha2;
                                        restoreTo3 = restoreTo2;
                                        sy2 = sy;
                                        sx2 = sx;
                                        cache3 = cache2;
                                        canvas.saveLayerAlpha(sx, sy, getWidth() + sx, sy + getHeight(), multipliedAlpha);
                                    }
                                } else {
                                    alpha3 = alpha2;
                                    restoreTo3 = restoreTo2;
                                    cache3 = cache2;
                                    sx2 = sx;
                                    sy2 = sy;
                                    this.mPrivateFlags |= 262144;
                                }
                            }
                        } else {
                            alpha3 = alpha2;
                            restoreTo3 = restoreTo2;
                            cache3 = cache2;
                            sx2 = sx;
                            sy2 = sy;
                        }
                        alpha = alpha3;
                    } else {
                        if ((this.mPrivateFlags & 262144) == 262144) {
                            onSetAlpha(255);
                            this.mPrivateFlags &= -262145;
                        }
                        restoreTo3 = restoreTo;
                        sy2 = sy;
                        sx2 = sx;
                        cache3 = cache;
                    }
                    if (!drawingWithRenderNode) {
                        z = false;
                    } else {
                        if ((parentFlags & 1) == 0 || cache3 != null) {
                            z = false;
                        } else if (offsetForScroll) {
                            canvas.clipRect(sx2, sy2, sx2 + getWidth(), sy2 + getHeight());
                            z = false;
                        } else {
                            if (!scalingRequired) {
                                z = false;
                            } else if (cache3 == null) {
                                z = false;
                            } else {
                                z = false;
                                canvas.clipRect(0, 0, cache3.getWidth(), cache3.getHeight());
                            }
                            canvas.clipRect(z ? 1 : 0, z ? 1 : 0, getWidth(), getHeight());
                        }
                        Rect rect = this.mClipBounds;
                        if (rect != null) {
                            canvas.clipRect(rect);
                        }
                    }
                    if (drawingWithDrawingCache) {
                        if (drawingWithRenderNode) {
                            this.mPrivateFlags = (-2097153) & this.mPrivateFlags;
                            ((RecordingCanvas) canvas).drawRenderNode(renderNode);
                        } else {
                            int i2 = this.mPrivateFlags;
                            if ((i2 & 128) == 128) {
                                this.mPrivateFlags = (-2097153) & i2;
                                dispatchDraw(canvas);
                            } else {
                                draw(canvas);
                            }
                        }
                    } else if (cache3 != null) {
                        this.mPrivateFlags = (-2097153) & this.mPrivateFlags;
                        if (layerType == 0 || (paint = this.mLayerPaint) == null) {
                            Paint cachePaint = parent.mCachePaint;
                            if (cachePaint == null) {
                                cachePaint = new Paint();
                                cachePaint.setDither(z);
                                parent.mCachePaint = cachePaint;
                            }
                            cachePaint.setAlpha((int) (alpha * 255.0f));
                            canvas.drawBitmap(cache3, 0.0f, 0.0f, cachePaint);
                        } else {
                            int layerPaintAlpha = paint.getAlpha();
                            if (alpha < 1.0f) {
                                this.mLayerPaint.setAlpha((int) (layerPaintAlpha * alpha));
                            }
                            canvas.drawBitmap(cache3, 0.0f, 0.0f, this.mLayerPaint);
                            if (alpha < 1.0f) {
                                this.mLayerPaint.setAlpha(layerPaintAlpha);
                            }
                        }
                    }
                    if (restoreTo3 >= 0) {
                        canvas.restoreToCount(restoreTo3);
                    }
                    a2 = a;
                    if (a2 != null && !more) {
                        if (!hardwareAcceleratedCanvas && !a2.getFillAfter()) {
                            onSetAlpha(255);
                        }
                        parent.finishAnimatingView(this, a2);
                    }
                    if (more && hardwareAcceleratedCanvas && a2.hasAlpha() && (this.mPrivateFlags & 262144) == 262144) {
                        invalidate(true);
                    }
                    this.mRecreateDisplayList = z;
                    return more;
                }
            }
            restoreTo = restoreTo4;
            if (!drawingWithRenderNode) {
            }
            if (transformToApply == null) {
            }
            if (transformToApply == null) {
            }
            transX = 0;
            if (offsetForScroll) {
            }
            if (transformToApply != null) {
            }
            if (!childHasIdentityMatrix) {
                canvas.translate(-transX, -transY);
                canvas.concat(getMatrix());
                canvas.translate(transX, transY);
            }
            alpha2 = alpha;
            if (alpha2 >= 1.0f) {
            }
            if (alpha2 >= 1.0f) {
            }
            parent.mGroupFlags |= 256;
            if (!drawingWithDrawingCache) {
            }
            alpha = alpha3;
            if (!drawingWithRenderNode) {
            }
            if (drawingWithDrawingCache) {
            }
            if (restoreTo3 >= 0) {
            }
            a2 = a;
            if (a2 != null) {
                if (!hardwareAcceleratedCanvas) {
                    onSetAlpha(255);
                }
                parent.finishAnimatingView(this, a2);
            }
            if (more) {
                invalidate(true);
            }
            this.mRecreateDisplayList = z;
            return more;
        }
        this.mPrivateFlags2 |= 268435456;
        return more;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Paint getDebugPaint() {
        if (sDebugPaint == null) {
            Paint paint = new Paint();
            sDebugPaint = paint;
            paint.setAntiAlias(false);
        }
        return sDebugPaint;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int dipsToPixels(int dips) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) ((dips * scale) + 0.5f);
    }

    private void debugDrawFocus(Canvas canvas) {
        if (isFocused()) {
            int cornerSquareSize = dipsToPixels(8);
            int l = this.mScrollX;
            int r = (this.mRight + l) - this.mLeft;
            int t = this.mScrollY;
            int b = (this.mBottom + t) - this.mTop;
            Paint paint = getDebugPaint();
            paint.setColor(DEBUG_CORNERS_COLOR);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(l, t, l + cornerSquareSize, t + cornerSquareSize, paint);
            canvas.drawRect(r - cornerSquareSize, t, r, t + cornerSquareSize, paint);
            canvas.drawRect(l, b - cornerSquareSize, l + cornerSquareSize, b, paint);
            canvas.drawRect(r - cornerSquareSize, b - cornerSquareSize, r, b, paint);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(l, t, r, b, paint);
            canvas.drawLine(l, b, r, t, paint);
        }
    }

    public void draw(Canvas canvas) {
        int paddingLeft;
        int bottom;
        boolean drawRight;
        float topFadeStrength;
        int length;
        int bottomSaveCount;
        int leftSaveCount;
        int bottomSaveCount2;
        int leftSaveCount2;
        int saveCount;
        int right;
        int bottomSaveCount3;
        int solidColor;
        int left;
        int bottom2;
        int rightSaveCount;
        int left2;
        float fadeHeight;
        int bottom3;
        int right2;
        int topSaveCount;
        int privateFlags = this.mPrivateFlags;
        this.mPrivateFlags = ((-2097153) & privateFlags) | 32;
        drawBackground(canvas);
        int viewFlags = this.mViewFlags;
        boolean horizontalEdges = (viewFlags & 4096) != 0;
        boolean verticalEdges = (viewFlags & 8192) != 0;
        if (!verticalEdges && !horizontalEdges) {
            onDraw(canvas);
            dispatchDraw(canvas);
            drawAutofilledHighlight(canvas);
            ViewOverlay viewOverlay = this.mOverlay;
            if (viewOverlay != null && !viewOverlay.isEmpty()) {
                this.mOverlay.getOverlayView().dispatchDraw(canvas);
            }
            onDrawForeground(canvas);
            drawDefaultFocusHighlight(canvas);
            if (isShowingLayoutBounds()) {
                debugDrawFocus(canvas);
                return;
            }
            return;
        }
        float bottomFadeStrength = 0.0f;
        float leftFadeStrength = 0.0f;
        float rightFadeStrength = 0.0f;
        int paddingLeft2 = this.mPaddingLeft;
        boolean offsetRequired = isPaddingOffsetRequired();
        if (!offsetRequired) {
            paddingLeft = paddingLeft2;
        } else {
            paddingLeft = paddingLeft2 + getLeftPaddingOffset();
        }
        int left3 = this.mScrollX + paddingLeft;
        boolean drawTop = false;
        int right3 = (((this.mRight + left3) - this.mLeft) - this.mPaddingRight) - paddingLeft;
        int top = this.mScrollY + getFadeTop(offsetRequired);
        int bottom4 = top + getFadeHeight(offsetRequired);
        if (!offsetRequired) {
            bottom = bottom4;
        } else {
            right3 += getRightPaddingOffset();
            bottom = bottom4 + getBottomPaddingOffset();
        }
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        boolean drawBottom = false;
        float fadeHeight2 = scrollabilityCache.fadingEdgeLength;
        boolean drawLeft = false;
        int length2 = (int) fadeHeight2;
        if (verticalEdges) {
            drawRight = false;
            topFadeStrength = 0.0f;
            if (top + length2 > bottom - length2) {
                length2 = (bottom - top) / 2;
            }
        } else {
            drawRight = false;
            topFadeStrength = 0.0f;
        }
        if (horizontalEdges && left3 + length2 > right3 - length2) {
            length = (right3 - left3) / 2;
        } else {
            length = length2;
        }
        if (verticalEdges) {
            float topFadeStrength2 = Math.max(0.0f, Math.min(1.0f, getTopFadingEdgeStrength()));
            drawTop = topFadeStrength2 * fadeHeight2 > 1.0f;
            topFadeStrength = topFadeStrength2;
            float topFadeStrength3 = getBottomFadingEdgeStrength();
            bottomFadeStrength = Math.max(0.0f, Math.min(1.0f, topFadeStrength3));
            drawBottom = bottomFadeStrength * fadeHeight2 > 1.0f;
        }
        if (horizontalEdges) {
            leftFadeStrength = Math.max(0.0f, Math.min(1.0f, getLeftFadingEdgeStrength()));
            boolean drawLeft2 = leftFadeStrength * fadeHeight2 > 1.0f;
            rightFadeStrength = Math.max(0.0f, Math.min(1.0f, getRightFadingEdgeStrength()));
            drawRight = rightFadeStrength * fadeHeight2 > 1.0f;
            drawLeft = drawLeft2;
        }
        int saveCount2 = canvas.getSaveCount();
        int topSaveCount2 = -1;
        int bottomSaveCount4 = -1;
        int leftSaveCount3 = -1;
        int solidColor2 = getSolidColor();
        if (solidColor2 != 0) {
            scrollabilityCache.setFadeColor(solidColor2);
            bottomSaveCount = -1;
            leftSaveCount = -1;
            bottomSaveCount2 = -1;
            leftSaveCount2 = saveCount2;
            saveCount = -1;
        } else {
            if (drawTop) {
                int topSaveCount3 = top + length;
                topSaveCount2 = canvas.saveUnclippedLayer(left3, top, right3, topSaveCount3);
            }
            if (!drawBottom) {
                topSaveCount = topSaveCount2;
            } else {
                topSaveCount = topSaveCount2;
                int topSaveCount4 = bottom - length;
                bottomSaveCount4 = canvas.saveUnclippedLayer(left3, topSaveCount4, right3, bottom);
            }
            if (drawLeft) {
                leftSaveCount3 = canvas.saveUnclippedLayer(left3, top, left3 + length, bottom);
            }
            if (!drawRight) {
                bottomSaveCount = bottomSaveCount4;
                leftSaveCount = leftSaveCount3;
                bottomSaveCount2 = topSaveCount;
                leftSaveCount2 = saveCount2;
                saveCount = -1;
            } else {
                int rightSaveCount2 = canvas.saveUnclippedLayer(right3 - length, top, right3, bottom);
                bottomSaveCount = bottomSaveCount4;
                leftSaveCount = leftSaveCount3;
                bottomSaveCount2 = topSaveCount;
                leftSaveCount2 = saveCount2;
                saveCount = rightSaveCount2;
            }
        }
        onDraw(canvas);
        dispatchDraw(canvas);
        int topSaveCount5 = bottomSaveCount2;
        Paint p = scrollabilityCache.paint;
        float bottomFadeStrength2 = bottomFadeStrength;
        Matrix matrix = scrollabilityCache.matrix;
        int leftSaveCount4 = leftSaveCount;
        Shader fade = scrollabilityCache.shader;
        if (!drawRight) {
            right = right3;
            bottomSaveCount3 = bottomSaveCount;
            solidColor = solidColor2;
            left = bottom;
            bottom2 = 1065353216;
            rightSaveCount = top;
            left2 = left3;
            fadeHeight = fadeHeight2;
        } else {
            matrix.setScale(1.0f, fadeHeight2 * rightFadeStrength);
            matrix.postRotate(90.0f);
            matrix.postTranslate(right3, top);
            fade.setLocalMatrix(matrix);
            p.setShader(fade);
            if (solidColor2 != 0) {
                int bottomSaveCount5 = bottomSaveCount;
                float f = bottom;
                rightSaveCount = top;
                left2 = left3;
                right = right3;
                fadeHeight = fadeHeight2;
                bottomSaveCount3 = bottomSaveCount5;
                solidColor = solidColor2;
                left = bottom;
                bottom2 = 1065353216;
                canvas.drawRect(right3 - length, top, right3, f, p);
            } else {
                canvas.restoreUnclippedLayer(saveCount, p);
                right = right3;
                fadeHeight = fadeHeight2;
                bottomSaveCount3 = bottomSaveCount;
                solidColor = solidColor2;
                left2 = left3;
                rightSaveCount = top;
                left = bottom;
                bottom2 = 1065353216;
            }
        }
        if (!drawLeft) {
            bottom3 = left;
        } else {
            matrix.setScale(bottom2, fadeHeight * leftFadeStrength);
            matrix.postRotate(-90.0f);
            matrix.postTranslate(left2, rightSaveCount);
            fade.setLocalMatrix(matrix);
            p.setShader(fade);
            if (solidColor == 0) {
                canvas.restoreUnclippedLayer(leftSaveCount4, p);
                bottom3 = left;
            } else {
                int bottom5 = left;
                bottom3 = bottom5;
                leftSaveCount4 = leftSaveCount4;
                canvas.drawRect(left2, rightSaveCount, left2 + length, bottom5, p);
            }
        }
        if (drawBottom) {
            matrix.setScale(bottom2, fadeHeight * bottomFadeStrength2);
            matrix.postRotate(180.0f);
            int bottom6 = bottom3;
            matrix.postTranslate(left2, bottom6);
            fade.setLocalMatrix(matrix);
            p.setShader(fade);
            if (solidColor == 0) {
                canvas.restoreUnclippedLayer(bottomSaveCount3, p);
                right2 = right;
            } else {
                int right4 = right;
                right2 = right4;
                canvas.drawRect(left2, bottom6 - length, right4, bottom6, p);
            }
        } else {
            right2 = right;
        }
        if (drawTop) {
            matrix.setScale(1.0f, fadeHeight * topFadeStrength);
            matrix.postTranslate(left2, rightSaveCount);
            fade.setLocalMatrix(matrix);
            p.setShader(fade);
            if (solidColor == 0) {
                canvas.restoreUnclippedLayer(topSaveCount5, p);
            } else {
                canvas.drawRect(left2, rightSaveCount, right2, rightSaveCount + length, p);
            }
        }
        canvas.restoreToCount(leftSaveCount2);
        drawAutofilledHighlight(canvas);
        ViewOverlay viewOverlay2 = this.mOverlay;
        if (viewOverlay2 != null && !viewOverlay2.isEmpty()) {
            this.mOverlay.getOverlayView().dispatchDraw(canvas);
        }
        onDrawForeground(canvas);
        drawDefaultFocusHighlight(canvas);
        if (isShowingLayoutBounds()) {
            debugDrawFocus(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        AttachInfo attachInfo;
        Drawable background = this.mBackground;
        if (background == null) {
            return;
        }
        setBackgroundBounds();
        if (canvas.isHardwareAccelerated() && (attachInfo = this.mAttachInfo) != null && attachInfo.mThreadedRenderer != null) {
            this.mBackgroundRenderNode = getDrawableRenderNode(background, this.mBackgroundRenderNode);
            RenderNode renderNode = this.mBackgroundRenderNode;
            if (renderNode != null && renderNode.hasDisplayList()) {
                setBackgroundRenderNodeProperties(renderNode);
                ((RecordingCanvas) canvas).drawRenderNode(renderNode);
                return;
            }
        }
        int scrollX = this.mScrollX;
        int scrollY = this.mScrollY;
        if ((scrollX | scrollY) == 0) {
            background.draw(canvas);
            return;
        }
        canvas.translate(scrollX, scrollY);
        background.draw(canvas);
        canvas.translate(-scrollX, -scrollY);
    }

    void setBackgroundBounds() {
        Drawable drawable;
        if (this.mBackgroundSizeChanged && (drawable = this.mBackground) != null) {
            drawable.setBounds(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
            this.mBackgroundSizeChanged = false;
            rebuildOutline();
        }
    }

    private void setBackgroundRenderNodeProperties(RenderNode renderNode) {
        renderNode.setTranslationX(this.mScrollX);
        renderNode.setTranslationY(this.mScrollY);
    }

    private RenderNode getDrawableRenderNode(Drawable drawable, RenderNode renderNode) {
        if (renderNode == null) {
            renderNode = RenderNode.create(drawable.getClass().getName(), new ViewAnimationHostBridge(this));
            renderNode.setUsageHint(1);
        }
        Rect bounds = drawable.getBounds();
        int width = bounds.width();
        int height = bounds.height();
        renderNode.clearStretch();
        RecordingCanvas canvas = renderNode.beginRecording(width, height);
        canvas.translate(-bounds.left, -bounds.top);
        try {
            drawable.draw(canvas);
            renderNode.endRecording();
            renderNode.setLeftTopRightBottom(bounds.left, bounds.top, bounds.right, bounds.bottom);
            renderNode.setProjectBackwards(drawable.isProjected());
            renderNode.setProjectionReceiver(true);
            renderNode.setClipToBounds(false);
            return renderNode;
        } catch (Throwable th) {
            renderNode.endRecording();
            throw th;
        }
    }

    public ViewOverlay getOverlay() {
        if (this.mOverlay == null) {
            this.mOverlay = new ViewOverlay(this.mContext, this);
        }
        return this.mOverlay;
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public int getSolidColor() {
        return 0;
    }

    private static String printFlags(int flags) {
        String output = "";
        int numFlags = 0;
        if ((flags & 1) == 1) {
            output = "TAKES_FOCUS";
            numFlags = 0 + 1;
        }
        switch (flags & 12) {
            case 4:
                if (numFlags > 0) {
                    output = output + " ";
                }
                return output + "INVISIBLE";
            case 8:
                if (numFlags > 0) {
                    output = output + " ";
                }
                return output + "GONE";
            default:
                return output;
        }
    }

    private static String printPrivateFlags(int privateFlags) {
        String output = "";
        int numFlags = 0;
        if ((privateFlags & 1) == 1) {
            output = "WANTS_FOCUS";
            numFlags = 0 + 1;
        }
        if ((privateFlags & 2) == 2) {
            if (numFlags > 0) {
                output = output + " ";
            }
            output = output + "FOCUSED";
            numFlags++;
        }
        if ((privateFlags & 4) == 4) {
            if (numFlags > 0) {
                output = output + " ";
            }
            output = output + "SELECTED";
            numFlags++;
        }
        if ((privateFlags & 8) == 8) {
            if (numFlags > 0) {
                output = output + " ";
            }
            output = output + "IS_ROOT_NAMESPACE";
            numFlags++;
        }
        if ((privateFlags & 16) == 16) {
            if (numFlags > 0) {
                output = output + " ";
            }
            output = output + "HAS_BOUNDS";
            numFlags++;
        }
        if ((privateFlags & 32) == 32) {
            if (numFlags > 0) {
                output = output + " ";
            }
            return output + "DRAWN";
        }
        return output;
    }

    public boolean isLayoutRequested() {
        return (this.mPrivateFlags & 4096) == 4096;
    }

    public static boolean isLayoutModeOptical(Object o) {
        return (o instanceof ViewGroup) && ((ViewGroup) o).isLayoutModeOptical();
    }

    public static void setTraceLayoutSteps(boolean traceLayoutSteps) {
        sTraceLayoutSteps = traceLayoutSteps;
    }

    public static void setTracedRequestLayoutClassClass(String s) {
        sTraceRequestLayoutClass = s;
    }

    private boolean setOpticalFrame(int left, int top, int right, int bottom) {
        ViewParent viewParent = this.mParent;
        Insets parentInsets = viewParent instanceof View ? ((View) viewParent).getOpticalInsets() : Insets.NONE;
        Insets childInsets = getOpticalInsets();
        return setFrame((parentInsets.left + left) - childInsets.left, (parentInsets.top + top) - childInsets.top, parentInsets.left + right + childInsets.right, parentInsets.top + bottom + childInsets.bottom);
    }

    public void layout(int l, int t, int r, int b) {
        View view;
        if ((this.mPrivateFlags3 & 8) != 0) {
            if (isTraversalTracingEnabled()) {
                Trace.beginSection(this.mTracingStrings.onMeasureBeforeLayout);
            }
            onMeasure(this.mOldWidthMeasureSpec, this.mOldHeightMeasureSpec);
            if (isTraversalTracingEnabled()) {
                Trace.endSection();
            }
            this.mPrivateFlags3 &= -9;
        }
        int oldL = this.mLeft;
        int oldT = this.mTop;
        int oldB = this.mBottom;
        int oldR = this.mRight;
        boolean changed = isLayoutModeOptical(this.mParent) ? setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);
        View view2 = null;
        if (!changed && (this.mPrivateFlags & 8192) != 8192) {
            view = null;
        } else {
            if (isTraversalTracingEnabled()) {
                Trace.beginSection(this.mTracingStrings.onLayout);
            }
            onLayout(changed, l, t, r, b);
            if (isTraversalTracingEnabled()) {
                Trace.endSection();
            }
            if (shouldDrawRoundScrollbar()) {
                if (this.mRoundScrollbarRenderer == null) {
                    this.mRoundScrollbarRenderer = new RoundScrollbarRenderer(this);
                }
            } else {
                this.mRoundScrollbarRenderer = null;
            }
            this.mPrivateFlags &= -8193;
            ListenerInfo li = this.mListenerInfo;
            if (li == null || li.mOnLayoutChangeListeners == null) {
                view = null;
            } else {
                ArrayList<OnLayoutChangeListener> listenersCopy = (ArrayList) li.mOnLayoutChangeListeners.clone();
                int numListeners = listenersCopy.size();
                int i = 0;
                while (i < numListeners) {
                    int numListeners2 = numListeners;
                    int numListeners3 = oldL;
                    listenersCopy.get(i).onLayoutChange(this, l, t, r, b, numListeners3, oldT, oldR, oldB);
                    i++;
                    view2 = view2;
                    numListeners = numListeners2;
                    listenersCopy = listenersCopy;
                    li = li;
                    oldL = oldL;
                }
                view = view2;
            }
        }
        boolean wasLayoutValid = isLayoutValid();
        this.mPrivateFlags &= -4097;
        this.mPrivateFlags3 |= 4;
        if (!wasLayoutValid && isFocused()) {
            this.mPrivateFlags &= -2;
            if (canTakeFocus()) {
                clearParentsWantFocus();
            } else if (getViewRootImpl() == null || !getViewRootImpl().isInLayout()) {
                clearFocusInternal(view, true, false);
                clearParentsWantFocus();
            } else if (!hasParentWantsFocus()) {
                clearFocusInternal(view, true, false);
            }
        } else {
            int i2 = this.mPrivateFlags;
            if ((i2 & 1) != 0) {
                this.mPrivateFlags = i2 & (-2);
                View focused = findFocus();
                if (focused != null && !restoreDefaultFocus() && !hasParentWantsFocus()) {
                    focused.clearFocusInternal(view, true, false);
                }
            }
        }
        int i3 = this.mPrivateFlags3;
        if ((134217728 & i3) != 0) {
            this.mPrivateFlags3 = i3 & (-134217729);
            notifyEnterOrExitForAutoFillIfNeeded(true);
        }
        notifyAppearedOrDisappearedForContentCaptureIfNeeded(true);
    }

    private boolean hasParentWantsFocus() {
        ViewParent parent = this.mParent;
        while (parent instanceof ViewGroup) {
            ViewGroup pv = (ViewGroup) parent;
            if ((pv.mPrivateFlags & 1) != 0) {
                return true;
            }
            parent = pv.mParent;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean setFrame(int left, int top, int right, int bottom) {
        boolean changed = false;
        int i = this.mLeft;
        if (i != left || this.mRight != right || this.mTop != top || this.mBottom != bottom) {
            changed = true;
            int drawn = this.mPrivateFlags & 32;
            int oldWidth = this.mRight - i;
            int oldHeight = this.mBottom - this.mTop;
            int newWidth = right - left;
            int newHeight = bottom - top;
            boolean sizeChanged = (newWidth == oldWidth && newHeight == oldHeight) ? false : true;
            invalidate(sizeChanged);
            this.mLeft = left;
            this.mTop = top;
            this.mRight = right;
            this.mBottom = bottom;
            this.mRenderNode.setLeftTopRightBottom(left, top, right, bottom);
            this.mPrivateFlags |= 16;
            if (sizeChanged) {
                sizeChange(newWidth, newHeight, oldWidth, oldHeight);
            }
            if ((this.mViewFlags & 12) == 0 || this.mGhostView != null) {
                this.mPrivateFlags |= 32;
                invalidate(sizeChanged);
                invalidateParentCaches();
            }
            this.mPrivateFlags |= drawn;
            this.mBackgroundSizeChanged = true;
            this.mDefaultFocusHighlightSizeChanged = true;
            ForegroundInfo foregroundInfo = this.mForegroundInfo;
            if (foregroundInfo != null) {
                foregroundInfo.mBoundsChanged = true;
            }
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
        return changed;
    }

    public final void setLeftTopRightBottom(int left, int top, int right, int bottom) {
        setFrame(left, top, right, bottom);
    }

    private void sizeChange(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        ViewOverlay viewOverlay = this.mOverlay;
        if (viewOverlay != null) {
            viewOverlay.getOverlayView().setRight(newWidth);
            this.mOverlay.getOverlayView().setBottom(newHeight);
        }
        if (!sCanFocusZeroSized && isLayoutValid()) {
            ViewParent viewParent = this.mParent;
            if (!(viewParent instanceof ViewGroup) || !((ViewGroup) viewParent).isLayoutSuppressed()) {
                if (newWidth <= 0 || newHeight <= 0) {
                    if (hasFocus()) {
                        clearFocus();
                        ViewParent viewParent2 = this.mParent;
                        if (viewParent2 instanceof ViewGroup) {
                            ((ViewGroup) viewParent2).clearFocusedInCluster();
                        }
                    }
                    clearAccessibilityFocus();
                } else if ((oldWidth <= 0 || oldHeight <= 0) && this.mParent != null && canTakeFocus()) {
                    this.mParent.focusableViewAvailable(this);
                }
            }
        }
        rebuildOutline();
        if (onCheckIsTextEditor() || this.mHandwritingDelegatorCallback != null) {
            setHandwritingArea(new Rect(0, 0, newWidth, newHeight));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFinishInflate() {
    }

    public Resources getResources() {
        return this.mResources;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (verifyDrawable(drawable)) {
            Rect dirty = drawable.getDirtyBounds();
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            invalidate(dirty.left + scrollX, dirty.top + scrollY, dirty.right + scrollX, dirty.bottom + scrollY);
            rebuildOutline();
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (verifyDrawable(who) && what != null) {
            long delay = when - SystemClock.uptimeMillis();
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null) {
                attachInfo.mViewRootImpl.mChoreographer.postCallbackDelayed(1, what, who, Choreographer.subtractFrameDelay(delay));
            } else {
                getRunQueue().postDelayed(what, delay);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (verifyDrawable(who) && what != null) {
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null) {
                attachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, what, who);
            }
            getRunQueue().removeCallbacks(what);
        }
    }

    public void unscheduleDrawable(Drawable who) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && who != null) {
            attachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, null, who);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resolveDrawables() {
        if (!isLayoutDirectionResolved() && getRawLayoutDirection() == 2) {
            return;
        }
        int layoutDirection = isLayoutDirectionResolved() ? getLayoutDirection() : getRawLayoutDirection();
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setLayoutDirection(layoutDirection);
        }
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        if (foregroundInfo != null && foregroundInfo.mDrawable != null) {
            this.mForegroundInfo.mDrawable.setLayoutDirection(layoutDirection);
        }
        Drawable drawable2 = this.mDefaultFocusHighlight;
        if (drawable2 != null) {
            drawable2.setLayoutDirection(layoutDirection);
        }
        this.mPrivateFlags2 |= 1073741824;
        onResolveDrawables(layoutDirection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean areDrawablesResolved() {
        return (this.mPrivateFlags2 & 1073741824) == 1073741824;
    }

    public void onResolveDrawables(int layoutDirection) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetResolvedDrawables() {
        resetResolvedDrawablesInternal();
    }

    void resetResolvedDrawablesInternal() {
        this.mPrivateFlags2 &= -1073741825;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        ForegroundInfo foregroundInfo;
        return who == this.mBackground || ((foregroundInfo = this.mForegroundInfo) != null && foregroundInfo.mDrawable == who) || this.mDefaultFocusHighlight == who;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void drawableStateChanged() {
        Drawable scrollBar;
        int[] state = getDrawableState();
        boolean changed = false;
        Drawable bg = this.mBackground;
        if (bg != null && bg.isStateful()) {
            changed = false | bg.setState(state);
        }
        Drawable hl = this.mDefaultFocusHighlight;
        if (hl != null && hl.isStateful()) {
            changed |= hl.setState(state);
        }
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        Drawable fg = foregroundInfo != null ? foregroundInfo.mDrawable : null;
        if (fg != null && fg.isStateful()) {
            changed |= fg.setState(state);
        }
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache != null && (scrollBar = scrollabilityCache.scrollBar) != null && scrollBar.isStateful()) {
            changed |= scrollBar.setState(state) && this.mScrollCache.state != 0;
        }
        StateListAnimator stateListAnimator = this.mStateListAnimator;
        if (stateListAnimator != null) {
            stateListAnimator.setState(state);
        }
        if (!isAggregatedVisible()) {
            jumpDrawablesToCurrentState();
        }
        if (changed) {
            invalidate();
        }
    }

    public void drawableHotspotChanged(float x, float y) {
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setHotspot(x, y);
        }
        Drawable drawable2 = this.mDefaultFocusHighlight;
        if (drawable2 != null) {
            drawable2.setHotspot(x, y);
        }
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        if (foregroundInfo != null && foregroundInfo.mDrawable != null) {
            this.mForegroundInfo.mDrawable.setHotspot(x, y);
        }
        dispatchDrawableHotspotChanged(x, y);
    }

    public void dispatchDrawableHotspotChanged(float x, float y) {
    }

    public void refreshDrawableState() {
        this.mPrivateFlags |= 1024;
        drawableStateChanged();
        ViewParent parent = this.mParent;
        if (parent != null) {
            parent.childDrawableStateChanged(this);
        }
    }

    private Drawable getDefaultFocusHighlightDrawable() {
        Context context;
        if (this.mDefaultFocusHighlightCache == null && (context = this.mContext) != null) {
            int[] attrs = {16843534};
            TypedArray ta = context.obtainStyledAttributes(attrs);
            this.mDefaultFocusHighlightCache = ta.getDrawable(0);
            ta.recycle();
        }
        return this.mDefaultFocusHighlightCache;
    }

    private void setDefaultFocusHighlight(Drawable highlight) {
        ForegroundInfo foregroundInfo;
        this.mDefaultFocusHighlight = highlight;
        boolean z = true;
        this.mDefaultFocusHighlightSizeChanged = true;
        if (highlight != null) {
            int i = this.mPrivateFlags;
            if ((i & 128) != 0) {
                this.mPrivateFlags = i & PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
            }
            highlight.setLayoutDirection(getLayoutDirection());
            if (highlight.isStateful()) {
                highlight.setState(getDrawableState());
            }
            if (isAttachedToWindow()) {
                if (getWindowVisibility() != 0 || !isShown()) {
                    z = false;
                }
                highlight.setVisible(z, false);
            }
            highlight.setCallback(this);
        } else if ((this.mViewFlags & 128) != 0 && this.mBackground == null && ((foregroundInfo = this.mForegroundInfo) == null || foregroundInfo.mDrawable == null)) {
            this.mPrivateFlags |= 128;
        }
        invalidate();
    }

    public boolean isDefaultFocusHighlightNeeded(Drawable background, Drawable foreground) {
        boolean lackFocusState = ((background != null && background.isStateful() && background.hasFocusStateSpecified()) || (foreground != null && foreground.isStateful() && foreground.hasFocusStateSpecified())) ? false : true;
        return !isInTouchMode() && getDefaultFocusHighlightEnabled() && lackFocusState && isAttachedToWindow() && sUseDefaultFocusHighlight;
    }

    private void switchDefaultFocusHighlight() {
        if (isFocused()) {
            Drawable drawable = this.mBackground;
            ForegroundInfo foregroundInfo = this.mForegroundInfo;
            boolean needed = isDefaultFocusHighlightNeeded(drawable, foregroundInfo == null ? null : foregroundInfo.mDrawable);
            boolean active = this.mDefaultFocusHighlight != null;
            if (needed && !active) {
                setDefaultFocusHighlight(getDefaultFocusHighlightDrawable());
            } else if (!needed && active) {
                setDefaultFocusHighlight(null);
            }
        }
    }

    private void drawDefaultFocusHighlight(Canvas canvas) {
        if (this.mDefaultFocusHighlight != null && isFocused()) {
            if (this.mDefaultFocusHighlightSizeChanged) {
                this.mDefaultFocusHighlightSizeChanged = false;
                int l = this.mScrollX;
                int r = (this.mRight + l) - this.mLeft;
                int t = this.mScrollY;
                int b = (this.mBottom + t) - this.mTop;
                this.mDefaultFocusHighlight.setBounds(l, t, r, b);
            }
            this.mDefaultFocusHighlight.draw(canvas);
        }
    }

    public final int[] getDrawableState() {
        int[] iArr = this.mDrawableState;
        if (iArr != null && (this.mPrivateFlags & 1024) == 0) {
            return iArr;
        }
        int[] onCreateDrawableState = onCreateDrawableState(0);
        this.mDrawableState = onCreateDrawableState;
        this.mPrivateFlags &= -1025;
        return onCreateDrawableState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int[] onCreateDrawableState(int extraSpace) {
        int i = this.mViewFlags;
        if ((i & 4194304) == 4194304) {
            ViewParent viewParent = this.mParent;
            if (viewParent instanceof View) {
                return ((View) viewParent).onCreateDrawableState(extraSpace);
            }
        }
        int privateFlags = this.mPrivateFlags;
        int viewStateIndex = (privateFlags & 16384) != 0 ? 0 | 16 : 0;
        if ((i & 32) == 0) {
            viewStateIndex |= 8;
        }
        if (isFocused() && hasWindowFocus()) {
            viewStateIndex |= 4;
        }
        if ((privateFlags & 4) != 0) {
            viewStateIndex |= 2;
        }
        if (hasWindowFocus()) {
            viewStateIndex |= 1;
        }
        if ((1073741824 & privateFlags) != 0) {
            viewStateIndex |= 32;
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && attachInfo.mHardwareAccelerationRequested) {
            viewStateIndex |= 64;
        }
        if ((268435456 & privateFlags) != 0) {
            viewStateIndex |= 128;
        }
        int privateFlags2 = this.mPrivateFlags2;
        if ((privateFlags2 & 1) != 0) {
            viewStateIndex |= 256;
        }
        if ((privateFlags2 & 2) != 0) {
            viewStateIndex |= 512;
        }
        int[] drawableState = StateSet.get(viewStateIndex);
        if (extraSpace == 0) {
            return drawableState;
        }
        if (drawableState != null) {
            int[] fullState = new int[drawableState.length + extraSpace];
            System.arraycopy(drawableState, 0, fullState, 0, drawableState.length);
            return fullState;
        }
        return new int[extraSpace];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int[] mergeDrawableStates(int[] baseState, int[] additionalState) {
        int N = baseState.length;
        int i = N - 1;
        while (i >= 0 && baseState[i] == 0) {
            i--;
        }
        System.arraycopy(additionalState, 0, baseState, i + 1, additionalState.length);
        return baseState;
    }

    public void jumpDrawablesToCurrentState() {
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
        StateListAnimator stateListAnimator = this.mStateListAnimator;
        if (stateListAnimator != null) {
            stateListAnimator.jumpToCurrentState();
        }
        Drawable drawable2 = this.mDefaultFocusHighlight;
        if (drawable2 != null) {
            drawable2.jumpToCurrentState();
        }
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        if (foregroundInfo != null && foregroundInfo.mDrawable != null) {
            this.mForegroundInfo.mDrawable.jumpToCurrentState();
        }
    }

    @RemotableViewMethod
    public void setBackgroundColor(int color) {
        Drawable drawable = this.mBackground;
        if (drawable instanceof ColorDrawable) {
            ((ColorDrawable) drawable.mutate()).setColor(color);
            computeOpaqueFlags();
            this.mBackgroundResource = 0;
            return;
        }
        setBackground(new ColorDrawable(color));
    }

    @RemotableViewMethod
    public void setBackgroundResource(int resid) {
        if (resid != 0 && resid == this.mBackgroundResource) {
            return;
        }
        Drawable d = null;
        if (resid != 0) {
            d = this.mContext.getDrawable(resid);
        }
        setBackground(d);
        this.mBackgroundResource = resid;
    }

    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        ForegroundInfo foregroundInfo;
        boolean z;
        computeOpaqueFlags();
        Drawable drawable = this.mBackground;
        if (background == drawable) {
            return;
        }
        boolean requestLayout = false;
        this.mBackgroundResource = 0;
        if (drawable != null) {
            if (isAttachedToWindow()) {
                this.mBackground.setVisible(false, false);
            }
            this.mBackground.setCallback(null);
            unscheduleDrawable(this.mBackground);
        }
        if (background != null) {
            ThreadLocal<Rect> threadLocal = sThreadLocal;
            Rect padding = threadLocal.get();
            if (padding == null) {
                padding = new Rect();
                threadLocal.set(padding);
            }
            resetResolvedDrawablesInternal();
            background.setLayoutDirection(getLayoutDirection());
            if (background.getPadding(padding)) {
                resetResolvedPaddingInternal();
                switch (background.getLayoutDirection()) {
                    case 1:
                        this.mUserPaddingLeftInitial = padding.right;
                        this.mUserPaddingRightInitial = padding.left;
                        internalSetPadding(padding.right, padding.top, padding.left, padding.bottom);
                        break;
                    default:
                        this.mUserPaddingLeftInitial = padding.left;
                        this.mUserPaddingRightInitial = padding.right;
                        internalSetPadding(padding.left, padding.top, padding.right, padding.bottom);
                        break;
                }
                this.mLeftPaddingDefined = false;
                this.mRightPaddingDefined = false;
            }
            Drawable drawable2 = this.mBackground;
            requestLayout = (drawable2 != null && drawable2.getMinimumHeight() == background.getMinimumHeight() && this.mBackground.getMinimumWidth() == background.getMinimumWidth()) ? true : true;
            this.mBackground = background;
            if (background.isStateful()) {
                background.setState(getDrawableState());
            }
            if (isAttachedToWindow()) {
                if (getWindowVisibility() != 0 || !isShown()) {
                    z = false;
                } else {
                    z = true;
                }
                background.setVisible(z, false);
            }
            applyBackgroundTint();
            background.setCallback(this);
            int i = this.mPrivateFlags;
            if ((i & 128) != 0) {
                this.mPrivateFlags = i & PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
                requestLayout = true;
            }
        } else {
            this.mBackground = null;
            if ((this.mViewFlags & 128) != 0 && this.mDefaultFocusHighlight == null && ((foregroundInfo = this.mForegroundInfo) == null || foregroundInfo.mDrawable == null)) {
                this.mPrivateFlags |= 128;
            }
            requestLayout = true;
        }
        computeOpaqueFlags();
        if (requestLayout) {
            requestLayout();
        }
        this.mBackgroundSizeChanged = true;
        invalidate(true);
        invalidateOutline();
    }

    public Drawable getBackground() {
        return this.mBackground;
    }

    @RemotableViewMethod
    public void setBackgroundTintList(ColorStateList tint) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintList = tint;
        this.mBackgroundTint.mHasTintList = true;
        applyBackgroundTint();
    }

    public ColorStateList getBackgroundTintList() {
        TintInfo tintInfo = this.mBackgroundTint;
        if (tintInfo != null) {
            return tintInfo.mTintList;
        }
        return null;
    }

    public void setBackgroundTintMode(PorterDuff.Mode tintMode) {
        BlendMode mode = null;
        if (tintMode != null) {
            mode = BlendMode.fromValue(tintMode.nativeInt);
        }
        setBackgroundTintBlendMode(mode);
    }

    @RemotableViewMethod
    public void setBackgroundTintBlendMode(BlendMode blendMode) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mBlendMode = blendMode;
        this.mBackgroundTint.mHasTintMode = true;
        applyBackgroundTint();
    }

    public PorterDuff.Mode getBackgroundTintMode() {
        TintInfo tintInfo = this.mBackgroundTint;
        if (tintInfo != null && tintInfo.mBlendMode != null) {
            PorterDuff.Mode porterDuffMode = BlendMode.blendModeToPorterDuffMode(this.mBackgroundTint.mBlendMode);
            return porterDuffMode;
        }
        return null;
    }

    public BlendMode getBackgroundTintBlendMode() {
        TintInfo tintInfo = this.mBackgroundTint;
        if (tintInfo != null) {
            return tintInfo.mBlendMode;
        }
        return null;
    }

    private void applyBackgroundTint() {
        if (this.mBackground != null && this.mBackgroundTint != null) {
            TintInfo tintInfo = this.mBackgroundTint;
            if (tintInfo.mHasTintList || tintInfo.mHasTintMode) {
                this.mBackground = this.mBackground.mutate();
                if (tintInfo.mHasTintList) {
                    this.mBackground.setTintList(tintInfo.mTintList);
                }
                if (tintInfo.mHasTintMode) {
                    this.mBackground.setTintBlendMode(tintInfo.mBlendMode);
                }
                if (this.mBackground.isStateful()) {
                    this.mBackground.setState(getDrawableState());
                }
            }
        }
    }

    public Drawable getForeground() {
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        if (foregroundInfo != null) {
            return foregroundInfo.mDrawable;
        }
        return null;
    }

    public void setForeground(Drawable foreground) {
        if (this.mForegroundInfo == null) {
            if (foreground == null) {
                return;
            }
            this.mForegroundInfo = new ForegroundInfo();
        }
        if (foreground == this.mForegroundInfo.mDrawable) {
            return;
        }
        if (this.mForegroundInfo.mDrawable != null) {
            if (isAttachedToWindow()) {
                this.mForegroundInfo.mDrawable.setVisible(false, false);
            }
            this.mForegroundInfo.mDrawable.setCallback(null);
            unscheduleDrawable(this.mForegroundInfo.mDrawable);
        }
        this.mForegroundInfo.mDrawable = foreground;
        boolean z = true;
        this.mForegroundInfo.mBoundsChanged = true;
        if (foreground != null) {
            int i = this.mPrivateFlags;
            if ((i & 128) != 0) {
                this.mPrivateFlags = i & PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
            }
            foreground.setLayoutDirection(getLayoutDirection());
            if (foreground.isStateful()) {
                foreground.setState(getDrawableState());
            }
            applyForegroundTint();
            if (isAttachedToWindow()) {
                if (getWindowVisibility() != 0 || !isShown()) {
                    z = false;
                }
                foreground.setVisible(z, false);
            }
            foreground.setCallback(this);
        } else if ((this.mViewFlags & 128) != 0 && this.mBackground == null && this.mDefaultFocusHighlight == null) {
            this.mPrivateFlags |= 128;
        }
        requestLayout();
        invalidate();
    }

    public boolean isForegroundInsidePadding() {
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        if (foregroundInfo != null) {
            return foregroundInfo.mInsidePadding;
        }
        return true;
    }

    public int getForegroundGravity() {
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        if (foregroundInfo != null) {
            return foregroundInfo.mGravity;
        }
        return 8388659;
    }

    public void setForegroundGravity(int gravity) {
        if (this.mForegroundInfo == null) {
            this.mForegroundInfo = new ForegroundInfo();
        }
        if (this.mForegroundInfo.mGravity != gravity) {
            if ((8388615 & gravity) == 0) {
                gravity |= Gravity.START;
            }
            if ((gravity & 112) == 0) {
                gravity |= 48;
            }
            this.mForegroundInfo.mGravity = gravity;
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setForegroundTintList(ColorStateList tint) {
        if (this.mForegroundInfo == null) {
            this.mForegroundInfo = new ForegroundInfo();
        }
        if (this.mForegroundInfo.mTintInfo == null) {
            this.mForegroundInfo.mTintInfo = new TintInfo();
        }
        this.mForegroundInfo.mTintInfo.mTintList = tint;
        this.mForegroundInfo.mTintInfo.mHasTintList = true;
        applyForegroundTint();
    }

    public ColorStateList getForegroundTintList() {
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        if (foregroundInfo == null || foregroundInfo.mTintInfo == null) {
            return null;
        }
        return this.mForegroundInfo.mTintInfo.mTintList;
    }

    public void setForegroundTintMode(PorterDuff.Mode tintMode) {
        BlendMode mode = null;
        if (tintMode != null) {
            mode = BlendMode.fromValue(tintMode.nativeInt);
        }
        setForegroundTintBlendMode(mode);
    }

    @RemotableViewMethod
    public void setForegroundTintBlendMode(BlendMode blendMode) {
        if (this.mForegroundInfo == null) {
            this.mForegroundInfo = new ForegroundInfo();
        }
        if (this.mForegroundInfo.mTintInfo == null) {
            this.mForegroundInfo.mTintInfo = new TintInfo();
        }
        this.mForegroundInfo.mTintInfo.mBlendMode = blendMode;
        this.mForegroundInfo.mTintInfo.mHasTintMode = true;
        applyForegroundTint();
    }

    public PorterDuff.Mode getForegroundTintMode() {
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        BlendMode blendMode = (foregroundInfo == null || foregroundInfo.mTintInfo == null) ? null : this.mForegroundInfo.mTintInfo.mBlendMode;
        if (blendMode != null) {
            return BlendMode.blendModeToPorterDuffMode(blendMode);
        }
        return null;
    }

    public BlendMode getForegroundTintBlendMode() {
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        if (foregroundInfo == null || foregroundInfo.mTintInfo == null) {
            return null;
        }
        return this.mForegroundInfo.mTintInfo.mBlendMode;
    }

    private void applyForegroundTint() {
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        if (foregroundInfo != null && foregroundInfo.mDrawable != null && this.mForegroundInfo.mTintInfo != null) {
            TintInfo tintInfo = this.mForegroundInfo.mTintInfo;
            if (tintInfo.mHasTintList || tintInfo.mHasTintMode) {
                ForegroundInfo foregroundInfo2 = this.mForegroundInfo;
                foregroundInfo2.mDrawable = foregroundInfo2.mDrawable.mutate();
                if (tintInfo.mHasTintList) {
                    this.mForegroundInfo.mDrawable.setTintList(tintInfo.mTintList);
                }
                if (tintInfo.mHasTintMode) {
                    this.mForegroundInfo.mDrawable.setTintBlendMode(tintInfo.mBlendMode);
                }
                if (this.mForegroundInfo.mDrawable.isStateful()) {
                    this.mForegroundInfo.mDrawable.setState(getDrawableState());
                }
            }
        }
    }

    private Drawable getAutofilledDrawable() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return null;
        }
        if (attachInfo.mAutofilledDrawable == null) {
            Context rootContext = getRootView().getContext();
            TypedArray a = rootContext.getTheme().obtainStyledAttributes(AUTOFILL_HIGHLIGHT_ATTR);
            int attributeResourceId = a.getResourceId(0, 0);
            this.mAttachInfo.mAutofilledDrawable = rootContext.getDrawable(attributeResourceId);
            a.recycle();
        }
        return this.mAttachInfo.mAutofilledDrawable;
    }

    private void drawAutofilledHighlight(Canvas canvas) {
        Drawable autofilledHighlight;
        if (isAutofilled() && !hideAutofillHighlight() && (autofilledHighlight = getAutofilledDrawable()) != null) {
            autofilledHighlight.setBounds(0, 0, getWidth(), getHeight());
            autofilledHighlight.draw(canvas);
        }
    }

    public void onDrawForeground(Canvas canvas) {
        onDrawScrollIndicators(canvas);
        onDrawScrollBars(canvas);
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        Drawable foreground = foregroundInfo != null ? foregroundInfo.mDrawable : null;
        if (foreground != null) {
            if (this.mForegroundInfo.mBoundsChanged) {
                this.mForegroundInfo.mBoundsChanged = false;
                Rect selfBounds = this.mForegroundInfo.mSelfBounds;
                Rect overlayBounds = this.mForegroundInfo.mOverlayBounds;
                if (this.mForegroundInfo.mInsidePadding) {
                    selfBounds.set(0, 0, getWidth(), getHeight());
                } else {
                    selfBounds.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
                }
                int ld = getLayoutDirection();
                Gravity.apply(this.mForegroundInfo.mGravity, foreground.getIntrinsicWidth(), foreground.getIntrinsicHeight(), selfBounds, overlayBounds, ld);
                foreground.setBounds(overlayBounds);
            }
            foreground.draw(canvas);
        }
    }

    public void setPadding(int left, int top, int right, int bottom) {
        resetResolvedPaddingInternal();
        this.mUserPaddingStart = Integer.MIN_VALUE;
        this.mUserPaddingEnd = Integer.MIN_VALUE;
        this.mUserPaddingLeftInitial = left;
        this.mUserPaddingRightInitial = right;
        this.mLeftPaddingDefined = true;
        this.mRightPaddingDefined = true;
        internalSetPadding(left, top, right, bottom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void internalSetPadding(int left, int top, int right, int bottom) {
        this.mUserPaddingLeft = left;
        this.mUserPaddingRight = right;
        this.mUserPaddingBottom = bottom;
        int viewFlags = this.mViewFlags;
        boolean changed = false;
        if ((viewFlags & 768) != 0) {
            if ((viewFlags & 512) != 0) {
                int offset = (viewFlags & 16777216) == 0 ? 0 : getVerticalScrollbarWidth();
                switch (this.mVerticalScrollbarPosition) {
                    case 0:
                        if (isLayoutRtl()) {
                            left += offset;
                            break;
                        } else {
                            right += offset;
                            break;
                        }
                    case 1:
                        left += offset;
                        break;
                    case 2:
                        right += offset;
                        break;
                }
            }
            if ((viewFlags & 256) != 0) {
                bottom += (viewFlags & 16777216) != 0 ? getHorizontalScrollbarHeight() : 0;
            }
        }
        if (this.mPaddingLeft != left) {
            changed = true;
            this.mPaddingLeft = left;
        }
        if (this.mPaddingTop != top) {
            changed = true;
            this.mPaddingTop = top;
        }
        if (this.mPaddingRight != right) {
            changed = true;
            this.mPaddingRight = right;
        }
        if (this.mPaddingBottom != bottom) {
            changed = true;
            this.mPaddingBottom = bottom;
        }
        if (changed) {
            requestLayout();
            invalidateOutline();
        }
    }

    public void setPaddingRelative(int start, int top, int end, int bottom) {
        resetResolvedPaddingInternal();
        this.mUserPaddingStart = start;
        this.mUserPaddingEnd = end;
        this.mLeftPaddingDefined = true;
        this.mRightPaddingDefined = true;
        switch (getLayoutDirection()) {
            case 1:
                this.mUserPaddingLeftInitial = end;
                this.mUserPaddingRightInitial = start;
                internalSetPadding(end, top, start, bottom);
                return;
            default:
                this.mUserPaddingLeftInitial = start;
                this.mUserPaddingRightInitial = end;
                internalSetPadding(start, top, end, bottom);
                return;
        }
    }

    public int getSourceLayoutResId() {
        return this.mSourceLayoutId;
    }

    public int getPaddingTop() {
        return this.mPaddingTop;
    }

    public int getPaddingBottom() {
        return this.mPaddingBottom;
    }

    public int getPaddingLeft() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return this.mPaddingLeft;
    }

    public int getPaddingStart() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return getLayoutDirection() == 1 ? this.mPaddingRight : this.mPaddingLeft;
    }

    public int getPaddingRight() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return this.mPaddingRight;
    }

    public int getPaddingEnd() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return getLayoutDirection() == 1 ? this.mPaddingLeft : this.mPaddingRight;
    }

    public boolean isPaddingRelative() {
        return (this.mUserPaddingStart == Integer.MIN_VALUE && this.mUserPaddingEnd == Integer.MIN_VALUE) ? false : true;
    }

    Insets computeOpticalInsets() {
        Drawable drawable = this.mBackground;
        return drawable == null ? Insets.NONE : drawable.getOpticalInsets();
    }

    public void resetPaddingToInitialValues() {
        if (isRtlCompatibilityMode()) {
            this.mPaddingLeft = this.mUserPaddingLeftInitial;
            this.mPaddingRight = this.mUserPaddingRightInitial;
        } else if (isLayoutRtl()) {
            int i = this.mUserPaddingEnd;
            if (i < 0) {
                i = this.mUserPaddingLeftInitial;
            }
            this.mPaddingLeft = i;
            int i2 = this.mUserPaddingStart;
            if (i2 < 0) {
                i2 = this.mUserPaddingRightInitial;
            }
            this.mPaddingRight = i2;
        } else {
            int i3 = this.mUserPaddingStart;
            if (i3 < 0) {
                i3 = this.mUserPaddingLeftInitial;
            }
            this.mPaddingLeft = i3;
            int i4 = this.mUserPaddingEnd;
            if (i4 < 0) {
                i4 = this.mUserPaddingRightInitial;
            }
            this.mPaddingRight = i4;
        }
    }

    public Insets getOpticalInsets() {
        if (this.mLayoutInsets == null) {
            this.mLayoutInsets = computeOpticalInsets();
        }
        return this.mLayoutInsets;
    }

    public void setOpticalInsets(Insets insets) {
        this.mLayoutInsets = insets;
    }

    public void setSelected(boolean selected) {
        int i = this.mPrivateFlags;
        if (((i & 4) != 0) != selected) {
            this.mPrivateFlags = (i & (-5)) | (selected ? 4 : 0);
            if (!selected) {
                resetPressedState();
            }
            invalidate(true);
            refreshDrawableState();
            dispatchSetSelected(selected);
            if (selected) {
                sendAccessibilityEvent(4);
            } else {
                notifyViewAccessibilityStateChangedIfNeeded(0);
            }
        }
    }

    protected void dispatchSetSelected(boolean selected) {
    }

    @ViewDebug.ExportedProperty
    public boolean isSelected() {
        return (this.mPrivateFlags & 4) != 0;
    }

    public void setActivated(boolean activated) {
        int i = this.mPrivateFlags;
        if (((i & 1073741824) != 0) != activated) {
            this.mPrivateFlags = (i & (-1073741825)) | (activated ? 1073741824 : 0);
            invalidate(true);
            refreshDrawableState();
            dispatchSetActivated(activated);
        }
    }

    protected void dispatchSetActivated(boolean activated) {
    }

    @ViewDebug.ExportedProperty
    public boolean isActivated() {
        return (this.mPrivateFlags & 1073741824) != 0;
    }

    public ViewTreeObserver getViewTreeObserver() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mTreeObserver;
        }
        if (this.mFloatingTreeObserver == null) {
            this.mFloatingTreeObserver = new ViewTreeObserver(this.mContext);
        }
        return this.mFloatingTreeObserver;
    }

    public View getRootView() {
        View v;
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && (v = attachInfo.mRootView) != null) {
            return v;
        }
        View parent = this;
        while (true) {
            ViewParent viewParent = parent.mParent;
            if (viewParent instanceof View) {
                parent = (View) viewParent;
            } else {
                return parent;
            }
        }
    }

    public boolean toGlobalMotionEvent(MotionEvent ev) {
        AttachInfo info = this.mAttachInfo;
        if (info == null) {
            return false;
        }
        Matrix m = info.mTmpMatrix;
        m.set(Matrix.IDENTITY_MATRIX);
        transformMatrixToGlobal(m);
        ev.transform(m);
        return true;
    }

    public boolean toLocalMotionEvent(MotionEvent ev) {
        AttachInfo info = this.mAttachInfo;
        if (info == null) {
            return false;
        }
        Matrix m = info.mTmpMatrix;
        m.set(Matrix.IDENTITY_MATRIX);
        transformMatrixToLocal(m);
        ev.transform(m);
        return true;
    }

    public void transformMatrixToGlobal(Matrix matrix) {
        ViewParent parent = this.mParent;
        if (parent instanceof View) {
            View vp = (View) parent;
            vp.transformMatrixToGlobal(matrix);
            matrix.preTranslate(-vp.mScrollX, -vp.mScrollY);
        } else if (parent instanceof ViewRootImpl) {
            ViewRootImpl vr = (ViewRootImpl) parent;
            vr.transformMatrixToGlobal(matrix);
            matrix.preTranslate(0.0f, -vr.mCurScrollY);
        }
        matrix.preTranslate(this.mLeft, this.mTop);
        if (!hasIdentityMatrix()) {
            matrix.preConcat(getMatrix());
        }
    }

    public void transformMatrixToLocal(Matrix matrix) {
        ViewParent parent = this.mParent;
        if (parent instanceof View) {
            View vp = (View) parent;
            vp.transformMatrixToLocal(matrix);
            matrix.postTranslate(vp.mScrollX, vp.mScrollY);
        } else if (parent instanceof ViewRootImpl) {
            ViewRootImpl vr = (ViewRootImpl) parent;
            vr.transformMatrixToLocal(matrix);
            matrix.postTranslate(0.0f, vr.mCurScrollY);
        }
        matrix.postTranslate(-this.mLeft, -this.mTop);
        if (!hasIdentityMatrix()) {
            matrix.postConcat(getInverseMatrix());
        }
    }

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT, indexMapping = {@ViewDebug.IntToString(from = 0, m86to = "x"), @ViewDebug.IntToString(from = 1, m86to = "y")})
    public int[] getLocationOnScreen() {
        int[] location = new int[2];
        getLocationOnScreen(location);
        return location;
    }

    public void getLocationOnScreen(int[] outLocation) {
        getLocationInWindow(outLocation);
        AttachInfo info = this.mAttachInfo;
        if (info != null) {
            outLocation[0] = outLocation[0] + info.mWindowLeft;
            outLocation[1] = outLocation[1] + info.mWindowTop;
            info.mViewRootImpl.applyViewLocationSandboxingIfNeeded(outLocation);
        }
    }

    public void getLocationInWindow(int[] outLocation) {
        if (outLocation == null || outLocation.length < 2) {
            throw new IllegalArgumentException("outLocation must be an array of two integers");
        }
        outLocation[0] = 0;
        outLocation[1] = 0;
        transformFromViewToWindowSpace(outLocation);
    }

    public void transformFromViewToWindowSpace(int[] inOutLocation) {
        if (inOutLocation == null || inOutLocation.length < 2) {
            throw new IllegalArgumentException("inOutLocation must be an array of two integers");
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            inOutLocation[1] = 0;
            inOutLocation[0] = 0;
            return;
        }
        float[] position = attachInfo.mTmpTransformLocation;
        position[0] = inOutLocation[0];
        position[1] = inOutLocation[1];
        if (!hasIdentityMatrix()) {
            getMatrix().mapPoints(position);
        }
        position[0] = position[0] + this.mLeft;
        position[1] = position[1] + this.mTop;
        ViewParent viewParent = this.mParent;
        while (viewParent instanceof View) {
            View view = (View) viewParent;
            position[0] = position[0] - view.mScrollX;
            position[1] = position[1] - view.mScrollY;
            if (!view.hasIdentityMatrix()) {
                view.getMatrix().mapPoints(position);
            }
            position[0] = position[0] + view.mLeft;
            position[1] = position[1] + view.mTop;
            viewParent = view.mParent;
        }
        if (viewParent instanceof ViewRootImpl) {
            ViewRootImpl vr = (ViewRootImpl) viewParent;
            position[1] = position[1] - vr.mCurScrollY;
        }
        inOutLocation[0] = Math.round(position[0]);
        inOutLocation[1] = Math.round(position[1]);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected <T extends View> T findViewTraversal(int id) {
        if (id == this.mID) {
            return this;
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected <T extends View> T findViewWithTagTraversal(Object tag) {
        if (tag != null && tag.equals(this.mTag)) {
            return this;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public <T extends View> T findViewByPredicateTraversal(Predicate<View> predicate, View childToSkip) {
        if (predicate.test(this)) {
            return this;
        }
        return null;
    }

    public final <T extends View> T findViewById(int id) {
        if (id == -1) {
            return null;
        }
        return (T) findViewTraversal(id);
    }

    public final <T extends View> T requireViewById(int id) {
        T view = (T) findViewById(id);
        if (view == null) {
            throw new IllegalArgumentException("ID does not reference a View inside this View");
        }
        return view;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends View> T findViewByAccessibilityIdTraversal(int accessibilityId) {
        if (getAccessibilityViewId() == accessibilityId) {
            return this;
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends View> T findViewByAutofillIdTraversal(int autofillId) {
        if (getAutofillViewId() == autofillId) {
            return this;
        }
        return null;
    }

    public final <T extends View> T findViewWithTag(Object tag) {
        if (tag == null) {
            return null;
        }
        return (T) findViewWithTagTraversal(tag);
    }

    public final <T extends View> T findViewByPredicate(Predicate<View> predicate) {
        return (T) findViewByPredicateTraversal(predicate, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x001c, code lost:
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final <T extends View> T findViewByPredicateInsideOut(View start, Predicate<View> predicate) {
        View childToSkip = null;
        while (true) {
            T view = (T) start.findViewByPredicateTraversal(predicate, childToSkip);
            if (view != null || start == this) {
                break;
            }
            ViewParent parent = start.getParent();
            if (parent == null || !(parent instanceof View)) {
                return null;
            }
            childToSkip = start;
            start = (View) parent;
        }
    }

    public void setId(int id) {
        this.mID = id;
        if (id == -1 && this.mLabelForId != -1) {
            this.mID = generateViewId();
        }
    }

    public void setIsRootNamespace(boolean isRoot) {
        if (isRoot) {
            this.mPrivateFlags |= 8;
        } else {
            this.mPrivateFlags &= -9;
        }
    }

    public boolean isRootNamespace() {
        return (this.mPrivateFlags & 8) != 0;
    }

    @ViewDebug.CapturedViewProperty
    public int getId() {
        return this.mID;
    }

    public long getUniqueDrawingId() {
        return this.mRenderNode.getUniqueId();
    }

    @ViewDebug.ExportedProperty
    public Object getTag() {
        return this.mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag(int key) {
        SparseArray<Object> sparseArray = this.mKeyedTags;
        if (sparseArray != null) {
            return sparseArray.get(key);
        }
        return null;
    }

    public void setTag(int key, Object tag) {
        if ((key >>> 24) < 2) {
            throw new IllegalArgumentException("The key must be an application-specific resource id.");
        }
        setKeyedTag(key, tag);
    }

    public void setTagInternal(int key, Object tag) {
        if ((key >>> 24) != 1) {
            throw new IllegalArgumentException("The key must be a framework-specific resource id.");
        }
        setKeyedTag(key, tag);
    }

    private void setKeyedTag(int key, Object tag) {
        if (this.mKeyedTags == null) {
            this.mKeyedTags = new SparseArray<>(2);
        }
        this.mKeyedTags.put(key, tag);
    }

    public void debug() {
        debug(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void debug(int depth) {
        String output;
        String output2 = debugIndent(depth - 1) + "+ " + this;
        int id = getId();
        if (id != -1) {
            output2 = output2 + " (id=" + id + NavigationBarInflaterView.KEY_CODE_END;
        }
        Object tag = getTag();
        if (tag != null) {
            output2 = output2 + " (tag=" + tag + NavigationBarInflaterView.KEY_CODE_END;
        }
        Log.m112d(VIEW_LOG_TAG, output2);
        if ((this.mPrivateFlags & 2) != 0) {
            Log.m112d(VIEW_LOG_TAG, debugIndent(depth) + " FOCUSED");
        }
        Log.m112d(VIEW_LOG_TAG, debugIndent(depth) + "frame={" + this.mLeft + ", " + this.mTop + ", " + this.mRight + ", " + this.mBottom + "} scroll={" + this.mScrollX + ", " + this.mScrollY + "} ");
        if (this.mPaddingLeft != 0 || this.mPaddingTop != 0 || this.mPaddingRight != 0 || this.mPaddingBottom != 0) {
            Log.m112d(VIEW_LOG_TAG, debugIndent(depth) + "padding={" + this.mPaddingLeft + ", " + this.mPaddingTop + ", " + this.mPaddingRight + ", " + this.mPaddingBottom + "}");
        }
        Log.m112d(VIEW_LOG_TAG, debugIndent(depth) + "mMeasureWidth=" + this.mMeasuredWidth + " mMeasureHeight=" + this.mMeasuredHeight);
        String output3 = debugIndent(depth);
        ViewGroup.LayoutParams layoutParams = this.mLayoutParams;
        if (layoutParams == null) {
            output = output3 + "BAD! no layout params";
        } else {
            output = layoutParams.debug(output3);
        }
        Log.m112d(VIEW_LOG_TAG, output);
        Log.m112d(VIEW_LOG_TAG, ((debugIndent(depth) + "flags={") + printFlags(this.mViewFlags)) + "}");
        Log.m112d(VIEW_LOG_TAG, ((debugIndent(depth) + "privateFlags={") + printPrivateFlags(this.mPrivateFlags)) + "}");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String debugIndent(int depth) {
        StringBuilder spaces = new StringBuilder(((depth * 2) + 3) * 2);
        for (int i = 0; i < (depth * 2) + 3; i++) {
            spaces.append(' ').append(' ');
        }
        return spaces.toString();
    }

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    public int getBaseline() {
        return -1;
    }

    public boolean isInLayout() {
        ViewRootImpl viewRoot = getViewRootImpl();
        return viewRoot != null && viewRoot.isInLayout();
    }

    private void printStackStrace(String name) {
        Log.m112d(VIEW_LOG_TAG, "---- ST:" + name);
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int endIndex = Math.min(stackTraceElements.length, 1 + 20);
        for (int i = 1; i < endIndex; i++) {
            StackTraceElement s = stackTraceElements[i];
            sb.append(s.getMethodName()).append(NavigationBarInflaterView.KEY_CODE_START).append(s.getFileName()).append(":").append(s.getLineNumber()).append(") <- ");
        }
        Log.m112d(VIEW_LOG_TAG, name + ": " + ((Object) sb));
    }

    public void requestLayout() {
        if (isRelayoutTracingEnabled()) {
            Trace.instantForTrack(4096L, "requestLayoutTracing", this.mTracingStrings.classSimpleName);
            printStackStrace(this.mTracingStrings.requestLayoutStacktracePrefix);
        }
        LongSparseLongArray longSparseLongArray = this.mMeasureCache;
        if (longSparseLongArray != null) {
            longSparseLongArray.clear();
        }
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && attachInfo.mViewRequestingLayout == null) {
            ViewRootImpl viewRoot = getViewRootImpl();
            if (viewRoot != null && viewRoot.isInLayout() && !viewRoot.requestLayoutDuringLayout(this)) {
                return;
            }
            this.mAttachInfo.mViewRequestingLayout = this;
        }
        int i = this.mPrivateFlags | 4096;
        this.mPrivateFlags = i;
        this.mPrivateFlags = i | Integer.MIN_VALUE;
        ViewParent viewParent = this.mParent;
        if (viewParent != null && !viewParent.isLayoutRequested()) {
            this.mParent.requestLayout();
        }
        AttachInfo attachInfo2 = this.mAttachInfo;
        if (attachInfo2 != null && attachInfo2.mViewRequestingLayout == this) {
            this.mAttachInfo.mViewRequestingLayout = null;
        }
    }

    public void forceLayout() {
        LongSparseLongArray longSparseLongArray = this.mMeasureCache;
        if (longSparseLongArray != null) {
            longSparseLongArray.clear();
        }
        int i = this.mPrivateFlags | 4096;
        this.mPrivateFlags = i;
        this.mPrivateFlags = i | Integer.MIN_VALUE;
    }

    /* JADX WARN: Removed duplicated region for block: B:70:0x0104  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0122  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasureSpec2;
        int heightMeasureSpec2;
        int i;
        boolean optical = isLayoutModeOptical(this);
        if (optical == isLayoutModeOptical(this.mParent)) {
            widthMeasureSpec2 = widthMeasureSpec;
            heightMeasureSpec2 = heightMeasureSpec;
        } else {
            Insets insets = getOpticalInsets();
            int oWidth = insets.left + insets.right;
            int oHeight = insets.top + insets.bottom;
            widthMeasureSpec2 = MeasureSpec.adjust(widthMeasureSpec, optical ? -oWidth : oWidth);
            heightMeasureSpec2 = MeasureSpec.adjust(heightMeasureSpec, optical ? -oHeight : oHeight);
        }
        long key = (widthMeasureSpec2 << 32) | (heightMeasureSpec2 & 4294967295L);
        if (this.mMeasureCache == null) {
            this.mMeasureCache = new LongSparseLongArray(2);
        }
        boolean needsLayout = true;
        boolean forceLayout = (this.mPrivateFlags & 4096) == 4096;
        boolean specChanged = (widthMeasureSpec2 == this.mOldWidthMeasureSpec && heightMeasureSpec2 == this.mOldHeightMeasureSpec) ? false : true;
        boolean isSpecExactly = MeasureSpec.getMode(widthMeasureSpec2) == 1073741824 && MeasureSpec.getMode(heightMeasureSpec2) == 1073741824;
        boolean matchesSpecSize = getMeasuredWidth() == MeasureSpec.getSize(widthMeasureSpec2) && getMeasuredHeight() == MeasureSpec.getSize(heightMeasureSpec2);
        if (!specChanged || (!sAlwaysRemeasureExactly && isSpecExactly && matchesSpecSize)) {
            needsLayout = false;
        }
        if (forceLayout || needsLayout) {
            this.mPrivateFlags &= -2049;
            resolveRtlPropertiesIfNeeded();
            int cacheIndex = forceLayout ? -1 : this.mMeasureCache.indexOfKey(key);
            if (cacheIndex >= 0 && !sIgnoreMeasureCache) {
                long value = this.mMeasureCache.valueAt(cacheIndex);
                setMeasuredDimensionRaw((int) (value >> 32), (int) value);
                this.mPrivateFlags3 |= 8;
                i = this.mPrivateFlags;
                if ((i & 2048) != 2048) {
                    this.mPrivateFlags = i | 8192;
                } else {
                    throw new IllegalStateException("View with id " + getId() + ": " + getClass().getName() + "#onMeasure() did not set the measured dimension by calling setMeasuredDimension()");
                }
            }
            if (isTraversalTracingEnabled()) {
                Trace.beginSection(this.mTracingStrings.onMeasure);
            }
            onMeasure(widthMeasureSpec2, heightMeasureSpec2);
            if (isTraversalTracingEnabled()) {
                Trace.endSection();
            }
            this.mPrivateFlags3 &= -9;
            i = this.mPrivateFlags;
            if ((i & 2048) != 2048) {
            }
        }
        this.mOldWidthMeasureSpec = widthMeasureSpec2;
        this.mOldHeightMeasureSpec = heightMeasureSpec2;
        this.mMeasureCache.put(key, (this.mMeasuredHeight & 4294967295L) | (this.mMeasuredWidth << 32));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setMeasuredDimension(int measuredWidth, int measuredHeight) {
        boolean optical = isLayoutModeOptical(this);
        if (optical != isLayoutModeOptical(this.mParent)) {
            Insets insets = getOpticalInsets();
            int opticalWidth = insets.left + insets.right;
            int opticalHeight = insets.top + insets.bottom;
            measuredWidth += optical ? opticalWidth : -opticalWidth;
            measuredHeight += optical ? opticalHeight : -opticalHeight;
        }
        setMeasuredDimensionRaw(measuredWidth, measuredHeight);
    }

    private void setMeasuredDimensionRaw(int measuredWidth, int measuredHeight) {
        this.mMeasuredWidth = measuredWidth;
        this.mMeasuredHeight = measuredHeight;
        this.mPrivateFlags |= 2048;
    }

    public static int combineMeasuredStates(int curState, int newState) {
        return curState | newState;
    }

    public static int resolveSize(int size, int measureSpec) {
        return resolveSizeAndState(size, measureSpec, 0) & 16777215;
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
                if (specSize < size) {
                    result = 16777216 | specSize;
                    break;
                } else {
                    result = size;
                    break;
                }
            case 1073741824:
                result = specSize;
                break;
            default:
                result = size;
                break;
        }
        return ((-16777216) & childMeasuredState) | result;
    }

    public static int getDefaultSize(int size, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
            case 1073741824:
                return specSize;
            case 0:
                return size;
            default:
                return size;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getSuggestedMinimumHeight() {
        Drawable drawable = this.mBackground;
        return drawable == null ? this.mMinHeight : Math.max(this.mMinHeight, drawable.getMinimumHeight());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getSuggestedMinimumWidth() {
        Drawable drawable = this.mBackground;
        return drawable == null ? this.mMinWidth : Math.max(this.mMinWidth, drawable.getMinimumWidth());
    }

    public int getMinimumHeight() {
        return this.mMinHeight;
    }

    @RemotableViewMethod
    public void setMinimumHeight(int minHeight) {
        this.mMinHeight = minHeight;
        requestLayout();
    }

    public int getMinimumWidth() {
        return this.mMinWidth;
    }

    @RemotableViewMethod
    public void setMinimumWidth(int minWidth) {
        this.mMinWidth = minWidth;
        requestLayout();
    }

    public Animation getAnimation() {
        return this.mCurrentAnimation;
    }

    public void startAnimation(Animation animation) {
        animation.setStartTime(-1L);
        setAnimation(animation);
        invalidateParentCaches();
        invalidate(true);
    }

    public void clearAnimation() {
        Animation animation = this.mCurrentAnimation;
        if (animation != null) {
            animation.detach();
        }
        this.mCurrentAnimation = null;
        invalidateParentIfNeeded();
    }

    public void setAnimation(Animation animation) {
        this.mCurrentAnimation = animation;
        if (animation != null) {
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null && attachInfo.mDisplayState == 1 && animation.getStartTime() == -1) {
                animation.setStartTime(AnimationUtils.currentAnimationTimeMillis());
            }
            animation.reset();
        }
    }

    protected void onAnimationStart() {
        this.mPrivateFlags |= 65536;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onAnimationEnd() {
        this.mPrivateFlags &= -65537;
    }

    protected boolean onSetAlpha(int alpha) {
        return false;
    }

    public boolean gatherTransparentRegion(Region region) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (region != null && attachInfo != null) {
            int pflags = this.mPrivateFlags;
            if ((pflags & 128) == 0) {
                int[] location = attachInfo.mTransparentLocation;
                getLocationInWindow(location);
                int shadowOffset = getZ() > 0.0f ? (int) getZ() : 0;
                region.m180op(location[0] - shadowOffset, location[1] - shadowOffset, ((location[0] + this.mRight) - this.mLeft) + shadowOffset, ((location[1] + this.mBottom) - this.mTop) + (shadowOffset * 3), Region.EnumC0813Op.DIFFERENCE);
            } else {
                Drawable drawable = this.mBackground;
                if (drawable != null && drawable.getOpacity() != -2) {
                    applyDrawableToTransparentRegion(this.mBackground, region);
                }
                ForegroundInfo foregroundInfo = this.mForegroundInfo;
                if (foregroundInfo != null && foregroundInfo.mDrawable != null && this.mForegroundInfo.mDrawable.getOpacity() != -2) {
                    applyDrawableToTransparentRegion(this.mForegroundInfo.mDrawable, region);
                }
                Drawable drawable2 = this.mDefaultFocusHighlight;
                if (drawable2 != null && drawable2.getOpacity() != -2) {
                    applyDrawableToTransparentRegion(this.mDefaultFocusHighlight, region);
                }
            }
        }
        return true;
    }

    public void playSoundEffect(int soundConstant) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || attachInfo.mRootCallbacks == null || !isSoundEffectsEnabled()) {
            return;
        }
        this.mAttachInfo.mRootCallbacks.playSoundEffect(soundConstant);
    }

    public boolean performHapticFeedback(int feedbackConstant) {
        return performHapticFeedback(feedbackConstant, 0);
    }

    public boolean performHapticFeedback(int feedbackConstant, int flags) {
        if (feedbackConstant == -1 || this.mAttachInfo == null) {
            return false;
        }
        if ((flags & 1) != 0 || isHapticFeedbackEnabled()) {
            return this.mAttachInfo.mRootCallbacks.performHapticFeedback(feedbackConstant, (flags & 2) != 0);
        }
        return false;
    }

    @Deprecated
    public void setSystemUiVisibility(int visibility) {
        AttachInfo attachInfo;
        if (visibility != this.mSystemUiVisibility) {
            this.mSystemUiVisibility = visibility;
            if (this.mParent != null && (attachInfo = this.mAttachInfo) != null && !attachInfo.mRecomputeGlobalAttributes) {
                this.mParent.recomputeViewAttributes(this);
            }
        }
    }

    @Deprecated
    public int getSystemUiVisibility() {
        return this.mSystemUiVisibility;
    }

    @Deprecated
    public int getWindowSystemUiVisibility() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mSystemUiVisibility;
        }
        return 0;
    }

    @Deprecated
    public void onWindowSystemUiVisibilityChanged(int visible) {
    }

    @Deprecated
    public void dispatchWindowSystemUiVisiblityChanged(int visible) {
        onWindowSystemUiVisibilityChanged(visible);
    }

    @Deprecated
    public void setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener l) {
        AttachInfo attachInfo;
        getListenerInfo().mOnSystemUiVisibilityChangeListener = l;
        if (this.mParent != null && (attachInfo = this.mAttachInfo) != null && !attachInfo.mRecomputeGlobalAttributes) {
            this.mParent.recomputeViewAttributes(this);
        }
    }

    @Deprecated
    public void dispatchSystemUiVisibilityChanged(int visibility) {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnSystemUiVisibilityChangeListener != null) {
            li.mOnSystemUiVisibilityChangeListener.onSystemUiVisibilityChange(visibility & PUBLIC_STATUS_BAR_VISIBILITY_MASK);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean updateLocalSystemUiVisibility(int localValue, int localChanges) {
        int i = this.mSystemUiVisibility;
        int val = ((~localChanges) & i) | (localValue & localChanges);
        if (val != i) {
            setSystemUiVisibility(val);
            return true;
        }
        return false;
    }

    public void setDisabledSystemUiVisibility(int flags) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null && attachInfo.mDisabledSystemUiVisibility != flags) {
            this.mAttachInfo.mDisabledSystemUiVisibility = flags;
            ViewParent viewParent = this.mParent;
            if (viewParent != null) {
                viewParent.recomputeViewAttributes(this);
            }
        }
    }

    public void onSystemBarAppearanceChanged(int appearance) {
    }

    /* loaded from: classes4.dex */
    public static class DragShadowBuilder {
        private final WeakReference<View> mView;

        public DragShadowBuilder(View view) {
            this.mView = new WeakReference<>(view);
        }

        public DragShadowBuilder() {
            this.mView = new WeakReference<>(null);
        }

        public final View getView() {
            return this.mView.get();
        }

        public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
            View view = this.mView.get();
            if (view != null) {
                outShadowSize.set(view.getWidth(), view.getHeight());
                outShadowTouchPoint.set(outShadowSize.f76x / 2, outShadowSize.f77y / 2);
                return;
            }
            Log.m110e(View.VIEW_LOG_TAG, "Asked for drag thumb metrics but no view");
        }

        public void onDrawShadow(Canvas canvas) {
            View view = this.mView.get();
            if (view != null) {
                view.draw(canvas);
            } else {
                Log.m110e(View.VIEW_LOG_TAG, "Asked to draw drag shadow but no view");
            }
        }
    }

    @Deprecated
    public final boolean startDrag(ClipData data, DragShadowBuilder shadowBuilder, Object myLocalState, int flags) {
        return startDragAndDrop(data, shadowBuilder, myLocalState, flags);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:121:0x029c  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x02a6  */
    /* JADX WARN: Type inference failed for: r15v0 */
    /* JADX WARN: Type inference failed for: r15v1 */
    /* JADX WARN: Type inference failed for: r15v10 */
    /* JADX WARN: Type inference failed for: r15v11 */
    /* JADX WARN: Type inference failed for: r15v12, types: [android.view.Surface] */
    /* JADX WARN: Type inference failed for: r15v2 */
    /* JADX WARN: Type inference failed for: r15v3, types: [android.view.Surface] */
    /* JADX WARN: Type inference failed for: r15v4, types: [android.view.Surface] */
    /* JADX WARN: Type inference failed for: r15v5 */
    /* JADX WARN: Type inference failed for: r15v6 */
    /* JADX WARN: Type inference failed for: r15v8 */
    /* JADX WARN: Type inference failed for: r22v0, types: [android.graphics.Point] */
    /* JADX WARN: Type inference failed for: r22v1 */
    /* JADX WARN: Type inference failed for: r22v10 */
    /* JADX WARN: Type inference failed for: r22v2, types: [boolean] */
    /* JADX WARN: Type inference failed for: r22v3 */
    /* JADX WARN: Type inference failed for: r22v5 */
    /* JADX WARN: Type inference failed for: r22v6 */
    /* JADX WARN: Type inference failed for: r22v7 */
    /* JADX WARN: Type inference failed for: r3v11, types: [java.lang.String] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean startDragAndDrop(ClipData data, DragShadowBuilder shadowBuilder, Object myLocalState, int flags) {
        SurfaceSession session;
        SurfaceControl.Transaction transaction;
        Canvas lockHardwareCanvas;
        String str;
        Surface surface = flags;
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            Log.m104w(VIEW_LOG_TAG, "startDragAndDrop called on a detached view.");
            return false;
        } else if (!attachInfo.mViewRootImpl.mSurface.isValid()) {
            Log.m104w(VIEW_LOG_TAG, "startDragAndDrop called with an invalid surface.");
            return false;
        } else {
            if (data != null) {
                data.prepareToLeaveProcess((surface & 256) != 0);
            }
            Rect bounds = new Rect();
            getBoundsOnScreen(bounds, true);
            Point lastTouchPoint = new Point();
            this.mAttachInfo.mViewRootImpl.getLastTouchPoint(lastTouchPoint);
            ViewRootImpl root = this.mAttachInfo.mViewRootImpl;
            boolean a11yEnabled = AccessibilityManager.getInstance(this.mContext).isEnabled();
            if (!a11yEnabled || (surface & 1024) == 0) {
                ?? r22 = lastTouchPoint;
                Point shadowSize = new Point();
                Point shadowTouchPoint = new Point();
                shadowBuilder.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
                if (shadowSize.f76x < 0 || shadowSize.f77y < 0 || shadowTouchPoint.f76x < 0 || shadowTouchPoint.f77y < 0) {
                    throw new IllegalStateException("Drag shadow dimensions must not be negative");
                }
                float overrideInvScale = CompatibilityInfo.getOverrideInvertedScale();
                if (overrideInvScale != 1.0f) {
                    shadowTouchPoint.f76x = (int) (shadowTouchPoint.f76x / overrideInvScale);
                    shadowTouchPoint.f77y = (int) (shadowTouchPoint.f77y / overrideInvScale);
                }
                if (shadowSize.f76x == 0 || shadowSize.f77y == 0) {
                    if (!sAcceptZeroSizeDragShadow) {
                        throw new IllegalStateException("Drag shadow dimensions must be positive");
                    }
                    shadowSize.f76x = 1;
                    shadowSize.f77y = 1;
                }
                SurfaceSession session2 = new SurfaceSession();
                SurfaceControl surfaceControl = new SurfaceControl.Builder(session2).setName("drag surface").setParent(root.getSurfaceControl()).setBufferSize(shadowSize.f76x, shadowSize.f77y).setFormat(-3).setCallsite("View.startDragAndDrop").build();
                if (overrideInvScale != 1.0f) {
                    transaction = new SurfaceControl.Transaction();
                    transaction.setMatrix(surfaceControl, 1.0f / overrideInvScale, 0.0f, 0.0f, 1.0f / overrideInvScale).apply();
                }
                Surface surface2 = new Surface();
                surface2.copyFrom(surfaceControl);
                IBinder token = null;
                try {
                    try {
                        if (isHardwareAccelerated()) {
                            try {
                                lockHardwareCanvas = surface2.lockHardwareCanvas();
                            } catch (Exception e) {
                                e = e;
                                surface = surface2;
                                session = session2;
                                transaction = VIEW_LOG_TAG;
                                r22 = 0;
                                Log.m109e(transaction, "Unable to initiate drag", e);
                                if (token == null) {
                                }
                                session.kill();
                                return r22;
                            } catch (Throwable th) {
                                e = th;
                                surface = surface2;
                                session = session2;
                                if (token == null) {
                                }
                                session.kill();
                                throw e;
                            }
                        } else {
                            lockHardwareCanvas = surface2.lockCanvas(null);
                        }
                        Canvas canvas = lockHardwareCanvas;
                        try {
                            try {
                                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                                shadowBuilder.onDrawShadow(canvas);
                                surface2.unlockCanvasAndPost(canvas);
                                IWindowSession iWindowSession = this.mAttachInfo.mSession;
                                IWindow iWindow = this.mAttachInfo.mWindow;
                                int lastTouchSource = root.getLastTouchSource();
                                try {
                                    try {
                                        float f = r22.f76x;
                                        try {
                                            float f2 = r22.f77y;
                                            float f3 = shadowTouchPoint.f76x;
                                            float f4 = shadowTouchPoint.f77y;
                                            surface = surface2;
                                            session = session2;
                                            r22 = 0;
                                            transaction = VIEW_LOG_TAG;
                                            IBinder token2 = iWindowSession.performDrag(iWindow, flags, surfaceControl, lastTouchSource, f, f2, f3, f4, data);
                                            if (token2 != null) {
                                                try {
                                                    if (this.mAttachInfo.mDragSurface != null) {
                                                        this.mAttachInfo.mDragSurface.release();
                                                    }
                                                    this.mAttachInfo.mDragSurface = surface;
                                                    this.mAttachInfo.mDragToken = token2;
                                                    root.setLocalDragState(myLocalState);
                                                    if (a11yEnabled) {
                                                        this.mAttachInfo.mViewRootImpl.setDragStartedViewForAccessibility(this);
                                                    }
                                                } catch (Exception e2) {
                                                    e = e2;
                                                    token = token2;
                                                    Log.m109e(transaction, "Unable to initiate drag", e);
                                                    if (token == null) {
                                                    }
                                                    session.kill();
                                                    return r22;
                                                } catch (Throwable th2) {
                                                    e = th2;
                                                    token = token2;
                                                    if (token == null) {
                                                        surface.destroy();
                                                    }
                                                    session.kill();
                                                    throw e;
                                                }
                                            }
                                            boolean z = token2 != null;
                                            if (token2 == null) {
                                                surface.destroy();
                                            }
                                            session.kill();
                                            return z;
                                        } catch (Exception e3) {
                                            e = e3;
                                            surface = surface2;
                                            session = session2;
                                            transaction = VIEW_LOG_TAG;
                                            r22 = 0;
                                            Log.m109e(transaction, "Unable to initiate drag", e);
                                            if (token == null) {
                                                surface.destroy();
                                            }
                                            session.kill();
                                            return r22;
                                        }
                                    } catch (Exception e4) {
                                        e = e4;
                                        surface = surface2;
                                        session = session2;
                                        r22 = 0;
                                        transaction = VIEW_LOG_TAG;
                                    }
                                } catch (Throwable th3) {
                                    e = th3;
                                    surface = surface2;
                                    session = session2;
                                }
                            } catch (Exception e5) {
                                e = e5;
                            }
                        } catch (Throwable th4) {
                            surface2.unlockCanvasAndPost(canvas);
                            throw th4;
                        }
                    } catch (Throwable th5) {
                        e = th5;
                    }
                } catch (Exception e6) {
                    e = e6;
                    surface = surface2;
                    session = session2;
                    transaction = VIEW_LOG_TAG;
                    r22 = 0;
                } catch (Throwable th6) {
                    e = th6;
                    surface = surface2;
                    session = session2;
                }
            } else {
                try {
                    IWindowSession iWindowSession2 = this.mAttachInfo.mSession;
                    IWindow iWindow2 = this.mAttachInfo.mWindow;
                    int lastTouchSource2 = this.mAttachInfo.mViewRootImpl.getLastTouchSource();
                    str = VIEW_LOG_TAG;
                    try {
                        IBinder token3 = iWindowSession2.performDrag(iWindow2, flags, null, lastTouchSource2, 0.0f, 0.0f, 0.0f, 0.0f, data);
                        if (token3 != null) {
                            try {
                                root.setLocalDragState(myLocalState);
                                this.mAttachInfo.mDragToken = token3;
                                this.mAttachInfo.mViewRootImpl.setDragStartedViewForAccessibility(this);
                                setAccessibilityDragStarted(true);
                            } catch (Exception e7) {
                                e = e7;
                                Log.m109e(str, "Unable to initiate a11y drag", e);
                                return false;
                            }
                        }
                        return token3 != null;
                    } catch (Exception e8) {
                        e = e8;
                    }
                } catch (Exception e9) {
                    e = e9;
                    str = VIEW_LOG_TAG;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAccessibilityDragStarted(boolean started) {
        int pflags4;
        int pflags42 = this.mPrivateFlags4;
        if (started) {
            pflags4 = pflags42 | 32768;
        } else {
            pflags4 = pflags42 & (-32769);
        }
        if (pflags4 != this.mPrivateFlags4) {
            this.mPrivateFlags4 = pflags4;
            sendWindowContentChangedAccessibilityEvent(0);
        }
    }

    private boolean startedSystemDragForAccessibility() {
        return (this.mPrivateFlags4 & 32768) != 0;
    }

    public final void cancelDragAndDrop() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            Log.m104w(VIEW_LOG_TAG, "cancelDragAndDrop called on a detached view.");
        } else if (attachInfo.mDragToken != null) {
            try {
                this.mAttachInfo.mSession.cancelDragAndDrop(this.mAttachInfo.mDragToken, false);
            } catch (Exception e) {
                Log.m109e(VIEW_LOG_TAG, "Unable to cancel drag", e);
            }
            this.mAttachInfo.mDragToken = null;
        } else {
            Log.m110e(VIEW_LOG_TAG, "No active drag to cancel");
        }
    }

    public final void updateDragShadow(DragShadowBuilder shadowBuilder) {
        Canvas canvas;
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            Log.m104w(VIEW_LOG_TAG, "updateDragShadow called on a detached view.");
        } else if (attachInfo.mDragToken != null) {
            try {
                if (isHardwareAccelerated()) {
                    canvas = this.mAttachInfo.mDragSurface.lockHardwareCanvas();
                } else {
                    canvas = this.mAttachInfo.mDragSurface.lockCanvas(null);
                }
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                shadowBuilder.onDrawShadow(canvas);
                this.mAttachInfo.mDragSurface.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                Log.m109e(VIEW_LOG_TAG, "Unable to update drag shadow", e);
            }
        } else {
            Log.m110e(VIEW_LOG_TAG, "No active drag");
        }
    }

    public final boolean startMovingTask(float startX, float startY) {
        try {
            return this.mAttachInfo.mSession.startMovingTask(this.mAttachInfo.mWindow, startX, startY);
        } catch (RemoteException e) {
            Log.m109e(VIEW_LOG_TAG, "Unable to start moving", e);
            return false;
        }
    }

    public void finishMovingTask() {
        try {
            this.mAttachInfo.mSession.finishMovingTask(this.mAttachInfo.mWindow);
        } catch (RemoteException e) {
            Log.m109e(VIEW_LOG_TAG, "Unable to finish moving", e);
        }
    }

    public boolean onDragEvent(DragEvent event) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo == null || listenerInfo.mOnReceiveContentListener == null) {
            return false;
        }
        if (event.getAction() == 1) {
            return true;
        }
        if (event.getAction() == 3) {
            DragAndDropPermissions permissions = DragAndDropPermissions.obtain(event);
            if (permissions != null) {
                permissions.takeTransient();
            }
            ContentInfo payload = new ContentInfo.Builder(event.getClipData(), 3).setDragAndDropPermissions(permissions).build();
            ContentInfo remainingPayload = performReceiveContent(payload);
            return remainingPayload != payload;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean dispatchDragEnterExitInPreN(DragEvent event) {
        return callDragEventHandler(event);
    }

    public boolean dispatchDragEvent(DragEvent event) {
        event.mEventHandlerWasCalled = true;
        if (event.mAction == 2 || event.mAction == 3) {
            getViewRootImpl().setDragFocus(this, event);
        }
        return callDragEventHandler(event);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean callDragEventHandler(DragEvent event) {
        boolean result;
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnDragListener != null && (this.mViewFlags & 32) == 0 && li.mOnDragListener.onDrag(this, event)) {
            result = true;
        } else {
            result = onDragEvent(event);
        }
        switch (event.mAction) {
            case 1:
                if (result && li != null && li.mOnDragListener != null) {
                    sendWindowContentChangedAccessibilityEvent(0);
                    break;
                }
                break;
            case 3:
                if (result && li != null && (li.mOnDragListener != null || li.mOnReceiveContentListener != null)) {
                    sendWindowContentChangedAccessibilityEvent(256);
                    break;
                }
                break;
            case 4:
                sendWindowContentChangedAccessibilityEvent(0);
                this.mPrivateFlags2 &= -4;
                refreshDrawableState();
                break;
            case 5:
                this.mPrivateFlags2 |= 2;
                refreshDrawableState();
                break;
            case 6:
                this.mPrivateFlags2 &= -3;
                refreshDrawableState();
                break;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canAcceptDrag() {
        return (this.mPrivateFlags2 & 1) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendWindowContentChangedAccessibilityEvent(int changeType) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setEventType(2048);
            event.setContentChangeTypes(changeType);
            sendAccessibilityEventUnchecked(event);
        }
    }

    public void onCloseSystemDialogs(String reason) {
    }

    public void applyDrawableToTransparentRegion(Drawable dr, Region region) {
        Region r = dr.getTransparentRegion();
        Rect db = dr.getBounds();
        AttachInfo attachInfo = this.mAttachInfo;
        if (r != null && attachInfo != null) {
            int w = getRight() - getLeft();
            int h = getBottom() - getTop();
            if (db.left > 0) {
                r.m180op(0, 0, db.left, h, Region.EnumC0813Op.UNION);
            }
            if (db.right < w) {
                r.m180op(db.right, 0, w, h, Region.EnumC0813Op.UNION);
            }
            if (db.top > 0) {
                r.m180op(0, 0, w, db.top, Region.EnumC0813Op.UNION);
            }
            if (db.bottom < h) {
                r.m180op(0, db.bottom, w, h, Region.EnumC0813Op.UNION);
            }
            int[] location = attachInfo.mTransparentLocation;
            getLocationInWindow(location);
            r.translate(location[0], location[1]);
            region.m177op(r, Region.EnumC0813Op.INTERSECT);
            return;
        }
        region.m179op(db, Region.EnumC0813Op.DIFFERENCE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkForLongClick(long delay, float x, float y, int classification) {
        int i = this.mViewFlags;
        if ((i & 2097152) == 2097152 || (i & 1073741824) == 1073741824) {
            this.mHasPerformedLongPress = false;
            if (this.mPendingCheckForLongPress == null) {
                this.mPendingCheckForLongPress = new CheckForLongPress();
            }
            this.mPendingCheckForLongPress.setAnchor(x, y);
            this.mPendingCheckForLongPress.rememberWindowAttachCount();
            this.mPendingCheckForLongPress.rememberPressedState();
            this.mPendingCheckForLongPress.setClassification(classification);
            postDelayed(this.mPendingCheckForLongPress, delay);
        }
    }

    public static View inflate(Context context, int resource, ViewGroup root) {
        LayoutInflater factory = LayoutInflater.from(context);
        return factory.inflate(resource, root);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int maxOverScrollX2;
        int maxOverScrollY2;
        boolean clampedX;
        boolean clampedY;
        int overScrollMode = this.mOverScrollMode;
        boolean canScrollHorizontal = computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        boolean canScrollVertical = computeVerticalScrollRange() > computeVerticalScrollExtent();
        boolean overScrollHorizontal = overScrollMode == 0 || (overScrollMode == 1 && canScrollHorizontal);
        boolean overScrollVertical = overScrollMode == 0 || (overScrollMode == 1 && canScrollVertical);
        int newScrollX = scrollX + deltaX;
        if (overScrollHorizontal) {
            maxOverScrollX2 = maxOverScrollX;
        } else {
            maxOverScrollX2 = 0;
        }
        int newScrollY = scrollY + deltaY;
        if (overScrollVertical) {
            maxOverScrollY2 = maxOverScrollY;
        } else {
            maxOverScrollY2 = 0;
        }
        int left = -maxOverScrollX2;
        int right = maxOverScrollX2 + scrollRangeX;
        int top = -maxOverScrollY2;
        int bottom = maxOverScrollY2 + scrollRangeY;
        if (newScrollX > right) {
            newScrollX = right;
            clampedX = true;
        } else if (newScrollX >= left) {
            clampedX = false;
        } else {
            newScrollX = left;
            clampedX = true;
        }
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY >= top) {
            clampedY = false;
        } else {
            newScrollY = top;
            clampedY = true;
        }
        onOverScrolled(newScrollX, newScrollY, clampedX, clampedY);
        return clampedX || clampedY;
    }

    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
    }

    public int getOverScrollMode() {
        return this.mOverScrollMode;
    }

    public void setOverScrollMode(int overScrollMode) {
        if (overScrollMode != 0 && overScrollMode != 1 && overScrollMode != 2) {
            throw new IllegalArgumentException("Invalid overscroll mode " + overScrollMode);
        }
        this.mOverScrollMode = overScrollMode;
    }

    public void setNestedScrollingEnabled(boolean enabled) {
        if (enabled) {
            this.mPrivateFlags3 |= 128;
            return;
        }
        stopNestedScroll();
        this.mPrivateFlags3 &= PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
    }

    public boolean isNestedScrollingEnabled() {
        return (this.mPrivateFlags3 & 128) == 128;
    }

    public boolean startNestedScroll(int axes) {
        if (hasNestedScrollingParent()) {
            return true;
        }
        if (isNestedScrollingEnabled()) {
            View child = this;
            for (ViewParent p = getParent(); p != null; p = p.getParent()) {
                try {
                    if (p.onStartNestedScroll(child, this, axes)) {
                        this.mNestedScrollingParent = p;
                        p.onNestedScrollAccepted(child, this, axes);
                        return true;
                    }
                } catch (AbstractMethodError e) {
                    Log.m109e(VIEW_LOG_TAG, "ViewParent " + p + " does not implement interface method onStartNestedScroll", e);
                }
                if (p instanceof View) {
                    View child2 = p;
                    child = child2;
                }
            }
            return false;
        }
        return false;
    }

    public void stopNestedScroll() {
        ViewParent viewParent = this.mNestedScrollingParent;
        if (viewParent != null) {
            viewParent.onStopNestedScroll(this);
            this.mNestedScrollingParent = null;
        }
    }

    public boolean hasNestedScrollingParent() {
        return this.mNestedScrollingParent != null;
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        if (isNestedScrollingEnabled() && this.mNestedScrollingParent != null) {
            if (dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0) {
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }
                this.mNestedScrollingParent.onNestedScroll(this, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
                if (offsetInWindow != null) {
                    getLocationInWindow(offsetInWindow);
                    offsetInWindow[0] = offsetInWindow[0] - startX;
                    offsetInWindow[1] = offsetInWindow[1] - startY;
                }
                return true;
            } else if (offsetInWindow != null) {
                offsetInWindow[0] = 0;
                offsetInWindow[1] = 0;
            }
        }
        return false;
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        if (isNestedScrollingEnabled() && this.mNestedScrollingParent != null) {
            if (dx != 0 || dy != 0) {
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }
                if (consumed == null) {
                    if (this.mTempNestedScrollConsumed == null) {
                        this.mTempNestedScrollConsumed = new int[2];
                    }
                    consumed = this.mTempNestedScrollConsumed;
                }
                consumed[0] = 0;
                consumed[1] = 0;
                this.mNestedScrollingParent.onNestedPreScroll(this, dx, dy, consumed);
                if (offsetInWindow != null) {
                    getLocationInWindow(offsetInWindow);
                    offsetInWindow[0] = offsetInWindow[0] - startX;
                    offsetInWindow[1] = offsetInWindow[1] - startY;
                }
                return (consumed[0] == 0 && consumed[1] == 0) ? false : true;
            } else if (offsetInWindow != null) {
                offsetInWindow[0] = 0;
                offsetInWindow[1] = 0;
            }
        }
        return false;
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        ViewParent viewParent;
        if (isNestedScrollingEnabled() && (viewParent = this.mNestedScrollingParent) != null) {
            return viewParent.onNestedFling(this, velocityX, velocityY, consumed);
        }
        return false;
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        ViewParent viewParent;
        if (isNestedScrollingEnabled() && (viewParent = this.mNestedScrollingParent) != null) {
            return viewParent.onNestedPreFling(this, velocityX, velocityY);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getVerticalScrollFactor() {
        if (this.mVerticalScrollFactor == 0.0f) {
            TypedValue outValue = new TypedValue();
            if (!this.mContext.getTheme().resolveAttribute(16842829, outValue, true)) {
                throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
            }
            this.mVerticalScrollFactor = outValue.getDimension(this.mContext.getResources().getDisplayMetrics());
        }
        return this.mVerticalScrollFactor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getHorizontalScrollFactor() {
        return getVerticalScrollFactor();
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, m86to = "INHERIT"), @ViewDebug.IntToString(from = 1, m86to = "FIRST_STRONG"), @ViewDebug.IntToString(from = 2, m86to = "ANY_RTL"), @ViewDebug.IntToString(from = 3, m86to = "LTR"), @ViewDebug.IntToString(from = 4, m86to = "RTL"), @ViewDebug.IntToString(from = 5, m86to = "LOCALE"), @ViewDebug.IntToString(from = 6, m86to = "FIRST_STRONG_LTR"), @ViewDebug.IntToString(from = 7, m86to = "FIRST_STRONG_RTL")})
    public int getRawTextDirection() {
        return (this.mPrivateFlags2 & 448) >> 6;
    }

    public void setTextDirection(int textDirection) {
        if (getRawTextDirection() != textDirection) {
            this.mPrivateFlags2 &= -449;
            resetResolvedTextDirection();
            this.mPrivateFlags2 |= (textDirection << 6) & 448;
            resolveTextDirection();
            onRtlPropertiesChanged(getLayoutDirection());
            requestLayout();
            invalidate(true);
        }
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, m86to = "INHERIT"), @ViewDebug.IntToString(from = 1, m86to = "FIRST_STRONG"), @ViewDebug.IntToString(from = 2, m86to = "ANY_RTL"), @ViewDebug.IntToString(from = 3, m86to = "LTR"), @ViewDebug.IntToString(from = 4, m86to = "RTL"), @ViewDebug.IntToString(from = 5, m86to = "LOCALE"), @ViewDebug.IntToString(from = 6, m86to = "FIRST_STRONG_LTR"), @ViewDebug.IntToString(from = 7, m86to = "FIRST_STRONG_RTL")})
    public int getTextDirection() {
        return (this.mPrivateFlags2 & 7168) >> 10;
    }

    public boolean resolveTextDirection() {
        int parentResolvedDirection;
        this.mPrivateFlags2 &= -7681;
        if (hasRtlSupport()) {
            int textDirection = getRawTextDirection();
            switch (textDirection) {
                case 0:
                    if (!canResolveTextDirection()) {
                        this.mPrivateFlags2 |= 1024;
                        return false;
                    }
                    try {
                        if (!this.mParent.isTextDirectionResolved()) {
                            this.mPrivateFlags2 |= 1024;
                            return false;
                        }
                        try {
                            parentResolvedDirection = this.mParent.getTextDirection();
                        } catch (AbstractMethodError e) {
                            Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                            parentResolvedDirection = 3;
                        }
                        switch (parentResolvedDirection) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                                this.mPrivateFlags2 |= parentResolvedDirection << 10;
                                break;
                            default:
                                this.mPrivateFlags2 |= 1024;
                                break;
                        }
                    } catch (AbstractMethodError e2) {
                        Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e2);
                        this.mPrivateFlags2 |= 1536;
                        return true;
                    }
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    this.mPrivateFlags2 |= textDirection << 10;
                    break;
                default:
                    this.mPrivateFlags2 |= 1024;
                    break;
            }
        } else {
            this.mPrivateFlags2 |= 1024;
        }
        this.mPrivateFlags2 |= 512;
        return true;
    }

    public boolean canResolveTextDirection() {
        switch (getRawTextDirection()) {
            case 0:
                ViewParent viewParent = this.mParent;
                if (viewParent != null) {
                    try {
                        return viewParent.canResolveTextDirection();
                    } catch (AbstractMethodError e) {
                        Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                        return false;
                    }
                }
                return false;
            default:
                return true;
        }
    }

    public void resetResolvedTextDirection() {
        int i = this.mPrivateFlags2 & (-7681);
        this.mPrivateFlags2 = i;
        this.mPrivateFlags2 = i | 1024;
    }

    public boolean isTextDirectionInherited() {
        return getRawTextDirection() == 0;
    }

    public boolean isTextDirectionResolved() {
        return (this.mPrivateFlags2 & 512) == 512;
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, m86to = "INHERIT"), @ViewDebug.IntToString(from = 1, m86to = "GRAVITY"), @ViewDebug.IntToString(from = 2, m86to = "TEXT_START"), @ViewDebug.IntToString(from = 3, m86to = "TEXT_END"), @ViewDebug.IntToString(from = 4, m86to = "CENTER"), @ViewDebug.IntToString(from = 5, m86to = "VIEW_START"), @ViewDebug.IntToString(from = 6, m86to = "VIEW_END")})
    public int getRawTextAlignment() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_ALIGNMENT_MASK) >> 13;
    }

    public void setTextAlignment(int textAlignment) {
        if (textAlignment != getRawTextAlignment()) {
            this.mPrivateFlags2 &= -57345;
            resetResolvedTextAlignment();
            this.mPrivateFlags2 |= (textAlignment << 13) & PFLAG2_TEXT_ALIGNMENT_MASK;
            resolveTextAlignment();
            onRtlPropertiesChanged(getLayoutDirection());
            requestLayout();
            invalidate(true);
        }
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, m86to = "INHERIT"), @ViewDebug.IntToString(from = 1, m86to = "GRAVITY"), @ViewDebug.IntToString(from = 2, m86to = "TEXT_START"), @ViewDebug.IntToString(from = 3, m86to = "TEXT_END"), @ViewDebug.IntToString(from = 4, m86to = "CENTER"), @ViewDebug.IntToString(from = 5, m86to = "VIEW_START"), @ViewDebug.IntToString(from = 6, m86to = "VIEW_END")})
    public int getTextAlignment() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK) >> 17;
    }

    public boolean resolveTextAlignment() {
        int parentResolvedTextAlignment;
        this.mPrivateFlags2 &= -983041;
        if (hasRtlSupport()) {
            int textAlignment = getRawTextAlignment();
            switch (textAlignment) {
                case 0:
                    if (!canResolveTextAlignment()) {
                        this.mPrivateFlags2 |= 131072;
                        return false;
                    }
                    try {
                        if (!this.mParent.isTextAlignmentResolved()) {
                            this.mPrivateFlags2 = 131072 | this.mPrivateFlags2;
                            return false;
                        }
                        try {
                            parentResolvedTextAlignment = this.mParent.getTextAlignment();
                        } catch (AbstractMethodError e) {
                            Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                            parentResolvedTextAlignment = 1;
                        }
                        switch (parentResolvedTextAlignment) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.mPrivateFlags2 |= parentResolvedTextAlignment << 17;
                                break;
                            default:
                                this.mPrivateFlags2 |= 131072;
                                break;
                        }
                    } catch (AbstractMethodError e2) {
                        Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e2);
                        this.mPrivateFlags2 |= 196608;
                        return true;
                    }
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    this.mPrivateFlags2 |= textAlignment << 17;
                    break;
                default:
                    this.mPrivateFlags2 |= 131072;
                    break;
            }
        } else {
            this.mPrivateFlags2 |= 131072;
        }
        this.mPrivateFlags2 |= 65536;
        return true;
    }

    public boolean canResolveTextAlignment() {
        switch (getRawTextAlignment()) {
            case 0:
                ViewParent viewParent = this.mParent;
                if (viewParent != null) {
                    try {
                        return viewParent.canResolveTextAlignment();
                    } catch (AbstractMethodError e) {
                        Log.m109e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                        return false;
                    }
                }
                return false;
            default:
                return true;
        }
    }

    public void resetResolvedTextAlignment() {
        int i = this.mPrivateFlags2 & (-983041);
        this.mPrivateFlags2 = i;
        this.mPrivateFlags2 = i | 131072;
    }

    public boolean isTextAlignmentInherited() {
        return getRawTextAlignment() == 0;
    }

    public boolean isTextAlignmentResolved() {
        return (this.mPrivateFlags2 & 65536) == 65536;
    }

    public static int generateViewId() {
        AtomicInteger atomicInteger;
        int result;
        int newValue;
        do {
            atomicInteger = sNextGeneratedId;
            result = atomicInteger.get();
            newValue = result + 1;
            if (newValue > 16777215) {
                newValue = 1;
            }
        } while (!atomicInteger.compareAndSet(result, newValue));
        return result;
    }

    private static boolean isViewIdGenerated(int id) {
        return ((-16777216) & id) == 0 && (16777215 & id) != 0;
    }

    public void captureTransitioningViews(List<View> transitioningViews) {
        if (getVisibility() == 0) {
            transitioningViews.add(this);
        }
    }

    public void findNamedViews(Map<String, View> namedElements) {
        String transitionName;
        if ((getVisibility() == 0 || this.mGhostView != null) && (transitionName = getTransitionName()) != null) {
            namedElements.put(transitionName, this);
        }
    }

    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        if (isDraggingScrollBar() || isOnScrollbarThumb(x, y)) {
            return PointerIcon.getSystemIcon(this.mContext, 1000);
        }
        return this.mPointerIcon;
    }

    public void setPointerIcon(PointerIcon pointerIcon) {
        this.mPointerIcon = pointerIcon;
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null || attachInfo.mHandlingPointerEvent) {
            return;
        }
        try {
            this.mAttachInfo.mSession.updatePointerIcon(this.mAttachInfo.mWindow);
        } catch (RemoteException e) {
        }
    }

    public PointerIcon getPointerIcon() {
        return this.mPointerIcon;
    }

    public boolean hasPointerCapture() {
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl == null) {
            return false;
        }
        return viewRootImpl.hasPointerCapture();
    }

    public void requestPointerCapture() {
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            viewRootImpl.requestPointerCapture(true);
        }
    }

    public void releasePointerCapture() {
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            viewRootImpl.requestPointerCapture(false);
        }
    }

    public void onPointerCaptureChange(boolean hasCapture) {
    }

    public void dispatchPointerCaptureChanged(boolean hasCapture) {
        onPointerCaptureChange(hasCapture);
    }

    public boolean onCapturedPointerEvent(MotionEvent event) {
        return false;
    }

    public void setOnCapturedPointerListener(OnCapturedPointerListener l) {
        getListenerInfo().mOnCapturedPointerListener = l;
    }

    /* loaded from: classes4.dex */
    public static class MeasureSpec {
        public static final int AT_MOST = Integer.MIN_VALUE;
        public static final int EXACTLY = 1073741824;
        private static final int MODE_MASK = -1073741824;
        private static final int MODE_SHIFT = 30;
        public static final int UNSPECIFIED = 0;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes4.dex */
        public @interface MeasureSpecMode {
        }

        public static int makeMeasureSpec(int size, int mode) {
            if (View.sUseBrokenMakeMeasureSpec) {
                return size + mode;
            }
            return (1073741823 & size) | ((-1073741824) & mode);
        }

        public static int makeSafeMeasureSpec(int size, int mode) {
            if (View.sUseZeroUnspecifiedMeasureSpec && mode == 0) {
                return 0;
            }
            return makeMeasureSpec(size, mode);
        }

        public static int getMode(int measureSpec) {
            return (-1073741824) & measureSpec;
        }

        public static int getSize(int measureSpec) {
            return 1073741823 & measureSpec;
        }

        static int adjust(int measureSpec, int delta) {
            int mode = getMode(measureSpec);
            int size = getSize(measureSpec);
            if (mode == 0) {
                return makeMeasureSpec(size, 0);
            }
            int size2 = size + delta;
            if (size2 < 0) {
                Log.m110e(View.VIEW_LOG_TAG, "MeasureSpec.adjust: new size would be negative! (" + size2 + ") spec: " + toString(measureSpec) + " delta: " + delta);
                size2 = 0;
            }
            return makeMeasureSpec(size2, mode);
        }

        public static String toString(int measureSpec) {
            int mode = getMode(measureSpec);
            int size = getSize(measureSpec);
            StringBuilder sb = new StringBuilder("MeasureSpec: ");
            if (mode == 0) {
                sb.append("UNSPECIFIED ");
            } else if (mode == 1073741824) {
                sb.append("EXACTLY ");
            } else if (mode == Integer.MIN_VALUE) {
                sb.append("AT_MOST ");
            } else {
                sb.append(mode).append(" ");
            }
            sb.append(size);
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class CheckForLongPress implements Runnable {
        private int mClassification;
        private boolean mOriginalPressedState;
        private int mOriginalWindowAttachCount;

        /* renamed from: mX */
        private float f492mX;

        /* renamed from: mY */
        private float f493mY;

        private CheckForLongPress() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mOriginalPressedState == View.this.isPressed() && View.this.mParent != null && this.mOriginalWindowAttachCount == View.this.mWindowAttachCount) {
                View.this.recordGestureClassification(this.mClassification);
                if (View.this.performLongClick(this.f492mX, this.f493mY)) {
                    View.this.mHasPerformedLongPress = true;
                }
            }
        }

        public void setAnchor(float x, float y) {
            this.f492mX = x;
            this.f493mY = y;
        }

        public void rememberWindowAttachCount() {
            this.mOriginalWindowAttachCount = View.this.mWindowAttachCount;
        }

        public void rememberPressedState() {
            this.mOriginalPressedState = View.this.isPressed();
        }

        public void setClassification(int classification) {
            this.mClassification = classification;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class CheckForTap implements Runnable {

        /* renamed from: x */
        public float f494x;

        /* renamed from: y */
        public float f495y;

        private CheckForTap() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.mPrivateFlags &= -33554433;
            View.this.setPressed(true, this.f494x, this.f495y);
            long delay = ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout();
            View.this.checkForLongClick(delay, this.f494x, this.f495y, 3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class PerformClick implements Runnable {
        private PerformClick() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.recordGestureClassification(1);
            View.this.performClickInternal();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recordGestureClassification(int classification) {
        if (classification == 0) {
            return;
        }
        FrameworkStatsLog.write(177, getClass().getName(), classification);
    }

    public ViewPropertyAnimator animate() {
        if (this.mAnimator == null) {
            this.mAnimator = new ViewPropertyAnimator(this);
        }
        return this.mAnimator;
    }

    public final void setTransitionName(String transitionName) {
        this.mTransitionName = transitionName;
    }

    @ViewDebug.ExportedProperty
    public String getTransitionName() {
        return this.mTransitionName;
    }

    public void requestKeyboardShortcuts(List<KeyboardShortcutGroup> data, int deviceId) {
    }

    /* loaded from: classes4.dex */
    public interface OnLongClickListener {
        boolean onLongClick(View view);

        default boolean onLongClickUseDefaultHapticFeedback(View v) {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class UnsetPressedState implements Runnable {
        private UnsetPressedState() {
        }

        @Override // java.lang.Runnable
        public void run() {
            View.this.setPressed(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class VisibilityChangeForAutofillHandler extends Handler {
        private final AutofillManager mAfm;
        private final View mView;

        private VisibilityChangeForAutofillHandler(AutofillManager afm, View view) {
            this.mAfm = afm;
            this.mView = view;
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            AutofillManager autofillManager = this.mAfm;
            View view = this.mView;
            autofillManager.notifyViewVisibilityChanged(view, view.isShown());
        }
    }

    /* loaded from: classes4.dex */
    public static class BaseSavedState extends AbsSavedState {
        static final int AUTOFILL_ID = 4;
        public static final Parcelable.Creator<BaseSavedState> CREATOR = new Parcelable.ClassLoaderCreator<BaseSavedState>() { // from class: android.view.View.BaseSavedState.1
            @Override // android.p008os.Parcelable.Creator
            public BaseSavedState createFromParcel(Parcel in) {
                return new BaseSavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.ClassLoaderCreator
            public BaseSavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new BaseSavedState(in, loader);
            }

            @Override // android.p008os.Parcelable.Creator
            public BaseSavedState[] newArray(int size) {
                return new BaseSavedState[size];
            }
        };
        static final int IS_AUTOFILLED = 2;
        static final int START_ACTIVITY_REQUESTED_WHO_SAVED = 1;
        int mAutofillViewId;
        boolean mHideHighlight;
        boolean mIsAutofilled;
        int mSavedData;
        String mStartActivityRequestWhoSaved;

        public BaseSavedState(Parcel source) {
            this(source, null);
        }

        public BaseSavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.mSavedData = source.readInt();
            this.mStartActivityRequestWhoSaved = source.readString();
            this.mIsAutofilled = source.readBoolean();
            this.mHideHighlight = source.readBoolean();
            this.mAutofillViewId = source.readInt();
        }

        public BaseSavedState(Parcelable superState) {
            super(superState);
        }

        @Override // android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.mSavedData);
            out.writeString(this.mStartActivityRequestWhoSaved);
            out.writeBoolean(this.mIsAutofilled);
            out.writeBoolean(this.mHideHighlight);
            out.writeInt(this.mAutofillViewId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static final class AttachInfo {
        int mAccessibilityFetchFlags;
        Drawable mAccessibilityFocusDrawable;
        boolean mAlwaysConsumeSystemBars;
        float mApplicationScale;
        Drawable mAutofilledDrawable;
        Canvas mCanvas;
        SparseArray<ArrayList<Object>> mContentCaptureEvents;
        ContentCaptureManager mContentCaptureManager;
        Window.OnContentApplyWindowInsetsListener mContentOnApplyWindowInsetsListener;
        int mDisabledSystemUiVisibility;
        Display mDisplay;
        public Surface mDragSurface;
        IBinder mDragToken;
        long mDrawingTime;
        boolean mForceReportNewAttributes;
        final Handler mHandler;
        boolean mHandlingPointerEvent;
        boolean mHardwareAccelerated;
        boolean mHardwareAccelerationRequested;
        boolean mHasNonEmptyGivenInternalInsets;
        boolean mHasSystemUiListeners;
        boolean mHasWindowFocus;
        IWindowId mIWindowId;
        boolean mInTouchMode;
        boolean mKeepScreenOn;
        int mLeashedParentAccessibilityViewId;
        IBinder mLeashedParentToken;
        boolean mNeedsUpdateLightCenter;
        IBinder mPanelParentWindowToken;
        List<RenderNode> mPendingAnimatingRenderNodes;
        boolean mReadyForContentCaptureUpdates;
        boolean mRecomputeGlobalAttributes;
        final Callbacks mRootCallbacks;
        View mRootView;
        boolean mScalingRequired;
        ScrollCaptureInternal mScrollCaptureInternal;
        final IWindowSession mSession;
        int mSystemUiVisibility;
        ThreadedRenderer mThreadedRenderer;
        View mTooltipHost;
        final ViewTreeObserver mTreeObserver;
        boolean mUnbufferedDispatchRequested;
        boolean mUse32BitDrawingCache;
        View mViewRequestingLayout;
        final ViewRootImpl mViewRootImpl;
        boolean mViewScrollChanged;
        boolean mViewVisibilityChanged;
        final IWindow mWindow;
        WindowId mWindowId;
        int mWindowLeft;
        Matrix mWindowMatrixInEmbeddedHierarchy;
        final IBinder mWindowToken;
        int mWindowTop;
        int mWindowVisibility;
        int mDisplayState = 0;
        final Rect mContentInsets = new Rect();
        final Rect mVisibleInsets = new Rect();
        final Rect mStableInsets = new Rect();
        final Rect mCaptionInsets = new Rect();
        final ViewTreeObserver.InternalInsetsInfo mGivenInternalInsets = new ViewTreeObserver.InternalInsetsInfo();
        final ArrayList<View> mScrollContainers = new ArrayList<>();
        final KeyEvent.DispatcherState mKeyDispatchState = new KeyEvent.DispatcherState();
        final int[] mTransparentLocation = new int[2];
        final int[] mInvalidateChildLocation = new int[2];
        final int[] mTmpLocation = new int[2];
        final float[] mTmpTransformLocation = new float[2];
        final Rect mTmpInvalRect = new Rect();
        final RectF mTmpTransformRect = new RectF();
        final RectF mTmpTransformRect1 = new RectF();
        final List<RectF> mTmpRectList = new ArrayList();
        final Matrix mTmpMatrix = new Matrix();
        final Transformation mTmpTransformation = new Transformation();
        final Outline mTmpOutline = new Outline();
        final ArrayList<View> mTempArrayList = new ArrayList<>(24);
        int mAccessibilityWindowId = -1;
        boolean mDebugLayout = DisplayProperties.debug_layout().orElse(false).booleanValue();
        final Point mPoint = new Point();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes4.dex */
        public interface Callbacks {
            boolean performHapticFeedback(int i, boolean z);

            void playSoundEffect(int i);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes4.dex */
        public static class InvalidateInfo {
            private static final int POOL_LIMIT = 10;
            private static final Pools.SynchronizedPool<InvalidateInfo> sPool = new Pools.SynchronizedPool<>(10);
            int bottom;
            int left;
            int right;
            View target;
            int top;

            InvalidateInfo() {
            }

            public static InvalidateInfo obtain() {
                InvalidateInfo instance = sPool.acquire();
                return instance != null ? instance : new InvalidateInfo();
            }

            public void recycle() {
                this.target = null;
                sPool.release(this);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public AttachInfo(IWindowSession session, IWindow window, Display display, ViewRootImpl viewRootImpl, Handler handler, Callbacks effectPlayer, Context context) {
            this.mSession = session;
            this.mWindow = window;
            this.mWindowToken = window.asBinder();
            this.mDisplay = display;
            this.mViewRootImpl = viewRootImpl;
            this.mHandler = handler;
            this.mRootCallbacks = effectPlayer;
            this.mTreeObserver = new ViewTreeObserver(context);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ContentCaptureManager getContentCaptureManager(Context context) {
            ContentCaptureManager contentCaptureManager = this.mContentCaptureManager;
            if (contentCaptureManager != null) {
                return contentCaptureManager;
            }
            ContentCaptureManager contentCaptureManager2 = (ContentCaptureManager) context.getSystemService(ContentCaptureManager.class);
            this.mContentCaptureManager = contentCaptureManager2;
            return contentCaptureManager2;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void delayNotifyContentCaptureInsetsEvent(Insets insets) {
            ContentCaptureManager contentCaptureManager = this.mContentCaptureManager;
            if (contentCaptureManager == null) {
                return;
            }
            ArrayList<Object> events = ensureEvents(contentCaptureManager.getMainContentCaptureSession());
            events.add(insets);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void delayNotifyContentCaptureEvent(ContentCaptureSession session, View view, boolean appeared) {
            ArrayList<Object> events = ensureEvents(session);
            events.add(appeared ? view : view.getAutofillId());
        }

        private ArrayList<Object> ensureEvents(ContentCaptureSession session) {
            if (this.mContentCaptureEvents == null) {
                this.mContentCaptureEvents = new SparseArray<>(1);
            }
            int sessionId = session.getId();
            ArrayList<Object> events = this.mContentCaptureEvents.get(sessionId);
            if (events == null) {
                ArrayList<Object> events2 = new ArrayList<>();
                this.mContentCaptureEvents.put(sessionId, events2);
                return events2;
            }
            return events;
        }

        ScrollCaptureInternal getScrollCaptureInternal() {
            if (this.mScrollCaptureInternal != null) {
                this.mScrollCaptureInternal = new ScrollCaptureInternal();
            }
            return this.mScrollCaptureInternal;
        }

        AttachedSurfaceControl getRootSurfaceControl() {
            return this.mViewRootImpl;
        }

        public void dump(String prefix, PrintWriter writer) {
            String innerPrefix = prefix + "  ";
            writer.println(prefix + "AttachInfo:");
            writer.println(innerPrefix + "mHasWindowFocus=" + this.mHasWindowFocus);
            writer.println(innerPrefix + "mWindowVisibility=" + this.mWindowVisibility);
            writer.println(innerPrefix + "mInTouchMode=" + this.mInTouchMode);
            writer.println(innerPrefix + "mUnbufferedDispatchRequested=" + this.mUnbufferedDispatchRequested);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class ScrollabilityCache implements Runnable {
        public static final int DRAGGING_HORIZONTAL_SCROLL_BAR = 2;
        public static final int DRAGGING_VERTICAL_SCROLL_BAR = 1;
        public static final int FADING = 2;
        public static final int NOT_DRAGGING = 0;
        public static final int OFF = 0;

        /* renamed from: ON */
        public static final int f496ON = 1;
        private static final float[] OPAQUE = {255.0f};
        private static final float[] TRANSPARENT = {0.0f};
        public boolean fadeScrollBars;
        public long fadeStartTime;
        public int fadingEdgeLength;
        public View host;
        public float[] interpolatorValues;
        private int mLastColor;
        public final Matrix matrix;
        public final Paint paint;
        public ScrollBarDrawable scrollBar;
        public int scrollBarMinTouchTarget;
        public int scrollBarSize;
        public Shader shader;
        public final Interpolator scrollBarInterpolator = new Interpolator(1, 2);
        public int state = 0;
        public final Rect mScrollBarBounds = new Rect();
        public final Rect mScrollBarTouchBounds = new Rect();
        public int mScrollBarDraggingState = 0;
        public float mScrollBarDraggingPos = 0.0f;
        public int scrollBarDefaultDelayBeforeFade = ViewConfiguration.getScrollDefaultDelay();
        public int scrollBarFadeDuration = ViewConfiguration.getScrollBarFadeDuration();

        public ScrollabilityCache(ViewConfiguration configuration, View host) {
            this.fadingEdgeLength = configuration.getScaledFadingEdgeLength();
            this.scrollBarSize = configuration.getScaledScrollBarSize();
            this.scrollBarMinTouchTarget = configuration.getScaledMinScrollbarTouchTarget();
            Paint paint = new Paint();
            this.paint = paint;
            this.matrix = new Matrix();
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 1.0f, -16777216, 0, Shader.TileMode.CLAMP);
            this.shader = linearGradient;
            paint.setShader(linearGradient);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            this.host = host;
        }

        public void setFadeColor(int color) {
            if (color != this.mLastColor) {
                this.mLastColor = color;
                if (color != 0) {
                    LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 1.0f, color | (-16777216), color & 16777215, Shader.TileMode.CLAMP);
                    this.shader = linearGradient;
                    this.paint.setShader(linearGradient);
                    this.paint.setXfermode(null);
                    return;
                }
                LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, 0.0f, 1.0f, -16777216, 0, Shader.TileMode.CLAMP);
                this.shader = linearGradient2;
                this.paint.setShader(linearGradient2);
                this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            long now = AnimationUtils.currentAnimationTimeMillis();
            if (now >= this.fadeStartTime) {
                int nextFrame = (int) now;
                Interpolator interpolator = this.scrollBarInterpolator;
                int framesCount = 0 + 1;
                interpolator.setKeyFrame(0, nextFrame, OPAQUE);
                interpolator.setKeyFrame(framesCount, nextFrame + this.scrollBarFadeDuration, TRANSPARENT);
                this.state = 2;
                this.host.invalidate(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SendAccessibilityEventThrottle implements Runnable {
        private AccessibilityEvent mAccessibilityEvent;
        public volatile boolean mIsPending;

        private SendAccessibilityEventThrottle() {
        }

        public void post(AccessibilityEvent accessibilityEvent) {
            updateWithAccessibilityEvent(accessibilityEvent);
            if (!this.mIsPending) {
                this.mIsPending = true;
                View.this.postDelayed(this, ViewConfiguration.getSendRecurringAccessibilityEventsInterval());
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AccessibilityManager.getInstance(View.this.mContext).isEnabled() && View.this.isShown()) {
                View.this.requestParentSendAccessibilityEvent(this.mAccessibilityEvent);
            }
            reset();
        }

        public void updateWithAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            this.mAccessibilityEvent = accessibilityEvent;
        }

        public void reset() {
            this.mIsPending = false;
            this.mAccessibilityEvent = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SendViewScrolledAccessibilityEvent extends SendAccessibilityEventThrottle {
        public int mDeltaX;
        public int mDeltaY;

        private SendViewScrolledAccessibilityEvent() {
            super();
        }

        @Override // android.view.View.SendAccessibilityEventThrottle
        public void updateWithAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            super.updateWithAccessibilityEvent(accessibilityEvent);
            this.mDeltaX += accessibilityEvent.getScrollDeltaX();
            this.mDeltaY += accessibilityEvent.getScrollDeltaY();
            accessibilityEvent.setScrollDeltaX(this.mDeltaX);
            accessibilityEvent.setScrollDeltaY(this.mDeltaY);
        }

        @Override // android.view.View.SendAccessibilityEventThrottle
        public void reset() {
            super.reset();
            this.mDeltaX = 0;
            this.mDeltaY = 0;
        }
    }

    private void cancel(SendAccessibilityEventThrottle callback) {
        if (callback == null || !callback.mIsPending) {
            return;
        }
        removeCallbacks(callback);
        callback.reset();
    }

    /* loaded from: classes4.dex */
    public static class AccessibilityDelegate {
        public void sendAccessibilityEvent(View host, int eventType) {
            host.sendAccessibilityEventInternal(eventType);
        }

        public boolean performAccessibilityAction(View host, int action, Bundle args) {
            return host.performAccessibilityActionInternal(action, args);
        }

        public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
            host.sendAccessibilityEventUncheckedInternal(event);
        }

        public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            return host.dispatchPopulateAccessibilityEventInternal(event);
        }

        public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            host.onPopulateAccessibilityEventInternal(event);
        }

        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            host.onInitializeAccessibilityEventInternal(event);
        }

        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            host.onInitializeAccessibilityNodeInfoInternal(info);
        }

        public void addExtraDataToAccessibilityNodeInfo(View host, AccessibilityNodeInfo info, String extraDataKey, Bundle arguments) {
            host.addExtraDataToAccessibilityNodeInfo(info, extraDataKey, arguments);
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            return host.onRequestSendAccessibilityEventInternal(child, event);
        }

        public AccessibilityNodeProvider getAccessibilityNodeProvider(View host) {
            return null;
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(View host) {
            return host.createAccessibilityNodeInfoInternal();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class MatchIdPredicate implements Predicate<View> {
        public int mId;

        private MatchIdPredicate() {
        }

        @Override // java.util.function.Predicate
        public boolean test(View view) {
            return view.mID == this.mId;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class MatchLabelForPredicate implements Predicate<View> {
        private int mLabeledId;

        private MatchLabelForPredicate() {
        }

        @Override // java.util.function.Predicate
        public boolean test(View view) {
            return view.mLabelForId == this.mLabeledId;
        }
    }

    public int getScrollCaptureHint() {
        return (this.mPrivateFlags4 & 7168) >> 10;
    }

    public void setScrollCaptureHint(int hint) {
        int i = this.mPrivateFlags4 & (-7169);
        this.mPrivateFlags4 = i;
        if ((hint & 1) != 0) {
            hint &= -3;
        }
        this.mPrivateFlags4 = i | ((hint << 10) & 7168);
    }

    public final void setScrollCaptureCallback(ScrollCaptureCallback callback) {
        getListenerInfo().mScrollCaptureCallback = callback;
    }

    public ScrollCaptureCallback createScrollCaptureCallbackInternal(Rect localVisibleRect, Point windowOffset) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo == null) {
            return null;
        }
        if (attachInfo.mScrollCaptureInternal == null) {
            this.mAttachInfo.mScrollCaptureInternal = new ScrollCaptureInternal();
        }
        return this.mAttachInfo.mScrollCaptureInternal.requestCallback(this, localVisibleRect, windowOffset);
    }

    public void dispatchScrollCaptureSearch(Rect localVisibleRect, Point windowOffset, Consumer<ScrollCaptureTarget> targets) {
        onScrollCaptureSearch(localVisibleRect, windowOffset, targets);
    }

    public void onScrollCaptureSearch(Rect localVisibleRect, Point windowOffset, Consumer<ScrollCaptureTarget> targets) {
        int hint = getScrollCaptureHint();
        if ((hint & 1) != 0) {
            return;
        }
        boolean rectIsVisible = true;
        Rect rect = this.mClipBounds;
        if (rect != null) {
            rectIsVisible = localVisibleRect.intersect(rect);
        }
        if (!rectIsVisible) {
            return;
        }
        ListenerInfo listenerInfo = this.mListenerInfo;
        ScrollCaptureCallback callback = listenerInfo == null ? null : listenerInfo.mScrollCaptureCallback;
        if (callback == null) {
            callback = createScrollCaptureCallbackInternal(localVisibleRect, windowOffset);
        }
        if (callback != null) {
            Point offset = new Point(windowOffset.f76x, windowOffset.f77y);
            Rect rect2 = new Rect(localVisibleRect);
            targets.accept(new ScrollCaptureTarget(this, rect2, offset, callback));
        }
    }

    private static void dumpFlags() {
        Field[] declaredFields;
        HashMap<String, String> found = Maps.newHashMap();
        try {
            for (Field field : View.class.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                    if (field.getType().equals(Integer.TYPE)) {
                        int value = field.getInt(null);
                        dumpFlag(found, field.getName(), value);
                    } else if (field.getType().equals(int[].class)) {
                        int[] values = (int[]) field.get(null);
                        for (int i = 0; i < values.length; i++) {
                            dumpFlag(found, field.getName() + NavigationBarInflaterView.SIZE_MOD_START + i + NavigationBarInflaterView.SIZE_MOD_END, values[i]);
                        }
                    }
                }
            }
            ArrayList<String> keys = Lists.newArrayList();
            keys.addAll(found.keySet());
            Collections.sort(keys);
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                Log.m112d(VIEW_LOG_TAG, found.get(key));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dumpFlag(HashMap<String, String> found, String name, int value) {
        String bits = String.format("%32s", Integer.toBinaryString(value)).replace('0', ' ');
        int prefix = name.indexOf(95);
        String key = (prefix > 0 ? name.substring(0, prefix) : name) + bits + name;
        String output = bits + " " + name;
        found.put(key, output);
    }

    public void encode(ViewHierarchyEncoder stream) {
        stream.beginObject(this);
        encodeProperties(stream);
        stream.endObject();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void encodeProperties(ViewHierarchyEncoder stream) {
        Object resolveId = ViewDebug.resolveId(getContext(), this.mID);
        if (resolveId instanceof String) {
            stream.addProperty("id", (String) resolveId);
        } else {
            stream.addProperty("id", this.mID);
        }
        TransformationInfo transformationInfo = this.mTransformationInfo;
        stream.addProperty("misc:transformation.alpha", transformationInfo != null ? transformationInfo.mAlpha : 0.0f);
        stream.addProperty("misc:transitionName", getTransitionName());
        stream.addProperty("layout:left", this.mLeft);
        stream.addProperty("layout:right", this.mRight);
        stream.addProperty("layout:top", this.mTop);
        stream.addProperty("layout:bottom", this.mBottom);
        stream.addProperty("layout:width", getWidth());
        stream.addProperty("layout:height", getHeight());
        stream.addProperty("layout:layoutDirection", getLayoutDirection());
        stream.addProperty("layout:layoutRtl", isLayoutRtl());
        stream.addProperty("layout:hasTransientState", hasTransientState());
        stream.addProperty("layout:baseline", getBaseline());
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            stream.addPropertyKey("layoutParams");
            layoutParams.encode(stream);
        }
        stream.addProperty("scrolling:scrollX", this.mScrollX);
        stream.addProperty("scrolling:scrollY", this.mScrollY);
        stream.addProperty("padding:paddingLeft", this.mPaddingLeft);
        stream.addProperty("padding:paddingRight", this.mPaddingRight);
        stream.addProperty("padding:paddingTop", this.mPaddingTop);
        stream.addProperty("padding:paddingBottom", this.mPaddingBottom);
        stream.addProperty("padding:userPaddingRight", this.mUserPaddingRight);
        stream.addProperty("padding:userPaddingLeft", this.mUserPaddingLeft);
        stream.addProperty("padding:userPaddingBottom", this.mUserPaddingBottom);
        stream.addProperty("padding:userPaddingStart", this.mUserPaddingStart);
        stream.addProperty("padding:userPaddingEnd", this.mUserPaddingEnd);
        stream.addProperty("measurement:minHeight", this.mMinHeight);
        stream.addProperty("measurement:minWidth", this.mMinWidth);
        stream.addProperty("measurement:measuredWidth", this.mMeasuredWidth);
        stream.addProperty("measurement:measuredHeight", this.mMeasuredHeight);
        stream.addProperty("drawing:elevation", getElevation());
        stream.addProperty("drawing:translationX", getTranslationX());
        stream.addProperty("drawing:translationY", getTranslationY());
        stream.addProperty("drawing:translationZ", getTranslationZ());
        stream.addProperty("drawing:rotation", getRotation());
        stream.addProperty("drawing:rotationX", getRotationX());
        stream.addProperty("drawing:rotationY", getRotationY());
        stream.addProperty("drawing:scaleX", getScaleX());
        stream.addProperty("drawing:scaleY", getScaleY());
        stream.addProperty("drawing:pivotX", getPivotX());
        stream.addProperty("drawing:pivotY", getPivotY());
        Rect rect = this.mClipBounds;
        stream.addProperty("drawing:clipBounds", rect == null ? null : rect.toString());
        stream.addProperty("drawing:opaque", isOpaque());
        stream.addProperty("drawing:alpha", getAlpha());
        stream.addProperty("drawing:transitionAlpha", getTransitionAlpha());
        stream.addProperty("drawing:shadow", hasShadow());
        stream.addProperty("drawing:solidColor", getSolidColor());
        stream.addProperty("drawing:layerType", this.mLayerType);
        stream.addProperty("drawing:willNotDraw", willNotDraw());
        stream.addProperty("drawing:hardwareAccelerated", isHardwareAccelerated());
        stream.addProperty("drawing:willNotCacheDrawing", willNotCacheDrawing());
        stream.addProperty("drawing:drawingCacheEnabled", isDrawingCacheEnabled());
        stream.addProperty("drawing:overlappingRendering", hasOverlappingRendering());
        stream.addProperty("drawing:outlineAmbientShadowColor", getOutlineAmbientShadowColor());
        stream.addProperty("drawing:outlineSpotShadowColor", getOutlineSpotShadowColor());
        stream.addProperty("focus:hasFocus", hasFocus());
        stream.addProperty("focus:isFocused", isFocused());
        stream.addProperty("focus:focusable", getFocusable());
        stream.addProperty("focus:isFocusable", isFocusable());
        stream.addProperty("focus:isFocusableInTouchMode", isFocusableInTouchMode());
        stream.addProperty("misc:clickable", isClickable());
        stream.addProperty("misc:pressed", isPressed());
        stream.addProperty("misc:selected", isSelected());
        stream.addProperty("misc:touchMode", isInTouchMode());
        stream.addProperty("misc:hovered", isHovered());
        stream.addProperty("misc:activated", isActivated());
        stream.addProperty("misc:visibility", getVisibility());
        stream.addProperty("misc:fitsSystemWindows", getFitsSystemWindows());
        stream.addProperty("misc:filterTouchesWhenObscured", getFilterTouchesWhenObscured());
        stream.addProperty("misc:enabled", isEnabled());
        stream.addProperty("misc:soundEffectsEnabled", isSoundEffectsEnabled());
        stream.addProperty("misc:hapticFeedbackEnabled", isHapticFeedbackEnabled());
        Resources.Theme theme = getContext().getTheme();
        if (theme != null) {
            stream.addPropertyKey("theme");
            theme.encode(stream);
        }
        String[] strArr = this.mAttributes;
        int n = strArr != null ? strArr.length : 0;
        stream.addProperty("meta:__attrCount__", n / 2);
        for (int i = 0; i < n; i += 2) {
            stream.addProperty("meta:__attr__" + this.mAttributes[i], this.mAttributes[i + 1]);
        }
        stream.addProperty("misc:scrollBarStyle", getScrollBarStyle());
        stream.addProperty("text:textDirection", getTextDirection());
        stream.addProperty("text:textAlignment", getTextAlignment());
        CharSequence contentDescription = getContentDescription();
        stream.addUserProperty("accessibility:contentDescription", contentDescription == null ? "" : contentDescription.toString());
        stream.addProperty("accessibility:labelFor", getLabelFor());
        stream.addProperty("accessibility:importantForAccessibility", getImportantForAccessibility());
    }

    boolean shouldDrawRoundScrollbar() {
        if (!this.mResources.getConfiguration().isScreenRound() || this.mAttachInfo == null) {
            return false;
        }
        View rootView = getRootView();
        WindowInsets insets = getRootWindowInsets();
        int height = getHeight();
        int width = getWidth();
        int displayHeight = rootView.getHeight();
        int displayWidth = rootView.getWidth();
        if (height == displayHeight && width == displayWidth) {
            getLocationInWindow(this.mAttachInfo.mTmpLocation);
            return this.mAttachInfo.mTmpLocation[0] == insets.getStableInsetLeft() && this.mAttachInfo.mTmpLocation[1] == insets.getStableInsetTop();
        }
        return false;
    }

    public void setTooltipText(CharSequence tooltipText) {
        if (TextUtils.isEmpty(tooltipText)) {
            setFlags(0, 1073741824);
            hideTooltip();
            this.mTooltipInfo = null;
            return;
        }
        setFlags(1073741824, 1073741824);
        if (this.mTooltipInfo == null) {
            TooltipInfo tooltipInfo = new TooltipInfo();
            this.mTooltipInfo = tooltipInfo;
            tooltipInfo.mShowTooltipRunnable = new Runnable() { // from class: android.view.View$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    View.this.showHoverTooltip();
                }
            };
            this.mTooltipInfo.mHideTooltipRunnable = new Runnable() { // from class: android.view.View$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    View.this.hideTooltip();
                }
            };
            this.mTooltipInfo.mHoverSlop = ViewConfiguration.get(this.mContext).getScaledHoverSlop();
            this.mTooltipInfo.clearAnchorPos();
        }
        this.mTooltipInfo.mTooltipText = tooltipText;
    }

    public void setTooltip(CharSequence tooltipText) {
        setTooltipText(tooltipText);
    }

    public CharSequence getTooltipText() {
        TooltipInfo tooltipInfo = this.mTooltipInfo;
        if (tooltipInfo != null) {
            return tooltipInfo.mTooltipText;
        }
        return null;
    }

    public CharSequence getTooltip() {
        return getTooltipText();
    }

    private boolean showTooltip(int x, int y, boolean fromLongClick) {
        TooltipInfo tooltipInfo;
        if (this.mAttachInfo == null || (tooltipInfo = this.mTooltipInfo) == null) {
            return false;
        }
        if ((!fromLongClick || (this.mViewFlags & 32) == 0) && !TextUtils.isEmpty(tooltipInfo.mTooltipText)) {
            hideTooltip();
            this.mTooltipInfo.mTooltipFromLongClick = fromLongClick;
            this.mTooltipInfo.mTooltipPopup = new TooltipPopup(getContext());
            boolean fromTouch = (this.mPrivateFlags3 & 131072) == 131072;
            this.mTooltipInfo.mTooltipPopup.show(this, x, y, fromTouch, this.mTooltipInfo.mTooltipText);
            this.mAttachInfo.mTooltipHost = this;
            notifyViewAccessibilityStateChangedIfNeeded(0);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideTooltip() {
        TooltipInfo tooltipInfo = this.mTooltipInfo;
        if (tooltipInfo == null) {
            return;
        }
        removeCallbacks(tooltipInfo.mShowTooltipRunnable);
        if (this.mTooltipInfo.mTooltipPopup == null) {
            return;
        }
        this.mTooltipInfo.mTooltipPopup.hide();
        this.mTooltipInfo.mTooltipPopup = null;
        this.mTooltipInfo.mTooltipFromLongClick = false;
        this.mTooltipInfo.clearAnchorPos();
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mTooltipHost = null;
        }
        notifyViewAccessibilityStateChangedIfNeeded(0);
    }

    private boolean showLongClickTooltip(int x, int y) {
        removeCallbacks(this.mTooltipInfo.mShowTooltipRunnable);
        removeCallbacks(this.mTooltipInfo.mHideTooltipRunnable);
        return showTooltip(x, y, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showHoverTooltip() {
        return showTooltip(this.mTooltipInfo.mAnchorX, this.mTooltipInfo.mAnchorY, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean dispatchTooltipHoverEvent(MotionEvent event) {
        int timeout;
        if (this.mTooltipInfo == null) {
            return false;
        }
        switch (event.getAction()) {
            case 7:
                if ((this.mViewFlags & 1073741824) == 1073741824) {
                    if (!this.mTooltipInfo.mTooltipFromLongClick && this.mTooltipInfo.updateAnchorPos(event)) {
                        if (this.mTooltipInfo.mTooltipPopup == null) {
                            removeCallbacks(this.mTooltipInfo.mShowTooltipRunnable);
                            postDelayed(this.mTooltipInfo.mShowTooltipRunnable, ViewConfiguration.getHoverTooltipShowTimeout());
                        }
                        if ((getWindowSystemUiVisibility() & 1) == 1) {
                            timeout = ViewConfiguration.getHoverTooltipHideShortTimeout();
                        } else {
                            timeout = ViewConfiguration.getHoverTooltipHideTimeout();
                        }
                        removeCallbacks(this.mTooltipInfo.mHideTooltipRunnable);
                        postDelayed(this.mTooltipInfo.mHideTooltipRunnable, timeout);
                    }
                    return true;
                }
                break;
            case 10:
                this.mTooltipInfo.clearAnchorPos();
                if (!this.mTooltipInfo.mTooltipFromLongClick) {
                    hideTooltip();
                    break;
                }
                break;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleTooltipKey(KeyEvent event) {
        switch (event.getAction()) {
            case 0:
                if (event.getRepeatCount() == 0) {
                    hideTooltip();
                    return;
                }
                return;
            case 1:
                handleTooltipUp();
                return;
            default:
                return;
        }
    }

    private void handleTooltipUp() {
        TooltipInfo tooltipInfo = this.mTooltipInfo;
        if (tooltipInfo == null || tooltipInfo.mTooltipPopup == null) {
            return;
        }
        removeCallbacks(this.mTooltipInfo.mHideTooltipRunnable);
        postDelayed(this.mTooltipInfo.mHideTooltipRunnable, ViewConfiguration.getLongPressTooltipHideTimeout());
    }

    private int getFocusableAttribute(TypedArray attributes) {
        TypedValue val = new TypedValue();
        if (attributes.getValue(19, val)) {
            if (val.type == 18) {
                return val.data == 0 ? 0 : 1;
            }
            return val.data;
        }
        return 16;
    }

    public View getTooltipView() {
        TooltipInfo tooltipInfo = this.mTooltipInfo;
        if (tooltipInfo == null || tooltipInfo.mTooltipPopup == null) {
            return null;
        }
        return this.mTooltipInfo.mTooltipPopup.getContentView();
    }

    public static boolean isDefaultFocusHighlightEnabled() {
        return sUseDefaultFocusHighlight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View dispatchUnhandledKeyEvent(KeyEvent evt) {
        if (onUnhandledKeyEvent(evt)) {
            return this;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onUnhandledKeyEvent(KeyEvent event) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null && listenerInfo.mUnhandledKeyListeners != null) {
            for (int i = this.mListenerInfo.mUnhandledKeyListeners.size() - 1; i >= 0; i--) {
                if (((OnUnhandledKeyEventListener) this.mListenerInfo.mUnhandledKeyListeners.get(i)).onUnhandledKeyEvent(this, event)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasUnhandledKeyListener() {
        ListenerInfo listenerInfo = this.mListenerInfo;
        return (listenerInfo == null || listenerInfo.mUnhandledKeyListeners == null || this.mListenerInfo.mUnhandledKeyListeners.isEmpty()) ? false : true;
    }

    public void addOnUnhandledKeyEventListener(OnUnhandledKeyEventListener listener) {
        ArrayList<OnUnhandledKeyEventListener> listeners = getListenerInfo().mUnhandledKeyListeners;
        if (listeners == null) {
            listeners = new ArrayList<>();
            getListenerInfo().mUnhandledKeyListeners = listeners;
        }
        listeners.add(listener);
        if (listeners.size() == 1) {
            ViewParent viewParent = this.mParent;
            if (viewParent instanceof ViewGroup) {
                ((ViewGroup) viewParent).incrementChildUnhandledKeyListeners();
            }
        }
    }

    public void removeOnUnhandledKeyEventListener(OnUnhandledKeyEventListener listener) {
        ListenerInfo listenerInfo = this.mListenerInfo;
        if (listenerInfo != null && listenerInfo.mUnhandledKeyListeners != null && !this.mListenerInfo.mUnhandledKeyListeners.isEmpty()) {
            this.mListenerInfo.mUnhandledKeyListeners.remove(listener);
            if (this.mListenerInfo.mUnhandledKeyListeners.isEmpty()) {
                this.mListenerInfo.mUnhandledKeyListeners = null;
                ViewParent viewParent = this.mParent;
                if (viewParent instanceof ViewGroup) {
                    ((ViewGroup) viewParent).decrementChildUnhandledKeyListeners();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setDetached(boolean detached) {
        if (detached) {
            this.mPrivateFlags4 |= 8192;
        } else {
            this.mPrivateFlags4 &= -8193;
        }
    }

    public void setIsCredential(boolean isCredential) {
        if (isCredential) {
            this.mPrivateFlags4 |= 131072;
        } else {
            this.mPrivateFlags4 &= -131073;
        }
    }

    public boolean isCredential() {
        return (this.mPrivateFlags4 & 131072) == 131072;
    }

    public void setAutoHandwritingEnabled(boolean enabled) {
        if (enabled) {
            this.mPrivateFlags4 |= 65536;
        } else {
            this.mPrivateFlags4 &= -65537;
        }
        updatePositionUpdateListener();
        postUpdate(new View$$ExternalSyntheticLambda7(this));
    }

    public boolean isAutoHandwritingEnabled() {
        return (this.mPrivateFlags4 & 65536) == 65536;
    }

    public boolean isStylusHandwritingAvailable() {
        return ((InputMethodManager) getContext().getSystemService(InputMethodManager.class)).isStylusHandwritingAvailable();
    }

    private void setTraversalTracingEnabled(boolean enabled) {
        if (enabled) {
            if (this.mTracingStrings == null) {
                this.mTracingStrings = new ViewTraversalTracingStrings(this);
            }
            this.mPrivateFlags4 |= 262144;
            return;
        }
        this.mPrivateFlags4 &= -262145;
    }

    private boolean isTraversalTracingEnabled() {
        return (this.mPrivateFlags4 & 262144) == 262144;
    }

    private void setRelayoutTracingEnabled(boolean enabled) {
        if (enabled) {
            if (this.mTracingStrings == null) {
                this.mTracingStrings = new ViewTraversalTracingStrings(this);
            }
            this.mPrivateFlags4 |= 524288;
            return;
        }
        this.mPrivateFlags4 &= -524289;
    }

    private boolean isRelayoutTracingEnabled() {
        return (this.mPrivateFlags4 & 524288) == 524288;
    }

    public void onCreateViewTranslationRequest(int[] supportedFormats, Consumer<ViewTranslationRequest> requestsCollector) {
    }

    public void onCreateVirtualViewTranslationRequests(long[] virtualIds, int[] supportedFormats, Consumer<ViewTranslationRequest> requestsCollector) {
    }

    public ViewTranslationCallback getViewTranslationCallback() {
        return this.mViewTranslationCallback;
    }

    public void setViewTranslationCallback(ViewTranslationCallback callback) {
        this.mViewTranslationCallback = callback;
    }

    public void clearViewTranslationCallback() {
        this.mViewTranslationCallback = null;
    }

    public ViewTranslationResponse getViewTranslationResponse() {
        return this.mViewTranslationResponse;
    }

    public void onViewTranslationResponse(ViewTranslationResponse response) {
        this.mViewTranslationResponse = response;
    }

    public void clearViewTranslationResponse() {
        this.mViewTranslationResponse = null;
    }

    public void onVirtualViewTranslationResponses(LongSparseArray<ViewTranslationResponse> response) {
    }

    public void dispatchCreateViewTranslationRequest(Map<AutofillId, long[]> viewIds, int[] supportedFormats, TranslationCapability capability, final List<ViewTranslationRequest> requests) {
        AutofillId autofillId = getAutofillId();
        if (viewIds.containsKey(autofillId)) {
            if (viewIds.get(autofillId) == null) {
                onCreateViewTranslationRequest(supportedFormats, new ViewTranslationRequestConsumer(requests));
            } else {
                onCreateVirtualViewTranslationRequests(viewIds.get(autofillId), supportedFormats, new Consumer() { // from class: android.view.View$$ExternalSyntheticLambda13
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        requests.add((ViewTranslationRequest) obj);
                    }
                });
            }
        }
    }

    /* loaded from: classes4.dex */
    private class ViewTranslationRequestConsumer implements Consumer<ViewTranslationRequest> {
        private boolean mCalled;
        private final List<ViewTranslationRequest> mRequests;

        ViewTranslationRequestConsumer(List<ViewTranslationRequest> requests) {
            this.mRequests = requests;
        }

        @Override // java.util.function.Consumer
        public void accept(ViewTranslationRequest request) {
            if (this.mCalled) {
                throw new IllegalStateException("The translation Consumer is not reusable.");
            }
            this.mCalled = true;
            if (request != null && request.getKeys().size() > 0) {
                this.mRequests.add(request);
                if (Log.isLoggable(View.CONTENT_CAPTURE_LOG_TAG, 2)) {
                    Log.m106v(View.CONTENT_CAPTURE_LOG_TAG, "Calling setHasTransientState(true) for " + View.this.getAutofillId());
                }
                View.this.setHasTransientState(true);
                View.this.setHasTranslationTransientState(true);
            }
        }
    }

    public void generateDisplayHash(String hashAlgorithm, Rect bounds, final Executor executor, final DisplayHashResultCallback callback) {
        IWindowSession session = getWindowSession();
        if (session == null) {
            callback.onDisplayHashError(-3);
            return;
        }
        IWindow window = getWindow();
        if (window == null) {
            callback.onDisplayHashError(-3);
            return;
        }
        Rect visibleBounds = new Rect();
        getGlobalVisibleRect(visibleBounds);
        if (bounds != null && bounds.isEmpty()) {
            callback.onDisplayHashError(-2);
            return;
        }
        if (bounds != null) {
            bounds.offset(visibleBounds.left, visibleBounds.top);
            visibleBounds.intersectUnchecked(bounds);
        }
        if (visibleBounds.isEmpty()) {
            callback.onDisplayHashError(-4);
            return;
        }
        RemoteCallback remoteCallback = new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: android.view.View$$ExternalSyntheticLambda11
            @Override // android.p008os.RemoteCallback.OnResultListener
            public final void onResult(Bundle bundle) {
                executor.execute(new Runnable() { // from class: android.view.View$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        View.lambda$generateDisplayHash$6(Bundle.this, r2);
                    }
                });
            }
        });
        try {
            session.generateDisplayHash(window, visibleBounds, hashAlgorithm, remoteCallback);
        } catch (RemoteException e) {
            Log.m110e(VIEW_LOG_TAG, "Failed to call generateDisplayHash");
            callback.onDisplayHashError(-1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$generateDisplayHash$6(Bundle result, DisplayHashResultCallback callback) {
        DisplayHash displayHash = (DisplayHash) result.getParcelable(DisplayHashResultCallback.EXTRA_DISPLAY_HASH, DisplayHash.class);
        int errorCode = result.getInt(DisplayHashResultCallback.EXTRA_DISPLAY_HASH_ERROR_CODE, -1);
        if (displayHash != null) {
            callback.onDisplayHashResult(displayHash);
        } else {
            callback.onDisplayHashError(errorCode);
        }
    }

    public AttachedSurfaceControl getRootSurfaceControl() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.getRootSurfaceControl();
        }
        return null;
    }
}
