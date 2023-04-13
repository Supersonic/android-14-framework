package com.android.server.accessibility.magnification;

import android.accessibilityservice.MagnificationConfig;
import android.graphics.Region;
import android.view.Display;
import android.view.accessibility.MagnificationAnimationCallback;
import java.io.PrintWriter;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class MagnificationProcessor {
    public final MagnificationController mController;

    public MagnificationProcessor(MagnificationController magnificationController) {
        this.mController = magnificationController;
    }

    public MagnificationConfig getMagnificationConfig(int i) {
        int controllingMode = getControllingMode(i);
        MagnificationConfig.Builder builder = new MagnificationConfig.Builder();
        if (controllingMode == 1) {
            FullScreenMagnificationController fullScreenMagnificationController = this.mController.getFullScreenMagnificationController();
            builder.setMode(controllingMode).setActivated(this.mController.isActivated(i, 1)).setScale(fullScreenMagnificationController.getScale(i)).setCenterX(fullScreenMagnificationController.getCenterX(i)).setCenterY(fullScreenMagnificationController.getCenterY(i));
        } else if (controllingMode == 2) {
            WindowMagnificationManager windowMagnificationMgr = this.mController.getWindowMagnificationMgr();
            builder.setMode(controllingMode).setActivated(this.mController.isActivated(i, 2)).setScale(windowMagnificationMgr.getScale(i)).setCenterX(windowMagnificationMgr.getCenterX(i)).setCenterY(windowMagnificationMgr.getCenterY(i));
        } else {
            builder.setActivated(false);
        }
        return builder.build();
    }

    public boolean setMagnificationConfig(int i, MagnificationConfig magnificationConfig, boolean z, int i2) {
        if (transitionModeIfNeeded(i, magnificationConfig, z, i2)) {
            return true;
        }
        int mode = magnificationConfig.getMode();
        if (mode == 0) {
            mode = getControllingMode(i);
        }
        boolean isActivated = magnificationConfig.isActivated();
        if (mode == 1) {
            if (isActivated) {
                return setScaleAndCenterForFullScreenMagnification(i, magnificationConfig.getScale(), magnificationConfig.getCenterX(), magnificationConfig.getCenterY(), z, i2);
            }
            return resetFullscreenMagnification(i, z);
        } else if (mode == 2) {
            if (isActivated) {
                return this.mController.getWindowMagnificationMgr().enableWindowMagnification(i, magnificationConfig.getScale(), magnificationConfig.getCenterX(), magnificationConfig.getCenterY(), z ? MagnificationAnimationCallback.STUB_ANIMATION_CALLBACK : null, i2);
            }
            return this.mController.getWindowMagnificationMgr().disableWindowMagnification(i, false);
        } else {
            return false;
        }
    }

    public final boolean setScaleAndCenterForFullScreenMagnification(int i, float f, float f2, float f3, boolean z, int i2) {
        if (!isRegistered(i)) {
            register(i);
        }
        return this.mController.getFullScreenMagnificationController().setScaleAndCenter(i, f, f2, f3, z, i2);
    }

    public final boolean transitionModeIfNeeded(int i, MagnificationConfig magnificationConfig, boolean z, int i2) {
        int controllingMode = getControllingMode(i);
        if (magnificationConfig.getMode() == 0) {
            return false;
        }
        if (controllingMode != magnificationConfig.getMode() || this.mController.hasDisableMagnificationCallback(i)) {
            this.mController.transitionMagnificationConfigMode(i, magnificationConfig, z, i2);
            return true;
        }
        return false;
    }

    public float getScale(int i) {
        return this.mController.getFullScreenMagnificationController().getScale(i);
    }

    public float getCenterX(int i, boolean z) {
        boolean registerDisplayMagnificationIfNeeded = registerDisplayMagnificationIfNeeded(i, z);
        try {
            return this.mController.getFullScreenMagnificationController().getCenterX(i);
        } finally {
            if (registerDisplayMagnificationIfNeeded) {
                unregister(i);
            }
        }
    }

    public float getCenterY(int i, boolean z) {
        boolean registerDisplayMagnificationIfNeeded = registerDisplayMagnificationIfNeeded(i, z);
        try {
            return this.mController.getFullScreenMagnificationController().getCenterY(i);
        } finally {
            if (registerDisplayMagnificationIfNeeded) {
                unregister(i);
            }
        }
    }

    public void getCurrentMagnificationRegion(int i, Region region, boolean z) {
        int controllingMode = getControllingMode(i);
        if (controllingMode == 1) {
            getFullscreenMagnificationRegion(i, region, z);
        } else if (controllingMode == 2) {
            this.mController.getWindowMagnificationMgr().getMagnificationSourceBounds(i, region);
        }
    }

    public void getFullscreenMagnificationRegion(int i, Region region, boolean z) {
        boolean registerDisplayMagnificationIfNeeded = registerDisplayMagnificationIfNeeded(i, z);
        try {
            this.mController.getFullScreenMagnificationController().getMagnificationRegion(i, region);
        } finally {
            if (registerDisplayMagnificationIfNeeded) {
                unregister(i);
            }
        }
    }

    public boolean resetCurrentMagnification(int i, boolean z) {
        int controllingMode = getControllingMode(i);
        if (controllingMode == 1) {
            return this.mController.getFullScreenMagnificationController().reset(i, z);
        }
        if (controllingMode == 2) {
            return this.mController.getWindowMagnificationMgr().disableWindowMagnification(i, false, z ? MagnificationAnimationCallback.STUB_ANIMATION_CALLBACK : null);
        }
        return false;
    }

    public boolean resetFullscreenMagnification(int i, boolean z) {
        return this.mController.getFullScreenMagnificationController().reset(i, z);
    }

    public void resetAllIfNeeded(int i) {
        this.mController.getFullScreenMagnificationController().resetAllIfNeeded(i);
        this.mController.getWindowMagnificationMgr().resetAllIfNeeded(i);
    }

    public boolean isMagnifying(int i) {
        int controllingMode = getControllingMode(i);
        if (controllingMode == 1) {
            return this.mController.getFullScreenMagnificationController().isActivated(i);
        }
        if (controllingMode == 2) {
            return this.mController.getWindowMagnificationMgr().isWindowMagnifierEnabled(i);
        }
        return false;
    }

    public int getControllingMode(int i) {
        if (this.mController.isActivated(i, 2)) {
            return 2;
        }
        return (!this.mController.isActivated(i, 1) && this.mController.getLastMagnificationActivatedMode(i) == 2) ? 2 : 1;
    }

    public final boolean registerDisplayMagnificationIfNeeded(int i, boolean z) {
        if (isRegistered(i) || !z) {
            return false;
        }
        register(i);
        return true;
    }

    public final boolean isRegistered(int i) {
        return this.mController.getFullScreenMagnificationController().isRegistered(i);
    }

    public final void register(int i) {
        this.mController.getFullScreenMagnificationController().register(i);
    }

    public final void unregister(int i) {
        this.mController.getFullScreenMagnificationController().unregister(i);
    }

    public void dump(PrintWriter printWriter, ArrayList<Display> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            int displayId = arrayList.get(i).getDisplayId();
            MagnificationConfig magnificationConfig = getMagnificationConfig(displayId);
            printWriter.println("Magnifier on display#" + displayId);
            printWriter.append((CharSequence) ("    " + magnificationConfig)).println();
            Region region = new Region();
            getCurrentMagnificationRegion(displayId, region, true);
            if (!region.isEmpty()) {
                printWriter.append("    Magnification region=").append((CharSequence) region.toString()).println();
            }
            printWriter.append((CharSequence) ("    IdOfLastServiceToMagnify=" + getIdOfLastServiceToMagnify(magnificationConfig.getMode(), displayId))).println();
            dumpTrackingTypingFocusEnabledState(printWriter, displayId, magnificationConfig.getMode());
        }
        printWriter.append((CharSequence) ("    SupportWindowMagnification=" + this.mController.supportWindowMagnification())).println();
        printWriter.append((CharSequence) ("    WindowMagnificationConnectionState=" + this.mController.getWindowMagnificationMgr().getConnectionState())).println();
    }

    public final int getIdOfLastServiceToMagnify(int i, int i2) {
        if (i == 1) {
            return this.mController.getFullScreenMagnificationController().getIdOfLastServiceToMagnify(i2);
        }
        return this.mController.getWindowMagnificationMgr().getIdOfLastServiceToMagnify(i2);
    }

    public final void dumpTrackingTypingFocusEnabledState(PrintWriter printWriter, int i, int i2) {
        if (i2 == 2) {
            printWriter.append((CharSequence) ("    TrackingTypingFocusEnabled=" + this.mController.getWindowMagnificationMgr().isTrackingTypingFocusEnabled(i))).println();
        }
    }
}
