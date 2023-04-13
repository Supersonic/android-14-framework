package com.android.server.p014wm;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.view.InsetsState;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import java.util.ArrayList;
/* renamed from: com.android.server.wm.TrustedOverlayHost */
/* loaded from: classes2.dex */
public class TrustedOverlayHost {
    public final ArrayList<SurfaceControlViewHost.SurfacePackage> mOverlays = new ArrayList<>();
    public SurfaceControl mSurfaceControl;
    public final WindowManagerService mWmService;

    public TrustedOverlayHost(WindowManagerService windowManagerService) {
        this.mWmService = windowManagerService;
    }

    public void requireOverlaySurfaceControl() {
        if (this.mSurfaceControl == null) {
            this.mSurfaceControl = this.mWmService.makeSurfaceBuilder(null).setContainerLayer().setHidden(true).setName("Overlay Host Leash").build();
            this.mWmService.mTransactionFactory.get().setTrustedOverlay(this.mSurfaceControl, true).apply();
        }
    }

    public void setParent(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        SurfaceControl surfaceControl2 = this.mSurfaceControl;
        if (surfaceControl2 == null) {
            return;
        }
        transaction.reparent(surfaceControl2, surfaceControl);
        if (surfaceControl != null) {
            transaction.show(this.mSurfaceControl);
        } else {
            transaction.hide(this.mSurfaceControl);
        }
    }

    public void setLayer(SurfaceControl.Transaction transaction, int i) {
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl != null) {
            transaction.setLayer(surfaceControl, i);
        }
    }

    public void setVisibility(SurfaceControl.Transaction transaction, boolean z) {
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl != null) {
            transaction.setVisibility(surfaceControl, z);
        }
    }

    public void addOverlay(SurfaceControlViewHost.SurfacePackage surfacePackage, SurfaceControl surfaceControl) {
        requireOverlaySurfaceControl();
        this.mOverlays.add(surfacePackage);
        this.mWmService.mEmbeddedWindowController.setIsOverlay(surfacePackage.getInputToken());
        SurfaceControl.Transaction transaction = this.mWmService.mTransactionFactory.get();
        transaction.reparent(surfacePackage.getSurfaceControl(), this.mSurfaceControl).show(surfacePackage.getSurfaceControl());
        setParent(transaction, surfaceControl);
        transaction.apply();
    }

    public boolean removeOverlay(SurfaceControlViewHost.SurfacePackage surfacePackage) {
        SurfaceControl.Transaction transaction = this.mWmService.mTransactionFactory.get();
        for (int size = this.mOverlays.size() - 1; size >= 0; size--) {
            SurfaceControlViewHost.SurfacePackage surfacePackage2 = this.mOverlays.get(size);
            if (surfacePackage2.getSurfaceControl().isSameSurface(surfacePackage.getSurfaceControl())) {
                this.mOverlays.remove(size);
                transaction.reparent(surfacePackage2.getSurfaceControl(), null);
                surfacePackage2.release();
            }
        }
        transaction.apply();
        return this.mOverlays.size() > 0;
    }

    public void dispatchConfigurationChanged(Configuration configuration) {
        for (int size = this.mOverlays.size() - 1; size >= 0; size--) {
            SurfaceControlViewHost.SurfacePackage surfacePackage = this.mOverlays.get(size);
            try {
                surfacePackage.getRemoteInterface().onConfigurationChanged(configuration);
            } catch (Exception unused) {
                removeOverlay(surfacePackage);
            }
        }
    }

    public final void dispatchDetachedFromWindow() {
        for (int size = this.mOverlays.size() - 1; size >= 0; size--) {
            SurfaceControlViewHost.SurfacePackage surfacePackage = this.mOverlays.get(size);
            try {
                surfacePackage.getRemoteInterface().onDispatchDetachedFromWindow();
            } catch (Exception unused) {
            }
            surfacePackage.release();
        }
    }

    public void dispatchInsetsChanged(InsetsState insetsState, Rect rect) {
        for (int size = this.mOverlays.size() - 1; size >= 0; size--) {
            try {
                this.mOverlays.get(size).getRemoteInterface().onInsetsChanged(insetsState, rect);
            } catch (Exception unused) {
            }
        }
    }

    public void release() {
        dispatchDetachedFromWindow();
        this.mOverlays.clear();
        this.mWmService.mTransactionFactory.get().remove(this.mSurfaceControl).apply();
        this.mSurfaceControl = null;
    }
}
