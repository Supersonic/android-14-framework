package com.android.internal.p028os;

import android.p008os.Process;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructCapUserData;
import android.system.StructCapUserHeader;
import android.util.Slog;
import android.util.TimingsTraceLog;
import dalvik.system.VMRuntime;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import libcore.io.IoUtils;
/* renamed from: com.android.internal.os.WrapperInit */
/* loaded from: classes4.dex */
public class WrapperInit {
    private static final String TAG = "AndroidRuntime";

    private WrapperInit() {
    }

    public static void main(String[] args) {
        int fdNum = Integer.parseInt(args[0], 10);
        int targetSdkVersion = Integer.parseInt(args[1], 10);
        if (fdNum != 0) {
            FileDescriptor fd = new FileDescriptor();
            try {
                try {
                    fd.setInt$(fdNum);
                    DataOutputStream os = new DataOutputStream(new FileOutputStream(fd));
                    os.writeInt(Process.myPid());
                    os.close();
                } catch (IOException ex) {
                    Slog.m97d(TAG, "Could not write pid of wrapped process to Zygote pipe.", ex);
                }
            } finally {
                IoUtils.closeQuietly(fd);
            }
        }
        ZygoteInit.preload(new TimingsTraceLog("WrapperInitTiming", 16384L));
        String[] runtimeArgs = new String[args.length - 2];
        System.arraycopy(args, 2, runtimeArgs, 0, runtimeArgs.length);
        Runnable r = wrapperInit(targetSdkVersion, runtimeArgs);
        r.run();
    }

    public static void execApplication(String invokeWith, String niceName, int targetSdkVersion, String instructionSet, FileDescriptor pipeFd, String[] args) {
        String appProcess;
        StringBuilder command = new StringBuilder(invokeWith);
        if (VMRuntime.is64BitInstructionSet(instructionSet)) {
            appProcess = "/system/bin/app_process64";
        } else {
            appProcess = "/system/bin/app_process32";
        }
        command.append(' ');
        command.append(appProcess);
        command.append(" -Xcompiler-option --generate-mini-debug-info");
        command.append(" /system/bin --application");
        if (niceName != null) {
            command.append(" '--nice-name=").append(niceName).append("'");
        }
        command.append(" com.android.internal.os.WrapperInit ");
        command.append(pipeFd != null ? pipeFd.getInt$() : 0);
        command.append(' ');
        command.append(targetSdkVersion);
        Zygote.appendQuotedShellArgs(command, args);
        preserveCapabilities();
        Zygote.execShell(command.toString());
    }

    private static Runnable wrapperInit(int targetSdkVersion, String[] argv) {
        ClassLoader classLoader = null;
        if (argv != null && argv.length > 2 && argv[0].equals("-cp")) {
            classLoader = ZygoteInit.createPathClassLoader(argv[1], targetSdkVersion);
            Thread.currentThread().setContextClassLoader(classLoader);
            String[] removedArgs = new String[argv.length - 2];
            System.arraycopy(argv, 2, removedArgs, 0, argv.length - 2);
            argv = removedArgs;
        }
        Zygote.nativePreApplicationInit();
        return RuntimeInit.applicationInit(targetSdkVersion, null, argv, classLoader);
    }

    private static void preserveCapabilities() {
        StructCapUserHeader header = new StructCapUserHeader(OsConstants._LINUX_CAPABILITY_VERSION_3, 0);
        try {
            StructCapUserData[] data = Os.capget(header);
            if (data[0].permitted != data[0].inheritable || data[1].permitted != data[1].inheritable) {
                data[0] = new StructCapUserData(data[0].effective, data[0].permitted, data[0].permitted);
                data[1] = new StructCapUserData(data[1].effective, data[1].permitted, data[1].permitted);
                try {
                    Os.capset(header, data);
                } catch (ErrnoException e) {
                    Slog.m95e(TAG, "RuntimeInit: Failed capset", e);
                    return;
                }
            }
            for (int i = 0; i < 64; i++) {
                int dataIndex = OsConstants.CAP_TO_INDEX(i);
                int capMask = OsConstants.CAP_TO_MASK(i);
                if ((data[dataIndex].inheritable & capMask) != 0) {
                    try {
                        Os.prctl(OsConstants.PR_CAP_AMBIENT, OsConstants.PR_CAP_AMBIENT_RAISE, i, 0L, 0L);
                    } catch (ErrnoException ex) {
                        Slog.m95e(TAG, "RuntimeInit: Failed to raise ambient capability " + i, ex);
                    }
                }
            }
        } catch (ErrnoException e2) {
            Slog.m95e(TAG, "RuntimeInit: Failed capget", e2);
        }
    }
}
