package com.android.server.media.projection;

import android.app.ActivityManagerInternal;
import android.app.AppOpsManager;
import android.app.IProcessObserver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaRouter;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionCallback;
import android.media.projection.IMediaProjectionManager;
import android.media.projection.IMediaProjectionWatcherCallback;
import android.media.projection.MediaProjectionInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.ContentRecordingSession;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.Watchdog;
import com.android.server.media.projection.MediaProjectionManagerService;
import com.android.server.p014wm.WindowManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;
/* loaded from: classes2.dex */
public final class MediaProjectionManagerService extends SystemService implements Watchdog.Monitor {
    public final ActivityManagerInternal mActivityManagerInternal;
    public final AppOpsManager mAppOps;
    public final CallbackDelegate mCallbackDelegate;
    public final Context mContext;
    public final Map<IBinder, IBinder.DeathRecipient> mDeathEaters;
    public final Object mLock;
    public MediaRouter.RouteInfo mMediaRouteInfo;
    public final MediaRouter mMediaRouter;
    public final MediaRouterCallback mMediaRouterCallback;
    public final PackageManager mPackageManager;
    public MediaProjection mProjectionGrant;
    public IBinder mProjectionToken;

    public MediaProjectionManagerService(Context context) {
        super(context);
        this.mLock = new Object();
        this.mContext = context;
        this.mDeathEaters = new ArrayMap();
        this.mCallbackDelegate = new CallbackDelegate();
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mPackageManager = context.getPackageManager();
        this.mMediaRouter = (MediaRouter) context.getSystemService("media_router");
        this.mMediaRouterCallback = new MediaRouterCallback();
        Watchdog.getInstance().addMonitor(this);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("media_projection", new BinderService(), false);
        this.mMediaRouter.addCallback(4, this.mMediaRouterCallback, 8);
        this.mActivityManagerInternal.registerProcessObserver(new IProcessObserver.Stub() { // from class: com.android.server.media.projection.MediaProjectionManagerService.1
            public void onForegroundActivitiesChanged(int i, int i2, boolean z) {
            }

            public void onProcessDied(int i, int i2) {
            }

            public void onForegroundServicesChanged(int i, int i2, int i3) {
                MediaProjectionManagerService.this.handleForegroundServicesChanged(i, i2, i3);
            }
        });
    }

    @Override // com.android.server.SystemService
    public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
        this.mMediaRouter.rebindAsUser(targetUser2.getUserIdentifier());
        synchronized (this.mLock) {
            MediaProjection mediaProjection = this.mProjectionGrant;
            if (mediaProjection != null) {
                mediaProjection.stop();
            }
        }
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        synchronized (this.mLock) {
        }
    }

    public final void handleForegroundServicesChanged(int i, int i2, int i3) {
        synchronized (this.mLock) {
            MediaProjection mediaProjection = this.mProjectionGrant;
            if (mediaProjection != null && mediaProjection.uid == i2) {
                if (mediaProjection.requiresForegroundService()) {
                    if ((i3 & 32) != 0) {
                        return;
                    }
                    this.mProjectionGrant.stop();
                }
            }
        }
    }

    public final void startProjectionLocked(MediaProjection mediaProjection) {
        MediaProjection mediaProjection2 = this.mProjectionGrant;
        if (mediaProjection2 != null) {
            mediaProjection2.stop();
        }
        if (this.mMediaRouteInfo != null) {
            this.mMediaRouter.getFallbackRoute().select();
        }
        this.mProjectionToken = mediaProjection.asBinder();
        this.mProjectionGrant = mediaProjection;
        dispatchStart(mediaProjection);
    }

    public final void stopProjectionLocked(MediaProjection mediaProjection) {
        this.mProjectionToken = null;
        this.mProjectionGrant = null;
        dispatchStop(mediaProjection);
    }

    public final void addCallback(final IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.media.projection.MediaProjectionManagerService.2
            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                MediaProjectionManagerService.this.removeCallback(iMediaProjectionWatcherCallback);
            }
        };
        synchronized (this.mLock) {
            this.mCallbackDelegate.add(iMediaProjectionWatcherCallback);
            linkDeathRecipientLocked(iMediaProjectionWatcherCallback, deathRecipient);
        }
    }

    public final void removeCallback(IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) {
        synchronized (this.mLock) {
            unlinkDeathRecipientLocked(iMediaProjectionWatcherCallback);
            this.mCallbackDelegate.remove(iMediaProjectionWatcherCallback);
        }
    }

    public final void linkDeathRecipientLocked(IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback, IBinder.DeathRecipient deathRecipient) {
        try {
            IBinder asBinder = iMediaProjectionWatcherCallback.asBinder();
            asBinder.linkToDeath(deathRecipient, 0);
            this.mDeathEaters.put(asBinder, deathRecipient);
        } catch (RemoteException e) {
            Slog.e("MediaProjectionManagerService", "Unable to link to death for media projection monitoring callback", e);
        }
    }

    public final void unlinkDeathRecipientLocked(IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) {
        IBinder asBinder = iMediaProjectionWatcherCallback.asBinder();
        IBinder.DeathRecipient remove = this.mDeathEaters.remove(asBinder);
        if (remove != null) {
            asBinder.unlinkToDeath(remove, 0);
        }
    }

    public final void dispatchStart(MediaProjection mediaProjection) {
        this.mCallbackDelegate.dispatchStart(mediaProjection);
    }

    public final void dispatchStop(MediaProjection mediaProjection) {
        this.mCallbackDelegate.dispatchStop(mediaProjection);
    }

    public final boolean isCurrentProjection(IBinder iBinder) {
        synchronized (this.mLock) {
            IBinder iBinder2 = this.mProjectionToken;
            if (iBinder2 != null) {
                return iBinder2.equals(iBinder);
            }
            return false;
        }
    }

    public final MediaProjectionInfo getActiveProjectionInfo() {
        synchronized (this.mLock) {
            MediaProjection mediaProjection = this.mProjectionGrant;
            if (mediaProjection == null) {
                return null;
            }
            return mediaProjection.getProjectionInfo();
        }
    }

    public final void dump(PrintWriter printWriter) {
        printWriter.println("MEDIA PROJECTION MANAGER (dumpsys media_projection)");
        synchronized (this.mLock) {
            printWriter.println("Media Projection: ");
            MediaProjection mediaProjection = this.mProjectionGrant;
            if (mediaProjection != null) {
                mediaProjection.dump(printWriter);
            } else {
                printWriter.println("null");
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class BinderService extends IMediaProjectionManager.Stub {
        public BinderService() {
        }

        public boolean hasProjectionPermission(int i, String str) {
            boolean z;
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (!checkPermission(str, "android.permission.CAPTURE_VIDEO_OUTPUT")) {
                    if (MediaProjectionManagerService.this.mAppOps.noteOpNoThrow(46, i, str) != 0) {
                        z = false;
                        return z | false;
                    }
                }
                z = true;
                return z | false;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public IMediaProjection createProjection(int i, String str, int i2, boolean z) {
            if (MediaProjectionManagerService.this.mContext.checkCallingPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
                throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to grant projection permission");
            }
            if (str == null || str.isEmpty()) {
                throw new IllegalArgumentException("package name must not be empty");
            }
            try {
                ApplicationInfo applicationInfo = MediaProjectionManagerService.this.mPackageManager.getApplicationInfo(str, 0);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    MediaProjection mediaProjection = new MediaProjection(i2, i, str, applicationInfo.targetSdkVersion, applicationInfo.isPrivilegedApp());
                    if (z) {
                        MediaProjectionManagerService.this.mAppOps.setMode(46, mediaProjection.uid, mediaProjection.packageName, 0);
                    }
                    return mediaProjection;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            } catch (PackageManager.NameNotFoundException unused) {
                throw new IllegalArgumentException("No package matching :" + str);
            }
        }

        public boolean isCurrentProjection(IMediaProjection iMediaProjection) {
            return MediaProjectionManagerService.this.isCurrentProjection(iMediaProjection == null ? null : iMediaProjection.asBinder());
        }

        public MediaProjectionInfo getActiveProjectionInfo() {
            if (MediaProjectionManagerService.this.mContext.checkCallingPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
                throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to add projection callbacks");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return MediaProjectionManagerService.this.getActiveProjectionInfo();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void stopActiveProjection() {
            if (MediaProjectionManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
                throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to add projection callbacks");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (MediaProjectionManagerService.this.mProjectionGrant != null) {
                    MediaProjectionManagerService.this.mProjectionGrant.stop();
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void notifyActiveProjectionCapturedContentResized(int i, int i2) {
            if (MediaProjectionManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
                throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to notify on captured content resize");
            }
            if (isCurrentProjection(MediaProjectionManagerService.this.mProjectionGrant)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (MediaProjectionManagerService.this.mProjectionGrant != null && MediaProjectionManagerService.this.mCallbackDelegate != null) {
                        MediaProjectionManagerService.this.mCallbackDelegate.dispatchResize(MediaProjectionManagerService.this.mProjectionGrant, i, i2);
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void notifyActiveProjectionCapturedContentVisibilityChanged(boolean z) {
            if (MediaProjectionManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
                throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to notify on captured content resize");
            }
            if (isCurrentProjection(MediaProjectionManagerService.this.mProjectionGrant)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (MediaProjectionManagerService.this.mProjectionGrant != null && MediaProjectionManagerService.this.mCallbackDelegate != null) {
                        MediaProjectionManagerService.this.mCallbackDelegate.dispatchVisibilityChanged(MediaProjectionManagerService.this.mProjectionGrant, z);
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void addCallback(IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) {
            if (MediaProjectionManagerService.this.mContext.checkCallingPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
                throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to add projection callbacks");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                MediaProjectionManagerService.this.addCallback(iMediaProjectionWatcherCallback);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void removeCallback(IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) {
            if (MediaProjectionManagerService.this.mContext.checkCallingPermission("android.permission.MANAGE_MEDIA_PROJECTION") != 0) {
                throw new SecurityException("Requires MANAGE_MEDIA_PROJECTION in order to remove projection callbacks");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                MediaProjectionManagerService.this.removeCallback(iMediaProjectionWatcherCallback);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setContentRecordingSession(ContentRecordingSession contentRecordingSession, IMediaProjection iMediaProjection) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (MediaProjectionManagerService.this.mLock) {
                    if (!isCurrentProjection(iMediaProjection)) {
                        throw new SecurityException("Unable to set ContentRecordingSession on non-current MediaProjection");
                    }
                    if (!((WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class)).setContentRecordingSession(contentRecordingSession) && MediaProjectionManagerService.this.mProjectionGrant != null) {
                        MediaProjectionManagerService.this.mProjectionGrant.stop();
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(MediaProjectionManagerService.this.mContext, "MediaProjectionManagerService", printWriter)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    MediaProjectionManagerService.this.dump(printWriter);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public final boolean checkPermission(String str, String str2) {
            return MediaProjectionManagerService.this.mContext.getPackageManager().checkPermission(str2, str) == 0;
        }
    }

    /* loaded from: classes2.dex */
    public final class MediaProjection extends IMediaProjection.Stub {
        public IMediaProjectionCallback mCallback;
        public IBinder.DeathRecipient mDeathEater;
        public final boolean mIsPrivileged;
        public IBinder mLaunchCookie = null;
        public boolean mRestoreSystemAlertWindow;
        public final int mTargetSdkVersion;
        public IBinder mToken;
        public final int mType;
        public final String packageName;
        public final int uid;
        public final UserHandle userHandle;

        public boolean canProjectSecureVideo() {
            return false;
        }

        public MediaProjection(int i, int i2, String str, int i3, boolean z) {
            this.mType = i;
            this.uid = i2;
            this.packageName = str;
            this.userHandle = new UserHandle(UserHandle.getUserId(i2));
            this.mTargetSdkVersion = i3;
            this.mIsPrivileged = z;
        }

        public boolean canProjectVideo() {
            int i = this.mType;
            return i == 1 || i == 0;
        }

        public boolean canProjectAudio() {
            int i = this.mType;
            return i == 1 || i == 2 || i == 0;
        }

        public int applyVirtualDisplayFlags(int i) {
            int i2 = this.mType;
            if (i2 == 0) {
                return (i & (-9)) | 18;
            }
            if (i2 == 1) {
                return (i & (-18)) | 10;
            }
            if (i2 == 2) {
                return (i & (-9)) | 19;
            }
            throw new RuntimeException("Unknown MediaProjection type");
        }

        public void start(final IMediaProjectionCallback iMediaProjectionCallback) {
            if (iMediaProjectionCallback == null) {
                throw new IllegalArgumentException("callback must not be null");
            }
            synchronized (MediaProjectionManagerService.this.mLock) {
                if (MediaProjectionManagerService.this.isCurrentProjection(asBinder())) {
                    Slog.w("MediaProjectionManagerService", "UID " + Binder.getCallingUid() + " attempted to start already started MediaProjection");
                    return;
                }
                if (requiresForegroundService() && !MediaProjectionManagerService.this.mActivityManagerInternal.hasRunningForegroundService(this.uid, 32)) {
                    throw new SecurityException("Media projections require a foreground service of type ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION");
                }
                this.mCallback = iMediaProjectionCallback;
                registerCallback(iMediaProjectionCallback);
                try {
                    this.mToken = iMediaProjectionCallback.asBinder();
                    IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.media.projection.MediaProjectionManagerService.MediaProjection.1
                        @Override // android.os.IBinder.DeathRecipient
                        public void binderDied() {
                            MediaProjectionManagerService.this.mCallbackDelegate.remove(iMediaProjectionCallback);
                            MediaProjection.this.stop();
                        }
                    };
                    this.mDeathEater = deathRecipient;
                    this.mToken.linkToDeath(deathRecipient, 0);
                    if (this.mType == 0) {
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        try {
                            if (ArrayUtils.contains(MediaProjectionManagerService.this.mPackageManager.getPackageInfoAsUser(this.packageName, IInstalld.FLAG_USE_QUOTA, UserHandle.getUserId(this.uid)).requestedPermissions, "android.permission.SYSTEM_ALERT_WINDOW") && MediaProjectionManagerService.this.mAppOps.unsafeCheckOpRawNoThrow(24, this.uid, this.packageName) == 3) {
                                MediaProjectionManagerService.this.mAppOps.setMode(24, this.uid, this.packageName, 0);
                                this.mRestoreSystemAlertWindow = true;
                            }
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                        } catch (PackageManager.NameNotFoundException e) {
                            Slog.w("MediaProjectionManagerService", "Package not found, aborting MediaProjection", e);
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return;
                        }
                    }
                    MediaProjectionManagerService.this.startProjectionLocked(this);
                } catch (RemoteException e2) {
                    Slog.w("MediaProjectionManagerService", "MediaProjectionCallbacks must be valid, aborting MediaProjection", e2);
                }
            }
        }

        public void stop() {
            synchronized (MediaProjectionManagerService.this.mLock) {
                if (!MediaProjectionManagerService.this.isCurrentProjection(asBinder())) {
                    Slog.w("MediaProjectionManagerService", "Attempted to stop inactive MediaProjection (uid=" + Binder.getCallingUid() + ", pid=" + Binder.getCallingPid() + ")");
                    return;
                }
                if (this.mRestoreSystemAlertWindow) {
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    if (MediaProjectionManagerService.this.mAppOps.unsafeCheckOpRawNoThrow(24, this.uid, this.packageName) == 0) {
                        MediaProjectionManagerService.this.mAppOps.setMode(24, this.uid, this.packageName, 3);
                    }
                    this.mRestoreSystemAlertWindow = false;
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
                MediaProjectionManagerService.this.stopProjectionLocked(this);
                this.mToken.unlinkToDeath(this.mDeathEater, 0);
                this.mToken = null;
                unregisterCallback(this.mCallback);
                this.mCallback = null;
            }
        }

        public void registerCallback(IMediaProjectionCallback iMediaProjectionCallback) {
            if (iMediaProjectionCallback == null) {
                throw new IllegalArgumentException("callback must not be null");
            }
            MediaProjectionManagerService.this.mCallbackDelegate.add(iMediaProjectionCallback);
        }

        public void unregisterCallback(IMediaProjectionCallback iMediaProjectionCallback) {
            if (iMediaProjectionCallback == null) {
                throw new IllegalArgumentException("callback must not be null");
            }
            MediaProjectionManagerService.this.mCallbackDelegate.remove(iMediaProjectionCallback);
        }

        public void setLaunchCookie(IBinder iBinder) {
            this.mLaunchCookie = iBinder;
        }

        public IBinder getLaunchCookie() {
            return this.mLaunchCookie;
        }

        public MediaProjectionInfo getProjectionInfo() {
            return new MediaProjectionInfo(this.packageName, this.userHandle);
        }

        public boolean requiresForegroundService() {
            return this.mTargetSdkVersion >= 29 && !this.mIsPrivileged;
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println("(" + this.packageName + ", uid=" + this.uid + "): " + MediaProjectionManagerService.typeToString(this.mType));
        }
    }

    /* loaded from: classes2.dex */
    public class MediaRouterCallback extends MediaRouter.SimpleCallback {
        public MediaRouterCallback() {
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteSelected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
            synchronized (MediaProjectionManagerService.this.mLock) {
                if ((i & 4) != 0) {
                    MediaProjectionManagerService.this.mMediaRouteInfo = routeInfo;
                    if (MediaProjectionManagerService.this.mProjectionGrant != null) {
                        MediaProjectionManagerService.this.mProjectionGrant.stop();
                    }
                }
            }
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteUnselected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
            if (MediaProjectionManagerService.this.mMediaRouteInfo == routeInfo) {
                MediaProjectionManagerService.this.mMediaRouteInfo = null;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class CallbackDelegate {
        public final Object mLock = new Object();
        public Handler mHandler = new Handler(Looper.getMainLooper(), null, true);
        public Map<IBinder, IMediaProjectionCallback> mClientCallbacks = new ArrayMap();
        public Map<IBinder, IMediaProjectionWatcherCallback> mWatcherCallbacks = new ArrayMap();

        public void add(IMediaProjectionCallback iMediaProjectionCallback) {
            synchronized (this.mLock) {
                this.mClientCallbacks.put(iMediaProjectionCallback.asBinder(), iMediaProjectionCallback);
            }
        }

        public void add(IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) {
            synchronized (this.mLock) {
                this.mWatcherCallbacks.put(iMediaProjectionWatcherCallback.asBinder(), iMediaProjectionWatcherCallback);
            }
        }

        public void remove(IMediaProjectionCallback iMediaProjectionCallback) {
            synchronized (this.mLock) {
                this.mClientCallbacks.remove(iMediaProjectionCallback.asBinder());
            }
        }

        public void remove(IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) {
            synchronized (this.mLock) {
                this.mWatcherCallbacks.remove(iMediaProjectionWatcherCallback.asBinder());
            }
        }

        public void dispatchStart(MediaProjection mediaProjection) {
            if (mediaProjection == null) {
                Slog.e("MediaProjectionManagerService", "Tried to dispatch start notification for a null media projection. Ignoring!");
                return;
            }
            synchronized (this.mLock) {
                for (IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback : this.mWatcherCallbacks.values()) {
                    this.mHandler.post(new WatcherStartCallback(mediaProjection.getProjectionInfo(), iMediaProjectionWatcherCallback));
                }
            }
        }

        public void dispatchStop(MediaProjection mediaProjection) {
            if (mediaProjection == null) {
                Slog.e("MediaProjectionManagerService", "Tried to dispatch stop notification for a null media projection. Ignoring!");
                return;
            }
            synchronized (this.mLock) {
                for (IMediaProjectionCallback iMediaProjectionCallback : this.mClientCallbacks.values()) {
                    this.mHandler.post(new ClientStopCallback(iMediaProjectionCallback));
                }
                for (IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback : this.mWatcherCallbacks.values()) {
                    this.mHandler.post(new WatcherStopCallback(mediaProjection.getProjectionInfo(), iMediaProjectionWatcherCallback));
                }
            }
        }

        public void dispatchResize(MediaProjection mediaProjection, final int i, final int i2) {
            if (mediaProjection == null) {
                Slog.e("MediaProjectionManagerService", "Tried to dispatch resize notification for a null media projection. Ignoring!");
                return;
            }
            synchronized (this.mLock) {
                for (final IMediaProjectionCallback iMediaProjectionCallback : this.mClientCallbacks.values()) {
                    this.mHandler.post(new Runnable() { // from class: com.android.server.media.projection.MediaProjectionManagerService$CallbackDelegate$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaProjectionManagerService.CallbackDelegate.lambda$dispatchResize$0(iMediaProjectionCallback, i, i2);
                        }
                    });
                }
            }
        }

        public static /* synthetic */ void lambda$dispatchResize$0(IMediaProjectionCallback iMediaProjectionCallback, int i, int i2) {
            try {
                iMediaProjectionCallback.onCapturedContentResize(i, i2);
            } catch (RemoteException e) {
                Slog.w("MediaProjectionManagerService", "Failed to notify media projection has resized to " + i + " x " + i2, e);
            }
        }

        public void dispatchVisibilityChanged(MediaProjection mediaProjection, final boolean z) {
            if (mediaProjection == null) {
                Slog.e("MediaProjectionManagerService", "Tried to dispatch visibility changed notification for a null media projection. Ignoring!");
                return;
            }
            synchronized (this.mLock) {
                for (final IMediaProjectionCallback iMediaProjectionCallback : this.mClientCallbacks.values()) {
                    this.mHandler.post(new Runnable() { // from class: com.android.server.media.projection.MediaProjectionManagerService$CallbackDelegate$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaProjectionManagerService.CallbackDelegate.lambda$dispatchVisibilityChanged$1(iMediaProjectionCallback, z);
                        }
                    });
                }
            }
        }

        public static /* synthetic */ void lambda$dispatchVisibilityChanged$1(IMediaProjectionCallback iMediaProjectionCallback, boolean z) {
            try {
                iMediaProjectionCallback.onCapturedContentVisibilityChanged(z);
            } catch (RemoteException e) {
                Slog.w("MediaProjectionManagerService", "Failed to notify media projection has captured content visibility change to " + z, e);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class WatcherStartCallback implements Runnable {
        public IMediaProjectionWatcherCallback mCallback;
        public MediaProjectionInfo mInfo;

        public WatcherStartCallback(MediaProjectionInfo mediaProjectionInfo, IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) {
            this.mInfo = mediaProjectionInfo;
            this.mCallback = iMediaProjectionWatcherCallback;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.mCallback.onStart(this.mInfo);
            } catch (RemoteException e) {
                Slog.w("MediaProjectionManagerService", "Failed to notify media projection has stopped", e);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class WatcherStopCallback implements Runnable {
        public IMediaProjectionWatcherCallback mCallback;
        public MediaProjectionInfo mInfo;

        public WatcherStopCallback(MediaProjectionInfo mediaProjectionInfo, IMediaProjectionWatcherCallback iMediaProjectionWatcherCallback) {
            this.mInfo = mediaProjectionInfo;
            this.mCallback = iMediaProjectionWatcherCallback;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.mCallback.onStop(this.mInfo);
            } catch (RemoteException e) {
                Slog.w("MediaProjectionManagerService", "Failed to notify media projection has stopped", e);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class ClientStopCallback implements Runnable {
        public IMediaProjectionCallback mCallback;

        public ClientStopCallback(IMediaProjectionCallback iMediaProjectionCallback) {
            this.mCallback = iMediaProjectionCallback;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.mCallback.onStop();
            } catch (RemoteException e) {
                Slog.w("MediaProjectionManagerService", "Failed to notify media projection has stopped", e);
            }
        }
    }

    public static String typeToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? Integer.toString(i) : "TYPE_PRESENTATION" : "TYPE_MIRRORING" : "TYPE_SCREEN_CAPTURE";
    }
}
