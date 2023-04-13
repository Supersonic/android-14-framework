package android.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.util.Log;
import android.view.ISurfaceControlViewHost;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.ViewRootImpl;
import android.view.WindowManager;
import android.view.WindowlessWindowManager;
import android.view.accessibility.IAccessibilityEmbeddedConnection;
import android.window.ISurfaceSyncGroup;
import android.window.WindowTokenClient;
import dalvik.system.CloseGuard;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes4.dex */
public class SurfaceControlViewHost {
    private static final String TAG = "SurfaceControlViewHost";
    private IAccessibilityEmbeddedConnection mAccessibilityEmbeddedConnection;
    private final CloseGuard mCloseGuard;
    private boolean mReleased;
    private ISurfaceControlViewHost mRemoteInterface;
    private SurfaceControl mSurfaceControl;
    private final ViewRootImpl mViewRoot;
    private WindowlessWindowManager mWm;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class ISurfaceControlViewHostImpl extends ISurfaceControlViewHost.Stub {
        private ISurfaceControlViewHostImpl() {
        }

        @Override // android.view.ISurfaceControlViewHost
        public void onConfigurationChanged(final Configuration configuration) {
            if (SurfaceControlViewHost.this.mViewRoot == null) {
                return;
            }
            SurfaceControlViewHost.this.mViewRoot.mHandler.post(new Runnable() { // from class: android.view.SurfaceControlViewHost$ISurfaceControlViewHostImpl$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SurfaceControlViewHost.ISurfaceControlViewHostImpl.this.lambda$onConfigurationChanged$0(configuration);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onConfigurationChanged$0(Configuration configuration) {
            if (SurfaceControlViewHost.this.mWm != null) {
                SurfaceControlViewHost.this.mWm.setConfiguration(configuration);
            }
            if (SurfaceControlViewHost.this.mViewRoot != null) {
                SurfaceControlViewHost.this.mViewRoot.forceWmRelayout();
            }
        }

        @Override // android.view.ISurfaceControlViewHost
        public void onDispatchDetachedFromWindow() {
            if (SurfaceControlViewHost.this.mViewRoot == null) {
                return;
            }
            SurfaceControlViewHost.this.mViewRoot.mHandler.post(new Runnable() { // from class: android.view.SurfaceControlViewHost$ISurfaceControlViewHostImpl$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    SurfaceControlViewHost.ISurfaceControlViewHostImpl.this.lambda$onDispatchDetachedFromWindow$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDispatchDetachedFromWindow$1() {
            SurfaceControlViewHost.this.release();
        }

        @Override // android.view.ISurfaceControlViewHost
        public void onInsetsChanged(InsetsState state, final Rect frame) {
            if (SurfaceControlViewHost.this.mViewRoot != null) {
                SurfaceControlViewHost.this.mViewRoot.mHandler.post(new Runnable() { // from class: android.view.SurfaceControlViewHost$ISurfaceControlViewHostImpl$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SurfaceControlViewHost.ISurfaceControlViewHostImpl.this.lambda$onInsetsChanged$2(frame);
                    }
                });
            }
            SurfaceControlViewHost.this.mWm.setInsetsState(state);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInsetsChanged$2(Rect frame) {
            SurfaceControlViewHost.this.mViewRoot.setOverrideInsetsFrame(frame);
        }

        @Override // android.view.ISurfaceControlViewHost
        public ISurfaceSyncGroup getSurfaceSyncGroup() {
            final CompletableFuture<ISurfaceSyncGroup> surfaceSyncGroup = new CompletableFuture<>();
            SurfaceControlViewHost.this.mViewRoot.mHandler.post(new Runnable() { // from class: android.view.SurfaceControlViewHost$ISurfaceControlViewHostImpl$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SurfaceControlViewHost.ISurfaceControlViewHostImpl.this.lambda$getSurfaceSyncGroup$3(surfaceSyncGroup);
                }
            });
            try {
                return surfaceSyncGroup.get(1L, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.m109e(SurfaceControlViewHost.TAG, "Failed to get SurfaceSyncGroup for SCVH", e);
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getSurfaceSyncGroup$3(CompletableFuture surfaceSyncGroup) {
            surfaceSyncGroup.complete(SurfaceControlViewHost.this.mViewRoot.getOrCreateSurfaceSyncGroup().mISurfaceSyncGroup);
        }
    }

    /* loaded from: classes4.dex */
    public static final class SurfacePackage implements Parcelable {
        public static final Parcelable.Creator<SurfacePackage> CREATOR = new Parcelable.Creator<SurfacePackage>() { // from class: android.view.SurfaceControlViewHost.SurfacePackage.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SurfacePackage createFromParcel(Parcel in) {
                return new SurfacePackage(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SurfacePackage[] newArray(int size) {
                return new SurfacePackage[size];
            }
        };
        private final IAccessibilityEmbeddedConnection mAccessibilityEmbeddedConnection;
        private final IBinder mInputToken;
        private final ISurfaceControlViewHost mRemoteInterface;
        private SurfaceControl mSurfaceControl;

        SurfacePackage(SurfaceControl sc, IAccessibilityEmbeddedConnection connection, IBinder inputToken, ISurfaceControlViewHost ri) {
            this.mSurfaceControl = sc;
            this.mAccessibilityEmbeddedConnection = connection;
            this.mInputToken = inputToken;
            this.mRemoteInterface = ri;
        }

        public SurfacePackage(SurfacePackage other) {
            SurfaceControl otherSurfaceControl = other.mSurfaceControl;
            if (otherSurfaceControl != null && otherSurfaceControl.isValid()) {
                SurfaceControl surfaceControl = new SurfaceControl();
                this.mSurfaceControl = surfaceControl;
                surfaceControl.copyFrom(otherSurfaceControl, "SurfacePackage");
            }
            this.mAccessibilityEmbeddedConnection = other.mAccessibilityEmbeddedConnection;
            this.mInputToken = other.mInputToken;
            this.mRemoteInterface = other.mRemoteInterface;
        }

        private SurfacePackage(Parcel in) {
            SurfaceControl surfaceControl = new SurfaceControl();
            this.mSurfaceControl = surfaceControl;
            surfaceControl.readFromParcel(in);
            this.mSurfaceControl.setUnreleasedWarningCallSite("SurfacePackage(Parcel)");
            this.mAccessibilityEmbeddedConnection = IAccessibilityEmbeddedConnection.Stub.asInterface(in.readStrongBinder());
            this.mInputToken = in.readStrongBinder();
            this.mRemoteInterface = ISurfaceControlViewHost.Stub.asInterface(in.readStrongBinder());
        }

        public SurfaceControl getSurfaceControl() {
            return this.mSurfaceControl;
        }

        public IAccessibilityEmbeddedConnection getAccessibilityEmbeddedConnection() {
            return this.mAccessibilityEmbeddedConnection;
        }

        public ISurfaceControlViewHost getRemoteInterface() {
            return this.mRemoteInterface;
        }

        public void notifyConfigurationChanged(Configuration c) {
            try {
                getRemoteInterface().onConfigurationChanged(c);
            } catch (RemoteException e) {
                e.rethrowAsRuntimeException();
            }
        }

        public void notifyDetachedFromWindow() {
            try {
                getRemoteInterface().onDispatchDetachedFromWindow();
            } catch (RemoteException e) {
                e.rethrowAsRuntimeException();
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            this.mSurfaceControl.writeToParcel(out, flags);
            out.writeStrongBinder(this.mAccessibilityEmbeddedConnection.asBinder());
            out.writeStrongBinder(this.mInputToken);
            out.writeStrongBinder(this.mRemoteInterface.asBinder());
        }

        public void release() {
            SurfaceControl surfaceControl = this.mSurfaceControl;
            if (surfaceControl != null) {
                surfaceControl.release();
            }
            this.mSurfaceControl = null;
        }

        public IBinder getInputToken() {
            return this.mInputToken;
        }
    }

    public SurfaceControlViewHost(Context c, Display d, WindowlessWindowManager wwm, String callsite) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mReleased = false;
        this.mRemoteInterface = new ISurfaceControlViewHostImpl();
        this.mWm = wwm;
        ViewRootImpl viewRootImpl = new ViewRootImpl(c, d, this.mWm, new WindowlessWindowLayout());
        this.mViewRoot = viewRootImpl;
        closeGuard.openWithCallSite("release", callsite);
        addConfigCallback(c, d);
        WindowManagerGlobal.getInstance().addWindowlessRoot(viewRootImpl);
        this.mAccessibilityEmbeddedConnection = viewRootImpl.getAccessibilityEmbeddedConnection();
    }

    public SurfaceControlViewHost(Context context, Display display, IBinder hostToken) {
        this(context, display, hostToken, "untracked");
    }

    public SurfaceControlViewHost(Context context, Display display, IBinder hostToken, String callsite) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mReleased = false;
        this.mRemoteInterface = new ISurfaceControlViewHostImpl();
        this.mSurfaceControl = new SurfaceControl.Builder().setContainerLayer().setName(TAG).setCallsite("SurfaceControlViewHost[" + callsite + NavigationBarInflaterView.SIZE_MOD_END).build();
        this.mWm = new WindowlessWindowManager(context.getResources().getConfiguration(), this.mSurfaceControl, hostToken);
        ViewRootImpl viewRootImpl = new ViewRootImpl(context, display, this.mWm, new WindowlessWindowLayout());
        this.mViewRoot = viewRootImpl;
        closeGuard.openWithCallSite("release", callsite);
        addConfigCallback(context, display);
        WindowManagerGlobal.getInstance().addWindowlessRoot(viewRootImpl);
        this.mAccessibilityEmbeddedConnection = viewRootImpl.getAccessibilityEmbeddedConnection();
    }

    private void addConfigCallback(Context c, final Display d) {
        final IBinder token = c.getWindowContextToken();
        ViewRootImpl.addConfigCallback(new ViewRootImpl.ConfigChangedCallback() { // from class: android.view.SurfaceControlViewHost$$ExternalSyntheticLambda0
            @Override // android.view.ViewRootImpl.ConfigChangedCallback
            public final void onConfigurationChanged(Configuration configuration) {
                SurfaceControlViewHost.lambda$addConfigCallback$0(IBinder.this, d, configuration);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$addConfigCallback$0(IBinder token, Display d, Configuration conf) {
        if (token instanceof WindowTokenClient) {
            WindowTokenClient w = (WindowTokenClient) token;
            w.onConfigurationChanged(conf, d.getDisplayId(), true);
        }
    }

    protected void finalize() throws Throwable {
        if (this.mReleased) {
            return;
        }
        CloseGuard closeGuard = this.mCloseGuard;
        if (closeGuard != null) {
            closeGuard.warnIfOpen();
        }
        this.mViewRoot.die(false);
        WindowManagerGlobal.getInstance().removeWindowlessRoot(this.mViewRoot);
    }

    public SurfacePackage getSurfacePackage() {
        if (this.mSurfaceControl != null && this.mAccessibilityEmbeddedConnection != null) {
            return new SurfacePackage(new SurfaceControl(this.mSurfaceControl, "getSurfacePackage"), this.mAccessibilityEmbeddedConnection, this.mWm.getFocusGrantToken(), this.mRemoteInterface);
        }
        return null;
    }

    public void setView(View view, int width, int height) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(width, height, 2, 0, -2);
        setView(view, lp);
    }

    public void setView(View view, WindowManager.LayoutParams attrs) {
        Objects.requireNonNull(view);
        attrs.flags |= 16777216;
        addWindowToken(attrs);
        view.setLayoutParams(attrs);
        this.mViewRoot.setView(view, attrs, null);
    }

    public View getView() {
        return this.mViewRoot.getView();
    }

    public IWindow getWindowToken() {
        return this.mViewRoot.mWindow;
    }

    public WindowlessWindowManager getWindowlessWM() {
        return this.mWm;
    }

    public void relayout(WindowManager.LayoutParams attrs, WindowlessWindowManager.ResizeCompleteCallback callback) {
        this.mViewRoot.setLayoutParams(attrs, false);
        this.mViewRoot.setReportNextDraw(true, "scvh_relayout");
        this.mWm.setCompletionCallback(this.mViewRoot.mWindow.asBinder(), callback);
    }

    public void relayout(WindowManager.LayoutParams attrs) {
        this.mViewRoot.setLayoutParams(attrs, false);
    }

    public void relayout(int width, int height) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(width, height, 2, 0, -2);
        relayout(lp);
    }

    public void release() {
        this.mViewRoot.die(true);
        WindowManagerGlobal.getInstance().removeWindowlessRoot(this.mViewRoot);
        this.mReleased = true;
        this.mCloseGuard.close();
    }

    public IBinder getFocusGrantToken() {
        return this.mWm.getFocusGrantToken();
    }

    private void addWindowToken(WindowManager.LayoutParams attrs) {
        WindowManagerImpl wm = (WindowManagerImpl) this.mViewRoot.mContext.getSystemService(Context.WINDOW_SERVICE);
        attrs.token = wm.getDefaultToken();
    }

    public boolean transferTouchGestureToHost() {
        if (this.mViewRoot == null) {
            return false;
        }
        IWindowSession realWm = WindowManagerGlobal.getWindowSession();
        try {
            return realWm.transferEmbeddedTouchFocusToHost(this.mViewRoot.mWindow);
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
            return false;
        }
    }
}
