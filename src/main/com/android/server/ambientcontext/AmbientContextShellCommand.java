package com.android.server.ambientcontext;

import android.app.ambientcontext.AmbientContextEvent;
import android.app.ambientcontext.AmbientContextEventRequest;
import android.app.ambientcontext.IAmbientContextObserver;
import android.content.ComponentName;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.util.Slog;
import com.android.server.ambientcontext.AmbientContextManagerPerUserService;
import com.android.server.ambientcontext.AmbientContextShellCommand;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes.dex */
public final class AmbientContextShellCommand extends ShellCommand {
    public final AmbientContextManagerService mService;
    public static final String TAG = AmbientContextShellCommand.class.getSimpleName();
    public static final AmbientContextEventRequest REQUEST = new AmbientContextEventRequest.Builder().addEventType(1).addEventType(2).addEventType(3).build();
    public static final AmbientContextEventRequest WEARABLE_REQUEST = new AmbientContextEventRequest.Builder().addEventType(100001).build();
    public static final AmbientContextEventRequest MIXED_REQUEST = new AmbientContextEventRequest.Builder().addEventType(1).addEventType(100001).build();
    public static final TestableCallbackInternal sTestableCallbackInternal = new TestableCallbackInternal();

    public AmbientContextShellCommand(AmbientContextManagerService ambientContextManagerService) {
        this.mService = ambientContextManagerService;
    }

    /* loaded from: classes.dex */
    public static class TestableCallbackInternal {
        public List<AmbientContextEvent> mLastEvents;
        public int mLastStatus;

        public int getLastStatus() {
            return this.mLastStatus;
        }

        public final IAmbientContextObserver createAmbientContextObserver() {
            return new IAmbientContextObserver.Stub() { // from class: com.android.server.ambientcontext.AmbientContextShellCommand.TestableCallbackInternal.1
                public void onEvents(List<AmbientContextEvent> list) throws RemoteException {
                    TestableCallbackInternal.this.mLastEvents = list;
                    PrintStream printStream = System.out;
                    printStream.println("Detection events available: " + list);
                }

                public void onRegistrationComplete(int i) throws RemoteException {
                    TestableCallbackInternal.this.mLastStatus = i;
                }
            };
        }

        public final RemoteCallback createRemoteStatusCallback() {
            return new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.ambientcontext.AmbientContextShellCommand$TestableCallbackInternal$$ExternalSyntheticLambda0
                public final void onResult(Bundle bundle) {
                    AmbientContextShellCommand.TestableCallbackInternal.this.lambda$createRemoteStatusCallback$0(bundle);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$createRemoteStatusCallback$0(Bundle bundle) {
            int i = bundle.getInt("android.app.ambientcontext.AmbientContextStatusBundleKey");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mLastStatus = i;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -2084150080:
                if (str.equals("get-bound-package")) {
                    c = 0;
                    break;
                }
                break;
            case -2048517510:
                if (str.equals("stop-detection")) {
                    c = 1;
                    break;
                }
                break;
            case -1827236351:
                if (str.equals("query-mixed-service-status")) {
                    c = 2;
                    break;
                }
                break;
            case -920381716:
                if (str.equals("start-detection-wearable")) {
                    c = 3;
                    break;
                }
                break;
            case -919702712:
                if (str.equals("start-detection-mixed")) {
                    c = 4;
                    break;
                }
                break;
            case -108354651:
                if (str.equals("set-temporary-services")) {
                    c = 5;
                    break;
                }
                break;
            case 943496953:
                if (str.equals("query-wearable-service-status")) {
                    c = 6;
                    break;
                }
                break;
            case 1104883342:
                if (str.equals("set-temporary-service")) {
                    c = 7;
                    break;
                }
                break;
            case 1519475119:
                if (str.equals("query-service-status")) {
                    c = '\b';
                    break;
                }
                break;
            case 2018305992:
                if (str.equals("get-last-status-code")) {
                    c = '\t';
                    break;
                }
                break;
            case 2084757210:
                if (str.equals("start-detection")) {
                    c = '\n';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return getBoundPackageName();
            case 1:
                return runStopDetection();
            case 2:
                return runQueryMixedServiceStatus();
            case 3:
                return runWearableStartDetection();
            case 4:
                return runMixedStartDetection();
            case 5:
                return setTemporaryServices();
            case 6:
                return runQueryWearableServiceStatus();
            case 7:
                return setTemporaryService();
            case '\b':
                return runQueryServiceStatus();
            case '\t':
                return getLastStatusCode();
            case '\n':
                return runStartDetection();
            default:
                return handleDefaultCommands(str);
        }
    }

    public final int runStartDetection() {
        int parseInt = Integer.parseInt(getNextArgRequired());
        String nextArgRequired = getNextArgRequired();
        AmbientContextManagerService ambientContextManagerService = this.mService;
        AmbientContextEventRequest ambientContextEventRequest = REQUEST;
        TestableCallbackInternal testableCallbackInternal = sTestableCallbackInternal;
        ambientContextManagerService.startDetection(parseInt, ambientContextEventRequest, nextArgRequired, testableCallbackInternal.createAmbientContextObserver());
        this.mService.newClientAdded(parseInt, ambientContextEventRequest, nextArgRequired, testableCallbackInternal.createAmbientContextObserver());
        return 0;
    }

    public final int runWearableStartDetection() {
        int parseInt = Integer.parseInt(getNextArgRequired());
        String nextArgRequired = getNextArgRequired();
        AmbientContextManagerService ambientContextManagerService = this.mService;
        AmbientContextEventRequest ambientContextEventRequest = WEARABLE_REQUEST;
        TestableCallbackInternal testableCallbackInternal = sTestableCallbackInternal;
        ambientContextManagerService.startDetection(parseInt, ambientContextEventRequest, nextArgRequired, testableCallbackInternal.createAmbientContextObserver());
        this.mService.newClientAdded(parseInt, ambientContextEventRequest, nextArgRequired, testableCallbackInternal.createAmbientContextObserver());
        return 0;
    }

    public final int runMixedStartDetection() {
        int parseInt = Integer.parseInt(getNextArgRequired());
        String nextArgRequired = getNextArgRequired();
        AmbientContextManagerService ambientContextManagerService = this.mService;
        AmbientContextEventRequest ambientContextEventRequest = MIXED_REQUEST;
        TestableCallbackInternal testableCallbackInternal = sTestableCallbackInternal;
        ambientContextManagerService.startDetection(parseInt, ambientContextEventRequest, nextArgRequired, testableCallbackInternal.createAmbientContextObserver());
        this.mService.newClientAdded(parseInt, ambientContextEventRequest, nextArgRequired, testableCallbackInternal.createAmbientContextObserver());
        return 0;
    }

    public final int runStopDetection() {
        this.mService.stopAmbientContextEvent(Integer.parseInt(getNextArgRequired()), getNextArgRequired());
        return 0;
    }

    public final int runQueryServiceStatus() {
        this.mService.queryServiceStatus(Integer.parseInt(getNextArgRequired()), getNextArgRequired(), new int[]{1, 2}, sTestableCallbackInternal.createRemoteStatusCallback());
        return 0;
    }

    public final int runQueryWearableServiceStatus() {
        this.mService.queryServiceStatus(Integer.parseInt(getNextArgRequired()), getNextArgRequired(), new int[]{100001}, sTestableCallbackInternal.createRemoteStatusCallback());
        return 0;
    }

    public final int runQueryMixedServiceStatus() {
        this.mService.queryServiceStatus(Integer.parseInt(getNextArgRequired()), getNextArgRequired(), new int[]{1, 100001}, sTestableCallbackInternal.createRemoteStatusCallback());
        return 0;
    }

    public final int getLastStatusCode() {
        getOutPrintWriter().println(sTestableCallbackInternal.getLastStatus());
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("AmbientContextEvent commands: ");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println();
        outPrintWriter.println("  start-detection USER_ID PACKAGE_NAME: Starts AmbientContextEvent detection.");
        outPrintWriter.println("  start-detection-wearable USER_ID PACKAGE_NAME: Starts AmbientContextEvent detection for wearable.");
        outPrintWriter.println("  start-detection-mixed USER_ID PACKAGE_NAME:  Starts AmbientContextEvent detection for mixed events.");
        outPrintWriter.println("  stop-detection USER_ID PACKAGE_NAME: Stops AmbientContextEvent detection.");
        outPrintWriter.println("  get-last-status-code: Prints the latest request status code.");
        outPrintWriter.println("  query-service-status USER_ID PACKAGE_NAME: Prints the service status code.");
        outPrintWriter.println("  query-wearable-service-status USER_ID PACKAGE_NAME: Prints the service status code for wearable.");
        outPrintWriter.println("  query-mixed-service-status USER_ID PACKAGE_NAME: Prints the service status code for mixed events.");
        outPrintWriter.println("  get-bound-package USER_ID:     Print the bound package that implements the service.");
        outPrintWriter.println("  set-temporary-service USER_ID [PACKAGE_NAME] [COMPONENT_NAME DURATION]");
        outPrintWriter.println("    Temporarily (for DURATION ms) changes the service implementation.");
        outPrintWriter.println("    To reset, call with just the USER_ID argument.");
        outPrintWriter.println("  set-temporary-services USER_ID [FIRST_PACKAGE_NAME] [SECOND_PACKAGE_NAME] [COMPONENT_NAME DURATION]");
        outPrintWriter.println("    Temporarily (for DURATION ms) changes the service implementation.");
        outPrintWriter.println("    To reset, call with just the USER_ID argument.");
    }

    public final int getBoundPackageName() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        ComponentName componentName = this.mService.getComponentName(Integer.parseInt(getNextArgRequired()), AmbientContextManagerPerUserService.ServiceType.DEFAULT);
        outPrintWriter.println(componentName == null ? "" : componentName.getPackageName());
        return 0;
    }

    public final int setTemporaryService() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        int parseInt = Integer.parseInt(getNextArgRequired());
        String nextArg = getNextArg();
        if (nextArg == null) {
            this.mService.resetTemporaryService(parseInt);
            outPrintWriter.println("AmbientContextDetectionService temporary reset. ");
            this.mService.setDefaultServiceEnabled(parseInt, true);
            return 0;
        }
        int parseInt2 = Integer.parseInt(getNextArgRequired());
        this.mService.setTemporaryService(parseInt, nextArg, parseInt2);
        outPrintWriter.println("AmbientContextDetectionService temporarily set to " + nextArg + " for " + parseInt2 + "ms");
        return 0;
    }

    public final int setTemporaryServices() {
        String[] strArr = new String[2];
        PrintWriter outPrintWriter = getOutPrintWriter();
        int parseInt = Integer.parseInt(getNextArgRequired());
        this.mService.setDefaultServiceEnabled(parseInt, false);
        String nextArg = getNextArg();
        String nextArg2 = getNextArg();
        if (nextArg == null || nextArg2 == null) {
            this.mService.resetTemporaryService(parseInt);
            this.mService.setDefaultServiceEnabled(parseInt, true);
            outPrintWriter.println("AmbientContextDetectionService temporary reset.");
            return 0;
        }
        strArr[0] = nextArg;
        strArr[1] = nextArg2;
        int parseInt2 = Integer.parseInt(getNextArgRequired());
        this.mService.setTemporaryServices(parseInt, strArr, parseInt2);
        String str = TAG;
        Slog.w(str, "AmbientContextDetectionService temporarily set to " + strArr[0] + " and " + strArr[1] + " for " + parseInt2 + "ms");
        outPrintWriter.println("AmbientContextDetectionService temporarily set to " + strArr[0] + " and " + strArr[1] + " for " + parseInt2 + "ms");
        return 0;
    }
}
