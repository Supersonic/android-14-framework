package android.view;

import android.graphics.Matrix;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.AudioSystem;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.util.SparseArray;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public final class MotionEvent extends InputEvent implements Parcelable {
    public static final int ACTION_BUTTON_PRESS = 11;
    public static final int ACTION_BUTTON_RELEASE = 12;
    public static final int ACTION_CANCEL = 3;
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_HOVER_ENTER = 9;
    public static final int ACTION_HOVER_EXIT = 10;
    public static final int ACTION_HOVER_MOVE = 7;
    public static final int ACTION_MASK = 255;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_OUTSIDE = 4;
    @Deprecated
    public static final int ACTION_POINTER_1_DOWN = 5;
    @Deprecated
    public static final int ACTION_POINTER_1_UP = 6;
    @Deprecated
    public static final int ACTION_POINTER_2_DOWN = 261;
    @Deprecated
    public static final int ACTION_POINTER_2_UP = 262;
    @Deprecated
    public static final int ACTION_POINTER_3_DOWN = 517;
    @Deprecated
    public static final int ACTION_POINTER_3_UP = 518;
    public static final int ACTION_POINTER_DOWN = 5;
    @Deprecated
    public static final int ACTION_POINTER_ID_MASK = 65280;
    @Deprecated
    public static final int ACTION_POINTER_ID_SHIFT = 8;
    public static final int ACTION_POINTER_INDEX_MASK = 65280;
    public static final int ACTION_POINTER_INDEX_SHIFT = 8;
    public static final int ACTION_POINTER_UP = 6;
    public static final int ACTION_SCROLL = 8;
    public static final int ACTION_UP = 1;
    public static final int AXIS_BRAKE = 23;
    public static final int AXIS_DISTANCE = 24;
    public static final int AXIS_GAS = 22;
    public static final int AXIS_GENERIC_1 = 32;
    public static final int AXIS_GENERIC_10 = 41;
    public static final int AXIS_GENERIC_11 = 42;
    public static final int AXIS_GENERIC_12 = 43;
    public static final int AXIS_GENERIC_13 = 44;
    public static final int AXIS_GENERIC_14 = 45;
    public static final int AXIS_GENERIC_15 = 46;
    public static final int AXIS_GENERIC_16 = 47;
    public static final int AXIS_GENERIC_2 = 33;
    public static final int AXIS_GENERIC_3 = 34;
    public static final int AXIS_GENERIC_4 = 35;
    public static final int AXIS_GENERIC_5 = 36;
    public static final int AXIS_GENERIC_6 = 37;
    public static final int AXIS_GENERIC_7 = 38;
    public static final int AXIS_GENERIC_8 = 39;
    public static final int AXIS_GENERIC_9 = 40;
    public static final int AXIS_GESTURE_PINCH_SCALE_FACTOR = 52;
    public static final int AXIS_GESTURE_SCROLL_X_DISTANCE = 50;
    public static final int AXIS_GESTURE_SCROLL_Y_DISTANCE = 51;
    public static final int AXIS_GESTURE_X_OFFSET = 48;
    public static final int AXIS_GESTURE_Y_OFFSET = 49;
    public static final int AXIS_HAT_X = 15;
    public static final int AXIS_HAT_Y = 16;
    public static final int AXIS_HSCROLL = 10;
    public static final int AXIS_LTRIGGER = 17;
    public static final int AXIS_ORIENTATION = 8;
    public static final int AXIS_PRESSURE = 2;
    public static final int AXIS_RELATIVE_X = 27;
    public static final int AXIS_RELATIVE_Y = 28;
    public static final int AXIS_RTRIGGER = 18;
    public static final int AXIS_RUDDER = 20;
    public static final int AXIS_RX = 12;
    public static final int AXIS_RY = 13;
    public static final int AXIS_RZ = 14;
    public static final int AXIS_SCROLL = 26;
    public static final int AXIS_SIZE = 3;
    private static final SparseArray<String> AXIS_SYMBOLIC_NAMES = new SparseArray<>();
    public static final int AXIS_THROTTLE = 19;
    public static final int AXIS_TILT = 25;
    public static final int AXIS_TOOL_MAJOR = 6;
    public static final int AXIS_TOOL_MINOR = 7;
    public static final int AXIS_TOUCH_MAJOR = 4;
    public static final int AXIS_TOUCH_MINOR = 5;
    public static final int AXIS_VSCROLL = 9;
    public static final int AXIS_WHEEL = 21;
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 11;
    public static final int BUTTON_BACK = 8;
    public static final int BUTTON_FORWARD = 16;
    public static final int BUTTON_PRIMARY = 1;
    public static final int BUTTON_SECONDARY = 2;
    public static final int BUTTON_STYLUS_PRIMARY = 32;
    public static final int BUTTON_STYLUS_SECONDARY = 64;
    private static final String[] BUTTON_SYMBOLIC_NAMES;
    public static final int BUTTON_TERTIARY = 4;
    public static final int CLASSIFICATION_AMBIGUOUS_GESTURE = 1;
    public static final int CLASSIFICATION_DEEP_PRESS = 2;
    public static final int CLASSIFICATION_MULTI_FINGER_SWIPE = 4;
    public static final int CLASSIFICATION_NONE = 0;
    public static final int CLASSIFICATION_PINCH = 5;
    public static final int CLASSIFICATION_TWO_FINGER_SWIPE = 3;
    public static final Parcelable.Creator<MotionEvent> CREATOR;
    private static final boolean DEBUG_CONCISE_TOSTRING = false;
    public static final int EDGE_BOTTOM = 2;
    public static final int EDGE_LEFT = 4;
    public static final int EDGE_RIGHT = 8;
    public static final int EDGE_TOP = 1;
    public static final int FLAG_CANCELED = 32;
    public static final int FLAG_HOVER_EXIT_PENDING = 4;
    public static final int FLAG_IS_ACCESSIBILITY_EVENT = 2048;
    public static final int FLAG_IS_GENERATED_GESTURE = 8;
    public static final int FLAG_NO_FOCUS_CHANGE = 64;
    public static final int FLAG_TAINTED = Integer.MIN_VALUE;
    public static final int FLAG_TARGET_ACCESSIBILITY_FOCUS = 1073741824;
    public static final int FLAG_WINDOW_IS_OBSCURED = 1;
    public static final int FLAG_WINDOW_IS_PARTIALLY_OBSCURED = 2;
    private static final int HISTORY_CURRENT = Integer.MIN_VALUE;
    private static final float INVALID_CURSOR_POSITION = Float.NaN;
    public static final int INVALID_POINTER_ID = -1;
    private static final String LABEL_PREFIX = "AXIS_";
    private static final int MAX_RECYCLED = 10;
    private static final long NS_PER_MS = 1000000;
    private static final String TAG = "MotionEvent";
    public static final int TOOL_TYPE_ERASER = 4;
    public static final int TOOL_TYPE_FINGER = 1;
    public static final int TOOL_TYPE_MOUSE = 3;
    public static final int TOOL_TYPE_PALM = 5;
    public static final int TOOL_TYPE_STYLUS = 2;
    private static final SparseArray<String> TOOL_TYPE_SYMBOLIC_NAMES;
    public static final int TOOL_TYPE_UNKNOWN = 0;
    private static final Object gRecyclerLock;
    private static MotionEvent gRecyclerTop;
    private static int gRecyclerUsed;
    private static final Object gSharedTempLock;
    private static PointerCoords[] gSharedTempPointerCoords;
    private static int[] gSharedTempPointerIndexMap;
    private static PointerProperties[] gSharedTempPointerProperties;
    private long mNativePtr;
    private MotionEvent mNext;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Classification {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ToolType {
    }

    private static native void nativeAddBatch(long j, long j2, PointerCoords[] pointerCoordsArr, int i);

    @FastNative
    private static native void nativeApplyTransform(long j, Matrix matrix);

    private static native int nativeAxisFromString(String str);

    private static native String nativeAxisToString(int i);

    @CriticalNative
    private static native long nativeCopy(long j, long j2, boolean z);

    private static native void nativeDispose(long j);

    @CriticalNative
    private static native int nativeFindPointerIndex(long j, int i);

    @CriticalNative
    private static native int nativeGetAction(long j);

    @CriticalNative
    private static native int nativeGetActionButton(long j);

    @FastNative
    private static native float nativeGetAxisValue(long j, int i, int i2, int i3);

    @CriticalNative
    private static native int nativeGetButtonState(long j);

    @CriticalNative
    private static native int nativeGetClassification(long j);

    @CriticalNative
    private static native int nativeGetDeviceId(long j);

    @CriticalNative
    private static native int nativeGetDisplayId(long j);

    @CriticalNative
    private static native long nativeGetDownTimeNanos(long j);

    @CriticalNative
    private static native int nativeGetEdgeFlags(long j);

    @FastNative
    private static native long nativeGetEventTimeNanos(long j, int i);

    @CriticalNative
    private static native int nativeGetFlags(long j);

    @CriticalNative
    private static native int nativeGetHistorySize(long j);

    @CriticalNative
    private static native int nativeGetId(long j);

    @CriticalNative
    private static native int nativeGetMetaState(long j);

    private static native void nativeGetPointerCoords(long j, int i, int i2, PointerCoords pointerCoords);

    @CriticalNative
    private static native int nativeGetPointerCount(long j);

    @FastNative
    private static native int nativeGetPointerId(long j, int i);

    private static native void nativeGetPointerProperties(long j, int i, PointerProperties pointerProperties);

    @FastNative
    private static native float nativeGetRawAxisValue(long j, int i, int i2, int i3);

    @CriticalNative
    private static native int nativeGetSource(long j);

    @CriticalNative
    private static native int nativeGetSurfaceRotation(long j);

    @FastNative
    private static native int nativeGetToolType(long j, int i);

    @CriticalNative
    private static native float nativeGetXCursorPosition(long j);

    @CriticalNative
    private static native float nativeGetXOffset(long j);

    @CriticalNative
    private static native float nativeGetXPrecision(long j);

    @CriticalNative
    private static native float nativeGetYCursorPosition(long j);

    @CriticalNative
    private static native float nativeGetYOffset(long j);

    @CriticalNative
    private static native float nativeGetYPrecision(long j);

    private static native long nativeInitialize(long j, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, float f, float f2, float f3, float f4, long j2, long j3, int i10, PointerProperties[] pointerPropertiesArr, PointerCoords[] pointerCoordsArr);

    @CriticalNative
    private static native boolean nativeIsTouchEvent(long j);

    @CriticalNative
    private static native void nativeOffsetLocation(long j, float f, float f2);

    private static native long nativeReadFromParcel(long j, Parcel parcel);

    @CriticalNative
    private static native void nativeScale(long j, float f);

    @CriticalNative
    private static native void nativeSetAction(long j, int i);

    @CriticalNative
    private static native void nativeSetActionButton(long j, int i);

    @CriticalNative
    private static native void nativeSetButtonState(long j, int i);

    @CriticalNative
    private static native void nativeSetCursorPosition(long j, float f, float f2);

    @CriticalNative
    private static native void nativeSetDisplayId(long j, int i);

    @CriticalNative
    private static native void nativeSetDownTimeNanos(long j, long j2);

    @CriticalNative
    private static native void nativeSetEdgeFlags(long j, int i);

    @CriticalNative
    private static native void nativeSetFlags(long j, int i);

    @CriticalNative
    private static native void nativeSetSource(long j, int i);

    @FastNative
    private static native void nativeTransform(long j, Matrix matrix);

    private static native void nativeWriteToParcel(long j, Parcel parcel);

    static {
        SparseArray<String> names = AXIS_SYMBOLIC_NAMES;
        names.append(0, "AXIS_X");
        names.append(1, "AXIS_Y");
        names.append(2, "AXIS_PRESSURE");
        names.append(3, "AXIS_SIZE");
        names.append(4, "AXIS_TOUCH_MAJOR");
        names.append(5, "AXIS_TOUCH_MINOR");
        names.append(6, "AXIS_TOOL_MAJOR");
        names.append(7, "AXIS_TOOL_MINOR");
        names.append(8, "AXIS_ORIENTATION");
        names.append(9, "AXIS_VSCROLL");
        names.append(10, "AXIS_HSCROLL");
        names.append(11, "AXIS_Z");
        names.append(12, "AXIS_RX");
        names.append(13, "AXIS_RY");
        names.append(14, "AXIS_RZ");
        names.append(15, "AXIS_HAT_X");
        names.append(16, "AXIS_HAT_Y");
        names.append(17, "AXIS_LTRIGGER");
        names.append(18, "AXIS_RTRIGGER");
        names.append(19, "AXIS_THROTTLE");
        names.append(20, "AXIS_RUDDER");
        names.append(21, "AXIS_WHEEL");
        names.append(22, "AXIS_GAS");
        names.append(23, "AXIS_BRAKE");
        names.append(24, "AXIS_DISTANCE");
        names.append(25, "AXIS_TILT");
        names.append(26, "AXIS_SCROLL");
        names.append(27, "AXIS_REALTIVE_X");
        names.append(28, "AXIS_REALTIVE_Y");
        names.append(32, "AXIS_GENERIC_1");
        names.append(33, "AXIS_GENERIC_2");
        names.append(34, "AXIS_GENERIC_3");
        names.append(35, "AXIS_GENERIC_4");
        names.append(36, "AXIS_GENERIC_5");
        names.append(37, "AXIS_GENERIC_6");
        names.append(38, "AXIS_GENERIC_7");
        names.append(39, "AXIS_GENERIC_8");
        names.append(40, "AXIS_GENERIC_9");
        names.append(41, "AXIS_GENERIC_10");
        names.append(42, "AXIS_GENERIC_11");
        names.append(43, "AXIS_GENERIC_12");
        names.append(44, "AXIS_GENERIC_13");
        names.append(45, "AXIS_GENERIC_14");
        names.append(46, "AXIS_GENERIC_15");
        names.append(47, "AXIS_GENERIC_16");
        names.append(48, "AXIS_GESTURE_X_OFFSET");
        names.append(49, "AXIS_GESTURE_Y_OFFSET");
        names.append(50, "AXIS_GESTURE_SCROLL_X_DISTANCE");
        names.append(51, "AXIS_GESTURE_SCROLL_Y_DISTANCE");
        names.append(52, "AXIS_GESTURE_PINCH_SCALE_FACTOR");
        BUTTON_SYMBOLIC_NAMES = new String[]{"BUTTON_PRIMARY", "BUTTON_SECONDARY", "BUTTON_TERTIARY", "BUTTON_BACK", "BUTTON_FORWARD", "BUTTON_STYLUS_PRIMARY", "BUTTON_STYLUS_SECONDARY", "0x00000080", "0x00000100", "0x00000200", "0x00000400", "0x00000800", "0x00001000", "0x00002000", "0x00004000", "0x00008000", "0x00010000", "0x00020000", "0x00040000", "0x00080000", "0x00100000", "0x00200000", "0x00400000", "0x00800000", "0x01000000", "0x02000000", "0x04000000", "0x08000000", "0x10000000", "0x20000000", "0x40000000", "0x80000000"};
        TOOL_TYPE_SYMBOLIC_NAMES = new SparseArray<>();
        SparseArray<String> names2 = TOOL_TYPE_SYMBOLIC_NAMES;
        names2.append(0, "TOOL_TYPE_UNKNOWN");
        names2.append(1, "TOOL_TYPE_FINGER");
        names2.append(2, "TOOL_TYPE_STYLUS");
        names2.append(3, "TOOL_TYPE_MOUSE");
        names2.append(4, "TOOL_TYPE_ERASER");
        gRecyclerLock = new Object();
        gSharedTempLock = new Object();
        CREATOR = new Parcelable.Creator<MotionEvent>() { // from class: android.view.MotionEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public MotionEvent createFromParcel(Parcel in) {
                in.readInt();
                return MotionEvent.createFromParcelBody(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public MotionEvent[] newArray(int size) {
                return new MotionEvent[size];
            }
        };
    }

    private static final void ensureSharedTempPointerCapacity(int desiredCapacity) {
        PointerCoords[] pointerCoordsArr = gSharedTempPointerCoords;
        if (pointerCoordsArr == null || pointerCoordsArr.length < desiredCapacity) {
            int capacity = pointerCoordsArr != null ? pointerCoordsArr.length : 8;
            while (capacity < desiredCapacity) {
                capacity *= 2;
            }
            gSharedTempPointerCoords = PointerCoords.createArray(capacity);
            gSharedTempPointerProperties = PointerProperties.createArray(capacity);
            gSharedTempPointerIndexMap = new int[capacity];
        }
    }

    private MotionEvent() {
    }

    protected void finalize() throws Throwable {
        try {
            long j = this.mNativePtr;
            if (j != 0) {
                nativeDispose(j);
                this.mNativePtr = 0L;
            }
        } finally {
            super.finalize();
        }
    }

    private static MotionEvent obtain() {
        synchronized (gRecyclerLock) {
            MotionEvent ev = gRecyclerTop;
            if (ev == null) {
                return new MotionEvent();
            }
            gRecyclerTop = ev.mNext;
            gRecyclerUsed--;
            ev.mNext = null;
            ev.prepareForReuse();
            return ev;
        }
    }

    public static MotionEvent obtain(long downTime, long eventTime, int action, int pointerCount, PointerProperties[] pointerProperties, PointerCoords[] pointerCoords, int metaState, int buttonState, float xPrecision, float yPrecision, int deviceId, int edgeFlags, int source, int displayId, int flags, int classification) {
        MotionEvent ev = obtain();
        boolean success = ev.initialize(deviceId, source, displayId, action, flags, edgeFlags, metaState, buttonState, classification, 0.0f, 0.0f, xPrecision, yPrecision, downTime * 1000000, 1000000 * eventTime, pointerCount, pointerProperties, pointerCoords);
        if (!success) {
            Log.m110e(TAG, "Could not initialize MotionEvent");
            ev.recycle();
            return null;
        }
        return ev;
    }

    public static MotionEvent obtain(long downTime, long eventTime, int action, int pointerCount, PointerProperties[] pointerProperties, PointerCoords[] pointerCoords, int metaState, int buttonState, float xPrecision, float yPrecision, int deviceId, int edgeFlags, int source, int displayId, int flags) {
        return obtain(downTime, eventTime, action, pointerCount, pointerProperties, pointerCoords, metaState, buttonState, xPrecision, yPrecision, deviceId, edgeFlags, source, displayId, flags, 0);
    }

    public static MotionEvent obtain(long downTime, long eventTime, int action, int pointerCount, PointerProperties[] pointerProperties, PointerCoords[] pointerCoords, int metaState, int buttonState, float xPrecision, float yPrecision, int deviceId, int edgeFlags, int source, int flags) {
        return obtain(downTime, eventTime, action, pointerCount, pointerProperties, pointerCoords, metaState, buttonState, xPrecision, yPrecision, deviceId, edgeFlags, source, 0, flags);
    }

    @Deprecated
    public static MotionEvent obtain(long downTime, long eventTime, int action, int pointerCount, int[] pointerIds, PointerCoords[] pointerCoords, int metaState, float xPrecision, float yPrecision, int deviceId, int edgeFlags, int source, int flags) {
        MotionEvent obtain;
        synchronized (gSharedTempLock) {
            ensureSharedTempPointerCapacity(pointerCount);
            PointerProperties[] pp = gSharedTempPointerProperties;
            for (int i = 0; i < pointerCount; i++) {
                pp[i].clear();
                pp[i].f484id = pointerIds[i];
            }
            obtain = obtain(downTime, eventTime, action, pointerCount, pp, pointerCoords, metaState, 0, xPrecision, yPrecision, deviceId, edgeFlags, source, flags);
        }
        return obtain;
    }

    public static MotionEvent obtain(long downTime, long eventTime, int action, float x, float y, float pressure, float size, int metaState, float xPrecision, float yPrecision, int deviceId, int edgeFlags) {
        return obtain(downTime, eventTime, action, x, y, pressure, size, metaState, xPrecision, yPrecision, deviceId, edgeFlags, 2, 0);
    }

    public static MotionEvent obtain(long downTime, long eventTime, int action, float x, float y, float pressure, float size, int metaState, float xPrecision, float yPrecision, int deviceId, int edgeFlags, int source, int displayId) {
        MotionEvent ev = obtain();
        synchronized (gSharedTempLock) {
            ensureSharedTempPointerCapacity(1);
            PointerProperties[] pp = gSharedTempPointerProperties;
            pp[0].clear();
            pp[0].f484id = 0;
            PointerCoords[] pc = gSharedTempPointerCoords;
            pc[0].clear();
            pc[0].f482x = x;
            pc[0].f483y = y;
            pc[0].pressure = pressure;
            pc[0].size = size;
            ev.initialize(deviceId, source, displayId, action, 0, edgeFlags, metaState, 0, 0, 0.0f, 0.0f, xPrecision, yPrecision, downTime * 1000000, eventTime * 1000000, 1, pp, pc);
        }
        return ev;
    }

    @Deprecated
    public static MotionEvent obtain(long downTime, long eventTime, int action, int pointerCount, float x, float y, float pressure, float size, int metaState, float xPrecision, float yPrecision, int deviceId, int edgeFlags) {
        return obtain(downTime, eventTime, action, x, y, pressure, size, metaState, xPrecision, yPrecision, deviceId, edgeFlags);
    }

    public static MotionEvent obtain(long downTime, long eventTime, int action, float x, float y, int metaState) {
        return obtain(downTime, eventTime, action, x, y, 1.0f, 1.0f, metaState, 1.0f, 1.0f, 0, 0);
    }

    public static MotionEvent obtain(MotionEvent other) {
        if (other == null) {
            throw new IllegalArgumentException("other motion event must not be null");
        }
        MotionEvent ev = obtain();
        ev.mNativePtr = nativeCopy(ev.mNativePtr, other.mNativePtr, true);
        return ev;
    }

    public static MotionEvent obtainNoHistory(MotionEvent other) {
        if (other == null) {
            throw new IllegalArgumentException("other motion event must not be null");
        }
        MotionEvent ev = obtain();
        ev.mNativePtr = nativeCopy(ev.mNativePtr, other.mNativePtr, false);
        return ev;
    }

    private boolean initialize(int deviceId, int source, int displayId, int action, int flags, int edgeFlags, int metaState, int buttonState, int classification, float xOffset, float yOffset, float xPrecision, float yPrecision, long downTimeNanos, long eventTimeNanos, int pointerCount, PointerProperties[] pointerIds, PointerCoords[] pointerCoords) {
        long nativeInitialize = nativeInitialize(this.mNativePtr, deviceId, source, displayId, action, flags, edgeFlags, metaState, buttonState, classification, xOffset, yOffset, xPrecision, yPrecision, downTimeNanos, eventTimeNanos, pointerCount, pointerIds, pointerCoords);
        this.mNativePtr = nativeInitialize;
        if (nativeInitialize == 0) {
            return false;
        }
        updateCursorPosition();
        return true;
    }

    @Override // android.view.InputEvent
    public MotionEvent copy() {
        return obtain(this);
    }

    @Override // android.view.InputEvent
    public final void recycle() {
        super.recycle();
        synchronized (gRecyclerLock) {
            int i = gRecyclerUsed;
            if (i < 10) {
                gRecyclerUsed = i + 1;
                this.mNext = gRecyclerTop;
                gRecyclerTop = this;
            }
        }
    }

    public final void scale(float scale) {
        if (scale != 1.0f) {
            nativeScale(this.mNativePtr, scale);
        }
    }

    @Override // android.view.InputEvent
    public int getId() {
        return nativeGetId(this.mNativePtr);
    }

    @Override // android.view.InputEvent
    public final int getDeviceId() {
        return nativeGetDeviceId(this.mNativePtr);
    }

    @Override // android.view.InputEvent
    public final int getSource() {
        return nativeGetSource(this.mNativePtr);
    }

    @Override // android.view.InputEvent
    public final void setSource(int source) {
        if (source == getSource()) {
            return;
        }
        nativeSetSource(this.mNativePtr, source);
        updateCursorPosition();
    }

    @Override // android.view.InputEvent
    public int getDisplayId() {
        return nativeGetDisplayId(this.mNativePtr);
    }

    @Override // android.view.InputEvent
    public void setDisplayId(int displayId) {
        nativeSetDisplayId(this.mNativePtr, displayId);
    }

    public final int getAction() {
        return nativeGetAction(this.mNativePtr);
    }

    public final int getActionMasked() {
        return nativeGetAction(this.mNativePtr) & 255;
    }

    public final int getActionIndex() {
        return (nativeGetAction(this.mNativePtr) & 65280) >> 8;
    }

    public final boolean isTouchEvent() {
        return nativeIsTouchEvent(this.mNativePtr);
    }

    public boolean isStylusPointer() {
        int actionIndex = getActionIndex();
        return isFromSource(16386) && (getToolType(actionIndex) == 2 || getToolType(actionIndex) == 4);
    }

    public boolean isHoverEvent() {
        return getActionMasked() == 9 || getActionMasked() == 10 || getActionMasked() == 7;
    }

    public final int getFlags() {
        return nativeGetFlags(this.mNativePtr);
    }

    @Override // android.view.InputEvent
    public final boolean isTainted() {
        int flags = getFlags();
        return (Integer.MIN_VALUE & flags) != 0;
    }

    @Override // android.view.InputEvent
    public final void setTainted(boolean tainted) {
        int flags = getFlags();
        nativeSetFlags(this.mNativePtr, tainted ? Integer.MIN_VALUE | flags : Integer.MAX_VALUE & flags);
    }

    public boolean isTargetAccessibilityFocus() {
        int flags = getFlags();
        return (1073741824 & flags) != 0;
    }

    public void setTargetAccessibilityFocus(boolean targetsFocus) {
        int i;
        int flags = getFlags();
        long j = this.mNativePtr;
        if (targetsFocus) {
            i = 1073741824 | flags;
        } else {
            i = (-1073741825) & flags;
        }
        nativeSetFlags(j, i);
    }

    public final boolean isHoverExitPending() {
        int flags = getFlags();
        return (flags & 4) != 0;
    }

    public void setHoverExitPending(boolean hoverExitPending) {
        int i;
        int flags = getFlags();
        long j = this.mNativePtr;
        if (hoverExitPending) {
            i = flags | 4;
        } else {
            i = flags & (-5);
        }
        nativeSetFlags(j, i);
    }

    public final long getDownTime() {
        return nativeGetDownTimeNanos(this.mNativePtr) / 1000000;
    }

    public final void setDownTime(long downTime) {
        nativeSetDownTimeNanos(this.mNativePtr, 1000000 * downTime);
    }

    @Override // android.view.InputEvent
    public final long getEventTime() {
        return nativeGetEventTimeNanos(this.mNativePtr, Integer.MIN_VALUE) / 1000000;
    }

    @Override // android.view.InputEvent
    public final long getEventTimeNano() {
        return nativeGetEventTimeNanos(this.mNativePtr, Integer.MIN_VALUE);
    }

    public final float getX() {
        return nativeGetAxisValue(this.mNativePtr, 0, 0, Integer.MIN_VALUE);
    }

    public final float getY() {
        return nativeGetAxisValue(this.mNativePtr, 1, 0, Integer.MIN_VALUE);
    }

    public final float getPressure() {
        return nativeGetAxisValue(this.mNativePtr, 2, 0, Integer.MIN_VALUE);
    }

    public final float getSize() {
        return nativeGetAxisValue(this.mNativePtr, 3, 0, Integer.MIN_VALUE);
    }

    public final float getTouchMajor() {
        return nativeGetAxisValue(this.mNativePtr, 4, 0, Integer.MIN_VALUE);
    }

    public final float getTouchMinor() {
        return nativeGetAxisValue(this.mNativePtr, 5, 0, Integer.MIN_VALUE);
    }

    public final float getToolMajor() {
        return nativeGetAxisValue(this.mNativePtr, 6, 0, Integer.MIN_VALUE);
    }

    public final float getToolMinor() {
        return nativeGetAxisValue(this.mNativePtr, 7, 0, Integer.MIN_VALUE);
    }

    public final float getOrientation() {
        return nativeGetAxisValue(this.mNativePtr, 8, 0, Integer.MIN_VALUE);
    }

    public final float getAxisValue(int axis) {
        return nativeGetAxisValue(this.mNativePtr, axis, 0, Integer.MIN_VALUE);
    }

    public final int getPointerCount() {
        return nativeGetPointerCount(this.mNativePtr);
    }

    public final int getPointerId(int pointerIndex) {
        return nativeGetPointerId(this.mNativePtr, pointerIndex);
    }

    public int getToolType(int pointerIndex) {
        return nativeGetToolType(this.mNativePtr, pointerIndex);
    }

    public final int findPointerIndex(int pointerId) {
        return nativeFindPointerIndex(this.mNativePtr, pointerId);
    }

    public final float getX(int pointerIndex) {
        return nativeGetAxisValue(this.mNativePtr, 0, pointerIndex, Integer.MIN_VALUE);
    }

    public final float getY(int pointerIndex) {
        return nativeGetAxisValue(this.mNativePtr, 1, pointerIndex, Integer.MIN_VALUE);
    }

    public final float getPressure(int pointerIndex) {
        return nativeGetAxisValue(this.mNativePtr, 2, pointerIndex, Integer.MIN_VALUE);
    }

    public final float getSize(int pointerIndex) {
        return nativeGetAxisValue(this.mNativePtr, 3, pointerIndex, Integer.MIN_VALUE);
    }

    public final float getTouchMajor(int pointerIndex) {
        return nativeGetAxisValue(this.mNativePtr, 4, pointerIndex, Integer.MIN_VALUE);
    }

    public final float getTouchMinor(int pointerIndex) {
        return nativeGetAxisValue(this.mNativePtr, 5, pointerIndex, Integer.MIN_VALUE);
    }

    public final float getToolMajor(int pointerIndex) {
        return nativeGetAxisValue(this.mNativePtr, 6, pointerIndex, Integer.MIN_VALUE);
    }

    public final float getToolMinor(int pointerIndex) {
        return nativeGetAxisValue(this.mNativePtr, 7, pointerIndex, Integer.MIN_VALUE);
    }

    public final float getOrientation(int pointerIndex) {
        return nativeGetAxisValue(this.mNativePtr, 8, pointerIndex, Integer.MIN_VALUE);
    }

    public final float getAxisValue(int axis, int pointerIndex) {
        return nativeGetAxisValue(this.mNativePtr, axis, pointerIndex, Integer.MIN_VALUE);
    }

    public final void getPointerCoords(int pointerIndex, PointerCoords outPointerCoords) {
        nativeGetPointerCoords(this.mNativePtr, pointerIndex, Integer.MIN_VALUE, outPointerCoords);
    }

    public final void getPointerProperties(int pointerIndex, PointerProperties outPointerProperties) {
        nativeGetPointerProperties(this.mNativePtr, pointerIndex, outPointerProperties);
    }

    public final int getMetaState() {
        return nativeGetMetaState(this.mNativePtr);
    }

    public final int getButtonState() {
        return nativeGetButtonState(this.mNativePtr);
    }

    public final void setButtonState(int buttonState) {
        nativeSetButtonState(this.mNativePtr, buttonState);
    }

    public int getClassification() {
        return nativeGetClassification(this.mNativePtr);
    }

    public final int getActionButton() {
        return nativeGetActionButton(this.mNativePtr);
    }

    public final void setActionButton(int button) {
        nativeSetActionButton(this.mNativePtr, button);
    }

    public final float getRawX() {
        return nativeGetRawAxisValue(this.mNativePtr, 0, 0, Integer.MIN_VALUE);
    }

    public final float getRawY() {
        return nativeGetRawAxisValue(this.mNativePtr, 1, 0, Integer.MIN_VALUE);
    }

    public float getRawX(int pointerIndex) {
        return nativeGetRawAxisValue(this.mNativePtr, 0, pointerIndex, Integer.MIN_VALUE);
    }

    public float getRawY(int pointerIndex) {
        return nativeGetRawAxisValue(this.mNativePtr, 1, pointerIndex, Integer.MIN_VALUE);
    }

    public final float getXPrecision() {
        return nativeGetXPrecision(this.mNativePtr);
    }

    public final float getYPrecision() {
        return nativeGetYPrecision(this.mNativePtr);
    }

    public float getXCursorPosition() {
        return nativeGetXCursorPosition(this.mNativePtr);
    }

    public float getYCursorPosition() {
        return nativeGetYCursorPosition(this.mNativePtr);
    }

    private void setCursorPosition(float x, float y) {
        nativeSetCursorPosition(this.mNativePtr, x, y);
    }

    public final int getHistorySize() {
        return nativeGetHistorySize(this.mNativePtr);
    }

    public final long getHistoricalEventTime(int pos) {
        return nativeGetEventTimeNanos(this.mNativePtr, pos) / 1000000;
    }

    public final long getHistoricalEventTimeNano(int pos) {
        return nativeGetEventTimeNanos(this.mNativePtr, pos);
    }

    public final float getHistoricalX(int pos) {
        return nativeGetAxisValue(this.mNativePtr, 0, 0, pos);
    }

    public final float getHistoricalY(int pos) {
        return nativeGetAxisValue(this.mNativePtr, 1, 0, pos);
    }

    public final float getHistoricalPressure(int pos) {
        return nativeGetAxisValue(this.mNativePtr, 2, 0, pos);
    }

    public final float getHistoricalSize(int pos) {
        return nativeGetAxisValue(this.mNativePtr, 3, 0, pos);
    }

    public final float getHistoricalTouchMajor(int pos) {
        return nativeGetAxisValue(this.mNativePtr, 4, 0, pos);
    }

    public final float getHistoricalTouchMinor(int pos) {
        return nativeGetAxisValue(this.mNativePtr, 5, 0, pos);
    }

    public final float getHistoricalToolMajor(int pos) {
        return nativeGetAxisValue(this.mNativePtr, 6, 0, pos);
    }

    public final float getHistoricalToolMinor(int pos) {
        return nativeGetAxisValue(this.mNativePtr, 7, 0, pos);
    }

    public final float getHistoricalOrientation(int pos) {
        return nativeGetAxisValue(this.mNativePtr, 8, 0, pos);
    }

    public final float getHistoricalAxisValue(int axis, int pos) {
        return nativeGetAxisValue(this.mNativePtr, axis, 0, pos);
    }

    public final float getHistoricalX(int pointerIndex, int pos) {
        return nativeGetAxisValue(this.mNativePtr, 0, pointerIndex, pos);
    }

    public final float getHistoricalY(int pointerIndex, int pos) {
        return nativeGetAxisValue(this.mNativePtr, 1, pointerIndex, pos);
    }

    public final float getHistoricalPressure(int pointerIndex, int pos) {
        return nativeGetAxisValue(this.mNativePtr, 2, pointerIndex, pos);
    }

    public final float getHistoricalSize(int pointerIndex, int pos) {
        return nativeGetAxisValue(this.mNativePtr, 3, pointerIndex, pos);
    }

    public final float getHistoricalTouchMajor(int pointerIndex, int pos) {
        return nativeGetAxisValue(this.mNativePtr, 4, pointerIndex, pos);
    }

    public final float getHistoricalTouchMinor(int pointerIndex, int pos) {
        return nativeGetAxisValue(this.mNativePtr, 5, pointerIndex, pos);
    }

    public final float getHistoricalToolMajor(int pointerIndex, int pos) {
        return nativeGetAxisValue(this.mNativePtr, 6, pointerIndex, pos);
    }

    public final float getHistoricalToolMinor(int pointerIndex, int pos) {
        return nativeGetAxisValue(this.mNativePtr, 7, pointerIndex, pos);
    }

    public final float getHistoricalOrientation(int pointerIndex, int pos) {
        return nativeGetAxisValue(this.mNativePtr, 8, pointerIndex, pos);
    }

    public final float getHistoricalAxisValue(int axis, int pointerIndex, int pos) {
        return nativeGetAxisValue(this.mNativePtr, axis, pointerIndex, pos);
    }

    public final void getHistoricalPointerCoords(int pointerIndex, int pos, PointerCoords outPointerCoords) {
        nativeGetPointerCoords(this.mNativePtr, pointerIndex, pos, outPointerCoords);
    }

    public final int getEdgeFlags() {
        return nativeGetEdgeFlags(this.mNativePtr);
    }

    public final void setEdgeFlags(int flags) {
        nativeSetEdgeFlags(this.mNativePtr, flags);
    }

    public final void setAction(int action) {
        nativeSetAction(this.mNativePtr, action);
    }

    public final void offsetLocation(float deltaX, float deltaY) {
        if (deltaX != 0.0f || deltaY != 0.0f) {
            nativeOffsetLocation(this.mNativePtr, deltaX, deltaY);
        }
    }

    public final void setLocation(float x, float y) {
        float oldX = getX();
        float oldY = getY();
        offsetLocation(x - oldX, y - oldY);
    }

    public final void transform(Matrix matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("matrix must not be null");
        }
        nativeTransform(this.mNativePtr, matrix);
    }

    public void applyTransform(Matrix matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("matrix must not be null");
        }
        nativeApplyTransform(this.mNativePtr, matrix);
    }

    public final void addBatch(long eventTime, float x, float y, float pressure, float size, int metaState) {
        synchronized (gSharedTempLock) {
            ensureSharedTempPointerCapacity(1);
            PointerCoords[] pc = gSharedTempPointerCoords;
            pc[0].clear();
            pc[0].f482x = x;
            pc[0].f483y = y;
            pc[0].pressure = pressure;
            pc[0].size = size;
            nativeAddBatch(this.mNativePtr, 1000000 * eventTime, pc, metaState);
        }
    }

    public final void addBatch(long eventTime, PointerCoords[] pointerCoords, int metaState) {
        nativeAddBatch(this.mNativePtr, 1000000 * eventTime, pointerCoords, metaState);
    }

    public final boolean addBatch(MotionEvent event) {
        int pointerCount;
        int action = nativeGetAction(this.mNativePtr);
        if ((action != 2 && action != 7) || action != nativeGetAction(event.mNativePtr) || nativeGetDeviceId(this.mNativePtr) != nativeGetDeviceId(event.mNativePtr) || nativeGetSource(this.mNativePtr) != nativeGetSource(event.mNativePtr) || nativeGetDisplayId(this.mNativePtr) != nativeGetDisplayId(event.mNativePtr) || nativeGetFlags(this.mNativePtr) != nativeGetFlags(event.mNativePtr) || nativeGetClassification(this.mNativePtr) != nativeGetClassification(event.mNativePtr) || (pointerCount = nativeGetPointerCount(this.mNativePtr)) != nativeGetPointerCount(event.mNativePtr)) {
            return false;
        }
        synchronized (gSharedTempLock) {
            ensureSharedTempPointerCapacity(Math.max(pointerCount, 2));
            PointerProperties[] pp = gSharedTempPointerProperties;
            PointerCoords[] pc = gSharedTempPointerCoords;
            for (int i = 0; i < pointerCount; i++) {
                nativeGetPointerProperties(this.mNativePtr, i, pp[0]);
                nativeGetPointerProperties(event.mNativePtr, i, pp[1]);
                if (!pp[0].equals(pp[1])) {
                    return false;
                }
            }
            int metaState = nativeGetMetaState(event.mNativePtr);
            int historySize = nativeGetHistorySize(event.mNativePtr);
            int h = 0;
            while (h <= historySize) {
                int historyPos = h == historySize ? Integer.MIN_VALUE : h;
                for (int i2 = 0; i2 < pointerCount; i2++) {
                    nativeGetPointerCoords(event.mNativePtr, i2, historyPos, pc[i2]);
                }
                long eventTimeNanos = nativeGetEventTimeNanos(event.mNativePtr, historyPos);
                nativeAddBatch(this.mNativePtr, eventTimeNanos, pc, metaState);
                h++;
            }
            return true;
        }
    }

    public final boolean isWithinBoundsNoHistory(float left, float top, float right, float bottom) {
        int pointerCount = nativeGetPointerCount(this.mNativePtr);
        for (int i = 0; i < pointerCount; i++) {
            float x = nativeGetAxisValue(this.mNativePtr, 0, i, Integer.MIN_VALUE);
            float y = nativeGetAxisValue(this.mNativePtr, 1, i, Integer.MIN_VALUE);
            if (x < left || x > right || y < top || y > bottom) {
                return false;
            }
        }
        return true;
    }

    private static final float clamp(float value, float low, float high) {
        if (value < low) {
            return low;
        }
        if (value > high) {
            return high;
        }
        return value;
    }

    public final MotionEvent clampNoHistory(float left, float top, float right, float bottom) {
        MotionEvent ev = obtain();
        synchronized (gSharedTempLock) {
            try {
                int pointerCount = nativeGetPointerCount(this.mNativePtr);
                ensureSharedTempPointerCapacity(pointerCount);
                PointerProperties[] pp = gSharedTempPointerProperties;
                PointerCoords[] pc = gSharedTempPointerCoords;
                for (int i = 0; i < pointerCount; i++) {
                    try {
                        nativeGetPointerProperties(this.mNativePtr, i, pp[i]);
                        nativeGetPointerCoords(this.mNativePtr, i, Integer.MIN_VALUE, pc[i]);
                        try {
                            pc[i].f482x = clamp(pc[i].f482x, left, right);
                            pc[i].f483y = clamp(pc[i].f483y, top, bottom);
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
                ev.initialize(nativeGetDeviceId(this.mNativePtr), nativeGetSource(this.mNativePtr), nativeGetDisplayId(this.mNativePtr), nativeGetAction(this.mNativePtr), nativeGetFlags(this.mNativePtr), nativeGetEdgeFlags(this.mNativePtr), nativeGetMetaState(this.mNativePtr), nativeGetButtonState(this.mNativePtr), nativeGetClassification(this.mNativePtr), nativeGetXOffset(this.mNativePtr), nativeGetYOffset(this.mNativePtr), nativeGetXPrecision(this.mNativePtr), nativeGetYPrecision(this.mNativePtr), nativeGetDownTimeNanos(this.mNativePtr), nativeGetEventTimeNanos(this.mNativePtr, Integer.MIN_VALUE), pointerCount, pp, pc);
                return ev;
            } catch (Throwable th3) {
                th = th3;
            }
        }
    }

    public final int getPointerIdBits() {
        int idBits = 0;
        int pointerCount = nativeGetPointerCount(this.mNativePtr);
        for (int i = 0; i < pointerCount; i++) {
            idBits |= 1 << nativeGetPointerId(this.mNativePtr, i);
        }
        return idBits;
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0064, code lost:
        r28 = 2;
     */
    /* JADX WARN: Removed duplicated region for block: B:38:0x008b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final MotionEvent split(int idBits) {
        int oldPointerCount;
        PointerProperties[] pp;
        PointerCoords[] pc;
        int[] map;
        int oldAction;
        int oldActionMasked;
        int oldActionPointerIndex;
        int i;
        int newActionPointerIndex;
        int newPointerCount;
        int newActionPointerIndex2;
        int newAction;
        int historySize;
        int h;
        int h2;
        int historySize2;
        int newPointerCount2;
        int oldActionPointerIndex2;
        int oldActionMasked2;
        int oldAction2;
        MotionEvent ev;
        MotionEvent ev2 = this;
        MotionEvent ev3 = obtain();
        synchronized (gSharedTempLock) {
            try {
                try {
                    oldPointerCount = nativeGetPointerCount(ev2.mNativePtr);
                    ensureSharedTempPointerCapacity(oldPointerCount);
                    pp = gSharedTempPointerProperties;
                    pc = gSharedTempPointerCoords;
                    map = gSharedTempPointerIndexMap;
                    oldAction = nativeGetAction(ev2.mNativePtr);
                    oldActionMasked = oldAction & 255;
                    oldActionPointerIndex = (65280 & oldAction) >> 8;
                    i = 0;
                    newActionPointerIndex = -1;
                    newPointerCount = 0;
                } catch (Throwable th) {
                    th = th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
            while (true) {
                newActionPointerIndex2 = 1;
                if (i >= oldPointerCount) {
                    break;
                }
                try {
                    nativeGetPointerProperties(ev2.mNativePtr, i, pp[newPointerCount]);
                    int idBit = 1 << pp[newPointerCount].f484id;
                    if ((idBit & idBits) != 0) {
                        if (i == oldActionPointerIndex) {
                            newActionPointerIndex = newPointerCount;
                        }
                        map[newPointerCount] = i;
                        newPointerCount++;
                    }
                    i++;
                } catch (Throwable th3) {
                    th = th3;
                }
                th = th3;
                throw th;
            }
            if (newPointerCount != 0) {
                if (oldActionMasked != 5 && oldActionMasked != 6) {
                    newAction = oldAction;
                    historySize = nativeGetHistorySize(ev2.mNativePtr);
                    h = 0;
                    while (h <= historySize) {
                        int historyPos = h == historySize ? Integer.MIN_VALUE : h;
                        for (int i2 = 0; i2 < newPointerCount; i2++) {
                            nativeGetPointerCoords(ev2.mNativePtr, map[i2], historyPos, pc[i2]);
                        }
                        long eventTimeNanos = nativeGetEventTimeNanos(ev2.mNativePtr, historyPos);
                        if (h == 0) {
                            int nativeGetDeviceId = nativeGetDeviceId(ev2.mNativePtr);
                            int nativeGetSource = nativeGetSource(ev2.mNativePtr);
                            int nativeGetDisplayId = nativeGetDisplayId(ev2.mNativePtr);
                            int nativeGetFlags = nativeGetFlags(ev2.mNativePtr);
                            int h3 = h;
                            int nativeGetEdgeFlags = nativeGetEdgeFlags(ev2.mNativePtr);
                            int historySize3 = historySize;
                            int nativeGetMetaState = nativeGetMetaState(ev2.mNativePtr);
                            int newPointerCount3 = newPointerCount;
                            int nativeGetButtonState = nativeGetButtonState(ev2.mNativePtr);
                            int oldActionPointerIndex3 = oldActionPointerIndex;
                            int nativeGetClassification = nativeGetClassification(ev2.mNativePtr);
                            oldActionMasked2 = oldActionMasked;
                            float nativeGetXOffset = nativeGetXOffset(ev2.mNativePtr);
                            oldAction2 = oldAction;
                            float nativeGetYOffset = nativeGetYOffset(ev2.mNativePtr);
                            float nativeGetXPrecision = nativeGetXPrecision(ev2.mNativePtr);
                            float nativeGetYPrecision = nativeGetYPrecision(ev2.mNativePtr);
                            long nativeGetDownTimeNanos = nativeGetDownTimeNanos(ev2.mNativePtr);
                            h2 = h3;
                            historySize2 = historySize3;
                            newPointerCount2 = newPointerCount3;
                            oldActionPointerIndex2 = oldActionPointerIndex3;
                            ev = ev3;
                            ev3.initialize(nativeGetDeviceId, nativeGetSource, nativeGetDisplayId, newAction, nativeGetFlags, nativeGetEdgeFlags, nativeGetMetaState, nativeGetButtonState, nativeGetClassification, nativeGetXOffset, nativeGetYOffset, nativeGetXPrecision, nativeGetYPrecision, nativeGetDownTimeNanos, eventTimeNanos, newPointerCount2, pp, pc);
                        } else {
                            h2 = h;
                            historySize2 = historySize;
                            newPointerCount2 = newPointerCount;
                            oldActionPointerIndex2 = oldActionPointerIndex;
                            oldActionMasked2 = oldActionMasked;
                            oldAction2 = oldAction;
                            ev = ev3;
                            nativeAddBatch(ev.mNativePtr, eventTimeNanos, pc, 0);
                        }
                        h = h2 + 1;
                        ev3 = ev;
                        oldActionMasked = oldActionMasked2;
                        oldAction = oldAction2;
                        historySize = historySize2;
                        newPointerCount = newPointerCount2;
                        oldActionPointerIndex = oldActionPointerIndex2;
                        ev2 = this;
                    }
                    return ev3;
                }
                if (newPointerCount == 1) {
                    if (oldActionMasked == 5) {
                        newActionPointerIndex2 = 0;
                    } else if ((getFlags() & 32) != 0) {
                        newActionPointerIndex2 = 3;
                    }
                    newAction = newActionPointerIndex2;
                } else {
                    int newAction2 = newActionPointerIndex << 8;
                    newAction = newAction2 | oldActionMasked;
                }
                historySize = nativeGetHistorySize(ev2.mNativePtr);
                h = 0;
                while (h <= historySize) {
                }
                return ev3;
            }
            throw new IllegalArgumentException("idBits did not match any ids in the event");
        }
    }

    private void updateCursorPosition() {
        if (getSource() != 8194) {
            setCursorPosition(Float.NaN, Float.NaN);
            return;
        }
        float x = 0.0f;
        float y = 0.0f;
        int pointerCount = getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += getX(i);
            y += getY(i);
        }
        setCursorPosition(x / pointerCount, y / pointerCount);
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("MotionEvent { action=").append(actionToString(getAction()));
        appendUnless(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, msg, ", actionButton=", buttonStateToString(getActionButton()));
        int pointerCount = getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            appendUnless(Integer.valueOf(i), msg, ", id[" + i + "]=", Integer.valueOf(getPointerId(i)));
            float x = getX(i);
            float y = getY(i);
            msg.append(", x[").append(i).append("]=").append(x);
            msg.append(", y[").append(i).append("]=").append(y);
            appendUnless(TOOL_TYPE_SYMBOLIC_NAMES.get(1), msg, ", toolType[" + i + "]=", toolTypeToString(getToolType(i)));
        }
        int i2 = getButtonState();
        appendUnless(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, msg, ", buttonState=", buttonStateToString(i2));
        appendUnless(classificationToString(0), msg, ", classification=", classificationToString(getClassification()));
        appendUnless(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, msg, ", metaState=", KeyEvent.metaStateToString(getMetaState()));
        appendUnless(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, msg, ", flags=0x", Integer.toHexString(getFlags()));
        appendUnless(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, msg, ", edgeFlags=0x", Integer.toHexString(getEdgeFlags()));
        appendUnless(1, msg, ", pointerCount=", Integer.valueOf(pointerCount));
        appendUnless(0, msg, ", historySize=", Integer.valueOf(getHistorySize()));
        msg.append(", eventTime=").append(getEventTime());
        msg.append(", downTime=").append(getDownTime());
        msg.append(", deviceId=").append(getDeviceId());
        msg.append(", source=0x").append(Integer.toHexString(getSource()));
        msg.append(", displayId=").append(getDisplayId());
        msg.append(", eventId=").append(getId());
        msg.append(" }");
        return msg.toString();
    }

    private static <T> void appendUnless(T defValue, StringBuilder sb, String key, T value) {
        sb.append(key).append(value);
    }

    public static String actionToString(int action) {
        switch (action) {
            case 0:
                return "ACTION_DOWN";
            case 1:
                return "ACTION_UP";
            case 2:
                return "ACTION_MOVE";
            case 3:
                return "ACTION_CANCEL";
            case 4:
                return "ACTION_OUTSIDE";
            case 5:
            case 6:
            default:
                int index = (65280 & action) >> 8;
                switch (action & 255) {
                    case 5:
                        return "ACTION_POINTER_DOWN(" + index + NavigationBarInflaterView.KEY_CODE_END;
                    case 6:
                        return "ACTION_POINTER_UP(" + index + NavigationBarInflaterView.KEY_CODE_END;
                    default:
                        return Integer.toString(action);
                }
            case 7:
                return "ACTION_HOVER_MOVE";
            case 8:
                return "ACTION_SCROLL";
            case 9:
                return "ACTION_HOVER_ENTER";
            case 10:
                return "ACTION_HOVER_EXIT";
            case 11:
                return "ACTION_BUTTON_PRESS";
            case 12:
                return "ACTION_BUTTON_RELEASE";
        }
    }

    public static String axisToString(int axis) {
        String symbolicName = nativeAxisToString(axis);
        return symbolicName != null ? LABEL_PREFIX + symbolicName : Integer.toString(axis);
    }

    public static int axisFromString(String symbolicName) {
        int axis;
        if (symbolicName.startsWith(LABEL_PREFIX) && (axis = nativeAxisFromString((symbolicName = symbolicName.substring(LABEL_PREFIX.length())))) >= 0) {
            return axis;
        }
        try {
            return Integer.parseInt(symbolicName, 10);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String buttonStateToString(int buttonState) {
        if (buttonState == 0) {
            return AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS;
        }
        StringBuilder result = null;
        int i = 0;
        while (buttonState != 0) {
            boolean isSet = (buttonState & 1) != 0;
            buttonState >>>= 1;
            if (isSet) {
                String name = BUTTON_SYMBOLIC_NAMES[i];
                if (result == null) {
                    if (buttonState == 0) {
                        return name;
                    }
                    result = new StringBuilder(name);
                } else {
                    result.append('|');
                    result.append(name);
                }
            }
            i++;
        }
        return result.toString();
    }

    public static String classificationToString(int classification) {
        switch (classification) {
            case 0:
                return KeyProperties.DIGEST_NONE;
            case 1:
                return "AMBIGUOUS_GESTURE";
            case 2:
                return "DEEP_PRESS";
            case 3:
                return "TWO_FINGER_SWIPE";
            case 4:
                return "MULTI_FINGER_SWIPE";
            default:
                return "UNKNOWN";
        }
    }

    public static String toolTypeToString(int toolType) {
        String symbolicName = TOOL_TYPE_SYMBOLIC_NAMES.get(toolType);
        return symbolicName != null ? symbolicName : Integer.toString(toolType);
    }

    public final boolean isButtonPressed(int button) {
        return button != 0 && (getButtonState() & button) == button;
    }

    public int getSurfaceRotation() {
        return nativeGetSurfaceRotation(this.mNativePtr);
    }

    public static Matrix createRotateMatrix(int rotation, int rotatedFrameWidth, int rotatedFrameHeight) {
        if (rotation == 0) {
            return new Matrix(Matrix.IDENTITY_MATRIX);
        }
        float[] values = null;
        if (rotation == 1) {
            values = new float[]{0.0f, 1.0f, 0.0f, -1.0f, 0.0f, rotatedFrameHeight, 0.0f, 0.0f, 1.0f};
        } else if (rotation == 2) {
            values = new float[]{-1.0f, 0.0f, rotatedFrameWidth, 0.0f, -1.0f, rotatedFrameHeight, 0.0f, 0.0f, 1.0f};
        } else if (rotation == 3) {
            values = new float[]{0.0f, -1.0f, rotatedFrameWidth, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        }
        Matrix toOrient = new Matrix();
        toOrient.setValues(values);
        return toOrient;
    }

    public static MotionEvent createFromParcelBody(Parcel in) {
        MotionEvent ev = obtain();
        ev.mNativePtr = nativeReadFromParcel(ev.mNativePtr, in);
        return ev;
    }

    @Override // android.view.InputEvent
    public final void cancel() {
        setAction(3);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(1);
        nativeWriteToParcel(this.mNativePtr, out);
    }

    /* loaded from: classes4.dex */
    public static final class PointerCoords {
        private static final int INITIAL_PACKED_AXIS_VALUES = 8;
        public boolean isResampled;
        private long mPackedAxisBits;
        private float[] mPackedAxisValues;
        public float orientation;
        public float pressure;
        public float relativeX;
        public float relativeY;
        public float size;
        public float toolMajor;
        public float toolMinor;
        public float touchMajor;
        public float touchMinor;

        /* renamed from: x */
        public float f482x;

        /* renamed from: y */
        public float f483y;

        public PointerCoords() {
        }

        public PointerCoords(PointerCoords other) {
            copyFrom(other);
        }

        public static PointerCoords[] createArray(int size) {
            PointerCoords[] array = new PointerCoords[size];
            for (int i = 0; i < size; i++) {
                array[i] = new PointerCoords();
            }
            return array;
        }

        public void clear() {
            this.mPackedAxisBits = 0L;
            this.f482x = 0.0f;
            this.f483y = 0.0f;
            this.pressure = 0.0f;
            this.size = 0.0f;
            this.touchMajor = 0.0f;
            this.touchMinor = 0.0f;
            this.toolMajor = 0.0f;
            this.toolMinor = 0.0f;
            this.orientation = 0.0f;
            this.relativeX = 0.0f;
            this.relativeY = 0.0f;
            this.isResampled = false;
        }

        public void copyFrom(PointerCoords other) {
            long bits = other.mPackedAxisBits;
            this.mPackedAxisBits = bits;
            if (bits != 0) {
                float[] otherValues = other.mPackedAxisValues;
                int count = Long.bitCount(bits);
                float[] values = this.mPackedAxisValues;
                if (values == null || count > values.length) {
                    values = new float[otherValues.length];
                    this.mPackedAxisValues = values;
                }
                System.arraycopy(otherValues, 0, values, 0, count);
            }
            this.f482x = other.f482x;
            this.f483y = other.f483y;
            this.pressure = other.pressure;
            this.size = other.size;
            this.touchMajor = other.touchMajor;
            this.touchMinor = other.touchMinor;
            this.toolMajor = other.toolMajor;
            this.toolMinor = other.toolMinor;
            this.orientation = other.orientation;
            this.relativeX = other.relativeX;
            this.relativeY = other.relativeY;
            this.isResampled = other.isResampled;
        }

        public float getAxisValue(int axis) {
            switch (axis) {
                case 0:
                    return this.f482x;
                case 1:
                    return this.f483y;
                case 2:
                    return this.pressure;
                case 3:
                    return this.size;
                case 4:
                    return this.touchMajor;
                case 5:
                    return this.touchMinor;
                case 6:
                    return this.toolMajor;
                case 7:
                    return this.toolMinor;
                case 8:
                    return this.orientation;
                case 27:
                    return this.relativeX;
                case 28:
                    return this.relativeY;
                default:
                    if (axis < 0 || axis > 63) {
                        throw new IllegalArgumentException("Axis out of range.");
                    }
                    long bits = this.mPackedAxisBits;
                    long axisBit = (-9223372036854775808) >>> axis;
                    if ((bits & axisBit) == 0) {
                        return 0.0f;
                    }
                    int index = Long.bitCount((~((-1) >>> axis)) & bits);
                    return this.mPackedAxisValues[index];
            }
        }

        public void setAxisValue(int axis, float value) {
            switch (axis) {
                case 0:
                    this.f482x = value;
                    return;
                case 1:
                    this.f483y = value;
                    return;
                case 2:
                    this.pressure = value;
                    return;
                case 3:
                    this.size = value;
                    return;
                case 4:
                    this.touchMajor = value;
                    return;
                case 5:
                    this.touchMinor = value;
                    return;
                case 6:
                    this.toolMajor = value;
                    return;
                case 7:
                    this.toolMinor = value;
                    return;
                case 8:
                    this.orientation = value;
                    return;
                case 27:
                    this.relativeX = value;
                    return;
                case 28:
                    this.relativeY = value;
                    return;
                default:
                    if (axis < 0 || axis > 63) {
                        throw new IllegalArgumentException("Axis out of range.");
                    }
                    long bits = this.mPackedAxisBits;
                    long axisBit = (-9223372036854775808) >>> axis;
                    int index = Long.bitCount((~((-1) >>> axis)) & bits);
                    float[] values = this.mPackedAxisValues;
                    if ((bits & axisBit) == 0) {
                        if (values == null) {
                            values = new float[8];
                            this.mPackedAxisValues = values;
                        } else {
                            int count = Long.bitCount(bits);
                            if (count < values.length) {
                                if (index != count) {
                                    System.arraycopy(values, index, values, index + 1, count - index);
                                }
                            } else {
                                float[] newValues = new float[count * 2];
                                System.arraycopy(values, 0, newValues, 0, index);
                                System.arraycopy(values, index, newValues, index + 1, count - index);
                                values = newValues;
                                this.mPackedAxisValues = values;
                            }
                        }
                        this.mPackedAxisBits = bits | axisBit;
                    }
                    values[index] = value;
                    return;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static final class PointerProperties {

        /* renamed from: id */
        public int f484id;
        public int toolType;

        public PointerProperties() {
            clear();
        }

        public PointerProperties(PointerProperties other) {
            copyFrom(other);
        }

        public static PointerProperties[] createArray(int size) {
            PointerProperties[] array = new PointerProperties[size];
            for (int i = 0; i < size; i++) {
                array[i] = new PointerProperties();
            }
            return array;
        }

        public void clear() {
            this.f484id = -1;
            this.toolType = 0;
        }

        public void copyFrom(PointerProperties other) {
            this.f484id = other.f484id;
            this.toolType = other.toolType;
        }

        public boolean equals(Object other) {
            if (other instanceof PointerProperties) {
                return equals((PointerProperties) other);
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean equals(PointerProperties other) {
            return other != null && this.f484id == other.f484id && this.toolType == other.toolType;
        }

        public int hashCode() {
            return this.f484id | (this.toolType << 8);
        }
    }
}
