package com.android.server.display.state;

import android.hardware.display.DisplayManagerInternal;
import android.util.IndentingPrintWriter;
import com.android.server.display.DisplayPowerProximityStateController;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class DisplayStateController {
    public DisplayPowerProximityStateController mDisplayPowerProximityStateController;
    public boolean mPerformScreenOffTransition = false;

    public DisplayStateController(DisplayPowerProximityStateController displayPowerProximityStateController) {
        this.mDisplayPowerProximityStateController = displayPowerProximityStateController;
    }

    public int updateDisplayState(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest, boolean z, boolean z2) {
        int i;
        this.mPerformScreenOffTransition = false;
        int i2 = displayPowerRequest.policy;
        if (i2 == 0) {
            this.mPerformScreenOffTransition = true;
            i = 1;
        } else if (i2 != 1) {
            i = 2;
        } else {
            i = displayPowerRequest.dozeScreenState;
            if (i == 0) {
                i = 3;
            }
        }
        this.mDisplayPowerProximityStateController.updateProximityState(displayPowerRequest, i);
        if (!z || z2 || this.mDisplayPowerProximityStateController.isScreenOffBecauseOfProximity()) {
            return 1;
        }
        return i;
    }

    public boolean shouldPerformScreenOffTransition() {
        return this.mPerformScreenOffTransition;
    }

    public void dumpsys(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("DisplayStateController:");
        printWriter.println("  mPerformScreenOffTransition:" + this.mPerformScreenOffTransition);
        PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, " ");
        DisplayPowerProximityStateController displayPowerProximityStateController = this.mDisplayPowerProximityStateController;
        if (displayPowerProximityStateController != null) {
            displayPowerProximityStateController.dumpLocal(indentingPrintWriter);
        }
    }
}
