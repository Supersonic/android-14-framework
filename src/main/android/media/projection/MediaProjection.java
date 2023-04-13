package android.media.projection;

import android.app.compat.CompatChanges;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.hardware.display.VirtualDisplayConfig;
import android.media.projection.IMediaProjectionCallback;
import android.media.projection.IMediaProjectionManager;
import android.media.projection.MediaProjection;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.view.ContentRecordingSession;
import android.view.Surface;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class MediaProjection {
    static final long MEDIA_PROJECTION_REQUIRES_CALLBACK = 269849258;
    private static final String TAG = "MediaProjection";
    private final Map<Callback, CallbackRecord> mCallbacks;
    private final Context mContext;
    private final DisplayManager mDisplayManager;
    private final IMediaProjection mImpl;
    private final IMediaProjectionManager mProjectionService;

    public MediaProjection(Context context, IMediaProjection impl) {
        this(context, impl, IMediaProjectionManager.Stub.asInterface(ServiceManager.getService(Context.MEDIA_PROJECTION_SERVICE)), (DisplayManager) context.getSystemService(DisplayManager.class));
    }

    public MediaProjection(Context context, IMediaProjection impl, IMediaProjectionManager service, DisplayManager displayManager) {
        this.mCallbacks = new ArrayMap();
        this.mContext = context;
        this.mImpl = impl;
        try {
            impl.start(new MediaProjectionCallback());
            this.mProjectionService = service;
            this.mDisplayManager = displayManager;
        } catch (RemoteException e) {
            throw new RuntimeException("Failed to start media projection", e);
        }
    }

    public void registerCallback(Callback callback, Handler handler) {
        Callback c = (Callback) Objects.requireNonNull(callback);
        if (handler == null) {
            handler = new Handler();
        }
        this.mCallbacks.put(c, new CallbackRecord(c, handler));
    }

    public void unregisterCallback(Callback callback) {
        Callback c = (Callback) Objects.requireNonNull(callback);
        this.mCallbacks.remove(c);
    }

    public VirtualDisplay createVirtualDisplay(String name, int width, int height, int dpi, boolean isSecure, Surface surface, VirtualDisplay.Callback callback, Handler handler) {
        int flags = 18;
        if (isSecure) {
            flags = 18 | 4;
        }
        VirtualDisplayConfig.Builder builder = new VirtualDisplayConfig.Builder(name, width, height, dpi).setFlags(flags);
        if (surface != null) {
            builder.setSurface(surface);
        }
        return createVirtualDisplay(builder, callback, handler);
    }

    public VirtualDisplay createVirtualDisplay(String name, int width, int height, int dpi, int flags, Surface surface, VirtualDisplay.Callback callback, Handler handler) {
        if (shouldMediaProjectionRequireCallback() && this.mCallbacks.isEmpty()) {
            throw new IllegalStateException("Must register a callback before starting capture, to manage resources in response to MediaProjection states.");
        }
        VirtualDisplayConfig.Builder builder = new VirtualDisplayConfig.Builder(name, width, height, dpi).setFlags(flags);
        if (surface != null) {
            builder.setSurface(surface);
        }
        return createVirtualDisplay(builder, callback, handler);
    }

    public VirtualDisplay createVirtualDisplay(VirtualDisplayConfig.Builder virtualDisplayConfig, VirtualDisplay.Callback callback, Handler handler) {
        ContentRecordingSession session;
        try {
            IBinder launchCookie = this.mImpl.getLaunchCookie();
            Context windowContext = null;
            if (launchCookie == null) {
                Context context = this.mContext;
                windowContext = context.createWindowContext(context.getDisplayNoVerify(), 2, null);
                session = ContentRecordingSession.createDisplaySession(windowContext.getWindowContextToken());
            } else {
                session = ContentRecordingSession.createTaskSession(launchCookie);
            }
            virtualDisplayConfig.setContentRecordingSession(session);
            virtualDisplayConfig.setWindowManagerMirroringEnabled(true);
            VirtualDisplay virtualDisplay = this.mDisplayManager.createVirtualDisplay(this, virtualDisplayConfig.build(), callback, handler, windowContext);
            if (virtualDisplay == null) {
                Slog.m90w(TAG, "Failed to create virtual display.");
                return null;
            }
            return virtualDisplay;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private boolean shouldMediaProjectionRequireCallback() {
        return CompatChanges.isChangeEnabled(MEDIA_PROJECTION_REQUIRES_CALLBACK);
    }

    public void stop() {
        try {
            this.mImpl.stop();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Unable to stop projection", e);
        }
    }

    public IMediaProjection getProjection() {
        return this.mImpl;
    }

    /* loaded from: classes2.dex */
    public static abstract class Callback {
        public void onStop() {
        }

        public void onCapturedContentResize(int width, int height) {
        }

        public void onCapturedContentVisibilityChanged(boolean isVisible) {
        }
    }

    /* loaded from: classes2.dex */
    private final class MediaProjectionCallback extends IMediaProjectionCallback.Stub {
        private MediaProjectionCallback() {
        }

        @Override // android.media.projection.IMediaProjectionCallback
        public void onStop() {
            for (CallbackRecord cbr : MediaProjection.this.mCallbacks.values()) {
                cbr.onStop();
            }
        }

        @Override // android.media.projection.IMediaProjectionCallback
        public void onCapturedContentResize(int width, int height) {
            for (CallbackRecord cbr : MediaProjection.this.mCallbacks.values()) {
                cbr.onCapturedContentResize(width, height);
            }
        }

        @Override // android.media.projection.IMediaProjectionCallback
        public void onCapturedContentVisibilityChanged(boolean isVisible) {
            for (CallbackRecord cbr : MediaProjection.this.mCallbacks.values()) {
                cbr.onCapturedContentVisibilityChanged(isVisible);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class CallbackRecord extends Callback {
        private final Callback mCallback;
        private final Handler mHandler;

        public CallbackRecord(Callback callback, Handler handler) {
            this.mCallback = callback;
            this.mHandler = handler;
        }

        @Override // android.media.projection.MediaProjection.Callback
        public void onStop() {
            this.mHandler.post(new Runnable() { // from class: android.media.projection.MediaProjection.CallbackRecord.1
                @Override // java.lang.Runnable
                public void run() {
                    CallbackRecord.this.mCallback.onStop();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCapturedContentResize$0(int width, int height) {
            this.mCallback.onCapturedContentResize(width, height);
        }

        @Override // android.media.projection.MediaProjection.Callback
        public void onCapturedContentResize(final int width, final int height) {
            this.mHandler.post(new Runnable() { // from class: android.media.projection.MediaProjection$CallbackRecord$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MediaProjection.CallbackRecord.this.lambda$onCapturedContentResize$0(width, height);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCapturedContentVisibilityChanged$1(boolean isVisible) {
            this.mCallback.onCapturedContentVisibilityChanged(isVisible);
        }

        @Override // android.media.projection.MediaProjection.Callback
        public void onCapturedContentVisibilityChanged(final boolean isVisible) {
            this.mHandler.post(new Runnable() { // from class: android.media.projection.MediaProjection$CallbackRecord$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MediaProjection.CallbackRecord.this.lambda$onCapturedContentVisibilityChanged$1(isVisible);
                }
            });
        }
    }
}
