package com.android.commands.locksettings;

import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import com.android.internal.os.BaseCommand;
import com.android.internal.widget.ILockSettings;
import java.io.FileDescriptor;
import java.io.PrintStream;
/* loaded from: classes.dex */
public final class LockSettingsCmd extends BaseCommand {
    public static void main(String[] args) {
        new LockSettingsCmd().run(args);
    }

    public void onShowUsage(PrintStream out) {
        main(new String[]{"help"});
    }

    public void onRun() throws Exception {
        ILockSettings lockSettings = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
        lockSettings.asBinder().shellCommand(FileDescriptor.in, FileDescriptor.out, FileDescriptor.err, getRawArgs(), new ShellCallback(), new ResultReceiver(null) { // from class: com.android.commands.locksettings.LockSettingsCmd.1
        });
    }
}
