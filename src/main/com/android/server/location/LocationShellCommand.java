package com.android.server.location;

import android.content.Context;
import android.location.Location;
import android.location.provider.ProviderProperties;
import android.os.SystemClock;
import android.os.UserHandle;
import com.android.modules.utils.BasicShellCommandHandler;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class LocationShellCommand extends BasicShellCommandHandler {
    public final Context mContext;
    public final LocationManagerService mService;

    public LocationShellCommand(Context context, LocationManagerService locationManagerService) {
        this.mContext = context;
        Objects.requireNonNull(locationManagerService);
        this.mService = locationManagerService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands((String) null);
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -1064420500:
                if (str.equals("is-location-enabled")) {
                    c = 0;
                    break;
                }
                break;
            case -547571550:
                if (str.equals("providers")) {
                    c = 1;
                    break;
                }
                break;
            case -444268534:
                if (str.equals("is-automotive-gnss-suspended")) {
                    c = 2;
                    break;
                }
                break;
            case -361391806:
                if (str.equals("set-automotive-gnss-suspended")) {
                    c = 3;
                    break;
                }
                break;
            case -84945726:
                if (str.equals("set-adas-gnss-location-enabled")) {
                    c = 4;
                    break;
                }
                break;
            case 1546249012:
                if (str.equals("set-location-enabled")) {
                    c = 5;
                    break;
                }
                break;
            case 1640843002:
                if (str.equals("is-adas-gnss-location-enabled")) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                handleIsLocationEnabled();
                return 0;
            case 1:
                return parseProvidersCommand(getNextArgRequired());
            case 2:
                handleIsAutomotiveGnssSuspended();
                return 0;
            case 3:
                handleSetAutomotiveGnssSuspended();
                return 0;
            case 4:
                handleSetAdasGnssLocationEnabled();
                return 0;
            case 5:
                handleSetLocationEnabled();
                return 0;
            case 6:
                handleIsAdasGnssLocationEnabled();
                return 0;
            default:
                return handleDefaultCommands(str);
        }
    }

    public final int parseProvidersCommand(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1669563581:
                if (str.equals("remove-test-provider")) {
                    c = 0;
                    break;
                }
                break;
            case -1650104991:
                if (str.equals("set-test-provider-location")) {
                    c = 1;
                    break;
                }
                break;
            case -61579243:
                if (str.equals("set-test-provider-enabled")) {
                    c = 2;
                    break;
                }
                break;
            case 11404448:
                if (str.equals("add-test-provider")) {
                    c = 3;
                    break;
                }
                break;
            case 2036447497:
                if (str.equals("send-extra-command")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                handleRemoveTestProvider();
                return 0;
            case 1:
                handleSetTestProviderLocation();
                return 0;
            case 2:
                handleSetTestProviderEnabled();
                return 0;
            case 3:
                handleAddTestProvider();
                return 0;
            case 4:
                handleSendExtraCommand();
                return 0;
            default:
                return handleDefaultCommands(str);
        }
    }

    public final void handleIsLocationEnabled() {
        int i = -3;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if ("--user".equals(nextOption)) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    throw new IllegalArgumentException("Unknown option: " + nextOption);
                }
            } else {
                getOutPrintWriter().println(this.mService.isLocationEnabledForUser(i));
                return;
            }
        }
    }

    public final void handleSetLocationEnabled() {
        boolean parseBoolean = Boolean.parseBoolean(getNextArgRequired());
        int i = -3;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if ("--user".equals(nextOption)) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    throw new IllegalArgumentException("Unknown option: " + nextOption);
                }
            } else {
                this.mService.setLocationEnabledForUser(parseBoolean, i);
                return;
            }
        }
    }

    public final void handleIsAdasGnssLocationEnabled() {
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            throw new IllegalStateException("command only recognized on automotive devices");
        }
        int i = -3;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if ("--user".equals(nextOption)) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    throw new IllegalArgumentException("Unknown option: " + nextOption);
                }
            } else {
                getOutPrintWriter().println(this.mService.isAdasGnssLocationEnabledForUser(i));
                return;
            }
        }
    }

    public final void handleSetAdasGnssLocationEnabled() {
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            throw new IllegalStateException("command only recognized on automotive devices");
        }
        boolean parseBoolean = Boolean.parseBoolean(getNextArgRequired());
        int i = -3;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if ("--user".equals(nextOption)) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    throw new IllegalArgumentException("Unknown option: " + nextOption);
                }
            } else {
                this.mService.setAdasGnssLocationEnabledForUser(parseBoolean, i);
                return;
            }
        }
    }

    public final void handleSetAutomotiveGnssSuspended() {
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            throw new IllegalStateException("command only recognized on automotive devices");
        }
        this.mService.setAutomotiveGnssSuspended(Boolean.parseBoolean(getNextArgRequired()));
    }

    public final void handleIsAutomotiveGnssSuspended() {
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            throw new IllegalStateException("command only recognized on automotive devices");
        }
        getOutPrintWriter().println(this.mService.isAutomotiveGnssSuspended());
    }

    public final void handleAddTestProvider() {
        String nextArgRequired = getNextArgRequired();
        List<String> emptyList = Collections.emptyList();
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        boolean z7 = false;
        int i = 1;
        int i2 = 1;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                char c = 65535;
                switch (nextOption.hashCode()) {
                    case -2115952999:
                        if (nextOption.equals("--accuracy")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1786843904:
                        if (nextOption.equals("--requiresNetwork")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1474799448:
                        if (nextOption.equals("--extraAttributionTags")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -1446936854:
                        if (nextOption.equals("--supportsBearing")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -1194644762:
                        if (nextOption.equals("--supportsAltitude")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1086076880:
                        if (nextOption.equals("--requiresCell")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 1279633236:
                        if (nextOption.equals("--hasMonetaryCost")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 1483009933:
                        if (nextOption.equals("--requiresSatellite")) {
                            c = 7;
                            break;
                        }
                        break;
                    case 1601002398:
                        if (nextOption.equals("--powerRequirement")) {
                            c = '\b';
                            break;
                        }
                        break;
                    case 2048042627:
                        if (nextOption.equals("--supportsSpeed")) {
                            c = '\t';
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        i2 = Integer.parseInt(getNextArgRequired());
                        break;
                    case 1:
                        z = true;
                        break;
                    case 2:
                        emptyList = Arrays.asList(getNextArgRequired().split(","));
                        break;
                    case 3:
                        z7 = true;
                        break;
                    case 4:
                        z5 = true;
                        break;
                    case 5:
                        z3 = true;
                        break;
                    case 6:
                        z4 = true;
                        break;
                    case 7:
                        z2 = true;
                        break;
                    case '\b':
                        i = Integer.parseInt(getNextArgRequired());
                        break;
                    case '\t':
                        z6 = true;
                        break;
                    default:
                        throw new IllegalArgumentException("Received unexpected option: " + nextOption);
                }
            } else {
                this.mService.addTestProvider(nextArgRequired, new ProviderProperties.Builder().setHasNetworkRequirement(z).setHasSatelliteRequirement(z2).setHasCellRequirement(z3).setHasMonetaryCost(z4).setHasAltitudeSupport(z5).setHasSpeedSupport(z6).setHasBearingSupport(z7).setPowerUsage(i).setAccuracy(i2).build(), emptyList, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
                return;
            }
        }
    }

    public final void handleRemoveTestProvider() {
        this.mService.removeTestProvider(getNextArgRequired(), this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
    }

    public final void handleSetTestProviderEnabled() {
        this.mService.setTestProviderEnabled(getNextArgRequired(), Boolean.parseBoolean(getNextArgRequired()), this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
    }

    public final void handleSetTestProviderLocation() {
        String nextArgRequired = getNextArgRequired();
        Location location = new Location(nextArgRequired);
        location.setAccuracy(100.0f);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption == null) {
                if (!z) {
                    throw new IllegalArgumentException("Option \"--location\" is required");
                }
                this.mService.setTestProviderLocation(nextArgRequired, location, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
                return;
            }
            char c = 65535;
            switch (nextOption.hashCode()) {
                case -2115952999:
                    if (nextOption.equals("--accuracy")) {
                        c = 0;
                        break;
                    }
                    break;
                case 1333430381:
                    if (nextOption.equals("--time")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1916798293:
                    if (nextOption.equals("--location")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    location.setAccuracy(Float.parseFloat(getNextArgRequired()));
                    break;
                case 1:
                    location.setTime(Long.parseLong(getNextArgRequired()));
                    break;
                case 2:
                    String[] split = getNextArgRequired().split(",");
                    if (split.length != 2) {
                        throw new IllegalArgumentException("Location argument must be in the form of \"<LATITUDE>,<LONGITUDE>\", not " + Arrays.toString(split));
                    }
                    location.setLatitude(Double.parseDouble(split[0]));
                    location.setLongitude(Double.parseDouble(split[1]));
                    z = true;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown option: " + nextOption);
            }
        }
    }

    public final void handleSendExtraCommand() {
        this.mService.sendExtraCommand(getNextArgRequired(), getNextArgRequired(), null);
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Location service commands:");
        outPrintWriter.println("  help or -h");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("  is-location-enabled [--user <USER_ID>]");
        outPrintWriter.println("    Gets the master location switch enabled state. If no user is specified,");
        outPrintWriter.println("    the current user is assumed.");
        outPrintWriter.println("  set-location-enabled true|false [--user <USER_ID>]");
        outPrintWriter.println("    Sets the master location switch enabled state. If no user is specified,");
        outPrintWriter.println("    the current user is assumed.");
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            outPrintWriter.println("  is-adas-gnss-location-enabled [--user <USER_ID>]");
            outPrintWriter.println("    Gets the ADAS GNSS location enabled state. If no user is specified,");
            outPrintWriter.println("    the current user is assumed.");
            outPrintWriter.println("  set-adas-gnss-location-enabled true|false [--user <USER_ID>]");
            outPrintWriter.println("    Sets the ADAS GNSS location enabled state. If no user is specified,");
            outPrintWriter.println("    the current user is assumed.");
            outPrintWriter.println("  is-automotive-gnss-suspended");
            outPrintWriter.println("    Gets the automotive GNSS suspended state.");
            outPrintWriter.println("  set-automotive-gnss-suspended true|false");
            outPrintWriter.println("    Sets the automotive GNSS suspended state.");
        }
        outPrintWriter.println("  providers");
        outPrintWriter.println("    The providers command is followed by a subcommand, as listed below:");
        outPrintWriter.println();
        outPrintWriter.println("    add-test-provider <PROVIDER> [--requiresNetwork] [--requiresSatellite]");
        outPrintWriter.println("      [--requiresCell] [--hasMonetaryCost] [--supportsAltitude]");
        outPrintWriter.println("      [--supportsSpeed] [--supportsBearing]");
        outPrintWriter.println("      [--powerRequirement <POWER_REQUIREMENT>]");
        outPrintWriter.println("      [--extraAttributionTags <TAG>,<TAG>,...]");
        outPrintWriter.println("      Add the given test provider. Requires MOCK_LOCATION permissions which");
        outPrintWriter.println("      can be enabled by running \"adb shell appops set <uid>");
        outPrintWriter.println("      android:mock_location allow\". There are optional flags that can be");
        outPrintWriter.println("      used to configure the provider properties and additional arguments. If");
        outPrintWriter.println("      no flags are included, then default values will be used.");
        outPrintWriter.println("    remove-test-provider <PROVIDER>");
        outPrintWriter.println("      Remove the given test provider.");
        outPrintWriter.println("    set-test-provider-enabled <PROVIDER> true|false");
        outPrintWriter.println("      Sets the given test provider enabled state.");
        outPrintWriter.println("    set-test-provider-location <PROVIDER> --location <LATITUDE>,<LONGITUDE>");
        outPrintWriter.println("      [--accuracy <ACCURACY>] [--time <TIME>]");
        outPrintWriter.println("      Set location for given test provider. Accuracy and time are optional.");
        outPrintWriter.println("    send-extra-command <PROVIDER> <COMMAND>");
        outPrintWriter.println("      Sends the given extra command to the given provider.");
        outPrintWriter.println();
        outPrintWriter.println("      Common commands that may be supported by the gps provider, depending on");
        outPrintWriter.println("      hardware and software configurations:");
        outPrintWriter.println("        delete_aiding_data - requests deletion of any predictive aiding data");
        outPrintWriter.println("        force_time_injection - requests NTP time injection");
        outPrintWriter.println("        force_psds_injection - requests predictive aiding data injection");
        outPrintWriter.println("        request_power_stats - requests GNSS power stats update");
    }
}
