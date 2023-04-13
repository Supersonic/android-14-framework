package com.android.server.p014wm;

import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.projection.IMediaProjectionManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.DeviceConfig;
import android.view.ContentRecordingSession;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
/* renamed from: com.android.server.wm.ContentRecorder */
/* loaded from: classes2.dex */
public final class ContentRecorder implements WindowContainerListener {
    @VisibleForTesting
    static final String KEY_RECORD_TASK_FEATURE = "record_task_content";
    public ContentRecordingSession mContentRecordingSession;
    public final DisplayContent mDisplayContent;
    public int mLastOrientation;
    public Rect mLastRecordedBounds;
    public final MediaProjectionManagerWrapper mMediaProjectionManager;
    public SurfaceControl mRecordedSurface;
    public WindowContainer mRecordedWindowContainer;

    @VisibleForTesting
    /* renamed from: com.android.server.wm.ContentRecorder$MediaProjectionManagerWrapper */
    /* loaded from: classes2.dex */
    public interface MediaProjectionManagerWrapper {
        void notifyActiveProjectionCapturedContentResized(int i, int i2);

        void notifyActiveProjectionCapturedContentVisibilityChanged(boolean z);

        void stopActiveProjection();
    }

    public ContentRecorder(DisplayContent displayContent) {
        this(displayContent, new RemoteMediaProjectionManagerWrapper());
    }

    @VisibleForTesting
    public ContentRecorder(DisplayContent displayContent, MediaProjectionManagerWrapper mediaProjectionManagerWrapper) {
        this.mContentRecordingSession = null;
        this.mRecordedWindowContainer = null;
        this.mRecordedSurface = null;
        this.mLastRecordedBounds = null;
        this.mLastOrientation = 0;
        this.mDisplayContent = displayContent;
        this.mMediaProjectionManager = mediaProjectionManagerWrapper;
    }

    public void setContentRecordingSession(ContentRecordingSession contentRecordingSession) {
        this.mContentRecordingSession = contentRecordingSession;
    }

    public boolean isContentRecordingSessionSet() {
        return this.mContentRecordingSession != null;
    }

    public boolean isCurrentlyRecording() {
        return (this.mContentRecordingSession == null || this.mRecordedSurface == null) ? false : true;
    }

    @VisibleForTesting
    public void updateRecording() {
        if (isCurrentlyRecording() && (this.mDisplayContent.getLastHasContent() || this.mDisplayContent.getDisplay().getState() == 1)) {
            pauseRecording();
        } else {
            startRecordingIfNeeded();
        }
    }

    public void onConfigurationChanged(@Configuration.Orientation int i) {
        if (!isCurrentlyRecording() || this.mLastRecordedBounds == null) {
            return;
        }
        if (this.mRecordedWindowContainer == null) {
            if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, 1444064727, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
                return;
            }
            return;
        }
        if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -302468137, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
        }
        Rect bounds = this.mRecordedWindowContainer.getBounds();
        int orientation = this.mRecordedWindowContainer.getOrientation();
        if (this.mLastRecordedBounds.equals(bounds) && i == orientation) {
            return;
        }
        Point fetchSurfaceSizeIfPresent = fetchSurfaceSizeIfPresent();
        if (fetchSurfaceSizeIfPresent != null) {
            if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -1373875178, 17, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId()), String.valueOf(bounds), Long.valueOf(orientation)});
            }
            updateMirroredSurface(this.mDisplayContent.mWmService.mTransactionFactory.get(), bounds, fetchSurfaceSizeIfPresent);
        } else if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -751255162, 17, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId()), String.valueOf(bounds), Long.valueOf(orientation)});
        }
    }

    public void pauseRecording() {
        if (this.mRecordedSurface == null) {
            return;
        }
        if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -1781861035, 13, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId()), Boolean.valueOf(this.mDisplayContent.getLastHasContent())});
        }
        this.mDisplayContent.mWmService.mTransactionFactory.get().remove(this.mRecordedSurface).reparent(this.mDisplayContent.getWindowingLayer(), this.mDisplayContent.getSurfaceControl()).reparent(this.mDisplayContent.getOverlayLayer(), this.mDisplayContent.getSurfaceControl()).apply();
        this.mRecordedSurface = null;
    }

    public void stopRecording() {
        unregisterListener();
        if (this.mRecordedSurface != null) {
            this.mDisplayContent.mWmService.mTransactionFactory.get().remove(this.mRecordedSurface).apply();
            this.mRecordedSurface = null;
            clearContentRecordingSession();
        }
    }

    public final void stopMediaProjection() {
        if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, 96494268, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
        }
        MediaProjectionManagerWrapper mediaProjectionManagerWrapper = this.mMediaProjectionManager;
        if (mediaProjectionManagerWrapper != null) {
            mediaProjectionManagerWrapper.stopActiveProjection();
        }
    }

    public final void clearContentRecordingSession() {
        this.mContentRecordingSession = null;
        WindowManagerService windowManagerService = this.mDisplayContent.mWmService;
        windowManagerService.mContentRecordingController.setContentRecordingSessionLocked(null, windowManagerService);
    }

    public final void unregisterListener() {
        WindowContainer windowContainer = this.mRecordedWindowContainer;
        Task asTask = windowContainer != null ? windowContainer.asTask() : null;
        if (asTask == null || !isRecordingContentTask()) {
            return;
        }
        asTask.unregisterWindowContainerListener(this);
        this.mRecordedWindowContainer = null;
    }

    public final void startRecordingIfNeeded() {
        if (this.mDisplayContent.getLastHasContent() || isCurrentlyRecording()) {
            return;
        }
        if (this.mDisplayContent.getDisplay().getState() == 1 || this.mContentRecordingSession == null) {
            return;
        }
        WindowContainer retrieveRecordedWindowContainer = retrieveRecordedWindowContainer();
        this.mRecordedWindowContainer = retrieveRecordedWindowContainer;
        if (retrieveRecordedWindowContainer == null) {
            return;
        }
        Point fetchSurfaceSizeIfPresent = fetchSurfaceSizeIfPresent();
        if (fetchSurfaceSizeIfPresent == null) {
            if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -142844021, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
                return;
            }
            return;
        }
        if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, 609880497, 5, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId()), Long.valueOf(this.mDisplayContent.getDisplay().getState())});
        }
        this.mRecordedSurface = SurfaceControl.mirrorSurface(this.mRecordedWindowContainer.getSurfaceControl());
        updateMirroredSurface(this.mDisplayContent.mWmService.mTransactionFactory.get().reparent(this.mRecordedSurface, this.mDisplayContent.getSurfaceControl()).reparent(this.mDisplayContent.getWindowingLayer(), null).reparent(this.mDisplayContent.getOverlayLayer(), null), this.mRecordedWindowContainer.getBounds(), fetchSurfaceSizeIfPresent);
        if (this.mContentRecordingSession.getContentToRecord() == 1) {
            this.mMediaProjectionManager.notifyActiveProjectionCapturedContentVisibilityChanged(this.mRecordedWindowContainer.asTask().isVisibleRequested());
            return;
        }
        this.mMediaProjectionManager.notifyActiveProjectionCapturedContentVisibilityChanged(this.mRecordedWindowContainer.asDisplayContent().getDisplay().getState() != 1);
    }

    public final WindowContainer retrieveRecordedWindowContainer() {
        int contentToRecord = this.mContentRecordingSession.getContentToRecord();
        IBinder tokenToRecord = this.mContentRecordingSession.getTokenToRecord();
        if (tokenToRecord == null) {
            handleStartRecordingFailed();
            if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -1605829532, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
            }
            return null;
        } else if (contentToRecord == 0) {
            WindowContainer<?> container = this.mDisplayContent.mWmService.mWindowContextListenerController.getContainer(tokenToRecord);
            if (container == null) {
                DisplayContent displayContent = this.mDisplayContent;
                displayContent.mWmService.mDisplayManagerInternal.setWindowManagerMirroring(displayContent.getDisplayId(), false);
                handleStartRecordingFailed();
                if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -732715767, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
                }
                return null;
            }
            return container.getDisplayContent();
        } else if (contentToRecord == 1) {
            if (!DeviceConfig.getBoolean("window_manager", KEY_RECORD_TASK_FEATURE, false)) {
                handleStartRecordingFailed();
                if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, 778774915, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
                }
                return null;
            }
            Task asTask = WindowContainer.fromBinder(tokenToRecord).asTask();
            if (asTask == null) {
                handleStartRecordingFailed();
                if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, 264036181, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
                }
            } else {
                asTask.registerWindowContainerListener(this);
            }
            return asTask;
        } else {
            handleStartRecordingFailed();
            if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, 1608402305, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
            }
            return null;
        }
    }

    public final void handleStartRecordingFailed() {
        boolean isRecordingContentTask = isRecordingContentTask();
        unregisterListener();
        clearContentRecordingSession();
        if (isRecordingContentTask) {
            stopMediaProjection();
        }
    }

    @VisibleForTesting
    public void updateMirroredSurface(SurfaceControl.Transaction transaction, Rect rect, Point point) {
        float min = Math.min(point.x / rect.width(), point.y / rect.height());
        int round = Math.round(rect.width() * min);
        int round2 = Math.round(rect.height() * min);
        int i = point.x;
        int i2 = round != i ? (i - round) / 2 : 0;
        int i3 = point.y;
        transaction.setWindowCrop(this.mRecordedSurface, rect.width(), rect.height()).setMatrix(this.mRecordedSurface, min, 0.0f, 0.0f, min).setPosition(this.mRecordedSurface, i2, round2 != i3 ? (i3 - round2) / 2 : 0).apply();
        Rect rect2 = new Rect(rect);
        this.mLastRecordedBounds = rect2;
        this.mMediaProjectionManager.notifyActiveProjectionCapturedContentResized(rect2.width(), this.mLastRecordedBounds.height());
    }

    public final Point fetchSurfaceSizeIfPresent() {
        DisplayContent displayContent = this.mDisplayContent;
        Point displaySurfaceDefaultSize = displayContent.mWmService.mDisplayManagerInternal.getDisplaySurfaceDefaultSize(displayContent.getDisplayId());
        if (displaySurfaceDefaultSize == null) {
            if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -1326876381, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
            }
            return null;
        }
        return displaySurfaceDefaultSize;
    }

    @Override // com.android.server.p014wm.WindowContainerListener
    public void onRemoved() {
        if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -1018968224, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.getDisplayId())});
        }
        unregisterListener();
        clearContentRecordingSession();
        stopMediaProjection();
    }

    @Override // com.android.server.p014wm.ConfigurationContainerListener
    public void onMergedOverrideConfigurationChanged(Configuration configuration) {
        super.onMergedOverrideConfigurationChanged(configuration);
        onConfigurationChanged(this.mLastOrientation);
        this.mLastOrientation = configuration.orientation;
    }

    @Override // com.android.server.p014wm.WindowContainerListener
    public void onVisibleRequestedChanged(boolean z) {
        if (!isCurrentlyRecording() || this.mLastRecordedBounds == null) {
            return;
        }
        this.mMediaProjectionManager.notifyActiveProjectionCapturedContentVisibilityChanged(z);
    }

    /* renamed from: com.android.server.wm.ContentRecorder$RemoteMediaProjectionManagerWrapper */
    /* loaded from: classes2.dex */
    public static final class RemoteMediaProjectionManagerWrapper implements MediaProjectionManagerWrapper {
        public IMediaProjectionManager mIMediaProjectionManager;

        public RemoteMediaProjectionManagerWrapper() {
            this.mIMediaProjectionManager = null;
        }

        @Override // com.android.server.p014wm.ContentRecorder.MediaProjectionManagerWrapper
        public void stopActiveProjection() {
            fetchMediaProjectionManager();
            IMediaProjectionManager iMediaProjectionManager = this.mIMediaProjectionManager;
            if (iMediaProjectionManager == null) {
                return;
            }
            try {
                iMediaProjectionManager.stopActiveProjection();
            } catch (RemoteException e) {
                if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                    ProtoLogImpl.e(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -1944652783, 0, (String) null, new Object[]{String.valueOf(e)});
                }
            }
        }

        @Override // com.android.server.p014wm.ContentRecorder.MediaProjectionManagerWrapper
        public void notifyActiveProjectionCapturedContentResized(int i, int i2) {
            fetchMediaProjectionManager();
            IMediaProjectionManager iMediaProjectionManager = this.mIMediaProjectionManager;
            if (iMediaProjectionManager == null) {
                return;
            }
            try {
                iMediaProjectionManager.notifyActiveProjectionCapturedContentResized(i, i2);
            } catch (RemoteException e) {
                if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                    ProtoLogImpl.e(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -1423223548, 0, (String) null, new Object[]{String.valueOf(e)});
                }
            }
        }

        @Override // com.android.server.p014wm.ContentRecorder.MediaProjectionManagerWrapper
        public void notifyActiveProjectionCapturedContentVisibilityChanged(boolean z) {
            fetchMediaProjectionManager();
            IMediaProjectionManager iMediaProjectionManager = this.mIMediaProjectionManager;
            if (iMediaProjectionManager == null) {
                return;
            }
            try {
                iMediaProjectionManager.notifyActiveProjectionCapturedContentVisibilityChanged(z);
            } catch (RemoteException e) {
                if (ProtoLogCache.WM_DEBUG_CONTENT_RECORDING_enabled) {
                    ProtoLogImpl.e(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING, -254406860, 0, (String) null, new Object[]{String.valueOf(e)});
                }
            }
        }

        public final void fetchMediaProjectionManager() {
            IBinder service;
            if (this.mIMediaProjectionManager == null && (service = ServiceManager.getService("media_projection")) != null) {
                this.mIMediaProjectionManager = IMediaProjectionManager.Stub.asInterface(service);
            }
        }
    }

    public final boolean isRecordingContentTask() {
        ContentRecordingSession contentRecordingSession = this.mContentRecordingSession;
        return contentRecordingSession != null && contentRecordingSession.getContentToRecord() == 1;
    }
}
