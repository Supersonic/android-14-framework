package com.android.commands.p000am;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.pm.IPackageManager;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.util.AndroidException;
import com.android.internal.os.BaseCommand;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
/* renamed from: com.android.commands.am.Am */
/* loaded from: classes.dex */
public class C0000Am extends BaseCommand {
    private IActivityManager mAm;
    private IPackageManager mPm;

    C0000Am() {
        svcInit();
    }

    public static void main(String[] args) {
        new C0000Am().run(args);
    }

    private void svcInit() {
        IActivityManager service = ActivityManager.getService();
        this.mAm = service;
        if (service == null) {
            System.err.println("Error type 2");
            return;
        }
        IPackageManager asInterface = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        this.mPm = asInterface;
        if (asInterface == null) {
            System.err.println("Error type 2");
        }
    }

    public void onShowUsage(PrintStream out) {
        try {
            runAmCmd(new String[]{"help"});
        } catch (AndroidException e) {
            e.printStackTrace(System.err);
        }
    }

    public void onRun() throws Exception {
        String op = nextArgRequired();
        if (op.equals("instrument")) {
            runInstrument();
        } else {
            runAmCmd(getRawArgs());
        }
    }

    int parseUserArg(String arg) {
        if ("all".equals(arg)) {
            return -1;
        }
        if ("current".equals(arg) || "cur".equals(arg)) {
            return -2;
        }
        int userId = Integer.parseInt(arg);
        return userId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.commands.am.Am$MyShellCallback */
    /* loaded from: classes.dex */
    public static final class MyShellCallback extends ShellCallback {
        boolean mActive = true;

        MyShellCallback() {
        }

        public ParcelFileDescriptor onOpenFile(String path, String seLinuxContext, String mode) {
            if (!this.mActive) {
                System.err.println("Open attempt after active for: " + path);
                return null;
            }
            File file = new File(path);
            try {
                ParcelFileDescriptor fd = ParcelFileDescriptor.open(file, 738197504);
                if (seLinuxContext != null) {
                    String tcon = SELinux.getFileContext(file.getAbsolutePath());
                    if (!SELinux.checkSELinuxAccess(seLinuxContext, tcon, "file", "write")) {
                        try {
                            fd.close();
                        } catch (IOException e) {
                        }
                        String msg = "System server has no access to file context " + tcon;
                        System.err.println(msg + " (from path " + file.getAbsolutePath() + ", context " + seLinuxContext + ")");
                        throw new IllegalArgumentException(msg);
                    }
                }
                return fd;
            } catch (FileNotFoundException e2) {
                String msg2 = "Unable to open file " + path + ": " + e2;
                System.err.println(msg2);
                throw new IllegalArgumentException(msg2);
            }
        }
    }

    void runAmCmd(String[] args) throws AndroidException {
        MyShellCallback cb = new MyShellCallback();
        try {
            try {
                this.mAm.asBinder().shellCommand(FileDescriptor.in, FileDescriptor.out, FileDescriptor.err, args, cb, new ResultReceiver(null) { // from class: com.android.commands.am.Am.1
                });
            } catch (RemoteException e) {
                System.err.println("Error type 2");
                throw new AndroidException("Can't call activity manager; is the system running?");
            }
        } finally {
            cb.mActive = false;
        }
    }

    public void runInstrument() throws Exception {
        Instrument instrument = new Instrument(this.mAm, this.mPm);
        while (true) {
            String opt = nextOption();
            if (opt != null) {
                if (opt.equals("-p")) {
                    instrument.profileFile = nextArgRequired();
                } else if (opt.equals("-w")) {
                    instrument.wait = true;
                } else if (opt.equals("-r")) {
                    instrument.rawMode = true;
                } else if (opt.equals("-m")) {
                    instrument.protoStd = true;
                } else if (opt.equals("-f")) {
                    instrument.protoFile = true;
                    if (peekNextArg() != null && !peekNextArg().startsWith("-")) {
                        instrument.logPath = nextArg();
                    }
                } else if (opt.equals("-e")) {
                    String argKey = nextArgRequired();
                    String argValue = nextArgRequired();
                    instrument.args.putString(argKey, argValue);
                } else if (opt.equals("--no_window_animation") || opt.equals("--no-window-animation")) {
                    instrument.noWindowAnimation = true;
                } else if (opt.equals("--no-hidden-api-checks")) {
                    instrument.disableHiddenApiChecks = true;
                } else if (opt.equals("--no-test-api-access")) {
                    instrument.disableTestApiChecks = false;
                } else if (opt.equals("--no-isolated-storage")) {
                    instrument.disableIsolatedStorage = true;
                } else if (opt.equals("--user")) {
                    instrument.userId = parseUserArg(nextArgRequired());
                } else if (opt.equals("--abi")) {
                    instrument.abi = nextArgRequired();
                } else if (opt.equals("--no-restart")) {
                    instrument.noRestart = true;
                } else if (opt.equals("--always-check-signature")) {
                    instrument.alwaysCheckSignature = true;
                } else if (opt.equals("--instrument-sdk-sandbox")) {
                    instrument.instrumentSdkSandbox = true;
                } else {
                    System.err.println("Error: Unknown option: " + opt);
                    return;
                }
            } else if (instrument.userId == -1) {
                System.err.println("Error: Can't start instrumentation with user 'all'");
                return;
            } else {
                instrument.componentNameArg = nextArgRequired();
                instrument.run();
                return;
            }
        }
    }
}
