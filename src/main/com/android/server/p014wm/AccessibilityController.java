package com.android.server.p014wm;

import android.accessibilityservice.AccessibilityTrace;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Application;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.graphics.BLASTBufferQueue;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.MagnificationSpec;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.ViewConfiguration;
import android.view.WindowInfo;
import android.view.animation.DecelerateInterpolator;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.TraceBuffer;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.p014wm.AccessibilityController;
import com.android.server.p014wm.AccessibilityWindowsPopulator;
import com.android.server.p014wm.WindowManagerInternal;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.AccessibilityController */
/* loaded from: classes2.dex */
public final class AccessibilityController {
    public static final String TAG = "AccessibilityController";
    public final AccessibilityControllerInternalImpl mAccessibilityTracing;
    public final AccessibilityWindowsPopulator mAccessibilityWindowsPopulator;
    public final WindowManagerService mService;
    public static final Object STATIC_LOCK = new Object();
    public static final Rect EMPTY_RECT = new Rect();
    public static final float[] sTempFloats = new float[9];
    public final SparseArray<DisplayMagnifier> mDisplayMagnifiers = new SparseArray<>();
    public final SparseArray<WindowsForAccessibilityObserver> mWindowsForAccessibilityObserver = new SparseArray<>();
    public SparseArray<IBinder> mFocusedWindow = new SparseArray<>();
    public int mFocusedDisplay = -1;
    public final SparseBooleanArray mIsImeVisibleArray = new SparseBooleanArray();
    public boolean mAllObserversInitialized = true;

    public static AccessibilityControllerInternalImpl getAccessibilityControllerInternal(WindowManagerService windowManagerService) {
        return AccessibilityControllerInternalImpl.getInstance(windowManagerService);
    }

    public AccessibilityController(WindowManagerService windowManagerService) {
        this.mService = windowManagerService;
        this.mAccessibilityTracing = getAccessibilityControllerInternal(windowManagerService);
        this.mAccessibilityWindowsPopulator = new AccessibilityWindowsPopulator(windowManagerService, this);
    }

    public boolean setMagnificationCallbacks(int i, WindowManagerInternal.MagnificationCallbacks magnificationCallbacks) {
        Display display;
        if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".setMagnificationCallbacks", 2048L, "displayId=" + i + "; callbacks={" + magnificationCallbacks + "}");
        }
        if (magnificationCallbacks != null) {
            if (this.mDisplayMagnifiers.get(i) != null) {
                throw new IllegalStateException("Magnification callbacks already set!");
            }
            DisplayContent displayContent = this.mService.mRoot.getDisplayContent(i);
            if (displayContent == null || (display = displayContent.getDisplay()) == null || display.getType() == 4) {
                return false;
            }
            DisplayMagnifier displayMagnifier = new DisplayMagnifier(this.mService, displayContent, display, magnificationCallbacks);
            displayMagnifier.notifyImeWindowVisibilityChanged(this.mIsImeVisibleArray.get(i, false));
            this.mDisplayMagnifiers.put(i, displayMagnifier);
            return true;
        }
        DisplayMagnifier displayMagnifier2 = this.mDisplayMagnifiers.get(i);
        if (displayMagnifier2 == null) {
            throw new IllegalStateException("Magnification callbacks already cleared!");
        }
        displayMagnifier2.destroy();
        this.mDisplayMagnifiers.remove(i);
        return true;
    }

    public void setWindowsForAccessibilityCallback(int i, WindowManagerInternal.WindowsForAccessibilityCallback windowsForAccessibilityCallback) {
        if (this.mAccessibilityTracing.isTracingEnabled(1024L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".setWindowsForAccessibilityCallback", 1024L, "displayId=" + i + "; callback={" + windowsForAccessibilityCallback + "}");
        }
        if (windowsForAccessibilityCallback != null) {
            if (this.mWindowsForAccessibilityObserver.get(i) != null) {
                String str = "Windows for accessibility callback of display " + i + " already set!";
                Slog.e(TAG, str);
                if (Build.IS_DEBUGGABLE) {
                    throw new IllegalStateException(str);
                }
                this.mWindowsForAccessibilityObserver.remove(i);
            }
            this.mAccessibilityWindowsPopulator.setWindowsNotification(true);
            WindowsForAccessibilityObserver windowsForAccessibilityObserver = new WindowsForAccessibilityObserver(this.mService, i, windowsForAccessibilityCallback, this.mAccessibilityWindowsPopulator);
            this.mWindowsForAccessibilityObserver.put(i, windowsForAccessibilityObserver);
            this.mAllObserversInitialized &= windowsForAccessibilityObserver.mInitialized;
            return;
        }
        if (this.mWindowsForAccessibilityObserver.get(i) == null) {
            String str2 = "Windows for accessibility callback of display " + i + " already cleared!";
            Slog.e(TAG, str2);
            if (Build.IS_DEBUGGABLE) {
                throw new IllegalStateException(str2);
            }
        }
        this.mWindowsForAccessibilityObserver.remove(i);
        if (this.mWindowsForAccessibilityObserver.size() <= 0) {
            this.mAccessibilityWindowsPopulator.setWindowsNotification(false);
        }
    }

    public void performComputeChangedWindowsNot(int i, boolean z) {
        WindowsForAccessibilityObserver windowsForAccessibilityObserver;
        if (this.mAccessibilityTracing.isTracingEnabled(1024L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".performComputeChangedWindowsNot", 1024L, "displayId=" + i + "; forceSend=" + z);
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                windowsForAccessibilityObserver = this.mWindowsForAccessibilityObserver.get(i);
                if (windowsForAccessibilityObserver == null) {
                    windowsForAccessibilityObserver = null;
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (windowsForAccessibilityObserver != null) {
            windowsForAccessibilityObserver.performComputeChangedWindows(z);
        }
    }

    public void setMagnificationSpec(int i, MagnificationSpec magnificationSpec) {
        if (this.mAccessibilityTracing.isTracingEnabled(3072L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".setMagnificationSpec", 3072L, "displayId=" + i + "; spec={" + magnificationSpec + "}");
        }
        this.mAccessibilityWindowsPopulator.setMagnificationSpec(i, magnificationSpec);
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(i);
        if (displayMagnifier != null) {
            displayMagnifier.setMagnificationSpec(magnificationSpec);
        }
        WindowsForAccessibilityObserver windowsForAccessibilityObserver = this.mWindowsForAccessibilityObserver.get(i);
        if (windowsForAccessibilityObserver != null) {
            windowsForAccessibilityObserver.scheduleComputeChangedWindows();
        }
    }

    public void getMagnificationRegion(int i, Region region) {
        if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".getMagnificationRegion", 2048L, "displayId=" + i + "; outMagnificationRegion={" + region + "}");
        }
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(i);
        if (displayMagnifier != null) {
            displayMagnifier.getMagnificationRegion(region);
        }
    }

    public void onDisplaySizeChanged(DisplayContent displayContent) {
        if (this.mAccessibilityTracing.isTracingEnabled(3072L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".onRotationChanged", 3072L, "displayContent={" + displayContent + "}");
        }
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(displayContent.getDisplayId());
        if (displayMagnifier != null) {
            displayMagnifier.onDisplaySizeChanged(displayContent);
        }
    }

    public void onAppWindowTransition(int i, int i2) {
        if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".onAppWindowTransition", 2048L, "displayId=" + i + "; transition=" + i2);
        }
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(i);
        if (displayMagnifier != null) {
            displayMagnifier.onAppWindowTransition(i, i2);
        }
    }

    public void onWMTransition(int i, int i2) {
        if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".onAppWindowTransition", 2048L, "displayId=" + i + "; type=" + i2);
        }
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(i);
        if (displayMagnifier != null) {
            displayMagnifier.onWMTransition(i, i2);
        }
    }

    public void onWindowTransition(WindowState windowState, int i) {
        if (this.mAccessibilityTracing.isTracingEnabled(3072L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".onWindowTransition", 3072L, "windowState={" + windowState + "}; transition=" + i);
        }
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(windowState.getDisplayId());
        if (displayMagnifier != null) {
            displayMagnifier.onWindowTransition(windowState, i);
        }
    }

    public void onWindowFocusChangedNot(int i) {
        WindowsForAccessibilityObserver windowsForAccessibilityObserver;
        if (this.mAccessibilityTracing.isTracingEnabled(1024L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".onWindowFocusChangedNot", 1024L, "displayId=" + i);
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                windowsForAccessibilityObserver = this.mWindowsForAccessibilityObserver.get(i);
                if (windowsForAccessibilityObserver == null) {
                    windowsForAccessibilityObserver = null;
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        if (windowsForAccessibilityObserver != null) {
            windowsForAccessibilityObserver.performComputeChangedWindows(false);
        }
        sendCallbackToUninitializedObserversIfNeeded();
    }

    public final void sendCallbackToUninitializedObserversIfNeeded() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                if (this.mAllObserversInitialized) {
                    return;
                }
                if (this.mService.mRoot.getTopFocusedDisplayContent().mCurrentFocus == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                ArrayList arrayList = new ArrayList();
                for (int size = this.mWindowsForAccessibilityObserver.size() - 1; size >= 0; size--) {
                    WindowsForAccessibilityObserver valueAt = this.mWindowsForAccessibilityObserver.valueAt(size);
                    if (!valueAt.mInitialized) {
                        arrayList.add(valueAt);
                    }
                }
                this.mAllObserversInitialized = true;
                WindowManagerService.resetPriorityAfterLockedSection();
                boolean z = true;
                for (int size2 = arrayList.size() - 1; size2 >= 0; size2--) {
                    WindowsForAccessibilityObserver windowsForAccessibilityObserver = (WindowsForAccessibilityObserver) arrayList.get(size2);
                    windowsForAccessibilityObserver.performComputeChangedWindows(true);
                    z &= windowsForAccessibilityObserver.mInitialized;
                }
                synchronized (this.mService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        this.mAllObserversInitialized &= z;
                    } finally {
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } finally {
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    public void onSomeWindowResizedOrMoved(int... iArr) {
        onSomeWindowResizedOrMovedWithCallingUid(Binder.getCallingUid(), iArr);
    }

    public void onSomeWindowResizedOrMovedWithCallingUid(int i, int... iArr) {
        if (this.mAccessibilityTracing.isTracingEnabled(1024L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".onSomeWindowResizedOrMoved", 1024L, "displayIds={" + Arrays.toString(iArr) + "}", "".getBytes(), i);
        }
        for (int i2 : iArr) {
            WindowsForAccessibilityObserver windowsForAccessibilityObserver = this.mWindowsForAccessibilityObserver.get(i2);
            if (windowsForAccessibilityObserver != null) {
                windowsForAccessibilityObserver.scheduleComputeChangedWindows();
            }
        }
    }

    public void drawMagnifiedRegionBorderIfNeeded(int i, SurfaceControl.Transaction transaction) {
        if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".drawMagnifiedRegionBorderIfNeeded", 2048L, "displayId=" + i + "; transaction={" + transaction + "}");
        }
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(i);
        if (displayMagnifier != null) {
            displayMagnifier.drawMagnifiedRegionBorderIfNeeded(transaction);
        }
    }

    public Pair<Matrix, MagnificationSpec> getWindowTransformationMatrixAndMagnificationSpec(IBinder iBinder) {
        Pair<Matrix, MagnificationSpec> pair;
        MagnificationSpec magnificationSpecForWindow;
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                Matrix matrix = new Matrix();
                MagnificationSpec magnificationSpec = new MagnificationSpec();
                WindowState windowState = this.mService.mWindowMap.get(iBinder);
                if (windowState != null) {
                    windowState.getTransformationMatrix(new float[9], matrix);
                    if (hasCallbacks() && (magnificationSpecForWindow = getMagnificationSpecForWindow(windowState)) != null && !magnificationSpecForWindow.isNop()) {
                        magnificationSpec.setTo(magnificationSpecForWindow);
                    }
                }
                pair = new Pair<>(matrix, magnificationSpec);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return pair;
    }

    public MagnificationSpec getMagnificationSpecForWindow(WindowState windowState) {
        if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".getMagnificationSpecForWindow", 2048L, "windowState={" + windowState + "}");
        }
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(windowState.getDisplayId());
        if (displayMagnifier != null) {
            return displayMagnifier.getMagnificationSpecForWindow(windowState);
        }
        return null;
    }

    public boolean hasCallbacks() {
        if (this.mAccessibilityTracing.isTracingEnabled(3072L)) {
            AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
            accessibilityControllerInternalImpl.logTrace(TAG + ".hasCallbacks", 3072L);
        }
        return this.mDisplayMagnifiers.size() > 0 || this.mWindowsForAccessibilityObserver.size() > 0;
    }

    public void setForceShowMagnifiableBounds(int i, boolean z) {
        if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".setForceShowMagnifiableBounds", 2048L, "displayId=" + i + "; show=" + z);
        }
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(i);
        if (displayMagnifier != null) {
            displayMagnifier.setForceShowMagnifiableBounds(z);
            displayMagnifier.showMagnificationBoundsIfNeeded();
        }
    }

    public void updateImeVisibilityIfNeeded(int i, boolean z) {
        if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
            this.mAccessibilityTracing.logTrace(TAG + ".updateImeVisibilityIfNeeded", 2048L, "displayId=" + i + ";shown=" + z);
        }
        if (this.mIsImeVisibleArray.get(i, false) == z) {
            return;
        }
        this.mIsImeVisibleArray.put(i, z);
        DisplayMagnifier displayMagnifier = this.mDisplayMagnifiers.get(i);
        if (displayMagnifier != null) {
            displayMagnifier.notifyImeWindowVisibilityChanged(z);
        }
    }

    public static void populateTransformationMatrix(WindowState windowState, Matrix matrix) {
        windowState.getTransformationMatrix(sTempFloats, matrix);
    }

    public void dump(PrintWriter printWriter, String str) {
        for (int i = 0; i < this.mDisplayMagnifiers.size(); i++) {
            DisplayMagnifier valueAt = this.mDisplayMagnifiers.valueAt(i);
            if (valueAt != null) {
                valueAt.dump(printWriter, str + "Magnification display# " + this.mDisplayMagnifiers.keyAt(i));
            }
        }
        printWriter.println(str + "mWindowsForAccessibilityObserver=" + this.mWindowsForAccessibilityObserver);
    }

    public void onFocusChanged(InputTarget inputTarget, InputTarget inputTarget2) {
        if (inputTarget != null) {
            this.mFocusedWindow.remove(inputTarget.getDisplayId());
        }
        if (inputTarget2 != null) {
            this.mFocusedWindow.put(inputTarget2.getDisplayId(), inputTarget2.getIWindow().asBinder());
        }
    }

    public void onDisplayRemoved(int i) {
        this.mIsImeVisibleArray.delete(i);
        this.mFocusedWindow.remove(i);
    }

    public void setFocusedDisplay(int i) {
        this.mFocusedDisplay = i;
    }

    public IBinder getFocusedWindowToken() {
        return this.mFocusedWindow.get(this.mFocusedDisplay);
    }

    /* renamed from: com.android.server.wm.AccessibilityController$DisplayMagnifier */
    /* loaded from: classes2.dex */
    public static final class DisplayMagnifier {
        public final AccessibilityControllerInternalImpl mAccessibilityTracing;
        public final WindowManagerInternal.MagnificationCallbacks mCallbacks;
        public final Display mDisplay;
        public final DisplayContent mDisplayContent;
        public final Context mDisplayContext;
        public final Handler mHandler;
        public final long mLongAnimationDuration;
        public final MagnifiedViewport mMagnifedViewport;
        public final WindowManagerService mService;
        public final Rect mTempRect1 = new Rect();
        public final Rect mTempRect2 = new Rect();
        public final Region mTempRegion1 = new Region();
        public final Region mTempRegion2 = new Region();
        public final Region mTempRegion3 = new Region();
        public final Region mTempRegion4 = new Region();
        public boolean mForceShowMagnifiableBounds = false;

        public DisplayMagnifier(WindowManagerService windowManagerService, DisplayContent displayContent, Display display, WindowManagerInternal.MagnificationCallbacks magnificationCallbacks) {
            Context createDisplayContext = windowManagerService.mContext.createDisplayContext(display);
            this.mDisplayContext = createDisplayContext;
            this.mService = windowManagerService;
            this.mCallbacks = magnificationCallbacks;
            this.mDisplayContent = displayContent;
            this.mDisplay = display;
            this.mHandler = new MyHandler(windowManagerService.f1164mH.getLooper());
            this.mMagnifedViewport = new MagnifiedViewport();
            AccessibilityControllerInternalImpl accessibilityControllerInternal = AccessibilityController.getAccessibilityControllerInternal(windowManagerService);
            this.mAccessibilityTracing = accessibilityControllerInternal;
            this.mLongAnimationDuration = createDisplayContext.getResources().getInteger(17694722);
            if (accessibilityControllerInternal.isTracingEnabled(2048L)) {
                accessibilityControllerInternal.logTrace("WindowManager.DisplayMagnifier.constructor", 2048L, "windowManagerService={" + windowManagerService + "}; displayContent={" + displayContent + "}; display={" + display + "}; callbacks={" + magnificationCallbacks + "}");
            }
        }

        public void setMagnificationSpec(MagnificationSpec magnificationSpec) {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.setMagnificationSpec", 2048L, "spec={" + magnificationSpec + "}");
            }
            this.mMagnifedViewport.updateMagnificationSpec(magnificationSpec);
            this.mMagnifedViewport.recomputeBounds();
            this.mService.applyMagnificationSpecLocked(this.mDisplay.getDisplayId(), magnificationSpec);
            this.mService.scheduleAnimationLocked();
        }

        public void setForceShowMagnifiableBounds(boolean z) {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.setForceShowMagnifiableBounds", 2048L, "show=" + z);
            }
            this.mForceShowMagnifiableBounds = z;
            this.mMagnifedViewport.setMagnifiedRegionBorderShown(z, true);
        }

        public boolean isForceShowingMagnifiableBounds() {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                this.mAccessibilityTracing.logTrace("WindowManager.isForceShowingMagnifiableBounds", 2048L);
            }
            return this.mForceShowMagnifiableBounds;
        }

        public void onDisplaySizeChanged(DisplayContent displayContent) {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.onDisplaySizeChanged", 2048L, "displayContent={" + displayContent + "}");
            }
            this.mMagnifedViewport.onDisplaySizeChanged();
            this.mHandler.sendEmptyMessage(4);
        }

        public void onAppWindowTransition(int i, int i2) {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.onAppWindowTransition", 2048L, "displayId=" + i + "; transition=" + i2);
            }
            if (isForceShowingMagnifiableBounds()) {
                if (i2 != 6 && i2 != 8 && i2 != 10 && i2 != 28) {
                    switch (i2) {
                        case 12:
                        case 13:
                        case 14:
                            break;
                        default:
                            return;
                    }
                }
                this.mHandler.sendEmptyMessage(3);
            }
        }

        public void onWMTransition(int i, int i2) {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.onWMTransition", 2048L, "displayId=" + i + "; type=" + i2);
            }
            if (isForceShowingMagnifiableBounds()) {
                if (i2 == 1 || i2 == 2 || i2 == 3 || i2 == 4) {
                    this.mHandler.sendEmptyMessage(3);
                }
            }
        }

        public void onWindowTransition(WindowState windowState, int i) {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.onWindowTransition", 2048L, "windowState={" + windowState + "}; transition=" + i);
            }
            boolean isForceShowingMagnifiableBounds = isForceShowingMagnifiableBounds();
            int i2 = windowState.mAttrs.type;
            if ((i == 1 || i == 3) && isForceShowingMagnifiableBounds) {
                if (i2 != 2 && i2 != 4 && i2 != 1005 && i2 != 2020 && i2 != 2024 && i2 != 2035 && i2 != 2038) {
                    switch (i2) {
                        case 1000:
                        case 1001:
                        case 1002:
                        case 1003:
                            break;
                        default:
                            switch (i2) {
                                case 2001:
                                case 2002:
                                case 2003:
                                    break;
                                default:
                                    switch (i2) {
                                        case 2005:
                                        case 2006:
                                        case 2007:
                                        case 2008:
                                        case 2009:
                                        case 2010:
                                            break;
                                        default:
                                            return;
                                    }
                            }
                    }
                }
                Rect rect = this.mTempRect2;
                this.mMagnifedViewport.getMagnifiedFrameInContentCoords(rect);
                Rect rect2 = this.mTempRect1;
                windowState.getTouchableRegion(this.mTempRegion1);
                this.mTempRegion1.getBounds(rect2);
                if (rect.intersect(rect2)) {
                    return;
                }
                this.mCallbacks.onRectangleOnScreenRequested(rect2.left, rect2.top, rect2.right, rect2.bottom);
            }
        }

        public void notifyImeWindowVisibilityChanged(boolean z) {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.notifyImeWindowVisibilityChanged", 2048L, "shown=" + z);
            }
            this.mHandler.obtainMessage(6, z ? 1 : 0, 0).sendToTarget();
        }

        public MagnificationSpec getMagnificationSpecForWindow(WindowState windowState) {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.getMagnificationSpecForWindow", 2048L, "windowState={" + windowState + "}");
            }
            MagnificationSpec magnificationSpec = this.mMagnifedViewport.getMagnificationSpec();
            if (magnificationSpec == null || magnificationSpec.isNop() || windowState.shouldMagnify()) {
                return magnificationSpec;
            }
            return null;
        }

        public void getMagnificationRegion(Region region) {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.getMagnificationRegion", 2048L, "outMagnificationRegion={" + region + "}");
            }
            this.mMagnifedViewport.recomputeBounds();
            this.mMagnifedViewport.getMagnificationRegion(region);
        }

        public void destroy() {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                this.mAccessibilityTracing.logTrace("WindowManager.destroy", 2048L);
            }
            this.mMagnifedViewport.destroyWindow();
        }

        public void showMagnificationBoundsIfNeeded() {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                this.mAccessibilityTracing.logTrace("WindowManager.showMagnificationBoundsIfNeeded", 2048L);
            }
            this.mHandler.obtainMessage(5).sendToTarget();
        }

        public void drawMagnifiedRegionBorderIfNeeded(SurfaceControl.Transaction transaction) {
            if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.drawMagnifiedRegionBorderIfNeeded", 2048L, "transition={" + transaction + "}");
            }
            this.mMagnifedViewport.drawWindowIfNeeded(transaction);
        }

        public void dump(PrintWriter printWriter, String str) {
            this.mMagnifedViewport.dump(printWriter, str);
        }

        /* renamed from: com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport */
        /* loaded from: classes2.dex */
        public final class MagnifiedViewport {
            public final float mBorderWidth;
            public final Path mCircularPath;
            public final int mDrawBorderInset;
            public boolean mFullRedrawNeeded;
            public final int mHalfBorderWidth;
            public final Region mMagnificationRegion;
            public final MagnificationSpec mMagnificationSpec;
            public final Region mOldMagnificationRegion;
            public final Point mScreenSize;
            public int mTempLayer;
            public final Matrix mTempMatrix;
            public final ViewportWindow mWindow;
            public final SparseArray<WindowState> mTempWindowStates = new SparseArray<>();
            public final RectF mTempRectF = new RectF();

            public final boolean isExcludedWindowType(int i) {
                return i == 2027 || i == 2039;
            }

            public MagnifiedViewport() {
                Point point = new Point();
                this.mScreenSize = point;
                this.mTempMatrix = new Matrix();
                this.mMagnificationRegion = new Region();
                this.mOldMagnificationRegion = new Region();
                this.mMagnificationSpec = new MagnificationSpec();
                this.mTempLayer = 0;
                float dimension = DisplayMagnifier.this.mDisplayContext.getResources().getDimension(17104909);
                this.mBorderWidth = dimension;
                this.mHalfBorderWidth = (int) Math.ceil(dimension / 2.0f);
                this.mDrawBorderInset = ((int) dimension) / 2;
                this.mWindow = new ViewportWindow(DisplayMagnifier.this.mDisplayContext);
                if (DisplayMagnifier.this.mDisplayContext.getResources().getConfiguration().isScreenRound()) {
                    Path path = new Path();
                    this.mCircularPath = path;
                    getDisplaySizeLocked(point);
                    float f = point.x / 2;
                    path.addCircle(f, f, f, Path.Direction.CW);
                } else {
                    this.mCircularPath = null;
                }
                recomputeBounds();
            }

            public void getMagnificationRegion(Region region) {
                region.set(this.mMagnificationRegion);
            }

            public void updateMagnificationSpec(MagnificationSpec magnificationSpec) {
                if (magnificationSpec != null) {
                    this.mMagnificationSpec.initialize(magnificationSpec.scale, magnificationSpec.offsetX, magnificationSpec.offsetY);
                } else {
                    this.mMagnificationSpec.clear();
                }
                if (DisplayMagnifier.this.mHandler.hasMessages(5)) {
                    return;
                }
                setMagnifiedRegionBorderShown(DisplayMagnifier.this.isForceShowingMagnifiableBounds(), true);
            }

            public void recomputeBounds() {
                getDisplaySizeLocked(this.mScreenSize);
                Point point = this.mScreenSize;
                int i = point.x;
                int i2 = point.y;
                this.mMagnificationRegion.set(0, 0, 0, 0);
                Region region = DisplayMagnifier.this.mTempRegion1;
                region.set(0, 0, i, i2);
                Path path = this.mCircularPath;
                if (path != null) {
                    region.setPath(path, region);
                }
                Region region2 = DisplayMagnifier.this.mTempRegion4;
                region2.set(0, 0, 0, 0);
                SparseArray<WindowState> sparseArray = this.mTempWindowStates;
                sparseArray.clear();
                populateWindowsOnScreen(sparseArray);
                for (int size = sparseArray.size() - 1; size >= 0; size--) {
                    WindowState valueAt = sparseArray.valueAt(size);
                    if (!isExcludedWindowType(valueAt.mAttrs.type)) {
                        int i3 = valueAt.mAttrs.privateFlags;
                        if ((2097152 & i3) == 0 && (i3 & 1048576) == 0) {
                            Matrix matrix = this.mTempMatrix;
                            AccessibilityController.populateTransformationMatrix(valueAt, matrix);
                            Region region3 = DisplayMagnifier.this.mTempRegion3;
                            valueAt.getTouchableRegion(region3);
                            Rect rect = DisplayMagnifier.this.mTempRect1;
                            region3.getBounds(rect);
                            RectF rectF = this.mTempRectF;
                            rectF.set(rect);
                            rectF.offset(-valueAt.getFrame().left, -valueAt.getFrame().top);
                            matrix.mapRect(rectF);
                            Region region4 = DisplayMagnifier.this.mTempRegion2;
                            region4.set((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                            Region region5 = DisplayMagnifier.this.mTempRegion3;
                            region5.set(this.mMagnificationRegion);
                            region5.op(region2, Region.Op.UNION);
                            region4.op(region5, Region.Op.DIFFERENCE);
                            if (valueAt.shouldMagnify()) {
                                this.mMagnificationRegion.op(region4, Region.Op.UNION);
                                this.mMagnificationRegion.op(region, Region.Op.INTERSECT);
                            } else {
                                region2.op(region4, Region.Op.UNION);
                                region.op(region4, Region.Op.DIFFERENCE);
                            }
                            if (AccessibilityController.isUntouchableNavigationBar(valueAt, DisplayMagnifier.this.mTempRegion3)) {
                                Rect systemBarInsetsFrame = AccessibilityController.getSystemBarInsetsFrame(valueAt);
                                region2.op(systemBarInsetsFrame, Region.Op.UNION);
                                region.op(systemBarInsetsFrame, Region.Op.DIFFERENCE);
                            }
                            if (valueAt.areAppWindowBoundsLetterboxed()) {
                                Region letterboxBounds = getLetterboxBounds(valueAt);
                                region2.op(letterboxBounds, Region.Op.UNION);
                                region.op(letterboxBounds, Region.Op.DIFFERENCE);
                            }
                            Region region6 = DisplayMagnifier.this.mTempRegion2;
                            region6.set(this.mMagnificationRegion);
                            region6.op(region2, Region.Op.UNION);
                            region6.op(0, 0, i, i2, Region.Op.INTERSECT);
                            if (region6.isRect()) {
                                Rect rect2 = DisplayMagnifier.this.mTempRect1;
                                region6.getBounds(rect2);
                                if (rect2.width() == i && rect2.height() == i2) {
                                    break;
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                }
                sparseArray.clear();
                Region region7 = this.mMagnificationRegion;
                int i4 = this.mDrawBorderInset;
                region7.op(i4, i4, i - i4, i2 - i4, Region.Op.INTERSECT);
                if (!this.mOldMagnificationRegion.equals(this.mMagnificationRegion)) {
                    this.mWindow.setBounds(this.mMagnificationRegion);
                    Rect rect3 = DisplayMagnifier.this.mTempRect1;
                    if (this.mFullRedrawNeeded) {
                        this.mFullRedrawNeeded = false;
                        int i5 = this.mDrawBorderInset;
                        rect3.set(i5, i5, i - i5, i2 - i5);
                        this.mWindow.invalidate(rect3);
                    } else {
                        Region region8 = DisplayMagnifier.this.mTempRegion3;
                        region8.set(this.mMagnificationRegion);
                        region8.op(this.mOldMagnificationRegion, Region.Op.XOR);
                        region8.getBounds(rect3);
                        this.mWindow.invalidate(rect3);
                    }
                    this.mOldMagnificationRegion.set(this.mMagnificationRegion);
                    SomeArgs obtain = SomeArgs.obtain();
                    obtain.arg1 = Region.obtain(this.mMagnificationRegion);
                    DisplayMagnifier.this.mHandler.obtainMessage(1, obtain).sendToTarget();
                }
            }

            public final Region getLetterboxBounds(WindowState windowState) {
                ActivityRecord activityRecord = windowState.mActivityRecord;
                if (activityRecord == null) {
                    return new Region();
                }
                Rect bounds = windowState.getBounds();
                Rect letterboxInsets = activityRecord.getLetterboxInsets();
                Rect copyOrNull = Rect.copyOrNull(bounds);
                copyOrNull.inset(Insets.subtract(Insets.NONE, Insets.of(letterboxInsets)));
                Region region = new Region();
                region.set(copyOrNull);
                region.op(bounds, Region.Op.DIFFERENCE);
                return region;
            }

            public void onDisplaySizeChanged() {
                if (DisplayMagnifier.this.isForceShowingMagnifiableBounds()) {
                    setMagnifiedRegionBorderShown(false, false);
                    Message obtainMessage = DisplayMagnifier.this.mHandler.obtainMessage(5);
                    DisplayMagnifier.this.mHandler.sendMessageDelayed(obtainMessage, ((float) DisplayMagnifier.this.mLongAnimationDuration) * DisplayMagnifier.this.mService.getWindowAnimationScaleLocked());
                }
                recomputeBounds();
                this.mWindow.updateSize();
            }

            public void setMagnifiedRegionBorderShown(boolean z, boolean z2) {
                if (z) {
                    this.mFullRedrawNeeded = true;
                    this.mOldMagnificationRegion.set(0, 0, 0, 0);
                }
                this.mWindow.setShown(z, z2);
            }

            public void getMagnifiedFrameInContentCoords(Rect rect) {
                MagnificationSpec magnificationSpec = this.mMagnificationSpec;
                this.mMagnificationRegion.getBounds(rect);
                rect.offset((int) (-magnificationSpec.offsetX), (int) (-magnificationSpec.offsetY));
                rect.scale(1.0f / magnificationSpec.scale);
            }

            public MagnificationSpec getMagnificationSpec() {
                return this.mMagnificationSpec;
            }

            public void drawWindowIfNeeded(SurfaceControl.Transaction transaction) {
                recomputeBounds();
                this.mWindow.drawIfNeeded(transaction);
            }

            public void destroyWindow() {
                this.mWindow.releaseSurface();
            }

            public final void populateWindowsOnScreen(final SparseArray<WindowState> sparseArray) {
                this.mTempLayer = 0;
                DisplayMagnifier.this.mDisplayContent.forAllWindows(new Consumer() { // from class: com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AccessibilityController.DisplayMagnifier.MagnifiedViewport.this.lambda$populateWindowsOnScreen$0(sparseArray, (WindowState) obj);
                    }
                }, false);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$populateWindowsOnScreen$0(SparseArray sparseArray, WindowState windowState) {
                if (windowState.isOnScreen() && windowState.isVisible() && windowState.mAttrs.alpha != 0.0f) {
                    int i = this.mTempLayer + 1;
                    this.mTempLayer = i;
                    sparseArray.put(i, windowState);
                }
            }

            public final void getDisplaySizeLocked(Point point) {
                Rect bounds = DisplayMagnifier.this.mDisplayContent.getConfiguration().windowConfiguration.getBounds();
                point.set(bounds.width(), bounds.height());
            }

            public void dump(PrintWriter printWriter, String str) {
                this.mWindow.dump(printWriter, str);
            }

            /* renamed from: com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport$ViewportWindow */
            /* loaded from: classes2.dex */
            public final class ViewportWindow {
                public int mAlpha;
                public final AnimationController mAnimationController;
                public final BLASTBufferQueue mBlastBufferQueue;
                public boolean mInvalidated;
                public boolean mShown;
                public final Surface mSurface;
                public final SurfaceControl mSurfaceControl;
                public final Region mBounds = new Region();
                public final Rect mDirtyRect = new Rect();
                public final Paint mPaint = new Paint();

                public ViewportWindow(Context context) {
                    SurfaceControl surfaceControl;
                    try {
                        surfaceControl = DisplayMagnifier.this.mDisplayContent.makeOverlay().setName("Magnification Overlay").setBLASTLayer().setFormat(-3).setCallsite("ViewportWindow").build();
                    } catch (Surface.OutOfResourcesException unused) {
                        surfaceControl = null;
                    }
                    this.mSurfaceControl = surfaceControl;
                    DisplayMagnifier.this.mDisplay.getRealSize(MagnifiedViewport.this.mScreenSize);
                    BLASTBufferQueue bLASTBufferQueue = new BLASTBufferQueue("Magnification Overlay", surfaceControl, MagnifiedViewport.this.mScreenSize.x, MagnifiedViewport.this.mScreenSize.y, 1);
                    this.mBlastBufferQueue = bLASTBufferQueue;
                    SurfaceControl.Transaction transaction = DisplayMagnifier.this.mService.mTransactionFactory.get();
                    transaction.setLayer(surfaceControl, DisplayMagnifier.this.mService.mPolicy.getWindowLayerFromTypeLw(2027) * FrameworkStatsLog.WIFI_BYTES_TRANSFER).setPosition(surfaceControl, 0.0f, 0.0f);
                    InputMonitor.setTrustedOverlayInputInfo(surfaceControl, transaction, DisplayMagnifier.this.mDisplayContent.getDisplayId(), "Magnification Overlay");
                    transaction.apply();
                    this.mSurface = bLASTBufferQueue.createSurface();
                    this.mAnimationController = new AnimationController(context, DisplayMagnifier.this.mService.f1164mH.getLooper());
                    TypedValue typedValue = new TypedValue();
                    context.getTheme().resolveAttribute(16843664, typedValue, true);
                    int color = context.getColor(typedValue.resourceId);
                    this.mPaint.setStyle(Paint.Style.STROKE);
                    this.mPaint.setStrokeWidth(MagnifiedViewport.this.mBorderWidth);
                    this.mPaint.setColor(color);
                    this.mInvalidated = true;
                }

                public void setShown(boolean z, boolean z2) {
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mShown == z) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                            this.mShown = z;
                            this.mAnimationController.onFrameShownStateChanged(z, z2);
                            WindowManagerService.resetPriorityAfterLockedSection();
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }

                public void setAlpha(int i) {
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mAlpha == i) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                            this.mAlpha = i;
                            invalidate(null);
                            WindowManagerService.resetPriorityAfterLockedSection();
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }

                public void setBounds(Region region) {
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (this.mBounds.equals(region)) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                            this.mBounds.set(region);
                            invalidate(this.mDirtyRect);
                            WindowManagerService.resetPriorityAfterLockedSection();
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }

                public void updateSize() {
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            MagnifiedViewport magnifiedViewport = MagnifiedViewport.this;
                            magnifiedViewport.getDisplaySizeLocked(magnifiedViewport.mScreenSize);
                            this.mBlastBufferQueue.update(this.mSurfaceControl, MagnifiedViewport.this.mScreenSize.x, MagnifiedViewport.this.mScreenSize.y, 1);
                            invalidate(this.mDirtyRect);
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                }

                public void invalidate(Rect rect) {
                    if (rect != null) {
                        this.mDirtyRect.set(rect);
                    } else {
                        this.mDirtyRect.setEmpty();
                    }
                    this.mInvalidated = true;
                    DisplayMagnifier.this.mService.scheduleAnimationLocked();
                }

                public void drawIfNeeded(SurfaceControl.Transaction transaction) {
                    Canvas canvas;
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (!this.mInvalidated) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                            this.mInvalidated = false;
                            if (this.mAlpha > 0) {
                                try {
                                    if (this.mDirtyRect.isEmpty()) {
                                        this.mBounds.getBounds(this.mDirtyRect);
                                    }
                                    this.mDirtyRect.inset(-MagnifiedViewport.this.mHalfBorderWidth, -MagnifiedViewport.this.mHalfBorderWidth);
                                    canvas = this.mSurface.lockCanvas(this.mDirtyRect);
                                } catch (Surface.OutOfResourcesException | IllegalArgumentException unused) {
                                    canvas = null;
                                }
                                if (canvas == null) {
                                    WindowManagerService.resetPriorityAfterLockedSection();
                                    return;
                                }
                                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                                this.mPaint.setAlpha(this.mAlpha);
                                canvas.drawPath(this.mBounds.getBoundaryPath(), this.mPaint);
                                this.mSurface.unlockCanvasAndPost(canvas);
                                transaction.show(this.mSurfaceControl);
                            } else {
                                transaction.hide(this.mSurfaceControl);
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }

                public void releaseSurface() {
                    BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
                    if (bLASTBufferQueue != null) {
                        bLASTBufferQueue.destroy();
                    }
                    DisplayMagnifier.this.mService.mTransactionFactory.get().remove(this.mSurfaceControl).apply();
                    this.mSurface.release();
                }

                public void dump(PrintWriter printWriter, String str) {
                    printWriter.println(str + " mBounds= " + this.mBounds + " mDirtyRect= " + this.mDirtyRect + " mWidth= " + MagnifiedViewport.this.mScreenSize.x + " mHeight= " + MagnifiedViewport.this.mScreenSize.y);
                }

                /* renamed from: com.android.server.wm.AccessibilityController$DisplayMagnifier$MagnifiedViewport$ViewportWindow$AnimationController */
                /* loaded from: classes2.dex */
                public final class AnimationController extends Handler {
                    public final ValueAnimator mShowHideFrameAnimator;

                    public AnimationController(Context context, Looper looper) {
                        super(looper);
                        ObjectAnimator ofInt = ObjectAnimator.ofInt(ViewportWindow.this, "alpha", 0, 255);
                        this.mShowHideFrameAnimator = ofInt;
                        ofInt.setInterpolator(new DecelerateInterpolator(2.5f));
                        ofInt.setDuration(context.getResources().getInteger(17694722));
                    }

                    public void onFrameShownStateChanged(boolean z, boolean z2) {
                        obtainMessage(1, z ? 1 : 0, z2 ? 1 : 0).sendToTarget();
                    }

                    @Override // android.os.Handler
                    public void handleMessage(Message message) {
                        if (message.what != 1) {
                            return;
                        }
                        boolean z = message.arg1 == 1;
                        if (message.arg2 == 1) {
                            if (this.mShowHideFrameAnimator.isRunning()) {
                                this.mShowHideFrameAnimator.reverse();
                                return;
                            } else if (z) {
                                this.mShowHideFrameAnimator.start();
                                return;
                            } else {
                                this.mShowHideFrameAnimator.reverse();
                                return;
                            }
                        }
                        this.mShowHideFrameAnimator.cancel();
                        if (z) {
                            ViewportWindow.this.setAlpha(255);
                        } else {
                            ViewportWindow.this.setAlpha(0);
                        }
                    }
                }
            }
        }

        /* renamed from: com.android.server.wm.AccessibilityController$DisplayMagnifier$MyHandler */
        /* loaded from: classes2.dex */
        public class MyHandler extends Handler {
            public MyHandler(Looper looper) {
                super(looper);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    Region region = (Region) ((SomeArgs) message.obj).arg1;
                    DisplayMagnifier.this.mCallbacks.onMagnificationRegionChanged(region);
                    region.recycle();
                } else if (i == 3) {
                    DisplayMagnifier.this.mCallbacks.onUserContextChanged();
                } else if (i == 4) {
                    DisplayMagnifier.this.mCallbacks.onDisplaySizeChanged();
                } else if (i != 5) {
                    if (i != 6) {
                        return;
                    }
                    DisplayMagnifier.this.mCallbacks.onImeWindowVisibilityChanged(message.arg1 == 1);
                } else {
                    synchronized (DisplayMagnifier.this.mService.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            if (DisplayMagnifier.this.isForceShowingMagnifiableBounds()) {
                                DisplayMagnifier.this.mMagnifedViewport.setMagnifiedRegionBorderShown(true, true);
                                DisplayMagnifier.this.mService.scheduleAnimationLocked();
                            }
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }
    }

    public static boolean isUntouchableNavigationBar(WindowState windowState, Region region) {
        if (windowState.mAttrs.type != 2019) {
            return false;
        }
        windowState.getTouchableRegion(region);
        return region.isEmpty();
    }

    public static Rect getSystemBarInsetsFrame(WindowState windowState) {
        if (windowState == null) {
            return EMPTY_RECT;
        }
        InsetsSourceProvider controllableInsetProvider = windowState.getControllableInsetProvider();
        return controllableInsetProvider != null ? controllableInsetProvider.getSource().getFrame() : EMPTY_RECT;
    }

    /* renamed from: com.android.server.wm.AccessibilityController$WindowsForAccessibilityObserver */
    /* loaded from: classes2.dex */
    public static final class WindowsForAccessibilityObserver {
        public final AccessibilityWindowsPopulator mA11yWindowsPopulator;
        public final AccessibilityControllerInternalImpl mAccessibilityTracing;
        public final WindowManagerInternal.WindowsForAccessibilityCallback mCallback;
        public final int mDisplayId;
        public final Handler mHandler;
        public boolean mInitialized;
        public final WindowManagerService mService;
        public final List<AccessibilityWindowsPopulator.AccessibilityWindow> mTempA11yWindows = new ArrayList();
        public final Set<IBinder> mTempBinderSet = new ArraySet();
        public final Point mTempPoint = new Point();
        public final Region mTempRegion = new Region();
        public final Region mTempRegion1 = new Region();
        public final Region mTempRegion2 = new Region();
        public final long mRecurringAccessibilityEventsIntervalMillis = ViewConfiguration.getSendRecurringAccessibilityEventsInterval();

        public static boolean isReportedWindowType(int i) {
            return (i == 2013 || i == 2021 || i == 2026 || i == 2016 || i == 2022 || i == 2018 || i == 2027 || i == 1004 || i == 2015 || i == 2030) ? false : true;
        }

        public WindowsForAccessibilityObserver(WindowManagerService windowManagerService, int i, WindowManagerInternal.WindowsForAccessibilityCallback windowsForAccessibilityCallback, AccessibilityWindowsPopulator accessibilityWindowsPopulator) {
            this.mService = windowManagerService;
            this.mCallback = windowsForAccessibilityCallback;
            this.mDisplayId = i;
            this.mHandler = new MyHandler(windowManagerService.f1164mH.getLooper());
            this.mAccessibilityTracing = AccessibilityController.getAccessibilityControllerInternal(windowManagerService);
            this.mA11yWindowsPopulator = accessibilityWindowsPopulator;
            computeChangedWindows(true);
        }

        public void performComputeChangedWindows(boolean z) {
            if (this.mAccessibilityTracing.isTracingEnabled(1024L)) {
                AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                accessibilityControllerInternalImpl.logTrace("WindowManager.performComputeChangedWindows", 1024L, "forceSend=" + z);
            }
            this.mHandler.removeMessages(1);
            computeChangedWindows(z);
        }

        public void scheduleComputeChangedWindows() {
            if (this.mAccessibilityTracing.isTracingEnabled(1024L)) {
                this.mAccessibilityTracing.logTrace("WindowManager.scheduleComputeChangedWindows", 1024L);
            }
            if (this.mHandler.hasMessages(1)) {
                return;
            }
            this.mHandler.sendEmptyMessageDelayed(1, this.mRecurringAccessibilityEventsIntervalMillis);
        }

        public void computeChangedWindows(boolean z) {
            WindowState topFocusWindow;
            if (this.mAccessibilityTracing.isTracingEnabled(1024L)) {
                this.mAccessibilityTracing.logTrace("WindowManager.computeChangedWindows", 1024L, "forceSend=" + z);
            }
            ArrayList arrayList = new ArrayList();
            synchronized (this.mService.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    RecentsAnimationController recentsAnimationController = this.mService.getRecentsAnimationController();
                    if (recentsAnimationController != null) {
                        topFocusWindow = recentsAnimationController.getTargetAppMainWindow();
                    } else {
                        topFocusWindow = getTopFocusWindow();
                    }
                    if (topFocusWindow == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    DisplayContent displayContent = this.mService.mRoot.getDisplayContent(this.mDisplayId);
                    if (displayContent == null) {
                        Slog.w(StartingSurfaceController.TAG, "display content is null, should be created later");
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    displayContent.getDisplay().getRealSize(this.mTempPoint);
                    Point point = this.mTempPoint;
                    int i = point.x;
                    int i2 = point.y;
                    Region region = this.mTempRegion;
                    region.set(0, 0, i, i2);
                    List<AccessibilityWindowsPopulator.AccessibilityWindow> list = this.mTempA11yWindows;
                    this.mA11yWindowsPopulator.populateVisibleWindowsOnScreenLocked(this.mDisplayId, list);
                    Set<IBinder> set = this.mTempBinderSet;
                    set.clear();
                    int size = list.size();
                    boolean z2 = false;
                    for (int i3 = 0; i3 < size; i3++) {
                        AccessibilityWindowsPopulator.AccessibilityWindow accessibilityWindow = list.get(i3);
                        Region region2 = new Region();
                        accessibilityWindow.getTouchableRegionInWindow(region2);
                        if (windowMattersToAccessibility(accessibilityWindow, region2, region)) {
                            addPopulatedWindowInfo(accessibilityWindow, region2, arrayList, set);
                            if (windowMattersToUnaccountedSpaceComputation(accessibilityWindow)) {
                                updateUnaccountedSpace(accessibilityWindow, region);
                            }
                            z2 |= accessibilityWindow.isFocused();
                        } else if (accessibilityWindow.isUntouchableNavigationBar()) {
                            region.op(AccessibilityController.getSystemBarInsetsFrame(this.mService.mWindowMap.get(accessibilityWindow.getWindowInfo().token)), region, Region.Op.REVERSE_DIFFERENCE);
                        }
                        if (region.isEmpty() && z2) {
                            break;
                        }
                    }
                    int size2 = arrayList.size();
                    for (int i4 = 0; i4 < size2; i4++) {
                        WindowInfo windowInfo = (WindowInfo) arrayList.get(i4);
                        if (!set.contains(windowInfo.parentToken)) {
                            windowInfo.parentToken = null;
                        }
                        List list2 = windowInfo.childTokens;
                        if (list2 != null) {
                            for (int size3 = list2.size() - 1; size3 >= 0; size3--) {
                                if (!set.contains(windowInfo.childTokens.get(size3))) {
                                    windowInfo.childTokens.remove(size3);
                                }
                            }
                        }
                    }
                    list.clear();
                    set.clear();
                    int displayId = this.mService.mRoot.getTopFocusedDisplayContent().getDisplayId();
                    IBinder asBinder = topFocusWindow.mClient.asBinder();
                    WindowManagerService.resetPriorityAfterLockedSection();
                    this.mCallback.onWindowsForAccessibilityChanged(z, displayId, asBinder, arrayList);
                    clearAndRecycleWindows(arrayList);
                    this.mInitialized = true;
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }

        public final boolean windowMattersToUnaccountedSpaceComputation(AccessibilityWindowsPopulator.AccessibilityWindow accessibilityWindow) {
            return (accessibilityWindow.isTouchable() || accessibilityWindow.getType() == 2034 || !accessibilityWindow.isTrustedOverlay()) && accessibilityWindow.getType() != 2032;
        }

        public final boolean windowMattersToAccessibility(AccessibilityWindowsPopulator.AccessibilityWindow accessibilityWindow, Region region, Region region2) {
            if (accessibilityWindow.ignoreRecentsAnimationForAccessibility()) {
                return false;
            }
            if (accessibilityWindow.isFocused()) {
                return true;
            }
            return (accessibilityWindow.isTouchable() || accessibilityWindow.getType() == 2034 || accessibilityWindow.isPIPMenu()) && !region2.quickReject(region) && isReportedWindowType(accessibilityWindow.getType());
        }

        public final void updateUnaccountedSpace(AccessibilityWindowsPopulator.AccessibilityWindow accessibilityWindow, Region region) {
            if (accessibilityWindow.getType() != 2032) {
                Region region2 = this.mTempRegion2;
                accessibilityWindow.getTouchableRegionInScreen(region2);
                region.op(region2, region, Region.Op.REVERSE_DIFFERENCE);
            }
        }

        public static void addPopulatedWindowInfo(AccessibilityWindowsPopulator.AccessibilityWindow accessibilityWindow, Region region, List<WindowInfo> list, Set<IBinder> set) {
            WindowInfo windowInfo = accessibilityWindow.getWindowInfo();
            if (windowInfo.token == null) {
                return;
            }
            windowInfo.regionInScreen.set(region);
            windowInfo.layer = set.size();
            list.add(windowInfo);
            set.add(windowInfo.token);
        }

        public static void clearAndRecycleWindows(List<WindowInfo> list) {
            for (int size = list.size() - 1; size >= 0; size--) {
                list.remove(size).recycle();
            }
        }

        public final WindowState getTopFocusWindow() {
            return this.mService.mRoot.getTopFocusedDisplayContent().mCurrentFocus;
        }

        public String toString() {
            return "WindowsForAccessibilityObserver{mDisplayId=" + this.mDisplayId + ", mInitialized=" + this.mInitialized + '}';
        }

        /* renamed from: com.android.server.wm.AccessibilityController$WindowsForAccessibilityObserver$MyHandler */
        /* loaded from: classes2.dex */
        public class MyHandler extends Handler {
            public MyHandler(Looper looper) {
                super(looper, null, false);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 1) {
                    return;
                }
                WindowsForAccessibilityObserver.this.computeChangedWindows(false);
            }
        }
    }

    /* renamed from: com.android.server.wm.AccessibilityController$AccessibilityControllerInternalImpl */
    /* loaded from: classes2.dex */
    public static final class AccessibilityControllerInternalImpl implements WindowManagerInternal.AccessibilityControllerInternal {
        public static AccessibilityControllerInternalImpl sInstance;
        public UiChangesForAccessibilityCallbacksDispatcher mCallbacksDispatcher;
        public volatile long mEnabledTracingFlags = 0;
        public final Looper mLooper;
        public final AccessibilityTracing mTracing;

        public static AccessibilityControllerInternalImpl getInstance(WindowManagerService windowManagerService) {
            AccessibilityControllerInternalImpl accessibilityControllerInternalImpl;
            synchronized (AccessibilityController.STATIC_LOCK) {
                if (sInstance == null) {
                    sInstance = new AccessibilityControllerInternalImpl(windowManagerService);
                }
                accessibilityControllerInternalImpl = sInstance;
            }
            return accessibilityControllerInternalImpl;
        }

        public AccessibilityControllerInternalImpl(WindowManagerService windowManagerService) {
            this.mLooper = windowManagerService.f1164mH.getLooper();
            this.mTracing = AccessibilityTracing.getInstance(windowManagerService);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.AccessibilityControllerInternal
        public void startTrace(long j) {
            this.mEnabledTracingFlags = j;
            this.mTracing.startTrace();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.AccessibilityControllerInternal
        public void stopTrace() {
            this.mTracing.stopTrace();
            this.mEnabledTracingFlags = 0L;
        }

        public boolean isTracingEnabled(long j) {
            return (j & this.mEnabledTracingFlags) != 0;
        }

        public void logTrace(String str, long j) {
            logTrace(str, j, "");
        }

        public void logTrace(String str, long j, String str2) {
            logTrace(str, j, str2, "".getBytes(), Binder.getCallingUid());
        }

        public void logTrace(String str, long j, String str2, byte[] bArr, int i) {
            this.mTracing.logState(str, j, str2, bArr, i, new HashSet(Arrays.asList("logTrace")));
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.AccessibilityControllerInternal
        public void logTrace(String str, long j, String str2, byte[] bArr, int i, StackTraceElement[] stackTraceElementArr, Set<String> set) {
            this.mTracing.logState(str, j, str2, bArr, i, stackTraceElementArr, set);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.AccessibilityControllerInternal
        public void logTrace(String str, long j, String str2, byte[] bArr, int i, StackTraceElement[] stackTraceElementArr, long j2, int i2, long j3, Set<String> set) {
            this.mTracing.logState(str, j, str2, bArr, i, stackTraceElementArr, j2, i2, j3, set);
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.AccessibilityControllerInternal
        public void setUiChangesForAccessibilityCallbacks(WindowManagerInternal.AccessibilityControllerInternal.UiChangesForAccessibilityCallbacks uiChangesForAccessibilityCallbacks) {
            if (isTracingEnabled(2048L)) {
                logTrace(AccessibilityController.TAG + ".setAccessibilityWindowManagerCallbacks", 2048L, "callbacks={" + uiChangesForAccessibilityCallbacks + "}");
            }
            if (uiChangesForAccessibilityCallbacks != null) {
                if (this.mCallbacksDispatcher != null) {
                    throw new IllegalStateException("Accessibility window manager callback already set!");
                }
                this.mCallbacksDispatcher = new UiChangesForAccessibilityCallbacksDispatcher(this, this.mLooper, uiChangesForAccessibilityCallbacks);
            } else if (this.mCallbacksDispatcher == null) {
                throw new IllegalStateException("Accessibility window manager callback already cleared!");
            } else {
                this.mCallbacksDispatcher = null;
            }
        }

        public boolean hasWindowManagerEventDispatcher() {
            if (isTracingEnabled(3072L)) {
                logTrace(AccessibilityController.TAG + ".hasCallbacks", 3072L);
            }
            return this.mCallbacksDispatcher != null;
        }

        public void onRectangleOnScreenRequested(int i, Rect rect) {
            if (isTracingEnabled(2048L)) {
                logTrace(AccessibilityController.TAG + ".onRectangleOnScreenRequested", 2048L, "rectangle={" + rect + "}");
            }
            UiChangesForAccessibilityCallbacksDispatcher uiChangesForAccessibilityCallbacksDispatcher = this.mCallbacksDispatcher;
            if (uiChangesForAccessibilityCallbacksDispatcher != null) {
                uiChangesForAccessibilityCallbacksDispatcher.onRectangleOnScreenRequested(i, rect);
            }
        }

        /* renamed from: com.android.server.wm.AccessibilityController$AccessibilityControllerInternalImpl$UiChangesForAccessibilityCallbacksDispatcher */
        /* loaded from: classes2.dex */
        public static final class UiChangesForAccessibilityCallbacksDispatcher {
            public final AccessibilityControllerInternalImpl mAccessibilityTracing;
            public final WindowManagerInternal.AccessibilityControllerInternal.UiChangesForAccessibilityCallbacks mCallbacks;
            public final Handler mHandler;

            public UiChangesForAccessibilityCallbacksDispatcher(AccessibilityControllerInternalImpl accessibilityControllerInternalImpl, Looper looper, WindowManagerInternal.AccessibilityControllerInternal.UiChangesForAccessibilityCallbacks uiChangesForAccessibilityCallbacks) {
                this.mAccessibilityTracing = accessibilityControllerInternalImpl;
                this.mCallbacks = uiChangesForAccessibilityCallbacks;
                this.mHandler = new Handler(looper);
            }

            public void onRectangleOnScreenRequested(int i, Rect rect) {
                if (this.mAccessibilityTracing.isTracingEnabled(2048L)) {
                    AccessibilityControllerInternalImpl accessibilityControllerInternalImpl = this.mAccessibilityTracing;
                    accessibilityControllerInternalImpl.logTrace("WindowManager.onRectangleOnScreenRequested", 2048L, "rectangle={" + rect + "}");
                }
                final WindowManagerInternal.AccessibilityControllerInternal.UiChangesForAccessibilityCallbacks uiChangesForAccessibilityCallbacks = this.mCallbacks;
                Objects.requireNonNull(uiChangesForAccessibilityCallbacks);
                this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: com.android.server.wm.AccessibilityController$AccessibilityControllerInternalImpl$UiChangesForAccessibilityCallbacksDispatcher$$ExternalSyntheticLambda0
                    public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                        WindowManagerInternal.AccessibilityControllerInternal.UiChangesForAccessibilityCallbacks.this.onRectangleOnScreenRequested(((Integer) obj).intValue(), ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), ((Integer) obj5).intValue());
                    }
                }, Integer.valueOf(i), Integer.valueOf(rect.left), Integer.valueOf(rect.top), Integer.valueOf(rect.right), Integer.valueOf(rect.bottom)));
            }
        }
    }

    /* renamed from: com.android.server.wm.AccessibilityController$AccessibilityTracing */
    /* loaded from: classes2.dex */
    public static final class AccessibilityTracing {
        public static AccessibilityTracing sInstance;
        public volatile boolean mEnabled;
        public final LogHandler mHandler;
        public final WindowManagerService mService;
        public final Object mLock = new Object();
        public final File mTraceFile = new File("/data/misc/a11ytrace/a11y_trace.winscope");
        public final TraceBuffer mBuffer = new TraceBuffer(12582912);

        public static AccessibilityTracing getInstance(WindowManagerService windowManagerService) {
            AccessibilityTracing accessibilityTracing;
            synchronized (AccessibilityController.STATIC_LOCK) {
                if (sInstance == null) {
                    sInstance = new AccessibilityTracing(windowManagerService);
                }
                accessibilityTracing = sInstance;
            }
            return accessibilityTracing;
        }

        public AccessibilityTracing(WindowManagerService windowManagerService) {
            this.mService = windowManagerService;
            HandlerThread handlerThread = new HandlerThread("AccessibilityTracing");
            handlerThread.start();
            this.mHandler = new LogHandler(handlerThread.getLooper());
        }

        public void startTrace() {
            if (Build.IS_USER) {
                Slog.e("AccessibilityTracing", "Error: Tracing is not supported on user builds.");
                return;
            }
            synchronized (this.mLock) {
                this.mEnabled = true;
                this.mBuffer.resetBuffer();
            }
        }

        public void stopTrace() {
            if (Build.IS_USER) {
                Slog.e("AccessibilityTracing", "Error: Tracing is not supported on user builds.");
                return;
            }
            synchronized (this.mLock) {
                this.mEnabled = false;
                if (this.mEnabled) {
                    Slog.e("AccessibilityTracing", "Error: tracing enabled while waiting for flush.");
                } else {
                    writeTraceToFile();
                }
            }
        }

        public void logState(String str, long j, String str2, byte[] bArr, int i, Set<String> set) {
            if (this.mEnabled) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                set.add("logState");
                logState(str, j, str2, bArr, i, stackTrace, set);
            }
        }

        public void logState(String str, long j, String str2, byte[] bArr, int i, StackTraceElement[] stackTraceElementArr, Set<String> set) {
            if (this.mEnabled) {
                log(str, j, str2, bArr, i, stackTraceElementArr, SystemClock.elapsedRealtimeNanos(), Process.myPid() + XmlUtils.STRING_ARRAY_SEPARATOR + Application.getProcessName(), Thread.currentThread().getId() + XmlUtils.STRING_ARRAY_SEPARATOR + Thread.currentThread().getName(), set);
            }
        }

        public void logState(String str, long j, String str2, byte[] bArr, int i, StackTraceElement[] stackTraceElementArr, long j2, int i2, long j3, Set<String> set) {
            if (this.mEnabled) {
                log(str, j, str2, bArr, i, stackTraceElementArr, j2, String.valueOf(i2), String.valueOf(j3), set);
            }
        }

        public final String toStackTraceString(StackTraceElement[] stackTraceElementArr, Set<String> set) {
            if (stackTraceElementArr == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            int i = 0;
            int i2 = -1;
            while (i < stackTraceElementArr.length) {
                Iterator<String> it = set.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    if (stackTraceElementArr[i].toString().contains(it.next())) {
                        i2 = i;
                        break;
                    }
                }
                if (i2 >= 0) {
                    break;
                }
                i++;
            }
            if (i < stackTraceElementArr.length) {
                do {
                    i++;
                    if (i >= stackTraceElementArr.length) {
                        break;
                    }
                    Iterator<String> it2 = set.iterator();
                    while (true) {
                        if (it2.hasNext()) {
                            if (stackTraceElementArr[i].toString().contains(it2.next())) {
                                i2 = i;
                                continue;
                                break;
                            }
                        }
                    }
                } while (i2 == i);
            }
            while (true) {
                i2++;
                if (i2 < stackTraceElementArr.length) {
                    sb.append(stackTraceElementArr[i2].toString());
                    sb.append("\n");
                } else {
                    return sb.toString();
                }
            }
        }

        public final void log(String str, long j, String str2, byte[] bArr, int i, StackTraceElement[] stackTraceElementArr, long j2, String str3, String str4, Set<String> set) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.argl1 = j2;
            obtain.argl2 = j;
            obtain.arg1 = str;
            obtain.arg2 = str3;
            obtain.arg3 = str4;
            obtain.arg4 = set;
            obtain.arg5 = str2;
            obtain.arg6 = stackTraceElementArr;
            obtain.arg7 = bArr;
            this.mHandler.obtainMessage(1, i, 0, obtain).sendToTarget();
        }

        public void writeTraceToFile() {
            this.mHandler.sendEmptyMessage(2);
        }

        /* renamed from: com.android.server.wm.AccessibilityController$AccessibilityTracing$LogHandler */
        /* loaded from: classes2.dex */
        public class LogHandler extends Handler {
            public LogHandler(Looper looper) {
                super(looper);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i != 1) {
                    if (i != 2) {
                        return;
                    }
                    synchronized (AccessibilityTracing.this.mLock) {
                        AccessibilityTracing.this.writeTraceToFileInternal();
                    }
                    return;
                }
                SomeArgs someArgs = (SomeArgs) message.obj;
                try {
                    ProtoOutputStream protoOutputStream = new ProtoOutputStream();
                    PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
                    long start = protoOutputStream.start(2246267895810L);
                    long j = someArgs.argl1;
                    long time = new Date().getTime() - ((SystemClock.elapsedRealtimeNanos() - j) / 1000000);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
                    protoOutputStream.write(1125281431553L, j);
                    protoOutputStream.write(1138166333442L, simpleDateFormat.format(Long.valueOf(time)).toString());
                    for (String str : AccessibilityTrace.getNamesOfLoggingTypes(someArgs.argl2)) {
                        protoOutputStream.write(2237677961219L, str);
                    }
                    protoOutputStream.write(1138166333446L, (String) someArgs.arg1);
                    protoOutputStream.write(1138166333444L, (String) someArgs.arg2);
                    protoOutputStream.write(1138166333445L, (String) someArgs.arg3);
                    protoOutputStream.write(1138166333447L, packageManagerInternal.getNameForUid(message.arg1));
                    protoOutputStream.write(1138166333448L, (String) someArgs.arg5);
                    protoOutputStream.write(1138166333449L, AccessibilityTracing.this.toStackTraceString((StackTraceElement[]) someArgs.arg6, (Set) someArgs.arg4));
                    protoOutputStream.write(1146756268042L, (byte[]) someArgs.arg7);
                    long start2 = protoOutputStream.start(1146756268043L);
                    synchronized (AccessibilityTracing.this.mService.mGlobalLock) {
                        WindowManagerService.boostPriorityForLockedSection();
                        AccessibilityTracing.this.mService.dumpDebugLocked(protoOutputStream, 0);
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    protoOutputStream.end(start2);
                    protoOutputStream.write(1138166333452L, AccessibilityTracing.this.printCpuStats(j));
                    protoOutputStream.end(start);
                    synchronized (AccessibilityTracing.this.mLock) {
                        AccessibilityTracing.this.mBuffer.add(protoOutputStream);
                    }
                } catch (Exception e) {
                    Slog.e("AccessibilityTracing", "Exception while tracing state", e);
                }
            }
        }

        public final void writeTraceToFileInternal() {
            try {
                ProtoOutputStream protoOutputStream = new ProtoOutputStream();
                protoOutputStream.write(1125281431553L, 4846245196254490945L);
                protoOutputStream.write(1125281431555L, TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()) - SystemClock.elapsedRealtimeNanos());
                this.mBuffer.writeTraceToFile(this.mTraceFile, protoOutputStream);
            } catch (IOException e) {
                Slog.e("AccessibilityTracing", "Unable to write buffer to file", e);
            }
        }

        public final String printCpuStats(long j) {
            Pair appProfileStatsForDebugging = this.mService.mAmInternal.getAppProfileStatsForDebugging(j, 5);
            return ((String) appProfileStatsForDebugging.first) + ((String) appProfileStatsForDebugging.second);
        }
    }
}
