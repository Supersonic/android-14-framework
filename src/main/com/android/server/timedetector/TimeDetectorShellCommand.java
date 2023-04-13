package com.android.server.timedetector;

import android.app.time.ExternalTimeSuggestion;
import android.app.time.TimeConfiguration;
import android.app.time.TimeState;
import android.app.time.UnixEpochTime;
import android.app.timedetector.ManualTimeSuggestion;
import android.app.timedetector.TelephonyTimeSuggestion;
import android.os.ShellCommand;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class TimeDetectorShellCommand extends ShellCommand {
    public final TimeDetectorService mInterface;

    public TimeDetectorShellCommand(TimeDetectorService timeDetectorService) {
        this.mInterface = timeDetectorService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -1630622545:
                if (str.equals("suggest_telephony_time")) {
                    c = 0;
                    break;
                }
                break;
            case -1316904020:
                if (str.equals("is_auto_detection_enabled")) {
                    c = 1;
                    break;
                }
                break;
            case -844157159:
                if (str.equals("suggest_network_time")) {
                    c = 2;
                    break;
                }
                break;
            case -532496502:
                if (str.equals("suggest_gnss_time")) {
                    c = 3;
                    break;
                }
                break;
            case -219775160:
                if (str.equals("set_time_state_for_tests")) {
                    c = 4;
                    break;
                }
                break;
            case -83861208:
                if (str.equals("get_time_state")) {
                    c = 5;
                    break;
                }
                break;
            case 200743238:
                if (str.equals("suggest_external_time")) {
                    c = 6;
                    break;
                }
                break;
            case 255251591:
                if (str.equals("get_network_time")) {
                    c = 7;
                    break;
                }
                break;
            case 754841328:
                if (str.equals("clear_network_time")) {
                    c = '\b';
                    break;
                }
                break;
            case 909817707:
                if (str.equals("suggest_manual_time")) {
                    c = '\t';
                    break;
                }
                break;
            case 1433926509:
                if (str.equals("clear_system_clock_network_time")) {
                    c = '\n';
                    break;
                }
                break;
            case 1860100418:
                if (str.equals("set_system_clock_network_time")) {
                    c = 11;
                    break;
                }
                break;
            case 1902269812:
                if (str.equals("set_auto_detection_enabled")) {
                    c = '\f';
                    break;
                }
                break;
            case 2097306860:
                if (str.equals("confirm_time")) {
                    c = '\r';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return runSuggestTelephonyTime();
            case 1:
                return runIsAutoDetectionEnabled();
            case 2:
                return runSuggestNetworkTime();
            case 3:
                return runSuggestGnssTime();
            case 4:
                return runSetTimeState();
            case 5:
                return runGetTimeState();
            case 6:
                return runSuggestExternalTime();
            case 7:
                return runGetLatestNetworkTime();
            case '\b':
                return runClearLatestNetworkTime();
            case '\t':
                return runSuggestManualTime();
            case '\n':
                return runClearSystemClockNetworkTime();
            case 11:
                return runSetSystemClockNetworkTime();
            case '\f':
                return runSetAutoDetectionEnabled();
            case '\r':
                return runConfirmTime();
            default:
                return handleDefaultCommands(str);
        }
    }

    public final int runIsAutoDetectionEnabled() {
        getOutPrintWriter().println(this.mInterface.getCapabilitiesAndConfig().getConfiguration().isAutoDetectionEnabled());
        return 0;
    }

    public final int runSetAutoDetectionEnabled() {
        return !this.mInterface.updateConfiguration(-2, new TimeConfiguration.Builder().setAutoDetectionEnabled(Boolean.parseBoolean(getNextArgRequired())).build());
    }

    public final int runSuggestManualTime() {
        Supplier supplier = new Supplier() { // from class: com.android.server.timedetector.TimeDetectorShellCommand$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                ManualTimeSuggestion lambda$runSuggestManualTime$0;
                lambda$runSuggestManualTime$0 = TimeDetectorShellCommand.this.lambda$runSuggestManualTime$0();
                return lambda$runSuggestManualTime$0;
            }
        };
        final TimeDetectorService timeDetectorService = this.mInterface;
        Objects.requireNonNull(timeDetectorService);
        return runSuggestTime(supplier, new Consumer() { // from class: com.android.server.timedetector.TimeDetectorShellCommand$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TimeDetectorService.this.suggestManualTime((ManualTimeSuggestion) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ManualTimeSuggestion lambda$runSuggestManualTime$0() {
        return ManualTimeSuggestion.parseCommandLineArg(this);
    }

    public final int runSuggestTelephonyTime() {
        Supplier supplier = new Supplier() { // from class: com.android.server.timedetector.TimeDetectorShellCommand$$ExternalSyntheticLambda6
            @Override // java.util.function.Supplier
            public final Object get() {
                TelephonyTimeSuggestion lambda$runSuggestTelephonyTime$1;
                lambda$runSuggestTelephonyTime$1 = TimeDetectorShellCommand.this.lambda$runSuggestTelephonyTime$1();
                return lambda$runSuggestTelephonyTime$1;
            }
        };
        final TimeDetectorService timeDetectorService = this.mInterface;
        Objects.requireNonNull(timeDetectorService);
        return runSuggestTime(supplier, new Consumer() { // from class: com.android.server.timedetector.TimeDetectorShellCommand$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TimeDetectorService.this.suggestTelephonyTime((TelephonyTimeSuggestion) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ TelephonyTimeSuggestion lambda$runSuggestTelephonyTime$1() {
        return TelephonyTimeSuggestion.parseCommandLineArg(this);
    }

    public final int runSuggestNetworkTime() {
        Supplier supplier = new Supplier() { // from class: com.android.server.timedetector.TimeDetectorShellCommand$$ExternalSyntheticLambda8
            @Override // java.util.function.Supplier
            public final Object get() {
                NetworkTimeSuggestion lambda$runSuggestNetworkTime$2;
                lambda$runSuggestNetworkTime$2 = TimeDetectorShellCommand.this.lambda$runSuggestNetworkTime$2();
                return lambda$runSuggestNetworkTime$2;
            }
        };
        final TimeDetectorService timeDetectorService = this.mInterface;
        Objects.requireNonNull(timeDetectorService);
        return runSuggestTime(supplier, new Consumer() { // from class: com.android.server.timedetector.TimeDetectorShellCommand$$ExternalSyntheticLambda9
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TimeDetectorService.this.suggestNetworkTime((NetworkTimeSuggestion) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ NetworkTimeSuggestion lambda$runSuggestNetworkTime$2() {
        return NetworkTimeSuggestion.parseCommandLineArg(this);
    }

    public final int runGetLatestNetworkTime() {
        getOutPrintWriter().println(this.mInterface.getLatestNetworkSuggestion());
        return 0;
    }

    public final int runClearLatestNetworkTime() {
        this.mInterface.clearLatestNetworkTime();
        return 0;
    }

    public final int runSuggestGnssTime() {
        Supplier supplier = new Supplier() { // from class: com.android.server.timedetector.TimeDetectorShellCommand$$ExternalSyntheticLambda4
            @Override // java.util.function.Supplier
            public final Object get() {
                GnssTimeSuggestion lambda$runSuggestGnssTime$3;
                lambda$runSuggestGnssTime$3 = TimeDetectorShellCommand.this.lambda$runSuggestGnssTime$3();
                return lambda$runSuggestGnssTime$3;
            }
        };
        final TimeDetectorService timeDetectorService = this.mInterface;
        Objects.requireNonNull(timeDetectorService);
        return runSuggestTime(supplier, new Consumer() { // from class: com.android.server.timedetector.TimeDetectorShellCommand$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TimeDetectorService.this.suggestGnssTime((GnssTimeSuggestion) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ GnssTimeSuggestion lambda$runSuggestGnssTime$3() {
        return GnssTimeSuggestion.parseCommandLineArg(this);
    }

    public final int runSuggestExternalTime() {
        Supplier supplier = new Supplier() { // from class: com.android.server.timedetector.TimeDetectorShellCommand$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                ExternalTimeSuggestion lambda$runSuggestExternalTime$4;
                lambda$runSuggestExternalTime$4 = TimeDetectorShellCommand.this.lambda$runSuggestExternalTime$4();
                return lambda$runSuggestExternalTime$4;
            }
        };
        final TimeDetectorService timeDetectorService = this.mInterface;
        Objects.requireNonNull(timeDetectorService);
        return runSuggestTime(supplier, new Consumer() { // from class: com.android.server.timedetector.TimeDetectorShellCommand$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TimeDetectorService.this.suggestExternalTime((ExternalTimeSuggestion) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ExternalTimeSuggestion lambda$runSuggestExternalTime$4() {
        return ExternalTimeSuggestion.parseCommandLineArg(this);
    }

    public final <T> int runSuggestTime(Supplier<T> supplier, Consumer<T> consumer) {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            T t = supplier.get();
            if (t == null) {
                outPrintWriter.println("Error: suggestion not specified");
                return 1;
            }
            consumer.accept(t);
            outPrintWriter.println("Suggestion " + t + " injected.");
            return 0;
        } catch (RuntimeException e) {
            outPrintWriter.println(e);
            return 1;
        }
    }

    public final int runGetTimeState() {
        getOutPrintWriter().println(this.mInterface.getTimeState());
        return 0;
    }

    public final int runSetTimeState() {
        this.mInterface.setTimeState(TimeState.parseCommandLineArgs(this));
        return 0;
    }

    public final int runConfirmTime() {
        getOutPrintWriter().println(this.mInterface.confirmTime(UnixEpochTime.parseCommandLineArgs(this)));
        return 0;
    }

    public final int runClearSystemClockNetworkTime() {
        this.mInterface.clearNetworkTimeForSystemClockForTests();
        return 0;
    }

    public final int runSetSystemClockNetworkTime() {
        NetworkTimeSuggestion parseCommandLineArg = NetworkTimeSuggestion.parseCommandLineArg(this);
        this.mInterface.setNetworkTimeForSystemClockForTests(parseCommandLineArg.getUnixEpochTime(), parseCommandLineArg.getUncertaintyMillis());
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.printf("Time Detector (%s) commands:\n", "time_detector");
        outPrintWriter.printf("  help\n", new Object[0]);
        outPrintWriter.printf("    Print this help text.\n", new Object[0]);
        outPrintWriter.printf("  %s\n", "is_auto_detection_enabled");
        outPrintWriter.printf("    Prints true/false according to the automatic time detection setting.\n", new Object[0]);
        outPrintWriter.printf("  %s true|false\n", "set_auto_detection_enabled");
        outPrintWriter.printf("    Sets the automatic time detection setting.\n", new Object[0]);
        outPrintWriter.println();
        outPrintWriter.printf("  %s <manual suggestion opts>\n", "suggest_manual_time");
        outPrintWriter.printf("    Suggests a time as if via the \"manual\" origin.\n", new Object[0]);
        outPrintWriter.printf("  %s <telephony suggestion opts>\n", "suggest_telephony_time");
        outPrintWriter.printf("    Suggests a time as if via the \"telephony\" origin.\n", new Object[0]);
        outPrintWriter.printf("  %s <network suggestion opts>\n", "suggest_network_time");
        outPrintWriter.printf("    Suggests a time as if via the \"network\" origin.\n", new Object[0]);
        outPrintWriter.printf("  %s <gnss suggestion opts>\n", "suggest_gnss_time");
        outPrintWriter.printf("    Suggests a time as if via the \"gnss\" origin.\n", new Object[0]);
        outPrintWriter.printf("  %s <external suggestion opts>\n", "suggest_external_time");
        outPrintWriter.printf("    Suggests a time as if via the \"external\" origin.\n", new Object[0]);
        outPrintWriter.printf("  %s\n", "get_time_state");
        outPrintWriter.printf("    Returns the current time setting state.\n", new Object[0]);
        outPrintWriter.printf("  %s <time state options>\n", "set_time_state_for_tests");
        outPrintWriter.printf("    Sets the current time state for tests.\n", new Object[0]);
        outPrintWriter.printf("  %s <unix epoch time options>\n", "confirm_time");
        outPrintWriter.printf("    Tries to confirms the time, raising the confidence.\n", new Object[0]);
        outPrintWriter.printf("  %s\n", "get_network_time");
        outPrintWriter.printf("    Prints the network time information held by the detector.\n", new Object[0]);
        outPrintWriter.printf("  %s\n", "clear_network_time");
        outPrintWriter.printf("    Clears the network time information held by the detector.\n", new Object[0]);
        outPrintWriter.printf("  %s <network suggestion opts>\n", "set_system_clock_network_time");
        outPrintWriter.printf("    Sets the network time information used for SystemClock.currentNetworkTimeClock().\n", new Object[0]);
        outPrintWriter.printf("  %s\n", "clear_system_clock_network_time");
        outPrintWriter.printf("    Clears the network time information used for SystemClock.currentNetworkTimeClock().\n", new Object[0]);
        outPrintWriter.println();
        ManualTimeSuggestion.printCommandLineOpts(outPrintWriter);
        outPrintWriter.println();
        TelephonyTimeSuggestion.printCommandLineOpts(outPrintWriter);
        outPrintWriter.println();
        NetworkTimeSuggestion.printCommandLineOpts(outPrintWriter);
        outPrintWriter.println();
        GnssTimeSuggestion.printCommandLineOpts(outPrintWriter);
        outPrintWriter.println();
        ExternalTimeSuggestion.printCommandLineOpts(outPrintWriter);
        outPrintWriter.println();
        TimeState.printCommandLineOpts(outPrintWriter);
        outPrintWriter.println();
        UnixEpochTime.printCommandLineOpts(outPrintWriter);
        outPrintWriter.println();
        outPrintWriter.printf("This service is also affected by the following device_config flags in the %s namespace:\n", "system_time");
        outPrintWriter.printf("  %s\n", "time_detector_lower_bound_millis_override");
        outPrintWriter.printf("    The lower bound used to validate time suggestions when they are received.\n", new Object[0]);
        outPrintWriter.printf("    Specified in milliseconds since the start of the Unix epoch.\n", new Object[0]);
        outPrintWriter.printf("  %s\n", "time_detector_origin_priorities_override");
        outPrintWriter.printf("    A comma separated list of origins. See TimeDetectorStrategy for details.\n", new Object[0]);
        outPrintWriter.println();
        outPrintWriter.printf("See \"adb shell cmd device_config\" for more information on setting flags.\n", new Object[0]);
        outPrintWriter.println();
    }
}
