package com.android.commands.svc;

import android.app.ActivityManager;
import android.os.ParcelFileDescriptor;
import com.android.commands.svc.Svc;
import java.io.FileInputStream;
/* loaded from: classes.dex */
public class SystemServerCommand extends Svc.Command {
    public SystemServerCommand() {
        super("system-server");
    }

    @Override // com.android.commands.svc.Svc.Command
    public String shortHelp() {
        return "System server process related command";
    }

    @Override // com.android.commands.svc.Svc.Command
    public String longHelp() {
        return shortHelp() + "\n\nusage: system-server wait-for-crash\n         Wait until the system server process crashes.\n\n";
    }

    private void waitForCrash() throws Exception {
        ParcelFileDescriptor fd = ActivityManager.getService().getLifeMonitor();
        if (fd == null) {
            System.err.println("Unable to get life monitor.");
            return;
        }
        System.out.println("Waiting for the system server process to die...");
        new FileInputStream(fd.getFileDescriptor()).read();
    }

    @Override // com.android.commands.svc.Svc.Command
    public void run(String[] args) {
        char c;
        try {
            if (args.length > 1) {
                String str = args[1];
                switch (str.hashCode()) {
                    case -318476469:
                        if (str.equals("wait-for-crash")) {
                            c = 0;
                            break;
                        }
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        waitForCrash();
                        return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println(longHelp());
    }
}
