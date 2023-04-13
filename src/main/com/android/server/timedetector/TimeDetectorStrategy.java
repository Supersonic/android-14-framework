package com.android.server.timedetector;

import android.app.time.ExternalTimeSuggestion;
import android.app.time.TimeState;
import android.app.time.UnixEpochTime;
import android.app.timedetector.ManualTimeSuggestion;
import android.app.timedetector.TelephonyTimeSuggestion;
import com.android.internal.util.Preconditions;
import com.android.server.timezonedetector.Dumpable;
/* loaded from: classes2.dex */
public interface TimeDetectorStrategy extends Dumpable {
    void clearLatestNetworkSuggestion();

    boolean confirmTime(UnixEpochTime unixEpochTime);

    NetworkTimeSuggestion getLatestNetworkSuggestion();

    TimeState getTimeState();

    void setTimeState(TimeState timeState);

    void suggestExternalTime(ExternalTimeSuggestion externalTimeSuggestion);

    void suggestGnssTime(GnssTimeSuggestion gnssTimeSuggestion);

    boolean suggestManualTime(int i, ManualTimeSuggestion manualTimeSuggestion, boolean z);

    void suggestNetworkTime(NetworkTimeSuggestion networkTimeSuggestion);

    void suggestTelephonyTime(TelephonyTimeSuggestion telephonyTimeSuggestion);

    static String originToString(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i == 5) {
                            return "external";
                        }
                        throw new IllegalArgumentException("origin=" + i);
                    }
                    return "gnss";
                }
                return "network";
            }
            return "manual";
        }
        return "telephony";
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x004d, code lost:
        if (r7.equals("external") == false) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    static int stringToOrigin(String str) {
        boolean z = false;
        Preconditions.checkArgument(str != null);
        str.hashCode();
        switch (str.hashCode()) {
            case -1820761141:
                break;
            case -1081415738:
                if (str.equals("manual")) {
                    z = true;
                    break;
                }
                z = true;
                break;
            case 3177863:
                if (str.equals("gnss")) {
                    z = true;
                    break;
                }
                z = true;
                break;
            case 783201304:
                if (str.equals("telephony")) {
                    z = true;
                    break;
                }
                z = true;
                break;
            case 1843485230:
                if (str.equals("network")) {
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
                return 5;
            case true:
                return 2;
            case true:
                return 4;
            case true:
                return 1;
            case true:
                return 3;
            default:
                throw new IllegalArgumentException("originString=" + str);
        }
    }
}
