package com.android.server.display.color;

import android.content.p000pm.PackageManagerInternal;
import android.os.ShellCommand;
import com.android.server.LocalServices;
/* loaded from: classes.dex */
public class ColorDisplayShellCommand extends ShellCommand {
    public final ColorDisplayService mService;

    public ColorDisplayShellCommand(ColorDisplayService colorDisplayService) {
        this.mService = colorDisplayService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        if (str.equals("set-layer-saturation")) {
            return setLayerSaturation();
        }
        if (str.equals("set-saturation")) {
            return setSaturation();
        }
        return handleDefaultCommands(str);
    }

    public final int setSaturation() {
        int level = getLevel();
        if (level == -1) {
            return -1;
        }
        this.mService.setSaturationLevelInternal(level);
        return 0;
    }

    public final int setLayerSaturation() {
        int level = getLevel();
        if (level == -1) {
            return -1;
        }
        String packageName = getPackageName();
        if (packageName == null) {
            getErrPrintWriter().println("Error: CALLER_PACKAGE must be an installed package name");
            return -1;
        }
        String packageName2 = getPackageName();
        if (packageName2 == null) {
            getErrPrintWriter().println("Error: TARGET_PACKAGE must be an installed package name");
            return -1;
        }
        this.mService.setAppSaturationLevelInternal(packageName, packageName2, level);
        return 0;
    }

    public final String getPackageName() {
        String nextArg = getNextArg();
        if (((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getPackage(nextArg) == null) {
            return null;
        }
        return nextArg;
    }

    public final int getLevel() {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: Required argument LEVEL is unspecified");
            return -1;
        }
        try {
            int parseInt = Integer.parseInt(nextArg);
            if (parseInt < 0 || parseInt > 100) {
                getErrPrintWriter().println("Error: LEVEL argument must be an integer between 0 and 100");
                return -1;
            }
            return parseInt;
        } catch (NumberFormatException unused) {
            getErrPrintWriter().println("Error: LEVEL argument is not an integer");
            return -1;
        }
    }

    public void onHelp() {
        getOutPrintWriter().print("usage: cmd color_display SUBCOMMAND [ARGS]\n    help\n      Shows this message.\n    set-saturation LEVEL\n      Sets the device saturation to the given LEVEL, 0-100 inclusive.\n    set-layer-saturation LEVEL CALLER_PACKAGE TARGET_PACKAGE\n      Sets the saturation LEVEL for all layers of the TARGET_PACKAGE, attributed\n      to the CALLER_PACKAGE. The lowest LEVEL from any CALLER_PACKAGE is applied.\n");
    }
}
