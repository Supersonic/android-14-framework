package com.android.commands.monkey;

import android.app.IActivityManager;
import android.view.IWindowManager;
/* loaded from: classes.dex */
public class MonkeyCommandEvent extends MonkeyEvent {
    private String mCmd;

    public MonkeyCommandEvent(String cmd) {
        super(4);
        this.mCmd = cmd;
    }

    @Override // com.android.commands.monkey.MonkeyEvent
    public int injectEvent(IWindowManager iwm, IActivityManager iam, int verbose) {
        if (this.mCmd != null) {
            try {
                Process p = Runtime.getRuntime().exec(this.mCmd);
                int status = p.waitFor();
                Logger.err.println("// Shell command " + this.mCmd + " status was " + status);
                return 1;
            } catch (Exception e) {
                Logger.err.println("// Exception from " + this.mCmd + ":");
                Logger.err.println(e.toString());
                return 1;
            }
        }
        return 1;
    }
}
