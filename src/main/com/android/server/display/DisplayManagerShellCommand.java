package com.android.server.display;

import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.ShellCommand;
import android.util.Slog;
import android.view.Display;
import java.io.PrintWriter;
import java.util.Arrays;
/* loaded from: classes.dex */
public class DisplayManagerShellCommand extends ShellCommand {
    public final DisplayManagerService mService;

    public DisplayManagerShellCommand(DisplayManagerService displayManagerService) {
        this.mService = displayManagerService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        getOutPrintWriter();
        char c = 65535;
        switch (str.hashCode()) {
            case -1505467592:
                if (str.equals("reset-brightness-configuration")) {
                    c = 0;
                    break;
                }
                break;
            case -1459563384:
                if (str.equals("get-displays")) {
                    c = 1;
                    break;
                }
                break;
            case -1021080420:
                if (str.equals("get-user-disabled-hdr-types")) {
                    c = 2;
                    break;
                }
                break;
            case -840680372:
                if (str.equals("undock")) {
                    c = 3;
                    break;
                }
                break;
            case -731435249:
                if (str.equals("dwb-logging-enable")) {
                    c = 4;
                    break;
                }
                break;
            case -687141135:
                if (str.equals("set-user-preferred-display-mode")) {
                    c = 5;
                    break;
                }
                break;
            case -601773083:
                if (str.equals("get-user-preferred-display-mode")) {
                    c = 6;
                    break;
                }
                break;
            case 3088947:
                if (str.equals("dock")) {
                    c = 7;
                    break;
                }
                break;
            case 483509981:
                if (str.equals("ab-logging-enable")) {
                    c = '\b';
                    break;
                }
                break;
            case 847215243:
                if (str.equals("dwb-set-cct")) {
                    c = '\t';
                    break;
                }
                break;
            case 1089842382:
                if (str.equals("ab-logging-disable")) {
                    c = '\n';
                    break;
                }
                break;
            case 1265268983:
                if (str.equals("get-active-display-mode-at-start")) {
                    c = 11;
                    break;
                }
                break;
            case 1428935945:
                if (str.equals("set-match-content-frame-rate-pref")) {
                    c = '\f';
                    break;
                }
                break;
            case 1604823708:
                if (str.equals("set-brightness")) {
                    c = '\r';
                    break;
                }
                break;
            case 1863255293:
                if (str.equals("get-match-content-frame-rate-pref")) {
                    c = 14;
                    break;
                }
                break;
            case 1873686952:
                if (str.equals("dmd-logging-disable")) {
                    c = 15;
                    break;
                }
                break;
            case 1894268611:
                if (str.equals("dmd-logging-enable")) {
                    c = 16;
                    break;
                }
                break;
            case 1928353192:
                if (str.equals("set-user-disabled-hdr-types")) {
                    c = 17;
                    break;
                }
                break;
            case 2076592732:
                if (str.equals("clear-user-preferred-display-mode")) {
                    c = 18;
                    break;
                }
                break;
            case 2081245916:
                if (str.equals("dwb-logging-disable")) {
                    c = 19;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return resetBrightnessConfiguration();
            case 1:
                return getDisplays();
            case 2:
                return getUserDisabledHdrTypes();
            case 3:
                return unsetDockedAndIdle();
            case 4:
                return setDisplayWhiteBalanceLoggingEnabled(true);
            case 5:
                return setUserPreferredDisplayMode();
            case 6:
                return getUserPreferredDisplayMode();
            case 7:
                return setDockedAndIdle();
            case '\b':
                return setAutoBrightnessLoggingEnabled(true);
            case '\t':
                return setAmbientColorTemperatureOverride();
            case '\n':
                return setAutoBrightnessLoggingEnabled(false);
            case 11:
                return getActiveDisplayModeAtStart();
            case '\f':
                return setMatchContentFrameRateUserPreference();
            case '\r':
                return setBrightness();
            case 14:
                return getMatchContentFrameRateUserPreference();
            case 15:
                return setDisplayModeDirectorLoggingEnabled(false);
            case 16:
                return setDisplayModeDirectorLoggingEnabled(true);
            case 17:
                return setUserDisabledHdrTypes();
            case 18:
                return clearUserPreferredDisplayMode();
            case 19:
                return setDisplayWhiteBalanceLoggingEnabled(false);
            default:
                return handleDefaultCommands(str);
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Display manager commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println();
        outPrintWriter.println("  set-brightness BRIGHTNESS");
        outPrintWriter.println("    Sets the current brightness to BRIGHTNESS (a number between 0 and 1).");
        outPrintWriter.println("  reset-brightness-configuration");
        outPrintWriter.println("    Reset the brightness to its default configuration.");
        outPrintWriter.println("  ab-logging-enable");
        outPrintWriter.println("    Enable auto-brightness logging.");
        outPrintWriter.println("  ab-logging-disable");
        outPrintWriter.println("    Disable auto-brightness logging.");
        outPrintWriter.println("  dwb-logging-enable");
        outPrintWriter.println("    Enable display white-balance logging.");
        outPrintWriter.println("  dwb-logging-disable");
        outPrintWriter.println("    Disable display white-balance logging.");
        outPrintWriter.println("  dmd-logging-enable");
        outPrintWriter.println("    Enable display mode director logging.");
        outPrintWriter.println("  dmd-logging-disable");
        outPrintWriter.println("    Disable display mode director logging.");
        outPrintWriter.println("  dwb-set-cct CCT");
        outPrintWriter.println("    Sets the ambient color temperature override to CCT (use -1 to disable).");
        outPrintWriter.println("  set-user-preferred-display-mode WIDTH HEIGHT REFRESH-RATE DISPLAY_ID (optional)");
        outPrintWriter.println("    Sets the user preferred display mode which has fields WIDTH, HEIGHT and REFRESH-RATE. If DISPLAY_ID is passed, the mode change is applied to displaywith id = DISPLAY_ID, else mode change is applied globally.");
        outPrintWriter.println("  clear-user-preferred-display-mode DISPLAY_ID (optional)");
        outPrintWriter.println("    Clears the user preferred display mode. If DISPLAY_ID is passed, the mode is cleared for  display with id = DISPLAY_ID, else mode is cleared globally.");
        outPrintWriter.println("  get-user-preferred-display-mode DISPLAY_ID (optional)");
        outPrintWriter.println("    Returns the user preferred display mode or null if no mode is set by user.If DISPLAY_ID is passed, the mode for display with id = DISPLAY_ID is returned, else global display mode is returned.");
        outPrintWriter.println("  get-active-display-mode-at-start DISPLAY_ID");
        outPrintWriter.println("    Returns the display mode which was found at boot time of display with id = DISPLAY_ID");
        outPrintWriter.println("  set-match-content-frame-rate-pref PREFERENCE");
        outPrintWriter.println("    Sets the match content frame rate preference as PREFERENCE ");
        outPrintWriter.println("  get-match-content-frame-rate-pref");
        outPrintWriter.println("    Returns the match content frame rate preference");
        outPrintWriter.println("  set-user-disabled-hdr-types TYPES...");
        outPrintWriter.println("    Sets the user disabled HDR types as TYPES");
        outPrintWriter.println("  get-user-disabled-hdr-types");
        outPrintWriter.println("    Returns the user disabled HDR types");
        outPrintWriter.println("  get-displays [CATEGORY]");
        outPrintWriter.println("    Returns the current displays. Can specify string category among");
        outPrintWriter.println("    DisplayManager.DISPLAY_CATEGORY_*; must use the actual string value.");
        outPrintWriter.println("  dock");
        outPrintWriter.println("    Sets brightness to docked + idle screen brightness mode");
        outPrintWriter.println("  undock");
        outPrintWriter.println("    Sets brightness to active (normal) screen brightness mode");
        outPrintWriter.println();
        Intent.printIntentArgsHelp(outPrintWriter, "");
    }

    public final int getDisplays() {
        Display[] displays = ((DisplayManager) this.mService.getContext().getSystemService(DisplayManager.class)).getDisplays(getNextArg());
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Displays:");
        for (int i = 0; i < displays.length; i++) {
            outPrintWriter.println("  " + displays[i]);
        }
        return 0;
    }

    public final int setBrightness() {
        float f;
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no brightness specified");
            return 1;
        }
        try {
            f = Float.parseFloat(nextArg);
        } catch (NumberFormatException unused) {
            f = -1.0f;
        }
        if (f < 0.0f || f > 1.0f) {
            getErrPrintWriter().println("Error: brightness should be a number between 0 and 1");
            return 1;
        }
        ((DisplayManager) this.mService.getContext().getSystemService(DisplayManager.class)).setBrightness(0, f);
        return 0;
    }

    public final int resetBrightnessConfiguration() {
        this.mService.resetBrightnessConfigurations();
        return 0;
    }

    public final int setAutoBrightnessLoggingEnabled(boolean z) {
        this.mService.setAutoBrightnessLoggingEnabled(z);
        return 0;
    }

    public final int setDisplayWhiteBalanceLoggingEnabled(boolean z) {
        this.mService.setDisplayWhiteBalanceLoggingEnabled(z);
        return 0;
    }

    public final int setDisplayModeDirectorLoggingEnabled(boolean z) {
        this.mService.setDisplayModeDirectorLoggingEnabled(z);
        return 0;
    }

    public final int setAmbientColorTemperatureOverride() {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no cct specified");
            return 1;
        }
        try {
            this.mService.setAmbientColorTemperatureOverride(Float.parseFloat(nextArg));
            return 0;
        } catch (NumberFormatException unused) {
            getErrPrintWriter().println("Error: cct should be a number");
            return 1;
        }
    }

    public final int setUserPreferredDisplayMode() {
        int parseInt;
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no width specified");
            return 1;
        }
        String nextArg2 = getNextArg();
        if (nextArg2 == null) {
            getErrPrintWriter().println("Error: no height specified");
            return 1;
        }
        String nextArg3 = getNextArg();
        if (nextArg3 == null) {
            getErrPrintWriter().println("Error: no refresh-rate specified");
            return 1;
        }
        try {
            int parseInt2 = Integer.parseInt(nextArg);
            int parseInt3 = Integer.parseInt(nextArg2);
            float parseFloat = Float.parseFloat(nextArg3);
            if ((parseInt2 < 0 || parseInt3 < 0) && parseFloat <= 0.0f) {
                getErrPrintWriter().println("Error: invalid value of resolution (width, height) and refresh rate");
                return 1;
            }
            String nextArg4 = getNextArg();
            if (nextArg4 != null) {
                try {
                    parseInt = Integer.parseInt(nextArg4);
                } catch (NumberFormatException unused) {
                    getErrPrintWriter().println("Error: invalid format of display ID");
                    return 1;
                }
            } else {
                parseInt = -1;
            }
            this.mService.setUserPreferredDisplayModeInternal(parseInt, new Display.Mode(parseInt2, parseInt3, parseFloat));
            return 0;
        } catch (NumberFormatException unused2) {
            getErrPrintWriter().println("Error: invalid format of width, height or refresh rate");
            return 1;
        }
    }

    public final int clearUserPreferredDisplayMode() {
        int parseInt;
        String nextArg = getNextArg();
        if (nextArg != null) {
            try {
                parseInt = Integer.parseInt(nextArg);
            } catch (NumberFormatException unused) {
                getErrPrintWriter().println("Error: invalid format of display ID");
                return 1;
            }
        } else {
            parseInt = -1;
        }
        this.mService.setUserPreferredDisplayModeInternal(parseInt, null);
        return 0;
    }

    public final int getUserPreferredDisplayMode() {
        int parseInt;
        String nextArg = getNextArg();
        if (nextArg != null) {
            try {
                parseInt = Integer.parseInt(nextArg);
            } catch (NumberFormatException unused) {
                getErrPrintWriter().println("Error: invalid format of display ID");
                return 1;
            }
        } else {
            parseInt = -1;
        }
        Display.Mode userPreferredDisplayModeInternal = this.mService.getUserPreferredDisplayModeInternal(parseInt);
        if (userPreferredDisplayModeInternal == null) {
            getOutPrintWriter().println("User preferred display mode: null");
            return 0;
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("User preferred display mode: " + userPreferredDisplayModeInternal.getPhysicalWidth() + " " + userPreferredDisplayModeInternal.getPhysicalHeight() + " " + userPreferredDisplayModeInternal.getRefreshRate());
        return 0;
    }

    public final int getActiveDisplayModeAtStart() {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no displayId specified");
            return 1;
        }
        try {
            Display.Mode activeDisplayModeAtStart = this.mService.getActiveDisplayModeAtStart(Integer.parseInt(nextArg));
            if (activeDisplayModeAtStart == null) {
                getOutPrintWriter().println("Boot display mode: null");
                return 0;
            }
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Boot display mode: " + activeDisplayModeAtStart.getPhysicalWidth() + " " + activeDisplayModeAtStart.getPhysicalHeight() + " " + activeDisplayModeAtStart.getRefreshRate());
            return 0;
        } catch (NumberFormatException unused) {
            getErrPrintWriter().println("Error: invalid displayId");
            return 1;
        }
    }

    public final int setMatchContentFrameRateUserPreference() {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no matchContentFrameRatePref specified");
            return 1;
        }
        try {
            int parseInt = Integer.parseInt(nextArg);
            if (parseInt < 0) {
                getErrPrintWriter().println("Error: invalid value of matchContentFrameRatePreference");
                return 1;
            }
            ((DisplayManager) this.mService.getContext().getSystemService(DisplayManager.class)).setRefreshRateSwitchingType(toRefreshRateSwitchingType(parseInt));
            return 0;
        } catch (NumberFormatException unused) {
            getErrPrintWriter().println("Error: invalid format of matchContentFrameRatePreference");
            return 1;
        }
    }

    public final int getMatchContentFrameRateUserPreference() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Match content frame rate type: " + ((DisplayManager) this.mService.getContext().getSystemService(DisplayManager.class)).getMatchContentFrameRateUserPreference());
        return 0;
    }

    public final int setUserDisabledHdrTypes() {
        String[] peekRemainingArgs = peekRemainingArgs();
        if (peekRemainingArgs == null) {
            getErrPrintWriter().println("Error: no userDisabledHdrTypes specified");
            return 1;
        }
        int[] iArr = new int[peekRemainingArgs.length];
        try {
            int length = peekRemainingArgs.length;
            int i = 0;
            int i2 = 0;
            while (i < length) {
                int i3 = i2 + 1;
                iArr[i2] = Integer.parseInt(peekRemainingArgs[i]);
                i++;
                i2 = i3;
            }
            ((DisplayManager) this.mService.getContext().getSystemService(DisplayManager.class)).setUserDisabledHdrTypes(iArr);
            return 0;
        } catch (NumberFormatException unused) {
            getErrPrintWriter().println("Error: invalid format of userDisabledHdrTypes");
            return 1;
        }
    }

    public final int getUserDisabledHdrTypes() {
        int[] userDisabledHdrTypes = ((DisplayManager) this.mService.getContext().getSystemService(DisplayManager.class)).getUserDisabledHdrTypes();
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("User disabled HDR types: " + Arrays.toString(userDisabledHdrTypes));
        return 0;
    }

    public final int toRefreshRateSwitchingType(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    Slog.e("DisplayManagerShellCommand", i + " is not a valid value of matchContentFrameRate type.");
                    return -1;
                }
                return 2;
            }
            return 1;
        }
        return 0;
    }

    public final int setDockedAndIdle() {
        this.mService.setDockedAndIdleEnabled(true, 0);
        return 0;
    }

    public final int unsetDockedAndIdle() {
        this.mService.setDockedAndIdleEnabled(false, 0);
        return 0;
    }
}
