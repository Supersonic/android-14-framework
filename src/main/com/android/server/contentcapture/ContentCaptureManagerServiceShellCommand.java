package com.android.server.contentcapture;

import android.os.Bundle;
import android.os.ShellCommand;
import android.os.UserHandle;
import com.android.internal.os.IResultReceiver;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public final class ContentCaptureManagerServiceShellCommand extends ShellCommand {
    public final ContentCaptureManagerService mService;

    public ContentCaptureManagerServiceShellCommand(ContentCaptureManagerService contentCaptureManagerService) {
        this.mService = contentCaptureManagerService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        char c = 65535;
        switch (str.hashCode()) {
            case 102230:
                if (str.equals("get")) {
                    c = 0;
                    break;
                }
                break;
            case 113762:
                if (str.equals("set")) {
                    c = 1;
                    break;
                }
                break;
            case 3322014:
                if (str.equals("list")) {
                    c = 2;
                    break;
                }
                break;
            case 1557372922:
                if (str.equals("destroy")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return requestGet(outPrintWriter);
            case 1:
                return requestSet(outPrintWriter);
            case 2:
                return requestList(outPrintWriter);
            case 3:
                return requestDestroy(outPrintWriter);
            default:
                return handleDefaultCommands(str);
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            outPrintWriter.println("ContentCapture Service (content_capture) commands:");
            outPrintWriter.println("  help");
            outPrintWriter.println("    Prints this help text.");
            outPrintWriter.println("");
            outPrintWriter.println("  get bind-instant-service-allowed");
            outPrintWriter.println("    Gets whether binding to services provided by instant apps is allowed");
            outPrintWriter.println("");
            outPrintWriter.println("  set bind-instant-service-allowed [true | false]");
            outPrintWriter.println("    Sets whether binding to services provided by instant apps is allowed");
            outPrintWriter.println("");
            outPrintWriter.println("  set temporary-service USER_ID [COMPONENT_NAME DURATION]");
            outPrintWriter.println("    Temporarily (for DURATION ms) changes the service implemtation.");
            outPrintWriter.println("    To reset, call with just the USER_ID argument.");
            outPrintWriter.println("");
            outPrintWriter.println("  set default-service-enabled USER_ID [true|false]");
            outPrintWriter.println("    Enable / disable the default service for the user.");
            outPrintWriter.println("");
            outPrintWriter.println("  get default-service-enabled USER_ID");
            outPrintWriter.println("    Checks whether the default service is enabled for the user.");
            outPrintWriter.println("");
            outPrintWriter.println("  list sessions [--user USER_ID]");
            outPrintWriter.println("    Lists all pending sessions.");
            outPrintWriter.println("");
            outPrintWriter.println("  destroy sessions [--user USER_ID]");
            outPrintWriter.println("    Destroys all pending sessions.");
            outPrintWriter.println("");
            outPrintWriter.close();
        } catch (Throwable th) {
            if (outPrintWriter != null) {
                try {
                    outPrintWriter.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public final int requestGet(PrintWriter printWriter) {
        String nextArgRequired = getNextArgRequired();
        nextArgRequired.hashCode();
        if (nextArgRequired.equals("default-service-enabled")) {
            return getDefaultServiceEnabled(printWriter);
        }
        if (nextArgRequired.equals("bind-instant-service-allowed")) {
            return getBindInstantService(printWriter);
        }
        printWriter.println("Invalid set: " + nextArgRequired);
        return -1;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int requestSet(PrintWriter printWriter) {
        char c;
        String nextArgRequired = getNextArgRequired();
        nextArgRequired.hashCode();
        switch (nextArgRequired.hashCode()) {
            case 529654941:
                if (nextArgRequired.equals("default-service-enabled")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 809633044:
                if (nextArgRequired.equals("bind-instant-service-allowed")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 2003978041:
                if (nextArgRequired.equals("temporary-service")) {
                    c = 2;
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
                return setDefaultServiceEnabled(printWriter);
            case 1:
                return setBindInstantService(printWriter);
            case 2:
                return setTemporaryService(printWriter);
            default:
                printWriter.println("Invalid set: " + nextArgRequired);
                return -1;
        }
    }

    public final int getBindInstantService(PrintWriter printWriter) {
        if (this.mService.getAllowInstantService()) {
            printWriter.println("true");
            return 0;
        }
        printWriter.println("false");
        return 0;
    }

    public final int setBindInstantService(PrintWriter printWriter) {
        String nextArgRequired = getNextArgRequired();
        String lowerCase = nextArgRequired.toLowerCase();
        lowerCase.hashCode();
        if (lowerCase.equals("true")) {
            this.mService.setAllowInstantService(true);
            return 0;
        } else if (lowerCase.equals("false")) {
            this.mService.setAllowInstantService(false);
            return 0;
        } else {
            printWriter.println("Invalid mode: " + nextArgRequired);
            return -1;
        }
    }

    public final int setTemporaryService(PrintWriter printWriter) {
        int nextIntArgRequired = getNextIntArgRequired();
        String nextArg = getNextArg();
        if (nextArg == null) {
            this.mService.resetTemporaryService(nextIntArgRequired);
            return 0;
        }
        int nextIntArgRequired2 = getNextIntArgRequired();
        this.mService.setTemporaryService(nextIntArgRequired, nextArg, nextIntArgRequired2);
        printWriter.println("ContentCaptureService temporarily set to " + nextArg + " for " + nextIntArgRequired2 + "ms");
        return 0;
    }

    public final int setDefaultServiceEnabled(PrintWriter printWriter) {
        int nextIntArgRequired = getNextIntArgRequired();
        boolean parseBoolean = Boolean.parseBoolean(getNextArgRequired());
        if (this.mService.setDefaultServiceEnabled(nextIntArgRequired, parseBoolean)) {
            return 0;
        }
        printWriter.println("already " + parseBoolean);
        return 0;
    }

    public final int getDefaultServiceEnabled(PrintWriter printWriter) {
        printWriter.println(this.mService.isDefaultServiceEnabled(getNextIntArgRequired()));
        return 0;
    }

    public final int requestDestroy(PrintWriter printWriter) {
        if (isNextArgSessions(printWriter)) {
            final int userIdFromArgsOrAllUsers = getUserIdFromArgsOrAllUsers();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final IResultReceiver.Stub stub = new IResultReceiver.Stub() { // from class: com.android.server.contentcapture.ContentCaptureManagerServiceShellCommand.1
                public void send(int i, Bundle bundle) {
                    countDownLatch.countDown();
                }
            };
            return requestSessionCommon(printWriter, countDownLatch, new Runnable() { // from class: com.android.server.contentcapture.ContentCaptureManagerServiceShellCommand$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ContentCaptureManagerServiceShellCommand.this.lambda$requestDestroy$0(userIdFromArgsOrAllUsers, stub);
                }
            });
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestDestroy$0(int i, IResultReceiver iResultReceiver) {
        this.mService.destroySessions(i, iResultReceiver);
    }

    public final int requestList(final PrintWriter printWriter) {
        if (isNextArgSessions(printWriter)) {
            final int userIdFromArgsOrAllUsers = getUserIdFromArgsOrAllUsers();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final IResultReceiver.Stub stub = new IResultReceiver.Stub() { // from class: com.android.server.contentcapture.ContentCaptureManagerServiceShellCommand.2
                public void send(int i, Bundle bundle) {
                    Iterator<String> it = bundle.getStringArrayList("sessions").iterator();
                    while (it.hasNext()) {
                        printWriter.println(it.next());
                    }
                    countDownLatch.countDown();
                }
            };
            return requestSessionCommon(printWriter, countDownLatch, new Runnable() { // from class: com.android.server.contentcapture.ContentCaptureManagerServiceShellCommand$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ContentCaptureManagerServiceShellCommand.this.lambda$requestList$1(userIdFromArgsOrAllUsers, stub);
                }
            });
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestList$1(int i, IResultReceiver iResultReceiver) {
        this.mService.listSessions(i, iResultReceiver);
    }

    public final boolean isNextArgSessions(PrintWriter printWriter) {
        if (getNextArgRequired().equals("sessions")) {
            return true;
        }
        printWriter.println("Error: invalid list type");
        return false;
    }

    public final int requestSessionCommon(PrintWriter printWriter, CountDownLatch countDownLatch, Runnable runnable) {
        runnable.run();
        return waitForLatch(printWriter, countDownLatch);
    }

    public final int waitForLatch(PrintWriter printWriter, CountDownLatch countDownLatch) {
        try {
            if (countDownLatch.await(5L, TimeUnit.SECONDS)) {
                return 0;
            }
            printWriter.println("Timed out after 5 seconds");
            return -1;
        } catch (InterruptedException unused) {
            printWriter.println("System call interrupted");
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    public final int getUserIdFromArgsOrAllUsers() {
        if ("--user".equals(getNextArg())) {
            return UserHandle.parseUserArg(getNextArgRequired());
        }
        return -1;
    }

    public final int getNextIntArgRequired() {
        return Integer.parseInt(getNextArgRequired());
    }
}
