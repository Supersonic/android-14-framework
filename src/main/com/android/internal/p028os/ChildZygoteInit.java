package com.android.internal.p028os;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
/* renamed from: com.android.internal.os.ChildZygoteInit */
/* loaded from: classes4.dex */
public class ChildZygoteInit {
    private static final String TAG = "ChildZygoteInit";

    static String parseSocketNameFromArgs(String[] argv) {
        for (String arg : argv) {
            if (arg.startsWith(Zygote.CHILD_ZYGOTE_SOCKET_NAME_ARG)) {
                return arg.substring(Zygote.CHILD_ZYGOTE_SOCKET_NAME_ARG.length());
            }
        }
        return null;
    }

    static String parseAbiListFromArgs(String[] argv) {
        for (String arg : argv) {
            if (arg.startsWith(Zygote.CHILD_ZYGOTE_ABI_LIST_ARG)) {
                return arg.substring(Zygote.CHILD_ZYGOTE_ABI_LIST_ARG.length());
            }
        }
        return null;
    }

    static int parseIntFromArg(String[] argv, String desiredArg) {
        int value = -1;
        for (String arg : argv) {
            if (arg.startsWith(desiredArg)) {
                String valueStr = arg.substring(arg.indexOf(61) + 1);
                try {
                    value = Integer.parseInt(valueStr);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid int argument: " + valueStr, e);
                }
            }
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void runZygoteServer(ZygoteServer server, String[] args) {
        String socketName = parseSocketNameFromArgs(args);
        if (socketName == null) {
            throw new NullPointerException("No socketName specified");
        }
        String abiList = parseAbiListFromArgs(args);
        if (abiList == null) {
            throw new NullPointerException("No abiList specified");
        }
        try {
            Os.prctl(OsConstants.PR_SET_NO_NEW_PRIVS, 1L, 0L, 0L, 0L);
            int uidGidMin = parseIntFromArg(args, Zygote.CHILD_ZYGOTE_UID_RANGE_START);
            int uidGidMax = parseIntFromArg(args, Zygote.CHILD_ZYGOTE_UID_RANGE_END);
            if (uidGidMin == -1 || uidGidMax == -1) {
                throw new RuntimeException("Couldn't parse UID range start/end");
            }
            if (uidGidMin > uidGidMax) {
                throw new RuntimeException("Passed in UID range is invalid, min > max.");
            }
            if (uidGidMin < 90000) {
                throw new RuntimeException("Passed in UID range does not map to isolated processes.");
            }
            Zygote.nativeInstallSeccompUidGidFilter(uidGidMin, uidGidMax);
            try {
                try {
                    server.registerServerSocketAtAbstractName(socketName);
                    Zygote.nativeAllowFileAcrossFork("ABSTRACT/" + socketName);
                    Runnable caller = server.runSelectLoop(abiList);
                    if (caller != null) {
                        caller.run();
                    }
                } catch (RuntimeException e) {
                    Log.m109e(TAG, "Fatal exception:", e);
                    throw e;
                }
            } finally {
                server.closeServerSocket();
            }
        } catch (ErrnoException ex) {
            throw new RuntimeException("Failed to set PR_SET_NO_NEW_PRIVS", ex);
        }
    }
}
