package com.android.server.display;

import android.content.Context;
import android.graphics.Point;
import android.hardware.display.IVirtualDisplayCallback;
import android.hardware.display.VirtualDisplayConfig;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionCallback;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.p005os.IInstalld;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayShape;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;
import java.io.PrintWriter;
@VisibleForTesting
/* loaded from: classes.dex */
public class VirtualDisplayAdapter extends DisplayAdapter {
    @VisibleForTesting
    static final String UNIQUE_ID_PREFIX = "virtual:";
    public final Handler mHandler;
    public final SurfaceControlDisplayFactory mSurfaceControlDisplayFactory;
    public final ArrayMap<IBinder, VirtualDisplayDevice> mVirtualDisplayDevices;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface SurfaceControlDisplayFactory {
        IBinder createDisplay(String str, boolean z, float f);

        void destroyDisplay(IBinder iBinder);
    }

    @Override // com.android.server.display.DisplayAdapter
    public /* bridge */ /* synthetic */ void dumpLocked(PrintWriter printWriter) {
        super.dumpLocked(printWriter);
    }

    @Override // com.android.server.display.DisplayAdapter
    public /* bridge */ /* synthetic */ void registerLocked() {
        super.registerLocked();
    }

    public VirtualDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener) {
        this(syncRoot, context, handler, listener, new SurfaceControlDisplayFactory() { // from class: com.android.server.display.VirtualDisplayAdapter.1
            @Override // com.android.server.display.VirtualDisplayAdapter.SurfaceControlDisplayFactory
            public IBinder createDisplay(String str, boolean z, float f) {
                return DisplayControl.createDisplay(str, z, f);
            }

            @Override // com.android.server.display.VirtualDisplayAdapter.SurfaceControlDisplayFactory
            public void destroyDisplay(IBinder iBinder) {
                DisplayControl.destroyDisplay(iBinder);
            }
        });
    }

    @VisibleForTesting
    public VirtualDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener, SurfaceControlDisplayFactory surfaceControlDisplayFactory) {
        super(syncRoot, context, handler, listener, "VirtualDisplayAdapter");
        this.mVirtualDisplayDevices = new ArrayMap<>();
        this.mHandler = handler;
        this.mSurfaceControlDisplayFactory = surfaceControlDisplayFactory;
    }

    public DisplayDevice createVirtualDisplayLocked(IVirtualDisplayCallback iVirtualDisplayCallback, IMediaProjection iMediaProjection, int i, String str, Surface surface, int i2, VirtualDisplayConfig virtualDisplayConfig) {
        String str2;
        boolean z;
        String name = virtualDisplayConfig.getName();
        boolean z2 = (i2 & 4) != 0;
        IBinder asBinder = iVirtualDisplayCallback.asBinder();
        IBinder createDisplay = this.mSurfaceControlDisplayFactory.createDisplay(name, z2, virtualDisplayConfig.getRequestedRefreshRate());
        String str3 = UNIQUE_ID_PREFIX + str + "," + i + "," + name + ",";
        int nextUniqueIndex = getNextUniqueIndex(str3);
        String uniqueId = virtualDisplayConfig.getUniqueId();
        if (uniqueId == null) {
            str2 = str3 + nextUniqueIndex;
        } else {
            str2 = UNIQUE_ID_PREFIX + str + XmlUtils.STRING_ARRAY_SEPARATOR + uniqueId;
        }
        String str4 = str2;
        IMediaProjectionCallback mediaProjectionCallback = iMediaProjection != null ? new MediaProjectionCallback(asBinder) : null;
        Callback callback = new Callback(iVirtualDisplayCallback, this.mHandler);
        IMediaProjectionCallback iMediaProjectionCallback = mediaProjectionCallback;
        VirtualDisplayDevice virtualDisplayDevice = new VirtualDisplayDevice(createDisplay, asBinder, i, str, surface, i2, callback, iMediaProjection, iMediaProjectionCallback, str4, nextUniqueIndex, virtualDisplayConfig);
        this.mVirtualDisplayDevices.put(asBinder, virtualDisplayDevice);
        if (iMediaProjection != null) {
            try {
                iMediaProjection.registerCallback(iMediaProjectionCallback);
            } catch (RemoteException unused) {
                z = false;
                this.mVirtualDisplayDevices.remove(asBinder);
                virtualDisplayDevice.destroyLocked(z);
                return null;
            }
        }
        z = false;
        try {
            asBinder.linkToDeath(virtualDisplayDevice, 0);
            return virtualDisplayDevice;
        } catch (RemoteException unused2) {
            this.mVirtualDisplayDevices.remove(asBinder);
            virtualDisplayDevice.destroyLocked(z);
            return null;
        }
    }

    public void resizeVirtualDisplayLocked(IBinder iBinder, int i, int i2, int i3) {
        VirtualDisplayDevice virtualDisplayDevice = this.mVirtualDisplayDevices.get(iBinder);
        if (virtualDisplayDevice != null) {
            virtualDisplayDevice.resizeLocked(i, i2, i3);
        }
    }

    @VisibleForTesting
    public Surface getVirtualDisplaySurfaceLocked(IBinder iBinder) {
        VirtualDisplayDevice virtualDisplayDevice = this.mVirtualDisplayDevices.get(iBinder);
        if (virtualDisplayDevice != null) {
            return virtualDisplayDevice.getSurfaceLocked();
        }
        return null;
    }

    public void setVirtualDisplaySurfaceLocked(IBinder iBinder, Surface surface) {
        VirtualDisplayDevice virtualDisplayDevice = this.mVirtualDisplayDevices.get(iBinder);
        if (virtualDisplayDevice != null) {
            virtualDisplayDevice.setSurfaceLocked(surface);
        }
    }

    public void setDisplayIdToMirror(IBinder iBinder, int i) {
        VirtualDisplayDevice virtualDisplayDevice = this.mVirtualDisplayDevices.get(iBinder);
        if (virtualDisplayDevice != null) {
            virtualDisplayDevice.setDisplayIdToMirror(i);
        }
    }

    public DisplayDevice releaseVirtualDisplayLocked(IBinder iBinder) {
        VirtualDisplayDevice remove = this.mVirtualDisplayDevices.remove(iBinder);
        if (remove != null) {
            remove.destroyLocked(true);
            iBinder.unlinkToDeath(remove, 0);
        }
        return remove;
    }

    public void setVirtualDisplayStateLocked(IBinder iBinder, boolean z) {
        VirtualDisplayDevice virtualDisplayDevice = this.mVirtualDisplayDevices.get(iBinder);
        if (virtualDisplayDevice != null) {
            virtualDisplayDevice.setDisplayState(z);
        }
    }

    public final int getNextUniqueIndex(String str) {
        int i = 0;
        if (this.mVirtualDisplayDevices.isEmpty()) {
            return 0;
        }
        for (VirtualDisplayDevice virtualDisplayDevice : this.mVirtualDisplayDevices.values()) {
            if (virtualDisplayDevice.getUniqueId().startsWith(str) && virtualDisplayDevice.mUniqueIndex >= i) {
                i = virtualDisplayDevice.mUniqueIndex + 1;
            }
        }
        return i;
    }

    public final void handleBinderDiedLocked(IBinder iBinder) {
        this.mVirtualDisplayDevices.remove(iBinder);
    }

    public final void handleMediaProjectionStoppedLocked(IBinder iBinder) {
        VirtualDisplayDevice virtualDisplayDevice = this.mVirtualDisplayDevices.get(iBinder);
        if (virtualDisplayDevice != null) {
            Slog.i("VirtualDisplayAdapter", "Virtual display device released because media projection stopped: " + virtualDisplayDevice.mName);
            virtualDisplayDevice.stopLocked();
        }
    }

    /* loaded from: classes.dex */
    public final class VirtualDisplayDevice extends DisplayDevice implements IBinder.DeathRecipient {
        public final IBinder mAppToken;
        public final Callback mCallback;
        public int mDensityDpi;
        public int mDisplayIdToMirror;
        public int mDisplayState;
        public final int mFlags;
        public int mHeight;
        public DisplayDeviceInfo mInfo;
        public boolean mIsDisplayOn;
        public boolean mIsWindowManagerMirroring;
        public final IMediaProjectionCallback mMediaProjectionCallback;
        public Display.Mode mMode;
        public final String mName;
        public final String mOwnerPackageName;
        public final int mOwnerUid;
        public int mPendingChanges;
        public final IMediaProjection mProjection;
        public float mRequestedRefreshRate;
        public boolean mStopped;
        public Surface mSurface;
        public int mUniqueIndex;
        public int mWidth;

        @Override // com.android.server.display.DisplayDevice
        public boolean hasStableUniqueId() {
            return false;
        }

        public VirtualDisplayDevice(IBinder iBinder, IBinder iBinder2, int i, String str, Surface surface, int i2, Callback callback, IMediaProjection iMediaProjection, IMediaProjectionCallback iMediaProjectionCallback, String str2, int i3, VirtualDisplayConfig virtualDisplayConfig) {
            super(VirtualDisplayAdapter.this, iBinder, str2, VirtualDisplayAdapter.this.getContext());
            this.mAppToken = iBinder2;
            this.mOwnerUid = i;
            this.mOwnerPackageName = str;
            this.mName = virtualDisplayConfig.getName();
            this.mWidth = virtualDisplayConfig.getWidth();
            this.mHeight = virtualDisplayConfig.getHeight();
            this.mDensityDpi = virtualDisplayConfig.getDensityDpi();
            this.mRequestedRefreshRate = virtualDisplayConfig.getRequestedRefreshRate();
            this.mMode = DisplayAdapter.createMode(this.mWidth, this.mHeight, getRefreshRate());
            this.mSurface = surface;
            this.mFlags = i2;
            this.mCallback = callback;
            this.mProjection = iMediaProjection;
            this.mMediaProjectionCallback = iMediaProjectionCallback;
            this.mDisplayState = 0;
            this.mPendingChanges |= 1;
            this.mUniqueIndex = i3;
            this.mIsDisplayOn = surface != null;
            this.mDisplayIdToMirror = virtualDisplayConfig.getDisplayIdToMirror();
            this.mIsWindowManagerMirroring = virtualDisplayConfig.isWindowManagerMirroringEnabled();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            IMediaProjectionCallback iMediaProjectionCallback;
            synchronized (VirtualDisplayAdapter.this.getSyncRoot()) {
                VirtualDisplayAdapter.this.handleBinderDiedLocked(this.mAppToken);
                Slog.i("VirtualDisplayAdapter", "Virtual display device released because application token died: " + this.mOwnerPackageName);
                destroyLocked(false);
                IMediaProjection iMediaProjection = this.mProjection;
                if (iMediaProjection != null && (iMediaProjectionCallback = this.mMediaProjectionCallback) != null) {
                    try {
                        iMediaProjection.unregisterCallback(iMediaProjectionCallback);
                    } catch (RemoteException e) {
                        Slog.w("VirtualDisplayAdapter", "Failed to unregister callback in binderDied", e);
                    }
                }
                VirtualDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 3);
            }
        }

        public void destroyLocked(boolean z) {
            IMediaProjectionCallback iMediaProjectionCallback;
            Surface surface = this.mSurface;
            if (surface != null) {
                surface.release();
                this.mSurface = null;
            }
            VirtualDisplayAdapter.this.mSurfaceControlDisplayFactory.destroyDisplay(getDisplayTokenLocked());
            IMediaProjection iMediaProjection = this.mProjection;
            if (iMediaProjection != null && (iMediaProjectionCallback = this.mMediaProjectionCallback) != null) {
                try {
                    iMediaProjection.unregisterCallback(iMediaProjectionCallback);
                } catch (RemoteException e) {
                    Slog.w("VirtualDisplayAdapter", "Failed to unregister callback in destroy", e);
                }
            }
            if (z) {
                this.mCallback.dispatchDisplayStopped();
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public int getDisplayIdToMirrorLocked() {
            return this.mDisplayIdToMirror;
        }

        public void setDisplayIdToMirror(int i) {
            if (this.mDisplayIdToMirror != i) {
                this.mDisplayIdToMirror = i;
                this.mInfo = null;
                VirtualDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 2);
                VirtualDisplayAdapter.this.sendTraversalRequestLocked();
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public boolean isWindowManagerMirroringLocked() {
            return this.mIsWindowManagerMirroring;
        }

        @Override // com.android.server.display.DisplayDevice
        public void setWindowManagerMirroringLocked(boolean z) {
            if (this.mIsWindowManagerMirroring != z) {
                this.mIsWindowManagerMirroring = z;
                VirtualDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 2);
                VirtualDisplayAdapter.this.sendTraversalRequestLocked();
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public Point getDisplaySurfaceDefaultSizeLocked() {
            Surface surface = this.mSurface;
            if (surface == null) {
                return null;
            }
            return surface.getDefaultSize();
        }

        @VisibleForTesting
        public Surface getSurfaceLocked() {
            return this.mSurface;
        }

        @Override // com.android.server.display.DisplayDevice
        public Runnable requestDisplayStateLocked(int i, float f, float f2) {
            if (i != this.mDisplayState) {
                this.mDisplayState = i;
                if (i == 1) {
                    this.mCallback.dispatchDisplayPaused();
                    return null;
                }
                this.mCallback.dispatchDisplayResumed();
                return null;
            }
            return null;
        }

        @Override // com.android.server.display.DisplayDevice
        public void performTraversalLocked(SurfaceControl.Transaction transaction) {
            if ((this.mPendingChanges & 2) != 0) {
                transaction.setDisplaySize(getDisplayTokenLocked(), this.mWidth, this.mHeight);
            }
            if ((this.mPendingChanges & 1) != 0) {
                setSurfaceLocked(transaction, this.mSurface);
            }
            this.mPendingChanges = 0;
        }

        public void setSurfaceLocked(Surface surface) {
            Surface surface2;
            if (this.mStopped || (surface2 = this.mSurface) == surface) {
                return;
            }
            if ((surface2 != null) != (surface != null)) {
                VirtualDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 2);
            }
            VirtualDisplayAdapter.this.sendTraversalRequestLocked();
            this.mSurface = surface;
            this.mInfo = null;
            this.mPendingChanges |= 1;
        }

        public void resizeLocked(int i, int i2, int i3) {
            if (this.mWidth == i && this.mHeight == i2 && this.mDensityDpi == i3) {
                return;
            }
            VirtualDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 2);
            VirtualDisplayAdapter.this.sendTraversalRequestLocked();
            this.mWidth = i;
            this.mHeight = i2;
            this.mMode = DisplayAdapter.createMode(i, i2, getRefreshRate());
            this.mDensityDpi = i3;
            this.mInfo = null;
            this.mPendingChanges |= 2;
        }

        public void setDisplayState(boolean z) {
            if (this.mIsDisplayOn != z) {
                this.mIsDisplayOn = z;
                this.mInfo = null;
                VirtualDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 2);
            }
        }

        public void stopLocked() {
            setSurfaceLocked(null);
            this.mStopped = true;
        }

        @Override // com.android.server.display.DisplayDevice
        public void dumpLocked(PrintWriter printWriter) {
            super.dumpLocked(printWriter);
            printWriter.println("mFlags=" + this.mFlags);
            printWriter.println("mDisplayState=" + Display.stateToString(this.mDisplayState));
            printWriter.println("mStopped=" + this.mStopped);
            printWriter.println("mDisplayIdToMirror=" + this.mDisplayIdToMirror);
            printWriter.println("mWindowManagerMirroring=" + this.mIsWindowManagerMirroring);
            printWriter.println("mRequestedRefreshRate=" + this.mRequestedRefreshRate);
        }

        @Override // com.android.server.display.DisplayDevice
        public DisplayDeviceInfo getDisplayDeviceInfoLocked() {
            if (this.mInfo == null) {
                DisplayDeviceInfo displayDeviceInfo = new DisplayDeviceInfo();
                this.mInfo = displayDeviceInfo;
                displayDeviceInfo.name = this.mName;
                displayDeviceInfo.uniqueId = getUniqueId();
                DisplayDeviceInfo displayDeviceInfo2 = this.mInfo;
                displayDeviceInfo2.width = this.mWidth;
                displayDeviceInfo2.height = this.mHeight;
                displayDeviceInfo2.modeId = this.mMode.getModeId();
                this.mInfo.renderFrameRate = this.mMode.getRefreshRate();
                this.mInfo.defaultModeId = this.mMode.getModeId();
                DisplayDeviceInfo displayDeviceInfo3 = this.mInfo;
                displayDeviceInfo3.supportedModes = new Display.Mode[]{this.mMode};
                int i = this.mDensityDpi;
                displayDeviceInfo3.densityDpi = i;
                displayDeviceInfo3.xDpi = i;
                displayDeviceInfo3.yDpi = i;
                displayDeviceInfo3.presentationDeadlineNanos = 1000000000 / ((int) getRefreshRate());
                DisplayDeviceInfo displayDeviceInfo4 = this.mInfo;
                displayDeviceInfo4.flags = 0;
                int i2 = this.mFlags;
                if ((i2 & 1) == 0) {
                    displayDeviceInfo4.flags = 0 | 48;
                }
                if ((i2 & 16) != 0) {
                    displayDeviceInfo4.flags &= -33;
                } else {
                    int i3 = displayDeviceInfo4.flags | 128;
                    displayDeviceInfo4.flags = i3;
                    if ((i2 & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0) {
                        displayDeviceInfo4.flags = i3 | 16384;
                    }
                }
                if ((i2 & 32768) != 0) {
                    displayDeviceInfo4.flags |= 262144;
                }
                if ((i2 & 4) != 0) {
                    displayDeviceInfo4.flags |= 4;
                }
                if ((i2 & 2) != 0) {
                    displayDeviceInfo4.flags |= 64;
                    if ((i2 & 1) != 0 && "portrait".equals(SystemProperties.get("persist.demo.remoterotation"))) {
                        this.mInfo.rotation = 3;
                    }
                }
                int i4 = this.mFlags;
                if ((i4 & 32) != 0) {
                    this.mInfo.flags |= 512;
                }
                if ((i4 & 128) != 0) {
                    this.mInfo.flags |= 2;
                }
                if ((i4 & 256) != 0) {
                    this.mInfo.flags |= 1024;
                }
                if ((i4 & 512) != 0) {
                    this.mInfo.flags |= IInstalld.FLAG_USE_QUOTA;
                }
                if ((i4 & 1024) != 0) {
                    this.mInfo.flags |= IInstalld.FLAG_FORCE;
                }
                if ((i4 & IInstalld.FLAG_USE_QUOTA) != 0) {
                    DisplayDeviceInfo displayDeviceInfo5 = this.mInfo;
                    int i5 = displayDeviceInfo5.flags;
                    if ((i5 & 16384) != 0 || (i4 & 32768) != 0) {
                        displayDeviceInfo5.flags = i5 | 32768;
                    } else {
                        Slog.w("VirtualDisplayAdapter", "Ignoring VIRTUAL_DISPLAY_FLAG_ALWAYS_UNLOCKED as it requires VIRTUAL_DISPLAY_FLAG_DEVICE_DISPLAY_GROUP or VIRTUAL_DISPLAY_FLAG_OWN_DISPLAY_GROUP.");
                    }
                }
                int i6 = this.mFlags;
                if ((i6 & IInstalld.FLAG_FORCE) != 0) {
                    this.mInfo.flags |= 65536;
                }
                if ((i6 & 16384) != 0) {
                    if ((i6 & 1024) != 0) {
                        this.mInfo.flags |= IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
                    } else {
                        Slog.w("VirtualDisplayAdapter", "Ignoring VIRTUAL_DISPLAY_FLAG_OWN_FOCUS as it requires VIRTUAL_DISPLAY_FLAG_TRUSTED.");
                    }
                }
                int i7 = this.mFlags;
                if ((i7 & 65536) != 0) {
                    if ((i7 & 1024) != 0 && (i7 & 16384) != 0) {
                        this.mInfo.flags |= 524288;
                    } else {
                        Slog.w("VirtualDisplayAdapter", "Ignoring VIRTUAL_DISPLAY_FLAG_STEAL_TOP_FOCUS_DISABLED as it requires VIRTUAL_DISPLAY_FLAG_OWN_FOCUS which requires VIRTUAL_DISPLAY_FLAG_TRUSTED.");
                    }
                }
                DisplayDeviceInfo displayDeviceInfo6 = this.mInfo;
                displayDeviceInfo6.type = 5;
                displayDeviceInfo6.touch = (this.mFlags & 64) == 0 ? 0 : 3;
                displayDeviceInfo6.state = this.mIsDisplayOn ? 2 : 1;
                displayDeviceInfo6.ownerUid = this.mOwnerUid;
                displayDeviceInfo6.ownerPackageName = this.mOwnerPackageName;
                displayDeviceInfo6.displayShape = DisplayShape.createDefaultDisplayShape(displayDeviceInfo6.width, displayDeviceInfo6.height, false);
            }
            return this.mInfo;
        }

        public final float getRefreshRate() {
            float f = this.mRequestedRefreshRate;
            if (f != 0.0f) {
                return f;
            }
            return 60.0f;
        }
    }

    /* loaded from: classes.dex */
    public static class Callback extends Handler {
        public final IVirtualDisplayCallback mCallback;

        public Callback(IVirtualDisplayCallback iVirtualDisplayCallback, Handler handler) {
            super(handler.getLooper());
            this.mCallback = iVirtualDisplayCallback;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            try {
                int i = message.what;
                if (i == 0) {
                    this.mCallback.onPaused();
                } else if (i == 1) {
                    this.mCallback.onResumed();
                } else if (i == 2) {
                    this.mCallback.onStopped();
                }
            } catch (RemoteException e) {
                Slog.w("VirtualDisplayAdapter", "Failed to notify listener of virtual display event.", e);
            }
        }

        public void dispatchDisplayPaused() {
            sendEmptyMessage(0);
        }

        public void dispatchDisplayResumed() {
            sendEmptyMessage(1);
        }

        public void dispatchDisplayStopped() {
            sendEmptyMessage(2);
        }
    }

    /* loaded from: classes.dex */
    public final class MediaProjectionCallback extends IMediaProjectionCallback.Stub {
        public IBinder mAppToken;

        public void onCapturedContentResize(int i, int i2) {
        }

        public void onCapturedContentVisibilityChanged(boolean z) {
        }

        public MediaProjectionCallback(IBinder iBinder) {
            this.mAppToken = iBinder;
        }

        public void onStop() {
            synchronized (VirtualDisplayAdapter.this.getSyncRoot()) {
                VirtualDisplayAdapter.this.handleMediaProjectionStoppedLocked(this.mAppToken);
            }
        }
    }
}
