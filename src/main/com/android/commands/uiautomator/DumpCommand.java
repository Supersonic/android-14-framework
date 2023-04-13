package com.android.commands.uiautomator;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.UiAutomation;
import android.graphics.Point;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Environment;
import android.view.Display;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.commands.uiautomator.Launcher;
import com.android.uiautomator.core.AccessibilityNodeInfoDumper;
import com.android.uiautomator.core.UiAutomationShellWrapper;
import java.io.File;
import java.util.concurrent.TimeoutException;
/* loaded from: classes.dex */
public class DumpCommand extends Launcher.Command {
    private static final File DEFAULT_DUMP_FILE = new File(Environment.getLegacyExternalStorageDirectory(), "window_dump.xml");

    public DumpCommand() {
        super("dump");
    }

    @Override // com.android.commands.uiautomator.Launcher.Command
    public String shortHelp() {
        return "creates an XML dump of current UI hierarchy";
    }

    @Override // com.android.commands.uiautomator.Launcher.Command
    public String detailedOptions() {
        return "    dump [--verbose][file]\n      [--compressed]: dumps compressed layout information.\n      [file]: the location where the dumped XML should be stored, default is\n      " + DEFAULT_DUMP_FILE.getAbsolutePath() + "\n";
    }

    @Override // com.android.commands.uiautomator.Launcher.Command
    public void run(String[] args) {
        File dumpFile = DEFAULT_DUMP_FILE;
        boolean verboseMode = true;
        boolean allWindows = false;
        for (String arg : args) {
            if (arg.equals("--compressed")) {
                verboseMode = false;
            } else if (arg.equals("--windows")) {
                allWindows = true;
            } else if (!arg.startsWith("-")) {
                dumpFile = new File(arg);
            }
        }
        UiAutomationShellWrapper automationWrapper = new UiAutomationShellWrapper();
        automationWrapper.connect();
        if (verboseMode) {
            automationWrapper.setCompressedLayoutHierarchy(false);
        } else {
            automationWrapper.setCompressedLayoutHierarchy(true);
        }
        try {
            UiAutomation uiAutomation = automationWrapper.getUiAutomation();
            uiAutomation.waitForIdle(1000L, 10000L);
            if (allWindows) {
                AccessibilityServiceInfo info = uiAutomation.getServiceInfo();
                info.flags |= 64;
                uiAutomation.setServiceInfo(info);
                AccessibilityNodeInfoDumper.dumpWindowsToFile(uiAutomation.getWindowsOnAllDisplays(), dumpFile, DisplayManagerGlobal.getInstance());
            } else {
                AccessibilityNodeInfo info2 = uiAutomation.getRootInActiveWindow();
                if (info2 == null) {
                    System.err.println("ERROR: null root node returned by UiTestAutomationBridge.");
                    return;
                }
                Display display = DisplayManagerGlobal.getInstance().getRealDisplay(0);
                int rotation = display.getRotation();
                Point size = new Point();
                display.getRealSize(size);
                AccessibilityNodeInfoDumper.dumpWindowToFile(info2, dumpFile, rotation, size.x, size.y);
            }
            automationWrapper.disconnect();
            System.out.println(String.format("UI hierchary dumped to: %s", dumpFile.getAbsolutePath()));
        } catch (TimeoutException e) {
            System.err.println("ERROR: could not get idle state.");
        } finally {
            automationWrapper.disconnect();
        }
    }
}
