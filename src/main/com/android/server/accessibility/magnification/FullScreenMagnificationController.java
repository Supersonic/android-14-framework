package com.android.server.accessibility.magnification;

import android.accessibilityservice.MagnificationConfig;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.display.DisplayManagerInternal;
import android.os.Handler;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.MathUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.DisplayInfo;
import android.view.MagnificationSpec;
import android.view.WindowManager;
import android.view.accessibility.MagnificationAnimationCallback;
import android.view.animation.DecelerateInterpolator;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.LocalServices;
import com.android.server.accessibility.AccessibilityTraceManager;
import com.android.server.accessibility.magnification.FullScreenMagnificationController;
import com.android.server.p014wm.WindowManagerInternal;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FullScreenMagnificationController implements WindowManagerInternal.AccessibilityControllerInternal.UiChangesForAccessibilityCallbacks {
    public boolean mAlwaysOnMagnificationEnabled;
    public final ControllerContext mControllerCtx;
    public final DisplayManagerInternal mDisplayManagerInternal;
    @GuardedBy({"mLock"})
    public final SparseArray<DisplayMagnification> mDisplays;
    public final Object mLock;
    public boolean mMagnificationFollowTypingEnabled;
    public final MagnificationInfoChangedCallback mMagnificationInfoChangedCallback;
    public final long mMainThreadId;
    public final MagnificationScaleProvider mScaleProvider;
    public final ScreenStateObserver mScreenStateObserver;
    public final Rect mTempRect;
    public final Supplier<MagnificationThumbnail> mThumbnailSupplier;

    /* loaded from: classes.dex */
    public interface MagnificationInfoChangedCallback {
        void onFullScreenMagnificationActivationState(int i, boolean z);

        void onFullScreenMagnificationChanged(int i, Region region, MagnificationConfig magnificationConfig);

        void onImeWindowVisibilityChanged(int i, boolean z);

        void onRequestMagnificationSpec(int i, int i2);
    }

    /* loaded from: classes.dex */
    public final class DisplayMagnification implements WindowManagerInternal.MagnificationCallbacks {
        public boolean mDeleteAfterUnregister;
        public final int mDisplayId;
        @GuardedBy({"mLock"})
        public MagnificationThumbnail mMagnificationThumbnail;
        public boolean mRegistered;
        public final SpecAnimationBridge mSpecAnimationBridge;
        public boolean mUnregisterPending;
        public final MagnificationSpec mCurrentMagnificationSpec = new MagnificationSpec();
        public final Region mMagnificationRegion = Region.obtain();
        public final Rect mMagnificationBounds = new Rect();
        public final Rect mTempRect = new Rect();
        public final Rect mTempRect1 = new Rect();
        public int mIdOfLastServiceToMagnify = -1;
        public boolean mMagnificationActivated = false;

        public DisplayMagnification(int i) {
            this.mDisplayId = i;
            this.mSpecAnimationBridge = new SpecAnimationBridge(FullScreenMagnificationController.this.mControllerCtx, FullScreenMagnificationController.this.mLock, i);
        }

        @GuardedBy({"mLock"})
        public boolean register() {
            if (FullScreenMagnificationController.this.traceEnabled()) {
                FullScreenMagnificationController fullScreenMagnificationController = FullScreenMagnificationController.this;
                fullScreenMagnificationController.logTrace("setMagnificationCallbacks", "displayID=" + this.mDisplayId + ";callback=" + this);
            }
            boolean magnificationCallbacks = FullScreenMagnificationController.this.mControllerCtx.getWindowManager().setMagnificationCallbacks(this.mDisplayId, this);
            this.mRegistered = magnificationCallbacks;
            if (!magnificationCallbacks) {
                Slog.w("FullScreenMagnificationController", "set magnification callbacks fail, displayId:" + this.mDisplayId);
                return false;
            }
            this.mSpecAnimationBridge.setEnabled(true);
            if (FullScreenMagnificationController.this.traceEnabled()) {
                FullScreenMagnificationController fullScreenMagnificationController2 = FullScreenMagnificationController.this;
                fullScreenMagnificationController2.logTrace("getMagnificationRegion", "displayID=" + this.mDisplayId + ";region=" + this.mMagnificationRegion);
            }
            FullScreenMagnificationController.this.mControllerCtx.getWindowManager().getMagnificationRegion(this.mDisplayId, this.mMagnificationRegion);
            this.mMagnificationRegion.getBounds(this.mMagnificationBounds);
            if (this.mMagnificationThumbnail == null) {
                this.mMagnificationThumbnail = (MagnificationThumbnail) FullScreenMagnificationController.this.mThumbnailSupplier.get();
            }
            return true;
        }

        @GuardedBy({"mLock"})
        public void unregister(boolean z) {
            if (this.mRegistered) {
                this.mSpecAnimationBridge.setEnabled(false);
                if (FullScreenMagnificationController.this.traceEnabled()) {
                    FullScreenMagnificationController fullScreenMagnificationController = FullScreenMagnificationController.this;
                    fullScreenMagnificationController.logTrace("setMagnificationCallbacks", "displayID=" + this.mDisplayId + ";callback=null");
                }
                FullScreenMagnificationController.this.mControllerCtx.getWindowManager().setMagnificationCallbacks(this.mDisplayId, null);
                this.mMagnificationRegion.setEmpty();
                this.mRegistered = false;
                FullScreenMagnificationController.this.unregisterCallbackLocked(this.mDisplayId, z);
                destroyThumbNail();
            }
            this.mUnregisterPending = false;
        }

        @GuardedBy({"mLock"})
        public void unregisterPending(boolean z) {
            this.mDeleteAfterUnregister = z;
            this.mUnregisterPending = true;
            reset(true);
        }

        public boolean isRegistered() {
            return this.mRegistered;
        }

        public boolean isActivated() {
            return this.mMagnificationActivated;
        }

        public float getScale() {
            return this.mCurrentMagnificationSpec.scale;
        }

        public float getOffsetX() {
            return this.mCurrentMagnificationSpec.offsetX;
        }

        public float getOffsetY() {
            return this.mCurrentMagnificationSpec.offsetY;
        }

        @GuardedBy({"mLock"})
        public float getCenterX() {
            return (((this.mMagnificationBounds.width() / 2.0f) + this.mMagnificationBounds.left) - getOffsetX()) / getScale();
        }

        @GuardedBy({"mLock"})
        public float getCenterY() {
            return (((this.mMagnificationBounds.height() / 2.0f) + this.mMagnificationBounds.top) - getOffsetY()) / getScale();
        }

        public float getSentScale() {
            return this.mSpecAnimationBridge.mSentMagnificationSpec.scale;
        }

        public float getSentOffsetX() {
            return this.mSpecAnimationBridge.mSentMagnificationSpec.offsetX;
        }

        public float getSentOffsetY() {
            return this.mSpecAnimationBridge.mSentMagnificationSpec.offsetY;
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.MagnificationCallbacks
        public void onMagnificationRegionChanged(Region region) {
            FullScreenMagnificationController.this.mControllerCtx.getHandler().sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.magnification.FullScreenMagnificationController$DisplayMagnification$$ExternalSyntheticLambda4
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((FullScreenMagnificationController.DisplayMagnification) obj).updateMagnificationRegion((Region) obj2);
                }
            }, this, Region.obtain(region)));
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.MagnificationCallbacks
        public void onRectangleOnScreenRequested(int i, int i2, int i3, int i4) {
            FullScreenMagnificationController.this.mControllerCtx.getHandler().sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: com.android.server.accessibility.magnification.FullScreenMagnificationController$DisplayMagnification$$ExternalSyntheticLambda0
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    ((FullScreenMagnificationController.DisplayMagnification) obj).requestRectangleOnScreen(((Integer) obj2).intValue(), ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), ((Integer) obj5).intValue());
                }
            }, this, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)));
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.MagnificationCallbacks
        public void onDisplaySizeChanged() {
            onUserContextChanged();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.MagnificationCallbacks
        public void onUserContextChanged() {
            FullScreenMagnificationController.this.mControllerCtx.getHandler().sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.magnification.FullScreenMagnificationController$DisplayMagnification$$ExternalSyntheticLambda3
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((FullScreenMagnificationController) obj).onUserContextChanged(((Integer) obj2).intValue());
                }
            }, FullScreenMagnificationController.this, Integer.valueOf(this.mDisplayId)));
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.MagnificationCallbacks
        public void onImeWindowVisibilityChanged(boolean z) {
            FullScreenMagnificationController.this.mControllerCtx.getHandler().sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.magnification.FullScreenMagnificationController$DisplayMagnification$$ExternalSyntheticLambda2
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((FullScreenMagnificationController) obj).notifyImeWindowVisibilityChanged(((Integer) obj2).intValue(), ((Boolean) obj3).booleanValue());
                }
            }, FullScreenMagnificationController.this, Integer.valueOf(this.mDisplayId), Boolean.valueOf(z)));
        }

        public void updateMagnificationRegion(Region region) {
            synchronized (FullScreenMagnificationController.this.mLock) {
                if (this.mRegistered) {
                    if (!this.mMagnificationRegion.equals(region)) {
                        this.mMagnificationRegion.set(region);
                        this.mMagnificationRegion.getBounds(this.mMagnificationBounds);
                        refreshThumbNail(getScale(), getCenterX(), getCenterY());
                        MagnificationSpec magnificationSpec = this.mCurrentMagnificationSpec;
                        if (updateCurrentSpecWithOffsetsLocked(magnificationSpec.offsetX, magnificationSpec.offsetY)) {
                            sendSpecToAnimation(this.mCurrentMagnificationSpec, null);
                        }
                        onMagnificationChangedLocked();
                    }
                    region.recycle();
                }
            }
        }

        public void sendSpecToAnimation(MagnificationSpec magnificationSpec, MagnificationAnimationCallback magnificationAnimationCallback) {
            if (Thread.currentThread().getId() == FullScreenMagnificationController.this.mMainThreadId) {
                this.mSpecAnimationBridge.updateSentSpecMainThread(magnificationSpec, magnificationAnimationCallback);
                return;
            }
            FullScreenMagnificationController.this.mControllerCtx.getHandler().sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.magnification.FullScreenMagnificationController$DisplayMagnification$$ExternalSyntheticLambda1
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((FullScreenMagnificationController.SpecAnimationBridge) obj).updateSentSpecMainThread((MagnificationSpec) obj2, (MagnificationAnimationCallback) obj3);
                }
            }, this.mSpecAnimationBridge, magnificationSpec, magnificationAnimationCallback));
        }

        public int getIdOfLastServiceToMagnify() {
            return this.mIdOfLastServiceToMagnify;
        }

        @GuardedBy({"mLock"})
        public void onMagnificationChangedLocked() {
            float scale = getScale();
            float centerX = getCenterX();
            float centerY = getCenterY();
            FullScreenMagnificationController.this.mMagnificationInfoChangedCallback.onFullScreenMagnificationChanged(this.mDisplayId, this.mMagnificationRegion, new MagnificationConfig.Builder().setMode(1).setActivated(this.mMagnificationActivated).setScale(scale).setCenterX(centerX).setCenterY(centerY).build());
            if (this.mUnregisterPending && !isActivated()) {
                unregister(this.mDeleteAfterUnregister);
            }
            if (isActivated()) {
                updateThumbNail(scale, centerX, centerY);
            } else {
                hideThumbNail();
            }
        }

        @GuardedBy({"mLock"})
        public boolean magnificationRegionContains(float f, float f2) {
            return this.mMagnificationRegion.contains((int) f, (int) f2);
        }

        @GuardedBy({"mLock"})
        public void getMagnificationBounds(Rect rect) {
            rect.set(this.mMagnificationBounds);
        }

        @GuardedBy({"mLock"})
        public void getMagnificationRegion(Region region) {
            region.set(this.mMagnificationRegion);
        }

        public final DisplayMetrics getDisplayMetricsForId() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            DisplayInfo displayInfo = FullScreenMagnificationController.this.mDisplayManagerInternal.getDisplayInfo(this.mDisplayId);
            if (displayInfo != null) {
                displayInfo.getLogicalMetrics(displayMetrics, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, (Configuration) null);
            } else {
                displayMetrics.setToDefaults();
            }
            return displayMetrics;
        }

        public void requestRectangleOnScreen(int i, int i2, int i3, int i4) {
            int i5;
            int i6;
            float f;
            int i7;
            int i8;
            synchronized (FullScreenMagnificationController.this.mLock) {
                Rect rect = this.mTempRect;
                getMagnificationBounds(rect);
                if (rect.intersects(i, i2, i3, i4)) {
                    Rect rect2 = this.mTempRect1;
                    getMagnifiedFrameInContentCoordsLocked(rect2);
                    float width = rect2.width() / 4.0f;
                    float applyDimension = TypedValue.applyDimension(1, 10.0f, getDisplayMetricsForId());
                    float f2 = 0.0f;
                    if (i3 - i > rect2.width()) {
                        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 0) {
                            f = i - rect2.left;
                        } else {
                            f = i3 - rect2.right;
                        }
                    } else {
                        if (i < rect2.left) {
                            f = (i - i5) - width;
                        } else {
                            f = i3 > rect2.right ? (i3 - i6) + width : 0.0f;
                        }
                    }
                    if (i4 - i2 > rect2.height()) {
                        f2 = i2 - rect2.top;
                    } else {
                        if (i2 < rect2.top) {
                            f2 = (i2 - i7) - applyDimension;
                        } else {
                            if (i4 > rect2.bottom) {
                                f2 = (i4 - i8) + applyDimension;
                            }
                        }
                    }
                    float scale = getScale();
                    offsetMagnifiedRegion(f * scale, f2 * scale, -1);
                }
            }
        }

        public void getMagnifiedFrameInContentCoordsLocked(Rect rect) {
            float sentScale = getSentScale();
            float sentOffsetX = getSentOffsetX();
            float sentOffsetY = getSentOffsetY();
            getMagnificationBounds(rect);
            rect.offset((int) (-sentOffsetX), (int) (-sentOffsetY));
            rect.scale(1.0f / sentScale);
        }

        @GuardedBy({"mLock"})
        public final boolean setActivated(boolean z) {
            boolean z2 = this.mMagnificationActivated != z;
            if (z2) {
                this.mMagnificationActivated = z;
                FullScreenMagnificationController.this.mMagnificationInfoChangedCallback.onFullScreenMagnificationActivationState(this.mDisplayId, this.mMagnificationActivated);
                FullScreenMagnificationController.this.mControllerCtx.getWindowManager().setForceShowMagnifiableBounds(this.mDisplayId, z);
            }
            return z2;
        }

        @GuardedBy({"mLock"})
        public boolean reset(boolean z) {
            return reset(FullScreenMagnificationController.transformToStubCallback(z));
        }

        @GuardedBy({"mLock"})
        public boolean reset(MagnificationAnimationCallback magnificationAnimationCallback) {
            if (this.mRegistered) {
                MagnificationSpec magnificationSpec = this.mCurrentMagnificationSpec;
                boolean isActivated = isActivated();
                setActivated(false);
                if (isActivated) {
                    magnificationSpec.clear();
                    onMagnificationChangedLocked();
                }
                this.mIdOfLastServiceToMagnify = -1;
                sendSpecToAnimation(magnificationSpec, magnificationAnimationCallback);
                hideThumbNail();
                return isActivated;
            }
            return false;
        }

        @GuardedBy({"mLock"})
        public boolean setScale(float f, float f2, float f3, boolean z, int i) {
            if (this.mRegistered) {
                float constrainScale = MagnificationScaleProvider.constrainScale(f);
                Rect rect = this.mTempRect;
                this.mMagnificationRegion.getBounds(rect);
                MagnificationSpec magnificationSpec = this.mCurrentMagnificationSpec;
                float f4 = magnificationSpec.scale;
                float f5 = magnificationSpec.offsetY;
                float height = (((rect.height() / 2.0f) - f5) + rect.top) / f4;
                float f6 = (f2 - magnificationSpec.offsetX) / f4;
                float f7 = (f3 - f5) / f4;
                float f8 = f4 / constrainScale;
                float f9 = (height - f7) * f8;
                this.mIdOfLastServiceToMagnify = i;
                return setScaleAndCenter(constrainScale, f6 + ((((((rect.width() / 2.0f) - magnificationSpec.offsetX) + rect.left) / f4) - f6) * f8), f7 + f9, FullScreenMagnificationController.transformToStubCallback(z), i);
            }
            return false;
        }

        @GuardedBy({"mLock"})
        public boolean setScaleAndCenter(float f, float f2, float f3, MagnificationAnimationCallback magnificationAnimationCallback, int i) {
            if (this.mRegistered) {
                boolean updateMagnificationSpecLocked = updateMagnificationSpecLocked(f, f2, f3) | setActivated(true);
                sendSpecToAnimation(this.mCurrentMagnificationSpec, magnificationAnimationCallback);
                if (isActivated() && i != -1) {
                    this.mIdOfLastServiceToMagnify = i;
                    FullScreenMagnificationController.this.mMagnificationInfoChangedCallback.onRequestMagnificationSpec(this.mDisplayId, this.mIdOfLastServiceToMagnify);
                }
                return updateMagnificationSpecLocked;
            }
            return false;
        }

        @GuardedBy({"mLock"})
        public void updateThumbNail(float f, float f2, float f3) {
            MagnificationThumbnail magnificationThumbnail = this.mMagnificationThumbnail;
            if (magnificationThumbnail != null) {
                magnificationThumbnail.updateThumbNail(f, f2, f3);
            }
        }

        @GuardedBy({"mLock"})
        public void refreshThumbNail(float f, float f2, float f3) {
            MagnificationThumbnail magnificationThumbnail = this.mMagnificationThumbnail;
            if (magnificationThumbnail != null) {
                magnificationThumbnail.setThumbNailBounds(this.mMagnificationBounds, f, f2, f3);
            }
        }

        @GuardedBy({"mLock"})
        public void hideThumbNail() {
            MagnificationThumbnail magnificationThumbnail = this.mMagnificationThumbnail;
            if (magnificationThumbnail != null) {
                magnificationThumbnail.hideThumbNail();
            }
        }

        @GuardedBy({"mLock"})
        public void destroyThumbNail() {
            if (this.mMagnificationThumbnail != null) {
                hideThumbNail();
                this.mMagnificationThumbnail = null;
            }
        }

        public boolean updateMagnificationSpecLocked(float f, float f2, float f3) {
            boolean z;
            if (Float.isNaN(f2)) {
                f2 = getCenterX();
            }
            if (Float.isNaN(f3)) {
                f3 = getCenterY();
            }
            if (Float.isNaN(f)) {
                f = getScale();
            }
            float constrainScale = MagnificationScaleProvider.constrainScale(f);
            if (Float.compare(this.mCurrentMagnificationSpec.scale, constrainScale) != 0) {
                this.mCurrentMagnificationSpec.scale = constrainScale;
                z = true;
            } else {
                z = false;
            }
            Rect rect = this.mMagnificationBounds;
            boolean updateCurrentSpecWithOffsetsLocked = updateCurrentSpecWithOffsetsLocked(((this.mMagnificationBounds.width() / 2.0f) + rect.left) - (f2 * constrainScale), ((rect.height() / 2.0f) + this.mMagnificationBounds.top) - (f3 * constrainScale)) | z;
            if (updateCurrentSpecWithOffsetsLocked) {
                onMagnificationChangedLocked();
            }
            return updateCurrentSpecWithOffsetsLocked;
        }

        @GuardedBy({"mLock"})
        public void offsetMagnifiedRegion(float f, float f2, int i) {
            if (this.mRegistered) {
                MagnificationSpec magnificationSpec = this.mCurrentMagnificationSpec;
                if (updateCurrentSpecWithOffsetsLocked(magnificationSpec.offsetX - f, magnificationSpec.offsetY - f2)) {
                    onMagnificationChangedLocked();
                }
                if (i != -1) {
                    this.mIdOfLastServiceToMagnify = i;
                }
                sendSpecToAnimation(this.mCurrentMagnificationSpec, null);
            }
        }

        public boolean updateCurrentSpecWithOffsetsLocked(float f, float f2) {
            boolean z;
            float constrain = MathUtils.constrain(f, getMinOffsetXLocked(), getMaxOffsetXLocked());
            if (Float.compare(this.mCurrentMagnificationSpec.offsetX, constrain) != 0) {
                this.mCurrentMagnificationSpec.offsetX = constrain;
                z = true;
            } else {
                z = false;
            }
            float constrain2 = MathUtils.constrain(f2, getMinOffsetYLocked(), getMaxOffsetYLocked());
            if (Float.compare(this.mCurrentMagnificationSpec.offsetY, constrain2) != 0) {
                this.mCurrentMagnificationSpec.offsetY = constrain2;
                return true;
            }
            return z;
        }

        public float getMinOffsetXLocked() {
            float width = this.mMagnificationBounds.left + this.mMagnificationBounds.width();
            return width - (this.mCurrentMagnificationSpec.scale * width);
        }

        public float getMaxOffsetXLocked() {
            int i = this.mMagnificationBounds.left;
            return i - (i * this.mCurrentMagnificationSpec.scale);
        }

        public float getMinOffsetYLocked() {
            float height = this.mMagnificationBounds.top + this.mMagnificationBounds.height();
            return height - (this.mCurrentMagnificationSpec.scale * height);
        }

        public float getMaxOffsetYLocked() {
            int i = this.mMagnificationBounds.top;
            return i - (i * this.mCurrentMagnificationSpec.scale);
        }

        public String toString() {
            return "DisplayMagnification[mCurrentMagnificationSpec=" + this.mCurrentMagnificationSpec + ", mMagnificationRegion=" + this.mMagnificationRegion + ", mMagnificationBounds=" + this.mMagnificationBounds + ", mDisplayId=" + this.mDisplayId + ", mIdOfLastServiceToMagnify=" + this.mIdOfLastServiceToMagnify + ", mRegistered=" + this.mRegistered + ", mUnregisterPending=" + this.mUnregisterPending + ']';
        }
    }

    public FullScreenMagnificationController(final Context context, AccessibilityTraceManager accessibilityTraceManager, Object obj, MagnificationInfoChangedCallback magnificationInfoChangedCallback, MagnificationScaleProvider magnificationScaleProvider) {
        this(new ControllerContext(context, accessibilityTraceManager, (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class), new Handler(context.getMainLooper()), context.getResources().getInteger(17694722)), obj, magnificationInfoChangedCallback, magnificationScaleProvider, new Supplier() { // from class: com.android.server.accessibility.magnification.FullScreenMagnificationController$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                MagnificationThumbnail lambda$new$0;
                lambda$new$0 = FullScreenMagnificationController.lambda$new$0(context);
                return lambda$new$0;
            }
        });
    }

    public static /* synthetic */ MagnificationThumbnail lambda$new$0(Context context) {
        if (DeviceConfig.getBoolean("accessibility", "enable_magnifier_thumbnail", false)) {
            return new MagnificationThumbnail(context, (WindowManager) context.getSystemService(WindowManager.class), new Handler(context.getMainLooper()));
        }
        return null;
    }

    @VisibleForTesting
    public FullScreenMagnificationController(ControllerContext controllerContext, Object obj, MagnificationInfoChangedCallback magnificationInfoChangedCallback, MagnificationScaleProvider magnificationScaleProvider, Supplier<MagnificationThumbnail> supplier) {
        this.mDisplays = new SparseArray<>(0);
        this.mTempRect = new Rect();
        this.mMagnificationFollowTypingEnabled = true;
        this.mAlwaysOnMagnificationEnabled = false;
        this.mControllerCtx = controllerContext;
        this.mLock = obj;
        this.mMainThreadId = controllerContext.getContext().getMainLooper().getThread().getId();
        this.mScreenStateObserver = new ScreenStateObserver(controllerContext.getContext(), this);
        this.mMagnificationInfoChangedCallback = magnificationInfoChangedCallback;
        this.mScaleProvider = magnificationScaleProvider;
        this.mDisplayManagerInternal = (DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class);
        this.mThumbnailSupplier = supplier;
    }

    public void register(int i) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                displayMagnification = new DisplayMagnification(i);
            }
            if (displayMagnification.isRegistered()) {
                return;
            }
            if (displayMagnification.register()) {
                this.mDisplays.put(i, displayMagnification);
                this.mScreenStateObserver.registerIfNecessary();
            }
        }
    }

    public void unregister(int i) {
        synchronized (this.mLock) {
            unregisterLocked(i, false);
        }
    }

    public void unregisterAll() {
        synchronized (this.mLock) {
            SparseArray<DisplayMagnification> clone = this.mDisplays.clone();
            for (int i = 0; i < clone.size(); i++) {
                unregisterLocked(clone.keyAt(i), false);
            }
        }
    }

    @Override // com.android.server.p014wm.WindowManagerInternal.AccessibilityControllerInternal.UiChangesForAccessibilityCallbacks
    public void onRectangleOnScreenRequested(int i, int i2, int i3, int i4, int i5) {
        synchronized (this.mLock) {
            if (this.mMagnificationFollowTypingEnabled) {
                DisplayMagnification displayMagnification = this.mDisplays.get(i);
                if (displayMagnification == null) {
                    return;
                }
                if (displayMagnification.isActivated()) {
                    Rect rect = this.mTempRect;
                    displayMagnification.getMagnifiedFrameInContentCoordsLocked(rect);
                    if (rect.contains(i2, i3, i4, i5)) {
                        return;
                    }
                    displayMagnification.onRectangleOnScreenRequested(i2, i3, i4, i5);
                }
            }
        }
    }

    public void setMagnificationFollowTypingEnabled(boolean z) {
        this.mMagnificationFollowTypingEnabled = z;
    }

    public void setAlwaysOnMagnificationEnabled(boolean z) {
        this.mAlwaysOnMagnificationEnabled = z;
    }

    public boolean isAlwaysOnMagnificationEnabled() {
        return this.mAlwaysOnMagnificationEnabled;
    }

    public void onUserContextChanged(int i) {
        synchronized (this.mLock) {
            if (isAlwaysOnMagnificationEnabled()) {
                setScaleAndCenter(i, 1.0f, Float.NaN, Float.NaN, true, 0);
            } else {
                reset(i, true);
            }
        }
    }

    public void onDisplayRemoved(int i) {
        synchronized (this.mLock) {
            unregisterLocked(i, true);
        }
    }

    public boolean isRegistered(int i) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return false;
            }
            return displayMagnification.isRegistered();
        }
    }

    public boolean isActivated(int i) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return false;
            }
            return displayMagnification.isActivated();
        }
    }

    public boolean magnificationRegionContains(int i, float f, float f2) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return false;
            }
            return displayMagnification.magnificationRegionContains(f, f2);
        }
    }

    public void getMagnificationRegion(int i, Region region) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return;
            }
            displayMagnification.getMagnificationRegion(region);
        }
    }

    public float getScale(int i) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return 1.0f;
            }
            return displayMagnification.getScale();
        }
    }

    public float getCenterX(int i) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return 0.0f;
            }
            return displayMagnification.getCenterX();
        }
    }

    public float getCenterY(int i) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return 0.0f;
            }
            return displayMagnification.getCenterY();
        }
    }

    public boolean reset(int i, boolean z) {
        return reset(i, z ? MagnificationAnimationCallback.STUB_ANIMATION_CALLBACK : null);
    }

    public boolean reset(int i, MagnificationAnimationCallback magnificationAnimationCallback) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return false;
            }
            return displayMagnification.reset(magnificationAnimationCallback);
        }
    }

    public boolean setScale(int i, float f, float f2, float f3, boolean z, int i2) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return false;
            }
            return displayMagnification.setScale(f, f2, f3, z, i2);
        }
    }

    public boolean setCenter(int i, float f, float f2, boolean z, int i2) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return false;
            }
            return displayMagnification.setScaleAndCenter(Float.NaN, f, f2, z ? MagnificationAnimationCallback.STUB_ANIMATION_CALLBACK : null, i2);
        }
    }

    public boolean setScaleAndCenter(int i, float f, float f2, float f3, boolean z, int i2) {
        return setScaleAndCenter(i, f, f2, f3, transformToStubCallback(z), i2);
    }

    public boolean setScaleAndCenter(int i, float f, float f2, float f3, MagnificationAnimationCallback magnificationAnimationCallback, int i2) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return false;
            }
            return displayMagnification.setScaleAndCenter(f, f2, f3, magnificationAnimationCallback, i2);
        }
    }

    public void offsetMagnifiedRegion(int i, float f, float f2, int i2) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return;
            }
            displayMagnification.offsetMagnifiedRegion(f, f2, i2);
        }
    }

    public int getIdOfLastServiceToMagnify(int i) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification == null) {
                return -1;
            }
            return displayMagnification.getIdOfLastServiceToMagnify();
        }
    }

    public void persistScale(int i) {
        float scale = getScale(0);
        if (scale < 1.3f) {
            return;
        }
        this.mScaleProvider.putScale(scale, i);
    }

    public float getPersistedScale(int i) {
        return MathUtils.constrain(this.mScaleProvider.getScale(i), 1.3f, 8.0f);
    }

    public void resetAllIfNeeded(int i) {
        synchronized (this.mLock) {
            for (int i2 = 0; i2 < this.mDisplays.size(); i2++) {
                resetIfNeeded(this.mDisplays.keyAt(i2), i);
            }
        }
    }

    public boolean resetIfNeeded(int i, boolean z) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification != null && displayMagnification.isActivated()) {
                displayMagnification.reset(z);
                return true;
            }
            return false;
        }
    }

    public boolean resetIfNeeded(int i, int i2) {
        synchronized (this.mLock) {
            DisplayMagnification displayMagnification = this.mDisplays.get(i);
            if (displayMagnification != null && displayMagnification.isActivated() && i2 == displayMagnification.getIdOfLastServiceToMagnify()) {
                displayMagnification.reset(true);
                return true;
            }
            return false;
        }
    }

    public void notifyImeWindowVisibilityChanged(int i, boolean z) {
        this.mMagnificationInfoChangedCallback.onImeWindowVisibilityChanged(i, z);
    }

    public final void onScreenTurnedOff() {
        this.mControllerCtx.getHandler().sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.magnification.FullScreenMagnificationController$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((FullScreenMagnificationController) obj).resetAllIfNeeded(((Boolean) obj2).booleanValue());
            }
        }, this, Boolean.FALSE));
    }

    public void resetAllIfNeeded(boolean z) {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mDisplays.size(); i++) {
                resetIfNeeded(this.mDisplays.keyAt(i), z);
            }
        }
    }

    public final void unregisterLocked(int i, boolean z) {
        DisplayMagnification displayMagnification = this.mDisplays.get(i);
        if (displayMagnification == null) {
            return;
        }
        if (!displayMagnification.isRegistered()) {
            if (z) {
                this.mDisplays.remove(i);
            }
        } else if (!displayMagnification.isActivated()) {
            displayMagnification.unregister(z);
        } else {
            displayMagnification.unregisterPending(z);
        }
    }

    public final void unregisterCallbackLocked(int i, boolean z) {
        if (z) {
            this.mDisplays.remove(i);
        }
        boolean z2 = false;
        for (int i2 = 0; i2 < this.mDisplays.size() && !(z2 = this.mDisplays.valueAt(i2).isRegistered()); i2++) {
        }
        if (z2) {
            return;
        }
        this.mScreenStateObserver.unregister();
    }

    public final boolean traceEnabled() {
        return this.mControllerCtx.getTraceManager().isA11yTracingEnabledForTypes(512L);
    }

    public final void logTrace(String str, String str2) {
        AccessibilityTraceManager traceManager = this.mControllerCtx.getTraceManager();
        traceManager.logTrace("WindowManagerInternal." + str, 512L, str2);
    }

    public String toString() {
        return "MagnificationController[, mDisplays=" + this.mDisplays + ", mScaleProvider=" + this.mScaleProvider + "]";
    }

    /* loaded from: classes.dex */
    public static class SpecAnimationBridge implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
        public MagnificationAnimationCallback mAnimationCallback;
        public final ControllerContext mControllerCtx;
        public final int mDisplayId;
        @GuardedBy({"mLock"})
        public boolean mEnabled;
        public final MagnificationSpec mEndMagnificationSpec;
        public final Object mLock;
        public final MagnificationSpec mSentMagnificationSpec;
        public final MagnificationSpec mStartMagnificationSpec;
        public final ValueAnimator mValueAnimator;

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }

        public SpecAnimationBridge(ControllerContext controllerContext, Object obj, int i) {
            this.mSentMagnificationSpec = new MagnificationSpec();
            this.mStartMagnificationSpec = new MagnificationSpec();
            this.mEndMagnificationSpec = new MagnificationSpec();
            this.mEnabled = false;
            this.mControllerCtx = controllerContext;
            this.mLock = obj;
            this.mDisplayId = i;
            long animationDuration = controllerContext.getAnimationDuration();
            ValueAnimator newValueAnimator = controllerContext.newValueAnimator();
            this.mValueAnimator = newValueAnimator;
            newValueAnimator.setDuration(animationDuration);
            newValueAnimator.setInterpolator(new DecelerateInterpolator(2.5f));
            newValueAnimator.setFloatValues(0.0f, 1.0f);
            newValueAnimator.addUpdateListener(this);
            newValueAnimator.addListener(this);
        }

        public void setEnabled(boolean z) {
            synchronized (this.mLock) {
                if (z != this.mEnabled) {
                    this.mEnabled = z;
                    if (!z) {
                        this.mSentMagnificationSpec.clear();
                        if (this.mControllerCtx.getTraceManager().isA11yTracingEnabledForTypes(512L)) {
                            AccessibilityTraceManager traceManager = this.mControllerCtx.getTraceManager();
                            traceManager.logTrace("WindowManagerInternal.setMagnificationSpec", 512L, "displayID=" + this.mDisplayId + ";spec=" + this.mSentMagnificationSpec);
                        }
                        this.mControllerCtx.getWindowManager().setMagnificationSpec(this.mDisplayId, this.mSentMagnificationSpec);
                    }
                }
            }
        }

        public void updateSentSpecMainThread(MagnificationSpec magnificationSpec, MagnificationAnimationCallback magnificationAnimationCallback) {
            if (this.mValueAnimator.isRunning()) {
                this.mValueAnimator.cancel();
            }
            this.mAnimationCallback = magnificationAnimationCallback;
            synchronized (this.mLock) {
                if (!this.mSentMagnificationSpec.equals(magnificationSpec)) {
                    if (this.mAnimationCallback != null) {
                        animateMagnificationSpecLocked(magnificationSpec);
                    } else {
                        setMagnificationSpecLocked(magnificationSpec);
                    }
                } else {
                    sendEndCallbackMainThread(true);
                }
            }
        }

        public final void sendEndCallbackMainThread(boolean z) {
            MagnificationAnimationCallback magnificationAnimationCallback = this.mAnimationCallback;
            if (magnificationAnimationCallback != null) {
                magnificationAnimationCallback.onResult(z);
                this.mAnimationCallback = null;
            }
        }

        @GuardedBy({"mLock"})
        public final void setMagnificationSpecLocked(MagnificationSpec magnificationSpec) {
            if (this.mEnabled) {
                this.mSentMagnificationSpec.setTo(magnificationSpec);
                if (this.mControllerCtx.getTraceManager().isA11yTracingEnabledForTypes(512L)) {
                    AccessibilityTraceManager traceManager = this.mControllerCtx.getTraceManager();
                    traceManager.logTrace("WindowManagerInternal.setMagnificationSpec", 512L, "displayID=" + this.mDisplayId + ";spec=" + this.mSentMagnificationSpec);
                }
                this.mControllerCtx.getWindowManager().setMagnificationSpec(this.mDisplayId, this.mSentMagnificationSpec);
            }
        }

        public final void animateMagnificationSpecLocked(MagnificationSpec magnificationSpec) {
            this.mEndMagnificationSpec.setTo(magnificationSpec);
            this.mStartMagnificationSpec.setTo(this.mSentMagnificationSpec);
            this.mValueAnimator.start();
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            synchronized (this.mLock) {
                if (this.mEnabled) {
                    float animatedFraction = valueAnimator.getAnimatedFraction();
                    MagnificationSpec magnificationSpec = new MagnificationSpec();
                    MagnificationSpec magnificationSpec2 = this.mStartMagnificationSpec;
                    float f = magnificationSpec2.scale;
                    MagnificationSpec magnificationSpec3 = this.mEndMagnificationSpec;
                    magnificationSpec.scale = f + ((magnificationSpec3.scale - f) * animatedFraction);
                    float f2 = magnificationSpec2.offsetX;
                    magnificationSpec.offsetX = f2 + ((magnificationSpec3.offsetX - f2) * animatedFraction);
                    float f3 = magnificationSpec2.offsetY;
                    magnificationSpec.offsetY = f3 + ((magnificationSpec3.offsetY - f3) * animatedFraction);
                    setMagnificationSpecLocked(magnificationSpec);
                }
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            sendEndCallbackMainThread(true);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            sendEndCallbackMainThread(false);
        }
    }

    /* loaded from: classes.dex */
    public static class ScreenStateObserver extends BroadcastReceiver {
        public final Context mContext;
        public final FullScreenMagnificationController mController;
        public boolean mRegistered = false;

        public ScreenStateObserver(Context context, FullScreenMagnificationController fullScreenMagnificationController) {
            this.mContext = context;
            this.mController = fullScreenMagnificationController;
        }

        public void registerIfNecessary() {
            if (this.mRegistered) {
                return;
            }
            this.mContext.registerReceiver(this, new IntentFilter("android.intent.action.SCREEN_OFF"));
            this.mRegistered = true;
        }

        public void unregister() {
            if (this.mRegistered) {
                this.mContext.unregisterReceiver(this);
                this.mRegistered = false;
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            this.mController.onScreenTurnedOff();
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class ControllerContext {
        public final Long mAnimationDuration;
        public final Context mContext;
        public final Handler mHandler;
        public final AccessibilityTraceManager mTrace;
        public final WindowManagerInternal mWindowManager;

        public ControllerContext(Context context, AccessibilityTraceManager accessibilityTraceManager, WindowManagerInternal windowManagerInternal, Handler handler, long j) {
            this.mContext = context;
            this.mTrace = accessibilityTraceManager;
            this.mWindowManager = windowManagerInternal;
            this.mHandler = handler;
            this.mAnimationDuration = Long.valueOf(j);
        }

        public Context getContext() {
            return this.mContext;
        }

        public AccessibilityTraceManager getTraceManager() {
            return this.mTrace;
        }

        public WindowManagerInternal getWindowManager() {
            return this.mWindowManager;
        }

        public Handler getHandler() {
            return this.mHandler;
        }

        public ValueAnimator newValueAnimator() {
            return new ValueAnimator();
        }

        public long getAnimationDuration() {
            return this.mAnimationDuration.longValue();
        }
    }

    public static MagnificationAnimationCallback transformToStubCallback(boolean z) {
        if (z) {
            return MagnificationAnimationCallback.STUB_ANIMATION_CALLBACK;
        }
        return null;
    }
}
