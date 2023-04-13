package com.android.commands.uiautomator;

import android.app.UiAutomation;
import android.view.accessibility.AccessibilityEvent;
import com.android.commands.uiautomator.Launcher;
import com.android.uiautomator.core.UiAutomationShellWrapper;
import java.text.SimpleDateFormat;
import java.util.Date;
/* loaded from: classes.dex */
public class EventsCommand extends Launcher.Command {
    private Object mQuitLock;

    public EventsCommand() {
        super("events");
        this.mQuitLock = new Object();
    }

    @Override // com.android.commands.uiautomator.Launcher.Command
    public String shortHelp() {
        return "prints out accessibility events until terminated";
    }

    @Override // com.android.commands.uiautomator.Launcher.Command
    public String detailedOptions() {
        return null;
    }

    @Override // com.android.commands.uiautomator.Launcher.Command
    public void run(String[] args) {
        UiAutomationShellWrapper automationWrapper = new UiAutomationShellWrapper();
        automationWrapper.connect();
        automationWrapper.getUiAutomation().setOnAccessibilityEventListener(new UiAutomation.OnAccessibilityEventListener() { // from class: com.android.commands.uiautomator.EventsCommand.1
            @Override // android.app.UiAutomation.OnAccessibilityEventListener
            public void onAccessibilityEvent(AccessibilityEvent event) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
                System.out.println(String.format("%s %s", formatter.format(new Date()), event.toString()));
            }
        });
        synchronized (this.mQuitLock) {
            try {
                this.mQuitLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        automationWrapper.disconnect();
    }
}
