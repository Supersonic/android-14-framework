package com.android.server.wallpaper;

import android.os.RemoteException;
import android.os.ShellCommand;
import android.util.Log;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class WallpaperManagerShellCommand extends ShellCommand {
    public final WallpaperManagerService mService;

    public WallpaperManagerShellCommand(WallpaperManagerService wallpaperManagerService) {
        this.mService = wallpaperManagerService;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0039, code lost:
        if (r5.equals("dim-with-uid") == false) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int onCommand(String str) {
        char c = 1;
        if (str == null) {
            onHelp();
            return 1;
        }
        switch (str.hashCode()) {
            case -1462105208:
                if (str.equals("set-dim-amount")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -296046994:
                break;
            case 1499:
                if (str.equals("-h")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3198785:
                if (str.equals("help")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 309630996:
                if (str.equals("get-dim-amount")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return setWallpaperDimAmount();
            case 1:
                return setDimmingWithUid();
            case 2:
            case 3:
                onHelp();
                return 0;
            case 4:
                return getWallpaperDimAmount();
            default:
                return handleDefaultCommands(str);
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Wallpaper manager commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println();
        outPrintWriter.println("  set-dim-amount DIMMING");
        outPrintWriter.println("    Sets the current dimming value to DIMMING (a number between 0 and 1).");
        outPrintWriter.println();
        outPrintWriter.println("  dim-with-uid UID DIMMING");
        outPrintWriter.println("    Sets the wallpaper dim amount to DIMMING as if an app with uid, UID, called it.");
        outPrintWriter.println();
        outPrintWriter.println("  get-dim-amount");
        outPrintWriter.println("    Get the current wallpaper dim amount.");
    }

    public final int setWallpaperDimAmount() {
        float parseFloat = Float.parseFloat(getNextArgRequired());
        try {
            this.mService.setWallpaperDimAmount(parseFloat);
        } catch (RemoteException unused) {
            Log.e("WallpaperManagerShellCommand", "Can't set wallpaper dim amount");
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Dimming the wallpaper to: " + parseFloat);
        return 0;
    }

    public final int getWallpaperDimAmount() {
        float wallpaperDimAmount = this.mService.getWallpaperDimAmount();
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("The current wallpaper dim amount is: " + wallpaperDimAmount);
        return 0;
    }

    public final int setDimmingWithUid() {
        int parseInt = Integer.parseInt(getNextArgRequired());
        float parseFloat = Float.parseFloat(getNextArgRequired());
        this.mService.setWallpaperDimAmountForUid(parseInt, parseFloat);
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Dimming the wallpaper for UID: " + parseInt + " to: " + parseFloat);
        return 0;
    }
}
