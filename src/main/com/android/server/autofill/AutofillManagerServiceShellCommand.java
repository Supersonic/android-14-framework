package com.android.server.autofill;

import android.os.Bundle;
import android.os.RemoteCallback;
import android.os.ShellCommand;
import android.os.UserHandle;
import android.service.autofill.AutofillFieldClassificationService;
import com.android.internal.os.IResultReceiver;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public final class AutofillManagerServiceShellCommand extends ShellCommand {
    public final AutofillManagerService mService;

    public AutofillManagerServiceShellCommand(AutofillManagerService autofillManagerService) {
        this.mService = autofillManagerService;
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
            case 108404047:
                if (str.equals("reset")) {
                    c = 3;
                    break;
                }
                break;
            case 1557372922:
                if (str.equals("destroy")) {
                    c = 4;
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
                return requestReset();
            case 4:
                return requestDestroy(outPrintWriter);
            default:
                return handleDefaultCommands(str);
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            outPrintWriter.println("AutoFill Service (autofill) commands:");
            outPrintWriter.println("  help");
            outPrintWriter.println("    Prints this help text.");
            outPrintWriter.println("");
            outPrintWriter.println("  get log_level ");
            outPrintWriter.println("    Gets the Autofill log level (off | debug | verbose).");
            outPrintWriter.println("");
            outPrintWriter.println("  get max_partitions");
            outPrintWriter.println("    Gets the maximum number of partitions per session.");
            outPrintWriter.println("");
            outPrintWriter.println("  get max_visible_datasets");
            outPrintWriter.println("    Gets the maximum number of visible datasets in the UI.");
            outPrintWriter.println("");
            outPrintWriter.println("  get full_screen_mode");
            outPrintWriter.println("    Gets the Fill UI full screen mode");
            outPrintWriter.println("");
            outPrintWriter.println("  get fc_score [--algorithm ALGORITHM] value1 value2");
            outPrintWriter.println("    Gets the field classification score for 2 fields.");
            outPrintWriter.println("");
            outPrintWriter.println("  get bind-instant-service-allowed");
            outPrintWriter.println("    Gets whether binding to services provided by instant apps is allowed");
            outPrintWriter.println("");
            outPrintWriter.println("  get saved-password-count");
            outPrintWriter.println("    Gets the number of saved passwords in the current service.");
            outPrintWriter.println("");
            outPrintWriter.println("  set log_level [off | debug | verbose]");
            outPrintWriter.println("    Sets the Autofill log level.");
            outPrintWriter.println("");
            outPrintWriter.println("  set max_partitions number");
            outPrintWriter.println("    Sets the maximum number of partitions per session.");
            outPrintWriter.println("");
            outPrintWriter.println("  set max_visible_datasets number");
            outPrintWriter.println("    Sets the maximum number of visible datasets in the UI.");
            outPrintWriter.println("");
            outPrintWriter.println("  set full_screen_mode [true | false | default]");
            outPrintWriter.println("    Sets the Fill UI full screen mode");
            outPrintWriter.println("");
            outPrintWriter.println("  set bind-instant-service-allowed [true | false]");
            outPrintWriter.println("    Sets whether binding to services provided by instant apps is allowed");
            outPrintWriter.println("");
            outPrintWriter.println("  set temporary-augmented-service USER_ID [COMPONENT_NAME DURATION]");
            outPrintWriter.println("    Temporarily (for DURATION ms) changes the augmented autofill service implementation.");
            outPrintWriter.println("    To reset, call with just the USER_ID argument.");
            outPrintWriter.println("");
            outPrintWriter.println("  set default-augmented-service-enabled USER_ID [true|false]");
            outPrintWriter.println("    Enable / disable the default augmented autofill service for the user.");
            outPrintWriter.println("");
            outPrintWriter.println("  set temporary-detection-service USER_ID [COMPONENT_NAME DURATION]");
            outPrintWriter.println("    Temporarily (for DURATION ms) changes the autofill detection service implementation.");
            outPrintWriter.println("    To reset, call with [COMPONENT_NAME 0].");
            outPrintWriter.println("");
            outPrintWriter.println("  get default-augmented-service-enabled USER_ID");
            outPrintWriter.println("    Checks whether the default augmented autofill service is enabled for the user.");
            outPrintWriter.println("");
            outPrintWriter.println("  list sessions [--user USER_ID]");
            outPrintWriter.println("    Lists all pending sessions.");
            outPrintWriter.println("");
            outPrintWriter.println("  destroy sessions [--user USER_ID]");
            outPrintWriter.println("    Destroys all pending sessions.");
            outPrintWriter.println("");
            outPrintWriter.println("  reset");
            outPrintWriter.println("    Resets all pending sessions and cached service connections.");
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

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int requestGet(PrintWriter printWriter) {
        char c;
        String nextArgRequired = getNextArgRequired();
        nextArgRequired.hashCode();
        switch (nextArgRequired.hashCode()) {
            case -2124387184:
                if (nextArgRequired.equals("fc_score")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -2006901047:
                if (nextArgRequired.equals("log_level")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1298810906:
                if (nextArgRequired.equals("full_screen_mode")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -255918237:
                if (nextArgRequired.equals("saved-password-count")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 809633044:
                if (nextArgRequired.equals("bind-instant-service-allowed")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 852405952:
                if (nextArgRequired.equals("default-augmented-service-enabled")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1393110435:
                if (nextArgRequired.equals("max_visible_datasets")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1772188804:
                if (nextArgRequired.equals("max_partitions")) {
                    c = 7;
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
                return getFieldClassificationScore(printWriter);
            case 1:
                return getLogLevel(printWriter);
            case 2:
                return getFullScreenMode(printWriter);
            case 3:
                return getSavedPasswordCount(printWriter);
            case 4:
                return getBindInstantService(printWriter);
            case 5:
                return getDefaultAugmentedServiceEnabled(printWriter);
            case 6:
                return getMaxVisibileDatasets(printWriter);
            case 7:
                return getMaxPartitions(printWriter);
            default:
                printWriter.println("Invalid set: " + nextArgRequired);
                return -1;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int requestSet(PrintWriter printWriter) {
        char c;
        String nextArgRequired = getNextArgRequired();
        nextArgRequired.hashCode();
        switch (nextArgRequired.hashCode()) {
            case -2006901047:
                if (nextArgRequired.equals("log_level")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1298810906:
                if (nextArgRequired.equals("full_screen_mode")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -571600804:
                if (nextArgRequired.equals("temporary-augmented-service")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 809633044:
                if (nextArgRequired.equals("bind-instant-service-allowed")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 852405952:
                if (nextArgRequired.equals("default-augmented-service-enabled")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1393110435:
                if (nextArgRequired.equals("max_visible_datasets")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1772188804:
                if (nextArgRequired.equals("max_partitions")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 2027866865:
                if (nextArgRequired.equals("temporary-detection-service")) {
                    c = 7;
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
                return setLogLevel(printWriter);
            case 1:
                return setFullScreenMode(printWriter);
            case 2:
                return setTemporaryAugmentedService(printWriter);
            case 3:
                return setBindInstantService(printWriter);
            case 4:
                return setDefaultAugmentedServiceEnabled(printWriter);
            case 5:
                return setMaxVisibileDatasets();
            case 6:
                return setMaxPartitions();
            case 7:
                return setTemporaryDetectionService(printWriter);
            default:
                printWriter.println("Invalid set: " + nextArgRequired);
                return -1;
        }
    }

    public final int getLogLevel(PrintWriter printWriter) {
        int logLevel = this.mService.getLogLevel();
        if (logLevel == 0) {
            printWriter.println("off");
            return 0;
        } else if (logLevel == 2) {
            printWriter.println("debug");
            return 0;
        } else if (logLevel == 4) {
            printWriter.println("verbose");
            return 0;
        } else {
            printWriter.println("unknow (" + logLevel + ")");
            return 0;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int setLogLevel(PrintWriter printWriter) {
        boolean z;
        String nextArgRequired = getNextArgRequired();
        String lowerCase = nextArgRequired.toLowerCase();
        lowerCase.hashCode();
        switch (lowerCase.hashCode()) {
            case 109935:
                if (lowerCase.equals("off")) {
                    z = false;
                    break;
                }
                z = true;
                break;
            case 95458899:
                if (lowerCase.equals("debug")) {
                    z = true;
                    break;
                }
                z = true;
                break;
            case 351107458:
                if (lowerCase.equals("verbose")) {
                    z = true;
                    break;
                }
                z = true;
                break;
            default:
                z = true;
                break;
        }
        switch (z) {
            case false:
                this.mService.setLogLevel(0);
                return 0;
            case true:
                this.mService.setLogLevel(2);
                return 0;
            case true:
                this.mService.setLogLevel(4);
                return 0;
            default:
                printWriter.println("Invalid level: " + nextArgRequired);
                return -1;
        }
    }

    public final int getMaxPartitions(PrintWriter printWriter) {
        printWriter.println(this.mService.getMaxPartitions());
        return 0;
    }

    public final int setMaxPartitions() {
        this.mService.setMaxPartitions(Integer.parseInt(getNextArgRequired()));
        return 0;
    }

    public final int getMaxVisibileDatasets(PrintWriter printWriter) {
        printWriter.println(this.mService.getMaxVisibleDatasets());
        return 0;
    }

    public final int setMaxVisibileDatasets() {
        this.mService.setMaxVisibleDatasets(Integer.parseInt(getNextArgRequired()));
        return 0;
    }

    public final int getFieldClassificationScore(final PrintWriter printWriter) {
        String str;
        String str2;
        String nextArgRequired = getNextArgRequired();
        if ("--algorithm".equals(nextArgRequired)) {
            str2 = getNextArgRequired();
            str = getNextArgRequired();
        } else {
            str = nextArgRequired;
            str2 = null;
        }
        String nextArgRequired2 = getNextArgRequired();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.mService.calculateScore(str2, str, nextArgRequired2, new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.autofill.AutofillManagerServiceShellCommand$$ExternalSyntheticLambda1
            public final void onResult(Bundle bundle) {
                AutofillManagerServiceShellCommand.lambda$getFieldClassificationScore$0(printWriter, countDownLatch, bundle);
            }
        }));
        return waitForLatch(printWriter, countDownLatch);
    }

    public static /* synthetic */ void lambda$getFieldClassificationScore$0(PrintWriter printWriter, CountDownLatch countDownLatch, Bundle bundle) {
        AutofillFieldClassificationService.Scores scores = (AutofillFieldClassificationService.Scores) bundle.getParcelable("scores", AutofillFieldClassificationService.Scores.class);
        if (scores == null) {
            printWriter.println("no score");
        } else {
            printWriter.println(scores.scores[0][0]);
        }
        countDownLatch.countDown();
    }

    public final int getFullScreenMode(PrintWriter printWriter) {
        Boolean fullScreenMode = this.mService.getFullScreenMode();
        if (fullScreenMode == null) {
            printWriter.println("default");
            return 0;
        } else if (fullScreenMode.booleanValue()) {
            printWriter.println("true");
            return 0;
        } else {
            printWriter.println("false");
            return 0;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int setFullScreenMode(PrintWriter printWriter) {
        char c;
        String nextArgRequired = getNextArgRequired();
        String lowerCase = nextArgRequired.toLowerCase();
        lowerCase.hashCode();
        switch (lowerCase.hashCode()) {
            case 3569038:
                if (lowerCase.equals("true")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 97196323:
                if (lowerCase.equals("false")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1544803905:
                if (lowerCase.equals("default")) {
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
                this.mService.setFullScreenMode(Boolean.TRUE);
                return 0;
            case 1:
                this.mService.setFullScreenMode(Boolean.FALSE);
                return 0;
            case 2:
                this.mService.setFullScreenMode(null);
                return 0;
            default:
                printWriter.println("Invalid mode: " + nextArgRequired);
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

    public final int setTemporaryDetectionService(PrintWriter printWriter) {
        int nextIntArgRequired = getNextIntArgRequired();
        String nextArg = getNextArg();
        int nextIntArgRequired2 = getNextIntArgRequired();
        if (nextArg == null) {
            this.mService.resetTemporaryDetectionService(nextIntArgRequired);
            return 0;
        } else if (nextIntArgRequired2 <= 0) {
            this.mService.resetTemporaryDetectionService(nextIntArgRequired);
            return 0;
        } else {
            this.mService.setTemporaryDetectionService(nextIntArgRequired, nextArg, nextIntArgRequired2);
            printWriter.println("Autofill Detection Service temporarily set to " + nextArg + " for " + nextIntArgRequired2 + "ms");
            return 0;
        }
    }

    public final int setTemporaryAugmentedService(PrintWriter printWriter) {
        int nextIntArgRequired = getNextIntArgRequired();
        String nextArg = getNextArg();
        if (nextArg == null) {
            this.mService.resetTemporaryAugmentedAutofillService(nextIntArgRequired);
            return 0;
        }
        int nextIntArgRequired2 = getNextIntArgRequired();
        this.mService.setTemporaryAugmentedAutofillService(nextIntArgRequired, nextArg, nextIntArgRequired2);
        printWriter.println("AugmentedAutofillService temporarily set to " + nextArg + " for " + nextIntArgRequired2 + "ms");
        return 0;
    }

    public final int getDefaultAugmentedServiceEnabled(PrintWriter printWriter) {
        printWriter.println(this.mService.isDefaultAugmentedServiceEnabled(getNextIntArgRequired()));
        return 0;
    }

    public final int setDefaultAugmentedServiceEnabled(PrintWriter printWriter) {
        int nextIntArgRequired = getNextIntArgRequired();
        boolean parseBoolean = Boolean.parseBoolean(getNextArgRequired());
        if (this.mService.setDefaultAugmentedServiceEnabled(nextIntArgRequired, parseBoolean)) {
            return 0;
        }
        printWriter.println("already " + parseBoolean);
        return 0;
    }

    public final int getSavedPasswordCount(final PrintWriter printWriter) {
        int nextIntArgRequired = getNextIntArgRequired();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        if (this.mService.requestSavedPasswordCount(nextIntArgRequired, new IResultReceiver.Stub() { // from class: com.android.server.autofill.AutofillManagerServiceShellCommand.1
            public void send(int i, Bundle bundle) {
                PrintWriter printWriter2 = printWriter;
                printWriter2.println("resultCode=" + i);
                if (i == 0 && bundle != null) {
                    PrintWriter printWriter3 = printWriter;
                    printWriter3.println("value=" + bundle.getInt("result"));
                }
                countDownLatch.countDown();
            }
        })) {
            waitForLatch(printWriter, countDownLatch);
            return 0;
        }
        return 0;
    }

    public final int requestDestroy(PrintWriter printWriter) {
        if (isNextArgSessions(printWriter)) {
            final int userIdFromArgsOrAllUsers = getUserIdFromArgsOrAllUsers();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final IResultReceiver.Stub stub = new IResultReceiver.Stub() { // from class: com.android.server.autofill.AutofillManagerServiceShellCommand.2
                public void send(int i, Bundle bundle) {
                    countDownLatch.countDown();
                }
            };
            return requestSessionCommon(printWriter, countDownLatch, new Runnable() { // from class: com.android.server.autofill.AutofillManagerServiceShellCommand$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    AutofillManagerServiceShellCommand.this.lambda$requestDestroy$1(userIdFromArgsOrAllUsers, stub);
                }
            });
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestDestroy$1(int i, IResultReceiver iResultReceiver) {
        this.mService.removeAllSessions(i, iResultReceiver);
    }

    public final int requestList(final PrintWriter printWriter) {
        if (isNextArgSessions(printWriter)) {
            final int userIdFromArgsOrAllUsers = getUserIdFromArgsOrAllUsers();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final IResultReceiver.Stub stub = new IResultReceiver.Stub() { // from class: com.android.server.autofill.AutofillManagerServiceShellCommand.3
                public void send(int i, Bundle bundle) {
                    Iterator<String> it = bundle.getStringArrayList("sessions").iterator();
                    while (it.hasNext()) {
                        printWriter.println(it.next());
                    }
                    countDownLatch.countDown();
                }
            };
            return requestSessionCommon(printWriter, countDownLatch, new Runnable() { // from class: com.android.server.autofill.AutofillManagerServiceShellCommand$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AutofillManagerServiceShellCommand.this.lambda$requestList$2(userIdFromArgsOrAllUsers, stub);
                }
            });
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestList$2(int i, IResultReceiver iResultReceiver) {
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

    public final int requestReset() {
        this.mService.reset();
        return 0;
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
