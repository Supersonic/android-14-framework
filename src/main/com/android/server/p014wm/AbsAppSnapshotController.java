package com.android.server.p014wm;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.hardware.HardwareBuffer;
import android.os.Trace;
import android.util.Pair;
import android.util.Slog;
import android.view.InsetsState;
import android.view.SurfaceControl;
import android.view.ThreadedRenderer;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.window.ScreenCapture;
import android.window.SnapshotDrawerUtils;
import android.window.TaskSnapshot;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.graphics.ColorUtils;
import com.android.server.p014wm.AbsAppSnapshotCache;
import com.android.server.p014wm.WindowContainer;
import com.android.server.p014wm.utils.InsetUtils;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.AbsAppSnapshotController */
/* loaded from: classes2.dex */
public abstract class AbsAppSnapshotController<TYPE extends WindowContainer, CACHE extends AbsAppSnapshotCache<TYPE>> {
    @VisibleForTesting
    static final int SNAPSHOT_MODE_APP_THEME = 1;
    @VisibleForTesting
    static final int SNAPSHOT_MODE_NONE = 2;
    @VisibleForTesting
    static final int SNAPSHOT_MODE_REAL = 0;
    public CACHE mCache;
    public final boolean mIsRunningOnIoT;
    public final boolean mIsRunningOnTv;
    public final WindowManagerService mService;
    public boolean mSnapshotEnabled;
    public final Rect mTmpRect = new Rect();
    public final float mHighResTaskSnapshotScale = initSnapshotScale();

    public abstract ActivityRecord findAppTokenForSnapshot(TYPE type);

    public abstract ActivityManager.TaskDescription getTaskDescription(TYPE type);

    public abstract ActivityRecord getTopActivity(TYPE type);

    public abstract ActivityRecord getTopFullscreenActivity(TYPE type);

    public abstract boolean use16BitFormat();

    public AbsAppSnapshotController(WindowManagerService windowManagerService) {
        this.mService = windowManagerService;
        this.mIsRunningOnTv = windowManagerService.mContext.getPackageManager().hasSystemFeature("android.software.leanback");
        this.mIsRunningOnIoT = windowManagerService.mContext.getPackageManager().hasSystemFeature("android.hardware.type.embedded");
    }

    public float initSnapshotScale() {
        return this.mService.mContext.getResources().getFloat(17105078);
    }

    public void initialize(CACHE cache) {
        this.mCache = cache;
    }

    public void setSnapshotEnabled(boolean z) {
        this.mSnapshotEnabled = z;
    }

    public boolean shouldDisableSnapshots() {
        return this.mIsRunningOnTv || this.mIsRunningOnIoT || !this.mSnapshotEnabled;
    }

    @VisibleForTesting
    public TaskSnapshot captureSnapshot(TYPE type, boolean z) {
        if (z) {
            return snapshot(type);
        }
        int snapshotMode = getSnapshotMode(type);
        if (snapshotMode != 0) {
            if (snapshotMode != 1) {
                return snapshotMode != 2 ? null : null;
            }
            return drawAppThemeSnapshot(type);
        }
        return snapshot(type);
    }

    public final TaskSnapshot recordSnapshotInner(TYPE type, boolean z) {
        TaskSnapshot captureSnapshot = captureSnapshot(type, z && type.isActivityTypeHome());
        if (captureSnapshot == null) {
            return null;
        }
        HardwareBuffer hardwareBuffer = captureSnapshot.getHardwareBuffer();
        if (hardwareBuffer.getWidth() == 0 || hardwareBuffer.getHeight() == 0) {
            hardwareBuffer.close();
            Slog.e(StartingSurfaceController.TAG, "Invalid task snapshot dimensions " + hardwareBuffer.getWidth() + "x" + hardwareBuffer.getHeight());
            return null;
        }
        this.mCache.putSnapshot(type, captureSnapshot);
        return captureSnapshot;
    }

    @VisibleForTesting
    public int getSnapshotMode(TYPE type) {
        ActivityRecord topActivity = getTopActivity(type);
        if (type.isActivityTypeStandardOrUndefined() || type.isActivityTypeAssistant()) {
            return (topActivity == null || !topActivity.shouldUseAppThemeSnapshot()) ? 0 : 1;
        }
        return 2;
    }

    public TaskSnapshot snapshot(TYPE type) {
        return snapshot(type, 0);
    }

    public TaskSnapshot snapshot(TYPE type, int i) {
        ScreenCapture.ScreenshotHardwareBuffer createSnapshot;
        TaskSnapshot.Builder builder = new TaskSnapshot.Builder();
        if (prepareTaskSnapshot(type, i, builder) && (createSnapshot = createSnapshot(type, builder)) != null) {
            builder.setSnapshot(createSnapshot.getHardwareBuffer());
            builder.setColorSpace(createSnapshot.getColorSpace());
            return builder.build();
        }
        return null;
    }

    public ScreenCapture.ScreenshotHardwareBuffer createSnapshot(TYPE type, TaskSnapshot.Builder builder) {
        Point point = new Point();
        Trace.traceBegin(32L, "createSnapshot");
        ScreenCapture.ScreenshotHardwareBuffer createSnapshot = createSnapshot(type, this.mHighResTaskSnapshotScale, builder.getPixelFormat(), point, builder);
        Trace.traceEnd(32L);
        builder.setTaskSize(point);
        return createSnapshot;
    }

    public ScreenCapture.ScreenshotHardwareBuffer createSnapshot(TYPE type, float f, int i, Point point, TaskSnapshot.Builder builder) {
        SurfaceControl[] surfaceControlArr;
        if (type.getSurfaceControl() == null) {
            return null;
        }
        type.getBounds(this.mTmpRect);
        boolean z = false;
        this.mTmpRect.offsetTo(0, 0);
        WindowState windowState = type.getDisplayContent().mInputMethodWindow;
        boolean z2 = (windowState == null || windowState.getSurfaceControl() == null || type.getDisplayContent().shouldImeAttachedToApp()) ? false : true;
        WindowState navigationBar = type.getDisplayContent().getDisplayPolicy().getNavigationBar();
        boolean z3 = navigationBar != null;
        if (z2 && z3) {
            surfaceControlArr = new SurfaceControl[]{windowState.getSurfaceControl(), navigationBar.getSurfaceControl()};
        } else if (z2 || z3) {
            SurfaceControl[] surfaceControlArr2 = new SurfaceControl[1];
            surfaceControlArr2[0] = z2 ? windowState.getSurfaceControl() : navigationBar.getSurfaceControl();
            surfaceControlArr = surfaceControlArr2;
        } else {
            surfaceControlArr = new SurfaceControl[0];
        }
        if (!z2 && windowState != null && windowState.isVisible()) {
            z = true;
        }
        builder.setHasImeSurface(z);
        ScreenCapture.ScreenshotHardwareBuffer captureLayersExcluding = ScreenCapture.captureLayersExcluding(type.getSurfaceControl(), this.mTmpRect, f, i, surfaceControlArr);
        if (point != null) {
            point.x = this.mTmpRect.width();
            point.y = this.mTmpRect.height();
        }
        if (isInvalidHardwareBuffer(captureLayersExcluding == null ? null : captureLayersExcluding.getHardwareBuffer())) {
            return null;
        }
        return captureLayersExcluding;
    }

    public static boolean isInvalidHardwareBuffer(HardwareBuffer hardwareBuffer) {
        return hardwareBuffer == null || hardwareBuffer.isClosed() || hardwareBuffer.getWidth() <= 1 || hardwareBuffer.getHeight() <= 1;
    }

    @VisibleForTesting
    public boolean prepareTaskSnapshot(TYPE type, int i, TaskSnapshot.Builder builder) {
        Pair<ActivityRecord, WindowState> checkIfReadyToSnapshot = checkIfReadyToSnapshot(type);
        boolean z = false;
        if (checkIfReadyToSnapshot == null) {
            return false;
        }
        ActivityRecord activityRecord = (ActivityRecord) checkIfReadyToSnapshot.first;
        WindowState windowState = (WindowState) checkIfReadyToSnapshot.second;
        Rect systemBarInsets = getSystemBarInsets(windowState.getFrame(), windowState.getInsetsStateWithVisibilityOverride());
        Rect letterboxInsets = activityRecord.getLetterboxInsets();
        InsetUtils.addInsets(systemBarInsets, letterboxInsets);
        builder.setIsRealSnapshot(true);
        builder.setId(System.currentTimeMillis());
        builder.setContentInsets(systemBarInsets);
        builder.setLetterboxInsets(letterboxInsets);
        boolean z2 = windowState.getAttrs().format != -1;
        boolean hasWallpaper = windowState.hasWallpaper();
        if (i == 0) {
            i = (use16BitFormat() && activityRecord.fillsParent() && (!z2 || !hasWallpaper)) ? 4 : 1;
        }
        if (PixelFormat.formatHasAlpha(i) && (!activityRecord.fillsParent() || z2)) {
            z = true;
        }
        builder.setTopActivityComponent(activityRecord.mActivityComponent);
        builder.setPixelFormat(i);
        builder.setIsTranslucent(z);
        builder.setOrientation(activityRecord.getTask().getConfiguration().orientation);
        builder.setRotation(activityRecord.getTask().getDisplayContent().getRotation());
        builder.setWindowingMode(type.getWindowingMode());
        builder.setAppearance(getAppearance(type));
        return true;
    }

    public Pair<ActivityRecord, WindowState> checkIfReadyToSnapshot(TYPE type) {
        ActivityRecord findAppTokenForSnapshot;
        if (!this.mService.mPolicy.isScreenOn() || (findAppTokenForSnapshot = findAppTokenForSnapshot(type)) == null || findAppTokenForSnapshot.hasCommittedReparentToAnimationLeash()) {
            return null;
        }
        WindowState findMainWindow = findAppTokenForSnapshot.findMainWindow();
        if (findMainWindow == null) {
            Slog.w(StartingSurfaceController.TAG, "Failed to take screenshot. No main window for " + type);
            return null;
        } else if (findAppTokenForSnapshot.hasFixedRotationTransform()) {
            return null;
        } else {
            return new Pair<>(findAppTokenForSnapshot, findMainWindow);
        }
    }

    public final TaskSnapshot drawAppThemeSnapshot(TYPE type) {
        WindowState findMainWindow;
        ActivityRecord topActivity = getTopActivity(type);
        if (topActivity == null || (findMainWindow = topActivity.findMainWindow()) == null) {
            return null;
        }
        ActivityManager.TaskDescription taskDescription = getTaskDescription(type);
        int alphaComponent = ColorUtils.setAlphaComponent(taskDescription.getBackgroundColor(), 255);
        WindowManager.LayoutParams attrs = findMainWindow.getAttrs();
        Rect bounds = type.getBounds();
        Rect systemBarInsets = getSystemBarInsets(findMainWindow.getFrame(), findMainWindow.getInsetsStateWithVisibilityOverride());
        SnapshotDrawerUtils.SystemBarBackgroundPainter systemBarBackgroundPainter = new SnapshotDrawerUtils.SystemBarBackgroundPainter(attrs.flags, attrs.privateFlags, attrs.insetsFlags.appearance, taskDescription, this.mHighResTaskSnapshotScale, findMainWindow.getRequestedVisibleTypes());
        int width = bounds.width();
        int height = bounds.height();
        float f = this.mHighResTaskSnapshotScale;
        int i = (int) (width * f);
        int i2 = (int) (height * f);
        RenderNode create = RenderNode.create("SnapshotController", null);
        create.setLeftTopRightBottom(0, 0, i, i2);
        create.setClipToBounds(false);
        RecordingCanvas start = create.start(i, i2);
        start.drawColor(alphaComponent);
        systemBarBackgroundPainter.setInsets(systemBarInsets);
        systemBarBackgroundPainter.drawDecors(start, (Rect) null);
        create.end(start);
        Bitmap createHardwareBitmap = ThreadedRenderer.createHardwareBitmap(create, i, i2);
        if (createHardwareBitmap == null) {
            return null;
        }
        Rect rect = new Rect(systemBarInsets);
        Rect letterboxInsets = topActivity.getLetterboxInsets();
        InsetUtils.addInsets(rect, letterboxInsets);
        return new TaskSnapshot(System.currentTimeMillis(), topActivity.mActivityComponent, createHardwareBitmap.getHardwareBuffer(), createHardwareBitmap.getColorSpace(), findMainWindow.getConfiguration().orientation, findMainWindow.getWindowConfiguration().getRotation(), new Point(width, height), rect, letterboxInsets, false, false, type.getWindowingMode(), getAppearance(type), false, false);
    }

    public static Rect getSystemBarInsets(Rect rect, InsetsState insetsState) {
        return insetsState.calculateInsets(rect, WindowInsets.Type.systemBars(), false).toRect();
    }

    public final int getAppearance(TYPE type) {
        ActivityRecord topFullscreenActivity = getTopFullscreenActivity(type);
        WindowState topFullscreenOpaqueWindow = topFullscreenActivity != null ? topFullscreenActivity.getTopFullscreenOpaqueWindow() : null;
        if (topFullscreenOpaqueWindow != null) {
            return topFullscreenOpaqueWindow.mAttrs.insetsFlags.appearance;
        }
        return 0;
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println(str + "mHighResTaskSnapshotScale=" + this.mHighResTaskSnapshotScale);
        printWriter.println(str + "mTaskSnapshotEnabled=" + this.mSnapshotEnabled);
        this.mCache.dump(printWriter, str);
    }
}
