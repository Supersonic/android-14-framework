package com.android.server.audio;

import android.media.AudioManager;
import android.os.ShellCommand;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class AudioManagerShellCommand extends ShellCommand {
    public final AudioService mService;

    public AudioManagerShellCommand(AudioService audioService) {
        this.mService = audioService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        getOutPrintWriter();
        char c = 65535;
        switch (str.hashCode()) {
            case -1873164504:
                if (str.equals("set-encoded-surround-mode")) {
                    c = 0;
                    break;
                }
                break;
            case -1340000401:
                if (str.equals("set-surround-format-enabled")) {
                    c = 1;
                    break;
                }
                break;
            case 937504014:
                if (str.equals("get-is-surround-format-enabled")) {
                    c = 2;
                    break;
                }
                break;
            case 1578511132:
                if (str.equals("get-encoded-surround-mode")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return setEncodedSurroundMode();
            case 1:
                return setSurroundFormatEnabled();
            case 2:
                return getIsSurroundFormatEnabled();
            case 3:
                return getEncodedSurroundMode();
            default:
                return 0;
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Audio manager commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println();
        outPrintWriter.println("  set-surround-format-enabled SURROUND_FORMAT IS_ENABLED");
        outPrintWriter.println("    Enables/disabled the SURROUND_FORMAT based on IS_ENABLED");
        outPrintWriter.println("  get-is-surround-format-enabled SURROUND_FORMAT");
        outPrintWriter.println("    Returns if the SURROUND_FORMAT is enabled");
        outPrintWriter.println("  set-encoded-surround-mode SURROUND_SOUND_MODE");
        outPrintWriter.println("    Sets the encoded surround sound mode to SURROUND_SOUND_MODE");
        outPrintWriter.println("  get-encoded-surround-mode");
        outPrintWriter.println("    Returns the encoded surround sound mode");
    }

    public final int setSurroundFormatEnabled() {
        String nextArg = getNextArg();
        String nextArg2 = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no surroundFormat specified");
            return 1;
        } else if (nextArg2 == null) {
            getErrPrintWriter().println("Error: no enabled value for surroundFormat specified");
            return 1;
        } else {
            try {
                int parseInt = Integer.parseInt(nextArg);
                boolean parseBoolean = Boolean.parseBoolean(nextArg2);
                if (parseInt < 0) {
                    getErrPrintWriter().println("Error: invalid value of surroundFormat");
                    return 1;
                }
                ((AudioManager) this.mService.mContext.getSystemService(AudioManager.class)).setSurroundFormatEnabled(parseInt, parseBoolean);
                return 0;
            } catch (NumberFormatException unused) {
                getErrPrintWriter().println("Error: wrong format specified for surroundFormat");
                return 1;
            }
        }
    }

    public final int getIsSurroundFormatEnabled() {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no surroundFormat specified");
            return 1;
        }
        try {
            int parseInt = Integer.parseInt(nextArg);
            if (parseInt < 0) {
                getErrPrintWriter().println("Error: invalid value of surroundFormat");
                return 1;
            }
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Value of enabled for " + parseInt + " is: " + ((AudioManager) this.mService.mContext.getSystemService(AudioManager.class)).isSurroundFormatEnabled(parseInt));
            return 0;
        } catch (NumberFormatException unused) {
            getErrPrintWriter().println("Error: wrong format specified for surroundFormat");
            return 1;
        }
    }

    public final int setEncodedSurroundMode() {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no encodedSurroundMode specified");
            return 1;
        }
        try {
            int parseInt = Integer.parseInt(nextArg);
            if (parseInt < 0) {
                getErrPrintWriter().println("Error: invalid value of encodedSurroundMode");
                return 1;
            }
            ((AudioManager) this.mService.mContext.getSystemService(AudioManager.class)).setEncodedSurroundMode(parseInt);
            return 0;
        } catch (NumberFormatException unused) {
            getErrPrintWriter().println("Error: wrong format specified for encoded surround mode");
            return 1;
        }
    }

    public final int getEncodedSurroundMode() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Encoded surround mode: " + ((AudioManager) this.mService.mContext.getSystemService(AudioManager.class)).getEncodedSurroundMode());
        return 0;
    }
}
