package com.android.server.p014wm;

import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.util.proto.ProtoOutputStream;
import android.view.DisplayInfo;
import android.view.InsetsState;
import android.view.SurfaceControl;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.WindowToken */
/* loaded from: classes2.dex */
public class WindowToken extends WindowContainer<WindowState> {
    public boolean mClientVisible;
    public SurfaceControl mFixedRotationTransformLeash;
    public FixedRotationTransformState mFixedRotationTransformState;
    public final boolean mFromClientToken;
    public final Bundle mOptions;
    public final boolean mOwnerCanManageAppTokens;
    public boolean mPersistOnEmpty;
    public final boolean mRoundedCornerOverlay;
    public final Comparator<WindowState> mWindowComparator;
    public boolean paused;
    public String stringName;
    public final IBinder token;
    public boolean waitingToShow;
    public final int windowType;

    @Override // com.android.server.p014wm.WindowContainer
    public WindowToken asWindowToken() {
        return this;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public long getProtoFieldId() {
        return 1146756268039L;
    }

    public boolean hasSizeCompatBounds() {
        return false;
    }

    public void onCancelFixedRotationTransform(int i) {
    }

    /* renamed from: com.android.server.wm.WindowToken$FixedRotationTransformState */
    /* loaded from: classes2.dex */
    public static class FixedRotationTransformState {
        public final DisplayFrames mDisplayFrames;
        public final DisplayInfo mDisplayInfo;
        public final Configuration mRotatedOverrideConfiguration;
        public final SeamlessRotator mRotator;
        public final ArrayList<WindowToken> mAssociatedTokens = new ArrayList<>(3);
        public final ArrayList<WindowContainer<?>> mRotatedContainers = new ArrayList<>(3);
        public boolean mIsTransforming = true;

        public FixedRotationTransformState(DisplayInfo displayInfo, DisplayFrames displayFrames, Configuration configuration, int i) {
            this.mDisplayInfo = displayInfo;
            this.mDisplayFrames = displayFrames;
            this.mRotatedOverrideConfiguration = configuration;
            this.mRotator = new SeamlessRotator(displayInfo.rotation, i, displayInfo, true);
        }

        public void transform(WindowContainer<?> windowContainer) {
            this.mRotator.unrotate(windowContainer.getPendingTransaction(), windowContainer);
            if (this.mRotatedContainers.contains(windowContainer)) {
                return;
            }
            this.mRotatedContainers.add(windowContainer);
        }

        public void resetTransform() {
            for (int size = this.mRotatedContainers.size() - 1; size >= 0; size--) {
                WindowContainer<?> windowContainer = this.mRotatedContainers.get(size);
                if (windowContainer.getParent() != null) {
                    this.mRotator.finish(windowContainer.getPendingTransaction(), windowContainer);
                }
            }
        }

        public void disassociate(WindowToken windowToken) {
            this.mAssociatedTokens.remove(windowToken);
            this.mRotatedContainers.remove(windowToken);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$new$0(WindowState windowState, WindowState windowState2) {
        if (windowState.mToken != this) {
            throw new IllegalArgumentException("newWindow=" + windowState + " is not a child of token=" + this);
        } else if (windowState2.mToken == this) {
            return isFirstChildWindowGreaterThanSecond(windowState, windowState2) ? 1 : -1;
        } else {
            throw new IllegalArgumentException("existingWindow=" + windowState2 + " is not a child of token=" + this);
        }
    }

    public WindowToken(WindowManagerService windowManagerService, IBinder iBinder, int i, boolean z, DisplayContent displayContent, boolean z2) {
        this(windowManagerService, iBinder, i, z, displayContent, z2, false, false, null);
    }

    public WindowToken(WindowManagerService windowManagerService, IBinder iBinder, int i, boolean z, DisplayContent displayContent, boolean z2, boolean z3, boolean z4, Bundle bundle) {
        super(windowManagerService);
        this.paused = false;
        this.mWindowComparator = new Comparator() { // from class: com.android.server.wm.WindowToken$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$new$0;
                lambda$new$0 = WindowToken.this.lambda$new$0((WindowState) obj, (WindowState) obj2);
                return lambda$new$0;
            }
        };
        this.token = iBinder;
        this.windowType = i;
        this.mOptions = bundle;
        this.mPersistOnEmpty = z;
        this.mOwnerCanManageAppTokens = z2;
        this.mRoundedCornerOverlay = z3;
        this.mFromClientToken = z4;
        if (displayContent != null) {
            displayContent.addWindowToken(iBinder, this);
        }
    }

    public void removeAllWindowsIfPossible() {
        int size = this.mChildren.size() - 1;
        while (size >= 0) {
            WindowState windowState = (WindowState) this.mChildren.get(size);
            if (ProtoLogCache.WM_DEBUG_WINDOW_MOVEMENT_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_WINDOW_MOVEMENT, 913494177, 0, (String) null, new Object[]{String.valueOf(windowState)});
            }
            windowState.removeIfPossible();
            if (size > this.mChildren.size()) {
                size = this.mChildren.size();
            }
            size--;
        }
    }

    public void setExiting(boolean z) {
        if (isEmpty()) {
            super.removeImmediately();
            return;
        }
        this.mPersistOnEmpty = false;
        if (isVisible()) {
            int size = this.mChildren.size();
            boolean z2 = false;
            for (int i = 0; i < size; i++) {
                z2 |= ((WindowState) this.mChildren.get(i)).onSetAppExiting(z);
            }
            if (z2) {
                this.mWmService.mWindowPlacerLocked.performSurfacePlacement();
                this.mWmService.updateFocusedWindowLocked(0, false);
            }
        }
    }

    public float getCompatScale() {
        return this.mDisplayContent.mCompatibleScreenScale;
    }

    public boolean isFirstChildWindowGreaterThanSecond(WindowState windowState, WindowState windowState2) {
        return windowState.mBaseLayer >= windowState2.mBaseLayer;
    }

    public void addWindow(WindowState windowState) {
        if (ProtoLogCache.WM_DEBUG_FOCUS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_FOCUS, 1219600119, 0, (String) null, new Object[]{String.valueOf(windowState), String.valueOf(Debug.getCallers(5))});
        }
        if (windowState.isChildWindow()) {
            return;
        }
        if (this.mSurfaceControl == null) {
            createSurfaceControl(true);
            reassignLayer(getSyncTransaction());
        }
        if (this.mChildren.contains(windowState)) {
            return;
        }
        if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, 241961619, 0, (String) null, new Object[]{String.valueOf(windowState), String.valueOf(this)});
        }
        addChild((WindowToken) windowState, (Comparator<WindowToken>) this.mWindowComparator);
        this.mWmService.mWindowsChanged = true;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void createSurfaceControl(boolean z) {
        if (!this.mFromClientToken || z) {
            super.createSurfaceControl(z);
        }
    }

    public boolean isEmpty() {
        return this.mChildren.isEmpty();
    }

    public WindowState getReplacingWindow() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            WindowState replacingWindow = ((WindowState) this.mChildren.get(size)).getReplacingWindow();
            if (replacingWindow != null) {
                return replacingWindow;
            }
        }
        return null;
    }

    public boolean windowsCanBeWallpaperTarget() {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            if (((WindowState) this.mChildren.get(size)).hasWallpaper()) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void removeImmediately() {
        DisplayContent displayContent = this.mDisplayContent;
        if (displayContent != null) {
            displayContent.removeWindowToken(this.token, true);
        }
        super.removeImmediately();
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void onDisplayChanged(DisplayContent displayContent) {
        displayContent.reParentWindowToken(this);
        super.onDisplayChanged(displayContent);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void assignLayer(SurfaceControl.Transaction transaction, int i) {
        if (this.mRoundedCornerOverlay) {
            super.assignLayer(transaction, 1073741826);
        } else {
            super.assignLayer(transaction, i);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public SurfaceControl.Builder makeSurface() {
        SurfaceControl.Builder makeSurface = super.makeSurface();
        if (this.mRoundedCornerOverlay) {
            makeSurface.setParent(null);
        }
        return makeSurface;
    }

    public boolean isClientVisible() {
        return this.mClientVisible;
    }

    public void setClientVisible(boolean z) {
        if (this.mClientVisible == z) {
            return;
        }
        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS, -2093859262, 12, (String) null, new Object[]{String.valueOf(this), Boolean.valueOf(z), String.valueOf(Debug.getCallers(5))});
        }
        this.mClientVisible = z;
        sendAppVisibilityToClients();
    }

    public boolean hasFixedRotationTransform() {
        return this.mFixedRotationTransformState != null;
    }

    public boolean hasFixedRotationTransform(WindowToken windowToken) {
        FixedRotationTransformState fixedRotationTransformState = this.mFixedRotationTransformState;
        if (fixedRotationTransformState == null || windowToken == null) {
            return false;
        }
        return this == windowToken || fixedRotationTransformState == windowToken.mFixedRotationTransformState;
    }

    public boolean isFinishingFixedRotationTransform() {
        FixedRotationTransformState fixedRotationTransformState = this.mFixedRotationTransformState;
        return (fixedRotationTransformState == null || fixedRotationTransformState.mIsTransforming) ? false : true;
    }

    public boolean isFixedRotationTransforming() {
        FixedRotationTransformState fixedRotationTransformState = this.mFixedRotationTransformState;
        return fixedRotationTransformState != null && fixedRotationTransformState.mIsTransforming;
    }

    public DisplayInfo getFixedRotationTransformDisplayInfo() {
        if (isFixedRotationTransforming()) {
            return this.mFixedRotationTransformState.mDisplayInfo;
        }
        return null;
    }

    public DisplayFrames getFixedRotationTransformDisplayFrames() {
        if (isFixedRotationTransforming()) {
            return this.mFixedRotationTransformState.mDisplayFrames;
        }
        return null;
    }

    public Rect getFixedRotationTransformMaxBounds() {
        if (isFixedRotationTransforming()) {
            return this.mFixedRotationTransformState.mRotatedOverrideConfiguration.windowConfiguration.getMaxBounds();
        }
        return null;
    }

    public Rect getFixedRotationTransformDisplayBounds() {
        if (isFixedRotationTransforming()) {
            return this.mFixedRotationTransformState.mRotatedOverrideConfiguration.windowConfiguration.getBounds();
        }
        return null;
    }

    public InsetsState getFixedRotationTransformInsetsState() {
        if (isFixedRotationTransforming()) {
            return this.mFixedRotationTransformState.mDisplayFrames.mInsetsState;
        }
        return null;
    }

    public void applyFixedRotationTransform(DisplayInfo displayInfo, DisplayFrames displayFrames, Configuration configuration) {
        FixedRotationTransformState fixedRotationTransformState = this.mFixedRotationTransformState;
        if (fixedRotationTransformState != null) {
            fixedRotationTransformState.disassociate(this);
        }
        FixedRotationTransformState fixedRotationTransformState2 = new FixedRotationTransformState(displayInfo, displayFrames, new Configuration(configuration), this.mDisplayContent.getRotation());
        this.mFixedRotationTransformState = fixedRotationTransformState2;
        fixedRotationTransformState2.mAssociatedTokens.add(this);
        this.mDisplayContent.getDisplayPolicy().simulateLayoutDisplay(displayFrames);
        onFixedRotationStatePrepared();
    }

    public void linkFixedRotationTransform(WindowToken windowToken) {
        FixedRotationTransformState fixedRotationTransformState;
        FixedRotationTransformState fixedRotationTransformState2 = windowToken.mFixedRotationTransformState;
        if (fixedRotationTransformState2 == null || (fixedRotationTransformState = this.mFixedRotationTransformState) == fixedRotationTransformState2) {
            return;
        }
        if (fixedRotationTransformState != null) {
            fixedRotationTransformState.disassociate(this);
        }
        this.mFixedRotationTransformState = fixedRotationTransformState2;
        fixedRotationTransformState2.mAssociatedTokens.add(this);
        onFixedRotationStatePrepared();
    }

    public final void onFixedRotationStatePrepared() {
        onConfigurationChanged(getParent().getConfiguration());
        ActivityRecord asActivityRecord = asActivityRecord();
        if (asActivityRecord == null || !asActivityRecord.hasProcess()) {
            return;
        }
        asActivityRecord.app.registerActivityConfigurationListener(asActivityRecord);
    }

    public boolean hasAnimatingFixedRotationTransition() {
        FixedRotationTransformState fixedRotationTransformState = this.mFixedRotationTransformState;
        if (fixedRotationTransformState == null) {
            return false;
        }
        for (int size = fixedRotationTransformState.mAssociatedTokens.size() - 1; size >= 0; size--) {
            ActivityRecord asActivityRecord = this.mFixedRotationTransformState.mAssociatedTokens.get(size).asActivityRecord();
            if (asActivityRecord != null && asActivityRecord.isInTransition()) {
                return true;
            }
        }
        return false;
    }

    public void finishFixedRotationTransform() {
        finishFixedRotationTransform(null);
    }

    public void finishFixedRotationTransform(Runnable runnable) {
        FixedRotationTransformState fixedRotationTransformState = this.mFixedRotationTransformState;
        if (fixedRotationTransformState == null) {
            return;
        }
        if (this.mTransitionController.isShellTransitionsEnabled()) {
            for (int size = fixedRotationTransformState.mAssociatedTokens.size() - 1; size >= 0; size--) {
                fixedRotationTransformState.mAssociatedTokens.get(size).removeFixedRotationLeash();
            }
        } else {
            fixedRotationTransformState.resetTransform();
        }
        fixedRotationTransformState.mIsTransforming = false;
        if (runnable != null) {
            runnable.run();
        }
        for (int size2 = fixedRotationTransformState.mAssociatedTokens.size() - 1; size2 >= 0; size2--) {
            WindowToken windowToken = fixedRotationTransformState.mAssociatedTokens.get(size2);
            windowToken.mFixedRotationTransformState = null;
            if (runnable == null) {
                windowToken.cancelFixedRotationTransform();
            }
        }
    }

    public final void cancelFixedRotationTransform() {
        WindowContainer parent = getParent();
        if (parent == null) {
            return;
        }
        if (this.mTransitionController.isShellTransitionsEnabled() && asActivityRecord() != null && isVisible()) {
            this.mTransitionController.requestTransitionIfNeeded(6, this);
            this.mTransitionController.setReady(this);
        }
        int rotation = getWindowConfiguration().getRotation();
        onConfigurationChanged(parent.getConfiguration());
        onCancelFixedRotationTransform(rotation);
    }

    public SurfaceControl getOrCreateFixedRotationLeash(SurfaceControl.Transaction transaction) {
        if (this.mTransitionController.isShellTransitionsEnabled()) {
            int relativeDisplayRotation = getRelativeDisplayRotation();
            if (relativeDisplayRotation == 0) {
                return this.mFixedRotationTransformLeash;
            }
            SurfaceControl surfaceControl = this.mFixedRotationTransformLeash;
            if (surfaceControl != null) {
                return surfaceControl;
            }
            SurfaceControl.Builder parent = makeSurface().setContainerLayer().setParent(getParentSurfaceControl());
            SurfaceControl build = parent.setName(getSurfaceControl() + " - rotation-leash").setHidden(false).setCallsite("WindowToken.getOrCreateFixedRotationLeash").build();
            Point point = this.mLastSurfacePosition;
            transaction.setPosition(build, (float) point.x, (float) point.y);
            transaction.reparent(getSurfaceControl(), build);
            this.mFixedRotationTransformLeash = build;
            updateSurfaceRotation(transaction, relativeDisplayRotation, build);
            return this.mFixedRotationTransformLeash;
        }
        return null;
    }

    public SurfaceControl getFixedRotationLeash() {
        return this.mFixedRotationTransformLeash;
    }

    public void removeFixedRotationLeash() {
        if (this.mFixedRotationTransformLeash == null) {
            return;
        }
        SurfaceControl.Transaction syncTransaction = getSyncTransaction();
        syncTransaction.reparent(getSurfaceControl(), getParentSurfaceControl());
        syncTransaction.remove(this.mFixedRotationTransformLeash);
        this.mFixedRotationTransformLeash = null;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public void resolveOverrideConfiguration(Configuration configuration) {
        super.resolveOverrideConfiguration(configuration);
        if (isFixedRotationTransforming()) {
            getResolvedOverrideConfiguration().updateFrom(this.mFixedRotationTransformState.mRotatedOverrideConfiguration);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void updateSurfacePosition(SurfaceControl.Transaction transaction) {
        super.updateSurfacePosition(transaction);
        if (this.mTransitionController.isShellTransitionsEnabled() || !isFixedRotationTransforming()) {
            return;
        }
        ActivityRecord asActivityRecord = asActivityRecord();
        Task rootTask = asActivityRecord != null ? asActivityRecord.getRootTask() : null;
        if (rootTask == null || !rootTask.inPinnedWindowingMode()) {
            this.mFixedRotationTransformState.transform(this);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void updateSurfaceRotation(SurfaceControl.Transaction transaction, int i, SurfaceControl surfaceControl) {
        Task rootTask;
        ActivityRecord asActivityRecord = asActivityRecord();
        if (asActivityRecord == null || (rootTask = asActivityRecord.getRootTask()) == null || this.mTransitionController.getWindowingModeAtStart(rootTask) != 2) {
            super.updateSurfaceRotation(transaction, i, surfaceControl);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void resetSurfacePositionForAnimationLeash(SurfaceControl.Transaction transaction) {
        if (isFixedRotationTransforming()) {
            return;
        }
        super.resetSurfacePositionForAnimationLeash(transaction);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean prepareSync() {
        DisplayContent displayContent = this.mDisplayContent;
        if (displayContent != null && displayContent.isRotationChanging() && AsyncRotationController.canBeAsync(this)) {
            return false;
        }
        return super.prepareSync();
    }

    @Override // com.android.server.p014wm.WindowContainer, com.android.server.p014wm.ConfigurationContainer
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j, int i) {
        if (i != 2 || isVisible()) {
            long start = protoOutputStream.start(j);
            super.dumpDebug(protoOutputStream, 1146756268033L, i);
            protoOutputStream.write(1120986464258L, System.identityHashCode(this));
            protoOutputStream.write(1133871366149L, this.waitingToShow);
            protoOutputStream.write(1133871366150L, this.paused);
            protoOutputStream.end(start);
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void dump(PrintWriter printWriter, String str, boolean z) {
        super.dump(printWriter, str, z);
        printWriter.print(str);
        printWriter.print("windows=");
        printWriter.println(this.mChildren);
        printWriter.print(str);
        printWriter.print("windowType=");
        printWriter.print(this.windowType);
        if (this.waitingToShow) {
            printWriter.print(" waitingToShow=true");
        }
        printWriter.println();
        if (hasFixedRotationTransform()) {
            printWriter.print(str);
            printWriter.print("fixedRotationConfig=");
            printWriter.println(this.mFixedRotationTransformState.mRotatedOverrideConfiguration);
        }
    }

    public String toString() {
        if (this.stringName == null) {
            this.stringName = "WindowToken{" + Integer.toHexString(System.identityHashCode(this)) + " type=" + this.windowType + " " + this.token + "}";
        }
        return this.stringName;
    }

    @Override // com.android.server.p014wm.ConfigurationContainer
    public String getName() {
        return toString();
    }

    public int getWindowLayerFromType() {
        return this.mWmService.mPolicy.getWindowLayerFromTypeLw(this.windowType, this.mOwnerCanManageAppTokens, this.mRoundedCornerOverlay);
    }

    public boolean isFromClient() {
        return this.mFromClientToken;
    }

    public void setInsetsFrozen(final boolean z) {
        forAllWindows(new Consumer() { // from class: com.android.server.wm.WindowToken$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WindowToken.this.lambda$setInsetsFrozen$1(z, (WindowState) obj);
            }
        }, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setInsetsFrozen$1(boolean z, WindowState windowState) {
        if (windowState.mToken == this) {
            if (z) {
                windowState.freezeInsetsState();
            } else {
                windowState.clearFrozenInsetsState();
            }
        }
    }

    @Override // com.android.server.p014wm.WindowContainer
    public int getWindowType() {
        return this.windowType;
    }

    /* renamed from: com.android.server.wm.WindowToken$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        public DisplayContent mDisplayContent;
        public boolean mFromClientToken;
        public Bundle mOptions;
        public boolean mOwnerCanManageAppTokens;
        public boolean mPersistOnEmpty;
        public boolean mRoundedCornerOverlay;
        public final WindowManagerService mService;
        public final IBinder mToken;
        public final int mType;

        public Builder(WindowManagerService windowManagerService, IBinder iBinder, int i) {
            this.mService = windowManagerService;
            this.mToken = iBinder;
            this.mType = i;
        }

        public Builder setPersistOnEmpty(boolean z) {
            this.mPersistOnEmpty = z;
            return this;
        }

        public Builder setDisplayContent(DisplayContent displayContent) {
            this.mDisplayContent = displayContent;
            return this;
        }

        public Builder setOwnerCanManageAppTokens(boolean z) {
            this.mOwnerCanManageAppTokens = z;
            return this;
        }

        public Builder setRoundedCornerOverlay(boolean z) {
            this.mRoundedCornerOverlay = z;
            return this;
        }

        public Builder setFromClientToken(boolean z) {
            this.mFromClientToken = z;
            return this;
        }

        public Builder setOptions(Bundle bundle) {
            this.mOptions = bundle;
            return this;
        }

        public WindowToken build() {
            return new WindowToken(this.mService, this.mToken, this.mType, this.mPersistOnEmpty, this.mDisplayContent, this.mOwnerCanManageAppTokens, this.mRoundedCornerOverlay, this.mFromClientToken, this.mOptions);
        }
    }
}
