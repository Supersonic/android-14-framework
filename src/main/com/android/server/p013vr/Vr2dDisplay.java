package com.android.server.p013vr;

import android.app.ActivityManagerInternal;
import android.app.Vr2dDisplayProperties;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.hardware.display.VirtualDisplayConfig;
import android.media.ImageReader;
import android.os.Handler;
import android.os.RemoteException;
import android.service.vr.IPersistentVrStateCallbacks;
import android.service.vr.IVrManager;
import android.util.Log;
import android.view.Surface;
import com.android.server.LocalServices;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.WindowManagerInternal;
/* renamed from: com.android.server.vr.Vr2dDisplay */
/* loaded from: classes2.dex */
public class Vr2dDisplay {
    public final ActivityManagerInternal mActivityManagerInternal;
    public final DisplayManager mDisplayManager;
    public ImageReader mImageReader;
    public boolean mIsPersistentVrModeEnabled;
    public boolean mIsVrModeOverrideEnabled;
    public Runnable mStopVDRunnable;
    public Surface mSurface;
    public VirtualDisplay mVirtualDisplay;
    public final IVrManager mVrManager;
    public final WindowManagerInternal mWindowManagerInternal;
    public final Object mVdLock = new Object();
    public final Handler mHandler = new Handler();
    public final IPersistentVrStateCallbacks mVrStateCallbacks = new IPersistentVrStateCallbacks.Stub() { // from class: com.android.server.vr.Vr2dDisplay.1
        public void onPersistentVrStateChanged(boolean z) {
            if (z != Vr2dDisplay.this.mIsPersistentVrModeEnabled) {
                Vr2dDisplay.this.mIsPersistentVrModeEnabled = z;
                Vr2dDisplay.this.updateVirtualDisplay();
            }
        }
    };
    public boolean mIsVirtualDisplayAllowed = true;
    public boolean mBootsToVr = false;
    public int mVirtualDisplayWidth = 1400;
    public int mVirtualDisplayHeight = 1800;
    public int mVirtualDisplayDpi = 320;

    public final void startDebugOnlyBroadcastReceiver(Context context) {
    }

    public Vr2dDisplay(DisplayManager displayManager, ActivityManagerInternal activityManagerInternal, WindowManagerInternal windowManagerInternal, IVrManager iVrManager) {
        this.mDisplayManager = displayManager;
        this.mActivityManagerInternal = activityManagerInternal;
        this.mWindowManagerInternal = windowManagerInternal;
        this.mVrManager = iVrManager;
    }

    public void init(Context context, boolean z) {
        startVrModeListener();
        startDebugOnlyBroadcastReceiver(context);
        this.mBootsToVr = z;
        if (z) {
            updateVirtualDisplay();
        }
    }

    public final void updateVirtualDisplay() {
        if (shouldRunVirtualDisplay()) {
            Log.i("Vr2dDisplay", "Attempting to start virtual display");
            startVirtualDisplay();
            return;
        }
        stopVirtualDisplay();
    }

    public final void startVrModeListener() {
        IVrManager iVrManager = this.mVrManager;
        if (iVrManager != null) {
            try {
                iVrManager.registerPersistentVrStateListener(this.mVrStateCallbacks);
            } catch (RemoteException e) {
                Log.e("Vr2dDisplay", "Could not register VR State listener.", e);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0074 A[Catch: all -> 0x00a3, TryCatch #0 {, blocks: (B:4:0x0003, B:9:0x0018, B:11:0x006d, B:13:0x0074, B:17:0x0080, B:20:0x0086, B:22:0x008a, B:23:0x009e, B:24:0x00a1, B:14:0x0077, B:16:0x007e, B:10:0x0046), top: B:29:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0077 A[Catch: all -> 0x00a3, TryCatch #0 {, blocks: (B:4:0x0003, B:9:0x0018, B:11:0x006d, B:13:0x0074, B:17:0x0080, B:20:0x0086, B:22:0x008a, B:23:0x009e, B:24:0x00a1, B:14:0x0077, B:16:0x007e, B:10:0x0046), top: B:29:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void setVirtualDisplayProperties(Vr2dDisplayProperties vr2dDisplayProperties) {
        boolean z;
        VirtualDisplay virtualDisplay;
        synchronized (this.mVdLock) {
            int width = vr2dDisplayProperties.getWidth();
            int height = vr2dDisplayProperties.getHeight();
            int dpi = vr2dDisplayProperties.getDpi();
            if (width >= 1 && height >= 1 && dpi >= 1) {
                Log.i("Vr2dDisplay", "Setting width/height/dpi to " + width + "," + height + "," + dpi);
                this.mVirtualDisplayWidth = width;
                this.mVirtualDisplayHeight = height;
                this.mVirtualDisplayDpi = dpi;
                z = true;
                if ((vr2dDisplayProperties.getAddedFlags() & 1) != 1) {
                    this.mIsVirtualDisplayAllowed = true;
                } else if ((vr2dDisplayProperties.getRemovedFlags() & 1) == 1) {
                    this.mIsVirtualDisplayAllowed = false;
                }
                virtualDisplay = this.mVirtualDisplay;
                if (virtualDisplay != null && z && this.mIsVirtualDisplayAllowed) {
                    virtualDisplay.resize(this.mVirtualDisplayWidth, this.mVirtualDisplayHeight, this.mVirtualDisplayDpi);
                    ImageReader imageReader = this.mImageReader;
                    this.mImageReader = null;
                    startImageReader();
                    imageReader.close();
                }
                updateVirtualDisplay();
            }
            Log.i("Vr2dDisplay", "Ignoring Width/Height/Dpi values of " + width + "," + height + "," + dpi);
            z = false;
            if ((vr2dDisplayProperties.getAddedFlags() & 1) != 1) {
            }
            virtualDisplay = this.mVirtualDisplay;
            if (virtualDisplay != null) {
                virtualDisplay.resize(this.mVirtualDisplayWidth, this.mVirtualDisplayHeight, this.mVirtualDisplayDpi);
                ImageReader imageReader2 = this.mImageReader;
                this.mImageReader = null;
                startImageReader();
                imageReader2.close();
            }
            updateVirtualDisplay();
        }
    }

    public int getVirtualDisplayId() {
        synchronized (this.mVdLock) {
            VirtualDisplay virtualDisplay = this.mVirtualDisplay;
            if (virtualDisplay != null) {
                return virtualDisplay.getDisplay().getDisplayId();
            }
            return -1;
        }
    }

    public final void startVirtualDisplay() {
        if (this.mDisplayManager == null) {
            Log.w("Vr2dDisplay", "Cannot create virtual display because mDisplayManager == null");
            return;
        }
        synchronized (this.mVdLock) {
            if (this.mVirtualDisplay != null) {
                Log.i("Vr2dDisplay", "VD already exists, ignoring request");
                return;
            }
            VirtualDisplayConfig.Builder builder = new VirtualDisplayConfig.Builder("VR 2D Display", this.mVirtualDisplayWidth, this.mVirtualDisplayHeight, this.mVirtualDisplayDpi);
            builder.setUniqueId("277f1a09-b88d-4d1e-8716-796f114d080b");
            builder.setFlags(1485);
            VirtualDisplay createVirtualDisplay = this.mDisplayManager.createVirtualDisplay(null, builder.build(), null, null, null);
            this.mVirtualDisplay = createVirtualDisplay;
            if (createVirtualDisplay != null) {
                updateDisplayId(createVirtualDisplay.getDisplay().getDisplayId());
                startImageReader();
                Log.i("Vr2dDisplay", "VD created: " + this.mVirtualDisplay);
                return;
            }
            Log.w("Vr2dDisplay", "Virtual display id is null after createVirtualDisplay");
            updateDisplayId(-1);
        }
    }

    public final void updateDisplayId(int i) {
        ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).setVr2dDisplayId(i);
        this.mWindowManagerInternal.setVr2dDisplayId(i);
    }

    public final void stopVirtualDisplay() {
        if (this.mStopVDRunnable == null) {
            this.mStopVDRunnable = new Runnable() { // from class: com.android.server.vr.Vr2dDisplay.3
                @Override // java.lang.Runnable
                public void run() {
                    if (Vr2dDisplay.this.shouldRunVirtualDisplay()) {
                        Log.i("Vr2dDisplay", "Virtual Display destruction stopped: VrMode is back on.");
                        return;
                    }
                    Log.i("Vr2dDisplay", "Stopping Virtual Display");
                    synchronized (Vr2dDisplay.this.mVdLock) {
                        Vr2dDisplay.this.updateDisplayId(-1);
                        Vr2dDisplay.this.setSurfaceLocked(null);
                        if (Vr2dDisplay.this.mVirtualDisplay != null) {
                            Vr2dDisplay.this.mVirtualDisplay.release();
                            Vr2dDisplay.this.mVirtualDisplay = null;
                        }
                        Vr2dDisplay.this.stopImageReader();
                    }
                }
            };
        }
        this.mHandler.removeCallbacks(this.mStopVDRunnable);
        this.mHandler.postDelayed(this.mStopVDRunnable, 2000L);
    }

    public final void setSurfaceLocked(Surface surface) {
        if (this.mSurface != surface) {
            if (surface == null || surface.isValid()) {
                Log.i("Vr2dDisplay", "Setting the new surface from " + this.mSurface + " to " + surface);
                VirtualDisplay virtualDisplay = this.mVirtualDisplay;
                if (virtualDisplay != null) {
                    virtualDisplay.setSurface(surface);
                }
                Surface surface2 = this.mSurface;
                if (surface2 != null) {
                    surface2.release();
                }
                this.mSurface = surface;
            }
        }
    }

    public final void startImageReader() {
        if (this.mImageReader == null) {
            this.mImageReader = ImageReader.newInstance(this.mVirtualDisplayWidth, this.mVirtualDisplayHeight, 1, 2);
            Log.i("Vr2dDisplay", "VD startImageReader: res = " + this.mVirtualDisplayWidth + "X" + this.mVirtualDisplayHeight + ", dpi = " + this.mVirtualDisplayDpi);
        }
        synchronized (this.mVdLock) {
            setSurfaceLocked(this.mImageReader.getSurface());
        }
    }

    public final void stopImageReader() {
        ImageReader imageReader = this.mImageReader;
        if (imageReader != null) {
            imageReader.close();
            this.mImageReader = null;
        }
    }

    public final boolean shouldRunVirtualDisplay() {
        return this.mIsVirtualDisplayAllowed && (this.mBootsToVr || this.mIsPersistentVrModeEnabled || this.mIsVrModeOverrideEnabled);
    }
}
